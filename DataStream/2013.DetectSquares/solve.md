# 2013. Detect Squares — Exhaustive Java Notes

## Problem Statement

Design a data structure that supports two operations on a stream of 2D points:

- `add(point)`: insert a point into the structure.
- `count(point)`: return the number of ways to pick **three stored points** such that together with the query point they form an **axis-aligned square** with **positive area**.

Important details:

- Duplicate points are allowed.
- Duplicates count as different choices.
- The square must be axis-aligned, so its sides are parallel to the x-axis and y-axis.

---

## Example

### Input

```text
["DetectSquares","add","add","add","count","count","add","count"]
[[], [[3,10]], [[11,2]], [[3,2]], [[11,10]], [[14,8]], [[11,2]], [[11,10]]]
```

### Output

```text
[null, null, null, null, 1, 0, null, 2]
```

### Explanation

After adding:

```text
(3,10), (11,2), (3,2)
```

For query `(11,10)`, the square is:

```text
(3,10)   (11,10)
(3,2)    (11,2)
```

So answer is `1`.

After adding another `(11,2)`, that bottom-right corner now exists twice, so the same square can be formed in `2` ways.

---

# Core Geometry Insight

Suppose the query point is:

```text
(x, y)
```

If this point is one corner of an axis-aligned square, then the opposite diagonal corner must be:

```text
(nx, ny)
```

such that:

- `abs(nx - x) == abs(ny - y)`
- `nx != x`
- `ny != y`

Once we choose such a diagonal point, the other two required corners are:

```text
(nx, y)
(x, ny)
```

So for every possible diagonal point `(nx, ny)`, the number of squares contributed is:

```text
count(nx, ny) * count(nx, y) * count(x, ny)
```

That is the whole problem.

The only challenge is how to organize stored points so that we can evaluate this efficiently.

---

# Approach 1 — Brute Force with Full Point List

## Intuition

Store every inserted point in a list.

When `count([x, y])` is called:

- iterate through every stored point `(nx, ny)`
- treat it as the possible diagonal corner
- check whether it forms a valid square with `(x, y)`
- if yes, count how many times the other two corners exist

This is the most direct interpretation.

Because duplicates are allowed, we cannot just use a set. We need frequencies.

---

## Data Structures

- `List<int[]> points` → stores every inserted point
- `Map<Integer, Map<Integer, Integer>> freq` → frequency of each coordinate

This nested map lets us ask quickly:

```text
How many times does point (a, b) exist?
```

---

## Java Code

```java
import java.util.*;

class DetectSquaresBruteForce {
    private final List<int[]> points;
    private final Map<Integer, Map<Integer, Integer>> freq;

    public DetectSquaresBruteForce() {
        points = new ArrayList<>();
        freq = new HashMap<>();
    }

    public void add(int[] point) {
        int x = point[0], y = point[1];
        points.add(new int[]{x, y});
        freq.computeIfAbsent(x, k -> new HashMap<>())
            .merge(y, 1, Integer::sum);
    }

    public int count(int[] point) {
        int x = point[0], y = point[1];
        int ans = 0;

        for (int[] p : points) {
            int nx = p[0], ny = p[1];

            // must be diagonal of a positive-area axis-aligned square
            if (nx == x || ny == y) continue;
            if (Math.abs(nx - x) != Math.abs(ny - y)) continue;

            int c1 = getCount(nx, y);
            int c2 = getCount(x, ny);

            ans += c1 * c2;
        }

        return ans;
    }

    private int getCount(int x, int y) {
        return freq.getOrDefault(x, Collections.emptyMap()).getOrDefault(y, 0);
    }
}
```

---

## Complexity

Let `n` be the number of inserted points.

### `add`

- Time: `O(1)` average
- Space: `O(n)`

### `count`

- Time: `O(n)`
- Space: `O(1)` extra

---

## Observations

This is already acceptable for the problem constraints:

- at most `3000` total calls
- coordinates are only from `0` to `1000`

So even a linear scan per query is fine.

Still, we can organize the data better.

---

# Approach 2 — HashMap by X Coordinate

## Intuition

For a query point `(x, y)`, the useful squares must have another vertical side at some other x-coordinate `col`.

At that `col`, we need a point `(col, ny)` where:

```text
abs(col - x) == abs(ny - y)
```

So instead of scanning all stored points blindly, we can scan all points that share the same x-column as a candidate diagonal column.

More specifically:

- fix query point `(x, y)`
- iterate through all `ny` values stored at the same x-coordinate `x`
- each `(x, ny)` can serve as one vertical partner
- let `side = abs(ny - y)`
- then the other column must be `x + side` or `x - side`
- for each such column, we need:
  - `(otherX, y)`
  - `(otherX, ny)`

Contribution:

```text
count(x, ny) * count(otherX, y) * count(otherX, ny)
```

This is the standard and cleanest solution.

---

## Why This Works Well

We maintain:

```text
x -> (y -> frequency)
```

So for any query `(x, y)`:

- we only inspect points in the same x-column
- for each such y-value we compute the two possible matching columns
- then multiply frequencies

This avoids scanning irrelevant points from unrelated x-columns.

---

## Java Code

```java
import java.util.*;

class DetectSquares {
    private final Map<Integer, Map<Integer, Integer>> cnt;

    public DetectSquares() {
        cnt = new HashMap<>();
    }

    public void add(int[] point) {
        int x = point[0], y = point[1];
        cnt.computeIfAbsent(x, k -> new HashMap<>())
           .merge(y, 1, Integer::sum);
    }

    public int count(int[] point) {
        int x = point[0], y = point[1];

        if (!cnt.containsKey(x)) {
            return 0;
        }

        int ans = 0;
        Map<Integer, Integer> yMap = cnt.get(x);

        for (Map.Entry<Integer, Integer> entry : yMap.entrySet()) {
            int ny = entry.getKey();
            int sameColumnCount = entry.getValue();

            if (ny == y) {
                continue; // zero side length, not a valid square
            }

            int side = Math.abs(ny - y);

            int rightX = x + side;
            int leftX = x - side;

            ans += sameColumnCount * getCount(rightX, y) * getCount(rightX, ny);
            ans += sameColumnCount * getCount(leftX, y) * getCount(leftX, ny);
        }

        return ans;
    }

    private int getCount(int x, int y) {
        return cnt.getOrDefault(x, Collections.emptyMap())
                  .getOrDefault(y, 0);
    }
}
```

---

## Complexity

Let `k` be the number of distinct y-values stored at query x-coordinate.

### `add`

- Time: `O(1)` average
- Space: `O(total distinct points + duplicates in counts only)` which is effectively `O(P)` for distinct coordinates

### `count`

- Time: `O(k)`
- Space: `O(1)` extra

In practice, this is very fast.

---

# Approach 3 — Fixed-Range 2D Frequency Grid

## Intuition

The constraints say:

```text
0 <= x, y <= 1000
```

That is a very small coordinate range.

So instead of hash maps, we can use a 2D array:

```text
freq[x][y]
```

Then every point lookup becomes true `O(1)` array access.

For query `(x, y)`:

- iterate through all possible `ny` from `0` to `1000`
- if `freq[x][ny] > 0` and `ny != y`, compute the side length
- check both `x + side` and `x - side`

This is simple and often very fast in Java because arrays are cache-friendly.

---

## Java Code

