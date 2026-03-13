# 1965. Employees With Missing Information — Approaches

## Approach 1: Simulate FULL JOIN via UNION of LEFT and RIGHT JOIN

### Overview

Some SQL databases do not support `FULL JOIN`.
To include all records from both tables (`Employees` and `Salaries`), we can simulate a full outer join by combining:

- `LEFT JOIN`
- `RIGHT JOIN`

Then merging their results using `UNION`.

This ensures that all employee IDs from both tables are included, with `NULL` values where information is missing.

---

## Intuition

### Step 1 — LEFT JOIN

```
SELECT *
FROM Employees
LEFT JOIN Salaries USING(employee_id)
```

- Returns all rows from **Employees**
- If an employee does not have a salary record, `salary` will be `NULL`

---

### Step 2 — RIGHT JOIN

```
SELECT *
FROM Employees
RIGHT JOIN Salaries USING(employee_id)
```

- Returns all rows from **Salaries**
- If an employee does not exist in Employees, `name` will be `NULL`

---

### Step 3 — Combine Results with UNION

```
UNION
```

- Combines both result sets
- Removes duplicate rows
- Effectively simulates a **FULL OUTER JOIN**

---

### Step 4 — Filter Missing Information

We only want rows where:

- `salary` is `NULL`, or
- `name` is `NULL`

```
WHERE salary IS NULL OR name IS NULL
```

---

### Step 5 — Sort the Results

```
ORDER BY employee_id
```

---

## SQL Implementation

```sql
SELECT
  T.employee_id
FROM
(
    SELECT *
    FROM Employees
    LEFT JOIN Salaries USING(employee_id)

    UNION

    SELECT *
    FROM Employees
    RIGHT JOIN Salaries USING(employee_id)
) AS T
WHERE
  T.salary IS NULL
  OR T.name IS NULL
ORDER BY
  employee_id;
```

---

# Approach 2: UNION with `WHERE ... NOT IN`

### Overview

This approach compares the two tables directly and finds employee IDs present in one table but missing in the other.

Two checks are performed:

1. Employees missing **salary**
2. Employees missing **name**

The results are then merged using `UNION`.

---

## Intuition

### Step 1 — Employees Missing Salary

```
SELECT employee_id
FROM Employees
WHERE employee_id NOT IN (
    SELECT employee_id FROM Salaries
)
```

Explanation:

- Finds employees present in `Employees`
- But **not present** in `Salaries`
- These employees are missing salary information

---

### Step 2 — Employees Missing Name

```
SELECT employee_id
FROM Salaries
WHERE employee_id NOT IN (
    SELECT employee_id FROM Employees
)
```

Explanation:

- Finds employees present in `Salaries`
- But **not present** in `Employees`
- These employees are missing name information

---

### Step 3 — Combine Results

```
UNION
```

- Combines both queries
- Removes duplicates

---

### Step 4 — Sort Output

```
ORDER BY employee_id ASC
```

---

## SQL Implementation

```sql
SELECT employee_id
FROM Employees
WHERE employee_id NOT IN (
    SELECT employee_id FROM Salaries
)

UNION

SELECT employee_id
FROM Salaries
WHERE employee_id NOT IN (
    SELECT employee_id FROM Employees
)

ORDER BY employee_id ASC;
```

---

# Key SQL Concepts Used

- `LEFT JOIN`
- `RIGHT JOIN`
- `UNION`
- `NOT IN`
- Simulating `FULL JOIN`
- Filtering `NULL` values
