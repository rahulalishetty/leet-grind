# 1659. Maximize Grid Happiness — Exhaustive Java Notes

## Problem Statement

You are given:

- a grid of size `m x n`
- `introvertsCount` introverts
- `extrovertsCount` extroverts

You may place **some or all** of them into the grid, with at most one person per cell.

Each person contributes happiness according to these rules:

### Introvert

- base happiness = `120`
- loses `30` for each neighbor

### Extrovert

- base happiness = `40`
- gains `20` for each neighbor

Neighbors are only the 4-directional adjacent cells:

- up
- down
- left
- right

The total grid happiness is the sum of all placed people's happiness.

Return the **maximum possible grid happiness**.

---

## Example 1

```text
Input:
m = 2, n = 3, introvertsCount = 1, extrovertsCount = 2

Output:
240
```

One optimal placement:

- introvert at `(1,1)`
- extroverts at `(1,3)` and `(2,3)`

Happiness:

- introvert: `120`
- extrovert: `40 + 20 = 60`
- extrovert: `40 + 20 = 60`

Total = `240`

---

## Example 2

```text
Input:
m = 3, n = 1, introvertsCount = 2, extrovertsCount = 1

Output:
260
```

Placement:

- introvert
- extrovert
- introvert

Happiness:

- introvert: `120 - 30 = 90`
- extrovert: `40 + 20 + 20 = 80`
- introvert: `120 - 30 = 90`

Total = `260`

---

## Example 3

```text
Input:
m = 2, n = 2, introvertsCount = 4, extrovertsCount = 0

Output:
240
```

---

## Constraints

```text
1 <= m, n <= 5
0 <= introvertsCount, extrovertsCount <= min(m * n, 6)
```

The grid is small, and the total number of people is at most `6`.

That is the main clue.

---

# 1. Core Insight

This is a classic **state compression dynamic programming** problem.

At each cell, we can choose one of three states:

- empty
- introvert
- extrovert

The challenge is that a placement only interacts locally with its **left** and **up** neighbors if we process the grid row by row.

That strongly suggests:

- profile DP
- memoized DFS over grid positions
- base-3 row states
- bitmask / ternary state compression

---

# 2. Pairwise Neighbor Contribution

A very important simplification is to think in terms of **pair interactions**.

If two adjacent cells both contain people, then both persons' happiness changes.

Let us compute the net contribution of one adjacent pair.

## Introvert + Introvert

Each introvert loses `30`, so total pair effect:

```text
-30 + -30 = -60
```

## Introvert + Extrovert

- introvert loses `30`
- extrovert gains `20`

Total pair effect:

```text
-30 + 20 = -10
```

## Extrovert + Extrovert

Each extrovert gains `20`, so total:

```text
20 + 20 = 40
```

These pair contributions are extremely useful.

We can summarize them in a matrix.

Let states be:

- `0 = empty`
- `1 = introvert`
- `2 = extrovert`

Base scores:

```text
score[0] = 0
score[1] = 120
score[2] = 40
```

Pairwise neighbor effect:

```text
pair[0][*] = 0
pair[*][0] = 0
pair[1][1] = -60
pair[1][2] = -10
pair[2][1] = -10
pair[2][2] = 40
```

---

# 3. Why Local DP Works

If we process cells in row-major order:

```text
(0,0), (0,1), ..., (0,n-1), (1,0), ...
```

then when placing a person in the current cell, the only already-placed neighbors are:

- left
- up

Right and down are not placed yet and will be handled later.

So to compute the new gain from placing something in the current cell, we only need to know:

- who is to the left
- who is above

This means we do **not** need the full grid state.
We only need enough information to recover the previous row and current row prefix.

That is the key DP compression idea.

---

# 4. Approach 1 — Top-Down DP with Base-3 State Compression

## Main Idea

Track the last `n` cells using a base-3 encoded state.

Each digit stores:

- `0` empty
- `1` introvert
- `2` extrovert

For each position `pos`, we keep:

- how many introverts remain
- how many extroverts remain
- encoded state of previous relevant cells

Then recursively try:

1. leave cell empty
2. place introvert
3. place extrovert

and take the maximum.

---

## State Definition

Let:

```text
dfs(pos, introLeft, extroLeft, mask)
```

Where:

