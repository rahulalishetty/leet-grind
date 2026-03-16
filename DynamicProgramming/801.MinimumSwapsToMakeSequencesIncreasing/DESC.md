# 801. Minimum Swaps To Make Sequences Increasing

## Problem Description

You are given two integer arrays of the same length:

- `nums1`
- `nums2`

In one operation, you are allowed to **swap the elements at the same index** between the two arrays.

Example swap:

```
nums1 = [1,2,3,8]
nums2 = [5,6,7,4]

swap index 3
```

Result:

```
nums1 = [1,2,3,4]
nums2 = [5,6,7,8]
```

Your goal is to **make both arrays strictly increasing** using the **minimum number of swaps**.

---

## Definition: Strictly Increasing

An array `arr` is strictly increasing if:

```
arr[0] < arr[1] < arr[2] < ... < arr[n-1]
```

---

## Goal

Return the **minimum number of swaps** required so that **both arrays become strictly increasing**.

The problem guarantees that a valid solution always exists.

---

## Example 1

### Input

```
nums1 = [1,3,5,4]
nums2 = [1,2,3,7]
```

### Output

```
1
```

### Explanation

Swap elements at index `3`.

Before swap:

```
nums1 = [1,3,5,4]
nums2 = [1,2,3,7]
```

After swap:

```
nums1 = [1,3,5,7]
nums2 = [1,2,3,4]
```

Both arrays are now strictly increasing.

---

## Example 2

### Input

```
nums1 = [0,3,5,8,9]
nums2 = [2,1,4,6,9]
```

### Output

```
1
```

---

## Constraints

```
2 <= nums1.length <= 10^5
nums2.length == nums1.length
0 <= nums1[i], nums2[i] <= 2 * 10^5
```

---
