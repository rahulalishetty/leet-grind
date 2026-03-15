# 1397. Find All Good Strings

## Problem Statement

You are given:

- an integer `n`
- two strings `s1` and `s2`, both of length `n`
- a string `evil`

We need to count how many strings `x` satisfy all of the following:

1. `x.length == n`
2. `s1 <= x <= s2` in lexicographic order
3. `evil` does **not** appear as a substring of `x`

Return the answer modulo:

```text
10^9 + 7
```

---

## Example 1

```text
Input:
n = 2
s1 = "aa"
s2 = "da"
evil = "b"

Output:
51
```

Explanation:

Valid strings in the range from `"aa"` to `"da"` that do not contain `"b"` total `51`.

---

## Example 2

```text
Input:
n = 8
s1 = "leetcode"
s2 = "leetgoes"
evil = "leet"

Output:
0
```

Explanation:

Every string in that lexicographic range starts with `"leet"`, which already contains the forbidden substring.

So the answer is `0`.

---

## Example 3

```text
Input:
n = 2
s1 = "gx"
s2 = "gz"
evil = "x"

Output:
2
```

---

## Constraints

- `s1.length == n`
- `s2.length == n`
- `s1 <= s2`
- `1 <= n <= 500`
- `1 <= evil.length <= 50`
- all strings contain only lowercase English letters

---

# Core Insight

This is a classic **digit DP on strings** combined with **pattern matching automaton**.

We are building a string of length `n` character by character.

At every position, we must track:

1. whether we are still exactly on the lower bound `s1`
2. whether we are still exactly on the upper bound `s2`
3. how much of `evil` we have matched so far as a suffix of the current prefix

That third part is the key difficulty.

If we ever fully match `evil`, that path becomes invalid.

So this is:

- DP over position and lexicographic tightness
- plus KMP-style automaton state for forbidden substring matching

That is the decisive formulation.

---

# Why Simple Brute Force Is Impossible

There are:

```text
26^n
```

possible strings of length `n`.

With `n = 500`, exhaustive generation is hopeless.

Even checking all strings between `s1` and `s2` directly is impossible.

So we need to count without enumerating.

---

# Approach 1: Brute Force Generation (Conceptual Baseline, Impossible)

## Intuition

A naive idea is:

- generate every string of length `n`
- keep only those in `[s1, s2]`
- reject strings containing `evil`

This is useful only to understand the problem structure, not to solve it.

---

## Why It Fails

Number of candidates:

```text
26^n
```

This is astronomically large even for moderate `n`.

---

## Verdict

Not viable.

---

# Approach 2: Recursive DFS With Range Pruning, But Naive Evil Checking

## Intuition

A more informed brute-force idea is to build the string character by character.

At each position:

- allowed characters depend on current lower/upper tightness
- recurse to next position
- reject if the constructed prefix already contains `evil`

This is better conceptually because it respects the lexicographic bounds.

But if we check `evil` naively in each recursion state, it is still far too slow.

Also, without memoization, the same subproblems repeat heavily.

---

## State Idea

A recursive state might be:

```text
(pos, isTightLow, isTightHigh, builtPrefix)
```

But `builtPrefix` is too large to use directly.

We need a compressed representation of the prefix relevant to `evil`, not the whole prefix.

That pushes us toward automaton state.

---

## Verdict

Good stepping stone, but still not enough.

---

# Approach 3: DP With Naive Forbidden-Substring Tracking

## Intuition

We do not need the full prefix history.

To decide whether appending the next character completes `evil`, we only need to know the longest suffix of the current prefix that matches a prefix of `evil`.

This is exactly the same idea used in KMP.

So instead of storing the whole built prefix, we store:

```text
matched = current matched prefix length of evil
```

Then appending one character updates this matched length.

If it ever becomes `evil.length()`, we have formed the forbidden substring and must reject the path.

This gives the correct DP structure.

---

# Why KMP State Is Necessary

Suppose `evil = "abab"` and our current constructed suffix is `"ab"`.

If we append `'a'`, we move to matched length `3`.

If later a mismatch happens, we do not restart from zero blindly.
We fall back using the KMP prefix function.

So the KMP automaton compresses exactly the information we need.

---

# Approach 4: Top-Down Digit DP + KMP Automaton (Best Exact Solution)

## Intuition

This is the standard optimal solution.

We define:

```text
dp(pos, matched, tightLow, tightHigh)
```

as the number of valid ways to build positions from `pos` onward, where:

- `pos` = current index in the result string
- `matched` = how many characters of `evil` are currently matched as a suffix
- `tightLow` = whether prefix built so far is still exactly equal to `s1` prefix
- `tightHigh` = whether prefix built so far is still exactly equal to `s2` prefix

### Transitions

At position `pos`, allowed character range is:

```text
from = tightLow  ? s1.charAt(pos) : 'a'
to   = tightHigh ? s2.charAt(pos) : 'z'
```

For each possible character `c` in that range:

1. update `matched` using KMP transition
2. if new matched length equals `evil.length()`, skip this choice
3. recurse to next position with updated tight flags

This explores only valid lexicographic states and forbids `evil` efficiently.

---

