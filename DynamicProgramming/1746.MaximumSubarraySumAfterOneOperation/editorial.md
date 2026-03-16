# Maximum Subarray Sum After One Square Operation — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Overview

We are given an integer array `nums`.

We are allowed to perform **exactly one operation**:

- pick one index `i`
- replace `nums[i]` with:

```text
nums[i] * nums[i]
```

After doing this, we want to find the **maximum possible sum of any subarray**.

---

# Restating the Goal

We are not asked to maximize the whole array sum.

We are asked to maximize the sum of **some contiguous subarray** after squaring exactly one element.

So the task is:

1. choose one element to square
2. then find the best contiguous subarray in the modified array
3. maximize that result

---

# Why the Brute Force Idea Is Too Slow

A direct idea would be:

- square each element one by one
- for each modified array, run Kadane’s algorithm to find the maximum subarray sum
- take the maximum across all choices

If the array length is `n`, then:

- there are `n` choices for the squared element
- each Kadane run is `O(n)`

So the total becomes:

```text
O(n^2)
```

That is too slow for large input sizes.

So we need an `O(n)` solution.

---

# Relationship to Maximum Subarray

This problem is closely related to **Kadane’s algorithm**.

If you have not yet internalized maximum subarray DP, it helps to first understand:

- **53. Maximum Subarray**

This problem is essentially Kadane plus one special state:

> whether we have already used the square operation or not

---

# Approach 1: MaxLeft and MaxRight

## Intuition

A very important observation is:

> Squaring an element outside the final chosen subarray is useless.

Why?

Because the squared value only helps if that element actually participates in the subarray whose sum we are maximizing.

So the optimal subarray must look like this:

1. some best subarray ending just before index `i`
2. the squared element at index `i`
3. some best subarray starting just after index `i`

In other words, for every possible squared index `i`, the best answer using `i` is:

```text
best sum ending before i
+ nums[i]^2
+ best sum starting after i
```

So if we can precompute:

- `maxLeft[i]` = best subarray sum ending just before `i`
- `maxRight[i]` = best subarray sum starting just after `i`

then the answer for index `i` is easy to compute.

---

## What `maxLeft[i]` Means

`maxLeft[i]` represents the maximum sum of a subarray that ends at index `i - 1`.

It may also be zero, which means:

> do not take any prefix on the left

So:

```text
maxLeft[i] = max(0, best subarray ending at i - 1)
```

---

## What `maxRight[i]` Means

`maxRight[i]` represents the maximum sum of a subarray that starts at index `i + 1`.

It may also be zero, which means:

> do not take any suffix on the right

So:

```text
maxRight[i] = max(0, best subarray starting at i + 1)
```

---

## Why This Works

Suppose we choose to square `nums[i]`.

Then the best subarray containing that squared element must include:

- the most profitable extension from the left
- the most profitable extension from the right

If extending in one direction would reduce the sum, we just skip it by using zero.

Thus the candidate answer at `i` is:

```text
maxLeft[i] + nums[i]^2 + maxRight[i]
```

Taking the maximum over all `i` gives the final result.

---

## Algorithm

1. Create arrays `maxLeft` and `maxRight`
2. Fill `maxLeft` from left to right
3. Fill `maxRight` from right to left
4. For each index `i`, compute:

```text
maxLeft[i] + nums[i]^2 + maxRight[i]
```

5. Return the maximum value found

---

## Java Implementation

```java
class Solution {

    public int maxSumAfterOperation(int[] nums) {
        int n = nums.length;

        // Arrays to store the maximum sum of subarrays ending before and starting after each element
        int[] maxLeft = new int[n];
        int[] maxRight = new int[n];

        // No subarray ends before the first element, so set maxLeft[0] to 0
        maxLeft[0] = 0;
        for (int i = 1; i < n; i++) {
            // Compute maxLeft[i]: the maximum subarray sum ending just before nums[i]
            maxLeft[i] = Math.max(maxLeft[i - 1] + nums[i - 1], 0);
        }

        // No subarray starts after the last element, so set maxRight[n - 1] to 0
        maxRight[n - 1] = 0;
        for (int i = n - 2; i >= 0; i--) {
            // Compute maxRight[i]: the maximum subarray sum starting just after nums[i]
            maxRight[i] = Math.max(maxRight[i + 1] + nums[i + 1], 0);
        }

        // Initialize the maximum sum as 0
        int maxSum = 0;

        // Iterate over each element in the array
        for (int i = 0; i < n; i++) {
            maxSum = Math.max(
                maxSum,
                maxLeft[i] + nums[i] * nums[i] + maxRight[i]
            );
        }

        return maxSum;
    }
}
```

