# 2583. Kth Largest Sum in a Binary Tree — Approaches

## Overview

We are given the root of a binary tree and an integer **k**, where we want to find the **k-th largest level sum** of the tree.

The **level sum** of a tree for a given level is defined as the **sum of values of all nodes that have the same distance from the root**.

---

# Approach 1: Level Order Traversal + Max Heap

## Intuition

To calculate the sum of each level in a tree, we can use **level order traversal** (BFS).

Level-order traversal processes nodes **level by level**, which makes it easy to compute the sum for each level.

Steps:

1. Traverse the tree level by level using a queue.
2. Compute the sum of nodes at each level.
3. Insert each level sum into a **max heap**.
4. Remove the largest element **k−1 times**.
5. The element at the top will be the **k-th largest level sum**.

---

## Algorithm

1. Initialize a **max heap** `pq`.
2. Initialize a queue `bfsQueue` for BFS traversal.
3. Add the root node to the queue.
4. While the queue is not empty:
   - Get the current level size.
   - Compute the sum of nodes for that level.
   - Push children of each node to the queue.
   - Insert the level sum into the heap.
5. If the heap size is smaller than `k`, return `-1`.
6. Remove the largest element `k-1` times.
7. Return the top element of the heap.

---

## Java Implementation

```java
class Solution {

    public long kthLargestLevelSum(TreeNode root, int k) {
        PriorityQueue<Long> pq = new PriorityQueue<>(Collections.reverseOrder());

        Queue<TreeNode> bfsQueue = new LinkedList<>();
        bfsQueue.add(root);

        while (!bfsQueue.isEmpty()) {
            int size = bfsQueue.size();
            long sum = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = bfsQueue.remove();
                sum += node.val;

                if (node.left != null) bfsQueue.add(node.left);
                if (node.right != null) bfsQueue.add(node.right);
            }

            pq.add(sum);
        }

        if (pq.size() < k) return -1;

        for (int i = 0; i < k - 1; i++) {
            pq.remove();
        }

        return pq.peek();
    }
}
```

---

## Complexity Analysis

Let **N** be the number of nodes in the tree.

### Time Complexity

```
O((N + K) * log N)
```

Explanation:

- BFS traversal: `O(N)`
- Heap insertions: `O(N log N)`
- Removing `k−1` elements: `O(k log N)`

Total complexity:

```
O((N + K) log N)
```

---

### Space Complexity

```
O(N)
```

Because:

- BFS queue can store up to `N` nodes.
- Heap can also store up to `N` level sums.

---

# Approach 2: Level Order Traversal + Min Heap

## Intuition

Instead of storing all level sums in a max heap, we maintain a **min heap of size k**.

Key idea:

- Keep only the **k largest level sums**.
- Whenever the heap size exceeds `k`, remove the smallest element.
- At the end, the top of the min heap will be the **k-th largest sum**.

This reduces heap size and improves efficiency.

---

## Algorithm

1. Initialize a **min heap** `pq`.
2. Perform BFS traversal to compute level sums.
3. For each level sum:
   - Insert it into the heap.
   - If heap size exceeds `k`, remove the smallest element.
4. After processing all levels:
   - If heap size < `k`, return `-1`.
   - Otherwise return `pq.peek()`.

---

## Java Implementation

```java
class Solution {

    public long kthLargestLevelSum(TreeNode root, int k) {
        PriorityQueue<Long> pq = new PriorityQueue<>();

        Queue<TreeNode> bfsQueue = new LinkedList<>();
        bfsQueue.add(root);

        while (!bfsQueue.isEmpty()) {
            int size = bfsQueue.size();
            long sum = 0;

            for (int i = 0; i < size; i++) {
                TreeNode node = bfsQueue.remove();
                sum += node.val;

                if (node.left != null) bfsQueue.add(node.left);
                if (node.right != null) bfsQueue.add(node.right);
            }

            pq.add(sum);

            if (pq.size() > k) {
                pq.remove();
            }
        }

        if (pq.size() < k) return -1;

        return pq.peek();
    }
}
```

---

## Complexity Analysis

Let **N** be the number of nodes.

### Time Complexity

```
O(N log k)
```

Explanation:

- BFS traversal: `O(N)`
- Heap operations limited to size `k`: `O(log k)`

---

### Space Complexity

```
O(N)
```

Because:

- BFS queue may contain up to `N` nodes.
- Heap stores at most `k` elements.

---

# Comparison of Approaches

| Approach               | Heap Type | Heap Size | Time Complexity |
| ---------------------- | --------- | --------- | --------------- |
| Level Order + Max Heap | Max Heap  | Up to N   | O((N+K) log N)  |
| Level Order + Min Heap | Min Heap  | K         | O(N log K)      |

---

# Recommended Approach

The **Min Heap approach** is preferred because:

- Heap size remains **bounded by k**
- Better performance when `k` is much smaller than number of levels
- Lower memory overhead

Final complexity:

```
Time: O(N log k)
Space: O(N)
```
