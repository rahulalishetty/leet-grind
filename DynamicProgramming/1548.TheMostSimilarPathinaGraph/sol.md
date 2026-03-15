# Most Similar Path in a Graph — Detailed Summary

## Problem

We are given:

- `n` cities labeled from `0` to `n - 1`
- `m` bidirectional roads
- `names[i]`: the 3-letter uppercase name of city `i`
- `targetPath`: a sequence of target names

The graph is connected.

We must build a **valid path of cities** of the same length as `targetPath` such that:

- the path length is exactly `targetPath.length`
- consecutive cities in the path must have a direct road between them
- the total **edit distance** to `targetPath` is minimum

Here, the edit distance is simply the number of positions where:

```text
names[path[i]] != targetPath[i]
```

If multiple optimal paths exist, any one is acceptable.

---

## Core insight

This is a dynamic programming problem on:

- **position in the target path**
- **current city**

At each step, we decide which city to stand on at that position in the path.

The only constraints are:

1. the chosen city name may or may not match `targetPath[i]`
2. the previous city must be connected to the current city

That gives a clean shortest-path-like DP.

---

# State definition

Let:

```text
L = targetPath.length
```

Define:

```text
dp[i][u]
```

as:

> the minimum edit distance for a valid path of length `i + 1` whose last city is `u`

Meaning:

- we have matched positions `0...i`
- the city chosen at position `i` is `u`

This is the main state.

---

# Mismatch cost

At position `i`, if we choose city `u`, then the local contribution is:

```text
cost(i, u) = 0 if names[u] == targetPath[i]
cost(i, u) = 1 otherwise
```

So the total edit distance is just the sum of these mismatch costs over all positions.

---

# Transition

To end at city `u` at position `i`, the previous city at position `i - 1` must be some neighbor `v` of `u`.

So:

```text
dp[i][u] = cost(i, u) + min(dp[i - 1][v]) over all neighbors v of u
```

That is the full recurrence.

---

# Base case

At position `0`, we can start from any city.

So:

```text
dp[0][u] = 0 if names[u] == targetPath[0]
dp[0][u] = 1 otherwise
```

No parent city is needed there.

---

# Path reconstruction

The DP gives the minimum edit distance, but the problem asks for the actual path.

So we also store:

```text
parent[i][u]
```

which means:

> the city at position `i - 1` that gave the optimal transition into city `u` at position `i`

After filling the DP:

1. find the ending city `end` with minimum `dp[L - 1][end]`
2. backtrack using the `parent` table
3. reverse the recovered sequence

This reconstructs one optimal path.

---

# Why this works

This is a classic optimal-substructure situation.

Suppose an optimal path ends at city `u` at position `i`.

Then the previous city must be one of `u`'s neighbors, say `v`.

If the prefix ending at `v` were not already optimal, we could replace it with a better prefix and get a better full solution, which is impossible.

So the best path ending at `u` at step `i` must come from the best path ending at some neighbor `v` at step `i - 1`.

That is exactly what the recurrence computes.

---

# Graph modeling

Because roads are bidirectional, the graph is undirected.

So build an adjacency list:

```java
graph[a].add(b);
graph[b].add(a);
```

This allows us to efficiently iterate over all valid previous cities for a transition.

---

# Full Java solution

