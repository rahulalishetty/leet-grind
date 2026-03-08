# 2393. Count Strictly Increasing Subarrays

## Problem Description

You are given an array `nums` consisting of **positive integers**.

Return the **number of subarrays** of `nums` that are in **strictly increasing order**.

A **subarray** is a **contiguous part** of an array.

---

## Examples

### Example 1

**Input**

```
nums = [1, 3, 5, 4, 4, 6]
```

**Output**

```
10
```

**Explanation**

The strictly increasing subarrays are:

**Subarrays of length 1**

```
[1], [3], [5], [4], [4], [6]
```

**Subarrays of length 2**

```
[1,3], [3,5], [4,6]
```

**Subarrays of length 3**

```
[1,3,5]
```

Total number of strictly increasing subarrays:

```
6 + 3 + 1 = 10
```

---

### Example 2

**Input**

```
nums = [1,2,3,4,5]
```

**Output**

```
15
```

**Explanation**

Every subarray is strictly increasing.

Total number of possible subarrays:

```
n(n + 1) / 2
= 5 × 6 / 2
= 15
```

---

## Constraints

- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^6`
  """
