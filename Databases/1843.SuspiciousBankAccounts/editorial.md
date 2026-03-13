# 1843. Suspicious Bank Accounts — Detailed Summary

## Approach: CTE and `PERIOD_DIFF`

This approach solves the problem in two major steps:

1. build a monthly-income summary for each account using a Common Table Expression (CTE)
2. compare those monthly summaries against each other to find **two consecutive months** where the income exceeded the account's `max_income`

The key idea is that we do **not** care about every transaction individually in the final decision.
We only care about:

- monthly total income
- whether that monthly total exceeded the allowed maximum
- whether such excess happened in consecutive months

---

## Problem Restatement

A bank account is suspicious if:

- the total **monthly income** exceeds `max_income`
- for **two or more consecutive months**

Important rules:

- income means only `Creditor` transactions
- `Debtor` transactions do **not** count toward income
- we need to return only the `account_id` values of suspicious accounts

---

## Core Idea

This problem becomes much simpler if we reduce the transaction table into a per-account, per-month summary.

For each account and month, compute:

- the month in `YYYYMM` form
- the total sum of `Creditor` amounts in that month
- the account's `max_income`

Then keep only the months where:

```text
monthly_income > max_income
```

Now the problem becomes:

> for which accounts do we have two records in this filtered monthly table that are exactly one month apart?

That is where `PERIOD_DIFF()` helps.

---

# Step 1: Build the Monthly Income CTE

## Query

```sql
WITH MonthlyIncome AS (
  SELECT
    t.account_id,
    DATE_FORMAT(t.day, '%Y%m') AS income_month,
    SUM(t.amount) AS monthly_income,
    a.max_income
  FROM Transactions t
  LEFT JOIN Accounts a
    ON a.account_id = t.account_id
  WHERE t.type = 'Creditor'
  GROUP BY
    t.account_id,
    income_month
  HAVING SUM(t.amount) > a.max_income
)
```

---

## What This CTE Does

This CTE transforms raw transaction rows into a compact monthly summary.

For each account and each month, it computes:

- `income_month` → the month of the transaction in `YYYYMM` format
- `monthly_income` → sum of deposits in that month
- `max_income` → the account's threshold

Then it immediately filters to only those months where the income exceeded the threshold.

So after the CTE, we have a much smaller table containing only the “exceeding” months.

---

## Clause-by-Clause Explanation

### `DATE_FORMAT(t.day, '%Y%m') AS income_month`

This converts the transaction timestamp into a month key.

Examples:

- `2021-06-02 11:38:14` → `202106`
- `2021-07-26 15:09:56` → `202107`

This lets us aggregate transactions at the monthly level.

---

### `SUM(t.amount) AS monthly_income`

This adds together all `Creditor` transactions for the same account in the same month.

That gives the account's monthly income.

---

### `LEFT JOIN Accounts a ON a.account_id = t.account_id`

This brings in the `max_income` threshold so we can compare the monthly income against it.

Since every account in transactions should correspond to an account record, this works as expected.

---

### `WHERE t.type = 'Creditor'`

This is critical.

Only deposits count as income.

Withdrawals (`Debtor`) must be excluded.

So the query intentionally ignores all non-creditor rows.

---

### `GROUP BY t.account_id, income_month`

This creates one row per:

- account
- month

That is exactly the grain of data needed for this problem.

---

### `HAVING SUM(t.amount) > a.max_income`

This filters the grouped rows to keep only months where the total income exceeded the threshold.

This is more efficient than keeping all months and checking later, because it narrows the working dataset early.

---

# Example Input

## Accounts

| account_id | max_income |
| ---------: | ---------: |
|          3 |      21000 |
|          4 |      10400 |

## Transactions

| transaction_id | account_id | type     | amount | day                 |
| -------------: | ---------: | -------- | -----: | ------------------- |
|              2 |          3 | Creditor | 107100 | 2021-06-02 11:38:14 |
|              4 |          4 | Creditor |  10400 | 2021-06-20 12:39:18 |
|             11 |          4 | Debtor   |  58800 | 2021-07-23 12:41:55 |
|              1 |          4 | Creditor |  49300 | 2021-05-03 16:11:04 |
|             15 |          3 | Debtor   |  75500 | 2021-05-23 14:40:20 |
|             10 |          3 | Creditor | 102100 | 2021-06-15 10:37:16 |
|             14 |          4 | Creditor |  56300 | 2021-07-21 12:12:25 |
|             19 |          4 | Debtor   | 101100 | 2021-05-09 15:21:49 |
|              8 |          3 | Creditor |  64900 | 2021-07-26 15:09:56 |
|              7 |          3 | Creditor |  90900 | 2021-06-14 11:23:07 |

