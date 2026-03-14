# Sum Root to Leaf Numbers — Solution Approaches

## Overview

### Prerequisites

Binary trees can be traversed using **Depth‑First Search (DFS)** in three ways:

- **Preorder:** Root → Left → Right
- **Inorder:** Left → Root → Right
- **Postorder:** Left → Right → Root

This problem naturally fits **DFS preorder traversal**, because we build the number **from root to leaf**.

Traversal direction:

```
Root → Left → Right
```

Since every node must be visited, the optimal time complexity is:

```
O(N)
```

The main goal is to reduce **space complexity**.

Three ways to implement preorder traversal:

1. **Iterative**
2. **Recursive**
3. **Morris Traversal (O(1) space)**

---

# Approach 1: Iterative Preorder Traversal

## Intuition

We simulate recursion using a **stack**.

Steps:

1. Push the root node into the stack.
2. While the stack is not empty:
   - Pop a node
   - Update the current number
   - If the node is a leaf → add to the result
   - Push right and left children

Important note:

Java recommends using **ArrayDeque instead of Stack**.

---

## Implementation

```java
class Solution {
    public int sumNumbers(TreeNode root) {
        int rootToLeaf = 0, currNumber = 0;
        Deque<Pair<TreeNode, Integer>> stack = new ArrayDeque<>();
        stack.push(new Pair(root, 0));

        while (!stack.isEmpty()) {
            Pair<TreeNode, Integer> p = stack.pop();
            root = p.getKey();
            currNumber = p.getValue();

            if (root != null) {
                currNumber = currNumber * 10 + root.val;

                if (root.left == null && root.right == null) {
                    rootToLeaf += currNumber;
                } else {
                    stack.push(new Pair(root.right, currNumber));
                    stack.push(new Pair(root.left, currNumber));
                }
            }
        }

        return rootToLeaf;
    }
}
```

---

## Complexity Analysis

**Time Complexity**

```
O(N)
```

Each node is visited exactly once.

**Space Complexity**

```
O(H)
```

Where **H** is the height of the tree (stack depth).

---

# Approach 2: Recursive Preorder Traversal

## Intuition

The recursive approach directly follows preorder traversal:

```
Root → Left → Right
```

Steps:

1. Update the current number.
2. If the node is a leaf → add it to the total sum.
3. Recursively traverse left subtree.
4. Recursively traverse right subtree.

---

## Implementation

```java
class Solution {
    int rootToLeaf = 0;

    public void preorder(TreeNode node, int currNumber) {

        if (node != null) {

            currNumber = currNumber * 10 + node.val;

            if (node.left == null && node.right == null) {
                rootToLeaf += currNumber;
            }

            preorder(node.left, currNumber);
            preorder(node.right, currNumber);
        }
    }

    public int sumNumbers(TreeNode root) {
        preorder(root, 0);
        return rootToLeaf;
    }
}
```

---

## Complexity Analysis

**Time Complexity**

```
O(N)
```

Each node is visited once.

**Space Complexity**

```
O(H)
```

Recursion stack height equals tree height.

---

# Approach 3: Morris Preorder Traversal (O(1) Space)

## Intuition

Both iterative and recursive approaches require **O(H) stack space**.

To remove this requirement we use **Morris Traversal**, which temporarily modifies the tree structure.

Key idea:

Create temporary links between nodes and their **predecessors**.

```
predecessor.right = root
```

Algorithm logic:

- If the link does not exist → create it and move left
- If the link exists → remove it and move right

This simulates recursion **without using a stack**.

If a node has no left child, simply move right.

---

## Implementation

```java
class Solution {
    public int sumNumbers(TreeNode root) {
        int rootToLeaf = 0, currNumber = 0;
        int steps;
        TreeNode predecessor;

        while (root != null) {

            if (root.left != null) {

                predecessor = root.left;
                steps = 1;

                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                    ++steps;
                }

                if (predecessor.right == null) {
                    currNumber = currNumber * 10 + root.val;
                    predecessor.right = root;
                    root = root.left;
                }

                else {
                    if (predecessor.left == null) {
                        rootToLeaf += currNumber;
                    }

                    for (int i = 0; i < steps; ++i) {
                        currNumber /= 10;
                    }

                    predecessor.right = null;
                    root = root.right;
                }
            }

            else {
                currNumber = currNumber * 10 + root.val;

                if (root.right == null) {
                    rootToLeaf += currNumber;
                }

                root = root.right;
            }
        }

        return rootToLeaf;
    }
}
```

---

## Complexity Analysis

**Time Complexity**

```
O(N)
```

Each node is visited a constant number of times.

**Space Complexity**

```
O(1)
```

No stack or recursion is used.
