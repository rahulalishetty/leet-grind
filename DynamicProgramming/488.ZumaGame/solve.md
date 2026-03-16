# 488. Zuma Game — Exhaustive Solution Notes

## Overview

This is a small-constraints search problem with a deceptively large branching factor.

We are given:

- a `board` string representing the current row of balls
- a `hand` string representing balls we can insert

At each move:

1. choose one ball from the hand
2. insert it at any position on the board
3. repeatedly remove every group of 3 or more equal consecutive balls
4. continue until no more removals are possible

We want the **minimum number of insertions** needed to clear the board.
If it is impossible, return `-1`.

Because:

- `board.length <= 16`
- `hand.length <= 5`

the intended solutions are based on **search**:

1. **DFS / Backtracking + Memoization**
2. **BFS on game states**
3. **Optimized DFS using hand counts and pruning**

The third one is the most practical and elegant.

---

## Problem Statement

You are playing a variation of the game Zuma.

The board is a row of colored balls. Each ball may be one of:

```text
R, Y, B, G, W
```

You also have some balls in your hand.

On each turn:

1. pick one ball from your hand
2. insert it anywhere in the board
3. if a group of 3 or more consecutive balls of the same color appears, remove it
4. keep removing groups formed by chain reactions
5. continue until the board is stable

Return the minimum number of balls you need to insert to clear the board.
If it is impossible, return `-1`.

---

## Example 1

**Input**

```text
board = "WRRBBW"
hand = "RB"
```

**Output**

```text
-1
```

**Explanation**

Best attempt:

```text
Insert 'R' -> WRRRBBW -> WBBW
Insert 'B' -> WBBBW -> WW
```

The board is not empty and the hand is exhausted.

So the answer is:

```text
-1
```

---

## Example 2

**Input**

```text
board = "WWRRBBWW"
hand = "WRBRW"
```

**Output**

```text
2
```

**Explanation**

One optimal sequence:

```text
Insert 'R' -> WWRRRBBWW -> WWBBWW
Insert 'B' -> WWBBBWW -> WWWW -> empty
```

So the minimum number of insertions is:

```text
2
```

---

## Example 3

**Input**

```text
board = "G"
hand = "GGGGG"
```

**Output**

```text
2
```

**Explanation**

```text
Insert 'G' -> GG
Insert 'G' -> GGG -> empty
```

So the answer is:

```text
2
```

---

## Constraints

- `1 <= board.length <= 16`
- `1 <= hand.length <= 5`
- `board` and `hand` contain only `R`, `Y`, `B`, `G`, `W`
- the initial board will not already contain groups of length 3 or more

---

# Key Insights

This problem is fundamentally a **state-space search** problem.

A state is defined by:

- the current board
- the remaining hand

From each state, we try all legal insertions and move to new states.

The main challenges are:

1. many insertions are redundant
2. chain reactions must be simulated correctly
3. repeated states must be avoided
4. we must minimize the number of inserted balls

That points naturally to:

- DFS + memoization, or
- BFS, since every move costs 1

---

# Important Helper: Board Reduction

Every serious solution needs a helper function that repeatedly removes consecutive groups of 3 or more equal balls.

Example:

```text
RRBBBRR
```

After removing `BBB`:

```text
RRRR
```

Then `RRRR` also disappears:

```text
empty
```

So reduction must continue until the board is stable.

---

## A Standard Reduction Function

A common implementation repeatedly scans for runs of length >= 3 and removes them until no more changes happen.

Pseudo-logic:

```text
shrink(board):
    repeat:
        scan board for consecutive groups
        if any group length >= 3:
            remove it
            restart scan
    until no changes
    return board
```

This helper is used in all approaches.

---

# Approach 1: BFS on States

## Intuition

Each move inserts exactly one ball.

So if we use BFS over states, then the first time we reach an empty board, we are guaranteed to have used the minimum number of insertions.

That makes BFS conceptually very appealing.

Each BFS node is:

```text
(board, remaining_hand)
```

For each node:

- try inserting each ball from the hand
- try all insertion positions
- reduce the board after insertion
- enqueue unseen states

The first empty board found gives the answer.

---

## Why BFS Works

