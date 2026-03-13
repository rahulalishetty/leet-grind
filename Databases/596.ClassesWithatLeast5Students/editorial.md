# 596. Classes With at Least 5 Students

## Detailed Summary of Two Accepted Approaches

We are given a table `Courses` where each row represents one student enrolled in one class.

The goal is to return all classes that have **at least 5 students**.

Since each row corresponds to one enrollment, the problem reduces to:

1. group rows by `class`
2. count how many students are in each class
3. keep only the classes whose count is at least 5

There are two common ways to do that:

1. use a subquery after `GROUP BY`
2. use `GROUP BY` together with `HAVING`

---

# Example input

| student | class    |
| ------- | -------- |
| A       | Math     |
| B       | English  |
| C       | Math     |
| D       | Biology  |
| E       | Math     |
| F       | Computer |
| G       | Math     |
| H       | Math     |
| I       | Math     |

---

# Step 1: Count students in each class

The first step in both approaches is the same.

We count how many students belong to each class:

```sql
SELECT
    class,
    COUNT(student)
FROM
    courses
GROUP BY class;
```

### Output

| class    | COUNT(student) |
| -------- | -------------- |
| Biology  | 1              |
| Computer | 1              |
| English  | 1              |
| Math     | 6              |

This shows that:

- Biology has 1 student
- Computer has 1 student
- English has 1 student
- Math has 6 students

Now we only want the classes where the count is at least 5.

---

# Approach 1: Use a subquery after `GROUP BY`

## Core idea

The first approach performs the grouping and counting inside a subquery.

Then the outer query filters the grouped result.

This is useful when you want to compute an intermediate aggregated table first and then apply a normal `WHERE` filter on that result.

---

## Query

```sql
SELECT
    class
FROM
    (
        SELECT
            class,
            COUNT(student) AS num
        FROM
            courses
        GROUP BY class
    ) AS temp_table
WHERE
    num >= 5;
```

---

## Step-by-step explanation

### Inner query

```sql
SELECT
    class,
    COUNT(student) AS num
FROM
    courses
GROUP BY class
```

This creates a temporary aggregated result like:

| class    | num |
| -------- | --- |
| Biology  | 1   |
| Computer | 1   |
| English  | 1   |
| Math     | 6   |

Here:

- rows are grouped by `class`
- `COUNT(student)` counts the number of students in each class
- the count is given the alias `num`

### Why give it an alias?

Because we want to refer to that count in the outer query:

```sql
WHERE num >= 5
```

Without the alias, the outer query would not have a simple column name to reference.

---

## Outer query

```sql
SELECT
    class
FROM
    (...) AS temp_table
WHERE
    num >= 5;
```

This filters the aggregated result and keeps only rows whose count is at least 5.

From the example:

- Biology -> 1 -> excluded
- Computer -> 1 -> excluded
- English -> 1 -> excluded
- Math -> 6 -> included

So the output is:

| class |
| ----- |
| Math  |

---

## Important note about aliases

The original explanation correctly notes that the alias is important.

In the subquery result:

```sql
COUNT(student) AS num
```

the name `num` becomes a column of the temporary table.

That lets the outer `WHERE` clause use:

```sql
WHERE num >= 5
```

because `num` now exists as a regular column in the outer query.

---

## Why not use `WHERE COUNT(student) >= 5` directly?

Because `WHERE` is evaluated before grouping.

That means this is invalid:

```sql
SELECT
    class
FROM
    courses
WHERE
    COUNT(student) >= 5
GROUP BY class;
```

`COUNT(student)` is an aggregate, and aggregates cannot be used in `WHERE`.

That is exactly why we either:

- use a subquery, or
- use `HAVING`

---

# Approach 1: Final query

```sql
SELECT
    class
FROM
    (
        SELECT
            class,
            COUNT(student) AS num
        FROM
            courses
        GROUP BY class
    ) AS temp_table
WHERE
    num >= 5;
```

---

# Approach 2: Use `GROUP BY` with `HAVING`

## Core idea

This is the more natural SQL solution.

Instead of first building a grouped result and then filtering it in an outer query, we filter grouped rows directly using `HAVING`.

`HAVING` is specifically designed for filtering groups after aggregation.

