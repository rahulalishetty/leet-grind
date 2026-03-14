# 1372. Longest ZigZag Path in a Binary Tree

## Problem

You are given the **root of a binary tree**.

A **ZigZag path** in a binary tree is defined as follows:

1. Choose any node in the binary tree and a direction (**left** or **right**).
2. If the current direction is **right**, move to the **right child**; otherwise move to the **left child**.
3. Change the direction (**right → left** or **left → right**).
4. Repeat steps 2 and 3 until you cannot move further in the tree.

The **ZigZag length** is defined as:

```
number of nodes visited − 1
```

A single node therefore has a ZigZag length of **0**.

Return the **length of the longest ZigZag path** in the tree.

---

# Example 1

### Input

```
root = [1,null,1,1,1,null,null,1,1,null,1,null,null,null,1]
```

### Output

```
3
```

### Explanation

The longest ZigZag path follows:

```
right → left → right
```

---

# Example 2

### Input

```
root = [1,1,1,null,1,null,null,1,1,null,1]
```

### Output

```
4
```

### Explanation

The longest ZigZag path follows:

```
left → right → left → right
```

---

# Example 3

### Input

```
root = [1]
```

### Output

```
0
```

---

# Constraints

```
1 ≤ number of nodes ≤ 5 × 10^4
1 ≤ Node.val ≤ 100
```
