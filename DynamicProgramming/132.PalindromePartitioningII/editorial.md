# 132. Palindrome Partitioning II — Exhaustive Approach Summary (Java)

## Problem restatement

Given a string `s`, partition it so that **every substring in the partition is a palindrome**.

Return the **minimum number of cuts** needed.

Example:

```text
s = "aab"
```

A valid palindrome partition is:

```text
["aa", "b"]
```

That uses exactly:

```text
1 cut
```

So the answer is `1`.

---

## Very important distinction from problem 131

### Palindrome Partitioning (131)

Return **all valid palindrome partitions**

### Palindrome Partitioning II (132)

Return the **minimum number of cuts**

So this is no longer a backtracking generation problem.

This is an **optimization DP** problem.

---

## Core recurrence intuition

Suppose we want the minimum cuts for suffix starting at index `i`.

If substring `s[i..j]` is a palindrome, then we can make the first piece:

```text
s[i..j]
```

and the rest is optimally partitioned from `j + 1`.

So:

```text
minCuts(i) = min over all j >= i where s[i..j] is palindrome:
             1 + minCuts(j + 1)
```

But there is one subtlety:

- if `s[i..n-1]` itself is a palindrome, then no cut is needed

So instead of thinking purely in “cuts” from the start, it is often easier to think in terms of:

- minimum number of palindromic pieces
- or use a recurrence carefully adjusted for cuts

This is where many bugs happen.

---

# Approach 1 — Brute force recursion

## Idea

Try every possible palindromic prefix and recursively solve the remaining suffix.

Among all valid choices, return the minimum cuts.

---

## State definition

Let:

```text
dfs(start) = minimum cuts needed for substring s[start..n-1]
```

For every `end >= start`:

- if `s[start..end]` is a palindrome:
  - if `end == n - 1`, then this whole suffix is a palindrome, so cuts = `0`
  - otherwise, cuts = `1 + dfs(end + 1)`

Take the minimum over all such `end`.

---

## Java code

```java
class Solution {
    public int minCut(String s) {
        return dfs(s, 0);
    }

    private int dfs(String s, int start) {
        if (start == s.length()) {
            return 0;
        }

        int minCuts = Integer.MAX_VALUE;

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                if (end == s.length() - 1) {
                    minCuts = 0;
                } else {
                    minCuts = Math.min(minCuts, 1 + dfs(s, end + 1));
                }
            }
        }

        return minCuts;
    }

    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Time complexity

This is exponential.

Why?

At each index, you may branch to many possible palindrome endings, and the same suffix is recomputed repeatedly.

A rough upper bound is:

```text
Exponential, often described as O(n * 2^n) or worse
```

If you include repeated palindrome checking, performance is even worse.

---

## Space complexity

Recursion depth is at most `n`:

```text
O(n)
```

---

## Strengths

- Helps derive the recurrence clearly

## Weaknesses

- Far too slow for `n = 2000`
- Repeated suffix work
- Repeated palindrome work

---

# Approach 2 — Top-down DP with memoization, on-the-fly palindrome checking

## Idea

The recursive suffix state repeats.

So memoize:

```text
memo[start] = minimum cuts needed for s[start..n-1]
```

This removes repeated suffix computation.

---

## Java code

```java
import java.util.Arrays;

class Solution {
    public int minCut(String s) {
        int[] memo = new int[s.length()];
        Arrays.fill(memo, -1);
        return dfs(s, 0, memo);
    }

    private int dfs(String s, int start, int[] memo) {
        if (start == s.length()) {
            return 0;
        }

        if (memo[start] != -1) {
            return memo[start];
        }

        int minCuts = Integer.MAX_VALUE;

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                if (end == s.length() - 1) {
                    minCuts = 0;
                } else {
                    minCuts = Math.min(minCuts, 1 + dfs(s, end + 1, memo));
                }
            }
        }

        memo[start] = minCuts;
        return minCuts;
    }

    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Time complexity

There are `n` suffix states.

For each `start`, we try all `end` from `start` to `n - 1`, and palindrome checking can cost up to `O(n)`.

