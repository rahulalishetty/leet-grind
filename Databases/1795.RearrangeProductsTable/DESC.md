# 1795. Rearrange Products Table

## Table: Products

| Column Name | Type |
| ----------- | ---- |
| product_id  | int  |
| store1      | int  |
| store2      | int  |
| store3      | int  |

### Notes

- `product_id` is the **primary key**.
- Each row represents the **price of a product in three stores**.
- If a product is **not available** in a store, the value is **NULL**.

---

# Problem

Rearrange the `Products` table so that each row contains:

- `product_id`
- `store`
- `price`

Rules:

- If a product is **not available in a store (NULL)**, **do not include** that row in the result.
- Return the result table **in any order**.

---

# Example

## Input

### Products Table

| product_id | store1 | store2 | store3 |
| ---------- | ------ | ------ | ------ |
| 0          | 95     | 100    | 105    |
| 1          | 70     | NULL   | 80     |

---

## Output

| product_id | store  | price |
| ---------- | ------ | ----- |
| 0          | store1 | 95    |
| 0          | store2 | 100   |
| 0          | store3 | 105   |
| 1          | store1 | 70    |
| 1          | store3 | 80    |

---

# Explanation

**Product 0**

- store1 → 95
- store2 → 100
- store3 → 105

**Product 1**

- store1 → 70
- store2 → not available (NULL → excluded)
- store3 → 80
