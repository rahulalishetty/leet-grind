# 2668. Find Latest Salaries — Approach

## Approach: GROUP BY with MAX Function

### Intuition

The problem states that **employee salaries increase every year**.
Therefore, the **largest salary value for each employee** represents the **most recent (latest) salary**.

To determine this in SQL:

1. **Group rows by employee (`emp_id`)**
2. **Use the `MAX()` aggregation function** to obtain the highest salary for each employee.

---

# Algorithm

### 1. Group Records by Employee

We group the table using:

```
GROUP BY emp_id
```

This ensures that all salary records belonging to the **same employee** are considered together.

---

### 2. Find the Latest Salary

Because salaries increase every year, the **maximum salary value** for each employee represents the **latest salary**.

We use:

```
MAX(salary)
```

This returns the **largest salary value within each employee group**.

---

### 3. Return Employee Details

The result must include:

- `emp_id`
- `firstname`
- `lastname`
- `salary`
- `department_id`

The salary returned is the **maximum salary per employee**.

---

### 4. Order the Result

The output must be ordered by:

```
emp_id ASC
```

Ascending order is the **default behavior** of `ORDER BY`, but it is included for clarity.

---

# MySQL Implementation

```sql
SELECT
  emp_id,
  firstname,
  lastname,
  MAX(salary) AS salary,
  department_id
FROM
  Salary
GROUP BY
  emp_id
ORDER BY
  emp_id;
```

---

# PostgreSQL Implementation

PostgreSQL requires **all non-aggregated columns** in the `SELECT` clause to appear in the `GROUP BY` clause.

```sql
SELECT
  emp_id,
  firstname,
  lastname,
  MAX(salary) AS salary,
  department_id
FROM
  Salary
GROUP BY
  emp_id,
  firstname,
  lastname,
  department_id
ORDER BY
  emp_id;
```

---

# Key SQL Concepts Used

- `GROUP BY`
- `MAX()` aggregation function
- Sorting with `ORDER BY`
- Aggregation logic for determining latest records
