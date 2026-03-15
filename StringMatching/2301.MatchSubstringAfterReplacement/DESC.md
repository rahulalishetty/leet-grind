# 2301. Match Substring After Replacement

## Problem

You are given two strings **s** and **sub**. You are also given a 2D character array **mappings** where:

```
mappings[i] = [old_i, new_i]
```

This means you may perform the following operation any number of times:

- Replace a character **old_i** in `sub` with \*\*new_i`.

Constraints for replacement:

- Each character in **sub** can be replaced **at most once**.
- You may perform **zero or more replacements**.

Your task is to determine whether it is possible to transform `sub` (using the allowed replacements) so that it becomes a **substring of `s`**.

A **substring** is a contiguous non-empty sequence of characters within a string.

Return:

```
true  -> if such a transformation is possible
false -> otherwise
```

---

## Example 1

**Input**

```
s = "fool3e7bar"
sub = "leet"
mappings = [["e","3"],["t","7"],["t","8"]]
```

**Output**

```
true
```

**Explanation**

Replace:

- first `'e'` → `'3'`
- `'t'` → `'7'`

Now:

```
sub = "l3e7"
```

`"l3e7"` is a substring of `"fool3e7bar"`.

---

## Example 2

**Input**

```
s = "fooleetbar"
sub = "f00l"
mappings = [["o","0"]]
```

**Output**

```
false
```

**Explanation**

- `"f00l"` is not a substring of `s`
- The mapping only allows replacing `'o' → '0'`
- We cannot replace `'0' → 'o'`

Therefore it is impossible.

---

## Example 3

**Input**

```
s = "Fool33tbaR"
sub = "leetd"
mappings = [["e","3"],["t","7"],["t","8"],["d","b"],["p","b"]]
```

**Output**

```
true
```

**Explanation**

Replace:

- `'e' → '3'`
- `'e' → '3'`
- `'d' → 'b'`

Now:

```
sub = "l33tb"
```

`"l33tb"` is a substring of `"Fool33tbaR"`.

---

## Constraints

- `1 <= sub.length <= s.length <= 5000`
- `0 <= mappings.length <= 1000`
- `mappings[i].length == 2`
- `old_i != new_i`
- `s` and `sub` consist of **uppercase and lowercase English letters and digits**
- `old_i` and `new_i` are **letters or digits**
