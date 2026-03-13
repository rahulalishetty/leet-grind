# 1729. Find Followers Count

## Table: Followers

| Column Name | Type |
| ----------- | ---- |
| user_id     | int  |
| follower_id | int  |

### Notes

- `(user_id, follower_id)` is the **primary key**.
- Each row represents a relationship where **follower_id follows user_id**.
- This table stores follower relationships in a social media application.

---

# Problem

Write a SQL query that returns, **for each user**, the **number of followers** they have.

Return the result table **ordered by `user_id` in ascending order**.

---

# Example

## Input

### Followers

| user_id | follower_id |
| ------- | ----------- |
| 0       | 1           |
| 1       | 0           |
| 2       | 0           |
| 2       | 1           |

---

## Output

| user_id | followers_count |
| ------- | --------------- |
| 0       | 1               |
| 1       | 1               |
| 2       | 2               |

---

# Explanation

- **User 0**
  - Followers: `{1}`
  - Count = **1**

- **User 1**
  - Followers: `{0}`
  - Count = **1**

- **User 2**
  - Followers: `{0, 1}`
  - Count = **2**
