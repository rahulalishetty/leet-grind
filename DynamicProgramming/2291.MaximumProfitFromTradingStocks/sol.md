# Maximum Profit from Buying Stocks Within a Budget — Exhaustive Summary

## Problem Overview

We are given:

- a `0`-indexed integer array `present`
- a `0`-indexed integer array `future`
- an integer `budget`

Both arrays have the same length.

For each stock `i`:

- `present[i]` is the current price of the stock
- `future[i]` is the price of the stock one year later

We may buy each stock **at most once**.

Our total spending cannot exceed `budget`.

We must return the **maximum total profit** we can make.

---

## What is the profit from one stock?

If we buy stock `i`, then:

```text
profit[i] = future[i] - present[i]
```

So each stock has:

- **cost** = `present[i]`
- **profit/value** = `future[i] - present[i]`

---

# First Key Observation

If a stock has:

```text
future[i] <= present[i]
```

then buying it gives zero or negative profit.

That can never help maximize profit.

So such stocks can be ignored.

Only stocks with:

```text
future[i] > present[i]
```

are useful candidates.

---

# Reduction to 0/1 Knapsack

After that observation, the problem becomes:

> We have several items.
> Each item can be chosen at most once.
> Each item has:
>
> - weight = present[i]
> - value = future[i] - present[i]
>
> We want the maximum total value such that total weight does not exceed `budget`.

That is exactly the **0/1 Knapsack** problem.

---

# Why this is 0/1 knapsack and not something else

This is 0/1 knapsack because:

- each stock can be bought **at most once**
- there is a limited capacity: `budget`
- each chosen stock contributes a fixed profit

It is **not** unbounded knapsack, because we cannot buy the same stock multiple times.

It is **not** greedy, because choosing the stock with the highest individual profit first is not always optimal.

---

# Dynamic Programming State

Let:

```text
dp[b]
```

represent the **maximum profit** we can achieve using total spending at most `b`.

So:

- `dp[0] = 0`
- initially all values are `0`, because we can always choose to buy nothing

---

# Transition

Suppose for stock `i`:

- `cost = present[i]`
- `profit = future[i] - present[i]`

If we decide to buy it, then for a budget `b` where `b >= cost`, we can transition as:

```text
dp[b] = max(dp[b], dp[b - cost] + profit)
```

Interpretation:

- do not buy this stock → keep `dp[b]`
- buy this stock → previous best profit with budget `b - cost`, then add this stock’s profit

Take the better of the two.

---

# Why we must iterate the budget backward

This is extremely important.

When processing one stock, we must iterate:

```text
for (b = budget; b >= cost; b--)
```

and **not forward**.

### Why?

Because each stock can be used at most once.

If we iterate forward, then after updating `dp[b]`, that updated value may get reused again in the same iteration, effectively allowing the same stock to be taken multiple times.

That would incorrectly turn the problem into **unbounded knapsack**.

Backward iteration prevents this and preserves the 0/1 constraint.

---

# Algorithm

1. Initialize a DP array of size `budget + 1` with zeroes
2. Iterate through every stock
3. For each stock:
   - compute `cost = present[i]`
   - compute `profit = future[i] - present[i]`
4. If:
   - `profit <= 0`, skip the stock
   - `cost > budget`, skip the stock
5. Otherwise, iterate budget backward from `budget` down to `cost`
6. Update:

```text
dp[b] = max(dp[b], dp[b - cost] + profit)
```

7. Return `dp[budget]`

---

# Java Solution

```java
class Solution {
    public int maximumProfit(int[] present, int[] future, int budget) {
        int n = present.length;
        int[] dp = new int[budget + 1];

        for (int i = 0; i < n; i++) {
            int cost = present[i];
            int profit = future[i] - present[i];

            // Ignore non-profitable stocks
            if (profit <= 0 || cost > budget) continue;

            for (int b = budget; b >= cost; b--) {
                dp[b] = Math.max(dp[b], dp[b - cost] + profit);
            }
        }

        return dp[budget];
    }
}
```

---

# Step-by-Step Example

Consider:

```text
present = [5, 4, 6]
future  = [8, 5, 10]
budget  = 9
```

## Step 1: Compute profits

- stock `0`: cost `5`, profit `8 - 5 = 3`
- stock `1`: cost `4`, profit `5 - 4 = 1`
- stock `2`: cost `6`, profit `10 - 6 = 4`

So the items are:

| Stock | Cost | Profit |
| ----- | ---: | -----: |
| 0     |    5 |      3 |
| 1     |    4 |      1 |
| 2     |    6 |      4 |

