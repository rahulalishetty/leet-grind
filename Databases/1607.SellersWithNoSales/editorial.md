# 1607. Sellers With No Sales — Approaches

## Overview

This is a typical **"NOT IN" problem** (also known as a **left anti-join** in SQL).
The goal is to retrieve all records from **Table A (Seller)** that **do not appear in Table B (Orders for 2020)**.

We will look at three approaches, starting from the simplest.

---

# Approach 1: NOT IN / NOT EXISTS Subquery

## Intuition

The simplest approach is:

1. Identify sellers who **did make sales in 2020** (unwanted group).
2. Select all sellers **excluding** this group.

---

## Algorithm

1. In a **subquery**, find sellers who had sales in **2020**.
2. In the **main query**, select all sellers **NOT IN** that result.
3. Order the results by `seller_name`.

---

## Step 1 — Subquery

```sql
SELECT DISTINCT seller_id
FROM Orders
WHERE YEAR(sale_date) = 2020;
```

---

## Step 2 — Main Query

```sql
SELECT seller_name
FROM Seller s
WHERE s.seller_id NOT IN (
    SELECT DISTINCT seller_id
    FROM Orders
    WHERE YEAR(sale_date) = 2020
)
ORDER BY 1 ASC;
```

---

# Approach 2: LEFT JOIN + NULL Filter

## Intuition

Another technique is to use **LEFT JOIN**.

- Join all sellers with sellers who had sales in 2020.
- Sellers **without matches** will produce **NULL values**.
- Filter these rows.

---

## Algorithm

1. Create a subquery containing sellers who had **sales in 2020**.
2. LEFT JOIN it with the `Seller` table.
3. Keep rows where the joined seller_id is **NULL**.

---

## Implementation

```sql
SELECT seller_name
FROM Seller a
LEFT JOIN (
    SELECT DISTINCT seller_id
    FROM Orders
    WHERE YEAR(sale_date) = 2020
) b
ON a.seller_id = b.seller_id
WHERE b.seller_id IS NULL
ORDER BY 1 ASC;
```

---

# Approach 3: Flag Records Using HAVING or CASE

## Intuition

Another method is to **flag sellers who made sales in 2020**.

Then remove sellers with a positive flag.

---

# Method A — Using HAVING

## Algorithm

1. LEFT JOIN `Seller` and `Orders`.
2. Group results by `seller_id`.
3. Count how many 2020 sales each seller had.
4. Keep sellers whose count is **0**.

---

## Implementation

```sql
SELECT seller_name
FROM Seller s
LEFT JOIN Orders o
ON s.seller_id = o.seller_id
GROUP BY s.seller_id
HAVING SUM(IFNULL(YEAR(sale_date)='2020',0)) = 0
ORDER BY 1 ASC;
```

---

# Method B — Using CASE WHEN

## Algorithm

1. LEFT JOIN sellers with orders.
2. Create a **flag column** counting 2020 sales.
3. Filter sellers where `flag = 0`.

---

## Step 1–2: Create Flag

```sql
SELECT seller_name,
       SUM(CASE WHEN YEAR(sale_date)='2020' THEN 1 ELSE 0 END) AS flag
FROM Seller s
LEFT JOIN Orders o
ON s.seller_id = o.seller_id
GROUP BY s.seller_id;
```

---

## Step 3–4: Filter Flag

```sql
SELECT seller_name
FROM (
    SELECT seller_name,
           SUM(CASE WHEN YEAR(sale_date)='2020' THEN 1 ELSE 0 END) AS flag
    FROM Seller s
    LEFT JOIN Orders o
    ON s.seller_id = o.seller_id
    GROUP BY s.seller_id
) t0
WHERE flag = 0
ORDER BY 1 ASC;
```

---

# Conclusion

- **Approach 1 (NOT IN)** → simplest and most readable.
- **Approach 2 (LEFT JOIN + NULL)** → common SQL anti-join technique.
- **Approach 3 (HAVING / CASE)** → more flexible for complex conditions.

For interviews and real-world queries, **Approach 1 or 2 are typically preferred**.
