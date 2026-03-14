# 464. Can I Win

## Problem Statement

In the classic **100 game**, two players take turns adding a number to a running total.
In this variation:

- Players may choose integers from `1` to `maxChoosableInteger`
- Each integer can be used **at most once**
- The player who first makes the running total **reach or exceed** `desiredTotal` wins

Both players play optimally.

We need to determine whether the **first player** can force a win.

---

## Java Function Signature

```java
class Solution {
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {

    }
}
```

---

## Examples

### Example 1

```text
Input: maxChoosableInteger = 10, desiredTotal = 11
Output: false
```

**Why?**

Whatever number the first player picks, the second player can respond with a number that makes the total at least `11`.

- First picks `1`, second picks `10`
- First picks `2`, second picks `9`
- ...
- First picks `10`, second picks `1`

So the first player loses with optimal play.

---

### Example 2

```text
Input: maxChoosableInteger = 10, desiredTotal = 0
Output: true
```

If the target is already `0`, the first player trivially wins without making any move.

---

### Example 3

```text
Input: maxChoosableInteger = 10, desiredTotal = 1
Output: true
```

The first player picks `1` and wins immediately.

---

## Constraints

```text
1 <= maxChoosableInteger <= 20
0 <= desiredTotal <= 300
```

---

# Core Game Theory Insight

This is a **two-player impartial game with perfect information**.

That usually suggests:

- try all possible moves
- assume the opponent also plays optimally
- determine whether there exists **at least one move** that forces the opponent into a losing state

This is the standard **minimax / DFS with memoization** pattern.

But before that, there are two very important observations.

---

## Observation 1: Immediate trivial win

If:

```text
desiredTotal <= 0
```

then the first player already wins.

---

## Observation 2: Total sum feasibility check

The largest total sum obtainable from all numbers `1..maxChoosableInteger` is:

```text
1 + 2 + ... + maxChoosableInteger
= maxChoosableInteger * (maxChoosableInteger + 1) / 2
```

If this sum is still smaller than `desiredTotal`, then even if we use **all numbers**, nobody can ever reach the target.

So:

```text
if totalSum < desiredTotal => return false
```

This pruning is essential.

---

# Approach 1: Plain Recursive Minimax

## Intuition

At any game state, the current player can choose any unused number.

A state is winning if:

- the player can pick a number that reaches the target immediately, or
- the player can move to a state where the opponent loses

A state is losing if **every possible move** lets the opponent win.

This is the most direct formulation.

---

## State Representation

We need to know:

- which numbers are already used
- how much total we still need to reach

A simple version stores a `boolean[] used`.

---

## Recursive Logic

Suppose we are at state `used`, and still need `remaining`.

For each unused number `i`:

- if `i >= remaining`, current player wins immediately
- otherwise mark `i` as used and recurse
- if the recursive result says the opponent loses, current player wins

If no move works, current player loses.

---

## Java Code

