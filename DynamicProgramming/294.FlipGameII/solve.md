# 294. Flip Game II

## Problem Statement

You are given a string `currentState` consisting only of:

- `'+'`
- `'-'`

Two players take turns. On each turn, a player may choose any occurrence of:

```text
"++"
```

and flip it into:

```text
"--"
```

The player who cannot make a move loses.

Return:

- `true` if the starting player can guarantee a win
- `false` otherwise

Both players are assumed to play optimally.

---

## Example 1

```text
Input:  "++++"
Output: true
```

Explanation:

The starting player can flip the middle `"++"`:

```text
"++++" -> "+--+"
```

The opponent then has no valid move, so the starting player wins.

---

## Example 2

```text
Input:  "+"
Output: false
```

Explanation:

There is no `"++"` to flip, so the starting player loses immediately.

---

## Constraints

- `1 <= currentState.length <= 60`
- `currentState[i]` is either `'+'` or `'-'`
- There cannot be more than **20 consecutive `'+'`**

---

# Core Insight

This is a **winning-position / losing-position** game.

A game state is:

- **winning** if there exists at least one move to a **losing** state for the opponent
- **losing** if every legal move leads to a **winning** state for the opponent

That leads to the standard recursive game-theory formulation:

```text
canWin(state) = true
if there exists a legal move to a state where canWin(nextState) = false
```

This is the fundamental idea behind all workable solutions.

---

# Approach 1: Plain Backtracking

## Intuition

Try every possible move.

For each `"++"`:

1. flip it to `"--"`
2. recursively ask whether the opponent can win from that new state
3. if the opponent cannot win, then the current player can force a win

If no move causes the opponent to lose, then the current state is losing.

This is the most direct solution.

---

## Java Code

```java
class Solution {
    public boolean canWin(String currentState) {
        for (int i = 0; i < currentState.length() - 1; i++) {
            if (currentState.charAt(i) == '+' && currentState.charAt(i + 1) == '+') {
                String next =
                    currentState.substring(0, i) + "--" + currentState.substring(i + 2);

                if (!canWin(next)) {
                    return true;
                }
            }
        }
        return false;
    }
}
```

---

## Complexity Analysis

### Time Complexity

This is exponential.

In the worst case, each state branches into several next states, and the same states are recomputed many times.

A rough bound is:

```text
O(b^d)
```

where:

- `b` is branching factor
- `d` is game depth

In practice this becomes very slow.

### Space Complexity

Recursion depth is at most the number of moves, which is `O(n)`.

So stack space is:

```text
O(n)
```

---

## Verdict

Correct, but too slow for the full constraint range.

---

# Approach 2: Backtracking + Memoization

## Intuition

The brute-force recursion repeats the same states again and again.

Example:

From different move orders, we may arrive at the same resulting board.

So we memoize:

```text
state -> whether it is winning
```

Then each distinct state is solved once.

This is the standard and best practical solution.

---

## Algorithm

For a state:

