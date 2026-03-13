# 3624. Number of Integers With Popcount-Depth Equal to K II — Exhaustive Java Notes

## Problem Statement

You are given an integer array `nums`.

For any positive integer `x`, define the sequence:

```text
p0 = x
pi+1 = popcount(pi)
```

where `popcount(y)` is the number of set bits in the binary representation of `y`.

This sequence always eventually reaches `1`.

The **popcount-depth** of `x` is the smallest integer `d >= 0` such that:

```text
pd = 1
```

### Example

If:

```text
x = 7   (binary = 111)
```

then the sequence is:

```text
7 -> 3 -> 2 -> 1
```

So the popcount-depth of `7` is:

```text
3
```

---

You are also given queries of two types:

### Type 1

```text
[1, l, r, k]
```

Count how many indices `j` satisfy:

```text
l <= j <= r
popcountDepth(nums[j]) == k
```

### Type 2

```text
[2, idx, val]
```

Update:

```text
nums[idx] = val
```

Return an array containing the answers to all Type 1 queries.

---

## Constraints

```text
1 <= n == nums.length <= 10^5
1 <= nums[i] <= 10^15
1 <= queries.length <= 10^5
0 <= l <= r <= n - 1
0 <= k <= 5
0 <= idx <= n - 1
1 <= val <= 10^15
```

---

# 1. Core Observation

The value of `nums[i]` can be as large as `10^15`, but its popcount becomes very small very quickly.

For any `x <= 10^15`:

- `popcount(x)` is at most about `50`
- then `popcount(popcount(x))` is at most `6`
- then next step is at most `3`
- then at most `2`
- then `1`

So the depth is tiny.

In fact, under these constraints, the depth is always in a very small range, and the query only asks for:

```text
k in [0, 5]
```

That means we only need to classify each number into one of a few buckets.

So the dynamic problem becomes:

> maintain counts of indices by depth over ranges, under point updates.

That strongly suggests:

- one Fenwick Tree per depth, or
- one Segment Tree storing counts for each depth.

---

# 2. Popcount-Depth Function

Define:

```text
depth(1) = 0
depth(x) = 1 + depth(popcount(x)) for x > 1
```

Because `x` shrinks extremely fast, this is cheap to compute.

---

# 3. Approach 1 — Brute Force Per Query

## Idea

For each Type 1 query:

- scan from `l` to `r`
- compute or look up the depth of each number
- count how many equal `k`

For Type 2:

- simply update the array

## Java Code

```java
class SolutionBruteForce {
    public int[] popcountDepth(long[] nums, long[][] queries) {
        java.util.List<Integer> ans = new java.util.ArrayList<>();

        for (long[] q : queries) {
            if (q[0] == 1) {
                int l = (int) q[1];
                int r = (int) q[2];
                int k = (int) q[3];

                int count = 0;
                for (int i = l; i <= r; i++) {
                    if (depth(nums[i]) == k) count++;
                }
                ans.add(count);
            } else {
                int idx = (int) q[1];
                long val = q[2];
                nums[idx] = val;
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) res[i] = ans.get(i);
        return res;
    }

    private int depth(long x) {
        int d = 0;
        while (x != 1) {
            x = Long.bitCount(x);
            d++;
        }
        return d;
    }
}
```

## Complexity

For each range query:

```text
O(r - l + 1)
```

Worst case total:

```text
O(n * q)
```

Too slow for `10^5`.

---

# 4. Approach 2 — Prefix Counts Rebuilt After Every Update

## Idea

Maintain arrays of counts for every depth:

```text
prefix[k][i] = number of elements with depth k in nums[0..i]
```

Then a Type 1 query can be answered in `O(1)`.

But after every Type 2 update, all prefix arrays after that index may need rebuilding.

This is better conceptually, but still too expensive.

## Complexity

- Query: `O(1)`
- Update: `O(n)`

Worst case still too large.

---

# 5. Approach 3 — Fenwick Tree Per Depth

This is the cleanest solution.

## Idea

Since `k` is only from `0` to `5`, we can maintain:

```text
6 Fenwick Trees
```

where:

- Fenwick `d` stores `1` at index `i` if `nums[i]` has popcount-depth `d`
- otherwise `0`

Then:

### Type 1 query `[1, l, r, k]`

Answer is:

```text
BIT[k].sum(r) - BIT[k].sum(l - 1)
```

