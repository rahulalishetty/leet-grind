# 3382. Maximum Area Rectangle With Point Constraints II — Exhaustive Java Notes

## Problem Statement

You are given `n` points on a plane through two arrays:

```text
xCoord[i], yCoord[i]
```

which represent the point:

```text
(xCoord[i], yCoord[i])
```

You must find the **maximum area** of an axis-aligned rectangle such that:

- its 4 corners are among the given points
- its sides are parallel to the axes
- **no other point** lies inside the rectangle
- **no other point** lies on the rectangle border

Return the maximum area, or:

```text
-1
```

if no such rectangle exists.

---

## Example 1

```text
Input:
xCoord = [1,1,3,3]
yCoord = [1,3,1,3]

Output:
4
```

The rectangle with corners:

```text
(1,1), (1,3), (3,1), (3,3)
```

is valid and has area:

```text
(3 - 1) * (3 - 1) = 4
```

---

## Example 2

```text
Input:
xCoord = [1,1,3,3,2]
yCoord = [1,3,1,3,2]

Output:
-1
```

The outer rectangle exists, but point `(2,2)` lies inside it, so it is invalid.

---

## Example 3

```text
Input:
xCoord = [1,1,3,3,1,3]
yCoord = [1,3,1,3,2,2]

Output:
2
```

The full height rectangle is invalid because `(1,2)` and `(3,2)` lie on its border.

But the rectangles:

- `(1,1),(1,2),(3,1),(3,2)`
- `(1,2),(1,3),(3,2),(3,3)`

are valid and each has area `2`.

---

## Constraints

```text
1 <= n <= 2 * 10^5
0 <= xCoord[i], yCoord[i] <= 8 * 10^7
All points are unique
```

This is the key difference from version I: brute force is impossible.

---

# 1. Core Geometry

A rectangle is defined by choosing:

- left x = `x1`
- right x = `x2`
- bottom y = `y1`
- top y = `y2`

with:

```text
x1 < x2
y1 < y2
```

The 4 corner points must be:

```text
(x1, y1), (x1, y2), (x2, y1), (x2, y2)
```

But that is not enough.

We must also guarantee:

- no point strictly inside
- no extra point on any of the 4 borders

So this is not merely a “corners exist” problem.
It is a **clean empty rectangle** problem.

---

# 2. Key Reformulation

Suppose two x-columns `xL` and `xR` both contain the same two adjacent y-values:

```text
yLow, yHigh
```

Then those four points define a candidate rectangle:

```text
(xL, yLow), (xL, yHigh), (xR, yLow), (xR, yHigh)
```

Now ask:

When is this rectangle automatically clean?

Answer:

- on each vertical side, there must be no point between `yLow` and `yHigh`
- on each horizontal side, between `xL` and `xR`, there must be no extra point at `yLow` or `yHigh`
- in the interior, there must be no point at all

This suggests a sweep-line by x, plus a range-count structure over points.

---

# 3. High-Level Optimal Strategy

We process points grouped by x-coordinate.

For each x-column:

1. sort all y-values in that column
2. every pair of **adjacent y-values** in that column forms a potential vertical edge with no point on that vertical border in between
3. if the same adjacent y-pair appeared before at some earlier x-column, then together they form a candidate rectangle
4. we must verify that the rectangle contains exactly the 4 corner points and nothing else

The final validation can be done with a 2D prefix-sum style counting structure after coordinate compression.

This is the cleanest practical optimal approach.

---

# 4. Why Only Adjacent Y-Pairs Matter

Suppose in a fixed x-column we have y-values:

```text
1, 2, 5
```

Could `(1,5)` be a valid vertical side of a clean rectangle?

No, because the point `(x,2)` lies on that vertical border.

So for a rectangle to have no extra point on its left or right border, each vertical side must be formed by **adjacent** y-values in that x-column.

That observation dramatically reduces candidates.

---

# 5. Candidate Rectangle Generation

For every x-column, after sorting its y-values:

```text
y[0] < y[1] < y[2] < ...
```

