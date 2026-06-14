# 236. Lowest Common Ancestor of a Binary Tree

## Problem Summary

Given a binary tree and two nodes `p` and `q`, find their **Lowest Common Ancestor (LCA)** — the deepest node that has both `p` and `q` as descendants (a node can be a descendant of itself).

---

## Approach 1: Recursive DFS (Optimal)

### Intuition

Perform a post-order DFS. At each node, ask: _"Does my left subtree contain p or q? Does my right subtree?"_

- If **both** left and right return non-null → current node is the LCA.
- If only **one side** returns non-null → that side found both nodes (one is ancestor of the other), bubble it up.
- If the **current node** is `p` or `q` → return it immediately (it could be the LCA if the other is in its subtree).

### Code

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Base case: empty node or found p or q
        if (root == null || root == p || root == q) {
            return root;
        }

        // Search left and right subtrees
        TreeNode left  = lowestCommonAncestor(root.left,  p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // p and q found on opposite sides → current node is LCA
        if (left != null && right != null) {
            return root;
        }

        // Both in one subtree → return the non-null result
        return left != null ? left : right;
    }
}
```

### Walkthrough (Example: p=5, q=4, root=3)

```
         3
        / \
       5   1
      / \ / \
     6  2 0  8
       / \
      7   4

DFS reaches node 4 → returns 4
DFS reaches node 7 → returns null
Node 2: left=null, right=4 → returns 4
Node 6: returns null
Node 5: left=null, right=4 → but root==p(5), so returns 5 immediately ✓
```

### Complexity

|           |                                                                         |
| --------- | ----------------------------------------------------------------------- |
| **Time**  | O(N) — visits every node once                                           |
| **Space** | O(H) — recursion stack (H = tree height; O(log N) balanced, O(N) worst) |

---

## Approach 2: Iterative with Parent Pointers

### Intuition

Use BFS/DFS to record the **parent of every node**. Then trace ancestors of `p` upward, storing them in a set. Then trace ancestors of `q` upward until hitting a node already in the set — that is the LCA.

### Code

```java
import java.util.*;

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Map each node to its parent
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        Deque<TreeNode> stack = new ArrayDeque<>();

        parent.put(root, null);
        stack.push(root);

        // Iterate until we've found both p and q
        while (!parent.containsKey(p) || !parent.containsKey(q)) {
            TreeNode node = stack.pop();
            if (node.left != null) {
                parent.put(node.left, node);
                stack.push(node.left);
            }
            if (node.right != null) {
                parent.put(node.right, node);
                stack.push(node.right);
            }
        }

        // Collect all ancestors of p (including p itself)
        Set<TreeNode> ancestors = new HashSet<>();
        while (p != null) {
            ancestors.add(p);
            p = parent.get(p);
        }

        // First ancestor of q that appears in p's ancestor set is the LCA
        while (!ancestors.contains(q)) {
            q = parent.get(q);
        }
        return q;
    }
}
```

### Walkthrough (Example: p=5, q=1, root=3)

```
Parent map (partial): {3→null, 5→3, 1→3, 6→5, 2→5, 0→1, 8→1, ...}

Ancestors of p=5: {5, 3}

Trace q=1 upward:
  1 → not in {5,3}
  parent(1) = 3 → 3 is in {5,3} ✓  →  LCA = 3
```

### Complexity

|           |                                  |
| --------- | -------------------------------- |
| **Time**  | O(N)                             |
| **Space** | O(N) — parent map + ancestor set |

---

## Approach 3: Iterative with Eulerian Path / Node Path Tracking

### Intuition

Find the **full root-to-node paths** for both `p` and `q`, then compare them element by element. The last node where the paths agree is the LCA.

### Code

```java
import java.util.*;

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathP = new ArrayList<>();
        List<TreeNode> pathQ = new ArrayList<>();

        findPath(root, p, pathP);
        findPath(root, q, pathQ);

        // Walk both paths together; last common node is the LCA
        TreeNode lca = root;
        int len = Math.min(pathP.size(), pathQ.size());
        for (int i = 0; i < len; i++) {
            if (pathP.get(i) == pathQ.get(i)) {
                lca = pathP.get(i);
            } else {
                break;
            }
        }
        return lca;
    }

    private boolean findPath(TreeNode node, TreeNode target, List<TreeNode> path) {
        if (node == null) return false;
        path.add(node);
        if (node == target) return true;
        if (findPath(node.left, target, path) || findPath(node.right, target, path)) {
            return true;
        }
        path.remove(path.size() - 1); // backtrack
        return false;
    }
}
```

### Walkthrough (Example: p=5, q=4)

```
pathP (root→5):  [3, 5]
pathQ (root→4):  [3, 5, 2, 4]

