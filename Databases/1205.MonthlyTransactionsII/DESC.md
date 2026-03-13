# 1205. Monthly Transactions II

## Tables

### Transactions

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| country     | varchar |
| state       | enum    |
| amount      | int     |
| trans_date  | date    |

Notes:

- `id` is the column with **unique values**.
- This table contains information about **incoming transactions**.
- The `state` column is an ENUM with values:
  - `"approved"`
  - `"declined"`

---

### Chargebacks

| Column Name | Type |
| ----------- | ---- |
| trans_id    | int  |
| trans_date  | date |

Notes:

- `trans_id` is a **foreign key** referencing `Transactions.id`.
- Each row represents a **chargeback event** for a previous transaction.
- A chargeback may correspond to **any transaction**, even if the original transaction was **declined**.

---

# Problem

Write a SQL query to find for **each month and country**:

- number of **approved transactions**
- total **approved transaction amount**
- number of **chargebacks**
- total **chargeback amount**

Important rules:

- The month should be derived from the `trans_date`.
- Chargeback transactions reference the original transaction through `trans_id`.
- If for a given `(month, country)` all values are zero, that row should **not appear** in the output.
- The result can be returned **in any order**.

---

# Example

## Input

### Transactions Table

| id  | country | state    | amount | trans_date |
| --- | ------- | -------- | ------ | ---------- |
| 101 | US      | approved | 1000   | 2019-05-18 |
| 102 | US      | declined | 2000   | 2019-05-19 |
| 103 | US      | approved | 3000   | 2019-06-10 |
| 104 | US      | declined | 4000   | 2019-06-13 |
| 105 | US      | approved | 5000   | 2019-06-15 |

---

### Chargebacks Table

| trans_id | trans_date |
| -------- | ---------- |
| 102      | 2019-05-29 |
| 101      | 2019-06-30 |
| 105      | 2019-09-18 |

---

# Output

| month   | country | approved_count | approved_amount | chargeback_count | chargeback_amount |
| ------- | ------- | -------------- | --------------- | ---------------- | ----------------- |
| 2019-05 | US      | 1              | 1000            | 1                | 2000              |
| 2019-06 | US      | 2              | 8000            | 1                | 1000              |
| 2019-09 | US      | 0              | 0               | 1                | 5000              |

---

# Explanation

### May 2019

Approved transactions:

- Transaction **101** → amount **1000**

Declined transactions:

- Transaction **102**

Chargebacks:

- Chargeback for **transaction 102** occurred on **2019‑05‑29**
- Chargeback amount = **2000**

Result:

- approved_count = **1**
- approved_amount = **1000**
- chargeback_count = **1**
- chargeback_amount = **2000**

---

### June 2019

Approved transactions:

- Transaction **103** → amount **3000**
- Transaction **105** → amount **5000**

Total approved amount = **8000**

Chargebacks:

- Chargeback for **transaction 101** occurred on **2019‑06‑30**
- Original transaction amount = **1000**

Result:

- approved_count = **2**
- approved_amount = **8000**
- chargeback_count = **1**
- chargeback_amount = **1000**

---

### September 2019

Approved transactions:

- none

Chargebacks:

- Chargeback for **transaction 105** occurred on **2019‑09‑18**
- Amount = **5000**

Result:

- approved_count = **0**
- approved_amount = **0**
- chargeback_count = **1**
- chargeback_amount = **5000**

---
