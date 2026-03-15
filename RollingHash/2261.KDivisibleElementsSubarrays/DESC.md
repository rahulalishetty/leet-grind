# 2261. K Divisible Elements Subarrays

## Problem Statement

Given an integer array `nums` and two integers `k` and `p`, return the number of **distinct subarrays** that contain **at most `k` elements divisible by `p`**.

Two arrays `nums1` and `nums2` are considered distinct if:

- They have different lengths, or
- There exists at least one index `i` such that `nums1[i] != nums2[i]`

A **subarray** is a non-empty contiguous sequence of elements in an array.

---

## Example 1

**Input:**

```text
nums = [2,3,3,2,2], k = 2, p = 2
```

**Output:**

```text
11
```

**Explanation:**

The elements divisible by `p = 2` are at indices `0`, `3`, and `4`.

The 11 distinct valid subarrays are:

- `[2]`
- `[2,3]`
- `[2,3,3]`
- `[2,3,3,2]`
- `[3]`
- `[3,3]`
- `[3,3,2]`
- `[3,3,2,2]`
- `[3,2]`
- `[3,2,2]`
- `[2,2]`

Note that `[2]` and `[3]` appear more than once in `nums`, but each is counted only once.

The subarray `[2,3,3,2,2]` is **not valid** because it contains `3` elements divisible by `2`, which is more than `k = 2`.

---

## Example 2

**Input:**

```text
nums = [1,2,3,4], k = 4, p = 1
```

**Output:**

```text
10
```

**Explanation:**

Since every element is divisible by `1`, every subarray contains at most `4` divisible elements.

All subarrays are distinct, so the answer is the total number of subarrays:

```text
10
```

---

## Constraints

- `1 <= nums.length <= 200`
- `1 <= nums[i], p <= 200`
- `1 <= k <= nums.length`

---

## Follow Up

Can you solve this problem in:

```text
O(n^2)
```

time complexity?
