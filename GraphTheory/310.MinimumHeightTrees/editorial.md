# 310. Minimum Height Trees — Java Solutions

## Problem Summary

Given an undirected tree of `n` nodes, find all nodes that, when chosen as root, produce the **minimum possible tree height**.

> Key insight: The answer always contains **at most 2 nodes** — the "center(s)" of the longest path in the tree.

---

## Approach 1: Topological Sort (Leaf Trimming) ✅ Optimal

### Intuition

Think of peeling an onion from the outside in. Leaves (nodes with degree 1) are always the worst roots — they're farthest from the center. We repeatedly remove the current leaf layer and expose new leaves, until only 1 or 2 nodes remain. Those are the centers — the roots giving minimum height.

### Why at most 2 centers?

In any tree, the "center" lies on the **longest path (diameter)**. A path has either 1 middle node (odd length) or 2 middle nodes (even length). So there are always ≤ 2 MHT roots.

### Algorithm

1. Build an adjacency list and track each node's degree.
2. Initialize a queue with all leaf nodes (degree = 1). Handle `n = 1` separately.
3. Repeatedly trim leaves layer by layer, decrementing neighbors' degrees.
4. When a neighbor's degree drops to 1, it becomes the new leaf — add to queue.
5. Stop when `remainingNodes <= 2`. The nodes still in the queue are the answer.

```java
import java.util.*;

class Solution {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        // Edge case: single node
        if (n == 1) return Collections.singletonList(0);

        // Step 1: Build adjacency list and degree array
        List<Set<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new HashSet<>());

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // Step 2: Initialize queue with all leaves (degree == 1)
        Queue<Integer> leaves = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (adj.get(i).size() == 1) leaves.offer(i);
        }

        // Step 3: Trim leaves layer by layer
        int remainingNodes = n;
        while (remainingNodes > 2) {
            int leafCount = leaves.size();
            remainingNodes -= leafCount;

            for (int i = 0; i < leafCount; i++) {
                int leaf = leaves.poll();
                // The leaf has exactly one neighbor
                int neighbor = adj.get(leaf).iterator().next();
                adj.get(neighbor).remove(leaf); // Remove the trimmed leaf

                if (adj.get(neighbor).size() == 1) {
                    leaves.offer(neighbor); // New leaf exposed
                }
            }
        }

        // Remaining nodes in queue are the MHT roots
        return new ArrayList<>(leaves);
    }
}
```

### Complexity

|           | Value                                |
| --------- | ------------------------------------ |
| **Time**  | O(n) — each node/edge processed once |
| **Space** | O(n) — adjacency list + queue        |

---

## Approach 2: BFS from All Nodes (Brute Force)

### Intuition

For each node, run a BFS to compute the height of the tree rooted at that node. Track the minimum height and collect all nodes achieving it.

This is the most straightforward interpretation of the problem, but is too slow for large inputs.

### Algorithm

1. Build an adjacency list.
2. For every node `i`, run BFS and compute the maximum depth (= height).
3. Track the global minimum height.
4. Collect all nodes whose BFS height equals the minimum.

```java
import java.util.*;

class Solution {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if (n == 1) return Collections.singletonList(0);

        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        int minHeight = Integer.MAX_VALUE;
        int[] heights = new int[n];

        // BFS from each node to compute its height as root
        for (int start = 0; start < n; start++) {
            heights[start] = bfsHeight(start, adj, n);
            minHeight = Math.min(minHeight, heights[start]);
        }

        // Collect all roots with minimum height
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (heights[i] == minHeight) result.add(i);
        }
        return result;
    }

    private int bfsHeight(int start, List<List<Integer>> adj, int n) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited[start] = true;
        int height = -1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            height++;
            for (int i = 0; i < size; i++) {
                int node = queue.poll();
                for (int neighbor : adj.get(node)) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.offer(neighbor);
                    }
                }
            }
        }
        return height;
    }
}
```

### Complexity

|           | Value                                     |
| --------- | ----------------------------------------- |
| **Time**  | O(n²) — BFS is O(n) and we run it n times |
| **Space** | O(n)                                      |

> ⚠️ **Too slow** for `n = 20,000`. Included for conceptual understanding only.

---

## Approach 3: Tree Diameter (Two-Pass BFS)

### Intuition

The MHT roots are always the **midpoint(s) of the tree's diameter** (longest path). We can find the diameter efficiently with two BFS passes:

1. **BFS from any node** → find the farthest node `u`.
2. **BFS from `u`** → find the farthest node `v` from `u`. The path `u → v` is the diameter.
3. **Walk to the middle** of this path — that gives 1 or 2 center nodes.

### Algorithm

