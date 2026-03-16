# 940. Distinct Subsequences II

## Problem Description

Given a string **s**, return the number of **distinct non-empty subsequences** of `s`.

Since the answer may be very large, return it **modulo (10^9 + 7)**.

A **subsequence** of a string is formed by deleting zero or more characters **without changing the relative order** of the remaining characters.

For example:

- `"ace"` is a subsequence of `"abcde"`
- `"aec"` is **not** a subsequence of `"abcde"` because the order is changed.

---

## Example 1

### Input

```
s = "abc"
```

### Output

```
7
```

### Explanation

The distinct subsequences are:

```
"a"
"b"
"c"
"ab"
"ac"
"bc"
"abc"
```

Total = **7**

---

## Example 2

### Input

```
s = "aba"
```

### Output

```
6
```

### Explanation

Distinct subsequences are:

```
"a"
"b"
"ab"
"aa"
"ba"
"aba"
```

Total = **6**

---

## Example 3

### Input

```
s = "aaa"
```

### Output

```
3
```

### Explanation

Distinct subsequences are:

```
"a"
"aa"
"aaa"
```

Total = **3**

---

## Constraints

```
1 <= s.length <= 2000
s consists of lowercase English letters
```

---

## Key Idea

The challenge is to count **distinct subsequences** without counting duplicates that arise due to repeated characters.

A dynamic programming approach is typically used to track how many subsequences exist up to each position while subtracting previously counted duplicates caused by repeated characters.

---

## Difficulty

```
Hard
```
