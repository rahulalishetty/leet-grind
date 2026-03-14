# 2773. Height of Special Binary Tree

## Problem summary

We are given the root of a **special binary tree**.

The internal structure looks like a normal binary tree, but the leaves have extra links:

If the leaves in sorted order are:

```text
b1 < b2 < ... < bk
```

then for every leaf `bi`:

- `bi.right = bi + 1` if `i < k`, otherwise `b1`
- `bi.left = bi - 1` if `i > 1`, otherwise `bk`

So the leaves form a **cycle**.

We need to return the **height** of the tree, where height is:

> the number of edges on the longest path from the root to any other node

---

# Core difficulty

If we run a normal DFS on this structure and blindly follow both children, we can get stuck in the cycle formed by the leaves.

So the real task is:

- traverse the original tree structure
- avoid walking around the leaf cycle forever

That means we need a way to detect when a child pointer is not part of the real downward tree, but only part of the leaf cycle.

---

# Key observation

A true tree edge always goes from a node to a node deeper in the tree.

But the special leaf pointers connect one leaf to another leaf, which are at the **same conceptual bottom level**, and they create cycles.

So when we reach a leaf in the original tree, we should **stop** and not continue through its special leaf links.

How do we recognize such a leaf?

In the original tree, a leaf is a node whose left and right children are either:

- both null, or
- special cycle links to other leaves

The cleanest practical observation used in solutions is this:

A node is an original leaf if one of its children points back to something that points back into it in the special leaf cycle.

More commonly, people avoid the cycle by tracking visited nodes.

That leads to very clean DFS/BFS solutions.

---

# Approach 1: DFS with visited set

## Intuition

Since the graph is no longer a pure tree because of the leaf cycle, treat it as a graph and do DFS with a `visited` set.

From the root, recursively explore neighbors through `left` and `right`, but never revisit a node already on the traversal.

The longest distance from the root to any reachable node is the answer.

Because the only cycle is among leaves, the visited set safely prevents infinite loops.

---

## Algorithm

1. Start DFS from the root at depth `0`
2. Mark the current node as visited
3. Recurse into `left` child if it exists and is not visited
4. Recurse into `right` child if it exists and is not visited
5. Return the maximum depth seen

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int heightOfTree(TreeNode root) {
        Set<TreeNode> visited = new HashSet<>();
        return dfs(root, visited, 0);
    }

    private int dfs(TreeNode node, Set<TreeNode> visited, int depth) {
        if (node == null) {
            return depth - 1;
        }

        visited.add(node);

        int best = depth;

        if (node.left != null && !visited.contains(node.left)) {
            best = Math.max(best, dfs(node.left, visited, depth + 1));
        }

        if (node.right != null && !visited.contains(node.right)) {
            best = Math.max(best, dfs(node.right, visited, depth + 1));
        }

        return best;
    }
}
```

---

## Why this works

The structure is a connected graph with one special cycle among the leaves.

DFS with a visited set computes the farthest distance from the root without getting trapped in the cycle.

Since all normal tree paths from root are explored exactly once, the maximum depth obtained is the height.

---

## Complexity

Let `n` be the number of nodes.

### Time complexity

```text
O(n)
```

Each node is processed once.

### Space complexity

```text
O(n)
```

for the visited set and recursion stack.

---

# Approach 2: BFS with visited set

## Intuition

We can also solve the problem with BFS.

Because BFS explores level by level, the last level we reach from the root corresponds to the height.

Again, because of the leaf cycle, we must use a `visited` set.

This is often the simplest way to think about "height" in graph terms.

---

## Algorithm

1. Initialize a queue with `(root, 0)`
2. Mark root as visited
3. While queue is not empty:
   - pop `(node, depth)`
   - update answer with `depth`
   - push unvisited `left` and `right` children with depth + 1
4. Return the maximum depth encountered

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

class Solution {
    static class Pair {
        TreeNode node;
        int depth;

        Pair(TreeNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    public int heightOfTree(TreeNode root) {
        Queue<Pair> queue = new ArrayDeque<>();
        Set<TreeNode> visited = new HashSet<>();

        queue.offer(new Pair(root, 0));
        visited.add(root);

        int ans = 0;

        while (!queue.isEmpty()) {
            Pair cur = queue.poll();
            TreeNode node = cur.node;
            int depth = cur.depth;

            ans = Math.max(ans, depth);

            if (node.left != null && !visited.contains(node.left)) {
                visited.add(node.left);
                queue.offer(new Pair(node.left, depth + 1));
            }

            if (node.right != null && !visited.contains(node.right)) {
                visited.add(node.right);
                queue.offer(new Pair(node.right, depth + 1));
            }
        }

        return ans;
    }
}
```

