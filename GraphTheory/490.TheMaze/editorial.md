# 490. The Maze — Detailed Solutions in Java

## Problem Recap

A ball rolls through a maze. Once it starts rolling in a direction, it **keeps rolling until it hits a wall** (or the maze boundary) — it cannot stop mid-corridor. At a wall, it stops and can pick a new direction. We need to determine if the ball can come to a **stop** exactly at `destination`. Passing through the destination without stopping does not count.

```
maze[i][j] == 0  -> empty space
maze[i][j] == 1  -> wall
```

---

## Key Insight

This is a graph reachability problem, but the "nodes" of the graph aren't individual cells — they are **stopping points** (cells where the ball comes to rest against a wall). From any stopping point, moving in a direction always lands on another well-defined stopping point (simulate rolling until blocked). So:

1. Treat each stopping position as a graph node.
2. From a node, generate up to 4 neighbor nodes (one per direction) by simulating the roll.
3. Search this graph (DFS or BFS) starting at `start`, looking for `destination`.
4. Mark stopping points visited to avoid infinite loops (the ball could otherwise oscillate forever between two walls).

This "roll until blocked" helper is shared by every approach below:

```java
// Starting at (row, col), roll in direction (dr, dc) until blocked.
// Returns the stopping cell.
private int[] roll(int[][] maze, int row, int col, int dr, int dc) {
    int m = maze.length, n = maze[0].length;
    while (row + dr >= 0 && row + dr < m &&
           col + dc >= 0 && col + dc < n &&
           maze[row + dr][col + dc] == 0) {
        row += dr;
        col += dc;
    }
    return new int[]{row, col};
}
```

All three approaches below differ only in _how they traverse the stopping-point graph_: recursive DFS, iterative DFS (explicit stack), and BFS.

---

## Approach 1: Recursive DFS

### Idea

Standard graph DFS. From the current stopping point, try all 4 directions, roll to find the next stopping point, and recurse if it hasn't been visited yet. If we ever land exactly on `destination`, return `true`.

### Java Code

```java
class Solution {
    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        int m = maze.length, n = maze[0].length;
        boolean[][] visited = new boolean[m][n];
        return dfs(maze, start[0], start[1], destination, visited);
    }

    private boolean dfs(int[][] maze, int row, int col, int[] destination, boolean[][] visited) {
        if (visited[row][col]) return false;
        visited[row][col] = true;

        if (row == destination[0] && col == destination[1]) return true;

        int m = maze.length, n = maze[0].length;
        for (int[] dir : DIRS) {
            int r = row, c = col;
            // Roll until hitting a wall or boundary
            while (r + dir[0] >= 0 && r + dir[0] < m &&
                   c + dir[1] >= 0 && c + dir[1] < n &&
                   maze[r + dir[0]][c + dir[1]] == 0) {
                r += dir[0];
                c += dir[1];
            }
            if (dfs(maze, r, c, destination, visited)) return true;
        }
        return false;
    }
}
```

### Walkthrough (Example 1)

- Start `(0,4)`. Try rolling in each direction; e.g. rolling **left** from `(0,4)` slides through `(0,3)` and stops at `(0,3)` because `(0,2)` is a wall.
- Continue exploring from each new stopping point (down, left, down, right, down, right…) until `(4,4)` is reached exactly as a stopping cell → `true`.

### Complexity

- **Time:** `O(m * n * max(m, n))`. There are at most `m*n` stopping points (each visited once), and from each we may scan up to `max(m,n)` cells in each of 4 directions while rolling.
- **Space:** `O(m * n)` for the `visited` array, plus `O(m * n)` worst-case recursion stack depth.

### Pros / Cons

- ✅ Very concise.
- ❌ Recursive — for very large mazes (up to `100 x 100`, so 10,000 stopping points) deep recursion is fine in Java's default stack, but it's still less robust than an iterative approach if constraints were larger.

---

## Approach 2: BFS (Queue-based)

### Idea

Identical graph, but traverse it level-by-level with a queue instead of recursion. This naturally avoids stack-overflow risk and is a very natural fit since we don't actually need shortest path here — just reachability — but BFS works equally well as DFS for that.

### Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        int m = maze.length, n = maze[0].length;
        boolean[][] visited = new boolean[m][n];

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(start);
        visited[start[0]][start[1]] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            if (curr[0] == destination[0] && curr[1] == destination[1]) {
                return true;
            }

            for (int[] dir : DIRS) {
                int r = curr[0], c = curr[1];
                while (r + dir[0] >= 0 && r + dir[0] < m &&
                       c + dir[1] >= 0 && c + dir[1] < n &&
                       maze[r + dir[0]][c + dir[1]] == 0) {
                    r += dir[0];
                    c += dir[1];
                }
                if (!visited[r][c]) {
                    visited[r][c] = true;
                    queue.offer(new int[]{r, c});
                }
            }
        }
        return false;
    }
}
```

### Walkthrough (Example 2 — expect `false`)

- Start `(0,4)`. BFS expands all reachable stopping points layer by layer: `(0,3)`, `(1,4)`, `(4,4)`, `(1,0)`, etc.
- The ball can **roll through** `(3,2)` while traveling between other stopping points, but it never comes to **rest** exactly at `(3,2)` — so `(3,2)` is never enqueued as a stopping node, and BFS exhausts the queue without finding it → `false`.

### Complexity

- **Time:** `O(m * n * max(m, n))` — same reasoning as DFS: each of up to `m*n` nodes processed once, each doing up to 4 rolls of length up to `max(m,n)`.
- **Space:** `O(m * n)` for `visited` and the queue in the worst case.

### Pros / Cons

- ✅ Iterative, no recursion depth concerns.
- ✅ Same complexity as DFS; purely a style choice for plain reachability.
- ➕ If the problem were extended to "shortest distance" (as in LeetCode 505, _The Maze II_), BFS naturally generalizes by tracking distance/steps per node — DFS does not generalize as cleanly.

---

## Approach 3: Iterative DFS (Explicit Stack)

### Idea

Same algorithm as Approach 1, but using an explicit `Deque` as a stack instead of relying on the call stack. Useful if you want DFS's traversal order without recursion overhead/risk.

### Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        int m = maze.length, n = maze[0].length;
        boolean[][] visited = new boolean[m][n];

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start[0]][start[1]] = true;

        while (!stack.isEmpty()) {
            int[] curr = stack.pop();

            if (curr[0] == destination[0] && curr[1] == destination[1]) {
                return true;
            }

            for (int[] dir : DIRS) {
                int r = curr[0], c = curr[1];
                while (r + dir[0] >= 0 && r + dir[0] < m &&
                       c + dir[1] >= 0 && c + dir[1] < n &&
                       maze[r + dir[0]][c + dir[1]] == 0) {
                    r += dir[0];
                    c += dir[1];
                }
                if (!visited[r][c]) {
                    visited[r][c] = true;
                    stack.push(new int[]{r, c});
                }
            }
        }
        return false;
    }
}
```

### Complexity

Same as Approach 1: `O(m * n * max(m, n))` time, `O(m * n)` space — but no recursion stack risk.

---

## Why a `visited` array is essential

Without marking stopping points as visited, the ball could bounce back and forth between two walls forever (e.g., roll right to a wall, then roll left back to where it started, then right again...), causing infinite recursion/looping. Marking each _stopping point_ visited the first time it's reached guarantees each of the at most `m * n` nodes is expanded only once.

---

## Common Mistake to Avoid

A frequent bug is checking `if (maze[row][col] == destination row/col)` **during the rolling loop** instead of only at stopping points. That would incorrectly return `true` for Example 2, where the ball merely _passes through_ `(3,2)` without stopping there. The destination check must happen only on the cell where rolling **stops** (post-loop), never mid-roll.

---

## Comparison Summary

| Approach              | Time Complexity | Space Complexity | Notes                                           |
| --------------------- | --------------- | ---------------- | ----------------------------------------------- |
| Recursive DFS         | O(m·n·max(m,n)) | O(m·n)           | Simplest to write; uses call stack              |
| BFS (Queue)           | O(m·n·max(m,n)) | O(m·n)           | Iterative; generalizes to shortest-path version |
| Iterative DFS (Stack) | O(m·n·max(m,n)) | O(m·n)           | DFS order without recursion risk                |

All three are functionally equivalent for this problem — pick BFS if you anticipate extending to "shortest path" (LeetCode 505), and DFS (recursive or iterative) if you just want the simplest reachability check.

---

## Verifying Against the Given Examples

- **Example 1:** `start=[0,4]`, `destination=[4,4]` → all three solutions return `true`.
- **Example 2:** `start=[0,4]`, `destination=[3,2]` → all three solutions return `false` (destination is only ever passed through, never a stopping point).
- **Example 3:** `start=[4,3]`, `destination=[0,1]` → all three solutions return `false`.
