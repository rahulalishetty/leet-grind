# 1595. Minimum Cost to Connect Two Groups of Points — Exhaustive Java Notes

## Problem Statement

You are given two groups of points:

- group 1 has `size1` points
- group 2 has `size2` points
- `size1 >= size2`

You are also given a cost matrix:

```text
cost[i][j]
```

which means:

> the cost to connect point `i` in group 1 with point `j` in group 2

The two groups are considered fully connected if:

- every point in group 1 is connected to **at least one** point in group 2
- every point in group 2 is connected to **at least one** point in group 1

A point may connect to multiple points on the opposite side.

Return the **minimum total cost** to connect the two groups.

---

## Example 1

```text
Input:
cost = [[15, 96],
        [36,  2]]

Output:
17
```

Explanation:

- connect group1 point 0 to group2 point 0 → cost 15
- connect group1 point 1 to group2 point 1 → cost 2

Total:

```text
15 + 2 = 17
```

---

## Example 2

```text
Input:
cost = [[1, 3, 5],
        [4, 1, 1],
        [1, 5, 3]]

Output:
4
```

One optimal solution:

- 0 → 0 cost 1
- 1 → 1 cost 1
- 1 → 2 cost 1
- 2 → 0 cost 1

Total:

```text
4
```

---

## Example 3

```text
Input:
cost = [[2, 5, 1],
        [3, 4, 7],
        [8, 1, 2],
        [6, 2, 4],
        [3, 8, 8]]

Output:
10
```

---

## Constraints

```text
1 <= size1, size2 <= 12
size1 >= size2
0 <= cost[i][j] <= 100
```

---

# 1. Core Insight

The first group can have up to 12 points.
The second group can also have up to 12 points.

That is the big clue.

Because `size2 <= 12`, we can represent which points in **group 2** have already been connected using a **bitmask**.

This is the key compression.

Instead of remembering every edge chosen so far, we only need to remember:

- which row `i` in group 1 we are currently processing
- which columns in group 2 have already been connected by at least one chosen edge

That leads directly to dynamic programming.

---

# 2. Why Bitmasking Group 2 Is Natural

Suppose we process group 1 points from top to bottom.

For each point in group 1, we must connect it to **at least one** point in group 2.

While doing that, we also gradually cover points in group 2.

So after processing the first `i` rows, the state we care about is:

```text
mask = set of group2 points already connected by at least one chosen edge
```

Since `size2 <= 12`, the number of masks is at most:

```text
2^12 = 4096
```

which is small enough for DP.

---

# 3. Reformulating the Problem

Think of the process this way:

- process group 1 points one by one
- when handling point `i`, choose exactly one point `j` in group 2 to connect it to
- update the mask to include `j`

At the end:

- all group 1 points are guaranteed connected, because we explicitly connected each one
- some group 2 points may still be uncovered
- those uncovered group 2 points must then be connected somehow

How do we fix uncovered group 2 points at the end?

For each such point `j`, we connect it to the cheapest possible point in group 1.

This is valid because:

- a group 2 point only needs at least one connection
- additional connections are allowed
- these fixes are independent of each other

That observation is the heart of the standard optimal solution.

---

# 4. Precomputation Needed

For every point `j` in group 2, precompute:

```text
minToGroup1[j] = minimum cost[i][j] over all i in group1
```

This tells us the cheapest way to connect group2 point `j` later if it remains uncovered.

---

# 5. Approach 1 — Top-Down DP + Bitmask

## Idea

Define:

```text
dp(i, mask)
```

as the minimum additional cost needed after processing the first `i` points of group 1, where `mask` tells which points in group 2 are already connected.

### Transition

For the current point `i` in group 1, try connecting it to every point `j` in group 2:

```text
cost[i][j] + dp(i + 1, mask | (1 << j))
```

Take the minimum.

### Base Case

When all points in group 1 are processed:

```text
i == size1
```

we must pay to connect any still-uncovered point in group 2:

