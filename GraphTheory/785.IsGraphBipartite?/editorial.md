# 785. Is Graph Bipartite? — Java Solutions

## Problem Recap

Given an undirected graph as an adjacency list `graph`, determine if it is
**bipartite** — i.e., whether its nodes can be split into two groups `A` and
`B` such that every edge connects a node in `A` to a node in `B` (no edge
stays within the same group).

**Key insight:** A graph is bipartite **if and only if it can be properly
2-colored** — meaning we can color every node with one of two colors such
that no two adjacent nodes share a color. This is equivalent to saying the
graph contains **no odd-length cycle**.

The graph may be disconnected, so every connected component must be checked
independently.

We'll look at three approaches:

1. **BFS coloring** — level-by-level, iterative
2. **DFS coloring** — recursive
3. **Union-Find (Disjoint Set Union)** — group-based

All three run in **O(V + E)** time and **O(V)** space (V = nodes, E = edges).

---

## Approach 1: BFS Coloring

### Idea

Treat colors as `0` (uncolored), `1`, and `-1` (the two "sides"). For each
uncolored node, start a BFS, color it `1`, and color every neighbor the
_opposite_ color of the current node. If we ever find a neighbor that's
already colored the _same_ as the current node, the graph is **not**
bipartite.

### Code

```java
import java.util.*;

class Solution {
    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n]; // 0 = uncolored, 1 or -1 = the two sides

        for (int start = 0; start < n; start++) {
            if (color[start] != 0) continue; // already colored in a previous BFS

            // Start a new BFS for this connected component
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(start);
            color[start] = 1;

            while (!queue.isEmpty()) {
                int u = queue.poll();

                for (int v : graph[u]) {
                    if (color[v] == 0) {
                        // Not yet colored: assign the opposite color and enqueue
                        color[v] = -color[u];
                        queue.offer(v);
                    } else if (color[v] == color[u]) {
                        // Same color as neighbor => odd cycle => not bipartite
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
```

### Dry Run — Example 1 (`[[1,2,3],[0,2],[0,1,3],[0,2]]`)

- Start BFS at `0`, color it `1`.
- Neighbors of `0`: `1, 2, 3` → all colored `-1`.
- Process `1`: neighbor `2` is already colored `-1`, same as `1` → **conflict** → return `false`. ✅ matches expected output.

### Dry Run — Example 2 (`[[1,3],[0,2],[1,3],[0,2]]`)

- Start BFS at `0`, color it `1`.
- Neighbors `1, 3` → colored `-1`.
- Process `1`: neighbor `0` (color `1`, fine, different), neighbor `2` uncolored → colored `1`.
- Process `3`: neighbor `0` fine, neighbor `2` already `1`, and color of `3` is `-1` → different, fine.
- Process `2`: neighbors `1` (`-1`, fine), `3` (`-1`, fine).
- No conflicts → return `true`. ✅

### Complexity

- **Time:** O(V + E) — each node and edge is visited once.
- **Space:** O(V) — color array + BFS queue.

---

## Approach 2: DFS Coloring (Recursive)

### Idea

Same 2-coloring idea, but propagate colors via recursion instead of a queue.
For each uncolored node, recursively color its neighbors the opposite color.
If a neighbor is already colored and matches the current node's color, the
graph isn't bipartite.

### Code

```java
class Solution {
    private int[] color;
    private int[][] graph;

    public boolean isBipartite(int[][] graph) {
        this.graph = graph;
        int n = graph.length;
        color = new int[n]; // 0 = uncolored

        for (int i = 0; i < n; i++) {
            if (color[i] == 0) {
                if (!dfs(i, 1)) return false;
            }
        }
        return true;
    }

    // Try to color node `u` with `c`, then recursively color its neighbors
    private boolean dfs(int u, int c) {
        color[u] = c;

        for (int v : graph[u]) {
            if (color[v] == c) {
                // Neighbor has the same color => conflict
                return false;
            }
            if (color[v] == 0) {
                // Uncolored neighbor: color it the opposite and recurse
                if (!dfs(v, -c)) return false;
            }
        }
        return true;
    }
}
```

