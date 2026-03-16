# Shortest Path Visiting All Nodes — Exhaustive Solution Notes

## Overview

This problem asks for the **length of the shortest path that visits every node in an undirected connected graph**.

A few details are important:

- You may start at **any node**
- You may **revisit nodes**
- You may **reuse edges**
- The graph is connected
- `n <= 12`, which is very small

That last constraint is the biggest clue.

Because `n` is so small, we can afford to explore states involving:

- the current node
- the set of visited nodes

This leads to the classic state:

```text
(node, mask)
```

where `mask` is a bitmask representing which nodes have been visited.

This write-up explains two approaches:

1. **DFS + Memoization (Top-Down DP)**
2. **Breadth-First Search (BFS)**

In practice, BFS is the cleaner and more standard solution for this problem.

---

## Problem Restatement

Given an undirected connected graph with `n` nodes labeled from `0` to `n - 1`, return the length of the shortest path that visits every node.

The path:

- may start at any node
- may revisit nodes
- may reuse edges

You only need the path length, not the actual path.

---

# Why Bitmasking Is Natural Here

At any point in the traversal, the only information that matters is:

1. **which node we are currently at**
2. **which nodes we have visited so far**

Since `n <= 12`, there are at most:

```text
2^n
```

different visited sets.

A bitmask is perfect for representing that set.

If bit `i` is `1`, then node `i` has been visited.

For example, if `n = 5` and the visited set is `{0, 2, 4}`, then:

```text
mask = 10101 (binary) = 21
```

---

## Useful Bit Operations

### Mark node `i` as visited

```text
mask | (1 << i)
```

This ensures the `i`-th bit becomes `1`.

### Check whether node `i` has been visited

```text
(mask & (1 << i)) != 0
```

### Flip the `i`-th bit

```text
mask ^ (1 << i)
```

This changes the bit from `0` to `1` or `1` to `0`.

---

# State Definition

The fundamental state is:

```text
(node, mask)
```

meaning:

- we are currently standing at `node`
- the set of visited nodes is `mask`

This state is sufficient because the future path only depends on the current position and which nodes have already been seen.

---

# Approach 1: DFS + Memoization (Top-Down DP)

## Intuition

Because `n` is small, we can recursively explore all possible states.

The difficulty is that many states repeat, and the graph is undirected, so naive DFS would cycle forever.

Memoization solves the repeated-subproblem issue, and careful caching avoids infinite bouncing between neighboring states.

This approach works by thinking **backward**:

- imagine that we are currently at some state `(node, mask)`
- we want to know the shortest path length needed to reach a starting state, where only one node was visited

That is why the DFS starts from the "fully visited" mask and works backward.

---

## Why Work Backward?

Let:

```text
endingMask = (1 << n) - 1
```

This is the bitmask where all `n` bits are set to `1`, meaning all nodes have been visited.

For example, if `n = 5`:

```text
endingMask = 11111 (binary) = 31
```

If we compute:

```text
dp(node, endingMask)
```

for every node, then we are effectively asking:

> if a shortest valid path ends at `node` after visiting all nodes, how long is that path?

Since the optimal full path may end at any node, we compute this for all nodes and take the minimum.

---

## Base Case

When does the DFS stop?

If `mask` has only one bit set, then that means only one node has been visited.

That corresponds to a valid starting state.

So:

```text
if mask has only one bit set:
    return 0
```

Because if we are already at a starting state, no more edges are needed.

A standard trick to check whether `mask` has exactly one bit set is:

```text
(mask & (mask - 1)) == 0
```

This works because subtracting 1 clears the lowest set bit and turns all lower zeros into ones. The AND becomes zero only for powers of two.

---

## Recurrence

Suppose we are at state `(node, mask)`.

We want to move backward to a previous state involving some neighbor.

If `neighbor` is adjacent to `node`, there are two possibilities:

### Case 1: `neighbor` was already visited before arriving at `node`

Then the visited set stays the same:

```text
dp(neighbor, mask)
```

### Case 2: `node` was visited for the first time when arriving here

Then previously, `node` was not yet marked visited, so we remove its bit:

```text
dp(neighbor, mask ^ (1 << node))
```

So the recurrence is:

```text
dp(node, mask) =
    1 + min(
        dp(neighbor, mask),
        dp(neighbor, mask ^ (1 << node))
    )
```

over all neighbors of `node`.

The extra `1` accounts for the edge traversed to move between the two states.

---

## Why Infinity Is Written into Cache Early

Because the graph is undirected, recursive transitions can bounce back and forth.

For example:

```text
(A, mask) -> (B, mask) -> (A, mask) -> ...
```

To avoid infinite recursion, the implementation writes a large value like infinity into the cache **before** recursing to neighbors.

This acts as a temporary "in progress" mark and prevents immediate cycles from recursing forever.

---

## Algorithm

1. Let `n = graph.length`
2. Let:
   ```text
   endingMask = (1 << n) - 1
   ```
3. Create a cache:
   ```text
   cache[node][mask]
   ```
4. Define recursive function:
   ```text
   dp(node, mask)
   ```
