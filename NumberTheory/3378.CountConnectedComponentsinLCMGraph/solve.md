# 3378. Count Connected Components in LCM Graph — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int countComponents(int[] nums, int threshold) {

    }
}
```

---

# Problem Restatement

We have `n` nodes.

Node `i` stores the value:

```text
nums[i]
```

There is an undirected edge between nodes `i` and `j` iff:

```text
lcm(nums[i], nums[j]) <= threshold
```

We must return the number of connected components in this graph.

---

# Core Difficulty

A direct pairwise check would test all pairs:

```text
O(n^2)
```

With:

```text
n <= 10^5
```

that is impossible.

So we need to exploit the structure of the LCM condition.

---

# First Key Observation

If a value `x` is greater than `threshold`, then for any other positive integer `y`:

```text
lcm(x, y) >= x > threshold
```

So any number strictly greater than `threshold` can never connect to any other node.

That means:

> Every `nums[i] > threshold` is automatically an isolated component.

This is a major simplification.

So from now on, the only interesting values are those with:

```text
nums[i] <= threshold
```

---

# Second Key Observation

Suppose `a <= threshold` and `b <= threshold`.

They are connected if:

```text
lcm(a, b) <= threshold
```

Instead of checking all such pairs directly, we can think in terms of shared multiples.

If some value `m <= threshold` is a common multiple of both `a` and `b`, then:

```text
lcm(a, b) <= m <= threshold
```

and therefore `a` and `b` should be connected.

In particular, if we introduce an auxiliary node for each integer `m` in `[1..threshold]`, and connect every present value `v` to every multiple of `v`, then:

- two values are connected through some `m`
- exactly when they have some common multiple within the threshold
- which is equivalent to `lcm(v1, v2) <= threshold`

This turns the problem into a DSU over values and their multiples.

---

# Graph Reformulation

For each value `v` that actually appears in `nums` and satisfies:

```text
v <= threshold
```

connect `v` to all multiples:

```text
v, 2v, 3v, ...
```

up to `threshold`.

Then two values end up in the same component iff they share some common multiple `<= threshold`.

That is exactly the same as:

```text
lcm(v1, v2) <= threshold
```

So the reformulation is correct.

---

# Approach 1 — DSU Over Values 1..threshold With Multiples (Recommended)

## Idea

1. Separate all values:
   - `nums[i] > threshold` are isolated components
   - `nums[i] <= threshold` participate in the DSU structure
2. Build DSU over integers `1..threshold`
3. For every present value `v <= threshold`, union `v` with all multiples of `v` up to `threshold`
4. Among the values that actually appear in `nums`, count how many distinct DSU roots exist
5. Add the isolated count from values `> threshold`

This is the intended efficient solution.

---

## Why this works

Two values `a` and `b` should be connected if:

```text
lcm(a, b) <= threshold
```

That means there exists some common multiple of `a` and `b` within the threshold.
In fact the smallest such common multiple is `lcm(a, b)` itself.

By unioning each present value with all its multiples up to `threshold`, any two values sharing a valid common multiple get merged into the same DSU set.

Thus DSU connectivity matches graph connectivity.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n + 1];
            size = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }

            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public int countComponents(int[] nums, int threshold) {
        boolean[] present = new boolean[threshold + 1];
        int isolated = 0;

        for (int x : nums) {
            if (x > threshold) {
                isolated++;
            } else {
                present[x] = true;
            }
        }

        DSU dsu = new DSU(threshold);

        for (int v = 1; v <= threshold; v++) {
            if (!present[v]) continue;

            for (int multiple = v; multiple <= threshold; multiple += v) {
                dsu.union(v, multiple);
            }
        }

        Set<Integer> roots = new HashSet<>();
        for (int v = 1; v <= threshold; v++) {
            if (present[v]) {
                roots.add(dsu.find(v));
            }
        }

        return isolated + roots.size();
    }
}
```

---

## Complexity

Let:

```text
T = threshold
```

The harmonic-series multiple iteration gives:

```text
1 + 1/2 + 1/3 + ... + 1/T
```

behavior, so the total number of multiple visits is about:

```text
O(T log T)
```

Thus:

```text
Time:  O(T log T * α(T))
Space: O(T)
```

Since:

```text
threshold <= 2 * 10^5
```

this is efficient.

---

# Approach 2 — DSU Only on Present Values, Enumerating Common Multiples (Equivalent but Less Convenient)

## Idea

Instead of building a DSU over all numbers `1..threshold`, we can try to union only values that appear in `nums`.

For each multiple `m` from `1` to `threshold`, collect all present divisors of `m`.
Any two such divisors `a` and `b` satisfy:

```text
a | m and b | m
```

So they share a common multiple `m <= threshold`, hence:

```text
lcm(a, b) <= threshold
```

and should be connected.

Then union all present divisors attached to the same `m`.

