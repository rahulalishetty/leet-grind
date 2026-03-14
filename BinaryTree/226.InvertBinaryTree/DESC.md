# 226. Invert Binary Tree

## Problem

Given the **root of a binary tree**, invert the tree and return its root.

Inverting a binary tree means swapping the **left** and **right** children of every node in the tree.

---

## Example 1

![alt text](image.png)

**Input**

```
root = [4,2,7,1,3,6,9]
```

**Output**

```
[4,7,2,9,6,3,1]
```

---

## Example 2

![alt text](image-1.png)

**Input**

```
root = [2,1,3]
```

**Output**

```
[2,3,1]
```

---

## Example 3

**Input**

```
root = []
```

**Output**

```
[]
```

---

## Constraints

- Number of nodes in the tree: **[0, 100]**
- `-100 ≤ Node.val ≤ 100`
