# 2572. Count the Number of Square-Free Subsets

You are given a positive integer **0-indexed array `nums`**.

A subset of the array `nums` is **square-free** if the **product of its elements is a square-free integer**.

---

# Definition

### Square-Free Integer

A **square-free integer** is an integer that **is not divisible by any perfect square greater than 1**.

Examples:

```
Square-free numbers:
1, 2, 3, 5, 6, 7, 10, 11, 13, ...

Not square-free:
4 (2²)
8 (divisible by 4)
9 (3²)
12 (divisible by 4)
```

---

# Task

Return the **number of square-free non-empty subsets** of the array `nums`.

Since the answer may be large, return it **modulo**:

```
10^9 + 7
```

---

# Subset Definition

A **non-empty subset** is formed by deleting **some (possibly none but not all)** elements from the array.

Two subsets are considered **different** if the indices removed are different.

---

# Example 1

## Input

```
nums = [3,4,4,5]
```

## Explanation

Valid square-free subsets:

```
[3]
[5]
[3,5]
```

Products:

```
3  -> square-free
5  -> square-free
15 -> square-free
```

Subsets containing `4` are invalid because:

```
4 = 2²
```

which already contains a square factor.

## Output

```
3
```

---

# Example 2

## Input

```
nums = [1]
```

## Explanation

Valid subsets:

```
[1]
```

Product:

```
1 -> square-free
```

## Output

```
1
```

---

# Constraints

```
1 <= nums.length <= 1000
1 <= nums[i] <= 30
```

---
