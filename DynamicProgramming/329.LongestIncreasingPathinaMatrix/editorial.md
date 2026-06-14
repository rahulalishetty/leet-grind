# Longest Increasing Path in a Matrix (LeetCode 329) — Exhaustive Java Approaches

## Problem Restatement

You are given an `m x n` matrix of integers.

Goal:

- Find the length of the **longest strictly increasing path**

Movement rules:

- From a cell, you may move only:
  - up
  - down
  - left
  - right
- No diagonal moves
- No wrap-around
- Next cell must have a **strictly larger** value

Return:

- the maximum path length

---

## Core Observation

This is not a general graph with cycles in the useful direction of movement.

Why?

Because moves are allowed only from smaller value to larger value.

That means:

- if you move from `a` to `b`, then `matrix[b] > matrix[a]`
- you can never come back to the same cell through a strictly increasing path

So the graph formed by valid moves is a **DAG** (Directed Acyclic Graph).

That is the key structural property behind the efficient solutions.

---

# Graph Interpretation

Treat every cell as a node.

There is a directed edge:

```text
(cell1) -> (cell2)
```

if:

- `cell2` is a valid neighbor of `cell1`
- `matrix[cell2] > matrix[cell1]`

Then the problem becomes:

> Find the longest path in a DAG.

That immediately suggests:

- DFS + memoization
- topological sorting / BFS by indegree
- DP on DAG ordering

---

# Approach 1: Pure DFS / Backtracking from Every Cell

## Idea

Start DFS from every cell and try all increasing paths.

Return the maximum length found.

This is the most direct brute-force solution.

---

## How It Works

For each cell `(r, c)`:

- recursively explore all 4 neighbors with larger values
- best path from this cell is:
  `1 + max(best from valid larger neighbors)`

Without memoization, the same subproblems are recomputed many times.

---

## Java Code

```java
class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int ans = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                ans = Math.max(ans, dfs(matrix, r, c));
            }
        }

        return ans;
    }

    private int dfs(int[][] matrix, int r, int c) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int best = 1;

        for (int[] d : DIRS) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && matrix[nr][nc] > matrix[r][c]) {
                best = Math.max(best, 1 + dfs(matrix, nr, nc));
            }
        }

        return best;
    }
}
```

---

## Complexity

### Time Complexity

- Exponential in the worst case

Because the same paths are explored repeatedly from many starting cells.

### Space Complexity

- `O(m * n)` recursion depth in the worst case path shape

---

## Pros

- Most direct recursive thinking
- Easy to understand initially

## Cons

- Far too slow
- Massive overlapping subproblems

---

# Approach 2: DFS + Memoization (Top-Down DP) — Standard Best Solution

## Idea

The path length starting from a cell depends only on:

- that cell
- the best path lengths from larger neighbors

So cache the result for each cell.

Let:

```text
memo[r][c] = longest increasing path starting at cell (r, c)
```

Then compute each cell once.

This is the standard and most common accepted solution.

---

## Recurrence

For cell `(r, c)`:

```text
memo[r][c] = 1 + max(memo[nr][nc]) over all valid larger neighbors
```

If no larger neighbor exists:

```text
memo[r][c] = 1
```

---

## Java Code

```java
class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] memo = new int[rows][cols];

        int ans = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                ans = Math.max(ans, dfs(matrix, r, c, memo));
            }
        }

        return ans;
    }

    private int dfs(int[][] matrix, int r, int c, int[][] memo) {
        if (memo[r][c] != 0) {
            return memo[r][c];
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int best = 1;

        for (int[] d : DIRS) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && matrix[nr][nc] > matrix[r][c]) {
                best = Math.max(best, 1 + dfs(matrix, nr, nc, memo));
            }
        }

        memo[r][c] = best;
        return best;
    }
}
```

---

## Why It Works

Every cell's answer depends only on larger-valued neighbors.

Since strictly increasing paths cannot cycle, this recursive dependency is safe.

Memoization ensures:

- once `dfs(r, c)` is computed, it is never recomputed

---

## Complexity

### Time Complexity

- `O(m * n)`

Each cell is computed once, and each computation checks at most 4 neighbors.

### Space Complexity

- `O(m * n)` for memo
- plus recursion stack worst-case `O(m * n)`

Overall commonly stated as `O(m * n)` extra space.

---

## Pros

- Cleanest solution
- Best mix of clarity and efficiency
- Most interview-friendly

## Cons

- Uses recursion stack

---

