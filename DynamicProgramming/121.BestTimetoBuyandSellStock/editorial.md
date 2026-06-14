# 121. Best Time to Buy and Sell Stock — Exhaustive Approach Summary (Java)

## Problem restatement

You are given an array `prices` where:

- `prices[i]` = stock price on day `i`

You may perform **exactly one buy and one sell**, with the condition that:

- you must **buy before you sell**

Your goal is to return the **maximum possible profit**.

If no profitable transaction exists, return `0`.

---

## Core observation

If you sell on day `i`, then the best buy day must be the **minimum price seen before day `i`**.

So for every day, the question becomes:

> If I sell today, what is the cheapest earlier price I could have bought at?

That gives the fundamental formula:

```text
profit if sold today = prices[i] - minPriceSoFar
```

The answer is the maximum of this over all days.

This single observation leads to the optimal solution.

---

# Approach 1 — Brute force: try every buy/sell pair

## Idea

Check all possible pairs:

- buy on day `i`
- sell on day `j` where `j > i`

Compute every profit and track the maximum.

This is the most direct interpretation of the problem statement.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int maxProfit = 0;

        for (int buy = 0; buy < n; buy++) {
            for (int sell = buy + 1; sell < n; sell++) {
                maxProfit = Math.max(maxProfit, prices[sell] - prices[buy]);
            }
        }

        return maxProfit;
    }
}
```

---

## Time complexity

There are about:

```text
n * (n - 1) / 2
```

pairs, so time complexity is:

```text
O(n^2)
```

---

## Space complexity

```text
O(1)
```

---

## Strengths

- Very easy to understand
- Good starting point for deriving better solutions

## Weaknesses

- Too slow for `n = 10^5`
- Repeats unnecessary work

---

# Approach 2 — Brute force with explicit “minimum earlier price” per sell day

## Idea

Instead of checking every pair, for each sell day `i`, scan all earlier days and find the cheapest buy day.

This is still quadratic, but it reframes the problem in the direction of the optimal solution.

For each day `i`:

- find `min(prices[0...i-1])`
- compute `prices[i] - minEarlier`

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int maxProfit = 0;

        for (int sell = 1; sell < n; sell++) {
            int minPrice = prices[0];

            for (int buy = 1; buy < sell; buy++) {
                minPrice = Math.min(minPrice, prices[buy]);
            }

            maxProfit = Math.max(maxProfit, prices[sell] - minPrice);
        }

        return maxProfit;
    }
}
```

---

## Time complexity

For each day, you may scan all previous days:

```text
O(n^2)
```

---

## Space complexity

```text
O(1)
```

---

## Why this approach matters

It highlights the key transformation:

> The only thing that matters for selling today is the minimum earlier price.

Once you see that, you no longer need to rescan all earlier days every time.

---

# Approach 3 — Prefix minimum array

## Idea

Precompute for every day `i`:

```text
minPrefix[i] = minimum stock price from day 0 to day i
```

Then if you sell on day `i`, the best earlier buy price is:

```text
minPrefix[i - 1]
```

or if you define the prefix more conveniently while scanning, you can simply use the minimum seen so far before considering today.

This approach turns the repeated “find minimum earlier price” query into O(1).

---

## Construction

### Step 1: Build prefix minimums

```text
minPrefix[0] = prices[0]
minPrefix[i] = min(minPrefix[i-1], prices[i])
```

### Step 2: Evaluate each sell day

```text
profit = prices[i] - minPrefix[i-1]
```

Track the maximum.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int[] minPrefix = new int[n];
        minPrefix[0] = prices[0];

        for (int i = 1; i < n; i++) {
            minPrefix[i] = Math.min(minPrefix[i - 1], prices[i]);
        }

        int maxProfit = 0;

        for (int sell = 1; sell < n; sell++) {
            int buyPrice = minPrefix[sell - 1];
            maxProfit = Math.max(maxProfit, prices[sell] - buyPrice);
        }

        return maxProfit;
    }
}
```

---

## Time complexity

- Build prefix minima: `O(n)`
- Scan sell days: `O(n)`

Total:

```text
O(n)
```

---

## Space complexity

Prefix array stores `n` values:

```text
O(n)
```

---

## Strengths

- Efficient
- Very clear logically
- Good stepping stone to the optimal `O(1)`-space solution

## Weaknesses

- Uses extra space that is not actually necessary

---

# Approach 4 — One-pass greedy / running minimum (optimal)

This is the standard best solution.

## Idea

While scanning from left to right, maintain:

- `minPriceSoFar` = cheapest price seen up to the current day
- `maxProfit` = best profit found so far

For each price:

1. Treat it as a potential sell day
2. Profit if sold today is:

```text
prices[i] - minPriceSoFar
```

3. Update `maxProfit`
4. Then update `minPriceSoFar`

Actually, in implementation, we usually update `minPriceSoFar` first or compare carefully. Either way works if done correctly.

---

## Why this is correct

At day `i`, any valid transaction that sells on day `i` must buy on one of the earlier days.

The best such earlier buy day is simply the smallest price seen so far.

So:

```text
best profit ending at i = prices[i] - minPriceSoFar
```

Then the global answer is the maximum over all `i`.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int minPriceSoFar = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            minPriceSoFar = Math.min(minPriceSoFar, price);
            maxProfit = Math.max(maxProfit, price - minPriceSoFar);
        }

        return maxProfit;
    }
}
```

