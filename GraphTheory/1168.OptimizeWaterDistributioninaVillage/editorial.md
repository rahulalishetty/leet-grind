## Detailed Notes on MST Conversion, Prim's Algorithm, and Kruskal's Algorithm

## Overview

This problem asks us to supply water to all houses with the minimum possible cost.

Each house has two possible ways to receive water:

1. Build a well directly in the house
2. Connect it to another house using pipes

At first, this does not look exactly like a standard graph problem, because both:

- houses have costs (well costs)
- pipes have costs

However, with one important transformation, the problem becomes a classic **Minimum Spanning Tree (MST)** problem.

This write-up explains:

1. how to convert the problem into an MST problem
2. how to solve it with **Prim’s algorithm**
3. how to solve it with **Kruskal’s algorithm + Union-Find**

---

# Core Insight: Convert the Problem into an MST Problem

## Step 1: Model houses and pipes as a graph

We can think of:

- each house as a vertex
- each pipe as an undirected weighted edge

This already looks like a graph.

But there is one complication:

- in a standard MST problem, only **edges** have costs
- here, houses also have costs because building a well in a house costs money

So we need a trick to encode house costs as edge costs.

---

# The Virtual Vertex Trick

To handle well costs, we introduce one extra node:

```text
virtual node = 0
```

Then for every house `i`, add an edge:

```text
0 -> i
```

with weight equal to the cost of building a well in house `i`.

So:

- a pipe remains an ordinary edge between two houses
- a well becomes an edge from the virtual node to that house

Now every cost in the problem is represented uniformly as an **edge cost**.

That means:

> choosing an edge from node 0 to a house means “build a well there”

and:

> choosing an edge between two houses means “connect them by a pipe”

After this transformation, the problem becomes:

> find the minimum spanning tree of the new graph

---

# Why the MST Gives the Correct Answer

In the transformed graph:

- all houses plus the virtual node must be connected
- connecting a house to node `0` means paying for its well
- connecting two houses directly means paying for a pipe

A spanning tree selects exactly the edges needed to connect everything without cycles.

A **minimum** spanning tree therefore gives the cheapest possible combination of:

- wells
- pipes

So the transformed MST corresponds exactly to the optimal water distribution strategy.

---

# Minimum Spanning Tree Refresher

A **minimum spanning tree** of a connected, weighted, undirected graph is a subset of edges that:

- connects all vertices
- contains no cycles
- has minimum total weight among all such subsets

This is why MST is the right abstraction for the transformed graph.

---

# Approach 1: Prim’s Algorithm with Heap

## Intuition

Prim’s algorithm builds an MST by growing one connected component at a time.

It starts from an arbitrary node and repeatedly adds the cheapest edge that connects:

- a node already in the MST
- to a node not yet in the MST

This is a greedy approach.

In this problem, it is convenient to start from the virtual node `0`.

That makes sense because:

- the virtual node connects to every house through its well edge
- from there, the algorithm can decide whether each house is cheaper to reach by:
  - a well
  - or some pipe chain

---

# Prim’s Algorithm Strategy in This Problem

We need three main data structures:

## 1. Adjacency list

To represent the graph.

## 2. Set

To store which vertices are already included in the MST.

## 3. Min-heap / priority queue

To always extract the cheapest available edge leaving the current MST.

The greedy rule is:

> each step, add the cheapest edge that expands the current tree to a new node

---

# Building the Graph for Prim

We must build a graph with:

- nodes `1..n` = houses
- node `0` = virtual water source

Add edges:

1. from `0` to each house `i`, with weight `wells[i - 1]`
2. for each pipe `[house1, house2, cost]`, add:
   - `house1 -> house2`
   - `house2 -> house1`

because pipes are bidirectional.

---

# Java Implementation: Prim’s Algorithm

