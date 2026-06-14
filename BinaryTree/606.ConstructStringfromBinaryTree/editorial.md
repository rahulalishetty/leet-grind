# Binary Tree to String — Java Solutions

## Problem Summary

Given the root of a binary tree, construct a string using **preorder traversal** with these rules:

- Each node is represented by its integer value.
- Children are wrapped in parentheses immediately after the parent's value.
- **Left child** always gets its own `(...)` if it exists.
- **Right child** gets its own `(...)` after the left.
- If a node has **only a right child**, insert an empty `()` for the missing left child to preserve structure.
- If a node has **only a left child** (no right child), omit the trailing `()` — no ambiguity arises.

### Example Tree

```
      1
     / \
    2   3
   /
  4
```

**Output:** `"1(2(4))(3)"`

---

## Approach 1: Recursive DFS (Most Intuitive)

### Explanation

Perform a preorder traversal recursively. At each node:

1. Append the node's value.
2. If the node has a left child, recurse into it wrapped in `()`.
3. If the node has a right child, recurse into it wrapped in `()`.
4. **Special case:** If there is no left child but there is a right child, append `()` first (placeholder for missing left), then recurse right.

### Code

```java
class Solution {
    public String tree2str(TreeNode root) {
        if (root == null) return "";

        StringBuilder sb = new StringBuilder();
        buildString(root, sb);
        return sb.toString();
    }

    private void buildString(TreeNode node, StringBuilder sb) {
        if (node == null) return;

        sb.append(node.val); // Step 1: append current node value

        // Step 2: handle left and right children
        if (node.left != null || node.right != null) {
            sb.append('(');
            buildString(node.left, sb);  // left subtree (may be empty)
            sb.append(')');

            if (node.right != null) {   // right subtree only if it exists
                sb.append('(');
                buildString(node.right, sb);
                sb.append(')');
            }
        }
    }
}
```

### Trace for `1(2(4))(3)`

| Call        | Action                        | Result so far |
| ----------- | ----------------------------- | ------------- |
| `node=1`    | Append `1`, has both children | `1`           |
| `node=2`    | Append `(2`, has left child   | `1(2`         |
| `node=4`    | Append `(4)`, no children     | `1(2(4)`      |
| Back to `2` | Close left paren, no right    | `1(2(4))`     |
| Back to `1` | Open right paren              | `1(2(4))(`    |
| `node=3`    | Append `3`, no children       | `1(2(4))(3`   |
| Back to `1` | Close right paren             | `1(2(4))(3)`  |

### Complexity

|           | Value                                          |
| --------- | ---------------------------------------------- |
| **Time**  | O(n) — each node visited once                  |
| **Space** | O(h) — recursion stack depth (h = tree height) |

---

## Approach 2: Iterative with Stack

### Explanation

Simulate preorder traversal using an explicit stack. The challenge is knowing **when to close parentheses**. The trick: use a `Set` to track nodes already visited (i.e., whose children have been processed). When we pop a node that is already in the visited set, we close a `(`.

**Algorithm steps:**

1. Push the root onto the stack.
2. While the stack is not empty:
   - Peek at the top node.
   - If not yet visited: mark it visited, append its value, and push children in reverse order (right first, then left), with parentheses as markers.
   - If already visited: pop it and append `)`.

A cleaner alternative (used below) is to push sentinel/null markers onto the stack to represent closing parentheses.

```java
import java.util.Stack;

class Solution {
    public String tree2str(TreeNode root) {
        if (root == null) return "";

        StringBuilder sb = new StringBuilder();
        Stack<TreeNode> stack = new Stack<>();
        Stack<TreeNode> visited = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.peek();

            if (!visited.isEmpty() && visited.peek() == node) {
                // Already processed children — time to close
                visited.pop();
                stack.pop();
                sb.append(')');
            } else {
                // First visit: print value and open parens
                visited.push(node);
                sb.append('(');
                sb.append(node.val);

                // Push right first (processed last in LIFO)
                if (node.right != null) stack.push(node.right);

                // If no left but right exists, add empty placeholder
                if (node.left == null && node.right != null) {
                    sb.append("()");
                }

                if (node.left != null) stack.push(node.left);
            }
        }

        // The root's outer parens are extra — strip them
        return sb.substring(1, sb.length() - 1);
    }
}
```

