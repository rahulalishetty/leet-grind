# 570. Managers with at Least 5 Direct Reports — Exhaustive Summary of SQL Approaches

## Problem Restatement

We are given an `Employee` table:

| Column       | Type    |
| ------------ | ------- |
| `id`         | int     |
| `name`       | varchar |
| `department` | varchar |
| `managerId`  | int     |

### Meaning of the table

- each row is one employee
- `id` uniquely identifies the employee
- `managerId` points to that employee’s manager
- if `managerId` is `NULL`, the employee has no manager
- no employee manages themself

---

## Goal

Find the **names of managers** who have **at least 5 direct reports**.

A **direct report** means an employee whose `managerId = manager.id`.

---

## Core Observation

This problem is fundamentally about:

1. counting how many employees report to each `managerId`
2. keeping only managers with count `>= 5`
3. converting those manager IDs back into manager names

So the solution naturally splits into two parts:

- **aggregation** on `managerId`
- **lookup** of the manager’s `name`

---

# Approach 1: Group By and Join

## Intuition

The cleanest way to think about the problem is:

- first identify all `managerId` values that appear at least 5 times
- then join those IDs back with the `Employee` table to get manager names

This is a very common SQL pattern:

- compute candidate keys in a subquery
- join them to the main table for final output

---

## Step 1: Find managers with at least 5 direct reports

We count employees grouped by `managerId`:

```sql
SELECT ManagerId
FROM Employee
GROUP BY ManagerId
HAVING COUNT(ManagerId) >= 5
```

### What this does

- `GROUP BY ManagerId` forms one group per manager
- `COUNT(ManagerId)` counts how many employees report to that manager
- `HAVING COUNT(ManagerId) >= 5` keeps only managers with at least 5 reports

### Why `HAVING` and not `WHERE`?

Because:

- `WHERE` filters rows **before grouping**
- `HAVING` filters groups **after aggregation**

Since we are filtering based on `COUNT(...)`, `HAVING` is the correct clause.

---

## Step 2: Join back to get manager names

Once we know the qualifying manager IDs, we join them with the `Employee` table:

```sql
SELECT
    Name
FROM
    Employee AS t1
JOIN
    (
        SELECT ManagerId
        FROM Employee
        GROUP BY ManagerId
        HAVING COUNT(ManagerId) >= 5
    ) AS t2
ON
    t1.Id = t2.ManagerId;
```

---

## Full Query

```sql
SELECT
    Name
FROM
    Employee AS t1
JOIN
    (
        SELECT
            ManagerId
        FROM
            Employee
        GROUP BY ManagerId
        HAVING COUNT(ManagerId) >= 5
    ) AS t2
ON
    t1.Id = t2.ManagerId;
```

---

## Detailed Breakdown

### Outer table: `t1`

```sql
Employee AS t1
```

This represents the full employee table, but in the final query we are using it specifically to retrieve manager names.

---

### Inner subquery: `t2`

```sql
(
    SELECT ManagerId
    FROM Employee
    GROUP BY ManagerId
    HAVING COUNT(ManagerId) >= 5
) AS t2
```

This produces a one-column table containing only the qualifying manager IDs.

For example, it might produce:

| ManagerId |
| --------- |
| 101       |
| 207       |
| 330       |

---

### Join condition

```sql
ON t1.Id = t2.ManagerId
```

This matches:

- `t1.Id` → employee’s id
- `t2.ManagerId` → qualifying manager id

So only rows corresponding to those managers survive the join.

---

## Example Walkthrough

### Input

| id  | name  | department | managerId |
| --- | ----- | ---------- | --------- |
| 101 | John  | A          | NULL      |
| 102 | Dan   | A          | 101       |
| 103 | James | A          | 101       |
| 104 | Amy   | A          | 101       |
| 105 | Anne  | A          | 101       |
| 106 | Ron   | B          | 101       |

### Subquery result

