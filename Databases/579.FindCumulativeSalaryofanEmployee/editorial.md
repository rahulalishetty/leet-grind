# 579. Find Cumulative Salary of an Employee

## Approach: Using `OUTER JOIN` and Temporary Tables

### Intuition

The problem asks us to compute a **3-month cumulative salary** for each employee, but with two special rules:

1. For a given month, include:
   - the current month,
   - the previous month,
   - and the month before that.
2. Exclude the **most recent month** worked by each employee.

A practical SQL way to solve this is to break the task into two parts:

- first compute the 3-month cumulative sum,
- then remove the latest month for each employee.

This approach uses **self joins** on the `Employee` table.

---

## High-Level Idea

For each employee-month row `E1`:

- join to the same employee's previous month as `E2`
- join to the same employee's two-months-ago row as `E3`

Then compute:

```sql
E1.salary + E2.salary + E3.salary
```

If a prior month does not exist, treat it as `0` using `IFNULL`.

After that, we separately identify the most recent month per employee and exclude it.

---

# Step 1: Understand the self join idea with 2 months first

Before jumping to 3 months, it helps to understand the 2-month version.

```sql
SELECT *
FROM Employee E1
LEFT JOIN Employee E2
    ON E2.id = E1.id
   AND E2.month = E1.month - 1
ORDER BY E1.id ASC, E1.month DESC;
```

### What this means

- `E1` is the current employee-month row
- `E2` tries to match the same employee in the previous month

So for each row in `E1`, we attach salary information from month `month - 1`.

---

## Example joined shape

| E1.id | E1.month | E1.salary | E2.id | E2.month | E2.salary |
| ----: | -------: | --------: | ----: | -------: | --------: |
|     1 |        4 |        60 |     1 |        3 |        40 |
|     1 |        3 |        40 |     1 |        2 |        30 |
|     1 |        2 |        30 |     1 |        1 |        20 |
|     1 |        1 |        20 |  NULL |     NULL |      NULL |
|     2 |        2 |        30 |     2 |        1 |        20 |
|     2 |        1 |        20 |  NULL |     NULL |      NULL |
|     3 |        4 |        70 |     3 |        3 |        60 |
|     3 |        3 |        60 |     3 |        2 |        40 |
|     3 |        2 |        40 |  NULL |     NULL |      NULL |

The blank values are actually `NULL` in SQL.

---

# Step 2: Compute a 2-month cumulative sum

Once we have the join, we can sum the current and previous month:

```sql
SELECT
    E1.id,
    E1.month,
    IFNULL(E1.salary, 0) + IFNULL(E2.salary, 0) AS Salary
FROM Employee E1
LEFT JOIN Employee E2
    ON E2.id = E1.id
   AND E2.month = E1.month - 1
ORDER BY E1.id ASC, E1.month DESC;
```

---

## Why `IFNULL`?

If there is no previous month row, `E2.salary` is `NULL`.

We want missing months to contribute `0`, so:

```sql
IFNULL(E2.salary, 0)
```

converts the missing salary into zero.

---

## Example 2-month result

|  id | month | Salary |
| --: | ----: | -----: |
|   1 |     4 |    100 |
|   1 |     3 |     70 |
|   1 |     2 |     50 |
|   1 |     1 |     20 |
|   2 |     2 |     50 |
|   2 |     1 |     20 |
|   3 |     4 |    130 |
|   3 |     3 |    100 |
|   3 |     2 |     40 |

This is only a warm-up. The real problem needs **3 months**.

---

# Step 3: Extend to 3 months

Now we join one more time:

- `E1` = current month
- `E2` = previous month
- `E3` = two months ago

```sql
SELECT
    E1.id,
    E1.month,
    (IFNULL(E1.salary, 0) + IFNULL(E2.salary, 0) + IFNULL(E3.salary, 0)) AS Salary
FROM Employee E1
LEFT JOIN Employee E2
    ON E2.id = E1.id
   AND E2.month = E1.month - 1
LEFT JOIN Employee E3
    ON E3.id = E1.id
   AND E3.month = E1.month - 2
ORDER BY E1.id ASC, E1.month DESC;
```

