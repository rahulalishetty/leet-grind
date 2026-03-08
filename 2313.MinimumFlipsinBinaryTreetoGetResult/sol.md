# Minimum Flips in a Boolean Binary Tree — Exhaustive Summary

## Problem Overview

We are given the root of a binary tree with the following meaning for node values:

### Leaf nodes

- `0` represents **false**
- `1` represents **true**

### Non-leaf nodes

- `2` represents **OR**
- `3` represents **AND**
- `4` represents **XOR**
- `5` represents **NOT**

We are also given a boolean `result`, which is the desired final evaluation of the root.

---

## Tree Evaluation Rules

The evaluation of a node works like this:

- If the node is a leaf, its evaluation is simply its value:
  - `0 -> false`
  - `1 -> true`

- If the node is an internal node:
  - evaluate its child or children
  - apply the boolean operator stored in that node

Special note:

- `NOT` nodes have exactly one child, either left or right
- `OR`, `AND`, and `XOR` nodes have two children

---

## Allowed Operation

In one operation, we may **flip a leaf node**:

- `0` becomes `1`
- `1` becomes `0`

We need to return the **minimum number of flips** required so that the whole tree evaluates to the desired `result`.

The problem guarantees that it is always possible.

---

# Core Insight

This is a classic **tree dynamic programming** problem.

At each node, we do not just want to know:

> what is the current evaluated value of this subtree?

That would not be enough.

Instead, we want to know:

> what is the minimum number of flips needed to make this subtree evaluate to `false`?
> and what is the minimum number of flips needed to make this subtree evaluate to `true`?

That is the right DP state.

---

# DP State Definition

For every node, compute two values:

- `dp0(node)` = minimum flips needed to make this subtree evaluate to `0`
- `dp1(node)` = minimum flips needed to make this subtree evaluate to `1`

So each subtree returns a pair:

```text
[minimum cost for false, minimum cost for true]
```

Once we compute this for the root:

- if `result == false`, answer is `dp0(root)`
- if `result == true`, answer is `dp1(root)`

---

# Why This State Is Sufficient

This state is enough because:

- the only thing the parent cares about from a child subtree is:
  - what is the minimum cost to make it false?
  - what is the minimum cost to make it true?

The parent does not need the exact sequence of flips used inside the child subtree.

So each subtree can be summarized completely by just these two values.

That is exactly what makes dynamic programming possible here.

---

# Base Case: Leaf Nodes

If the node is a leaf:

## Case 1: leaf value is `0`

It already evaluates to false.

So:

```text
dp0 = 0
dp1 = 1
```

Why?

- cost to make it false is `0`, since it already is false
- cost to make it true is `1`, because we need one flip

---

## Case 2: leaf value is `1`

It already evaluates to true.

So:

```text
dp0 = 1
dp1 = 0
```

Why?

- cost to make it false is `1`
- cost to make it true is `0`

---

# Transitions for Internal Nodes

Suppose for a node’s children we already know:

- left child returns:
  - `l0` = minimum cost to make left false
  - `l1` = minimum cost to make left true

- right child returns:
  - `r0` = minimum cost to make right false
  - `r1` = minimum cost to make right true

Then we combine them depending on the operator.

---

# OR Node (`val = 2`)

An OR expression is false **only if both children are false**.

So:

```text
dp0 = l0 + r0
```

An OR expression is true if **at least one child is true**.

Possible ways:

- left true, right false -> `l1 + r0`
- left false, right true -> `l0 + r1`
- left true, right true -> `l1 + r1`

So:

```text
dp1 = min(l1 + r0, l0 + r1, l1 + r1)
```

---

# AND Node (`val = 3`)

An AND expression is true **only if both children are true**.

So:

```text
dp1 = l1 + r1
```

An AND expression is false if **at least one child is false**.

Possible ways:

- left false, right false -> `l0 + r0`
- left false, right true -> `l0 + r1`
- left true, right false -> `l1 + r0`

So:

```text
dp0 = min(l0 + r0, l0 + r1, l1 + r0)
```

---

# XOR Node (`val = 4`)

XOR is true when the children are different.

Possible ways:

- left false, right true -> `l0 + r1`
- left true, right false -> `l1 + r0`

So:

```text
dp1 = min(l0 + r1, l1 + r0)
```

XOR is false when the children are the same.

Possible ways:

- left false, right false -> `l0 + r0`
- left true, right true -> `l1 + r1`

So:

```text
dp0 = min(l0 + r0, l1 + r1)
```

---

# NOT Node (`val = 5`)

A NOT node has only one child.

Suppose the child costs are:

