# Minimum Cost to Reach Each Stop Using Regular and Express Routes

## Problem Restatement

A train line has two parallel routes:

- **Regular**
- **Express**

Both routes go through the same stops from `0` to `n`.

You start at:

- **stop `0`**
- on the **regular route**

You are given:

- `regular[i]`: cost to go from stop `i - 1` to stop `i` using the regular route
- `express[i]`: cost to go from stop `i - 1` to stop `i` using the express route
- `expressCost`: cost to transfer from **regular → express**

Important rules:

- **regular → express** costs `expressCost`
- **express → regular** is free
- staying on express has no extra fee beyond the segment cost
- for each stop, you want the **minimum total cost to reach that stop**, regardless of whether you end on regular or express

We must return an array `costs` where `costs[i]` is the minimum cost to reach stop `i + 1`.

---

# Core Intuition

This problem looks like route switching, but the real structure is:

> At every stop, only two things matter:
>
> 1. the cheapest way to reach this stop and end on **regular**
> 2. the cheapest way to reach this stop and end on **express**

That is the full state.

We do **not** need to remember the entire path used earlier.

Why? Because once we are standing at stop `i`, the future only depends on:

- which route we are currently on
- how much total cost we have already paid

Nothing else about the past matters anymore.

This is exactly the signal that a **dynamic programming state compression** approach is appropriate.

---

# DP State Definition

Let:

- `dpR[i]` = minimum cost to reach stop `i` and end on the **regular** route
- `dpE[i]` = minimum cost to reach stop `i` and end on the **express** route

We start at stop `0` on regular, so:

- `dpR[0] = 0`
- `dpE[0] = +∞`

We use infinity for `dpE[0]` because we have not yet transferred to express.

---

# Transition Logic

We now decide how to reach stop `i` from stop `i - 1`.

## 1. Reaching stop `i` on the regular route

To end on **regular** at stop `i`, there are only two possible previous states:

- we were already on **regular** at stop `i - 1`
- we were on **express** at stop `i - 1` and switch to regular

Switching from express to regular is free.

So:

```text
dpR[i] = min(dpR[i - 1], dpE[i - 1]) + regular[i]
```

If arrays are 0-indexed in code, then the segment index becomes `i - 1`.

---

## 2. Reaching stop `i` on the express route

To end on **express** at stop `i`, again there are only two possibilities:

- we were already on **express** at stop `i - 1` and stay on express
- we were on **regular** at stop `i - 1`, transfer to express, pay `expressCost`, then take the express segment

So:

```text
dpE[i] = min(
    dpE[i - 1] + express[i],
    dpR[i - 1] + expressCost + express[i]
)
```

---

## 3. Final answer for stop `i`

A stop is considered reached if we arrive there by either route.

So:

```text
costs[i] = min(dpR[i], dpE[i])
```

---

# Why This State Is Sufficient

A common doubt is:

> Do we need to track how many times we switched?

No.

Each switch from regular to express is paid exactly at the moment it happens.
That cost is already absorbed into the DP value.

Once we know the cheapest cost to be at stop `i` on a certain route, any more detailed history becomes irrelevant.

That is why the DP works with only two states.

---

# Base Case

At stop `0`:

- being on regular costs `0`
- being on express is impossible initially

So:

```text
dpR[0] = 0
dpE[0] = INF
```

This enforces the rule that you begin on regular.

---

# Full DP Recurrence in 1-Indexed Math Form

For each stop `i` from `1` to `n`:

```text
dpR[i] = min(dpR[i - 1], dpE[i - 1]) + regular[i]
dpE[i] = min(dpE[i - 1] + express[i], dpR[i - 1] + expressCost + express[i])
costs[i] = min(dpR[i], dpE[i])
```

---

# Space Optimization

Observe the recurrence carefully:

- `dpR[i]` depends only on `dpR[i - 1]` and `dpE[i - 1]`
- `dpE[i]` also depends only on the previous stop

