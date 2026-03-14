# Binary Tree Level Order Traversal II — Approaches

## Tree Traversal Strategies

There are two general strategies to traverse a tree.

---

# 1. Depth First Search (DFS)

In DFS, we prioritize **depth**.

Traversal goes:

```
Root → deep into a branch → leaf → backtrack → next branch
```

DFS has three common variants depending on node order:

### Preorder

```
Root → Left → Right
```

### Inorder

```
Left → Root → Right
```

### Postorder

```
Left → Right → Root
```

---

# 2. Breadth First Search (BFS)

In BFS we traverse the tree **level by level**.

Traversal goes:

```
Level 0 → Level 1 → Level 2 → ...
```

Nodes on higher levels are visited before nodes on lower levels.

![alt text](image.png)

---

# Problem Goal

The problem asks for:

```
Bottom‑Up Level Order Traversal
```

Example output:

```
[[4,5], [2,3], [1]]
```

This means:

1. Traverse the tree **level by level**
2. Then **reverse the result** to obtain bottom‑up order

Two simple techniques:

1. **Recursive DFS**
2. **Iterative BFS using queues**

Both approaches perform **root → bottom traversal** and then reverse the final result.

---

# Approach 1: Recursion (DFS Preorder)

## Intuition

We perform DFS while tracking the **level index**.

We store nodes in a structure:

```
levels[level] → nodes at that level
```

Steps:

1. If we encounter a new level, create a new list.
2. Add the current node value to that level.
3. Recurse on children.
4. Reverse the result at the end.

---

## Java Implementation

```java
class Solution {

    List<List<Integer>> levels = new ArrayList<>();

    public void helper(TreeNode node, int level) {

        if (levels.size() == level)
            levels.add(new ArrayList<>());

        levels.get(level).add(node.val);

        if (node.left != null)
            helper(node.left, level + 1);

        if (node.right != null)
            helper(node.right, level + 1);
    }

    public List<List<Integer>> levelOrderBottom(TreeNode root) {

        if (root == null)
            return levels;

        helper(root, 0);

        Collections.reverse(levels);

        return levels;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each node is processed exactly once.

### Space Complexity

```
O(N)
```

Space is required for:

- recursion stack
- result structure

---

# Approach 2: Iteration (BFS Traversal)

## Intuition

We use **queues** to process nodes level by level.

Two queues are maintained:

```
currLevel → nodes of current level
nextLevel → nodes of next level
```

Steps:

1. Start with root in `nextLevel`
2. Move nodes from `nextLevel` → `currLevel`
3. Process nodes in `currLevel`
4. Add their children into `nextLevel`
5. Reverse the result at the end

---

## Java Implementation

```java
class Solution {

    public List<List<Integer>> levelOrderBottom(TreeNode root) {

        List<List<Integer>> levels = new ArrayList<>();

        if (root == null)
            return levels;

        ArrayDeque<TreeNode> nextLevel = new ArrayDeque<>();
        nextLevel.offer(root);

        ArrayDeque<TreeNode> currLevel;

        while (!nextLevel.isEmpty()) {

            currLevel = new ArrayDeque<>(nextLevel);
            nextLevel.clear();

            levels.add(new ArrayList<>());

            for (TreeNode node : currLevel) {

                levels.get(levels.size() - 1).add(node.val);

                if (node.left != null)
                    nextLevel.offer(node.left);

                if (node.right != null)
                    nextLevel.offer(node.right);
            }
        }

        Collections.reverse(levels);

        return levels;
    }
}
```

---

# Complexity Analysis

### Time Complexity

```
O(N)
```

Every node is visited exactly once.

### Space Complexity

```
O(N)
```

Space is required for:

- queue
- output structure

---

# Summary

| Approach | Technique             | Time | Space |
| -------- | --------------------- | ---- | ----- |
| DFS      | Recursive preorder    | O(N) | O(N)  |
| BFS      | Level order traversal | O(N) | O(N)  |

---

# Key Insight

Both approaches naturally produce:

```
Top → Bottom traversal
```

The final step is simply:

```
Reverse(levels)
```

to produce:

```
Bottom → Top traversal
```
