# 1587. Bank Account Summary II

## Table: Users

| Column Name | Type    |
| ----------- | ------- |
| account     | int     |
| name        | varchar |

Notes:

- `account` is the **primary key** (unique value).
- Each row represents a **bank user account**.
- No two users have the same name.

---

## Table: Transactions

| Column Name   | Type |
| ------------- | ---- |
| trans_id      | int  |
| account       | int  |
| amount        | int  |
| transacted_on | date |

Notes:

- `trans_id` is the **primary key**.
- Each row represents a **transaction affecting an account**.
- `amount`:
  - **Positive** → money received
  - **Negative** → money transferred out
- All accounts start with a **balance of 0**.

---

# Problem

Write a SQL query to report:

- `name`
- `balance`

For users whose **account balance is greater than 10000**.

The **balance** of an account is defined as:

```
balance = SUM(amount of all transactions for that account)
```

Return the result table **in any order**.

---

# Example

## Input

### Users

| account | name    |
| ------- | ------- |
| 900001  | Alice   |
| 900002  | Bob     |
| 900003  | Charlie |

### Transactions

| trans_id | account | amount | transacted_on |
| -------- | ------- | ------ | ------------- |
| 1        | 900001  | 7000   | 2020-08-01    |
| 2        | 900001  | 7000   | 2020-09-01    |
| 3        | 900001  | -3000  | 2020-09-02    |
| 4        | 900002  | 1000   | 2020-09-12    |
| 5        | 900003  | 6000   | 2020-08-07    |
| 6        | 900003  | 6000   | 2020-09-07    |
| 7        | 900003  | -4000  | 2020-09-11    |

---

## Output

| name  | balance |
| ----- | ------- |
| Alice | 11000   |

---

## Explanation

- **Alice**:
  7000 + 7000 - 3000 = **11000**

- **Bob**:
  balance = **1000**

- **Charlie**:
  6000 + 6000 - 4000 = **8000**

Only **Alice** has a balance greater than **10000**, so she appears in the result.
