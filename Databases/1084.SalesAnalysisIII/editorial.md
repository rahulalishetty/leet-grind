# 1084. Sales Analysis III — Detailed Summary

## Problem Recap

We need to report the products that were **only sold in the first quarter of 2019**.

That means a product qualifies only if **all of its sales** happened within this inclusive date range:

```text
2019-01-01 to 2019-03-31
```

So the requirement is not merely:

- sold during Q1 2019

It is stricter:

- sold during Q1 2019
- and **never sold outside** Q1 2019

The output should include:

- `product_id`
- `product_name`

---

## Core Idea

For each product, we want to inspect the full range of its sales dates.

A product qualifies if:

- its **earliest** sale date is on or after `2019-01-01`
- its **latest** sale date is on or before `2019-03-31`

That leads naturally to using aggregation:

- `MIN(sale_date)` → first sale date for that product
- `MAX(sale_date)` → last sale date for that product

If both stay inside Q1 2019, then every sale for that product must also be inside Q1 2019.

---

## Why this works

Suppose all sales for a product are inside Q1.

Then clearly:

- the minimum date is still inside Q1
- the maximum date is still inside Q1

Now suppose even one sale lies outside Q1.

Then one of these must happen:

- a sale is before `2019-01-01` → `MIN(sale_date)` becomes too early
- a sale is after `2019-03-31` → `MAX(sale_date)` becomes too late

So checking only `MIN` and `MAX` is enough.

That is the key insight.

---

## Base Query Structure

We first join `Sales` and `Product` so that we can return both product id and product name.

```sql
SELECT DISTINCT p.product_id, p.product_name
FROM Sales s
LEFT JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id;
```

---

## Step-by-step explanation

### Step 1: Join `Sales` and `Product`

```sql
FROM Sales s
LEFT JOIN Product p
  ON p.product_id = s.product_id
```

This connects each sale row to its product details.

We need this because:

- `Sales` has `product_id`
- `Product` has `product_name`

Without the join, we cannot return the product name.

---

### Step 2: Group by product

```sql
GROUP BY p.product_id
```

We want to evaluate each product as a whole, not each sale row separately.

Grouping by `product_id` lets us aggregate all sales belonging to the same product.

---

### Step 3: Use `HAVING` on aggregated dates

```sql
HAVING MIN(sale_date) >= '2019-01-01'
   AND MAX(sale_date) <= '2019-03-31';
```

This is the heart of the solution.

For each grouped product:

- `MIN(sale_date)` gets the earliest sale
- `MAX(sale_date)` gets the latest sale

A product is kept only when both boundaries stay within Q1 2019.

---

## Final Query

```sql
SELECT DISTINCT p.product_id, p.product_name
FROM Sales s
LEFT JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id
HAVING MIN(s.sale_date) >= '2019-01-01'
   AND MAX(s.sale_date) <= '2019-03-31';
```

---

## Important correction

The original idea is good, but there is one SQL detail worth tightening.

If you write:

```sql
SELECT DISTINCT p.product_id, p.product_name
...
GROUP BY p.product_id
```

some SQL engines accept it, but stricter SQL dialects may complain because `product_name` is selected without being grouped or aggregated.

A cleaner and more portable version is:

```sql
SELECT p.product_id, p.product_name
FROM Sales s
JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id, p.product_name
HAVING MIN(s.sale_date) >= '2019-01-01'
   AND MAX(s.sale_date) <= '2019-03-31';
```

This version is better because:

- no unnecessary `DISTINCT`
- full SQL correctness across stricter engines
- clearer intent

---

# Detailed Walkthrough with Example

## Input

### Product

| product_id | product_name | unit_price |
| ---------: | ------------ | ---------: |
|          1 | S8           |       1000 |
|          2 | G4           |        800 |
|          3 | iPhone       |       1400 |

### Sales

| seller_id | product_id | buyer_id | sale_date  | quantity | price |
| --------: | ---------: | -------: | ---------- | -------: | ----: |
|         1 |          1 |        1 | 2019-01-21 |        2 |  2000 |
|         1 |          2 |        2 | 2019-02-17 |        1 |   800 |
|         2 |          2 |        3 | 2019-06-02 |        1 |   800 |
|         3 |          3 |        4 | 2019-05-13 |        2 |  2800 |

---

## Group by product

After grouping by `product_id`, think of the data like this:

### Product 1 (`S8`)

Sales dates:

- `2019-01-21`

So:

- `MIN(sale_date) = 2019-01-21`
- `MAX(sale_date) = 2019-01-21`

Both are within Q1 2019, so product 1 qualifies.

---

### Product 2 (`G4`)

Sales dates:

- `2019-02-17`
- `2019-06-02`

So:

- `MIN(sale_date) = 2019-02-17`
- `MAX(sale_date) = 2019-06-02`

The maximum date is after `2019-03-31`, so product 2 does not qualify.

---

### Product 3 (`iPhone`)

Sales dates:

- `2019-05-13`

So:

- `MIN(sale_date) = 2019-05-13`
- `MAX(sale_date) = 2019-05-13`

The minimum date is already after Q1, which means this product was not sold in the target quarter at all. It does not qualify.

---

## Result

Only:

| product_id | product_name |
| ---------: | ------------ |
|          1 | S8           |

---

# Why `HAVING` and not `WHERE`

This is a frequent source of confusion.

- `WHERE` filters rows **before grouping**
- `HAVING` filters groups **after aggregation**

Here we need to compare:

- `MIN(sale_date)`
- `MAX(sale_date)`

