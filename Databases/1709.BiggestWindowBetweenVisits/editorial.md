# 1709. Biggest Window Between Visits — Detailed Summary

## Problem Restatement

For each `user_id`, we need to find the **largest window in days** between:

- one visit and the **next** visit, or
- the **last** visit and **today**, where today is fixed as:

```text
2021-01-01
```

The result should return:

- `user_id`
- `biggest_window`

ordered by `user_id`.

---

## What Needs to Be Done

To solve this correctly, there are four core tasks:

1. For each `user_id`, sort `visit_date` in ascending order
2. Treat `2021-01-01` as the next date after the user's last visit
3. Compute the day difference between each visit and the next date
4. Take the maximum such difference per user

These steps can be implemented in different ways.

---

# Approach 1: Find Next Using `LEAD()` + Append Value Using `IFNULL()`

## Main Idea

This approach handles the first three tasks in one shot:

- sort visits per user
- find the next visit date
- if there is no next visit, use `2021-01-01` instead

Then compute the difference in days between:

- current visit date
- next visit date or `2021-01-01`

Finally, group by `user_id` and take the maximum difference.

---

## Why `LEAD()` Fits Naturally

`LEAD()` is designed exactly for this kind of problem.

It allows us to look at the **next row's value** within a sorted partition.

Here:

- partition by `user_id`
- order by `visit_date`

So for each user's visit, `LEAD(visit_date)` returns that user's next visit date.

---

## Step 1: Compute the window after each visit

```sql
SELECT
    user_id,
    visit_date,
    DATEDIFF(
        IFNULL(
            LEAD(visit_date, 1) OVER (
                PARTITION BY user_id
                ORDER BY visit_date
            ),
            '2021-01-01'
        ),
        visit_date
    ) AS w
FROM UserVisits;
```

---

## Clause-by-Clause Explanation

## `LEAD(visit_date, 1) OVER (...)`

```sql
LEAD(visit_date, 1) OVER (
    PARTITION BY user_id
    ORDER BY visit_date
)
```

This means:

- for each user separately
- sort visits by date
- get the next `visit_date`

Example for user `1`:

| visit_date | next visit from `LEAD()` |
| ---------- | ------------------------ |
| 2020-10-20 | 2020-11-28               |
| 2020-11-28 | 2020-12-03               |
| 2020-12-03 | NULL                     |

The last row has no next visit, so `LEAD()` returns `NULL`.

---

## `IFNULL(..., '2021-01-01')`

```sql
IFNULL(LEAD(...), '2021-01-01')
```

This replaces the `NULL` from the last visit with today's date:

```text
2021-01-01
```

So the last visit can also produce a valid window.

For user `1`:

| visit_date | next date after `IFNULL()` |
| ---------- | -------------------------- |
| 2020-10-20 | 2020-11-28                 |
| 2020-11-28 | 2020-12-03                 |
| 2020-12-03 | 2021-01-01                 |

---

## `DATEDIFF(..., visit_date)`

```sql
DATEDIFF(next_date, visit_date)
```

This computes the number of days between the current visit and the next date.

So the intermediate result becomes:

| user_id | visit_date |   w |
| ------: | ---------- | --: |
|       1 | 2020-10-20 |  39 |
|       1 | 2020-11-28 |   5 |
|       1 | 2020-12-03 |  29 |
|       2 | 2020-10-05 |  65 |
|       2 | 2020-12-09 |  23 |
|       3 | 2020-11-11 |  51 |

This `w` column is the window size for each visit.

---

## Step 2: Take the largest window per user

Now we wrap the previous query and take the maximum `w` for each user.

```sql
SELECT user_id, MAX(w) AS biggest_window
FROM (
    SELECT
        user_id,
        visit_date,
        DATEDIFF(
            IFNULL(
                LEAD(visit_date, 1) OVER (
                    PARTITION BY user_id
                    ORDER BY visit_date
                ),
                '2021-01-01'
            ),
            visit_date
        ) AS w
    FROM UserVisits
) AS a
GROUP BY user_id;
```

---

## Full Implementation — Approach 1

```sql
SELECT user_id, MAX(w) AS biggest_window
FROM (
    SELECT
        user_id,
        visit_date,
        DATEDIFF(
            IFNULL(
                LEAD(visit_date, 1) OVER (
                    PARTITION BY user_id
                    ORDER BY visit_date
                ),
                '2021-01-01'
            ),
            visit_date
        ) AS w
    FROM UserVisits
) AS a
GROUP BY user_id;
```

