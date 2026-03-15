# 3031. Minimum Time to Revert Word to Initial State II

## Problem Statement

You are given a **0-indexed string** `word` and an integer `k`.

At every second, you must perform the following operations:

1. **Remove the first `k` characters** of `word`.
2. **Add any `k` characters** to the **end** of `word`.

Note:

- The characters added **do not have to be the same** as the characters removed.
- However, **both operations must be performed every second**.

Return the **minimum time greater than zero** required for `word` to **revert back to its initial state**.

---

## Example 1

### Input

```
word = "abacaba"
k = 3
```

### Output

```
2
```

### Explanation

**Second 1**

Remove `"aba"` from the start.

Add `"bac"` to the end.

```
word = "cababac"
```

**Second 2**

Remove `"cab"`.

Add `"aba"`.

```
word = "abacaba"
```

The word returns to its **initial state** after **2 seconds**.

---

## Example 2

### Input

```
word = "abacaba"
k = 4
```

### Output

```
1
```

### Explanation

**Second 1**

Remove `"abac"`.

Add `"caba"`.

```
word = "abacaba"
```

The word returns to its original form after **1 second**.

---

## Example 3

### Input

```
word = "abcbabcd"
k = 2
```

### Output

```
4
```

### Explanation

Each second we remove the first **2 characters** and append them to the end.

```
abcbabcd
→ cbabcdab
→ abcdabcb
→ cdabcbab
→ abcbabcd
```

After **4 seconds**, the word returns to its initial state.

---

## Constraints

- `1 <= word.length <= 10^6`
- `1 <= k <= word.length`
- `word` consists only of **lowercase English letters**
