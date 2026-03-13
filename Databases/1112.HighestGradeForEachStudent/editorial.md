# Highest Grade Per Student

## Detailed Summary of Three Approaches

We need to return, for each student:

- the **highest grade**
- the **course** where that highest grade was obtained

If a student has the same highest grade in multiple courses, we must return the one with the **smallest `course_id`**.

So this is a **top-per-student** problem with an additional tie-break rule.

This summary covers three approaches:

1. **Window Function**
2. **Aggregation + Self-Join**
3. **Subquery with Aggregation**

All three are correct.
They differ mainly in style and how they express:

- “find the highest grade”
- “break ties using the smallest course id”

---

# Core observation

For each `student_id`, we need to choose exactly one row according to this ordering priority:

1. highest `grade`
2. if tied, smallest `course_id`

So conceptually, for each student, we want the row that would come first under:

```sql
ORDER BY grade DESC, course_id ASC
```

That viewpoint is especially useful for the window-function solution.

---

# Approach 1: Window Function

## Core idea

This approach ranks each student’s rows using:

```sql
ORDER BY grade DESC, course_id ASC
```

Then it keeps only the rows with rank `1`.

That means:

- highest grade comes first
- if multiple rows share the same grade, the smaller course id comes first

So rank 1 is exactly the row we need.

---

## Step 1: Start from the raw data

### Enrollments

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          2 |         2 |    95 |
|          2 |         3 |    95 |
|          1 |         1 |    90 |
|          1 |         2 |    99 |
|          3 |         1 |    80 |
|          3 |         2 |    75 |
|          3 |         3 |    82 |

---

## Step 2: Rank rows inside each student partition

The query uses:

```sql
DENSE_RANK() OVER (
  PARTITION BY student_id
  ORDER BY grade DESC, course_id ASC
) AS rnk
```

This ranks each student’s rows from best to worst according to the required rules.

---

## Why this works

### `PARTITION BY student_id`

Ranking restarts separately for each student.

### `ORDER BY grade DESC`

Higher grades come first.

### `ORDER BY course_id ASC`

If two rows have the same grade, the smaller course id comes first.

That exactly matches the tie-breaking rule.

---

## Ranked result for the sample

| student_id | course_id | grade | rnk |
| ---------: | --------: | ----: | --: |
|          1 |         2 |    99 |   1 |
|          1 |         1 |    90 |   2 |
|          2 |         2 |    95 |   1 |
|          2 |         3 |    95 |   2 |
|          3 |         3 |    82 |   1 |
|          3 |         1 |    80 |   2 |
|          3 |         2 |    75 |   3 |

Notice the important point for student `2`:

- course `2` and course `3` both have grade `95`
- because we sort by `course_id ASC` after sorting by grade descending, course `2` comes first
- therefore course `2` gets rank `1`
- course `3` gets rank `2`

So the tie-break is handled naturally by the ordering.

---

## Step 3: Keep only the top-ranked row

```sql
SELECT
  student_id,
  course_id,
  grade
FROM
  (
    SELECT
      student_id,
      course_id,
      grade,
      DENSE_RANK() OVER (
        PARTITION BY student_id
        ORDER BY
          grade DESC,
          course_id ASC
      ) AS rnk
    FROM
      Enrollments
  ) AS ranked
WHERE
  rnk = 1
ORDER BY
  student_id;
```

---

## Why this works

Once the rows are ranked correctly, the required row for each student is simply the row with:

```sql
rnk = 1
```

That returns exactly one row per student in this setup, because the tie-break is resolved inside the `ORDER BY`.

---

## Final query for Approach 1

```sql
SELECT
  student_id,
  course_id,
  grade
FROM
  (
    SELECT
      student_id,
      course_id,
      grade,
      DENSE_RANK() OVER (
        PARTITION BY student_id
        ORDER BY
          grade DESC,
          course_id ASC
      ) AS rnk
    FROM
      Enrollments
  ) AS ranked
WHERE
  rnk = 1
ORDER BY
  student_id;
```

---

## Strengths of Approach 1

- direct
- elegant
- naturally expresses both:
  - best grade
  - tie-break by smallest course id

### Tradeoff

- requires familiarity with window functions
- `ROW_NUMBER()` would actually be an even more natural choice here since only one row should survive after the tie-break ordering

Still, the provided `DENSE_RANK()` works because the full ordering differentiates tied grades by course id.

---

# Approach 2: Aggregation & Self-Join

## Core idea

This approach breaks the task into two stages:

1. find each student’s maximum grade
2. join back to the original table to keep only rows with that maximum grade
3. if multiple rows remain because of a tie in grade, use `MIN(course_id)` to pick the smallest course

This is a classic aggregation-then-filter approach.

---

