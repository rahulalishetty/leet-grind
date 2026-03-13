# 3515. Shortest Path in a Weighted Tree — Exhaustive Java Notes

## Problem Statement

You are given a weighted tree with `n` nodes rooted at node `1`.

Each edge is:

```text
[u, v, w]
```

meaning there is an undirected edge between `u` and `v` with weight `w`.

You must process queries of two types:

### Type 1 — Update edge weight

```text
[1, u, v, w']
```

Update the weight of edge `(u, v)` to `w'`.

### Type 2 — Query root-to-node distance

```text
[2, x]
```

Return the shortest path distance from root node `1` to node `x`.

Because the graph is a tree, the root-to-node path is unique, so this is simply the sum of the edge weights on that unique path.

Return the answers to all type-2 queries in order.

---

## Example 1

```text
Input:
n = 2
edges = [[1,2,7]]
queries = [[2,2],[1,1,2,4],[2,2]]

Output:
[7,4]
```

Explanation:

- Distance from `1` to `2` is `7`
- Update edge `(1,2)` from `7` to `4`
- New distance from `1` to `2` is `4`

---

## Example 2

```text
Input:
n = 3
edges = [[1,2,2],[1,3,4]]
queries = [[2,1],[2,3],[1,1,3,7],[2,2],[2,3]]

Output:
[0,4,2,7]
```

---

## Example 3

```text
Input:
n = 4
edges = [[1,2,2],[2,3,1],[3,4,5]]
queries = [[2,4],[2,3],[1,2,3,3],[2,2],[2,3]]

Output:
[8,3,2,5]
```

---

## Constraints

```text
1 <= n <= 10^5
1 <= queries.length <= 10^5
```

So we need something close to:

```text
O((n + q) log n)
```

A naive recomputation after every update is too slow.

---

# 1. Core Insight

In a rooted tree, every node `x` has exactly one path from root `1` to `x`.

So the answer for node `x` is:

```text
dist[x] = sum of edge weights on path from 1 to x
```

Now suppose an edge weight changes.

If edge `(parent, child)` changes by:

```text
delta = newWeight - oldWeight
```

then:

- every node inside `child`'s subtree has its root-distance changed by `delta`
- every node outside that subtree is unaffected

That is the crucial observation.

So the problem becomes:

> support subtree range add
> support point query

This is exactly what Euler Tour + Fenwick Tree is good at.

---

# 2. Euler Tour Flattening

Do a DFS from root `1`.

For each node `u`, record:

- `tin[u]` = entry time in DFS
- `tout[u]` = exit time in DFS

Then every subtree becomes a contiguous interval in Euler order:

```text
subtree(u) = [tin[u], tout[u]]
```

So if edge `(parent, child)` changes by `delta`, we can add `delta` to the entire interval:

```text
[tin[child], tout[child]]
```

Then querying node `x` just means reading the accumulated added value at:

```text
tin[x]
```

and adding it to the node’s original root-distance.

---

# 3. Edge Orientation Trick

The queries give an edge as `(u, v)` in arbitrary order.

But in the rooted tree, every edge has a unique direction:

```text
parent -> child
```

So during DFS, we store:

- `parent[node]`
- `baseWeightToParent[node]`

Then for each original edge `(u, v)` we identify which endpoint is deeper.
That deeper endpoint is the child whose subtree gets affected by updates.

So for each edge we only really need to know:

```text
child endpoint
current weight of that edge
```

---

# 4. Approach 1 — Recompute Distances After Every Update

## Idea

Every update changes an edge weight.

After each update, run DFS/BFS again from root and recompute all root distances.

Then every type-2 query is `O(1)`.

## Why it is too slow

Each update costs:

```text
O(n)
```

With up to `10^5` updates, total complexity becomes:

```text
O(nq)
```

which is far too large.

## Java Code

```java
import java.util.*;

class SolutionBruteForce {
    List<int[]>[] g;
    Map<Long, Integer> weight;
    long[] dist;

    public int[] treeQueries(int n, int[][] edges, int[][] queries) {
        g = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) g[i] = new ArrayList<>();

        weight = new HashMap<>();
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            g[u].add(new int[]{v});
            g[v].add(new int[]{u});
            weight.put(key(u, v), w);
            weight.put(key(v, u), w);
        }

        recompute(n);

        List<Integer> ans = new ArrayList<>();
        for (int[] q : queries) {
            if (q[0] == 1) {
                int u = q[1], v = q[2], w = q[3];
                weight.put(key(u, v), w);
                weight.put(key(v, u), w);
                recompute(n);
            } else {
                int x = q[1];
                ans.add((int) dist[x]);
            }
        }

        return ans.stream().mapToInt(i -> i).toArray();
    }

    private void recompute(int n) {
        dist = new long[n + 1];
        boolean[] vis = new boolean[n + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        dq.add(1);
        vis[1] = true;

        while (!dq.isEmpty()) {
            int u = dq.poll();
            for (int[] next : g[u]) {
                int v = next[0];
                if (vis[v]) continue;
                vis[v] = true;
                dist[v] = dist[u] + weight.get(key(u, v));
                dq.add(v);
            }
        }
    }

    private long key(int a, int b) {
        return (((long) a) << 32) ^ b;
    }
}
```

