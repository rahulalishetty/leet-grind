# 1194. Tournament Winners

## Approach: Grouped Window Ranking Method

## Core idea

We need to find the winner in each group.

A player's total score is the sum of all points they scored across all matches in their group.
The winner of a group is:

1. the player with the **highest total score**
2. if multiple players tie on total score, the one with the **smallest `player_id`**

A clean way to solve this is:

1. combine scores from both match positions into one unified score table
2. aggregate total score per player
3. attach group information
4. use a window function to pick the top player in each group

This solution uses:

- **CTEs** for clarity
- **`UNION ALL`** to combine score rows
- **aggregation** to compute total score
- **`FIRST_VALUE()`** to pick the winner inside each group

---

## Why the scores must be unified first

In the `Matches` table, a player can appear in two different roles:

- `first_player` with score `first_score`
- `second_player` with score `second_score`

If we want total score per player, we cannot just sum one side of the table.

We first need to transform match rows into a simpler structure:

| player_id | score |

so that every appearance of a player contributes one score row.

That is exactly what the first CTE does.

---

# Step 1: Consolidate scores from both player positions

```sql
WITH PlayerScores AS (
    SELECT first_player AS player_id, first_score AS score
    FROM matches
    UNION ALL
    SELECT second_player AS player_id, second_score AS score
    FROM matches
)
```

---

## Why `UNION ALL` is correct

We want to keep **every scoring event**.

If a player appears in multiple matches, all of those score rows must remain.
So we use:

```sql
UNION ALL
```

and not `UNION`.

`UNION` removes duplicates, which would be wrong here because repeated score rows still represent real match contributions.

---

## Example result of `PlayerScores`

Using the sample:

### Matches

| match_id | first_player | second_player | first_score | second_score |
| -------- | ------------ | ------------- | ----------- | ------------ |
| 1        | 15           | 45            | 3           | 0            |
| 2        | 30           | 25            | 1           | 2            |
| 3        | 30           | 15            | 2           | 0            |
| 4        | 40           | 20            | 5           | 2            |
| 5        | 35           | 50            | 1           | 1            |

The unified score rows become:

| player_id | score |
| --------- | ----- |
| 15        | 3     |
| 30        | 1     |
| 30        | 2     |
| 40        | 5     |
| 35        | 1     |
| 45        | 0     |
| 25        | 2     |
| 15        | 0     |
| 20        | 2     |
| 50        | 1     |

Now every player's scoring contributions are in one column.

---

# Step 2: Aggregate total score per player

Now we sum all score rows for each player.

```sql
TotalScores AS (
    SELECT
        player_id,
        SUM(score) AS total_score
    FROM
        PlayerScores
    GROUP BY
        player_id
)
```

---

## Why this works

After Step 1, each row is already in the form:

| player_id | score |

So grouping by `player_id` and summing `score` gives each player's tournament total.

---

## Example result of `TotalScores`

| player_id | total_score |
| --------- | ----------- |
| 15        | 3           |
| 25        | 2           |
| 30        | 3           |
| 35        | 1           |
| 40        | 5           |
| 45        | 0           |
| 20        | 2           |
| 50        | 1           |

Careful check for group 1 in the sample:

- Player 15: `3 + 0 = 3`
- Player 25: `2`
- Player 30: `1 + 2 = 3`
- Player 45: `0`

So among players who actually appear in matches, players `15` and `30` tie with score `3`, and tie-breaking chooses `15`.

For group 2:

- Player 35: `1`
- Player 50: `1`

Tie-breaking chooses `35`.

For group 3:

- Player 40: `5`
- Player 20: `2`

Winner is `40`.

---

# Step 3: Attach group information and pick the winner per group

The `TotalScores` CTE only contains:

- `player_id`
- `total_score`

But to determine winners **per group**, we need each player's `group_id`.

That comes from the `Players` table.

Then we use a window function:

```sql
FIRST_VALUE(TotalScores.player_id) OVER (
    PARTITION BY group_id
    ORDER BY total_score DESC, TotalScores.player_id
)
```

This means:

- inside each group
- sort players by:
  1. highest score first
  2. smallest player_id first for tie-breaking
- return the first player's id

---

## Final query

```sql
WITH PlayerScores AS (
  SELECT
    first_player AS player_id,
    first_score AS score
  FROM
    matches
  UNION ALL
  SELECT
    second_player AS player_id,
    second_score AS score
  FROM
    matches
),
TotalScores AS (
  SELECT
    player_id,
    SUM(score) AS total_score
  FROM
    PlayerScores
  GROUP BY
    player_id
)
SELECT
  DISTINCT group_id,
  FIRST_VALUE(TotalScores.player_id) OVER (
    PARTITION BY group_id
    ORDER BY
      total_score DESC,
      TotalScores.player_id
  ) AS player_id
FROM
  TotalScores
  LEFT JOIN players
    ON TotalScores.player_id = players.player_id;
```

---

# Step-by-step explanation of the final query

## `PlayerScores` CTE

```sql
WITH PlayerScores AS (
  SELECT first_player AS player_id, first_score AS score
  FROM matches
  UNION ALL
  SELECT second_player AS player_id, second_score AS score
  FROM matches
)
```

