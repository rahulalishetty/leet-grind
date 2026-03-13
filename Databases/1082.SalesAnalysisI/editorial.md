# 1082. Sales Analysis I — Approach: Max Sales Filtering with Aggregation

## Intuition

The solution identifies the **best seller(s)** by computing the **total sales price per seller**, determining the **maximum sales value**, and returning all sellers whose total equals that maximum.

To keep the query clean and modular, the approach uses a **Common Table Expression (CTE)**.

The strategy consists of three logical stages:

1. **Aggregate** total sales per seller.
2. **Find the maximum total sales value.**
3. **Return sellers whose sales equal that maximum.**

---

# Step 1 — Create an Aggregated Sales Table

First, compute the **total sales price for each seller**.

```sql
WITH aggregated_sales AS (
  SELECT
    seller_id,
    SUM(price) AS total_price
  FROM Sales
  GROUP BY seller_id
)
```

This produces a temporary table (`aggregated_sales`) where each seller appears once with their **total sales value**.

### Example Input (Simplified)

| seller_id | product_id | price |
| --------- | ---------- | ----- |
| 1         | 1          | 2000  |
| 1         | 2          | 800   |
| 2         | 2          | 800   |
| 3         | 3          | 2800  |

### Resulting `aggregated_sales` Table

| seller_id | total_price |
| --------- | ----------- |
| 1         | 2800        |
| 2         | 800         |
| 3         | 2800        |

---

# Step 2 — Identify the Maximum Sales Value

Next, determine the **highest sales value among all sellers**.

```sql
SELECT MAX(total_price)
FROM aggregated_sales
```

This query returns:

```
2800
```

---

# Step 3 — Filter Sellers With Maximum Sales

Now return all sellers whose **total sales equal the maximum value**.

```sql
SELECT seller_id
FROM aggregated_sales
WHERE total_price = (
    SELECT MAX(total_price)
    FROM aggregated_sales
);
```

This ensures that:

- If **multiple sellers tie for the maximum**, they are all returned.
- The query remains scalable and readable.

---

# Final SQL Implementation

```sql
WITH aggregated_sales AS (
  SELECT
    seller_id,
    SUM(price) AS total_price
  FROM Sales
  GROUP BY seller_id
)
SELECT seller_id
FROM aggregated_sales
WHERE total_price = (
    SELECT MAX(total_price)
    FROM aggregated_sales
);
```

---

# Final Output

| seller_id |
| --------- |
| 1         |
| 3         |
