# 1890. The Latest Login in 2020

## Table: Logins

| Column Name | Type     |
| ----------- | -------- |
| user_id     | int      |
| time_stamp  | datetime |

### Notes

- `(user_id, time_stamp)` is the **primary key**.
- Each row records the **login timestamp** of a user.

---

# Problem

Write a SQL query to report the **latest login for each user in the year 2020**.

Rules:

- Consider **only logins that occurred in 2020**.
- If a user **did not login in 2020**, they should **not appear** in the result.
- For users who logged in **multiple times in 2020**, return only their **latest login timestamp**.

Return the result table **in any order**.

---

# Example

## Input

### Logins Table

| user_id | time_stamp          |
| ------- | ------------------- |
| 6       | 2020-06-30 15:06:07 |
| 6       | 2021-04-21 14:06:06 |
| 6       | 2019-03-07 00:18:15 |
| 8       | 2020-02-01 05:10:53 |
| 8       | 2020-12-30 00:46:50 |
| 2       | 2020-01-16 02:49:50 |
| 2       | 2019-08-25 07:59:08 |
| 14      | 2019-07-14 09:00:00 |
| 14      | 2021-01-06 11:59:59 |

---

## Output

| user_id | last_stamp          |
| ------- | ------------------- |
| 6       | 2020-06-30 15:06:07 |
| 8       | 2020-12-30 00:46:50 |
| 2       | 2020-01-16 02:49:50 |

---

# Explanation

- **User 6**
  - Logged in three times overall but **once in 2020** → included.

- **User 8**
  - Logged in **twice in 2020** → latest login in **December** is returned.

- **User 2**
  - Logged in twice overall but **once in 2020** → included.

- **User 14**
  - Did **not login in 2020** → excluded.
