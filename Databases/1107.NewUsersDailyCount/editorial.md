# 1107. New Users Daily Count

## Detailed Summary of Two Accepted Approaches

We need to report, for each date, the number of users who **logged in for the first time on that date**, subject to this condition:

- the first login date must be within **at most 90 days from `2019-06-30`**

Important details:

- we only care about rows where `activity = 'login'`
- each user must be counted **only on their first-ever login date**
- dates with zero users are not included
- the table may contain duplicate rows, so we must stay careful about counting logic

This summary covers two approaches:

1. **Using aggregate functions**
2. **Using the `RANK()` window function**

---

# Core idea

For every user, we first need to determine:

```text
their earliest login date
```

Then we filter those first-login dates to keep only the ones within 90 days of:

```text
2019-06-30
```

Finally, we group by that first-login date and count users.

So the whole problem is really:

1. identify first login per user
2. keep only recent first logins
3. count users by date

---

# Approach 1: Using Aggregate Function

## Core idea

This approach uses:

- `WHERE activity = 'login'` to keep only login rows
- `MIN(activity_date)` to find each user’s first login date
- `HAVING` with `DATEDIFF()` to keep only users whose first login is within 90 days
- an outer query to count users by that first-login date

This is the most direct and natural solution.

---

## Step 1: Filter to login rows and find first login per user

The inner subquery is:

```sql
SELECT
    user_id,
    MIN(activity_date) AS login_date
FROM
    Traffic
WHERE
    activity = 'login'
GROUP BY
    user_id
HAVING
    DATEDIFF('2019-06-30', MIN(activity_date)) <= 90
```

---

## Why this works

### `WHERE activity = 'login'`

The problem is specifically about login activity.

So all other activity types such as:

- `logout`
- `jobs`
- `groups`
- `homepage`

must be ignored.

---

### `GROUP BY user_id`

We need one result per user, because we are trying to find each user’s first login date.

---

### `MIN(activity_date)`

Among a user’s login rows, the earliest date is their first login date.

That is exactly what the problem asks us to use.

---

### `HAVING DATEDIFF('2019-06-30', MIN(activity_date)) <= 90`

This keeps only users whose first login occurred within 90 days of `2019-06-30`.

The `HAVING` clause is used because the filter depends on an aggregate value:

```sql
MIN(activity_date)
```

---

## Example result of the inner subquery

Using the sample data, the qualified first-login rows are:

| user_id | login_date |
| ------: | ---------- |
|       1 | 2019-05-01 |
|       2 | 2019-06-21 |
|       4 | 2019-06-21 |

Why?

- user 1 first logged in on `2019-05-01` -> within 90 days
- user 2 first logged in on `2019-06-21` -> within 90 days
- user 4 first logged in on `2019-06-21` -> within 90 days
- user 3 first logged in on `2019-01-01` -> too old
- user 5 first logged in on `2019-03-01` -> too old

---

## Step 2: Count users by first-login date

Now that we have one qualifying first-login row per user, the final step is simply:

```sql
SELECT
  login_date,
  COUNT(DISTINCT user_id) AS user_count
FROM
  (
    SELECT
      user_id,
      MIN(activity_date) AS login_date
    FROM
      Traffic
    WHERE
      activity = 'login'
    GROUP BY
      user_id
    HAVING
      DATEDIFF(
        '2019-06-30',
        MIN(activity_date)
      ) <= 90
  ) t0
GROUP BY
  login_date
```

---

## Why this works

At this stage, each row in the subquery represents one user and that user’s first login date.

Grouping by `login_date` lets us count how many users first logged in on each date.

---

## Why `COUNT(DISTINCT user_id)` is used

The source solution uses:

```sql
COUNT(DISTINCT user_id)
```

This is a safe choice, especially because the original table may contain duplicate rows.

Even though the subquery already produces one row per user, using `DISTINCT` keeps the counting robust and explicit.

---

## Final query for Approach 1

```sql
SELECT
  login_date,
  COUNT(DISTINCT user_id) AS user_count
FROM
  (
    SELECT
      user_id,
      MIN(activity_date) AS login_date
    FROM
      Traffic
    WHERE
      activity = 'login'
    GROUP BY
      user_id
    HAVING
      DATEDIFF(
        '2019-06-30',
        MIN(activity_date)
      ) <= 90
  ) t0
GROUP BY
  login_date;
```

