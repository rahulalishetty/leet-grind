# 431. Encode N-ary Tree to Binary Tree

`Hard` | `Tree` | `Depth-First Search` | `Breadth-First Search` | `Design`

---

## Problem Summary

Design a **stateless** encode/decode algorithm to convert an N-ary tree into a binary tree and back, preserving structure exactly.

---

## Core Insight: The Canonical Encoding Trick

The key idea used in all approaches below is the same **left-child / right-sibling** representation:

```
For every node in the N-ary tree:
  - Its FIRST child  → becomes the LEFT  child in the binary tree
  - Its NEXT sibling → becomes the RIGHT child in the binary tree
```

**Example transformation:**

```
N-ary Tree:           Binary Tree:
     1                    1
   / | \                 /
  3  2  4               3
 / \                   / \
5   6                 5   6
                       \   \
                        ?   2
                             \
                              4
```

This mapping is lossless and reversible — the decode just mirrors it.

---

## Approach 1: Recursive DFS (Pre-order)

### Explanation

Encode and decode using simple recursive pre-order traversal.

**Encode:** For each N-ary node:

1. Create a binary node with the same value.
2. Recursively encode the **first child** → assign to `left`.
3. Recursively encode the **next sibling** → assign to `right`.

**Decode:** For each binary node:

1. Create an N-ary node with the same value.
2. The `left` child is the **first child** of this N-ary node — decode it.
3. Walk the decoded child's **right chain** — each right node is the next sibling, decode and add all to the parent's children list.

### Code

```java
// Node definition for N-ary tree
class Node {
    public int val;
    public List<Node> children;
    public Node(int val) {
        this.val = val;
        this.children = new ArrayList<>();
    }
}

// TreeNode definition for Binary tree
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}

public class Codec_Recursive {

    // ──────────────── ENCODE ────────────────
    public TreeNode encode(Node root) {
        if (root == null) return null;

        TreeNode binaryNode = new TreeNode(root.val);

        // First child goes to the LEFT
        if (!root.children.isEmpty()) {
            binaryNode.left = encode(root.children.get(0));
        }

        // Chain the remaining siblings via RIGHT pointers
        TreeNode curr = binaryNode.left;
        for (int i = 1; i < root.children.size(); i++) {
            curr.right = encode(root.children.get(i));
            curr = curr.right;
        }

        return binaryNode;
    }

    // ──────────────── DECODE ────────────────
    public Node decode(TreeNode root) {
        if (root == null) return null;

        Node naryNode = new Node(root.val);

        // Walk the left child's right-sibling chain
        TreeNode sibling = root.left;
        while (sibling != null) {
            naryNode.children.add(decode(sibling));
            sibling = sibling.right;
        }

        return naryNode;
    }
}
```

### Complexity

|           | Encode     | Decode     |
| --------- | ---------- | ---------- |
| **Time**  | O(n)       | O(n)       |
| **Space** | O(h) stack | O(h) stack |

where `n` = number of nodes, `h` = height of the tree (up to 1000).

---

## Approach 2: Iterative DFS (Explicit Stack)

### Explanation

Same left-child / right-sibling mapping, but using an **explicit stack** to avoid recursion depth issues on tall trees (height up to 1000).

Each stack frame stores: `(N-ary node, Binary node, child index)` — the child index tracks which sibling we're currently encoding.

**Encode (iterative):**

- Push the root pair.
- For each frame, encode the child at `childIndex` as the `left` of the current binary node (first child) or `right` of the previous sibling.

**Decode (iterative):**

- Push the root pair.
- Walk the `left` subtree of each binary node; each node in the right-sibling chain becomes a child.

### Code

```java
public class Codec_Iterative {

    // ──────────────── ENCODE ────────────────
    public TreeNode encode(Node root) {
        if (root == null) return null;

        TreeNode binaryRoot = new TreeNode(root.val);

        // Stack holds [naryNode, binaryNode]
        Deque<Object[]> stack = new ArrayDeque<>();
        stack.push(new Object[]{root, binaryRoot});

        while (!stack.isEmpty()) {
            Object[] frame = stack.pop();
            Node nNode    = (Node)     frame[0];
            TreeNode bNode = (TreeNode) frame[1];

            TreeNode prev = null;
            for (Node child : nNode.children) {
                TreeNode bChild = new TreeNode(child.val);
                if (prev == null) {
                    bNode.left = bChild;   // first child → left
                } else {
                    prev.right = bChild;   // subsequent → right sibling
                }
                prev = bChild;
                stack.push(new Object[]{child, bChild});
            }
        }

        return binaryRoot;
    }

    // ──────────────── DECODE ────────────────
    public Node decode(TreeNode root) {
        if (root == null) return null;

        Node naryRoot = new Node(root.val);

        Deque<Object[]> stack = new ArrayDeque<>();
        stack.push(new Object[]{root, naryRoot});

        while (!stack.isEmpty()) {
            Object[] frame   = stack.pop();
            TreeNode bNode   = (TreeNode) frame[0];
            Node     nNode   = (Node)     frame[1];

            // Walk the right-sibling chain of bNode.left
            TreeNode sibling = bNode.left;
            while (sibling != null) {
                Node nChild = new Node(sibling.val);
                nNode.children.add(nChild);
                stack.push(new Object[]{sibling, nChild});
                sibling = sibling.right;
            }
        }

        return naryRoot;
    }
}
```

