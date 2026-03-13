# 1077. Project Employees III

## Table: Project

| Column Name | Type |
| ----------- | ---- |
| project_id  | int  |
| employee_id | int  |

- `(project_id, employee_id)` is the **primary key** (unique combination).
- `employee_id` is a **foreign key** referencing the `Employee` table.
- Each row indicates that an employee is working on a specific project.

---

## Table: Employee

| Column Name      | Type    |
| ---------------- | ------- |
| employee_id      | int     |
| name             | varchar |
| experience_years | int     |

- `employee_id` is the **primary key** of this table.
- Each row contains information about a single employee.
- `experience_years` indicates the number of years of work experience.

---

# Problem

Find the **most experienced employees in each project**.

Rules:

- For every `project_id`, determine the **maximum number of experience years** among the employees working on that project.
- Return **all employees** whose `experience_years` equals that maximum.
- If multiple employees have the same maximum experience, **return all of them**.

---

# Output Requirements

Return a table with the following columns:

| Column      |
| ----------- |
| project_id  |
| employee_id |

- The result may be returned **in any order**.

---

# Example

## Input

### Project

| project_id | employee_id |
| ---------- | ----------- |
| 1          | 1           |
| 1          | 2           |
| 1          | 3           |
| 2          | 1           |
| 2          | 4           |

### Employee

| employee_id | name   | experience_years |
| ----------- | ------ | ---------------- |
| 1           | Khaled | 3                |
| 2           | Ali    | 2                |
| 3           | John   | 3                |
| 4           | Doe    | 2                |

---

# Output

| project_id | employee_id |
| ---------- | ----------- |
| 1          | 1           |
| 1          | 3           |
| 2          | 1           |

---

# Explanation

## Project 1

Employees assigned:

```
1 (3 years experience)
2 (2 years experience)
3 (3 years experience)
```

Maximum experience:

```
3 years
```

Employees with that experience:

```
1, 3
```

Both are included.

---

## Project 2

Employees assigned:

```
1 (3 years experience)
4 (2 years experience)
```

Maximum experience:

```
3 years
```

Employee with that experience:

```
1
```

So only employee **1** is included.

---

# Final Result

| project_id | employee_id |
| ---------- | ----------- |
| 1          | 1           |
| 1          | 3           |
| 2          | 1           |
