# 2280. Minimum Lines to Represent a Line Chart — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int minimumLines(int[][] stockPrices) {

    }
}
```

---

# Problem Restatement

Each point is:

```text
(day, price)
```

We plot all points and connect adjacent points in increasing order of day.

We need the minimum number of straight line segments needed to represent the whole chart.

Equivalent view:

- sort points by day
- walk through consecutive segments
- whenever the slope changes, we need a new line

---

# Core Insight

If three consecutive points lie on the same line, then the two adjacent segments can be merged into one line.

So the task is to count how many times the slope changes between consecutive pairs of points.

That immediately gives the structure:

1. sort by `day`
2. start with 1 line if there are at least 2 points
3. compare each new slope with the previous slope
4. if different, increment answer

---

# Important Detail: Avoid Floating Point

A slope is:

```text
(price2 - price1) / (day2 - day1)
```

Do **not** compare these using `double`.

That can fail because of precision issues.

Instead compare by cross multiplication:

For slopes:

```text
dy1 / dx1
dy2 / dx2
```

they are equal iff:

```text
dy1 * dx2 == dy2 * dx1
```

Use `long` to avoid overflow, because coordinates can be as large as `10^9`.

---

# Approach 1 — Sort + Compare Consecutive Slopes with Cross Multiplication (Recommended)

## Idea

After sorting by day:

- if there is only 1 point, answer is 0
- otherwise answer starts at 1
- for every triple of consecutive points:
  - compute slope of segment `(i-1 -> i)`
  - compute slope of segment `(i -> i+1)`
  - if the slopes differ, a new line starts

This is the cleanest intended solution.

---

## Why this works

A line chart is made of consecutive segments.

As long as consecutive segments have the same slope, they belong to the same straight line.

The first segment always needs one line.
Every later segment either:

- continues the same line, or
- starts a new one

So counting slope changes is exactly the minimum number of lines.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int minimumLines(int[][] stockPrices) {
        int n = stockPrices.length;
        if (n <= 1) {
            return 0;
        }

        Arrays.sort(stockPrices, (a, b) -> Integer.compare(a[0], b[0]));

        int lines = 1;

        for (int i = 2; i < n; i++) {
            long dx1 = (long) stockPrices[i - 1][0] - stockPrices[i - 2][0];
            long dy1 = (long) stockPrices[i - 1][1] - stockPrices[i - 2][1];

            long dx2 = (long) stockPrices[i][0] - stockPrices[i - 1][0];
            long dy2 = (long) stockPrices[i][1] - stockPrices[i - 1][1];

            if (dy1 * dx2 != dy2 * dx1) {
                lines++;
            }
        }

        return lines;
    }
}
```

---

## Complexity

Sorting dominates.

```text
Time:  O(n log n)
Space: O(log n) to O(n), depending on sort implementation details
```

This is optimal for the problem because the days are unsorted.

---

# Approach 2 — Normalize Slope as Reduced Fraction (Also Valid)

## Idea

Instead of comparing slopes by cross multiplication each time, we can normalize each slope into reduced form:

```text
dy / dx
```

by dividing numerator and denominator by their GCD.

Then compare normalized pairs.

This also works, but it is a bit more code than necessary.

---

## Notes

When normalizing:

- compute `g = gcd(abs(dy), abs(dx))`
- reduce to `(dy / g, dx / g)`
- keep a consistent sign convention, for example:
  - ensure `dx > 0`
  - if not, multiply both by `-1`

