# 1890. The Latest Login in 2020 — Approaches

## Overview

To solve this problem we must satisfy two conditions:

1. **Filter logins that occurred in the year 2020**
2. **From those records, select the latest login for each user**

### Extracting the Year

Two commonly used SQL functions can extract the year from a date:

- `YEAR(date)` → returns the year from a date
- `EXTRACT(unit FROM date)` → extracts specific parts of a date (year, month, etc.)

### Finding the Latest Record

Two common methods exist:

- `MAX(expr)` → returns the maximum value of an expression (latest timestamp)
- `FIRST_VALUE(expr)` → window function that returns the first value in a sorted window

---

# Approach 1: Using `YEAR()` and `MAX()`

## Intuition

1. Filter records where the login occurred in **2020**
2. Use **MAX(time_stamp)** to find the latest login for each user
3. Group results by **user_id**

---

## Algorithm

1. Select required columns (`user_id`, latest timestamp)
2. Filter rows where `YEAR(time_stamp) = 2020`
3. Use `MAX(time_stamp)` to find the latest login
4. Group results by `user_id`

---

## SQL Implementation

```sql
SELECT
    user_id,
    MAX(time_stamp) AS last_stamp
FROM Logins
WHERE YEAR(time_stamp) = 2020
GROUP BY user_id;
```

---

# Approach 2: Using `EXTRACT()` and `FIRST_VALUE()`

## Intuition

1. Filter logins that occurred in **2020**
2. Use a **window function** to retrieve the latest timestamp
3. Sort timestamps **descending** so the newest login appears first
4. Use `DISTINCT` to ensure only one record per user

---

## Algorithm

1. Extract year using `EXTRACT(YEAR FROM time_stamp)`
2. Partition rows by `user_id`
3. Order timestamps descending
4. Use `FIRST_VALUE()` to retrieve the latest login

---

## SQL Implementation

```sql
SELECT
    DISTINCT user_id,
    FIRST_VALUE(time_stamp) OVER (
        PARTITION BY user_id
        ORDER BY time_stamp DESC
    ) AS last_stamp
FROM Logins
WHERE EXTRACT(YEAR FROM time_stamp) = 2020;
```

---

# Key SQL Concepts Used

- `YEAR()` function
- `EXTRACT()` date function
- `MAX()` aggregation
- Window functions (`FIRST_VALUE`)
- `PARTITION BY`
- `ORDER BY`
- `DISTINCT`
