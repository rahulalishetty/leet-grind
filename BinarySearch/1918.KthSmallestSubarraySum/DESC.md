# 1918. Kth Smallest Subarray Sum

## Problem Description

Given an integer array `nums` of length `n` and an integer `k`, return the **kth smallest subarray sum**.

A **subarray** is defined as a **non-empty contiguous sequence** of elements in an array.

The **subarray sum** is the sum of all elements in that subarray.

---

## Example 1

Input:

```
nums = [2,1,3]
k = 4
```

Output:

```
3
```

Explanation:

All subarrays of `[2,1,3]`:

```
[2] → sum = 2
[1] → sum = 1
[3] → sum = 3
[2,1] → sum = 3
[1,3] → sum = 4
[2,1,3] → sum = 6
```

Sorted subarray sums:

```
1, 2, 3, 3, 4, 6
```

The **4th smallest** sum is:

```
3
```

---

## Example 2

Input:

```
nums = [3,3,5,5]
k = 7
```

Output:

```
10
```

Explanation:

All subarrays of `[3,3,5,5]`:

```
[3] → 3
[3] → 3
[5] → 5
[5] → 5
[3,3] → 6
[3,5] → 8
[5,5] → 10
[3,3,5] → 11
[3,5,5] → 13
[3,3,5,5] → 16
```

Sorted sums:

```
3, 3, 5, 5, 6, 8, 10, 11, 13, 16
```

The **7th smallest** sum is:

```
10
```

---

## Constraints

```
n == nums.length
```

```
1 ≤ n ≤ 2 * 10^4
```

```
1 ≤ nums[i] ≤ 5 * 10^4
```

```
1 ≤ k ≤ n * (n + 1) / 2
```
