# 1159. Market Analysis II

## Tables

### Users

| Column Name    | Type    |
| -------------- | ------- |
| user_id        | int     |
| join_date      | date    |
| favorite_brand | varchar |

- `user_id` is the **primary key**.
- This table contains information about users of an online shopping platform.
- Users can both **buy and sell items**.

---

### Orders

| Column Name | Type |
| ----------- | ---- |
| order_id    | int  |
| order_date  | date |
| item_id     | int  |
| buyer_id    | int  |
| seller_id   | int  |

- `order_id` is the **primary key**.
- `item_id` is a foreign key referencing the **Items** table.
- `buyer_id` and `seller_id` are foreign keys referencing the **Users** table.
- Each row represents one transaction.

---

### Items

| Column Name | Type    |
| ----------- | ------- |
| item_id     | int     |
| item_brand  | varchar |

- `item_id` is the **primary key**.
- This table stores the **brand of each item**.

---

## Problem

For each user, determine whether the **brand of the second item they sold** (ordered by date) matches their **favorite brand**.

### Rules

1. If a user sold **less than two items**, the answer should be **"no"**.
2. If the **brand of the second sold item** equals the user's **favorite_brand**, the answer should be **"yes"**.
3. Otherwise, the answer should be **"no"**.
4. It is guaranteed that **no seller sells more than one item on the same day**.

Return the result table in any order.

---

## Example

### Input

#### Users

| user_id | join_date  | favorite_brand |
| ------- | ---------- | -------------- |
| 1       | 2019-01-01 | Lenovo         |
| 2       | 2019-02-09 | Samsung        |
| 3       | 2019-01-19 | LG             |
| 4       | 2019-05-21 | HP             |

---

#### Orders

| order_id | order_date | item_id | buyer_id | seller_id |
| -------- | ---------- | ------- | -------- | --------- |
| 1        | 2019-08-01 | 4       | 1        | 2         |
| 2        | 2019-08-02 | 2       | 1        | 3         |
| 3        | 2019-08-03 | 3       | 2        | 3         |
| 4        | 2019-08-04 | 1       | 4        | 2         |
| 5        | 2019-08-04 | 1       | 3        | 4         |
| 6        | 2019-08-05 | 2       | 2        | 4         |

---

#### Items

| item_id | item_brand |
| ------- | ---------- |
| 1       | Samsung    |
| 2       | Lenovo     |
| 3       | LG         |
| 4       | HP         |

---

### Output

| seller_id | 2nd_item_fav_brand |
| --------- | ------------------ |
| 1         | no                 |
| 2         | yes                |
| 3         | yes                |
| 4         | no                 |

---

## Explanation

- **User 1** sold **no items**, so the result is **no**.
- **User 2** sold two items:
  - First: item brand HP
  - Second: item brand Samsung
  - Favorite brand = Samsung → **yes**
- **User 3** sold two items:
  - Second sold item brand = LG
  - Favorite brand = LG → **yes**
- **User 4** sold two items:
  - Second sold item brand ≠ favorite brand → **no**
