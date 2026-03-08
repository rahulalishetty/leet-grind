# Longest Palindromic Subsequence — Detailed Summary of 3 DP Approaches

## Problem

Given a string `s`, return the length of the **longest palindromic subsequence** in `s`.

A subsequence is formed by deleting zero or more characters **without changing the relative order** of the remaining characters.

A palindrome is a sequence that reads the same forward and backward.

---

## Important distinction: subsequence vs substring

This problem asks for a **subsequence**, not a substring.

That means the chosen characters do **not** need to be contiguous.

Example:

```text
s = "bbbab"
```

A longest palindromic subsequence is:

```text
"bbbb"
```

Even though those four `'b'` characters are not one contiguous block, they still form a valid subsequence.

This distinction is the main reason the problem is different from “longest palindromic substring”.

---

# Core recurrence

All three approaches are built on the same interval recurrence.

Let:

```text
LPS(i, j)
```

be the length of the longest palindromic subsequence inside substring:

```text
s[i..j]
```

Now compare the two ends of the substring:

- `s[i]`
- `s[j]`

There are two cases.

## Case 1: `s[i] == s[j]`

If the two end characters match, then they can both be part of the palindrome.

So:

```text
LPS(i, j) = 2 + LPS(i + 1, j - 1)
```

because we use both ends and solve the inside substring.

## Case 2: `s[i] != s[j]`

If the end characters do not match, they cannot both serve as the two ends of the same palindrome.

So one of them must be skipped.

That gives:

```text
LPS(i, j) = max(LPS(i + 1, j), LPS(i, j - 1))
```

We try both possibilities and keep the better one.

---

# Base cases

## Empty interval

If:

```text
i > j
```

the substring is empty, so:

```text
LPS(i, j) = 0
```

## Single character

If:

```text
i == j
```

then the substring has one character, which is itself a palindrome of length 1:

```text
LPS(i, j) = 1
```

These base cases are especially important in the recursive approach.

---

# Why this recurrence is correct

Consider any substring `s[i..j]`.

## If ends match

Suppose `s[i] == s[j]`.

Then those two equal characters can sit at the two ends of a palindromic subsequence. Once we use them, the best thing we can do in the middle is exactly the best palindromic subsequence of `s[i+1..j-1]`.

So the total becomes:

```text
2 + LPS(i + 1, j - 1)
```

## If ends do not match

Suppose `s[i] != s[j]`.

Then they cannot both be used simultaneously as symmetric endpoints of the same palindrome. So any valid longest palindromic subsequence must exclude either:

- `s[i]`, or
- `s[j]`

That means the answer must come from either:

```text
LPS(i + 1, j)
```

or

```text
LPS(i, j - 1)
```

Hence we take the maximum.

That fully covers all possibilities.

---

# Approach 1: Recursive Dynamic Programming (Top-Down Memoization)

## Intuition

The recurrence is naturally recursive.

Define a function:

```text
lps(i, j)
```

that returns the answer for substring `s[i..j]`.

Without memoization, many subproblems repeat.

For example, intervals like:

```text
lps(2, n-2)
lps(1, n-3)
```

can be reached through different branches of the recursion tree.

So we store the result for every pair `(i, j)` in a 2D memo table.

This converts an exponential brute force into `O(n^2)`.

---

## Algorithm

1. Let `n = s.length()`.
2. Create a 2D memo table:

```text
memo[n][n]
```

initialized with `0`. 3. Define recursive function `lps(s, i, j, memo)`:

- if `i > j`, return `0`
- if `i == j`, return `1`
- if `memo[i][j] != 0`, return cached value
- if `s[i] == s[j]`, compute `2 + lps(i+1, j-1)`
- else compute `max(lps(i+1, j), lps(i, j-1))`

4. Return:

```text
lps(0, n - 1)
```

---

