# 1055. Shortest Way to Form String

A **subsequence** of a string is a new string formed from the original string by deleting some (possibly none) characters **without changing the relative order of the remaining characters**.

Example:

- `"ace"` is a subsequence of `"abcde"`
- `"aec"` is **not** a subsequence of `"abcde"`

---

## Problem

Given two strings:

- `source`
- `target`

Return the **minimum number of subsequences of `source`** such that their **concatenation equals `target`**.

If it is **impossible** to form the target string, return:

```
-1
```

---

## Example 1

**Input**

```
source = "abc"
target = "abcbc"
```

**Output**

```
2
```

**Explanation**

The target `"abcbc"` can be formed as:

```
"abc" + "bc"
```

Both `"abc"` and `"bc"` are subsequences of `"abc"`.

---

## Example 2

**Input**

```
source = "abc"
target = "acdbc"
```

**Output**

```
-1
```

**Explanation**

The target contains the character `"d"`, which does not appear in the source string.
Therefore, constructing the target string is **impossible**.

---

## Example 3

**Input**

```
source = "xyz"
target = "xzyxz"
```

**Output**

```
3
```

**Explanation**

The target can be constructed as:

```
"xz" + "y" + "xz"
```

Each part is a subsequence of `"xyz"`.

---

## Constraints

- `1 <= source.length, target.length <= 1000`
- Both `source` and `target` consist of **lowercase English letters**
