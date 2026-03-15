# The Most Similar Path in a Graph — Dynamic Programming Approach (Detailed Summary)

## Problem setting

We are given:

- `n` cities
- an undirected connected graph `roads`
- `names[i]`: the 3-letter name of city `i`
- `targetPath`: a sequence of target names

We need to find a **valid path of cities** of length `targetPath.length` such that:

- consecutive cities in the path are connected by a road
- the total mismatch count against `targetPath` is minimized

The mismatch count here is the **edit distance used by the problem**, which is simply:

```text
number of positions i where names[path[i]] != targetPath[i]
```

This is not full Levenshtein distance.
There are no insertions or deletions. Only position-wise mismatches matter.

---

# Main idea

Instead of directly trying to construct the best path, first solve an easier subproblem:

> For every prefix of `targetPath` and every ending city `v`, what is the minimum possible mismatch count?

That leads naturally to dynamic programming.

---

# DP definition

Let:

```text
k = targetPath.length
```

Define:

```text
dp[i][v]
```

as:

> the minimum edit distance between `targetPath[0..i]` and any valid path of length `i + 1` that ends at vertex `v`

This is the key state.

So:

- `i` tells us how much of the target path we have matched
- `v` tells us the last city of the chosen path

---

# What does `dp[i][v]` really mean?

It helps to read the definition slowly.

Suppose:

```text
targetPath = ["LAX", "ABC", "LAX", "DEF"]
```

and we want:

```text
dp[3][1]
```

This means:

- consider all valid city paths of length `4`
- whose final city is `1`
- compare their city-name sequence against `["LAX", "ABC", "LAX", "DEF"]`
- among all of them, take the minimum mismatch count

So `dp[3][1]` is **not one path**.
It is the best possible cost among all such paths.

---

# Example intuition

Suppose city names are such that these paths end at city `1`:

```text
[2,0,3,1] -> ["LAX", "ATL", "DXB", "PEK"]  => edit distance 3
[3,2,0,1] -> ["DXB", "LAX", "ATL", "PEK"]  => edit distance 4
[2,0,2,1] -> ["LAX", "ATL", "LAX", "PEK"]  => edit distance 2
[2,1,2,1] -> ["LAX", "PEK", "LAX", "PEK"]  => edit distance 2
```

Then:

```text
dp[3][1] = 2
```

because `2` is the minimum among all valid choices ending at city `1`.

That is exactly what the DP table stores.

---

# Base case

Consider:

```text
dp[0][v]
```

This means:

- we only need a path of length `1`
- the only possible path ending at `v` is simply `[v]`

So the mismatch is just whether the city name matches the first target word:

```text
dp[0][v] = 0   if names[v] == targetPath[0]
dp[0][v] = 1   otherwise
```

This is the base row of the DP.

---

# Transition

Now suppose `i > 0`.

We want to compute:

```text
dp[i][v]
```

This is the best mismatch count for a valid path of length `i + 1` ending at city `v`.

If the path ends at `v`, then the previous city in the path must be some neighbor `u` of `v`.

So we try every neighbor `u` of `v` and transition from:

```text
dp[i - 1][u]
```

The current position contributes:

```text
mismatch = 0 if names[v] == targetPath[i]
mismatch = 1 otherwise
```

Therefore:

```text
dp[i][v] = mismatch + min(dp[i - 1][u]) over all neighbors u of v
```

This is the core recurrence.

---

# Why the recurrence is correct

To end at city `v` at step `i`, the previous step must have ended at some adjacent city `u`.

There is no other possibility because the path must be valid in the graph.

Among all possible neighbors `u`, we choose the one that gives the minimum previous cost, then add the mismatch contributed by placing city `v` at position `i`.

That is exactly optimal substructure:

- best path ending at `v` now
- comes from best path ending at a valid predecessor `u` before

---

# Path reconstruction

The DP table gives the minimum cost, but the problem asks for the actual path.

So we also store another table:

```text
p[i][v]
```

where:

> `p[i][v]` = the previous vertex `u` that gave the optimal transition into `dp[i][v]`

Then after we compute all DP states:

1. find the city `v` minimizing `dp[k - 1][v]`
2. that city is the last city of the optimal path
3. repeatedly jump backward using `p[i][v]`
4. reverse the collected sequence

That reconstructs one optimal path.