---

## Why this works

BFS computes shortest distances from the root in the graph.

Since the original tree edges form all valid downward paths and the leaf cycle does not create a shorter route to deeper nodes than their tree path, the farthest BFS level from the root matches the required height.

The visited set prevents revisiting leaf-cycle nodes infinitely.

---

## Complexity

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(n)
```

for the queue and visited set.

---

# Approach 3: DFS that explicitly detects original leaves

## Intuition

Instead of treating the structure as a graph and using a visited set, we can try to stop recursion exactly when we reach an **original leaf** of the binary tree.

The special property says that leaves have extra left/right pointers to other leaves.

So we need a way to tell whether a node is a true stopping point in the original tree.

A very useful trick is:

A node is an original leaf if its children are not real descendants but part of the leaf cycle.

In many accepted solutions, this can be detected through local structure. One practical condition is:

- if `node.left != null` and `node.left.right == node`, then `node` is in the leaf ring
- similarly if `node.right != null` and `node.right.left == node`

Once we detect that a node is in the leaf cycle, we stop recursion there and treat it as a leaf for height purposes.

This avoids using a visited set.

---

## Algorithm

1. DFS from root with depth
2. If current node belongs to the leaf cycle, return current depth
3. Otherwise recurse into children
4. Return the maximum depth

---

## Java code

```java
class Solution {
    public int heightOfTree(TreeNode root) {
        return dfs(root, 0);
    }

    private int dfs(TreeNode node, int depth) {
        if (node == null) {
            return depth - 1;
        }

        if (isSpecialLeaf(node)) {
            return depth;
        }

        int left = depth;
        int right = depth;

        if (node.left != null) {
            left = dfs(node.left, depth + 1);
        }

        if (node.right != null) {
            right = dfs(node.right, depth + 1);
        }

        return Math.max(left, right);
    }

    private boolean isSpecialLeaf(TreeNode node) {
        boolean leftCycle = node.left != null && node.left.right == node;
        boolean rightCycle = node.right != null && node.right.left == node;

        return leftCycle || rightCycle || (node.left == null && node.right == null);
    }
}
```

---

## Important note

This approach depends on the structural property of the special tree and is more specialized than Approach 1.

It is elegant when the cycle pattern is well understood, but the visited-set solutions are more robust and easier to reason about.

---

## Complexity

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(h)
```

where `h` is recursion depth, worst case `O(n)`.

---

# Approach 4: Build only the original tree height by ignoring cyclic leaf links (conceptual)

## Intuition

Conceptually, what we really want is the height of the **original tree before leaf-cycle augmentation**.

If we could identify which child pointers are "real downward tree edges" and which are just "special leaf links", we could run normal height DFS.

This is essentially what Approach 3 tries to do locally.

However, for implementation simplicity and correctness, Approaches 1 and 2 are usually better.

So this approach is more of a conceptual reformulation:

- the special structure is a tree + one cycle over leaves
- we want the tree height, not graph diameter or graph depth through the cycle

---

# Comparison of approaches

## Approach 1: DFS + visited

- clean
- robust
- easy to justify
- excellent interview solution

## Approach 2: BFS + visited

- equally correct
- natural if you think of height as levels
- easy to code

## Approach 3: DFS + special-leaf detection

- more specialized
- avoids visited set
- trickier to reason about

---

# Recommended solution

The best overall solution is **Approach 1 (DFS with visited set)**.

It is simple, safe, and linear.

---

# Final recommended solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int heightOfTree(TreeNode root) {
        Set<TreeNode> visited = new HashSet<>();
        return dfs(root, visited, 0);
    }

    private int dfs(TreeNode node, Set<TreeNode> visited, int depth) {
        if (node == null) {
            return depth - 1;
        }

        visited.add(node);

        int best = depth;

        if (node.left != null && !visited.contains(node.left)) {
            best = Math.max(best, dfs(node.left, visited, depth + 1));
        }

        if (node.right != null && !visited.contains(node.right)) {
            best = Math.max(best, dfs(node.right, visited, depth + 1));
        }

        return best;
    }
}
```

## Complexity

- **Time:** `O(n)`
- **Space:** `O(n)`

This is the cleanest and most reliable solution for the problem.