So we do **not** need full arrays for the DP states.

We only keep:

- `reg` = current best cost ending on regular
- `exp` = current best cost ending on express

Then for every segment:

```text
newReg = min(reg, exp) + regular[i]
newExp = min(exp + express[i], reg + expressCost + express[i])
```

After computing both, update:

```text
reg = newReg
exp = newExp
answer[i] = min(reg, exp)
```

This reduces extra space from `O(n)` to `O(1)`.

---

# Step-by-Step Example

Consider:

```text
regular = [1, 6, 9]
express = [5, 2, 3]
expressCost = 4
```

We start with:

```text
reg = 0
exp = INF
```

---

## Stop 1

### End on regular

```text
newReg = min(0, INF) + 1 = 1
```

### End on express

Two choices:

- stay on express: impossible
- switch from regular: `0 + 4 + 5 = 9`

So:

```text
newExp = 9
```

Answer for stop 1:

```text
min(1, 9) = 1
```

Now:

```text
reg = 1
exp = 9
```

---

## Stop 2

### End on regular

```text
newReg = min(1, 9) + 6 = 7
```

### End on express

Two choices:

- stay on express: `9 + 2 = 11`
- switch from regular: `1 + 4 + 2 = 7`

So:

```text
newExp = 7
```

Answer for stop 2:

```text
min(7, 7) = 7
```

Now:

```text
reg = 7
exp = 7
```

---

## Stop 3

### End on regular

```text
newReg = min(7, 7) + 9 = 16
```

### End on express

Two choices:

- stay on express: `7 + 3 = 10`
- switch from regular: `7 + 4 + 3 = 14`

So:

```text
newExp = 10
```

Answer for stop 3:

```text
min(16, 10) = 10
```

Final output:

```text
[1, 7, 10]
```

---

# Key Insight Behind the Recurrence

The tricky part is not the movement cost.
The tricky part is the **directional switching rule**:

- regular → express costs money
- express → regular is free

That asymmetry is what forces us to maintain two route-specific states.

If switching were free in both directions, the problem would collapse into a much simpler form.
If switching had different costs both ways, the same DP idea would still work, but with adjusted transitions.

So the essence of the problem is:

> the cost to reach a stop depends not just on the stop, but also on the route used to reach it.

That is why a 1D DP on stops alone would be incorrect.

---

# Correctness Argument

We can justify the solution rigorously.

## Claim 1

`dpR[i]` correctly stores the minimum cost to reach stop `i` and end on regular.

### Reason

Any path that ends on regular at stop `i` must come from exactly one of these:

- regular at stop `i - 1`
- express at stop `i - 1`

There are no other possibilities, because movement is only from one stop to the next.

Among these two possibilities, taking the cheaper previous cost and then paying the regular segment cost gives the true minimum.

So the recurrence for `dpR[i]` is complete and correct.

---

## Claim 2

`dpE[i]` correctly stores the minimum cost to reach stop `i` and end on express.

### Reason

Any path ending on express at stop `i` must come from exactly one of:

- express at stop `i - 1` and remain on express
- regular at stop `i - 1`, then switch to express, paying `expressCost`

Again, these are the only valid possibilities.

Taking the minimum of these two costs yields the optimal value for `dpE[i]`.

---

## Claim 3

`min(dpR[i], dpE[i])` is the minimum cost to reach stop `i`.

### Reason

The problem allows the stop to be reached through either route.
So the best overall cost is simply the better of the two route-ending states.

---

## Conclusion

By induction over stops from `0` to `n`, the DP computes the correct answer for every stop.

---

# Java Code

```java
class Solution {
    public long[] minimumCosts(int[] regular, int[] express, int expressCost) {
        int n = regular.length;
        long[] ans = new long[n];

        long reg = 0;
        long exp = Long.MAX_VALUE / 4;

        for (int i = 0; i < n; i++) {
            long newReg = Math.min(reg, exp) + regular[i];
            long newExp = Math.min(
                exp + express[i],
                reg + expressCost + express[i]
            );

            reg = newReg;
            exp = newExp;
            ans[i] = Math.min(reg, exp);
        }

        return ans;
    }
}
```

