# 3735. Lexicographically Smallest String After Reverse II

## Problem Description

You are given a string `s` of length `n` consisting of lowercase English letters.

You must perform **exactly one operation** by choosing any integer `k` such that:

```
1 <= k <= n
```

and performing **one of the following operations**:

1. Reverse the **first `k` characters** of the string.
2. Reverse the **last `k` characters** of the string.

After performing exactly one operation, return the **lexicographically smallest string** that can be obtained.

---

## Lexicographical Order

A string `a` is lexicographically smaller than string `b` if:

- At the first position where they differ, the character in `a` comes earlier in the alphabet than the character in `b`.

Example:

```
"abc" < "abd"
"acd" < "bcd"
```

---

# Example 1

### Input

```
s = "dcab"
```

### Output

```
acdb
```

### Explanation

Choose:

```
k = 3
```

Reverse the first 3 characters:

```
"dca" → "acd"
```

Result:

```
"acdb"
```

This is the **lexicographically smallest possible result**.

---

# Example 2

### Input

```
s = "abba"
```

### Output

```
aabb
```

### Explanation

Choose:

```
k = 3
```

Reverse the last 3 characters:

```
"bba" → "abb"
```

Result:

```
"aabb"
```

This is the **smallest lexicographic string achievable**.

---

# Example 3

### Input

```
s = "zxy"
```

### Output

```
xzy
```

### Explanation

Choose:

```
k = 2
```

Reverse the first 2 characters:

```
"zx" → "xz"
```

Result:

```
"xzy"
```

---

# Constraints

```
1 <= n == s.length <= 10^5
```

```
s consists only of lowercase English letters
```
