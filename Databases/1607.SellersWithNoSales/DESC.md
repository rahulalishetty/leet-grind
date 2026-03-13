# 1607. Sellers With No Sales

## Table: Customer

| Column Name   | Type    |
| ------------- | ------- |
| customer_id   | int     |
| customer_name | varchar |

Notes:

- `customer_id` contains **unique values**.
- Each row represents a **customer in the WebStore**.

---

## Table: Orders

| Column Name | Type |
| ----------- | ---- |
| order_id    | int  |
| sale_date   | date |
| order_cost  | int  |
| customer_id | int  |
| seller_id   | int  |

Notes:

- `order_id` contains **unique values**.
- Each row represents an **order placed in the WebStore**.
- `sale_date` indicates when the transaction occurred between a **customer** and a **seller**.

---

## Table: Seller

| Column Name | Type    |
| ----------- | ------- |
| seller_id   | int     |
| seller_name | varchar |

Notes:

- `seller_id` contains **unique values**.
- Each row represents a **seller in the WebStore**.

---

# Problem

Write a SQL query to report the **names of all sellers who did not make any sales in the year 2020**.

Return the result table **ordered by `seller_name` in ascending order**.

---

# Example

## Input

### Customer

| customer_id | customer_name |
| ----------- | ------------- |
| 101         | Alice         |
| 102         | Bob           |
| 103         | Charlie       |

### Orders

| order_id | sale_date  | order_cost | customer_id | seller_id |
| -------- | ---------- | ---------- | ----------- | --------- |
| 1        | 2020-03-01 | 1500       | 101         | 1         |
| 2        | 2020-05-25 | 2400       | 102         | 2         |
| 3        | 2019-05-25 | 800        | 101         | 3         |
| 4        | 2020-09-13 | 1000       | 103         | 2         |
| 5        | 2019-02-11 | 700        | 101         | 2         |

### Seller

| seller_id | seller_name |
| --------- | ----------- |
| 1         | Daniel      |
| 2         | Elizabeth   |
| 3         | Frank       |

---

# Output

| seller_name |
| ----------- |
| Frank       |

---

# Explanation

- **Daniel** made **1 sale in 2020**.
- **Elizabeth** made **2 sales in 2020** and **1 sale in 2019**.
- **Frank** made **1 sale in 2019** but **no sales in 2020**.

Therefore, **Frank** is the only seller who had **no sales in 2020**.