BFS explores all states using:

- 0 insertions
- then 1 insertion
- then 2 insertions
- etc.

Since all moves have equal cost `1`, BFS naturally finds the shortest path.

---

## State Representation

A state may be represented as:

```text
board + "#" + sorted/encoded hand
```

or more explicitly with a class storing:

- `String board`
- `String hand`

To avoid duplicates, use a `HashSet<String>` for visited states.

---

## Important Pruning Ideas for BFS

Naively trying every position for every ball produces many duplicates.

Useful pruning rules:

1. **Skip duplicate hand colors in the same layer**
   If the hand has repeated identical balls, inserting the first or second identical copy produces equivalent choices.

2. **Skip symmetric insertions**
   Inserting the same color into equivalent neighboring positions often produces the same result.

3. **Prefer meaningful insertions**
   An insertion is most useful when:
   - it matches a nearby board color, or
   - it bridges two equal colors

These prunings are optional for correctness but helpful for efficiency.

---

## Java Implementation — BFS

```java
import java.util.*;

class Solution {
    static class State {
        String board;
        String hand;

        State(String board, String hand) {
            this.board = board;
            this.hand = hand;
        }
    }

    public int findMinStep(String board, String hand) {
        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        char[] chars = hand.toCharArray();
        Arrays.sort(chars);
        hand = new String(chars);

        queue.offer(new State(board, hand));
        visited.add(board + "#" + hand);

        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int s = 0; s < size; s++) {
                State cur = queue.poll();

                if (cur.board.length() == 0) {
                    return steps;
                }

                for (int i = 0; i < cur.hand.length(); i++) {
                    if (i > 0 && cur.hand.charAt(i) == cur.hand.charAt(i - 1)) {
                        continue;
                    }

                    char ball = cur.hand.charAt(i);
                    String nextHand = cur.hand.substring(0, i) + cur.hand.substring(i + 1);

                    for (int pos = 0; pos <= cur.board.length(); pos++) {
                        if (!isUsefulInsertion(cur.board, pos, ball)) {
                            continue;
                        }

                        String nextBoard = cur.board.substring(0, pos) + ball + cur.board.substring(pos);
                        nextBoard = shrink(nextBoard);

                        String key = nextBoard + "#" + nextHand;
                        if (visited.add(key)) {
                            queue.offer(new State(nextBoard, nextHand));
                        }
                    }
                }
            }

            steps++;
        }

        return -1;
    }

    private boolean isUsefulInsertion(String board, int pos, char ball) {
        boolean leftMatch = pos > 0 && board.charAt(pos - 1) == ball;
        boolean rightMatch = pos < board.length() && board.charAt(pos) == ball;
        boolean bridge = pos > 0 && pos < board.length() && board.charAt(pos - 1) == board.charAt(pos)
                && board.charAt(pos - 1) != ball;

        return leftMatch || rightMatch || bridge;
    }

    private String shrink(String board) {
        boolean changed = true;

        while (changed) {
            changed = false;
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < board.length()) {
                int j = i;
                while (j < board.length() && board.charAt(j) == board.charAt(i)) {
                    j++;
                }

                if (j - i >= 3) {
                    changed = true;
                } else {
                    sb.append(board, i, j);
                }

                i = j;
            }

            board = sb.toString();
        }

        return board;
    }
}
```

---

## Complexity Analysis — BFS

Let:

- `B` = board length (at most 16)
- `H` = hand length (at most 5)

Theoretical worst-case branching is large, but constraints are tiny.

### Time Complexity

State space is exponential in practice, but bounded enough for the constraints.

So the complexity is best described as:

```text
Exponential in hand size and board branching
```

but feasible because:

- hand is at most 5
- board is at most 16

### Space Complexity

Also exponential in the number of reachable states due to queue + visited set.

---

# Approach 2: DFS / Backtracking + Memoization

## Intuition

Instead of exploring by layers like BFS, we can recursively try all possible moves and return the minimum insertions needed from the current state.

Define:

```text
dfs(board, hand) = minimum number of insertions needed to clear board
```

Then:

- if board is empty, return `0`
- if hand is empty and board is non-empty, return impossible
- otherwise try all valid insertions, recurse, and take the minimum

