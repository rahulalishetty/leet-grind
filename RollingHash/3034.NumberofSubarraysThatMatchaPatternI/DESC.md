# 3034. Number of Subarrays That Match a Pattern I

## Problem Statement

You are given:

- A **0-indexed integer array** `nums` of size `n`
- A **0-indexed integer array** `pattern` of size `m` consisting of values `-1`, `0`, and `1`

A subarray `nums[i..j]` of size `m + 1` is said to **match the pattern** if the following conditions hold for each `k` in `pattern`:

- `nums[i + k + 1] > nums[i + k]` if `pattern[k] == 1`
- `nums[i + k + 1] == nums[i + k]` if `pattern[k] == 0`
- `nums[i + k + 1] < nums[i + k]` if `pattern[k] == -1`

Return the **number of subarrays in `nums` that match the given pattern**.

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

The pattern `[1,1]` means we want a **strictly increasing sequence** of length `3`.

Matching subarrays:

```
[1,2,3]
[2,3,4]
[3,4,5]
[4,5,6]
```

Total = **4**.

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

The pattern `[1,0,-1]` means:

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

Total = **2**.

---

## Constraints

- `2 <= n == nums.length <= 100`
- `1 <= nums[i] <= 10^9`
- `1 <= m == pattern.length < n`
- `-1 <= pattern[i] <= 1`
