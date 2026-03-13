# 1873. Calculate Special Bonus — Approach

## Approach: IF Statement

### Algorithm

In SQL, we can use the **conditional function `IF()`** to return different values depending on a condition.

Syntax:

```
IF(condition, value_if_true, value_if_false)
```

---

## Condition Logic

Two conditions must be satisfied for an employee to receive the bonus:

1. **Employee ID must be odd**

```
employee_id % 2 = 1
```

This checks if the employee ID is odd.

---

2. **Employee name must NOT start with 'M'**

```
name NOT REGEXP '^M'
```

Explanation:

- `REGEXP` is used for **pattern matching**.
- `^M` means the string **starts with 'M'**.
- `NOT REGEXP '^M'` ensures the name **does not start with 'M'**.

---

## Bonus Calculation

If both conditions are satisfied:

```
salary
```

Otherwise:

```
0
```

Combined condition:

```
IF(employee_id % 2 = 1 AND name NOT REGEXP '^M', salary, 0)
```

---

## SQL Implementation

```sql
SELECT
    employee_id,
    IF(employee_id % 2 = 1 AND name NOT REGEXP '^M', salary, 0) AS bonus
FROM employees
ORDER BY employee_id;
```

---

## Example Result

| employee_id | bonus |
| ----------- | ----- |
| 2           | 0     |
| 3           | 0     |
| 7           | 7400  |
| 8           | 0     |
| 9           | 7700  |

---

## Key SQL Concepts Used

- **IF() conditional function**
- **Modulo operator (`%`)** to check odd numbers
- **Regular Expressions (`REGEXP`)**
- **ORDER BY** for sorting results
