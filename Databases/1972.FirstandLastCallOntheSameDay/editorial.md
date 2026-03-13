# 1972. First and Last Call On the Same Day

## Approach: Dual-Ranking Unified Call Analysis

## Core idea

We need to report the users whose **first** and **last** calls on **any given day** were with the **same person**.

A call must count for a user whether they were:

- the **caller**
- or the **recipient**

So the clean strategy is:

1. create a **unified call view** where every call is represented from each participant’s perspective
2. rank each user’s calls per day in two directions:
   - earliest first
   - latest first
3. keep only the first and last calls of each day
4. check whether those calls involved the **same other participant**

This solution uses:

- **CTEs** for structure
- **window functions** for ranking
- **grouping + HAVING** to verify whether first and last call partner match

---

## Why a unified call table is necessary

The raw `Calls` table stores two roles:

- `caller_id`
- `recipient_id`

But the problem says:

> calls are counted regardless of being the caller or the recipient

So if we want to analyze a user’s daily calls, we should not care whether the user appeared in the caller column or the recipient column.

That leads to the first key transformation:

- rewrite every call as a generic user participation record

This gives us a table with columns like:

- `user_id`
- `call_time`
- `other_participant_id`

Once that is done, the rest of the problem becomes much simpler.

---

# Step 1: Create a unified view of calls

```sql
WITH UnifiedCalls AS (
  SELECT
    caller_id AS user_id,
    call_time,
    recipient_id AS other_participant_id
  FROM
    Calls

  UNION

  SELECT
    recipient_id AS user_id,
    call_time,
    caller_id AS other_participant_id
  FROM
    Calls
)
```

---

## Why this works

Every original call:

```text
caller_id -> recipient_id at call_time
```

is transformed into **two rows**:

1. from the caller’s perspective
2. from the recipient’s perspective

So if the original call was:

```text
8 calls 4 at 2021-08-24 17:46:07
```

the unified view contains:

| user_id | call_time           | other_participant_id |
| ------: | ------------------- | -------------------: |
|       8 | 2021-08-24 17:46:07 |                    4 |
|       4 | 2021-08-24 17:46:07 |                    8 |

That means every user’s calls can now be analyzed in exactly the same way.

---

## Why `UNION` is used here

The provided solution uses:

```sql
UNION
```

rather than `UNION ALL`.

That means duplicate rows would be removed if they happened to appear.

Given the table’s primary key `(caller_id, recipient_id, call_time)`, exact duplicates in the original table are not possible, and the transformed rows are intended to represent one participation record per user per call.

So this works correctly under the problem constraints.

---

# Step 2: Rank each user’s calls per day in both directions

Now that all calls are represented uniformly, we need to identify:

- the **earliest call** of the day
- the **latest call** of the day

for each user.

The solution uses `DENSE_RANK()` twice:

1. ascending by `call_time` to mark earliest calls
2. descending by `call_time` to mark latest calls

```sql
RankedCalls AS (
  SELECT
    user_id,
    other_participant_id,
    DATE(call_time) AS call_date,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time ASC
    ) AS rank_earliest_call,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time DESC
    ) AS rank_latest_call
  FROM
    UnifiedCalls
)
```

---

## Why partition by `user_id` and `DATE(call_time)`

We are not looking for a user’s first and last calls overall.

We are looking for:

- first call **of each day**
- last call **of each day**

So the ranking must restart for every:

- `user_id`
- `call_date`

That is exactly what this partition does:

```sql
PARTITION BY user_id, DATE(call_time)
```

---

## Why `DATE(call_time)` is extracted

`call_time` is a datetime, which includes both date and time.

But the problem is day-based.

So we isolate just the date part:

```sql
DATE(call_time) AS call_date
```

This allows calls to be grouped and ranked within each calendar day.

---

## Why `DENSE_RANK()` works

### Earliest rank

```sql
DENSE_RANK() OVER (
  PARTITION BY user_id, DATE(call_time)
  ORDER BY call_time ASC
)
```

The earliest call of the day gets rank `1`.

### Latest rank

