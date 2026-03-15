# 1316. Distinct Echo Substrings

## Problem Description

Given a string `text`, return the number of **distinct non-empty substrings** that can be written as:

```text
a + a
```

where `a` is some string.

In other words, the substring must be made by concatenating a string with itself.

Such substrings are called **echo substrings**.

---

## Example 1

### Input

```text
text = "abcabcabc"
```

### Output

```text
3
```

### Explanation

The 3 distinct echo substrings are:

```text
"abcabc"
"bcabca"
"cabcab"
```

Each of them can be written as:

- `"abcabc" = "abc" + "abc"`
- `"bcabca" = "bca" + "bca"`
- `"cabcab" = "cab" + "cab"`

---

## Example 2

### Input

```text
text = "leetcodeleetcode"
```

### Output

```text
2
```

### Explanation

The 2 distinct echo substrings are:

```text
"ee"
"leetcodeleetcode"
```

Because:

- `"ee" = "e" + "e"`
- `"leetcodeleetcode" = "leetcode" + "leetcode"`

---

## Constraints

```text
1 <= text.length <= 2000
```

```text
text consists only of lowercase English letters
```
