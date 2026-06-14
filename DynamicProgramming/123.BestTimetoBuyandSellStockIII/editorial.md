# 123. Best Time to Buy and Sell Stock III — Exhaustive Approach Summary (Java)

## Problem restatement

You are given an array `prices` where:

- `prices[i]` = stock price on day `i`

You may complete **at most two transactions**.

A transaction means:

- buy one stock
- later sell that stock

Rules:

- you cannot hold more than one share at a time
- so you must sell before buying again

Return the **maximum profit**.

---

## What changes compared with Stock I and Stock II?

### Stock I

- only **one** transaction allowed

### Stock II

- **unlimited** transactions allowed

### Stock III

- **at most two** transactions allowed

That makes this problem more structured than Stock II and more complex than Stock I.

The challenge is no longer just finding one best interval or summing all local gains.

Now you must decide how to optimally use **up to two non-overlapping transactions**.

---

## Key intuition

A valid solution can look like:

- no transaction
- one transaction
- two transactions

If there are two transactions, they must happen in order:

```text
buy1 -> sell1 -> buy2 -> sell2
```

So the problem is fundamentally about tracking how profit evolves through these four stages.

That leads to the most elegant optimal solution.

---

# Approach 1 — Brute force over all possible two transactions

## Idea

Try every possible first transaction and every possible second transaction after it.

More concretely:

- choose `buy1`, `sell1`
- choose `buy2`, `sell2` with `buy2 > sell1`
- maximize total profit

Also include the possibility of only one transaction.

This directly models the definition, but it is extremely slow.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int best = 0;

        for (int buy1 = 0; buy1 < n; buy1++) {
            for (int sell1 = buy1 + 1; sell1 < n; sell1++) {
                int profit1 = prices[sell1] - prices[buy1];
                best = Math.max(best, profit1);

                for (int buy2 = sell1 + 1; buy2 < n; buy2++) {
                    for (int sell2 = buy2 + 1; sell2 < n; sell2++) {
                        int profit2 = prices[sell2] - prices[buy2];
                        best = Math.max(best, profit1 + profit2);
                    }
                }
            }
        }

        return Math.max(best, 0);
    }
}
```

---

## Time complexity

There are four nested loops:

```text
O(n^4)
```

This is far too slow.

---

## Space complexity

```text
O(1)
```

---

## Why this approach still matters

It clarifies the structure:

- two transactions
- ordered, non-overlapping
- total profit is the sum of profits of both segments

That structural understanding helps derive better solutions.

---

# Approach 2 — Recursion with state `(day, transactionsLeft, holding)`

## Idea

At each day, your decision depends on:

- current day
- how many transactions you still have available
- whether you are currently holding a stock

This naturally gives a recursive state machine.

---

## State definition

Let:

```text
dfs(day, transactionsLeft, holding)
```

be the maximum profit from this point onward.

Where:

- `day` is current index
- `transactionsLeft` is how many sells you can still complete
- `holding` is `0` or `1`

Important interpretation:

- a transaction is considered completed when you **sell**
- so selling consumes one transaction

---

## Transitions

### If not holding

You may:

- skip → `dfs(day + 1, transactionsLeft, 0)`
- buy → `-prices[day] + dfs(day + 1, transactionsLeft, 1)`

### If holding

You may:

- skip → `dfs(day + 1, transactionsLeft, 1)`
- sell → `prices[day] + dfs(day + 1, transactionsLeft - 1, 0)`

---

## Base cases

If:

- `day == n`, return `0`
- `transactionsLeft == 0` and `holding == 0`, return `0`

If `transactionsLeft == 0` and still holding, you cannot profitably complete more sales, so the recursion structure will naturally avoid invalid further buys/sells if handled carefully.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        return dfs(prices, 0, 2, 0);
    }

    private int dfs(int[] prices, int day, int transactionsLeft, int holding) {
        if (day == prices.length) {
            return 0;
        }

        if (transactionsLeft == 0 && holding == 0) {
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

Without memoization, this is exponential:

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

- Very natural general stock-DP state machine
- Scales conceptually to many stock variants

## Weaknesses

- Too slow without memoization

---

# Approach 3 — Top-down DP with memoization

## Idea

The recursive states repeat.

There are only:

- `n` choices for `day`
- `3` choices for `transactionsLeft` (`0,1,2`)
- `2` choices for `holding`

So total states are only:

```text
O(n * 3 * 2) = O(n)
```

Memoization makes the solution efficient.

---

## Java code

```java
class Solution {
    private Integer[][][] memo;