These are aggregate values, so the filtering must happen in `HAVING`.

Correct:

```sql
GROUP BY p.product_id, p.product_name
HAVING MIN(s.sale_date) >= '2019-01-01'
   AND MAX(s.sale_date) <= '2019-03-31'
```

Not correct for this logic:

```sql
WHERE sale_date >= '2019-01-01'
  AND sale_date <= '2019-03-31'
```

That would remove rows outside Q1 before grouping, which would hide disqualifying sales instead of detecting them.

That mistake would produce wrong answers.

---

# Why filtering in `WHERE` would be wrong

Consider product 2:

- sold on `2019-02-17`
- sold again on `2019-06-02`

If you do this:

```sql
WHERE sale_date BETWEEN '2019-01-01' AND '2019-03-31'
```

then the `2019-06-02` row disappears before aggregation.

Now the grouped result for product 2 would only see `2019-02-17`, making it look like product 2 was sold only in Q1.

That is incorrect.

So the query must evaluate **all sales first**, then decide whether the product qualifies.

---

# Cleaner Recommended Version

```sql
SELECT p.product_id, p.product_name
FROM Sales s
JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id, p.product_name
HAVING MIN(s.sale_date) >= '2019-01-01'
   AND MAX(s.sale_date) <= '2019-03-31';
```

---

## Why this version is preferred

### 1. `JOIN` instead of `LEFT JOIN`

A `LEFT JOIN` keeps rows from the left table even if there is no match on the right.

But here:

- every `Sales.product_id` is a foreign key to `Product.product_id`

So a matching product must exist.

That means a regular `JOIN` is sufficient and clearer.

---

### 2. No need for `DISTINCT`

Once we group by:

```sql
GROUP BY p.product_id, p.product_name
```

we already get one row per product.

So `DISTINCT` becomes redundant.

---

### 3. More portable SQL

Grouping by both:

- `p.product_id`
- `p.product_name`

is better SQL style and avoids issues in stricter SQL dialects.

---

# Alternative equivalent formulation

Another way to express the same condition is to count whether a product has any sale outside Q1.

## Example

```sql
SELECT p.product_id, p.product_name
FROM Sales s
JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id, p.product_name
HAVING SUM(
    CASE
        WHEN s.sale_date < '2019-01-01' OR s.sale_date > '2019-03-31'
        THEN 1
        ELSE 0
    END
) = 0;
```

---

## How this works

For each sale row:

- add `1` if the sale lies outside Q1
- add `0` otherwise

Then for each product:

- if the sum is `0`, there are no outside-Q1 sales
- so the product qualifies

This is logically equivalent to the `MIN/MAX` approach.

---

## Which is better: `MIN/MAX` or `CASE WHEN`?

For this specific problem, `MIN/MAX` is simpler and more elegant.

Why:

- it directly captures the date range
- it is shorter
- it is easier to reason about

The `CASE WHEN` formulation is useful when the condition is more complicated than a simple continuous date range.

---

# Edge Cases

## 1. Duplicate rows in `Sales`

The problem says duplicates may exist.

That does not break this solution.

Why:

- duplicate rows do not change the earliest or latest date unless they introduce a different date
- grouping still works correctly
- a product still appears only once in the final result

---

## 2. Product sold only once

Example:

- one sale on `2019-02-10`

Then:

- `MIN = MAX = 2019-02-10`

It qualifies because the single sale lies within Q1.

---

## 3. Product sold both inside and outside Q1

Example:

- `2019-03-15`
- `2019-04-01`

Then:

- `MAX = 2019-04-01`

This exceeds `2019-03-31`, so the product is excluded.

---

## 4. Product sold only outside Q1

Example:

- `2019-05-10`

Then:

- `MIN = 2019-05-10`

This is not `>= '2019-01-01'` and `<= '2019-03-31'` as a complete range condition, so it fails the Q1-only rule.

More precisely, `MAX <= '2019-03-31'` fails.

So the product is excluded.

---

# Time and Space Complexity

Assume:

- `n` = number of rows in `Sales`

## Time Complexity

The query scans sales rows and groups them by product.

Typical complexity is:

```text
O(n)
```

for scanning, plus grouping cost depending on the SQL engine and indexing strategy.

In practical SQL interview discussion, it is fine to say:

- overall dominated by scanning and grouping the `Sales` table

## Space Complexity

The engine needs grouping state per product.

If there are `k` distinct products sold, the grouping state is roughly:

```text
O(k)
```

---

# Final Takeaway

This problem is about checking whether **all sales of a product** fall inside a required date window.

The most elegant observation is:

- if all sale dates are within the range,
- then both the earliest and latest dates must also be within the range.

That makes `MIN(sale_date)` and `MAX(sale_date)` the perfect tools.

---

# Final Recommended Solution

```sql
SELECT p.product_id, p.product_name
FROM Sales s
JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id, p.product_name
HAVING MIN(s.sale_date) >= '2019-01-01'
   AND MAX(s.sale_date) <= '2019-03-31';
```

---

# Original Style Version

If you want a version that stays very close to the original approach text:

```sql
SELECT DISTINCT p.product_id, p.product_name
FROM Sales s
LEFT JOIN Product p
  ON p.product_id = s.product_id
GROUP BY p.product_id
HAVING MIN(s.sale_date) >= '2019-01-01'
   AND MAX(s.sale_date) <= '2019-03-31';
```

This captures the intended idea, but the grouped version with both `product_id` and `product_name` is generally the better form.
