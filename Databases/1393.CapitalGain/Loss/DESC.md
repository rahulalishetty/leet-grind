# 1393. Capital Gain/Loss

## Table: Stocks

| Column Name   | Type    |
| ------------- | ------- |
| stock_name    | varchar |
| operation     | enum    |
| operation_day | int     |
| price         | int     |

**Notes:**

- `(stock_name, operation_day)` is the **primary key** (unique combination).
- `operation` is an ENUM with values:
  - `'Sell'`
  - `'Buy'`
- Each row represents a **stock operation** performed on a given day at a given price.

Additional guarantees:

- Every **Sell** operation has a corresponding **Buy** operation on a **previous day**.
- Every **Buy** operation has a corresponding **Sell** operation on a **later day**.

---

# Problem

Write a SQL query to compute the **capital gain or loss for each stock**.

### Capital Gain/Loss Definition

The **capital gain/loss** of a stock is:

```
Sum of (Sell price − Buy price)
```

for all matched **Buy → Sell** transactions.

If the result is positive → **gain**.
If the result is negative → **loss**.

Return the result table in **any order**.

---

# Example

## Input

### Stocks Table

| stock_name   | operation | operation_day | price |
| ------------ | --------- | ------------- | ----- |
| Leetcode     | Buy       | 1             | 1000  |
| Corona Masks | Buy       | 2             | 10    |
| Leetcode     | Sell      | 5             | 9000  |
| Handbags     | Buy       | 17            | 30000 |
| Corona Masks | Sell      | 3             | 1010  |
| Corona Masks | Buy       | 4             | 1000  |
| Corona Masks | Sell      | 5             | 500   |
| Corona Masks | Buy       | 6             | 1000  |
| Handbags     | Sell      | 29            | 7000  |
| Corona Masks | Sell      | 10            | 10000 |

---

# Output

| stock_name   | capital_gain_loss |
| ------------ | ----------------- |
| Corona Masks | 9500              |
| Leetcode     | 8000              |
| Handbags     | -23000            |

---

# Explanation

### Leetcode

Operations:

| Day | Operation | Price |
| --- | --------- | ----- |
| 1   | Buy       | 1000  |
| 5   | Sell      | 9000  |

Capital gain:

```
9000 - 1000 = 8000
```

---

### Handbags

Operations:

| Day | Operation | Price |
| --- | --------- | ----- |
| 17  | Buy       | 30000 |
| 29  | Sell      | 7000  |

Capital loss:

```
7000 - 30000 = -23000
```

---

### Corona Masks

Operations:

| Day | Operation | Price |
| --- | --------- | ----- |
| 2   | Buy       | 10    |
| 3   | Sell      | 1010  |
| 4   | Buy       | 1000  |
| 5   | Sell      | 500   |
| 6   | Buy       | 1000  |
| 10  | Sell      | 10000 |

Capital gain/loss calculation:

```
(1010 - 10) + (500 - 1000) + (10000 - 1000)
= 1000 - 500 + 9000
= 9500
```

---
