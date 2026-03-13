# 2837. Total Traveled Distance

## Tables

### Users

| Column Name | Type    |
| ----------- | ------- |
| user_id     | int     |
| name        | varchar |

- `user_id` is the **primary key**.
- Each row represents a **user and their name**.

---

### Rides

| Column Name | Type |
| ----------- | ---- |
| ride_id     | int  |
| user_id     | int  |
| distance    | int  |

- `ride_id` is the **primary key**.
- Each row represents a **ride taken by a user**.
- `distance` represents the distance traveled in that ride.

---

# Problem

Write a SQL query to calculate the **total distance traveled by each user**.

Requirements:

- If a user **has not taken any rides**, their traveled distance should be **0**.
- Return the following columns:

| user_id | name | traveled distance |

- The result must be **ordered by `user_id` in ascending order**.

---

# Example

## Input

### Users Table

| user_id | name    |
| ------- | ------- |
| 17      | Addison |
| 14      | Ethan   |
| 4       | Michael |
| 2       | Avery   |
| 10      | Eleanor |

### Rides Table

| ride_id | user_id | distance |
| ------- | ------- | -------- |
| 72      | 17      | 160      |
| 42      | 14      | 161      |
| 45      | 4       | 59       |
| 32      | 2       | 197      |
| 15      | 4       | 357      |
| 56      | 2       | 196      |
| 10      | 14      | 25       |

---

## Output

| user_id | name    | traveled distance |
| ------- | ------- | ----------------- |
| 2       | Avery   | 393               |
| 4       | Michael | 416               |
| 10      | Eleanor | 0                 |
| 14      | Ethan   | 186               |
| 17      | Addison | 160               |

---

# Explanation

- **User 2**
  - Distances: 197 + 196 = **393**

- **User 4**
  - Distances: 59 + 357 = **416**

- **User 14**
  - Distances: 161 + 25 = **186**

- **User 17**
  - Distance: **160**

- **User 10**
  - No rides → **0 distance**
