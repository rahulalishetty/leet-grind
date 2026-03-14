# 1660. Correct a Binary Tree — Detailed Approaches

## Overview

In this problem, there is a defective node **fromNode** and it incorrectly points to another node **toNode**. The `toNode` is at the **same depth** and is located **to the right** of `fromNode`.

We need to **remove `fromNode` and all of its descendants** from the tree.

Removing the node means replacing the reference to that node from its parent with `null`.

Keeping the reference to the **parent node** is helpful.

---

## Important Constraints

1. **All `node.val` values are unique**

   This means we can safely use `node.val` as a unique identifier for nodes if needed.

2. **`fromNode != toNode`**

3. **`fromNode` and `toNode` exist in the tree**

4. **Both nodes are on the same depth**

5. **`toNode` is to the right of `fromNode`**

6. **`fromNode.right` was originally null**

---

# Approach 1: Breadth‑First Search (BFS)

## Intuition

Because the corrupted pointer connects nodes **on the same depth**, exploring the tree **level‑by‑level** is natural.

BFS (Level Order Traversal) helps us examine nodes at the same depth.

The key insight:

If we traverse **from right to left**, the node that the defective pointer points to will **already be visited**.

Thus we detect corruption when:

```
node.right is already visited
```

At that moment:

```
node = fromNode
```

Then we remove this node from its parent.

---

## Algorithm

1. Create a queue for BFS.
2. Store **pairs of (node, parent)** in the queue.
3. Traverse the tree level by level.
4. For each level:
   - Maintain a `visited` set
   - Traverse nodes from **right to left**

5. For each node:
   - If `node.right` is already in `visited`, then this node is the defective node.
   - Remove it from its parent.
   - Return the root.

6. Otherwise:
   - Add the node to the visited set.
   - Push its children into the queue.

---

## Java Implementation

```java
class Solution {
    public TreeNode correctBinaryTree(TreeNode root) {

        Queue<TreeNode[]> queue = new LinkedList<>();
        queue.add(new TreeNode[]{root, null});

        while (!queue.isEmpty()) {

            int size = queue.size();
            Set<TreeNode> visited = new HashSet<>();

            for (int i = 0; i < size; i++) {

                TreeNode[] pair = queue.poll();
                TreeNode node = pair[0];
                TreeNode parent = pair[1];

                if (visited.contains(node.right)) {

                    if (parent.left == node) {
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }

                    return root;
                }

                visited.add(node);

                if (node.right != null) {
                    queue.add(new TreeNode[]{node.right, node});
                }

                if (node.left != null) {
                    queue.add(new TreeNode[]{node.left, node});
                }
            }
        }

        return root;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each node is processed once.

Operations per node:

- queue operations
- set lookup
- set insertion

All are constant time.

---

### Space Complexity

```
O(N)
```

Space used by:

- queue
- visited set

---

# Approach 2: Depth‑First Search (DFS)

## Intuition

Instead of level traversal, we can traverse **right‑first** using DFS.

Why right‑first?

Because the defective pointer always points to a node **to the right**.

So if we traverse the **rightmost branch first**, we will see the target node before the defective node.

We keep a **set of visited nodes**.

If during traversal we encounter a node whose:

```
node.right is already visited
```

Then this node is the defective `fromNode`.

So we remove it by returning `null`.

---

## Algorithm

1. Maintain a `visited` set.
2. Perform DFS.
3. Traverse in this order:

```
node
right
left
```

4. If `node.right` is already visited:

```
this node is defective
return null
```

5. Otherwise:
   - add node to visited
   - recursively build right subtree
   - recursively build left subtree

---

## Java Implementation

```java
class Solution {

    Set<Integer> visited = new HashSet<>();

    public TreeNode correctBinaryTree(TreeNode root) {

        if (root == null) {
            return null;
        }

        if (root.right != null && visited.contains(root.right.val)) {
            return null;
        }

        visited.add(root.val);

        root.right = correctBinaryTree(root.right);
        root.left = correctBinaryTree(root.left);

        return root;
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
O(N)
```

Used by:

- recursion stack
- visited set

---

# BFS vs DFS

| Approach | Time | Space | Notes                      |
| -------- | ---- | ----- | -------------------------- |
| BFS      | O(N) | O(N)  | Intuitive level traversal  |
| DFS      | O(N) | O(N)  | Cleaner recursive solution |

---

# Key Insight of the Problem

The corruption occurs when:

```
node.right points to a node already visited on the same level
```

Therefore the solution strategy is:

```
Traverse nodes from right to left
Detect backward pointer
Remove defective subtree
```

---

# Interview Takeaway

The most important insight is:

**Traversal order matters.**

Right‑first traversal ensures the `toNode` is visited **before** the defective `fromNode`.

This makes detecting the corrupted pointer possible in **O(N)** time.
