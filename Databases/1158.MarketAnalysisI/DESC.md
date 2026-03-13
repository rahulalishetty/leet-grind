# 1158. Market Analysis I

## Tables

### Users

| Column Name    | Type    |
| -------------- | ------- |
| user_id        | int     |
| join_date      | date    |
| favorite_brand | varchar |

Notes:

- `user_id` is the **primary key**.
- This table stores information about users of an online shopping platform.
- Users can **buy and sell items**.

---

### Orders

| Column Name | Type |
| ----------- | ---- |
| order_id    | int  |
| order_date  | date |
| item_id     | int  |
| buyer_id    | int  |
| seller_id   | int  |

Notes:

- `order_id` is the **primary key**.
- `item_id` references the **Items** table.
- `buyer_id` and `seller_id` reference the **Users** table.

---

### Items

| Column Name | Type    |
| ----------- | ------- |
| item_id     | int     |
| item_brand  | varchar |

Notes:

- `item_id` is the **primary key**.

---

# Problem

Write a SQL query to **find for each user**:

- their **join date**
- the **number of orders they made as a buyer in 2019**

Requirements:

- Every user must appear in the result, even if they made **zero orders in 2019**.
- The output can be returned **in any order**.

---

# Example

## Input

### Users Table

| user_id | join_date  | favorite_brand |
| ------- | ---------- | -------------- |
| 1       | 2018-01-01 | Lenovo         |
| 2       | 2018-02-09 | Samsung        |
| 3       | 2018-01-19 | LG             |
| 4       | 2018-05-21 | HP             |

---

### Orders Table

| order_id | order_date | item_id | buyer_id | seller_id |
| -------- | ---------- | ------- | -------- | --------- |
| 1        | 2019-08-01 | 4       | 1        | 2         |
| 2        | 2018-08-02 | 2       | 1        | 3         |
| 3        | 2019-08-03 | 3       | 2        | 3         |
| 4        | 2018-08-04 | 1       | 4        | 2         |
| 5        | 2018-08-04 | 1       | 3        | 4         |
| 6        | 2019-08-05 | 2       | 2        | 4         |

---

### Items Table

| item_id | item_brand |
| ------- | ---------- |
| 1       | Samsung    |
| 2       | Lenovo     |
| 3       | LG         |
| 4       | HP         |

---

# Output

| buyer_id | join_date  | orders_in_2019 |
| -------- | ---------- | -------------- |
| 1        | 2018-01-01 | 1              |
| 2        | 2018-02-09 | 2              |
| 3        | 2018-01-19 | 0              |
| 4        | 2018-05-21 | 0              |

---

# Explanation

- **User 1**
  - Bought **1 item in 2019**
  - Bought another item in **2018**, which does **not count**

- **User 2**
  - Bought **2 items in 2019**

- **User 3**
  - Did not buy anything in **2019**
  - Result shows **0**

- **User 4**
  - Did not buy anything in **2019**
  - Result shows **0**
