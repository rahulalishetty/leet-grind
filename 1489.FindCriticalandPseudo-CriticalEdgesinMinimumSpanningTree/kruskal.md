# Critical vs Pseudo‑Critical Edges in Minimum Spanning Trees (MST) — Clear Summary + Code

This note summarizes the standard approach (Kruskal + Union‑Find) to find **critical** and **pseudo‑critical** edges in an undirected weighted graph.

---

## 1) Key concepts (plain language)

### MST weight is unique, MST structure may not be

A graph has a single **minimum possible total weight** for a spanning tree, but there may be multiple different MSTs that achieve that same minimum weight.

### Critical edge

An edge is **critical** if removing it makes the best possible MST **worse** (higher total weight) or impossible (graph becomes disconnected for MST construction).

**Equivalent statement:** the edge appears in **every** MST.

### Pseudo‑critical edge

An edge is **pseudo‑critical** if it can be part of **some** MST, but not necessarily all.

**Equivalent statement:** you can force‑include it and still achieve the same overall MST weight, but it isn’t mandatory.

---

## 2) Why Kruskal + Union‑Find is the natural tool

### Kruskal’s algorithm

1. Sort edges by weight ascending.
2. Add edges greedily if they connect two different components (don’t form a cycle).

### Union‑Find (DSU)

Union‑Find tracks which nodes belong to which components, supporting:

- `find(x)` → representative of x’s component
- `union(x, y)` → merges two components if different

With union by size/rank + path compression, each operation is “almost constant” time.

---

## 3) High-level algorithm

### Step A — Preprocess edges

Create a new array `newEdges` where each edge stores its original index:

`[u, v, w, originalIndex]`

Then sort by `w`.

### Step B — Compute the baseline MST weight

Run a normal Kruskal to compute:

- `stdWeight` = weight of a minimum spanning tree

### Step C — For each edge, test two scenarios

#### 1) Ignore the edge (critical test)

Compute MST weight when the edge is excluded:

- If MST becomes disconnected **or**
- MST weight becomes **larger** than `stdWeight`

→ this edge is **critical**.

#### 2) Force the edge (pseudo‑critical test)

Only if it is **not critical**:

Compute MST weight when the edge must be included:

- union(u, v) first, add its weight
- then run Kruskal over the rest (excluding the forced edge)

If resulting MST weight == `stdWeight`
→ edge is **pseudo‑critical**.

---

## 4) Java implementation (as-is style)

```java
class Solution {
    public List<List<Integer>> findCriticalAndPseudoCriticalEdges(int n, int[][] edges) {
        // Add index to edges for tracking
        int m = edges.length;
        int[][] newEdges = new int[m][4];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < 3; j++) {
                newEdges[i][j] = edges[i][j];
            }
            newEdges[i][3] = i; // original index
        }

        // Sort edges based on weight
        Arrays.sort(newEdges, Comparator.comparingInt(edge -> edge[2]));

        // Find standard MST weight using union-find
        UnionFind ufStd = new UnionFind(n);
        int stdWeight = 0;
        for (int[] edge : newEdges) {
            if (ufStd.union(edge[0], edge[1])) {
                stdWeight += edge[2];
            }
        }

        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // critical
        result.add(new ArrayList<>()); // pseudo-critical

        // Check each edge for critical and pseudo-critical
        for (int i = 0; i < m; i++) {

            // 1) Ignore this edge and calculate MST weight
            UnionFind ufIgnore = new UnionFind(n);
            int ignoreWeight = 0;
            for (int j = 0; j < m; j++) {
                if (i != j && ufIgnore.union(newEdges[j][0], newEdges[j][1])) {
                    ignoreWeight += newEdges[j][2];
                }
            }

            // If disconnected or heavier => critical
            if (ufIgnore.maxSize < n || ignoreWeight > stdWeight) {
                result.get(0).add(newEdges[i][3]);
            } else {
                // 2) Force this edge and calculate MST weight
                UnionFind ufForce = new UnionFind(n);

                ufForce.union(newEdges[i][0], newEdges[i][1]);
                int forceWeight = newEdges[i][2];

                for (int j = 0; j < m; j++) {
                    if (i != j && ufForce.union(newEdges[j][0], newEdges[j][1])) {
                        forceWeight += newEdges[j][2];
                    }
                }

                // If total weight same => pseudo-critical
                if (forceWeight == stdWeight) {
                    result.get(1).add(newEdges[i][3]);
                }
            }
        }

        return result;
    }

    class UnionFind {
        int[] parent;
        int[] size;
        int maxSize;

        public UnionFind(int n) {
            parent = new int[n];
            size = new int[n];
            maxSize = 1;
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            if (x != parent[x]) {
                parent[x] = find(parent[x]); // path compression
            }
            return parent[x];
        }

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                if (size[rootX] < size[rootY]) {
                    int temp = rootX;
                    rootX = rootY;
                    rootY = temp;
                }
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
                maxSize = Math.max(maxSize, size[rootX]);
                return true;
            }
            return false;
        }
    }
}
```

---

## 5) Complexity analysis (clear breakdown)

Let:

- **n** = number of nodes
- **m** = number of edges

### Sorting

Sorting edges once:

- **O(m log m)**

### Kruskal MST computation cost

A single Kruskal pass scans all edges and performs up to m Union‑Find operations.

With union by size/rank + path compression:

- each union/find is **amortized O(α(n))**
- so one Kruskal pass is **O(m · α(n))**

Here α(n) is the **inverse Ackermann function**, which grows so slowly that it is ≤ 4 or 5 for any practical input size.

### Repeating per edge

For each edge, we may do:

- one “ignore” MST build: **O(m · α(n))**
- and possibly one “force” MST build: **O(m · α(n))**

So per edge worst-case: **O(m · α(n))** (constant factor ~2)

Repeated for m edges:

- **O(m² · α(n))**

### Total time

- **O(m log m + m² · α(n))**
- Dominant term is **O(m² · α(n))**

### Space

- store edges: O(m)
- union-find arrays: O(n)

Total: **O(m + n)**, and since in a connected graph **m ≥ n−1**, this is typically reported as **O(m)**.

---

## 6) Practical notes / gotchas

- The “disconnected MST” check: here done via `maxSize < n`. Another common way is to count unions and confirm it equals `n-1`.
- This solution is feasible because constraints are small (commonly n ≤ 100), allowing O(m²) style repetition.
- If constraints were large (like 2e5 edges), you’d need a more advanced technique (group edges by weight, use bridges in Kruskal graph, etc.).

---
