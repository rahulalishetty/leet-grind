# 689. Maximum Sum of 3 Non-Overlapping Subarrays — Exhaustive Solution Notes

## Overview

We are given:

- an integer array `nums`
- an integer `k`

We must choose **three non-overlapping subarrays**, each of length `k`, such that their **total sum is maximized**.

We must return the **starting indices** of those three subarrays.

If multiple optimal answers exist, we return the **lexicographically smallest** one.

This problem is interesting because:

- a greedy approach is tempting
- but local choices can block better global choices
- the non-overlap condition makes the problem combinational
- tie-breaking by lexicographically smallest indices matters

This write-up explains four approaches in detail:

1. **Memoization**
2. **Tabulation**
3. **Three Pointers**
4. **Sliding Window**

In practice, the most standard linear-time solution is the **Three Pointers** approach, while the **Sliding Window** approach is the most space-efficient specialized solution for exactly 3 subarrays.

---

## Problem Statement

Given an integer array `nums` and an integer `k`, find three non-overlapping subarrays of length `k` with maximum total sum.

Return a list of the three starting indices.

If there are multiple answers, return the lexicographically smallest one.

---

## Example 1

**Input**

```text
nums = [1,2,1,2,6,7,5,1]
k = 2
```

**Output**

```text
[0,3,5]
```

**Explanation**

The chosen subarrays are:

- `nums[0..1] = [1,2]` → sum = 3
- `nums[3..4] = [2,6]` → sum = 8
- `nums[5..6] = [7,5]` → sum = 12

Total:

```text
3 + 8 + 12 = 23
```

Another valid choice is:

```text
[1,3,5]
```

but:

```text
[0,3,5]
```

is lexicographically smaller, so it is returned.

---

## Example 2

**Input**

```text
nums = [1,2,1,2,1,2,1,2,1]
k = 2
```

**Output**

```text
[0,2,4]
```

---

## Constraints

- `1 <= nums.length <= 2 * 10^4`
- `1 <= nums[i] < 2^16`
- `1 <= k <= floor(nums.length / 3)`

---

# Why Greedy Fails

A natural thought is:

> choose the three largest subarray sums of length `k`

This fails because the three largest subarrays may overlap.

Even if we avoid overlap greedily, it can still fail because taking a slightly smaller subarray now might allow two much larger subarrays later.

So the problem requires **global optimization**, not local optimization.

---

# Key Preprocessing Insight

For every valid starting index `i`, define:

```text
sums[i] = sum of subarray nums[i ... i+k-1]
```

There are:

```text
n = nums.length - k + 1
```

possible subarrays of length `k`.

Once we build `sums`, the problem becomes:

> choose 3 non-overlapping indices in `sums` such that the total is maximized

with the rule that if we choose index `i`, the next chosen index must be at least:

```text
i + k
```

because the original windows must not overlap.

This preprocessing simplifies every approach.

---

# Approach 1: Memoization

## Intuition

At each position in `sums`, we have a classic dynamic programming choice:

- **take** the current subarray
- **skip** the current subarray

If we take it:

- we add `sums[idx]`
- then jump ahead by `k` to avoid overlap
- and reduce the number of remaining subarrays by 1

If we skip it:

- we move to `idx + 1`
- and still need the same number of subarrays

This is very similar to a take-or-skip DP such as 0/1 Knapsack style reasoning.

---

## State Definition

Let:

```text
dp(idx, rem)
```

be the maximum total sum obtainable starting from `idx` in `sums`, when we still need to pick `rem` subarrays.

Here:

- `idx` = current starting position under consideration
- `rem` = how many subarrays are still required

Since the problem needs exactly 3 subarrays, `rem` ranges from `0` to `3`.

---

## Base Cases

### 1. No subarrays remaining

If:

```text
rem == 0
```

then we have completed our selection successfully.

Return:

```text
0
```

because no more sum needs to be added.

---

### 2. Out of bounds

If:

```text
idx >= sums.length
```