```java
class SolutionBruteForce {
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;

        int totalSum = maxChoosableInteger * (maxChoosableInteger + 1) / 2;
        if (totalSum < desiredTotal) return false;

        boolean[] used = new boolean[maxChoosableInteger + 1];
        return dfs(used, desiredTotal, maxChoosableInteger);
    }

    private boolean dfs(boolean[] used, int remaining, int maxChoosableInteger) {
        for (int pick = 1; pick <= maxChoosableInteger; pick++) {
            if (used[pick]) continue;

            if (pick >= remaining) {
                return true;
            }

            used[pick] = true;
            boolean opponentCanWin = dfs(used, remaining - pick, maxChoosableInteger);
            used[pick] = false;

            if (!opponentCanWin) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Complexity

This is extremely expensive.

In the worst case, we explore permutations of picks.

Roughly:

```text
Time:  O(n!)
Space: O(n)
```

where `n = maxChoosableInteger`.

This is too slow for `n = 20`.

---

## Problem With This Approach

The same game state is recomputed many times.

For example, these two pick orders:

- `1 -> 4`
- `4 -> 1`

lead to the same set of used numbers and the same remaining target.

That means there is huge overlap between subproblems.

So we need memoization.

---

# Approach 2: DFS + Memoization Using String / Array Encoding

## Intuition

The result of a state depends only on:

- which numbers are already used
- equivalently, the current subset of picked numbers

Once that subset is fixed, the remaining target is also fixed, because the sum of used numbers is fixed.

So we can memoize by the set of used numbers.

A simple but not ideal way is to serialize `used[]` into a string key.

---

## Why This Works

A game state is fully determined by the chosen subset.
If we reach the same subset again from another move order, the answer must be identical.

Memoization avoids recomputing it.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class SolutionMemoString {
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;

        int totalSum = maxChoosableInteger * (maxChoosableInteger + 1) / 2;
        if (totalSum < desiredTotal) return false;

        boolean[] used = new boolean[maxChoosableInteger + 1];
        Map<String, Boolean> memo = new HashMap<>();
        return dfs(used, desiredTotal, maxChoosableInteger, memo);
    }

    private boolean dfs(boolean[] used, int remaining, int maxChoosableInteger,
                        Map<String, Boolean> memo) {
        String key = encode(used);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        for (int pick = 1; pick <= maxChoosableInteger; pick++) {
            if (used[pick]) continue;

            if (pick >= remaining) {
                memo.put(key, true);
                return true;
            }

            used[pick] = true;
            boolean opponentCanWin = dfs(used, remaining - pick, maxChoosableInteger, memo);
            used[pick] = false;

            if (!opponentCanWin) {
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    private String encode(boolean[] used) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < used.length; i++) {
            sb.append(used[i] ? '1' : '0');
        }
        return sb.toString();
    }
}
```

---

## Complexity

There are at most:

```text
2^n
```

distinct subsets.

For each state, we may try up to `n` choices.

So:

```text
Time:  O(n * 2^n)
Space: O(2^n)
```

This is much better.

However, the string encoding introduces extra overhead.

We can do better with a bitmask.

---

# Approach 3: DFS + Memoization with Bitmask (Optimal)

## Intuition

Since:

```text
maxChoosableInteger <= 20
```

we can represent the used numbers with a single integer bitmask.

For example, if `maxChoosableInteger = 5`:

- bit `0` -> number `1`
- bit `1` -> number `2`
- bit `2` -> number `3`
- bit `3` -> number `4`
- bit `4` -> number `5`

If bit `i` is `1`, it means number `i + 1` is already used.

This gives a compact and fast representation of state.

---

## Why Bitmask Is Ideal Here

With `n <= 20`, the total number of subsets is at most:

```text
2^20 = 1,048,576
```

That is manageable.

Also:

- checking whether a number is used is `O(1)`
- marking it as used is `O(1)`
- memo keys become integers instead of strings

This is the standard optimal solution.

---

## State Definition

Let:

```text
dfs(mask, remaining)
```

mean:

> Can the current player force a win if the used numbers are represented by `mask`, and the player still needs `remaining` to win?

But actually, since `remaining` is determined by `mask`, we can memoize only on `mask`.

---

## Transition

For each number `pick` from `1` to `maxChoosableInteger`:

- if not used in `mask`
  - if `pick >= remaining`, current player wins
  - otherwise recurse on the next state with that bit set
  - if the recursive call returns `false`, it means opponent loses, so current player wins

