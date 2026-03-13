# 1873. Calculate Special Bonus

## Table: Employees

| Column Name | Type    |
| ----------- | ------- |
| employee_id | int     |
| name        | varchar |
| salary      | int     |

### Notes

- `employee_id` is the **primary key**.
- Each row represents an employee with their **ID, name, and salary**.

---

# Problem

Calculate the **bonus** for each employee.

Rules:

- If the **employee_id is odd** AND the employee's **name does not start with 'M'**,
  then the bonus is **100% of their salary**.
- Otherwise, the bonus is **0**.

Return the result table **ordered by `employee_id`**.

---

# Example

## Input

### Employees Table

| employee_id | name    | salary |
| ----------- | ------- | ------ |
| 2           | Meir    | 3000   |
| 3           | Michael | 3800   |
| 7           | Addilyn | 7400   |
| 8           | Juan    | 6100   |
| 9           | Kannon  | 7700   |

---

## Output

| employee_id | bonus |
| ----------- | ----- |
| 2           | 0     |
| 3           | 0     |
| 7           | 7400  |
| 8           | 0     |
| 9           | 7700  |

---

# Explanation

- **Employee 2** → even ID → bonus = 0
- **Employee 3** → name starts with 'M' → bonus = 0
- **Employee 7** → odd ID and name does not start with 'M' → bonus = 7400
- **Employee 8** → even ID → bonus = 0
- **Employee 9** → odd ID and name does not start with 'M' → bonus = 7700