but we still need subarrays, then this path is invalid.

Return negative infinity conceptually, implemented using a very small integer.

This prevents invalid incomplete paths from being chosen.

---

## Recurrence

At state `(idx, rem)`:

### Take current subarray

```text
withCurrent = sums[idx] + dp(idx + k, rem - 1)
```

### Skip current subarray

```text
skipCurrent = dp(idx + 1, rem)
```

Then:

```text
dp(idx, rem) = max(withCurrent, skipCurrent)
```

This computes the maximum total sum.

---

## Why DFS Reconstruction Gives Lexicographically Smallest Answer

Once the DP table is built, we reconstruct the chosen indices.

At each state, if:

```text
withCurrent >= skipCurrent
```

we choose the current index.

The `>=` is important.

Why?

Because when sums are equal, taking the earlier index produces the lexicographically smaller answer.

Thus reconstruction naturally respects the tie-breaking rule.

---

## Step 1: Build `sums` Using Sliding Window

We first compute the sum of every subarray of length `k`.

This is done in linear time with a sliding window:

- compute the first window sum
- slide one step at a time
- subtract the outgoing element
- add the incoming element

---

## Java Implementation — Memoization

```java
import java.util.*;

class Solution {

    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length - k + 1;

        int[] sums = new int[n];
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }
        sums[0] = windowSum;

        for (int i = k; i < nums.length; i++) {
            windowSum = windowSum - nums[i - k] + nums[i];
            sums[i - k + 1] = windowSum;
        }

        int[][] memo = new int[n][4];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        List<Integer> indices = new ArrayList<>();

        dp(sums, k, 0, 3, memo);
        dfs(sums, k, 0, 3, memo, indices);

        int[] result = new int[3];
        for (int i = 0; i < 3; i++) {
            result[i] = indices.get(i);
        }

        return result;
    }

    private int dp(int[] sums, int k, int idx, int rem, int[][] memo) {
        if (rem == 0) return 0;
        if (idx >= sums.length) {
            return rem > 0 ? Integer.MIN_VALUE : 0;
        }

        if (memo[idx][rem] != -1) {
            return memo[idx][rem];
        }

        int withCurrent = sums[idx] + dp(sums, k, idx + k, rem - 1, memo);
        int skipCurrent = dp(sums, k, idx + 1, rem, memo);

        memo[idx][rem] = Math.max(withCurrent, skipCurrent);
        return memo[idx][rem];
    }

    private void dfs(
        int[] sums,
        int k,
        int idx,
        int rem,
        int[][] memo,
        List<Integer> indices
    ) {
        if (rem == 0) return;
        if (idx >= sums.length) return;

        int withCurrent = sums[idx] + dp(sums, k, idx + k, rem - 1, memo);
        int skipCurrent = dp(sums, k, idx + 1, rem, memo);

        if (withCurrent >= skipCurrent) {
            indices.add(idx);
            dfs(sums, k, idx + k, rem - 1, memo, indices);
        } else {
            dfs(sums, k, idx + 1, rem, memo, indices);
        }
    }
}
```

---

## Complexity Analysis — Memoization

Let:

- `n` = number of possible starting positions in `sums`
- `m` = number of required subarrays

Here `m = 3`, but we keep it symbolic.

### Time Complexity

- Building `sums`: `O(n)`
- DP table size: `O(n × m)`
- Each state is computed once
- DFS reconstruction: `O(m)`

So total is:

```text
O(n × m)
```

Since `m = 3`, this simplifies to:

```text
O(n)
```

---

### Space Complexity

- `sums`: `O(n)`
- `memo`: `O(n × m)`
- recursion stack: `O(n)` worst case

So total is:

```text
O(n × m)
```

With `m = 3`, this is:

```text
O(n)
```

---

# Approach 2: Tabulation

## Intuition

The previous solution is top-down.

We can also solve the same state definition bottom-up.

Instead of recursively asking:

> what is the best from this point onward?

we iteratively build:

