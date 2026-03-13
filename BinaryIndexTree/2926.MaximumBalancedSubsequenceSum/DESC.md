# 2926. Maximum Balanced Subsequence Sum

## Problem Statement

You are given a **0-indexed integer array** `nums`.

A subsequence of `nums` having length `k` and consisting of indices:

```
i0 < i1 < ... < ik-1
```

is **balanced** if the following holds:

```
nums[ij] - nums[ij-1] >= ij - ij-1
```

for every `j` in the range:

```
1 <= j <= k - 1
```

A subsequence of length **1** is always considered balanced.

Return the **maximum possible sum** of elements in a balanced subsequence of `nums`.

A **subsequence** of an array is a new non‑empty array that is formed from the original array by deleting some (possibly none) elements **without changing the order of the remaining elements**.

---

# Example 1

## Input

```
nums = [3,3,5,6]
```

## Output

```
14
```

## Explanation

One valid subsequence is:

```
[3,5,6]
```

Using indices:

```
0, 2, 3
```

Check conditions:

```
nums[2] - nums[0] >= 2 - 0
nums[3] - nums[2] >= 3 - 2
```

Both conditions hold, so the subsequence is balanced.

Sum:

```
3 + 5 + 6 = 14
```

The subsequence `[3,5,6]` (or `[3,5,6]` using indices `1,2,3`) gives the **maximum possible sum**.

---

# Example 2

## Input

```
nums = [5,-1,-3,8]
```

## Output

```
13
```

## Explanation

One valid subsequence is:

```
[5,8]
```

Using indices:

```
0, 3
```

Check condition:

```
nums[3] - nums[0] >= 3 - 0
```

So the subsequence is balanced.

Sum:

```
5 + 8 = 13
```

This is the maximum possible balanced subsequence sum.

---

# Example 3

## Input

```
nums = [-2,-1]
```

## Output

```
-1
```

## Explanation

The best subsequence is:

```
[-1]
```

Any single element subsequence is balanced.

So the maximum sum is:

```
-1
```

---

# Constraints

```
1 <= nums.length <= 10^5
-10^9 <= nums[i] <= 10^9
```