## Complexity

- Update: `O(n)`
- Query: `O(1)`

Overall worst case:

```text
O(nq)
```

Too slow.

---

# 5. Approach 2 — DFS Path Sum Per Query

## Idea

Keep updates in a map.
For each query `[2, x]`, compute path sum from root to `x` by traversing parent chain.

If we precompute `parent[x]`, then each query is:

```text
O(depth[x])
```

Still too slow in a chain-like tree where depth can be `O(n)`.

---

# 6. Approach 3 — Euler Tour + Fenwick Tree (Optimal)

This is the correct scalable solution.

## Main idea

1. Root the tree at `1`
2. Compute initial root-distance `baseDist[x]`
3. Compute Euler interval `[tin[x], tout[x]]`
4. For each edge update:
   - find the deeper endpoint = `child`
   - let `delta = newWeight - oldWeight`
   - range-add `delta` to `child` subtree
5. For query `[2, x]`:
   - answer = `baseDist[x] + accumulatedDeltaAt(tin[x])`

---

# 7. Fenwick Tree for Range Add + Point Query

A Fenwick tree can support:

- add `delta` to interval `[l, r]`
- get value at position `p`

using the difference-array trick:

To add `delta` on `[l, r]`:

```text
bit.add(l, delta)
bit.add(r + 1, -delta)
```

To get the value at point `p`:

```text
bit.prefixSum(p)
```

So subtree update becomes:

```text
add(tin[child], +delta)
add(tout[child] + 1, -delta)
```

---

# 8. Detailed Preprocessing

During DFS, compute:

- `parent[u]`
- `depth[u]`
- `baseDist[u]`
- `tin[u]`
- `tout[u]`
- current edge weight for node `u` to its parent

Since updates identify edges by `(u, v)`, store a map:

```text
edge -> child
```

So later, for update `(u, v, w')`, we directly know which subtree to update.

---

# 9. Why This Works

Suppose edge `(p, c)` changes by `delta`.

Every node in subtree `c` has a root path:

```text
1 -> ... -> p -> c -> ... -> node
```

So that path includes edge `(p, c)`, meaning the root-distance changes by `delta`.

Any node outside subtree `c` does not use that edge on its root path.

So subtree range add is exactly correct.

---

# 10. Optimal Java Solution

```java
import java.util.*;

class Solution {
    static class Edge {
        int to, w;
        Edge(int to, int w) {
            this.to = to;
            this.w = w;
        }
    }

    static class Fenwick {
        long[] bit;
        int n;

        Fenwick(int n) {
            this.n = n;
            this.bit = new long[n + 2];
        }

        void add(int idx, long delta) {
            while (idx <= n) {
                bit[idx] += delta;
                idx += idx & -idx;
            }
        }

        long sum(int idx) {
            long res = 0;
            while (idx > 0) {
                res += bit[idx];
                idx -= idx & -idx;
            }
            return res;
        }

        void rangeAdd(int l, int r, long delta) {
            add(l, delta);
            add(r + 1, -delta);
        }

        long pointQuery(int idx) {
            return sum(idx);
        }
    }

    List<Edge>[] g;
    int[] parent;
    int[] tin, tout;
    long[] baseDist;
    int timer = 0;

    public int[] treeQueries(int n, int[][] edges, int[][] queries) {
        g = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) g[i] = new ArrayList<>();

        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            g[u].add(new Edge(v, w));
            g[v].add(new Edge(u, w));
        }

        parent = new int[n + 1];
        tin = new int[n + 1];
        tout = new int[n + 1];
        baseDist = new long[n + 1];

        dfs(1, 0, 0L);

        // Map each undirected edge to the child endpoint in rooted tree
        Map<Long, Integer> edgeToChild = new HashMap<>();
        Map<Long, Integer> edgeWeight = new HashMap<>();

        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            int child = (parent[u] == v) ? u : v;
            long key = edgeKey(u, v);
            edgeToChild.put(key, child);
            edgeWeight.put(key, w);
        }

        Fenwick bit = new Fenwick(n + 2);
        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                int u = q[1], v = q[2], newW = q[3];
                long key = edgeKey(u, v);

                int child = edgeToChild.get(key);
                int oldW = edgeWeight.get(key);
                int delta = newW - oldW;

                if (delta != 0) {
                    bit.rangeAdd(tin[child], tout[child], delta);
                    edgeWeight.put(key, newW);
                }
            } else {
                int x = q[1];
                long cur = baseDist[x] + bit.pointQuery(tin[x]);
                ans.add((int) cur);
            }
        }

        return ans.stream().mapToInt(i -> i).toArray();
    }

    private void dfs(int u, int p, long dist) {
        parent[u] = p;
        baseDist[u] = dist;
        tin[u] = ++timer;

        for (Edge e : g[u]) {
            if (e.to == p) continue;
            dfs(e.to, u, dist + e.w);
        }

        tout[u] = timer;
    }

    private long edgeKey(int u, int v) {
        int a = Math.min(u, v);
        int b = Math.max(u, v);
        return (((long) a) << 32) ^ b;
    }
}
```

