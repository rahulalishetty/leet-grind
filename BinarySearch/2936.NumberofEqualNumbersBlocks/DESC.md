# 2936. Number of Equal Numbers Blocks

## Problem Description

You are given a **0-indexed array of integers `nums`** with the following property:

> All occurrences of the same value are **adjacent**.

Formally:

If there exist indices `i < j` such that:

```
nums[i] == nums[j]
```

then for **every index `k` where `i < k < j`**:

```
nums[k] == nums[i]
```

This means equal values appear in **continuous blocks**.

---

## BigArray Interface

The array is **very large**, so it cannot be accessed directly.

Instead, an API `BigArray` is provided:

```
int at(long long index)
    → Returns nums[index]

long long size()
    → Returns nums.length
```

---

## Goal

Partition the array into **maximal blocks of equal values**.

Return the **number of such blocks**.

A block is defined as:

```
a maximal contiguous subarray where all values are equal
```

---

# Example 1

Input

```
nums = [3,3,3,3,3]
```

Output

```
1
```

Explanation

All elements are the same, so the whole array forms **one block**:

```
[3,3,3,3,3]
```

---

# Example 2

Input

```
nums = [1,1,1,3,9,9,9,2,10,10]
```

Output

```
5
```

Explanation

Blocks are:

```
[1,1,1]
[3]
[9,9,9]
[2]
[10,10]
```

Total blocks:

```
5
```

---

# Example 3

Input

```
nums = [1,2,3,4,5,6,7]
```

Output

```
7
```

Explanation

All values are distinct, so each element forms its own block.

```
[1]
[2]
[3]
[4]
[5]
[6]
[7]
```

Total blocks:

```
7
```

---

# Constraints

```
1 <= nums.length <= 10^15
```

```
1 <= nums[i] <= 10^9
```

Additional guarantees:

- Equal values are **always adjacent**
- The sum of elements of `nums` ≤ `10^15`
- Custom testing behavior is undefined for arrays larger than length 10
