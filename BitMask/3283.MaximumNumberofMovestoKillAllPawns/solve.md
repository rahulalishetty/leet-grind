# 3283. Maximum Number of Moves to Kill All Pawns

## Problem Restatement

There is a `50 x 50` chessboard with:

- one knight
- several pawns

You are given:

- the knight start position `(kx, ky)`
- an array `positions`, where `positions[i] = [xi, yi]` is the position of the `i-th` pawn

Alice and Bob alternate turns, with **Alice moving first**.

On each turn:

1. the current player chooses **any remaining pawn**
2. the knight captures that pawn using the **minimum possible number of knight moves**
3. only that chosen pawn is removed
4. the knight’s new position becomes the captured pawn’s square

Alice wants to **maximize** the total number of moves made over the whole game.
Bob wants to **minimize** it.

Return the maximum total number of moves Alice can guarantee if both play optimally.

---

## Key Constraints

```text
1 <= number of pawns <= 15
board size = 50 x 50
```

The crucial signal is:

```text
pawns <= 15
```

That strongly suggests:

- bitmask DP
- minimax on subsets
- state compression over remaining pawns

But to use that, we first need pairwise knight distances.

---

# Core Insight

The game has two layers:

## Layer 1: Movement cost

If the knight is currently at position `A` and the chosen next pawn is at `B`, the turn cost is:

```text
minimum knight distance from A to B
```

Because the player must capture the chosen pawn in the fewest possible moves.

## Layer 2: Game strategy

Once all pairwise movement costs are known, the problem becomes:

> starting from the knight position, players alternately choose the next remaining pawn
> Alice maximizes total future cost, Bob minimizes it

That is a standard **minimax + bitmask DP**.

---

# Step 1: Precompute Knight Distances

We need the distance between:

- the knight starting position
- every pawn position
- every pair of pawn positions

If there are `m` pawns, define `m + 1` special points:

- point `0..m-1` = pawns
- point `m` = knight start

Then we need a distance matrix:

```text
dist[i][j] = minimum knight moves from special point i to special point j
```

Since the board is only `50 x 50`, we can run BFS from each special point.

---

## Knight BFS

A knight has 8 possible moves:

```text
(+2, +1), (+2, -1), (-2, +1), (-2, -1),
(+1, +2), (+1, -2), (-1, +2), (-1, -2)
```

BFS from one source cell gives the shortest knight distance to all board cells.

Running BFS from each special point is cheap because:

- board has only `2500` cells
- at most `16` BFS runs

---

# Approach 1: Minimax DP on `(mask, last, turn)` (Recommended)

## High-Level Idea

Let `m = number of pawns`.

We represent the set of already captured pawns by a bitmask:

- bit `i = 1` means pawn `i` is already removed
- bit `i = 0` means pawn `i` is still alive

Let `last` represent the knight's current location among special points:

- `0..m-1` means currently standing on that pawn’s square
- `m` means still at initial knight position

Let `turn` indicate whose turn it is:

- `0` = Alice (maximize)
- `1` = Bob (minimize)

Then define:

```text
dp[mask][last][turn]
```

= optimal total remaining moves from this state onward.

---

## Recurrence

If all pawns are captured:

```text
mask == (1 << m) - 1
```

then no moves remain:

```text
return 0
```

Otherwise:

- if it is Alice’s turn, choose the remaining pawn maximizing:

  ```text
  dist[last][next] + dfs(mask | (1 << next), next, Bob)
  ```

- if it is Bob’s turn, choose the remaining pawn minimizing:
  ```text
  dist[last][next] + dfs(mask | (1 << next), next, Alice)
  ```

This is a straightforward alternating minimax recurrence.

---

## Why this works

At any point, the future only depends on:

- which pawns remain
- where the knight currently is
- whose turn it is

The exact earlier order does not matter except through those three pieces of information.

