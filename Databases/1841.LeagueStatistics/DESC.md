# 1841. League Statistics

## Table: Teams

| Column Name | Type    |
| ----------- | ------- |
| team_id     | int     |
| team_name   | varchar |

**Notes:**

- `team_id` contains **unique values**.
- Each row represents a **team participating in the league**.

---

## Table: Matches

| Column Name     | Type |
| --------------- | ---- |
| home_team_id    | int  |
| away_team_id    | int  |
| home_team_goals | int  |
| away_team_goals | int  |

**Notes:**

- `(home_team_id, away_team_id)` is the **primary key**.
- Each row represents a **match played between two teams**.
- `home_team_goals` is the number of goals scored by the **home team**.
- `away_team_goals` is the number of goals scored by the **away team**.
- The team that scores more goals **wins the match**.

---

# Problem

Generate **league statistics** based on the matches played.

Points are awarded as follows:

- **Win** → 3 points
- **Loss** → 0 points
- **Draw** → 1 point for each team

For each team, compute the following statistics:

| Column         | Description                         |
| -------------- | ----------------------------------- |
| team_name      | Name of the team                    |
| matches_played | Total matches played (home + away)  |
| points         | Total league points                 |
| goal_for       | Total goals scored by the team      |
| goal_against   | Total goals scored against the team |
| goal_diff      | `goal_for - goal_against`           |

---

# Sorting Rules

The result must be ordered by:

1. **points (descending)**
2. **goal_diff (descending)**
3. **team_name (lexicographical order)**

---

# Example

## Input

### Teams Table

| team_id | team_name |
| ------- | --------- |
| 1       | Ajax      |
| 4       | Dortmund  |
| 6       | Arsenal   |

### Matches Table

| home_team_id | away_team_id | home_team_goals | away_team_goals |
| ------------ | ------------ | --------------- | --------------- |
| 1            | 4            | 0               | 1               |
| 1            | 6            | 3               | 3               |
| 4            | 1            | 5               | 2               |
| 6            | 1            | 0               | 0               |

---

# Output

| team_name | matches_played | points | goal_for | goal_against | goal_diff |
| --------- | -------------- | ------ | -------- | ------------ | --------- |
| Dortmund  | 2              | 6      | 6        | 2            | 4         |
| Arsenal   | 2              | 2      | 3        | 3            | 0         |
| Ajax      | 4              | 2      | 5        | 9            | -4        |

---

# Explanation

### Ajax (team_id = 1)

Matches played:

| Opponent    | Score |
| ----------- | ----- |
| vs Dortmund | 0 - 1 |
| vs Arsenal  | 3 - 3 |
| vs Dortmund | 2 - 5 |
| vs Arsenal  | 0 - 0 |

Results:

- Loss
- Draw
- Loss
- Draw

Points:

```
0 + 1 + 0 + 1 = 2
```

Goals:

- Goals scored = **5**
- Goals conceded = **9**
- Goal difference = **-4**

---

### Dortmund (team_id = 4)

Matches played:

| Opponent | Score |
| -------- | ----- |
| vs Ajax  | 1 - 0 |
| vs Ajax  | 5 - 2 |

Results:

- Win
- Win

Points:

```
3 + 3 = 6
```

Goals:

- Goals scored = **6**
- Goals conceded = **2**
- Goal difference = **4**

---

### Arsenal (team_id = 6)

Matches played:

| Opponent | Score |
| -------- | ----- |
| vs Ajax  | 3 - 3 |
| vs Ajax  | 0 - 0 |

Results:

- Draw
- Draw

Points:

```
1 + 1 = 2
```

Goals:

- Goals scored = **3**
- Goals conceded = **3**
- Goal difference = **0**

---

# Ranking Logic

Final ordering rules applied:

1. **Dortmund** → highest points (6)
2. **Arsenal** and **Ajax** both have 2 points
3. Compare `goal_diff`
   - Arsenal → 0
   - Ajax → -4

Therefore:

```
Dortmund
Arsenal
Ajax
```
