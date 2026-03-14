# 2850. Minimum Moves to Spread Stones Over Grid

## Problem Restatement

You are given a `3 x 3` grid.

- `grid[i][j]` tells how many stones are currently in cell `(i, j)`
- the total number of stones in the whole grid is exactly `9`
- the target is to make every cell contain exactly `1` stone

In one move, you can move **one stone** to a neighboring cell sharing a side:

- up
- down
- left
- right

Return the **minimum number of moves** needed to reach the target configuration.

---

## Key Observations

### 1. Final state is fixed

Because there are 9 cells and exactly 9 stones total, the final state must be:

```text
every cell has exactly 1 stone
```

There is no ambiguity about the target.

---

### 2. Extra stones and empty cells matter

A cell with:

- `0` stones needs `1` incoming stone
- `1` stone is already correct
- `x > 1` stones has `x - 1` extra stones that must be moved out

So the problem reduces to:

> move extra stones into empty cells with minimum total movement cost

---

### 3. Cost between two cells is Manhattan distance

Since each move goes only to a side-adjacent cell, moving one stone from:

```text
(r1, c1) -> (r2, c2)
```

costs:

```text
|r1 - r2| + |c1 - c2|
```

So this is an assignment / matching problem on a tiny grid.

---

### 4. The state space is very small

This is a `3 x 3` grid only.

That means several exponential or search-based approaches are totally feasible.

---

# Approach 1: Backtracking by Assigning Extra Stones to Empty Cells

## Core Idea

Build two lists:

1. `extras`: every extra stone position listed separately
2. `empties`: every empty cell position

For example, if a cell has `3` stones, it contributes **2 entries** into `extras`, because it has 2 movable surplus stones.

Then:

- every empty cell needs exactly one stone
- every extra stone must be assigned to one empty cell
- the cost of assignment is Manhattan distance

So we just try all matchings and take the minimum total cost.

---

## Why listing extra stones separately works

Suppose a cell has value `4`.

That means:

- 1 stone stays there
- 3 stones must move elsewhere

So in the matching view, that cell contributes **3 distinct extra stones**.

Even though they start at the same cell, treating them as separate items is correct because each of them must be sent to some empty position.

---

## Backtracking Structure

Let:

- `extras.size() = m`
- `empties.size() = m`

We recursively assign the `idx-th` extra stone to one unused empty cell.

At each step:

- try all currently unused empties
- add distance cost
- recurse
- backtrack

Take the minimum.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumMoves(int[][] grid) {
        List<int[]> extras = new ArrayList<>();
        List<int[]> empties = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == 0) {
                    empties.add(new int[]{r, c});
                } else if (grid[r][c] > 1) {
                    for (int k = 0; k < grid[r][c] - 1; k++) {
                        extras.add(new int[]{r, c});
                    }
                }
            }
        }

        boolean[] used = new boolean[empties.size()];
        return dfs(extras, empties, used, 0);
    }

    private int dfs(List<int[]> extras, List<int[]> empties, boolean[] used, int idx) {
        if (idx == extras.size()) return 0;

        int ans = Integer.MAX_VALUE;
        int[] from = extras.get(idx);

        for (int i = 0; i < empties.size(); i++) {
            if (used[i]) continue;

            used[i] = true;
            int[] to = empties.get(i);

            int cost = Math.abs(from[0] - to[0]) + Math.abs(from[1] - to[1]);
            ans = Math.min(ans, cost + dfs(extras, empties, used, idx + 1));

            used[i] = false;
        }

        return ans;
    }
}
```

---

## Complexity

At most there can be 8 extra stones and 8 empty cells.

So the backtracking explores permutations of size up to 8:

```text
O(m!)
```

where `m <= 8`.

That is completely feasible.

Space complexity:

```text
O(m)
```

for recursion and used array.

---

## Pros

- Very intuitive
- Easy to derive
- Fast enough because the grid is tiny

## Cons

- Still factorial in form
- Does repeated work if many states overlap

---

# Approach 2: Bitmask DP for Assignment (Recommended)

## Core Idea

This is the optimized version of Approach 1.

Again build:

- `extras`
- `empties`

Now instead of plain backtracking, use DP over which empty cells have already been assigned.

Let:

```text
dp[mask] = minimum cost after assigning the first popcount(mask) extra stones
           to the empty cells selected in mask