### Type 2 query `[2, idx, val]`

- compute old depth of `nums[idx]`
- compute new depth of `val`
- if different:
  - remove 1 from old BIT at `idx`
  - add 1 to new BIT at `idx`
- update `nums[idx] = val`

This gives fast range queries and fast point updates.

---

# 6. Why This Works

Each index belongs to exactly one depth bucket.

So for each depth `d`, the Fenwick Tree tracks exactly which indices currently have that depth.

A range query asking for depth `k` is just a frequency query on that tree.

Point update only affects one position, so Fenwick Trees are a perfect fit.

---

# 7. Recommended Java Solution — 6 Fenwick Trees

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int idx, int delta) {
            idx++; // 0-indexed -> 1-indexed
            while (idx <= n) {
                bit[idx] += delta;
                idx += idx & -idx;
            }
        }

        int sum(int idx) {
            idx++;
            int res = 0;
            while (idx > 0) {
                res += bit[idx];
                idx -= idx & -idx;
            }
            return res;
        }

        int rangeSum(int l, int r) {
            if (l > r) return 0;
            return sum(r) - (l == 0 ? 0 : sum(l - 1));
        }
    }

    public int[] popcountDepth(long[] nums, long[][] queries) {
        int n = nums.length;

        // Depth is tiny under given constraints, and query k is only 0..5.
        Fenwick[] bits = new Fenwick[6];
        for (int d = 0; d <= 5; d++) {
            bits[d] = new Fenwick(n);
        }

        int[] depths = new int[n];
        for (int i = 0; i < n; i++) {
            depths[i] = getDepth(nums[i]);
            if (depths[i] <= 5) {
                bits[depths[i]].add(i, 1);
            }
        }

        List<Integer> answer = new ArrayList<>();

        for (long[] q : queries) {
            if (q[0] == 1) {
                int l = (int) q[1];
                int r = (int) q[2];
                int k = (int) q[3];

                if (k < 0 || k > 5) {
                    answer.add(0);
                } else {
                    answer.add(bits[k].rangeSum(l, r));
                }
            } else {
                int idx = (int) q[1];
                long val = q[2];

                int oldDepth = depths[idx];
                int newDepth = getDepth(val);

                if (oldDepth != newDepth) {
                    if (oldDepth <= 5) bits[oldDepth].add(idx, -1);
                    if (newDepth <= 5) bits[newDepth].add(idx, 1);
                    depths[idx] = newDepth;
                }

                nums[idx] = val;
            }
        }

        int[] res = new int[answer.size()];
        for (int i = 0; i < answer.size(); i++) {
            res[i] = answer.get(i);
        }
        return res;
    }

    private int getDepth(long x) {
        int d = 0;
        while (x != 1) {
            x = Long.bitCount(x);
            d++;
        }
        return d;
    }
}
```

---

# 8. Complexity Analysis

Let:

- `n = nums.length`
- `q = queries.length`

## Building

For each element we compute depth in very small time and update one Fenwick Tree:

```text
O(n log n)
```

More precisely, depth computation is effectively constant because popcount shrinks immediately.

## Each query

### Type 1

One Fenwick range sum:

```text
O(log n)
```

### Type 2

At most two Fenwick updates:

```text
O(log n)
```

## Total

```text
O((n + q) log n)
```

## Space

We store 6 Fenwick Trees of size `n`:

```text
O(6n) = O(n)
```

---

# 9. Approach 4 — Segment Tree with 6 Counts per Node

This is another good solution.

## Idea

Build a segment tree where each node stores:

```text
cnt[0..5]
```

where `cnt[d]` is the number of elements in that segment whose depth is `d`.

### Merge rule

To merge left and right child:

```text
parent.cnt[d] = left.cnt[d] + right.cnt[d]
```

### Point update

Recompute one leaf, then update ancestors.

### Range query

Collect counts for the interval and return `cnt[k]`.

---

# 10. Java Code — Segment Tree Version

```java
import java.util.*;

class SolutionSegmentTree {
    static class SegTree {
        int n;
        int[][] tree; // tree[node][depth]

        SegTree(int[] depths) {
            n = depths.length;
            tree = new int[4 * n][6];
            build(1, 0, n - 1, depths);
        }

        private void build(int node, int l, int r, int[] depths) {
            if (l == r) {
                tree[node][depths[l]] = 1;
                return;
            }
            int mid = (l + r) >>> 1;
            build(node << 1, l, mid, depths);
            build(node << 1 | 1, mid + 1, r, depths);
            pull(node);
        }

