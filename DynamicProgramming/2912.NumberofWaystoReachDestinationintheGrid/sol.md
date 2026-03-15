# Number of Ways to Reach Destination in Exactly `k` Moves

## Problem Restatement

You are given an `n x m` 1-indexed grid, a starting cell `source = [sx, sy]`, a destination cell `dest = [dx, dy]`, and an integer `k`.

In one move, from cell `[x1, y1]`, you may move to **any other cell** in the **same row** or **same column**:

- same row: `[x1, y2]` where `y2 != y1`
- same column: `[x2, y1]` where `x2 != x1`

You must compute the number of distinct ways to reach `dest` from `source` in **exactly `k` moves**, modulo `10^9 + 7`.

---

## Core Insight

A direct DP over all cells would be too literal.

At first glance, it may seem that we need to track the exact cell after every move. But that would be wasteful because the movement rule only depends on whether your current cell shares:

- the same **row** as `dest`
- the same **column** as `dest`

That means many cells are behaviorally identical with respect to the destination.

So instead of tracking exact coordinates, we classify every cell into one of **four categories** based on its relationship to `dest`.

---

## The 4 States

Let `dest = [dx, dy]`.

We define:

### State A: exactly at destination

Current cell is:

- `[dx, dy]`

### State B: same row as destination, but not destination

Current cell is:

- `[dx, y]` where `y != dy`

### State C: same column as destination, but not destination

Current cell is:

- `[x, dy]` where `x != dx`

### State D: neither same row nor same column as destination

Current cell is:

- `[x, y]` where `x != dx` and `y != dy`

These four states completely capture everything that matters for future transitions.

---

## Why This Compression Is Valid

This is the key justification.

Suppose you are in some cell in state `B`, meaning:

- same row as destination
- not the destination itself

No matter **which exact column** you are in, the set of possible transitions to the 4 destination-relative states is always the same count.

Similarly, every cell in state `C` behaves identically, and every cell in state `D` behaves identically.

So instead of doing DP over all `n * m` cells, we can do DP over just these 4 states.

This makes the solution both elegant and efficient.

---

## Transition Counting in Detail

We now compute how many ways there are to move from each state to every other state in one move.

---

## Transitions from State A

State `A` means you are already at `dest = [dx, dy]`.

From here, you can move to:

- any other cell in the same row: `m - 1` choices
- any other cell in the same column: `n - 1` choices

Those destinations fall into:

- same row but not destination -> `B`
- same column but not destination -> `C`

So:

- `A -> B = m - 1`
- `A -> C = n - 1`
- `A -> A = 0`
- `A -> D = 0`

---

## Transitions from State B

State `B` means you are at `[dx, y]` where `y != dy`.

You are in the destination row, but not at destination.

### Possible row moves

You can move within the same row to any other column:

- total row choices: `m - 1`
- among them:
  - one is `dest = [dx, dy]` -> state `A`
  - one is the current cell, which is excluded already by move rule
  - the remaining `m - 2` are still in same row as destination -> state `B`

So from row moves:

- `B -> A = 1`
- `B -> B = m - 2`

### Possible column moves

If you move in the same column, the column remains `y`, which is **not** `dy`.
So every such move changes the row away from `dx`, and the new cell is neither in destination row nor destination column.

There are `n - 1` such cells.

So:

- `B -> D = n - 1`

No column move can land in `C` or `A`.

Therefore total transitions from `B` are:

- `B -> A = 1`
- `B -> B = m - 2`
- `B -> C = 0`
- `B -> D = n - 1`

---

## Transitions from State C

This is symmetric to state `B`.

You are at `[x, dy]` where `x != dx`.

### Column moves

Moving within the same column:

- one choice reaches destination -> `A`
- remaining `n - 2` stay in same column as destination -> `C`

### Row moves

Moving within the same row keeps row `x != dx` and changes the column away from `dy`, so all such cells go to `D`.

There are `m - 1` such cells.

So:

- `C -> A = 1`
- `C -> B = 0`
- `C -> C = n - 2`
- `C -> D = m - 1`

---

## Transitions from State D

State `D` means you are at `[x, y]` with:

- `x != dx`
- `y != dy`

So you currently share neither row nor column with destination.

### Move within same row

If you stay in row `x` and change column:

- choosing column `dy` lands at `[x, dy]` -> state `C`
- the other `m - 2` columns keep you away from destination column -> still `D`

So row moves contribute:

- `D -> C = 1`
- `D -> D += m - 2`

### Move within same column

If you stay in column `y` and change row:

- choosing row `dx` lands at `[dx, y]` -> state `B`
- the other `n - 2` rows keep you away from destination row -> still `D`

So column moves contribute:

- `D -> B = 1`
- `D -> D += n - 2`

Total:

- `D -> A = 0`
- `D -> B = 1`
- `D -> C = 1`
- `D -> D = (m - 2) + (n - 2) = m + n - 4`

---

## DP Formulation

Let after some number of moves:

- `a` = number of ways to be in state `A`
- `b` = number of ways to be in state `B`
- `c` = number of ways to be in state `C`
- `d` = number of ways to be in state `D`

Then after one more move:

### New value for A

The only ways to reach destination in one move are:

- from `B` by moving directly to destination
- from `C` by moving directly to destination

So:

```text
newA = b + c
```

### New value for B

You can end in `B` from:

- `A`, in `m - 1` ways
- `B`, in `m - 2` ways
- `D`, in `1` way

So:

```text
newB = a * (m - 1) + b * (m - 2) + d
```

### New value for C

You can end in `C` from:

- `A`, in `n - 1` ways
- `C`, in `n - 2` ways
- `D`, in `1` way

So:

```text
newC = a * (n - 1) + c * (n - 2) + d
```

