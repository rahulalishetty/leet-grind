# 3060. User Activities within Time Bounds

## Approach: Window Functions

## Core idea

We need to find users who have **at least two sessions of the same type** such that the gap between them is **at most 12 hours**.

A session pair qualifies only if:

- both sessions belong to the **same user**
- both sessions have the **same `session_type`**
- the next session starts within **12 hours** after the current session ends

A natural way to solve this is:

1. sort sessions for each `(user_id, session_type)` group
2. look at the **next session** in that ordered sequence
3. compare the current session’s end with the next session’s start
4. keep users where at least one such pair has a gap of at most 12 hours

This is exactly what window functions are good at.

---

## Why a window function is a good fit

The problem is about comparing **adjacent sessions** within a user and type.

That means we do not want to join every row with every other row.
We only care about the **next** session in chronological order.

So the right tool is:

```sql
LEAD(...)
```

This lets us look one row ahead inside an ordered partition.

That makes the solution compact and efficient.

---

# Step 1: Partition sessions by user and session type

The solution groups data by:

```sql
PARTITION BY s1.user_id, s1.session_type
```

This means:

- sessions are separated by user
- and also separated by session type

So:

- a user's `Viewer` sessions are analyzed together
- a user's `Streamer` sessions are analyzed together
- but `Viewer` and `Streamer` sessions are not mixed

That is essential because the problem requires **two sessions of the same type**.

---

# Step 2: Order sessions by start time

Within each `(user_id, session_type)` group, sessions are ordered by:

```sql
ORDER BY s1.session_start
```

This ensures that for each row, the next row in the ordering is the next chronological session of the same type for that user.

That gives us the correct notion of "consecutive sessions" within that partition.

---

# Step 3: Use `LEAD` to fetch the next session start

The core expression is:

```sql
LEAD(s1.session_start) OVER(
  PARTITION BY s1.user_id,
  s1.session_type
  ORDER BY
    s1.session_start
) AS next_session_start
```

This adds a new column:

```text
next_session_start
```

which stores the start time of the next session of the same type for the same user.

If no next session exists, the value is `NULL`.

---

## Why this works

For every session row, we now know:

- current session end time
- next session start time

That is exactly what we need to compute the gap.

So the problem reduces to a simple timestamp difference check.

---

# Step 4: Compute the gap and filter to qualifying pairs

Once `next_session_start` is available, the query filters rows using:

```sql
TIMESTAMPDIFF(
  HOUR, a.session_end, a.next_session_start
) <= 12
```

This checks whether the next session begins within 12 hours after the current one ends.

The query also ensures the next session exists:

```sql
a.next_session_start IS NOT NULL
```

Together, these conditions identify valid qualifying session pairs.

---

# Step 5: Return distinct users

A user may have multiple qualifying pairs.

But the output only needs each user once.

So the final query uses:

```sql
SELECT DISTINCT a.user_id
```

That returns a unique list of users who satisfy the condition at least once.

---

## Final accepted query

```sql
SELECT
  DISTINCT a.user_id
FROM
  (
    SELECT
      s1.user_id,
      s1.session_type,
      s1.session_end,
      LEAD(s1.session_start) OVER(
        PARTITION BY s1.user_id,
        s1.session_type
        ORDER BY
          s1.session_start
      ) AS next_session_start
    FROM
      Sessions s1
  ) a
WHERE
  a.next_session_start IS NOT NULL
  AND TIMESTAMPDIFF(
    HOUR, a.session_end, a.next_session_start
  ) <= 12;
```

---

# Step-by-step explanation of the query

## Inner subquery

```sql
SELECT
  s1.user_id,
  s1.session_type,
  s1.session_end,
  LEAD(s1.session_start) OVER(
    PARTITION BY s1.user_id,
    s1.session_type
    ORDER BY
      s1.session_start
  ) AS next_session_start
FROM
  Sessions s1
```

This creates a session-level view where each session is paired with the next session of the same type for that user.

So each row now has:

- `user_id`
- `session_type`
- `session_end`
- `next_session_start`

---

## Outer filter

```sql
WHERE
  a.next_session_start IS NOT NULL
  AND TIMESTAMPDIFF(
    HOUR, a.session_end, a.next_session_start
  ) <= 12
```

This keeps only rows where:

- there is a next session
- the next session starts within 12 hours after the current session ends

---

## Final projection

```sql
SELECT DISTINCT a.user_id
```

This returns each qualifying user only once.

---

# Walkthrough on the sample

## Sample input

