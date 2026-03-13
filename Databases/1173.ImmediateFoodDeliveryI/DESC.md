# 1173. Immediate Food Delivery I

## Table: Delivery

| Column Name                 | Type |
| --------------------------- | ---- |
| delivery_id                 | int  |
| customer_id                 | int  |
| order_date                  | date |
| customer_pref_delivery_date | date |

Notes:

- `delivery_id` is the **primary key** of the table.
- The table stores information about **food delivery orders**.
- Each order has:
  - the **order date**
  - the **customer’s preferred delivery date**.
- The preferred delivery date can be **the same as the order date or later**.

---

# Problem

If the customer's preferred delivery date is the **same as the order date**, the order is called:

```
Immediate
```

Otherwise, the order is called:

```
Scheduled
```

Write a SQL query to find the **percentage of immediate orders** in the table.

Requirements:

- Return the result **rounded to 2 decimal places**.
- The result should contain a single column:

```
immediate_percentage
```

---

# Example

## Input

### Delivery table

| delivery_id | customer_id | order_date | customer_pref_delivery_date |
| ----------- | ----------- | ---------- | --------------------------- |
| 1           | 1           | 2019-08-01 | 2019-08-02                  |
| 2           | 5           | 2019-08-02 | 2019-08-02                  |
| 3           | 1           | 2019-08-11 | 2019-08-11                  |
| 4           | 3           | 2019-08-24 | 2019-08-26                  |
| 5           | 4           | 2019-08-21 | 2019-08-22                  |
| 6           | 2           | 2019-08-11 | 2019-08-13                  |

---

# Output

| immediate_percentage |
| -------------------- |
| 33.33                |

---

# Explanation

Immediate orders occur when:

```
order_date = customer_pref_delivery_date
```

From the table:

Immediate orders:

- delivery_id **2**
- delivery_id **3**

Total orders:

```
6
```

Immediate orders:

```
2
```

Percentage:

```
(2 / 6) * 100 = 33.33%
```