```java
import java.util.*;

class Solution {
    public List<Integer> mostSimilar(int n, int[][] roads, String[] names, String[] targetPath) {
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] road : roads) {
            int a = road[0];
            int b = road[1];
            graph[a].add(b);
            graph[b].add(a);
        }

        int L = targetPath.length;
        int[][] dp = new int[L][n];
        int[][] parent = new int[L][n];

        int INF = 1_000_000_000;

        for (int i = 0; i < L; i++) {
            Arrays.fill(dp[i], INF);
            Arrays.fill(parent[i], -1);
        }

        // Base case: start from any city
        for (int city = 0; city < n; city++) {
            dp[0][city] = names[city].equals(targetPath[0]) ? 0 : 1;
        }

        // Fill DP
        for (int i = 1; i < L; i++) {
            for (int city = 0; city < n; city++) {
                int mismatch = names[city].equals(targetPath[i]) ? 0 : 1;

                for (int prev : graph[city]) {
                    int candidate = dp[i - 1][prev] + mismatch;
                    if (candidate < dp[i][city]) {
                        dp[i][city] = candidate;
                        parent[i][city] = prev;
                    }
                }
            }
        }

        // Choose best ending city
        int endCity = 0;
        for (int city = 1; city < n; city++) {
            if (dp[L - 1][city] < dp[L - 1][endCity]) {
                endCity = city;
            }
        }

        // Reconstruct path
        LinkedList<Integer> answer = new LinkedList<>();
        int cur = endCity;
        for (int i = L - 1; i >= 0; i--) {
            answer.addFirst(cur);
            cur = parent[i][cur];
        }

        return answer;
    }
}
```

---

# Dry run

## Example setup

Suppose:

```text
n = 5
names = ["ATL", "PEK", "LAX", "DXB", "HND"]
targetPath = ["ATL", "DXB", "HND"]
```

and roads allow these transitions:

```text
0-1, 1-3, 3-4, 0-2, 2-3
```

So in graph form:

- `ATL(0)` connected to `PEK(1)` and `LAX(2)`
- `PEK(1)` connected to `ATL(0)` and `DXB(3)`
- `LAX(2)` connected to `ATL(0)` and `DXB(3)`
- `DXB(3)` connected to `PEK(1)`, `LAX(2)`, `HND(4)`
- `HND(4)` connected to `DXB(3)`

We want a path of 3 cities minimizing mismatch with:

```text
["ATL", "DXB", "HND"]
```

---

## Step 1: initialize position 0

For each city `u`:

```text
dp[0][u] = 0 if names[u] == "ATL", else 1
```

So:

- `ATL` matches → `0`
- all others mismatch → `1`

Thus:

```text
dp[0] = [0, 1, 1, 1, 1]
```

---

## Step 2: compute position 1 for target `"DXB"`

Now for each city we compute:

```text
dp[1][city] = mismatch + min(dp[0][neighbor])
```

### City 3 (`DXB`)

- mismatch cost = `0`
- neighbors are cities `1` and `2` and `4`
- previous costs = `1, 1, 1`
- best = `1`

So:

```text
dp[1][3] = 1
parent[1][3] = 1 or 2 or 4
```

### City 1 (`PEK`)

- mismatch cost = `1`
- neighbors: `0`, `3`
- previous costs = `0`, `1`
- best = `0`

So:

```text
dp[1][1] = 1
parent[1][1] = 0
```

Similarly compute all others.

The key point is that each city at layer `i` looks only one step backward to adjacent cities.

---

## Step 3: compute position 2 for target `"HND"`

Again:

```text
dp[2][city] = mismatch + min(dp[1][neighbor])
```

For city `4` (`HND`):

- mismatch cost = `0`
- only neighbor = `3`
- if `dp[1][3] = 1`, then:

```text
dp[2][4] = 1
parent[2][4] = 3
```

This gives a path like:

```text
0 -> 1 -> 4
```

if valid through stored parents, or more likely:

```text
0 -> 1 -> 3 -> 4
```

for a longer example.

For this 3-step target, the best recovered path might be:

```text
[0, 3, 4]
```

if roads permit, otherwise another valid optimal path.

---

# What the DP is really doing

It is finding a minimum-cost walk of exactly `L` nodes in the graph, where:

- being at city `u` at time `i` costs `0` if its name matches `targetPath[i]`
- otherwise costs `1`

So you can think of it as:

- a layered graph with `L` layers
- each layer has `n` possible cities
- transitions exist only along roads
- node cost = mismatch cost

Then the problem becomes a shortest path in that layered DAG-like structure.

That interpretation is often very helpful.

---

# Complexity analysis

Let:

