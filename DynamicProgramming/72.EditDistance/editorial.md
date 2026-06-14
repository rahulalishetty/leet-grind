# LeetCode 72 — Edit Distance

## Exhaustive summary of approaches with Java code, detailed explanation, and complexity analysis

---

## Problem

Given two strings `word1` and `word2`, return the minimum number of operations required to convert `word1` into `word2`.

Allowed operations:

- Insert a character
- Delete a character
- Replace a character

This is the classic **Levenshtein distance** problem.

---

## Constraints

- `0 <= word1.length, word2.length <= 500`
- Both strings contain lowercase English letters

---

# 1. Core intuition

Suppose we are comparing:

- `word1[i...]`
- `word2[j...]`

At each step, there are only a few meaningful choices:

1. If current characters already match:
   - do nothing
   - move both pointers

2. If they do not match:
   - **insert**
   - **delete**
   - **replace**

The challenge is not listing these choices.
The challenge is that the same subproblems repeat again and again.

That is why dynamic programming is the natural fit.

---

# 2. State definitions you should know

There are two equally common ways to define state.

## Suffix-style state

`f(i, j)` = minimum edits to convert `word1[i...]` into `word2[j...]`

This is ideal for recursion and memoization.

## Prefix-style state

`dp[i][j]` = minimum edits to convert the first `i` characters of `word1` into the first `j` characters of `word2`

This is ideal for bottom-up tabulation.

Both describe the same problem from opposite directions.

---

# 3. Approach 1 — Pure brute force recursion

This is the most direct solution, and also the least practical.

---

## Idea

At indices `(i, j)`:

- if characters match, move to `(i+1, j+1)`
- otherwise try:
  - insert
  - delete
  - replace

and take the minimum.

---

## Recurrence

Let `f(i, j)` be the edit distance from `word1[i...]` to `word2[j...]`.

### Base cases

- If `i == word1.length()`, then `word1` is exhausted.
  - We must insert all remaining characters of `word2`
  - answer = `word2.length() - j`

- If `j == word2.length()`, then `word2` is exhausted.
  - We must delete all remaining characters of `word1`
  - answer = `word1.length() - i`

### Transition

If `word1.charAt(i) == word2.charAt(j)`:

```text
f(i, j) = f(i + 1, j + 1)
```

Else:

```text
f(i, j) = 1 + min(
    f(i, j + 1),     // insert
    f(i + 1, j),     // delete
    f(i + 1, j + 1)  // replace
)
```

---

## Why the three operations map like this

This part is worth understanding carefully.

### Insert

If we insert `word2[j]` into `word1` at position `i`, then that newly inserted character matches `word2[j]`.

So:

- `word1` stays at `i`
- `word2` moves to `j+1`

Hence:

- `f(i, j+1)`

### Delete

If we delete `word1[i]`, then:

- `word1` moves to `i+1`
- `word2` stays at `j`

Hence:

- `f(i+1, j)`

### Replace

If we replace `word1[i]` with `word2[j]`, then both current characters are resolved.

So:

- move both
- `f(i+1, j+1)`

---

## Complexity

This recursion explodes because the same states are recomputed many times.

- Time: exponential, roughly `O(3^(m+n))` in the loose worst-case sense
- Space: `O(m+n)` recursion depth

This is not usable for the given constraints.

---

## Java code

```java
class SolutionBruteForce {
    public int minDistance(String word1, String word2) {
        return dfs(word1, word2, 0, 0);
    }

    private int dfs(String w1, String w2, int i, int j) {
        if (i == w1.length()) {
            return w2.length() - j;
        }
        if (j == w2.length()) {
            return w1.length() - i;
        }

        if (w1.charAt(i) == w2.charAt(j)) {
            return dfs(w1, w2, i + 1, j + 1);
        }

        int insert = dfs(w1, w2, i, j + 1);
        int delete = dfs(w1, w2, i + 1, j);
        int replace = dfs(w1, w2, i + 1, j + 1);

        return 1 + Math.min(insert, Math.min(delete, replace));
    }
}
```

---

## When to use this approach

Only to derive the recurrence.
Never as the final answer.

---

# 4. Approach 2 — Top-down DP (memoization)

This is the brute-force recurrence fixed with caching.

---

## Idea

The recursive solution revisits the same `(i, j)` many times.
But there are only:

- `(m + 1) * (n + 1)`

distinct states.

So compute each state once and store it.

---

## State

Let:

- `memo[i][j]` = edit distance to convert `word1[i...]` into `word2[j...]`

---

## Recurrence

Same as brute force, but cache the result.

### Base cases