> what is the best possible result up to each array position, using 1, 2, or 3 subarrays?

This eliminates recursive overhead and makes the reconstruction cleaner.

---

## Prefix Sum

Here we compute a standard prefix sum array:

```text
prefixSum[i] = sum of nums[0 ... i-1]
```

Then any subarray sum of length `k` ending at `endIndex` or starting at `startIndex` can be computed in constant time.

For a window ending at `endIndex` and starting at `endIndex - k`:

```text
prefixSum[endIndex] - prefixSum[endIndex - k]
```

---

## State Definition

Let:

```text
bestSum[subarrayCount][endIndex]
```

be the maximum total sum achievable using exactly `subarrayCount` non-overlapping subarrays considering elements only up to `endIndex`.

Let:

```text
bestIndex[subarrayCount][endIndex]
```

store the starting index of the latest chosen subarray that leads to that best sum.

---

## Transition

For each `subarrayCount` from `1` to `3`, and each possible `endIndex`:

### Option 1: Include a subarray ending here

Take the current window of length `k`, and combine it with the best result for one fewer subarray ending before this window begins:

```text
currentSum =
    sum(window ending at endIndex)
    + bestSum[subarrayCount - 1][endIndex - k]
```

### Option 2: Skip current ending position

Reuse the best result from the previous position:

```text
bestSum[subarrayCount][endIndex - 1]
```

We choose the larger one.

To ensure lexicographically smallest result, we only update when the current sum is **strictly greater** than the previous best.
If equal, we keep the earlier configuration.

---

## Reconstruction

After filling the table, we trace backward:

- find the best index for 3 subarrays
- then move to the position before that subarray starts
- find the best index for 2 subarrays
- then similarly for 1 subarray

This reconstructs the chosen intervals in reverse order.

---

## Java Implementation — Tabulation

```java
class Solution {

    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length;

        int[] prefixSum = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            prefixSum[i] = prefixSum[i - 1] + nums[i - 1];
        }

        int[][] bestSum = new int[4][n + 1];
        int[][] bestIndex = new int[4][n + 1];

        for (int subarrayCount = 1; subarrayCount <= 3; subarrayCount++) {
            for (int endIndex = k * subarrayCount; endIndex <= n; endIndex++) {
                int currentSum =
                    prefixSum[endIndex] -
                    prefixSum[endIndex - k] +
                    bestSum[subarrayCount - 1][endIndex - k];

                if (currentSum > bestSum[subarrayCount][endIndex - 1]) {
                    bestSum[subarrayCount][endIndex] = currentSum;
                    bestIndex[subarrayCount][endIndex] = endIndex - k;
                } else {
                    bestSum[subarrayCount][endIndex] =
                        bestSum[subarrayCount][endIndex - 1];
                    bestIndex[subarrayCount][endIndex] =
                        bestIndex[subarrayCount][endIndex - 1];
                }
            }
        }

        int[] result = new int[3];
        int currentEnd = n;
        for (int subarrayIndex = 3; subarrayIndex >= 1; subarrayIndex--) {
            result[subarrayIndex - 1] = bestIndex[subarrayIndex][currentEnd];
            currentEnd = result[subarrayIndex - 1];
        }

        return result;
    }
}
```

---

## Complexity Analysis — Tabulation

Let:

- `n` = length of `nums`
- `m` = number of subarrays to pick

### Time Complexity

- Prefix sums: `O(n)`
- DP filling: `O(n × m)`
- Reconstruction: `O(m)`

So total:

```text
O(n × m)
```

With `m = 3`, this becomes:

```text
O(n)
```

---

### Space Complexity

- prefix sum: `O(n)`
- bestSum: `O(n × m)`
- bestIndex: `O(n × m)`

So total:

```text
O(n × m)
```

With `m = 3`, this becomes:

```text
O(n)
```

---

# Approach 3: Three Pointers

## Intuition

Now we exploit a very specific fact:

> the problem asks for exactly 3 subarrays, not an arbitrary number.

