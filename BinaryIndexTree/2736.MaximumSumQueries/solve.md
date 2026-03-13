# Maximum Sum Queries — Exhaustive Java Notes

## Problem Statement

You are given two arrays `nums1` and `nums2`, both of length `n`.

Think of them as `n` points:

```text
(nums1[i], nums2[i])
```

You are also given `m` queries, where each query is:

```text
[x, y]
```

For each query, we need the maximum value of:

```text
nums1[j] + nums2[j]
```

among all indices `j` such that:

```text
nums1[j] >= x
nums2[j] >= y
```

If no such index exists, return `-1`.

---

## Example 1

```text
nums1 = [4,3,1,2]
nums2 = [2,4,9,5]
queries = [[4,1],[1,3],[2,5]]
```

Output:

```text
[6,10,7]
```

Explanation:

- Query `[4,1]`:
  - valid points must satisfy `nums1 >= 4` and `nums2 >= 1`
  - only index `0` works: `(4,2)` → sum = `6`

- Query `[1,3]`:
  - valid points must satisfy `nums1 >= 1` and `nums2 >= 3`
  - best is index `2`: `(1,9)` → sum = `10`

- Query `[2,5]`:
  - valid points must satisfy `nums1 >= 2` and `nums2 >= 5`
  - best is index `3`: `(2,5)` → sum = `7`

---

## Example 2

```text
nums1 = [3,2,5]
nums2 = [2,3,4]
queries = [[4,4],[3,2],[1,1]]
```

Output:

```text
[9,9,9]
```

Explanation:

Index `2` gives point `(5,4)` with sum `9`, and it satisfies all three queries.

---

## Example 3

```text
nums1 = [2,1]
nums2 = [2,3]
queries = [[3,3]]
```

Output:

```text
[-1]
```

No point satisfies both constraints.

---

# 1. Core Geometric View

Each index `i` is a point:

```text
(a, b) = (nums1[i], nums2[i])
```

Each query asks:

> Among all points in the top-right rectangle `a >= x` and `b >= y`, what is the maximum value of `a + b`?

So this is a **2D offline query** problem.

The challenge is large constraints:

- `n <= 10^5`
- `queries.length <= 10^5`

So we need around `O((n + q) log n)` or `O((n + q) log^2 n)`, not brute force.

---

# 2. Approach 1 — Brute Force

## Idea

For every query:

- scan every index
- check whether it satisfies:
  - `nums1[j] >= x`
  - `nums2[j] >= y`
- among valid indices, take max of `nums1[j] + nums2[j]`

## Java Code

```java
class SolutionBruteForce {
    public int[] maximumSumQueries(int[] nums1, int[] nums2, int[][] queries) {
        int n = nums1.length;
        int q = queries.length;
        int[] ans = new int[q];

        for (int i = 0; i < q; i++) {
            int x = queries[i][0];
            int y = queries[i][1];
            int best = -1;

            for (int j = 0; j < n; j++) {
                if (nums1[j] >= x && nums2[j] >= y) {
                    best = Math.max(best, nums1[j] + nums2[j]);
                }
            }

            ans[i] = best;
        }

        return ans;
    }
}
```

## Complexity

- Time: `O(n * q)`
- Space: `O(1)` extra, excluding output

## Why it fails

With `n = 10^5` and `q = 10^5`, this becomes `10^10` checks, which is far too slow.

---

# 3. Approach 2 — Offline Processing + Monotonic Skyline + Binary Search

This is the most elegant standard solution for this problem.

## High-level idea

We process queries **offline** in descending order of `x`.

At any moment, we maintain all points whose:

```text
nums1[i] >= currentQueryX
```

Among those active points, we need to answer:

```text
max(nums1[i] + nums2[i]) for nums2[i] >= y
```

So after filtering by `nums1`, the remaining problem is:

> Over active points, query maximum sum for `nums2 >= y`.

Now we maintain a special structure over active points:

- sorted by `nums2`
- only keep useful points
- for increasing `nums2`, the corresponding `sum = nums1 + nums2` is strictly increasing