- `i == m` → `n - j`
- `j == n` → `m - i`

### If chars match

- `memo[i][j] = memo[i+1][j+1]`

### Else

- `1 + min(insert, delete, replace)`

---

## Complexity

- Number of states: `O(mn)`
- Each state does `O(1)` work

So:

- Time: `O(mn)`
- Space:
  - memo table: `O(mn)`
  - recursion stack: `O(m+n)`

Overall:

- `O(mn)` space if considering dominant term

---

## Java code

```java
class SolutionTopDown {
    private Integer[][] memo;
    private String word1;
    private String word2;

    public int minDistance(String word1, String word2) {
        this.word1 = word1;
        this.word2 = word2;
        this.memo = new Integer[word1.length() + 1][word2.length() + 1];
        return dfs(0, 0);
    }

    private int dfs(int i, int j) {
        if (i == word1.length()) {
            return word2.length() - j;
        }
        if (j == word2.length()) {
            return word1.length() - i;
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int ans;
        if (word1.charAt(i) == word2.charAt(j)) {
            ans = dfs(i + 1, j + 1);
        } else {
            int insert = dfs(i, j + 1);
            int delete = dfs(i + 1, j);
            int replace = dfs(i + 1, j + 1);

            ans = 1 + Math.min(insert, Math.min(delete, replace));
        }

        return memo[i][j] = ans;
    }
}
```

---

## Strengths

- Very close to the recursive intuition
- Easy to explain
- Good for learning the problem deeply

## Weaknesses

- Uses recursion
- Slightly more overhead than iterative DP
- Can hit recursion-depth issues in some environments

---

# 5. Approach 3 — Bottom-up dynamic programming (full 2D table)

This is the standard iterative solution.

---

## State definition

Let:

```text
dp[i][j] = minimum edits to convert first i chars of word1 into first j chars of word2
```

That means:

- `word1[0...i-1]`
- `word2[0...j-1]`

---

## Table dimensions

If:

- `m = word1.length()`
- `n = word2.length()`

Then `dp` has size:

- `(m + 1) x (n + 1)`

---

## Base initialization

### Convert non-empty prefix of `word1` into empty string

We must delete all characters:

```text
dp[i][0] = i
```

### Convert empty string into non-empty prefix of `word2`

We must insert all characters:

```text
dp[0][j] = j
```

### Empty to empty

```text
dp[0][0] = 0
```

---

## Transition

For `i >= 1`, `j >= 1`:

### If current characters match

If:

```java
word1.charAt(i - 1) == word2.charAt(j - 1)
```

then no operation is needed:

```text
dp[i][j] = dp[i - 1][j - 1]
```

### If current characters differ

We consider the three possible operations.

#### Insert

Insert `word2[j-1]` into `word1`:

```text
dp[i][j - 1] + 1
```

#### Delete

Delete `word1[i-1]`:

```text
dp[i - 1][j] + 1
```

#### Replace

Replace `word1[i-1]` with `word2[j-1]`:

```text
dp[i - 1][j - 1] + 1
```

So:

```text
dp[i][j] = 1 + min(
    dp[i][j - 1],
    dp[i - 1][j],
    dp[i - 1][j - 1]
)
```

---

## Complexity

- Time: `O(mn)`
- Space: `O(mn)`

This is the standard textbook solution.

---

## Java code

```java
class SolutionBottomUp2D {
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int insert = dp[i][j - 1];
                    int delete = dp[i - 1][j];
                    int replace = dp[i - 1][j - 1];

                    dp[i][j] = 1 + Math.min(insert, Math.min(delete, replace));
                }
            }
        }

        return dp[m][n];
    }
}
```

---

## Strengths

- Most standard answer
- No recursion
- Easy to reason about
- Very robust

## Weaknesses

- Uses `O(mn)` memory
- More memory than necessary

---

# 6. Approach 4 — Bottom-up DP with rolling arrays

This is the standard space optimization.

---

## Why it works

Look at the 2D recurrence:

- `dp[i][j]` depends on:
  - `dp[i][j - 1]`
  - `dp[i - 1][j]`
  - `dp[i - 1][j - 1]`

That means to compute row `i`, we only need:

- the previous row
- the current row

So we can reduce space from `O(mn)` to `O(n)` if we iterate over columns of `word2`.

---

## State

Use two arrays:

- `prev[j]` = previous row
- `curr[j]` = current row

---

## Complexity

- Time: `O(mn)`
- Space: `O(n)`

If we choose the shorter string as the column dimension, we can even say:

- Space: `O(min(m, n))`

That is a practical refinement.

---

