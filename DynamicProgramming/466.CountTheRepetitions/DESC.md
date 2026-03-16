# 466. Count The Repetitions

## Problem Description

We define:

```
str = [s, n]
```

as the string formed by **concatenating string `s` exactly `n` times**.

### Example

```
str = ["abc", 3]
```

becomes

```
"abcabcabc"
```

---

## Subsequence Definition

A string **s1 can be obtained from s2** if we can **remove some characters from s2** so that it becomes **s1**.

This means **s1 must be a subsequence of s2**.

### Example

```
s1 = "abc"
s2 = "abdbec"
```

We can obtain `"abc"` from `"abdbec"` by removing some characters.

---

## Given

You are given:

```
s1, n1
s2, n2
```

Define:

```
str1 = [s1, n1]
str2 = [s2, n2]
```

Meaning:

```
str1 = s1 repeated n1 times
str2 = s2 repeated n2 times
```

---

## Goal

Return the **maximum integer `m`** such that:

```
[str2, m]
```

can be obtained from:

```
str1
```

In other words:

We want to know how many times the sequence

```
s2 repeated n2 times
```

can appear as a **subsequence** inside

```
s1 repeated n1 times
```

---

## Example 1

### Input

```
s1 = "acb"
n1 = 4
s2 = "ab"
n2 = 2
```

### Explanation

```
str1 = "acbacbacbacb"
str2 = "abab"
```

We can obtain `"abab"` from `str1` two times.

### Output

```
2
```

---

## Example 2

### Input

```
s1 = "acb"
n1 = 1
s2 = "acb"
n2 = 1
```

### Explanation

```
str1 = "acb"
str2 = "acb"
```

One copy can be obtained.

### Output

```
1
```

---

## Constraints

```
1 <= s1.length, s2.length <= 100
s1 and s2 consist of lowercase English letters
1 <= n1, n2 <= 10^6
```

---
