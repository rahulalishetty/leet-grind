# 1082. Sales Analysis I

## Table: Product

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |
| unit_price   | int     |

- `product_id` is the **primary key** (unique values) for this table.
- Each row represents a product with its **name** and **unit price**.

---

## Table: Sales

| Column Name | Type |
| ----------- | ---- |
| seller_id   | int  |
| product_id  | int  |
| buyer_id    | int  |
| sale_date   | date |
| quantity    | int  |
| price       | int  |

- This table **may contain duplicate rows**.
- `product_id` is a **foreign key** referencing `Product(product_id)`.
- Each row represents a **product sale transaction**.

---

## Problem

Write an SQL query to **report the best seller(s)** based on **total sales price**.

Rules:

- Compute the **total sales price for each seller**.
- Identify the **seller(s) with the maximum total sales price**.
- If multiple sellers have the same highest sales amount, **report all of them**.

The result table can be returned in **any order**.

---

## Example

### Input

### Product Table

| product_id | product_name | unit_price |
| ---------- | ------------ | ---------- |
| 1          | S8           | 1000       |
| 2          | G4           | 800        |
| 3          | iPhone       | 1400       |

### Sales Table

| seller_id | product_id | buyer_id | sale_date  | quantity | price |
| --------- | ---------- | -------- | ---------- | -------- | ----- |
| 1         | 1          | 1        | 2019-01-21 | 2        | 2000  |
| 1         | 2          | 2        | 2019-02-17 | 1        | 800   |
| 2         | 2          | 3        | 2019-06-02 | 1        | 800   |
| 3         | 3          | 4        | 2019-05-13 | 2        | 2800  |

---

### Output

| seller_id |
| --------- |
| 1         |
| 3         |

---

### Explanation

- Seller **1** total sales = `2000 + 800 = 2800`
- Seller **2** total sales = `800`
- Seller **3** total sales = `2800`

The **highest total sales price is 2800**.

Both sellers **1 and 3** reach this value, so **both are returned**.
