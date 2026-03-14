# 1644. Lowest Common Ancestor of a Binary Tree II — Approaches

## Overview

This problem is a follow-up to **Lowest Common Ancestor of a Binary Tree**.

In the original LCA problem, it is guaranteed that both nodes **p** and **q** exist in the tree.
In this variation, **either node may be missing**, so we must verify their existence before returning the LCA.

Possible scenarios:

1. `p` and `q` exist → return their LCA.
2. `q` is in the subtree of `p` → return `p`.
3. `p` is in the subtree of `q` → return `q`.
4. `p` does not exist → return `null`.
5. `q` does not exist → return `null`.
6. Neither exists → return `null`.

---

# Approach 1: Depth First Search — Modify Standard LCA

## Intuition

We reuse the classic **LCA solution**:

```
if node == null or node == p or node == q
    return node
```

But that approach **assumes both nodes exist**.

Example issue:

If recursion finds `p`, it immediately returns `p` and stops exploring the subtree.
If `q` does not exist there, we will incorrectly return `p`.

So after computing the LCA, we must **verify the existence of the other node**.

---

## Algorithm

1. Compute `ans = LCA(root, p, q)`.
2. If `ans == p`, verify `q` exists in `p`'s subtree.
3. If `ans == q`, verify `p` exists in `q`'s subtree.
4. If verification fails, return `null`.
5. Otherwise return `ans`.

### LCA function

```
If node is null or node == p or node == q
    return node

left = LCA(node.left)
right = LCA(node.right)

If left != null AND right != null
    return node

Return left OR right
```

### DFS existence check

```
If node == target → return true
If node == null → return false
Return dfs(left) OR dfs(right)
```

---

## Java Implementation

```java
class Solution {

    public TreeNode lowestCommonAncestor(
        TreeNode root,
        TreeNode p,
        TreeNode q
    ) {
        TreeNode ans = LCA(root, p, q);

        if (ans == p) {
            return dfs(p, q) ? p : null;
        }
        else if (ans == q) {
            return dfs(q, p) ? q : null;
        }

        return ans;
    }

    private TreeNode LCA(TreeNode node, TreeNode p, TreeNode q) {
        if (node == null || node == p || node == q) return node;

        TreeNode left = LCA(node.left, p, q);
        TreeNode right = LCA(node.right, p, q);

        if (left != null && right != null) return node;
        else if (left != null) return left;
        else return right;
    }

    private boolean dfs(TreeNode node, TreeNode target) {
        if (node == target) return true;
        if (node == null) return false;

        return dfs(node.left, target) || dfs(node.right, target);
    }
}
```

---

## Complexity Analysis

Let **n** be the number of nodes.

### Time Complexity

```
O(n)
```

- LCA traversal → O(n)
- DFS verification → O(n)

### Space Complexity

```
O(n)
```

Due to recursion stack in worst-case skewed tree.

---

# Approach 2: DFS with 2-of-3 Conditions

## Intuition

For any node, three conditions may hold:

1. The node itself is `p` or `q`.
2. One target is found in the left subtree.
3. One target is found in the right subtree.

If **any two conditions are true**, we know:

```
Both p and q exist in this subtree
```

So we mark the solution as valid.

---

## Algorithm

1. Maintain a boolean `nodesFound`.
2. DFS traversal:
   - recurse left
   - recurse right
3. Track the number of satisfied conditions.
4. If `conditions == 2`:
   - both nodes found
   - mark `nodesFound = true`
5. Return node as LCA candidate.

Finally:

```
return nodesFound ? ans : null
```

---

## Java Implementation

```java
class Solution {

    boolean nodesFound = false;

    public TreeNode lowestCommonAncestor(
        TreeNode root,
        TreeNode p,
        TreeNode q
    ) {
        TreeNode ans = dfs(root, p, q);
        return nodesFound ? ans : null;
    }

    private TreeNode dfs(TreeNode node, TreeNode p, TreeNode q) {

        if (node == null) return null;

        TreeNode left = dfs(node.left, p, q);
        TreeNode right = dfs(node.right, p, q);

        int conditions = 0;

        if (node == p || node == q) conditions++;
        if (left != null) conditions++;
        if (right != null) conditions++;

        if (conditions == 2) nodesFound = true;

        if ((left != null && right != null) || node == p || node == q)
            return node;

        return left != null ? left : right;
    }
}
```

---

## Complexity Analysis

Let **n** be the number of nodes.

### Time Complexity

```
O(n)
```

We perform a single DFS traversal.

### Space Complexity

```
O(n)
```

Worst-case recursion depth for a skewed tree.

---

# Key Insight

The difference between:

- **LCA I**
- **LCA II (this problem)**

is **node existence validation**.

Classic LCA assumes both nodes exist.
This version must **verify both nodes were found** before returning the result.

---

# Interview Takeaway

If asked this problem:

1. Explain **standard LCA** first.
2. Show why it fails when nodes might not exist.
3. Add existence validation.

The **Approach 2 (single DFS)** is usually considered the **cleanest interview solution**.
