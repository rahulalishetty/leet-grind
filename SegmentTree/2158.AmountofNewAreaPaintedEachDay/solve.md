# 2158. Amount of New Area Painted Each Day

## Problem Restatement

You are given `paint`, where:

```text
paint[i] = [start_i, end_i]
```

means that on day `i`, you want to paint the half-open interval:

```text
[start_i, end_i)
```

You only want to count **newly painted** area.
If some part of the interval was already painted on previous days, repainting it does **not** count.

Return an array `worklog` where:

```text
worklog[i] = amount of new area painted on day i
```

---

## Key Constraints

```text
1 <= paint.length <= 10^5
0 <= start_i < end_i <= 5 * 10^4
```

These constraints strongly suggest:

- we need something much faster than checking overlap against all previous days
- the coordinate range is relatively small (`<= 50000`)
- interval-skipping or union-find-like tricks are very suitable

---

# Core Insight

Each day, we need to count how much of `[start, end)` is still unpainted.

A naive solution would examine each unit segment one by one and mark it painted.

That actually suggests something important:

- the coordinates are discrete enough
- once a point/segment is painted, we want to **skip it quickly** in the future

This leads to the best approach:

> maintain a “next unpainted position” pointer so already painted regions can be jumped over in near-constant time

That is essentially a **disjoint set / union-find on positions**.

---

# Interval Interpretation

Since painting `[start, end)` covers all unit segments:

```text
start, start+1, ..., end-1
```

the task reduces to:

- every day, count how many integer positions in that range have not been painted yet
- once painted, they should be efficiently skipped later

---

# Approach 1: Union-Find / Next-Unpainted Pointer (Recommended)

## Idea

Let `parent[x]` represent the next candidate position at or after `x` that may still be unpainted.

Initially:

```text
parent[x] = x
```

for all `x`.

When position `x` gets painted, we union it to `x + 1` by setting:

```text
parent[x] = find(x + 1)
```

Then later, if we query `find(x)`, we jump directly to the first unpainted position at or after `x`.

---

## How a day is processed

Suppose today’s interval is:

```text
[start, end)
```

We do:

1. `x = find(start)`
2. while `x < end`:
   - this position is newly painted
   - increment today’s count
   - mark it painted by linking it to `find(x + 1)`
   - move to next unpainted position

Because painted positions are permanently skipped, each position is processed at most once overall.

That is the key reason the solution is fast.

---

## Why this is so efficient

Even though each day may seem to loop over a range, each unit position is painted only once globally.

After that, `find()` skips it.

So across all days, the total work is roughly proportional to the coordinate range, not to `n * range`.

---

## Java Code

```java
class Solution {
    private int[] parent;

    public int[] amountPainted(int[][] paint) {
        int max = 50001; // because end_i <= 50000, we need x+1 safely
        parent = new int[max + 1];

        for (int i = 0; i <= max; i++) {
            parent[i] = i;
        }

        int[] ans = new int[paint.length];

        for (int i = 0; i < paint.length; i++) {
            int start = paint[i][0];
            int end = paint[i][1];
            int work = 0;

            int x = find(start);
            while (x < end) {
                work++;
                parent[x] = find(x + 1); // mark x as painted
                x = find(x);             // jump to next unpainted
            }

            ans[i] = work;
        }

        return ans;
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
}
```

---

## Complexity

Let `M = 50000`.

Each unit position is painted at most once, and union-find operations are almost constant amortized.

Time complexity:

```text
O(M * α(M) + n)
```

which is effectively near-linear.

Space complexity:

```text
O(M)
```

---

## Pros

- Best practical solution
- Very elegant once understood
- Exploits the coordinate bound perfectly

## Cons

- The “union-find over positions” trick is non-obvious if you have not seen it before

---

# Approach 2: TreeMap of Disjoint Painted Intervals

## Idea

Maintain a set of already painted disjoint intervals in a `TreeMap`.

For each new interval `[start, end)`:

- find overlaps with existing painted intervals
- compute how much of today’s interval is not already covered
- merge everything into one combined painted interval

This is a standard interval-union approach.

---

## How it works

Suppose painted intervals are stored as:

```text
start -> end
```

For a new interval:

1. locate the interval just before `start`
2. merge all overlapping/adjacent intervals
3. subtract already covered lengths from today’s interval
4. insert the merged interval back

