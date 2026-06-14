# Maximum Width of Binary Tree (LeetCode 662)

## Problem Summary

Given the root of a binary tree, return the maximum width among all levels.

The width of a level is calculated using the positions that nodes would occupy in a complete binary tree. This means null gaps between the leftmost and rightmost non-null nodes are counted.

---

# Key Insight

Assign each node an index as if the tree were stored in an array representation of a complete binary tree.

For a node at index `i`:

- Left child → `2 * i`
- Right child → `2 * i + 1`

For every level:

```text
width = rightmostIndex - leftmostIndex + 1
```

The answer is the maximum width across all levels.

---

# Solution 1: BFS (Level Order Traversal)

## Idea

Traverse level by level.

For each level:

1. Record the first node index.
2. Record the last node index.
3. Width = last - first + 1.
4. Update maximum width.

To avoid overflow for deep trees:

Normalize indices at each level by subtracting the first index.

---

## Complexity

- Time: O(n)
- Space: O(n)

---

## Java Code

```java
class Solution {

    private static class Pair {
        TreeNode node;
        long index;

        Pair(TreeNode node, long index) {
            this.node = node;
            this.index = index;
        }
    }

    public int widthOfBinaryTree(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<Pair> queue = new LinkedList<>();
        queue.offer(new Pair(root, 0));

        long maxWidth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            long minIndex = queue.peek().index;

            long first = 0;
            long last = 0;

            for (int i = 0; i < size; i++) {
                Pair current = queue.poll();

                long normalizedIndex = current.index - minIndex;

                if (i == 0) {
                    first = normalizedIndex;
                }

                if (i == size - 1) {
                    last = normalizedIndex;
                }

                if (current.node.left != null) {
                    queue.offer(
                        new Pair(current.node.left,
                                 2 * normalizedIndex + 1)
                    );
                }

                if (current.node.right != null) {
                    queue.offer(
                        new Pair(current.node.right,
                                 2 * normalizedIndex + 2)
                    );
                }
            }

            maxWidth = Math.max(maxWidth, last - first + 1);
        }

        return (int) maxWidth;
    }
}
```

---

# Solution 2: DFS with Index Tracking

## Idea

Perform DFS while tracking:

- depth
- index

For each depth, store the first index encountered.

Whenever we visit another node at the same depth:

```text
width = currentIndex - firstIndexAtDepth + 1
```

Keep the maximum width.

---

## Complexity

- Time: O(n)
- Space: O(h)

Where `h` is tree height.

---

## Java Code

```java
class Solution {

    private long maxWidth = 0;

    public int widthOfBinaryTree(TreeNode root) {
        List<Long> firstIndex = new ArrayList<>();
        dfs(root, 0, 0L, firstIndex);
        return (int) maxWidth;
    }

    private void dfs(
            TreeNode node,
            int depth,
            long index,
            List<Long> firstIndex) {

        if (node == null) {
            return;
        }

        if (depth == firstIndex.size()) {
            firstIndex.add(index);
        }

        long width =
                index - firstIndex.get(depth) + 1;

        maxWidth = Math.max(maxWidth, width);

        dfs(node.left,
            depth + 1,
            2 * index + 1,
            firstIndex);

        dfs(node.right,
            depth + 1,
            2 * index + 2,
            firstIndex);
    }
}
```

---

# Solution 3: BFS Without Explicit Pair Class

Some interviewers prefer fewer helper classes.

Use a queue for nodes and another queue for indices.

---

## Complexity

- Time: O(n)
- Space: O(n)

---

## Java Code

```java
class Solution {

    public int widthOfBinaryTree(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Long> indexQueue = new LinkedList<>();

        nodeQueue.offer(root);
        indexQueue.offer(0L);

        long answer = 0;

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();

            long first = indexQueue.peek();
            long last = first;

            for (int i = 0; i < size; i++) {
                TreeNode node = nodeQueue.poll();
                long index = indexQueue.poll();

                last = index;

                if (node.left != null) {
                    nodeQueue.offer(node.left);
                    indexQueue.offer(2 * index + 1);
                }

                if (node.right != null) {
                    nodeQueue.offer(node.right);
                    indexQueue.offer(2 * index + 2);
                }
            }

            answer = Math.max(answer, last - first + 1);
        }

        return (int) answer;
    }
}
```

---

# Example

```text
        1
      /   \
     3     2
    / \     \
   5   3     9
```

Indices:

```text
          0
       /     \
      1       2
    /   \      \
   3     4      6
```

Level widths:

```text
Level 0 => 1
Level 1 => 2
Level 2 => 6 - 3 + 1 = 4
```

Answer:

```text
4
```

---

# Interview Discussion

## Why do we assign indices?

Because the problem counts the null gaps between nodes.

A normal level-order traversal only counts existing nodes and would miss those gaps.

---

## Why normalize indices in BFS?

For a skewed tree of height 3000+, indices can become extremely large.

Normalizing:

```java
normalized = index - firstIndexOfLevel;
```

keeps numbers small while preserving widths.

---

## Which solution should be used in interviews?

**Recommended:** BFS with normalization (Solution 1)

Reasons:

- Most intuitive.
- Easy to explain.
- Avoids overflow.
- Commonly accepted interview solution.

DFS is elegant and shorter, but BFS is generally considered the safest interview answer.