# Approach 3: BFS / Topological Sort by Indegree (Kahn’s Algorithm)

## Idea

Because the increasing graph is a DAG, we can solve longest path using topological layers.

Define edges from smaller cell to larger cell.

Then:

- cells with indegree `0` are local minima
- they form the first layer
- removing one layer at a time corresponds to growing path length by 1

So the number of BFS layers equals the longest increasing path length.

This is one of the most elegant non-recursive solutions.

---

## How Indegree Is Defined

For each cell `(r, c)`:

- look at its 4 neighbors
- if neighbor value is smaller than current value, then there is an incoming edge from that neighbor
- indegree counts how many smaller neighbors point into this cell

Cells with indegree `0`:

- no smaller neighbor
- valid path starting points

---

## Java Code

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] indegree = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                for (int[] d : DIRS) {
                    int nr = r + d[0];
                    int nc = c + d[1];

                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && matrix[nr][nc] < matrix[r][c]) {
                        indegree[r][c]++;
                    }
                }
            }
        }

        Queue<int[]> queue = new ArrayDeque<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (indegree[r][c] == 0) {
                    queue.offer(new int[]{r, c});
                }
            }
        }

        int layers = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            layers++;

            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                int r = cell[0], c = cell[1];

                for (int[] d : DIRS) {
                    int nr = r + d[0];
                    int nc = c + d[1];

                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && matrix[nr][nc] > matrix[r][c]) {
                        indegree[nr][nc]--;
                        if (indegree[nr][nc] == 0) {
                            queue.offer(new int[]{nr, nc});
                        }
                    }
                }
            }
        }

        return layers;
    }
}
```

---

## Why Layers Equal Path Length

Layer 1:

- all local minima

Layer 2:

- cells reachable by increasing one step from some local minimum

Layer 3:

- cells that require 2 increasing transitions

And so on.

Thus the number of layers peeled off equals the longest path length.

---

## Complexity

### Time Complexity

- `O(m * n)`

Each cell and each of its 4 directional relationships are processed a constant number of times.

### Space Complexity

- `O(m * n)` for indegree + queue

---

## Pros

- No recursion
- Elegant DAG perspective
- Strong conceptual solution

## Cons

- Less immediately intuitive than DFS + memo for many people

---

# Approach 4: Bottom-Up DP After Sorting Cells by Value

## Idea

Since moves only go from smaller to larger values, if we process cells in increasing order of value, then when handling a cell:

- all smaller-value neighbors are already processed

We can define:

```text
dp[r][c] = longest increasing path ending at cell (r, c)
```

Then for every smaller neighbor:

```text
dp[r][c] = max(dp[r][c], 1 + dp[neighbor])
```

This is a valid iterative DAG DP approach.

---

## Java Code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        List<int[]> cells = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells.add(new int[]{matrix[r][c], r, c});
            }
        }

        Collections.sort(cells, (a, b) -> Integer.compare(a[0], b[0]));

        int[][] dp = new int[rows][cols];
        int ans = 1;

        for (int[] cell : cells) {
            int r = cell[1];
            int c = cell[2];
            dp[r][c] = 1;

            for (int[] d : DIRS) {
                int nr = r + d[0];
                int nc = c + d[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && matrix[nr][nc] < matrix[r][c]) {
                    dp[r][c] = Math.max(dp[r][c], 1 + dp[nr][nc]);
                }
            }

            ans = Math.max(ans, dp[r][c]);
        }

        return ans;
    }
}
```

---

## Complexity

### Time Complexity

- Sorting cells: `O(m * n * log(m * n))`
- DP transitions: `O(m * n)`

Total:

- `O(m * n * log(m * n))`

### Space Complexity

- `O(m * n)` for cell list + DP

---

## Pros

- Purely iterative
- Very explicit DAG DP

## Cons

- Slower than memoized DFS / topological BFS because of sorting
- More cumbersome

---

# Approach 5: Explicit DAG Construction + Longest Path DP

## Idea

Build the graph explicitly:

- node for each cell
- directed edge from smaller to larger neighbor

Then run:

- DFS + memo
  or
- topological DP

This is conceptually valid but usually unnecessary because the graph is implicit in the grid.

---

