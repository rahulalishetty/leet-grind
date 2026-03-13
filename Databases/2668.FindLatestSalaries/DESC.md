# 2668. Find Latest Salaries

## Table: Salary

| Column Name   | Type    |
| ------------- | ------- |
| emp_id        | int     |
| firstname     | varchar |
| lastname      | varchar |
| salary        | varchar |
| department_id | varchar |

### Notes

- `(emp_id, salary)` is the **primary key**.
- The table contains **multiple salary records for the same employee**.
- Some records are **old salary records**, while others represent the **latest salary**.

---

# Problem

Write a SQL query to find the **current salary of each employee**.

Assumption:

- **Salaries increase every year**, therefore the **maximum salary value for an employee is their latest salary**.

Return the result table ordered by **emp_id in ascending order**.

---

# Example

## Input

### Salary Table

| emp_id | firstname | lastname | salary | department_id |
| ------ | --------- | -------- | ------ | ------------- |
| 1      | Todd      | Wilson   | 110000 | D1006         |
| 1      | Todd      | Wilson   | 106119 | D1006         |
| 2      | Justin    | Simon    | 128922 | D1005         |
| 2      | Justin    | Simon    | 130000 | D1005         |
| 3      | Kelly     | Rosario  | 42689  | D1002         |
| 4      | Patricia  | Powell   | 162825 | D1004         |
| 4      | Patricia  | Powell   | 170000 | D1004         |
| 5      | Sherry    | Golden   | 44101  | D1002         |
| 6      | Natasha   | Swanson  | 79632  | D1005         |
| 6      | Natasha   | Swanson  | 90000  | D1005         |

---

## Output

| emp_id | firstname | lastname | salary | department_id |
| ------ | --------- | -------- | ------ | ------------- |
| 1      | Todd      | Wilson   | 110000 | D1006         |
| 2      | Justin    | Simon    | 130000 | D1005         |
| 3      | Kelly     | Rosario  | 42689  | D1002         |
| 4      | Patricia  | Powell   | 170000 | D1004         |
| 5      | Sherry    | Golden   | 44101  | D1002         |
| 6      | Natasha   | Swanson  | 90000  | D1005         |

---

# Explanation

- **Employee 1**
  - Salaries: 110000, 106119
  - Latest salary = **110000**

- **Employee 2**
  - Salaries: 128922, 130000
  - Latest salary = **130000**

- **Employee 3**
  - Only one salary record → **42689**

- **Employee 4**
  - Salaries: 162825, 170000
  - Latest salary = **170000**

- **Employee 5**
  - Only one salary record → **44101**

- **Employee 6**
  - Salaries: 79632, 90000
  - Latest salary = **90000**
