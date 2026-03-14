# 1379. Find a Corresponding Node of a Binary Tree in a Clone of That Tree — Approaches

## Overview

### Core Idea

Traverse **both trees simultaneously**.

Once the **target node** is found in the **original tree**, return the **corresponding node from the cloned tree**.

Because the cloned tree is an **exact structural copy**, every traversal step can be mirrored.

---

# Tree Traversal Options

There are two main traversal strategies:

## DFS — Depth First Search

DFS explores **deep into the tree before backtracking**.

Traversal styles:

- Preorder
- Inorder
- Postorder

DFS usually uses a **stack or recursion**.

---

## BFS — Breadth First Search

BFS explores **level by level**.

Traversal order:

```
Level 0
Level 1
Level 2
...
```

BFS uses a **queue**.

---

### Which One Should Be Used?

The problem description does not strongly favor either.

However:

- **DFS inorder traversal** is the most common interview solution.
- BFS is also valid but less commonly expected.

---

# Can We Solve It in Constant Space?

Technically yes, using **Morris Traversal**.

But Morris traversal **modifies the tree temporarily**, which is **not allowed in this problem**.

Therefore constant space solutions are not valid here.

---

# Handling Duplicate Values

If values are **unique**, we can compare node values:

```java
if (node_o.val == target.val)
```

If **duplicates are allowed**, we must compare node references:

```java
if (node_o == target)
```

---

# Approach 1: DFS — Recursive Inorder Traversal

## Intuition

Inorder traversal follows:

```
Left → Node → Right
```

We traverse **original and cloned trees simultaneously**.

When the target node is found in the original tree, we return the corresponding node in the cloned tree.

---

## Implementation

```java
class Solution {
    TreeNode ans, target;

    public void inorder(TreeNode o, TreeNode c) {
        if (o != null) {
            inorder(o.left, c.left);

            if (o == target) {
                ans = c;
            }

            inorder(o.right, c.right);
        }
    }

    public TreeNode getTargetCopy(TreeNode original, TreeNode cloned, TreeNode target) {
        this.target = target;
        inorder(original, cloned);
        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Every node is visited once.

---

### Space Complexity

```
O(H)
```

Where **H = height of the tree**.

Worst case (skewed tree):

```
O(N)
```

Balanced tree:

```
O(log N)
```

---

# Approach 2: DFS — Iterative Inorder Traversal

## Intuition

Instead of recursion, use **two stacks**:

- one for the original tree
- one for the cloned tree

Traverse left nodes first, just like recursive inorder traversal.

---

## Implementation

```java
class Solution {

    public TreeNode getTargetCopy(TreeNode original, TreeNode cloned, TreeNode target) {
        Deque<TreeNode> stack_o = new ArrayDeque<>();
        Deque<TreeNode> stack_c = new ArrayDeque<>();

        TreeNode node_o = original;
        TreeNode node_c = cloned;

        while (!stack_o.isEmpty() || node_o != null) {

            while (node_o != null) {
                stack_o.push(node_o);
                stack_c.push(node_c);

                node_o = node_o.left;
                node_c = node_c.left;
            }

            node_o = stack_o.pop();
            node_c = stack_c.pop();

            if (node_o == target) {
                return node_c;
            }

            node_o = node_o.right;
            node_c = node_c.right;
        }

        return null;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Every node is processed once.

---

### Space Complexity

```
O(H)
```

Worst case:

```
O(N)
```

---

# Approach 3: BFS — Level Order Traversal

## Intuition

Perform **level order traversal** on both trees simultaneously.

Use two queues:

- one for original tree nodes
- one for cloned tree nodes

Whenever the target node is found in the original tree, return the cloned node.

---

## Algorithm

1. Push root nodes into queues.
2. While queues are not empty:
   - Pop nodes.
   - If original node equals target → return cloned node.
   - Push children into queues.

---

## Implementation

```java
class Solution {

    public TreeNode getTargetCopy(TreeNode original, TreeNode cloned, TreeNode target) {

        Deque<TreeNode> queue_o = new ArrayDeque<>();
        queue_o.offer(original);

        Deque<TreeNode> queue_c = new ArrayDeque<>();
        queue_c.offer(cloned);

        while (!queue_o.isEmpty()) {

            TreeNode node_o = queue_o.poll();
            TreeNode node_c = queue_c.poll();

            if (node_o == target) {
                return node_c;
            }

            if (node_o.left != null) {
                queue_o.offer(node_o.left);
                queue_c.offer(node_c.left);
            }

            if (node_o.right != null) {
                queue_o.offer(node_o.right);
                queue_c.offer(node_c.right);
            }
        }

        return null;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each node is visited once.

---

### Space Complexity

```
O(N)
```

The queue may hold up to **N/2 nodes** in a complete binary tree.