5. Base case:
   - if `mask` has only one bit set, return `0`
6. If the state is already cached, return it
7. Otherwise:
   - initialize cache entry to a large value
   - for each neighbor:
     - if neighbor is present in the mask:
       - try both:
         ```text
         dp(neighbor, mask)
         dp(neighbor, mask ^ (1 << node))
         ```
       - take the minimum and add `1`
8. Return the minimum over all end nodes:
   ```text
   min(dp(node, endingMask))
   ```

---

## Java Implementation — DFS + Memoization

```java
class Solution {
    private int[][] cache;
    private int endingMask;

    public int dp(int node, int mask, int[][] graph) {
        if (cache[node][mask] != 0) {
            return cache[node][mask];
        }

        if ((mask & (mask - 1)) == 0) {
            return 0;
        }

        cache[node][mask] = Integer.MAX_VALUE - 1;

        for (int neighbor : graph[node]) {
            if ((mask & (1 << neighbor)) != 0) {
                int alreadyVisited = dp(neighbor, mask, graph);
                int notVisited = dp(neighbor, mask ^ (1 << node), graph);
                int betterOption = Math.min(alreadyVisited, notVisited);
                cache[node][mask] = Math.min(cache[node][mask], 1 + betterOption);
            }
        }

        return cache[node][mask];
    }

    public int shortestPathLength(int[][] graph) {
        int n = graph.length;
        endingMask = (1 << n) - 1;
        cache = new int[n + 1][endingMask + 1];

        int best = Integer.MAX_VALUE;
        for (int node = 0; node < n; node++) {
            best = Math.min(best, dp(node, endingMask, graph));
        }

        return best;
    }
}
```

---

## Complexity Analysis — DFS + Memoization

Let `N` be the number of nodes.

### Number of States

There are:

- `N` possible current nodes
- `2^N` possible masks

So total states:

```text
O(N × 2^N)
```

### Transition Cost

At each state, we loop through the neighbors of the current node.

In the worst case of a complete graph, each node has `O(N)` neighbors.

So total time complexity is:

```text
O(2^N × N^2)
```

### Space Complexity

The cache stores all states:

```text
O(N × 2^N)
```

So the space complexity is:

```text
O(N × 2^N)
```

---

# Approach 2: Breadth-First Search (BFS)

## Intuition

This is the most natural approach for shortest path problems.

Each state is again:

```text
(node, mask)
```

But now we search **forward** from all possible starting nodes simultaneously.

Why simultaneously?

Because the problem allows us to start at any node. So instead of running BFS from every node separately, we can put all starting states into the queue at once.

That means the initial states are:

```text
(0, 1 << 0)
(1, 1 << 1)
(2, 1 << 2)
...
(n-1, 1 << (n-1))
```

Each one represents:

- currently at node `i`
- only node `i` has been visited so far

Then BFS explores all paths by increasing length.

As soon as we reach a state whose mask is:

```text
endingMask = (1 << n) - 1
```

we know we have found the shortest path visiting all nodes.

---

## Why BFS Guarantees Optimality

Every move along an edge costs exactly `1`.

So this is an unweighted shortest path problem in the expanded state graph.

BFS explores states in order of increasing number of steps.

Therefore, the first time we reach a state with all nodes visited, the corresponding path length is optimal.

---

## State Transition

From state:

```text
(node, mask)
```

for each `neighbor` in `graph[node]`, the next state is:

```text
(neighbor, mask | (1 << neighbor))
```

Why OR?

Because after moving to `neighbor`, we want to mark that neighbor as visited.

Unlike the DFS formulation, there is no need to consider two cases. We always want the destination node marked as visited.

---

## Visited Array

To avoid revisiting the same state and creating cycles, we keep:

```text
seen[node][mask]
```

If a state has already been visited, we skip it.

This prevents infinite loops and redundant work.

---

## Algorithm

1. If the graph has only one node, return `0`
2. Let:
   ```text
   n = graph.length
   endingMask = (1 << n) - 1
   ```
3. Create:
   - `seen[n][endingMask + 1]`
   - queue of states `(node, mask)`
4. Initialize queue with all starting nodes:
   ```text
   (i, 1 << i) for all i
   ```
5. Perform BFS level by level:
   - for each current state:
     - for each neighbor:
       - compute:
         ```text
         nextMask = mask | (1 << neighbor)
         ```
       - if `nextMask == endingMask`, return `steps + 1`
       - if unseen, mark seen and enqueue
6. The graph is guaranteed connected, so BFS will always find an answer

---

## Java Implementation — BFS