1. If it is already in `memo`, return the stored value
2. Try every legal move
3. Build `nextState`
4. If `canWin(nextState)` is `false`, then current state is winning
5. Otherwise, after all moves fail, current state is losing

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private final Map<String, Boolean> memo = new HashMap<>();

    public boolean canWin(String currentState) {
        if (memo.containsKey(currentState)) {
            return memo.get(currentState);
        }

        for (int i = 0; i < currentState.length() - 1; i++) {
            if (currentState.charAt(i) == '+' && currentState.charAt(i + 1) == '+') {
                String next =
                    currentState.substring(0, i) + "--" + currentState.substring(i + 2);

                if (!canWin(next)) {
                    memo.put(currentState, true);
                    return true;
                }
            }
        }

        memo.put(currentState, false);
        return false;
    }
}
```

---

## Complexity Analysis

Let `S` be the number of distinct reachable states.

### Time Complexity

Each state is processed once, and for each state we scan the string for `"++"` positions.

So a useful bound is:

```text
O(S * n)
```

Because `S` can still be exponential in the worst case, the algorithm is still exponential in theory.

However, the input has an important restriction:

> there cannot be more than 20 consecutive `'+'`

This dramatically limits the effective search space, which is why memoization works well here.

### Space Complexity

Memo table stores up to `S` states:

```text
O(S)
```

Recursion stack is `O(n)`.

So total is:

```text
O(S + n)
```

Usually written as:

```text
O(S)
```

---

## Verdict

This is the main interview solution.

---

# Approach 3: Backtracking + Memoization Using Character Array

## Intuition

String concatenation creates many temporary strings.

We can reduce some overhead by converting the string to a mutable character array during recursion:

- flip two positions
- recurse
- flip them back

We still need a string key for memoization, but move generation becomes cleaner and avoids repeated substring assembly for every trial.

This is mostly an implementation optimization of Approach 2.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private final Map<String, Boolean> memo = new HashMap<>();

    public boolean canWin(String currentState) {
        char[] arr = currentState.toCharArray();
        return dfs(arr);
    }

    private boolean dfs(char[] arr) {
        String key = new String(arr);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == '+' && arr[i + 1] == '+') {
                arr[i] = '-';
                arr[i + 1] = '-';

                boolean opponentWins = dfs(arr);

                arr[i] = '+';
                arr[i + 1] = '+';

                if (!opponentWins) {
                    memo.put(key, true);
                    return true;
                }
            }
        }

        memo.put(key, false);
        return false;
    }
}
```

---

## Complexity Analysis

Same asymptotic complexity as Approach 2.

### Time Complexity

```text
O(S * n)
```

### Space Complexity

```text
O(S)
```

---

## Verdict

Useful implementation refinement, but same core idea.

---

# Approach 4: Sprague-Grundy / Game Decomposition by Segments

## Intuition

This is the deeper game-theory view.

A `'-'` breaks the board into independent segments of `'+'`.

Example:

```text
"++--++++-+++"
```

splits into segments of lengths:

```text
2, 4, 3
```

A move inside one segment does not affect the others except by splitting that segment further.

That means the game can be modeled as the XOR of impartial subgames.

For a segment of `L` consecutive pluses, define its Grundy number `g(L)`.

When flipping a pair at positions `i` and `i+1`, the segment of length `L` splits into:

- left segment length `i`
- right segment length `L - i - 2`

So:

```text
g(L) = mex( g(left) XOR g(right) )
```

over all legal flips.

Then the whole state is winning iff the XOR of segment Grundy numbers is non-zero.

This is the most theoretically elegant solution.

---

## Why the Constraint on Consecutive `'+'` Matters

Since no run of `'+'` exceeds 20, we only need Grundy values up to 20.

That makes this approach very feasible.

---

## Algorithm

1. Precompute `grundy[len]` for all `len` from `0` to `20`
2. Parse the input into lengths of consecutive `'+'` segments
3. XOR their Grundy numbers
4. If XOR is non-zero, starting player wins; otherwise loses

---

