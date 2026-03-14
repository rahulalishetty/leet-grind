# 1879. Minimum XOR Sum of Two Arrays — Exhaustive Java Notes

## Problem Statement

You are given two integer arrays `nums1` and `nums2`, both of length `n`.

The XOR sum of the two arrays is:

```text
(nums1[0] XOR nums2[0]) +
(nums1[1] XOR nums2[1]) +
...
(nums1[n - 1] XOR nums2[n - 1])
```

You may **rearrange `nums2` arbitrarily**.

Your task is to minimize the XOR sum after rearranging `nums2`.

Return the minimum possible XOR sum.

---

## Example 1

```text
Input:
nums1 = [1,2]
nums2 = [2,3]

Output:
2
```

Explanation:

Rearrange `nums2` to `[3,2]`.

Then:

```text
(1 XOR 3) + (2 XOR 2) = 2 + 0 = 2
```

---

## Example 2

```text
Input:
nums1 = [1,0,3]
nums2 = [5,3,4]

Output:
8
```

Explanation:

Rearrange `nums2` to `[5,4,3]`.

Then:

```text
(1 XOR 5) + (0 XOR 4) + (3 XOR 3) = 4 + 4 + 0 = 8
```

---

## Constraints

```text
1 <= n <= 14
0 <= nums1[i], nums2[i] <= 10^7
```

The crucial clue is:

```text
n <= 14
```

That strongly suggests **bitmask DP**.

---

# 1. Core Insight

This is an assignment problem.

We want to match each element in `nums1` with exactly one element in `nums2` such that the total cost is minimized, where the cost of matching:

```text
nums1[i] with nums2[j]
```

is:

```text
nums1[i] XOR nums2[j]
```

So we are effectively trying to find a minimum-cost perfect matching in a complete bipartite graph of size `n x n`.

Because `n <= 14`, the intended solution is not Hungarian algorithm, but **state compression DP**.

---

# 2. Why Bitmask DP Fits Perfectly

Suppose we process `nums1` from left to right.

At any point, we only need to know:

- how many elements from `nums1` have already been assigned,
- which elements from `nums2` have already been used.

A bitmask naturally represents the used elements of `nums2`.

For example, if:

```text
mask = 10110
```

then the set bits tell us which indices of `nums2` are already assigned.

If `bitCount(mask) = k`, that means we have already assigned:

```text
nums1[0], nums1[1], ..., nums1[k - 1]
```

So the next `nums1` element to assign is:

```text
nums1[k]
```

This gives an extremely clean DP formulation.

---

# 3. Approach 1 — Top-Down DP with Bitmask Memoization

## Main Idea

Define:

```text
dfs(mask)
```

as the minimum XOR sum achievable after using the subset of `nums2` described by `mask`.

If `bitCount(mask) = i`, then we are currently assigning:

```text
nums1[i]
```

For each unused `j` in `nums2`, try pairing:

```text
nums1[i] with nums2[j]
```

and recurse.

Transition:

```text
dfs(mask) =
min over unused j of
(nums1[i] XOR nums2[j]) + dfs(mask | (1 << j))
```

Base case:

```text
if mask == (1 << n) - 1, return 0
```

because all elements have been assigned.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[] nums1;
    private int[] nums2;
    private int n;
    private int[] memo;

    public int minimumXORSum(int[] nums1, int[] nums2) {
        this.nums1 = nums1;
        this.nums2 = nums2;
        this.n = nums1.length;
        this.memo = new int[1 << n];
        Arrays.fill(memo, -1);
        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int i = Integer.bitCount(mask);
        int best = Integer.MAX_VALUE;

        for (int j = 0; j < n; j++) {
            if (((mask >> j) & 1) == 0) {
                best = Math.min(best,
                    (nums1[i] ^ nums2[j]) + dfs(mask | (1 << j))
                );
            }
        }

        memo[mask] = best;
        return best;
    }
}
```

---

## Complexity Analysis

There are:

```text
2^n
```

possible masks.

For each mask, we may try up to `n` possible unused `nums2` elements.

So the time complexity is:

```text
O(n * 2^n)
```

Space:

```text
O(2^n)
```

for memoization.

This is excellent for `n <= 14`.

---

# 4. Why the Top-Down Recurrence Is Correct

At state `mask`:

- exactly `bitCount(mask)` elements of `nums1` have been assigned,
- the set bits in `mask` indicate which `nums2` elements are already used.

So the next choice is exactly which unused element of `nums2` we should pair with the current element of `nums1`.

Since every valid full assignment begins with one such choice, and the remaining problem is structurally identical, the recurrence explores all valid assignments and picks the best one.

This is a textbook optimal substructure.

---

# 5. Approach 2 — Bottom-Up Bitmask DP

## Main Idea

We can compute the same DP iteratively.

Let:

```text
dp[mask] = minimum XOR sum for assigning the first bitCount(mask) elements of nums1
           using exactly the nums2 indices present in mask
