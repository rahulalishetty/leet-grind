# 188. Best Time to Buy and Sell Stock IV — Exhaustive Approach Summary (Java)

## Problem restatement

You are given:

- an integer array `prices`, where `prices[i]` is the stock price on day `i`
- an integer `k`

You may complete **at most `k` transactions**.

Each transaction is:

- one buy
- followed later by one sell

Rules:

- you cannot hold more than one share at a time
- so you must sell before buying again

Return the **maximum profit**.

---

## Why this problem matters

This is the general form of the classic stock DP family.

It generalizes:

- **Stock I** → `k = 1`
- **Stock III** → `k = 2`
- **Stock II** → effectively unlimited `k`

So if you understand this problem deeply, the whole stock series becomes much easier to organize mentally.

---

## Core state intuition

A valid sequence of actions for at most `k` transactions looks like:

```text
buy1 -> sell1 -> buy2 -> sell2 -> ... -> buyk -> sellk
```

That suggests two broad DP views:

1. **Day + holding + transaction count**
2. **Buy/sell phase states**

Both are correct. Some are more intuitive, some are more optimized.

---

# Approach 1 — Brute force recursion over all choices

## Idea

At each day, depending on whether you are holding a stock or not, decide:

- skip
- buy
- sell

And track how many transactions remain.

This is the most direct state-space exploration.

---

## State definition

Let:

```text
dfs(day, transactionsLeft, holding)
```

mean the maximum profit from `day` onward.

Where:

- `day` = current index
- `transactionsLeft` = how many completed sells you can still make
- `holding` = `0` or `1`

We count a transaction as completed when we **sell**.

---

## Transitions

### If not holding

You may:

- skip:
  ```text
  dfs(day + 1, transactionsLeft, 0)
  ```
- buy:
  ```text
  -prices[day] + dfs(day + 1, transactionsLeft, 1)
  ```

### If holding

You may:

- skip:
  ```text
  dfs(day + 1, transactionsLeft, 1)
  ```
- sell, if `transactionsLeft > 0`:
  ```text
  prices[day] + dfs(day + 1, transactionsLeft - 1, 0)
  ```

---

## Base case

If `day == prices.length`, return `0`.

No more days remain, so no more profit can be added.

---

## Java code

```java
class Solution {
    public int maxProfit(int k, int[] prices) {
        return dfs(prices, 0, k, 0);
    }

    private int dfs(int[] prices, int day, int transactionsLeft, int holding) {
        if (day == prices.length) {
            return 0;
        }

        if (holding == 0) {
            int skip = dfs(prices, day + 1, transactionsLeft, 0);
            int buy = -prices[day] + dfs(prices, day + 1, transactionsLeft, 1);
            return Math.max(skip, buy);
        } else {
            int skip = dfs(prices, day + 1, transactionsLeft, 1);
            int sell = Integer.MIN_VALUE;

            if (transactionsLeft > 0) {
                sell = prices[day] + dfs(prices, day + 1, transactionsLeft - 1, 0);
            }

            return Math.max(skip, sell);
        }
    }
}
```

---

## Time complexity

This is exponential because at each day there are branching choices:

```text
O(2^n)
```

in the worst case.

---

## Space complexity

Recursion depth is at most `n`:

```text
O(n)
```

---

## Strengths

- Very intuitive
- Good for discovering the DP state

## Weaknesses

- Far too slow
- Repeats the same states many times

---

# Approach 2 — Top-down DP with memoization

## Idea

The brute force recursion revisits the same state repeatedly.

But the number of unique states is limited:

- `day` has `n` possibilities
- `transactionsLeft` has `k + 1` possibilities
- `holding` has `2` possibilities

So total states are:

```text
O(n * k * 2) = O(nk)
```

Memoization makes the solution efficient.

---

## Java code

