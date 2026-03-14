# 2741. Special Permutations

## Problem Restatement

You are given an array `nums` of **distinct** positive integers.

A permutation of `nums` is called **special** if for every adjacent pair:

```text
a, b
```

we have:

```text
a % b == 0   OR   b % a == 0
```

That means every neighboring pair must satisfy a divisibility relationship in at least one direction.

Return the total number of special permutations modulo:

```text
10^9 + 7
```

---

## Key Constraints

```text
2 <= nums.length <= 14
1 <= nums[i] <= 10^9
nums contains distinct integers
```

The most important clue is:

```text
n <= 14
```

That strongly suggests:

- bitmask DP
- subset/state compression
- DFS + memoization over subsets

Because:

```text
2^14 = 16384
```

which is small enough to support subset-based solutions.

---

# Core Insight

A permutation is valid if **every adjacent pair is compatible**.

So before thinking about permutations, we should precompute a graph:

- each number is a node
- edge between `i` and `j` if:
  ```text
  nums[i] % nums[j] == 0 || nums[j] % nums[i] == 0
  ```

Then the problem becomes:

> Count the number of Hamiltonian paths in this compatibility graph.

Why?

Because a permutation uses every number exactly once, and adjacent elements must be connected.

That is exactly a path that visits all nodes once.

---

# Approach 1: Bitmask DP with Last Element (Recommended)

## Idea

Let:

```text
dp[mask][last]
```

be the number of special permutations that:

- use exactly the set of indices in `mask`
- end at index `last`

This is the classic “count permutations with adjacency constraints” DP.

---

## State Definition

- `mask` tells us which elements have already been used
- `last` tells us the final element in the current partial permutation

This is enough because when we append a new number, only the current last number matters for adjacency.

---

## Transition

Suppose we are at:

```text
dp[mask][last]
```

We want to append some unused index `next`.

That is valid only if `last` and `next` are compatible:

```text
nums[last] % nums[next] == 0 || nums[next] % nums[last] == 0
```

Then:

```text
dp[mask | (1 << next)][next] += dp[mask][last]
```

All additions are modulo `10^9 + 7`.

---

## Base Case

A single-element permutation is always valid.

So for every index `i`:

```text
dp[1 << i][i] = 1
```

---

## Final Answer

We need permutations using all elements:

```text
fullMask = (1 << n) - 1
```

So answer is:

```text
sum(dp[fullMask][last]) for all last
```

---

## Why this works

A valid partial permutation is fully described by:

- which elements were used
- what the last element is

Everything before the last element matters only through the count already stored.

That is the DP compression principle.

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;

    public int specialPerm(int[] nums) {
        int n = nums.length;
        int totalMasks = 1 << n;

        boolean[][] can = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && (nums[i] % nums[j] == 0 || nums[j] % nums[i] == 0)) {
                    can[i][j] = true;
                }
            }
        }

        int[][] dp = new int[totalMasks][n];

        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = 1;
        }

        for (int mask = 1; mask < totalMasks; mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0) continue;
                if (dp[mask][last] == 0) continue;

                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    if (!can[last][next]) continue;

                    int nextMask = mask | (1 << next);
                    dp[nextMask][next] = (dp[nextMask][next] + dp[mask][last]) % MOD;
                }
            }
        }

        int fullMask = totalMasks - 1;
        int ans = 0;
        for (int last = 0; last < n; last++) {
            ans = (ans + dp[fullMask][last]) % MOD;
        }

        return ans;
    }
}
```

---

## Complexity

### Precompute compatibility

We check every pair:

```text
O(n^2)
```

### DP states

There are:

```text
2^n * n
```

states.

For each state, we may try all `n` next elements.

So total:

```text
O(2^n * n^2)
```

With `n <= 14`, this is easily fast enough.

Space:

```text
O(2^n * n)
```

---

## Pros

- Standard and elegant
- Very reliable
- Best general solution for this problem

## Cons

- Requires understanding subset DP
- Slightly heavy if you are new to bitmasking

---

# Approach 2: DFS + Memoization on `(mask, last)`

## Idea

This is the top-down version of Approach 1.

Instead of filling a DP table iteratively, define:

```text
dfs(mask, last)
```

= number of special ways to complete the permutation if:

- current used indices are `mask`
- current last chosen element is `last`

Then recursively try all unused compatible next elements.

Memoize the result.

---

## Why this is equivalent to Approach 1

Both use the same state:

- subset of used elements
- last element

The only difference is:

- Approach 1 computes bottom-up
- Approach 2 computes top-down

---

## Recursive Definition

If:

```text
mask == fullMask
```

then all elements are used, so we found one valid permutation:

```text
return 1
```

Otherwise:

- try every unused `next`
- if `can[last][next]`, recurse

---

## Starting the recursion

Since the permutation can start anywhere:

```text
answer = sum(dfs(1 << i, i)) for all i
```

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private static final int MOD = 1_000_000_007;
    private int[] nums;
    private boolean[][] can;
    private int[][] memo;
    private int fullMask;
    private int n;

    public int specialPerm(int[] nums) {
        this.nums = nums;
        this.n = nums.length;
        this.fullMask = (1 << n) - 1;

        can = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && (nums[i] % nums[j] == 0 || nums[j] % nums[i] == 0)) {
                    can[i][j] = true;
                }
            }
        }

        memo = new int[1 << n][n];
        for (int[] row : memo) Arrays.fill(row, -1);

        long ans = 0;
        for (int i = 0; i < n; i++) {
            ans += dfs(1 << i, i);
            ans %= MOD;
        }

        return (int) ans;
    }

    private int dfs(int mask, int last) {
        if (mask == fullMask) return 1;
        if (memo[mask][last] != -1) return memo[mask][last];

        long ways = 0;

        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;
            if (!can[last][next]) continue;

            ways += dfs(mask | (1 << next), next);
            ways %= MOD;
        }

        return memo[mask][last] = (int) ways;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
O(2^n * n^2)
```

