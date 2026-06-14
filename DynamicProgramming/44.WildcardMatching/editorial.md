# LeetCode 44 — Wildcard Matching

## Exhaustive summary of approaches with Java code, detailed explanation, and complexity analysis

---

## Problem

Given a string `s` and a pattern `p`, implement wildcard matching with:

- `?` → matches exactly one character
- `*` → matches any sequence of characters, including the empty sequence

The match must cover the **entire** string.

---

## Constraints

- `0 <= s.length, p.length <= 2000`
- `s` contains lowercase English letters
- `p` contains lowercase English letters, `?`, `*`

---

# 1. First, what makes this problem hard?

The difficulty is entirely due to `*`.

A `*` can represent:

- zero characters
- one character
- many characters

So one `*` creates many possible branches. Multiple `*` can create a combinatorial explosion unless we control repeated work.

---

# 2. Useful notation

Let:

- `n = s.length()`
- `m = p.length()`

We often define a state:

- `f(i, j)` = whether `s[i...]` matches `p[j...]`

or in bottom-up form:

- `dp[i][j]` = whether the first `i` chars of `s` match the first `j` chars of `p`

---

# 3. Pattern preprocessing: collapse consecutive `*`

Before discussing the main algorithms, there is one practical optimization.

## Why it works

Multiple consecutive `*` are redundant:

- `"a**b***c"` is equivalent to `"a*b*c"`

because each `*` already means “any sequence”.

## Benefit

- fewer states
- simpler greedy behavior
- better constants in DP

## Complexity

- Time: `O(m)`
- Space: `O(m)`

## Java code

```java
class PatternUtils {
    public static String normalizeStars(String p) {
        StringBuilder sb = new StringBuilder();
        boolean prevStar = false;

        for (int i = 0; i < p.length(); i++) {
            char ch = p.charAt(i);
            if (ch == '*') {
                if (!prevStar) {
                    sb.append(ch);
                    prevStar = true;
                }
            } else {
                sb.append(ch);
                prevStar = false;
            }
        }
        return sb.toString();
    }
}
```

---

# 4. Approach 1 — Pure brute force recursion

This is the most direct formulation.

---

## Core idea

At each `(i, j)`:

- if pattern char is a letter, it must match exactly
- if pattern char is `?`, consume one character from both
- if pattern char is `*`, try every possible number of characters it could absorb

This is conceptually simple, but computationally terrible.

---

## Recurrence intuition

Let `f(i, j)` mean whether `s[i...]` matches `p[j...]`.

### Base cases

1. If both string and pattern are exhausted:
   - return `true`

2. If pattern is exhausted but string is not:
   - return `false`

3. If string is exhausted:
   - remaining pattern must be all `*`

### Transitions

- normal char:
  - must match exactly, then go to `(i+1, j+1)`

- `?`:
  - consume one char from both, go to `(i+1, j+1)`

- `*`:
  - try:
    - zero chars
    - one char
    - two chars
    - ...
    - all remaining chars

---

## Why it is bad

Suppose the pattern has several `*`.
Then each `*` branches into many possibilities, and the same `(i, j)` states get recomputed again and again.

---

## Complexity

- Time: exponential
- A broad safe description: `O(2^(n+m))` or worse in practice
- Space: `O(n + m)` recursion stack

---

## Java code

```java
class SolutionBruteForce {
    public boolean isMatch(String s, String p) {
        return dfs(s, p, 0, 0);
    }

    private boolean dfs(String s, String p, int i, int j) {
        if (i == s.length() && j == p.length()) {
            return true;
        }

        if (j == p.length()) {
            return false;
        }

        if (i == s.length()) {
            for (int k = j; k < p.length(); k++) {
                if (p.charAt(k) != '*') {
                    return false;
                }
            }
            return true;
        }

        char pc = p.charAt(j);

        if (pc == '?') {
            return dfs(s, p, i + 1, j + 1);
        }

        if (pc == '*') {
            for (int k = i; k <= s.length(); k++) {
                if (dfs(s, p, k, j + 1)) {
                    return true;
                }
            }
            return false;
        }

        if (s.charAt(i) == pc) {
            return dfs(s, p, i + 1, j + 1);
        }

        return false;
    }
}
```

