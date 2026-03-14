# Minimum Depth of Binary Tree — Detailed Approaches

## Problem Goal

Given the **root of a binary tree**, return the **minimum number of nodes** along the shortest path from the root to the **nearest leaf node**.

A **leaf node** is a node with:

node.left == null AND node.right == null

---

# Approach 1: Depth-First Search (DFS)

## Intuition

We can divide the problem into **subproblems**.

If:

minDepth(left subtree) = x
minDepth(right subtree) = y

Then the depth of the current node becomes:

1 + min(x, y)

The `+1` accounts for the **current root node**.

However, there is one important edge case.

### Important Edge Case

If one child is `null`, we **cannot** use:

min(leftDepth, rightDepth)

because the null child would incorrectly return `0`.

Instead:

- If `left == null` → go right
- If `right == null` → go left

---

## Algorithm

1. If `root == null` → return `0`
2. If `root.left == null` → return `1 + dfs(root.right)`
3. If `root.right == null` → return `1 + dfs(root.left)`
4. Otherwise return:

1 + min(dfs(root.left), dfs(root.right))

---

## Java Implementation

```java
class Solution {

    private int dfs(TreeNode root) {

        if (root == null) {
            return 0;
        }

        if (root.left == null) {
            return 1 + dfs(root.right);
        }

        if (root.right == null) {
            return 1 + dfs(root.left);
        }

        return 1 + Math.min(dfs(root.left), dfs(root.right));
    }

    public int minDepth(TreeNode root) {
        return dfs(root);
    }
}
```

---

## Complexity Analysis

### Time Complexity

O(N)

Every node is visited exactly once.

### Space Complexity

O(N)

Worst case recursion stack equals tree height.

---

# Approach 2: Breadth-First Search (BFS)

## Intuition

BFS traverses the tree **level by level**.

Since we are looking for the **minimum depth**, the **first leaf node encountered during BFS must be the answer**.

---

## Algorithm

1. If `root == null` → return `0`
2. Initialize queue and add root
3. Set depth = 1
4. While queue not empty:
   - Iterate through current level
   - If node is leaf → return depth
   - Add children to queue
5. Increment depth after each level

---

## Java Implementation

```java
class Solution {

    public int minDepth(TreeNode root) {

        if (root == null) {
            return 0;
        }

        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);

        int depth = 1;

        while (!q.isEmpty()) {

            int size = q.size();

            while (size > 0) {

                TreeNode node = q.remove();
                size--;

                if (node.left == null && node.right == null) {
                    return depth;
                }

                if (node.left != null) {
                    q.add(node.left);
                }

                if (node.right != null) {
                    q.add(node.right);
                }
            }

            depth++;
        }

        return -1;
    }
}
```

---

# Complexity Analysis

### Time Complexity

O(N)

Each node is processed once.

### Space Complexity

O(N)

Queue may store up to N nodes in worst case.

---

# Approach Comparison

| Approach | Strategy               | Time | Space | Notes                                |
| -------- | ---------------------- | ---- | ----- | ------------------------------------ |
| DFS      | Recursive depth search | O(N) | O(N)  | Simple but may traverse deeper paths |
| BFS      | Level order traversal  | O(N) | O(N)  | Stops early at first leaf            |

---

# Recommended Approach

BFS is often preferred because the **first leaf encountered guarantees the minimum depth**.
