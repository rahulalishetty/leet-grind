# Minimum Operations to Make Array Monotonic — Exhaustive Summary

## Problem Overview

We are given a `0`-indexed integer array `nums`.

In one operation, we may choose any index `i` and do exactly one of the following:

- increase `nums[i]` by `1`
- decrease `nums[i]` by `1`

We must return the **minimum number of operations** needed to make the array either:

- **non-decreasing**, or
- **non-increasing**

---

## What does monotonic mean here?

### Non-decreasing

An array is non-decreasing if:

```text
a[0] <= a[1] <= a[2] <= ...
```

### Non-increasing

An array is non-increasing if:

```text
a[0] >= a[1] >= a[2] >= ...
```

We are allowed to choose whichever of these two directions gives the smaller number of operations.

---

# First Key Insight

Changing `nums[i]` to some target value `x` costs:

```text
|nums[i] - x|
```

because each operation changes the value by exactly `1`.

So the problem becomes:

> Find a target array `target` such that:
>
> - `target` is non-decreasing, or
> - `target` is non-increasing
>
> and the total cost
>
> ```text
> sum(|nums[i] - target[i]|)
> ```
>
> is minimized.

So this is no longer about simulating individual `+1` and `-1` moves directly.
It is a **minimum-cost monotonic transformation** problem.

---

# Reduce the problem to two subproblems

We can solve:

1. minimum cost to make `nums` **non-decreasing**
2. minimum cost to make `nums` **non-increasing**

Then return:

```text
min(costNonDecreasing, costNonIncreasing)
```

---

# Why non-increasing can be converted into non-decreasing

A very useful trick:

Making `nums` non-increasing is equivalent to making:

```text
reverse(nums)
```

non-decreasing.

Why?

If:

```text
nums[0] >= nums[1] >= nums[2]
```

then after reversing:

```text
nums[2] <= nums[1] <= nums[0]
```

So we only need to write one helper:

```text
minCostNonDecreasing(...)
```

Then compute:

- `minCostNonDecreasing(nums)`
- `minCostNonDecreasing(reverse(nums))`

and take the smaller answer.

---

# The subtle question: what target values do we need to consider?

Suppose we want to make the array non-decreasing.

In principle, each final value could be any integer, which seems impossible to DP directly.

But we do **not** need to consider all integers.

It is enough to consider assigning each position to one of the values in the **sorted copy of `nums`**.

Let:

```text
sorted = sorted(nums)
```

Why is this enough?

Because in an optimal monotonic solution, the chosen target levels can always be adjusted, or “snapped,” to one of the existing sorted values without increasing total absolute-deviation cost.

This is a standard idea in isotonic-style DP with absolute costs.

So our candidate target values come only from:

```text
sorted[0], sorted[1], ..., sorted[n-1]
```

That keeps the state space finite and manageable.

---

# Dynamic Programming for Non-Decreasing Case

Let:

```text
sorted = sorted(nums)
```

Now define:

```text
dp[j]
```

as the minimum cost to transform the processed prefix of `nums` into a non-decreasing array such that the current element is assigned the value:

```text
sorted[j]
```

More intuitively:

- we process `nums` from left to right
- at each position, we choose one candidate target value from `sorted`
- the chosen target values must be non-decreasing

---

# Base Case

For the first element `nums[0]`, if we assign it to `sorted[j]`, then the cost is simply:

```text
|nums[0] - sorted[j]|
```

So:

```text
dp[j] = |nums[0] - sorted[j]|
```

for all `j`.

---

# Transition

Suppose we are processing index `i`.

If we assign:

```text
nums[i] -> sorted[j]
```

then because the final array must be non-decreasing, the previous assigned value must be:

```text
<= sorted[j]
```

That means the previous index could have used any candidate:

```text
sorted[0], sorted[1], ..., sorted[j]
```

So the transition is:

```text
newDp[j] = min(dp[0], dp[1], ..., dp[j]) + |nums[i] - sorted[j]|
```

This is the key recurrence.

---

# Why prefix minimum is important

If we compute:

```text
min(dp[0..j])
```

naively for every `j`, the transition becomes `O(n^3)` overall.

But observe that while scanning `j` from left to right, we can maintain:

```text
prefixMin = min(dp[0..j])
```

incrementally.

So for each row:

```text
prefixMin = min(prefixMin, dp[j])
newDp[j] = prefixMin + |nums[i] - sorted[j]|
```

That reduces the transition for one row to `O(n)`.

Since there are `n` rows, total becomes:

```text
O(n^2)
```

---

# Algorithm for Non-Decreasing Case

1. Make a sorted copy of `nums`
2. Initialize a DP array:
   - `dp[j] = |nums[0] - sorted[j]|`
3. For each next element `nums[i]`:
   - create `next[]`
   - maintain a running `prefixMin`
   - for each `j`:
     - update `prefixMin = min(prefixMin, dp[j])`
     - set:

```text
next[j] = prefixMin + |nums[i] - sorted[j]|
```

