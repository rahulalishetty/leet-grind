# 410. Split Array Largest Sum — Exhaustive Solution Notes

## Overview

We are given an array of non-negative integers and an integer `k` (sometimes written as `m` in explanations).
We must split the array into exactly `k` **non-empty contiguous subarrays** such that the **largest subarray sum** is as small as possible.

This is a very important optimization problem because:

- we are not trying to minimize the total sum,
- we are not trying to minimize the number of subarrays,
- we are specifically trying to minimize the **maximum subarray sum among all chosen subarrays**.

At first glance, this looks like a dynamic programming problem, and it absolutely can be solved that way.
But there is also a more elegant and faster solution using **binary search on the answer**.

This write-up covers all three major approaches in detail:

1. **Top-Down Dynamic Programming**
2. **Bottom-Up Dynamic Programming**
3. **Binary Search on the Answer** — the optimal practical solution

---

## Problem Statement

Given an integer array `nums` and an integer `k`, split `nums` into `k` non-empty subarrays such that the **largest sum of any subarray is minimized**.

Return the minimized largest sum.

A subarray is a contiguous part of the array.

---

## Example 1

**Input**

```text
nums = [7,2,5,10,8]
k = 2
```

**Output**

```text
18
```

**Explanation**

There are four ways to split the array into two subarrays:

```text
[7] [2,5,10,8]      -> largest sum = 25
[7,2] [5,10,8]      -> largest sum = 23
[7,2,5] [10,8]      -> largest sum = 18   <- best
[7,2,5,10] [8]      -> largest sum = 24
```

The minimum possible largest subarray sum is:

```text
18
```

---

## Example 2

**Input**

```text
nums = [1,2,3,4,5]
k = 2
```

**Output**

```text
9
```

**Explanation**

Possible splits:

```text
[1] [2,3,4,5]       -> largest sum = 14
[1,2] [3,4,5]       -> largest sum = 12
[1,2,3] [4,5]       -> largest sum = 9    <- best
[1,2,3,4] [5]       -> largest sum = 10
```

So the answer is:

```text
9
```

---

## Constraints

- `1 <= nums.length <= 1000`
- `0 <= nums[i] <= 10^6`
- `1 <= k <= min(50, nums.length)`

---

# Why Brute Force Is Not Feasible

To split an array of length `n` into exactly `k` subarrays, we must choose `k - 1` split points among the `n - 1` possible boundaries.

So the number of ways is:

```text
C(n - 1, k - 1)
```

This can be enormous.

In the worst case:

```text
C(999, 49)
```

which is astronomically large.

So enumerating all splits is not practical.

---

# Key Observations

There are two useful ways to think about the problem.

## Observation 1: Dynamic Programming Structure

As we scan the array, at each step we decide:

- continue the current subarray, or
- start a new subarray

This decision affects future decisions, so the problem has overlapping subproblems and optimal substructure.

That makes it a candidate for dynamic programming.

---

## Observation 2: Binary Search on the Answer

We are minimizing a quantity:

```text
largest subarray sum
```

This quantity has a monotonic property:

- if a value `X` is large enough to allow splitting into `k` or fewer subarrays,
  then any value larger than `X` will also work
- if a value `X` is too small to allow such a split,
  then any value smaller than `X` will also fail

That monotonicity is exactly what binary search needs.

---

# Approach 1: Top-Down Dynamic Programming

## Intuition

Let us define the first subarray to be:

```text
nums[currIndex ... i]
```

for some choice of `i`.

Once we choose that first subarray, the rest of the problem becomes:

> Split the remaining suffix `nums[i+1 ... n-1]` into `subarrayCount - 1` subarrays so that the largest subarray sum is minimized.

So we can try every valid endpoint `i` for the current subarray and choose the split that minimizes the resulting maximum sum.

---

## State Definition

Let:

```text
F(currIndex, subarrayCount)
```

represent:

> the minimum possible largest subarray sum for the suffix `nums[currIndex ... n-1]` when we must split it into exactly `subarrayCount` non-empty subarrays.

This is the central DP state.

---

## Recurrence Relation

Suppose the first subarray ends at index `i`.

Then:

- the sum of the first subarray is:
  ```text
  sum(currIndex, i)
  ```
