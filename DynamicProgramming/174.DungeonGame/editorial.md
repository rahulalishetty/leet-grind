# 174. Dungeon Game — Exhaustive Solution Notes

## Overview

This problem is a classic **2D dynamic programming** problem.

At first glance, it may look like a pathfinding problem where we try to simulate the knight moving from the top-left corner to the bottom-right corner. But that forward perspective is actually awkward, because the knight's survival depends not only on what has happened so far, but also on how much health he must still preserve for future rooms.

That is the key reason this problem becomes much cleaner when solved **backwards**.

Instead of asking:

> "How much health does the knight have after reaching this cell?"

we ask:

> "What is the minimum health the knight must have upon entering this cell so that he can still reach the princess safely?"

That reverse viewpoint leads directly to a dynamic programming solution.

This write-up covers two approaches in detail:

1. **Dynamic Programming with a 2D DP table**
2. **Dynamic Programming with reduced space using a circular queue / rolling structure**

---

## Problem Statement

The demons have captured the princess and imprisoned her in the **bottom-right corner** of a dungeon.

The dungeon is an `m x n` grid.

A knight starts at the **top-left** cell and must rescue the princess by reaching the **bottom-right** cell.

Each room may affect the knight’s health:

- **negative value** → the knight loses health,
- **0** → no effect,
- **positive value** → the knight gains health.

The knight may only move:

- **right**, or
- **down**

The knight dies immediately if his health becomes **0 or below** at any time.

We must return the **minimum initial health** required so that the knight can rescue the princess.

---

## Example 1

**Input**

```text
dungeon = [
  [-2, -3,  3],
  [-5,-10,  1],
  [10, 30, -5]
]
```

**Output**

```text
7
```

**Explanation**

The knight can survive with initial health `7` if he takes the optimal path:

```text
RIGHT → RIGHT → DOWN → DOWN
```

---

## Example 2

**Input**

```text
dungeon = [[0]]
```

**Output**

```text
1
```

**Explanation**

The knight starts and ends in the same cell.
Even if the room is neutral, he must still have at least `1` health to stay alive.

---

## Constraints

- `m == dungeon.length`
- `n == dungeon[i].length`
- `1 <= m, n <= 200`
- `-1000 <= dungeon[i][j] <= 1000`

---

# Why a Forward Greedy Approach Fails

A natural first attempt is to move from the start to the end while trying to preserve as much health as possible.

That approach is unreliable.

Why?

Because the problem is not asking:

- for the **maximum remaining health** at the end,

but rather:

- for the **minimum initial health** required to survive the entire route.

A path that looks attractive early may later force the knight through heavily damaging rooms.

So the decision at a cell depends on what comes **after** it.

That is why a backward dynamic programming approach is the right fit.

---

# Core Reverse Insight

Suppose we are standing at some cell `(row, col)`.

Instead of tracking how much health we currently have, we define:

```text
dp[row][col] = minimum health needed upon entering cell (row, col)
               so that the knight can still reach the princess alive
```

This definition is powerful because once we know the minimum health required for the next cell, we can determine what is required for the current cell.

---

# Approach 1: Dynamic Programming

## Intuition

The destination is the **bottom-right** cell.

If we know the minimum health needed when entering the destination cell, then we can work backwards through the grid.

For any cell, the knight has at most two possible next moves:

- move **right**
- move **down**

So from the current cell, we compute the minimum health required if we choose the right path and the minimum health required if we choose the down path, then take the better one.

---

## Understanding the Destination Cell

Let the destination cell contain value `dungeon[m-1][n-1]`.

The knight must survive after entering that room.

So:

- if the room gives health or is neutral, the knight still needs at least `1` health,
- if the room causes damage `-x`, the knight needs at least `x + 1`.

So the formula is:

```text
dp[m-1][n-1] = max(1, 1 - dungeon[m-1][n-1])
```

---

## Transition for Any Other Cell

Suppose the knight is at cell `(row, col)` with value:

```text
curr = dungeon[row][col]
```

If the knight moves right, then he must have enough health to enter the right cell safely:

```text
required_from_right = dp[row][col + 1]
```

Because the current cell changes health by `curr`, the health needed upon entering the current cell becomes:

```text
required_if_go_right = max(1, dp[row][col + 1] - curr)
```

Similarly, if the knight moves down:

```text
required_if_go_down = max(1, dp[row + 1][col] - curr)
```

Since the knight will choose the better path, we take the smaller one:

```text
dp[row][col] = min(required_if_go_right, required_if_go_down)
```

---