---

## When to use this approach

Only to understand the recurrence.
Never use it for the actual constraints.

---

# 5. Approach 2 — Top-down DP (memoized recursion)

This is the brute-force recurrence repaired with caching.

---

## Core idea

Many recursive calls revisit the same `(i, j)`.

So cache:

- `true`
- `false`
- unknown

for every pair `(i, j)`.

Each state is solved once.

---

## State

`memo[i][j]` represents whether `s[i...]` matches `p[j...]`.

There are at most:

- `(n + 1) * (m + 1)` states

---

## Transition

### Base cases

- `i == n && j == m` → `true`
- `j == m && i < n` → `false`
- `i == n` → remaining pattern must be all `*`

### If `p[j]` is a letter or `?`

- if current characters match, recurse to `(i+1, j+1)`

### If `p[j] == '*'`

There are two canonical choices:

1. `*` matches empty:
   - `f(i, j+1)`

2. `*` matches one more character:
   - if `i < n`, `f(i+1, j)`

So:

```text
f(i, j) = f(i, j+1) || (i < n && f(i+1, j))
```

This is the critical simplification.
You do **not** need a loop over all possible lengths because repeated `(i+1, j)` calls already allow the `*` to keep expanding.

---

## Complexity

- Time: `O(n * m)`
- Space:
  - memo table: `O(n * m)`
  - recursion stack: `O(n + m)`
- Overall: `O(n * m)`

---

## Java code

```java
class SolutionTopDown {
    private Boolean[][] memo;
    private String s;
    private String p;

    public boolean isMatch(String s, String p) {
        this.s = s;
        this.p = PatternUtils.normalizeStars(p);
        this.memo = new Boolean[s.length() + 1][this.p.length() + 1];
        return dfs(0, 0);
    }

    private boolean dfs(int i, int j) {
        if (memo[i][j] != null) {
            return memo[i][j];
        }

        if (i == s.length() && j == p.length()) {
            return memo[i][j] = true;
        }

        if (j == p.length()) {
            return memo[i][j] = false;
        }

        if (i == s.length()) {
            for (int k = j; k < p.length(); k++) {
                if (p.charAt(k) != '*') {
                    return memo[i][j] = false;
                }
            }
            return memo[i][j] = true;
        }

        char pc = p.charAt(j);
        boolean ans;

        if (pc == '*') {
            ans = dfs(i, j + 1) || dfs(i + 1, j);
        } else {
            boolean firstMatch = (pc == '?' || pc == s.charAt(i));
            ans = firstMatch && dfs(i + 1, j + 1);
        }

        return memo[i][j] = ans;
    }
}
```

---

## Strengths

- Very natural after brute force
- Best approach for understanding the recurrence
- Correct and systematic

## Weaknesses

- Large memory
- Recursion depth can become an issue for long inputs
- Slightly slower than iterative DP in practice

---

# 6. Approach 3 — Bottom-up DP with full 2D table

This is the iterative version of the same state relation.

---

## State definition

Let:

```text
dp[i][j] = whether s[0...i-1] matches p[0...j-1]
```

That means:

- prefix of length `i` from `s`
- prefix of length `j` from `p`

---

## Base initialization

### Empty string vs empty pattern

- `dp[0][0] = true`

### Empty string vs pattern prefix

The empty string can only be matched by a pattern made entirely of `*`.

So:

```text
dp[0][j] = dp[0][j-1]   if p[j-1] == '*'
```

### Non-empty string vs empty pattern

- `dp[i][0] = false` for all `i > 0`

---

## Transition

For each `i >= 1`, `j >= 1`:

### Case 1: direct match or `?`

If:

- `p[j-1] == s[i-1]`
- or `p[j-1] == '?'`

then:

```text
dp[i][j] = dp[i-1][j-1]
```

because both consume exactly one character.

---

### Case 2: `*`

A `*` can behave in two ways:

1. Match empty:
   - ignore the `*`
   - `dp[i][j-1]`