- the best possible largest subarray sum for the remaining suffix is:
  ```text
  F(i + 1, subarrayCount - 1)
  ```

If we split at `i`, then the largest subarray sum produced by this decision is:

```text
max(sum(currIndex, i), F(i + 1, subarrayCount - 1))
```

Since we want the best split, we minimize over all valid `i`:

```text
F(currIndex, subarrayCount) =
    min over i in [currIndex, n - subarrayCount] of
    max(sum(currIndex, i), F(i + 1, subarrayCount - 1))
```

---

## Why the Range Ends at `n - subarrayCount`

If we choose `i` too far to the right, then there will not be enough elements left to form the remaining subarrays.

Since we need exactly `subarrayCount - 1` more non-empty subarrays after the current one, we must leave at least that many elements.

So:

```text
i <= n - subarrayCount
```

This is a very important pruning constraint.

---

## Base Case

If:

```text
subarrayCount == 1
```

then we have no choice left.

All remaining elements must go into one final subarray.

So:

```text
F(currIndex, 1) = sum(currIndex, n - 1)
```

---

## Prefix Sum Optimization

We repeatedly need range sums of the form:

```text
sum(currIndex, i)
```

To compute these in constant time, we build a prefix sum array:

```text
prefixSum[x] = sum of nums[0 ... x-1]
```

Then:

```text
sum(currIndex, i) = prefixSum[i + 1] - prefixSum[currIndex]
```

---

## Memoization

The recursive formulation has repeated subproblems.

So we memoize results for each pair:

```text
(currIndex, subarrayCount)
```

That avoids recomputation.

---

## Pruning Optimization

As we extend the first subarray from `currIndex` to `i`, its sum only increases because all numbers are non-negative.

If at some point:

```text
firstSplitSum >= currentBestAnswer
```

then continuing further cannot improve the answer.

Why?

Because:

- `firstSplitSum` will only grow,
- the `max(firstSplitSum, ...)` will not become smaller.

So we can break early.

That is a nice optimization.

---

## Algorithm

1. Build a prefix sum array.
2. Define recursive function:
   ```text
   getMinimumLargestSplitSum(currIndex, subarrayCount)
   ```
3. If result is memoized, return it.
4. If `subarrayCount == 1`, return the sum of the remaining suffix.
5. Otherwise:
   - try every possible split endpoint `i`
   - compute the first subarray sum
   - recursively compute the optimal result for the remainder
   - minimize the resulting largest subarray sum
6. Memoize and return.

---

## Java Implementation — Top-Down DP

```java
class Solution {
    // Defined as per maximum constraints
    Integer[][] memo = new Integer[1001][51];

    private int getMinimumLargestSplitSum(int[] prefixSum, int currIndex, int subarrayCount) {
        int n = prefixSum.length - 1;

        if (memo[currIndex][subarrayCount] != null) {
            return memo[currIndex][subarrayCount];
        }

        // Base case: only one subarray left
        if (subarrayCount == 1) {
            return memo[currIndex][subarrayCount] = prefixSum[n] - prefixSum[currIndex];
        }

        int minimumLargestSplitSum = Integer.MAX_VALUE;

        for (int i = currIndex; i <= n - subarrayCount; i++) {
            int firstSplitSum = prefixSum[i + 1] - prefixSum[currIndex];

            int largestSplitSum = Math.max(
                firstSplitSum,
                getMinimumLargestSplitSum(prefixSum, i + 1, subarrayCount - 1)
            );

            minimumLargestSplitSum = Math.min(minimumLargestSplitSum, largestSplitSum);

            if (firstSplitSum >= minimumLargestSplitSum) {
                break;
            }
        }

        return memo[currIndex][subarrayCount] = minimumLargestSplitSum;
    }

    public int splitArray(int[] nums, int m) {
        int n = nums.length;
        int[] prefixSum = new int[n + 1];

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        return getMinimumLargestSplitSum(prefixSum, 0, m);
    }
}
```

---

## Complexity Analysis — Top-Down DP

Let:

- `N` = length of the array
- `M` = number of subarrays

### Time Complexity

There are:

```text
O(N × M)
```

possible states.

For each state, we may try up to `O(N)` split points.

So the total time complexity is:

```text
O(N^2 × M)
```

---

### Space Complexity

Memo table stores:

