# Minimum Number of Days to Defeat All Monsters

## Problem Restatement

You are given an array `power` where `power[i]` is the power of the `i`th monster.

You start with:

- `mana = 0`
- `gain = 1`

Each day:

1. your mana increases by `gain`
2. after that, if your mana is at least the power of some undefeated monster, you may defeat that monster

When you defeat a monster:

- your mana is reset to `0`
- `gain` increases by `1`

You must defeat all monsters, and the goal is to minimize the total number of days.

---

## First Principles: What Actually Matters?

At first glance, this looks like a simulation problem involving:

- mana accumulation day by day
- different monster powers
- choosing which monster to defeat next
- gain increasing after each kill

That suggests there may be a lot of state.

But there is one extremely important simplification:

### After every monster kill, mana resets to 0

That destroys any carry-over effect from one monster to the next.

So once a monster is defeated, the only long-term effect is:

- `gain` becomes larger by 1

This means the exact mana history does **not** matter across kills.

That is the key structural insight.

---

## Core Observation

Suppose you have already defeated `k` monsters.

Then your current `gain` must be:

`gain = k + 1`

because:

- initially `gain = 1`
- each defeated monster increases `gain` by 1

Now suppose the next monster you choose has power `p`.

You are starting from:

- `mana = 0`
- daily increase = `gain`

After `d` days, your mana becomes:

`d * gain`

You can defeat the monster as soon as:

`d * gain >= p`

So the minimum number of days needed is:

`ceil(p / gain)`

That means the entire process can be reinterpreted as:

- choose an order of monsters
- if a monster is killed in position `1`, it costs `ceil(power / 1)` days
- if a monster is killed in position `2`, it costs `ceil(power / 2)` days
- ...
- if a monster is killed in position `k`, it costs `ceil(power / k)` days

So the problem becomes:

> Find an ordering of monsters that minimizes
> `ceil(power[perm[0]] / 1) + ceil(power[perm[1]] / 2) + ... + ceil(power[perm[n-1]] / n)`.

This is no longer a day-by-day simulation problem.
It is an **ordering optimization** problem.

---

## Why Greedy Is Suspicious

A natural temptation is to try a greedy rule such as:

- kill weakest first
- kill strongest first
- kill the monster with best “benefit” first

But none of these is obviously safe.

Why?

Because putting one monster earlier means another monster gets pushed later, and later positions are cheaper because gain is larger.
So every local choice affects the cost structure of all future choices.

That is exactly the kind of dependency where greedy often fails and dynamic programming becomes the right tool.

---

## Best Modeling: Bitmask DP

Since the cost of killing a monster depends only on:

- which monsters have already been defeated, or equivalently
- how many monsters have already been defeated

we can use a **subset DP**.

### State

Let:

- `mask` = bitmask representing which monsters are already defeated
- `dp[mask]` = minimum days needed to defeat exactly the monsters in `mask`

If `cnt = bitcount(mask)`, then the next monster will be fought with:

`gain = cnt + 1`

because `cnt` monsters have already been defeated.

Then for every monster `i` not yet in `mask`, we may choose to defeat it next.

---

## Transition

Suppose monster `i` is not in `mask`.

Then:

- `nextMask = mask | (1 << i)`

The number of days needed to defeat monster `i` next is:

`ceil(power[i] / gain)`

So the transition is:

`dp[nextMask] = min(dp[nextMask], dp[mask] + ceil(power[i] / gain))`

This tries every possible next choice and keeps the best total.

---

## Integer Formula for Ceiling

To avoid floating point, use:

`ceil(a / b) = (a + b - 1) / b`

So:

```java
daysNeeded = (power[i] + gain - 1) / gain;
```

This is exact integer arithmetic.

---

## Why the State Is Complete

This is worth checking carefully.

Could two different histories that produce the same `mask` lead to different future possibilities?

No.

Once `mask` is fixed:

- the number of defeated monsters is fixed
- therefore `gain` is fixed
- mana has already been reset to 0 after the last kill

So there is no hidden information left.

That means `mask` alone fully determines the future cost structure.

