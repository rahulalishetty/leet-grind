# 3045. Count Prefix and Suffix Pairs II

You are given a **0-indexed string array `words`**.

Let's define a boolean function `isPrefixAndSuffix` that takes two strings `str1` and `str2`.

```
isPrefixAndSuffix(str1, str2)
```

returns **true** if `str1` is both a **prefix** and a **suffix** of `str2`, otherwise **false**.

Example:

```
isPrefixAndSuffix("aba", "ababa") → true
```

because `"aba"` is both the prefix and suffix of `"ababa"`.

```
isPrefixAndSuffix("abc", "abcd") → false
```

because `"abc"` is a prefix but **not** a suffix.

---

# Problem

Return the number of index pairs **(i, j)** such that:

```
i < j
```

and

```
isPrefixAndSuffix(words[i], words[j]) == true
```

---

# Example 1

```
Input:
words = ["a","aba","ababa","aa"]

Output:
4
```

Explanation:

Valid pairs:

```
(0,1) → "a" is prefix and suffix of "aba"
(0,2) → "a" is prefix and suffix of "ababa"
(0,3) → "a" is prefix and suffix of "aa"
(1,2) → "aba" is prefix and suffix of "ababa"
```

Total = **4**

---

# Example 2

```
Input:
words = ["pa","papa","ma","mama"]

Output:
2
```

Explanation:

```
(0,1) → "pa" prefix and suffix of "papa"
(2,3) → "ma" prefix and suffix of "mama"
```

Total = **2**

---

# Example 3

```
Input:
words = ["abab","ab"]

Output:
0
```

Explanation:

Only pair is `(0,1)` but `"abab"` is **not** both prefix and suffix of `"ab"`.

---

# Constraints

```
1 <= words.length <= 100000
1 <= words[i].length <= 100000
words[i] contains only lowercase English letters
Sum of lengths of all words <= 500000
```
