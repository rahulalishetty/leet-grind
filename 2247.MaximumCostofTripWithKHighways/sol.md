# Maximum Cost of a Trip That Uses Exactly `k` Highways — Exhaustive Summary

## Problem Overview

We are given:

- an integer `n` representing the number of cities labeled from `0` to `n - 1`
- a 2D array `highways`, where:

```text
highways[i] = [city1, city2, toll]
```

means there is an undirected highway between `city1` and `city2` with travel cost `toll`

- an integer `k`

We want to go on a trip that:

- crosses **exactly `k` highways**
- may start at **any city**
- visits each city **at most once**

We must return the **maximum total toll cost** of such a trip.

If no such trip exists, return:

```text
-1
```

---

# What does the problem really ask?

A trip that crosses exactly `k` highways and never visits the same city twice is exactly a:

```text
simple path with exactly k edges
```

in an undirected weighted graph.

So the problem becomes:

> Find the maximum total weight of any simple path that uses exactly `k` edges.

---

# Why the “visit each city at most once” condition matters

Without that condition, we could revisit cities and potentially form cycles.

That would make the problem much easier to abuse, because we could keep walking around expensive loops.

But the problem forbids revisiting a city, so the trip must be a **simple path**.

That means the set of visited cities matters, not just the current city.

This is the strongest signal that the state likely needs:

- current city
- set of visited cities
- or equivalently number of used edges so far

That leads naturally to **bitmask dynamic programming**.

---

# Core Insight

A path with exactly `k` highways contains exactly:

```text
k + 1 cities
```

because each highway connects two consecutive cities in the path.

So instead of thinking in terms of “take exactly `k` edges”, we can think in terms of:

- choosing a set of visited cities
- arranging them into a valid path
- with the path ending at some city

That is a standard pattern for subset DP.

---

# Why brute force is too expensive

A naive approach would be:

1. try every starting city
2. do DFS exploring all simple paths of length `k`
3. keep the maximum total toll

That can work for very small limits, but in general it grows exponentially.

If `n` is modest, there is a better way:

- encode visited cities as a bitmask
- use dynamic programming to reuse overlapping subproblems

---

# Dynamic Programming with Bitmask

## State definition

Let:

```text
dp[mask][u]
```

be the maximum total toll of a valid trip that:

- visits exactly the cities in `mask`
- ends at city `u`

Here:

- `mask` is a bitmask of visited cities
- `u` must be included in `mask`

If a state is impossible, we store:

```text
-1
```

or some very negative sentinel.

---

## Why this state is enough

If we know:

- which cities have already been visited
- which city the current path ends at

then we have enough information to decide what next moves are valid.

A next move can go from `u` to some neighbor `v` only if:

- `v` has not been visited yet

This guarantees the path stays simple.

The exact order before reaching `u` does not matter anymore except through the accumulated cost, which is already stored in `dp[mask][u]`.

That is exactly the optimal substructure we need.

---

# Base Case

A trip may start at **any city**.

So for every city `u`, the path containing only that one city has cost `0`.

That means:

```text
dp[1 << u][u] = 0
```

for all `u`.

This corresponds to a trip of:

- `0` edges
- `1` visited city

---

# Transition

Suppose we are at state:

```text
dp[mask][u]
```

and there is a highway:

```text
u <-> v with toll w
```

If city `v` is not yet visited in `mask`, then we may extend the path to `v`.

The new state becomes:

```text
newMask = mask | (1 << v)
dp[newMask][v] = max(dp[newMask][v], dp[mask][u] + w)
```

This is the standard extension step for path-building subset DP.

---

# When do we have exactly `k` highways?

A path that uses exactly `k` edges has exactly `k + 1` cities.

So after computing all DP states, we only care about masks with:

```text
bitcount(mask) = k + 1
```

Among those states, the answer is the maximum of:

```text
dp[mask][u]
```

over all valid ending cities `u`.

If none exists, return `-1`.

---

# Graph Representation

We build an adjacency list from `highways`.

For each highway:

```text
[a, b, toll]
```

add:

- `(b, toll)` to adjacency of `a`
- `(a, toll)` to adjacency of `b`

because the graph is undirected.

---

# Full Algorithm

1. Build adjacency list for the graph
2. Create DP table:

```text
dp[mask][u]
```

initialized to `-1` 3. For each city `u`, initialize:

```text
dp[1 << u][u] = 0
```

4. Iterate over all masks
5. For each ending city `u` in that mask:
   - if `dp[mask][u]` is valid, try extending to each unvisited neighbor `v`
6. After filling the table, look only at masks with exactly `k + 1` visited cities
7. Return the maximum value among all such states
8. If no valid state exists, return `-1`

---

