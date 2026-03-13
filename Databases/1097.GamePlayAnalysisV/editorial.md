# 1097. Game Play Analysis V

## Approach: CTEs, Indicator Variable, and `LEFT JOIN`

## Core idea

For each player's **install date** (their first login date), we need to report:

1. how many players installed on that date
2. what fraction of those players came back on the **very next day**

That second part is called **day 1 retention**.

A clear way to solve this is in three stages:

1. find each player's first login date
2. determine whether each player logged in again exactly one day after that first login
3. group by install date and compute:
   - number of installs
   - retention ratio

This solution uses:

- **CTEs** to organize the logic
- a **0/1 indicator variable** to mark whether a player returned on day 1
- a **LEFT JOIN** to preserve players who did _not_ return the next day

---

## Why CTEs are useful here

This problem is conceptually multi-step:

- first identify installs
- then detect next-day returners
- then aggregate by install date

A Common Table Expression (CTE) is a strong fit because it lets us break the problem into named stages.

That makes the logic easier to inspect and modify.

This is especially useful in this Game Play Analysis series, where each part builds on similar ideas with slightly different requirements.

---

# Step 1: Identify the first login date for each player

The install date of a player is defined as the **first day they logged in**.

So for each `player_id`, we compute:

```sql
MIN(event_date)
```

That gives:

```sql
WITH first_logins AS (
  SELECT
    A.player_id,
    MIN(A.event_date) AS first_login
  FROM
    Activity A
  GROUP BY
    A.player_id
)
```

---

## Why this works

- `GROUP BY A.player_id` groups activity rows player by player
- `MIN(A.event_date)` gives the earliest login date for that player
- that earliest date is the install date

---

## Example result of `first_logins`

For the sample data:

| player_id | device_id | event_date | games_played |
| --------- | --------- | ---------- | ------------ |
| 1         | 2         | 2016-03-01 | 5            |
| 1         | 2         | 2016-03-02 | 6            |
| 2         | 3         | 2017-06-25 | 1            |
| 3         | 1         | 2016-03-01 | 0            |
| 3         | 4         | 2016-07-03 | 5            |

the first-login table becomes:

| player_id | first_login |
| --------- | ----------- |
| 1         | 2016-03-01  |
| 2         | 2017-06-25  |
| 3         | 2016-03-01  |

This is the install-date information we need.

---

# Step 2: Mark whether each player returned the next day

Now we need to identify whether each player logged in **exactly one day after** their first login.

A convenient way to represent this is with an **indicator variable**:

- `1` if the player logged in on the next day
- `0` otherwise

This is sometimes called a **dummy variable** in statistics.

The solution builds that using a `LEFT JOIN`.

---

## Why `LEFT JOIN` is needed

A skeptical question is: why not just use an inner join?

Because we need to keep **all players**, including those who did **not** return the next day.

If we used an inner join, players who failed to return would disappear from the result, which would break the denominator for retention.

A `LEFT JOIN` preserves every player from `first_logins`, and then we can assign:

- `1` if a matching next-day activity row exists
- `0` if it does not

---

## Build the indicator variable

```sql
, consec_login_info AS (
  SELECT
    F.player_id,
    (CASE
      WHEN A.player_id IS NULL THEN 0
      ELSE 1
    END) AS logged_in_consecutively,
    F.first_login
  FROM
    first_logins F
    LEFT JOIN Activity A
      ON F.player_id = A.player_id
     AND F.first_login = DATE_SUB(A.event_date, INTERVAL 1 DAY)
)
```

---

## Understanding the join condition

The key part is:

```sql
F.first_login = DATE_SUB(A.event_date, INTERVAL 1 DAY)
```

This is equivalent to saying:

```text
A.event_date = F.first_login + 1 day
```

So it matches a player's activity row **only if** that activity happened on the day immediately after their first login.

---

## Why the `CASE` works

After the `LEFT JOIN`:

- if a matching next-day row exists, `A.player_id` is not null
- if no match exists, the joined columns from `A` are null

