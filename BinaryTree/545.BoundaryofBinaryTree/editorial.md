# Boundary of Binary Tree — Approaches

## Approach 1: Simple Solution

### Algorithm

This approach divides the problem into **three subproblems**:

1. Left Boundary
2. Leaves
3. Right Boundary

---

### Left Boundary

Traverse the tree towards the **left side** and keep adding nodes to the result list **as long as they are not leaf nodes**.

Rules:

- Move to the **left child** whenever possible.
- If the left child does not exist but the **right child exists**, move to the right child.
- **Leaf nodes are excluded** from the left boundary.

---

### Leaf Nodes

Use a recursive function:

```
addLeaves(res, root)
```

Logic:

1. If the current node is a **leaf**, add it to the result.
2. Otherwise recursively traverse:
   - left subtree
   - right subtree

---

### Right Boundary

This process is similar to the **left boundary**, but traversal happens toward the **right side**.

Steps:

- Traverse rightwards.
- If the right child does not exist, move to the left child.
- Push nodes onto a **stack** instead of directly inserting into the result.
- After traversal, **pop elements from the stack** and append them to the result to maintain reverse order.

---

### Implementation

```java
public class Solution {

    public boolean isLeaf(TreeNode t) {
        return t.left == null && t.right == null;
    }

    public void addLeaves(List<Integer> res, TreeNode root) {
        if (isLeaf(root)) {
            res.add(root.val);
        } else {
            if (root.left != null) {
                addLeaves(res, root.left);
            }
            if (root.right != null) {
                addLeaves(res, root.right);
            }
        }
    }

    public List<Integer> boundaryOfBinaryTree(TreeNode root) {
        ArrayList<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }

        if (!isLeaf(root)) {
            res.add(root.val);
        }

        TreeNode t = root.left;

        while (t != null) {
            if (!isLeaf(t)) {
                res.add(t.val);
            }

            if (t.left != null) {
                t = t.left;
            } else {
                t = t.right;
            }
        }

        addLeaves(res, root);

        Stack<Integer> s = new Stack<>();
        t = root.right;

        while (t != null) {
            if (!isLeaf(t)) {
                s.push(t.val);
            }

            if (t.right != null) {
                t = t.right;
            } else {
                t = t.left;
            }
        }

        while (!s.empty()) {
            res.add(s.pop());
        }

        return res;
    }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(n)
```

- One traversal for leaves
- One traversal for left boundary
- One traversal for right boundary

**Space Complexity**

```
O(n)
```

- Result list
- Stack used for right boundary

---

# Approach 2: Using Preorder Traversal

### Intuition

The traversal order resembles **preorder traversal**:

```
Root → Left → Right
```

However, we selectively include nodes based on their **position in the tree**.

Nodes can belong to:

- Left Boundary
- Right Boundary
- Leaves
- Middle nodes (ignored)

---

### Node Classification Using Flags

We assign flags to indicate the type of node.

| Flag | Meaning        |
| ---- | -------------- |
| 0    | Root Node      |
| 1    | Left Boundary  |
| 2    | Right Boundary |
| 3    | Middle Node    |

We maintain three lists:

- `left_boundary`
- `right_boundary`
- `leaves`

At the end we combine:

```
left_boundary + leaves + right_boundary
```

---

### Determining Child Flags

To determine how children behave, we use two helper functions:

```
leftChildFlag()
rightChildFlag()
```

These determine whether a child behaves like:

- left boundary
- right boundary
- middle node

based on the **parent node's role**.

---

### Implementation

```java
public class Solution {

    public List<Integer> boundaryOfBinaryTree(TreeNode root) {

        List<Integer> left_boundary = new LinkedList<>();
        List<Integer> right_boundary = new LinkedList<>();
        List<Integer> leaves = new LinkedList<>();

        preorder(root, left_boundary, right_boundary, leaves, 0);

        left_boundary.addAll(leaves);
        left_boundary.addAll(right_boundary);

        return left_boundary;
    }

    public boolean isLeaf(TreeNode cur) {
        return (cur.left == null && cur.right == null);
    }

    public boolean isRightBoundary(int flag) {
        return (flag == 2);
    }

    public boolean isLeftBoundary(int flag) {
        return (flag == 1);
    }

    public boolean isRoot(int flag) {
        return (flag == 0);
    }

    public int leftChildFlag(TreeNode cur, int flag) {

        if (isLeftBoundary(flag) || isRoot(flag))
            return 1;

        else if (isRightBoundary(flag) && cur.right == null)
            return 2;

        else
            return 3;
    }

    public int rightChildFlag(TreeNode cur, int flag) {

        if (isRightBoundary(flag) || isRoot(flag))
            return 2;

        else if (isLeftBoundary(flag) && cur.left == null)
            return 1;

        else
            return 3;
    }

    public void preorder(TreeNode cur,
                         List<Integer> left_boundary,
                         List<Integer> right_boundary,
                         List<Integer> leaves,
                         int flag) {

        if (cur == null)
            return;

        if (isRightBoundary(flag))
            right_boundary.add(0, cur.val);

        else if (isLeftBoundary(flag) || isRoot(flag))
            left_boundary.add(cur.val);

        else if (isLeaf(cur))
            leaves.add(cur.val);

        preorder(cur.left, left_boundary, right_boundary, leaves, leftChildFlag(cur, flag));
        preorder(cur.right, left_boundary, right_boundary, leaves, rightChildFlag(cur, flag));
    }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(n)
```

One full traversal of the tree.

**Space Complexity**

```
O(n)
```

- Recursive stack depth
- Lists storing boundary nodes