- `pos` = current cell index from `0` to `m*n - 1`
- `introLeft` = introverts still available
- `extroLeft` = extroverts still available
- `mask` = base-3 state of the last `n` cells

### Why last `n` cells?

Because from `mask` we can recover:

- the left neighbor = last digit in current row
- the up neighbor = digit corresponding to previous row same column

As we move forward, we shift the mask and append the new cell state.

---

## Helper Operations

Suppose `pow3[i] = 3^i`.

Then:

- `up = mask / pow3[n-1]` gives the top cell's state if mask is structured left-to-right appropriately
- `left = mask % 3` gives the left cell's state
- `newMask = (mask % pow3[n-1]) * 3 + newState`

This removes the oldest cell and appends the new state.

---

## Gain Calculation

If placing state `s` in current cell:

```text
gain = score[s] + pair[s][left] + pair[s][up]
```

That already fully accounts for all incremental happiness contributed now.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int m, n;
    private int[] pow3;
    private int[][] pair = {
        {0, 0, 0},
        {0, -60, -10},
        {0, -10, 40}
    };
    private int[] base = {0, 120, 40};

    private Map<Long, Integer> memo = new HashMap<>();

    public int getMaxGridHappiness(int m, int n, int introvertsCount, int extrovertsCount) {
        this.m = m;
        this.n = n;
        this.pow3 = new int[n + 1];
        pow3[0] = 1;
        for (int i = 1; i <= n; i++) {
            pow3[i] = pow3[i - 1] * 3;
        }
        return dfs(0, introvertsCount, extrovertsCount, 0);
    }

    private int dfs(int pos, int intro, int extro, int mask) {
        if (pos == m * n || (intro == 0 && extro == 0)) {
            return 0;
        }

        long key = encode(pos, intro, extro, mask);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int col = pos % n;

        int up = mask / pow3[n - 1];
        int left = (col == 0) ? 0 : (mask % 3);

        int trimmed = mask % pow3[n - 1];

        int best = dfs(pos + 1, intro, extro, trimmed * 3);

        if (intro > 0) {
            int gain = base[1] + pair[1][left] + pair[1][up];
            best = Math.max(best,
                gain + dfs(pos + 1, intro - 1, extro, trimmed * 3 + 1));
        }

        if (extro > 0) {
            int gain = base[2] + pair[2][left] + pair[2][up];
            best = Math.max(best,
                gain + dfs(pos + 1, intro, extro - 1, trimmed * 3 + 2));
        }

        memo.put(key, best);
        return best;
    }

    private long encode(int pos, int intro, int extro, int mask) {
        long key = pos;
        key = key * 7 + intro;
        key = key * 7 + extro;
        key = key * 250 + mask;
        return key;
    }
}
```

---

## Complexity

The state count is approximately:

```text
(m * n) * 7 * 7 * 3^n
```

because:

- `m*n <= 25`
- introverts `<= 6`
- extroverts `<= 6`
- `3^n <= 3^5 = 243`

So the actual state space is very manageable.

A rough bound:

```text
O(m * n * introvertsCount * extrovertsCount * 3^n)
```

with 3 transitions per state.

Space complexity is the same order for memoization.

This is excellent for the constraints.

---

# 5. Approach 2 — Row-by-Row DP with Precomputed Row States

## Main Idea

Instead of processing cell by cell, process **one row at a time**.

Each row configuration is a base-3 number of length `n`, where each digit is:

- `0` empty
- `1` introvert
- `2` extrovert

There are at most:

```text
3^n <= 243
```

possible row states.

For each row state, precompute:

1. how many introverts it uses
2. how many extroverts it uses
3. its internal row happiness
4. interaction happiness with another row state

Then do DP over rows.

---

## Precomputation

For each row state `s`:

- decode the row cells
- count introverts / extroverts in that row
- compute horizontal interactions inside the row
- compute row base happiness

For each pair of row states `(a, b)`:

- compute vertical interactions between row `a` and row `b`

Then total contribution of placing row `b` below row `a` is:

```text
rowScore[b] + vertical[a][b]
```

---

## DP Definition

Let:

```text
dp[row][introUsed][extroUsed][prevState]
```

be the best total happiness after processing rows up to `row`, with `prevState` as the previous row.

A cleaner implementation uses memoized DFS:

```text
dfs(row, introLeft, extroLeft, prevState)
```

Try every valid current row state.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int m, n;
    private int[][] pair = {
        {0, 0, 0},
        {0, -60, -10},
        {0, -10, 40}
    };
    private int[] base = {0, 120, 40};

    private List<int[]> states = new ArrayList<>();
    private int[] introCnt, extroCnt, rowScore;
    private int[][] vertical;
    private Integer[][][][] memo;

    public int getMaxGridHappiness(int m, int n, int introvertsCount, int extrovertsCount) {
        this.m = m;
        this.n = n;

        int totalStates = 1;
        for (int i = 0; i < n; i++) totalStates *= 3;

        introCnt = new int[totalStates];
        extroCnt = new int[totalStates];
        rowScore = new int[totalStates];
        vertical = new int[totalStates][totalStates];

        for (int state = 0; state < totalStates; state++) {
            int[] cells = decode(state);
            states.add(cells);

            for (int c : cells) {
                if (c == 1) introCnt[state]++;
                else if (c == 2) extroCnt[state]++;
                rowScore[state] += base[c];
            }

            for (int j = 1; j < n; j++) {
                rowScore[state] += pair[cells[j - 1]][cells[j]];
            }
        }

        for (int a = 0; a < totalStates; a++) {
            for (int b = 0; b < totalStates; b++) {
                int score = 0;
                int[] ra = states.get(a);
                int[] rb = states.get(b);
                for (int j = 0; j < n; j++) {
                    score += pair[ra[j]][rb[j]];
                }
                vertical[a][b] = score;
            }
        }

        memo = new Integer[m + 1][introvertsCount + 1][extrovertsCount + 1][totalStates];
        return dfs(0, introvertsCount, extrovertsCount, 0);
    }

    private int dfs(int row, int introLeft, int extroLeft, int prevState) {
        if (row == m) return 0;
        if (memo[row][introLeft][extroLeft][prevState] != null) {
            return memo[row][introLeft][extroLeft][prevState];
        }

        int best = 0;
        int totalStates = states.size();

        for (int cur = 0; cur < totalStates; cur++) {
            if (introCnt[cur] > introLeft || extroCnt[cur] > extroLeft) continue;

            int gain = rowScore[cur] + vertical[prevState][cur];
            best = Math.max(best, gain + dfs(
                row + 1,
                introLeft - introCnt[cur],
                extroLeft - extroCnt[cur],
                cur
            ));
        }

        memo[row][introLeft][extroLeft][prevState] = best;
        return best;
    }

    private int[] decode(int state) {
        int[] cells = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            cells[i] = state % 3;
            state /= 3;
        }
        return cells;
    }
}
```

