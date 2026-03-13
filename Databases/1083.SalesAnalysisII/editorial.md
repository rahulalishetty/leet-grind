# 1083. Sales Analysis II — Detailed Summary

## Problem Recap

We need to return the `buyer_id` values for buyers who:

- bought **`S8`**
- but **did not buy `iPhone`**

The relevant tables are:

### `Product`

| Column         | Meaning                   |
| -------------- | ------------------------- |
| `product_id`   | Unique product identifier |
| `product_name` | Name of the product       |
| `unit_price`   | Listed unit price         |

### `Sales`

| Column       | Meaning                       |
| ------------ | ----------------------------- |
| `seller_id`  | Seller identifier             |
| `product_id` | Product sold                  |
| `buyer_id`   | Buyer identifier              |
| `sale_date`  | Date of sale                  |
| `quantity`   | Quantity sold                 |
| `price`      | Total price for that sale row |

The core challenge is:

- identify buyers who belong to the **wanted group**: buyers who purchased `S8`
- remove buyers who belong to the **unwanted group**: buyers who purchased `iPhone`

---

## Key Observation

This is a classic **set difference** problem.

You can think of it as:

```text
buyers_who_bought_S8
MINUS
buyers_who_bought_iPhone
```

That is why several SQL patterns work naturally here:

1. `NOT IN` / `NOT EXISTS`
2. `LEFT JOIN ... IS NULL`
3. `GROUP BY ... HAVING` with conditional logic

All three are valid. The best choice usually depends on readability, SQL dialect, and personal preference.

---

## Example

### Input

### `Product`

| product_id | product_name | unit_price |
| ---------- | -----------: | ---------: |
| 1          |           S8 |       1000 |
| 2          |           G4 |        800 |
| 3          |       iPhone |       1400 |

### `Sales`

| seller_id | product_id | buyer_id | sale_date  | quantity | price |
| --------: | ---------: | -------: | ---------- | -------: | ----: |
|         1 |          1 |        1 | 2019-01-21 |        2 |  2000 |
|         1 |          2 |        2 | 2019-02-17 |        1 |   800 |
|         2 |          1 |        3 | 2019-06-02 |        1 |   800 |
|         3 |          3 |        3 | 2019-05-13 |        2 |  2800 |

### Interpretation

- Buyer `1` bought `S8` and never bought `iPhone` → include
- Buyer `2` bought neither `S8` nor `iPhone` → exclude
- Buyer `3` bought both `S8` and `iPhone` → exclude

### Output

| buyer_id |
| -------: |
|        1 |

---

# Approach 1: `NOT IN` / `NOT EXISTS`

## Intuition

This is the most direct way to think about the problem.

First:

- find all buyers who bought `iPhone` → unwanted buyers

Then:

- find all buyers who bought `S8`
- exclude any buyer present in the unwanted group

So the logic is:

```text
Wanted buyers
= S8 buyers
  excluding iPhone buyers
```

---

## Step 1: Find buyers who bought `iPhone`

```sql
SELECT DISTINCT buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'iPhone';
```

### What this does

- joins `Sales` with `Product` so we can use `product_name`
- filters rows to only `iPhone`
- returns distinct buyers who purchased it

---

## Step 2: Find buyers who bought `S8`

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8';
```

### What this does

- finds all buyers who purchased `S8`
- uses `DISTINCT` so repeated purchases do not duplicate the result

---

## Final Query Using `NOT IN`

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8'
WHERE s.buyer_id NOT IN (
    SELECT DISTINCT s2.buyer_id
    FROM Sales s2
    JOIN Product p2
      ON s2.product_id = p2.product_id
     AND p2.product_name = 'iPhone'
);
```

---

## Why this works

The outer query gives all `S8` buyers.

The subquery gives all `iPhone` buyers.

`NOT IN` removes the overlapping buyers, leaving only buyers who bought `S8` but not `iPhone`.

---

## Safer Variant Using `NOT EXISTS`

`NOT EXISTS` is often preferred because `NOT IN` can behave unexpectedly if the subquery returns `NULL`.

In this problem, `buyer_id` is never `NULL`, so `NOT IN` is safe. Still, `NOT EXISTS` is often considered more robust in general SQL practice.

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8'
WHERE NOT EXISTS (
    SELECT 1
    FROM Sales s2
    JOIN Product p2
      ON s2.product_id = p2.product_id
     AND p2.product_name = 'iPhone'
    WHERE s2.buyer_id = s.buyer_id
);
```

---

## Pros

- very intuitive
- closely matches the problem statement
- easy to explain

## Cons

- `NOT IN` can be risky in some problems if `NULL` values are possible
- some people find correlated subqueries slightly less readable than join-based solutions

---

# Approach 2: `LEFT JOIN` + `IS NULL`

## Intuition

This is another standard anti-join pattern.

Idea:

- put the wanted buyers (`S8` buyers) on the left side
- put the unwanted buyers (`iPhone` buyers) on the right side
- left join them on `buyer_id`
- keep only the rows where the right side did not match

If a buyer appears in the unwanted group, the right side will have a value.

If a buyer does not appear there, the joined columns from the right side become `NULL`.

That lets us keep only buyers who bought `S8` and did not buy `iPhone`.

---

## Step 1: Build the unwanted group

```sql
SELECT DISTINCT buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'iPhone';
```

---

## Step 2: Start from `S8` buyers

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8';
```

