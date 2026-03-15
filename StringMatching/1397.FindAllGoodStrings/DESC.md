# 1397. Find All Good Strings

## Problem

Given the strings **s1** and **s2** of size **n** and the string **evil**, return the number of **good strings**.

A **good string** must satisfy all the following conditions:

- The string length is **n**
- It is **alphabetically greater than or equal to `s1`**
- It is **alphabetically smaller than or equal to `s2`**
- It **does not contain the string `evil` as a substring**

Since the answer can be very large, return it **modulo 10⁹ + 7**.

---

## Example 1

**Input**

```
n = 2
s1 = "aa"
s2 = "da"
evil = "b"
```

**Output**

```
51
```

**Explanation**

There are **25 good strings starting with 'a'**:

```
aa, ac, ad, ..., az
```

Then there are **25 good strings starting with 'c'**:

```
ca, cc, cd, ..., cz
```

Finally there is **1 good string starting with 'd'**:

```
da
```

Total = **51**

---

## Example 2

**Input**

```
n = 8
s1 = "leetcode"
s2 = "leetgoes"
evil = "leet"
```

**Output**

```
0
```

**Explanation**

All strings in the range `[s1, s2]` begin with `"leet"`.

Since `"leet"` is the forbidden substring (`evil`), **no valid strings exist**.

---

## Example 3

**Input**

```
n = 2
s1 = "gx"
s2 = "gz"
evil = "x"
```

**Output**

```
2
```

---

## Constraints

- `s1.length == n`
- `s2.length == n`
- `s1 <= s2`
- `1 <= n <= 500`
- `1 <= evil.length <= 50`
- All strings consist of **lowercase English letters**
