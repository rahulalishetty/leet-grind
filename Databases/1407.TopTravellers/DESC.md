# 1407. Top Travellers

## Table: Users

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |

Notes:

- `id` is a **unique column** for this table.
- `name` represents the **name of the user**.

---

## Table: Rides

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| user_id     | int  |
| distance    | int  |

Notes:

- `id` is a **unique column** for this table.
- `user_id` indicates the **user who traveled**.
- `distance` represents the **distance traveled during the ride**.

---

# Problem

Write a SQL query to report the **total distance traveled by each user**.

Requirements:

- Include **all users**, even if they **did not take any rides**.
- If a user has **no rides**, their traveled distance should be **0**.

Sorting rules:

1. Order by **travelled_distance in descending order**
2. If two users traveled the same distance, order by **name in ascending order**

---

# Example

## Input

### Users table

| id  | name     |
| --- | -------- |
| 1   | Alice    |
| 2   | Bob      |
| 3   | Alex     |
| 4   | Donald   |
| 7   | Lee      |
| 13  | Jonathan |
| 19  | Elvis    |

### Rides table

| id  | user_id | distance |
| --- | ------- | -------- |
| 1   | 1       | 120      |
| 2   | 2       | 317      |
| 3   | 3       | 222      |
| 4   | 7       | 100      |
| 5   | 13      | 312      |
| 6   | 19      | 50       |
| 7   | 7       | 120      |
| 8   | 19      | 400      |
| 9   | 7       | 230      |

---

# Output

| name     | travelled_distance |
| -------- | ------------------ |
| Elvis    | 450                |
| Lee      | 450                |
| Bob      | 317                |
| Jonathan | 312                |
| Alex     | 222                |
| Alice    | 120                |
| Donald   | 0                  |

---

# Explanation

We calculate the **total distance traveled by each user** by summing their rides.

### Elvis

```
50 + 400 = 450
```

### Lee

```
100 + 120 + 230 = 450
```

Since Elvis and Lee have the same distance, they are ordered **alphabetically by name**.

### Other users

- Bob → 317
- Jonathan → 312
- Alex → 222
- Alice → 120

### Donald

Donald did **not take any rides**, so his total distance is:

```
0
```
