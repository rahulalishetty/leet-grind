# 1907. Count Salary Categories — Detailed Summary

## Approach: Conditional Aggregation with `CASE WHEN` + `UNION`

This approach builds the final result one category at a time.

For each salary category, it:

1. creates a fixed category label
2. counts how many rows in `Accounts` belong to that category using `SUM(CASE WHEN ...)`
3. combines the three category-specific results using `UNION`

This guarantees that the final output always contains exactly these three rows:

- `Low Salary`
- `Average Salary`
- `High Salary`

Even if one category has no matching accounts, it still appears with count `0`.

---

## Problem Restatement

We need to count accounts in three categories:

### Low Salary

```text
income < 20000
```

### Average Salary

```text
20000 <= income <= 50000
```

### High Salary

```text
income > 50000
```

Important requirement:

- the result must always contain all three categories
- if a category has no accounts, its count must be `0`

---

# Core Idea

Instead of grouping the accounts table by a derived category, this approach manually constructs one row per category.

For each category, we run a query that looks like:

```sql
SELECT
    'Some Category' AS category,
    SUM(CASE WHEN condition THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts
```

Then we stack the three rows using `UNION`.

This is a very direct approach, and it naturally guarantees that missing categories still appear.

---

# Step 1: Count Low Salary Accounts

## Query

```sql
SELECT
    'Low Salary' AS category,
    SUM(CASE WHEN income < 20000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

---

## How It Works

### `'Low Salary' AS category`

This creates a literal label for the output row.

No matter what data is in the table, this query will return a row labeled:

```text
Low Salary
```

### `CASE WHEN income < 20000 THEN 1 ELSE 0 END`

This checks each account:

- if income is below `20000`, return `1`
- otherwise, return `0`

### `SUM(...)`

Summing those `1`s and `0`s gives the total number of accounts in the Low Salary category.

---

## Example

Suppose the input is:

| account_id | income |
| ---------: | -----: |
|          3 | 108939 |
|          2 |  12747 |
|          8 |  87709 |
|          6 |  91796 |

Then the expression:

```sql
CASE WHEN income < 20000 THEN 1 ELSE 0 END
```

produces:

| income | result |
| -----: | -----: |
| 108939 |      0 |
|  12747 |      1 |
|  87709 |      0 |
|  91796 |      0 |

Sum:

```text
0 + 1 + 0 + 0 = 1
```

So the result is:

| category   | accounts_count |
| ---------- | -------------: |
| Low Salary |              1 |

---

# Step 2: Count Average Salary Accounts

## Query

```sql
SELECT
    'Average Salary' AS category,
    SUM(CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END)
        AS accounts_count
