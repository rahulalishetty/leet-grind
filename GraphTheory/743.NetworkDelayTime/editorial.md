# 743. Network Delay Time — Multiple Approaches in Java

## Problem Summary

We have `n` nodes (labeled `1..n`) and a list of directed weighted edges `times[i] = (ui, vi, wi)`. A signal starts at node `k`. We need the time for the signal to reach **every** node — i.e., the maximum over all nodes of the shortest distance from `k`. If any node is unreachable, return `-1`.

This is fundamentally a **single-source shortest path** problem. Below are four classic ways to solve it, each with different trade-offs.

---

## Approach 1: Dijkstra's Algorithm (Priority Queue) — Recommended

### Idea

Since all edge weights `wi >= 0`, Dijkstra's algorithm is the natural fit. We greedily expand the closest unvisited node using a min-heap, finalizing its shortest distance, then relax its outgoing edges.

### Java Code

```java
import java.util.*;

class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list: node -> list of [neighbor, weight]
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] time : times) {
            graph.computeIfAbsent(time[0], x -> new ArrayList<>())
                 .add(new int[]{time[1], time[2]});
        }

        // Min-heap ordered by accumulated distance
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{k, 0});

        Map<Integer, Integer> dist = new HashMap<>(); // finalized shortest distances

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0], time = curr[1];

            if (dist.containsKey(node)) continue; // already finalized with a smaller time
            dist.put(node, time);

            if (graph.containsKey(node)) {
                for (int[] neighbor : graph.get(node)) {
                    int nextNode = neighbor[0], weight = neighbor[1];
                    if (!dist.containsKey(nextNode)) {
                        pq.offer(new int[]{nextNode, time + weight});
                    }
                }
            }
        }

        if (dist.size() != n) return -1; // not all nodes reachable

        int maxTime = 0;
        for (int time : dist.values()) {
            maxTime = Math.max(maxTime, time);
        }
        return maxTime;
    }
}
```

### Why It Works

- We always pop the node with the **smallest tentative distance** from the heap. Once popped, that distance is guaranteed final (no negative edges can ever produce a shorter path later).
- "Lazy deletion": instead of decreasing keys in the heap, we just push duplicates and skip stale entries via the `dist.containsKey(node)` check.

### Complexity

- **Time:** `O(E log E)` — each edge can be pushed onto the heap once, and heap operations cost `log` of heap size.
- **Space:** `O(V + E)` for the graph and heap.

### Dry Run (Example 1)

`times = [[2,1,1],[2,3,1],[3,4,1]]`, `n = 4`, `k = 2`

- Start: `pq = [(2,0)]`
- Pop `(2,0)` → `dist={2:0}` → push `(1,1)`, `(3,1)`
- Pop `(1,1)` → `dist={2:0,1:1}` (node 1 has no outgoing edges)
- Pop `(3,1)` → `dist={2:0,1:1,3:1}` → push `(4,2)`
- Pop `(4,2)` → `dist={2:0,1:1,3:1,4:2}`
- All 4 nodes reached, max = **2** ✅

---

## Approach 2: Bellman-Ford Algorithm

### Idea

