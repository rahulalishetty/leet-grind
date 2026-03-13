# 2720. Popularity Percentage

## Approach: Bidirectional Friendship Popularity Analysis

## Core idea

We need to compute, for each user:

```text
(number of friends / total number of users) * 100
```

and round the result to **2 decimal places**.

The important challenge is that the `Friends` table stores each friendship only once, as a pair:

```text
(user1, user2)
```

But friendship is **bidirectional**.

So if `(2, 1)` appears, then:

- user `2` is a friend of user `1`
- user `1` is also a friend of user `2`

That means we first need to expand the friendship data so that every friendship is visible from **both users’ perspectives**.

Once we do that, the rest becomes:

1. count how many unique friends each user has
2. divide by total number of users
3. multiply by `100`
4. round to `2` decimals

---

## Why friendship expansion is necessary

The original table gives rows like:

| user1 | user2 |
| ----: | ----: |
|     2 |     1 |
|     1 |     3 |

If we only count from the `user1` column, then:

- user `1` would appear to have friend `3`
- but user `1` would incorrectly miss friend `2`

That would undercount friendships.

So we must explicitly represent each friendship in both directions.

---

# Step 1: Expand friendships in both directions

```sql
WITH FriendshipsExpanded AS (
  SELECT
    user1,
    user2
  FROM
    Friends

  UNION

  SELECT
    user2,
    user1
  FROM
    Friends
)
```

---

## Why this works

This CTE creates a symmetric view of the friendship graph.

For every original row:

```text
(A, B)
```

it produces:

- `(A, B)`
- `(B, A)`

So every user gets a row for each friend they are connected to.

---

## Example of expansion

Suppose the original table contains:

| user1 | user2 |
| ----: | ----: |
|     2 |     1 |
|     1 |     3 |

After expansion, the CTE becomes:

| user1 | user2 |
| ----: | ----: |
|     2 |     1 |
|     1 |     3 |
|     1 |     2 |
|     3 |     1 |

Now it is easy to interpret:

- user `1` has friends `2` and `3`
- user `2` has friend `1`
- user `3` has friend `1`

That is exactly what we need.

---

## Why `UNION` is used

The solution uses:

```sql
UNION
```

instead of `UNION ALL`.

That means if the same friendship row would somehow appear twice after expansion, duplicates are removed.

Given the problem constraints, this is safe and helps ensure the friendship list remains clean.

---

# Step 2: Group by user and count unique friends

Now that every friendship is represented from each user’s point of view, we can compute the number of friends per user using:

```sql
COUNT(DISTINCT user2)
```

because:

- `user1` is the current user
- `user2` is the friend

So for each `user1`, we count how many distinct `user2` values they are connected to.

---

# Step 3: Compute the total number of users

The popularity formula divides by:

```text
total number of users on the platform
```

In this solution, after expansion and grouping, the total user count is obtained using:

```sql
COUNT(user1) OVER ()
```

Because the final grouped query has one row per user, this window function counts how many users appear in the result.

That gives the total number of users in the network.

---

# Step 4: Compute percentage popularity

The full formula becomes:

```sql
ROUND(
  100 * COUNT(DISTINCT user2) / COUNT(user1) OVER (),
  2
) AS percentage_popularity
```

This does exactly what the problem asks:

1. count number of friends
2. divide by total number of users
3. multiply by 100
4. round to 2 decimals

---

## Final accepted query

```sql
WITH FriendshipsExpanded AS (
  SELECT
    user1,
    user2
  FROM
    Friends
  UNION
  SELECT
    user2,
    user1
  FROM
    Friends
)
SELECT
  user1,
  ROUND(
    100 * COUNT(DISTINCT user2) / COUNT(user1) OVER (),
    2
  ) AS percentage_popularity
FROM
  FriendshipsExpanded
GROUP BY
  user1
ORDER BY
  user1;
```

---

# Step-by-step explanation of the final query

## `FriendshipsExpanded` CTE

```sql
WITH FriendshipsExpanded AS (
  SELECT user1, user2 FROM Friends
  UNION
  SELECT user2, user1 FROM Friends
)
```

This builds a bidirectional friendship view.

---

## Outer `SELECT`

```sql
SELECT
  user1,
  ROUND(
    100 * COUNT(DISTINCT user2) / COUNT(user1) OVER (),
    2
  ) AS percentage_popularity
```

This:

- groups by `user1`
- counts how many distinct friends each user has
- divides by total number of users
- converts to percentage
- rounds to 2 decimals

---

## `GROUP BY user1`

```sql
GROUP BY user1
```

This ensures one output row per user.

---

## `ORDER BY user1`

```sql
ORDER BY user1
```

This sorts the output in ascending user order, as required.

---

# Walkthrough on the sample

## Input

