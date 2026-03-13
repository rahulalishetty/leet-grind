# 1205. Monthly Transactions II — Detailed Summary

## Approach: Combining Two Tables Using `UNION ALL`

This approach solves the problem by computing two separate monthly summaries:

1. monthly **chargebacks**
2. monthly **approved transactions**

Then it merges them using `UNION ALL`, and finally aggregates again by:

- `month`
- `country`

This works because approved transactions and chargebacks come from different logical sources:

- approved transactions are directly stored in `Transactions`
- chargebacks are recorded in `Chargebacks`, but their amount and country must be looked up from `Transactions`

---

## Problem Restatement

For each `(month, country)`, we need to report:

- `approved_count`
- `approved_amount`
- `chargeback_count`
- `chargeback_amount`

Important details:

- The month is based on the relevant transaction date
- For approved transactions, use `Transactions.trans_date`
- For chargebacks, use `Chargebacks.trans_date`
- Chargeback amount and country come from the original transaction row in `Transactions`
- Rows with all four metrics equal to zero should not appear

---

# Core Idea

Instead of trying to compute everything in one complicated query, we split the task into two simpler aggregations:

## Part 1: Monthly chargebacks

For every `(month, country)`:

- count chargebacks
- sum their amounts
- set approved columns to `0`

## Part 2: Monthly approved transactions

For every `(month, country)`:

- count approved transactions
- sum their amounts
- set chargeback columns to `0`

Then stack both outputs using:

```sql
UNION ALL
```

After stacking, some `(month, country)` values appear twice:

- once from the chargeback side
- once from the approved side

So we do one final outer aggregation to combine them.

---

# Why `UNION ALL` Is the Right Choice

`UNION ALL` keeps all rows exactly as they are.

That is what we want, because:

- approved rows and chargeback rows represent different metrics
- we do not want duplicate elimination
- we want the outer `SUM(...)` to combine them

If we used `UNION`, the database would try to remove duplicates, which is unnecessary and can be slower.

---

# Step 1: Compute Monthly Chargebacks

## Query

```sql
SELECT DISTINCT
    DATE_FORMAT(c.trans_date, '%Y-%m') AS month,
    t.country,
    0 AS approved_count,
    0 AS approved_amount,
    COUNT(DISTINCT c.trans_id) AS chargeback_count,
    SUM(amount) AS chargeback_amount
FROM Chargebacks c
JOIN Transactions t
    ON c.trans_id = t.id
GROUP BY month, country;
```

---

## Why We Need a Join Here

The `Chargebacks` table contains:

- `trans_id`
- `trans_date`

But it does **not** contain:

- `country`
- `amount`

Those are stored in `Transactions`.

So to compute monthly chargeback totals by country, we must join:

```sql
c.trans_id = t.id
```

This lets us access:

- `t.country`
- `t.amount`

for each chargeback row.

---

## Why the Month Comes from `Chargebacks.trans_date`

A chargeback belongs to the month when the chargeback occurred, not the month when the original transaction occurred.

So for chargebacks we use:

```sql
DATE_FORMAT(c.trans_date, '%Y-%m')
```

This converts the date into year-month form such as:

- `2019-05`
- `2019-06`
- `2019-09`

---

## Why Approved Columns Are Set to 0

This subquery only computes chargeback metrics.

So it fills the approved columns with zeros:

```sql
0 AS approved_count,
0 AS approved_amount
```

That makes the schema match the approved-transactions subquery, which is necessary for `UNION ALL`.

---

## Example Output of Step 1

| month   | country | approved_count | approved_amount | chargeback_count | chargeback_amount |
| ------- | ------- | -------------: | --------------: | ---------------: | ----------------: |
| 2019-05 | US      |              0 |               0 |                1 |              2000 |
| 2019-06 | US      |              0 |               0 |                1 |              1000 |
| 2019-09 | US      |              0 |               0 |                1 |              5000 |

---

# Step 2: Compute Monthly Approved Transactions

## Query

```sql
SELECT
    DATE_FORMAT(trans_date, '%Y-%m') AS month,
    country,
    COUNT(DISTINCT id) AS approved_count,
    SUM(amount) AS approved_amount,
    0 AS chargeback_count,
    0 AS chargeback_amount
FROM Transactions t
WHERE state = 'approved'
GROUP BY month, country;
```

---

## What This Does

This query groups approved transactions by:

- month
- country

Then computes:

- how many approved transactions occurred
- total approved amount

Since this subquery is only about approved transactions, the chargeback columns are filled with 0.

---

## Why Filter with `WHERE state = 'approved'`

Only transactions with state `approved` should contribute to:

- `approved_count`
- `approved_amount`

So declined transactions are excluded here.