So:

```sql
CASE
  WHEN A.player_id IS NULL THEN 0
  ELSE 1
END
```

produces the 0/1 flag we need.

---

## Example result of `consec_login_info`

For the sample, this CTE becomes:

| player_id | logged_in_consecutively | first_login |
| --------- | ----------------------- | ----------- |
| 1         | 1                       | 2016-03-01  |
| 2         | 0                       | 2017-06-25  |
| 3         | 0                       | 2016-03-01  |

Interpretation:

- Player 1 installed on `2016-03-01` and returned on `2016-03-02` -> `1`
- Player 2 installed on `2017-06-25` and did not return the next day -> `0`
- Player 3 installed on `2016-03-01` and did not return the next day -> `0`

This table is the heart of the solution.

---

# Step 3: Group by install date and compute the final metrics

Now that each player has:

- their install date
- a 0/1 indicator of next-day return

we can aggregate by `first_login`.

We need:

## installs

This is simply:

```sql
COUNT(C.player_id)
```

because there is one row per player in `consec_login_info`.

## day 1 retention

This is:

```text
(number of players who returned next day)
/
(total installs on that day)
```

Since `logged_in_consecutively` is `1` for returners and `0` for non-returners, summing it gives the number of returners.

So retention becomes:

```sql
SUM(C.logged_in_consecutively) / COUNT(C.player_id)
```

Then round to 2 decimals.

---

## Final accepted query

```sql
WITH first_logins AS (
  SELECT
    A.player_id,
    MIN(A.event_date) AS first_login
  FROM
    Activity A
  GROUP BY
    A.player_id
), consec_login_info AS (
  SELECT
    F.player_id,
    (CASE
      WHEN A.player_id IS NULL THEN 0
      ELSE 1
    END) AS logged_in_consecutively,
    F.first_login
  FROM
    first_logins F
    LEFT JOIN Activity A ON F.player_id = A.player_id
    AND F.first_login = DATE_SUB(A.event_date, INTERVAL 1 DAY)
)
SELECT
  C.first_login AS install_dt,
  COUNT(C.player_id) AS installs,
  ROUND(
    SUM(C.logged_in_consecutively)
    / COUNT(C.player_id)
  , 2) AS Day1_Retention
FROM
  consec_login_info C
GROUP BY
  C.first_login;
```

---

# Full explanation of the final query

## `first_logins` CTE

```sql
WITH first_logins AS (
  SELECT
    A.player_id,
    MIN(A.event_date) AS first_login
  FROM
    Activity A
  GROUP BY
    A.player_id
)
```

This gives each player's install date.

---

## `consec_login_info` CTE

```sql
, consec_login_info AS (
  SELECT
    F.player_id,
    (CASE
      WHEN A.player_id IS NULL THEN 0
      ELSE 1
    END) AS logged_in_consecutively,
    F.first_login
  FROM
    first_logins F
    LEFT JOIN Activity A ON F.player_id = A.player_id
    AND F.first_login = DATE_SUB(A.event_date, INTERVAL 1 DAY)
)
```

This determines whether each player returned on day 1.

---

## Final aggregation

```sql
SELECT
  C.first_login AS install_dt,
  COUNT(C.player_id) AS installs,
  ROUND(
    SUM(C.logged_in_consecutively)
    / COUNT(C.player_id)
  , 2) AS Day1_Retention
FROM
  consec_login_info C
GROUP BY
  C.first_login;
```

This produces one row per install date with:

- total installs
- day 1 retention

---

# Walkthrough on the sample

## Sample input

| player_id | device_id | event_date | games_played |
| --------- | --------- | ---------- | ------------ |
| 1         | 2         | 2016-03-01 | 5            |
| 1         | 2         | 2016-03-02 | 6            |
| 2         | 3         | 2017-06-25 | 1            |
| 3         | 1         | 2016-03-01 | 0            |
| 3         | 4         | 2016-07-03 | 5            |

