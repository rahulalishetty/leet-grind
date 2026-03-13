# 1398. Customers Who Bought Products A and B but Not C

## Table: Customers

| Column Name   | Type    |
| ------------- | ------- |
| customer_id   | int     |
| customer_name | varchar |

**Notes:**

- `customer_id` contains **unique values** for each customer.
- `customer_name` is the name of the customer.

---

## Table: Orders

| Column Name  | Type    |
| ------------ | ------- |
| order_id     | int     |
| customer_id  | int     |
| product_name | varchar |

**Notes:**

- `order_id` contains **unique values**.
- `customer_id` identifies the customer who purchased the product.
- `product_name` represents the product purchased in that order.

---

# Problem

Write a SQL query to report the:

- `customer_id`
- `customer_name`

of customers who:

- **bought product "A"**
- **bought product "B"**
- **did NOT buy product "C"**

The result should be ordered by:

```
customer_id
```

---

# Example

## Input

### Customers Table

| customer_id | customer_name |
| ----------- | ------------- |
| 1           | Daniel        |
| 2           | Diana         |
| 3           | Elizabeth     |
| 4           | Jhon          |

### Orders Table

| order_id | customer_id | product_name |
| -------- | ----------- | ------------ |
| 10       | 1           | A            |
| 20       | 1           | B            |
| 30       | 1           | D            |
| 40       | 1           | C            |
| 50       | 2           | A            |
| 60       | 3           | A            |
| 70       | 3           | B            |
| 80       | 3           | D            |
| 90       | 4           | C            |

---

# Output

| customer_id | customer_name |
| ----------- | ------------- |
| 3           | Elizabeth     |

---

# Explanation

### Customer 1 (Daniel)

Purchased:

- A
- B
- D
- C

Since **product C was purchased**, this customer is **excluded**.

---

### Customer 2 (Diana)

Purchased:

- A

Customer **did not buy B**, so this customer is **excluded**.

---

### Customer 3 (Elizabeth)

Purchased:

- A
- B
- D

Customer **bought A and B**, and **did not buy C**.

Therefore, **Elizabeth qualifies**.

---

### Customer 4 (Jhon)

Purchased:

- C

Customer **did not buy A and B**, so this customer is **excluded**.

---
