# Fraction of Players Who Logged In Again the Day After First Login (Game Play Analysis IV-style) — SQL Summary

Problem (concept, commonly LeetCode 550):
Compute the fraction of players who **logged in on the day after their first login**.

Formally:

- For each `player_id`, let `first_login = MIN(event_date)`.
- A player is “returning next day” if there exists a row with:
  ```text
  event_date = first_login + 1 day
  ```
- Return:
  ```text
  fraction = (# players returning next day) / (# total players)
  ```
- Round to **2 decimal places**.

Key schema property:

- `(player_id, event_date)` is the **primary key** in `Activity`.
  - So a player cannot have two rows on the same date.

---

## Why This Is Not Just `MIN()` + `GROUP BY`

Part I was easy because you only needed:

- earliest date per player → `MIN(event_date)`.

Here you need:

- earliest date per player **and**
- whether a row exists at **first_login + 1 day**.

That “existence check” is the main challenge.

---

## Approach 1: Multi-Column `IN` (Tuple IN) + Date Arithmetic

### Intuition

We can reuse the “tuple matching” trick from Part II:

- First compute `(player_id, first_login)` pairs.
- Then check if the table contains a row `(player_id, first_login + 1 day)`.

A clever way to express that in MySQL:

- For an Activity row `A1` at date `d`,
- compute `DATE_SUB(d, 1 day)` and see if that equals the player’s `first_login`.

If yes, then `d` is exactly `first_login + 1 day`, meaning the player returned next day.

### Algorithm

1. Compute `(player_id, first_login)` via:
   - `MIN(event_date)` grouped by player.
2. From `Activity` rows `A1`, keep those where:
   - `(A1.player_id, DATE_SUB(A1.event_date, 1 day))` matches `(player_id, first_login)`.
3. Count how many such players exist (in MySQL, `COUNT(A1.player_id)` is safe here).
4. Divide by total distinct players and round.

### Implementation (MySQL)

```sql
SELECT
  ROUND(
    COUNT(A1.player_id)
    / (SELECT COUNT(DISTINCT A3.player_id) FROM Activity A3)
  , 2) AS fraction
FROM
  Activity A1
WHERE
  (A1.player_id, DATE_SUB(A1.event_date, INTERVAL 1 DAY)) IN (
    SELECT
      A2.player_id,
      MIN(A2.event_date)
    FROM
      Activity A2
    GROUP BY
      A2.player_id
  );
```

### Why `COUNT(A1.player_id)` (not DISTINCT) is enough

Because `(player_id, event_date)` is a primary key:

- For a given player, there can be **at most one** row on `first_login + 1 day`.
- So duplicates cannot occur for the “next-day login” event.

### Portability note

Tuple `IN` comparisons (row constructors) are supported in MySQL, but not equally supported across all DBMS. If you want a widely portable approach, use CTE + JOIN (Approach 2).

---

## Approach 2: CTEs + INNER JOIN (Preferred)

### Intuition

Think in clean, linear steps using CTEs:

1. Find each player’s `first_login`.
2. Count how many players have a login at `first_login + 1 day`.
3. Divide (2) by total players (1) and round.

This is easier to derive in an interview and is usually very efficient.

### Algorithm

1. `first_logins`: `(player_id, first_login)` per player.
2. `consec_logins`: count players who have Activity on `first_login + 1 day`.
3. Final: `num_logins / total_players`, rounded.

### Implementation (MySQL)

```sql
WITH first_logins AS (
  SELECT
    A.player_id,
    MIN(A.event_date) AS first_login
  FROM
    Activity A
  GROUP BY
    A.player_id
), consec_logins AS (
  SELECT
    COUNT(A.player_id) AS num_logins
  FROM
    first_logins F
    INNER JOIN Activity A
      ON F.player_id = A.player_id
     AND F.first_login = DATE_SUB(A.event_date, INTERVAL 1 DAY)
)
SELECT
  ROUND(
    (SELECT C.num_logins FROM consec_logins C)
    / (SELECT COUNT(F.player_id) FROM first_logins F)
  , 2) AS fraction;
```

### Why this join condition works

The join keeps rows where:

```text
F.first_login = A.event_date - 1 day
```

Rearrange:

```text
A.event_date = F.first_login + 1 day
```

So every joined row is a “next day after first login” record.

### Why `COUNT(A.player_id)` is enough

Same reason as Approach 1:

- Primary key prevents duplicate same-day records per player.

---

## Which Approach Should You Use?

- **Approach 2 (CTE + JOIN)** is typically preferred:
  - easier to reason about,
  - more portable,
  - efficient with indexes,
  - interview-friendly.

- **Approach 1 (tuple IN)** is elegant and compact in MySQL:
  - builds on prior problem series tricks,
  - but can be harder to invent on the spot.

---

## Practical Notes (Performance & Indexing)

Helpful indexing:

- Primary key `(player_id, event_date)` is ideal (often given).
- It makes:
  - `MIN(event_date)` per player efficient,
  - and the join lookup on `(player_id, event_date)` efficient.

Complexity intuition:

- Both approaches are generally near-linear / log-linear with proper indexes.
- They avoid correlated subqueries that can degrade to nested loops.

---
