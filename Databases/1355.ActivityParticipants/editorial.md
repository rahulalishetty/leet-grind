# 1355. Activity Participants — Detailed Summary

## Problem Restatement

We need to find all activities whose number of participants is:

- **not the maximum**
- **not the minimum**

In other words, for each activity, count how many friends participate in it, then return only those activities whose count lies **strictly between** the smallest and largest participant counts.

---

## Key Observation

The problem is really about two steps:

1. Compute the participant count for each activity
2. Remove activities whose count is:
   - equal to the maximum participant count
   - equal to the minimum participant count

So almost every good solution starts by building the same core intermediate result:

```sql
activity | user_cnts
```

where `user_cnts` is the number of participants in that activity.

---

# Shared Foundation: Count Participants Per Activity

All three approaches begin from the same base idea:

```sql
SELECT activity, COUNT(DISTINCT id) AS user_cnts
FROM Friends
GROUP BY activity
```

This produces one row per activity with the number of unique friends participating.

### Why `COUNT(DISTINCT id)`?

Because `id` identifies a friend uniquely.

Even if duplicate rows somehow existed, `COUNT(DISTINCT id)` safely counts unique participants.

---

## Example Intermediate Output

From the sample input:

| activity     | user_cnts |
| ------------ | --------: |
| Eating       |         3 |
| Horse Riding |         1 |
| Singing      |         2 |

From this:

- maximum participant count = `3`
- minimum participant count = `1`

So the only activity we want is the one with count strictly between them:

- `Singing` → `2`

---

# Approach 1: `NOT IN` / `NOT EXISTS`

## Main Idea

This approach identifies the unwanted participant counts first:

- the maximum participant count
- the minimum participant count

Then it removes all activities whose participant count belongs to either of those two values.

So the logic is:

> keep activities where `user_cnts` is neither max nor min

---

## Step 1: Build a CTE with Participant Counts

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
```

### What this does

This creates a reusable temporary result named `user_by_activity`.

That result contains:

| activity     | user_cnts |
| ------------ | --------: |
| Eating       |         3 |
| Horse Riding |         1 |
| Singing      |         2 |

---

## Step 2: Select All Activities from the CTE

```sql
SELECT activity
FROM user_by_activity
```

At this stage we are looking at all activities.

Now we need to remove the unwanted ones.

---

## Step 3: Exclude Maximum and Minimum Counts Using `NOT IN`

```sql
SELECT activity
FROM user_by_activity
WHERE user_cnts NOT IN (SELECT MAX(user_cnts) FROM user_by_activity)
  AND user_cnts NOT IN (SELECT MIN(user_cnts) FROM user_by_activity)
```

### What this means

- reject activities whose count equals the maximum count
- reject activities whose count equals the minimum count

So if:

- max = 3
- min = 1

then only activities with counts other than `3` and `1` are kept.

That means only:

- `Singing` with `2`

---

## Final Implementation — Approach 1

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
SELECT activity
FROM user_by_activity
WHERE user_cnts NOT IN (SELECT MAX(user_cnts) FROM user_by_activity)
  AND user_cnts NOT IN (SELECT MIN(user_cnts) FROM user_by_activity);
```

---

## Why This Works

This works because the problem is based entirely on comparing each activity's participant count against the global extremes.

The CTE gives us one count per activity.

Then the outer filter keeps only counts that are neither the largest nor the smallest.

---

## Alternative Thought Using `NOT EXISTS`

A similar idea can be expressed with `NOT EXISTS`, although `NOT IN` is simpler here because we are comparing scalar aggregate results.

---

# Approach 2: Using `RANK()` to Identify Maximum and Minimum

## Main Idea

Instead of directly computing max and min values and comparing against them, this approach ranks activities by participant count:

- ascending rank → identifies the minimum
- descending rank → identifies the maximum

Then it keeps activities that are **not ranked 1** in either direction.

---

## Why Ranking Works

If we rank activity counts in ascending order:

- the smallest count gets rank `1`

If we rank them in descending order:

- the largest count gets rank `1`

So the activities we want are exactly those for which:

- `rank_asc != 1`
- `rank_desc != 1`

---

## Step 1: Aggregate and Rank

```sql
SELECT activity,
       RANK() OVER (ORDER BY COUNT(id)) AS rank_asc,
       RANK() OVER (ORDER BY COUNT(id) DESC) AS rank_desc
FROM Friends
GROUP BY activity
```

### What this does

For each activity:

- compute participant count
- assign ascending rank by count
- assign descending rank by count

---

## Example Result

| activity     | rank_asc | rank_desc |
| ------------ | -------: | --------: |
| Eating       |        3 |         1 |
| Singing      |        2 |         2 |
| Horse Riding |        1 |         3 |

### Interpretation

- `Horse Riding` has ascending rank `1` → minimum participant count
- `Eating` has descending rank `1` → maximum participant count
- `Singing` is neither minimum nor maximum

