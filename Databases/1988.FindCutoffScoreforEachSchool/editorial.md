# 1988. Find Cutoff Score for Each School — Detailed Summary

## Approach: Conditional `LEFT JOIN` and Aggregation Query

This approach solves the problem by matching each school with all exam scores that are **safe** for that school's capacity.

A score is safe for a school if:

```text
student_count <= capacity
```

because `student_count` means the number of students who scored **at least** that score.

So if the school chooses that score as its cutoff, at most that many students may apply, and the school must be able to accept all of them.

After finding all safe scores for a school, we choose the **smallest score** among them, because:

- smaller score → more students can apply
- the problem wants to maximize the number of eligible applicants
- among equally valid options, choose the smallest score

If a school has no safe score at all, the answer must be `-1`.

---

## Problem Restatement

For each school, find the minimum score requirement such that:

1. every student who meets the requirement can be accepted
2. the number of students who qualify is as large as possible
3. the chosen score must exist in the `Exam` table

If there is no score in the table that can guarantee this, return:

```text
-1
```

---

## Core Insight

The `Exam` table tells us:

> for each score, how many students got **at least** that score

So if a school picks score `s` as its cutoff, then the maximum number of applicants is exactly:

```text
student_count at score s
```

That score is valid only if:

```text
student_count <= capacity
```

Among all valid scores, the school wants the one that allows the most students to apply.

Because lower scores correspond to **greater or equal** student counts, the best valid score is simply:

- the **smallest score** whose `student_count` is still within capacity

That is why the query uses:

```sql
MIN(score)
```

after filtering valid matches.

---

# Query

```sql
SELECT
  school_id,
  IFNULL(MIN(score), -1) AS score
FROM Schools
LEFT JOIN Exam
  ON capacity >= student_count
GROUP BY school_id;
```

---

# Step-by-Step Explanation

## 1. Join each school to all feasible exam scores

```sql
FROM Schools
LEFT JOIN Exam
  ON capacity >= student_count
```

### What this means

For each school, join all exam rows where:

```text
student_count <= capacity
```

Those are exactly the scores that are safe for that school.

Because if a score has `student_count` students at or above it, and that count is within the school's capacity, then the school can safely choose that score.

---

## Why `LEFT JOIN` Is Used

A `LEFT JOIN` keeps every school in the result, even if it matches **no** exam rows.

That is important, because some schools may not have any feasible score.

For those schools, the joined exam columns become `NULL`, and later we convert that to `-1`.

Without `LEFT JOIN`, such schools would disappear from the result, which would be wrong.

---

## 2. Group by school

```sql
GROUP BY school_id
```

This creates one result row per school.

Each group contains all exam scores that are valid for that school.

---

## 3. Choose the minimum valid score

```sql
MIN(score)
```

Among the valid scores for a school, choose the smallest score.

### Why the smallest score is correct

Suppose the valid scores for a school are:

- 844
- 749

Then choosing `749` is better because it allows at least as many students as `844`, and the problem says to maximize the number of possible applicants.

Because the `Exam` table is monotonic:

- lower score → same or larger `student_count`

So the best valid score is the **smallest valid score**.

---

## 4. Replace missing result with `-1`

```sql
IFNULL(MIN(score), -1) AS score
```

If a school has no valid matched exam row, then `MIN(score)` is `NULL`.

That means:

- for every known score, `student_count > capacity`
- the available data is not enough to determine a valid cutoff

So the query returns:

```text
-1
```

which matches the problem requirement.

---

# Example Walkthrough

## Input

### Schools

| school_id | capacity |
| --------: | -------: |
|        11 |      151 |
|         5 |       48 |
|         9 |        9 |
|        10 |       99 |

### Exam

| score | student_count |
| ----: | ------------: |
|   975 |            10 |
|   966 |            60 |
|   844 |            76 |
|   749 |            76 |
|   744 |           100 |

---

# Per-School Analysis

## School 5

Capacity:

```text
48
```

Check exam rows:

| score | student_count | valid? |
| ----: | ------------: | ------ |
|   975 |            10 | yes    |
|   966 |            60 | no     |
|   844 |            76 | no     |
|   749 |            76 | no     |
|   744 |           100 | no     |

Only valid score:

- `975`

So:

```text
MIN(valid scores) = 975
```

Result:

| school_id | score |
| --------: | ----: |
|         5 |   975 |

---

## School 10

Capacity:

```text
99
```

Check exam rows:

| score | student_count | valid? |
| ----: | ------------: | ------ |
|   975 |            10 | yes    |
|   966 |            60 | yes    |
|   844 |            76 | yes    |
|   749 |            76 | yes    |
|   744 |           100 | no     |

Valid scores:

- `975`
- `966`
- `844`
- `749`

Choose the smallest:

```text
749
```

Result:

| school_id | score |
| --------: | ----: |
|        10 |   749 |

---

## School 11

Capacity:

```text
151
```

Check exam rows:

| score | student_count | valid? |
| ----: | ------------: | ------ |
|   975 |            10 | yes    |
|   966 |            60 | yes    |
|   844 |            76 | yes    |
|   749 |            76 | yes    |
|   744 |           100 | yes    |

All rows are valid, so choose the smallest score:

```text
744
```

Result:

| school_id | score |
| --------: | ----: |
|        11 |   744 |

---

## School 9

Capacity:

```text
9
```

Check exam rows:

| score | student_count | valid? |
| ----: | ------------: | ------ |
|   975 |            10 | no     |
|   966 |            60 | no     |
|   844 |            76 | no     |
|   749 |            76 | no     |
|   744 |           100 | no     |

No exam row satisfies:

```text
student_count <= 9
```

So `MIN(score)` is `NULL`, and:

```sql
IFNULL(NULL, -1) = -1
```

Result:

| school_id | score |
| --------: | ----: |
|         9 |    -1 |

---

# Final Output

| school_id | score |
| --------: | ----: |
|         5 |   975 |
|         9 |    -1 |
|        10 |   749 |
|        11 |   744 |

---

# Why This Approach Is Elegant

This solution is concise because it uses the structure of the `Exam` table directly.

Instead of computing complicated search logic, it relies on a simple observation:

- a school can safely choose any score where `student_count <= capacity`
- among those, the smallest score is optimal

So the whole problem reduces to:

1. find valid scores with a join condition
2. take `MIN(score)` per school
3. replace missing values with `-1`

That is why the query is short and effective.

---

# Clause-by-Clause Breakdown

## `SELECT school_id, IFNULL(MIN(score), -1) AS score`

Returns:

- the school ID
- the chosen cutoff score
- or `-1` if no score is feasible

---

## `FROM Schools LEFT JOIN Exam ON capacity >= student_count`

Matches schools to all exam rows that are within capacity.

---

## `GROUP BY school_id`

Produces one row per school.

---

# Why `MIN(score)` and Not `MAX(score)`

This is a subtle but important point.

A school wants to maximize the number of students who can apply.

Because lower scores allow more students, once we know which scores are safe, we should choose the **smallest** safe score.

Using `MAX(score)` would choose the strictest valid score, which would unnecessarily reduce the applicant pool.

So `MIN(score)` is the correct choice.

---

# Why the Monotonic Exam Property Matters

The problem states that if one score is higher, its `student_count` is less than or equal to the count for a lower score.

That monotonicity guarantees that the smallest safe score is exactly the best cutoff.

Without that property, the logic would be less straightforward.

---

# Important Edge Case

If a school's capacity is smaller than the smallest `student_count` in the `Exam` table, then no score in the table is safe.

In that situation:

- we do not know whether some higher unlisted score might work
- the available data is insufficient

So the answer must be:

```text
-1
```

This is exactly what `IFNULL(MIN(score), -1)` captures after the `LEFT JOIN`.

---

# Cleaner Explicit Version

A slightly more explicit version is:

```sql
SELECT
  s.school_id,
  COALESCE(MIN(e.score), -1) AS score
FROM Schools s
LEFT JOIN Exam e
  ON s.capacity >= e.student_count
GROUP BY s.school_id;
```

This version uses table aliases and `COALESCE`, which many people find clearer.

It is logically the same as the original query.

---

# Complexity Analysis

Let:

- `S` = number of schools
- `E` = number of exam rows

The join conceptually compares each school with exam score rows.

So the main work is around:

```text
O(S × E)
```

followed by grouping by school.

For typical SQL problem sizes, this is perfectly fine, and the query is very readable.

---

# Final Recommended Query

```sql
SELECT
  s.school_id,
  COALESCE(MIN(e.score), -1) AS score
FROM Schools s
LEFT JOIN Exam e
  ON s.capacity >= e.student_count
GROUP BY s.school_id;
```

---

# Key Takeaways

- A score is valid for a school if `student_count <= capacity`
- Among valid scores, choose the smallest score
- Use `LEFT JOIN` so schools with no valid score are still included
- Use `MIN(score)` to select the optimal cutoff
- Use `IFNULL` or `COALESCE` to return `-1` when no score can be determined

---