---

## Complexity

Let:

```text
S = 3^n
```

Then:

- row state precomputation: `O(S * n)`
- vertical interaction precomputation: `O(S^2 * n)`
- DP states: `O(m * intro * extro * S)`
- each state tries all row states: `O(S)`

Total:

```text
O(S^2 * n + m * intro * extro * S^2)
```

Since:

- `n <= 5`
- `S <= 243`
- `intro, extro <= 6`

this is absolutely fine.

---

# 6. Which Approach Is Better?

Both are strong.

## Cell-by-cell ternary profile DP

Pros:

- very direct
- elegant local transition
- small state space

Cons:

- a little tricky to reason about mask shifting

## Row-by-row precomputed-state DP

Pros:

- very structured
- often easier to optimize and explain in interviews
- separates local row scoring from DP

Cons:

- more code because of preprocessing

In practice, the **row-state DP** is often the most polished editorial-style solution, while the **cell-by-cell DP** is a great compact solution.

---

# 7. Why Brute Force Placement Is Too Slow

Each of the `m*n` cells has 3 choices:

- empty
- introvert
- extrovert

So naive brute force is:

```text
3^(m*n)
```

In the worst case:

```text
3^25
```

which is astronomically large.

Even with only up to 6 introverts and 6 extroverts, a plain combinational brute force still explodes.

That is why state compression is required.

---

# 8. Worked Pair-Score Example

Suppose two adjacent cells are:

- introvert next to extrovert

Then:

- introvert base = 120
- extrovert base = 40

Neighbor interaction:

- introvert loses 30
- extrovert gains 20

