# 1084. Sales Analysis III

## Tables

### Product

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |
| unit_price   | int     |

- `product_id` is the **primary key** (unique values).
- Each row represents a product with its **name and price**.

---

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

- The table **can contain duplicate rows**.
- `product_id` is a **foreign key** referencing `Product(product_id)`.
- Each row represents **one sale transaction**.

---

# Problem

Write a SQL query to report the **products that were only sold in the first quarter of 2019**.

The first quarter of 2019 is defined as the date range:

```
2019-01-01 → 2019-03-31
```

inclusive.

A product should be included **only if all its sales occurred within this period**.

Return the result table **in any order**.

---

# Example

## Input

### Product table

| product_id | product_name | unit_price |
| ---------- | ------------ | ---------- |
| 1          | S8           | 1000       |
| 2          | G4           | 800        |
| 3          | iPhone       | 1400       |

### Sales table

| seller_id | product_id | buyer_id | sale_date  | quantity | price |
| --------- | ---------- | -------- | ---------- | -------- | ----- |
| 1         | 1          | 1        | 2019-01-21 | 2        | 2000  |
| 1         | 2          | 2        | 2019-02-17 | 1        | 800   |
| 2         | 2          | 3        | 2019-06-02 | 1        | 800   |
| 3         | 3          | 4        | 2019-05-13 | 2        | 2800  |

---

# Output

| product_id | product_name |
| ---------- | ------------ |
| 1          | S8           |

---

# Explanation

- **Product 1 (S8)**
  - Sold on **2019-01-21**
  - This date lies within **Q1 2019**, and there are **no sales outside Q1**, so it qualifies.

- **Product 2 (G4)**
  - Sold on **2019-02-17** (inside Q1)
  - Sold again on **2019-06-02** (outside Q1)
  - Because it has a sale outside the required range, it **does not qualify**.

- **Product 3 (iPhone)**
  - Sold only on **2019-05-13**
  - This is **outside Q1**, so it **does not qualify**.

Therefore, the result contains only **product 1**.