This becomes a **skyline / Pareto frontier** of useful candidates.

Then for each query:

- binary search the first candidate with `nums2 >= y`
- answer is its associated maximum sum
- if none, answer `-1`

---

## 3.1 Why offline sorting by `x` helps

Suppose we sort points by `nums1` descending, and queries by `x` descending.

When processing a query `[x, y]`, we add every point with:

```text
nums1 >= x
```

into our active structure.

This means each point is inserted once, and each query is answered once.

So the only remaining challenge is the data structure for `nums2 >= y`.

---

## 3.2 The crucial pruning rule

When inserting a point `(b, s)` where:

- `b = nums2[i]`
- `s = nums1[i] + nums2[i]`

some previously stored points become useless.

If we already have a point with:

- `nums2 >= b`
- `sum >= s`

then the new point is useless, because any query that accepts this new point also accepts the existing point, and the existing point gives at least as large a sum.

Similarly, when inserting the new point, some smaller-`nums2` points with smaller or equal sum may become useless.

So the maintained structure should satisfy:

- `nums2` increasing
- `sum` strictly increasing

That makes binary search possible and correct.

---

## 3.3 Data structure choice in Java

We use a `TreeMap<Integer, Integer>`:

- key = `nums2`
- value = best achievable `nums1 + nums2`

But we must maintain it in a pruned form.

Insertion logic:

1. Find entry with smallest key `>= current nums2`
   - if its sum is already `>= current sum`, new point is dominated, ignore it
2. Otherwise insert/replace current point
3. Remove entries to the left whose sums are `<= current sum`, because they are now dominated

Then for a query threshold `y`:

- `ceilingEntry(y)` gives the first active candidate with `nums2 >= y`
- because sums increase with nums2 in the pruned structure, that first one is optimal

That last part is subtle and very important.

---

## 3.4 Why the first `nums2 >= y` entry is the best answer

Because after pruning, the structure has:

- increasing `nums2`
- increasing sums

So once we find the first key `>= y`, every later key has even larger `nums2`, but also larger sum.
Wait — that sounds like maybe later entries are better. So why can we just take the first one?

Because we are not storing arbitrary entries. We are storing a skyline built in a specific way: for each `nums2`, the value stored is the best sum among candidates with that `nums2`, and after pruning the map can be interpreted as breakpoints where for all thresholds up to the next breakpoint, this entry gives the right answer. However, this reasoning is easy to get wrong if implemented carelessly.

So although the `TreeMap skyline` solution is popular, the safest way to understand it is:

- the map stores nondominated breakpoints
- each breakpoint means “for threshold at least this `nums2`, this sum becomes the best reachable from here”
- therefore `ceilingEntry(y)` gives the answer

This works, but it is conceptually trickier than the segment tree solution.

Because Rahul tends to prefer solid internals-style reasoning, it is worth being skeptical here: this approach is elegant, but easier to misimplement than the segment tree approach.

Still, it is optimal and standard.

---

## 3.5 Java Code — TreeMap Skyline

```java
import java.util.*;

class Solution {
    public int[] maximumSumQueries(int[] nums1, int[] nums2, int[][] queries) {
        int n = nums1.length;
        int q = queries.length;

        int[][] points = new int[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = nums1[i];
            points[i][1] = nums2[i];
        }

        Arrays.sort(points, (a, b) -> Integer.compare(b[0], a[0]));

        int[][] qs = new int[q][3];
        for (int i = 0; i < q; i++) {
            qs[i][0] = queries[i][0]; // x
            qs[i][1] = queries[i][1]; // y
            qs[i][2] = i;             // original index
        }

        Arrays.sort(qs, (a, b) -> Integer.compare(b[0], a[0]));

        int[] ans = new int[q];
        TreeMap<Integer, Integer> map = new TreeMap<>();

        int p = 0;

        for (int[] query : qs) {
            int x = query[0];
            int y = query[1];
            int idx = query[2];

            while (p < n && points[p][0] >= x) {
                int b = points[p][1];
                int sum = points[p][0] + points[p][1];

                Map.Entry<Integer, Integer> next = map.ceilingEntry(b);

                if (next != null && next.getValue() >= sum) {
                    p++;
                    continue;
                }

                while (true) {
                    Map.Entry<Integer, Integer> prev = map.floorEntry(b);
                    if (prev != null && prev.getValue() <= sum) {
                        map.remove(prev.getKey());
                    } else {
                        break;
                    }
                }

                map.put(b, sum);
                p++;
            }

            Map.Entry<Integer, Integer> res = map.ceilingEntry(y);
            ans[idx] = (res == null) ? -1 : res.getValue();
        }

        return ans;
    }
}
```