So instead of general DP over `m`, we can fix the middle subarray and ask:

- what is the best left subarray before it?
- what is the best right subarray after it?

For each possible middle subarray, if we already know:

- the best left window before it
- the best right window after it

then we can compute the total in constant time.

This leads to a clean linear-time solution.

---

## Step 1: Prefix Sum

Again use prefix sums so every `k`-window sum can be computed in `O(1)`.

---

## Step 2: `leftMaxIndex`

Define:

```text
leftMaxIndex[i]
```

as the starting index of the best subarray of length `k` among all windows ending at or before index `i`.

We scan left to right.

At each step:

- compute current `k`-window sum
- compare it with the best seen so far
- if larger, update best index
- if equal, do not update, because keeping the earlier index gives lexicographically smaller result

---

## Step 3: `rightMaxIndex`

Define:

```text
rightMaxIndex[i]
```

as the starting index of the best subarray of length `k` among all windows starting at or after index `i`.

We scan right to left.

At each step:

- compute current `k`-window sum
- compare with the best seen so far on the right
- if greater **or equal**, update current index

Why `>=` here?

Because while scanning from right to left, choosing the current earlier index in case of equality helps achieve lexicographically smaller final triples.

---

## Step 4: Try Every Middle Window

Let the middle subarray start at `i`.

Then:

- left subarray start is `leftMaxIndex[i - 1]`
- right subarray start is `rightMaxIndex[i + k]`

These are guaranteed non-overlapping because:

- left ends before `i`
- middle occupies `[i, i+k-1]`
- right starts at or after `i+k`

Compute the total sum of left + middle + right.

Keep the best triple.

Because we scan middle positions from left to right and update only on strictly better total sum, lexicographic tie-breaking is naturally preserved.

---

## Java Implementation — Three Pointers

```java
class Solution {

    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length;
        int maxSum = 0;

        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        int[] leftMaxIndex = new int[n];
        int[] rightMaxIndex = new int[n];
        int[] result = new int[3];

        for (
            int i = k, currentMaxSum = prefixSum[k] - prefixSum[0];
            i < n;
            i++
        ) {
            if (prefixSum[i + 1] - prefixSum[i + 1 - k] > currentMaxSum) {
                leftMaxIndex[i] = i + 1 - k;
                currentMaxSum = prefixSum[i + 1] - prefixSum[i + 1 - k];
            } else {
                leftMaxIndex[i] = leftMaxIndex[i - 1];
            }
        }

        rightMaxIndex[n - k] = n - k;
        for (
            int i = n - k - 1, currentMaxSum = prefixSum[n] - prefixSum[n - k];
            i >= 0;
            i--
        ) {
            if (prefixSum[i + k] - prefixSum[i] >= currentMaxSum) {
                rightMaxIndex[i] = i;
                currentMaxSum = prefixSum[i + k] - prefixSum[i];
            } else {
                rightMaxIndex[i] = rightMaxIndex[i + 1];
            }
        }

        for (int i = k; i <= n - 2 * k; i++) {
            int leftIndex = leftMaxIndex[i - 1];
            int rightIndex = rightMaxIndex[i + k];

            int totalSum =
                (prefixSum[i + k] - prefixSum[i]) +
                (prefixSum[leftIndex + k] - prefixSum[leftIndex]) +
                (prefixSum[rightIndex + k] - prefixSum[rightIndex]);

            if (totalSum > maxSum) {
                maxSum = totalSum;
                result[0] = leftIndex;
                result[1] = i;
                result[2] = rightIndex;
            }
        }

        return result;
    }
}
```

---

## Complexity Analysis — Three Pointers

### Time Complexity

We perform:

- one pass for prefix sum
- one pass for `leftMaxIndex`
- one pass for `rightMaxIndex`
- one pass for all middle windows

So total:

```text
O(n)
```

---

### Space Complexity

We use:

- prefix sum array
- leftMaxIndex
- rightMaxIndex

So space complexity is:

```text
O(n)
```

---

