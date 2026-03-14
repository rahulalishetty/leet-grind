# 1617. Count Subtrees With Max Distance Between Cities — Exhaustive Java Notes

## Problem Statement

You are given a tree with `n` cities labeled from `1` to `n`.

The input:

```text
edges[i] = [u, v]
```

means there is a bidirectional edge between cities `u` and `v`.

Because the graph is a tree:

- it has exactly `n - 1` edges,
- and there is exactly one simple path between any two cities.

A **subtree** here means a subset of cities such that:

- every city in the subset is reachable from every other city in the subset,
- and every path between two chosen cities uses only cities inside the subset.

For every distance `d` from `1` to `n - 1`, count how many subtrees have **diameter exactly `d`**.

The diameter of a subtree is the maximum distance between any two cities inside that subtree.

Return an array `ans` of size `n - 1`, where:

```text
ans[d - 1] = number of connected induced subgraphs with diameter d
```

---

## Example 1

```text
Input:
n = 4
edges = [[1,2],[2,3],[2,4]]

Output:
[3,4,0]
```

Explanation:

Diameter `1` subtrees:

- `{1,2}`
- `{2,3}`
- `{2,4}`

Diameter `2` subtrees:

- `{1,2,3}`
- `{1,2,4}`
- `{2,3,4}`
- `{1,2,3,4}`

No subtree has diameter `3`.

---

## Example 2

```text
Input:
n = 2
edges = [[1,2]]

Output:
[1]
```

---

## Example 3

```text
Input:
n = 3
edges = [[1,2],[2,3]]

Output:
[2,1]
```

---

## Constraints

```text
2 <= n <= 15
```

This is the biggest clue.

Because `n` is at most `15`, we should strongly suspect:

- subset enumeration
- bitmask DP
- Floyd-Warshall on `n <= 15`
- BFS/DFS over subsets

Since:

```text
2^15 = 32768
```

enumerating all subsets is feasible.

---

# 1. Core Insight

A subtree in this problem is exactly a **connected induced subgraph** of the tree.

So the problem becomes:

> Enumerate every non-empty subset of nodes with size at least 2, check if it is connected, compute its diameter, and increment the corresponding answer bucket.

The only challenge is doing that efficiently.

Because `n <= 15`, the brute-force-over-subsets route is not only feasible — it is the intended direction.

---

# 2. What Makes a Subset a Valid Subtree?

Let a subset contain `k` cities.

In a tree, an induced subgraph on those cities is connected **if and only if** the number of edges inside the subset is exactly:

```text
k - 1
```

Why?

Because any connected graph on `k` nodes with no cycles has `k - 1` edges, and every induced subgraph of a tree is acyclic.

So for a subset:

- count number of chosen vertices = `nodes`
- count number of original edges with both endpoints chosen = `edgesInside`

Then:

```text
subset is connected iff edgesInside == nodes - 1
```

This is a very powerful simplification.

---

# 3. Diameter in a Tree Subset

Once a subset is connected, its diameter is the largest distance between any pair of chosen nodes, measured along the unique tree path.

There are several ways to compute this:

1. Precompute all-pairs shortest distances once for the original tree, then for every connected subset scan all chosen pairs.
2. For each subset, do two BFS/DFS passes restricted to the subset.
3. DP on subsets / rerooting-style ideas.

Because `n <= 15`, the simplest and best approach is often:

> Precompute all distances once, then evaluate subsets.

---

# 4. Approach 1 — Subset Enumeration + Edge Count + Precomputed Distances

## Idea

1. Precompute all-pairs distances between cities.
2. Enumerate every subset `mask` from `1` to `(1 << n) - 1`.
3. Skip subsets with fewer than 2 nodes.
4. Count how many original edges are fully inside the subset.
5. If that count equals `nodes - 1`, then the subset is connected.
6. Compute the diameter by scanning all pairs of nodes inside the subset.
7. Increment `ans[diameter - 1]`.

This is probably the cleanest solution.

---

## Step A: Precompute Distances

Since the input is a tree and `n <= 15`, we can build an `n x n` distance matrix.

Two common ways:

- BFS from every node: `O(n * (n + m))`
- Floyd-Warshall: `O(n^3)`

