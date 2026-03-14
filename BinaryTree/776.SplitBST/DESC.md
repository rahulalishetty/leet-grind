# 776. Split BST

## Problem

Given the **root of a Binary Search Tree (BST)** and an integer **target**, split the tree into **two subtrees**:

1. The first subtree contains all nodes with values **≤ target**.
2. The second subtree contains all nodes with values **> target**.

The original **BST structure should be preserved as much as possible**.

More formally:

If a node `c` had parent `p` in the original tree, and **both remain in the same resulting subtree**, then `c` should still have `p` as its parent.

Return an array containing the **roots of the two resulting trees**.

```
result[0] → tree with values ≤ target
result[1] → tree with values > target
```

---

# Example 1

Input

```
root = [4,2,6,1,3,5,7]
target = 2
```

Output

```
[[2,1],[4,3,6,null,null,5,7]]
```

Explanation

Original tree

```
        4
       / \\
      2   6
     / \\ / \\
    1  3 5  7
```

After split at `target = 2`

Tree 1 (≤2)

```
   2
  /
 1
```

Tree 2 (>2)

```
      4
     / \\
    3   6
       / \\
      5   7
```

---

# Example 2

Input

```
root = [1]
target = 1
```

Output

```
[[1],[]]
```

Explanation

All nodes are ≤ target, so the second tree is empty.

---

# Constraints

```
1 ≤ number of nodes ≤ 50
0 ≤ Node.val ≤ 1000
0 ≤ target ≤ 1000
```

Additional guarantees:

- The input tree is a **valid Binary Search Tree**
