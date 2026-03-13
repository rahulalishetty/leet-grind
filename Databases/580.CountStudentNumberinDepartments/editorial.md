# 580. Count Student Number in Departments

## Approach: Using `OUTER JOIN` and `COUNT(expression)`

## Intuition

This problem asks us to report the number of students in **every department**, including departments that currently have **no students**.

That means a simple query on the `Student` table is not enough, because departments with zero students would never appear there.

So the correct direction is:

1. Start from the `Department` table
2. Bring in matching students using a `LEFT OUTER JOIN`
3. Group by department
4. Count how many students belong to each department

At first, many people write a query like this:

```sql
SELECT
    dept_name,
    COUNT(*) AS student_number
FROM Department
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id
GROUP BY Department.dept_name
ORDER BY student_number DESC, Department.dept_name;
```

This looks reasonable, but it contains a subtle mistake.

---

## The core issue

The problem says departments with no students must show:

```text
0
```

But with `LEFT OUTER JOIN`, a department with no matching student still produces **one output row**:

- department columns are filled
- student columns are `NULL`

So for a department like `Law`, the joined result still contains one row like:

| dept_id | dept_name | student_id | student_name | gender | dept_id |
| ------- | --------- | ---------- | ------------ | ------ | ------- |
| 3       | Law       | NULL       | NULL         | NULL   | NULL    |

Now see what happens:

```sql
COUNT(*)
```

counts **rows**, not just non-null student matches.

So `COUNT(*)` counts that row too, and incorrectly returns `1` for `Law`.

That is why `COUNT(*)` is wrong in this problem.

---

## Why `COUNT(student_id)` works

Instead of counting all rows, we should count a student-side column that is only non-null when a real student exists.

This is why we use:

```sql
COUNT(student_id)
```

`COUNT(expression)` counts only rows where the expression is **not NULL**.

So for departments with no students:

- the `LEFT JOIN` still produces one row
- but `student_id` is `NULL`
- `COUNT(student_id)` ignores it
- result becomes `0`

That is exactly what we want.

---

## Correct SQL Solution

```sql
SELECT
    dept_name,
    COUNT(student_id) AS student_number
FROM Department
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id
GROUP BY Department.dept_name
ORDER BY student_number DESC, Department.dept_name;
```

---

## Step-by-step breakdown

### 1. Start from `Department`

We begin with the department table because the question requires **all departments**, even empty ones.

```sql
FROM Department
```

### 2. Use `LEFT OUTER JOIN`

We attach students to departments:

```sql
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id
```

Why `LEFT OUTER JOIN`?

Because:

- departments with students will match normally
- departments without students will still remain in the result
- student columns for those departments will be `NULL`

If we used `INNER JOIN`, empty departments would disappear.

---

## Why `INNER JOIN` is wrong

Example:

```sql
SELECT
    dept_name,
    COUNT(student_id) AS student_number
FROM Department
INNER JOIN Student
    ON Department.dept_id = Student.dept_id
GROUP BY Department.dept_name;
```

This would completely exclude departments like `Law` that have no students.

So `INNER JOIN` violates the requirement.

---

## 3. Group by department

We want one result row per department:

```sql
GROUP BY Department.dept_name
```

This groups all matched student rows under each department.

---

## 4. Count actual students

Now we count real student records:

```sql
COUNT(student_id) AS student_number
```

Since `student_id` is `NULL` when there is no matching student, departments with no students correctly get `0`.

---

## 5. Sort properly

The required order is:

1. `student_number` descending
2. `dept_name` alphabetically when counts tie

So:

```sql
ORDER BY student_number DESC, Department.dept_name;
```

---

## Example walkthrough

### Input tables

#### Student

| student_id | student_name | gender | dept_id |
| ---------- | ------------ | ------ | ------- |
| 1          | Jack         | M      | 1       |
| 2          | Jane         | F      | 1       |
| 3          | Mark         | M      | 2       |

#### Department

| dept_id | dept_name   |
| ------- | ----------- |
| 1       | Engineering |
| 2       | Science     |
| 3       | Law         |

---

## Result after `LEFT OUTER JOIN`

```sql
SELECT *
FROM Department
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id;
```

Conceptually, the joined rows look like:

| Department.dept_id | dept_name   | student_id | student_name | gender | Student.dept_id |
| ------------------ | ----------- | ---------- | ------------ | ------ | --------------- |
| 1                  | Engineering | 1          | Jack         | M      | 1               |
| 1                  | Engineering | 2          | Jane         | F      | 1               |
| 2                  | Science     | 3          | Mark         | M      | 2               |
| 3                  | Law         | NULL       | NULL         | NULL   | NULL            |

Now observe:

- Engineering has 2 student rows
- Science has 1 student row
- Law has 1 joined row, but student columns are all `NULL`

This is exactly where `COUNT(*)` fails.

---

## Wrong query using `COUNT(*)`

```sql
SELECT
    dept_name,
    COUNT(*) AS student_number
FROM Department
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id
GROUP BY Department.dept_name
ORDER BY student_number DESC, Department.dept_name;
```

### Wrong output

| dept_name   | student_number |
| ----------- | -------------- |
| Engineering | 2              |
| Law         | 1              |
| Science     | 1              |

Why is `Law = 1` wrong?

Because there is one joined row for `Law`, even though there is no actual student.

`COUNT(*)` counts that row.

---

## Correct query using `COUNT(student_id)`

```sql
SELECT
    dept_name,
    COUNT(student_id) AS student_number
FROM Department
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id
GROUP BY Department.dept_name
ORDER BY student_number DESC, Department.dept_name;
```

### Correct output

| dept_name   | student_number |
| ----------- | -------------- |
| Engineering | 2              |
| Science     | 1              |
| Law         | 0              |

This is correct because `COUNT(student_id)` ignores the `NULL` value for `Law`.

---

## Important SQL concept: `COUNT(*)` vs `COUNT(column)`

### `COUNT(*)`

Counts **all rows** in the group.

Example:

```sql
COUNT(*)
```

This includes rows where some columns are `NULL`.

### `COUNT(column)`

Counts only rows where the specified column is **not NULL**.

Example:

```sql
COUNT(student_id)
```

This excludes the null placeholder rows created by `LEFT JOIN`.

---

## Simple rule to remember

When using `LEFT JOIN` and you want to count matching rows from the right table:

- use `COUNT(right_table.non_null_column)`
- do **not** use `COUNT(*)`

That pattern is extremely important in SQL.

---

## Alternative acceptable version

You may also write:

```sql
SELECT
    d.dept_name,
    COUNT(s.student_id) AS student_number
FROM Department d
LEFT JOIN Student s
    ON d.dept_id = s.dept_id
GROUP BY d.dept_name
ORDER BY student_number DESC, d.dept_name;
```

This is the same logic, just cleaner with aliases.

---

## Why `student_id` is a good choice for counting

`student_id` is the primary key of the `Student` table, so:

- every real student row has a non-null `student_id`
- the null row created by `LEFT JOIN` has `student_id = NULL`

That makes it a reliable column to count.

Any guaranteed non-null student-side column would work, but `student_id` is the safest and clearest choice.

---

## Full reasoning in one sentence

We use `LEFT OUTER JOIN` to keep all departments, and `COUNT(student_id)` to count only real students while ignoring the null placeholder rows for empty departments.

---

## Final Answer

```sql
SELECT
    dept_name,
    COUNT(student_id) AS student_number
FROM Department
LEFT OUTER JOIN Student
    ON Department.dept_id = Student.dept_id
GROUP BY Department.dept_name
ORDER BY student_number DESC, Department.dept_name;
```

---

## Complexity

Let:

- `D` = number of departments
- `S` = number of students

### Time Complexity

Typically `O(D + S)` to process the join and grouping, depending on indexing and execution plan.

For interview-style reasoning, it is usually acceptable to say:

```text
O(number of joined rows)
```

### Space Complexity

`O(D)` for grouped output, or more generally the extra space needed by the database engine for grouping.

---

## Key Takeaways

1. Use `LEFT OUTER JOIN` when you must keep rows from the left table even if no match exists.
2. `COUNT(*)` counts rows, including null placeholder rows created by `LEFT JOIN`.
3. `COUNT(column)` ignores `NULL`.
4. To count matches from the right table after a `LEFT JOIN`, use a non-nullable column from the right table.
5. In this problem, `COUNT(student_id)` is the correct choice.
