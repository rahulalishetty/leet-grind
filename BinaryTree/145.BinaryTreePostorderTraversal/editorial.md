# Binary Tree Postorder Traversal — Detailed Approaches

## Overview

To traverse a tree, we use two main strategies:

### Breadth-First Search (BFS)

This strategy scans the tree **level by level** from top to bottom, visiting nodes at higher levels before nodes at lower levels.

### Depth-First Search (DFS)

DFS explores **as far down a branch as possible before backtracking**.

Types of DFS traversal:

- **Preorder:** Root → Left → Right
- **Inorder:** Left → Root → Right
- **Postorder:** Left → Right → Root

---

# Tree Traversal Example

For a binary tree represented as:

```
[1, null, 2, 3]
```

The structure is:

```
1
 \
  2
 /
3
```

### Postorder Traversal

Order:

```
Left → Right → Root
```

Traversal sequence:

```
3 → 2 → 1
```

Output:

```
[3,2,1]
```

---

# Approach 1: Recursive Postorder Traversal

## Intuition

Recursion treats each node as the root of its own subtree.

Steps:

1. Traverse the **left subtree**
2. Traverse the **right subtree**
3. Process the **current node**

The recursion stops when the current node becomes `null`.

## Algorithm

1. Define helper function `postorderTraversalHelper`
2. If node is `null`, return
3. Traverse left subtree
4. Traverse right subtree
5. Add node value to result

## Implementation

```java
class Solution {

    private void postorderTraversalHelper(TreeNode currentNode, List<Integer> result) {

        if (currentNode == null) {
            return;
        }

        postorderTraversalHelper(currentNode.left, result);
        postorderTraversalHelper(currentNode.right, result);

        result.add(currentNode.val);
    }

    public List<Integer> postorderTraversal(TreeNode root) {

        List<Integer> result = new ArrayList<>();

        postorderTraversalHelper(root, result);

        return result;
    }
}
```

## Complexity

Time Complexity: **O(n)**
Each node visited once.

Space Complexity: **O(n)**
Recursion stack may grow to height of tree.

---

# Approach 2: Manipulating Preorder Traversal (Iterative Hack)

## Intuition

Postorder =

```
Left → Right → Root
```

Modified preorder:

```
Root → Right → Left
```

If we reverse the result:

```
Left → Right → Root
```

We obtain postorder.

## Algorithm

1. Use a stack.
2. Process node.
3. Traverse **right first**, then **left**.
4. Reverse the final result.

## Implementation

```java
class Solution {

    public List<Integer> postorderTraversal(TreeNode root) {

        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode node = root;

        while (node != null || !stack.isEmpty()) {

            if (node != null) {

                result.add(node.val);
                stack.push(node);
                node = node.right;

            } else {

                node = stack.pop();
                node = node.left;
            }
        }

        Collections.reverse(result);
        return result;
    }
}
```

## Complexity

Time Complexity: **O(n)**

Space Complexity: **O(n)**

---

# Approach 3: Two Stack Postorder Traversal

## Intuition

Two stacks help reverse processing order.

Process:

1. Push nodes from first stack to second stack.
2. Second stack automatically produces **postorder sequence**.

## Algorithm

1. Push root into stack1.
2. Pop from stack1 and push to stack2.
3. Push left and right children to stack1.
4. Pop stack2 for final result.

## Implementation

```java
class Solution {

    public List<Integer> postorderTraversal(TreeNode root) {

        List<Integer> result = new ArrayList<>();

        if (root == null) return result;

        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();

        stack1.push(root);

        while (!stack1.isEmpty()) {

            TreeNode node = stack1.pop();
            stack2.push(node);

            if (node.left != null) stack1.push(node.left);
            if (node.right != null) stack1.push(node.right);
        }

        while (!stack2.isEmpty()) {
            result.add(stack2.pop().val);
        }

        return result;
    }
}
```

## Complexity

Time Complexity: **O(n)**

Space Complexity: **O(n)**

---

# Approach 4: Single Stack Postorder Traversal

## Intuition

Use:

- **one stack**
- **previous pointer**

The pointer tracks whether the right subtree has already been processed.

## Algorithm

1. Traverse left while pushing nodes.
2. Check right subtree.
3. If right subtree not processed → go right.
4. Otherwise process node.

## Implementation

```java
class Solution {

    public List<Integer> postorderTraversal(TreeNode root) {

        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode prev = null;

        while (root != null || !stack.isEmpty()) {

            if (root != null) {

                stack.push(root);
                root = root.left;

            } else {

                TreeNode node = stack.peek();

                if (node.right == null || node.right == prev) {

                    result.add(node.val);
                    stack.pop();
                    prev = node;

                } else {

                    root = node.right;
                }
            }
        }

        return result;
    }
}
```

## Complexity

Time Complexity: **O(n)**

Space Complexity: **O(n)**

---

# Approach 5: Morris Traversal (O(1) Space)

## Intuition

Morris Traversal modifies the tree temporarily to simulate recursion without using stack.

Key idea:

Create **temporary threads** using inorder predecessors.

This allows traversal without additional memory.

## Algorithm

1. Create dummy node.
2. Find predecessor of each node.
3. Create temporary thread.
4. Reverse subtree traversal.
5. Restore original tree structure.

## Implementation

```java
class Solution {

    public List<Integer> postorderTraversal(TreeNode root) {

        List<Integer> result = new ArrayList<>();

        TreeNode dummy = new TreeNode(-1);
        dummy.left = root;

        TreeNode cur = dummy;
        TreeNode prev = null;

        while (cur != null) {

            if (cur.left == null) {
                cur = cur.right;
            } else {

                prev = cur.left;

                while (prev.right != null && prev.right != cur) {
                    prev = prev.right;
                }

                if (prev.right == null) {
                    prev.right = cur;
                    cur = cur.left;
                } else {

                    addReverse(cur.left, prev, result);

                    prev.right = null;
                    cur = cur.right;
                }
            }
        }

        return result;
    }

    private void addReverse(TreeNode from, TreeNode to, List<Integer> result) {

        reverse(from, to);

        TreeNode node = to;

        while (true) {

            result.add(node.val);

            if (node == from) break;

            node = node.right;
        }

        reverse(to, from);
    }

    private void reverse(TreeNode start, TreeNode end) {

        TreeNode prev = null;
        TreeNode cur = start;
        TreeNode next;

        while (prev != end) {

            next = cur.right;
            cur.right = prev;
            prev = cur;
            cur = next;
        }
    }
}
```

## Complexity

Time Complexity: **O(n)**

Space Complexity: **O(1)**