---

## Complexity Analysis

Let `n` be the length of the array.

### Time Complexity

We traverse the array:

- once to build `maxLeft`
- once to build `maxRight`
- once to evaluate each squared position

So total time is:

```text
O(n)
```

### Space Complexity

We use two arrays of length `n`:

- `maxLeft`
- `maxRight`

So space is:

```text
O(n)
```

---

# Approach 2: Top-Down Dynamic Programming

## Intuition

Now let us build a recursive DP.

For each position, there are two states:

1. we have **not used** the square operation yet
2. we **have already used** the square operation

At each index, we want the maximum subarray sum **starting from that index** under those conditions.

This is similar to recursion for maximum subarray, but with an extra boolean state indicating whether the square is still available.

---

## State Definition

Define:

```text
getMaxSumHelper(index, canSquare)
```

as:

> the maximum sum of a subarray starting at `index`, where `canSquare` tells us whether we are still allowed to square one element.

So:

- `canSquare = true` means the square operation is still unused
- `canSquare = false` means the square operation has already been used

---

## Two Main Choices at Each Index

### Choice 1: Do not square `nums[index]`

Then the current value is just:

```text
nums[index]
```

We may optionally extend into the next index if doing so helps.

So:

```text
nums[index] + max(0, next sum without squaring now)
```

---

### Choice 2: Square `nums[index]`

This is only allowed if `canSquare == true`.

Then the current value becomes:

```text
nums[index] * nums[index]
```

After using the square here, all future recursion must use:

```text
canSquare = false
```

Again we may extend to the right only if that helps.

So:

```text
nums[index]^2 + max(0, next sum after using square)
```

---

## Why Memoization Is Needed

Without memoization, the recursion would repeatedly solve the same subproblems.

Since the state is determined by:

- `index`
- `canSquare`

there are only `2 * n` unique states.

Memoization reduces the exponential recursion to linear number of states.

---

## Java Implementation

```java
class Solution {

    public int maxSumAfterOperation(int[] nums) {
        int n = nums.length;
        // Initialize a DP table to store results of subproblems.
        int[][] dp = new int[n][2];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dp[i], -1); // Initialize all entries to -1.
        }

        // Create array to pass by reference
        int maxSum[] = new int[1];
        maxSum[0] = 0;

        // Call the recursive helper function to compute the result.
        getMaxSumHelper(0, nums, true, dp, maxSum);
        return maxSum[0];
    }

    private int getMaxSumHelper(
        int index,
        int[] nums,
        boolean canSquare,
        int[][] dp,
        int[] maxSum
    ) {
        if (index == nums.length) {
            return 0; // Base case: if we reach the end of the array, return 0.
        }

        // If the result is already computed for this state, return it.
        if (dp[index][canSquare ? 1 : 0] != -1) {
            return dp[index][canSquare ? 1 : 0];
        }

        // Case 1: Skip squaring the current element.
        int nextSumWithoutSquare = getMaxSumHelper(
            index + 1,
            nums,
            canSquare,
            dp,
            maxSum
        );
        int maxSumWithoutSquare = nums[index];
        if (nextSumWithoutSquare > 0) {
            maxSumWithoutSquare += nextSumWithoutSquare;
        }

        // Case 2: Square the current element if allowed.
        int maxSumWithSquare = 0;
        if (canSquare) {
            maxSumWithSquare = nums[index] * nums[index];
            int nextSumWithSquare = getMaxSumHelper(
                index + 1,
                nums,
                false,
                dp,
                maxSum
            );
            if (nextSumWithSquare > 0) {
                maxSumWithSquare += nextSumWithSquare;
            }
        }

        // Update the global maxSum if we find a better one.
        maxSum[0] = Math.max(
            maxSum[0],
            Math.max(maxSumWithSquare, maxSumWithoutSquare)
        );

        // Store the result in dp table and return the maximum of the two options.
        dp[index][canSquare ? 1 : 0] = Math.max(
            maxSumWithSquare,
            maxSumWithoutSquare
        );
        return dp[index][canSquare ? 1 : 0];
    }
}
```

