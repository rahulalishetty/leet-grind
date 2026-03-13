# Approach: GROUP BY and CROSS JOIN

## Algorithm

### Step 1 — Count Exams per Student per Subject

First, we aggregate the **Examinations** table to count how many times each student attended each subject.

```sql
SELECT
    student_id,
    subject_name,
    COUNT(*) AS attended_exams
FROM Examinations
GROUP BY student_id, subject_name;
```

Example intermediate result:

| student_id | subject_name | attended_exams |
| ---------- | ------------ | -------------- |
| 1          | Math         | 3              |
| 1          | Physics      | 2              |
| 1          | Programming  | 1              |
| 2          | Programming  | 1              |
| 13         | Math         | 1              |
| 13         | Programming  | 1              |
| 13         | Physics      | 1              |
| 2          | Math         | 1              |

This table only contains **existing exam records**.

However, the final result must include **all student–subject combinations**, even if a student never attended that exam.

---

# Step 2 — Generate All Student–Subject Combinations

To generate all possible `(student_id, subject_name)` pairs, we use a **CROSS JOIN** between:

- `Students`
- `Subjects`

```sql
SELECT *
FROM Students s
CROSS JOIN Subjects sub;
```

Result example:

| student_id | student_name | subject_name |
| ---------- | ------------ | ------------ |
| 1          | Alice        | Programming  |
| 1          | Alice        | Physics      |
| 1          | Alice        | Math         |
| 2          | Bob          | Programming  |
| 2          | Bob          | Physics      |
| 2          | Bob          | Math         |
| 13         | John         | Programming  |
| 13         | John         | Physics      |
| 13         | John         | Math         |
| 6          | Alex         | Programming  |
| 6          | Alex         | Physics      |
| 6          | Alex         | Math         |

This ensures every student appears with every subject.

---

# Step 3 — Join the Aggregated Exam Counts

Next, we **LEFT JOIN** the aggregated exam counts with the full student–subject combinations.

The join condition is:

```
(student_id, subject_name)
```

This allows us to keep all combinations while attaching the exam counts where they exist.

---

# Step 4 — Replace NULL Values

For combinations where a student never attended the exam, the count will be `NULL`.

We convert these values to `0` using:

```
IFNULL()
```

---

# Final SQL Query

```sql
SELECT
    s.student_id,
    s.student_name,
    sub.subject_name,
    IFNULL(grouped.attended_exams, 0) AS attended_exams
FROM Students s
CROSS JOIN Subjects sub
LEFT JOIN (
    SELECT
        student_id,
        subject_name,
        COUNT(*) AS attended_exams
    FROM Examinations
    GROUP BY student_id, subject_name
) grouped
ON s.student_id = grouped.student_id
AND sub.subject_name = grouped.subject_name
ORDER BY s.student_id, sub.subject_name;
```

---

# Key Concepts

- **CROSS JOIN** generates all possible student–subject combinations.
- **GROUP BY** aggregates exam counts.
- **LEFT JOIN** preserves combinations with no exam attendance.
- **IFNULL()** converts missing values to `0`.
