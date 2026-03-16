# 701. Insert into a Binary Search Tree

## Detailed Notes on Recursive and Iterative Insertion

## Intuition

One of the biggest advantages of a **Binary Search Tree (BST)** is that searching for an element can be efficient.

In a balanced BST:

- search takes `O(log N)` time on average
- insertion also takes `O(log N)` time on average

This is because insertion follows almost the same decision process as search.

To insert a new value `val`, we repeatedly compare it with the current node:

- if `val > node.val`, move to the **right subtree**
- if `val < node.val`, move to the **left subtree**

Since the problem guarantees that `val` does not already exist in the tree, we never have to handle the equality case.

Eventually, we reach a `null` child position where the new value should be inserted.

So the fundamental idea is:

> Follow the standard BST search path until you find the empty spot where the new node belongs.

That is why insertion in a BST is so natural.

---

# Core BST Property Used

A BST satisfies:

- all values in the left subtree are smaller than the node value
- all values in the right subtree are larger than the node value

So for inserting `val`:

- if `val` is smaller than the current node, it must go somewhere in the left subtree
- if `val` is larger than the current node, it must go somewhere in the right subtree

This repeated comparison narrows the search exactly like binary search does on a sorted array.

---

# High-Level Strategy

No matter which implementation we choose, the insertion logic is the same:

1. start at the root
2. compare `val` with the current node value
3. go left or right based on BST rules
4. once a `null` child is reached, place the new node there

There are two standard ways to implement this:

1. **Recursion**
2. **Iteration**

Both are correct. They mainly differ in style and auxiliary space usage.

---

# Approach 1: Recursion

## Intuition

The recursive solution is the most direct expression of BST insertion.

At any node:

- if the node is `null`, this is the insertion point
- otherwise, recursively insert into the left or right subtree depending on the value comparison

Because each subtree is itself a BST, the problem naturally breaks into a smaller subproblem:

> Insert `val` into the correct subtree.

This is why recursion fits so well.

---

## Recursive Logic

For a subtree rooted at `root`:

### Case 1: `root == null`

That means we have found the correct empty position for the new value.

So we create and return a new node:

```text
new TreeNode(val)
```

---

### Case 2: `val > root.val`

The new value belongs in the right subtree.

So recursively insert there:

```text
root.right = insertIntoBST(root.right, val)
```

---

### Case 3: `val < root.val`

The new value belongs in the left subtree.

So recursively insert there:

```text
root.left = insertIntoBST(root.left, val)
```

After inserting into the correct subtree, return the current root.

---

## Java Code

```java
class Solution {
  public TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);

    // insert into the right subtree
    if (val > root.val) root.right = insertIntoBST(root.right, val);
    // insert into the left subtree
    else root.left = insertIntoBST(root.left, val);
    return root;
  }
}
```

---

## Detailed Walkthrough

### 1. Base Case

```java
if (root == null) return new TreeNode(val);
```

This is the key insertion moment.

When recursion reaches a null child reference, we create the new node and return it upward.

That returned node becomes attached to its parent.

---

### 2. Go Right if the Value Is Larger

```java
if (val > root.val) root.right = insertIntoBST(root.right, val);
```

If `val` is larger than the current node, the BST property says it must belong somewhere in the right subtree.

So we recursively insert there.

---

### 3. Go Left Otherwise

```java
else root.left = insertIntoBST(root.left, val);
```

If `val` is smaller, it belongs in the left subtree.

---

### 4. Return the Current Root

```java
return root;
```

This ensures that the structure of the tree is rebuilt correctly on the way back up the recursion.

The tree root remains unchanged unless the original root was null.

---

## Example Walkthrough

Suppose:

```text
root = [4,2,7,1,3]
val = 5
```

Tree:

```text
      4
     / \\
    2   7
   / \\
  1   3
```

Now insert `5`.

### Step 1

Compare with `4`:

```text
5 > 4
```

Go right to node `7`.

### Step 2

Compare with `7`:

```text
5 < 7
```

Go left.

### Step 3

Left child of `7` is null.

So create:

```text
new TreeNode(5)
```

Attach it as `7.left`.

Final tree:

```text
      4
     / \\
    2   7
   / \\  /
  1   3 5
```

---

## Why This Is Correct

At each step, recursion moves into the only subtree where `val` could possibly belong while preserving the BST property.

The base case inserts the node exactly where the correct null position is found.

Because we never violate the left-smaller / right-larger ordering, the resulting tree remains a valid BST.

---

## Complexity Analysis

Let `H` be the height of the tree.

### Time Complexity

```text
O(H)
```

Why?

At each step, we move down exactly one level of the tree.

So the number of comparisons is proportional to the height.

That means:

- average case for a balanced BST: `O(log N)`
- worst case for a skewed BST: `O(N)`

---

### Space Complexity

```text
O(H)
```

Why?

The recursive calls use the call stack.

So:

- average case: `O(log N)`
- worst case: `O(N)`

---

## Clarification About Master Theorem Mention

Sometimes explanations compare BST search and insertion with binary search.

That intuition is fine in the balanced case, because each step tends to eliminate about half the remaining search space.

However, the simplest and most precise complexity statement here is:

```text
Time = O(H)
```

where `H` is the tree height.

Then we derive:

- balanced tree → `H = O(log N)`
- skewed tree → `H = O(N)`

That is the cleanest way to express complexity.

---

# Approach 2: Iteration

## Intuition

The recursive solution can be converted directly into an iterative one.

