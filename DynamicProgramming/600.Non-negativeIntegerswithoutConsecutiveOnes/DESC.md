# 600. Non-negative Integers without Consecutive Ones

## Problem Description

Given a **positive integer `n`**, return the number of integers in the range:

```
[0, n]
```

whose **binary representations do not contain consecutive `1`s**.

In other words, count all integers `x` such that:

- `0 ≤ x ≤ n`
- The binary representation of `x` **does not contain the substring `11`**.

---

# Example 1

## Input

```
n = 5
```

## Output

```
5
```

## Explanation

All integers `≤ 5`:

| Integer | Binary |
| ------- | ------ |
| 0       | 0      |
| 1       | 1      |
| 2       | 10     |
| 3       | 11     |
| 4       | 100    |
| 5       | 101    |

Among these:

- `3 → 11` contains **two consecutive ones**, so it is invalid.
- All others are valid.

Valid numbers:

```
0, 1, 2, 4, 5
```

Total:

```
5
```

---

# Example 2

## Input

```
n = 1
```

## Output

```
2
```

Valid numbers:

```
0 → 0
1 → 1
```

---

# Example 3

## Input

```
n = 2
```

## Output

```
3
```

Valid numbers:

```
0 → 0
1 → 1
2 → 10
```

---

# Constraints

```
1 ≤ n ≤ 10^9
```

---
