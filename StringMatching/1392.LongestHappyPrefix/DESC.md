# 1392. Longest Happy Prefix

## Problem

A string is called a **happy prefix** if it is a **non-empty prefix** that is also a **suffix**, excluding the entire string itself.

Given a string `s`, return the **longest happy prefix** of `s`.
Return an empty string `""` if no such prefix exists.

---

## Example 1

**Input**

```
s = "level"
```

**Output**

```
"l"
```

**Explanation**

Prefixes (excluding the full string):

```
l
le
lev
leve
```

Suffixes:

```
l
el
vel
evel
```

The largest string that appears in both sets is:

```
"l"
```

---

## Example 2

**Input**

```
s = "ababab"
```

**Output**

```
"abab"
```

**Explanation**

The prefix `"abab"` is also a suffix of the string.
Overlapping between prefix and suffix **is allowed**.

---

## Constraints

- `1 <= s.length <= 10^5`
- `s` contains only **lowercase English letters**
