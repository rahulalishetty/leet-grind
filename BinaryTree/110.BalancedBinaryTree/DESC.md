# 110. Balanced Binary Tree

## Problem Description

Given the **root of a binary tree**, determine whether the tree is **height-balanced**.

A **height-balanced binary tree** is defined as a binary tree in which the depth of the two subtrees of every node **never differs by more than 1**.

---

## Example 1

### Input

```
root = [3,9,20,null,null,15,7]
```

### Output

```
true
```

### Explanation

The tree:

```
        3
       / \\
      9   20
         /  \\
        15   7
```

Both subtrees of every node differ in height by **no more than 1**, so the tree is balanced.

---

## Example 2

### Input

```
root = [1,2,2,3,3,null,null,4,4]
```

### Output

```
false
```

### Explanation

The tree:

```
        1
       / \\
      2   2
     / \\
    3   3
   / \\
  4   4
```

The height difference between left and right subtrees becomes **greater than 1**, so the tree is **not balanced**.

---

## Example 3

### Input

```
root = []
```

### Output

```
true
```

### Explanation

An **empty tree** is considered balanced.

---

## Constraints

```
0 <= number of nodes <= 5000
-10^4 <= Node.val <= 10^4
```

---

## Function Template

```java
class Solution {
    public boolean isBalanced(TreeNode root) {

    }
}
```

---

## Definition for Binary Tree Node

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
```
