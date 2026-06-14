# 97. Interleaving String — Exhaustive Approach Summary (Java)

## Problem restatement

Given three strings:

- `s1`
- `s2`
- `s3`

determine whether `s3` can be formed by **interleaving** `s1` and `s2`.

Interleaving means:

- all characters of `s1` and `s2` are used
- the **relative order** of characters inside `s1` must remain unchanged
- the **relative order** of characters inside `s2` must remain unchanged

So the problem is really asking:

> Can we build `s3` by repeatedly taking the next character either from `s1` or from `s2`, without reordering characters inside either string?

---

## First critical observation

If `s3` is formed by interleaving `s1` and `s2`, then:

```text
s1.length() + s2.length() == s3.length()
```

If this length condition fails, the answer is immediately `false`.

That check should always be the very first step.

---

## Core DP insight

Suppose we have already used:

- first `i` characters from `s1`
- first `j` characters from `s2`

Then we have formed exactly:

```text
i + j
```

characters of `s3`.

So the next character we must match in `s3` is:

```java
s3.charAt(i + j)
```

This gives us the central state definition for almost every solution.

---

# Approach 1 — Plain recursion (brute force)

## Idea

At any state `(i, j)`:

- if `s1[i]` matches `s3[i + j]`, try consuming from `s1`
- if `s2[j]` matches `s3[i + j]`, try consuming from `s2`

If either path reaches the end successfully, return `true`.

This is the most direct formulation.

---

## Recursive state

Let:

```text
dfs(i, j) = whether s3[i+j...] can be formed using s1[i...] and s2[j...]
```

Equivalently:

> Have we successfully built prefix `s3[0..i+j-1]` using `s1[0..i-1]` and `s2[0..j-1]`?

---

## Transition

At `(i, j)`:

- if `i < s1.length()` and `s1[i] == s3[i+j]`, try `dfs(i+1, j)`
- if `j < s2.length()` and `s2[j] == s3[i+j]`, try `dfs(i, j+1)`

If either succeeds, answer is `true`.

---

## Base case

If:

```text
i == s1.length() and j == s2.length()
```

then all characters are consumed, so return `true`.

---

## Java code

```java
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length()) {
            return false;
        }
        return dfs(s1, s2, s3, 0, 0);
    }

    private boolean dfs(String s1, String s2, String s3, int i, int j) {
        if (i == s1.length() && j == s2.length()) {
            return true;
        }

        int k = i + j;

        if (i < s1.length() && s1.charAt(i) == s3.charAt(k)) {
            if (dfs(s1, s2, s3, i + 1, j)) {
                return true;
            }
        }

        if (j < s2.length() && s2.charAt(j) == s3.charAt(k)) {
            if (dfs(s1, s2, s3, i, j + 1)) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Time complexity

Worst case:

```text
O(2^(m+n))
```

where:

- `m = s1.length()`
- `n = s2.length()`

Why so large?

Because many states branch in two directions, especially when characters repeat and both choices remain possible.

---

## Space complexity

```text
O(m + n)
```

for recursion depth in the worst case.

---

## Strengths

- Very intuitive
- Great for deriving the DP state

## Weaknesses

- Too slow due to massive repeated work

---

# Approach 2 — Top-down DP with memoization

## Idea

In brute force recursion, the same state `(i, j)` can be reached through many different paths.

That means:

- the problem has **overlapping subproblems**
- memoization will eliminate repeated work

So we cache the result for each pair `(i, j)`.

---

## State definition

```text
memo[i][j] = whether s3[i+j...] can be formed from s1[i...] and s2[j...]
```

Since there are only `(m+1) * (n+1)` distinct states, this becomes polynomial.

---

## Java code

```java
class Solution {
    private Boolean[][] memo;

    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length();
        int n = s2.length();

        if (m + n != s3.length()) {
            return false;
        }

        memo = new Boolean[m + 1][n + 1];
        return dfs(s1, s2, s3, 0, 0);
    }

    private boolean dfs(String s1, String s2, String s3, int i, int j) {
        if (i == s1.length() && j == s2.length()) {
            return true;
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int k = i + j;
        boolean ans = false;

        if (i < s1.length() && s1.charAt(i) == s3.charAt(k)) {
            ans = dfs(s1, s2, s3, i + 1, j);
        }

        if (!ans && j < s2.length() && s2.charAt(j) == s3.charAt(k)) {
            ans = dfs(s1, s2, s3, i, j + 1);
        }

        memo[i][j] = ans;
        return ans;
    }
}
```

---

## Time complexity

```text
O(m * n)
```

Each state `(i, j)` is computed at most once.

---

## Space complexity

```text
O(m * n)
```

for memo table, plus recursion stack:

```text
O(m + n)
```

Overall auxiliary space is dominated by:

```text
O(m * n)
```

---

## Why this is a strong interview path

This is usually the most natural explanation path:

1. Start from recursion
2. Observe repeated states
3. Memoize `(i, j)`

That shows clean DP thinking.

---

# Approach 3 — Bottom-up 2D DP

## Idea

Instead of recursion, define a DP table iteratively.

Let:

```text
dp[i][j] = whether first i chars of s1 and first j chars of s2
           can form first i+j chars of s3
