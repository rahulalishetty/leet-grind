# 3060. User Activities within Time Bounds

## Table: Sessions

| Column Name   | Type     |
| ------------- | -------- |
| user_id       | int      |
| session_start | datetime |
| session_end   | datetime |
| session_id    | int      |
| session_type  | enum     |

- `session_id` contains **unique values** for each row.
- `session_type` is an ENUM with possible values:
  - `Viewer`
  - `Streamer`
- Each row represents a **user session**, including the start and end time.

---

# Problem

Find the **users who have at least two sessions of the same type** (`Viewer` or `Streamer`) such that:

```
The gap between the sessions is at most 12 hours.
```

The result should include only:

```
user_id
```

Return the result ordered by:

```
user_id ASC
```

---

# Example

## Input

### Sessions

| user_id | session_start       | session_end         | session_id | session_type |
| ------- | ------------------- | ------------------- | ---------- | ------------ |
| 101     | 2023-11-01 08:00:00 | 2023-11-01 09:00:00 | 1          | Viewer       |
| 101     | 2023-11-01 10:00:00 | 2023-11-01 11:00:00 | 2          | Streamer     |
| 102     | 2023-11-01 13:00:00 | 2023-11-01 14:00:00 | 3          | Viewer       |
| 102     | 2023-11-01 15:00:00 | 2023-11-01 16:00:00 | 4          | Viewer       |
| 101     | 2023-11-02 09:00:00 | 2023-11-02 10:00:00 | 5          | Viewer       |
| 102     | 2023-11-02 12:00:00 | 2023-11-02 13:00:00 | 6          | Streamer     |
| 101     | 2023-11-02 13:00:00 | 2023-11-02 14:00:00 | 7          | Streamer     |
| 102     | 2023-11-02 16:00:00 | 2023-11-02 17:00:00 | 8          | Viewer       |
| 103     | 2023-11-01 08:00:00 | 2023-11-01 09:00:00 | 9          | Viewer       |
| 103     | 2023-11-02 20:00:00 | 2023-11-02 23:00:00 | 10         | Viewer       |
| 103     | 2023-11-03 09:00:00 | 2023-11-03 10:00:00 | 11         | Viewer       |

---

# Output

| user_id |
| ------- |
| 102     |
| 103     |

---

# Explanation

## User 101

Sessions:

```
Viewer   (session 1)
Streamer (session 2)
Viewer   (session 5)
Streamer (session 7)
```

There are **no two sessions of the same type within 12 hours**, therefore:

```
User 101 is excluded.
```

---

## User 102

Viewer sessions:

```
Session 3: 13:00 - 14:00
Session 4: 15:00 - 16:00
```

Gap between sessions:

```
1 hour
```

Since the gap is **less than 12 hours**, the user qualifies.

```
User 102 is included.
```

---

## User 103

Viewer sessions:

```
Session 10: 2023‑11‑02 20:00
Session 11: 2023‑11‑03 09:00
```

Gap between sessions:

```
13 hours from start times,
but the gap between session_end and next session_start is < 12 hours
```

Therefore:

```
User 103 qualifies.
```

---

# Final Result

| user_id |
| ------- |
| 102     |
| 103     |

Results are sorted in **ascending order of user_id**.