---

## Complexity Analysis

Let `n` be the array length.

### Time Complexity

There are only `2 * n` states:

- each index
- each of two `canSquare` values

Each state does constant work.

So:

```text
O(n)
```

### Space Complexity

We use:

- a DP table of size `2n`
- recursion stack up to depth `n`

So total extra space is:

```text
O(n)
```

---

# Approach 3: Bottom-Up Dynamic Programming

## Intuition

The top-down recursion suggests a natural iterative DP formulation.

Instead of thinking about subarrays **starting at** an index, we can think in classic Kadane style:

> maximum subarray sum **ending at** index `i`

This is a standard direction for bottom-up DP.

We maintain two values for each index:

- maximum subarray sum ending at `i` with **no squared element**
- maximum subarray sum ending at `i` with **exactly one squared element**

---

## DP Definition

Let:

```text
dp[index][0]
```

be:

> maximum subarray sum ending at `index` with no squared element used

Let:

```text
dp[index][1]
```

be:

> maximum subarray sum ending at `index` with exactly one squared element used

---

## Transition for `dp[index][0]`

This is just standard Kadane.

We have two choices:

1. start a new subarray at `index`
2. extend the previous no-square subarray

So:

```text
dp[index][0] = max(nums[index], dp[index - 1][0] + nums[index])
```

---

## Transition for `dp[index][1]`

Now we want a subarray ending at `index` with exactly one squared element somewhere.

There are three possibilities:

### Option 1: Start a new subarray and square the current element

```text
nums[index]^2
```

### Option 2: Continue a no-square subarray and square the current element now

```text
dp[index - 1][0] + nums[index]^2
```

### Option 3: Continue a subarray that already used the square earlier

```text
dp[index - 1][1] + nums[index]
```

So the transition becomes:

```text
dp[index][1] = max(
    nums[index]^2,
    dp[index - 1][0] + nums[index]^2,
    dp[index - 1][1] + nums[index]
)
```

---

## Final Answer

Since we must use exactly one square operation, the result is the maximum among all:

```text
dp[index][1]
```

---

## Java Implementation

```java
class Solution {

    public int maxSumAfterOperation(int[] nums) {
        int n = nums.length;

        // Initialize a DP table
        int[][] dp = new int[n][2];

        // Base case
        dp[0][0] = nums[0];
        dp[0][1] = nums[0] * nums[0];

        int maxSum = dp[0][1];

        for (int index = 1; index < n; index++) {
            // No square used yet
            dp[index][0] = Math.max(
                nums[index],
                dp[index - 1][0] + nums[index]
            );

            // Exactly one square used
            dp[index][1] = Math.max(
                Math.max(
                    nums[index] * nums[index],
                    dp[index - 1][0] + nums[index] * nums[index]
                ),
                dp[index - 1][1] + nums[index]
            );

            maxSum = Math.max(maxSum, dp[index][1]);
        }

        return maxSum;
    }
}
```

---

## Complexity Analysis

Let `n` be the length of the array.

### Time Complexity

We iterate once through the array, and each index requires only constant-time transitions.

So:

```text
O(n)
```

### Space Complexity

We store a `dp` table of size `n x 2`.

So:

```text
O(n)
```

---

# Approach 4: Space-Optimized Dynamic Programming

## Intuition

In the previous DP approach, each row only depends on the previous row.

So storing the whole `dp` table is unnecessary.

We only need:

- previous `dp[index - 1][0]`
- previous `dp[index - 1][1]`

This allows us to collapse the DP into just two variables.

