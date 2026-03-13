# 1633. Percentage of Users Attended a Contest

## Table: Users

| Column Name | Type    |
| ----------- | ------- |
| user_id     | int     |
| user_name   | varchar |

Notes:

- `user_id` is the **primary key** (unique value).
- Each row represents a **user in the system**.

---

## Table: Register

| Column Name | Type |
| ----------- | ---- |
| contest_id  | int  |
| user_id     | int  |

Notes:

- `(contest_id, user_id)` is the **primary key**.
- Each row indicates that a **user registered for a contest**.

---

# Problem

Write a SQL query to calculate the **percentage of users who registered for each contest**.

The percentage should be:

```
(number of users registered for the contest / total number of users) * 100
```

Requirements:

- Round the percentage to **two decimal places**
- Return results **ordered by percentage in descending order**
- If percentages are equal, order by **contest_id in ascending order**

---

# Example

## Input

### Users

| user_id | user_name |
| ------- | --------- |
| 6       | Alice     |
| 2       | Bob       |
| 7       | Alex      |

### Register

| contest_id | user_id |
| ---------- | ------- |
| 215        | 6       |
| 209        | 2       |
| 208        | 2       |
| 210        | 6       |
| 208        | 6       |
| 209        | 7       |
| 209        | 6       |
| 215        | 7       |
| 208        | 7       |
| 210        | 2       |
| 207        | 2       |
| 210        | 7       |

---

# Output

| contest_id | percentage |
| ---------- | ---------- |
| 208        | 100.0      |
| 209        | 100.0      |
| 210        | 100.0      |
| 215        | 66.67      |
| 207        | 33.33      |

---

# Explanation

- **Contests 208, 209, 210**: All users registered → **100%**
- **Contest 215**: 2 of 3 users registered → **66.67%**
- **Contest 207**: 1 of 3 users registered → **33.33%**
