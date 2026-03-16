# 694. Number of Distinct Islands

You are given an **m x n binary matrix** `grid`.

An **island** is a group of `1`s (representing land) connected **4-directionally** (horizontal or vertical).

You may assume that **all four edges of the grid are surrounded by water**.

Two islands are considered the **same** if and only if **one island can be translated (shifted)** to equal the other.

**Rotation and reflection are NOT allowed.**

Return the **number of distinct islands**.

---

# Examples

## Example 1

**Input**

```
grid = [
  [1,1,0,0,0],
  [1,1,0,0,0],
  [0,0,0,1,1],
  [0,0,0,1,1]
]
```

**Output**

```
1
```

---

## Example 2

**Input**

```
grid = [
  [1,1,0,1,1],
  [1,0,0,0,0],
  [0,0,0,0,1],
  [1,1,0,1,1]
]
```

**Output**

```
3
```

---

# Constraints

```
m == grid.length
n == grid[i].length
1 <= m, n <= 50
grid[i][j] is either 0 or 1
```
