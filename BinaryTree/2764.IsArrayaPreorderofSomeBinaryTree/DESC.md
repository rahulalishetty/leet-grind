# 2764. Is Array a Preorder of Some Binary Tree

## Problem

You are given a **0-indexed 2D integer array `nodes`**.

Your task is to determine whether the given array represents the **preorder traversal of some binary tree**.

Each element of the array is:

```
nodes[i] = [id, parentId]
```

Where:

- `id` → the unique identifier of the node.
- `parentId` → the identifier of the parent node.
- If a node has **no parent**, then:

```
parentId = -1
```

---

# Preorder Traversal

Preorder traversal of a binary tree follows the order:

```
1. Visit the current node
2. Traverse the left subtree (preorder)
3. Traverse the right subtree (preorder)
```

---

# Goal

Return:

```
true
```

if the array represents the **preorder traversal of some binary tree**, otherwise return:

```
false
```

---

# Example 1

![alt text](image.png)

### Input

```
nodes = [[0,-1],[1,0],[2,0],[3,2],[4,2]]
```

### Output

```
true
```

### Explanation

The nodes form the following tree:

```
        0
       / \
      1   2
         / \
        3   4
```

The preorder traversal of this tree is:

```
0 → 1 → 2 → 3 → 4
```

This matches the given order.

---

# Example 2

![alt text](image-1.png)

### Input

```
nodes = [[0,-1],[1,0],[2,0],[3,1],[4,1]]
```

### Output

```
false
```

### Explanation

The nodes form the following tree:

```
        0
       / \
      1   2
     / \
    3   4
```

The preorder traversal would be:

```
0 → 1 → 3 → 4 → 2
```

However, the given order is:

```
0 → 1 → 2 → 3 → 4
```

Node `2` appears before the traversal of node `1`’s subtree is complete, so the order **cannot be a preorder traversal**.

---

# Constraints

```
1 ≤ nodes.length ≤ 10^5
nodes[i].length == 2

0 ≤ nodes[i][0] ≤ 10^5
-1 ≤ nodes[i][1] ≤ 10^5
```

Additional guarantee:

```
The input is generated such that nodes make a binary tree.
```
