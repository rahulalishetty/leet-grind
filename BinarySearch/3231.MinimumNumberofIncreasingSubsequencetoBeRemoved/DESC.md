# 3231. Minimum Number of Increasing Subsequence to Be Removed

## Problem Description

You are given an integer array `nums`.

You may perform the following operation any number of times:

- Remove a **strictly increasing subsequence** from the array.

A **subsequence** is a sequence that can be derived from the array by deleting some or no elements **without changing the order of the remaining elements**.

Your goal is to determine the **minimum number of operations required to remove all elements from the array**.

---

## Example 1

### Input

```
nums = [5,3,1,4,2]
```

### Output

```
3
```

### Explanation

We can remove the following increasing subsequences:

```
[1, 2]
[3, 4]
[5]
```

Total operations = **3**.

---

## Example 2

### Input

```
nums = [1,2,3,4,5]
```

### Output

```
1
```

### Explanation

The whole array is already strictly increasing, so it can be removed in **one operation**.

---

## Example 3

### Input

```
nums = [5,4,3,2,1]
```

### Output

```
5
```

### Explanation

No two elements form a strictly increasing subsequence, so each element must be removed individually.

```
[5], [4], [3], [2], [1]
```

Total operations = **5**.

---

## Constraints

```
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^5
```

---

## Notes

- A subsequence must be **strictly increasing**, meaning each next element must be greater than the previous one.
- You may remove subsequences **in any order**.
- The goal is to **minimize the number of subsequences removed**.
