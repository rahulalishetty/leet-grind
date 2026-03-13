# 1724. Checking Existence of Edge Length Limited Paths II — Exhaustive Java Notes

## Problem Statement

You are given an undirected graph with `n` nodes and an edge list:

```text
edgeList[i] = [ui, vi, disi]
```

where there is an undirected edge between `ui` and `vi` with distance `disi`.

The graph may:

- contain multiple edges between the same pair of nodes
- be disconnected

You need to implement:

```java
class DistanceLimitedPathsExist {

    public DistanceLimitedPathsExist(int n, int[][] edgeList) {
    }

    public boolean query(int p, int q, int limit) {
    }
}
```

For each query, return `true` if there exists a path from `p` to `q` such that **every edge on the path has distance strictly less than `limit`**.

---

## Example

```text
Input:
["DistanceLimitedPathsExist", "query", "query", "query", "query"]
[[6, [[0, 2, 4], [0, 3, 2], [1, 2, 3], [2, 3, 1], [4, 5, 5]]],
 [2, 3, 2],
 [1, 3, 3],
 [2, 0, 3],
 [0, 5, 6]]

Output:
[null, true, false, true, false]
```

Explanation:

- `query(2, 3, 2)` → `true`, because edge `(2,3)` has weight `1 < 2`
- `query(1, 3, 3)` → `false`
- `query(2, 0, 3)` → `true`, via `2 -> 3 -> 0` with edges `1, 2`
- `query(0, 5, 6)` → `false`, because nodes are disconnected

---

## Constraints

- `2 <= n <= 10^4`
- `0 <= edgeList.length <= 10^4`
- `1 <= disi, limit <= 10^9`
- At most `10^4` calls to `query`

---

# 1. Core Insight

For a fixed query limit `L`, we are only allowed to use edges with:

```text
weight < L
```

So each query is really asking:

> If we ignore all edges with weight `>= limit`, are `p` and `q` in the same connected component?

That turns the problem into a dynamic connectivity question based on edge thresholds.

The immediate danger is this:

- up to `10^4` edges
- up to `10^4` queries

A naive graph search per query may still pass in some languages, but it is not the best approach. There are more structured methods.

---

# 2. Approach 1 — BFS / DFS Per Query

## Idea

For each query:

1. Build or use the adjacency list.
2. Run BFS/DFS from `p`.
3. Only traverse edges with `weight < limit`.
4. Return whether `q` is reachable.

This is the most direct interpretation.

## Java Code

```java
import java.util.*;

class DistanceLimitedPathsExist {
    private final List<int[]>[] graph;

    public DistanceLimitedPathsExist(int n, int[][] edgeList) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] e : edgeList) {
            int u = e[0], v = e[1], w = e[2];
            graph[u].add(new int[]{v, w});
            graph[v].add(new int[]{u, w});
        }
    }

    public boolean query(int p, int q, int limit) {
        boolean[] visited = new boolean[graph.length];
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.offer(p);
        visited[p] = true;

        while (!dq.isEmpty()) {
            int cur = dq.poll();
            if (cur == q) return true;

            for (int[] nxt : graph[cur]) {
                int node = nxt[0];
                int weight = nxt[1];
                if (!visited[node] && weight < limit) {
                    visited[node] = true;
                    dq.offer(node);
                }
            }
        }

        return false;
    }
}
```

## Complexity

For each query:

- BFS/DFS can scan up to all nodes and edges

So:

- **Constructor:** `O(n + m)`
- **Each query:** `O(n + m)`
- **Total:** `O(q(n + m))`

With `n, m, q <= 10^4`, this can become roughly `10^8` scale operations. It may survive, but it is not elegant.

## Verdict

Good for intuition. Not the strongest solution.

---

# 3. Approach 2 — Sort Queries Offline + Union Find

This is the standard solution for the related batch-query problem.

## Idea

If all queries were known in advance, we could:

1. Sort edges by weight
2. Sort queries by limit
3. Sweep through limits from small to large
4. Union all edges with `weight < limit`
5. Answer query by checking whether `find(p) == find(q)`

This is excellent, but there is a practical issue:

The class interface requires:

```java
new DistanceLimitedPathsExist(...)
query(...)
query(...)
query(...)
```

So queries arrive online, one by one.

