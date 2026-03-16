# 669. Trim a Binary Search Tree

## Approach 1: Recursion

## Intuition

Let:

```text
trim(node)
```

represent the root of the trimmed subtree for the subtree originally rooted at `node`.

Our goal is to build this answer recursively.

Because the input tree is a **Binary Search Tree (BST)**, we can take advantage of its ordering property:

- all nodes in the left subtree are smaller than the current node
- all nodes in the right subtree are larger than the current node

This gives us a very strong pruning rule.

---

# Key BST Observation

For a node with value `node.val`:

## Case 1: `node.val > high`

If the current node value is greater than `high`, then:

- the current node is invalid
- everything in its **right subtree** is even larger
- so the entire right subtree must also be invalid

Therefore, the only possible valid nodes must lie in the **left subtree**.

So:

```text
trim(node) = trim(node.left)
```

---

## Case 2: `node.val < low`

If the current node value is smaller than `low`, then:

- the current node is invalid
- everything in its **left subtree** is even smaller
- so the entire left subtree must also be invalid

Therefore, the only possible valid nodes must lie in the **right subtree**.

So:

```text
trim(node) = trim(node.right)
```

---

## Case 3: `low <= node.val <= high`

If the current node lies within the valid range, then:

- this node should remain in the tree
- but its left and right subtrees may still contain invalid nodes

So we recursively trim both children:

```text
node.left = trim(node.left)
node.right = trim(node.right)
```

Then return the current node.

---

# Why Recursion Fits Naturally

This problem is recursive by structure.

For every node, the trimmed result depends only on the trimmed results of its children.

So recursion is a very natural way to express the solution.

At each step, we ask:

> What should the root of the trimmed subtree for this node be?

The answer is:

- the trimmed left subtree
- or the trimmed right subtree
- or the current node with trimmed children

depending on the node value.

---

# Algorithm

1. If the current node is `null`, return `null`.
2. If `node.val > high`, recursively trim the left subtree and return it.
3. If `node.val < low`, recursively trim the right subtree and return it.
4. Otherwise:
   - recursively trim the left subtree
   - recursively trim the right subtree
   - attach them back to the current node
   - return the current node

---

# Java Code

```java
class Solution {
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null) return root;
        if (root.val > high) return trimBST(root.left, low, high);
        if (root.val < low) return trimBST(root.right, low, high);

        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);
        return root;
    }
}
```

---

# Detailed Walkthrough of the Code

## Base Case

```java
if (root == null) return root;
```

If the subtree is empty, there is nothing to trim.

So we simply return `null`.

---

## When the Current Node Is Too Large

```java
if (root.val > high) return trimBST(root.left, low, high);
```

Since `root.val` is already too large, and all nodes in the right subtree are even larger, neither the root nor its right subtree can remain.

So the only candidate subtree is the trimmed version of `root.left`.

---

## When the Current Node Is Too Small

```java
if (root.val < low) return trimBST(root.right, low, high);
```

Since `root.val` is too small, and all nodes in the left subtree are even smaller, neither the root nor its left subtree can remain.

So the only candidate subtree is the trimmed version of `root.right`.

---

## When the Current Node Is Valid

```java
root.left = trimBST(root.left, low, high);
root.right = trimBST(root.right, low, high);
return root;
```

Now the root lies inside the allowed range, so we keep it.

But we still need to recursively trim both child subtrees.

After trimming:

- reattach the trimmed left subtree
- reattach the trimmed right subtree
- return the current node

---

# Example Walkthrough

## Example

```text
root = [3,0,4,null,2,null,null,1]
low = 1
high = 3
```

Tree:

```text
        3
       / \\
      0   4
       \\
        2
       /
      1
```

---

## Step 1: Start at node 3

- `3` is within `[1, 3]`
- keep node `3`
- trim both subtrees

---

## Step 2: Trim left subtree rooted at 0

- `0 < 1`
- node `0` is too small
- discard node `0` and its left subtree
- keep only `trim(0.right)`

That means move to subtree rooted at `2`

---

## Step 3: Trim subtree rooted at 2

- `2` is valid
- keep node `2`
- trim left and right

Its left child is `1`, which is valid.
Its right child is `null`.

So subtree rooted at `2` remains:

```text
    2
   /
  1
```

---

## Step 4: Trim right subtree rooted at 4

- `4 > 3`
- node `4` is too large
- discard node `4` and its right subtree
- keep only `trim(4.left)`

But `4.left` is `null`, so result is `null`

---

## Final Result

The tree becomes:

```text
      3
     /
    2
   /
  1
```

Which matches the expected output.

---

# Why the Relative Structure Is Preserved

The problem says trimming should not change the relative structure of the remaining elements.

This recursive approach preserves that automatically.

Why?

Because we never rearrange valid nodes arbitrarily.

We only do one of these things:

- discard a node and replace it with one of its already-existing trimmed subtrees
- keep a node and recursively trim its children

So descendants remain descendants, and the BST structure stays valid.

---

# Correctness Intuition

This solution is correct because of the BST property.

At any node:

- if the node is too large, everything to its right is also too large
- if the node is too small, everything to its left is also too small
- if the node is valid, only its descendants may need trimming

So each recursive decision removes only nodes that are guaranteed to be invalid and keeps all potentially valid structure intact.

That is exactly what the problem requires.

---

# Complexity Analysis

Let `N` be the number of nodes in the tree.

## Time Complexity

```text
O(N)
```

Why?

Each node is visited at most once.

For every node, we do only constant extra work besides recursive calls.

So total time is linear in the number of nodes.

---

## Space Complexity

```text
O(N)
```

in the worst case.

Even though we do not explicitly allocate extra data structures, recursion uses the call stack.

In the worst-case skewed tree:

- the height of the tree can be `N`
- so recursion depth can become `N`

More precisely, the space complexity is:

```text
O(H)
```

where `H` is the height of the tree

- worst case: `O(N)`
- balanced BST: `O(log N)`

---

# Strengths of This Approach

- very short and elegant
- fully uses the BST property
- linear time
- naturally preserves structure
- easy to reason about recursively

---

# Final Takeaway

This problem becomes simple once we use the BST ordering property.

The crucial pruning rules are:

- if `node.val > high`, discard the node and go left
- if `node.val < low`, discard the node and go right
- otherwise, keep the node and trim both sides

That leads to a clean recursive solution that is both efficient and easy to understand.

---

# Summary

## Main Idea

Use recursion and BST pruning.

For each node:

- if it is too large, return the trimmed left subtree
- if it is too small, return the trimmed right subtree
- otherwise, keep the node and recursively trim both children

## Java Code

```java
class Solution {
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null) return root;
        if (root.val > high) return trimBST(root.left, low, high);
        if (root.val < low) return trimBST(root.right, low, high);

        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);
        return root;
    }
}
```

## Complexity

- **Time:** `O(N)`
- **Space:** `O(N)` worst case due to recursion stack