```sql
DENSE_RANK() OVER (
  PARTITION BY user_id, DATE(call_time)
  ORDER BY call_time DESC
)
```

The latest call of the day gets rank `1`.

So after this step, for any given user-day pair:

- rows with `rank_earliest_call = 1` are earliest calls
- rows with `rank_latest_call = 1` are latest calls

---

# Step 3: Keep only first and last calls and check whether the partner is the same

Now we want only the rows that are either:

- first call of the day
- or last call of the day

So we filter:

```sql
WHERE
  rank_earliest_call = 1
  OR rank_latest_call = 1
```

Then, for each `(user_id, call_date)` group, we count how many distinct `other_participant_id` values remain.

If that count is `1`, it means:

- the first call partner
- and the last call partner

are the same person.

That is exactly the condition required by the problem.

---

## Final accepted query

```sql
WITH UnifiedCalls AS (
  SELECT
    caller_id AS user_id,
    call_time,
    recipient_id AS other_participant_id
  FROM
    Calls

  UNION

  SELECT
    recipient_id AS user_id,
    call_time,
    caller_id AS other_participant_id
  FROM
    Calls
),
RankedCalls AS (
  SELECT
    user_id,
    other_participant_id,
    DATE(call_time) AS call_date,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time ASC
    ) AS rank_earliest_call,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time DESC
    ) AS rank_latest_call
  FROM
    UnifiedCalls
)
SELECT
  DISTINCT user_id
FROM
  RankedCalls
WHERE
  rank_earliest_call = 1
  OR rank_latest_call = 1
GROUP BY
  user_id,
  call_date
HAVING
  COUNT(DISTINCT other_participant_id) = 1;
```

---

# Step-by-step explanation of the final query

## `UnifiedCalls` CTE

```sql
WITH UnifiedCalls AS (
  SELECT
    caller_id AS user_id,
    call_time,
    recipient_id AS other_participant_id
  FROM
    Calls

  UNION

  SELECT
    recipient_id AS user_id,
    call_time,
    caller_id AS other_participant_id
  FROM
    Calls
)
```

This converts every call into a generic user-participation format.

---

## `RankedCalls` CTE

```sql
RankedCalls AS (
  SELECT
    user_id,
    other_participant_id,
    DATE(call_time) AS call_date,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time ASC
    ) AS rank_earliest_call,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time DESC
    ) AS rank_latest_call
  FROM
    UnifiedCalls
)
```

This assigns two independent rankings per user-day:

- earliest-call ranking
- latest-call ranking

---

## Final selection

```sql
SELECT DISTINCT user_id
FROM RankedCalls
WHERE rank_earliest_call = 1
   OR rank_latest_call = 1
GROUP BY user_id, call_date
HAVING COUNT(DISTINCT other_participant_id) = 1;
```

This:

1. keeps only first and last calls
2. groups by user and date
3. checks whether those calls involved only one distinct partner
4. returns the users who satisfy the condition on at least one day

---

# Walkthrough on the sample

## Sample input

| caller_id | recipient_id | call_time           |
| --------: | -----------: | ------------------- |
|         8 |            4 | 2021-08-24 17:46:07 |
|         4 |            8 | 2021-08-24 19:57:13 |
|         5 |            1 | 2021-08-11 05:28:44 |
|         8 |            3 | 2021-08-17 04:04:15 |
|        11 |            3 | 2021-08-17 13:07:00 |
|         8 |           11 | 2021-08-17 22:22:22 |

---

## UnifiedCalls result conceptually

Every row appears twice, one for each participant.

For example:

### Call: `8 -> 4` on `2021-08-24 17:46:07`

Becomes:

| user_id | call_time           | other_participant_id |
| ------: | ------------------- | -------------------: |
|       8 | 2021-08-24 17:46:07 |                    4 |
|       4 | 2021-08-24 17:46:07 |                    8 |

Do this for all calls, and every user gets a personal call history.

---

## Analyze day `2021-08-24`

Calls involving user 8 on that day:

- 17:46 with 4
- 19:57 with 4

So:

- first call partner = 4
- last call partner = 4

Distinct partner count among first/last calls = `1`

So user `8` qualifies.

Similarly for user `4`:

- first call partner = 8
- last call partner = 8

So user `4` also qualifies.

---

## Analyze day `2021-08-11`

Only one call exists:

- `5 <-> 1` at 05:28

For user `5`:

- first call = last call = with `1`

For user `1`:

- first call = last call = with `5`

So both users qualify.

---

## Analyze day `2021-08-17`

For user `8`, the calls are:

- 04:04 with 3
- 22:22 with 11

Different people.

So `COUNT(DISTINCT other_participant_id)` among first/last rows = `2`.

User `8` does **not** qualify for this day.

The same mismatch happens for users `3` and `11`.

So none of them qualify on `2021-08-17`.

---

## Final result

Users who qualify on **at least one day**:

| user_id |
| ------: |
|       1 |
|       4 |
|       5 |
|       8 |

---

# Why the `HAVING COUNT(DISTINCT other_participant_id) = 1` test is the key

This is the most elegant part of the solution.

After filtering down to only first and last calls of the day, the problem becomes:

> do those rows involve only one distinct other person?

If yes, then the first and last calls were with the same person.

So instead of explicitly comparing two rows, we rely on grouping and distinct-count logic.

That is a compact and clever SQL pattern.

---

# Why `DISTINCT user_id` is used in the outer select

A user may satisfy the condition on multiple different days.

But the output only wants each qualifying user id once.

So:

```sql
SELECT DISTINCT user_id
```

ensures uniqueness in the final result.

---

# Important SQL concepts used here

## 1. CTE

Used to break the problem into readable stages.

## 2. `UNION`

Used to convert caller/recipient roles into a unified participant-centric view.

## 3. `DATE(call_time)`

Used to extract the date portion for daily analysis.

## 4. `DENSE_RANK()`

Used twice to find earliest and latest calls per user per day.

## 5. `HAVING COUNT(DISTINCT ...) = 1`

Used to test whether first and last call partner are the same.

---

# Why this approach is elegant

The challenge in this problem is that each call involves two roles, and the condition is day-based and position-based.

This solution handles that elegantly by:

- normalizing the call representation first
- ranking independently for earliest and latest
- checking sameness through grouping

That avoids messy self-joins or manual earliest/latest comparisons.

---

# Complexity

Let `n` be the number of rows in `Calls`.

## Time Complexity

The solution:

- expands calls into roughly `2n` unified rows
- ranks them by user/day
- groups filtered rows by user/day

A practical summary is:

```text
O(n log n)
```

because the window-function ordering typically dominates.

## Space Complexity

Additional space is used for:

- the unified calls CTE
- the ranked calls CTE

So auxiliary space is proportional to the expanded intermediate data size.

---

# Key takeaways

1. Convert each call into a participant-centric representation first.
2. Rank calls per user per day in both ascending and descending order.
3. Keep only first and last calls.
4. Group by user and date.
5. If the number of distinct call partners among those rows is `1`, the user qualifies.
6. Return each qualifying user only once.

---

## Final accepted implementation

```sql
WITH UnifiedCalls AS (
  SELECT
    caller_id AS user_id,
    call_time,
    recipient_id AS other_participant_id
  FROM
    Calls

  UNION

  SELECT
    recipient_id AS user_id,
    call_time,
    caller_id AS other_participant_id
  FROM
    Calls
),
RankedCalls AS (
  SELECT
    user_id,
    other_participant_id,
    DATE(call_time) AS call_date,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time ASC
    ) AS rank_earliest_call,
    DENSE_RANK() OVER (
      PARTITION BY user_id, DATE(call_time)
      ORDER BY call_time DESC
    ) AS rank_latest_call
  FROM
    UnifiedCalls
)
SELECT
  DISTINCT user_id
FROM
  RankedCalls
WHERE
  rank_earliest_call = 1
  OR rank_latest_call = 1
GROUP BY
  user_id,
  call_date
HAVING
  COUNT(DISTINCT other_participant_id) = 1;
```
