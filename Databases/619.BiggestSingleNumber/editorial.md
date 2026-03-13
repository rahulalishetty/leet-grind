# 619. Biggest Single Number — Detailed Summary

## Approach 1: Using a Subquery and `MAX()`

This approach solves the problem in two clear steps:

1. find all numbers that appear **exactly once**
2. among those, return the **largest** one using `MAX()`

This is a very natural SQL solution because the problem itself has two layers:

- identify single numbers
- choose the biggest single number

---

## Problem Restatement

A **single number** is a number that appears exactly once in the table.

We need to return:

- the largest number whose frequency is `1`

If no such number exists, the answer should be:

```text
NULL
```

---

## Core Idea

The first task is to count how many times each number appears.

That is done with:

```sql
GROUP BY num
```

Then we keep only the groups whose count is exactly `1`:

```sql
HAVING COUNT(num) = 1
```

Now we have all single numbers.

Finally, among those numbers, we return the maximum value:

```sql
MAX(num)
```

If the subquery returns no rows, `MAX(num)` naturally returns `NULL`, which matches the problem requirement.

---

# Step 1: Find All Single Numbers

## Query

```sql
SELECT
    num
FROM
    my_numbers
GROUP BY num
HAVING COUNT(num) = 1;
```

---

## How This Works

### `GROUP BY num`

This groups identical numbers together.

So if the table is:

| num |
| --: |
|   8 |
|   8 |
|   3 |
|   3 |
|   1 |
|   4 |
|   5 |
|   6 |

then grouping produces logical groups like:

- `8` → appears 2 times
- `3` → appears 2 times
- `1` → appears 1 time
- `4` → appears 1 time
- `5` → appears 1 time
- `6` → appears 1 time

---

### `HAVING COUNT(num) = 1`

This keeps only groups whose frequency is exactly one.

So from the above grouped values, the remaining numbers are:

- `1`
- `4`
- `5`
- `6`

These are the single numbers.

---

# Step 2: Choose the Largest Single Number

## Query

```sql
SELECT
    MAX(num) AS num
FROM
    (
        SELECT
            num
        FROM
            my_numbers
        GROUP BY num
        HAVING COUNT(num) = 1
    ) AS t;
```

---

## Why `MAX()` Works

Once the subquery returns all single numbers, the problem reduces to:

> return the largest number from this filtered set

That is exactly what `MAX()` does.

So if the subquery returns:

| num |
| --: |
|   1 |
|   4 |
|   5 |
|   6 |

then:

```sql
MAX(num) = 6
```

So the final output is:

| num |
| --: |
|   6 |

---

# Full Implementation

```sql
SELECT
    MAX(num) AS num
FROM
    (
        SELECT
            num
        FROM
            my_numbers
        GROUP BY num
        HAVING COUNT(num) = 1
    ) AS t;
```

---

# Worked Example 1

## Input

| num |
| --: |
|   8 |
|   8 |
|   3 |
|   3 |
|   1 |
|   4 |
|   5 |
|   6 |

---

## Grouped Counts

| num | frequency |
| --: | --------: |
|   8 |         2 |
|   3 |         2 |
|   1 |         1 |
|   4 |         1 |
|   5 |         1 |
|   6 |         1 |

---

## Single Numbers

Filter with:

```sql
HAVING COUNT(num) = 1
```

Remaining values:

| num |
| --: |
|   1 |
|   4 |
|   5 |
|   6 |

---

## Largest Single Number

```sql
MAX(num) = 6
```

### Output

| num |
| --: |
|   6 |

---

# Worked Example 2

## Input

| num |
| --: |
|   8 |
|   8 |
|   7 |
|   7 |
|   3 |
|   3 |
|   3 |

---

## Grouped Counts

| num | frequency |
| --: | --------: |
|   8 |         2 |
|   7 |         2 |
|   3 |         3 |

---

## Single Numbers

There are no groups where:

```sql
COUNT(num) = 1
```

So the subquery returns no rows.

---

## What Happens with `MAX()`

When `MAX(num)` is applied to an empty result set, SQL returns:

```text
NULL
```

That exactly matches the required output.

### Output

| num  |
| ---- |
| NULL |

---

# Why This Approach Is Correct

This solution directly follows the definition of a single number.

A number is single if it appears once.

That is precisely captured by:

```sql
GROUP BY num
HAVING COUNT(num) = 1
```

Then the largest such number is selected by:

```sql
MAX(num)
```

The query is both logically correct and very concise.

---

# Clause-by-Clause Breakdown

## Inner Query

```sql
SELECT
    num
FROM
    my_numbers
GROUP BY num
HAVING COUNT(num) = 1
```

### Purpose

Return all numbers that appear exactly once.

---

## Outer Query

```sql
SELECT
    MAX(num) AS num
FROM (...)
```

### Purpose

Return the largest number from the filtered inner query result.

---

# Why `HAVING` Is Used Instead of `WHERE`

This is an important SQL detail.

`COUNT(num)` is an aggregate value.

Aggregate filters must be applied with `HAVING`, not `WHERE`.

So this is correct:

```sql
GROUP BY num
HAVING COUNT(num) = 1
```

This would be invalid or conceptually wrong:

```sql
WHERE COUNT(num) = 1
```

because `WHERE` is evaluated before grouping.

---

# Why `MAX()` Naturally Handles the `NULL` Case

One nice property of this solution is that it does not need any special handling for the case where no single numbers exist.

If the inner query returns no rows, the outer query:

```sql
MAX(num)
```

returns `NULL` automatically.

So the required behavior comes for free.

---

# Alternative Mental Model

You can think of the solution like this:

1. count how often each number appears
2. discard all repeated numbers
3. among the remaining numbers, keep the largest one

That is exactly what the query does.

---

# Complexity Analysis

Let `n` be the number of rows in `my_numbers`.

## Time Complexity

The main work is grouping the numbers and computing counts.

Conceptually, the solution is driven by:

- one grouping step
- one max over the grouped result

This is efficient for the problem.

## Space Complexity

The engine needs space to store grouped counts per distinct number.

That depends on the number of distinct values.

---

# Final Recommended Query

```sql
SELECT
    MAX(num) AS num
FROM
    (
        SELECT
            num
        FROM
            my_numbers
        GROUP BY num
        HAVING COUNT(num) = 1
    ) AS t;
```

---

# Key Takeaways

- Use `GROUP BY num` to count occurrences of each number
- Use `HAVING COUNT(num) = 1` to keep only single numbers
- Use `MAX(num)` to return the largest single number
- If no single number exists, `MAX()` over an empty set returns `NULL`, which is exactly what the problem requires

---
