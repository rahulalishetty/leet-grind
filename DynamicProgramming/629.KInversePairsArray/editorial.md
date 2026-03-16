# 629. K Inverse Pairs Array — Exhaustive Solution Notes

## Overview

We are asked to count how many permutations of the numbers:

```text
1, 2, 3, ..., n
```

have exactly `k` inverse pairs.

An inverse pair is a pair of indices `(i, j)` such that:

```text
0 <= i < j < nums.length
and nums[i] > nums[j]
```

The answer can be very large, so everything must be computed modulo:

```text
10^9 + 7
```

This problem is fundamentally a **dynamic programming + combinatorics** problem.

The brute force approach is hopeless because the number of permutations is `n!`.
The real insight comes from understanding how the permutation count changes when we insert the largest number `n` into permutations of `1..n-1`.

This write-up explains all approaches provided:

1. **Brute Force**
2. **Recursion with Memoization**
3. **Dynamic Programming**
4. **Dynamic Programming with Cumulative Sum**
5. **Another Optimized Dynamic Programming Approach**
6. **Memoization Again (optimized recurrence)**
7. **1-D Dynamic Programming**

In practice, the 1-D DP is the best final solution here.

---

## Problem Statement

For an integer array `nums`, an inverse pair is a pair of indices `[i, j]` such that:

```text
0 <= i < j < nums.length
and nums[i] > nums[j]
```

Given two integers `n` and `k`, return the number of different arrays consisting of numbers from `1` to `n` such that there are exactly `k` inverse pairs.

Return the result modulo `10^9 + 7`.

---

## Example 1

**Input**

```text
n = 3, k = 0
```

**Output**

```text
1
```

**Explanation**

Only:

```text
[1, 2, 3]
```

has exactly `0` inverse pairs.

---

## Example 2

**Input**

```text
n = 3, k = 1
```

**Output**

```text
2
```

**Explanation**

The valid arrays are:

```text
[1, 3, 2]
[2, 1, 3]
```

---

## Constraints

- `1 <= n <= 1000`
- `0 <= k <= 1000`

---

# What Is the Main Insight?

The key idea is to build permutations of `1..n` from permutations of `1..n-1`.

Suppose we already have a permutation of:

```text
1..(n-1)
```

with exactly `x` inverse pairs.

Now insert `n`, the largest element.

If we place `n`:

- at the far right, it creates `0` new inverse pairs
- 1 position from the right, it creates `1` new inverse pair
- 2 positions from the right, it creates `2` new inverse pairs
- ...
- at the far left, it creates `n - 1` new inverse pairs

Why?

Because `n` is larger than every other element, and every smaller element placed to its right forms an inverse pair with `n`.

So inserting `n` at position `p` from the right contributes exactly `p` new inverse pairs.

That gives the recurrence.

---

# Recurrence Relation

Let:

```text
count(n, k)
```

be the number of permutations of `1..n` with exactly `k` inverse pairs.

Then:

```text
count(n, k) = sum(count(n - 1, k - i)) for i = 0 to min(k, n - 1)
```

Why does this work?

Because:

- choose a permutation of `1..n-1` with `k - i` inverse pairs
- insert `n` so that it contributes exactly `i` new inverse pairs

The total becomes exactly `k`.

This recurrence is the foundation of every useful approach.

---

# Approach 1: Brute Force

## Intuition

The most naive solution is:

1. generate every permutation of `1..n`
2. count the number of inverse pairs in each permutation
3. count how many have exactly `k`

This works conceptually, but it is far too slow.

---

## Complexity Analysis — Brute Force

### Time Complexity

There are:

```text
n!
```

permutations.

For each permutation, we can count inverse pairs in `O(n log n)` using merge sort, or `O(n^2)` naively.

So the given estimate is:

```text
O(n! × n log n)
```

which is completely infeasible.

---

### Space Complexity

Each generated array needs `O(n)` space.

So space complexity is:

```text
O(n)
```

---

# Approach 2: Using Recursion with Memoization

## Intuition

Using the recurrence:

```text
count(n, k) = sum(count(n - 1, k - i)) for i = 0 to min(k, n - 1)
```

we can write a recursive function.

However, direct recursion causes many repeated subproblems.

So we memoize:

```text
memo[n][k]
```

to store already computed results.

---

## Recurrence Explanation in More Detail

Suppose we know all counts for arrays of size `n - 1`.

To build an array of size `n` with exactly `k` inverse pairs:

- insert `n` at the last position → adds `0`
- insert `n` 1 place from the right → adds `1`
- insert `n` 2 places from the right → adds `2`
- ...
- insert `n` `i` places from the right → adds `i`

So if the previous array had `k - i` inverse pairs, then after insertion it has `k`.

Thus:

```text
count(n, k) =
count(n - 1, k)
+ count(n - 1, k - 1)
+ ...
+ count(n - 1, k - min(k, n - 1))
```

---

## Base Cases

### If `n == 0`

There are no permutations of size 0 contributing to positive inverse pairs.

So:

```text
count(0, k) = 0
```

for the recursion as implemented.

### If `k == 0`

There is exactly one permutation with zero inverse pairs:

```text
[1, 2, 3, ..., n]
```

So:

```text
count(n, 0) = 1
```

---

## Java Implementation — Memoized Recursion

```java
class Solution {
    Integer[][] memo = new Integer[1001][1001];

    public int kInversePairs(int n, int k) {
        if (n == 0)
            return 0;
        if (k == 0)
            return 1;
        if (memo[n][k] != null)
            return memo[n][k];

        int inv = 0;
        for (int i = 0; i <= Math.min(k, n - 1); i++)
            inv = (inv + kInversePairs(n - 1, k - i)) % 1000000007;

        memo[n][k] = inv;
        return inv;
    }
}
```

---

## Complexity Analysis — Recursion with Memoization

### Time Complexity

There are about:

```text
n × k
```

states.

For each state, we loop up to:

```text
min(n, k)
```

times.

So the time complexity is:

```text
O(n × k × min(n, k))
```

---

### Space Complexity

The memo table uses:

```text
O(n × k)
```

space.

Recursion stack depth can go up to `n`.

So total space complexity is:

```text
O(n × k)
```

---

# Approach 3: Dynamic Programming

## Intuition

The memoized recursion can be converted directly into tabulation.

Define:

```text
dp[i][j]
```

as the number of arrays using numbers `1..i` with exactly `j` inverse pairs.

Then use the same recurrence.

---

## DP Definition

```text
dp[i][j] = number of permutations of 1..i with exactly j inverse pairs
```

---

## Base Cases

### `dp[i][0] = 1`

There is exactly one sorted permutation with zero inverse pairs.

### `dp[0][j] = 0` for `j > 0`

No positive inverse pairs are possible with zero elements.

---

## Recurrence

```text
dp[i][j] = sum(dp[i - 1][j - p]) for p = 0 to min(j, i - 1)
```

This is exactly the insertion-of-`i` idea.

---

## Java Implementation — Basic DP

```java
class Solution {
    public int kInversePairs(int n, int k) {
        int[][] dp = new int[n + 1][k + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                if (j == 0)
                    dp[i][j] = 1;
                else {
                    for (int p = 0; p <= Math.min(j, i - 1); p++)
                        dp[i][j] = (dp[i][j] + dp[i - 1][j - p]) % 1000000007;
                }
            }
        }

        return dp[n][k];
    }
}
```

---

## Complexity Analysis — Basic DP

### Time Complexity

There are:

```text
n × k
```

states, and each state loops up to `min(n, k)` times.

So:

```text
O(n × k × min(n, k))
```

---

### Space Complexity

The DP table is:

```text
O(n × k)
```

---

# Approach 4: Dynamic Programming with Cumulative Sum

## Intuition

The previous DP is still too slow because every state does an inner summation.

We want to eliminate that repeated summation.

Notice:

```text
dp[i][j] = dp[i - 1][j] + dp[i - 1][j - 1] + ... + dp[i - 1][j - (i - 1)]
```

This is a sliding window sum over the previous row.

So instead of recomputing that sum from scratch for every `j`, we can use prefix sums / cumulative sums.

---

## Cumulative Sum Interpretation

In this approach, each `dp[i][j]` stores not the exact value directly, but the cumulative sum up to `j`.

That lets us compute range sums in `O(1)`.

The needed window in the previous row is:

```text
dp[i - 1][j - (i - 1)] to dp[i - 1][j]
```

If cumulative sums are available, that becomes:

```text
dp[i - 1][j] - dp[i - 1][j - i]
```

when `j - i >= 0`.

Otherwise, if the window starts before 0, then we just take all values up to `j`.

---

## Java Implementation — Cumulative Sum DP

