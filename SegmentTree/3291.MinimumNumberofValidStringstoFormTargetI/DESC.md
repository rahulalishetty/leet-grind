# 3291. Minimum Number of Valid Strings to Form Target I

## Problem Description

You are given:

- An array of strings `words`
- A string `target`

A string **x** is called **valid** if **x is a prefix of any string in `words`**.

Your task is to determine the **minimum number of valid strings** that can be concatenated to form the string `target`.

If it is **not possible** to form `target`, return:

```
-1
```

---

# Key Definition

### Valid String

A string `x` is valid if it is a **prefix of at least one string in `words`**.

Example:

```
words = ["abcdef"]
```

Valid strings include:

```
"a"
"ab"
"abc"
"abcd"
"abcde"
"abcdef"
```

---

# Goal

Form the string `target` by concatenating valid strings while minimizing the number of strings used.

---

# Example 1

## Input

```
words = ["abc","aaaaa","bcdef"]
target = "aabcdabc"
```

## Output

```
3
```

## Explanation

The target string can be formed by concatenating:

```
"aa"   → prefix of "aaaaa"
"bcd"  → prefix of "bcdef"
"abc"  → prefix of "abc"
```

Total valid strings used:

```
3
```

---

# Example 2

## Input

```
words = ["abababab","ab"]
target = "ababaababa"
```

## Output

```
2
```

## Explanation

The target can be formed by:

```
"ababa" → prefix of "abababab"
"ababa" → prefix of "abababab"
```

Total valid strings used:

```
2
```

---

# Example 3

## Input

```
words = ["abcdef"]
target = "xyz"
```

## Output

```
-1
```

## Explanation

No prefix of `"abcdef"` matches the start of `"xyz"`.

Therefore, forming the target is **impossible**.

---

# Constraints

```
1 <= words.length <= 100
1 <= words[i].length <= 5 * 10^3
Sum of all words[i].length <= 10^5

words[i] contains only lowercase English letters

1 <= target.length <= 5 * 10^3
target contains only lowercase English letters
```