```sql
SELECT ManagerId
FROM Employee
GROUP BY ManagerId
HAVING COUNT(ManagerId) >= 5
```

Result:

| ManagerId |
| --------- |
| 101       |

Because employee `101` manages 5 employees.

### Join result

Join `Employee` with this result on:

```sql
t1.Id = t2.ManagerId
```

That matches `id = 101`, whose name is `John`.

### Final output

| Name |
| ---- |
| John |

---

## Why This Works

The subquery identifies **which employee IDs represent valid managers**.

The join converts those IDs into the manager rows themselves, giving access to `name`.

So logically:

- subquery answers: **who qualifies?**
- join answers: **what are their names?**

---

## Complexity Discussion

Let `N` be the number of rows in `Employee`.

### Time Complexity

Conceptually:

- grouping all rows by `managerId`: `O(N)`
- joining qualifying manager IDs back to employees: roughly `O(N)` with proper indexing / optimizer support

So the query is generally treated as:

```text
O(N)
```

in practical interview-style analysis.

In a real database engine, exact performance depends on:

- indexing
- query planner choices
- hash aggregation vs sort aggregation
- hash join vs nested loop vs merge join

But from a problem-solving standpoint, `O(N)` is the right mental model.

---

## Strengths of Approach 1

- very explicit and readable
- separates counting logic from lookup logic
- easy to explain in an interview
- standard SQL pattern

## Weaknesses of Approach 1

- slightly more verbose than necessary
- uses a derived table even though simpler formulations exist

---

# Approach 2: IN Clause with Subquery

## Intuition

Instead of joining, we can:

- directly select employee names
- keep only those employees whose `id` appears in the set of qualifying manager IDs

That is exactly what `IN` does.

So this approach still uses the same aggregation idea, but replaces the join with a membership check.

---

## Full Query

```sql
SELECT
    name
FROM
    employee
WHERE
    id IN (
        SELECT
            managerId
        FROM
            employee
        GROUP BY
            managerId
        HAVING COUNT(*) >= 5
    );
```

---

## How It Works

### Outer query

```sql
SELECT name
FROM employee
```

This reads all employees.

### Filter condition

```sql
WHERE id IN (...)
```

Only keep employees whose `id` is in the list returned by the subquery.

### Subquery

```sql
SELECT managerId
FROM employee
GROUP BY managerId
HAVING COUNT(*) >= 5
```

This produces the manager IDs with at least 5 reports.

So the outer query is effectively asking:

> return the names of employees whose ID belongs to the set of managers with at least 5 direct reports

---

## Example Walkthrough

Using the same sample:

| id  | name  | department | managerId |
| --- | ----- | ---------- | --------- |
| 101 | John  | A          | NULL      |
| 102 | Dan   | A          | 101       |
| 103 | James | A          | 101       |
| 104 | Amy   | A          | 101       |
| 105 | Anne  | A          | 101       |
| 106 | Ron   | B          | 101       |

### Subquery result

```sql
SELECT managerId
FROM employee
GROUP BY managerId
HAVING COUNT(*) >= 5
```

returns:

| managerId |
| --------- |
| 101       |

### Outer query filter

```sql
WHERE id IN (101)
```

Only the row for John remains.

### Final output

| name |
| ---- |
| John |

---

## Why This Works

This is the same logic as Approach 1, just written differently.

- Approach 1: join with qualifying manager IDs
- Approach 2: filter employees by whether their ID belongs to the qualifying manager IDs

So both solutions are conceptually identical in what they compute.

---

## `COUNT(ManagerId)` vs `COUNT(*)`

You may notice a small difference between the two formulations:

### In Approach 1

```sql
HAVING COUNT(ManagerId) >= 5
```

### In Approach 2

```sql
HAVING COUNT(*) >= 5
```

Both work here.

### Why?

Inside each `GROUP BY managerId` group:

- all rows in that group correspond to employees having that `managerId`
- `managerId` is not `NULL` within that group in the practical counting sense

