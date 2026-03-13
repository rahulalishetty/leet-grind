# 2250. Count Number of Rectangles Containing Each Point

## Problem Restatement

We are given:

- `rectangles[i] = [li, hi]`
- `points[j] = [xj, yj]`

Each rectangle has:

- bottom-left corner at `(0, 0)`
- top-right corner at `(li, hi)`

A rectangle contains a point `(x, y)` if:

```text
x <= li and y <= hi
```

because all rectangles start from `(0,0)`.

For each point, we need to count how many rectangles contain it.

---

## Key Observation

A rectangle `[l, h]` contains point `[x, y]` exactly when:

```text
l >= x
and
h >= y
```

So for each point, we need to count rectangles whose:

- width is at least `x`
- height is at least `y`

The big clue in the constraints is:

```text
1 <= hi, yj <= 100
```

Height is tiny.

Width can be huge (`10^9`), but height is only from `1` to `100`.

That means we should organize the solution primarily around height buckets.

---

# Approach 1 — Brute Force Check Every Rectangle for Every Point

## Intuition

The most direct solution is:

For each point:

- iterate over every rectangle
- check whether the rectangle contains that point
- count how many do

This is correct but too slow for the full constraints.

---

## Java Code

```java
class Solution {
    public int[] countRectangles(int[][] rectangles, int[][] points) {
        int[] ans = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            int x = points[i][0];
            int y = points[i][1];

            int count = 0;
            for (int[] rect : rectangles) {
                if (rect[0] >= x && rect[1] >= y) {
                    count++;
                }
            }

            ans[i] = count;
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let:

- `R = rectangles.length`
- `P = points.length`

### Time Complexity

```text
O(R * P)
```

In the worst case:

```text
5 * 10^4 * 5 * 10^4 = 2.5 * 10^9
```

which is too slow.

### Space Complexity

```text
O(1)
```

excluding the output array.

---

# Approach 2 — Height Buckets + Sort Widths + Binary Search

## Intuition

Since height is only from `1` to `100`, we can group rectangles by height.

For each possible height `h`, store the widths of all rectangles having that exact height.

Then sort each bucket.

Now for a query point `(x, y)`:

We need all rectangles with:

```text
height >= y
width >= x
```

So we iterate heights from `y` to `100`.

For each height bucket:

- use binary search to count how many widths are at least `x`

That gives the total.

This is the classic intended solution.

---

## Why This Works Well

The height loop is small:

```text
at most 100 iterations
```

And inside each bucket, binary search is fast.

So each point is processed efficiently.

---

## Algorithm

1. Create 101 lists:
   - `buckets[h]` stores widths of rectangles with height `h`
2. Put each rectangle width into its height bucket
3. Sort each bucket
4. For each point `(x, y)`:
   - initialize count = 0
   - for each height `h` from `y` to `100`:
     - in `buckets[h]`, find the first width `>= x`
     - all widths after that position are valid
   - store result
5. Return answer array

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] countRectangles(int[][] rectangles, int[][] points) {
        List<Integer>[] buckets = new ArrayList[101];
        for (int h = 0; h <= 100; h++) {
            buckets[h] = new ArrayList<>();
        }

        for (int[] rect : rectangles) {
            buckets[rect[1]].add(rect[0]);
        }

        for (int h = 1; h <= 100; h++) {
            Collections.sort(buckets[h]);
        }

        int[] ans = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            int x = points[i][0];
            int y = points[i][1];

            int count = 0;
            for (int h = y; h <= 100; h++) {
                List<Integer> widths = buckets[h];
                int idx = lowerBound(widths, x);
                count += widths.size() - idx;
            }

            ans[i] = count;
        }

        return ans;
    }

    private int lowerBound(List<Integer> list, int target) {
        int left = 0, right = list.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

## Complexity Analysis

Let:

- `R = rectangles.length`
- `P = points.length`

### Time Complexity

Building buckets:

```text
O(R)
```

Sorting all buckets together:

```text
O(R log R)
```

Processing each point:

- up to 100 heights
- one binary search per height

So:

```text
O(P * 100 * log R)
```

Since 100 is constant, this is effectively:

```text
O(R log R + P log R)
```

### Space Complexity

Buckets store all rectangle widths:

```text
O(R)
```

---

# Approach 3 — Offline Sweep by Width + Fenwick Tree on Heights

## Intuition

Another strong approach is to sort rectangles and points by `x`.

For a point `(x, y)`, we want all rectangles with:

```text
width >= x
height >= y
```

So if we process points in descending order of `x`, then whenever we are at a point:

- all rectangles with width at least `x` can be inserted into a data structure
- then we only need to count how many inserted rectangles have height at least `y`

Since height is only `1..100`, a Fenwick Tree over heights is perfect.

This is a clean offline solution.

---

## Algorithm

1. Sort rectangles by width descending
2. Create indexed points: `(x, y, originalIndex)` and sort by `x` descending
3. Maintain a Fenwick Tree over height values `1..100`
4. Sweep through points:
   - while current rectangle width `>= point.x`, insert its height into Fenwick Tree
   - query how many inserted heights are in `[y, 100]`
5. Store the result in the original point index
6. Return answer array

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] countRectangles(int[][] rectangles, int[][] points) {
        Arrays.sort(rectangles, (a, b) -> Integer.compare(b[0], a[0]));

        int n = points.length;
        int[][] indexedPoints = new int[n][3];
        for (int i = 0; i < n; i++) {
            indexedPoints[i][0] = points[i][0];
            indexedPoints[i][1] = points[i][1];
            indexedPoints[i][2] = i;
        }

        Arrays.sort(indexedPoints, (a, b) -> Integer.compare(b[0], a[0]));

        Fenwick bit = new Fenwick(101);
        int[] ans = new int[n];

        int r = 0;
        for (int[] p : indexedPoints) {
            int x = p[0], y = p[1], idx = p[2];

            while (r < rectangles.length && rectangles[r][0] >= x) {
                bit.add(rectangles[r][1], 1);
                r++;
            }

            ans[idx] = bit.query(100) - bit.query(y - 1);
        }

        return ans;
    }

    static class Fenwick {
        int[] tree;

        Fenwick(int n) {
            tree = new int[n + 2];
        }

        void add(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }
            return sum;
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

Sorting rectangles:

```text
O(R log R)
```

Sorting points:

```text
O(P log P)
```

Fenwick updates and queries:

```text
O((R + P) log 100)
```

Since `log 100` is constant, total is effectively:

```text
O(R log R + P log P)
```

### Space Complexity

```text
O(P + 100)
```

plus sorting structures.

This is asymptotically very strong.

---

# Approach 4 — Height Buckets + Prefix of Sorted Arrays Concept

## Intuition

This is more of a conceptual variant of Approach 2.

Instead of thinking:

- exact height buckets from `y` to `100`

you can think:

For each height threshold `h`, consider all rectangles with height at least `h`, and keep their widths sorted.

Then a point `(x, y)` only needs a single binary search on the structure for threshold `y`.

This can be made to work, but building all threshold lists explicitly would duplicate a lot of data.

So while it is a useful conceptual simplification, it is less memory efficient than Approach 2 or Approach 3.

---

# Worked Example

## Example 1

```text
rectangles = [[1,2],[2,3],[2,5]]
points = [[2,1],[1,4]]
```

### For point `(2,1)`

We need rectangles with:

```text
width >= 2
height >= 1
```

Check rectangles:

- `[1,2]` -> width too small
- `[2,3]` -> valid
- `[2,5]` -> valid

Count:

```text
2
```

### For point `(1,4)`

We need rectangles with:

```text
width >= 1
height >= 4
```

Check rectangles:

- `[1,2]` -> height too small
- `[2,3]` -> height too small
- `[2,5]` -> valid

Count:

```text
1
```

Answer:

```text
[2,1]
```

---

# Why the Bucket + Binary Search Approach Is Correct

## Claim 1

A rectangle `[l, h]` contains point `[x, y]` iff:

```text
l >= x and h >= y
```

### Proof

Since every rectangle starts at `(0,0)` and ends at `(l,h)`, a point is inside exactly when its coordinates do not exceed the rectangle’s top-right coordinates.

So the condition is necessary and sufficient.

Proved.

---

## Claim 2

For a fixed point `(x, y)`, it is sufficient to inspect only rectangles with height at least `y`.

### Proof

If `h < y`, then rectangle `[l,h]` cannot contain the point because the point’s y-coordinate is outside the rectangle.

If `h >= y`, then containment depends only on whether `l >= x`.

So only heights `y..100` matter.

Proved.

---

## Claim 3

Within a fixed height bucket, binary search correctly counts how many rectangles have width at least `x`.

### Proof

Each bucket is sorted by width.

Lower bound finds the first position where width is at least `x`.

All widths from that position onward satisfy `>= x`, and those before it do not.

Therefore:

```text
bucket.size() - lowerBound(bucket, x)
```

is exactly the number of valid rectangles in that bucket.

Proved.

---

# Comparison of Approaches

## Approach 1 — Brute force

Pros:

- simplest
- easy to verify

Cons:

- far too slow

---

## Approach 2 — Height buckets + binary search

Pros:

- directly exploits `height <= 100`
- simple and efficient
- very common interview solution

Cons:

- per point loops through up to 100 heights

This is the most standard solution.

---

## Approach 3 — Offline sweep + Fenwick Tree

Pros:

- elegant
- asymptotically excellent
- uses sorting and one sweep

Cons:

- slightly more advanced to derive

This is often the strongest algorithmic solution.

---

## Approach 4 — Threshold-height precomputation idea

Pros:

- conceptually simple once understood

Cons:

- wasteful if implemented literally

---

# Final Recommended Java Solution

For this problem, the most practical balance is **Approach 2**.

```java
import java.util.*;

