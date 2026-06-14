# 122. Best Time to Buy and Sell Stock II — Exhaustive Approach Summary (Java)

## Problem restatement

You are given an array `prices` where:

- `prices[i]` = stock price on day `i`

You may complete **as many transactions as you want**, with these rules:

- you may buy and/or sell on the same day
- you may hold **at most one share** at a time
- so before buying again, you must have sold the current share

Return the **maximum profit**.

---

## What makes this different from Stock I?

In **Best Time to Buy and Sell Stock I**, you were allowed only one buy and one sell.

Here, you can perform **multiple transactions**.

That changes the structure completely.

Now the question is no longer:

> What is the single best interval?

Instead it becomes:

> How do I capture all profitable upward movements while never holding more than one share?

---

## Core observation

If price goes up from one day to the next:

```text
prices[i] > prices[i - 1]
```

then that increase can contribute to the final profit.

Why?

Because any longer profitable rise can be decomposed into consecutive daily gains.

Example:

```text
buy at 1, sell at 5  => profit = 4
```

is equivalent to:

```text
(2 - 1) + (3 - 2) + (4 - 3) + (5 - 4) = 4
```

So the maximum profit is simply the sum of all positive day-to-day increases.

This is the most important insight in the problem.

---

# Approach 1 — Brute force recursion: try all valid actions

## Idea

At each day, you are in one of two states:

- **not holding** a stock
- **holding** a stock

Then recursively try all valid choices:

### If not holding:

- skip
- buy

### If holding:

- skip
- sell

Take the maximum profit among all possibilities.

This is the most direct state-space formulation.

---

## State definition

Let:

```text
dfs(day, holding)
```

be the maximum profit starting from `day` with the given holding status.

---

## Transitions

### If `holding == 0`

You may:

- do nothing → `dfs(day + 1, 0)`
- buy today → `-prices[day] + dfs(day + 1, 1)`

### If `holding == 1`

You may:

- do nothing → `dfs(day + 1, 1)`
- sell today → `prices[day] + dfs(day + 1, 0)`

---

## Base case

If `day == prices.length`, no more actions are possible:

```text
return 0
```

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        return dfs(prices, 0, 0);
    }

    private int dfs(int[] prices, int day, int holding) {
        if (day == prices.length) {
            return 0;
        }

        if (holding == 0) {
            int skip = dfs(prices, day + 1, 0);
            int buy = -prices[day] + dfs(prices, day + 1, 1);
            return Math.max(skip, buy);
        } else {
            int skip = dfs(prices, day + 1, 1);
            int sell = prices[day] + dfs(prices, day + 1, 0);
            return Math.max(skip, sell);
        }
    }
}
```

---

## Time complexity

Each day branches into choices, so worst-case complexity is exponential:

```text
O(2^n)
```

---

## Space complexity

Recursion depth:

```text
O(n)
```

---

## Strengths

- Very natural and complete formulation
- Good for discovering the DP states

## Weaknesses

- Too slow for the constraints
- Recomputes many identical states

---

# Approach 2 — Top-down DP with memoization

## Idea

The recursive state `(day, holding)` repeats many times.

So cache the answer for each state.

Since there are only:

- `n` possible days
- `2` holding states

the total number of unique states is just:

```text
O(n)
```

---

## State definition

```text
memo[day][holding] = maximum profit starting from this state
```

---

## Java code

```java
class Solution {
    private Integer[][] memo;

    public int maxProfit(int[] prices) {
        memo = new Integer[prices.length][2];
        return dfs(prices, 0, 0);
    }

