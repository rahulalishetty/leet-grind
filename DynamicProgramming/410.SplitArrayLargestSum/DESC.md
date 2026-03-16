# 410. Split Array Largest Sum

## Problem Statement

Given an integer array **nums** and an integer **k**, split `nums` into **k non-empty subarrays** such that the **largest sum of any subarray is minimized**.

Return the **minimum possible value of the largest subarray sum**.

A **subarray** is a contiguous part of the array.

---

## Example 1

**Input**

```
nums = [7,2,5,10,8]
k = 2
```

**Output**

```
18
```

**Explanation**

There are four possible ways to split the array into two subarrays:

```
[7] [2,5,10,8]      -> largest sum = 25
[7,2] [5,10,8]      -> largest sum = 23
[7,2,5] [10,8]      -> largest sum = 18  ✓ optimal
[7,2,5,10] [8]      -> largest sum = 24
```

The best split is:

```
[7,2,5] and [10,8]
```

The minimized largest subarray sum is **18**.

---

## Example 2

**Input**

```
nums = [1,2,3,4,5]
k = 2
```

**Output**

```
9
```

**Explanation**

Possible splits:

```
[1] [2,3,4,5]      -> largest sum = 14
[1,2] [3,4,5]      -> largest sum = 12
[1,2,3] [4,5]      -> largest sum = 9  ✓ optimal
[1,2,3,4] [5]      -> largest sum = 10
```

The best split is:

```
[1,2,3] and [4,5]
```

The minimized largest subarray sum is **9**.

---

## Constraints

- `1 <= nums.length <= 1000`
- `0 <= nums[i] <= 10^6`
- `1 <= k <= min(50, nums.length)`

---

## Key Idea

We must divide the array into **k contiguous subarrays** while minimizing the **largest subarray sum**.

This means:

- Every element must belong to **exactly one subarray**
- Each subarray must be **non-empty**
- The goal is to **balance the sums** so that the maximum subarray sum is as small as possible.

---

## Problem Summary

Given:

```
nums = array of integers
k = number of subarrays
```

Find the **minimum possible value of the largest subarray sum** after splitting the array into **k contiguous parts**.
