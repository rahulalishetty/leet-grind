# 1741. Find Total Time Spent by Each Employee

## Table: Employees

| Column Name | Type |
| ----------- | ---- |
| emp_id      | int  |
| event_day   | date |
| in_time     | int  |
| out_time    | int  |

### Notes

- `(emp_id, event_day, in_time)` is the **primary key**.
- The table records employee **entry and exit times** in the office.
- `event_day` → the day the event occurred.
- `in_time` → minute when the employee entered the office.
- `out_time` → minute when the employee left the office.
- `in_time` and `out_time` range from **1 to 1440**.
- It is guaranteed that:
  - Events **do not overlap** on the same day.
  - `in_time < out_time`.

---

# Problem

Calculate the **total time (in minutes)** spent by each employee **per day** in the office.

An employee may **enter and leave multiple times in the same day**.

For a single entry, the time spent is:

```
out_time - in_time
```

Return the result table **in any order**.

---

# Example

## Input

### Employees Table

| emp_id | event_day  | in_time | out_time |
| ------ | ---------- | ------- | -------- |
| 1      | 2020-11-28 | 4       | 32       |
| 1      | 2020-11-28 | 55      | 200      |
| 1      | 2020-12-03 | 1       | 42       |
| 2      | 2020-11-28 | 3       | 33       |
| 2      | 2020-12-09 | 47      | 74       |

---

## Output

| day        | emp_id | total_time |
| ---------- | ------ | ---------- |
| 2020-11-28 | 1      | 173        |
| 2020-11-28 | 2      | 30         |
| 2020-12-03 | 1      | 41         |
| 2020-12-09 | 2      | 27         |

---

# Explanation

### Employee 1

- **2020-11-28**
  - (32 - 4) = 28
  - (200 - 55) = 145
  - Total = **173 minutes**

- **2020-12-03**
  - (42 - 1) = **41 minutes**

### Employee 2

- **2020-11-28**
  - (33 - 3) = **30 minutes**

- **2020-12-09**
  - (74 - 47) = **27 minutes**
