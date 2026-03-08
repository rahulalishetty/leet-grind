# Maximum Alternating Subarray Sum — Detailed Explanation

## Problem Statement

You are given a `0-indexed` integer array `nums`.

A **subarray** is a contiguous non-empty sequence of elements of the array.

For a subarray from index `i` to `j`, its **alternating subarray sum** is:

```text
nums[i] - nums[i+1] + nums[i+2] - nums[i+3] + ...
```

That is, the signs alternate starting with a `+` at the first element of the chosen subarray.

Your task is to return the **maximum alternating subarray sum** among **all possible subarrays**.

---

# Example of Alternating Sum

Suppose:

```text
nums = [4, 2, 5, 3]
```

Then:

- subarray `[4]` has alternating sum:

```text
4
```

- subarray `[4, 2]` has alternating sum:

```text
4 - 2 = 2
```

- subarray `[4, 2, 5]` has alternating sum:

```text
4 - 2 + 5 = 7
```

- subarray `[2, 5, 3]` has alternating sum:

```text
2 - 5 + 3 = 0
```

We want the maximum value over all such subarrays.

---

# Key Insight

This problem looks like a subarray optimization problem, so it is natural to ask:

> Can we do something similar to Kadane’s algorithm?

Yes.

The main difference is that the sign of each element is not fixed globally.
It depends on the position **inside the chosen subarray**.

That means the same element may contribute with:

- `+` if it is at an even offset from the subarray start
- `-` if it is at an odd offset from the subarray start

So when processing subarrays ending at a position `i`, we need to know:

- what is the best alternating sum where `nums[i]` is used with a `+`
- what is the best alternating sum where `nums[i]` is used with a `-`

That leads to a very compact DP.

---

# DP State Definition

For subarrays that **end at index `i`**, define:

## `plus`

The maximum alternating sum of any subarray ending at `i` such that:

```text
nums[i] is taken with a '+' sign
```

## `minus`

The maximum alternating sum of any subarray ending at `i` such that:

```text
nums[i] is taken with a '-' sign
```

These two states are enough because alternating signs completely determine how a valid extension works.

---

# Transition Logic

Suppose we are processing `nums[i]`.

## Case 1: `nums[i]` is taken with `+`

Then there are only two possibilities:

### Option A: Start a new subarray at `i`

If the subarray consists of only `nums[i]`, then:

```text
value = nums[i]
```

because the first element of every subarray always has `+`.

### Option B: Extend a previous subarray ending at `i-1` where the last element had `-`

If the previous subarray ended with a `-`, then appending `nums[i]` makes it the next `+`.

So:

```text
value = previousMinus + nums[i]
```

Therefore:

```text
newPlus = max(nums[i], previousMinus + nums[i])
```

---

## Case 2: `nums[i]` is taken with `-`

Then this element cannot start a new subarray, because every subarray must begin with `+`.

So the only valid way is to extend a previous subarray ending at `i-1` where the last sign was `+`.

Thus:

```text
newMinus = previousPlus - nums[i]
```

---

# Final Recurrence

At each index:

```text
newPlus  = max(nums[i], minus + nums[i])
newMinus = plus - nums[i]
```

Then update:

```text
plus = newPlus
minus = newMinus
```

The answer is the maximum value ever seen among these states.

---

# Why This Works

An alternating subarray always has sign pattern:

```text
+ - + - + - ...
```

So if the current element has:

- `+`, it either starts the subarray or follows a `-`
- `-`, it must follow a `+`

There are no other possibilities.

That means the DP captures all valid alternating subarrays ending at each position.

---

# Initialization

At index `0`:

- a subarray `[nums[0]]` is valid
- its value is simply `nums[0]`

So initialize:

```text
plus = nums[0]
```

What about `minus`?

There is no valid subarray of length 1 ending at index `0` where `nums[0]` has `-`.

So initialize it to a very small number:

```text
minus = -infinity
```

In code, use something like:

```java
Long.MIN_VALUE / 4
```

to avoid overflow when adding later.

Initial answer:

```text
ans = nums[0]
```

---

# Step-by-Step Dry Run

Consider:

```text
nums = [3, -1, 4, 2]
```

We will track:

- `plus`
- `minus`
- `ans`

---

## Start: `i = 0`, value = `3`

Only one valid subarray:

```text
[3]
```

So:

```text
plus = 3
minus = -inf
ans = 3
```

---

## `i = 1`, value = `-1`

### Compute `newPlus`

```text
newPlus = max(-1, minus + (-1))
        = max(-1, -inf)
        = -1
```

This corresponds to starting a new subarray:

```text
[-1]
```

### Compute `newMinus`

```text
newMinus = plus - (-1)
         = 3 + 1
         = 4
```

This corresponds to extending `[3]` into:

```text
[3, -1]
```

whose alternating sum is:

```text
3 - (-1) = 4
```

Update:

```text
plus = -1
minus = 4
ans = 4
```

---

## `i = 2`, value = `4`

