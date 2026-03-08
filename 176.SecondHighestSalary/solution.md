# Approach 1: Using Subquery + LIMIT

## Core Idea

1. Select distinct salaries.
2. Sort them in descending order.
3. Use `LIMIT 1 OFFSET 1` to skip the highest and fetch the second highest.

### Basic Query

```sql
SELECT DISTINCT
    Salary AS SecondHighestSalary
FROM Employee
ORDER BY Salary DESC
LIMIT 1 OFFSET 1;
```

## Issue

If there is only one distinct salary:

- The query returns **zero rows**
- This is considered incorrect for platforms like LeetCode
- Expected output: one row with NULL

---

## Corrected Version (Using Scalar Subquery)

We wrap the query inside a scalar subquery:

```sql
SELECT
    (SELECT DISTINCT
            Salary
     FROM Employee
     ORDER BY Salary DESC
     LIMIT 1 OFFSET 1) AS SecondHighestSalary;
```

## Why This Works

- A scalar subquery returns:
  - The value if it exists
  - NULL if it returns no rows
- The outer SELECT always returns exactly one row

### Behavior Comparison

| Scenario             | Basic Query    | Scalar Subquery         |
| -------------------- | -------------- | ----------------------- |
| Second salary exists | Returns 1 row  | Returns 1 row           |
| No second salary     | Returns 0 rows | Returns 1 row with NULL |

This ensures correct output format.

---

# Approach 2: Using IFNULL + LIMIT

## Core Idea

Use `IFNULL()` to explicitly handle NULL cases.

`IFNULL(expression, value)`:

- Returns `expression` if it is not NULL
- Otherwise returns `value`

### Query

```sql
SELECT
    IFNULL(
        (SELECT DISTINCT Salary
         FROM Employee
         ORDER BY Salary DESC
         LIMIT 1 OFFSET 1),
        NULL
    ) AS SecondHighestSalary;
```

## Why This Works

- The inner subquery behaves exactly like Approach 1.
- If no second salary exists, subquery returns NULL.
- `IFNULL(..., NULL)` ensures:
  - Output always contains exactly one row.
  - NULL is returned explicitly.

---

# Conceptual Difference Between the Approaches

| Approach               | Mechanism              | Handles Empty Case? | Output Shape      |
| ---------------------- | ---------------------- | ------------------- | ----------------- |
| Basic DISTINCT + LIMIT | Direct query           | ❌ No               | May return 0 rows |
| Scalar Subquery        | Expression-based       | ✅ Yes              | Always 1 row      |
| IFNULL Version         | Explicit NULL handling | ✅ Yes              | Always 1 row      |

---

# Key Takeaways

- `DISTINCT` ensures uniqueness of salary values.
- `LIMIT 1 OFFSET 1` retrieves the second highest value.
- Wrapping the query as a scalar subquery guarantees a single-row result.
- `IFNULL()` provides explicit NULL handling.
- Scalar subqueries behave like expressions — returning a single value or NULL.

---

# Final Recommendation

For correctness and predictable output structure, use either:

- Scalar subquery version (cleaner), or
- IFNULL version (more explicit handling).

Both ensure compliance with expected SQL output behavior in edge cases.
