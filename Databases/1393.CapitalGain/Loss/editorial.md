# 1393. Capital Gain/Loss — Detailed Summary

## Approach: `GROUP BY` and Aggregation

This approach solves the problem by treating:

- each **Buy** as a negative cash flow
- each **Sell** as a positive cash flow

Then, for each stock, we sum all those values.

That final sum is exactly the stock's total capital gain or loss.

---

## Core Idea

For any stock transaction pair:

```text
gain/loss = Sell price - Buy price
```

If a stock is traded multiple times, then the total capital gain/loss is:

```text
(sum of all Sell prices) - (sum of all Buy prices)
```

That can be rewritten as:

```text
sum(+Sell prices, -Buy prices)
```

This is why the query uses a `CASE` expression inside `SUM()`:

- `Buy` → subtract price
- `Sell` → add price

---

## Why This Works

The problem guarantees:

- every `Buy` has a matching later `Sell`
- every `Sell` has a matching earlier `Buy`

So for each stock, the total result can be computed safely by simply summing:

- `-price` for buys
- `+price` for sells

We do **not** need to manually pair individual buy and sell rows.

That is the crucial simplification.

---

## Query

```sql
SELECT
    stock_name,
    SUM(
        CASE
            WHEN operation = 'buy' THEN -price
            WHEN operation = 'sell' THEN price
        END
    ) AS capital_gain_loss
FROM Stocks
GROUP BY stock_name;
```

---

## Step-by-Step Explanation

## 1. Group rows by stock

```sql
GROUP BY stock_name
```

This creates one group per stock.

So all operations for:

- `Leetcode`
- `Corona Masks`
- `Handbags`

are grouped separately.

That is necessary because we want one final capital gain/loss value for each stock.

---

## 2. Convert each row into a signed contribution

Inside the `SUM()`:

```sql
CASE
    WHEN operation = 'buy' THEN -price
    WHEN operation = 'sell' THEN price
END
```

This transforms each row as follows:

### If operation is `Buy`

A buy means money is spent.

So it should reduce profit.

That is why we convert:

```sql
Buy -> -price
```

### If operation is `Sell`

A sell means money is received.

So it should increase profit.

That is why we convert:

```sql
Sell -> +price
```

---

## 3. Sum all contributions within each stock group

```sql
SUM(...)
```

Once every row has been converted to a signed value, summing them gives the total gain or loss for that stock.

---

# Worked Example

## Input

| stock_name   | operation | operation_day | price |
| ------------ | --------- | ------------: | ----: |
| Leetcode     | Buy       |             1 |  1000 |
| Corona Masks | Buy       |             2 |    10 |
| Leetcode     | Sell      |             5 |  9000 |
| Handbags     | Buy       |            17 | 30000 |
| Corona Masks | Sell      |             3 |  1010 |
| Corona Masks | Buy       |             4 |  1000 |
| Corona Masks | Sell      |             5 |   500 |
| Corona Masks | Buy       |             6 |  1000 |
| Handbags     | Sell      |            29 |  7000 |
| Corona Masks | Sell      |            10 | 10000 |

---

## Example 1: Leetcode

Rows:

| operation | price | signed value |
| --------- | ----: | -----------: |
| Buy       |  1000 |        -1000 |
| Sell      |  9000 |        +9000 |

Sum:

```text
-1000 + 9000 = 8000
```

So:

| stock_name | capital_gain_loss |
| ---------- | ----------------: |
| Leetcode   |              8000 |

---

## Example 2: Handbags

Rows:

| operation | price | signed value |
| --------- | ----: | -----------: |
| Buy       | 30000 |       -30000 |
| Sell      |  7000 |        +7000 |

Sum:

```text
-30000 + 7000 = -23000
```

So:

| stock_name | capital_gain_loss |
| ---------- | ----------------: |
| Handbags   |            -23000 |

---

## Example 3: Corona Masks

Rows:

| operation | price | signed value |
| --------- | ----: | -----------: |
| Buy       |    10 |          -10 |
| Sell      |  1010 |        +1010 |
| Buy       |  1000 |        -1000 |
| Sell      |   500 |         +500 |
| Buy       |  1000 |        -1000 |
| Sell      | 10000 |       +10000 |

Sum:

```text
-10 + 1010 - 1000 + 500 - 1000 + 10000
= 9500
```

