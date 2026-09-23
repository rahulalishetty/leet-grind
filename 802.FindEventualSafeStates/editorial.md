# 802. Find Eventual Safe States — Java Solutions

## Problem Recap

A node is **safe** if every path starting from it eventually reaches a terminal node
(a node with no outgoing edges). Equivalently: a node is **unsafe** if it lies on a
cycle, or it has a path leading into a cycle. So the safe nodes are exactly the nodes
that are **not** part of, and cannot reach, any cycle.

This gives us two natural angles of attack:

1. Detect cycles directly with DFS coloring (3-color / white-gray-black).
2. Reverse the graph and repeatedly strip away terminal nodes (Kahn's / topological
   sort style) — a node is safe iff it eventually becomes "terminal" in this peeling
   process.

---

## Solution 1: DFS with 3-Color Cycle Detection (Recommended)

### Idea

Think of each node as being in one of three states while we DFS:

- **WHITE (0)** — unvisited.
- **GRAY (1)** — currently on the recursion stack (being explored).
- **BLACK (2)** — fully explored, and confirmed **safe**.

If a DFS from node `u` ever reaches a **GRAY** node, that means we've found a back
edge — a cycle. Every node on that cycle (and everything that can reach it) is
**unsafe**.

A node is safe if and only if DFS from it never revisits a GRAY node, i.e., all of
its outgoing paths terminate cleanly. We memoize with the color array so every node
is processed only once (amortized O(1) revisits).

### Java Code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    private static final int WHITE = 0; // unvisited
    private static final int GRAY  = 1; // in progress (on current DFS path)
    private static final int BLACK = 2; // fully processed -> safe

    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n]; // defaults to WHITE (0)
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (isSafe(i, graph, color)) {
                result.add(i);
            }
        }
        return result;
    }

    private boolean isSafe(int node, int[][] graph, int[] color) {
        if (color[node] != WHITE) {
            // GRAY  -> currently being visited => part of a cycle => unsafe
            // BLACK -> already proven safe
            return color[node] == BLACK;
        }

        color[node] = GRAY; // mark as "in progress"

        for (int next : graph[node]) {
            if (!isSafe(next, graph, color)) {
                return false; // a neighbor leads into a cycle => this node unsafe too
            }
        }

        color[node] = BLACK; // no cycle found from this node => safe
        return true;
    }
}
```

### Complexity

- **Time:** `O(V + E)` — each node and edge is visited a constant number of times
  thanks to memoization via `color[]`.
- **Space:** `O(V)` for the color array and recursion stack (plus `O(V)` for output).

---

## Solution 2: Reverse Graph + BFS Topological Peeling (Kahn's Algorithm Style)

### Idea

A terminal node (out-degree 0) is trivially safe. A node is safe if **all** of its
outgoing edges point to already-confirmed-safe nodes. This is exactly the logic of
Kahn's algorithm, but run on the **reversed** graph, peeling from terminal nodes
inward:

1. Build the reverse graph: `revGraph[v]` contains all `u` such that `u -> v` in the
   original graph.
2. Compute `outDegree[u]` for every node in the **original** graph.
3. Push all nodes with `outDegree == 0` (terminal nodes) into a queue — they are safe.
4. Pop a safe node `v`. For every predecessor `u` in `revGraph[v]` (i.e., edges
   `u -> v` in the original graph), decrement `outDegree[u]`. If it drops to `0`,
   all of `u`'s outgoing paths have been proven safe, so `u` is safe — push it.
5. Whatever never gets pushed is stuck in a cycle (or feeds one) — unsafe.

### Java Code

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

class Solution {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        List<List<Integer>> revGraph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            revGraph.add(new ArrayList<>());
        }

        int[] outDegree = new int[n];
        for (int u = 0; u < n; u++) {
            outDegree[u] = graph[u].length;
            for (int v : graph[u]) {
                revGraph.get(v).add(u); // edge v <- u in reverse graph
            }
        }

        boolean[] safe = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();

        // Step 1: all terminal nodes are safe
        for (int i = 0; i < n; i++) {
            if (outDegree[i] == 0) {
                queue.offer(i);
            }
        }

        // Step 2: peel nodes whose outgoing edges all lead to safe nodes
        while (!queue.isEmpty()) {
            int v = queue.poll();
            safe[v] = true;
            for (int u : revGraph.get(v)) {
                outDegree[u]--;
                if (outDegree[u] == 0) {
                    queue.offer(u);
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (safe[i]) {
                result.add(i);
            }
        }
        Collections.sort(result); // already ascending since we iterate 0..n-1, kept for clarity
        return result;
    }
}
```

