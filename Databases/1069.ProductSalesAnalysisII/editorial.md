# 1069. Product Sales Analysis II — Detailed Summary

## Approach: Get Aggregated Sum by Using `GROUP BY` and `SUM()`

This approach solves the problem by working only with the `Sales` table.

That is enough because the required output needs only:

- `product_id`
- total quantity sold

Both of these are already present in `Sales`.

So the solution is simply:

1. group rows by `product_id`
2. sum the `quantity` values in each group
3. rename that sum as `total_quantity`

---

## Problem Restatement

We need to report, for every product that appears in `Sales`:

- `product_id`
- total quantity sold across all its sales records

The output does **not** require:

- product name
- year
- price

So there is no need to join with the `Product` table.

---

## Core Idea

Each row in `Sales` is one sale record for a product.

If the same product appears in multiple rows, then the total quantity sold for that product is just the sum of the `quantity` values across those rows.

That naturally suggests an aggregation query using:

- `GROUP BY product_id`
- `SUM(quantity)`

---

# Query

```sql
SELECT
    product_id,
    SUM(quantity) AS total_quantity
FROM Sales
GROUP BY product_id;
```

---

# Step-by-Step Explanation

## 1. Read the needed columns from `Sales`

```sql
FROM Sales
```

The `Sales` table already contains:

- `product_id`
- `quantity`

Those are the only columns needed for this task.

So no other table is required.

---

## 2. Group rows by `product_id`

```sql
GROUP BY product_id
```

This gathers together all sales rows that belong to the same product.

For example, if the table has:

| sale_id | product_id | quantity |
| ------: | ---------: | -------: |
|       1 |        100 |       10 |
|       2 |        100 |       12 |
|       7 |        200 |       15 |

then grouping by `product_id` creates groups like:

- product `100` → quantities `10, 12`
- product `200` → quantity `15`

---

## 3. Sum the quantities in each group

```sql
SUM(quantity)
```

This adds up all quantity values inside each product group.

So for:

### Product 100

```text
10 + 12 = 22
```

### Product 200

```text
15
```

That gives the total quantity sold for each product.

---

## 4. Rename the aggregated column

```sql
SUM(quantity) AS total_quantity
```

This gives the output column the required name:

```text
total_quantity
```

---

# Worked Example

## Input

### Sales

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2009 |       12 |  5000 |
|       7 |        200 | 2011 |       15 |  9000 |

---

# Grouped View

After grouping by `product_id`:

## Product 100

Rows:

| product_id | quantity |
| ---------: | -------: |
|        100 |       10 |
|        100 |       12 |

Sum:

```text
22
```

---

## Product 200

Rows:

| product_id | quantity |
| ---------: | -------: |
|        200 |       15 |

Sum:

```text
15
```

---

# Final Output

| product_id | total_quantity |
| ---------: | -------------: |
|        100 |             22 |
|        200 |             15 |

---

# Why Product 300 Does Not Appear

The `Product` table contains product `300`, but there is no matching row for it in `Sales`.

Since this query only uses the `Sales` table, only products that actually have sales records appear in the output.

That is correct, because the problem asks for the total quantity sold, and products with no sales have nothing to aggregate here.

---

# Why the `Product` Table Is Not Needed

This is an important observation.

Even though the schema includes both:

- `Sales`
- `Product`

the problem only asks for:

- `product_id`
- `total_quantity`

The `Product` table would only be needed if the output required:

- `product_name`
  or other product details.

Since it does not, using only `Sales` is the simplest and correct approach.

---

# Clause-by-Clause Breakdown

## `SELECT product_id`

Returns the identifier of the product.

---

## `SUM(quantity) AS total_quantity`

Computes the total quantity sold for that product and renames the result column.

---

## `FROM Sales`

Reads the sales data.

---

## `GROUP BY product_id`

Aggregates rows per product.

---

# Why `SUM()` Is the Correct Aggregate

The problem asks for the **total quantity sold**.

“Total” means we need to add the quantities together.

So `SUM(quantity)` is exactly the correct aggregation.

Other aggregates would not match the requirement:

- `COUNT(quantity)` would count rows, not units sold
- `MAX(quantity)` would return the largest single sale quantity
- `AVG(quantity)` would return the average quantity per sale

Only `SUM(quantity)` gives the total units sold.

---

# Example of Why `COUNT()` Would Be Wrong

Suppose product `100` has:

| quantity |
| -------: |
|       10 |
|       12 |

Then:

```sql
COUNT(quantity) = 2
```

But the problem wants:

```sql
10 + 12 = 22
```

So `COUNT()` is not appropriate here.

---

# Why `GROUP BY` Is Necessary

Without grouping, `SUM(quantity)` would give one total across the entire table.

But the problem asks for totals **for every product_id**.

So we must group by product.

That creates one aggregate result row per product.

---

# Complexity Analysis

Let `n` be the number of rows in `Sales`.

The query performs:

- one scan of the table
- one grouping operation by `product_id`

This is efficient and standard for aggregation problems.

---

# Final Recommended Query

```sql
SELECT
    product_id,
    SUM(quantity) AS total_quantity
FROM Sales
GROUP BY product_id;
```

---

# Key Takeaways

- Only the `Sales` table is needed
- Group by `product_id`
- Sum `quantity` within each group
- Rename the result as `total_quantity`

---
