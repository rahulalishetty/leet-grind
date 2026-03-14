# 1302. Deepest Leaves Sum — Approaches

## Overview

There are two fundamental ways to traverse a binary tree:

- **DFS (Depth‑First Search)**
- **BFS (Breadth‑First Search)**

### Key Difference

| Traversal | Behavior                                                    |
| --------- | ----------------------------------------------------------- |
| DFS       | Goes **deep into branches first** before exploring siblings |
| BFS       | Traverses the tree **level by level**                       |

Both algorithms:

- Start from the **root**
- Use additional data structures
- Push children nodes for future processing

### Structural Difference

| Traversal | Data Structure | Order                     |
| --------- | -------------- | ------------------------- |
| BFS       | Queue          | Level by level            |
| DFS       | Stack          | Root → subtree until leaf |

### Operational Difference

| Step          | BFS                | DFS             |
| ------------- | ------------------ | --------------- |
| Pop           | Left side of queue | Top of stack    |
| Push children | Left then Right    | Right then Left |

---

# Approach 1: Iterative DFS (Preorder Traversal)

## Intuition

We use **DFS preorder traversal** with a stack.

While traversing the tree we track:

- the **depth of each node**
- the **sum of deepest leaf nodes**

Whenever we encounter a **leaf node**:

1. If its depth is **greater than the current deepest depth**, reset the sum.
2. If its depth equals the deepest depth, **add its value to the sum**.

---

## Algorithm

1. Push `(root, depth=0)` into stack.
2. While stack is not empty:
   - Pop node.
   - If node is a **leaf**:
     - Update deepest sum.
   - Otherwise:
     - Push **right child**
     - Push **left child**

3. Return the final deepest sum.

---

## Implementation

```java
class Solution {
  public int deepestLeavesSum(TreeNode root) {

    int deepestSum = 0;
    int depth = 0;
    int currDepth;

    Deque<Pair<TreeNode, Integer>> stack = new ArrayDeque<>();
    stack.push(new Pair(root, 0));

    while (!stack.isEmpty()) {

      Pair<TreeNode, Integer> p = stack.pop();
      root = p.getKey();
      currDepth = p.getValue();

      if (root.left == null && root.right == null) {

        if (depth < currDepth) {
          deepestSum = root.val;
          depth = currDepth;
        } else if (depth == currDepth) {
          deepestSum += root.val;
        }

      } else {

        if (root.right != null)
          stack.push(new Pair(root.right, currDepth + 1));

        if (root.left != null)
          stack.push(new Pair(root.left, currDepth + 1));
      }
    }

    return deepestSum;
  }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each node is visited exactly once.

### Space Complexity

```
O(H)
```

`H` = tree height (maximum recursion/stack depth).

Worst case (skewed tree):

```
O(N)
```

---

# Approach 2: Iterative BFS Traversal

## Intuition

Instead of exploring depth first, BFS explores the tree **level by level**.

For each node we track:

- its **depth**
- whether it is a **leaf**

We update the deepest leaf sum similarly to the DFS solution.

---

## Algorithm

1. Add `(root, depth=0)` to queue.
2. While queue is not empty:
   - Pop node from queue
   - If node is leaf:
     - update deepest sum
   - Push children into queue

3. Return deepest sum.

---

## Implementation

```java
class Solution {

  public int deepestLeavesSum(TreeNode root) {

    int deepestSum = 0;
    int depth = 0;
    int currDepth;

    Deque<Pair<TreeNode, Integer>> queue = new ArrayDeque<>();
    queue.offer(new Pair(root, 0));

    while (!queue.isEmpty()) {

      Pair<TreeNode, Integer> p = queue.poll();
      root = p.getKey();
      currDepth = p.getValue();

      if (root.left == null && root.right == null) {

        if (depth < currDepth) {
          deepestSum = root.val;
          depth = currDepth;
        }
        else if (depth == currDepth) {
          deepestSum += root.val;
        }

      } else {

        if (root.left != null)
          queue.offer(new Pair(root.left, currDepth + 1));

        if (root.right != null)
          queue.offer(new Pair(root.right, currDepth + 1));
      }
    }

    return deepestSum;
  }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Every node is visited once.

### Space Complexity

```
O(N)
```

Queue may contain up to **N/2 nodes** in the last level of a complete tree.

---

# Approach 3: Optimized BFS (Level‑Order)

## Intuition

Since BFS already processes the tree **level by level**, we do not need to track depth explicitly.

Instead:

- process one level at a time
- keep only nodes from the **current level**
- the **last processed level will contain deepest leaves**

Then simply sum those nodes.

---

## Algorithm

1. Initialize queue with root.
2. While queue is not empty:
   - store nodes of current level
   - prepare queue for next level

3. After traversal finishes:
   - sum values of nodes from last level.

---

## Implementation

```java
class Solution {

  public int deepestLeavesSum(TreeNode root) {

    ArrayDeque<TreeNode> nextLevel = new ArrayDeque<>();
    ArrayDeque<TreeNode> currLevel;

    nextLevel.offer(root);

    while (!nextLevel.isEmpty()) {

      currLevel = nextLevel.clone();
      nextLevel.clear();

      for (TreeNode node : currLevel) {

        if (node.left != null)
          nextLevel.offer(node.left);

        if (node.right != null)
          nextLevel.offer(node.right);
      }
    }

    int deepestSum = 0;

    for (TreeNode node : currLevel)
      deepestSum += node.val;

    return deepestSum;
  }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Every node is processed exactly once.

### Space Complexity

```
O(N)
```

The queue may contain up to **N/2 nodes** at the deepest level.

---

# Summary

| Approach      | Method          | Key Idea                | Time | Space |
| ------------- | --------------- | ----------------------- | ---- | ----- |
| DFS           | Stack traversal | Track depth of leaves   | O(N) | O(H)  |
| BFS           | Queue traversal | Track depth with nodes  | O(N) | O(N)  |
| Optimized BFS | Level traversal | Sum nodes at last level | O(N) | O(N)  |

---

## Key Insight

This problem becomes simple when we observe:

> **The deepest leaves are simply the nodes in the last level of the tree.**
