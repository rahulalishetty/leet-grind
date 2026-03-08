# Minimum Days to Disconnect an Island — O(m·n) Articulation Point (Tarjan) Approach (Summary + Code)

This note summarizes an efficient solution for:

> Given a binary grid (1 = land, 0 = water), return the minimum number of days (flip 1→0) to make the grid have **0 islands** or **≥ 2 islands**.

Key result:

- Answer is always **0, 1, or 2**.
- We can decide between these using **articulation points** (cut vertices) in the island graph.

---

## 1) Core intuition

### Articulation point = “one cell that can split the island”

Treat each land cell as a node in a graph; edges exist between 4-direction adjacent land cells.

A land cell is an **articulation point** if removing that node disconnects the graph, i.e., splits a single island into two or more pieces.

Therefore:

- If the grid currently has **one island** and has **any articulation point**, then we can disconnect it in **1 day** (remove that cell).
- If there is **no articulation point**, then removing a single cell cannot disconnect the island → answer becomes **2** (except the single-cell special case).

---

## 2) What Tarjan’s algorithm tracks (per node/cell)

During DFS, each node maintains:

1. **discoveryTime[u]**
   The DFS time when `u` is first visited.

2. **lowestReachable[u]** (often called `low[u]`)
   The minimum discovery time reachable from `u`’s subtree using:
   - tree edges (DFS edges) and
   - at most one back-edge

3. **parent[u]**
   The DFS parent of `u` (to ignore the undirected edge back to parent).

---

## 3) Articulation point conditions

### Condition A: Non-root node

A non-root node `u` is an articulation point if it has a child `v` such that:

- `low[v] >= disc[u]`

Meaning:

- The subtree under `v` cannot reach any ancestor of `u` without passing through `u`.
- So removing `u` disconnects that subtree from the rest.

### Condition B: Root node

The DFS root is an articulation point if it has **more than one** DFS child.

Meaning:

- Root is the only common connector among those separate DFS branches.

---

## 4) Full algorithm (minDays)

1. Traverse the grid, counting:
   - `landCells`
   - `islandCount` (via DFS starts)
   - and whether **any articulation point exists**

2. Return rules:

- If `islandCount == 0` or `islandCount >= 2` → return **0**
- Else (exactly 1 island):
  - If `landCells == 1` → return **1**
  - If `hasArticulationPoint` → return **1**
  - Else → return **2**

---

## 5) Java implementation (as-is style)

```java
class Solution {

    // Directions for adjacent cells: right, down, left, up
    private static final int[][] DIRECTIONS = {
        { 0, 1 },
        { 1, 0 },
        { 0, -1 },
        { -1, 0 },
    };

    public int minDays(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        ArticulationPointInfo apInfo = new ArticulationPointInfo(false, 0);
        int landCells = 0, islandCount = 0;

        // Arrays to store information for each cell
        int[][] discoveryTime = new int[rows][cols]; // Time when a cell is first discovered
        int[][] lowestReachable = new int[rows][cols]; // Lowest discovery time reachable from subtree rooted at this cell
        int[][] parentCell = new int[rows][cols]; // Parent of each cell in DFS tree

        // Initialize arrays with default values
        for (int i = 0; i < rows; i++) {
            Arrays.fill(discoveryTime[i], -1);
            Arrays.fill(lowestReachable[i], -1);
            Arrays.fill(parentCell[i], -1);
        }

        // Traverse the grid to find islands and articulation points
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    landCells++;
                    if (discoveryTime[i][j] == -1) { // If not yet visited
                        // Start DFS for a new island
                        findArticulationPoints(
                            grid,
                            i,
                            j,
                            discoveryTime,
                            lowestReachable,
                            parentCell,
                            apInfo
                        );
                        islandCount++;
                    }
                }
            }
        }

        // Determine the minimum number of days to disconnect the grid
        if (islandCount == 0 || islandCount >= 2) return 0; // Already disconnected or no land
        if (landCells == 1) return 1; // Only one land cell
        if (apInfo.hasArticulationPoint) return 1; // An articulation point exists
        return 2; // Need to remove any two land cells
    }

    private void findArticulationPoints(
        int[][] grid,
        int row,
        int col,
        int[][] discoveryTime,
        int[][] lowestReachable,
        int[][] parentCell,
        ArticulationPointInfo apInfo
    ) {
        int rows = grid.length, cols = grid[0].length;

        discoveryTime[row][col] = apInfo.time;
        apInfo.time++;
        lowestReachable[row][col] = discoveryTime[row][col];

        int children = 0;

        // Explore all adjacent cells
        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (isValidLandCell(grid, newRow, newCol)) {
                if (discoveryTime[newRow][newCol] == -1) {
                    children++;
                    parentCell[newRow][newCol] = row * cols + col; // Set parent

                    findArticulationPoints(
                        grid,
                        newRow,
                        newCol,
                        discoveryTime,
                        lowestReachable,
                        parentCell,
                        apInfo
                    );

                    // Update lowest reachable time from child
                    lowestReachable[row][col] = Math.min(
                        lowestReachable[row][col],
                        lowestReachable[newRow][newCol]
                    );

                    // Articulation condition for non-root
                    if (
                        lowestReachable[newRow][newCol] >= discoveryTime[row][col] &&
                        parentCell[row][col] != -1
                    ) {
                        apInfo.hasArticulationPoint = true;
                    }
                } else if (newRow * cols + newCol != parentCell[row][col]) {
                    // Back edge: update low with neighbor's discovery time
                    lowestReachable[row][col] = Math.min(
                        lowestReachable[row][col],
                        discoveryTime[newRow][newCol]
                    );
                }
            }
        }

        // Root is an articulation point if it has > 1 DFS child
        if (parentCell[row][col] == -1 && children > 1) {
            apInfo.hasArticulationPoint = true;
        }
    }

    // Check if the given cell is a valid land cell
    private boolean isValidLandCell(int[][] grid, int row, int col) {
        int rows = grid.length, cols = grid[0].length;
        return (
            row >= 0 &&
            col >= 0 &&
            row < rows &&
            col < cols &&
            grid[row][col] == 1
        );
    }

    private class ArticulationPointInfo {
        boolean hasArticulationPoint;
        int time;

        ArticulationPointInfo(boolean hasArticulationPoint, int time) {
            this.hasArticulationPoint = hasArticulationPoint;
            this.time = time;
        }
    }
}
```

---

## 6) Complexity analysis

Let m = rows, n = cols.

### Time: O(m·n)

- Initializing arrays: O(m·n)
- DFS visits each land cell once; each cell checks up to 4 neighbors → O(m·n)

Total time: **O(m·n)**

### Space: O(m·n)

- discoveryTime, lowestReachable, parentCell arrays: O(m·n)
- recursion stack worst case (all land): O(m·n)

Total: **O(m·n)**

---

## 7) Practical notes

- This is essentially Tarjan’s articulation point algorithm adapted to a grid graph.
- It avoids the O((m·n)²) brute-force approach of “try removing each cell and recount islands”.
- If recursion depth is a concern, DFS can be converted to an explicit stack, but handling `low[]` iteratively is more complex.
