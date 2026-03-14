# 1879. Minimum XOR Sum of Two Arrays

## Problem Description

You are given two integer arrays:

```
nums1
nums2
```

Both arrays have the same length `n`.

The **XOR sum** of the two arrays is defined as:

```
(nums1[0] XOR nums2[0]) +
(nums1[1] XOR nums2[1]) +
...
(nums1[n-1] XOR nums2[n-1])
```

You are allowed to **rearrange the elements of `nums2`** in any order.

Your goal is to **minimize the resulting XOR sum** after rearranging `nums2`.

Return the **minimum possible XOR sum**.

---

# Example 1

### Input

```
nums1 = [1,2]
nums2 = [2,3]
```

### Output

```
2
```

### Explanation

Rearrange `nums2`:

```
nums2 = [3,2]
```

Compute XOR sum:

```
(1 XOR 3) + (2 XOR 2)
= 2 + 0
= 2
```

---

# Example 2

### Input

```
nums1 = [1,0,3]
nums2 = [5,3,4]
```

### Output

```
8
```

### Explanation

Rearrange `nums2`:

```
nums2 = [5,4,3]
```

XOR sum:

```
(1 XOR 5) + (0 XOR 4) + (3 XOR 3)
= 4 + 4 + 0
= 8
```

---

# Constraints

```
n == nums1.length
n == nums2.length
```

```
1 <= n <= 14
```

```
0 <= nums1[i], nums2[i] <= 10^7
```

---

# Key Observations

This is essentially a **minimum assignment problem**:

We want to assign each element in `nums1` to exactly one element in `nums2` such that the total XOR cost is minimized.

Equivalent formulation:

```
Minimize:
sum(nums1[i] XOR nums2[p[i]])
```

Where `p` is a permutation of indices of `nums2`.

---

# Important Insight

The constraint:

```
n <= 14
```

suggests that **bitmask dynamic programming** is an appropriate technique.

Total possible assignments:

```
n!  (too large)
```

But using bitmask DP:

```
2^n * n
```

which is feasible for `n = 14`.

---

# Problem Type

This problem belongs to the category:

```
Assignment Problem
Bitmask DP
Minimum Matching
```

Typical solution strategies include:

- Bitmask Dynamic Programming
- DFS + Memoization
- State Compression DP

---

# Summary

Goal:

```
Rearrange nums2 to minimize total XOR sum with nums1
```

Key technique:

```
Bitmask DP
```

Reason:

```
n ≤ 14
```

allows efficient exploration of subsets of assignments.
