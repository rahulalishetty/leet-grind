# 2701. Consecutive Transactions with Increasing Amounts

## Table: Transactions

| Column Name      | Type |
| ---------------- | ---- |
| transaction_id   | int  |
| customer_id      | int  |
| transaction_date | date |
| amount           | int  |

- `transaction_id` is the **primary key** of the table.
- Each row represents a **transaction** made by a customer.
- `(customer_id, transaction_date)` is **unique**, meaning each customer can have only one transaction per day.

---

## Problem

Find the customers who have made **consecutive transactions** where:

1. Transactions happen on **consecutive days**
2. The **amount strictly increases** each day
3. The sequence lasts for **at least 3 consecutive days**

For every valid sequence, return:

- `customer_id`
- `consecutive_start` → first date of the sequence
- `consecutive_end` → last date of the sequence

A customer may have **multiple valid sequences**.

---

## Output Requirements

Return a table ordered by:

```
customer_id
consecutive_start
consecutive_end
```

(all in **ascending order**)

---

# Example

## Input

### Transactions

| transaction_id | customer_id | transaction_date | amount |
| -------------- | ----------- | ---------------- | ------ |
| 1              | 101         | 2023-05-01       | 100    |
| 2              | 101         | 2023-05-02       | 150    |
| 3              | 101         | 2023-05-03       | 200    |
| 4              | 102         | 2023-05-01       | 50     |
| 5              | 102         | 2023-05-03       | 100    |
| 6              | 102         | 2023-05-04       | 200    |
| 7              | 105         | 2023-05-01       | 100    |
| 8              | 105         | 2023-05-02       | 150    |
| 9              | 105         | 2023-05-03       | 200    |
| 10             | 105         | 2023-05-04       | 300    |
| 11             | 105         | 2023-05-12       | 250    |
| 12             | 105         | 2023-05-13       | 260    |
| 13             | 105         | 2023-05-14       | 270    |

---

## Output

| customer_id | consecutive_start | consecutive_end |
| ----------- | ----------------- | --------------- |
| 101         | 2023-05-01        | 2023-05-03      |
| 105         | 2023-05-01        | 2023-05-04      |
| 105         | 2023-05-12        | 2023-05-14      |

---

## Explanation

### Customer 101

Transactions:

```
2023-05-01 → 100
2023-05-02 → 150
2023-05-03 → 200
```

- Dates are **consecutive**
- Amounts are **increasing**
- Length = **3 days**

Result:

```
2023-05-01 → 2023-05-03
```

---

### Customer 102

Transactions:

```
2023-05-01 → 50
2023-05-03 → 100
2023-05-04 → 200
```

Problems:

- Missing **May 2**
- Not **consecutive days**

Therefore:

```
No valid sequence
```

---

### Customer 105

Transactions:

```
2023-05-01 → 100
2023-05-02 → 150
2023-05-03 → 200
2023-05-04 → 300
```

- Consecutive days
- Increasing amounts
- Length = **4 days**

Result:

```
2023-05-01 → 2023-05-04
```

---

Second sequence:

```
2023-05-12 → 250
2023-05-13 → 260
2023-05-14 → 270
```

- Consecutive days
- Increasing amounts
- Length = **3 days**

Result:

```
2023-05-12 → 2023-05-14
```

---

## Final Result

| customer_id | consecutive_start | consecutive_end |
| ----------- | ----------------- | --------------- |
| 101         | 2023-05-01        | 2023-05-03      |
| 105         | 2023-05-01        | 2023-05-04      |
| 105         | 2023-05-12        | 2023-05-14      |
