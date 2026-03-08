# Nim Game — Detailed Summary

## Problem

There are `n` piles of stones.

Two players, **Alice** and **Bob**, take turns, with **Alice starting first**.

On each turn, a player:

- chooses **one non-empty pile**
- removes **one or more stones** from that pile

The player who **cannot make a move loses**.
Both players play **optimally**.

We need to return:

- `true` if **Alice** wins
- `false` if **Bob** wins

---

# Big Picture

This is the classic **Nim Game**.

There are two major ways to think about it:

1. **Simulation + Dynamic Programming**
   - More intuitive
   - Easier to derive in an interview
   - Much less efficient

2. **Mathematical / Bit Manipulation**
   - Much more efficient
   - Very easy to implement
   - Harder to discover from scratch

---

# Approach 1: Simulation + Dynamic Programming

## Core intuition

This is a finite, deterministic, perfect-information game.

That gives us three crucial facts:

1. **The game must end**
   - Every move removes at least one stone
   - Total stones strictly decrease
   - So the number of moves is finite

2. **There are no draws**
   - Either the current player eventually wins
   - Or the current player eventually loses

3. **Optimal play determines the answer**
   - Players do not make mistakes
   - So from a given state, the result is fixed

---

## Winning and losing state logic

A game state is:

- **winning** if there exists **at least one move** that makes the opponent lose
- **losing** if **every possible move** makes the opponent win
- also **losing** if there are **no legal moves**

That is the standard recursive game-DP idea.

---

## State definition

A state is completely described by the sizes of all piles.

Example:

```text
[1, 2, 1]
```

means:

- pile 1 has 1 stone
- pile 2 has 2 stones
- pile 3 has 1 stone

From `[1, 2, 1]`, these moves are possible:

- remove 1 from pile 1 → `[0, 2, 1]`
- remove 1 from pile 2 → `[1, 1, 1]`
- remove 2 from pile 2 → `[1, 0, 1]`
- remove 1 from pile 3 → `[1, 2, 0]`

So recursion naturally fits.

---

## Recursive formulation

Let:

```text
isWinner(state) = whether the current player can force a win from this state
```

Then:

```text
isWinner(state):
    if all piles are zero:
        return false

    for each possible nextState:
        if isWinner(nextState) == false:
            return true

    return false
```

Meaning:

- if there is some move after which the opponent loses, current player wins
- otherwise current player loses

---

## Why memoization is needed

Without memoization, the recursion tree explodes because the same game state appears many times.

For example, the same state may be reached through different move sequences.

So we store previously computed answers in a hash map:

- **key** = representation of pile state
- **value** = whether that state is winning or losing

This avoids recomputing subproblems.

---

## Important optimization: treat equivalent permutations as the same state

The order of piles does **not** matter.

These states are equivalent:

```text
[1, 2, 2]
[2, 1, 2]
[2, 2, 1]
```

All describe the same game situation.

So before memoizing or recursing, sort the piles.
That way, equivalent states share the same memo entry.

This is a major reduction in repeated work.

---

## Java implementation — Simulation + Memoization

```java
import java.util.*;

class Solution {
    public boolean nimGame(int[] piles) {
        int remaining = 0;
        for (int x : piles) remaining += x;

        Arrays.sort(piles);
        Map<String, Boolean> memo = new HashMap<>();

        return isNextPersonWinner(piles, remaining, memo);
    }

    private boolean isNextPersonWinner(int[] piles, int remaining, Map<String, Boolean> memo) {
        String key = getKey(piles);

        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        if (remaining == 0) {
            return false;
        }

        for (int i = 0; i < piles.length; i++) {
            if (piles[i] == 0) continue;

            for (int remove = 1; remove <= piles[i]; remove++) {
                piles[i] -= remove;

                int[] nextState = piles.clone();
                Arrays.sort(nextState);

                if (!isNextPersonWinner(nextState, remaining - remove, memo)) {
                    memo.put(key, true);
                    piles[i] += remove;
                    return true;
                }

                piles[i] += remove;
            }
        }

        memo.put(key, false);
        return false;
    }

    private String getKey(int[] piles) {
        StringBuilder sb = new StringBuilder();
        for (int x : piles) {
            sb.append(x).append('#');
        }
        return sb.toString();
    }
}
```

