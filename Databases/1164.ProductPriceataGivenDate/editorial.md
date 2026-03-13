# 1164. Product Price at a Given Date — Detailed Summary

## Problem Restatement

We are given a `Products` table where each row means:

- a product's price changed to `new_price`
- on a specific `change_date`

Important rule:

- every product starts with an **initial price of 10**

We need to find the price of **all products** on the date:

```text
2019-08-16
```

That means for each product:

- if it had one or more price changes on or before `2019-08-16`, use the **latest such price**
- otherwise, use the initial price `10`

---

# Core Challenge

The tricky part is not finding the latest date alone.

The tricky part is:

> once we find the latest valid `change_date` for a product, how do we retrieve the matching `new_price` from that exact row?

This is why a plain `GROUP BY product_id` is not enough.

For example, consider:

| product_id | new_price | change_date |
| ---------: | --------: | ----------- |
|          1 |        20 | 2019-08-14  |
|          1 |        30 | 2019-08-15  |
|          1 |        35 | 2019-08-16  |

If we write something like:

```sql
SELECT product_id, new_price, MAX(change_date)
FROM Products
WHERE change_date <= '2019-08-16'
GROUP BY product_id;
```

this is logically problematic because after grouping by `product_id`, there are multiple candidate `new_price` values:

- 20
- 30
- 35

The database cannot safely choose the correct one unless we explicitly tie the chosen date to the chosen price.

So every correct solution must somehow do this:

1. identify the relevant latest date per product
2. retrieve the `new_price` belonging to that exact row
3. handle products with no earlier change by returning 10

---

# Approach 1: Divide Cases Using `UNION ALL`

## Main Idea

Split the products into two categories:

### Case 1

Products whose **first** ever change happens **after** `2019-08-16`

These products had no price change before or on the target date, so their price is still:

```text
10
```

### Case 2

Products that had at least one change on or before `2019-08-16`

For these, we need the **latest** change on or before that date, and then we return its `new_price`.

Then combine the two result sets with:

```sql
UNION ALL
```

---

## Why `UNION ALL` Instead of `UNION`

`UNION` removes duplicates, which usually means extra sorting or hashing work.

Here, the two subqueries represent two **disjoint cases**:

- products with no valid change by the target date
- products with at least one valid change by the target date

A product cannot belong to both groups.

So duplicate removal is unnecessary.

That is why `UNION ALL` is better.

---

## Why We Cannot Just Group and Select `new_price`

Suppose we filter to dates on or before `2019-08-16`:

| product_id | new_price | change_date |
| ---------: | --------: | ----------- |
|          1 |        20 | 2019-08-14  |
|          1 |        30 | 2019-08-15  |
|          1 |        35 | 2019-08-16  |
|          2 |        50 | 2019-08-14  |

The latest valid date for product `1` is `2019-08-16`, so the correct price is `35`.

But `GROUP BY product_id` alone does not tell the DBMS which `new_price` belongs to `MAX(change_date)` unless we explicitly match both fields.

That is why this approach uses:

```sql
(product_id, change_date) IN ( ... )
```

to retrieve the exact row corresponding to the maximum date.

---

## Algorithm for Approach 1

### Step 1

Find products whose earliest change is after `2019-08-16`.

These products never changed before the target date, so their price is `10`.

### Step 2

Find, for every product that has at least one valid change, the maximum `change_date` where:

```sql
change_date <= '2019-08-16'
```

### Step 3

Retrieve the `new_price` from rows matching those `(product_id, max_change_date)` pairs.

### Step 4

Combine both result sets using `UNION ALL`.

---

## Implementation — Approach 1

```sql
SELECT
  product_id,
  10 AS price
FROM
  Products
GROUP BY
  product_id
HAVING
  MIN(change_date) > '2019-08-16'

UNION ALL

SELECT
  product_id,
  new_price AS price
FROM
  Products
WHERE
  (product_id, change_date) IN (
    SELECT
      product_id,
      MAX(change_date)
    FROM
      Products
    WHERE
      change_date <= '2019-08-16'
    GROUP BY
      product_id
  );
```

---

## Detailed Explanation of the First Subquery

```sql
SELECT
  product_id,
  10 AS price
FROM
  Products
GROUP BY
  product_id
HAVING
  MIN(change_date) > '2019-08-16'
```

### What it does

For each product, it looks at the earliest date that product ever changed.

If even the earliest change is after `2019-08-16`, then clearly the product had no change before or on the target date.

