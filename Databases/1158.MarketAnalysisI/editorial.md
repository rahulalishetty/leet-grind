# 1158. Market Analysis I — Detailed Summary

## Approach 1: `LEFT JOIN` and Aggregation

This approach solves the problem by starting from the `Users` table and joining each user to their orders made in **2019**.

The key idea is:

- every user must appear in the output
- even users with **no orders in 2019** must still be shown
- so we use a `LEFT JOIN`
- then we count how many matching 2019 orders each user has

---

## Problem Restatement

For **each user**, return:

- their `user_id` as `buyer_id`
- their `join_date`
- the number of orders they made as a **buyer** in **2019**

Important detail:

- users with no purchases in 2019 must still appear with `0`

---

## Why `LEFT JOIN` Is the Correct Choice

If we used an `INNER JOIN`, only users who have matching rows in `Orders` would appear.

That would incorrectly exclude users who made zero orders in 2019.

A `LEFT JOIN` fixes that:

- all rows from `Users` are preserved
- matching `Orders` rows are attached when they exist
- if no matching order exists, the order columns become `NULL`

That behavior is exactly what we need.

---

## Query

```sql
SELECT
  u.user_id AS buyer_id,
  join_date,
  COUNT(o.order_id) AS orders_in_2019
FROM
  Users u
  LEFT JOIN Orders o
    ON u.user_id = o.buyer_id
   AND YEAR(order_date) = '2019'
GROUP BY
  u.user_id
ORDER BY
  u.user_id;
```

---

## Step-by-Step Intuition

## 1. Start from the `Users` table

```sql
FROM Users u
```

This is the foundation of the result.

That means:

- every user starts as part of the output
- nobody is lost just because they did not place orders

Conceptually, before joining, we have:

| user_id | join_date  | favorite_brand |
| ------: | ---------- | -------------- |
|       1 | 2018-01-01 | Lenovo         |
|       2 | 2018-02-09 | Samsung        |
|       3 | 2018-01-19 | LG             |
|       4 | 2018-05-21 | HP             |

---

## 2. Join each user with their 2019 orders

```sql
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(order_date) = '2019'
```

This join has two conditions:

### Condition A

```sql
u.user_id = o.buyer_id
```

This connects a user to orders where that user is the buyer.

### Condition B

```sql
YEAR(order_date) = '2019'
```

This keeps only orders placed in 2019.

So the join does **not** attach all orders. It attaches only the user's orders from 2019.

---

## Why the Year Filter Is Inside the `ON` Clause

This is a very important detail.

The query uses:

```sql
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(order_date) = '2019'
```

This is correct.

If instead we wrote:

```sql
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
WHERE YEAR(order_date) = '2019'
```

then users with no matching orders would be removed by the `WHERE` clause.

That would effectively turn the query into an inner join for practical purposes.

So placing the year filter inside the `ON` clause preserves users with zero orders.

---

## 3. Group by user

```sql
GROUP BY u.user_id
```

After the join, a user may be matched with:

- multiple order rows
- one order row
- no order rows

Grouping by `user_id` lets us collapse all those rows into one row per user.

That gives us one final result row per user.

---

## 4. Count the matched orders

```sql
COUNT(o.order_id) AS orders_in_2019
```

This is the core aggregation.

`COUNT(o.order_id)` counts only non-null values.

That means:

- if a user has matching 2019 orders, each one contributes to the count
- if a user has no matching 2019 orders, `o.order_id` is `NULL`, so the count is `0`

This is exactly what we want.

---

## 5. Return the desired columns

```sql
SELECT
  u.user_id AS buyer_id,
  join_date,
  COUNT(o.order_id) AS orders_in_2019
```

This gives:

- `buyer_id` → user ID
- `join_date` → from the `Users` table
- `orders_in_2019` → number of matching orders in 2019

---

## 6. Sort the output

```sql
ORDER BY u.user_id
```

The problem says the result can be returned in any order, but sorting by `user_id` gives a clean, predictable output.

---

# Example Walkthrough

## Input Tables

