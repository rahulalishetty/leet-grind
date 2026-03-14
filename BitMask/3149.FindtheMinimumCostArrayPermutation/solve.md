# 3149. Find the Minimum Cost Array Permutation

## Problem Restatement

You are given `nums`, which is a permutation of:

```text
[0, 1, 2, ..., n - 1]
```

You must construct another permutation `perm` of the same values that minimizes:

```text
score(perm) =
|perm[0] - nums[perm[1]]| +
|perm[1] - nums[perm[2]]| +
...
+ |perm[n - 1] - nums[perm[0]]|
```

This is a **cyclic** cost:

- each element contributes relative to the next element in the permutation
- the last element connects back to the first

If multiple permutations have the same minimum score, return the **lexicographically smallest** one.

---

## Key Constraints

```text
2 <= n <= 14
nums is a permutation of [0..n-1]
```

The crucial clue is:

```text
n <= 14
```

That strongly suggests:

- bitmask DP
- traveling-salesman-style subset DP
- DFS + memoization on subsets

Because:

```text
2^14 = 16384
```

which is small enough for state compression.

---

# Core Insight

The score looks unusual at first:

```text
|perm[i] - nums[perm[i+1]]|
```

Notice carefully:

- the current permutation element is used directly
- the next permutation element is used as an index into `nums`

That means if we think of `perm` as a cyclic ordering of nodes, then moving from:

```text
next -> current
```

or equivalently choosing adjacent values in the cycle creates a cost based on the pair.

A very useful way to rewrite the cost is:

For adjacent elements `a = perm[i]` and `b = perm[i+1]`, the contribution is:

```text
|a - nums[b]|
```

And for the closing edge:

```text
|perm[n - 1] - nums[perm[0]]|
```

So the problem becomes:

> Find a Hamiltonian cycle ordering of nodes `0..n-1` minimizing this custom directed edge cost.

Because the cycle can be rotated, and lexicographic order matters, the standard trick is to **fix the first element**.

Since the lexicographically smallest permutation is preferred, and every permutation is a permutation of `0..n-1`, the lexicographically smallest possible first element is always:

```text
0
```

So in the optimal lexicographically smallest answer, we can safely fix:

```text
perm[0] = 0
```

Then we only need to build the rest of the cycle starting from `0` and eventually return to `0`.

That removes rotational ambiguity.

---

# Why fixing `perm[0] = 0` is valid

A cycle can be written starting from any of its elements.

If some optimal cycle does not start with `0`, rotate it so that it does.

That rotated cycle has the same score because the edge relationships remain cyclically identical.

Among all minimum-score cycles, the lexicographically smallest representation must start with the smallest possible first element, which is `0`.

So we only need to search among permutations that start with `0`.

This is the key simplification.

---

# Approach 1: Bitmask DP + Path Reconstruction (Recommended)

## Idea

We fix:

```text
perm[0] = 0
```

Now we need to arrange the remaining nodes exactly once and finally close the cycle back to `0`.

Define the directed edge cost:

```text
cost(a, b) = |a - nums[b]|
```

If our current path ends at `last` and we choose `next` after it, we add:

```text
|last - nums[next]|
```

At the very end, when all nodes are used, we add the closing cost:

```text
|last - nums[0]|
```

because the next element after the final element is `perm[0] = 0`.

---

## DP State

Let:

```text
dp[mask][last]
```

be the minimum additional cost needed to complete the cycle, assuming:

- we have already used exactly the nodes in `mask`
- the current path ends at node `last`
- node `0` is already included in `mask`

Then the recurrence is:

- if all nodes are used, return closing cost `|last - nums[0]|`
- otherwise, try every unused `next`:
  ```text
  |last - nums[next]| + dp[mask | (1 << next)][next]
  ```

Among equal costs, we must choose the lexicographically smallest full permutation.
That means during reconstruction, when multiple `next` choices give the same optimal cost, we choose the **smallest `next`**.

Because the prefix is built left to right, that guarantees lexicographic minimality.

---

## Top-Down Form

This problem is especially natural as memoized DFS:

```text
dfs(mask, last) = min cost to finish from here
```

