# 3161. Block Placement Queries — Exhaustive Java Notes

## Problem Statement

There is an infinite number line starting at `0` and extending to the right.

You are given queries of two types:

- `queries[i] = [1, x]`
  Build an obstacle at position `x`.

- `queries[i] = [2, x, sz]`
  Check whether a block of size `sz` can be placed somewhere inside:

```text
[0, x]
```

such that:

- the whole block lies inside `[0, x]`
- it does **not intersect** any obstacle
- it **may touch** an obstacle

We do **not** actually place the block. Each type-2 query is only a check.

Return a boolean array containing the answers for all type-2 queries.

---

## Example 1

```text
Input: queries = [[1,2],[2,3,3],[2,3,1],[2,2,2]]
Output: [false,true,true]
```

After placing an obstacle at `2`:

- in `[0,3]`, the free segments are `[0,2]` and `[2,3]`
- largest available length is `2`

So:

- block size `3` → cannot fit
- block size `1` → can fit
- in `[0,2]`, block size `2` → can fit

---

## Example 2

```text
Input: queries = [[1,7],[2,7,6],[1,2],[2,7,5],[2,7,6]]
Output: [true,true,false]
```

---

## Constraints

- `1 <= queries.length <= 150000`
- `2 <= queries[i].length <= 3`
- `1 <= queries[i][0] <= 2`
- `1 <= x, sz <= min(5 * 10^4, 3 * queries.length)`
- For type-1 queries, no obstacle already exists at `x`
- There is at least one type-2 query

---

# 1. Core Insight

For a query `[2, x, sz]`, we only care about obstacles inside:

```text
[0, x]
```

If those obstacle positions are:

```text
p1 < p2 < ... < pk
```

then the free places where a block may lie are exactly the gaps:

- from `0` to `p1`
- from `p1` to `p2`
- ...
- from `pk` to `x`

Since touching an obstacle is allowed, a gap of length `d` can hold a block of size `d`.

So the query is equivalent to asking:

> Is the maximum gap among consecutive obstacle boundaries in `[0, x]` at least `sz`?

That is the whole problem.

The hard part is that obstacles are added online.

---

# 2. Approach 1 — Direct Simulation with Ordered Set

## Idea

Maintain all built obstacles in a sorted set.

For query `[2, x, sz]`:

1. iterate through all obstacles `<= x`
2. compute all consecutive gaps
3. check whether the maximum gap is at least `sz`

This is easy to reason about, but too slow if repeated many times.

## Java Code

```java
import java.util.*;

class SolutionBruteForce {
    public List<Boolean> getResults(int[][] queries) {
        TreeSet<Integer> obstacles = new TreeSet<>();
        List<Boolean> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                obstacles.add(q[1]);
            } else {
                int x = q[1];
                int sz = q[2];

                int prev = 0;
                int best = x; // if there are no obstacles <= x
                best = 0;

                for (int p : obstacles.headSet(x, true)) {
                    best = Math.max(best, p - prev);
                    prev = p;
                }

                best = Math.max(best, x - prev);
                ans.add(best >= sz);
            }
        }

        return ans;
    }
}
```

## Complexity

Let `m` be the number of obstacles already added.

- Type 1: `O(log m)`
- Type 2: potentially `O(m)`

Worst case:

```text
O(q^2)
```

Too slow for `150000` queries.

---

# 3. Why This Problem Is Tricky

A type-2 query asks for the largest gap inside a prefix `[0, x]`.

If the obstacles were static, this would be easy:

- sort obstacles
- compute adjacent gaps
- answer prefix maximum queries

But obstacles are inserted over time.

So we need a structure that supports:

- insert obstacle
- query maximum gap up to position `x`

A naive online structure is not simple because one insertion changes local gaps.

That is why the standard solution processes the queries **offline in reverse**.

This is the key leap.

---

# 4. Reverse Processing Insight

Suppose instead of inserting obstacles forward, we process queries backward.

Then:

- a forward type-1 “insert obstacle at `x`”
- becomes a backward “remove obstacle at `x`”

Removing an obstacle is often easier to handle than inserting one if we maintain obstacle neighbors.

Why?

Because when removing an obstacle `x`, only one thing changes:

- two adjacent gaps merge into one bigger gap

That is very local.

This suggests:

1. first assume **all obstacles that will ever appear already exist**
2. answer queries in reverse
3. when encountering a type-1 query in reverse, remove that obstacle and merge its neighboring gaps

Now the geometry becomes much cleaner.

---

# 5. What a Type-2 Query Needs in Reverse

At reverse time, we have some current obstacle set.

For `[2, x, sz]`, the block can fit iff either:

