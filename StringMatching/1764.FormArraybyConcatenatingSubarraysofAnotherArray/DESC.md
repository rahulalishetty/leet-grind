# 1764. Form Array by Concatenating Subarrays of Another Array

## Problem

You are given a **2D integer array `groups`** of length `n`.
You are also given an integer array **`nums`**.

Your task is to determine whether you can choose **n disjoint subarrays** from `nums` such that:

- The **i-th subarray** is equal to `groups[i]` (0-indexed).
- If `i > 0`, the `(i-1)`th subarray must appear **before** the `i`th subarray in `nums`.

Return **true** if this is possible, otherwise return **false**.

A **subarray** is a contiguous sequence of elements within an array.

Two subarrays are **disjoint** if there is **no index `k`** such that `nums[k]` belongs to more than one subarray.

---

## Example 1

**Input**

```
groups = [[1,-1,-1],[3,-2,0]]
nums = [1,-1,0,1,-1,-1,3,-2,0]
```

**Output**

```
true
```

**Explanation**

We can choose:

- `[1,-1,-1]` from indices `[3,4,5]`
- `[3,-2,0]` from indices `[6,7,8]`

They appear in the correct order and are disjoint.

---

## Example 2

**Input**

```
groups = [[10,-2],[1,2,3,4]]
nums = [1,2,3,4,10,-2]
```

**Output**

```
false
```

**Explanation**

`[10,-2]` must appear **before** `[1,2,3,4]` in `nums`, which is not the case.

---

## Example 3

**Input**

```
groups = [[1,2,3],[3,4]]
nums = [7,7,1,2,3,4,7,7]
```

**Output**

```
false
```

**Explanation**

The two chosen subarrays would overlap at element `nums[4]`, so they are **not disjoint**.

---

## Constraints

- `groups.length == n`
- `1 <= n <= 10^3`
- `1 <= groups[i].length`
- `sum(groups[i].length) <= 10^3`
- `1 <= nums.length <= 10^3`
- `-10^7 <= groups[i][j], nums[k] <= 10^7`