Then reconstruct the path greedily using the memo table.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private int[] nums;
    private int n;
    private int fullMask;
    private int[][] memo;

    public int[] findPermutation(int[] nums) {
        this.nums = nums;
        this.n = nums.length;
        this.fullMask = (1 << n) - 1;
        this.memo = new int[1 << n][n];

        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        // Start from 0 to ensure lexicographically smallest cycle representation
        dfs(1, 0);

        int[] ans = new int[n];
        ans[0] = 0;

        int mask = 1;
        int last = 0;

        for (int i = 1; i < n; i++) {
            int bestNext = -1;
            int bestCost = Integer.MAX_VALUE;

            for (int next = 0; next < n; next++) {
                if ((mask & (1 << next)) != 0) continue;

                int candidate = Math.abs(last - nums[next]) + dfs(mask | (1 << next), next);

                if (candidate < bestCost) {
                    bestCost = candidate;
                    bestNext = next;
                } else if (candidate == bestCost && next < bestNext) {
                    bestNext = next;
                }
            }

            ans[i] = bestNext;
            mask |= (1 << bestNext);
            last = bestNext;
        }

        return ans;
    }

    private int dfs(int mask, int last) {
        if (mask == fullMask) {
            return Math.abs(last - nums[0]);
        }

        if (memo[mask][last] != -1) {
            return memo[mask][last];
        }

        int best = Integer.MAX_VALUE;

        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;

            int candidate = Math.abs(last - nums[next]) + dfs(mask | (1 << next), next);
            best = Math.min(best, candidate);
        }

        return memo[mask][last] = best;
    }
}
```

---

## Complexity

There are:

```text
2^n * n
```

states.

Each state tries up to `n` next choices.

So time complexity is:

```text
O(2^n * n^2)
```

With `n <= 14`, this is completely feasible.

Space complexity:

```text
O(2^n * n)
```

---

## Why lexicographic reconstruction works

Once the minimum score is known for every state, reconstruction proceeds from left to right.

At state `(mask, last)`, multiple `next` values may preserve optimal total cost.

Choosing the smallest such `next` makes the next position in `perm` as small as possible, which is exactly what lexicographic minimization requires.

Since future choices are then determined recursively the same way, the whole permutation becomes lexicographically smallest among all optimal ones.

---

# Approach 2: Bottom-Up Bitmask DP + Parent Reconstruction

## Idea

We can also solve it iteratively.

Let:

```text
dp[mask][last]
```

be the minimum cost to start at `0`, visit exactly `mask`, and end at `last`.

Here `mask` always contains `0`.

Transition:

If we append `last` after `prev`, then:

```text
dp[mask][last] = min(
    dp[mask ^ (1 << last)][prev] + |prev - nums[last]|
)
```

for all `prev` in `mask` before `last`.

At the end, once all nodes are visited, total cycle cost is:

```text
dp[fullMask][last] + |last - nums[0]|
```

Then pick the best ending `last`.

To get the lexicographically smallest permutation among equal-cost answers, we need careful tie-breaking during parent tracking or reconstruction.

This is why top-down reconstruction is often cleaner, but bottom-up is still valid.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int[] findPermutation(int[] nums) {
        int n = nums.length;
        int fullMask = (1 << n) - 1;
        int INF = (int) 1e9;

        int[][] dp = new int[1 << n][n];
        int[][] parent = new int[1 << n][n];

        for (int[] row : dp) Arrays.fill(row, INF);
        for (int[] row : parent) Arrays.fill(row, -1);

        dp[1][0] = 0; // start from 0

        for (int mask = 1; mask <= fullMask; mask++) {
            if ((mask & 1) == 0) continue; // must include 0

            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0) continue;
                if (dp[mask][last] == INF) continue;

                for (int next = 1; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;

                    int nextMask = mask | (1 << next);
                    int candidate = dp[mask][last] + Math.abs(last - nums[next]);

                    if (candidate < dp[nextMask][next]) {
                        dp[nextMask][next] = candidate;
                        parent[nextMask][next] = last;
                    } else if (candidate == dp[nextMask][next]) {
                        // A full lexicographic tie-break is awkward here.
                        // Parent-only local tie-breaking is not always sufficient,
                        // so this bottom-up version is mainly for cost computation.
                    }
                }
            }
        }

        int bestLast = -1;
        int bestCost = INF;

        for (int last = 0; last < n; last++) {
            if (dp[fullMask][last] == INF) continue;

            int totalCost = dp[fullMask][last] + Math.abs(last - nums[0]);
            if (totalCost < bestCost) {
                bestCost = totalCost;
                bestLast = last;
            }
        }

        // Reconstruct one minimum-cost path (without full lexicographic guarantee
        // from local parent ties alone).
        int[] ans = new int[n];
        int mask = fullMask;
        int idx = n - 1;
        int cur = bestLast;

        while (cur != -1) {
            ans[idx--] = cur;
            int p = parent[mask][cur];
            mask ^= (1 << cur);
            cur = p;
        }

        return ans;
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

---

## Pros

- Iterative
- Good for minimum-cost Hamiltonian path style DP

## Cons

- Lexicographic tie-breaking is trickier than top-down reconstruction
- Not as elegant for this specific problem

---

# Approach 3: DFS + Memo Returning Both Cost and Lexicographic Choice

## Idea

Instead of storing only minimum cost, we can explicitly store the best next decision for each state:

- `memoCost[mask][last]`
- `choice[mask][last]`

During DFS:

1. compute minimum possible remaining cost
2. among equal-cost choices, store the smallest `next`

Then reconstruction becomes trivial.

This is really a refinement of Approach 1, but it is conceptually useful because it separates:

- cost DP
- lexicographic best next decision

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private int[] nums;
    private int n;
    private int fullMask;
    private int[][] memo;
    private int[][] choice;

    public int[] findPermutation(int[] nums) {
        this.nums = nums;
        this.n = nums.length;
        this.fullMask = (1 << n) - 1;
        this.memo = new int[1 << n][n];
        this.choice = new int[1 << n][n];

        for (int[] row : memo) Arrays.fill(row, -1);
        for (int[] row : choice) Arrays.fill(row, -1);

        dfs(1, 0);

        int[] ans = new int[n];
        ans[0] = 0;

        int mask = 1;
        int last = 0;

        for (int i = 1; i < n; i++) {
            int next = choice[mask][last];
            ans[i] = next;
            mask |= (1 << next);
            last = next;
        }

        return ans;
    }

    private int dfs(int mask, int last) {
        if (mask == fullMask) {
            return Math.abs(last - nums[0]);
        }

        if (memo[mask][last] != -1) {
            return memo[mask][last];
        }

        int bestCost = Integer.MAX_VALUE;
        int bestNext = -1;

        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;

            int candidate = Math.abs(last - nums[next]) + dfs(mask | (1 << next), next);

            if (candidate < bestCost) {
                bestCost = candidate;
                bestNext = next;
            } else if (candidate == bestCost && next < bestNext) {
                bestNext = next;
            }
        }

        choice[mask][last] = bestNext;
        return memo[mask][last] = bestCost;
    }
}
```