```

Initialization:

```text
dp[0] = 0
```

Transition:

If `i = bitCount(mask)`, then from `mask`, try assigning `nums1[i]` to any unused `nums2[j]`:

```text
nextMask = mask | (1 << j)
dp[nextMask] = min(dp[nextMask], dp[mask] + (nums1[i] XOR nums2[j]))
```

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumXORSum(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int totalMasks = 1 << n;
        int INF = Integer.MAX_VALUE / 2;

        int[] dp = new int[totalMasks];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            int i = Integer.bitCount(mask);
            if (i == n) continue;

            for (int j = 0; j < n; j++) {
                if (((mask >> j) & 1) == 0) {
                    int nextMask = mask | (1 << j);
                    dp[nextMask] = Math.min(
                        dp[nextMask],
                        dp[mask] + (nums1[i] ^ nums2[j])
                    );
                }
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

## Complexity Analysis

Exactly the same asymptotic complexity:

```text
O(n * 2^n)
```

Space:

```text
O(2^n)
```

---

# 6. Approach 3 — DFS + Memoization with Precomputed XOR Cost Matrix

## Main Idea

We can slightly clean up transitions by precomputing:

```text
cost[i][j] = nums1[i] XOR nums2[j]
```

This does not change asymptotic complexity, but it can make the recursion more readable and avoids recomputing XOR in every transition.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int n;
    private int[][] cost;
    private int[] memo;

    public int minimumXORSum(int[] nums1, int[] nums2) {
        n = nums1.length;
        cost = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cost[i][j] = nums1[i] ^ nums2[j];
            }
        }

        memo = new int[1 << n];
        Arrays.fill(memo, -1);

        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) {
            return 0;
        }
        if (memo[mask] != -1) {
            return memo[mask];
        }

        int i = Integer.bitCount(mask);
        int best = Integer.MAX_VALUE;

        for (int j = 0; j < n; j++) {
            if (((mask >> j) & 1) == 0) {
                best = Math.min(best, cost[i][j] + dfs(mask | (1 << j)));
            }
        }

        memo[mask] = best;
        return best;
    }
}
```

---

## Complexity

Still:

```text
O(n * 2^n)
```

Space:

```text
O(n^2 + 2^n)
```

because of the additional cost matrix.

---

# 7. Approach 4 — Assignment DP Interpreted as Perfect Matching

This is not a fundamentally different asymptotic algorithm, but it is useful conceptually.

We can view the problem as a minimum-cost perfect matching between:

- left side: indices of `nums1`
- right side: indices of `nums2`

with edge weight:

```text
w(i, j) = nums1[i] XOR nums2[j]
```

For general assignment problems, the Hungarian algorithm could solve this in `O(n^3)`.

However, because:

```text
n <= 14
```

bitmask DP is simpler and usually faster to implement under contest constraints.

So while Hungarian is theoretically possible, it is not the intended tool here.

This is worth knowing as conceptual context.

---

# 8. Why Greedy Fails

A tempting greedy rule would be:

> for each `nums1[i]`, pick the unused `nums2[j]` that minimizes `nums1[i] XOR nums2[j]`

This is not safe.

Why?

Because a local cheap match may block a much more valuable future pairing.

This is the exact reason assignment problems require global optimization.

