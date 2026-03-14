# Path Sum II — Approach: Depth First Traversal (DFS) | Recursion

## Intuition

The intuition for this approach is straightforward. The problem asks us to find **all root-to-leaf paths** in a binary tree whose node values sum to a given target.

If we think about **Depth First Search (DFS)**, it naturally explores one branch completely before moving to the next. This behavior fits perfectly with the idea of exploring **root-to-leaf paths**.

During traversal we maintain:

1. **A running remaining sum**
2. **A list representing the current path**

If we reach a **leaf node** and the remaining sum equals the node's value, we have found a valid path.

We then add this path to the final list of results.

DFS works well because it processes **one branch at a time**, allowing us to **backtrack efficiently**.

---

## Algorithm

We define a recursive function:

```
recurseTree(node, remainingSum, pathNodes, pathsList)
```

### Parameters

- **node** — current node in the traversal
- **remainingSum** — remaining sum required to reach the target
- **pathNodes** — list containing the nodes visited so far
- **pathsList** — list of all valid paths found

### Steps

1. If the node is `null`, return.
2. Add the current node value to `pathNodes`.
3. If:
   - `remainingSum == node.val`
   - and the node is a **leaf node**

   then we add a **copy** of `pathNodes` to the result.

4. Otherwise:
   - Recurse on the left subtree
   - Recurse on the right subtree
5. After recursion, **remove the current node from pathNodes** (backtracking).

---

## Java Implementation

```java
class Solution {
    private void recurseTree(
        TreeNode node,
        int remainingSum,
        List<Integer> pathNodes,
        List<List<Integer>> pathsList
    ) {
        if (node == null) {
            return;
        }

        // Add the current node to the path
        pathNodes.add(node.val);

        // If it's a leaf and matches the remaining sum
        if (
            remainingSum == node.val &&
            node.left == null &&
            node.right == null
        ) {
            pathsList.add(new ArrayList<>(pathNodes));
        } else {
            // Recurse left and right
            recurseTree(node.left, remainingSum - node.val, pathNodes, pathsList);
            recurseTree(node.right, remainingSum - node.val, pathNodes, pathsList);
        }

        // Backtrack
        pathNodes.remove(pathNodes.size() - 1);
    }

    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> pathsList = new ArrayList<>();
        List<Integer> pathNodes = new ArrayList<>();
        recurseTree(root, sum, pathNodes, pathsList);
        return pathsList;
    }
}
```

---

# Complexity Analysis

## Time Complexity

```
O(N²)
```

Worst case occurs in a **complete binary tree**:

- About **N/2 leaf nodes**
- For each leaf we copy a path that may contain **O(N) nodes**

Thus worst-case complexity becomes:

```
O(N * N) = O(N²)
```

---

## Space Complexity

```
O(N)
```

DFS recursion depth can reach **N** in the worst case (skewed tree).

The extra space used includes:

- recursion stack
- `pathNodes` list for the current path

If we include **output storage**, total space could be **O(N²)**.

---

# Why BFS Is Not Ideal Here

Breadth First Search would also work logically, but it is **memory heavy**.

### Problem with BFS

BFS processes nodes **level by level**, which means:

- For every node in the queue, we must store its **entire path** from the root.

Example:

At level 10:

```
20 nodes
```

We would need:

```
20 different path lists
```

This causes large memory overhead.

---

### Advantage of DFS

DFS processes **one branch at a time**:

- Only one `pathNodes` list is maintained
- Backtracking removes nodes when returning from recursion

This keeps memory usage minimal.

---

## When BFS Would Be Better

If the problem asked for:

```
Count of paths
```

instead of storing full paths, BFS could be equally efficient.

But since we must **return every path**, DFS with backtracking is the better approach.