| user1 | user2 |
| ----: | ----: |
|     2 |     1 |
|     1 |     3 |
|     4 |     1 |
|     1 |     5 |
|     1 |     6 |
|     2 |     6 |
|     7 |     2 |
|     8 |     3 |
|     3 |     9 |

---

## Expanded friendship view

After bidirectional expansion:

| user1 | user2 |
| ----: | ----: |
|     2 |     1 |
|     1 |     2 |
|     1 |     3 |
|     3 |     1 |
|     4 |     1 |
|     1 |     4 |
|     1 |     5 |
|     5 |     1 |
|     1 |     6 |
|     6 |     1 |
|     2 |     6 |
|     6 |     2 |
|     7 |     2 |
|     2 |     7 |
|     8 |     3 |
|     3 |     8 |
|     3 |     9 |
|     9 |     3 |

Now each user’s friends are easy to count.

---

## Count friends per user

### User 1

Friends:

```text
2, 3, 4, 5, 6
```

Count:

```text
5
```

### User 2

Friends:

```text
1, 6, 7
```

Count:

```text
3
```

### User 3

Friends:

```text
1, 8, 9
```

Count:

```text
3
```

### User 4

Friends:

```text
1
```

Count:

```text
1
```

### User 5

Friends:

```text
1
```

Count:

```text
1
```

### User 6

Friends:

```text
1, 2
```

Count:

```text
2
```

### User 7

Friends:

```text
2
```

Count:

```text
1
```

### User 8

Friends:

```text
3
```

Count:

```text
1
```

### User 9

Friends:

```text
3
```

Count:

```text
1
```

---

## Total number of users

There are `9` users total:

```text
1, 2, 3, 4, 5, 6, 7, 8, 9
```

So the denominator is:

```text
9
```

---

## Compute percentage popularity

### User 1

```text
(5 / 9) * 100 = 55.56
```

### User 2

```text
(3 / 9) * 100 = 33.33
```

### User 3

```text
(3 / 9) * 100 = 33.33
```

### User 4

```text
(1 / 9) * 100 = 11.11
```

### User 5

```text
(1 / 9) * 100 = 11.11
```

### User 6

```text
(2 / 9) * 100 = 22.22
```

### User 7

```text
(1 / 9) * 100 = 11.11
```

### User 8

```text
(1 / 9) * 100 = 11.11
```

### User 9

```text
(1 / 9) * 100 = 11.11
```

---

## Final result

| user1 | percentage_popularity |
| ----: | --------------------: |
|     1 |                 55.56 |
|     2 |                 33.33 |
|     3 |                 33.33 |
|     4 |                 11.11 |
|     5 |                 11.11 |
|     6 |                 22.22 |
|     7 |                 11.11 |
|     8 |                 11.11 |
|     9 |                 11.11 |

---

# Why this approach is elegant

The key difficulty in this problem is that friendships are stored in an undirected way, but SQL rows are directional.

This solution handles that perfectly by first normalizing the data into a bidirectional representation.

Once that is done, the popularity calculation becomes a straightforward grouped aggregation.

That separation of concerns is what makes the query clean.

---

# Important SQL concepts used here

## 1. `UNION`

Used to expand each friendship into both directions.

## 2. `COUNT(DISTINCT user2)`

Used to count unique friends per user.

## 3. `COUNT(user1) OVER ()`

Used to compute the total number of users in the final grouped result.

## 4. `ROUND(..., 2)`

Used to format the popularity percentage to two decimals.

---

# Why the denominator is total users, not total friendships

A common mistake would be to divide by the number of friendships or friendship rows.

But the problem defines popularity percentage as:

```text
total number of friends / total number of users on the platform
```

So the denominator must be the number of users, not the number of edges/friendships.

This solution gets that right by counting grouped users.

---

# Complexity

Let `n` be the number of rows in `Friends`.

## Time Complexity

The solution:

- expands friendships into roughly `2n` rows
- groups by user
- counts distinct friends

A practical summary is:

```text
O(n log n)
```

depending on how the database executes grouping and distinct counting.

## Space Complexity

Additional space is used for the expanded friendship CTE, proportional to the number of friendship rows after expansion.

---

# Key takeaways

1. Friendships are bidirectional, so the table must first be expanded in both directions.
2. After expansion, each user’s number of distinct friends can be counted directly.
3. Popularity percentage is:
   - `friends / total_users * 100`
4. The result must be rounded to 2 decimals and ordered by user id.

---

## Final accepted implementation

```sql
WITH FriendshipsExpanded AS (
  -- Expanding friendships to account for both user1 and user2 as primary users
  SELECT
    user1,
    user2
  FROM
    Friends
  UNION
  SELECT
    user2,
    user1
  FROM
    Friends
)
SELECT
  user1,
  ROUND(
    100 * COUNT(DISTINCT user2) / COUNT(user1) OVER (),
    2
  ) AS percentage_popularity
FROM
  FriendshipsExpanded
GROUP BY
  user1
ORDER BY
  user1;
```
