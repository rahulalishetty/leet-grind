# Cities Connected by Common Divisor — Java (All Approaches)

## Method Signature Used in All Approaches

```java
public List<Boolean> areConnected(int n, int threshold, int[][] queries)
```

All solutions below return `List<Boolean>` to match the required method signature.

---

# Problem Summary

We have **n cities labeled from 1 to n**.

Two cities **x and y** are directly connected if there exists a divisor **z** such that:

- `x % z == 0`
- `y % z == 0`
- `z > threshold`

Queries ask whether two cities are **connected directly or indirectly**.

Indirect connection means there may exist a path through intermediate cities.

Example:

```
3 -- 6 -- 9
```

If each pair shares a valid divisor greater than the threshold, they form a connected component.

---

# Key Insight

If a divisor `d > threshold` exists, then:

```
d, 2d, 3d, 4d ...
```

all share divisor `d`, so they belong to the **same connected component**.

This leads naturally to **Union-Find (Disjoint Set Union)**.

---

# Approach 1 — Union Find Using Divisor Multiples (Recommended)

## Idea

For each divisor `d > threshold`, connect all multiples of `d`.

Example:

```
d = 3
multiples → 3,6,9,12
```

Union them together.

After preprocessing, two cities are connected if their DSU parents match.

---

## Java Implementation

```java
import java.util.*;

class Solution {

    static class DSU {
        int[] parent;
        int[] rank;

        DSU(int n) {
            parent = new int[n + 1];
            rank = new int[n + 1];

            for (int i = 0; i <= n; i++)
                parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x)
                parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int a, int b) {

            int ra = find(a);
            int rb = find(b);

            if (ra == rb) return;

            if (rank[ra] < rank[rb])
                parent[ra] = rb;
            else if (rank[ra] > rank[rb])
                parent[rb] = ra;
            else {
                parent[rb] = ra;
                rank[ra]++;
            }
        }
    }

    public List<Boolean> areConnected(int n, int threshold, int[][] queries) {

        if (threshold == 0) {
            List<Boolean> ans = new ArrayList<>();
            for (int i = 0; i < queries.length; i++)
                ans.add(true);
            return ans;
        }

        DSU dsu = new DSU(n);

        for (int d = threshold + 1; d <= n; d++) {
            for (int multiple = 2 * d; multiple <= n; multiple += d) {
                dsu.union(d, multiple);
            }
        }

        List<Boolean> ans = new ArrayList<>();

        for (int[] q : queries)
            ans.add(dsu.find(q[0]) == dsu.find(q[1]));

        return ans;
    }
}
```

### Complexity

Time:

```
O(n log n + q)
```

Space:

```
O(n)
```

This is the **optimal solution**.

---

# Approach 2 — Union Find (Size Optimization)

This version uses **union by size** instead of rank.

---

```java
import java.util.*;

class Solution {

    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {

            parent = new int[n + 1];
            size = new int[n + 1];

            for (int i = 0; i <= n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {

            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }

            return x;
        }

        void union(int a, int b) {

            int ra = find(a);
            int rb = find(b);

            if (ra == rb) return;

            if (size[ra] < size[rb]) {
                int temp = ra;
                ra = rb;
                rb = temp;
            }

            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public List<Boolean> areConnected(int n, int threshold, int[][] queries) {

        DSU dsu = new DSU(n);

        for (int d = threshold + 1; d <= n; d++) {
            for (int multiple = 2 * d; multiple <= n; multiple += d)
                dsu.union(d, multiple);
        }

        List<Boolean> ans = new ArrayList<>();

        for (int[] q : queries)
            ans.add(dsu.find(q[0]) == dsu.find(q[1]));

        return ans;
    }
}
```

---

# Approach 3 — Explicit Graph + BFS

Instead of DSU, we build a graph and run BFS.

This approach is **correct but inefficient**.

Graph creation requires checking many connections.

---

```java
import java.util.*;

class Solution {

    public List<Boolean> areConnected(int n, int threshold, int[][] queries) {

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i <= n; i++)
            graph.add(new ArrayList<>());

        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {

                if (gcd(i, j) > threshold) {

                    graph.get(i).add(j);
                    graph.get(j).add(i);

                }
            }
        }

        List<Boolean> ans = new ArrayList<>();

        for (int[] q : queries)
            ans.add(bfs(graph, q[0], q[1], n));

        return ans;
    }

    boolean bfs(List<List<Integer>> graph, int src, int dst, int n) {

        Queue<Integer> q = new ArrayDeque<>();
        boolean[] seen = new boolean[n + 1];

        q.offer(src);
        seen[src] = true;

        while (!q.isEmpty()) {

            int node = q.poll();

            if (node == dst)
                return true;

            for (int nei : graph.get(node)) {

                if (!seen[nei]) {

                    seen[nei] = true;
                    q.offer(nei);

                }
            }
        }

        return false;
    }

    int gcd(int a, int b) {

        while (b != 0) {

            int t = a % b;
            a = b;
            b = t;

        }

        return a;
    }
}
```

### Complexity

Graph building:

```
O(n²)
```

Too slow for large `n`.

---

# Approach 4 — Component Labeling Using Graph

Build graph via divisor multiples and run BFS once to assign components.

Queries become O(1).

Still heavier than DSU.

---

```java
import java.util.*;

class Solution {

    public List<Boolean> areConnected(int n, int threshold, int[][] queries) {

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i <= n; i++)
            graph.add(new ArrayList<>());

        for (int d = threshold + 1; d <= n; d++) {

            List<Integer> multiples = new ArrayList<>();

            for (int x = d; x <= n; x += d)
                multiples.add(x);

            for (int i = 1; i < multiples.size(); i++) {

                int u = multiples.get(0);
                int v = multiples.get(i);

                graph.get(u).add(v);
                graph.get(v).add(u);
            }
        }

        int[] comp = new int[n + 1];
        Arrays.fill(comp, -1);

        int compId = 0;

        for (int node = 1; node <= n; node++) {

            if (comp[node] != -1) continue;

            Queue<Integer> q = new ArrayDeque<>();
            q.offer(node);

            comp[node] = compId;

            while (!q.isEmpty()) {

                int cur = q.poll();

                for (int nei : graph.get(cur)) {

                    if (comp[nei] == -1) {

                        comp[nei] = compId;
                        q.offer(nei);

                    }
                }
            }

            compId++;
        }

        List<Boolean> ans = new ArrayList<>();

        for (int[] q : queries)
            ans.add(comp[q[0]] == comp[q[1]]);

        return ans;
    }
}
```

---

# Complexity Comparison

| Approach          | Time           | Space        | Notes        |
| ----------------- | -------------- | ------------ | ------------ |
| Union Find        | O(n log n + q) | O(n)         | Best         |
| Union Find (size) | O(n log n + q) | O(n)         | Also optimal |
| Graph + BFS       | O(n²)          | O(n²)        | Too slow     |
| Component Graph   | O(n log n)     | O(n + edges) | Heavy        |

---

# Final Recommendation

Use **Union-Find with divisor multiples**.

Steps:

1. Iterate divisors `d > threshold`
2. Union all multiples of `d`
3. For each query compare DSU roots

Final complexity:

```
Time:  O(n log n + q)
Space: O(n)
```