```text
c0 = minimum cost to make child false
c1 = minimum cost to make child true
```

Then after applying NOT:

- to make the NOT expression false, the child must be true
- to make the NOT expression true, the child must be false

So:

```text
dp0 = c1
dp1 = c0
```

This is just a swap.

---

# Why the Transitions Are Correct

At every internal node, the result depends only on:

- the operator at this node
- the truth values of the child subtrees

Since each child subtree already tells us the minimum cost to force either truth value, the parent can combine those options and choose the cheapest valid combination.

That is exactly what the recurrence is doing.

---

# Recursive Tree DP Strategy

We solve the problem with postorder traversal:

1. solve left subtree
2. solve right subtree
3. combine the child results according to the operator

This guarantees that when we process a node, all information from its children is already available.

---

# Full Java Solution

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public int minimumFlips(TreeNode root, boolean result) {
        int[] dp = solve(root); // dp[0] = cost for false, dp[1] = cost for true
        return result ? dp[1] : dp[0];
    }

    private int[] solve(TreeNode node) {
        // Leaf node
        if (node.left == null && node.right == null) {
            if (node.val == 0) {
                return new int[]{0, 1};
            }
            return new int[]{1, 0}; // node.val == 1
        }

        // NOT node
        if (node.val == 5) {
            TreeNode child = (node.left != null) ? node.left : node.right;
            int[] c = solve(child);
            return new int[]{c[1], c[0]};
        }

        int[] L = solve(node.left);
        int[] R = solve(node.right);

        int l0 = L[0], l1 = L[1];
        int r0 = R[0], r1 = R[1];

        int[] ans = new int[2];

        if (node.val == 2) { // OR
            ans[0] = l0 + r0;
            ans[1] = Math.min(l1 + r0, Math.min(l0 + r1, l1 + r1));
        } else if (node.val == 3) { // AND
            ans[0] = Math.min(l0 + r0, Math.min(l0 + r1, l1 + r0));
            ans[1] = l1 + r1;
        } else { // XOR, node.val == 4
            ans[0] = Math.min(l0 + r0, l1 + r1);
            ans[1] = Math.min(l0 + r1, l1 + r0);
        }

        return ans;
    }
}
```

---

# Code Walkthrough

## `minimumFlips`

```java
public int minimumFlips(TreeNode root, boolean result) {
    int[] dp = solve(root);
    return result ? dp[1] : dp[0];
}
```

We compute the pair for the whole tree.

Then:

- if we want the root to evaluate to `true`, return `dp[1]`
- otherwise return `dp[0]`

---

## `solve(node)`

This function returns:

```text
[cost to make subtree false, cost to make subtree true]
```

for the subtree rooted at `node`.

---

## Leaf handling

```java
if (node.left == null && node.right == null) {
    if (node.val == 0) {
        return new int[]{0, 1};
    }
    return new int[]{1, 0};
}
```

This directly encodes the flip cost for a boolean leaf.

---

## NOT handling

```java
if (node.val == 5) {
    TreeNode child = (node.left != null) ? node.left : node.right;
    int[] c = solve(child);
    return new int[]{c[1], c[0]};
}
```

NOT simply swaps false and true costs.

---

## Binary operators

```java
int[] L = solve(node.left);
int[] R = solve(node.right);
```

We first compute the child costs.

Then we combine them depending on whether the current operator is OR, AND, or XOR.

That combination is the essence of the DP transition.

---

# Worked Example

Suppose we have this tree:

```text
        OR
       /  \
      0   AND
         /   \
        1     0
```

Encoded values:

- OR = `2`
- AND = `3`
- leaves = `0`, `1`, `0`

We want the whole tree to become `true`.

---

## Step 1: Leaf costs

Leaf `0`:

```text
[0, 1]
```

Leaf `1`:

```text
[1, 0]
```

Leaf `0`:

```text
[0, 1]
```

---

## Step 2: AND node

Left child = leaf `1` -> `[1, 0]`
Right child = leaf `0` -> `[0, 1]`

For AND:

```text
dp1 = l1 + r1 = 0 + 1 = 1
dp0 = min(l0 + r0, l0 + r1, l1 + r0)
    = min(1 + 0, 1 + 1, 0 + 0)
    = 0
```

So AND subtree returns:

```text
[0, 1]
```

Interpretation:

- make AND false: cost `0`
- make AND true: cost `1`

That makes sense: currently `1 AND 0 = false`, and flipping the last leaf makes it true.

---

## Step 3: OR root

Left child = leaf `0` -> `[0, 1]`
Right child = AND subtree -> `[0, 1]`

For OR:

```text
dp0 = l0 + r0 = 0 + 0 = 0
dp1 = min(l1 + r0, l0 + r1, l1 + r1)
    = min(1 + 0, 0 + 1, 1 + 1)
    = 1