4. Replace `dp = next`
5. Final answer is the minimum value in `dp`

---

# Java Solution

```java
import java.util.*;

class Solution {
    public int convertArray(int[] nums) {
        return Math.min(minCostNonDecreasing(nums), minCostNonDecreasing(reverse(nums)));
    }

    private int minCostNonDecreasing(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        long[] dp = new long[n];
        for (int j = 0; j < n; j++) {
            dp[j] = Math.abs((long) nums[0] - sorted[j]);
        }

        for (int i = 1; i < n; i++) {
            long[] next = new long[n];
            long prefixMin = Long.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                prefixMin = Math.min(prefixMin, dp[j]);
                next[j] = prefixMin + Math.abs((long) nums[i] - sorted[j]);
            }

            dp = next;
        }

        long ans = Long.MAX_VALUE;
        for (long val : dp) {
            ans = Math.min(ans, val);
        }
        return (int) ans;
    }

    private int[] reverse(int[] nums) {
        int n = nums.length;
        int[] rev = new int[n];
        for (int i = 0; i < n; i++) {
            rev[i] = nums[n - 1 - i];
        }
        return rev;
    }
}
```

---

# Code Walkthrough

## `convertArray`

```java
public int convertArray(int[] nums) {
    return Math.min(minCostNonDecreasing(nums), minCostNonDecreasing(reverse(nums)));
}
```

This computes:

- cost to make original array non-decreasing
- cost to make reversed array non-decreasing
  which is equivalent to making original array non-increasing

and returns the smaller value.

---

## `minCostNonDecreasing`

This function computes the minimum cost to make an array non-decreasing.

### Step 1: sort the candidate values

```java
int[] sorted = nums.clone();
Arrays.sort(sorted);
```

These are the only target levels we need to consider.

---

### Step 2: initialize first DP row

```java
long[] dp = new long[n];
for (int j = 0; j < n; j++) {
    dp[j] = Math.abs((long) nums[0] - sorted[j]);
}
```

If the first element is converted to `sorted[j]`, the cost is just the absolute difference.

---

### Step 3: build DP row by row

```java
for (int i = 1; i < n; i++) {
    long[] next = new long[n];
    long prefixMin = Long.MAX_VALUE;

    for (int j = 0; j < n; j++) {
        prefixMin = Math.min(prefixMin, dp[j]);
        next[j] = prefixMin + Math.abs((long) nums[i] - sorted[j]);
    }

    dp = next;
}
```

For each possible target value `sorted[j]` at position `i`:

- previous target must be at most `sorted[j]`
- best previous cost is the prefix minimum among `dp[0..j]`

Then add the adjustment cost for the current element.

---

### Step 4: get final answer

```java
long ans = Long.MAX_VALUE;
for (long val : dp) {
    ans = Math.min(ans, val);
}
return (int) ans;
```

Since the final element could end at any valid target value, take the minimum across the last DP row.

---

# Worked Example

Consider:

```text
nums = [3, 2, 4, 5, 0]
```

We want the minimum cost to make it non-decreasing.

---

## Step 1: sort candidate values

```text
sorted = [0, 2, 3, 4, 5]
```

---

## Step 2: initialize first row

For `nums[0] = 3`:

```text
dp[0] = |3 - 0| = 3
dp[1] = |3 - 2| = 1
dp[2] = |3 - 3| = 0
dp[3] = |3 - 4| = 1
dp[4] = |3 - 5| = 2
```

So:

```text
dp = [3, 1, 0, 1, 2]
```

---

## Step 3: process `nums[1] = 2`

We compute prefix minima of previous row while filling `next`.

### j = 0, target = 0

```text
prefixMin = min(inf, 3) = 3
next[0] = 3 + |2 - 0| = 5
```

### j = 1, target = 2

```text
prefixMin = min(3, 1) = 1
next[1] = 1 + |2 - 2| = 1
```

### j = 2, target = 3

```text
prefixMin = min(1, 0) = 0
next[2] = 0 + |2 - 3| = 1
```

### j = 3, target = 4

```text
prefixMin = min(0, 1) = 0
next[3] = 0 + |2 - 4| = 2
```

### j = 4, target = 5

```text
prefixMin = min(0, 2) = 0
next[4] = 0 + |2 - 5| = 3
```

Now:

```text
dp = [5, 1, 1, 2, 3]
```

This means, for the first two elements, these are the minimum costs for ending at each target level.

---

## Continue similarly

We repeat this for each remaining position.

The DP automatically ensures the assigned target sequence is non-decreasing.

At the end, the smallest value in the last row is the answer for the non-decreasing version.

Then we also do the same on the reversed array to account for non-increasing.

---

# Why the solution is correct

The correctness rests on three ideas.

## 1. Any valid final array must be monotonic

That is directly required by the problem.

## 2. Cost of fixing one element is independent once the target value is chosen

Changing `nums[i]` to target `x` always costs:

```text
|nums[i] - x|
```

