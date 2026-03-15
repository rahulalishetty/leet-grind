# 3037. Find Pattern in Infinite Stream II

## Problem Statement

You are given:

- A **binary array** `pattern`
- An object `stream` of class **InfiniteStream** representing a 0‑indexed infinite stream of bits

The class `InfiniteStream` provides the following function:

```
int next()
```

This function reads **one bit** (either `0` or `1`) from the stream and returns it.

Your task is to **find the first starting index** where the sequence of bits read from the stream matches the given `pattern`.

For example, if:

```
pattern = [1,0]
```

and the stream is:

```
[0,1,0,1,...]
```

then the first match occurs starting at index **1**.

Return the **starting index** of the first match.

---

# Example 1

## Input

```
stream = [1,1,1,0,1,1,1,...]
pattern = [0,1]
```

## Output

```
3
```

## Explanation

The stream begins as:

```
[1,1,1,0,1,...]
```

The first occurrence of `[0,1]` starts at index **3**.

---

# Example 2

## Input

```
stream = [0,0,0,0,...]
pattern = [0]
```

## Output

```
0
```

## Explanation

The first occurrence of `[0]` starts immediately at index **0**.

---

# Example 3

## Input

```
stream = [1,0,1,1,0,1,1,0,1,...]
pattern = [1,1,0,1]
```

## Output

```
2
```

## Explanation

The first occurrence of `[1,1,0,1]` begins at index **2**.

---

# Constraints

```
1 <= pattern.length <= 10^4
pattern contains only 0 and 1
stream produces only 0 and 1
The correct match occurs within the first 10^5 bits of the stream
```