This works because painted area over time becomes a union of disjoint intervals.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] amountPainted(int[][] paint) {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        int[] ans = new int[paint.length];

        for (int i = 0; i < paint.length; i++) {
            int start = paint[i][0];
            int end = paint[i][1];
            int originalLen = end - start;
            int covered = 0;

            Integer key = map.floorKey(start);
            if (key != null && map.get(key) < start) {
                key = map.higherKey(key);
            }

            int newStart = start;
            int newEnd = end;

            while (key != null && key <= end) {
                int curStart = key;
                int curEnd = map.get(key);

                if (curStart > newEnd) break;

                covered += Math.max(0, Math.min(end, curEnd) - Math.max(start, curStart));
                newStart = Math.min(newStart, curStart);
                newEnd = Math.max(newEnd, curEnd);

                Integer nextKey = map.higherKey(key);
                map.remove(key);
                key = nextKey;
            }

            map.put(newStart, newEnd);
            ans[i] = originalLen - covered;
        }

        return ans;
    }
}
```

---

## Complexity

Each interval insertion/merge involves `TreeMap` operations:

- searching: `O(log n)`
- merging overlapping intervals: proportional to number of overlaps

Overall time complexity is roughly:

```text
O(n log n + total overlaps)
```

Space complexity:

```text
O(n)
```

---

## Pros

- Good general interval-union solution
- Does not depend on the fixed coordinate limit as strongly

## Cons

- More complicated than union-find skipping
- Easy to make mistakes in overlap accounting
- Usually slower in practice for this problem

---

# Approach 3: Segment Tree with Lazy Marking

## Idea

Build a segment tree over coordinates `[0, 50000)`.

For each day:

1. query how much of `[start, end)` is already painted
2. newly painted amount = interval length - painted amount
3. update `[start, end)` as fully painted

Each node stores how much length in its segment is already painted.

---

## Why this works

A segment tree can maintain:

- total painted length in a segment
- lazy propagation for full cover

Then each interval can be processed in logarithmic time.

This is a robust interval-coverage technique.

---

## Java Code

```java
class Solution {
    private static class SegTree {
        int[] tree;
        boolean[] lazy;

        SegTree(int n) {
            tree = new int[4 * n];
            lazy = new boolean[4 * n];
        }

        int query(int node, int l, int r, int ql, int qr) {
            if (ql >= r || qr <= l) return 0;
            if (ql <= l && r <= qr) return tree[node];

            push(node, l, r);
            int mid = (l + r) / 2;
            return query(node * 2, l, mid, ql, qr)
                 + query(node * 2 + 1, mid, r, ql, qr);
        }

        void update(int node, int l, int r, int ql, int qr) {
            if (ql >= r || qr <= l) return;
            if (ql <= l && r <= qr) {
                tree[node] = r - l;
                lazy[node] = true;
                return;
            }

            push(node, l, r);
            int mid = (l + r) / 2;
            update(node * 2, l, mid, ql, qr);
            update(node * 2 + 1, mid, r, ql, qr);
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        void push(int node, int l, int r) {
            if (!lazy[node] || r - l == 1) return;
            int mid = (l + r) / 2;

            tree[node * 2] = mid - l;
            tree[node * 2 + 1] = r - mid;
            lazy[node * 2] = true;
            lazy[node * 2 + 1] = true;
            lazy[node] = false;
        }
    }

