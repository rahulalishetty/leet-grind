# 120. Triangle — Exhaustive Approach Summary (Java)

## Problem restatement

You are given a triangle of integers:

```text
[
     [2],
    [3, 4],
   [6, 5, 7],
  [4, 1, 8, 3]
]
```

Starting at the top element, at each step you may move to one of the two **adjacent** elements in the next row:

- from index `j` in row `i`
- you may move to:
  - index `j` in row `i + 1`
  - index `j + 1` in row `i + 1`

You must return the **minimum path sum** from top to bottom.

---

## Core recurrence

Let `f(i, j)` be the minimum path sum starting from cell `(i, j)` and going to the bottom.

From `(i, j)`, you have only two choices:

- go down to `(i + 1, j)`
- go down-right to `(i + 1, j + 1)`

So the recurrence is:

```text
f(i, j) = triangle[i][j] + min(f(i + 1, j), f(i + 1, j + 1))
```

Base case:

```text
f(lastRow, j) = triangle[lastRow][j]
```

This recurrence drives almost every correct solution.

---

# Approach 1 — Plain recursion (top-down brute force)

## Idea

At every cell, recursively try both valid moves:

- down
- down-right

Then return the current value plus the minimum of the two recursive results.

This is the most direct translation of the problem statement.

---

## Recursive state

Define:

```text
dfs(i, j) = minimum path sum from triangle[i][j] to the bottom
```

Then:

```text
dfs(i, j) = triangle[i][j] + min(dfs(i+1, j), dfs(i+1, j+1))
```

---

## Base case

If we reach the last row, there are no more choices:

```text
dfs(lastRow, j) = triangle[lastRow][j]
```

---

## Java code

```java
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        return dfs(triangle, 0, 0);
    }

    private int dfs(List<List<Integer>> triangle, int i, int j) {
        if (i == triangle.size() - 1) {
            return triangle.get(i).get(j);
        }

        int down = dfs(triangle, i + 1, j);
        int diagonal = dfs(triangle, i + 1, j + 1);

        return triangle.get(i).get(j) + Math.min(down, diagonal);
    }
}
```

---

## Time complexity

At each row, the recursion branches into two paths.

In the worst case, this explores an exponential number of paths:

```text
O(2^n)
```

where `n` is the number of rows.

More precisely, many subproblems are recomputed repeatedly.

---

## Space complexity

Recursion depth is at most the number of rows:

```text
O(n)
```

---

## Strengths

- Very intuitive
- Best for discovering the recurrence

## Weaknesses

- Too slow for the constraint limit
- Massive repeated computation

---

# Approach 2 — Top-down DP with memoization

## Idea

The plain recursive solution recomputes the same states many times.

For example, multiple paths may reach the same cell `(i, j)`, but the minimum path from `(i, j)` downward is always the same.

So cache the answer for each `(i, j)`.

---

## State definition

```text
memo[i][j] = minimum path sum from cell (i, j) to the bottom
```

If already computed, reuse it.

---

## Java code

```java
import java.util.List;

class Solution {
    private Integer[][] memo;

    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        memo = new Integer[n][n];
        return dfs(triangle, 0, 0);
    }

    private int dfs(List<List<Integer>> triangle, int i, int j) {
        if (i == triangle.size() - 1) {
            return triangle.get(i).get(j);
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int down = dfs(triangle, i + 1, j);
        int diagonal = dfs(triangle, i + 1, j + 1);

        memo[i][j] = triangle.get(i).get(j) + Math.min(down, diagonal);
        return memo[i][j];
    }
}
```

---

## Time complexity

There are only as many states as there are cells in the triangle.

A triangle with `n` rows contains:

```text
1 + 2 + 3 + ... + n = O(n^2)
```

Each state is computed once, so total time is:

```text
O(n^2)
```

---

## Space complexity

- Memo table: `O(n^2)`
- Recursion stack: `O(n)`

So overall auxiliary space is:

```text
O(n^2)
```

---

## Why this is a big improvement

This removes repeated work entirely.

The recursive structure remains easy to explain, but performance becomes efficient enough.

---

# Approach 3 — Bottom-up DP with a 2D table

## Idea

Instead of solving from the top recursively, solve from the bottom upward.

The last row already knows its own answer:

```text
dp[lastRow][j] = triangle[lastRow][j]
```

