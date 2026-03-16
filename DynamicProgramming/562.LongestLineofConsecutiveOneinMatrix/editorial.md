# 562. Longest Line of Consecutive One in Matrix — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

The problem is:

> Given an `m x n` binary matrix `mat`, return the length of the longest line of consecutive `1`s.

A line may be in any of these four directions:

- Horizontal
- Vertical
- Diagonal
- Anti-diagonal

---

# Problem Restatement

For every cell containing `1`, we want to know whether it can extend a line of consecutive `1`s in one of the allowed directions.

The answer is the maximum length among all such lines.

Because the line can go in four directions, we must account for all four while avoiding unnecessary repeated work.

---

# Approach 1: Brute Force

## Intuition

The brute-force idea is very direct:

- explicitly traverse every possible horizontal line
- explicitly traverse every possible vertical line
- explicitly traverse every possible diagonal
- explicitly traverse every possible anti-diagonal

While traversing each line:

- keep a running count of consecutive `1`s
- reset the count when a `0` is encountered
- update the global maximum whenever the count grows

This approach works because every possible valid line in the matrix belongs to one of those four direction groups.

---

## What Exactly Gets Traversed?

The brute force solution traverses the matrix in six phases:

1. All horizontal lines
2. All vertical lines
3. All upper diagonals
4. All lower diagonals
5. All upper anti-diagonals
6. All lower anti-diagonals

This is simply a way to ensure every diagonal and anti-diagonal is visited once.

---

## Algorithm

### Horizontal Traversal

For each row:

- scan from left to right
- count consecutive `1`s
- reset to `0` when a `0` appears

### Vertical Traversal

For each column:

- scan from top to bottom
- count consecutive `1`s
- reset when a `0` appears

### Diagonal Traversal

There are two groups of diagonals:

- diagonals starting on the first row
- diagonals starting on the first column

For each such starting point:

- move down-right
- count consecutive `1`s
- reset when a `0` appears

### Anti-diagonal Traversal

There are again two groups:

- anti-diagonals starting on the first row
- anti-diagonals starting on the last column

For each such starting point:

- move down-left
- count consecutive `1`s
- reset when a `0` appears

---

## Java Implementation

```java
class Solution {
  public int longestLine(int[][] M) {
    if (M.length == 0) return 0;
    int ones = 0;

    // horizontal
    for (int i = 0; i < M.length; i++) {
      int count = 0;
      for (int j = 0; j < M[0].length; j++) {
        if (M[i][j] == 1) {
          count++;
          ones = Math.max(ones, count);
        } else {
          count = 0;
        }
      }
    }

    // vertical
    for (int i = 0; i < M[0].length; i++) {
      int count = 0;
      for (int j = 0; j < M.length; j++) {
        if (M[j][i] == 1) {
          count++;
          ones = Math.max(ones, count);
        } else {
          count = 0;
        }
      }
    }

    // upper diagonal
    for (int i = 0; i < M[0].length || i < M.length; i++) {
      int count = 0;
      for (int x = 0, y = i; x < M.length && y < M[0].length; x++, y++) {
        if (M[x][y] == 1) {
          count++;
          ones = Math.max(ones, count);
        } else {
          count = 0;
        }
      }
    }

    // lower diagonal
    for (int i = 0; i < M[0].length || i < M.length; i++) {
      int count = 0;
      for (int x = i, y = 0; x < M.length && y < M[0].length; x++, y++) {
        if (M[x][y] == 1) {
          count++;
          ones = Math.max(ones, count);
        } else {
          count = 0;
        }
      }
    }

    // upper anti-diagonal
    for (int i = 0; i < M[0].length || i < M.length; i++) {
      int count = 0;
      for (int x = 0, y = M[0].length - i - 1; x < M.length && y >= 0; x++, y--) {
        if (M[x][y] == 1) {
          count++;
          ones = Math.max(ones, count);
        } else {
          count = 0;
        }
      }
    }

    // lower anti-diagonal
    for (int i = 0; i < M[0].length || i < M.length; i++) {
      int count = 0;
      for (int x = i, y = M[0].length - 1; x < M.length && y >= 0; x++, y--) {
        if (M[x][y] == 1) {
          count++;
          ones = Math.max(ones, count);
        } else {
          count = 0;
        }
      }
    }

    return ones;
  }
}
```

