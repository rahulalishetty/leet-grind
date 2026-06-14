# 1135. Connecting Cities With Minimum Cost

## Detailed Notes on the Minimum Spanning Tree Approach Using Kruskal’s Algorithm

## Overview

We are given:

- `n` cities labeled from `1` to `n`
- a list of possible bidirectional connections
- each connection has a cost

Our task is to connect all cities so that:

- every city is reachable from every other city
- the **total cost** of chosen connections is minimized

If it is impossible to connect all cities, we return:

```text
-1
```

This is a classic **Minimum Spanning Tree (MST)** problem.

---

# Why This Is a Minimum Spanning Tree Problem

If we model the input as a graph:

- each city is a **node**
- each connection is an **undirected weighted edge**
- the edge weight is the connection cost

Then the problem asks for:

> a subset of edges that connects all nodes with minimum possible total weight

That is exactly the definition of a **Minimum Spanning Tree**.

---

# What Is a Minimum Spanning Tree?

A **spanning tree** of a graph is a subset of edges that:

- connects all the nodes
- contains no cycles

If the graph has `N` nodes, then any spanning tree must contain exactly:

```text
N - 1 edges
```

A **minimum spanning tree** is the spanning tree whose total edge weight is as small as possible.

So our task is simply:

- find the MST cost
- if no spanning tree exists, return `-1`

---

# Why Kruskal’s Algorithm Fits Well

There are multiple MST algorithms, most notably:

- **Kruskal’s algorithm**
- **Prim’s algorithm**

The editorial chooses **Kruskal’s algorithm**, which is especially natural when the graph is given as an edge list.

Kruskal’s algorithm works by:

1. sorting all edges by weight
2. scanning from smallest to largest
3. greedily taking an edge if it connects two previously disconnected components
4. skipping edges that would form a cycle

To efficiently check whether two nodes are already connected, we use **Disjoint Set Union (DSU)**, also called **Union-Find**.

---

# High-Level Idea of Kruskal’s Algorithm

Suppose the edges are sorted by cost.

We process them one by one in ascending order.

For each edge `(a, b, w)`:

- if `a` and `b` are already connected, adding this edge would create a cycle, so skip it
- otherwise, take the edge and merge the two connected components

This greedy strategy is correct for MST construction.

At the end:

- if we picked exactly `N - 1` edges, then all cities are connected
- otherwise the graph was disconnected, so return `-1`

---

# Why Union-Find Is Needed

While scanning edges, we repeatedly need to answer:

> Are city `a` and city `b` already in the same connected component?

If yes, skip the edge.
If no, include it and merge the components.

Doing this efficiently is exactly what Union-Find is designed for.

Union-Find supports three core ideas:

- each set represents one connected component
- `Find(x)` gives the representative (root) of the set containing `x`
- `Union(a, b)` merges the sets of `a` and `b`

---

# Step 1: Vanilla Disjoint Set

The simplest Union-Find stores only a `parent` array.

Initially:

- each node is its own parent
- so every city starts as its own isolated component

---

## Vanilla Disjoint Set Code

```java
/** Vanilla Disjoint-set Union Find **/
class DisjointSet {
    private int[] parents;

    public void Union(int a, int b) {
        int rootA = Find(a);
        int rootB = Find(b);
        // If both a and b have same root, i.e. they both belong to the same set, hence we don't need to take union
        if (rootA == rootB) return;
        // Take union by assigning rootA as the parent of rootB
        this.parents[rootB] = rootA;
    }

    public int Find(int a) {
        // Traverse all the way to the top (root) going through the parent nodes
        while (a != this.parents[a]) {
            a = this.parents[a];
        }
        return a;
    }

    public boolean isInSameGroup(int a, int b) {
        // Return true if both a and b belong to the same set, otherwise return false
        return Find(a) == Find(b);
    }

    public DisjointSet(int N) {
        this.parents = new int[N + 1];
        // Set the initial parent node to itself
        for (int i = 1; i <= N; ++i) {
            this.parents[i] = i;
        }
    }
}
```

---

## How This Works

### Constructor

```java
for (int i = 1; i <= N; ++i) {
    this.parents[i] = i;
}
```

Initially every city is alone, so it is its own root.

---

### `Find(a)`

```java
while (a != this.parents[a]) {
    a = this.parents[a];
}
return a;
```

