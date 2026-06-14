# 102. Binary Tree Level Order Traversal

**Difficulty:** Medium
**Topic:** Tree, BFS, Queue

---

## Problem Summary

Given the `root` of a binary tree, return the **level order traversal** of its node values — left to right, level by level.

```
Input:  root = [3,9,20,null,null,15,7]
Output: [[3],[9,20],[15,7]]
```

---

## Approach 1: BFS with Queue (Iterative) ✅ Most Common

### Intuition

Level order traversal is **Breadth-First Search (BFS)** by definition. We process nodes level by level using a **Queue**. The key trick is to snapshot the queue's size at the start of each level — that tells us exactly how many nodes belong to the current level.

### Algorithm

1. Add `root` to the queue (if not null).
2. While the queue is not empty:
   - Record `size = queue.size()` (nodes at this level).
   - Loop `size` times: poll a node, add its value to the current level list, enqueue its children.
   - Add the current level list to the result.

### Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();           // number of nodes at current level
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(level);
        }
        return result;
    }
}
```

### Complexity

|           |                                                                               |
| --------- | ----------------------------------------------------------------------------- |
| **Time**  | O(n) — every node is visited once                                             |
| **Space** | O(n) — queue holds at most one full level (up to n/2 nodes at the last level) |

---

## Approach 2: DFS / Recursion (Pre-order)

### Intuition

We can also solve this with **Depth-First Search**. Instead of a queue, we carry the current **depth** as a parameter. If the result list doesn't yet have a sublist for this depth, we create one. Then we append the node's value to `result[depth]`.

### Algorithm

1. Define a recursive helper `dfs(node, depth)`.
2. If `result.size() == depth`, add a new empty list (new level discovered).
3. Add `node.val` to `result.get(depth)`.
4. Recurse on left child with `depth + 1`, then right child.

### Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    private void dfs(TreeNode node, int depth, List<List<Integer>> result) {
        if (node == null) return;

        // First time visiting this depth — create a new level list
        if (result.size() == depth) {
            result.add(new ArrayList<>());
        }

        result.get(depth).add(node.val);

        dfs(node.left,  depth + 1, result);
        dfs(node.right, depth + 1, result);
    }
}
```

### Complexity

|           |                                                                                                                       |
| --------- | --------------------------------------------------------------------------------------------------------------------- |
| **Time**  | O(n) — every node is visited once                                                                                     |
| **Space** | O(h) — recursive call stack, where h is the height of the tree (O(log n) best case, O(n) worst case for skewed trees) |

---

## Approach 3: BFS with Two Queues

### Intuition

Instead of tracking `queue.size()`, we use **two queues** — one for the current level and one for the next. When the current queue is empty, we swap them and move on to the next level.

### Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> current = new LinkedList<>();
        Queue<TreeNode> next    = new LinkedList<>();
        current.offer(root);

        while (!current.isEmpty()) {
            List<Integer> level = new ArrayList<>();

            while (!current.isEmpty()) {
                TreeNode node = current.poll();
                level.add(node.val);

                if (node.left  != null) next.offer(node.left);
                if (node.right != null) next.offer(node.right);
            }

            result.add(level);

            // Swap queues
            Queue<TreeNode> temp = current;
            current = next;
            next = temp;
        }
        return result;
    }
}
```

### Complexity

|           |                                                           |
| --------- | --------------------------------------------------------- |
| **Time**  | O(n)                                                      |
| **Space** | O(n) — two queues, but combined they hold at most n nodes |

---

## Approach 4: BFS with Null Sentinel

### Intuition

Insert a **`null` marker** into the queue to signal the end of each level. When we encounter `null`, we know the current level is complete and start a new one.

> ⚠️ Be careful: only add a new `null` when the queue is still non-empty (there is a next level to process). Otherwise, you'd create an infinite loop.

### Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        queue.offer(null);          // sentinel marking end of level 0

        List<Integer> level = new ArrayList<>();

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node == null) {
                result.add(level);
                level = new ArrayList<>();
                if (!queue.isEmpty()) {
                    queue.offer(null);  // sentinel for the next level
                }
            } else {
                level.add(node.val);
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return result;
    }
}
```

### Complexity

|           |      |
| --------- | ---- |
| **Time**  | O(n) |
| **Space** | O(n) |

---

## Approach 5: Iterative with ArrayDeque (Optimized BFS)

### Intuition

`ArrayDeque` is faster than `LinkedList` for queue operations in Java because it avoids the overhead of node allocation. This is a performance-optimized version of Approach 1.

### Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>(size); // pre-sized for efficiency

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(level);
        }
        return result;
    }
}
```

### Complexity

|           |      |
| --------- | ---- |
| **Time**  | O(n) |
| **Space** | O(n) |

> **Why `ArrayDeque` over `LinkedList`?**
> `LinkedList` allocates a wrapper `Node` object for every element (extra GC pressure). `ArrayDeque` uses a resizable circular array — faster in practice.

---

## Comparison Table

| Approach            | Strategy      | Time | Space | Notes                         |
| ------------------- | ------------- | ---- | ----- | ----------------------------- |
| 1. BFS + Queue Size | Iterative BFS | O(n) | O(n)  | Clean, most readable          |
| 2. DFS Recursive    | Pre-order DFS | O(n) | O(h)  | Less memory on balanced trees |
| 3. Two Queues       | Iterative BFS | O(n) | O(n)  | Avoids size snapshot          |
| 4. Null Sentinel    | Iterative BFS | O(n) | O(n)  | Clever but tricky             |
| 5. ArrayDeque       | Iterative BFS | O(n) | O(n)  | Best practical performance    |

---

## TreeNode Definition (for reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
```

---

## Recommended Approach

**For interviews:** Use **Approach 1 (BFS with queue size)** — it's clean, easy to explain, and directly maps to the concept of level-by-level traversal.

**For recursive thinkers:** **Approach 2 (DFS)** is elegant and uses less stack space on balanced trees.

**For performance-critical code:** **Approach 5 (ArrayDeque)** is the most efficient in practice.
