# First Device Logged In for Each Player (Game Play Analysis II) — SQL Summary

Problem (Game Play Analysis II, commonly LeetCode 512):
For each `player_id`, report the **device_id used on the player's first login**.

Why this is harder than Part I (first login date):

- In Part I, you only needed the **earliest date** per player → `MIN(event_date)` works directly.
- Here, you need the **device_id associated with that earliest date**.
- `MIN(event_date)` alone does not automatically bring along the matching `device_id`.

Key schema property (important for correctness):

- `(player_id, event_date)` is the **primary key** of `Activity`.
- Therefore, for each player, the earliest `event_date` corresponds to **exactly one row**, so the device is uniquely determined.

---

## Core Idea

1. Find the **earliest event_date per player**.
2. Retrieve the **row(s)** in `Activity` that match `(player_id, earliest_event_date)`.
3. Output `player_id, device_id`.

Two clean families of solutions:

- **Approach 1**: MIN date per player + match back to Activity (subquery / join).
- **Approach 2**: Window functions to rank rows and pick the first.

---

## Approach 1: Subquery + Multi-Column `IN` (MySQL Row Constructor)

### Intuition

If we can compute the tuple `(player_id, MIN(event_date))` for each player, then we can select the matching rows from `Activity` and pull out `device_id`.

Because `(player_id, event_date)` is unique, this returns exactly one device per player.

### Algorithm

1. Compute for each player: earliest date.
2. From `Activity`, select rows whose `(player_id, event_date)` matches those pairs.
3. Return `player_id, device_id`.

### Implementation (MySQL supports `(col1, col2) IN (SELECT ...)`)

```sql
SELECT
  A1.player_id,
  A1.device_id
FROM
  Activity A1
WHERE
  (A1.player_id, A1.event_date) IN (
    SELECT
      A2.player_id,
      MIN(A2.event_date)
    FROM
      Activity A2
    GROUP BY
      A2.player_id
  );
```

### Practical note: portability

Not every DB supports tuple `IN` comparisons the same way. If you want a broadly portable approach, use a derived table/CTE + `JOIN`.

---

### Workaround: CTE + INNER JOIN (Portable and Clear)

#### Idea

Create a CTE with the earliest date per player, then join it back to `Activity` on both `player_id` and `event_date`.

#### Implementation

```sql
WITH min_data AS (
  SELECT
    A.player_id,
    MIN(A.event_date) AS event_date
  FROM
    Activity A
  GROUP BY
    A.player_id
)
SELECT
  A2.player_id,
  A2.device_id
FROM
  Activity A2
  INNER JOIN min_data M
    ON M.player_id = A2.player_id
   AND M.event_date = A2.event_date;
```

### Why Approach 1 is often preferred

- Simple conceptually: “min date then look up the row”.
- Very performant with an index on `(player_id, event_date)` (often the PK).
- Builds directly on Part I’s solution.

---

## Approach 2: Window Functions (Rank rows, pick rank 1)

### Intuition

Within each player’s rows, order by `event_date` and assign a rank.
The row with rank 1 is the earliest login; its `device_id` is the answer.

### Algorithm

1. Create a CTE (or subquery) with per-player ranking by date.
2. Filter to `rank = 1`.
3. Output `player_id, device_id`.

### Implementation (using `RANK()`)

```sql
WITH ranked_logins AS (
  SELECT
    A.player_id,
    A.device_id,
    RANK() OVER (
      PARTITION BY A.player_id
      ORDER BY A.event_date
    ) AS rnk
  FROM
    Activity A
)
SELECT
  RL.player_id,
  RL.device_id
FROM
  ranked_logins RL
WHERE
  RL.rnk = 1;
```

### Why `RANK`, `DENSE_RANK`, and `ROW_NUMBER` all work here

Because `(player_id, event_date)` is the primary key:

- No two rows for the same player share the same `event_date`.
- So ties do not exist.
- Therefore `RANK()`, `DENSE_RANK()`, and `ROW_NUMBER()` will all assign a unique “first row” per player.

A common variant:

```sql
WITH t AS (
  SELECT
    player_id,
    device_id,
    ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY event_date) AS rn
  FROM Activity
)
SELECT player_id, device_id
FROM t
WHERE rn = 1;
```

---

## Alternative Window Functions (Often Less Simple)

### 2A) `FIRST_VALUE()` (needs `DISTINCT`)

`FIRST_VALUE` returns the first value **for every row** in the partition, so you must deduplicate.

```sql
SELECT DISTINCT
  A.player_id,
  FIRST_VALUE(A.device_id) OVER (
    PARTITION BY A.player_id
    ORDER BY A.event_date
  ) AS device_id
FROM
  Activity A;
```

### 2B) `LAST_VALUE()` (requires correct frame)

In MySQL, `LAST_VALUE` with `ORDER BY` has a default frame ending at `CURRENT ROW`, so it can behave unexpectedly unless you specify a full-partition frame.

One way: order descending and take the last value over the full partition:

```sql
SELECT DISTINCT
  A.player_id,
  LAST_VALUE(A.device_id) OVER (
    PARTITION BY A.player_id
    ORDER BY A.event_date DESC
    RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
  ) AS device_id
FROM
  Activity A;
```

This is correct but more verbose and easier to get wrong than ranking.

---

## Recommendation

✅ Prefer **Approach 1 (min date per player + match back to Activity)**:

- simplest
- efficient
- directly extends Part I logic

Use **Approach 2 (window ranking)** when:

- you want a “first row per group” pattern that scales to more complex selections,
- you’re practicing window functions for interviews,
- or you need additional columns from the first row in a clean way.

---

## Indexing / Performance Notes (Practical)

Best-case indexing for these queries:

- Primary key `(player_id, event_date)` (already given)
- This makes both “min date per player” and “join on player+date” efficient.

---
