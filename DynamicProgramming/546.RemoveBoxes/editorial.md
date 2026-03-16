# 546. Remove Boxes — Exhaustive Solution Notes

## Overview

This problem is one of the classic hard interval dynamic programming problems.

We are given an array `boxes`, where each integer represents a color.
In one move, we may remove any **continuous group of equal-colored boxes**.

If the group has size `k`, then we gain:

```text
k × k
```

points.

Our goal is to remove all boxes in some order so that the **total score is maximized**.

At first glance, the problem may look like a greedy problem:

- remove the largest group first
- or remove the most frequent color first
- or remove the earliest merge opportunity first

All of these ideas fail.

The core difficulty is that:

> sometimes removing a smaller group first allows equal-colored boxes that are separated to merge later, producing a much larger square reward.

That is why the problem needs dynamic programming, and not a simple greedy strategy.

This write-up explains two approaches in detail:

1. **Brute Force**
2. **Top-Down Dynamic Programming with 3D memoization**

The second approach is the standard accepted solution.

---

## Problem Statement

You are given an array `boxes` of positive integers representing colored boxes.

You may repeatedly remove groups of **contiguous boxes with the same color**.

If you remove a group of size `k`, you score:

```text
k × k
```

Return the maximum points you can get after removing all boxes.

---

## Example 1

**Input**

```text
boxes = [1,3,2,2,2,3,4,3,1]
```

**Output**

```text
23
```

**Explanation**

One optimal sequence is:

```text
[1, 3, 2, 2, 2, 3, 4, 3, 1]
-> remove [2,2,2]          score = 3×3 = 9
[1, 3, 3, 4, 3, 1]

-> remove [4]              score = 1×1 = 1
[1, 3, 3, 3, 1]

-> remove [3,3,3]          score = 3×3 = 9
[1, 1]

-> remove [1,1]            score = 2×2 = 4
[]
```

Total:

```text
9 + 1 + 9 + 4 = 23
```

---

## Example 2

**Input**

```text
boxes = [1,1,1]
```

**Output**

```text
9
```

**Explanation**

Remove all three together:

```text
3 × 3 = 9
```

---

## Example 3

**Input**

```text
boxes = [1]
```

**Output**

```text
1
```

---

## Constraints

- `1 <= boxes.length <= 100`
- `1 <= boxes[i] <= 100`

---

# Why Greedy Fails

Consider:

```text
[A, A, B, A, A]
```

If we greedily remove the left `AA` and right `AA` separately, we get:

```text
2² + 2² + 1² = 4 + 4 + 1 = 9
```

But if we remove `B` first:

```text
1² = 1
```

then the array becomes:

```text
[A, A, A, A]
```

and then:

```text
4² = 16
```

Total:

```text
1 + 16 = 17
```

which is much better than `9`.

So the order of removals matters critically.

This means local decisions are not enough.

---

# Approach 1: Brute Force [Time Limit Exceeded]

## Intuition

The most straightforward approach is:

- try removing every possible contiguous same-color group,
- recursively solve the remaining array,
- take the maximum total score.

This is easy to think of and guarantees correctness, but the number of possible recursive branches is enormous.

---

## Algorithm

For the current array:

1. Iterate through each possible contiguous same-color group.
2. Remove that group and construct the remaining array.
3. Recursively compute the best score for the remaining array.
4. Add:
   ```text
   groupSize × groupSize
   ```
5. Take the maximum over all choices.

---

## Java Implementation — Brute Force

```java
class Solution {
    public int removeBoxes(int[] boxes) {
        return remove(boxes);
    }

    public int remove(int[] boxes) {
        if (boxes.length == 0) {
            return 0;
        }

        int res = 0;

        for (int i = 0, j = i + 1; i < boxes.length; i++) {
            while (j < boxes.length && boxes[i] == boxes[j]) {
                j++;
            }

            int[] newBoxes = new int[boxes.length - (j - i)];
            for (int k = 0, p = 0; k < boxes.length; k++) {
                if (k == i) {
                    k = j;
                }
                if (k < boxes.length) {
                    newBoxes[p++] = boxes[k];
                }
            }

            res = Math.max(res, remove(newBoxes) + (j - i) * (j - i));
        }

        return res;
    }
}
```

