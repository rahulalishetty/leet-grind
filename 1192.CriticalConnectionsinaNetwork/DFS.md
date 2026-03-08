# Critical Connections (Bridges) in an Undirected Graph — DFS “Rank” Approach (Summary + Code)

This note summarizes a bridge-finding solution using **DFS + ranks** (discovery levels) to detect **cycles**, and discarding edges that are part of any cycle. The remaining edges are the **bridges** (a.k.a. _critical connections_).

---

## 1) Problem restatement

You are given a **connected**, **undirected** graph.
A **bridge** (critical connection) is an edge whose removal makes the graph **disconnected**.

Goal:

- return all bridge edges.

---

## 2) Key observation: bridges are exactly edges not in any cycle

In an undirected graph:

> **An edge is a bridge ⇔ it is not part of any cycle.**

Reason:

- If an edge lies on a cycle, there is an alternative route between its endpoints, so removing it does not disconnect the graph.
- If an edge is not on any cycle, it is the only route connecting two regions, so removing it disconnects the graph.

So the task becomes:

- detect cycle membership of edges during DFS, and discard those edges.
- whatever remains are bridges.

---

## 3) DFS idea: “rank” (discovery depth) to detect back-edges

### Rank definition

- Pick any node as a DFS start (root); set its rank = **0**.
- For each DFS step to an unvisited neighbor, rank increases by 1.
- Unvisited nodes have rank = **null** (or any sentinel except values you use for logic).

Rank acts like “visited + depth”.

### Cycle detection using ranks

During DFS at node `u` with `discoveryRank`:

- If you see a neighbor `v` that has already been assigned a rank that is **≤ discoveryRank**, then you found a way “back” to an ancestor → **cycle exists**.
- The algorithm must ensure it ignores the **parent edge** (in undirected graphs parent appears as a neighbor).

---

## 4) Why returning “min rank reachable” matters

If a cycle is detected “down below”, you want ancestors to know it.

So DFS returns:

> the **minimum rank** reachable from the current node’s subtree (including back edges).

If a neighbor subtree can reach an ancestor of the current node, then the edge to that neighbor lies on a cycle and is **not** a bridge → discard it.

In this approach:

- When DFS returns a `recursiveRank` for neighbor `v`:
  - If `recursiveRank <= discoveryRank`, then `u—v` is part of a cycle → remove from candidate bridges.

---

## 5) Data structures used

1. **Adjacency list** graph: `Map<Integer, List<Integer>> graph`
2. **rank map**: `Map<Integer, Integer> rank`
3. **connDict** (edge set/dictionary): stores all edges initially, removes edges found to be in cycles.
   - This is crucial: removal in O(1) average time.

Edges are stored as ordered pairs `(min(u,v), max(u,v))` so `(u,v)` and `(v,u)` map to the same key.

---

## 6) Algorithm steps

### Build graph

- Initialize adjacency list.
- Add both directions for each edge (undirected).
- Add each edge into `connDict` using sorted endpoints.

### DFS

Call `dfs(startNode=0, discoveryRank=0)`:

Inside `dfs(node, discoveryRank)`:

1. If node already visited (rank not null), return its rank.
2. Assign rank[node] = discoveryRank.
3. Initialize `minRank = discoveryRank + 1`.
4. For each neighbor:
   - Skip the parent (detected via `neighRank == discoveryRank - 1`)
   - Recurse: `recursiveRank = dfs(neighbor, discoveryRank + 1)`
   - If `recursiveRank <= discoveryRank`, then edge `(node, neighbor)` is in a cycle → remove from `connDict`
   - Update `minRank = min(minRank, recursiveRank)`
5. Return `minRank`

### Output

After DFS, whatever edges remain in `connDict` are bridges.

---

## 7) Java code (as-is style)

```java
class Solution {

    private Map<Integer, List<Integer>> graph;
    private Map<Integer, Integer> rank;
    private Map<Pair<Integer, Integer>, Boolean> connDict;

    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {

        this.formGraph(n, connections);
        this.dfs(0, 0);

        List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (Pair<Integer, Integer> criticalConnection : this.connDict.keySet()) {
            result.add(new ArrayList<Integer>(
                Arrays.asList(criticalConnection.getKey(), criticalConnection.getValue())
            ));
        }

        return result;
    }

    private int dfs(int node, int discoveryRank) {

        // Already visited: return rank immediately
        if (this.rank.get(node) != null) {
            return this.rank.get(node);
        }

        // Mark visited with its discovery rank
        this.rank.put(node, discoveryRank);

        // Start minRank as "larger than current", not INF
        int minRank = discoveryRank + 1;

        for (Integer neighbor : this.graph.get(node)) {

            // Skip the parent edge in an undirected DFS tree
            Integer neighRank = this.rank.get(neighbor);
            if (neighRank != null && neighRank == discoveryRank - 1) {
                continue;
            }

            // Recurse
            int recursiveRank = this.dfs(neighbor, discoveryRank + 1);

            // If neighbor subtree can reach an ancestor (or current),
            // then node-neighbor edge lies on a cycle => not a bridge
            if (recursiveRank <= discoveryRank) {
                int sortedU = Math.min(node, neighbor), sortedV = Math.max(node, neighbor);
                this.connDict.remove(new Pair<Integer, Integer>(sortedU, sortedV));
            }

            // Track minimum reachable rank
            minRank = Math.min(minRank, recursiveRank);
        }

        return minRank;
    }

    private void formGraph(int n, List<List<Integer>> connections) {

        this.graph = new HashMap<Integer, List<Integer>>();
        this.rank = new HashMap<Integer, Integer>();
        this.connDict = new HashMap<Pair<Integer, Integer>, Boolean>();

        // Default rank for unvisited nodes is "null"
        for (int i = 0; i < n; i++) {
            this.graph.put(i, new ArrayList<Integer>());
            this.rank.put(i, null);
        }

        for (List<Integer> edge : connections) {

            // Bidirectional edges
            int u = edge.get(0), v = edge.get(1);
            this.graph.get(u).add(v);
            this.graph.get(v).add(u);

            // Store edge in canonical order
            int sortedU = Math.min(u, v), sortedV = Math.max(u, v);
            connDict.put(new Pair<Integer, Integer>(sortedU, sortedV), true);
        }
    }
}
```

> Note: In plain Java, you would need a `Pair` implementation (or use `long key = ((long)u<<32)|v` style) so that edges can be used as hash keys.

---

## 8) Complexity

Let:

- V = number of vertices
- E = number of edges

### Time: O(V + E)

- Each vertex is assigned a rank once.
- Each edge is processed a constant number of times via adjacency traversal.
- Removal from `connDict` is O(1) average (hash map).

Because the graph is connected, typically E ≥ V−1, so people often say time is dominated by E → **O(E)**.

### Space: O(V + E)

- adjacency list: O(V + E)
- rank: O(V)
- connDict: O(E)

Total: O(V + E), which is O(E) when E dominates.

---

## 9) Practical notes / gotchas

- The “skip parent” check using `neighRank == discoveryRank - 1` works only because the parent’s rank is exactly one less in the DFS tree. Another common approach is to pass the `parent` explicitly.
- This algorithm assumes the graph is connected because it starts DFS from node 0. If graph could be disconnected, you’d need to loop over all nodes and start DFS from each unvisited node.
- This method is conceptually close to Tarjan’s bridge algorithm; it’s essentially rediscovering the “low-link” idea using ranks.

---