---

## Complexity Analysis

Let:

- `m` = number of rows
- `n` = number of columns

Then the total number of cells is:

```text
m * n
```

### Time Complexity

Although the implementation is split into multiple directional scans, each scan collectively covers the matrix in linear total work.

So the overall time complexity is:

```text
O(mn)
```

### Space Complexity

Only a few scalar variables are used:

```text
O(1)
```

---

## Drawback of the Brute Force Approach

Even though the asymptotic complexity is already linear, the matrix is traversed multiple times in different ways.

This makes the code longer and less elegant.

It also motivates looking for a solution that processes all directions in a **single pass**.

That leads to dynamic programming.

---

# Approach 2: Using 3D Dynamic Programming

## Intuition

Instead of traversing the same matrix repeatedly for different directions, we can compute all four directional answers while scanning the matrix once.

For each cell `(i, j)`, if it contains `1`, we want to know:

- how many consecutive `1`s end here horizontally
- how many end here vertically
- how many end here diagonally
- how many end here anti-diagonally

This suggests storing four values for every cell.

---

## DP Definition

Let:

```text
dp[i][j][0] = longest horizontal line of 1s ending at (i, j)
dp[i][j][1] = longest vertical line of 1s ending at (i, j)
dp[i][j][2] = longest diagonal line of 1s ending at (i, j)
dp[i][j][3] = longest anti-diagonal line of 1s ending at (i, j)
```

If `M[i][j] == 0`, then all four values are zero.

If `M[i][j] == 1`, then each of these values depends only on the appropriate previous cell.

---

## Transition Rules

If `M[i][j] == 1`:

### Horizontal

We extend from the left:

```text
dp[i][j][0] = dp[i][j - 1][0] + 1
```

if `j > 0`, otherwise `1`.

### Vertical

We extend from above:

```text
dp[i][j][1] = dp[i - 1][j][1] + 1
```

if `i > 0`, otherwise `1`.

### Diagonal

We extend from upper-left:

```text
dp[i][j][2] = dp[i - 1][j - 1][2] + 1
```

if `i > 0` and `j > 0`, otherwise `1`.

### Anti-diagonal

We extend from upper-right:

```text
dp[i][j][3] = dp[i - 1][j + 1][3] + 1
```

if `i > 0` and `j < n - 1`, otherwise `1`.

At each cell, we update the global maximum among these four values.

---

## Java Implementation

```java
class Solution {
  public int longestLine(int[][] M) {
    if (M.length == 0) return 0;
    int ones = 0;
    int[][][] dp = new int[M.length][M[0].length][4];

    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M[0].length; j++) {
        if (M[i][j] == 1) {
          dp[i][j][0] = j > 0 ? dp[i][j - 1][0] + 1 : 1;
          dp[i][j][1] = i > 0 ? dp[i - 1][j][1] + 1 : 1;
          dp[i][j][2] = (i > 0 && j > 0) ? dp[i - 1][j - 1][2] + 1 : 1;
          dp[i][j][3] = (i > 0 && j < M[0].length - 1) ? dp[i - 1][j + 1][3] + 1 : 1;

          ones = Math.max(
              ones,
              Math.max(
                  Math.max(dp[i][j][0], dp[i][j][1]),
                  Math.max(dp[i][j][2], dp[i][j][3])
              )
          );
        }
      }
    }
    return ones;
  }
}
```

---

## Why This Works

For each direction, the longest line ending at `(i, j)` can only come from one immediately preceding cell in that direction.

So the problem naturally breaks into four local transitions.

By storing all four directional values at each cell, we can compute the answer in one row-wise pass over the matrix.

---

## Complexity Analysis

### Time Complexity

Each cell is processed once, and each processing step does constant work on four directions.

So:

```text
O(mn)
```

### Space Complexity

The DP array has size:

```text
m * n * 4
```

which is:

```text
O(mn)
```

---

## Drawback

The 3D DP solution is clean, but it stores four values for every cell, even though each cell depends only on the previous row and the current row context.

That means the space can be improved.

---

# Approach 3: Using 2D Dynamic Programming

## Intuition

In the previous approach, notice that the DP state for row `i` only depends on:

- the current row while scanning left to right
- the previous row’s stored values

