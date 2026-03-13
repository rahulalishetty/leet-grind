# 1068. Product Sales Analysis I — Detailed Summary

## Approach: Inner Join

This approach solves the problem by joining the two related tables:

- `Sales`
- `Product`

The reason a join is needed is simple:

- `Sales` contains the `year` and `price`
- `Product` contains the `product_name`

Since the required output needs columns coming from both tables, we must combine them using their shared key:

```sql
product_id
```

---

## Problem Restatement

We need to report, for each sale:

- `product_name`
- `year`
- `price`

The data is split across two tables:

### `Sales`

Contains:

- `product_id`
- `year`
- `price`

### `Product`

Contains:

- `product_id`
- `product_name`

So the task is essentially:

> match each sale with its product name, then return the requested columns.

---

## Core Idea

The two tables are related through:

```sql
product_id
```

This means:

- each sale row refers to a product using `product_id`
- the `Product` table tells us the corresponding `product_name`

So if we join the two tables on `product_id`, each sale row gets enriched with its product name.

After that, we simply select the needed columns.

---

# Query

```sql
SELECT
    p.product_name,
    s.year,
    s.price
FROM
    Sales s
JOIN
    Product p
ON
    s.product_id = p.product_id;
```

---

# Step-by-Step Explanation

## 1. Start from the `Sales` table

```sql
FROM Sales s
```

This table contains one row per sale.

Each sale already has:

- `product_id`
- `year`
- `price`

But it does **not** have the `product_name`.

So we need another table to get that.

---

## 2. Join with the `Product` table

```sql
JOIN Product p
ON s.product_id = p.product_id
```

This matches each sale row with the corresponding product row.

Because `product_id` is the shared column between the two tables, it acts as the link.

### What this does

If a sale has:

```text
product_id = 100
```

and the `Product` table has:

```text
100 -> Nokia
```

then the join allows us to attach:

```text
product_name = Nokia
```

to that sale row.

---

## 3. Select the required columns

```sql
SELECT
    p.product_name,
    s.year,
    s.price
```

The problem only asks for these three columns.

So:

- `product_name` comes from `Product`
- `year` comes from `Sales`
- `price` comes from `Sales`

That is why the query selects columns from both tables.

---

# Worked Example

## Input

### Sales

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2009 |       12 |  5000 |
|       7 |        200 | 2011 |       15 |  9000 |

### Product

| product_id | product_name |
| ---------: | ------------ |
|        100 | Nokia        |
|        200 | Apple        |
|        300 | Samsung      |

---

# Row-by-Row Matching

## Sale row 1

From `Sales`:

| sale_id | product_id | year | price |
| ------: | ---------: | ---: | ----: |
|       1 |        100 | 2008 |  5000 |

Match with `Product` where:

```sql
product_id = 100
```

From `Product`:

| product_id | product_name |
| ---------: | ------------ |
|        100 | Nokia        |

Combined result:

| product_name | year | price |
| ------------ | ---: | ----: |
| Nokia        | 2008 |  5000 |

---

## Sale row 2

From `Sales`:

| sale_id | product_id | year | price |
| ------: | ---------: | ---: | ----: |
|       2 |        100 | 2009 |  5000 |

Again, `product_id = 100`, so the product name is `Nokia`.

Combined result:

| product_name | year | price |
| ------------ | ---: | ----: |
| Nokia        | 2009 |  5000 |

---

## Sale row 7

From `Sales`:

| sale_id | product_id | year | price |
| ------: | ---------: | ---: | ----: |
|       7 |        200 | 2011 |  9000 |

Match with `Product` where:

```sql
product_id = 200
```

From `Product`:

| product_id | product_name |
| ---------: | ------------ |
|        200 | Apple        |

Combined result:

| product_name | year | price |
| ------------ | ---: | ----: |
| Apple        | 2011 |  9000 |

---

# Final Output

| product_name | year | price |
| ------------ | ---: | ----: |
| Nokia        | 2008 |  5000 |
| Nokia        | 2009 |  5000 |
| Apple        | 2011 |  9000 |

---

# Why Inner Join Is the Correct Choice

An inner join returns only rows where the matching `product_id` exists in both tables.

That is appropriate here because:

- every valid sale should correspond to a valid product
- the problem asks only for actual sales enriched with product names

So an inner join gives exactly the rows we need.

---

# Clause-by-Clause Breakdown

## `SELECT p.product_name, s.year, s.price`

Returns the requested output columns.

---

## `FROM Sales s`

Starts from the sales records.

---

## `JOIN Product p`

Brings in product information.

---

## `ON s.product_id = p.product_id`

Specifies how the two tables are matched.

---

# Why Table Aliases Are Used

The query uses aliases:

- `s` for `Sales`
- `p` for `Product`

This makes the query shorter and clearer.

Instead of writing:

```sql
Sales.year
Product.product_name
```

we write:

```sql
s.year
p.product_name
```

That is especially useful when multiple tables are involved.

---

# Alternative Equivalent Query

The same logic can be written without aliases:

```sql
SELECT
    Product.product_name,
    Sales.year,
    Sales.price
FROM
    Sales
JOIN
    Product
ON
    Sales.product_id = Product.product_id;
```

This is equivalent, but the aliased version is cleaner.

---

# Why No Aggregation Is Needed

This problem does not ask for:

- counts
- sums
- grouped results

It only asks to report columns from related tables.

So no `GROUP BY` or aggregate functions are required.

A simple join is enough.

---

# Complexity Analysis

Let:

- `S` = number of rows in `Sales`
- `P` = number of rows in `Product`

The query performs a join on `product_id`.

Because `Product.product_id` is a primary key, this is a straightforward one-to-many match from product to sales.

This is efficient and standard for relational queries like this.

---

# Final Recommended Query

```sql
SELECT
    p.product_name,
    s.year,
    s.price
FROM
    Sales s
JOIN
    Product p
ON
    s.product_id = p.product_id;
```

---

# Key Takeaways

- The required columns come from two different tables
- `product_id` is the shared key used to connect them
- Use an inner join to combine each sale with its product name
- Select only `product_name`, `year`, and `price`

---