```

This is the most standard tabulation formulation.

---

## State meaning

If `dp[i][j]` is true, then:

```text
s1[0..i-1] and s2[0..j-1]
```

can interleave to form:

```text
s3[0..i+j-1]
```

---

## Transition

To compute `dp[i][j]`, the last character of that prefix in `s3` must come from one of two places:

### Case 1 — Last char comes from `s1`

Then:

- `i > 0`
- `dp[i-1][j]` must already be true
- `s1[i-1] == s3[i+j-1]`

So:

```text
dp[i][j] |= dp[i-1][j] && s1[i-1] == s3[i+j-1]
```

### Case 2 — Last char comes from `s2`

Then:

- `j > 0`
- `dp[i][j-1]` must be true
- `s2[j-1] == s3[i+j-1]`

So:

```text
dp[i][j] |= dp[i][j-1] && s2[j-1] == s3[i+j-1]
```

---

## Base case

```text
dp[0][0] = true
```

Empty `s1` + empty `s2` makes empty `s3`.

---

## Initialization of first row and first column

### First column: only `s1` is used

```text
dp[i][0] = dp[i-1][0] && s1[i-1] == s3[i-1]
```

### First row: only `s2` is used

```text
dp[0][j] = dp[0][j-1] && s2[j-1] == s3[j-1]
```

---

## Java code

```java
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length();
        int n = s2.length();

        if (m + n != s3.length()) {
            return false;
        }

        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 1; i <= m; i++) {
            dp[i][0] = dp[i - 1][0] && s1.charAt(i - 1) == s3.charAt(i - 1);
        }

        for (int j = 1; j <= n; j++) {
            dp[0][j] = dp[0][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char target = s3.charAt(i + j - 1);

                dp[i][j] =
                    (dp[i - 1][j] && s1.charAt(i - 1) == target) ||
                    (dp[i][j - 1] && s2.charAt(j - 1) == target);
            }
        }

        return dp[m][n];
    }
}
```

---

## Time complexity

```text
O(m * n)
```

---

## Space complexity

```text
O(m * n)
```

---

## Why this table is powerful

This DP gives a very precise structural interpretation:

- each cell says whether a certain prefix pair can produce a certain target prefix
- the recurrence is local and easy to reason about
- it directly matches the problem statement

---

# Approach 4 — Bottom-up DP with O(s2.length) space

This is the follow-up and also the most important optimization.

## Key observation

In 2D DP:

```text
dp[i][j]
```

depends only on:

- `dp[i-1][j]` (previous row)
- `dp[i][j-1]` (current row, previous column)

So we do not need the entire matrix.

We only need one row and update it carefully from left to right.

---

## 1D state definition

Let:

```text
dp[j] = current row's dp[i][j]
```

Before updating `dp[j]`, it still stores the previous row value:

```text
old dp[j] = dp[i-1][j]
```

And `dp[j-1]` after update represents:

```text
dp[i][j-1]
```

That is exactly what we need.

---

## Transition

For each `i` and `j`:

```text
dp[j] =
    (dp[j]   && s1[i-1] == s3[i+j-1])   // from previous row
 || (dp[j-1] && s2[j-1] == s3[i+j-1])   // from current row, left cell
