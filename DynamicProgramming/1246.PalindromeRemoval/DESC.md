# 1246. Palindrome Removal

## Problem Description

You are given an integer array `arr`.

In one move, you can select a **palindromic subarray**:

```
arr[i], arr[i + 1], ..., arr[j]
```

where `i <= j`, and remove that subarray from the array.

After removing the subarray, the elements on the left and right shift together to fill the gap.

Your task is to determine the **minimum number of moves** required to remove **all elements** from the array.

---

## Examples

### Example 1

Input:

```
arr = [1,2]
```

Output:

```
2
```

Explanation:

Possible moves:

1. Remove `[1]`
2. Remove `[2]`

Total moves = 2.

---

### Example 2

Input:

```
arr = [1,3,4,1,5]
```

Output:

```
3
```

Explanation:

One optimal sequence of moves:

1. Remove `[4]`
2. Remove `[1,3,1]`
3. Remove `[5]`

Total moves = 3.

---

## Constraints

```
1 <= arr.length <= 100
1 <= arr[i] <= 20
```

---

## Key Observation

The chosen subarray must be a **palindrome**.
Because removing elements changes the structure of the array, the optimal strategy often requires **dynamic programming over intervals**.

This problem is commonly solved using **interval DP with O(n^3) time complexity**.