---

## First login per player

| player_id | first_login |
| --------- | ----------- |
| 1         | 2016-03-01  |
| 2         | 2017-06-25  |
| 3         | 2016-03-01  |

---

## Day-1 return indicator

| player_id | logged_in_consecutively | first_login |
| --------- | ----------------------- | ----------- |
| 1         | 1                       | 2016-03-01  |
| 2         | 0                       | 2017-06-25  |
| 3         | 0                       | 2016-03-01  |

---

## Group by `first_login`

### Install date `2016-03-01`

Players:

- 1 -> returned next day -> `1`
- 3 -> did not return next day -> `0`

Installs:

```text
2
```

Retention:

```text
(1 + 0) / 2 = 1 / 2 = 0.50
```

### Install date `2017-06-25`

Players:

- 2 -> did not return next day -> `0`

Installs:

```text
1
```

Retention:

```text
0 / 1 = 0.00
```

---

## Final result

| install_dt | installs | Day1_retention |
| ---------- | -------- | -------------- |
| 2016-03-01 | 2        | 0.50           |
| 2017-06-25 | 1        | 0.00           |

---

# Why the indicator-variable trick is powerful

The most elegant part of this solution is:

```sql
logged_in_consecutively = 0 or 1
```

Once you encode a yes/no condition as a numeric indicator:

- `SUM(...)` gives the number of successes
- `COUNT(...)` gives the total population
- dividing them gives the proportion

This is a very general SQL pattern for retention, conversion, and funnel problems.

---

# Why this is clearer than trying to compute the ratio directly

You could try to compute the numerator and denominator separately in more tangled subqueries.

But the CTE + indicator-variable method is cleaner because it creates a per-player intermediate representation first.

That makes the final aggregation almost self-explanatory.

---

# Important SQL concepts used here

## 1. `MIN(event_date)`

Finds the install date for each player.

## 2. `LEFT JOIN`

Preserves all installers, including those who did not return.

## 3. `CASE`

Converts existence of a next-day row into a 0/1 indicator.

## 4. `DATE_SUB(..., INTERVAL 1 DAY)`

Used to test whether a login happened exactly one day after installation.

## 5. `ROUND(..., 2)`

Formats retention to two decimal places as required.

---

# Complexity

Let `n` be the number of rows in `Activity`.

## Time Complexity

The query:

- groups activity rows by player to find first login
- joins first-logins back to activity
- aggregates by install date

A practical interview-style summary is:

```text
O(n)
```

plus grouping/join overhead depending on the database engine and indexing.

## Space Complexity

Additional space is used for:

- the first-logins CTE
- the consecutive-login-info CTE

So space is proportional to the number of distinct players.

---

# Key takeaways

1. First login date = install date.
2. Day 1 retention means returning exactly one day after install.
3. A `LEFT JOIN` is necessary so players who did not return are still counted.
4. A 0/1 indicator variable makes retention easy to aggregate.
5. `SUM(indicator) / COUNT(players)` is a clean way to compute retention ratios.

---

## Final accepted implementation

```sql
WITH first_logins AS (
  SELECT
    A.player_id,
    MIN(A.event_date) AS first_login
  FROM
    Activity A
  GROUP BY
    A.player_id
), consec_login_info AS (
  SELECT
    F.player_id,
    (CASE
      WHEN A.player_id IS NULL THEN 0
      ELSE 1
    END) AS logged_in_consecutively,
    F.first_login
  FROM
    first_logins F
    LEFT JOIN Activity A ON F.player_id = A.player_id
    AND F.first_login = DATE_SUB(A.event_date, INTERVAL 1 DAY)
)
SELECT
  C.first_login AS install_dt,
  COUNT(C.player_id) AS installs,
  ROUND(
    SUM(C.logged_in_consecutively)
    / COUNT(C.player_id)
  , 2) AS Day1_Retention
FROM
  consec_login_info C
GROUP BY
  C.first_login;
```
