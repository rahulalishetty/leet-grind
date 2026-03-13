# 3600. Maximize Spanning Tree Stability with Upgrades

You are given an integer **n**, representing **n nodes** numbered from **0 to n - 1** and a list of edges, where:

```
edges[i] = [ui, vi, si, musti]
```

- **ui, vi** indicate an undirected edge between nodes `ui` and `vi`.
- **si** is the **strength** of the edge.
- **musti** is either `0` or `1`:
  - If `musti == 1`, the edge **must be included** in the spanning tree.
  - Mandatory edges **cannot be upgraded**.

You are also given an integer **k**, representing the **maximum number of upgrades** allowed.

Each upgrade:

- **doubles the strength** of an edge
- can only be applied to **optional edges (musti == 0)**
- each eligible edge can be upgraded **at most once**

---

## Stability Definition

The **stability of a spanning tree** is defined as:

```
minimum strength among all edges in the spanning tree
```

Your task is to **maximize this stability**.

Return the **maximum possible stability** of any valid spanning tree.

If it is **impossible to connect all nodes**, return:

```
-1
```

---

## Spanning Tree Reminder

A **spanning tree** of a graph with `n` nodes:

- connects **all nodes**
- contains **exactly n - 1 edges**
- contains **no cycles**

---

# Example 1

Input:

```
n = 3
edges = [[0,1,2,1],[1,2,3,0]]
k = 1
```

Output:

```
2
```

Explanation:

- Edge `[0,1]` must be included with strength `2`
- Edge `[1,2]` can be upgraded from `3` → `6`
- Spanning tree edges: `[2, 6]`
- Stability = `min(2,6) = 2`

---

# Example 2

Input:

```
n = 3
edges = [[0,1,4,0],[1,2,3,0],[0,2,1,0]]
k = 2
```

Output:

```
6
```

Explanation:

Upgrade:

```
[0,1] : 4 → 8
[1,2] : 3 → 6
```

Spanning tree edges:

```
8 and 6
```

Stability:

```
min(8,6) = 6
```

---

# Example 3

Input:

```
n = 3
edges = [[0,1,1,1],[1,2,1,1],[2,0,1,1]]
k = 0
```

Output:

```
-1
```

Explanation:

All edges are **mandatory**, but they form a **cycle**, which violates the spanning tree rule.

Therefore, no valid spanning tree exists.

---

# Constraints

```
2 <= n <= 10^5
1 <= edges.length <= 10^5
edges[i] = [ui, vi, si, musti]

0 <= ui, vi < n
ui != vi

1 <= si <= 10^5
musti ∈ {0,1}

0 <= k <= n

No duplicate edges
```
