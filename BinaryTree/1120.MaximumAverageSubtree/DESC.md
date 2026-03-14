# 1120. Maximum Average Subtree

## Problem

Given the **root of a binary tree**, return the **maximum average value of any subtree** in the tree.

A **subtree** consists of a node and **all of its descendants**.

The **average value** of a subtree is:

```
average = (sum of all node values in subtree) / (number of nodes in subtree)
```

Answers within **10⁻⁵** of the correct value are accepted.

---

## Example 1

**Input**

```
root = [5,6,1]
```

**Output**

```
6.00000
```

**Explanation**

Subtrees:

```
Node 5 → (5 + 6 + 1) / 3 = 4
Node 6 → 6 / 1 = 6
Node 1 → 1 / 1 = 1
```

The **maximum average** is:

```
6
```

---

## Example 2

**Input**

```
root = [0,null,1]
```

**Output**

```
1.00000
```

---

## Constraints

```
1 ≤ number of nodes ≤ 10⁴
0 ≤ Node.val ≤ 10⁵
```

Additional guarantees:

- The input is a valid **binary tree**
- The result must be computed with precision **within 10⁻⁵**
