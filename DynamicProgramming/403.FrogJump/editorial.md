# 403. Frog Jump — Exhaustive Solution Notes

## Overview

This problem asks whether a frog can cross a river by landing only on stones.

The frog starts on the first stone at position `0`, and:

- the **first jump must be exactly `1`**
- if the last jump was `k`, then the next jump can only be:
  - `k - 1`
  - `k`
  - `k + 1`
- the frog can only move **forward**
- the frog must land **exactly on a stone**

The question is not asking for the path itself. It only asks:

> **Is there at least one valid way to reach the last stone?**

That is an important clue. Combined with the fact that each decision depends on the **previous jump size**, this strongly suggests **dynamic programming**.

This write-up explains two DP approaches in detail:

1. **Top-Down Dynamic Programming (Recursion + Memoization)**
2. **Bottom-Up Dynamic Programming**

---

## Problem Statement

A frog is crossing a river.

The river is divided into units, and at each unit there may or may not be a stone.

You are given a sorted array `stones`, where:

```text
stones[i] = position of the i-th stone
```

The frog starts on the first stone and assumes:

- the first jump must be `1` unit

If the frog’s last jump was `k`, then the next jump must be one of:

```text
k - 1, k, or k + 1
```

The frog can only jump in the forward direction.

Return:

- `true` if the frog can reach the last stone
- `false` otherwise

---

## Example 1

**Input**

```text
stones = [0,1,3,5,6,8,12,17]
```

**Output**

```text
true
```

**Explanation**

One valid sequence is:

```text
0 -> 1   (jump 1)
1 -> 3   (jump 2)
3 -> 5   (jump 2)
5 -> 8   (jump 3)
8 -> 12  (jump 4)
12 -> 17 (jump 5)
```

So the frog can reach the last stone.

---

## Example 2

**Input**

```text
stones = [0,1,2,3,4,8,9,11]
```

**Output**

```text
false
```

**Explanation**

There is no valid way to reach the final stone.

The large gap between positions `4` and `8` makes crossing impossible under the jump rules.

---

## Constraints

- `2 <= stones.length <= 2000`
- `0 <= stones[i] <= 2^31 - 1`
- `stones[0] == 0`
- `stones` is sorted in strictly increasing order

---

# Key Observations

Before jumping into the solutions, a few important things stand out.

## 1. The frog’s future depends on its previous jump

If the frog just jumped `k`, then its next jump can only be:

```text
k - 1, k, k + 1
```

So the current state is not determined only by the stone position.

It also depends on the **last jump size**.

That means a state should include:

- current stone
- previous jump

---

## 2. The frog can only land on stones

If the frog is at position `x` and decides to jump `k`, then it must land on:

```text
x + k
```

If there is no stone there, that move is invalid.

So efficient lookup of whether a stone exists at a position is very important.

---

## 3. We only need a yes/no answer

We do not need the actual path.

That means we can define subproblems as:

> "From this stone, with this previous jump size, can the frog reach the end?"

This is a natural DP state.

---

# DP State Definition

A very useful state is:

```text
(index, prevJump)
```

where:

- `index` = the index of the stone the frog is currently standing on
- `prevJump` = the jump size used to reach this stone

Then the subproblem becomes:

> Can the frog reach the last stone starting from `stones[index]`, given that the previous jump size was `prevJump`?

This state is used in both top-down and bottom-up solutions.

---

# Why We Need a Map

At many steps we will compute a potential next position like:

```text
stones[index] + nextJump
```

We need to know quickly whether that position contains a stone.

If we search linearly every time, the solution becomes too slow.

So we create a map:

```text
position -> index
```

This lets us answer in near-constant time:

- does a stone exist at this position?
- if yes, what is its index?

---

# Approach 1: Top-Down Dynamic Programming

## Intuition

Think recursively.

Suppose the frog is currently on stone `index`, and the previous jump was `prevJump`.

Then the next jump can only be:

```text
prevJump - 1
prevJump
prevJump + 1
```

For each of these options:

1. ignore non-positive jumps
2. compute the next position
3. check if a stone exists there
4. recursively try from that stone

If any recursive path reaches the last stone, the answer is `true`.

This recursive structure is straightforward.

The problem is that many states repeat.

For example, the same stone index and jump size can be reached through multiple routes.

That creates overlapping subproblems, which is exactly where memoization helps.

---

## Recursive State

Let:

```text
solve(index, prevJump)
```

mean:

> whether the frog can reach the final stone starting from `stones[index]`, given that the last jump size was `prevJump`

---

## Base Case

If:

```text
index == n - 1
```

then the frog is already on the last stone.

So we return:

```text
true
```

---

## Transition

From `(index, prevJump)`, try:

```text
nextJump in {prevJump - 1, prevJump, prevJump + 1}
```

