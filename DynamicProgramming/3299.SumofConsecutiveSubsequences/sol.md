# Sum of Values of All Consecutive Subsequences — Detailed Solution Summary

## Problem Restatement

We are given an integer array `nums`.

A non-empty subsequence `arr` is called **consecutive** if one of the following holds:

1. `arr[i] - arr[i - 1] == 1` for every `i >= 1`
2. `arr[i] - arr[i - 1] == -1` for every `i >= 1`

So every adjacent difference inside the subsequence must be consistently:

- all `+1`, or
- all `-1`

Examples:

- `[3, 4, 5]` is valid
- `[9, 8]` is valid
- `[3]` is valid
- `[3, 4, 3]` is invalid
- `[8, 6]` is invalid

The **value** of a subsequence is the sum of its elements.

We must return:

> the sum of the values of **all** consecutive non-empty subsequences of `nums`

modulo:

```text
10^9 + 7
```

---

## First Important Clarification: This Is About Subsequences, Not Subarrays

This is a crucial distinction.

A **subarray** must be contiguous.

A **subsequence** only needs to preserve order.

So from:

```text
nums = [3, 1, 2]
```

the subsequence `[1, 2]` is valid even though those elements are not adjacent in the original array.

That means we cannot rely on contiguous-window techniques.

Instead, we must think in terms of:

- choosing elements in order
- tracking how many valid subsequences can end at a particular value

This naturally leads to dynamic programming.

---

## Core Insight

A valid consecutive subsequence must be one of only two global types:

### Type 1: Increasing by exactly `+1`

Examples:

- `[4]`
- `[2, 3]`
- `[7, 8, 9]`

### Type 2: Decreasing by exactly `-1`

Examples:

- `[6]`
- `[5, 4]`
- `[10, 9, 8]`

There is no third possibility.

So instead of handling all valid subsequences at once, it is much cleaner to split the problem into two families:

- subsequences whose adjacent differences are all `+1`
- subsequences whose adjacent differences are all `-1`

Then combine the results carefully.

That is the central structural simplification.

---

## Why Dynamic Programming by Ending Value Works

Suppose we are scanning `nums` from left to right and currently processing a value `x`.

Ask:

> What valid subsequences can end at this `x`?

### For increasing-by-1 subsequences

Any such subsequence ending at `x` must be either:

- the singleton `[x]`
- or some increasing subsequence ending at `x - 1`, extended by appending `x`

Because if the last difference must be `+1`, the previous value has to be exactly `x - 1`.

### For decreasing-by-1 subsequences

Similarly, any such subsequence ending at `x` must be either:

- the singleton `[x]`
- or some decreasing subsequence ending at `x + 1`, extended by appending `x`

Because if the last difference must be `-1`, the previous value has to be exactly `x + 1`.

This means that for each value `x`, we only need information about:

- value `x - 1` for increasing subsequences
- value `x + 1` for decreasing subsequences

That is a very local dependency, which makes DP efficient.

---

## The Right State to Store

We do not just need to know **how many** valid subsequences exist.

We also need the **sum of their values**, because the question asks for the total value over all valid subsequences.

So for each possible ending value, we store two things:

### Increasing DP

- `incCnt[v]` = number of increasing-by-1 subsequences seen so far that end at value `v`
- `incSum[v]` = total sum of values of all those subsequences

### Decreasing DP

- `decCnt[v]` = number of decreasing-by-1 subsequences seen so far that end at value `v`
- `decSum[v]` = total sum of values of all those subsequences

These states are maintained while iterating through `nums` from left to right.

---

## Why Count and Sum Are Both Needed

Suppose we want to extend all increasing subsequences ending at `x - 1` by appending `x`.

If there are:

- `c` such subsequences
- with total value sum `s`

then after appending `x` to each one:

- the number of new subsequences is still `c`
- each subsequence’s sum increases by `x`

So the new total contribution becomes:

```text
s + c * x
```

This is why just knowing the count is not enough.

And just knowing the sum is not enough either.

We need both.

---

## DP Transition for Increasing Subsequences

Let the current number be `x`.

Look at all increasing-by-1 subsequences ending at `x - 1`.

Suppose:

- `prevIncCnt = incCnt[x - 1]`
- `prevIncSum = incSum[x - 1]`

Now form new increasing subsequences ending at `x`.

There are two sources:

### 1. Start fresh with `[x]`

This contributes:

- count: `1`
- sum: `x`

### 2. Extend all increasing subsequences ending at `x - 1`

If we append `x` to all of them:

- number of new subsequences: `prevIncCnt`
- total new sum:
  - old sums contribute `prevIncSum`
  - appending `x` to each adds `prevIncCnt * x`

So contribution is:

