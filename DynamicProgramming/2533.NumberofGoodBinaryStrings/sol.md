# Equal Number of 0s and 1s in a Grid Path

## Problem

You are given a `0`-indexed `m x n` binary matrix `grid`.

You start at `(0, 0)` and want to reach `(m - 1, n - 1)`.

From any cell `(row, col)`, you may move only:

- **down** to `(row + 1, col)`
- **right** to `(row, col + 1)`

You must determine whether there exists **at least one path** such that along the path, the number of `0`s visited is exactly equal to the number of `1`s visited.

Return `true` if such a path exists, otherwise return `false`.

---

## Core intuition

This problem looks like a path problem, but the actual constraint is not about shortest distance or cost.

The real condition is:

> Along some valid path, the count of `0`s must equal the count of `1`s.

That means we are not just asking whether a path exists — a path always exists if the grid dimensions are valid.
We are asking whether there is a path whose **composition** satisfies a balance condition.

So the first step is to convert that balance condition into something easier to track.

---

## Step 1: Convert the requirement into a sum condition

Suppose we assign:

- `1` cell -> `+1`
- `0` cell -> `-1`

Then along a path:

- every `1` contributes `+1`
- every `0` contributes `-1`

If a path has equal numbers of `0`s and `1`s, then the total sum along that path becomes:

```text
(# of 1s) - (# of 0s) = 0
```

So the problem becomes:

> Is there any valid path from start to end whose transformed sum is exactly `0`?

That reframing is the central insight.

---

## Step 2: Observe the path length

Any path from `(0,0)` to `(m-1,n-1)` must make:

- exactly `m - 1` downward moves
- exactly `n - 1` rightward moves

So the total number of visited cells on any path is always:

```text
(m - 1) + (n - 1) + 1 = m + n - 1
```

That means **every possible path has the same length**.

Now think carefully:

For a path to have equal numbers of `0`s and `1`s, the total number of visited cells must be even.

Why?

Because if the number of `0`s equals the number of `1`s, then:

```text
total cells = count(0) + count(1) = 2 * count(0)
```

which must be divisible by `2`.

So if:

```text
m + n - 1
```

is odd, the answer is immediately `false`.

This is a very important early pruning step.

---

## Step 3: Why brute force is not feasible

A path consists of `m - 1` downs and `n - 1` rights in some order.

The number of such paths is:

```text
C(m+n-2, m-1)
```

which grows combinatorially.

So checking each path explicitly is not practical.

We need dynamic programming.

---

## Step 4: DP state idea

At any cell `(i, j)`, many different paths can reach that cell.

Those paths may produce different transformed sums.

So for each cell, we want to know:

> What sums are possible when arriving at this cell?

This leads to the DP definition:

```text
dp[i][j] = set of all possible transformed sums reachable at cell (i, j)
```

Where transformed sum means:

- add `+1` for a `1`
- add `-1` for a `0`

---

## Step 5: State transition

To reach `(i, j)`, you can only come from:

- `(i - 1, j)` from above
- `(i, j - 1)` from the left

Let the current cell value be:

- `+1` if `grid[i][j] == 1`
- `-1` if `grid[i][j] == 0`

Then:

- for every sum `s` reachable at `(i - 1, j)`, sum `s + val` is reachable at `(i, j)`
- for every sum `s` reachable at `(i, j - 1)`, sum `s + val` is reachable at `(i, j)`

So we merge reachable sums from top and left.

---

## Step 6: Base case

At the starting cell `(0,0)`:

- there is exactly one possible sum
- it is just the transformed value of that starting cell

So:

- if `grid[0][0] == 1`, start sum is `+1`
- if `grid[0][0] == 0`, start sum is `-1`

---

## Step 7: Final condition

At the destination `(m-1,n-1)`, if sum `0` is one of the reachable sums, then:

- there exists a valid path whose number of `0`s equals number of `1`s

Otherwise no such path exists.

---

## Why the set-based DP works

This is worth making fully explicit.

At each cell, the set stores **every possible balance** achievable there.

Because every path to a cell must come from either top or left, and because we extend all previously reachable sums by the current cell's contribution, the DP captures all valid path possibilities without enumerating paths individually.

So this is a classic:

- local transition
- merge of possibilities
- exact-state DP