### Complexity

|           | Value                                         |
| --------- | --------------------------------------------- |
| **Time**  | O(n)                                          |
| **Space** | O(n) — stack may hold all nodes in worst case |

---

## Approach 3: String Concatenation (Simple but Slower)

### Explanation

The simplest possible formulation: build the string directly with `+` in a recursive helper, returning it up the call stack. This avoids any mutable state but creates many intermediate `String` objects.

Best for readability and interviews where performance isn't critical.

```java
class Solution {
    public String tree2str(TreeNode root) {
        if (root == null) return "";

        String result = String.valueOf(root.val);

        if (root.left == null && root.right == null) {
            return result; // Leaf node — nothing to add
        }

        result += "(" + tree2str(root.left) + ")"; // Always add left paren block

        if (root.right != null) {
            result += "(" + tree2str(root.right) + ")"; // Add right only if exists
        }

        return result;
    }
}
```

### Why `root.left == null && root.right == null`?

If neither child exists, return early. Otherwise, left is always printed (even as empty `()`), and right is printed only if non-null. This naturally handles the "empty left placeholder" rule.

### Complexity

|           | Value                                                    |
| --------- | -------------------------------------------------------- |
| **Time**  | O(n²) in worst case due to repeated string concatenation |
| **Space** | O(h) for recursion stack                                 |

---

## Approach 4: Post-order with StringBuilder (Optimized Iterative)

### Explanation

A variant of iterative DFS that avoids a `visited` set by encoding state directly in how we push to the stack. We push `null` sentinels as "close parenthesis" markers.

```java
import java.util.Deque;
import java.util.ArrayDeque;

class Solution {
    public String tree2str(TreeNode root) {
        if (root == null) return "";

        StringBuilder sb = new StringBuilder();
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        // Use a parallel boolean stack to mark "close paren after this"
        Deque<Boolean> needsClose = new ArrayDeque<>();
        needsClose.push(false); // root doesn't need outer wrapping

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            boolean close = needsClose.pop();

            if (node == null) {
                sb.append(')'); // Sentinel: close a paren
                continue;
            }

            if (close) sb.append('(');
            sb.append(node.val);

            boolean hasLeft = node.left != null;
            boolean hasRight = node.right != null;

            if (hasLeft || hasRight) {
                // Push right subtree (or closing paren for it)
                if (hasRight) {
                    stack.push(null);       // close right
                    needsClose.push(false);
                    stack.push(node.right);
                    needsClose.push(true);  // open right
                }

                // Push empty left placeholder if needed
                if (!hasLeft && hasRight) {
                    sb.append("()");
                }

                // Push left subtree
                if (hasLeft) {
                    stack.push(null);       // close left
                    needsClose.push(false);
                    stack.push(node.left);
                    needsClose.push(true);  // open left
                }
            }

            if (close) { /* closing handled by null sentinel */ }
        }

        return sb.toString();
    }
}
```

> **Note:** This approach is included for completeness. The recursive `StringBuilder` approach (Approach 1) achieves the same O(n) complexity with less code.

---

## Comparison Summary

| Approach                | Time  | Space | Code Simplicity | Notes                       |
| ----------------------- | ----- | ----- | --------------- | --------------------------- |
| **1 — Recursive DFS**   | O(n)  | O(h)  | ⭐⭐⭐⭐⭐      | Best all-round choice       |
| **2 — Iterative Stack** | O(n)  | O(n)  | ⭐⭐⭐          | Avoids recursion limit      |
| **3 — String Concat**   | O(n²) | O(h)  | ⭐⭐⭐⭐⭐      | Easy to understand, slower  |
| **4 — Sentinel Stack**  | O(n)  | O(n)  | ⭐⭐            | Most complex, minor benefit |

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

## Key Rule Recap

```
Node has only left child   →  1(2)        ✅ no trailing ()
Node has only right child  →  1()(3)      ✅ empty () required for left
Node has both children     →  1(2)(3)     ✅ both shown
Node is a leaf             →  1           ✅ no parens at all
```
