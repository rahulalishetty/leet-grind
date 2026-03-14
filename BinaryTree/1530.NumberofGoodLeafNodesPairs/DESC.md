# 1530. Number of Good Leaf Nodes Pairs

## Problem

You are given the **root of a binary tree** and an integer **distance**.

A pair of **two different leaf nodes** of the binary tree is considered **good** if the **length of the shortest path between them is less than or equal to `distance`**.

Your task is to **return the number of such good leaf node pairs**.

---

## Definition

A **leaf node** is a node that has **no children**.

The **distance between two nodes** is defined as the **number of edges in the shortest path connecting them**.

---

# Example 1

### Input

```
root = [1,2,3,null,4]
distance = 3
```

### Output

```
1
```

### Explanation

Leaf nodes are:

```
3 and 4
```

Shortest path:

```
3 → 1 → 2 → 4
```

Distance:

```
3
```

Since `3 ≤ distance`, the pair is **good**.

---

# Example 2

### Input

```
root = [1,2,3,4,5,6,7]
distance = 3
```

### Output

```
2
```

### Explanation

Good pairs:

```
[4,5]  → distance = 2
[6,7]  → distance = 2
```

Pair `[4,6]` is **not good** because:

```
distance = 4
```

---

# Example 3

### Input

```
root = [7,1,4,6,null,5,3,null,null,null,null,null,2]
distance = 3
```

### Output

```
1
```

### Explanation

The only good pair:

```
[2,5]
```

---

# Constraints

```
Number of nodes in the tree: 1 ≤ n ≤ 2^10
1 ≤ Node.val ≤ 100
1 ≤ distance ≤ 10
```