```

So root returns:

```text
[0, 1]
```

Thus minimum cost to make root true is:

```text
1
```

---

# Another Example: NOT Node

Suppose the tree is:

```text
    NOT
     |
     0
```

Leaf `0` gives:

```text
[0, 1]
```

NOT swaps:

```text
[1, 0]
```

So:

- cost to make the whole tree false = `1`
- cost to make the whole tree true = `0`

Correct, because `NOT false = true`.

---

# Correctness Intuition

The algorithm is correct because of optimal substructure.

To force a node to evaluate to either false or true:

- we only need to know the minimum costs to force each child to false or true
- from those possibilities, we choose the cheapest combination that makes the operator produce the desired result

No global dependency is missed, because leaf flips inside one subtree do not directly interfere with leaf flips inside another subtree. The only interaction happens through the operator at the current node.

That is why combining child DP values is enough.

---

# Formal DP Summary

For each node return:

```text
(dpFalse, dpTrue)
```

### Leaf

- node = `0` -> `(0, 1)`
- node = `1` -> `(1, 0)`

### OR

- `false = l0 + r0`
- `true = min(l1 + r0, l0 + r1, l1 + r1)`

### AND

- `false = min(l0 + r0, l0 + r1, l1 + r0)`
- `true = l1 + r1`

### XOR

- `false = min(l0 + r0, l1 + r1)`
- `true = min(l0 + r1, l1 + r0)`

### NOT

- if child = `(c0, c1)`
- result = `(c1, c0)`

---

# Complexity Analysis

Let `n` be the number of nodes.

## Time Complexity: `O(n)`

Each node is processed exactly once.

At each node, we do only constant work:

- inspect the operator
- combine a constant number of values

So total time is:

```text
O(n)
```

---

## Space Complexity: `O(h)`

We use recursion, so the auxiliary space is the recursion stack depth.

If the tree height is `h`, then:

```text
Space = O(h)
```

### Worst case

If the tree is skewed:

```text
O(n)
```

### Balanced tree

If the tree is balanced:

```text
O(log n)
```

---

# Common Pitfalls

## 1. Computing only the current boolean value

That is not enough.

We need both:

- minimum flips to make subtree false
- minimum flips to make subtree true

Without both, the parent cannot make optimal decisions.

---

## 2. Forgetting that NOT has only one child

NOT is unary, unlike OR/AND/XOR.

So do not try to use both left and right children for it.

---

## 3. Mixing up XOR conditions

For XOR:

- true means children are different
- false means children are the same

This is easy to invert accidentally.

---

## 4. Treating internal nodes like flippable nodes

Only leaf nodes can be flipped.

Operator nodes are fixed.

So all cost changes come only from leaf flips.

---

# Final Takeaway

The problem becomes clean once you ask the right question for each subtree:

> What is the minimum cost to make this subtree false?
> What is the minimum cost to make this subtree true?

That leads directly to a tree DP with two values per node.

Then:

- leaves give simple base cases
- OR, AND, XOR combine left/right child costs
- NOT just swaps child costs

This yields a simple and optimal `O(n)` solution.

---

# Full Code Reference

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public int minimumFlips(TreeNode root, boolean result) {
        int[] dp = solve(root);
        return result ? dp[1] : dp[0];
    }

    private int[] solve(TreeNode node) {
        if (node.left == null && node.right == null) {
            if (node.val == 0) return new int[]{0, 1};
            return new int[]{1, 0};
        }

        if (node.val == 5) {
            TreeNode child = (node.left != null) ? node.left : node.right;
            int[] c = solve(child);
            return new int[]{c[1], c[0]};
        }

        int[] L = solve(node.left);
        int[] R = solve(node.right);

        int l0 = L[0], l1 = L[1];
        int r0 = R[0], r1 = R[1];

        int[] ans = new int[2];

        if (node.val == 2) { // OR
            ans[0] = l0 + r0;
            ans[1] = Math.min(l1 + r0, Math.min(l0 + r1, l1 + r1));
        } else if (node.val == 3) { // AND
            ans[0] = Math.min(l0 + r0, Math.min(l0 + r1, l1 + r0));
            ans[1] = l1 + r1;
        } else { // XOR
            ans[0] = Math.min(l0 + r0, l1 + r1);
            ans[1] = Math.min(l0 + r1, l1 + r0);
        }

        return ans;
    }
}
```
