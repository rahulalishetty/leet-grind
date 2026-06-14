# 653. Two Sum IV - Input is a BST

## Problem Statement

Given the `root` of a **Binary Search Tree (BST)** and an integer `k`, return `true` if there exist **two distinct nodes** such that:

```
node1.val + node2.val = k
```

---

## Core Idea

For any node with value `x`, the complement needed to reach `k` is:

```
complement = k - x
```

If we have already seen `complement` somewhere in the tree, we return `true`.

---

## Approach 1: DFS + HashSet

### Intuition

Traverse the tree using recursive DFS. At each node, check if `k - node.val` already exists in a `HashSet`. If yes, a valid pair is found. Otherwise, add the current value to the set and continue.

This approach works on **any binary tree** — it does not rely on the BST property.

### Java Code

```java
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public boolean findTarget(TreeNode root, int k) {
        Set<Integer> set = new HashSet<>();
        return dfs(root, k, set);
    }

    private boolean dfs(TreeNode node, int k, Set<Integer> set) {
        if (node == null) return false;

        // Check if the complement has been seen before
        if (set.contains(k - node.val)) return true;

        // Record current value
        set.add(node.val);

        // Recurse into left and right subtrees
        return dfs(node.left, k, set) || dfs(node.right, k, set);
    }
}
```

### Step-by-Step Walkthrough

1. **Base case** — if the node is `null`, return `false`.
2. **Complement check** — if `k - node.val` is in the set, two valid nodes have been found.
3. **Record the value** — add `node.val` to the set for future lookups.
4. **Recurse** — search left and right subtrees, returning `true` if either succeeds.

### Complexity

|           | Complexity                                                                          |
| --------- | ----------------------------------------------------------------------------------- |
| **Time**  | O(n) — each node visited once                                                       |
| **Space** | O(n) — set stores up to n values; recursion stack up to O(h) where h is tree height |

---

## Approach 2: BFS + HashSet

### Intuition

Same complement-check logic as Approach 1, but uses **level-order (BFS) traversal** with a queue instead of recursion. This avoids potential stack overflow on very deep trees.

### Java Code

```java
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Solution {
    public boolean findTarget(TreeNode root, int k) {
        Set<Integer> set = new HashSet<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) continue;

            // Check complement
            if (set.contains(k - node.val)) return true;

            // Record current value
            set.add(node.val);

            // Enqueue children
            queue.add(node.left);
            queue.add(node.right);
        }

        return false;
    }
}
```

### Step-by-Step Walkthrough

1. **Initialize** a queue with the root and an empty set.
2. **Poll** a node from the queue. Skip if `null`.
3. **Complement check** — if `k - node.val` is in the set, return `true`.
4. **Record the value** — add `node.val` to the set.
5. **Enqueue children** — add left and right children to the queue.
6. **Repeat** until the queue is empty, then return `false`.

### Complexity

|           | Complexity                                                                               |
| --------- | ---------------------------------------------------------------------------------------- |
| **Time**  | O(n) — each node processed once                                                          |
| **Space** | O(n) — set stores up to n values; queue holds up to O(w) nodes where w is max tree width |

---

## Approach 3: In-order Traversal + Two Pointers

### Intuition

A BST's **in-order traversal produces a sorted array**. Once sorted, the classic two-pointer technique can find any pair summing to `k` in O(n) time.

- Place pointer `l` at the start (smallest value).
- Place pointer `r` at the end (largest value).
- Move pointers inward based on whether the current sum is too small or too large.

### Java Code

```java
import java.util.ArrayList;
import java.util.List;

public class Solution {
    public boolean findTarget(TreeNode root, int k) {
        List<Integer> sorted = new ArrayList<>();
        inorder(root, sorted);

        int l = 0, r = sorted.size() - 1;

        while (l < r) {
            int sum = sorted.get(l) + sorted.get(r);
            if (sum == k)  return true;
            if (sum < k)   l++;   // need a larger sum → move left pointer right
            else           r--;   // need a smaller sum → move right pointer left
        }

        return false;
    }

    private void inorder(TreeNode node, List<Integer> list) {
        if (node == null) return;
        inorder(node.left, list);    // visit left
        list.add(node.val);          // record current
        inorder(node.right, list);   // visit right
    }
}
```

### Step-by-Step Walkthrough

1. **In-order traversal** — fills `sorted` with BST values in ascending order.
2. **Initialize pointers** — `l = 0` (smallest), `r = size - 1` (largest).
3. **Compute sum** — `sum = sorted[l] + sorted[r]`.
4. **Compare to k**:
   - `sum == k` → found a valid pair, return `true`.
   - `sum < k` → sum is too small, increment `l` to try a larger value.
   - `sum > k` → sum is too large, decrement `r` to try a smaller value.
5. **Termination** — if `l >= r`, no valid pair exists; return `false`.

### Why Two Pointers Work

Because the list is sorted, there is no benefit in moving both pointers in the same direction. Every move either increases or decreases the sum in a controlled way — no pairs are skipped.

### Complexity

|           | Complexity                                                     |
| --------- | -------------------------------------------------------------- |
| **Time**  | O(n) — O(n) for in-order traversal + O(n) for two-pointer scan |
| **Space** | O(n) — list stores all n node values                           |

---

## Comparison Table

| Approach                | Time | Space | Uses BST Property | Traversal          |
| ----------------------- | ---- | ----- | ----------------- | ------------------ |
| DFS + HashSet           | O(n) | O(n)  | No                | Recursive DFS      |
| BFS + HashSet           | O(n) | O(n)  | No                | Iterative BFS      |
| In-order + Two Pointers | O(n) | O(n)  | **Yes**           | Recursive in-order |

---

## Which Approach to Use?

| Goal                               | Best Approach               |
| ---------------------------------- | --------------------------- |
| Simplest, most direct solution     | **DFS + HashSet**           |
| Avoid recursion / deep call stacks | **BFS + HashSet**           |
| Leverage BST structure explicitly  | **In-order + Two Pointers** |

---

## Key Takeaway

All three approaches run in **O(n) time and O(n) space**. The real difference is conceptual:

- Approaches 1 and 2 treat the BST as a **generic binary tree** and rely on fast complement lookup.
- Approach 3 exploits the **sorted order guarantee** of a BST and solves the reduced problem with two pointers.

For interviews, Approach 1 (DFS + HashSet) is usually the fastest to write, while Approach 3 (In-order + Two Pointers) best demonstrates BST-specific thinking.