That means pure offline sorting is not directly usable unless we are allowed to gather all queries first, which we are not.

Still, this approach is worth understanding because it motivates the optimal online structure.

## Offline Java Code Sketch

```java
import java.util.*;

class OfflineSolution {
    static class DSU {
        int[] parent, rank;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int a, int b) {
            int pa = find(a), pb = find(b);
            if (pa == pb) return;
            if (rank[pa] < rank[pb]) {
                parent[pa] = pb;
            } else if (rank[pb] < rank[pa]) {
                parent[pb] = pa;
            } else {
                parent[pb] = pa;
                rank[pa]++;
            }
        }
    }

    public boolean[] solve(int n, int[][] edgeList, int[][] queries) {
        Arrays.sort(edgeList, Comparator.comparingInt(a -> a[2]));

        int q = queries.length;
        int[][] ext = new int[q][4];
        for (int i = 0; i < q; i++) {
            ext[i][0] = queries[i][0];
            ext[i][1] = queries[i][1];
            ext[i][2] = queries[i][2];
            ext[i][3] = i;
        }

        Arrays.sort(ext, Comparator.comparingInt(a -> a[2]));

        DSU dsu = new DSU(n);
        boolean[] ans = new boolean[q];
        int ei = 0;

        for (int[] qu : ext) {
            int p = qu[0], qNode = qu[1], limit = qu[2], idx = qu[3];

            while (ei < edgeList.length && edgeList[ei][2] < limit) {
                dsu.union(edgeList[ei][0], edgeList[ei][1]);
                ei++;
            }

            ans[idx] = dsu.find(p) == dsu.find(qNode);
        }

        return ans;
    }
}
```

## Complexity

- Sorting edges: `O(m log m)`
- Sorting queries: `O(q log q)`
- DSU operations: almost linear, `O((m + q) α(n))`

Very strong, but **not online**.

---

# 4. What Online Queries Need

Since queries are online, we need a data structure that answers:

> Among edges with weight `< limit`, are `p` and `q` connected?

This means connectivity changes as `limit` increases.

That suggests we should build a structure over edge weights.

A skeptical way to think about it is:

- If I had the minimum threshold at which two nodes become connected, then any query is easy:
  - return `true` iff that threshold is `< limit`

So the real question becomes:

> For every pair of nodes, what is the minimum possible bottleneck needed to connect them?

That sounds like minimum/maximum spanning tree territory.

---

# 5. Key Theorem — MST Bottleneck Property

For any two nodes `u` and `v`:

- In a **minimum spanning tree (MST)**,
- the maximum edge weight on the unique tree path between `u` and `v`
- is the minimum possible such bottleneck among all paths in the original graph.

This is the crucial theorem.

So if we build an MST of the graph, then for a query `(p, q, limit)`:

- let `mx` be the maximum edge weight on the MST path from `p` to `q`
- then there exists a path in the original graph with all edges `< limit`
  **iff**
  `mx < limit`

This converts the problem into:

1. Build MST forest
2. Preprocess max-edge-on-path queries
3. Answer each query in `O(log n)`

This is the correct online approach.

---

# 6. Approach 3 — MST + Binary Lifting LCA

## High-Level Plan

### Constructor

1. Sort edges by weight
2. Run Kruskal to build an MST forest
3. Build adjacency list of the MST forest
4. DFS/BFS each component to compute:
   - depth
   - connected component id
   - binary lifting parents
   - maximum edge to each ancestor

### Query

To answer `(p, q, limit)`:

1. If `p` and `q` are in different MST components, return `false`
2. Compute maximum edge weight on the tree path from `p` to `q`
3. Return whether that maximum is `< limit`

---

# 7. Why MST Works Here

Suppose in the original graph, there exists a path from `p` to `q` using only edges `< limit`.

Then the minimum bottleneck needed to connect `p` and `q` is `< limit`.

By the MST bottleneck property, the maximum edge on the MST path from `p` to `q` is exactly that minimum bottleneck.

So checking the MST path is enough.

This is much stronger than it first looks. We are not just finding a convenient path in the MST. We are using a theorem that says the MST preserves optimal bottleneck connectivity.

---

# 8. Java Implementation — Recommended Solution

