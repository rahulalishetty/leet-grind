# 2193. Minimum Number of Moves to Make Palindrome

## Problem Statement

You are given a string `s` consisting only of **lowercase English letters**.

In one move, you can select **any two adjacent characters** of `s` and **swap them**.

Your task is to return the **minimum number of moves** required to make the string a **palindrome**.

It is guaranteed that the input string **can always be converted into a palindrome**.

---

# Example 1

## Input

```
s = "aabb"
```

## Output

```
2
```

## Explanation

Two possible palindromes can be formed:

```
abba
baab
```

One way to obtain `"abba"`:

```
aabb → abab → abba
```

Moves = **2**

One way to obtain `"baab"`:

```
aabb → abab → baab
```

Moves = **2**

Minimum moves = **2**

---

# Example 2

## Input

```
s = "letelt"
```

## Output

```
2
```

## Explanation

One possible palindrome is:

```
lettel
```

Transformation:

```
letelt → letetl → lettel
```

Other palindromes such as `"tleelt"` can also be formed in **2 moves**.

It can be proven that **no solution requires fewer than 2 moves**.

---

# Constraints

```
1 <= s.length <= 2000
s consists only of lowercase English letters
s can always be converted into a palindrome
```