---

## Dry run intuition for DP approach

Suppose:

```text
piles = [1, 2, 1]
```

Sorted state:

```text
[1, 1, 2]
```

Try every move:

- remove from the first `1`
- remove from the second `1`
- remove `1` or `2` from the pile `2`

For each resulting state, recursively ask:

> “If I move here, can the next player win?”

- If for some child state the answer is **false**, then current state is **winning**
- If for every child state the answer is **true**, then current state is **losing**

That exactly matches optimal play.

---

## Complexity of DP approach

Let:

- `n` = number of piles
- `m` = maximum stones in any pile

### Time complexity

A careful upper bound from the provided explanation is:

```text
O(n^2 * m * C(n+m-1, n) * log n)
```

### Why?

There are three main components:

1. **Number of distinct normalized states**
   - Since piles are sorted and repetitions are allowed, the number of states is like choosing `n` objects from `m+1` values with repetition
   - This gives approximately:

```text
C(n+m-1, n)
```

2. **Transitions per state**
   - For each of `n` piles, up to `m` removals
   - So at most:

```text
O(n * m)
```

3. **Sorting next state**
   - Each transition sorts an array of length `n`

```text
O(n log n)
```

Multiplying them gives:

```text
O(C(n+m-1, n) * n * m * n log n)
= O(n^2 * m * C(n+m-1, n) * log n)
```

### Space complexity

The explanation gives:

```text
O(n * C(n+m-1, n))
```

Reason:

- the memo table stores all distinct states
- each key stores `n` numbers

The recursion stack and temporary arrays also use space, but the memo dominates.

---

# Approach 2: Mathematical / Bit Manipulation

This is the classical Nim solution.

## Key concept: nim-sum

Take XOR of all pile sizes:

```text
nimSum = piles[0] ^ piles[1] ^ ... ^ piles[n-1]
```

This XOR value completely determines whether the current player is winning or losing.

---

## Final rule

- if `nimSum == 0` → current player is in a **losing** state
- if `nimSum != 0` → current player is in a **winning** state

Since Alice starts first:

- **Alice wins iff XOR of all piles is non-zero**

---

## Java implementation — Optimal solution

```java
class Solution {
    public boolean nimGame(int[] piles) {
        int nimSum = 0;
        for (int p : piles) {
            nimSum ^= p;
        }
        return nimSum != 0;
    }
}
```

---

## Example

Suppose:

```text
piles = [3, 2, 5]
```

Binary:

```text
3 = 011
2 = 010
5 = 101
```

XOR:

```text
011 ^ 010 ^ 101 = 100
```

So:

```text
nimSum = 4
```

Since `nimSum != 0`, Alice is in a winning state.

---

# Why XOR works

This is the heart of Nim theory.

The idea is:

- a state with XOR `0` is a **losing** state
- a state with XOR non-zero is a **winning** state

And optimal play is:

> From a non-zero XOR state, move to a zero XOR state.

Then the opponent is always forced back into a non-zero XOR state.

Eventually, the player who keeps handing over zero-XOR states wins.

---

# Theorem

## Winning strategy in Nim

Finish every move with a state whose XOR of pile sizes is `0`.

---

# Proof structure

There are two key lemmas.

---

## Lemma 1: From a zero nim-sum state, every legal move leads to a non-zero nim-sum state

Suppose the piles are:

```text
n1, n2, ..., ni, ..., nk
```

and:

```text
n1 ^ n2 ^ ... ^ ni ^ ... ^ nk = 0
```

A player removes `x > 0` stones from pile `i`, making it `ni - x`.

New XOR becomes:

```text
s = n1 ^ n2 ^ ... ^ (ni - x) ^ ... ^ nk
```

Since original XOR is zero, we have:

```text
n1 ^ n2 ^ ... ^ nk = 0
=> n1 ^ n2 ^ ... ^ n(i-1) ^ n(i+1) ^ ... ^ nk = ni
```