```java
class Solution {
    public int kInversePairs(int n, int k) {
        int[][] dp = new int[n + 1][k + 1];
        int M = 1000000007;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                if (j == 0)
                    dp[i][j] = 1;
                else {
                    int val = (dp[i - 1][j] + M - ((j - i) >= 0 ? dp[i - 1][j - i] : 0)) % M;
                    dp[i][j] = (dp[i][j - 1] + val) % M;
                }
            }
        }

        return (dp[n][k] + M - (k > 0 ? dp[n][k - 1] : 0)) % M;
    }
}
```

---

## Complexity Analysis — Cumulative Sum DP

### Time Complexity

Now each DP state is computed in `O(1)`.

So total time becomes:

```text
O(n × k)
```

---

### Space Complexity

The 2D table still uses:

```text
O(n × k)
```

---

# Approach 5: Another Optimized Dynamic Programming Approach

## Intuition

This is essentially a direct optimized formulation of the same sliding-window DP.

Instead of explicitly thinking in terms of cumulative rows, we derive:

```text
dp[i][j] = dp[i][j - 1] + dp[i - 1][j] - dp[i - 1][j - i]
```

when `j - i >= 0`.

Otherwise:

```text
dp[i][j] = dp[i][j - 1] + dp[i - 1][j]
```

This avoids recomputing the summation window and stores the exact count directly.

---

## Additional Optimization

For `i` elements, the maximum possible number of inverse pairs is:

```text
i × (i - 1) / 2
```

because that occurs only for the descending array:

```text
[i, i-1, ..., 2, 1]
```

So for row `i`, we only need to compute `j` up to:

```text
min(k, i × (i - 1) / 2)
```

---

## Java Implementation — Another Optimized DP

```java
class Solution {
    public int kInversePairs(int n, int k) {
        int[][] dp = new int[n + 1][k + 1];
        int M = 1000000007;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= k && j <= i * (i - 1) / 2; j++) {
                if (i == 1 && j == 0) {
                    dp[i][j] = 1;
                    break;
                } else if (j == 0) {
                    dp[i][j] = 1;
                } else {
                    int val = (dp[i - 1][j] + M - ((j - i) >= 0 ? dp[i - 1][j - i] : 0)) % M;
                    dp[i][j] = (dp[i][j - 1] + val) % M;
                }
            }
        }

        return dp[n][k];
    }
}
```

---

## Complexity Analysis — Another Optimized DP

### Time Complexity

Each state is `O(1)` and there are `O(n × k)` relevant states.

So:

```text
O(n × k)
```

---

### Space Complexity

The DP table uses:

```text
O(n × k)
```

---

# Approach 6: Once Again Memoization

## Intuition

The optimized recurrence from Approach 5 can also be implemented recursively with memoization.

This time, instead of memoizing the original summation recurrence, we memoize the optimized prefix-sum-style recurrence.

This reduces the time complexity to `O(n × k)`.

---

## Optimized Recursive Recurrence

Let `inv(n, k)` represent the cumulative-style value.

Then:

```text
inv(n, k) = inv(n, k - 1) + inv(n - 1, k) - inv(n - 1, k - n)
```

with proper boundary handling and modulo.

The final answer is then extracted as:

```text
inv(n, k) - inv(n, k - 1)
```

---

## Java Implementation — Optimized Memoization

```java
class Solution {
    Integer[][] memo = new Integer[1001][1001];
    int M = 1000000007;

    public int kInversePairs(int n, int k) {
        return (inv(n, k) + M - (k > 0 ? inv(n, k - 1) : 0)) % M;
    }

    public int inv(int n, int k) {
        if (n == 0)
            return 0;
        if (k == 0)
            return 1;
        if (memo[n][k] != null)
            return memo[n][k];

        int val = (inv(n - 1, k) + M - ((k - n) >= 0 ? inv(n - 1, k - n) : 0)) % M;
        memo[n][k] = (inv(n, k - 1) + val) % M;
        return memo[n][k];
    }
}
```

---

## Complexity Analysis — Optimized Memoization

### Time Complexity

There are:

```text
n × k
```

states, each computed once in `O(1)`.

So:

```text
O(n × k)
```

---

### Space Complexity

The memo table uses:

```text
O(n × k)
```

---

# Approach 7: 1-D Dynamic Programming

## Intuition

In all DP solutions, row `i` depends only on row `i - 1`.

So we do not need the full 2D DP table.

We can keep:

- `dp` = previous row
- `temp` = current row

This reduces space complexity from `O(n × k)` to `O(k)`.

The same optimized recurrence is used.

---

## Java Implementation — 1-D DP

