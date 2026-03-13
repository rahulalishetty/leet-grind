# 2356. Number of Unique Subjects Taught by Each Teacher

## Table: Teacher

| Column Name | Type |
| ----------- | ---- |
| teacher_id  | int  |
| subject_id  | int  |
| dept_id     | int  |

### Notes

- `(subject_id, dept_id)` is the **primary key**.
- Each row indicates that a **teacher teaches a subject in a department**.

---

# Problem

Write a SQL query to calculate the **number of unique subjects** each teacher teaches in the university.

Return the result table in **any order**.

---

# Example

## Input

### Teacher Table

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

## Output

| teacher_id | cnt |
| ---------- | --- |
| 1          | 2   |
| 2          | 4   |

---

# Explanation

### Teacher 1

- Teaches **subject 2** in departments **3 and 4**
- Teaches **subject 3** in department **3**

Unique subjects = **2**

### Teacher 2

- Teaches **subject 1**
- Teaches **subject 2**
- Teaches **subject 3**
- Teaches **subject 4**

Unique subjects = **4**