## Step 1: Find the highest grade for each student

```sql
SELECT
  student_id,
  MAX(grade) AS max_grade
FROM
  Enrollments
GROUP BY
  student_id
```

---

## Why this works

This groups rows by student and finds the top grade in each group.

For the sample:

| student_id | max_grade |
| ---------: | --------: |
|          1 |        99 |
|          2 |        95 |
|          3 |        82 |

---

## Step 2: Join back to the original table

Now we join the original table to those maximum-grade values:

```sql
SELECT
  e1.student_id,
  MIN(e1.course_id) AS course_id,
  e1.grade
FROM
  Enrollments e1
  JOIN (
    SELECT
      student_id,
      MAX(grade) AS max_grade
    FROM
      Enrollments
    GROUP BY
      student_id
  ) e2
    ON e1.student_id = e2.student_id
   AND e1.grade = e2.max_grade
GROUP BY
  e1.student_id,
  e1.grade
ORDER BY
  e1.student_id;
```

---

## Why this works

### Join condition

```sql
e1.student_id = e2.student_id
AND e1.grade = e2.max_grade
```

This keeps only the rows whose grade is the highest for that student.

For student `2`, after the join we get:

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          2 |         2 |    95 |
|          2 |         3 |    95 |

Because both rows match the maximum grade.

---

## Step 3: Break ties with `MIN(course_id)`

After the join, tied highest-grade rows may still remain.

So we resolve that by grouping and choosing:

```sql
MIN(e1.course_id)
```

That gives the smallest course id among the highest-grade rows.

---

## Final query for Approach 2

```sql
SELECT
  e1.student_id,
  MIN(e1.course_id) AS course_id,
  e1.grade
FROM
  Enrollments e1
  JOIN (
    SELECT
      student_id,
      MAX(grade) AS max_grade
    FROM
      Enrollments
    GROUP BY
      student_id
  ) e2
    ON e1.student_id = e2.student_id
   AND e1.grade = e2.max_grade
GROUP BY
  e1.student_id,
  e1.grade
ORDER BY
  e1.student_id;
```

---

## Strengths of Approach 2

- based on familiar aggregate logic
- easy to explain step by step
- cleanly separates “find max grade” from “break tie by smallest course”

### Tradeoff

- slightly more verbose than the window-function solution
- requires a join-back step

---

# Approach 3: Subquery with Aggregation

## Core idea

This approach is very similar to Approach 2, but instead of joining to the max-grade subquery, it uses a tuple-based `IN` filter.

The logic is:

1. compute `(student_id, max_grade)` pairs
2. keep rows whose `(student_id, grade)` matches those pairs
3. resolve ties using `MIN(course_id)`

So it is aggregation + filtering, but without an explicit join.

---

## Step 1: Compute max grade per student

The subquery is:

```sql
SELECT
  student_id,
  MAX(grade)
FROM
  Enrollments
GROUP BY
  student_id
```

This gives:

| student_id | max_grade |
| ---------: | --------: |
|          1 |        99 |
|          2 |        95 |
|          3 |        82 |

---

## Step 2: Filter rows using tuple matching

```sql
WHERE
  (student_id, grade) IN (
    SELECT
      student_id,
      MAX(grade)
    FROM
      Enrollments
    GROUP BY
      student_id
  )
```

---

## Why this works

The tuple condition:

```sql
(student_id, grade)
```

ensures that we keep only rows where the student’s grade equals that student’s maximum grade.

For student `2`, both rows:

- `(2, 95)` course 2
- `(2, 95)` course 3

match the tuple condition.

So both remain, and we still need to resolve the tie by smallest course id.

---

## Step 3: Break ties with `MIN(course_id)`

That is done with:

```sql
GROUP BY
  student_id,
  grade
```

and:

```sql
MIN(course_id)
```

So among all highest-grade rows for a student, we pick the smallest course id.

---

## Final query for Approach 3

```sql
SELECT
  student_id,
  MIN(course_id) AS course_id,
  grade
FROM
  Enrollments
WHERE
  (student_id, grade) IN (
    SELECT
      student_id,
      MAX(grade)
    FROM
      Enrollments
    GROUP BY
      student_id
  )
GROUP BY
  student_id,
  grade
ORDER BY
  student_id;
```

---

## Strengths of Approach 3

- shorter than the self-join version
- still uses simple aggregate logic
- nice use of tuple comparison

### Tradeoff

- tuple-based `IN` can feel less explicit than a join
- logically very similar to Approach 2, so it may not feel fundamentally different

---

# Walkthrough on the sample

## Input

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          2 |         2 |    95 |
|          2 |         3 |    95 |
|          1 |         1 |    90 |
|          1 |         2 |    99 |
|          3 |         1 |    80 |
|          3 |         2 |    75 |
|          3 |         3 |    82 |

