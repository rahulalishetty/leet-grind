# 1484. Group Sold Products By The Date — Approach

## Approach: Grouping and Aggregation of Strings

### Algorithm

To solve this problem, we perform grouping and aggregation on the **Activities** table.

---

# Step 1 — Group by Date

We group the rows by the `sell_date` column.

```
GROUP BY sell_date
```

This ensures that each row in the result corresponds to **one date**.

---

# Step 2 — Count Unique Products

To calculate the number of different products sold on each date, we use:

```
COUNT(DISTINCT product)
```

This counts the **unique products** sold on that date.

Example:

| sell_date  | product |
| ---------- | ------- |
| 2020‑06‑02 | Mask    |
| 2020‑06‑02 | Mask    |

The result becomes:

```
num_sold = 1
```

---

# Step 3 — Concatenate Product Names

The challenging part is generating the **comma-separated list of product names**.

For this we use the **GROUP_CONCAT()** function.

### GROUP_CONCAT Syntax

```
GROUP_CONCAT(
    DISTINCT expression
    ORDER BY expression
    SEPARATOR separator
)
```

Explanation:

- **DISTINCT** → removes duplicate product names
- **ORDER BY** → sorts product names lexicographically
- **SEPARATOR ','** → joins values using commas

Example:

```
GROUP_CONCAT(
    DISTINCT product
    ORDER BY product
    SEPARATOR ','
)
```

Output example:

```
Basketball,Headphone,T-shirt
```

---

# Step 4 — Sort the Result

The final result must be sorted by **sell_date in ascending order**.

```
ORDER BY sell_date ASC
```

---

# Final SQL Query

```sql
SELECT
    sell_date,
    COUNT(DISTINCT product) AS num_sold,
    GROUP_CONCAT(DISTINCT product ORDER BY product SEPARATOR ',') AS products
FROM Activities
GROUP BY sell_date
ORDER BY sell_date ASC;
```

---

# Key Concepts

- **GROUP BY** groups rows by sell date.
- **COUNT(DISTINCT)** counts unique products.
- **GROUP_CONCAT()** aggregates strings from multiple rows.
- **ORDER BY inside GROUP_CONCAT** ensures lexicographical sorting.
