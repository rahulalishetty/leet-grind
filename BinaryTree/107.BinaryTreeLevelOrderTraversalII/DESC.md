# 107. Binary Tree Level Order Traversal II

## Problem Description

Given the root of a binary tree, return the **bottom-up level order traversal** of its nodes' values.

This means the tree should be traversed **level by level**, but the final result should be returned **from the lowest level up to the root**.

Traversal order:

```
Leaf Level → ... → Root Level
```

---

## Example 1

### Input

```
root = [3,9,20,null,null,15,7]
```

### Output

```
[[15,7],[9,20],[3]]
```

### Explanation

Binary Tree:

```
        3
       / \\
      9   20
         /  \\
        15   7
```

Normal level order traversal:

```
[[3], [9,20], [15,7]]
```

Bottom-up traversal:

```
[[15,7], [9,20], [3]]
```

---

## Example 2

### Input

```
root = [1]
```

### Output

```
[[1]]
```

---

## Example 3

### Input

```
root = []
```

### Output

```
[]
```

---

## Constraints

```
0 <= number of nodes <= 2000
-1000 <= Node.val <= 1000
```

---