That is exactly why state compression works here.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    private int[][] dist;
    private Integer[][][] memo;
    private int m;
    private int fullMask;

    public int maxMoves(int kx, int ky, int[][] positions) {
        m = positions.length;
        fullMask = (1 << m) - 1;

        int[][] points = new int[m + 1][2];
        for (int i = 0; i < m; i++) {
            points[i] = positions[i];
        }
        points[m] = new int[]{kx, ky};

        dist = new int[m + 1][m + 1];
        for (int i = 0; i <= m; i++) {
            int[][] d = bfs(points[i][0], points[i][1]);
            for (int j = 0; j <= m; j++) {
                dist[i][j] = d[points[j][0]][points[j][1]];
            }
        }

        memo = new Integer[1 << m][m + 1][2];
        return dfs(0, m, 0);
    }

    private int dfs(int mask, int last, int turn) {
        if (mask == fullMask) return 0;
        if (memo[mask][last][turn] != null) return memo[mask][last][turn];

        int ans;
        if (turn == 0) { // Alice maximizes
            ans = 0;
            for (int next = 0; next < m; next++) {
                if ((mask & (1 << next)) != 0) continue;
                ans = Math.max(ans,
                    dist[last][next] + dfs(mask | (1 << next), next, 1));
            }
        } else { // Bob minimizes
            ans = Integer.MAX_VALUE;
            for (int next = 0; next < m; next++) {
                if ((mask & (1 << next)) != 0) continue;
                ans = Math.min(ans,
                    dist[last][next] + dfs(mask | (1 << next), next, 0));
            }
        }

        return memo[mask][last][turn] = ans;
    }

    private int[][] bfs(int sx, int sy) {
        int[][] d = new int[50][50];
        for (int i = 0; i < 50; i++) {
            Arrays.fill(d[i], -1);
        }

        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{sx, sy});
        d[sx][sy] = 0;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            for (int[] dir : DIRS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx < 0 || nx >= 50 || ny < 0 || ny >= 50) continue;
                if (d[nx][ny] != -1) continue;

                d[nx][ny] = d[x][y] + 1;
                q.offer(new int[]{nx, ny});
            }
        }

        return d;
    }
}
```

---

## Complexity

Let `m = number of pawns <= 15`.

### BFS preprocessing

We run at most `m + 1 <= 16` BFS traversals.

Each BFS visits up to:

```text
50 * 50 = 2500
```

cells.

So preprocessing is roughly:

```text
O((m + 1) * 2500)
```

which is tiny.

### DP states

Number of minimax states:

```text
2^m * (m + 1) * 2
```

Each state tries up to `m` next pawns.

So total DP time:

```text
O(2^m * m^2)
```

Space:

```text
O(2^m * m)
```

up to constant factors.

This is feasible for `m <= 15`.

---

## Pros

- Clean and standard
- Very natural once pairwise distances are known
- Best practical solution

## Cons

- Requires BFS preprocessing + minimax DP together
- Slightly more moving parts than simpler problems

---

# Approach 2: Minimax DP Without Explicit `turn` Dimension

## Idea

We can actually derive whose turn it is from the number of already captured pawns.

If:

```text
captured = bitcount(mask)
```

then:

- if `captured` is even, it is Alice’s turn
- if `captured` is odd, it is Bob’s turn

Because Alice starts first, and each move captures exactly one pawn.

So the DP can be compressed to:

```text
dp[mask][last]
```

and turn is inferred from parity.

---

## Recurrence

Let:

```text
turn = bitcount(mask) % 2
```

- `0` -> Alice maximizes
- `1` -> Bob minimizes

Transition logic is otherwise identical.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    private int[][] dist;
    private Integer[][] memo;
    private int m;
    private int fullMask;

    public int maxMoves(int kx, int ky, int[][] positions) {
        m = positions.length;
        fullMask = (1 << m) - 1;

        int[][] points = new int[m + 1][2];
        for (int i = 0; i < m; i++) {
            points[i] = positions[i];
        }
        points[m] = new int[]{kx, ky};

        dist = new int[m + 1][m + 1];
        for (int i = 0; i <= m; i++) {
            int[][] d = bfs(points[i][0], points[i][1]);
            for (int j = 0; j <= m; j++) {
                dist[i][j] = d[points[j][0]][points[j][1]];
            }
        }

        memo = new Integer[1 << m][m + 1];
        return dfs(0, m);
    }

    private int dfs(int mask, int last) {
        if (mask == fullMask) return 0;
        if (memo[mask][last] != null) return memo[mask][last];

        boolean aliceTurn = (Integer.bitCount(mask) % 2 == 0);
        int ans = aliceTurn ? 0 : Integer.MAX_VALUE;

        for (int next = 0; next < m; next++) {
            if ((mask & (1 << next)) != 0) continue;

            int candidate = dist[last][next] + dfs(mask | (1 << next), next);

            if (aliceTurn) {
                ans = Math.max(ans, candidate);
            } else {
                ans = Math.min(ans, candidate);
            }
        }

        return memo[mask][last] = ans;
    }

    private int[][] bfs(int sx, int sy) {
        int[][] d = new int[50][50];
        for (int i = 0; i < 50; i++) {
            Arrays.fill(d[i], -1);
        }

        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{sx, sy});
        d[sx][sy] = 0;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            for (int[] dir : DIRS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx < 0 || nx >= 50 || ny < 0 || ny >= 50) continue;
                if (d[nx][ny] != -1) continue;

                d[nx][ny] = d[x][y] + 1;
                q.offer(new int[]{nx, ny});
            }
        }

        return d;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
O(2^m * m^2)
```

