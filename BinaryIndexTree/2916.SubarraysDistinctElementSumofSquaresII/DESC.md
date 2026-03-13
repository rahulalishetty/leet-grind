# 2916. Subarrays Distinct Element Sum of Squares II

## Problem Statement

You are given a **0-indexed** integer array `nums`.

The **distinct count** of a subarray `nums[i..j]` is defined as the number of distinct values present in that subarray.

Formally, for a subarray:

```text
nums[i..j], where 0 <= i <= j < nums.length
```

the number of unique values inside that subarray is called its **distinct count**.

Return the **sum of the squares of the distinct counts** of **all subarrays** of `nums`.

Since the answer may be very large, return it modulo:

```text
10^9 + 7
```

A **subarray** is a contiguous non-empty sequence of elements within an array.

---

## Example 1

### Input

```text
nums = [1,2,1]
```

### Output

```text
15
```

### Explanation

The six subarrays are:

- `[1]` → 1 distinct value
- `[2]` → 1 distinct value
- `[1]` → 1 distinct value
- `[1,2]` → 2 distinct values
- `[2,1]` → 2 distinct values
- `[1,2,1]` → 2 distinct values

Sum of squares:

```text
1^2 + 1^2 + 1^2 + 2^2 + 2^2 + 2^2
= 1 + 1 + 1 + 4 + 4 + 4
= 15
```

---

## Example 2

### Input

```text
nums = [2,2]
```

### Output

```text
3
```

### Explanation

The three subarrays are:

- `[2]` → 1 distinct value
- `[2]` → 1 distinct value
- `[2,2]` → 1 distinct value

Sum of squares:

```text
1^2 + 1^2 + 1^2 = 3
```

---

## Constraints

```text
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^5
```
