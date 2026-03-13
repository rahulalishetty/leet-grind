# 1075. Project Employees I

## Table: Project

| Column Name | Type |
| ----------- | ---- |
| project_id  | int  |
| employee_id | int  |

### Notes

- `(project_id, employee_id)` is the **primary key**.
- `employee_id` is a **foreign key** referencing the `Employee` table.
- Each row indicates that a specific employee works on a specific project.

---

## Table: Employee

| Column Name      | Type    |
| ---------------- | ------- |
| employee_id      | int     |
| name             | varchar |
| experience_years | int     |

### Notes

- `employee_id` is the **primary key**.
- `experience_years` represents the number of years of experience an employee has.
- It is guaranteed that `experience_years` is **not NULL**.

---

# Problem

Write an SQL query to report the **average experience years of all employees for each project**.

Requirements:

- Calculate the **average of `experience_years`** for employees assigned to each project.
- Round the result to **2 decimal places**.
- Return the result table **in any order**.

---

# Output Format

| project_id | average_years |

Where:

- `project_id` → the project identifier
- `average_years` → average experience of employees working on that project

---

# Example

## Input

### Project Table

| project_id | employee_id |
| ---------- | ----------- |
| 1          | 1           |
| 1          | 2           |
| 1          | 3           |
| 2          | 1           |
| 2          | 4           |

### Employee Table

| employee_id | name   | experience_years |
| ----------- | ------ | ---------------- |
| 1           | Khaled | 3                |
| 2           | Ali    | 2                |
| 3           | John   | 1                |
| 4           | Doe    | 2                |

---

# Explanation

### Project 1

Employees:

```
1 → 3 years
2 → 2 years
3 → 1 year
```

Average:

```
(3 + 2 + 1) / 3 = 2.00
```

---

### Project 2

Employees:

```
1 → 3 years
4 → 2 years
```

Average:

```
(3 + 2) / 2 = 2.50
```

---

# Output

| project_id | average_years |
| ---------- | ------------- |
| 1          | 2.00          |
| 2          | 2.50          |

---

# Summary

The task requires:

- joining the **Project** and **Employee** tables using `employee_id`
- grouping by `project_id`
- calculating the **average experience years per project**
- rounding the result to **2 decimal places**
