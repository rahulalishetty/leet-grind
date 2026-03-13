# 1141. User Activity for the Past 30 Days I

## Table: Activity

| Column Name   | Type |
| ------------- | ---- |
| user_id       | int  |
| session_id    | int  |
| activity_date | date |
| activity_type | enum |

Notes:

- This table **may contain duplicate rows**.
- The `activity_type` column is an ENUM with the following values:

```
('open_session', 'end_session', 'scroll_down', 'send_message')
```

- The table records **user activities on a social media website**.
- Each **session belongs to exactly one user**.

---

# Problem

Write a SQL query to find the **daily active user count** for a **30‑day period ending on `2019-07-27` (inclusive)**.

A **user is considered active on a day** if they perform **at least one activity** on that day.

All activity types are valid and should be considered:

```
open_session
end_session
scroll_down
send_message
```

Return the result table **in any order**.

Note:

- Only return **days with at least one active user**.
- Days with **zero active users should not appear in the result**.

---

# Example

## Input

### Activity table

| user_id | session_id | activity_date | activity_type |
| ------- | ---------- | ------------- | ------------- |
| 1       | 1          | 2019-07-20    | open_session  |
| 1       | 1          | 2019-07-20    | scroll_down   |
| 1       | 1          | 2019-07-20    | end_session   |
| 2       | 4          | 2019-07-20    | open_session  |
| 2       | 4          | 2019-07-21    | send_message  |
| 2       | 4          | 2019-07-21    | end_session   |
| 3       | 2          | 2019-07-21    | open_session  |
| 3       | 2          | 2019-07-21    | send_message  |
| 3       | 2          | 2019-07-21    | end_session   |
| 4       | 3          | 2019-06-25    | open_session  |
| 4       | 3          | 2019-06-25    | end_session   |

---

# Output

| day        | active_users |
| ---------- | ------------ |
| 2019-07-20 | 2            |
| 2019-07-21 | 2            |

---

# Explanation

We consider activities that occurred within the **30‑day window ending on 2019‑07‑27**.

The relevant range is:

```
2019-06-28 → 2019-07-27
```

### 2019‑07‑20

Active users:

```
user 1
user 2
```

Total active users = **2**

---

### 2019‑07‑21

Active users:

```
user 2
user 3
```

Total active users = **2**

---

### 2019‑06‑25

User 4 has activity here, but this date is **outside the 30‑day window**, so it is ignored.

---

Only days with **at least one active user** are returned in the result.
