# 333. Largest BST Subtree — Detailed Approaches

## Overview

We need to find the **largest subtree that is also a Binary Search Tree (BST)**.
"**Largest**" means the subtree contains the **maximum number of nodes**.

### Binary Search Tree Properties

A Binary Search Tree (BST) satisfies:

1. The **left subtree** contains only nodes with values **less than the parent node**.
2. The **right subtree** contains only nodes with values **greater than the parent node**.
3. Both **left and right subtrees must also be BSTs**.

---

# Approach 1: Pre‑Order Traversal (Brute Force)

## Intuition

The root of the largest BST could be **any node** in the tree.

Therefore we:

1. Traverse every node.
2. Check if the subtree rooted at that node is a BST.
3. If yes, count nodes in that subtree.
4. Track the maximum size.

This approach repeatedly recomputes information for many subtrees, making it inefficient.

---

## Checking if a Tree is a BST

A subtree is a BST if:

- Maximum value in left subtree < root value
- Minimum value in right subtree > root value
- Left subtree is BST
- Right subtree is BST

### Pseudocode

```
isValidBST(root):
    if root == NULL
        return True

    leftMax = max value in left subtree
    if leftMax >= root.val
        return False

    rightMin = min value in right subtree
    if rightMin <= root.val
        return False

    return isValidBST(root.left) AND isValidBST(root.right)
```

---

## Finding Max / Min Values

```
findMax(root):
    if root == NULL
        return -∞

    return max(root.val, findMax(root.left), findMax(root.right))
```

```
findMin(root):
    if root == NULL
        return +∞

    return min(root.val, findMin(root.left), findMin(root.right))
```

---

## Counting Nodes

```
countNodes(root):
    if root == NULL
        return 0

    return 1 + countNodes(root.left) + countNodes(root.right)
```

---

## Java Implementation

```java
class Solution {

    private boolean isValidBST(TreeNode root) {
        if (root == null) return true;

        int leftMax = findMax(root.left);
        if (leftMax >= root.val) return false;

        int rightMin = findMin(root.right);
        if (rightMin <= root.val) return false;

        return isValidBST(root.left) && isValidBST(root.right);
    }

    private int findMax(TreeNode root) {
        if (root == null) return Integer.MIN_VALUE;
        return Math.max(Math.max(root.val, findMax(root.left)), findMax(root.right));
    }

    private int findMin(TreeNode root) {
        if (root == null) return Integer.MAX_VALUE;
        return Math.min(Math.min(root.val, findMin(root.left)), findMin(root.right));
    }

    private int countNodes(TreeNode root) {
        if (root == null) return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    public int largestBSTSubtree(TreeNode root) {
        if (root == null) return 0;

        if (isValidBST(root)) {
            return countNodes(root);
        }

        return Math.max(
            largestBSTSubtree(root.left),
            largestBSTSubtree(root.right)
        );
    }
}
```

---

## Complexity

Time Complexity

```
O(N³)
```

Reason:

- Checking BST: `O(N²)`
- Counting nodes: `O(N)`
- Repeated for each node.

Space Complexity

```
O(N)
```

due to recursion stack.

---

# Approach 2: Pre‑Order Traversal (Optimized)

## Key Idea

A **BST produces a strictly increasing sequence during inorder traversal**.

So instead of computing max/min repeatedly, we:

- Perform inorder traversal
- Compare current node with previous node

If order is violated → not a BST.

---

## Implementation

```java
class Solution {

    private TreeNode previous;

    private boolean isValidBST(TreeNode root) {
        if (root == null) return true;

        if (!isValidBST(root.left)) return false;

        if (previous != null && previous.val >= root.val) return false;

        previous = root;

        return isValidBST(root.right);
    }

    private int countNodes(TreeNode root) {
        if (root == null) return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    public int largestBSTSubtree(TreeNode root) {

        if (root == null) return 0;

        previous = null;

        if (isValidBST(root)) {
            return countNodes(root);
        }

        return Math.max(
            largestBSTSubtree(root.left),
            largestBSTSubtree(root.right)
        );
    }
}
```

---

## Complexity

Time Complexity

```
O(N²)
```

Space Complexity

```
O(N)
```

---

# Approach 3: Post‑Order Traversal (Optimal)

## Key Insight

Instead of recomputing subtree information repeatedly, we **propagate information upward**.

Each subtree returns:

- Minimum value in subtree
- Maximum value in subtree
- Size of largest BST

This allows the parent node to determine if the subtree forms a BST in **constant time**.

---

## Data Returned From Each Node

```
(minValue, maxValue, sizeOfBST)
```

If subtree is NOT BST → return

```
(-∞, +∞)
```

so parent fails BST validation.

---

## Java Implementation

```java
class NodeValue {

    int minNode;
    int maxNode;
    int maxSize;

    NodeValue(int minNode, int maxNode, int maxSize) {
        this.minNode = minNode;
        this.maxNode = maxNode;
        this.maxSize = maxSize;
    }
}

class Solution {

    public NodeValue largestBSTSubtreeHelper(TreeNode root) {

        if (root == null) {
            return new NodeValue(Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
        }

        NodeValue left = largestBSTSubtreeHelper(root.left);
        NodeValue right = largestBSTSubtreeHelper(root.right);

        if (left.maxNode < root.val && root.val < right.minNode) {

            return new NodeValue(
                Math.min(root.val, left.minNode),
                Math.max(root.val, right.maxNode),
                left.maxSize + right.maxSize + 1
            );
        }

        return new NodeValue(
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Math.max(left.maxSize, right.maxSize)
        );
    }

    public int largestBSTSubtree(TreeNode root) {
        return largestBSTSubtreeHelper(root).maxSize;
    }
}
```

---

## Complexity

Time Complexity

```
O(N)
```

Each node is processed exactly once.

Space Complexity

```
O(N)
```

due to recursion stack in worst case (skewed tree).
