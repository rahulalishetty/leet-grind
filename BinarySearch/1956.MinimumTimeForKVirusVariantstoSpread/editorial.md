# 1956. Minimum Time For K Virus Variants to Spread — Java Solutions and Detailed Notes

## Problem

We have `n` virus variants, where variant `i` starts at point:

```text
points[i] = (xi, yi)
```

on day `0`.

Every day, each infected cell spreads to its four neighbors:

- up
- down
- left
- right

So after `d` days, variant `i` has infected exactly the cells whose **Manhattan distance** from `(xi, yi)` is at most `d`.

We need the minimum integer `d` such that **some grid point** contains at least `k` variants.

---

## Key reformulation

A point `(x, y)` contains variant `i` on day `d` iff:

```text
|x - xi| + |y - yi| <= d
```

So for a fixed point `(x, y)`, the minimum day on which it contains at least `k` variants is:

> the `k`th smallest Manhattan distance from `(x, y)` to all virus origins.

Therefore the whole problem becomes:

> Find a point `(x, y)` that minimizes the `k`th smallest distance to the given origins.

---

# Very important observation: we only need to search inside `[1..100] x [1..100]`

All virus origins satisfy:

```text
1 <= xi, yi <= 100
```

Suppose we choose a point `(x, y)` outside that box.

If `x < 1`, moving `x` right to `1` decreases or preserves the Manhattan distance to **every** origin.
If `x > 100`, moving `x` left to `100` decreases or preserves the Manhattan distance to **every** origin.

The same is true for `y`.

So **clamping** any outside point into `[1,100] x [1,100]` can never make the answer worse.

That means an optimal meeting point always exists inside the finite `100 x 100` grid.

This single observation makes a very practical brute-force solution possible.

---

# Approach 1: Enumerate Every Grid Cell and Compute the k-th Distance

## Idea

For every grid cell `(x, y)` in:

```text
1 <= x <= 100
1 <= y <= 100
```

1. compute its Manhattan distances to all virus origins,
2. sort those distances,
3. take the `k`th smallest distance,
4. minimize that over all cells.

Because the grid is only `100 x 100` and `n <= 50`, this is absolutely feasible.

---

## Example

```text
points = [[3,3],[1,2],[9,2]], k = 2
```

Try point `(2,2)`:

Distances:

- to `(3,3)` = 2
- to `(1,2)` = 1
- to `(9,2)` = 7

Sorted:

```text
[1,2,7]
```

The 2nd smallest is `2`, so day `2` is enough for point `(2,2)` to contain at least 2 variants.

If we scan all grid cells and take the minimum such value, we get the final answer.

---

## Java code

```java
import java.util.Arrays;

class Solution {
    public int minDayskVariants(int[][] points, int k) {
        int answer = Integer.MAX_VALUE;
        int n = points.length;

        for (int x = 1; x <= 100; x++) {
            for (int y = 1; y <= 100; y++) {
                int[] dist = new int[n];

                for (int i = 0; i < n; i++) {
                    dist[i] = Math.abs(x - points[i][0]) + Math.abs(y - points[i][1]);
                }

                Arrays.sort(dist);
                answer = Math.min(answer, dist[k - 1]);
            }
        }

        return answer;
    }
}
```

---

## Complexity

Let `n = points.length`.

Time complexity:

```text
O(100 * 100 * n log n)
```

Since:

- there are `10,000` grid cells,
- for each cell we compute `n` distances,
- then sort at most `50` numbers.

This is easily fast enough.

Space complexity:

```text
O(n)
```

for the distance array.

---

## Verdict

This is the cleanest and most practical solution for the given constraints.

---

# Approach 2: Binary Search on Days + Check Every Grid Cell

## Idea

Instead of directly computing the best day per cell, we can binary search on the answer.

For a candidate day `d`, ask:

> Does there exist a grid point that is within distance `d` of at least `k` virus origins?

This is monotonic:

- if day `d` works, then every larger day also works,
- if day `d` does not work, then every smaller day also does not work.

