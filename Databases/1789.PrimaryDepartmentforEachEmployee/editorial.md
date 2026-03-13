# 1789. Primary Department for Each Employee — Approaches

## Approach 1: UNION

### Intuition

This approach combines two logical cases using the **UNION** operator.

---

### Step 1 — Employees with `primary_flag = 'Y'`

```sql
SELECT employee_id, department_id
FROM Employee
WHERE primary_flag = 'Y'
```

Explanation:

- Employees belonging to **multiple departments** will have exactly **one department marked with `primary_flag = 'Y'`**.
- This query retrieves those explicitly defined primary departments.

---

### Step 2 — Employees who belong to only one department

```sql
SELECT employee_id, department_id
FROM Employee
GROUP BY employee_id
HAVING COUNT(employee_id) = 1
```

Explanation:

- If an employee appears **only once** in the table, they belong to **one department only**.
- In that case, that department must be their **primary department**, even if the flag is `'N'`.

---

### Step 3 — Combine results

```sql
SELECT employee_id, department_id
FROM Employee
WHERE primary_flag = 'Y'

UNION

SELECT employee_id, department_id
FROM Employee
GROUP BY employee_id
HAVING COUNT(employee_id) = 1;
```

Explanation:

- `UNION` merges the results of both queries.
- Duplicate rows are automatically removed.
- The final result returns the **primary department for every employee**.

---

# Approach 2: Window Function (COUNT)

This approach uses a **window function** to count how many departments each employee belongs to.

---

## Step 1 — Inner Query with Window Function

```sql
SELECT
    *,
    COUNT(employee_id) OVER(PARTITION BY employee_id) AS EmployeeCount
FROM Employee
```

Explanation:

- `PARTITION BY employee_id` groups rows by employee.
- `COUNT() OVER(...)` calculates how many departments each employee belongs to.
- The result is a new column:

```
EmployeeCount
```

Example:

| employee_id | department_id | primary_flag | EmployeeCount |
| ----------- | ------------- | ------------ | ------------- |
| 2           | 1             | Y            | 2             |
| 2           | 2             | N            | 2             |

---

## Step 2 — Outer Query

The inner query is treated as a temporary table.

```sql
SELECT employee_id, department_id
FROM EmployeePartition
```

---

## Step 3 — Filter the Primary Department

```sql
WHERE EmployeeCount = 1
OR primary_flag = 'Y'
```

Explanation:

Two valid conditions:

1. **EmployeeCount = 1**
   → employee belongs to only one department

2. **primary_flag = 'Y'**
   → explicitly marked primary department

---

# Implementation

```sql
SELECT employee_id, department_id
FROM (
    SELECT
        *,
        COUNT(employee_id) OVER(PARTITION BY employee_id) AS EmployeeCount
    FROM Employee
) EmployeePartition
WHERE EmployeeCount = 1
OR primary_flag = 'Y';
```

---

# Key SQL Concepts Used

- **UNION** → combine multiple result sets
- **GROUP BY** → aggregation per employee
- **HAVING** → filter grouped results
- **Window Functions (`COUNT OVER`)**
- **Partitioning (`PARTITION BY`)**
