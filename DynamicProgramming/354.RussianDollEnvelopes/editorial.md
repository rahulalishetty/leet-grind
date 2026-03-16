# 354. Russian Doll Envelopes — Exhaustive Solution Notes

## Overview

This problem is a classic and elegant reduction:

- the original question is about **nesting envelopes in 2 dimensions**,
- but with the right sorting trick, it becomes a **1-dimensional Longest Increasing Subsequence (LIS)** problem.

That reduction is the whole heart of the solution.

At first glance, the problem seems like a 2D dynamic programming problem where we try to compare each envelope with all others. That would work, but it would be too slow for the given constraints:

```text
1 <= envelopes.length <= 10^5
```

So we need something faster than the standard `O(N^2)` LIS approach.

The accepted optimal solution is:

1. **sort envelopes carefully**
2. **extract only heights**
3. **run O(N log N) LIS on heights**

This write-up explains not only how the solution works, but also **why the special sort order is necessary**.

---

## Problem Statement

You are given a 2D array:

```text
envelopes[i] = [wi, hi]
```

where:

- `wi` is the width of the envelope
- `hi` is the height of the envelope

One envelope can fit inside another **if and only if**:

```text
w1 < w2 and h1 < h2
```

You may not rotate envelopes.

Return the maximum number of envelopes you can Russian doll.

---

## Example 1

**Input**

```text
envelopes = [[5,4],[6,4],[6,7],[2,3]]
```

**Output**

```text
3
```

**Explanation**

One valid nesting chain is:

```text
[2,3] -> [5,4] -> [6,7]
```

So the maximum number of envelopes is `3`.

---

## Example 2

**Input**

```text
envelopes = [[1,1],[1,1],[1,1]]
```

**Output**

```text
1
```

**Explanation**

All envelopes are identical, so no one can fit into another.

---

## Constraints

- `1 <= envelopes.length <= 10^5`
- `envelopes[i].length == 2`
- `1 <= wi, hi <= 10^5`

---

# Core Insight

This problem is really asking for the longest chain such that both dimensions increase strictly:

```text
[w1, h1] -> [w2, h2] -> [w3, h3] -> ...
```

with:

```text
w1 < w2 < w3 < ...
h1 < h2 < h3 < ...
```

That is a **2-dimensional increasing subsequence** problem.

The difficulty is that the envelopes are given in arbitrary order, and we are free to rearrange them.

So the challenge becomes:

> Can we sort the envelopes in a way that lets us reduce the 2D problem into a standard 1D LIS problem?

The answer is yes.

---

# Why Sorting Helps

Suppose we sort all envelopes by width in increasing order.

Then any valid nesting chain must also be increasing in this sorted order, because widths must increase.

So after sorting by width, we no longer need to worry about reordering widths ourselves.

Then the only remaining task is:

> among the sorted envelopes, find the longest subsequence whose heights are strictly increasing

That sounds exactly like LIS on heights.

But there is one subtle issue.

---

# The Critical Edge Case

Suppose the envelopes are:

```text
[[1, 3], [1, 4], [1, 5], [2, 3]]
```

If we sort by width ascending only, we get:

```text
[[1,3], [1,4], [1,5], [2,3]]
```

Now extract heights:

```text
[3, 4, 5, 3]
```

The LIS on this array is:

```text
[3, 4, 5]
```

which has length `3`.

But that is wrong.

Why?

Because those first three envelopes all have the same width `1`, so none of them can fit into another.

Equal widths are not allowed in a valid chain.

So simply sorting by width ascending is **not enough**.

---

# The Sorting Trick

To fix this, we sort using this rule:

1. sort by **width ascending**
2. if widths are equal, sort by **height descending**

So the same example becomes:

```text
[[1,5], [1,4], [1,3], [2,3]]
```

Extract heights:

```text
[5, 4, 3, 3]
```

Now the LIS is only length `1`, which is correct.

---

# Why Descending Height for Equal Width Works

This is the key argument.

If two envelopes have the same width:

```text
[w, h1] and [w, h2]
```

then they cannot be nested, no matter what their heights are, because widths must be strictly increasing.

By sorting heights in **descending** order when widths are equal, we ensure they cannot accidentally appear as an increasing subsequence in height.

For example, equal-width heights will look like:

```text
5, 4, 3
```

which is decreasing, so the LIS will never take more than one of them.

