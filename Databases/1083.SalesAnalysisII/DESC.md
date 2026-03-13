# 1083. Sales Analysis II

## Tables

### Product

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |
| unit_price   | int     |

- `product_id` is the **primary key** (unique values).
- Each row indicates the **name and price** of a product.

### Sales

| Column Name | Type |
| ----------- | ---- |
| seller_id   | int  |
| product_id  | int  |
| buyer_id    | int  |
| sale_date   | date |
| quantity    | int  |
| price       | int  |

Notes:

- The table **may contain duplicate rows**.
- `product_id` is a **foreign key** referencing `Product(product_id)`.
- `buyer_id` is **never NULL**.
- `sale_date` is **never NULL**.
- Each row represents **one sale transaction**.

---

## Problem

Write a SQL query to report the **buyers who have bought `S8` but have NOT bought `iPhone`**.

Both **S8** and **iPhone** are products listed in the **Product** table.

Return the result table in **any order**.

---

## Example

### Input

#### Product table

| product_id | product_name | unit_price |
| ---------- | ------------ | ---------- |
| 1          | S8           | 1000       |
| 2          | G4           | 800        |
| 3          | iPhone       | 1400       |

#### Sales table

| seller_id | product_id | buyer_id | sale_date  | quantity | price |
| --------- | ---------- | -------- | ---------- | -------- | ----- |
| 1         | 1          | 1        | 2019-01-21 | 2        | 2000  |
| 1         | 2          | 2        | 2019-02-17 | 1        | 800   |
| 2         | 1          | 3        | 2019-06-02 | 1        | 800   |
| 3         | 3          | 3        | 2019-05-13 | 2        | 2800  |

---

### Output

| buyer_id |
| -------- |
| 1        |

### Explanation

- Buyer **1** purchased **S8** but did **not purchase iPhone**.
- Buyer **3** purchased **both S8 and iPhone**, so they are **excluded**.