---

## Worked Example for Approach 1

### User 1

Visits:

- 2020-10-20
- 2020-11-28
- 2020-12-03

Computed windows:

- 2020-10-20 → 2020-11-28 = 39
- 2020-11-28 → 2020-12-03 = 5
- 2020-12-03 → 2021-01-01 = 29

Largest:

```text
39
```

### User 2

Visits:

- 2020-10-05
- 2020-12-09

Windows:

- 2020-10-05 → 2020-12-09 = 65
- 2020-12-09 → 2021-01-01 = 23

Largest:

```text
65
```

### User 3

Visits:

- 2020-11-11

Only window:

- 2020-11-11 → 2021-01-01 = 51

Largest:

```text
51
```

---

# Approach 2: Find the Next Visit Using `RANK()`

## Main Idea

This approach builds the next-date comparison manually.

Instead of using `LEAD()`, it does this:

1. append `2021-01-01` as an extra row for each user
2. rank all dates per user in ascending order
3. self-join rank `n` with rank `n+1`
4. compute day differences between consecutive ranked rows
5. take the maximum difference per user

This is more manual than `LEAD()`, but still valid and instructive.

---

## Step 1: Add today's date for each user

```sql
WITH all_dates AS (
    SELECT user_id, visit_date
    FROM UserVisits

    UNION

    SELECT user_id, '2021-01-01' AS visit_date
    FROM UserVisits
)
```

---

## Why This Step Is Needed

The problem says the last window is from the user's last visit to today.

So instead of handling that with `IFNULL`, this approach explicitly inserts today's date into each user's timeline.

That means for every user, the last comparison can be treated exactly the same as all earlier comparisons.

---

## Example output of `all_dates`

| user_id | visit_date |
| ------: | ---------- |
|       1 | 2020-10-20 |
|       1 | 2020-11-28 |
|       1 | 2020-12-03 |
|       1 | 2021-01-01 |
|       2 | 2020-10-05 |
|       2 | 2020-12-09 |
|       2 | 2021-01-01 |
|       3 | 2020-11-11 |
|       3 | 2021-01-01 |

---

## Step 2: Rank dates per user

```sql
SELECT *,
    RANK() OVER (
        PARTITION BY user_id
        ORDER BY visit_date
    ) AS date_rnk
FROM all_dates;
```

This assigns ascending position numbers to each visit date for each user.

---

## Example ranked result

| user_id | visit_date | date_rnk |
| ------: | ---------- | -------: |
|       1 | 2020-10-20 |        1 |
|       1 | 2020-11-28 |        2 |
|       1 | 2020-12-03 |        3 |
|       1 | 2021-01-01 |        4 |
|       2 | 2020-10-05 |        1 |
|       2 | 2020-12-09 |        2 |
|       2 | 2021-01-01 |        3 |
|       3 | 2020-11-11 |        1 |
|       3 | 2021-01-01 |        2 |

Now consecutive dates can be matched through rank:

- current row = `date_rnk`
- next row = `date_rnk + 1`

---

## Step 3: Self-join consecutive ranks

```sql
SELECT
    a.user_id,
    MAX(DATEDIFF(b.visit_date, a.visit_date)) AS biggest_window
FROM rnk a, rnk b
WHERE a.user_id = b.user_id
  AND b.date_rnk = a.date_rnk + 1
GROUP BY a.user_id;
```

---

## Why This Works

This treats:

- `a` as the current visit
- `b` as the next visit

The condition:

```sql
b.date_rnk = a.date_rnk + 1
```

ensures we compare only consecutive dates.

Then:

```sql
DATEDIFF(b.visit_date, a.visit_date)
```

computes the window size.

Finally:

```sql
MAX(...)
```

selects the largest such window for each user.

---

## Full Implementation — Approach 2

```sql
WITH all_dates AS (
    SELECT user_id, visit_date
    FROM UserVisits

    UNION

    SELECT user_id, '2021-01-01' AS visit_date
    FROM UserVisits
),
rnk AS (
    SELECT *,
        RANK() OVER (
            PARTITION BY user_id
            ORDER BY visit_date
        ) AS date_rnk
    FROM all_dates
)
SELECT
    a.user_id,
    MAX(DATEDIFF(b.visit_date, a.visit_date)) AS biggest_window
FROM rnk a, rnk b
WHERE a.user_id = b.user_id
  AND b.date_rnk = a.date_rnk + 1
GROUP BY a.user_id;
```

