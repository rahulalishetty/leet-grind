# Sum of Distances in Tree — Exhaustive Solution Notes

## Overview

This problem asks us to compute, for **every node** in a tree, the sum of distances from that node to all other nodes.

If we do this naively for each node, we might run a full traversal from every starting point, which leads to:

```text
O(N^2)
```

That is too slow for large trees.

The accepted solution uses a very elegant **tree DP + rerooting** technique:

1. a **post-order DFS** to compute subtree sizes and subtree distance sums
2. a **pre-order DFS** to reroot the answer from parent to child in constant time per edge

This gives a linear-time solution.

---

## Problem Idea

Let:

```text
ans[x]
```

be the sum of distances from node `x` to every other node in the tree.

We want to compute `ans[x]` for all nodes.

The main question is:

> If we already know the answer for one node, can we derive the answer for a neighboring node quickly?

That turns out to be the key insight.

---

# Core Intuition

Consider two neighboring nodes:

```text
x -- y
```

If we cut the edge between them, the tree splits into two parts:

- `X`: the component containing `x`
- `Y`: the component containing `y`

Now think about moving the root of the answer from `x` to `y`.

For every node in `Y`, the distance to `y` is **1 less** than the distance to `x`.

For every node in `X`, the distance to `y` is **1 more** than the distance to `x`.

So if `|Y|` is the number of nodes in `Y`, then:

```text
ans[y] = ans[x] - |Y| + |X|
```

Since:

```text
|X| = N - |Y|
```

we get:

```text
ans[y] = ans[x] - |Y| + (N - |Y|)
```

In the rooted-tree version of the algorithm, if `y` is a child of `x`, then:

```text
|Y| = count[y]
```

where `count[y]` is the size of the subtree rooted at `y`.

Thus:

```text
ans[child] = ans[parent] - count[child] + (N - count[child])
```

This is the rerooting formula.

---

# Approach: Subtree Sum and Count

## High-Level Plan

We root the tree arbitrarily, usually at node `0`.

Then we compute two things for every node:

### 1. `count[node]`

The number of nodes in the subtree rooted at `node`.

### 2. `ans[node]` during first DFS

Initially, this stores the sum of distances from `node` to all nodes in its own subtree.

After the first DFS:

- `count[node]` is correct for every node
- `ans[0]` becomes the full answer for the root

Then we use a second DFS to propagate the answer from parent to child using the rerooting formula.

---

# First DFS: Post-Order Traversal

## Goal

Compute:

- subtree sizes
- subtree distance sums

Suppose `child` is a child of `node`.

If we already know:

- `count[child]`
- `ans[child]` = sum of distances from `child` to all nodes in its subtree

then when we move up to `node`:

- all nodes in `child`'s subtree are now 1 farther away from `node` than from `child`
- so their total contribution becomes:

```text
ans[child] + count[child]
```

Thus:

```text
count[node] += count[child]
ans[node] += ans[child] + count[child]
```

This is why post-order traversal works: children are solved before the parent.

---

## Meaning After First DFS

After the first DFS:

- `count[node]` = number of nodes in node's subtree
- `ans[node]` = sum of distances from `node` to all nodes in its subtree

In particular, for the root, the subtree is the entire tree, so:

```text
ans[root]
```

is already the final answer for the root.

---

# Second DFS: Pre-Order Traversal

## Goal

Use the answer for a parent to derive the answer for each child.

If `child` is a child of `node`, then:

- there are `count[child]` nodes in the child’s subtree
- these nodes become 1 **closer** when moving from `node` to `child`
- the remaining `N - count[child]` nodes become 1 **farther**

So:

```text
ans[child] = ans[node] - count[child] + (N - count[child])
```

Once we compute `ans[child]`, we continue DFS into that child.

This is a classic rerooting step.

---

# Why This Works

When moving the root from parent to child:

- every node inside the child’s subtree decreases its distance by 1
- every node outside the child’s subtree increases its distance by 1

That change can be computed using only the subtree size.

So once subtree sizes are known, each reroot transition costs only `O(1)`.

Since every edge is traversed twice overall, the whole algorithm is linear.

---

# Data Structures Used

We use:

- `graph`: adjacency list of the tree
- `count[node]`: subtree size
- `ans[node]`: answer array

The adjacency list is usually implemented as:

```java
List<Set<Integer>>
```

or more commonly:

```java
List<List<Integer>>
```

Both work.

---

# Step-by-Step Algorithm

## Step 1: Build the Graph

Convert the edge list into an adjacency list.

---

## Step 2: Initialize Arrays

- `count[node] = 1` for every node initially, because every node counts itself
- `ans[node] = 0`

---

## Step 3: Run Post-Order DFS

For each child:

- recurse into child
- update:
  ```text
  count[node] += count[child]
  ans[node] += ans[child] + count[child]
  ```

After this, `ans[0]` is the final answer for root `0`.

---

## Step 4: Run Pre-Order DFS

For each child of `node`:

```text
ans[child] = ans[node] - count[child] + N - count[child]
```

Then recurse into that child.

This fills all answers.

---

# Java Implementation

```java
import java.util.*;

class Solution {
    int[] ans, count;
    List<Set<Integer>> graph;
    int N;

    public int[] sumOfDistancesInTree(int N, int[][] edges) {
        this.N = N;
        graph = new ArrayList<>();
        ans = new int[N];
        count = new int[N];
        Arrays.fill(count, 1);

        for (int i = 0; i < N; ++i) {
            graph.add(new HashSet<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        dfs(0, -1);
        dfs2(0, -1);

        return ans;
    }

    public void dfs(int node, int parent) {
        for (int child : graph.get(node)) {
            if (child != parent) {
                dfs(child, node);
                count[node] += count[child];
                ans[node] += ans[child] + count[child];
            }
        }
    }

    public void dfs2(int node, int parent) {
        for (int child : graph.get(node)) {
            if (child != parent) {
                ans[child] = ans[node] - count[child] + N - count[child];
                dfs2(child, node);
            }
        }
    }
}
```

