# 741. Cherry Pickup — Exhaustive Solution Notes

## Overview

This problem looks like a pathfinding problem, but the real trick is to **reformulate** it.

We are asked to:

1. go from `(0, 0)` to `(n - 1, n - 1)` using only **right** and **down**
2. then return from `(n - 1, n - 1)` to `(0, 0)` using only **left** and **up**
3. collect as many cherries as possible
4. avoid thorns (`-1`)
5. never count the same cherry twice

At first glance, this seems like a two-trip problem.
The key insight is:

> Instead of thinking about one person going forward and then backward, think of **two people walking from `(0,0)` to `(n-1,n-1)` at the same time**.

That transformation turns the problem into dynamic programming.

This write-up explains three approaches from the prompt:

1. **Greedy [Wrong Answer]**
2. **Dynamic Programming (Top Down)**
3. **Dynamic Programming (Bottom Up)**

---

## Problem Statement

You are given an `n x n` grid.

Each cell contains:

- `0` → empty
- `1` → cherry
- `-1` → thorn / blocked cell

You start at `(0,0)`, move to `(n-1,n-1)` by going only **right** or **down**, then return to `(0,0)` by going only **left** or \*\*up`.

Every time you step on a cherry cell, you collect the cherry and that cell becomes `0`.

Return the maximum number of cherries you can collect.

If there is no valid path from start to end, return `0`.

---

## Example 1

**Input**

```text
grid = [[0,1,-1],
        [1,0,-1],
        [1,1,1]]
```

**Output**

```text
5
```

**Explanation**

One optimal route forward:

```text
(0,0) -> (1,0) -> (2,0) -> (2,1) -> (2,2)
```

Cherries collected on the first trip: `4`

Then a good return trip collects `1` more cherry.

Total:

```text
5
```

---

## Example 2

**Input**

```text
grid = [[1,1,-1],
        [1,-1,1],
        [-1,1,1]]
```

**Output**

```text
0
```

**Explanation**

There is no valid path from `(0,0)` to `(n-1,n-1)`.

So no cherries can be collected.

---

## Constraints

- `1 <= n <= 50`
- `grid[i][j]` is `-1`, `0`, or `1`
- `grid[0][0] != -1`
- `grid[n-1][n-1] != -1`

---

# Why Greedy Fails

## Greedy Idea

A tempting idea is:

1. find the single path from start to end that collects the most cherries
2. remove those cherries
3. find the best second path on the remaining grid

This feels reasonable, but it is wrong.

Why?

Because the first path may take cherries that would have been better shared across two paths.
A locally optimal first trip can destroy the globally optimal two-trip solution.

So this problem cannot be solved by optimizing the first trip independently.

---

# The Core Transformation

Instead of:

- one person going forward
- then backward

imagine:

- **person A** goes from `(0,0)` to `(n-1,n-1)`
- **person B** also goes from `(0,0)` to `(n-1,n-1)`

Both move only **right** or **down**.

Why is this equivalent?

Because reversing the return trip gives another forward trip from `(0,0)` to `(n-1,n-1)`.

Now we only need to maximize the total cherries picked by these two synchronized paths.

If both people step on the same cell, we count that cherry only once.

This is the standard reformulation that makes the problem solvable.

---

# Important Observation About Steps

Suppose both people have taken exactly `t` moves.

If person 1 is at:

```text
(r1, c1)
```

and person 2 is at:

```text
(r2, c2)
```

then:

```text
r1 + c1 = t
r2 + c2 = t
```

So once we know:

- `r1`
- `c1`
- `c2`

we can derive:

```text
r2 = r1 + c1 - c2
```

That reduces the state dimension from 4 coordinates to 3.

This is the key DP optimization.

---

# Approach 1: Greedy [Wrong Answer]

## Intuition

The idea is:

- compute the best possible path from top-left to bottom-right
- collect cherries along that path
- remove those cherries from the grid
- compute the best path again

This can be implemented with a standard DP for max-path-sum under blocked cells.

---

## Why It Fails

The problem is that the best first path may block the best global pairing of two paths.

A counterexample exists where choosing a slightly worse first path allows a much better second path, leading to a better total.

So this strategy is not reliable.

---

## Java Implementation — Greedy (Incorrect)

```java
class Solution {
    public int cherryPickup(int[][] grid) {
        int ans = 0;
        int[][] path = bestPath(grid);
        if (path == null) {
            return 0;
        }
        for (int[] step : path) {
            ans += grid[step[0]][step[1]];
            grid[step[0]][step[1]] = 0;
        }

        for (int[] step : bestPath(grid)) {
            ans += grid[step[0]][step[1]];
        }

        return ans;
    }

