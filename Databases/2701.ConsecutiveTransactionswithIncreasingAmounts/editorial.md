# 2701. Consecutive Transactions with Increasing Amounts

## Approach: Sequential Grouping and Aggregation Method

## Core idea

We need to find transaction periods for each customer such that:

1. transactions happen on **consecutive days**
2. the **amount strictly increases** from one day to the next
3. the streak lasts for **at least 3 consecutive days**

The solution works in stages:

1. identify transaction rows that participate in valid day-to-day increasing pairs
2. assign row numbers per customer
3. build a group identifier for consecutive-date streaks
4. aggregate each streak
5. keep only streaks of sufficient length and compute the end date

This is a classic **gap-and-island** style solution, adapted for increasing consecutive transactions.

---

## Important observation

A valid streak of length 3 days like:

```text
Day 1 -> Day 2 -> Day 3
```

contains **two valid day-to-day increasing pairs**:

- Day 1 to Day 2
- Day 2 to Day 3

So one subtle point in this approach is that the CTE identifying increasing consecutive pairs returns the **starting days** of those valid adjacent transitions.

That is why, later, the final filter uses:

```sql
transaction_count > 1
```

instead of `>= 3`, because two pair-start rows correspond to a 3-day streak.

---

# Step 1: Identify consecutive increasing transaction pairs

```sql
WITH ConsecutiveIncreasingTransactions AS (
  SELECT
    a.customer_id,
    a.transaction_date
  FROM
    Transactions a
    JOIN Transactions b
      ON a.customer_id = b.customer_id
     AND b.amount > a.amount
     AND DATEDIFF(b.transaction_date, a.transaction_date) = 1
)
```

---

## Why this works

This self-join compares each transaction `a` with another transaction `b` for the same customer.

The conditions are:

### Same customer

```sql
a.customer_id = b.customer_id
```

So we only compare transactions within the same customer’s history.

### Increasing amount

```sql
b.amount > a.amount
```

So the later transaction must have a larger amount.

### Consecutive day

```sql
DATEDIFF(b.transaction_date, a.transaction_date) = 1
```

So `b` must occur exactly one day after `a`.

Together, these conditions identify every transaction date `a.transaction_date` that starts a valid one-day increasing step.

---

## Example from the sample

For customer `101`:

| transaction_date | amount |
| ---------------- | ------ |
| 2023-05-01       | 100    |
| 2023-05-02       | 150    |
| 2023-05-03       | 200    |

Valid pairs:

- `2023-05-01 -> 2023-05-02` because 150 > 100 and dates differ by 1
- `2023-05-02 -> 2023-05-03` because 200 > 150 and dates differ by 1

So `ConsecutiveIncreasingTransactions` contains:

| customer_id | transaction_date |
| ----------- | ---------------- |
| 101         | 2023-05-01       |
| 101         | 2023-05-02       |

These are the pair-start days.

---

# Step 2: Assign row numbers per customer

```sql
RankedTransactions AS (
  SELECT
    customer_id,
    transaction_date,
    ROW_NUMBER() OVER (
      PARTITION BY customer_id
      ORDER BY transaction_date
    ) AS row_num
  FROM
    ConsecutiveIncreasingTransactions
)
```

---

## Why this works

For each customer, we order the qualifying transaction dates and assign:

- first qualifying date -> row number 1
- second qualifying date -> row number 2
- third qualifying date -> row number 3
- and so on

This prepares the data for the next step, where we identify islands of consecutive dates.

---

# Step 3: Group rows into consecutive streaks

```sql
GroupedTransactions AS (
  SELECT
    customer_id,
    transaction_date,
    DATE_SUB(
      transaction_date, INTERVAL row_num DAY
    ) AS group_identifier
  FROM
    RankedTransactions
)
```

---

## Why this works

This is the key gap-and-island trick.

For consecutive dates, the value:

```sql
transaction_date - row_num
```

stays constant.

### Example

Suppose the qualifying dates are:

| transaction_date | row_num |
| ---------------- | ------- |
| 2023-05-01       | 1       |
| 2023-05-02       | 2       |
| 2023-05-03       | 3       |

Then:

- `2023-05-01 - 1 day`
- `2023-05-02 - 2 days`
- `2023-05-03 - 3 days`

all give the same value.

That shared value becomes the `group_identifier`.

So all rows in the same consecutive sequence end up in the same group.

If there is a break in dates, the identifier changes, creating a new group.

---

# Step 4: Aggregate each streak group

```sql
TransactionGroups AS (
  SELECT
    customer_id,
    MIN(transaction_date) AS consecutive_start,
    COUNT(*) AS transaction_count
  FROM
    GroupedTransactions
  GROUP BY
    customer_id,
    group_identifier
)
```

---

## Why this works

For each `(customer_id, group_identifier)` combination:

- `MIN(transaction_date)` gives the start of the streak
- `COUNT(*)` gives the number of pair-start days in that streak

