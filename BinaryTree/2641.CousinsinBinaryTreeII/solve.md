# 2641. Cousins in Binary Tree II — Approaches

## Approach 1: Two Pass BFS

### Intuition

Cousins are nodes that share the same depth but have different parents.
To compute the cousin sum of a node:

1. Compute the **total sum of all nodes at the same depth**.
2. Subtract the **node and its sibling values** from that total.

This leaves the sum of the node's cousins.

The algorithm therefore works in **two BFS passes**:

1. First pass → compute the sum of nodes at every level.
2. Second pass → update each node using the stored level sums.

---

### Algorithm

1. If the root is null, return root.
2. Perform a BFS traversal and compute the sum of node values at each level.
3. Store those sums in an array `levelSums`.
4. Perform another BFS traversal.
5. For each node:
   - Compute the sum of its children (siblings).
   - Set each child's value to:

```
levelSum[level] - siblingSum
```

6. Root value becomes `0` since it has no cousins.

---

### Java Implementation

```java
class Solution {

    public TreeNode replaceValueInTree(TreeNode root) {
        if (root == null) return root;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.offer(root);
        List<Integer> levelSums = new ArrayList<>();

        // First BFS: calculate level sums
        while (!nodeQueue.isEmpty()) {
            int levelSum = 0;
            int levelSize = nodeQueue.size();

            for (int i = 0; i < levelSize; ++i) {
                TreeNode node = nodeQueue.poll();
                levelSum += node.val;

                if (node.left != null) nodeQueue.offer(node.left);
                if (node.right != null) nodeQueue.offer(node.right);
            }

            levelSums.add(levelSum);
        }

        nodeQueue.offer(root);
        root.val = 0;
        int levelIndex = 1;

        while (!nodeQueue.isEmpty()) {
            int levelSize = nodeQueue.size();

            for (int i = 0; i < levelSize; ++i) {
                TreeNode node = nodeQueue.poll();

                int siblingSum =
                    (node.left != null ? node.left.val : 0) +
                    (node.right != null ? node.right.val : 0);

                if (node.left != null) {
                    node.left.val = levelSums.get(levelIndex) - siblingSum;
                    nodeQueue.offer(node.left);
                }

                if (node.right != null) {
                    node.right.val = levelSums.get(levelIndex) - siblingSum;
                    nodeQueue.offer(node.right);
                }
            }

            levelIndex++;
        }

        return root;
    }
}
```

---

### Complexity Analysis

**Time Complexity:**

```
O(n)
```

Two BFS traversals each visiting all nodes.

**Space Complexity:**

```
O(n)
```

Queue + level sums storage.

---

# Approach 2: Two Pass DFS

## Intuition

This approach mirrors the BFS strategy but uses **DFS traversal**.

1. First DFS → compute level sums.
2. Second DFS → update each node using the cousin formula.

---

### Algorithm

1. Maintain an array `levelSums`.
2. DFS traversal accumulates values for each depth.
3. Second DFS replaces node values:

```
node.val = levelSum[level] - node.val - siblingSum
```

4. Root and its immediate children become `0` because they have no cousins.

---

### Java Implementation

```java
class Solution {

    private int[] levelSums = new int[100000];

    public TreeNode replaceValueInTree(TreeNode root) {
        calculateLevelSum(root, 0);
        replaceValueInTreeInternal(root, 0, 0);
        return root;
    }

    private void calculateLevelSum(TreeNode node, int level) {
        if (node == null) return;

        levelSums[level] += node.val;

        calculateLevelSum(node.left, level + 1);
        calculateLevelSum(node.right, level + 1);
    }

    private void replaceValueInTreeInternal(TreeNode node, int siblingSum, int level) {
        if (node == null) return;

        int leftVal = node.left == null ? 0 : node.left.val;
        int rightVal = node.right == null ? 0 : node.right.val;

        if (level <= 1) {
            node.val = 0;
        } else {
            node.val = levelSums[level] - node.val - siblingSum;
        }

        replaceValueInTreeInternal(node.left, rightVal, level + 1);
        replaceValueInTreeInternal(node.right, leftVal, level + 1);
    }
}
```

---

### Complexity Analysis

**Time Complexity:**

```
O(n)
```

Two DFS traversals visiting each node once.

**Space Complexity:**

```
O(n)
```

Worst-case recursion stack + level array.

---

# Approach 3: Single BFS with Running Sum

## Intuition

Instead of storing all level sums beforehand, we can compute them dynamically during traversal.

Idea:

For each level maintain:

```
currentLevelSum
```

Then update node values using:

```
node.val = currentLevelSum - node.val
```

While preparing children values using sibling sums for the next level.

---

### Algorithm

1. Initialize queue with root.
2. Maintain `currentLevelSum`.
3. For each node:
   - Replace node value using cousin formula.
   - Compute sibling sum of its children.
   - Assign that sibling sum temporarily to children.
4. Track `nextLevelSum` while processing.

---

### Java Implementation

```java
class Solution {

    public TreeNode replaceValueInTree(TreeNode root) {
        if (root == null) return root;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int currentLevelSum = root.val;

        while (!queue.isEmpty()) {

            int levelSize = queue.size();
            int nextLevelSum = 0;

            for (int i = 0; i < levelSize; i++) {

                TreeNode node = queue.poll();

                node.val = currentLevelSum - node.val;

                int siblingSum =
                    (node.left != null ? node.left.val : 0) +
                    (node.right != null ? node.right.val : 0);

                if (node.left != null) {
                    nextLevelSum += node.left.val;
                    node.left.val = siblingSum;
                    queue.offer(node.left);
                }

                if (node.right != null) {
                    nextLevelSum += node.right.val;
                    node.right.val = siblingSum;
                    queue.offer(node.right);
                }
            }

            currentLevelSum = nextLevelSum;
        }

        return root;
    }
}
```

---

### Complexity Analysis

**Time Complexity:**

```
O(n)
```

Each node processed exactly once.

**Space Complexity:**

```
O(n)
```

Queue storing nodes during BFS.

---

# Comparison of Approaches

| Approach     | Traversals | Extra Memory | Notes               |
| ------------ | ---------- | ------------ | ------------------- |
| Two-pass BFS | 2 BFS      | O(n)         | Most intuitive      |
| Two-pass DFS | 2 DFS      | O(n)         | Recursion-based     |
| Single BFS   | 1 BFS      | O(n)         | Optimized traversal |

---

# Recommended Approach

The **Single BFS approach** is generally the best because:

- Only **one traversal**
- No extra level sum storage
- Clean cousin-sum computation

It achieves:

```
Time: O(n)
Space: O(n)
```