## KMP Prefix Function

We first build the LPS array for `evil`.

That allows us to compute transitions:

```text
nextMatched(matched, c)
```

in amortized constant time or directly via precomputed automaton.

---

## State Count

The DP state space is:

```text
n * evil.length * 2 * 2
```

At most:

```text
500 * 50 * 4 = 100000
```

which is very manageable.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int MOD = 1_000_000_007;

    private String s1, s2, evil;
    private int n, m;
    private int[] lps;
    private int[][][] memo; // pos, matched, tightMask

    public int findGoodStrings(int n, String s1, String s2, String evil) {
        this.n = n;
        this.s1 = s1;
        this.s2 = s2;
        this.evil = evil;
        this.m = evil.length();

        this.lps = buildLPS(evil);
        this.memo = new int[n + 1][m + 1][4];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                Arrays.fill(memo[i][j], -1);
            }
        }

        return dfs(0, 0, true, true);
    }

    private int dfs(int pos, int matched, boolean tightLow, boolean tightHigh) {
        if (matched == m) {
            return 0; // evil already formed
        }

        if (pos == n) {
            return 1; // built valid full string
        }

        int mask = (tightLow ? 2 : 0) | (tightHigh ? 1 : 0);
        if (memo[pos][matched][mask] != -1) {
            return memo[pos][matched][mask];
        }

        char from = tightLow ? s1.charAt(pos) : 'a';
        char to = tightHigh ? s2.charAt(pos) : 'z';

        long ans = 0;

        for (char c = from; c <= to; c++) {
            int nextMatched = advance(matched, c);

            if (nextMatched == m) {
                continue; // forms evil, invalid
            }

            boolean nextTightLow = tightLow && (c == from);
            boolean nextTightHigh = tightHigh && (c == to);

            ans += dfs(pos + 1, nextMatched, nextTightLow, nextTightHigh);
            if (ans >= MOD) {
                ans %= MOD;
            }
        }

        memo[pos][matched][mask] = (int)(ans % MOD);
        return memo[pos][matched][mask];
    }

    private int advance(int matched, char c) {
        while (matched > 0 && evil.charAt(matched) != c) {
            matched = lps[matched - 1];
        }
        if (evil.charAt(matched) == c) {
            matched++;
        }
        return matched;
    }

    private int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0;

        for (int i = 1; i < p.length(); ) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

Let:

- `n = length of target string`
- `m = evil.length()`

### Time Complexity

Number of states:

```text
O(n * m * 4)
```

Each state tries up to 26 characters.

Transition update is effectively constant-time with KMP fallback bounded by total structure.

So total:

```text
O(n * m * 26)
```

which is well within limits.

### Space Complexity

Memo table:

```text
O(n * m * 4)
```

LPS array:

```text
O(m)
```

Total:

```text
O(n * m)
```

---

## Verdict

This is the best exact solution.

---

# Approach 5: Bottom-Up DP + KMP Automaton

## Intuition

The same idea can be written iteratively.

We can precompute a transition table:

```text
nextState[matched][char]
```

Then run DP from left to right.

State:

```text
dp[pos][matched][tightMask]
```

This avoids recursion and can be cleaner for some people, though slightly more verbose.

---

## Transition Table Benefit

Precomputing the automaton makes each character transition truly `O(1)`.