    public int[][] bestPath(int[][] grid) {
        int N = grid.length;
        int[][] dp = new int[N][N];
        for (int[] row : dp) {
            Arrays.fill(row, Integer.MIN_VALUE);
        }
        dp[N - 1][N - 1] = grid[N - 1][N - 1];
        for (int i = N - 1; i >= 0; --i) {
            for (int j = N - 1; j >= 0; --j) {
                if (grid[i][j] >= 0 && (i != N - 1 || j != N - 1)) {
                    dp[i][j] = Math.max(
                        i + 1 < N ? dp[i + 1][j] : Integer.MIN_VALUE,
                        j + 1 < N ? dp[i][j + 1] : Integer.MIN_VALUE
                    );
                    dp[i][j] += grid[i][j];
                }
            }
        }
        if (dp[0][0] < 0) {
            return null;
        }
        int[][] ans = new int[2 * N - 1][2];
        int i = 0, j = 0, t = 0;
        while (i != N - 1 || j != N - 1) {
            if (j + 1 == N || i + 1 < N && dp[i + 1][j] >= dp[i][j + 1]) {
                i++;
            } else {
                j++;
            }

            ans[t][0] = i;
            ans[t][1] = j;
            t++;
        }
        return ans;
    }
}
```

---

## Complexity Analysis — Greedy

### Time Complexity

The helper DP for one best path takes:

```text
O(N^2)
```

and it is run twice.

So total time is:

```text
O(N^2)
```

### Space Complexity

The DP table uses:

```text
O(N^2)
```

---

# Approach 2: Dynamic Programming (Top Down)

## Intuition

Now we use the two-person interpretation.

Let the two people currently be at:

- person 1: `(r1, c1)`
- person 2: `(r2, c2)`

Because both have taken the same number of steps:

```text
r1 + c1 = r2 + c2
```

So we only need three independent variables:

```text
(r1, c1, c2)
```

and can compute:

```text
r2 = r1 + c1 - c2
```

Now define:

```text
dp(r1, c1, c2)
```

as the maximum cherries collectable from the state where:

- person 1 is at `(r1, c1)`
- person 2 is at `(r2, c2)`

and both continue to `(n-1, n-1)`.

---

## State Meaning

At any state, the two people are standing on two cells.

We collect:

- `grid[r1][c1]`
- `grid[r2][c2]`

But if they are on the same cell, we must count that cherry only once.

Then both persons make one move each.

Each can move either:

- down
- right

So there are 4 possible next-state combinations:

1. person 1 down, person 2 down
2. person 1 right, person 2 down
3. person 1 down, person 2 right
4. person 1 right, person 2 right

We take the maximum of these 4 choices.

---

## Invalid States

A state is invalid if:

- any coordinate goes out of bounds
- either person lands on a thorn cell (`-1`)

In those cases, return a very negative number so that this path is never chosen.

---

## Base Case

If person 1 reaches `(n-1,n-1)`, then because both took the same number of steps, person 2 must also be there.

So return:

```text
grid[n-1][n-1]
```

That is the last cherry contribution.

---

## Recurrence

Let:

```text
r2 = r1 + c1 - c2
```

Then:

```text
dp(r1, c1, c2) =
    cherries at current positions
    + max of the 4 next moves
```

Current cherries are:

- `grid[r1][c1]`
- plus `grid[r2][c2]` if the positions are different

Then recursively add the best future result.

---

## Why This Avoids Double Counting

If both paths pass through the same cell, we should only count its cherry once, because in the original problem the cherry disappears after being picked once.

So if:

```text
(r1, c1) == (r2, c2)
```

we add the cell’s value only once.

Otherwise, add both values.

---

## Java Implementation — Top-Down DP

```java
class Solution {
    int[][][] memo;
    int[][] grid;
    int N;

