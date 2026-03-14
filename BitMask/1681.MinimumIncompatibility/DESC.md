# 1681. Minimum Incompatibility

## Problem Description

You are given:

- An integer array `nums`
- An integer `k`

You must divide the array into **k subsets of equal size** such that:

1. **No subset contains duplicate elements**
2. Each subset has the same number of elements
3. Every element from `nums` must be used

The **incompatibility** of a subset is defined as:

```
max(subset) - min(subset)
```

Your goal is to **minimize the total incompatibility** across all `k` subsets.

If it is impossible to divide the array according to the rules, return **-1**.

---

# Definitions

### Subset

A subset is a group of integers taken from the array. Order does not matter.

### Incompatibility

For a subset:

```
incompatibility = max value − min value
```

Example:

```
subset = [1,4,2]
max = 4
min = 1
incompatibility = 3
```

---

# Example 1

## Input

```
nums = [1,2,1,4]
k = 2
```

## Output

```
4
```

## Explanation

Optimal subsets:

```
[1,2]
[1,4]
```

Incompatibility:

```
(2 - 1) + (4 - 1) = 1 + 3 = 4
```

Note:

```
[1,1] and [2,4]
```

would produce a smaller value but is **invalid** because duplicates are not allowed in a subset.

---

# Example 2

## Input

```
nums = [6,3,8,1,3,1,2,2]
k = 4
```

## Output

```
6
```

## Explanation

Optimal grouping:

```
[1,2]
[2,3]
[6,8]
[1,3]
```

Incompatibility:

```
(2-1) + (3-2) + (8-6) + (3-1)
= 1 + 1 + 2 + 2
= 6
```

---

# Example 3

## Input

```
nums = [5,3,3,6,3,3]
k = 3
```

## Output

```
-1
```

## Explanation

It is impossible to divide the array into 3 subsets of equal size without placing duplicate values in a subset.

---

# Constraints

```
1 <= k <= nums.length <= 16
```

```
nums.length is divisible by k
```

```
1 <= nums[i] <= nums.length
```

---

# Key Observations

Important constraints:

```
nums.length ≤ 16
```

This strongly suggests that **bitmask dynamic programming** or **subset enumeration** will be the intended approach.

Typical strategies used to solve this problem:

- Bitmask DP
- Precomputing valid subsets
- State compression
- Memoization