So the product's price on `2019-08-16` must still be the default:

```text
10
```

### Example

For product `3`:

| product_id | new_price | change_date |
| ---------: | --------: | ----------- |
|          3 |        20 | 2019-08-18  |

Minimum change date is `2019-08-18`, which is after `2019-08-16`.

So product `3` gets price `10`.

---

## Detailed Explanation of the Second Subquery

```sql
SELECT
  product_id,
  new_price AS price
FROM
  Products
WHERE
  (product_id, change_date) IN (
    SELECT
      product_id,
      MAX(change_date)
    FROM
      Products
    WHERE
      change_date <= '2019-08-16'
    GROUP BY
      product_id
  );
```

### Inner query

```sql
SELECT
  product_id,
  MAX(change_date)
FROM
  Products
WHERE
  change_date <= '2019-08-16'
GROUP BY
  product_id
```

This finds the latest valid change date for each product.

### Outer query

The outer query then picks the row whose `(product_id, change_date)` matches that pair.

That gives us the correct `new_price`.

### Example

For product `1`:

Valid rows up to `2019-08-16`:

| product_id | new_price | change_date |
| ---------: | --------: | ----------- |
|          1 |        20 | 2019-08-14  |
|          1 |        30 | 2019-08-15  |
|          1 |        35 | 2019-08-16  |

The max date is `2019-08-16`, so the matching row gives:

```text
price = 35
```

For product `2`:

Valid rows up to `2019-08-16`:

| product_id | new_price | change_date |
| ---------: | --------: | ----------- |
|          2 |        50 | 2019-08-14  |

The max date is `2019-08-14`, so the matching row gives:

```text
price = 50
```

---

## Result of Approach 1 on the Example

Combined result:

| product_id | price |
| ---------: | ----: |
|          2 |    50 |
|          1 |    35 |
|          3 |    10 |

This matches the expected output.

---

# Approach 2: Divide Cases Using `LEFT JOIN`

## Main Idea

This approach still separates the logic conceptually, but instead of physically combining two result sets with `UNION ALL`, it uses a `LEFT JOIN`.

The idea is:

1. generate the list of all unique product IDs
2. compute each product's latest changed price on or before the target date
3. left join those prices back to all products
4. if no matching row exists, the result is `NULL`, meaning the product never changed before the target date
5. replace `NULL` with `10`

So `LEFT JOIN` handles the default-price case naturally.

---

## Why `LEFT JOIN` Works Here

Suppose a product has no price change on or before `2019-08-16`.

Then it will not appear in the "latest valid price" derived table.

When we `LEFT JOIN` from all unique products to that derived table:

- products with a match get their actual latest price
- products without a match get `NULL`

Then:

```sql
IFNULL(..., 10)
```

turns that missing value into the default price.

---

## Algorithm for Approach 2

### Step 1

Build a table of all distinct product IDs.

### Step 2

For products with valid changes up to `2019-08-16`, find the latest `change_date`.

### Step 3

Use an `INNER JOIN` to get the `new_price` corresponding to that latest date.

### Step 4

`LEFT JOIN` this result to the full product list.

### Step 5

Use `IFNULL` to assign price `10` when no earlier change exists.

---

## Implementation — Approach 2

```sql
SELECT
  UniqueProductIds.product_id,
  IFNULL(LastChangedPrice.new_price, 10) AS price
FROM
  (
    SELECT DISTINCT
      product_id
    FROM
      Products
  ) AS UniqueProductIds
LEFT JOIN (
    SELECT
      Products.product_id,
      new_price
    FROM
      Products
      JOIN (
        SELECT
          product_id,
          MAX(change_date) AS change_date
        FROM
          Products
        WHERE
          change_date <= '2019-08-16'
        GROUP BY
          product_id
      ) AS LastChangedDate
      USING (product_id, change_date)
    GROUP BY
      product_id
) AS LastChangedPrice
USING (product_id);
```

---

## Step-by-Step Explanation of Approach 2

## Part A: Get all unique products

```sql
SELECT DISTINCT product_id
FROM Products
```

This ensures every product appears exactly once in the final output.

---

## Part B: Find each product's latest valid change date

```sql
SELECT
  product_id,
  MAX(change_date) AS change_date
FROM Products
WHERE change_date <= '2019-08-16'
GROUP BY product_id
```

This produces the latest valid date for each product that changed on or before the target date.

---

## Part C: Join back to retrieve `new_price`