## Java Code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean canWin(String currentState) {
        int maxLen = 20;
        int[] grundy = new int[maxLen + 1];

        for (int len = 2; len <= maxLen; len++) {
            Set<Integer> seen = new HashSet<>();

            for (int i = 0; i <= len - 2; i++) {
                int left = i;
                int right = len - i - 2;
                seen.add(grundy[left] ^ grundy[right]);
            }

            grundy[len] = mex(seen);
        }

        int xor = 0;
        int run = 0;

        for (int i = 0; i < currentState.length(); i++) {
            if (currentState.charAt(i) == '+') {
                run++;
            } else {
                if (run > 0) {
                    xor ^= grundy[run];
                    run = 0;
                }
            }
        }

        if (run > 0) {
            xor ^= grundy[run];
        }

        return xor != 0;
    }

    private int mex(Set<Integer> set) {
        int x = 0;
        while (set.contains(x)) {
            x++;
        }
        return x;
    }
}
```

---

## Complexity Analysis

Let `n = currentState.length()`, and let `M = 20` be the maximum plus-run length.

### Precomputation Time

For each `len`, we try all flips:

```text
O(M^2)
```

Since `M = 20`, this is tiny.

### State Evaluation Time

Single scan over the string:

```text
O(n)
```

### Total Time

```text
O(n + M^2)
```

With `M = 20`, this is effectively:

```text
O(n)
```

### Space Complexity

Grundy array and temporary sets are tiny:

```text
O(M^2)
```

effectively constant.

---

## Verdict

This is the most advanced solution and gives the cleanest runtime bound under the given constraint.

---

# Why Segment Decomposition Works

A move only affects one consecutive `'+'` block.

If the board has multiple separated blocks, a move in one block does not alter move options in the others.

That is exactly the structure of an impartial combinatorial game that decomposes into independent subgames.

The Sprague-Grundy theorem says:

> The combined game is winning iff the XOR of the Grundy numbers of the components is non-zero.

That is the rigorous reason this optimization works.

---

# Follow-Up: Runtime Complexity Discussion

The problem explicitly asks to derive runtime complexity.

There are two main answers depending on the approach.

## For Recursive Memoization

If `S` is the number of distinct reachable states, then:

```text
Time = O(S * n)
Space = O(S)
```

Since `S` may be exponential, the worst-case theoretical runtime is still exponential.

However, because no run of `'+'` is longer than 20, the actual reachable-state space is far smaller.

## For Grundy Decomposition

Because each plus-segment is at most length 20:

- Grundy values are precomputable up to 20
- then evaluating a position is linear in string length

So runtime is:

```text
O(n)
```

after tiny precomputation.

This is the strongest runtime story.

---

# Common Mistakes

## 1. Forgetting optimal play

This is not “can I make a move?”
It is:

> can I force a win assuming the opponent also plays optimally?

That is why greedy local choices are insufficient.

## 2. Returning true after finding any legal move

A legal move is not enough.

You need a move that leads to an opponent **losing** state.

## 3. Missing memoization

Plain recursion blows up very quickly.

## 4. Not noticing the 20-plus constraint

That detail is what makes the segment/Grundy solution especially attractive.

---

# Final Recommended Solutions

## Best interview-standard solution

Use:

- recursion
- memoization
- winning/losing state logic

It is the most straightforward and expected solution.

## Best theoretical solution under these constraints

Use:

- split into plus segments
- compute Grundy numbers
- XOR them

This gives the best runtime.

---

# Clean Final Java Solution (Memoization Version)

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private final Map<String, Boolean> memo = new HashMap<>();

    public boolean canWin(String currentState) {
        if (memo.containsKey(currentState)) {
            return memo.get(currentState);
        }

        for (int i = 0; i < currentState.length() - 1; i++) {
            if (currentState.charAt(i) == '+' && currentState.charAt(i + 1) == '+') {
                String next =
                    currentState.substring(0, i) + "--" + currentState.substring(i + 2);

                if (!canWin(next)) {
                    memo.put(currentState, true);
                    return true;
                }
            }
        }

        memo.put(currentState, false);
        return false;
    }
}
```

---

# Complexity Summary

## Plain Backtracking

- Time: exponential
- Space: `O(n)`

## Backtracking + Memoization

- Time: `O(S * n)`
- Space: `O(S)`

where `S` is the number of reachable states.

## Grundy / Segment Decomposition

- Time: `O(n)` after tiny precomputation
- Space: effectively constant

---

# Interview Summary

This is a classic impartial game.

A state is winning if it has at least one move to a losing state.

That leads naturally to recursion + memoization.

A deeper optimization comes from splitting the board into independent runs of `'+'` and treating each run as a subgame with a Grundy number. Since no run exceeds length 20, that version becomes extremely efficient.
