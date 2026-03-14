# 742. Closest Leaf in a Binary Tree

## Problem

Given the **root of a binary tree** where every node has a **unique value**, and a **target value `k`**, return the value of the **closest leaf node** to the node with value `k`.

A **leaf node** is defined as a node that **has no children**.

The **distance** between nodes is measured as the **number of edges traveled**.

---

## Definition

A leaf node is the **nearest** if the number of edges traveled from the node with value `k` to the leaf is **minimum**.

---

## Example 1

Input

```
root = [1,3,2]
k = 1
```

Output

```
2
```

Explanation

```
    1
   / \\
  3   2
```

Both `2` and `3` are leaves and are at distance **1** from node `1`. Either can be returned.

---

## Example 2

Input

```
root = [1]
k = 1
```

Output

```
1
```

Explanation

The root node itself is the **only leaf node**, so the result is `1`.

---

## Example 3

Input

```
root = [1,2,3,4,null,null,null,5,null,6]
k = 2
```

Output

```
3
```

Explanation

The leaf node `3` is closer to node `2` than leaf node `6`.

---

## Constraints

```
1 <= number of nodes <= 1000
1 <= Node.val <= 1000
```

Additional guarantees:

- All node values are **unique**
- There exists a node where `Node.val == k`
