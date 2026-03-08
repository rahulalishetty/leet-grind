# Maximum Total Tastiness with Budget and Coupons

## Problem Restatement

You are given two arrays:

- `price[i]`: the cost of the `i`th fruit
- `tastiness[i]`: the tastiness gained if you buy the `i`th fruit

You are also given:

- `maxAmount`: the maximum total money you can spend
- `maxCoupons`: the maximum number of coupons you may use

For each fruit, you may choose exactly one of these:

1. **Skip it**
2. **Buy it normally** for `price[i]`
3. **Buy it with a coupon** for `floor(price[i] / 2)`, consuming one coupon

Each fruit can be purchased **at most once**, and a coupon can be applied to a fruit **at most once**.

The goal is to maximize the total tastiness.

---

## Core Pattern

This is a **0/1 knapsack with two resource constraints**:

- money
- coupons

That is the key modeling step.

In ordinary knapsack, there is one constraint: capacity.
Here, there are two capacities:

- amount spent must be at most `maxAmount`
- coupons used must be at most `maxCoupons`

So the DP must track both.

---

## First Intuition

For each fruit, you are deciding among three actions:

- do not take it
- take it normally
- take it with a coupon

This strongly suggests **dynamic programming over items**.

A brute-force approach would try all subsets and coupon placements, but that becomes exponential because:

- each fruit has 3 choices
- total possibilities are roughly `3^n`

That is far too expensive.

So we need to compress the decision process into a DP.

---

## DP State

Let:

`dp[a][c] = maximum tastiness achievable using at most amount a and at most c coupons after processing some prefix of fruits`

When we use rolling / in-place item processing, `dp[a][c]` means:

- among fruits processed so far,
- with budget limit `a`,
- and coupon limit `c`,
- what is the best tastiness we can achieve?

This is enough information because future choices only care about how much budget and how many coupons remain.

---

## Why This State Is Correct

A valid partial solution is fully characterized by:

- how much money has been spent so far
- how many coupons have been used so far
- what total tastiness has been achieved

We do not care about the exact set of fruits except insofar as it affects those resource usages and the tastiness score.
That is exactly why DP works here.

---

## Transition Logic

Suppose we are processing fruit `i`.

Define:

- `normalCost = price[i]`
- `couponCost = price[i] / 2`
- `value = tastiness[i]`

From each previous state, we have three possibilities.

### 1. Skip the fruit

Then nothing changes.

This is automatically handled because the current `dp[a][c]` value is already retained.

---

### 2. Buy normally

If we have enough budget:

- current amount capacity `a` must satisfy `a >= normalCost`

Then we can transition from:

- `dp[a - normalCost][c]`

to

- `dp[a][c]`

by adding this fruit’s tastiness:

`dp[a][c] = max(dp[a][c], dp[a - normalCost][c] + value)`

---

### 3. Buy with a coupon

If we have enough:

- budget: `a >= couponCost`
- coupons: `c >= 1`

Then:

`dp[a][c] = max(dp[a][c], dp[a - couponCost][c - 1] + value)`

This means we spend half the price and consume one coupon.

---

## Important Subtlety: Why Reverse Iteration?

This is a **0/1** problem, not an unbounded knapsack.

Each fruit may be used at most once.

If we iterate `amount` and `coupons` forward, then while processing one fruit we may accidentally reuse an already updated state from the same fruit, effectively taking that fruit multiple times.

That would be wrong.

So we must iterate in reverse:

- `amount` from `maxAmount` down to `0`
- `coupons` from `maxCoupons` down to `0`

This guarantees that each transition reads only states from the previous item-layer logically, even though we are storing them in the same table.

This is the exact same reason ordinary 0/1 knapsack uses reverse iteration in capacity.

---

## Full DP Algorithm

1. Create a 2D DP table:
   - rows = `0..maxAmount`
   - columns = `0..maxCoupons`
2. Initialize all values to `0`
   - because choosing nothing gives tastiness `0`
3. For each fruit:
   - compute normal price
   - compute coupon price
   - update the DP table in reverse
4. The answer is the maximum value present in the table, or equivalently `dp[a][c]` over all valid `a` and `c`

