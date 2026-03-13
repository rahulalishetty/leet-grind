# 1731. The Number of Employees Which Report to Each Employee — Approaches

## Approach 1: Self Join

### Overview

This query identifies managers, counts how many employees report directly to them, and calculates the average age of those employees.

The table `employees` contains both employee and manager information. Therefore, the query performs a **self join** to match employees with their managers.

---

## Intuition

### Join Operation

We join the `employees` table with itself:

```sql
FROM employees emp
JOIN employees mgr
ON emp.reports_to = mgr.employee_id
```

- `emp` represents the employee.
- `mgr` represents the manager.

This join pairs each employee with their manager.

---

### Aggregation and Calculation

We compute:

- Number of direct reports
- Average age of the reports

```sql
SELECT
  mgr.employee_id,
  mgr.name,
  COUNT(emp.employee_id) AS reports_count,
  ROUND(AVG(emp.age)) AS average_age
```

- **reports_count** counts the employees reporting to each manager.
- **average_age** computes the average age of those employees.

---

### Grouping

```sql
GROUP BY employee_id
```

Grouping ensures the counts and averages are calculated **per manager**.

---

### Ordering

```sql
ORDER BY employee_id
```

The result is sorted by the manager's `employee_id`.

---

## Implementation

```sql
SELECT
  mgr.employee_id,
  mgr.name,
  COUNT(emp.employee_id) AS reports_count,
  ROUND(AVG(emp.age)) AS average_age
FROM employees emp
JOIN employees mgr
ON emp.reports_to = mgr.employee_id
GROUP BY employee_id
ORDER BY employee_id;
```

---

# Approach 2: Correlated Subquery

This approach also calculates the managers, their number of reports, and the average age of those reports.

Instead of a self join, it uses:

- **GROUP BY**
- **correlated subquery** to fetch the manager name

---

## Intuition

### Grouping by Manager

```sql
FROM employees e
GROUP BY reports_to
```

Grouping by `reports_to` groups all employees under their manager.

---

### Selecting Manager ID and Name

```sql
SELECT
  reports_to AS employee_id,
  (
    SELECT name
    FROM employees e1
    WHERE e.reports_to = e1.employee_id
  ) AS name
```

- `reports_to` is the manager ID.
- The subquery retrieves the manager's name.

---

### Reports Count and Average Age

```sql
COUNT(reports_to) AS reports_count,
ROUND(AVG(age)) AS average_age
```

- Counts how many employees report to the manager.
- Computes the average age of those employees.

---

### Filter Managers Only

```sql
HAVING reports_count > 0
```

This ensures that only **actual managers** (employees with reports) appear in the output.

---

### Ordering Results

```sql
ORDER BY employee_id
```

Results are sorted by manager ID.

---

## Implementation

```sql
SELECT
  reports_to AS employee_id,
  (
    SELECT name
    FROM employees e1
    WHERE e.reports_to = e1.employee_id
  ) AS name,
  COUNT(reports_to) AS reports_count,
  ROUND(AVG(age)) AS average_age
FROM employees e
GROUP BY reports_to
HAVING reports_count > 0
ORDER BY employee_id;
```

---

# Key SQL Concepts Used

- **Self Join** for employee‑manager relationships
- **GROUP BY** for aggregation
- **COUNT()** to count reports
- **AVG()** to calculate average age
- **ROUND()** to format output
- **Correlated Subquery** to retrieve related data