This climbs the parent chain until reaching the root of the component.

---

### `Union(a, b)`

```java
int rootA = Find(a);
int rootB = Find(b);
if (rootA == rootB) return;
this.parents[rootB] = rootA;
```

If the roots differ, the two components are merged by making one root point to the other.

---

### `isInSameGroup(a, b)`

```java
return Find(a) == Find(b);
```

If two nodes share the same root, they are already connected.

---

# Step 2: Path Compression Optimization

The vanilla `Find(...)` can become slow if the parent chains get tall.

To improve it, we use **path compression**.

The idea is:

- while finding the root
- make nodes along the path point closer to the root

That flattens the tree and speeds up future operations.

---

## Path Compression Version of `Find`

```java
private int Find(int a) {
    while (a != this.parents[a]) {
        // Path compression
        // a's grandparent is now a's parent
        this.parents[a] = this.parents[parents[a]];
        a = this.parents[a];
    }
    return a;
}
```

---

## Why Path Compression Helps

Without path compression, repeated finds on deep trees can be expensive.

With path compression:

- trees flatten quickly
- future `Find(...)` calls become much faster

---

# Step 3: Weighted Union Optimization

Another optimization is **weighted union** (also called union by size).

The idea is:

- keep track of the size of each component
- when merging two components, attach the smaller tree under the larger tree

This helps keep the tree shallow.

---

## Weighted Union Code

```java
class DisjointSet {
    private int[] weights; // Used to store weights of each nodes
    private int[] parents;

    // Modify Union method to incorporate weighted compression
    private void Union(int a, int b) {
        int rootA = Find(a);
        int rootB = Find(b);
        // If both a and b have same root, i.e. they both belong to the same set, hence we don't need to take union
        if (rootA == rootB) return;
        // Weighted union
        if (this.weights[rootA] > this.weights[rootB]) {
            // In case rootA is having more weight
            // 1. Make rootA as the parent of rootB
            // 2. Increment the weight of rootA by rootB's weight
            this.parents[rootB] = rootA;
            this.weights[rootA] += this.weights[rootB];
        } else {
            // Otherwise
            // 1. Make rootB as the parent of rootA
            // 2. Increment the weight of rootB by rootA's weight
            this.parents[rootA] = rootB;
            this.weights[rootB] += this.weights[rootA];
        }
    }

    // Initialize weight for each node to be 1
    public DisjointSet(int N) {
        this.parents = new int[N + 1];
        this.weights = new int[N + 1];
        // Set the initial parent node to itself
        for (int i = 1; i <= N; ++i) {
            this.parents[i] = i;
            this.weights[i] = 1;
        }
    }
}
```

---

## Why Weighted Union Helps

If we always attach the smaller component under the larger one, the resulting trees stay balanced enough that operations become very efficient.

Combined with path compression, Union-Find becomes extremely fast in practice.

---

# Step 4: Final Optimized Disjoint Set

Now combine:

- path compression
- weighted union

This is the final Union-Find used in Kruskal’s algorithm.

---

## Final Disjoint Set Code

```java
class DisjointSet {
    private int[] weights; // Used to store weights of each nodes
    private int[] parents;

    public void Union(int a, int b) {
        int rootA = Find(a);
        int rootB = Find(b);
        // If both a and b have same root, i.e. they both belong to the same set, hence we don't need to take union
        if (rootA == rootB) return;

        // Weighted union
        if (this.weights[rootA] > this.weights[rootB]) {
            // In case rootA is having more weight
            // 1. Make rootA as the parent of rootB
            // 2. Increment the weight of rootA by rootB's weight
            this.parents[rootB] = rootA;
            this.weights[rootA] += this.weights[rootB];
        } else {
            // Otherwise
            // 1. Make rootB as the parent of rootA
            // 2. Increment the weight of rootB by rootA's weight
            this.parents[rootA] = rootB;
            this.weights[rootB] += this.weights[rootA];
        }
    }

    public int Find(int a) {
        // Traverse all the way to the top (root) going through the parent nodes
        while (a != this.parents[a]) {
            // Path compression
            // a's grandparent is now a's parent
            this.parents[a] = this.parents[parents[a]];
            a = this.parents[a];
        }
        return a;
    }

    public boolean isInSameGroup(int a, int b) {
        // Return true if both a and b belong to the same set, otherwise return false
        return Find(a) == Find(b);
    }

    // Initialize weight for each node to be 1
    public DisjointSet(int N) {
        this.parents = new int[N + 1];
        this.weights = new int[N + 1];
        // Set the initial parent node to itself
        for (int i = 1; i <= N; ++i) {
            this.parents[i] = i;
            this.weights[i] = 1;
        }
    }
}
```

