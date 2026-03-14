# 1261. Find Elements in a Contaminated Binary Tree — Approaches

## Overview

We are given a binary tree that originally followed these rules:

1. `root.val = 0`
2. If a node has value `x`:
   - `left child = 2 * x + 1`
3. If a node has value `x`:
   - `right child = 2 * x + 2`

However, the tree has been **contaminated**, meaning **all node values were replaced with `-1`**.

Our task is to reconstruct the original values and support a query:

```
bool find(int target)
```

that checks whether a value existed in the original tree.

We implement two functions:

- **FindElements(root)** → reconstruct the tree
- **find(target)** → check if the value exists

---

# Approach 1: Tree Traversal (DFS)

## Intuition

The rules defining node values allow us to reconstruct the entire tree **starting from the root**.

Since:

```
root.val = 0
```

We can compute child values using the formulas:

```
left  = 2*x + 1
right = 2*x + 2
```

Thus, once the root value is known, all node values can be recovered by traversing the tree.

A natural traversal method for this is **Depth‑First Search (DFS)**.

During traversal:

- We compute the correct value of each node
- Store it inside a **HashSet**
- Use that set for efficient lookup

DFS works well because each node's value depends only on its **parent value**.

---

## Algorithm

1. Create a **HashSet `seen`**.
2. Start DFS from the root with value `0`.
3. For each node:
   - Add its recovered value to `seen`
   - Recurse to children

4. Compute child values:

```
left  = value * 2 + 1
right = value * 2 + 2
```

5. For `find(target)`:

```
return seen.contains(target)
```

---

## Implementation

```java
class FindElements {

    HashSet<Integer> seen;

    public FindElements(TreeNode root) {
        seen = new HashSet<>();
        dfs(root, 0);
    }

    public boolean find(int target) {
        return seen.contains(target);
    }

    private void dfs(TreeNode currentNode, int currentValue) {
        if (currentNode == null) return;

        seen.add(currentValue);

        dfs(currentNode.left, currentValue * 2 + 1);
        dfs(currentNode.right, currentValue * 2 + 2);
    }
}
```

---

## Complexity Analysis

Let **N = number of nodes**.

### Time Complexity

```
Constructor → O(N)
find()      → O(1)
```

We traverse every node once to reconstruct the tree.

---

### Space Complexity

```
O(N)
```

The `HashSet` stores all node values.

---

# Approach 2: Tree Traversal (BFS)

## Intuition

Instead of DFS, we can reconstruct the tree using **Breadth‑First Search (BFS)**.

BFS processes nodes **level by level** using a queue.

During traversal:

1. Assign correct values to nodes
2. Add them to the set
3. Enqueue children

Unlike DFS where values are passed as parameters, BFS can **overwrite node values directly**.

---

## Algorithm

1. Create a **HashSet `seen`**
2. Initialize queue with root
3. Set:

```
root.val = 0
```

4. While queue not empty:
   - Pop node
   - Add value to `seen`
   - Assign values to children
   - Push children into queue

5. `find(target)` returns:

```
seen.contains(target)
```

---

## Implementation

```java
class FindElements {

    HashSet<Integer> seen;

    public FindElements(TreeNode root) {
        seen = new HashSet<>();
        bfs(root);
    }

    public boolean find(int target) {
        return seen.contains(target);
    }

    private void bfs(TreeNode root) {

        Queue<TreeNode> queue = new LinkedList<>();

        root.val = 0;
        queue.add(root);

        while (!queue.isEmpty()) {

            TreeNode currentNode = queue.remove();

            seen.add(currentNode.val);

            if (currentNode.left != null) {
                currentNode.left.val = currentNode.val * 2 + 1;
                queue.add(currentNode.left);
            }

            if (currentNode.right != null) {
                currentNode.right.val = currentNode.val * 2 + 2;
                queue.add(currentNode.right);
            }
        }
    }
}
```

---

## Complexity Analysis

Let **N = number of nodes**.

### Time Complexity

```
Constructor → O(N)
find()      → O(1)
```

Each node is processed exactly once.

---

### Space Complexity

```
O(N)
```

We store all recovered node values in a set.

---

# Summary

| Approach | Traversal   | Idea                            | Constructor | find() |
| -------- | ----------- | ------------------------------- | ----------- | ------ |
| DFS      | Recursive   | Recover values during recursion | O(N)        | O(1)   |
| BFS      | Level order | Recover values level by level   | O(N)        | O(1)   |

---

## Key Insight

Once the root value is known:

```
node.left  = 2*x + 1
node.right = 2*x + 2
```

the **entire tree can be reconstructed deterministically**, allowing fast lookups using a hash set.
