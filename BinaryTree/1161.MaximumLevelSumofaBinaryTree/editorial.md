from textwrap import dedent
import pypandoc

md = dedent("""

# 1161. Maximum Level Sum of a Binary Tree — Approaches

## Overview

We are given the root of a binary tree.

Our task is to return the **smallest level `x` such that the sum of all node values at level `x` is maximal**.

Two main traversal strategies can be used:

- **Breadth First Search (BFS)** — level-by-level traversal.
- **Depth First Search (DFS)** — explore branches deeply and track level sums.

---

# Approach 1: Breadth First Search (BFS)

## Intuition

We need the **sum of node values for each level**.

Since BFS naturally traverses a tree **level-by-level**, it is a perfect fit.

Key idea:

1. Process nodes **level by level** using a queue.
2. Compute the **sum of nodes at each level**.
3. Track the **maximum sum encountered**.
4. Record the **smallest level index** producing that maximum.

---

## Algorithm

1. Initialize:

```
maxSum = -∞
ans = 0
level = 0
```

2. Add the root node to a queue.

3. While the queue is not empty:

- Increment level.
- Compute `sumAtCurrentLevel`.
- Process exactly `queue.size()` nodes.
- Add their children to the queue.

4. If:

```
sumAtCurrentLevel > maxSum
```

update:

```
maxSum = sumAtCurrentLevel
ans = level
```

5. Return `ans`.

---

## Implementation

```java
class Solution {
    public int maxLevelSum(TreeNode root) {
        int maxSum = Integer.MIN_VALUE;
        int ans = 0, level = 0;

        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);

        while (!q.isEmpty()) {

            level++;
            int sumAtCurrentLevel = 0;

            for (int sz = q.size(); sz > 0; --sz) {
                TreeNode node = q.poll();
                sumAtCurrentLevel += node.val;

                if (node.left != null)
                    q.offer(node.left);

                if (node.right != null)
                    q.offer(node.right);
            }

            if (maxSum < sumAtCurrentLevel) {
                maxSum = sumAtCurrentLevel;
                ans = level;
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let **n = number of nodes**.

### Time Complexity

```
O(n)
```

Each node is pushed and popped from the queue exactly once.

---

### Space Complexity

```
O(n)
```

In a complete binary tree, the queue can contain up to **(n+1)/2 nodes**.

---

# Approach 2: Depth First Search (DFS)

## Intuition

Instead of processing level-by-level, DFS explores **branch by branch**.

While traversing, we pass the **level of the current node** and accumulate sums in a list.

```
sumOfNodesAtLevel[level] += node.val
```

If a level is encountered for the first time, we create a new entry in the list.

After traversal:

- Iterate through the list
- Return the level with the **maximum sum**.

---

## Algorithm

1. Maintain a list:

```
sumOfNodesAtLevel[i] → sum of nodes at level i
```

2. Perform DFS:

```
dfs(node, level)
```

3. For each node:

- If level is new → append value
- Otherwise → add value to existing level sum

4. Traverse left and right children.

5. After traversal, find the index with the **maximum sum**.

6. Return `index + 1` (levels start at 1).

---

## Implementation

```java
class Solution {

    public void dfs(TreeNode node, int level, List<Integer> sumOfNodesAtLevel) {
        if (node == null) return;

        if (sumOfNodesAtLevel.size() == level)
            sumOfNodesAtLevel.add(node.val);
        else
            sumOfNodesAtLevel.set(level, sumOfNodesAtLevel.get(level) + node.val);

        dfs(node.left, level + 1, sumOfNodesAtLevel);
        dfs(node.right, level + 1, sumOfNodesAtLevel);
    }

    public int maxLevelSum(TreeNode root) {

        List<Integer> sumOfNodesAtLevel = new ArrayList<>();
        dfs(root, 0, sumOfNodesAtLevel);

        int maxSum = Integer.MIN_VALUE;
        int ans = 0;

        for (int i = 0; i < sumOfNodesAtLevel.size(); i++) {
            if (maxSum < sumOfNodesAtLevel.get(i)) {
                maxSum = sumOfNodesAtLevel.get(i);
                ans = i + 1;
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let **n = number of nodes**.

### Time Complexity

```
O(n)
```

- DFS visits each node once.
- Computing level sums also takes O(n).

---

### Space Complexity

```
O(n)
```

Space used for:

- recursion stack
- level sum list

Worst case occurs when the tree is **skewed**.

---

# Summary

| Approach | Traversal   | Key Idea                          | Time | Space |
| -------- | ----------- | --------------------------------- | ---- | ----- |
| BFS      | Level-order | Compute sum at each level         | O(n) | O(n)  |
| DFS      | Recursive   | Track level sums during traversal | O(n) | O(n)  |

---

## Key Insight

The problem essentially reduces to:

> **Compute the sum of nodes at each level and return the smallest level with the maximum sum.**
> """)

path = "/mnt/data/1161_maximum_level_sum_binary_tree_approaches.md"

pypandoc.convert_text(md, "md", format="md", outputfile=path, extra_args=["--standalone"])

path
