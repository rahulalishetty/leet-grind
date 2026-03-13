# 3054. Binary Tree Nodes

## Table: Tree

| Column Name | Type |
| ----------- | ---- |
| N           | int  |
| P           | int  |

**Notes:**

- `N` contains **unique values**.
- Each row represents a node in a **Binary Tree**.
- `N` = value of the node.
- `P` = parent node of `N`.

---

# Problem

Determine the **type of each node** in the binary tree.

Each node should be classified as one of the following:

| Type  | Description                                                |
| ----- | ---------------------------------------------------------- |
| Root  | The node that **has no parent**                            |
| Leaf  | The node that **has no children**                          |
| Inner | The node that **has both a parent and at least one child** |

---

# Task

Return a table containing:

| Column | Description                               |
| ------ | ----------------------------------------- |
| N      | Node value                                |
| Type   | Type of node (`Root`, `Leaf`, or `Inner`) |

---

# Requirements

- The result must be **ordered by `N` in ascending order**.

---

# Example

## Input

### Tree Table

| N   | P    |
| --- | ---- |
| 1   | 2    |
| 3   | 2    |
| 6   | 8    |
| 9   | 8    |
| 2   | 5    |
| 8   | 5    |
| 5   | null |

---

# Output

| N   | Type  |
| --- | ----- |
| 1   | Leaf  |
| 2   | Inner |
| 3   | Leaf  |
| 5   | Root  |
| 6   | Leaf  |
| 8   | Inner |
| 9   | Leaf  |

---

# Explanation

### Root Node

A **root node** has **no parent**.

```
Node 5
```

Because:

```
P = NULL
```

---

### Leaf Nodes

A **leaf node** has **no children**.

Nodes:

```
1, 3, 6, 9
```

These nodes **do not appear as parent values** in the table.

---

### Inner Nodes

An **inner node** has:

- a **parent**
- at least **one child**

Nodes:

```
2, 8
```

These nodes appear:

- as a **child (N)** with a parent
- and also appear as **parent (P)** of other nodes.
