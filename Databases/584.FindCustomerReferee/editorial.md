# 584. Find Customer Referee

## Approach: Using `<>` (`!=`) and `IS NULL`

## Intuition

A very common first attempt for this problem is:

```sql
SELECT name
FROM Customer
WHERE referee_id <> 2;
```

At first glance, this looks correct because the problem asks us to return customers who were **not referred by customer `2`**.

But this query is **incomplete**.

It will return only customers whose `referee_id` is a concrete value other than `2`.
It will **not** return customers whose `referee_id` is `NULL`.

That is the key issue.

---

## Why this happens

In MySQL, comparisons involving `NULL` do **not** behave like normal comparisons.

MySQL uses **three-valued logic**:

- `TRUE`
- `FALSE`
- `UNKNOWN`

Whenever you compare something with `NULL`, the result is usually **`UNKNOWN`**, not `TRUE`.

Examples:

```sql
NULL = 2
```

Result: `UNKNOWN`

```sql
NULL <> 2
```

Result: `UNKNOWN`

```sql
NULL = NULL
```

Result: `UNKNOWN`

Because the `WHERE` clause keeps only rows where the condition is **TRUE**, rows that evaluate to **UNKNOWN** are filtered out.

So this condition:

```sql
referee_id <> 2
```

does **not** include rows where `referee_id IS NULL`.

---

## What the problem really wants

We need to return customers who are either:

1. referred by someone whose id is **not 2**
2. **not referred by anyone at all**

The second case means:

```sql
referee_id IS NULL
```

So the correct condition is:

```sql
referee_id <> 2 OR referee_id IS NULL
```

---

## Correct SQL Solution

### Version 1: Using `<>`

```sql
SELECT name
FROM Customer
WHERE referee_id <> 2 OR referee_id IS NULL;
```

### Version 2: Using `!=`

```sql
SELECT name
FROM Customer
WHERE referee_id != 2 OR referee_id IS NULL;
```

Both versions are acceptable in MySQL.

---

## Step-by-step reasoning on the example

Given:

| id  | name | referee_id |
| --- | ---- | ---------- |
| 1   | Will | NULL       |
| 2   | Jane | NULL       |
| 3   | Alex | 2          |
| 4   | Bill | NULL       |
| 5   | Zack | 1          |
| 6   | Mark | 2          |

Now evaluate row by row.

### Row 1: Will

- `referee_id = NULL`
- `referee_id <> 2` -> `UNKNOWN`
- `referee_id IS NULL` -> `TRUE`
- `UNKNOWN OR TRUE` -> `TRUE`
- Included

### Row 2: Jane

- `referee_id = NULL`
- `referee_id <> 2` -> `UNKNOWN`
- `referee_id IS NULL` -> `TRUE`
- Included

### Row 3: Alex

- `referee_id = 2`
- `referee_id <> 2` -> `FALSE`
- `referee_id IS NULL` -> `FALSE`
- `FALSE OR FALSE` -> `FALSE`
- Excluded

### Row 4: Bill

- `referee_id = NULL`
- `referee_id <> 2` -> `UNKNOWN`
- `referee_id IS NULL` -> `TRUE`
- Included

### Row 5: Zack

- `referee_id = 1`
- `referee_id <> 2` -> `TRUE`
- `referee_id IS NULL` -> `FALSE`
- `TRUE OR FALSE` -> `TRUE`
- Included

### Row 6: Mark

- `referee_id = 2`
- `referee_id <> 2` -> `FALSE`
- `referee_id IS NULL` -> `FALSE`
- Excluded

So the final answer is:

| name |
| ---- |
| Will |
| Jane |
| Bill |
| Zack |

---

## Why the intuitive query is wrong

This query:

```sql
SELECT name
FROM Customer
WHERE referee_id <> 2;
```

returns only:

| name |
| ---- |
| Zack |

Why only Zack?

Because:

- Zack has `referee_id = 1`, so `1 <> 2` is `TRUE`
- Will, Jane, and Bill have `NULL`, so `NULL <> 2` is `UNKNOWN`, and those rows are removed

That is why this query misses valid answers.

---

## Another wrong solution

This query is also wrong:

```sql
SELECT name
FROM Customer
WHERE referee_id = NULL OR referee_id <> 2;
```

### Why it is wrong

Because:

```sql
referee_id = NULL
```

is never the correct way to test for `NULL`.

Any comparison using `= NULL` gives `UNKNOWN`, not `TRUE`.

So even if `referee_id` is actually `NULL`, this expression still does not work.

To test for null values, you must use:

```sql
IS NULL
```

and not:

```sql
= NULL
```

---

## Important SQL rule to remember

Use:

```sql
column IS NULL
```

or

```sql
column IS NOT NULL
```

Do **not** use:

```sql
column = NULL
column <> NULL
```

Those are logically incorrect in SQL.

---

## Compact Explanation

The problem is not really about joins or referrals.
It is mainly a **NULL-handling** problem.

The real trap is this:

```sql
referee_id <> 2
```

does **not** include `NULL` values.

So we must explicitly add:

```sql
OR referee_id IS NULL
```

That gives the correct final query.

---

## Final Answer

```sql
SELECT name
FROM Customer
WHERE referee_id <> 2 OR referee_id IS NULL;
```

or equivalently:

```sql
SELECT name
FROM Customer
WHERE referee_id != 2 OR referee_id IS NULL;
```

---

## Complexity

Since we scan the table once:

- **Time Complexity:** `O(n)`
- **Space Complexity:** `O(1)`

where `n` is the number of rows in `Customer`.

---

## Key Takeaways

1. `NULL` is not a regular value in SQL.
2. Comparisons with `NULL` usually produce `UNKNOWN`.
3. `WHERE` keeps only rows where the condition is `TRUE`.
4. To check for nulls, always use `IS NULL` or `IS NOT NULL`.
5. For this problem, the correct filter is:

```sql
referee_id <> 2 OR referee_id IS NULL
```