---

## What this does

For each `(id, month)` row:

- take salary from the current month
- add salary from month - 1 if it exists
- add salary from month - 2 if it exists

Missing months contribute `0`.

---

## Example 3-month result

|  id | month | Salary |
| --: | ----: | -----: |
|   1 |     4 |    130 |
|   1 |     3 |     90 |
|   1 |     2 |     50 |
|   1 |     1 |     20 |
|   2 |     2 |     50 |
|   2 |     1 |     20 |
|   3 |     4 |    170 |
|   3 |     3 |    100 |
|   3 |     2 |     40 |

This computes the rolling sum correctly, but it still includes the most recent month for each employee. The problem says we must remove that month.

---

# Step 4: Identify the most recent month for each employee

We can create a temporary derived table that stores the latest month per employee:

```sql
SELECT
    id,
    MAX(month) AS month
FROM Employee
GROUP BY id
HAVING COUNT(*) > 1;
```

---

## Why `HAVING COUNT(*) > 1`?

If an employee has only one salary row total, then after excluding their most recent month there would be no remaining rows.

This filter keeps only employees who have more than one month of data, because only they can contribute to the final answer.

---

## Example latest-month table

|  id | month |
| --: | ----: |
|   1 |     4 |
|   2 |     2 |
|   3 |     4 |

This tells us the month we need to exclude for each employee.

---

# Step 5: Exclude the most recent month

Now we combine everything.

We use the derived table `maxmonth` and only join rows from `Employee E1` where:

```sql
maxmonth.month > E1.month
```

That condition excludes the latest month itself and keeps only earlier months.

---

## Final Query

```sql
SELECT
    E1.id,
    E1.month,
    (IFNULL(E1.salary, 0) + IFNULL(E2.salary, 0) + IFNULL(E3.salary, 0)) AS Salary
FROM
    (
        SELECT
            id,
            MAX(month) AS month
        FROM Employee
        GROUP BY id
        HAVING COUNT(*) > 1
    ) AS maxmonth
LEFT JOIN Employee E1
    ON maxmonth.id = E1.id
   AND maxmonth.month > E1.month
LEFT JOIN Employee E2
    ON E2.id = E1.id
   AND E2.month = E1.month - 1
LEFT JOIN Employee E3
    ON E3.id = E1.id
   AND E3.month = E1.month - 2
ORDER BY E1.id ASC, E1.month DESC;
```

---

# Detailed Explanation of the Final Query

## Derived table: `maxmonth`

```sql
SELECT
    id,
    MAX(month) AS month
FROM Employee
GROUP BY id
HAVING COUNT(*) > 1
```

This produces one row per employee containing their most recent month.

---

## Join to `E1`

```sql
LEFT JOIN Employee E1
    ON maxmonth.id = E1.id
   AND maxmonth.month > E1.month
```

This keeps only months strictly earlier than the employee's most recent month.

That is how the latest month is excluded.

---

## Join to `E2`

```sql
LEFT JOIN Employee E2
    ON E2.id = E1.id
   AND E2.month = E1.month - 1
```

Gets the previous month salary.

---

## Join to `E3`

```sql
LEFT JOIN Employee E3
    ON E3.id = E1.id
   AND E3.month = E1.month - 2
```

Gets the salary from two months earlier.

---

## Salary expression

```sql
IFNULL(E1.salary, 0) + IFNULL(E2.salary, 0) + IFNULL(E3.salary, 0)
```

This gives the 3-month rolling sum while treating missing months as zero.

---

# Dry Run on the Example

Suppose we have:

|  id | month | salary |
| --: | ----: | -----: |
|   1 |     1 |     20 |
|   1 |     2 |     30 |
|   1 |     3 |     40 |
|   1 |     4 |     60 |
|   2 |     1 |     20 |
|   2 |     2 |     30 |
|   3 |     2 |     40 |
|   3 |     3 |     60 |
|   3 |     4 |     70 |