the valid vertical segments are only:

```text
(y[0], y[1]), (y[1], y[2]), ...
```

For each adjacent pair `(a, b)`, store the latest x where this pair occurred.

If later the same pair `(a, b)` appears at x = `x2`, and it last appeared at x = `x1`, then we get a candidate rectangle:

```text
left = x1
right = x2
bottom = a
top = b
```

Now we only need to verify emptiness.

---

# 6. Emptiness Test via 2D Range Counting

For a candidate rectangle, if it is valid, then the number of points inside the closed rectangle:

```text
[x1, x2] × [y1, y2]
```

must be exactly:

```text
4
```

Why?

Because the 4 corners are present, and no other point may lie:

- inside
- on border

So the rectangle is valid iff the rectangle count is exactly 4.

This is a very strong simplification.

So the problem becomes:

- generate candidate rectangles efficiently
- support point count in an axis-aligned closed rectangle efficiently

---

# 7. Data Structure Choices

We need two things:

## A. Mapping adjacent y-pairs to the last x where they appeared

Use a hash map:

```java
Map<Long, Integer>
```

where the key encodes `(yLow, yHigh)`.

## B. Count points inside a rectangle

Because coordinates are huge (`8 * 10^7`), we coordinate-compress x and y.

Then we can answer offline 2D range count queries using a Fenwick tree over y while sweeping x.

A standard way:

- create 4 events per rectangle query using inclusion-exclusion
- sort point insertions and query events by x
- process with Fenwick over compressed y

This gives total complexity around:

```text
O(n log n)
```

plus candidate generation.

---

# 8. Approach 1 — Naive Pair-of-Columns Check

## Idea

Group by x.
For every pair of x-columns, find common y-values, try pairs of y-values, and verify by point counting.

## Why it fails

If one x has many points, comparing all column pairs is too expensive:

```text
O(number_of_columns^2)
```

which can be quadratic in `n`.

So this is only useful as a stepping stone.

---

# 9. Approach 2 — Adjacent Y-Pair Sweep + Offline Rectangle Counting

This is the recommended solution.

## Step 1: Build points and coordinate compression

Create `(x, y)` pairs.

Compress x and y separately.

## Step 2: Group points by original x

For each x, collect all y-values and sort them.

## Step 3: Generate candidate rectangles

For each adjacent pair `(y[i], y[i+1])` in the sorted y-list of that x:

- encode the pair
- if seen before at x = `prevX`, create a rectangle candidate:
  - `(prevX, currentX, y[i], y[i+1])`
- update last seen x for that pair

## Step 4: Validate each candidate by rectangle point count

For each candidate rectangle, count how many points lie in:

```text
[x1, x2] × [y1, y2]
```

If the count is exactly 4, it is valid.

Take the maximum area.

---

# 10. Why This Candidate Generation Is Complete

Suppose a valid rectangle exists.

Then on its left side, the two corner y-values must be adjacent in that x-column.
Otherwise there would be a point on the border.

Same for the right side.

Therefore the pair `(bottom, top)` appears as an adjacent y-pair in both x-columns.

So the sweep over adjacent y-pairs will generate this rectangle.

That proves completeness.

---

# 11. Offline Rectangle Counting

For each candidate rectangle, we need:

```text
count(x in [x1, x2], y in [y1, y2])
```

Use inclusion-exclusion:

```text
count(<= x2, <= y2)
- count(<= x1-1, <= y2)
- count(<= x2, <= y1-1)
+ count(<= x1-1, <= y1-1)
```

Since x/y are compressed discrete coordinates, that becomes prefix queries.

Offline method:

- sort points by x
- sort prefix-query events by x
- sweep x, adding points into Fenwick by y
- each event gets prefix count

Then compute each rectangle count from its 4 events.

---