# Java Solution

```java
import java.util.*;

class Solution {
    public int maximumCost(int n, int[][] highways, int k) {
        List<int[]>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] h : highways) {
            int u = h[0], v = h[1], w = h[2];
            graph[u].add(new int[]{v, w});
            graph[v].add(new int[]{u, w});
        }

        int totalMasks = 1 << n;
        int[][] dp = new int[totalMasks][n];
        for (int i = 0; i < totalMasks; i++) {
            Arrays.fill(dp[i], -1);
        }

        // Base case: start at any city with cost 0
        for (int u = 0; u < n; u++) {
            dp[1 << u][u] = 0;
        }

        for (int mask = 0; mask < totalMasks; mask++) {
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] == -1) continue;
                if ((mask & (1 << u)) == 0) continue;

                for (int[] edge : graph[u]) {
                    int v = edge[0];
                    int w = edge[1];

                    if ((mask & (1 << v)) != 0) continue; // cannot revisit a city

                    int nextMask = mask | (1 << v);
                    dp[nextMask][v] = Math.max(dp[nextMask][v], dp[mask][u] + w);
                }
            }
        }

        int ans = -1;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) != k + 1) continue;

            for (int u = 0; u < n; u++) {
                ans = Math.max(ans, dp[mask][u]);
            }
        }

        return ans;
    }
}
```

---

# Code Walkthrough

## Step 1: Build graph

```java
List<int[]>[] graph = new ArrayList[n];
for (int i = 0; i < n; i++) {
    graph[i] = new ArrayList<>();
}

for (int[] h : highways) {
    int u = h[0], v = h[1], w = h[2];
    graph[u].add(new int[]{v, w});
    graph[v].add(new int[]{u, w});
}
```

Each `graph[u]` stores pairs:

```text
[neighbor, toll]
```

This lets us iterate over all possible next moves from city `u`.

---

## Step 2: DP initialization

```java
int totalMasks = 1 << n;
int[][] dp = new int[totalMasks][n];
for (int i = 0; i < totalMasks; i++) {
    Arrays.fill(dp[i], -1);
}
```

We use:

- `1 << n` masks
- `n` possible ending cities

Value `-1` means unreachable.

---

## Step 3: Base cases

```java
for (int u = 0; u < n; u++) {
    dp[1 << u][u] = 0;
}
```

This means a trip can start at any city.

A path with just one city and no highways has cost `0`.

---

## Step 4: Transition over masks

```java
for (int mask = 0; mask < totalMasks; mask++) {
    for (int u = 0; u < n; u++) {
        if (dp[mask][u] == -1) continue;
        if ((mask & (1 << u)) == 0) continue;

        for (int[] edge : graph[u]) {
            int v = edge[0];
            int w = edge[1];

            if ((mask & (1 << v)) != 0) continue;

            int nextMask = mask | (1 << v);
            dp[nextMask][v] = Math.max(dp[nextMask][v], dp[mask][u] + w);
        }
    }
}
```

For every valid state, we try to append one unvisited neighbor.

That guarantees:

- exactly one new city is added
- no city is revisited

So the resulting structure remains a simple path.

---

## Step 5: Extract answer

```java
int ans = -1;

for (int mask = 0; mask < totalMasks; mask++) {
    if (Integer.bitCount(mask) != k + 1) continue;

    for (int u = 0; u < n; u++) {
        ans = Math.max(ans, dp[mask][u]);
    }
}
```

We only care about masks with `k + 1` cities, because that means the path used exactly `k` highways.

If all such states are impossible, the answer stays `-1`.

---

# Worked Example

Suppose:

```text
n = 4
highways = [
  [0,1,5],
  [1,2,7],
  [2,3,4],
  [0,2,6]
]
k = 2
```

We want a simple path using exactly `2` highways.

That means the trip must contain exactly:

```text
k + 1 = 3 cities
```

Possible paths of length 2:

### Path 0 -> 1 -> 2

Cost:

```text
5 + 7 = 12
```

### Path 1 -> 2 -> 3

Cost:

```text
7 + 4 = 11
```

### Path 1 -> 0 -> 2

Cost:

```text
5 + 6 = 11
```

### Path 0 -> 2 -> 3

Cost:

```text
6 + 4 = 10
```

So the answer is:

```text
12
```

The DP will discover exactly these possibilities by building states over subsets of size 3.

---

# Why the DP is correct

The correctness comes from optimal substructure.

Consider any optimal simple path ending at city `u` and visiting exactly the set of cities in `mask`.

Remove the final city `u`.

Then what remains is:

- a valid simple path
- ending at some previous city `p`
- visiting exactly the cities in `mask - {u}`

Its best possible cost is already represented by:

```text
dp[maskWithoutU][p]
```

So the best path ending at `u` can be obtained by extending some best smaller path by one final edge.

That is exactly the transition:

```text
dp[mask][u] = max(dp[maskWithoutU][p] + edgeCost)
```

Since the DP explores all masks and all valid path extensions, it eventually computes the best possible cost for every valid simple path state.

Taking the best among states with exactly `k + 1` cities yields the correct answer.

---

# Complexity Analysis

Let:

```text
M = 2^n
```

## Time Complexity

There are:

- `2^n` masks
- `n` possible ending cities per mask
- for each state, we may iterate over neighbors of the current city

So a clean upper bound is:

```text
O(2^n * n * n)
```

or more precisely:

```text
O(2^n * E)
```

up to constant factors depending on implementation.

This is exponential in `n`, which is expected for subset DP.

This method is practical only when `n` is relatively small.

---

## Space Complexity

The DP table stores:

```text
2^n * n
```

states.

So the space complexity is:

```text
O(2^n * n)
```

---

# Important Practical Note

This solution is the standard and clean approach when `n` is small enough for bitmask DP.

If `n` were as large as `10^5`, this would be impossible.

So this problem is only suitable for bitmask DP when the hidden or actual constraints on `n` are small.

That is typical for “visit each node at most once” path-maximization problems.

---

# Common Pitfalls

## 1. Forgetting that the path may start at any city

Do not force the path to start at city `0`.

Base case must allow every city as a starting point.

---

## 2. Counting cities instead of highways incorrectly

A path with exactly `k` highways uses exactly:

```text
k + 1 cities
```

This is easy to get off by one.

---

## 3. Allowing revisits

If you do not track visited cities, you may accidentally count cyclic paths, which are illegal.

---

## 4. Using shortest-path algorithms like Dijkstra

This is not a standard shortest-path problem, because:

- the path length must be exactly `k`
- cities cannot be revisited
- we want maximum cost, not minimum

So subset DP is the appropriate tool here.

---

# Alternative DFS + Memo View

You can also think of the same solution recursively.

Define:

```text
dfs(mask, u) = maximum cost of a path that visits mask and ends at u
```

Then try extending from `u` to every unvisited neighbor.

That is the same state definition as the bottom-up DP, just written top-down with memoization.

The bitmask DP shown above is the iterative version of that idea.

---

# Summary Table

| Concept              | Meaning                                       |
| -------------------- | --------------------------------------------- |
| Trip                 | simple path in a weighted graph               |
| Exactly `k` highways | exactly `k + 1` visited cities                |
| State                | `dp[mask][u]`                                 |
| `mask`               | set of visited cities                         |
| `u`                  | current ending city                           |
| Base case            | `dp[1 << u][u] = 0` for all `u`               |
| Transition           | extend to an unvisited neighbor               |
| Answer               | max over states with `bitcount(mask) = k + 1` |
| Time                 | `O(2^n * n^2)` worst-case                     |
| Space                | `O(2^n * n)`                                  |

---

# Final Takeaway

The heart of the problem is recognizing that:

- the trip must be a **simple path**
- exact path length matters
- the visited set matters

That combination strongly suggests **bitmask dynamic programming**.

The clean state is:

```text
dp[mask][u] = best cost of a simple path visiting mask and ending at u
```

From there:

- start from every single city
- extend to unvisited neighbors
- keep only paths with exactly `k + 1` cities

and take the maximum cost.

---

# Full Code Reference

```java
import java.util.*;

class Solution {
    public int maximumCost(int n, int[][] highways, int k) {
        List<int[]>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] h : highways) {
            int u = h[0], v = h[1], w = h[2];
            graph[u].add(new int[]{v, w});
            graph[v].add(new int[]{u, w});
        }

        int totalMasks = 1 << n;
        int[][] dp = new int[totalMasks][n];
        for (int i = 0; i < totalMasks; i++) {
            Arrays.fill(dp[i], -1);
        }

        for (int u = 0; u < n; u++) {
            dp[1 << u][u] = 0;
        }

        for (int mask = 0; mask < totalMasks; mask++) {
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] == -1) continue;
                if ((mask & (1 << u)) == 0) continue;

                for (int[] edge : graph[u]) {
                    int v = edge[0];
                    int w = edge[1];

                    if ((mask & (1 << v)) != 0) continue;

                    int nextMask = mask | (1 << v);
                    dp[nextMask][v] = Math.max(dp[nextMask][v], dp[mask][u] + w);
                }
            }
        }

        int ans = -1;
        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) != k + 1) continue;

            for (int u = 0; u < n; u++) {
                ans = Math.max(ans, dp[mask][u]);
            }
        }

        return ans;
    }
}
```
