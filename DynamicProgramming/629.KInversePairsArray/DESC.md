# 629. K Inverse Pairs Array

## Problem Description

For an integer array `nums`, an **inverse pair** is a pair of indices `[i, j]` such that:

```
0 <= i < j < nums.length
and
nums[i] > nums[j]
```

Given two integers **n** and **k**, return the number of different arrays consisting of numbers from **1 to n** such that there are **exactly k inverse pairs**.

Because the result can be very large, return the answer modulo:

```
10^9 + 7
```

---

## Example 1

### Input

```
n = 3
k = 0
```

### Output

```
1
```

### Explanation

Only the array:

```
[1, 2, 3]
```

has **0 inverse pairs**.

---

## Example 2

### Input

```
n = 3
k = 1
```

### Output

```
2
```

### Explanation

The arrays with exactly **1 inverse pair** are:

```
[1, 3, 2]
[2, 1, 3]
```

---

## Constraints

```
1 <= n <= 1000
0 <= k <= 1000
```

---
