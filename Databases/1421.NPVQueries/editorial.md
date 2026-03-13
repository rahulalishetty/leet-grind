# 1421. NPV Queries — Approach

## Approach 1: JOIN with a Fallback to DEFAULT

### Intuition

The goal is to match rows from the **Queries** table with the **NPV** table and retrieve the corresponding **NPV value**.

To accomplish this, we use a **LEFT JOIN**.

A **LEFT JOIN** ensures:

- All rows from the **left table (Queries)** appear in the result.
- Matching rows from the **right table (NPV)** are included when they exist.
- If no matching row exists in `NPV`, the result becomes **NULL**.

---

# Step 1 — Join Queries with NPV

We match the tables using both:

```
id
year
```

SQL query:

```sql
SELECT
    Q.id,
    Q.year,
    N.npv
FROM Queries Q
LEFT JOIN NPV N
ON Q.id = N.id
AND Q.year = N.year;
```

Behavior:

- If `(id, year)` exists in `NPV`, the value is returned.
- If not, `npv` becomes **NULL**.

---

# Step 2 — Replace NULL Values

When the `(id, year)` pair does not exist in `NPV`, we must return **0 instead of NULL**.

Two SQL functions can handle this:

## COALESCE()

```
COALESCE(value, default)
```

Returns the **first non-null value**.

Example:

```
COALESCE(N.npv, 0)
```

If `N.npv` is NULL → returns **0**.

---

## IFNULL()

```
IFNULL(value, replacement)
```

Returns:

- `value` if not NULL
- otherwise `replacement`

Example:

```
IFNULL(N.npv, 0)
```

Both functions work equally well for this problem.

---

# Final SQL Query (Using COALESCE)

```sql
SELECT
    Q.id,
    Q.year,
    COALESCE(N.npv, 0) AS npv
FROM Queries Q
LEFT JOIN NPV N
ON Q.id = N.id
AND Q.year = N.year;
```

---

# Alternative Query (Using IFNULL)

```sql
SELECT
    Q.id,
    Q.year,
    IFNULL(N.npv, 0) AS npv
FROM Queries Q
LEFT JOIN NPV N
ON Q.id = N.id
AND Q.year = N.year;
```

---

# Key Concepts

- **LEFT JOIN** ensures every query row appears.
- **COALESCE() / IFNULL()** replaces NULL values with 0.
- Joining on **(id, year)** ensures accurate matching.