---

## Complexity Analysis — Brute Force

### Time Complexity

In the worst case, the branching factor becomes extremely large.

A rough upper bound is:

```text
O(n!)
```

because for many configurations we try many different removal orders.

So the brute-force solution is far too slow.

---

### Space Complexity

The recursion depth can go up to `n`, and at each level we may create new arrays.

So the space complexity is roughly:

```text
O(n²)
```

---

# Why Plain Interval DP Is Not Enough

A natural thought is:

> Let `dp[l][r]` be the best score for subarray `boxes[l...r]`.

Unfortunately, this is not sufficient.

Why?

Because the best score for a subarray may depend on boxes **outside** the interval that can later merge with its boundary.

For example, consider:

```text
[3, 2, 1, 4, 4, 2, 4, 4]
```

The value for the subarray `[3, 2, 1]` depends on whether the `2` later in the array has already been removed, because that affects whether certain `4`s can merge together.

So a 2D interval state loses important information.

We need an extra dimension.

---

# Approach 2: Top-Down Dynamic Programming

## Intuition

The key trick is to carry information about how many extra boxes of the same color as the right boundary are already attached to it.

Define:

```text
dp[l][r][k]
```

as:

> the maximum points obtainable from subarray `boxes[l...r]`
> if there are already `k` extra boxes equal to `boxes[r]`
> attached to the right of index `r`

This is the crucial insight.

It means that when evaluating `boxes[r]`, we know how many same-colored boxes can be grouped with it later.

---

## Meaning of `k`

Suppose we are evaluating:

```text
boxes[l...r]
```

and additionally imagine there are `k` extra boxes of color `boxes[r]` immediately after `r`.

Then if we eventually remove `boxes[r]` together with those extra `k` boxes, the group size becomes:

```text
k + 1
```

or larger if `boxes[r-1]`, `boxes[r-2]`, etc. also match.

This extra dimension is exactly what allows us to model future merges.

---

## State Definition

Let:

```text
calculatePoints(l, r, k)
```

represent:

> the maximum score we can obtain from `boxes[l...r]`
> assuming there are `k` extra boxes equal to `boxes[r]`
> that are conceptually attached after `r`

This is stored in:

```text
dp[l][r][k]
```

---

## Base Case

If:

```text
l > r
```

then there are no boxes left.

So:

```text
return 0
```

---

# First Important Optimization: Merge Trailing Equal Boxes

Before solving `(l, r, k)`, if the last few boxes are already equal, we should compress them into `k`.

For example, if the suffix looks like:

```text
..., 6, 6, 6
```

then instead of treating them as separate boxes, we can fold them into the state.

So while:

```text
boxes[r] == boxes[r - 1]
```

we do:

- `r--`
- `k++`

This reduces the number of distinct states and makes the recursion more efficient.

---

## Transition 1: Remove the Last Group Immediately

The simplest option is:

- solve the interval `boxes[l...r-1]`
- then remove `boxes[r]` together with its `k` attached equal boxes

That gives:

```text
dp[l][r][k] = dp[l][r-1][0] + (k + 1)²
```

Why `0` in `dp[l][r-1][0]`?

Because once we decide to remove `boxes[r]` with its attached boxes, the left subproblem no longer has any carry-over boxes.

---

## Transition 2: Merge `boxes[r]` with an Earlier Matching Box

Now comes the subtle but powerful part.

Suppose there exists an index `i` with:

```text
l <= i < r
and boxes[i] == boxes[r]
```

Then instead of removing `boxes[r]` immediately, we may first remove all boxes between `i` and `r`:

```text
boxes[i+1...r-1]
```

