# 1070. Product Sales Analysis III

## Approach: Filtering from Minimum Value Subquery

## Core idea

We need to return all sales rows that happened in the **first year** each product was sold.

That means the problem has two parts:

1. for each `product_id`, find the **minimum year**
2. return all rows from `Sales` whose `(product_id, year)` matches that minimum-year pair

This approach does exactly that using:

- a grouped subquery to compute the first year per product
- a main query that filters rows using a tuple match

It is short, direct, and easy to reason about.

---

## Why the problem is slightly subtle

A product may have:

- multiple sales rows overall
- multiple sales rows in the same year

So we do **not** want just one row per product.

We want:

> all rows for that product in its earliest year

That is why we cannot simply group and return aggregate values directly from the grouped query.
We first identify the earliest year, then go back to the original table and return the matching rows.

---

# Step 1: Find the first year for each product

The inner subquery is:

```sql
SELECT
  product_id,
  MIN(year) AS year
FROM
  Sales
GROUP BY
  product_id
```

---

## Why this works

### `GROUP BY product_id`

This groups the sales rows product by product.

### `MIN(year)`

Within each product group, this picks the earliest year in which the product appears.

So the result is one row per product:

| product_id | year |
| ---------- | ---- |
| ...        | ...  |

where `year` is the first year that product was sold.

---

## Example on the sample

### Sales table

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2009 |       12 |  5000 |
|       7 |        200 | 2011 |       15 |  9000 |

Now group by `product_id` and take minimum year:

### Product 100

Years:

```text
2008, 2009
```

Minimum year:

```text
2008
```

### Product 200

Years:

```text
2011
```

Minimum year:

```text
2011
```

So the subquery returns:

| product_id | year |
| ---------- | ---- |
| 100        | 2008 |
| 200        | 2011 |

These are the `(product_id, first_year)` pairs we need.

---

# Step 2: Filter the Sales table using those pairs

The outer query is:

```sql
SELECT
  product_id,
  year AS first_year,
  quantity,
  price
FROM
  Sales
WHERE
  (product_id, year) IN (
    SELECT
      product_id,
      MIN(year) AS year
    FROM
      Sales
    GROUP BY
      product_id
  );
```

---

## Why this works

The condition:

```sql
(product_id, year) IN (subquery)
```

is a tuple comparison.

It checks whether the current row’s:

```text
(product_id, year)
```

matches one of the `(product_id, first_year)` pairs produced by the subquery.

So only sales rows belonging to the earliest year of each product are kept.

---

## Why this correctly returns all rows from the first year

This is important.

Suppose a product had two different sales rows in its first year:

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2008 |       12 |  4800 |
|       3 |        100 | 2009 |        8 |  4700 |

The subquery would still return:

```text
(100, 2008)
```

Then the outer query would return **both** rows from 2008, because both satisfy:

```text
(product_id, year) = (100, 2008)
```

That is exactly what the problem asks for.

---

# Final accepted query

```sql
SELECT
  product_id,
  year AS first_year,
  quantity,
  price
FROM
  Sales
WHERE
  (product_id, year) IN (
    SELECT
      product_id,
      MIN(year) AS year
    FROM
      Sales
    GROUP BY
      product_id
  );
```

---

# Step-by-step explanation of the query

## Outer `SELECT`

```sql
SELECT
  product_id,
  year AS first_year,
  quantity,
  price
FROM
  Sales
```

This chooses the required output columns.

The column `year` is renamed to `first_year` because that is the name required by the problem.

---

## `WHERE (product_id, year) IN (...)`

This is the filtering step.

It keeps only those rows whose `(product_id, year)` pair is one of the earliest-year pairs for that product.

---

## Inner subquery

```sql
SELECT
  product_id,
  MIN(year) AS year
FROM
  Sales
GROUP BY
  product_id
```

This computes the first year for each product.

---

# Walkthrough on the sample

## Input

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2009 |       12 |  5000 |
|       7 |        200 | 2011 |       15 |  9000 |

---

## Subquery result

| product_id | year |
| ---------- | ---- |
| 100        | 2008 |
| 200        | 2011 |

---

## Apply outer filter

Check each original row:

### Row 1

```text
(product_id, year) = (100, 2008)
```

This is in the subquery result -> keep it.

### Row 2

```text
(product_id, year) = (100, 2009)
```

This is not in the subquery result -> discard it.

### Row 7

```text
(product_id, year) = (200, 2011)
```

This is in the subquery result -> keep it.

---

## Final output

| product_id | first_year | quantity | price |
| ---------- | ---------- | -------- | ----- |
| 100        | 2008       | 10       | 5000  |
| 200        | 2011       | 15       | 9000  |

That matches the expected result.

---

# Why this approach is elegant

This approach is elegant because it separates the problem into:

- identifying the qualifying `(product_id, year)` pairs
- retrieving the matching detailed rows

That avoids unnecessary complexity.

You do not need:

- joins
- window functions
- recursive logic
- extra grouping in the outer query

A simple grouped subquery plus tuple filtering is enough.

---

# Important SQL concepts used here

## 1. `MIN(year)`

Used to identify the first year a product appears.

## 2. `GROUP BY product_id`

Used to compute that first year separately for each product.

## 3. Tuple comparison with `IN`

```sql
(product_id, year) IN (...)
```

Used to filter rows based on a pair of values, not just one value.

## 4. Column alias

```sql
year AS first_year
```

Used to match the required output column name.

---

# Why tuple matching is important here

A common mistake would be to filter only by:

```sql
year IN (SELECT MIN(year) ...)
```

That would be wrong.

Why?

Because two different products can have different first years, and we need to match the first year **for the correct product**.

So we must match the pair:

```text
(product_id, year)
```

not just `year` alone.

That is why the tuple-based `IN` condition is the right choice.

---

# Complexity

Let `n` be the number of rows in `Sales`.

## Time Complexity

The solution:

- scans and groups the table once in the subquery
- filters rows from the outer query

A practical summary is that it is efficient and much simpler than more elaborate alternatives.

## Space Complexity

Additional space is mainly for the grouped subquery result, which has one row per product.

---

# Key takeaways

1. First find the minimum year for each product.
2. Then return all original sales rows whose `(product_id, year)` matches that first-year pair.
3. A tuple comparison is essential here because first year must be matched per product.
4. This is a clean and direct solution with a grouped subquery.

---

## Final accepted implementation

```sql
SELECT
  product_id,
  year AS first_year,
  quantity,
  price
FROM
  Sales
WHERE
  (product_id, year) IN (
    SELECT
      product_id,
      MIN(year) AS year
    FROM
      Sales
    GROUP BY
      product_id
  );
```
