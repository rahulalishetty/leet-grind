# Median from Frequency Table — Correct Exhaustive Summary

## Problem Restatement

We are given a compressed table:

| Column      | Type |
| ----------- | ---- |
| `num`       | int  |
| `frequency` | int  |

- `num` is unique
- `frequency` tells us how many times `num` appears

If we expanded the table, we would get a sorted multiset of numbers.
We need to compute the **median** of that decompressed data and return it rounded to **one decimal place**.

---

## Example

### Input

| num | frequency |
| --- | --------: |
| 0   |         7 |
| 1   |         1 |
| 2   |         3 |
| 3   |         1 |

### Decompressed Array

```text
[0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3]
```

There are `12` values, so the median is the average of:

- 6th value
- 7th value

Both are `0`, so the answer is:

```text
0.0
```

---

# Main Challenge

We should **not actually decompress** the table.

That would be wasteful, especially if some frequencies are large.

Instead, we should reason in terms of **positions** in the decompressed sorted sequence.

For each number `num`, its frequency tells us that it occupies a continuous block of positions.

So the real problem becomes:

> Which number block contains the middle position(s)?

---

# Key Idea: Position Ranges

Suppose we process rows in ascending `num` order and compute a running prefix sum of frequencies.

For each row:

- `pref` = cumulative frequency up to and including this `num`
- `frequency` = size of this number's block

Then this number occupies positions:

```text
(pref - frequency + 1) ... pref
```

So every row corresponds to one interval in the decompressed sorted array.

---

# Median Positions

Let total number of decompressed elements be:

```text
total = SUM(frequency)
```

Then the median positions are:

```text
left_pos  = FLOOR((total + 1) / 2)
right_pos = FLOOR((total + 2) / 2)
```

This is the clean unified trick.

## Why this works

### If `total` is odd

Example: `total = 5`

```text
left_pos  = FLOOR((5 + 1) / 2) = 3
right_pos = FLOOR((5 + 2) / 2) = 3
```

Both positions collapse to the same middle position.

### If `total` is even

Example: `total = 6`

```text
left_pos  = FLOOR((6 + 1) / 2) = 3
right_pos = FLOOR((6 + 2) / 2) = 4
```

These are the two middle positions.

So we only need to find which row(s) contain these two positions, and then average their `num` values.

---

# Correct SQL Solution

## MySQL 8+ / SQL with Window Functions

```sql
WITH cte AS (
    SELECT
        num,
        frequency,
        SUM(frequency) OVER (ORDER BY num) AS pref,
        SUM(frequency) OVER () AS total
    FROM Numbers
)
SELECT ROUND(AVG(num), 1) AS median
FROM cte
WHERE (
        pref >= FLOOR((total + 1) / 2)
    AND pref - frequency < FLOOR((total + 1) / 2)
      )
   OR (
        pref >= FLOOR((total + 2) / 2)
    AND pref - frequency < FLOOR((total + 2) / 2)
      );
```

---

# Explanation of the SQL

## Step 1: Compute prefix sums

```sql
WITH cte AS (
    SELECT
        num,
        frequency,
        SUM(frequency) OVER (ORDER BY num) AS pref,
        SUM(frequency) OVER () AS total
    FROM Numbers
)
```

This gives us:

- `pref`: ending position of each number block
- `total`: total decompressed size

So if a row has:

- `num = 7`
- `frequency = 4`
- `pref = 10`

then `7` occupies positions:

```text
7, 8, 9, 10
```

because:

```text
start = pref - frequency + 1 = 10 - 4 + 1 = 7
end   = pref = 10
```

---

## Step 2: Check whether a median position lies inside the block

A row occupies:

```text
(pref - frequency + 1) ... pref
```

A target position `p` lies inside this block if:

```text
pref - frequency < p
AND
pref >= p
```

That is exactly what the query checks for both median positions.

---

## Step 3: Average the selected `num` values

```sql
SELECT ROUND(AVG(num), 1) AS median
```

This is elegant because:

- if odd length, both positions are the same → only one row is selected
- if even length and both positions fall in same block → still one row
- if even length and positions fall in different blocks → two rows are selected

In all cases, `AVG(num)` gives the correct median.

---

# Why Earlier Incorrect Conditions Fail

Two kinds of mistakes are common:

## 1. Using the wrong midpoint formula

Median positions must be handled carefully for odd and even totals.
A formula that works for one case may fail for the other.

## 2. Incorrect interval boundaries

The occupied positions are inclusive, so boundary logic matters a lot.

A row with:

- `pref = 3`
- `frequency = 1`

occupies only position `3`

not `2..3`, not `3..4`.

That means the correct membership check is:

```text
pref - frequency < p <= pref
```

not some looser approximation.

---

# Example 1: Given Sample

## Input

| num | frequency |
| --- | --------: |
| 0   |         7 |
| 1   |         1 |
| 2   |         3 |
| 3   |         1 |

## Prefix Table

| num | frequency | pref |
| --- | --------: | ---: |
| 0   |         7 |    7 |
| 1   |         1 |    8 |
| 2   |         3 |   11 |
| 3   |         1 |   12 |

## Position Ranges

| num | positions |
| --- | --------- |
| 0   | 1..7      |
| 1   | 8..8      |
| 2   | 9..11     |
| 3   | 12..12    |

## Total