So total complexity is:

```text
O(n^3)
```

---

## Space complexity

- memo array: `O(n)`
- recursion stack: `O(n)`

Total auxiliary space:

```text
O(n)
```

---

## Why this is better than brute force

This removes repeated recomputation of suffix answers.

But it still repeatedly checks whether substrings are palindromes, which becomes the next bottleneck.

---

# Approach 3 — Top-down DP with memoization + palindrome DP table

## Idea

Optimize Approach 2 by precomputing whether every substring is a palindrome.

Then recurrence becomes:

- try every `end`
- if `isPal[start][end]` is true, consider the cut

Now palindrome lookup is O(1).

---

## Palindrome DP definition

Let:

```text
isPal[i][j] = whether substring s[i..j] is a palindrome
```

Transition:

```text
isPal[i][j] = (s.charAt(i) == s.charAt(j)) &&
              (j - i <= 2 || isPal[i + 1][j - 1])
```

We fill from shorter substrings to longer ones by iterating `i` backward.

---

## Java code

```java
import java.util.Arrays;

class Solution {
    public int minCut(String s) {
        int n = s.length();
        boolean[][] isPal = buildPalindromeTable(s);
        int[] memo = new int[n];
        Arrays.fill(memo, -1);
        return dfs(s, 0, isPal, memo);
    }

    private int dfs(String s, int start, boolean[][] isPal, int[] memo) {
        if (start == s.length()) {
            return 0;
        }

        if (memo[start] != -1) {
            return memo[start];
        }

        int minCuts = Integer.MAX_VALUE;

        for (int end = start; end < s.length(); end++) {
            if (isPal[start][end]) {
                if (end == s.length() - 1) {
                    minCuts = 0;
                } else {
                    minCuts = Math.min(minCuts, 1 + dfs(s, end + 1, isPal, memo));
                }
            }
        }

        memo[start] = minCuts;
        return minCuts;
    }

    private boolean[][] buildPalindromeTable(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                }
            }
        }

        return isPal;
    }
}
```

---

## Time complexity

### Build palindrome table

```text
O(n^2)
```

### DFS with memoization

There are `n` states, and each scans up to `n` endings:

```text
O(n^2)
```

Total:

```text
O(n^2)
```

---

## Space complexity

- palindrome table: `O(n^2)`
- memo: `O(n)`
- recursion stack: `O(n)`

Total auxiliary space dominated by:

```text
O(n^2)
```

---

## Why this is a major milestone

This is the first truly efficient solution for the given constraints.

It transforms:

- repeated palindrome testing → O(1) lookup
- repeated suffix computation → memoized DP

---

# Approach 4 — Bottom-up DP with palindrome table (standard solution)

This is the most standard interview solution.

## Idea

Instead of recursive suffix DP, define:

```text
dp[i] = minimum cuts needed for substring s[0..i]
```

That is, the minimum cuts for the prefix ending at index `i`.

Then answer is `dp[n - 1]`.

---

## Transition

Consider the last palindromic piece ending at `i`.

If `s[j..i]` is a palindrome, then:

- if `j == 0`, the whole prefix `s[0..i]` is a palindrome, so `dp[i] = 0`
- otherwise, we cut right before `j`, so:
  ```text
  dp[i] = min(dp[i], dp[j - 1] + 1)
  ```

So:

```text
dp[i] = min over all j <= i where s[j..i] is palindrome:
        0 if j == 0
        dp[j - 1] + 1 otherwise
```

---

## Java code

```java
class Solution {
    public int minCut(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                }
            }
        }

        int[] dp = new int[n];

        for (int i = 0; i < n; i++) {
            if (isPal[0][i]) {
                dp[i] = 0;
            } else {
                dp[i] = i; // worst case: cut before every character

                for (int j = 1; j <= i; j++) {
                    if (isPal[j][i]) {
                        dp[i] = Math.min(dp[i], dp[j - 1] + 1);
                    }
                }
            }
        }

        return dp[n - 1];
    }
}
```