```text
sum of minToGroup1[j] for every j not in mask
```

---

## Java Code

```java
import java.util.*;

class Solution {
    private int m, n;
    private int[][] grid;
    private int[] minToGroup1;
    private int[][] memo;

    public int connectTwoGroups(List<List<Integer>> cost) {
        m = cost.size();
        n = cost.get(0).size();
        grid = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = cost.get(i).get(j);
            }
        }

        minToGroup1 = new int[n];
        Arrays.fill(minToGroup1, Integer.MAX_VALUE);

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                minToGroup1[j] = Math.min(minToGroup1[j], grid[i][j]);
            }
        }

        memo = new int[m][1 << n];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        return dfs(0, 0);
    }

    private int dfs(int i, int mask) {
        if (i == m) {
            int extra = 0;
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    extra += minToGroup1[j];
                }
            }
            return extra;
        }

        if (memo[i][mask] != -1) {
            return memo[i][mask];
        }

        int ans = Integer.MAX_VALUE / 2;

        for (int j = 0; j < n; j++) {
            ans = Math.min(ans, grid[i][j] + dfs(i + 1, mask | (1 << j)));
        }

        memo[i][mask] = ans;
        return ans;
    }
}
```

---

## Complexity

Let:

- `m = size1`
- `n = size2`

State count:

```text
m * 2^n
```

Each state tries all `n` choices.

So time complexity is:

```text
O(m * n * 2^n)
```

Space complexity:

```text
O(m * 2^n)
```

This is excellent for `n <= 12`.

---

# 6. Why the Base Case Is Correct

A common question is:

> Why can we just add `minToGroup1[j]` for uncovered `j` independently?

Because by the time we reach the base case:

- every point in group 1 has already been connected at least once
- the only remaining requirement is to ensure every point in group 2 is connected at least once

If a point `j` in group 2 is uncovered, we are free to connect it to **any** point in group 1.
There is no restriction preventing reuse of points or adding extra edges.

So the cheapest possible way to satisfy `j` is simply:

```text
min_i cost[i][j]
```

And since uncovered group2 points do not interfere with each other, the total completion cost is the sum of these independent minima.

---

# 7. Approach 2 — Bottom-Up DP + Bitmask

## Idea

We can convert the same recurrence into iterative DP.

Let:

```text
dp[i][mask]
```

be the minimum cost after processing the first `i` points of group 1 and having covered the group 2 points described by `mask`.

Initialize:

```text
dp[0][0] = 0
```

Then transition row by row.

