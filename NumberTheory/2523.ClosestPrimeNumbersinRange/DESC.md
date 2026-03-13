# 2523. Closest Prime Numbers in Range

You are given two positive integers **left** and **right**.

Find two integers **num1** and **num2** such that:

- `left <= num1 < num2 <= right`
- Both **num1** and **num2** are **prime numbers**
- `num2 - num1` is **minimum** among all such valid pairs

Return the array:

```
[num1, num2]
```

If multiple pairs satisfy the condition, return the pair with the **smallest num1**.

If **no such pair exists**, return:

```
[-1, -1]
```

---

# Definition

### Prime Number

A **prime number** is a number greater than **1** that has exactly **two divisors**:

```
1 and itself
```

Examples of primes:

```
2, 3, 5, 7, 11, 13, 17 ...
```

---

# Example 1

## Input

```
left = 10
right = 19
```

## Explanation

Prime numbers between **10 and 19**:

```
11, 13, 17, 19
```

Possible prime pairs:

| Pair  | Gap |
| ----- | --- |
| 11,13 | 2   |
| 13,17 | 4   |
| 17,19 | 2   |

Minimum gap is:

```
2
```

Possible pairs:

```
[11,13] and [17,19]
```

Since **11 < 17**, we return:

```
[11,13]
```

## Output

```
[11,13]
```

---

# Example 2

## Input

```
left = 4
right = 6
```

Prime numbers in the range:

```
5
```

There is **only one prime**, so we cannot form a pair.

## Output

```
[-1,-1]
```

---

# Constraints

```
1 <= left <= right <= 10^6
```

---

# Problem Goal

Within the range:

```
[left, right]
```

Find the **two closest prime numbers** such that their **difference is minimum**.
