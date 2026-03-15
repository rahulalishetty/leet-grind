# 1428. Leftmost Column with at Least a One

## Problem Description

A **row-sorted binary matrix** means:

- All elements are either `0` or `1`
- Each row is sorted in **non-decreasing order**

Given a row-sorted binary matrix `binaryMatrix`, return the **index (0-indexed)** of the **leftmost column that contains at least one `1`**.

If no such column exists, return:

```
-1
```

---

## Access Restrictions

You **cannot access the matrix directly**.

Instead, you must use the `BinaryMatrix` interface:

```
BinaryMatrix.get(row, col)
```

Returns the element at `(row, col)`.

```
BinaryMatrix.dimensions()
```

Returns the dimensions of the matrix:

```
[rows, cols]
```

---

## Important Constraint

You may **not call `BinaryMatrix.get` more than 1000 times**.

Any solution exceeding this limit will be judged **Wrong Answer**.

---

## Example 1

Input:

```
mat = [[0,0],
       [1,1]]
```

Output:

```
0
```

Explanation:

The first column already contains a `1`.

---

## Example 2

Input:

```
mat = [[0,0],
       [0,1]]
```

Output:

```
1
```

Explanation:

The leftmost column containing `1` is column `1`.

---

## Example 3

Input:

```
mat = [[0,0],
       [0,0]]
```

Output:

```
-1
```

Explanation:

There is **no column containing `1`**.

---

## Constraints

```
rows == mat.length
cols == mat[i].length
```

```
1 <= rows, cols <= 100
```

```
mat[i][j] ∈ {0, 1}
```

Each row is **sorted in non-decreasing order**.
