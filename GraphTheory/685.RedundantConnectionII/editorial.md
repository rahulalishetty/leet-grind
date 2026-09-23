# 685. Redundant Connection II — Three Java Approaches

## 1. Understanding the Problem

A valid rooted tree with `n` nodes has exactly `n - 1` edges, one root with **in-degree 0**,
and every other node with **in-degree exactly 1**. Here we are given `n` edges for `n` nodes
— **one edge too many** — and that single extra edge can break the tree in one of three ways:

| Case                             | What happens                                                                                                                       | Example                           |
| -------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- | --------------------------------- |
| **A. Pure cycle**                | Every node still has in-degree ≤ 1, but the edges form a cycle (no node ends up with in-degree 0).                                 | `[[1,2],[2,3],[3,4],[4,1],[1,5]]` |
| **B. Two parents only**          | One node gets in-degree 2, but if you remove the _later_ of those two edges, no cycle remains.                                     | `[[1,2],[1,3],[2,3]]`             |
| **C. Two parents _and_ a cycle** | One node gets in-degree 2, **and** removing the later edge alone still leaves a cycle (the cycle goes through the _earlier_ edge). | `[[2,1],[3,1],[4,2],[1,4]]`       |

The crux of every efficient solution is:

1. Detect whether some node has two incoming edges → call them `candidateA` (earlier) and `candidateB` (later).
2. Detect whether a cycle exists, **pretending `candidateB` was never added**.
3. Decide:
   - No two-parent node → the answer is whatever edge closes the cycle (must be reported as the one appearing **last** in the input).
   - Two-parent node, no cycle after removing `candidateB` → answer is `candidateB`.
   - Two-parent node, cycle still exists after removing `candidateB` → answer is `candidateA`.

Below are three ways to implement this idea, from simplest/slowest to most elegant.

---

## 2. Approach 1 — Brute Force (Try Removing Each Edge)

### Idea

Since `n ≤ 1000`, we can afford to literally try removing each edge (scanning **from the end**,
so the first removal that works is automatically "the last valid answer" required by the problem),
and check whether the remaining `n - 1` edges form a valid rooted tree.

A set of `n-1` edges is a valid rooted tree **iff**:

- No node receives more than one incoming edge (in-degree ≤ 1 for all), **and**
- The edges contain no cycle (checked with Union-Find).

### Algorithm

1. For `i` from `n-1` down to `0`:
   - Build the graph using all edges except `edges[i]`.
   - Track in-degree; if any node exceeds 1, this removal fails.
   - Union-Find all edges; if any union finds `u` and `v` already connected, there's a cycle — this removal fails.
   - If neither failure occurred, `edges[i]` is the answer.

### Java Code

```java
class Solution {
    public int[] findRedundantDirectedConnection(int[][] edges) {
        int n = edges.length;
        // Scan from the end so the first success is the answer that appears last in the input.
        for (int i = n - 1; i >= 0; i--) {
            if (isValidTree(edges, n, i)) {
                return edges[i];
            }
        }
        return new int[0]; // unreachable for valid input
    }

    private boolean isValidTree(int[][] edges, int n, int skipIndex) {
        int[] indegree = new int[n + 1];

        for (int i = 0; i < n; i++) {
            if (i == skipIndex) continue;
            int v = edges[i][1];
            indegree[v]++;
            if (indegree[v] > 1) return false; // a node would have two parents
        }

        int[] uf = new int[n + 1];
        for (int i = 1; i <= n; i++) uf[i] = i;

        int edgeCount = 0;
        for (int i = 0; i < n; i++) {
            if (i == skipIndex) continue;
            edgeCount++;
            int u = edges[i][0], v = edges[i][1];
            int ru = find(uf, u), rv = find(uf, v);
            if (ru == rv) return false; // adding this edge would close a cycle
            uf[rv] = ru;
        }

        return edgeCount == n - 1;
    }

    private int find(int[] uf, int x) {
        while (uf[x] != x) {
            uf[x] = uf[uf[x]]; // path compression
            x = uf[x];
        }
        return x;
    }
}
```

### Complexity

- **Time:** `O(n)` candidate removals × `O(n)` validation each ⇒ **`O(n²)`**.
- **Space:** `O(n)` for the in-degree and union-find arrays.

### Why it works (dry run, Example 1)

`edges = [[1,2],[1,3],[2,3]]`, start at `i = 2` (skip `[2,3]`):
remaining edges `[1,2],[1,3]` → in-degrees `{2:1, 3:1}` (fine), Union-Find never finds an existing
connection → valid tree. So we immediately return `[2,3]` ✅, matching the expected output.

This approach is simple to reason about but doesn't scale as elegantly — it's a good baseline
before moving to the optimized solutions.

---

## 3. Approach 2 — Single-Pass Union-Find (Optimal)

### Idea

Walk through the edges once, maintaining:

- `parentOf[v]`: the first edge that gave node `v` a parent (0 = none yet).
- A Union-Find structure over tree edges only.

If we encounter an edge into a node that **already** has a parent, we've found our two
candidates (`candidateA`, `candidateB`) — and we simply **skip adding this edge** to Union-Find,
continuing to process the rest of the edges as if `candidateB` doesn't exist.
If Union-Find ever reports `u` and `v` already in the same set, that edge closes a cycle —
remember it as `cycleEdge`.

### Algorithm

1. For each edge `(u, v)`:
   - If `v` already has a parent → record `candidateA = (parentOf[v], v)`, `candidateB = (u, v)`, and **skip** the union step for this edge.
   - Else → set `parentOf[v] = u`, then `union(u, v)`. If `find(u) == find(v)` already, record `cycleEdge = (u, v)` instead of unioning.
2. Decide:
   - `candidateA == null` → return `cycleEdge` (Case A: pure cycle).
   - `candidateA != null`, `cycleEdge == null` → return `candidateB` (Case B).
   - `candidateA != null`, `cycleEdge != null` → return `candidateA` (Case C).

### Java Code

```java
class Solution {
    private int[] parent; // Union-Find parent array

    public int[] findRedundantDirectedConnection(int[][] edges) {
        int n = edges.length;
        int[] parentOf = new int[n + 1]; // tree-parent of each node (0 = none yet)
        parent = new int[n + 1];
        for (int i = 1; i <= n; i++) parent[i] = i;

        int[] candidateA = null; // first edge to the double-parent node
        int[] candidateB = null; // second edge to the double-parent node (appears later)
        int[] cycleEdge = null;  // an edge that closes a cycle

        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];

            if (parentOf[v] != 0) {
                // v already has a parent -> this is the duplicate-parent conflict
                candidateA = new int[]{parentOf[v], v};
                candidateB = new int[]{u, v};
                continue; // pretend this edge doesn't exist for cycle detection
            }

            parentOf[v] = u;

            int ru = find(u), rv = find(v);
            if (ru == rv) {
                cycleEdge = edge; // adding this edge would close a cycle
            } else {
                parent[rv] = ru;
            }
        }

        if (candidateA == null) {
            return cycleEdge; // Case A: pure cycle
        }
        if (cycleEdge == null) {
            return candidateB; // Case B: removing the later edge is enough
        }
        return candidateA; // Case C: must remove the earlier edge
    }

    private int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]]; // path compression
            x = parent[x];
        }
        return x;
    }
}
```

### Complexity

- **Time:** `O(n)` — a single pass, with near-`O(1)` amortized Union-Find operations (path compression).
- **Space:** `O(n)` for `parentOf` and the Union-Find array.

### Dry run — Example 2

`edges = [[1,2],[2,3],[3,4],[4,1],[1,5]]`

| edge    | parentOf updated? | union result                               |
| ------- | ----------------- | ------------------------------------------ |
| `[1,2]` | `parentOf[2]=1`   | union(1,2) ok                              |
| `[2,3]` | `parentOf[3]=2`   | union(1,3) ok                              |
| `[3,4]` | `parentOf[4]=3`   | union(1,4) ok                              |
| `[4,1]` | `parentOf[1]=4`   | `find(4)==find(1)` → **cycleEdge = [4,1]** |
| `[1,5]` | `parentOf[5]=1`   | union(1,5) ok                              |

No node ever had two parents (`candidateA` stays `null`), so we return `cycleEdge = [4,1]` ✅.

### Dry run — Example 1

`edges = [[1,2],[1,3],[2,3]]`

- `[1,2]` → `parentOf[2]=1`, union ok.
- `[1,3]` → `parentOf[3]=1`, union ok.
- `[2,3]` → `parentOf[3]` is already `1` → `candidateA=[1,3]`, `candidateB=[2,3]`, skipped.

`cycleEdge` is still `null`, so we return `candidateB = [2,3]` ✅.

---

## 4. Approach 3 — Parent-Pointer Cycle Tracing (No Union-Find)

### Idea

Some people find Union-Find's "set merging" a bit opaque for this problem. Here's an
equivalent approach that only uses plain arrays:

1. Build `parentOf[v]` exactly as in Approach 2 (the **first** edge into `v` wins; the
   second conflicting edge is recorded as `candidateA`/`candidateB` but never overwrites
   `parentOf`). This means `parentOf[]` _already_ represents "the graph with `candidateB`
   removed".
2. Because every node now has **at most one parent**, `parentOf[]` is a _functional graph_:
   starting at any node and repeatedly following `parentOf` either reaches the root
   (`parentOf == 0`) or loops back on itself (a cycle). Detect this with a simple
   visited/in-path marking — no Union-Find needed.
3. Apply the same case decision as before. In the "pure cycle" case, we additionally
   need _which_ edge in the cycle appears last in the original input — we get that by
   tracking, for each node, which edge index assigned its parent, then taking the max
   index among the cycle's nodes.