---

# Comparing the Two Approaches

## Approach 1: `LEAD()` + `IFNULL()`

### Strengths

- concise
- direct
- naturally models “next row”
- easiest to read once you know window functions

### Weaknesses

- requires support for `LEAD()`

---

## Approach 2: `RANK()` + self-join

### Strengths

- useful for learning how to model “next row” manually
- can be adapted to other sequence-comparison problems

### Weaknesses

- longer
- more manual
- more moving parts than necessary for this exact problem

---

# Important Note About Duplicate Rows

The problem says `UserVisits` may contain duplicate rows.

That matters.

If duplicate `(user_id, visit_date)` rows exist, they may affect ranking and next-date logic.

The examples shown here assume clean distinct visit dates per user.

A more defensive version could deduplicate first:

```sql
SELECT DISTINCT user_id, visit_date
FROM UserVisits
```

before applying either approach.

That is especially useful for Approach 2.

For Approach 1, duplicates would produce zero-day gaps between duplicate dates if not removed.

Depending on interpretation, deduplicating may be preferable.

---

# Safer Recommended Version of Approach 1

To handle possible duplicate visit rows more cleanly, a robust version is:

```sql
SELECT user_id, MAX(w) AS biggest_window
FROM (
    SELECT
        user_id,
        visit_date,
        DATEDIFF(
            IFNULL(
                LEAD(visit_date) OVER (
                    PARTITION BY user_id
                    ORDER BY visit_date
                ),
                '2021-01-01'
            ),
            visit_date
        ) AS w
    FROM (
        SELECT DISTINCT user_id, visit_date
        FROM UserVisits
    ) d
) x
GROUP BY user_id
ORDER BY user_id;
```

This version:

- removes duplicate visits first
- computes windows
- returns the maximum per user
- orders by `user_id`

---

# Why Approach 1 Is Usually Preferred

This problem is fundamentally about consecutive-row comparison.

`LEAD()` is built exactly for that.

So Approach 1 is usually the cleanest way to think about it:

- sort visits
- look one row ahead
- replace missing next value with today
- compute day difference
- take the max

That is the shortest path from the problem statement to the answer.

---

# Complexity Analysis

Let `n` be the number of visit rows.

## Approach 1

- sorts visits within each user partition
- computes one lead value per row
- aggregates max per user

Typical complexity is dominated by partitioned sorting.

## Approach 2

- adds extra rows
- ranks dates
- self-joins ranked rows
- aggregates max per user

This is generally more work than Approach 1.

So Approach 1 is usually more practical.

---

# Final Code Examples

## Approach 1 — `LEAD()` + `IFNULL()`

```sql
SELECT user_id, MAX(w) AS biggest_window
FROM (
    SELECT
        user_id,
        visit_date,
        DATEDIFF(
            IFNULL(
                LEAD(visit_date, 1) OVER (
                    PARTITION BY user_id
                    ORDER BY visit_date
                ),
                '2021-01-01'
            ),
            visit_date
        ) AS w
    FROM UserVisits
) AS a
GROUP BY user_id
ORDER BY user_id;
```

---

## Approach 2 — `RANK()` + self-join

```sql
WITH all_dates AS (
    SELECT user_id, visit_date
    FROM UserVisits

    UNION

    SELECT user_id, '2021-01-01' AS visit_date
    FROM UserVisits
),
rnk AS (
    SELECT *,
        RANK() OVER (
            PARTITION BY user_id
            ORDER BY visit_date
        ) AS date_rnk
    FROM all_dates
)
SELECT
    a.user_id,
    MAX(DATEDIFF(b.visit_date, a.visit_date)) AS biggest_window
FROM rnk a, rnk b
WHERE a.user_id = b.user_id
  AND b.date_rnk = a.date_rnk + 1
GROUP BY a.user_id
ORDER BY a.user_id;
```

---

# Key Takeaways

- The problem asks for the largest gap between consecutive visits, with the last gap ending at `2021-01-01`
- `LEAD()` is the most direct way to fetch the next visit date
- `IFNULL(..., '2021-01-01')` handles the last visit
- `DATEDIFF()` computes each window size
- `MAX()` per user gives the biggest window
- A ranking + self-join method is also valid, but more manual

---
