# 3036. Number of Subarrays That Match a Pattern II

## Problem Statement

You are given:

- A **0-indexed integer array** `nums` of size `n`
- A **0-indexed integer array** `pattern` of size `m` consisting of integers `-1`, `0`, and `1`

A subarray `nums[i..j]` of size `m + 1` is said to **match the pattern** if the following conditions hold for each `k`:

- `nums[i + k + 1] > nums[i + k]` if `pattern[k] == 1`
- `nums[i + k + 1] == nums[i + k]` if `pattern[k] == 0`
- `nums[i + k + 1] < nums[i + k]` if `pattern[k] == -1`

Return the **number of subarrays in `nums` that match the pattern**.

---

## Example 1

### Input

```
nums = [1,2,3,4,5,6]
pattern = [1,1]
```

### Output

```
4
```

### Explanation

The pattern `[1,1]` indicates that we are looking for strictly increasing subarrays of size `3`.

Matching subarrays:

```
[1,2,3]
[2,3,4]
[3,4,5]
[4,5,6]
```

Hence, the answer is **4**.

---

## Example 2

### Input

```
nums = [1,4,4,1,3,5,5,3]
pattern = [1,0,-1]
```

### Output

```
2
```

### Explanation

The pattern `[1,0,-1]` indicates the following relationships:

```
nums[i]   < nums[i+1]
nums[i+1] == nums[i+2]
nums[i+2] > nums[i+3]
```

Matching subarrays:

```
[1,4,4,1]
[3,5,5,3]
```

Hence, the answer is **2**.

---

## Constraints

- `2 <= n == nums.length <= 10^6`
- `1 <= nums[i] <= 10^9`
- `1 <= m == pattern.length < n`
- `-1 <= pattern[i] <= 1`