Because many states repeat, memoization is essential.

---

## Why Memoization Helps

Different insertion sequences can lead to the same:

- reduced board
- remaining hand

Without memoization, we would recompute the same state many times.

So cache the answer for each state:

```text
board + "#" + encoded_hand
```

---

## DFS Transition

For each ball available in the hand:

- remove one copy from the hand
- try inserting it at every useful position
- reduce the board
- recursively solve the new state
- answer is:
  ```text
  1 + dfs(nextBoard, nextHand)
  ```

Take the minimum over all moves.

If no move can clear the board, return impossible.

---

## Java Implementation — DFS + Memoization

```java
import java.util.*;

class Solution {
    private final Map<String, Integer> memo = new HashMap<>();
    private static final int INF = 1_000_000;

    public int findMinStep(String board, String hand) {
        char[] chars = hand.toCharArray();
        Arrays.sort(chars);
        int ans = dfs(board, new String(chars));
        return ans >= INF ? -1 : ans;
    }

    private int dfs(String board, String hand) {
        board = shrink(board);
        if (board.length() == 0) {
            return 0;
        }
        if (hand.length() == 0) {
            return INF;
        }

        String key = board + "#" + hand;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int ans = INF;

        for (int i = 0; i < hand.length(); i++) {
            if (i > 0 && hand.charAt(i) == hand.charAt(i - 1)) {
                continue;
            }

            char ball = hand.charAt(i);
            String nextHand = hand.substring(0, i) + hand.substring(i + 1);

            for (int pos = 0; pos <= board.length(); pos++) {
                if (!isUsefulInsertion(board, pos, ball)) {
                    continue;
                }

                String nextBoard = board.substring(0, pos) + ball + board.substring(pos);
                int sub = dfs(nextBoard, nextHand);
                if (sub != INF) {
                    ans = Math.min(ans, 1 + sub);
                }
            }
        }

        memo.put(key, ans);
        return ans;
    }

    private boolean isUsefulInsertion(String board, int pos, char ball) {
        boolean leftMatch = pos > 0 && board.charAt(pos - 1) == ball;
        boolean rightMatch = pos < board.length() && board.charAt(pos) == ball;
        boolean bridge = pos > 0 && pos < board.length()
                && board.charAt(pos - 1) == board.charAt(pos)
                && board.charAt(pos - 1) != ball;
        return leftMatch || rightMatch || bridge;
    }

    private String shrink(String board) {
        boolean changed = true;
        while (changed) {
            changed = false;
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < board.length()) {
                int j = i;
                while (j < board.length() && board.charAt(j) == board.charAt(i)) {
                    j++;
                }
                if (j - i >= 3) {
                    changed = true;
                } else {
                    sb.append(board, i, j);
                }
                i = j;
            }
            board = sb.toString();
        }
        return board;
    }
}
```

---

## Complexity Analysis — DFS + Memoization

Again the formal worst case is exponential in the number of reachable states.

### Time Complexity

Best described as:

```text
Exponential state search with memoization
```

But practical due to tiny constraints.

### Space Complexity

Memo table plus recursion stack:

```text
Exponential state cache + O(H) recursion depth
```

where `H <= 5`.

---

# Approach 3: Optimized DFS with Hand Counts

## Intuition

This is the most elegant and standard solution.

Instead of trying every insertion position and every hand character explicitly, we use a stronger observation:

To eliminate a run of balls of the same color, we only care about how many extra balls are needed to bring that run to size 3.

Suppose we have a consecutive group like:

```text
RR
```

Then we need:

```text
1 more R
```

to remove it.

If we have:

```text
G
```

then we need:

```text
2 more G
```

to remove it.

So instead of simulating arbitrary insertions everywhere, we recursively choose a run and try to complete it.

This dramatically reduces branching.

---

## Key Reduction Idea

Let a run be:

```text
board[i...j-1]
```

all of the same color.

Let:

```text
need = 3 - (j - i)
```

Then:

- if `need <= 0`, the run already disappears
- otherwise, if we have at least `need` balls of that color in hand, we can spend them to eliminate this run

After removing that run, the board may collapse and create chain reactions.

