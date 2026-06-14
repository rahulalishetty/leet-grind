# Graph Valid Tree

## Detailed Notes on Graph Theory, DFS, BFS, and Union Find Approaches

## Overview

This write-up explains multiple ways to determine whether a given undirected graph is a **valid tree**.

We assume the input is:

- `n`: the number of nodes labeled from `0` to `n - 1`
- `edges`: the list of undirected edges

A graph is a **tree** if and only if:

1. it is **fully connected**
2. it contains **no cycles**

This document explains three main approaches in detail:

1. **Graph Theory + Iterative Depth-First Search**
2. **Advanced Graph Theory + Search with the `n - 1` Edges Fact**
3. **Advanced Graph Theory + Union Find**

It also discusses useful variants such as:

- recursive DFS
- iterative BFS
- cycle handling in undirected graphs
- path compression and union by size in Union Find

---

# What Does It Mean for a Graph to Be a Tree?

A graph `G` is a tree if and only if both conditions hold:

## 1. Fully Connected

For every pair of nodes in the graph, there is a path between them.

This means the graph has exactly one connected component.

## 2. No Cycles

There is no closed loop.

Equivalently, between any two nodes there is exactly one simple path.

---

# Why Graph Traversal Helps

Depth-first search (DFS) and breadth-first search (BFS) are standard graph traversal methods.

They are useful because:

- if you start from one node and can reach every node, the graph is connected
- if during careful traversal of an undirected graph you find a non-trivial back connection, the graph has a cycle

So graph traversal lets us test both tree conditions.

---

# First Step: Build an Adjacency List

Most graph problems become easier once the input is converted into an adjacency list.

An adjacency list is a list where:

- `adjacencyList[i]` contains all immediate neighbours of node `i`

---

## Java Template for Building the Adjacency List

```java
// Create a new list of lists.
List<List<Integer>> adjacencyList = new ArrayList<>();
// Initialise an empty list for each node.
for (int i = 0; i < n; i++) {
    adjacencyList.add(new ArrayList<>());
}
// Go through the edge list, populating the adjacency list.
for (int[] edge : edges) {
    adjacencyList.get(edge[0]).add(edge[1]);
    adjacencyList.get(edge[1]).add(edge[0]);
}
```

---

## Why an Adjacency List Is the Right Representation

There are three common graph representations:

### 1. Adjacency List

Best when the graph is sparse or moderately dense.
Efficient for iterating through neighbors.

### 2. Adjacency Matrix

Useful mainly when edge density is very high and constant-time edge lookup is important.
But it uses `O(n^2)` space.

### 3. Linked Node Representation

Rarely the best choice in interview problems.
Usually overcomplicated compared with adjacency lists.

For this problem, adjacency list is the right fit.

---

# Approach 1: Graph Theory + Iterative Depth-First Search

## Intuition

A graph is a tree if:

- it is connected
- it has no cycles

We can check both using DFS.

However, because the graph is **undirected**, cycle detection must be done carefully.

If we naïvely say:

```java
if (seen.contains(neighbour)) return false;
```

then we will falsely detect the trivial back-edge:

```text
A -> B -> A
```

which appears naturally in undirected graphs because every undirected edge is stored in both directions.

So we need a way to ignore the immediate edge back to the parent.

---

# Basic Iterative DFS Template

Before adapting DFS for tree checking, recall the standard iterative DFS structure.

```java
// Use a stack to keep track of unexplored nodes.
Stack<Integer> stack = new Stack<>();
stack.push(0);

// Use a set to keep track of already seen nodes to
// avoid infinite looping.
Set<Integer> seen = new HashSet<>();
seen.add(0);

// While there are nodes remaining on the stack...
while (!stack.isEmpty()) {
    int node = stack.pop(); // Take one off to visit.
    // Check for unseen neighbours of this node:
    for (int neighbour : adjacencyList.get(node)) {
        if (seen.contains(neighbour)) {
            continue; // Already seen this node.
        }
        // Otherwise, put this neighbour onto stack
        // and record that it has been seen.
        stack.push(neighbour);
        seen.add(neighbour);
    }
}
```

By itself, this only explores the graph.
It does not yet correctly detect cycles in an undirected graph.

---

# How to Check Connectivity

This part is easy.

If a DFS starting from node `0` discovers all nodes, then the graph is connected.

So after traversal:

```java
return seen.size() == n;
```

This checks whether every node was reached.

---

# Why Undirected Cycle Detection Is Tricky

Suppose nodes `A` and `B` are connected.

In the adjacency list:

- `A` lists `B`
- `B` lists `A`

