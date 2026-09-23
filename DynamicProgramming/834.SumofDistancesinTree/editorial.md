# 834. Sum of Distances in Tree

## Problem Recap

Given an undirected connected tree with `n` nodes (`0` to `n-1`) and `n-1` edges, return an array `answer` where `answer[i]` is the sum of distances from node `i` to every other node in the tree.

---

## Approach 1: Brute Force (BFS/DFS from every node)

**Idea:** For each node, run a BFS (or DFS) to compute distances to all other nodes and sum them up.

**Why it works:** This is the most direct translation of the problem statement — literally compute `dist(i, j)` for every pair.

**Complexity:**

- Time: `O(n^2)` — one `O(n)` traversal per node.
- Space: `O(n)` for the adjacency list and BFS queue/visited array.
- This is **too slow** for `n` up to `3 * 10^4` (worst case ~9\*10^8 operations), but it's a great starting point and works fine for small inputs.

```java
import java.util.*;

class SolutionBruteForce {
    public int[] sumOfDistancesInTree(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int[] e : edges) {
            graph.get(e[0]).add(e[1]);
            graph.get(e[1]).add(e[0]);
        }

        int[] answer = new int[n];
        for (int start = 0; start < n; start++) {
            answer[start] = bfsSumOfDistances(start, n, graph);
        }
        return answer;
    }

    private int bfsSumOfDistances(int start, int n, List<List<Integer>> graph) {
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        dist[start] = 0;

        Deque<Integer> queue = new ArrayDeque<>();
        queue.offer(start);

        int total = 0;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int next : graph.get(node)) {
                if (dist[next] == -1) {
                    dist[next] = dist[node] + 1;
                    total += dist[next];
                    queue.offer(next);
                }
            }
        }
        return total;
    }
}
```

---

## Approach 2: Optimal — Reroot Technique (Two-Pass DFS)

**Idea (subtree sizes + rerooting):**

This is the classic "tree DP with rerooting" trick. It works in two phases:

**Phase 1 — Post-order DFS (rooted at node 0):**
For every node `u`, compute:

- `count[u]` = number of nodes in the subtree rooted at `u` (including `u`).
- `answer[0]` accumulated as: `answer[0] += ` the sum of distances from the root (node 0) to every node, computed as depth-weighted subtree sizes.

Concretely, while doing the post-order traversal, for each child `v` of `u`:

```
count[u] += count[v]
answer[u] += answer[v] + count[v]
```

This works because every node in `v`'s subtree is `count[v]` nodes, and each of those nodes is exactly 1 edge farther from `u` than from `v`. So `answer[v] + count[v]` converts "distances from v" into "distances from u" for that subtree.

After phase 1, `answer[0]` is correct (distances from the root to everyone), and `count[u]` tells us the subtree size of every node.

**Phase 2 — Pre-order DFS (reroot from parent to child):**
Once we know `answer[u]` (distances from `u` to all nodes), we can derive `answer[v]` for a child `v` of `u` in O(1) using this insight:

- Moving the "center" from `u` to `v`:
  - Every node in `v`'s subtree (`count[v]` of them) gets **1 closer**.
  - Every other node (`n - count[v]` of them) gets **1 farther**.

So:

```
answer[v] = answer[u] - count[v] + (n - count[v])
```

This lets us compute `answer[]` for the entire tree in **two linear passes**.

**Complexity:**

- Time: `O(n)` — two DFS traversals, each visiting every node/edge once.
- Space: `O(n)` for adjacency list, recursion stack (converted to explicit stack below to avoid Java's default recursion depth issues on skewed trees), `count[]`, and `answer[]`.

```java
import java.util.*;

class Solution {
    public int[] sumOfDistancesInTree(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int[] e : edges) {
            graph.get(e[0]).add(e[1]);
            graph.get(e[1]).add(e[0]);
        }

        int[] count = new int[n];   // subtree sizes
        int[] answer = new int[n];  // final answer
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        // ---------- Phase 1: post-order (iterative) ----------
        // Build a traversal order (parent before children) using BFS/DFS,
        // then process it in reverse to simulate post-order.
        int[] order = new int[n];
        int idx = 0;
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        visited[0] = true;
        while (!stack.isEmpty()) {
            int node = stack.pop();
            order[idx++] = node;
            for (int next : graph.get(node)) {
                if (!visited[next]) {
                    visited[next] = true;
                    parent[next] = node;
                    stack.push(next);
                }
            }
        }

        Arrays.fill(count, 1); // each node counts itself
        // Process in reverse traversal order => children before parents (post-order)
        for (int i = n - 1; i >= 0; i--) {
            int node = order[i];
            int p = parent[node];
            if (p != -1) {
                count[p] += count[node];
                answer[p] += answer[node] + count[node];
            }
        }

        // ---------- Phase 2: pre-order (reroot) ----------
        // Process in original traversal order => parents before children
        for (int i = 1; i < n; i++) {
            int node = order[i];
            int p = parent[node];
            answer[node] = answer[p] - count[node] + (n - count[node]);
        }

        return answer;
    }
}
```

### Walkthrough on Example 1

`n = 6, edges = [[0,1],[0,2],[2,3],[2,4],[2,5]]`

Tree (rooted at 0):

```
        0
       / \
      1   2
         /|\
        3 4 5
```

**Phase 1 (post-order, leaves → root):**

- `count[1]=1, count[3]=1, count[4]=1, count[5]=1`
- `count[2] = 1 + count[3] + count[4] + count[5] = 4`, `answer[2] = 0+1 + 0+1 + 0+1 = 3`
- `count[0] = 1 + count[1] + count[2] = 6`
  `answer[0] = (answer[1]+count[1]) + (answer[2]+count[2]) = (0+1) + (3+4) = 8` ✅ matches expected `answer[0] = 8`

**Phase 2 (reroot, root → leaves):**

- `answer[1] = answer[0] - count[1] + (n - count[1]) = 8 - 1 + 5 = 12` ✅
- `answer[2] = answer[0] - count[2] + (n - count[2]) = 8 - 4 + 2 = 6` ✅
- `answer[3] = answer[2] - count[3] + (n - count[3]) = 6 - 1 + 5 = 10` ✅
- `answer[4] = 6 - 1 + 5 = 10` ✅
- `answer[5] = 6 - 1 + 5 = 10` ✅

Final: `[8, 12, 6, 10, 10, 10]` — matches the expected output.

---

## Why the Iterative (Stack-Based) Traversal Instead of Recursion?

With `n` up to `3 * 10^4`, a skewed tree (essentially a linked list) could cause a plain recursive DFS to hit **stack overflow** in Java (default thread stack depth is often a few thousand to ~10⁴ frames depending on JVM settings). Using an explicit stack for traversal order, then processing that order forwards/backwards, avoids this risk entirely while keeping the algorithm `O(n)`.

---

## Complexity Comparison

| Approach                      | Time     | Space  | Notes                                    |
| ----------------------------- | -------- | ------ | ---------------------------------------- |
| Brute Force (BFS per node)    | `O(n^2)` | `O(n)` | Simple, but TLE for large `n`            |
| Reroot Technique (2-pass DFS) | `O(n)`   | `O(n)` | Optimal; required for `n` up to `3*10^4` |

---

## Edge Cases Handled

- `n = 1`, `edges = []` → returns `[0]` (single node, no traversal needed, loop bodies simply don't execute).
- `n = 2` → works correctly since phase 1 computes `answer[0]` directly and phase 2 reroots to node 1.
- Skewed/linear trees (path graphs) → handled safely via iterative traversal instead of recursion.
