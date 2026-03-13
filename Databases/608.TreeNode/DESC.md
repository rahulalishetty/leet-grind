# 608. Tree Node

## Table: Tree

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| p_id        | int  |

- `id` contains **unique values**.
- Each row represents a **node in a tree**.
- `p_id` represents the **parent node id**.
- If `p_id` is `NULL`, the node has **no parent** and is the **root**.

The given structure is always a **valid tree**.

---

# Problem

Each node in the tree can be classified into one of three types:

| Type  | Meaning                                             |
| ----- | --------------------------------------------------- |
| Root  | Node whose `p_id` is `NULL`                         |
| Inner | Node that has both a **parent** and **child nodes** |
| Leaf  | Node that has a **parent** but **no children**      |

Write a SQL query to **report the type of each node** in the tree.

Return the result in **any order**.

---

# Example 1

## Input

### Tree table

| id  | p_id |
| --- | ---- |
| 1   | null |
| 2   | 1    |
| 3   | 1    |
| 4   | 2    |
| 5   | 2    |

---

## Output

| id  | type  |
| --- | ----- |
| 1   | Root  |
| 2   | Inner |
| 3   | Leaf  |
| 4   | Leaf  |
| 5   | Leaf  |

---

## Explanation

Tree structure:

```
      1
     / \\
    2   3
   / \\
  4   5
```

Classification:

- **Node 1**
  - `p_id = NULL`
  - It has children (2 and 3)
    → **Root**

- **Node 2**
  - Has parent `1`
  - Has children `4` and `5`
    → **Inner**

- **Nodes 3, 4, 5**
  - Have parents
  - Have **no children**
    → **Leaf**

---

# Example 2

## Input

### Tree table

| id  | p_id |
| --- | ---- |
| 1   | null |

---

## Output

| id  | type |
| --- | ---- |
| 1   | Root |

---

## Explanation

If the tree contains **only one node**, it is automatically the **Root**.

---

## Note

This question is the same as:

**LeetCode 3054 — Binary Tree Nodes**
