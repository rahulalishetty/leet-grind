# 233. Number of Digit One

## Problem Statement

Given an integer **n**, count the total number of digit **1** appearing in all **non-negative integers less than or equal to n**.

---

## Examples

### Example 1

**Input**

```
n = 13
```

**Output**

```
6
```

**Explanation**

The digit **1** appears in the following numbers:

```
1
10
11
12
13
```

Counting each occurrence:

- 1 → one '1'
- 10 → one '1'
- 11 → two '1's
- 12 → one '1'
- 13 → one '1'

Total = **6**.

---

### Example 2

**Input**

```
n = 0
```

**Output**

```
0
```

**Explanation**

There are no numbers containing the digit **1** between **0 and 0**.

---

## Constraints

- `0 <= n <= 10^9`

---

## Problem Summary

You are asked to compute how many times the **digit '1'** appears in the **decimal representations** of all numbers from:

```
0 → n
```

For example:

```
n = 13
numbers = 0,1,2,3,...,13
```

Count every occurrence of the digit **1** across all numbers.

The result must include:

- single-digit numbers
- multi-digit numbers
- repeated occurrences (e.g., **11** contains two '1's)