Compare index by index:
  i=0: 3 == 3 → lca = 3
  i=1: 5 == 5 → lca = 5
  i=2: paths differ (pathP ended) → break

LCA = 5 ✓
```

### Complexity

|           |                                         |
| --------- | --------------------------------------- |
| **Time**  | O(N)                                    |
| **Space** | O(N) — two path lists + recursion stack |

---

## Approach 4: Morris-style Iterative DFS (No Recursion, No Parent Map)

### Intuition

Simulate the recursive approach iteratively using an **explicit stack that mimics the call stack**. Each stack entry holds the node and a state (0 = unvisited, 1 = left done, 2 = both done) so we can process in post-order. The LCA result is propagated upward through a `result` variable.

### Code

```java
import java.util.*;

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        Deque<TreeNode[]> stack = new ArrayDeque<>();
        // Each entry: [node, state, leftResult, rightResult]
        // We'll use a simpler simulation approach:

        // Use a map to track child results as we unwind
        Map<TreeNode, TreeNode> childResult = new HashMap<>();
        Deque<TreeNode> order = new ArrayDeque<>();
        Set<TreeNode> visited = new HashSet<>();

        // Iterative post-order traversal
        Deque<TreeNode> iterStack = new ArrayDeque<>();
        iterStack.push(root);
        while (!iterStack.isEmpty()) {
            TreeNode node = iterStack.peek();
            if (node.left != null && !visited.contains(node.left) && !visited.contains(node)) {
                iterStack.push(node.left);
            } else if (node.right != null && !visited.contains(node.right)) {
                visited.add(node); // mark left as processed
                iterStack.push(node.right);
            } else {
                order.push(iterStack.pop());
                visited.add(node);
            }
        }

        // Process nodes in post-order (leaves first)
        TreeNode result = null;
        Map<TreeNode, TreeNode> lcaMap = new HashMap<>();
        while (!order.isEmpty()) {
            TreeNode node = order.pop();
            TreeNode left  = lcaMap.get(node.left);
            TreeNode right = lcaMap.get(node.right);
            if (node == p || node == q) {
                lcaMap.put(node, node);
            } else if (left != null && right != null) {
                lcaMap.put(node, node);
            } else {
                lcaMap.put(node, left != null ? left : right);
            }
        }
        return lcaMap.get(root);
    }
}
```

> **Note:** This iterative simulation faithfully mirrors Approach 1 but avoids the call stack entirely — useful when tree depth could cause a stack overflow (N up to 10^5).

### Complexity

|           |                               |
| --------- | ----------------------------- |
| **Time**  | O(N)                          |
| **Space** | O(N) — explicit stack and map |

---

## Comparison Table

| Approach                | Time | Space | Style           | Best For                         |
| ----------------------- | ---- | ----- | --------------- | -------------------------------- |
| 1. Recursive DFS        | O(N) | O(H)  | Clean & concise | Most interviews                  |
| 2. Parent Pointer       | O(N) | O(N)  | Iterative       | When recursion disallowed        |
| 3. Path Comparison      | O(N) | O(N)  | Intuitive       | Explaining step-by-step          |
| 4. Iterative Post-order | O(N) | O(N)  | No-recursion    | Deep trees (stack overflow risk) |

---

## Key Insight

The elegance of **Approach 1** is that it never explicitly searches for LCA — it just returns whatever non-null node it finds, and the post-order traversal ensures the first node where **both sides are non-null** must be the LCA.

```
                    null ← only p found below
                     ↑
        LCA (root) ← both sides non-null → return root
                     ↑
                    p or q ← return immediately
```