plus BFS preprocessing.

---

## Pros

- Slightly smaller DP state
- Elegant use of parity

## Cons

- Slightly less explicit than storing turn directly
- Same asymptotic complexity

---

# Approach 3: Bottom-Up Game DP on Subsets

## Idea

Instead of recursive minimax, we can fill states iteratively.

For each subset `mask` and endpoint `last`, compute the optimal result for that state based on the parity of `mask`.

This works because transitions always go from smaller captured sets to larger captured sets.

However, bottom-up implementation is a bit less natural than top-down minimax here.

---

## State

```text
dp[mask][last]
```

= optimal total future moves after exactly `mask` has been captured and knight is at `last`.

To compute it, we inspect all `next` not in `mask`.

If parity of `mask` indicates Alice’s turn, take max; otherwise take min.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    public int maxMoves(int kx, int ky, int[][] positions) {
        int m = positions.length;
        int fullMask = (1 << m) - 1;

        int[][] points = new int[m + 1][2];
        for (int i = 0; i < m; i++) points[i] = positions[i];
        points[m] = new int[]{kx, ky};

        int[][] dist = new int[m + 1][m + 1];
        for (int i = 0; i <= m; i++) {
            int[][] d = bfs(points[i][0], points[i][1]);
            for (int j = 0; j <= m; j++) {
                dist[i][j] = d[points[j][0]][points[j][1]];
            }
        }

        int[][] dp = new int[1 << m][m + 1];

        for (int mask = fullMask; mask >= 0; mask--) {
            for (int last = 0; last <= m; last++) {
                if (mask == fullMask) {
                    dp[mask][last] = 0;
                    continue;
                }

                boolean aliceTurn = (Integer.bitCount(mask) % 2 == 0);
                int best = aliceTurn ? 0 : Integer.MAX_VALUE;

                for (int next = 0; next < m; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    int candidate = dist[last][next] + dp[mask | (1 << next)][next];

                    if (aliceTurn) best = Math.max(best, candidate);
                    else best = Math.min(best, candidate);
                }

                dp[mask][last] = best;
            }
        }

        return dp[0][m];
    }

    private int[][] bfs(int sx, int sy) {
        int[][] d = new int[50][50];
        for (int i = 0; i < 50; i++) Arrays.fill(d[i], -1);

        int[][] dirs = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{sx, sy});
        d[sx][sy] = 0;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            for (int[] dir : dirs) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx < 0 || nx >= 50 || ny < 0 || ny >= 50) continue;
                if (d[nx][ny] != -1) continue;

                d[nx][ny] = d[x][y] + 1;
                q.offer(new int[]{nx, ny});
            }
        }

        return d;
    }
}
```

---

## Complexity

Same overall complexity:

```text
O(2^m * m^2)
```

plus BFS preprocessing.

---

## Pros

- Iterative
- No recursion depth concerns

## Cons

- Less intuitive than memoized minimax
- More awkward to explain

---

# Approach 4: Plain Minimax Backtracking Without Memoization

## Idea

At each turn:

- try every remaining pawn
- recursively solve the rest of the game

Alice takes the maximum, Bob takes the minimum.

This is the most direct game-theory formulation.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    private int[][] dist;
    private int m;
    private int fullMask;

    public int maxMoves(int kx, int ky, int[][] positions) {
        m = positions.length;
        fullMask = (1 << m) - 1;

        int[][] points = new int[m + 1][2];
        for (int i = 0; i < m; i++) points[i] = positions[i];
        points[m] = new int[]{kx, ky};

        dist = new int[m + 1][m + 1];
        for (int i = 0; i <= m; i++) {
            int[][] d = bfs(points[i][0], points[i][1]);
            for (int j = 0; j <= m; j++) {
                dist[i][j] = d[points[j][0]][points[j][1]];
            }
        }

        return solve(0, m, true);
    }

    private int solve(int mask, int last, boolean alice) {
        if (mask == fullMask) return 0;

        int ans = alice ? 0 : Integer.MAX_VALUE;

        for (int next = 0; next < m; next++) {
            if ((mask & (1 << next)) != 0) continue;

            int candidate = dist[last][next] + solve(mask | (1 << next), next, !alice);

            if (alice) ans = Math.max(ans, candidate);
            else ans = Math.min(ans, candidate);
        }

        return ans;
    }

    private int[][] bfs(int sx, int sy) {
        int[][] d = new int[50][50];
        for (int i = 0; i < 50; i++) Arrays.fill(d[i], -1);

        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{sx, sy});
        d[sx][sy] = 0;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            for (int[] dir : DIRS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx < 0 || nx >= 50 || ny < 0 || ny >= 50) continue;
                if (d[nx][ny] != -1) continue;

                d[nx][ny] = d[x][y] + 1;
                q.offer(new int[]{nx, ny});
            }
        }

        return d;
    }
}
```