So binary search applies.

---

## Search range

Minimum possible answer:

```text
0
```

Maximum possible answer:

```text
198
```

Why `198`?

Because we only need to search inside `[1,100] x [1,100]`, and the maximum Manhattan distance between two points in that box is:

```text
|1 - 100| + |1 - 100| = 198
```

So the answer is guaranteed to lie in:

```text
[0, 198]
```

---

## Feasibility check

For a given `days`, scan every point `(x, y)` in `[1..100] x [1..100]` and count how many virus origins satisfy:

```text
|x - xi| + |y - yi| <= days
```

If any cell has count `>= k`, then `days` works.

---

## Java code

```java
class Solution {
    public int minDayskVariants(int[][] points, int k) {
        int left = 0;
        int right = 198;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canMeet(points, k, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canMeet(int[][] points, int k, int days) {
        for (int x = 1; x <= 100; x++) {
            for (int y = 1; y <= 100; y++) {
                int count = 0;

                for (int[] p : points) {
                    int dist = Math.abs(x - p[0]) + Math.abs(y - p[1]);
                    if (dist <= days) {
                        count++;
                    }
                }

                if (count >= k) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity

Time complexity:

```text
O(log 198 * 100 * 100 * n)
```

Since `log 198` is tiny, this is also fast in practice.

Space complexity:

```text
O(1)
```

excluding input storage.

---

## Verdict

This is a nice “binary-search-on-answer” formulation. It is slightly more abstract than Approach 1, but still straightforward.

---

# Approach 3: Binary Search + Geometry Transform (Advanced)

## Why this approach is interesting

Each virus covers a **diamond** in `(x, y)` coordinates:

```text
|x - xi| + |y - yi| <= d
```

Checking whether at least `k` such diamonds overlap at some point can be converted into a rectangle overlap problem by rotating coordinates.

This is the most geometric approach among the three.

---

## Coordinate transform

Define:

```text
u = x + y
v = x - y
```

For a fixed virus origin `(xi, yi)`, let:

```text
ui = xi + yi
vi = xi - yi
```

Then:

```text
|x - xi| + |y - yi| <= d
```

is equivalent to:

```text
ui - d <= u <= ui + d
vi - d <= v <= vi + d
```

So each Manhattan diamond becomes an **axis-aligned rectangle** in `(u, v)` space.

Now the question becomes:

> Is there an integer pair `(u, v)` with the same parity such that at least `k` of these rectangles contain it?

Why same parity?

Because:

```text
x = (u + v) / 2
y = (u - v) / 2
```

For `x` and `y` to be integers, `u` and `v` must have the same parity.

---

## Binary search on days

For a candidate `d`:

1. convert every virus to a rectangle in `(u, v)`,
2. check whether some valid `(u, v)` is covered by at least `k` rectangles,
3. binary search the minimum `d`.

---

## How to check overlap efficiently

For one rectangle:

```text
u in [L, R]
v in [B, T]
```

Coverage only changes when crossing rectangle boundaries.

For integer coordinates, the critical positions are:

- `L`
- `R + 1`

and similarly for `v`.

To handle parity safely, it is enough to test:

- each critical coordinate,
- and also critical coordinate `+ 1`.

That gives a small candidate set of `u` and `v` values.

Because `n <= 50`, brute forcing over these candidate transformed coordinates is still small.

---

## Java code

```java
import java.util.*;

