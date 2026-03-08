# Minimum Days to Disconnect an Island (Binary Grid) — Summary + Code

This note summarizes a common approach for the problem:

> Given a binary grid (1 = land, 0 = water), return the minimum number of **days** (cell flips from 1→0) needed so the grid has **0 islands** or **more than 1 island**.
> (Equivalently: make the land **disconnected** or **empty**.)

---

## 1) Intuition (what we’re trying to achieve)

A grid can be in one of three states:

1. **0 islands** (all water)
2. **Exactly 1 island**
3. **More than 1 island**

Only state (2) needs work.

Key observation used by this solution:

- If the grid is already in state (1) or (3), answer is **0**.
- Otherwise, try removing **one land cell**. If that makes the grid become (1) or (3), answer is **1**.
- If removing any single land cell does not work, return **2**.

Why “2” is enough:

- In a binary grid with 4-direction adjacency, if you cannot disconnect an island by removing one cell, you can always do it in **two** cell removals. (This is a known property used in standard solutions.)

So the answer is always in {0, 1, 2}.

---

## 2) Strategy overview

1. Compute `initialIslandCount` using `countIslands(grid)`.
   - If `initialIslandCount != 1`, return **0** immediately.
2. Otherwise, for each land cell:
   - Temporarily flip it to water (1→0)
   - Recompute islands count with `countIslands`
   - If the result is not 1, return **1**
   - Restore the cell back to land (0→1)
3. If none of the single flips work, return **2**.

The expensive operation is `countIslands`, so the algorithm is essentially:

- “Try all single-cell deletions, and run DFS island counting each time.”

Constraints in typical versions of this problem make this feasible.

---

## 3) Counting islands (flood-fill / DFS)

### `countIslands(grid)`

- Maintain a `visited[m][n]` boolean.
- Iterate all cells.
- When you find an unvisited land cell, run `exploreIsland(...)` to mark its entire connected component.
- Each DFS call corresponds to one island.
- Return the total.

### `exploreIsland(grid, r, c, visited)`

Classic DFS:

- mark current cell visited
- attempt 4-direction moves (up/down/left/right)
- recurse into valid neighboring land cells

---

## 4) Java implementation (as-is style)

```java
class Solution {

    // Directions for adjacent cells: right, left, down, up
    private static final int[][] DIRECTIONS = {
        { 0, 1 },
        { 0, -1 },
        { 1, 0 },
        { -1, 0 },
    };

    public int minDays(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Count initial islands
        int initialIslandCount = countIslands(grid);

        // Already disconnected or no land
        if (initialIslandCount != 1) {
            return 0;
        }

        // Try removing each land cell
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == 0) continue; // Skip water

                // Temporarily change to water
                grid[row][col] = 0;
                int newIslandCount = countIslands(grid);

                // Check if disconnected
                if (newIslandCount != 1) return 1;

                // Revert change
                grid[row][col] = 1;
            }
        }

        return 2;
    }

    private int countIslands(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int islandCount = 0;

        // Iterate through all cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Found new island
                if (!visited[row][col] && grid[row][col] == 1) {
                    exploreIsland(grid, row, col, visited);
                    islandCount++;
                }
            }
        }
        return islandCount;
    }

    // Helper method to explore all cells of an island
    private void exploreIsland(
        int[][] grid,
        int row,
        int col,
        boolean[][] visited
    ) {
        visited[row][col] = true;

        // Check all adjacent cells
        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            // Explore if valid land cell
            if (isValidLandCell(grid, newRow, newCol, visited)) {
                exploreIsland(grid, newRow, newCol, visited);
            }
        }
    }

    private boolean isValidLandCell(
        int[][] grid,
        int row,
        int col,
        boolean[][] visited
    ) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Check bounds, land, and not visited
        return (
            row >= 0 &&
            col >= 0 &&
            row < rows &&
            col < cols &&
            grid[row][col] == 1 &&
            !visited[row][col]
        );
    }
}
```

---

## 5) Complexity analysis

Let:

- m = number of rows
- n = number of columns
- total cells = m·n

### Time complexity: **O((m·n)²)**

- `countIslands` is O(m·n) in the worst case (DFS can visit every cell once).
- In `minDays`, we call `countIslands`:
  - once for the initial check
  - up to (m·n) times while trying each land cell removal

So total:

- O(m·n) \* (1 + m·n) = O((m·n)²)

### Space complexity: **O(m·n)**

- visited array: O(m·n)
- recursion stack worst-case depth: O(m·n)

Total space: O(m·n)

---

## 6) Practical notes

- Because the answer is always 0, 1, or 2, this brute-force “try each cell once” approach is widely used.
- If recursion depth is a concern (very large grids), `exploreIsland` can be rewritten using an explicit stack (iterative DFS) to avoid stack overflow.