## Why `max(1, ...)` Is Necessary

Even if the next cell requires very little health and the current cell gives a huge health boost, the knight still cannot enter the current cell with `0` or negative health.

So the minimum possible health at any moment is always:

```text
1
```

That is why every transition is wrapped with:

```text
max(1, ...)
```

---

## DP State Definition

We define:

```text
dp[row][col]
```

as:

> the minimum health required upon entering `dungeon[row][col]` so that the knight can eventually rescue the princess.

---

## Fill Order

Because every cell depends on:

- the cell to the **right**, and
- the cell **below**,

we must fill the DP table in reverse order:

- from **bottom to top**
- from **right to left**

That means:

- `row` goes from `rows - 1` down to `0`
- `col` goes from `cols - 1` down to `0`

---

## Detailed Thought Process with the Example

For:

```text
[
  [-2, -3,  3],
  [-5,-10,  1],
  [10, 30, -5]
]
```

Start at destination `(2, 2)`:

```text
dungeon[2][2] = -5
```

So:

```text
dp[2][2] = max(1, 1 - (-5)) = 6
```

That means the knight must enter the princess cell with at least `6` health.

Now consider `(2, 1)` which contains `30`.

From there, the only move is right into a cell requiring `6`.

So:

```text
dp[2][1] = max(1, 6 - 30) = 1
```

That makes sense: the knight can enter with just `1` health, gain `30`, and then survive the final `-5`.

Now consider `(1, 2)` which contains `1`.

From there, the only move is down into a cell requiring `6`.

So:

```text
dp[1][2] = max(1, 6 - 1) = 5
```

Continue this process for all cells, and eventually:

```text
dp[0][0] = 7
```

which is the answer.

---

## Formal Algorithm

1. Create a 2D array `dp[rows][cols]`.
2. Traverse the dungeon from bottom-right to top-left.
3. For each cell:
   - compute health required if moving right,
   - compute health required if moving down,
   - choose the minimum of the available options.
4. If the current cell is the destination, compute its value directly.
5. Return `dp[0][0]`.

---

## Java Implementation — 2D DP

```java
class Solution {
    int inf = Integer.MAX_VALUE;
    int[][] dp;
    int rows, cols;

    public int getMinHealth(int currCell, int nextRow, int nextCol) {
        if (nextRow >= this.rows || nextCol >= this.cols) return inf;

        int nextCell = this.dp[nextRow][nextCol];

        // The knight must always have at least 1 health.
        return Math.max(1, nextCell - currCell);
    }

    public int calculateMinimumHP(int[][] dungeon) {
        this.rows = dungeon.length;
        this.cols = dungeon[0].length;
        this.dp = new int[rows][cols];

        for (int[] arr : this.dp) {
            Arrays.fill(arr, this.inf);
        }

        int currCell, rightHealth, downHealth, nextHealth, minHealth;

        for (int row = this.rows - 1; row >= 0; --row) {
            for (int col = this.cols - 1; col >= 0; --col) {
                currCell = dungeon[row][col];

                rightHealth = getMinHealth(currCell, row, col + 1);
                downHealth = getMinHealth(currCell, row + 1, col);
                nextHealth = Math.min(rightHealth, downHealth);

                if (nextHealth != inf) {
                    minHealth = nextHealth;
                } else {
                    // Destination cell
                    minHealth = currCell >= 0 ? 1 : 1 - currCell;
                }

                this.dp[row][col] = minHealth;
            }
        }

        return this.dp[0][0];
    }
}
```

---

## Complexity Analysis — Approach 1

### Time Complexity

We visit each cell exactly once.

If the dungeon has `M` rows and `N` columns, the total number of cells is:

```text
M × N
```

So the time complexity is:

```text
O(M × N)
```

---

### Space Complexity

We store a DP matrix of the same size as the dungeon:

```text
O(M × N)
```

So the space complexity is:

```text
O(M × N)
```

---

# Approach 2: Dynamic Programming with Circular Queue

## Intuition

In the 2D DP solution, each `dp[row][col]` only depends on two neighboring values:

- the value to the right,
- the value below.

That means we do **not** actually need to store the entire 2D matrix at once.

We only need enough previously computed values to represent the "active window" of dependencies.

This allows us to reduce space from:

```text
O(M × N)
```

to:

```text
O(N)
```

where `N` is the number of columns.

---

## Flattening the DP Matrix

Conceptually, the 2D DP table can be flattened into 1D:

```text
dp[row][col]  →  dp[row * N + col]
```

When computing entries in reverse order, each new DP state only depends on:

- one neighboring value from the current rolling row,
- one value from the previous row.

So only a window of size `N` is needed.

That is where a **circular queue** can be used.

---

## Why a Circular Queue Works

A circular queue acts like a fixed-size sliding window.

As we compute more DP states, older values that are no longer needed can be overwritten.

Because at any point we only need the last `N` relevant DP values, a queue with capacity `N` is sufficient.

This reduces memory usage significantly.

---

## Circular Queue Structure

The queue supports two operations:

- `enQueue(value)` → insert a newly computed DP value
- `get(index)` → retrieve a previously stored value using modular indexing

This way, it behaves like a rolling DP buffer.

---

## Java Implementation — Circular Queue Version

```java
class MyCircularQueue {
    protected int capacity;
    protected int tailIndex;
    public int[] queue;

    public MyCircularQueue(int capacity) {
        this.queue = new int[capacity];
        this.tailIndex = 0;
        this.capacity = capacity;
    }

    public void enQueue(int value) {
        this.queue[this.tailIndex] = value;
        this.tailIndex = (this.tailIndex + 1) % this.capacity;
    }

    public int get(int index) {
        return this.queue[index % this.capacity];
    }
}

class Solution {
    int inf = Integer.MAX_VALUE;
    MyCircularQueue dp;
    int rows, cols;

    public int getMinHealth(int currCell, int nextRow, int nextCol) {
        if (nextRow < 0 || nextCol < 0) return inf;

        int index = cols * nextRow + nextCol;
        int nextCell = this.dp.get(index);

        // The knight must always have at least 1 health.
        return Math.max(1, nextCell - currCell);
    }

    public int calculateMinimumHP(int[][] dungeon) {
        this.rows = dungeon.length;
        this.cols = dungeon[0].length;
        this.dp = new MyCircularQueue(this.cols);

        int currCell, rightHealth, downHealth, nextHealth, minHealth;

        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.cols; ++col) {
                currCell = dungeon[rows - row - 1][cols - col - 1];

                rightHealth = getMinHealth(currCell, row, col - 1);
                downHealth = getMinHealth(currCell, row - 1, col);
                nextHealth = Math.min(rightHealth, downHealth);

                if (nextHealth != inf) {
                    minHealth = nextHealth;
                } else {
                    // Destination cell
                    minHealth = currCell >= 0 ? 1 : 1 - currCell;
                }

                this.dp.enQueue(minHealth);
            }
        }

        // Last result corresponds to dp[0][0]
        return this.dp.get(this.cols - 1);
    }
}
```

---

## Complexity Analysis — Approach 2

### Time Complexity

We still process every dungeon cell exactly once.

So the time complexity remains:

```text
O(M × N)
```

---

### Space Complexity

The circular queue stores at most `N` values, where `N` is the number of columns.

So the space complexity becomes:

```text
O(N)
```

---

# Important Practical Note

Although the circular queue solution is clever, in practice most candidates and engineers prefer a simpler rolling-array implementation instead of explicitly using a circular queue.

Why?

Because the circular queue version is harder to reason about during interviews and more error-prone.

A standard 1D rolling DP array is usually the clearer optimization.

Still, the circular queue formulation is valuable because it shows the underlying space-reduction idea explicitly.

---

# Cleaner Space-Optimized Alternative

A more common `O(N)` solution uses a 1D array directly.

Let `dp[col]` represent the minimum health needed for the current row at column `col`.

We process from bottom-right to top-left.

A common implementation looks like this:

```java
class Solution {
    public int calculateMinimumHP(int[][] dungeon) {
        int rows = dungeon.length;
        int cols = dungeon[0].length;

        int[] dp = new int[cols];

        for (int row = rows - 1; row >= 0; row--) {
            for (int col = cols - 1; col >= 0; col--) {
                if (row == rows - 1 && col == cols - 1) {
                    dp[col] = Math.max(1, 1 - dungeon[row][col]);
                } else if (row == rows - 1) {
                    dp[col] = Math.max(1, dp[col + 1] - dungeon[row][col]);
                } else if (col == cols - 1) {
                    dp[col] = Math.max(1, dp[col] - dungeon[row][col]);
                } else {
                    int next = Math.min(dp[col], dp[col + 1]);
                    dp[col] = Math.max(1, next - dungeon[row][col]);
                }
            }
        }

        return dp[0];
    }
}
```

This version has:

- Time: `O(M × N)`
- Space: `O(N)`

and is usually easier to explain.

---

# Why the Reverse DP Formula Is Correct

