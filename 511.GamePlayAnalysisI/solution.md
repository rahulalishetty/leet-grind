# First Login Date for Each Player — SQL Summary (GROUP BY vs Window Functions)

Problem:
Report the **first login date** (`first_login`) for **each** `player_id` from the `Activity` table.
Return results in any order.

Typical schema idea (commonly for LeetCode 511):

- `Activity(player_id, device_id, event_date, games_played)`
- Primary key: `(player_id, event_date)` (important for ranking approach uniqueness)

---

## What Makes This Problem Interesting?

You must compute an **extreme value per group**:

- Group = all rows for the same `player_id`
- Extreme = **earliest** `event_date` in that group (minimum date)

So the core question is:

> “How do we group rows by player and return the smallest date in each group?”

---

## Approach 1: GROUP BY + MIN() (Recommended)

### Intuition

Once rows are grouped by `player_id`, you can apply `MIN(event_date)` to find the earliest date in that group.

### Algorithm

1. Group rows by `player_id`.
2. For each group, compute `MIN(event_date)`.
3. Output `player_id` + that minimum as `first_login`.

### Implementation

```sql
SELECT
  A.player_id,
  MIN(A.event_date) AS first_login
FROM
  Activity A
GROUP BY
  A.player_id;
```

### Why this is usually best

- Very simple and direct.
- Naturally expresses the question: “minimum date per player”.
- Efficient (with indexes on `(player_id, event_date)` it’s usually excellent).

---

## Approach 2: Window Functions (Multiple Alternatives)

Window functions compute values **per row**, while still allowing you to keep row-level detail.
Because of that, you often need an extra step (like `DISTINCT` or filtering with `ROW_NUMBER`) to return **one row per player**.

These are “medium-level” solutions for what is otherwise an easy aggregation problem.

---

### 2A) `FIRST_VALUE()` (Requires `DISTINCT`)

#### Intuition

Within each `player_id` partition ordered by `event_date`, the first value is the first login date.

#### Implementation

```sql
SELECT DISTINCT
  A.player_id,
  FIRST_VALUE(A.event_date) OVER (
    PARTITION BY A.player_id
    ORDER BY A.event_date
  ) AS first_login
FROM
  Activity A;
```

#### Why `DISTINCT` is needed

Window functions return a value **for every row**, not per group.

Example: if player 1 has 2 rows, you’d get:

| player_id | first_login |
| --------- | ----------- |
| 1         | 2016-03-01  |
| 1         | 2016-03-01  |

`DISTINCT` collapses duplicates so you only see one row per player.

---

### 2B) `LAST_VALUE()` (Needs a Correct Window Frame)

#### The pitfall

In MySQL, when you write:

```sql
LAST_VALUE(x) OVER (PARTITION BY ... ORDER BY ...)
```

the default frame is:

```text
RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
```

That means **“last value so far up to this row”**, not “last value in the partition”.
So results can vary row-by-row and look incorrect.

#### Fix: Use an explicit frame that covers the whole partition

If you order by `event_date DESC`, then the “last value” over the full partition becomes the earliest date.

#### Implementation

```sql
SELECT DISTINCT
  A.player_id,
  LAST_VALUE(A.event_date) OVER (
    PARTITION BY A.player_id
    ORDER BY A.event_date DESC
    RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
  ) AS first_login
FROM
  Activity A;
```

#### Why this works

- `ORDER BY event_date DESC` flips the order (latest → earliest).
- `... UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING` ensures the frame includes **all rows** in the partition.
- `LAST_VALUE` then becomes the earliest date in that full partition.

---

### 2C) Ranking Functions (`RANK()` / `DENSE_RANK()` / `ROW_NUMBER()`)

#### Intuition

Rank rows per player by date ascending, then pick rank 1.

Because `(player_id, event_date)` is a primary key, ordering by `event_date` produces a unique first row per player, so `RANK`, `DENSE_RANK`, and `ROW_NUMBER` all behave equivalently here.

#### Implementation (using `RANK`)

```sql
SELECT
  X.player_id,
  X.event_date AS first_login
FROM
  (
    SELECT
      A.player_id,
      A.event_date,
      RANK() OVER (
        PARTITION BY A.player_id
        ORDER BY A.event_date
      ) AS rnk
    FROM
      Activity A
  ) X
WHERE
  X.rnk = 1;
```

#### Common interview-friendly variant: `ROW_NUMBER()`

```sql
SELECT
  player_id,
  event_date AS first_login
FROM (
  SELECT
    player_id,
    event_date,
    ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY event_date) AS rn
  FROM Activity
) t
WHERE rn = 1;
```

---

## When Should You Use Which?

### Recommended default

✅ **Approach 1 (GROUP BY + MIN)**

- simplest
- clearest
- usually fastest
- easiest to reason about

### Window functions are useful when:

- You need extra columns from the “first row” (not just the first date), e.g. first device used.
- You want to show more advanced SQL fluency in interviews.
- The task expands into “first row per group” patterns.

---
