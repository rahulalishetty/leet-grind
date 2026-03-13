# 1445. Apples & Oranges — Detailed Summary

## Overview

To perform a calculation between values that live in different rows of the same table, one straightforward SQL strategy is to read the same table more than once.

For this problem, each day has two rows in the `Sales` table:

- one for `apples`
- one for `oranges`

We need to compute:

```text
diff = apples_sold - oranges_sold
```

There are multiple ways to do that:

1. use aliases or a self join to compare the two rows directly
2. split the table into two separate derived tables first, then join them
3. use `SUM(CASE WHEN ...)` and treat one fruit as positive and the other as negative

Because this problem has exactly two categories and the final calculation is just a difference, the `SUM(CASE WHEN ...)` approach is especially clean.

---

# Problem Restatement

For each `sale_date`, return:

- `sale_date`
- `diff = apples sold - oranges sold`

The result must be ordered by:

```sql
sale_date
```

---

# Input Structure

The `Sales` table looks like:

| sale_date  | fruit   | sold_num |
| ---------- | ------- | -------: |
| 2020-05-01 | apples  |       10 |
| 2020-05-01 | oranges |        8 |
| 2020-05-02 | apples  |       15 |
| 2020-05-02 | oranges |       15 |

Since `(sale_date, fruit)` is the primary key, there is at most one row for each fruit on each day.

That property simplifies the solutions.

---

# Approach 1: Aliases or SELF JOIN

## Main Idea

Use the same `Sales` table twice:

- one copy for apples
- one copy for oranges

Then join the two copies on the same date.

After that, compute:

```text
apples.sold_num - oranges.sold_num
```

---

## Why This Works

For each day:

- one row represents apples sold
- one row represents oranges sold

If we join those two rows by `sale_date`, both numbers become available in the same result row, and subtraction becomes easy.

---

## Algorithm

1. Use the `Sales` table twice
2. Restrict one instance to apples and the other to oranges
3. Match them on the same `sale_date`
4. Subtract orange sales from apple sales
5. Order by `sale_date`

---

## Version A: Creating Aliases with a Comma Join

```sql
SELECT
    a.sale_date,
    a.sold_num - b.sold_num AS diff
FROM Sales a, Sales b
WHERE a.fruit IN ('apples')
  AND b.fruit IN ('oranges')
  AND a.sale_date = b.sale_date
GROUP BY 1
ORDER BY 1;
```

---

## Explanation

### `Sales a, Sales b`

This reads the `Sales` table twice:

- `a` will represent apples rows
- `b` will represent oranges rows

### `a.fruit IN ('apples')`

Keeps only apple rows in `a`.

### `b.fruit IN ('oranges')`

Keeps only orange rows in `b`.

### `a.sale_date = b.sale_date`

Pairs the apples row and oranges row from the same day.

### `a.sold_num - b.sold_num`

Computes the required difference.

---

## Version B: Using an Explicit SELF JOIN

```sql
SELECT
    a.sale_date,
    a.sold_num - b.sold_num AS diff
FROM Sales a
JOIN Sales b
  ON a.sale_date = b.sale_date
 AND a.fruit IN ('apples')
 AND b.fruit IN ('oranges')
GROUP BY 1
ORDER BY 1;
```

---

## Why the Explicit Join Version Is Better

This version expresses the relationship more clearly than the comma-style join.

It is usually preferred because:

- easier to read
- clearer join condition
- more modern SQL style

---

## Example Walkthrough for Approach 1

Given:

| sale_date  | fruit   | sold_num |
| ---------- | ------- | -------: |
| 2020-05-01 | apples  |       10 |
| 2020-05-01 | oranges |        8 |

After the self join:

| sale_date  | apples_sold | oranges_sold |
| ---------- | ----------: | -----------: |
| 2020-05-01 |          10 |            8 |

Now compute:

```text
10 - 8 = 2
```

So the output row becomes:

| sale_date  | diff |
| ---------- | ---: |
| 2020-05-01 |    2 |

---

# Approach 2: Create Two Separate Tables and Columns First

## Main Idea

Instead of reading the same table twice inline, first create two smaller derived tables:

- one containing only apples
- one containing only oranges

Then join those derived tables on `sale_date`.

This is conceptually similar to Approach 1, but more structured.

