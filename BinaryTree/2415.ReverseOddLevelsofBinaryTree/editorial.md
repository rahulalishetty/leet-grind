# 2415. Reverse Odd Levels of Binary Tree — Approaches

## Overview

We are given the root of a **perfect binary tree**, and our task is to return the root after **reversing the values at the odd levels** of the tree.

A binary tree is considered **perfect** if:

- All parent nodes have **exactly two children**.
- All leaves are on the **same level**.

The **level of a node** is defined as the **number of edges along the path between it and the root node**.

---

# Approach 1: Depth-First Search (DFS)

## Intuition

Binary trees naturally support recursion because each node’s left and right children can themselves be treated as roots of smaller binary trees.

Using this property, we can traverse the tree recursively and process pairs of nodes that are **mirror images of each other**.

While traversing:

- If the level is **even (0-based)**, we **swap the values** of the two mirrored nodes.
- If the level is **odd**, we leave them unchanged.

Because the tree is **perfect and symmetric**, we can recursively pair:

- `leftChild.left` with `rightChild.right`
- `leftChild.right` with `rightChild.left`

This mirroring ensures that values are reversed correctly across the level.

---

## Algorithm

### Main Function

`reverseOddLevels(root)`

1. Call `traverseDFS(root.left, root.right, 0)`
2. Return `root`

### Recursive Function

`traverseDFS(leftChild, rightChild, level)`

1. **Base Case**
   - If either child is `null`, return.

2. **Swap condition**
   - If `level % 2 == 0`, swap values of `leftChild` and `rightChild`.

3. **Recursive Calls**
   - `traverseDFS(leftChild.left, rightChild.right, level + 1)`
   - `traverseDFS(leftChild.right, rightChild.left, level + 1)`

---

## Implementation

```java
class Solution {

    public TreeNode reverseOddLevels(TreeNode root) {
        traverseDFS(root.left, root.right, 0);
        return root;
    }

    private void traverseDFS(
        TreeNode leftChild,
        TreeNode rightChild,
        int level
    ) {
        if (leftChild == null || rightChild == null) {
            return;
        }

        // Swap values on odd levels (based on 0 indexing)
        if (level % 2 == 0) {
            int temp = leftChild.val;
            leftChild.val = rightChild.val;
            rightChild.val = temp;
        }

        traverseDFS(leftChild.left, rightChild.right, level + 1);
        traverseDFS(leftChild.right, rightChild.left, level + 1);
    }
}
```

---

## Complexity Analysis

Let **n** be the number of nodes in the tree.

### Time Complexity

```
O(n)
```

Every node is visited exactly once during the DFS traversal. Each swap operation takes constant time.

---

### Space Complexity

```
O(log n)
```

Since the tree is **perfect**, its height is `log n`.
The recursion stack depth therefore becomes `O(log n)`.

---

# Approach 2: Breadth-First Search (BFS)

## Intuition

Instead of recursion, we can process the tree **level by level** using **Breadth-First Search**.

Steps:

1. Use a **queue** to perform level-order traversal.
2. For each level:
   - Store nodes in a list.
3. If the level is **odd**, reverse the node values.

The queue always contains nodes from **only one level at a time**, allowing easy reversal when needed.

---

## Algorithm

1. Create a queue and add the root node.
2. Initialize `level = 0`.
3. While queue is not empty:
   - Get the number of nodes at the current level (`size`).
   - Create `currentLevelNodes` list.

4. Process all nodes in the current level:
   - Remove node from queue.
   - Add node to `currentLevelNodes`.
   - Add its children to queue.

5. If `level % 2 == 1`:
   - Reverse values in `currentLevelNodes` using two pointers.

6. Increment `level`.

7. Continue until traversal completes.

8. Return the modified root.

---

## Implementation

```java
class Solution {

    public TreeNode reverseOddLevels(TreeNode root) {
        if (root == null) {
            return null;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<TreeNode> currentLevelNodes = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                currentLevelNodes.add(node);

                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }

            if (level % 2 == 1) {
                int left = 0;
                int right = currentLevelNodes.size() - 1;

                while (left < right) {
                    int temp = currentLevelNodes.get(left).val;
                    currentLevelNodes.get(left).val =
                        currentLevelNodes.get(right).val;
                    currentLevelNodes.get(right).val = temp;

                    left++;
                    right--;
                }
            }

            level++;
        }

        return root;
    }
}
```

---

## Complexity Analysis

Let **n** be the number of nodes.

### Time Complexity

```
O(n)
```

BFS traversal visits every node exactly once.

---

### Space Complexity

```
O(n)
```

The queue may store up to an entire level of nodes in the tree.

In a perfect binary tree, the largest level may contain approximately **n/2 nodes**, giving **O(n)** auxiliary space.