### Java Code

```java
import java.util.*;

class Solution {
    public int[] findRedundantDirectedConnection(int[][] edges) {
        int n = edges.length;
        int[] parentOf = new int[n + 1];      // parentOf[v] = u  <=>  edge u->v is v's recorded parent edge
        int[] parentEdgeIdx = new int[n + 1]; // index (in `edges`) of that parent edge
        Arrays.fill(parentEdgeIdx, -1);

        int[] candidateA = null; // first edge into the conflict node
        int[] candidateB = null; // second edge into the conflict node (appears later)

        for (int i = 0; i < n; i++) {
            int u = edges[i][0], v = edges[i][1];
            if (parentOf[v] != 0) {
                candidateA = edges[parentEdgeIdx[v]];
                candidateB = edges[i];
            } else {
                parentOf[v] = u;
                parentEdgeIdx[v] = i;
            }
        }

        // Detect a cycle in the graph defined by parentOf[] (candidateB is excluded by construction)
        boolean[] done = new boolean[n + 1];
        boolean[] inPath = new boolean[n + 1];
        int cycleNode = -1;

        for (int v = 1; v <= n && cycleNode == -1; v++) {
            if (done[v]) continue;
            List<Integer> path = new ArrayList<>();
            int cur = v;
            while (cur != 0 && !done[cur] && !inPath[cur]) {
                inPath[cur] = true;
                path.add(cur);
                cur = parentOf[cur];
            }
            if (cur != 0 && inPath[cur]) {
                cycleNode = cur; // cycle found
            }
            for (int node : path) {
                inPath[node] = false;
                done[node] = true;
            }
        }

        boolean hasCycle = cycleNode != -1;

        if (candidateA == null) {
            // No duplicate-parent conflict: a cycle must exist among the n edges.
            // Collect its nodes, then return whichever of its edges appears last in the input.
            Set<Integer> cycleNodes = new HashSet<>();
            int cur = cycleNode;
            do {
                cycleNodes.add(cur);
                cur = parentOf[cur];
            } while (cur != cycleNode);

            int bestIdx = -1;
            for (int node : cycleNodes) {
                bestIdx = Math.max(bestIdx, parentEdgeIdx[node]);
            }
            return edges[bestIdx];
        }

        // Duplicate-parent conflict exists
        return hasCycle ? candidateA : candidateB;
    }
}
```

### Complexity

- **Time:** `O(n)` overall. Each node is visited by at most one outer-loop traversal because of
  the `done[]` marking (classic amortized argument, same flavor as path compression).
- **Space:** `O(n)` for `parentOf`, `parentEdgeIdx`, `done`, `inPath`.

### Dry run — Example 2

`edges = [[1,2],[2,3],[3,4],[4,1],[1,5]]`
`parentOf = {1:4, 2:1, 3:2, 4:3, 5:1}` (no conflict, `candidateA` stays `null`).

Tracing from node `1`: `1 → 4 → 3 → 2 → 1` — we land back on a node already in our current
path (`1`), so `cycleNode = 1`. Walking the cycle: `{1, 4, 3, 2}` with parent-edge indices
`{1:3, 4:2, 3:1, 2:0}`. The maximum index is `3`, i.e. `edges[3] = [4,1]` ✅.

### Dry run — Example 1

`edges = [[1,2],[1,3],[2,3]]`
After processing: `parentOf = {2:1, 3:1}`, and at `i=2` we hit the conflict:
`candidateA = [1,3]`, `candidateB = [2,3]`.

Tracing `parentOf` (`1` has no parent, `2→1`, `3→1`) never loops — no cycle found.
So `hasCycle = false`, and we return `candidateB = [2,3]` ✅.

---

## 5. Comparing the Three Approaches

| Approach                      | Time               | Space  | Key idea                                                                                  | Best for                                                                   |
| ----------------------------- | ------------------ | ------ | ----------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| **1. Brute Force**            | `O(n²)`            | `O(n)` | Try removing each edge, validate the rest with Union-Find                                 | Quick to write, easiest to convince yourself it's correct                  |
| **2. Single-Pass Union-Find** | `O(n)` (amortized) | `O(n)` | Classify the conflict while building Union-Find sets in one pass                          | The standard, most efficient interview-ready solution                      |
| **3. Parent-Pointer Tracing** | `O(n)` (amortized) | `O(n)` | Same case logic, but cycle detection via plain parent-array tracing instead of Union-Find | Avoids needing a Union-Find class; arguably easier to explain step by step |

All three return the same result for every input, since they're built around the same
three-case classification (pure cycle / two-parents-only / two-parents-with-cycle) — they
just differ in _how_ they detect the cycle and how aggressively they avoid recomputation.

For `n ≤ 1000` as given in the constraints, even the brute-force solution runs comfortably
fast; for larger `n`, Approach 2 or 3 is strongly preferred.