---

## Why This Works

This approach makes the logic very explicit:

- first isolate each fruit into its own dataset
- then bring them together by date
- then subtract

That can be useful if the filtering logic becomes more complicated.

---

## Algorithm

1. Create an apples-only subquery
2. Create an oranges-only subquery
3. Join them on `sale_date`
4. Compute `a.sold_num - b.sold_num`
5. Order by date

---

## Query

```sql
SELECT
    a.sale_date,
    a.sold_num - b.sold_num AS diff
FROM
    (SELECT sale_date, sold_num FROM Sales WHERE fruit IN ('apples')) a
JOIN
    (SELECT sale_date, sold_num FROM Sales WHERE fruit IN ('oranges')) b
ON a.sale_date = b.sale_date
GROUP BY 1
ORDER BY 1;
```

---

## Explanation

### Apples subquery

```sql
SELECT sale_date, sold_num
FROM Sales
WHERE fruit IN ('apples')
```

This gives:

| sale_date  | sold_num |
| ---------- | -------: |
| 2020-05-01 |       10 |
| 2020-05-02 |       15 |

### Oranges subquery

```sql
SELECT sale_date, sold_num
FROM Sales
WHERE fruit IN ('oranges')
```

This gives:

| sale_date  | sold_num |
| ---------- | -------: |
| 2020-05-01 |        8 |
| 2020-05-02 |       15 |

### Join on date

This pairs the apple and orange sales for the same day, making subtraction straightforward.

---

## When This Style Is Useful

This style can be easier to reason about when:

- filters are more complex
- there are additional transformations before joining
- you want to inspect each category separately

It is a bit more verbose, but often easier to debug.

---

# Approach 3: Calculate with `SUM(CASE WHEN)`

## Main Idea

Instead of joining rows together, convert each row into a signed contribution:

- apples stay positive
- oranges become negative

Then sum those contributions per day.

That sum becomes:

```text
apples_sold - oranges_sold
```

---

## Why This Works

Suppose for one day we have:

- apples = 10
- oranges = 8

If we transform the rows like this:

- apples → `+10`
- oranges → `-8`

then summing them gives:

```text
10 + (-8) = 2
```

which is exactly the desired difference.

This is the most elegant solution when there are just two categories and one needs to be subtracted from the other.

---

## Algorithm

1. For each row:
   - keep apple sales as positive
   - convert orange sales to negative
2. Group by `sale_date`
3. Sum the signed values
4. Order by `sale_date`

---

## Query

```sql
SELECT
    sale_date,
    SUM(
        CASE
            WHEN fruit IN ('apples') THEN sold_num
            WHEN fruit IN ('oranges') THEN sold_num * -1
        END
    ) AS diff
FROM Sales
GROUP BY 1
ORDER BY 1;
```

---

## Explanation

### `CASE WHEN`

```sql
CASE
    WHEN fruit IN ('apples') THEN sold_num
    WHEN fruit IN ('oranges') THEN sold_num * -1
END
```

This transforms rows:

- apples contribute positively
- oranges contribute negatively

### `SUM(...)`

After that transformation, summing over the group gives the difference.

### `GROUP BY sale_date`

This ensures the calculation is done separately for each day.

---

## Example Walkthrough for Approach 3

### 2020-05-01

Rows:

| fruit   | sold_num | signed value |
| ------- | -------: | -----------: |
| apples  |       10 |          +10 |
| oranges |        8 |           -8 |

Sum:

```text
10 + (-8) = 2
```

---

### 2020-05-02

Rows:

| fruit   | sold_num | signed value |
| ------- | -------: | -----------: |
| apples  |       15 |          +15 |
| oranges |       15 |          -15 |

Sum:

```text
15 + (-15) = 0
```

---

### 2020-05-03

Rows:

| fruit   | sold_num | signed value |
| ------- | -------: | -----------: |
| apples  |       20 |          +20 |
| oranges |        0 |            0 |

Sum:

```text
20 + 0 = 20
```

---

### 2020-05-04

Rows:

| fruit   | sold_num | signed value |
| ------- | -------: | -----------: |
| apples  |       15 |          +15 |
| oranges |       16 |          -16 |

Sum:

```text
15 + (-16) = -1
```

---

## Final Output