---

## Step 2: Initialize DP

Budget is `9`, so:

```text
dp = [0,0,0,0,0,0,0,0,0,0]
```

Index `b` means maximum profit with budget at most `b`.

---

## Step 3: Process stock 0

Stock 0 has:

```text
cost = 5
profit = 3
```

Update budgets from `9` down to `5`:

- `dp[9] = max(dp[9], dp[4] + 3) = 3`
- `dp[8] = max(dp[8], dp[3] + 3) = 3`
- `dp[7] = max(dp[7], dp[2] + 3) = 3`
- `dp[6] = max(dp[6], dp[1] + 3) = 3`
- `dp[5] = max(dp[5], dp[0] + 3) = 3`

Now:

```text
dp = [0,0,0,0,0,3,3,3,3,3]
```

---

## Step 4: Process stock 1

Stock 1 has:

```text
cost = 4
profit = 1
```

Update budgets from `9` down to `4`:

- `dp[9] = max(3, dp[5] + 1) = max(3, 3 + 1) = 4`
- `dp[8] = max(3, dp[4] + 1) = 1 -> remains 3`
- `dp[7] = max(3, dp[3] + 1) = 1 -> remains 3`
- `dp[6] = max(3, dp[2] + 1) = 1 -> remains 3`
- `dp[5] = max(3, dp[1] + 1) = 1 -> remains 3`
- `dp[4] = max(0, dp[0] + 1) = 1`

Now:

```text
dp = [0,0,0,0,1,3,3,3,3,4]
```

Interpretation:

With budget `9`, we can now buy stock `0` and stock `1` together:

- cost = `5 + 4 = 9`
- profit = `3 + 1 = 4`

---

## Step 5: Process stock 2

Stock 2 has:

```text
cost = 6
profit = 4
```

Update budgets from `9` down to `6`:

- `dp[9] = max(4, dp[3] + 4) = 4`
- `dp[8] = max(3, dp[2] + 4) = 4`
- `dp[7] = max(3, dp[1] + 4) = 4`
- `dp[6] = max(3, dp[0] + 4) = 4`

Final:

```text
dp = [0,0,0,0,1,3,4,4,4,4]
```

So the answer is:

```text
dp[9] = 4
```

---

# Why the answer is 4 in the example

Possible valid choices within budget `9`:

### Buy stock 0 only

- cost = `5`
- profit = `3`

### Buy stock 1 only

- cost = `4`
- profit = `1`

### Buy stock 2 only

- cost = `6`
- profit = `4`

### Buy stock 0 + stock 1

- cost = `5 + 4 = 9`
- profit = `3 + 1 = 4`

### Buy stock 1 + stock 2

- cost = `4 + 6 = 10` → exceeds budget

### Buy stock 0 + stock 2

- cost = `5 + 6 = 11` → exceeds budget

Thus maximum profit is:

```text
4
```

---

# Another Example

Consider:

```text
present = [2, 3, 4]
future  = [3, 10, 5]
budget  = 5
```

Profits:

- stock 0: `1`
- stock 1: `7`
- stock 2: `1`

Possible choices:

### Buy stock 1 only

- cost = `3`
- profit = `7`

### Buy stock 0 + stock 1

- cost = `2 + 3 = 5`
- profit = `1 + 7 = 8`

### Buy stock 0 + stock 2

- cost = `6` → too much

### Buy stock 2 only

- profit = `1`

Answer is:

```text
8
```

This shows why a greedy choice like “take the stock with highest profit first” is not always enough.
We must consider combinations.

---

# Why greedy does not work reliably

A tempting idea is:

> Always buy the stock with the highest profit first

That fails because budget is limited, and sometimes a combination of smaller-profit stocks gives a better total.

Example:

```text
present = [4, 5, 2]
future  = [7, 9, 4]
budget  = 6
```

Profits:

- stock 0: `3`
- stock 1: `4`
- stock 2: `2`

Greedy may choose stock 1 first:

- cost `5`, profit `4`

Then only 1 budget remains, and nothing else fits.

Total = `4`

But better is:

- stock 0 + stock 2
- cost `4 + 2 = 6`
- profit `3 + 2 = 5`

So greedy misses the optimum.

That is why dynamic programming is required.

---

# Correctness Intuition

The DP works because of optimal substructure.

For each stock, there are only two choices:

### 1. Do not buy the stock

Then the best profit remains whatever it already was.

### 2. Buy the stock

Then we must have enough remaining budget, and the best total profit becomes:

```text
(best profit with remaining budget) + current stock profit
```

