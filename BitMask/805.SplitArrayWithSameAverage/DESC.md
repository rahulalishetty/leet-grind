# 805. Split Array With Same Average

## Problem Description

You are given an integer array `nums`.

Your task is to split the elements of `nums` into **two non-empty arrays** `A` and `B` such that:

```
average(A) == average(B)
```

Where:

```
average(arr) = sum(arr) / length(arr)
```

Return **true** if such a split is possible, otherwise return **false**.

---

## Example 1

### Input

```
nums = [1,2,3,4,5,6,7,8]
```

### Output

```
true
```

### Explanation

One valid split is:

```
A = [1,4,5,8]
B = [2,3,6,7]
```

Both subsets have the same average:

```
average(A) = (1+4+5+8)/4 = 18/4 = 4.5
average(B) = (2+3+6+7)/4 = 18/4 = 4.5
```

---

## Example 2

### Input

```
nums = [3,1]
```

### Output

```
false
```

### Explanation

There is no way to split the array into two non-empty subsets with equal averages.

---

## Constraints

```
1 <= nums.length <= 30
```

```
0 <= nums[i] <= 10^4
```

---

## Notes

- Both subsets **must be non-empty**.
- The averages of the two subsets must be **exactly equal**.
- The order of elements in the subsets does not matter.