That is exactly what we want.

So after this sorting rule, LIS on heights becomes valid.

---

# Reduction to LIS

After sorting:

- width is already handled,
- equal-width conflicts are neutralized by descending height sort,
- so we only need the longest strictly increasing subsequence of heights.

That gives the correct answer.

---

# Notes on the O(N log N) LIS Algorithm

Before diving into the final algorithm, it helps to understand the standard `O(N log N)` LIS method.

We maintain an array:

```text
dp
```

where:

```text
dp[i]
```

stores:

> the smallest possible tail value of an increasing subsequence of length `i + 1`

This is a very important idea.

It does **not** store an actual subsequence.

Instead, it stores the best possible tail for each length.

Smaller tail values are better, because they leave more room for future elements to extend the subsequence.

---

## How LIS Updating Works

For each new number `num`:

- binary search in `dp` for the first position where `num` can go
- place `num` there
- if `num` extends beyond the current end of `dp`, increase the LIS length

This works because:

- replacing a larger tail with a smaller one improves future possibilities
- the length of `dp` at the end is the LIS length

---

## Small LIS Example

Suppose heights are:

```text
[3, 4, 2, 8, 10, 5]
```

We process one by one.

### Start

```text
dp = []
```

### Read 3

```text
dp = [3]
```

### Read 4

```text
dp = [3, 4]
```

### Read 2

Replace first element:

```text
dp = [2, 4]
```

### Read 8

```text
dp = [2, 4, 8]
```

### Read 10

```text
dp = [2, 4, 8, 10]
```

### Read 5

Replace 8:

```text
dp = [2, 4, 5, 10]
```

The LIS length is:

```text
4
```

We do not care that `dp` itself is not necessarily an actual subsequence at every step. Its length is what matters.

---

# Approach 1: Sort + Longest Increasing Subsequence

## Intuition

We must find the maximum sequence of envelopes such that both width and height strictly increase.

A direct 2D LIS would be too expensive.

Instead:

1. sort envelopes by width ascending
2. for equal widths, sort height descending
3. extract heights
4. compute LIS on heights

This works because sorting resolves the width dimension, and the descending tie-break prevents invalid equal-width chains from polluting the LIS.

---

## Algorithm

### Step 1: Sort Envelopes

Sort envelopes with comparator:

- increasing width
- decreasing height if widths are equal

That is:

```text
if w1 == w2:
    sort by h2 - h1
else:
    sort by w1 - w2
```

---

### Step 2: Extract Heights

After sorting, create an array containing only the heights.

For example, if sorted envelopes are:

```text
[[2,3], [5,4], [6,7], [6,4]]
```

after proper sorting they become:

```text
[[2,3], [5,4], [6,7], [6,4]]
```

and heights are:

```text
[3, 4, 7, 4]
```

Then we run LIS on this heights array.

---

### Step 3: Run O(N log N) LIS

Use a `dp` array and binary search to compute the longest strictly increasing subsequence of heights.

The result is the answer.

---

# Why This Gives the Correct Answer

After sorting:

- widths are non-decreasing
- equal widths are arranged with descending heights

So any strictly increasing subsequence of heights must come from envelopes whose widths are also strictly increasing.

Why?

Because if widths were equal, heights would be descending, which cannot be part of a strictly increasing subsequence.

Thus every valid LIS in heights corresponds to a valid Russian doll chain.

And every valid Russian doll chain can be represented after sorting.

So the reduction is correct.

---

# Java Implementation

```java
class Solution {

    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        int len = 0;

        for (int num : nums) {
            int i = Arrays.binarySearch(dp, 0, len, num);
            if (i < 0) {
                i = -(i + 1);
            }

            dp[i] = num;

            if (i == len) {
                len++;
            }
        }

        return len;
    }

    public int maxEnvelopes(int[][] envelopes) {
        // Sort by width ascending, and by height descending when widths are equal
        Arrays.sort(envelopes, new Comparator<int[]>() {
            public int compare(int[] arr1, int[] arr2) {
                if (arr1[0] == arr2[0]) {
                    return arr2[1] - arr1[1];
                } else {
                    return arr1[0] - arr2[0];
                }
            }
        });

        // Extract heights
        int[] secondDim = new int[envelopes.length];
        for (int i = 0; i < envelopes.length; ++i) {
            secondDim[i] = envelopes[i][1];
        }

        return lengthOfLIS(secondDim);
    }
}
```

