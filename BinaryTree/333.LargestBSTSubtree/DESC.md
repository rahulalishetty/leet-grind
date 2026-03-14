# 333. Largest BST Subtree

Given the root of a binary tree, find the **largest subtree that is also a Binary Search Tree (BST)**.
"**Largest**" means the subtree contains the **maximum number of nodes**.

A **Binary Search Tree (BST)** satisfies the following properties:

- All values in the **left subtree** are **less than** the parent node's value.
- All values in the **right subtree** are **greater than** the parent node's value.
- A subtree must include **all of its descendants**.

---

## Example 1

Input

```
root = [10,5,15,1,8,null,7]
```

Output

```
3
```

Explanation

The largest BST subtree contains **3 nodes**.

---

## Example 2

Input

```
root = [4,2,7,2,3,5,null,2,null,null,null,null,null,1]
```

Output

```
2
```

---

## Constraints

- The number of nodes in the tree is in the range:

```
0 ≤ n ≤ 10^4
```

- Node values range:

```
-10^4 ≤ Node.val ≤ 10^4
```

---

## Follow Up

Can you solve this problem in:

```
O(n)
```

time complexity?
