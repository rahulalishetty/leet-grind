# 3003. Maximize the Number of Partitions After Operations

## Problem Statement

You are given:

- A string **s**
- An integer **k**

You are allowed to **change at most one character** in the string to any other lowercase English letter.

After that, perform the following operation repeatedly until the string becomes empty:

1. Choose the **longest prefix** of `s` containing **at most `k` distinct characters**.
2. Delete this prefix from `s`.
3. Increase the number of partitions by **1**.

The remaining characters maintain their original order.

Your goal is to **maximize the number of partitions** after performing these operations.

Return the **maximum number of partitions** possible.

---

# Example 1

## Input

```
s = "accca"
k = 2
```

## Output

```
3
```

## Explanation

Change `s[2]` to another character (for example `b`).

```
accca → acbca
```

Now perform partitioning:

1. Longest prefix with ≤2 distinct characters → `"ac"`
   Remaining string → `"bca"`

2. Longest prefix with ≤2 distinct characters → `"bc"`
   Remaining string → `"a"`

3. Longest prefix → `"a"`

Total partitions:

```
3
```

---

# Example 2

## Input

```
s = "aabaab"
k = 3
```

## Output

```
1
```

## Explanation

The string already has only **2 distinct characters**.

Even after changing any character, the string will still contain **≤ 3 distinct characters**.

Therefore the entire string will always be taken as a single prefix.

Partitions:

```
1
```

---

# Example 3

## Input

```
s = "xxyz"
k = 1
```

## Output

```
4
```

## Explanation

Change `s[0]` to a new character (for example `w`).

```
xxyz → wxyz
```

Now all characters are distinct.

Since `k = 1`, each prefix can contain **only one distinct character**, so each character forms its own partition.

Partitions:

```
w | x | y | z
```

Total partitions:

```
4
```

---

# Constraints

```
1 <= s.length <= 10^4
s consists only of lowercase English letters
1 <= k <= 26
```

---

# Key Observations

- The operation always selects the **longest prefix with ≤ k distinct characters**.
- Changing **one character strategically** can increase the number of partitions.
- If the prefix already contains ≤ k distinct characters for the entire string, only **1 partition** will be created.
- If `k` is small, the string will split frequently when new characters appear.

---

# Summary

The problem asks us to:

1. Optionally change **one character** in the string.
2. Repeatedly remove the **longest prefix with at most `k` distinct characters**.
3. Count the number of partitions created.
4. Return the **maximum possible number of partitions** achievable.
