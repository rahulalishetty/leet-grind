# 2173. Longest Winning Streak

## Table: Matches

| Column Name | Type |
| ----------- | ---- |
| player_id   | int  |
| match_day   | date |
| result      | enum |

- `(player_id, match_day)` is the **primary key**.
- Each row represents a **match played by a player** on a specific day.
- `result` is an ENUM with values:
  - `'Win'`
  - `'Draw'`
  - `'Lose'`

---

## Problem

The **winning streak** of a player is defined as the number of **consecutive wins** that are **not interrupted by draws or losses**.

Your task is to write a SQL query to determine:

> The **longest winning streak for each player**.

---

## Output Requirements

Return a table containing:

| Column         | Description                        |
| -------------- | ---------------------------------- |
| player_id      | ID of the player                   |
| longest_streak | Longest consecutive winning streak |

The result can be returned **in any order**.

---

# Example

## Input

### Matches

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

## Output

| player_id | longest_streak |
| --------- | -------------- |
| 1         | 3              |
| 2         | 0              |
| 3         | 1              |

---

# Explanation

## Player 1

Matches:

```
2022‑01‑17  Win
2022‑01‑18  Win
2022‑01‑25  Win
2022‑01‑31  Draw
2022‑02‑08  Win
```

Winning streaks:

```
Win → Win → Win = 3
Draw breaks the streak
Win = 1
```

Longest streak:

```
3
```

---

## Player 2

Matches:

```
Lose
Lose
```

There are **no wins**, therefore:

```
Longest streak = 0
```

---

## Player 3

Matches:

```
Win
```

Only one match and it is a win.

```
Longest streak = 1
```

---

# Follow‑up Question

If we want to compute the **longest streak without losing** (i.e., both **Win** and **Draw** count as part of the streak):

- The streak continues for results:

```
Win
Draw
```

- The streak **breaks only on:**

```
Lose
```

So the logic would change from:

```
result = 'Win'
```

to

```
result != 'Lose'
```

meaning both **Win** and **Draw** would extend the streak.
