# 1777. Product's Price for Each Store

## Table: Products

| Column Name | Type |
| ----------- | ---- |
| product_id  | int  |
| store       | enum |
| price       | int  |

### Notes

- `(product_id, store)` is the **primary key**.
- `store` is an ENUM with values:
  - `store1`
  - `store2`
  - `store3`
- `price` represents the **price of the product in that store**.

---

# Problem

Find the **price of each product in each store**.

The output should display:

- `product_id`
- price in `store1`
- price in `store2`
- price in `store3`

If a product is **not sold in a store**, the value should be **NULL**.

Return the result table **in any order**.

---

# Example

## Input

### Products Table

| product_id | store  | price |
| ---------- | ------ | ----- |
| 0          | store1 | 95    |
| 0          | store3 | 105   |
| 0          | store2 | 100   |
| 1          | store1 | 70    |
| 1          | store3 | 80    |

---

## Output

| product_id | store1 | store2 | store3 |
| ---------- | ------ | ------ | ------ |
| 0          | 95     | 100    | 105    |
| 1          | 70     | NULL   | 80     |

---

# Explanation

**Product 0**

- store1 → 95
- store2 → 100
- store3 → 105

**Product 1**

- store1 → 70
- store2 → not available → NULL
- store3 → 80
