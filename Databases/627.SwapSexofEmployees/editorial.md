# 627. Swap Sex of Employees — Detailed Summary

## Approach: Using `UPDATE` and `CASE...WHEN`

This approach solves the problem with a **single `UPDATE` statement**.

The main idea is to update the `sex` column dynamically based on its current value:

- if the current value is `'m'`, change it to `'f'`
- otherwise, change it to `'m'`

Because the only valid values are `'m'` and `'f'`, this swaps all rows correctly in one pass.

---

## Problem Restatement

We have a table `Salary` with a column:

```sql
sex
```

where the allowed values are:

- `'m'`
- `'f'`

We must swap every value so that:

- `'m'` becomes `'f'`
- `'f'` becomes `'m'`

Constraints:

- use **one single `UPDATE` statement**
- no temporary tables
- no separate `SELECT` query

---

## Core Idea

A normal update like this would not work safely:

```sql
UPDATE salary
SET sex = 'f'
WHERE sex = 'm';
```

because that only changes one direction.

And doing two separate updates would violate the problem requirement.

So we need one statement that decides, row by row, what the new value should be.

That is exactly what `CASE` is for.

---

# Query

```sql
UPDATE salary
SET
    sex = CASE sex
        WHEN 'm' THEN 'f'
        ELSE 'm'
    END;
```

---

# Step-by-Step Explanation

## 1. Use `UPDATE` to modify the table

```sql
UPDATE salary
```

This tells SQL that we want to change rows in the `salary` table.

Because there is no `WHERE` clause, the update applies to **all rows**.

That is correct, since every employee's `sex` value must be swapped.

---

## 2. Assign a new value to `sex`

```sql
SET sex = ...
```

This specifies that the `sex` column will be recalculated for every row.

The new value depends on the current value of `sex`.

---

## 3. Use `CASE` for conditional replacement

```sql
CASE sex
    WHEN 'm' THEN 'f'
    ELSE 'm'
END
```

This is a compact conditional expression.

### How it works

- if `sex = 'm'`, assign `'f'`
- otherwise, assign `'m'`

Since the table guarantees that `sex` is only `'m'` or `'f'`, the `ELSE 'm'` branch effectively means:

- if it is not `'m'`, it must be `'f'`
- so change it to `'m'`

This swaps the values correctly.

---

# Why This Works in One Statement

The important point is that SQL evaluates the expression based on the row's **original value** during the update.

So each row is transformed independently:

- rows with `'m'` become `'f'`
- rows with `'f'` become `'m'`

There is no need for an intermediate placeholder value.

That is why this method works safely in one statement.

---

# Example Walkthrough

## Input

|  id | name | sex | salary |
| --: | ---- | --- | -----: |
|   1 | A    | m   |   2500 |
|   2 | B    | f   |   1500 |
|   3 | C    | m   |   5500 |
|   4 | D    | f   |    500 |

---

## Apply the `CASE` logic row by row

### Row 1

Current value:

```text
sex = 'm'
```

Rule:

```sql
WHEN 'm' THEN 'f'
```

New value:

```text
'f'
```

---

### Row 2

Current value:

```text
sex = 'f'
```

This does not match `WHEN 'm'`, so it goes to:

```sql
ELSE 'm'
```

New value:

```text
'm'
```

---

### Row 3

Current value:

```text
sex = 'm'
```

New value:

```text
'f'
```

---

### Row 4

Current value:

```text
sex = 'f'
```

New value:

```text
'm'
```

---

## Final Output

|  id | name | sex | salary |
| --: | ---- | --- | -----: |
|   1 | A    | f   |   2500 |
|   2 | B    | m   |   1500 |
|   3 | C    | f   |   5500 |
|   4 | D    | m   |    500 |

---

# Why `CASE` Is a Good Fit

`CASE` is useful whenever an updated value depends on the current value.

In this problem, the new value of `sex` is not constant. It depends on whether the old value is:

- `'m'`
- `'f'`

So `CASE` expresses the business rule clearly and directly.

---

# Clause-by-Clause Breakdown

## `UPDATE salary`

Modify the `salary` table.

---

## `SET sex = ...`

Assign a new value to the `sex` column.

---

## `CASE sex`

Compare the current value of `sex`.

---

## `WHEN 'm' THEN 'f'`

If the current value is `'m'`, replace it with `'f'`.

---

## `ELSE 'm'`

For all other valid rows, replace with `'m'`.

Because the only possible values are `'m'` and `'f'`, this correctly swaps `'f'` to `'m'`.

---

# Alternative Equivalent Version

A slightly more explicit version is:

```sql
UPDATE salary
SET sex = CASE
    WHEN sex = 'm' THEN 'f'
    WHEN sex = 'f' THEN 'm'
END;
```

This version states both mappings explicitly.

It is logically equivalent and can be easier to read for some people.

---

# Why No Temporary Table Is Needed

Some swap problems require a temporary placeholder, such as:

- change `'m'` to `'x'`
- change `'f'` to `'m'`
- change `'x'` to `'f'`

But here, `CASE` avoids that completely.

Each row is updated directly to its final correct value.

So no temporary table or temporary placeholder value is needed.

---

# Why No `SELECT` Is Needed

The problem explicitly says not to use a `SELECT`.

This solution respects that because it is a pure `UPDATE` statement.

All decision-making happens inline inside the `CASE` expression.

---

# Final Recommended Query

```sql
UPDATE salary
SET
    sex = CASE sex
        WHEN 'm' THEN 'f'
        ELSE 'm'
    END;
```

---

# Key Takeaways

- Use one `UPDATE` statement to modify all rows
- Use `CASE` to decide the replacement value row by row
- `'m'` maps to `'f'`
- `'f'` maps to `'m'`
- No temporary table and no `SELECT` are needed

---