As noted earlier, if a streak covers `k` actual transaction days, then there are `k - 1` valid adjacent increasing pairs.

So:

- 3-day streak -> `transaction_count = 2`
- 4-day streak -> `transaction_count = 3`

That is why the final filter checks for `transaction_count > 1`.

---

# Step 5: Compute final start and end dates

```sql
SELECT
  customer_id,
  consecutive_start,
  DATE_ADD(
    consecutive_start, INTERVAL transaction_count DAY
  ) AS consecutive_end
FROM
  TransactionGroups
WHERE
  transaction_count > 1
ORDER BY
  customer_id;
```

---

## Why the end date is computed this way

If a valid streak has:

```text
transaction_count = number of valid adjacent increasing pairs
```

then the actual streak length in days is:

```text
transaction_count + 1
```

So the end date is:

```sql
consecutive_start + transaction_count days
```

### Example

For a 3-day streak:

- pair-start rows count = 2
- end date = start date + 2 days

That correctly gives the third day.

For a 4-day streak:

- pair-start rows count = 3
- end date = start date + 3 days

That correctly gives the fourth day.

---

## Why `transaction_count > 1` is the right filter here

A streak must be at least 3 days long.

In this approach:

- 2-day streak -> only 1 valid adjacent pair -> `transaction_count = 1`
- 3-day streak -> 2 valid adjacent pairs -> `transaction_count = 2`

So keeping:

```sql
transaction_count > 1
```

means we keep only streaks of 3 or more actual transaction days.

---

# Final accepted query

```sql
WITH ConsecutiveIncreasingTransactions AS (
  SELECT
    a.customer_id,
    a.transaction_date
  FROM
    Transactions a
    JOIN Transactions b ON a.customer_id = b.customer_id
    AND b.amount > a.amount
    AND DATEDIFF(
      b.transaction_date, a.transaction_date
    ) = 1
),
RankedTransactions AS (
  SELECT
    customer_id,
    transaction_date,
    ROW_NUMBER() OVER (
      PARTITION BY customer_id
      ORDER BY
        transaction_date
    ) AS row_num
  FROM
    ConsecutiveIncreasingTransactions
),
GroupedTransactions AS (
  SELECT
    customer_id,
    transaction_date,
    DATE_SUB(
      transaction_date, INTERVAL row_num DAY
    ) AS group_identifier
  FROM
    RankedTransactions
),
TransactionGroups AS (
  SELECT
    customer_id,
    MIN(transaction_date) AS consecutive_start,
    COUNT(*) AS transaction_count
  FROM
    GroupedTransactions
  GROUP BY
    customer_id,
    group_identifier
)
SELECT
  customer_id,
  consecutive_start,
  DATE_ADD(
    consecutive_start, INTERVAL transaction_count DAY
  ) AS consecutive_end
FROM
  TransactionGroups
WHERE
  transaction_count > 1
ORDER BY
  customer_id;
```

---

# Walkthrough on the sample

## Sample input

| transaction_id | customer_id | transaction_date | amount |
| -------------- | ----------- | ---------------- | ------ |
| 1              | 101         | 2023-05-01       | 100    |
| 2              | 101         | 2023-05-02       | 150    |
| 3              | 101         | 2023-05-03       | 200    |
| 4              | 102         | 2023-05-01       | 50     |
| 5              | 102         | 2023-05-03       | 100    |
| 6              | 102         | 2023-05-04       | 200    |
| 7              | 105         | 2023-05-01       | 100    |
| 8              | 105         | 2023-05-02       | 150    |
| 9              | 105         | 2023-05-03       | 200    |
| 10             | 105         | 2023-05-04       | 300    |
| 11             | 105         | 2023-05-12       | 250    |
| 12             | 105         | 2023-05-13       | 260    |
| 13             | 105         | 2023-05-14       | 270    |

---

## Customer 101

Transactions:

```text
2023-05-01 -> 100
2023-05-02 -> 150
2023-05-03 -> 200
```

Valid increasing consecutive pairs:

- `2023-05-01 -> 2023-05-02`
- `2023-05-02 -> 2023-05-03`

So pair-start dates are:

```text
2023-05-01, 2023-05-02
```

That group has:

```text
transaction_count = 2
```

So:

- `consecutive_start = 2023-05-01`
- `consecutive_end = 2023-05-01 + 2 days = 2023-05-03`

Result:

| customer_id | consecutive_start | consecutive_end |
| ----------- | ----------------- | --------------- |
| 101         | 2023-05-01        | 2023-05-03      |

---

## Customer 102

Transactions:

```text
2023-05-01 -> 50
2023-05-03 -> 100
2023-05-04 -> 200
```

Check pairs:

- `2023-05-01 -> 2023-05-03` is not consecutive
- `2023-05-03 -> 2023-05-04` is consecutive and increasing