    public int maxProfit(int[] prices) {
        int n = prices.length;
        memo = new Integer[n][3][2];
        return dfs(prices, 0, 2, 0);
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
O(n)
```

More precisely:

```text
O(n * 2 * 3) = O(n)
```

---

## Space complexity

- memo table: `O(n)`
- recursion stack: `O(n)`

Total:

```text
O(n)
```

---

## Why this approach is valuable

This is the most reusable conceptual solution.

Once you understand this state machine, you can adapt it to:

- cooldown
- fee
- at most `k` transactions
- transaction limits with other constraints

---

# Approach 4 — Bottom-up DP with transaction count and holding state

## Idea

Convert the memoized state machine into iterative DP.

Define:

```text
dp[day][transactionsLeft][holding]
```

or more conveniently, track states forward by number of completed transactions and holding status.

But for clarity, we can instead model the four important phases directly.

Still, before that, it helps to understand the general DP table approach.

---

## A clearer forward DP formulation

Let:

```text
dp[i][k][0] = max profit after day i, with at most k transactions completed, not holding stock
dp[i][k][1] = max profit after day i, with at most k transactions completed, holding stock
```

Then:

```text
dp[i][k][0] = max(dp[i-1][k][0], dp[i-1][k][1] + prices[i])
dp[i][k][1] = max(dp[i-1][k][1], dp[i-1][k-1][0] - prices[i])
```

But indexing can get subtle depending on whether `k` means transactions used or remaining.

Because this often causes confusion, the phase-based approach below is usually cleaner for exactly two transactions.

---

# Approach 5 — Two-pass split array method

This is one of the most intuitive `O(n)` solutions.

## Idea

Split the problem into two independent parts:

- best profit from **one transaction** in prefix `[0..i]`
- best profit from **one transaction** in suffix `[i..n-1]`

Then combine them.

### Step 1

For each day `i`, compute:

```text
leftProfit[i] = maximum profit from one transaction using days 0..i
```

### Step 2

For each day `i`, compute:

```text
rightProfit[i] = maximum profit from one transaction using days i..n-1
```

### Step 3

Try every split point:

```text
answer = max(leftProfit[i] + rightProfit[i])
```

This covers:

- both transactions separated around `i`
- one transaction only, because the other side can contribute `0`

---

## How to compute `leftProfit`

Same as Stock I:

- track minimum price so far
- compute best one-transaction profit up to each index

---

## How to compute `rightProfit`

Reverse logic:

- track maximum selling price seen from the right
- compute best one-transaction profit from each index to end

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int[] leftProfit = new int[n];
        int[] rightProfit = new int[n];

        int minPrice = prices[0];
        for (int i = 1; i < n; i++) {
            minPrice = Math.min(minPrice, prices[i]);
            leftProfit[i] = Math.max(leftProfit[i - 1], prices[i] - minPrice);
        }

        int maxPrice = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            maxPrice = Math.max(maxPrice, prices[i]);
            rightProfit[i] = Math.max(rightProfit[i + 1], maxPrice - prices[i]);
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            ans = Math.max(ans, leftProfit[i] + rightProfit[i]);
        }

        return ans;
    }
}
```

---

## Time complexity

- left pass: `O(n)`
- right pass: `O(n)`
- combine pass: `O(n)`

Total:

```text
O(n)`
```

---

## Space complexity

Two arrays of length `n`:

```text
O(n)
```

---

## Why this approach is excellent

It is very intuitive.

It reduces Stock III to two Stock I computations plus a merge.

This is often one of the easiest correct explanations in interviews.

---

# Approach 6 — State-machine DP with four variables (optimal and elegant)

This is the most compact and popular optimal solution.

## Key idea

At most two transactions means the process can go through these four states:

1. `firstBuy` → after first buy
2. `firstSell` → after first sell
3. `secondBuy` → after second buy
4. `secondSell` → after second sell

We track the best profit possible in each state as we scan prices.

---

## State meanings

Let:

- `firstBuy` = best profit after buying first stock
- `firstSell` = best profit after selling first stock
- `secondBuy` = best profit after buying second stock
- `secondSell` = best profit after selling second stock

These are profits, so buying subtracts price and selling adds price.

---

## Transitions

For each price:

```text
firstBuy   = max(firstBuy, -price)
firstSell  = max(firstSell, firstBuy + price)
secondBuy  = max(secondBuy, firstSell - price)
secondSell = max(secondSell, secondBuy + price)
```

Interpretation:

### `firstBuy`

Either:

- keep earlier first buy
- buy today as first transaction

### `firstSell`

Either:

- keep earlier first sell
- sell today the stock from `firstBuy`

### `secondBuy`

Either:

- keep earlier second buy
- buy today using profit from first completed sell

### `secondSell`

Either:

- keep earlier second sell
- sell today the stock from `secondBuy`

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int firstBuy = Integer.MIN_VALUE;
        int firstSell = 0;
        int secondBuy = Integer.MIN_VALUE;
        int secondSell = 0;

        for (int price : prices) {
            firstBuy = Math.max(firstBuy, -price);
            firstSell = Math.max(firstSell, firstBuy + price);
            secondBuy = Math.max(secondBuy, firstSell - price);
            secondSell = Math.max(secondSell, secondBuy + price);
        }

        return secondSell;
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

Only four variables:

```text
O(1)
```

---

## Why this is the best practical answer

It is:

- optimal in time
- optimal in extra space
- elegant
- directly expresses the four transaction phases

For this exact problem, this is often the best final solution.

---

# Approach 7 — General `k` transactions DP specialized to `k = 2`

This is useful for understanding the broader pattern.

## Idea

For at most `k` transactions, a classic DP is:

```text
buy[t]  = max profit after the t-th buy
sell[t] = max profit after the t-th sell
```

For each price:

```text
buy[t]  = max(buy[t], sell[t-1] - price)
sell[t] = max(sell[t], buy[t] + price)
```

For `k = 2`, this becomes exactly the four-variable solution.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int k = 2;
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

For general `k`:

```text
O(nk)
```

For `k = 2`:

```text
O(n)
```

---

## Space complexity

For general `k`:

```text
O(k)
```

For `k = 2`:

```text
O(1)
```

---

## Why this matters

This is the bridge from Stock III to Stock IV.

It shows that the four-variable trick is not magical. It is just the `k = 2` case of a general DP template.

---

# Detailed dry run of the four-variable solution

Example:

```text
prices = [3,3,5,0,0,3,1,4]
```

Initialize:

```text
firstBuy = -∞
firstSell = 0
secondBuy = -∞
secondSell = 0
```

We update day by day.

---

## Day 1, price = 3

```text
firstBuy = max(-∞, -3) = -3
firstSell = max(0, -3 + 3) = 0
secondBuy = max(-∞, 0 - 3) = -3
secondSell = max(0, -3 + 3) = 0
```

---

## Day 2, price = 3

No real improvement:

```text
firstBuy = -3
firstSell = 0
secondBuy = -3
secondSell = 0
```

---

## Day 3, price = 5

```text
firstBuy = max(-3, -5) = -3
firstSell = max(0, -3 + 5) = 2
secondBuy = max(-3, 2 - 5) = -3
secondSell = max(0, -3 + 5) = 2
```

---

## Day 4, price = 0

```text
firstBuy = max(-3, 0) = 0
firstSell = max(2, 0 + 0) = 2
secondBuy = max(-3, 2 - 0) = 2
secondSell = max(2, 2 + 0) = 2
```

This can look strange at first.

Why can `firstBuy = 0`?

Because `firstBuy` stores **profit after first buy**, not the literal purchase price.
Buying at price `0` means profit state becomes `0`.

Why can `secondBuy = 2`?

Because after making `2` profit from first sell, buying second stock at `0` still leaves net profit `2`.

---

## Day 5, price = 0

No change.

---

## Day 6, price = 3

```text
firstSell = max(2, 0 + 3) = 3
secondSell = max(2, 2 + 3) = 5
```

---

## Day 7, price = 1

```text
secondBuy = max(2, 3 - 1) = 2
```

No improvement beyond `2`.

---

## Day 8, price = 4

```text
firstSell = max(3, 0 + 4) = 4
secondSell = max(5, 2 + 4) = 6
```

Answer:

```text
6
```

---

# Another dry run: increasing prices

```text
prices = [1,2,3,4,5]
```

Best answer is:

```text
4
```

Even though two transactions are allowed, one long transaction is enough.

The four-variable DP naturally handles this and returns `4`.

---

# Edge cases

## 1. Strictly decreasing prices

```text
[7,6,4,3,1]
```

No profitable transaction exists.

Answer:

```text
0
```

---

## 2. Only one transaction is beneficial

Example:

```text
[1, 5, 2, 2]
```

Best is just one transaction:

```text
buy at 1, sell at 5 => 4
```

The algorithm must not force two transactions.

It does not. “At most two” includes one or zero.

---

## 3. Very short arrays

If `prices.length < 2`, answer is immediately `0`.

The provided DP formulations already handle that naturally if coded carefully, but it is still worth understanding.

---

# Common mistakes

## Mistake 1 — Reusing Stock II greedy logic

For unlimited transactions, summing all positive differences works.

For at most two transactions, that greedy rule is **wrong**.

Example:

```text
[1,2,4,2,5,7,2,4,9,0]
```

Unlimited-transactions greedy can exceed what two transactions allow.

So transaction limit matters.

---

## Mistake 2 — Overlapping transactions

You cannot buy second stock before selling the first.

Any solution that independently picks two best intervals without enforcing order/non-overlap is invalid.

---

## Mistake 3 — Misinterpreting the four variables

These variables are not prices.

They are **best profits after certain stages**.

That is why values like `secondBuy = 2` are valid.

It means:

> after finishing one sell and then buying the second stock, the best net profit so far is 2

---

## Mistake 4 — Wrong update order in the four-variable solution

The order matters:

```java
firstBuy
firstSell
secondBuy
secondSell
```

because each state depends on the previous stage updated for the current price.

Using a random order can break correctness.

---

## Mistake 5 — Confusion about “transaction count”

Some formulations count:

- buy as consuming a transaction
- others count sell as completing a transaction

Either is fine, but you must be consistent.

The recursion in this document treats **sell** as completing a transaction.

---

# Comparing all approaches

| Approach                    | Idea                                       |     Time |  Space | Notes                             |
| --------------------------- | ------------------------------------------ | -------: | -----: | --------------------------------- |
| Brute force two intervals   | Try all buy/sell combinations              | `O(n^4)` | `O(1)` | Too slow, but clarifies structure |
| Recursion with states       | `(day, transactionsLeft, holding)`         | `O(2^n)` | `O(n)` | Good conceptual start             |
| Memoized recursion          | Cache states                               |   `O(n)` | `O(n)` | Very reusable stock DP            |
| Two-pass split arrays       | Best one-transaction profit left and right |   `O(n)` | `O(n)` | Very intuitive                    |
| Four-variable state machine | `buy1, sell1, buy2, sell2`                 |   `O(n)` | `O(1)` | Best practical solution           |
| General `k` DP              | `buy[t], sell[t]`                          |  `O(nk)` | `O(k)` | Bridge to Stock IV                |

---

# Which solution should you give in interviews?

A strong explanation path is:

1. Describe recursive state `(day, transactionsLeft, holding)`
2. Mention memoization makes it linear
3. Then present the optimized specialized form for `k = 2`

If the interviewer wants the cleanest final implementation, give the **four-variable state-machine DP**.

If they want the most intuitive `O(n)` explanation, the **two-pass split array** method is also excellent.

---

# Recommended final interview solution

```java
class Solution {
    public int maxProfit(int[] prices) {
        int firstBuy = Integer.MIN_VALUE;
        int firstSell = 0;
        int secondBuy = Integer.MIN_VALUE;
        int secondSell = 0;

        for (int price : prices) {
            firstBuy = Math.max(firstBuy, -price);
            firstSell = Math.max(firstSell, firstBuy + price);
            secondBuy = Math.max(secondBuy, firstSell - price);
            secondSell = Math.max(secondSell, secondBuy + price);
        }

        return secondSell;
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

# Recommended intuitive alternative solution

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int[] leftProfit = new int[n];
        int[] rightProfit = new int[n];

        int minPrice = prices[0];
        for (int i = 1; i < n; i++) {
            minPrice = Math.min(minPrice, prices[i]);
            leftProfit[i] = Math.max(leftProfit[i - 1], prices[i] - minPrice);
        }

        int maxPrice = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            maxPrice = Math.max(maxPrice, prices[i]);
            rightProfit[i] = Math.max(rightProfit[i + 1], maxPrice - prices[i]);
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            ans = Math.max(ans, leftProfit[i] + rightProfit[i]);
        }

        return ans;
    }
}
```

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(n)
```

---

# Compact interview explanation

You can explain the four-variable solution like this:

> At most two transactions means the process has four stages: first buy, first sell, second buy, second sell.
> For each price, update the best profit achievable after each stage:
>
> - first buy: `max(firstBuy, -price)`
> - first sell: `max(firstSell, firstBuy + price)`
> - second buy: `max(secondBuy, firstSell - price)`
> - second sell: `max(secondSell, secondBuy + price)`
>
> The final answer is the best profit after the second sell.

That explanation is compact and strong.

---

# Final takeaway

This problem is a classic example of **state-machine DP**.

The central insight is:

> with at most two transactions, there are only four meaningful profit states to track

Once that clicks, the optimal `O(n)` / `O(1)` solution becomes very natural.

---

# Practice extension

The natural next problems are:

- Best Time to Buy and Sell Stock IV
- Best Time to Buy and Sell Stock with Cooldown
- Best Time to Buy and Sell Stock with Transaction Fee

Those help generalize the stock state-machine pattern beyond the fixed 2-transaction case.
