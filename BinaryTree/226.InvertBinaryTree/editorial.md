# Invert Binary Tree — Solution Approaches

## Approach 1: Recursive

This is a classic binary tree problem and is naturally suited for recursion.

### Algorithm

- The inverse of an empty tree is an empty tree.
- For a node `r`:
  - Recursively invert the **right subtree**
  - Recursively invert the **left subtree**
  - Swap them

Conceptually:

```
Original:
    r
   / \\
 left right

Inverted:
    r
   / \\
right left
```

### Java Implementation

```java
class Solution {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode right = invertTree(root.right);
        TreeNode left = invertTree(root.left);

        root.left = right;
        root.right = left;

        return root;
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n)
```

Each node is visited exactly once.

**Space Complexity**

```
O(h)
```

Where `h` is the height of the tree.

Worst case (skewed tree):

```
O(n)
```

because recursion depth equals the number of nodes.

---

# Approach 2: Iterative (Breadth‑First Search)

Instead of recursion, we can perform a **level-order traversal** using a queue.

### Algorithm

1. Create a queue.
2. Insert the root node.
3. While the queue is not empty:
   - Remove a node
   - Swap its left and right children
   - Add the children to the queue
4. Continue until all nodes are processed.

This approach mirrors **BFS traversal**.

### Java Implementation

```java
class Solution {

    public TreeNode invertTree(TreeNode root) {

        if (root == null) return null;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {

            TreeNode current = queue.poll();

            TreeNode temp = current.left;
            current.left = current.right;
            current.right = temp;

            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }

        return root;
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n)
```

Every node is processed exactly once.

**Space Complexity**

```
O(n)
```

In the worst case, the queue can contain all nodes at the largest level of the tree.

For a full binary tree, the last level can contain:

```
≈ n / 2 nodes
```

So the space complexity becomes:

```
O(n)
```
