# Probability of Exactly `target` Heads — Detailed Summary

## Problem

You are given an array:

```text
prob[i]
```

where `prob[i]` is the probability that the `i`-th coin lands **heads**.

You toss every coin exactly once.

Return the probability that the total number of coins showing heads is exactly:

```text
target
```

---

# Core idea

Each coin contributes to the head count in one of two ways:

- it lands **tails**, so the number of heads does **not** increase
- it lands **heads**, so the number of heads increases by **1**

This is a classic dynamic programming problem where the DP state stores a **probability**, not a minimum or maximum.

---

# Main DP definition

Let:

```text
dp[i][j]
```

mean:

> the probability of getting exactly `j` heads after tossing the first `i` coins

This is the most natural state.

So:

- `i` = how many coins we have already processed
- `j` = how many heads we want among those `i` coins

Our final answer is:

```text
dp[n][target]
```

where:

```text
n = prob.length
```

---

# Why this state works

To get exactly `j` heads after `i` coins, the `i`-th coin has only two possibilities:

## Case 1: the `i`-th coin is tails

Then the number of heads remains `j`.

So we come from:

```text
dp[i-1][j]
```

and multiply by:

```text
1 - prob[i-1]
```

because the current coin lands tails.

Contribution:

```text
dp[i-1][j] * (1 - prob[i-1])
```

---

## Case 2: the `i`-th coin is heads

Then before tossing this coin, we must have had exactly `j - 1` heads.

So we come from:

```text
dp[i-1][j-1]
```

and multiply by:

```text
prob[i-1]
```

because the current coin lands heads.

Contribution:

```text
dp[i-1][j-1] * prob[i-1]
```

---

# Recurrence

Combining the two cases:

```text
dp[i][j] =
    dp[i-1][j]   * (1 - prob[i-1])
  + dp[i-1][j-1] * prob[i-1]
```

This is the full transition.

---

# Base case

Before tossing any coins:

```text
dp[0][0] = 1
```

Why?

Because with 0 coins, the probability of getting exactly 0 heads is 1.

Also:

```text
dp[0][j] = 0 for j > 0
```

because it is impossible to get positive heads from zero coins.

---

# Bounds and validity

For any `i`:

- `j` cannot exceed `i`
- but for implementation, it is often fine to loop up to `target`

Invalid states naturally remain `0`.

---

# Full 2D DP approach

## Java implementation

```java
class Solution {
    public double probabilityOfHeads(double[] prob, int target) {
        int n = prob.length;

        double[][] dp = new double[n + 1][target + 1];
        dp[0][0] = 1.0;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= target; j++) {

                // Current coin lands tails
                dp[i][j] += dp[i - 1][j] * (1.0 - prob[i - 1]);

                // Current coin lands heads
                if (j > 0) {
                    dp[i][j] += dp[i - 1][j - 1] * prob[i - 1];
                }
            }
        }

        return dp[n][target];
    }
}
```

---

# Step-by-step dry run

## Example

```text
prob = [0.4, 0.6]
target = 1
```

There are 2 coins.

We build the table `dp[i][j]`.

---

## Initialization

```text
dp[0][0] = 1.0
dp[0][1] = 0.0
```

So before tossing any coin:

```text
0 heads with probability 1
1 head with probability 0
```

---

## Process first coin (`p = 0.4`)

### `j = 0`

Only way is tails:

```text
dp[1][0] = dp[0][0] * (1 - 0.4)
         = 1.0 * 0.6
         = 0.6
```

### `j = 1`

Only way is heads:

```text
dp[1][1] = dp[0][1] * 0.6 + dp[0][0] * 0.4
         = 0 + 0.4
         = 0.4
```

Now:

```text
dp[1][0] = 0.6
dp[1][1] = 0.4
```

---

## Process second coin (`p = 0.6`)

### `j = 0`

Both coins must be tails:

```text
dp[2][0] = dp[1][0] * (1 - 0.6)
         = 0.6 * 0.4
         = 0.24
```

### `j = 1`

Two possibilities:

- first coin had 1 head, second coin tails
- first coin had 0 heads, second coin heads

So:

```text
dp[2][1]
= dp[1][1] * 0.4 + dp[1][0] * 0.6
= 0.4 * 0.4 + 0.6 * 0.6
= 0.16 + 0.36
= 0.52
```

Final answer:

```text
0.52
```

---

# Manual probability verification

For:

```text
prob = [0.4, 0.6]
```

Exactly 1 head can happen in two ways:

## Outcome 1: Head, Tail

```text
0.4 * 0.4 = 0.16
```

## Outcome 2: Tail, Head

```text
0.6 * 0.6 = 0.36
```

Total:

```text
0.16 + 0.36 = 0.52
```

which matches the DP.

---

# Why this is like knapsack DP

This problem is structurally very similar to subset/count knapsack.

At each item (coin):

- either we do not increase the “count of heads”
- or we increase it by 1

The difference is that instead of counting ways, we accumulate **probabilities**.

So the state transition looks very much like counting subsets with exactly `j` selected items, except weighted by probabilities.

---

# Space optimization

Notice:

```text
dp[i][*]
```

depends only on:

```text
dp[i-1][*]
```

So we do not actually need the full 2D table.

We can compress the DP into one 1D array.

---

# Important detail in 1D DP

When updating in place, we must iterate:

```text
j from target down to 0
```

Why?

Because `dp[j]` for the current coin depends on the previous row’s `dp[j]` and `dp[j-1]`.

If we iterate left to right, `dp[j-1]` would already be overwritten, corrupting the transition.