class Solution {
    public int minDayskVariants(int[][] points, int k) {
        int left = 0;
        int right = 198;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canMeet(points, k, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canMeet(int[][] points, int k, int d) {
        List<Integer> candU = new ArrayList<>();
        List<Integer> candV = new ArrayList<>();

        int n = points.length;
        int[] u = new int[n];
        int[] v = new int[n];

        for (int i = 0; i < n; i++) {
            u[i] = points[i][0] + points[i][1];
            v[i] = points[i][0] - points[i][1];

            int lu = u[i] - d;
            int ru = u[i] + d;
            int lv = v[i] - d;
            int rv = v[i] + d;

            // Critical points for inclusive integer intervals:
            // [lu, ru] changes at lu and ru + 1.
            candU.add(lu);
            candU.add(ru + 1);
            candU.add(lu + 1);
            candU.add(ru + 2);

            candV.add(lv);
            candV.add(rv + 1);
            candV.add(lv + 1);
            candV.add(rv + 2);
        }

        // Deduplicate
        Set<Integer> setU = new HashSet<>(candU);
        Set<Integer> setV = new HashSet<>(candV);

        for (int cu : setU) {
            for (int cv : setV) {
                // Need same parity so that x and y are integers
                if (((cu ^ cv) & 1) != 0) {
                    continue;
                }

                int count = 0;

                for (int i = 0; i < n; i++) {
                    if (Math.abs(cu - u[i]) <= d && Math.abs(cv - v[i]) <= d) {
                        count++;
                    }
                }

                if (count >= k) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity

Let `n = points.length`.

Time complexity:

```text
O(log 198 * C^2 * n)
```

where `C = O(n)` is the number of candidate transformed coordinates.

That simplifies to roughly:

```text
O(n^3)
```

inside a very small constant and tiny `log 198`.

With `n <= 50`, this is still acceptable.

Space complexity:

```text
O(n)
```

for transformed coordinates and candidate lists.

---

## Verdict

This is the most advanced and most geometric solution.

It is useful if you want to recognize the standard trick:

```text
|x - a| + |y - b| <= d
```

becomes a rectangle after the transform:

```text
u = x + y
v = x - y
```

---

# Comparison of approaches

## Approach 1: Enumerate every grid cell

### Pros

- simplest
- directly uses the constraints
- very easy to reason about
- likely the best interview implementation here given `xi, yi <= 100`

### Cons

- relies heavily on the coordinate bound `<= 100`

### Complexity

```text
Time:  O(100 * 100 * n log n)
Space: O(n)
```

---

## Approach 2: Binary search on days + grid scan

### Pros

- introduces monotonic feasibility
- clean binary-search-on-answer pattern
- still easy to code

### Cons

- slightly more indirect than Approach 1
- still relies on the bounded `100 x 100` search area

### Complexity

```text
Time:  O(log 198 * 100 * 100 * n)
Space: O(1)
```

---

## Approach 3: Binary search + geometry transform

### Pros

- elegant geometric reduction
- does not conceptually depend on scanning the whole original grid
- good for learning Manhattan-geometry transforms

### Cons

- much more subtle
- parity handling is easy to get wrong
- more complex than necessary for these constraints

### Complexity

```text
Time:  about O(log 198 * n^3)
Space: O(n)
```

---

# Recommended solution

For this exact problem, the best practical choice is:

## Approach 1: Enumerate every cell in `[1..100] x [1..100]`

It is:

- short,
- robust,
- easy to explain,
- fully within constraints.

---

# Final polished Java solution

```java
import java.util.Arrays;

class Solution {
    public int minDayskVariants(int[][] points, int k) {
        int answer = Integer.MAX_VALUE;
        int n = points.length;

        for (int x = 1; x <= 100; x++) {
            for (int y = 1; y <= 100; y++) {
                int[] dist = new int[n];

                for (int i = 0; i < n; i++) {
                    dist[i] = Math.abs(x - points[i][0]) + Math.abs(y - points[i][1]);
                }

                Arrays.sort(dist);
                answer = Math.min(answer, dist[k - 1]);
            }
        }

        return answer;
    }
}
```

---

# Quick intuition recap

A point gets infected by variant `i` after exactly:

```text
ManhattanDistance(point, origin_i)
```

days.

So for any fixed point, the first day it has at least `k` variants is just the:

```text
kth smallest distance to all origins
```

Then scan all candidate points and minimize that value.

That is the whole problem.

---

# Extra note

If the coordinate values were huge instead of bounded by `100`, then Approach 1 would no longer be attractive, and the geometry-based or more advanced optimization approaches would become much more important.
