# Survey Log: Highest Answer Rate

## Problem Overview

We want to find the `question_id` that has the **highest answer rate**.

The answer rate for a question is:

```text
number of answers / number of shows
```

If multiple questions have the same highest answer rate, we must return the **smallest `question_id`** among them.

---

## General Strategy

The problem can be broken into four clear steps:

1. Calculate the answer rate for each `question_id`
2. Identify the highest answer rate
3. If multiple questions share that highest rate, choose the smallest `question_id`
4. Return the result with the required column name: `survey_log`

---

## Understanding the Answer Rate

For each `question_id`:

- count how many rows have `action = 'answer'`
- count how many rows have `action = 'show'`
- divide answers by shows

A common SQL pattern for conditional counting is:

```sql
SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
```

and

```sql
SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END)
```

Because the data is grouped by `question_id`, these expressions give us the numerator and denominator for each question.

---

# Approach 1: Using `RANK()`

## Intuition

This approach follows the problem statement very directly:

- first compute the answer rate for every question
- then rank the questions:
  - higher answer rate comes first
  - if rates tie, smaller `question_id` comes first
- finally return the row with rank `1`

This is clean and expressive, especially when window functions are available.

---

## Step 1: Compute answer rate for each question

We can use a CTE:

```sql
WITH answer_rate AS (
    SELECT
        question_id,
        SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
        / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) AS rate
    FROM surveylog
    GROUP BY question_id
)
```

### What this does

- `GROUP BY question_id` creates one row per question
- `SUM(CASE WHEN ...)` counts answers and shows separately
- division gives the answer rate

---

## Example intermediate result

Suppose the CTE produces:

| question_id | rate |
| ----------: | ---: |
|         285 |    1 |
|         369 |    0 |

That means:

- question `285` was answered every time it was shown
- question `369` was never answered

So question `285` is better.

---

## Step 2: Rank the questions

Now we rank by:

1. `rate DESC`
2. `question_id ASC`

```sql
SELECT
    question_id,
    RANK() OVER (ORDER BY rate DESC, question_id ASC) AS rnk
FROM answer_rate;
```

### Why this ordering?

- `rate DESC` puts the highest answer rate first
- `question_id ASC` ensures that if two questions have the same rate, the smaller ID comes first

---

## Example ranked result

| question_id | rnk |
| ----------: | --: |
|         285 |   1 |
|         369 |   2 |

So the desired answer is the row with `rnk = 1`.

---

## Step 3: Select the top-ranked question

```sql
SELECT question_id AS survey_log
FROM (
    SELECT
        question_id,
        RANK() OVER (ORDER BY rate DESC, question_id ASC) AS rnk
    FROM answer_rate
) AS t0
WHERE rnk = 1;
```

---

## Full Implementation for Approach 1

```sql
WITH answer_rate AS (
    SELECT
        question_id,
        SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
        / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) AS rate
    FROM surveylog
    GROUP BY question_id
)
SELECT question_id AS survey_log
FROM (
    SELECT
        question_id,
        RANK() OVER (ORDER BY rate DESC, question_id ASC) AS rnk
    FROM answer_rate
) AS t0
WHERE rnk = 1;
```

---

## Important correction

In the original draft, this line appeared:

```sql
RANK()OVER(ORDER BY rate DESC question_id)
```

That is missing a comma.

The correct syntax is:

```sql
RANK() OVER (ORDER BY rate DESC, question_id ASC)
```

Without the comma, the SQL is invalid.

---

## Why `RANK()` works here

Because after sorting by:

- highest rate first
- smallest question ID first

the row with `rnk = 1` is exactly the question we need.

Even though `RANK()` can assign the same rank to ties, the secondary sort on `question_id ASC` ensures the smallest ID is encountered first in the ordering logic. In practice, for this problem, using `ROW_NUMBER()` would often be even more explicit, but `RANK()` can still be used in this ordered setup.

---

## Slightly cleaner window-function version

```sql
WITH answer_rate AS (
    SELECT
        question_id,
        SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
        / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) AS rate
    FROM surveylog
    GROUP BY question_id
)
SELECT question_id AS survey_log
FROM (
    SELECT
        question_id,
        ROW_NUMBER() OVER (ORDER BY rate DESC, question_id ASC) AS rn
    FROM answer_rate
) t
WHERE rn = 1;
```

This uses `ROW_NUMBER()` instead of `RANK()`, which is often conceptually better when you want exactly one row.

---

# Approach 2: Using `ORDER BY` + `LIMIT`

## Intuition

This approach is more compact.

Instead of:

- computing rate in a CTE
- ranking rows
- filtering rank `1`

we simply:

- group by `question_id`
- order by answer rate descending
- break ties by smaller `question_id`
- keep only the first row with `LIMIT 1`

