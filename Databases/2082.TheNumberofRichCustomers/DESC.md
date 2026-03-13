# 2082. The Number of Rich Customers

## Table: Store

| Column Name | Type |
| ----------- | ---- |
| bill_id     | int  |
| customer_id | int  |
| amount      | int  |

### Notes

- `bill_id` is the **primary key**.
- Each row represents a **bill issued to a customer**.
- `amount` represents the value of that bill.

---

# Problem

Write a SQL query to report the **number of customers** who had **at least one bill** with an amount **strictly greater than 500**.

Return the result in the following format:

| rich_count |

---

# Example

## Input

### Store Table

| bill_id | customer_id | amount |
| ------- | ----------- | ------ |
| 6       | 1           | 549    |
| 8       | 1           | 834    |
| 4       | 2           | 394    |
| 11      | 3           | 657    |
| 13      | 3           | 257    |

---

## Output

| rich_count |
| ---------- |
| 2          |

---

# Explanation

- **Customer 1**
  - Bills: 549, 834 → both > 500 → qualifies

- **Customer 2**
  - Bill: 394 → not > 500 → does not qualify

- **Customer 3**
  - Bills: 657, 257 → one bill > 500 → qualifies

Therefore, **2 customers** satisfy the condition.