The DP is correct because it preserves all reachable sums at every step.

---

## Important subtle point: why sum range stays manageable

The length of any path to cell `(i, j)` is:

```text
i + j + 1
```

So the transformed sum at that cell must lie between:

```text
-(i + j + 1)  and  +(i + j + 1)
```

That means each cell cannot have arbitrarily many possible sums.

In fact, the number of distinct sums per cell is bounded by `O(m + n)`.

That makes the DP feasible.

---

## Java code

```java
import java.util.*;

class Solution {
    public boolean isThereAPath(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int len = m + n - 1;

        // Equal number of 0s and 1s requires even path length
        if ((len & 1) == 1) return false;

        Set<Integer>[][] dp = new HashSet[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = new HashSet<>();
                int val = (grid[i][j] == 1) ? 1 : -1;

                if (i == 0 && j == 0) {
                    dp[i][j].add(val);
                    continue;
                }

                if (i > 0) {
                    for (int s : dp[i - 1][j]) {
                        dp[i][j].add(s + val);
                    }
                }

                if (j > 0) {
                    for (int s : dp[i][j - 1]) {
                        dp[i][j].add(s + val);
                    }
                }
            }
        }

        return dp[m - 1][n - 1].contains(0);
    }
}
```

---

## Line-by-line explanation of the code

### 1. Read dimensions and compute total path length

```java
int m = grid.length, n = grid[0].length;
int len = m + n - 1;
```

- `m` = number of rows
- `n` = number of columns
- `len` = total number of cells visited in any valid path

---

### 2. Early parity pruning

```java
if ((len & 1) == 1) return false;
```

If path length is odd, equal number of `0`s and `1`s is impossible.

This check avoids all DP work in impossible cases.

---

### 3. DP structure

```java
Set<Integer>[][] dp = new HashSet[m][n];
```

Each `dp[i][j]` stores the set of reachable transformed sums at cell `(i, j)`.

---

### 4. Fill DP cell by cell

```java
for (int i = 0; i < m; i++) {
    for (int j = 0; j < n; j++) {
```

We iterate row by row.
This works because each state depends only on top and left, which are already computed.

---

### 5. Initialize current set and transformed value

```java
dp[i][j] = new HashSet<>();
int val = (grid[i][j] == 1) ? 1 : -1;
```

- if current cell is `1`, contribution is `+1`
- if current cell is `0`, contribution is `-1`

---

### 6. Base case for start cell

```java
if (i == 0 && j == 0) {
    dp[i][j].add(val);
    continue;
}
```

At the start there is exactly one reachable sum: the value of the first cell.

---

### 7. Transition from top

```java
if (i > 0) {
    for (int s : dp[i - 1][j]) {
        dp[i][j].add(s + val);
    }
}
```

Every reachable sum from the top cell can be extended by current cell’s contribution.

---

### 8. Transition from left

```java
if (j > 0) {
    for (int s : dp[i][j - 1]) {
        dp[i][j].add(s + val);
    }
}
```

Same logic for paths coming from the left.

---

### 9. Final answer

```java
return dp[m - 1][n - 1].contains(0);
```

If `0` is reachable at destination, then some path has equal `0`s and `1`s.

---

## Worked example

Consider:

```text
grid =
[
  [0, 1],
  [1, 0]
]
```

Transform values:

```text
[
  [-1, +1],
  [+1, -1]
]
```

Path length:

```text
2 + 2 - 1 = 3
```

That is odd.

So immediately answer is `false`.

Indeed every path visits 3 cells, and you cannot split 3 visited cells into equal numbers of `0`s and `1`s.

---

Now consider:

```text
grid =
[
  [0, 1, 0]
]
```

Path length is:

```text
1 + 3 - 1 = 3
```

Again odd, so impossible.

---

Consider:

```text
grid =
[
  [1, 0],
  [0, 1],
  [1, 0]
]
```

Path length:

```text
3 + 2 - 1 = 4
```

Even, so possible in principle.

Now DP checks whether some valid right/down path yields transformed sum `0`.

---

## Correctness argument

We can state correctness more formally.

### Claim

`dp[i][j]` contains exactly all transformed sums obtainable by valid paths from `(0,0)` to `(i,j)`.

### Proof sketch

