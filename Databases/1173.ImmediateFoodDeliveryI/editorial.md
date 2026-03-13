# Immediate Food Delivery I — Explanation

## Intuition

The percentage of immediate deliveries can be calculated using the formula:

```
percentage = (validCount / totalCount) * 100
```

Where:

- **validCount** = number of immediate orders
- **totalCount** = total number of orders

An order is **immediate** when:

```
order_date = customer_pref_delivery_date
```

---

# Algorithm

## Step 1 — Compute the ratio of immediate orders

We compute the ratio of immediate orders using:

```sql
AVG(order_date = customer_pref_delivery_date)
```

### Why this works

The expression:

```
order_date = customer_pref_delivery_date
```

evaluates to:

- **1 (True)** if the dates are equal
- **0 (False)** otherwise

Example:

| order_date | customer_pref_delivery_date | result |
| ---------- | --------------------------- | ------ |
| 2019‑08‑02 | 2019‑08‑02                  | 1      |
| 2019‑08‑01 | 2019‑08‑02                  | 0      |

So if we have:

```
[0,1,1,0,0,0]
```

Then:

```
AVG = (0 + 1 + 1 + 0 + 0 + 0) / 6 = 0.3333
```

This represents the **fraction of immediate orders**.

---

# Step 2 — Convert the ratio to percentage

Multiply by **100**:

```sql
100 * AVG(order_date = customer_pref_delivery_date)
```

Example:

```
0.3333 * 100 = 33.33
```

---

# Step 3 — Round the result

Use the `ROUND()` function:

```
ROUND(number, k)
```

Where:

- **number** = value to round
- **k** = number of decimal places

For this problem:

```sql
ROUND(100 * AVG(order_date = customer_pref_delivery_date), 2)
```

This rounds the result to **2 decimal places**.

---

# Step 4 — Rename the result column

Use `AS` to rename the column:

```sql
AS immediate_percentage
```

---

# Final SQL Query

```sql
SELECT ROUND(
    100 * AVG(order_date = customer_pref_delivery_date),
    2
) AS immediate_percentage
FROM Delivery;
```

---

# Key Concepts

- **Boolean expressions in MySQL** evaluate to **1 or 0**.
- **AVG()** can therefore compute proportions.
- Multiplying by **100** converts the fraction to a percentage.
- **ROUND(..., 2)** ensures the result has **two decimal places**.