Then solve the remaining board recursively.

This is a much smarter search than naive insertion enumeration.

---

## Hand Representation

Store hand as counts of the 5 colors:

```text
int[] count = new int[128];
```

Then:

- `count['R']`, `count['Y']`, etc.

This makes checking availability and using balls easy.

---

## DFS State

Define:

```text
helper(board, count)
```

as the minimum number of insertions needed to clear `board` with the current hand counts.

At each step:

1. shrink the board
2. if empty, return 0
3. iterate over each consecutive run
4. compute how many balls are needed to eliminate it
5. if hand has enough, use them and recurse
6. take the minimum

---

## Why This Works

Any winning strategy eventually removes some run.

Removing a run requires the run to reach size at least 3.

So it is enough to reason in terms of completing runs rather than arbitrary insertion orders.

This gives a smaller and cleaner search tree.

---

## Java Implementation — Optimized DFS with Counts

```java
import java.util.*;

class Solution {
    private static final int INF = 1_000_000;
    private final Map<String, Integer> memo = new HashMap<>();

    public int findMinStep(String board, String hand) {
        int[] count = new int[128];
        for (char c : hand.toCharArray()) {
            count[c]++;
        }

        int ans = helper(board, count);
        return ans >= INF ? -1 : ans;
    }

    private int helper(String board, int[] count) {
        board = shrink(board);
        if (board.length() == 0) {
            return 0;
        }

        String key = board + "#" + encode(count);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int ans = INF;
        int i = 0;

        while (i < board.length()) {
            int j = i;
            while (j < board.length() && board.charAt(j) == board.charAt(i)) {
                j++;
            }

            char color = board.charAt(i);
            int runLength = j - i;
            int need = 3 - runLength;

            if (count[color] >= need) {
                int used = Math.max(need, 0);
                count[color] -= used;

                String nextBoard = board.substring(0, i) + board.substring(j);
                int sub = helper(nextBoard, count);
                if (sub != INF) {
                    ans = Math.min(ans, used + sub);
                }

                count[color] += used;
            }

            i = j;
        }

        memo.put(key, ans);
        return ans;
    }

    private String shrink(String board) {
        boolean changed = true;

        while (changed) {
            changed = false;
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < board.length()) {
                int j = i;
                while (j < board.length() && board.charAt(j) == board.charAt(i)) {
                    j++;
                }

                if (j - i >= 3) {
                    changed = true;
                } else {
                    sb.append(board, i, j);
                }

                i = j;
            }

            board = sb.toString();
        }

        return board;
    }

    private String encode(int[] count) {
        return new StringBuilder()
                .append(count['R']).append(',')
                .append(count['Y']).append(',')
                .append(count['B']).append(',')
                .append(count['G']).append(',')
                .append(count['W'])
                .toString();
    }
}
```

---

## Important Note About `need`

If a run length is:

- `1`, then `need = 2`
- `2`, then `need = 1`
- `3` or more, then `need <= 0`

Because the board is shrunk before recursion, runs of length `>= 3` usually do not remain.
Still, using:

```java
int used = Math.max(need, 0);
```

keeps the logic robust.

---

## Complexity Analysis — Optimized DFS

The exact worst-case bound is still exponential because this is a search problem.

But this approach prunes far more aggressively than naive insertion search.

With the given tiny limits:

- board length <= 16
- hand length <= 5

it is efficient enough.

### Time Complexity

Exponential in reachable compressed states, but highly pruned.

### Space Complexity

Memo cache plus recursion stack.

---

# Comparing the Approaches

## BFS

### Strengths

- naturally returns the shortest number of insertions
- conceptually clean because each edge cost is 1

### Weaknesses

- can generate many states
- needs careful visited representation
- more memory-heavy

---

## DFS + Memoization

### Strengths

- straightforward recursive formulation
- easier to write than BFS in some cases
- memoization removes repeated work

### Weaknesses

- still explores many insertions unless pruned
- state branching can be large

---

## Optimized DFS with Run Completion

### Strengths

- best pruning
- focuses only on meaningful removals
- standard and elegant
- usually fastest in practice

### Weaknesses

- less obvious at first
- requires the insight that only completing runs matters