---

## Complexity

Again:

```text
O(2^n * n^2)
```

Space:

```text
O(2^n * n)
```

---

## Pros

- Very clean final reconstruction
- Lexicographic handling is explicit
- Probably the easiest polished implementation

## Cons

- Same asymptotic complexity as Approach 1
- Essentially the same idea with slightly richer memo state

---

# Approach 4: Brute Force Permutation Enumeration

## Idea

Generate every permutation of `[0..n-1]`, compute its score, and choose:

1. minimum cost
2. lexicographically smallest among those

Because `n <= 14`, this is not remotely feasible in the worst case.

Still, it is useful as a conceptual baseline.

---

## Java Code

```java
class Solution {
    private int bestCost = Integer.MAX_VALUE;
    private int[] bestPerm = null;

    public int[] findPermutation(int[] nums) {
        int n = nums.length;
        boolean[] used = new boolean[n];
        int[] perm = new int[n];

        backtrack(nums, used, perm, 0);
        return bestPerm;
    }

    private void backtrack(int[] nums, boolean[] used, int[] perm, int idx) {
        int n = nums.length;

        if (idx == n) {
            int cost = 0;
            for (int i = 0; i < n; i++) {
                int next = (i + 1) % n;
                cost += Math.abs(perm[i] - nums[perm[next]]);
            }

            if (cost < bestCost || (cost == bestCost && lexicographicallySmaller(perm, bestPerm))) {
                bestCost = cost;
                bestPerm = perm.clone();
            }
            return;
        }

        for (int x = 0; x < n; x++) {
            if (used[x]) continue;
            used[x] = true;
            perm[idx] = x;
            backtrack(nums, used, perm, idx + 1);
            used[x] = false;
        }
    }

    private boolean lexicographicallySmaller(int[] a, int[] b) {
        if (b == null) return true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return a[i] < b[i];
        }
        return false;
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

For each, cost calculation is `O(n)`.

So total is:

```text
O(n * n!)
```

This is far too slow for `n = 14`.

---

## Pros

- Most direct interpretation
- Good for verifying tiny examples

## Cons

- Not viable
- Ignores overlapping subproblems completely

---

# Deep Intuition

## Why this is basically a TSP-style problem

The structure is not the standard TSP distance:

```text
dist(a, b) = |a - b|
```

Instead it is:

```text
dist(a, b) = |a - nums[b]|
```

That is a **directed** edge cost depending on both the current node `a` and the next node `b`.

But the optimization pattern is still the same:

- choose an order of all nodes
- total cost is sum of adjacent transition costs
- cycle closes back to start

That is exactly the shape of a traveling-salesman / Hamiltonian-cycle DP.

---

## Why subset + last is enough

Suppose we have already built a prefix of the permutation ending at `last`.

To decide what to do next, we do not need the exact full order of earlier nodes. We only need:

- which nodes have already been used
- what the current endpoint is

That is because the next added cost only depends on:

```text
last and next
```

Earlier nodes matter only because they cannot be reused.

That is the standard optimal substructure that makes bitmask DP work.

---

## Why fixing the starting point resolves lexicographic ambiguity cleanly

Since the score is cyclic, a single cycle corresponds to multiple rotated permutations.

Example:

```text
[0,2,1], [2,1,0], [1,0,2]
```

all describe the same cycle ordering under rotation.

If we did not fix the first element, lexicographic comparison would get messy.

By fixing the first element to `0`, we turn the cycle into a canonical representation.

Then lexicographic order is well-defined and easy to enforce left-to-right.

---

# Correctness Sketch for Approach 1

We prove that the top-down DP with fixed start `0` is correct.

## Step 1: Canonical start

Any cycle can be rotated without changing its score.

Therefore among all minimum-score cycles, there is an equivalent representation starting at `0`.

The lexicographically smallest optimal permutation must use this canonical start.

So restricting to permutations with `perm[0] = 0` does not lose the optimal answer.

## Step 2: State definition

`dfs(mask, last)` is the minimum additional cost needed to complete the permutation from current state, where:

- used nodes are exactly `mask`
- current path ends at `last`

This state contains all information needed for future decisions.

## Step 3: Recurrence

If all nodes are used, the only remaining cost is to close the cycle back to `0`:

```text
|last - nums[0]|
```

Otherwise, choose any unused `next`, pay:

```text
|last - nums[next]|
```

and recurse.

Thus:

```text
dfs(mask, last) =
min over next not in mask (
    |last - nums[next]| + dfs(mask ∪ {next}, next)
)
```

## Step 4: Optimal substructure

If an optimal completion from `(mask, last)` begins by choosing `next`, then the remainder of the path after that must itself be an optimal completion of `(mask | (1 << next), next)`. Otherwise we could replace it with a cheaper one and improve the total cost.

## Step 5: Lexicographic tie-breaking

During reconstruction, among all `next` values that preserve the optimal DP value, choosing the smallest `next` makes the earliest differing position as small as possible. Therefore the resulting full permutation is lexicographically smallest among all optimal permutations.

So the algorithm is correct.

---

# Example Walkthrough

## Example 1

```text
nums = [1,0,2]
```

We fix:

```text
perm[0] = 0
```

Candidates for the rest are permutations of `[1,2]`.

### Candidate 1

```text
perm = [0,1,2]
```

Cost:

```text
|0 - nums[1]| + |1 - nums[2]| + |2 - nums[0]|
= |0 - 0| + |1 - 2| + |2 - 1|
= 0 + 1 + 1
= 2
```

### Candidate 2

```text
perm = [0,2,1]
```

Cost:

```text
|0 - nums[2]| + |2 - nums[1]| + |1 - nums[0]|
= |0 - 2| + |2 - 0| + |1 - 1|
= 2 + 2 + 0
= 4
```

So `[0,1,2]` is optimal.

---

## Example 2

```text
nums = [0,2,1]
```

Again fix first element to `0`.

### Candidate 1

```text
[0,1,2]
```

Cost:

```text
|0 - nums[1]| + |1 - nums[2]| + |2 - nums[0]|
= |0 - 2| + |1 - 1| + |2 - 0|
= 2 + 0 + 2
= 4
```

### Candidate 2

```text
[0,2,1]
```

Cost:

```text
|0 - nums[2]| + |2 - nums[1]| + |1 - nums[0]|
= |0 - 1| + |2 - 2| + |1 - 0|
= 1 + 0 + 1
= 2
```

So answer is:

```text
[0,2,1]
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.Arrays;