This is exactly why the DP is valid.

---

## Correct Java Code

Because the answer can exceed `int`, especially for large powers such as many monsters with power `10^9`, the DP must use `long`.

```java
import java.util.*;

class Solution {
    public long minimumTime(int[] power) {
        int n = power.length;
        int size = 1 << n;

        long[] dp = new long[size];
        Arrays.fill(dp, Long.MAX_VALUE / 4);
        dp[0] = 0L;

        for (int mask = 0; mask < size; mask++) {
            int killed = Integer.bitCount(mask);
            long gain = killed + 1L;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) continue; // already defeated

                long daysNeeded = (power[i] + gain - 1) / gain;
                int nextMask = mask | (1 << i);

                dp[nextMask] = Math.min(dp[nextMask], dp[mask] + daysNeeded);
            }
        }

        return dp[size - 1];
    }
}
```

---

## Detailed Walkthrough of the Code

### 1. Number of monsters and number of subsets

```java
int n = power.length;
int size = 1 << n;
```

There are `n` monsters.
Each monster can be either defeated or not defeated in a subset state.
So the total number of subset states is `2^n`.

---

### 2. DP array

```java
long[] dp = new long[size];
Arrays.fill(dp, Long.MAX_VALUE / 4);
dp[0] = 0L;
```

- `dp[mask]` = best answer for that subset
- initialize everything to a very large number
- `dp[0] = 0` because defeating no monsters takes 0 days

We use `Long.MAX_VALUE / 4` rather than `Long.MAX_VALUE` to avoid overflow when doing:

`dp[mask] + daysNeeded`

---

### 3. Iterate over all subsets

```java
for (int mask = 0; mask < size; mask++) {
```

We try to extend every reachable defeated set.

---

### 4. Determine current gain

```java
int killed = Integer.bitCount(mask);
long gain = killed + 1L;
```

If `killed` monsters are already defeated, then gain is exactly `killed + 1`.

That is the dynamic resource level for the next choice.

---

### 5. Try every undefeated monster as the next one

```java
for (int i = 0; i < n; i++) {
    if ((mask & (1 << i)) != 0) continue;
```

If the bit is already set, that monster was already defeated, so skip it.

Otherwise, monster `i` is a candidate for the next kill.

---

### 6. Compute the days needed for that next kill

```java
long daysNeeded = (power[i] + gain - 1) / gain;
```

This is the integer-ceiling formula for `ceil(power[i] / gain)`.

Because `gain` is larger later in the ordering, some monsters become cheaper if delayed.

---

### 7. Transition to the new subset

```java
int nextMask = mask | (1 << i);

dp[nextMask] = Math.min(dp[nextMask], dp[mask] + daysNeeded);
```

If we choose monster `i` next, we move from `mask` to `nextMask`.

We keep the minimum total days among all ways to reach `nextMask`.

---

### 8. Final answer

```java
return dp[size - 1];
```

`size - 1` is the mask with all bits set, meaning all monsters are defeated.

---

## Worked Example

Consider:

```text
power = [2, 3, 4]
```

We want the best order.

### Order 1: [2, 3, 4]

- first monster power 2 with gain 1:
  - `ceil(2 / 1) = 2`
- second monster power 3 with gain 2:
  - `ceil(3 / 2) = 2`
- third monster power 4 with gain 3:
  - `ceil(4 / 3) = 2`

Total = `2 + 2 + 2 = 6`

---

### Order 2: [2, 4, 3]

- power 2 with gain 1:
  - `2`
- power 4 with gain 2:
  - `2`
- power 3 with gain 3:
  - `1`

Total = `2 + 2 + 1 = 5`

---

### Order 3: [4, 2, 3]

- power 4 with gain 1:
  - `4`
- power 2 with gain 2:
  - `1`
- power 3 with gain 3:
  - `1`

Total = `4 + 1 + 1 = 6`

So the best order is `[2, 4, 3]` and the answer is `5`.

The DP explores all such orderings implicitly and finds the minimum.

---

## Why `long` Is Necessary