So:

```text
s = ni ^ (ni - x)
```

Now XOR of two numbers is zero only if the two numbers are equal.

But:

```text
ni != ni - x
```

because `x > 0`.

Therefore:

```text
s != 0
```

So from a zero-XOR state, every move goes to a non-zero-XOR state.

That means zero-XOR states are losing.

---

## Lemma 2: From any non-zero nim-sum state, there exists a move to a zero nim-sum state

Suppose total XOR is:

```text
s = n1 ^ n2 ^ ... ^ nk
```

and `s != 0`.

Take the most significant set bit in `s`.

At least one pile `ni` must also have that bit set, otherwise that bit could not appear in the XOR.

Now define:

```text
y = s ^ ni
```

Because `ni` and `s` share the highest set bit, `y < ni`.

So we can legally reduce pile `ni` to `y` by removing:

```text
ni - y
```

stones.

Now new XOR is:

```text
n1 ^ ... ^ y ^ ... ^ nk
= n1 ^ ... ^ (s ^ ni) ^ ... ^ nk
```

Rearranging:

```text
= (n1 ^ ... ^ ni ^ ... ^ nk) ^ s
= s ^ s
= 0
```

So from every non-zero-XOR state, there is at least one move to XOR zero.

That means non-zero-XOR states are winning.

---

# Example for Lemma 2

Take:

```text
piles = [2, 3, 4]
```

Binary:

```text
2 = 010
3 = 011
4 = 100
```

XOR:

```text
010 ^ 011 ^ 100 = 101
```

So:

```text
nimSum = 5
```

The highest set bit is the `4` bit.

Choose pile `4` because it has that bit set.

Now compute:

```text
y = 5 ^ 4 = 1
```

Reduce pile `4` to `1` by removing `3` stones.

New piles:

```text
[2, 3, 1]
```

XOR:

```text
2 ^ 3 ^ 1 = 0
```

Exactly as required.

---

# Strategic interpretation

If Alice starts in a non-zero-XOR position, she can always make one move that gives Bob a zero-XOR state.

Then whatever Bob does:

- he must produce a non-zero-XOR state
- Alice restores zero-XOR again

This continues until Bob is eventually forced into the terminal losing situation.

If Alice starts in a zero-XOR state, she cannot keep the invariant. Any move she makes gives Bob a non-zero-XOR position, and then Bob can begin the zero-XOR strategy.

---

# Complexity of XOR approach

Let `n` be the number of piles.

## Time complexity

```text
O(n)
```

Reason:

- scan all piles once
- each XOR is `O(1)`

## Space complexity

```text
O(1)
```

Reason:

- only one accumulator variable is needed

---

# Side-by-side comparison

## Approach 1: Simulation + DP

### Pros

- natural and intuitive
- matches general recursive game reasoning
- useful in interviews when deriving logic from first principles

### Cons

- very expensive
- state explosion
- implementation is much longer
- requires memoization and normalization

---

## Approach 2: XOR / Nim theorem

### Pros

- optimal
- tiny implementation
- mathematically elegant
- linear time and constant space

### Cons

- hard to invent on the spot unless you already know Nim theory

---

# Recommended interview framing

A strong practical way to explain this in an interview is:

1. Start with the recursive game-state idea
   - winning state if any move makes opponent lose
   - losing state if all moves make opponent win

2. Mention memoization and state normalization
   - sort piles so equivalent states collapse together

3. Then say:
   - “This is actually the classical Nim Game”
   - “There is a known theorem: the first player wins iff XOR of all piles is non-zero”

That shows both problem-solving ability and awareness of the optimal theorem.

---

# Final takeaway

The entire game reduces to one check:

```java
class Solution {
    public boolean nimGame(int[] piles) {
        int nimSum = 0;
        for (int p : piles) {
            nimSum ^= p;
        }
        return nimSum != 0;
    }
}
```

## Final result

- **XOR = 0** → Bob wins
- **XOR != 0** → Alice wins

That is the complete optimal rule for Nim.