This is mathematically valid, but usually a bit more cumbersome than Approach 1.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            if (size[ra] < size[rb]) {
                int t = ra; ra = rb; rb = t;
            }
            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public int countComponents(int[] nums, int threshold) {
        List<Integer> active = new ArrayList<>();
        int isolated = 0;

        for (int x : nums) {
            if (x > threshold) isolated++;
            else active.add(x);
        }

        int m = active.size();
        if (m == 0) return isolated;

        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i < m; i++) {
            index.put(active.get(i), i);
        }

        boolean[] present = new boolean[threshold + 1];
        for (int x : active) present[x] = true;

        DSU dsu = new DSU(m);

        for (int mult = 1; mult <= threshold; mult++) {
            int firstIdx = -1;
            for (int d = 1; d * d <= mult; d++) {
                if (mult % d != 0) continue;

                int d1 = d;
                int d2 = mult / d;

                if (d1 <= threshold && present[d1]) {
                    int idx = index.get(d1);
                    if (firstIdx == -1) firstIdx = idx;
                    else dsu.union(firstIdx, idx);
                }

                if (d2 != d1 && d2 <= threshold && present[d2]) {
                    int idx = index.get(d2);
                    if (firstIdx == -1) firstIdx = idx;
                    else dsu.union(firstIdx, idx);
                }
            }
        }

        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < m; i++) {
            roots.add(dsu.find(i));
        }

        return isolated + roots.size();
    }
}
```

---

## Why this is less preferred

It works, but enumerating divisors for every multiple is more awkward and often slower in practice than the direct multiples-union approach.

The first solution is cleaner.

---

# Approach 3 — Direct Pairwise LCM Check With DSU (Too Slow)

## Idea

Check every pair:

```text
lcm(nums[i], nums[j]) <= threshold
```

If true, union them.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            if (size[ra] < size[rb]) {
                int t = ra; ra = rb; rb = t;
            }
            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public int countComponents(int[] nums, int threshold) {
        int n = nums.length;
        DSU dsu = new DSU(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                long g = gcd(nums[i], nums[j]);
                long l = nums[i] / g * 1L * nums[j];
                if (l <= threshold) {
                    dsu.union(i, j);
                }
            }
        }

        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < n; i++) {
            roots.add(dsu.find(i));
        }

        return roots.size();
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

## Why it fails

This is:

```text
O(n^2)
```

which is impossible for:

```text
n <= 10^5
```

So this approach is not viable.

---

# Approach 4 — Explicit Graph Construction + DFS/BFS (Also Too Slow)

## Idea

Construct the adjacency list explicitly using pairwise LCM checks, then run DFS/BFS to count components.

This has the same fatal problem as Approach 3: constructing all candidate edges is already too expensive.

---

# Detailed Walkthrough

## Example 1

```text
nums = [2,4,8,3,9]
threshold = 5
```

Values greater than `5`:

```text
8, 9
```

So they are immediately isolated.

Active values:

```text
2, 4, 3
```

Now:

- `2` connects to multiples `2,4`
- `4` connects to `4`
- `3` connects to `3`

So `2` and `4` get united, while `3` stays alone.

Components:

```text
(2,4), (3), (8), (9)
```

Total:

```text
4
```

---

## Example 2

```text
nums = [2,4,8,3,9,12]
threshold = 10
```

Value greater than `10`:

```text
12
```

So `12` is isolated.

Active values:

```text
2,4,8,3,9
```

Multiples within `10`:

- `2` reaches `2,4,6,8,10`
- `4` reaches `4,8`
- `8` reaches `8`
- `3` reaches `3,6,9`
- `9` reaches `9`

Now observe:

- `2` and `3` both connect to `6`
- `3` and `9` connect via `9`
- `2`, `4`, `8` connect through shared multiples

So all active values join into one component:

```text
(2,3,4,8,9)
```

plus isolated:

```text
(12)
```

Total:

```text
2
```

---

# Important Correctness Argument

Why does “union value with all its multiples up to threshold” work?

Take two active values `a` and `b`.

They should be adjacent in the original graph iff:

```text
lcm(a, b) <= threshold
```

Now:

- if `lcm(a, b) <= threshold`, then `lcm(a, b)` is a common multiple within the threshold
- so both `a` and `b` get unioned with that same number, hence end up in the same DSU set

Conversely:

- if `a` and `b` share some common multiple `m <= threshold`
- then their least common multiple must also satisfy:

```text
lcm(a, b) <= m <= threshold
```

So they indeed should be adjacent.

Thus DSU connectivity matches graph connectivity exactly.

---

# Common Pitfalls

## 1. Forgetting that numbers larger than threshold are automatically isolated

This is one of the biggest simplifications.

---

## 2. Trying to check all pairs

That leads to quadratic time and is impossible.

---

## 3. Confusing “connected by a path” with “directly adjacent”

The DSU multiple-based method handles both because it reconstructs the exact edge connectivity structure through shared common multiples.

---

## 4. Using LCM formula without overflow care

If you ever compute:

```text
lcm(a, b) = a / gcd(a,b) * b
```

use `long`.

---

# Best Approach

## Recommended: DSU over 1..threshold, unioning present values with their multiples

This is the cleanest and most efficient approach because:

- it uses the small threshold bound
- it avoids pairwise checks completely
- it directly captures the LCM condition via common multiples

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n + 1];
            size = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }

            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public int countComponents(int[] nums, int threshold) {
        boolean[] present = new boolean[threshold + 1];
        int isolated = 0;

        for (int x : nums) {
            if (x > threshold) {
                isolated++;
            } else {
                present[x] = true;
            }
        }

        DSU dsu = new DSU(threshold);

        for (int v = 1; v <= threshold; v++) {
            if (!present[v]) continue;

            for (int multiple = v; multiple <= threshold; multiple += v) {
                dsu.union(v, multiple);
            }
        }

        Set<Integer> roots = new HashSet<>();
        for (int v = 1; v <= threshold; v++) {
            if (present[v]) {
                roots.add(dsu.find(v));
            }
        }

        return isolated + roots.size();
    }
}
```

---

# Complexity Summary

Let:

```text
T = threshold
```

Then:

```text
Time:  O(T log T * α(T))
Space: O(T)
```

This is efficient because:

```text
threshold <= 2 * 10^5
```

---

# Final Takeaway

The key shift is:

Do not think in terms of checking LCM for every pair.

Instead think:

- two values are adjacent iff they have a common multiple within the threshold
- unioning each present value with all its multiples up to the threshold captures exactly that condition

That turns a seemingly pairwise graph problem into a divisor/multiple DSU problem.