---

## Time complexity

Single pass through the array:

```text
O(n)
```

---

## Space complexity

Only a few variables are used:

```text
O(1)
```

---

## Why this is often called “greedy”

Because at every point we greedily keep the best possible buy price seen so far.

But more precisely, this is really:

- a prefix minimum optimization
- or a one-pass dynamic tracking solution

It is not greedy in the fragile “locally optimal without proof” sense. It works because the problem structure mathematically guarantees it.

---

# Approach 5 — Kadane’s algorithm transformation

This is an elegant alternative viewpoint.

## Idea

Transform the prices array into a **difference array**:

```text
diff[i] = prices[i] - prices[i - 1]
```

Then any buy/sell profit from day `l` to day `r` is:

```text
prices[r] - prices[l]
```

which equals the sum of consecutive differences:

```text
(prices[l+1] - prices[l]) +
(prices[l+2] - prices[l+1]) +
...
(prices[r] - prices[r-1])
```

So the problem becomes:

> Find the maximum sum subarray in the difference array.

That is exactly Kadane’s algorithm.

---

## Example

For:

```text
prices = [7,1,5,3,6,4]
```

Difference array:

```text
[-6, 4, -2, 3, -2]
```

Best subarray sum is:

```text
4 + (-2) + 3 = 5
```

