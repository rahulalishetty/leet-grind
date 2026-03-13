# 570. Managers with at Least 5 Direct Reports

## Problem Description

**Table: Employee**

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |
| department  | varchar |
| managerId   | int     |

- `id` is the **primary key** (unique identifier).
- Each row represents an employee.
- `managerId` indicates the **manager of that employee**.
- If `managerId` is `NULL`, the employee **does not have a manager**.
- No employee will manage themselves.

---

## Objective

Write a SQL query to **find managers who have at least five direct reports**.

A **direct report** means an employee whose `managerId` equals the manager's `id`.

Return the result table **in any order**.

---

## Example

### Input

**Employee Table**

| id  | name  | department | managerId |
| --- | ----- | ---------- | --------- |
| 101 | John  | A          | NULL      |
| 102 | Dan   | A          | 101       |
| 103 | James | A          | 101       |
| 104 | Amy   | A          | 101       |
| 105 | Anne  | A          | 101       |
| 106 | Ron   | B          | 101       |

---

### Output

| name |
| ---- |
| John |

---
