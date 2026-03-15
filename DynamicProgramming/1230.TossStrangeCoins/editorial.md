# 1230. Toss Strange Coins — Detailed Summary of 3 Approaches

## Problem

You are given an array:

```text
prob[i]
```

where `prob[i]` is the probability that the `i`-th coin lands **heads**.

You toss every coin exactly once.

Return the probability that the total number of heads is exactly:

```text
target
```

---

# High-level idea

Each coin has two outcomes:

- **heads** with probability `prob[i]`
- **tails** with probability `1 - prob[i]`

We want the probability that after all tosses, the number of heads is exactly `target`.

This naturally leads to dynamic programming because:

- after processing some prefix of coins,
- the only thing that matters is how many heads we still need or already have.

The three approaches below are all based on the same state transition:

- either the current coin is heads
- or the current coin is tails

The difference is only in **how the DP is organized**:

1. recursive DP with memoization
2. bottom-up 2D DP
3. bottom-up 1D DP with space optimization

---

# Probability reasoning

Suppose we are currently considering coin `i`.

To get exactly `target` heads overall:

## Case 1: current coin is heads

Then we need:

```text
target - 1
```

heads from the remaining coins.

Contribution:

```text
prob[i] * probability(remaining coins give target - 1 heads)
```

## Case 2: current coin is tails

Then we still need:

```text
target
```

heads from the remaining coins.

Contribution:

```text
(1 - prob[i]) * probability(remaining coins give target heads)
```

Because these two cases are mutually exclusive and together cover all possibilities, we add them.

That is the central recurrence behind all three solutions.

---

# Approach 1: Recursive Dynamic Programming (Top-Down Memoization)

## Intuition

Imagine we process coins from left to right using recursion.

Define a function:

```text
findProbability(index, target)
```

which means:

> probability of getting exactly `target` heads using coins from `index` to `n - 1`

This is a very natural recursive definition.

At each step, coin `index` either:

- lands heads → reduce target by 1
- lands tails → target stays the same

So:

```text
findProbability(index, target)
=
findProbability(index + 1, target - 1) * prob[index]
+
findProbability(index + 1, target) * (1 - prob[index])
```

---

## Base cases

### 1. `target < 0`

This means we have already taken more heads than needed.

Impossible state.

So:

```text
return 0
```

### 2. `index == n`

We have processed all coins.

Now:

- if `target == 0`, we have achieved exactly the required number of heads
- otherwise, we failed

So:

```text
return 1 if target == 0 else 0
```

---

## Why memoization is needed

Without memoization, the recursion explores many repeated subproblems.

For example:

- `findProbability(3, 2)`
- `findProbability(3, 1)`

may be reached from multiple paths in the recursion tree.

So we cache results in:

```text
memo[index][target]
```

Once a state is computed, we reuse it immediately.

This reduces exponential recursion to polynomial time.

---

## Algorithm

1. Let `n = prob.length`.
2. Create memo table:

```text
memo[n][target + 1]
```

initialized with `-1`. 3. Define recursive function:

- if `target < 0`, return `0`
- if `index == n`, return `1` if `target == 0`, else `0`
- if already computed, return memo value
- otherwise compute both head and tail branches

4. Return:

```text
findProbability(0, target)
```

---

## Java implementation

```java
import java.util.*;

class Solution {
    private double findProbability(int index, int n, double[][] memo, double[] prob, int target) {
        // More heads than needed
        if (target < 0) {
            return 0.0;
        }

        // All coins processed
        if (index == n) {
            return target == 0 ? 1.0 : 0.0;
        }

        if (memo[index][target] != -1.0) {
            return memo[index][target];
        }

        memo[index][target] =
                findProbability(index + 1, n, memo, prob, target - 1) * prob[index]
              + findProbability(index + 1, n, memo, prob, target) * (1.0 - prob[index]);

        return memo[index][target];
    }

    public double probabilityOfHeads(double[] prob, int target) {
        int n = prob.length;
        double[][] memo = new double[n][target + 1];

        for (double[] row : memo) {
            Arrays.fill(row, -1.0);
        }

        return findProbability(0, n, memo, prob, target);
    }
}
```

---

## Complexity analysis

Let:

```text
n = prob.length
```

### Time complexity

There are at most:

```text
n * (target + 1)
```

distinct states.

Each state is computed once.

So:

```text
Time = O(n * target)
```

### Space complexity

Memo table:

```text
O(n * target)
```

Recursion stack depth:

```text
O(n)
```

Overall auxiliary space is dominated by the memo table:

```text
O(n * target)
```

---

# Approach 2: Iterative Dynamic Programming (Bottom-Up 2D)

## Intuition

Instead of solving states recursively, build them iteratively.

Define:

```text
dp[i][j]
```

