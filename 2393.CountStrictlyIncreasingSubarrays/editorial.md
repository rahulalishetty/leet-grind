# Count Strictly Increasing Subarrays — Exhaustive Summary

## Problem Overview

We are given an array `nums` of `N` positive integers.

We need to return the number of **subarrays** that are **strictly increasing**.

A subarray is strictly increasing if every next element is greater than the previous one.

For example, if:

```text
nums = [1, 3, 5]
```

then the strictly increasing subarrays are:

```text
[1]
[3]
[5]
[1, 3]
[3, 5]
[1, 3, 5]
```

So the answer is `6`.

---

## Naive Thinking

A direct approach would be:

1. Generate every possible subarray
2. Check whether each subarray is strictly increasing

There are `O(N^2)` subarrays, and checking one subarray may take `O(N)` in the worst case.

So the total complexity becomes:

```text
O(N^3)
```

That is too slow.

So we need to find a smarter way.

---

# Approach 1: Dynamic Programming

## Intuition

Let us think about the subarrays that **end at index `x`**.

Suppose we already know how many strictly increasing subarrays end at index `x - 1`, or more specifically, we know the length of the current increasing streak ending at `x - 1`.

Now consider `nums[x]`.

### Case 1: `nums[x] > nums[x - 1]`

Then the increasing streak continues.

That means every increasing subarray ending at `x - 1` can be extended by `nums[x]`.

Also, the single-element subarray `[nums[x]]` is always valid.

So if the previous increasing streak length was `k`, the new streak length becomes:

```text
k + 1
```

and this also tells us the number of increasing subarrays ending at index `x`.

### Case 2: `nums[x] <= nums[x - 1]`

Then the increasing streak breaks.

Any previous subarray ending at `x - 1` can no longer be extended.

So the only strictly increasing subarray ending at `x` is:

```text
[nums[x]]
```

Thus the new streak length becomes:

```text
1
```

---

## Core DP Idea

Maintain:

- `currSubarray` = length of the current strictly increasing streak ending at the current index
- `subarrayCount` = total number of strictly increasing subarrays found so far

At every index:

- if the sequence is still increasing, increment `currSubarray`
- otherwise reset it to `1`
- add `currSubarray` to the final answer

Why do we add `currSubarray`?

Because if the increasing streak ending at index `i` has length `L`, then there are exactly `L` strictly increasing subarrays ending at `i`:

- the last 1 element
- the last 2 elements
- the last 3 elements
- ...
- the last `L` elements

---

## Algorithm

1. Initialize:
   - `currSubarray = 1`
   - `subarrayCount = 1`
2. Start iterating from index `1`
3. For each index `i`:
   - if `nums[i] > nums[i - 1]`, increment `currSubarray`
   - otherwise, reset `currSubarray = 1`
   - add `currSubarray` to `subarrayCount`
4. Return `subarrayCount`

---

## Code — Dynamic Programming

```java
class Solution {
    public long countSubarrays(int[] nums) {
        long currSubarray = 1;
        long subarrayCount = 1;

        for (int i = 1; i < nums.length; i++) {
            // If the current element is greater, increase the subarrays.
            if (nums[i] > nums[i - 1]) {
                currSubarray++;
            } else {
                // Otherwise, reset the subarray size to 1.
                currSubarray = 1;
            }
            // Add the number of subarrays to the total count.
            subarrayCount += currSubarray;
        }

        return subarrayCount;
    }
}
```

---

## Step-by-Step Example

Consider:

```text
nums = [1, 2, 4, 3]
```

Initialize:

```text
currSubarray = 1
subarrayCount = 1
```

### Index 1

```text
nums[1] = 2 > nums[0] = 1
```

So:

```text
currSubarray = 2
subarrayCount = 1 + 2 = 3
```

Subarrays ending at index 1:

```text
[2]
[1, 2]
```

### Index 2

```text
nums[2] = 4 > nums[1] = 2
```

So:

```text
currSubarray = 3
subarrayCount = 3 + 3 = 6
```

Subarrays ending at index 2:

```text
[4]
[2, 4]
[1, 2, 4]
```

### Index 3

```text
nums[3] = 3 <= nums[2] = 4
```

Increasing streak breaks:

```text
currSubarray = 1
subarrayCount = 6 + 1 = 7
```

Subarrays ending at index 3:

```text
[3]
```

Final answer:

```text
7
```

---

## Why This Works

At every index, `currSubarray` tells us exactly how many strictly increasing subarrays end there.

That is enough, because every strictly increasing subarray has exactly one ending position.

So by summing this value for every index, we count every valid subarray exactly once.

This is a compact dynamic programming idea because:

- current answer depends only on the previous index
- we carry forward the current increasing streak length

---

## Complexity Analysis — Dynamic Programming

Let `N` be the number of elements in `nums`.

### Time Complexity: `O(N)`

We scan the array exactly once.

### Space Complexity: `O(1)`

We use only:

- `currSubarray`
- `subarrayCount`

So extra space is constant.

---

# Approach 2: Greedy

## Intuition

If we look carefully at the previous approach, we notice a pattern.

Suppose we have one maximal strictly increasing contiguous segment of length `K`.

For example:

```text
[2, 5, 8, 10]
```

Here `K = 4`.

How many strictly increasing subarrays come from this segment?

They are:

- length 1 subarrays: `4`
- length 2 subarrays: `3`
- length 3 subarrays: `2`
- length 4 subarrays: `1`

Total:

```text
4 + 3 + 2 + 1 = 10
```

This is equal to:

```text
K * (K + 1) / 2
```

So instead of adding streak length one by one at each index, we can:

1. find each maximal strictly increasing run
2. compute its contribution directly using the formula

---

## Why the formula works

For a run of length `K`:

- there are `K` choices of starting point for length 1 subarrays
- `K - 1` choices for length 2
- ...
- `1` choice for length `K`

So total count is:

```text
K + (K - 1) + (K - 2) + ... + 1
```

which equals:

```text
K * (K + 1) / 2
```

---

## Algorithm

1. Initialize `subarrayCount = 0`
2. Iterate through the array
3. At each starting point:
   - initialize `currSubarray = 1`
   - keep extending while `nums[i] < nums[i + 1]`
   - this gives the length of the current maximal increasing run
4. Add:

```text
(currSubarray * (currSubarray + 1)) / 2
```

to the answer 5. Continue until the array ends 6. Return the answer

---

## Code — Greedy

```java
class Solution {
    public long countSubarrays(int[] nums) {
        // Variable to store the total number of subarrays.
        long subarrayCount = 0;

        for (int i = 0; i < nums.length; i++) {
            // Length of the current subarray.
            long currSubarray = 1;

            while (i + 1 < nums.length && nums[i] < nums[i + 1]) {
                currSubarray++;
                i++;
            }

            // Add the total number of different subarrays possible from this length.
            subarrayCount += (currSubarray * (currSubarray + 1)) / 2;
        }

        return subarrayCount;
    }
}
```

---

## Step-by-Step Example

Consider:

```text
nums = [1, 2, 5, 4, 6]
```

### First increasing run

Starting at index `0`:

```text
1 < 2 < 5
```

So the run is:

```text
[1, 2, 5]
```

Length:

```text
K = 3
```

Contribution:

```text
3 * 4 / 2 = 6
```

These are:

```text
[1]
[2]
[5]
[1,2]
[2,5]
[1,2,5]
```

### Second increasing run

Then we move to:

```text
[4, 6]
```

Length:

```text
K = 2
```

Contribution:

```text
2 * 3 / 2 = 3
```

These are:

```text
[4]
[6]
[4,6]
```

Total:

```text
6 + 3 = 9
```

---

## Why This Works

Every strictly increasing subarray belongs entirely to exactly one maximal strictly increasing run.

So if we split the array into maximal increasing runs and count the number of subarrays contributed by each run, we count every strictly increasing subarray exactly once.

That is why the greedy grouping works.

---

## Complexity Analysis — Greedy

Let `N` be the number of elements.

### Time Complexity: `O(N)`

Even though there is a nested `while` loop, each element is processed only once overall because index `i` only moves forward.

So total work is linear.

### Space Complexity: `O(1)`

We use only constant extra variables.

---

# Comparison of the Two Approaches

## Dynamic Programming View

This approach is more local.

At each index, it asks:

> How many strictly increasing subarrays end here?

This is elegant and incremental.

---

## Greedy View

This approach is more segment-based.

It asks:

> What are the maximal strictly increasing runs, and how much does each run contribute?

This is mathematically compact.

---

## Relationship Between Them

These are not fundamentally different solutions.

They are two different ways of viewing the same structure.

In the DP approach, if a run has length `K`, we add:

```text
1 + 2 + 3 + ... + K
```

over time.

In the greedy approach, we compute that sum directly as:

```text
K * (K + 1) / 2
```

So both methods are counting the same thing, just from different angles.

---

# Common Pitfalls

## 1. Confusing strictly increasing with non-decreasing

The condition must be:

```text
nums[i] > nums[i - 1]
```

not:

```text
nums[i] >= nums[i - 1]
```

Equal elements break the streak.

---

## 2. Using `int` instead of `long`

The number of subarrays can be large.

For example, if the whole array is strictly increasing and length is `N`, the answer is:

```text
N * (N + 1) / 2
```

This can exceed 32-bit integer range.

So `long` is safer.

---

## 3. Forgetting single-element subarrays

Every individual element forms a strictly increasing subarray of length `1`.

So the answer is never `0` for a non-empty array.

---

# Final Takeaway

The heart of the problem is understanding that strictly increasing subarrays are built from contiguous increasing runs.

You can count them in two clean ways:

### Dynamic Programming

Track the length of the increasing streak ending at each index, and add that to the answer.

### Greedy

Find each maximal increasing run and add:

```text
K * (K + 1) / 2
```

for a run of length `K`.

Both approaches run in:

- **Time:** `O(N)`
- **Space:** `O(1)`

---

# Summary Table

| Approach            | Main Idea                                         |   Time |  Space |
| ------------------- | ------------------------------------------------- | -----: | -----: |
| Dynamic Programming | count increasing subarrays ending at each index   | `O(N)` | `O(1)` |
| Greedy              | count contribution of each maximal increasing run | `O(N)` | `O(1)` |

---

# Full Code Reference

## Approach 1 — Dynamic Programming

```java
class Solution {
    public long countSubarrays(int[] nums) {
        long currSubarray = 1;
        long subarrayCount = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > nums[i - 1]) {
                currSubarray++;
            } else {
                currSubarray = 1;
            }
            subarrayCount += currSubarray;
        }

        return subarrayCount;
    }
}
```

## Approach 2 — Greedy

```java
class Solution {
    public long countSubarrays(int[] nums) {
        long subarrayCount = 0;

        for (int i = 0; i < nums.length; i++) {
            long currSubarray = 1;

            while (i + 1 < nums.length && nums[i] < nums[i + 1]) {
                currSubarray++;
                i++;
            }

            subarrayCount += (currSubarray * (currSubarray + 1)) / 2;
        }

        return subarrayCount;
    }
}
```
