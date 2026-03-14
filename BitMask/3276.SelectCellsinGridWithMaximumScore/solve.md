# 3276. Select Cells in Grid With Maximum Score

## Problem Restatement

You are given a matrix `grid` of positive integers.

You may select **one or more cells** such that:

- no two selected cells come from the same row
- all selected values are distinct

Your score is the **sum of the selected values**.

Return the maximum score possible.

---

## Important Constraints

```text
1 <= number of rows <= 10
1 <= number of columns <= 10
1 <= grid[i][j] <= 100
```

These constraints are small in two very useful ways:

- at most `10` rows
- values are at most `100`

That strongly suggests:

- bitmask DP over rows
- DP over values from `1..100`
- backtracking with memoization

---

# Core Insight

The row constraint says:

> at most one selected cell per row

The uniqueness constraint says:

> if we pick value `x`, we cannot pick value `x` again from any other row

This means the problem is not really about **cells** independently.
It is more naturally about **values** and which rows can provide them.

---

## Reframing the problem

For each value `v`, determine which rows contain `v`.

Then, when considering value `v`, we have two choices:

1. skip `v`
2. assign `v` to exactly one row that contains it and has not been used yet

This is a classic DP over:

- current value
- set of already used rows

Because there are at most `10` rows, the row usage can be represented with a bitmask of size up to `2^10 = 1024`.

That is tiny.

---

# Approach 1: DP Over Values + Row Bitmask (Recommended)

## High-Level Idea

Let:

```text
rows = number of rows
mask = bitmask of used rows
```

We process values from `1` to `100`.

For each value `v`, if it appears in some rows, we can:

- ignore it
- or choose one row containing `v` if that row is still unused

Let:

```text
dp[v][mask] = maximum score obtainable considering values from v..100
              when rows in mask are already used
```

This is a top-down or bottom-up DP.

A very clean version is top-down DFS + memoization.

---

## Why this state works

Once we know:

- which value we are currently considering
- which rows are already occupied

we have all the information needed for future decisions.

We do **not** need to remember which exact values were previously selected, because we process each value at most once in increasing/decreasing order.
So uniqueness is automatically handled by the fact that each value is considered only once.

That is the key simplification.

---

## Preprocessing

Build:

```text
valueToRows[v] = list of rows that contain value v
```

If a row contains the same value multiple times, we only need that row once for this value.

That is because selecting value `v` from the same row more than once is impossible anyway, and duplicate cells with the same value in the same row do not create new useful choices.

---

## Recurrence

At state `(value, mask)`:

### Option 1: Skip current value

```text
dfs(value + 1, mask)
```

### Option 2: Take current value from one available row

For each row `r` in `valueToRows[value]`:

- if row `r` is unused in `mask`
- gain = `value + dfs(value + 1, mask | (1 << r))`

