# 399. Evaluate Division — Multiple Java Approaches

## Problem Recap

Given equations like `a / b = 2.0` and `b / c = 3.0`, answer queries like `a / c = ?`. The natural model is a **weighted directed graph**: every equation `A / B = v` becomes two edges, `A → B` with weight `v` and `B → A` with weight `1/v`. Answering `C / D` is then "find the product of edge weights along a path from `C` to `D`."

Four approaches below solve this with different trade-offs: DFS, BFS, Weighted Union-Find, and Floyd-Warshall.

---

## Approach 1: DFS over the Graph

### Idea

Build an adjacency list where each edge stores the division ratio. For each query, run a DFS from `C` looking for `D`, multiplying edge weights as you go. If `D` is unreachable (or either variable is unknown), return `-1.0`.

### Java Code

```java
import java.util.*;

class SolutionDFS {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = buildGraph(equations, values);
        double[] results = new double[queries.size()];

        for (int i = 0; i < queries.size(); i++) {
            String src = queries.get(i).get(0);
            String dst = queries.get(i).get(1);

            if (!graph.containsKey(src) || !graph.containsKey(dst)) {
                results[i] = -1.0;
            } else if (src.equals(dst)) {
                results[i] = 1.0;
            } else {
                results[i] = dfs(graph, src, dst, new HashSet<>());
            }
        }
        return results;
    }

    private Map<String, Map<String, Double>> buildGraph(List<List<String>> equations, double[] values) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double val = values[i];
            graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, val);
            graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / val);
        }
        return graph;
    }

    private double dfs(Map<String, Map<String, Double>> graph, String src, String dst, Set<String> visited) {
        visited.add(src);
        for (Map.Entry<String, Double> nbr : graph.get(src).entrySet()) {
            String next = nbr.getKey();
            double weight = nbr.getValue();
            if (next.equals(dst)) return weight;
            if (!visited.contains(next)) {
                double sub = dfs(graph, next, dst, visited);
                if (sub != -1.0) return weight * sub;
            }
        }
        return -1.0;
    }
}
```

### Complexity

Time `O(Q · (V + E))` — each query may traverse the whole graph. Space `O(V + E)` for the graph plus `O(V)` recursion stack.

---

## Approach 2: BFS over the Graph

### Idea

Same graph, but traverse level by level from `C`, tracking the accumulated ratio to each visited node. Stop as soon as `D` is dequeued. BFS avoids recursion-depth issues on long chains and is easy to convert to an early-exit shortest-path style search.

### Java Code

```java
import java.util.*;

class SolutionBFS {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = buildGraph(equations, values);
        double[] results = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            results[i] = bfs(graph, queries.get(i).get(0), queries.get(i).get(1));
        }
        return results;
    }

    private Map<String, Map<String, Double>> buildGraph(List<List<String>> equations, double[] values) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double val = values[i];
            graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, val);
            graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / val);
        }
        return graph;
    }

    private double bfs(Map<String, Map<String, Double>> graph, String src, String dst) {
        if (!graph.containsKey(src) || !graph.containsKey(dst)) return -1.0;
        if (src.equals(dst)) return 1.0;

        Queue<String> queue = new LinkedList<>();
        Map<String, Double> ratios = new HashMap<>();
        queue.offer(src);
        ratios.put(src, 1.0);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            double currRatio = ratios.get(curr);
            if (curr.equals(dst)) return currRatio;

            for (Map.Entry<String, Double> nbr : graph.get(curr).entrySet()) {
                String next = nbr.getKey();
                if (!ratios.containsKey(next)) {
                    ratios.put(next, currRatio * nbr.getValue());
                    queue.offer(next);
                }
            }
        }
        return -1.0;
    }
}
```

### Complexity

Time `O(Q · (V + E))`, Space `O(V + E)`. Practically identical to DFS; choice is mostly stylistic, though BFS sidesteps stack-overflow risk on pathological chain graphs.

---

## Approach 3: Weighted Union-Find (Disjoint Set with Ratios)

### Idea

Each variable points to a parent, and `ratio[x]` stores `x / parent[x]`. `find(x)` does path compression while rolling up the ratio so that, after the call, `ratio[x]` is `x` divided by the _component root_. Two variables can answer a query directly once they share a root: `C / D = ratio[C] / ratio[D]`. This is the most scalable approach for many repeated queries because `find` is nearly `O(1)` amortized.

### Java Code