Example-style intuition:

- one `nums2[j]` might be “pretty good” for the current `nums1[i]`,
- but it may be the **only** excellent match for some later `nums1[k]`.

So greedy can easily get trapped.

---

# 9. Small Worked Example

Take:

```text
nums1 = [1,2]
nums2 = [2,3]
```

Possible assignments:

### Assignment A

- `1 -> 2` gives `1 XOR 2 = 3`
- `2 -> 3` gives `2 XOR 3 = 1`

Total:

```text
4
```

### Assignment B

- `1 -> 3` gives `1 XOR 3 = 2`
- `2 -> 2` gives `2 XOR 2 = 0`

Total:

```text
2
```

So the optimum is `2`.

Using DP:

- `mask = 00`, assign `nums1[0] = 1`
- choose `nums2[0] = 2` or `nums2[1] = 3`
- recurse on each
- DP discovers the second choice is better.

---

# 10. Correctness Sketch

For each state `mask`, we know exactly which `nums2` elements have been used, and therefore which index `i` in `nums1` we are assigning next.

Any valid completion must choose one unused `nums2[j]` for `nums1[i]`.
After making that choice, the remaining problem is exactly the same kind of subproblem on a larger mask.

Thus:

- every full permutation corresponds to one path in the DP,
- every DP path corresponds to one valid assignment,
- taking the minimum over all transitions gives the optimal answer.

So the recurrence is correct.

---

# 11. Comparison of Approaches

| Approach                        | Main Idea                         |            Time |          Space | Notes                   |
| ------------------------------- | --------------------------------- | --------------: | -------------: | ----------------------- |
| Top-down bitmask DP             | DFS on used nums2 mask            |    `O(n * 2^n)` |       `O(2^n)` | cleanest recursive form |
| Bottom-up bitmask DP            | iterative subset DP               |    `O(n * 2^n)` |       `O(2^n)` | stack-free              |
| Top-down with precomputed costs | same DP, cleaner transitions      |    `O(n * 2^n)` | `O(n^2 + 2^n)` | nice readability        |
| Hungarian-style viewpoint       | assignment problem interpretation | not needed here |              — | conceptual only         |

---

# 12. Which Approach Should You Prefer?

For interviews and clarity:

## Best overall:

**Top-down bitmask DP**

Why?

- concise,
- very easy to derive,
- directly matches the problem structure,
- memoization logic is straightforward.

If you prefer iterative DP, the bottom-up version is equally strong.

---

# 13. Practical Java Notes

## About `Integer.bitCount(mask)`

This is heavily used to determine which index of `nums1` we are assigning next.

Since `n <= 14`, this is completely fine performance-wise.

If desired, you could precompute bit counts for all masks, but it is unnecessary.

## About overflow

Each XOR term is at most on the order of `10^7`, and `n <= 14`, so total sum comfortably fits in `int`.

---

# 14. Recommended Java Solution

```java
import java.util.*;

class Solution {
    private int[] nums1;
    private int[] nums2;
    private int n;
    private int[] memo;

    public int minimumXORSum(int[] nums1, int[] nums2) {
        this.nums1 = nums1;
        this.nums2 = nums2;
        this.n = nums1.length;
        this.memo = new int[1 << n];
        Arrays.fill(memo, -1);
        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int i = Integer.bitCount(mask);
        int best = Integer.MAX_VALUE;

        for (int j = 0; j < n; j++) {
            if (((mask >> j) & 1) == 0) {
                best = Math.min(best,
                    (nums1[i] ^ nums2[j]) + dfs(mask | (1 << j))
                );
            }
        }

        memo[mask] = best;
        return best;
    }
}
```

---

# 15. Final Takeaway

This problem is a classic:

> small `n` + rearrangement / assignment + pair cost

That almost always points to:

```text
bitmask DP
```

The essential observation is:

- once we know which elements of `nums2` are already used,
- we automatically know which element of `nums1` we are assigning next.

That reduces the whole assignment problem to a single-mask DP with:

```text
O(n * 2^n)
```

which is perfectly feasible for `n <= 14`.