```java
class Solution {
    public int minCostToSupplyWater(int n, int[] wells, int[][] pipes) {
        // min heap to maintain the order of edges to be visited.
        PriorityQueue<Pair<Integer, Integer>> edgesHeap =
                new PriorityQueue<>(n, (a, b) -> (a.getKey() - b.getKey()));

        // representation of graph in adjacency list
        List<List<Pair<Integer, Integer>>> graph = new ArrayList<>(n + 1);
        for (int i = 0; i < n + 1; ++i) {
            graph.add(new ArrayList<Pair<Integer, Integer>>());
        }

        // add a virtual vertex indexed with 0,
        //   then add an edge to each of the house weighted by the cost
        for (int i = 0; i < wells.length; ++i) {
            Pair<Integer, Integer> virtualEdge = new Pair<>(wells[i], i + 1);
            graph.get(0).add(virtualEdge);
            // initialize the heap with the edges from the virtual vertex.
            edgesHeap.add(virtualEdge);
        }

        // add the bidirectional edges to the graph
        for (int i = 0; i < pipes.length; ++i) {
            int house1 = pipes[i][0];
            int house2 = pipes[i][1];
            int cost = pipes[i][2];
            graph.get(house1).add(new Pair<Integer, Integer>(cost, house2));
            graph.get(house2).add(new Pair<Integer, Integer>(cost, house1));
        }

        // kick off the exploration from the virtual vertex 0
        Set<Integer> mstSet = new HashSet<>();
        mstSet.add(0);

        int totalCost = 0;
        while (mstSet.size() < n + 1) {
            Pair<Integer, Integer> edge = edgesHeap.poll();
            int cost = edge.getKey();
            int nextHouse = edge.getValue();
            if (mstSet.contains(nextHouse)) {
                continue;
            }

            // adding the new vertex into the set
            mstSet.add(nextHouse);
            totalCost += cost;

            // expanding the candidates of edge to choose from in the next round
            for (Pair<Integer, Integer> neighborEdge : graph.get(nextHouse)) {
                if (!mstSet.contains(neighborEdge.getValue())) {
                    edgesHeap.add(neighborEdge);
                }
            }
        }

        return totalCost;
    }
}
```

---

# Detailed Walkthrough of Prim’s Code

## Step 1: Create the min-heap

```java
PriorityQueue<Pair<Integer, Integer>> edgesHeap =
        new PriorityQueue<>(n, (a, b) -> (a.getKey() - b.getKey()));
```

Each heap entry stores:

- edge cost
- destination house

The heap always gives us the cheapest edge available.

---

## Step 2: Build the adjacency list

```java
List<List<Pair<Integer, Integer>>> graph = new ArrayList<>(n + 1);
for (int i = 0; i < n + 1; ++i) {
    graph.add(new ArrayList<Pair<Integer, Integer>>());
}
```

This graph includes the virtual node `0`.

---

## Step 3: Add virtual edges for wells

```java
for (int i = 0; i < wells.length; ++i) {
    Pair<Integer, Integer> virtualEdge = new Pair<>(wells[i], i + 1);
    graph.get(0).add(virtualEdge);
    edgesHeap.add(virtualEdge);
}
```

This means:

- house `i + 1` can always be connected directly to water by paying `wells[i]`

These virtual edges are also used to initialize the heap, since Prim starts from node `0`.

---

## Step 4: Add bidirectional pipe edges

```java
for (int i = 0; i < pipes.length; ++i) {
    int house1 = pipes[i][0];
    int house2 = pipes[i][1];
    int cost = pipes[i][2];
    graph.get(house1).add(new Pair<Integer, Integer>(cost, house2));
    graph.get(house2).add(new Pair<Integer, Integer>(cost, house1));
}
```

Pipes are undirected, so add both directions.

---

## Step 5: Start MST from virtual node 0

```java
Set<Integer> mstSet = new HashSet<>();
mstSet.add(0);
```

At the beginning, only node `0` is in the tree.

---

## Step 6: Expand MST greedily

```java
while (mstSet.size() < n + 1) {
    Pair<Integer, Integer> edge = edgesHeap.poll();
    int cost = edge.getKey();
    int nextHouse = edge.getValue();
    if (mstSet.contains(nextHouse)) {
        continue;
    }

    mstSet.add(nextHouse);
    totalCost += cost;
```

Each time:

- take the cheapest edge from the heap
- if it reaches a new node, include it in the MST
- add its cost

If the destination house is already in `mstSet`, skip it because adding it would form a cycle or not expand the tree.

---

## Step 7: Push outgoing edges of the newly added node

```java
for (Pair<Integer, Integer> neighborEdge : graph.get(nextHouse)) {
    if (!mstSet.contains(neighborEdge.getValue())) {
        edgesHeap.add(neighborEdge);
    }
}
```

Now that this house is in the MST, any edges leaving it become candidates for future expansion.

---

# Complexity Analysis of Prim’s Algorithm

Let:

- `N` = number of houses
- `M` = number of pipes

## Time Complexity

### Graph construction

Building the graph takes:

```text
O(N + M)
```

because we add:

- `N` virtual well edges
- `2M` adjacency list entries for pipes

### Heap operations

In the worst case, every edge may enter the heap once and be removed once.

That gives:

```text
O((N + M) log(N + M))
```

So the total time complexity is:

```text
O((N + M) log(N + M))
```

---

## Space Complexity

We store:

- adjacency list: `O(N + M)`
- MST set: `O(N)`
- heap: up to `O(N + M)`

So total space is:

```text
O(N + M)
```

---

# Approach 2: Kruskal’s Algorithm with Union-Find

## Intuition

Kruskal’s algorithm is another classic MST algorithm.

Instead of growing a single connected tree like Prim, Kruskal works by:

- sorting all edges globally by weight
- adding them one by one if they connect different components

So Kruskal builds the MST by repeatedly merging disjoint components.

This fits naturally with the **Union-Find** data structure.

---

# Kruskal’s Strategy

The idea is simple:

1. create a list of all edges
2. include:
   - virtual well edges from node `0`
   - all pipe edges
3. sort all edges by increasing cost
4. iterate through sorted edges:
   - if the edge connects two different components, add it
   - otherwise skip it

At the end, the chosen edges form the MST.

---

# Why Union-Find Is Useful Here

For every edge, we need to decide:

> do the two endpoints already belong to the same connected component?

If yes:

- adding the edge would form a cycle
- skip it

If no:

- add it
- merge the two components

Union-Find supports exactly these operations efficiently:

- `find(x)` → returns the representative/root of the component containing `x`
- `union(a, b)` → merges the two components if different

---

# Java Implementation: Union-Find

```java
class UnionFind {
    /**
     * Implementation of UnionFind without load-balancing.
     */
    private int[] group;
    private int[] rank;

    public UnionFind(int size) {
        // container to hold the group id for each member
        // Note: the index of member starts from 1,
        //   thus we add one more element to the container.
        group = new int[size + 1];
        rank = new int[size + 1];
        for (int i = 0; i < size + 1; ++i) {
            group[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * return the group id that the person belongs to.
     */
    public int find(int person) {
        if (group[person] != person) {
            group[person] = find(group[person]);
        }
        return group[person];
    }

    /**
     * Join the groups together.
     * return:
     * false when the two persons belong to the same group already,
     * otherwise true
     */
    public boolean union(int person1, int person2) {
        int group1 = find(person1);
        int group2 = find(person2);
        if (group1 == group2) {
            return false;
        }

        // attach the group of lower rank to the one with higher rank
        if (rank[group1] > rank[group2]) {
            group[group2] = group1;
        } else if (rank[group1] < rank[group2]) {
            group[group1] = group2;
        } else {
            group[group1] = group2;
            rank[group2] += 1;
        }

        return true;
    }
}
```

---

# Detailed Walkthrough of Union-Find

## `group[]`

Stores the parent / component representative for each node.

Initially:

```java
group[i] = i;
```

so every node starts in its own component.

## `rank[]`

Stores an approximate tree depth.
It is used to keep the Union-Find trees balanced.

## `find(person)`

Uses path compression:

```java
if (group[person] != person) {
    group[person] = find(group[person]);
}
```

This flattens the structure and speeds up future lookups.

## `union(person1, person2)`

If the nodes are already in the same group, return `false`.

Otherwise merge them, using union by rank to keep trees shallow, and return `true`.

This return value is especially convenient in Kruskal’s algorithm, because it directly tells us whether the edge was useful.

---

# Java Implementation: Kruskal’s Algorithm

```java
class Solution {
    public int minCostToSupplyWater(int n, int[] wells, int[][] pipes) {
        List<int[]> orderedEdges = new ArrayList<>(n + 1 + pipes.length);

        // add the virtual vertex (index with 0) along with the new edges.
        for (int i = 0; i < wells.length; ++i) {
            orderedEdges.add(new int[]{0, i + 1, wells[i]});
        }

        // add the existing edges
        for (int i = 0; i < pipes.length; ++i) {
            int[] edge = pipes[i];
            orderedEdges.add(edge);
        }

        // sort the edges based on their cost
        Collections.sort(orderedEdges, (a, b) -> a[2] - b[2]);

        // iterate through the ordered edges
        UnionFind uf = new UnionFind(n);
        int totalCost = 0;
        for (int[] edge : orderedEdges) {
            int house1 = edge[0];
            int house2 = edge[1];
            int cost = edge[2];
            // determine if we should add the new edge to the final MST
            if (uf.union(house1, house2)) {
                totalCost += cost;
            }
        }

        return totalCost;
    }
}
```

---

# Detailed Walkthrough of Kruskal’s Code

## Step 1: Build the edge list

```java
List<int[]> orderedEdges = new ArrayList<>(n + 1 + pipes.length);
```

This will contain all edges in the transformed graph.

---

## Step 2: Add virtual well edges

```java
for (int i = 0; i < wells.length; ++i) {
    orderedEdges.add(new int[]{0, i + 1, wells[i]});
}
```

