# 2941. Maximum GCD-Sum of a Subarray

## Problem Description

You are given:

- An integer array `nums`
- An integer `k`

We define the **gcd-sum** of an array `a` as follows:

1. Let `s` be the **sum of all elements** of the array.
2. Let `g` be the **greatest common divisor (GCD)** of all elements of the array.

The gcd-sum is defined as:

```
gcd-sum = s * g
```

Your task is to **find the maximum gcd-sum among all subarrays of `nums` that contain at least `k` elements**.

---

# Example 1

Input

```
nums = [2,1,4,4,4,2]
k = 2
```

Output

```
48
```

Explanation

Consider the subarray:

```
[4,4,4]
```

- Sum = `4 + 4 + 4 = 12`
- GCD = `4`

So:

```
gcd-sum = 12 * 4 = 48
```

No other subarray produces a larger gcd-sum.

---

# Example 2

Input

```
nums = [7,3,9,4]
k = 1
```

Output

```
81
```

Explanation

Choose the subarray:

```
[9]
```

- Sum = `9`
- GCD = `9`

```
gcd-sum = 9 * 9 = 81
```

No other subarray has a larger gcd-sum.

---

# Constraints

```
n == nums.length
1 <= n <= 10^5
```

```
1 <= nums[i] <= 10^6
```

```
1 <= k <= n
```

---

# Notes

- A **subarray** is a contiguous sequence of elements in the array.
- The subarray must contain **at least `k` elements**.
- Efficient computation is required because `n` can be as large as **100,000**.
