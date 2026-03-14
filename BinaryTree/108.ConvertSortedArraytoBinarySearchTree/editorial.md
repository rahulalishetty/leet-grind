# Convert Sorted Array to Height-Balanced BST — Detailed Explanation

## Tree Traversal Strategies

There are two general strategies to traverse a tree.

---

# 1. Depth First Search (DFS)

In **DFS**, we prioritize depth.

Traversal pattern:

```
Root → go deep into a branch → reach leaf → backtrack → explore next branch
```

DFS has three main variants depending on node order.

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

In **BFS**, we traverse the tree **level by level**.

Traversal pattern:

```
Level 0 → Level 1 → Level 2 → ...
```

Nodes at higher levels are visited before nodes at lower levels.

---

# Construct BST from Inorder Traversal

It is known that:

```
Inorder traversal of a BST = sorted array
```

Therefore this problem can be interpreted as:

```
Construct BST from sorted inorder traversal
```

---

# Is the Solution Unique?

No.

A sorted array does **not uniquely determine a BST**.

Many different BSTs can produce the same inorder traversal.

Example:

```
inorder = sorted(preorder)
inorder = sorted(postorder)
```

Both preorder and postorder uniquely identify a BST when combined with inorder, but **inorder alone is insufficient**.

Therefore:

```
sorted array → BST
```

has **multiple valid solutions**.

---

# Height Balanced Restriction

The tree must be **height-balanced**.

Definition:

```
| height(left subtree) − height(right subtree) | ≤ 1
```

This restriction still does **not guarantee a unique solution**.

For even-length arrays we can choose:

```
left middle element
or
right middle element
```

Both produce valid height-balanced BSTs.

---

# Approach 1: Always Choose Left Middle Element

## Idea

Pick the **left middle element** as the root.

This ensures the tree remains balanced.

## Algorithm

Define:

```
helper(left, right)
```

Steps:

1. If `left > right` → return null
2. Pick middle index

```
p = (left + right) / 2
```

3. Create root node
4. Recursively build left subtree
5. Recursively build right subtree

---

## Java Implementation

```java
class Solution {

    int[] nums;

    public TreeNode helper(int left, int right) {

        if (left > right)
            return null;

        int p = (left + right) / 2;

        TreeNode root = new TreeNode(nums[p]);

        root.left = helper(left, p - 1);
        root.right = helper(p + 1, right);

        return root;
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        this.nums = nums;
        return helper(0, nums.length - 1);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Every element becomes exactly one node.

### Space Complexity

```
O(log N)
```

Because recursion depth equals tree height.

Since the tree is balanced:

```
height ≈ log N
```

---

# Approach 2: Always Choose Right Middle Element

## Idea

Choose the **right middle element** as the root.

This produces a different but still valid balanced BST.

## Algorithm

```
p = (left + right) / 2
if (left + right) is odd
    p = p + 1
```

---

## Java Implementation

```java
class Solution {

    int[] nums;

    public TreeNode helper(int left, int right) {

        if (left > right)
            return null;

        int p = (left + right) / 2;

        if ((left + right) % 2 == 1)
            p++;

        TreeNode root = new TreeNode(nums[p]);

        root.left = helper(left, p - 1);
        root.right = helper(p + 1, right);

        return root;
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        this.nums = nums;
        return helper(0, nums.length - 1);
    }
}
```

---

# Approach 3: Random Middle Element

## Idea

Instead of a fixed rule, randomly choose between:

```
left middle
right middle
```

Each run produces a different valid BST.

---

## Java Implementation

```java
class Solution {

    int[] nums;
    Random rand = new Random();

    public TreeNode helper(int left, int right) {

        if (left > right)
            return null;

        int p = (left + right) / 2;

        if ((left + right) % 2 == 1)
            p += rand.nextInt(2);

        TreeNode root = new TreeNode(nums[p]);

        root.left = helper(left, p - 1);
        root.right = helper(p + 1, right);

        return root;
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        this.nums = nums;
        return helper(0, nums.length - 1);
    }
}
```

---

# Complexity Analysis

### Time Complexity

```
O(N)
```

Every element is visited once.

### Space Complexity

```
O(log N)
```

Recursion stack height equals the tree height.

Balanced BST height:

```
≈ log N
```

---

# Summary

| Approach      | Root Selection        | Result       |
| ------------- | --------------------- | ------------ |
| Left Middle   | `(l+r)/2`             | Balanced BST |
| Right Middle  | `(l+r)/2 + 1` if even | Balanced BST |
| Random Middle | random choice         | Balanced BST |

All approaches produce **valid height-balanced BSTs**, but the exact tree structure may differ.
