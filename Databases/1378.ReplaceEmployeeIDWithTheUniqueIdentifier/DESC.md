# 1378. Replace Employee ID With The Unique Identifier

## Table: Employees

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |

Notes:

- `id` is the **primary key**.
- Each row represents an **employee in the company**.

---

## Table: EmployeeUNI

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| unique_id   | int  |

Notes:

- `(id, unique_id)` is the **primary key**.
- Each row maps an employee `id` to a **unique identifier**.

---

# Problem

Write a SQL query to display the **unique ID of each employee**.

Requirements:

- If an employee **has a unique ID**, show it.
- If an employee **does not have a unique ID**, show **NULL** instead.
- Return the result table **in any order**.

The result must contain:

- `unique_id`
- `name`

---

# Example

## Input

### Employees table

| id  | name     |
| --- | -------- |
| 1   | Alice    |
| 7   | Bob      |
| 11  | Meir     |
| 90  | Winston  |
| 3   | Jonathan |

### EmployeeUNI table

| id  | unique_id |
| --- | --------- |
| 3   | 1         |
| 11  | 2         |
| 90  | 3         |

---

# Output

| unique_id | name     |
| --------- | -------- |
| NULL      | Alice    |
| NULL      | Bob      |
| 2         | Meir     |
| 3         | Winston  |
| 1         | Jonathan |

---

# Explanation

- **Alice** and **Bob** do not have entries in the `EmployeeUNI` table → `NULL`.
- **Meir** has `unique_id = 2`.
- **Winston** has `unique_id = 3`.
- **Jonathan** has `unique_id = 1`.

Employees without a matching record in `EmployeeUNI` must still appear in the result.
