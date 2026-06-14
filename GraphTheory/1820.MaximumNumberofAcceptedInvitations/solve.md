## Multiple Java Approaches with Detailed Explanation

## Problem Restatement

We have:

- `m` boys
- `n` girls

We are given a binary matrix `grid` of size `m x n`.

If:

```text
grid[i][j] == 1
```

then boy `i` can invite girl `j`.

Rules:

- each boy can invite at most one girl
- each girl can accept at most one invitation
- only allowed boy-girl pairs can be matched

We need to return the **maximum number of accepted invitations**.

---

# Core Graph Interpretation

This is a classic **bipartite matching** problem.

We can model it as a bipartite graph:

- left side = boys
- right side = girls
- edge `(boy i, girl j)` exists if `grid[i][j] == 1`

Now the problem becomes:

> Find the maximum number of pairwise disjoint edges in a bipartite graph.

That is exactly the **maximum bipartite matching** problem.

---

# Why This Is Bipartite Matching

A valid invitation assignment must satisfy:

- one boy matched with at most one girl
- one girl matched with at most one boy

This is precisely the definition of a matching in a graph.

Since the graph has two partitions:

- boys
- girls

it is a bipartite graph.

So we want the **maximum bipartite matching**.

---

# Approaches Covered

We will explain three approaches in Java:

1. **Backtracking / Brute Force**
2. **DFS-based Augmenting Path (Kuhn’s Algorithm)**
3. **Hopcroft-Karp Algorithm**

For interview purposes:

- **Approach 2** is the most common and easiest to implement
- **Approach 3** is the most optimized and important for advanced understanding

---

# Approach 1: Backtracking / Brute Force

## Intuition

The most direct way is to try all possible invitation decisions.

For each boy, we can:

- try matching him with any girl he can invite who is still free
- or skip him

We recursively explore all possibilities and keep the best result.

This works conceptually, but it is far too slow for the given constraints because the search space grows exponentially.

Still, it is useful as a conceptual starting point.

---

## Java Code

```java
class Solution {
    public int maximumInvitations(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        boolean[] usedGirls = new boolean[n];
        return backtrack(grid, 0, usedGirls);
    }

    private int backtrack(int[][] grid, int boy, boolean[] usedGirls) {
        if (boy == grid.length) {
            return 0;
        }

        int best = backtrack(grid, boy + 1, usedGirls); // skip this boy

        for (int girl = 0; girl < grid[0].length; girl++) {
            if (grid[boy][girl] == 1 && !usedGirls[girl]) {
                usedGirls[girl] = true;
                best = Math.max(best, 1 + backtrack(grid, boy + 1, usedGirls));
                usedGirls[girl] = false;
            }
        }

        return best;
    }
}
```

---

## How It Works

At each boy:

- either do not match him
- or try every available girl he can invite

We recursively solve for the remaining boys.

---

## Complexity Analysis

Let:

- `m` = number of boys
- `n` = number of girls

### Time Complexity

Exponential in the worst case.

A rough upper bound is:

```text
O(n^m)
```

or more precisely a large branching recursion tree.

### Space Complexity

```text
O(n + m)
```

for recursion stack and used array.

---

## Why This Is Not Practical

The constraints allow up to:

```text
m, n <= 200
```

An exponential solution is far too slow.

So we need a smarter graph algorithm.

---

# Approach 2: DFS-Based Augmenting Path (Kuhn’s Algorithm)

## Intuition

This is the standard interview solution for maximum bipartite matching.

The key idea is:

- try to match each boy one by one
- if a girl is already matched, try to **reroute** the currently matched boy to some other girl
- if rerouting succeeds, the current boy can take that girl

This rerouting process is called finding an **augmenting path**.

---

# What Is an Augmenting Path?

Suppose we already have some current matching.

If a new boy wants a girl who is already taken, we ask:

> Can the currently matched boy be moved to another girl instead?

If yes, we effectively increase the total matching size by 1.

So for every boy, we run DFS trying to either:

- take a free girl
- or recursively reassign another boy

If successful, the matching grows.

---

# Main Data Structure

We keep an array:

```java
girlMatch[girl] = boy
```

meaning girl `girl` is currently matched with that boy.

If a girl is unmatched:

```java
girlMatch[girl] = -1
```

For each boy, we run DFS with a fresh `visitedGirls` array so we do not loop infinitely inside a single augmenting search.

---

## Java Code