```

If `mask` has `k` bits set, then we have already assigned:

```text
extras[0], extras[1], ..., extras[k-1]
```

The next extra stone to place is:

```text
extras[k]
```

Then try assigning it to any empty cell not yet in `mask`.

---

## State Definition

- `mask` is a bitmask over empty cells
- bit `i` = 1 means empty cell `i` has already been filled

If `mask` uses `k` cells, then we have assigned `k` extra stones.

---

## Transition

Let:

```text
k = bitcount(mask)
```

Then next source stone is `extras[k]`.

For each empty target `j` not used in `mask`:

```text
nextMask = mask | (1 << j)
dp[nextMask] = min(dp[nextMask], dp[mask] + dist(extras[k], empties[j]))
```

---

## Why this works

The only thing that matters during matching is:

- how many extra stones have already been assigned
- which empty cells are already taken

The exact order of previous assignments does not matter beyond that.

This is classic assignment DP.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumMoves(int[][] grid) {
        List<int[]> extras = new ArrayList<>();
        List<int[]> empties = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == 0) {
                    empties.add(new int[]{r, c});
                } else if (grid[r][c] > 1) {
                    for (int k = 0; k < grid[r][c] - 1; k++) {
                        extras.add(new int[]{r, c});
                    }
                }
            }
        }

        int m = empties.size();
        int totalMasks = 1 << m;
        int[] dp = new int[totalMasks];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue;

            int k = Integer.bitCount(mask);
            if (k == m) continue;

            int[] from = extras.get(k);

            for (int j = 0; j < m; j++) {
                if ((mask & (1 << j)) != 0) continue;

                int[] to = empties.get(j);
                int cost = Math.abs(from[0] - to[0]) + Math.abs(from[1] - to[1]);

                int nextMask = mask | (1 << j);
                dp[nextMask] = Math.min(dp[nextMask], dp[mask] + cost);
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

## Complexity

If there are `m` empty cells, then:

- number of states = `2^m`
- each state tries up to `m` transitions

So time complexity:

```text
O(m * 2^m)
```

with `m <= 8`, which is tiny.

Space complexity:

```text
O(2^m)
```

---

## Pros

- Clean and efficient
- Avoids repeated subproblems
- Best practical solution

## Cons

- Needs comfort with bitmask DP

---

# Approach 3: BFS on Grid States

## Core Idea

Think of the whole grid configuration as a state.

From any state:

- pick a cell with at least 2 stones
- move one stone to one of its 4 neighbors
- that gives a new state

Since every move has uniform cost `1`, we can run BFS from the initial grid until we reach the target grid where every cell has exactly one stone.

Because the grid is only 3x3 and total stones are fixed at 9, the total number of reachable states is actually manageable.

---

## Why BFS gives the minimum

BFS explores states by increasing number of moves.

So the first time we reach the target state, that distance is the minimum number of moves.

---

## State Encoding

We can encode the 3x3 grid into a string like:

```text
"110111121"
```

or into an integer / tuple.

That lets us store visited states.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumMoves(int[][] grid) {
        String start = encode(grid);
        String target = "111111111";

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        int steps = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int s = 0; s < size; s++) {
                String cur = queue.poll();
                if (cur.equals(target)) return steps;

                int[] arr = new int[9];
                for (int i = 0; i < 9; i++) {
                    arr[i] = cur.charAt(i) - '0';
                }

                for (int i = 0; i < 9; i++) {
                    if (arr[i] <= 1) continue;

                    int r = i / 3, c = i % 3;

                    for (int[] d : dirs) {
                        int nr = r + d[0];
                        int nc = c + d[1];

                        if (nr < 0 || nr >= 3 || nc < 0 || nc >= 3) continue;

                        int ni = nr * 3 + nc;

                        arr[i]--;
                        arr[ni]++;

                        String next = encode(arr);
                        if (visited.add(next)) {
                            queue.offer(next);
                        }

                        arr[i]++;
                        arr[ni]--;
                    }
                }
            }

            steps++;
        }

        return -1;
    }

    private String encode(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : grid) {
            for (int x : row) {
                sb.append(x);
            }
        }
        return sb.toString();
    }

    private String encode(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int x : arr) {
            sb.append(x);
        }
        return sb.toString();
    }
}
```

