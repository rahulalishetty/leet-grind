# 1153. String Transforms Into Another String

## Problem Statement

You are given two strings:

- `str1`
- `str2`

Both strings have the **same length**.

Your task is to determine whether it is possible to **transform `str1` into `str2`** using a sequence of conversions.

---

# Conversion Rule

In one conversion operation:

- You can convert **all occurrences of one character in `str1`**
- Into **any other lowercase English character**.

Example:

```
convert 'a' -> 'c'
```

This converts **every 'a' in the string** into `'c'` simultaneously.

---

# Goal

Return:

```
true
```

if it is possible to transform `str1` into `str2` using **zero or more conversions**.

Otherwise return:

```
false
```

---

# Important Details

- Conversions apply to **all occurrences** of a character at once.
- Conversion order **matters**.
- Characters can be converted multiple times through intermediate characters.

Example:

```
a -> c
c -> e
```

This effectively converts `a -> e`.

---

# Example 1

```
Input:
str1 = "aabcc"
str2 = "ccdee"
```

### Transformation Steps

```
c -> e
b -> d
a -> c
```

Applying these conversions in the correct order results in:

```
aabcc
↓
ccdee
```

### Output

```
true
```

---

# Example 2

```
Input:
str1 = "leetcode"
str2 = "codeleet"
```

### Explanation

There is **no sequence of valid conversions** that transforms `str1` into `str2`.

### Output

```
false
```

---

# Constraints

```
1 <= str1.length == str2.length <= 10^4
str1 and str2 contain only lowercase English letters
```

---

# Key Observations

This problem involves **mapping characters from one string to another**.

Important characteristics:

- Each character in `str1` must map consistently to **exactly one character in `str2`**
- Multiple characters may map to the same target character
- But conflicting mappings are not allowed

Example of **invalid mapping**:

```
a -> b
a -> c
```

This is impossible because one character cannot convert to two different targets.

---

# Core Idea

We must determine whether a **consistent character transformation mapping** exists from `str1` to `str2`.

The transformation must obey the rule:

```
Each source character maps to exactly one destination character.
```

---

# Graph Interpretation

You can think of the transformation rules as a **directed graph**:

```
source_char → target_char
```

Example:

```
a → c
b → d
c → e
```

---

# When Transformation Is Impossible

Transformation fails when:

1. A character in `str1` maps to **two different characters** in `str2`
2. Cycles exist that cannot be broken due to lack of unused characters

Example:

```
a → b
b → a
```

This requires a temporary placeholder character to break the cycle.

---

# Important Constraint

There are **26 lowercase letters**.

If all 26 characters are already used in the mapping targets, then we **cannot break cycles** using an extra character.

---

# Summary

To determine if transformation is possible:

- Ensure a **consistent mapping** from `str1` to `str2`
- Ensure cycles can be broken if necessary
- Return `true` only if the transformation rules are valid
