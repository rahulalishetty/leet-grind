# Bomb Enemy — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

The problem discussed here is the classic **Bomb Enemy** problem:

- The grid contains:
  - `'W'` for walls
  - `'E'` for enemies
  - `'0'` for empty cells
- A bomb placed on an empty cell kills all enemies in the same row and column
- The blast stops at walls
- We want the maximum number of enemies that can be killed by placing one bomb

---

# Approach 1: Brute-force Enumeration

## Intuition

The most direct idea is:

- Try placing a bomb on **every empty cell**
- For each empty cell, compute how many enemies would be killed
- Return the maximum count seen

This is the simplest possible approach and is often the natural first solution.

Even though it is not optimal, it is correct and can pass smaller test cases.

---

## How the Explosion Works

If we place a bomb at some empty cell `(row, col)`:

- scan left until hitting a wall or boundary
- scan right until hitting a wall or boundary
- scan up until hitting a wall or boundary
- scan down until hitting a wall or boundary

Every enemy encountered in those four scans is counted.

Since walls block the explosion, scanning must stop immediately when a wall is reached.

---

## Algorithm

1. Iterate through every cell of the grid
2. If the current cell is empty (`'0'`):
   - call a helper function `killEnemies(row, col)`
3. In `killEnemies(row, col)`:
   - scan in all four directions
   - count enemies until a wall or boundary is reached
4. Keep track of the maximum count across all empty cells
5. Return the maximum

---

## Java Implementation

```java
class Solution {
    public int maxKilledEnemies(char[][] grid) {
        if (grid.length == 0)
            return 0;

        int rows = grid.length;
        int cols = grid[0].length;

        int maxCount = 0;

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (grid[row][col] == '0') {
                    int hits = this.killEnemies(row, col, grid);
                    maxCount = Math.max(maxCount, hits);
                }
            }
        }

        return maxCount;
    }

    /**
     * return the number of enemies we kill, starting from the given empty cell.
     */
    private int killEnemies(int row, int col, char[][] grid) {
        int enemyCount = 0;

        // look to the left side of the cell
        for (int c = col - 1; c >= 0; --c) {
            if (grid[row][c] == 'W')
                break;
            else if (grid[row][c] == 'E')
                enemyCount += 1;
        }

        // look to the right side of the cell
        for (int c = col + 1; c < grid[0].length; ++c) {
            if (grid[row][c] == 'W')
                break;
            else if (grid[row][c] == 'E')
                enemyCount += 1;
        }

        // look to the up side of the cell
        for (int r = row - 1; r >= 0; --r) {
            if (grid[r][col] == 'W')
                break;
            else if (grid[r][col] == 'E')
                enemyCount += 1;
        }

        // look to the down side of the cell
        for (int r = row + 1; r < grid.length; ++r) {
            if (grid[r][col] == 'W')
                break;
            else if (grid[r][col] == 'E')
                enemyCount += 1;
        }

        return enemyCount;
    }
}
```

---

## Complexity Analysis

Let:

- `W` = width of the grid (number of columns)
- `H` = height of the grid (number of rows)

### Time Complexity

We examine every cell in the grid:

```text
W * H
```

For each empty cell, in the worst case with no walls, we may scan:

- up to `W - 1` cells horizontally
- up to `H - 1` cells vertically

So each empty cell can cost:

```text
O(W + H)
```

Thus, in the worst case:

```text
O(W * H * (W + H))
```

### Space Complexity

We only use a constant amount of extra space:

```text
O(1)
```

---

# Why Brute Force Repeats Work

The brute-force solution recomputes the same row and column counts many times.

For example, suppose a row segment between two walls contains 3 enemies.

Then for every empty cell inside that same segment, the horizontal count is identical.

But the brute-force solution rescans the entire segment from scratch for every empty cell.

The same issue occurs for columns.

This repeated scanning is exactly the redundancy we want to eliminate.

---

# Approach 2: Dynamic Programming

## Intuition

The key optimization is recognizing that for many cells, the number of enemies visible in the same row or column does **not change**.

### Important Observation

Between two walls, all cells in the same row segment share the same horizontal enemy count.

Similarly, between two walls, all cells in the same column segment share the same vertical enemy count.

So instead of recomputing the full scan at each empty cell, we can cache and reuse these values.

This is a dynamic programming style optimization:

- compute intermediate results once
- reuse them whenever possible

---

## Breaking the Problem into Subproblems

For any empty cell `(row, col)`:

```text
total_hits = row_hits + col_hits
```

where:

- `row_hits` = enemies visible horizontally in the current row segment
- `col_hits` = enemies visible vertically in the current column segment

So the problem reduces to efficiently maintaining `row_hits` and `col_hits`.

---

## How to Maintain `row_hits`

When scanning left to right across a row:

- If we are at the first column, or
- If the previous cell is a wall,

then the current row segment starts here, so we must recompute `row_hits` by scanning rightward until a wall.

