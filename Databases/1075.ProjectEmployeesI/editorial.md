# 1075. Project Employees I — Approach: JOIN and Calculate

## Intuition

The information required for the result is stored in **two separate tables**:

- `Project` → contains which employees are assigned to each project.
- `Employee` → contains employee details, including their `experience_years`.

To compute the **average experience of employees working on each project**, we must:

1. **Join the two tables** using the shared column `employee_id`.
2. After joining, each row will represent a project–employee pair along with the employee’s experience.
3. Since multiple employees can belong to the same project, we calculate the **average experience** using the aggregate function `AVG()`.
4. The results must be grouped by `project_id`.
5. Finally, the result must be **rounded to two decimal places** using the `ROUND()` function and renamed as `average_years`.

---

# SQL Implementation

```sql
SELECT
    project_id,
    ROUND(AVG(experience_years), 2) AS average_years
FROM
    Project p
JOIN
    Employee e
ON
    p.employee_id = e.employee_id
GROUP BY
    project_id;
```

---

# Explanation

### 1. Join the Tables

```sql
FROM Project p
JOIN Employee e
ON p.employee_id = e.employee_id
```

- This combines rows from `Project` and `Employee` where the `employee_id` matches.
- After the join, each row includes:
  - the `project_id`
  - the employee’s `experience_years`

---

### 2. Calculate the Average Experience

```sql
AVG(experience_years)
```

- Computes the average number of experience years for all employees assigned to a project.

---

### 3. Round the Result

```sql
ROUND(AVG(experience_years), 2)
```

- Ensures the result is rounded to **two decimal places**, as required by the problem.

---

### 4. Group by Project

```sql
GROUP BY project_id
```

- Aggregation must happen per project.
- This ensures the average experience is calculated **for each project separately**.

---

# Output Columns

| Column        | Description                                         |
| ------------- | --------------------------------------------------- |
| project_id    | Identifier of the project                           |
| average_years | Average employee experience (rounded to 2 decimals) |

---

# Key Idea

The problem is a classic **join + aggregation** pattern:

1. Join related tables.
2. Aggregate data using `GROUP BY`.
3. Format the result using `ROUND()`.
