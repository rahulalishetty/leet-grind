# 3530. Maximum Profit from Valid Topological Order in DAG

## Problem Restatement

You are given:

- a DAG with `n` nodes labeled `0 .. n-1`
- a score array `score`, where `score[i]` is the score of node `i`
- directed edges `u -> v`, meaning `u` must appear before `v` in the order

You must choose a **valid topological ordering** of all nodes.

If a node is placed at 1-based position `pos`, it contributes:

```text
score[node] * pos
```

The total profit is the sum of all node contributions.

Return the **maximum possible profit** over all valid topological orders.

---

## Key Constraints

```text
1 <= n <= 22
1 <= score[i] <= 10^5
The graph is a DAG
```

The most important clue is:

```text
n <= 22
```

That is too large for factorial permutation search, but small enough that:

- subset DP
- bitmask state compression
- DAG dependency masks

become very natural.

---

# Core Insight

We are not just looking for **any** topological order.
We want the one that pushes **larger scores later**, because later positions get bigger multipliers.

But precedence constraints may force some nodes to appear early.

So the problem becomes:

> among all valid topological orders, place nodes to maximize weighted position sum

This is a classic **topological ordering optimization** problem.

---

# A Very Useful Reformulation

Suppose we build the order **from left to right**.

When we place the `k-th` node (1-based), it contributes:

```text
score[node] * k
```

This suggests a DP over:

- which nodes are already placed
- how many positions have already been filled

Since the number of placed nodes is exactly the bitcount of the subset, we do not need to store the position explicitly.

That leads directly to subset DP.

---

# Dependency Mask Preprocessing

For every node `v`, compute a bitmask:

```text
pre[v] = set of all immediate prerequisites of v
```

That means if we have already placed the set `mask`, then node `v` is currently available iff:

```text
(pre[v] & ~mask) == 0
```

In words:

- every prerequisite of `v` is already included in `mask`

This lets us test topological validity in O(1) per node.

---

# Approach 1: Bitmask DP Building the Order Left to Right (Recommended)

## Idea

Let:

```text
dp[mask] = maximum profit achievable after placing exactly the nodes in mask
```

If `mask` contains `k` nodes, then the next placed node will go to position:

```text
k + 1
```

From state `mask`, try every node `v` not yet placed such that all its prerequisites are already in `mask`.

Transition:

```text
dp[mask | (1 << v)] = max(
    dp[mask | (1 << v)],
    dp[mask] + score[v] * (k + 1)
)
```

This explores all valid topological orderings implicitly.

---

## Why this works

Any topological ordering can be built step by step by repeatedly choosing an available node.

At each subset `mask`, the set of legal next nodes is exactly the set whose prerequisites are all satisfied.

So the DP explores every valid topological ordering and takes the best profit.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int maxProfit(int n, int[][] edges, int[] score) {
        int[] pre = new int[n];
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            pre[v] |= (1 << u);
        }

        int totalMasks = 1 << n;
        long[] dp = new long[totalMasks];
        Arrays.fill(dp, Long.MIN_VALUE);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] == Long.MIN_VALUE) continue;

            int pos = Integer.bitCount(mask) + 1;

            for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) != 0) continue;

                // all prerequisites of v must already be in mask
                if ((pre[v] & ~mask) != 0) continue;

                int nextMask = mask | (1 << v);
                dp[nextMask] = Math.max(dp[nextMask], dp[mask] + 1L * score[v] * pos);
            }
        }

        return (int) dp[totalMasks - 1];
    }
}
```

---

## Complexity

There are:

```text
2^n
```

states.

For each state, we may try all `n` nodes.

So time complexity is:

```text
O(n * 2^n)
```

Space complexity:

```text
O(2^n)
```

---

## Important note about feasibility

This is the cleanest conceptual solution.

However, for `n = 22`:

```text
2^22 ≈ 4.19 million
```

That is large but still often feasible in optimized Java if memory is handled carefully.

A `long[] dp` of this size is manageable.

This is very likely the intended exact solution pattern if full bitmask DP is acceptable.

---

# Approach 2: Reverse View — Choose the Last Node First

## Idea

Instead of constructing the order from the front, we can construct it from the back.

Suppose a node is placed last, at position `k`, where `k` is the number of nodes in the current subset.

Which nodes can be placed last?

A node `v` can be the last node in a subset `mask` if:

- `v` is in `mask`
- there is no outgoing edge from `v` to another node still inside `mask \ {v}`

In other words, among the remaining nodes in the subset, `v` must be a sink.

This gives another DP:

```text
dp[mask] = maximum profit achievable using exactly the nodes in mask
```

If `|mask| = k`, and `v` is chosen as the last node, then:

```text
dp[mask] = max(dp[mask ^ (1 << v)] + score[v] * k)
```

provided `v` is valid as the last node of that subset.

---

## Why this is equivalent

Topological ordering means:

- every edge goes from earlier to later nodes

So if we build from the end, the last node must be one with no outgoing dependency to another still-unplaced node.

This is the exact mirror image of “choose an available source next.”

---

## Outgoing Mask Preprocessing

For each node `u`, compute:

```text
out[u] = bitmask of nodes directly reachable by outgoing edges from u
```

Then node `u` can be the last node of subset `mask` iff:

```text
(out[u] & (mask ^ (1 << u))) == 0
```

That means `u` has no outgoing edge to another node still remaining in the subset.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int maxProfit(int n, int[][] edges, int[] score) {
        int[] out = new int[n];
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            out[u] |= (1 << v);
        }

        int totalMasks = 1 << n;
        long[] dp = new long[totalMasks];
        Arrays.fill(dp, Long.MIN_VALUE);
        dp[0] = 0;

        for (int mask = 1; mask < totalMasks; mask++) {
            int pos = Integer.bitCount(mask);

            for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) == 0) continue;

                int prevMask = mask ^ (1 << v);

                // v must be a sink within mask
                if ((out[v] & prevMask) != 0) continue;

                dp[mask] = Math.max(dp[mask], dp[prevMask] + 1L * score[v] * pos);
            }
        }

        return (int) dp[totalMasks - 1];
    }
}
```

