# 1112. Highest Grade For Each Student

## Table: Enrollments

| Column Name | Type |
| ----------- | ---- |
| student_id  | int  |
| course_id   | int  |
| grade       | int  |

- `(student_id, course_id)` is the **primary key** (unique combination).
- `grade` is **never NULL**.
- Each row represents a student’s grade in a specific course.

---

# Problem

Find the **highest grade obtained by each student** along with the **course in which it was achieved**.

### Tie-breaking rule

If a student has the **same highest grade in multiple courses**, return the course with the **smallest `course_id`**.

---

# Output Requirements

Return the result table with the following columns:

| Column     |
| ---------- |
| student_id |
| course_id  |
| grade      |

The result must be **ordered by `student_id` in ascending order**.

---

# Example

## Input

### Enrollments

| student_id | course_id | grade |
| ---------- | --------- | ----- |
| 2          | 2         | 95    |
| 2          | 3         | 95    |
| 1          | 1         | 90    |
| 1          | 2         | 99    |
| 3          | 1         | 80    |
| 3          | 2         | 75    |
| 3          | 3         | 82    |

---

# Output

| student_id | course_id | grade |
| ---------- | --------- | ----- |
| 1          | 2         | 99    |
| 2          | 2         | 95    |
| 3          | 3         | 82    |

---

# Explanation

### Student 1

Courses taken:

```
Course 1 -> Grade 90
Course 2 -> Grade 99
```

Highest grade:

```
99
```

Course with that grade:

```
Course 2
```

Result:

```
(1, 2, 99)
```

---

### Student 2

Courses taken:

```
Course 2 -> Grade 95
Course 3 -> Grade 95
```

Both courses have the **same highest grade (95)**.

Tie-break rule:

```
choose smallest course_id
```

So we select:

```
Course 2
```

Result:

```
(2, 2, 95)
```

---

### Student 3

Courses taken:

```
Course 1 -> Grade 80
Course 2 -> Grade 75
Course 3 -> Grade 82
```

Highest grade:

```
82
```

Course with that grade:

```
Course 3
```

Result:

```
(3, 3, 82)
```

---

# Final Result

| student_id | course_id | grade |
| ---------- | --------- | ----- |
| 1          | 2         | 99    |
| 2          | 2         | 95    |
| 3          | 3         | 82    |