Backward iteration preserves correctness.

---

# 1D DP recurrence

Let:

```text
dp[j]
```

mean the probability of getting exactly `j` heads after processing the coins seen so far.

When processing coin with head probability `p`:

```text
dp[j] = dp[j] * (1 - p) + dp[j - 1] * p
```

for `j > 0`, and:

```text
dp[0] = dp[0] * (1 - p)
```

because getting 0 heads still requires all processed coins to be tails.

---

# Space-optimized Java implementation

```java
class Solution {
    public double probabilityOfHeads(double[] prob, int target) {
        double[] dp = new double[target + 1];
        dp[0] = 1.0;

        for (double p : prob) {
            for (int j = target; j >= 0; j--) {
                dp[j] = dp[j] * (1.0 - p) + (j > 0 ? dp[j - 1] * p : 0.0);
            }
        }

        return dp[target];
    }
}
```

---

# Slightly more explicit 1D version

Some people find this clearer:

```java
class Solution {
    public double probabilityOfHeads(double[] prob, int target) {
        double[] dp = new double[target + 1];
        dp[0] = 1.0;

        for (double p : prob) {
            for (int j = target; j >= 1; j--) {
                dp[j] = dp[j] * (1.0 - p) + dp[j - 1] * p;
            }
            dp[0] = dp[0] * (1.0 - p);
        }

        return dp[target];
    }
}
```

This separates the `j = 0` case more clearly.

---

# Another dry run for intuition

## Example

```text
prob = [0.5, 0.5, 0.5]
target = 2
```

Expected answer should be:

```text
3 * (0.5)^3 = 3/8 = 0.375
```

because exactly 2 heads can happen in 3 choose 2 = 3 ways.

Let us see the DP conceptually.

### After 0 coins

```text
dp = [1, 0, 0]
```

### After 1st coin

```text
dp = [0.5, 0.5, 0]
```

### After 2nd coin

```text
dp[0] = 0.25
dp[1] = 0.50
dp[2] = 0.25
```

### After 3rd coin

```text
dp[0] = 0.125
dp[1] = 0.375
dp[2] = 0.375
```

Answer:

```text
0.375
```

Matches expectation.

---

# Why the probabilities add

The two transition cases are **mutually exclusive**:

- current coin is tails
- current coin is heads

These events cannot both happen simultaneously.

So their probabilities add.

This is why the recurrence uses `+`.

---

# Why multiplication appears

For a full outcome sequence, probabilities of independent coin tosses multiply.

For example:

```text
P(Head then Tail) = P(Head) * P(Tail)
```

because coin tosses are independent.

That is why each DP transition multiplies the previous state probability by either:

```text
p
```

or:

```text
1 - p
```

---

# Edge cases

## 1. `target = 0`

Then we want all coins to land tails.

The answer becomes:

```text
(1 - prob[0]) * (1 - prob[1]) * ...
```

The DP handles this naturally.

---

## 2. `target > n`

Impossible.

If constraints allow this, the answer is `0`.

The DP also naturally returns `0` because those states cannot be reached.

---

## 3. Coin with `prob[i] = 0`

That coin always lands tails.

The transition becomes:

```text
dp[j] = dp[j]
```

for tails contribution, with no head contribution.

---

## 4. Coin with `prob[i] = 1`

That coin always lands heads.

The transition becomes:

```text
dp[j] = dp[j - 1]
```

because the head count must increase by exactly one.

---

# Complexity analysis

Let:

```text
n = prob.length
```

## 2D DP

### Time

We fill:

```text
(n + 1) * (target + 1)
```

states, each in `O(1)`.

So:

```text
Time = O(n * target)
```

### Space

```text
Space = O(n * target)
```

---

## 1D DP

### Time

Still:

```text
O(n * target)
```

because we still process each coin and each target count once.

### Space

```text
O(target)
```

which is much better.

---

# Common mistakes

## 1. Updating 1D DP from left to right

This is wrong because it overwrites states that are still needed.

Correct direction:

```text
from target down to 0
```

or `target down to 1` with separate handling of `dp[0]`.

---

## 2. Forgetting the tails contribution

Some incorrect solutions only add:

```text
dp[i-1][j-1] * prob[i-1]
```

but ignore the case where the current coin is tails.

Both branches are necessary.

---

## 3. Wrong base case

Correct:

```text
dp[0][0] = 1
```

Not `0`.

Before tossing any coins, getting exactly zero heads is certain.

---

## 4. Confusing this with expected value

This problem does **not** ask for expected number of heads.

It asks for the probability of getting **exactly** `target` heads.

That requires full distribution DP, not a simple sum of probabilities.

---

# Final recurrence summary

## 2D version

```text
dp[0][0] = 1
dp[i][j] =
    dp[i-1][j]   * (1 - prob[i-1])
  + dp[i-1][j-1] * prob[i-1]
```

---

## 1D version

For each coin probability `p`:

```text
for j from target down to 1:
    dp[j] = dp[j] * (1 - p) + dp[j - 1] * p

dp[0] = dp[0] * (1 - p)
```

---

# Final takeaway

This problem is a clean dynamic programming problem over:

- how many coins we have processed
- how many heads we currently have

The state stores **probabilities**, and each coin creates two transitions:

- tails → same head count
- heads → head count + 1

That leads to an elegant:

```text
O(n * target)
```

solution, with either:

- `O(n * target)` space using 2D DP
- or `O(target)` space using 1D DP

## Recommended implementation

For interviews and coding rounds, the **1D DP** solution is usually the best balance of clarity and efficiency.