```text
prevIncSum + prevIncCnt * x
```

### Combine both

So the new increasing subsequences created by this occurrence of `x` are:

```text
addIncCnt = 1 + prevIncCnt
addIncSum = x + prevIncSum + prevIncCnt * x
```

These are then accumulated into the states for value `x`.

---

## DP Transition for Decreasing Subsequences

Now consider decreasing-by-1 subsequences ending at `x`.

They can come from:

- singleton `[x]`
- extending decreasing subsequences ending at `x + 1`

Suppose:

- `prevDecCnt = decCnt[x + 1]`
- `prevDecSum = decSum[x + 1]`

Then the new decreasing subsequences created by this occurrence of `x` are:

```text
addDecCnt = 1 + prevDecCnt
addDecSum = x + prevDecSum + prevDecCnt * x
```

Again, accumulate them into the states for value `x`.

---

## Why Accumulation Into `x` Is Necessary

This point is subtle and important.

When `x` appears multiple times in `nums`, each occurrence can produce additional subsequences ending at value `x`.

These subsequences are distinct because they use different positions in the original array.

So when processing another `x`, we must **add** its contribution to the existing DP state for value `x`, not overwrite it.

That is why we do:

```text
incCnt[x] += addIncCnt
incSum[x] += addIncSum
```

and similarly for decreasing.

---

## Global Totals

We maintain:

- `totalInc` = sum of values of all increasing-by-1 subsequences
- `totalDec` = sum of values of all decreasing-by-1 subsequences

Every time we create new increasing subsequences ending at the current `x`, we add their total contribution `addIncSum` into `totalInc`.

Likewise for decreasing.

---

## The Double-Counting Problem

A singleton subsequence `[x]` is valid in both interpretations:

- increasing-by-1
- decreasing-by-1

because a length-1 subsequence has no adjacent differences to violate either rule.

That means if we compute:

```text
totalInc + totalDec
```

then every singleton subsequence is counted **twice**.

But every longer valid subsequence belongs to exactly one family:

- a length >= 2 subsequence cannot be both strictly `+1` and strictly `-1`

So the only overlap is the singleton subsequences.

Therefore we must subtract the sum of all single-element subsequences once.

Let:

```text
singleSum = sum(nums[i])
```

Then the final answer is:

```text
answer = totalInc + totalDec - singleSum
```

taken modulo `10^9 + 7`.

This is the key correction step.

---