# Approach 4: Sliding Window

## Intuition

This approach is specialized to exactly 3 windows.

Imagine 3 fixed-size windows of length `k` moving together through the array:

- first window
- second window
- third window

At every step, maintain:

- current sum of the 1st window
- current sum of the 2nd window
- current sum of the 3rd window

Also maintain the best results seen so far:

- best single subarray
- best pair of non-overlapping subarrays
- best triple of non-overlapping subarrays

This works because by the time the third window reaches a position, we already know the best possible compatible choices for the first and second windows.

Thus we build the solution incrementally in one pass.

---

## State We Maintain

### `bestSingleSum`

The maximum sum seen so far for one window.

### `bestDoubleSum`

The maximum sum seen so far for two non-overlapping windows.

### `bestTripleSum`

The maximum sum seen so far for three non-overlapping windows.

Along with each, store the corresponding start indices.

---

## Why It Works

At each alignment of the three windows:

1. update the first window’s best seen sum
2. use that to update the best pair
3. use the best pair to update the best triple

Since the windows move in order and never overlap, each update is valid.

This is like a compressed DP specifically for 3 subarrays.

---

## Java Implementation — Sliding Window

```java
class Solution {

    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int bestSingleStart = 0;
        int[] bestDoubleStart = { 0, k };
        int[] bestTripleStart = { 0, k, k * 2 };

        int currentWindowSumSingle = 0;
        for (int i = 0; i < k; i++) {
            currentWindowSumSingle += nums[i];
        }

        int currentWindowSumDouble = 0;
        for (int i = k; i < k * 2; i++) {
            currentWindowSumDouble += nums[i];
        }

        int currentWindowSumTriple = 0;
        for (int i = k * 2; i < k * 3; i++) {
            currentWindowSumTriple += nums[i];
        }

        int bestSingleSum = currentWindowSumSingle;
        int bestDoubleSum = currentWindowSumSingle + currentWindowSumDouble;
        int bestTripleSum =
            currentWindowSumSingle +
            currentWindowSumDouble +
            currentWindowSumTriple;

        int singleStartIndex = 1;
        int doubleStartIndex = k + 1;
        int tripleStartIndex = k * 2 + 1;

        while (tripleStartIndex <= nums.length - k) {
            currentWindowSumSingle =
                currentWindowSumSingle -
                nums[singleStartIndex - 1] +
                nums[singleStartIndex + k - 1];

            currentWindowSumDouble =
                currentWindowSumDouble -
                nums[doubleStartIndex - 1] +
                nums[doubleStartIndex + k - 1];

            currentWindowSumTriple =
                currentWindowSumTriple -
                nums[tripleStartIndex - 1] +
                nums[tripleStartIndex + k - 1];

            if (currentWindowSumSingle > bestSingleSum) {
                bestSingleStart = singleStartIndex;
                bestSingleSum = currentWindowSumSingle;
            }

            if (currentWindowSumDouble + bestSingleSum > bestDoubleSum) {
                bestDoubleStart[0] = bestSingleStart;
                bestDoubleStart[1] = doubleStartIndex;
                bestDoubleSum = currentWindowSumDouble + bestSingleSum;
            }

            if (currentWindowSumTriple + bestDoubleSum > bestTripleSum) {
                bestTripleStart[0] = bestDoubleStart[0];
                bestTripleStart[1] = bestDoubleStart[1];
                bestTripleStart[2] = tripleStartIndex;
                bestTripleSum = currentWindowSumTriple + bestDoubleSum;
            }

            singleStartIndex += 1;
            doubleStartIndex += 1;
            tripleStartIndex += 1;
        }

        return bestTripleStart;
    }
}
```

---

## Complexity Analysis — Sliding Window

### Time Complexity

- initial 3 window sums: `O(k)`
- then one linear pass: `O(n)`

So total:

```text
O(n + k)
```

which is effectively:

```text
O(n)
```

for the problem constraints.

---

### Space Complexity

Only a constant number of variables and fixed-size arrays are used.