```java
class Solution {
    private Integer[][][] memo;

    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        memo = new Integer[n][k + 1][2];
        return dfs(prices, 0, k, 0);
    }

    private int dfs(int[] prices, int day, int transactionsLeft, int holding) {
        if (day == prices.length) {
            return 0;
        }

        if (memo[day][transactionsLeft][holding] != null) {
            return memo[day][transactionsLeft][holding];
        }

        int ans;

        if (holding == 0) {
            int skip = dfs(prices, day + 1, transactionsLeft, 0);
            int buy = -prices[day] + dfs(prices, day + 1, transactionsLeft, 1);
            ans = Math.max(skip, buy);
        } else {
            int skip = dfs(prices, day + 1, transactionsLeft, 1);
            int sell = (transactionsLeft > 0)
                ? prices[day] + dfs(prices, day + 1, transactionsLeft - 1, 0)
                : Integer.MIN_VALUE;
            ans = Math.max(skip, sell);
        }

        memo[day][transactionsLeft][holding] = ans;
        return ans;
    }
}
```

---

## Time complexity

Each state is computed once:

```text
O(nk)
```

---

## Space complexity

- memo table: `O(nk)`
- recursion stack: `O(n)`

Total auxiliary space:

```text
O(nk)
```

---

## Why this is important

This is the most reusable formulation.

It generalizes naturally to many stock variants:

- cooldown
- fee
- exactly `k`
- unlimited transactions
- more complicated constraints

---

# Approach 3 — Bottom-up DP with 3D state

## Idea

Convert the memoized recursion into iterative DP.

Define:

```text
dp[day][transactionsLeft][holding]
```

where:

- `day` ranges from `0` to `n`
- `transactionsLeft` ranges from `0` to `k`
- `holding` is `0/1`

We fill the table from the end toward the beginning.

---

## Transition

For each state:

### If not holding:

```text
dp[day][t][0] = max(
    dp[day+1][t][0],                // skip
    -prices[day] + dp[day+1][t][1]  // buy
)
```

### If holding:

```text
dp[day][t][1] = max(
    dp[day+1][t][1],                    // skip
    prices[day] + dp[day+1][t-1][0]     // sell, if t > 0
)
```

---

## Java code

```java
class Solution {
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        int[][][] dp = new int[n + 1][k + 1][2];

        for (int day = n - 1; day >= 0; day--) {
            for (int t = 0; t <= k; t++) {
                dp[day][t][0] = Math.max(
                    dp[day + 1][t][0],
                    -prices[day] + dp[day + 1][t][1]
                );

                int skip = dp[day + 1][t][1];
                int sell = Integer.MIN_VALUE;
                if (t > 0) {
                    sell = prices[day] + dp[day + 1][t - 1][0];
                }

                dp[day][t][1] = Math.max(skip, sell);
            }
        }

        return dp[0][k][0];
    }
}
```

---

## Time complexity

There are `n * (k + 1) * 2` states:

```text
O(nk)
```

---

## Space complexity

The 3D DP table stores all states:

```text
O(nk)
```

---

## Strengths

- Iterative
- Easy to reason about once the state is clear
- Matches memoized recursion exactly

## Weaknesses

- Uses more space than necessary

---

# Approach 4 — Bottom-up DP with O(k) space using hold/cash states

This is the standard optimized DP.

## Idea

For each number of completed transactions, maintain two states:

- `buy[t]` = best profit after the `t`-th buy
- `sell[t]` = best profit after the `t`-th sell

Where:

- `t` ranges from `1` to `k`
- `sell[0] = 0` conceptually means before any transaction, profit is 0

---

## State meaning

For a given transaction number `t`:

- `buy[t]` means:

  > best net profit after the `t`-th buy

- `sell[t]` means:
  > best net profit after the `t`-th sell

Transitions:

```text
buy[t]  = max(buy[t],  sell[t-1] - price)
sell[t] = max(sell[t], buy[t]    + price)
```

---

## Why this works

To perform the `t`-th buy, you must already have completed `t - 1` sells.

So:

```text
buy[t] = sell[t-1] - price
```

To perform the `t`-th sell, you must already have performed the `t`-th buy.

So:

```text
sell[t] = buy[t] + price
```

Take max with “do nothing” in each state.

---

## Java code