### Compute `newPlus`

```text
newPlus = max(4, minus + 4)
        = max(4, 4 + 4)
        = 8
```

This corresponds to extending `[3, -1]` into:

```text
[3, -1, 4]
```

Alternating sum:

```text
3 - (-1) + 4 = 8
```

### Compute `newMinus`

```text
newMinus = plus - 4
         = -1 - 4
         = -5
```

That corresponds to extending `[-1]` into:

```text
[-1, 4]
```

Alternating sum:

```text
-1 - 4 = -5
```

Update:

```text
plus = 8
minus = -5
ans = 8
```

---

## `i = 3`, value = `2`

### Compute `newPlus`

```text
newPlus = max(2, minus + 2)
        = max(2, -5 + 2)
        = 2
```

### Compute `newMinus`

```text
newMinus = plus - 2
         = 8 - 2
         = 6
```

Update:

```text
plus = 2
minus = 6
ans = 8
```

Final answer:

```text
8
```

The best subarray is:

```text
[3, -1, 4]
```

---

# Java Solution

```java
class Solution {
    public long maximumAlternatingSubarraySum(int[] nums) {
        long plus = nums[0];
        long minus = Long.MIN_VALUE / 4; // invalid initially
        long ans = nums[0];

        for (int i = 1; i < nums.length; i++) {
            long newPlus = Math.max(nums[i], minus + nums[i]);
            long newMinus = plus - nums[i];

            plus = newPlus;
            minus = newMinus;

            ans = Math.max(ans, Math.max(plus, minus));
        }

        return ans;
    }
}
```

---

# Equivalent Version with Explicit Temporary Variables

Sometimes it is clearer to write it this way:

```java
class Solution {
    public long maximumAlternatingSubarraySum(int[] nums) {
        long plus = nums[0];
        long minus = Long.MIN_VALUE / 4;
        long ans = nums[0];

        for (int i = 1; i < nums.length; i++) {
            long prevPlus = plus;
            long prevMinus = minus;

            plus = Math.max(nums[i], prevMinus + nums[i]);
            minus = prevPlus - nums[i];

            ans = Math.max(ans, Math.max(plus, minus));
        }

        return ans;
    }
}
```

This makes the dependency on the **previous** states a bit more obvious.

---

# Why `minus` Can Be Part of the Answer

At first glance, it may seem strange that we consider both:

```text
plus and minus
```

for the answer.

But remember:

- a subarray can end at any position
- its last element may be taken with either `+` or `-`, depending on the subarray length

Examples:

- odd-length subarray ends with `+`
- even-length subarray ends with `-`

So both states represent valid completed subarrays and both should be considered.

---

# Time Complexity

We process each element once.

Each step performs only constant work.

So the time complexity is:

```text
O(n)
```

---

# Space Complexity

We only keep two DP states:

- `plus`
- `minus`

So the space complexity is:

```text
O(1)
```

---

# Comparison with Brute Force

A brute-force solution would:

1. enumerate all subarrays
2. compute each alternating sum

That takes:

- `O(n^2)` subarrays
- up to `O(n)` work each if recomputed directly

Worst case:

```text
O(n^3)
```

Even with prefix tricks, this is still much worse than the DP approach.

The DP solution is linear and much better.

---

# Common Mistakes

## 1. Forgetting that the sign pattern restarts for every subarray

The signs are not based on the original array indices.
They are based on the position **inside the chosen subarray**.

So you cannot simply preassign global `+ - + -` signs to the array.

---

## 2. Initializing `minus = 0`

That is wrong.

A length-1 subarray cannot end with `-`.

So `minus` must start as an invalid very negative value.

---

## 3. Updating `plus` before using its old value for `minus`

This is a common bug.

Since:

```text
newMinus = oldPlus - nums[i]
```

you must either:

- store old values in temporaries
- or compute both new states before overwriting

---

## 4. Using `int` instead of `long`

Alternating sums can exceed `int` range depending on constraints.
Using `long` is safer and standard here.

---

# Intuition Summary

This is a Kadane-style DP with two states.

At every index:

- `plus` = best alternating subarray ending here if current sign is `+`
- `minus` = best alternating subarray ending here if current sign is `-`

Transitions:

```text
plus  = max(nums[i], previousMinus + nums[i])
minus = previousPlus - nums[i]
```

Track the best value over all states.

That gives the maximum alternating subarray sum in:

- `O(n)` time
- `O(1)` space

---

# Final Recommended Solution

```java
class Solution {
    public long maximumAlternatingSubarraySum(int[] nums) {
        long plus = nums[0];
        long minus = Long.MIN_VALUE / 4;
        long ans = nums[0];

        for (int i = 1; i < nums.length; i++) {
            long newPlus = Math.max(nums[i], minus + nums[i]);
            long newMinus = plus - nums[i];

            plus = newPlus;
            minus = newMinus;

            ans = Math.max(ans, Math.max(plus, minus));
        }

        return ans;
    }
}
```
