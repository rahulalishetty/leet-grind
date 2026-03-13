# Approach: LEFT JOIN on ID

## Algorithm

### Step 1 — Join the Tables

We perform a **LEFT JOIN** between the `Employees` table and the `EmployeeUNI` table using the `id` column.

The reason for using **LEFT JOIN** instead of `INNER JOIN` is that we must include **all employees**, even if they do not have a corresponding record in `EmployeeUNI`.

```sql
SELECT *
FROM Employees
LEFT JOIN EmployeeUNI
ON Employees.id = EmployeeUNI.id;
```

Example intermediate result:

| id  | name     | id   | unique_id |
| --- | -------- | ---- | --------- |
| 1   | Alice    | NULL | NULL      |
| 7   | Bob      | NULL | NULL      |
| 11  | Meir     | 11   | 2         |
| 90  | Winston  | 90   | 3         |
| 3   | Jonathan | 3    | 1         |

Explanation:

- **Alice** and **Bob** have no matching rows in `EmployeeUNI`, so their `unique_id` values become `NULL`.
- Other employees have matching rows, so their `unique_id` values appear.

---

## Step 2 — Select the Required Columns

The problem only requires:

- `unique_id`
- `name`

Therefore we select:

- `EmployeeUNI.unique_id`
- `Employees.name`

---

# Final SQL Query

```sql
SELECT
    EmployeeUNI.unique_id,
    Employees.name
FROM Employees
LEFT JOIN EmployeeUNI
ON Employees.id = EmployeeUNI.id;
```

---

# Key Concepts

- **LEFT JOIN** keeps all rows from the left table (`Employees`).
- If there is no matching row in the right table (`EmployeeUNI`), the joined columns become **NULL**.
- This ensures employees without a unique ID still appear in the result.