```java
import java.util.*;

class SolutionUnionFind {
    private Map<String, String> parent = new HashMap<>();
    private Map<String, Double> ratio = new HashMap<>(); // ratio[x] = x / parent[x]

    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        for (int i = 0; i < equations.size(); i++) {
            union(equations.get(i).get(0), equations.get(i).get(1), values[i]);
        }

        double[] results = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String c = queries.get(i).get(0);
            String d = queries.get(i).get(1);

            if (!parent.containsKey(c) || !parent.containsKey(d)) {
                results[i] = -1.0;
                continue;
            }
            String rootC = find(c);
            String rootD = find(d);
            results[i] = rootC.equals(rootD) ? ratio.get(c) / ratio.get(d) : -1.0;
        }
        return results;
    }

    private String find(String x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
            ratio.put(x, 1.0);
        }
        if (!parent.get(x).equals(x)) {
            String root = find(parent.get(x));
            ratio.put(x, ratio.get(x) * ratio.get(parent.get(x)));
            parent.put(x, root);
        }
        return parent.get(x);
    }

    private void union(String a, String b, double value) {
        String rootA = find(a);
        String rootB = find(b);
        if (rootA.equals(rootB)) return;

        // a/b = value, a/rootA = ratio[a], b/rootB = ratio[b]
        // => rootA/rootB = value * ratio[b] / ratio[a]
        parent.put(rootA, rootB);
        ratio.put(rootA, value * ratio.get(b) / ratio.get(a));
    }
}
```

### Complexity

Building the structure is `O(N · α(N))` for `N` equations; each query is `O(α(N))` after that, where `α` is the inverse Ackermann function (effectively constant). Space `O(V)`.

---

## Approach 4: Floyd-Warshall (Precompute All Pairs)

### Idea

Treat the variables as nodes `0..n-1` and build a dense `n × n` ratio matrix, initialized from the direct equations (`dist[i][i] = 1`, everything else `-1` meaning "unknown"). Run a multiplicative Floyd-Warshall: for every intermediate `k`, if `i → k` and `k → j` are both known, derive `i → j`. After `O(V³)` preprocessing, every query is an `O(1)` lookup — ideal when the variable count is small but the query count is large.

### Java Code

```java
import java.util.*;

class SolutionFloydWarshall {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Set<String> varSet = new HashSet<>();
        for (List<String> eq : equations) {
            varSet.add(eq.get(0));
            varSet.add(eq.get(1));
        }
        List<String> variables = new ArrayList<>(varSet);
        Map<String, Integer> indexOf = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) indexOf.put(variables.get(i), i);

        int n = variables.size();
        double[][] dist = new double[n][n];
        for (double[] row : dist) Arrays.fill(row, -1.0);
        for (int i = 0; i < n; i++) dist[i][i] = 1.0;

        for (int i = 0; i < equations.size(); i++) {
            int a = indexOf.get(equations.get(i).get(0));
            int b = indexOf.get(equations.get(i).get(1));
            dist[a][b] = values[i];
            dist[b][a] = 1.0 / values[i];
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == -1.0) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == -1.0) continue;
                    if (dist[i][j] == -1.0) {
                        dist[i][j] = dist[i][k] * dist[k][j];
                    }
                }
            }
        }

        double[] results = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String c = queries.get(i).get(0);
            String d = queries.get(i).get(1);
            results[i] = (!indexOf.containsKey(c) || !indexOf.containsKey(d))
                    ? -1.0
                    : dist[indexOf.get(c)][indexOf.get(d)];
        }
        return results;
    }
}
```

### Complexity

Preprocessing `O(V³)`; each query `O(1)`. Space `O(V²)` for the matrix. Given the constraint `equations.length ≤ 20`, `V ≤ 40`, so `V³` is trivially fast here.

---

## Complexity Comparison

| Approach            | Preprocessing | Per Query | Space    | Best Suited For                           |
| ------------------- | ------------- | --------- | -------- | ----------------------------------------- |
| DFS                 | O(V + E)      | O(V + E)  | O(V + E) | Simple, few queries, easy to reason about |
| BFS                 | O(V + E)      | O(V + E)  | O(V + E) | Same as DFS, avoids deep recursion        |
| Weighted Union-Find | O(N·α(N))     | O(α(N))   | O(V)     | Many queries, online/streaming equations  |
| Floyd-Warshall      | O(V³)         | O(1)      | O(V²)    | Small variable set, query-heavy workloads |

All four pass the given examples (`{a,b,c}`, `{a,b,c,bc,cd}`, `{a,b}` test cases) and correctly return `-1.0` for both "unreachable" pairs (e.g., `a/e`) and "never-seen" variables (e.g., `x/x`).

## Choosing an Approach

For interviews, DFS or BFS is usually the expected answer since it directly mirrors the graph-traversal intuition and is easy to explain on a whiteboard. Union-Find is the better production choice when equations and queries arrive incrementally over time. Floyd-Warshall is worth mentioning as an alternative when the variable universe is small and queries vastly outnumber equations, since it trades a one-time cubic cost for constant-time answers afterward.
