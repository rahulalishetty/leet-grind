# 329. Longest Increasing Path in a Matrix

Given an `m x n` integers matrix, return the length of the longest increasing path in the matrix.

From each cell, you can move in four directions: left, right, up, or down. Diagonal moves or moves outside the boundary (i.e., wrap-around) are not allowed.

## Examples

### Example 1

**Input:**
`matrix = [[9,9,4],[6,6,8],[2,1,1]]`
**Output:**
`4`
**Explanation:**
The longest increasing path is `[1, 2, 6, 9]`.

### Example 2

**Input:**
`matrix = [[3,4,5],[3,2,6],[2,2,1]]`
**Output:**
`4`
**Explanation:**
The longest increasing path is `[3, 4, 5, 6]`. Diagonal moves are not allowed.

### Example 3

**Input:**
`matrix = [[1]]`
**Output:**
`1`

## Constraints

- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 200`
- `0 <= matrix[i][j] <= 2^31 - 1`
