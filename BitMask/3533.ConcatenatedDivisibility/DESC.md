# 3533. Concatenated Divisibility

## Problem Statement

You are given:

- An array of positive integers `nums`
- A positive integer `k`

A **permutation** of `nums` forms a **divisible concatenation** if the number formed by concatenating the decimal representations of the numbers in that order is **divisible by `k`**.

Your task is to return the **lexicographically smallest permutation** (considered as a list of integers) that forms a divisible concatenation.

If **no such permutation exists**, return an empty list.

---

## Definition

If a permutation is:

```
[a, b, c]
```

The concatenated value is:

```
abc
```

For example:

```
[3, 12, 45] → 31245
```

The result is valid if:

```
31245 % k == 0
```

---

# Example 1

## Input

```
nums = [3,12,45]
k = 5
```

## Output

```
[3,12,45]
```

## Explanation

| Permutation | Concatenated Value | Divisible by 5 |
| ----------- | ------------------ | -------------- |
| [3, 12, 45] | 31245              | Yes            |
| [3, 45, 12] | 34512              | No             |
| [12, 3, 45] | 12345              | Yes            |
| [12, 45, 3] | 12453              | No             |
| [45, 3, 12] | 45312              | No             |
| [45, 12, 3] | 45123              | No             |

The **lexicographically smallest valid permutation** is:

```
[3,12,45]
```

---

# Example 2

## Input

```
nums = [10,5]
k = 10
```

## Output

```
[5,10]
```

## Explanation

| Permutation | Concatenated Value | Divisible by 10 |
| ----------- | ------------------ | --------------- |
| [5,10]      | 510                | Yes             |
| [10,5]      | 105                | No              |

The lexicographically smallest valid permutation is:

```
[5,10]
```

---

# Example 3

## Input

```
nums = [1,2,3]
k = 5
```

## Output

```
[]
```

## Explanation

No permutation of `nums` forms a concatenated number divisible by `5`, so the result is an empty list.

---

# Constraints

```
1 <= nums.length <= 13
1 <= nums[i] <= 100000
1 <= k <= 100
```
