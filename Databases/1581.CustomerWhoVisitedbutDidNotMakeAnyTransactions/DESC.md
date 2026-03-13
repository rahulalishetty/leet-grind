# 1581. Customer Who Visited but Did Not Make Any Transactions

## Table: Visits

| Column Name | Type |
| ----------- | ---- |
| visit_id    | int  |
| customer_id | int  |

Notes:

- `visit_id` has **unique values**.
- This table records **customers who visited the mall**.

---

## Table: Transactions

| Column Name    | Type |
| -------------- | ---- |
| transaction_id | int  |
| visit_id       | int  |
| amount         | int  |

Notes:

- `transaction_id` has **unique values**.
- This table records **transactions made during a visit**.

---

# Problem

Write a SQL query to find:

1. The **IDs of customers who visited but did not make any transactions**
2. The **number of such visits for each customer**

Return the result table containing:

| Column         | Description                               |
| -------------- | ----------------------------------------- |
| customer_id    | ID of the customer                        |
| count_no_trans | Number of visits with **no transactions** |

The result can be returned **in any order**.

---

# Example

## Input

### Visits table

| visit_id | customer_id |
| -------- | ----------- |
| 1        | 23          |
| 2        | 9           |
| 4        | 30          |
| 5        | 54          |
| 6        | 96          |
| 7        | 54          |
| 8        | 54          |

### Transactions table

| transaction_id | visit_id | amount |
| -------------- | -------- | ------ |
| 2              | 5        | 310    |
| 3              | 5        | 300    |
| 9              | 5        | 200    |
| 12             | 1        | 910    |
| 13             | 2        | 970    |

---

# Output

| customer_id | count_no_trans |
| ----------- | -------------- |
| 54          | 2              |
| 30          | 1              |
| 96          | 1              |

---

# Explanation

- Customer **23** visited once and made a transaction → excluded.
- Customer **9** visited once and made a transaction → excluded.
- Customer **30** visited once and made **no transactions** → counted once.
- Customer **54** visited three times:
  - One visit had transactions
  - Two visits had **no transactions** → counted twice.
- Customer **96** visited once and made **no transactions** → counted once.