---

# Kruskal’s Algorithm for This Problem

Now we apply Kruskal’s MST logic.

The steps are:

1. initialize Union-Find on all cities
2. sort all connections by increasing cost
3. iterate through the sorted edges
4. if edge endpoints are already connected, skip
5. otherwise:
   - union them
   - add the edge cost
   - increase the count of chosen edges
6. after processing all edges:
   - if we selected `N - 1` edges, return total cost
   - otherwise return `-1`

---

## Full Java Solution

```java
class DisjointSet {
    private int[] weights; // Used to store weights of each nodes
    private int[] parents;

    public void Union(int a, int b) {
        int rootA = Find(a);
        int rootB = Find(b);
        // If both a and b have same root, i.e. they both belong to the same set, hence we don't need to take union
        if (rootA == rootB) return;

        // Weighted union
        if (this.weights[rootA] > this.weights[rootB]) {
            // In case rootA is having more weight
            // 1. Make rootA as the parent of rootB
            // 2. Increment the weight of rootA by rootB's weight
            this.parents[rootB] = rootA;
            this.weights[rootA] += this.weights[rootB];
        } else {
            // Otherwise
            // 1. Make rootB as the parent of rootA
            // 2. Increment the weight of rootB by rootA's weight
            this.parents[rootA] = rootB;
            this.weights[rootB] += this.weights[rootA];
        }
    }

    public int Find(int a) {
        // Traverse all the way to the top (root) going through the parent nodes
        while (a != this.parents[a]) {
            // Path compression
            // a's grandparent is now a's parent
            this.parents[a] = this.parents[parents[a]];
            a = this.parents[a];
        }
        return a;
    }

    public boolean isInSameGroup(int a, int b) {
        // Return true if both a and b belong to the same set, otherwise return false
        return Find(a) == Find(b);
    }

    // Initialize weight for each node to be 1
    public DisjointSet(int N) {
        this.parents = new int[N + 1];
        this.weights = new int[N + 1];
        // Set the initial parent node to itself
        for (int i = 1; i <= N; ++i) {
            this.parents[i] = i;
            this.weights[i] = 1;
        }
    }
}

class Solution {
    public int minimumCost(int N, int[][] connections) {
        DisjointSet disjointset = new DisjointSet(N);
        // Sort connections based on their weights (in increasing order)
        Arrays.sort(connections, (a, b) -> a[2] - b[2]);
        // Keep track of total edges added in the MST
        int total = 0;
        // Keep track of the total cost of adding all those edges
        int cost = 0;
        for (int i = 0; i < connections.length; ++i) {
            int a = connections[i][0];
            int b = connections[i][1];
            // Do not add the edge from a to b if it is already connected
            if (disjointset.isInSameGroup(a, b)) continue;
            // If a and b are not connected, take union
            disjointset.Union(a, b);
            // increment cost
            cost += connections[i][2];
            // increment number of edges added in the MST
            total++;
        }
        // If all N nodes are connected, the MST will have a total of N - 1 edges
        if (total == N - 1) {
            return cost;
        } else {
            return -1;
        }
    }
}
```

---

# Detailed Walkthrough of the MST Logic

## Step 1: Sort edges by cost

```java
Arrays.sort(connections, (a, b) -> a[2] - b[2]);
```

This is the greedy core of Kruskal’s algorithm.

We always consider the cheapest remaining connection first.

---

## Step 2: Track MST cost and edge count

```java
int total = 0;
int cost = 0;
```

- `total` = number of edges chosen so far
- `cost` = total MST cost so far

---

## Step 3: Process each edge

```java
for (int i = 0; i < connections.length; ++i) {
    int a = connections[i][0];
    int b = connections[i][1];
```

Each edge connects city `a` and city `b`.

---

## Step 4: Skip edges that create cycles

```java
if (disjointset.isInSameGroup(a, b)) continue;
```

If `a` and `b` are already connected, adding this edge would form a cycle.
Since MSTs cannot contain cycles, skip it.

---