For `n = 15`, both are trivial.

Floyd-Warshall is especially simple in Java here.

---

## Step B: Enumerate Subsets

For each subset mask:

- `Integer.bitCount(mask)` gives number of nodes.
- To count internal edges, iterate through original edges and check whether both endpoints are in the mask.

If connected, compute diameter:

```text
max(dist[i][j]) over all i, j in subset
```

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] countSubgraphsForEachDiameter(int n, int[][] edges) {
        int[][] dist = new int[n][n];
        int INF = 1_000_000_000;

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        for (int[] e : edges) {
            int u = e[0] - 1;
            int v = e[1] - 1;
            dist[u][v] = 1;
            dist[v][u] = 1;
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                }
            }
        }

        int[] ans = new int[n - 1];

        for (int mask = 1; mask < (1 << n); mask++) {
            int nodes = Integer.bitCount(mask);
            if (nodes < 2) continue;

            int edgeCount = 0;
            for (int[] e : edges) {
                int u = e[0] - 1;
                int v = e[1] - 1;
                if (((mask >> u) & 1) == 1 && ((mask >> v) & 1) == 1) {
                    edgeCount++;
                }
            }

            if (edgeCount != nodes - 1) continue;

            int diameter = 0;
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 0) continue;
                for (int j = i + 1; j < n; j++) {
                    if (((mask >> j) & 1) == 1) {
                        diameter = Math.max(diameter, dist[i][j]);
                    }
                }
            }

            ans[diameter - 1]++;
        }

        return ans;
    }
}
```

---

## Complexity Analysis

There are `2^n` subsets.

For each subset:

- counting internal edges takes `O(n)`
  because tree has `n - 1` edges,
- scanning all pairs takes `O(n^2)`.

Distance preprocessing with Floyd-Warshall takes:

```text
O(n^3)
```

Overall:

```text
O(n^3 + 2^n * n^2)
```

Since `n <= 15`, this is easily fast enough.

Space:

```text
O(n^2)
```

for the distance matrix.

---

# 5. Why Approach 1 Works

The correctness rests on two facts:

### Fact 1

A connected induced subgraph of a tree on `k` vertices has exactly `k - 1` internal edges.

### Fact 2

If a subset is connected, then its diameter is exactly the largest pairwise shortest-path distance among chosen vertices.

Because the original graph is a tree, shortest paths are unique and well-defined.

Thus enumerating all connected subsets and bucketing by diameter is exactly what the problem asks.

---

# 6. Approach 2 — Subset Enumeration + Connectivity DFS + Diameter via Double BFS

## Idea

Instead of using the edge-count trick plus precomputed distances, we can:

1. enumerate subsets,
2. explicitly test connectivity using DFS restricted to the subset,
3. compute the diameter of the subset using the standard tree-diameter trick:
   - BFS/DFS from any chosen node to find farthest node `a`
   - BFS/DFS from `a` to find farthest distance `diameter`

Because the induced subgraph is a tree if connected, double BFS works.

This approach is more “graph algorithm flavored” and avoids Floyd-Warshall.

---

## Connectivity Check

Pick any node inside the subset and run DFS only through nodes that are also in the subset.

If the number of visited nodes equals the subset size, then it is connected.

---

## Diameter Computation

On a tree, the diameter can be found by:

1. start from arbitrary node `x`
2. find farthest node `a`
3. start from `a`
4. farthest distance found is the diameter

This remains correct for the connected induced subtree.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] graph;

    public int[] countSubgraphsForEachDiameter(int n, int[][] edges) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] e : edges) {
            int u = e[0] - 1;
            int v = e[1] - 1;
            graph[u].add(v);
            graph[v].add(u);
        }

        int[] ans = new int[n - 1];

        for (int mask = 1; mask < (1 << n); mask++) {
            int nodes = Integer.bitCount(mask);
            if (nodes < 2) continue;

            int start = -1;
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    start = i;
                    break;
                }
            }

            int[] res1 = bfsWithinSubset(start, mask, n);
            int farthest = res1[0];
            int visitedCount = res1[1];

            if (visitedCount != nodes) continue;

            int[] res2 = bfsWithinSubset(farthest, mask, n);
            int diameter = res2[2];

            ans[diameter - 1]++;
        }

        return ans;
    }

    // returns {farthestNode, visitedCount, maxDistance}
    private int[] bfsWithinSubset(int src, int mask, int n) {
        Queue<Integer> queue = new ArrayDeque<>();
        boolean[] seen = new boolean[n];
        int[] dist = new int[n];
        Arrays.fill(dist, -1);

        queue.offer(src);
        seen[src] = true;
        dist[src] = 0;

        int visitedCount = 0;
        int farthest = src;
        int maxDist = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            visitedCount++;

            if (dist[u] > maxDist) {
                maxDist = dist[u];
                farthest = u;
            }

            for (int v : graph[u]) {
                if (((mask >> v) & 1) == 0 || seen[v]) continue;
                seen[v] = true;
                dist[v] = dist[u] + 1;
                queue.offer(v);
            }
        }

        return new int[]{farthest, visitedCount, maxDist};
    }
}
```

