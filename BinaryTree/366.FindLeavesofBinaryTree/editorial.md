# 366. Find Leaves of Binary Tree — Solutions

## Approach 1: DFS (Depth-First Search) with Sorting

### Intuition

The order in which nodes appear in the final answer depends on the **height** of each node.

The **height of a node** is defined as the number of edges from the node to the deepest leaf node.

Nodes that share the same height will appear in the same group in the result.

For a node `root`, the height is:

```
height(root) = 1 + max(height(root.left), height(root.right))
```

If the node is `null`, we return height `-1`.

Because height depends on children, the traversal must be **post-order**.

---

### Algorithm

1. Traverse the tree using DFS.
2. Compute the height of each node.
3. Store `(height, value)` pairs.
4. Sort the pairs by height.
5. Group nodes with the same height together.

---

### Java Implementation

```java
class Solution {

    private List<Pair<Integer, Integer>> pairs;

    private int getHeight(TreeNode root) {

        if (root == null) return -1;

        int leftHeight = getHeight(root.left);
        int rightHeight = getHeight(root.right);

        int currHeight = Math.max(leftHeight, rightHeight) + 1;

        pairs.add(new Pair<>(currHeight, root.val));

        return currHeight;
    }

    public List<List<Integer>> findLeaves(TreeNode root) {

        pairs = new ArrayList<>();

        getHeight(root);

        Collections.sort(pairs, Comparator.comparing(p -> p.getKey()));

        List<List<Integer>> result = new ArrayList<>();

        int i = 0;
        int height = 0;

        while (i < pairs.size()) {

            List<Integer> group = new ArrayList<>();

            while (i < pairs.size() && pairs.get(i).getKey() == height) {
                group.add(pairs.get(i).getValue());
                i++;
            }

            result.add(group);
            height++;
        }

        return result;
    }
}
```

---

### Complexity

**Time Complexity**

```
O(N log N)
```

- DFS traversal → `O(N)`
- Sorting pairs → `O(N log N)`

**Space Complexity**

```
O(N)
```

- Storage of pairs
- Recursion stack

---

# Approach 2: DFS Without Sorting (Optimal)

### Intuition

Instead of collecting `(height, value)` pairs and sorting them later, we can **directly place nodes into their correct position** in the result.

Key idea:

- The height of a node determines which list it belongs to.
- If we encounter a new height level, we expand the result list.

Example:

```
[[4,3,5]] → [[4,3,5], []]
```

Then insert:

```
[[4,3,5],[2]]
```

---

### Algorithm

1. Traverse the tree using DFS.
2. Compute height of each node.
3. If `solution.size() == height`, create a new list.
4. Insert the node value at `solution[height]`.

---

### Java Implementation

```java
class Solution {

    private List<List<Integer>> solution;

    private int getHeight(TreeNode root) {

        if (root == null) return -1;

        int leftHeight = getHeight(root.left);
        int rightHeight = getHeight(root.right);

        int currHeight = Math.max(leftHeight, rightHeight) + 1;

        if (solution.size() == currHeight) {
            solution.add(new ArrayList<>());
        }

        solution.get(currHeight).add(root.val);

        return currHeight;
    }

    public List<List<Integer>> findLeaves(TreeNode root) {

        solution = new ArrayList<>();

        getHeight(root);

        return solution;
    }
}
```

---

### Complexity

**Time Complexity**

```
O(N)
```

Each node is visited once.

**Space Complexity**

```
O(N)
```

- Recursion stack
- Output storage
