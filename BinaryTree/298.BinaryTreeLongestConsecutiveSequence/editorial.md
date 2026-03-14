# 298. Binary Tree Longest Consecutive Sequence — Solutions

## Approach 1: Top-Down Depth First Search (DFS)

### Algorithm

This approach uses a **top-down DFS traversal**, similar to **preorder traversal**.

Key idea:

- Maintain a variable `length` representing the current consecutive sequence length.
- Compare each node with its parent.
- If the sequence continues (`node.val == parent.val + 1`), increment the length.
- Otherwise, reset the length to `1`.
- Track the global maximum length during traversal.

---

### Java Implementation

```java
private int maxLength = 0;

public int longestConsecutive(TreeNode root) {
    dfs(root, null, 0);
    return maxLength;
}

private void dfs(TreeNode p, TreeNode parent, int length) {
    if (p == null) return;

    length = (parent != null && p.val == parent.val + 1) ? length + 1 : 1;

    maxLength = Math.max(maxLength, length);

    dfs(p.left, p, length);
    dfs(p.right, p, length);
}
```

---

### Alternative Implementation

```java
public int longestConsecutive(TreeNode root) {
    return dfs(root, null, 0);
}

private int dfs(TreeNode p, TreeNode parent, int length) {
    if (p == null) return length;

    length = (parent != null && p.val == parent.val + 1) ? length + 1 : 1;

    return Math.max(
        length,
        Math.max(
            dfs(p.left, p, length),
            dfs(p.right, p, length)
        )
    );
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(n)
```

Every node in the tree is visited exactly once.

**Space Complexity**

```
O(n)
```

Due to recursion stack depth in the worst case (skewed tree).

---

# Approach 2: Bottom-Up Depth First Search

### Algorithm

This approach is similar to **post-order traversal**.

Key idea:

- Compute the longest consecutive sequence **starting from children and going upward**.
- Each node returns the length of the consecutive path that **starts at that node**.
- Parent nodes check whether they can extend the child's consecutive sequence.

Steps:

1. Recursively compute the consecutive sequence length from left and right children.
2. If the child value is not `node.val + 1`, reset the length to `1`.
3. Update the global maximum length.
4. Return the longest path from this node.

---

### Java Implementation

```java
private int maxLength = 0;

public int longestConsecutive(TreeNode root) {
    dfs(root);
    return maxLength;
}

private int dfs(TreeNode p) {

    if (p == null) return 0;

    int L = dfs(p.left) + 1;
    int R = dfs(p.right) + 1;

    if (p.left != null && p.val + 1 != p.left.val) {
        L = 1;
    }

    if (p.right != null && p.val + 1 != p.right.val) {
        R = 1;
    }

    int length = Math.max(L, R);

    maxLength = Math.max(maxLength, length);

    return length;
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(n)
```

Each node is visited once during traversal.

**Space Complexity**

```
O(n)
```

Recursion stack depth may reach `n` in skewed trees.