So we do not actually need to keep the whole `m x n x 4` structure.

We can compress the DP into a `n x 4` table, where each column stores the directional counts for the current sweep.

This reduces space from `O(mn)` to `O(n)`.

---

## Key Compression Idea

For each column `j`, maintain:

```text
dp[j][0] = horizontal count ending at current row, column j
dp[j][1] = vertical count ending at current row, column j
dp[j][2] = diagonal count ending at current row, column j
dp[j][3] = anti-diagonal count ending at current row, column j
```

### Dependencies

- Horizontal depends on `dp[j - 1][0]` in the current row
- Vertical depends on previous `dp[j][1]`
- Diagonal depends on the old diagonal value from previous row and previous column
- Anti-diagonal depends on previous-row information in `dp[j + 1][3]`

Because diagonal needs the previous row’s previous-column diagonal value, we carry it in a temporary variable `old`.

---

## Algorithm

1. Create `dp[n][4]`
2. For each row:
   - reset `old = 0`
   - scan columns left to right
3. If `M[i][j] == 1`:
   - update all four direction values
   - update maximum
4. If `M[i][j] == 0`:
   - reset all direction values at `dp[j]` to zero
5. Continue until all rows are processed

---

## Java Implementation

```java
class Solution {
  public int longestLine(int[][] M) {
    if (M.length == 0) return 0;
    int ones = 0;
    int[][] dp = new int[M[0].length][4];

    for (int i = 0; i < M.length; i++) {
      int old = 0;
      for (int j = 0; j < M[0].length; j++) {
        if (M[i][j] == 1) {
          dp[j][0] = j > 0 ? dp[j - 1][0] + 1 : 1;
          dp[j][1] = i > 0 ? dp[j][1] + 1 : 1;

          int prev = dp[j][2];
          dp[j][2] = (i > 0 && j > 0) ? old + 1 : 1;
          old = prev;

          dp[j][3] = (i > 0 && j < M[0].length - 1) ? dp[j + 1][3] + 1 : 1;

          ones = Math.max(
              ones,
              Math.max(
                  Math.max(dp[j][0], dp[j][1]),
                  Math.max(dp[j][2], dp[j][3])
              )
          );
        } else {
          old = dp[j][2];
          dp[j][0] = dp[j][1] = dp[j][2] = dp[j][3] = 0;
        }
      }
    }

    return ones;
  }
}
```

---

## Why This Works

The DP compression works because each state only needs a limited amount of past information:

- horizontal only needs current-row left neighbor
- vertical only needs previous-row same column
- diagonal only needs previous-row previous column
- anti-diagonal only needs previous-row next column

So instead of storing all rows, we store only what is still needed while processing the current row.

That gives the same answer as the 3D DP with much less space.

---

## Complexity Analysis

### Time Complexity

Each cell is still processed once:

```text
O(mn)
```

### Space Complexity

The compressed DP array has size:

```text
n * 4
```

So:

```text
O(n)
```

---

# Comparison of Approaches

| Approach    | Main Idea                                                      | Time Complexity | Space Complexity |
| ----------- | -------------------------------------------------------------- | --------------: | ---------------: |
| Brute Force | Traverse every row, column, diagonal, anti-diagonal explicitly |         `O(mn)` |           `O(1)` |
| 3D DP       | Store 4 directional counts for every cell                      |         `O(mn)` |          `O(mn)` |
| 2D DP       | Compress DP using only current/previous-row information        |         `O(mn)` |           `O(n)` |

---

# Key Takeaways

## 1. Brute force is conceptually simplest

It works by explicitly scanning each line in each direction.

## 2. Dynamic programming unifies all directions in one pass

Instead of separate traversals, DP lets us update horizontal, vertical, diagonal, and anti-diagonal counts at the same time.

## 3. DP state compression is possible

Because the transition only needs nearby previous values, we can reduce memory from `O(mn)` to `O(n)`.

---

# Final Insight

All three approaches achieve the same linear scan time:

```text
O(mn)
```

The main difference is:

- brute force uses repeated directional traversals
- 3D DP is cleaner conceptually but uses more memory
- 2D DP is the most space-efficient dynamic programming version

For interviews, the **2D DP** solution is usually the strongest answer because it combines:

- single traversal
- all-direction tracking
- optimal space reduction
