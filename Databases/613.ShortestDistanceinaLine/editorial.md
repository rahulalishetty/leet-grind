# 613. Shortest Distance in a Line

## Approach: Using `ABS()` and `MIN()` Functions

## Core idea

We are given a table `Point(x)` where each row is one point on the X-axis.

The distance between two points on a line is:

```text
|x1 - x2|
```

So the problem becomes:

1. generate all pairs of distinct points
2. compute the absolute difference for each pair
3. take the minimum distance

Since we are comparing rows from the same table, the natural SQL tool is a **self join**.

---

## Why a self join is needed

To compute distances, each point must be compared with other points in the same table.

That means we need two copies of the table:

- one copy as `p1`
- another copy as `p2`

Then the distance between two points is:

```sql
ABS(p1.x - p2.x)
```

---

## Step 1: Generate all pairs of different points

A straightforward way is:

```sql
SELECT
    p1.x,
    p2.x,
    ABS(p1.x - p2.x) AS distance
FROM
    point p1
    JOIN point p2
        ON p1.x != p2.x;
```

### Why `p1.x != p2.x`?

Because we do **not** want to compare a point with itself.

If we did compare a point with itself, the distance would be:

```text
|x - x| = 0
```

and then the minimum distance would always become `0`, which would be incorrect.

So this condition excludes self-pairs.

---

## Example walkthrough

### Input

| x   |
| --- |
| -1  |
| 0   |
| 2   |

Running the pair-generation query:

```sql
SELECT
    p1.x,
    p2.x,
    ABS(p1.x - p2.x) AS distance
FROM
    point p1
    JOIN point p2
        ON p1.x != p2.x;
```

produces:

| p1.x | p2.x | distance |
| ---- | ---- | -------- |
| 0    | -1   | 1        |
| 2    | -1   | 3        |
| -1   | 0    | 1        |
| 2    | 0    | 2        |
| -1   | 2    | 3        |
| 0    | 2    | 2        |

---

## Important observation

You can see that many comparisons appear twice:

- `(0, -1)` and `(-1, 0)`
- `(2, -1)` and `(-1, 2)`
- `(2, 0)` and `(0, 2)`

That is fine for this approach, because although there is duplicated work, the minimum distance is still correct.

We only care about the smallest value in the distance column.

---

## Step 2: Take the minimum distance

Once all pairwise distances are computed, the answer is simply:

```sql
MIN(ABS(p1.x - p2.x))
```

So the final query is:

```sql
SELECT
    MIN(ABS(p1.x - p2.x)) AS shortest
FROM
    point p1
    JOIN point p2
        ON p1.x != p2.x;
```

---

## Final accepted query

```sql
SELECT
    MIN(ABS(p1.x - p2.x)) AS shortest
FROM
    point p1
    JOIN point p2
        ON p1.x != p2.x;
```

---

## Full explanation of the final query

### `FROM point p1 JOIN point p2`

This creates two logical copies of the same table so every point can be compared with every other point.

### `ON p1.x != p2.x`

This excludes self-comparisons.

### `ABS(p1.x - p2.x)`

Distance on a line must always be nonnegative, so we use absolute value.

### `MIN(...)`

Among all computed distances, we take the smallest one.

### `AS shortest`

This names the output column exactly as required.

---

## Why `ABS()` is necessary

Without `ABS()`, subtraction could produce negative values depending on the order of the two points.

For example:

```text
-1 - 2 = -3
2 - (-1) = 3
```

But distance cannot be negative.

So we must use:

```sql
ABS(p1.x - p2.x)
```

to ensure the value is always nonnegative.

---

## Why the solution works

Every valid pair of distinct points is included in the self join.

For each pair, we compute the distance.

Then `MIN(...)` gives the smallest distance among all such pairs.

That matches the problem definition exactly.

---

## Output for the sample

Input points:

```text
-1, 0, 2
```

Distances:

- `|-1 - 0| = 1`
- `|-1 - 2| = 3`
- `|0 - 2| = 2`

Minimum:

```text
1
```

So the output is:

| shortest |
| -------- |
| 1        |

---

## Follow-up intuition: what if the table is sorted?

The follow-up asks how to optimize if the points are already ordered in ascending order.

A skeptical observation helps here:

If points are sorted, the shortest distance must occur between **adjacent points**, not arbitrary far-apart points.

For example, if the sorted points are:

```text
-1, 0, 2
```

you only need to compare:

- `0 - (-1) = 1`
- `2 - 0 = 2`

The minimum of adjacent differences is the global minimum.

That avoids the full self join and can be much more efficient.

The accepted solution you provided does not implement that optimization, but this is the key mathematical insight behind the follow-up.

---

## Complexity

Let `n` be the number of rows in `Point`.

### Time Complexity

Because the self join compares each point with every other point, the work is conceptually:

```text
O(n^2)
```

### Space Complexity

Ignoring the output and internal engine details, extra space is conceptually:

```text
O(1)
```

for the query expression itself.

---

## Strengths and limitation of this approach

### Strengths

- simple
- direct
- easy to understand
- matches the mathematical definition cleanly

### Limitation

- computes duplicated comparisons
- not optimal for large datasets
- does not use the sorted-order optimization from the follow-up

---

## Key takeaways

1. The distance between two points on a line is `ABS(x1 - x2)`.
2. A self join is used to compare every point with every other point.
3. `p1.x != p2.x` avoids comparing a point with itself.
4. `MIN(...)` selects the shortest distance among all pairs.
5. The final accepted query is:

```sql
SELECT
    MIN(ABS(p1.x - p2.x)) AS shortest
FROM
    point p1
    JOIN point p2
        ON p1.x != p2.x;
```
