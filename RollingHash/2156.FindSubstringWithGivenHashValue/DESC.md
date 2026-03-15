# 2156. Find Substring With Given Hash Value

## Problem Statement

The hash of a **0-indexed string** `s` of length `k`, given integers `p` and `m`, is computed using the following function:

```
hash(s, p, m) = (val(s[0]) * p^0 + val(s[1]) * p^1 + ... + val(s[k-1]) * p^(k-1)) mod m
```

Where:

- `val(s[i])` represents the index of the character in the alphabet.
- `val('a') = 1`
- `val('b') = 2`
- ...
- `val('z') = 26`

You are given:

- a string `s`
- integers `power`, `modulo`, `k`, and `hashValue`

Your task is to **return the first substring of length `k`** whose hash value equals `hashValue`.

The problem guarantees that **an answer always exists**.

A **substring** is a contiguous non-empty sequence of characters within a string.

---

## Example 1

**Input**

```
s = "leetcode"
power = 7
modulo = 20
k = 2
hashValue = 0
```

**Output**

```
"ee"
```

**Explanation**

```
hash("ee", 7, 20)
= (5 * 7^0 + 5 * 7^1) mod 20
= (5 + 35) mod 20
= 40 mod 20
= 0
```

`"ee"` is the **first substring of length 2** with hash value `0`.

---

## Example 2

**Input**

```
s = "fbxzaad"
power = 31
modulo = 100
k = 3
hashValue = 32
```

**Output**

```
"fbx"
```

**Explanation**

```
hash("fbx", 31, 100)
= (6 * 31^0 + 2 * 31^1 + 24 * 31^2) mod 100
= (6 + 62 + 23064) mod 100
= 23132 mod 100
= 32
```

Another substring `"bxz"` also has hash value `32`, but `"fbx"` appears **earlier**, so it is returned.

---

## Constraints

- `1 <= k <= s.length <= 2 * 10^4`
- `1 <= power, modulo <= 10^9`
- `0 <= hashValue < modulo`
- `s` consists only of **lowercase English letters**
- The test cases guarantee **an answer exists**