So only one pair-start date appears:

```text
2023-05-03
```

That gives:

```text
transaction_count = 1
```

Since `transaction_count > 1` is false, customer 102 is excluded.

Correct, because there is no 3-day streak.

---

## Customer 105

Transactions:

```text
2023-05-01 -> 100
2023-05-02 -> 150
2023-05-03 -> 200
2023-05-04 -> 300
2023-05-12 -> 250
2023-05-13 -> 260
2023-05-14 -> 270
```

### First streak

Valid pairs:

- `2023-05-01 -> 2023-05-02`
- `2023-05-02 -> 2023-05-03`
- `2023-05-03 -> 2023-05-04`

Pair-start dates:

```text
2023-05-01, 2023-05-02, 2023-05-03
```

So:

- `transaction_count = 3`
- `consecutive_start = 2023-05-01`
- `consecutive_end = 2023-05-01 + 3 days = 2023-05-04`

### Second streak

Valid pairs:

- `2023-05-12 -> 2023-05-13`
- `2023-05-13 -> 2023-05-14`

Pair-start dates:

```text
2023-05-12, 2023-05-13
```

So:

- `transaction_count = 2`
- `consecutive_start = 2023-05-12`
- `consecutive_end = 2023-05-12 + 2 days = 2023-05-14`

Results:

| customer_id | consecutive_start | consecutive_end |
| ----------- | ----------------- | --------------- |
| 105         | 2023-05-01        | 2023-05-04      |
| 105         | 2023-05-12        | 2023-05-14      |

---

# Why this approach is elegant

The difficult part of this problem is recognizing that:

- the real target is a streak of days
- but the easiest first filter is on **adjacent day pairs**

Once adjacent increasing pairs are isolated, the problem becomes a consecutive-date grouping problem.

Then the classic gap-and-island trick:

```sql
date - row_number
```

does the grouping cleanly.

That is a strong SQL pattern to recognize.

---

# Important SQL concepts used here

## 1. Self join

Used to find adjacent increasing transactions.

## 2. `DATEDIFF(...)=1`

Used to enforce consecutive days.

## 3. `ROW_NUMBER()`

Used to prepare the consecutive-date grouping.

## 4. Gap-and-island grouping

```sql
DATE_SUB(transaction_date, INTERVAL row_num DAY)
```

Used to identify continuous date streaks.

## 5. `DATE_ADD(...)`

Used to compute the final end date.

---

# Subtle but important interpretation

This approach groups **pair-start dates**, not all actual transaction dates.

That means the aggregated `transaction_count` is one less than the actual streak length in days.

So:

- filter is `transaction_count > 1`
- end date is `start + transaction_count days`

Both are correct for this formulation.

If you miss that detail, the logic can look off at first glance.

---

# Complexity

Let `n` be the number of rows in `Transactions`.

## Time Complexity

The self join on transactions per customer can be the dominant part.

A practical high-level summary is that the query is heavier than a pure single-pass window-function solution because it explicitly compares rows through a join.

## Space Complexity

Additional space is used for the intermediate CTEs and grouped results.

---

# Key takeaways

1. First find adjacent transactions that are both:
   - one day apart
   - strictly increasing in amount
2. Then group the resulting dates into consecutive streaks.
3. Use the gap-and-island trick to separate multiple streaks for the same customer.
4. A 3-day valid streak produces 2 adjacent valid pair-start rows.
5. That is why the final condition is `transaction_count > 1`.

---

## Final accepted implementation

```sql
WITH ConsecutiveIncreasingTransactions AS (
  SELECT
    a.customer_id,
    a.transaction_date
  FROM
    Transactions a
    JOIN Transactions b ON a.customer_id = b.customer_id
    AND b.amount > a.amount
    AND DATEDIFF(
      b.transaction_date, a.transaction_date
    ) = 1
),
RankedTransactions AS (
  SELECT
    customer_id,
    transaction_date,
    ROW_NUMBER() OVER (
      PARTITION BY customer_id
      ORDER BY
        transaction_date
    ) AS row_num
  FROM
    ConsecutiveIncreasingTransactions
),
GroupedTransactions AS (
  SELECT
    customer_id,
    transaction_date,
    DATE_SUB(
      transaction_date, INTERVAL row_num DAY
    ) AS group_identifier
  FROM
    RankedTransactions
),
TransactionGroups AS (
  SELECT
    customer_id,
    MIN(transaction_date) AS consecutive_start,
    COUNT(*) AS transaction_count
  FROM
    GroupedTransactions
  GROUP BY
    customer_id,
    group_identifier
)
SELECT
  customer_id,
  consecutive_start,
  DATE_ADD(
    consecutive_start, INTERVAL transaction_count DAY
  ) AS consecutive_end
FROM
  TransactionGroups
WHERE
  transaction_count > 1
ORDER BY
  customer_id;
```
