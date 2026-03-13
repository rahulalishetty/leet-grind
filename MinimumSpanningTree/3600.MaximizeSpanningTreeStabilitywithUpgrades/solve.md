# Approach: Binary Answer + Minimum Spanning Tree

## Prerequisites

This method assumes that the reader is already familiar with:

- The idea of **binary search on the answer** and its algorithmic framework.
- The definition of a **minimum spanning tree (MST)** and the **Kruskal algorithm** used to construct it.

---

# Intuition

This problem asks us to **maximize the minimum edge weight** in a spanning tree.

Problems involving:

- _maximize the minimum_
- _minimize the maximum_

are frequently solved using **Binary Search on the Answer**.

So we binary search on the **minimum edge weight (stability)** and check whether it is possible to construct a spanning tree satisfying that constraint.

---

# Key Idea

For a given stability threshold `mid`:

Can we build a spanning tree where every edge has:

```
strength >= mid
```

If not, we allow **upgrades**.

An upgrade doubles an optional edge weight.

---

# Greedy Strategy

To maximize the minimum edge weight:

We should **prefer edges with larger strength**.

This is equivalent to building a:

```
Maximum Spanning Tree
```

We use **Kruskal's algorithm** with edges sorted in **descending order of strength**.

---

# Handling Mandatory Edges

Edges with:

```
must = 1
```

must always be included.

Steps:

1. Preprocess mandatory edges.
2. Add them first using **Union-Find (DSU)**.
3. Track the minimum strength among them.
4. This minimum becomes the **upper bound of binary search**.

If mandatory edges already form a **cycle**, return `-1`.

---

# Handling Optional Edges

Edges with:

```
must = 0
```

are sorted by strength **descending**.

During the MST construction:

- If edge weight ≥ mid → use directly
- Else if upgrade available and `weight * 2 ≥ mid` → upgrade and use
- Else → cannot satisfy constraint

---

# Valid Spanning Tree Conditions

A valid spanning tree must:

```
1. Connect all nodes
2. Contain exactly n-1 edges
3. Contain no cycles
```

Union-Find already guarantees **no cycles**.

So we only check:

```
selectedEdges == n - 1
```

---

# Implementation

```java
class DSU {

    int[] parent;

    DSU(int[] parent) {
        this.parent = parent.clone();
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    void join(int x, int y) {
        int px = find(x);
        int py = find(y);
        parent[px] = py;
    }
}

public class Solution {

    private static final int MAX_STABILITY = 200000;

    public int maxStability(int n, int[][] edges, int k) {
        int ans = -1;
        if (edges.length < n - 1) {
            return -1;
        }

        List<int[]> mustEdges = new ArrayList<>();
        List<int[]> optionalEdges = new ArrayList<>();

        for (int[] edge : edges) {
            if (edge[3] == 1) {
                mustEdges.add(edge);
            } else {
                optionalEdges.add(edge);
            }
        }

        if (mustEdges.size() > n - 1) {
            return -1;
        }

        optionalEdges.sort((a, b) -> b[2] - a[2]);

        int selectedInit = 0;
        int mustMinStability = MAX_STABILITY;

        int[] initParent = new int[n];
        for (int i = 0; i < n; i++) {
            initParent[i] = i;
        }

        DSU dsuInit = new DSU(initParent);

        for (int[] e : mustEdges) {
            int u = e[0];
            int v = e[1];
            int s = e[2];

            if (dsuInit.find(u) == dsuInit.find(v) || selectedInit == n - 1) {
                return -1;
            }

            dsuInit.join(u, v);
            selectedInit++;
            mustMinStability = Math.min(mustMinStability, s);
        }

        int l = 0;
        int r = mustMinStability;

        while (l < r) {

            int mid = l + (r - l + 1) / 2;

            DSU dsu = new DSU(dsuInit.parent);

            int selected = selectedInit;
            int doubledCount = 0;

            for (int[] e : optionalEdges) {

                int u = e[0];
                int v = e[1];
                int s = e[2];

                if (dsu.find(u) == dsu.find(v)) {
                    continue;
                }

                if (s >= mid) {
                    dsu.join(u, v);
                    selected++;
                }

                else if (doubledCount < k && s * 2 >= mid) {
                    doubledCount++;
                    dsu.join(u, v);
                    selected++;
                }

                else {
                    break;
                }

                if (selected == n - 1) {
                    break;
                }
            }

            if (selected != n - 1) {
                r = mid - 1;
            } else {
                ans = l = mid;
            }
        }

        return ans;
    }
}
```

---

# Complexity Analysis

Let:

```
m = number of edges
v = upper bound of binary search
```

### Time Complexity

```
O(m log m + (n + m * α(n)) * log v)
```

Explanation:

- Sorting edges → `O(m log m)`
- Union-Find operations → `O(α(n))`
- Binary search iterations → `O(log v)`

Where:

```
α(n)
```

is the **inverse Ackermann function**, which grows extremely slowly and is effectively constant.

---

### Space Complexity

```
O(n + m)
```

- Union-Find parent array → `O(n)`
- Edge storage → `O(m)`