This is a critical implementation detail.

Consider 17 monsters, all with power `1_000_000_000`.

Then the total minimum days is:

`ceil(10^9 / 1) + ceil(10^9 / 2) + ... + ceil(10^9 / 17)`

This is already well above `2,147,483,647`, the maximum value of a 32-bit signed integer.

So if `dp` is declared as `int[]`, it overflows and produces wrong answers.

That is why the correct implementation uses:

- `long[] dp`
- `long gain`
- `long daysNeeded`
- return type `long`

---

## Correctness Argument

We can prove correctness by induction on the number of bits set in `mask`.

### Claim

`dp[mask]` equals the minimum number of days needed to defeat exactly the monsters in `mask`.

---

### Base Case

For `mask = 0`, no monsters are defeated.

This takes `0` days, so:

`dp[0] = 0`

which is correct.

---

### Inductive Step

Assume the claim is true for some subset `mask`.

Let `cnt = bitcount(mask)`, so the current gain is `cnt + 1`.

If we choose an undefeated monster `i` next, then:

- it takes `ceil(power[i] / (cnt + 1))` days to defeat it
- after defeating it, the defeated set becomes `nextMask = mask ∪ {i}`

Thus any valid strategy reaching `nextMask` through `mask` has cost:

`dp[mask] + ceil(power[i] / (cnt + 1))`

The transition takes the minimum across all possible choices of next monster and all ways to reach the previous mask.

Therefore `dp[nextMask]` is set to the true minimum.

By induction, all DP states are correct, including the final state of all monsters defeated.

---

## Complexity Analysis

Let `n = power.length`.

### Number of states

There are:

`2^n`

subset masks.

---

### Work per state

For each subset, we try up to `n` monsters as the next one.

So time complexity is:

`O(n * 2^n)`

---

### Space complexity

We store one value per subset:

`O(2^n)`

---

## Why This Complexity Is Reasonable

This is exponential in `n`, so it is intended for small `n`.

That usually signals one of these patterns:

- subset DP
- traveling-salesman style ordering DP
- state compression DP

This problem falls exactly into that family because the order matters and the future cost depends on how many items have already been processed.

---

## Common Mistakes

### 1. Using `int` instead of `long`

This causes overflow on large powers or enough monsters.

### 2. Simulating day by day

That is far too slow and misses the structural simplification.

### 3. Forgetting that mana resets to 0

If mana carried over, the problem would be very different.
The reset is what makes the subset DP clean.

### 4. Using the wrong current gain

If `k` monsters are already defeated, the next gain is `k + 1`, not `k`.

### 5. Using floating point for ceiling

That is unnecessary and may introduce precision issues.
Use integer arithmetic:
`(a + b - 1) / b`

---

## Alternative Recursive Formulation

You can also write this as memoized DFS:

- `solve(mask)` = minimum days needed starting from defeated set `mask`

Then:

- `gain = bitcount(mask) + 1`
- try every undefeated monster `i`
- return the minimum of:
  `ceil(power[i] / gain) + solve(mask | (1 << i))`

That has the same complexity:

- `O(n * 2^n)` time
- `O(2^n)` memory for memoization

The iterative DP is often simpler to control and slightly more efficient.

---

## High-Level Intuition Summary

The decisive insight is:

- mana reset means no cross-fight mana history matters
- after `k` kills, gain is always `k + 1`
- so the cost of killing a monster depends only on **when** in the order you kill it

That transforms the problem into:

- choose an order minimizing a sum of position-dependent costs

And once the problem is about choosing an order over a small set, subset DP becomes the natural tool.

---

## Final Complexity Summary

- **Time Complexity:** `O(n * 2^n)`
- **Space Complexity:** `O(2^n)`

---

## Final Takeaway

This problem looks like resource simulation, but it is really an **ordering DP** problem.

The mana reset collapses the state dramatically.
Once you notice that, the whole problem becomes:

- each subset tells you how many monsters are already gone
- that determines the current gain
- try each remaining monster next
- use bitmask DP to optimize over all orders

That is the complete logic behind the solution.