So:

| stock_name   | capital_gain_loss |
| ------------ | ----------------: |
| Corona Masks |              9500 |

---

# Final Output

| stock_name   | capital_gain_loss |
| ------------ | ----------------: |
| Corona Masks |              9500 |
| Leetcode     |              8000 |
| Handbags     |            -23000 |

---

# Why Explicit Pairing Is Not Needed

A natural question is:

> Should we match each Buy with its corresponding Sell?

In this problem, the answer is no.

Because addition is associative, the total:

```text
(sell1 - buy1) + (sell2 - buy2) + ...
```

is the same as:

```text
(sum of sells) - (sum of buys)
```

So as long as all buys and sells belong to the same stock and the problem guarantees valid matching overall, simply summing signed prices is enough.

That makes this solution much simpler than constructing explicit Buy → Sell pairs.

---

# Clause-by-Clause Breakdown

## `SELECT stock_name`

```sql
SELECT stock_name
```

Returns the stock identifier for each grouped result.

---

## `SUM(CASE ...)`

```sql
SUM(
    CASE
        WHEN operation = 'buy' THEN -price
        WHEN operation = 'sell' THEN price
    END
)
```

Converts each operation into a signed number, then adds them all.

---

## `AS capital_gain_loss`

```sql
AS capital_gain_loss
```

Names the final computed column.

---

## `FROM Stocks`

```sql
FROM Stocks
```

Reads data from the `Stocks` table.

---

## `GROUP BY stock_name`

```sql
GROUP BY stock_name
```

Ensures one result row per stock.

---

# Important Note About Enum Values

The problem statement lists operation values as:

- `'Sell'`
- `'Buy'`

But the query uses:

```sql
WHEN operation = 'buy'
WHEN operation = 'sell'
```

Whether this works depends on the SQL engine's case sensitivity rules.

A safer version is to match the exact casing from the problem:

```sql
SELECT
    stock_name,
    SUM(
        CASE
            WHEN operation = 'Buy' THEN -price
            WHEN operation = 'Sell' THEN price
        END
    ) AS capital_gain_loss
FROM Stocks
GROUP BY stock_name;
```

This version is more portable and safer.

---

# Recommended Final Query

```sql
SELECT
    stock_name,
    SUM(
        CASE
            WHEN operation = 'Buy' THEN -price
            WHEN operation = 'Sell' THEN price
        END
    ) AS capital_gain_loss
FROM Stocks
GROUP BY stock_name;
```

---

# Alternative Equivalent Form

Some people prefer to write the same logic with the positive case first:

```sql
SELECT
    stock_name,
    SUM(
        CASE
            WHEN operation = 'Sell' THEN price
            ELSE -price
        END
    ) AS capital_gain_loss
FROM Stocks
GROUP BY stock_name;
```

This works too, assuming the only two operation values are `Buy` and `Sell`.

Because the problem guarantees exactly those two values, this is valid.

Still, the more explicit two-branch version is easier to read.

---

# Complexity Analysis

Let `n` be the number of rows in `Stocks`.

## Time Complexity

```text
O(n)
```

We scan each row once and aggregate by `stock_name`.

In practice, grouping cost depends on the database engine, but conceptually this is linear in the number of rows.

## Space Complexity

Depends on the number of distinct stocks.

Conceptually:

```text
O(k)
```

where `k` is the number of distinct `stock_name` values, because one aggregate value is maintained per stock.

---

# Why This Approach Is Elegant

This solution is elegant because it turns a transaction-matching problem into a simple aggregation problem.

Instead of reasoning about individual buy/sell pairs, it uses a financial interpretation:

- buying spends money → negative
- selling earns money → positive

Then ordinary summation gives the answer.

That makes the SQL short, efficient, and easy to verify.

---

# Final Code Example

```sql
SELECT
    stock_name,
    SUM(
        CASE
            WHEN operation = 'Buy' THEN -price
            WHEN operation = 'Sell' THEN price
        END
    ) AS capital_gain_loss
FROM Stocks
GROUP BY stock_name;
```

---

# Key Takeaways

- Group by `stock_name`
- Treat each `Buy` as `-price`
- Treat each `Sell` as `+price`
- Sum all signed values per stock
- Exact Buy → Sell pairing is unnecessary because total gain/loss equals total sells minus total buys

---