If you traverse from `A` to `B`, then when looking at `B`’s neighbors, you naturally see `A` again.

That is not a meaningful cycle.
It is just the reverse direction of the same undirected edge.

So we need to ignore this parent edge.

---

# Two Strategies for Avoiding Trivial Cycles

## Strategy 1: Remove Reverse Edges

When traversing edge `A -> B`, remove the reverse edge `B -> A` from the adjacency list.

### Example snippet

```java
// While there are nodes remaining on the stack...
while (!stack.isEmpty()) {
    int node = stack.pop(); // Take one off to visit.
    // Check for unseen neighbours of this node:
    for (int neighbour : adjacencyList.get(node)) {
        // Check if we've already seen this node.
        if (seen.contains(neighbour)) {
            return false;
        }
        // Otherwise, put this neighbour onto stack
        // and record that it has been seen.
        stack.push(neighbour);
        seen.add(neighbour);
        // Remove the link that goes in the opposite direction.
        adjacencyList.get(neighbour).remove(node);
    }
}
```

This works, but it mutates the graph representation.

---

## Strategy 2: Track Parent of Each Node

This is the cleaner strategy.

Instead of just a `seen` set, maintain a `parent` map:

- `parent[x]` tells us which node we came from when entering `x`

Then when exploring neighbors:

- ignore the parent
- if we see any other already-discovered node, there must be a cycle

This is usually the preferred solution.

---

# Parent-Map DFS Logic

```java
// Use a map to keep track of how we got into each node.
Map<Integer, Integer> parent = new HashMap<>();
parent.put(0, -1);

// While there are nodes remaining on the stack...
while (!stack.isEmpty()) {
    int node = stack.pop(); // Take one off to visit.
    // Check for unseen neighbours of this node:
    for (int neighbour : adjacencyList.get(node)) {
        // Don't look at the trivial cycle.
        if (parent.get(node) == neighbour) {
            continue;
        }
        // Check if we've already seen this node.
        if (parent.containsKey(neighbour)) {
            return false; // There must be a cycle.
        }
        // Otherwise, put this neighbour onto stack
        // and record that it has been seen.
        stack.push(neighbour);
        parent.put(neighbour, node);
    }
}
```

Why does this work?

Because each node is entered only once from one parent.
If later we encounter it again from a different edge, that implies a real cycle.

---

# Complete Iterative DFS Solution

```java
public boolean validTree(int n, int[][] edges) {

    List<List<Integer>> adjacencyList = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        adjacencyList.add(new ArrayList<>());
    }
    for (int[] edge : edges) {
        adjacencyList.get(edge[0]).add(edge[1]);
        adjacencyList.get(edge[1]).add(edge[0]);
    }

    Map<Integer, Integer> parent = new HashMap<>();
    parent.put(0, -1);
    Stack<Integer> stack = new Stack<>();
    stack.push(0);

    while (!stack.isEmpty()) {
        int node = stack.pop();
        for (int neighbour : adjacencyList.get(node)) {
            if (parent.get(node) == neighbour) {
                continue;
            }
            if (parent.containsKey(neighbour)) {
                return false;
            }
            stack.push(neighbour);
            parent.put(neighbour, node);
        }
    }

    return parent.size() == n;
}
```

---

# Recursive DFS Variant

The recursive version is more elegant.

Because recursion naturally passes the parent as a parameter, we can use a plain `seen` set instead of a parent map.

```java
class Solution {

    private List<List<Integer>> adjacencyList = new ArrayList<>();
    private Set<Integer> seen = new HashSet<>();


    public boolean validTree(int n, int[][] edges) {

        if (edges.length != n - 1) return false;

        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacencyList.get(edge[0]).add(edge[1]);
            adjacencyList.get(edge[1]).add(edge[0]);
        }

        // We return true iff no cycles were detected,
        // AND the entire graph has been reached.
        return dfs(0, -1) && seen.size() == n;
    }

    public boolean dfs(int node, int parent) {
        if (seen.contains(node)) return false;
        seen.add(node);
        for (int neighbour : adjacencyList.get(node)) {
            if (parent != neighbour) {
                boolean result = dfs(neighbour, node);
                if (!result) return false;
            }
        }
        return true;
    }
}
```

---

# Iterative BFS Variant

BFS uses the same logic as DFS, but with a queue instead of a stack.

