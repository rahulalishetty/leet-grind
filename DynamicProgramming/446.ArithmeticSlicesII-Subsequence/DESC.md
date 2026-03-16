# 446. Arithmetic Slices II - Subsequence

## Problem Statement

Given an integer array **nums**, return the **number of all arithmetic subsequences** of `nums`.

A sequence of numbers is called **arithmetic** if:

- It contains **at least three elements**
- The **difference between every pair of consecutive elements is the same**

---

## Definition of Arithmetic Sequence

Examples of arithmetic sequences:

```
[1, 3, 5, 7, 9]
[7, 7, 7, 7]
[3, -1, -5, -9]
```

Examples of non-arithmetic sequences:

```
[1, 1, 2, 5, 7]
```

The difference between consecutive elements must remain **constant**.

---

## Subsequence Definition

A **subsequence** of an array is a sequence that can be formed by **removing some elements without changing the order** of the remaining elements.

Example:

```
[2,5,10] is a subsequence of [1,2,1,2,4,1,5,10]
```

Elements do **not need to be contiguous**, but **order must be preserved**.

---

## Goal

Return the **total number of arithmetic subsequences** in the array.

Conditions:

- Each subsequence must contain **at least 3 elements**
- The subsequence must maintain the **original order of elements**

The result is guaranteed to **fit in a 32-bit integer**.

---

## Example 1

### Input

```
nums = [2,4,6,8,10]
```

### Output

```
7
```

### Explanation

All arithmetic subsequences are:

```
[2,4,6]
[4,6,8]
[6,8,10]
[2,4,6,8]
[4,6,8,10]
[2,4,6,8,10]
[2,6,10]
```

Total = **7**

---

## Example 2

### Input

```
nums = [7,7,7,7,7]
```

### Output

```
16
```

### Explanation

Since all numbers are equal, **every subsequence of length ≥ 3 is arithmetic**.

Total arithmetic subsequences = **16**.

---

## Constraints

- `1 <= nums.length <= 1000`
- `-2^31 <= nums[i] <= 2^31 - 1`

---

## Key Observations

- A valid arithmetic subsequence must have **at least three elements**
- The **difference between consecutive elements must remain constant**
- The subsequence **does not need to be contiguous**
- The array size can be up to **1000**, which makes brute-force enumeration infeasible

---

## Problem Summary

Given an array:

```
nums = [a1, a2, a3, ..., an]
```

We must count all subsequences:

```
[a_i1, a_i2, a_i3, ..., a_ik]
```

such that:

- `k ≥ 3`
- `a_i2 - a_i1 = a_i3 - a_i2 = ...`

Return the **total number of such arithmetic subsequences**.
