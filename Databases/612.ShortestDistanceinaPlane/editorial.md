# 612. Shortest Distance in a Plane

## Detailed Summary of Two Accepted Approaches

We are given a table `Point2D(x, y)` where each row is a point on the plane.

The task is to compute the **shortest Euclidean distance** between **any two distinct points** and return it rounded to **2 decimal places**.

The Euclidean distance between two points:

```text
P1(x1, y1), P2(x2, y2)
```

is:

```text
sqrt((x1 - x2)^2 + (y1 - y2)^2)
```

So the overall strategy is:

1. generate pairs of points
2. compute the distance for each pair
3. take the minimum
4. round to 2 decimals

---

# Core observation

This is a same-table pair-comparison problem.

To compare every point against every other point, the standard SQL pattern is a **self join**.

That means:

- one copy of the table is treated as `p1`
- another copy is treated as `p2`

Then we compute the distance between `p1` and `p2`.

The most important detail is this:

> we must not compare a point with itself

because the distance from a point to itself is always `0`, and then the minimum would always be zero.

---

# Distance formula in SQL

The squared Euclidean distance is:

```sql
POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)
```

The actual distance is the square root of that:

```sql
SQRT(POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2))
```

So every solution in this problem is built on that formula.

---

# Example input

| x   | y   |
| --- | --- |
| -1  | -1  |
| 0   | 0   |
| -1  | -2  |

Possible point pairs:

- `(-1, -1)` and `(0, 0)`
- `(-1, -1)` and `(-1, -2)`
- `(0, 0)` and `(-1, -2)`

Distances:

1. Between `(-1, -1)` and `(0, 0)`:

```text
sqrt((−1−0)^2 + (−1−0)^2)
= sqrt(1 + 1)
= sqrt(2)
≈ 1.41
```

2. Between `(-1, -1)` and `(-1, -2)`:

```text
sqrt((−1−(−1))^2 + (−1−(−2))^2)
= sqrt(0 + 1)
= 1
```

3. Between `(0, 0)` and `(-1, -2)`:

```text
sqrt((0−(−1))^2 + (0−(−2))^2)
= sqrt(1 + 4)
= sqrt(5)
≈ 2.24
```

The minimum is:

```text
1.00
```

---

# Approach 1: Using `SQRT`, `POW()`, and self join

## Core idea

Join the table with itself, compute the distance between all distinct pairs of points, and then choose the minimum.

This is the most direct approach.

---

## Step 1: Join the table with itself

```sql
SELECT
    p1.x,
    p1.y,
    p2.x,
    p2.y,
    SQRT((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2))) AS distance
FROM
    point_2d p1
    JOIN point_2d p2
        ON p1.x != p2.x OR p1.y != p2.y;
```

### Why this join condition?

```sql
p1.x != p2.x OR p1.y != p2.y
```

This is used to make sure that `p1` and `p2` are not the same point.

If both x and y were equal, then we would be comparing a point with itself.

That would produce distance:

```text
0
```

and then the minimum would always be zero, which would be wrong.

So this condition filters out self-pairs.

---

## Output of the full pair comparison

For the sample input, the joined rows produce distances like:

| p1.x | p1.y | p2.x | p2.y | distance           |
| ---- | ---- | ---- | ---- | ------------------ |
| 0    | 0    | -1   | -1   | 1.4142135623730951 |
| -1   | -2   | -1   | -1   | 1                  |
| -1   | -1   | 0    | 0    | 1.4142135623730951 |
| -1   | -2   | 0    | 0    | 2.23606797749979   |
| -1   | -1   | -1   | -2   | 1                  |
| 0    | 0    | -1   | -2   | 2.23606797749979   |

Notice that many distances are duplicated:

- distance from `A` to `B`
- distance from `B` to `A`

Both appear.

That is acceptable in this first approach because we only care about the minimum.

---

## Step 2: Take the minimum distance

Now wrap the distance computation in `MIN(...)` and round the result.

```sql
SELECT
    ROUND(
        SQRT(MIN((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)))),
        2
    ) AS shortest
FROM
    point_2d p1
    JOIN point_2d p2
        ON p1.x != p2.x OR p1.y != p2.y;
```

---

## Why `MIN(...)` is inside `SQRT(...)`

The solution notes that placing `MIN()` inside `SQRT()` slightly improves performance.

That is because the square root function is monotonic for non-negative values:

- if `a < b`, then `sqrt(a) < sqrt(b)`

So instead of:

```sql
MIN(SQRT(...))
```

we can do:

```sql
SQRT(MIN(...))
```

and get the same result.

This avoids applying `SQRT()` to every distance before taking the minimum.

That is a small but sensible optimization.

---

## Final query for Approach 1

```sql
SELECT
    ROUND(SQRT(MIN((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)))), 2) AS shortest
FROM
    point_2d p1
    JOIN point_2d p2
        ON p1.x != p2.x OR p1.y != p2.y;
```

---

## Strengths of Approach 1

- direct
- easy to understand
- closely follows the mathematical formula

### Tradeoff

It calculates both:

- distance from `A` to `B`
- distance from `B` to `A`

So it performs duplicate work.

---

# Approach 2: Optimize to avoid reduplicate calculations

## Core idea

In Approach 1, every pair is effectively computed twice:

- once as `(p1, p2)`
- once as `(p2, p1)`

That repetition is unnecessary.

So the optimization idea is:

> compare each pair only in one fixed directional rule

This reduces duplicate calculations.

---

## Intuition behind the ordering rule

If we force one point to come “before” the other according to some ordering rule, then each unordered pair is computed only once.

The provided solution uses conditions like:

```sql
(p1.x <= p2.x AND p1.y < p2.y)
OR (p1.x <= p2.x AND p1.y > p2.y)
OR (p1.x < p2.x AND p1.y = p2.y)
```

This is a way to ensure that only one orientation of a point pair is selected.

The goal is to reduce repeated comparisons while still covering all distinct pairs.

---

## Query for the optimized pair generation

```sql
SELECT
    t1.x,
    t1.y,
    t2.x,
    t2.y,
    SQRT((POW(t1.x - t2.x, 2) + POW(t1.y - t2.y, 2))) AS distance
FROM
    point_2d t1
    JOIN point_2d t2
        ON (t1.x <= t2.x AND t1.y < t2.y)
        OR (t1.x <= t2.x AND t1.y > t2.y)
        OR (t1.x < t2.x AND t1.y = t2.y);
```

---

## What this reduces

For the sample input, this produces fewer rows than the first solution.

Sample output:

| x   | y   | x   | y   | distance           |
| --- | --- | --- | --- | ------------------ |
| -1  | -2  | -1  | -1  | 1                  |
| -1  | -1  | 0   | 0   | 1.4142135623730951 |
| -1  | -2  | 0   | 0   | 2.23606797749979   |
| -1  | -1  | -1  | -2  | 1                  |

It is smaller than the full pair listing from Approach 1.

The solution notes that this can reduce the total number of distance computations, though not always perfectly down to `n * (n - 1) / 2` in every practical case because of equal-coordinate situations.

Also notice that some apparent duplication can still remain depending on the chosen comparison rule and coordinate values.

---

## Step 2: Take the minimum and round it

Once the optimized pair set is generated, the rest is the same:

```sql
SELECT
    ROUND(SQRT(MIN((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)))), 2) AS shortest
FROM
    point_2d p1
    JOIN point_2d p2
        ON (p1.x <= p2.x AND p1.y < p2.y)
        OR (p1.x <= p2.x AND p1.y > p2.y)
        OR (p1.x < p2.x AND p1.y = p2.y);
```

---

## Final query for Approach 2

```sql
SELECT
    ROUND(SQRT(MIN((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)))), 2) AS shortest
FROM
    point_2d p1
    JOIN point_2d p2
        ON (p1.x <= p2.x AND p1.y < p2.y)
        OR (p1.x <= p2.x AND p1.y > p2.y)
        OR (p1.x < p2.x AND p1.y = p2.y);
```

---

# Comparing the two approaches

## Approach 1: all distinct ordered pairs

### Idea

Compare every point to every other point except itself.

### Pros

- simplest to understand
- very direct mapping from problem to SQL

### Cons

- performs duplicate pair calculations
- distance `(A, B)` and `(B, A)` are both computed

---

## Approach 2: avoid some duplicate comparisons

### Idea

Impose a comparison rule so that a pair is considered only in one direction.

### Pros

- reduces unnecessary calculations
- more performance-aware

### Cons

- join condition is more complicated
- still not as visually straightforward as Approach 1
- depending on coordinate patterns, some redundancy can still remain

---

# Important SQL and math observations

## 1. Why self join is necessary

We need distances between pairs of rows in the same table.

That is why a self join is the natural approach.

---

## 2. Why self-pairs must be excluded

If a point is compared with itself:

```text
distance = 0
```

Then the shortest distance would always be zero, which is wrong unless duplicate points existed, but duplicates are not possible here because `(x, y)` is the primary key.

So self-pairs must be filtered out.

---

## 3. Why `SQRT(MIN(...))` is valid

Because square root preserves ordering for non-negative numbers.

So minimizing squared distance is equivalent to minimizing actual distance.

That lets us compute:

```sql
SQRT(MIN(squared_distance))
```

instead of:

```sql
MIN(SQRT(squared_distance))
```

---

# A cleaner conceptual optimization

A common alternative optimization rule many people use in similar problems is something like:

```sql
(p1.x < p2.x) OR (p1.x = p2.x AND p1.y < p2.y)
```

This gives a strict lexicographic ordering of points and avoids duplicate pair generation more cleanly.

The provided solution uses a different but related rule.
Since you asked for the summary based on the provided approaches, the explanations above preserve the original formulation.

---

# Final accepted implementations

## Approach 1

```sql
SELECT
    ROUND(SQRT(MIN((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)))), 2) AS shortest
FROM
    point_2d p1
    JOIN point_2d p2
        ON p1.x != p2.x OR p1.y != p2.y;
```

## Approach 2

```sql
SELECT
    ROUND(SQRT(MIN((POW(p1.x - p2.x, 2) + POW(p1.y - p2.y, 2)))),2) AS shortest
FROM
    point_2d p1
    JOIN point_2d p2
        ON (p1.x <= p2.x AND p1.y < p2.y)
        OR (p1.x <= p2.x AND p1.y > p2.y)
        OR (p1.x < p2.x AND p1.y = p2.y);
```

---

# Complexity discussion

Let `n` be the number of points.

## Approach 1

This compares essentially all ordered distinct pairs, so conceptually it behaves like:

```text
O(n^2)
```

distance evaluations.

## Approach 2

This also remains quadratic in the worst case, but with fewer duplicate comparisons in practice.

So the big-O class is still:

```text
O(n^2)
```

but with a smaller constant factor.

---

# Key takeaways

1. This is a pairwise same-table comparison problem, so self join is the natural SQL tool.
2. The distance formula is:
   - `SQRT(POW(dx, 2) + POW(dy, 2))`
3. Self-pairs must be excluded or the minimum will become zero.
4. `SQRT(MIN(squared_distance))` is valid and slightly more efficient than `MIN(actual_distance)`.
5. Approach 1 is simpler.
6. Approach 2 reduces duplicate calculations by enforcing a comparison rule between point pairs.