### Users

| user_id | join_date  | favorite_brand |
| ------: | ---------- | -------------- |
|       1 | 2018-01-01 | Lenovo         |
|       2 | 2018-02-09 | Samsung        |
|       3 | 2018-01-19 | LG             |
|       4 | 2018-05-21 | HP             |

### Orders

| order_id | order_date | item_id | buyer_id | seller_id |
| -------: | ---------- | ------: | -------: | --------: |
|        1 | 2019-08-01 |       4 |        1 |         2 |
|        2 | 2018-08-02 |       2 |        1 |         3 |
|        3 | 2019-08-03 |       3 |        2 |         3 |
|        4 | 2018-08-04 |       1 |        4 |         2 |
|        5 | 2018-08-04 |       1 |        3 |         4 |
|        6 | 2019-08-05 |       2 |        2 |         4 |

---

## Joined View Conceptually

After applying the `LEFT JOIN` with only 2019 orders, the meaningful matches look like this:

| user_id | join_date  | matched order_id |
| ------: | ---------- | ---------------: |
|       1 | 2018-01-01 |                1 |
|       2 | 2018-02-09 |                3 |
|       2 | 2018-02-09 |                6 |
|       3 | 2018-01-19 |             NULL |
|       4 | 2018-05-21 |             NULL |

Why?

- User 1 bought order `1` in 2019, but order `2` is from 2018, so it does not match
- User 2 bought orders `3` and `6` in 2019
- User 3 has no 2019 purchase
- User 4 has no 2019 purchase

---

## Grouped Result

After grouping and counting:

| buyer_id | join_date  | orders_in_2019 |
| -------: | ---------- | -------------: |
|        1 | 2018-01-01 |              1 |
|        2 | 2018-02-09 |              2 |
|        3 | 2018-01-19 |              0 |
|        4 | 2018-05-21 |              0 |

That matches the expected output.

---

# Detailed Breakdown of the SQL

## Full Query

```sql
SELECT
  u.user_id AS buyer_id,
  join_date,
  COUNT(o.order_id) AS orders_in_2019
FROM
  Users u
  LEFT JOIN Orders o
    ON u.user_id = o.buyer_id
   AND YEAR(order_date) = '2019'
GROUP BY
  u.user_id
ORDER BY
  u.user_id;
```

---

## Clause-by-Clause Explanation

### `SELECT`

```sql
SELECT
  u.user_id AS buyer_id,
  join_date,
  COUNT(o.order_id) AS orders_in_2019
```

- `u.user_id AS buyer_id`: rename the user ID to match the required output
- `join_date`: return the date the user joined
- `COUNT(o.order_id)`: count the number of matching 2019 orders

---

### `FROM`

```sql
FROM Users u
```

This ensures all users are included.

---

### `LEFT JOIN`

```sql
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(order_date) = '2019'
```

This attaches only 2019 orders made by that user as buyer.

---

### `GROUP BY`

```sql
GROUP BY u.user_id
```

This collapses multiple order rows into one output row per user.

---

### `ORDER BY`

```sql
ORDER BY u.user_id
```

Sorts results by user ID.

---

# Important SQL Subtlety: Grouping by Only `u.user_id`

In some SQL dialects, selecting `join_date` while grouping only by `u.user_id` is allowed because:

- `user_id` is the primary key
- `join_date` is functionally dependent on `user_id`

However, some SQL engines are strict and may require `join_date` to also appear in `GROUP BY`.

A more portable version is:

```sql
SELECT
  u.user_id AS buyer_id,
  u.join_date,
  COUNT(o.order_id) AS orders_in_2019
FROM Users u
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(o.order_date) = 2019
GROUP BY
  u.user_id,
  u.join_date
ORDER BY
  u.user_id;
```

This version is safer across databases.

---

# Recommended Portable Solution

```sql
SELECT
  u.user_id AS buyer_id,
  u.join_date,
  COUNT(o.order_id) AS orders_in_2019
FROM Users u
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(o.order_date) = 2019
GROUP BY
  u.user_id,
  u.join_date
ORDER BY
  u.user_id;
```

