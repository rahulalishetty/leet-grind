# 156. Binary Tree Upside Down

## Problem

Given the root of a binary tree, turn the tree upside down and return the new root.

Transformation rules:

- The original **left child** becomes the **new root**.
- The original **root** becomes the **new right child**.
- The original **right child** becomes the **new left child**.

These operations are applied level by level.

### Important guarantees

- Every right node has a sibling.
- Every right node has no children.

---

## Example 1

**Input**

```text
root = [1,2,3,4,5]
```

**Output**

```text
[4,5,2,null,null,3,1]
```

### Visual intuition

Original tree:

```text
    1
   / \\
  2   3
 / \\
4   5
```

After flipping upside down:

```text
    4
   / \\
  5   2
     / \\
    3   1
```

---

## Example 2

**Input**

```text
root = []
```

**Output**

```text
[]
```

---

## Example 3

**Input**

```text
root = [1]
```

**Output**

```text
[1]
```

---

## Constraints

- Number of nodes is in the range `[0, 10]`
- `1 <= Node.val <= 10`
- Every right node has a left sibling
- Every right node has no children

---

# Core Insight

This transformation always follows the **left spine** of the tree.

At each node:

- `node.left` will move upward and become the parent in the new tree.
- `node.right` will become the new left child of that promoted node.
- `node` itself will become the new right child.

So if we look at:

```text
    root
   /   \\
 left  right
```

after transformation around `left`, it becomes:

```text
   left
  /   \\
right root
```

That is the entire problem.

The only real challenge is preserving pointers while rewiring.

---

# Approach 1: Recursive Bottom-Up Rewiring

## Intuition

The new root is always the **leftmost node**.

So the recursive strategy is:

1. Keep going left until we reach the leftmost node.
2. That leftmost node becomes the new root.
3. While recursion returns upward, rewire each node:
   - `node.left.left = node.right`
   - `node.left.right = node`
4. Clear the old pointers:
   - `node.left = null`
   - `node.right = null`

This works naturally because once we return from deeper recursion, the subtree below is already flipped.

---

## Dry run on example

Original:

```text
    1
   / \\
  2   3
 / \\
4   5
```

### Step 1

Recurse down to node `4`.

Node `4` has no left child, so it becomes the new root.

### Step 2

Return to node `2`:

- `2.left = 4`
- set `4.left = 5`
- set `4.right = 2`
- clear `2.left` and `2.right`

Now partial tree:

```text
    4
   / \\
  5   2
```

### Step 3

Return to node `1`:

- `1.left = 2`
- set `2.left = 3`
- set `2.right = 1`
- clear `1.left` and `1.right`

Final tree:

```text
    4
   / \\
  5   2
     / \\
    3   1
```

---

## Java code

```java
class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        // Empty tree or already the final root
        if (root == null || root.left == null) {
            return root;
        }

        TreeNode newRoot = upsideDownBinaryTree(root.left);

        // Rewire pointers
        root.left.left = root.right;
        root.left.right = root;

        // Important: detach old links
        root.left = null;
        root.right = null;

        return newRoot;
    }
}
```

---

## Why this works

Suppose recursion has already correctly flipped the subtree rooted at `root.left`.

Then `root.left` is now somewhere near the bottom-right edge of the flipped subtree, and we can attach:

- original `root.right` as its new left child
- original `root` as its new right child

Because every right child has no children and always has a left sibling, this rewiring is valid and does not destroy any needed structure.

---

## Complexity

### Time Complexity

`O(n)`

Each node is visited once.

### Space Complexity

`O(h)`

Because of recursion stack, where `h` is the height of the tree.
In the worst case, since the tree follows the left chain, this can be `O(n)`.

---

# Approach 2: Iterative Pointer Rotation

## Intuition

Recursion is elegant, but the transformation can also be done iteratively.

We walk down the left chain and keep track of three things:

- `curr` → current node
- `parent` → previous node that will become the new right child
- `parentRight` → previous node's right child that will become the new left child

At each step, we rotate pointers like this:

Before:

```text
    curr
   /   \\
 next  right
```

After one local transformation relative to previous context:

- `curr.left = parentRight`
- `curr.right = parent`

Then move downward.

This is similar to reversing a linked list, except each node carries an extra right child that has to be preserved and repositioned.

---

## Iterative state transition

For each node:

1. Save `next = curr.left`
2. Set `curr.left = parentRight`
3. Save current right child into `parentRight`
4. Set `curr.right = parent`
5. Move:
   - `parent = curr`
   - `curr = next`

When traversal finishes, `parent` is the new root.

---

## Java code

```java
class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        TreeNode curr = root;
        TreeNode parent = null;
        TreeNode parentRight = null;

        while (curr != null) {
            TreeNode next = curr.left;

            // Current left becomes previous right sibling
            curr.left = parentRight;

            // Save current right before overwriting it
            parentRight = curr.right;

            // Current right becomes previous parent
            curr.right = parent;

            // Advance
            parent = curr;
            curr = next;
        }

        return parent;
    }
}
```

