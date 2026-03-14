# 1755. Closest Subsequence Sum

## Problem Description

You are given:

- An integer array `nums`
- An integer `goal`

You need to choose a **subsequence** of `nums` such that the sum of the chosen elements is **as close as possible to `goal`**.

The objective is to minimize:

```
abs(sum - goal)
```

Where:

- `sum` is the sum of the chosen subsequence.

A **subsequence** is obtained by removing zero or more elements from the array without changing the order of the remaining elements.

Return the **minimum possible value of `abs(sum - goal)`**.

---

# Example 1

## Input

```
nums = [5,-7,3,5]
goal = 6
```

## Output

```
0
```

## Explanation

Choose the whole array:

```
[5, -7, 3, 5]
sum = 6
```

```
abs(6 - 6) = 0
```

---

# Example 2

## Input

```
nums = [7,-9,15,-2]
goal = -5
```

## Output

```
1
```

## Explanation

Choose subsequence:

```
[7, -9, -2]
sum = -4
```

Difference:

```
abs(-4 - (-5)) = 1
```

---

# Example 3

## Input

```
nums = [1,2,3]
goal = -7
```

## Output

```
7
```

## Explanation

The closest sum is `0` (empty subsequence):

```
abs(0 - (-7)) = 7
```

---

# Constraints

```
1 <= nums.length <= 40
```

```
-10^7 <= nums[i] <= 10^7
```

```
-10^9 <= goal <= 10^9
```

---

# Key Observations

Important constraint:

```
nums.length <= 40
```

If we tried brute force over all subsequences:

```
2^40 ≈ 1 trillion
```

This is too large.

A standard technique for this problem is:

```
Meet-in-the-Middle
```

This works by:

1. Splitting the array into two halves
2. Computing all subset sums for each half
3. Combining the results efficiently (often using binary search)

This reduces the complexity to roughly:

```
O(2^(n/2) log 2^(n/2))
```

which is feasible for `n = 40`.

Other strategies sometimes used:

- Meet-in-the-middle with two-pointer search
- Binary search on subset sums
- Bitmask enumeration of halves