    private int dfs(int[] prices, int day, int holding) {
        if (day == prices.length) {
            return 0;
        }

        if (memo[day][holding] != null) {
            return memo[day][holding];
        }

        int ans;
        if (holding == 0) {
            int skip = dfs(prices, day + 1, 0);
            int buy = -prices[day] + dfs(prices, day + 1, 1);
            ans = Math.max(skip, buy);
        } else {
            int skip = dfs(prices, day + 1, 1);
            int sell = prices[day] + dfs(prices, day + 1, 0);
            ans = Math.max(skip, sell);
        }

        memo[day][holding] = ans;
        return ans;
    }
}
```

---

## Time complexity

There are `2 * n` states, and each is computed once:

```text
O(n)
```

---

## Space complexity

- memo table: `O(n)`
- recursion stack: `O(n)`

Total auxiliary space:

```text
O(n)
```

---

## Why this is a big improvement

It turns a brute-force exponential exploration into a linear-state DP.

This is a good interview transition from brute force to optimized DP.

---

# Approach 3 — Bottom-up DP with 2 states per day

## Idea

Instead of recursion, define DP iteratively.

For each day `i`, track:

- the best profit when **not holding** a stock after day `i`
- the best profit when **holding** a stock after day `i`

This is the canonical stock-state-machine DP view.

---

## State definition

Let:

```text
dp[i][0] = max profit after processing day i, ending with no stock
dp[i][1] = max profit after processing day i, ending while holding one stock
```

---

## Transitions

### Not holding after day `i`

Either:

- you were already not holding yesterday and do nothing
- you were holding yesterday and sell today

So:

```text
dp[i][0] = max(dp[i-1][0], dp[i-1][1] + prices[i])
```

### Holding after day `i`

Either:

- you were already holding yesterday and do nothing
- you were not holding yesterday and buy today

So:

```text
dp[i][1] = max(dp[i-1][1], dp[i-1][0] - prices[i])
```

---

## Initialization

On day 0:

- if not holding, profit = `0`
- if holding, profit = `-prices[0]`

So:

```text
dp[0][0] = 0
dp[0][1] = -prices[0]
```

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int[][] dp = new int[n][2];

        dp[0][0] = 0;
        dp[0][1] = -prices[0];

        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i]);
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 1][0] - prices[i]);
        }

        return dp[n - 1][0];
    }
}
```

---

## Time complexity

```text
O(n)
```

---

## Space complexity

```text
O(n)
```

---

## Why this formulation is important

This exact state-machine template generalizes to harder stock problems:

- cooldown
- transaction fee
- at most `k` transactions
- exactly `k` transactions

So learning this form is extremely valuable.

---

# Approach 4 — Bottom-up DP with O(1) space

## Idea

In Approach 3, each day depends only on the previous day.

So we do not need the entire DP table.

Just keep two variables:

- `cash` = max profit when not holding
- `hold` = max profit when holding

---

## Transition

For each price:

```text
newCash = max(cash, hold + price)
newHold = max(hold, cash - price)
```

Then update.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int cash = 0;
        int hold = -prices[0];

        for (int i = 1; i < prices.length; i++) {
            int newCash = Math.max(cash, hold + prices[i]);
            int newHold = Math.max(hold, cash - prices[i]);

            cash = newCash;
            hold = newHold;
        }

        return cash;
    }
}
```

---

## Time complexity

```text
O(n)
```

---

## Space complexity

```text
O(1)
```

---

## Why this is a strong DP answer

It is optimal and elegant while still preserving the general stock-DP reasoning.

This is often the best answer if the interviewer expects a DP/state-machine style solution.

---

# Approach 5 — Greedy: sum all positive increases

This is the most direct and simplest optimal solution for this specific problem.

## Idea

Whenever the price rises from one day to the next, that increase can be taken as profit.

So add up all positive differences:

```text
if prices[i] > prices[i - 1]:
    profit += prices[i] - prices[i - 1]
```

---

## Why this works

Consider a rising sequence:

```text
1, 2, 3, 4, 5
```

Buying at `1` and selling at `5` gives:

```text
5 - 1 = 4
```

But summing daily gains gives:

```text
(2 - 1) + (3 - 2) + (4 - 3) + (5 - 4) = 4
```

Exactly the same.

So any long profitable interval can be decomposed into daily profitable steps without losing total profit.

And because we are allowed multiple transactions, taking each small upward step is valid.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int profit = 0;

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }

        return profit;
    }
}
```

---

## Time complexity

