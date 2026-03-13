# 1964. Find the Longest Valid Obstacle Course at Each Position

## Problem Statement

You want to build some obstacle courses. You are given a **0-indexed integer array `obstacles`** of length `n`, where:

```
obstacles[i] = height of the i-th obstacle
```

For every index `i` between `0` and `n - 1` (inclusive), find the **length of the longest obstacle course** such that:

1. You choose any number of obstacles between `0` and `i` inclusive.
2. You **must include the `i`-th obstacle** in the course.
3. The obstacles must appear **in the same order** as in the array.
4. Every obstacle (except the first) must be **taller than or equal to the previous obstacle**.

Return an array `ans` of length `n` where:

```
ans[i] = length of the longest valid obstacle course ending at index i
```

---

# Example 1

## Input

```
obstacles = [1,2,3,2]
```

## Output

```
[1,2,3,3]
```

## Explanation

The longest valid obstacle course at each position:

- `i = 0`: `[1]` → length = 1
- `i = 1`: `[1,2]` → length = 2
- `i = 2`: `[1,2,3]` → length = 3
- `i = 3`: `[1,2,3,2]` → `[1,2,2]` → length = 3

---

# Example 2

## Input

```
obstacles = [2,2,1]
```

## Output

```
[1,2,1]
```

## Explanation

- `i = 0`: `[2]` → length = 1
- `i = 1`: `[2,2]` → length = 2
- `i = 2`: `[2,2,1]` → `[1]` → length = 1

---

# Example 3

## Input

```
obstacles = [3,1,5,6,4,2]
```

## Output

```
[1,1,2,3,2,2]
```

## Explanation

- `i = 0`: `[3]` → length = 1
- `i = 1`: `[3,1]` → `[1]` → length = 1
- `i = 2`: `[3,1,5]` → `[3,5]` or `[1,5]` → length = 2
- `i = 3`: `[3,1,5,6]` → `[3,5,6]` or `[1,5,6]` → length = 3
- `i = 4`: `[3,1,5,6,4]` → `[3,4]` or `[1,4]` → length = 2
- `i = 5`: `[3,1,5,6,4,2]` → `[1,2]` → length = 2

---

# Constraints

```
n == obstacles.length
1 <= n <= 10^5
1 <= obstacles[i] <= 10^7
```