```java
public boolean validTree(int n, int[][] edges) {

    List<List<Integer>> adjacencyList = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        adjacencyList.add(new ArrayList<>());
    }
    for (int[] edge : edges) {
        adjacencyList.get(edge[0]).add(edge[1]);
        adjacencyList.get(edge[1]).add(edge[0]);
    }

    Map<Integer, Integer> parent = new HashMap<>();
    parent.put(0, -1);
    Queue<Integer> queue = new LinkedList<>();
    queue.offer(0);

    while (!queue.isEmpty()) {
        int node = queue.poll();
        for (int neighbour : adjacencyList.get(node)) {
            if (parent.get(node) == neighbour) {
                continue;
            }
            if (parent.containsKey(neighbour)) {
                return false;
            }
            queue.offer(neighbour);
            parent.put(neighbour, node);
        }
    }

    return parent.size() == n;
}
```

---

# Complexity Analysis for Approach 1

Let:

- `N` = number of nodes
- `E` = number of edges

### Time Complexity

```text
O(N + E)
```

Why?

- Building the adjacency list takes `O(N + E)`
- The traversal processes each node and edge once overall

So total time is linear in graph size.

### Space Complexity

```text
O(N + E)
```

Why?

- adjacency list uses `O(N + E)`
- stack / queue / parent map / seen set can use `O(N)`

So overall space is linear in graph size.

---

# Approach 2: Advanced Graph Theory + Search with the n - 1 Edges Fact

## Intuition

There is a stronger graph-theory characterization of a tree:

> An undirected graph is a tree if and only if:
>
> 1. it has exactly `n - 1` edges
> 2. it is fully connected

This is extremely useful.

Why?

- If there are fewer than `n - 1` edges, the graph cannot be connected.
- If there are more than `n - 1` edges, the graph must contain a cycle.
- So once we confirm `edges.length == n - 1`, all we need to check is connectivity.

This removes the need for explicit cycle detection logic during traversal.

---

# Algorithm

1. If `edges.length != n - 1`, immediately return `false`.
2. Build the adjacency list.
3. Run DFS or BFS from node `0`.
4. If all nodes are reached, return `true`.
5. Otherwise return `false`.

---

# Iterative DFS Version

```java
public boolean validTree(int n, int[][] edges) {

    if (edges.length != n - 1) return false;

    // Make the adjacency list.
    List<List<Integer>> adjacencyList = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        adjacencyList.add(new ArrayList<>());
    }
    for (int[] edge : edges) {
        adjacencyList.get(edge[0]).add(edge[1]);
        adjacencyList.get(edge[1]).add(edge[0]);
    }

    Stack<Integer> stack = new Stack<>();
    Set<Integer> seen = new HashSet<>();
    stack.push(0);
    seen.add(0);

    while (!stack.isEmpty()) {
        int node = stack.pop();
        for (int neighbour : adjacencyList.get(node)) {
            if (seen.contains(neighbour)) continue;
            seen.add(neighbour);
            stack.push(neighbour);
        }
    }

    return seen.size() == n;
}
```

---

# Recursive DFS Version

```java
class Solution {

    private List<List<Integer>> adjacencyList = new ArrayList<>();
    private Set<Integer> seen = new HashSet<>();


    public boolean validTree(int n, int[][] edges) {

        if (edges.length != n - 1) return false;

        // Make the adjacency list.
        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacencyList.get(edge[0]).add(edge[1]);
            adjacencyList.get(edge[1]).add(edge[0]);
        }

        // Carry out depth first search.
        dfs(0);
        // Inspect result and return the verdict.
        return seen.size() == n;
    }

    public void dfs(int node) {
        if (seen.contains(node)) return;
        seen.add(node);
        for (int neighbour : adjacencyList.get(node)) {
            dfs(neighbour);
        }
    }
}
```

---

# Iterative BFS Version

```java
public boolean validTree(int n, int[][] edges) {

    if (edges.length != n - 1) return false;

    // Make the adjacency list.
    List<List<Integer>> adjacencyList = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        adjacencyList.add(new ArrayList<>());
    }
    for (int[] edge : edges) {
        adjacencyList.get(edge[0]).add(edge[1]);
        adjacencyList.get(edge[1]).add(edge[0]);
    }

    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> seen = new HashSet<>();
    queue.offer(0);
    seen.add(0);

    while (!queue.isEmpty()) {
        int node = queue.poll();
        for (int neighbour : adjacencyList.get(node)) {
            if (seen.contains(neighbour)) continue;
            seen.add(neighbour);
            queue.offer(neighbour);
        }
    }

    return seen.size() == n;
}
```

---

# Why This Approach Is Simpler Than Approach 1

In Approach 1, explicit cycle detection in an undirected graph was the difficult part.

Here, graph theory handles that for us:

- if the edge count is exactly `n - 1`
- and the graph is connected

then the graph must be a tree.

So the traversal only needs to test connectivity.

That makes the implementation simpler.

---

# Complexity Analysis for Approach 2

