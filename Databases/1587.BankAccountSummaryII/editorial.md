# 1587. Bank Account Summary II — Approaches

## Overview

Since each user has only one name but multiple transactions (amount), it is easier to:

1. Calculate the **balance for each account**
2. Identify accounts with **balance > 10000**
3. Join with the **Users table** to get the user names.

---

# Approach 1: First Calculate Then JOIN

## Algorithm

1. Use `SUM()` to calculate the **total balance** for each account.
2. Use `HAVING` to **filter accounts with balance greater than 10000**.
3. Join the result with the **Users table** to retrieve the user names.

---

## Step 1 & Step 2 — Calculate Balance

```sql
SELECT
    account,
    SUM(amount) AS balance
FROM Transactions
GROUP BY 1
HAVING balance > 10000;
```

This query:

- Groups transactions by `account`
- Calculates total balance
- Keeps only accounts with balance **greater than 10000**

---

## Step 3 — Join with Users Table

```sql
SELECT
    DISTINCT a.name,
    b.balance
FROM Users a
JOIN (
    SELECT
        account,
        SUM(amount) AS balance
    FROM Transactions
    GROUP BY 1
    HAVING balance > 10000
) b
ON a.account = b.account;
```

This retrieves the **name associated with each qualifying account**.

---

# Approach 2: JOIN and Calculate at the Same Time

## Algorithm

1. Select the required columns:
   - `name`
   - `SUM(amount)` as `balance`
2. Join `Users` and `Transactions` tables.
3. Group results by `account`.
4. Use `HAVING` to filter balances greater than **10000**.

---

## Implementation

```sql
SELECT
    u.name,
    SUM(t.amount) AS balance
FROM Users u
JOIN Transactions t
ON u.account = t.account
GROUP BY u.account
HAVING balance > 10000;
```

---

# Key SQL Concepts Used

- **SUM()** → Aggregates transaction amounts
- **GROUP BY** → Groups results per account
- **HAVING** → Filters aggregated results
- **JOIN** → Combines Users and Transactions tables
