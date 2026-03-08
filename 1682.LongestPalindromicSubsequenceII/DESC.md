# 1682. Longest Palindromic Subsequence II

## Problem Description

A **subsequence** of a string `s` is considered a **good palindromic subsequence** if:

1. It is a **subsequence** of `s`.
2. It is a **palindrome** (reads the same forward and backward).
3. It has **even length**.
4. **No two consecutive characters are equal**, except for the **two middle characters**.

### Example

If:

```
s = "abcabcabb"
```

Then:

```
"abba"
```

is considered a **good palindromic subsequence**.

However:

```
"bcb"
```

is **not valid** because the length is **not even**.

```
"bbbb"
```

is **not valid** because it contains **consecutive equal characters outside the middle pair**.

---

## Task

Given a string `s`, return the **length of the longest good palindromic subsequence**.

---

# Examples

## Example 1

**Input**

```
s = "bbabab"
```

**Output**

```
4
```

**Explanation**

The longest good palindromic subsequence is:

```
"baab"
```

---

## Example 2

**Input**

```
s = "dcbccacdb"
```

**Output**

```
4
```

**Explanation**

The longest good palindromic subsequence is:

```
"dccd"
```

---

# Constraints

```
1 <= s.length <= 250
s consists of lowercase English letters
```