For each candidate:

- require `nextJump > 0`
- compute:
  ```text
  nextPosition = stones[index] + nextJump
  ```
- if `nextPosition` exists in the stone map, recurse on:
  ```text
  (mark[nextPosition], nextJump)
  ```

If any of those recursive calls returns `true`, then the current state is also `true`.

---

## Memoization

Without memoization, the recursion may explore the same state many times.

So we store results in a 2D memo table:

```text
dp[index][prevJump]
```

where:

- `-1` = not computed yet
- `1` = true
- `0` = false

This ensures each state is solved once.

---

## Algorithm

1. Build a map from stone position to stone index.
2. Initialize a DP table with `-1`.
3. Start recursion from:
   ```text
   solve(0, 0)
   ```
   because the frog starts on stone index `0` and has not jumped yet.
4. In each state:
   - if at last stone, return `true`
   - if memoized, return stored result
   - try `prevJump - 1`, `prevJump`, `prevJump + 1`
   - recurse only for positive jumps that land on a valid stone
   - store and return whether any option works

---

## Java Implementation — Top-Down DP

```java
class Solution {
    HashMap<Integer, Integer> mark = new HashMap<>();
    int dp[][] = new int[2001][2001];

    boolean solve(int[] stones, int n, int index, int prevJump) {
        // If reached the last stone, return true.
        if (index == n - 1) {
            return true;
        }

        // If already computed, return memoized answer.
        if (dp[index][prevJump] != -1) {
            return dp[index][prevJump] == 1;
        }

        boolean ans = false;

        // Try jumps: k - 1, k, k + 1
        for (int nextJump = prevJump - 1; nextJump <= prevJump + 1; nextJump++) {
            if (nextJump > 0 && mark.containsKey(stones[index] + nextJump)) {
                ans = ans || solve(stones, n, mark.get(stones[index] + nextJump), nextJump);
            }
        }

        dp[index][prevJump] = ans ? 1 : 0;
        return ans;
    }

    public boolean canCross(int[] stones) {
        for (int i = 0; i < stones.length; i++) {
            mark.put(stones[i], i);
        }

        for (int i = 0; i < 2000; i++) {
            Arrays.fill(dp[i], -1);
        }

        return solve(stones, stones.length, 0, 0);
    }
}
```

---

## Why Starting with `(0, 0)` Works

At the very beginning, the frog has not made any jump yet.

So we model the starting state as:

```text
index = 0
prevJump = 0
```

Then the next jumps considered are:

```text
-1, 0, 1
```

After filtering out non-positive jumps, only `1` remains.

That correctly enforces the rule that the first jump must be exactly `1`.

---

## Complexity Analysis — Top-Down DP

Let `N` be the number of stones.

### Time Complexity

A state is identified by:

- `index`
- `prevJump`

Both can take up to about `N` meaningful values.

So the total number of states is:

```text
O(N^2)
```

Each state tries at most 3 transitions.

So the total time complexity is:

```text
O(N^2)
```

---

### Space Complexity

We use:

- a memo table of size `O(N^2)`
- a map of size `O(N)`
- recursion stack up to `O(N)` in the worst case

So the total space complexity is:

```text
O(N^2)
```

---

# Approach 2: Bottom-Up Dynamic Programming

## Intuition

The top-down solution works well, but recursion uses call stack space.

We can avoid recursion by solving the same DP iteratively.

The core idea is:

If the frog can reach a state:

```text
(index, prevJump)
```

then from there it may be able to reach:

```text
(index of next stone, prevJump - 1)
(index of next stone, prevJump)
(index of next stone, prevJump + 1)
```

provided:

- the jump is positive
- the target position contains a stone

So instead of asking recursively whether a state is solvable, we propagate reachable states forward.

---

## DP Table Meaning

Let:

```text
dp[index][prevJump]
```

mean:

> whether it is possible for the frog to stand on `stones[index]` after making a jump of size `prevJump`

This is a reachability table.

---

## Initial State

At the start, the frog is standing on the first stone without having jumped yet:

```text
dp[0][0] = true
```

This is the only initially reachable state.

---

## Transition

For every state `(index, prevJump)` such that:

```text
dp[index][prevJump] == true
```

try the three next jumps:

```text
prevJump - 1
prevJump
prevJump + 1
```

For each positive jump:

1. compute target position
2. check if a stone exists
3. mark the corresponding state as reachable

---

## Final Check

After filling the table, if there exists any jump size `k` such that:

```text
dp[n - 1][k] == true
```

then the frog can reach the last stone.

Otherwise, it cannot.

---

## Algorithm

1. Build a map from stone position to index.
2. Initialize `dp[0][0] = true`.
3. Iterate over all possible `(index, prevJump)` pairs.
4. For every reachable state:
   - try `prevJump - 1`, `prevJump`, `prevJump + 1`
   - if the jump is valid and lands on a stone, mark the new state `true`
