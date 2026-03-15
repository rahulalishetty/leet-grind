# 3008. Find Beautiful Indices in the Given Array II

## Problem Statement

You are given:

- A **0-indexed string** `s`
- A string `a`
- A string `b`
- An integer `k`

An index `i` is called **beautiful** if the following conditions hold:

1. `0 <= i <= s.length - a.length`
2. `s[i..(i + a.length - 1)] == a`
3. There exists an index `j` such that:
   - `0 <= j <= s.length - b.length`
   - `s[j..(j + b.length - 1)] == b`
   - `|j - i| <= k`

Return an array containing all **beautiful indices** in **sorted order (ascending)**.

---

## Example 1

**Input**

```
s = "isawsquirrelnearmysquirrelhouseohmy"
a = "my"
b = "squirrel"
k = 15
```

**Output**

```
[16, 33]
```

**Explanation**

There are two beautiful indices:

- `i = 16`
  - `s[16..17] = "my"`
  - There exists `j = 4` where `s[4..11] = "squirrel"`
  - `|16 - 4| = 12 <= 15`

- `i = 33`
  - `s[33..34] = "my"`
  - There exists `j = 18` where `s[18..25] = "squirrel"`
  - `|33 - 18| = 15 <= 15`

Thus the result is:

```
[16, 33]
```

---

## Example 2

**Input**

```
s = "abcd"
a = "a"
b = "a"
k = 4
```

**Output**

```
[0]
```

**Explanation**

There is one beautiful index:

- `i = 0`
  - `s[0..0] = "a"`
  - `j = 0` where `s[0..0] = "a"`
  - `|0 - 0| = 0 <= 4`

Thus the result is:

```
[0]
```

---

## Constraints

- `1 <= k <= s.length <= 5 * 10^5`
- `1 <= a.length, b.length <= 5 * 10^5`
- `s`, `a`, and `b` consist only of **lowercase English letters**