```java
import java.util.*;

class Solution {
    public int shortestPathLength(int[][] graph) {
        if (graph.length == 1) {
            return 0;
        }

        int n = graph.length;
        int endingMask = (1 << n) - 1;
        boolean[][] seen = new boolean[n][endingMask + 1];
        ArrayList<int[]> queue = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            queue.add(new int[] {i, 1 << i});
            seen[i][1 << i] = true;
        }

        int steps = 0;

        while (!queue.isEmpty()) {
            ArrayList<int[]> nextQueue = new ArrayList<>();

            for (int i = 0; i < queue.size(); i++) {
                int[] currentPair = queue.get(i);
                int node = currentPair[0];
                int mask = currentPair[1];

                for (int neighbor : graph[node]) {
                    int nextMask = mask | (1 << neighbor);

                    if (nextMask == endingMask) {
                        return 1 + steps;
                    }

                    if (!seen[neighbor][nextMask]) {
                        seen[neighbor][nextMask] = true;
                        nextQueue.add(new int[] {neighbor, nextMask});
                    }
                }
            }

            steps++;
            queue = nextQueue;
        }

        return -1;
    }
}
```

---

## Complexity Analysis — BFS

Let `N` be the number of nodes.

### Number of States

Same as before:

```text
O(N × 2^N)
```

### Transition Cost

For each state, we iterate through neighbors.

In the worst case of a complete graph, each node has `O(N)` neighbors.

So the total time complexity is:

```text
O(2^N × N^2)
```

### Space Complexity

The `seen` table stores:

```text
O(N × 2^N)
```

states.

The queue may also hold that many states in the worst case.

So total space complexity is:

```text
O(N × 2^N)
```

---

# Comparing the Two Approaches

## DFS + Memoization

### Strengths

- elegant if you like recursive DP
- directly shows the state recurrence
- good for understanding the subproblem structure

### Weaknesses

- works backward, which is less intuitive
- needs care to avoid infinite cycles
- requires trying every ending node

---

## BFS

### Strengths

- more natural for shortest-path problems
- starts from all valid starting states at once
- as soon as it reaches all-nodes-visited, it stops
- usually faster in practice

### Weaknesses

- still exponential in the number of states
- requires explicit queue and seen tracking

---

# Why Multi-Source BFS Is Perfect Here

The problem allows us to start from **any node**.

That is exactly what multi-source BFS is good at.

Instead of guessing the best starting node, we initialize the queue with all possible starting nodes. BFS then explores the state space from all of them simultaneously.

This is one of the cleanest uses of multi-source BFS.

---

# Small Example

Consider the graph:

```text
0 - 1
 \ /
  2
```

Initial queue contains:

```text
(0, 001)
(1, 010)
(2, 100)
```

Suppose we process `(0, 001)`:

- move to `1` → `(1, 011)`
- move to `2` → `(2, 101)`

Similarly other starts expand.

Eventually BFS reaches a state with mask:

```text
111
```

meaning all nodes have been visited.

The first time this happens gives the shortest path length.

---

# Common Mistakes

## 1. Running BFS from Each Node Separately

That works but is unnecessary.

It is cleaner and usually faster to do a **multi-source BFS** starting from all nodes simultaneously.

---

## 2. Using Only `mask` as Visited State

That is incorrect.

The same visited set with different current nodes are different states.

You must track:

```text
(node, mask)
```

not just `mask`.

---

## 3. Forgetting That Revisiting Nodes Is Allowed

This is not a Hamiltonian path problem.

We are allowed to revisit nodes and reuse edges, which is why BFS over `(node, mask)` works.

---

## 4. Confusing `|` and `^`

In BFS, when moving to `neighbor`, we want to ensure the bit is set, so we use:

```text
mask | (1 << neighbor)
```

not XOR.

---

# Final Summary

## State

The central state is:

```text
(node, mask)
```

where:

- `node` = current node
- `mask` = visited nodes

---

## DFS + Memoization

### Idea

Work backward from the all-visited mask to a starting mask with only one visited node.

### Complexity

- Time: `O(2^N × N^2)`
- Space: `O(2^N × N)`

---

## BFS

### Idea

Start from all nodes simultaneously and perform BFS over `(node, mask)` states until all nodes are visited.

### Complexity

- Time: `O(2^N × N^2)`
- Space: `O(2^N × N)`

---

# Best Practical Java Solution

BFS is usually the preferred solution because the problem asks for a shortest path and BFS naturally returns the optimum first.

```java
import java.util.*;

class Solution {
    public int shortestPathLength(int[][] graph) {
        if (graph.length == 1) {
            return 0;
        }

        int n = graph.length;
        int endingMask = (1 << n) - 1;
        boolean[][] seen = new boolean[n][endingMask + 1];
        ArrayList<int[]> queue = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            queue.add(new int[] {i, 1 << i});
            seen[i][1 << i] = true;
        }

        int steps = 0;

        while (!queue.isEmpty()) {
            ArrayList<int[]> nextQueue = new ArrayList<>();

            for (int[] state : queue) {
                int node = state[0];
                int mask = state[1];

                for (int neighbor : graph[node]) {
                    int nextMask = mask | (1 << neighbor);

                    if (nextMask == endingMask) {
                        return steps + 1;
                    }

                    if (!seen[neighbor][nextMask]) {
                        seen[neighbor][nextMask] = true;
                        nextQueue.add(new int[] {neighbor, nextMask});
                    }
                }
            }

            steps++;
            queue = nextQueue;
        }

        return -1;
    }
}
```

This is the standard accepted BFS solution for the problem.