---

## Complexity

There are `2^n` subsets.

For each subset, we do a constant number of BFS traversals over at most `n` nodes and `n - 1` edges.

So the complexity is roughly:

```text
O(2^n * n)
```

or more conservatively:

```text
O(2^n * (n + m)) = O(2^n * n)
```

for a tree.

Space:

```text
O(n)
```

per BFS, plus adjacency list storage.

This is also fully acceptable.

---

# 7. Comparison of Approaches 1 and 2

## Approach 1

- uses precomputed all-pairs distances
- uses edge-count formula to detect connectivity
- very compact
- likely simplest overall

## Approach 2

- uses explicit graph traversals per subset
- conceptually closer to tree algorithms
- avoids Floyd-Warshall
- also elegant and fast

In interviews, Approach 1 is often the cleanest because the edge-count characterization is powerful and shortens the solution.

---

# 8. Approach 3 — DP over Subsets? Why It Is Usually Not Worth It

Because `n <= 15`, someone may wonder whether there is a fancy subset DP that avoids scanning all pairs.

In practice, this is usually unnecessary.

You could attempt:

- DP to determine whether subset is connected,
- DP to track diameter,
- merge smaller connected subsets,

but the bookkeeping becomes significantly more complex and does not materially improve over the direct subset-enumeration solutions.

For this problem, the intended efficient solution is almost certainly one of:

- subset enumeration + precomputed distances
- subset enumeration + BFS/DFS

These are both simpler and already optimal enough for the given limits.

---

# 9. Important Proof: Why Edge Count Detects Connectivity

Suppose a subset of nodes of size `k` is chosen from a tree.

The induced subgraph is always acyclic, because any subgraph of a tree is acyclic.

Now:

- if the induced subgraph is connected, then being a tree on `k` vertices, it must have exactly `k - 1` edges.
- if the induced subgraph has exactly `k - 1` edges and is acyclic, then it must be connected.

Since acyclicity is automatic here, checking:

```text
edgesInside == nodes - 1
```

is equivalent to checking connectivity.

That is why Approach 1 is so clean.

---

# 10. Worked Example

Take:

```text
n = 4
edges = [[1,2],[2,3],[2,4]]
```

Consider subset:

```text
{1,2,3}
```

Bitmask representation (0-based nodes 0,1,2):

```text
0111
```

Number of chosen nodes:

```text
3
```

Internal edges:

- (1,2) inside
- (2,3) inside
- (2,4) not fully inside

So internal edge count:

```text
2
```

Since:

```text
2 = 3 - 1
```

the subset is connected.

Diameter:

- dist(1,2)=1
- dist(2,3)=1
- dist(1,3)=2

So diameter = 2.

Thus increment `ans[1]`.

---

# 11. Approach 4 — DFS Restricted to Subset + Pairwise DFS Distances

This is another valid but less elegant option.

For each subset:

1. test connectivity by DFS restricted to the subset
2. for each chosen node, run DFS restricted to subset to compute maximum distance
3. take the largest among them

This is clearly correct, but slower than necessary because it recomputes too much.

Still, with `n <= 15`, it can pass if implemented carefully.