---

## Why the Month Comes from `Transactions.trans_date`

For approved transaction metrics, the month should reflect when the transaction itself happened.

So we use:

```sql
DATE_FORMAT(trans_date, '%Y-%m')
```

based on the `Transactions` table.

---

## Example Output of Step 2

| month   | country | approved_count | approved_amount | chargeback_count | chargeback_amount |
| ------- | ------- | -------------: | --------------: | ---------------: | ----------------: |
| 2019-05 | US      |              1 |            1000 |                0 |                 0 |
| 2019-06 | US      |              2 |            8000 |                0 |                 0 |

---

# Step 3: Combine Both Outputs with `UNION ALL`

When we stack the results, we get:

| month   | country | approved_count | approved_amount | chargeback_count | chargeback_amount |
| ------- | ------- | -------------: | --------------: | ---------------: | ----------------: |
| 2019-05 | US      |              0 |               0 |                1 |              2000 |
| 2019-06 | US      |              0 |               0 |                1 |              1000 |
| 2019-09 | US      |              0 |               0 |                1 |              5000 |
| 2019-05 | US      |              1 |            1000 |                0 |                 0 |
| 2019-06 | US      |              2 |            8000 |                0 |                 0 |

Notice that:

- `2019-05 / US` appears twice
- `2019-06 / US` appears twice

That is expected.

One row holds the chargeback metrics, the other holds the approved metrics.

So now we need one more aggregation step.

---

# Step 4: Aggregate Again by Month and Country

The outer query groups by:

- `month`
- `country`

and sums each metric.

That combines the two partial rows into one final row per month and country.

---

# Final Implementation

```sql
SELECT
    t0.month,
    t0.country,
    SUM(approved_count) AS approved_count,
    SUM(approved_amount) AS approved_amount,
    SUM(chargeback_count) AS chargeback_count,
    SUM(chargeback_amount) AS chargeback_amount
FROM (
    SELECT
        DATE_FORMAT(c.trans_date, '%Y-%m') AS month,
        t.country,
        0 AS approved_count,
        0 AS approved_amount,
        COUNT(DISTINCT c.trans_id) AS chargeback_count,
        SUM(amount) AS chargeback_amount
    FROM Chargebacks c
    JOIN Transactions t
        ON c.trans_id = t.id
    GROUP BY month, country

    UNION ALL

    SELECT
        DATE_FORMAT(trans_date, '%Y-%m') AS month,
        country,
        COUNT(DISTINCT id) AS approved_count,
        SUM(amount) AS approved_amount,
        0 AS chargeback_count,
        0 AS chargeback_amount
    FROM Transactions t
    WHERE state = 'approved'
    GROUP BY month, country
) AS t0
GROUP BY month, country;
```

---

# Step-by-Step Walkthrough on the Example

## Input Data

### Transactions

|  id | country | state    | amount | trans_date |
| --: | ------- | -------- | -----: | ---------- |
| 101 | US      | approved |   1000 | 2019-05-18 |
| 102 | US      | declined |   2000 | 2019-05-19 |
| 103 | US      | approved |   3000 | 2019-06-10 |
| 104 | US      | declined |   4000 | 2019-06-13 |
| 105 | US      | approved |   5000 | 2019-06-15 |

### Chargebacks

| trans_id | trans_date |
| -------: | ---------- |
|      102 | 2019-05-29 |
|      101 | 2019-06-30 |
|      105 | 2019-09-18 |

---

## Chargeback Summary

### Chargeback for transaction 102

- original transaction amount = 2000
- country = US
- chargeback date = 2019-05-29
- month = `2019-05`

### Chargeback for transaction 101

- original amount = 1000
- country = US
- chargeback date = 2019-06-30
- month = `2019-06`

### Chargeback for transaction 105

- original amount = 5000
- country = US
- chargeback date = 2019-09-18
- month = `2019-09`

So chargeback aggregate is:

| month   | country | chargeback_count | chargeback_amount |
| ------- | ------- | ---------------: | ----------------: |
| 2019-05 | US      |                1 |              2000 |
| 2019-06 | US      |                1 |              1000 |
| 2019-09 | US      |                1 |              5000 |

---

## Approved Transactions Summary

Approved rows are:

- 101 → May → 1000
- 103 → June → 3000
- 105 → June → 5000

So approved aggregate is:

| month   | country | approved_count | approved_amount |
| ------- | ------- | -------------: | --------------: |
| 2019-05 | US      |              1 |            1000 |
| 2019-06 | US      |              2 |            8000 |

---

## Final Combined Output

After outer aggregation:

| month   | country | approved_count | approved_amount | chargeback_count | chargeback_amount |
| ------- | ------- | -------------: | --------------: | ---------------: | ----------------: |
| 2019-05 | US      |              1 |            1000 |                1 |              2000 |
| 2019-06 | US      |              2 |            8000 |                1 |              1000 |
| 2019-09 | US      |              0 |               0 |                1 |              5000 |

This matches the required result.

---

# Why Rows with All Zeros Are Naturally Ignored

The problem says:

> given the month and country, ignore rows with all zeros

This solution naturally satisfies that requirement because the subqueries only generate rows when there is:

- at least one approved transaction
  or
- at least one chargeback

So a `(month, country)` pair with no activity in either category never appears at all.

Therefore, the final result cannot contain a row where all four metrics are zero.

---

# Why `COUNT(DISTINCT ...)` Is Used

## In the chargeback query

```sql
COUNT(DISTINCT c.trans_id)
```

This counts unique chargeback transaction IDs.

## In the approved query

```sql
COUNT(DISTINCT id)
```

This counts unique approved transaction IDs.

Because transaction IDs are unique in `Transactions`, `COUNT(id)` would also work there. But using `DISTINCT` is still safe and explicit.

For `Chargebacks`, if the table structure guarantees one row per chargeback event per transaction, plain `COUNT(*)` might also work. But `COUNT(DISTINCT c.trans_id)` is a safe defensive choice.

---

# Important Distinction Between the Two Dates

This problem has **two different date columns**, and they mean different things.

## For approved transactions

Use:

```sql
Transactions.trans_date
```

because we are counting when the transaction occurred.

## For chargebacks

Use:

```sql
Chargebacks.trans_date
```

because we are counting when the chargeback occurred.

That distinction is critical.

A common mistake is to group chargebacks by the original transaction date instead of the chargeback date.

That would be wrong.

---

# Why Country Comes from `Transactions`

The `Chargebacks` table has no country column.

So even for chargebacks, country must be obtained from the original transaction row:

```sql
JOIN Transactions t
ON c.trans_id = t.id
```

---

# Alternative Mental Model

You can think of this approach as building a normalized event stream with the same schema.

## Approved side produces rows like:

- monthly approved count/amount
- chargeback metrics set to zero

## Chargeback side produces rows like:

- monthly chargeback count/amount
- approved metrics set to zero

Then the outer query simply merges those compatible rows.

This is why the union-based approach is elegant here.

---

# Complexity Analysis

Let:

- `T` = number of rows in `Transactions`
- `C` = number of rows in `Chargebacks`

## Time Complexity

The solution performs:

- one aggregation over approved transactions
- one join between chargebacks and transactions
- one aggregation over that joined result
- one final aggregation over the unioned rows

So overall work is roughly proportional to:

```text
O(T + C + join_cost)
```

depending on indexing and the database engine.

If indexes exist on:

- `Transactions.id`
- `Transactions.state`
- maybe date-related fields

the query performs well.

---

## Space Complexity

The database engine will need temporary space for:

- the grouped chargeback result
- the grouped approved result
- the unioned intermediate result

But all are aggregated forms, not raw full-table copies.

---

# Final Recommended Query

```sql
SELECT
    t0.month,
    t0.country,
    SUM(approved_count) AS approved_count,
    SUM(approved_amount) AS approved_amount,
    SUM(chargeback_count) AS chargeback_count,
    SUM(chargeback_amount) AS chargeback_amount
FROM (
    SELECT
        DATE_FORMAT(c.trans_date, '%Y-%m') AS month,
        t.country,
        0 AS approved_count,
        0 AS approved_amount,
        COUNT(DISTINCT c.trans_id) AS chargeback_count,
        SUM(t.amount) AS chargeback_amount
    FROM Chargebacks c
    JOIN Transactions t
        ON c.trans_id = t.id
    GROUP BY month, country

    UNION ALL

    SELECT
        DATE_FORMAT(t.trans_date, '%Y-%m') AS month,
        t.country,
        COUNT(DISTINCT t.id) AS approved_count,
        SUM(t.amount) AS approved_amount,
        0 AS chargeback_count,
        0 AS chargeback_amount
    FROM Transactions t
    WHERE t.state = 'approved'
    GROUP BY month, country
) AS t0
GROUP BY month, country;
```

This version explicitly qualifies columns with table aliases, which makes it easier to read and less error-prone.

---

# Key Takeaways

- Compute approved transactions and chargebacks separately
- Use `UNION ALL` to stack the two summaries
- Aggregate once more to combine rows with the same `(month, country)`
- Use `Chargebacks.trans_date` for chargeback month
- Use `Transactions.trans_date` for approved month
- Get chargeback amount and country from `Transactions`
- Rows with all zeros never appear because only active month-country pairs are generated

---