```java
class Solution {
    public int maxProfit(int k, int[] prices) {
        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];

        for (int t = 1; t <= k; t++) {
            buy[t] = Integer.MIN_VALUE;
            sell[t] = 0;
        }

        for (int price : prices) {
            for (int t = 1; t <= k; t++) {
                buy[t] = Math.max(buy[t], sell[t - 1] - price);
                sell[t] = Math.max(sell[t], buy[t] + price);
            }
        }

        return sell[k];
    }
}
```

---

## Time complexity

For each day, iterate through all transaction counts:

```text
O(nk)
```

---

## Space complexity

Two arrays of size `k + 1`:

```text
O(k)
```

---

## Why this is the general optimal DP for bounded `k`

It avoids the `O(nk)` table while preserving the same recurrence.

This is typically the best general-purpose answer for Stock IV when `k` is not huge.

---

# Approach 5 — Special-case optimization when `k >= n/2`

This is crucial.

## Key observation

A transaction needs at least two days:

- one buy day
- one sell day later

So in `n` days, the maximum number of non-overlapping transactions possible is:

```text
floor(n / 2)
```

Therefore, if:

```text
k >= n / 2
```

then the constraint “at most `k` transactions” is effectively not restrictive.

That means the problem becomes exactly **Stock II** (unlimited transactions).

And the optimal solution becomes:

```text
sum of all positive adjacent differences
```

---

## Why this optimization matters

Without it, the `O(nk)` DP can be unnecessarily expensive when `k` is large.

With the optimization:

- large-`k` cases become `O(n)`
- bounded-`k` cases stay `O(nk)`

This is the standard production-quality solution.

---

## Java code

```java
class Solution {
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;

        if (k >= n / 2) {
            int profit = 0;
            for (int i = 1; i < n; i++) {
                if (prices[i] > prices[i - 1]) {
                    profit += prices[i] - prices[i - 1];
                }
            }
            return profit;
        }

        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];

        for (int t = 1; t <= k; t++) {
            buy[t] = Integer.MIN_VALUE;
            sell[t] = 0;
        }

        for (int price : prices) {
            for (int t = 1; t <= k; t++) {
                buy[t] = Math.max(buy[t], sell[t - 1] - price);
                sell[t] = Math.max(sell[t], buy[t] + price);
            }
        }

        return sell[k];
    }
}
```

---

## Time complexity

Two cases:

### If `k >= n/2`

Greedy Stock II:

```text
O(n)
```

### Otherwise

DP:

```text
O(nk)
```

So overall:

```text
O(n * min(k, n))
```

More precisely, with the optimization:

```text
O(nk)` when k < n/2, otherwise O(n)
```

---

## Space complexity

```text
O(k)
```

---

## This is the best standard answer

In interviews and practice, this is usually the most accepted final solution:

- correct
- efficient
- scalable
- handles the important large-`k` edge case

---

# Approach 6 — Local/global DP formulation

This is a classic but less intuitive formulation.

## Idea

Define:

- `local[i][t]` = max profit with exactly `t` transactions, and the last transaction **must end on day i**
- `global[i][t]` = max profit with at most `t` transactions up to day `i`

This can solve the problem using price differences.

---

## Transition intuition

Let:

```text
diff = prices[i] - prices[i - 1]
```

Then:

```text
local[i][t] = max(
    global[i - 1][t - 1] + max(diff, 0),
    local[i - 1][t] + diff
)

global[i][t] = max(global[i - 1][t], local[i][t])
```

This formulation is elegant mathematically, but harder to derive under interview pressure.

---

## Java code

```java
class Solution {
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (n == 0) return 0;

        if (k >= n / 2) {
            int profit = 0;
            for (int i = 1; i < n; i++) {
                if (prices[i] > prices[i - 1]) {
                    profit += prices[i] - prices[i - 1];
                }
            }
            return profit;
        }

        int[][] local = new int[n][k + 1];
        int[][] global = new int[n][k + 1];