This transforms match data into a single list of player scores.

---

## `TotalScores` CTE

```sql
TotalScores AS (
  SELECT
    player_id,
    SUM(score) AS total_score
  FROM
    PlayerScores
  GROUP BY
    player_id
)
```

This computes one total score per player.

---

## Join with `Players`

```sql
LEFT JOIN players ON TotalScores.player_id = players.player_id
```

This attaches each player's `group_id` so that grouping by tournament group becomes possible.

---

## Window function with `FIRST_VALUE`

```sql
FIRST_VALUE(TotalScores.player_id) OVER (
  PARTITION BY group_id
  ORDER BY total_score DESC, TotalScores.player_id
)
```

This selects the winner inside each group:

- first by highest score
- then by lowest player id in case of tie

---

## `DISTINCT group_id`

The window function produces a winner value on every row within the same group.
Since we only want one row per group, we use:

```sql
SELECT DISTINCT group_id, ...
```

to collapse duplicates.

---

# Walkthrough on the sample

## Players

| player_id | group_id |
| --------- | -------- |
| 15        | 1        |
| 25        | 1        |
| 30        | 1        |
| 45        | 1        |
| 10        | 2        |
| 35        | 2        |
| 50        | 2        |
| 20        | 3        |
| 40        | 3        |

## Matches

| match_id | first_player | second_player | first_score | second_score |
| -------- | ------------ | ------------- | ----------- | ------------ |
| 1        | 15           | 45            | 3           | 0            |
| 2        | 30           | 25            | 1           | 2            |
| 3        | 30           | 15            | 2           | 0            |
| 4        | 40           | 20            | 5           | 2            |
| 5        | 35           | 50            | 1           | 1            |

### Group 1 totals

- 15 -> 3
- 25 -> 2
- 30 -> 3
- 45 -> 0

Tie between `15` and `30` with total score `3`.
Lower `player_id` wins -> `15`.

### Group 2 totals

- 35 -> 1
- 50 -> 1

Tie -> lower `player_id` wins -> `35`.

### Group 3 totals

- 40 -> 5
- 20 -> 2

Winner -> `40`.

Final output:

| group_id | player_id |
| -------- | --------- |
| 1        | 15        |
| 2        | 35        |
| 3        | 40        |

---

# Important note about players with no matches

A careful concern: the `TotalScores` CTE only contains players who appear in matches.

So players with zero matches do not appear in `TotalScores`.

In the sample, player `10` from group `2` has no matches and therefore is absent from the ranking step.

Under the provided approach and explanation, winners are determined from aggregated scored players, and tie-breaking is applied among those participants.

This matches the provided solution structure.

---

# Why `FIRST_VALUE` is a good fit

A simpler mental model is:

- sort players within each group by the winning criteria
- then take the first player

That is exactly what `FIRST_VALUE` does.

It is a clean alternative to writing another rank-and-filter query.

---

# Why `ORDER BY total_score DESC, player_id` handles tie-breaking

The winning rules are:

1. maximum total score
2. if tied, smallest player id

This translates directly into:

```sql
ORDER BY total_score DESC, player_id ASC
```

The first row under that ordering is the winner.

---

# Strengths of this approach

- clean multi-stage structure
- separates score collection, score aggregation, and winner selection
- uses window functions elegantly
- easy to reason about once you understand window ordering

### Tradeoffs

- relies on window-function support
- players with zero matches are not part of `TotalScores`, so this approach focuses on players with recorded match scores

---

# Complexity

Let:

- `P` = number of players
- `M` = number of matches

## Time Complexity

The solution:

- scans matches twice logically through the `UNION ALL`
- aggregates player totals
- joins totals to players
- applies window ordering within groups

A practical summary is:

```text
O(M + P log P)
```

depending on how the database executes grouping and window sorting.

## Space Complexity

Additional space is needed for:

- the unified score CTE
- the aggregated totals CTE

So auxiliary space depends mainly on the number of match-derived score rows.

---

# Key takeaways

1. Convert both match sides into a unified `(player_id, score)` table.
2. Sum scores per player to get total performance.
3. Join with `Players` to recover group membership.
4. Use `FIRST_VALUE()` partitioned by group to choose the winner.
5. Tie-breaking is handled by ordering on `total_score DESC, player_id`.

---

## Final accepted implementation

```sql
WITH PlayerScores AS (
  SELECT
    first_player AS player_id,
    first_score AS score
  FROM
    matches
  UNION ALL
  SELECT
    second_player AS player_id,
    second_score AS score
  FROM
    matches
),
TotalScores AS (
  SELECT
    player_id,
    SUM(score) AS total_score
  FROM
    PlayerScores
  GROUP BY
    player_id
)
SELECT
  DISTINCT group_id,
  FIRST_VALUE(TotalScores.player_id) OVER (
    PARTITION BY group_id
    ORDER BY
      total_score DESC,
      TotalScores.player_id
  ) AS player_id
FROM
  TotalScores
  LEFT JOIN players
    ON TotalScores.player_id = players.player_id;
```
