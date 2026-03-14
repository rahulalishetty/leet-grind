# 1161. Maximum Level Sum of a Binary Tree

## Problem

Given the **root of a binary tree**, the **level of the root is 1**, the level of its children is **2**, and so on.

Your task is to **return the smallest level `x` such that the sum of all node values at level `x` is maximal**.

---

## Example 1

### Input

```
root = [1,7,0,7,-8,null,null]
```

### Output

```
2
```

### Explanation

```
Level 1 sum = 1
Level 2 sum = 7 + 0 = 7
Level 3 sum = 7 + (-8) = -1
```

The maximum sum occurs at **Level 2**, so the answer is:

```
2
```

---

## Example 2

### Input

```
root = [989,null,10250,98693,-89388,null,null,null,-32127]
```

### Output

```
2
```

---

## Constraints

```
1 ≤ number of nodes ≤ 10^4
-10^5 ≤ Node.val ≤ 10^5
```

---

## Notes

- The tree levels are counted starting from **1 at the root**.
- If multiple levels have the same maximum sum, **return the smallest level number**.
