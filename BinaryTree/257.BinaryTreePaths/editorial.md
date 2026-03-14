# Binary Tree Paths — Solution Approaches

## Approach 1: Recursion

### Intuition

The most intuitive solution uses **recursion**.

We traverse the tree and maintain the current path from the root to the current node.

For each node:

- If the node is **not a leaf**, append its value to the current path and recursively process its children.
- If the node **is a leaf**, finalize the path and add it to the result list.

---

### Algorithm

1. Create a helper function `construct_paths(node, path, paths)`.
2. Append the current node value to the path.
3. If the node is a **leaf**:
   - Add the completed path to the result list.
4. Otherwise:
   - Extend the path with `"->"`
   - Recursively process the left child.
   - Recursively process the right child.

---

### Java Implementation

```java
class Solution {

  public void construct_paths(TreeNode root, String path, LinkedList<String> paths) {

    if (root != null) {

      path += Integer.toString(root.val);

      if (root.left == null && root.right == null) {
        paths.add(path);
      } else {

        path += "->";

        construct_paths(root.left, path, paths);
        construct_paths(root.right, path, paths);
      }
    }
  }

  public List<String> binaryTreePaths(TreeNode root) {

    LinkedList<String> paths = new LinkedList<>();
    construct_paths(root, "", paths);

    return paths;
  }
}
```

---

### Complexity Analysis

#### Time Complexity

```
O(N log N)
```

- Each node is visited once → `O(N)`
- Path copying costs `O(height)`
- In balanced trees `height ≈ log N`

Thus total complexity:

```
O(N log N)
```

---

#### Space Complexity

```
O(N)
```

Space is used for:

- Recursion stack
- Result list storing all root-to-leaf paths

Worst case (skewed tree):

```
O(N)
```

Balanced tree:

```
O(log N)
```

---

# Approach 2: Iterative (Using Stack)

### Intuition

We can simulate recursion using an **explicit stack**.

The stack stores:

- Nodes to visit
- Their corresponding paths

At each step:

- Pop a node and its path
- If it is a leaf → add path to result
- Otherwise → push children with updated paths

---

### Algorithm

1. Initialize stacks:
   - `node_stack`
   - `path_stack`
2. Push the root node and its value.
3. While stack is not empty:
   - Pop node and path
   - If leaf → add path to result
   - Otherwise → push children with updated paths.

---

### Java Implementation

```java
class Solution {

  public List<String> binaryTreePaths(TreeNode root) {

    LinkedList<String> paths = new LinkedList<>();

    if (root == null)
      return paths;

    LinkedList<TreeNode> node_stack = new LinkedList<>();
    LinkedList<String> path_stack = new LinkedList<>();

    node_stack.add(root);
    path_stack.add(Integer.toString(root.val));

    TreeNode node;
    String path;

    while (!node_stack.isEmpty()) {

      node = node_stack.pollLast();
      path = path_stack.pollLast();

      if (node.left == null && node.right == null) {
        paths.add(path);
      }

      if (node.left != null) {
        node_stack.add(node.left);
        path_stack.add(path + "->" + node.left.val);
      }

      if (node.right != null) {
        node_stack.add(node.right);
        path_stack.add(path + "->" + node.right.val);
      }
    }

    return paths;
  }
}
```

---

### Complexity Analysis

#### Time Complexity

```
O(N)
```

Each node is processed exactly once.

---

#### Space Complexity

```
O(N)
```

In the worst case the stack may store nodes from the entire tree.
