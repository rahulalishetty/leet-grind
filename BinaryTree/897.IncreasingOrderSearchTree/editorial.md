# 897. Increasing Order Search Tree

## Detailed Notes on In-order Traversal Approaches

## Overview

We are given the root of a **Binary Search Tree (BST)**.

We must rearrange the tree so that:

- the **leftmost node** becomes the new root
- every node has **no left child**
- every node has **exactly one right child** except the last one
- the final tree follows the nodes in **increasing order**

In other words, we want to transform the BST into a **right-skewed increasing-order chain**.

This document explains two approaches in detail:

1. **In-order Traversal with Value Collection**
2. **In-order Traversal with Relinking**

---

# Core BST Insight

The most important property of a BST is:

- all nodes in the left subtree are smaller than the current node
- all nodes in the right subtree are larger than the current node

Because of that, an **in-order traversal** of a BST:

```text
left -> node -> right
```

visits all node values in **ascending order**.

That means the desired final tree is simply the in-order sequence written as:

```text
smallest -> next -> next -> ... -> largest
```

using only right pointers.

So the core of the problem is:

> Traverse the BST in sorted order, then rebuild or relink the nodes into a right-only chain.

---

# Example Intuition

Suppose the BST is:

```text
        5
       / \\
      3   6
     / \\   \\
    2   4   8
   /       / \\
  1       7   9
```

Its in-order traversal is:

```text
1, 2, 3, 4, 5, 6, 7, 8, 9
```

So the final tree must become:

```text
1
 \\
  2
   \\
    3
     \\
      4
       \\
        5
         \\
          6
           \\
            7
             \\
              8
               \\
                9
```

That is exactly the sorted order of BST values.

---

# Approach 1: In-order Traversal with Value Collection

## Intuition

The simplest idea is:

1. perform an in-order traversal
2. collect all values into a list
3. build a brand new tree using those values in order

This approach does not modify the existing tree nodes directly. Instead, it creates new nodes.

Because the in-order traversal already gives values in increasing order, rebuilding the answer becomes straightforward.

---

## Why This Works

If the values are collected in sorted order, then creating a new chain of nodes where each node points to the next one through its `right` pointer automatically produces the required increasing-order search tree.

Since every new node has no left child and only one right child, the result satisfies the problem conditions.

---

## Algorithm

1. Create an empty list `vals`.
2. Perform an in-order traversal of the BST and append node values to `vals`.
3. Create a dummy node `ans`.
4. Use a pointer `cur` starting at the dummy node.
5. For each value `v` in `vals`:
   - create a new node with value `v`
   - attach it as `cur.right`
   - move `cur` forward
6. Return `ans.right` as the real root of the new tree.

---

## Java Code

```java
class Solution {
    public TreeNode increasingBST(TreeNode root) {
        List<Integer> vals = new ArrayList();
        inorder(root, vals);
        TreeNode ans = new TreeNode(0), cur = ans;
        for (int v: vals) {
            cur.right = new TreeNode(v);
            cur = cur.right;
        }
        return ans.right;
    }

    public void inorder(TreeNode node, List<Integer> vals) {
        if (node == null) return;
        inorder(node.left, vals);
        vals.add(node.val);
        inorder(node.right, vals);
    }
}
```

---

## Detailed Walkthrough

### 1. Collect Values in Sorted Order

```java
List<Integer> vals = new ArrayList();
inorder(root, vals);
```

This list will store the node values in ascending order.

The in-order traversal is:

```java
public void inorder(TreeNode node, List<Integer> vals) {
    if (node == null) return;
    inorder(node.left, vals);
    vals.add(node.val);
    inorder(node.right, vals);
}
```

Because the input is a BST, this gives a sorted sequence.

---

### 2. Create a Dummy Node

```java
TreeNode ans = new TreeNode(0), cur = ans;
```

The dummy node is just a helper.

It makes it easier to build the chain without handling the first-node case separately.

At the end, the actual answer starts at:

```java
ans.right
```

---

### 3. Build the Right-Skewed Tree

```java
for (int v: vals) {
    cur.right = new TreeNode(v);
    cur = cur.right;
}
```

