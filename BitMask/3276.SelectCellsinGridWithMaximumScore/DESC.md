# 3276. Select Cells in Grid With Maximum Score

## Problem Statement

You are given a **2D matrix `grid`** consisting of **positive integers**.

You must select **one or more cells** from the matrix such that the following conditions hold:

1. **No two selected cells are from the same row.**
2. **All selected cell values must be unique.**

Your **score** is defined as the **sum of the values** of the selected cells.

Return the **maximum score** you can achieve.

---

# Example 1

## Input

```
grid = [[1,2,3],
        [4,3,2],
        [1,1,1]]
```

## Output

```
8
```

## Explanation

One optimal selection is:

```
1 (row 0), 3 (row 1), 4 (row 1)
```

But respecting row constraints, the valid optimal choice is selecting:

```
1, 3, 4
```

Their sum is:

```
1 + 3 + 4 = 8
```

---

# Example 2

## Input

```
grid = [[8,7,6],
        [8,3,2]]
```

## Output

```
15
```

## Explanation

Select:

```
7 from row 0
8 from row 1
```

Sum:

```
7 + 8 = 15
```

---

# Constraints

```
1 <= grid.length <= 10
1 <= grid[i].length <= 10
1 <= grid[i][j] <= 100
```

---

# Key Observations

- Only **one cell per row** can be chosen.
- All **selected values must be distinct**.
- We aim to **maximize the total sum**.
- Grid size is small (maximum **10 × 10**), which suggests solutions using:
  - Backtracking
  - Bitmask Dynamic Programming
  - Value-based state compression

---

# Summary

To solve the problem:

1. Choose at most **one value from each row**.
2. Ensure **all chosen values are unique**.
3. Maximize the **sum of selected values**.

The goal is to determine the **optimal subset of cells** satisfying these constraints.