This is the version I would recommend in practice.

---

# Why `COUNT(o.order_id)` and Not `COUNT(*)`

This is another important detail.

### If we use:

```sql
COUNT(*)
```

then even users with no matching orders would get count `1`, because after a `LEFT JOIN`, the user row still exists.

### But with:

```sql
COUNT(o.order_id)
```

only non-null `order_id` values are counted.

So users with no matching orders correctly get `0`.

This is the correct choice.

---

# Why the `Items` Table Is Not Needed

The problem only asks for:

- user ID
- join date
- count of orders made as buyer in 2019

No item brand information is needed.

So the `Items` table is irrelevant for this problem.

That is worth noticing, because many SQL interview problems include extra tables that are not needed.

---

# Alternative Form Using a Pre-Aggregated Subquery

Another clean way to solve this is to first compute the count of 2019 orders per buyer, then join that back to `Users`.

## Example

```sql
SELECT
  u.user_id AS buyer_id,
  u.join_date,
  COALESCE(o.orders_in_2019, 0) AS orders_in_2019
FROM Users u
LEFT JOIN (
  SELECT
    buyer_id,
    COUNT(*) AS orders_in_2019
  FROM Orders
  WHERE YEAR(order_date) = 2019
  GROUP BY buyer_id
) o
  ON u.user_id = o.buyer_id
ORDER BY u.user_id;
```

### Why this also works

The subquery produces one row per buyer with the number of 2019 orders.

Then the outer `LEFT JOIN` ensures all users appear, and `COALESCE(..., 0)` converts missing counts to zero.

This is also a strong solution.

---

# Comparing the Two Styles

## Style 1: Direct `LEFT JOIN` + `GROUP BY`

```sql
SELECT
  u.user_id AS buyer_id,
  u.join_date,
  COUNT(o.order_id) AS orders_in_2019
FROM Users u
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(o.order_date) = 2019
GROUP BY u.user_id, u.join_date
ORDER BY u.user_id;
```

### Pros

- compact
- direct
- easy to understand once join logic is clear

### Cons

- requires careful understanding of filter placement in `ON`

---

## Style 2: Pre-Aggregate Orders, Then Join

```sql
SELECT
  u.user_id AS buyer_id,
  u.join_date,
  COALESCE(o.orders_in_2019, 0) AS orders_in_2019
FROM Users u
LEFT JOIN (
  SELECT
    buyer_id,
    COUNT(*) AS orders_in_2019
  FROM Orders
  WHERE YEAR(order_date) = 2019
  GROUP BY buyer_id
) o
  ON u.user_id = o.buyer_id
ORDER BY u.user_id;
```

### Pros

- very explicit
- separates aggregation logic from user join logic
- often easier to debug

### Cons

- slightly longer

Both are valid.

---

# Complexity Analysis

Let:

- `U` = number of users
- `O` = number of orders

## Time Complexity

For the direct join solution, roughly:

```text
O(U + O)
```

plus grouping/join overhead depending on indexing and the database engine.

In practice, performance depends on:

- indexes on `Orders(buyer_id)`
- indexes or efficient handling of `order_date`

## Space Complexity

Depends on the join/group implementation used by the database engine.

Conceptually, the query stores grouping state per user.

---

# Final Recommended Answer

```sql
SELECT
  u.user_id AS buyer_id,
  u.join_date,
  COUNT(o.order_id) AS orders_in_2019
FROM Users u
LEFT JOIN Orders o
  ON u.user_id = o.buyer_id
 AND YEAR(o.order_date) = 2019
GROUP BY
  u.user_id,
  u.join_date
ORDER BY
  u.user_id;
```

---

# Key Takeaways

- Start from `Users` because every user must appear
- Use `LEFT JOIN` so users with no 2019 orders are preserved
- Put the year filter in the `ON` clause, not `WHERE`
- Use `COUNT(o.order_id)` so unmatched users get `0`
- The `Items` table is not needed
- Group by user to compute one result row per user

---
