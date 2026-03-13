# Customers Spending At Least $100 in June and July 2020 — Approach

## Approach: Group by `customer_id` and Filter by Spending Sum

### Intuition

The goal is to find customers who spent **at least $100 in both June and July 2020**.

To compute this, we must:

1. Combine information from multiple tables.
2. Calculate spending per customer per month.
3. Filter customers who satisfy the spending condition.

---

# Step 1 — Join the Tables

We need data from three tables:

- **Customers** → customer information (`customer_id`, `name`)
- **Orders** → order details (`order_date`, `quantity`)
- **Product** → product prices

We join them as follows:

```
Customers → Orders → Product
```

SQL:

```sql
JOIN Orders USING(customer_id)
JOIN Product USING(product_id)
```

This produces a dataset containing:

- customer name
- order date
- quantity purchased
- product price

---

# Step 2 — Filter by Year

We only care about **orders placed in 2020**.

```sql
WHERE YEAR(order_date) = 2020
```

This removes orders from other years.

---

# Step 3 — Group by Customer

To compute total spending per customer, we group rows by:

```
GROUP BY customer_id
```

Each group now represents **all orders of a single customer**.

---

# Step 4 — Conditional Aggregation

We compute two separate spending totals:

- June 2020 spending
- July 2020 spending

This is done using conditional aggregation.

### June spending

```
SUM(IF(MONTH(order_date) = 6, quantity, 0) * price)
```

Explanation:

- If order month is **June (6)** → include `quantity`
- Otherwise → use `0`
- Multiply by product price

---

### July spending

```
SUM(IF(MONTH(order_date) = 7, quantity, 0) * price)
```

Same logic, but applied to **July orders**.

---

# Step 5 — Apply the Spending Condition

We keep only customers who spent **at least $100 in both months**.

Using the `HAVING` clause:

```
HAVING June_spending >= 100
AND July_spending >= 100
```

---

# Final SQL Query

```sql
SELECT
  customer_id,
  name
FROM Customers
JOIN Orders USING(customer_id)
JOIN Product USING(product_id)
WHERE YEAR(order_date) = 2020
GROUP BY customer_id
HAVING
  SUM(IF(MONTH(order_date) = 6, quantity, 0) * price) >= 100
AND
  SUM(IF(MONTH(order_date) = 7, quantity, 0) * price) >= 100;
```

---

# Key Concepts

- **JOIN** combines data from multiple tables.
- **GROUP BY** aggregates results per customer.
- **Conditional aggregation** calculates spending for specific months.
- **HAVING** filters aggregated results.
