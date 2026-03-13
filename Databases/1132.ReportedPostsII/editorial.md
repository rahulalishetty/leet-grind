# 1132. Reported Posts II — Detailed Approach Summary

## Approach: Identifying Matched Records Using `CASE WHEN`

This approach computes the answer in **two layers**:

1. Compute the **daily percentage** of spam-reported posts that were eventually removed.
2. Compute the **average of those daily percentages**.

---

## Core Idea

We only care about posts that were:

- reported with `extra = 'spam'` in the `Actions` table
- possibly removed later, as recorded in the `Removals` table

So the job is:

- for each `action_date`
- find all **distinct spam-reported posts**
- among them, count how many were removed
- compute:

```sql
removed_spam_posts / total_spam_reported_posts
```

Then average these daily values across all dates that had at least one spam report.

---

## Why a `LEFT JOIN` is Needed

The `Actions` table contains all spam reports.

The `Removals` table contains only posts that were actually removed.

If we use:

```sql
LEFT JOIN Removals r
ON a.post_id = r.post_id
```

then:

- every spam-reported post from `Actions` is preserved
- matching removed posts get values from `Removals`
- non-removed posts get `NULL` on the `Removals` side

That is exactly what we need, because the denominator includes **all spam-reported posts**, while the numerator includes only those that were removed.

---

## Why `CASE WHEN` is Used

After the join, each row may or may not have a match in `Removals`.

So this expression:

```sql
CASE WHEN a.post_id = r.post_id THEN r.post_id END
```

returns:

- the `post_id` if the post was removed
- `NULL` otherwise

Then:

```sql
COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END)
```

counts only the distinct removed spam-reported posts.

Because `COUNT(...)` ignores `NULL`, unmatched rows do not contribute to the numerator.

---

## Why `DISTINCT` is Necessary

The `Actions` table may have duplicate rows.

Also, the same post may be reported multiple times on the same day.

But for this problem, each post should count only once per day in:

- the total spam-reported posts
- the removed spam-reported posts

So both counts must use `DISTINCT post_id`.

Without `DISTINCT`, duplicate reports would inflate the counts and produce incorrect percentages.

---

## Step 1: Compute the Daily Rate

### Query

```sql
SELECT
    action_date,
    COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END)
        / COUNT(DISTINCT a.post_id) AS daily_rate
FROM Actions a
LEFT JOIN Removals r
    ON a.post_id = r.post_id
WHERE a.extra = 'spam'
GROUP BY action_date;
```

---

## How This Query Works

### 1. Filter to only spam reports

```sql
WHERE a.extra = 'spam'
```

This removes all non-spam rows such as:

- `view`
- `like`
- `share`
- `report` with other reasons like `racism`

So now we are working only with spam-reported posts.

---

### 2. Group by date

```sql
GROUP BY action_date
```

This ensures the percentage is computed separately for each day.

---

### 3. Count removed spam posts

```sql
COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END)
```

This is the numerator.

It counts how many distinct spam-reported posts from that day were eventually removed.

---

### 4. Count all spam-reported posts

```sql
COUNT(DISTINCT a.post_id)
```

This is the denominator.

It counts how many distinct posts were reported as spam on that day.

---

### 5. Divide numerator by denominator

```sql
COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END)
/
COUNT(DISTINCT a.post_id)
```

This gives the daily percentage as a fraction:

- `1` means 100%
- `0.5` means 50%
- `0` means none were removed

---

## Daily Result for the Example

Using the example input, the intermediate result is:

| action_date | daily_rate |
| ----------- | ---------: |
| 2019-07-02  |          1 |
| 2019-07-04  |        0.5 |

### Explanation

#### 2019-07-02

Spam-reported posts:

- post `3`

Removed posts:

- post `3`

So:

```text
1 / 1 = 1
```

---

#### 2019-07-04

Spam-reported posts:

- post `2`
- post `4`

Removed posts:

- post `2`

So:

```text
1 / 2 = 0.5
```

---

#### 2019-07-01 and 2019-07-03

These days do not count because there were no spam reports on those dates.

So they are excluded from the grouped result.

---

## Step 2: Compute the Final Average

Once the daily rates are computed, take the average across all days.

### Final Query

```sql
SELECT ROUND(AVG(daily_rate) * 100, 2) AS average_daily_percent
FROM (
    SELECT
        action_date,
        COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END)
            / COUNT(DISTINCT a.post_id) AS daily_rate
    FROM Actions a
    LEFT JOIN Removals r
        ON a.post_id = r.post_id
    WHERE a.extra = 'spam'
    GROUP BY action_date
) t0;
```

---

## Final Computation for the Example

Daily rates are:

- `1`
- `0.5`

Average:

```text
(1 + 0.5) / 2 = 0.75
```

Convert to percentage:

```text
0.75 * 100 = 75
```

Round to 2 decimal places:

```text
75.00
```

So the final answer is:

| average_daily_percent |
| --------------------: |
|                 75.00 |

---

## Full Explanation of the Final Query

### Outer Query

```sql
SELECT ROUND(AVG(daily_rate) * 100, 2) AS average_daily_percent
```