---

# Step 1 Result: `MonthlyIncome`

After filtering to only `Creditor` transactions and aggregating by month, the CTE produces:

| account_id | income_month | monthly_income | max_income |
| ---------: | -----------: | -------------: | ---------: |
|          3 |       202106 |         300100 |      21000 |
|          4 |       202105 |          49300 |      10400 |
|          4 |       202107 |          56300 |      10400 |
|          3 |       202107 |          64900 |      21000 |

---

## How These Values Were Computed

### Account 3

#### June 2021

Deposits:

- 107100
- 102100
- 90900

Total:

```text
107100 + 102100 + 90900 = 300100
```

Compare with `max_income = 21000`:

```text
300100 > 21000
```

So `202106` is kept.

#### July 2021

Deposits:

- 64900

Total:

```text
64900
```

Compare with `21000`:

```text
64900 > 21000
```

So `202107` is kept.

---

### Account 4

#### May 2021

Deposits:

- 49300

Total:

```text
49300
```

Compare with `10400`:

```text
49300 > 10400
```

So `202105` is kept.

#### June 2021

Deposits:

- 10400

Total:

```text
10400
```

Compare with `10400`:

```text
10400 > 10400
```

This is false, because equality does not count as exceeding.

So `202106` is **not** kept.

#### July 2021

Deposits:

- 56300

Total:

```text
56300
```

Compare with `10400`:

```text
56300 > 10400
```

So `202107` is kept.

---

# Step 2: Self-Join the CTE to Find Consecutive Months

## Query

```sql
SELECT
  Income1.account_id
FROM MonthlyIncome Income1,
     MonthlyIncome Income2
WHERE Income1.account_id = Income2.account_id
  AND PERIOD_DIFF(Income1.income_month, Income2.income_month) = 1
GROUP BY Income1.account_id
ORDER BY Income1.account_id;
```

---

## Why a Self-Join Is Used

After the CTE, each row represents one exceeding month for one account.

Now we need to check whether the same account has **another exceeding month** that is exactly one month earlier or later.

That is a comparison between rows of the same derived table.

So a self-join is a natural choice.

---

## How `PERIOD_DIFF()` Helps

`PERIOD_DIFF(x, y)` returns the difference in months between two `YYYYMM` period values.

Examples:

- `PERIOD_DIFF(202107, 202106)` → `1`
- `PERIOD_DIFF(202107, 202105)` → `2`

So if:

```sql
PERIOD_DIFF(Income1.income_month, Income2.income_month) = 1
```

then the two rows represent consecutive months.

---

## Join Conditions Explained

### `Income1.account_id = Income2.account_id`

We only compare months for the same account.

---

### `PERIOD_DIFF(Income1.income_month, Income2.income_month) = 1`

This means:

- `Income1` is one month after `Income2`

So if an account exceeded the limit in:

- June 2021
- July 2021

then this condition matches the pair:

- `(202107, 202106)`

That proves there are two consecutive exceeding months.

---

## Why `GROUP BY Income1.account_id` Is Used

The self-join may produce multiple matching pairs for the same suspicious account.

But the output should list each account only once.

So grouping by account ID collapses duplicates into one row per suspicious account.

---

# Example Result of Step 2

From the CTE:

| account_id | income_month |
| ---------: | -----------: |
|          3 |       202106 |
|          3 |       202107 |
|          4 |       202105 |
|          4 |       202107 |

Now compare within the same account.

### Account 3

- `PERIOD_DIFF(202107, 202106) = 1`

So account `3` qualifies.

### Account 4

- `PERIOD_DIFF(202107, 202105) = 2`

Not consecutive.

So account `4` does not qualify.

Final result:

| account_id |
| ---------: |
|          3 |

---

# Full Implementation