```java
import java.util.*;

class DistanceLimitedPathsExist {
    private static class DSU {
        int[] parent;
        int[] rank;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        boolean union(int a, int b) {
            int pa = find(a), pb = find(b);
            if (pa == pb) return false;

            if (rank[pa] < rank[pb]) {
                parent[pa] = pb;
            } else if (rank[pb] < rank[pa]) {
                parent[pb] = pa;
            } else {
                parent[pb] = pa;
                rank[pa]++;
            }
            return true;
        }
    }

    private final int n;
    private final int LOG;
    private final List<int[]>[] tree;
    private final int[][] up;
    private final int[][] maxEdge;
    private final int[] depth;
    private final int[] comp;

    public DistanceLimitedPathsExist(int n, int[][] edgeList) {
        this.n = n;
        int log = 1;
        while ((1 << log) <= n) log++;
        this.LOG = log;

        tree = new ArrayList[n];
        for (int i = 0; i < n; i++) tree[i] = new ArrayList<>();

        Arrays.sort(edgeList, Comparator.comparingInt(a -> a[2]));
        DSU dsu = new DSU(n);

        for (int[] e : edgeList) {
            int u = e[0], v = e[1], w = e[2];
            if (dsu.union(u, v)) {
                tree[u].add(new int[]{v, w});
                tree[v].add(new int[]{u, w});
            }
        }

        up = new int[LOG][n];
        maxEdge = new int[LOG][n];
        depth = new int[n];
        comp = new int[n];
        Arrays.fill(comp, -1);

        for (int j = 0; j < LOG; j++) {
            Arrays.fill(up[j], -1);
        }

        int componentId = 0;
        for (int i = 0; i < n; i++) {
            if (comp[i] == -1) {
                bfsBuild(i, componentId++);
            }
        }

        for (int j = 1; j < LOG; j++) {
            for (int v = 0; v < n; v++) {
                int mid = up[j - 1][v];
                if (mid != -1) {
                    up[j][v] = up[j - 1][mid];
                    maxEdge[j][v] = Math.max(maxEdge[j - 1][v], maxEdge[j - 1][mid]);
                }
            }
        }
    }

    private void bfsBuild(int start, int componentId) {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.offer(start);
        comp[start] = componentId;
        depth[start] = 0;
        up[0][start] = -1;
        maxEdge[0][start] = 0;

        while (!dq.isEmpty()) {
            int cur = dq.poll();

            for (int[] nxt : tree[cur]) {
                int node = nxt[0], w = nxt[1];
                if (node == up[0][cur]) continue;
                if (comp[node] != -1) continue;

                comp[node] = componentId;
                depth[node] = depth[cur] + 1;
                up[0][node] = cur;
                maxEdge[0][node] = w;
                dq.offer(node);
            }
        }
    }

    public boolean query(int p, int q, int limit) {
        if (comp[p] != comp[q]) return false;
        int maxOnPath = getMaxEdgeOnPath(p, q);
        return maxOnPath < limit;
    }

    private int getMaxEdgeOnPath(int a, int b) {
        int ans = 0;

        if (depth[a] < depth[b]) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        int diff = depth[a] - depth[b];
        for (int j = LOG - 1; j >= 0; j--) {
            if (((diff >> j) & 1) == 1) {
                ans = Math.max(ans, maxEdge[j][a]);
                a = up[j][a];
            }
        }

        if (a == b) return ans;

        for (int j = LOG - 1; j >= 0; j--) {
            if (up[j][a] != up[j][b]) {
                ans = Math.max(ans, maxEdge[j][a]);
                ans = Math.max(ans, maxEdge[j][b]);
                a = up[j][a];
                b = up[j][b];
            }
        }

        ans = Math.max(ans, maxEdge[0][a]);
        ans = Math.max(ans, maxEdge[0][b]);
        return ans;
    }
}
```

---

# 9. Complexity

Let:

- `m = edgeList.length`

### Constructor

- Sort edges: `O(m log m)`
- Kruskal DSU: `O(m α(n))`
- BFS/DFS preprocessing: `O(n log n)`

Overall constructor:

```text
O(m log m + n log n)
```

### Query

Each query uses binary lifting:

```text
O(log n)
```

### Space

- MST adjacency: `O(n)`
- lifting tables: `O(n log n)`

Overall:

```text
O(n log n)
```

This is excellent for `10^4` constraints.

---

# 10. Dry Run on the Example

Graph:

```text
0-2 (4)
0-3 (2)
1-2 (3)
2-3 (1)
4-5 (5)
```

## Step 1: Build MST forest with Kruskal

Sort by weight:

```text
2-3 (1)
0-3 (2)
1-2 (3)
0-2 (4)
4-5 (5)
```

Pick edges greedily if they do not form a cycle:

- `2-3 (1)` -> take
- `0-3 (2)` -> take
- `1-2 (3)` -> take
- `0-2 (4)` -> skip, cycle
- `4-5 (5)` -> take

MST forest edges:

```text
2-3 (1)
0-3 (2)
1-2 (3)
4-5 (5)
```

## Query 1: `query(2,3,2)`

Path in MST:

```text
2 -> 3
```

Maximum edge on path:

```text
1
```

Check:

```text
1 < 2
```

Answer: `true`

## Query 2: `query(1,3,3)`

Path:

```text
1 -> 2 -> 3
```

Max edge:

```text
max(3,1) = 3
```

Check:

```text
3 < 3
```

False

## Query 3: `query(2,0,3)`

Path:

```text
2 -> 3 -> 0
```

Max edge:

```text
max(1,2) = 2
```

Check:

```text
2 < 3
```

True

## Query 4: `query(0,5,6)`

Different components in MST forest.

False

Everything matches.

---

# 11. Correctness Proof

## Lemma 1

For any two nodes `u` and `v`, the unique path between them in an MST minimizes the maximum edge weight among all possible `u-v` paths in the original graph.

### Proof

This is the standard bottleneck property of MSTs. If there existed another path whose maximum edge were smaller than the maximum edge on the MST path, then replacing that larger MST edge would yield a spanning tree with smaller total weight or violate MST minimality via the cut/cycle property. Therefore the MST path realizes the minimum bottleneck value. ∎

---

## Lemma 2

There exists a path from `p` to `q` using only edges `< limit` iff the minimum bottleneck value between `p` and `q` is `< limit`.

### Proof

If such a path exists, its maximum edge is `< limit`, so the minimum possible bottleneck is also `< limit`. Conversely, if the minimum bottleneck is `< limit`, then there exists some path whose maximum edge is `< limit`, which means every edge on that path is `< limit`. ∎

---

## Lemma 3

For nodes in the same connected component, the maximum edge on their MST path is exactly the minimum bottleneck value.

### Proof

By Lemma 1, the MST path minimizes the maximum edge among all possible paths, so its maximum edge equals the minimum bottleneck value. ∎

---

## Theorem

`query(p, q, limit)` returns `true` iff there exists a path from `p` to `q` such that every edge on the path has distance strictly less than `limit`.

### Proof

If `p` and `q` are in different MST components, then they are disconnected in the original graph as well, so the answer is `false`.

Otherwise, by Lemma 3, the maximum edge weight on the MST path equals the minimum bottleneck between `p` and `q`. By Lemma 2, such a path exists with all edges `< limit` iff that bottleneck is `< limit`. The algorithm checks exactly this condition, so it is correct. ∎

---

# 12. Common Mistakes

## Mistake 1: Using shortest path algorithms

This is not a shortest path problem. The query condition is:

```text
all edges on the path must be < limit
```

That is a bottleneck/connectivity condition, not a sum-of-weights condition.

## Mistake 2: Building a fresh DSU for every query

You could sort all edges below the limit each time, but that becomes too expensive.

## Mistake 3: Thinking the MST changes per query

The MST is built once. The bottleneck property makes it sufficient for all queries.

## Mistake 4: Forgetting disconnected components

The graph may not be connected. Your MST is really an MST forest.

## Mistake 5: Using `<= limit` instead of `< limit`

The statement requires **strictly less than** `limit`.

---

# 13. Comparison of Approaches

| Approach                   |                  Constructor |      Query |        Space | Notes                      |
| -------------------------- | ---------------------------: | ---------: | -----------: | -------------------------- |
| BFS / DFS per query        |                   `O(n + m)` | `O(n + m)` |   `O(n + m)` | Simple but slower          |
| Offline sort queries + DSU | `O(m log m + q log q)` batch | not online |   `O(n + q)` | Great for batch version    |
| MST + LCA / binary lifting |       `O(m log m + n log n)` | `O(log n)` | `O(n log n)` | Best for this class design |

---