---

# 11. Complexity Analysis

## DFS preprocessing

```text
O(n)
```

## Each update

- edge lookup in map: `O(1)` average
- Fenwick range update: `O(log n)`

## Each query

- Fenwick point query: `O(log n)`

Overall:

```text
O((n + q) log n)
```

Space:

```text
O(n)
```

This fits comfortably.

---

# 12. Dry Run on Example 3

```text
n = 4
edges = [[1,2,2],[2,3,1],[3,4,5]]
```

Rooted tree:

```text
1
 \
  2
   \
    3
     \
      4
```

Initial distances:

```text
baseDist[1] = 0
baseDist[2] = 2
baseDist[3] = 3
baseDist[4] = 8
```

Now update:

```text
[1,2,3,3]
```

Old weight of edge `(2,3)` = `1`
New weight = `3`

So:

```text
delta = +2
```

The deeper endpoint is node `3`, so subtree of `3` gets `+2`.

Affected nodes:

- `3`
- `4`

Now query:

```text
dist(2) = baseDist[2] + 0 = 2
dist(3) = baseDist[3] + 2 = 5
```

Correct.

---

# 13. Alternative Approach — Heavy-Light Decomposition

You could also solve this with HLD.

## Idea

Store edge weights on the deeper endpoint.
Root-to-node query becomes path sum from root to node.
Edge update becomes point update.

This also gives:

```text
O(log^2 n)
```

or `O(log n)` with segment tree tricks.

## Why Euler Tour is simpler here

This problem is easier than general path queries because all queries are from root `1`.

That means edge updates affect entire subtrees, so Euler Tour is much simpler and more elegant.

So HLD works, but it is not the best choice here.

---

# 14. Alternative Approach — Segment Tree Instead of Fenwick

Instead of Fenwick, you can use a segment tree with lazy propagation.

That supports subtree range add and point query too.

But Fenwick is shorter and cleaner for this exact update/query pattern.

---

# 15. Common Mistakes

## Mistake 1: Recomputing all distances after every update

Too slow: `O(nq)`.

## Mistake 2: Forgetting that only one endpoint is the subtree root

For update `(u, v)`, you must identify the deeper endpoint in the rooted tree.

## Mistake 3: Using directed edge keys

The input query may say `(u, v)` or `(v, u)`.
Always normalize the edge key using:

```text
(min(u,v), max(u,v))
```

## Mistake 4: Overflow concerns

Distances can become large over many edges, so internal storage should use:

```java
long
```

Even if the final method signature returns `int[]`, using `long` internally is safer.

## Mistake 5: Wrong Fenwick usage

This problem needs:

- subtree range add
- point query

So use the difference-array trick, not plain point-add / prefix-sum.

---

# 16. Correctness Sketch

## Lemma 1

For any edge `(parent, child)`, an update to its weight changes the root-distance of exactly the nodes in `child`'s subtree.

### Reason

Every node in that subtree uses the edge on its path from root. No node outside the subtree does.

## Lemma 2

In Euler Tour order, the subtree of any node forms a contiguous interval `[tin[node], tout[node]]`.

### Reason

Standard DFS subtree flattening property.

## Lemma 3

Applying `delta` to `[tin[child], tout[child]]` correctly represents the root-distance change caused by an edge weight update.

### Reason

By Lemma 1 and Lemma 2.

## Lemma 4

For any node `x`, the total accumulated change affecting it is exactly the point-query value at `tin[x]`.

### Reason

The Fenwick difference structure stores the sum of all subtree updates covering that Euler position.

## Theorem

The algorithm returns the correct root-distance for every query `[2, x]`.

### Reason

Initial distance is `baseDist[x]`, and every subsequent edge update contributes exactly when `x` lies in the affected subtree. The Fenwick query accumulates all such contributions.

---

# 17. Comparison of Approaches

| Approach                    |     Update |                      Query |               Total | Verdict      |
| --------------------------- | ---------: | -------------------------: | ------------------: | ------------ |
| Recompute all distances     |     `O(n)` |                     `O(1)` |             `O(nq)` | Too slow     |
| Parent-chain walk per query |     `O(1)` |                 `O(depth)` | Too slow worst-case | Too slow     |
| HLD + segment tree          | `O(log n)` | `O(log n)` or `O(log^2 n)` |                Good | More complex |
| Euler Tour + Fenwick        | `O(log n)` |                 `O(log n)` |      Optimal enough | Best choice  |

---

# 18. Final Interview Summary

This problem looks like dynamic shortest paths, but because the graph is a **tree rooted at 1**, every query is just a root-to-node path sum.

When edge `(parent, child)` changes by `delta`, every node in `child`'s subtree has its answer changed by `delta`.

So:

- flatten the tree with Euler Tour
- subtree becomes a contiguous interval
- use Fenwick Tree for range add + point query
- answer for node `x` is:

```text
baseDist[x] + deltaAt(tin[x])
```

That yields:

```text
O((n + q) log n)
```

with clean implementation.