# 12. Java Implementation

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 2];
        }

        void add(int idx, int delta) {
            while (idx <= n) {
                bit[idx] += delta;
                idx += idx & -idx;
            }
        }

        int sum(int idx) {
            int res = 0;
            while (idx > 0) {
                res += bit[idx];
                idx -= idx & -idx;
            }
            return res;
        }
    }

    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Rect {
        int x1, x2, y1, y2;
        long area;
        Rect(int x1, int x2, int y1, int y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.area = 1L * (x2 - x1) * (y2 - y1);
        }
    }

    static class Event {
        int x;
        int y;
        int rectId;
        int sign;
        Event(int x, int y, int rectId, int sign) {
            this.x = x;
            this.y = y;
            this.rectId = rectId;
            this.sign = sign;
        }
    }

    public long maxRectangleArea(int[] xCoord, int[] yCoord) {
        int n = xCoord.length;
        Point[] points = new Point[n];
        TreeSet<Integer> xSet = new TreeSet<>();
        TreeSet<Integer> ySet = new TreeSet<>();

        for (int i = 0; i < n; i++) {
            points[i] = new Point(xCoord[i], yCoord[i]);
            xSet.add(xCoord[i]);
            ySet.add(yCoord[i]);
        }

        // Group y-values by original x
        Map<Integer, List<Integer>> byX = new HashMap<>();
        for (int i = 0; i < n; i++) {
            byX.computeIfAbsent(xCoord[i], k -> new ArrayList<>()).add(yCoord[i]);
        }

        List<Integer> sortedX = new ArrayList<>(xSet);
        Collections.sort(sortedX);

        Map<Long, Integer> lastX = new HashMap<>();
        List<Rect> rects = new ArrayList<>();

        // Generate candidate rectangles from adjacent y-pairs
        for (int x : sortedX) {
            List<Integer> ys = byX.get(x);
            Collections.sort(ys);

            for (int i = 0; i + 1 < ys.size(); i++) {
                int y1 = ys.get(i);
                int y2 = ys.get(i + 1);
                long key = (((long) y1) << 32) ^ (y2 & 0xffffffffL);

                if (lastX.containsKey(key)) {
                    int prevX = lastX.get(key);
                    rects.add(new Rect(prevX, x, y1, y2));
                }
                lastX.put(key, x);
            }
        }

        if (rects.isEmpty()) return -1;

        // Coordinate compression for y
        List<Integer> sortedY = new ArrayList<>(ySet);
        Collections.sort(sortedY);

        Map<Integer, Integer> yRank = new HashMap<>();
        for (int i = 0; i < sortedY.size(); i++) {
            yRank.put(sortedY.get(i), i + 1);
        }

        // Sort points by x for sweep
        Arrays.sort(points, Comparator.comparingInt(p -> p.x));

        // Build offline prefix events
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < rects.size(); i++) {
            Rect r = rects.get(i);

            int y2Rank = upperBound(sortedY, r.y2);
            int y1MinusRank = upperBound(sortedY, r.y1 - 1);

            events.add(new Event(r.x2, y2Rank, i, +1));
            events.add(new Event(r.x1 - 1, y2Rank, i, -1));
            events.add(new Event(r.x2, y1MinusRank, i, -1));
            events.add(new Event(r.x1 - 1, y1MinusRank, i, +1));
        }

        events.sort(Comparator.comparingInt(e -> e.x));

        Fenwick bit = new Fenwick(sortedY.size() + 2);
        long[] cnt = new long[rects.size()];

        int p = 0;
        for (Event e : events) {
            while (p < n && points[p].x <= e.x) {
                bit.add(yRank.get(points[p].y), 1);
                p++;
            }
            if (e.y > 0) {
                cnt[e.rectId] += 1L * e.sign * bit.sum(e.y);
            }
        }

        long ans = -1;
        for (int i = 0; i < rects.size(); i++) {
            if (cnt[i] == 4) {
                ans = Math.max(ans, rects.get(i).area);
            }
        }

        return ans;
    }

    private int upperBound(List<Integer> arr, int target) {
        int l = 0, r = arr.size();
        while (l < r) {
            int m = (l + r) >>> 1;
            if (arr.get(m) <= target) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l; // Fenwick is 1-indexed prefix length
    }
}
```

---

# 13. Complexity Analysis

Let `n` be the number of points.

## Candidate generation

Each x-column is sorted once.
Across all columns, total points is `n`.

So this part is:

```text
O(n log n)
```

## Rectangle counting

If the number of candidates is `m`, then we create `4m` events.

Fenwick sweep:

```text
O((n + m) log n)
```

In typical accepted behavior, `m` is linear in the number of adjacent-pair occurrences.

So overall:

```text
O((n + m) log n)
```

Space:

```text
O(n + m)
```

---

# 14. Subtle Point: Why Count == 4 Is Sufficient

For a candidate rectangle, we already know the four corners exist.

If the total number of points in the closed rectangle is exactly 4, then:

- there is nothing inside
- there is nothing else on the border

So validity is guaranteed.

If the count is more than 4, then some extra point lies either:

- inside
- on an edge

and the rectangle is invalid.

So this single count check is enough.

---

# 15. Approach 3 — Segment Tree / 2D Range Tree Variants

You could also solve the rectangle counting part with:

- segment tree over y
- ordered map of x events
- range tree / BIT of vectors

But for this problem, offline Fenwick is simpler and usually the best engineering choice.

So these are alternatives, not the preferred version.

---

# 16. Common Mistakes

## Mistake 1: Checking only corners

That ignores interior/border constraints and fails on Examples 2 and 3.

## Mistake 2: Using non-adjacent y-pairs in a column

That can create vertical borders with extra points already on them.

## Mistake 3: Forgetting border points count as invalid

The phrase:

```text
inside or on its border
```

is critical.

## Mistake 4: Using raw coordinates directly in Fenwick

Coordinates go up to `8 * 10^7`, so compression is required.

## Mistake 5: Doing online 2D queries naively

That can become too slow. Offline prefix counting is much cleaner.

---

# 17. Dry Run on Example 3

```text
x = 1 column -> y = [1,2,3]
adjacent pairs: (1,2), (2,3)

