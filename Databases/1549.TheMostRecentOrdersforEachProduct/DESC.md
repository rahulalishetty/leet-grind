# 1549. The Most Recent Orders for Each Product

## Table: Customers

| Column Name | Type    |
| ----------- | ------- |
| customer_id | int     |
| name        | varchar |

**Notes:**

- `customer_id` contains **unique values**.
- This table contains information about the **customers**.

---

## Table: Orders

| Column Name | Type |
| ----------- | ---- |
| order_id    | int  |
| order_date  | date |
| customer_id | int  |
| product_id  | int  |

**Notes:**

- `order_id` contains **unique values**.
- Each row represents an order placed by a customer.
- `customer_id` refers to the customer who made the order.
- `product_id` refers to the product that was ordered.
- It is guaranteed that the **same user will not order the same product more than once on the same day**.

---

## Table: Products

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |
| price        | int     |

**Notes:**

- `product_id` contains **unique values**.
- This table stores information about products.

---

# Problem

Write a SQL query to **find the most recent order(s) for each product**.

Important rules:

- If multiple orders exist on the **same most recent date**, all of them must be returned.
- Products that **have never been ordered** should **not appear** in the result.

The result must be ordered by:

1. `product_name` ascending
2. `product_id` ascending
3. `order_id` ascending

---

# Example

## Input

### Customers Table

| customer_id | name      |
| ----------- | --------- |
| 1           | Winston   |
| 2           | Jonathan  |
| 3           | Annabelle |
| 4           | Marwan    |
| 5           | Khaled    |

### Orders Table

| order_id | order_date | customer_id | product_id |
| -------- | ---------- | ----------- | ---------- |
| 1        | 2020-07-31 | 1           | 1          |
| 2        | 2020-07-30 | 2           | 2          |
| 3        | 2020-08-29 | 3           | 3          |
| 4        | 2020-07-29 | 4           | 1          |
| 5        | 2020-06-10 | 1           | 2          |
| 6        | 2020-08-01 | 2           | 1          |
| 7        | 2020-08-01 | 3           | 1          |
| 8        | 2020-08-03 | 1           | 2          |
| 9        | 2020-08-07 | 2           | 3          |
| 10       | 2020-07-15 | 1           | 2          |

### Products Table

| product_id | product_name | price |
| ---------- | ------------ | ----- |
| 1          | keyboard     | 120   |
| 2          | mouse        | 80    |
| 3          | screen       | 600   |
| 4          | hard disk    | 450   |

---

# Output

| product_name | product_id | order_id | order_date |
| ------------ | ---------- | -------- | ---------- |
| keyboard     | 1          | 6        | 2020-08-01 |
| keyboard     | 1          | 7        | 2020-08-01 |
| mouse        | 2          | 8        | 2020-08-03 |
| screen       | 3          | 3        | 2020-08-29 |

---

# Explanation

### Keyboard (product_id = 1)

Orders:

| order_id | order_date |
| -------- | ---------- |
| 1        | 2020-07-31 |
| 4        | 2020-07-29 |
| 6        | 2020-08-01 |
| 7        | 2020-08-01 |

The **latest order date** is **2020-08-01**.

Two orders occurred on this date:

- order_id **6**
- order_id **7**

Both are included in the result.

---

### Mouse (product_id = 2)

Orders:

| order_id | order_date |
| -------- | ---------- |
| 2        | 2020-07-30 |
| 5        | 2020-06-10 |
| 8        | 2020-08-03 |
| 10       | 2020-07-15 |

The **most recent order date** is **2020-08-03**.

Only **order_id 8** occurred on that date.

---

### Screen (product_id = 3)

Orders:

| order_id | order_date |
| -------- | ---------- |
| 3        | 2020-08-29 |
| 9        | 2020-08-07 |

The **most recent order date** is **2020-08-29**.

So the result contains **order_id 3**.

---

### Hard Disk (product_id = 4)

There are **no orders** for this product.

Therefore, it does **not appear** in the result table.
