# Maximum Total Weight Packed Into Two Bags

## Problem

You are given:

- an integer array `weights`
- two integers `w1` and `w2`

These represent the capacities of two bags:

- Bag 1 can hold total weight at most `w1`
- Bag 2 can hold total weight at most `w2`

Each item:

- has weight `weights[i]`
- may be placed in **at most one** bag
- may also be skipped

We need to return the **maximum total weight** that can be packed into the two bags.

---

# 1. What the problem is really asking

This is a variant of the classic **0/1 knapsack** problem.

In ordinary 0/1 knapsack:

- each item can be taken or skipped
- there is one capacity constraint

Here, for each item, we have **three** choices:

1. do not take it
2. put it into bag 1
3. put it into bag 2

And we must obey **two independent capacity constraints**.

So this is naturally a **two-dimensional 0/1 knapsack**.

---

# 2. Core intuition

For each item of weight `x`, the decision is:

- skip it
- place it in bag 1 if bag 1 has enough remaining capacity
- place it in bag 2 if bag 2 has enough remaining capacity

The total packed weight is simply the sum of the chosen item weights.

Since every item's “value” is equal to its weight, the objective is just:

> maximize the total selected weight without exceeding either bag capacity.

Because there are two capacity limits, the DP state must track both.

---

# 3. DP state definition

Let:

`dp[a][b] = maximum total weight that can be packed using some prefix of items, with bag 1 capacity limited to a and bag 2 capacity limited to b`

Equivalently, you can think of:

- `a` = remaining or available capacity in bag 1
- `b` = remaining or available capacity in bag 2

But in implementation, the most common view is:

- `a` and `b` are the current capacity bounds we are allowed to use

The important part is that `dp[a][b]` stores the best achievable packed weight under those two capacity limits.

---

# 4. Why two dimensions are necessary

With one bag, a 1D DP is enough because only one resource matters: capacity.

With two bags, each item affects one of two different resources:

- if placed in bag 1, it consumes bag 1 capacity
- if placed in bag 2, it consumes bag 2 capacity

So a state with:

- bag 1 remaining = `4`
- bag 2 remaining = `7`

is fundamentally different from a state with:

- bag 1 remaining = `7`
- bag 2 remaining = `4`

Even though the total remaining capacity is the same, the future choices differ.

That is why the DP must track both capacities separately.

---

# 5. Transition idea

Suppose the current item has weight:

`x`

For every state `dp[a][b]`, there are three possibilities.

## Option 1: skip the item

Then the state stays unchanged:

`dp[a][b]`

## Option 2: place the item in bag 1

This is allowed only if:

`a >= x`

Then we can come from state:

`dp[a - x][b]`

and add `x` to the total packed weight.

So candidate value is:

`dp[a - x][b] + x`

## Option 3: place the item in bag 2

This is allowed only if:

`b >= x`

Then we can come from state:

`dp[a][b - x]`

and add `x`.

So candidate value is:

`dp[a][b - x] + x`

Therefore the transition is:

`dp[a][b] = max(dp[a][b], dp[a - x][b] + x, dp[a][b - x] + x)`

when those states exist.

---

# 6. Why this is a 0/1 knapsack, not unbounded knapsack

Each item may be used **at most once**.

So when processing an item:

- we can put it in bag 1
- or put it in bag 2
- or skip it

But we cannot use the same item twice.

That means we must update the DP in a way that avoids reusing the same item in the same iteration.

That is exactly why we use **backward iteration** on both capacities.

---

# 7. Why backward iteration is necessary

Suppose we process an item of weight `x`.

If we iterate `a` and `b` forward, then after updating a state using this item, that newly updated state might get used again later in the same iteration.

That would effectively allow the same item to be counted multiple times.

That is incorrect because this is a 0/1 choice per item.

To prevent this, we iterate both capacities from large to small:

- `a = w1 down to 0`
- `b = w2 down to 0`

This ensures each transition uses only states from the **previous item layer**, not states already updated by the current item.

That preserves the “use each item at most once” rule.

---

# 8. DP initialization

Before processing any items:

- the best total packed weight is `0`
- regardless of the capacities

So initialize:

`dp[a][b] = 0` for all `0 <= a <= w1`, `0 <= b <= w2`

This makes sense because with zero items, the best we can do is pack nothing.

---

# 9. Complete algorithm

For each item weight `x` in `weights`:

1. iterate `a` from `w1` down to `0`
2. iterate `b` from `w2` down to `0`
3. if `a >= x`, try putting `x` in bag 1:
   - `dp[a][b] = max(dp[a][b], dp[a - x][b] + x)`
4. if `b >= x`, try putting `x` in bag 2:
   - `dp[a][b] = max(dp[a][b], dp[a][b - x] + x)`

At the end, the answer is:

`dp[w1][w2]`

because that represents the best packed weight under the full capacities of both bags.

---

# 10. Small example

Suppose:

`weights = [2, 3, 4]`
`w1 = 5`
`w2 = 3`

We want the maximum total weight that can be packed.

## Try possibilities manually

### Option A

- bag 1: `2 + 3 = 5`
- bag 2: empty

Total = `5`

### Option B

- bag 1: `4`
- bag 2: `3`

Total = `7`

This is valid because:

- bag 1 uses `4 <= 5`
- bag 2 uses `3 <= 3`

### Can we pack all three?

Weights total to `2 + 3 + 4 = 9`

Try:

- bag 1: `2 + 3 = 5`
- bag 2: `4` → not possible because `4 > 3`

Try:

- bag 1: `4 + 2 = 6` → exceeds `5`

Try:

- bag 1: `4`
- bag 2: `3 + 2 = 5` → exceeds `3`

So all three cannot fit.

Best answer is:

`7`

---

# 11. Step-by-step DP intuition on the example

We start with all `dp[a][b] = 0`.

## Process item `2`

Now for any state with capacity at least `2` in either bag, we can pack total weight `2`.

Examples:

- `dp[2][0] = 2`
- `dp[0][2] = 2`
- `dp[5][3] = 2`

## Process item `3`

Now we can combine with previous states.

Examples:

- put `3` in bag 1 and maybe `2` in bag 2
- or put `3` in bag 2 and maybe `2` in bag 1

So states like:

- `dp[5][0] = 5` from putting `2` and `3` in bag 1
- `dp[2][3] = 5` from putting `2` in bag 1 and `3` in bag 2
- `dp[5][3] = 5`

## Process item `4`

Now we check if placing `4` in one of the bags improves the total.

We find:

- `dp[5][3] = 7`

This comes from:

- put `4` in bag 1
- put `3` in bag 2

or equivalent valid packing.

Thus the final answer is `7`.

---

# 12. Java code

```java
class Solution {
    public int maxWeight(int[] weights, int w1, int w2) {
        int[][] dp = new int[w1 + 1][w2 + 1];

        for (int x : weights) {
            for (int a = w1; a >= 0; a--) {
                for (int b = w2; b >= 0; b--) {
                    if (a >= x) {
                        dp[a][b] = Math.max(dp[a][b], dp[a - x][b] + x);
                    }
                    if (b >= x) {
                        dp[a][b] = Math.max(dp[a][b], dp[a][b - x] + x);
                    }
                }
            }
        }

        return dp[w1][w2];
    }
}
```

---

# 13. Code explanation in detail

## Method signature

```java
public int maxWeight(int[] weights, int w1, int w2)
```

This method returns the maximum total weight that can be packed into the two bags.

- `weights` = item weights
- `w1` = capacity of bag 1
- `w2` = capacity of bag 2

## DP table creation

```java
int[][] dp = new int[w1 + 1][w2 + 1];
```

`dp[a][b]` stores the best total packed weight achievable with capacities `a` and `b`.

The table has:

- `w1 + 1` rows for bag 1 capacities `0..w1`
- `w2 + 1` columns for bag 2 capacities `0..w2`

Initially all values are `0`.

## Iterate through items

```java
for (int x : weights) {
```

We process items one by one.

Each item can be:

- skipped
- put in bag 1
- put in bag 2

## Iterate capacities backward

```java
for (int a = w1; a >= 0; a--) {
    for (int b = w2; b >= 0; b--) {
```

Backward iteration ensures the current item is not reused more than once.

This is the same principle as standard 0/1 knapsack.

## Try placing item in bag 1

```java
if (a >= x) {
    dp[a][b] = Math.max(dp[a][b], dp[a - x][b] + x);
}
```

If bag 1 has enough capacity, we can place the item there.

Then:

- previous state is `dp[a - x][b]`
- adding this item contributes `+x`

So we compare that against the current best.

## Try placing item in bag 2

```java
if (b >= x) {
    dp[a][b] = Math.max(dp[a][b], dp[a][b - x] + x);
}
```

Similarly, if bag 2 has enough capacity, we can place the item there.

## Skip case is implicit

There is no explicit “skip” statement because if neither transition improves `dp[a][b]`, the old value stays.

So:

- keeping the old value = skipping the item

## Return the answer

```java
return dp[w1][w2];
```

This gives the best packed weight using the full capacities of both bags.

---

# 14. Correctness argument

We can justify correctness with the standard knapsack reasoning.

## Claim

After processing the first `k` items, `dp[a][b]` equals the maximum total weight achievable using those items such that:

- total weight placed in bag 1 is at most `a`
- total weight placed in bag 2 is at most `b`

## Base case

Before processing any items:

- no weight can be packed
- so `dp[a][b] = 0` is correct for all capacities

## Inductive step

When processing an item of weight `x`, every valid solution for capacities `(a, b)` must do one of three things:

1. skip the item
2. place it in bag 1, if `a >= x`
3. place it in bag 2, if `b >= x`

These are the only legal possibilities, because each item can be placed in at most one bag.

The transition takes the maximum among those possibilities.

So the updated `dp[a][b]` is exactly the best achievable total packed weight after considering this item.

By induction, after all items are processed, `dp[w1][w2]` is the optimal answer.

---

# 15. Complexity analysis

Let:

- `n = weights.length`

## Time complexity

For each of the `n` items, we iterate over:

- all `w1 + 1` capacities of bag 1
- all `w2 + 1` capacities of bag 2

So the total time is:

**`O(n * w1 * w2)`**

More precisely:

**`O(n * (w1 + 1) * (w2 + 1))`**

which is asymptotically the same.

## Space complexity

The DP table size is:

- `(w1 + 1) * (w2 + 1)`

So the space usage is:

**`O(w1 * w2)`**

---

# 16. Why a simpler greedy does not work

A greedy strategy such as:

- always put the current item into the bag with more free space
- or always try the heavier items first

is not reliable.

Why?

Because a locally good placement may block a better combination later.

This is standard knapsack behavior:
the best global packing often depends on combinations, not just individual item choices.

So dynamic programming is the correct tool here.

---

# 17. Alternative viewpoint

You can also think of each item as having three destinations:

1. unused
2. bag 1
3. bag 2

The DP explores these choices efficiently while remembering only the information that matters for future decisions:

- remaining/usable capacity in bag 1
- remaining/usable capacity in bag 2

That is why the problem fits a two-dimensional 0/1 knapsack.

---