Take the maximum.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] valueToRows;
    private int[][] memo;
    private int rows;

    public int maxScore(List<List<Integer>> grid) {
        rows = grid.size();

        valueToRows = new ArrayList[101];
        for (int v = 1; v <= 100; v++) {
            valueToRows[v] = new ArrayList<>();
        }

        // Build mapping from value -> rows that contain that value
        for (int r = 0; r < rows; r++) {
            Set<Integer> seen = new HashSet<>(grid.get(r));
            for (int val : seen) {
                valueToRows[val].add(r);
            }
        }

        memo = new int[101][1 << rows];
        for (int i = 0; i <= 100; i++) {
            Arrays.fill(memo[i], -1);
        }

        return dfs(1, 0);
    }

    private int dfs(int value, int mask) {
        if (value > 100) return 0;
        if (memo[value][mask] != -1) return memo[value][mask];

        int ans = dfs(value + 1, mask); // skip this value

        for (int row : valueToRows[value]) {
            if ((mask & (1 << row)) == 0) {
                ans = Math.max(ans, value + dfs(value + 1, mask | (1 << row)));
            }
        }

        return memo[value][mask] = ans;
    }
}
```

---

## Complexity

Let:

- `R = number of rows <= 10`
- values range from `1..100`

Number of states:

```text
100 * 2^R
```

For each state, we iterate over rows containing that value. At most `R`.

So time complexity is:

```text
O(100 * 2^R * R)
```

Since `R <= 10`, this is easily fast enough.

Space complexity:

```text
O(100 * 2^R)
```

---

## Why this is the best approach

This solution directly matches the constraint structure:

- small row count -> bitmask
- small value range -> DP over values

It is compact, efficient, and elegant.

---

# Approach 2: Bottom-Up DP Over Values and Used Rows

## Idea

This is the iterative version of Approach 1.

Let:

```text
dp[mask] = best score after processing some prefix of values
```

Process values one by one from `1` to `100`.

For each value `v`, create a new DP array `next` starting from current `dp`:

- skip `v`
- or assign `v` to one unused row containing it

This avoids recursion.

---

## Transition

For each `mask`:

- `next[mask] = max(next[mask], dp[mask])` // skip
- for each row `r` containing current value `v`:
  - if `r` unused in `mask`
  - `next[mask | (1 << r)] = max(next[mask | (1 << r)], dp[mask] + v)`

After processing value `v`, replace `dp = next`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maxScore(List<List<Integer>> grid) {
        int rows = grid.size();

        List<Integer>[] valueToRows = new ArrayList[101];
        for (int v = 1; v <= 100; v++) {
            valueToRows[v] = new ArrayList<>();
        }

        for (int r = 0; r < rows; r++) {
            Set<Integer> seen = new HashSet<>(grid.get(r));
            for (int val : seen) {
                valueToRows[val].add(r);
            }
        }

        int totalMasks = 1 << rows;
        int[] dp = new int[totalMasks];

        for (int value = 1; value <= 100; value++) {
            int[] next = Arrays.copyOf(dp, totalMasks); // skip by default

            for (int mask = 0; mask < totalMasks; mask++) {
                for (int row : valueToRows[value]) {
                    if ((mask & (1 << row)) == 0) {
                        int nextMask = mask | (1 << row);
                        next[nextMask] = Math.max(next[nextMask], dp[mask] + value);
                    }
                }
            }

            dp = next;
        }

        int ans = 0;
        for (int score : dp) {
            ans = Math.max(ans, score);
        }

        return ans;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
O(100 * 2^R * R)
```

Space:

```text
O(2^R)
```

if you use rolling arrays as above.

---

## Pros

- Iterative
- Memory efficient
- No recursion overhead

## Cons

- Slightly less intuitive than top-down DFS
- Requires careful transition reasoning

---

# Approach 3: Backtracking by Rows with Used Values Set

## Idea

A natural first thought is:

- process rows one by one
- for each row, either pick one cell from that row or skip the row
- maintain a set of already used values

This is workable because rows and columns are both at most 10.

However, without memoization, this repeats many states.

---

## State

At row `i`:

- either skip row `i`
- or choose one value from row `i` not already used

We maintain:

- current row index
- used values set

