# 1164. Product Price at a Given Date

## Table: Products

| Column Name | Type |
| ----------- | ---- |
| product_id  | int  |
| new_price   | int  |
| change_date | date |

Notes:

- `(product_id, change_date)` is the **primary key**.
- Each row indicates that the **price of a product changed** to `new_price` on `change_date`.
- Initially, **all products start with a price of 10**.

---

# Problem

Write a SQL query to **find the price of every product on the date `2019-08-16`.**

Important rules:

- If a product had a price change **on or before** `2019-08-16`, the latest change before that date determines the price.
- If a product **never had a price change before or on that date**, its price remains **10**.
- The result can be returned **in any order**.

---

# Example

## Input

### Products Table

| product_id | new_price | change_date |
| ---------- | --------- | ----------- |
| 1          | 20        | 2019-08-14  |
| 2          | 50        | 2019-08-14  |
| 1          | 30        | 2019-08-15  |
| 1          | 35        | 2019-08-16  |
| 2          | 65        | 2019-08-17  |
| 3          | 20        | 2019-08-18  |

---

# Output

| product_id | price |
| ---------- | ----- |
| 2          | 50    |
| 1          | 35    |
| 3          | 10    |

---

# Explanation

### Product 1

Price changes:

| change_date | price |
| ----------- | ----- |
| 2019-08-14  | 20    |
| 2019-08-15  | 30    |
| 2019-08-16  | 35    |

Latest price **on or before 2019-08-16** → **35**

---

### Product 2

Price changes:

| change_date | price |
| ----------- | ----- |
| 2019-08-14  | 50    |
| 2019-08-17  | 65    |

Latest price **before 2019-08-16** → **50**

---

### Product 3

Price changes:

| change_date | price |
| ----------- | ----- |
| 2019-08-18  | 20    |

Since the first change occurs **after 2019-08-16**, the product keeps its **initial price = 10**.

---
