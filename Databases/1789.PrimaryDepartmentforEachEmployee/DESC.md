# 1789. Primary Department for Each Employee

## Table: Employee

| Column Name   | Type    |
| ------------- | ------- |
| employee_id   | int     |
| department_id | int     |
| primary_flag  | varchar |

### Notes

- `(employee_id, department_id)` is the **primary key**.
- `employee_id` represents the employee.
- `department_id` represents the department the employee belongs to.
- `primary_flag` is an ENUM with values:
  - `'Y'` → this is the **primary department**
  - `'N'` → this is **not the primary department**

---

# Problem

Employees can belong to **multiple departments**.

When an employee belongs to multiple departments, one of them will be marked with:

```
primary_flag = 'Y'
```

If an employee belongs to **only one department**, the flag will be:

```
primary_flag = 'N'
```

Even though it is `'N'`, that department should still be considered the **primary department** because it is the **only department**.

---

# Task

Report:

- `employee_id`
- `department_id` (their **primary department**)

Rules:

- If `primary_flag = 'Y'`, return that department.
- If the employee belongs to **only one department**, return that department.

Return the result table **in any order**.

---

# Example

## Input

### Employee Table

| employee_id | department_id | primary_flag |
| ----------- | ------------- | ------------ |
| 1           | 1             | N            |
| 2           | 1             | Y            |
| 2           | 2             | N            |
| 3           | 3             | N            |
| 4           | 2             | N            |
| 4           | 3             | Y            |
| 4           | 4             | N            |

---

## Output

| employee_id | department_id |
| ----------- | ------------- |
| 1           | 1             |
| 2           | 1             |
| 3           | 3             |
| 4           | 3             |

---

# Explanation

- **Employee 1** → only one department → **1**
- **Employee 2** → primary_flag = 'Y' for department **1**
- **Employee 3** → only one department → **3**
- **Employee 4** → primary_flag = 'Y' for department **3**
