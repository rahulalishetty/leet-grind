# 2173. Longest Winning Streak

## Approach: Window Function

## Core idea

We need to compute, for each player, the **longest streak of consecutive wins**.

A streak:

- **continues** when the result is `Win`
- **breaks** when the result is `Draw` or `Lose`

So the challenge is to group each player’s matches into segments such that:

- consecutive wins belong to the same streak segment
- a non-win starts or belongs to a different segment
- then we can count the wins in each segment
- and finally take the maximum for each player

This solution uses:

- **CTEs**
- **window functions**
- a **derived streak-group identifier**
- a cumulative sum to count wins inside each streak group

---

## High-level plan

The query works in three main stages:

1. build a row-level representation of matches with:
   - a flag for whether the row is not a win
   - a computed `streak_group` identifier

2. within each `(player_id, streak_group)` group, compute a running streak length

3. for each player, return the maximum streak length across all winning groups

---

# Step 1: Build the `RankedMatches` CTE

```sql
WITH RankedMatches AS (
  SELECT
    player_id,
    match_day,
    result,
    CASE WHEN result = 'Win' THEN 0 ELSE 1 END AS is_not_win,
    ROW_NUMBER() OVER (
      PARTITION BY player_id
      ORDER BY match_day
    ) - ROW_NUMBER() OVER (
      PARTITION BY player_id, result
      ORDER BY match_day
    ) AS streak_group
  FROM
    Matches
)
```

---

## What this CTE is doing

It computes two important values:

1. `is_not_win`
2. `streak_group`

---

## Part A: `is_not_win`

```sql
CASE WHEN result = 'Win' THEN 0 ELSE 1 END AS is_not_win
```

This is a simple indicator:

- `Win` -> `0`
- `Draw` or `Lose` -> `1`

Why do this?

Because later, we want to count wins and ignore non-wins.
Representing non-wins explicitly makes the later logic easier.

---

## Part B: `streak_group`

This is the most subtle part:

```sql
ROW_NUMBER() OVER (
  PARTITION BY player_id
  ORDER BY match_day
)
-
ROW_NUMBER() OVER (
  PARTITION BY player_id, result
  ORDER BY match_day
) AS streak_group
```

This creates a grouping key based on row-number differences.

---

## Intuition behind `streak_group`

A skeptical question is:

> how can row numbers help us identify consecutive streak segments?

The trick is this:

- the first `ROW_NUMBER()` counts all matches in chronological order for a player
- the second `ROW_NUMBER()` counts matches chronologically **within the same result type**

When consecutive rows have the same pattern, the difference stays stable.

When the result type changes, the second row-number behaves differently, and the difference changes.

That changing difference can be used as a segment/group identifier.

---

## Why this works conceptually

Suppose a player’s results are:

```text
Win, Win, Win, Draw, Win
```

For one player, ordered by match day:

### Row number over all matches

```text
1, 2, 3, 4, 5
```

### Row number partitioned by `(player_id, result)`

For `Win` rows:

```text
1, 2, 3, -, 4
```

For `Draw` rows:

```text
-, -, -, 1, -
```

Now subtract:

```text
1-1 = 0
2-2 = 0
3-3 = 0
4-1 = 3
5-4 = 1
```

So the sequence becomes:

```text
0, 0, 0, 3, 1
```

The first three wins share the same `streak_group = 0`, which groups them together.

The draw gets a different group.
The later win also gets a different group because it belongs to a new streak after the draw.

That is exactly the behavior we want.

---

## Important subtlety

This approach does **not** assign streak groups by explicitly checking previous rows.
Instead, it lets the row-number difference naturally separate contiguous result runs.

That makes the query elegant and fully set-based.

---

# Step 2: Build the `Streaks` CTE

```sql
Streaks AS (
  SELECT
    player_id,
    SUM(1 - is_not_win) OVER (
      PARTITION BY player_id,
      streak_group
      ORDER BY match_day
    ) AS streak_length,
    is_not_win
  FROM
    RankedMatches
)
```

---

## What this CTE is doing

Now that each match is assigned to a `streak_group`, we want to count how many wins are in that group.

The expression:

```sql
1 - is_not_win
```

gives:

- `1` for a win
- `0` for a draw or loss

because:

- if `is_not_win = 0` -> `1 - 0 = 1`
- if `is_not_win = 1` -> `1 - 1 = 0`

So:

```sql
SUM(1 - is_not_win) OVER (...)
```

is effectively a cumulative count of wins within the group.

---

## Why this gives streak length

Within each `(player_id, streak_group)` partition:

- consecutive win rows accumulate as `1, 2, 3, ...`
- non-win rows contribute `0`

For a winning streak group, the last row in that group reaches the full streak length.

Then the final `MAX(...)` in the outer query can pick the largest one.

---

# Step 3: Get the longest streak for each player

```sql
SELECT
  player_id,
  MAX(
    CASE WHEN is_not_win = 0 THEN streak_length ELSE 0 END
  ) AS longest_streak
FROM
  Streaks
GROUP BY
  player_id;
```

---

## Why this works

We only want streak lengths from **winning rows**.

So the query uses:

```sql
CASE WHEN is_not_win = 0 THEN streak_length ELSE 0 END
```

That means:

- win rows contribute their streak length
- non-win rows contribute `0`

Then:

```sql
MAX(...)
```

returns the largest winning-streak length for that player.