| user_id | session_start       | session_end         | session_id | session_type |
| ------: | ------------------- | ------------------- | ---------: | ------------ |
|     101 | 2023-11-01 08:00:00 | 2023-11-01 09:00:00 |          1 | Viewer       |
|     101 | 2023-11-01 10:00:00 | 2023-11-01 11:00:00 |          2 | Streamer     |
|     102 | 2023-11-01 13:00:00 | 2023-11-01 14:00:00 |          3 | Viewer       |
|     102 | 2023-11-01 15:00:00 | 2023-11-01 16:00:00 |          4 | Viewer       |
|     101 | 2023-11-02 09:00:00 | 2023-11-02 10:00:00 |          5 | Viewer       |
|     102 | 2023-11-02 12:00:00 | 2023-11-02 13:00:00 |          6 | Streamer     |
|     101 | 2023-11-02 13:00:00 | 2023-11-02 14:00:00 |          7 | Streamer     |
|     102 | 2023-11-02 16:00:00 | 2023-11-02 17:00:00 |          8 | Viewer       |
|     103 | 2023-11-01 08:00:00 | 2023-11-01 09:00:00 |          9 | Viewer       |
|     103 | 2023-11-02 20:00:00 | 2023-11-02 23:00:00 |         10 | Viewer       |
|     103 | 2023-11-03 09:00:00 | 2023-11-03 10:00:00 |         11 | Viewer       |

---

## User 101

### Viewer sessions

- session 1: 2023-11-01 08:00 to 09:00
- session 5: 2023-11-02 09:00 to 10:00

Gap from session 1 end to session 5 start:

```text
24 hours
```

Not within 12 hours.

### Streamer sessions

- session 2: 2023-11-01 10:00 to 11:00
- session 7: 2023-11-02 13:00 to 14:00

Gap:

```text
26 hours
```

Not within 12 hours.

So user `101` does not qualify.

---

## User 102

### Viewer sessions

- session 3: 13:00 to 14:00
- session 4: 15:00 to 16:00
- session 8: next day 16:00 to 17:00

Check adjacent pairs:

#### session 3 -> session 4

Gap:

```text
1 hour
```

This satisfies the condition.

So user `102` qualifies.

We do not need to continue checking further for final inclusion, though the query naturally does.

---

## User 103

### Viewer sessions

- session 9: 2023-11-01 08:00 to 09:00
- session 10: 2023-11-02 20:00 to 23:00
- session 11: 2023-11-03 09:00 to 10:00

Check adjacent pairs:

#### session 9 -> session 10

Gap:

```text
much more than 12 hours
```

Does not qualify.

#### session 10 -> session 11

Gap from session 10 end (23:00) to session 11 start (09:00 next day):

```text
10 hours
```

This is within 12 hours.

So user `103` qualifies.

---

## Final result

| user_id |
| ------: |
|     102 |
|     103 |

---

# Why comparing adjacent sessions is enough

A useful skeptical question is:

> what if a non-adjacent pair also has a gap <= 12 hours?

We do not need to compare all possible pairs.

If a user has at least two sessions of the same type within a maximum gap of 12 hours, then there must exist at least one **adjacent pair** in chronological order that satisfies the condition.

So checking consecutive sessions in sorted order is sufficient.

That is why `LEAD` is enough.

---

# Why the gap is computed from `session_end` to `next_session_start`

The problem speaks about the maximum gap **between sessions**.

So the correct gap is not between start times.
It is between:

- the end of the earlier session
- the start of the later session

That is why the query uses:

```sql
TIMESTAMPDIFF(HOUR, a.session_end, a.next_session_start)
```

This is an important detail.

---

# Important SQL concepts used here

## 1. `LEAD()`

Used to look at the next session’s start time.

## 2. Window partitioning

```sql
PARTITION BY user_id, session_type
```

Ensures only sessions of the same type for the same user are compared.

## 3. `TIMESTAMPDIFF()`

Used to compute the gap in hours between sessions.

## 4. `DISTINCT`

Ensures each qualifying user appears only once.

---

# Why this approach is elegant

The problem asks for a condition involving nearby events in time.

Window functions are designed for exactly this situation.

Instead of writing a complicated self-join across sessions, the query:

- sorts the sessions
- looks one row ahead
- performs one direct comparison

That makes the logic concise and easy to reason about.

---

# Complexity

Let `n` be the number of rows in `Sessions`.

## Time Complexity

The dominant work is sorting within the window partitions.

A practical summary is:

```text
O(n log n)
```

depending on how the SQL engine executes the window function.

## Space Complexity

Additional space is used for the intermediate result containing `next_session_start`, proportional to the number of session rows.

---

# Key takeaways

1. Only sessions of the same type and same user should be compared.
2. Sorting sessions by `session_start` lets us compare adjacent sessions.
3. `LEAD()` is the natural tool to fetch the next session.
4. The gap is measured from `session_end` to the next `session_start`.
5. A user qualifies if at least one such adjacent pair has a gap of at most 12 hours.

---

## Final accepted implementation

```sql
SELECT
  DISTINCT a.user_id
FROM
  (
    SELECT
      s1.user_id,
      s1.session_type,
      s1.session_end,
      LEAD(s1.session_start) OVER(
        PARTITION BY s1.user_id,
        s1.session_type
        ORDER BY
          s1.session_start
      ) AS next_session_start
    FROM
      Sessions s1
  ) a
WHERE
  a.next_session_start IS NOT NULL
  AND TIMESTAMPDIFF(
    HOUR, a.session_end, a.next_session_start
  ) <= 12;
```