This is the same optimization often applied to Kadane-like DP.

---

## Variables

Maintain:

- `maxSumWithoutSquare` = current best subarray sum ending here with no square used
- `maxSumWithSquare` = current best subarray sum ending here with exactly one square used

At every new element, update both.

---

## Important Update Order Subtlety

When computing the next values, `maxSumWithSquare` depends on the **old** `maxSumWithoutSquare`.

So conceptually, it is safest to think in terms of:

- compute new values from old values
- then assign them

The provided implementation updates in-place, but the underlying logic is still based on previous-state values.

---

## Java Implementation

```java
class Solution {

    public int maxSumAfterOperation(int[] nums) {
        int n = nums.length;

        // Initialize variables to store the maximum sums.
        int maxSumWithoutSquare = nums[0];
        int maxSumWithSquare = nums[0] * nums[0];
        int maxSum = maxSumWithSquare;

        for (int index = 1; index < n; index++) {
            // Option 1: Square the current element.
            // Option 2: Add the square of the current element to the previous sum without a square.
            // Option 3: Add the current element to the previous sum with a square.
            maxSumWithSquare = Math.max(
                Math.max(
                    nums[index] * nums[index],
                    maxSumWithoutSquare + nums[index] * nums[index]
                ),
                maxSumWithSquare + nums[index]
            );

            // Option 1: Start a new subarray.
            // Option 2: Continue the previous subarray.
            maxSumWithoutSquare = Math.max(
                nums[index],
                maxSumWithoutSquare + nums[index]
            );

            // Update maxSum
            maxSum = Math.max(maxSum, maxSumWithSquare);
        }

        return maxSum;
    }
}
```

---

## Complexity Analysis

Let `n` be the array length.

### Time Complexity

We traverse the array once.

So:

```text
O(n)
```

### Space Complexity

We only use a constant number of variables.

So:

```text
O(1)
```

---

# Comparing the Approaches

| Approach           | Main Idea                                                                 | Time Complexity | Space Complexity |
| ------------------ | ------------------------------------------------------------------------- | --------------: | ---------------: |
| MaxLeft / MaxRight | Precompute best left and right contributions around each squared position |          `O(n)` |           `O(n)` |
| Top-Down DP        | Memoized recursion with state: square available or not                    |          `O(n)` |           `O(n)` |
| Bottom-Up DP       | DP table for subarrays ending at each index                               |          `O(n)` |           `O(n)` |
| Space-Optimized DP | Same as bottom-up DP using only two variables                             |          `O(n)` |           `O(1)` |

---

# Which Approach Is Best?

## Conceptually Easiest

The **MaxLeft / MaxRight** approach is very intuitive because it explicitly models the final subarray as:

```text
left part + squared element + right part
```

It is a nice structural view of the problem.

---

## Most Standard DP View

The **Bottom-Up DP** approach is usually the most interview-friendly if you already know Kadane’s algorithm.

It naturally extends maximum subarray reasoning with one extra state.

---

## Best Practical Version

The **Space-Optimized DP** is usually the strongest final implementation because it gives:

- linear time
- constant extra space
- very compact logic

---

# Key Takeaways

## 1. The squared element must belong to the chosen subarray

Squaring an element outside the final subarray cannot help.

That is the central insight behind Approach 1.

---

## 2. This is Kadane with an extra state

Classic Kadane tracks:

- best subarray ending here

This problem tracks two versions:

- best subarray ending here with no square used
- best subarray ending here with one square used

---

## 3. Exactly one operation matters

The answer must come from the state where one square has been used.

So in DP, we care about `with square` states for the final result.

---

## 4. State compression is straightforward

Because each DP state depends only on the previous index, the `O(n)` DP table compresses to `O(1)` space.

---

# Final Insight

This problem looks like a brute-force “try squaring every element” task at first, but the real trick is realizing that the square operation can be folded directly into maximum-subarray DP.

Once you separate the states:

- no square used yet
- square already used

the whole problem becomes a linear-time dynamic programming problem.

That is why all of the optimized solutions run in:

```text
O(n)
```

instead of `O(n^2)`.
