# 1392. Longest Happy Prefix

## Problem Description

A string is called a **happy prefix** if it is a **non-empty prefix** of the string which is also a **suffix**, but **not equal to the entire string itself**.

Given a string `s`, return the **longest happy prefix** of `s`.
If no such prefix exists, return an empty string `""`.

---

## Example 1

### Input

```
s = "level"
```

### Output

```
"l"
```

### Explanation

Prefixes of `s` (excluding the entire string):

```
"l", "le", "lev", "leve"
```

Suffixes of `s`:

```
"l", "el", "vel", "evel"
```

The **largest prefix that is also a suffix** is:

```
"l"
```

---

## Example 2

### Input

```
s = "ababab"
```

### Output

```
"abab"
```

### Explanation

The prefixes:

```
"a", "ab", "aba", "abab", "ababa"
```

The suffixes:

```
"b", "ab", "bab", "abab", "babab"
```

The **longest common prefix and suffix** (excluding the full string) is:

```
"abab"
```

Note that the prefix and suffix **can overlap** in the original string.

---

## Constraints

```
1 <= s.length <= 10^5
```

```
s contains only lowercase English letters
```
