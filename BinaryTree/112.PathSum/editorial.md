# 112. Path Sum

**Difficulty:** Easy
**Topic:** Binary Tree, DFS, BFS, Recursion

---

## Problem Statement

Given the root of a binary tree and an integer `targetSum`, return `true` if the tree has a **root-to-leaf path** such that adding up all the values along the path equals `targetSum`.

> A **leaf** is a node with no children.

---

## Approach 1: Recursive DFS (Brute Force)

### Explanation

The most intuitive approach — recursively traverse every root-to-leaf path, carrying a running sum. At each node, subtract the node's value from the remaining target. When a leaf is reached, check if the remaining target equals zero.

**Steps:**

1. Base case: if `root == null`, return `false`.
2. Subtract `root.val` from `targetSum`.
3. If it's a leaf (`left == null && right == null`), return `targetSum == 0`.
4. Recurse on left and right subtrees; return `true` if either returns `true`.

**Time Complexity:** `O(N)` — every node is visited once.
**Space Complexity:** `O(H)` — recursion stack depth equals tree height `H`. Worst case `O(N)` for a skewed tree, `O(log N)` for a balanced tree.

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        // Base case: empty node
        if (root == null) return false;

        // Subtract current node's value
        targetSum -= root.val;

        // If it's a leaf, check if remaining sum is 0
        if (root.left == null && root.right == null) {
            return targetSum == 0;
        }

        // Recurse on left and right subtrees
        return hasPathSum(root.left, targetSum)
            || hasPathSum(root.right, targetSum);
    }
}
```

---

## Approach 2: Iterative DFS (Using Stack)

### Explanation

Simulate the recursive DFS using an explicit stack. Each stack entry stores a `(node, remainingSum)` pair. This avoids recursion overhead and stack overflow risk for very deep trees.

**Steps:**

1. Push `(root, targetSum - root.val)` onto the stack.
2. Pop a node and its remaining sum.
3. If it's a leaf and remaining sum is `0`, return `true`.
4. Push non-null children with updated remaining sums.
5. If the stack empties without a match, return `false`.

**Time Complexity:** `O(N)` — every node is visited once.
**Space Complexity:** `O(H)` — stack holds at most one path's worth of nodes.

```java
import java.util.Stack;

class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;

        // Stack of [node, remaining sum]
        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Integer> sumStack = new Stack<>();

        nodeStack.push(root);
        sumStack.push(targetSum - root.val);

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int remaining = sumStack.pop();

            // Leaf check
            if (node.left == null && node.right == null && remaining == 0) {
                return true;
            }

            // Push right child first so left is processed first
            if (node.right != null) {
                nodeStack.push(node.right);
                sumStack.push(remaining - node.right.val);
            }
            if (node.left != null) {
                nodeStack.push(node.left);
                sumStack.push(remaining - node.left.val);
            }
        }

        return false;
    }
}
```

---

## Approach 3: Iterative BFS (Using Queue)

### Explanation

Use BFS (level-order traversal) with a queue instead of a stack. The logic is identical to iterative DFS, but nodes are processed level by level. This doesn't change worst-case complexity but may find a matching path faster in wide, shallow trees.

**Time Complexity:** `O(N)`
**Space Complexity:** `O(W)` where `W` is the maximum width of the tree. Worst case `O(N)` for a perfect binary tree's last level.

```java
import java.util.LinkedList;
import java.util.Queue;

class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> sumQueue = new LinkedList<>();

        nodeQueue.offer(root);
        sumQueue.offer(targetSum - root.val);

        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            int remaining = sumQueue.poll();

            // Leaf check
            if (node.left == null && node.right == null && remaining == 0) {
                return true;
            }

            if (node.left != null) {
                nodeQueue.offer(node.left);
                sumQueue.offer(remaining - node.left.val);
            }
            if (node.right != null) {
                nodeQueue.offer(node.right);
                sumQueue.offer(remaining - node.right.val);
            }
        }

        return false;
    }
}
```

---

## Approach 4: Optimized Recursive DFS (Tail-Recursion Style)

### Explanation

A minor but clean optimization of Approach 1. Instead of subtracting from `targetSum` as we go down, we pass a **running sum** upward and compare with `targetSum` only at leaf nodes. This makes the intent clearer and is slightly more cache-friendly in some JVM implementations.

> In practice, this and Approach 1 have the same performance. The real optimization is in code clarity and avoiding redundant operations.

**Time Complexity:** `O(N)`
**Space Complexity:** `O(H)`

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        return dfs(root, 0, targetSum);
    }

    private boolean dfs(TreeNode node, int currentSum, int targetSum) {
        if (node == null) return false;

        currentSum += node.val;

        // Leaf node: check if accumulated sum matches target
        if (node.left == null && node.right == null) {
            return currentSum == targetSum;
        }

        // Explore both subtrees
        return dfs(node.left, currentSum, targetSum)
            || dfs(node.right, currentSum, targetSum);
    }
}
```

---

## Comparison Table

| Approach            | Time | Space | Style     | Notes                         |
| ------------------- | ---- | ----- | --------- | ----------------------------- |
| Recursive DFS       | O(N) | O(H)  | Recursive | Simplest, most readable       |
| Iterative DFS       | O(N) | O(H)  | Iterative | Stack-safe for deep trees     |
| Iterative BFS       | O(N) | O(W)  | Iterative | Better for wide/shallow trees |
| Optimized Recursive | O(N) | O(H)  | Recursive | Cleaner accumulation logic    |

> `H` = tree height, `W` = max tree width, `N` = number of nodes.

---

## TreeNode Definition (for reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
```

---

## Key Takeaways

- **Leaf check is critical** — only check the sum at leaf nodes (`left == null && right == null`), not at every node.
- **Short-circuit evaluation** — using `||` in the recursive call means the right subtree is only visited if the left subtree doesn't find a path. This can save time in practice.
- **Negative values are allowed** — the constraints allow negative node values and a negative `targetSum`, so you cannot prune early based on sum exceeding the target.
- **Recursive DFS** is the go-to solution for interviews due to its simplicity and clarity.