1. the final gap from the last obstacle before or at `x` up to `x` has length at least `sz`, or
2. some earlier full gap between consecutive obstacles inside `[0, x]` has length at least `sz`

So we need two things:

- predecessor obstacle of `x`
- maximum gap whose **right endpoint** is at most `x`

This is the exact structure used by the optimal solution.

---

# 6. Approach 2 — Reverse Processing + TreeSet + Fenwick Tree for Prefix Maximum (Optimal)

## Data structures

We keep:

- a `TreeSet<Integer>` of current obstacle positions
- a Fenwick Tree / BIT that stores, for each obstacle position `r`, the length of the gap ending at `r`

If there is a gap from `l` to `r`, then its length is:

```text
r - l
```

and we store that value at index `r`.

Then for a query `[2, x, sz]`:

- let `prev = predecessor obstacle <= x`
- the trailing gap is:
  ```text
  x - prev
  ```
- the best full gap ending at or before `x` is:
  ```text
  maxGapPrefix(x)
  ```

Answer is true iff:

```text
max(maxGapPrefix(x), x - prev) >= sz
```

---

## 6.1 Why the trailing gap is handled separately

The Fenwick Tree stores gaps that end at actual obstacle positions.

But the interval from the last obstacle before `x` to `x` may end at a point that is **not** an obstacle.

So that last partial gap is not represented in the BIT and must be checked separately.

This is subtle and easy to miss.

---

## 6.2 Fenwick Tree for prefix maximum

A normal BIT is often used for prefix sums, but here we use it for prefix maxima.

Operations:

- `update(pos, val)` → store maximum at relevant BIT nodes
- `query(pos)` → maximum over indices `<= pos`

In reverse processing, gaps only get larger when obstacles are removed, so monotone max-updates are enough.

---

## 6.3 Reverse algorithm

### Step 1 — Collect all obstacle positions

Scan queries and collect every `x` from type-1 queries.

Also insert `0` as a boundary obstacle.

### Step 2 — Build initial obstacle set

At reverse start, all obstacles that ever appear are present.

### Step 3 — Initialize gaps

For each consecutive pair of obstacles `(l, r)`, store gap `r - l` at position `r` in the BIT.

### Step 4 — Process queries backward

- If query is type 2:
  - find predecessor `prev <= x`
  - query BIT prefix maximum up to `x`
  - compute `x - prev`
  - answer using max of the two

- If query is type 1 at `x`:
  - in reverse, remove obstacle `x`
  - let neighbors be `l` and `r`
  - merged gap becomes `r - l`
  - update BIT at `r` with the merged gap
  - remove `x` from `TreeSet`

This works because removing `x` merges the two gaps `(l, x)` and `(x, r)` into `(l, r)`.

---

# 7. Java Code — Reverse + TreeSet + Fenwick Max

```java
import java.util.*;

class Solution {
    static class FenwickMax {
        int n;
        int[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new int[n + 2];
        }

        void update(int index, int value) {
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        int query(int index) {
            int ans = 0;
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public List<Boolean> getResults(int[][] queries) {
        int maxX = 0;
        TreeSet<Integer> obstacleSet = new TreeSet<>();
        obstacleSet.add(0);

        for (int[] q : queries) {
            maxX = Math.max(maxX, q[1]);
            if (q[0] == 1) {
                obstacleSet.add(q[1]);
            }
        }

        FenwickMax bit = new FenwickMax(maxX + 2);

        Integer prev = null;
        for (int pos : obstacleSet) {
            if (prev != null) {
                bit.update(pos, pos - prev);
            }
            prev = pos;
        }

        List<Boolean> reversed = new ArrayList<>();

        for (int i = queries.length - 1; i >= 0; i--) {
            int[] q = queries[i];

            if (q[0] == 2) {
                int x = q[1];
                int sz = q[2];

                Integer p = obstacleSet.floor(x);
                int best = bit.query(x);
                best = Math.max(best, x - p);

                reversed.add(best >= sz);
            } else {
                int x = q[1];

                Integer left = obstacleSet.lower(x);
                Integer right = obstacleSet.higher(x);

                if (right != null) {
                    bit.update(right, right - left);
                }

                obstacleSet.remove(x);
            }
        }

        Collections.reverse(reversed);
        return reversed;
    }
}
```

---

## 7.1 Complexity

Each query does:

- `TreeSet` predecessor/successor/remove: `O(log q)`
- BIT update/query: `O(log X)`

Since `X <= 50000` here, this is very fast.

Overall:

```text
Time:  O(q log q)
Space: O(q + X)
```

This is fully acceptable.

---

# 8. Approach 3 — Reverse Processing + Segment Tree

