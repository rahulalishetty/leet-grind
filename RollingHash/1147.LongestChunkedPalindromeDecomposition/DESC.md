# 1147. Longest Chunked Palindrome Decomposition

## Problem

You are given a string `text`. You should split it into `k` substrings:

```
(subtext1, subtext2, ..., subtextk)
```

such that:

1. `subtexti` is a **non-empty string**.
2. The concatenation of all substrings equals the original string:

```
subtext1 + subtext2 + ... + subtextk == text
```

3. The substrings satisfy the **palindromic chunk condition**:

```
subtexti == subtext(k - i + 1)
```

for all valid `i`.

Your goal is to **maximize the value of `k`**.

---

## Example 1

### Input

```
text = "ghiabcdefhelloadamhelloabcdefghi"
```

### Output

```
7
```

### Explanation

We can split the string as:

```
(ghi)(abcdef)(hello)(adam)(hello)(abcdef)(ghi)
```

This produces **7 chunks**, which satisfy the symmetric condition.

---

## Example 2

### Input

```
text = "merchant"
```

### Output

```
1
```

### Explanation

No symmetric decomposition exists except the entire string:

```
(merchant)
```

---

## Example 3

### Input

```
text = "antaprezatepzapreanta"
```

### Output

```
11
```

### Explanation

A valid decomposition is:

```
(a)(nt)(a)(pre)(za)(tep)(za)(pre)(a)(nt)(a)
```

This results in **11 chunks**.

---

## Constraints

```
1 <= text.length <= 1000
```

```
text consists only of lowercase English letters
```
