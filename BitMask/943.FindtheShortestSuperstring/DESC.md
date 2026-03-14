# 943. Find the Shortest Superstring

## Problem Description

Given an array of strings `words`, return the **smallest string** that contains **each string in `words` as a substring**.

If there are multiple valid answers with the smallest length, **return any of them**.

You may assume:

- No string in `words` is a substring of another string in `words`.

---

## Example 1

### Input

```
words = ["alex","loves","leetcode"]
```

### Output

```
"alexlovesleetcode"
```

### Explanation

All permutations of:

```
"alex", "loves", "leetcode"
```

would also be valid answers as long as all strings appear as substrings.

---

## Example 2

### Input

```
words = ["catg","ctaagt","gcta","ttca","atgcatc"]
```

### Output

```
"gctaagttcatgcatc"
```

---

## Constraints

```
1 <= words.length <= 12
```

```
1 <= words[i].length <= 20
```

```
words[i] consists of lowercase English letters
```

```
All strings in words are unique
```

---

## Notes

- The result must contain **every word in the input array as a substring**.
- The goal is to produce the **shortest possible superstring**.
- If multiple shortest superstrings exist, **any one of them can be returned**.