---

## Time complexity

### Palindrome table

```text
O(n^2)
```

### DP transitions

For each `i`, scan all `j <= i`:

```text
O(n^2)
```

Total:

```text
O(n^2)
```

---

## Space complexity

- palindrome table: `O(n^2)`
- dp array: `O(n)`

Total auxiliary space:

```text
O(n^2)
```

---

## Why this is usually the best answer

It is:

- iterative
- efficient
- standard
- easy to justify

For most interviews, this is the safest final solution.

---

# Approach 5 — Bottom-up DP with center expansion (O(n^2) time, O(n) extra space)

This is a very elegant optimization.

## Idea

Instead of explicitly storing the full `isPal[i][j]` table, expand around every center and directly update the cut DP.

Define:

```text
dp[i] = minimum cuts needed for prefix s[0..i-1]
```

This shifted indexing is often cleaner.

So:

- `dp[0] = -1`
  - because an empty string needs no cuts, and this trick makes transitions elegant
- answer will be `dp[n]`

When we find a palindrome `s[left..right]`, we can update:

```text
dp[right + 1] = min(dp[right + 1], dp[left] + 1)
```

Interpretation:

- partition prefix up to `left - 1` optimally
- then add palindrome `s[left..right]`
- so number of cuts is `dp[left] + 1`

The `dp[0] = -1` trick ensures that if the palindrome starts at index `0`, then:

```text
dp[right + 1] = 0
```

which is exactly correct.

---

## Why center expansion helps

Every palindrome can be found by expanding around a center.

For each center:

- expand odd-length palindromes
- expand even-length palindromes

Every time expansion succeeds, update the cut DP immediately.

So we avoid storing the full palindrome table.

---

## Java code

```java
import java.util.Arrays;

class Solution {
    public int minCut(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);

        dp[0] = -1;

        for (int center = 0; center < n; center++) {
            expandAndUpdate(s, center, center, dp);       // odd-length palindromes
            expandAndUpdate(s, center, center + 1, dp);   // even-length palindromes
        }

        return dp[n];
    }

    private void expandAndUpdate(String s, int left, int right, int[] dp) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            dp[right + 1] = Math.min(dp[right + 1], dp[left] + 1);
            left--;
            right++;
        }
    }
}
```

---

## Time complexity

For each center, expansion can take up to `O(n)`.

There are `2n - 1` centers, so total:

```text
O(n^2)
```

---

## Space complexity

Only the cut DP array is stored:

```text
O(n)
```

Auxiliary space is therefore:

```text
O(n)
```

---

## Why this approach is excellent

It matches the optimal `O(n^2)` time while reducing space from `O(n^2)` to `O(n)`.

This is arguably the most elegant fully optimized solution.

However, it is slightly trickier to derive than the palindrome-table solution.

---

# Approach 6 — BFS over cut positions (conceptual alternative)

This is more educational than standard.

## Idea

Treat each index as a node.

If `s[i..j]` is a palindrome, then you can jump from index `i` to `j + 1`.

You want the minimum number of cuts, which corresponds to the minimum number of jumps minus 1.

This becomes a shortest path problem on a DAG of indices.

However, you still need palindrome information, and DP is simpler.

So BFS is conceptually valid but not usually preferred.

---

## Why it is not the standard answer

- it does not simplify the recurrence
- you still need efficient palindrome detection
- DP is cleaner and more direct

Still, it can help intuition:

> each palindrome substring is an allowed jump, and we want the minimum jumps to reach the end

---

# Detailed dry run of the standard bottom-up DP

Input:

```text
s = "aab"
```

Indices:

```text
0 1 2
a a b
```

---

## Step 1 — Build palindrome table

Palindromic substrings:

- `"a"` at `[0,0]`
- `"a"` at `[1,1]`
- `"b"` at `[2,2]`
- `"aa"` at `[0,1]`

Not palindrome:

- `"ab"` at `[1,2]`
- `"aab"` at `[0,2]`

---

## Step 2 — Compute dp

Let:

```text
dp[i] = minimum cuts for s[0..i]
```

### i = 0

Substring `s[0..0] = "a"` is palindrome.

So:

```text
dp[0] = 0
```

### i = 1

Substring `s[0..1] = "aa"` is palindrome.

So:

```text
dp[1] = 0
```

### i = 2

Substring `s[0..2] = "aab"` is not palindrome.

Initialize worst case:

```text
dp[2] = 2
```

Try all `j`:

- `j = 1`: `s[1..2] = "ab"` not palindrome
- `j = 2`: `s[2..2] = "b"` palindrome
  so:
  ```text
  dp[2] = min(dp[2], dp[1] + 1) = min(2, 0 + 1) = 1
  ```

Final:

```text
dp[2] = 1
```

Answer:

```text
1
```

---

# Detailed dry run of center-expansion solution

Input:

```text
s = "aab"
```

Let:

```text
dp[i] = minimum cuts for prefix s[0..i-1]
```

Initialize:

```text
dp = [-1, INF, INF, INF]
```

---

## Center = 0

### Odd palindrome `(0,0)` => `"a"`

Update:

```text
dp[1] = min(dp[1], dp[0] + 1) = min(INF, -1 + 1) = 0
```

### Even palindrome `(0,1)` => `"aa"`

Update:

```text
dp[2] = min(dp[2], dp[0] + 1) = 0
```

Now:

```text
dp = [-1, 0, 0, INF]
```

---

## Center = 1

### Odd palindrome `(1,1)` => `"a"`

Update:

```text
dp[2] = min(0, dp[1] + 1 = 1) = 0
```

### Even palindrome `(1,2)` => `"ab"` not palindrome

---

## Center = 2

### Odd palindrome `(2,2)` => `"b"`

Update:

```text
dp[3] = min(INF, dp[2] + 1 = 1)
```

Now:

```text
dp = [-1, 0, 0, 1]
```

Answer:

```text
dp[3] = 1
```

Correct.

---

# Edge cases

## 1. Single character

```text
s = "a"
```

Already a palindrome.

Minimum cuts:

```text
0
```

---

## 2. Entire string is a palindrome

```text
s = "abba"
```

No cuts needed:

```text
0
```

---

## 3. No two-character-or-more palindrome

```text
s = "abc"
```

Best partition:

```text
"a" | "b" | "c"
```

Cuts:

```text
2
```

In general, worst-case answer is:

```text
n - 1
```

---

## 4. Repeated characters

```text
s = "aaaa"
```

Whole string is a palindrome.

Answer:

```text
0
```

This is a good test for whether your palindrome detection and DP are correct.

---

# Common mistakes

## Mistake 1 — Off-by-one confusion between “pieces” and “cuts”

If a string is partitioned into `p` palindromic pieces, then cuts are:

```text
p - 1
```

Many incorrect recurrences accidentally count pieces instead of cuts.

The standard prefix-DP and center-expansion methods avoid this confusion cleanly.

---

## Mistake 2 — Forgetting the special case when the whole prefix is palindrome

In prefix DP:

```java
if (isPal[0][i]) dp[i] = 0;
```

Without this, the recurrence may incorrectly force at least one cut.

---

## Mistake 3 — Wrong palindrome table fill order

For:

```text
isPal[i][j] depends on isPal[i+1][j-1]
```

so `i` should typically go from `n - 1` down to `0`.

---

## Mistake 4 — Using brute force/backtracking because problem 131 used it

Problem 131 asks for all partitions, so exponential output is unavoidable.

Problem 132 asks only for the minimum cut, so DP is the right tool.

Do not carry over the backtracking mindset blindly.

---

## Mistake 5 — Misunderstanding the `dp[0] = -1` trick in center expansion

This trick is deliberate.

It ensures:

- if the first piece `s[0..r]` is a palindrome,
- then:
  ```text
  dp[r + 1] = dp[0] + 1 = 0
  ```

which correctly means no cuts are needed before the first palindrome piece.

---

# Comparing all approaches