This means:

- choosing edge `(0, i+1, wells[i])` corresponds to digging a well in house `i+1`

---

## Step 3: Add all pipe edges

```java
for (int i = 0; i < pipes.length; ++i) {
    int[] edge = pipes[i];
    orderedEdges.add(edge);
}
```

Unlike Prim’s adjacency list, Kruskal only needs a flat edge list.

---

## Step 4: Sort all edges by cost

```java
Collections.sort(orderedEdges, (a, b) -> a[2] - b[2]);
```

This is the greedy core of Kruskal’s algorithm.

We always consider the cheapest remaining edge first.

---

## Step 5: Process edges in sorted order

```java
for (int[] edge : orderedEdges) {
    int house1 = edge[0];
    int house2 = edge[1];
    int cost = edge[2];
    if (uf.union(house1, house2)) {
        totalCost += cost;
    }
}
```

If `union(...)` returns `true`, that means the edge connected two previously separate components and should be included in the MST.

If it returns `false`, the edge would create a cycle and is skipped.

---

# Why Kruskal’s Algorithm Is Correct Here

Kruskal’s correctness follows from MST theory:

> the cheapest edge that connects two different components is always safe to add

By processing edges globally in sorted order and only adding those that connect distinct components, Kruskal builds a minimum spanning tree.

Since we included the virtual well edges, the resulting MST gives the cheapest mix of:

- well-building choices
- pipe-laying choices

---

# Complexity Analysis of Kruskal’s Algorithm

Let:

- `N` = number of houses
- `M` = number of pipes

## Time Complexity

### Build edge list

There are:

- `N` virtual edges
- `M` pipe edges

So building the list takes:

```text
O(N + M)
```

### Sort edges

Sorting all `N + M` edges takes:

```text
O((N + M) log(N + M))
```

### Union-Find operations

We process each edge once.
Each union/find operation is nearly constant amortized time, often written as:

```text
O(log* N)
```

or more classically inverse-Ackermann-like.

So across all edges, this is effectively linear:

```text
O((N + M) log* N)
```

The sorting step dominates.

So total time complexity is:

```text
O((N + M) log(N + M))
```

---

## Space Complexity

We store:

- edge list: `O(N + M)`
- Union-Find arrays: `O(N)`

So total space complexity is:

```text
O(N + M)
```

---

# Comparing Prim and Kruskal

## Prim’s Algorithm

### Strengths

- grows one connected component naturally
- works well with adjacency lists
- intuitive when you think in terms of “expand the current water network”

### Data structures

- adjacency list
- min-heap
- visited/MST set

### Complexity

```text
Time:  O((N + M) log(N + M))
Space: O(N + M)
```

---

## Kruskal’s Algorithm

### Strengths

- very natural when the graph is given as an edge list
- elegant with Union-Find
- easy to reason about as “sort edges, add safe ones”

### Data structures

- edge list
- sorting
- Union-Find

### Complexity

```text
Time:  O((N + M) log(N + M))
Space: O(N + M)
```

---

# Which Approach Should You Prefer?

Both are correct and efficient.

## Prefer Prim when:

- you already have or want an adjacency-list graph
- you like the “grow from a starting node” intuition

## Prefer Kruskal when:

- the input is naturally edge-based
- you are comfortable with Union-Find
- you want a very interview-friendly MST solution

In many interview settings, Kruskal is especially attractive because the transformed graph is easy to express as a list of weighted edges.

---

# Final Takeaway

The key insight in this problem is the **virtual node transformation**.

Once we add:

- one virtual node `0`
- one edge `(0, house, wellCost)` for every house

the original water-distribution problem becomes a pure MST problem.

From there, we can solve it with any standard MST algorithm:

- Prim’s algorithm
- Kruskal’s algorithm

Both give the minimum total cost of supplying water to all houses.

---

# Summary

## Main Trick

Convert well costs into edge costs by adding a virtual node `0`.

## Transformed Graph

- node `0` = virtual water source
- edge `(0, i)` = build well in house `i`
- edge `(u, v)` = pipe between houses

## Goal After Transformation

Find the Minimum Spanning Tree of the transformed graph.

## Approach 1: Prim’s Algorithm

- start from node `0`
- use heap to greedily expand MST
- **Time:** `O((N + M) log(N + M))`
- **Space:** `O(N + M)`

## Approach 2: Kruskal’s Algorithm

- build all edges
- sort by cost
- use Union-Find to add only safe edges
- **Time:** `O((N + M) log(N + M))`
- **Space:** `O(N + M)`

## Recommended Understanding

This is best viewed as a **graph transformation + MST** problem.
