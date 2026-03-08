# 1692. Count Ways to Distribute Candies — Detailed Explanation

## Problem Summary

We are given:

- `n` **unique candies** labeled `1..n`
- `k` **bags**

Goal:

Distribute all candies into the bags such that:

1. **Every bag contains at least one candy**
2. **Order of bags does not matter**
3. **Order of candies inside a bag does not matter**

Return the **number of valid distributions**, modulo:

```
MOD = 1,000,000,007
```

Constraints:

```
1 ≤ k ≤ n ≤ 1000
```

---

# Key Insight

This problem is exactly the **Stirling Number of the Second Kind**:

```
S(n, k)
```

Definition:

> The number of ways to partition **n distinct elements** into **k non‑empty unlabeled groups**.

In our case:

- candies → elements
- bags → groups

So the answer is:

```
S(n, k)
```

---

# Understanding the Recurrence

We derive a recurrence relation for `S(n, k)`.

Consider the **nth candy**.

There are **two possibilities**.

---

## Case 1 — Candy forms a new bag

Candy `n` is alone in its own bag.

Then the remaining `n‑1` candies must be distributed into `k‑1` bags.

Number of ways:

```
S(n-1, k-1)
```

---

## Case 2 — Candy joins an existing bag

First distribute `n-1` candies into `k` bags.

Then candy `n` can be placed into **any of the k bags**.

Ways:

```
k × S(n-1, k)
```

---

## Recurrence

Combining both cases:

```
S(n, k) = S(n-1, k-1) + k * S(n-1, k)
```

This is the fundamental recurrence used in the DP solution.

---

# Base Cases

Important base conditions:

```
S(0,0) = 1
S(n,0) = 0   (n > 0)
S(0,k) = 0   (k > 0)
S(n,n) = 1
S(n,1) = 1
```

Explanation:

- Only one way to place `n` items into `n` bags → each item alone.
- Only one way to place `n` items into `1` bag → all together.

---

# Dynamic Programming Approach

We compute the values iteratively.

Define:

```
dp[i][j] = number of ways to distribute i candies into j bags
```

Transition:

```
dp[i][j] = dp[i-1][j-1] + j * dp[i-1][j]
```

Modulo is applied at each step.

---

# Example Walkthrough

Example:

```
n = 3
k = 2
```

Possible partitions:

```
(1), (2,3)
(2), (1,3)
(3), (1,2)
```

Total:

```
3 ways
```

Using recurrence:

```
S(3,2)
= S(2,1) + 2*S(2,2)
= 1 + 2*1
= 3
```

---

# DP Table Example

For `n = 4`, `k = 2`

| i \\ j | 1   | 2   |
| ------ | --- | --- |
| 1      | 1   | 0   |
| 2      | 1   | 1   |
| 3      | 1   | 3   |
| 4      | 1   | 7   |

Answer:

```
S(4,2) = 7
```

Matches the example.

---

# Java Implementation (2D DP)

```java
class Solution {
    public int waysToDistribute(int n, int k) {

        int MOD = 1_000_000_007;
        long[][] dp = new long[n + 1][k + 1];

        dp[0][0] = 1;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= Math.min(i, k); j++) {

                dp[i][j] =
                        (dp[i - 1][j - 1] + j * dp[i - 1][j]) % MOD;
            }
        }

        return (int) dp[n][k];
    }
}
```

---

# Space Optimized DP

Observation:

```
dp[i][*] depends only on dp[i-1][*]
```

So we can compress the DP to **1D**.

---

# Java Implementation (1D DP)

```java
class Solution {
    public int waysToDistribute(int n, int k) {

        int MOD = 1_000_000_007;
        long[] dp = new long[k + 1];

        dp[0] = 1;

        for (int i = 1; i <= n; i++) {

            for (int j = Math.min(i, k); j >= 1; j--) {

                dp[j] =
                        (dp[j - 1] + j * dp[j]) % MOD;
            }
        }

        return (int) dp[k];
    }
}
```

---

# Why Reverse Iteration?

In the 1D version we iterate `j` **backwards**:

```
for (j = k → 1)
```

This ensures we do not overwrite values that are still needed for computation.

---

# Complexity Analysis

Let:

```
n = number of candies
k = number of bags
```

### Time Complexity

```
O(n * k)
```

At most:

```
1000 × 1000 = 10^6 operations
```

which is easily manageable.

---

### Space Complexity

2D DP:

```
O(n * k)
```

1D optimized DP:

```
O(k)
```

---

# Intuition Summary

The core idea:

When adding the `nth` candy:

1. It **creates a new bag**
2. It **joins one of the existing bags**

Those two possibilities completely describe the problem, which leads to the **Stirling number recurrence**.

---

# Final Formula

```
S(n,k) = S(n-1,k-1) + k*S(n-1,k)
```

Return:

```
S(n,k) mod 1,000,000,007
```

---

# Key Takeaway

This problem is a direct application of:

```
Stirling Numbers of the Second Kind
```

Understanding this recurrence turns a seemingly complex combinatorics problem into a simple dynamic programming solution.
