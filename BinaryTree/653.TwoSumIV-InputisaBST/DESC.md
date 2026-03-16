# 653. Two Sum IV - Input is a BST

## Problem

Given the **root of a Binary Search Tree (BST)** and an integer **k**, return:

- **true** if there exist **two elements in the BST** such that their sum equals **k**
- **false** otherwise.

---

## Binary Search Tree Reminder

A **Binary Search Tree (BST)** satisfies the following properties:

- The **left subtree** of a node contains only nodes with values **less than** the node’s value.
- The **right subtree** of a node contains only nodes with values **greater than** the node’s value.
- Both the left and right subtrees must also be BSTs.

---

# Objective

Determine whether there exist **two distinct nodes** in the BST such that:

```
node1.val + node2.val = k
```

---

# Example 1

## Input

```
root = [5,3,6,2,4,null,7]
k = 9
```

## Output

```
true
```

## Explanation

The nodes with values **2 and 7** add up to **9**.

```
2 + 7 = 9
```

So the answer is **true**.

---

# Example 2

## Input

```
root = [5,3,6,2,4,null,7]
k = 28
```

## Output

```
false
```

## Explanation

There are **no two nodes** in the BST whose sum equals **28**.

---

# Constraints

```
The number of nodes in the tree is in the range [1, 10^4]

-10^4 <= Node.val <= 10^4

root is guaranteed to be a valid Binary Search Tree

-10^5 <= k <= 10^5
```