5. After processing all states, scan the last stone row of the DP table.
6. Return `true` if any state there is reachable.

---

## Java Implementation — Bottom-Up DP

```java
class Solution {
    HashMap<Integer, Integer> mark = new HashMap<Integer, Integer>();
    boolean dp[][] = new boolean[2001][2001];

    public boolean canCross(int[] stones) {
        int n = stones.length;

        // Map stone positions to their indices
        for (int i = 0; i < n; i++) {
            mark.put(stones[i], i);
        }

        dp[0][0] = true;

        for (int index = 0; index < n; index++) {
            for (int prevJump = 0; prevJump <= n; prevJump++) {
                if (dp[index][prevJump]) {
                    if (mark.containsKey(stones[index] + prevJump)) {
                        dp[mark.get(stones[index] + prevJump)][prevJump] = true;
                    }
                    if (mark.containsKey(stones[index] + prevJump + 1)) {
                        dp[mark.get(stones[index] + prevJump + 1)][prevJump + 1] = true;
                    }
                    if (mark.containsKey(stones[index] + prevJump - 1)) {
                        dp[mark.get(stones[index] + prevJump - 1)][prevJump - 1] = true;
                    }
                }
            }
        }

        for (int jump = 0; jump <= n; jump++) {
            if (dp[n - 1][jump]) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Important Note About `prevJump - 1`

In bottom-up code, when checking:

```text
stones[index] + prevJump - 1
```

the jump size could become zero or negative.

In this implementation, the map lookup safely prevents invalid backward or zero jumps from creating meaningful progress, because there will not be a valid forward stone reached in those cases starting from valid states.

Still, conceptually, it is cleaner to explicitly require:

```text
nextJump > 0
```

just as in the top-down approach.

A cleaner version is shown later.

---

## Complexity Analysis — Bottom-Up DP

Let `N` be the number of stones.

### Time Complexity

The DP table has states defined by:

- `index`
- `prevJump`

So there are at most:

```text
O(N^2)
```

states.

Each state processes at most 3 transitions.

Thus the total time complexity is:

```text
O(N^2)
```

---

### Space Complexity

We store:

- a DP table of size `O(N^2)`
- a map of size `O(N)`

So the total space complexity is:

```text
O(N^2)
```

---

# Comparing the Two Approaches

## Top-Down DP

### Advantages

- very natural to derive
- easy to express recursively
- memoization avoids repeated work

### Drawbacks

- uses recursion stack
- sometimes slightly slower due to recursive overhead

---

## Bottom-Up DP

### Advantages

- avoids recursion stack
- often slightly faster in practice
- computes states iteratively

### Drawbacks

- can be a little less intuitive initially
- full DP table is still `O(N^2)`

---

# Why `O(N^2)` Is Acceptable

The number of stones is at most:

```text
2000
```

So:

```text
N^2 = 4,000,000
```

A DP solution with around four million states is acceptable in this context.

That is why both `O(N^2)` approaches are practical.

---

# A Cleaner Bottom-Up Version

Here is a cleaner formulation of the bottom-up solution that explicitly checks valid positive jumps:

```java
class Solution {
    public boolean canCross(int[] stones) {
        int n = stones.length;
        HashMap<Integer, Integer> mark = new HashMap<>();
        boolean[][] dp = new boolean[n][n + 1];

        for (int i = 0; i < n; i++) {
            mark.put(stones[i], i);
        }

        dp[0][0] = true;

        for (int index = 0; index < n; index++) {
            for (int prevJump = 0; prevJump <= n; prevJump++) {
                if (!dp[index][prevJump]) {
                    continue;
                }

                for (int nextJump = prevJump - 1; nextJump <= prevJump + 1; nextJump++) {
                    if (nextJump <= 0) {
                        continue;
                    }

                    int nextPosition = stones[index] + nextJump;
                    if (mark.containsKey(nextPosition)) {
                        int nextIndex = mark.get(nextPosition);
                        dp[nextIndex][nextJump] = true;
                    }
                }
            }
        }

        for (int jump = 0; jump <= n; jump++) {
            if (dp[n - 1][jump]) {
                return true;
            }
        }

        return false;
    }
}
```

This version makes the transition logic clearer and aligns more closely with the problem statement.

---

# Small Walkthrough of Example 1

Input:

```text
stones = [0,1,3,5,6,8,12,17]
```

Start:

```text
dp[0][0] = true
```

From `(0, 0)`, only jump `1` is valid:

- reach stone at position `1`
- mark:
  ```text
  dp[1][1] = true
  ```

From `(1, 1)`, possible jumps are `{0,1,2}`:

- jump `2` reaches position `3`
- mark:
  ```text
  dp[2][2] = true
  ```

From `(2, 2)`, possible jumps are `{1,2,3}`:

- jump `2` reaches `5`
- jump `3` reaches `6`

Continue propagating states.

Eventually:

- from `8` with jump `4`, frog reaches `12`
- from `12` with jump `5`, frog reaches `17`

So the last stone becomes reachable.

---

# Why Example 2 Fails

Input:

```text
stones = [0,1,2,3,4,8,9,11]
```

At first, several stones are reachable.

But eventually the frog reaches position `4`, and the next stone is at `8`, a gap of `4`.

Given the sequence of allowed jump sizes leading up to that point, there is no valid way to make that jump while respecting the `k-1`, `k`, `k+1` rule.

So the last stone remains unreachable.

---

# Common Mistakes

## 1. Tracking Only Position, Not Previous Jump

This is incorrect.

The same stone can be reached with different jump sizes, and those states are not equivalent.

For example:

- reaching stone `x` with last jump `2`
- reaching stone `x` with last jump `5`

lead to completely different future possibilities.

So the DP state must include both:

- current stone
- previous jump size

---

## 2. Using Stone Position as Array Index Directly

Stone positions can be as large as:

```text
2^31 - 1
```

So we cannot build arrays indexed by position.

We must use a map from position to index instead.

---

## 3. Forgetting the First Jump Constraint

The first jump must be exactly `1`.

The top-down `(0, 0)` setup handles this naturally.

In other solutions, this must be enforced carefully.

---

## 4. Allowing Zero or Negative Jumps

The frog can only move forward.

So jumps must always satisfy:

```text
nextJump > 0
```

---

# Interview Perspective

This problem is good for checking whether someone can identify the correct DP state.

A strong explanation usually goes like this:

1. The frog’s future depends on the previous jump, so position alone is not enough.
2. Define state as `(stone index, previous jump size)`.
3. Use a map to jump quickly from target position to stone index.
4. Solve either with:
   - top-down memoization, or
   - bottom-up reachability DP.

That is the key insight.

---

# Final Summary

## State

A useful state is:

```text
(index, prevJump)
```

which means:

- frog is at `stones[index]`
- last jump size was `prevJump`

---

## Top-Down DP

### Idea

Try all three next jumps recursively and memoize results.

### Complexity

- Time: `O(N^2)`
- Space: `O(N^2)`

---

## Bottom-Up DP

### Idea

Mark reachable states iteratively and propagate forward.

### Complexity

- Time: `O(N^2)`
- Space: `O(N^2)`

---

# Best Final Java Solution (Top-Down)

```java
class Solution {
    HashMap<Integer, Integer> mark = new HashMap<>();
    int[][] dp = new int[2001][2001];