```sql
WITH MonthlyIncome AS (
  SELECT
    t.account_id,
    DATE_FORMAT(t.day, '%Y%m') AS income_month,
    SUM(t.amount) AS monthly_income,
    a.max_income
  FROM Transactions t
  LEFT JOIN Accounts a
    ON a.account_id = t.account_id
  WHERE t.type = 'Creditor'
  GROUP BY
    t.account_id,
    income_month
  HAVING SUM(t.amount) > a.max_income
)
SELECT
  Income1.account_id
FROM MonthlyIncome Income1,
     MonthlyIncome Income2
WHERE Income1.account_id = Income2.account_id
  AND PERIOD_DIFF(Income1.income_month, Income2.income_month) = 1
GROUP BY Income1.account_id
ORDER BY Income1.account_id;
```

---

# Why This Approach Is Correct

This solution directly follows the business rule:

1. compute monthly income using only deposit transactions
2. keep only months that exceed the maximum allowed income
3. detect whether such exceeding months occur in consecutive months
4. return the corresponding account IDs

That is exactly what the problem asks.

---

# Important Subtlety: “Exceeds” Means Strictly Greater Than

The problem says:

> total income exceeds max_income

That means the comparison must be:

```sql
SUM(t.amount) > a.max_income
```

not:

```sql
>=
```

This matters for account 4 in June, where:

```text
10400 = max_income
```

That month should not count as suspicious.

---

# Cleaner Explicit JOIN Version

The provided solution uses comma-style self-join syntax:

```sql
FROM MonthlyIncome Income1, MonthlyIncome Income2
```

A cleaner equivalent version is:

```sql
WITH MonthlyIncome AS (
  SELECT
    t.account_id,
    DATE_FORMAT(t.day, '%Y%m') AS income_month,
    SUM(t.amount) AS monthly_income,
    a.max_income
  FROM Transactions t
  JOIN Accounts a
    ON a.account_id = t.account_id
  WHERE t.type = 'Creditor'
  GROUP BY
    t.account_id,
    income_month
  HAVING SUM(t.amount) > a.max_income
)
SELECT DISTINCT
  i1.account_id
FROM MonthlyIncome i1
JOIN MonthlyIncome i2
  ON i1.account_id = i2.account_id
 AND PERIOD_DIFF(i1.income_month, i2.income_month) = 1
ORDER BY i1.account_id;
```

This is usually easier to read.

---

# Why `JOIN` Might Be Better Than `LEFT JOIN` in the CTE

In the original query, the CTE uses:

```sql
LEFT JOIN Accounts a ON a.account_id = t.account_id
```

In practice, every transaction account should exist in `Accounts`, since `max_income` is required for comparison.

So a regular `JOIN` is often more natural here.

Both work as long as referential integrity holds.

---

# Alternative Thought: Window Functions

Another possible solution style would be:

1. build the monthly exceeding-income table
2. assign sequence information with window functions
3. compare neighboring months using `LAG()` or `LEAD()`

But for this problem, the CTE + self-join + `PERIOD_DIFF()` approach is very direct and readable.

---

# Complexity Analysis

Let:

- `T` = number of transaction rows
- `M` = number of grouped monthly-income rows after aggregation

## CTE cost

The query scans creditor transactions, groups by account and month, and applies a `HAVING` filter.

## Self-join cost

The second stage compares rows within the filtered monthly table.

Since this filtered table is usually much smaller than raw transactions, the self-join is often manageable.

Overall, this is a strong approach for the problem.

---

# Final Recommended Query

```sql
WITH MonthlyIncome AS (
  SELECT
    t.account_id,
    DATE_FORMAT(t.day, '%Y%m') AS income_month,
    SUM(t.amount) AS monthly_income,
    a.max_income
  FROM Transactions t
  JOIN Accounts a
    ON a.account_id = t.account_id
  WHERE t.type = 'Creditor'
  GROUP BY
    t.account_id,
    income_month
  HAVING SUM(t.amount) > a.max_income
)
SELECT DISTINCT
  i1.account_id
FROM MonthlyIncome i1
JOIN MonthlyIncome i2
  ON i1.account_id = i2.account_id
 AND PERIOD_DIFF(i1.income_month, i2.income_month) = 1
ORDER BY i1.account_id;
```

---

# Key Takeaways

- First reduce transactions to monthly deposit totals
- Ignore `Debtor` rows completely
- Keep only months where income is strictly greater than `max_income`
- Use `PERIOD_DIFF()` to detect consecutive months
- Self-join the filtered monthly table by account to find suspicious accounts

---
