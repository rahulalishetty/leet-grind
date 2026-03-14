# 1644. Lowest Common Ancestor of a Binary Tree II

## Problem

Given the **root of a binary tree**, return the **lowest common ancestor (LCA)** of two given nodes `p` and `q`.

If **either `p` or `q` does not exist in the tree**, return **null**.

All values in the tree are **unique**.

---

## Definition

According to the definition of **Lowest Common Ancestor (LCA)**:

> The lowest common ancestor of two nodes `p` and `q` in a binary tree `T` is the **lowest node that has both `p` and `q` as descendants** (where a node can be a descendant of itself).

A **descendant** of node `x` is a node `y` that lies on the path from `x` to any leaf.

---

# Example 1

Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
p = 5
q = 1
```

Output

```
3
```

Explanation

The lowest common ancestor of nodes **5** and **1** is **3**.

---

# Example 2

Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
p = 5
q = 4
```

Output

```
5
```

Explanation

The LCA of nodes **5** and **4** is **5**.

A node can be a **descendant of itself** according to the LCA definition.

---

# Example 3

Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
p = 5
q = 10
```

Output

```
null
```

Explanation

Node **10** does not exist in the tree, so the result is **null**.

---

# Constraints

```
1 <= number of nodes <= 10^4
-10^9 <= Node.val <= 10^9
```

Additional guarantees:

- All node values are **unique**
- `p != q`

---

# Follow-up

Can you compute the **Lowest Common Ancestor** while **traversing the tree only once**, without separately checking whether nodes `p` and `q` exist in the tree?