---

## Strengths of Approach 1

- very direct
- easy to read
- easy to explain
- efficient for this problem shape

### Tradeoff

- relies on grouped aggregation rather than row-level ranking
- not as flexible if you wanted more row-wise detail later

---

# Approach 2: Using the `RANK()` Window Function

## Core idea

This approach finds the first login per user by assigning a rank to each login date for that user.

The earliest login gets:

```text
rank = 1
```

Then we keep only rows with rank 1, apply the 90-day filter, and count users by date.

This is more “window-function style” than the aggregate solution.

---

## Step 1: Keep only login rows and rank them per user

The inner subquery is:

```sql
SELECT
  *,
  RANK() OVER (
    PARTITION BY user_id
    ORDER BY
      activity_date ASC
  ) AS rnk
FROM
  Traffic
WHERE
  activity = 'login'
```

---

## Why this works

### `WHERE activity = 'login'`

Same as before, we care only about login events.

---

### `PARTITION BY user_id`

This restarts the ranking separately for each user.

So every user gets their own ranked login history.

---

### `ORDER BY activity_date ASC`

This sorts login dates from earliest to latest.

So the first login date gets rank `1`.

---

## Example result of the ranked subquery

Using the sample, the inner query produces something like:

| user_id | activity | activity_date | rnk |
| ------: | -------- | ------------- | --: |
|       1 | login    | 2019-05-01    |   1 |
|       2 | login    | 2019-06-21    |   1 |
|       3 | login    | 2019-01-01    |   1 |
|       4 | login    | 2019-06-21    |   1 |
|       5 | login    | 2019-03-01    |   1 |
|       5 | login    | 2019-06-21    |   2 |

Interpretation:

- user 5 logged in twice
- the earlier one (`2019-03-01`) gets rank 1
- the later one (`2019-06-21`) gets rank 2

This is exactly what we need.

---

## Step 2: Keep only first login rows and apply the 90-day filter

The outer query is:

```sql
SELECT
  activity_date AS login_date,
  COUNT(DISTINCT user_id) AS user_count
FROM
  (
    SELECT
      *,
      RANK() OVER (
        PARTITION BY user_id
        ORDER BY
          activity_date ASC
      ) AS rnk
    FROM
      Traffic
    WHERE
      activity = 'login'
  ) t0
WHERE
  rnk = 1
  AND DATEDIFF('2019-06-30', activity_date) <= 90
GROUP BY
  activity_date
```

---

## Why this works

### `WHERE rnk = 1`

This keeps only the earliest login row for each user.

### `DATEDIFF('2019-06-30', activity_date) <= 90`

This keeps only those first-login dates that are within 90 days.

### `GROUP BY activity_date`

Now we count how many users had their first login on each qualifying date.

---

## Final query for Approach 2

```sql
SELECT
  activity_date AS login_date,
  COUNT(DISTINCT user_id) AS user_count
FROM
  (
    SELECT
      *,
      RANK() OVER (
        PARTITION BY user_id
        ORDER BY
          activity_date ASC
      ) AS rnk
    FROM
      Traffic
    WHERE
      activity = 'login'
  ) t0
WHERE
  rnk = 1
  AND DATEDIFF('2019-06-30', activity_date) <= 90
GROUP BY
  activity_date;
```

---

## Strengths of Approach 2

- elegant use of window functions
- useful if you want row-level ranking logic
- easy to extend if the problem later asks for second login, third login, etc.

### Tradeoff

- a bit more advanced than the aggregate version
- may be less intuitive if all you need is the minimum date

---

# Walkthrough on the sample

## Input

| user_id | activity | activity_date |
| ------: | -------- | ------------- |
|       1 | login    | 2019-05-01    |
|       1 | homepage | 2019-05-01    |
|       1 | logout   | 2019-05-01    |
|       2 | login    | 2019-06-21    |
|       2 | logout   | 2019-06-21    |
|       3 | login    | 2019-01-01    |
|       3 | jobs     | 2019-01-01    |
|       3 | logout   | 2019-01-01    |
|       4 | login    | 2019-06-21    |
|       4 | groups   | 2019-06-21    |
|       4 | logout   | 2019-06-21    |
|       5 | login    | 2019-03-01    |
|       5 | logout   | 2019-03-01    |
|       5 | login    | 2019-06-21    |
|       5 | logout   | 2019-06-21    |