Net pair interaction:

```text
-10
```

So if these cells are adjacent, the pair contributes:

```text
120 + 40 - 10 = 150
```

instead of:

```text
120 + 40 = 160
```

This pairwise accounting is why the matrix `pair[a][b]` is so convenient.

---

# 9. Important Correctness Idea

When processing a cell or row, we only add the interaction with previously placed neighbors.

Why is this enough?

Because every adjacent pair is counted exactly once:

- either when the second cell is placed in cell-by-cell DP
- or in row-state DP using horizontal score inside the row plus vertical interaction with previous row

So there is no double counting and no missing contribution.

That is the core correctness invariant.

---

# 10. Approach 3 — Bottom-Up DP over Rows

This is the iterative version of Approach 2.

You can maintain:

```text
dp[row][introUsed][extroUsed][state]
```

or compress rows using hash maps / arrays.

Transition from previous row states to current row states.

This is more verbose in Java, but conceptually straightforward.

Because the recursive memoized version is cleaner, that is usually preferred.

Still, for completeness, here is the iterative style.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[][] pair = {
        {0, 0, 0},
        {0, -60, -10},
        {0, -10, 40}
    };
    private int[] base = {0, 120, 40};

    public int getMaxGridHappiness(int m, int n, int introvertsCount, int extrovertsCount) {
        int totalStates = 1;
        for (int i = 0; i < n; i++) totalStates *= 3;

        int[][] cells = new int[totalStates][n];
        int[] introCnt = new int[totalStates];
        int[] extroCnt = new int[totalStates];
        int[] rowScore = new int[totalStates];
        int[][] vertical = new int[totalStates][totalStates];

        for (int state = 0; state < totalStates; state++) {
            int x = state;
            for (int i = n - 1; i >= 0; i--) {
                cells[state][i] = x % 3;
                x /= 3;
            }

            for (int i = 0; i < n; i++) {
                int c = cells[state][i];
                if (c == 1) introCnt[state]++;
                else if (c == 2) extroCnt[state]++;
                rowScore[state] += base[c];
                if (i > 0) rowScore[state] += pair[cells[state][i - 1]][c];
            }
        }

        for (int a = 0; a < totalStates; a++) {
            for (int b = 0; b < totalStates; b++) {
                int score = 0;
                for (int i = 0; i < n; i++) {
                    score += pair[cells[a][i]][cells[b][i]];
                }
                vertical[a][b] = score;
            }
        }

        int[][][] dp = new int[introvertsCount + 1][extrovertsCount + 1][totalStates];
        for (int i = 0; i <= introvertsCount; i++) {
            for (int e = 0; e <= extrovertsCount; e++) {
                Arrays.fill(dp[i][e], Integer.MIN_VALUE / 4);
            }
        }
        dp[0][0][0] = 0;

        for (int row = 0; row < m; row++) {
            int[][][] next = new int[introvertsCount + 1][extrovertsCount + 1][totalStates];
            for (int i = 0; i <= introvertsCount; i++) {
                for (int e = 0; e <= extrovertsCount; e++) {
                    Arrays.fill(next[i][e], Integer.MIN_VALUE / 4);
                }
            }

            for (int usedI = 0; usedI <= introvertsCount; usedI++) {
                for (int usedE = 0; usedE <= extrovertsCount; usedE++) {
                    for (int prev = 0; prev < totalStates; prev++) {
                        int curVal = dp[usedI][usedE][prev];
                        if (curVal <= Integer.MIN_VALUE / 8) continue;

                        for (int cur = 0; cur < totalStates; cur++) {
                            int ni = usedI + introCnt[cur];
                            int ne = usedE + extroCnt[cur];
                            if (ni > introvertsCount || ne > extrovertsCount) continue;

                            int gain = rowScore[cur] + vertical[prev][cur];
                            next[ni][ne][cur] = Math.max(next[ni][ne][cur], curVal + gain);
                        }
                    }
                }
            }

            dp = next;
        }

        int ans = 0;
        for (int i = 0; i <= introvertsCount; i++) {
            for (int e = 0; e <= extrovertsCount; e++) {
                for (int state = 0; state < totalStates; state++) {
                    ans = Math.max(ans, dp[i][e][state]);
                }
            }
        }

        return ans;
    }
}
```

---

## Complexity

Same essential order as the recursive row-state DP:

```text
O(m * intro * extro * S^2)
```

where:

```text
S = 3^n
```

Space is larger because we explicitly store DP layers.

---

# 11. Comparison Table

| Approach               | Core Idea                                  |                           Time |    Space | Notes                       |
| ---------------------- | ------------------------------------------ | -----------------------------: | -------: | --------------------------- |
| Top-down cell-by-cell  | ternary window over last `n` cells         |       `O(m*n*intro*extro*3^n)` |  similar | compact and elegant         |
| Top-down row-state DP  | precompute row states and row interactions | `O(S^2*n + m*intro*extro*S^2)` | moderate | most structured             |
| Bottom-up row-state DP | iterative version of row-state DP          |                        similar |   larger | useful if recursion avoided |

with `S = 3^n`.

---

# 12. Recommended Java Solution

For interviews and clarity, the **row-state top-down DP** is usually the strongest presentation because:

- it clearly separates preprocessing from decision making,
- it uses the small `n <= 5` perfectly,
- and it makes neighbor handling very clean.

Here it is again as the recommended version.

```java
import java.util.*;