```text
O(N × M)
```

states.

Recursion depth is at most `M`.

So the total space complexity is:

```text
O(N × M)
```

---

# Approach 2: Bottom-Up Dynamic Programming

## Intuition

The top-down recurrence is correct, but recursion adds stack overhead.

We can fill the same DP table iteratively.

The state definition remains the same:

```text
memo[currIndex][subarrayCount]
```

meaning:

> the minimum possible largest subarray sum for suffix `nums[currIndex ... n-1]` with `subarrayCount` subarrays.

The difference is only in how we compute it.

---

## Base Case

For all `currIndex`:

```text
memo[currIndex][1] = sum(currIndex, n - 1)
```

because if only one subarray remains, it must contain all remaining elements.

---

## Transition

For larger `subarrayCount`, use the same recurrence:

```text
memo[currIndex][subarrayCount] =
    min over i in [currIndex, n - subarrayCount] of
    max(sum(currIndex, i), memo[i + 1][subarrayCount - 1])
```

---

## Fill Order

We build results in increasing order of `subarrayCount`:

- first compute all states for `subarrayCount = 1`
- then `2`
- then `3`
- ...
- up to `m`

For each `subarrayCount`, compute all valid `currIndex`.

That ensures every required smaller subproblem is already known.

---

## Algorithm

1. Build prefix sum array.
2. Initialize DP table.
3. For each `subarrayCount` from `1` to `m`:
   - for each `currIndex` from `0` to `n - 1`:
     - if `subarrayCount == 1`, fill base case
     - otherwise use recurrence to compute the state
4. Return `memo[0][m]`.

---

## Java Implementation — Bottom-Up DP

```java
class Solution {
    int[][] memo = new int[1001][51];

    public int splitArray(int[] nums, int m) {
        int n = nums.length;

        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        for (int subarrayCount = 1; subarrayCount <= m; subarrayCount++) {
            for (int currIndex = 0; currIndex < n; currIndex++) {

                if (subarrayCount == 1) {
                    memo[currIndex][subarrayCount] = prefixSum[n] - prefixSum[currIndex];
                    continue;
                }

                int minimumLargestSplitSum = Integer.MAX_VALUE;

                for (int i = currIndex; i <= n - subarrayCount; i++) {
                    int firstSplitSum = prefixSum[i + 1] - prefixSum[currIndex];

                    int largestSplitSum = Math.max(
                        firstSplitSum,
                        memo[i + 1][subarrayCount - 1]
                    );

                    minimumLargestSplitSum = Math.min(minimumLargestSplitSum, largestSplitSum);

                    if (firstSplitSum >= minimumLargestSplitSum) {
                        break;
                    }
                }

                memo[currIndex][subarrayCount] = minimumLargestSplitSum;
            }
        }

        return memo[0][m];
    }
}
```

---

## Complexity Analysis — Bottom-Up DP

Let:

- `N` = array length
- `M` = number of subarrays

### Time Complexity

There are:

```text
O(N × M)
```

states.

Each state tries up to `O(N)` split points.

So total time is:

```text
O(N^2 × M)
```

---

### Space Complexity

The memo table size is:

```text
O(N × M)
```

So total space complexity is:

```text
O(N × M)
```

---

# Approach 3: Binary Search

## Intuition

This is the most elegant solution.

Instead of directly constructing the optimal split, we guess a value:

```text
X = candidate for the largest allowed subarray sum
```

Then we ask:

> Can the array be split into `k` or fewer subarrays such that every subarray sum is at most `X`?

If yes, then `X` is feasible.

If no, then `X` is too small.

Now the key insight:

- if `X` is feasible, then any value larger than `X` is also feasible
- if `X` is infeasible, then any value smaller than `X` is also infeasible

That monotonic behavior allows binary search.

---

## Lower and Upper Bound of the Answer

The answer must lie between:

### Lower bound

The answer cannot be smaller than the largest element in the array.

Why?

Because every element must belong to some subarray, so that subarray’s sum must be at least that element.

So:

```text
left = max(nums)
```

---

### Upper bound

The answer cannot be larger than the total sum of the array.

Why?

Because putting the whole array in one subarray gives sum:

```text
sum(nums)
```

So:

```text
right = sum(nums)
```

---

## Feasibility Check