So counting rows with `COUNT(*)` and counting non-null `managerId` values both produce the same result for non-null grouped managers.

### Slight SQL subtlety

If there is a `NULL` managerId group, then:

- `COUNT(managerId)` ignores nulls
- `COUNT(*)` counts rows

But since managers with `NULL` cannot be matched to employee IDs anyway, this does not affect the final valid answer for this problem.

Still, many people prefer:

```sql
COUNT(*)
```

because it clearly means “count rows in the group”.

---

## Complexity Discussion

Again let `N` be the number of rows.

### Time Complexity

- grouping subquery: `O(N)`
- membership filtering of outer query: typically optimized efficiently

So practical complexity is again treated as:

```text
O(N)
```

subject to optimizer/index details.

---

## Strengths of Approach 2

- shorter and concise
- easy to read once comfortable with subqueries
- avoids explicit join syntax

## Weaknesses of Approach 2

- some people find `IN (subquery)` less explicit than a join
- depending on SQL dialect and optimizer, readability/performance perception varies

---

# Comparing Approach 1 and Approach 2

## Logical Structure

### Approach 1

- build qualifying manager IDs
- join them back to `Employee`

### Approach 2

- build qualifying manager IDs
- filter employees whose `id` is in that set

Both are equivalent.

---

## Readability

### Approach 1: Group By + Join

Often considered more explicit because:

- the lookup step is visible as a join
- easier to extend if you later need more columns

### Approach 2: IN Subquery

Often considered shorter and more elegant for simple membership filtering.

---

## Performance

Modern SQL engines often optimize both forms similarly.

For interview purposes, do not overstate performance differences unless the problem specifically asks for them.

The meaningful part is the aggregation:

```sql
GROUP BY managerId
HAVING COUNT(...) >= 5
```

That is the real core of the solution.

---

# Clean Final SQL Versions

## Version 1: Join-Based

```sql
SELECT
    e.name
FROM
    Employee e
JOIN (
    SELECT
        managerId
    FROM
        Employee
    GROUP BY
        managerId
    HAVING COUNT(*) >= 5
) m
ON e.id = m.managerId;
```

---

## Version 2: IN-Based

```sql
SELECT
    name
FROM
    Employee
WHERE
    id IN (
        SELECT
            managerId
        FROM
            Employee
        GROUP BY
            managerId
        HAVING COUNT(*) >= 5
    );
```

---

# Simplest Interview Explanation

A compact way to explain the problem is:

1. group employees by `managerId`
2. keep only manager IDs that appear at least 5 times
3. use those IDs to fetch manager names

That explanation covers both solutions.

---

# Common SQL Pattern Behind This Problem

This problem is a classic example of:

- **self-referential relationship** in one table
- **aggregation over foreign-key-like column**
- **filter by count**
- **recover entity details using join or IN**

General form:

```sql
SELECT parent.name
FROM table parent
WHERE parent.id IN (
    SELECT child.parentId
    FROM table child
    GROUP BY child.parentId
    HAVING COUNT(*) >= k
);
```

or equivalently:

```sql
SELECT parent.name
FROM table parent
JOIN (
    SELECT child.parentId
    FROM table child
    GROUP BY child.parentId
    HAVING COUNT(*) >= k
) x
ON parent.id = x.parentId;
```

This pattern appears in many hierarchy problems, such as:

- managers with many reports
- customers with many orders
- authors with many books
- categories with many products

---

# Final Takeaways

## Most important idea

The key is not the join or the `IN` clause. The key is:

```sql
GROUP BY managerId
HAVING COUNT(*) >= 5
```

That is what identifies managers with at least five direct reports.

## Then there are two equivalent ways to get names

- join to the employee table
- or filter employee IDs with `IN`

## Preferred answer

Either is correct. In many cases, the `IN` version is slightly shorter, while the `JOIN` version is slightly more explicit.

---
