# 1069. Product Sales Analysis II

## Table: Sales

| Column Name | Type |
| ----------- | ---- |
| sale_id     | int  |
| product_id  | int  |
| year        | int  |
| quantity    | int  |
| price       | int  |

### Notes

- `(sale_id, year)` is the **primary key** (unique combination).
- `product_id` is a **foreign key** referencing the `Product` table.
- Each row represents a **sale of a product in a given year**.
- `price` represents the **price per unit**.

---

## Table: Product

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |

### Notes

- `product_id` is the **primary key**.
- Each row represents the **name of a product**.

---

# Problem

Write a SQL query to report the **total quantity sold for every product_id**.

The result should contain:

| product_id | total_quantity |

Return the resulting table **in any order**.

---

# Example

## Input

### Sales Table

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2009 |       12 |  5000 |
|       7 |        200 | 2011 |       15 |  9000 |

### Product Table

| product_id | product_name |
| ---------: | ------------ |
|        100 | Nokia        |
|        200 | Apple        |
|        300 | Samsung      |

---

# Explanation

### Product 100

Sales quantities:

```
10 + 12
```

Total quantity:

```
22
```

---

### Product 200

Sales quantities:

```
15
```

Total quantity:

```
15
```

---

### Product 300

There are **no sales records** for this product, so it does not appear in the result.

---

# Output

| product_id | total_quantity |
| ---------- | -------------- |
| 100        | 22             |
| 200        | 15             |

---

# Summary

The task requires:

- grouping sales by `product_id`
- summing the `quantity` column
- returning the total quantity sold per product
