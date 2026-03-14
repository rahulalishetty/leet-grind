# 1602. Find Nearest Right Node in Binary Tree

## Problem

Given the root of a binary tree and a node `u` in the tree, return the **nearest node on the same level that is to the right of `u`**.

If `u` is the **rightmost node in its level**, return `null`.

---

## Example 1

![alt text](image.png)

**Input**

```
root = [1,2,3,null,4,5,6]
u = 4
```

**Output**

```
5
```

**Explanation**

The nearest node on the same level to the right of node `4` is node `5`.

---

## Example 2

**Input**

```
root = [3,null,4,2]
u = 2
```

**Output**

```
null
```

**Explanation**

There are no nodes to the right of `2` on the same level.

---

## Constraints

```
1 <= number of nodes <= 100000
1 <= Node.val <= 100000
```

Additional guarantees:

- All node values in the tree are **distinct**
- `u` is guaranteed to exist in the tree rooted at `root`