Relax **all edges**, `n - 1` times. After `i` rounds, the shortest path using at most `i` edges is guaranteed correct. Since the longest possible simple path has at most `n - 1` edges, `n - 1` rounds suffice. This approach also naturally handles negative weights (not needed here, but it's a generalization).

### Java Code

```java
import java.util.*;

class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;
            for (int[] time : times) {
                int u = time[0], v = time[1], w = time[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    updated = true;
                }
            }
            if (!updated) break; // converged early, no point continuing
        }

        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxTime = Math.max(maxTime, dist[i]);
        }
        return maxTime;
    }
}
```

### Why It Works

- Each full pass over all edges can only "settle" paths that are exactly one edge longer than what's already settled.
- After at most `n - 1` passes, every shortest path (which has at most `n - 1` edges in a graph with `n` nodes and no negative cycles) is found.

### Complexity

- **Time:** `O(V * E)` — `n - 1` rounds, each scanning all edges.
- **Space:** `O(V)` for the distance array.

### When to Prefer This

Useful if edge weights could be negative (Dijkstra would break), or when the graph is simple to represent as an edge list without building adjacency structures.

---

## Approach 3: Floyd-Warshall Algorithm (All-Pairs Shortest Path)

### Idea

Compute shortest paths between **every pair** of nodes using dynamic programming: try every node `mid` as an intermediate hop between every pair `(i, j)`. This is overkill for a single-source problem but illustrates the all-pairs technique, and is useful if you need repeated queries from different sources.

### Java Code

```java
import java.util.*;

class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        final int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        for (int[] time : times) {
            dist[time[0]][time[1]] = time[2];
        }

        // Try every node as an intermediate point
        for (int mid = 1; mid <= n; mid++) {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (dist[i][mid] + dist[mid][j] < dist[i][j]) {
                        dist[i][j] = dist[i][mid] + dist[mid][j];
                    }
                }
            }
        }

        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[k][i] >= INF) return -1;
            maxTime = Math.max(maxTime, dist[k][i]);
        }
        return maxTime;
    }
}
```

### Why It Works

The DP invariant: after the loop with `mid` fixed at value `m`, `dist[i][j]` holds the shortest path from `i` to `j` using only nodes `1..m` as intermediates. By the time `mid` has cycled through all `n` nodes, every `dist[i][j]` is the true shortest path.

### Complexity

- **Time:** `O(V^3)` — three nested loops over all nodes.
- **Space:** `O(V^2)` for the distance matrix.

### Trade-off

Much slower than Dijkstra/Bellman-Ford for a _single_ source query, but it computes shortest paths between **all** pairs in one pass — valuable if you needed the answer for multiple different starting nodes `k`.

---

## Approach 4: SPFA (Shortest Path Faster Algorithm — Queue-based Bellman-Ford)

### Idea

A common optimization of Bellman-Ford: instead of blindly relaxing _all_ edges in every round, only re-process nodes whose distance was just improved, using a queue (similar in spirit to BFS).

### Java Code

```java
import java.util.*;

class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] time : times) {
            graph.computeIfAbsent(time[0], x -> new ArrayList<>())
                 .add(new int[]{time[1], time[2]});
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(k);
        boolean[] inQueue = new boolean[n + 1];
        inQueue[k] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            inQueue[node] = false;

            if (!graph.containsKey(node)) continue;

            for (int[] neighbor : graph.get(node)) {
                int nextNode = neighbor[0], weight = neighbor[1];
                if (dist[node] + weight < dist[nextNode]) {
                    dist[nextNode] = dist[node] + weight;
                    if (!inQueue[nextNode]) {
                        queue.offer(nextNode);
                        inQueue[nextNode] = true;
                    }
                }
            }
        }

        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxTime = Math.max(maxTime, dist[i]);
        }
        return maxTime;
    }
}
```

### Why It Works

A node only needs to be re-relaxed if its own distance changed. By tracking which nodes are "active" (in the queue), we skip a lot of useless work that plain Bellman-Ford does by re-scanning all edges every round.

### Complexity

- **Time:** Average case is much faster than `O(V * E)`, though worst case is still `O(V * E)` (e.g., adversarial graphs that mimic Bellman-Ford's worst case).
- **Space:** `O(V + E)`.

---

## Comparison Table

| Approach        | Time Complexity                  | Space Complexity | Handles Negative Weights? | Best Use Case                                                  |
| --------------- | -------------------------------- | ---------------- | ------------------------- | -------------------------------------------------------------- |
| Dijkstra (heap) | `O(E log E)`                     | `O(V + E)`       | No                        | Single source, non-negative weights (this problem's ideal fit) |
| Bellman-Ford    | `O(V * E)`                       | `O(V)`           | Yes                       | Simple to implement, works with negative weights               |
| Floyd-Warshall  | `O(V^3)`                         | `O(V^2)`         | Yes (no negative cycles)  | Need shortest paths between **all** pairs of nodes             |
| SPFA            | `O(V * E)` worst, faster average | `O(V + E)`       | Yes                       | Sparse graphs, practical speedup over Bellman-Ford             |

## Recommendation

For this specific problem (non-negative weights, single source `k`), **Dijkstra's algorithm with a priority queue** is the most efficient and idiomatic solution. The other approaches are included for completeness and to illustrate how the same problem generalizes to negative weights (Bellman-Ford/SPFA) or all-pairs queries (Floyd-Warshall).