---

## Dry run

For:

```text
    1
   / \\
  2   3
 / \\
4   5
```

### Initially

- `curr = 1`
- `parent = null`
- `parentRight = null`

### Process node 1

- `next = 2`
- `1.left = null`
- `parentRight = 3`
- `1.right = null`
- move: `parent = 1`, `curr = 2`

### Process node 2

- `next = 4`
- `2.left = 3`
- `parentRight = 5`
- `2.right = 1`
- move: `parent = 2`, `curr = 4`

### Process node 4

- `next = null`
- `4.left = 5`
- `parentRight = null`
- `4.right = 2`
- move: `parent = 4`, `curr = null`

Return `4`.

Final:

```text
    4
   / \\
  5   2
     / \\
    3   1
```

---

## Why this works

The iterative method preserves exactly the information recursion would have stored on the call stack:

- previous parent
- previous right sibling

Each step rewires one node into its final position in the flipped tree.

This is effectively an in-place transformation.

---

## Complexity

### Time Complexity

`O(n)`

Each node is processed once.

### Space Complexity

`O(1)`

Only a few pointers are used.

---

# Approach 3: Stack-Based Reconstruction

## Intuition

This approach is not the most elegant, but it is useful for understanding the structure clearly.

We first walk down the left edge and push all nodes into a stack.

Then:

1. The last node in the left chain becomes the new root.
2. Pop nodes one by one.
3. Rebuild the flipped tree from bottom to top.

This is conceptually straightforward, though it uses extra memory.

---

## Algorithm

1. Traverse from root to the leftmost node, pushing each node onto a stack.
2. Pop the leftmost node: that becomes `newRoot`.
3. Maintain a pointer `curr` in the new tree.
4. For each popped parent node:
   - `curr.left = parent.right`
   - `curr.right = parent`
   - clear `parent.left` and `parent.right`
   - move `curr = curr.right`

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode node = root;

        // Push left chain
        while (node != null) {
            stack.push(node);
            node = node.left;
        }

        // Leftmost node becomes new root
        TreeNode newRoot = stack.pop();
        TreeNode curr = newRoot;

        while (!stack.isEmpty()) {
            TreeNode parent = stack.pop();

            curr.left = parent.right;
            curr.right = parent;

            parent.left = null;
            parent.right = null;

            curr = curr.right;
        }

        return newRoot;
    }
}
```

---

## Complexity

### Time Complexity

`O(n)`

Each node is visited at most once.

### Space Complexity

`O(h)`

The stack stores the left chain. Worst case `O(n)`.

---

# Which approach is best?

## Best practical choice

**Approach 2: Iterative Pointer Rotation**

Why:

- in-place
- `O(1)` extra space
- efficient
- interview-friendly once understood

## Best for intuition

**Approach 1: Recursive Bottom-Up Rewiring**

Why:

- shortest
- cleanest
- directly matches the tree transformation

## Best for conceptual clarity

**Approach 3: Stack-Based Reconstruction**

Why:

- easy to reason about bottom-up rebuilding
- useful if recursive rewiring feels too magical

---

# Common mistakes

## 1. Forgetting to null out old pointers

In the recursive approach, after rewiring:

```java
root.left = null;
root.right = null;
```

If you skip this, you can create cycles or leave stale connections.

---

## 2. Returning the wrong root

The new root is **not** the original root.
It is always the **leftmost node**.

---

## 3. Confusing which child becomes which

After flipping:

- original `right child` becomes **new left**
- original `root` becomes **new right**

That direction matters.

---

## 4. Trying generic tree rotation logic

This is not a standard AVL/red-black rotation problem.

It depends heavily on the guarantee:

- every right node has a left sibling
- every right node has no children

Without that guarantee, the same logic would not generally work safely.

---

# Final recommended solution

If you want the cleanest accepted answer in interviews and production-style code, use the iterative solution:

```java
class Solution {
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        TreeNode curr = root;
        TreeNode parent = null;
        TreeNode parentRight = null;

        while (curr != null) {
            TreeNode next = curr.left;
            curr.left = parentRight;
            parentRight = curr.right;
            curr.right = parent;
            parent = curr;
            curr = next;
        }

        return parent;
    }
}
```

---

# Summary

This problem looks unusual at first, but its structure is rigid.

The key observation is:

- the tree is transformed along the **left spine**
- each node is rewired so that:
  - old right child becomes new left child
  - old parent becomes new right child

You can solve it:

1. **Recursively** from bottom to top
2. **Iteratively** with pointer rotation
3. **Using a stack** by rebuilding from the leftmost node upward

For interviews, the recursive solution is easiest to explain, and the iterative solution is the strongest optimized version.