### Complexity

- **Time:** `O(V + E)` — building the reverse graph is `O(E)`, and each node/edge is
  processed once during the BFS peel.
- **Space:** `O(V + E)` for the reverse adjacency list, plus `O(V)` for the queue and
  auxiliary arrays.

---

## Solution 3: Iterative DFS (No Recursion) with Explicit Stack

### Idea

Same 3-color cycle detection as Solution 1, but implemented iteratively to avoid
recursion — useful when `n` is large (up to `10^4`) and you want to avoid the risk of
a stack overflow from deep recursion in the worst case (e.g., a long chain).

We simulate the call stack manually, tracking which neighbor index we're currently
processing for each node on the stack.

### Java Code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    private static final int WHITE = 0;
    private static final int GRAY  = 1;
    private static final int BLACK = 2;

    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n];
        List<Integer> result = new ArrayList<>();

        for (int start = 0; start < n; start++) {
            if (color[start] != WHITE) continue;
            dfsIterative(start, graph, color);
        }

        for (int i = 0; i < n; i++) {
            if (color[i] == BLACK) result.add(i);
        }
        return result;
    }

    private void dfsIterative(int start, int[][] graph, int[] color) {
        // Stack holds nodes; nextIndex tracks how far we've iterated each node's neighbors
        int[] nodeStack = new int[graph.length + 1];
        int[] nextIndex = new int[graph.length + 1];
        int top = 0;

        nodeStack[top] = start;
        color[start] = GRAY;

        while (top >= 0) {
            int node = nodeStack[top];

            if (nextIndex[top] < graph[node].length) {
                int next = graph[node][nextIndex[top]++];

                if (color[next] == WHITE) {
                    color[next] = GRAY;
                    top++;
                    nodeStack[top] = next;
                    // nextIndex[top] is already 0 by default
                } else if (color[next] == GRAY) {
                    // Cycle detected: everything currently GRAY on the stack
                    // stays unsafe (never promoted to BLACK). We simply stop
                    // expanding further down this path.
                    // (No action needed: node will just never turn BLACK.)
                }
                // if BLACK, it's already proven safe -> nothing to do
            } else {
                // All neighbors processed with no unresolved cycle through this node
                color[node] = BLACK;
                top--;
            }
        }
    }
}
```

**Note:** In this iterative version, if a cycle is detected, the nodes on that cycle
(and their ancestors up to the point of detection) simply never get promoted to
`BLACK`, so they're correctly excluded from the final `result` list.

### Complexity

- **Time:** `O(V + E)` — identical work to the recursive version, just without
  call-stack overhead.
- **Space:** `O(V)` for the explicit stack arrays and color array.

---

## Comparison

| Approach                              | Time     | Space    | Notes                                               |
| ------------------------------------- | -------- | -------- | --------------------------------------------------- |
| 1. Recursive DFS (3-color)            | O(V + E) | O(V)     | Cleanest, most intuitive; recursion depth up to `n` |
| 2. Reverse graph + BFS (Kahn's style) | O(V + E) | O(V + E) | No recursion; naturally avoids stack depth issues   |
| 3. Iterative DFS (explicit stack)     | O(V + E) | O(V)     | Same logic as (1) but recursion-safe for large `n`  |

All three run in linear time relative to the graph size and correctly handle
self-loops (a self-loop `i -> i` immediately marks `i` as GRAY-revisited, i.e.,
unsafe).

For most interview/practical settings, **Solution 1** (recursive DFS with coloring)
is the go-to for its clarity, while **Solution 2** is a great alternative when you
want to avoid any recursion entirely and think in terms of topological peeling.