---

# Step-by-step algorithm

Let `k = targetPath.length`.

## 1. Create DP arrays

We need:

- `dp[k][n]` for costs
- `p[k][n]` for parent reconstruction

## 2. Initialize the base row

For each city `v`:

```text
dp[0][v] = 0 if names[v] == targetPath[0], else 1
```

## 3. Fill the DP for positions `1..k-1`

For each position `i`:

- for each valid transition between adjacent cities
- try extending from the previous city into the current city
- update cost and parent if better

## 4. Find best ending city

Take the city `v` with minimum:

```text
dp[k - 1][v]
```

## 5. Reconstruct path

Start from that ending city and walk backward using `p`.

---

# Two equivalent ways to implement transitions

There are two common implementations.

## Style A: adjacency-list view

For each city `v`, iterate over all neighbors `u`.

This directly mirrors the recurrence:

```text
dp[i][v] = mismatch + min(dp[i - 1][u]) over neighbors u
```

## Style B: edge iteration view

Iterate over every road `(a, b)` and treat it as both directions:

- `a -> b`
- `b -> a`

This is exactly what the provided implementation does.

Both are equivalent.

---

# Java implementation from the approach

```java
import java.util.*;

class Solution {
    public List<Integer> mostSimilar(int n, int[][] roads, String[] names,
            String[] targetPath) {
        Integer[][] dp = new Integer[targetPath.length][n];
        int[][] p = new int[targetPath.length][n];

        // initialize DP
        for (int i = 0; i < n; i++) {
            dp[0][i] = names[i].equals(targetPath[0]) ? 0 : 1;
        }

        // calculate DP
        for (int i = 1; i < targetPath.length; i++) {
            Arrays.fill(dp[i], targetPath.length + 1);

            for (int[] road : roads) {
                // consider both directions: (u, v) and (v, u)
                for (int j = 0; j < 2; j++) {
                    int u = road[j];
                    int v = road[j ^ 1];

                    int cur = dp[i - 1][u] + (names[v].equals(targetPath[i]) ? 0 : 1);

                    if (cur < dp[i][v]) {
                        dp[i][v] = cur;
                        p[i][v] = u;
                    }
                }
            }
        }

        List<Integer> lastDP = Arrays.asList(dp[targetPath.length - 1]);

        // last vertex of the optimal path
        int v = lastDP.indexOf(Collections.min(lastDP));

        List<Integer> ans = new ArrayList<>();
        ans.add(v);

        for (int i = targetPath.length - 1; i > 0; i--) {
            v = p[i][v];
            ans.add(v);
        }

        Collections.reverse(ans);
        return ans;
    }
}
```

---

# Cleaner Java version using primitive `int` arrays

The above solution is correct, but this version avoids `Integer[][]` boxing and is a bit cleaner in practice.

```java
import java.util.*;

class Solution {
    public List<Integer> mostSimilar(int n, int[][] roads, String[] names, String[] targetPath) {
        int k = targetPath.length;
        int INF = k + 1;

        int[][] dp = new int[k][n];
        int[][] parent = new int[k][n];

        for (int i = 0; i < k; i++) {
            Arrays.fill(dp[i], INF);
            Arrays.fill(parent[i], -1);
        }

        // Base case
        for (int v = 0; v < n; v++) {
            dp[0][v] = names[v].equals(targetPath[0]) ? 0 : 1;
        }

        // DP transitions
        for (int i = 1; i < k; i++) {
            for (int[] road : roads) {
                int a = road[0];
                int b = road[1];

                // a -> b
                int costToB = dp[i - 1][a] + (names[b].equals(targetPath[i]) ? 0 : 1);
                if (costToB < dp[i][b]) {
                    dp[i][b] = costToB;
                    parent[i][b] = a;
                }

                // b -> a
                int costToA = dp[i - 1][b] + (names[a].equals(targetPath[i]) ? 0 : 1);
                if (costToA < dp[i][a]) {
                    dp[i][a] = costToA;
                    parent[i][a] = b;
                }
            }
        }

        int end = 0;
        for (int v = 1; v < n; v++) {
            if (dp[k - 1][v] < dp[k - 1][end]) {
                end = v;
            }
        }

        LinkedList<Integer> ans = new LinkedList<>();
        int cur = end;
        for (int i = k - 1; i >= 0; i--) {
            ans.addFirst(cur);
            cur = parent[i][cur];
        }

        return ans;
    }
}
```

