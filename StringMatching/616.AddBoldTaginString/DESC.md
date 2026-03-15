# 616. Add Bold Tag in String

## Problem

You are given a string `s` and an array of strings `words`.

You should add a closed pair of bold tags `<b>` and `</b>` to wrap the substrings in `s` that exist in `words`.

Rules:

- If two such substrings **overlap**, they should be wrapped together using **one pair of bold tags**.
- If two substrings wrapped by bold tags are **consecutive**, they should also be **merged into a single bold section**.

Return the string `s` after adding the bold tags.

---

## Example 1

**Input**

```
s = "abcxyz123"
words = ["abc","123"]
```

**Output**

```
"<b>abc</b>xyz<b>123</b>"
```

**Explanation**

The substrings `"abc"` and `"123"` appear in `s`.
We wrap them with `<b>` and `</b>`.

---

## Example 2

**Input**

```
s = "aaabbb"
words = ["aa","b"]
```

**Output**

```
"<b>aaabbb</b>"
```

**Explanation**

- `"aa"` appears twice.
- `"b"` appears three times.

Initial wrapping creates overlapping and consecutive bold tags.

Overlapping tags are merged, and consecutive bold sections are combined, resulting in:

```
"<b>aaabbb</b>"
```

---

## Constraints

- `1 <= s.length <= 1000`
- `0 <= words.length <= 100`
- `1 <= words[i].length <= 1000`
- `s` and `words[i]` consist of **English letters and digits**
- All values in `words` are **unique**

---

## Note

This problem is the same as **LeetCode 758 – Bold Words in String**.