```

---

## Initialization

Start with row `i = 0`, meaning only `s2` contributes:

```text
dp[0] = true
dp[j] = dp[j-1] && s2[j-1] == s3[j-1]
```

Then for each new row `i`:

```text
dp[0] = dp[0] && s1[i-1] == s3[i-1]
```

because first column means only `s1` contributes.

---

## Java code

```java
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length();
        int n = s2.length();

        if (m + n != s3.length()) {
            return false;
        }

        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int j = 1; j <= n; j++) {
            dp[j] = dp[j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        for (int i = 1; i <= m; i++) {
            dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);

            for (int j = 1; j <= n; j++) {
                char target = s3.charAt(i + j - 1);

                dp[j] =
                    (dp[j] && s1.charAt(i - 1) == target) ||
                    (dp[j - 1] && s2.charAt(j - 1) == target);
            }
        }

        return dp[n];
    }
}
```

---

## Time complexity

```text
O(m * n)
```

## Space complexity

```text
O(n)
```

where `n = s2.length()`.

This matches the follow-up requirement.

---

## Why left-to-right update works

This is subtle and important.

When computing new `dp[j]`:

- old `dp[j]` is still `dp[i-1][j]`
- updated `dp[j-1]` is already `dp[i][j-1]`

That gives us both dependencies in one array.

If you update in the wrong direction, you destroy one of the required values.

---

# Approach 5 — BFS / state graph view

This is not the usual interview solution, but it is a useful conceptual alternative.

## Graph interpretation

Think of each state `(i, j)` as a node.

From `(i, j)`:

- go to `(i+1, j)` if `s1[i] == s3[i+j]`
- go to `(i, j+1)` if `s2[j] == s3[i+j]`

Then the problem becomes:

> Is there a path from `(0, 0)` to `(m, n)`?

This is equivalent to the DP problem.

---

## Why it is not usually preferred

Without visited-state pruning, BFS/DFS repeats states.
With visited-state pruning, it becomes essentially the same complexity as memoized DP.

So it is more of a conceptual reformulation than a practically better solution.

---

## Java code using BFS

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length();
        int n = s2.length();

        if (m + n != s3.length()) {
            return false;
        }

        boolean[][] visited = new boolean[m + 1][n + 1];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{0, 0});
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int i = state[0];
            int j = state[1];

            if (i == m && j == n) {
                return true;
            }

            int k = i + j;

            if (i < m && !visited[i + 1][j] && s1.charAt(i) == s3.charAt(k)) {
                visited[i + 1][j] = true;
                queue.offer(new int[]{i + 1, j});
            }

            if (j < n && !visited[i][j + 1] && s2.charAt(j) == s3.charAt(k)) {
                visited[i][j + 1] = true;
                queue.offer(new int[]{i, j + 1});
            }
        }

        return false;
    }
}
```

---

## Time complexity

```text
O(m * n)
```

Each state is visited at most once.

## Space complexity

```text
O(m * n)
```

for visited + queue in worst case.

---

## When this view helps

This view is useful if you want to understand that DP here is just reachability on a DAG/grid of states.

It is not usually the cleanest final answer, but it deepens intuition.

---

# Detailed dry run of the 2D DP approach

Let:

```text
s1 = "aabcc"
s2 = "dbbca"
s3 = "aadbbcbcac"
```

We define:

```text
dp[i][j] = can s1[0..i-1] and s2[0..j-1] form s3[0..i+j-1]
```

### Step 1: Length check

```text
5 + 5 = 10
s3.length() = 10
```

So it passes.

### Step 2: Base case

```text
dp[0][0] = true
```

### Step 3: First row

Only `s2` is used to match `s3`.

- `dp[0][1]`: does `"d"` match `"a"`? no
- after that, remaining first-row cells also stay false

### Step 4: First column

Only `s1` is used.

- `dp[1][0]`: `"a"` matches `"a"` → true
- `dp[2][0]`: `"aa"` matches `"aa"` → true
- `dp[3][0]`: next char in `s1` is `'b'`, next in `s3` is `'d'` → false
- rest stay false

Then fill the table by checking whether each target character can come from `s1` or `s2`.

Eventually `dp[5][5] = true`, so answer is `true`.

---

# Another dry run for a false case

```text
s1 = "aabcc"
s2 = "dbbca"
s3 = "aadbbbaccc"
```

This time the table gets stuck because at some point the next required character in `s3` cannot be matched while preserving order from both source strings.

So `dp[5][5] = false`.

---

# Edge cases you must handle carefully

## 1. All strings empty

```text
s1 = ""
s2 = ""
s3 = ""
```

Answer: `true`

Because empty + empty interleaves to empty.

---

## 2. Length mismatch

```text
s1 = "a"
s2 = "b"
s3 = "a"
```

Answer: `false`

Because lengths do not add up.

This check should happen before any DP or recursion.

---

## 3. One source string empty

### Example

```text
s1 = "abc"
s2 = ""
s3 = "abc"
```

Answer: `true`

Because the only valid interleaving is taking everything from `s1`.

### Example

```text
s1 = "abc"
s2 = ""
s3 = "acb"
```

Answer: `false`

Because the order inside `s1` must remain unchanged.

---

## 4. Repeated characters create branching

### Example

```text
s1 = "aa"
s2 = "aa"
s3 = "aaaa"
```