So:

```text
O(1)
```

---

# Comparing the Approaches

## Memoization

### Strengths

- easiest to derive from “take or skip”
- generalizes well to arbitrary number of subarrays

### Weaknesses

- recursion overhead
- requires reconstruction step

---

## Tabulation

### Strengths

- iterative
- explicit DP states
- clean reconstruction

### Weaknesses

- more memory than necessary for fixed `m = 3`

---

## Three Pointers

### Strengths

- elegant linear-time solution
- standard interview solution
- clear tie-handling logic

### Weaknesses

- specialized to exactly 3 subarrays

---

## Sliding Window

### Strengths

- fastest and most space-efficient
- elegant once understood
- only constant extra space

### Weaknesses

- more specialized and less obvious
- harder to derive from scratch

---

# Lexicographic Tie-Breaking

This is extremely important.

If multiple triples give the same total sum, we must return the lexicographically smallest.

That means:

- smaller first index is better
- if tied, smaller second index is better
- if still tied, smaller third index is better

Different approaches enforce this in different ways:

### Memoization

Use:

```text
withCurrent >= skipCurrent
```

during reconstruction to prefer earlier index.

### Tabulation

Only update on strictly better sum, keeping earlier indices on ties.

### Three Pointers

For left scan, update only on strictly greater sum.
For right scan, update on greater **or equal** sum while scanning from right to left.
This combination preserves lexicographically smallest triples.

### Sliding Window

Again, update only when strictly better total sum appears.

---

# Final Summary

## Main Idea

Convert the original array into `k`-window sums and choose 3 non-overlapping starting positions with maximum total.

---

## Best Complexities

### Memoization

- Time: `O(n × m)` where `m = 3`
- Space: `O(n × m)`

### Tabulation

- Time: `O(n × m)`
- Space: `O(n × m)`

### Three Pointers

- Time: `O(n)`
- Space: `O(n)`

### Sliding Window

- Time: `O(n + k)`
- Space: `O(1)`

---

# Best Practical Java Solution

The **Three Pointers** solution is the standard balance of clarity and efficiency.

```java
class Solution {

    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length;
        int maxSum = 0;

        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        int[] leftMaxIndex = new int[n];
        int[] rightMaxIndex = new int[n];
        int[] result = new int[3];

        for (
            int i = k, currentMaxSum = prefixSum[k] - prefixSum[0];
            i < n;
            i++
        ) {
            if (prefixSum[i + 1] - prefixSum[i + 1 - k] > currentMaxSum) {
                leftMaxIndex[i] = i + 1 - k;
                currentMaxSum = prefixSum[i + 1] - prefixSum[i + 1 - k];
            } else {
                leftMaxIndex[i] = leftMaxIndex[i - 1];
            }
        }

        rightMaxIndex[n - k] = n - k;
        for (
            int i = n - k - 1, currentMaxSum = prefixSum[n] - prefixSum[n - k];
            i >= 0;
            i--
        ) {
            if (prefixSum[i + k] - prefixSum[i] >= currentMaxSum) {
                rightMaxIndex[i] = i;
                currentMaxSum = prefixSum[i + k] - prefixSum[i];
            } else {
                rightMaxIndex[i] = rightMaxIndex[i + 1];
            }
        }

        for (int i = k; i <= n - 2 * k; i++) {
            int leftIndex = leftMaxIndex[i - 1];
            int rightIndex = rightMaxIndex[i + k];

            int totalSum =
                (prefixSum[i + k] - prefixSum[i]) +
                (prefixSum[leftIndex + k] - prefixSum[leftIndex]) +
                (prefixSum[rightIndex + k] - prefixSum[rightIndex]);

            if (totalSum > maxSum) {
                maxSum = totalSum;
                result[0] = leftIndex;
                result[1] = i;
                result[2] = rightIndex;
            }
        }

        return result;
    }
}
```

This is the standard linear-time solution for Maximum Sum of 3 Non-Overlapping Subarrays.
