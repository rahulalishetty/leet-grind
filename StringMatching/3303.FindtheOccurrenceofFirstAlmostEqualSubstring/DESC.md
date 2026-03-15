# 3303. Find the Occurrence of First Almost Equal Substring

## Problem

You are given two strings `s` and `pattern`.

A string `x` is called **almost equal** to `y` if you can change **at most one character** in `x` to make it identical to `y`.

Return the **smallest starting index** of a substring in `s` that is almost equal to `pattern`.

If no such index exists, return `-1`.

A substring is a contiguous non-empty sequence of characters within a string.

---

## Example 1

**Input**

```text
s = "abcdefg"
pattern = "bcdffg"
```

**Output**

```text
1
```

**Explanation**

The substring:

```text
s[1..6] = "bcdefg"
```

can be converted to:

```text
"bcdffg"
```

by changing `s[4]` to `'f'`.

---

## Example 2

**Input**

```text
s = "ababbababa"
pattern = "bacaba"
```

**Output**

```text
4
```

**Explanation**

The substring:

```text
s[4..9] = "bababa"
```

can be converted to:

```text
"bacaba"
```

by changing `s[6]` to `'c'`.

---

## Example 3

**Input**

```text
s = "abcd"
pattern = "dba"
```

**Output**

```text
-1
```

---

## Example 4

**Input**

```text
s = "dde"
pattern = "d"
```

**Output**

```text
0
```

---

## Constraints

- `1 <= pattern.length < s.length <= 10^5`
- `s` and `pattern` consist only of lowercase English letters

---

## Follow-up

Could you solve the problem if at most `k` consecutive characters can be changed?
