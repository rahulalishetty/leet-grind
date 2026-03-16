# 530. Minimum Absolute Difference in BST

## Problem Description

Given the **root of a Binary Search Tree (BST)**, return the **minimum absolute difference** between the values of any two **different nodes** in the tree.

A Binary Search Tree has the following properties:

- The left subtree of a node contains only nodes with values **less than** the node's value.
- The right subtree of a node contains only nodes with values **greater than** the node's value.
- Both the left and right subtrees must also be BSTs.

---

# Objective

Find the **minimum absolute difference** between values of **any two nodes** in the BST.

Formally:

```
min(|a - b|)
```

Where:

- `a` and `b` are values of two different nodes in the tree.

---

# Example 1

## Input

```
root = [4,2,6,1,3]
```

## Tree Structure

```
      4
     / \\
    2   6
   / \\
  1   3
```

## Output

```
1
```

## Explanation

Possible absolute differences:

```
|1 - 2| = 1
|2 - 3| = 1
|3 - 4| = 1
|4 - 6| = 2
```

The **minimum difference is 1**.

---

# Example 2

## Input

```
root = [1,0,48,null,null,12,49]
```

## Tree Structure

```
      1
     / \\
    0   48
        / \\
       12  49
```

## Output

```
1
```

## Explanation

The minimum difference occurs between:

```
|48 - 49| = 1
```

---

# Constraints

```
The number of nodes in the tree is in the range [2, 10^4]

0 <= Node.val <= 10^5
```

---

# Note

This problem is the **same as LeetCode 783**:

```
Minimum Distance Between BST Nodes
```

Both problems ask for the **minimum difference between values of any two nodes in a BST**.
