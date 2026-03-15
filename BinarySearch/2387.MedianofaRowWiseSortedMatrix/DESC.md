# 2387. Median of a Row Wise Sorted Matrix

## Problem Description

You are given an **m x n matrix** `grid` that contains an **odd number of integers**.

Properties of the matrix:

- Each **row is sorted in non‑decreasing order**
- The total number of elements (`m * n`) is **odd**

Your task is to **return the median of the matrix**.

---

## Important Constraint

You must solve the problem in **less than**:

```
O(m * n)
```

time complexity.

---

# Example 1

Input

```
grid = [[1,1,2],
        [2,3,3],
        [1,3,4]]
```

Output

```
2
```

Explanation

All elements in sorted order:

```
1,1,1,2,2,3,3,3,4
```

The **median** is:

```
2
```

---

# Example 2

Input

```
grid = [[1,1,3,3,4]]
```

Output

```
3
```

Explanation

All elements in sorted order:

```
1,1,3,3,4
```

The **median** is:

```
3
```

---

# Constraints

```
m == grid.length
n == grid[i].length
```

```
1 <= m, n <= 500
```

```
m and n are both odd
```

```
1 <= grid[i][j] <= 10^6
```

```
grid[i] is sorted in non-decreasing order
```