There are many ways to choose from `s1` or `s2`, but we only need to determine whether at least one valid path exists.

This is why plain recursion blows up: many choices look identical locally.

---

# Common mistakes

## Mistake 1 — Forgetting the length check

This is the fastest pruning condition.

Without it, you waste time exploring impossible states.

---

## Mistake 2 — Using the wrong index in `s3`

At state `(i, j)`, the next character in `s3` is:

```java
s3.charAt(i + j)
```

or in prefix DP transitions:

```java
s3.charAt(i + j - 1)
```

depending on whether you use 0-based consumed counts or 1-based DP table indices.

This is one of the most common sources of bugs.

---

## Mistake 3 — Confusing subsequence with interleaving

This is not just “does `s3` contain both as subsequences”.

It must consume **all** characters of both strings, and the total length must match exactly.

---

## Mistake 4 — Wrong 1D DP update order

For the `O(n)` solution, you must update `j` from left to right.

Why?

Because new `dp[j]` needs:

- old `dp[j]` = previous row
- new `dp[j-1]` = current row left neighbor

If you update right to left, you break that logic.

---

## Mistake 5 — Incorrect base initialization

The first row and first column are not trivial defaults.

They represent whether one string alone can match a prefix of `s3`.

You must initialize them carefully.

---

# Comparing all approaches

| Approach           | Idea                                   |         Time |    Space | Notes                             |
| ------------------ | -------------------------------------- | -----------: | -------: | --------------------------------- |
| Plain recursion    | Try taking next char from `s1` or `s2` | `O(2^(m+n))` | `O(m+n)` | Good only for deriving recurrence |
| Memoized recursion | Cache states `(i, j)`                  |     `O(m*n)` | `O(m*n)` | Clean and intuitive               |
| Bottom-up 2D DP    | Prefix table `dp[i][j]`                |     `O(m*n)` | `O(m*n)` | Most standard tabulation          |
| Bottom-up 1D DP    | Compress the DP row                    |     `O(m*n)` |   `O(n)` | Best answer for follow-up         |
| BFS on state graph | Reachability in `(i,j)` grid           |     `O(m*n)` | `O(m*n)` | Useful conceptually               |

---

# Which solution is best in interviews?

A strong answer flow is:

1. Start with recursion
2. Identify the state `(i, j)`
3. Memoize it
4. Convert to bottom-up
5. Optimize to `O(s2.length())` extra space

That shows complete mastery.

If the interviewer specifically asks for the final best implementation, the `O(n)` space DP is the strongest practical solution.

---

# Recommended final interview solution (O(n) extra space)

```java
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length();
        int n = s2.length();

        if (m + n != s3.length()) {
            return false;
        }

        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int j = 1; j <= n; j++) {
            dp[j] = dp[j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        for (int i = 1; i <= m; i++) {
            dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);

            for (int j = 1; j <= n; j++) {
                char target = s3.charAt(i + j - 1);

                dp[j] =
                    (dp[j] && s1.charAt(i - 1) == target) ||
                    (dp[j - 1] && s2.charAt(j - 1) == target);
            }
        }

        return dp[n];
    }
}
```

---

# Why this final solution works

At every point, to build prefix length `i + j` of `s3`, the final character must come from either:

- `s1[i - 1]`, if previous prefix `dp[i-1][j]` was valid
- `s2[j - 1]`, if previous prefix `dp[i][j-1]` was valid

The 1D array stores exactly enough information to simulate the 2D DP row by row.

---

# Compact interview explanation

You can explain it like this:

> Let `dp[j]` represent whether the current prefix of `s1` together with the first `j` characters of `s2` can form the corresponding prefix of `s3`.
> For each cell, the current target character in `s3` must come either from `s1` or from `s2`.
> So the transition is:
>
> `dp[j] = (dp[j] && s1[i-1] == s3[i+j-1]) || (dp[j-1] && s2[j-1] == s3[i+j-1])`
>
> This gives `O(m*n)` time and `O(n)` extra space.

That is usually concise and convincing.

---

# Final takeaway

This problem is a classic **2-string merge DP** problem.

The important mental model is:

- state is determined by how many chars have been consumed from each source string
- the target position in `s3` is automatically `i + j`
- each state can be reached from at most two previous states

Once that clicks, the DP becomes very natural.

---

# Practice extensions

After this, the next useful problems with similar flavor are:

- Edit Distance
- Distinct Subsequences
- Regular Expression Matching
- Wildcard Matching
- Scramble String

They all train the same deeper skill:

> choosing the right DP state over prefixes/suffixes of strings.