It is useful mainly as a conceptual stepping stone, not the best final answer.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] graph;
    private int n;

    public int[] countSubgraphsForEachDiameter(int n, int[][] edges) {
        this.n = n;
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] e : edges) {
            int u = e[0] - 1;
            int v = e[1] - 1;
            graph[u].add(v);
            graph[v].add(u);
        }

        int[] ans = new int[n - 1];

        for (int mask = 1; mask < (1 << n); mask++) {
            int nodes = Integer.bitCount(mask);
            if (nodes < 2) continue;

            int start = -1;
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    start = i;
                    break;
                }
            }

            boolean[] seen = new boolean[n];
            int visited = dfsCount(start, mask, seen);

            if (visited != nodes) continue;

            int diameter = 0;
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    diameter = Math.max(diameter, dfsMaxDist(i, -1, mask));
                }
            }

            ans[diameter - 1]++;
        }

        return ans;
    }

    private int dfsCount(int u, int mask, boolean[] seen) {
        seen[u] = true;
        int count = 1;

        for (int v : graph[u]) {
            if (((mask >> v) & 1) == 0 || seen[v]) continue;
            count += dfsCount(v, mask, seen);
        }

        return count;
    }

    private int dfsMaxDist(int u, int parent, int mask) {
        int best = 0;
        for (int v : graph[u]) {
            if (v == parent || ((mask >> v) & 1) == 0) continue;
            best = Math.max(best, 1 + dfsMaxDist(v, u, mask));
        }
        return best;
    }
}
```

Note that this code computes the maximum downward distance from each node, which is **not enough by itself** to compute the full diameter in all trees unless used carefully with rerooting or pair exploration. So this particular pattern is best treated as a learning sketch, not the recommended implementation.

That is exactly why Approaches 1 and 2 are preferable.

---

# 12. Recommended Approach

The best practical solution is:

## Subset Enumeration + Edge Count + Floyd-Warshall Distances

because it gives:

- simple connectivity test
- simple diameter computation
- very compact code
- guaranteed correctness
- excellent performance for `n <= 15`

---

# 13. Final Complexity Summary

## Approach 1

Subset enumeration + edge count + all-pairs distances

```text
Time:  O(n^3 + 2^n * n^2)
Space: O(n^2)
```

## Approach 2

Subset enumeration + BFS for connectivity and diameter

```text
Time:  O(2^n * n)
Space: O(n)
```

In practice, both pass comfortably.

---

# 14. Interview Summary

This problem is mostly about recognizing that:

- `n <= 15`
- so all subsets are enumerable
- each valid subtree is just a connected subset
- once connected, we only need its diameter

The cleanest line of attack is:

1. enumerate subsets
2. test whether the subset is connected
3. compute its diameter
4. count by diameter

The nicest trick is the connectivity criterion:

```text
For a subset of a tree:
connected iff internalEdges = nodes - 1
```

That removes the need for a BFS just to test connectivity.

Once distances are precomputed, the rest is straightforward.

---

# 15. Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int[] countSubgraphsForEachDiameter(int n, int[][] edges) {
        int[][] dist = new int[n][n];
        int INF = 1_000_000_000;

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        for (int[] e : edges) {
            int u = e[0] - 1;
            int v = e[1] - 1;
            dist[u][v] = 1;
            dist[v][u] = 1;
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                }
            }
        }

        int[] ans = new int[n - 1];

        for (int mask = 1; mask < (1 << n); mask++) {
            int nodes = Integer.bitCount(mask);
            if (nodes < 2) continue;

            int edgeCount = 0;
            for (int[] e : edges) {
                int u = e[0] - 1;
                int v = e[1] - 1;
                if (((mask >> u) & 1) == 1 && ((mask >> v) & 1) == 1) {
                    edgeCount++;
                }
            }

            if (edgeCount != nodes - 1) continue;

            int diameter = 0;
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 0) continue;
                for (int j = i + 1; j < n; j++) {
                    if (((mask >> j) & 1) == 1) {
                        diameter = Math.max(diameter, dist[i][j]);
                    }
                }
            }

            ans[diameter - 1]++;
        }

        return ans;
    }
}
```
