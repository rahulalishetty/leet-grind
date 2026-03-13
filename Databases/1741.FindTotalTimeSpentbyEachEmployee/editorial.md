# 1741. Find Total Time Spent by Each Employee — Approach

## Approach: Calculate Time and Group By

### Intuition

To determine how long each employee spends in the office each day, we need to:

1. Compute the **time spent for each entry** using:

```
out_time - in_time
```

2. Since employees may **enter and leave multiple times per day**, we must **sum all time intervals** for that day.

3. Therefore, we group the results by:

- `emp_id`
- `event_day`

and compute the **sum of all time intervals**.

---

# Algorithm

Steps:

1. **Calculate the duration of each office entry**

   For every row:

```
duration = out_time - in_time
```

2. **Group records** by:
   - `emp_id`
   - `event_day`

3. **Sum all durations** for each employee per day.

---

# Example

## Original Table

| emp_id | event_day  | in_time | out_time |
| ------ | ---------- | ------- | -------- |
| 1      | 2020-11-28 | 4       | 32       |
| 1      | 2020-11-28 | 55      | 200      |
| 1      | 2020-12-03 | 1       | 42       |
| 2      | 2020-11-28 | 3       | 33       |
| 2      | 2020-12-09 | 47      | 74       |

---

## After Aggregation

| day        | emp_id | total_time |
| ---------- | ------ | ---------- |
| 2020-11-28 | 1      | 173        |
| 2020-11-28 | 2      | 30         |
| 2020-12-03 | 1      | 41         |
| 2020-12-09 | 2      | 27         |

---

# SQL Implementation

```sql
SELECT
    event_day AS day,
    emp_id,
    SUM(out_time - in_time) AS total_time
FROM Employees
GROUP BY event_day, emp_id;
```

---

# Key SQL Concepts Used

- **Arithmetic calculation** (`out_time - in_time`)
- **Aggregation using `SUM()`**
- **Grouping using `GROUP BY`**