```java
import java.util.*;

class Solution {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if (n == 1) return Collections.singletonList(0);
        if (n == 2) return Arrays.asList(0, 1);

        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // Pass 1: BFS from node 0, find farthest node u
        int[] result1 = bfsFarthest(0, adj, n);
        int u = result1[0];

        // Pass 2: BFS from u, find farthest node v and record parent path
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        int[] result2 = bfsFarthestWithParent(u, adj, n, parent);
        int v = result2[0];
        int diameter = result2[1];

        // Reconstruct the diameter path from v back to u
        List<Integer> path = new ArrayList<>();
        int cur = v;
        while (cur != -1) {
            path.add(cur);
            cur = parent[cur];
        }

        // The center(s) are the middle node(s) of the diameter path
        List<Integer> centers = new ArrayList<>();
        int len = path.size();
        if (len % 2 == 1) {
            centers.add(path.get(len / 2));
        } else {
            centers.add(path.get(len / 2 - 1));
            centers.add(path.get(len / 2));
        }
        return centers;
    }

    // BFS returning [farthestNode, distance]
    private int[] bfsFarthest(int start, List<List<Integer>> adj, int n) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited[start] = true;
        int farthest = start, dist = 0, curDist = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            curDist++;
            for (int i = 0; i < size; i++) {
                int node = queue.poll();
                for (int nb : adj.get(node)) {
                    if (!visited[nb]) {
                        visited[nb] = true;
                        queue.offer(nb);
                        if (curDist > dist) { dist = curDist; farthest = nb; }
                    }
                }
            }
        }
        return new int[]{farthest, dist};
    }

    // BFS with parent tracking
    private int[] bfsFarthestWithParent(int start, List<List<Integer>> adj, int n, int[] parent) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited[start] = true;
        parent[start] = -1;
        int farthest = start, dist = 0, curDist = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            curDist++;
            for (int i = 0; i < size; i++) {
                int node = queue.poll();
                for (int nb : adj.get(node)) {
                    if (!visited[nb]) {
                        visited[nb] = true;
                        parent[nb] = node;
                        queue.offer(nb);
                        if (curDist > dist) { dist = curDist; farthest = nb; }
                    }
                }
            }
        }
        return new int[]{farthest, dist};
    }
}
```

### Complexity

|           | Value                 |
| --------- | --------------------- |
| **Time**  | O(n) — two BFS passes |
| **Space** | O(n)                  |

---

## Approach 4: DFS with Re-rooting (DP on Trees)

### Intuition

For each node, the height as root = max depth among all its subtrees. We can compute this efficiently using **re-rooting DP**:

- **Downward pass (DFS 1):** Compute `down[v]` = max depth going _into_ the subtree rooted at `v`.
- **Upward pass (DFS 2):** Compute `up[v]` = max depth going _up_ through parent toward all other branches.
- **Height of `v` as root** = `max(down[v], up[v])`.

The upward value uses the parent's top-2 downward depths (to handle the case where the best downward path from parent goes through `v` itself).

```java
import java.util.*;

class Solution {
    private List<List<Integer>> adj;
    private int[] down;  // max depth going down into subtree
    private int[] down2; // second-best depth going down (different child)
    private int[] downChild; // which child gave the best down depth
    private int[] up;    // max depth going up through parent
    private int n;

    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        this.n = n;
        if (n == 1) return Collections.singletonList(0);

        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        down = new int[n];
        down2 = new int[n];
        downChild = new int[n];
        up = new int[n];
        Arrays.fill(downChild, -1);

        // Pass 1: compute down[] from root=0
        dfsDown(0, -1);

        // Pass 2: compute up[] propagating from root=0 downward
        dfsUp(0, -1);

        // Find minimum height and collect results
        int minH = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            minH = Math.min(minH, Math.max(down[i], up[i]));
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (Math.max(down[i], up[i]) == minH) result.add(i);
        }
        return result;
    }

    // Compute max depth going into subtree rooted at node
    private int dfsDown(int node, int parent) {
        int best = -1, second = -1, bestChild = -1;
        for (int nb : adj.get(node)) {
            if (nb == parent) continue;
            int d = dfsDown(nb, node) + 1;
            if (d > best) {
                second = best;
                best = d;
                bestChild = nb;
            } else if (d > second) {
                second = d;
            }
        }
        down[node] = Math.max(best, 0);
        down2[node] = Math.max(second, 0);
        downChild[node] = bestChild;
        return down[node];
    }

    // Propagate upward depth from parent to children
    private void dfsUp(int node, int parent) {
        for (int nb : adj.get(node)) {
            if (nb == parent) continue;
            // If nb is the child that gave node's best down depth,
            // use node's second-best down depth instead (avoid going through nb)
            int parentDown = (downChild[node] == nb) ? down2[node] : down[node];
            up[nb] = Math.max(up[node], parentDown) + 1;
            dfsUp(nb, node);
        }
    }
}
```

### Complexity

|           | Value                 |
| --------- | --------------------- |
| **Time**  | O(n) — two DFS passes |
| **Space** | O(n)                  |

---

## Comparison of All Approaches

| Approach                  | Time  | Space | Notes                               |
| ------------------------- | ----- | ----- | ----------------------------------- |
| **BFS Leaf Trimming**     | O(n)  | O(n)  | ✅ Simplest & fastest — recommended |
| **Brute Force BFS**       | O(n²) | O(n)  | ❌ TLE on large inputs              |
| **Tree Diameter (2-BFS)** | O(n)  | O(n)  | ✅ Elegant, geometric insight       |
| **Re-rooting DP (DFS)**   | O(n)  | O(n)  | ✅ Generalizes to other DP problems |

---

## Visual Walkthrough — Leaf Trimming on Example 2

```
Input: n=6, edges=[[3,0],[3,1],[3,2],[3,4],[5,4]]

Initial tree:       Degrees:
  0                 0:1  1:1  2:1
  |                 3:4  4:2  5:1
  3 — 1
  |\
  | 2
  4
  |
  5

Round 1 — trim leaves {0, 1, 2, 5}:
  remainingNodes = 6 - 4 = 2
  After trimming, degrees become: 3:1, 4:1

remainingNodes = 2 → STOP
Answer: [3, 4]  ✅
```

---

## Key Takeaways

- The MHT root(s) are always the **topological center** of the tree — at most 2 nodes.
- **Leaf trimming** is the most intuitive O(n) solution and the recommended approach.
- **Two-pass BFS** frames it as a geometry problem: find the diameter, take its midpoint.
- **Re-rooting DP** is the most powerful pattern, applicable to any per-node tree aggregation problem.
