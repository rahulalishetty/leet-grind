# 2385. Amount of Time for Binary Tree to Be Infected — Approaches

## Overview

Our objective is to find the **number of minutes needed for the entire tree to become infected**.

Key observation:

- A node **1 level away** from the start node becomes infected after **1 minute**.
- A node **2 levels away** becomes infected after **2 minutes**.
- Therefore, the infection time equals the **distance from the start node**.

Thus, the solution is equivalent to finding:

```
Maximum distance from the start node to any node in the tree
```

---

# Approach 1: Convert to Graph + Breadth-First Search (BFS)

## Intuition

The start node is **not necessarily the root**.

Infection spreads:

- From parent → child
- From child → parent

However, a binary tree normally allows traversal **only downward**.

To simulate infection spreading in **all directions**, we convert the tree into an **undirected graph**.

In this graph:

- Each node is a vertex
- Parent-child relationships become **bidirectional edges**

Once converted, we can run **BFS starting from the start node** to compute the maximum distance.

---

## Step 1 — Convert Binary Tree to Graph

We perform a **preorder traversal** and build an adjacency list.

Each node stores its neighbors:

- parent
- left child
- right child

### Conversion Function

```java
public void convert(TreeNode current, int parent, Map<Integer, Set<Integer>> map){
    if (current == null) {
        return;
    }

    if (!map.containsKey(current.val)) {
        map.put(current.val, new HashSet<>());
    }

    Set<Integer> adjacentList = map.get(current.val);

    if (parent != 0) {
        adjacentList.add(parent);
    }

    if (current.left != null) {
        adjacentList.add(current.left.val);
    }

    if (current.right != null) {
        adjacentList.add(current.right.val);
    }

    convert(current.left, current.val, map);
    convert(current.right, current.val, map);
}
```

---

## Step 2 — BFS from Start Node

Use BFS to calculate **maximum distance from the start node**.

BFS characteristics:

- Level-by-level traversal
- Each level corresponds to **one minute of infection spread**

Key variables:

- `queue` → nodes to process
- `visited` → prevents revisiting nodes
- `minute` → infection time

---

## Algorithm

1. Create adjacency map `map`.
2. Convert tree to graph using `convert()`.
3. Initialize:
   - queue containing `start`
   - visited set containing `start`
4. While queue is not empty:
   - Process nodes level-by-level.
   - Add unvisited neighbors to queue.
   - Increment `minute` after each level.
5. Return `minute - 1`.

The subtraction occurs because the **first node starts infected at minute 0**.

---

## Implementation

```java
class Solution {

    public int amountOfTime(TreeNode root, int start) {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        convert(root, 0, map);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);

        int minute = 0;

        Set<Integer> visited = new HashSet<>();
        visited.add(start);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            while (levelSize > 0) {
                int current = queue.poll();

                for (int num : map.get(current)) {
                    if (!visited.contains(num)) {
                        visited.add(num);
                        queue.add(num);
                    }
                }

                levelSize--;
            }

            minute++;
        }

        return minute - 1;
    }

    public void convert(
        TreeNode current,
        int parent,
        Map<Integer, Set<Integer>> map
    ) {
        if (current == null) {
            return;
        }

        if (!map.containsKey(current.val)) {
            map.put(current.val, new HashSet<>());
        }

        Set<Integer> adjacentList = map.get(current.val);

        if (parent != 0) {
            adjacentList.add(parent);
        }

        if (current.left != null) {
            adjacentList.add(current.left.val);
        }

        if (current.right != null) {
            adjacentList.add(current.right.val);
        }

        convert(current.left, current.val, map);
        convert(current.right, current.val, map);
    }
}
```

---

## Complexity Analysis

Let **n** be the number of nodes.

### Time Complexity

```
O(n)
```

- Converting the tree to a graph takes **O(n)**.
- BFS traversal also takes **O(n)**.

---

### Space Complexity

```
O(n)
```

Additional memory required for:

- adjacency map
- BFS queue
- visited set

---

# Approach 2: One-Pass Depth-First Search

## Intuition

The previous solution required **two passes**:

1. Convert tree → graph
2. BFS traversal

We can solve the problem using **one DFS traversal**.

Key idea:

If the **start node were the root**, the answer would simply be the **height of the tree**.

However, since the start node may be anywhere, the infection spreads in **two directions**:

1. Downward into the start node's subtree
2. Upward toward ancestors and then into other subtrees

Thus, the maximum infection time equals the **maximum distance from the start node**.

---

## Key Trick

We use **negative depths** to indicate that a subtree **contains the start node**.

This allows us to propagate the distance from the start node upward during recursion.

### Four Cases

1️⃣ `root == null`

```
return 0
```

---

2️⃣ `root.val == start`

- Found start node
- Calculate depth of its subtree
- Return **-1** to signal start found

---

3️⃣ Start node not in subtree

```
depth = max(leftDepth, rightDepth) + 1
```

---

4️⃣ Start node exists in subtree

Compute distance:

```
distance = abs(leftDepth) + abs(rightDepth)
```

Update global maximum distance.

---

## Algorithm

1. Maintain global variable `maxDistance`.
2. Run DFS traversal.
3. Use negative values to track distance to start node.
4. Update `maxDistance` when combining subtrees.
5. Return `maxDistance`.

---

## Implementation

```java
class Solution {

    private int maxDistance = 0;

    public int amountOfTime(TreeNode root, int start) {
        traverse(root, start);
        return maxDistance;
    }

    public int traverse(TreeNode root, int start) {
        int depth = 0;

        if (root == null) {
            return depth;
        }

        int leftDepth = traverse(root.left, start);
        int rightDepth = traverse(root.right, start);

        if (root.val == start) {
            maxDistance = Math.max(leftDepth, rightDepth);
            depth = -1;
        }
        else if (leftDepth >= 0 && rightDepth >= 0) {
            depth = Math.max(leftDepth, rightDepth) + 1;
        }
        else {
            int distance = Math.abs(leftDepth) + Math.abs(rightDepth);
            maxDistance = Math.max(maxDistance, distance);

            depth = Math.min(leftDepth, rightDepth) - 1;
        }

        return depth;
    }
}
```

---

## Complexity Analysis

Let **n** be the number of nodes.

### Time Complexity

```
O(n)
```

Each node is visited exactly once during DFS traversal.

---

### Space Complexity

```
O(n)
```

The recursion stack depth may reach **n** in the worst case (skewed tree).