## Java implementation

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] memo = new int[n][n];
        return lps(s, 0, n - 1, memo);
    }

    private int lps(String s, int i, int j, int[][] memo) {
        if (i > j) {
            return 0;
        }
        if (i == j) {
            return 1;
        }
        if (memo[i][j] != 0) {
            return memo[i][j];
        }

        if (s.charAt(i) == s.charAt(j)) {
            memo[i][j] = 2 + lps(s, i + 1, j - 1, memo);
        } else {
            memo[i][j] = Math.max(
                lps(s, i + 1, j, memo),
                lps(s, i, j - 1, memo)
            );
        }

        return memo[i][j];
    }
}
```

---

## Complexity

Let `n = s.length()`.

### Time complexity

There are `O(n^2)` possible intervals `(i, j)`.

Each one is solved once because of memoization.

So:

```text
Time = O(n^2)
```

### Space complexity

The memo table uses:

```text
O(n^2)
```

The recursion stack can go up to `O(n)` depth, but the memo dominates.

So:

```text
Space = O(n^2)
```

---

# Approach 2: Iterative Dynamic Programming (Bottom-Up 2D)

## Intuition

The top-down solution can be converted into a bottom-up DP table.

Define:

```text
dp[i][j]
```

as the length of the longest palindromic subsequence in substring `s[i..j]`.

This is the same state as before, just computed iteratively.

The answer is:

```text
dp[0][n - 1]
```

---

## Fill order

Since `dp[i][j]` depends on:

- `dp[i + 1][j - 1]`
- `dp[i + 1][j]`
- `dp[i][j - 1]`

we must fill smaller intervals first.

A standard order is:

- `i` from `n - 1` down to `0`
- `j` from `i + 1` up to `n - 1`

This ensures every dependency is already known when computing `dp[i][j]`.

---

## Transition

For each interval `[i..j]`:

### If `s[i] == s[j]`

```text
dp[i][j] = 2 + dp[i + 1][j - 1]
```

### Otherwise

```text
dp[i][j] = max(dp[i + 1][j], dp[i][j - 1])
```

Base:

```text
dp[i][i] = 1
```

---

## Java implementation

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];

        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;

            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + dp[i + 1][j - 1];
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

## Why this works

This is exactly the same recurrence as the recursive solution, but computed in an order where all dependent subproblems are already available.

It is often easier to debug than recursion because the whole DP table is explicit.

---

## Complexity

### Time complexity

We fill an `n x n` table, and each cell takes `O(1)` work.

So:

```text
Time = O(n^2)
```

### Space complexity

The 2D table uses:

```text
Space = O(n^2)
```

---

# Approach 3: Dynamic Programming with Space Optimization

## Intuition

The 2D DP uses `O(n^2)` memory.

But if you inspect the recurrence:

```text
if s[i] == s[j]:
    dp[i][j] = 2 + dp[i + 1][j - 1]
else:
    dp[i][j] = max(dp[i + 1][j], dp[i][j - 1])
```

you can see that while computing row `i`, you only need:

- values from row `i + 1`
- values already computed in the current row

That means we do not actually need the full 2D table.

We can compress it into 1D.

---

## Two-array interpretation

Use two arrays of size `n`:

- `dp[j]` = current row values, corresponding to intervals starting at `i`
- `dpPrev[j]` = previous row values, corresponding to intervals starting at `i + 1`

Then:

### If `s[i] == s[j]`

```text
dp[j] = dpPrev[j - 1] + 2
```

because `dpPrev[j - 1]` represents `dp[i + 1][j - 1]`.

### Otherwise

```text
dp[j] = max(dpPrev[j], dp[j - 1])
```

because:

- `dpPrev[j]` represents `dp[i + 1][j]`
- `dp[j - 1]` represents `dp[i][j - 1]`

After finishing row `i`, copy `dp` into `dpPrev`.

---

## Algorithm

1. Let `n = s.length()`.
2. Create arrays:

```text
dp[n], dpPrev[n]
```

3. Iterate `i` from `n - 1` down to `0`:
   - set `dp[i] = 1`
   - iterate `j` from `i + 1` to `n - 1`
   - apply recurrence
   - copy `dp` to `dpPrev`
4. Return `dp[n - 1]`.

---

## Java implementation

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[] dp = new int[n];
        int[] dpPrev = new int[n];

        for (int i = n - 1; i >= 0; --i) {
            dp[i] = 1;
            for (int j = i + 1; j < n; ++j) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[j] = dpPrev[j - 1] + 2;
                } else {
                    dp[j] = Math.max(dpPrev[j], dp[j - 1]);
                }
            }
            dpPrev = dp.clone();
        }

        return dp[n - 1];
    }
}
```

---

## Why this works

At row `i`:

- `dpPrev[j]` stores what used to be `dp[i + 1][j]`
- `dpPrev[j - 1]` stores what used to be `dp[i + 1][j - 1]`
- `dp[j - 1]` has already been updated in the current row, so it stores `dp[i][j - 1]`

Those are exactly the values needed by the recurrence.