---

## Complexity

This explores all possible orders of capturing pawns:

```text
O(m!)
```

after preprocessing.

That is too slow for `m = 15`.

---

## Pros

- Very direct
- Easy to derive conceptually

## Cons

- Recomputes the same subgames many times
- Not feasible for the upper limit

---

# Deep Intuition

## Why the knight passing over pawns does not complicate the state

A common concern is:

> “If the knight passes through squares containing other pawns, do those pawns affect the movement?”

No.

The problem explicitly says:

- only the chosen pawn is captured on that turn
- the knight may pass other pawns without capturing them

So other pawns do **not** block movement and do **not** alter shortest-path distance.

This is crucial because it means each turn cost depends only on:

- current knight square
- chosen pawn square

not on the locations of other surviving pawns.

That is exactly why pairwise distances are enough.

---

## Why this is a minimax TSP-like problem

After precomputing distances, each game state asks:

- which targets remain
- where am I now
- whose turn is it

Then choose the next target, paying the transition cost.

That is structurally similar to:

- subset DP over permutations
- but with alternating max/min instead of plain minimization

So you can think of it as a **game-theoretic subset DP**.

---

## Why subset + last is enough

Suppose two different move sequences lead to the same situation:

- same set of pawns already captured
- knight standing on the same current pawn
- same player to move

From that moment onward, the future possibilities and payoffs are identical.

Therefore those histories can share one DP state.

That is the overlapping subproblem that memoization exploits.

---

# Correctness Sketch for Approach 1

We prove the minimax DP is correct.

## Step 1: Distance preprocessing is correct

For a knight on an unblocked chessboard, BFS computes the minimum number of moves to every cell.

Since other pawns do not interfere with movement, the turn cost for capturing a chosen pawn is exactly the BFS distance between current knight location and that pawn.

So the `dist` matrix is correct.

## Step 2: State meaning

`dfs(mask, last, turn)` is the optimal total remaining move count from the state where:

- pawns in `mask` are already removed
- knight is currently at special point `last`
- `turn` indicates whether Alice or Bob moves next

This is a complete description of the future game.

## Step 3: Recurrence

If all pawns are captured, the remaining total is `0`.

Otherwise, the current player must choose one remaining pawn `next`.
That immediately contributes `dist[last][next]`, and then the game continues from the next state.

- Alice picks the choice maximizing total future moves
- Bob picks the choice minimizing total future moves

So the recurrence exactly matches optimal play.

## Step 4: Optimal substructure

After choosing a pawn, the remaining game is again the same kind of game on a smaller set of pawns. Therefore the value of the full game is determined by the value of the resulting subgame.

Thus the DP computes the correct minimax result.

---

# Example Walkthrough

## Example 1

```text
kx = 1, ky = 1
positions = [[0,0]]
```

Only one pawn exists.

Alice must choose it.

Knight distance from `(1,1)` to `(0,0)` is `4`.

So total result is:

```text
4
```

---

## Example 2

```text
kx = 0, ky = 2
positions = [[1,1],[2,2],[3,3]]
```