For a guessed value `maxSumAllowed`, we greedily compute the minimum number of subarrays needed so that no subarray sum exceeds `maxSumAllowed`.

### Greedy Strategy

Scan left to right:

- keep adding elements to the current subarray while the sum stays `<= maxSumAllowed`
- if adding the next element would exceed the limit:
  - start a new subarray
  - increment split count

This greedy strategy gives the **minimum** number of subarrays needed for that limit.

---

## Why the Greedy Check Works

To minimize the number of subarrays, we should pack as many elements as possible into the current subarray before starting a new one.

Because all numbers are non-negative, delaying a split never hurts feasibility for the current limit.

So the greedy scan correctly computes the minimum number of required subarrays.

---

## Binary Search Logic

Let:

```text
required = minimumSubarraysRequired(nums, maxSumAllowed)
```

Then:

- if `required <= k`, the guess is feasible
  - try smaller values
- else, the guess is too small
  - try larger values

Eventually binary search finds the smallest feasible value.

That is exactly the answer.

---

## Algorithm

1. Compute:
   - `sum` = total array sum
   - `maxElement` = largest element
2. Set:
   ```text
   left = maxElement
   right = sum
   ```
3. While `left <= right`:
   - compute `mid`
   - compute how many subarrays are required if each subarray sum must be `<= mid`
   - if required subarrays `<= k`, record `mid` and move left
   - otherwise move right
4. Return the smallest feasible value found.

---

## Java Implementation — Binary Search

```java
class Solution {
    private int minimumSubarraysRequired(int[] nums, int maxSumAllowed) {
        int currentSum = 0;
        int splitsRequired = 0;

        for (int element : nums) {
            if (currentSum + element <= maxSumAllowed) {
                currentSum += element;
            } else {
                currentSum = element;
                splitsRequired++;
            }
        }

        return splitsRequired + 1;
    }

    public int splitArray(int[] nums, int m) {
        int sum = 0;
        int maxElement = Integer.MIN_VALUE;

        for (int element : nums) {
            sum += element;
            maxElement = Math.max(maxElement, element);
        }

        int left = maxElement;
        int right = sum;
        int minimumLargestSplitSum = 0;

        while (left <= right) {
            int maxSumAllowed = left + (right - left) / 2;

            if (minimumSubarraysRequired(nums, maxSumAllowed) <= m) {
                minimumLargestSplitSum = maxSumAllowed;
                right = maxSumAllowed - 1;
            } else {
                left = maxSumAllowed + 1;
            }
        }

        return minimumLargestSplitSum;
    }
}
```

---

# Walkthrough of Binary Search on Example 1

Input:

```text
nums = [7,2,5,10,8]
k = 2
```

### Step 1: Bounds

```text
maxElement = 10
sum = 32
```

So search range is:

```text
[10, 32]
```

---

### Step 2: Try mid = 21

Can we split into at most 2 subarrays with max sum 21?

Greedy packing:

- `[7,2,5]` sum = 14
- next `10` would make 24, so split
- `[10,8]` sum = 18

Total subarrays = 2

Feasible.

So try smaller values.

---

### Step 3: Try mid = 15

Greedy packing:

- `[7,2,5]` sum = 14
- next `10` would exceed 15, split
- `[10]`
- next `8` would exceed 15, split
- `[8]`

Total subarrays = 3

Not feasible.

So we need a larger value.

---

### Step 4: Continue

Binary search eventually narrows to:

```text
18
```

That is the smallest feasible largest subarray sum.

---

# Why Binary Search Is Better Than DP Here

The DP solutions are correct but cost:

```text
O(N^2 × M)
```

With constraints:

- `N <= 1000`
- `M <= 50`

this may still pass, but it is significantly heavier.

The binary search solution takes:

```text
O(N × log(S))
```

where `S` is the total sum of the array.

That is much better in practice.

It also uses constant extra space.

---

# Complexity Analysis — Binary Search

Let:

- `N` = array length
- `S` = total sum of the array

### Time Complexity

Binary search runs for:

```text
O(log S)
```

iterations.

Each iteration performs one linear greedy scan of the array:

```text
O(N)
```

So total time complexity is:

```text
O(N × log S)
```

---

### Space Complexity

Only a few variables are used.

So total space complexity is:

```text
O(1)
```

