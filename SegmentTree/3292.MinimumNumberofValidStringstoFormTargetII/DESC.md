# 3292. Minimum Number of Valid Strings to Form Target II

## Problem Description

You are given:

- An array of strings **words**
- A string **target**

A string **x** is considered **valid** if **x is a prefix of any string in `words`**.

Your task is to determine the **minimum number of valid strings** that can be concatenated together to form the string `target`.

If it is **not possible** to construct the target string, return:

```
-1
```

---

# Definition

### Valid String

A string `x` is valid if it is a **prefix** of at least one string in `words`.

Example:

```
words = ["abcdef"]
```

Valid prefixes include:

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

Construct the string **target** using the **minimum number of valid prefixes**.

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

The target can be formed using:

```
"aa"   → prefix of "aaaaa"
"bcd"  → prefix of "bcdef"
"abc"  → prefix of "abc"
```

Number of strings used:

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

Total strings used:

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

There is **no prefix of "abcdef"** that matches the beginning of `"xyz"`.

Therefore it is impossible to construct the target string.

---

# Constraints

```
1 <= words.length <= 100
1 <= words[i].length <= 5 * 10^4

Sum(words[i].length) <= 10^5

words[i] contains only lowercase English letters

1 <= target.length <= 5 * 10^4
target contains only lowercase English letters
```