Single pass:

```text
O(n)
```

---

## Space complexity

```text
O(1)
```

---

## Why greedy works only here

This greedy logic works because:

- unlimited transactions are allowed
- there is no transaction fee
- there is no cooldown
- you can immediately buy again after selling
- holding more than one stock is disallowed, but that does not block this strategy

In harder stock variants, this exact greedy rule usually breaks.

---

# Approach 6 — Valley and peak method

This is another greedy formulation.

## Idea

Instead of summing every positive adjacent difference, identify:

- a valley = local minimum where you buy
- a peak = local maximum where you sell

Repeat this over the array.

---

## Example

For:

```text
[7,1,5,3,6,4]
```

- valley = `1`, peak = `5` → profit `4`
- valley = `3`, peak = `6` → profit `3`

Total = `7`

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int i = 0;
        int n = prices.length;
        int profit = 0;

        while (i < n - 1) {
            while (i < n - 1 && prices[i] >= prices[i + 1]) {
                i++;
            }
            int valley = prices[i];

            while (i < n - 1 && prices[i] <= prices[i + 1]) {
                i++;
            }
            int peak = prices[i];

            profit += peak - valley;
        }

        return profit;
    }
}
```

---

## Time complexity

```text
O(n)
```

---

## Space complexity

```text
O(1)
```

---

## Relationship to positive-difference greedy

This method is equivalent to summing all positive adjacent differences.

It is just a grouped version.

For example:

```text
1 -> 2 -> 3 -> 4 -> 5
```

Valley-peak gives:

```text
5 - 1 = 4
```

Positive-difference method gives:

```text
(2-1)+(3-2)+(4-3)+(5-4)=4
```

Same answer.

---

# Approach 7 — Explicit interval decomposition view

This is more conceptual than practical.

## Idea

Any valid strategy can be thought of as picking a set of non-overlapping increasing intervals:

- buy at the start of an increasing run
- sell at the end

The total maximum profit is the sum of profits of all such profitable runs.

This leads directly to the greedy rule.

It is not usually coded separately, but it is a useful proof intuition.

---

# Detailed dry runs

## Example 1

```text
prices = [7,1,5,3,6,4]
```

### Greedy positive-difference method

Day-to-day changes:

- `1 - 7 = -6` → ignore
- `5 - 1 = 4` → take
- `3 - 5 = -2` → ignore
- `6 - 3 = 3` → take
- `4 - 6 = -2` → ignore

Total profit:

```text
4 + 3 = 7
```

---

## Example 2

```text
prices = [1,2,3,4,5]
```

Daily increases:

- `2 - 1 = 1`
- `3 - 2 = 1`
- `4 - 3 = 1`
- `5 - 4 = 1`

Total:

```text
1 + 1 + 1 + 1 = 4
```

Equivalent to buying once at `1` and selling once at `5`.

---

## Example 3

```text
prices = [7,6,4,3,1]
```

Every difference is non-positive, so total profit remains:

```text
0
```

---

# Formal intuition for greedy correctness

Suppose there is a monotonic increasing segment:

```text
a < b < c < d
```

Profit from buying at `a` and selling at `d` is:

```text
d - a
```

But this equals:

```text
(b-a) + (c-b) + (d-c)
```

So taking all individual increases gives exactly the same profit.

Now extend this logic across the whole array:

- whenever prices rise, capture the rise
- whenever prices fall, stop and restart later

This produces the global optimum because every profitable interval can be decomposed this way, and no profitable increase is missed.

---

# Common mistakes

## Mistake 1 — Reusing the Stock I solution

For Stock I, you only needed one best interval.

That solution returns the maximum single transaction profit, which is **not** enough here.

Example:

```text
[7,1,5,3,6,4]
```

Stock I answer = `5`
Stock II answer = `7`

So this problem is fundamentally different.

---

## Mistake 2 — Thinking multiple transactions force a complicated solution

Because multiple transactions are allowed, some people immediately jump to heavy DP.

DP is valid, but in this special case the greedy rule is enough.

Do not overcomplicate it.

---

## Mistake 3 — Believing buy/sell on same day matters here

The statement says you may buy and/or sell on the same day.

That mainly ensures model flexibility, but it does not change the final greedy answer.

Selling and immediately rebuying conceptually allows splitting long upward moves into smaller profitable steps.

---

## Mistake 4 — Trying to hold multiple shares

The problem explicitly forbids holding more than one share at a time.

Any solution that assumes multiple parallel buys is invalid.

---

## Mistake 5 — Missing why greedy fails in harder variants

The positive-difference greedy rule works only because there is:

- no fee
- no cooldown
- unlimited transactions
- no cap on number of transactions

As soon as one of those changes, DP/state-machine reasoning becomes necessary.

---

# Comparing all approaches

| Approach                    | Idea                                |     Time |  Space | Notes                               |
| --------------------------- | ----------------------------------- | -------: | -----: | ----------------------------------- |
| Plain recursion             | Try buy/sell/skip recursively       | `O(2^n)` | `O(n)` | Good for deriving states            |
| Memoized recursion          | Cache `(day, holding)`              |   `O(n)` | `O(n)` | Clean DP formulation                |
| Bottom-up 2-state DP        | Track holding / not holding per day |   `O(n)` | `O(n)` | Canonical stock DP                  |
| O(1)-space DP               | Compress 2-state DP                 |   `O(n)` | `O(1)` | Strong general answer               |
| Greedy positive differences | Sum every upward daily move         |   `O(n)` | `O(1)` | Best simple answer for this variant |
| Valley-peak greedy          | Buy valleys, sell peaks             |   `O(n)` | `O(1)` | Equivalent to positive differences  |

---

# Which solution should you give in interviews?

That depends on the interviewer’s flavor.

## Best practical answer for this exact problem

Use the **greedy positive-difference** solution.

It is:

- simplest
- optimal
- easy to explain
- specific to this problem’s structure

## Best answer if you want reusable stock-problem reasoning

Use the **O(1)-space DP state-machine** solution.

That shows understanding that generalizes to harder stock variants.

A very strong interview flow is:

1. Mention recursive states `(day, holding)`
2. Show DP formulation
3. Then observe that for this specific problem the DP collapses to a greedy rule:
   sum all positive adjacent differences

That is a strong, complete explanation.

---

# Recommended final interview solution for this exact problem

```java
class Solution {
    public int maxProfit(int[] prices) {
        int profit = 0;

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }

        return profit;
    }
}
```

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(1)
```

---

# Recommended DP/state-machine solution

```java
class Solution {
    public int maxProfit(int[] prices) {
        int cash = 0;
        int hold = -prices[0];

        for (int i = 1; i < prices.length; i++) {
            int newCash = Math.max(cash, hold + prices[i]);
            int newHold = Math.max(hold, cash - prices[i]);

            cash = newCash;
            hold = newHold;
        }

        return cash;
    }
}
```

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(1)
```

---

# Compact interview explanation

A concise greedy explanation:

> Since we can make unlimited transactions, every profitable upward move can be taken.
> A rise from `a` to `d` gives the same profit as summing all daily rises inside that interval.
> Therefore, the maximum profit is just the sum of all positive differences `prices[i] - prices[i-1]`.

A concise DP explanation:

> Track two states each day: the best profit when holding a stock and when not holding one.
> Update them using buy/sell/skip transitions.
> This gives an `O(n)` time and `O(1)` space state-machine solution.

---

# Final takeaway

This problem is a great example of a DP-looking problem that collapses into a simple greedy rule because of special constraints.

The key insight is:

> unlimited transactions + no fee + no cooldown
> ⇒ every positive local increase is worth taking

That is why the greedy solution is optimal here.

---

# Practice extension

The natural next problems are:

- Best Time to Buy and Sell Stock III
- Best Time to Buy and Sell Stock with Cooldown
- Best Time to Buy and Sell Stock with Transaction Fee

Those are where the simple greedy rule stops working and the full stock-state DP becomes essential.
