# 2614. Prime In Diagonal

## Problem Statement

You are given a **0-indexed two-dimensional integer array `nums`**.

Return the **largest prime number** that lies on **at least one of the diagonals** of `nums`.

If **no prime number exists on any diagonal**, return **0**.

---

## Prime Number Definition

An integer is **prime** if:

- It is **greater than 1**
- It has **no positive divisors other than 1 and itself**

---

## Diagonal Definition

An element `val` lies on one of the diagonals if:

```
nums[i][i] = val
```

or

```
nums[i][n - i - 1] = val
```

where `n = nums.length`.

Thus the diagonals are:

- **Primary diagonal** → `nums[i][i]`
- **Secondary diagonal** → `nums[i][n - i - 1]`

---

## Example

For matrix:

```
[1 2 3
 4 5 6
 7 8 9]
```

Primary diagonal:

```
[1, 5, 9]
```

Secondary diagonal:

```
[3, 5, 7]
```

---

# Example 1

## Input

```
nums = [[1,2,3],
        [5,6,7],
        [9,10,11]]
```

## Output

```
11
```

## Explanation

Numbers on diagonals:

```
1, 3, 6, 9, 11
```

Prime numbers among them:

```
3, 11
```

Largest prime:

```
11
```

---

# Example 2

## Input

```
nums = [[1,2,3],
        [5,17,7],
        [9,11,10]]
```

## Output

```
17
```

## Explanation

Numbers on diagonals:

```
1, 3, 9, 10, 17
```

Prime numbers:

```
3, 17
```

Largest prime:

```
17
```

---

# Constraints

```
1 <= nums.length <= 300
nums.length == nums[i].length
1 <= nums[i][j] <= 4 * 10^6
```

---