---

# Walkthrough of Example 1

Input:

```text
[[5,4],[6,4],[6,7],[2,3]]
```

## Step 1: Sort

Sort by width ascending, and by height descending for ties:

```text
[[2,3],[5,4],[6,7],[6,4]]
```

Notice that among width `6`, we place `[6,7]` before `[6,4]`.

## Step 2: Extract Heights

```text
[3, 4, 7, 4]
```

## Step 3: LIS on Heights

The longest strictly increasing subsequence is:

```text
[3, 4, 7]
```

Length:

```text
3
```

So the answer is:

```text
3
```

---

# Why Example 2 Gives 1

Input:

```text
[[1,1],[1,1],[1,1]]
```

After sorting:

```text
[[1,1],[1,1],[1,1]]
```

Heights:

```text
[1,1,1]
```

Strictly increasing LIS length is:

```text
1
```

Correct, because identical envelopes cannot nest.

---

# Complexity Analysis

## Time Complexity

There are two main parts:

### 1. Sorting

Sorting `N` envelopes takes:

```text
O(N log N)
```

### 2. LIS with Binary Search

For each of `N` heights, we do binary search in `dp`, which takes:

```text
O(log N)
```

So this phase also takes:

```text
O(N log N)
```

### Total

```text
O(N log N)
```

---

## Space Complexity

We use:

- an array of heights of size `N`
- a `dp` array of size up to `N`

So the extra space is:

```text
O(N)
```

Sorting may also use extra internal space depending on implementation.

---

# Common Mistakes

## 1. Sorting Both Width and Height Ascending

This is the most common mistake.

If you sort equal widths by height ascending, LIS on heights may incorrectly use multiple envelopes with the same width.

That produces invalid answers.

---

## 2. Forgetting That LIS Must Be Strictly Increasing

The nesting condition is:

```text
w1 < w2 and h1 < h2
```

So heights must also increase strictly.

We are not allowed to treat equal heights as valid extensions.

---

## 3. Using O(N^2) LIS for Large Constraints

A standard DP LIS solution is:

```text
dp[i] = 1 + max(dp[j]) for all j < i where nums[j] < nums[i]
```

That is `O(N^2)`.

With `N = 10^5`, that will not pass.

---

## 4. Thinking the Sort Trick Is Optional

It is not optional.

The descending-height tie-break is what makes the reduction to 1D LIS valid.

Without it, the solution is incorrect.

---

# Interview Perspective

This problem is a very popular interview problem because it tests whether you can:

- recognize a hidden LIS structure,
- reduce a 2D ordering problem into 1D,
- and handle tie-breaking carefully.

A strong explanation should include:

1. why the problem resembles 2D LIS
2. why arbitrary order is a problem
3. why sorting by width ascending is natural
4. why equal widths require height descending
5. why LIS on heights now becomes valid

That reasoning is more important than just memorizing the code.

---

# Final Summary

## Key Reduction

The problem is a 2D LIS problem.

To solve it efficiently:

1. sort by width ascending
2. for equal widths, sort height descending
3. run LIS on heights

---

## Why Descending Height for Equal Widths?

Because envelopes with equal widths cannot nest.

Sorting equal widths by descending height ensures they cannot appear together in a strictly increasing subsequence of heights.

---

## LIS Idea

Maintain:

```text
dp[i] = smallest possible tail of an increasing subsequence of length i + 1
```

Use binary search to update `dp`.

The final length of `dp` is the answer.

---

## Complexity

- Time: `O(N log N)`
- Space: `O(N)`

---

# Best Final Java Solution

```java
class Solution {

    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        int len = 0;

        for (int num : nums) {
            int i = Arrays.binarySearch(dp, 0, len, num);
            if (i < 0) {
                i = -(i + 1);
            }

            dp[i] = num;

            if (i == len) {
                len++;
            }
        }

        return len;
    }

    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                if (a[0] == b[0]) {
                    return b[1] - a[1];
                }
                return a[0] - b[0];
            }
        });

        int[] heights = new int[envelopes.length];
        for (int i = 0; i < envelopes.length; i++) {
            heights[i] = envelopes[i][1];
        }

        return lengthOfLIS(heights);
    }
}
```

This is the standard optimal solution for the problem.
