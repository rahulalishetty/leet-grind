# 597. Friend Requests I: Overall Acceptance Rate

## Approach 1: Using `ROUND` and `IFNULL`

## Core idea

The acceptance rate is defined as:

```text
accept_rate = number of accepted requests / number of requests
```

But there are two important details in this problem:

1. We must count **unique** requests and **unique** accepted requests, because duplicates should count only once.
2. If there are **no requests at all**, we must return `0.00` instead of causing a division-by-zero issue.

So the overall plan is:

1. count distinct accepted friend pairs from `RequestAccepted`
2. count distinct request pairs from `FriendRequest`
3. divide the first count by the second
4. protect against the case where the denominator is zero
5. round the final answer to 2 decimal places

---

## Step 1: Count distinct accepted requests

The `RequestAccepted` table may contain duplicates.

So we do **not** want:

```sql
SELECT COUNT(*)
FROM RequestAccepted;
```

because that would count repeated `(requester_id, accepter_id)` pairs more than once.

Instead, we first remove duplicates and then count the remaining rows.

```sql
SELECT COUNT(*)
FROM (
    SELECT DISTINCT requester_id, accepter_id
    FROM RequestAccepted
) AS A;
```

### Why this works

- `SELECT DISTINCT requester_id, accepter_id` keeps each accepted friendship pair only once
- the outer `COUNT(*)` counts how many unique accepted pairs remain

---

## Step 2: Count distinct requests

The same issue exists in `FriendRequest`.

A sender may send multiple requests to the same receiver, but duplicates should only count once.

So again, we remove duplicates first:

```sql
SELECT COUNT(*)
FROM (
    SELECT DISTINCT sender_id, send_to_id
    FROM FriendRequest
) AS B;
```

### Why this works

- `SELECT DISTINCT sender_id, send_to_id` keeps each request pair only once
- the outer `COUNT(*)` gives the number of unique requests

---

## Step 3: Divide accepted count by request count

Once we have both counts, the acceptance rate is:

```text
distinct accepted requests / distinct total requests
```

So conceptually:

```sql
accepted_count / request_count
```

---

## Step 4: Handle the zero-request case

This is the subtle part.

If the `FriendRequest` table is empty, then the number of requests is zero.

That means the denominator becomes zero, and direct division would be invalid.

So we need a fallback value of `0`.

This approach uses `IFNULL(...)` to handle that special case.

---

## Step 5: Round the result

The problem requires the result to be rounded to **2 decimal places**.

So we wrap the final result in:

```sql
ROUND(..., 2)
```

---

## Final accepted query

```sql
SELECT
ROUND(
    IFNULL(
        (SELECT COUNT(*)
         FROM (
             SELECT DISTINCT requester_id, accepter_id
             FROM RequestAccepted
         ) AS A)
        /
        (SELECT COUNT(*)
         FROM (
             SELECT DISTINCT sender_id, send_to_id
             FROM FriendRequest
         ) AS B),
        0
    ),
    2
) AS accept_rate;
```

---

## Full explanation of the final query

### Numerator

```sql
(SELECT COUNT(*)
 FROM (
     SELECT DISTINCT requester_id, accepter_id
     FROM RequestAccepted
 ) AS A)
```

This gives the number of unique accepted requests.

### Denominator

```sql
(SELECT COUNT(*)
 FROM (
     SELECT DISTINCT sender_id, send_to_id
     FROM FriendRequest
 ) AS B)
```

This gives the number of unique friend requests.

### Division

```sql
numerator / denominator
```

This computes the overall acceptance rate.

### `IFNULL(..., 0)`

If the division result is `NULL`, return `0` instead.

### `ROUND(..., 2)`

Round the final result to 2 decimal places.

---

## Example walkthrough

### FriendRequest table

| sender_id | send_to_id | request_date |
| --------- | ---------- | ------------ |
| 1         | 2          | 2016/06/01   |
| 1         | 3          | 2016/06/01   |
| 1         | 4          | 2016/06/01   |
| 2         | 3          | 2016/06/02   |
| 3         | 4          | 2016/06/09   |

Distinct `(sender_id, send_to_id)` pairs are:

```text
(1,2)
(1,3)
(1,4)
(2,3)
(3,4)
```

So total distinct requests = `5`.

---

### RequestAccepted table

| requester_id | accepter_id | accept_date |
| ------------ | ----------- | ----------- |
| 1            | 2           | 2016/06/03  |
| 1            | 3           | 2016/06/08  |
| 2            | 3           | 2016/06/08  |
| 3            | 4           | 2016/06/09  |
| 3            | 4           | 2016/06/10  |