---

## 3.6 Complexity

- Sorting points: `O(n log n)`
- Sorting queries: `O(q log q)`
- Each point inserted once, removed at most once from the map
- Each map operation: `O(log n)`

Overall:

- Time: `O((n + q) log n)`
- Space: `O(n + q)`

---

# 4. Approach 3 — Offline Processing + Coordinate Compression + Segment Tree

This is the most straightforward-to-prove optimal solution.

## Idea

Again process queries offline by descending `x`.

When a point becomes active, we want to update information at coordinate `nums2[i]`:

```text
best sum at nums2 = nums2[i] is max(existing, nums1[i] + nums2[i])
```

For a query `[x, y]`, among active points we need:

```text
max sum over all nums2 >= y
```

That is a range maximum query over a suffix.

Since `nums2[i]` and `y` can be up to `10^9`, we coordinate-compress all relevant `nums2` and query `y` values.

Then use a segment tree to support:

- point update: set position `nums2[i]` to max(current, sum)
- range query: maximum over suffix `[posOfY, end]`

This approach is easier to reason about than the skyline map, and very robust.

---

## 4.1 Coordinate compression

Collect all values:

- every `nums2[i]`
- every query `y`

Sort unique values.

Then map each original value to compressed index.

For a query threshold `y`, we need the first compressed position with original value `>= y`.

Since all query `y` values are included, that position always exists in the compressed list.

---

## 4.2 Offline processing

Sort:

- points by `nums1` descending
- queries by `x` descending

Maintain pointer `p` into sorted points.

For query `[x, y]`:

- activate all points with `nums1 >= x`
- each activation updates the segment tree at compressed `nums2`
- answer = segment tree max on suffix corresponding to `y`

---

## 4.3 Java Code — Segment Tree

```java
import java.util.*;

class SolutionSegmentTree {
    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
            Arrays.fill(this.tree, -1);
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
            if (ql > right || qr < left) return -1;
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
            if (left > right) return -1;
            return query(1, 0, n - 1, left, right);
        }
    }

    public int[] maximumSumQueries(int[] nums1, int[] nums2, int[][] queries) {
        int n = nums1.length;
        int q = queries.length;

        int[][] points = new int[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = nums1[i];
            points[i][1] = nums2[i];
        }

        Arrays.sort(points, (a, b) -> Integer.compare(b[0], a[0]));

        int[][] qs = new int[q][3];
        for (int i = 0; i < q; i++) {
            qs[i][0] = queries[i][0];
            qs[i][1] = queries[i][1];
            qs[i][2] = i;
        }

        Arrays.sort(qs, (a, b) -> Integer.compare(b[0], a[0]));

        List<Integer> values = new ArrayList<>();
        for (int v : nums2) values.add(v);
        for (int[] query : queries) values.add(query[1]);

        Collections.sort(values);

        List<Integer> unique = new ArrayList<>();
        for (int v : values) {
            if (unique.isEmpty() || unique.get(unique.size() - 1) != v) {
                unique.add(v);
            }
        }

        Map<Integer, Integer> compress = new HashMap<>();
        for (int i = 0; i < unique.size(); i++) {
            compress.put(unique.get(i), i);
        }

        SegmentTree st = new SegmentTree(unique.size());
        int[] ans = new int[q];
        int p = 0;

        for (int[] query : qs) {
            int x = query[0];
            int y = query[1];
            int idx = query[2];

            while (p < n && points[p][0] >= x) {
                int pos = compress.get(points[p][1]);
                int sum = points[p][0] + points[p][1];
                st.update(pos, sum);
                p++;
            }

            int left = compress.get(y);
            ans[idx] = st.query(left, unique.size() - 1);
        }

        return ans;
    }
}
```

