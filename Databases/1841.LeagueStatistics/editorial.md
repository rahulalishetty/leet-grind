# 1841. League Statistics — Detailed Summary

## Approach: Aggregate Ranking

This approach solves the league table problem by joining each team with every match it participated in, then using conditional aggregation to compute:

- matches played
- total points
- goals scored
- goals conceded
- goal difference

Finally, the result is sorted according to league ranking rules.

---

## Problem Restatement

For each team, we need to compute:

- `team_name`
- `matches_played`
- `points`
- `goal_for`
- `goal_against`
- `goal_diff = goal_for - goal_against`

Points are awarded as follows:

- win → `3`
- draw → `1`
- loss → `0`

A team may appear in a match as either:

- the home team
- the away team

So every calculation must correctly handle both possibilities.

---

## Core Idea

The `Matches` table stores each game from the perspective of:

- `home_team_id`
- `away_team_id`

But the final result must be from the perspective of each individual team.

So for every team, we join to all matches where that team appears either as:

- `home_team_id`
  or
- `away_team_id`

Once that is done, `CASE` expressions let us reinterpret each match from the point of view of the current team.

That is the central trick of the solution.

---

# Query

```sql
SELECT
  t.team_name,
  COUNT(*) AS matches_played,
  SUM(
    CASE
      WHEN (
        m.home_team_id = t.team_id
        AND m.home_team_goals > m.away_team_goals
      )
      OR (
        m.away_team_id = t.team_id
        AND m.away_team_goals > m.home_team_goals
      ) THEN 3
      WHEN m.home_team_goals = m.away_team_goals THEN 1
      ELSE 0
    END
  ) AS points,
  SUM(
    CASE
      WHEN m.home_team_id = t.team_id THEN m.home_team_goals
      ELSE m.away_team_goals
    END
  ) AS goal_for,
  SUM(
    CASE
      WHEN m.home_team_id = t.team_id THEN m.away_team_goals
      ELSE m.home_team_goals
    END
  ) AS goal_against,
  SUM(
    CASE
      WHEN m.home_team_id = t.team_id THEN m.home_team_goals - m.away_team_goals
      ELSE m.away_team_goals - m.home_team_goals
    END
  ) AS goal_diff
FROM
  Teams t
  JOIN Matches m
    ON m.home_team_id = t.team_id
    OR m.away_team_id = t.team_id
GROUP BY
  t.team_id,
  t.team_name
ORDER BY
  points DESC,
  goal_diff DESC,
  team_name;
```

---

# Step-by-Step Explanation

## 1. Join `Teams` to `Matches`

```sql
FROM Teams t
JOIN Matches m
  ON m.home_team_id = t.team_id
  OR m.away_team_id = t.team_id
```

### What this does

This join matches each team to every game it played.

That includes matches where the team was:

- home
- away

So each team gets one joined row per match it participated in.

For example, if Ajax played 4 matches, then Ajax appears in 4 joined rows.

---

## Why the `OR` condition is necessary

A team can appear in either of two columns in `Matches`:

- `home_team_id`
- `away_team_id`

If we joined only on `home_team_id`, then away matches would be missed.

If we joined only on `away_team_id`, then home matches would be missed.

So we must use:

```sql
m.home_team_id = t.team_id OR m.away_team_id = t.team_id
```

to capture all played matches.

---

## 2. Count matches played

```sql
COUNT(*) AS matches_played
```

After the join, each row represents one match involving the team.

So counting joined rows gives the total number of matches played.

### Example

Ajax appears in these 4 match rows:

- home vs Dortmund
- home vs Arsenal
- away vs Dortmund
- away vs Arsenal

So:

```text
matches_played = 4
```

---

## 3. Calculate points using `CASE`

```sql
SUM(
  CASE
    WHEN (
      m.home_team_id = t.team_id
      AND m.home_team_goals > m.away_team_goals
    )
    OR (
      m.away_team_id = t.team_id
      AND m.away_team_goals > m.home_team_goals
    ) THEN 3
    WHEN m.home_team_goals = m.away_team_goals THEN 1
    ELSE 0
  END
) AS points
```

