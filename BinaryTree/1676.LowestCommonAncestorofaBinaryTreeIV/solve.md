# 1676. Lowest Common Ancestor of a Binary Tree IV

## Problem summary

We are given:

- the `root` of a binary tree
- an array `nodes` containing distinct tree nodes

We need to return the **lowest common ancestor (LCA)** of **all** nodes in `nodes`.

The standard LCA problem asks for two nodes.
This problem generalizes it to `k` nodes.

Important guarantees:

- all values are unique
- every node in `nodes` exists in the tree
- all nodes in `nodes` are distinct

So we do **not** need to handle missing-node cases.

---

# Core idea

For two nodes, the classic recursive LCA rule is:

- if current node is `p` or `q`, return it
- recursively solve left and right
- if both sides return non-null, current node is LCA
- otherwise return the non-null side

For **multiple nodes**, the same idea still works if we replace:

```text
"is current node p or q?"
```

with:

```text
"is current node one of the target nodes?"
```

That gives the cleanest solution.

---

# Approach 1: Recursive DFS with a target set (generalized classic LCA)

## Intuition

Convert the given `nodes` array into a hash set for `O(1)` membership checks.

Then run the classic LCA recursion:

At each node:

1. If node is null → return null
2. If node itself is one of the target nodes → return node
3. Recurse on left and right
4. If both return non-null → current node is the LCA
5. Otherwise return the non-null one

Why does this work for multiple nodes?

Because the recursion bubbles up the lowest subtree that contains targets from both sides, or a target node itself.

If all targets lie in one side, that side's answer keeps bubbling upward.
The first node where the target set is split across left/right (or one side plus current node) becomes the LCA.

---

## Algorithm

1. Put every node from `nodes` into a `HashSet<TreeNode> targets`
2. Call a DFS helper on `root`
3. In helper:
   - if node is null → return null
   - if node is in targets → return node
   - recurse into left and right
   - if both left and right are non-null → return current node
   - otherwise return whichever side is non-null
4. Return the helper result

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
        Set<TreeNode> targets = new HashSet<>();
        for (TreeNode node : nodes) {
            targets.add(node);
        }
        return dfs(root, targets);
    }

    private TreeNode dfs(TreeNode root, Set<TreeNode> targets) {
        if (root == null) {
            return null;
        }

        if (targets.contains(root)) {
            return root;
        }

        TreeNode left = dfs(root.left, targets);
        TreeNode right = dfs(root.right, targets);

        if (left != null && right != null) {
            return root;
        }

        return left != null ? left : right;
    }
}
```

---

## Why this works

Suppose all target nodes are in the subtree rooted at `X`.

- If they are all inside the left subtree of `X`, recursion returns the answer from left
- If all are inside the right subtree, recursion returns the answer from right
- If some are in left and some are in right, then `X` is exactly the first split point, so `X` is the LCA
- If `X` itself is in the target set and the rest are below it, then `X` is also the correct LCA

This is exactly the same correctness principle as the 2-node LCA solution, just generalized.

---

## Complexity

Let:

- `n` = number of nodes in the tree
- `k` = number of target nodes

### Time complexity

```text
O(n + k)
```

- `O(k)` to build the set
- `O(n)` for DFS

### Space complexity

```text
O(k + h)
```

- `O(k)` for the target set
- `O(h)` recursion stack, where `h` is tree height

Worst case:

```text
O(n + k)
```

and recursion stack can become `O(n)` in a skewed tree.

---

# Approach 2: Repeated pairwise LCA reduction

## Intuition

Another simple approach is to reduce the multi-node LCA problem into repeated 2-node LCA problems.

Idea:

1. Start with `answer = nodes[0]`
2. For every next node `nodes[i]`, replace:

```text
answer = LCA(root, answer, nodes[i])
```

At the end, `answer` will be the LCA of all target nodes.

This works because LCA is associative in the following sense:

```text
LCA(a, b, c) = LCA(LCA(a, b), c)
```

More generally:

```text
LCA(all nodes) = fold pairwise LCA over the list
```

This is conceptually straightforward, though less efficient than Approach 1.

---

## Algorithm

1. Initialize `ans = nodes[0]`
2. For each `i` from 1 to `nodes.length - 1`:
   - compute `ans = lca(root, ans, nodes[i])`
3. Return `ans`

The helper `lca(root, p, q)` is the standard 2-node LCA recursion.

---

## Java code

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
        TreeNode ans = nodes[0];

        for (int i = 1; i < nodes.length; i++) {
            ans = lca(root, ans, nodes[i]);
        }

        return ans;
    }

    private TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lca(root.left, p, q);
        TreeNode right = lca(root.right, p, q);

        if (left != null && right != null) {
            return root;
        }

        return left != null ? left : right;
    }
}
```

---

## Complexity

Let:

- `n` = size of the tree
- `k` = number of target nodes

### Time complexity

Each 2-node LCA computation is `O(n)`.

We do that `k - 1` times.

```text
O(n * k)
```

### Space complexity

```text
O(h)
```

due to recursion stack for each LCA call.

Worst case `O(n)` in a skewed tree.

---

## Pros and cons

### Pros

- very easy to derive if you already know 2-node LCA
- simple code

### Cons

- much slower than the single-pass DFS solution

---

# Approach 3: DFS counting how many target nodes are inside each subtree

## Intuition

Instead of returning a node directly like Approach 1, we can return **how many target nodes** are found in a subtree.

Then:

- for every node, count how many targets exist in:
  - left subtree
  - right subtree
  - current node itself
- once the total count at some node becomes equal to the total number of targets `k`,
  that node is an ancestor of all targets
- the **first such node found from below** is the LCA

