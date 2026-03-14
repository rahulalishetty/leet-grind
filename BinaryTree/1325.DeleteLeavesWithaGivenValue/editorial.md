# 1325. Delete Leaves With a Given Value — Approaches

## Overview

We are given:

- A binary tree **root**
- An integer **target**

Our objective is to **delete all leaf nodes** of the binary tree whose value equals **target**.

A **leaf node** is a node that **has no children**.

### Key Observations

- When deleting nodes, the **binary tree structure must remain valid**.
- Removing a leaf node may cause its **parent to become a new leaf node**.
- If that new leaf node also has value **target**, it must also be removed.
- This process continues until **no more qualifying nodes exist**.

---

# Approach 1: Recursion (Postorder Traversal)

## Intuition

Deleting a child node might transform a parent node into a **new leaf node**.
Therefore we must process nodes **from the bottom of the tree upward**.

This naturally leads to **Postorder Traversal**:

```
Left → Right → Node
```

By processing children first, we ensure:

- Descendant nodes are already processed.
- We correctly identify **new leaf nodes created by deletions**.

---

## Algorithm

### Base Case

If `root == null`, return `null`.

### Recursive Traversal

1. Recursively process the **left subtree**.
2. Recursively process the **right subtree**.

```
root.left = removeLeafNodes(root.left, target)
root.right = removeLeafNodes(root.right, target)
```

### Node Evaluation

If the current node:

- has **no children**
- and **value == target**

then return `null` (delete the node).

Otherwise return the node unchanged.

---

## Implementation

```java
class Solution {

    public TreeNode removeLeafNodes(TreeNode root, int target) {

        if (root == null) {
            return null;
        }

        root.left = removeLeafNodes(root.left, target);
        root.right = removeLeafNodes(root.right, target);

        if (root.left == null && root.right == null && root.val == target) {
            return null;
        }

        return root;
    }
}
```

---

## Complexity Analysis

Let **n = number of nodes**.

### Time Complexity

```
O(n)
```

Each node is visited exactly once.

---

### Space Complexity

```
O(n)
```

Worst case recursion stack height = tree height.

For skewed trees:

```
height = n
```

---

# Approach 2: Iterative Postorder Traversal

## Intuition

The recursive solution uses the **call stack**.

We can simulate this behavior with a **manual stack** to perform **postorder traversal** iteratively.

Key idea:

- Traverse left subtree first
- Then right subtree
- Then process the node

We must also track whether the **right subtree was already visited** to avoid revisiting nodes.

---

## Key Variables

| Variable      | Purpose                           |
| ------------- | --------------------------------- |
| stack         | stores nodes during traversal     |
| currentNode   | current traversal node            |
| lastRightNode | tracks last visited right subtree |

---

## Algorithm

1. Initialize stack
2. Start traversal from root
3. Push all left children onto stack
4. Check right subtree availability
5. If right subtree exists and not visited → explore it
6. Otherwise process the node
7. If node becomes leaf and value == target → delete it
8. Continue until stack empty

---

## Implementation

```java
class Solution {

    public TreeNode removeLeafNodes(TreeNode root, int target) {

        Stack<TreeNode> stack = new Stack<>();
        TreeNode currentNode = root, lastRightNode = null;

        while (!stack.isEmpty() || currentNode != null) {

            while (currentNode != null) {
                stack.push(currentNode);
                currentNode = currentNode.left;
            }

            currentNode = stack.peek();

            if (currentNode.right != lastRightNode && currentNode.right != null) {
                currentNode = currentNode.right;
                continue;
            }

            stack.pop();

            if (
                currentNode.left == null &&
                currentNode.right == null &&
                currentNode.val == target
            ) {

                if (stack.isEmpty()) {
                    return null;
                }

                TreeNode parent = stack.peek();

                if (parent.left == currentNode) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            }

            lastRightNode = currentNode;
            currentNode = null;
        }

        return root;
    }
}
```

---

## Complexity Analysis

Let **n = number of nodes**.

### Time Complexity

```
O(n)
```

Each node is processed exactly once.

---

### Space Complexity

```
O(n)
```

Worst case stack size = tree height.

For skewed tree:

```
height = n
```

---

# Summary

| Approach  | Technique       | Time | Space |
| --------- | --------------- | ---- | ----- |
| Recursive | Postorder DFS   | O(n) | O(n)  |
| Iterative | Stack Postorder | O(n) | O(n)  |

---

## Key Insight

The **postorder traversal order** is essential because:

- Children must be processed first.
- Only then can we determine whether a parent **became a leaf node** after deletions.
