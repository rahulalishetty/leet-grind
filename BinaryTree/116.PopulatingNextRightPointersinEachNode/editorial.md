# 116. Populating Next Right Pointers in Each Node

`Medium` | Tree | BFS | DFS | Linked List

---

## Problem Summary

Given a **perfect binary tree**, populate each node's `next` pointer to point to its next right node on the same level. If no such node exists, set `next` to `NULL`.

```
        1                        1 → NULL
      /   \                    /   \
     2     3        →         2  →  3 → NULL
    / \   / \               /  \  /  \
   4   5 6   7             4→  5→6 →  7 → NULL
```

---

## Approach 1: BFS (Level Order Traversal)

### Intuition

Process the tree level by level using a queue. For each level, link nodes left to right using `next` pointers. The last node of each level points to `NULL`.

### Algorithm

1. Add root to the queue.
2. For each level, iterate through all nodes in the queue.
3. Link each node to the next node in the same level.
4. Set the last node's `next` to `NULL`.
5. Enqueue left and right children for the next level.

### Complexity

- **Time:** O(n) — each node is visited once
- **Space:** O(n) — queue holds up to n/2 nodes at the widest level

### Java Code

```java
class Solution {
    public Node connect(Node root) {
        if (root == null) return null;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size(); // number of nodes at current level

            for (int i = 0; i < size; i++) {
                Node node = queue.poll();

                // Link to next node (not the last node in this level)
                if (i < size - 1) {
                    node.next = queue.peek();
                }

                // Enqueue children for next level
                if (node.left != null)  queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }

        return root;
    }
}
```

---

## Approach 2: Recursive (DFS)

### Intuition

For a perfect binary tree, every parent has exactly two children. We can exploit this structure recursively:

- Connect `left.next = right` (same parent — easy).
- Connect `right.next = parent.next.left` (cross-parent — use the already-connected `next` of the current node).

### Algorithm

1. If `root` is null or a leaf, return.
2. Connect `root.left.next = root.right`.
3. If `root.next` exists, connect `root.right.next = root.next.left`.
4. Recurse on left and right subtrees.

> **Key insight:** Since we process top-down and left-to-right, by the time we process a node's right child's `next`, the current level's `next` pointers are already set.

### Complexity

- **Time:** O(n) — each node visited once
- **Space:** O(log n) — recursion stack depth equals tree height

### Java Code

```java
class Solution {
    public Node connect(Node root) {
        if (root == null || root.left == null) return root;

        // Connect left child to right child (same parent)
        root.left.next = root.right;

        // Connect right child to left child of next subtree
        if (root.next != null) {
            root.right.next = root.next.left;
        }

        // Recurse on both subtrees
        connect(root.left);
        connect(root.right);

        return root;
    }
}
```

---

## Approach 3: Iterative with O(1) Space (Optimal)

### Intuition

Use the already-established `next` pointers of the current level to wire up the next level — like using one level as a "linked list" to navigate while building the next level's connections.

Think of it as processing level by level:

- Use a pointer `curr` to traverse nodes at the current level via `next`.
- For each `curr`, connect its children.

### Algorithm

1. Start with `leftmost = root` (first node of each level).
2. While `leftmost.left != null` (not at leaf level):
   - Traverse the current level using `curr = leftmost`.
   - For each `curr`:
     - `curr.left.next = curr.right`
     - If `curr.next != null`: `curr.right.next = curr.next.left`
   - Move to the next level: `leftmost = leftmost.left`

### Complexity

- **Time:** O(n) — each node visited once
- **Space:** O(1) — no extra data structures (follow-up requirement satisfied ✅)

### Java Code

```java
class Solution {
    public Node connect(Node root) {
        if (root == null) return null;

        Node leftmost = root; // first node of each level

        while (leftmost.left != null) { // stop at leaf level
            Node curr = leftmost;

            while (curr != null) {
                // Connect left child to right child
                curr.left.next = curr.right;

                // Connect right child to left child of the next node
                if (curr.next != null) {
                    curr.right.next = curr.next.left;
                }

                // Move to next node in the current level
                curr = curr.next;
            }

            // Move down to next level
            leftmost = leftmost.left;
        }

        return root;
    }
}
```

---

## Visual Walkthrough (Approach 3)

```
Level 0:  leftmost = 1
          curr = 1
            → 2.next = 3
            → (1.next is null, skip cross-parent link)
          Move down: leftmost = 2

Level 1:  leftmost = 2
          curr = 2
            → 4.next = 5
            → 5.next = 3.left = 6   (using 2.next = 3)
          curr = 3 (via 2.next)
            → 6.next = 7
            → (3.next is null, skip)
          Move down: leftmost = 4

Level 2:  leftmost = 4 → 4.left is null → STOP
```

---

## Comparison Table

| Approach             | Time | Space    | Notes                         |
| -------------------- | ---- | -------- | ----------------------------- |
| BFS (Queue)          | O(n) | O(n)     | Simple and intuitive          |
| Recursive (DFS)      | O(n) | O(log n) | Clean and elegant             |
| Iterative O(1) Space | O(n) | **O(1)** | Optimal — satisfies follow-up |

---

## Recommended Approach

**Approach 3 (Iterative O(1) Space)** is the best solution overall:

- Meets the O(1) space follow-up requirement.
- Leverages the perfect binary tree property elegantly.
- Easy to understand once you see the "use current level to build the next" pattern.

Use **Approach 1 (BFS)** if you need a quick, generalizable solution (also works for non-perfect trees in [Problem 117](https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii/)).
