# 2947. Count Beautiful Substrings I

You are given a string **s** and a positive integer **k**.

Let **vowels** and **consonants** be the number of vowels and consonants in a string.

A string is **beautiful** if:

1. `vowels == consonants`
2. `(vowels * consonants) % k == 0`
   In other words, the multiplication of vowels and consonants is divisible by `k`.

Return the number of **non-empty beautiful substrings** in the given string `s`.

A **substring** is a contiguous sequence of characters in a string.

---

## Definitions

**Vowels** in English:

```
a, e, i, o, u
```

**Consonants**:

Every lowercase English letter except vowels.

---

# Example 1

## Input

```
s = "baeyh"
k = 2
```

## Output

```
2
```

## Explanation

There are **2 beautiful substrings**.

### Substring: `"aeyh"`

```
vowels = 2  -> [a, e]
consonants = 2 -> [y, h]
```

Check conditions:

```
vowels == consonants
2 * 2 % 2 = 0
```

Valid.

---

### Substring: `"baey"`

```
vowels = 2 -> [a, e]
consonants = 2 -> [b, y]
```

Check conditions:

```
vowels == consonants
2 * 2 % 2 = 0
```

Valid.

---

# Example 2

## Input

```
s = "abba"
k = 1
```

## Output

```
3
```

## Explanation

There are **3 beautiful substrings**.

### Substring: `"ab"`

```
vowels = 1 -> [a]
consonants = 1 -> [b]
```

Valid because:

```
1 * 1 % 1 = 0
```

---

### Substring: `"ba"`

```
vowels = 1 -> [a]
consonants = 1 -> [b]
```

Valid.

---

### Substring: `"abba"`

```
vowels = 2 -> [a, a]
consonants = 2 -> [b, b]
```

Check:

```
2 * 2 % 1 = 0
```

Valid.

---

# Example 3

## Input

```
s = "bcdf"
k = 1
```

## Output

```
0
```

## Explanation

The string contains **no vowels**, so it is impossible for any substring to satisfy:

```
vowels == consonants
```

Therefore the answer is:

```
0
```

---

# Constraints

```
1 <= s.length <= 1000
1 <= k <= 1000
s consists only of lowercase English letters
```

---