For each value:

- create a new node
- attach it to the right of the current node
- move the pointer forward

This creates a chain in increasing order.

---

### 4. Return the Real Root

```java
return ans.right;
```

Since `ans` is only a dummy node, the actual new tree begins at `ans.right`.

---

## Example Walkthrough

Suppose the in-order traversal produces:

```text
[1, 2, 3, 4, 5]
```

Then the construction loop does:

- create node `1`, attach to dummy
- create node `2`, attach to `1.right`
- create node `3`, attach to `2.right`
- create node `4`, attach to `3.right`
- create node `5`, attach to `4.right`

Final tree:

```text
1 -> 2 -> 3 -> 4 -> 5
```

with all left pointers null.

---

## Complexity Analysis

Let `N` be the number of nodes in the tree.

### Time Complexity

```text
O(N)
```

Why?

- the in-order traversal visits each node once
- the reconstruction loop also visits each collected value once

So total time is linear.

---

### Space Complexity

```text
O(N)
```

Why?

- the list stores all `N` node values
- recursion stack also uses extra space, up to the height of the tree

So total auxiliary space is linear.

---

## Strengths and Weaknesses

### Strengths

- very simple
- easy to understand
- clean separation between traversal and construction

### Weaknesses

- allocates an entirely new tree
- stores all values in a list first
- does not reuse original nodes

That motivates the second approach.

---

# Approach 2: Traversal with Relinking

## Intuition

Instead of collecting values and creating new nodes, we can directly **reuse the existing tree nodes**.

The idea is still the same:

- perform an in-order traversal
- visit nodes in increasing order

But now, instead of storing their values in a list, we attach each visited node directly to the growing result tree.

To make the final structure correct:

- set `node.left = null`
- attach the current node to the `right` of the previously processed node

This way, we build the answer on the fly.

---

## Why This Works

In-order traversal already visits nodes in the exact order needed for the final tree.

So as soon as we visit a node:

- all smaller nodes have already been processed
- this node should come immediately after them in the new structure

Therefore, we can link it directly to the growing right-skewed chain.

---

## Algorithm

1. Create a dummy node `ans`.
2. Maintain a pointer `cur` pointing to the last node in the result chain.
3. Perform in-order traversal.
4. For each visited node:
   - recursively process left subtree
   - cut `node.left = null`
   - set `cur.right = node`
   - move `cur = node`
   - recursively process right subtree
5. Return `ans.right`.

---

## Java Code

```java
class Solution {
    TreeNode cur;
    public TreeNode increasingBST(TreeNode root) {
        TreeNode ans = new TreeNode(0);
        cur = ans;
        inorder(root);
        return ans.right;
    }

    public void inorder(TreeNode node) {
        if (node == null) return;
        inorder(node.left);
        node.left = null;
        cur.right = node;
        cur = node;
        inorder(node.right);
    }
}
```

---

## Detailed Walkthrough

### 1. Dummy Node and Tail Pointer

```java
TreeNode ans = new TreeNode(0);
cur = ans;
```

Again, `ans` is a dummy node to simplify construction.

`cur` always points to the last node in the new increasing-order chain.

---

### 2. Recursive In-order Traversal

```java
public void inorder(TreeNode node) {
    if (node == null) return;
    inorder(node.left);
    ...
    inorder(node.right);
}
```

This visits nodes in ascending order.

---

### 3. Remove Left Child

```java
node.left = null;
```

The problem requires that every node in the final tree must have no left child.

So we explicitly cut the left pointer.

---

### 4. Attach Current Node to the Result

```java
cur.right = node;
cur = node;
```

This appends the current node to the end of the growing right-only chain.

Then `cur` moves forward to the newly added node.

---

### 5. Continue to Right Subtree

```java
inorder(node.right);
```

Now process the larger nodes.

---

## Why We Can Reuse Nodes Safely

The original BST already contains the nodes we need.

We are not changing their values, only their pointers.

Since in-order traversal visits them in sorted order, relinking them in that same order produces the desired final structure.

The key operation is clearing `left`, since the final tree must not have left children.

---

## Example Walkthrough

Using this BST:

```text
    5
   / \\
  1   7
```