---

## Step 1: Consider only login rows

Relevant rows:

| user_id | activity_date |
| ------: | ------------- |
|       1 | 2019-05-01    |
|       2 | 2019-06-21    |
|       3 | 2019-01-01    |
|       4 | 2019-06-21    |
|       5 | 2019-03-01    |
|       5 | 2019-06-21    |

---

## Step 2: Find each user’s first login

### User 1

First login:

```text
2019-05-01
```

### User 2

First login:

```text
2019-06-21
```

### User 3

First login:

```text
2019-01-01
```

### User 4

First login:

```text
2019-06-21
```

### User 5

First login:

```text
2019-03-01
```

---

## Step 3: Keep only first logins within 90 days of 2019-06-30

Qualifying users:

- user 1 -> `2019-05-01`
- user 2 -> `2019-06-21`
- user 4 -> `2019-06-21`

Non-qualifying users:

- user 3 -> first login too old
- user 5 -> first login too old

---

## Step 4: Count by date

### `2019-05-01`

Users:

```text
1
```

Count:

```text
1
```

### `2019-06-21`

Users:

```text
2, 4
```

Count:

```text
2
```

---

## Final output

| login_date | user_count |
| ---------- | ---------- |
| 2019-05-01 | 1          |
| 2019-06-21 | 2          |

That matches the expected answer.

---

# Why user 5 is not counted on 2019-06-21

This is the key conceptual trap in the problem.

User 5 has a login on `2019-06-21`, but that is **not** their first login.

Their first login was:

```text
2019-03-01
```

Since that first login date is older than 90 days from `2019-06-30`, user 5 is excluded entirely.

So we are counting users by the date of **their first-ever login**, not by any login in the last 90 days.

---

# Why only non-zero dates appear

The problem explicitly says:

> we only care about dates with non zero user count

So neither approach generates missing dates or fills zeros.

They simply return the qualifying dates that actually have users.

---

# Important SQL concepts used here

## 1. `MIN(activity_date)`

Used in Approach 1 to find first login per user.

## 2. `HAVING`

Used in Approach 1 because the filter depends on the aggregated minimum date.

## 3. `RANK()`

Used in Approach 2 to label each user’s earliest login row.

## 4. `DATEDIFF()`

Used to check whether a first login date is within 90 days of `2019-06-30`.

## 5. `COUNT(DISTINCT user_id)`

Used to count users safely by first-login date.

---

# Comparing the two approaches

## Approach 1: Aggregate function

### Best when

- you want the simplest solution
- you only need the earliest date per user

### Pros

- straightforward
- highly readable
- natural use of grouping

### Cons

- less row-oriented if you later want detailed ranking info

---

## Approach 2: `RANK()` window function

### Best when

- you prefer analytic-function style
- you may want to extend the logic to second or third login later

### Pros

- elegant
- flexible
- useful for row-level analysis

### Cons

- slightly more advanced than necessary for this exact problem

---

# Key takeaways

1. Only `login` rows matter.
2. Each user must be counted only on their **first-ever** login date.
3. Then apply the 90-day filter to that first login date.
4. Finally, group by the qualifying first-login dates and count users.
5. Approach 1 is the simplest direct solution; Approach 2 is a clean window-function alternative.

---

## Final accepted implementations

### Approach 1: Using Aggregate Function

```sql
SELECT
  login_date,
  COUNT(DISTINCT user_id) AS user_count
FROM
  (
    SELECT
      user_id,
      MIN(activity_date) AS login_date
    FROM
      Traffic
    WHERE
      activity = 'login'
    GROUP BY
      user_id
    HAVING
      DATEDIFF(
        '2019-06-30',
        MIN(activity_date)
      ) <= 90
  ) t0
GROUP BY
  login_date;
```

### Approach 2: Using the `RANK()` Window Function

```sql
SELECT
  activity_date AS login_date,
  COUNT(DISTINCT user_id) AS user_count
FROM
  (
    SELECT
      *,
      RANK() OVER (
        PARTITION BY user_id
        ORDER BY
          activity_date ASC
      ) AS rnk
    FROM
      Traffic
    WHERE
      activity = 'login'
  ) t0
WHERE
  rnk = 1
  AND DATEDIFF('2019-06-30', activity_date) <= 90
GROUP BY
  activity_date;
```