---

# Code Walkthrough

## Method signature

```java
public long[] minimumCosts(int[] regular, int[] express, int expressCost)
```

We return `long[]` rather than `int[]` because the cumulative cost can become larger than what fits safely in `int`.

Even if each segment cost fits in `int`, summing many of them can overflow.

---

## Output array

```java
int n = regular.length;
long[] ans = new long[n];
```

There are `n` segments from stop `0` to stop `n`, so we need `n` answers.

---

## DP variables

```java
long reg = 0;
long exp = Long.MAX_VALUE / 4;
```

- `reg` is the minimum cost to be at the current stop on regular
- `exp` is the minimum cost to be at the current stop on express

We use a very large number instead of true infinity.

`Long.MAX_VALUE / 4` is safer than `Long.MAX_VALUE`, because later we add costs to it.
Using the full maximum could overflow during addition.

---

## Main loop

```java
for (int i = 0; i < n; i++) {
```

Each `i` corresponds to moving from stop `i` to stop `i + 1`.

---

## Compute new regular state

```java
long newReg = Math.min(reg, exp) + regular[i];
```

To end on regular for the next stop:

- either stay on regular
- or come from express for free

Then pay the regular segment cost.

---

## Compute new express state

```java
long newExp = Math.min(
    exp + express[i],
    reg + expressCost + express[i]
);
```

To end on express for the next stop:

- either stay on express
- or transfer from regular and pay `expressCost`

Then pay the express segment cost.

---

## Update states

```java
reg = newReg;
exp = newExp;
```

Now these represent the minimum costs for the newly reached stop.

---

## Save answer for current stop

```java
ans[i] = Math.min(reg, exp);
```

The stop is reachable through either route, so store the cheaper one.

---

# Complexity Analysis

## Time Complexity

For each of the `n` stops, we do only constant work:

- a few additions
- a few `min` operations

So:

```text
Time Complexity = O(n)
```

---

## Space Complexity

Ignoring the output array, we only store:

- `reg`
- `exp`
- `newReg`
- `newExp`

So extra space is:

```text
Space Complexity = O(1)
```

The returned answer array itself takes `O(n)`, which is required by the problem.

---

# Why Greedy Does Not Work Reliably

A tempting but flawed idea is:

> At each stop, choose whichever route is cheaper for this segment.

This fails because the express route has an **entry cost** (`expressCost`).

A locally cheaper express segment might still be globally worse if paying the switching fee is not worth it yet.

Likewise, a slightly more expensive move now may position us advantageously for several later express segments.

So this is not a local-choice problem.
It requires state-aware optimization, which is why DP is the correct method.

---

# Common Pitfalls

## 1. Forgetting that we start on regular

Many mistakes come from allowing express immediately at no cost.

That is wrong.

You must pay `expressCost` the first time you move from regular to express.

---

## 2. Using `int` instead of `long`

The total accumulated cost may exceed 32-bit integer range.

Use `long`.

---

## 3. Updating `reg` before computing `newExp`

If you overwrite `reg` too early, then `newExp` may accidentally use the new state instead of the previous one.

Always compute `newReg` and `newExp` first, then assign them.

---

## 4. Misreading the free switch direction

Only **express → regular** is free.
**regular → express** costs `expressCost`.

The asymmetry matters.

---

# Final Takeaway

This is a classic **two-state dynamic programming** problem.

The key mental model is:

- every stop has two meaningful states: end on regular, or end on express
- each new state depends only on the previous stop’s two states
- therefore we can solve it in linear time with constant extra memory

The recurrence is simple once the state is chosen correctly:

```text
newReg = min(reg, exp) + regular[i]
newExp = min(exp + express[i], reg + expressCost + express[i])
answer[i] = min(newReg, newExp)
```

That is the whole solution.
