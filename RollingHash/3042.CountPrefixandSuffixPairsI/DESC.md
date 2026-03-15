# 3042. Count Prefix and Suffix Pairs I

## Problem Statement

You are given a **0-indexed array of strings** called `words`.

Define a boolean function:

```
isPrefixAndSuffix(str1, str2)
```

This function returns **true** if `str1` is both:

- a **prefix** of `str2`
- a **suffix** of `str2`

Otherwise it returns **false**.

For example:

```
isPrefixAndSuffix("aba", "ababa") = true
```

because `"aba"` appears both at the beginning and at the end of `"ababa"`.

But:

```
isPrefixAndSuffix("abc", "abcd") = false
```

because `"abc"` is a prefix but **not a suffix**.

Your task is to return the **number of index pairs (i, j)** such that:

```
i < j
isPrefixAndSuffix(words[i], words[j]) == true
```

---

# Example 1

## Input

```
words = ["a","aba","ababa","aa"]
```

## Output

```
4
```

## Explanation

Valid pairs:

```
(0,1) -> "a" is prefix and suffix of "aba"
(0,2) -> "a" is prefix and suffix of "ababa"
(0,3) -> "a" is prefix and suffix of "aa"
(1,2) -> "aba" is prefix and suffix of "ababa"
```

Total = **4**.

---

# Example 2

## Input

```
words = ["pa","papa","ma","mama"]
```

## Output

```
2
```

## Explanation

Valid pairs:

```
(0,1) -> "pa" is prefix and suffix of "papa"
(2,3) -> "ma" is prefix and suffix of "mama"
```

Total = **2**.

---

# Example 3

## Input

```
words = ["abab","ab"]
```

## Output

```
0
```

## Explanation

The only possible pair is:

```
(0,1)
```

But `"abab"` is **not** both a prefix and suffix of `"ab"`.

So the answer is **0**.

---

# Constraints

```
1 <= words.length <= 50
1 <= words[i].length <= 10
words[i] consists only of lowercase English letters
```