We use induction on cells in row-major order.

#### Base case

At `(0,0)`, the only path consists of the single starting cell, so the only reachable sum is that cell’s transformed value.
Thus `dp[0][0]` is correct.

#### Inductive step

Assume all previously processed cells have correct sets.

Any valid path to `(i,j)` must come either:

- from `(i-1,j)` if `i > 0`
- from `(i,j-1)` if `j > 0`

By inductive hypothesis, those predecessor cells already contain exactly all reachable sums for paths ending there.

Appending cell `(i,j)` adds its contribution `val`, so every predecessor sum `s` becomes `s + val`.

Therefore the union of:

- `{s + val | s in dp[i-1][j]}`
- `{s + val | s in dp[i][j-1]}`

is exactly the set of all transformed sums for paths ending at `(i,j)`.

Thus `dp[i][j]` is correct.

#### Final step

At the destination, a path has equal numbers of `0`s and `1`s exactly when its transformed sum is `0`.
So checking whether `0` belongs to `dp[m-1][n-1]` gives the correct answer.

Hence the algorithm is correct.

---

## Complexity analysis

Let:

```text
L = m + n - 1
```

### Number of possible sums per cell

At a cell, the path length up to that point is at most `L`, so the transformed sum lies in the range `[-L, L]`.

That gives at most `2L + 1` possible sums.

So each cell stores `O(L)` sums.

---

### Time complexity

For each cell, we iterate over sums from top and left sets.

Each set has size `O(L)`.

So the total time is:

```text
O(m * n * L)
```

That is:

```text
O(m * n * (m + n))
```

---

### Space complexity

We store a set of size `O(L)` for each of the `m * n` cells.

So space complexity is:

```text
O(m * n * L)
```

or:

```text
O(m * n * (m + n))
```

---

## Practical interpretation of the complexity

This is much better than enumerating all paths, which is exponential/combinatorial.

Instead, we compress all paths that lead to the same cell and same balance into one state.

That is the reason DP is effective here.

---

## Stronger optimization insight

There is an even more refined approach.

Instead of storing **all reachable sums**, we can sometimes track only:

- minimum number of `1`s reachable at each cell
- maximum number of `1`s reachable at each cell

Why might that work?

Because the reachable counts often form a continuous interval.

Then the question becomes whether half the path length lies in that reachable interval.

This gives a more compact solution and is the standard optimized approach for this problem.

Still, the set-based DP is excellent for understanding the logic because it makes the state space explicit.

---

## Common mistakes

### 1. Forgetting the parity check

This is the easiest pruning and often immediately rules out many cases.

If total path length is odd, the answer is always `false`.

---

### 2. Counting moves instead of cells

A path contains:

```text
moves + 1
```

cells.

So the path length is:

```text
(m - 1) + (n - 1) + 1 = m + n - 1
```

not `m + n - 2`.

---

### 3. Using greedy reasoning

You cannot greedily choose cells to “balance” zeros and ones locally.

A locally promising choice may block future balance.

This is why DP is needed.

---

### 4. Confusing sum of cell values with equal counts

The original grid uses `0` and `1`, but equal counts are easier to detect after transforming:

- `0 -> -1`
- `1 -> +1`

Without that transformation, the condition is not directly a sum-zero condition.

---

## Final takeaway

The clean mental model is:

1. A balanced path means equal count of `0`s and `1`s.
2. Convert that into a zero-sum path by mapping:
   - `1 -> +1`
   - `0 -> -1`
3. Since every path has fixed length, odd path lengths are immediately impossible.
4. Use DP where each cell stores all reachable balances.
5. Check whether balance `0` is reachable at the destination.

This is a very good example of turning a counting constraint into a path-sum DP.

---

## Summary

### Main idea

Transform the grid into `+1 / -1` values and ask whether any valid path has total sum `0`.

### DP state

`dp[i][j]` = set of all transformed sums reachable at cell `(i, j)`.

### Transition

Take all sums from top and left predecessor cells, then add current cell contribution.

### Base case

`dp[0][0] = {+1 or -1}` depending on the starting cell.

### Final answer

Return whether `0` is in `dp[m-1][n-1]`.

### Complexity

- **Time:** `O(m * n * (m + n))`
- **Space:** `O(m * n * (m + n))`
