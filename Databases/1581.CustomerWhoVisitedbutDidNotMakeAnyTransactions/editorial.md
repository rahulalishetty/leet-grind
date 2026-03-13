# 1581. Customer Who Visited but Did Not Make Any Transactions — Approaches

## Approach 1: Removing Records Using NOT IN / EXISTS

### Algorithm

In this approach, we remove visits that had transactions using a **subquery with NOT IN**.

---

### Step 1 — Identify Visits With Transactions

First, find all visit IDs that appear in the `Transactions` table.

```sql
SELECT visit_id
FROM Transactions;
```

These visit IDs represent visits where at least one transaction occurred.

---

### Step 2 — Exclude These Visits

Next, we select visits from the `Visits` table that **do not appear** in the `Transactions` table.

```sql
WHERE visit_id NOT IN (
    SELECT visit_id FROM Transactions
)
```

This leaves only visits where **no transaction occurred**.

---

### Step 3 — Count Visits Per Customer

Finally, we group the results by `customer_id` and count how many such visits each customer had.

---

### Implementation

```sql
SELECT
  customer_id,
  COUNT(visit_id) AS count_no_trans
FROM Visits
WHERE visit_id NOT IN (
    SELECT visit_id
    FROM Transactions
)
GROUP BY customer_id;
```

---

# Approach 2: Removing Records Using LEFT JOIN and IS NULL

### Algorithm

Another way to solve this problem is by using a **LEFT JOIN**.

The idea:

- Join all visits with transactions.
- Visits without transactions will produce **NULL values** on the transaction side.

---

### Step 1 — LEFT JOIN Visits and Transactions

```sql
LEFT JOIN Transactions
ON Visits.visit_id = Transactions.visit_id
```

This attaches transaction information to visits.

---

### Step 2 — Filter Rows Without Transactions

If a visit has **no matching transaction**, the `transaction.visit_id` will be **NULL**.

```sql
WHERE t.visit_id IS NULL
```

This keeps only visits that **did not produce transactions**.

---

### Step 3 — Count Visits Per Customer

We group by `customer_id` to count how many such visits each customer had.

---

### Implementation

```sql
SELECT
  customer_id,
  COUNT(*) AS count_no_trans
FROM Visits AS v
LEFT JOIN Transactions AS t
ON v.visit_id = t.visit_id
WHERE t.visit_id IS NULL
GROUP BY customer_id;
```

---

# Key Concepts

- **NOT IN / EXISTS** can filter out rows present in another table.
- **LEFT JOIN + IS NULL** is a common pattern for finding unmatched rows.
- **GROUP BY** aggregates visits per customer.
