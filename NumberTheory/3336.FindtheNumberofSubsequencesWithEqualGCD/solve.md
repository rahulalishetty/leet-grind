# 3336. Find the Number of Subsequences With Equal GCD — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int subsequencePairCount(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an integer array `nums`.

We need to count ordered pairs of subsequences:

```text
(seq1, seq2)
```

such that:

1. both subsequences are non-empty
2. they are disjoint, meaning no index is used in both
3. `gcd(seq1) == gcd(seq2)`

Return the answer modulo:

```text
10^9 + 7
```

---

# Core Difficulty

Each element of `nums` has three choices:

- put it into `seq1`
- put it into `seq2`
- put it into neither

So a brute-force exploration has:

```text
3^n
```

states, which is impossible in general.

But the constraints are small in one crucial way:

```text
nums[i] <= 200
```

That means every GCD we ever care about is also in the range:

```text
0..200
```

This makes DP over GCD states feasible.

---

# Key Insight

We do not need to remember the full contents of the subsequences.

We only need to remember:

- current gcd of subsequence 1
- current gcd of subsequence 2

Let:

- gcd `0` mean “subsequence is still empty”
- once we add a number `x`:
  - `gcd(0, x)` becomes `x`
  - otherwise gcd updates normally

So the state can be compressed to:

```text
dp[g1][g2] = number of ways so far
```

where:

- `g1` is gcd of seq1 so far, or 0 if empty
- `g2` is gcd of seq2 so far, or 0 if empty

At the end, we sum all states where:

```text
g1 == g2 > 0
```

because both subsequences must be non-empty and have equal gcd.

---

# Approach 1 — Dynamic Programming on Pair of GCD States (Recommended)

## Idea

Process elements one by one.

For each element `x`, every current state `(g1, g2)` branches into 3 choices:

1. skip `x`
2. put `x` into `seq1`
3. put `x` into `seq2`

Transitions:

- skip:
  ```text
  (g1, g2) -> (g1, g2)
  ```
- add to seq1:
  ```text
  (g1, g2) -> (gcd(g1, x), g2)
  ```
- add to seq2:
  ```text
  (g1, g2) -> (g1, gcd(g2, x))
  ```

Because gcd values stay in `0..200`, this is manageable.

---

## Why this works

The only relevant property of a subsequence for future elements is its current gcd.

We do not care which exact elements created that gcd.

That is a classic DP compression.