        private void pull(int node) {
            for (int d = 0; d <= 5; d++) {
                tree[node][d] = tree[node << 1][d] + tree[node << 1 | 1][d];
            }
        }

        void update(int idx, int oldDepth, int newDepth) {
            update(1, 0, n - 1, idx, oldDepth, newDepth);
        }

        private void update(int node, int l, int r, int idx, int oldDepth, int newDepth) {
            if (l == r) {
                tree[node][oldDepth]--;
                tree[node][newDepth]++;
                return;
            }
            int mid = (l + r) >>> 1;
            if (idx <= mid) update(node << 1, l, mid, idx, oldDepth, newDepth);
            else update(node << 1 | 1, mid + 1, r, idx, oldDepth, newDepth);
            pull(node);
        }

        int query(int ql, int qr, int k) {
            return query(1, 0, n - 1, ql, qr, k);
        }

        private int query(int node, int l, int r, int ql, int qr, int k) {
            if (ql <= l && r <= qr) return tree[node][k];
            int mid = (l + r) >>> 1;
            int ans = 0;
            if (ql <= mid) ans += query(node << 1, l, mid, ql, qr, k);
            if (qr > mid) ans += query(node << 1 | 1, mid + 1, r, ql, qr, k);
            return ans;
        }
    }

    public int[] popcountDepth(long[] nums, long[][] queries) {
        int n = nums.length;
        int[] depths = new int[n];
        for (int i = 0; i < n; i++) depths[i] = getDepth(nums[i]);

        SegTree st = new SegTree(depths);
        List<Integer> ans = new ArrayList<>();

        for (long[] q : queries) {
            if (q[0] == 1) {
                int l = (int) q[1];
                int r = (int) q[2];
                int k = (int) q[3];
                if (k < 0 || k > 5) ans.add(0);
                else ans.add(st.query(l, r, k));
            } else {
                int idx = (int) q[1];
                long val = q[2];
                int newDepth = getDepth(val);
                if (newDepth != depths[idx]) {
                    st.update(idx, depths[idx], newDepth);
                    depths[idx] = newDepth;
                }
                nums[idx] = val;
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) res[i] = ans.get(i);
        return res;
    }

    private int getDepth(long x) {
        int d = 0;
        while (x != 1) {
            x = Long.bitCount(x);
            d++;
        }
        return d;
    }
}
```

---

# 11. Fenwick vs Segment Tree

Both are valid.

## Fenwick Tree approach

Pros:

- simpler
- less memory
- easy because `k` is small and fixed

Cons:

- requires one BIT per depth

## Segment Tree approach

Pros:

- generalizes well if depth categories were more complex
- single structure

Cons:

- more code
- more memory

For this problem, **Fenwick Trees are cleaner**.

---

# 12. Small Popcount-Depth Table

It helps to build intuition.

```text
x = 1    -> depth 0
x = 2    -> 2 -> 1                depth 1
x = 4    -> 1                     depth 1
x = 3    -> 3 -> 2 -> 1           depth 2
x = 5    -> 5 -> 2 -> 1           depth 2
x = 6    -> 6 -> 2 -> 1           depth 2
x = 7    -> 7 -> 3 -> 2 -> 1      depth 3
```

Actually the path is determined only by repeated popcounts.

---

# 13. Important Optimization Insight

You might wonder whether we need memoization for `getDepth(x)`.

The answer is: not really necessary.

Why?

Because for `x <= 10^15`:

- first popcount reduces to at most about `50`
- then to at most `6`
- then to `3`, `2`, `1`

So each depth calculation takes only a handful of iterations.

Still, memoization is harmless if you want it.

---

# 14. Optional Memoized Depth Function

```java
private final Map<Long, Integer> memo = new HashMap<>();

private int getDepth(long x) {
    if (x == 1) return 0;
    if (memo.containsKey(x)) return memo.get(x);
    int ans = 1 + getDepth(Long.bitCount(x));
    memo.put(x, ans);
    return ans;
}
```

This is fine, but not essential.

---

# 15. Correctness Sketch

## Lemma 1

Each array index belongs to exactly one popcount-depth bucket.

### Reason

Every positive integer has exactly one popcount-depth.

## Lemma 2

Fenwick Tree `d` stores exactly the set of indices whose current value has depth `d`.

### Reason

During initialization, every index is inserted into exactly one tree.
During updates, if the depth changes, we remove it from the old tree and insert it into the new one.

## Lemma 3

For any Type 1 query `[1, l, r, k]`, the answer is exactly the number of indices in `[l, r]` whose depth is `k`.

### Reason

Fenwick `k` stores 1 for exactly those indices with depth `k`.
So its range sum on `[l, r]` is exactly the requested count.

## Lemma 4

Type 2 updates preserve the invariant in Lemma 2.

### Reason

Only one array position changes.
Its old contribution is removed from the old-depth structure, and its new contribution is added to the new-depth structure.

## Theorem

The algorithm returns the correct answer for all Type 1 queries.

### Reason

By Lemmas 2–4, the maintained structures are always correct, and every range query reads the correct count.

---

# 16. Common Mistakes

## Mistake 1 — Forgetting that depth of 1 is 0

```text
1 already equals 1
```

so its depth is zero.

## Mistake 2 — Using `int` instead of `long` for values

The array values go up to:

```text
10^15
```

So use `long`.

## Mistake 3 — Overengineering the depth computation

Repeated popcount shrinks extremely fast. No heavy preprocessing is needed.

## Mistake 4 — Using only one BIT

One BIT cannot answer "count of depth exactly k" unless it stores richer information.
The simplest design is one BIT per depth.

## Mistake 5 — Ignoring `k > actual max depth`

The query may ask up to `k = 5`. If some value is never in that bucket, answer is naturally 0.

---

# 17. Comparison of Approaches

| Approach                   |      Query |     Update |  Space | Verdict     |
| -------------------------- | ---------: | ---------: | -----: | ----------- |
| Brute force scan           |     `O(n)` |     `O(1)` | `O(1)` | too slow    |
| Rebuild prefix counts      |     `O(1)` |     `O(n)` | `O(n)` | too slow    |
| 6 Fenwick Trees            | `O(log n)` | `O(log n)` | `O(n)` | recommended |
| Segment Tree with 6 counts | `O(log n)` | `O(log n)` | `O(n)` | also good   |

---

# 18. Final Recommended Answer

Use:

- a function `getDepth(long x)`
- 6 Fenwick Trees, one for each possible query depth `0..5`
- point updates for Type 2
- range-sum queries for Type 1

This gives:

```text
Time:  O((n + q) log n)
Space: O(n)
```

and is fully efficient for the given constraints.

---

# 19. Final Clean Java Solution

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int idx, int delta) {
            idx++;
            while (idx <= n) {
                bit[idx] += delta;
                idx += idx & -idx;
            }
        }

        int sum(int idx) {
            idx++;
            int res = 0;
            while (idx > 0) {
                res += bit[idx];
                idx -= idx & -idx;
            }
            return res;
        }

        int rangeSum(int l, int r) {
            if (l > r) return 0;
            return sum(r) - (l == 0 ? 0 : sum(l - 1));
        }
    }

    public int[] popcountDepth(long[] nums, long[][] queries) {
        int n = nums.length;

        Fenwick[] bits = new Fenwick[6];
        for (int i = 0; i < 6; i++) bits[i] = new Fenwick(n);

        int[] depth = new int[n];
        for (int i = 0; i < n; i++) {
            depth[i] = getDepth(nums[i]);
            if (depth[i] <= 5) bits[depth[i]].add(i, 1);
        }

        List<Integer> ans = new ArrayList<>();

        for (long[] q : queries) {
            if (q[0] == 1) {
                int l = (int) q[1];
                int r = (int) q[2];
                int k = (int) q[3];
                ans.add((k >= 0 && k <= 5) ? bits[k].rangeSum(l, r) : 0);
            } else {
                int idx = (int) q[1];
                long val = q[2];

                int newDepth = getDepth(val);
                int oldDepth = depth[idx];

                if (newDepth != oldDepth) {
                    if (oldDepth <= 5) bits[oldDepth].add(idx, -1);
                    if (newDepth <= 5) bits[newDepth].add(idx, 1);
                    depth[idx] = newDepth;
                }

                nums[idx] = val;
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) res[i] = ans.get(i);
        return res;
    }

    private int getDepth(long x) {
        int d = 0;
        while (x != 1) {
            x = Long.bitCount(x);
            d++;
        }
        return d;
    }
}
```
