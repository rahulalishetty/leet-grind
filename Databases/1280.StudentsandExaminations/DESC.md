# 1280. Students and Examinations

## Table: Students

| Column Name  | Type    |
| ------------ | ------- |
| student_id   | int     |
| student_name | varchar |

Notes:

- `student_id` is the **primary key**.
- Each row represents one **student in the school**.

---

## Table: Subjects

| Column Name  | Type    |
| ------------ | ------- |
| subject_name | varchar |

Notes:

- `subject_name` is the **primary key**.
- Each row represents one **subject offered in the school**.

---

## Table: Examinations

| Column Name  | Type    |
| ------------ | ------- |
| student_id   | int     |
| subject_name | varchar |

Notes:

- This table **does not have a primary key**.
- The table **may contain duplicate rows**.
- Each row indicates that a student **attended the exam of a specific subject**.

---

# Problem

Write a SQL query to determine **how many times each student attended each exam**.

Requirements:

- The result must include **every student and every subject**.
- If a student did **not attend an exam**, the count should be **0**.
- The result must be **ordered by**:

```
student_id
subject_name
```

---

# Example

## Input

### Students table

| student_id | student_name |
| ---------- | ------------ |
| 1          | Alice        |
| 2          | Bob          |
| 13         | John         |
| 6          | Alex         |

### Subjects table

| subject_name |
| ------------ |
| Math         |
| Physics      |
| Programming  |

### Examinations table

| student_id | subject_name |
| ---------- | ------------ |
| 1          | Math         |
| 1          | Physics      |
| 1          | Programming  |
| 2          | Programming  |
| 1          | Physics      |
| 1          | Math         |
| 13         | Math         |
| 13         | Programming  |
| 13         | Physics      |
| 2          | Math         |
| 1          | Math         |

---

# Output

| student_id | student_name | subject_name | attended_exams |
| ---------- | ------------ | ------------ | -------------- |
| 1          | Alice        | Math         | 3              |
| 1          | Alice        | Physics      | 2              |
| 1          | Alice        | Programming  | 1              |
| 2          | Bob          | Math         | 1              |
| 2          | Bob          | Physics      | 0              |
| 2          | Bob          | Programming  | 1              |
| 6          | Alex         | Math         | 0              |
| 6          | Alex         | Physics      | 0              |
| 6          | Alex         | Programming  | 0              |
| 13         | John         | Math         | 1              |
| 13         | John         | Physics      | 1              |
| 13         | John         | Programming  | 1              |

---

# Explanation

The result must contain **every combination of student and subject**.

For each pair:

```
(student_id, subject_name)
```

we count how many times the student attended that exam.

Examples:

### Alice

- Math → 3 times
- Physics → 2 times
- Programming → 1 time

### Bob

- Math → 1 time
- Physics → 0 times
- Programming → 1 time

### Alex

- Did not attend any exams → all counts are **0**.

### John

- Attended each subject **once**.
