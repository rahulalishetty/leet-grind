# LeetCode 104. Maximum Depth of Binary Tree

## Problem Statement

Given the root of a binary tree, return its maximum depth.

**Maximum depth** = number of nodes along the longest path from the root node down to the farthest leaf node.

---

# Approach 1: Recursive DFS (Top-Down)

## Idea

For every node:

1. Compute the depth of the left subtree.
2. Compute the depth of the right subtree.
3. The current node's depth is:

```text
1 + max(leftDepth, rightDepth)
```

### Visualization

```text
        3
      /   \
     9     20
          /  \
         15   7
```

- Depth(15) = 1
- Depth(7) = 1
- Depth(20) = 1 + max(1,1) = 2
- Depth(9) = 1
- Depth(3) = 1 + max(1,2) = 3

Answer = 3

---

## Java Code

```java
class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);

        return 1 + Math.max(leftDepth, rightDepth);
    }
}
```

### Complexity

- Time Complexity: O(n)
- Space Complexity: O(h)

Where:

- n = number of nodes
- h = height of tree

Worst case:

- O(n) space for a skewed tree

Balanced tree:

- O(log n) space

---

# Approach 2: Iterative DFS Using Stack

## Idea

Simulate recursion using an explicit stack.

Store:

- Node
- Current depth

While traversing:

- Pop node
- Update maximum depth
- Push children with depth + 1

---

## Java Code

```java
import java.util.*;

class Solution {

    static class Pair {
        TreeNode node;
        int depth;

        Pair(TreeNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Stack<Pair> stack = new Stack<>();
        stack.push(new Pair(root, 1));

        int maxDepth = 0;

        while (!stack.isEmpty()) {
            Pair current = stack.pop();

            maxDepth = Math.max(maxDepth, current.depth);

            if (current.node.left != null) {
                stack.push(new Pair(current.node.left,
                                    current.depth + 1));
            }

            if (current.node.right != null) {
                stack.push(new Pair(current.node.right,
                                    current.depth + 1));
            }
        }

        return maxDepth;
    }
}
```

### Complexity

- Time Complexity: O(n)
- Space Complexity: O(h)

---

# Approach 3: BFS / Level Order Traversal

## Idea

Each level of the tree contributes exactly 1 to the depth.

Perform level-order traversal:

1. Put root in queue.
2. Process one level at a time.
3. After finishing a level, increment depth.
4. When queue becomes empty, depth equals maximum depth.

---

## Example

```text
Level 1: [3]
Level 2: [9,20]
Level 3: [15,7]
```

Number of levels = 3

Answer = 3

---

## Java Code

```java
import java.util.*;

class Solution {

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int depth = 0;

        while (!queue.isEmpty()) {

            int size = queue.size();

            for (int i = 0; i < size; i++) {

                TreeNode node = queue.poll();

                if (node.left != null) {
                    queue.offer(node.left);
                }

                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            depth++;
        }

        return depth;
    }
}
```

### Complexity

- Time Complexity: O(n)
- Space Complexity: O(w)

Where:

- w = maximum width of the tree

Worst case:

- O(n)

---

# Approach 4: Bottom-Up DFS

## Idea

Think of the problem as:

> What is the height of the tree rooted at the current node?

Formula:

```text
height(node)
    = 1 + max(height(left),
              height(right))
```

Base case:

```text
height(null) = 0
```

This is conceptually identical to Approach 1 but viewed as a height-calculation problem.

---

## Java Code

```java
class Solution {

    public int maxDepth(TreeNode root) {
        return height(root);
    }

    private int height(TreeNode node) {
        if (node == null) {
            return 0;
        }

        return 1 + Math.max(
                height(node.left),
                height(node.right));
    }
}
```

### Complexity

- Time Complexity: O(n)
- Space Complexity: O(h)

---

# Interview Discussion

## Why DFS is the Most Common Answer

Recursive DFS:

- Very short
- Easy to reason about
- Naturally follows tree structure
- Usually expected in interviews

```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;

    return 1 + Math.max(
            maxDepth(root.left),
            maxDepth(root.right));
}
```

---

## When BFS is Better

BFS becomes useful when:

- You need level information.
- You need shortest path in an unweighted tree/graph.
- The problem naturally talks about levels.

Examples:

- Binary Tree Level Order Traversal
- Right Side View
- Minimum Depth of Binary Tree

---

# Summary

| Approach        | Time | Space | Notes                          |
| --------------- | ---- | ----- | ------------------------------ |
| Recursive DFS   | O(n) | O(h)  | Most common interview solution |
| Iterative DFS   | O(n) | O(h)  | Avoids recursion               |
| BFS Level Order | O(n) | O(w)  | Natural level-based solution   |
| Bottom-Up DFS   | O(n) | O(h)  | Tree-height perspective        |

## Recommended Answer

For interviews, use **Recursive DFS** unless the interviewer explicitly asks for an iterative solution.