which matches the correct answer.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int maxEndingHere = 0;
        int maxSoFar = 0;

        for (int i = 1; i < prices.length; i++) {
            int diff = prices[i] - prices[i - 1];
            maxEndingHere = Math.max(0, maxEndingHere + diff);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
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

## Why this works

A single buy and sell corresponds to one contiguous profit interval in the difference array.

So maximizing profit is equivalent to maximizing the sum of a contiguous subarray of day-to-day price changes.

---

## Is this the best interview answer?

Usually not the first answer.

The running minimum solution is more direct and simpler.

But this approach is great for deep understanding because it shows a strong reduction to a classic problem.

---

# Approach 6 — DP interpretation

This problem can also be expressed in DP language, although the optimal one-pass solution is simpler.

## State idea

Suppose:

```text
dp[i] = maximum profit obtainable by selling on or before day i
```

Then to compute day `i`, you need the best earlier buy price.

That collapses immediately into maintaining:

- best minimum price seen so far
- best profit so far

So the DP recurrence is essentially:

```text
dp[i] = max(dp[i-1], prices[i] - minPriceSoFar)
```

with:

```text
minPriceSoFar = min(minPriceSoFar, prices[i])
```

This is really just another way to justify the one-pass solution.

---

## Java code

```java
class Solution {
    public int maxProfit(int[] prices) {
        int minPriceSoFar = prices[0];
        int[] dp = new int[prices.length];
        dp[0] = 0;

        for (int i = 1; i < prices.length; i++) {
            minPriceSoFar = Math.min(minPriceSoFar, prices[i]);
            dp[i] = Math.max(dp[i - 1], prices[i] - minPriceSoFar);
        }

        return dp[prices.length - 1];
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

DP array:

```text
O(n)
```

---

## Why this is mostly educational

The DP array is unnecessary because only the previous answer matters.

So this naturally compresses to the one-pass optimal solution.

Still, this formulation helps if you are learning how greedy-looking problems often collapse from DP.

---

# Detailed dry runs

## Example 1

```text
prices = [7,1,5,3,6,4]
```

### Running minimum approach

Start:

- `minPriceSoFar = +∞`
- `maxProfit = 0`

#### Day 0, price = 7

- `minPriceSoFar = 7`
- profit if sold today = `7 - 7 = 0`
- `maxProfit = 0`

#### Day 1, price = 1

- `minPriceSoFar = 1`
- profit if sold today = `1 - 1 = 0`
- `maxProfit = 0`

#### Day 2, price = 5

- `minPriceSoFar = 1`
- profit if sold today = `5 - 1 = 4`
- `maxProfit = 4`

#### Day 3, price = 3

- `minPriceSoFar = 1`
- profit if sold today = `3 - 1 = 2`
- `maxProfit = 4`

#### Day 4, price = 6

- `minPriceSoFar = 1`
- profit if sold today = `6 - 1 = 5`
- `maxProfit = 5`

#### Day 5, price = 4

- `minPriceSoFar = 1`
- profit if sold today = `4 - 1 = 3`
- `maxProfit = 5`

Answer:

```text
5
```

---

## Example 2

```text
prices = [7,6,4,3,1]
```

Prices keep decreasing.

At every day, the best sell profit is non-positive, so `maxProfit` stays `0`.

Answer:

```text
0
```

---

# Edge cases

## 1. Single day

```text
prices = [5]
```

You cannot buy and then sell later, because there is no later day.

Answer:

```text
0
```

The one-pass solution handles this naturally.

---

## 2. All equal prices

```text
prices = [3,3,3,3]
```

No profit possible.

Answer:

```text
0
```

---

## 3. Strictly increasing prices

```text
prices = [1,2,3,4,5]
```

Best strategy:

- buy at `1`
- sell at `5`

Answer:

```text
4
```

---

## 4. Strictly decreasing prices

```text
prices = [5,4,3,2,1]
```

No profitable transaction.

Answer:

```text
0`
```

---

## 5. Minimum price occurs late

```text
prices = [8,7,6,1,5]
```

Best buy day is not obvious until later.

One-pass tracking catches it correctly:

- minimum becomes `1`
- later sell at `5`
- answer = `4`

---

# Common mistakes

## Mistake 1 — Selling before buying

The problem requires:

- buy first
- sell later

A solution that simply computes:

```text
max(prices) - min(prices)
```

is wrong, because the global minimum might occur after the global maximum.

Example:

```text
[7, 6, 4, 3, 1]
```

`max - min = 6`, but that transaction is illegal.

---

## Mistake 2 — Updating profit before preserving the correct minimum

In a one-pass solution, the ordering is subtle but manageable.

This works:

```java
minPriceSoFar = Math.min(minPriceSoFar, price);
maxProfit = Math.max(maxProfit, price - minPriceSoFar);
```

because if the current price becomes the new minimum, then `price - minPriceSoFar = 0`, which is harmless.

This also works:

```java
maxProfit = Math.max(maxProfit, price - minPriceSoFar);
minPriceSoFar = Math.min(minPriceSoFar, price);
```

as long as `minPriceSoFar` represents only earlier days at that point.

Both are fine when understood correctly.

---

## Mistake 3 — Returning negative profit

If prices always decline, the correct answer is:

```text
0
```

not a negative number.

You are allowed to do nothing.

---

## Mistake 4 — Overcomplicating with multiple transactions logic

This problem allows only **one** buy and **one** sell.

Do not mix it with the harder stock problems where multiple transactions are allowed.

---

# Comparing all approaches

| Approach                       | Idea                                     |     Time |  Space | Notes                       |
| ------------------------------ | ---------------------------------------- | -------: | -----: | --------------------------- |
| Brute force all pairs          | Check every buy/sell pair                | `O(n^2)` | `O(1)` | Simplest but too slow       |
| Recompute min before each sell | For every sell day, scan earlier days    | `O(n^2)` | `O(1)` | Reveals the key structure   |
| Prefix minimum array           | Precompute cheapest price up to each day |   `O(n)` | `O(n)` | Clean stepping stone        |
| One-pass running minimum       | Track min so far and best profit         |   `O(n)` | `O(1)` | Best standard answer        |
| Kadane transformation          | Convert to max subarray on differences   |   `O(n)` | `O(1)` | Elegant alternative view    |
| DP array formulation           | `dp[i] = best profit up to day i`        |   `O(n)` | `O(n)` | Educational but unnecessary |

---

# Which solution should you give in interviews?

The strongest default answer is the **one-pass running minimum** solution.

It is:

- optimal in time
- optimal in space
- easy to explain
- directly derived from the problem constraint

A strong interview flow is:

1. Mention brute force
2. Observe that selling on day `i` only needs the minimum earlier price
3. Collapse that into one running variable
4. Present the `O(n)` / `O(1)` solution

That shows both reasoning and optimization.

---

# Recommended final interview solution

```java
class Solution {
    public int maxProfit(int[] prices) {
        int minPriceSoFar = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            minPriceSoFar = Math.min(minPriceSoFar, price);
            maxProfit = Math.max(maxProfit, price - minPriceSoFar);
        }

        return maxProfit;
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

You can explain it like this:

> If we sell on day `i`, the best possible buy day is simply the minimum price among all earlier days.
> So while scanning the array, we keep track of the minimum price seen so far and compute the profit if we sold today.
> The answer is the maximum such profit over all days.
> This gives `O(n)` time and `O(1)` space.

That is typically the cleanest explanation.

---

# Final takeaway

This problem looks like a stock problem, but algorithmically it is really a **running minimum + best difference** problem.

The key mental transformation is:

> maximize `prices[j] - prices[i]` with `i < j`

That immediately suggests:

- maintain the smallest `prices[i]` seen so far
- evaluate every `prices[j]` against it

That is the whole problem.

---

# Practice extension

After this, the natural next problems are:

- Best Time to Buy and Sell Stock II
- Best Time to Buy and Sell Stock III
- Maximum Subarray
- Maximum Difference Between Increasing Elements

These help distinguish when a simple one-pass invariant is enough and when full DP/state-machine thinking is required.
