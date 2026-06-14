# 1059. All Paths from Source Lead to Destination

## Problem Statement

Given the edges of a directed graph where:

```
edges[i] = [ai, bi]
```

indicates there is a directed edge from node **ai → bi**.

You are also given two nodes:

- **source**
- **destination**

Your task is to determine whether **all paths starting from `source` eventually end at `destination`.**

Return **true** if and only if the following conditions hold.

---

# Conditions

For the graph to be valid:

### 1. A path from source to destination must exist

There must be **at least one path** starting at `source` that reaches `destination`.

---

### 2. Any path that ends must end at destination

If we reach a node that has **no outgoing edges**, then that node **must be the destination**.

In other words:

```
Dead-end nodes are only allowed if they are the destination.
```

---

### 3. The number of paths must be finite

There must **not be an infinite loop** reachable from the source.

If a cycle exists that can be visited indefinitely without reaching the destination, the answer must be **false**.

---

# Return Value

Return:

```
true
```

if and only if **all paths from source lead to destination**.

Otherwise return:

```
false
```

---

# Example 1

```
Input:
n = 3
edges = [[0,1],[0,2]]
source = 0
destination = 2
```

### Graph

```
0 → 1
 \
  → 2
```

### Explanation

- From node `0` we can go to `1` or `2`.
- Node `1` has no outgoing edges.
- Node `1` is **not the destination**.

So we can get stuck at node `1`.

```
Output: false
```

---

# Example 2

```
Input:
n = 4
edges = [[0,1],[0,3],[1,2],[2,1]]
source = 0
destination = 3
```

### Graph

```
0 → 1 → 2
     ↑   ↓
     └───┘

0 → 3
```

### Explanation

There are two possibilities:

1. Path reaches `3` (valid).
2. Path loops infinitely between `1` and `2`.

Because **infinite paths exist**, the condition fails.

```
Output: false
```

---

# Example 3

```
Input:
n = 4
edges = [[0,1],[0,2],[1,3],[2,3]]
source = 0
destination = 3
```

### Graph

```
0 → 1 → 3
 \
  → 2 → 3
```

### Explanation

All possible paths from `0` eventually reach `3`.

No dead ends exist other than `3`.

No infinite cycles exist.

```
Output: true
```

---

# Constraints

```
1 <= n <= 10^4
0 <= edges.length <= 10^4
edges[i].length == 2
0 <= ai, bi <= n - 1
0 <= source <= n - 1
0 <= destination <= n - 1
```

Additional notes:

- The graph **may contain self-loops**.
- The graph **may contain parallel edges**.