---

# Dry run on the recurrence

Suppose:

```text
targetPath = ["ATL", "DXB", "HND", "LAX"]
```

and we want to compute:

```text
dp[2][4]
```

This means:

- best mismatch cost for matching `["ATL", "DXB", "HND"]`
- with a valid path of length 3
- ending at city `4`

Let city `4` be named `"HND"`.

Then current mismatch is:

```text
0
```

Now look at all neighbors `u` of city `4`.

If city `4` has neighbors `1` and `2`, then:

```text
dp[2][4] = 0 + min(dp[1][1], dp[1][2])
```

So we do not care about all paths ending at `4` explicitly.
We only care about the best prefix paths ending at its neighbors one step earlier.

That is the DP compression.

---

# Intuition behind edge iteration

The implementation loops over roads rather than over adjacency lists.

Why does that still work?

Because each road `(u, v)` represents two valid transitions in an undirected graph:

- path can go from `u` to `v`
- path can go from `v` to `u`

So for each DP layer `i`, every road contributes two possible transitions from the previous layer.

This is just another way of enumerating all neighbor-based transitions.

---

# Why this is not greedy

A greedy idea like:

> at each position choose a city whose name best matches `targetPath[i]`

does not work.

Reason:

- the next move depends on graph connectivity
- a locally good choice can block or worsen future positions
- the optimal solution depends on the whole sequence, not one step alone

So we need DP over both:

- current position in the target
- current city in the graph

---

# Why revisiting nodes is allowed

In the original problem, the path is simply required to be valid.

That means:

- consecutive cities must be adjacent
- the same city can appear multiple times

The DP above fully allows revisits.

That is why the state only needs `(i, v)` and not a visited-set.

---

# Follow-up: what if each node can be visited only once?

Then the state `(i, v)` is no longer enough.

Why?

Because whether we may move to a city now depends on whether we used it earlier.

So the state must also remember which cities have been visited.

That means something like:

```text
dp[i][v][mask]
```

or equivalently:

```text
(position, currentNode, visitedSet)
```

This becomes a **bitmask DP / Hamiltonian-path style** problem.

That is much more expensive, because the number of visited sets is exponential in `n`.

So the original elegant `O(m * k)` DP works because revisits are allowed.

---

# Complexity analysis

Let:

- `n` = number of cities
- `m` = number of roads
- `k` = `targetPath.length`

## Time complexity

The dominant work is filling the DP.

For each layer `i = 1 .. k-1`, we iterate over all `m` roads, and for each road we process two directions in constant time.

So:

```text
Time = O(m * k)
```

The initialization and reconstruction are smaller and do not change the bound.

---

## Space complexity

We store:

- `dp[k][n]`
- `p[k][n]`

So:

```text
Space = O(n * k)
```

---

# Common mistakes

## 1. Misreading “edit distance”

This problem uses only position-wise mismatch count, not full string-edit distance with insertions/deletions.

## 2. Forgetting both directions of a road

The graph is undirected, so every road must be treated as two transitions.

## 3. Computing only the minimum cost but not storing parents

Without the parent table, you cannot reconstruct the actual path.

## 4. Using a greedy matching strategy

The graph structure invalidates greedy local choices.

---

# Compact recurrence summary

For every position `i` and city `v`:

```text
dp[0][v] = 0 if names[v] == targetPath[0], else 1
```

For `i > 0`:

```text
dp[i][v] = mismatch(v, i) + min(dp[i - 1][u]) over all neighbors u of v
```

where:

```text
mismatch(v, i) = 0 if names[v] == targetPath[i], else 1
```

Also store:

```text
parent[i][v] = the neighbor u that gave the minimum
```

Then backtrack from the best ending city.

---

# Final takeaway

This is a classic dynamic programming problem on a graph.

The winning perspective is:

- one DP layer per position of `targetPath`
- one state per possible ending city
- transitions only along graph edges
- local cost is whether the city name matches the target name at that position

So the DP state is:

```text
dp[i][v] = best mismatch count for prefix targetPath[0..i] ending at city v
```

That leads to an efficient solution with:

```text
Time:  O(m * k)
Space: O(n * k)
```

and a parent table lets us recover the actual optimal path.
