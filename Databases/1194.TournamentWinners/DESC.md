# 1194. Tournament Winners

## Tables

### Players

| Column Name | Type |
| ----------- | ---- |
| player_id   | int  |
| group_id    | int  |

- `player_id` is the **primary key** (unique).
- Each row indicates which **group** a player belongs to.

---

### Matches

| Column Name   | Type |
| ------------- | ---- |
| match_id      | int  |
| first_player  | int  |
| second_player | int  |
| first_score   | int  |
| second_score  | int  |

- `match_id` is the **primary key**.
- `first_player` and `second_player` reference `player_id` in the **Players** table.
- `first_score` and `second_score` store the points scored by each player.

It is guaranteed that **players in each match belong to the same group**.

---

## Problem

The **winner in each group** is defined as:

- The player with the **maximum total points scored across all matches** within that group.
- If there is a **tie in total points**, the player with the **lowest `player_id` wins**.

Write a SQL query to find the **winner in each group**.

Return the result table in **any order**.

---

## Example

### Input

#### Players

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

---

#### Matches

| match_id | first_player | second_player | first_score | second_score |
| -------- | ------------ | ------------- | ----------- | ------------ |
| 1        | 15           | 45            | 3           | 0            |
| 2        | 30           | 25            | 1           | 2            |
| 3        | 30           | 15            | 2           | 0            |
| 4        | 40           | 20            | 5           | 2            |
| 5        | 35           | 50            | 1           | 1            |

---

### Output

| group_id | player_id |
| -------- | --------- |
| 1        | 15        |
| 2        | 35        |
| 3        | 40        |

---

## Explanation

- **Group 1**:
  - Player 15 scored **3 + 2 = 5**
  - Player 25 scored **2**
  - Player 30 scored **1 + 2 = 3**
  - Player 45 scored **0**

  Player **15** has the highest score → winner.

- **Group 2**:
  - Player 35 scored **1**
  - Player 50 scored **1**
  - Player 10 did not appear in matches → **0**

  Tie between 35 and 50 → **lowest player_id wins → 35**.

- **Group 3**:
  - Player 40 scored **5**
  - Player 20 scored **2**

  Player **40** wins.

---
