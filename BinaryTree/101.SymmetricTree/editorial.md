# 101. Symmetric Tree

**Difficulty:** Easy
**Topic:** Binary Tree, BFS, DFS, Recursion

---

## Problem Statement

Given the root of a binary tree, check whether it is a **mirror of itself** (i.e., symmetric around its center).

```
Input: root = [1,2,2,3,4,4,3]
Output: true

Input: root = [1,2,2,null,3,null,3]
Output: false
```

---

## Core Insight

A tree is symmetric if the **left subtree is a mirror of the right subtree**.

Two trees are mirrors of each other when:

1. Their root values are equal
2. The **left child** of one mirrors the **right child** of the other (and vice versa)

```
        1
       / \
      2   2        ← same values
     / \ / \
    3  4 4  3      ← outer pair (3,3) and inner pair (4,4) match
```

---

## Approach 1 — Recursive DFS

### Intuition

Define a helper `isMirror(left, right)` that checks whether two subtrees are mirror images. At each step:

- Both null → symmetric ✅
- One null, one not → asymmetric ❌
- Values differ → asymmetric ❌
- Otherwise → recurse: compare `left.left` with `right.right` AND `left.right` with `right.left`

### Diagram

```
isMirror(2, 2)
├── values equal? 2 == 2 ✅
├── isMirror(3, 3)   ← left.left  vs right.right
│   └── both null children → true ✅
└── isMirror(4, 4)   ← left.right vs right.left
    └── both null children → true ✅
```

### Code

```java
class Solution {
    public boolean isSymmetric(TreeNode root) {
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode left, TreeNode right) {
        // Base case 1: both nodes are null → symmetric
        if (left == null && right == null) return true;

        // Base case 2: one is null, the other isn't → asymmetric
        if (left == null || right == null) return false;

        // Check: current values match AND children mirror each other
        return (left.val == right.val)
            && isMirror(left.left,  right.right)   // outer pair
            && isMirror(left.right, right.left);   // inner pair
    }
}
```

### Complexity

|           |                                                                         |
| --------- | ----------------------------------------------------------------------- |
| **Time**  | O(n) — every node is visited once                                       |
| **Space** | O(h) — recursion stack, h = tree height (O(log n) balanced, O(n) worst) |

---

## Approach 2 — Iterative BFS (Queue)

### Intuition

Simulate the recursive logic using a queue. At each step, dequeue **two nodes** that should be mirrors of each other and validate them. Then enqueue their children in mirror order:

- `left.left` paired with `right.right`
- `left.right` paired with `right.left`

### Diagram

```
Queue state (pairs):
[(2,2)]
  → dequeue, values match → enqueue (3,3) and (4,4)
[(3,3), (4,4)]
  → dequeue (3,3), match → enqueue (null,null) x2
  → dequeue (4,4), match → enqueue (null,null) x2
[(null,null), (null,null), (null,null), (null,null)]
  → all null pairs → true ✅
```

### Code

```java
import java.util.LinkedList;
import java.util.Queue;

class Solution {
    public boolean isSymmetric(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();

        // Seed the queue with the two children of root
        queue.offer(root.left);
        queue.offer(root.right);

        while (!queue.isEmpty()) {
            TreeNode left  = queue.poll();
            TreeNode right = queue.poll();

            // Both null → this pair is symmetric, continue
            if (left == null && right == null) continue;

            // One null or values differ → not symmetric
            if (left == null || right == null)   return false;
            if (left.val != right.val)            return false;

            // Enqueue children in mirror order
            queue.offer(left.left);
            queue.offer(right.right);

            queue.offer(left.right);
            queue.offer(right.left);
        }

        return true;
    }
}
```

### Complexity

|           |                                                                   |
| --------- | ----------------------------------------------------------------- |
| **Time**  | O(n) — every node is visited once                                 |
| **Space** | O(n) — queue can hold up to O(n) nodes (widest level of the tree) |

---

## Approach 3 — Iterative DFS (Stack)

### Intuition

Identical logic to the BFS approach, but uses a **stack** instead of a queue. The traversal order differs (LIFO vs FIFO), but the symmetry check is the same. Useful when you want DFS behaviour without recursion (e.g., to avoid stack overflow on very deep trees).

### Code

```java
import java.util.Stack;

class Solution {
    public boolean isSymmetric(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();

        stack.push(root.left);
        stack.push(root.right);

        while (!stack.isEmpty()) {
            TreeNode right = stack.pop();
            TreeNode left  = stack.pop();

            if (left == null && right == null) continue;
            if (left == null || right == null)  return false;
            if (left.val != right.val)           return false;

            // Push pairs in mirror order
            stack.push(left.left);
            stack.push(right.right);

            stack.push(left.right);
            stack.push(right.left);
        }

        return true;
    }
}
```

### Complexity

|           |      |
| --------- | ---- |
| **Time**  | O(n) |
| **Space** | O(n) |

---

## Approach Comparison

| Approach      | Time | Space | Notes                                                    |
| ------------- | ---- | ----- | -------------------------------------------------------- |
| Recursive DFS | O(n) | O(h)  | Cleanest code; risk of stack overflow on very deep trees |
| Iterative BFS | O(n) | O(n)  | Level-order traversal; explicit queue                    |
| Iterative DFS | O(n) | O(n)  | Same as recursive logic, no call stack risk              |

> **h** = height of tree. For a balanced tree h = O(log n); for a skewed tree h = O(n).

---

## Edge Cases

| Input                   | Expected | Reason                          |
| ----------------------- | -------- | ------------------------------- |
| `[1]`                   | `true`   | Single node is always symmetric |
| `[1,2,2,null,3,null,3]` | `false`  | Inner children don't mirror     |
| `[1,2,3]`               | `false`  | Children have different values  |
| `[1,null,null]`         | `true`   | Both children null              |

---

## Recommended Approach

Use **Approach 1 (Recursive)** for interviews — it is the most readable and directly expresses the definition of symmetry. Switch to **Approach 2 or 3** if the tree depth could cause a stack overflow.