Instead of letting the call stack remember the current position, we keep a pointer `node` and move through the tree ourselves.

At each step:

- compare `val` with `node.val`
- move left or right
- if the required child is null, insert immediately

This avoids recursion and therefore uses constant auxiliary space.

---

## Iterative Logic

1. Start with `node = root`
2. While `node != null`:
   - if `val > node.val`:
     - if `node.right == null`, insert there
     - else move to `node.right`
   - otherwise:
     - if `node.left == null`, insert there
     - else move to `node.left`
3. If the original root was null, return a new node

---

## Java Code

```java
class Solution {
  public TreeNode insertIntoBST(TreeNode root, int val) {
    TreeNode node = root;
    while (node != null) {
      // insert into the right subtree
      if (val > node.val) {
        // insert right now
        if (node.right == null) {
          node.right = new TreeNode(val);
          return root;
        }
        else node = node.right;
      }
      // insert into the left subtree
      else {
        // insert right now
        if (node.left == null) {
          node.left = new TreeNode(val);
          return root;
        }
        else node = node.left;
      }
    }
    return new TreeNode(val);
  }
}
```

---

## Detailed Walkthrough

### 1. Start at the Root

```java
TreeNode node = root;
```

This pointer moves downward until the insertion spot is found.

---

### 2. Traverse Until a Null Child Position Is Found

```java
while (node != null) {
    ...
}
```

As long as there is a current node, keep descending.

---

### 3. Move Right if the Value Is Larger

```java
if (val > node.val) {
    if (node.right == null) {
        node.right = new TreeNode(val);
        return root;
    }
    else node = node.right;
}
```

If the right child is missing, that is the insertion point.

Otherwise continue moving right.

---

### 4. Move Left if the Value Is Smaller

```java
else {
    if (node.left == null) {
        node.left = new TreeNode(val);
        return root;
    }
    else node = node.left;
}
```

Same idea for the left side.

---

### 5. Handle Empty Original Tree

```java
return new TreeNode(val);
```

If the while loop never runs, that means the original root was null.

So the inserted node itself becomes the new root.

---

## Example Walkthrough

Again take:

```text
root = [40,20,60,10,30,50,70]
val = 25
```

Tree:

```text
        40
       /  \\
     20    60
    / \\   / \\
  10  30 50  70
```

### Step 1

Start at `40`.

```text
25 < 40
```

Move left to `20`.

### Step 2

At `20`:

```text
25 > 20
```

Move right to `30`.

### Step 3

At `30`:

```text
25 < 30
```

Check left child.

It is null, so insert `25` there.

Final tree:

```text
        40
       /  \\
     20    60
    / \\   / \\
  10  30 50  70
      /
     25
```

---

## Why This Is Correct

The iterative version follows the exact same search path as the recursive version.

At each node, it chooses the only subtree where the new value can belong while preserving BST rules.

When the appropriate null child is found, insertion there preserves the BST property.

So correctness is identical to the recursive solution.

---

## Complexity Analysis

Let `H` be the tree height.

### Time Complexity

```text
O(H)
```

Because we move downward through only one path from the root.

So:

- average case: `O(log N)`
- worst case: `O(N)`

---

### Space Complexity

```text
O(1)
```

Why?

We only use a constant number of pointers and variables.

No recursion stack and no auxiliary data structure proportional to tree size is used.

---

# Comparing the Two Approaches

## Approach 1: Recursion

### Strengths

- shorter
- elegant
- directly matches the recursive tree structure

### Weaknesses

- uses call stack
- worst-case recursion depth can be large

---

## Approach 2: Iteration

### Strengths

- constant extra space
- avoids recursion depth issues
- equally efficient in time

### Weaknesses

- slightly more verbose
- a bit less elegant than the recursive version

---

# Which Approach Should You Prefer?

## For simplicity and readability

Use **recursion**.

It expresses the BST insertion logic very naturally.

## For constant extra space

Use **iteration**.

It is slightly longer, but more robust if very deep trees are possible.

---

# Final Takeaway

The main advantage of a BST is that insertion follows the same idea as search.

At every step:

- compare with the current node
- move left or right based on BST order
- insert when a null child spot is found

So the real cost of insertion is simply the cost of following one root-to-leaf path.

That is why the complexity is:

```text
O(H)
```

where `H` is the tree height.

In a balanced BST, insertion is logarithmic.
In a skewed BST, insertion degrades to linear.

---

# Summary

## Core Idea

To insert a value into a BST:

- move right if the value is greater
- move left if the value is smaller
- insert at the first null child encountered in the correct direction

## Approach 1: Recursion

```java
class Solution {
  public TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);

    if (val > root.val) root.right = insertIntoBST(root.right, val);
    else root.left = insertIntoBST(root.left, val);
    return root;
  }
}
```

### Complexity

- **Time:** `O(H)`
- **Space:** `O(H)`

---

## Approach 2: Iteration

```java
class Solution {
  public TreeNode insertIntoBST(TreeNode root, int val) {
    TreeNode node = root;
    while (node != null) {
      if (val > node.val) {
        if (node.right == null) {
          node.right = new TreeNode(val);
          return root;
        } else node = node.right;
      } else {
        if (node.left == null) {
          node.left = new TreeNode(val);
          return root;
        } else node = node.left;
      }
    }
    return new TreeNode(val);
  }
}
```

### Complexity

- **Time:** `O(H)`
- **Space:** `O(1)`

## Recommended

- Use **recursion** for clarity
- Use **iteration** when constant auxiliary space is preferred