2. Match one or more characters:
   - consume `s[i-1]`
   - keep `*` active
   - `dp[i-1][j]`

So:

```text
dp[i][j] = dp[i][j-1] || dp[i-1][j]
```

---

## Complexity

- Time: `O(n * m)`
- Space: `O(n * m)`

---

## Java code

```java
class SolutionBottomUp2D {
    public boolean isMatch(String s, String p) {
        p = PatternUtils.normalizeStars(p);

        int n = s.length();
        int m = p.length();

        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true;

        for (int j = 1; j <= m; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);

                if (pc == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        return dp[n][m];
    }
}
```

---

## Strengths

- No recursion depth problems
- Very explicit
- Easy to derive from top-down DP
- Good for debugging

## Weaknesses

- Uses a lot of memory
- For `2000 x 2000`, memory is noticeable

---

# 7. Approach 4 — Bottom-up DP with rolling arrays

This is the space-optimized version of bottom-up DP.

---

## Why it works

In the 2D table:

- `dp[i][j]` depends only on:
  - `dp[i-1][j-1]`
  - `dp[i][j-1]`
  - `dp[i-1][j]`

So to compute the current row, we only need:

- previous row
- current row

We do not need the whole table.

---

## Complexity

- Time: `O(n * m)`
- Space: `O(m)`

This is often the best DP implementation in practice.

---

## Java code

```java
class SolutionRollingDP {
    public boolean isMatch(String s, String p) {
        p = PatternUtils.normalizeStars(p);

        int n = s.length();
        int m = p.length();

        boolean[] prev = new boolean[m + 1];
        boolean[] curr = new boolean[m + 1];

        prev[0] = true;

        for (int j = 1; j <= m; j++) {
            if (p.charAt(j - 1) == '*') {
                prev[j] = prev[j - 1];
            }
        }

        for (int i = 1; i <= n; i++) {
            curr[0] = false;

            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);

                if (pc == '*') {
                    curr[j] = curr[j - 1] || prev[j];
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = false;
                }
            }

            boolean[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[m];
    }
}
```

---

## Important implementation detail

After swapping rows, values in `curr` from the previous iteration remain.
That is fine because every `curr[j]` for `j >= 1` gets overwritten before reuse, and `curr[0]` is explicitly reset.

If you want clearer code, you can fill `curr` with `false` each row, but that adds some constant overhead.

---

## Strengths

- Same DP logic
- Much lower memory
- Iterative and robust

## Weaknesses

- Slightly less intuitive than full 2D DP
- Harder to visualize than the full table

---

# 8. Approach 5 — Greedy two pointers with backtracking to the last `*`

This is the most elegant solution for LeetCode 44 specifically.

---

## Core idea

Maintain:

- `i` → current index in `s`
- `j` → current index in `p`

Also remember:

- `starIdx` → position of the last `*` seen in `p`
- `matchIdx` → position in `s` where that `*` started covering characters

### Greedy strategy

- Match normal characters and `?` directly
- When you see `*`, assume it matches **empty** first
- If a future mismatch happens, go back to that `*` and expand it by one more character
- Repeat until either you recover or fail

This is a controlled backtracking strategy, not exponential backtracking.

---

## Step-by-step logic

### Case 1: current chars match or pattern has `?`

Advance both pointers.

### Case 2: current pattern char is `*`

Record:

- `starIdx = j`
- `matchIdx = i`

Then move `j++`.

Interpretation:

- for now, `*` is matching empty

### Case 3: mismatch, but we have seen a `*`

We can try making the last `*` absorb one more character.

Do:

- `j = starIdx + 1`
- `matchIdx++`
- `i = matchIdx`

### Case 4: mismatch and no previous `*`

Return `false`

### Final cleanup

After the string is consumed, the remaining pattern must be all `*`.

---

## Why this works

This is the key conceptual fact:

- normal letters and `?` have fixed width
- `*` is the only flexible-width operator

If a mismatch occurs after a previous `*`, the only possible rescue is to make that `*` absorb more characters.