---

## Final Query

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8'
LEFT JOIN (
    SELECT DISTINCT s2.buyer_id
    FROM Sales s2
    JOIN Product p2
      ON s2.product_id = p2.product_id
     AND p2.product_name = 'iPhone'
) a
  ON s.buyer_id = a.buyer_id
WHERE a.buyer_id IS NULL;
```

---

## Why this works

- the left side contains all buyers who bought `S8`
- the subquery `a` contains all buyers who bought `iPhone`
- if a buyer exists in both groups, the join succeeds
- if a buyer exists only in the left side, `a.buyer_id` becomes `NULL`
- filtering with `WHERE a.buyer_id IS NULL` keeps only those left-only buyers

This is effectively the SQL version of a set difference.

---

## Pros

- a very common and readable anti-join pattern
- avoids the `NULL` concerns often associated with `NOT IN`
- often easy to optimize and reason about

## Cons

- slightly more verbose than the first approach
- requires an extra derived table / subquery

---

# Approach 3: `GROUP BY` + scoring each buyer

## Intuition

Instead of building separate wanted and unwanted groups, this approach groups every buyer’s purchases together and then asks:

- did this buyer ever buy `S8`?
- did this buyer ever buy `iPhone`?

If the answer is:

- `S8` > 0
- `iPhone` = 0

then the buyer qualifies.

This is a very elegant way to solve problems that ask whether a grouped entity contains or excludes certain values.

---

## Base grouped query

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id;
```

This groups all purchase rows by buyer.

Now we can write conditions in the `HAVING` clause, because `HAVING` filters groups after aggregation.

---

## Variant 3A: `CASE WHEN` scoring

### Idea

Convert each product into a score:

- add `1` when the product is `iPhone`
- add `1` when the product is `S8`

Then inspect the totals for each buyer.

A buyer qualifies only when:

- iPhone score = 0
- S8 score > 0

---

## Final Query Using `CASE WHEN`

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id
HAVING SUM(CASE WHEN p.product_name = 'iPhone' THEN 1 ELSE 0 END) = 0
   AND SUM(CASE WHEN p.product_name = 'S8' THEN 1 ELSE 0 END) > 0;
```

---

## Why this works

For each buyer:

- every `iPhone` purchase contributes `1` to the iPhone sum
- every non-iPhone purchase contributes `0`

So:

```sql
SUM(CASE WHEN p.product_name = 'iPhone' THEN 1 ELSE 0 END)
```

counts how many purchase rows for that buyer involved `iPhone`.

Similarly:

```sql
SUM(CASE WHEN p.product_name = 'S8' THEN 1 ELSE 0 END)
```

counts how many purchase rows involved `S8`.

Then:

- `= 0` means the buyer never bought `iPhone`
- `> 0` means the buyer bought `S8` at least once

---

## Important note

You do **not** need `DISTINCT` here, because `GROUP BY s.buyer_id` already produces one row per buyer.

A cleaner version is:

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id
HAVING SUM(CASE WHEN p.product_name = 'iPhone' THEN 1 ELSE 0 END) = 0
   AND SUM(CASE WHEN p.product_name = 'S8' THEN 1 ELSE 0 END) > 0;
```

---

## Pros

- very expressive
- scales nicely to more complex conditions
- excellent pattern for “has X but not Y” across grouped entities

## Cons

- slightly less obvious to beginners than a direct anti-join
- requires comfort with `GROUP BY` and `HAVING`

---

## Variant 3B: `GROUP_CONCAT`

### Idea

Instead of converting products to numeric scores, combine all product names for each buyer into one string and then search within that string.

Example grouped value:

```text
'S8,G4,S8'
```

Then check:

- string contains `S8`
- string does not contain `iPhone`

---

## Final Query Using `GROUP_CONCAT`

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id
HAVING GROUP_CONCAT(p.product_name) LIKE '%S8%'
   AND GROUP_CONCAT(p.product_name) NOT LIKE '%iPhone%';