```java
class Solution {
    public int kInversePairs(int n, int k) {
        int[] dp = new int[k + 1];
        int M = 1000000007;

        for (int i = 1; i <= n; i++) {
            int[] temp = new int[k + 1];
            temp[0] = 1;

            for (int j = 1; j <= k; j++) {
                int val = (dp[j] + M - ((j - i) >= 0 ? dp[j - i] : 0)) % M;
                temp[j] = (temp[j - 1] + val) % M;
            }

            dp = temp;
        }

        return (dp[k] + M - (k > 0 ? dp[k - 1] : 0)) % M;
    }
}
```

---

## Why the Final Answer Uses `dp[k] - dp[k - 1]`

In the optimized recurrence versions, the DP row stores cumulative counts.

So:

```text
dp[j]
```

represents the number of arrays with inverse pairs up to `j`, not exactly `j`.

Therefore, to get the exact value for `k`, we compute:

```text
dp[k] - dp[k - 1]
```

modulo `10^9 + 7`.

That is why the final answer is extracted this way.

---

## Complexity Analysis — 1-D DP

### Time Complexity

We compute `k + 1` entries for each of `n` rows.

So:

```text
O(n × k)
```

---

### Space Complexity

Only two arrays of size `k + 1` are needed.

So:

```text
O(k)
```

---

# Comparing the Approaches

## Brute Force

### Strengths

- straightforward conceptually

### Weaknesses

- completely infeasible
- factorial explosion

---

## Memoized Recurrence / Basic 2D DP

### Strengths

- directly follows the combinatorial recurrence
- easier to derive

### Weaknesses

- too slow at `O(n × k × min(n, k))`

---

## Prefix-Sum / Optimized DP

### Strengths

- reduces time to `O(n × k)`
- efficient enough for constraints
- standard accepted technique

### Weaknesses

- recurrence is less obvious at first

---

## 1-D DP

### Strengths

- best space usage
- still `O(n × k)` time
- most practical final implementation

### Weaknesses

- slightly harder to understand because rows are compressed

---

# Key Mathematical Insight Again

For permutations of `1..n`:

- insert `n` into a permutation of `1..n-1`
- if inserted `i` places from the right, it contributes exactly `i` new inverse pairs

That is the entire foundation of the recurrence.

Without understanding this insertion view, the DP formulas can look mysterious.

---

# Common Mistakes

## 1. Forgetting modulo handling during subtraction

When using expressions like:

```java
a - b
```

under modulo arithmetic, always do:

```java
(a + MOD - b) % MOD
```

to avoid negative numbers.

---

## 2. Forgetting the upper bound on inverse pairs

For `i` elements, the maximum possible inverse pairs is:

```text
i × (i - 1) / 2
```

Any larger `j` is impossible.

---

## 3. Confusing cumulative DP values with exact counts

In the optimized approaches, the row often stores prefix sums rather than exact counts.

So the final extraction step is important.

---

## 4. Using the slow recurrence directly for all constraints

The raw summation recurrence is fine conceptually, but too slow for `n, k <= 1000`.

The prefix-sum optimization is necessary.

---

# Final Summary

## Main Recurrence

Let:

```text
count(n, k)
```

be the number of permutations of `1..n` with exactly `k` inverse pairs.

Then:

```text
count(n, k) = sum(count(n - 1, k - i)) for i = 0 to min(k, n - 1)
```

because inserting `n` `i` positions from the right adds exactly `i` new inverse pairs.

---

## Best Complexities

### Basic DP / Memoization

- Time: `O(n × k × min(n, k))`
- Space: `O(n × k)`

### Optimized DP

- Time: `O(n × k)`
- Space: `O(n × k)`

### 1-D DP

- Time: `O(n × k)`
- Space: `O(k)`

---

# Best Final Java Solution

```java
class Solution {
    public int kInversePairs(int n, int k) {
        int[] dp = new int[k + 1];
        int M = 1000000007;

        for (int i = 1; i <= n; i++) {
            int[] temp = new int[k + 1];
            temp[0] = 1;

            for (int j = 1; j <= k; j++) {
                int val = (dp[j] + M - ((j - i) >= 0 ? dp[j - i] : 0)) % M;
                temp[j] = (temp[j - 1] + val) % M;
            }

            dp = temp;
        }

        return (dp[k] + M - (k > 0 ? dp[k - 1] : 0)) % M;
    }
}
```

This is the standard optimized solution and is the best choice among the approaches listed.