So we keep only `Singing`.

---

## Step 2: Filter Out Rank 1 in Either Direction

```sql
SELECT activity
FROM
(
    SELECT activity,
           RANK() OVER (ORDER BY COUNT(id)) AS rank_asc,
           RANK() OVER (ORDER BY COUNT(id) DESC) AS rank_desc
    FROM Friends
    GROUP BY activity
) t0
WHERE rank_asc != 1 AND rank_desc != 1;
```

This removes:

- all activities with minimum participant count
- all activities with maximum participant count

---

## Final Implementation — Approach 2

```sql
SELECT activity
FROM
(
    SELECT activity,
           RANK() OVER (ORDER BY COUNT(id)) AS rank_asc,
           RANK() OVER (ORDER BY COUNT(id) DESC) AS rank_desc
    FROM Friends
    GROUP BY activity
) t0
WHERE rank_asc != 1
  AND rank_desc != 1;
```

---

## Why `RANK()` Is a Good Fit

`RANK()` is especially useful when multiple activities can tie for min or max.

For example, if two activities both have the minimum count, they both get rank `1` in ascending order, and both are excluded.

Similarly, if multiple activities tie for the maximum count, they all get rank `1` in descending order and are excluded.

That matches the problem perfectly.

---

## Small Improvement Note

To stay fully consistent with duplicates or defensive counting, some SQL writers may prefer:

```sql
COUNT(DISTINCT id)
```

instead of:

```sql
COUNT(id)
```

So a slightly safer version is:

```sql
SELECT activity
FROM
(
    SELECT activity,
           RANK() OVER (ORDER BY COUNT(DISTINCT id)) AS rank_asc,
           RANK() OVER (ORDER BY COUNT(DISTINCT id) DESC) AS rank_desc
    FROM Friends
    GROUP BY activity
) t0
WHERE rank_asc != 1
  AND rank_desc != 1;
```

---

# Approach 3: Remove Matching Records Using `LEFT JOIN`

## Main Idea

This approach also starts by computing participant counts per activity.

Then it builds a small table containing the **unwanted counts**:

- the maximum participant count
- the minimum participant count

Next, it `LEFT JOIN`s all activities against this unwanted-count table.

If an activity's count matches one of those unwanted counts, the join succeeds.

If not, the joined value is `NULL`.

Finally, we keep only the rows where the joined value is `NULL`.

That effectively removes min-count and max-count activities.

---

## Step 1: Build the Participant Count CTE

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
```

This gives:

| activity     | user_cnts |
| ------------ | --------: |
| Eating       |         3 |
| Horse Riding |         1 |
| Singing      |         2 |

---

## Step 2: Build the Unwanted Count Set

```sql
SELECT MAX(user_cnts) AS user_cnts
FROM user_by_activity
UNION
SELECT MIN(user_cnts) AS user_cnts
FROM user_by_activity
```

### Example Result

| user_cnts |
| --------: |
|         3 |
|         1 |

These are the participant counts we want to exclude.

### Why `UNION` and not `UNION ALL`?

If max and min happen to be equal, `UNION` avoids duplicate rows.

That is fine here because we only need the distinct unwanted counts.

---

## Step 3: Left Join All Activities Against the Unwanted Counts

```sql
SELECT activity
FROM user_by_activity u
LEFT JOIN
(
    SELECT MAX(user_cnts) AS user_cnts
    FROM user_by_activity
    UNION
    SELECT MIN(user_cnts) AS user_cnts
    FROM user_by_activity
) m
ON u.user_cnts = m.user_cnts
WHERE m.user_cnts IS NULL;
```

---

## How the Join Works

### Activities table side

| activity     | user_cnts |
| ------------ | --------: |
| Eating       |         3 |
| Horse Riding |         1 |
| Singing      |         2 |

### Unwanted counts side

| user_cnts |
| --------: |
|         3 |
|         1 |

### Join result conceptually

| activity     | user_cnts | matched_unwanted_cnt |
| ------------ | --------: | -------------------: |
| Eating       |         3 |                    3 |
| Horse Riding |         1 |                    1 |
| Singing      |         2 |                 NULL |

Only `Singing` has no match.

So keeping:

```sql
WHERE m.user_cnts IS NULL
```

returns only the desired activity.

---

## Final Implementation — Approach 3

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
SELECT activity
FROM user_by_activity u
LEFT JOIN
(
    SELECT MAX(user_cnts) AS user_cnts
    FROM user_by_activity
    UNION
    SELECT MIN(user_cnts) AS user_cnts
    FROM user_by_activity
) m
ON u.user_cnts = m.user_cnts
WHERE m.user_cnts IS NULL;
```

---

# Comparing the Three Approaches

## Approach 1: `NOT IN`

### Strengths

- simple
- direct
- easy to understand
- close to the problem statement

