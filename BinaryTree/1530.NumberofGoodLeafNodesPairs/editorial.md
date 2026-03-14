# 1530. Number of Good Leaf Nodes Pairs — Approaches

## Overview

Given the root of a binary tree, we must count the number of **pairs of leaf nodes** whose **shortest path distance** is **≤ distance**.

Distance is defined as the **number of edges in the shortest path** between two nodes.

---

# Approach 1 — Graph Conversion + BFS

## Intuition

Binary tree nodes only store references to their **children**, not their **parent**.

To easily move **both up and down the tree**, we convert the tree into an **undirected graph**.

Steps:

1. Traverse the tree and build an **adjacency list**
2. Identify **all leaf nodes**
3. For every leaf node:
   - Run **BFS**
   - Count other leaf nodes within `distance`
4. Divide the final count by **2** because each pair is counted twice

---

## Algorithm

1. Build adjacency list graph.
2. Collect all leaf nodes.
3. For each leaf node:
   - Run BFS up to depth `distance`.
   - Count reachable leaf nodes.
4. Return `ans / 2`.

---

## Java Implementation

```java
class Solution {

    public int countPairs(TreeNode root, int distance) {
        Map<TreeNode, List<TreeNode>> graph = new HashMap<>();
        Set<TreeNode> leafNodes = new HashSet<>();

        traverseTree(root, null, graph, leafNodes);

        int ans = 0;

        for (TreeNode leaf : leafNodes) {

            Queue<TreeNode> queue = new LinkedList<>();
            Set<TreeNode> seen = new HashSet<>();

            queue.add(leaf);
            seen.add(leaf);

            for (int i = 0; i <= distance; i++) {

                int size = queue.size();

                for (int j = 0; j < size; j++) {

                    TreeNode node = queue.remove();

                    if (leafNodes.contains(node) && node != leaf) {
                        ans++;
                    }

                    for (TreeNode neighbor : graph.getOrDefault(node, List.of())) {

                        if (!seen.contains(neighbor)) {
                            queue.add(neighbor);
                            seen.add(neighbor);
                        }
                    }
                }
            }
        }

        return ans / 2;
    }
}
```

---

## Complexity

Time Complexity:

```
O(N²)
```

Space Complexity:

```
O(N)
```

---

# Approach 2 — Post‑Order Traversal

## Intuition

The shortest path between two nodes always goes through their **Lowest Common Ancestor (LCA)**.

During **post‑order traversal**, each node gathers information about leaf nodes in its subtrees.

For each node:

- left subtree distances
- right subtree distances

We combine them to count valid leaf pairs.

---

## Algorithm

1. Recursively process left subtree.
2. Recursively process right subtree.
3. Count valid leaf pairs where:

```
2 + d1 + d2 ≤ distance
```

4. Return updated distance counts upward.

---

## Java Implementation

```java
class Solution {

    public int countPairs(TreeNode root, int distance) {
        return postOrder(root, distance)[11];
    }

    private int[] postOrder(TreeNode node, int distance) {

        if (node == null) return new int[12];

        if (node.left == null && node.right == null) {
            int[] arr = new int[12];
            arr[0] = 1;
            return arr;
        }

        int[] left = postOrder(node.left, distance);
        int[] right = postOrder(node.right, distance);

        int[] curr = new int[12];

        for (int i = 0; i < 10; i++) {
            curr[i + 1] += left[i] + right[i];
        }

        curr[11] += left[11] + right[11];

        for (int d1 = 0; d1 <= distance; d1++) {
            for (int d2 = 0; d2 <= distance; d2++) {

                if (2 + d1 + d2 <= distance) {
                    curr[11] += left[d1] * right[d2];
                }
            }
        }

        return curr;
    }
}
```

---

## Complexity

Time Complexity:

```
O(N · D²)
```

Space Complexity:

```
O(H)
```

Where:

```
N = number of nodes
D = distance limit
H = tree height
```

---

# Approach 3 — Post‑Order + Prefix Sum Optimization

## Intuition

The previous approach checks **all distance pairs**, which costs:

```
O(D²)
```

We can optimize using **prefix sums**.

Instead of checking every `(d1, d2)` pair:

```
2 + d1 + d2 ≤ distance
```

we accumulate valid counts efficiently.

---

## Algorithm

1. Compute leaf distance arrays for left and right subtrees.
2. Use prefix sums on left distances.
3. Multiply with right subtree counts.

---

## Java Implementation

```java
class Solution {

    public int countPairs(TreeNode root, int distance) {
        return postOrder(root, distance)[11];
    }

    private int[] postOrder(TreeNode node, int distance) {

        if (node == null) return new int[12];

        if (node.left == null && node.right == null) {
            int[] arr = new int[12];
            arr[0] = 1;
            return arr;
        }

        int[] left = postOrder(node.left, distance);
        int[] right = postOrder(node.right, distance);

        int[] curr = new int[12];

        for (int i = 0; i < 10; i++) {
            curr[i + 1] += left[i] + right[i];
        }

        curr[11] += left[11] + right[11];

        int prefixSum = 0;
        int i = 0;

        for (int d2 = distance - 2; d2 >= 0; d2--) {
            prefixSum += left[i++];
            curr[11] += prefixSum * right[d2];
        }

        return curr;
    }
}
```

---

# Complexity

Time Complexity:

```
O(N · D)
```

Space Complexity:

```
O(H)
```

---

# Summary

| Approach            | Idea                       | Time       | Space |
| ------------------- | -------------------------- | ---------- | ----- |
| Graph + BFS         | Convert tree to graph      | O(N²)      | O(N)  |
| Post‑Order          | Count pairs at LCA         | O(N·D²)    | O(H)  |
| Prefix Optimization | Efficient distance pairing | **O(N·D)** | O(H)  |

The **optimized post‑order traversal** is the **most efficient solution**.