In this problem, after sorting by day, `dx` is always positive because days are distinct, so sign handling is simpler.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int minimumLines(int[][] stockPrices) {
        int n = stockPrices.length;
        if (n <= 1) {
            return 0;
        }

        Arrays.sort(stockPrices, (a, b) -> Integer.compare(a[0], b[0]));

        int lines = 1;

        long prevDy = (long) stockPrices[1][1] - stockPrices[0][1];
        long prevDx = (long) stockPrices[1][0] - stockPrices[0][0];
        long g = gcd(Math.abs(prevDy), Math.abs(prevDx));
        prevDy /= g;
        prevDx /= g;

        for (int i = 2; i < n; i++) {
            long dy = (long) stockPrices[i][1] - stockPrices[i - 1][1];
            long dx = (long) stockPrices[i][0] - stockPrices[i - 1][0];

            long gg = gcd(Math.abs(dy), Math.abs(dx));
            dy /= gg;
            dx /= gg;

            if (dy != prevDy || dx != prevDx) {
                lines++;
                prevDy = dy;
                prevDx = dx;
            }
        }

        return lines;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Same asymptotic complexity:

```text
Time:  O(n log n)
Space: O(log n) to O(n)
```

Still efficient, but the cross-multiplication method is simpler.

---

# Approach 3 — Brute Force Line Extension Check (Too Slow)

## Idea

After sorting, you could attempt to extend each line as far as possible by checking all later points for collinearity.

That works logically, but it is unnecessary and can degrade toward quadratic time.

---

## Why it is inferior

The line chart only depends on consecutive points.
You do not need to search arbitrarily ahead.
Only consecutive slope comparisons matter.

So this approach overcomplicates a local problem.

---

# Detailed Walkthrough

## Example 1

```text
stockPrices = [[1,7],[2,6],[3,5],[4,4],[5,4],[6,3],[7,2],[8,1]]
```

After sorting, the points are already in order.

Consecutive slopes:

- `(1,7) -> (2,6)` slope = `-1`
- `(2,6) -> (3,5)` slope = `-1`
- `(3,5) -> (4,4)` slope = `-1`

So these stay on the same line.

Next:

- `(4,4) -> (5,4)` slope = `0`

Slope changed, so line count becomes 2.

Then:

- `(5,4) -> (6,3)` slope = `-1`
- `(6,3) -> (7,2)` slope = `-1`
- `(7,2) -> (8,1)` slope = `-1`

Slope changed again from `0` to `-1`, so line count becomes 3.

Final answer:

```text
3
```

---

## Example 2

```text
stockPrices = [[3,4],[1,2],[7,8],[2,3]]
```

After sorting by day:

```text
[(1,2), (2,3), (3,4), (7,8)]
```

Consecutive slopes:

- `(1,2) -> (2,3)` slope = `1`
- `(2,3) -> (3,4)` slope = `1`
- `(3,4) -> (7,8)` slope = `1`

All the same, so only one line is needed.

Answer:

```text
1
```

---

# Important Correctness Argument

Once points are sorted by day, the chart is a chain of consecutive segments.

A set of consecutive segments belongs to one straight line exactly when all those segments have the same slope.

Therefore:

- the first segment always contributes 1 line
- each later segment contributes 0 if its slope matches the previous segment
- otherwise it contributes 1 new line

This greedy counting is exact, because no non-consecutive merging can reduce the answer further; the line chart is already forced by adjacency.

---

# Common Pitfalls

## 1. Forgetting to sort by day

The input order is not guaranteed to be sorted.

This is essential.

---

## 2. Comparing slopes with floating point

Do not use `double`.

Use cross multiplication with `long`.

---

## 3. Using int for multiplication

Differences can be up to about `10^9`, so products can be about `10^18`.

Use `long`.

---

## 4. Returning 1 when there is only one point

If there is only one point, no line is needed.

The answer is:

```text
0
```

---

# Best Approach

## Recommended: Sort + compare consecutive slopes via cross multiplication

This is the best solution because:

- it is simple
- it is precise
- it avoids floating-point issues
- it matches the natural structure of the chart

---

# Final Recommended Java Solution

```java
import java.util.Arrays;

class Solution {
    public int minimumLines(int[][] stockPrices) {
        int n = stockPrices.length;
        if (n <= 1) {
            return 0;
        }

        Arrays.sort(stockPrices, (a, b) -> Integer.compare(a[0], b[0]));

        int lines = 1;

        for (int i = 2; i < n; i++) {
            long dx1 = (long) stockPrices[i - 1][0] - stockPrices[i - 2][0];
            long dy1 = (long) stockPrices[i - 1][1] - stockPrices[i - 2][1];

            long dx2 = (long) stockPrices[i][0] - stockPrices[i - 1][0];
            long dy2 = (long) stockPrices[i][1] - stockPrices[i - 1][1];

            if (dy1 * dx2 != dy2 * dx1) {
                lines++;
            }
        }

        return lines;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n log n)
Space: O(log n) to O(n)
```

---

# Final Takeaway

The problem becomes easy once you realize that the chart is determined entirely by consecutive points after sorting by day.

Then the answer is simply:

> 1 for the first segment, plus 1 each time the slope changes

and slope comparison should be done with cross multiplication, not floating point.
