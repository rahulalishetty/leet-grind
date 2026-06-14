# 156. Binary Tree Upside Down — Java Solutions

## Problem Recap

Given the root of a binary tree, flip it **upside down** by applying these rules at every level:

| Original Role | New Role        |
| ------------- | --------------- |
| Left child    | New root        |
| Original root | New right child |
| Right child   | New left child  |

**Key constraints:**

- Every right node has a left sibling (same parent).
- Every right node has **no children**.

This means the tree is essentially a **left-skewed spine** with optional right siblings — not a general binary tree.

### Visual Walkthrough

```
Input:
        1
       / \
      2   3
     / \
    4   5

Step 1: Process node 4 (leftmost)
  4 becomes new root
  4.left = 5  (original right sibling of 2)
  4.right = 2 (original parent)

Step 2: Process node 2
  2 becomes new root under 4
  2.left = 3  (original right sibling of 1)
  2.right = 1 (original parent)

Output:
    4
   / \
  5   2
     / \
    3   1
```

---

## TreeNode Definition

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```

---

## Approach 1: Iterative (Bottom-Up)

### Intuition

Walk down the **left spine** of the tree (since right nodes never have children, the
interesting chain is always left → left → left...). At each step, save the current node's
left and right children before overwriting them.

- `curr` — the node we are currently processing
- `prevParent` — the node that was `curr`'s parent (will become `curr`'s new right child)
- `prevSibling` — the node that was `curr`'s right child (will become `curr`'s new left child)

After the loop, `curr` is `null` and `prevParent` is the new root.

### Code

```java
class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        TreeNode curr        = root;
        TreeNode prevParent  = null;   // becomes the new right child
        TreeNode prevSibling = null;   // becomes the new left child

        while (curr != null) {
            TreeNode nextLeft  = curr.left;   // save before overwrite
            TreeNode nextRight = curr.right;  // save before overwrite

            curr.left  = prevSibling;   // original right sibling → new left
            curr.right = prevParent;    // original parent        → new right

            prevParent  = curr;
            prevSibling = nextRight;
            curr        = nextLeft;     // move down the left spine
        }

        return prevParent;  // last non-null node processed = new root
    }
}
```

### Dry Run on `[1, 2, 3, 4, 5]`

| Iteration | curr | prevParent | prevSibling | curr.left | curr.right |
| --------- | ---- | ---------- | ----------- | --------- | ---------- |
| 1         | 1    | null       | null        | null      | null       |
| 2         | 2    | 1          | 3           | null      | 1          |
| 3         | 4    | 2          | 5           | 5         | 2          |
| exit      | null | 4          | —           | —         | —          |

Return `prevParent` = node **4** ✓

### Complexity

|           |                                     |
| --------- | ----------------------------------- |
| **Time**  | O(h) — h = height of the left spine |
| **Space** | O(1) — only a handful of pointers   |

---

## Approach 2: Recursive (DFS Post-Order)

### Intuition

Recurse all the way to the **bottom-left leaf** first; that leaf is the new root.
On the way back up, rewire each node: make the current root the right child of its
left child, and make the current right sibling the left child of its left child.

```
upsideDown(1)
  └─ upsideDown(2)
       └─ upsideDown(4)   ← base case, returns 4 (new root)
            rewire: 4.left = 5, 4.right = 2
       rewire: 2.left = 3, 2.right = 1
  returns 4
```

### Code

```java
class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        // Base case: leaf node or empty tree — this is already the new root
        if (root == null || root.left == null) {
            return root;
        }

        // Recurse down the left spine
        TreeNode newRoot = upsideDownBinaryTree(root.left);

        // Rewire: root.left becomes the new sub-root
        //   root.left.left  = root.right  (sibling becomes left child)
        //   root.left.right = root         (parent becomes right child)
        root.left.left  = root.right;
        root.left.right = root;

        // Detach root from its old children to avoid cycles
        root.left  = null;
        root.right = null;

        return newRoot;
    }
}
```

### Call Stack for `[1, 2, 3, 4, 5]`

```
upsideDown(1)
  calls upsideDown(2)
    calls upsideDown(4)
      4.left == null → return 4  (newRoot = 4)
    back in upsideDown(2):
      2.left.left  = 2.right → 4.left = 5
      2.left.right = 2       → 4.right = 2
      2.left = null, 2.right = null
      return 4
  back in upsideDown(1):
    1.left.left  = 1.right → 2.left = 3
    1.left.right = 1       → 2.right = 1
    1.left = null, 1.right = null
    return 4
```

Result: `4 → {left:5, right:2 → {left:3, right:1}}` ✓

### Complexity

|           |                                                           |
| --------- | --------------------------------------------------------- |
| **Time**  | O(h)                                                      |
| **Space** | O(h) — recursion stack depth equals the left-spine height |

---

## Approach 3: Stack-Based Explicit Simulation

### Intuition

If recursion overhead is a concern but you still want a clean separation of
"collect nodes" and "rewire", push all left-spine nodes onto a stack first,
then pop them in reverse order (bottom-up) and do the same pointer rewiring as
the iterative approach — but more explicitly.

### Code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        // Phase 1: push the entire left spine (with right children) onto a stack
        Deque<TreeNode[]> stack = new ArrayDeque<>();
        TreeNode curr = root;
        while (curr != null) {
            stack.push(new TreeNode[]{curr, curr.right});  // [node, its right sibling]
            curr = curr.left;
        }

        // Phase 2: pop bottom-up and rewire
        TreeNode newRoot = null;
        TreeNode parent  = null;

        while (!stack.isEmpty()) {
            TreeNode[] pair    = stack.pop();
            TreeNode   node    = pair[0];
            TreeNode   sibling = pair[1];

            if (newRoot == null) {
                newRoot = node;          // first popped = bottom leaf = new root
            }

            node.left  = sibling;        // right sibling → new left child
            node.right = parent;         // old parent    → new right child

            parent = node;
        }

        return newRoot;
    }
}
```