- `n` = number of cities
- `m` = number of roads
- `L` = `targetPath.length`

## Time complexity

For each layer `i` from `1` to `L - 1`, we process every city and iterate through its neighbors.

Across one layer, total neighbor iterations is proportional to the sum of degrees:

```text
2m
```

So total time is:

```text
O(L * m)
```

More explicitly, it is often written as:

```text
O(L * (n + m))
```

but the transition work is dominated by the edges.

---

## Space complexity

We store:

- `dp[L][n]`
- `parent[L][n]`

So:

```text
O(L * n)
```

The adjacency list takes:

```text
O(n + m)
```

So total auxiliary memory is:

```text
O(L * n + n + m)
```

which is usually summarized as:

```text
O(L * n + m)
```

---

# Why greedy does not work

A tempting wrong idea is:

> At each position, choose a city whose name matches `targetPath[i]`.

That is not enough.

Reason:

- a locally matching city may make it impossible or expensive to continue with a valid path later
- the graph connectivity matters across the entire sequence

So this is not a position-wise greedy problem.
It is a sequence optimization problem with graph constraints, which is why DP is the right tool.

---

# Common mistakes

## 1. Ignoring connectivity

Choosing the best matching city independently at each position does not guarantee adjacent chosen cities have a road between them.

Every transition must respect graph edges.

---

## 2. Forgetting to store parents

Without a parent table, you can compute the optimal cost but not reconstruct the actual city order.

---

## 3. Misunderstanding the “edit distance”

In this problem, it is not full Levenshtein distance.

There are no insertions or deletions.

It is only:

```text
number of positions where names[path[i]] != targetPath[i]
```

So the local cost is just `0` or `1`.

---

## 4. Thinking the path cannot repeat cities

The problem only requires a valid path in the graph.

It does **not** say cities cannot repeat.

So revisiting cities is allowed.

That is important.

---

# Slightly more compact Java version

```java
import java.util.*;

class Solution {
    public List<Integer> mostSimilar(int n, int[][] roads, String[] names, String[] targetPath) {
        List<Integer>[] g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        for (int[] e : roads) {
            g[e[0]].add(e[1]);
            g[e[1]].add(e[0]);
        }

        int L = targetPath.length;
        int[][] dp = new int[L][n];
        int[][] parent = new int[L][n];
        int INF = 1_000_000_000;

        for (int i = 0; i < L; i++) {
            Arrays.fill(dp[i], INF);
            Arrays.fill(parent[i], -1);
        }

        for (int u = 0; u < n; u++) {
            dp[0][u] = names[u].equals(targetPath[0]) ? 0 : 1;
        }

        for (int i = 1; i < L; i++) {
            for (int u = 0; u < n; u++) {
                int cost = names[u].equals(targetPath[i]) ? 0 : 1;
                for (int v : g[u]) {
                    int cand = dp[i - 1][v] + cost;
                    if (cand < dp[i][u]) {
                        dp[i][u] = cand;
                        parent[i][u] = v;
                    }
                }
            }
        }

        int end = 0;
        for (int u = 1; u < n; u++) {
            if (dp[L - 1][u] < dp[L - 1][end]) {
                end = u;
            }
        }

        LinkedList<Integer> ans = new LinkedList<>();
        for (int i = L - 1, cur = end; i >= 0; i--) {
            ans.addFirst(cur);
            cur = parent[i][cur];
        }

        return ans;
    }
}
```

---

# Final takeaway

This problem is best viewed as:

- one layer per position in `targetPath`
- one state per city in that layer
- transition only through roads
- mismatch cost `0/1` based on city name vs target name

So the right DP is:

```text
dp[i][u] = minimum cost of a valid length-(i+1) path ending at city u
```

with transition:

```text
dp[i][u] = cost(i, u) + min(dp[i-1][v]) over neighbors v of u
```

Then store parents and backtrack.

## Final complexities

- **Time:** `O(L * m)`
- **Space:** `O(L * n + m)`

This is efficient and fits the problem constraints well.