## Java code

```java
class SolutionRollingDP {
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            curr[0] = i;

            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    int insert = curr[j - 1];
                    int delete = prev[j];
                    int replace = prev[j - 1];

                    curr[j] = 1 + Math.min(insert, Math.min(delete, replace));
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }
}
```

---

## Important implementation detail

After swapping `prev` and `curr`, the old values in `curr` remain.
That is okay because every `curr[j]` is overwritten before being used in the next row, and `curr[0]` is explicitly set at the start of each row.

---

## Strengths

- Same logic as full DP
- Much better memory usage
- Very practical

## Weaknesses

- Slightly harder to visualize than full 2D DP
- Harder to reconstruct the actual edit sequence

---

# 7. Approach 5 — Bottom-up DP with one array

This is a more aggressive space optimization.

---

## Idea

We can optimize to a single array if we are careful with update order.

The problem is that when computing `dp[j]`, we still need access to the old diagonal value `dp[i-1][j-1]`.

So we store that in a temporary variable, usually named `prevDiagonal`.

---

## State meaning

At the start of each new row `i`:

- `dp[j]` still represents the old row: `dp[i-1][j]`

As we update left to right:

- `dp[j-1]` becomes the new row value: `dp[i][j-1]`

We maintain:

- `prevDiagonal` = old `dp[i-1][j-1]`

---

## Complexity

- Time: `O(mn)`
- Space: `O(n)`

Same asymptotic space as rolling arrays, but slightly smaller constant factor.

---

## Java code

```java
class SolutionOneArrayDP {
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[] dp = new int[n + 1];

        for (int j = 0; j <= n; j++) {
            dp[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            int prevDiagonal = dp[0];
            dp[0] = i;

            for (int j = 1; j <= n; j++) {
                int temp = dp[j];

                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prevDiagonal;
                } else {
                    int insert = dp[j - 1];
                    int delete = dp[j];
                    int replace = prevDiagonal;

                    dp[j] = 1 + Math.min(insert, Math.min(delete, replace));
                }

                prevDiagonal = temp;
            }
        }

        return dp[n];
    }
}
```

---

## Strengths

- Very memory-efficient
- Good for high-performance DP implementations

## Weaknesses

- More subtle than two-array rolling DP
- Easier to get wrong
- Less beginner-friendly

---

# 8. Approach 6 — Recovering the actual edit operations

The problem only asks for the minimum number of operations, but sometimes it is useful to recover the sequence too.

This is not a different recurrence, but it is a meaningful extension of the 2D DP approach.

---

## Idea

After building the full `dp` table, backtrack from `dp[m][n]` to `dp[0][0]`.

At each step:

- if characters match and `dp[i][j] == dp[i-1][j-1]`
  - move diagonally without recording an operation

- else if `dp[i][j] == dp[i-1][j-1] + 1`
  - replacement happened

- else if `dp[i][j] == dp[i-1][j] + 1`
  - deletion happened

- else if `dp[i][j] == dp[i][j-1] + 1`
  - insertion happened

This allows reconstruction of an optimal sequence.

---

## Complexity

For distance only:

- building DP table: `O(mn)`
- backtracking: `O(m+n)`

Space:

- still `O(mn)` because reconstruction needs the table

---

