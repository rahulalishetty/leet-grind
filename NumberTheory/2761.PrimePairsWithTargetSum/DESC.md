# 2761. Prime Pairs With Target Sum

You are given an integer **n**.

We say that two integers **x** and **y** form a **prime number pair** if:

- `1 <= x <= y <= n`
- `x + y == n`
- `x` and `y` are **prime numbers**

Return a **2D sorted list** of prime number pairs `[xi, yi]`.

The list should be sorted in **increasing order of `xi`**.

If there are **no prime number pairs**, return an **empty array**.

---

## Definition

A **prime number** is a natural number greater than `1` that has exactly **two factors**:

```
1 and itself
```

---

# Example 1

## Input

```
n = 10
```

## Output

```
[[3,7],[5,5]]
```

## Explanation

The prime pairs that sum to `10` are:

```
3 + 7 = 10
5 + 5 = 10
```

Both `3`, `5`, and `7` are prime numbers.

The output must be sorted by the first element of each pair:

```
[[3,7],[5,5]]
```

---

# Example 2

## Input

```
n = 2
```

## Output

```
[]
```

## Explanation

There are no prime pairs `(x, y)` such that:

```
x + y = 2
```

and both `x` and `y` are prime numbers.

So the result is:

```
[]
```

---

# Constraints

```
1 <= n <= 10^6
```

---