## Step 5: Otherwise include the edge

```java
disjointset.Union(a, b);
cost += connections[i][2];
total++;
```

This merges the components and adds the cost.

---

## Step 6: Final connectivity check

```java
if (total == N - 1) {
    return cost;
} else {
    return -1;
}
```

A spanning tree on `N` cities must contain exactly `N - 1` edges.

If we chose fewer than that, the graph was disconnected, so connecting all cities was impossible.

---

# Example Walkthrough

## Example 1

```text
n = 3
connections = [[1,2,5],[1,3,6],[2,3,1]]
```

Sort by cost:

```text
[2,3,1]
[1,2,5]
[1,3,6]
```

Now process in order.

### Edge (2,3) cost 1

- different components
- take it
- cost = 1
- total = 1

### Edge (1,2) cost 5

- different components
- take it
- cost = 6
- total = 2

Now `total = N - 1 = 2`, so all cities are connected.

Answer:

```text
6
```

---

## Example 2

```text
n = 4
connections = [[1,2,3],[3,4,4]]
```

There are only two disconnected pieces:

- component `{1,2}`
- component `{3,4}`

Even after using all edges, we cannot connect the whole graph.

We end with fewer than `N - 1 = 3` chosen edges.

So answer is:

```text
-1
```

---

# Why the Greedy Choice Is Correct

Kruskal’s algorithm relies on a classic MST property:

> The minimum-weight edge that connects two different components is always safe to add.

By always taking the cheapest edge that does not form a cycle, we never miss the optimal MST.

That is why sorting first and greedily selecting works.

---

# Complexity Analysis

Let:

- `N` = number of cities (nodes)
- `M` = number of connections (edges)

## Time Complexity

### 1. Sorting edges

```text
O(M log M)
```

because we sort all `M` connections by cost.

### 2. Union-Find operations

Each edge may perform:

- `isInSameGroup(...)`
- `Find(...)`
- possibly `Union(...)`

With:

- path compression
- weighted union

the amortized cost per Union-Find operation is extremely close to constant, commonly written as:

```text
O(log* N)
```

or more classically with inverse Ackermann notation as nearly constant.

So across all `M` edges:

```text
O(M log* N)
```

which is practically just linear.

### Total

The sorting term dominates:

```text
O(M log M)
```

---

## Space Complexity

```text
O(N)
```

Why?

The Union-Find structure stores:

- `parents[]`
- `weights[]`

both of size `N + 1`.

So total auxiliary space is linear in the number of cities.

---

# Why `log* N` Is Treated Like a Constant

The editorial mentions the iterated logarithm / inverse-Ackermann-type behavior.

This grows so slowly that for any practical input size, it is tiny.

So in implementation discussions, Union-Find operations are usually treated as almost constant time.

That is why the sorting cost is the main bottleneck.

---

# Strengths of This Approach

- perfect fit for an edge-list MST problem
- elegant greedy solution
- Union-Find makes cycle detection efficient
- easy to prove correctness with MST theory
- scalable for large sparse graphs

---

# Potential Pitfalls

## 1. Forgetting cities are 1-indexed

The DSU constructor uses arrays of size `N + 1` because cities are labeled from `1` to `N`.

## 2. Returning cost without checking `N - 1` edges

Even if you collect some cheap edges, the graph may still be disconnected.
You must ensure exactly `N - 1` edges were chosen.

## 3. Adding an edge before checking connectivity

Always test whether its endpoints already belong to the same group first.

---

# Final Takeaway

The key insight is:

> Connecting all cities with minimum total cost is exactly the Minimum Spanning Tree problem.

Once that is recognized, Kruskal’s algorithm becomes the natural solution:

1. sort edges by cost
2. greedily add safe edges
3. use Union-Find to avoid cycles
4. verify that all cities ended up connected

This gives a clean and efficient solution.

---

# Summary

## Problem Transformation

Model cities as graph nodes and connections as weighted undirected edges.

## Core Goal

Find the minimum spanning tree (MST) cost.

## Main Algorithm

Use **Kruskal’s algorithm** with **Disjoint Set Union (Union-Find)**.

## Union-Find Optimizations

- path compression
- weighted union / union by size

## Complexity

- **Time:** `O(M log M)`
- **Space:** `O(N)`

## Recommended

Kruskal’s algorithm is the most natural solution for this edge-list-based MST problem.