### Trace for `[1, 2, 3, 4, 5]`

```
Phase 1 — stack (top first):
  [4, null]
  [2, 3]
  [1, null]    ← bottom of stack (pushed first)

Phase 2 — pop order:
  pop [4, null]: newRoot=4, 4.left=null,  4.right=null  (parent was null)
  pop [2, 3]:              2.left=3,     2.right=4
  pop [1, null]:           1.left=null,  1.right=2

Wait — top of stack after Phase 1 is [4, null] (pushed last).
```

> **Note:** `ArrayDeque.push()` = stack push (LIFO). The last node pushed
> is node `4` (leftmost leaf), so it is the first popped → correct bottom-up order.

### Complexity

|           |                                          |
| --------- | ---------------------------------------- |
| **Time**  | O(h)                                     |
| **Space** | O(h) — stack stores all left-spine nodes |

---

## Approach 4: Array Snapshot + Rewire

### Intuition

Collect all left-spine nodes into a plain array for clarity, then iterate the
array from right (bottom) to left (top) rewiring. Very readable — good for
interviews when you want to show the structure explicitly.

### Code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        if (root == null) return null;

        // Collect left-spine nodes
        List<TreeNode> spine = new ArrayList<>();
        TreeNode curr = root;
        while (curr != null) {
            spine.add(curr);
            curr = curr.left;
        }

        // New root is the last (deepest) node
        TreeNode newRoot = spine.get(spine.size() - 1);

        // Rewire from bottom upward
        for (int i = spine.size() - 1; i > 0; i--) {
            TreeNode node   = spine.get(i);          // current (lower node)
            TreeNode parent = spine.get(i - 1);      // original parent (one above)

            node.left  = parent.right;   // right sibling → new left child
            node.right = parent;         // parent        → new right child

            parent.left  = null;         // clean up to avoid cycles
            parent.right = null;
        }

        return newRoot;
    }
}
```

### Complexity

|           |                                  |
| --------- | -------------------------------- |
| **Time**  | O(h)                             |
| **Space** | O(h) — array of left-spine nodes |

---

## Approach Comparison

| Approach           | Time | Space    | Style                     | Best For                    |
| ------------------ | ---- | -------- | ------------------------- | --------------------------- |
| **Iterative**      | O(h) | **O(1)** | In-place pointer juggling | Production / optimal memory |
| **Recursive**      | O(h) | O(h)     | Clean & concise           | Interview readability       |
| **Stack**          | O(h) | O(h)     | Explicit phase separation | Teaching two-pass logic     |
| **Array Snapshot** | O(h) | O(h)     | Most readable             | Clarity / debugging         |

> All approaches run in O(h) time where h = height of the left spine
> (= total nodes, since this is always a left-skewed tree by constraint).

---

## Edge Cases

```java
// Empty tree
upsideDownBinaryTree(null)  // → null ✓

// Single node
upsideDownBinaryTree(new TreeNode(1))  // → TreeNode(1) ✓

// Only left spine, no right siblings
//   1
//    \   ← none
//   2
// Result: 2.left=null, 2.right=1
```

---

## Quick Test Harness

```java
public class Main {
    static TreeNode build(Integer[] vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode[] nodes = new TreeNode[vals.length];
        for (int i = 0; i < vals.length; i++)
            if (vals[i] != null) nodes[i] = new TreeNode(vals[i]);
        for (int i = 0; i < vals.length; i++) {
            if (nodes[i] == null) continue;
            if (2*i+1 < vals.length) nodes[i].left  = nodes[2*i+1];
            if (2*i+2 < vals.length) nodes[i].right = nodes[2*i+2];
        }
        return nodes[0];
    }

    static String bfs(TreeNode root) {
        if (root == null) return "[]";
        java.util.Queue<TreeNode> q = new java.util.LinkedList<>();
        q.add(root);
        java.util.List<String> res = new java.util.ArrayList<>();
        while (!q.isEmpty()) {
            TreeNode n = q.poll();
            if (n == null) { res.add("null"); continue; }
            res.add(String.valueOf(n.val));
            q.add(n.left);
            q.add(n.right);
        }
        // trim trailing nulls
        int last = res.size() - 1;
        while (last >= 0 && res.get(last).equals("null")) last--;
        return res.subList(0, last + 1).toString();
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println(bfs(sol.upsideDownBinaryTree(build(new Integer[]{1,2,3,4,5}))));
        // Expected: [4, 5, 2, null, null, 3, 1]

        System.out.println(bfs(sol.upsideDownBinaryTree(build(new Integer[]{}))));
        // Expected: []

        System.out.println(bfs(sol.upsideDownBinaryTree(build(new Integer[]{1}))));
        // Expected: [1]
    }
}
```