Alice chooses first and wants a large total.

A strong choice is pawn `(2,2)` with distance `2`.

Then Bob chooses from the new knight location `(2,2)` and tries to keep total low.

Optimal play yields total:

```text
8
```

which matches the statement.

---

## Example 3

```text
kx = 0, ky = 0
positions = [[1,2],[2,4]]
```

Distances:

- from start `(0,0)` to `(1,2)` = `1`
- from start `(0,0)` to `(2,4)` = `2`
- between `(2,4)` and `(1,2)` = `1`

Alice maximizes total, so she chooses `(2,4)` first for cost `2`.

Then Bob takes the last pawn for cost `1`.

Total:

```text
3
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    private int[][] dist;
    private Integer[][] memo;
    private int m;
    private int fullMask;

    public int maxMoves(int kx, int ky, int[][] positions) {
        m = positions.length;
        fullMask = (1 << m) - 1;

        int[][] points = new int[m + 1][2];
        for (int i = 0; i < m; i++) {
            points[i] = positions[i];
        }
        points[m] = new int[]{kx, ky};

        // Precompute pairwise knight distances
        dist = new int[m + 1][m + 1];
        for (int i = 0; i <= m; i++) {
            int[][] d = bfs(points[i][0], points[i][1]);
            for (int j = 0; j <= m; j++) {
                dist[i][j] = d[points[j][0]][points[j][1]];
            }
        }

        memo = new Integer[1 << m][m + 1];
        return dfs(0, m);
    }

    private int dfs(int mask, int last) {
        if (mask == fullMask) return 0;
        if (memo[mask][last] != null) return memo[mask][last];

        boolean aliceTurn = (Integer.bitCount(mask) % 2 == 0);
        int ans = aliceTurn ? 0 : Integer.MAX_VALUE;

        for (int next = 0; next < m; next++) {
            if ((mask & (1 << next)) != 0) continue;

            int candidate = dist[last][next] + dfs(mask | (1 << next), next);

            if (aliceTurn) {
                ans = Math.max(ans, candidate);
            } else {
                ans = Math.min(ans, candidate);
            }
        }

        return memo[mask][last] = ans;
    }

    private int[][] bfs(int sx, int sy) {
        int[][] d = new int[50][50];
        for (int i = 0; i < 50; i++) {
            Arrays.fill(d[i], -1);
        }

        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{sx, sy});
        d[sx][sy] = 0;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            for (int[] dir : DIRS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx < 0 || nx >= 50 || ny < 0 || ny >= 50) continue;
                if (d[nx][ny] != -1) continue;

                d[nx][ny] = d[x][y] + 1;
                q.offer(new int[]{nx, ny});
            }
        }

        return d;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                                   |               Time Complexity | Space Complexity | Recommended |
| ---------- | ----------------------------------------------------------- | ----------------------------: | ---------------: | ----------- |
| Approach 1 | BFS preprocessing + minimax DP with explicit turn           | `O(2500 * (m+1) + 2^m * m^2)` |     `O(2^m * m)` | Yes         |
| Approach 2 | BFS preprocessing + minimax DP with turn inferred by parity | `O(2500 * (m+1) + 2^m * m^2)` |     `O(2^m * m)` | Yes         |
| Approach 3 | Bottom-up subset game DP                                    | `O(2500 * (m+1) + 2^m * m^2)` |     `O(2^m * m)` | Good        |
| Approach 4 | Plain minimax backtracking                                  |        `O(2500 * (m+1) + m!)` |  Recursive stack | No          |

Here `m = positions.length <= 15`.

---

# Pattern Recognition Takeaway

This problem has a very recognizable structure:

- board movement distances can be precomputed independently
- number of meaningful targets is small
- players alternate maximize / minimize
- order of visiting targets matters

That strongly suggests:

1. precompute shortest path distances between all important positions
2. compress remaining targets into a bitmask
3. run minimax DP on subsets

This pattern appears often in small-target adversarial path problems.

---

# Final Takeaway

The cleanest way to solve this problem is:

1. treat the knight start and each pawn as special points
2. precompute all pairwise knight distances using BFS
3. use minimax DP on `(capturedMask, currentPosition)`
4. infer turn from the parity of `capturedMask` or store it explicitly
5. Alice takes max, Bob takes min

That gives an efficient and correct solution for up to 15 pawns.