### Notes

- This is essentially the same algorithm as BFS but explores depth-first.
- Recursion depth can reach O(V) in the worst case (e.g., a long path or
  dense component), so for very large graphs an iterative DFS with an
  explicit stack may be preferable to avoid stack overflow. Given the
  constraint `n <= 100`, recursion is perfectly safe here.

### Complexity

- **Time:** O(V + E)
- **Space:** O(V) — color array + recursion stack.

---

## Approach 3: Union-Find (Disjoint Set Union)

### Idea

A neat reframing: for a node `u` with neighbors `graph[u]`, in a bipartite
graph **all of `u`'s neighbors must belong to the same set** (the opposite
set from `u`), since they're all only connected to `u`, not necessarily to
each other. So:

- Union all neighbors of `u` together.
- If `u` itself ends up in the _same_ set as one of its own neighbors, the
  graph can't be 2-colored — return `false`.

This avoids explicit coloring and instead directly encodes "must be on the
opposite side" via grouping "these nodes must be on the same side."

### Code

```java
class Solution {
    private int[] parent;
    private int[] rank_;

    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        parent = new int[n];
        rank_ = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        for (int u = 0; u < n; u++) {
            for (int v : graph[u]) {
                // If u and any of its neighbors are already unioned,
                // that means u ended up grouped with something it's
                // directly connected to => not bipartite.
                if (find(u) == find(v)) {
                    return false;
                }
            }
            // Union all neighbors of u together (they must all be on
            // the opposite side from u, hence the same side as each other)
            if (graph[u].length > 0) {
                int first = graph[u][0];
                for (int v : graph[u]) {
                    union(first, v);
                }
            }
        }

        return true;
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression
        }
        return parent[x];
    }

    private void union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return;
        // union by rank
        if (rank_[ra] < rank_[rb]) {
            parent[ra] = rb;
        } else if (rank_[ra] > rank_[rb]) {
            parent[rb] = ra;
        } else {
            parent[rb] = ra;
            rank_[ra]++;
        }
    }
}
```

### Dry Run — Example 1 (`[[1,2,3],[0,2],[0,1,3],[0,2]]`)

- `u = 0`, neighbors `1,2,3`: none unioned with `0` yet → fine. Union `1,2,3` together (all same set).
- `u = 1`, neighbors `0,2`: check `find(1) == find(0)`? No. Check `find(1) == find(2)`? **Yes** (unioned in the previous step) → return `false`. ✅

### Complexity

- **Time:** O(V + E · α(V)) ≈ O(V + E) with path compression + union by rank (α = inverse Ackermann, effectively constant).
- **Space:** O(V) — parent and rank arrays.

---

## Comparison of Approaches

| Approach     | Time        | Space | Style                         | Notes                                                                                         |
| ------------ | ----------- | ----- | ----------------------------- | --------------------------------------------------------------------------------------------- |
| BFS Coloring | O(V+E)      | O(V)  | Iterative, queue-based        | Intuitive; easy to reason about levels                                                        |
| DFS Coloring | O(V+E)      | O(V)  | Recursive                     | Concise; watch recursion depth on huge graphs                                                 |
| Union-Find   | O(V+E·α(V)) | O(V)  | Set-based, no explicit colors | Elegant alternative; useful if Union-Find is already being used elsewhere in a larger problem |

All three approaches are optimal in asymptotic complexity for this problem;
choice mostly comes down to style preference or what fits best with
surrounding code (e.g., if you're already maintaining a DSU structure for
other purposes, Approach 3 slots in naturally).

## Edge Cases Handled

- **Disconnected graph:** All approaches loop over every node as a potential
  BFS/DFS start or Union-Find seed, ensuring every component is checked.
- **Isolated node** (`graph[u]` is empty): Automatically bipartite on its
  own; no coloring conflict, no union to perform.
- **Single node graph** (`n == 1`): Trivially bipartite — loop runs once,
  no edges to conflict.