By trying both possibilities for every stock and every budget, DP guarantees that all valid combinations are considered exactly once in the 0/1 sense.

---

# Formal DP Interpretation

For each stock `i` and budget `b`, the optimal answer depends only on smaller subproblems.

If stock `i` is not taken:

```text
answer stays as previous best
```

If stock `i` is taken:

```text
answer = dp[b - cost] + profit
```

Then:

```text
dp[b] = max(skip, take)
```

This recurrence is the standard 0/1 knapsack recurrence.

---

# Complexity Analysis

Let:

- `n = present.length`
- `B = budget`

## Time Complexity

For each of the `n` stocks, we iterate through up to `B` budgets.

So:

```text
Time Complexity = O(n * B)
```

---

## Space Complexity

We use a 1D DP array of size:

```text
budget + 1
```

So:

```text
Space Complexity = O(B)
```

This is the optimized version of 0/1 knapsack.

A 2D DP would use `O(n * B)`, but it is unnecessary because we only need the previous row, which can be compressed into one array.

---

# Common Pitfalls

## 1. Forgetting to ignore non-profitable stocks

If:

```text
future[i] <= present[i]
```

then buying that stock cannot improve the answer.

These can safely be skipped.

---

## 2. Iterating budget forward instead of backward

This is the most common mistake.

If you do:

```java
for (int b = cost; b <= budget; b++)
```

then the same stock may be used multiple times in one iteration.

That would be incorrect.

You must iterate backward:

```java
for (int b = budget; b >= cost; b--)
```

---

## 3. Thinking `dp[b]` means exactly budget `b` must be used

In practice here, `dp[b]` represents the best profit achievable with capacity up to `b`.

It is perfectly fine if the chosen stocks use less than `b`.

---

## 4. Using a greedy approach

This problem is about optimal subset selection under a budget constraint.
That is a knapsack pattern, not a greedy one.

---

# Alternative 2D DP View

For completeness, the problem can also be written using a 2D DP:

Let:

```text
dp[i][b]
```

be the maximum profit using the first `i` stocks with budget `b`.

Transition:

```text
dp[i][b] = dp[i - 1][b]
```

and if the `i`-th stock fits:

```text
dp[i][b] = max(dp[i][b], dp[i - 1][b - cost] + profit)
```

This is correct, but uses more space.

The 1D version is just the space-optimized form of this standard recurrence.

---

# 2D DP Code Example

```java
class Solution {
    public int maximumProfit(int[] present, int[] future, int budget) {
        int n = present.length;
        int[][] dp = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            int cost = present[i - 1];
            int profit = future[i - 1] - present[i - 1];

            for (int b = 0; b <= budget; b++) {
                dp[i][b] = dp[i - 1][b];

                if (profit > 0 && b >= cost) {
                    dp[i][b] = Math.max(dp[i][b], dp[i - 1][b - cost] + profit);
                }
            }
        }

        return dp[n][budget];
    }
}
```

This version is easier to understand initially, but less space-efficient.

---

# Preferred Optimized Solution

```java
class Solution {
    public int maximumProfit(int[] present, int[] future, int budget) {
        int n = present.length;
        int[] dp = new int[budget + 1];

        for (int i = 0; i < n; i++) {
            int cost = present[i];
            int profit = future[i] - present[i];

            if (profit <= 0 || cost > budget) continue;

            for (int b = budget; b >= cost; b--) {
                dp[b] = Math.max(dp[b], dp[b - cost] + profit);
            }
        }

        return dp[budget];
    }
}
```

---

# Summary Table

| Concept                    | Meaning                                     |
| -------------------------- | ------------------------------------------- |
| Item cost                  | `present[i]`                                |
| Item profit                | `future[i] - present[i]`                    |
| Can choose multiple times? | No                                          |
| Problem type               | 0/1 Knapsack                                |
| DP state                   | `dp[b]` = max profit with budget `b`        |
| Transition                 | `dp[b] = max(dp[b], dp[b - cost] + profit)` |
| Budget loop direction      | Backward                                    |
| Time complexity            | `O(n * budget)`                             |
| Space complexity           | `O(budget)`                                 |

---

# Final Takeaway

The core trick is recognizing that this stock-buying problem is not really about stock markets.
It is a classic **0/1 knapsack** disguised in a finance story.

Each stock is simply:

- an item with a buying cost
- a resulting profit
- and a restriction that it may be chosen at most once

Once you see that, the solution becomes standard:

- ignore non-profitable stocks
- run 0/1 knapsack DP
- iterate the budget backward
- return the best profit within the given budget