x = 3 column -> y = [1,2,3]
adjacent pairs: (1,2), (2,3)
```

So candidates:

- rectangle `(1,3,1,2)` area = 2
- rectangle `(1,3,2,3)` area = 2

The large rectangle `(1,3,1,3)` is never generated, because `(1,3)` is not an adjacent pair in either column.

That is exactly correct, because `(1,2)` and `(3,2)` lie on the borders.

Both candidate rectangles have exactly 4 points inside their closed boundaries, so they are valid.

Answer:

```text
2
```

---

# 18. Correctness Sketch

## Lemma 1

In a valid rectangle, the two y-values on each vertical side must be adjacent among the points with that x-coordinate.

### Reason

Otherwise some point lies on the border between them, making the rectangle invalid.

## Lemma 2

Every valid rectangle is generated by the adjacent-pair sweep.

### Reason

Its left and right sides each correspond to the same adjacent y-pair `(bottom, top)`, so when the second x-column is processed, the rectangle is generated.

## Lemma 3

A generated candidate rectangle is valid iff the number of points in its closed boundary box is exactly 4.

### Reason

The four corners are known to exist. Any additional point in the closed box would be either inside or on the border.

## Theorem

The algorithm returns the maximum valid rectangle area.

### Reason

By Lemma 2, every valid rectangle is generated. By Lemma 3, validity is checked exactly. Therefore taking the maximum area among valid generated rectangles is correct.

---

# 19. Final Recommended Java Solution

This is the same implementation again, kept here for easy copy-paste:

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 2];
        }

        void add(int idx, int delta) {
            while (idx <= n) {
                bit[idx] += delta;
                idx += idx & -idx;
            }
        }

        int sum(int idx) {
            int res = 0;
            while (idx > 0) {
                res += bit[idx];
                idx -= idx & -idx;
            }
            return res;
        }
    }

    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Rect {
        int x1, x2, y1, y2;
        long area;
        Rect(int x1, int x2, int y1, int y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.area = 1L * (x2 - x1) * (y2 - y1);
        }
    }

    static class Event {
        int x, y;
        int rectId;
        int sign;
        Event(int x, int y, int rectId, int sign) {
            this.x = x;
            this.y = y;
            this.rectId = rectId;
            this.sign = sign;
        }
    }

    public long maxRectangleArea(int[] xCoord, int[] yCoord) {
        int n = xCoord.length;
        Point[] points = new Point[n];
        TreeSet<Integer> xSet = new TreeSet<>();
        TreeSet<Integer> ySet = new TreeSet<>();

        for (int i = 0; i < n; i++) {
            points[i] = new Point(xCoord[i], yCoord[i]);
            xSet.add(xCoord[i]);
            ySet.add(yCoord[i]);
        }

        Map<Integer, List<Integer>> byX = new HashMap<>();
        for (int i = 0; i < n; i++) {
            byX.computeIfAbsent(xCoord[i], k -> new ArrayList<>()).add(yCoord[i]);
        }

        List<Integer> sortedX = new ArrayList<>(xSet);
        Collections.sort(sortedX);

        Map<Long, Integer> lastX = new HashMap<>();
        List<Rect> rects = new ArrayList<>();

        for (int x : sortedX) {
            List<Integer> ys = byX.get(x);
            Collections.sort(ys);

            for (int i = 0; i + 1 < ys.size(); i++) {
                int y1 = ys.get(i);
                int y2 = ys.get(i + 1);
                long key = (((long) y1) << 32) ^ (y2 & 0xffffffffL);

                if (lastX.containsKey(key)) {
                    int prevX = lastX.get(key);
                    rects.add(new Rect(prevX, x, y1, y2));
                }
                lastX.put(key, x);
            }
        }

        if (rects.isEmpty()) return -1;

        List<Integer> sortedY = new ArrayList<>(ySet);
        Collections.sort(sortedY);

        Map<Integer, Integer> yRank = new HashMap<>();
        for (int i = 0; i < sortedY.size(); i++) {
            yRank.put(sortedY.get(i), i + 1);
        }

        Arrays.sort(points, Comparator.comparingInt(p -> p.x));

        List<Event> events = new ArrayList<>();
        for (int i = 0; i < rects.size(); i++) {
            Rect r = rects.get(i);

            int y2Rank = upperBound(sortedY, r.y2);
            int y1MinusRank = upperBound(sortedY, r.y1 - 1);

            events.add(new Event(r.x2, y2Rank, i, +1));
            events.add(new Event(r.x1 - 1, y2Rank, i, -1));
            events.add(new Event(r.x2, y1MinusRank, i, -1));
            events.add(new Event(r.x1 - 1, y1MinusRank, i, +1));
        }

        events.sort(Comparator.comparingInt(e -> e.x));

        Fenwick bit = new Fenwick(sortedY.size() + 2);
        long[] cnt = new long[rects.size()];

        int p = 0;
        for (Event e : events) {
            while (p < n && points[p].x <= e.x) {
                bit.add(yRank.get(points[p].y), 1);
                p++;
            }
            if (e.y > 0) {
                cnt[e.rectId] += 1L * e.sign * bit.sum(e.y);
            }
        }

        long ans = -1;
        for (int i = 0; i < rects.size(); i++) {
            if (cnt[i] == 4) {
                ans = Math.max(ans, rects.get(i).area);
            }
        }

        return ans;
    }

    private int upperBound(List<Integer> arr, int target) {
        int l = 0, r = arr.size();
        while (l < r) {
            int m = (l + r) >>> 1;
            if (arr.get(m) <= target) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l;
    }
}
```

---

# 20. Interview Summary

The trick is to realize that in a valid rectangle, each vertical side must connect **adjacent y-values** in its x-column, otherwise some point would lie on the border.

So sweep x-columns, record adjacent y-pairs, and whenever the same adjacent pair appears again, it forms a candidate rectangle.

Then validate the candidate by counting points in the closed rectangle using an offline 2D range-count query. The rectangle is valid iff that count is exactly 4.

This yields an efficient solution around:

```text
O((n + m) log n)
```

with `m` candidate rectangles.
