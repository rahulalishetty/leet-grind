# 111. Minimum Depth of Binary Tree

## Problem Description

Given the **root of a binary tree**, find its **minimum depth**.

The **minimum depth** is defined as the number of nodes along the **shortest path from the root node down to the nearest leaf node**.

A **leaf node** is a node that **has no children**.

---

## Example 1

### Input

```
root = [3,9,20,null,null,15,7]
```

### Output

```
2
```

### Explanation

Binary Tree:

```
        3
       / \\
      9   20
         /  \\
        15   7
```

The shortest path from root to a leaf is:

```
3 → 9
```

Depth = **2**

---

## Example 2

### Input

```
root = [2,null,3,null,4,null,5,null,6]
```

### Output

```
5
```

### Explanation

The tree is skewed:

```
2
 \
  3
   \
    4
     \
      5
       \
        6
```

The shortest path from root to leaf is:

```
2 → 3 → 4 → 5 → 6
```

Depth = **5**

---

## Constraints

```
0 <= number of nodes <= 10^5
-1000 <= Node.val <= 1000
```

---
