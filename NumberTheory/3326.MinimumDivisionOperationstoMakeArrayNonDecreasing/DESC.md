# 3326. Minimum Division Operations to Make Array Non Decreasing

You are given an integer array **nums**.

Any positive divisor of a natural number `x` that is strictly less than `x` is called a **proper divisor** of `x`.

Example:

- `2` is a proper divisor of `4`
- `6` is **not** a proper divisor of `6`

---

## Operation

You may perform the following operation any number of times:

1. Select any element `nums[i]`
2. Divide it by its **greatest proper divisor**

---

## Goal

Return the **minimum number of operations** required to make the array **non‑decreasing**.

An array is non‑decreasing if:

```
nums[i] <= nums[i+1]
```

for every valid index `i`.

If it is **impossible** to make the array non‑decreasing, return:

```
-1
```

---

# Example 1

## Input

```
nums = [25,7]
```

## Output

```
1
```

## Explanation

The greatest proper divisor of `25` is `5`.

Perform one operation:

```
25 / 5 = 5
```

Array becomes:

```
[5,7]
```

The array is now non‑decreasing.

---

# Example 2

## Input

```
nums = [7,7,6]
```

## Output

```
-1
```

## Explanation

There is no sequence of operations that can make the array non‑decreasing.

---

# Example 3

## Input

```
nums = [1,1,1,1]
```

## Output

```
0
```

## Explanation

The array is already non‑decreasing, so no operations are required.

---

# Constraints

```
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^6
```

---