That removes the need for repeated fallback logic inside the DP loop.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int MOD = 1_000_000_007;

    public int findGoodStrings(int n, String s1, String s2, String evil) {
        int m = evil.length();
        int[] lps = buildLPS(evil);
        int[][] next = buildAutomaton(evil, lps);

        int[][][] dp = new int[n + 1][m][4];
        dp[0][0][3] = 1; // both tight

        for (int pos = 0; pos < n; pos++) {
            for (int matched = 0; matched < m; matched++) {
                for (int mask = 0; mask < 4; mask++) {
                    int cur = dp[pos][matched][mask];
                    if (cur == 0) continue;

                    boolean tightLow = (mask & 2) != 0;
                    boolean tightHigh = (mask & 1) != 0;

                    char from = tightLow ? s1.charAt(pos) : 'a';
                    char to = tightHigh ? s2.charAt(pos) : 'z';

                    for (char c = from; c <= to; c++) {
                        int nm = next[matched][c - 'a'];
                        if (nm == m) continue;

                        int newMask = 0;
                        if (tightLow && c == from) newMask |= 2;
                        if (tightHigh && c == to) newMask |= 1;

                        dp[pos + 1][nm][newMask] += cur;
                        if (dp[pos + 1][nm][newMask] >= MOD) {
                            dp[pos + 1][nm][newMask] -= MOD;
                        }
                    }
                }
            }
        }

        long ans = 0;
        for (int matched = 0; matched < m; matched++) {
            for (int mask = 0; mask < 4; mask++) {
                ans += dp[n][matched][mask];
            }
        }

        return (int)(ans % MOD);
    }

    private int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0;
        for (int i = 1; i < p.length();) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    private int[][] buildAutomaton(String evil, int[] lps) {
        int m = evil.length();
        int[][] next = new int[m][26];

        for (int state = 0; state < m; state++) {
            for (char c = 'a'; c <= 'z'; c++) {
                int j = state;
                while (j > 0 && evil.charAt(j) != c) {
                    j = lps[j - 1];
                }
                if (evil.charAt(j) == c) {
                    j++;
                }
                next[state][c - 'a'] = j;
            }
        }

        return next;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building automaton:

```text
O(m * 26 * fallback cost)
```

which is small because `m <= 50`.

DP:

```text
O(n * m * 4 * 26)
```

### Space Complexity

```text
O(n * m * 4)
```

Can be reduced further with rolling arrays if desired.

---

## Verdict

Also excellent and exact.

---

# Approach 6: Inclusion-Exclusion / Count Up To Bound (Alternative Framing)

## Intuition

Another way to think about the problem is:

```text
count(strings <= s2 and avoiding evil)
-
count(strings < s1 and avoiding evil)
```

This is a common digit-DP trick.

Then the DP only needs one-sided tightness instead of both lower and upper simultaneously.

That can simplify the recurrence conceptually.

However, to compute `count(strings < s1)` cleanly, we need a predecessor-like careful lexicographic handling, which can make implementation messier for strings.

Because the two-tightness DP is already straightforward here, this alternative is usually not the best implementation choice.

---

## Verdict

Valid perspective, but the direct bounded DP is cleaner.

---

# Why the KMP State Is Exactly the Right Compression

Suppose we have built some prefix of the candidate string.

To know whether appending the next character causes `evil` to appear, we do **not** need the whole prefix.

We only need the longest suffix of the current built string that is also a prefix of `evil`.

That is exactly the KMP automaton state.

This compression is what makes the DP feasible.

Without it, the state would explode.

---

# Common Mistakes

## 1. Trying to store the built prefix in the DP state

That is far too large.

The correct compressed state is the KMP matched length.

---

## 2. Forgetting lexicographic tightness on both ends

We must stay within:

```text
s1 <= x <= s2
```

So both lower and upper constraints matter while building the string.

---

## 3. Not cutting off when `evil` is fully matched

As soon as `matched == evil.length()`, that path is invalid and contributes `0`.

---

## 4. Mishandling KMP fallback transitions

When adding a character, if it mismatches the next needed `evil` character, we fall back with the LPS table, not all the way to zero immediately.

---

## 5. Integer overflow before modulo

Use `long` during accumulation, then take modulo.

---

# Final Recommended Solution

Use:

## top-down DP with memoization

plus

## KMP prefix-function automaton for `evil`

This is the most standard and elegant exact solution.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    private static final int MOD = 1_000_000_007;

    private String s1, s2, evil;
    private int n, m;
    private int[] lps;
    private Integer[][][][] memo;

    public int findGoodStrings(int n, String s1, String s2, String evil) {
        this.n = n;
        this.s1 = s1;
        this.s2 = s2;
        this.evil = evil;
        this.m = evil.length();
        this.lps = buildLPS(evil);
        this.memo = new Integer[n + 1][m + 1][2][2];

        return dfs(0, 0, 1, 1);
    }

    private int dfs(int pos, int matched, int tightLow, int tightHigh) {
        if (matched == m) return 0;
        if (pos == n) return 1;

        if (memo[pos][matched][tightLow][tightHigh] != null) {
            return memo[pos][matched][tightLow][tightHigh];
        }

        char from = tightLow == 1 ? s1.charAt(pos) : 'a';
        char to = tightHigh == 1 ? s2.charAt(pos) : 'z';

        long ans = 0;

        for (char c = from; c <= to; c++) {
            int nextMatched = advance(matched, c);
            if (nextMatched == m) continue;

            ans += dfs(
                pos + 1,
                nextMatched,
                (tightLow == 1 && c == from) ? 1 : 0,
                (tightHigh == 1 && c == to) ? 1 : 0
            );

            ans %= MOD;
        }

        return memo[pos][matched][tightLow][tightHigh] = (int) ans;
    }

    private int advance(int matched, char c) {
        while (matched > 0 && evil.charAt(matched) != c) {
            matched = lps[matched - 1];
        }
        if (evil.charAt(matched) == c) {
            matched++;
        }
        return matched;
    }

    private int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0;

        for (int i = 1; i < p.length();) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }
}
```

---

# Complexity Summary

## Brute Force

- Time: exponential
- Space: enormous
- Not viable

## Recursive bounded generation without compressed state

- Still too slow
- Repeats subproblems heavily

## Top-down DP + KMP automaton

- Time: `O(n * m * 26)`
- Space: `O(n * m)`

## Bottom-up DP + automaton

- Time: `O(n * m * 26)`
- Space: `O(n * m)`

---

# Interview Summary

This is a bounded lexicographic counting problem, so digit-DP style reasoning applies.

The forbidden substring `evil` makes ordinary DP insufficient unless we also track how much of `evil` is currently matched.

That is exactly what the KMP prefix-function automaton provides.

So the final recipe is:

1. build KMP LPS for `evil`
2. run DP on:
   - position
   - matched prefix length of `evil`
   - lower-bound tightness
   - upper-bound tightness
3. reject states that fully match `evil`

That gives an exact and efficient solution.
