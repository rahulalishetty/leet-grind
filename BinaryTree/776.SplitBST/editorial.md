# Split BST – Detailed Approaches

## Overview

We are given a **Binary Search Tree (BST)** and a **target value**.
The goal is to split the tree into two BSTs:

- Tree 1 → Nodes with values **≤ target**
- Tree 2 → Nodes with values **> target**

The BST structure should be preserved whenever possible.

Key BST Property:

```
Left subtree values  <  node value
Right subtree values >  node value
```

This property helps determine where nodes belong after splitting.

For every recursive call, we return **two trees**:

```
result[0] → subtree with values ≤ target
result[1] → subtree with values > target
```

---

# Approach 1 — Recursive DFS

## Intuition

Using recursion, we process the tree starting from the root.

For each node:

### Case 1 — Node value ≤ target

- Node belongs to the **left split tree**
- Its **left subtree also belongs there**
- We recursively split the **right subtree**

### Case 2 — Node value > target

- Node belongs to the **right split tree**
- Its **right subtree also belongs there**
- We recursively split the **left subtree**

### Base Case

```
if root == null → return [null, null]
```

---

## Algorithm

1. If root is `null`, return `[null, null]`.

2. If `root.val > target`:

```
leftSplit = splitBST(root.left, target)

root.left = leftSplit[1]

return [leftSplit[0], root]
```

3. Otherwise:

```
rightSplit = splitBST(root.right, target)

root.right = rightSplit[0]

return [root, rightSplit[1]]
```

---

## Implementation

```java
class Solution {

    public TreeNode[] splitBST(TreeNode root, int target) {

        if (root == null) {
            return new TreeNode[2];
        }

        if (root.val > target) {

            TreeNode[] left = splitBST(root.left, target);

            root.left = left[1];

            return new TreeNode[]{left[0], root};

        } else {

            TreeNode[] right = splitBST(root.right, target);

            root.right = right[0];

            return new TreeNode[]{root, right[1]};
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(h)
```

`h` = height of the tree.

We only traverse along a single root-to-leaf path.

### Space Complexity

```
O(h)
```

Due to recursion stack.

---

# Approach 2 — Iterative Traversal (Stack)

## Intuition

Every recursive solution can be transformed into an **iterative version using a stack**.

We traverse the tree following BST rules:

```
if node.val > target → move left
else → move right
```

Nodes encountered are pushed into a stack.

After traversal:

- Nodes ≤ target appear **toward bottom**
- Nodes > target appear **toward top**

We then rebuild two BSTs by popping nodes.

---

## Algorithm

1. Create result array `ans[2]`.

2. Traverse tree while pushing nodes to stack.

3. Pop nodes one-by-one.

4. If node value > target:

```
node.left = ans[1]
ans[1] = node
```

5. Otherwise:

```
node.right = ans[0]
ans[0] = node
```

---

## Implementation

```java
class Solution {

    public TreeNode[] splitBST(TreeNode root, int target) {

        TreeNode[] ans = new TreeNode[2];

        if (root == null) {
            return ans;
        }

        Stack<TreeNode> stack = new Stack<>();

        while (root != null) {
            stack.push(root);

            if (root.val > target) {
                root = root.left;
            } else {
                root = root.right;
            }
        }

        while (!stack.isEmpty()) {

            TreeNode curr = stack.pop();

            if (curr.val > target) {

                curr.left = ans[1];
                ans[1] = curr;

            } else {

                curr.right = ans[0];
                ans[0] = curr;
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(h)
```

Only a path of height `h` is processed.

### Space Complexity

```
O(h)
```

Stack stores nodes along path.

---

# Approach 3 — Iterative with Dummy Heads

## Intuition

We can eliminate the stack entirely.

Instead, we create **two dummy nodes** that act as the heads of the resulting trees.

```
dummySm → tree with nodes ≤ target
dummyLg → tree with nodes > target
```

We maintain two pointers:

```
curSm → last node in ≤ target tree
curLg → last node in > target tree
```

We traverse along the **split boundary** and attach nodes directly.

---

## Algorithm

1. Create dummy nodes:

```
dummySm, dummyLg
```

2. Initialize pointers:

```
curSm = dummySm
curLg = dummyLg
```

3. Traverse tree:

### If node ≤ target

```
curSm.right = node
curSm = node

next = node.right
node.right = null

node = next
```

### If node > target

```
curLg.left = node
curLg = node

next = node.left
node.left = null

node = next
```

4. Return:

```
[dummySm.right, dummyLg.left]
```

---

## Implementation

```java
class Solution {

    public static TreeNode[] splitBST(TreeNode root, int target) {

        TreeNode dummySm = new TreeNode(0);
        TreeNode curSm = dummySm;

        TreeNode dummyLg = new TreeNode(0);
        TreeNode curLg = dummyLg;

        TreeNode current = root;
        TreeNode nextNode;

        while (current != null) {

            if (current.val <= target) {

                curSm.right = current;
                curSm = current;

                nextNode = current.right;
                curSm.right = null;

                current = nextNode;

            } else {

                curLg.left = current;
                curLg = current;

                nextNode = current.left;
                curLg.left = null;

                current = nextNode;
            }
        }

        return new TreeNode[]{dummySm.right, dummyLg.left};
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

All nodes are visited once.

### Space Complexity

```
O(1)
```

Only constant extra pointers are used.
