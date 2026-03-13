# 596. Classes With at Least 5 Students

## Table: Courses

| Column Name | Type    |
| ----------- | ------- |
| student     | varchar |
| class       | varchar |

- `(student, class)` is the **primary key** (a combination of columns with unique values).
- Each row indicates that a **student is enrolled in a class**.

---

## Problem

Write a SQL query to find all the **classes that have at least five students**.

Return the result table in **any order**.

---

## Example

### Input

#### Courses table

| student | class    |
| ------- | -------- |
| A       | Math     |
| B       | English  |
| C       | Math     |
| D       | Biology  |
| E       | Math     |
| F       | Computer |
| G       | Math     |
| H       | Math     |
| I       | Math     |

---

### Output

| class |
| ----- |
| Math  |

---

## Explanation

- **Math** has **6 students**, so it is included.
- **English** has **1 student**, so it is not included.
- **Biology** has **1 student**, so it is not included.
- **Computer** has **1 student**, so it is not included.

Only **Math** satisfies the condition of having **at least 5 students**.