So total cost is just the sum across positions.

## 3. For a non-decreasing target, the previous chosen value must not exceed the current one

That is exactly what the recurrence captures:

```text
newDp[j] = min(dp[0..j]) + |nums[i] - sorted[j]|
```

This considers every valid previous state and chooses the cheapest.

Since all valid monotonic assignments are explored through these transitions, and invalid assignments are excluded, the DP returns the optimal answer.

---

# Why considering only sorted original values is enough

This is one of the trickiest parts conceptually.

The final monotone sequence could seem to require arbitrary integers.
But with absolute difference cost, an optimal solution can always be adjusted so that each chosen level matches one of the sorted values from the original array.

So instead of an infinite number of target choices, we only need `n` candidates.

That makes dynamic programming feasible.

This is a standard compression trick for monotonic transformation problems.

---

# Complexity Analysis

Let:

```text
n = nums.length
```

## Time Complexity

Sorting takes:

```text
O(n log n)
```

The DP has:

- `n` positions
- `n` candidate target values

So:

```text
O(n^2)
```

dominates overall.

Final time complexity:

```text
O(n^2)
```

---

## Space Complexity

We store:

- one sorted array of size `n`
- one current DP row of size `n`
- one next DP row of size `n`

So total extra space is:

```text
O(n)
```

---

# Common Pitfalls

## 1. Trying greedy local fixes

A local decision like “adjust current element just enough to preserve monotonicity” is not always globally optimal.

Later elements may make that choice expensive.

This is why dynamic programming is needed.

---

## 2. Forgetting to check both directions

The problem allows either:

- non-decreasing
- non-increasing

You must compute both and take the minimum.

---

## 3. Iterating all integer target values

That would be far too large and unnecessary.

Only the sorted values from the original array are needed as candidate levels.

---

## 4. Using `int` carelessly in intermediate states

Even if final answer fits in `int`, intermediate DP sums are safer in `long`.

That is why the implementation uses `long[] dp`.

---

# Alternative viewpoint

Another way to think about this:

We are trying to “project” the array onto the set of monotone sequences with minimum L1 distance.

Here L1 distance means:

```text
sum |nums[i] - target[i]|
```

The DP computes that projection efficiently after compressing the target values.

---

# Small Example for Non-Increasing Case

Suppose:

```text
nums = [1, 5, 2]
```

To make it non-increasing directly is a bit awkward.

Instead reverse it:

```text
reverse(nums) = [2, 5, 1]
```

Now compute minimum cost to make this reversed array non-decreasing.

That gives the same answer as making the original array non-increasing.

This trick avoids writing a separate DP.

---

# Summary Table

| Concept                  | Meaning                                                        |
| ------------------------ | -------------------------------------------------------------- | ------------------- | --- |
| Cost to change one value | `                                                              | nums[i] - target[i] | `   |
| Goal                     | transform array into monotone sequence with minimum total cost |
| Need to solve            | non-decreasing and non-increasing                              |
| Non-increasing trick     | solve non-decreasing on reversed array                         |
| Candidate targets        | sorted values of original array                                |
| DP state                 | `dp[j]` = min cost ending current position at `sorted[j]`      |
| Transition               | `newDp[j] = min(dp[0..j]) +                                    | nums[i] - sorted[j] | `   |
| Optimization             | maintain prefix minimum                                        |
| Time complexity          | `O(n^2)`                                                       |
| Space complexity         | `O(n)`                                                         |

---

# Final Takeaway

The main challenge is realizing that this is not a simulation problem and not a greedy problem.

It is a **dynamic programming over target values** problem.

The core structure is:

1. Convert the problem into choosing a monotone target array
2. Restrict target values to the sorted values of `nums`
3. Use DP to build the cheapest non-decreasing sequence
4. Reuse the same helper on the reversed array for the non-increasing case
5. Return the minimum of the two answers

That yields a clean and efficient solution.

---

# Full Code Reference

```java
import java.util.*;

class Solution {
    public int convertArray(int[] nums) {
        return Math.min(minCostNonDecreasing(nums), minCostNonDecreasing(reverse(nums)));
    }

    private int minCostNonDecreasing(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        long[] dp = new long[n];
        for (int j = 0; j < n; j++) {
            dp[j] = Math.abs((long) nums[0] - sorted[j]);
        }

        for (int i = 1; i < n; i++) {
            long[] next = new long[n];
            long prefixMin = Long.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                prefixMin = Math.min(prefixMin, dp[j]);
                next[j] = prefixMin + Math.abs((long) nums[i] - sorted[j]);
            }

            dp = next;
        }

        long ans = Long.MAX_VALUE;
        for (long val : dp) {
            ans = Math.min(ans, val);
        }
        return (int) ans;
    }

    private int[] reverse(int[] nums) {
        int n = nums.length;
        int[] rev = new int[n];
        for (int i = 0; i < n; i++) {
            rev[i] = nums[n - 1 - i];
        }
        return rev;
    }
}
```