---

## 4.4 Complexity

- Sorting points: `O(n log n)`
- Sorting queries: `O(q log q)`
- Compression sorting: `O((n + q) log (n + q))`
- Each point update: `O(log (n + q))`
- Each query: `O(log (n + q))`

Overall:

- Time: `O((n + q) log (n + q))`
- Space: `O(n + q)`

---

# 5. Approach 4 — Offline Processing + Coordinate Compression + Fenwick Tree for Prefix Max

A Fenwick Tree typically handles prefix sums, but it can also be adapted for prefix maximums.

Since our query needs:

```text
max over nums2 >= y
```

we can reverse the compressed coordinate order.

Then:

- larger original `nums2` becomes smaller reversed index
- suffix max becomes prefix max

Now the Fenwick Tree can support:

- update position with `max`
- query prefix max

This is slightly more specialized than the segment tree approach, but shorter and very fast.

---

## 5.1 Trick

If compressed positions are:

```text
0, 1, 2, ..., m-1
```

define reversed index as:

```text
rev = m - 1 - pos
```

Then original condition:

```text
nums2 >= y
```

becomes:

```text
rev <= rev(y)
```

which is a prefix.

Fenwick can now do prefix maximum.

---

## 5.2 Java Code — Fenwick Max

```java
import java.util.*;

class SolutionFenwick {
    static class FenwickMax {
        int n;
        int[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new int[n + 1];
            Arrays.fill(this.bit, -1);
        }

        void update(int index, int value) {
            index++; // 1-based
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        int query(int index) {
            int res = -1;
            index++; // 1-based
            while (index > 0) {
                res = Math.max(res, bit[index]);
                index -= index & -index;
            }
            return res;
        }
    }

    public int[] maximumSumQueries(int[] nums1, int[] nums2, int[][] queries) {
        int n = nums1.length;
        int q = queries.length;

        int[][] points = new int[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = nums1[i];
            points[i][1] = nums2[i];
        }

        Arrays.sort(points, (a, b) -> Integer.compare(b[0], a[0]));

        int[][] qs = new int[q][3];
        for (int i = 0; i < q; i++) {
            qs[i][0] = queries[i][0];
            qs[i][1] = queries[i][1];
            qs[i][2] = i;
        }

        Arrays.sort(qs, (a, b) -> Integer.compare(b[0], a[0]));

        List<Integer> values = new ArrayList<>();
        for (int v : nums2) values.add(v);
        for (int[] query : queries) values.add(query[1]);

        Collections.sort(values);

        List<Integer> unique = new ArrayList<>();
        for (int v : values) {
            if (unique.isEmpty() || unique.get(unique.size() - 1) != v) {
                unique.add(v);
            }
        }

        Map<Integer, Integer> compress = new HashMap<>();
        for (int i = 0; i < unique.size(); i++) {
            compress.put(unique.get(i), i);
        }

        int m = unique.size();
        FenwickMax fw = new FenwickMax(m);
        int[] ans = new int[q];
        int p = 0;

        for (int[] query : qs) {
            int x = query[0];
            int y = query[1];
            int idx = query[2];

            while (p < n && points[p][0] >= x) {
                int pos = compress.get(points[p][1]);
                int rev = m - 1 - pos;
                int sum = points[p][0] + points[p][1];
                fw.update(rev, sum);
                p++;
            }

            int posY = compress.get(y);
            int revY = m - 1 - posY;
            ans[idx] = fw.query(revY);
        }

        return ans;
    }
}
```

---

## 5.3 Complexity

