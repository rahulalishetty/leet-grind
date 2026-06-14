# 117. Populating Next Right Pointers in Each Node II

**Difficulty:** Medium
**Topics:** Tree, BFS, Linked List, Binary Tree

---

## Problem Summary

Given a binary tree where each node has an extra `next` pointer, populate every `next` pointer to point to its next right node at the same level. If there is no next right node, set `next` to `NULL`.

```
Input:  [1, 2, 3, 4, 5, null, 7]

        1
       / \
      2   3
     / \    \
    4   5    7

Output: 1 -> #
        2 -> 3 -> #
        4 -> 5 -> 7 -> #
```

---

## Approach 1: BFS with Queue (Level Order Traversal)

### Intuition

The most natural approach is to process the tree level by level using a queue. For each level, we connect all nodes from left to right by setting `node.next = nextNode`.

### Algorithm

1. Push the root into a queue.
2. For each level, iterate through all nodes at that level.
3. For each node (except the last in the level), set its `next` to the next node dequeued.
4. Add left and right children to the queue for the next level.

### Complexity

- **Time:** O(n) — every node is visited once.
- **Space:** O(n) — queue holds at most one full level (up to n/2 nodes).

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

                // Connect to the next node in the same level
                if (i < size - 1) {
                    node.next = queue.peek();
                }

                // Enqueue children for the next level
                if (node.left != null)  queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }

        return root;
    }
}
```

---

## Approach 2: O(1) Space — Using Previously Established `next` Pointers

### Intuition

Once a level's `next` pointers are all set, we can use them to traverse that level like a linked list, and simultaneously wire up the `next` pointers for the level below — without a queue.

Think of it as: use the current level as a "road" to build the highway for the next level.

### Algorithm

1. Start at `curr = root` (current level's leftmost node).
2. Use a **dummy node** as the head of the next level's linked list.
3. For each node `curr` in the current level (traversed via `next`):
   - If `curr.left` exists: attach it to the next level list.
   - If `curr.right` exists: attach it to the next level list.
4. After processing the whole level, move down: `curr = dummy.next`.
5. Repeat until no more levels.

### Complexity

- **Time:** O(n) — every node is visited once.
- **Space:** O(1) — no queue; only a few pointers used.

### Java Code

```java
class Solution {
    public Node connect(Node root) {
        Node curr = root; // pointer to the current level's leftmost node

        while (curr != null) {
            Node dummy = new Node(0);  // dummy head for the next level
            Node tail = dummy;         // tail to build next level's linked list

            // Traverse current level using next pointers
            while (curr != null) {
                if (curr.left != null) {
                    tail.next = curr.left;
                    tail = tail.next;
                }
                if (curr.right != null) {
                    tail.next = curr.right;
                    tail = tail.next;
                }
                curr = curr.next; // move to next node in current level
            }

            // Move to the first node of the next level
            curr = dummy.next;
        }

        return root;
    }
}
```

### Dry Run on Example

```
Level 1: curr = 1
  dummy -> 2 -> 3
  curr = 2

Level 2: curr = 2
  dummy -> 4 -> 5   (from node 2's children)
  dummy -> 4 -> 5 -> 7  (from node 3's right child)
  curr = 4

Level 3: curr = 4
  No children found for 4, 5, or 7
  curr = null → loop ends
```

---

## Approach 3: Recursive (DFS)

### Intuition

We can solve this recursively by processing right subtrees before left subtrees. The tricky part is connecting a node's right child to the first available node in the same level belonging to nodes further right (via already-set `next` pointers).

A helper method `findNext(node)` walks the current level using `next` pointers to locate the first child of any subsequent node.

### Algorithm

1. If `root` is null, return.
2. Find the `next` node for `root.right` (or `root.left` if no right child) using `findNext`.
3. Set the appropriate `next` pointers.
4. Recurse on **right first**, then left (crucial: right must be processed first so its `next` links are ready when we process left).

### Complexity

- **Time:** O(n²) in the worst case (skewed trees), O(n log n) average for balanced trees due to `findNext` traversal.
- **Space:** O(h) — implicit call stack, where h is tree height. O(log n) for balanced, O(n) worst case.

### Java Code

```java
class Solution {
    public Node connect(Node root) {
        if (root == null) return null;

        // Connect root's right child first
        if (root.right != null) {
            root.right.next = findNext(root.next);
        }

        // Connect root's left child
        if (root.left != null) {
            root.left.next = (root.right != null) ? root.right : findNext(root.next);
        }

        // Process right before left so next pointers are ready
        connect(root.right);
        connect(root.left);

        return root;
    }

    // Find the first child node reachable via next pointers from node
    private Node findNext(Node node) {
        while (node != null) {
            if (node.left != null)  return node.left;
            if (node.right != null) return node.right;
            node = node.next;
        }
        return null;
    }
}
```

---

## Comparison of Approaches

| Approach             | Time        | Space | Notes                               |
| -------------------- | ----------- | ----- | ----------------------------------- |
| BFS with Queue       | O(n)        | O(n)  | Simplest to understand              |
| O(1) Space Iterative | O(n)        | O(1)  | Optimal; recommended for interviews |
| Recursive DFS        | O(n²) worst | O(h)  | Elegant but not space-optimal       |

---

## Key Takeaway

The **O(1) space iterative approach** (Approach 2) is the ideal solution for this problem. The insight is that once you've linked level `k`, you can use those links as a free traversal mechanism to build level `k+1` — turning the previous level's `next` pointers into a pseudo-queue without any extra data structure.