```java
class DetectSquaresGrid {
    private final int[][] freq;

    public DetectSquaresGrid() {
        freq = new int[1001][1001];
    }

    public void add(int[] point) {
        freq[point[0]][point[1]]++;
    }

    public int count(int[] point) {
        int x = point[0], y = point[1];
        int ans = 0;

        for (int ny = 0; ny <= 1000; ny++) {
            if (ny == y || freq[x][ny] == 0) {
                continue;
            }

            int side = Math.abs(ny - y);

            int rightX = x + side;
            if (rightX <= 1000) {
                ans += freq[x][ny] * freq[rightX][y] * freq[rightX][ny];
            }

            int leftX = x - side;
            if (leftX >= 0) {
                ans += freq[x][ny] * freq[leftX][y] * freq[leftX][ny];
            }
        }

        return ans;
    }
}
```

---

## Complexity

### `add`

- Time: `O(1)`

### `count`

- Time: `O(1001)` which is effectively `O(1)` under these constraints

### Space

- `O(1001 * 1001)` integers

This is about one million integers, which is perfectly reasonable.

---

# Best Practical Choice

For interviews and general-purpose coding, **Approach 2** is usually the best answer.

Why:

- clean
- scalable beyond small coordinate bounds
- directly models the geometry
- avoids wasting time scanning all `0..1000` rows if the input is sparse

If the problem explicitly guarantees tiny coordinate bounds and you want the simplest fast implementation, **Approach 3** is also excellent.

---

# Deep Dive: Why Frequency Multiplication Is Necessary

Suppose we query `(11, 10)` and have:

- `(3, 10)` once
- `(3, 2)` once
- `(11, 2)` twice

Then the square:

```text
(3,10), (3,2), (11,2), (11,10)
```

can be formed in **2** ways, because the point `(11,2)` exists twice.

In formula form:

```text
count(3,2) * count(3,10) * count(11,2)
= 1 * 1 * 2
= 2
```

This is why we must store counts, not just presence/absence.

---

# Dry Run of Approach 2

## Operations

```text
add(3,10)
add(11,2)
add(3,2)
count(11,10)
```

Stored counts:

```text
x = 3  -> {10:1, 2:1}
x = 11 -> {2:1}
```

Now query `(11,10)`.

We inspect all points in x-column `11`:

- only `ny = 2`, count = 1

Then:

```text
side = |2 - 10| = 8
```

Possible other columns:

- `11 + 8 = 19` → no points
- `11 - 8 = 3` → yes

Now contribution from left column `3`:

- `(3,10)` exists once
- `(3,2)` exists once
- `(11,2)` exists once

So:

```text
1 * 1 * 1 = 1
```

Answer is `1`.

If we add `(11,2)` again, then the same iteration gives:

```text
sameColumnCount = 2
2 * 1 * 1 = 2
```

Correct.

---

# Common Mistakes

## Mistake 1: Using only a set

A set loses duplicate information. Duplicates matter.

## Mistake 2: Forgetting positive area

If `ny == y` or `nx == x`, side length becomes `0`. That is not a valid square.

## Mistake 3: Mixing rectangle logic with square logic

Axis-aligned square requires:

```text
abs(nx - x) == abs(ny - y)
```

not just any rectangle.

## Mistake 4: Double counting incorrectly

In Approach 2, for each vertical partner `(x, ny)`, we explicitly check:

- left square
- right square

That is correct and does not overcount because they correspond to different columns.

## Mistake 5: Assuming query point must already exist

The query point does **not** need to be added before `count`. It is just used as one corner.

---

# Correctness Proof for Approach 2

We prove that the algorithm returns exactly the number of valid axis-aligned squares.

## Lemma 1

For a square with query point `(x, y)` as one corner, if another corner in the same vertical line is `(x, ny)`, then the side length is `|ny - y|`, and the other two corners must be either:

```text
(x + side, y) and (x + side, ny)
```

or

```text
(x - side, y) and (x - side, ny)
```

### Proof

In an axis-aligned square, moving from `(x, y)` to `(x, ny)` changes only y-coordinate, so this is a vertical side. The side length is `|ny - y|`. The opposite vertical side must be at horizontal distance exactly equal to the same side length, hence at x-coordinate `x + side` or `x - side`. Therefore the remaining two corners are uniquely determined. ∎