Distinct `(requester_id, accepter_id)` pairs are:

```text
(1,2)
(1,3)
(2,3)
(3,4)
```

So total distinct accepted requests = `4`.

---

## Compute the final rate

```text
accept_rate = 4 / 5 = 0.8
```

Rounded to 2 decimals:

```text
0.80
```

LeetCode may display it as `0.8`, but logically the rounded value is `0.80`.

---

## Why `DISTINCT` is necessary

Without `DISTINCT`, duplicates would inflate the counts.

For example, in `RequestAccepted`, the pair `(3,4)` appears twice:

- 2016/06/09
- 2016/06/10

But the problem says duplicated acceptances should count only once.

So this would be wrong:

```sql
SELECT COUNT(*)
FROM RequestAccepted;
```

because it would count both rows.

That is why we must use:

```sql
SELECT DISTINCT requester_id, accepter_id
```

before counting.

The same logic applies to `FriendRequest`.

---

## Why `IFNULL` is needed

Suppose the `FriendRequest` table is empty.

Then:

```sql
SELECT COUNT(*)
FROM (
    SELECT DISTINCT sender_id, send_to_id
    FROM FriendRequest
) AS B;
```

would return `0`.

Now the expression becomes:

```text
accepted_count / 0
```

which is not a valid acceptance rate.

The problem explicitly says:

> If there are no requests at all, return 0.00

So we need a safeguard.

This solution uses `IFNULL(..., 0)` to ensure the final answer becomes `0` instead of failing logically.

---

## Important note on behavior

A careful SQL reader may notice that `IFNULL` here depends on how the SQL engine treats division with a zero denominator.

In practical MySQL-style reasoning used by this solution, the intent is:

- if the division result becomes `NULL`
- replace it with `0`

That matches the accepted LeetCode-style approach.

A more defensive real-world pattern often uses `NULLIF` in the denominator, such as:

```sql
accepted_count / NULLIF(request_count, 0)
```

but your provided approach specifically uses `IFNULL`, so this summary preserves that exact method.

---

## Equivalent expanded version

To make the structure easier to read, here is the same logic laid out more visibly:

```sql
SELECT
    ROUND(
        IFNULL(
            (
                SELECT COUNT(*)
                FROM (
                    SELECT DISTINCT requester_id, accepter_id
                    FROM RequestAccepted
                ) AS accepted_pairs
            )
            /
            (
                SELECT COUNT(*)
                FROM (
                    SELECT DISTINCT sender_id, send_to_id
                    FROM FriendRequest
                ) AS request_pairs
            ),
            0
        ),
        2
    ) AS accept_rate;
```

Same logic, just clearer aliases.

---

## Wrong approach example

This would be wrong:

```sql
SELECT COUNT(*) / COUNT(*)
FROM FriendRequest, RequestAccepted;
```

Why?

- it ignores duplicates
- it does not independently count unique pairs
- it creates meaningless multiplication effects if written as a cross join
- it does not follow the problem definition

Another wrong version is:

```sql
SELECT
    ROUND(
        (SELECT COUNT(*) FROM RequestAccepted) /
        (SELECT COUNT(*) FROM FriendRequest),
        2
    ) AS accept_rate;
```

Why wrong?

- duplicate requests would be counted more than once
- duplicate acceptances would also be counted more than once

So `DISTINCT` is essential.

---

## Complexity

Let:

- `n` = number of rows in `FriendRequest`
- `m` = number of rows in `RequestAccepted`

### Time Complexity

The database must scan both tables and deduplicate the relevant pairs.

A practical summary is:

```text
O(n + m)
```

plus the internal cost of deduplication, depending on implementation.

### Space Complexity

Additional space is needed for distinct pair tracking:

```text
O(n + m)
```

in the worst case, depending on how the engine performs `DISTINCT`.

---

## Final accepted implementation

```sql
SELECT
ROUND(
    IFNULL(
    (SELECT COUNT(*) FROM (SELECT DISTINCT requester_id, accepter_id FROM RequestAccepted) AS A)
    /
    (SELECT COUNT(*) FROM (SELECT DISTINCT sender_id, send_to_id FROM FriendRequest) AS B),
    0)
, 2) AS accept_rate;
```

---

## Key takeaways

1. Count **distinct** request pairs and **distinct** accepted pairs.
2. Duplicates must not affect the acceptance rate.
3. Accepted pairs are counted even if they never appeared in `FriendRequest`.
4. Handle the no-request case by returning `0.00`.
5. Round the final result to 2 decimal places.
