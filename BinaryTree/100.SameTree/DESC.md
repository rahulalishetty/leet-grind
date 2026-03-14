# 100. Same Tree

## Problem Description

Given the roots of two binary trees `p` and `q`, write a function to check if they are the **same tree**.

Two binary trees are considered the same if:

1. They are **structurally identical**
2. The nodes have the **same values**

---

## Example 1

### Input

```
p = [1,2,3]
q = [1,2,3]
```

### Output

```
true
```

### Explanation

Both trees:

```
    1
   / \\
  2   3
```

They have the same structure and the same node values.

---

## Example 2

### Input

```
p = [1,2]
q = [1,null,2]
```

### Output

```
false
```

### Explanation

Trees:

```
Tree p:        Tree q:

   1              1
  /                \\
 2                  2
```

The structures are different.

---

## Example 3

### Input

```
p = [1,2,1]
q = [1,1,2]
```

### Output

```
false
```

### Explanation

Although the structures are identical, the node values differ.

---

## Constraints

```
0 <= number of nodes <= 100
-10^4 <= Node.val <= 10^4
```