This is the most important logic in the query.

It computes how many points the current team earns from each joined match row, then sums those values across all matches.

---

## How the points logic works

### Case 1: Team wins

A team wins if:

- it is the home team and `home_team_goals > away_team_goals`
  or
- it is the away team and `away_team_goals > home_team_goals`

If either is true:

```sql
THEN 3
```

So the team gets 3 points.

---

### Case 2: Match is a draw

```sql
WHEN m.home_team_goals = m.away_team_goals THEN 1
```

If the goals are equal, both teams get 1 point.

Since the row is already joined to one specific team, this correctly assigns 1 point to that team.

---

### Case 3: Team loses

```sql
ELSE 0
```

If neither win nor draw happened, the team lost, so it gets 0 points.

---

## 4. Calculate goals scored (`goal_for`)

```sql
SUM(
  CASE
    WHEN m.home_team_id = t.team_id THEN m.home_team_goals
    ELSE m.away_team_goals
  END
) AS goal_for
```

This calculates how many goals the team scored across all matches.

### Logic

- if the team is home, its scored goals are `home_team_goals`
- otherwise, it is away, so its scored goals are `away_team_goals`

---

## 5. Calculate goals conceded (`goal_against`)

```sql
SUM(
  CASE
    WHEN m.home_team_id = t.team_id THEN m.away_team_goals
    ELSE m.home_team_goals
  END
) AS goal_against
```

This calculates how many goals were scored against the team.

### Logic

- if the team is home, opponent goals are `away_team_goals`
- if the team is away, opponent goals are `home_team_goals`

---

## 6. Calculate goal difference (`goal_diff`)

```sql
SUM(
  CASE
    WHEN m.home_team_id = t.team_id THEN m.home_team_goals - m.away_team_goals
    ELSE m.away_team_goals - m.home_team_goals
  END
) AS goal_diff
```

This computes the goal difference on a match-by-match basis and then sums it.

### Why this works

For each row:

- if team is home → team goals minus opponent goals
- if team is away → away goals minus home goals

Summing these per-match differences gives the total:

```text
goal_diff = goal_for - goal_against
```

---

## 7. Group by team

```sql
GROUP BY t.team_id, t.team_name
```

This ensures that all match rows belonging to the same team are aggregated into one final result row.

---

## 8. Apply league ranking order

```sql
ORDER BY
  points DESC,
  goal_diff DESC,
  team_name;
```

This sorts teams according to the required ranking rules:

1. more points first
2. if tied, higher goal difference first
3. if still tied, alphabetical team name

---

# Worked Example

## Input

### Teams

| team_id | team_name |
| ------: | --------- |
|       1 | Ajax      |
|       4 | Dortmund  |
|       6 | Arsenal   |

### Matches

| home_team_id | away_team_id | home_team_goals | away_team_goals |
| -----------: | -----------: | --------------: | --------------: |
|            1 |            4 |               0 |               1 |
|            1 |            6 |               3 |               3 |
|            4 |            1 |               5 |               2 |
|            6 |            1 |               0 |               0 |

---

# Team-by-Team Breakdown

## Ajax

Matches:

1. Ajax vs Dortmund → `0 - 1` → loss
2. Ajax vs Arsenal → `3 - 3` → draw
3. Dortmund vs Ajax → `5 - 2` → loss
4. Arsenal vs Ajax → `0 - 0` → draw

### Matches played

```text
4
```

### Points

- loss → 0
- draw → 1
- loss → 0
- draw → 1

Total:

```text
2
```

### Goals for

```text
0 + 3 + 2 + 0 = 5
```

### Goals against

```text
1 + 3 + 5 + 0 = 9
```

### Goal difference

```text
5 - 9 = -4
```

---

## Dortmund

Matches:

1. Ajax vs Dortmund → Dortmund wins `1 - 0`
2. Dortmund vs Ajax → Dortmund wins `5 - 2`