The greedy algorithm tries the smallest match first, then gradually expands only when needed.

That is enough for wildcard matching.

---

## Complexity

- Time: `O(n + m)` in the standard accepted analysis
- Space: `O(1)`

This is the best asymptotic solution among standard approaches.

---

## Java code

```java
class SolutionGreedy {
    public boolean isMatch(String s, String p) {
        p = PatternUtils.normalizeStars(p);

        int i = 0;          // pointer in s
        int j = 0;          // pointer in p
        int starIdx = -1;   // last seen '*'
        int matchIdx = -1;  // position in s corresponding to expansion of last '*'

        while (i < s.length()) {
            if (j < p.length() &&
                (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                i++;
                j++;
            } else if (j < p.length() && p.charAt(j) == '*') {
                starIdx = j;
                matchIdx = i;
                j++;
            } else if (starIdx != -1) {
                j = starIdx + 1;
                matchIdx++;
                i = matchIdx;
            } else {
                return false;
            }
        }

        while (j < p.length() && p.charAt(j) == '*') {
            j++;
        }

        return j == p.length();
    }
}
```

---

## Strengths

- Best time complexity
- Constant extra space
- Usually the preferred final answer for this problem

## Weaknesses

- Less obvious to derive
- Easier to make off-by-one mistakes
- Harder to prove than DP if you have not internalized the reasoning

---

# 9. Approach 6 — BFS / state graph search

This is not the standard interview solution, but it is conceptually valid.

---

## State graph view

Treat each `(i, j)` as a node.

Possible transitions:

- if chars match or `?`: `(i, j) -> (i+1, j+1)`
- if `*`:
  - `(i, j) -> (i, j+1)` for empty match
  - `(i, j) -> (i+1, j)` if `i < n`, for consuming a char

Now the question becomes:

- can we reach `(n, m)` from `(0, 0)`?

With a visited structure, each state is processed once.

---

## Complexity

- Time: `O(n * m)`
- Space: `O(n * m)`

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Queue;

class SolutionBFS {
    public boolean isMatch(String s, String p) {
        p = PatternUtils.normalizeStars(p);

        int n = s.length();
        int m = p.length();

        boolean[][] visited = new boolean[n + 1][m + 1];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{0, 0});
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int i = state[0];
            int j = state[1];

            if (i == n && j == m) {
                return true;
            }

            if (j == m) {
                continue;
            }

            char pc = p.charAt(j);

            if (pc == '*') {
                if (!visited[i][j + 1]) {
                    visited[i][j + 1] = true;
                    queue.offer(new int[]{i, j + 1});
                }
                if (i < n && !visited[i + 1][j]) {
                    visited[i + 1][j] = true;
                    queue.offer(new int[]{i + 1, j});
                }
            } else if (i < n && (pc == '?' || pc == s.charAt(i))) {
                if (!visited[i + 1][j + 1]) {
                    visited[i + 1][j + 1] = true;
                    queue.offer(new int[]{i + 1, j + 1});
                }
            }
        }

        return false;
    }
}
```

---

## Strengths

- Excellent for conceptual understanding
- Makes the transition graph explicit

## Weaknesses

- More overhead than DP
- Rarely the best practical implementation

---

# 10. Approach 7 — Automata / NFA-style simulation intuition

This is more theoretical than interview-practical.

---

## Core idea

The pattern can be viewed as a very small nondeterministic automaton:

- letter matches itself
- `?` matches one arbitrary char
- `*` can stay active across many chars or consume zero

You maintain a set of active pattern positions while scanning the string.

This is closely related to regex-engine ideas, but wildcard matching is simpler than full regex.

---

## Complexity

Depending on the exact implementation:

- Time: usually `O(n * m)`
- Space: usually `O(m)` or more

---

## Java sketch

Below is a conceptual active-state simulation. It is not the cleanest production solution, but it shows the automata flavor.

```java
import java.util.HashSet;
import java.util.Set;

