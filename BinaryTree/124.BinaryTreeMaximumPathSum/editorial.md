# 124. Binary Tree Maximum Path Sum — Java Solutions

## Problem Summary

Given the root of a binary tree, return the **maximum path sum** of any non-empty path.
A path is any sequence of nodes connected by edges where each node appears at most once.
The path does **not** need to pass through the root.

---

## Core Insight

At any node, a path can:

1. Go **down-left only**
2. Go **down-right only**
3. Pass **through the node** connecting left and right subtrees
4. Be **just the node itself** (if both children contribute negative sums)

When we recurse, we can only **extend one side** upward to the parent.
But locally at each node, we can **combine both sides** to evaluate a candidate maximum.

---

## Approach 1 — DFS with Global Max (Optimal)

### Explanation

Perform a single post-order DFS traversal. At every node:

- Compute the **max gain from the left subtree** (clamp to 0 if negative — we simply don't include it).
- Compute the **max gain from the right subtree** (same clamping logic).
- **Update global max** with `node.val + leftGain + rightGain` — this is the best path passing through this node as the "bend".
- **Return** `node.val + max(leftGain, rightGain)` — the best single-arm extension the parent can use.

This correctly separates two concerns:

- **Local evaluation** (can the path bend here?): uses both arms.
- **Upward contribution** (what can we offer the parent?): uses only the better arm.

### Complexity

|       |                                                                   |
| ----- | ----------------------------------------------------------------- |
| Time  | O(n) — each node visited once                                     |
| Space | O(h) — recursion stack; O(log n) balanced, O(n) worst-case skewed |

### Code

```java
class Solution {
    private int globalMax;

    public int maxPathSum(TreeNode root) {
        globalMax = Integer.MIN_VALUE;
        dfs(root);
        return globalMax;
    }

    // Returns the maximum gain this subtree can contribute to its parent (single arm)
    private int dfs(TreeNode node) {
        if (node == null) return 0;

        // Clamp to 0: if a subtree is negative, we skip it
        int leftGain  = Math.max(0, dfs(node.left));
        int rightGain = Math.max(0, dfs(node.right));

        // Best path that bends at this node (cannot be extended further up)
        int localPath = node.val + leftGain + rightGain;
        globalMax = Math.max(globalMax, localPath);

        // Return the best single-arm the parent can extend
        return node.val + Math.max(leftGain, rightGain);
    }
}
```

### Trace — Example 2: `[-10, 9, 20, null, null, 15, 7]`

```
Tree structure:
        -10
       /    \
      9      20
            /  \
           15    7

DFS call order (post-order):
  dfs(9)  → left=0, right=0 → localPath=9,  globalMax=9,  return 9
  dfs(15) → left=0, right=0 → localPath=15, globalMax=15, return 15
  dfs(7)  → left=0, right=0 → localPath=7,  globalMax=15, return 7
  dfs(20) → leftGain=15, rightGain=7
           → localPath = 20+15+7 = 42, globalMax=42, return 20+15=35
  dfs(-10)→ leftGain=max(0,9)=9, rightGain=max(0,35)=35
           → localPath = -10+9+35 = 34, globalMax still 42

Answer: 42 ✓
```

---

## Approach 2 — DFS Returning an int[] (No Instance Variable)

### Explanation

Same algorithm as Approach 1, but avoids a mutable instance field by returning a two-element array from the recursive helper:

- `result[0]` — the **global max** seen so far across the entire subtree.
- `result[1]` — the **best single-arm** contribution to offer the parent.

This is a purely functional style: no side effects, no shared state — easier to reason about in concurrent or functional contexts.

### Complexity

|       |                                                       |
| ----- | ----------------------------------------------------- |
| Time  | O(n)                                                  |
| Space | O(h) stack + O(h) array allocations on the call stack |

> Slightly higher constant factor than Approach 1 due to array allocation at every node.

### Code

```java
class Solution {
    public int maxPathSum(TreeNode root) {
        return dfs(root)[0];
    }

    // Returns int[] { globalMax in subtree, best single-arm from this node }
    private int[] dfs(TreeNode node) {
        if (node == null) return new int[]{Integer.MIN_VALUE, 0};

        int[] left  = dfs(node.left);
        int[] right = dfs(node.right);

        int leftGain  = Math.max(0, left[1]);
        int rightGain = Math.max(0, right[1]);

        int localPath = node.val + leftGain + rightGain;
        int subtreeMax = Math.max(localPath, Math.max(left[0], right[0]));
        int armContribution = node.val + Math.max(leftGain, rightGain);

        return new int[]{subtreeMax, armContribution};
    }
}
```

---

## Approach 3 — Iterative Post-Order (Explicit Stack)

### Explanation

Converts the recursive DFS into an **iterative post-order traversal** using an explicit stack.

Key idea: process each node only after both its children have been processed.
Use a `Map<TreeNode, Integer>` to store the **best single-arm gain** for each node once it's computed.

Steps:

1. Push nodes in a two-pass manner (push node → push right child → push left child so left is processed first).
2. On pop, look up pre-computed child gains from the map, apply the same logic as Approach 1, and update `globalMax`.
3. Store the arm contribution back in the map keyed by this node, so its parent can retrieve it.

This avoids any recursion-depth risk for extremely deep (skewed) trees.

### Complexity

|       |                                 |
| ----- | ------------------------------- |
| Time  | O(n)                            |
| Space | O(n) — explicit stack + HashMap |

### Code

```java
import java.util.*;

class Solution {
    public int maxPathSum(TreeNode root) {
        int globalMax = Integer.MIN_VALUE;
        Map<TreeNode, Integer> armGain = new HashMap<>();
        Deque<TreeNode> stack = new ArrayDeque<>();

        // Build post-order sequence into a list
        List<TreeNode> order = new ArrayList<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            order.add(node);
            if (node.left  != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }

        // Process in reverse (post-order: children before parents)
        Collections.reverse(order);

        for (TreeNode node : order) {
            int leftGain  = Math.max(0, armGain.getOrDefault(node.left,  0));
            int rightGain = Math.max(0, armGain.getOrDefault(node.right, 0));

            int localPath = node.val + leftGain + rightGain;
            globalMax = Math.max(globalMax, localPath);

            armGain.put(node, node.val + Math.max(leftGain, rightGain));
        }

        return globalMax;
    }
}
```

---

## Approach 4 — Morris Traversal (O(1) Extra Space)

### Explanation

Morris traversal threads the tree to allow traversal **without a stack or recursion**, achieving true O(1) auxiliary space (beyond the output variable and a few pointers).

However, for this problem the path sum computation requires **post-order** evaluation (children must be known before the parent), so a naive single-pass Morris traversal does not directly apply.

A practical workaround: use Morris in-order traversal to enumerate nodes, combined with the HashMap approach (still O(n) space for the map). **Pure O(1) space** for this specific problem is theoretically possible with a two-pass Morris reverse post-order but is rarely asked in interviews and adds significant complexity.

Below is the **Morris + HashMap** hybrid — demonstrating the traversal pattern with practical utility:

```java
import java.util.*;

class Solution {
    public int maxPathSum(TreeNode root) {
        // Collect nodes in post-order using Morris-like reverse pre-order trick
        List<TreeNode> postOrder = new ArrayList<>();
        TreeNode curr = root;
        Deque<TreeNode> stack = new ArrayDeque<>();

        // This is the iterative post-order approach expressed via two stacks
        // (kept here for clarity; reduces to Approach 3 under the hood)
        Deque<TreeNode> s1 = new ArrayDeque<>(), s2 = new ArrayDeque<>();
        s1.push(root);
        while (!s1.isEmpty()) {
            TreeNode node = s1.pop();
            s2.push(node);
            if (node.left  != null) s1.push(node.left);
            if (node.right != null) s1.push(node.right);
        }

        int globalMax = Integer.MIN_VALUE;
        Map<TreeNode, Integer> gain = new HashMap<>();
        while (!s2.isEmpty()) {
            TreeNode node = s2.pop();
            int l = Math.max(0, gain.getOrDefault(node.left,  0));
            int r = Math.max(0, gain.getOrDefault(node.right, 0));
            globalMax = Math.max(globalMax, node.val + l + r);
            gain.put(node, node.val + Math.max(l, r));
        }
        return globalMax;
    }
}
```

> **Note:** True O(1)-space Morris post-order for path-sum problems is an advanced topic. In practice, Approach 3 (iterative with HashMap) is the go-to iterative solution.

---

## Approach Comparison

| Approach                  | Time | Space | Style     | Notes                                     |
| ------------------------- | ---- | ----- | --------- | ----------------------------------------- |
| 1 — DFS + Global Variable | O(n) | O(h)  | Recursive | Simplest, most idiomatic                  |
| 2 — DFS + int[] Return    | O(n) | O(h)  | Recursive | No side effects; functional style         |
| 3 — Iterative Post-Order  | O(n) | O(n)  | Iterative | Safe for very deep trees                  |
| 4 — Morris Hybrid         | O(n) | O(n)  | Iterative | Demonstrates traversal; no space win here |

**Recommended:** Approach 1 for interviews — clean, minimal, easy to explain.
Use Approach 3 if the tree could be deeply skewed (n up to 10^5) and stack overflow is a concern.

---

## TreeNode Definition (Reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```

---

## Edge Cases to Know

| Case                                | Behaviour                                     |
| ----------------------------------- | --------------------------------------------- |
| Single node                         | Returns that node's value (even if negative)  |
| All negative values                 | Returns the least-negative single node        |
| Skewed tree (linked list shape)     | Correct; Approach 3/4 avoids stack overflow   |
| Path entirely in left/right subtree | `globalMax` captures it via child `dfs` calls |
