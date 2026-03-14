# 103. Binary Tree Zigzag Level Order Traversal

## Problem Description

Given the root of a binary tree, return the **zigzag level order traversal** of its nodes' values.

This means the traversal alternates direction at every level:

- Level 1: Left → Right
- Level 2: Right → Left
- Level 3: Left → Right
- and so on.

---

## Example 1

### Input

```
root = [3,9,20,null,null,15,7]
```

### Output

```
[[3],[20,9],[15,7]]
```

### Explanation

Binary tree:

```
        3
       / \\
      9   20
         /  \\
        15   7
```

Zigzag traversal:

```
Level 1 → [3]
Level 2 → [20, 9]
Level 3 → [15, 7]
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
-100 <= Node.val <= 100
```
