# 758. Bold Words in String

## Problem

Given an array of keywords `words` and a string `s`, make all appearances of all keywords `words[i]` in `s` bold.

Any letters between `<b>` and `</b>` tags become bold.

Return `s` after adding the bold tags.

### Requirements

- The returned string should use the **least number of tags possible**.
- The tags should form a **valid combination**.

---

## Example 1

**Input**

```
words = ["ab","bc"]
s = "aabcd"
```

**Output**

```
"a<b>abc</b>d"
```

**Explanation**

Returning:

```
"a<b>a<b>b</b>c</b>d"
```

would use more tags and is therefore incorrect.

---

## Example 2

**Input**

```
words = ["ab","cb"]
s = "aabcd"
```

**Output**

```
"a<b>ab</b>cd"
```

---

## Constraints

- `1 <= s.length <= 500`
- `0 <= words.length <= 50`
- `1 <= words[i].length <= 10`
- `s` and `words[i]` consist of **lowercase English letters**

---

## Note

This problem is the same as:

**LeetCode 616 — Add Bold Tag in String**
