# 601. Human Traffic of Stadium

## Table: Stadium

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| visit_date  | date |
| people      | int  |

- `visit_date` contains **unique values**.
- Each row records a **stadium visit** on a specific date.
- `people` represents the **number of visitors** during that visit.
- As `id` increases, the `visit_date` also increases.

---

# Problem

Write a SQL query to display **records that belong to groups of at least three consecutive `id` values** where:

```
people >= 100
```

### Requirements

1. The records must have **three or more consecutive ids**.
2. Each of those records must have **at least 100 people**.
3. Return the results **ordered by `visit_date` in ascending order**.

---

# Example

## Input

### Stadium table

| id  | visit_date | people |
| --- | ---------- | ------ |
| 1   | 2017-01-01 | 10     |
| 2   | 2017-01-02 | 109    |
| 3   | 2017-01-03 | 150    |
| 4   | 2017-01-04 | 99     |
| 5   | 2017-01-05 | 145    |
| 6   | 2017-01-06 | 1455   |
| 7   | 2017-01-07 | 199    |
| 8   | 2017-01-09 | 188    |

---

## Output

| id  | visit_date | people |
| --- | ---------- | ------ |
| 5   | 2017-01-05 | 145    |
| 6   | 2017-01-06 | 1455   |
| 7   | 2017-01-07 | 199    |
| 8   | 2017-01-09 | 188    |

---

# Explanation

To qualify for the result:

- The rows must have **three or more consecutive `id` values**.
- Each row must have **`people >= 100`**.

Looking at the table:

- Rows **2 and 3** both have ≥100 people, but they form only **two consecutive ids**, which is **not enough**.
- Row **4** has fewer than 100 people, which breaks any sequence.

The rows **5, 6, 7, and 8**:

- Have **consecutive ids**
- Each has **≥100 people**

So they form a valid sequence of at least three consecutive qualifying rows.

Note:

Even though the `visit_date` of row **8** is not the next day after row **7**, it is still included because the condition is based on **consecutive `id` values**, not consecutive dates.

---