class SolutionNFAStyle {
    public boolean isMatch(String s, String p) {
        p = PatternUtils.normalizeStars(p);
        Set<Integer> active = new HashSet<>();
        active.add(0);
        active = expandStars(active, p);

        for (int i = 0; i < s.length(); i++) {
            Set<Integer> next = new HashSet<>();
            for (int j : active) {
                if (j == p.length()) {
                    continue;
                }
                char pc = p.charAt(j);
                if (pc == '?' || pc == s.charAt(i)) {
                    next.add(j + 1);
                } else if (pc == '*') {
                    next.add(j); // '*' keeps consuming
                }
            }
            active = expandStars(next, p);
        }

        return active.contains(p.length());
    }

    private Set<Integer> expandStars(Set<Integer> states, String p) {
        Set<Integer> closure = new HashSet<>(states);
        boolean changed = true;

        while (changed) {
            changed = false;
            Set<Integer> add = new HashSet<>();
            for (int j : closure) {
                if (j < p.length() && p.charAt(j) == '*') {
                    if (!closure.contains(j + 1)) {
                        add.add(j + 1);
                    }
                }
            }
            if (!add.isEmpty()) {
                closure.addAll(add);
                changed = true;
            }
        }
        return closure;
    }
}
```

---

## Strengths

- Good theoretical perspective
- Connects the problem to language recognition

## Weaknesses

- Overkill for this problem
- More complicated than DP or greedy

---

# 11. Comparing all approaches

| Approach              |            Time |          Space | Practicality | Main idea                                  |
| --------------------- | --------------: | -------------: | ------------ | ------------------------------------------ |
| Brute force recursion |     Exponential |       `O(n+m)` | No           | Try all possibilities directly             |
| Top-down DP           |         `O(nm)` |        `O(nm)` | Yes          | Cache recursive states                     |
| Bottom-up 2D DP       |         `O(nm)` |        `O(nm)` | Yes          | Iterative table                            |
| Rolling DP            |         `O(nm)` |         `O(m)` | Yes          | Space-optimized DP                         |
| Greedy two pointers   |        `O(n+m)` |         `O(1)` | Yes, best    | Track last `*` and expand only when needed |
| BFS state search      |         `O(nm)` |        `O(nm)` | Rarely       | Explore state graph                        |
| NFA-style simulation  | Usually `O(nm)` | `O(m)` to more | Rarely       | Automata viewpoint                         |

---

# 12. Which approach should you choose?

## For learning

Start with:

1. brute force recurrence
2. top-down DP
3. bottom-up DP

That gives you the clearest mental model.

## For a safe implementation

Use:

- rolling DP

It is robust and much easier to justify formally than greedy.

## For the strongest final answer to LeetCode 44

Use:

- greedy two pointers

It gives:

- `O(n + m)` time
- `O(1)` space

and is the best standard solution for this specific problem.

---

# 13. Common edge cases

## Empty string and empty pattern

```text
s = "", p = ""
true
```

## Empty string with only stars

```text
s = "", p = "***"
true
```

## Empty string with non-star pattern

```text
s = "", p = "*?"
false
```

## Full-string requirement

```text
s = "aa", p = "a"
false
```

## Star can absorb middle section

```text
s = "adceb", p = "*a*b"
true
```

## Consecutive stars

```text
s = "abc", p = "a**c"
true
```

## Pattern longer than string but still matchable

```text
s = "ab", p = "*?*"
true
```

---

# 14. Recommended final interview answer

If the interviewer wants the best asymptotic solution:

- explain the greedy two-pointer method

If the interviewer pushes for a more systematic proof:

- present DP, especially rolling DP

A good sequence in an interview is:

1. derive recursion
2. optimize to memoization
3. mention bottom-up DP
4. then present greedy as the final optimized insight

That shows both correctness reasoning and algorithmic maturity.

---

# 15. Final takeaway

There are two major families of solutions:

## DP family

- brute force → memoization → tabulation → rolling DP

This family is the most systematic and the easiest to prove.

## Greedy family

- two pointers + last `*` memory

This family is the most efficient for wildcard matching.

For LeetCode 44 specifically:

- **Best practical answer:** greedy
- **Best pedagogical answer:** top-down DP or rolling DP