    public int[] amountPainted(int[][] paint) {
        int MAX = 50001;
        SegTree st = new SegTree(MAX);
        int[] ans = new int[paint.length];

        for (int i = 0; i < paint.length; i++) {
            int l = paint[i][0];
            int r = paint[i][1];
            int already = st.query(1, 0, MAX, l, r);
            ans[i] = (r - l) - already;
            st.update(1, 0, MAX, l, r);
        }

        return ans;
    }
}
```

---

## Complexity

Each day performs one query and one update.

Time complexity:

```text
O(n log M)
```

where `M = 50000`.

Space complexity:

```text
O(M)
```

---

## Pros

- Very general
- Strong if coordinate range or interval updates become more complex

## Cons

- Overkill here
- More code and more bug-prone than union-find skipping

---

# Approach 4: Naive Boolean Painting Array

## Idea

Because coordinates only go up to `50000`, one may try:

- maintain `painted[x] = true/false`
- for each day, scan `x` from `start` to `end - 1`
- count and mark unpainted positions

This is correct but too slow in the worst case.

---

## Java Code

```java
class Solution {
    public int[] amountPainted(int[][] paint) {
        boolean[] painted = new boolean[50001];
        int[] ans = new int[paint.length];

        for (int i = 0; i < paint.length; i++) {
            int start = paint[i][0];
            int end = paint[i][1];
            int work = 0;

            for (int x = start; x < end; x++) {
                if (!painted[x]) {
                    painted[x] = true;
                    work++;
                }
            }

            ans[i] = work;
        }

        return ans;
    }
}
```

---

## Complexity

Worst-case time complexity:

```text
O(n * M)
```

with `M = 50000`, which is too large for `n = 10^5`.

Space complexity:

```text
O(M)
```

---

## Pros

- Very easy to understand

## Cons

- Too slow for the constraints

---

# Deep Intuition

## Why union-find works on a line here

This is not union-find over connected components in the usual graph sense.

Instead, we use it as a **jump pointer structure**:

- once position `x` is painted, redirect it to `x + 1`
- `find(x)` then means: “where is the next unpainted position at or after `x`?”

That is exactly the operation we need repeatedly.

This is why the method feels almost like “path compression over a linked list of unpainted coordinates.”

---

## Why every coordinate is processed only once

The crucial efficiency argument is:

- when a coordinate `x` is first painted, it gets linked forward
- afterward, future days never process `x` again individually
- path compression makes repeated jumps faster and faster

So although many intervals may overlap heavily, the total amount of per-coordinate work across all days is still small.

---

## Why the answer is interval length minus already-covered portion

Every day’s interval is conceptually:

```text
[start, end)
```

Some pieces were painted earlier, some were not.

The newly painted work is exactly the unpainted measure of that interval.

All efficient approaches are just different ways to discover that measure quickly.

---

# Correctness Sketch for Approach 1

We prove the union-find skipping approach is correct.

## Data structure meaning

At any time, `find(x)` returns the smallest position `y >= x` that is not yet painted.
If every position from `x` onward in the relevant range is already painted, it returns a value at or beyond the end of the interval.

## Initially

Before any painting, every position is unpainted, so:

```text
parent[x] = x
```

and `find(x) = x`.

This satisfies the invariant.

## Painting a position

When position `x` is newly painted, we set:

```text
parent[x] = find(x + 1)
```

So from then on, `find(x)` skips over `x` to the next unpainted position.

This preserves the invariant.

## Processing one day

For interval `[start, end)`, we repeatedly set:

```text
x = find(current)
```

If `x >= end`, there are no unpainted positions left in the interval.

Otherwise, `x` is exactly one newly painted unit segment inside the interval, so counting it is correct, and marking it painted updates the structure correctly.

Since this continues until no unpainted positions remain, the total count is exactly the amount of new area painted that day.

Therefore the algorithm is correct.

---

# Example Walkthrough

## Example 1

```text
paint = [[1,4],[4,7],[5,8]]
```

### Day 0: [1,4)

Unpainted positions are:

```text
1, 2, 3
```

Count = 3

### Day 1: [4,7)

Unpainted positions are:

```text
4, 5, 6
```

Count = 3

### Day 2: [5,8)

Positions `5, 6` are already painted, so union-find jumps directly to:

```text
7
```

Only position `7` is newly painted.

Count = 1

Answer:

```text
[3,3,1]
```

---

## Example 3

```text
paint = [[1,5],[2,4]]
```

### Day 0: [1,5)

Paint:

```text
1, 2, 3, 4
```

Count = 4

### Day 1: [2,4)

`find(2)` skips directly beyond all already painted positions in that range, so no new positions are counted.

Count = 0

Answer:

```text
[4,0]
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
class Solution {
    private int[] parent;

    public int[] amountPainted(int[][] paint) {
        int max = 50001;
        parent = new int[max + 1];

        for (int i = 0; i <= max; i++) {
            parent[i] = i;
        }

        int[] ans = new int[paint.length];

        for (int i = 0; i < paint.length; i++) {
            int start = paint[i][0];
            int end = paint[i][1];
            int work = 0;

            int x = find(start);
            while (x < end) {
                work++;
                parent[x] = find(x + 1);
                x = find(x);
            }

            ans[i] = work;
        }

        return ans;
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                |           Time Complexity | Space Complexity | Recommended |
| ---------- | ---------------------------------------- | ------------------------: | ---------------: | ----------- |
| Approach 1 | Union-find / next-unpainted skip pointer | Near `O(M + n)` amortized |           `O(M)` | Yes         |
| Approach 2 | TreeMap of merged painted intervals      |   `O(n log n + overlaps)` |           `O(n)` | Good        |
| Approach 3 | Segment tree for covered length          |              `O(n log M)` |           `O(M)` | Good        |
| Approach 4 | Naive boolean scan                       |                `O(n * M)` |           `O(M)` | No          |

Here `M = 50000`.

---

# Pattern Recognition Takeaway

This problem is a classic sign for the pattern:

- many interval updates
- coverage counted only once
- fixed small-ish coordinate range
- need to skip already processed positions efficiently

When you see that, strongly consider:

- union-find on coordinates
- "next available" / "next unpainted" pointers

This is one of the nicest uses of DSU outside traditional graph connectivity.

---

# Final Takeaway

The cleanest solution is:

1. treat each unit position as paintable once
2. maintain a union-find where each painted position points to the next candidate
3. when processing an interval, repeatedly jump to the next unpainted position
4. paint it, count it, and link it forward

That gives an efficient and elegant solution that handles the constraints comfortably.
