# Count Univalue Subtrees — Solution Approaches

## Overview

Given the root of a binary tree, return the **number of uni-value subtrees**.

A **uni-value subtree** means **all nodes in the subtree have the same value**.

---

# Approach 1: Depth First Search (DFS)

## Intuition

A subtree rooted at a node is a **uni-value subtree** if:

1. Both children are uni-value subtrees.
2. The children (if they exist) have the **same value as the current node**.

Leaf nodes automatically satisfy these conditions because their subtree contains only themselves.

DFS works well because we must process **children before determining the parent's status**.

---

## Key Idea

For each node:

- Recursively check the left subtree.
- Recursively check the right subtree.
- If both are uni-value and values match → current subtree is uni-value.

---

## Algorithm

1. Maintain a counter `count`.
2. Perform DFS starting from root.
3. For each node:
   - If node is null → return true.
   - Recursively evaluate left subtree.
   - Recursively evaluate right subtree.
4. If both subtrees are uni-value:
   - Check if child values match current node.
   - If yes → increment count.
5. Return whether current subtree is uni-value.

---

## Java Implementation

```java
class Solution {

    int count = 0;

    public boolean dfs(TreeNode node) {

        if (node == null) {
            return true;
        }

        boolean left = dfs(node.left);
        boolean right = dfs(node.right);

        if (left && right) {

            if (node.left != null && node.left.val != node.val) {
                return false;
            }

            if (node.right != null && node.right.val != node.val) {
                return false;
            }

            count++;
            return true;
        }

        return false;
    }

    public int countUnivalSubtrees(TreeNode root) {
        dfs(root);
        return count;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

Each node is visited exactly once.

### Space Complexity

```
O(n)
```

Due to recursion stack in worst case (skewed tree).

---

# Approach 2: DFS Without Global Variable

## Motivation

Using global variables is generally discouraged because:

- They reduce modularity.
- Any function could modify them.
- Harder to reason about program behavior.

Instead we return **multiple values** from DFS.

---

## Idea

The DFS function returns:

```
{isUnivalueSubtree, numberOfUnivalueSubtrees}
```

Where:

- `isUnivalueSubtree` → whether current subtree is uni-value
- `count` → number of uni-value subtrees inside this subtree

---

## Algorithm

1. Perform DFS on left subtree.
2. Perform DFS on right subtree.
3. Combine results.
4. If both subtrees are uni-value and values match → increment count.
5. Return `{true, count}` or `{false, count}`.

---

## Java Implementation

```java
class Solution {

    private Pair<Boolean, Integer> dfs(TreeNode node) {

        if (node == null) {
            return new Pair<>(true, 0);
        }

        Pair<Boolean, Integer> left = dfs(node.left);
        Pair<Boolean, Integer> right = dfs(node.right);

        boolean isLeftUniValue = left.getKey();
        boolean isRightUniValue = right.getKey();

        int count = left.getValue() + right.getValue();

        if (isLeftUniValue && isRightUniValue) {

            if (node.left != null && node.val != node.left.val) {
                return new Pair<>(false, count);
            }

            if (node.right != null && node.val != node.right.val) {
                return new Pair<>(false, count);
            }

            count++;
            return new Pair<>(true, count);
        }

        return new Pair<>(false, count);
    }

    public int countUnivalSubtrees(TreeNode root) {
        return dfs(root).getValue();
    }
}
```

---

# Alternative: Pass-by-Reference Counter

Instead of global variables, we can pass a **mutable container** like an array.

This works because arrays are passed **by reference**.

---

## Java Implementation

```java
class Solution {

    private boolean dfs(TreeNode root, int[] count) {

        if (root == null) {
            return true;
        }

        boolean left = dfs(root.left, count);
        boolean right = dfs(root.right, count);

        if (left && right &&
            (root.left == null || root.left.val == root.val) &&
            (root.right == null || root.right.val == root.val)) {

            count[0]++;
            return true;
        }

        return false;
    }

    public int countUnivalSubtrees(TreeNode root) {

        int[] count = new int[1];
        dfs(root, count);
        return count[0];
    }
}
```

---

# Complexity Analysis

Let **n** be the number of nodes in the tree.

### Time Complexity

```
O(n)
```

Each node is processed exactly once.

### Space Complexity

```
O(n)
```

Worst case recursion depth equals the tree height.
