# 501. Find Mode in Binary Search Tree

Given the root of a **Binary Search Tree (BST)** that may contain **duplicate values**, return **all mode(s)** of the tree.

A **mode** is the value that appears **most frequently** in the tree.

If there are multiple modes, return **all of them in any order**.

---

## Definition of a BST

A Binary Search Tree follows these rules:

- The **left subtree** contains nodes with values **less than or equal to** the current node.
- The **right subtree** contains nodes with values **greater than or equal to** the current node.
- Both the **left and right subtrees** must also be valid BSTs.

---

## Example 1

**Input**

```
root = [1,null,2,2]
```

**Output**

```
[2]
```

**Explanation**

The value `2` appears **twice**, which is the highest frequency.

---

## Example 2

**Input**

```
root = [0]
```

**Output**

```
[0]
```

---

## Constraints

```
1 <= number of nodes <= 10^4
-10^5 <= Node.val <= 10^5
```

---

## Follow-up

Can you solve the problem **without using extra space**?

Note:

The **implicit recursion stack space** does **not count as extra space**.