Because the table represents “at most” resources, scanning all states at the end is safe and explicit.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maxTastiness(int[] price, int[] tastiness, int maxAmount, int maxCoupons) {
        int n = price.length;
        int[][] dp = new int[maxAmount + 1][maxCoupons + 1];

        for (int i = 0; i < n; i++) {
            int normal = price[i];
            int half = price[i] / 2;
            int taste = tastiness[i];

            for (int amount = maxAmount; amount >= 0; amount--) {
                for (int coupons = maxCoupons; coupons >= 0; coupons--) {
                    // Buy normally
                    if (amount >= normal) {
                        dp[amount][coupons] = Math.max(
                            dp[amount][coupons],
                            dp[amount - normal][coupons] + taste
                        );
                    }

                    // Buy with coupon
                    if (coupons > 0 && amount >= half) {
                        dp[amount][coupons] = Math.max(
                            dp[amount][coupons],
                            dp[amount - half][coupons - 1] + taste
                        );
                    }
                }
            }
        }

        int ans = 0;
        for (int amount = 0; amount <= maxAmount; amount++) {
            for (int coupons = 0; coupons <= maxCoupons; coupons++) {
                ans = Math.max(ans, dp[amount][coupons]);
            }
        }
        return ans;
    }
}
```

---

## Line-by-Line Logic Walkthrough

### DP table creation

```java
int[][] dp = new int[maxAmount + 1][maxCoupons + 1];
```

`dp[a][c]` stores the best tastiness using budget up to `a` and coupons up to `c`.

The table is initialized to `0`, meaning:
buying nothing is always allowed and yields tastiness `0`.

---

### Iterate through fruits

```java
for (int i = 0; i < n; i++) {
```

We process fruits one by one so each fruit is handled exactly once.

---

### Precompute the fruit’s two costs

```java
int normal = price[i];
int half = price[i] / 2;
int taste = tastiness[i];
```

These are the only two purchase modes for the current fruit.

---

### Reverse iterate amount and coupons

```java
for (int amount = maxAmount; amount >= 0; amount--) {
    for (int coupons = maxCoupons; coupons >= 0; coupons--) {
```

This is the essential 0/1 knapsack update order.

---

### Normal purchase transition

```java
if (amount >= normal) {
    dp[amount][coupons] = Math.max(
        dp[amount][coupons],
        dp[amount - normal][coupons] + taste
    );
}
```

Interpretation:

- Suppose we want to end in state `(amount, coupons)` after buying this fruit normally.
- Then before taking this fruit, we must have been at `(amount - normal, coupons)`.
- Add this fruit’s tastiness.

---

### Coupon purchase transition

```java
if (coupons > 0 && amount >= half) {
    dp[amount][coupons] = Math.max(
        dp[amount][coupons],
        dp[amount - half][coupons - 1] + taste
    );
}
```

Interpretation:

- To end in `(amount, coupons)` after using a coupon on this fruit,
- previously we must have been at `(amount - half, coupons - 1)`.

So this transition consumes one coupon and spends the discounted cost.

---

### Extract answer

```java
int ans = 0;
for (int amount = 0; amount <= maxAmount; amount++) {
    for (int coupons = 0; coupons <= maxCoupons; coupons++) {
        ans = Math.max(ans, dp[amount][coupons]);
    }
}
return ans;
```

This scans all feasible resource states and returns the best tastiness among them.

---

## Worked Example

Consider:

```text
price      = [4, 6, 3]
tastiness  = [5, 8, 4]
maxAmount  = 7
maxCoupons = 1
```

We examine possible good choices:

- Fruit 0 normally: cost 4, taste 5
- Fruit 0 coupon: cost 2, taste 5
- Fruit 1 normally: cost 6, taste 8
- Fruit 1 coupon: cost 3, taste 8
- Fruit 2 normally: cost 3, taste 4
- Fruit 2 coupon: cost 1, taste 4

### Some candidate combinations

- Fruit 1 normally alone:
  - cost 6, taste 8

- Fruit 0 normally + Fruit 2 normally:
  - cost 4 + 3 = 7
  - taste 5 + 4 = 9

- Fruit 1 with coupon + Fruit 2 normally:
  - cost 3 + 3 = 6
  - taste 8 + 4 = 12

- Fruit 0 with coupon + Fruit 1 normally:
  - cost 2 + 6 = 8, too expensive

Best is:

- fruit 1 with coupon
- fruit 2 normally

Total:

- cost = 6
- coupons used = 1
- tastiness = 12

So the answer is `12`.

The DP finds this systematically.

---

## Why Greedy Fails

A greedy approach might try ideas like:

- always use coupons on the most expensive fruit
- always buy the highest tastiness fruit first
- maximize tastiness/price ratio

All of these can fail because the coupon is a second resource interacting with budget in nontrivial ways.

A fruit with slightly lower price but huge tastiness may become better with a coupon.
Or spending the coupon on one fruit may block a much better combination later.

This interaction is exactly what DP handles and greedy usually misses.

---

## Correctness Argument

We can justify the DP with a standard induction argument.

### Definition

After processing the first `i` fruits, `dp[a][c]` stores the maximum tastiness achievable using:

- total amount at most `a`
- total coupons at most `c`

with each of the first `i` fruits chosen at most once.

---

### Base Case

Before processing any fruit, the best tastiness is `0` for every `(a, c)` by choosing nothing.

So the initialization is correct.

---

### Inductive Step

Assume the DP is correct after processing the first `i` fruits.

Now consider fruit `i + 1`.

Any optimal solution after considering this fruit must fall into exactly one of three categories:

1. it does not include the fruit
2. it includes the fruit bought normally
3. it includes the fruit bought with a coupon

Our transitions compute the best among precisely these three possibilities.

Because we reverse iterate, the fruit is not reused during its own update.

Therefore the updated table is correct after processing fruit `i + 1`.

By induction, the final DP table is correct.

---

## Complexity Analysis

Let:

- `n = price.length`
- `A = maxAmount`
- `C = maxCoupons`

### Time Complexity

For each of the `n` fruits, we iterate over:

- all `A + 1` amount states
- all `C + 1` coupon states

So the total time is:

`O(n * A * C)`

---

### Space Complexity

The DP table has:

`(A + 1) * (C + 1)` states

So space is:

`O(A * C)`

This is already space-optimized compared to a naive 3D DP.

---

## Why a 2D DP Is Enough

A more direct formulation is:

`dp[i][a][c] = best tastiness using first i fruits`

That works, but it costs:

- time: `O(n * A * C)`
- space: `O(n * A * C)`

However, the transitions for fruit `i` depend only on states from fruit `i - 1`.

So we can compress away the item dimension and keep only the 2D table.

That is why the reverse-order update is so important: it simulates the previous layer correctly.

---

## Common Mistakes

### 1. Iterating forward

This turns the problem into accidental unbounded knapsack and may reuse the same fruit multiple times.

### 2. Forgetting `floor(price[i] / 2)`

The coupon cost is integer division, not exact half.

### 3. Treating normal purchase and coupon purchase as independent items

They are two modes of the same fruit, and only one may be chosen.

### 4. Using a greedy strategy

Local decisions do not reliably produce the global optimum.

### 5. Using only budget in the DP state

That ignores coupon usage, which is essential.

---

## Alternative Top-Down View

You can also define a memoized recursion:

`solve(i, remainingAmount, remainingCoupons)`

which returns the maximum tastiness from fruits starting at index `i`.

Transitions:

- skip fruit `i`
- buy normally if affordable
- buy with coupon if affordable and coupons remain

That is conceptually clean, but bottom-up DP is usually more memory efficient and avoids recursion overhead.

---

## Final Takeaway

The clean way to recognize this problem is:

- each fruit is used at most once → **0/1 knapsack**
- there are two bounded resources → **2D capacity DP**
- each item has three choices → skip / normal / coupon

So the correct approach is a reverse-iterated 2D DP:

- state by amount and coupons
- transition by taking current fruit normally or with coupon
- maximize tastiness

---

## Final Complexity Summary

- **Time:** `O(n * maxAmount * maxCoupons)`
- **Space:** `O(maxAmount * maxCoupons)`

This is the standard optimal DP formulation for the given constraints structure.
