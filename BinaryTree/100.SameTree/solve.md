# 100. Same Tree — Detailed Approaches

## Approach 1: Recursion

### Intuition

The simplest strategy is to use **recursion**.

At every step we compare the current nodes from both trees.

Steps:

1. If both nodes are `null`, they are identical.
2. If one node is `null` and the other is not, the trees are different.
3. If node values differ, the trees are different.
4. Recursively compare the **left subtrees** and **right subtrees**.

If all checks pass, the trees are the same.

---

### Java Implementation

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        // p and q are both null
        if (p == null && q == null) return true;

        // one of p and q is null
        if (p == null || q == null) return false;

        // values are different
        if (p.val != q.val) return false;

        return isSameTree(p.right, q.right) &&
               isSameTree(p.left, q.left);
    }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(N)
```

Where `N` is the number of nodes in the tree.
Each node is visited exactly once.

**Space Complexity**

```
O(N)
```

In the worst case (completely unbalanced tree), the recursion stack can grow to `N`.

---

# Approach 2: Iteration (BFS)

### Intuition

Instead of recursion, we can simulate traversal using **queues (deques)**.

Process nodes level by level:

1. Start from both roots.
2. Pop nodes from the queues.
3. Check:

```
p == null && q == null
p.val == q.val
```

4. Push child nodes if valid.

If at any point a mismatch occurs, return `false`.

---

### Java Implementation

```java
class Solution {

    public boolean check(TreeNode p, TreeNode q) {
        // both null
        if (p == null && q == null) return true;

        // one null
        if (p == null || q == null) return false;

        // values differ
        if (p.val != q.val) return false;

        return true;
    }

    public boolean isSameTree(TreeNode p, TreeNode q) {

        if (p == null && q == null) return true;
        if (!check(p, q)) return false;

        ArrayDeque<TreeNode> deqP = new ArrayDeque<>();
        ArrayDeque<TreeNode> deqQ = new ArrayDeque<>();

        deqP.addLast(p);
        deqQ.addLast(q);

        while (!deqP.isEmpty()) {

            p = deqP.removeFirst();
            q = deqQ.removeFirst();

            if (!check(p, q)) return false;

            if (p != null) {

                if (!check(p.left, q.left)) return false;
                if (p.left != null) {
                    deqP.addLast(p.left);
                    deqQ.addLast(q.left);
                }

                if (!check(p.right, q.right)) return false;
                if (p.right != null) {
                    deqP.addLast(p.right);
                    deqQ.addLast(q.right);
                }
            }
        }

        return true;
    }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(N)
```

Every node is visited exactly once.

**Space Complexity**

```
O(N)
```

In the worst case (perfect binary tree), the queue may store an entire level of nodes.
