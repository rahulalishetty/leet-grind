# 1777. Product's Price for Each Store — Approach

## Approach 1: Using MAX with CASE WHEN (Pivoting)

### Intuition

The goal is to transform rows into columns so that each product shows its price in each store.

The table structure is:

| product_id | store  | price |
| ---------- | ------ | ----- |
| 0          | store1 | 95    |
| 0          | store2 | 100   |
| 0          | store3 | 105   |

We want to convert it into:

| product_id | store1 | store2 | store3 |
| ---------- | ------ | ------ | ------ |
| 0          | 95     | 100    | 105    |

This transformation is known as **pivoting**.

---

# Key Idea

We use **CASE WHEN** to create conditional columns for each store.

Example:

```
CASE WHEN store = 'store1' THEN price END
```

This returns the price when the store is `store1`, otherwise NULL.

However, this still produces multiple rows per product.
To consolidate them into one row per product, we use an **aggregate function** such as:

- `MAX()`
- `MIN()`
- `SUM()`

Here `MAX()` works because each `(product_id, store)` pair has only **one price**.

---

# Algorithm

1. Use `CASE WHEN` to create conditional price columns for each store.
2. Use `MAX()` to select the actual price value.
3. Group results by `product_id`.

---

# SQL Implementation

```sql
SELECT
  product_id,
  MAX(CASE WHEN store = 'store1' THEN price END) AS store1,
  MAX(CASE WHEN store = 'store2' THEN price END) AS store2,
  MAX(CASE WHEN store = 'store3' THEN price END) AS store3
FROM Products
GROUP BY product_id;
```

---

# Key SQL Concepts Used

- **CASE WHEN** → conditional column creation
- **MAX() aggregation** → collapse rows into a single value
- **GROUP BY** → ensure one result per product
- **Pivoting technique** → convert rows into columns