This is usually the simplest solution.

---

## Query

```sql
SELECT question_id AS survey_log
FROM surveylog
GROUP BY question_id
ORDER BY
    SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
    / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) DESC,
    question_id ASC
LIMIT 1;
```

---

## Explanation

### `GROUP BY question_id`

Creates one row per question.

### First ordering key

```sql
SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
/
SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) DESC
```

This computes the answer rate and sorts from highest to lowest.

### Second ordering key

```sql
question_id ASC
```

If multiple questions have the same highest rate, the smaller `question_id` comes first.

### `LIMIT 1`

Keeps only the best row after sorting.

---

## Full Implementation for Approach 2

```sql
SELECT question_id AS survey_log
FROM surveylog
GROUP BY question_id
ORDER BY
    SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END)
    / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) DESC,
    question_id ASC
LIMIT 1;
```

---

# Dry Run Example

Suppose `surveylog` contains rows such that:

- question `285`:
  - shown 3 times
  - answered 3 times
  - rate = `3 / 3 = 1`
- question `369`:
  - shown 4 times
  - answered 0 times
  - rate = `0 / 4 = 0`

Grouped result:

| question_id | answers | shows | rate |
| ----------: | ------: | ----: | ---: |
|         285 |       3 |     3 |    1 |
|         369 |       0 |     4 |    0 |

Sort by:

1. rate descending
2. question_id ascending

Sorted result:

| question_id | rate |
| ----------: | ---: |
|         285 |    1 |
|         369 |    0 |

Take first row:

| survey_log |
| ---------: |
|        285 |

---

# Comparing the Two Approaches

## Approach 1: `RANK()` / window function

### Pros

- very explicit
- separates calculation and selection clearly
- useful when you want richer ranking logic

### Cons

- longer
- slightly more complex than needed for this problem

---

## Approach 2: `ORDER BY` + `LIMIT`

### Pros

- concise
- easy to read
- usually the most practical solution here

### Cons

- packs more logic directly into the `ORDER BY`, which some people find less step-by-step

---

# Recommended Approach

For this problem, **Approach 2** is generally the best choice because:

- it is shorter
- it is easier to write in an interview or coding platform
- it directly matches the problem requirement

Use Approach 1 when you want to emphasize a more structured, analytical SQL style.

---

# Edge Cases to Think About

## 1. Tie on answer rate

If two questions have the same answer rate, we must return the smaller `question_id`.

That is why both approaches include:

```sql
question_id ASC
```

as a secondary sort key.

---

## 2. Integer division

Depending on the SQL engine, dividing two integers may produce integer truncation instead of a decimal.

To be safer, many SQL solutions force floating-point division:

```sql
SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END) * 1.0
/
SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END)
```

So a more robust version is:

```sql
SELECT question_id AS survey_log
FROM surveylog
GROUP BY question_id
ORDER BY
    SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END) * 1.0
    / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) DESC,
    question_id ASC
LIMIT 1;
```

This avoids accidental integer truncation.

---

## 3. Division by zero

In the standard problem setup, each valid question should have at least one `show` event. If not, division by zero would need special handling. Most solutions assume the data is valid according to the problem constraints.

---

# Final Clean Solutions

## Solution 1: Using `RANK()`

```sql
WITH answer_rate AS (
    SELECT
        question_id,
        SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END) * 1.0
        / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) AS rate
    FROM surveylog
    GROUP BY question_id
)
SELECT question_id AS survey_log
FROM (
    SELECT
        question_id,
        ROW_NUMBER() OVER (ORDER BY rate DESC, question_id ASC) AS rn
    FROM answer_rate
) t
WHERE rn = 1;
```

---

## Solution 2: Using `ORDER BY` + `LIMIT`

```sql
SELECT question_id AS survey_log
FROM surveylog
GROUP BY question_id
ORDER BY
    SUM(CASE WHEN action = 'answer' THEN 1 ELSE 0 END) * 1.0
    / SUM(CASE WHEN action = 'show' THEN 1 ELSE 0 END) DESC,
    question_id ASC
LIMIT 1;
```

---

# Key Takeaways

- Use `GROUP BY question_id` to compute per-question metrics.
- Use `SUM(CASE WHEN ...)` for conditional counting.
- The answer rate is `answers / shows`.
- Sort by:
  1. highest rate first
  2. smallest `question_id` first
- `LIMIT 1` is the simplest way to return the correct row.
- When precision matters, force floating-point division with `* 1.0`.

---

# Final Verdict

Both approaches are valid.

- Choose **Approach 1** if you want a structured solution with intermediate steps.
- Choose **Approach 2** if you want the cleanest and most practical SQL answer.
