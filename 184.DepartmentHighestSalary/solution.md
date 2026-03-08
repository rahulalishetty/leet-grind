# Highest Salary per Department — Left Join + WHERE (IN) Approach

## Problem

Given two tables:

- Employee(id, name, salary, departmentId)
- Department(id, name)

Return:

- Department name
- Employee name
- Salary

For employees who earn the highest salary in their department.

Important:

- Multiple employees may share the same highest salary.
- All such employees must be returned.

---

# Step 1 — Compute Maximum Salary per Department

First, determine the highest salary in each department.

```sql
SELECT
    DepartmentId,
    MAX(Salary) AS MaxSalary
FROM Employee
GROUP BY DepartmentId;
```

Example Output:

| DepartmentId | MaxSalary |
| ------------ | --------- |
| 1            | 90000     |
| 2            | 80000     |

This query:

- Groups employees by department
- Computes the maximum salary in each department

We do not include employee names here because:

- There may be multiple employees with the same maximum salary
- Aggregation collapses rows

---

# Step 2 — Match Employees with the Maximum Salary

Now, retrieve employees whose:

- (DepartmentId, Salary) pair
- Matches the (DepartmentId, MAX(Salary)) pair

## Final Implementation

```sql
SELECT
    Department.name AS 'Department',
    Employee.name AS 'Employee',
    Salary
FROM Employee
JOIN Department
    ON Employee.DepartmentId = Department.Id
WHERE (Employee.DepartmentId, Salary) IN (
    SELECT
        DepartmentId,
        MAX(Salary)
    FROM Employee
    GROUP BY DepartmentId
);
```

---

# Why This Works

The subquery returns:

(DepartmentId, MaxSalary)

The outer query keeps only employees whose:

(Employee.DepartmentId, Employee.Salary)

exists in that result set.

So for each department:

- It finds the max salary
- Then selects employees whose salary equals that max

This naturally handles ties.

---

# Alternative Equivalent Form (Using JOIN Instead of IN)

```sql
SELECT
    d.name AS Department,
    e.name AS Employee,
    e.salary AS Salary
FROM Employee e
JOIN (
    SELECT DepartmentId, MAX(Salary) AS MaxSalary
    FROM Employee
    GROUP BY DepartmentId
) m
  ON e.DepartmentId = m.DepartmentId
 AND e.Salary = m.MaxSalary
JOIN Department d
  ON e.DepartmentId = d.Id;
```

Both approaches are logically equivalent.

---

# Performance Considerations

- Ensure an index on:
  - Employee(DepartmentId, Salary)
- The grouping step scans the Employee table once.
- The join step matches employees efficiently when indexed.

## SubQuery

```sql
SELECT d.name AS Department,
       e.name AS Employee,
       e.salary AS Salary
FROM Employee e
JOIN Department d
  ON e.departmentId = d.id
WHERE e.salary = (
    SELECT MAX(e2.salary)
    FROM Employee e2
    WHERE e2.departmentId = e.departmentId
);
```

## Window Function

```sql
SELECT Department, Employee, Salary
FROM (
    SELECT d.name AS Department,
           e.name AS Employee,
           e.salary AS Salary,
           DENSE_RANK() OVER (
               PARTITION BY e.departmentId
               ORDER BY e.salary DESC
           ) AS rnk
    FROM Employee e
    JOIN Department d
      ON e.departmentId = d.id
) t
WHERE rnk = 1;
```