Space:

```text
O(2^n * n)
```

plus recursion stack:

```text
O(n)
```

---

## Pros

- Often easier to derive from the problem
- Very clean recursion
- Memoization makes the logic natural

## Cons

- Recursive overhead
- Some people find iterative DP easier to debug

---

# Approach 3: Plain Backtracking / Brute Force Permutations

## Idea

Generate all permutations and check whether each is special.

This is the most direct solution conceptually.

---

## Algorithm

1. Generate all permutations of `nums`
2. For each permutation, scan adjacent pairs
3. If every adjacent pair satisfies divisibility, count it

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private int ans = 0;

    public int specialPerm(int[] nums) {
        boolean[] used = new boolean[nums.length];
        int[] perm = new int[nums.length];
        backtrack(nums, used, perm, 0);
        return ans;
    }

    private void backtrack(int[] nums, boolean[] used, int[] perm, int idx) {
        if (idx == nums.length) {
            ans = (ans + 1) % MOD;
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            if (idx > 0) {
                int prev = perm[idx - 1];
                int cur = nums[i];
                if (prev % cur != 0 && cur % prev != 0) continue;
            }

            used[i] = true;
            perm[idx] = nums[i];
            backtrack(nums, used, perm, idx + 1);
            used[i] = false;
        }
    }
}
```

---

## Complexity

There are:

```text
n!
```

permutations.

Checking adjacency is embedded during construction, but worst-case branching is still factorial.

So time complexity is roughly:

```text
O(n!)
```

Space:

```text
O(n)
```

for recursion and tracking arrays.

---

## Pros

- Very intuitive
- Easy first thought

## Cons

- Too slow for `n = 14`
- Not the intended solution

---

# Approach 4: Backtracking with Compatibility Graph + Pruning

## Idea

A better brute force variant is to first build the compatibility graph and then only move along valid edges.

That reduces branching compared to plain permutation generation.

Still, without memoization, many subproblems repeat.

For example, if you reach the same subset with the same last element through different paths, the number of completions from that point is identical.
That repeated structure is exactly what DP/memoization captures.

So this approach is useful mainly as a stepping stone to DP.

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private boolean[][] can;
    private int n;
    private int ans = 0;

    public int specialPerm(int[] nums) {
        n = nums.length;
        can = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && (nums[i] % nums[j] == 0 || nums[j] % nums[i] == 0)) {
                    can[i][j] = true;
                }
            }
        }

        boolean[] used = new boolean[n];

        for (int i = 0; i < n; i++) {
            used[i] = true;
            dfs(i, 1, used);
            used[i] = false;
        }

        return ans;
    }

    private void dfs(int last, int count, boolean[] used) {
        if (count == n) {
            ans++;
            if (ans >= MOD) ans -= MOD;
            return;
        }

        for (int next = 0; next < n; next++) {
            if (used[next] || !can[last][next]) continue;

            used[next] = true;
            dfs(next, count + 1, used);
            used[next] = false;
        }
    }
}
```

---

## Complexity

Still exponential/factorial in the worst case.

Better than raw permutation generation in sparse compatibility graphs, but still not as good as DP.

---

## Why DP is fundamentally better

Backtracking may revisit the same logical state many times.

Example state:

- used indices = `{0, 2, 4, 5}`
- last index = `4`

No matter how you arrived here, the number of valid completions is identical.

That means the problem has **overlapping subproblems**, which strongly calls for memoization or DP.

---

# Deep Intuition

## Why this is not just a permutation problem

At first glance, it looks like:

> “Generate all permutations and test them.”

But the adjacency condition only depends on neighboring elements, not the full prefix.

