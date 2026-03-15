# 1216. Valid Palindrome III

## Problem

Given a string `s` and an integer `k`, return **true** if `s` is a **k-palindrome**.

A string is called a **k-palindrome** if it can be transformed into a palindrome by removing **at most `k` characters**.

---

## Example 1

Input:

```
s = "abcdeca"
k = 2
```

Output:

```
true
```

Explanation:

Remove characters **'b'** and **'e'**:

```
abcdeca -> aceca
```

`aceca` is a palindrome.

---

## Example 2

Input:

```
s = "abbababa"
k = 1
```

Output:

```
true
```

The string can be transformed into a palindrome by removing at most one character.

---

## Constraints

```
1 <= s.length <= 1000
s consists only of lowercase English letters
1 <= k <= s.length
```

---

## Notes

A common approach is to compute the **minimum number of deletions needed to make the string a palindrome**.

If the minimum deletions required is:

```
<= k
```

then the string is a **k-palindrome**.

This problem is typically solved using **Dynamic Programming** in **O(n²)** time.
