# 828. Count Unique Characters of All Substrings of a Given String

## Problem Description

Define a function:

```
countUniqueChars(s)
```

This function returns the number of **unique characters** in the string `s`.

A character is considered **unique** if it appears **exactly once** in the string.

For example:

```
s = "LEETCODE"
```

The unique characters are:

```
L, T, C, O, D
```

These characters appear only once in the string.

Therefore:

```
countUniqueChars("LEETCODE") = 5
```

---

## Task

Given a string `s`, compute the **sum of the unique character counts for all substrings of `s`**.

In other words:

For every substring `t` of `s`, compute:

```
countUniqueChars(t)
```

Then return the sum of these values.

### Important Notes

- Substrings must be **contiguous**.
- **Repeated substrings must also be counted**.
- The result is guaranteed to fit within a **32‑bit integer**.

---

## Example 1

### Input

```
s = "ABC"
```

### All Substrings

```
"A"
"B"
"C"
"AB"
"BC"
"ABC"
```

### Unique Character Counts

| Substring | Unique Characters | Count |
| --------- | ----------------- | ----- |
| A         | A                 | 1     |
| B         | B                 | 1     |
| C         | C                 | 1     |
| AB        | A,B               | 2     |
| BC        | B,C               | 2     |
| ABC       | A,B,C             | 3     |

### Sum

```
1 + 1 + 1 + 2 + 2 + 3 = 10
```

### Output

```
10
```

---

## Example 2

### Input

```
s = "ABA"
```

### Substrings

```
"A"
"B"
"A"
"AB"
"BA"
"ABA"
```

### Unique Character Counts

| Substring | Unique Characters | Count |
| --------- | ----------------- | ----- |
| A         | A                 | 1     |
| B         | B                 | 1     |
| A         | A                 | 1     |
| AB        | A,B               | 2     |
| BA        | B,A               | 2     |
| ABA       | B                 | 1     |

### Sum

```
1 + 1 + 1 + 2 + 2 + 1 = 8
```

### Output

```
8
```

---

## Example 3

### Input

```
s = "LEETCODE"
```

### Output

```
92
```

---

## Constraints

```
1 <= s.length <= 100000
s consists only of uppercase English letters.
```

---