### Weaknesses

- repeats scalar subqueries for max and min
- some people are cautious with `NOT IN` in nullable contexts, although here it is safe

---

## Approach 2: `RANK()`

### Strengths

- elegant
- naturally handles ties
- good when practicing window functions

### Weaknesses

- may feel heavier than necessary for a simple min/max exclusion problem
- requires familiarity with window functions

---

## Approach 3: `LEFT JOIN`

### Strengths

- clean anti-join style
- flexible if you want to exclude based on a derived set
- often useful as a general SQL pattern

### Weaknesses

- more verbose than Approach 1
- slightly indirect if you only need min/max comparison

---

# Worked Example

Given:

|  id | name        | activity     |
| --: | ----------- | ------------ |
|   1 | Jonathan D. | Eating       |
|   2 | Jade W.     | Singing      |
|   3 | Victor J.   | Singing      |
|   4 | Elvis Q.    | Eating       |
|   5 | Daniel A.   | Eating       |
|   6 | Bob B.      | Horse Riding |

Count participants per activity:

| activity     | user_cnts |
| ------------ | --------: |
| Eating       |         3 |
| Singing      |         2 |
| Horse Riding |         1 |

Now:

- max count = `3`
- min count = `1`

Activities to exclude:

- `Eating`
- `Horse Riding`

Remaining activity:

- `Singing`

Final answer:

| activity |
| -------- |
| Singing  |

---

# Edge Case Discussion

## What if multiple activities tie for maximum?

All of them should be removed.

Example:

| activity | user_cnts |
| -------- | --------: |
| A        |         5 |
| B        |         5 |
| C        |         2 |

Then `A` and `B` are both maximum and should be excluded.

All three approaches handle this correctly.

---

## What if multiple activities tie for minimum?

All of them should be removed.

Example:

| activity | user_cnts |
| -------- | --------: |
| A        |         1 |
| B        |         1 |
| C        |         4 |

Then `A` and `B` are both minimum and should be excluded.

Again, all three approaches handle this correctly.

---

## What if all activities have the same participant count?

Then every activity is both minimum and maximum.

So the correct result is empty.

All three approaches also handle that case correctly.

---

# Complexity Discussion

Let `n` be the number of rows in `Friends`, and let `a` be the number of distinct activities.

## Shared Base Cost

All approaches first compute counts per activity:

```text
O(n)
```

plus grouping overhead depending on the database engine.

## Approach 1

After building the grouped result, max and min are computed over `a` rows.

This is efficient and usually the most straightforward.

## Approach 2

Window ranking is computed over `a` grouped rows.

Still efficient, though window functions may be a bit heavier.

## Approach 3

Builds the grouped result, then a tiny unwanted-count set, then does a left join across `a` grouped rows.

Also efficient.

---

# Recommended Solution

For clarity and simplicity, Approach 1 is usually the most practical:

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
SELECT activity
FROM user_by_activity
WHERE user_cnts NOT IN (SELECT MAX(user_cnts) FROM user_by_activity)
  AND user_cnts NOT IN (SELECT MIN(user_cnts) FROM user_by_activity);
```

It directly expresses the actual business rule:

- compute participant counts
- remove max
- remove min

---

# Final Code Examples

## Approach 1 — `NOT IN`

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
SELECT activity
FROM user_by_activity
WHERE user_cnts NOT IN (SELECT MAX(user_cnts) FROM user_by_activity)
  AND user_cnts NOT IN (SELECT MIN(user_cnts) FROM user_by_activity);
```

---

## Approach 2 — `RANK()`

```sql
SELECT activity
FROM
(
    SELECT activity,
           RANK() OVER (ORDER BY COUNT(DISTINCT id)) AS rank_asc,
           RANK() OVER (ORDER BY COUNT(DISTINCT id) DESC) AS rank_desc
    FROM Friends
    GROUP BY activity
) t0
WHERE rank_asc != 1
  AND rank_desc != 1;
```

---

## Approach 3 — `LEFT JOIN`

```sql
WITH user_by_activity AS
(
    SELECT activity, COUNT(DISTINCT id) AS user_cnts
    FROM Friends
    GROUP BY activity
)
SELECT activity
FROM user_by_activity u
LEFT JOIN
(
    SELECT MAX(user_cnts) AS user_cnts
    FROM user_by_activity
    UNION
    SELECT MIN(user_cnts) AS user_cnts
    FROM user_by_activity
) m
ON u.user_cnts = m.user_cnts
WHERE m.user_cnts IS NULL;
```

---

# Key Takeaways

- First compute participant count per activity
- Then remove activities whose counts equal the global minimum or maximum
- `NOT IN`, `RANK()`, and `LEFT JOIN` are all valid ways to express the logic
- `COUNT(DISTINCT id)` is the safest counting choice
- Ties at min or max should all be excluded

---