If no move leads to a win, return `false`.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;

        int totalSum = maxChoosableInteger * (maxChoosableInteger + 1) / 2;
        if (totalSum < desiredTotal) return false;

        Map<Integer, Boolean> memo = new HashMap<>();
        return dfs(0, desiredTotal, maxChoosableInteger, memo);
    }

    private boolean dfs(int mask, int remaining, int maxChoosableInteger,
                        Map<Integer, Boolean> memo) {
        if (memo.containsKey(mask)) {
            return memo.get(mask);
        }

        for (int pick = 1; pick <= maxChoosableInteger; pick++) {
            int bit = 1 << (pick - 1);

            if ((mask & bit) != 0) {
                continue;
            }

            if (pick >= remaining) {
                memo.put(mask, true);
                return true;
            }

            boolean opponentCanWin = dfs(mask | bit, remaining - pick,
                                         maxChoosableInteger, memo);

            if (!opponentCanWin) {
                memo.put(mask, true);
                return true;
            }
        }

        memo.put(mask, false);
        return false;
    }
}
```

---

## Complexity

There are at most `2^n` masks, and for each mask we try up to `n` numbers.

So:

```text
Time:  O(n * 2^n)
Space: O(2^n)
```

This is the accepted optimal complexity for this constraint range.

---

# Approach 4: DFS + Memoization with Integer Array Instead of HashMap

## Intuition

Since the number of masks is bounded by `2^20`, we can replace the `HashMap<Integer, Boolean>` with an integer array.

This is usually slightly faster than a hash map because array access is cheaper.

We can store:

- `0` = uncomputed
- `1` = losing state
- `2` = winning state

This avoids boxing/unboxing and hashing overhead.

---

## Java Code

```java
class SolutionArrayMemo {
    private int[] memo;
    private int n;

    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;

        int totalSum = maxChoosableInteger * (maxChoosableInteger + 1) / 2;
        if (totalSum < desiredTotal) return false;

        this.n = maxChoosableInteger;
        this.memo = new int[1 << n];

        return dfs(0, desiredTotal);
    }

    private boolean dfs(int mask, int remaining) {
        if (memo[mask] != 0) {
            return memo[mask] == 2;
        }

        for (int pick = 1; pick <= n; pick++) {
            int bit = 1 << (pick - 1);

            if ((mask & bit) != 0) continue;

            if (pick >= remaining) {
                memo[mask] = 2;
                return true;
            }

            if (!dfs(mask | bit, remaining - pick)) {
                memo[mask] = 2;
                return true;
            }
        }

        memo[mask] = 1;
        return false;
    }
}
```

---

## Complexity

Same asymptotic complexity:

```text
Time:  O(n * 2^n)
Space: O(2^n)
```

But practically a bit faster than the map-based version.

---

# Can We Do Bottom-Up DP?

In principle, yes.

You could define a DP over all masks and determine whether each mask is winning or losing.
But this is awkward because transitions naturally go from a state to its supersets, and the recursive relation is much cleaner.

For this problem, **top-down DFS with memoization** is the best fit.

So while bottom-up is possible, it is not the most natural or most interview-friendly solution.

---

# Deep Reasoning About Why Memoization by Mask Alone Is Enough

A skeptical question is:

> Why can memo use only `mask` and not `remaining`?

Because `remaining` is fully determined by `mask`.

Suppose numbers used in `mask` sum to `usedSum`. Then:

```text
remaining = desiredTotal - usedSum
```

Since `desiredTotal` is fixed for the whole game, every mask always corresponds to exactly one remaining value.

So two recursive calls with the same `mask` can never disagree about `remaining`.

That is why memoizing solely by `mask` is correct.

---

# Dry Run

Consider:

```text
maxChoosableInteger = 4
desiredTotal = 6
```

Numbers available: `{1, 2, 3, 4}`

Total sum is:

```text
1 + 2 + 3 + 4 = 10
```

So winning is at least possible.

Start:

```text
mask = 0000
remaining = 6
```

### Try pick = 1

Now opponent sees:

```text
mask = 0001
remaining = 5
```

Opponent can pick `4` and win immediately.

So pick `1` is bad.

### Try pick = 2

Opponent sees:

```text
mask = 0010
remaining = 4
```

Opponent can pick `4` and win immediately.

Bad.

### Try pick = 3

Opponent sees:

```text
mask = 0100
remaining = 3
```

Opponent can pick `3`? No, already used.
But opponent can pick `4`, which is >= 3, so opponent wins.

Bad.

### Try pick = 4

Opponent sees:

```text
mask = 1000
remaining = 2
```

Opponent can pick `2` and win immediately.

Bad.

So all moves fail.

Answer:

```text
false
```

---

# Common Mistakes

## Mistake 1: Forgetting the total-sum impossibility check

If:

```text
1 + 2 + ... + maxChoosableInteger < desiredTotal
```

then the answer must be `false`.

Without this check, the recursion wastes time.

---

## Mistake 2: Memoizing by current total instead of used set

Different sets of used numbers can have the same current total but allow different future choices.

Example:

- used `{1, 4}` total = `5`
- used `{2, 3}` total = `5`

These are not equivalent states, because the remaining available numbers differ.

So memoizing only by total is incorrect.

---

## Mistake 3: Using a mutable global structure without proper backtracking

If you mark a number used and forget to unmark it after recursion, the state becomes corrupted.

Always backtrack:

```java
used[pick] = true;
...
used[pick] = false;
```

With bitmask this problem disappears because integers are immutable in recursive calls.

---

## Mistake 4: Not noticing immediate win moves

If:

```text
pick >= remaining
```

you should immediately return `true`.

No need to recurse further.

---

## Mistake 5: Thinking greedy works

Picking the largest available number is not always optimal.

This is a strategic game, not a simple maximization problem.
Sometimes a smaller pick creates a losing state for the opponent, while a large pick does not.

So pure greedy is not reliable.

---

# Comparison of Approaches

| Approach                    | Idea                               |                        Time |    Space | Verdict                |
| --------------------------- | ---------------------------------- | --------------------------: | -------: | ---------------------- |
| Plain recursion             | Explore all move sequences         | Very large / factorial-like |   `O(n)` | Too slow               |
| Memo with string key        | Cache states by encoded used array |                `O(n * 2^n)` | `O(2^n)` | Works, but slower      |
| Memo with bitmask + HashMap | Compact state + DFS                |                `O(n * 2^n)` | `O(2^n)` | Standard optimal       |
| Memo with bitmask + array   | Same as above, faster constants    |                `O(n * 2^n)` | `O(2^n)` | Best practical version |

---

# Interview Explanation Version

A clean interview explanation would sound like this:

1. This is a two-player optimal-play game, so I model it with minimax.
2. A state is determined by which numbers have already been used.
3. From a state, I try every unused number.
4. If I can pick a number that reaches the target immediately, I win.
5. Otherwise, I recurse. If any move makes the opponent lose, then the current state is winning.
6. Since `maxChoosableInteger <= 20`, I can represent the used set as a bitmask.
7. There are at most `2^20` states, so DFS + memoization is efficient enough.
8. I also add a pruning check: if the sum of all numbers is smaller than `desiredTotal`, the answer is immediately false.

That is the full idea.

---

# Final Recommended Java Solution

```java
class Solution {
    private int[] memo;
    private int n;

    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;