---

## Complexity

This depends on the number of reachable states, not just on input size.

Because the grid is constant size, this is still feasible.

In practice, it is fine for a 3x3 grid.

However, it is less elegant than the assignment-based approaches.

---

## Pros

- Very direct “minimum moves” interpretation
- BFS automatically guarantees shortest path

## Cons

- Heavier state exploration than necessary
- Less clean than assignment DP
- More implementation overhead

---

# Approach 4: Brute Force Permutation of Empty Cells

## Idea

This is the raw version of the assignment approach.

Build:

- a list of all extra stones
- a list of empty cells

Then try every permutation of empty cells and compute total matching cost.

This works because the problem size is tiny.

It is very similar to Approach 1, except you can think of it as permuting target empties rather than recursively choosing matches.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumMoves(int[][] grid) {
        List<int[]> extras = new ArrayList<>();
        List<int[]> empties = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == 0) {
                    empties.add(new int[]{r, c});
                } else if (grid[r][c] > 1) {
                    for (int k = 0; k < grid[r][c] - 1; k++) {
                        extras.add(new int[]{r, c});
                    }
                }
            }
        }

        return permuteAndCalc(extras, empties, 0);
    }

    private int permuteAndCalc(List<int[]> extras, List<int[]> empties, int idx) {
        if (idx == empties.size()) {
            int cost = 0;
            for (int i = 0; i < empties.size(); i++) {
                cost += dist(extras.get(i), empties.get(i));
            }
            return cost;
        }

        int ans = Integer.MAX_VALUE;

        for (int i = idx; i < empties.size(); i++) {
            Collections.swap(empties, idx, i);
            ans = Math.min(ans, permuteAndCalc(extras, empties, idx + 1));
            Collections.swap(empties, idx, i);
        }

        return ans;
    }

    private int dist(int[] a, int[] b) {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }
}
```

---

## Complexity

If there are `m` empty cells:

```text
O(m! * m)
```

with `m <= 8`.

Feasible, but inferior to bitmask DP.

---

## Pros

- Simple to understand
- Good stepping stone to assignment thinking

## Cons

- Does more repeated work than DP
- Not the cleanest final solution

---

# Deep Intuition

## Why Manhattan distance is the correct movement cost

A stone moves only through side-adjacent cells.

To go from `(r1, c1)` to `(r2, c2)`, the shortest path always needs:

```text
|r1 - r2| vertical moves + |c1 - c2| horizontal moves
```

So every assignment cost is exactly Manhattan distance.

That turns the original “simulate moves” problem into a matching problem.

---

## Why we do not need to simulate the exact sequence of moves

A common trap is to think:

> “I must simulate all move sequences.”

That is unnecessary.

If we decide that one extra stone from cell `A` goes to empty cell `B`, then the minimum cost is already known:

```text
ManhattanDistance(A, B)
```

The exact intermediate route does not matter, because on a grid with only side moves, shortest path length is fixed by Manhattan distance.

So the problem is not really about pathfinding on the grid.
It is about optimally pairing surplus stones with deficit cells.

---

## Why treating extra stones independently is valid

Suppose one cell has `4` stones.

Then after keeping one stone there, the other three stones are indistinguishable extras.

Each must travel to some empty cell. Their costs add independently.

So it is valid to list the cell three times in `extras`.

This simplifies the problem drastically.

---

# Correctness Sketch for Approach 2

We prove the bitmask DP is correct.

## Step 1: Problem reformulation

Each empty cell needs exactly one incoming stone.
Each extra stone must be moved to exactly one empty cell.
The cost of assigning one extra stone to one empty cell is Manhattan distance.

So the problem becomes a minimum-cost perfect matching between:

- extra stones
- empty cells

## Step 2: DP state meaning

`dp[mask]` = minimum cost after assigning the first `popcount(mask)` extra stones to the empty cells selected by `mask`.

This state is well-defined because the order of extras is fixed.

## Step 3: Transition

If `mask` has `k` bits set, then the next extra stone is `extras[k]`.

Try matching it with any unused empty cell `j`.
That gives:

```text
dp[mask | (1 << j)] = min(dp[mask | (1 << j)], dp[mask] + dist(extras[k], empties[j]))
```

This considers every valid next assignment.

## Step 4: Optimal substructure

If an optimal complete matching ends with extra stone `k` assigned to empty `j`, then the earlier assignments must already form an optimal assignment for the previous mask. Otherwise we could improve the total cost.

So the recurrence is valid.

## Step 5: Final state

When all empty cells are used, we have assigned every extra stone, so that state gives the minimum total moves.

Thus the algorithm is correct.

---

# Example Walkthrough

## Example 1

```text
grid = [[1,1,0],
        [1,1,1],
        [1,2,1]]