## Full Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private static final long MOD = 1_000_000_007L;

    public int sumOfConsecutiveSubsequences(int[] nums) {
        Map<Long, Long> incCnt = new HashMap<>();
        Map<Long, Long> incSum = new HashMap<>();
        Map<Long, Long> decCnt = new HashMap<>();
        Map<Long, Long> decSum = new HashMap<>();

        long totalInc = 0;
        long totalDec = 0;
        long singleSum = 0;

        for (int num : nums) {
            long x = num;
            long xm1 = x - 1;
            long xp1 = x + 1;

            // Increasing subsequences ending at x
            long prevIncCnt = incCnt.getOrDefault(xm1, 0L);
            long prevIncSum = incSum.getOrDefault(xm1, 0L);

            long addIncCnt = (1 + prevIncCnt) % MOD;
            long addIncSum = (mod(x) + prevIncSum + prevIncCnt * mod(x)) % MOD;

            incCnt.put(x, (incCnt.getOrDefault(x, 0L) + addIncCnt) % MOD);
            incSum.put(x, (incSum.getOrDefault(x, 0L) + addIncSum) % MOD);

            totalInc = (totalInc + addIncSum) % MOD;

            // Decreasing subsequences ending at x
            long prevDecCnt = decCnt.getOrDefault(xp1, 0L);
            long prevDecSum = decSum.getOrDefault(xp1, 0L);

            long addDecCnt = (1 + prevDecCnt) % MOD;
            long addDecSum = (mod(x) + prevDecSum + prevDecCnt * mod(x)) % MOD;

            decCnt.put(x, (decCnt.getOrDefault(x, 0L) + addDecCnt) % MOD);
            decSum.put(x, (decSum.getOrDefault(x, 0L) + addDecSum) % MOD);

            totalDec = (totalDec + addDecSum) % MOD;

            singleSum = (singleSum + mod(x)) % MOD;
        }

        long ans = (totalInc + totalDec - singleSum) % MOD;
        if (ans < 0) ans += MOD;
        return (int) ans;
    }

    private long mod(long x) {
        x %= MOD;
        if (x < 0) x += MOD;
        return x;
    }
}
```

---

## Detailed Code Walkthrough

## 1. Hash maps for DP by ending value

```java
Map<Long, Long> incCnt = new HashMap<>();
Map<Long, Long> incSum = new HashMap<>();
Map<Long, Long> decCnt = new HashMap<>();
Map<Long, Long> decSum = new HashMap<>();
```

We use hash maps because the values in `nums` can be large or negative.

We are indexing DP states by numeric value, not by array index.

So an array-based DP would only be convenient if the numeric range were small and non-negative, which we cannot assume.

Using hash maps gives:

- expected `O(1)` access
- no need for coordinate compression
- direct representation of “best / total for ending value `v`”

---

## 2. Running global totals

```java
long totalInc = 0;
long totalDec = 0;
long singleSum = 0;
```

These track:

- total sum over all increasing valid subsequences
- total sum over all decreasing valid subsequences
- total sum of all singletons, which must later be subtracted once

---

## 3. Process each number from left to right

```java
for (int num : nums) {
    long x = num;
    long xm1 = x - 1;
    long xp1 = x + 1;
```

Every subsequence must preserve original order, so scanning left to right is the correct way to build subsequences incrementally.

We compute `x - 1` and `x + 1` because those are the only possible predecessor values for valid consecutive subsequences.

---

## 4. Increasing transition

```java
long prevIncCnt = incCnt.getOrDefault(xm1, 0L);
long prevIncSum = incSum.getOrDefault(xm1, 0L);
```

These represent all increasing-by-1 subsequences ending at `x - 1` built from earlier positions.

Then:

```java
long addIncCnt = (1 + prevIncCnt) % MOD;
long addIncSum = (mod(x) + prevIncSum + prevIncCnt * mod(x)) % MOD;
```

Interpretation:

- `1` corresponds to the singleton `[x]`
- `prevIncCnt` corresponds to extending all previous increasing subsequences ending at `x - 1`
- `prevIncSum` is their old sum
- `prevIncCnt * x` is the extra amount contributed by appending `x` to each of them

Then we accumulate:

```java
incCnt.put(x, (incCnt.getOrDefault(x, 0L) + addIncCnt) % MOD);
incSum.put(x, (incSum.getOrDefault(x, 0L) + addIncSum) % MOD);
```

This is necessary because different occurrences of the same value `x` create distinct subsequences.

Finally:

```java
totalInc = (totalInc + addIncSum) % MOD;
```

So every newly created increasing subsequence contributes to the global total.

---

## 5. Decreasing transition

```java
long prevDecCnt = decCnt.getOrDefault(xp1, 0L);
long prevDecSum = decSum.getOrDefault(xp1, 0L);
```

These represent all decreasing-by-1 subsequences ending at `x + 1`.

Then:

```java
long addDecCnt = (1 + prevDecCnt) % MOD;
long addDecSum = (mod(x) + prevDecSum + prevDecCnt * mod(x)) % MOD;
```

This is the exact mirror of the increasing logic.

Then accumulate:

```java
decCnt.put(x, (decCnt.getOrDefault(x, 0L) + addDecCnt) % MOD);
decSum.put(x, (decSum.getOrDefault(x, 0L) + addDecSum) % MOD);
```

And add to the global decreasing total:

```java
totalDec = (totalDec + addDecSum) % MOD;
```

---

## 6. Track singleton contribution

```java
singleSum = (singleSum + mod(x)) % MOD;
```

Every element contributes one singleton subsequence `[x]`.

Since singletons are counted once in increasing and once in decreasing, we later subtract `singleSum` exactly once.

---

## 7. Final correction

```java
long ans = (totalInc + totalDec - singleSum) % MOD;
if (ans < 0) ans += MOD;
return (int) ans;
```

The subtraction may become negative modulo `MOD`, so we normalize it back into the valid range.

---

## Why the Formula for `addIncSum` Is Correct

This is one of the most important points, so it is worth isolating.

Suppose the increasing subsequences ending at `x - 1` are:

```text
S1, S2, ..., Sc
```

Their total sum of values is:

```text
prevIncSum
```

Now append `x` to each one:

```text
S1 + [x], S2 + [x], ..., Sc + [x]
```

Each new subsequence has its sum increased by `x`.

So the total sum of all extended subsequences is:

```text
prevIncSum + c * x
```

Then also include singleton `[x]`, whose sum is just `x`.

So:

```text
addIncSum = x + prevIncSum + c * x
```

Exactly the same reasoning gives the decreasing formula.

---

## Worked Example

Consider:

```text
nums = [3, 4, 3]
```

Let us list all consecutive non-empty subsequences.

### Singletons

- `[3]` → 3
- `[4]` → 4
- `[3]` → 3

Total = `10`

### Length 2 subsequences

- `[3, 4]` → valid increasing, sum = 7
- `[3, 3]` → invalid
- `[4, 3]` → valid decreasing, sum = 7

Total = `14`

### Length 3 subsequence

- `[3, 4, 3]` → invalid because differences are `+1, -1`

So final answer:

```text
10 + 14 = 24
```

Now see how the DP captures this:

- first `3` creates singleton increasing and singleton decreasing
- then `4` extends increasing subsequences ending at `3`
- last `3` extends decreasing subsequences ending at `4`

The DP aggregates all of that without explicitly enumerating subsequences.

---

## Another Example

Consider:

```text
nums = [1, 2, 3]
```

Valid subsequences:

### Singletons

- `[1]`, `[2]`, `[3]` → total = `6`

### Length 2

- `[1, 2]` → 3
- `[1, 3]` → invalid
- `[2, 3]` → 5

Total = `8`

### Length 3

- `[1, 2, 3]` → valid, sum = 6

Final answer:

```text
6 + 8 + 6 = 20
```

The increasing DP naturally builds:

- `[1]`
- `[2]`, `[1,2]`
- `[3]`, `[2,3]`, `[1,2,3]`

There are no decreasing subsequences longer than 1 here.

---

## Why This Does Not Double Count Longer Subsequences

A subsequence of length at least 2 cannot be both:

- increasing by 1 at every step
- decreasing by 1 at every step

because those conditions are incompatible.

So the only overlap between the increasing and decreasing families is the singleton subsequences.

That makes the correction exact and safe.

---

## Formal Correctness Intuition

We can express correctness using invariants.

### Invariant for increasing DP

After processing the prefix `nums[0..i]`:

- `incCnt[v]` equals the number of increasing-by-1 subsequences within this prefix that end with value `v`
- `incSum[v]` equals the total sum of values of all such subsequences

### Why the transition preserves the invariant

Any increasing-by-1 subsequence ending at current value `x` must be either:

- `[x]`
- or an increasing-by-1 subsequence ending at `x - 1`, extended by `x`

There is no other possibility.

So the recurrence includes all valid cases and only valid cases.

The same reasoning holds for the decreasing DP.

### Final combination

Every valid non-empty consecutive subsequence is either:

- increasing-by-1
- decreasing-by-1

Single-element subsequences belong to both sets.

Longer subsequences belong to exactly one set.

Therefore:

```text
all valid sums = totalInc + totalDec - singleSum
```

This proves the algorithm’s logic.

---

## Why Brute Force Is Not Practical

There are `2^n - 1` non-empty subsequences.

Enumerating all of them is exponentially expensive.

Even after generating them, checking whether each is consecutive would take additional work.

So brute force quickly becomes infeasible.

The DP avoids explicit subsequence generation by aggregating all subsequences with the same ending value into compact states.

That is the major optimization.

---

## Time Complexity

For each number in `nums`, we do a constant number of hash map operations:

- read `x - 1`
- read `x + 1`
- update `x` in four maps
- update totals

Each hash map operation is expected `O(1)`.

So overall:

```text
Time Complexity = O(n)
```

expected time.

### Important note

This is expected `O(n)` due to hash map usage. In pathological hashing scenarios, hash maps can degrade, but under standard assumptions this is linear.

---

## Space Complexity

In the worst case, all numbers in `nums` are distinct and create distinct keys in the maps.

So the number of stored keys can be `O(n)`.

Hence:

```text
Space Complexity = O(n)
```

---

## Why HashMap Is the Practical Choice

An array-based DP indexed by value is only feasible if values are small and bounded.

But here `nums[i]` may be:

- large
- negative
- sparse

So a hash map is a more robust choice.

It stores only values that actually occur or are needed.

---

## Main Takeaways

This problem becomes manageable once you separate the two valid subsequence types:

1. increasing by `+1`
2. decreasing by `-1`

Then the right question becomes:

> How many valid subsequences end at value `x`, and what is the sum of their values?

That leads to the compact DP states:

- count by ending value
- sum by ending value

for both increasing and decreasing directions.

The final subtlety is recognizing that:

- singletons are counted in both families
- longer subsequences are not

So subtracting the sum of singletons exactly once gives the correct final result.

---

## Final Complexity Summary

- **Time Complexity:** `O(n)` expected
- **Space Complexity:** `O(n)`

---

## Final Conceptual Summary

This is essentially:

- DP over **subsequence endings by value**
- done separately for:
  - increasing consecutive subsequences
  - decreasing consecutive subsequences
- with a final overlap correction for singletons

The decisive ideas are:

1. split valid subsequences into two monotonic families
2. track both count and total sum for each ending value
3. extend only from `x - 1` or `x + 1`
4. subtract singleton contribution once to remove double counting

That yields an efficient and elegant solutio