That means the partial history can be compressed.

Once we know:

- which elements have been used
- the last chosen element

we know everything needed for future decisions.

That is the exact shape of a **Hamiltonian-path-on-subsets** DP.

---

## Why `last` is enough

Suppose we already built a valid partial permutation.

To decide the next element, we only care whether the next candidate is compatible with the **current last** element.

Earlier elements matter only because they cannot be reused.

So the full order of earlier elements does not need to be stored explicitly.
The mask stores which are used, and `last` stores the endpoint.

That is the state compression insight.

---

# Graph Interpretation

Build an undirected graph where:

- each node is an index in `nums`
- edge exists between `i` and `j` if the two values divide each other in one direction

Then each special permutation is exactly a path visiting every node exactly once.

So the problem becomes:

> Count the number of Hamiltonian paths in this graph.

That perspective makes the DP structure feel much more natural.

---

# Correctness Sketch for Approach 1

We prove that `dp[mask][last]` correctly counts the number of valid partial permutations that use exactly `mask` and end at `last`.

## Base Case

For every single index `i`:

```text
dp[1 << i][i] = 1
```

This is correct because a single-element permutation is always valid.

## Transition

Assume `dp[mask][last]` is correct.

To extend such a permutation, choose an unused `next` such that `last` and `next` are compatible.

Appending `next` keeps the permutation special, so:

```text
dp[mask | (1 << next)][next] += dp[mask][last]
```

This accounts for all valid one-step extensions.

## No overcounting

Each full special permutation has a unique final state:

- full mask of all elements
- its final last element

And each transition sequence corresponds uniquely to its element order.

So no valid permutation is counted more than once.

## Completeness

Every special permutation can be built from its first element by repeatedly appending the next compatible element. Therefore it will be counted by the DP.

Hence the final sum over all possible last elements is exactly the total number of special permutations.

---

# Example Walkthrough

## Example 1

```text
nums = [2, 3, 6]
```

Compatibility:

- `2` and `6` are compatible because `6 % 2 == 0`
- `3` and `6` are compatible because `6 % 3 == 0`
- `2` and `3` are not compatible

So graph looks like:

```text
2 -- 6 -- 3
```

Now valid full paths are:

```text
2 -> 6 -> 3
3 -> 6 -> 2
```

So answer is:

```text
2
```

---

## Example 2

```text
nums = [1, 4, 3]
```

Compatibility:

- `1` is compatible with everyone
- `4` and `3` are not compatible

Graph:

```text
4 -- 1 -- 3
```

Valid full paths:

```text
4 -> 1 -> 3
3 -> 1 -> 4
```

Answer:

```text
2
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
class Solution {
    private static final int MOD = 1_000_000_007;

    public int specialPerm(int[] nums) {
        int n = nums.length;
        int totalMasks = 1 << n;

        boolean[][] can = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && (nums[i] % nums[j] == 0 || nums[j] % nums[i] == 0)) {
                    can[i][j] = true;
                }
            }
        }

        int[][] dp = new int[totalMasks][n];

        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = 1;
        }

        for (int mask = 1; mask < totalMasks; mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0) continue;
                if (dp[mask][last] == 0) continue;

                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    if (!can[last][next]) continue;

                    int nextMask = mask | (1 << next);
                    dp[nextMask][next] = (dp[nextMask][next] + dp[mask][last]) % MOD;
                }
            }
        }

        int fullMask = totalMasks - 1;
        int ans = 0;
        for (int last = 0; last < n; last++) {
            ans = (ans + dp[fullMask][last]) % MOD;
        }

        return ans;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                    |                    Time Complexity | Space Complexity | Recommended |
| ---------- | -------------------------------------------- | ---------------------------------: | ---------------: | ----------- |
| Approach 1 | Bottom-up bitmask DP with last element       |                     `O(2^n * n^2)` |     `O(2^n * n)` | Yes         |
| Approach 2 | Top-down DFS + memo on `(mask, last)`        |                     `O(2^n * n^2)` |     `O(2^n * n)` | Yes         |
| Approach 3 | Generate all permutations and check validity |                            `O(n!)` |           `O(n)` | No          |
| Approach 4 | Backtracking with graph pruning              | Exponential / factorial worst case |           `O(n)` | No          |

---

# Pattern Recognition Takeaway

When you see:

- `n <= 14`
- permutations
- adjacency constraint depending only on neighbors

you should strongly suspect:

- compatibility graph
- bitmask DP with `last`
- Hamiltonian path counting

That is the core pattern behind this problem.

---

# Final Takeaway

The cleanest formulation is:

1. build a graph where two indices are connected if their values divide each other
2. use subset DP to count paths that use a given set of indices and end at a given last index
3. sum all ways that use all indices

That yields an efficient and robust solution within the constraints.