In-order traversal order is:

```text
1, 5, 7
```

Now the relinking happens like this:

### Visit 1

- `1.left = null`
- `cur.right = 1`
- `cur = 1`

### Visit 5

- `5.left = null`
- `1.right = 5`
- `cur = 5`

### Visit 7

- `7.left = null`
- `5.right = 7`
- `cur = 7`

Final tree:

```text
1
 \\
  5
   \\
    7
```

Exactly as required.

---

## Complexity Analysis

Let `N` be the number of nodes, and `H` be the height of the tree.

### Time Complexity

```text
O(N)
```

Why?

Every node is visited exactly once during in-order traversal.

Each visit does constant work besides recursive calls.

---

### Space Complexity

```text
O(H)
```

Why?

We do not store a separate list of values anymore.

The only extra space comes from the implicit recursion stack.

So:

- balanced tree: `O(log N)`
- worst-case skewed tree: `O(N)`

This is better than Approach 1 in terms of additional memory.

---

## Strengths and Weaknesses

### Strengths

- does not allocate new nodes
- more memory efficient
- builds the answer directly
- elegant once understood

### Weaknesses

- modifies the original tree in place
- slightly trickier than the value-list method

---

# Comparing the Two Approaches

## Approach 1: In-order Traversal with List

### Idea

Collect all values in sorted order, then build a new right-skewed tree.

### Time

```text
O(N)
```

### Space

```text
O(N)
```

### Best For

- simplicity
- easy explanation
- clear separation of steps

---

## Approach 2: Traversal with Relinking

### Idea

Traverse in-order and directly relink the existing nodes into a right-only chain.

### Time

```text
O(N)
```

### Space

```text
O(H)
```

### Best For

- better memory efficiency
- reusing original nodes
- cleaner in-place transformation

---

# Which Approach Should You Prefer?

## For easiest understanding

Use **Approach 1**.

It is straightforward:

- gather sorted values
- rebuild the tree

## For a better optimized solution

Use **Approach 2**.

It avoids building new nodes and avoids the full value list.

This is usually the preferred approach.

---

# Final Takeaway

The entire problem depends on one simple observation:

> In-order traversal of a BST gives values in increasing order.

Once we know that, the problem becomes:

- take nodes in sorted order
- arrange them as a right-only chain

Approach 1 does this in two separate phases.
Approach 2 does it directly during traversal.

Both are correct and efficient, but the second one is more space-efficient because it reuses the original tree nodes.

---

# Summary

## Main Insight

- BST in-order traversal gives node values in sorted order.
- The required final tree is exactly those values arranged in a right-skewed chain.

---

## Approach 1: In-order Traversal with List

### Steps

- collect all values using in-order traversal
- build a new tree using those values in order

### Java Code

```java
class Solution {
    public TreeNode increasingBST(TreeNode root) {
        List<Integer> vals = new ArrayList();
        inorder(root, vals);
        TreeNode ans = new TreeNode(0), cur = ans;
        for (int v: vals) {
            cur.right = new TreeNode(v);
            cur = cur.right;
        }
        return ans.right;
    }

    public void inorder(TreeNode node, List<Integer> vals) {
        if (node == null) return;
        inorder(node.left, vals);
        vals.add(node.val);
        inorder(node.right, vals);
    }
}
```

### Complexity

- **Time:** `O(N)`
- **Space:** `O(N)`

---

## Approach 2: Traversal with Relinking

### Steps

- perform in-order traversal
- for each visited node:
  - clear its left child
  - append it to the right of the current tail

### Java Code

```java
class Solution {
    TreeNode cur;
    public TreeNode increasingBST(TreeNode root) {
        TreeNode ans = new TreeNode(0);
        cur = ans;
        inorder(root);
        return ans.right;
    }

    public void inorder(TreeNode node) {
        if (node == null) return;
        inorder(node.left);
        node.left = null;
        cur.right = node;
        cur = node;
        inorder(node.right);
    }
}
```

### Complexity

- **Time:** `O(N)`
- **Space:** `O(H)`

## Recommended

Use **Approach 2** when you want the more space-efficient in-place solution.