## Latest month table

|  id | month |
| --: | ----: |
|   1 |     4 |
|   2 |     2 |
|   3 |     4 |

## Excluding latest month

Remaining `E1` rows:

|  id | month | salary |
| --: | ----: | -----: |
|   1 |     3 |     40 |
|   1 |     2 |     30 |
|   1 |     1 |     20 |
|   2 |     1 |     20 |
|   3 |     3 |     60 |
|   3 |     2 |     40 |

## Compute rolling sums

### Employee 1

- Month 3 → `40 + 30 + 20 = 90`
- Month 2 → `30 + 20 + 0 = 50`
- Month 1 → `20 + 0 + 0 = 20`

### Employee 2

- Month 1 → `20 + 0 + 0 = 20`

### Employee 3

- Month 3 → `60 + 40 + 0 = 100`
- Month 2 → `40 + 0 + 0 = 40`

Final output:

|  id | month | Salary |
| --: | ----: | -----: |
|   1 |     3 |     90 |
|   1 |     2 |     50 |
|   1 |     1 |     20 |
|   2 |     1 |     20 |
|   3 |     3 |    100 |
|   3 |     2 |     40 |

---

# Important Subtlety

This specific solution works well for the shown data, but you should notice something carefully:

- it assumes that months are numeric and consecutive comparisons like `month - 1` and `month - 2` are meaningful,
- which is correct for this problem.

It also handles missing months properly because if `month - 1` or `month - 2` does not exist, the join returns `NULL`, which becomes `0`.

For example:

- employee 1 working in month 7 but not 5 or 6
- salary for month 7 becomes `90 + 0 + 0 = 90`

That matches the problem requirement.

---

# Complexity Analysis

Let `n` be the number of rows in `Employee`.

## Time Complexity

This solution uses:

- one grouped scan to find latest month per employee
- up to three self joins on the `Employee` table

In practical terms, the complexity depends on indexing and execution strategy, but conceptually this is heavier than a simple scan because of repeated joins.

A reasonable interview-level statement is:

- grouping cost plus multiple self joins
- generally more expensive than a window-function solution, but still valid for moderate input sizes

## Space Complexity

- extra space for the derived `maxmonth` table
- plus join intermediates managed by the SQL engine

---

# Why This Approach Is Useful

This approach is valuable because it shows how to solve rolling-sum problems even **without window functions**.

That makes it useful when:

- window functions are unavailable,
- or you want to practice classic SQL join-based reasoning.

---

# Alternative Perspective

You can think of this as manually building a 3-row sliding window:

- current row
- previous row
- two rows back

using joins instead of analytical window functions.

That is the main conceptual insight.

---

# Final MySQL Solution

```sql
SELECT
    E1.id,
    E1.month,
    (IFNULL(E1.salary, 0) + IFNULL(E2.salary, 0) + IFNULL(E3.salary, 0)) AS Salary
FROM
    (
        SELECT
            id,
            MAX(month) AS month
        FROM
            Employee
        GROUP BY id
        HAVING COUNT(*) > 1
    ) AS maxmonth
LEFT JOIN Employee E1
    ON maxmonth.id = E1.id
   AND maxmonth.month > E1.month
LEFT JOIN Employee E2
    ON E2.id = E1.id
   AND E2.month = E1.month - 1
LEFT JOIN Employee E3
    ON E3.id = E1.id
   AND E3.month = E1.month - 2
ORDER BY E1.id ASC, E1.month DESC;
```

---

# Key Takeaways

- Use self joins to fetch earlier months for the same employee.
- `LEFT JOIN` is essential because prior months may be missing.
- `IFNULL(..., 0)` turns missing salaries into zero.
- Use a grouped derived table to identify the latest month per employee.
- Exclude the latest month by requiring:

```sql
maxmonth.month > E1.month
```

- Order the result with:

```sql
ORDER BY E1.id ASC, E1.month DESC
```

This produces the required cumulative salary summary.
