# 1461. Check If a String Contains All Binary Codes of Size K

## Problem Description

Given a **binary string** `s` and an integer `k`, return **true** if **every binary code of length `k`** is a substring of `s`. Otherwise, return **false**.

A binary code is a string consisting only of `'0'` and `'1'`.

---

## Example 1

### Input

```
s = "00110110"
k = 2
```

### Output

```
true
```

### Explanation

All binary codes of length `2` are:

```
"00", "01", "10", "11"
```

They appear in `s` as substrings at indices:

```
"00" → index 0
"01" → index 1
"11" → index 2
"10" → index 3
```

Since all are present, the result is **true**.

---

## Example 2

### Input

```
s = "0110"
k = 1
```

### Output

```
true
```

### Explanation

Binary codes of length `1`:

```
"0", "1"
```

Both appear in the string, so the result is **true**.

---

## Example 3

### Input

```
s = "0110"
k = 2
```

### Output

```
false
```

### Explanation

Binary codes of length `2`:

```
"00", "01", "10", "11"
```

The substring `"00"` does **not** appear in `s`, so the answer is **false**.

---

## Constraints

```
1 <= s.length <= 5 * 10^5
```

```
s[i] is either '0' or '1'
```

```
1 <= k <= 20
```