as:

> probability of getting exactly `j` heads using the first `i` coins

This is a standard bottom-up reformulation of the same recurrence.

We want:

```text
dp[n][target]
```

---

## State transition

To compute `dp[i][j]`, consider the `i`-th coin (0-based in `prob`, so its probability is `prob[i - 1]`).

### If current coin is heads

Then the previous `i - 1` coins must contain exactly `j - 1` heads.

Contribution:

```text
dp[i - 1][j - 1] * prob[i - 1]
```

### If current coin is tails

Then the previous `i - 1` coins must contain exactly `j` heads.

Contribution:

```text
dp[i - 1][j] * (1 - prob[i - 1])
```

So:

```text
dp[i][j] = dp[i - 1][j - 1] * prob[i - 1]
         + dp[i - 1][j] * (1 - prob[i - 1])
```

---

## Base case

Before processing any coin:

```text
dp[0][0] = 1
```

because getting 0 heads from 0 coins is certain.

And:

```text
dp[0][j] = 0 for j > 0
```

because you cannot get positive heads from zero coins.

---

## Special case for `j = 0`

To get 0 heads using first `i` coins, all of them must be tails.

So:

```text
dp[i][0] = dp[i - 1][0] * (1 - prob[i - 1])
```

---

## Algorithm

1. Let `n = prob.length`.
2. Create DP table:

```text
dp[n + 1][target + 1]
```

3. Set `dp[0][0] = 1`.
4. For `i = 1..n`:
   - compute `dp[i][0]`
   - for `j = 1..target`, compute the transition
   - optional optimization: stop when `j > i`
5. Return `dp[n][target]`.

---

## Java implementation

```java
class Solution {
    public double probabilityOfHeads(double[] prob, int target) {
        int n = prob.length;
        double[][] dp = new double[n + 1][target + 1];
        dp[0][0] = 1.0;

        for (int i = 1; i <= n; i++) {
            dp[i][0] = dp[i - 1][0] * (1.0 - prob[i - 1]);

            for (int j = 1; j <= target && j <= i; j++) {
                dp[i][j] =
                        dp[i - 1][j - 1] * prob[i - 1]
                      + dp[i - 1][j] * (1.0 - prob[i - 1]);
            }
        }

        return dp[n][target];
    }
}
```

---

## Complexity analysis

### Time complexity

We fill about:

```text
n * target
```

states.

So:

```text
Time = O(n * target)
```

### Space complexity

We store the full 2D DP table:

```text
Space = O(n * target)
```

---

# Approach 3: Dynamic Programming with Space Optimization

## Intuition

From the 2D transition:

```text
dp[i][j] = dp[i - 1][j - 1] * prob[i - 1]
         + dp[i - 1][j] * (1 - prob[i - 1])
```

we see that row `i` depends only on row `i - 1`.

So we do not need the full `n x target` table.

We can compress it into a single 1D array:

```text
dp[j]
```

where after processing some prefix of coins, `dp[j]` means the probability of getting exactly `j` heads.

---

## Why reverse iteration is necessary

Suppose we update `dp[j]` in-place.

The formula needs:

- old `dp[j]`
- old `dp[j - 1]`

If we iterate from left to right, `dp[j - 1]` would already be overwritten by the current coin’s update.

That would corrupt the recurrence.

So we must iterate:

```text
j = target down to 1
```

This ensures `dp[j - 1]` still refers to the previous row when used.

---

## 1D transition

When processing coin with probability `p = prob[i - 1]`:

For `j >= 1`:

```text
dp[j] = dp[j - 1] * p + dp[j] * (1 - p)
```

For `j = 0`:

```text
dp[0] = dp[0] * (1 - p)
```

because getting 0 heads still means all processed coins so far must be tails.

---

## Algorithm

1. Create 1D array:

```text
dp[target + 1]
```

2. Set:

```text
dp[0] = 1
```

3. For each coin:
   - update `dp[j]` from `target` down to `1`
   - then update `dp[0]`
4. Return `dp[target]`.

---

## Java implementation

```java
class Solution {
    public double probabilityOfHeads(double[] prob, int target) {
        int n = prob.length;
        double[] dp = new double[target + 1];
        dp[0] = 1.0;

        for (int i = 1; i <= n; i++) {
            for (int j = target; j >= 1; j--) {
                dp[j] = dp[j - 1] * prob[i - 1] + dp[j] * (1.0 - prob[i - 1]);
            }
            dp[0] = dp[0] * (1.0 - prob[i - 1]);
        }

        return dp[target];
    }
}
```

---

## Equivalent clearer version

Some people find this version easier to read:

```java
class Solution {
    public double probabilityOfHeads(double[] prob, int target) {
        double[] dp = new double[target + 1];
        dp[0] = 1.0;

        for (double p : prob) {
            for (int j = target; j >= 1; j--) {
                dp[j] = dp[j - 1] * p + dp[j] * (1.0 - p);
            }
            dp[0] *= (1.0 - p);
        }

        return dp[target];
    }
}
```

---

## Complexity analysis

### Time complexity

We still process every coin and every target count:

```text
Time = O(n * target)
```

### Space complexity

Only one 1D array of length `target + 1` is stored:

```text
Space = O(target)
```

This is the best among the three DP approaches.

---

# Worked example

## Input

```text
prob = [0.5, 0.5, 0.5]
target = 2
```

Expected probability:

Exactly 2 heads out of 3 fair coins happens in 3 ways:

- HHT
- HTH
- THH

Each has probability:

```text
0.5 * 0.5 * 0.5 = 0.125
```

So total is:

```text
3 * 0.125 = 0.375
```

---

## 2D DP view

### Initial state

```text
dp[0][0] = 1
dp[0][1] = 0
dp[0][2] = 0
```

### After first coin

```text
dp[1][0] = 0.5
dp[1][1] = 0.5
dp[1][2] = 0
```

### After second coin

```text
dp[2][0] = 0.25
dp[2][1] = 0.50
dp[2][2] = 0.25
```

### After third coin

```text
dp[3][0] = 0.125
dp[3][1] = 0.375
dp[3][2] = 0.375
```

So answer is:

```text
0.375
```

---

# Why probabilities are multiplied and added

## Multiplication

When specific independent outcomes must happen in sequence, probabilities multiply.

Example:

```text
P(H then T) = P(H) * P(T)
```

because tosses are independent.

## Addition

If two disjoint cases both lead to the desired result, add their probabilities.

Example for exactly `j` heads after current coin:

- current coin heads
- current coin tails

These are mutually exclusive, so:

```text
total probability = probability(case 1) + probability(case 2)
```

That is why the recurrence uses both multiplication and addition.

---

# Comparing the three approaches

## Approach 1: Recursive DP with memoization

### Pros

- very intuitive if you think recursively
- maps directly to “try heads / try tails”

### Cons

- recursion overhead
- uses extra stack space
- usually slightly less practical than iterative DP

---

## Approach 2: Iterative 2D DP

### Pros

- straightforward bottom-up table
- easy to debug
- very explicit state transitions

### Cons

- uses `O(n * target)` space

---

## Approach 3: Iterative 1D DP

### Pros

- same time complexity as 2D DP
- only `O(target)` space
- usually the best final implementation

### Cons

- reverse iteration detail is subtle
- easier to make indexing mistakes

---

# Common mistakes

## 1. Updating 1D DP left to right

This is wrong because `dp[j - 1]` gets overwritten too early.

Correct:

```text
for j from target down to 1
```

---

## 2. Forgetting the tails branch

The transition must include both:

- heads contribution
- tails contribution

Wrong solutions often only consider the head case.

---

## 3. Wrong base case

Correct:

```text
dp[0][0] = 1
```

not `0`.

With 0 coins, the probability of exactly 0 heads is certain.

---

## 4. Confusing this with expected value

This problem does not ask for the expected number of heads.

It asks for the probability of **exactly** `target` heads.

That requires DP over the full distribution.

---

# Final recurrence summary

## Top-down version

```text
findProbability(index, target)
=
findProbability(index + 1, target - 1) * prob[index]
+
findProbability(index + 1, target) * (1 - prob[index])
```

Base cases:

```text
if target < 0: return 0
if index == n: return 1 if target == 0 else 0
```

---

## Bottom-up 2D version

```text
dp[0][0] = 1

dp[i][j]
=
dp[i - 1][j - 1] * prob[i - 1]
+
dp[i - 1][j] * (1 - prob[i - 1])
```

with:

```text
dp[i][0] = dp[i - 1][0] * (1 - prob[i - 1])
```

---

## Space-optimized 1D version

For each coin probability `p`:

```text
for j from target down to 1:
    dp[j] = dp[j - 1] * p + dp[j] * (1 - p)

dp[0] = dp[0] * (1 - p)
```

---

# Final takeaway

All three approaches solve the same probability DP.

The central idea is always:

- process coins one by one
- track the probability of having exactly `j` heads
- update using the two possible outcomes of the current coin

## Final complexity summary

Let:

```text
n = prob.length
```

### Approach 1: Recursive DP

```text
Time:  O(n * target)
Space: O(n * target)
```

### Approach 2: Iterative 2D DP

```text
Time:  O(n * target)
Space: O(n * target)
```

### Approach 3: 1D Space-Optimized DP

```text
Time:  O(n * target)
Space: O(target)
```

For interviews and production code, the **1D DP** solution is usually the best final version.
