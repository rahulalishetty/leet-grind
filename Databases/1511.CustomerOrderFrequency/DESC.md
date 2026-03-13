# 1511. Customer Order Frequency

## Table: Customers

| Column Name | Type    |
| ----------- | ------- |
| customer_id | int     |
| name        | varchar |
| country     | varchar |

Notes:

- `customer_id` has unique values.
- Contains information about customers.

---

## Table: Product

| Column Name | Type    |
| ----------- | ------- |
| product_id  | int     |
| description | varchar |
| price       | int     |

Notes:

- `product_id` has unique values.
- `price` represents the cost of the product.

---

## Table: Orders

| Column Name | Type |
| ----------- | ---- |
| order_id    | int  |
| customer_id | int  |
| product_id  | int  |
| order_date  | date |
| quantity    | int  |

Notes:

- `order_id` has unique values.
- Records customer purchases.
- `quantity` indicates how many products were purchased.
- `order_date` format: `YYYY-MM-DD`.

---

# Problem

Write a SQL query to report the **customer_id** and **customer name** of customers who have spent **at least $100 in both June and July 2020**.

Spending is calculated as:

```
price × quantity
```

Requirements:

- Calculate spending per customer per month.
- A customer must satisfy:
  - **June 2020 spending ≥ $100**
  - **July 2020 spending ≥ $100**
- Return the result in **any order**.

---

# Example

## Input

### Customers

| customer_id | name     | country |
| ----------- | -------- | ------- |
| 1           | Winston  | USA     |
| 2           | Jonathan | Peru    |
| 3           | Moustafa | Egypt   |

### Product

| product_id | description | price |
| ---------- | ----------- | ----- |
| 10         | LC Phone    | 300   |
| 20         | LC T-Shirt  | 10    |
| 30         | LC Book     | 45    |
| 40         | LC Keychain | 2     |

### Orders

| order_id | customer_id | product_id | order_date | quantity |
| -------- | ----------- | ---------- | ---------- | -------- |
| 1        | 1           | 10         | 2020-06-10 | 1        |
| 2        | 1           | 20         | 2020-07-01 | 1        |
| 3        | 1           | 30         | 2020-07-08 | 2        |
| 4        | 2           | 10         | 2020-06-15 | 2        |
| 5        | 2           | 40         | 2020-07-01 | 10       |
| 6        | 3           | 20         | 2020-06-24 | 2        |
| 7        | 3           | 30         | 2020-06-25 | 2        |
| 9        | 3           | 30         | 2020-05-08 | 3        |

---

# Output

| customer_id | name    |
| ----------- | ------- |
| 1           | Winston |

---

# Explanation

### Winston

June:

```
300 × 1 = 300
```

July:

```
10 × 1 + 45 × 2 = 100
```

Both months ≥ 100 → included.

---

### Jonathan

June:

```
300 × 2 = 600
```

July:

```
2 × 10 = 20
```

July spending < 100 → excluded.

---

### Moustafa

June:

```
10 × 2 + 45 × 2 = 110
```

July:

```
0
```

July spending < 100 → excluded.
