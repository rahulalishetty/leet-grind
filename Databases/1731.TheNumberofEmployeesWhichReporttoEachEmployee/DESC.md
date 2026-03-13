# 1731. The Number of Employees Which Report to Each Employee

## Table: Employees

| Column Name | Type    |
| ----------- | ------- |
| employee_id | int     |
| name        | varchar |
| reports_to  | int     |
| age         | int     |

### Notes

- `employee_id` has **unique values**.
- `reports_to` stores the **employee_id of the manager**.
- Some employees do **not report to anyone** (`reports_to = NULL`).

---

# Problem

For this problem, a **manager** is defined as an employee who has **at least one direct report**.

Write a SQL query to report:

- `employee_id`
- `name`
- `reports_count` → number of employees who **directly report** to the manager
- `average_age` → **average age of the direct reports**, rounded to the **nearest integer**

Return the result table **ordered by `employee_id`**.

---

# Example 1

## Input

### Employees

| employee_id | name    | reports_to | age |
| ----------- | ------- | ---------- | --- |
| 9           | Hercy   | null       | 43  |
| 6           | Alice   | 9          | 41  |
| 4           | Bob     | 9          | 36  |
| 2           | Winston | null       | 37  |

---

## Output

| employee_id | name  | reports_count | average_age |
| ----------- | ----- | ------------- | ----------- |
| 9           | Hercy | 2             | 39          |

---

## Explanation

Hercy manages **Alice** and **Bob**.

Average age:

```
(41 + 36) / 2 = 38.5
```

Rounded to nearest integer:

```
39
```

---

# Example 2

## Input

### Employees

| employee_id | name    | reports_to | age |
| ----------- | ------- | ---------- | --- |
| 1           | Michael | null       | 45  |
| 2           | Alice   | 1          | 38  |
| 3           | Bob     | 1          | 42  |
| 4           | Charlie | 2          | 34  |
| 5           | David   | 2          | 40  |
| 6           | Eve     | 3          | 37  |
| 7           | Frank   | null       | 50  |
| 8           | Grace   | null       | 48  |

---

## Output

| employee_id | name    | reports_count | average_age |
| ----------- | ------- | ------------- | ----------- |
| 1           | Michael | 2             | 40          |
| 2           | Alice   | 2             | 37          |
| 3           | Bob     | 1             | 37          |

---

# Explanation

- **Michael** manages Alice and Bob
  - Average age = (38 + 42) / 2 = **40**

- **Alice** manages Charlie and David
  - Average age = (34 + 40) / 2 = **37**

- **Bob** manages Eve
  - Average age = **37**
