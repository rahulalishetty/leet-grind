# 2748. Number of Beautiful Pairs

## Problem Statement

You are given a **0-indexed integer array `nums`**.

A pair of indices `(i, j)` is called **beautiful** if:

```
0 <= i < j < nums.length
```

and the **first digit of `nums[i]`** and the **last digit of `nums[j]`** are **coprime**.

Two integers `x` and `y` are **coprime** if:

```
gcd(x, y) == 1
```

where `gcd(x, y)` is the **greatest common divisor** of `x` and `y`.

Return the **total number of beautiful pairs** in the array.

---

## Definitions

### Coprime Numbers

Two integers `x` and `y` are coprime if **no integer greater than 1 divides both of them**.

Example:

```
gcd(2,5) = 1  → coprime
gcd(6,8) = 2  → not coprime
```

---

# Example 1

## Input

```
nums = [2,5,1,4]
```

## Output

```
5
```

## Explanation

Beautiful pairs:

```
(i=0, j=1) → first digit 2, last digit 5 → gcd(2,5)=1
(i=0, j=2) → first digit 2, last digit 1 → gcd(2,1)=1
(i=1, j=2) → first digit 5, last digit 1 → gcd(5,1)=1
(i=1, j=3) → first digit 5, last digit 4 → gcd(5,4)=1
(i=2, j=3) → first digit 1, last digit 4 → gcd(1,4)=1
```

Total beautiful pairs:

```
5
```

---

# Example 2

## Input

```
nums = [11,21,12]
```

## Output

```
2
```

## Explanation

Beautiful pairs:

```
(i=0, j=1) → first digit 1, last digit 1 → gcd(1,1)=1
(i=0, j=2) → first digit 1, last digit 2 → gcd(1,2)=1
```

Total beautiful pairs:

```
2
```

---

# Constraints

```
2 <= nums.length <= 100
1 <= nums[i] <= 9999
nums[i] % 10 != 0
```

---