---

# Walkthrough on a Small Example

Consider the tree:

```text
    0
   / \
  1   2
     / \
    3   4
```

Suppose `N = 5`.

---

## First DFS (post-order)

### Leaves

Nodes `1`, `3`, and `4` are leaves:

```text
count[1] = 1, ans[1] = 0
count[3] = 1, ans[3] = 0
count[4] = 1, ans[4] = 0
```

### Node 2

Children are `3` and `4`.

So:

```text
count[2] = 1 + count[3] + count[4] = 3
ans[2] = 0 + (0 + 1) + (0 + 1) = 2
```

This means from node `2`, the sum of distances to nodes in its subtree `{2,3,4}` is `2`.

### Node 0

Children are `1` and `2`.

So:

```text
count[0] = 1 + count[1] + count[2] = 5
ans[0] = 0 + (0 + 1) + (2 + 3) = 6
```

So the answer for root `0` is `6`.

Indeed, distances from `0` are:

```text
to 1 = 1
to 2 = 1
to 3 = 2
to 4 = 2
sum = 6
```

Correct.

---

## Second DFS (rerooting)

### From 0 to 1

```text
ans[1] = ans[0] - count[1] + (N - count[1])
       = 6 - 1 + 4
       = 9
```

Check manually:

```text
dist(1,0)=1
dist(1,2)=2
dist(1,3)=3
dist(1,4)=3
sum = 9
```

Correct.

### From 0 to 2

```text
ans[2] = 6 - 3 + 2 = 5
```

Check manually:

```text
dist(2,0)=1
dist(2,1)=2
dist(2,3)=1
dist(2,4)=1
sum = 5
```

Correct.

And similarly for nodes `3` and `4`.

---

# Why Two DFS Traversals Are Enough

The first DFS computes:

- local subtree information

The second DFS converts:

- local subtree information into global answers

This works because every child’s answer can be derived directly from its parent’s answer once subtree sizes are known.

So we do not need to recompute distances from scratch for every node.

---

# Correctness Intuition

The algorithm is correct because:

1. The post-order DFS correctly aggregates subtree sizes and subtree distance sums.
2. The root’s subtree is the whole tree, so `ans[root]` becomes correct.
3. The pre-order DFS uses a mathematically exact rerooting formula:
   - subtree nodes become 1 closer
   - outside nodes become 1 farther

Thus every node’s total distance sum is computed correctly.

---

# Complexity Analysis

Let `N` be the number of nodes.

## Time Complexity

We traverse every edge:

- once in the first DFS
- once in the second DFS

So total time is:

```text
O(N)
```

because a tree with `N` nodes has `N - 1` edges.

---

## Space Complexity

We store:

- adjacency list: `O(N)`
- `ans` array: `O(N)`
- `count` array: `O(N)`
- recursion stack: `O(N)` in the worst case for a skewed tree

So total space complexity is:

```text
O(N)
```

---

# Common Mistakes

## 1. Trying BFS/DFS from Every Node

That leads to:

```text
O(N^2)
```

which is too slow.

---

## 2. Forgetting to Initialize `count[node] = 1`

Every subtree includes the node itself, so subtree size should start at 1.

---

## 3. Using the Wrong Reroot Formula

The correct reroot transition is:

```text
ans[child] = ans[parent] - count[child] + (N - count[child])
```

Not using subtree size correctly here will break the whole solution.

---

## 4. Revisiting Parent as a Child

Because the graph is undirected, each DFS must ignore the parent to avoid infinite recursion.

---

# Relationship to Tree DP / Rerooting

This is a classic **rerooting DP** problem.

The general pattern is:

1. compute values assuming one fixed root
2. propagate answers to neighboring roots efficiently

This pattern appears in many advanced tree problems.

If you understand this problem deeply, you are learning one of the most important tree-DP techniques.

---

# Final Summary

## Key Arrays

### `count[node]`

Number of nodes in the subtree rooted at `node`.

### `ans[node]`

Eventually, the sum of distances from `node` to all other nodes.

---

## First DFS

Post-order:

```text
count[node] += count[child]
ans[node] += ans[child] + count[child]
```

This computes subtree information.

---

## Second DFS

Pre-order rerooting:

```text
ans[child] = ans[node] - count[child] + (N - count[child])
```

This converts parent answer into child answer.

---

## Complexity

- Time: `O(N)`
- Space: `O(N)`

---

# Best Final Java Solution

```java
import java.util.*;

class Solution {
    int[] ans, count;
    List<Set<Integer>> graph;
    int N;

    public int[] sumOfDistancesInTree(int N, int[][] edges) {
        this.N = N;
        graph = new ArrayList<>();
        ans = new int[N];
        count = new int[N];
        Arrays.fill(count, 1);

        for (int i = 0; i < N; ++i) {
            graph.add(new HashSet<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        dfs(0, -1);
        dfs2(0, -1);

        return ans;
    }

    public void dfs(int node, int parent) {
        for (int child : graph.get(node)) {
            if (child != parent) {
                dfs(child, node);
                count[node] += count[child];
                ans[node] += ans[child] + count[child];
            }
        }
    }

    public void dfs2(int node, int parent) {
        for (int child : graph.get(node)) {
            if (child != parent) {
                ans[child] = ans[node] - count[child] + N - count[child];
                dfs2(child, node);
            }
        }
    }
}
```

This is the standard linear-time rerooting solution for the problem.
