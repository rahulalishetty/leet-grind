# 2862. Maximum Element-Sum of a Complete Subset of Indices

You are given a **1-indexed array** `nums`. Your task is to select a **complete subset** from `nums` such that:

For every pair of selected indices `i` and `j`:

```
i * j
```

must be a **perfect square**.

Return the **maximum possible sum** of such a complete subset.

---

# Key Definition

A **perfect square** is a number that can be expressed as:

```
x = y^2
```

Examples:

```
1, 4, 9, 16, 25, ...
```

---

# Example 1

## Input

```
nums = [8,7,3,5,7,2,4,9]
```

## Output

```
16
```

## Explanation

We select elements at indices:

```
2 and 8
```

Because:

```
2 * 8 = 16
```

which is a **perfect square**.

Sum:

```
7 + 9 = 16
```

---

# Example 2

## Input

```
nums = [8,10,3,8,1,13,7,9,4]
```

## Output

```
20
```

## Explanation

We select indices:

```
1, 4, 9
```

Products:

```
1 * 4 = 4
1 * 9 = 9
4 * 9 = 36
```

All are **perfect squares**.

Sum:

```
8 + 8 + 4 = 20
```

---

# Constraints

```
1 ≤ n = nums.length ≤ 10^4
1 ≤ nums[i] ≤ 10^9
```
