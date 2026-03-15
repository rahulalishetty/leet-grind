# 1960. Maximum Product of the Length of Two Palindromic Substrings

## Problem

You are given a **0-indexed string `s`**.

Your task is to find **two non-overlapping palindromic substrings of odd length** such that the **product of their lengths is maximized**.

Formally, choose integers:

```
0 <= i <= j < k <= l < s.length
```

Such that:

- `s[i...j]` is a palindrome
- `s[k...l]` is a palindrome
- Both palindromes have **odd lengths**
- The substrings **do not intersect**

Return the **maximum product of their lengths**.

A **palindrome** is a string that reads the same forward and backward.

A **substring** is a contiguous sequence of characters.

---

## Example 1

### Input

```
s = "ababbb"
```

### Output

```
9
```

### Explanation

Two palindromes:

```
"aba"
"bbb"
```

Both have length `3`.

```
3 * 3 = 9
```

---

## Example 2

### Input

```
s = "zaaaxbbby"
```

### Output

```
9
```

### Explanation

Two palindromes:

```
"aaa"
"bbb"
```

Both have length `3`.

```
3 * 3 = 9
```

---

## Constraints

```
2 <= s.length <= 10^5
```

```
s consists only of lowercase English letters
```
