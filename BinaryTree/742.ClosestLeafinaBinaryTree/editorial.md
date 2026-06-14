# 742. Closest Leaf in a Binary Tree

## Problem Summary

Given a binary tree and a target node value `k`, find the value of the leaf node that has the minimum distance from the node with value `k`.

A leaf node has no children.

---

# Key Observation

The closest leaf is **not necessarily in the subtree of k**.

Example:

```text
        1
       / \
      2   3
     /
    4
   /
  5
```

If `k = 4`:

- Closest leaf in its subtree = `5` (distance 1)
- But sometimes a closer leaf may exist by moving upward and then downward into another branch.

Therefore we must treat the tree like an **undirected graph**.

---

# Approach 1: Convert Tree to Graph + BFS (Recommended)

## Idea

Convert the tree into an undirected graph:

```text
parent <-> child
```

Then:

1. Find node `k`
2. Start BFS from node `k`
3. First leaf encountered is the answer

Why?

BFS explores nodes level-by-level.

The first leaf reached must be the closest leaf.

---

## Example

```text
        1
       / \
      2   3
     /
    4
```

k = 2

Graph:

```text
1 <-> 2
1 <-> 3
2 <-> 4
```

BFS:

```text
2
├── 1
└── 4
```

Node 4 is a leaf.

Answer = 4.

---

## Algorithm

### DFS Phase

Build:

```java
Map<TreeNode, List<TreeNode>>
```

Store all bidirectional edges.

Also locate target node.

### BFS Phase

Start BFS from target.

For every popped node:

```java
if leaf
    return node.val;
```

Otherwise push neighbors.

---

## Complexity

### Time

Building graph:

```text
O(N)
```

BFS:

```text
O(N)
```

Total:

```text
O(N)
```

### Space

Graph + queue + visited:

```text
O(N)
```

---

## Java Solution

```java
class Solution {

    private Map<TreeNode, List<TreeNode>> graph = new HashMap<>();
    private TreeNode target;

    public int findClosestLeaf(TreeNode root, int k) {

        buildGraph(root, null, k);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();

        queue.offer(target);
        visited.add(target);

        while (!queue.isEmpty()) {

            TreeNode node = queue.poll();

            if (node.left == null && node.right == null) {
                return node.val;
            }

            for (TreeNode next : graph.getOrDefault(node, List.of())) {

                if (visited.add(next)) {
                    queue.offer(next);
                }
            }
        }

        return -1;
    }

    private void buildGraph(TreeNode node, TreeNode parent, int k) {

        if (node == null) {
            return;
        }

        if (node.val == k) {
            target = node;
        }

        graph.putIfAbsent(node, new ArrayList<>());

        if (parent != null) {

            graph.get(node).add(parent);
            graph.get(parent).add(node);
        }

        buildGraph(node.left, node, k);
        buildGraph(node.right, node, k);
    }
}
```

---

# Approach 2: Parent Map + BFS

## Idea

Instead of building a full graph, store only:

```java
child -> parent
```

During BFS each node can move:

- left child
- right child
- parent

This effectively creates the graph lazily.

---

## Algorithm

### DFS

Store:

```java
Map<TreeNode, TreeNode> parent
```

Find target node.

### BFS

From target:

```text
left
right
parent
```

The first leaf found is the answer.

---

## Complexity

Time:

```text
O(N)
```

Space:

```text
O(N)
```

---

## Java Solution

```java
class Solution {

    private Map<TreeNode, TreeNode> parent = new HashMap<>();
    private TreeNode target;

    public int findClosestLeaf(TreeNode root, int k) {

        buildParent(root, null, k);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();

        queue.offer(target);
        visited.add(target);

        while (!queue.isEmpty()) {

            TreeNode node = queue.poll();

            if (node.left == null && node.right == null) {
                return node.val;
            }

            if (node.left != null && visited.add(node.left)) {
                queue.offer(node.left);
            }

            if (node.right != null && visited.add(node.right)) {
                queue.offer(node.right);
            }

            TreeNode p = parent.get(node);

            if (p != null && visited.add(p)) {
                queue.offer(p);
            }
        }

        return -1;
    }

    private void buildParent(TreeNode node,
                             TreeNode par,
                             int k) {

        if (node == null) {
            return;
        }

        if (node.val == k) {
            target = node;
        }

        parent.put(node, par);

        buildParent(node.left, node, k);
        buildParent(node.right, node, k);
    }
}
```

---

# Approach 3: Dynamic Programming on Tree (Interview Advanced)

## Idea

For every node compute:

```text
distance to closest leaf in its subtree
which leaf achieves it
```

Then perform a second DFS.

While moving from parent to child, propagate information about:

```text
closest leaf outside child's subtree
```

This allows each node to know:

1. Best leaf below it
2. Best leaf through ancestors

Answer for k is the minimum of both.

---

## Why It Works

Every path from k to a leaf belongs to one of two categories:

### Category 1

Leaf inside k's subtree

```text
k
│
└── leaf
```

### Category 2

Go upward first

```text
k
↑
parent
↓
other subtree
```

The rerooting DP captures both.

---

## Complexity

Time:

```text
O(N)
```

Space:

```text
O(N)
```

---

# Which Approach Should You Use?

| Approach            | Time | Space | Difficulty  |
| ------------------- | ---- | ----- | ----------- |
| Graph + BFS         | O(N) | O(N)  | Easy        |
| Parent Map + BFS    | O(N) | O(N)  | Easy-Medium |
| Tree DP / Rerooting | O(N) | O(N)  | Hard        |

---

# Interview Recommendation

The cleanest solution is:

**Parent Map + BFS**

Reasons:

1. Easy to explain
2. Uses standard tree techniques
3. Naturally handles upward movement
4. Optimal O(N) complexity
5. Most commonly accepted interview solution

A useful mental model is:

> Convert the tree into an undirected graph (explicitly or implicitly), then run BFS from node k. The first leaf reached is guaranteed to be the closest leaf.