Same asymptotic complexity as segment tree version:

- Time: `O((n + q) log (n + q))`
- Space: `O(n + q)`

---

# 6. Which Approach Is Best?

## Brute force

Good only for understanding.

## TreeMap skyline

Very elegant and optimal.
But the domination logic is subtle, and it is easier to make mistakes.

## Segment tree

The safest optimal approach.

- easy to reason about
- easy to prove
- robust for interviews

## Fenwick max

Also excellent.

- shorter than segment tree
- a little trickier because of reversed coordinates

---

# 7. Detailed Intuition for the Offline Pattern

This pattern appears again and again in advanced problems.

You have constraints of the form:

```text
a >= x
b >= y
```

A powerful strategy is:

1. Sort points by one dimension, say `a`, descending
2. Sort queries by `x`, descending
3. Sweep through points, activating those that now satisfy the first constraint
4. Use a data structure on the second dimension `b`

That reduces a 2D filter into:

- one dimension handled by sorting/sweeping
- one dimension handled by a query structure

This is the real structural idea behind the problem.

---

# 8. Dry Run — Segment Tree Approach

Let:

```text
nums1 = [4,3,1,2]
nums2 = [2,4,9,5]
queries = [[4,1],[1,3],[2,5]]
```

Points:

```text
(4,2) sum=6
(3,4) sum=7
(1,9) sum=10
(2,5) sum=7
```

Sort points by `nums1` descending:

```text
(4,2,6), (3,4,7), (2,5,7), (1,9,10)
```

Queries with indices:

```text
(4,1,0), (1,3,1), (2,5,2)
```

Sort by `x` descending:

```text
(4,1,0), (2,5,2), (1,3,1)
```

Coordinate compression over all `nums2` and query `y`:

```text
[1,2,3,4,5,9]
```

Now process:

## Query `(4,1)`

Activate points with `nums1 >= 4`:

- `(4,2)` sum=6

Update at `nums2=2`.

Now query suffix `nums2 >= 1`:

- answer = 6

## Query `(2,5)`

Activate additional points with `nums1 >= 2`:

- `(3,4)` sum=7
- `(2,5)` sum=7

Now query suffix `nums2 >= 5`:

- candidates at `5` and `9` among active only
- only active valid one is `(2,5)` → 7

Answer = 7

## Query `(1,3)`

Activate additional points with `nums1 >= 1`:

- `(1,9)` sum=10

Now query suffix `nums2 >= 3`:

- best is 10

Answer = 10

Restore original order:

```text
[6,10,7]
```

Correct.

---

# 9. Correctness Argument — Segment Tree Approach

We will prove the segment tree method is correct.

## Lemma 1

When processing query `[x, y]`, exactly the points with `nums1 >= x` have been activated.

### Reason

Points are sorted descending by `nums1`, and queries are processed descending by `x`. Before answering a query, we advance the pointer and activate all points with `nums1 >= x`. No point with smaller `nums1` is activated yet.

---

## Lemma 2

For every compressed `nums2` position, the segment tree stores the maximum sum among active points with that exact `nums2`.

### Reason

Whenever a point becomes active, we update its compressed `nums2` position with `max(current, nums1 + nums2)`. Therefore the leaf stores exactly the best active sum for that coordinate.

---

## Lemma 3

A suffix query on the segment tree returns the maximum sum among all active points with `nums2 >= y`.

### Reason

After compression, all coordinates `>= y` form a suffix of compressed indices. Since internal segment tree nodes store range maxima, querying that suffix returns the maximum sum among all active points in that suffix.

---

## Theorem

For each query `[x, y]`, the algorithm returns the maximum value of `nums1[j] + nums2[j]` over all indices `j` satisfying `nums1[j] >= x` and `nums2[j] >= y`, or `-1` if none exists.

### Reason

By Lemma 1, the active points are exactly those satisfying `nums1 >= x`. By Lemma 3, the suffix query finds the best among active points with `nums2 >= y`. Therefore the returned value is exactly the best valid point.

---

