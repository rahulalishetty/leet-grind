# 664. Strange Printer

## Problem Description

There is a strange printer with the following two special properties:

1. The printer can only print a sequence of the **same character** each time.
2. At each turn, the printer can print new characters starting from and ending at **any place** and will **cover the original existing characters**.

Given a string `s`, return the **minimum number of turns** the printer needed to print it.

---

## Example 1

### Input

```
s = "aaabbb"
```

### Output

```
2
```

### Explanation

1. Print `"aaa"` in the first turn.
2. Print `"bbb"` in the second turn.

---

## Example 2

### Input

```
s = "aba"
```

### Output

```
2
```

### Explanation

1. Print `"aaa"` first.
2. Print `"b"` at the second position, covering the existing `'a'`.

---

## Constraints

```
1 <= s.length <= 100
s consists of lowercase English letters
```

---
