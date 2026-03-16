# 938. Range Sum of BST

## Approach 1: Depth First Search

## Overview

We are given:

- the root of a **Binary Search Tree (BST)**
- two integers `low` and `high`

We must return the **sum of all node values** that lie in the inclusive range:

```text
[low, high]
```

This problem can be solved with a standard tree traversal, but because the input is a **BST**, we can do better than blindly visiting both children of every node.

The BST property lets us **prune** subtrees that cannot possibly contain valid values.

---

# Core BST Insight

For any node with value `node.val`:

- all values in the **left subtree** are smaller than `node.val`
- all values in the **right subtree** are larger than `node.val`

That means:

## Case 1: `node.val < low`

If the current node is smaller than the lower bound:

```text
node.val < low
```

then:

- the current node is not part of the answer
- every node in the left subtree is even smaller
- so the **entire left subtree can be skipped**

Only the **right subtree** could still contain valid values.

---

## Case 2: `node.val > high`

If the current node is larger than the upper bound:

```text
node.val > high
```

then:

- the current node is not part of the answer
- every node in the right subtree is even larger
- so the **entire right subtree can be skipped**

Only the **left subtree** could still contain valid values.

---

## Case 3: `low <= node.val <= high`

If the node value is inside the range:

- add it to the answer
- both left and right subtrees might still contain valid values

So in that case, we need to explore both sides.

---

# Why Depth First Search Works Well

A Depth First Search (DFS) is a natural fit for tree problems because it explores each subtree fully before backtracking.

For this problem, DFS is especially convenient because:

- it is easy to implement recursively
- it is also easy to implement iteratively with a stack
- we can naturally prune subtrees using BST ordering

This document explains both versions:

1. **Recursive DFS**
2. **Iterative DFS using a Stack**

---

# Recursive DFS

## Intuition

The recursive solution mirrors the tree structure directly.

At each node:

1. if the node is null, stop
2. if the node value is in range, add it to the answer
3. if the node value is greater than `low`, the left subtree might contain valid values
4. if the node value is less than `high`, the right subtree might contain valid values

This way, we only recurse into subtrees that can still contribute to the final sum.

---

## Java Code

```java
class Solution {
    int ans;

    public int rangeSumBST(TreeNode root, int low, int high) {
        ans = 0;
        dfs(root, low, high);
        return ans;
    }

    public void dfs(TreeNode node, int low, int high) {
        if (node != null) {
            if (low <= node.val && node.val <= high)
                ans += node.val;
            if (low < node.val)
                dfs(node.left, low, high);
            if (node.val < high)
                dfs(node.right, low, high);
        }
    }
}
```

---

## Detailed Walkthrough

### 1. Running Sum

```java
int ans;
```

This stores the total sum of all valid node values found during traversal.

It is a class-level variable so that all recursive calls can update the same accumulator.

---

### 2. Initialization

```java
public int rangeSumBST(TreeNode root, int low, int high) {
    ans = 0;
    dfs(root, low, high);
    return ans;
}
```

We initialize the answer to `0`, perform DFS, and return the accumulated sum at the end.

---

### 3. Base Check

```java
if (node != null) {
    ...
}
```

If the current node is null, there is nothing to process.

---

### 4. Add Value If In Range

```java
if (low <= node.val && node.val <= high)
    ans += node.val;
```

If the current node value lies within `[low, high]`, include it in the sum.

---

### 5. Explore Left Only If It Can Help

```java
if (low < node.val)
    dfs(node.left, low, high);
```

Why this condition?

If `node.val` is strictly greater than `low`, then the left subtree might still contain values within the range.

But if `node.val <= low`, then everything in the left subtree is even smaller, so no node there can qualify.

So in that case, we skip the left subtree entirely.

---

### 6. Explore Right Only If It Can Help

```java
if (node.val < high)
    dfs(node.right, low, high);
```

Why?

If `node.val` is strictly less than `high`, then the right subtree might still contain valid values.

But if `node.val >= high`, then everything in the right subtree is even larger, so we can skip it.

---

## Example Walkthrough

Take:

```text
root = [10,5,15,3,7,null,18]
low = 7
high = 15
```

Tree:

```text
       10
      /  \\
     5    15
    / \\     \\
   3   7     18
```

### Start at node 10

- `10` is within range → add `10`
- `low < 10`, so explore left
- `10 < high`, so explore right

### Visit node 5

- `5` is not in range
- `low < 5` is false, so skip left subtree of 5? Let's check:
  - `7 < 5` is false indeed, and because 5 is below the range, everything in its left subtree is even smaller, so skipping is correct
- `5 < high`, so explore right subtree

### Visit node 7

- `7` is in range → add `7`
- `low < 7` is false, so skip left
- `7 < high`, so explore right (null)

### Visit node 15

- `15` is in range → add `15`
- `low < 15`, so explore left (null)
- `15 < high` is false, so skip right subtree entirely

### Final sum

```text
10 + 7 + 15 = 32
```

---

# Iterative DFS Using a Stack

## Intuition

If we do not want recursion, we can simulate the DFS ourselves using an explicit stack.

The logic stays exactly the same:

- pop one node
- add it if it lies in range
- push left child only if left subtree may contain valid values
- push right child only if right subtree may contain valid values

