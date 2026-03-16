# 1062. Longest Repeating Substring

## Problem Description

Given a string `s`, return the **length of the longest repeating substring**.

A **repeating substring** is a substring that appears **at least twice** in the string.
The occurrences may overlap.

If no repeating substring exists, return **0**.

---

## Examples

### Example 1

Input:

```
s = "abcd"
```

Output:

```
0
```

Explanation:

There are no repeating substrings in `"abcd"`.

---

### Example 2

Input:

```
s = "abbaba"
```

Output:

```
2
```

Explanation:

The longest repeating substrings are:

```
"ab"
"ba"
```

Each appears twice.

---

### Example 3

Input:

```
s = "aabcaabdaab"
```

Output:

```
3
```

Explanation:

The longest repeating substring is:

```
"aab"
```

This substring appears **three times** in the string.

---

## Constraints

```
1 <= s.length <= 2000
s consists of lowercase English letters
```

---

## Notes

- A **substring** is a contiguous sequence of characters in a string.
- The substring must appear **at least twice** in the string to be considered repeating.
- Overlapping occurrences **are allowed**.