        for (int i = 1; i < n; i++) {
            int diff = prices[i] - prices[i - 1];

            for (int t = 1; t <= k; t++) {
                local[i][t] = Math.max(
                    global[i - 1][t - 1] + Math.max(diff, 0),
                    local[i - 1][t] + diff
                );

                global[i][t] = Math.max(global[i - 1][t], local[i][t]);
            }
        }

        return global[n - 1][k];
    }
}
```

---

## Time complexity

```text
O(nk)
```

---

## Space complexity

```text
O(nk)
```

---

## When this approach is useful

Mostly for deeper DP study.

It is correct and elegant, but less intuitive than the `buy[t] / sell[t]` state-machine solution.

---

# Detailed dry run of the optimized `buy/sell` DP

Example:

```text
k = 2
prices = [3,2,6,5,0,3]
```

Initialize:

```text
buy[1] = -∞, sell[1] = 0
buy[2] = -∞, sell[2] = 0
```

---

## Day 1, price = 3

For `t = 1`:

```text
buy[1] = max(-∞, sell[0] - 3) = -3
sell[1] = max(0, -3 + 3) = 0
```

For `t = 2`:

```text
buy[2] = max(-∞, sell[1] - 3) = -3
sell[2] = max(0, -3 + 3) = 0
```

---

## Day 2, price = 2

For `t = 1`:

```text
buy[1] = max(-3, 0 - 2) = -2
sell[1] = max(0, -2 + 2) = 0
```

For `t = 2`:

```text
buy[2] = max(-3, 0 - 2) = -2
sell[2] = max(0, -2 + 2) = 0
```

---

## Day 3, price = 6

For `t = 1`:

```text
buy[1] = max(-2, -6) = -2
sell[1] = max(0, -2 + 6) = 4
```

For `t = 2`:

```text
buy[2] = max(-2, 4 - 6) = -2
sell[2] = max(0, -2 + 6) = 4
```

---

## Day 4, price = 5

For `t = 1`:

```text
sell[1] = max(4, -2 + 5) = 4
```

For `t = 2`:

```text
buy[2] = max(-2, 4 - 5) = -1
sell[2] = max(4, -1 + 5) = 4
```

---

## Day 5, price = 0

For `t = 1`:

```text
buy[1] = max(-2, 0 - 0) = 0
sell[1] = max(4, 0 + 0) = 4
```

For `t = 2`:

```text
buy[2] = max(-1, 4 - 0) = 4
sell[2] = max(4, 4 + 0) = 4
```

---

## Day 6, price = 3

For `t = 1`:

```text
sell[1] = max(4, 0 + 3) = 4
```

For `t = 2`:

```text
sell[2] = max(4, 4 + 3) = 7
```

Answer:

```text
7
```

---

## Why `buy[t]` can become positive

This often confuses people.

Example:

```text
buy[2] = 4
```

does **not** mean you bought at a negative price.

It means:

> after finishing one profitable sell, you buy again and still have net profit 4 remaining

These variables represent **profit states**, not raw prices.

---

# Edge cases

## 1. `k = 1`

This reduces exactly to Stock I.

The DP still works and returns the correct one-transaction answer.

---

## 2. `k >= n/2`

This reduces to unlimited transactions, so greedy Stock II applies.

This optimization is essential.

---

## 3. Strictly decreasing prices

Profit should be:

```text
0
```

The DP handles this naturally because all sell states stay at 0.

---

## 4. Short array

If `prices.length < 2`, answer is `0`.

The optimized solution naturally handles this, though you can also add an early return.

---

# Common mistakes

## Mistake 1 — Forgetting the `k >= n/2` optimization

Without this, the DP is still correct, but unnecessarily slower for large `k`.

This is one of the most common missed improvements.

---

## Mistake 2 — Updating transaction states inconsistently

Be clear whether a transaction is counted when you:

- buy, or
- sell

Both conventions work, but the formulas differ.

This document uses the convention:

> a transaction is completed on **sell**

---

## Mistake 3 — Treating `buy[t]` and `sell[t]` as literal prices

They are not prices.

They are the **best profits after a certain stage**.

That is why `buy[t]` may become positive.

---

## Mistake 4 — Wrong loop order or wrong formula in `buy/sell` DP

The core formulas are:

```java
buy[t] = Math.max(buy[t], sell[t - 1] - price);
sell[t] = Math.max(sell[t], buy[t] + price);
```

This relies on a consistent interpretation of states.

If you change the meaning of `t`, the formulas must change too.

---

## Mistake 5 — Reusing Stock II greedy logic when `k` is small

Greedy positive-difference works only when transactions are effectively unlimited.

For bounded `k`, you need DP unless `k >= n/2`.

---

# Comparing all approaches

| Approach                     | Idea                                                  |              Time |   Space | Notes                        |
| ---------------------------- | ----------------------------------------------------- | ----------------: | ------: | ---------------------------- |
| Brute force recursion        | Try buy/sell/skip choices                             |          `O(2^n)` |  `O(n)` | Good only for deriving state |
| Memoized recursion           | Cache `(day, k, holding)`                             |           `O(nk)` | `O(nk)` | Most reusable formulation    |
| Bottom-up 3D DP              | Iterative version of memoized recursion               |           `O(nk)` | `O(nk)` | Clear but bulky              |
| `buy/sell` state arrays      | Track best profit after each buy/sell                 |           `O(nk)` |  `O(k)` | Best general bounded-`k` DP  |
| `k >= n/2` optimization + DP | Switch to unlimited-transactions greedy when possible | `O(n)` or `O(nk)` |  `O(k)` | Best standard answer         |
| Local/global DP              | Difference-based DP                                   |           `O(nk)` | `O(nk)` | Elegant but less intuitive   |

---

# Which solution should you give in interviews?

A strong answer path is:

1. Describe the recursive state `(day, transactionsLeft, holding)`
2. Explain memoization gives `O(nk)`
3. Then optimize to `buy[t] / sell[t]` arrays
4. Finally mention the critical shortcut:
   if `k >= n/2`, it becomes Stock II

That demonstrates both depth and practicality.

---

# Recommended final interview solution

```java
class Solution {
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (n < 2 || k == 0) {
            return 0;
        }

