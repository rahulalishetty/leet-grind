# 113. Path Sum II

## Problem Description

Given the **root of a binary tree** and an integer **targetSum**, return **all root-to-leaf paths** where the sum of the node values in the path equals `targetSum`.

Each path should be returned as a **list of node values**, not node references.

A **root-to-leaf path** is defined as a path that:

- Starts from the **root**
- Ends at a **leaf node**

A **leaf node** is a node with **no children**.

---

## Example 1

### Input

```
root = [5,4,8,11,null,13,4,7,2,null,null,5,1]
targetSum = 22
```

### Output

```
[[5,4,11,2],[5,8,4,5]]
```

### Explanation

There are two valid root-to-leaf paths:

```
5 → 4 → 11 → 2  = 22
5 → 8 → 4 → 5   = 22
```

---

## Example 2

### Input

```
root = [1,2,3]
targetSum = 5
```

### Output

```
[]
```

### Explanation

No root-to-leaf path produces a sum of `5`.

---

## Example 3

### Input

```
root = [1,2]
targetSum = 0
```

### Output

```
[]
```

### Explanation

No path from root to leaf produces the required sum.

---

## Constraints

```
0 <= number of nodes <= 5000
-1000 <= Node.val <= 1000
-1000 <= targetSum <= 1000
```

---