```sql
SELECT
  Products.product_id,
  new_price
FROM
  Products
  JOIN (
    ...
  ) AS LastChangedDate
  USING (product_id, change_date)
```

This step solves the central problem:

- the grouped query tells us the correct date
- the join retrieves the `new_price` from the exact row with that date

---

## Part D: Left join to all products

```sql
LEFT JOIN (...) AS LastChangedPrice USING (product_id)
```

Now all products are preserved.

Products with no valid earlier change get `NULL` in `LastChangedPrice.new_price`.

---

## Part E: Replace `NULL` with 10

```sql
IFNULL(LastChangedPrice.new_price, 10) AS price
```

This applies the rule that products with no earlier price change still have price 10.

---

# Approach 3: Use a Window Function

## Main Idea

This approach uses a window function to directly identify the latest valid price per product.

Instead of:

- grouping by product
- finding max date
- joining back to get the price

we can use:

```sql
FIRST_VALUE(new_price) OVER (
  PARTITION BY product_id
  ORDER BY change_date DESC
)
```

after filtering to rows on or before the target date.

This works because once the rows are sorted in descending date order within each product, the first row is the latest valid change.

---

## Window Function Refresher

A window function computes a value across a group of rows, but unlike ordinary aggregate functions, it still returns a result for each row.

For example:

- `MAX(...) OVER (...)`
- `SUM(...) OVER (...)`
- `FIRST_VALUE(...) OVER (...)`
- `RANK() OVER (...)`

Here we use a non-aggregate window function:

```sql
FIRST_VALUE
```

This returns the first value in the ordered window frame.

---

## Why `FIRST_VALUE` Works Here

If we first filter:

```sql
WHERE change_date <= '2019-08-16'
```

then only valid price history remains.

Now for each product, sort by:

```sql
ORDER BY change_date DESC
```

So the most recent valid change comes first.

Then:

```sql
FIRST_VALUE(new_price)
```

gives the latest price for that product.

---

## Algorithm for Approach 3

### Step 1

Keep only rows with:

```sql
change_date <= '2019-08-16'
```

### Step 2

For each product, compute the latest valid price using `FIRST_VALUE(new_price)` ordered by descending `change_date`.

### Step 3

Take distinct `(product_id, price)` pairs from that windowed result.

### Step 4

Left join to all product IDs.

### Step 5

Replace missing values with 10.

---

## Implementation — Approach 3

```sql
SELECT
  product_id,
  IFNULL(price, 10) AS price
FROM
  (
    SELECT DISTINCT
      product_id
    FROM
      Products
  ) AS UniqueProducts
LEFT JOIN (
    SELECT DISTINCT
      product_id,
      FIRST_VALUE(new_price) OVER (
        PARTITION BY product_id
        ORDER BY change_date DESC
      ) AS price
    FROM
      Products
    WHERE
      change_date <= '2019-08-16'
) AS LastChangedPrice
USING (product_id);
```

---

## Explanation of Approach 3

### Inner window query

```sql
SELECT DISTINCT
  product_id,
  FIRST_VALUE(new_price) OVER (
    PARTITION BY product_id
    ORDER BY change_date DESC
  ) AS price
FROM Products
WHERE change_date <= '2019-08-16'
```

For each product:

- rows are partitioned by `product_id`
- sorted newest to oldest
- `FIRST_VALUE(new_price)` gives the most recent valid price

Because that value will be repeated for all rows in the same partition, `DISTINCT` is used to reduce it to one row per product.

### Outer `LEFT JOIN`

This ensures products with no valid earlier change still appear.

### `IFNULL`

Assigns 10 where no earlier change exists.

---

# Comparing the Three Approaches

## Approach 1: `UNION ALL`

### Strengths

- clear separation of cases
- often efficient
- avoids duplicate removal overhead by using `UNION ALL`

### Weaknesses

- slightly more manual
- uses two result sets

---

## Approach 2: `LEFT JOIN`

### Strengths

- elegant handling of missing earlier changes via `NULL`
- conceptually clean if you like join-based reasoning
- no union needed

### Weaknesses

- a bit more verbose
- still requires a join-back to fetch the correct price

---

## Approach 3: Window Function

### Strengths

- very expressive
- directly models “latest value by date”
- often easier to reason about once window functions are familiar

### Weaknesses

- requires window function support
- may be less approachable if window functions are new to the reader

---

# Why Approach 1 Is Often Recommended

The original recommendation prefers Approach 1 because:

- it is straightforward
- it is efficient
- `UNION ALL` avoids the overhead of duplicate elimination
- the two cases are naturally disjoint

