# 124. Binary Tree Maximum Path Sum

## Problem

A **path** in a binary tree is a sequence of nodes where each pair of adjacent nodes has an edge connecting them.

Rules for a path:

- A node can appear **only once** in the path.
- The path **does not need to pass through the root**.
- The path must contain **at least one node**.

The **path sum** is the sum of the values of the nodes in the path.

Given the `root` of a binary tree, return the **maximum path sum** of any **non‑empty path**.

---

# Examples

## Example 1

**Input**

```
root = [1,2,3]
```

**Output**

```
6
```

**Explanation**

The optimal path is:

```
2 → 1 → 3
```

Path sum:

```
2 + 1 + 3 = 6
```

---

## Example 2

**Input**

```
root = [-10,9,20,null,null,15,7]
```

**Output**

```
42
```

**Explanation**

The optimal path is:

```
15 → 20 → 7
```

Path sum:

```
15 + 20 + 7 = 42
```

---

# Constraints

- Number of nodes in the tree: **[1, 3 × 10⁴]**
- Node value range: **-1000 ≤ Node.val ≤ 1000**