## Java code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SolutionWithPath {
    public int minDistance(String word1, String word2) {
        return buildTable(word1, word2)[word1.length()][word2.length()];
    }

    public List<String> getOperations(String word1, String word2) {
        int[][] dp = buildTable(word1, word2);
        int i = word1.length();
        int j = word2.length();

        List<String> ops = new ArrayList<>();

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && word1.charAt(i - 1) == word2.charAt(j - 1)) {
                if (dp[i][j] == dp[i - 1][j - 1]) {
                    i--;
                    j--;
                    continue;
                }
            }

            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add("Replace '" + word1.charAt(i - 1) + "' with '" + word2.charAt(j - 1) + "'");
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add("Delete '" + word1.charAt(i - 1) + "'");
                i--;
            } else {
                ops.add("Insert '" + word2.charAt(j - 1) + "'");
                j--;
            }
        }

        Collections.reverse(ops);
        return ops;
    }

    private int[][] buildTable(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i][j - 1],
                                Math.min(dp[i - 1][j], dp[i - 1][j - 1]));
                }
            }
        }

        return dp;
    }
}
```

---

## Strengths

- Gives the actual transformation sequence
- Useful in editors, diff tools, autocorrect, biology, and spell-check systems

## Weaknesses

- Uses full table memory
- More code and more edge-case handling

---

# 9. Approach 7 — BFS on state graph

This is not the standard way to solve edit distance, but it is conceptually valid.

---

## State graph model

Each state is `(i, j)` meaning:

- currently at index `i` in `word1`
- currently at index `j` in `word2`

Edges represent operations:

- match or replace → `(i+1, j+1)`
- delete → `(i+1, j)`
- insert → `(i, j+1)`

Since each edit operation costs 1, shortest path on this state graph corresponds to edit distance.

However, because transitions include zero-cost matches and unit-cost edits, ordinary BFS is not the neatest formulation. You would need a shortest-path style handling or layer-based care.

So while conceptually elegant, it is not the preferred implementation.

---

## Complexity

If implemented carefully over all states:

- Time: `O(mn)`
- Space: `O(mn)`

But with more overhead than DP.

---

# 10. Approach 8 — Relation to Longest Common Subsequence (LCS)

This is a useful comparison, but also a place people make mistakes.

---

## Important distinction

Edit distance allows:

- insert
- delete
- replace

LCS focuses only on keeping common subsequences.

If only insertions and deletions were allowed, then the answer could be expressed using LCS:

```text
distance = (m - LCS) + (n - LCS)
```

But because **replacement is allowed**, vanilla LCS is not enough to solve the full edit-distance problem directly.

So LCS gives intuition, but not the exact optimal solution for LeetCode 72.

---

## Why this matters

A common incorrect instinct is:

- “Find common parts and count the rest.”

That misses the fact that a replacement can be cheaper than a delete+insert pair.

For example:

- changing `'a'` to `'b'`
- edit distance cost = 1
- delete+insert cost = 2

So the DP for edit distance must explicitly model replacement.

---

# 11. Full comparison of approaches

| Approach                    |        Time |    Space | Practicality | Notes                                     |
| --------------------------- | ----------: | -------: | ------------ | ----------------------------------------- |
| Brute force recursion       | Exponential | `O(m+n)` | No           | Only useful to derive recurrence          |
| Top-down DP                 |     `O(mn)` |  `O(mn)` | Yes          | Natural recursive thinking                |
| Bottom-up 2D DP             |     `O(mn)` |  `O(mn)` | Yes          | Standard textbook solution                |
| Rolling 2-row DP            |     `O(mn)` |   `O(n)` | Yes          | Best practical DP form                    |
| One-array DP                |     `O(mn)` |   `O(n)` | Yes          | Most compact iterative DP                 |
| 2D DP + path reconstruction |     `O(mn)` |  `O(mn)` | Yes          | Needed if operations sequence is required |
| BFS / graph search          |     `O(mn)` |  `O(mn)` | Rarely       | Conceptual alternative                    |

---

# 12. Common edge cases

## Both strings empty

```text
word1 = ""
word2 = ""
answer = 0
```

## One empty, one non-empty

```text
word1 = ""
word2 = "abc"
answer = 3
```

```text
word1 = "abc"
word2 = ""
answer = 3
```

## Already equal

```text
word1 = "abc"
word2 = "abc"
answer = 0
```

## Single replacement

```text
word1 = "a"
word2 = "b"
answer = 1
```

## Insertions only

```text
word1 = "abc"
word2 = "abdc"
answer = 1
```

## Deletions only

```text
word1 = "abcd"
word2 = "acd"
answer = 1
```

---

# 13. Which approach should you choose?

## For learning the problem deeply

Use:

- brute force first
- then memoization

That builds the cleanest intuition.

## For the safest interview answer

Use:

- bottom-up 2D DP

It is the most standard and easiest to explain.

## For the best practical implementation

Use:

- rolling DP
  or
- one-array DP

These give the same time with much less memory.

## If the interviewer asks for the actual operations

Use:

- full 2D DP + backtracking

---

# 14. Recommended interview progression

A strong explanation sequence is:

1. Start with the recursive state `f(i, j)`
2. Explain the three operations
3. Show overlapping subproblems
4. Add memoization
5. Convert to bottom-up DP
6. Optimize space

That shows both correctness and refinement skill.

---

# 15. Final takeaway

Edit distance is one of the cleanest DP problems because every state asks:

> “What is the cheapest way to resolve the current mismatch?”

The full problem can be solved by systematically comparing prefixes or suffixes and choosing among:

- insert
- delete
- replace

The core results are:

- standard DP time: **`O(mn)`**
- standard DP space: **`O(mn)`**
- optimized DP space: **`O(n)`** or **`O(min(m, n))`**

For LeetCode 72, the best practical answer is usually:

- **Bottom-up DP with rolling arrays**

because it is both efficient and easy to justify.