## Java Code Sketch

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};
    private List<Integer>[] graph;
    private int[] memo;
    private int rows, cols;

    public int longestIncreasingPath(int[][] matrix) {
        rows = matrix.length;
        cols = matrix[0].length;
        int total = rows * cols;

        graph = new ArrayList[total];
        for (int i = 0; i < total; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = r * cols + c;

                for (int[] d : DIRS) {
                    int nr = r + d[0];
                    int nc = c + d[1];

                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && matrix[nr][nc] > matrix[r][c]) {
                        graph[id].add(nr * cols + nc);
                    }
                }
            }
        }

        memo = new int[total];
        int ans = 0;

        for (int i = 0; i < total; i++) {
            ans = Math.max(ans, dfs(i));
        }

        return ans;
    }

    private int dfs(int node) {
        if (memo[node] != 0) {
            return memo[node];
        }

        int best = 1;
        for (int next : graph[node]) {
            best = Math.max(best, 1 + dfs(next));
        }

        memo[node] = best;
        return best;
    }
}
```

---

## Complexity

### Time Complexity

- Building graph: `O(m * n)`
- DFS + memo: `O(m * n + edges)` = `O(m * n)`

### Space Complexity

- `O(m * n)` nodes
- graph edges also `O(m * n)` since max 4 outgoing edges per node

So overall:

- `O(m * n)` time
- `O(m * n)` space

---

## Honest Assessment

Correct, but usually overengineered.

The implicit-grid DFS/BFS versions are cleaner.

---

# Approach 6: DP with Repeated Relaxation (Bellman-Ford Style Thinking)

## Idea

You could initialize every cell to path length `1`, then repeatedly try to relax transitions from smaller cells to larger cells until no update occurs.

This mirrors longest-path relaxation on a DAG, but without using the DAG structure efficiently.

This is more of a conceptual anti-pattern than a recommended solution.

---

## Why It Is Inferior

You may need many passes over the grid before stabilization.

Worst-case time becomes much worse than necessary.

So while logically valid, it is not practical.

---

# Why Greedy Fails

A tempting idea:

- always move to the largest neighboring value

This fails because:

- a large immediate jump may destroy access to a longer future chain

Example:

```text
[ [3,4,5],
  [3,2,6],
  [2,2,1] ]
```

From `2`, choosing the locally largest next step is not the real issue — the global path structure matters.

This is a classic signal that local greedy decisions are insufficient.

---

# Full Derivation of DFS + Memoization

Let:

```text
dp[r][c] = longest increasing path starting from (r, c)
```

Then:

- if no valid larger neighbor exists:

  ```text
  dp[r][c] = 1
  ```

- otherwise:
  ```text
  dp[r][c] = 1 + max(dp[nr][nc])
  ```
  over all neighbors with:
  ```text
  matrix[nr][nc] > matrix[r][c]
  ```

Since every move strictly increases value:

- no cycle is possible
- recursion is safe
- memoization gives linear-time total work

This is the central recurrence.

---

# Dry Run of DFS + Memoization

Take:

```text
matrix =
[
  [9, 9, 4],
  [6, 6, 8],
  [2, 1, 1]
]
```

We compute `dfs(r, c)` = best path starting there.

---

## Cell `(0,0) = 9`

No larger neighbor.
So:

```text
dfs(0,0) = 1
```

## Cell `(0,1) = 9`

No larger neighbor.
So:

```text
dfs(0,1) = 1
```

## Cell `(0,2) = 4`

Larger neighbors:

- `(0,1)=9`
- `(1,2)=8`

So:

```text
dfs(0,2) = 1 + max(dfs(0,1), dfs(1,2))
```

`dfs(0,1)=1`
`dfs(1,2)=1` (since 8 has no larger neighbor)

So:

```text
dfs(0,2)=2
```

## Cell `(1,0)=6`

Larger neighbor:

- `(0,0)=9`

So:

```text
dfs(1,0)=1+1=2
```

## Cell `(2,0)=2`

Larger neighbor:

- `(1,0)=6`

So:

```text
dfs(2,0)=1+dfs(1,0)=1+2=3
```

## Cell `(2,1)=1`

Larger neighbors:

- `(2,0)=2`
- `(1,1)=6`

So:

```text
dfs(2,1)=1+max(dfs(2,0), dfs(1,1))
```

`dfs(2,0)=3`
`dfs(1,1)=2` because it can go to `9`

Therefore:

```text
dfs(2,1)=1+3=4
```

This corresponds to:

```text
1 -> 2 -> 6 -> 9
```

Final answer:

```text
4
```

---

# Comparing All Approaches

| Approach                  |              Time |         Space | Notes                       |
| ------------------------- | ----------------: | ------------: | --------------------------- |
| Pure DFS / Backtracking   |       Exponential | O(m\*n) stack | Conceptual only             |
| DFS + Memoization         |           O(m\*n) |       O(m\*n) | Standard best solution      |
| Topological BFS (Kahn)    |           O(m\*n) |       O(m\*n) | Best iterative DAG solution |
| Sort Cells + DAG DP       |   O(m*n log(m*n)) |       O(m\*n) | Clean but slower            |
| Explicit DAG Construction |           O(m\*n) |       O(m\*n) | Correct but overengineered  |
| Repeated Relaxation       | Worse than needed |       O(m\*n) | Not recommended             |

---

# Recommended Final Java Solution

## Best Practical Solution: DFS + Memoization

```java
class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] memo = new int[rows][cols];

        int ans = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                ans = Math.max(ans, dfs(matrix, r, c, memo));
            }
        }

        return ans;
    }

    private int dfs(int[][] matrix, int r, int c, int[][] memo) {
        if (memo[r][c] != 0) {
            return memo[r][c];
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int best = 1;

        for (int[] d : DIRS) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && matrix[nr][nc] > matrix[r][c]) {
                best = Math.max(best, 1 + dfs(matrix, nr, nc, memo));
            }
        }

        memo[r][c] = best;
        return best;
    }
}
```

---

## Complexity of Recommended Solution

- **Time Complexity:** `O(m * n)`
- **Space Complexity:** `O(m * n)`

---

# Alternative Recommended Iterative Solution: Topological BFS

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    private static final int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] indegree = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                for (int[] d : DIRS) {
                    int nr = r + d[0];
                    int nc = c + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && matrix[nr][nc] < matrix[r][c]) {
                        indegree[r][c]++;
                    }
                }
            }
        }

        Queue<int[]> queue = new ArrayDeque<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (indegree[r][c] == 0) {
                    queue.offer(new int[]{r, c});
                }
            }
        }

        int layers = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            layers++;

            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                int r = cell[0], c = cell[1];

                for (int[] d : DIRS) {
                    int nr = r + d[0];
                    int nc = c + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && matrix[nr][nc] > matrix[r][c]) {
                        indegree[nr][nc]--;
                        if (indegree[nr][nc] == 0) {
                            queue.offer(new int[]{nr, nc});
                        }
                    }
                }
            }
        }

        return layers;
    }
}
```

