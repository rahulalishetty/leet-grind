# 1571. Warehouse Manager — Approach

## Approach: LEFT JOIN and Aggregation

### Algorithm

To calculate the total inventory volume in each warehouse, we follow these steps.

---

# Step 1 — Calculate Product Volume

Each product's volume is calculated using its dimensions:

```
volume = width × length × height
```

We create a **subquery** that computes the cubic feet for every product.

```sql
SELECT
    p.product_id,
    p.width * p.length * p.height AS volume
FROM Products p;
```

This produces a table containing:

| product_id | volume |
| ---------- | ------ |
| 1          | 10000  |
| 2          | 125    |
| 3          | 200    |
| 4          | 800    |

---

# Step 2 — Join with Warehouse Table

Next, we **LEFT JOIN** the `Warehouse` table with the subquery on `product_id`.

This allows us to combine:

- number of units stored
- volume per unit

```sql
SELECT *
FROM Warehouse w
LEFT JOIN (
    SELECT
        p.product_id,
        p.width * p.length * p.height AS cubic_ft
    FROM Products p
) AS sub
ON w.product_id = sub.product_id;
```

Resulting dataset:

| name     | product_id | units | product_id | cubic_ft |
| -------- | ---------- | ----- | ---------- | -------- |
| LCHouse1 | 1          | 1     | 1          | 10000    |
| LCHouse1 | 2          | 10    | 2          | 125      |
| LCHouse1 | 3          | 5     | 3          | 200      |
| LCHouse2 | 1          | 2     | 1          | 10000    |
| LCHouse2 | 2          | 2     | 2          | 125      |
| LCHouse3 | 4          | 1     | 4          | 800      |

---

# Step 3 — Compute Total Volume per Warehouse

Each warehouse contains multiple products.

Total volume contribution of each row:

```
units × cubic_ft
```

To compute the warehouse total, we use:

```
SUM(units × cubic_ft)
```

---

# Step 4 — Group by Warehouse

We group rows by warehouse name so that each warehouse produces one result row.

```
GROUP BY warehouse_name
```

---

# Final SQL Query

```sql
SELECT
    w.name AS warehouse_name,
    SUM(w.units * sub.cubic_ft) AS volume
FROM Warehouse w
LEFT JOIN (
    SELECT
        p.product_id,
        p.width * p.length * p.height AS cubic_ft
    FROM Products p
) AS sub
ON w.product_id = sub.product_id
GROUP BY warehouse_name;
```

---

# Key Concepts

- **Subquery** to compute product volume.
- **LEFT JOIN** to attach volume to warehouse inventory.
- **Multiplication (units × volume)** to calculate total space used.
- **SUM aggregation** to compute warehouse totals.
- **GROUP BY** to produce one row per warehouse.