        int totalSum = maxChoosableInteger * (maxChoosableInteger + 1) / 2;
        if (totalSum < desiredTotal) return false;

        this.n = maxChoosableInteger;
        this.memo = new int[1 << n]; // 0 = unknown, 1 = lose, 2 = win

        return dfs(0, desiredTotal);
    }

    private boolean dfs(int mask, int remaining) {
        if (memo[mask] != 0) {
            return memo[mask] == 2;
        }

        for (int pick = 1; pick <= n; pick++) {
            int bit = 1 << (pick - 1);

            if ((mask & bit) != 0) continue;

            if (pick >= remaining) {
                memo[mask] = 2;
                return true;
            }

            if (!dfs(mask | bit, remaining - pick)) {
                memo[mask] = 2;
                return true;
            }
        }

        memo[mask] = 1;
        return false;
    }
}
```

---

# Why This Solution Is Correct

For every state:

- it tries all legal moves
- it returns `true` iff at least one move leads to an opponent-losing state
- it returns `false` iff every move leads to an opponent-winning state

That is exactly the minimax definition of a winning state.

Memoization ensures each distinct subset of used numbers is solved once.

So the algorithm is both correct and efficient.

---

# Final Takeaway

The key leap is this:

> The game is not about the running total alone.
> It is about the **subset of numbers still available**.

Once you see that, the problem becomes a classic:

- game state compression
- DFS
- memoization
- bitmask DP

This is the right mental model for many “pick without replacement” game problems.