---

## Complexity

Same as Approach 1:

```text
O(n * 2^n)
```

Space:

```text
O(2^n)
```

---

## Pros

- Elegant reverse interpretation
- Sometimes easier to reason about profit because multiplier equals subset size

## Cons

- Same asymptotic complexity
- Slightly less intuitive if you naturally think in forward topological order

---

# Approach 3: DFS + Memoization on Used Set

## Idea

This is the top-down version of Approach 1.

Define:

```text
dfs(mask) = maximum profit from the state where nodes in mask are already placed
```

Let:

```text
pos = bitcount(mask) + 1
```

Then for every currently available node `v`:

```text
dfs(mask) = max(
    score[v] * pos + dfs(mask | (1 << v))
)
```

This uses memoization instead of bottom-up table filling.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private int n;
    private int[] pre;
    private int[] score;
    private long[] memo;
    private boolean[] seen;
    private int fullMask;

    public int maxProfit(int n, int[][] edges, int[] score) {
        this.n = n;
        this.score = score;
        this.pre = new int[n];
        this.fullMask = (1 << n) - 1;

        for (int[] e : edges) {
            int u = e[0], v = e[1];
            pre[v] |= (1 << u);
        }

        memo = new long[1 << n];
        seen = new boolean[1 << n];

        return (int) dfs(0);
    }

    private long dfs(int mask) {
        if (mask == fullMask) return 0;
        if (seen[mask]) return memo[mask];
        seen[mask] = true;

        int pos = Integer.bitCount(mask) + 1;
        long best = 0;

        for (int v = 0; v < n; v++) {
            if ((mask & (1 << v)) != 0) continue;
            if ((pre[v] & ~mask) != 0) continue;

            best = Math.max(best, 1L * score[v] * pos + dfs(mask | (1 << v)));
        }

        return memo[mask] = best;
    }
}
```

---

## Complexity

Same state count and transitions:

```text
O(n * 2^n)
```

Space:

```text
O(2^n)
```

plus recursion depth:

```text
O(n)
```

---

## Pros

- Often easier to derive from the recurrence
- Only computes reachable states naturally

## Cons

- Recursive overhead
- Still exponential in `n`

---

# Approach 4: Greedy / Priority-Based Topological Ordering (Why it is not reliable)

## Tempting idea

You might think:

> among currently available topological nodes, always place the one with the smallest score early and save large scores for later

This sounds plausible because later positions have larger multipliers.

And in some simple cases, it works.

---

## Why greedy is dangerous

The problem is that choosing one available node changes **which future nodes become available**.

A node with a small score might unlock many huge-score descendants, so placing it early may be good.
But another small-score node might delay access to even better future arrangements.

So the objective is globally constrained by the DAG structure.
Local “pick smallest available score” is not always enough.

---

## Counterexample intuition

Suppose:

- one low-score node unlocks a very high-score chain
- another low-score node is irrelevant

A purely local greedy choice can choose the wrong low-score node first and delay a better future ordering.

That is exactly why dynamic programming over subsets is needed.

---

# Deep Intuition

## Why later placement favors larger scores

Since profit contribution is:

```text
score[node] * position
```

placing a node later gives it a larger multiplier.

If there were **no edges**, the best order would simply be:

- smallest scores first
- largest scores last

That is just sorting by score ascending.

But the DAG constraints restrict which nodes are allowed to move later.

So the problem is really:

> among all topological orders, find the one that imitates ascending score order as much as the graph permits

This is a very useful mental model.

---

## Why subset DP is natural here

At any point, the only thing that matters is:

- which nodes have already been placed

From that set, we know exactly:

- current position = number of placed nodes + 1
- which nodes are now available

We do **not** need the exact order of earlier choices, only the used set.

That is the hallmark of bitmask DP.

---

## Why the DAG guarantee matters

If the graph had cycles, some subsets would get stuck with no available node.

But because the graph is guaranteed to be a DAG:

- there is always at least one valid topological ordering
- every partial valid ordering can be extended

So the DP never faces an impossible full-instance outcome.

---

# Correctness Sketch for Approach 1

We prove the forward subset DP is correct.

## State definition

`dp[mask]` is the maximum profit achievable after placing exactly the nodes in `mask` into the first `|mask|` positions in some valid topological order.

## Base case

```text
dp[0] = 0
```

With no nodes placed, profit is zero.

## Transition

From state `mask`, the next placed node must be a node `v` not in `mask` whose prerequisites are all already in `mask`.

Placing it at position `|mask| + 1` adds:

```text
score[v] * (|mask| + 1)
```

Thus:

```text
dp[mask ∪ {v}] = max(dp[mask ∪ {v}], dp[mask] + score[v] * (|mask| + 1))
```

This considers every legal next step of a valid topological order.

## Optimal substructure

Any optimal topological order for a larger subset can be decomposed into:

- an optimal order for the earlier subset
- followed by a legal next node

If the earlier subset ordering were not optimal, replacing it with a better one would improve the total profit, contradiction.

Therefore the recurrence is valid.

## Conclusion

By iterating over all subsets and valid transitions, the DP computes the maximum profit over all valid topological orders.

---

# Example Walkthrough

## Example 1

```text
n = 2
edges = [[0,1]]
score = [2,3]
```

Only valid topological order is:

```text
[0,1]
```

Profit:

```text
2 * 1 + 3 * 2 = 2 + 6 = 8
```

So answer is:

```text
8
```

---

## Example 2

```text
n = 3
edges = [[0,1],[0,2]]
score = [1,6,3]
```

Node `0` must come first.

After that, nodes `1` and `2` are both available.

If we choose:

```text
[0,1,2]
```

profit is:

```text
1*1 + 6*2 + 3*3 = 1 + 12 + 9 = 22
```

If we choose:

```text
[0,2,1]
```

profit is:

```text
1*1 + 3*2 + 6*3 = 1 + 6 + 18 = 25
```

So delaying the larger score `6` to the later position gives the better answer.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.Arrays;

class Solution {
    public int maxProfit(int n, int[][] edges, int[] score) {
        int[] pre = new int[n];
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            pre[v] |= (1 << u);
        }

        int totalMasks = 1 << n;
        long[] dp = new long[totalMasks];
        Arrays.fill(dp, Long.MIN_VALUE);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] == Long.MIN_VALUE) continue;

            int pos = Integer.bitCount(mask) + 1;

            for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) != 0) continue;
                if ((pre[v] & ~mask) != 0) continue;

                int nextMask = mask | (1 << v);
                dp[nextMask] = Math.max(dp[nextMask], dp[mask] + 1L * score[v] * pos);
            }
        }

        return (int) dp[totalMasks - 1];
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                               | Time Complexity | Space Complexity | Recommended |
| ---------- | --------------------------------------- | --------------: | ---------------: | ----------- |
| Approach 1 | Forward subset DP by placed nodes       |    `O(n * 2^n)` |         `O(2^n)` | Yes         |
| Approach 2 | Reverse subset DP by choosing last node |    `O(n * 2^n)` |         `O(2^n)` | Yes         |
| Approach 3 | DFS + memoization on used-set           |    `O(n * 2^n)` |         `O(2^n)` | Good        |
| Approach 4 | Greedy topological choice               |    Not reliable |            Small | No          |

---

# Pattern Recognition Takeaway

This problem has a recognizable pattern:

- `n` is around `20`
- need the best ordering
- validity depends only on subset prerequisites
- contribution depends on position in the order

That strongly suggests:

- bitmask DP over subsets
- prerequisite masks
- position derived from subset size

This is one of the standard forms of DAG ordering optimization.

---

# Final Takeaway

The cleanest solution is:

1. precompute prerequisite bitmasks for each node
2. let `dp[mask]` be the best profit after placing nodes in `mask`
3. from each subset, place any currently available node next
4. add `score[node] * (bitcount(mask) + 1)`
5. maximize over all valid transitions

That gives the optimal topological order profit exactly.
