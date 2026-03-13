# 1747. Leetflex Banned Accounts — Detailed Summary

## Approach 1: Using `CROSS JOIN` and `DISTINCT`

This approach solves the problem by comparing each login session against every other login session.

The goal is to detect whether the **same account** was active at the same moment from **two different IP addresses**.

That means we need to find pairs of rows such that:

- both rows belong to the same `account_id`
- the `ip_address` values are different
- the two time intervals overlap

If such a pair exists, the account should be banned.

---

## Core Idea

The `LogInfo` table stores login sessions as time intervals:

- session starts at `login`
- session ends at `logout`

So each row can be treated like an interval:

```text
[login, logout]
```

An account should be banned if two intervals for the same account:

- come from different IPs
- overlap at any moment

To detect such pairs, this approach joins the table with itself.

---

# Why a Self-Comparison Is Needed

The question is not asking something about one row alone.

It asks whether **one session conflicts with another session** for the same account.

That means we must compare rows against other rows from the same table.

A `CROSS JOIN` is one way to do this.

It creates all possible row pairs between:

- one copy of `LogInfo`
- another copy of `LogInfo`

Then the `WHERE` clause keeps only the meaningful pairs.

---

# Query

```sql
SELECT
  DISTINCT l1.account_id
FROM
  LogInfo l1
CROSS JOIN
  LogInfo l2
WHERE
  l1.account_id = l2.account_id
  AND l1.ip_address != l2.ip_address
  AND l1.login <= l2.logout
  AND l2.login <= l1.logout;
```

---

# Step-by-Step Explanation

## 1. Create all row pairs with `CROSS JOIN`

```sql
FROM LogInfo l1
CROSS JOIN LogInfo l2
```

This creates the Cartesian product of the table with itself.

If there are `n` rows, this produces up to `n × n` row pairs before filtering.

Conceptually, every session is compared with every other session.

That sounds expensive, but it allows us to search for overlapping conflicting sessions.

---

## 2. Keep only pairs from the same account

```sql
l1.account_id = l2.account_id
```

We only care about comparisons within the same account.

A session from account `1` should not be compared to a session from account `2`.

So this condition restricts the comparison to same-account pairs.

---

## 3. Require different IP addresses

```sql
l1.ip_address != l2.ip_address
```

Even if an account has overlapping sessions, it should only be banned if the overlap comes from **different IP addresses**.

So two rows with the same IP should not count.

This condition ensures we only detect suspicious multi-IP overlap.

---

## 4. Check whether the two sessions overlap

```sql
l1.login <= l2.logout
AND l2.login <= l1.logout
```

This is a standard interval-overlap condition.

Each session is an interval:

- `l1` = `[l1.login, l1.logout]`
- `l2` = `[l2.login, l2.logout]`

Two intervals overlap if:

```text
start1 <= end2
AND
start2 <= end1
```

That is exactly what the query checks.

---

# Why This Overlap Condition Is Correct

Suppose the intervals are:

- Session A: `09:00` to `09:30`
- Session B: `08:00` to `11:30`

These clearly overlap.

Check the condition:

- `09:00 <= 11:30` → true
- `08:00 <= 09:30` → true

So overlap is detected.

Now suppose the intervals are:

- Session A: `16:00` to `16:59:59`
- Session B: `17:00` to `17:59:59`

These do **not** overlap.

Check the condition:

- `16:00 <= 17:59:59` → true
- `17:00 <= 16:59:59` → false

Since both conditions are not true, overlap is not detected.

That is correct.

---

# Important Detail: Endpoints Count as Overlap

The example includes account `4`:

- one session ends at `17:00:00`
- another session starts at `17:00:00`

The account should be banned.

That means equality at the boundary counts as overlap.

This is why the query uses:

```sql
<=
```

instead of:

```sql
<
```

So intervals touching at exactly one moment are considered overlapping.

---

# Why `DISTINCT` Is Needed

After a `CROSS JOIN`, the same banned account may appear many times.

For example, if one account has multiple conflicting row pairs, then the query could produce repeated values like:

| account_id |
| ---------: |
|          1 |
|          1 |
|          1 |
|          4 |
|          4 |

But the final answer should list each banned account only once.

So we use:

```sql
SELECT DISTINCT l1.account_id
```

to return unique account IDs.

---

# Worked Example

## Input

| account_id | ip_address | login               | logout              |
| ---------: | ---------: | ------------------- | ------------------- |
|          1 |          1 | 2021-02-01 09:00:00 | 2021-02-01 09:30:00 |
|          1 |          2 | 2021-02-01 08:00:00 | 2021-02-01 11:30:00 |
|          2 |          6 | 2021-02-01 20:30:00 | 2021-02-01 22:00:00 |
|          2 |          7 | 2021-02-02 20:30:00 | 2021-02-02 22:00:00 |
|          3 |          9 | 2021-02-01 16:00:00 | 2021-02-01 16:59:59 |
|          3 |         13 | 2021-02-01 17:00:00 | 2021-02-01 17:59:59 |
|          4 |         10 | 2021-02-01 16:00:00 | 2021-02-01 17:00:00 |
|          4 |         11 | 2021-02-01 17:00:00 | 2021-02-01 17:59:59 |