---

## Complexity of Alternative Iterative Solution

- **Time Complexity:** `O(m * n)`
- **Space Complexity:** `O(m * n)`

---

# Common Mistakes

## Mistake 1: Using a visited array globally

A global visited set is wrong for this problem.

Why?
Because the same cell may belong to many different candidate paths.

You need memoization of best answer from a cell, not a permanent ban on revisiting.

---

## Mistake 2: Assuming cycles are possible

Strictly increasing movement means cycles cannot exist.

This is why memoized DFS is safe.

---

## Mistake 3: Confusing this with longest path in an arbitrary graph

Longest path in a general graph is hard.

But here the graph is a DAG because values must strictly increase.

That changes everything.

---

## Mistake 4: Forgetting strict inequality

You may move only if:

```text
next > current
```

not `>=`.

Equal-value cells do not continue an increasing path.

---

# Pattern Recognition

This problem is a classic signal for:

- memoized DFS on a grid
- DAG longest path
- topological BFS layer peeling

Useful cues:

- grid problem
- path length optimization
- movement restricted by monotonic rule (`>`)

Related problems:

- Number of Increasing Paths in a Grid
- Pacific Atlantic Water Flow
- Longest path in DAG
- Matrix longest path with constraints

---

# Interview Explanation Template

A strong explanation:

1. Model each cell as a node, with edges to larger-valued neighbors.
2. Because edges go only from smaller to larger values, the graph is acyclic.
3. Let `dp[r][c]` be the longest increasing path starting from `(r, c)`.
4. Then:
   `dp[r][c] = 1 + max(dp[nr][nc])` over all larger neighbors.
5. Use DFS + memoization so each cell is solved once.
6. Final answer is the maximum `dp[r][c]` over all cells.

Time:

- `O(m * n)`

Space:

- `O(m * n)`

---

# Final Takeaway

The key is to stop thinking of this as arbitrary path search.

Because moves must be strictly increasing, the matrix induces a DAG.

That turns the problem into:

> compute the longest path in a DAG over grid cells

The best practical solution is **DFS + memoization**, and the clean iterative alternative is **topological BFS by indegree**.