```

---

## Why this works

`GROUP_CONCAT(p.product_name)` builds one combined string of product names per buyer.

Then the `HAVING` clause checks text patterns inside that grouped string.

---

## Caveat

This approach works, but it is usually less robust than `CASE WHEN` because:

- it depends on string matching
- it is more dialect-specific
- it can become messy for larger or more precise filtering logic
- accidental substring collisions are possible in other problems

For example, in a different problem, matching text with `LIKE '%phone%'` could be dangerous if multiple product names contain that substring.

So while this approach is clever, `CASE WHEN` is usually the better grouped solution.

---

# Comparing the Approaches

## 1. `NOT IN` / `NOT EXISTS`

Best when you want the logic to mirror:

```text
Take S8 buyers and remove iPhone buyers
```

This is often the first approach people think of.

---

## 2. `LEFT JOIN ... IS NULL`

Best when you like anti-join style solutions.

This is one of the most common SQL exclusion patterns.

---

## 3. `GROUP BY ... HAVING`

Best when you want to reason per buyer in aggregated form.

This becomes especially useful when the problem grows into something like:

- bought A and B but not C
- bought at least 2 products from category X
- never bought product Y after a certain date

In those cases, conditional aggregation is often the most flexible tool.

---

# Recommended Solution

The cleanest choices are usually:

## Recommended Option A: `NOT EXISTS`

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8'
WHERE NOT EXISTS (
    SELECT 1
    FROM Sales s2
    JOIN Product p2
      ON s2.product_id = p2.product_id
     AND p2.product_name = 'iPhone'
    WHERE s2.buyer_id = s.buyer_id
);
```

### Why recommend this

- direct
- readable
- robust
- avoids common `NOT IN` pitfalls in more general cases

---

## Recommended Option B: conditional aggregation

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id
HAVING SUM(CASE WHEN p.product_name = 'iPhone' THEN 1 ELSE 0 END) = 0
   AND SUM(CASE WHEN p.product_name = 'S8' THEN 1 ELSE 0 END) > 0;
```

### Why recommend this

- elegant
- powerful
- very reusable for similar interview problems

---

# Small Refinements and Best Practices

## 1. Put non-join filters in `WHERE` when appropriate

You may see this style:

```sql
JOIN Product p
  ON s.product_id = p.product_id
 AND p.product_name = 'S8'
```

This works, but many people prefer separating join logic from filtering logic for readability:

```sql
JOIN Product p
  ON s.product_id = p.product_id
WHERE p.product_name = 'S8'
```

Both are valid here for an inner join.

Example:

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
WHERE p.product_name = 'S8'
  AND s.buyer_id NOT IN (
      SELECT DISTINCT s2.buyer_id
      FROM Sales s2
      JOIN Product p2
        ON s2.product_id = p2.product_id
      WHERE p2.product_name = 'iPhone'
  );
```

---

## 2. `DISTINCT` vs `GROUP BY`

- If you are selecting raw rows and want unique buyer IDs, use `DISTINCT`
- If you are aggregating per buyer, `GROUP BY` already gives one row per buyer

So in grouped solutions, `DISTINCT` is usually unnecessary.

---

## 3. Duplicate sales rows do not break these approaches

The problem states that `Sales` may contain repeated rows.

That is fine:

- `DISTINCT` prevents duplicate buyer output in non-grouped solutions
- `GROUP BY` naturally collapses all rows per buyer in grouped solutions

Even if a buyer bought `S8` many times, they should still appear only once in the answer.

---

# Final Takeaway

This problem is fundamentally about **excluding one set of buyers from another**.

There are three standard SQL ways to express that:

1. **Subquery exclusion**
   Use `NOT IN` or `NOT EXISTS`

2. **Anti-join**
   Use `LEFT JOIN ... IS NULL`

3. **Conditional aggregation**
   Use `GROUP BY ... HAVING`

For interview preparation and long-term SQL fluency, it is worth becoming comfortable with all three, because they appear repeatedly in different forms.

---

# Final Code Collection

## Solution 1 — `NOT IN`

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
WHERE p.product_name = 'S8'
  AND s.buyer_id NOT IN (
      SELECT DISTINCT s2.buyer_id
      FROM Sales s2
      JOIN Product p2
        ON s2.product_id = p2.product_id
      WHERE p2.product_name = 'iPhone'
  );
```

## Solution 2 — `NOT EXISTS`

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
WHERE p.product_name = 'S8'
  AND NOT EXISTS (
      SELECT 1
      FROM Sales s2
      JOIN Product p2
        ON s2.product_id = p2.product_id
      WHERE p2.product_name = 'iPhone'
        AND s2.buyer_id = s.buyer_id
  );
```

## Solution 3 — `LEFT JOIN`

```sql
SELECT DISTINCT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
LEFT JOIN (
    SELECT DISTINCT s2.buyer_id
    FROM Sales s2
    JOIN Product p2
      ON s2.product_id = p2.product_id
    WHERE p2.product_name = 'iPhone'
) a
  ON s.buyer_id = a.buyer_id
WHERE p.product_name = 'S8'
  AND a.buyer_id IS NULL;
```

## Solution 4 — `CASE WHEN`

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id
HAVING SUM(CASE WHEN p.product_name = 'iPhone' THEN 1 ELSE 0 END) = 0
   AND SUM(CASE WHEN p.product_name = 'S8' THEN 1 ELSE 0 END) > 0;
```

## Solution 5 — `GROUP_CONCAT`

```sql
SELECT s.buyer_id
FROM Sales s
JOIN Product p
  ON s.product_id = p.product_id
GROUP BY s.buyer_id
HAVING GROUP_CONCAT(p.product_name) LIKE '%S8%'
   AND GROUP_CONCAT(p.product_name) NOT LIKE '%iPhone%';
```