### Complexity

|           | Encode     | Decode     |
| --------- | ---------- | ---------- |
| **Time**  | O(n)       | O(n)       |
| **Space** | O(n) stack | O(n) stack |

> Uses O(n) stack space in the worst case (wide tree), but avoids call-stack overflow for deep trees.

---

## Approach 3: BFS (Level Order)

### Explanation

Process nodes **level by level** using a queue. This is more natural for tree serialization and closely mirrors the level-order format used in the problem statement.

**Encode:** Queue holds `(N-ary node, Binary node)` pairs. For each dequeued pair, encode all children and enqueue them.

**Decode:** Queue holds `(Binary node, N-ary node)` pairs. For each dequeued pair, walk the left → right sibling chain to rebuild the children list.

### Code

```java
public class Codec_BFS {

    // ──────────────── ENCODE ────────────────
    public TreeNode encode(Node root) {
        if (root == null) return null;

        TreeNode binaryRoot = new TreeNode(root.val);
        Queue<Object[]> queue = new LinkedList<>();
        queue.offer(new Object[]{root, binaryRoot});

        while (!queue.isEmpty()) {
            Object[] pair  = queue.poll();
            Node     nNode = (Node)     pair[0];
            TreeNode bNode = (TreeNode) pair[1];

            TreeNode prev = null;
            for (Node child : nNode.children) {
                TreeNode bChild = new TreeNode(child.val);
                if (prev == null) {
                    bNode.left = bChild;
                } else {
                    prev.right = bChild;
                }
                prev = bChild;
                queue.offer(new Object[]{child, bChild});
            }
        }

        return binaryRoot;
    }

    // ──────────────── DECODE ────────────────
    public Node decode(TreeNode root) {
        if (root == null) return null;

        Node naryRoot = new Node(root.val);
        Queue<Object[]> queue = new LinkedList<>();
        queue.offer(new Object[]{root, naryRoot});

        while (!queue.isEmpty()) {
            Object[] pair  = queue.poll();
            TreeNode bNode = (TreeNode) pair[0];
            Node     nNode = (Node)     pair[1];

            TreeNode sibling = bNode.left;
            while (sibling != null) {
                Node nChild = new Node(sibling.val);
                nNode.children.add(nChild);
                queue.offer(new Object[]{sibling, nChild});
                sibling = sibling.right;
            }
        }

        return naryRoot;
    }
}
```

### Complexity

|           | Encode     | Decode     |
| --------- | ---------- | ---------- |
| **Time**  | O(n)       | O(n)       |
| **Space** | O(w) queue | O(w) queue |

where `w` = maximum width of the tree. For a balanced tree, O(n/2) ≈ O(n). BFS is memory-efficient for **deep, narrow** trees.

---

## Approach Comparison

| Approach          | Time | Space      | Best For                                  | Risk                                                      |
| ----------------- | ---- | ---------- | ----------------------------------------- | --------------------------------------------------------- |
| **Recursive DFS** | O(n) | O(h)       | Clean, simple code                        | Stack overflow if h ≈ 1000 and recursive frames are large |
| **Iterative DFS** | O(n) | O(n) worst | Avoids call-stack overflow                | Slightly more verbose                                     |
| **BFS**           | O(n) | O(w)       | Wide/shallow trees; level-order intuition | More memory for very wide trees                           |

> For this problem's constraints (height ≤ 1000, nodes ≤ 10⁴), all three approaches are safe. Recursive DFS is cleanest; Iterative DFS is safest for production.

---

## Dry Run — Example 1

**Input N-ary:** `[1, null, 3, 2, 4, null, 5, 6]`

```
N-ary:               After Encode (Binary):
     1                       1
   / | \                    /
  3  2  4                  3
 / \                      / \
5   6                    5   2
                          \   \
                           6   4
```

**Decode trace:**

- Binary node `1`: left=`3` → child 3
- Walk right chain of `3`: right=`2` → child 2, right=`4` → child 4
- Binary node `3`: left=`5` → child 5; walk right of `5`: right=`6` → child 6
- Result: original N-ary tree ✓

---

## Key Takeaways

1. The **left-child / right-sibling** trick is a classic CS technique for converting arbitrary trees to binary trees.
2. All three implementations are **stateless** — no class-level variables, satisfying the constraint.
3. The encode/decode are exact inverses — no information is lost.
4. Every node is visited exactly once in both encode and decode → **O(n) time** is optimal.