This does three things:

1. `AVG(daily_rate)` computes the average of all daily fractions
2. `* 100` converts the fraction to a percentage
3. `ROUND(..., 2)` rounds to 2 decimal places

---

### Inner Query

```sql
SELECT
    action_date,
    COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END)
        / COUNT(DISTINCT a.post_id) AS daily_rate
FROM Actions a
LEFT JOIN Removals r
    ON a.post_id = r.post_id
WHERE a.extra = 'spam'
GROUP BY action_date
```

This builds the per-day percentages first.

That inner result is then averaged by the outer query.

---

## Important Detail: We Do Not Care About `remove_date`

The problem asks whether the post **got removed after being reported as spam**.

It does **not** ask us to compare removal date with report date.

So the only thing that matters is whether the `post_id` exists in `Removals`.

That is why the join is only:

```sql
ON a.post_id = r.post_id
```

and `remove_date` is completely irrelevant for the computation.

---

## Code Example with Comments

```sql
SELECT ROUND(AVG(daily_rate) * 100, 2) AS average_daily_percent
FROM (
    SELECT
        a.action_date,

        -- Count distinct spam-reported posts that were removed
        COUNT(DISTINCT CASE
            WHEN a.post_id = r.post_id THEN r.post_id
        END)

        /

        -- Count distinct spam-reported posts for that day
        COUNT(DISTINCT a.post_id) AS daily_rate

    FROM Actions a
    LEFT JOIN Removals r
        ON a.post_id = r.post_id
    WHERE a.extra = 'spam'
    GROUP BY a.action_date
) t0;
```

---

## Why This Approach Is Correct

This solution is correct because it directly follows the problem statement:

- **Per day**, identify all distinct spam-reported posts
- Among them, identify which were removed
- Compute that day's percentage
- Average across all such days

It also safely handles duplicates by using `DISTINCT`.

---

## Potential Pitfall: Integer Division

In some SQL engines, dividing two integers may produce integer division.

For example:

```sql
1 / 2 = 0
```

instead of `0.5`.

If your SQL dialect behaves that way, force decimal division:

```sql
COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END) * 1.0
/ COUNT(DISTINCT a.post_id)
```

### Safer Version

```sql
SELECT ROUND(AVG(daily_rate) * 100, 2) AS average_daily_percent
FROM (
    SELECT
        a.action_date,
        COUNT(DISTINCT CASE WHEN a.post_id = r.post_id THEN r.post_id END) * 1.0
        / COUNT(DISTINCT a.post_id) AS daily_rate
    FROM Actions a
    LEFT JOIN Removals r
        ON a.post_id = r.post_id
    WHERE a.extra = 'spam'
    GROUP BY a.action_date
) t0;
```

This version is more portable.

---

## Alternative Equivalent Style

Some people prefer checking for non-null joined rows instead of comparing IDs again.

That version looks like this:

```sql
SELECT ROUND(AVG(daily_rate) * 100, 2) AS average_daily_percent
FROM (
    SELECT
        a.action_date,
        COUNT(DISTINCT CASE WHEN r.post_id IS NOT NULL THEN a.post_id END) * 1.0
        / COUNT(DISTINCT a.post_id) AS daily_rate
    FROM Actions a
    LEFT JOIN Removals r
        ON a.post_id = r.post_id
    WHERE a.extra = 'spam'
    GROUP BY a.action_date
) t0;
```

This is logically equivalent because after the join:

- `r.post_id IS NOT NULL` means the post was removed
- `NULL` means it was not removed

---

## Complexity Analysis

Let:

- `A` = number of rows in `Actions`
- `R` = number of rows in `Removals`

### Time Complexity

Roughly:

```text
O(A + join_cost)
```

In practice, this depends on indexing and the database engine.

Typical efficient execution comes from indexing:

- `Actions(post_id)`
- `Actions(extra, action_date, post_id)` or similar
- `Removals(post_id)`

Because `Removals.post_id` is a primary key, that side is already indexed in most databases.

---

### Space Complexity

The query uses grouped aggregates and join processing.

Extra space depends on the query engine, but conceptually:

- storage for join/grouping structures
- storage for distinct counting

No explicit extra tables are created by the user.

---

## Final Recommended Solution

```sql
SELECT ROUND(AVG(daily_rate) * 100, 2) AS average_daily_percent
FROM (
    SELECT
        a.action_date,
        COUNT(DISTINCT CASE WHEN r.post_id IS NOT NULL THEN a.post_id END) * 1.0
        / COUNT(DISTINCT a.post_id) AS daily_rate
    FROM Actions a
    LEFT JOIN Removals r
        ON a.post_id = r.post_id
    WHERE a.extra = 'spam'
    GROUP BY a.action_date
) t0;
```

---

## Key Takeaways

- Use `LEFT JOIN` because we need both removed and non-removed spam reports.
- Use `CASE WHEN` to count only matched removed posts.
- Use `DISTINCT` because duplicate reports must not distort the percentages.
- Compute daily percentages first, then average them.
- Ignore `remove_date`; only the existence of the post in `Removals` matters.
- Be careful about integer division in some SQL engines.

---
