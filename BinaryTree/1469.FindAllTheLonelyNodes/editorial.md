# 1469. Find All The Lonely Nodes

## Approach 1: Depth‑First Search (DFS)

### Intuition

A **lonely node** is a node whose **parent has only one child**.

To identify lonely nodes, we traverse the tree and determine whether a node has a sibling. If a node has **no sibling**, it is lonely and should be added to the result list.

We can traverse the tree using **Depth‑First Search (DFS)**. During traversal, we pass a boolean flag `isLonely` that indicates whether the current node is the only child of its parent.

- If a node is reached via the **left child** and the parent has **no right child**, the node is lonely.
- If a node is reached via the **right child** and the parent has **no left child**, the node is lonely.

Thus, when calling DFS recursively:

- For the left child → `isLonely = (parent.right == null)`
- For the right child → `isLonely = (parent.left == null)`

The root is never lonely because it has no parent.

---

### Algorithm

1. Define a recursive function `DFS(node, isLonely, ans)`.
2. If `node` is `null`, return.
3. If `isLonely` is `true`, add the node value to the result list.
4. Recursively visit the left child:
   - `isLonely = (node.right == null)`
5. Recursively visit the right child:
   - `isLonely = (node.left == null)`
6. Start DFS with `root` and `isLonely = false`.
7. Return the result list.

---

### Java Implementation

```java
class Solution {
    void DFS(TreeNode root, boolean isLonely, List<Integer> ans) {
        if (root == null) {
            return;
        }

        if (isLonely) {
            ans.add(root.val);
        }

        DFS(root.left, root.right == null, ans);
        DFS(root.right, root.left == null, ans);
    }

    public List<Integer> getLonelyNodes(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        DFS(root, false, ans);
        return ans;
    }
}
```

---

### Complexity Analysis

Let **N** be the number of nodes.

**Time Complexity:** `O(N)`
Each node is visited exactly once.

**Space Complexity:** `O(N)` (worst case)
In a skewed tree, the recursion stack may reach size `N`.

---

# Approach 2: Breadth‑First Search (BFS)

### Intuition

Instead of DFS, we can also traverse the tree **level by level** using **Breadth‑First Search (BFS)**.

While traversing, we store pairs:

```
(node, isLonely)
```

The flag indicates whether the node has a sibling. When processing each node, if `isLonely` is true, we add it to the result.

For each node:

- If the **right child exists**, it is lonely when `left == null`
- If the **left child exists**, it is lonely when `right == null`

---

### Algorithm

1. Initialize an empty list `ans`.
2. Initialize a queue storing `(node, isLonely)` pairs.
3. Push `(root, false)` into the queue.
4. While the queue is not empty:
   - Pop `(currNode, isLonely)`.
   - If `isLonely == true`, add `currNode.val` to `ans`.
   - If `currNode.right` exists:
     - push `(currNode.right, currNode.left == null)`.
   - If `currNode.left` exists:
     - push `(currNode.left, currNode.right == null)`.
5. Return `ans`.

---

### Java Implementation

```java
class Solution {
    public List<Integer> getLonelyNodes(TreeNode root) {
        List<Integer> ans = new ArrayList<>();

        Queue<Pair<TreeNode, Boolean>> q = new LinkedList<>();
        q.add(new Pair(root, false));

        while (!q.isEmpty()) {
            Pair<TreeNode, Boolean> qFront = q.remove();

            TreeNode currNode = qFront.getKey();
            Boolean isLonely = qFront.getValue();

            if (isLonely) {
                ans.add(currNode.val);
            }

            if (currNode.right != null) {
                q.add(new Pair(currNode.right, currNode.left == null));
            }

            if (currNode.left != null) {
                q.add(new Pair(currNode.left, currNode.right == null));
            }
        }

        return ans;
    }
}
```

---

### Complexity Analysis

Let **N** be the number of nodes.

**Time Complexity:** `O(N)`
Each node is processed exactly once.

**Space Complexity:** `O(N)`
The queue can contain up to `N` nodes in the worst case.

---

# Key Insight

A node is **lonely** if and only if:

```
(parent.left == null && parent.right != null)
OR
(parent.left != null && parent.right == null)
```

Both DFS and BFS approaches simply detect this structural property during traversal.
