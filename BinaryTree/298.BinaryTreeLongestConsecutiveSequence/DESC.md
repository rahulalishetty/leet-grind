# 298. Binary Tree Longest Consecutive Sequence

Given the root of a binary tree, return the length of the **longest consecutive sequence path**.

A **consecutive sequence path** is a path where the values increase by **exactly one** along the path.

Notes:

- The path can **start at any node** in the tree.
- The path **must move downward** (parent → child).
- You **cannot move from a node to its parent**.

---

## Example 1

Input:

```
root = [1,null,3,2,4,null,null,null,5]
```

Output:

```
3
```

Explanation:

The longest consecutive sequence path is:

```
3 → 4 → 5
```

Length = **3**

---

## Example 2

Input:

```
root = [2,null,3,2,null,1]
```

Output:

```
2
```

Explanation:

The longest consecutive sequence path is:

```
2 → 3
```

The sequence `3 → 2 → 1` is **not valid** because the values must **increase by 1**.

---

## Constraints

- The number of nodes in the tree is in the range:

```
1 ≤ n ≤ 3 × 10^4
```

- Node values range:

```
-3 × 10^4 ≤ Node.val ≤ 3 × 10^4
```