---

# Step-by-Step Example

Consider:

```text
board = "WWRRBBWW"
hand = "WRBRW"
```

After shrinking, board stays the same initially.

Look at runs:

```text
WW | RR | BB | WW
```

To eliminate:

- `RR`, need 1 `R`
- `BB`, need 1 `B`
- each `WW`, need 1 `W`

Suppose we use one `R` on `RR`:

```text
WWRRRBBWW -> WWBBWW
```

Now board becomes:

```text
WWBBWW
```

Then use one `B`:

```text
WWBBBWW -> WWWW -> empty
```

Total used:

```text
2
```

which is optimal.

---

# Common Mistakes

## 1. Removing only one group once

Chain reactions must continue until the board is stable.

You cannot stop after the first deletion.

---

## 2. Trying every insertion blindly

That creates many redundant branches.

Useful pruning or run-based reasoning is essential.

---

## 3. Not memoizing states

The same board and hand combination can be reached through different move orders.

Without memoization, the search repeats too much work.

---

## 4. Representing hand as an unsorted string in memo keys

If using a string-based hand state, normalize it by sorting.
Otherwise equivalent hands like `"RB"` and `"BR"` will be treated as different states.

---

## 5. Forgetting that the goal is minimum insertions

A DFS that returns as soon as it finds _some_ solution is not enough.
It must compare all valid possibilities and return the minimum.

---

# Final Recommendation

For clarity:

- use **DFS + Memoization**

For stronger pruning and the cleanest competitive-programming solution:

- use **Optimized DFS with hand counts**

For shortest-path interpretation:

- use **BFS**

---

# Final Summary

## Problem Type

This is a **state-space search** problem with chain reactions and very small constraints.

---

## Best Practical Idea

Use recursion with memoization and aggressively prune by completing runs rather than trying arbitrary insertions.

---

## Approaches

### 1. BFS

- shortest-path search on `(board, hand)` states
- correct because each insertion costs 1

### 2. DFS + Memoization

- recursive minimum-cost search
- memoize repeated states

### 3. Optimized DFS with Counts

- choose runs to eliminate
- spend the minimum balls needed to make a run disappear
- recurse on the reduced board

---

## Best Final Java Solution

```java
import java.util.*;

class Solution {
    private static final int INF = 1_000_000;
    private final Map<String, Integer> memo = new HashMap<>();

    public int findMinStep(String board, String hand) {
        int[] count = new int[128];
        for (char c : hand.toCharArray()) {
            count[c]++;
        }

        int ans = helper(board, count);
        return ans >= INF ? -1 : ans;
    }

    private int helper(String board, int[] count) {
        board = shrink(board);
        if (board.length() == 0) {
            return 0;
        }

        String key = board + "#" + encode(count);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int ans = INF;
        int i = 0;

        while (i < board.length()) {
            int j = i;
            while (j < board.length() && board.charAt(j) == board.charAt(i)) {
                j++;
            }

            char color = board.charAt(i);
            int runLength = j - i;
            int need = 3 - runLength;

            if (count[color] >= need) {
                int used = Math.max(need, 0);
                count[color] -= used;

                String nextBoard = board.substring(0, i) + board.substring(j);
                int sub = helper(nextBoard, count);
                if (sub != INF) {
                    ans = Math.min(ans, used + sub);
                }

                count[color] += used;
            }

            i = j;
        }

        memo.put(key, ans);
        return ans;
    }

    private String shrink(String board) {
        boolean changed = true;

        while (changed) {
            changed = false;
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < board.length()) {
                int j = i;
                while (j < board.length() && board.charAt(j) == board.charAt(i)) {
                    j++;
                }

                if (j - i >= 3) {
                    changed = true;
                } else {
                    sb.append(board, i, j);
                }

                i = j;
            }

            board = sb.toString();
        }

        return board;
    }

    private String encode(int[] count) {
        return new StringBuilder()
                .append(count['R']).append(',')
                .append(count['Y']).append(',')
                .append(count['B']).append(',')
                .append(count['G']).append(',')
                .append(count['W'])
                .toString();
    }
}
```

This is the most practical exhaustive solution for the problem under the given constraints.