At the end, for each mask, add the cost of covering remaining group2 points.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int connectTwoGroups(List<List<Integer>> cost) {
        int m = cost.size();
        int n = cost.get(0).size();
        int full = 1 << n;
        int INF = 1_000_000_000;

        int[][] dp = new int[m + 1][full];
        for (int i = 0; i <= m; i++) {
            Arrays.fill(dp[i], INF);
        }
        dp[0][0] = 0;

        int[] minToGroup1 = new int[n];
        Arrays.fill(minToGroup1, INF);

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                minToGroup1[j] = Math.min(minToGroup1[j], cost.get(i).get(j));
            }
        }

        for (int i = 0; i < m; i++) {
            for (int mask = 0; mask < full; mask++) {
                if (dp[i][mask] == INF) continue;

                for (int j = 0; j < n; j++) {
                    int nextMask = mask | (1 << j);
                    dp[i + 1][nextMask] = Math.min(
                        dp[i + 1][nextMask],
                        dp[i][mask] + cost.get(i).get(j)
                    );
                }
            }
        }

        int ans = INF;

        for (int mask = 0; mask < full; mask++) {
            int extra = 0;
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    extra += minToGroup1[j];
                }
            }
            ans = Math.min(ans, dp[m][mask] + extra);
        }

        return ans;
    }
}
```

---

## Complexity

Exactly the same asymptotic complexity:

```text
O(m * n * 2^n)
```

Space:

```text
O(m * 2^n)
```

This can even be reduced to `O(2^n)` with rolling arrays.

---

# 8. Approach 3 — Bottom-Up DP with 1D Compression

## Idea

Since row `i+1` only depends on row `i`, we can compress the DP table.

Let:

```text
dp[mask]
```

represent the minimum cost after processing some prefix of group 1.

For each row, build a new array `next`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int connectTwoGroups(List<List<Integer>> cost) {
        int m = cost.size();
        int n = cost.get(0).size();
        int full = 1 << n;
        int INF = 1_000_000_000;

        int[] minToGroup1 = new int[n];
        Arrays.fill(minToGroup1, INF);

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                minToGroup1[j] = Math.min(minToGroup1[j], cost.get(i).get(j));
            }
        }

        int[] dp = new int[full];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int i = 0; i < m; i++) {
            int[] next = new int[full];
            Arrays.fill(next, INF);

            for (int mask = 0; mask < full; mask++) {
                if (dp[mask] == INF) continue;

                for (int j = 0; j < n; j++) {
                    int nextMask = mask | (1 << j);
                    next[nextMask] = Math.min(next[nextMask], dp[mask] + cost.get(i).get(j));
                }
            }

            dp = next;
        }

        int ans = INF;
        for (int mask = 0; mask < full; mask++) {
            int extra = 0;
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    extra += minToGroup1[j];
                }
            }
            ans = Math.min(ans, dp[mask] + extra);
        }

        return ans;
    }
}
```

---

## Complexity

Time:

```text
O(m * n * 2^n)
```

Space:

```text
O(2^n)
```

This is the most space-efficient standard version.

---

# 9. Approach 4 — Equivalent State Compression DP from the Other Direction

There is another way to think about the same DP:

Instead of saying:

> each row in group 1 must choose one column in group 2

you can say:

> process rows one by one, and track which columns are covered so far

This is mathematically identical to the earlier formulations.
So there is not really a fundamentally different asymptotically better approach under these constraints.
The problem is essentially a classic state-compression DP.

Still, there is a practical variant:

- initialize DP on masks using the first row
- fold the remaining rows one by one

This is just a stylistic reorganization of Approach 3.

---

# 10. Why Greedy Fails

A tempting greedy strategy is:

> For each point in group 1, connect it to the cheapest point in group 2.

That fails because it may leave some points in group 2 uncovered, and the later forced repairs may be expensive.

Example:

```text
cost =
[
  [1, 100],
  [1, 100],
  [100, 2]
]
```

Greedy by row:

- row 0 → col 0, cost 1
- row 1 → col 0, cost 1
- row 2 → col 1, cost 2

This happens to work, but if the last row were slightly different, greedy could leave one column uncovered and then you'd be forced to add an expensive edge.

The real issue is:

> the cost of a row's choice depends on future column coverage

So local cheapest choices are not safe.

That is why DP over the coverage mask is necessary.

---

# 11. Small Worked Example

Take:

```text
cost = [[15, 96],
        [36,  2]]
```

Here:

- `m = 2`
- `n = 2`

Column minimums:

- `minToGroup1[0] = min(15, 36) = 15`
- `minToGroup1[1] = min(96, 2) = 2`

Now process row 0:

From `mask = 00`

- connect row 0 to col 0 → cost 15, new mask `01`
- connect row 0 to col 1 → cost 96, new mask `10`

Process row 1:

From `01`

- to col 0 → total 51, mask `01`
- to col 1 → total 17, mask `11`

From `10`

- to col 0 → total 132, mask `11`
- to col 1 → total 98, mask `10`

At the end:

- `mask 11` already covers both columns
- best is 17

Correct answer.

---

# 12. Why Each Group1 Point Needs Only One Explicit Choice in DP

Some users wonder:

> what if a group1 point should connect to multiple group2 points in the optimal answer?

That can happen in the final optimal graph, yes.

But our DP only forces each group1 point to choose one connection during the main recursion.