A segment tree can replace the Fenwick Tree.

## Idea

Store, for each obstacle position `r`, the gap length ending at `r`.

Support:

- point update with max
- range maximum query on `[0, x]`

This is more verbose than BIT, but conceptually similar.

## Java Code

```java
import java.util.*;

class SolutionSegmentTree {
    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void update(int node, int left, int right, int index, int value) {
            if (left == right) {
                tree[node] = Math.max(tree[node], value);
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index, value);
            } else {
                update(node * 2 + 1, mid + 1, right, index, value);
            }

            tree[node] = Math.max(tree[node * 2], tree[node * 2 + 1]);
        }

        int query(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return 0;
            if (ql <= left && right <= qr) return tree[node];

            int mid = left + (right - left) / 2;
            return Math.max(
                query(node * 2, left, mid, ql, qr),
                query(node * 2 + 1, mid + 1, right, ql, qr)
            );
        }

        void update(int index, int value) {
            update(1, 0, n - 1, index, value);
        }

        int query(int left, int right) {
            if (left > right) return 0;
            return query(1, 0, n - 1, left, right);
        }
    }

    public List<Boolean> getResults(int[][] queries) {
        int maxX = 0;
        TreeSet<Integer> set = new TreeSet<>();
        set.add(0);

        for (int[] q : queries) {
            maxX = Math.max(maxX, q[1]);
            if (q[0] == 1) set.add(q[1]);
        }

        SegmentTree st = new SegmentTree(maxX + 2);

        Integer prev = null;
        for (int pos : set) {
            if (prev != null) {
                st.update(pos, pos - prev);
            }
            prev = pos;
        }

        List<Boolean> reversed = new ArrayList<>();

        for (int i = queries.length - 1; i >= 0; i--) {
            int[] q = queries[i];

            if (q[0] == 2) {
                int x = q[1];
                int sz = q[2];

                Integer p = set.floor(x);
                int best = st.query(0, x);
                best = Math.max(best, x - p);

                reversed.add(best >= sz);
            } else {
                int x = q[1];
                Integer left = set.lower(x);
                Integer right = set.higher(x);

                if (right != null) {
                    st.update(right, right - left);
                }

                set.remove(x);
            }
        }

        Collections.reverse(reversed);
        return reversed;
    }
}
```

---

## Complexity

Also:

```text
O(q log q)
```

with slightly more code than the BIT version.

---

# 9. Dry Run on Example 2

```text
queries = [[1,7],[2,7,6],[1,2],[2,7,5],[2,7,6]]
```

Forward, all obstacles that ever appear are:

```text
{2, 7}
```

In reverse start, current obstacle set is:

```text
{0, 2, 7}
```

Gaps:

- `(0,2)` length `2`, stored at right endpoint `2`
- `(2,7)` length `5`, stored at right endpoint `7`

Now process reverse.

### Reverse query: [2,7,6]

- predecessor of `7` is `7`
- best stored gap up to `7` is `5`
- trailing gap `7 - 7 = 0`
- best = `5`
- `5 >= 6`? no → false

### Reverse query: [2,7,5]

- same state
- best = `5`
- `5 >= 5`? yes → true

### Reverse query: [1,2]

Reverse means remove obstacle `2`.

Neighbors of `2` are `0` and `7`, so merged gap is:

```text
7 - 0 = 7
```

Update gap ending at `7` to `7`.

Now set is:

```text
{0, 7}
```

### Reverse query: [2,7,6]

- predecessor of `7` is `7`
- best stored gap up to `7` is `7`
- trailing gap = `0`
- best = `7`
- `7 >= 6`? yes → true

Reverse collected answers:

```text
[false, true, true]
```

Reverse again:

```text
[true, true, false]
```

Correct.

---

# 10. Why the Reverse Merge Works

Suppose current obstacles are:

```text
... < l < x < r < ...
```

Then the two gaps involving `x` are:

- `x - l`
- `r - x`

If we remove `x`, those two gaps merge into:

```text
r - l
```

No other gaps change.

That locality is what makes reverse processing elegant.

In contrast, forward insertion splits one gap into two smaller ones, which is harder to maintain with simple prefix-max structures.

---

# 11. Correctness Proof

## Lemma 1

For a fixed obstacle set, a block of size `sz` can be placed inside `[0, x]` iff the maximum free gap inside `[0, x]` is at least `sz`.

### Proof

A valid placement is exactly a closed interval of length `sz` lying entirely in a free segment. Because touching obstacles is allowed, a free segment of length `d` can contain a block of size `sz` iff `d >= sz`. Therefore existence of a placement is equivalent to the maximum gap being at least `sz`. ∎

---

## Lemma 2

