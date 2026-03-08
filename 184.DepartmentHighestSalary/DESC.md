# 184. Department Highest Salary

## Table: Employee

| Column Name  | Type    |
| ------------ | ------- |
| id           | int     |
| name         | varchar |
| salary       | int     |
| departmentId | int     |

- `id` is the primary key for this table.
- `departmentId` is a foreign key referencing the `id` column in the Department table.
- Each row represents an employee's ID, name, salary, and their department ID.

## Table: Department

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |

- `id` is the primary key for this table.
- Each row represents a department's ID and name. The department name is guaranteed to be non-NULL.

## Problem Statement

Write a solution to find employees who have the highest salary in each department.

### Return Format

The result table should include the following columns:

- `Department`: The name of the department.
- `Employee`: The name of the employee with the highest salary in that department.
- `Salary`: The highest salary in that department.

The result can be returned in any order.

### Example

#### Input

**Employee Table**:

| id  | name  | salary | departmentId |
| --- | ----- | ------ | ------------ |
| 1   | Joe   | 70000  | 1            |
| 2   | Jim   | 90000  | 1            |
| 3   | Henry | 80000  | 2            |
| 4   | Sam   | 60000  | 2            |
| 5   | Max   | 90000  | 1            |

**Department Table**:

| id  | name  |
| --- | ----- |
| 1   | IT    |
| 2   | Sales |

#### Output

| Department | Employee | Salary |
| ---------- | -------- | ------ |
| IT         | Jim      | 90000  |
| Sales      | Henry    | 80000  |
| IT         | Max      | 90000  |

#### Explanation

- Jim and Max both have the highest salary in the IT department.
- Henry has the highest salary in the Sales department.