---

## Lemma 2

For each fixed `ny != y`, the algorithm counts exactly all valid squares that use `(x, ny)` as the vertical partner.

### Proof

By Lemma 1, there are only two possible squares: one to the right and one to the left.
For the right square, the number of realizations equals:

```text
count(x, ny) * count(x + side, y) * count(x + side, ny)
```

because each occurrence of these three stored points can be chosen independently.
The same reasoning applies to the left square.
Thus the algorithm counts exactly all valid squares for that `ny`. ∎

---

## Lemma 3

Every valid axis-aligned square with query point `(x, y)` is counted once by the algorithm.

### Proof

Every such square has exactly one other corner in the same x-column as the query point, namely `(x, ny)` with `ny != y`. When the algorithm iterates to this `ny`, it computes the correct side length and checks the unique matching left or right column of that square. So the square is counted. It cannot be counted under a different `ny`, because that would require a different vertical partner, which would define a different square. ∎

---

## Theorem

The algorithm returns the exact number of ways to form a valid axis-aligned square with the query point.

### Proof

By Lemma 2, every iteration counts exactly the valid squares associated with that vertical partner.
By Lemma 3, every valid square is counted once.
Therefore the total returned by the algorithm is exactly the answer. ∎

---

# Comparison of Approaches

| Approach                    | Idea                             |    Add |     Count |     Space | Notes                                 |
| --------------------------- | -------------------------------- | -----: | --------: | --------: | ------------------------------------- |
| Brute force list + freq map | scan every inserted point        | `O(1)` |    `O(n)` |    `O(n)` | simplest to reason about              |
| HashMap by x-column         | scan only same-column candidates | `O(1)` |    `O(k)` |    `O(P)` | best general solution                 |
| 2D array grid               | exploit small coordinate bounds  | `O(1)` | `O(1001)` | `O(10^6)` | very simple and fast for this problem |

Where:

- `n` = number of inserted points
- `k` = number of distinct y-values at query x-column
- `P` = number of distinct stored coordinates

---

# Final Recommended Java Solution

This is the solution I would submit in an interview or coding platform.

```java
import java.util.*;

class DetectSquares {
    private final Map<Integer, Map<Integer, Integer>> cnt;

    public DetectSquares() {
        cnt = new HashMap<>();
    }

    public void add(int[] point) {
        int x = point[0];
        int y = point[1];

        cnt.computeIfAbsent(x, k -> new HashMap<>())
           .merge(y, 1, Integer::sum);
    }

    public int count(int[] point) {
        int x = point[0];
        int y = point[1];

        Map<Integer, Integer> sameX = cnt.get(x);
        if (sameX == null) {
            return 0;
        }

        int ans = 0;

        for (Map.Entry<Integer, Integer> entry : sameX.entrySet()) {
            int ny = entry.getKey();
            int freqVertical = entry.getValue();

            if (ny == y) {
                continue;
            }

            int side = Math.abs(ny - y);

            int x1 = x + side;
            ans += freqVertical * getCount(x1, y) * getCount(x1, ny);

            int x2 = x - side;
            ans += freqVertical * getCount(x2, y) * getCount(x2, ny);
        }

        return ans;
    }

    private int getCount(int x, int y) {
        return cnt.getOrDefault(x, Collections.emptyMap())
                  .getOrDefault(y, 0);
    }
}
```

---

# Interview Summary

The key observation is:

- choose the query point `(x, y)`
- choose another point `(x, ny)` in the same column
- then the side length is `|ny - y|`
- the square must be completed on either:
  - `x + side`
  - `x - side`

For each such candidate, multiply the frequencies of the three required stored points.

That gives a clean and efficient solution using:

```text
Map<x, Map<y, count>>
```

which supports:

- `add` in `O(1)` average
- `count` in time proportional to the number of points sharing the query x-coordinate

This is the main idea that makes the problem easy.