---

## Query

```sql
SELECT
    class
FROM
    courses
GROUP BY class
HAVING COUNT(student) >= 5;
```

---

## Why this works

### `GROUP BY class`

This groups all rows by class.

### `COUNT(student)`

This counts how many students belong to each class.

### `HAVING COUNT(student) >= 5`

This keeps only those groups whose count is at least 5.

So it directly expresses the problem:

- group by class
- keep classes with at least 5 students

---

## Why `HAVING` is better here

Compared with the subquery approach, this is:

- shorter
- more direct
- easier to read
- more idiomatic SQL

This is the standard pattern for filtering grouped results.

---

# Approach 2: Final query

```sql
SELECT
    class
FROM
    courses
GROUP BY class
HAVING COUNT(student) >= 5;
```

---

# Walkthrough on the sample

Input:

| student | class    |
| ------- | -------- |
| A       | Math     |
| B       | English  |
| C       | Math     |
| D       | Biology  |
| E       | Math     |
| F       | Computer |
| G       | Math     |
| H       | Math     |
| I       | Math     |

Grouped counts:

| class    | count |
| -------- | ----- |
| Biology  | 1     |
| Computer | 1     |
| English  | 1     |
| Math     | 6     |

Now apply the condition:

```sql
count >= 5
```

Only `Math` satisfies it.

Final output:

| class |
| ----- |
| Math  |

---

# `WHERE` vs `HAVING`

This distinction is very important.

## `WHERE`

- filters individual rows
- happens before grouping

## `HAVING`

- filters groups
- happens after grouping

Since `COUNT(student)` is a grouped result, it belongs in `HAVING`, not `WHERE`.

That is why this is correct:

```sql
SELECT
    class
FROM
    courses
GROUP BY class
HAVING COUNT(student) >= 5;
```

and this is wrong:

```sql
SELECT
    class
FROM
    courses
WHERE COUNT(student) >= 5
GROUP BY class;
```

---

# Why `COUNT(student)` works

Each row in `Courses` corresponds to one `(student, class)` pair.

Also, `(student, class)` is the primary key, so there are no duplicate enrollments for the same student in the same class.

That means:

```sql
COUNT(student)
```

correctly counts how many students are in each class.

You could also write:

```sql
COUNT(*)
```

and it would work as well in this problem, because each row represents one enrollment and `student` is not null in the intended logic.

For example:

```sql
SELECT
    class
FROM
    courses
GROUP BY class
HAVING COUNT(*) >= 5;
```

This is also valid.

Still, since the provided approach uses `COUNT(student)`, the summary keeps that form.

---

# Comparing the two approaches

## Approach 1: Subquery

### Strengths

- useful for understanding aggregation in stages
- makes the grouped result visible as a temporary table
- works well when you want to do more processing after grouping

### Tradeoffs

- longer
- more verbose
- less natural for this specific problem

## Approach 2: `HAVING`

### Strengths

- shorter
- cleaner
- standard SQL solution
- directly expresses the grouped filter

### Tradeoffs

- none for this problem; it is the preferred solution

---

# Final accepted implementations

## Approach 1

```sql
SELECT
    class
FROM
    (
        SELECT
            class,
            COUNT(student) AS num
        FROM
            courses
        GROUP BY class
    ) AS temp_table
WHERE
    num >= 5;
```

## Approach 2

```sql
SELECT
    class
FROM
    courses
GROUP BY class
HAVING COUNT(student) >= 5;
```

---

# Complexity

Let `n` be the number of rows in `Courses`.

## Time Complexity

Both approaches require scanning the table and grouping rows by class.

A practical interview-level summary is:

```text
O(n)
```

for counting/grouping, assuming efficient hashing by the database engine.

## Space Complexity

```text
O(k)
```

where `k` is the number of distinct classes, because the grouped result stores one row per class.

---

# Key takeaways

1. This is a grouping-and-counting problem.
2. `GROUP BY class` collects all rows for the same class.
3. `COUNT(student)` gives the number of students in each class.
4. If you filter after aggregation, use either:
   - a subquery, or
   - `HAVING`
5. `HAVING COUNT(student) >= 5` is the most direct and idiomatic solution.