# 10. Common Mistakes

## Mistake 1: Solving each query independently

That leads to `O(nq)`.

## Mistake 2: Forgetting offline ordering

If you do not sort points and queries by `x`, you lose the sweep-line advantage.

## Mistake 3: Not compressing coordinates

`nums2[i]` can be up to `10^9`, so direct indexing is impossible.

## Mistake 4: Using the wrong suffix/prefix direction

For segment tree:

- query suffix `[posY, end]`

For Fenwick-max:

- reverse coordinates so suffix becomes prefix

## Mistake 5: Misreading the query indices

The statement text sometimes appears with indexing confusion. The real query content is simply:

```text
queries[i] = [xi, yi]
```

so:

- `x = queries[i][0]`
- `y = queries[i][1]`

---

# 11. Final Recommended Java Solution

For clarity and reliability, the segment tree solution is the best one to carry into an interview or revision sheet.

```java
import java.util.*;

class Solution {
    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
            Arrays.fill(this.tree, -1);
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
            if (ql > right || qr < left) return -1;
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
            if (left > right) return -1;
            return query(1, 0, n - 1, left, right);
        }
    }

    public int[] maximumSumQueries(int[] nums1, int[] nums2, int[][] queries) {
        int n = nums1.length;
        int q = queries.length;

        int[][] points = new int[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = nums1[i];
            points[i][1] = nums2[i];
        }

        Arrays.sort(points, (a, b) -> Integer.compare(b[0], a[0]));

        int[][] qs = new int[q][3];
        for (int i = 0; i < q; i++) {
            qs[i][0] = queries[i][0];
            qs[i][1] = queries[i][1];
            qs[i][2] = i;
        }

        Arrays.sort(qs, (a, b) -> Integer.compare(b[0], a[0]));

        List<Integer> values = new ArrayList<>();
        for (int v : nums2) values.add(v);
        for (int[] query : queries) values.add(query[1]);

        Collections.sort(values);

        List<Integer> unique = new ArrayList<>();
        for (int v : values) {
            if (unique.isEmpty() || unique.get(unique.size() - 1) != v) {
                unique.add(v);
            }
        }

        Map<Integer, Integer> compress = new HashMap<>();
        for (int i = 0; i < unique.size(); i++) {
            compress.put(unique.get(i), i);
        }

        SegmentTree st = new SegmentTree(unique.size());

        int[] ans = new int[q];
        int p = 0;

        for (int[] query : qs) {
            int x = query[0];
            int y = query[1];
            int idx = query[2];

            while (p < n && points[p][0] >= x) {
                int pos = compress.get(points[p][1]);
                int sum = points[p][0] + points[p][1];
                st.update(pos, sum);
                p++;
            }

            int left = compress.get(y);
            ans[idx] = st.query(left, unique.size() - 1);
        }

        return ans;
    }
}
```

---

# 12. Quick Comparison Table

| Approach                  | Idea                                                           |                 Time |    Space | Notes              |
| ------------------------- | -------------------------------------------------------------- | -------------------: | -------: | ------------------ |
| Brute force               | Scan all points for every query                                |              `O(nq)` |   `O(1)` | Too slow           |
| Offline + TreeMap skyline | Sweep by `nums1`, maintain nondominated frontier in `nums2`    |     `O((n+q) log n)` | `O(n+q)` | Elegant but subtle |
| Offline + Segment Tree    | Sweep by `nums1`, suffix max on compressed `nums2`             | `O((n+q) log (n+q))` | `O(n+q)` | Most robust        |
| Offline + Fenwick Max     | Same as segment tree, but suffix becomes prefix after reversal | `O((n+q) log (n+q))` | `O(n+q)` | Compact and fast   |

---

# 13. Interview Summary

The core trick is to process everything **offline**:

- sort points by `nums1` descending
- sort queries by `x` descending
- activate points as they become eligible on the first dimension
- answer the second dimension constraint with a data structure over `nums2`

That turns a hard 2D query problem into a sweep-line plus range maximum queries.