        if (k >= n / 2) {
            int profit = 0;
            for (int i = 1; i < n; i++) {
                if (prices[i] > prices[i - 1]) {
                    profit += prices[i] - prices[i - 1];
                }
            }
            return profit;
        }

        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];

        for (int t = 1; t <= k; t++) {
            buy[t] = Integer.MIN_VALUE;
            sell[t] = 0;
        }

        for (int price : prices) {
            for (int t = 1; t <= k; t++) {
                buy[t] = Math.max(buy[t], sell[t - 1] - price);
                sell[t] = Math.max(sell[t], buy[t] + price);
            }
        }

        return sell[k];
    }
}
```

### Time complexity

- `O(n)` when `k >= n/2`
- otherwise `O(nk)`

### Space complexity

```text
O(k)
```

---

# Compact interview explanation

You can explain it like this:

> Let `buy[t]` be the best profit after the `t`-th buy, and `sell[t]` be the best profit after the `t`-th sell.
> For each price:
>
> - `buy[t] = max(buy[t], sell[t-1] - price)`
> - `sell[t] = max(sell[t], buy[t] + price)`
>
> This gives an `O(nk)` DP with `O(k)` extra space.
> Also, if `k >= n/2`, the transaction limit is effectively irrelevant, so the problem reduces to unlimited transactions and we can just sum all positive adjacent differences in `O(n)`.

That is usually a very strong answer.

---

# Final takeaway

This problem is the general stock-DP template.

The most important things to remember are:

1. bounded `k` → use DP
2. general optimal DP:
   ```text
   buy[t], sell[t]
   ```
3. if `k >= n/2`, reduce to unlimited transactions

That trio is the real heart of Stock IV.

---

# Practice extension

The next useful follow-ups are:

- derive Stock III as the `k = 2` special case
- add transaction fee
- add cooldown
- compare “transactions remaining” vs “transactions completed” state definitions

That builds a very strong mental model for stock DP problems.