Because we reject immediately whenever:

```text
E != N - 1
```

the worst case occurs when `E = N - 1`, so `E` is proportional to `N`.

### Time Complexity

```text
O(N)
```

Why?

- adjacency list construction is `O(N + E)` which becomes `O(N)`
- traversal is also `O(N + E)` which becomes `O(N)`

### Space Complexity

```text
O(N)
```

Why?

- adjacency list becomes `O(N)`
- stack / queue / seen set also `O(N)`

So total is linear.

---

# Approach 3: Advanced Graph Theory + Union Find

## Intuition

Approach 2 used the fact:

> A graph is a tree if it has exactly `n - 1` edges and is connected.

Instead of testing connectivity with graph traversal, we can test it using **Union Find**.

Union Find treats each connected component as a set.

Initially:

- every node is in its own set

Each edge attempts to merge two sets.

If an edge connects two nodes already in the same set, then adding that edge forms a cycle.

So Union Find gives us a very elegant way to detect cycles while merging connected components.

---

# How Union Find Represents Sets

Each set is represented as a tree where each node points to its parent.

The root of the tree is the representative of that connected component.

We maintain:

- `parent[node]`
- optionally `size[node]`

---

# Core Operations

## `find(A)`

Returns the root representative of the set containing `A`.

## `union(A, B)`

Merges the sets containing `A` and `B`.

- If they are already in the same set, return `false`
- Otherwise merge them and return `true`

If `union(...)` ever returns `false` while processing an edge, that edge creates a cycle.

---

# Union Find Without Optimizations

```java
class UnionFind {

    private int[] parent;

    // For efficiency, we aren't using makeset, but instead initialising
    // all the sets at the same time in the constructor.
    public UnionFind(int n) {
        parent = new int[n];
        for (int node = 0; node < n; node++) {
            parent[node] = node;
        }
    }

    // The find method, without any optimizations. It traces up the parent
    // links until it finds the root node for A, and returns that root.
    public int find(int A) {
        while (parent[A] != A) {
            A = parent[A];
        }
        return A;
    }

    // The union method, without any optimizations. It returns True if a
    // merge happened, False if otherwise.
    public boolean union(int A, int B) {
        // Find the roots for A and B.
        int rootA = find(A);
        int rootB = find(B);
        // Check if A and B are already in the same set.
        if (rootA == rootB) {
            return false;
        }
        // Merge the sets containing A and B.
        parent[rootA] = rootB;
        return true;
    }
}

class Solution {

    public boolean validTree(int n, int[][] edges) {

        // Condition 1: The graph must contain n - 1 edges.
        if (edges.length != n - 1) return false;

        // Condition 2: The graph must contain a single connected component.
        // Create a new UnionFind object with n nodes.
        UnionFind unionFind = new UnionFind(n);
        // Add each edge. Check if a merge happened, because if it
        // didn't, there must be a cycle.
        for (int[] edge : edges) {
            int A = edge[0];
            int B = edge[1];
            if (!unionFind.union(A, B)) {
                return false;
            }
        }

        // If we got this far, there's no cycles!
        return true;
    }

}
```

Because we already checked `edges.length == n - 1`, the absence of cycles guarantees connectivity.

So returning `true` is enough.

---

# Union Find With Path Compression and Union by Size

These are the standard optimizations.

## Path Compression

After finding a root, redirect every visited node to point directly to that root.

This speeds up future `find(...)` calls.

## Union by Size

Always attach the smaller set under the larger one.

This keeps trees shallow.

---

## Optimized Code