That is a reasonable recommendation, especially in SQL interview settings where clarity matters.

---

# Worked Example

Given:

| product_id | new_price | change_date |
| ---------: | --------: | ----------- |
|          1 |        20 | 2019-08-14  |
|          2 |        50 | 2019-08-14  |
|          1 |        30 | 2019-08-15  |
|          1 |        35 | 2019-08-16  |
|          2 |        65 | 2019-08-17  |
|          3 |        20 | 2019-08-18  |

We want prices on `2019-08-16`.

## Product 1

Valid rows up to target date:

- 20 on 2019-08-14
- 30 on 2019-08-15
- 35 on 2019-08-16

Latest valid price = `35`

## Product 2

Valid rows up to target date:

- 50 on 2019-08-14

The 65-price change is on 2019-08-17, which is too late.

Latest valid price = `50`

## Product 3

Only change is on 2019-08-18, which is after target date.

So it still has initial price = `10`

Final answer:

| product_id | price |
| ---------: | ----: |
|          1 |    35 |
|          2 |    50 |
|          3 |    10 |

---

# Final Code Examples

## Approach 1 — `UNION ALL`

```sql
SELECT
  product_id,
  10 AS price
FROM
  Products
GROUP BY
  product_id
HAVING
  MIN(change_date) > '2019-08-16'

UNION ALL

SELECT
  product_id,
  new_price AS price
FROM
  Products
WHERE
  (product_id, change_date) IN (
    SELECT
      product_id,
      MAX(change_date)
    FROM
      Products
    WHERE
      change_date <= '2019-08-16'
    GROUP BY
      product_id
  );
```

---

## Approach 2 — `LEFT JOIN`

```sql
SELECT
  UniqueProductIds.product_id,
  IFNULL(LastChangedPrice.new_price, 10) AS price
FROM
  (
    SELECT DISTINCT
      product_id
    FROM
      Products
  ) AS UniqueProductIds
LEFT JOIN (
    SELECT
      Products.product_id,
      new_price
    FROM
      Products
      JOIN (
        SELECT
          product_id,
          MAX(change_date) AS change_date
        FROM
          Products
        WHERE
          change_date <= '2019-08-16'
        GROUP BY
          product_id
      ) AS LastChangedDate
      USING (product_id, change_date)
    GROUP BY
      product_id
) AS LastChangedPrice
USING (product_id);
```

---

## Approach 3 — Window Function

```sql
SELECT
  product_id,
  IFNULL(price, 10) AS price
FROM
  (
    SELECT DISTINCT
      product_id
    FROM
      Products
  ) AS UniqueProducts
LEFT JOIN (
    SELECT DISTINCT
      product_id,
      FIRST_VALUE(new_price) OVER (
        PARTITION BY product_id
        ORDER BY change_date DESC
      ) AS price
    FROM
      Products
    WHERE
      change_date <= '2019-08-16'
) AS LastChangedPrice
USING (product_id);
```

---

# Practical Notes

## 1. Why `Items`-style extra tables are absent here

This problem only depends on the `Products` table. There is no need for any external table.

## 2. Why the initial price matters

A product with no earlier record is not “missing”; its price is explicitly defined as `10`.

## 3. Why the target date comparison is inclusive

The problem asks for the price **on** `2019-08-16`, so changes made exactly on that date must count.

That is why every valid filter is:

```sql
change_date <= '2019-08-16'
```

not:

```sql
change_date < '2019-08-16'
```

---

# Complexity Discussion

Let `n` be the number of rows in `Products`.

## Approach 1

- grouping by `product_id`
- subquery with `MAX(change_date)`
- matching rows via tuple comparison

Typically around grouping and filtering cost, depending on indexing.

## Approach 2

- distinct product list
- grouped max-date subquery
- join-back to fetch price
- left join for defaults

Potentially more join work than Approach 1.

## Approach 3

- filter valid rows
- window function partitioned by `product_id`
- distinct product list
- left join for defaults

Often elegant, but performance depends on DB engine support for window functions.

In practice, all three are acceptable; Approach 1 is often favored for simplicity and solid performance.

---

# Key Takeaways

- The real challenge is retrieving the `new_price` corresponding to the latest valid `change_date`
- Products with no change by the target date must return the default price `10`
- `UNION ALL` works well when the cases are disjoint
- `LEFT JOIN` handles missing earlier prices cleanly via `NULL`
- Window functions offer a concise way to express “latest value per product”

---