class Solution {
    public int[] countRectangles(int[][] rectangles, int[][] points) {
        List<Integer>[] buckets = new ArrayList[101];
        for (int h = 0; h <= 100; h++) {
            buckets[h] = new ArrayList<>();
        }

        for (int[] rect : rectangles) {
            buckets[rect[1]].add(rect[0]);
        }

        for (int h = 1; h <= 100; h++) {
            Collections.sort(buckets[h]);
        }

        int[] ans = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            int x = points[i][0];
            int y = points[i][1];

            int count = 0;
            for (int h = y; h <= 100; h++) {
                List<Integer> widths = buckets[h];
                int idx = lowerBound(widths, x);
                count += widths.size() - idx;
            }

            ans[i] = count;
        }

        return ans;
    }

    private int lowerBound(List<Integer> list, int target) {
        int left = 0, right = list.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

# Complexity Summary

Let:

- `R = rectangles.length`
- `P = points.length`

## Approach 1

```text
Time:  O(R * P)
Space: O(1)
```

## Approach 2

```text
Time:  O(R log R + P * 100 * log R)
Space: O(R)
```

## Approach 3

```text
Time:  O(R log R + P log P)
Space: O(P)
```

---

# Final Takeaway

The crucial constraint is:

```text
height <= 100
```

That means the problem is best attacked by organizing rectangles by height.

Then for each point:

- only heights `>= y` matter
- within each such bucket, we only need to count widths `>= x`

That leads naturally to the bucket + binary search solution, while the offline Fenwick sweep gives an even more algorithmic
