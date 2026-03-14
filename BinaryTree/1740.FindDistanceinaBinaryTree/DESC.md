# 1740. Find Distance in a Binary Tree

## Problem

You are given:

- The **root of a binary tree**
- Two integers **p** and **q** representing values of nodes in the tree

Your task is to return the **distance between the nodes with values `p` and `q`**.

---

## Definition

The **distance between two nodes** is defined as:

> The number of **edges** on the path between the two nodes.

---

# Example 1

![alt text](image.png)

### Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
p = 5
q = 0
```

### Output

```
3
```

### Explanation

The path between `5` and `0` is:

```
5 → 3 → 1 → 0
```

Number of edges:

```
3
```

---

# Example 2

![alt text](image-1.png)

### Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
p = 5
q = 7
```

### Output

```
2
```

### Explanation

The path between `5` and `7` is:

```
5 → 2 → 7
```

Number of edges:

```
2
```

---

# Example 3

![alt text](image-2.png)

### Input

```
root = [3,5,1,6,2,0,8,null,null,7,4]
p = 5
q = 5
```

### Output

```
0
```

### Explanation

Distance from a node to itself is **0**.

---

# Constraints

```
1 <= number of nodes <= 10^4
0 <= Node.val <= 10^9
```

Additional guarantees:

- All node values are **unique**
- `p` and `q` **exist in the tree**