So the 1D transition is mathematically equivalent to the 2D DP.

---

## Complexity

### Time complexity

We still iterate over all `(i, j)` pairs.

So:

```text
Time = O(n^2)
```

### Space complexity

We only use two arrays of length `n`.

So:

```text
Space = O(n)
```

This is the best among the three approaches.

---

# Worked example

## Example 1

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

Reasoning with the recurrence:

- `s[0] == s[4] == 'b'`
- so:

```text
LPS(0, 4) = 2 + LPS(1, 3)
```

Inside substring `"bba"` has LPS length `2` (`"bb"`), so:

```text
LPS(0, 4) = 2 + 2 = 4
```

---

## Example 2

```text
s = "cbbd"
```

Expected answer:

```text
2
```

A longest palindromic subsequence is:

```text
"bb"
```

Reasoning:

- ends `'c'` and `'d'` do not match
- so answer is:

```text
max(LPS(1, 3), LPS(0, 2))
```

Both eventually produce `2`.

So final answer is `2`.

---

# Alternative viewpoint: LCS with reversed string

Another valid way to think about this problem is:

The longest palindromic subsequence in `s` is equal to the **longest common subsequence** between:

```text
s
reverse(s)
```

This is true because a palindrome reads the same forward and backward.

So this problem can also be solved using LCS DP.

However, the direct interval DP is usually more natural and cleaner for this problem.

---

# Why interval DP is usually preferred

The palindrome condition directly compares the two ends of a substring.

That makes the interval recurrence feel very natural:

- if ends match, use them
- otherwise skip one side

So for explanation and interviews, interval DP is usually the best presentation.

---

# Common mistakes

## 1. Confusing subsequence with substring

A subsequence does not need to be contiguous.

This is the most common misunderstanding.

---

## 2. Using longest palindromic substring logic

That solves a different problem entirely.

Longest palindromic substring is about contiguous ranges. This problem is not.

---

## 3. Filling bottom-up DP in the wrong order

If `dp[i + 1][j - 1]`, `dp[i + 1][j]`, or `dp[i][j - 1]` are not ready yet, the recurrence breaks.

---

## 4. Forgetting the empty interval base case in recursion

In the top-down approach, `i > j` must return `0`.

Without that, the equal-ends case for short substrings breaks.

---

## 5. Mishandling the 1D optimization

In the optimized version, it is crucial to understand what each array entry means:

- `dpPrev[j]` is from row `i + 1`
- `dp[j - 1]` is already updated for the current row

If that meaning is lost, the optimization becomes error-prone.

---

# Comparison of the 3 approaches

## Approach 1: Top-Down DP

### Pros

- very intuitive
- directly matches the recurrence
- easy to derive

### Cons

- recursion overhead
- uses `O(n^2)` memo plus recursion stack

---

## Approach 2: Bottom-Up 2D DP

### Pros

- iterative
- explicit DP table
- often easiest to debug

### Cons

- still uses `O(n^2)` memory

---

## Approach 3: Bottom-Up 1D DP

### Pros

- same `O(n^2)` time
- only `O(n)` space
- best final optimization

### Cons

- more subtle to understand
- requires careful interpretation of current and previous rows

---

# Final recurrence summary

Let:

```text
LPS(i, j) = longest palindromic subsequence length in s[i..j]
```

Base:

```text
if i > j: return 0
if i == j: return 1
```

Transition:

```text
if s[i] == s[j]:
    LPS(i, j) = 2 + LPS(i + 1, j - 1)
else:
    LPS(i, j) = max(LPS(i + 1, j), LPS(i, j - 1))
```

Final answer:

```text
LPS(0, n - 1)
```

---

# Final takeaway

This problem is a classic interval dynamic programming problem.

The central idea is simple:

- if the two ends match, include them
- if they do not match, skip one end and take the better result

That leads to three standard DP implementations:

1. **Top-down memoization**
2. **Bottom-up 2D DP**
3. **Bottom-up 1D optimized DP**

## Final complexity summary

Let `n = s.length()`.

### Top-down DP

```text
Time:  O(n^2)
Space: O(n^2)
```

### Bottom-up 2D DP

```text
Time:  O(n^2)
Space: O(n^2)
```

### Bottom-up 1D DP

```text
Time:  O(n^2)
Space: O(n)
```

For explanation, the 2D interval DP is usually the cleanest.
For a polished final solution, the 1D optimized DP is the most memory-efficient.
