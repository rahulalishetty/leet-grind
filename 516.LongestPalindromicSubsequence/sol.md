# Longest Palindromic Subsequence — Detailed Summary

## Problem

Given a string `s`, return the length of the **longest palindromic subsequence** in `s`.

A **subsequence** is formed by deleting zero or more characters without changing the order of the remaining characters.

A **palindrome** reads the same forward and backward.

---

## Important distinction: subsequence vs substring

This problem asks for a **subsequence**, not a substring.

That means the characters of the palindrome do **not** need to be contiguous.

For example:

```text
s = "bbbab"
```

The longest palindromic subsequence is:

```text
"bbbb"
```

This is valid even though those four `'b'` characters are not all adjacent in one block.

This distinction is the main reason dynamic programming is needed.

---

# Core idea

We solve this using **interval dynamic programming**.

The key subproblem is:

```text
dp[i][j]
```

where:

> `dp[i][j]` = length of the longest palindromic subsequence inside substring `s[i..j]`

So instead of solving the entire string at once, we solve all substrings and build upward.

---

# Why interval DP fits

Whenever a problem asks for an optimal answer inside a substring `s[i..j]`, and the answer depends on smaller substrings such as:

- `s[i+1..j]`
- `s[i..j-1]`
- `s[i+1..j-1]`

that is a strong sign of interval DP.

This problem has exactly that structure.

---

# Transition

Look at the two end characters of the current interval:

```text
s[i] and s[j]
```

There are two cases.

---

## Case 1: `s[i] == s[j]`

If the two end characters match, then we can use both of them as the two ends of a palindrome.

So the answer becomes:

```text
dp[i][j] = 2 + dp[i+1][j-1]
```

Why?

Because:

- we take `s[i]`
- we take `s[j]`
- and in the middle we place the best palindromic subsequence from `s[i+1..j-1]`

So the total length increases by 2.

---

## Case 2: `s[i] != s[j]`

If the two ends do not match, then they cannot both be used as opposite ends of the same palindrome.

So one of them must be skipped.

That gives two possibilities:

- skip `s[i]` → answer from `s[i+1..j]`
- skip `s[j]` → answer from `s[i..j-1]`

So:

```text
dp[i][j] = max(dp[i+1][j], dp[i][j-1])
```

We take the better of the two.

---

# Base case

A single character is always a palindrome of length 1.

So:

```text
dp[i][i] = 1
```

This initializes the diagonal of the DP table.

---

# Why the recurrence is correct

Let us justify it carefully.

For substring `s[i..j]`:

## If `s[i] == s[j]`

Then an optimal palindromic subsequence can include both ends, because matching characters are exactly what palindromes need at symmetric positions.

Once we use them, the remaining problem is the inside substring `s[i+1..j-1]`.

That yields:

```text
2 + dp[i+1][j-1]
```

## If `s[i] != s[j]`

Then those two characters cannot both be the outermost matching pair of a palindrome.

So at least one of them must be excluded.

That means the optimal answer must lie entirely in one of:

- `s[i+1..j]`
- `s[i..j-1]`

So taking the maximum of those two is sufficient.

This covers all possibilities.

---

# Order of computation

Because `dp[i][j]` depends on smaller intervals:

- `dp[i+1][j-1]`
- `dp[i+1][j]`
- `dp[i][j-1]`

we must compute shorter substrings before longer substrings.

A standard safe order is:

- iterate `i` from `n - 1` down to `0`
- for each `i`, iterate `j` from `i + 1` up to `n - 1`

This ensures all dependencies are already available when computing `dp[i][j]`.

---

# 2D DP Java solution

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;

            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + (i + 1 <= j - 1 ? dp[i + 1][j - 1] : 0);
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

# Slightly cleaner Java version

This version handles short intervals a bit more explicitly.

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;

            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2;
                    if (i + 1 <= j - 1) {
                        dp[i][j] += dp[i + 1][j - 1];
                    }
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

# Dry run 1

## Example

```text
s = "bbbab"
```

Expected answer:

```text
4
```

One longest palindromic subsequence is:

```text
"bbbb"
```

Let us reason through the DP idea.

Characters with indices:

```text
0 1 2 3 4
b b b a b
```

The full interval is `dp[0][4]`.

- `s[0] == s[4] == 'b'`
- so we can use both ends
- then we add the best answer from `dp[1][3]`

Now `s[1..3] = "bba"`.

Its longest palindromic subsequence has length `2` (`"bb"`).

So:

```text
dp[0][4] = 2 + 2 = 4
```

Thus the answer is `4`.

---

# Dry run 2

## Example

```text
s = "cbbd"
```

Expected answer:

```text
2
```

The best palindromic subsequence is:

```text
"bb"
```

Let us reason:

- substring `"cbbd"`
- ends `'c'` and `'d'` do not match
- so answer is the max of:
  - LPS of `"bbd"`
  - LPS of `"cbb"`

Both give `2`.

So final answer is `2`.

---

# DP table intuition

For a string of length `n`, imagine an `n x n` table.

- diagonal cells `dp[i][i] = 1`
- cells above the diagonal represent answers for longer substrings
- each cell depends on:
  - left neighbor
  - bottom neighbor
  - bottom-left diagonal neighbor

So the table is filled diagonally by substring length.

This is a classic interval DP pattern.

---

# Alternative view: LCS with reversed string

Another way to think about the problem is:

The longest palindromic subsequence of `s` is equal to the **longest common subsequence** between:

```text
s
reverse(s)
```

Because a palindrome reads the same forward and backward.

So this problem can also be solved by LCS DP.

However, the interval DP solution is usually more direct and more elegant for this problem.

---

## LCS-based Java solution

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        String rev = new StringBuilder(s).reverse().toString();
        int n = s.length();

        int[][] dp = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == rev.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[n][n];
    }
}
```

---

# Why interval DP is usually preferred here

Although LCS with reversed string works, interval DP is more natural because:

- the palindrome property directly involves the two ends
- the recurrence becomes very intuitive
- it avoids bringing in an extra transformed string

So for explanation and interviews, interval DP is typically the cleaner solution.

---

# Space optimization

The standard interval DP uses:

```text
O(n^2)
```

space.

There are space optimizations possible, but they are trickier because of the diagonal dependency `dp[i+1][j-1]`.

For most interview and contest settings, the `O(n^2)` solution is the standard accepted solution.

---

# Complexity analysis

Let:

```text
n = s.length()
```

## Time complexity

We compute one DP value for each pair `(i, j)` with `i <= j`.

That is about:

```text
n^2 / 2
```

states.

Each state is computed in `O(1)` time.

So:

```text
Time = O(n^2)
```

## Space complexity

We store an `n x n` table:

```text
Space = O(n^2)
```

---

# Common mistakes

## 1. Confusing subsequence with substring

This is the most common mistake.

A substring must be contiguous.

A subsequence only needs order to be preserved.

So `"bbbb"` is a valid subsequence of `"bbbab"` even though it is not one contiguous block.

---

## 2. Using the longest palindromic substring algorithm

That solves a completely different problem.

Longest palindromic substring requires contiguity, which this problem does not.

---

## 3. Filling the DP in the wrong order

If you compute `dp[i][j]` before `dp[i+1][j-1]`, `dp[i+1][j]`, or `dp[i][j-1]`, the recurrence breaks.

---

## 4. Misunderstanding the equal-ends case

When `s[i] == s[j]`, those two characters can extend a palindromic subsequence inside the interval.

That is why we add 2.

---

# Compact recurrence summary

Let:

```text
dp[i][j] = LPS length in s[i..j]
```

Base:

```text
dp[i][i] = 1
```

Transition:

```text
if s[i] == s[j]:
    dp[i][j] = 2 + dp[i+1][j-1]
else:
    dp[i][j] = max(dp[i+1][j], dp[i][j-1])
```

Answer:

```text
dp[0][n-1]
```

---

# Final takeaway

The problem is a standard interval dynamic programming problem.

The key insight is:

- if the ends match, use them both
- if they do not match, skip one side

That leads directly to an `O(n^2)` DP solution.

## Final complexities

```text
Time:  O(n^2)
Space: O(n^2)
```

For interviews and implementation, the interval DP is the most direct and recommended approach.
