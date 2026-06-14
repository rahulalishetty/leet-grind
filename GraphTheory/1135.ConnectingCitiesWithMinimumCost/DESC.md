# 1135. Connecting Cities With Minimum Cost

## Problem Statement

There are **n cities** labeled from **1 to n**.

You are given:

- an integer `n`
- a list `connections`

Each element in `connections` has the format:

```
connections[i] = [xi, yi, costi]
```

This means:

- City **xi** and city **yi** can be connected
- The **cost** to connect them is **costi**
- The connection is **bidirectional**

---

# Goal

Connect **all cities** such that:

- There is **at least one path between every pair of cities**.

In other words, the graph formed by the chosen connections must be **fully connected**.

Return:

```
The minimum total cost required to connect all cities
```

If it is **impossible to connect all cities**, return:

```
-1
```

---

# Important Notes

- The total cost is the **sum of the costs of the selected connections**
- Not all connections must be used
- Only the **minimum set of edges** required to connect all cities should be chosen

---

# Example 1

```
Input:
n = 3
connections = [[1,2,5],[1,3,6],[2,3,1]]
```

### Graph Representation

```
1 ---5--- 2
|         |
6         1
|         |
3 ---------
```

### Explanation

We need **n - 1 = 2 edges** to connect 3 cities.

Possible selections:

1. (1,2) and (1,3) → cost = 11
2. (1,2) and (2,3) → cost = 6
3. (1,3) and (2,3) → cost = 7

Minimum cost choice:

```
(1,2) + (2,3)
= 5 + 1
= 6
```

### Output

```
6
```

---

# Example 2

```
Input:
n = 4
connections = [[1,2,3],[3,4,4]]
```

### Graph Representation

```
1 ---3--- 2

3 ---4--- 4
```

### Explanation

The graph has **two disconnected components**:

Component 1:

```
1 --- 2
```

Component 2:

```
3 --- 4
```

There is **no connection between the two components**, so it is impossible to connect all cities.

### Output

```
-1
```

---

# Constraints

```
1 <= n <= 10^4
1 <= connections.length <= 10^4
connections[i].length == 3
1 <= xi, yi <= n
xi != yi
0 <= costi <= 10^5
```

Additional Notes:

- Cities are labeled **1 through n**
- Connections are **undirected**
- Multiple connections may exist between cities