class Solution {
    private int[] nums;
    private int n;
    private int fullMask;
    private int[][] memo;
    private int[][] choice;

    public int[] findPermutation(int[] nums) {
        this.nums = nums;
        this.n = nums.length;
        this.fullMask = (1 << n) - 1;
        this.memo = new int[1 << n][n];
        this.choice = new int[1 << n][n];

        for (int[] row : memo) Arrays.fill(row, -1);
        for (int[] row : choice) Arrays.fill(row, -1);

        // Fix start at 0 for canonical lexicographically smallest representation
        dfs(1, 0);

        int[] ans = new int[n];
        ans[0] = 0;

        int mask = 1;
        int last = 0;

        for (int i = 1; i < n; i++) {
            int next = choice[mask][last];
            ans[i] = next;
            mask |= (1 << next);
            last = next;
        }

        return ans;
    }

    private int dfs(int mask, int last) {
        if (mask == fullMask) {
            return Math.abs(last - nums[0]);
        }

        if (memo[mask][last] != -1) {
            return memo[mask][last];
        }

        int bestCost = Integer.MAX_VALUE;
        int bestNext = -1;

        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;

            int candidate = Math.abs(last - nums[next]) + dfs(mask | (1 << next), next);

            if (candidate < bestCost) {
                bestCost = candidate;
                bestNext = next;
            } else if (candidate == bestCost && next < bestNext) {
                bestNext = next;
            }
        }

