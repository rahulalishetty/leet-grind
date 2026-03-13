# 3260. Find the Largest Palindrome Divisible by K

You are given two positive integers `n` and `k`.

An integer `x` is called **k-palindromic** if:

1. `x` is a **palindrome**
2. `x` is **divisible by `k`**

Return the **largest integer having `n` digits** (as a string) that is `k-palindromic`.

The integer must **not have leading zeros**.

---

# Example 1

## Input

```text
n = 3
k = 5
```

## Output

```text
"595"
```

## Explanation

`595` is the largest 3-digit palindrome divisible by `5`.

---

# Example 2

## Input

```text
n = 1
k = 4
```

## Output

```text
"8"
```

## Explanation

The 1-digit palindromes divisible by `4` are:

```text
4, 8
```

So the largest one is:

```text
"8"
```

---

# Example 3

## Input

```text
n = 5
k = 6
```

## Output

```text
"89898"
```

---

# Constraints

```text
1 <= n <= 10^5
1 <= k <= 9
```