FROM Accounts;
```

---

## How It Works

This time the condition checks whether income falls in the inclusive range:

```text
20000 to 50000
```

So for each row:

- if income is between `20000` and `50000` inclusive, return `1`
- otherwise return `0`

Then `SUM(...)` gives the count.

---

## Example

With the same sample data:

| account_id | income |
| ---------: | -----: |
|          3 | 108939 |
|          2 |  12747 |
|          8 |  87709 |
|          6 |  91796 |

The expression:

```sql
CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END
```

produces:

| income | result |
| -----: | -----: |
| 108939 |      0 |
|  12747 |      0 |
|  87709 |      0 |
|  91796 |      0 |

Sum:

```text
0
```

So the result is:

| category       | accounts_count |
| -------------- | -------------: |
| Average Salary |              0 |

---

# Step 3: Count High Salary Accounts

## Query

```sql
SELECT
    'High Salary' AS category,
    SUM(CASE WHEN income > 50000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

---

## How It Works

For each row:

- if income is greater than `50000`, return `1`
- otherwise return `0`

Then sum those results.

---

## Example

Using the same sample data:

| income | result |
| -----: | -----: |
| 108939 |      1 |
|  12747 |      0 |
|  87709 |      1 |
|  91796 |      1 |

Sum:

```text
1 + 0 + 1 + 1 = 3
```

So the result is:

| category    | accounts_count |
| ----------- | -------------: |
| High Salary |              3 |

---

# Step 4: Combine the Three Results with `UNION`

Once we have the three separate queries, we combine them using `UNION`.

## Full Query

```sql
SELECT
    'Low Salary' AS category,
    SUM(CASE WHEN income < 20000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts

UNION

SELECT
    'Average Salary' AS category,
    SUM(CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END)
        AS accounts_count
FROM Accounts

UNION

SELECT
    'High Salary' AS category,
    SUM(CASE WHEN income > 50000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

---

## Why `UNION` Works Here

Each query returns exactly one row with the same structure:

- first column: category name
- second column: count

So they can be safely combined into a single result set.

The final result becomes:

| category       | accounts_count |
| -------------- | -------------: |
| Low Salary     |              1 |
| Average Salary |              0 |
| High Salary    |              3 |

---

# Why This Approach Guarantees All Three Categories

This is the biggest advantage of this method.

If we instead grouped by derived categories from data, categories with no rows might disappear.

But here, each category is created explicitly with a separate query.

So even if no accounts fall into a category, that query still returns one row, and the sum becomes `0`.

That perfectly matches the problem requirement.

---

# Detailed Explanation of `CASE WHEN`

The general syntax of `CASE WHEN` is:

```sql
CASE
    WHEN condition1 THEN result1
    WHEN condition2 THEN result2
    ...
    ELSE resultN
END
```

In this problem, each category query uses a single condition and converts it into either:

- `1` if the row belongs to the category
- `0` otherwise

This makes `SUM(...)` behave like a conditional counter.

---

# Why `SUM(CASE WHEN ...)` Is Used Instead of `COUNT`

This is an important SQL pattern.

Suppose we wrote:

```sql
COUNT(CASE WHEN income < 20000 THEN 1 ELSE 0 END)
```

That would not behave the same way, because `COUNT(...)` counts non-null values, and both `1` and `0` are non-null.

So it would count every row.

By contrast, `SUM(CASE WHEN ... THEN 1 ELSE 0 END)` works exactly as intended:

- matching rows contribute `1`
- non-matching rows contribute `0`
- total sum becomes the count of matches

That is why `SUM(...)` is the correct tool here.

---

# Example Walkthrough

## Input

| account_id | income |
| ---------: | -----: |
|          3 | 108939 |
|          2 |  12747 |
|          8 |  87709 |
|          6 |  91796 |

---

## Category Classification

### Account 3

Income = `108939`

```text
> 50000
```

Category: `High Salary`

### Account 2

Income = `12747`

```text
< 20000
```

Category: `Low Salary`

### Account 8

Income = `87709`

```text
> 50000
```

Category: `High Salary`

### Account 6

Income = `91796`

```text
> 50000
```

Category: `High Salary`

---

## Final Counts

- Low Salary → `1`
- Average Salary → `0`
- High Salary → `3`

---

# Why `UNION` Column Requirements Matter

When using `UNION`, SQL requires:

- same number of columns in each query
- compatible data types in corresponding columns
- same logical column order

In this solution, all three queries return:

1. a string category
2. a numeric count

So the `UNION` works correctly.

---

# `UNION` vs `UNION ALL`

The provided solution uses `UNION`.

Because the three category labels are different, duplicate removal is irrelevant here.

So `UNION ALL` would also work and may be slightly more efficient.

A cleaner performance-oriented version is:

```sql
SELECT
    'Low Salary' AS category,
    SUM(CASE WHEN income < 20000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts

UNION ALL

SELECT
    'Average Salary' AS category,
    SUM(CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END)
        AS accounts_count
FROM Accounts

UNION ALL

SELECT
    'High Salary' AS category,
    SUM(CASE WHEN income > 50000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

Since the category names are distinct, `UNION ALL` is safe.

---

# Recommended Final Version

```sql
SELECT
    'Low Salary' AS category,
    SUM(CASE WHEN income < 20000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts

UNION ALL

SELECT
    'Average Salary' AS category,
    SUM(CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END)
        AS accounts_count
FROM Accounts

UNION ALL

SELECT
    'High Salary' AS category,
    SUM(CASE WHEN income > 50000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

This version keeps the same logic while avoiding unnecessary duplicate elimination work.

---

# Alternative Approach Note

Another valid approach is to create a small inline table of category names and left join counts onto it.

That can be more scalable if there are many categories.

But for exactly three fixed categories, the `UNION` or `UNION ALL` approach is very direct and easy to understand.

---

# Complexity Analysis

Let `n` be the number of rows in `Accounts`.

Each of the three queries scans the table once.

So the total work is roughly:

```text
O(3n) = O(n)
```

This is efficient enough for the problem, and the logic is simple and robust.

---

# Final Code Examples

## Original Style with `UNION`

```sql
SELECT
    'Low Salary' AS category,
    SUM(CASE WHEN income < 20000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts

UNION

SELECT
    'Average Salary' AS category,
    SUM(CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END)
        AS accounts_count
FROM Accounts

UNION

SELECT
    'High Salary' AS category,
    SUM(CASE WHEN income > 50000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

---

## Recommended Style with `UNION ALL`

```sql
SELECT
    'Low Salary' AS category,
    SUM(CASE WHEN income < 20000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts

UNION ALL

SELECT
    'Average Salary' AS category,
    SUM(CASE WHEN income >= 20000 AND income <= 50000 THEN 1 ELSE 0 END)
        AS accounts_count
FROM Accounts

UNION ALL

SELECT
    'High Salary' AS category,
    SUM(CASE WHEN income > 50000 THEN 1 ELSE 0 END) AS accounts_count
FROM Accounts;
```

---

# Key Takeaways

- Use one query per salary category
- `SUM(CASE WHEN ... THEN 1 ELSE 0 END)` counts rows conditionally
- Combine the three category rows with `UNION` or `UNION ALL`
- This approach guarantees all three categories always appear, even when a category has zero accounts

---