        choice[mask][last] = bestNext;
        return memo[mask][last] = bestCost;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                                 | Time Complexity | Space Complexity | Recommended |
| ---------- | --------------------------------------------------------- | --------------: | ---------------: | ----------- |
| Approach 1 | Top-down bitmask DP + greedy lexicographic reconstruction |  `O(2^n * n^2)` |     `O(2^n * n)` | Yes         |
| Approach 2 | Bottom-up bitmask DP                                      |  `O(2^n * n^2)` |     `O(2^n * n)` | Good        |
| Approach 3 | Top-down DP storing best next choice explicitly           |  `O(2^n * n^2)` |     `O(2^n * n)` | Yes         |
| Approach 4 | Brute-force permutations                                  |     `O(n * n!)` |           `O(n)` | No          |

---

# Pattern Recognition Takeaway

This problem is a strong signal for:

- small `n` up to around 14
- cyclic ordering cost
- optimization over permutations
- lexicographic tie-breaking

That combination usually points to:

- fix a canonical starting point
- use subset DP with endpoint
- reconstruct the lexicographically smallest optimal path

This is essentially a TSP-style DP with custom directed edge weights.

---

# Final Takeaway

The cleanest way to solve this problem is:

1. fix the permutation to start at `0`
2. define transition cost:
   ```text
   |last - nums[next]|
   ```
3. use bitmask DP on `(mask, last)` to compute minimum remaining cost
4. reconstruct the lexicographically smallest optimal permutation by always choosing the smallest optimal next node

That yields an efficient and correct solution for `n <= 14`.
