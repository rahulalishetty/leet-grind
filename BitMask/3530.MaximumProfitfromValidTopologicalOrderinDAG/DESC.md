# 3530. Maximum Profit from Valid Topological Order in DAG

## Problem Statement

You are given a **Directed Acyclic Graph (DAG)** with `n` nodes labeled from `0` to `n - 1`.

The graph is represented by:

```
edges[i] = [ui, vi]
```

This indicates a directed edge from node `ui` to node `vi`.

Each node has an associated score given by the array:

```
score[i]
```

where `score[i]` represents the score of node `i`.

---

## Processing Rules

You must process the nodes in a **valid topological order**.

Each node is assigned a **1-based position** in the processing order.

---

## Profit Calculation

The **profit** is calculated as:

```
profit = Σ(score[node] × position_of_node)
```

Your goal is to **maximize the total profit** by choosing the optimal valid topological ordering.

---

## Definition: Topological Order

A **topological order** of a DAG is a linear ordering of its nodes such that:

```
for every edge u → v
node u appears before node v in the ordering
```

---

# Example 1

## Input

```
n = 2
edges = [[0,1]]
score = [2,3]
```

## Output

```
8
```

## Explanation

Node `1` depends on node `0`, so the only valid order is:

```
[0, 1]
```

| Node | Processing Order | Score | Multiplier | Profit    |
| ---- | ---------------- | ----- | ---------- | --------- |
| 0    | 1st              | 2     | 1          | 2 × 1 = 2 |
| 1    | 2nd              | 3     | 2          | 3 × 2 = 6 |

Total profit:

```
2 + 6 = 8
```

---

# Example 2

## Input

```
n = 3
edges = [[0,1],[0,2]]
score = [1,6,3]
```

## Output

```
25
```

## Explanation

Nodes `1` and `2` depend on node `0`.

A valid optimal order is:

```
[0, 2, 1]
```

| Node | Processing Order | Score | Multiplier | Profit     |
| ---- | ---------------- | ----- | ---------- | ---------- |
| 0    | 1st              | 1     | 1          | 1 × 1 = 1  |
| 2    | 2nd              | 3     | 2          | 3 × 2 = 6  |
| 1    | 3rd              | 6     | 3          | 6 × 3 = 18 |

Total profit:

```
1 + 6 + 18 = 25
```

---

# Constraints

```
1 <= n == score.length <= 22
1 <= score[i] <= 10^5
0 <= edges.length <= n * (n - 1) / 2
edges[i] == [ui, vi]
0 <= ui, vi < n
ui != vi
```
