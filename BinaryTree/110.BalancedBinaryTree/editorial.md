# Balanced Binary Tree — Detailed Approaches

## Overview

A binary tree **T** is considered **balanced** if for every node `p` in the tree:

```
| height(p.left) - height(p.right) | ≤ 1
```

If there exists any node where the difference in height between the left and right subtree is greater than 1, the tree is **not balanced**.

This property naturally suggests that each **subtree should be treated as a subproblem**.

The key question becomes:

> In what order should we evaluate these subproblems?

Two primary strategies exist:

1. **Top‑Down Recursion**
2. **Bottom‑Up Recursion**

---

# Approach 1: Top‑Down Recursion

## Intuition

We compute the height of subtrees first and then verify if the current node is balanced.

A tree rooted at `r` is balanced if:

1. The heights of its left and right subtrees differ by at most 1.
2. Both left and right subtrees are also balanced.

---

## Height Definition

For a node `p`:

```
height(p) =
    -1                               if p is null
    1 + max(height(p.left), height(p.right)) otherwise
```

---

## Algorithm

```
isBalanced(root):
    if root == NULL:
        return true

    if |height(root.left) - height(root.right)| > 1:
        return false

    return isBalanced(root.left) AND isBalanced(root.right)
```

---

## Java Implementation

```java
class Solution {

    private int height(TreeNode root) {
        if (root == null) {
            return -1;
        }

        return 1 + Math.max(height(root.left), height(root.right));
    }

    public boolean isBalanced(TreeNode root) {

        if (root == null) {
            return true;
        }

        return (
            Math.abs(height(root.left) - height(root.right)) < 2 &&
            isBalanced(root.left) &&
            isBalanced(root.right)
        );
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n log n)
```

Reason:

For a node at depth `d`, the height function may be called `d` times.

The number of nodes in a balanced tree follows:

```
f(h) = f(h-1) + f(h-2) + 1
```

Which resembles the **Fibonacci recurrence**.

Thus the height of a balanced tree is bounded by:

```
O(log n)
```

Therefore the total work becomes:

```
O(n log n)
```

Worst case (skewed tree without early stopping):

```
O(n²)
```

However early stopping limits this to approximately `O(n)` in skewed scenarios.

---

### Space Complexity

```
O(n)
```

In the worst case the recursion stack may contain all nodes (skewed tree).

---

# Approach 2: Bottom‑Up Recursion

## Intuition

The top‑down approach recomputes subtree heights multiple times.

To eliminate redundancy, we compute the height **while returning whether the subtree is balanced**.

This means each subtree is processed **only once**.

---

## Algorithm

1. Recursively evaluate left subtree.
2. Recursively evaluate right subtree.
3. If either subtree is not balanced → stop immediately.
4. Compare heights of the subtrees.
5. Return both:

```
(height, balanced)
```

---

## Helper Structure

We store both height and balance information together.

```java
final class TreeInfo {
    public final int height;
    public final boolean balanced;

    public TreeInfo(int height, boolean balanced) {
        this.height = height;
        this.balanced = balanced;
    }
}
```

---

## Java Implementation

```java
final class TreeInfo {

    public final int height;
    public final boolean balanced;

    public TreeInfo(int height, boolean balanced) {
        this.height = height;
        this.balanced = balanced;
    }
}

class Solution {

    private TreeInfo isBalancedTreeHelper(TreeNode root) {

        if (root == null) {
            return new TreeInfo(-1, true);
        }

        TreeInfo left = isBalancedTreeHelper(root.left);

        if (!left.balanced) {
            return new TreeInfo(-1, false);
        }

        TreeInfo right = isBalancedTreeHelper(root.right);

        if (!right.balanced) {
            return new TreeInfo(-1, false);
        }

        if (Math.abs(left.height - right.height) < 2) {
            return new TreeInfo(Math.max(left.height, right.height) + 1, true);
        }

        return new TreeInfo(-1, false);
    }

    public boolean isBalanced(TreeNode root) {
        return isBalancedTreeHelper(root).balanced;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

Each node is processed exactly **once**.

Height is computed during recursion without recomputation.

---

### Space Complexity

```
O(n)
```

Recursion stack may grow up to `O(n)` in skewed trees.

For balanced trees:

```
O(log n)
```

---

# Approach Comparison

| Approach  | Time Complexity | Space Complexity | Key Idea                              |
| --------- | --------------- | ---------------- | ------------------------------------- |
| Top‑Down  | O(n log n)      | O(n)             | Recompute height repeatedly           |
| Bottom‑Up | O(n)            | O(n)             | Compute height while checking balance |

---

# Recommended Approach

The **Bottom‑Up recursion** method is generally preferred because:

- No redundant height calculations
- Linear time complexity
- Early stopping when imbalance is detected
