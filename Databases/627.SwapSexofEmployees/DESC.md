# 627. Swap Sex of Employees

## Table: Salary

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |
| sex         | ENUM    |
| salary      | int     |

### Notes

- `id` is the **primary key**.
- `sex` is an ENUM with values:
  - `'m'` → male
  - `'f'` → female
- The table stores information about employees.

---

# Problem

Write a **single UPDATE statement** that swaps the values:

- `'m'` → `'f'`
- `'f'` → `'m'`

Constraints:

- You **must use only one UPDATE statement**.
- **No temporary tables** are allowed.
- **No SELECT statements** should be used.

---

# Example

## Input

### Salary Table

| id  | name | sex | salary |
| --- | ---- | --- | ------ |
| 1   | A    | m   | 2500   |
| 2   | B    | f   | 1500   |
| 3   | C    | m   | 5500   |
| 4   | D    | f   | 500    |

---

# Output

| id  | name | sex | salary |
| --- | ---- | --- | ------ |
| 1   | A    | f   | 2500   |
| 2   | B    | m   | 1500   |
| 3   | C    | f   | 5500   |
| 4   | D    | m   | 500    |

---

# Explanation

Rows updated:

- `(1, A)` and `(3, C)` were changed from **'m' → 'f'**
- `(2, B)` and `(4, D)` were changed from **'f' → 'm'**

The operation swaps the gender value for every row in the table.