Because values are up to `100`, a compact state is harder if done directly with a set object.
You could use bitsets or memo with a custom encoding, but then the value-based DP from Approaches 1 and 2 is usually cleaner.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int ans = 0;

    public int maxScore(List<List<Integer>> grid) {
        backtrack(grid, 0, new HashSet<>(), 0);
        return ans;
    }

    private void backtrack(List<List<Integer>> grid, int row, Set<Integer> used, int score) {
        if (row == grid.size()) {
            ans = Math.max(ans, score);
            return;
        }

        // Option 1: skip this row
        backtrack(grid, row + 1, used, score);

        // Option 2: choose one unique value from this row
        Set<Integer> uniqueInRow = new HashSet<>(grid.get(row));
        for (int val : uniqueInRow) {
            if (used.contains(val)) continue;

            used.add(val);
            backtrack(grid, row + 1, used, score + val);
            used.remove(val);
        }
    }
}
```

---

## Complexity

Worst case:

- each row offers up to 10 choices plus skip
- so roughly exponential in number of rows

A rough upper bound is:

```text
O(11^R)
```

with `R <= 10`.

This can still be feasible for small inputs, but it is not the cleanest intended solution.

---

## Pros

- Very intuitive
- Good starting point for deriving better DP

## Cons

- Repeats work heavily
- Does not exploit the small value range as effectively

---

# Approach 4: Backtracking + Memoization on `(valueIndex, usedRows)` via Compressed Distinct Values

## Idea

This is a refined version of Approach 1.

Instead of processing all values from `1..100`, first collect only the distinct values that actually appear in the grid.

Suppose the distinct values are:

```text
vals = [v1, v2, ..., vm]
```

Then define DP over these actual values only.

This may reduce the constant factor if the grid uses far fewer than 100 distinct values.

---

## Recurrence

Let:

```text
dfs(idx, mask)
```

be the maximum score considering values from `vals[idx...]` onward, with rows in `mask` already used.

Transitions are the same:

- skip current value
- or assign it to one unused row that contains it

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer> values;
    private Map<Integer, List<Integer>> valueToRows;
    private int[][] memo;

    public int maxScore(List<List<Integer>> grid) {
        int rows = grid.size();

        valueToRows = new HashMap<>();
        for (int r = 0; r < rows; r++) {
            Set<Integer> seen = new HashSet<>(grid.get(r));
            for (int val : seen) {
                valueToRows.computeIfAbsent(val, k -> new ArrayList<>()).add(r);
            }
        }

        values = new ArrayList<>(valueToRows.keySet());
        Collections.sort(values);

        memo = new int[values.size()][1 << rows];
        for (int i = 0; i < values.size(); i++) {
            Arrays.fill(memo[i], -1);
        }

        return dfs(0, 0, rows);
    }

    private int dfs(int idx, int mask, int rows) {
        if (idx == values.size()) return 0;
        if (memo[idx][mask] != -1) return memo[idx][mask];

        int val = values.get(idx);
        int ans = dfs(idx + 1, mask, rows); // skip

        for (int row : valueToRows.get(val)) {
            if ((mask & (1 << row)) == 0) {
                ans = Math.max(ans, val + dfs(idx + 1, mask | (1 << row), rows));
            }
        }

        return memo[idx][mask] = ans;
    }
}
```

---

## Complexity

Let `D` be the number of distinct values in the grid.

Then time complexity becomes:

```text
O(D * 2^R * R)
```

where `D <= 100`.

Space:

```text
O(D * 2^R)
```

---

## Pros

- Same idea as the main DP, but slightly tighter
- Avoids processing unused values

## Cons

- Slightly more bookkeeping
- The simpler fixed-range `1..100` solution is often easier to explain

---

# Deep Intuition

## Why processing by value is better than processing by cell

At first glance, the problem looks cell-based:

- choose cells
- avoid same row
- avoid repeated values

But uniqueness is global by **value**, not by cell.

That makes value-centric thinking much more natural.

If you process cells directly, you have to keep track of which values were already chosen.
That is awkward because values go up to 100.

Instead, if you process values one at a time:

- each value is naturally used at most once
- uniqueness is automatic
- the only remaining state is which rows are already occupied

That is a huge simplification.

---

## Why duplicates inside the same row do not matter

Suppose row `r` contains:

```text
[5, 5, 5]
```

Choosing any of those cells is equivalent:

- same row
- same value
- same score contribution

So for DP purposes, row `r` either can provide value `5` or it cannot.

We do not care how many times `5` appears inside the row.

---

## Why skipping a value is necessary

A large value is tempting, but if it occupies a row that could later provide an even better combination overall, taking it may be suboptimal.

So each value must allow both:

- take
- skip

This is exactly why a DP is needed instead of greedy selection.

---

# Correctness Sketch for Approach 1

We prove the top-down DP is correct.

## Step 1: State meaning

`dfs(value, mask)` is the maximum score obtainable using only values from `value..100`, where rows in `mask` have already been used.

This is a well-defined subproblem.

## Step 2: Exhaustiveness

For each value `value`, every valid solution must do exactly one of the following:

1. not select this value at all
2. select it from exactly one row that contains it and is not yet used