Since subsequences must be disjoint, each element is assigned to at most one side, and the three-way branching handles that exactly.

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int MAX = 200;

    public int subsequencePairCount(int[] nums) {
        long[][] dp = new long[MAX + 1][MAX + 1];
        dp[0][0] = 1;

        for (int x : nums) {
            long[][] next = new long[MAX + 1][MAX + 1];

            for (int g1 = 0; g1 <= MAX; g1++) {
                for (int g2 = 0; g2 <= MAX; g2++) {
                    long cur = dp[g1][g2];
                    if (cur == 0) continue;

                    // 1) skip x
                    next[g1][g2] = (next[g1][g2] + cur) % MOD;

                    // 2) put x into seq1
                    int ng1 = (g1 == 0 ? x : gcd(g1, x));
                    next[ng1][g2] = (next[ng1][g2] + cur) % MOD;

                    // 3) put x into seq2
                    int ng2 = (g2 == 0 ? x : gcd(g2, x));
                    next[g1][ng2] = (next[g1][ng2] + cur) % MOD;
                }
            }

            dp = next;
        }

        long ans = 0;
        for (int g = 1; g <= MAX; g++) {
            ans = (ans + dp[g][g]) % MOD;
        }

        return (int) ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Let:

- `n = nums.length`
- `V = 200`

Then:

```text
Time:  O(n * V^2)
Space: O(V^2)
```

Numerically:

```text
O(200 * 201 * 201)
```

per full DP scan, which is completely fine.

This is the most direct and reliable solution.

---

# Approach 2 — Memoized DFS on (index, gcd1, gcd2)

## Idea

The same state transition can be written top-down.

Define:

```text
dfs(i, g1, g2)
```

= number of valid ways using elements from index `i` onward, given current gcds `g1` and `g2`.

Transitions are again the same three choices:

- skip current number
- put it in seq1
- put it in seq2

Base case:

- when `i == n`, return 1 only if `g1 == g2 > 0`, else return 0

Memoization avoids recomputation.

This is mathematically equivalent to Approach 1.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private static final int MOD = 1_000_000_007;
    private int[] nums;
    private int n;
    private int[][][] memo;

    public int subsequencePairCount(int[] nums) {
        this.nums = nums;
        this.n = nums.length;
        this.memo = new int[n][201][201];

        for (int i = 0; i < n; i++) {
            for (int g1 = 0; g1 <= 200; g1++) {
                Arrays.fill(memo[i][g1], -1);
            }
        }

        return dfs(0, 0, 0);
    }

    private int dfs(int i, int g1, int g2) {
        if (i == n) {
            return (g1 > 0 && g1 == g2) ? 1 : 0;
        }

        if (memo[i][g1][g2] != -1) {
            return memo[i][g1][g2];
        }

        int x = nums[i];
        long ans = 0;

        // skip
        ans += dfs(i + 1, g1, g2);

        // put in seq1
        int ng1 = (g1 == 0 ? x : gcd(g1, x));
        ans += dfs(i + 1, ng1, g2);

        // put in seq2
        int ng2 = (g2 == 0 ? x : gcd(g2, x));
        ans += dfs(i + 1, g1, ng2);

        ans %= MOD;
        memo[i][g1][g2] = (int) ans;
        return memo[i][g1][g2];
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

The number of states is:

```text
O(n * 201 * 201)
```

Each state does constant work.

So:

```text
Time:  O(n * 201 * 201)
Space: O(n * 201 * 201)
```

This is acceptable for `n <= 200`, though the iterative DP is more memory-efficient.

---

# Approach 3 — GCD Frequency / Inclusion-Exclusion Thinking (Conceptual, Harder)

## Idea

You might try to count:

- how many subsequences have gcd exactly `g`
- then somehow count disjoint pairs of such subsequences

But the disjointness requirement makes this difficult.

Two subsequences may both have gcd `g`, yet overlap in indices.

So counting subsequences independently and multiplying counts would overcount invalid pairs.

This makes a direct combinatorial inclusion-exclusion approach much harder than the DP on assignments.

---

## Why it is not preferred

The main issue is that disjointness is naturally captured when each element has 3 assignment choices.

Trying to enforce disjointness afterward becomes messy.

So this approach is mostly useful for intuition, not implementation.

---

# Approach 4 — Brute Force Enumeration of All Subsequence Pairs (Too Slow)

## Idea

Enumerate all subsequences, then all pairs of subsequences, and test:

- both non-empty
- disjoint
- equal gcd

This is clearly impossible beyond tiny inputs.

---

## Why it fails

Each element can be:

- in first subsequence
- in second subsequence
- in neither

So total assignments are:

```text
3^n
```

For `n = 200`, this is hopelessly large.

That is why state compression by gcd is essential.

---

# Detailed Walkthrough

## Example 2

```text
nums = [10, 20, 30]
```

We want pairs of disjoint non-empty subsequences with equal gcd.

Valid pairs are exactly those where both gcds are 10.

Examples:

- `seq1 = [10]`, `seq2 = [20,30]`
- `seq1 = [20,30]`, `seq2 = [10]`

Both are disjoint, non-empty, and:

```text
gcd([10]) = 10
gcd([20,30]) = 10
```

So answer is:

```text
2
```

The DP finds these by exploring all assignments of each element to seq1 / seq2 / neither, while compressing subsequence content into gcd states.

---

# Important Correctness Argument

At any point during processing, the future only depends on:

- current gcd of seq1
- current gcd of seq2
- which elements remain unprocessed

The detailed composition of each subsequence no longer matters once its gcd is known.

That makes `(index, g1, g2)` or just `(g1, g2)` in iterative processing a sufficient state representation.

The three choices per element exactly encode the disjoint subsequence construction.

Therefore the DP is both sound and complete.

---

# Common Pitfalls

## 1. Forgetting that subsequences must be non-empty

States with gcd 0 at the end correspond to empty subsequences and must not be counted.

Only `g > 0` with `g1 == g2` are valid final states.

---

## 2. Misinterpreting disjointness

Disjoint means an index cannot be used in both subsequences.

That is why each element has exactly 3 choices, not 4.

---

## 3. Trying to count subsequences with gcd `g` independently

That ignores overlap between subsequences and overcounts badly.

---

## 4. Missing the usefulness of `gcd(0, x) = x` as a DP convention

This is what makes “empty subsequence so far” easy to encode.

---

# Best Approach

## Recommended: DP over pairs of current gcd values

This is the cleanest solution because:

- gcd values are small (`<= 200`)
- disjointness is naturally encoded
- the DP state space is manageable
- implementation is straightforward

The bottom-up version is typically the most elegant.

---

# Final Recommended Java Solution

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int MAX = 200;

    public int subsequencePairCount(int[] nums) {
        long[][] dp = new long[MAX + 1][MAX + 1];
        dp[0][0] = 1;

        for (int x : nums) {
            long[][] next = new long[MAX + 1][MAX + 1];

            for (int g1 = 0; g1 <= MAX; g1++) {
                for (int g2 = 0; g2 <= MAX; g2++) {
                    long cur = dp[g1][g2];
                    if (cur == 0) continue;

                    // skip
                    next[g1][g2] = (next[g1][g2] + cur) % MOD;

                    // put in seq1
                    int ng1 = (g1 == 0 ? x : gcd(g1, x));
                    next[ng1][g2] = (next[ng1][g2] + cur) % MOD;

                    // put in seq2
                    int ng2 = (g2 == 0 ? x : gcd(g2, x));
                    next[g1][ng2] = (next[g1][ng2] + cur) % MOD;
                }
            }

            dp = next;
        }

        long ans = 0;
        for (int g = 1; g <= MAX; g++) {
            ans = (ans + dp[g][g]) % MOD;
        }

        return (int) ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n * 201 * 201)
Space: O(201 * 201)
```

With:

```text
n <= 200
nums[i] <= 200
```

this is efficient.

---

# Final Takeaway

The key shift is:

Do not think of this as choosing two subsequences explicitly.

Think of it as processing each element with 3 choices and tracking only:

- gcd of subsequence 1
- gcd of subsequence 2

Because gcd values are small, the exponential search collapses into a small DP over gcd states.
