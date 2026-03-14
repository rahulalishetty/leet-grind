# 996. Number of Squareful Arrays

## Problem Description

An array is **squareful** if the sum of every pair of adjacent elements is a **perfect square**.

Given an integer array `nums`, return the **number of permutations of `nums`** that are squareful.

Two permutations `perm1` and `perm2` are considered different if there exists some index `i` such that:

```
perm1[i] != perm2[i]
```

---

## Example 1

### Input

```
nums = [1,17,8]
```

### Output

```
2
```

### Explanation

Valid squareful permutations:

```
[1,8,17]
[17,8,1]
```

Because:

```
1 + 8 = 9  -> perfect square
8 + 17 = 25 -> perfect square
```

---

## Example 2

### Input

```
nums = [2,2,2]
```

### Output

```
1
```

---

## Constraints

```
1 <= nums.length <= 12
```

```
0 <= nums[i] <= 10^9
```

---

## Notes

- Every adjacent pair must sum to a **perfect square**.
- All permutations of the array must be checked.
- Duplicate numbers may exist in the array, but permutations that produce identical sequences are counted only once.
