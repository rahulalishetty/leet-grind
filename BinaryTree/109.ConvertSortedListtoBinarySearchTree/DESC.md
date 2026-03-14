# 109. Convert Sorted List to Binary Search Tree

## Problem Description

Given the **head of a singly linked list** where the elements are **sorted in ascending order**, convert it into a **height-balanced binary search tree (BST)**.

A **height-balanced BST** is defined as a binary tree in which the depth of the two subtrees of every node never differs by more than one.

---

## Example 1

### Input

```
head = [-10,-3,0,5,9]
```

### Output

```
[0,-3,9,-10,null,5]
```

### Explanation

One possible height-balanced BST:

```
        0
       / \\
     -3   9
     /   /
  -10   5
```

Other balanced BST structures are also valid.

---

## Example 2

### Input

```
head = []
```

### Output

```
[]
```

---

## Constraints

```
0 <= number of nodes <= 2 * 10^4
-10^5 <= Node.val <= 10^5
```

The values in the linked list are **sorted in ascending order**.

---

## Function Template

```java
class Solution {
    public TreeNode sortedListToBST(ListNode head) {

    }
}
```

---

## Definition for Singly Linked List Node

```java
class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
```

---

## Definition for Binary Tree Node

```java
class TreeNode {
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
