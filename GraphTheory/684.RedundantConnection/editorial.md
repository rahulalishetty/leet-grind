# 684. Redundant Connection

## Problem Restatement

We start with a tree of `n` nodes (labeled `1..n`) and add **one extra edge**, creating exactly one cycle. We must find and return that extra edge. If several edges could be removed to restore a valid tree, we return the one that appears **last** in the input array.

**Key insight:** Since the graph starts as a valid tree and exactly one extra edge is added, the redundant edge is always the **first edge encountered (scanning left to right)** that connects two nodes which are _already_ connected to each other through some other path. Because of this, simply scanning edges in order and stopping at the first "already-connected" edge automatically gives us the _last valid answer_ — no extra bookkeeping needed.

---

## Approach 1: Union-Find (Disjoint Set Union) with Path Compression + Union by Rank — Optimal

### Idea

Process edges one at a time, maintaining a Disjoint Set Union (DSU) structure of connected components.

- For edge `(a, b)`: find the root of `a` and root of `b`.
  - If the roots are **different**, the nodes weren't connected yet — union them (this edge belongs to the underlying tree).
  - If the roots are the **same**, `a` and `b` are already connected through earlier edges — adding this edge creates the cycle, so **this is our answer**.

Because we scan left-to-right and return immediately upon detecting the cycle, we naturally return the **last** edge that would complete a cycle (the only edge that does, since there's only one cycle in this graph).

### Why Path Compression + Union by Rank?

- **Path compression**: when finding a root, we flatten the tree so future lookups are faster.
- **Union by rank**: we always attach the shorter tree under the taller one, keeping trees flat.
- Together, these give amortized **O(α(n))** (effectively constant) time per `find`/`union` operation, where α is the inverse Ackermann function.

### Java Code

```java
class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        int[] rank = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i; // each node starts as its own root
        }

        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            if (!union(parent, rank, a, b)) {
                // a and b were already connected -> this edge is redundant
                return edge;
            }
        }

        return new int[0]; // unreachable given problem constraints
    }

    // Find root of x with path compression
    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    // Union by rank. Returns false if x and y were already in the same set.
    private boolean union(int[] parent, int[] rank, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);

        if (rootX == rootY) {
            return false; // already connected -> would form a cycle
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }
}
```

### Complexity

- **Time:** O(n · α(n)) ≈ O(n) for `n` edges.
- **Space:** O(n) for `parent` and `rank` arrays.

---

## Approach 2: DFS-Based Cycle Detection (Build Graph Incrementally)

### Idea

Build an adjacency-list graph edge by edge. Before actually adding edge `(a, b)` to the graph, run a DFS from `a` to see if `b` is **already reachable**.

- If `b` is reachable from `a` _before_ adding this edge, then this edge closes a cycle — return it.
- Otherwise, add the edge to the graph and continue.

This directly simulates "is there already a path between these two nodes?" without an explicit Union-Find structure.

### Java Code

```java
import java.util.*;

class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            boolean[] visited = new boolean[n + 1];

            if (isConnected(graph, a, b, visited)) {
                // a path already exists between a and b -> redundant edge
                return edge;
            }

            // safe to add: no cycle formed
            graph.get(a).add(b);
            graph.get(b).add(a);
        }

        return new int[0]; // unreachable given problem constraints
    }

    // DFS to check if target is reachable from src in the current graph
    private boolean isConnected(List<List<Integer>> graph, int src, int target, boolean[] visited) {
        if (src == target) return true;
        visited[src] = true;

        for (int neighbor : graph.get(src)) {
            if (!visited[neighbor] && isConnected(graph, neighbor, target, visited)) {
                return true;
            }
        }
        return false;
    }
}
```

### Complexity

- **Time:** O(n²) worst case — for each of the `n` edges we may run a DFS that visits O(n) nodes/edges.
- **Space:** O(n) for the adjacency list and recursion stack/visited array.

### Trade-off

Conceptually simple and avoids implementing Union-Find, but noticeably slower on larger inputs since each edge insertion can require a full graph traversal.

---

## Approach 3: Naive Union-Find (No Path Compression, No Union by Rank)

### Idea

Same general strategy as Approach 1, but using a bare-bones DSU: `find` just follows `parent` pointers in a loop until it hits a root, and `union` blindly attaches one root under the other without balancing. This shows what happens _without_ the standard optimizations.

### Java Code

```java
class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }

        for (int[] edge : edges) {
            int rootA = find(parent, edge[0]);
            int rootB = find(parent, edge[1]);

            if (rootA == rootB) {
                return edge; // cycle detected -> redundant edge
            }

            parent[rootA] = rootB; // naive union, no rank balancing
        }

        return new int[0]; // unreachable given problem constraints
    }

    // Naive find: walk parent pointers until reaching the root
    private int find(int[] parent, int x) {
        while (parent[x] != x) {
            x = parent[x];
        }
        return x;
    }
}
```

### Complexity

- **Time:** O(n) per `find` in the worst case (if the parent chain degenerates into a long list), giving **O(n²)** overall in the worst case.
- **Space:** O(n).

### Trade-off

Much simpler to write and still passes for the given constraints (`n ≤ 1000`), but it doesn't scale as gracefully as Approach 1 because the union-find "forest" can become a long chain in adversarial inputs.

---

## Comparison Summary

| Approach                                | Time Complexity    | Space Complexity | Notes                                       |
| --------------------------------------- | ------------------ | ---------------- | ------------------------------------------- |
| 1. Union-Find (path compression + rank) | O(n · α(n)) ≈ O(n) | O(n)             | Optimal; recommended for large `n`          |
| 2. DFS-based cycle detection            | O(n²)              | O(n)             | Simple to reason about, but slower          |
| 3. Naive Union-Find (no optimizations)  | O(n²) worst case   | O(n)             | Easiest to write; fine for small/medium `n` |

## Walkthrough on Example 1

`edges = [[1,2],[1,3],[2,3]]`

- `(1,2)`: roots differ → union 1 and 2.
- `(1,3)`: roots differ → union {1,2} and 3.
- `(2,3)`: `find(2)` and `find(3)` give the same root → **cycle detected** → return `[2,3]`. ✅ matches expected output.

## Walkthrough on Example 2

`edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]`

- `(1,2)`: union 1,2.
- `(2,3)`: union {1,2},3.
- `(3,4)`: union {1,2,3},4.
- `(1,4)`: `find(1)` and `find(4)` are already the same root → **cycle detected** → return `[1,4]`. ✅ matches expected output.
- (Edge `[1,5]` is never even reached, since we return as soon as the first redundant edge is found.)
