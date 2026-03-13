# 1709. Biggest Window Between Visits

## Table: UserVisits

| Column Name | Type |
| ----------- | ---- |
| user_id     | int  |
| visit_date  | date |

**Notes:**

- This table **does not have a primary key**, so duplicate rows may exist.
- Each row records a **date when a user visited a retailer**.

---

## Problem

Assume today's date is:

```
2021-01-01
```

For each `user_id`, compute the **largest window of days between visits**.

The window is defined as:

- The difference between a visit date and the **next visit date**, or
- The difference between the **last visit date and today (2021‑01‑01)**

Return the result table **ordered by `user_id`**.

---

## Example

### Input

#### UserVisits Table

| user_id | visit_date |
| ------- | ---------- |
| 1       | 2020-11-28 |
| 1       | 2020-10-20 |
| 1       | 2020-12-03 |
| 2       | 2020-10-05 |
| 2       | 2020-12-09 |
| 3       | 2020-11-11 |

---

## Output

| user_id | biggest_window |
| ------- | -------------- |
| 1       | 39             |
| 2       | 65             |
| 3       | 51             |

---

## Explanation

### User 1

Visits:

| visit_date |
| ---------- |
| 2020-10-20 |
| 2020-11-28 |
| 2020-12-03 |

Windows between visits:

| From       | To         | Days |
| ---------- | ---------- | ---- |
| 2020‑10‑20 | 2020‑11‑28 | 39   |
| 2020‑11‑28 | 2020‑12‑03 | 5    |
| 2020‑12‑03 | 2021‑01‑01 | 29   |

Largest window:

```
39 days
```

---

### User 2

Visits:

| visit_date |
| ---------- |
| 2020‑10‑05 |
| 2020‑12‑09 |

Windows:

| From       | To         | Days |
| ---------- | ---------- | ---- |
| 2020‑10‑05 | 2020‑12‑09 | 65   |
| 2020‑12‑09 | 2021‑01‑01 | 23   |

Largest window:

```
65 days
```

---

### User 3

Visits:

| visit_date |
| ---------- |
| 2020‑11‑11 |

Window:

| From       | To         | Days |
| ---------- | ---------- | ---- |
| 2020‑11‑11 | 2021‑01‑01 | 51   |

Largest window:

```
51 days
```