Then for every cell above it:

```text
dp[i][j] = triangle[i][j] + min(dp[i+1][j], dp[i+1][j+1])
```

So we fill the table from bottom to top.

---

## State definition

```text
dp[i][j] = minimum path sum from cell (i, j) to the bottom
```

---

## Transition

For every non-last-row cell:

```text
dp[i][j] = triangle[i][j] + Math.min(dp[i + 1][j], dp[i + 1][j + 1])
```

---

## Java code

```java
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[][] dp = new int[n][n];

        for (int j = 0; j < n; j++) {
            dp[n - 1][j] = triangle.get(n - 1).get(j);
        }

        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                dp[i][j] = triangle.get(i).get(j) +
                           Math.min(dp[i + 1][j], dp[i + 1][j + 1]);
            }
        }

        return dp[0][0];
    }
}
```

---

## Time complexity

We process every cell once:

```text
O(n^2)
```

---

## Space complexity

The DP table stores all triangle states:

```text
O(n^2)
```

---

## Why bottom-up is attractive

- No recursion
- Very direct recurrence
- Often easier to reason about once the state is defined

---

# Approach 4 — Bottom-up DP with O(n) extra space

This is the standard optimized solution.

## Key observation

To compute row `i`, you only need row `i + 1`.

So there is no need to store the entire 2D table.

You can use a 1D array that represents the best answers for the row below.

---

## State meaning

Let:

```text
dp[j] = minimum path sum from the current processing row's child position j downward
```

Start by initializing `dp` with the last row.

Then move upward.

For each cell `(i, j)`:

```text
dp[j] = triangle[i][j] + min(dp[j], dp[j + 1])
```

Interpretation:

- old `dp[j]` = best path from below-left child
- old `dp[j+1]` = best path from below-right child

After update, `dp[j]` becomes best path from current cell.

---

## Java code

```java
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] dp = new int[n];

        for (int j = 0; j < n; j++) {
            dp[j] = triangle.get(n - 1).get(j);
        }

        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                dp[j] = triangle.get(i).get(j) + Math.min(dp[j], dp[j + 1]);
            }
        }

        return dp[0];
    }
}
```

---

## Time complexity

We still process every cell once:

```text
O(n^2)
```

---

## Space complexity

Only one array of size equal to number of rows is used:

```text
O(n)
```

This is the best standard DP optimization.

---

## Why this works

Because the value at `(i, j)` depends only on two values from the row below:

- `(i+1, j)`
- `(i+1, j+1)`

A full matrix is unnecessary.

---

# Approach 5 — In-place bottom-up modification of the input triangle

## Idea

If modifying the input is allowed, we can store the DP values directly inside the triangle.

Starting from the second-last row, update each cell:

```text
triangle[i][j] += min(triangle[i+1][j], triangle[i+1][j+1])
```

After finishing, `triangle[0][0]` contains the answer.

---

## Java code

```java
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();

        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                int bestBelow = Math.min(triangle.get(i + 1).get(j),
                                         triangle.get(i + 1).get(j + 1));
                triangle.get(i).set(j, triangle.get(i).get(j) + bestBelow);
            }
        }

        return triangle.get(0).get(0);
    }
}
```

---

## Time complexity

```text
O(n^2)
```

---

## Space complexity

Ignoring the input itself, auxiliary space is:

```text
O(1)
```

---

## Important caveat

This mutates the input triangle.

That is often acceptable in some interview settings if explicitly allowed, but many interviewers prefer not modifying the input unless stated.

So this is a useful optimization, but not always the safest final answer.

---

# Approach 6 — Top-down DP from the top (minimum cost to reach each cell)

This is another valid DP direction.

## Idea

Instead of asking:

> What is the minimum path from this cell to the bottom?

ask:

> What is the minimum path sum to reach this cell from the top?

This is also valid because every cell can be reached from at most two parents.

---

## State definition

```text
dp[i][j] = minimum path sum from top to cell (i, j)
```

---

## Transition

Each cell `(i, j)` has possible parents:

- `(i - 1, j - 1)` if `j > 0`
- `(i - 1, j)` if `j < i`

So:

```text
dp[i][j] = triangle[i][j] + min(parent candidates)
```

Special cases:

- left edge `j == 0` can come only from `(i - 1, 0)`
- right edge `j == i` can come only from `(i - 1, i - 1)`

Final answer is the minimum value in the last row.

---

## Java code

```java
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[][] dp = new int[n][n];

        dp[0][0] = triangle.get(0).get(0);

        for (int i = 1; i < n; i++) {
            dp[i][0] = dp[i - 1][0] + triangle.get(i).get(0);
            dp[i][i] = dp[i - 1][i - 1] + triangle.get(i).get(i);

            for (int j = 1; j < i; j++) {
                dp[i][j] = triangle.get(i).get(j) +
                           Math.min(dp[i - 1][j - 1], dp[i - 1][j]);
            }
        }

        int ans = dp[n - 1][0];
        for (int j = 1; j < n; j++) {
            ans = Math.min(ans, dp[n - 1][j]);
        }

        return ans;
    }
}
```

---

## Time complexity

```text
O(n^2)
```

---

## Space complexity

```text
O(n^2)
```

---

## Is this as good as bottom-up-from-bottom?

Yes, asymptotically it is equally good.

However, the bottom-up-from-bottom version is slightly cleaner because the answer ends up directly at `dp[0][0]`, whereas the top-down-from-top version requires taking the minimum over the last row.

---

# Approach 7 — Top-down DP from the top with O(n) space

This compresses the top-down DP row by row.

## Idea

For each row, build the minimum path sums using only the previous row.

This requires careful handling because each cell depends on:

- previous row same index
- previous row previous index

So we usually use a fresh `next` array for each row.

---

## Java code

```java
import java.util.Arrays;
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);

        dp[0] = triangle.get(0).get(0);

        for (int i = 1; i < n; i++) {
            int[] next = new int[n];
            Arrays.fill(next, Integer.MAX_VALUE);

            for (int j = 0; j <= i; j++) {
                int val = triangle.get(i).get(j);

                if (j < i) {
                    next[j] = Math.min(next[j], dp[j] + val);
                }

                if (j > 0) {
                    next[j] = Math.min(next[j], dp[j - 1] + val);
                }
            }

            dp = next;
        }

        int ans = dp[0];
        for (int j = 1; j < n; j++) {
            ans = Math.min(ans, dp[j]);
        }

        return ans;
    }
}
```

---

## Time complexity

```text
O(n^2)
```

---

## Space complexity

```text
O(n)
```

---

## Why this is less commonly chosen

It works well, but the bottom-up `dp[j] = triangle[i][j] + min(dp[j], dp[j+1])` solution is more compact and elegant.

Still, this approach is useful because it reinforces that DP direction is a design choice.

---

# Detailed dry run of the O(n) bottom-up solution

Input:

```text
[
     [2],
    [3, 4],
   [6, 5, 7],
  [4, 1, 8, 3]
]
```

## Step 1 — Initialize with last row

```text
dp = [4, 1, 8, 3]
```

This means:

- best path from last-row cell 0 is `4`
- from cell 1 is `1`
- from cell 2 is `8`
- from cell 3 is `3`

---

## Step 2 — Process row `[6, 5, 7]`

For each position:

- `dp[0] = 6 + min(4, 1) = 7`
- `dp[1] = 5 + min(1, 8) = 6`
- `dp[2] = 7 + min(8, 3) = 10`

Now:

```text
dp = [7, 6, 10, 3]
```

Only first three entries matter now.

---

## Step 3 — Process row `[3, 4]`

- `dp[0] = 3 + min(7, 6) = 9`
- `dp[1] = 4 + min(6, 10) = 10`

Now:

```text
dp = [9, 10, 10, 3]
```

Only first two entries matter.

---

## Step 4 — Process row `[2]`

- `dp[0] = 2 + min(9, 10) = 11`

Now:

```text
dp = [11, 10, 10, 3]
```

Answer:

```text
11
```

---

# Edge cases

## 1. Single element triangle

Input:

```text
[[-10]]
```

There is only one path, so answer is:

```text
-10
```

---

## 2. Negative values

Negative values do not change the recurrence.

You still choose the minimum path.

That is why greedy local thinking is dangerous; the best global path may involve a locally larger choice if it enables smaller values later.

---

## 3. Large triangle

Since `triangle.length <= 200`, an `O(n^2)` solution is entirely appropriate.

Brute force recursion is not.

---

