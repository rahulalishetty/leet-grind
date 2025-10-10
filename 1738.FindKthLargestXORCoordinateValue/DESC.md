# 1738. Find Kth Largest XOR Coordinate Value

`Medium`

You are given an `m x n` matrix of non-negative integers and an integer `k`.

The value at coordinate `(a, b)` is defined as the XOR of all `matrix[i][j]` where `0 <= i <= a < m` and `0 <= j <= b < n` (0-indexed).

Your task is to find the k-th largest value (1-indexed) among all coordinates.

## Examples

**Example 1:**

```note
Input: matrix = [[5,2],[1,6]], k = 1
Output: 7
Explanation: The value at (0,1) is 5 XOR 2 = 7, which is the largest.
```

**Example 2:**

```note
Input: matrix = [[5,2],[1,6]], k = 2
Output: 5
Explanation: The value at (0,0) is 5, which is the 2nd largest.
```

**Example 3:**

```note
Input: matrix = [[5,2],[1,6]], k = 3
Output: 4
Explanation: The value at (1,0) is 5 XOR 1 = 4, which is the 3rd largest.
```

## Constraints

- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 1000`
- `0 <= matrix[i][j] <= 10^6`
- `1 <= k <= m * n`
