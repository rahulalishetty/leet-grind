# 1245. Tree Diameter

## Problem Statement

The **diameter of a tree** is defined as the **number of edges in the longest path between any two nodes** in the tree.

You are given an **undirected tree** with `n` nodes labeled from `0` to `n - 1`.

The tree is described by an array `edges` where:

```
edges[i] = [a_i, b_i]
```

indicates that there is an **undirected edge** between node `a_i` and node `b_i`.

Your task is to **return the diameter of the tree**.

---

## Example 1

Input

```
edges = [[0,1],[0,2]]
```

Output

```
2
```

Explanation

The longest path in the tree is:

```
1 — 0 — 2
```

Number of edges in this path = **2**.

---

## Example 2

Input

```
edges = [[0,1],[1,2],[2,3],[1,4],[4,5]]
```

Output

```
4
```

Explanation

The longest path in the tree is:

```
3 — 2 — 1 — 4 — 5
```

Number of edges in this path = **4**.

---

## Constraints

```
n == edges.length + 1
1 <= n <= 10^4
0 <= a_i, b_i < n
a_i != b_i
```

The input graph is guaranteed to be a **tree**.

---
