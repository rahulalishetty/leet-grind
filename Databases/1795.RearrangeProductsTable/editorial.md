# 1795. Rearrange Products Table — Approach

## Approach: Union Tables

### Intuition

The goal is to transform the table structure.

Original table (wide format):

| product_id | store1 | store2 | store3 |
| ---------- | ------ | ------ | ------ |
| 0          | 95     | 100    | 105    |
| 1          | 70     | NULL   | 80     |

Desired table (long format):

| product_id | store  | price |
| ---------- | ------ | ----- |
| 0          | store1 | 95    |
| 0          | store2 | 100   |
| 0          | store3 | 105   |
| 1          | store1 | 70    |
| 1          | store3 | 80    |

This transformation is called **unpivoting**.

To achieve this, we treat each store column as a separate query and **stack them vertically** using the `UNION` operator.

---

# Algorithm

1. Extract data for **store1**:
   - Use `'store1'` as the store name.
   - Use `store1` column as price.
   - Exclude rows where `store1` is `NULL`.

2. Extract data for **store2**:
   - Use `'store2'` as store name.
   - Use `store2` column as price.
   - Exclude rows where `store2` is `NULL`.

3. Extract data for **store3**:
   - Use `'store3'` as store name.
   - Use `store3` column as price.
   - Exclude rows where `store3` is `NULL`.

4. Combine all results using **UNION**.

---

# Example Query (store1)

```sql
SELECT product_id, 'store1' AS store, store1 AS price
FROM Products
WHERE store1 IS NOT NULL
```

Result:

| product_id | store  | price |
| ---------- | ------ | ----- |
| 0          | store1 | 95    |
| 1          | store1 | 70    |

---

# Final SQL Implementation

```sql
SELECT product_id, 'store1' AS store, store1 AS price
FROM Products
WHERE store1 IS NOT NULL

UNION

SELECT product_id, 'store2' AS store, store2 AS price
FROM Products
WHERE store2 IS NOT NULL

UNION

SELECT product_id, 'store3' AS store, store3 AS price
FROM Products
WHERE store3 IS NOT NULL;
```

---

# Key SQL Concepts Used

- **UNION** → combine multiple result sets
- **Column aliasing (`AS`)** → rename columns
- **Filtering (`WHERE ... IS NOT NULL`)** → remove unavailable products
- **Unpivoting technique** → convert columns into rows