    public int cherryPickup(int[][] grid) {
        this.grid = grid;
        N = grid.length;
        memo = new int[N][N][N];

        for (int[][] layer : memo) {
            for (int[] row : layer) {
                Arrays.fill(row, Integer.MIN_VALUE);
            }
        }

        return Math.max(0, dp(0, 0, 0));
    }

    public int dp(int r1, int c1, int c2) {
        int r2 = r1 + c1 - c2;

        if (N == r1 || N == r2 || N == c1 || N == c2 ||
            grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return -999999;
        } else if (r1 == N - 1 && c1 == N - 1) {
            return grid[r1][c1];
        } else if (memo[r1][c1][c2] != Integer.MIN_VALUE) {
            return memo[r1][c1][c2];
        } else {
            int ans = grid[r1][c1];
            if (c1 != c2) {
                ans += grid[r2][c2];
            }

            ans += Math.max(
                Math.max(dp(r1, c1 + 1, c2 + 1), dp(r1 + 1, c1, c2 + 1)),
                Math.max(dp(r1, c1 + 1, c2), dp(r1 + 1, c1, c2))
            );

            memo[r1][c1][c2] = ans;
            return ans;
        }
    }
}
```

---

## Complexity Analysis — Top-Down DP

### Time Complexity

The state is defined by:

- `r1`
- `c1`
- `c2`

Each ranges over `N`.

So the total number of states is:

```text
O(N^3)
```

Each state does constant work, checking 4 transitions.

So total time complexity is:

```text
O(N^3)
```

---

### Space Complexity

The memo array is:

```text
O(N^3)
```

So space complexity is:

```text
O(N^3)
```

---

# Approach 3: Dynamic Programming (Bottom Up)

## Intuition

The top-down DP is correct, but it uses `O(N^3)` memory.

Notice that the recurrence only depends on the **next layer of time**.

Let:

```text
t = r + c
```

be the total number of steps taken.

At step `t`, if person 1 is at column `c1`, then row is:

```text
r1 = t - c1
```

Similarly for person 2:

```text
r2 = t - c2
```

So for a fixed `t`, the state only depends on:

```text
(c1, c2)
```

That means we can process the DP layer by layer in `t`, and only keep two 2D layers at a time.

This reduces memory from `O(N^3)` to `O(N^2)`.

---

## State Definition

At time `t`, let:

```text
dp[c1][c2]
```

be the maximum cherries collected by two people who have both taken `t` steps and are now at:

- person 1: `(t - c1, c1)`
- person 2: `(t - c2, c2)`

Again, we avoid double counting if both occupy the same cell.

---

## Transition

At each time `t`, we compute a new table `dp2`.

Each person could have come from:

- above
- left

That translates to previous columns:

- `c`
- `c - 1`

So we examine all combinations of previous `(c1, c2)` states.

There are again 4 predecessor combinations.

Take the maximum valid predecessor and add the current cherry contribution.

---

## Initialization

At time `t = 0`, both people are at `(0,0)`.

So:

```text
dp[0][0] = grid[0][0]
```

All other states are invalid initially.

---

## Final Answer

After processing all layers up to:

```text
t = 2N - 2
```

the result is at:

```text
dp[N-1][N-1]
```

because both people have reached `(N-1, N-1)`.

If that value is negative, return `0`.

---

## Java Implementation — Bottom-Up DP

```java
class Solution {
    public int cherryPickup(int[][] grid) {
        int N = grid.length;
        int[][] dp = new int[N][N];
        for (int[] row : dp) {
            Arrays.fill(row, Integer.MIN_VALUE);
        }
        dp[0][0] = grid[0][0];

        for (int t = 1; t <= 2 * N - 2; ++t) {
            int[][] dp2 = new int[N][N];
            for (int[] row : dp2) {
                Arrays.fill(row, Integer.MIN_VALUE);
            }

            for (int i = Math.max(0, t - (N - 1)); i <= Math.min(N - 1, t); ++i) {
                for (int j = Math.max(0, t - (N - 1)); j <= Math.min(N - 1, t); ++j) {
                    if (grid[i][t - i] == -1 || grid[j][t - j] == -1) {
                        continue;
                    }

                    int val = grid[i][t - i];
                    if (i != j) {
                        val += grid[j][t - j];
                    }

                    for (int pi = i - 1; pi <= i; ++pi) {
                        for (int pj = j - 1; pj <= j; ++pj) {
                            if (pi >= 0 && pj >= 0) {
                                dp2[i][j] = Math.max(dp2[i][j], dp[pi][pj] + val);
                            }
                        }
                    }
                }
            }
            dp = dp2;
        }

        return Math.max(0, dp[N - 1][N - 1]);
    }
}
```

---

## Complexity Analysis — Bottom-Up DP

### Time Complexity

We iterate over:

- `t` from `1` to `2N - 2`
- `i` across up to `N`
- `j` across up to `N`

That gives:

```text
O(N^3)
```

Each state checks only 4 previous combinations, which is constant.

So total time complexity is:

```text
O(N^3)
```

---

### Space Complexity

We only keep two `N x N` layers:

- `dp`
- `dp2`

So space complexity is:

```text
O(N^2)
```

---

# Why the Two-Person Transformation Is Correct

The original problem is:

- one forward trip
- one backward trip

Reverse the backward trip.

Now both trips become forward trips from `(0,0)` to `(n-1,n-1)`.

The set of visited cells is the same.

The cherry-counting rule is the same if we ensure that shared cells contribute only once.

So the transformed problem is equivalent.

This is the crucial conceptual leap.

---

# Common Mistakes

## 1. Solving forward and backward separately

That misses the interaction between the two trips and usually gives the wrong answer.

---

## 2. Double counting cherries when both paths use the same cell

If both people are on the same cell at the same time-step, count that cherry only once.

---

## 3. Forgetting invalid thorn states

If either person lands on `-1`, the whole state is invalid.

---

## 4. Returning a negative value instead of 0

If no valid full path exists, the answer must be:

```text
0
```

not a negative sentinel.

---

# Comparing the Approaches

## Greedy

### Strengths

- easy to think of initially

### Weaknesses

- incorrect
- ignores the global coupling between the two trips

---

## Top-Down DP

### Strengths

- conceptually clean once the transformation is understood
- direct recurrence
- easy to reason about correctness

### Weaknesses

- uses `O(N^3)` memory
- recursion overhead

---

## Bottom-Up DP

### Strengths

- same optimal time complexity
- reduces space to `O(N^2)`
- avoids recursion

### Weaknesses

- more difficult to derive
- indexing by time layer is less intuitive at first

---

# Final Summary

## Key Transformation

Instead of one round trip, think of:

- two people
- both starting at `(0,0)`
- both moving to `(n-1,n-1)`

This converts the problem into synchronized path DP.

---

## Best Accepted Approaches

### Top-Down DP

- Time: `O(N^3)`
- Space: `O(N^3)`

### Bottom-Up DP

- Time: `O(N^3)`
- Space: `O(N^2)`

---

## Best Practical Solution

The bottom-up DP is usually the best overall because it keeps the same time complexity while reducing memory.

---

# Best Final Java Solution

```java
class Solution {
    public int cherryPickup(int[][] grid) {
        int N = grid.length;
        int[][] dp = new int[N][N];
        for (int[] row : dp) {
            Arrays.fill(row, Integer.MIN_VALUE);
        }
        dp[0][0] = grid[0][0];

        for (int t = 1; t <= 2 * N - 2; ++t) {
            int[][] dp2 = new int[N][N];
            for (int[] row : dp2) {
                Arrays.fill(row, Integer.MIN_VALUE);
            }

            for (int i = Math.max(0, t - (N - 1)); i <= Math.min(N - 1, t); ++i) {
                for (int j = Math.max(0, t - (N - 1)); j <= Math.min(N - 1, t); ++j) {
                    if (grid[i][t - i] == -1 || grid[j][t - j] == -1) {
                        continue;
                    }

                    int val = grid[i][t - i];
                    if (i != j) {
                        val += grid[j][t - j];
                    }

                    for (int pi = i - 1; pi <= i; ++pi) {
                        for (int pj = j - 1; pj <= j; ++pj) {
                            if (pi >= 0 && pj >= 0) {
                                dp2[i][j] = Math.max(dp2[i][j], dp[pi][pj] + val);
                            }
                        }
                    }
                }
            }

            dp = dp2;
        }

        return Math.max(0, dp[N - 1][N - 1]);
    }
}
```

This is the standard optimized dynamic programming solution for Cherry Pickup.