```java
class Solution {
    public int maximumInvitations(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int[] girlMatch = new int[n];
        java.util.Arrays.fill(girlMatch, -1);

        int matches = 0;

        for (int boy = 0; boy < m; boy++) {
            boolean[] visitedGirls = new boolean[n];
            if (canMatch(boy, grid, girlMatch, visitedGirls)) {
                matches++;
            }
        }

        return matches;
    }

    private boolean canMatch(int boy, int[][] grid, int[] girlMatch, boolean[] visitedGirls) {
        for (int girl = 0; girl < grid[0].length; girl++) {
            if (grid[boy][girl] == 0 || visitedGirls[girl]) {
                continue;
            }

            visitedGirls[girl] = true;

            // If girl is free, or her current boy can be reassigned
            if (girlMatch[girl] == -1 || canMatch(girlMatch[girl], grid, girlMatch, visitedGirls)) {
                girlMatch[girl] = boy;
                return true;
            }
        }

        return false;
    }
}
```

---

# Step-by-Step Explanation

## Step 1: Initialize all girls as unmatched

```java
int[] girlMatch = new int[n];
Arrays.fill(girlMatch, -1);
```

Initially, no girl has accepted any invitation.

---

## Step 2: Try to match each boy

```java
for (int boy = 0; boy < m; boy++) {
    boolean[] visitedGirls = new boolean[n];
    if (canMatch(boy, grid, girlMatch, visitedGirls)) {
        matches++;
    }
}
```

For every boy, we try to find one augmenting path.

If successful, total matching size increases by one.

---

## Step 3: DFS logic

Inside `canMatch(...)`:

```java
for (int girl = 0; girl < grid[0].length; girl++) {
```

Check all girls this boy can invite.

If the girl is already tried in this DFS call, skip her.

---

## Step 4: If girl is free, take her

```java
if (girlMatch[girl] == -1)
```

Then simply match her with the current boy.

---

## Step 5: If girl is taken, try to reassign her current boy

```java
canMatch(girlMatch[girl], grid, girlMatch, visitedGirls)
```

This is the heart of augmenting-path matching.

If the already matched boy can move elsewhere, then this girl becomes available for the current boy.

---

# Why This Works

Each successful DFS finds an augmenting path and increases the matching size by exactly 1.

A fundamental theorem in matching theory says:

> A matching is maximum if and only if there is no augmenting path.

Since we keep augmenting until no more boys can increase the matching, the result is maximum.

---

## Complexity Analysis

Let:

- `m` = number of boys
- `n` = number of girls

### Time Complexity

In the worst case, for each boy we may scan all girls and recursively revisit many edges.

A common bound is:

```text
O(m * n * n)
```

or more generally:

```text
O(V * E)
```

for Kuhn’s algorithm on bipartite graphs.

Since `m, n <= 200`, this is fast enough in practice.

### Space Complexity

```text
O(n)
```

for the match array and visited array, plus recursion stack.

If including recursion depth:

```text
O(n + m)
```

---

# Approach 3: Hopcroft-Karp Algorithm

## Intuition

The DFS augmenting-path solution is good, but it finds augmenting paths **one at a time**.

Hopcroft-Karp improves this by finding **multiple shortest augmenting paths in parallel** in each phase.

It does this with two steps:

1. **BFS** builds levels in the alternating graph
2. **DFS** uses those levels to find many disjoint augmenting paths efficiently

This reduces the complexity significantly.

---

# Key Idea of Hopcroft-Karp

Instead of augmenting by one path per round, we:

- layer the graph with BFS from all currently unmatched boys
- then use DFS to find as many shortest augmenting paths as possible

Each BFS+DFS phase can increase the matching by multiple edges.

That makes the algorithm much faster on larger bipartite graphs.

---

# Data Structures

We maintain:

- `pairU[boy]` = matched girl for each boy, or `-1`
- `pairV[girl]` = matched boy for each girl, or `-1`
- `dist[boy]` = BFS layering distance for boys

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maximumInvitations(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            adj.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    adj.get(i).add(j);
                }
            }
        }

        int[] pairU = new int[m];
        int[] pairV = new int[n];
        int[] dist = new int[m];
        Arrays.fill(pairU, -1);
        Arrays.fill(pairV, -1);

        int matching = 0;

        while (bfs(adj, pairU, pairV, dist, m)) {
            for (int u = 0; u < m; u++) {
                if (pairU[u] == -1 && dfs(u, adj, pairU, pairV, dist)) {
                    matching++;
                }
            }
        }

        return matching;
    }

    private boolean bfs(List<List<Integer>> adj, int[] pairU, int[] pairV, int[] dist, int m) {
        Queue<Integer> queue = new LinkedList<>();
        boolean foundAugmentingPath = false;

        for (int u = 0; u < m; u++) {
            if (pairU[u] == -1) {
                dist[u] = 0;
                queue.offer(u);
            } else {
                dist[u] = -1;
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v : adj.get(u)) {
                int matchedBoy = pairV[v];
                if (matchedBoy != -1 && dist[matchedBoy] == -1) {
                    dist[matchedBoy] = dist[u] + 1;
                    queue.offer(matchedBoy);
                }
                if (matchedBoy == -1) {
                    foundAugmentingPath = true;
                }
            }
        }

        return foundAugmentingPath;
    }

    private boolean dfs(int u, List<List<Integer>> adj, int[] pairU, int[] pairV, int[] dist) {
        for (int v : adj.get(u)) {
            int matchedBoy = pairV[v];

            if (matchedBoy == -1 || (dist[matchedBoy] == dist[u] + 1 && dfs(matchedBoy, adj, pairU, pairV, dist))) {
                pairU[u] = v;
                pairV[v] = u;
                return true;
            }
        }

        dist[u] = -1;
        return false;
    }
}
```

---

# Step-by-Step Explanation

## Step 1: Build adjacency list

Instead of repeatedly scanning the whole matrix during DFS, we can precompute the girls each boy can invite.

```java
if (grid[i][j] == 1) {
    adj.get(i).add(j);
}
```

---

## Step 2: BFS level graph

The BFS starts from all currently unmatched boys.

```java
if (pairU[u] == -1) {
    dist[u] = 0;
    queue.offer(u);
}
```

It builds alternating levels through matched/unmatched edges.

If BFS can reach some unmatched girl, then an augmenting path exists.

---

## Step 3: DFS along shortest augmenting paths

The DFS only follows valid layered transitions:

```java
dist[matchedBoy] == dist[u] + 1
```

This ensures we only search shortest augmenting paths from the current BFS layering.

---

## Step 4: Augment multiple paths per phase

Inside the outer loop:

```java
while (bfs(...)) {
    for (int u = 0; u < m; u++) {
        if (pairU[u] == -1 && dfs(...)) {
            matching++;
        }
    }
}
```

Each BFS phase prepares the level graph, and multiple DFS calls can increase the matching several times before rebuilding layers again.

---

# Complexity Analysis

Let:

- `V = m + n`
- `E` = number of `1`s in the matrix

Hopcroft-Karp runs in:

```text
O(E * sqrt(V))
```

This is asymptotically better than the plain DFS augmenting-path approach.

### Space Complexity

```text
O(V + E)
```

for adjacency list, matching arrays, queue, and distance arrays.

---

# Which Approach Should You Use?

## Approach 1: Backtracking

Use only as a conceptual baseline.
Not practical.

## Approach 2: DFS Augmenting Path

Best for interviews.
Simple, standard, and fast enough for this problem.

## Approach 3: Hopcroft-Karp

Best for advanced optimization.
Useful when constraints are larger or when you want the canonical maximum bipartite matching algorithm.

---

# Why This Is Not a Greedy Problem

A tempting but wrong approach is:

- let each boy choose the first available girl he can invite

This can fail badly because a locally convenient choice may block a better overall arrangement.

Maximum bipartite matching requires global coordination, which is exactly why augmenting-path algorithms are needed.

---

# Example Walkthrough for DFS Matching

Suppose:

```text
grid = [
  [1,1,1],
  [1,0,1],
  [0,0,1]
]
```

### Boy 0

Can match with girl 0.

### Boy 1

Also wants girl 0, but girl 0 is taken by boy 0.
Try to reroute boy 0.
Boy 0 can instead take girl 1.
So now:

- boy 1 -> girl 0
- boy 0 -> girl 1

### Boy 2

Can take girl 2

Total = 3

This demonstrates why rerouting is essential.

---

# Final Takeaway

This problem is a textbook **maximum bipartite matching** problem.

The graph is:

- boys on one side
- girls on the other side
- edges represent possible invitations

The best practical solution is the **DFS-based augmenting path algorithm**, while the most optimized classical algorithm is **Hopcroft-Karp**.

---

# Summary

## Problem Type

Maximum bipartite matching

## Approaches

### 1. Backtracking

- brute force
- exponential
- not practical

### 2. DFS Augmenting Path (Kuhn)

- simple and standard
- excellent interview choice
- roughly `O(VE)` / practical `O(m * n * n)`

### 3. Hopcroft-Karp

- advanced optimized solution
- `O(E * sqrt(V))`

## Recommended

Use **Approach 2** in interviews unless the problem explicitly demands maximum optimization.
