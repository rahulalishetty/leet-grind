# Database Approach 1: Using `OUTER JOIN` and `WHERE` Clause

## Problem Context

We have two tables:

- `Employee`
- `Bonus`

The key detail is:

- `Bonus.empId` is a foreign key referring to `Employee.empId`
- Not every employee has a matching row in the `Bonus` table

The goal of this approach is to return employees whose:

- bonus is **less than 1000**, or
- bonus is **missing** (`NULL`)

---

## Core Idea

Because some employees may not have a bonus record at all, an **inner join** would incorrectly exclude them.

So we use a **LEFT JOIN**:

- keep **all rows** from `Employee`
- attach matching rows from `Bonus` when they exist
- if no matching bonus row exists, the bonus columns become `NULL`

Then we use a `WHERE` clause to keep only:

- employees with `bonus < 1000`
- employees with `bonus IS NULL`

---

## Step 1: Join `Employee` and `Bonus`

```sql
SELECT
    Employee.name,
    Bonus.bonus
FROM
    Employee
LEFT OUTER JOIN
    Bonus
ON Employee.empId = Bonus.empId;
```

### Explanation

This query says:

- Start from the `Employee` table
- Try to match each employee with a row in `Bonus`
- If a match exists, include the bonus
- If no match exists, still keep the employee row, but show `NULL` for bonus

---

## Why `LEFT OUTER JOIN`?

A `LEFT OUTER JOIN` preserves every row from the table on the left side, which here is `Employee`.

That is exactly what we need because:

- every employee should be considered
- some employees may not have a bonus record

### Equivalent form

```sql
LEFT OUTER JOIN
```

and

```sql
LEFT JOIN
```

mean the same thing in SQL.

---

## Example Intermediate Output

After this join, the result may look like:

| name   | bonus |
| ------ | ----- |
| Dan    | 500   |
| Thomas | 2000  |
| Brad   | NULL  |
| John   | NULL  |

This tells us:

- Dan has a bonus of 500
- Thomas has a bonus of 2000
- Brad has no bonus record
- John has no bonus record

---

## Understanding `NULL`

`NULL` does **not** mean zero.

It means:

- missing value
- unknown value
- no matching row / no stored value

This distinction matters a lot in SQL.

For example:

```sql
bonus = NULL
```

is **wrong**.

SQL requires:

```sql
bonus IS NULL
```

or

```sql
bonus IS NOT NULL
```

when checking for `NULL`.

---

## Step 2: Filter the rows we want

Now we add a `WHERE` clause:

```sql
SELECT
    Employee.name,
    Bonus.bonus
FROM
    Employee
LEFT JOIN
    Bonus
ON Employee.empId = Bonus.empId
WHERE
    bonus < 1000 OR bonus IS NULL;
```

---

## Explanation of the `WHERE` Clause

### Condition 1

```sql
bonus < 1000
```

This keeps employees whose bonus exists and is less than 1000.

### Condition 2

```sql
bonus IS NULL
```

This keeps employees who do not have a bonus record.

### Combined Condition

```sql
bonus < 1000 OR bonus IS NULL
```

So the final result includes:

- employees with small bonuses
- employees with no bonus

---

## Full Implementation

```sql
SELECT
    Employee.name,
    Bonus.bonus
FROM
    Employee
LEFT JOIN
    Bonus
ON Employee.empId = Bonus.empId
WHERE
    bonus < 1000 OR bonus IS NULL;
```

---

## Dry Run with Sample Data

### Employee table

| empId | name   |
| ----: | ------ |
|     1 | Dan    |
|     2 | Thomas |
|     3 | Brad   |
|     4 | John   |

### Bonus table

| empId | bonus |
| ----: | ----: |
|     1 |   500 |
|     2 |  2000 |

---

### After `LEFT JOIN`

```sql
SELECT
    Employee.name,
    Bonus.bonus
FROM
    Employee
LEFT JOIN
    Bonus
ON Employee.empId = Bonus.empId;
```

Result:

| name   | bonus |
| ------ | ----- |
| Dan    | 500   |
| Thomas | 2000  |
| Brad   | NULL  |
| John   | NULL  |

---

### Apply the filter

```sql
WHERE bonus < 1000 OR bonus IS NULL
```

Evaluate row by row:

- Dan → `500 < 1000` → keep
- Thomas → `2000 < 1000` is false and not `NULL` → remove
- Brad → `bonus IS NULL` → keep
- John → `bonus IS NULL` → keep

Final output:

| name | bonus |
| ---- | ----- |
| Dan  | 500   |
| Brad | NULL  |
| John | NULL  |

---

## Why an `INNER JOIN` Would Be Wrong

Suppose we wrote:

```sql
SELECT
    Employee.name,
    Bonus.bonus
FROM
    Employee
INNER JOIN
    Bonus
ON Employee.empId = Bonus.empId;
```

This would only return employees who have a matching bonus row.

Result:

| name   | bonus |
| ------ | ----- |
| Dan    | 500   |
| Thomas | 2000  |

Brad and John disappear completely, which is incorrect for this problem because employees with no bonus should also be included.

That is why `LEFT JOIN` is the correct choice.

---

## Important SQL Detail About `NULL`

This is a common mistake:

```sql
WHERE bonus < 1000 OR bonus = NULL
```

This does **not** work.

Correct version:

```sql
WHERE bonus < 1000 OR bonus IS NULL
```

### Why?

Because `NULL` is not a normal value. It represents unknown or missing data, so SQL uses special syntax for checking it.

---

## Simpler Submission Version

```sql
SELECT
    e.name,
    b.bonus
FROM
    Employee e
LEFT JOIN
    Bonus b
ON e.empId = b.empId
WHERE
    b.bonus < 1000 OR b.bonus IS NULL;
```

This version does the same thing using table aliases:

- `e` for `Employee`
- `b` for `Bonus`

It is shorter and often easier to read.

---

## Time Complexity

Let:

- `n` = number of rows in `Employee`
- `m` = number of rows in `Bonus`

The database performs:

- a left join between the two tables
- a filter on the joined result

In high-level terms, the cost depends on indexing and the query planner, but conceptually:

- join work is around `O(n + m)` to `O(n log m)` depending on implementation
- filtering is linear over the joined rows

For interview-style discussion, it is enough to say the query scans the relevant rows and filters them after joining.

---

## Space Complexity

The join result may temporarily hold rows combining employee and bonus information.

Conceptually, auxiliary space depends on the database engine, but from a reasoning perspective this is typically treated as proportional to the output or intermediate join processing.

---

## Key Takeaways

- Use `LEFT JOIN` when you must keep rows from the left table even if no match exists on the right.
- Missing bonus rows appear as `NULL`.
- Use `IS NULL` instead of `= NULL`.
- Filter with:

```sql
bonus < 1000 OR bonus IS NULL
```

to keep both low-bonus employees and employees without any bonus record.

---

## Final Answer

```sql
SELECT
    Employee.name,
    Bonus.bonus
FROM
    Employee
LEFT JOIN
    Bonus
ON Employee.empId = Bonus.empId
WHERE
    bonus < 1000 OR bonus IS NULL;
```