| sale_date  | diff |
| ---------- | ---: |
| 2020-05-01 |    2 |
| 2020-05-02 |    0 |
| 2020-05-03 |   20 |
| 2020-05-04 |   -1 |

---

# Comparing the Three Approaches

## Approach 1: Aliases / SELF JOIN

### Strengths

- straightforward
- natural when comparing two rows from the same table
- easy to understand

### Weaknesses

- requires reading the same table twice
- can become messier when categories increase

---

## Approach 2: Two Derived Tables First

### Strengths

- very structured
- easy to debug
- clean when categories need different preprocessing

### Weaknesses

- more verbose
- still essentially a join-based solution

---

## Approach 3: `SUM(CASE WHEN)`

### Strengths

- shortest and cleanest for this problem
- scans the table once conceptually
- elegant when one category should be added and the other subtracted

### Weaknesses

- requires comfort with conditional aggregation
- less intuitive at first if you are new to signed transformations

---

# Practical Recommendation

## Under interview pressure

A self join or alias-based solution is often the most direct first thought because it makes the subtraction visually obvious:

```text
apples_row - oranges_row
```

So Approach 1 is a very natural starting point.

## In practice for this exact problem

Approach 3 is usually the best:

```sql
SELECT
    sale_date,
    SUM(CASE
            WHEN fruit = 'apples' THEN sold_num
            WHEN fruit = 'oranges' THEN -sold_num
        END) AS diff
FROM Sales
GROUP BY sale_date
ORDER BY sale_date;
```

It is compact, efficient, and directly expresses the business rule.

---

# Recommended Final Query

```sql
SELECT
    sale_date,
    SUM(
        CASE
            WHEN fruit = 'apples' THEN sold_num
            WHEN fruit = 'oranges' THEN -sold_num
        END
    ) AS diff
FROM Sales
GROUP BY sale_date
ORDER BY sale_date;
```

This is the clearest final answer for this problem.

---

# Why `GROUP BY 1` Works but Is Less Clear

Some example queries use:

```sql
GROUP BY 1
ORDER BY 1
```

That means:

- group by the first selected column
- order by the first selected column

It works, but writing the column name explicitly is usually clearer:

```sql
GROUP BY sale_date
ORDER BY sale_date
```

That is easier to read and maintain.

---

# Complexity Analysis

Let `n` be the number of rows in `Sales`.

## Approach 1

Uses the table twice and joins by date. Performance depends on the engine and indexing, but conceptually it is heavier than necessary for this problem.

## Approach 2

Also uses two filtered table scans and a join. Clear, but not minimal.

## Approach 3

Single grouped scan with conditional aggregation.

Conceptually:

```text
Time: O(n)
Space: O(number of distinct sale dates)
```

This is typically the cleanest and most efficient among the three.

---

# Final Code Examples

## Approach 1 — Self Join

```sql
SELECT
    a.sale_date,
    a.sold_num - b.sold_num AS diff
FROM Sales a
JOIN Sales b
  ON a.sale_date = b.sale_date
 AND a.fruit = 'apples'
 AND b.fruit = 'oranges'
ORDER BY a.sale_date;
```

---

## Approach 2 — Two Derived Tables

```sql
SELECT
    a.sale_date,
    a.sold_num - b.sold_num AS diff
FROM
    (SELECT sale_date, sold_num FROM Sales WHERE fruit = 'apples') a
JOIN
    (SELECT sale_date, sold_num FROM Sales WHERE fruit = 'oranges') b
ON a.sale_date = b.sale_date
ORDER BY a.sale_date;
```

---

## Approach 3 — Conditional Aggregation

```sql
SELECT
    sale_date,
    SUM(
        CASE
            WHEN fruit = 'apples' THEN sold_num
            WHEN fruit = 'oranges' THEN -sold_num
        END
    ) AS diff
FROM Sales
GROUP BY sale_date
ORDER BY sale_date;
```

---

# Key Takeaways

- This problem is about computing `apples - oranges` for each day
- A self join works because each day has one apples row and one oranges row
- Splitting into two derived tables is a more structured variation of the same idea
- `SUM(CASE WHEN ...)` is the cleanest solution here because one fruit can be treated as positive and the other as negative
- For this problem, conditional aggregation is usually the best final answer

---