For a query `[2, x, sz]`, the maximum free gap inside `[0, x]` equals:

```text
max(prefixGapMax(x), x - prevObstacle(x))
```

where `prevObstacle(x)` is the largest obstacle `<= x`, and `prefixGapMax(x)` is the maximum full gap ending at an obstacle position `<= x`.

### Proof

Every free segment inside `[0, x]` is either:

- a full gap between two consecutive obstacles whose right endpoint is `<= x`, or
- the final partial gap from the last obstacle before `x` up to `x`

There are no other possibilities. Taking the maximum over these two categories gives the maximum free gap. ∎

---

## Lemma 3

During reverse processing, when obstacle `x` is removed, updating the gap ending at its right neighbor `r` with value `r - l` correctly reflects the merged gap.

### Proof

Before removal, consecutive obstacles are `l < x < r`, so gaps are `(l, x)` and `(x, r)`. After removing `x`, the consecutive pair becomes `(l, r)`, whose gap is exactly `r - l`. No other gaps change. ∎

---

## Theorem

The reverse-processing algorithm returns the correct answer for every type-2 query.

### Proof

By Lemma 3, the maintained obstacle set and gap structure are correct throughout reverse processing. By Lemma 2, each query computes the true maximum free gap inside `[0, x]`. By Lemma 1, comparing this with `sz` yields the correct boolean answer. Therefore all answers are correct. ∎

---

# 12. Common Mistakes

## Mistake 1: Trying to solve it online with naive insertion

Forward insertion splits a gap into two, and maintaining prefix maximum gaps dynamically is much trickier than reverse merging.

## Mistake 2: Forgetting the trailing segment from last obstacle to `x`

This segment may end at a non-obstacle, so it is not represented by the BIT and must be checked separately.

## Mistake 3: Forgetting boundary `0`

The number line starts at `0`, so `0` behaves like a fixed left boundary.

## Mistake 4: Using sums instead of maxima in the BIT

This problem is about largest gap, not number of obstacles.

## Mistake 5: Assuming touching obstacles is forbidden

Touching is allowed, so a gap of length exactly `sz` is enough.

---

# 13. Comparison of Approaches

| Approach                         |                Time |  Space | Notes                   |
| -------------------------------- | ------------------: | -----: | ----------------------- |
| Direct scan with ordered set     | `O(q^2)` worst case | `O(q)` | Too slow                |
| Reverse + Fenwick max + TreeSet  |        `O(q log q)` | `O(q)` | Best practical solution |
| Reverse + Segment tree + TreeSet |        `O(q log q)` | `O(q)` | Also valid              |

---

# 14. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class FenwickMax {
        int n;
        int[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new int[n + 2];
        }

        void update(int index, int value) {
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        int query(int index) {
            int ans = 0;
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public List<Boolean> getResults(int[][] queries) {
        int maxX = 0;
        TreeSet<Integer> obstacles = new TreeSet<>();
        obstacles.add(0);

        for (int[] q : queries) {
            maxX = Math.max(maxX, q[1]);
            if (q[0] == 1) {
                obstacles.add(q[1]);
            }
        }

        FenwickMax bit = new FenwickMax(maxX + 2);

        Integer prev = null;
        for (int pos : obstacles) {
            if (prev != null) {
                bit.update(pos + 1, pos - prev);
            }
            prev = pos;
        }

        List<Boolean> reversed = new ArrayList<>();

        for (int i = queries.length - 1; i >= 0; i--) {
            int[] q = queries[i];

            if (q[0] == 2) {
                int x = q[1];
                int sz = q[2];

                Integer p = obstacles.floor(x);
                int best = bit.query(x + 1);
                best = Math.max(best, x - p);

                reversed.add(best >= sz);
            } else {
                int x = q[1];
                Integer left = obstacles.lower(x);
                Integer right = obstacles.higher(x);

                if (right != null) {
                    bit.update(right + 1, right - left);
                }

                obstacles.remove(x);
            }
        }

        Collections.reverse(reversed);
        return reversed;
    }
}
```

---

# 15. Interview Summary

The direct formulation asks for the largest obstacle-free gap inside a changing prefix `[0, x]`.

The clean way is to process queries in reverse:

- all obstacles are initially present
- reverse type-1 means removing an obstacle
- removing an obstacle merges two adjacent gaps into one larger gap

Maintain:

- a sorted set of obstacles for predecessor/successor lookup
- a Fenwick tree storing maximum full-gap lengths by right endpoint

For a query `[2, x, sz]`, check the maximum of:

- the best full gap ending at or before `x`
- the trailing gap from the last obstacle before `x` to `x`

This yields an `O(q log q)` solution.