```

Empty cells:

```text
(0,2)
```

Extra stones:

```text
(2,1)
```

Only one matching exists.

Distance:

```text
|2 - 0| + |1 - 2| = 2 + 1 = 3
```

So answer is:

```text
3
```

That matches the example.

---

## Example 2

```text
grid = [[1,3,0],
        [1,0,0],
        [1,0,3]]
```

Extras:

- `(0,1)` contributes 2 extra stones
- `(2,2)` contributes 2 extra stones

So extras list can be:

```text
[(0,1), (0,1), (2,2), (2,2)]
```

Empties:

```text
[(0,2), (1,1), (1,2), (2,1)]
```

Now test assignments and minimize total Manhattan distance.
Best total turns out to be:

```text
4
```

which matches the example.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    public int minimumMoves(int[][] grid) {
        List<int[]> extras = new ArrayList<>();
        List<int[]> empties = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == 0) {
                    empties.add(new int[]{r, c});
                } else if (grid[r][c] > 1) {
                    for (int k = 0; k < grid[r][c] - 1; k++) {
                        extras.add(new int[]{r, c});
                    }
                }
            }
        }

        int m = empties.size();
        int totalMasks = 1 << m;
        int[] dp = new int[totalMasks];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue;

            int k = Integer.bitCount(mask);
            if (k == m) continue;

            int[] from = extras.get(k);

            for (int j = 0; j < m; j++) {
                if ((mask & (1 << j)) != 0) continue;

                int[] to = empties.get(j);
                int cost = Math.abs(from[0] - to[0]) + Math.abs(from[1] - to[1]);

                int nextMask = mask | (1 << j);
                dp[nextMask] = Math.min(dp[nextMask], dp[mask] + cost);
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                    |       Time Complexity |      Space Complexity | Recommended |
| ---------- | -------------------------------------------- | --------------------: | --------------------: | ----------- |
| Approach 1 | Backtracking assignment of extras to empties |               `O(m!)` |                `O(m)` | Good        |
| Approach 2 | Bitmask DP assignment                        |          `O(m * 2^m)` |              `O(2^m)` | Best        |
| Approach 3 | BFS over full grid states                    | State-space dependent | State-space dependent | Okay        |
| Approach 4 | Brute force permutation of targets           |           `O(m! * m)` |                `O(m)` | Okay        |

Here `m` is the number of empty cells, and `m <= 8`.

---

# Pattern Recognition Takeaway

This problem is a strong signal for one of these patterns:

- assignment / matching
- tiny-state bitmask DP
- surplus-to-deficit redistribution

Whenever you see:

- a very small fixed board
- some cells with extra units
- some cells with deficits
- move cost equal to Manhattan distance

it is often best to convert the problem into:

> assign surplus items to deficit positions with minimum total cost

That is the cleanest lens here.

---

# Final Takeaway

The best way to think about this problem is:

1. identify extra stones and empty cells
2. each extra stone must go to one empty cell
3. the cost is Manhattan distance
4. solve the assignment problem with bitmask DP

That gives a compact, correct, and efficient solution.