| Approach                                         | Idea                                          |                                Time |    Space | Notes                               |
| ------------------------------------------------ | --------------------------------------------- | ----------------------------------: | -------: | ----------------------------------- |
| Brute force recursion                            | Try all palindromic prefixes recursively      |                         Exponential |   `O(n)` | Good only for recurrence derivation |
| Memoized recursion + on-the-fly palindrome check | Cache suffix answers                          |                            `O(n^3)` |   `O(n)` | Removes suffix recomputation only   |
| Memoized recursion + palindrome table            | Cache suffix answers + O(1) palindrome lookup |                            `O(n^2)` | `O(n^2)` | Efficient top-down solution         |
| Bottom-up prefix DP + palindrome table           | `dp[i] = min cuts for prefix`                 |                            `O(n^2)` | `O(n^2)` | Standard interview answer           |
| Center expansion + cut DP                        | Expand palindromes and update cuts directly   |                            `O(n^2)` |   `O(n)` | Best optimized space solution       |
| BFS / shortest path view                         | Nodes are indices, palindromes are jumps      | Usually `O(n^2)` with preprocessing |   Varies | More conceptual than practical      |

---

# Which solution should you give in interviews?

A strong answer progression is:

1. Start from recursive suffix recurrence
2. Add memoization
3. Notice palindrome checking bottleneck
4. Precompute palindrome information
5. Present standard bottom-up DP
6. Optionally mention center-expansion optimization for `O(n)` extra space

That shows both depth and optimization awareness.

---

# Recommended final interview solution

This is the safest standard answer:

```java
class Solution {
    public int minCut(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                }
            }
        }

        int[] dp = new int[n];

        for (int i = 0; i < n; i++) {
            if (isPal[0][i]) {
                dp[i] = 0;
            } else {
                dp[i] = i;

                for (int j = 1; j <= i; j++) {
                    if (isPal[j][i]) {
                        dp[i] = Math.min(dp[i], dp[j - 1] + 1);
                    }
                }
            }
        }

        return dp[n - 1];
    }
}
```

### Time complexity

```text
O(n^2)
```

### Space complexity

```text
O(n^2)
```

---

# Recommended optimized-space solution

```java
import java.util.Arrays;

class Solution {
    public int minCut(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = -1;

        for (int center = 0; center < n; center++) {
            expandAndUpdate(s, center, center, dp);
            expandAndUpdate(s, center, center + 1, dp);
        }

        return dp[n];
    }

    private void expandAndUpdate(String s, int left, int right, int[] dp) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            dp[right + 1] = Math.min(dp[right + 1], dp[left] + 1);
            left--;
            right++;
        }
    }
}
```

### Time complexity

```text
O(n^2)
```

### Space complexity

```text
O(n)
```

---

# Compact interview explanation

You can explain the standard DP like this:

> Let `dp[i]` be the minimum cuts needed for prefix `s[0..i]`.
> If `s[j..i]` is a palindrome, then the last piece can be `s[j..i]`.
> So:
>
> - if `j == 0`, then `dp[i] = 0`
> - otherwise `dp[i] = min(dp[i], dp[j-1] + 1)`
>
> Precompute a palindrome table `isPal[j][i]` so palindrome checks are O(1).
> This yields an `O(n^2)` solution.

For the optimized-space version:

> Instead of storing the whole palindrome table, expand around each center and directly update the cut DP.
> This keeps time `O(n^2)` but reduces extra space to `O(n)`.

---

# Final takeaway

This problem is a classic example of:

- recognizing that brute force partitioning is too expensive
- converting the problem into a prefix/suffix DP
- precomputing or implicitly generating palindrome information efficiently

The most important mental pattern is:

> choose the last palindromic piece of a prefix, then optimize what comes before it

That is the heart of the solution.

---

# Practice extension

The most useful related problems after this are:

- Palindrome Partitioning (131)
- Word Break
- Word Break II
- Minimum Cut / segmentation style DP problems

They help build the instinct for when a decomposition problem is:

- generation/backtracking
- counting DP
- minimum-cost DP
