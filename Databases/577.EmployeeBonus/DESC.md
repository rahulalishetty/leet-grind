# 577. Employee Bonus

## Problem

### Table: Employee

| Column Name | Type    |
| ----------- | ------- |
| empId       | int     |
| name        | varchar |
| supervisor  | int     |
| salary      | int     |

- `empId` is the **primary key** (unique).
- Each row represents an employee.
- `supervisor` stores the employee ID of the manager.
- `salary` stores the employee's salary.

---

### Table: Bonus

| Column Name | Type |
| ----------- | ---- |
| empId       | int  |
| bonus       | int  |

- `empId` is the **primary key** for this table.
- `empId` is a **foreign key referencing `Employee.empId`**.
- Each row contains the bonus amount for an employee.

---

## Goal

Report the **name and bonus amount** of each employee who satisfies **either** of the following:

1. The employee has a **bonus less than 1000**
2. The employee **did not receive any bonus**

The result can be returned **in any order**.

---

# Example

## Input

### Employee table

| empId | name   | supervisor | salary |
| ----- | ------ | ---------- | ------ |
| 3     | Brad   | NULL       | 4000   |
| 1     | John   | 3          | 1000   |
| 2     | Dan    | 3          | 2000   |
| 4     | Thomas | 3          | 4000   |

### Bonus table

| empId | bonus |
| ----- | ----- |
| 2     | 500   |
| 4     | 2000  |

---

## Output

| name | bonus |
| ---- | ----- |
| Brad | NULL  |
| John | NULL  |
| Dan  | 500   |

---

## Explanation

- **Brad** → has **no bonus record**, so bonus = `NULL` → included
- **John** → has **no bonus record**, so bonus = `NULL` → included
- **Dan** → bonus = **500**, which is **less than 1000** → included
- **Thomas** → bonus = **2000**, which is **greater than 1000** → excluded

Thus the final result contains:

- employees with **bonus < 1000**
- employees with **no bonus**
