# Approach 1: Using SQL Queries

## Algorithm

### 1. Initialize Query

Start writing an SQL query.

### 2. Select Columns

Select:

- `product_id`
- the **average selling price** for each product

The average selling price is calculated as:

```
total revenue / total units sold
```

Where:

```
revenue = price × units
```

---

### 3. Join Tables

Join the **Prices** table with the **UnitsSold** table.

A **LEFT JOIN** is used so that products with **no sales** are still included in the result.

Join condition:

```
p.product_id = u.product_id
```

---

### 4. Filter Data

Ensure that the sale occurred during the valid price period.

```
u.purchase_date BETWEEN p.start_date AND p.end_date
```

This guarantees that the correct price is applied to each sale.

---

### 5. Group Data

Group the rows by:

```
product_id
```

This allows aggregation of:

- total revenue
- total units sold

for each product.

---

### 6. Calculate Average Price

Compute the average selling price:

```
SUM(price * units) / SUM(units)
```

Then:

- multiply units by price to compute revenue
- divide by total units

---

### 7. Handle NULL Values

If a product has **no sold units**, the result becomes `NULL`.

Use:

```
IFNULL(..., 0)
```

to convert the result to **0**.

---

### 8. Round the Result

Use the `ROUND()` function to round the value to **2 decimal places**.

```
ROUND(value, 2)
```

---

# Final SQL Query

```sql
SELECT
    p.product_id,
    IFNULL(
        ROUND(SUM(p.price * u.units) / SUM(u.units), 2),
        0
    ) AS average_price
FROM
    Prices AS p
LEFT JOIN
    UnitsSold AS u
ON
    p.product_id = u.product_id
    AND u.purchase_date BETWEEN p.start_date AND p.end_date
GROUP BY
    p.product_id;
```