so that `boxes[i]` and `boxes[r]` become adjacent.

This allows us to merge `boxes[i]` with `boxes[r]` and the `k` attached boxes.

The recurrence becomes:

```text
dp[l][r][k] =
max(
    dp[l][r][k],
    dp[l][i][k+1] + dp[i+1][r-1][0]
)
```

---

## Why This Transition Works

If `boxes[i] == boxes[r]`, then after removing everything between them, we can treat `boxes[i]` as part of the larger trailing group.

So:

- `dp[i+1][r-1][0]` removes the middle section first
- `dp[l][i][k+1]` solves the left interval, but now with one extra matching box attached at the right

That is exactly how separated same-colored groups get merged for better scoring.

---

# Full Recurrence

After compressing trailing equal boxes, we compute:

```text
dp[l][r][k] =
dp[l][r-1][0] + (k + 1)²
```

Then for every `i` in `[l, r-1]` such that:

```text
boxes[i] == boxes[r]
```

update:

```text
dp[l][r][k] =
max(
    dp[l][r][k],
    dp[l][i][k+1] + dp[i+1][r-1][0]
)
```

That gives the optimal answer for the state.

---

## Java Implementation — Top-Down DP

```java
class Solution {
    public int removeBoxes(int[] boxes) {
        int n = boxes.length;
        int[][][] dp = new int[n][n][n];
        return calculatePoints(boxes, dp, 0, n - 1, 0);
    }

    public int calculatePoints(int[] boxes, int[][][] dp, int l, int r, int k) {
        if (l > r) {
            return 0;
        }

        while (r > l && boxes[r] == boxes[r - 1]) {
            r--;
            k++;
        }

        if (dp[l][r][k] != 0) {
            return dp[l][r][k];
        }

        dp[l][r][k] = calculatePoints(boxes, dp, l, r - 1, 0) + (k + 1) * (k + 1);

        for (int i = l; i < r; i++) {
            if (boxes[i] == boxes[r]) {
                dp[l][r][k] = Math.max(
                    dp[l][r][k],
                    calculatePoints(boxes, dp, l, i, k + 1)
                    + calculatePoints(boxes, dp, i + 1, r - 1, 0)
                );
            }
        }

        return dp[l][r][k];
    }
}
```

---

# Step-by-Step Interpretation of the DP

Suppose we are solving:

```text
dp[l][r][k]
```

Think of it this way:

- we are responsible for boxes from `l` to `r`
- `boxes[r]` has `k` extra matching companions already attached

Then we ask:

### Option A

Remove the `boxes[r]` group now.

That yields:

```text
(k + 1)²
```

plus whatever best score we can get from the left side.

### Option B

Try to find some earlier `i` where `boxes[i] == boxes[r]`.

If we can eliminate everything between `i` and `r`, then `boxes[i]` and `boxes[r]` can join into one larger group, which may give a higher square reward later.

The DP compares all such possibilities.

---

# Example Intuition

Consider:

```text
[1, 3, 2, 2, 2, 3, 4, 3, 1]
```

A greedy strategy might remove `3`s too early.

But the optimal strategy removes:

- the `2,2,2` block first,
- then `4`,
- which lets the three `3`s merge,
- and later the two `1`s merge.

This is exactly the kind of future-merging effect the `k` parameter is designed to capture.

---

# Why the DP Is Correct

The recurrence is complete because for the rightmost box group there are only two essential possibilities:

1. remove it now
2. delay removing it so it can merge with an earlier matching box

Every optimal strategy must fall into one of these categories.

The DP explores both and takes the best.

Because results are memoized, each state is solved only once.

---

## Complexity Analysis — Top-Down DP

Let `n = boxes.length`.

### Time Complexity

The DP table has size:

```text
O(n³)
```

because the state is `(l, r, k)`.

For each state, we may scan all indices `i` from `l` to `r-1`.

So total time complexity is:

```text
O(n⁴)
```

---

### Space Complexity

The memoization table stores:

```text
O(n³)
```

states.

So the space complexity is:

```text
O(n³)
```

The recursion stack adds at most `O(n)` more, which is dominated by `O(n³)`.

---

# Comparing the Two Approaches

## Brute Force

### Strengths

- very direct
- easy to understand initially

### Weaknesses

- enormous repeated computation
- factorial/exponential explosion
- completely infeasible for `n = 100`

---

## Top-Down DP

### Strengths

- captures the true structure of the problem
- reuses subproblem results
- accepted and standard

### Weaknesses

- the 3D state is not obvious
- harder to derive than standard interval DP

---

# Common Mistakes

## 1. Using only `dp[l][r]`

This loses crucial information about possible future merges of equal-colored boxes.

The third parameter `k` is essential.

---

## 2. Forgetting to compress trailing equal boxes

The optimization:

```java
while (r > l && boxes[r] == boxes[r - 1]) {
    r--;
    k++;
}
```

is important both for performance and for simplifying the state meaning.

---

## 3. Assuming removing the largest current group is always best

That is false because smaller removals may enable much larger merged groups later.

---

## 4. Missing the merge transition

The recurrence is not complete if it only considers:

```text
dp[l][r-1][0] + (k+1)²
```

It must also try to merge `boxes[r]` with earlier equal-colored boxes.

---

# Interview Perspective

This problem is famous because it teaches an important DP lesson:

> Sometimes the best state is not just the interval itself, but the interval plus some extra context about what is attached outside it.

That is why the state is 3D instead of 2D.

A strong explanation usually goes like this:

1. Brute force tries every removal order but repeats too much work.
2. A 2D interval DP is insufficient because future merges outside the interval matter.
3. Add a third parameter `k` to remember how many same-colored boxes are attached to the right.
4. Transition:
   - remove the trailing group now
   - or merge it with an earlier matching box by removing the middle first
5. Memoize the result.

That is the key insight.

---

# Final Summary

## Problem Type

This is a hard **interval dynamic programming** problem with an extra carry parameter.

---

## State

```text
dp[l][r][k]
```

means:

> maximum score obtainable from `boxes[l...r]`
> assuming there are `k` extra boxes equal to `boxes[r]`
> attached after index `r`

---

## Transitions

### Remove trailing group immediately

```text
dp[l][r][k] = dp[l][r-1][0] + (k+1)²
```

### Merge with earlier equal-colored box

For each `i` where `boxes[i] == boxes[r]`:

```text
dp[l][r][k] =
max(dp[l][r][k], dp[l][i][k+1] + dp[i+1][r-1][0])
```

---

## Complexities

### Brute Force

- Time: `O(n!)` (rough upper bound / extremely bad)
- Space: `O(n²)`

### Top-Down DP

- Time: `O(n⁴)`
- Space: `O(n³)`

---

# Best Final Java Solution

```java
class Solution {
    public int removeBoxes(int[] boxes) {
        int n = boxes.length;
        int[][][] dp = new int[n][n][n];
        return calculatePoints(boxes, dp, 0, n - 1, 0);
    }

    public int calculatePoints(int[] boxes, int[][][] dp, int l, int r, int k) {
        if (l > r) {
            return 0;
        }

        while (r > l && boxes[r] == boxes[r - 1]) {
            r--;
            k++;
        }

        if (dp[l][r][k] != 0) {
            return dp[l][r][k];
        }

        dp[l][r][k] = calculatePoints(boxes, dp, l, r - 1, 0) + (k + 1) * (k + 1);

        for (int i = l; i < r; i++) {
            if (boxes[i] == boxes[r]) {
                dp[l][r][k] = Math.max(
                    dp[l][r][k],
                    calculatePoints(boxes, dp, l, i, k + 1)
                    + calculatePoints(boxes, dp, i + 1, r - 1, 0)
                );
            }
        }

        return dp[l][r][k];
    }
}
```

This is the standard accepted dynamic programming solution for the problem.