```java
class UnionFind {

    private int[] parent;
    private int[] size; // We use this to keep track of the size of each set.

    // For efficiency, we aren't using makeset, but instead initialising
    // all the sets at the same time in the constructor.
    public UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        for (int node = 0; node < n; node++) {
            parent[node] = node;
            size[node] = 1;
        }
    }

    // The find method, with path compression. There are ways of implementing
    // this elegantly with recursion, but the iterative version is easier for
    // most people to understand!
    public int find(int A) {
        // Step 1: Find the root.
        int root = A;
        while (parent[root] != root) {
            root = parent[root];
        }
        // Step 2: Do a second traversal, this time setting each node to point
        // directly at A as we go.
        while (A != root) {
            int oldRoot = parent[A];
            parent[A] = root;
            A = oldRoot;
        }
        return root;
    }

    // The union method, with optimization union by size. It returns True if a
    // merge happened, False if otherwise.
    public boolean union(int A, int B) {
        // Find the roots for A and B.
        int rootA = find(A);
        int rootB = find(B);
        // Check if A and B are already in the same set.
        if (rootA == rootB) {
            return false;
        }
        // We want to ensure the larger set remains the root.
        if (size[rootA] < size[rootB]) {
            // Make rootB the overall root.
            parent[rootA] = rootB;
            // The size of the set rooted at B is the sum of the 2.
            size[rootB] += size[rootA];
        }
        else {
            // Make rootA the overall root.
            parent[rootB] = rootA;
            // The size of the set rooted at A is the sum of the 2.
            size[rootA] += size[rootB];
        }
        return true;
    }
}

class Solution {

    public boolean validTree(int n, int[][] edges) {

        // Condition 1: The graph must contain n - 1 edges.
        if (edges.length != n - 1) return false;

        // Condition 2: The graph must contain a single connected component.
        // Create a new UnionFind object with n nodes.
        UnionFind unionFind = new UnionFind(n);
        // Add each edge. Check if a merge happened, because if it
        // didn't, there must be a cycle.
        for (int[] edge : edges) {
            int A = edge[0];
            int B = edge[1];
            if (!unionFind.union(A, B)) {
                return false;
            }
        }

        // If we got this far, there's no cycles!
        return true;
    }

}
```

---

# Why Union Find Works Here

Suppose we process every edge.

Each successful union merges two previously separate connected components.

If an edge tries to connect two nodes already in the same component, then it creates a cycle.

Because we also know the graph has exactly `n - 1` edges, if no cycle occurs, the graph must be a tree.

---

# Complexity Analysis for Approach 3

Let:

- `N` = number of nodes
- `E` = number of edges

Since we immediately reject unless:

```text
E = N - 1
```

we can think of `E` as proportional to `N`.

### Time Complexity

With path compression and union by size:

```text
O(N * α(N))
```

where `α(N)` is the inverse Ackermann function.

This function grows so slowly that in practice it is effectively constant.

So Union Find behaves almost like:

```text
O(N)
```

in real use.

### Space Complexity

```text
O(N)
```

because Union Find stores:

- parent array
- size array

both of length `N`.

---

# Why Union Find Can Be Better in Practice

Asymptotic notation ignores constants.

Approach 2 still needs to:

- build an adjacency list
- then traverse it

Union Find skips adjacency-list construction entirely.

It processes the edge list directly.

So even though both are effectively linear, Union Find often has less overhead in practice.

---

# Comparing the Approaches

## Approach 1: Graph Theory + DFS/BFS with explicit cycle handling

### Strengths

- directly shows connectedness and cycle logic
- useful for understanding graph traversal deeply

### Weaknesses

- cycle handling in undirected graphs is subtle
- requires adjacency list construction

---

## Approach 2: `n - 1` edges fact + DFS/BFS connectivity

### Strengths

- simpler than Approach 1
- elegant graph-theory shortcut
- easy to code

### Weaknesses

- still needs adjacency list construction

---

## Approach 3: `n - 1` edges fact + Union Find

### Strengths

- very efficient in practice
- no adjacency list needed
- elegant for cycle detection and component merging

### Weaknesses

- requires knowing Union Find well
- slightly less intuitive if you are new to DSU

---

# Which Approach Should You Prefer?

## For interviews with traversal focus

Use **Approach 2**.

It is usually the cleanest explanation:

- first check `edges.length == n - 1`
- then just verify connectivity

## For interviews where Union Find is welcome

Use **Approach 3**.

It is often the most practical and efficient implementation.

## For learning purposes

Study **Approach 1** carefully, because it teaches the important undirected-cycle issue that many people initially miss.

---

# Final Takeaway

There are two especially important graph-theory facts behind this problem:

1. A graph is a tree iff it is connected and acyclic.
2. For an undirected graph, that is equivalent to:
   - exactly `n - 1` edges
   - connected

Once you know the second fact, the problem becomes much simpler.

Then you can solve it either by:

- checking connectivity with DFS/BFS
- or processing edges with Union Find

Both are strong solutions.

---

# Summary

## Approach 1

Use DFS/BFS and explicitly detect cycles while handling the parent edge in an undirected graph.

- Time: `O(N + E)`
- Space: `O(N + E)`

## Approach 2

Use graph theory:

- if edges are not `n - 1`, return false
- otherwise just check connectivity with DFS/BFS

- Time: `O(N)`
- Space: `O(N)`

## Approach 3

Use Union Find:

- if edges are not `n - 1`, return false
- union all edges
- if any union fails, there is a cycle
- otherwise return true

- Time: `O(N * α(N))`
- Space: `O(N)`

## Recommended

- Use **Approach 2** for the simplest traversal-based solution
- Use **Approach 3** for the most practical high-performance solution
