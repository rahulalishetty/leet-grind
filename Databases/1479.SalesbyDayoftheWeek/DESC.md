# 1479. Sales by Day of the Week

## Tables

### Orders

| Column Name | Type    |
| ----------- | ------- |
| order_id    | int     |
| customer_id | int     |
| order_date  | date    |
| item_id     | varchar |
| quantity    | int     |

- `(order_id, item_id)` is the **primary key**.
- Each row represents an order of a specific item.
- `order_date` is the date the item was ordered.
- `quantity` indicates how many units were ordered.

---

### Items

| Column Name   | Type    |
| ------------- | ------- |
| item_id       | varchar |
| item_name     | varchar |
| item_category | varchar |

- `item_id` is the **primary key**.
- `item_name` is the name of the item.
- `item_category` indicates the category to which the item belongs.

---

## Problem

You are the business owner and want to create a **sales report by item category and day of the week**.

For each **item category**, report the total number of units ordered on each day of the week:

- Monday
- Tuesday
- Wednesday
- Thursday
- Friday
- Saturday
- Sunday

The output should:

- Display one row per **item category**
- Show the **total quantity ordered per weekday**
- Be **ordered by category**

---

## Example

### Input

#### Orders

| order_id | customer_id | order_date | item_id | quantity |
| -------- | ----------- | ---------- | ------- | -------- |
| 1        | 1           | 2020-06-01 | 1       | 10       |
| 2        | 1           | 2020-06-08 | 2       | 10       |
| 3        | 2           | 2020-06-02 | 1       | 5        |
| 4        | 3           | 2020-06-03 | 3       | 5        |
| 5        | 4           | 2020-06-04 | 4       | 1        |
| 6        | 4           | 2020-06-05 | 5       | 5        |
| 7        | 5           | 2020-06-05 | 1       | 10       |
| 8        | 5           | 2020-06-14 | 4       | 5        |
| 9        | 5           | 2020-06-21 | 3       | 5        |

---

#### Items

| item_id | item_name     | item_category |
| ------- | ------------- | ------------- |
| 1       | LC Alg. Book  | Book          |
| 2       | LC DB. Book   | Book          |
| 3       | LC Smartphone | Phone         |
| 4       | LC Phone 2020 | Phone         |
| 5       | LC SmartGlass | Glasses       |
| 6       | LC T-Shirt XL | T-Shirt       |

---

### Output

| Category | Monday | Tuesday | Wednesday | Thursday | Friday | Saturday | Sunday |
| -------- | ------ | ------- | --------- | -------- | ------ | -------- | ------ |
| Book     | 20     | 5       | 0         | 0        | 10     | 0        | 0      |
| Glasses  | 0      | 0       | 0         | 0        | 5      | 0        | 0      |
| Phone    | 0      | 0       | 5         | 1        | 0      | 0        | 10     |
| T-Shirt  | 0      | 0       | 0         | 0        | 0      | 0        | 0      |

---

## Explanation

- **Monday (2020‑06‑01, 2020‑06‑08)**
  - Book category sold **20 units (10 + 10)**.

- **Tuesday (2020‑06‑02)**
  - Book category sold **5 units**.

- **Wednesday (2020‑06‑03)**
  - Phone category sold **5 units**.

- **Thursday (2020‑06‑04)**
  - Phone category sold **1 unit**.

- **Friday (2020‑06‑05)**
  - Book category sold **10 units**.
  - Glasses category sold **5 units**.

- **Saturday**
  - No items sold.

- **Sunday (2020‑06‑14, 2020‑06‑21)**
  - Phone category sold **10 units (5 + 5)**.

- **T‑Shirt category** has **no sales**, so all values are **0**.

---