Those are the only legal possibilities because selected values must be unique.

## Step 3: Recurrence

So:

```text
dfs(value, mask) =
max(
    dfs(value + 1, mask),
    value + dfs(value + 1, mask with chosen row marked)
)
```

over all valid available rows containing `value`.

This considers all legal choices for the current value.

## Step 4: Optimal substructure

Once we decide what to do with the current value, the remaining problem depends only on:

- the next value
- which rows are already used

Therefore the remainder is exactly another instance of the same subproblem.

## Step 5: Base case

When `value > 100`, no values remain, so the best additional score is `0`.

Thus the recurrence is correct, and the DP computes the optimal answer.

---

# Example Walkthrough

## Example 1

```text
grid = [[1,2,3],
        [4,3,2],
        [1,1,1]]
```

### Value to rows mapping

- `1 -> rows {0, 2}`
- `2 -> rows {0, 1}`
- `3 -> rows {0, 1}`
- `4 -> row {1}`

Now we must choose distinct values using distinct rows.

One best choice is:

- row 2 gives `1`
- row 0 gives `3`
- row 1 gives `4`

Total:

```text
1 + 3 + 4 = 8
```

That is optimal.

---

## Example 2

```text
grid = [[8,7,6],
        [8,3,2]]
```

Value mapping:

- `8 -> rows {0, 1}`
- `7 -> row {0}`
- `6 -> row {0}`
- `3 -> row {1}`
- `2 -> row {1}`

Best choice:

- row 0 gives `7`
- row 1 gives `8`

Total:

```text
15
```

That beats alternatives like `8 + 3 = 11`.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    private List<Integer>[] valueToRows;
    private int[][] memo;
    private int rows;

    public int maxScore(List<List<Integer>> grid) {
        rows = grid.size();

        valueToRows = new ArrayList[101];
        for (int v = 1; v <= 100; v++) {
            valueToRows[v] = new ArrayList<>();
        }

        for (int r = 0; r < rows; r++) {
            Set<Integer> seen = new HashSet<>(grid.get(r));
            for (int val : seen) {
                valueToRows[val].add(r);
            }
        }

        memo = new int[101][1 << rows];
        for (int i = 0; i <= 100; i++) {
            Arrays.fill(memo[i], -1);
        }

        return dfs(1, 0);
    }

    private int dfs(int value, int mask) {
        if (value > 100) return 0;
        if (memo[value][mask] != -1) return memo[value][mask];

        int ans = dfs(value + 1, mask); // skip current value

        for (int row : valueToRows[value]) {
            if ((mask & (1 << row)) == 0) {
                ans = Math.max(ans, value + dfs(value + 1, mask | (1 << row)));
            }
        }

        return memo[value][mask] = ans;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                 |    Time Complexity |        Space Complexity | Recommended |
| ---------- | ----------------------------------------- | -----------------: | ----------------------: | ----------- |
| Approach 1 | Top-down DP over values and used-row mask | `O(100 * 2^R * R)` |          `O(100 * 2^R)` | Yes         |
| Approach 2 | Bottom-up DP over values and row mask     | `O(100 * 2^R * R)` |                `O(2^R)` | Yes         |
| Approach 3 | Backtracking by rows with used values set |        Exponential | Exponential / recursion | No          |
| Approach 4 | DP over compressed distinct values        |   `O(D * 2^R * R)` |            `O(D * 2^R)` | Good        |

Here:

- `R <= 10`
- `D <= 100`

---

# Pattern Recognition Takeaway

This problem has a very recognizable structure:

- one choice per row
- global uniqueness by value
- small number of rows
- small bounded value range

That strongly suggests:

- bitmask rows
- process values as the main DP dimension

Whenever the number of rows is small but values are globally constrained, this kind of DP is often the cleanest route.

---

# Final Takeaway

The cleanest solution is:

1. map each value to the rows that contain it
2. process values from `1` to `100`
3. maintain a bitmask of already used rows
4. for each value, either skip it or assign it to one available row
5. maximize total score

That gives an efficient and elegant solution well within the constraints.