```text
12
```

## Median Positions

```text
left_pos  = FLOOR((12 + 1) / 2) = 6
right_pos = FLOOR((12 + 2) / 2) = 7
```

Both positions fall in `1..7`, so the selected value is:

```text
0
```

Median:

```text
0.0
```

---

# Example 2: Important Counterexample

## Input

| num | frequency |
| --- | --------: |
| 1   |         3 |
| 2   |         3 |

## Decompressed Array

```text
[1, 1, 1, 2, 2, 2]
```

## Prefix Table

| num | frequency | pref |
| --- | --------: | ---: |
| 1   |         3 |    3 |
| 2   |         3 |    6 |

## Position Ranges

| num | positions |
| --- | --------- |
| 1   | 1..3      |
| 2   | 4..6      |

## Total

```text
6
```

## Median Positions

```text
left_pos  = FLOOR((6 + 1) / 2) = 3
right_pos = FLOOR((6 + 2) / 2) = 4
```

So:

- position 3 → `1`
- position 4 → `2`

Median:

```text
(1 + 2) / 2 = 1.5
```

This is exactly why the SQL must handle the two central positions separately.

---

# Example 3: Another Important Counterexample

## Input

| num | frequency |
| --- | --------: |
| 0   |         1 |
| 1   |         1 |
| 2   |         1 |
| 3   |         1 |
| 4   |         1 |

## Decompressed Array

```text
[0, 1, 2, 3, 4]
```

## Prefix Table

| num | frequency | pref |
| --- | --------: | ---: |
| 0   |         1 |    1 |
| 1   |         1 |    2 |
| 2   |         1 |    3 |
| 3   |         1 |    4 |
| 4   |         1 |    5 |

## Position Ranges

| num | positions |
| --- | --------- |
| 0   | 1..1      |
| 1   | 2..2      |
| 2   | 3..3      |
| 3   | 4..4      |
| 4   | 5..5      |

## Total

```text
5
```

## Median Positions

```text
left_pos  = FLOOR((5 + 1) / 2) = 3
right_pos = FLOOR((5 + 2) / 2) = 3
```

Only `num = 2` contains position `3`.

So median is:

```text
2.0
```

This example is good for verifying that odd-length inputs are handled correctly.

---

# Why `AVG(num)` Is the Right Final Step

There are only three possibilities:

## Case 1: Odd total count

Both median positions are identical, so exactly one row contributes.

Then `AVG(num)` is just that number.

## Case 2: Even total count, both positions in same block

Still only one row contributes.

Then `AVG(num)` is still just that number.

## Case 3: Even total count, positions in different blocks

Two rows contribute, one for each middle position.

Then `AVG(num)` computes:

```text
(num_left + num_right) / 2
```

which is exactly the median.

So using `AVG(num)` is both correct and compact.

---

# Complexity Analysis

Let `k` be the number of distinct rows in `Numbers`.

We never expand the full dataset.

## Time Complexity

The query sorts by `num` for the window function:

```text
O(k log k)
```

or close to linear if ordering is already supported by indexing and execution strategy.

## Space Complexity

```text
O(k)
```

for intermediate results.

This is much better than explicit decompression, which could require space proportional to the total number of values.

---

# Why Not Decompress?

A naive algorithm would be:

1. expand every number `frequency` times
2. find the middle element(s)
3. compute the median

This is conceptually easy but inefficient.

Example:

| num | frequency |
| --- | --------: |
| 100 |   1000000 |

Expanding that row would create one million copies of `100`, which is unnecessary.

The compressed form already tells us the exact positional range of the value.
That is enough to compute the median.

---

# Alternative View

Another way to say it:

A row should be selected if its number block contains one of the target median ranks.

Since each number occupies a continuous interval in sorted order, median computation becomes a rank-selection problem rather than a decompression problem.

That is the real conceptual shift.

---

# Final Recommended SQL

```sql
WITH cte AS (
    SELECT
        num,
        frequency,
        SUM(frequency) OVER (ORDER BY num) AS pref,
        SUM(frequency) OVER () AS total
    FROM Numbers
)
SELECT ROUND(AVG(num), 1) AS median
FROM cte
WHERE (
        pref >= FLOOR((total + 1) / 2)
    AND pref - frequency < FLOOR((total + 1) / 2)
      )
   OR (
        pref >= FLOOR((total + 2) / 2)
    AND pref - frequency < FLOOR((total + 2) / 2)
      );
```

---

# Minimal Interview Explanation

A concise explanation is:

1. use cumulative frequency to determine the position interval occupied by each number
2. compute the two median ranks using `(total + 1) / 2` and `(total + 2) / 2`
3. select the row(s) whose interval contains those ranks
4. average their `num` values and round to one decimal place

---

# Final Takeaways

## 1. This is a rank problem, not a decompression problem

The important thing is where each value sits in sorted order.

## 2. Prefix sums convert frequencies into position intervals

That is the core transformation.

## 3. Use two median positions

`FLOOR((total + 1) / 2)` and `FLOOR((total + 2) / 2)` cleanly handle both odd and even totals.

## 4. Use precise interval membership

A position `p` belongs to a row iff:

```text
pref - frequency < p <= pref
```

That boundary detail is critical.

## 5. `AVG(num)` elegantly handles all cases

Whether one row or two rows are selected, it returns the correct median.

---