---

# Comparing the Three Approaches

## Top-Down DP

### Pros

- natural recursive formulation
- easy to derive mathematically

### Cons

- slower
- uses recursion stack
- memo table required

### Complexity

- Time: `O(N^2 × M)`
- Space: `O(N × M)`

---

## Bottom-Up DP

### Pros

- avoids recursion
- iterative and explicit

### Cons

- still expensive
- still needs `O(N × M)` memory

### Complexity

- Time: `O(N^2 × M)`
- Space: `O(N × M)`

---

## Binary Search

### Pros

- fastest practical solution
- elegant monotonic reasoning
- constant extra space

### Cons

- requires recognizing binary search on answer
- less obvious at first glance

### Complexity

- Time: `O(N × log S)`
- Space: `O(1)`

---

# Common Mistakes

## 1. Confusing "exactly `k`" with "at most `k`"

The original problem requires exactly `k` subarrays.

In the binary search check, we ask whether we can split into **`k` or fewer** subarrays under a given limit.

Why is that okay?

Because if we can split into fewer than `k`, we can always split some subarray further, since all numbers are non-negative, without increasing the maximum allowed sum.

So feasibility with `<= k` is enough.

---

## 2. Choosing Wrong Binary Search Bounds

The correct bounds are:

```text
left = max(nums)
right = sum(nums)
```

Not:

- `0`
- arbitrary large number

Those wrong bounds either waste time or break correctness.

---

## 3. Using Greedy Incorrectly

During feasibility check, you must greedily keep adding elements until the next one would exceed the limit.

That produces the minimum number of subarrays required for that limit.

---

## 4. Forgetting Prefix Sum in DP

Without prefix sum, repeated range sum computation makes DP slower.

Prefix sums allow:

```text
sum(l, r) = prefix[r + 1] - prefix[l]
```

in constant time.

---

# Interview Perspective

This is a very strong interview problem because it tests two different instincts:

1. Can you formulate it as dynamic programming?
2. Can you step back and see the monotonic property that allows binary search?

Many candidates stop at DP.

A stronger solution notices:

> We are minimizing a value, and feasibility is monotonic.

That often signals binary search on the answer.

That is the real key insight.

---

# Final Summary

## Problem Goal

Split the array into exactly `k` contiguous non-empty subarrays such that the largest subarray sum is minimized.

---

## DP State

For DP, define:

```text
F(currIndex, subarrayCount)
```

as the minimum possible largest subarray sum for suffix `nums[currIndex ... n-1]` using exactly `subarrayCount` subarrays.

---

## DP Recurrence

```text
F(currIndex, subarrayCount) =
    min over i in [currIndex, n - subarrayCount] of
    max(sum(currIndex, i), F(i + 1, subarrayCount - 1))
```

Base case:

```text
F(currIndex, 1) = sum(currIndex, n - 1)
```

---

## Binary Search Insight

Guess a value `X` and check whether the array can be split into at most `k` subarrays where every subarray sum is `<= X`.

This feasibility is monotonic, so binary search works.

---

## Best Practical Complexity

Binary search solution:

- Time: `O(N × log S)`
- Space: `O(1)`

where `S` is the total sum of the array.

---

# Best Final Java Solution

```java
class Solution {
    private int minimumSubarraysRequired(int[] nums, int maxSumAllowed) {
        int currentSum = 0;
        int splitsRequired = 0;

        for (int element : nums) {
            if (currentSum + element <= maxSumAllowed) {
                currentSum += element;
            } else {
                currentSum = element;
                splitsRequired++;
            }
        }

        return splitsRequired + 1;
    }

    public int splitArray(int[] nums, int m) {
        int sum = 0;
        int maxElement = Integer.MIN_VALUE;

        for (int element : nums) {
            sum += element;
            maxElement = Math.max(maxElement, element);
        }

        int left = maxElement;
        int right = sum;
        int answer = right;

        while (left <= right) {
            int maxSumAllowed = left + (right - left) / 2;

            if (minimumSubarraysRequired(nums, maxSumAllowed) <= m) {
                answer = maxSumAllowed;
                right = maxSumAllowed - 1;
            } else {
                left = maxSumAllowed + 1;
            }
        }

        return answer;
    }
}
```

This is the standard optimal solution for the problem.
