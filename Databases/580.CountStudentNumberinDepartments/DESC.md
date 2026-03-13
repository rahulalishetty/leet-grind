# 580. Count Student Number in Departments

## Table: Student

| Column Name  | Type    |
| ------------ | ------- |
| student_id   | int     |
| student_name | varchar |
| gender       | varchar |
| dept_id      | int     |

- `student_id` is the **primary key** (unique values).
- `dept_id` is a **foreign key** referencing `dept_id` in the `Department` table.
- Each row represents a student and the department they belong to.

---

## Table: Department

| Column Name | Type    |
| ----------- | ------- |
| dept_id     | int     |
| dept_name   | varchar |

- `dept_id` is the **primary key**.
- Each row represents a department.

---

## Problem

Write a SQL query to report:

- The **department name**
- The **number of students** majoring in that department

### Requirements

1. Include **all departments**, even those with **no students**.
2. Order the result by:
   - `student_number` **descending**
   - If there is a tie, sort by `dept_name` **alphabetically**.

---

## Example

### Input

#### Student table

| student_id | student_name | gender | dept_id |
| ---------- | ------------ | ------ | ------- |
| 1          | Jack         | M      | 1       |
| 2          | Jane         | F      | 1       |
| 3          | Mark         | M      | 2       |

#### Department table

| dept_id | dept_name   |
| ------- | ----------- |
| 1       | Engineering |
| 2       | Science     |
| 3       | Law         |

---

### Output

| dept_name   | student_number |
| ----------- | -------------- |
| Engineering | 2              |
| Science     | 1              |
| Law         | 0              |
