# 586. Customer Placing the Largest Number of Orders

## Table: Orders

| Column Name     | Type |
| --------------- | ---- |
| order_number    | int  |
| customer_number | int  |

- `order_number` is the **primary key** (unique values).
- This table stores information about **orders** and the **customer who placed each order**.

---

## Problem

Write a SQL query to find the **customer_number** for the customer who has placed the **largest number of orders**.

### Guarantee

The test cases guarantee that **exactly one customer** has placed **more orders than any other customer**.

---

## Example

### Input

#### Orders table

| order_number | customer_number |
| ------------ | --------------- |
| 1            | 1               |
| 2            | 2               |
| 3            | 3               |
| 4            | 3               |

---

### Output

| customer_number |
| --------------- |
| 3               |

---

## Explanation

- Customer **1** placed **1 order**
- Customer **2** placed **1 order**
- Customer **3** placed **2 orders**

Since **customer 3** has the **largest number of orders**, the result is:

```
customer_number = 3
```

---

## Follow-up

What if **multiple customers** have the **same largest number of orders**?

In that case, return **all customer_number values** that share the maximum order count.
