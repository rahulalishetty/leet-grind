# Running Total of Games Played per Player (Game Play Analysis III-style) — SQL Summary

Problem (concept):
For every `(player_id, event_date)` row in `Activity`, report the **running total** of `games_played` for that player **up to and including** that date.

In other words, for each row:

```text
games_played_so_far(player_id, event_date)
  = SUM(games_played) for same player_id
    where event_date <= current row's event_date
```

---

## Why a Correlated Subquery is Tempting (but Slow)

A direct translation of the definition is a correlated subquery:

```sql
SELECT
  A1.player_id,
  A1.event_date,
  (
    SELECT
      SUM(A2.games_played)
    FROM
      Activity A2
    WHERE
      A2.player_id = A1.player_id
      AND A2.event_date <= A1.event_date
  ) AS games_played_so_far
FROM
  Activity A1;
```

### Correctness

This is correct because for each row `A1`, it sums all earlier (and same-day) rows `A2` for the same player.

### Why it can cause TLE

Many query engines execute correlated subqueries as **nested loop joins**:

- For each outer row (`A1`), scan matching inner rows (`A2`).
- With many rows, this can approach **O(n²)** behavior.

Even if all test cases return correct results, performance can exceed time limits.

---

## Approach 1: `SUM()` Window Function (Recommended)

### Intuition

“Running total” and “rolling sum” are classic window function use-cases.

We want:

- compute a cumulative sum per player,
- ordered by date.

### Algorithm

1. Partition rows by `player_id`.
2. Within each partition, order by `event_date` ascending.
3. Compute a running sum of `games_played`.

### Implementation

```sql
SELECT
  A.player_id,
  A.event_date,
  SUM(A.games_played) OVER (
    PARTITION BY A.player_id
    ORDER BY A.event_date
  ) AS games_played_so_far
FROM
  Activity A;
```

### How it works (operational view)

For each player partition:

- Sort by `event_date`.
- Maintain an accumulator:
  - add current row’s `games_played`
  - output the accumulator as `games_played_so_far`

### Notes

- Very concise and readable.
- Usually the most efficient and scalable.
- Requires DB support for window functions (MySQL 8+, Postgres, SQL Server, etc.).

---

## Approach 2: Non-Equi Self-Join + Group By (Creative Alternative)

### Intuition

For each “date being considered” (A2.event_date), generate all rows for the same player (A1) with earlier or same dates:

```text
A1.event_date <= A2.event_date
```

Then group by `(player_id, date considered)` and sum `A1.games_played`.

This “expands” the data first, then aggregates.

### Algorithm

1. Self-join `Activity` to itself:
   - same `player_id`
   - A1.event_date <= A2.event_date
2. Group by `A2.player_id, A2.event_date`.
3. Sum `A1.games_played`.

### Implementation

```sql
SELECT
  A2.player_id,
  A2.event_date,
  SUM(A1.games_played) AS games_played_so_far
FROM
  Activity A1
  INNER JOIN Activity A2
    ON A1.player_id = A2.player_id
   AND A1.event_date <= A2.event_date
GROUP BY
  A2.player_id,
  A2.event_date;
```

### Why it works

For a fixed `(A2.player_id, A2.event_date)`:

- the join selects all rows `(A1)` for that player up to that date,
- summing gives the running total.

### Trade-offs

- Can generate a large intermediate result set (potentially many matches per row).
- Often slower/more memory-intensive than a window function.
- Useful when window functions are unavailable.

---

## Recommendation

✅ Prefer **Approach 1 (window SUM)**:

- simplest
- clearest
- typically best performance

Use **Approach 2 (non-equi self join)** if:

- your SQL dialect does not support window functions,
- or you want to demonstrate alternative reasoning patterns in interviews.

Avoid the correlated subquery in production unless you have strong evidence your optimizer rewrites it well.

---