---

## Student 1

Rows:

| course_id | grade |
| --------: | ----: |
|         1 |    90 |
|         2 |    99 |

Highest grade:

```text
99
```

So choose:

```text
course 2
```

Result:

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          1 |         2 |    99 |

---

## Student 2

Rows:

| course_id | grade |
| --------: | ----: |
|         2 |    95 |
|         3 |    95 |

Highest grade:

```text
95
```

Tie on grade.
Smallest course id:

```text
2
```

Result:

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          2 |         2 |    95 |

---

## Student 3

Rows:

| course_id | grade |
| --------: | ----: |
|         1 |    80 |
|         2 |    75 |
|         3 |    82 |

Highest grade:

```text
82
```

So choose:

```text
course 3
```

Result:

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          3 |         3 |    82 |

---

## Final result

| student_id | course_id | grade |
| ---------: | --------: | ----: |
|          1 |         2 |    99 |
|          2 |         2 |    95 |
|          3 |         3 |    82 |

---

# Comparing the three approaches

## Approach 1: Window Function

### Best when

- you want the most direct “pick best row per student” logic
- you are comfortable with analytic functions

### Pros

- concise
- elegant
- handles ranking and tie-break in one place

### Cons

- requires familiarity with window functions

---

## Approach 2: Aggregation + Self-Join

### Best when

- you prefer classical SQL grouping logic
- you want the max-grade step to be very explicit

### Pros

- easy to explain in stages
- very readable

### Cons

- requires a join-back step

---

## Approach 3: Subquery with Aggregation

### Best when

- you prefer a filtered-subquery style
- you want to avoid an explicit join

### Pros

- compact
- clean use of tuple comparison

### Cons

- conceptually close to Approach 2
- tuple-based `IN` can be slightly less intuitive for some readers

---

# Important SQL concepts used here

## 1. Window functions

Used in Approach 1 to rank rows within each student.

## 2. `MAX(grade)`

Used in Approaches 2 and 3 to identify the highest grade per student.

## 3. `MIN(course_id)`

Used in Approaches 2 and 3 to resolve ties by picking the smallest course id.

## 4. Self-join / join-back pattern

Used in Approach 2 to recover rows matching the maximum grade.

## 5. Tuple comparison with `IN`

Used in Approach 3 to filter rows matching `(student_id, max_grade)` pairs.

---

# A subtle note about `DENSE_RANK()` in Approach 1

The provided solution uses:

```sql
DENSE_RANK() OVER (
  PARTITION BY student_id
  ORDER BY grade DESC, course_id ASC
)
```

Because both `grade` and `course_id` are included in the ordering, rows with the same grade but different course ids do **not** tie under the full ordering. So only the smallest `course_id` among the top grades gets rank 1.

That means the query works correctly.

Conceptually, `ROW_NUMBER()` would also be a very natural fit here, because the goal is to keep exactly one best row after fully specifying the priority order.

---

# Key takeaways

1. For each student, the desired row is the one that comes first under:
   - `grade DESC`
   - `course_id ASC`
2. Window functions can express that directly with ranking.
3. Aggregation-based approaches first find the max grade, then resolve ties with `MIN(course_id)`.
4. All three approaches correctly handle the tie-break rule.

---

## Final accepted implementations

### Approach 1: Window Function

```sql
SELECT
  student_id,
  course_id,
  grade
FROM
  (
    SELECT
      student_id,
      course_id,
      grade,
      DENSE_RANK() OVER (
        PARTITION BY student_id
        ORDER BY
          grade DESC,
          course_id ASC
      ) AS rnk
    FROM
      Enrollments
  ) AS ranked
WHERE
  rnk = 1
ORDER BY
  student_id;
```

### Approach 2: Aggregation & Self-Join

```sql
SELECT
  e1.student_id,
  MIN(e1.course_id) AS course_id,
  e1.grade
FROM
  Enrollments e1
  JOIN (
    SELECT
      student_id,
      MAX(grade) AS max_grade
    FROM
      Enrollments
    GROUP BY
      student_id
  ) e2 ON e1.student_id = e2.student_id
  AND e1.grade = e2.max_grade
GROUP BY
  e1.student_id,
  e1.grade
ORDER BY
  e1.student_id;
```

### Approach 3: Subquery with Aggregation

```sql
SELECT
  student_id,
  MIN(course_id) AS course_id,
  grade
FROM
  Enrollments
WHERE
  (student_id, grade) IN (
    SELECT
      student_id,
      MAX(grade)
    FROM
      Enrollments
    GROUP BY
      student_id
  )
GROUP BY
  student_id,
  grade
ORDER BY
  student_id;
```
