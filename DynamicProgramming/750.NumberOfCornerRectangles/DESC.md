# 750. Number Of Corner Rectangles

## Problem Description

Given an **m x n binary matrix `grid`** (containing only `0` or `1`), return the **number of corner rectangles**.

A **corner rectangle** is defined as four distinct `1`s in the grid that form the corners of an **axis-aligned rectangle**.

Important notes:

- Only the **four corners must contain `1`**.
- The cells inside the rectangle **do not matter**.
- All four `1`s must be **distinct positions** in the grid.

---

## Definition

A valid rectangle requires:

```
grid[r1][c1] = 1
grid[r1][c2] = 1
grid[r2][c1] = 1
grid[r2][c2] = 1
```

Where:

```
r1 < r2
c1 < c2
```

This means:

- Two rows `r1` and `r2`
- Two columns `c1` and `c2`
- All four corner cells contain `1`

---

# Example 1

Input:

```
grid = [
 [1,0,0,1,0],
 [0,0,1,0,1],
 [0,0,0,1,0],
 [1,0,1,0,1]
]
```

Output:

```
1
```

Explanation:

The only rectangle is formed by the corners:

```
grid[1][2]
grid[1][4]
grid[3][2]
grid[3][4]
```

---

# Example 2

Input:

```
grid = [
 [1,1,1],
 [1,1,1],
 [1,1,1]
]
```

Output:

```
9
```

Explanation:

Possible rectangles:

- Four **2×2 rectangles**
- Four **2×3 or 3×2 rectangles**
- One **3×3 rectangle**

Total:

```
4 + 4 + 1 = 9
```

---

# Example 3

Input:

```
grid = [[1,1,1,1]]
```

Output:

```
0
```

Explanation:

A rectangle requires **two distinct rows**.

Since there is only **one row**, no rectangle can be formed.

---

# Constraints

```
m == grid.length
n == grid[i].length
1 <= m, n <= 200
grid[i][j] is either 0 or 1
The number of 1's in the grid is between 1 and 6000
```

---