If a player has no wins at all, all values are `0`, so the result becomes `0`.

That correctly handles players whose longest winning streak is zero.

---

# Final accepted query

```sql
WITH RankedMatches AS (
  SELECT
    player_id,
    match_day,
    result,
    CASE WHEN result = 'Win' THEN 0 ELSE 1 END AS is_not_win,
    ROW_NUMBER() OVER (
      PARTITION BY player_id
      ORDER BY
        match_day
    ) - ROW_NUMBER() OVER (
      PARTITION BY player_id,
      result
      ORDER BY
        match_day
    ) AS streak_group
  FROM
    Matches
),
Streaks AS (
  SELECT
    player_id,
    SUM(1 - is_not_win) OVER (
      PARTITION BY player_id,
      streak_group
      ORDER BY
        match_day
    ) AS streak_length,
    is_not_win
  FROM
    RankedMatches
)
SELECT
  player_id,
  MAX(
    CASE WHEN is_not_win = 0 THEN streak_length ELSE 0 END
  ) AS longest_streak
FROM
  Streaks
GROUP BY
  player_id;
```

---

# Walkthrough on the sample

## Sample input

| player_id | match_day  | result |
| --------- | ---------- | ------ |
| 1         | 2022-01-17 | Win    |
| 1         | 2022-01-18 | Win    |
| 1         | 2022-01-25 | Win    |
| 1         | 2022-01-31 | Draw   |
| 1         | 2022-02-08 | Win    |
| 2         | 2022-02-06 | Lose   |
| 2         | 2022-02-08 | Lose   |
| 3         | 2022-03-30 | Win    |

---

## Player 1

Results in order:

```text
Win, Win, Win, Draw, Win
```

Winning streaks are:

- `Win, Win, Win` -> streak length `3`
- `Win` -> streak length `1`

Longest streak:

```text
3
```

---

## Player 2

Results:

```text
Lose, Lose
```

No wins exist.

So:

```text
longest_streak = 0
```

---

## Player 3

Results:

```text
Win
```

Single win means streak length:

```text
1
```

---

## Final result

| player_id | longest_streak |
| --------- | -------------- |
| 1         | 3              |
| 2         | 0              |
| 3         | 1              |

---

# Why the solution is elegant

A direct approach might try to compare each row with the previous row and manually reset counters.

This window-function approach avoids procedural row-by-row logic.

Instead, it:

1. creates a grouping key using row-number differences
2. uses cumulative sums within those groups
3. extracts the maximum streak

That is concise, scalable, and set-based.

---

# Important SQL concepts used here

## 1. `ROW_NUMBER()`

Used to build the streak-group identifier.

## 2. Window-sum

```sql
SUM(1 - is_not_win) OVER (...)
```

Used to count wins within each group.

## 3. `CASE`

Used both to mark non-wins and to ignore non-win rows in the final `MAX()`.

## 4. CTEs

Used to break the logic into readable stages.

---

# Why `Draw` and `Lose` both break the streak

The problem defines the winning streak as:

> consecutive wins uninterrupted by draws or losses

So anything that is not `Win` ends the streak.

That is why the query uses:

```sql
CASE WHEN result = 'Win' THEN 0 ELSE 1 END
```

Both `Draw` and `Lose` are treated as non-wins.

---

# Follow-up intuition: longest streak without losing

The follow-up asks:

> what if the streak should continue for `Win` or `Draw`, and break only on `Lose`?

Then the core logic changes from:

```sql
result = 'Win'
```

to:

```sql
result != 'Lose'
```

That means:

- `Win` contributes to the streak
- `Draw` also contributes to the streak
- only `Lose` resets it

So the indicator logic would need to redefine what counts as “not part of streak.”

The same general window-function strategy can still be used.

---

# Complexity

Let `n` be the number of rows in `Matches`.

## Time Complexity

The query performs window operations over each player’s ordered matches.

A practical summary is:

```text
O(n log n)
```

because ordering within partitions generally dominates.

## Space Complexity

Additional space is used for the intermediate CTEs and window-function processing, proportional to the number of rows.

---

# Key takeaways

1. The problem is about consecutive wins, not just total wins.
2. A row-number difference can act as a streak-group identifier.
3. Once streak groups are formed, a cumulative sum can count streak length.
4. `MAX()` over winning rows gives the longest streak per player.
5. Players with no wins naturally get a longest streak of `0`.

---

## Final accepted implementation

```sql
WITH RankedMatches AS (
  SELECT
    player_id,
    match_day,
    result,
    CASE WHEN result = 'Win' THEN 0 ELSE 1 END AS is_not_win,
    ROW_NUMBER() OVER (
      PARTITION BY player_id
      ORDER BY
        match_day
    ) - ROW_NUMBER() OVER (
      PARTITION BY player_id,
      result
      ORDER BY
        match_day
    ) AS streak_group
  FROM
    Matches
),
Streaks AS (
  SELECT
    player_id,
    SUM(1 - is_not_win) OVER (
      PARTITION BY player_id,
      streak_group
      ORDER BY
        match_day
    ) AS streak_length,
    is_not_win
  FROM
    RankedMatches
)
SELECT
  player_id,
  MAX(
    CASE WHEN is_not_win = 0 THEN streak_length ELSE 0 END
  ) AS longest_streak
FROM
  Streaks
GROUP BY
  player_id;
```
