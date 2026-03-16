# 741. Cherry Pickup

## Problem Description

You are given an **n x n grid** representing a field of cherries.
Each cell in the grid can contain one of three values:

- `0` → The cell is empty and can be passed through.
- `1` → The cell contains a cherry that can be picked up.
- `-1` → The cell contains a thorn and cannot be passed through.

Your goal is to collect the **maximum number of cherries** while following these rules.

---

## Movement Rules

1. Start at position **(0, 0)**.
2. Move to **(n-1, n-1)** by moving **right** or **down** only.
3. After reaching **(n-1, n-1)**, return to **(0, 0)** by moving **left** or **up** only.
4. When you pass through a cell containing a cherry (`1`), you **pick it up**, and the cell becomes `0`.
5. Cells with `-1` are **blocked** and cannot be used.
6. If there is **no valid path** from `(0,0)` to `(n-1,n-1)`, the result is **0**.

---

## Goal

Return the **maximum number of cherries** that can be collected.

---

## Example 1

### Input

```
grid = [[0,1,-1],
        [1,0,-1],
        [1,1,1]]
```

### Output

```
5
```

### Explanation

Forward trip:

```
(0,0) → (1,0) → (2,0) → (2,1) → (2,2)
```

Cherries collected: **4**

Grid after picking cherries:

```
[[0,1,-1],
 [0,0,-1],
 [0,0,0]]
```

Return trip:

```
(2,2) → (2,1) → (1,1) → (0,1) → (0,0)
```

One additional cherry collected.

Total cherries collected:

```
4 + 1 = 5
```

---

## Example 2

### Input

```
grid = [[1,1,-1],
        [1,-1,1],
        [-1,1,1]]
```

### Output

```
0
```

### Explanation

There is **no valid path** from `(0,0)` to `(2,2)` due to thorns (`-1`), so no cherries can be collected.

---

## Constraints

```
n == grid.length
n == grid[i].length
1 <= n <= 50
grid[i][j] ∈ {-1, 0, 1}
grid[0][0] != -1
grid[n-1][n-1] != -1
```

---