    boolean solve(int[] stones, int n, int index, int prevJump) {
        if (index == n - 1) {
            return true;
        }

        if (dp[index][prevJump] != -1) {
            return dp[index][prevJump] == 1;
        }

        boolean ans = false;

        for (int nextJump = prevJump - 1; nextJump <= prevJump + 1; nextJump++) {
            if (nextJump > 0 && mark.containsKey(stones[index] + nextJump)) {
                ans = ans || solve(stones, n, mark.get(stones[index] + nextJump), nextJump);
            }
        }

        dp[index][prevJump] = ans ? 1 : 0;
        return ans;
    }

    public boolean canCross(int[] stones) {
        for (int i = 0; i < stones.length; i++) {
            mark.put(stones[i], i);
        }

        for (int i = 0; i < 2000; i++) {
            Arrays.fill(dp[i], -1);
        }

        return solve(stones, stones.length, 0, 0);
    }
}
```

---

# Best Final Java Solution (Bottom-Up)

```java
class Solution {
    public boolean canCross(int[] stones) {
        int n = stones.length;
        HashMap<Integer, Integer> mark = new HashMap<>();
        boolean[][] dp = new boolean[n][n + 1];

        for (int i = 0; i < n; i++) {
            mark.put(stones[i], i);
        }

        dp[0][0] = true;

        for (int index = 0; index < n; index++) {
            for (int prevJump = 0; prevJump <= n; prevJump++) {
                if (!dp[index][prevJump]) {
                    continue;
                }

                for (int nextJump = prevJump - 1; nextJump <= prevJump + 1; nextJump++) {
                    if (nextJump <= 0) {
                        continue;
                    }

                    int nextPosition = stones[index] + nextJump;
                    if (mark.containsKey(nextPosition)) {
                        int nextIndex = mark.get(nextPosition);
                        dp[nextIndex][nextJump] = true;
                    }
                }
            }
        }

        for (int jump = 0; jump <= n; jump++) {
            if (dp[n - 1][jump]) {
                return true;
            }
        }

        return false;
    }
}
```

These are the standard detailed DP solutions for the problem.
