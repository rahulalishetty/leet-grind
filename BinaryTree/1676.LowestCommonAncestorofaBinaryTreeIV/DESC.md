# 1676. Lowest Common Ancestor of a Binary Tree IV

## Problem

You are given:

- The **root of a binary tree**
- An **array of TreeNode objects `nodes`**

Your task is to return the **Lowest Common Ancestor (LCA)** of **all nodes in the array**.

---

## Definition

Extending the standard LCA definition:

> The lowest common ancestor of nodes `p1, p2, ..., pn` in a binary tree is the **lowest node in the tree that has every `pi` as a descendant** (where a node can be a descendant of itself).

A **descendant** of node `x` is a node that appears on the path from `x` to any leaf node.

---

# Example 1

![alt text](image.png)

### Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
nodes = [4,7]
```

### Output

```
2
```

### Explanation

The lowest common ancestor of nodes `4` and `7` is **node 2**.

---

# Example 2

![alt text](image-1.png)

### Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
nodes = [1]
```

### Output

```
1
```

### Explanation

If there is **only one node**, the LCA is the node itself.

---

# Example 3

![alt text](image-2.png)

### Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
nodes = [7,6,2,4]
```

### Output

```
5
```

### Explanation

Node **5** is the lowest node that has all nodes `7,6,2,4` as descendants.

---

# Constraints

```
1 <= number of nodes in tree <= 10^4
-10^9 <= Node.val <= 10^9
```

Additional guarantees:

- All `Node.val` values are **unique**
- All `nodes[i]` exist in the tree
- All `nodes[i]` are **distinct**
