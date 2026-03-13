# 1965. Employees With Missing Information

## Table: Employees

| Column Name | Type    |
| ----------- | ------- |
| employee_id | int     |
| name        | varchar |

### Notes

- `employee_id` has **unique values**.
- Each row represents an employee and their name.

---

## Table: Salaries

| Column Name | Type |
| ----------- | ---- |
| employee_id | int  |
| salary      | int  |

### Notes

- `employee_id` has **unique values**.
- Each row represents the **salary of an employee**.

---

# Problem

Write a SQL query to report the **IDs of all employees with missing information**.

Information is considered **missing** if:

1. The employee's **name is missing** (exists in `Salaries` but not in `Employees`).
2. The employee's **salary is missing** (exists in `Employees` but not in `Salaries`).

Return the result table **ordered by `employee_id` in ascending order**.

---

# Example

## Input

### Employees Table

| employee_id | name     |
| ----------- | -------- |
| 2           | Crew     |
| 4           | Haven    |
| 5           | Kristian |

### Salaries Table

| employee_id | salary |
| ----------- | ------ |
| 5           | 76071  |
| 1           | 22517  |
| 4           | 63539  |

---

## Output

| employee_id |
| ----------- |
| 1           |
| 2           |

---

# Explanation

Employees working in the company: **1, 2, 4, 5**

- **Employee 1** → salary exists but **name missing**
- **Employee 2** → name exists but **salary missing**
- **Employee 4** → complete information
- **Employee 5** → complete information
