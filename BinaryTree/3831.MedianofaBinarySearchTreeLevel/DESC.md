# 3831. Median of a Binary Search Tree Level

## Problem

You are given the root of a **Binary Search Tree (BST)** and an integer `level`.

- The **root node is at level 0**.
- Each level represents the **distance from the root**.

Your task is to **return the median value of all node values present at the given level**.

---

## Median Definition

The **median** is defined as:

- The **middle element** after sorting the values at that level in **non-decreasing order**.
- If the number of elements is **even**, return the **upper median** (the larger of the two middle values).

---

## Edge Case

Return:

```
-1
```

if:

- the specified level **does not exist**, or
- **no nodes** are present at that level.

---

# Example 1

### Input

```
root = [4,null,5,null,7]
level = 2
```

### Output

```
7
```

### Explanation

Nodes at level 2:

```
[7]
```

Sorted:

```
[7]
```

Median:

```
7
```

---

# Example 2

### Input

```
root = [6,3,8]
level = 1
```

### Output

```
8
```

### Explanation

Nodes at level 1:

```
[3, 8]
```

Sorted:

```
[3, 8]
```

Two medians exist:

```
3 and 8
```

Return the **upper median**:

```
8
```

---

# Example 3

### Input

```
root = [2,1]
level = 2
```

### Output

```
-1
```

### Explanation

There are **no nodes at level 2**, so the result is:

```
-1
```

---

# Constraints

```
1 ≤ number of nodes ≤ 2 * 10^5
1 ≤ Node.val ≤ 10^6
0 ≤ level ≤ 2 * 10^5
```

---

# Key Observations

1. The BST property **does not help directly**, since we only care about nodes at a specific depth.
2. The problem reduces to:
   - Collect all node values at the target level.
   - Sort them.
   - Return the median.
3. The most natural traversal method is **Level Order Traversal (BFS)**.

---

# High-Level Strategy

1. Perform **Breadth-First Search (BFS)**.
2. Stop when the desired level is reached.
3. Collect all node values at that level.
4. Sort the values.
5. Return the **upper median**.

---

# Time Complexity

```
O(n + k log k)
```

Where:

- `n` = total nodes in tree (for traversal)
- `k` = nodes at the target level

---

# Space Complexity

```
O(w + k)
```

Where:

- `w` = maximum width of the tree
- `k` = number of nodes at the requested level
