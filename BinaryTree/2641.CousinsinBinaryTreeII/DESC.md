# 2641. Cousins in Binary Tree II

## Problem

You are given the **root of a binary tree**.

Replace the value of each node with the **sum of all its cousins' values**.

Two nodes are considered **cousins** if:

- They are at the **same depth**
- They have **different parents**

Return the **root of the modified tree**.

---

## Definition

The **depth** of a node is the number of edges from the root to that node.

---

# Example 1

### Input

```
root = [5,4,9,1,10,null,7]
```

### Output

```
[0,0,0,7,7,null,11]
```

### Explanation

Initial tree:

```
        5
      /   \\
     4     9
    / \\     \\
   1  10     7
```

Modified tree:

```
        0
      /   \\
     0     0
    / \\     \\
   7   7     11
```

Explanation per node:

- **5** has no cousins → `0`
- **4** has no cousins → `0`
- **9** has no cousins → `0`
- **1** has cousin `7` → `7`
- **10** has cousin `7` → `7`
- **7** has cousins `1` and `10` → `11`

---

# Example 2

### Input

```
root = [3,1,2]
```

### Output

```
[0,0,0]
```

### Explanation

Tree:

```
    3
   / \\
  1   2
```

No nodes have cousins.

All values become:

```
[0,0,0]
```

---

# Constraints

```
1 ≤ number of nodes ≤ 10^5
1 ≤ Node.val ≤ 10^4
```