This iterative version is often useful when recursion depth may be a concern.

---

## Java Code

```java
class Solution {
    public int rangeSumBST(TreeNode root, int low, int high) {
        int ans = 0;
        Stack<TreeNode> stack = new Stack();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (node != null) {
                if (low <= node.val && node.val <= high)
                    ans += node.val;
                if (low < node.val)
                    stack.push(node.left);
                if (node.val < high)
                    stack.push(node.right);
            }
        }

        return ans;
    }
}
```

---

## Detailed Walkthrough

### 1. Initialize Answer and Stack

```java
int ans = 0;
Stack<TreeNode> stack = new Stack();
stack.push(root);
```

- `ans` stores the running sum
- the stack controls DFS order

---

### 2. Process Until the Stack Is Empty

```java
while (!stack.isEmpty()) {
    TreeNode node = stack.pop();
    ...
}
```

Each iteration processes one node.

---

### 3. Skip Null Nodes

```java
if (node != null) {
    ...
}
```

Only real nodes are processed.

---

### 4. Add Current Node If Valid

```java
if (low <= node.val && node.val <= high)
    ans += node.val;
```

Same as in the recursive version.

---

### 5. Push Left Child Only If Necessary

```java
if (low < node.val)
    stack.push(node.left);
```

This means the left subtree might still contain values in range.

---

### 6. Push Right Child Only If Necessary

```java
if (node.val < high)
    stack.push(node.right);
```

This means the right subtree might still contain values in range.

---

## Why the Iterative Version Is Correct

The correctness is identical to the recursive solution.

The only difference is that recursion implicitly uses the call stack, while here we maintain our own stack explicitly.

Since the same pruning conditions are applied, both versions explore exactly the same useful subtrees and compute the same sum.

---

# Why Pruning Matters

If we ignored the BST property, a generic DFS would visit every node and simply check whether it lies in range.

That would still work, but it would miss an important optimization.

Because this is a BST, we can sometimes eliminate an entire subtree immediately.

For example:

- if `node.val < low`, there is no reason to search left
- if `node.val > high`, there is no reason to search right

This can reduce the actual amount of work significantly on many inputs, even though the worst-case complexity remains linear.

---

# Complexity Analysis

Let `N` be the number of nodes in the tree.

## Time Complexity

```text
O(N)
```

Why?

In the worst case, we may still visit every node once.

Even though pruning may skip many subtrees in practice, the worst-case scenario still occurs when most of the tree lies inside or near the range.

So the asymptotic worst-case time complexity is linear.

---

## Space Complexity

```text
O(N)
```

Why?

For both recursive and iterative DFS:

- the worst-case tree shape is a chain
- then the recursion stack or explicit stack can grow to size `N`

So the worst-case auxiliary space is linear.

More precisely:

- recursive version uses `O(H)` call stack space
- iterative version uses `O(H)` explicit stack space

where `H` is the tree height

Thus:

- balanced BST → `O(log N)`
- skewed BST → `O(N)`

---

# Comparing the Two Implementations

## Recursive DFS

### Strengths

- shorter
- very natural for tree traversal
- easy to read

### Weaknesses

- uses recursion stack
- may risk stack overflow on very deep trees

---

## Iterative DFS

### Strengths

- avoids recursion depth issues
- explicit control over traversal
- same pruning efficiency

### Weaknesses

- slightly more verbose
- manual stack management

---

# Final Takeaway

The key idea is not just to do DFS, but to use the **BST property to prune** unnecessary branches.

At each node:

- if the value is within range, add it
- if the value is too small, only search right
- if the value is too large, only search left

That turns a plain traversal into a smarter traversal.

Both recursive and iterative DFS implement exactly this same idea.

---

# Summary

## Main Insight

Use DFS, but prune subtrees using the BST ordering rule.

- if `node.val < low`, only the right subtree can help
- if `node.val > high`, only the left subtree can help
- otherwise, add the node and explore both possible sides

---

## Recursive DFS

```java
class Solution {
    int ans;

    public int rangeSumBST(TreeNode root, int low, int high) {
        ans = 0;
        dfs(root, low, high);
        return ans;
    }

    public void dfs(TreeNode node, int low, int high) {
        if (node != null) {
            if (low <= node.val && node.val <= high)
                ans += node.val;
            if (low < node.val)
                dfs(node.left, low, high);
            if (node.val < high)
                dfs(node.right, low, high);
        }
    }
}
```

### Complexity

- **Time:** `O(N)`
- **Space:** `O(N)` worst case

---

## Iterative DFS

```java
class Solution {
    public int rangeSumBST(TreeNode root, int low, int high) {
        int ans = 0;
        Stack<TreeNode> stack = new Stack();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (node != null) {
                if (low <= node.val && node.val <= high)
                    ans += node.val;
                if (low < node.val)
                    stack.push(node.left);
                if (node.val < high)
                    stack.push(node.right);
            }
        }

        return ans;
    }
}
```

### Complexity

- **Time:** `O(N)`
- **Space:** `O(N)` worst case

## Recommended

Use the **recursive DFS** for clarity, or the **iterative DFS** if you want to avoid recursion depth concerns.
