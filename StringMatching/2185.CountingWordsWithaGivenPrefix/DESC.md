# 2185. Counting Words With a Given Prefix

## Problem

You are given:

- An array of strings `words`
- A string `pref`

Your task is to return the **number of strings in `words` that contain `pref` as a prefix**.

A **prefix** of a string `s` is any leading contiguous substring of `s`.

---

## Example 1

**Input**

```
words = ["pay","attention","practice","attend"]
pref = "at"
```

**Output**

```
2
```

**Explanation**

The strings that start with `"at"` are:

- `"attention"`
- `"attend"`

So the answer is **2**.

---

## Example 2

**Input**

```
words = ["leetcode","win","loops","success"]
pref = "code"
```

**Output**

```
0
```

**Explanation**

None of the words start with `"code"`.

---

## Constraints

- `1 <= words.length <= 100`
- `1 <= words[i].length <= 100`
- `1 <= pref.length <= 100`
- `words[i]` and `pref` consist of **lowercase English letters**