---

## Account 1

Sessions:

- IP 1: `09:00` to `09:30`
- IP 2: `08:00` to `11:30`

Different IPs? Yes.

Overlap?

- `09:00 <= 11:30` → yes
- `08:00 <= 09:30` → yes

So account `1` is banned.

---

## Account 2

Sessions:

- IP 6: `2021-02-01 20:30` to `2021-02-01 22:00`
- IP 7: `2021-02-02 20:30` to `2021-02-02 22:00`

Different IPs? Yes.

Overlap?

These sessions are on different days, so there is no shared time interval.

So account `2` is not banned.

---

## Account 3

Sessions:

- IP 9: `16:00:00` to `16:59:59`
- IP 13: `17:00:00` to `17:59:59`

Different IPs? Yes.

Overlap?

- `16:00:00 <= 17:59:59` → yes
- `17:00:00 <= 16:59:59` → no

No overlap.

So account `3` is not banned.

---

## Account 4

Sessions:

- IP 10: `16:00:00` to `17:00:00`
- IP 11: `17:00:00` to `17:59:59`

Different IPs? Yes.

Overlap?

- `16:00:00 <= 17:59:59` → yes
- `17:00:00 <= 17:00:00` → yes

They overlap at exactly `17:00:00`.

So account `4` is banned.

---

# Final Output

| account_id |
| ---------: |
|          1 |
|          4 |

---

# Why `CROSS JOIN` Works Here

A `CROSS JOIN` is conceptually simple:

- generate all possible pairs
- filter to the suspicious ones

This makes the logic easy to express.

It is effectively a self-join written in Cartesian-product style.

Equivalent logic can also be written using an explicit self join:

```sql
SELECT DISTINCT l1.account_id
FROM LogInfo l1
JOIN LogInfo l2
  ON l1.account_id = l2.account_id
 AND l1.ip_address != l2.ip_address
 AND l1.login <= l2.logout
 AND l2.login <= l1.logout;
```

This is usually easier to read than `CROSS JOIN` + `WHERE`, but both are logically similar.

---

# Important Note About Duplicate Rows

The problem says `LogInfo` may contain duplicate rows.

That means the self-comparison can generate even more repeated matches.

However, because the final query uses:

```sql
DISTINCT l1.account_id
```

duplicate row pairs do not affect the correctness of the final result.

They may affect performance, but not the final banned-account list.

---

# Symmetry of Pair Matching

One subtle point:

if session A overlaps session B, then session B also overlaps session A.

So the self-join can find both:

- `(A, B)`
- `(B, A)`

That creates duplicate logical evidence.

Again, `DISTINCT` protects the final result.

If you wanted to reduce duplicate pair comparisons, you could add an ordering rule such as:

```sql
AND l1.login <= l2.login
```

or compare row identifiers if one existed.

But since the task only asks for unique account IDs, that optimization is optional.

---

# Portable and Cleaner Version

A slightly cleaner and more explicit version is:

```sql
SELECT DISTINCT l1.account_id
FROM LogInfo l1
JOIN LogInfo l2
  ON l1.account_id = l2.account_id
 AND l1.ip_address <> l2.ip_address
 AND l1.login <= l2.logout
 AND l2.login <= l1.logout;
```

This avoids the visual heaviness of `CROSS JOIN`.

It is still the same idea: compare sessions of the same account to detect overlap across different IPs.

---

# Complexity Discussion

Let `n` be the number of rows in `LogInfo`.

## Time Complexity

A self-comparison of the table can be expensive.

In the worst case, the join considers roughly:

```text
O(n^2)
```

row pairs before filtering.

So this is not a lightweight query for very large tables.

## Space Complexity

Depends on the database engine and join execution strategy.

The query itself does not create a large explicit intermediate object in user code, but conceptually it works over pairwise comparisons.

---

# Why This Approach Is Still Reasonable

Even though the worst-case cost is quadratic, the logic is very direct:

- compare all relevant sessions
- filter overlapping different-IP pairs
- return unique account IDs

For an interview or a SQL practice problem, that directness is often more valuable than micro-optimizing the join.

---

# Final Code Example

```sql
SELECT
  DISTINCT l1.account_id
FROM
  LogInfo l1
CROSS JOIN
  LogInfo l2
WHERE
  l1.account_id = l2.account_id
  AND l1.ip_address != l2.ip_address
  AND l1.login <= l2.logout
  AND l2.login <= l1.logout;
```

---

# Recommended Readable Variant

```sql
SELECT DISTINCT l1.account_id
FROM LogInfo l1
JOIN LogInfo l2
  ON l1.account_id = l2.account_id
 AND l1.ip_address <> l2.ip_address
 AND l1.login <= l2.logout
 AND l2.login <= l1.logout;
```

---

# Key Takeaways

- Treat each row as a login interval
- An account is banned if two sessions from different IPs overlap
- Compare the table with itself to detect conflicting session pairs
- Two intervals overlap when:
  - `login1 <= logout2`
  - `login2 <= logout1`
- Use `DISTINCT` because self-comparison can produce repeated account IDs

---