Let us verify the recurrence carefully.

Suppose at cell `(r, c)` the dungeon value is `x`.

Suppose the knight chooses a next cell that requires `need` health upon entry.

Then after passing through `(r, c)`, the knight’s health becomes:

```text
health_after_current = initial_health_at_current + x
```

To safely enter the next cell, we need:

```text
initial_health_at_current + x >= need
```

So:

```text
initial_health_at_current >= need - x
```

But the knight must also stay alive in the current cell, so:

```text
initial_health_at_current >= 1
```

Combining both:

```text
initial_health_at_current = max(1, need - x)
```

That is exactly the DP formula.

---

# Common Mistakes

## 1. Solving from Start to End

Many people try to compute the best health while moving forward.

That becomes messy because you do not know how much health must be preserved for future damage.

Backward DP avoids that problem completely.

---

## 2. Forgetting Health Must Always Stay Above Zero

The knight does not just need to survive overall.
He must survive **at every single step**.

That is why `max(1, ...)` is essential.

---

## 3. Using Path Sum Logic

This is not a minimum path sum problem.

A path with a larger total sum may still require a larger initial health if it contains severe dips early in the route.

The order and timing of damage matter.

---

## 4. Mishandling the Destination Cell

The princess cell still affects health.

If it contains `-5`, the knight must enter with at least `6`, not `1`.

---

# Interview Perspective

This problem is excellent for testing whether someone can identify the correct DP state.

The key leap is this:

Instead of storing:

- current health remaining,

store:

- minimum health required to enter this cell safely.

That state definition makes the problem simple.

A strong interview answer usually proceeds like this:

1. Explain why forward simulation is difficult.
2. Define the reverse DP state.
3. Derive the recurrence:
   ```text
   dp[r][c] = max(1, min(dp[r+1][c], dp[r][c+1]) - dungeon[r][c])
   ```
4. Handle boundary conditions carefully.
5. Mention the `O(N)` space optimization.

---

# Final Summary

## DP Definition

```text
dp[row][col] = minimum health needed upon entering cell (row, col)
```

---

## Transition

For non-destination cells:

```text
dp[row][col] =
    max(1, min(dp[row+1][col], dp[row][col+1]) - dungeon[row][col])
```

---

## Destination

```text
dp[rows-1][cols-1] = max(1, 1 - dungeon[rows-1][cols-1])
```

---

## Complexities

### Approach 1: 2D DP

- Time: `O(M × N)`
- Space: `O(M × N)`

### Approach 2: Circular Queue / Rolling Window

- Time: `O(M × N)`
- Space: `O(N)`

---

# Best Practical Java Solution

```java
class Solution {
    public int calculateMinimumHP(int[][] dungeon) {
        int rows = dungeon.length;
        int cols = dungeon[0].length;
        int[][] dp = new int[rows][cols];

        for (int row = rows - 1; row >= 0; row--) {
            for (int col = cols - 1; col >= 0; col--) {
                if (row == rows - 1 && col == cols - 1) {
                    dp[row][col] = Math.max(1, 1 - dungeon[row][col]);
                } else if (row == rows - 1) {
                    dp[row][col] = Math.max(1, dp[row][col + 1] - dungeon[row][col]);
                } else if (col == cols - 1) {
                    dp[row][col] = Math.max(1, dp[row + 1][col] - dungeon[row][col]);
                } else {
                    int next = Math.min(dp[row + 1][col], dp[row][col + 1]);
                    dp[row][col] = Math.max(1, next - dungeon[row][col]);
                }
            }
        }

        return dp[0][0];
    }
}
```

This is the cleanest version for explanation and implementation.

---

# Best Space-Optimized Java Solution

```java
class Solution {
    public int calculateMinimumHP(int[][] dungeon) {
        int rows = dungeon.length;
        int cols = dungeon[0].length;
        int[] dp = new int[cols];

        for (int row = rows - 1; row >= 0; row--) {
            for (int col = cols - 1; col >= 0; col--) {
                if (row == rows - 1 && col == cols - 1) {
                    dp[col] = Math.max(1, 1 - dungeon[row][col]);
                } else if (row == rows - 1) {
                    dp[col] = Math.max(1, dp[col + 1] - dungeon[row][col]);
                } else if (col == cols - 1) {
                    dp[col] = Math.max(1, dp[col] - dungeon[row][col]);
                } else {
                    int next = Math.min(dp[col], dp[col + 1]);
                    dp[col] = Math.max(1, next - dungeon[row][col]);
                }
            }
        }

        return dp[0];
    }
}
```