# Common mistakes

## Mistake 1 — Using greedy choice

A greedy idea like:

> at each row, choose the smaller of the two adjacent children

does **not** always work.

Why?

Because the locally smaller child may lead to a much worse path later.

This is a classic DP problem precisely because future consequences matter.

---

## Mistake 2 — Wrong loop bounds

In row `i`, valid column indices are:

```text
0 to i
```

not `0 to n-1`.

That detail matters a lot for triangular arrays.

---

## Mistake 3 — Confusing top-down recursion with top-down DP table direction

“Top-down” can mean recursion with memoization, but it can also informally mean filling from the top row downward.

These are different things. Be precise in interviews.

---

## Mistake 4 — Forgetting that bottom-up 1D DP overwrites values safely

In the bottom-up optimized solution:

```java
dp[j] = triangle[i][j] + Math.min(dp[j], dp[j + 1]);
```

this is safe because both `dp[j]` and `dp[j+1]` still refer to the row below at the moment of update.

That is a subtle but crucial invariant.

---

## Mistake 5 — Mutating input without mentioning it

If you use the in-place solution, explicitly state that it modifies the input triangle.

That shows care and avoids surprise.

---

# Comparing all approaches

| Approach                | Idea                               |     Time |      Space | Notes                               |
| ----------------------- | ---------------------------------- | -------: | ---------: | ----------------------------------- |
| Plain recursion         | Try both paths from every cell     | `O(2^n)` |     `O(n)` | Too slow, but useful for recurrence |
| Memoized recursion      | Cache `(i, j)` results             | `O(n^2)` |   `O(n^2)` | Very natural                        |
| Bottom-up 2D DP         | Build answers from last row upward | `O(n^2)` |   `O(n^2)` | Iterative and clear                 |
| Bottom-up 1D DP         | Keep only one row of DP            | `O(n^2)` |     `O(n)` | Best standard answer                |
| In-place bottom-up      | Reuse input triangle as DP table   | `O(n^2)` | `O(1)` aux | Best space if mutation allowed      |
| Top-down-from-top 2D DP | Minimum cost to reach each cell    | `O(n^2)` |   `O(n^2)` | Equally valid                       |
| Top-down-from-top 1D DP | Compress previous-row DP           | `O(n^2)` |     `O(n)` | Good alternative formulation        |

Here `n` is the number of rows.

---

# Which solution should you give in interviews?

A strong answer flow is:

1. State the recurrence
2. Show plain recursion
3. Add memoization
4. Convert to bottom-up
5. Optimize to `O(n)` space

That demonstrates the full DP thought process.

If the interviewer wants the final best practical solution, use:

- **Bottom-up 1D DP** if input should not be modified
- **In-place bottom-up** if mutation is allowed and you want minimal auxiliary space

---

# Recommended final interview solution

This is the cleanest practical solution:

```java
import java.util.List;

class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] dp = new int[n];

        for (int j = 0; j < n; j++) {
            dp[j] = triangle.get(n - 1).get(j);
        }

        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                dp[j] = triangle.get(i).get(j) + Math.min(dp[j], dp[j + 1]);
            }
        }

        return dp[0];
    }
}
```

### Time complexity

```text
O(n^2)
```

### Space complexity

```text
O(n)
```

---

# Compact interview explanation

You can explain it like this:

> Let `dp[j]` represent the minimum path sum from the cell in the row below at index `j` down to the bottom.
> Initialize `dp` with the last row.
> Then move upward, and for each cell `(i, j)`, update:
>
> `dp[j] = triangle[i][j] + min(dp[j], dp[j+1])`
>
> because from `(i, j)` we can only move to the two adjacent cells below.
> After processing all rows, `dp[0]` is the minimum path sum from the top.

That is short, correct, and easy to justify.

---

# Final takeaway

This problem is a classic **grid-like DP on a triangular structure**.

The central insight is:

- each cell depends only on the two adjacent cells below
- that makes bottom-up DP especially natural
- because only the next row is needed, space can be reduced to `O(n)`

That is the pattern worth remembering.

---

# Practice extension

After this, the next useful related problems are:

- Minimum Path Sum
- Unique Paths
- Unique Paths II
- Dungeon Game

These all reinforce the same skill:

> define a state over positions, then express it in terms of a small number of neighboring states.