This approach is very explicit and often easier to reason about for some people.

---

## Algorithm

1. Put all target nodes in a hash set
2. Let `totalTargets = nodes.length`
3. Run postorder DFS
4. For each node:
   - get `leftCount`
   - get `rightCount`
   - `self = 1` if current node is a target, else 0
   - `count = leftCount + rightCount + self`
5. If `count == totalTargets` and answer not yet assigned:
   - set answer = current node
6. Return `count`

Because this is postorder, the first node satisfying full coverage is the **lowest** such node.

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    private TreeNode answer = null;
    private int totalTargets;
    private Set<TreeNode> targets;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
        targets = new HashSet<>();
        for (TreeNode node : nodes) {
            targets.add(node);
        }

        totalTargets = nodes.length;
        dfs(root);
        return answer;
    }

    private int dfs(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int left = dfs(root.left);
        int right = dfs(root.right);
        int self = targets.contains(root) ? 1 : 0;

        int count = left + right + self;

        if (count == totalTargets && answer == null) {
            answer = root;
        }

        return count;
    }
}
```

---

## Why `answer == null` check matters

We want the **lowest** node containing all targets.

Since this DFS is postorder:

- children are processed before parent
- therefore the first node that accumulates all targets is the lowest valid LCA

So once `answer` is set, we should not overwrite it.

---

## Complexity

### Time complexity

```text
O(n + k)
```

- `O(k)` to build the set
- `O(n)` for DFS

### Space complexity

```text
O(k + h)
```

- `O(k)` for set
- `O(h)` recursion stack

---

# Approach 4: Parent map + ancestor intersection (not optimal, but useful to know)

## Intuition

We can also convert the problem into an ancestor-chain problem.

Steps:

1. Build a parent map using DFS/BFS from root
2. Gather ancestors of one target node into a set
3. Intersect with ancestors of the remaining nodes
4. Among the common ancestors, choose the deepest one

This works, but it is more cumbersome than the direct DFS solution.

Still, it is a useful alternative if you are more comfortable thinking in terms of parent links.

---

## High-level algorithm

1. Build `parent` map for every node
2. Start with set of ancestors of `nodes[0]`
3. For each remaining node:
   - compute its ancestor set
   - intersect with the running common-ancestor set
4. From the remaining common ancestors, pick the deepest one

To pick the deepest one, you can:

- compute depths during parent-map building
- choose the common ancestor with maximum depth

---

## Java code

```java
import java.util.*;

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        Map<TreeNode, Integer> depth = new HashMap<>();

        build(root, null, 0, parent, depth);

        Set<TreeNode> common = getAncestors(nodes[0], parent);

        for (int i = 1; i < nodes.length; i++) {
            Set<TreeNode> curr = getAncestors(nodes[i], parent);
            common.retainAll(curr);
        }

        TreeNode ans = null;
        int bestDepth = -1;

        for (TreeNode node : common) {
            if (depth.get(node) > bestDepth) {
                bestDepth = depth.get(node);
                ans = node;
            }
        }

        return ans;
    }

    private void build(TreeNode node, TreeNode par, int d,
                       Map<TreeNode, TreeNode> parent,
                       Map<TreeNode, Integer> depth) {
        if (node == null) return;

        parent.put(node, par);
        depth.put(node, d);

        build(node.left, node, d + 1, parent, depth);
        build(node.right, node, d + 1, parent, depth);
    }

    private Set<TreeNode> getAncestors(TreeNode node, Map<TreeNode, TreeNode> parent) {
        Set<TreeNode> ancestors = new HashSet<>();

        while (node != null) {
            ancestors.add(node);
            node = parent.get(node);
        }

        return ancestors;
    }
}
```

---

## Complexity

Let:

- `n` = tree size
- `k` = number of targets
- `h` = tree height

### Time complexity

Building parent/depth map:

```text
O(n)
```

Ancestor gathering:
Each target contributes up to `O(h)` ancestors.

So total:

```text
O(n + k*h)
```

Worst case for a skewed tree:

```text
O(n + k*n)
```

which is much worse than Approach 1.

### Space complexity

```text
O(n + k*h)
```

mainly due to parent map and ancestor sets.

---

# Which approach is best?

## Best interview solution

**Approach 1**

Why:

- clean generalization of classic LCA
- single DFS
- optimal time
- short code

## Best “explicit reasoning” solution

**Approach 3**

Why:

- directly counts how many target nodes are in each subtree
- easy to justify correctness

## Easy but slower solution

**Approach 2**

Good if you want to quickly derive a solution from standard 2-node LCA.

---

# Correctness intuition for Approach 1

Approach 1 works because each recursive call returns one of these:

- `null` if no target exists in that subtree
- a target node if that subtree contains one target chain
- the LCA of all targets in that subtree if the split has already happened below

At any node:

- if both left and right return non-null, then targets appear in both sides, so current node must be their LCA
- if current node itself is a target and the rest of the targets are below one side, current node is the correct lowest common ancestor
- if all targets are confined to one subtree, the answer is entirely in that subtree

So the first node from below where the target set “comes together” is the LCA.

---

# Final recommended solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
        Set<TreeNode> targets = new HashSet<>();
        for (TreeNode node : nodes) {
            targets.add(node);
        }
        return dfs(root, targets);
    }

    private TreeNode dfs(TreeNode root, Set<TreeNode> targets) {
        if (root == null) {
            return null;
        }

        if (targets.contains(root)) {
            return root;
        }

        TreeNode left = dfs(root.left, targets);
        TreeNode right = dfs(root.right, targets);

        if (left != null && right != null) {
            return root;
        }

        return left != null ? left : right;
    }
}
```

This is the cleanest and most efficient solution for the problem.
