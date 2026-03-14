# 1372. Longest ZigZag Path in a Binary Tree — Approach

## Overview

We are given the **root node of a binary tree**.

Our task is to find the **longest ZigZag path** contained in the tree.

A ZigZag path alternates between **left and right directions** at every step.

---

# Approach: Depth First Search (DFS)

## Intuition

From every node we have **two possible moves**:

- Move to the **left child**
- Move to the **right child**

The ZigZag rule requires that **each step alternates direction**.

---

## Observations

Assume we are at a parent node **p**.

### Case 1: `p` is a left child

If `p` has:

- **Left child (l)**
  The direction is the same → cannot extend ZigZag.
  Start a **new ZigZag path** with length `1`.

- **Right child (r)**
  The direction changes → ZigZag continues.
  Increase path length by `1`.

---

### Case 2: `p` is a right child

If `p` has:

- **Left child (l)**
  Direction changes → ZigZag continues.

- **Right child (r)**
  Same direction → must start a **new path**.

---

## Key Idea

While traversing the tree we track:

- the **current direction**
- the **current ZigZag length**

We maintain a global variable:

```
pathLength
```

which stores the **maximum ZigZag length found so far**.

---

# DFS Strategy

Define a recursive function:

```
dfs(node, goLeft, steps)
```

Where:

- `node` → current node
- `goLeft` → direction to continue ZigZag
- `steps` → current ZigZag length

---

## DFS Logic

1. If node is `null`, return.
2. Update maximum ZigZag length.
3. Depending on direction:

### If continuing left

```
dfs(node.left, false, steps + 1)   // continue zigzag
dfs(node.right, true, 1)           // start new path
```

### If continuing right

```
dfs(node.left, false, 1)           // start new path
dfs(node.right, true, steps + 1)   // continue zigzag
```

---

# Algorithm

1. Initialize

```
pathLength = 0
```

2. Define DFS recursion.

3. Start DFS from root:

```
dfs(root, true, 0)
```

4. Return `pathLength`.

---

# Implementation

```java
class Solution {
    int pathLength = 0;

    private void dfs(TreeNode node, boolean goLeft, int steps) {
        if (node == null) {
            return;
        }

        pathLength = Math.max(pathLength, steps);

        if (goLeft) {
            dfs(node.left, false, steps + 1);
            dfs(node.right, true, 1);
        } else {
            dfs(node.left, false, 1);
            dfs(node.right, true, steps + 1);
        }
    }

    public int longestZigZag(TreeNode root) {
        dfs(root, true, 0);
        return pathLength;
    }
}
```

---

# Complexity Analysis

Let **n** be the number of nodes in the tree.

## Time Complexity

```
O(n)
```

DFS visits every node once.

---

## Space Complexity

```
O(n)
```

In the worst case (skewed tree), the recursion stack may contain `n` nodes.