class Solution {
    private int m, n;
    private int[][] pair = {
        {0, 0, 0},
        {0, -60, -10},
        {0, -10, 40}
    };
    private int[] base = {0, 120, 40};

    private List<int[]> states = new ArrayList<>();
    private int[] introCnt, extroCnt, rowScore;
    private int[][] vertical;
    private Integer[][][][] memo;

    public int getMaxGridHappiness(int m, int n, int introvertsCount, int extrovertsCount) {
        this.m = m;
        this.n = n;

        int totalStates = 1;
        for (int i = 0; i < n; i++) totalStates *= 3;

        introCnt = new int[totalStates];
        extroCnt = new int[totalStates];
        rowScore = new int[totalStates];
        vertical = new int[totalStates][totalStates];

        for (int state = 0; state < totalStates; state++) {
            int[] cells = decode(state);
            states.add(cells);

            for (int c : cells) {
                if (c == 1) introCnt[state]++;
                else if (c == 2) extroCnt[state]++;
                rowScore[state] += base[c];
            }

            for (int j = 1; j < n; j++) {
                rowScore[state] += pair[cells[j - 1]][cells[j]];
            }
        }

        for (int a = 0; a < totalStates; a++) {
            for (int b = 0; b < totalStates; b++) {
                int score = 0;
                int[] ra = states.get(a);
                int[] rb = states.get(b);
                for (int j = 0; j < n; j++) {
                    score += pair[ra[j]][rb[j]];
                }
                vertical[a][b] = score;
            }
        }

        memo = new Integer[m + 1][introvertsCount + 1][extrovertsCount + 1][totalStates];
        return dfs(0, introvertsCount, extrovertsCount, 0);
    }

    private int dfs(int row, int introLeft, int extroLeft, int prevState) {
        if (row == m) return 0;
        if (memo[row][introLeft][extroLeft][prevState] != null) {
            return memo[row][introLeft][extroLeft][prevState];
        }

        int best = 0;
        int totalStates = states.size();

        for (int cur = 0; cur < totalStates; cur++) {
            if (introCnt[cur] > introLeft || extroCnt[cur] > extroLeft) continue;

            int gain = rowScore[cur] + vertical[prevState][cur];
            best = Math.max(best, gain + dfs(
                row + 1,
                introLeft - introCnt[cur],
                extroLeft - extroCnt[cur],
                cur
            ));
        }

        memo[row][introLeft][extroLeft][prevState] = best;
        return best;
    }

    private int[] decode(int state) {
        int[] cells = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            cells[i] = state % 3;
            state /= 3;
        }
        return cells;
    }
}
```

---

# 13. Final Takeaway

This problem looks intimidating because it mixes:

- combinatorial placement
- two person types
- asymmetric happiness updates
- adjacency constraints

But the decisive observation is:

```text
m, n <= 5
introvertsCount, extrovertsCount <= 6
```

That makes **profile DP** the right tool.

Once you encode row states or recent cell states compactly, the rest becomes standard memoized search.

The entire problem is really about:

> designing the right compressed state so local neighbor effects can be computed exactly once.