Why is that enough?

Because any extra connections needed only serve one purpose:

- covering any still-uncovered group2 points

And those extra repairs can be added at the end independently using `minToGroup1[j]`.

So the DP does not miss any optimal solution.

In effect:

- one mandatory edge per group1 point during recursion
- extra optional repair edges for uncovered group2 points at the base case

This exactly captures all valid optimal graphs.

---

# 13. Correctness Sketch

We prove the top-down DP idea.

Let `dp(i, mask)` be the minimum extra cost to connect:

- all points in group1 from index `i` onward
- while `mask` tells which points in group2 are already covered

For row `i`, in any valid solution, point `i` must connect to at least one point `j` in group2.
If we choose that first connection to be `j`, the remaining problem is exactly:

```text
dp(i + 1, mask | (1 << j))
```

plus the direct cost `cost[i][j]`.

So taking the minimum over all `j` is correct.

When `i == m`, all group1 points are already connected.
The only remaining task is to connect uncovered points in group2.
Each such point `j` can be connected independently at minimum cost `minToGroup1[j]`.
Summing these is optimal.

Therefore the recurrence is correct, and memoization yields the optimal answer.

---

# 14. Comparison of Approaches

| Approach              |                Style |             Time |        Space | Notes                |
| --------------------- | -------------------: | ---------------: | -----------: | -------------------- |
| Top-down DP + bitmask |            recursive | `O(m * n * 2^n)` | `O(m * 2^n)` | cleanest to derive   |
| Bottom-up DP 2D       |            iterative | `O(m * n * 2^n)` | `O(m * 2^n)` | easy to reason about |
| Bottom-up DP 1D       | iterative compressed | `O(m * n * 2^n)` |     `O(2^n)` | best space usage     |

---

# 15. Practical Recommendation

For interviews, the best version is usually the **top-down DP + bitmask** solution because:

- the state is intuitive
- the base case is elegant
- the proof is clean

For production-style cleanliness or if recursion is undesirable, use the **1D compressed bottom-up DP**.

---

# 16. Final Interview Summary

This problem is a classic **state compression DP** problem.

The key observations are:

1. `size2 <= 12`, so we should bitmask the second group
2. while processing each point in group1, connect it to one point in group2
3. the mask tracks which points in group2 are already covered
4. uncovered points in group2 can be fixed independently at the end using their cheapest incoming edge

That gives a DP state:

```text
dp(i, mask)
```

with transition:

```text
dp(i, mask) = min over j of cost[i][j] + dp(i+1, mask | (1<<j))
```

and base case:

```text
sum(minToGroup1[j]) over all uncovered j
```

The final complexity is:

```text
O(size1 * size2 * 2^size2)
```

which is efficient for the constraints.

---

# 17. Recommended Java Solution

```java
import java.util.*;

class Solution {
    private int m, n;
    private int[][] grid;
    private int[] minToGroup1;
    private int[][] memo;

    public int connectTwoGroups(List<List<Integer>> cost) {
        m = cost.size();
        n = cost.get(0).size();
        grid = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = cost.get(i).get(j);
            }
        }

        minToGroup1 = new int[n];
        Arrays.fill(minToGroup1, Integer.MAX_VALUE);

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                minToGroup1[j] = Math.min(minToGroup1[j], grid[i][j]);
            }
        }

        memo = new int[m][1 << n];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        return dfs(0, 0);
    }

    private int dfs(int i, int mask) {
        if (i == m) {
            int extra = 0;
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    extra += minToGroup1[j];
                }
            }
            return extra;
        }

        if (memo[i][mask] != -1) {
            return memo[i][mask];
        }

        int ans = Integer.MAX_VALUE / 2;

        for (int j = 0; j < n; j++) {
            ans = Math.min(ans, grid[i][j] + dfs(i + 1, mask | (1 << j)));
        }

        memo[i][mask] = ans;
        return ans;
    }
}
```