Otherwise, we can reuse the already computed `row_hits` because we are still in the same row segment.

This works because all cells between two walls share the same row segment count.

---

## How to Maintain `col_hits`

For columns, the idea is similar, but because we scan row by row, we need to remember separate vertical counts for each column.

So we use an array:

```text
colHits[col]
```

When processing cell `(row, col)`:

- If we are at the first row, or
- If the cell directly above is a wall,

then a new column segment starts here, so we recompute `colHits[col]` by scanning downward until a wall.

Otherwise, the previously stored `colHits[col]` remains valid.

---

## Algorithm

1. Initialize:
   - `rowHits = 0`
   - `colHits = new int[cols]`
2. Iterate through the grid row by row, column by column
3. For each cell `(row, col)`:

### Recompute `rowHits` when needed

If:

- `col == 0`, or
- `grid[row][col - 1] == 'W'`

then scan to the right from `(row, col)` until a wall and count enemies

### Recompute `colHits[col]` when needed

If:

- `row == 0`, or
- `grid[row - 1][col] == 'W'`

then scan downward from `(row, col)` until a wall and count enemies

### Update result

If `grid[row][col] == '0'`, then:

```text
maxCount = max(maxCount, rowHits + colHits[col])
```

---

## Java Implementation

```java
class Solution {
    public int maxKilledEnemies(char[][] grid) {
        if (grid.length == 0)
            return 0;

        int rows = grid.length;
        int cols = grid[0].length;

        int maxCount = 0, rowHits = 0;
        int[] colHits = new int[cols];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {

                // reset the hits on the row, if necessary.
                if (col == 0 || grid[row][col - 1] == 'W') {
                    rowHits = 0;
                    for (int k = col; k < cols; ++k) {
                        if (grid[row][k] == 'W')
                            break;
                        else if (grid[row][k] == 'E')
                            rowHits += 1;
                    }
                }

                // reset the hits on the column, if necessary.
                if (row == 0 || grid[row - 1][col] == 'W') {
                    colHits[col] = 0;
                    for (int k = row; k < rows; ++k) {
                        if (grid[k][col] == 'W')
                            break;
                        else if (grid[k][col] == 'E')
                            colHits[col] += 1;
                    }
                }

                // run the calculation for the empty cell.
                if (grid[row][col] == '0') {
                    maxCount = Math.max(maxCount, rowHits + colHits[col]);
                }
            }
        }

        return maxCount;
    }
}
```

---

## Why This Works

The DP idea is not a traditional table over indices with recurrence, but rather a reuse of segment counts.

### For rows

Between two walls, every cell sees the same set of enemies horizontally.

So we compute that number once when entering the segment.

### For columns

Between two walls in a column, every cell sees the same set of enemies vertically.

So we compute `colHits[col]` once when entering the vertical segment.

Thus, each horizontal segment is scanned once, and each vertical segment is scanned once.

That eliminates the repeated rescanning done by brute force.

---

## Complexity Analysis

Let:

- `W` = number of columns
- `H` = number of rows

### Time Complexity

At first glance, because there are inner scans inside the main loops, one might think the complexity is still:

```text
O(W * H * (W + H))
```

But that is not the correct way to count the work here.

A better view is to count how many times each cell is involved:

1. Each cell is visited once in the main double loop
2. Each cell is involved in at most one row-segment scan
3. Each cell is involved in at most one column-segment scan

So each cell is processed only a constant number of times.

Therefore the total time complexity is:

```text
O(W * H)
```

### Space Complexity

We use:

- one integer `rowHits`
- one array `colHits` of length `W`

So the extra space is:

```text
O(W)
```

---

# Comparison Between the Two Approaches

| Approach            | Main Idea                                             |      Time Complexity | Space Complexity |
| ------------------- | ----------------------------------------------------- | -------------------: | ---------------: |
| Brute Force         | Try bomb at every empty cell and scan four directions | `O(W * H * (W + H))` |           `O(1)` |
| Dynamic Programming | Reuse row and column segment enemy counts             |           `O(W * H)` |           `O(W)` |

---

# Key Insights

## 1. Brute force is easy but repetitive

The brute-force solution rescans the same uninterrupted row and column segments many times.

## 2. Walls partition the grid into reusable segments

Walls naturally divide rows and columns into independent segments.

Within a segment:

- horizontal enemy count stays fixed
- vertical enemy count stays fixed

## 3. Recompute only at segment boundaries

That is the central optimization.

We only recompute:

- `rowHits` when entering a new row segment
- `colHits[col]` when entering a new column segment

---

# Final Takeaway

The brute-force solution is conceptually simple:

- place a bomb everywhere possible
- simulate the blast

The dynamic programming solution improves efficiency by caching the number of enemies in row and column segments and reusing those counts.

That reduces the runtime from:

```text
O(W * H * (W + H))
```

to:

```text
O(W * H)
```

which is the optimal approach for this problem.
