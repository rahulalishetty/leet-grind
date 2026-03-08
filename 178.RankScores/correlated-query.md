# 1) What is a correlated subquery?

A **correlated subquery** is a subquery that **refers to columns from the outer query**.

That means:

- The subquery cannot be evaluated independently first (in the general case),
- because it depends on values from the current row of the outer query.

### Example (correlated)

```sql
SELECT *
FROM t1
WHERE column1 = ANY (
  SELECT column1
  FROM t2
  WHERE t2.column2 = t1.column2
);
```

Here, the inner query references `t1.column2`, even though `t1` is not in the subquery `FROM` clause.
MySQL resolves `t1` by looking **outside** the subquery, into the outer query.

### Why correlation changes meaning

Suppose:

- `t1` has a row `(column1=5, column2=6)`
- `t2` has a row `(column1=5, column2=7)`

Then:

- `column1 = ANY (SELECT column1 FROM t2)` might be TRUE (because 5 exists in t2),
- but with correlation (`WHERE t2.column2 = t1.column2`), the inner query sees only rows of `t2` where `t2.column2 = 6`.
  That set might be empty, so the whole predicate becomes FALSE.

Correlation makes the inner query depend on the outer row.

---

## 2) Scoping / alias resolution rule (“inside → outside”)

MySQL resolves names from **inside the subquery outward**.

Example:

```sql
SELECT column1
FROM t1 AS x
WHERE x.column1 = (
  SELECT column1
  FROM t2 AS x
  WHERE x.column1 = (
    SELECT column1
    FROM t3
    WHERE x.column2 = t3.column1
  )
);
```

Important point:

- The alias `x` is reused inside the subquery: `FROM t2 AS x`
- Therefore, inside that subquery block, `x` refers to **t2**, not **t1**.
- So `x.column2` must be a column of **t2** (not t1), because the nearest scope wins.

**Rule of thumb**

- The closest alias binding in nested scopes takes precedence.
- If a name is not found in the current scope, MySQL looks outward until it finds it.

---

## 3) Correlated scalar subquery → derived table transformation (MySQL 8.0.24+)

### What is a “correlated scalar subquery”?

- **Scalar**: returns at most **one row and one column**
- **Correlated**: refers to outer columns

Example:

```sql
SELECT *
FROM t1
WHERE (SELECT a FROM t2 WHERE t2.a = t1.a) > 0;
```

This subquery returns a single value (or NULL), correlated via `t2.a = t1.a`.

### Why transform it?

Naively, the database might evaluate the subquery once per outer row (costly).
MySQL 8.0.24+ can rewrite it to a **derived table + join**, which may be executed more efficiently.

### Switch controlling it

The optimizer can perform this rewrite when:

- `subquery_to_derived` flag in `optimizer_switch` is enabled.

---

## 4) How the optimizer rewrite works (high level)

Original:

```sql
SELECT *
FROM t1
WHERE (SELECT a FROM t2 WHERE t2.a = t1.a) > 0;
```

Rewritten idea:

1. Build a derived table from the inner table (`t2`) grouped by the join column (`t2.a`)
2. Join `t1` to this derived table
3. Apply a **cardinality check** that enforces “scalar subquery must return at most 1 row”
4. Evaluate the lifted predicate (`> 0`) only after cardinality is safe

Illustrative rewrite from docs (conceptual):

```sql
SELECT t1.*
FROM t1
LEFT OUTER JOIN (
  SELECT a, COUNT(*) AS ct
  FROM t2
  GROUP BY a
) AS derived
  ON t1.a = derived.a
  AND REJECT_IF(
        (ct > 1),
        "ERROR 1242 (21000): Subquery returns more than 1 row"
      )
WHERE derived.a > 0;
```

### What is `REJECT_IF()`?

- It is an **internal** function (not something you write in application SQL).
- It models MySQL’s internal cardinality enforcement:
  - If the subquery would produce more than one row for a given outer row,
    MySQL must raise:
    **“Subquery returns more than 1 row”** (error 1242).
- The optimizer adds logic equivalent to that check when rewriting to a join.

### Why the derived table uses `COUNT(*)` and grouping

- Grouping by `t2.a` collapses all t2 rows with the same `a` into one group.
- `COUNT(*) AS ct` lets MySQL detect if a group would yield multiple rows (violating scalar subquery rules).

If the original subquery already has explicit grouping, MySQL may add additional grouping as needed.

---

## 5) Conditions required for the transformation

The rewrite is only possible if a set of strict constraints are satisfied.

### 5.1 Where the subquery can appear

Allowed:

- In the **SELECT list**
- In a **WHERE** condition
- In a **HAVING** condition

Not allowed:

- In a **JOIN condition**
- With **LIMIT** or **OFFSET**
- With **set operations** like `UNION`

### 5.2 WHERE clause structure inside the subquery

- Must be predicates combined with **AND** only
- If there is an **OR**, transformation is rejected
- At least one predicate must be transformable
- None of the predicates may forbid transformation

### 5.3 Predicate eligibility (very strict)

A transformable predicate must be:

- An **equality** predicate (`=`)
- Both operands must be **simple column references**
- Other operators are not eligible:
  - no `<`, `>`, `>=`, `!=`, etc.
- The null-safe operator `<=>` is **not supported** in this context

### 5.4 Inner-only vs outer-only references

- Predicate with only inner references is **not eligible** (it can be evaluated before grouping)
- Predicate with only outer references is eligible (it can be lifted), but requires a cardinality check approach
- A “good” correlated predicate for transformation typically has:
  - one operand referencing only inner columns
  - one operand referencing only outer columns

If this “one inner / one outer” requirement is not met, transformation is rejected.

### 5.5 Where correlated columns are allowed inside the subquery

Correlated columns may appear only in:

- The subquery’s **WHERE clause**

Correlated columns may **not** appear in:

- The subquery **SELECT list**
- Any **JOIN** or **ORDER BY** clause inside the subquery
- The subquery **GROUP BY** list
- The subquery **HAVING**
- Any derived table in the subquery’s FROM list

### 5.6 Correlated columns and aggregates

- A correlated column cannot be used as an argument inside an aggregate function.
- Also, the subquery must not contain aggregates that conceptually aggregate “outside” the subquery block.

Special note about `COUNT()`:

- If `COUNT()` appears in the subquery SELECT list, it must be:
  - at the topmost level
  - not part of a larger expression

### 5.7 Correlation resolution constraints

- Correlated column must be resolved in the query block directly containing the subquery being transformed.
- Correlated columns cannot be present in:
  - nested scalar subqueries inside the subquery’s WHERE clause

### 5.8 Window functions

- The subquery cannot contain window functions.

---

## 6) Practical takeaways (engineering perspective)

1. **Correlated subqueries** are powerful but can be expensive if executed per outer row.
2. MySQL 8.0.24+ can sometimes rewrite scalar correlated subqueries into joins for better performance.
3. That rewrite applies only under strict structural rules (mainly simple equality correlations and AND-only predicates).
4. If you want SQL that’s easier for the optimizer to transform:
   - Prefer simple `t2.col = t1.col` correlation predicates
   - Avoid OR, LIMIT/OFFSET, UNION, window functions inside the scalar subquery
   - Keep correlated references confined to the subquery WHERE clause

---