### New value for D

You can end in `D` from:

- `B`, in `n - 1` ways
- `C`, in `m - 1` ways
- `D`, in `m + n - 4` ways

So:

```text
newD = b * (n - 1) + c * (m - 1) + d * (m + n - 4)
```

All operations are modulo `10^9 + 7`.

---

## Initialization

We initialize the starting state based on the position of `source` relative to `dest`.

### Case 1: source == dest

Start in state `A`:

- `a = 1`

### Case 2: same row, different column

Start in state `B`:

- `b = 1`

### Case 3: same column, different row

Start in state `C`:

- `c = 1`

### Case 4: neither same row nor same column

Start in state `D`:

- `d = 1`

Then apply the transition exactly `k` times.

At the end, the answer is `a`, because `A` means being exactly at the destination.

---

## Full Java Code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;

    public int numberOfWays(int n, int m, int k, int[] source, int[] dest) {
        long a = 0, b = 0, c = 0, d = 0;

        int sx = source[0], sy = source[1];
        int dx = dest[0], dy = dest[1];

        if (sx == dx && sy == dy) {
            a = 1;
        } else if (sx == dx) {
            b = 1;
        } else if (sy == dy) {
            c = 1;
        } else {
            d = 1;
        }

        for (int step = 0; step < k; step++) {
            long newA = (b + c) % MOD;
            long newB = (a * (m - 1) + b * (m - 2) + d) % MOD;
            long newC = (a * (n - 1) + c * (n - 2) + d) % MOD;
            long newD = (b * (n - 1) + c * (m - 1) + d * (n + m - 4)) % MOD;

            a = newA;
            b = newB;
            c = newC;
            d = newD;
        }

        return (int) a;
    }
}
```

---

## Code Walkthrough

### MOD constant

```java
private static final long MOD = 1_000_000_007L;
```

We use modulo arithmetic because the number of paths can become very large.

`long` is used for intermediate multiplication so we do not overflow `int` before taking modulo.

---

### State variables

```java
long a = 0, b = 0, c = 0, d = 0;
```

These hold the counts for the 4 compressed states:

- `a`: at destination
- `b`: same row as destination, not destination
- `c`: same column as destination, not destination
- `d`: neither same row nor same column

---

### Source and destination coordinates

```java
int sx = source[0], sy = source[1];
int dx = dest[0], dy = dest[1];
```

We extract coordinates for readability.

---

### Initialization logic

```java
if (sx == dx && sy == dy) {
    a = 1;
} else if (sx == dx) {
    b = 1;
} else if (sy == dy) {
    c = 1;
} else {
    d = 1;
}
```

This places the starting cell into exactly one of the 4 categories.

Only one state starts with count `1`; the others are `0`.

---

### Transition loop

```java
for (int step = 0; step < k; step++) {
    long newA = (b + c) % MOD;
    long newB = (a * (m - 1) + b * (m - 2) + d) % MOD;
    long newC = (a * (n - 1) + c * (n - 2) + d) % MOD;
    long newD = (b * (n - 1) + c * (m - 1) + d * (n + m - 4)) % MOD;

    a = newA;
    b = newB;
    c = newC;
    d = newD;
}
```

Each iteration corresponds to exactly one move.

We compute the next values from the current values, then replace the old state counts.

Using temporary variables is important because all transitions for the next step must be based on the previous step's counts.

---

### Final answer

```java
return (int) a;
```

After exactly `k` moves, `a` stores the number of ways to be exactly at `dest`.

---

## Worked Example

Consider:

- `n = 3`, `m = 3`
- `source = [1, 1]`
- `dest = [1, 3]`
- `k = 1`

### Initial state

`source` shares the same row as `dest`, but is not destination.

So:

- `a = 0`
- `b = 1`
- `c = 0`
- `d = 0`

### One transition

```text
newA = b + c = 1 + 0 = 1
newB = a*(m-1) + b*(m-2) + d = 0*2 + 1*1 + 0 = 1
newC = a*(n-1) + c*(n-2) + d = 0*2 + 0*1 + 0 = 0
newD = b*(n-1) + c*(m-1) + d*(m+n-4) = 1*2 + 0*2 + 0*2 = 2
```

So after 1 move:

- `a = 1`

Answer = `1`, which is correct, because from `[1,1]` to `[1,3]` there is exactly one direct move.

---

## Another Sanity Check

### If source == dest and k = 1

You must move to a different cell in one move, so it should be impossible to still be at destination after exactly one move.

Initialization:

- `a = 1`
- `b = c = d = 0`

After one step:

```text
newA = b + c = 0
```

Correct.

---

## Why This Is Better Than Cell-Based DP

A naïve DP might try:

```text
dp[step][x][y] = number of ways to be at cell (x, y) after step moves
```

Then for each cell, transition to every cell in its row and column.

That would be very expensive:

- `O(k * n * m * (n + m))` in the straightforward implementation

Even with some optimization, it is still much heavier than necessary.

The compressed-state DP avoids all irrelevant detail and reduces the problem to only 4 counts.

That is the real conceptual win.

---

## Time Complexity

Each of the `k` steps performs only constant work:

- a few arithmetic operations
- no loops over grid cells

So the time complexity is:

```text
O(k)
```

---

## Space Complexity

We store only:

- 4 current state values
- 4 next state values

So the space complexity is:

```text
O(1)
```

---

## Final Takeaway

The main idea is not about path enumeration. It is about **state compression**.

The destination acts as a reference point, and every cell can be grouped by how it relates to that destination:

- at destination
- same row
- same column
- neither

Once that grouping is recognized, the problem becomes a compact 4-state DP with simple transition formulas.

That is why the solution is both efficient and mathematically clean.