### Matches played

```text
2
```

### Points

- win → 3
- win → 3

Total:

```text
6
```

### Goals for

```text
1 + 5 = 6
```

### Goals against

```text
0 + 2 = 2
```

### Goal difference

```text
6 - 2 = 4
```

---

## Arsenal

Matches:

1. Ajax vs Arsenal → `3 - 3` → draw
2. Arsenal vs Ajax → `0 - 0` → draw

### Matches played

```text
2
```

### Points

- draw → 1
- draw → 1

Total:

```text
2
```

### Goals for

```text
3 + 0 = 3
```

### Goals against

```text
3 + 0 = 3
```

### Goal difference

```text
0
```

---

# Final Output

| team_name | matches_played | points | goal_for | goal_against | goal_diff |
| --------- | -------------: | -----: | -------: | -----------: | --------: |
| Dortmund  |              2 |      6 |        6 |            2 |         4 |
| Arsenal   |              2 |      2 |        3 |            3 |         0 |
| Ajax      |              4 |      2 |        5 |            9 |        -4 |

---

# Why Arsenal Comes Before Ajax

Both Arsenal and Ajax have:

```text
points = 2
```

So the next sorting rule is `goal_diff`.

- Arsenal → `0`
- Ajax → `-4`

Since `0 > -4`, Arsenal ranks higher.

---

# Why This Query Is Elegant

The nice part of this solution is that it avoids splitting home and away statistics into separate queries.

Instead, it uses one joined dataset and lets `CASE` reinterpret each match from the current team's point of view.

That keeps everything in one aggregation query.

---

# Alternative Mental Model

You can think of each joined row as being transformed into a team-centric match summary:

- result points from this match
- goals scored by this team
- goals conceded by this team
- goal difference from this match

Then `SUM(...)` adds these match summaries together to produce the league totals.

---

# Portability Note

The query orders by aliases:

```sql
ORDER BY points DESC, goal_diff DESC, team_name
```

That is supported in many SQL engines and is usually fine.

---

# Complexity Analysis

Let:

- `T` = number of teams
- `M` = number of matches

The join connects each team to the matches it participated in.

Since each match contributes to exactly two teams, the joined result is proportional to roughly `2M`.

So the work is mainly:

- joining teams to matches
- aggregating by team
- sorting the final team rows

Conceptually, this is efficient and scales well for this type of reporting query.

---

# Final Recommended Query

```sql
SELECT
  t.team_name,
  COUNT(*) AS matches_played,
  SUM(
    CASE
      WHEN (
        m.home_team_id = t.team_id
        AND m.home_team_goals > m.away_team_goals
      )
      OR (
        m.away_team_id = t.team_id
        AND m.away_team_goals > m.home_team_goals
      ) THEN 3
      WHEN m.home_team_goals = m.away_team_goals THEN 1
      ELSE 0
    END
  ) AS points,
  SUM(
    CASE
      WHEN m.home_team_id = t.team_id THEN m.home_team_goals
      ELSE m.away_team_goals
    END
  ) AS goal_for,
  SUM(
    CASE
      WHEN m.home_team_id = t.team_id THEN m.away_team_goals
      ELSE m.home_team_goals
    END
  ) AS goal_against,
  SUM(
    CASE
      WHEN m.home_team_id = t.team_id THEN m.home_team_goals - m.away_team_goals
      ELSE m.away_team_goals - m.home_team_goals
    END
  ) AS goal_diff
FROM Teams t
JOIN Matches m
  ON m.home_team_id = t.team_id
  OR m.away_team_id = t.team_id
GROUP BY
  t.team_id,
  t.team_name
ORDER BY
  points DESC,
  goal_diff DESC,
  team_name;
```

---

# Key Takeaways

- Join each team to all matches where it appears as home or away
- Use `CASE` to compute statistics from the team's perspective
- `COUNT(*)` gives matches played
- `SUM(CASE ...)` gives points, goals for, goals against, and goal difference
- Sort by points, then goal difference, then team name

---
