# 639. Decode Ways II

## Problem Description

A message containing letters from **A-Z** can be encoded into numbers using the following mapping:

```
'A' -> "1"
'B' -> "2"
...
'Z' -> "26"
```

To decode an encoded message, all the digits must be grouped and mapped back into letters using the reverse of the mapping above (there may be multiple ways).

For example, `"11106"` can be mapped into:

```
"AAJF" with the grouping (1 1 10 6)
"KJF" with the grouping (11 10 6)
```

Note that the grouping `(1 11 06)` is invalid because `"06"` cannot be mapped into `'F'` since `"6"` is different from `"06"`.

---

## Special Character `*`

In addition to digits, the encoded message may contain the character:

```
'*'
```

The `*` character can represent **any digit from '1' to '9'** (zero is excluded).

Example:

```
"1*"
```

can represent any of the following encoded messages:

```
"11", "12", "13", "14", "15", "16", "17", "18", "19"
```

Decoding `"1*"` is equivalent to decoding **all of these possibilities combined**.

---

## Task

Given a string `s` consisting of digits and `'*'` characters, return **the total number of ways to decode it**.

Because the result may be very large, return the answer modulo:

```
10^9 + 7
```

---

## Example 1

### Input

```
s = "*"
```

### Output

```
9
```

### Explanation

`*` can represent any digit from **1–9**:

```
1,2,3,4,5,6,7,8,9
```

Each maps to:

```
A,B,C,D,E,F,G,H,I
```

So there are **9 total decoding ways**.

---

## Example 2

### Input

```
s = "1*"
```

### Output

```
18
```

### Explanation

`"1*"` can represent:

```
11,12,13,14,15,16,17,18,19
```

Each of these numbers has **2 decoding possibilities**.

Example:

```
11 -> AA or K
```

Thus:

```
9 possibilities × 2 ways each = 18
```

---

## Example 3

### Input

```
s = "2*"
```

### Output

```
15
```

### Explanation

`"2*"` can represent:

```
21,22,23,24,25,26,27,28,29
```

Decoding counts:

```
21–26 → 2 ways each
27–29 → 1 way each
```

Total:

```
(6 × 2) + (3 × 1) = 12 + 3 = 15
```

---

## Constraints

```
1 <= s.length <= 100000
s[i] is a digit or '*'
```

---
