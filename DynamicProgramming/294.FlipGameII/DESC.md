from textwrap import dedent

md = dedent("""

# 294. Flip Game II

## Problem Statement

You are playing a **Flip Game** with your friend.

You are given a string `currentState` that contains only the characters:

- `'+'`
- `'-'`

Players take turns performing the following move:

- Choose **two consecutive `"++"`**
- Flip them into `"--"`

Example move:

```
"++++" → "+--+"
```

The game continues until **a player can no longer make a move**.

When a player cannot make a move:

- That player **loses**
- The **other player wins**

Your task is to determine:

> Whether the **starting player can guarantee a win** assuming both players play optimally.

Return:

```
true  → if the starting player can force a win
false → otherwise
```

---

# Examples

## Example 1

Input

```
currentState = "++++"
```

Output

```
true
```

Explanation

The starting player can flip the middle `"++"`:

```
++++
  ↓
+--+
```

Now the opponent has **no valid move** that leads to victory, so the starting player guarantees a win.

---

## Example 2

Input

```
currentState = "+"
```

Output

```
false
```

Explanation

There are **no `"++"` pairs**, so the starting player cannot make a move and immediately loses.

---

# Constraints

```
1 <= currentState.length <= 60
currentState[i] ∈ {'+', '-'}
There cannot be more than 20 consecutive '+'
```

---

# Key Idea

This problem is a **game theory problem**.

We must determine if the **current player has a winning move**.

A move is winning if:

```
there exists a move that forces the opponent into a losing position
```

This naturally leads to a **recursive search with memoization**.

---

# Observations

At every turn:

1. Scan the string for `"++"`
2. Replace it with `"--"`
3. Check whether the opponent loses from that new state

If **any move causes the opponent to lose**, the current player wins.

This is known as the **Minimax principle**.

---

# Approach: Backtracking + Memoization

## Intuition

We recursively explore all valid moves.

For each `"++"` we:

1. Flip it to `"--"`
2. Recursively check if the opponent loses

If the opponent loses for **any move**, we win.

To avoid repeated calculations, we **memoize previously computed states**.

---

# Algorithm

1. Use a hash map `memo` storing:

```
state → true/false
```

2. For a given state:
   - If already in `memo`, return the stored result

3. Try every possible `"++"` flip

4. Generate next state

5. Recursively check opponent outcome

6. If opponent loses → store `true` and return

7. If no winning move exists → store `false`

---

# Java Implementation

```java
class Solution {

    private Map<String, Boolean> memo = new HashMap<>();

    public boolean canWin(String currentState) {

        if (memo.containsKey(currentState)) {
            return memo.get(currentState);
        }

        for (int i = 0; i < currentState.length() - 1; i++) {

            if (currentState.charAt(i) == '+' &&
                currentState.charAt(i + 1) == '+') {

                String nextState =
                        currentState.substring(0, i)
                        + "--"
                        + currentState.substring(i + 2);

                if (!canWin(nextState)) {
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

# Complexity Analysis

Let:

```
n = length of currentState
```

## Time Complexity

Worst case:

```
O(2^n)
```

Reason:

Each move reduces `"++"` pairs but there can still be an exponential number of game states.

Memoization significantly reduces repeated work but the theoretical upper bound remains exponential.

---

## Space Complexity

```
O(2^n)
```

because the memoization table may store many game states.

Additionally recursion stack depth is:

```
O(n)
```

---

# Follow-Up: Runtime Analysis

The main cost comes from exploring game states.

Each state represents a **different configuration of `+` and `-`**.

Because each `"++"` flip reduces available moves, the search tree depth is bounded.

Memoization ensures that **each state is evaluated once**.

Thus the practical runtime is much smaller than the naive exponential bound.

---

# Key Takeaways

- This is a **combinatorial game problem**
- The optimal strategy uses **recursion + memoization**
- A player wins if they can move the opponent into a **losing state**
- The problem demonstrates a classic **game state DP pattern**
  """)

path = "/mnt/data/294_flip_game_ii_problem.md"

with open(path, "w") as f:
f.write(md)

print(path)
