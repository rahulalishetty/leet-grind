# 1554. Strings Differ by One Character

## Problem Description

You are given a list of strings `dict` where **all strings have the same length**.

Return **true** if there exist **two strings that differ by exactly one character at the same index**. Otherwise return **false**.

---

## Example 1

### Input

```
dict = ["abcd","acbd","aacd"]
```

### Output

```
true
```

### Explanation

The strings:

```
"abcd"
"aacd"
```

differ by only **one character at index 1**.

---

## Example 2

### Input

```
dict = ["ab","cd","yz"]
```

### Output

```
false
```

### Explanation

No pair of strings differs by **exactly one character at the same position**.

---

## Example 3

### Input

```
dict = ["abcd","cccc","abyd","abab"]
```

### Output

```
true
```

---

## Constraints

```
Total number of characters in dict <= 10^5
```

```
dict[i].length == dict[j].length
```

```
dict[i] are unique
```

```
dict[i] contains only lowercase English letters
```
