# 2196. Create Binary Tree From Descriptions

## Problem

You are given a **2D integer array `descriptions`** where:

```
descriptions[i] = [parent_i, child_i, isLeft_i]
```

This indicates that:

- `parent_i` is the **parent node**
- `child_i` is the **child node**
- `isLeft_i` specifies whether the child is on the left or right side

### Rules

- If `isLeft_i == 1`, then `child_i` is the **left child** of `parent_i`.
- If `isLeft_i == 0`, then `child_i` is the **right child** of `parent_i`.

All node values in the tree are **unique**.

Your task is to:

```
Construct the binary tree described by descriptions and return its root.
```

The input guarantees that the described binary tree is **valid**.

---

# Example 1

### Input

```
descriptions = [[20,15,1],
                [20,17,0],
                [50,20,1],
                [50,80,0],
                [80,19,1]]
```

### Output

```
[50,20,80,15,17,19]
```

### Explanation

The node with value **50** has **no parent**, therefore it is the **root**.

The resulting tree structure:

```
        50
       /  \
     20    80
    /  \   /
   15  17 19
```

---

# Example 2

### Input

```
descriptions = [[1,2,1],
                [2,3,0],
                [3,4,1]]
```

### Output

```
[1,2,null,null,3,4]
```

### Explanation

Node **1** has no parent, so it becomes the **root**.

Tree structure:

```
1
/
2
 \\
  3
 /
4
```

---

# Constraints

```
1 ≤ descriptions.length ≤ 10^4
descriptions[i].length = 3
1 ≤ parent_i, child_i ≤ 10^5
0 ≤ isLeft_i ≤ 1
```

Additional guarantees:

- All node values are **unique**
- The binary tree described by `descriptions` is **valid**
