# 698. Partition to K Equal Sum Subsets

## Problem Description

Given an integer array `nums` and an integer `k`, determine whether it is possible to divide the array into **k non-empty subsets** such that the **sum of elements in each subset is equal**.

If it is possible, return **true**, otherwise return **false**.

---

## Example 1

### Input

```
nums = [4,3,2,3,5,2,1]
k = 4
```

### Output

```
true
```

### Explanation

It is possible to partition the array into the following 4 subsets:

```
(5)
(1,4)
(2,3)
(2,3)
```

Each subset has the same sum.

---

## Example 2

### Input

```
nums = [1,2,3,4]
k = 3
```

### Output

```
false
```

### Explanation

It is not possible to divide the array into 3 subsets with equal sums.

---

## Constraints

```
1 <= k <= nums.length <= 16
```

```
1 <= nums[i] <= 10^4
```

```
The frequency of each element is in the range [1, 4]
```

---
