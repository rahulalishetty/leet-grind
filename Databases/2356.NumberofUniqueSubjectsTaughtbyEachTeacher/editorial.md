# 2356. Number of Unique Subjects Taught by Each Teacher — Approach

## Approach: GROUP BY and COUNT DISTINCT

### Intuition

To determine how many **unique subjects each teacher teaches**, we focus on the `subject_id` column.

However, a teacher may teach the **same subject in multiple departments**. Since we only care about **unique subjects**, we must ensure duplicates are not counted.

SQL provides the function:

COUNT(DISTINCT column_name)

which counts only **unique values** in a column.

---

# Algorithm

1. **Group the table by `teacher_id`**

   This groups all rows belonging to the same teacher.

2. **Count unique subjects for each teacher**

   Use:

   COUNT(DISTINCT subject_id)

   This ensures that even if the same subject appears in multiple departments, it is counted **only once**.

---

# Example

### Original Table

| teacher_id | subject_id | dept_id |
| ---------- | ---------- | ------- |
| 1          | 2          | 3       |
| 1          | 2          | 4       |
| 1          | 3          | 3       |
| 2          | 1          | 1       |
| 2          | 2          | 1       |
| 2          | 3          | 1       |
| 2          | 4          | 1       |

---

### After Grouping and Counting Distinct Subjects

| teacher_id | cnt |
| ---------- | --- |
| 1          | 2   |
| 2          | 4   |

Explanation:

- **Teacher 1**
  - Subject 2 (taught in dept 3 and 4 → counted once)
  - Subject 3
  - Total unique subjects = **2**

- **Teacher 2**
  - Subjects 1, 2, 3, 4
  - Total unique subjects = **4**

---

# SQL Implementation

```sql
SELECT
    teacher_id,
    COUNT(DISTINCT subject_id) AS cnt
FROM
    Teacher
GROUP BY
    teacher_id;
```

---

# Key SQL Concepts Used

- `GROUP BY`
- `COUNT(DISTINCT ...)`
- Aggregation functions