# 14. Final Recommended Java Solution

```java
import java.util.*;

class DistanceLimitedPathsExist {
    private static class DSU {
        int[] parent;
        int[] rank;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        boolean union(int a, int b) {
            int pa = find(a), pb = find(b);
            if (pa == pb) return false;

            if (rank[pa] < rank[pb]) parent[pa] = pb;
            else if (rank[pb] < rank[pa]) parent[pb] = pa;
            else {
                parent[pb] = pa;
                rank[pa]++;
            }
            return true;
        }
    }

    private final int n;
    private final int LOG;
    private final List<int[]>[] tree;
    private final int[][] up;
    private final int[][] maxEdge;
    private final int[] depth;
    private final int[] comp;

    public DistanceLimitedPathsExist(int n, int[][] edgeList) {
        this.n = n;
        int log = 1;
        while ((1 << log) <= n) log++;
        LOG = log;

        tree = new ArrayList[n];
        for (int i = 0; i < n; i++) tree[i] = new ArrayList<>();

        Arrays.sort(edgeList, Comparator.comparingInt(a -> a[2]));
        DSU dsu = new DSU(n);

        for (int[] e : edgeList) {
            int u = e[0], v = e[1], w = e[2];
            if (dsu.union(u, v)) {
                tree[u].add(new int[]{v, w});
                tree[v].add(new int[]{u, w});
            }
        }

        up = new int[LOG][n];
        maxEdge = new int[LOG][n];
        depth = new int[n];
        comp = new int[n];
        Arrays.fill(comp, -1);

        for (int j = 0; j < LOG; j++) Arrays.fill(up[j], -1);

        int cid = 0;
        for (int i = 0; i < n; i++) {
            if (comp[i] == -1) {
                build(i, cid++);
            }
        }

        for (int j = 1; j < LOG; j++) {
            for (int v = 0; v < n; v++) {
                int mid = up[j - 1][v];
                if (mid != -1) {
                    up[j][v] = up[j - 1][mid];
                    maxEdge[j][v] = Math.max(maxEdge[j - 1][v], maxEdge[j - 1][mid]);
                }
            }
        }
    }

    private void build(int start, int cid) {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.offer(start);
        comp[start] = cid;
        depth[start] = 0;
        maxEdge[0][start] = 0;

        while (!dq.isEmpty()) {
            int cur = dq.poll();
            for (int[] nxt : tree[cur]) {
                int node = nxt[0], w = nxt[1];
                if (node == up[0][cur]) continue;
                if (comp[node] != -1) continue;

                comp[node] = cid;
                depth[node] = depth[cur] + 1;
                up[0][node] = cur;
                maxEdge[0][node] = w;
                dq.offer(node);
            }
        }
    }

    public boolean query(int p, int q, int limit) {
        if (comp[p] != comp[q]) return false;
        return getMaxOnPath(p, q) < limit;
    }

    private int getMaxOnPath(int a, int b) {
        int ans = 0;

        if (depth[a] < depth[b]) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        int diff = depth[a] - depth[b];
        for (int j = LOG - 1; j >= 0; j--) {
            if (((diff >> j) & 1) == 1) {
                ans = Math.max(ans, maxEdge[j][a]);
                a = up[j][a];
            }
        }

        if (a == b) return ans;

        for (int j = LOG - 1; j >= 0; j--) {
            if (up[j][a] != up[j][b]) {
                ans = Math.max(ans, maxEdge[j][a]);
                ans = Math.max(ans, maxEdge[j][b]);
                a = up[j][a];
                b = up[j][b];
            }
        }

        ans = Math.max(ans, maxEdge[0][a]);
        ans = Math.max(ans, maxEdge[0][b]);
        return ans;
    }
}
```

---

# 15. Interview Summary

The class requires online queries, so the usual offline sorted-query DSU method is not the best fit.

The decisive observation is the MST bottleneck property:

> For any two nodes, the maximum edge on their path in the MST is the minimum possible bottleneck among all paths in the original graph.

So:

1. Build an MST forest with Kruskal
2. Preprocess binary lifting tables storing maximum edge to each ancestor
3. For each query, compute the maximum edge on the MST path
4. Return whether that value is `< limit`

That gives:

- preprocessing: `O(m log m + n log n)`
- per query: `O(log n)`

and is the cleanest solution for this interface.
