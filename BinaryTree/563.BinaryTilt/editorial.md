# Binary Tree Tilt — Java Solutions

## Problem Summary

The **tilt** of a node = `|sum(left subtree) - sum(right subtree)|`

Return the **sum of tilts** of every node in the tree.

---

## Approach 1: Single DFS with Running Total (Optimal)

### Idea

Do one post-order DFS. Each recursive call returns the **subtree sum** rooted at the current node. As a side effect, accumulate the tilt of every node into a single `int[]` result holder.

- **Time:** O(n) — every node visited once
- **Space:** O(h) — recursion stack where h is tree height

```java
class Solution {
    public int findTilt(TreeNode root) {
        int[] totalTilt = {0};
        subtreeSum(root, totalTilt);
        return totalTilt[0];
    }

    // Returns the sum of all values in the subtree rooted at `node`.
    // Accumulates each node's tilt into totalTilt[0] along the way.
    private int subtreeSum(TreeNode node, int[] totalTilt) {
        if (node == null) return 0;

        int leftSum  = subtreeSum(node.left,  totalTilt); // sum of left subtree
        int rightSum = subtreeSum(node.right, totalTilt); // sum of right subtree

        totalTilt[0] += Math.abs(leftSum - rightSum);    // tilt of current node

        return node.val + leftSum + rightSum;             // total sum of this subtree
    }
}
```

### Trace — Example 1: `[1, 2, 3]`

```
subtreeSum(2) → leftSum=0, rightSum=0 → tilt=0, returns 2
subtreeSum(3) → leftSum=0, rightSum=0 → tilt=0, returns 3
subtreeSum(1) → leftSum=2, rightSum=3 → tilt=|2-3|=1, returns 6

totalTilt = 0 + 0 + 1 = 1  ✓
```

---

## Approach 2: Two-Pass DFS (Conceptually Clearer)

### Idea

1. **First pass** — compute the subtree sum for every node with a helper.
2. **Second pass** — compute and accumulate the tilt for every node.

Easier to understand but visits each node **twice**.

- **Time:** O(n²) in the worst case (skewed tree), O(n log n) on balanced trees
- **Space:** O(h)

```java
class Solution {
    public int findTilt(TreeNode root) {
        if (root == null) return 0;

        // Tilt of current node
        int currentTilt = Math.abs(getSum(root.left) - getSum(root.right));

        // Recursively add tilts of left and right subtrees
        return currentTilt + findTilt(root.left) + findTilt(root.right);
    }

    // Returns the total sum of all node values in the subtree rooted at node.
    private int getSum(TreeNode node) {
        if (node == null) return 0;
        return node.val + getSum(node.left) + getSum(node.right);
    }
}
```

### Trace — Example 2: `[4, 2, 9, 3, 5, null, 7]`

```
getSum(node 3) = 3,  getSum(node 5) = 5
tilt(node 2)   = |3 - 5| = 2

getSum(null) = 0,  getSum(node 7) = 7
tilt(node 9) = |0 - 7| = 7

getSum(node 2's subtree) = 3+5+2 = 10
getSum(node 9's subtree) = 9+7   = 16
tilt(node 4) = |10 - 16| = 6

Total = 0 + 0 + 0 + 2 + 7 + 6 = 15  ✓
```

---

## Approach 3: Iterative Post-Order with a Stack

### Idea

Avoid recursion entirely by simulating post-order traversal with an explicit stack.

1. Collect nodes in reverse post-order (root → right → left) using a stack, and push them into a list.
2. Iterate that list in reverse (= left → right → root) to process children before parents.
3. Use a `HashMap` to store the subtree sum of every node.

- **Time:** O(n)
- **Space:** O(n) — stack + HashMap

```java
import java.util.*;

class Solution {
    public int findTilt(TreeNode root) {
        if (root == null) return 0;

        Deque<TreeNode> stack   = new ArrayDeque<>();
        List<TreeNode>  order   = new ArrayList<>();
        Map<TreeNode, Integer> sumMap = new HashMap<>();

        // Phase 1: collect nodes in reverse post-order
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            order.add(node);
            if (node.left  != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }

        // Phase 2: process in true post-order (children guaranteed before parent)
        int totalTilt = 0;
        for (int i = order.size() - 1; i >= 0; i--) {
            TreeNode node = order.get(i);

            int leftSum  = sumMap.getOrDefault(node.left,  0);
            int rightSum = sumMap.getOrDefault(node.right, 0);

            totalTilt += Math.abs(leftSum - rightSum);
            sumMap.put(node, node.val + leftSum + rightSum);
        }

        return totalTilt;
    }
}
```

---

## Approach 4: BFS (Level-Order) with Sum Map

### Idea

Use a queue for BFS, collect nodes level by level, then compute subtree sums bottom-up using a reverse-iteration of the collected list — same idea as Approach 3 but with BFS ordering.

- **Time:** O(n)
- **Space:** O(n)

```java
import java.util.*;

class Solution {
    public int findTilt(TreeNode root) {
        if (root == null) return 0;

        Queue<TreeNode>        queue  = new LinkedList<>();
        List<TreeNode>         nodes  = new ArrayList<>();
        Map<TreeNode, Integer> sumMap = new HashMap<>();

        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            nodes.add(node);
            if (node.left  != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        // Process in reverse BFS order = leaves first, root last
        int totalTilt = 0;
        for (int i = nodes.size() - 1; i >= 0; i--) {
            TreeNode node = nodes.get(i);

            int leftSum  = sumMap.getOrDefault(node.left,  0);
            int rightSum = sumMap.getOrDefault(node.right, 0);

            totalTilt += Math.abs(leftSum - rightSum);
            sumMap.put(node, node.val + leftSum + rightSum);
        }

        return totalTilt;
    }
}
```

---

## Comparison

| Approach                      | Time        | Space | Recursion | Notes                                        |
| ----------------------------- | ----------- | ----- | --------- | -------------------------------------------- |
| 1. Single DFS (running total) | O(n)        | O(h)  | Yes       | **Best overall** — minimal space             |
| 2. Two-pass DFS               | O(n²) worst | O(h)  | Yes       | Simplest to read; inefficient on large trees |
| 3. Iterative post-order       | O(n)        | O(n)  | No        | Stack-safe; good for very deep trees         |
| 4. BFS + reverse pass         | O(n)        | O(n)  | No        | Familiar BFS pattern                         |

**Recommendation:** Use **Approach 1** in production/interviews — it's O(n) time and O(h) space with clean, concise code. Use **Approach 2** when readability matters more than efficiency.

---

## TreeNode Definition (for reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```
