# Balance a Binary Search Tree — Approaches

## Overview

We need to **balance a Binary Search Tree (BST)** such that:

```
| height(left subtree) - height(right subtree) | ≤ 1
```

for **every node** in the tree.

### Reminder: BST Property

A Binary Search Tree satisfies:

```
left subtree values  <  node value  <  right subtree values
```

Balanced BSTs are desirable because they keep the **tree height logarithmic**, allowing operations such as:

- Search
- Insert
- Delete

to run in:

```
O(log n)
```

instead of degrading to:

```
O(n)
```

in skewed trees.

---

# Two Main Approaches

1. **Rebuild the tree from sorted values**
   - Traverse BST → get sorted array
   - Reconstruct balanced BST

2. **In-place balancing (Day-Stout-Warren algorithm)**
   - Restructure tree using rotations
   - Does not require additional storage

The first approach is **much simpler** and commonly used in interviews.

---

# Approach 1: Inorder Traversal + Recursive Construction

## Intuition

A key property of BST:

```
Inorder traversal → sorted order
```

So we can:

1. Traverse the tree using **inorder traversal**
2. Store values in a sorted list
3. Rebuild the tree using the **middle element as root**

Choosing the middle element guarantees the tree remains balanced.

Example:

```
Sorted array: [1,2,3,4,5]

Choose middle → 3

        3
      /   \
     1     4
      \     \
       2     5
```

The same logic applies recursively to left and right halves.

---

## Algorithm

### Step 1: Store nodes using inorder traversal

```
left → root → right
```

This produces a sorted list.

### Step 2: Build balanced BST

Define recursive function:

```
createBalancedBST(list, start, end)
```

Steps:

1. If start > end → return null
2. Pick middle index
3. Create node with middle value
4. Recursively build:
   - left subtree from left half
   - right subtree from right half

---

## Implementation

```java
class Solution {

    public TreeNode balanceBST(TreeNode root) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        return createBalancedBST(inorder, 0, inorder.size() - 1);
    }

    private void inorderTraversal(TreeNode root, List<Integer> inorder) {
        if (root == null) return;

        inorderTraversal(root.left, inorder);
        inorder.add(root.val);
        inorderTraversal(root.right, inorder);
    }

    private TreeNode createBalancedBST(List<Integer> inorder, int start, int end) {

        if (start > end) return null;

        int mid = start + (end - start) / 2;

        TreeNode leftSubtree = createBalancedBST(inorder, start, mid - 1);
        TreeNode rightSubtree = createBalancedBST(inorder, mid + 1, end);

        return new TreeNode(inorder.get(mid), leftSubtree, rightSubtree);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

Explanation:

- Inorder traversal → O(n)
- Building balanced BST → O(n)

Total:

```
O(n)
```

---

### Space Complexity

```
O(n)
```

Reasons:

- Inorder array storage
- Recursion stack

Worst case recursion depth:

```
O(n)
```

(for skewed trees)

---

# Approach 2: Day-Stout-Warren (DSW) Algorithm — In-Place Balancing

⚠️ **Advanced algorithm (rarely required in interviews).**

This algorithm balances a BST **without extra storage** by performing rotations.

---

## Tree Rotations

Two operations restructure the tree:

### Right Rotation

Transforms:

```
    A
   /
  B
```

into:

```
   B
    \
     A
```

---

### Left Rotation

Transforms:

```
A
 \
  B
```

into:

```
   B
  /
 A
```

These rotations preserve the BST property.

---

# DSW Algorithm Phases

The algorithm consists of **three phases**.

---

## Phase 1: Create the Backbone (Vine)

Convert BST into a **right-skewed linked list**.

```
1
 \
  2
   \
    3
     \
      4
```

This is done using **right rotations** whenever a node has a left child.

---

## Phase 2: Count Nodes

Traverse the vine and count nodes:

```
n = number of nodes
```

---

## Phase 3: Balance the Vine

Convert the vine into a balanced BST.

Steps:

1. Compute:

```
m = largest power of 2 less than (n + 1) minus 1
```

2. Perform:

```
n - m
```

left rotations.

3. Repeatedly:

```
m = m / 2
```

and perform **m rotations** until fully balanced.

---

## Implementation

```java
class Solution {

    public TreeNode balanceBST(TreeNode root) {
        if (root == null) return null;

        TreeNode vineHead = new TreeNode(0);
        vineHead.right = root;

        TreeNode current = vineHead;

        while (current.right != null) {
            if (current.right.left != null) {
                rightRotate(current, current.right);
            } else {
                current = current.right;
            }
        }

        int nodeCount = 0;
        current = vineHead.right;

        while (current != null) {
            nodeCount++;
            current = current.right;
        }

        int m = (int)Math.pow(
            2,
            Math.floor(Math.log(nodeCount + 1) / Math.log(2))
        ) - 1;

        makeRotations(vineHead, nodeCount - m);

        while (m > 1) {
            m /= 2;
            makeRotations(vineHead, m);
        }

        return vineHead.right;
    }

    private void rightRotate(TreeNode parent, TreeNode node) {
        TreeNode tmp = node.left;
        node.left = tmp.right;
        tmp.right = node;
        parent.right = tmp;
    }

    private void leftRotate(TreeNode parent, TreeNode node) {
        TreeNode tmp = node.right;
        node.right = tmp.left;
        tmp.left = node;
        parent.right = tmp;
    }

    private void makeRotations(TreeNode vineHead, int count) {
        TreeNode current = vineHead;

        for (int i = 0; i < count; i++) {
            TreeNode tmp = current.right;
            leftRotate(current, tmp);
            current = current.right;
        }
    }
}
```

---

# Complexity Analysis

### Time Complexity

```
O(n)
```

Reason:

- Backbone creation → O(n)
- Node counting → O(n)
- Rotations → O(n)

---

### Space Complexity

```
O(1) auxiliary space
```

Only a few pointers are used.

However recursion is not used, so stack overhead is minimal.

---

# Summary

| Approach          | Idea                                 | Time | Space | Difficulty |
| ----------------- | ------------------------------------ | ---- | ----- | ---------- |
| Inorder + rebuild | Convert to sorted array then rebuild | O(n) | O(n)  | Easy       |
| Day‑Stout‑Warren  | Rotate tree in-place                 | O(n) | O(1)  | Advanced   |

In interviews, **Approach 1 is almost always expected**.
