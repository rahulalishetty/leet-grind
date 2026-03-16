# Count Different Palindromic Subsequences — Exhaustive Solution Notes

## Overview

This problem asks us to count the number of **distinct palindromic subsequences** in a string.

Two things make it difficult:

1. We are counting **subsequences**, not substrings.
2. We need the count of **distinct** palindromes, not just all palindromic occurrences.

That means straightforward recursion or standard palindrome DP is not enough.
We must carefully avoid double-counting.

The approaches here rely on dynamic programming and exploit a key special constraint often associated with this problem:

```text
S consists only of the characters 'a', 'b', 'c', and 'd'
```

That limited alphabet is what makes these `O(N^2)` solutions possible.

This write-up explains two accepted approaches in detail:

1. **Dynamic Programming using a 3D array**
2. **Dynamic Programming using a 2D array with next/previous occurrence tables**

---

## Problem Statement

Given a string `S`, return the number of **different non-empty palindromic subsequences** in `S`.

Because the answer may be very large, return it modulo:

```text
10^9 + 7
```

A subsequence is obtained by deleting zero or more characters without changing the order of the remaining characters.

A palindrome reads the same forward and backward.

---

## Why This Problem Is Hard

Suppose the string is:

```text
bccb
```

The distinct palindromic subsequences include:

```text
b
c
bb
cc
bcb
bccb
```

Notice:

- the same palindrome can be formed in multiple ways as a subsequence
- but it should be counted only once

So this is not just counting palindromic subsequences.
It is counting **unique values** among them.

That uniqueness requirement is what makes the problem challenging.

---

# Approach 1: Dynamic Programming using 3D Array

## Core Intuition

Since the string uses only four characters:

```text
a, b, c, d
```

we can classify palindromic subsequences by their starting and ending character.

Let:

```text
dp[x][i][j]
```

denote:

> the number of distinct palindromic subsequences in substring `S[i...j]`
> that start and end with the character `'a' + x`

where:

- `x = 0` means `'a'`
- `x = 1` means `'b'`
- `x = 2` means `'c'`
- `x = 3` means `'d'`

This is the key state.

By separating answers according to the outer character, we can avoid double-counting.

---

## Why This State Helps

Every palindrome in `S[i...j]` must belong to exactly one of these four categories:

- starts and ends with `'a'`
- starts and ends with `'b'`
- starts and ends with `'c'`
- starts and ends with `'d'`

So if we compute each category separately and add them, we get the total number of distinct palindromic subsequences.

---

## State Definition

Let:

```text
dp[x][i][j]
```

be the number of distinct palindromic subsequences in substring `S[i...j]` such that:

```text
first character = last character = ('a' + x)
```

---

## Transition Cases

For a fixed character `c = 'a' + x`, consider substring `S[i...j]`.

### Case 1: `S[i] != c`

Then any palindrome counted in `dp[x][i][j]` must lie entirely in `S[i+1...j]`.

So:

```text
dp[x][i][j] = dp[x][i+1][j]
```

---

### Case 2: `S[j] != c`

Similarly, then any valid palindrome must lie entirely in `S[i...j-1]`.

So:

```text
dp[x][i][j] = dp[x][i][j-1]
```

---

### Case 3: `S[i] == S[j] == c`

Now we have the most interesting case.

If both ends are `c`, then the palindromes of type `c...c` include:

1. the palindrome `"c"`
2. the palindrome `"cc"`
3. every palindrome from the inner substring `S[i+1...j-1]`, wrapped with `c` on both ends

That gives:

```text
dp[x][i][j] = 2 + sum(dp[m][i+1][j-1]) for m in {0,1,2,3}
```

The `2` corresponds to:

```text
"c"
"cc"
```

and the inner palindromes produce:

```text
c + innerPalindrome + c
```

which are also distinct palindromes of type `c...c`.

---

## Special Small Case: `j == i + 1`

If the substring length is exactly 2 and both characters are equal to `c`, then the distinct palindromes are simply:

```text
"c"
"cc"
```

So:

```text
dp[x][i][j] = 2
```

---

## Base Case: Single Character

If `i == j`, then the substring is one character long.

For fixed `x`:

- if `S[i] == 'a' + x`, then:
  ```text
  dp[x][i][i] = 1
  ```
  because the single-character palindrome exists
- otherwise:
  ```text
  dp[x][i][i] = 0
  ```

---

## Final Answer

The total number of distinct palindromic subsequences in the full string is:

```text
dp[0][0][n-1] + dp[1][0][n-1] + dp[2][0][n-1] + dp[3][0][n-1]
```

modulo `10^9 + 7`.

---

## Java Implementation — 3D DP

```java
class Solution {
    public int countPalindromicSubsequences(String S) {
        int n = S.length();
        int mod = 1000000007;
        int[][][] dp = new int[4][n][n];

        for (int i = n - 1; i >= 0; --i) {
            for (int j = i; j < n; ++j) {
                for (int k = 0; k < 4; ++k) {
                    char c = (char) ('a' + k);

                    if (j == i) {
                        if (S.charAt(i) == c) {
                            dp[k][i][j] = 1;
                        } else {
                            dp[k][i][j] = 0;
                        }
                    } else {
                        if (S.charAt(i) != c) {
                            dp[k][i][j] = dp[k][i + 1][j];
                        } else if (S.charAt(j) != c) {
                            dp[k][i][j] = dp[k][i][j - 1];
                        } else {
                            if (j == i + 1) {
                                dp[k][i][j] = 2;
                            } else {
                                dp[k][i][j] = 2;
                                for (int m = 0; m < 4; ++m) {
                                    dp[k][i][j] += dp[m][i + 1][j - 1];
                                    dp[k][i][j] %= mod;
                                }
                            }
                        }
                    }
                }
            }
        }

        int ans = 0;
        for (int k = 0; k < 4; ++k) {
            ans += dp[k][0][n - 1];
            ans %= mod;
        }

        return ans;
    }
}
```

---

## Walkthrough Idea for the 3D DP

Suppose we are evaluating palindromes in a substring `S[i...j]`.

For each of the four letters separately:

- if the letter does not appear at one end, shrink the interval
- if it appears at both ends, create:
  - the single-letter palindrome
  - the double-letter palindrome
  - and wrap every inner palindrome with this letter

This decomposition ensures distinctness because each palindrome is categorized by its outermost character.

---

## Complexity Analysis — 3D DP

Let `N = S.length()`.

### Time Complexity

We fill:

- `N × N` substrings
- for each, 4 character categories
- and in the matching-both-ends case, loop over 4 inner categories

So the total time complexity is:

```text
O(N^2)
```

The factor 4 is constant.

---

### Space Complexity

The DP table has size:

```text
4 × N × N
```

So the space complexity is:

```text
O(N^2)
```

Again, the factor 4 is ignored as a constant.

---

# Approach 2: Dynamic Programming using 2D Array

## Core Intuition

This approach is more compact and elegant.

Instead of explicitly splitting by outer character in a 3D DP, we define:

```text
dp(i, j)
```

as:

> the number of distinct palindromic subsequences in `S[i...j]`,
> including the empty palindrome

Then we use precomputed arrays:

- `nxt[i][k]` = next occurrence of character `k` at or after index `i`
- `prv[i][k]` = previous occurrence of character `k` at or before index `i`

These let us efficiently find the first and last occurrence of each character inside any interval.

---

## Why Include the Empty Palindrome?

Including the empty palindrome simplifies the recurrence.

At the very end, we subtract `1` to exclude it from the final answer.

That is why the code returns:

```java
dp(0, N - 1) - 1
```

---

## State Definition

Let:

```text
dp(i, j)
```

denote:

> the number of distinct palindromic subsequences in `S[i...j]`,
> including the empty subsequence `""`

---

## Key Observation

Every non-empty palindrome in `S[i...j]` begins and ends with one of:

```text
a, b, c, d
```

So for each character `k` in `{a,b,c,d}`:

- find its first occurrence `i0` in `S[i...j]`
- find its last occurrence `j0` in `S[i...j]`

Then:

### If the character does not occur

It contributes nothing.

### If it occurs once

It contributes exactly one palindrome:

```text
"k"
```

### If it occurs at least twice

Then we get:

- the single-letter palindrome `"k"`
- every palindrome formed by wrapping inner palindromes in `S[i0+1...j0-1]` with `k`

So the contribution is:

```text
1 + dp(i0 + 1, j0 - 1)
```

where `dp` already includes the empty palindrome, which corresponds to `"kk"` after wrapping.

That is why the recurrence works neatly.

---

## Preprocessing `nxt` and `prv`

We build two tables:

### `nxt[i][k]`

The next occurrence of character `k` starting from index `i`.

### `prv[i][k]`

The previous occurrence of character `k` up to index `i`.

These allow us to answer:

> Where is the first / last occurrence of a given character inside interval `[i, j]`?

in constant time.

---

## Recurrence

Initialize:

```text
ans = 1
```

for the empty palindrome.

Then for each character `k` in `{a,b,c,d}`:

- let `i0 = nxt[i][k]`
- let `j0 = prv[j][k]`

### If `k` appears in `[i, j]`

Then `ans++` for the single-character palindrome.

### If `k` appears at least twice

That is, if:

```text
i0 < j0
```

then add:

```text
dp(i0 + 1, j0 - 1)
```

This counts all palindromes formed by wrapping inner palindromes with `k`.

Take modulo at each step.

---

## Java Implementation — 2D DP

```java
import java.util.*;

class Solution {
    int[][] memo, prv, nxt;
    byte[] A;
    int MOD = 1_000_000_007;

    public int countPalindromicSubsequences(String S) {
        int N = S.length();
        prv = new int[N][4];
        nxt = new int[N][4];
        memo = new int[N][N];

        for (int[] row : prv) {
            Arrays.fill(row, -1);
        }
        for (int[] row : nxt) {
            Arrays.fill(row, -1);
        }

        A = new byte[N];
        int ix = 0;
        for (char c : S.toCharArray()) {
            A[ix++] = (byte) (c - 'a');
        }

        int[] last = new int[4];
        Arrays.fill(last, -1);
        for (int i = 0; i < N; ++i) {
            last[A[i]] = i;
            for (int k = 0; k < 4; ++k) {
                prv[i][k] = last[k];
            }
        }

        Arrays.fill(last, -1);
        for (int i = N - 1; i >= 0; --i) {
            last[A[i]] = i;
            for (int k = 0; k < 4; ++k) {
                nxt[i][k] = last[k];
            }
        }

        return dp(0, N - 1) - 1;
    }

    public int dp(int i, int j) {
        if (memo[i][j] > 0) {
            return memo[i][j];
        }

        int ans = 1; // empty palindrome

        if (i <= j) {
            for (int k = 0; k < 4; ++k) {
                int i0 = nxt[i][k];
                int j0 = prv[j][k];

                if (i <= i0 && i0 <= j) {
                    ans++;
                }
                if (-1 < i0 && i0 < j0) {
                    ans += dp(i0 + 1, j0 - 1);
                }
                if (ans >= MOD) {
                    ans -= MOD;
                }
            }
        }

        memo[i][j] = ans;
        return ans;
    }
}
```

---

## Why the 2D Recurrence Works

For a fixed outer character `k`:

- if it appears once in the interval, we get exactly one palindrome: `"k"`
- if it appears twice or more, we get:
  - `"k"`
  - `"kk"` from wrapping the empty palindrome
  - and all larger wrapped palindromes from the inner interval

Since `dp(i0 + 1, j0 - 1)` includes the empty palindrome, the recurrence automatically handles all of these cases correctly.

That is why the formulation is elegant.

---

## Complexity Analysis — 2D DP

### Time Complexity

Building `prv` and `nxt` takes:

```text
O(N)
```

up to constant factor 4.

The memoized recursion has at most:

```text
O(N^2)
```

states.

Each state does constant work over 4 characters.

So the total time complexity is:

```text
O(N^2)
```

---

### Space Complexity

The memo table has size:

```text
O(N^2)
```

The `prv` and `nxt` tables are `O(N)` each up to constant factor 4.

So the dominant space complexity is:

```text
O(N^2)
```

---

# Comparing the Two Accepted Approaches

## 3D DP

### Strengths

- very direct once the outer-character categorization is understood
- recurrence is explicit and structured
- often easier to reason about distinctly by first/last character

### Weaknesses

- uses a 3D DP table
- state definition looks more intimidating at first

---

## 2D DP with `nxt` / `prv`

### Strengths

- more compact
- elegant handling of distinctness
- only needs a 2D memo table

### Weaknesses

- the recurrence is subtler
- requires understanding how the empty palindrome is being used

---

# Common Mistakes

## 1. Counting all palindromic subsequences instead of distinct ones

Different index choices can form the same palindrome string.

We must count unique palindrome values, not occurrences.

---

## 2. Using standard palindrome DP for substrings

This is a subsequence problem, not a substring problem.
The standard substring-palindrome DP does not solve it.

---

## 3. Forgetting the alphabet restriction

These accepted `O(N^2)` solutions rely crucially on the fact that the string uses only:

```text
a, b, c, d
```

That small alphabet is what makes the constant-factor transitions possible.

---

## 4. Mishandling modulo arithmetic

Because many additions happen repeatedly, always apply modulo carefully.

---

## 5. Forgetting to subtract 1 in the 2D approach

That DP includes the empty palindrome.
So the final answer must be:

```text
dp(0, N - 1) - 1
```

---

# Final Summary

## Problem Type

This is a hard dynamic programming problem about counting **distinct palindromic subsequences**.

---

## Accepted Approach 1: 3D DP

### State

```text
dp[x][i][j]
```

= number of distinct palindromic subsequences in `S[i...j]` that start and end with character `'a' + x`.

### Complexity

- Time: `O(N^2)`
- Space: `O(N^2)`

---

## Accepted Approach 2: 2D DP + next/prev occurrence tables

### State

```text
dp(i, j)
```

= number of distinct palindromic subsequences in `S[i...j]`, including the empty palindrome.

### Complexity

- Time: `O(N^2)`
- Space: `O(N^2)`

---

# Best Final Java Solution (3D DP)

```java
class Solution {
    public int countPalindromicSubsequences(String S) {
        int n = S.length();
        int mod = 1000000007;
        int[][][] dp = new int[4][n][n];

        for (int i = n - 1; i >= 0; --i) {
            for (int j = i; j < n; ++j) {
                for (int k = 0; k < 4; ++k) {
                    char c = (char) ('a' + k);

                    if (j == i) {
                        dp[k][i][j] = (S.charAt(i) == c) ? 1 : 0;
                    } else {
                        if (S.charAt(i) != c) {
                            dp[k][i][j] = dp[k][i + 1][j];
                        } else if (S.charAt(j) != c) {
                            dp[k][i][j] = dp[k][i][j - 1];
                        } else {
                            if (j == i + 1) {
                                dp[k][i][j] = 2;
                            } else {
                                dp[k][i][j] = 2;
                                for (int m = 0; m < 4; ++m) {
                                    dp[k][i][j] += dp[m][i + 1][j - 1];
                                    dp[k][i][j] %= mod;
                                }
                            }
                        }
                    }
                }
            }
        }

        int ans = 0;
        for (int k = 0; k < 4; ++k) {
            ans += dp[k][0][n - 1];
            ans %= mod;
        }

        return ans;
    }
}
```

---

# Best Final Java Solution (2D DP)

```java
class Solution {
    int[][] memo, prv, nxt;
    byte[] A;
    int MOD = 1_000_000_007;

    public int countPalindromicSubsequences(String S) {
        int N = S.length();
        prv = new int[N][4];
        nxt = new int[N][4];
        memo = new int[N][N];

        for (int[] row : prv) Arrays.fill(row, -1);
        for (int[] row : nxt) Arrays.fill(row, -1);

        A = new byte[N];
        for (int i = 0; i < N; i++) {
            A[i] = (byte) (S.charAt(i) - 'a');
        }

        int[] last = new int[4];
        Arrays.fill(last, -1);
        for (int i = 0; i < N; ++i) {
            last[A[i]] = i;
            for (int k = 0; k < 4; ++k) prv[i][k] = last[k];
        }

        Arrays.fill(last, -1);
        for (int i = N - 1; i >= 0; --i) {
            last[A[i]] = i;
            for (int k = 0; k < 4; ++k) nxt[i][k] = last[k];
        }

        return dp(0, N - 1) - 1;
    }

    public int dp(int i, int j) {
        if (memo[i][j] > 0) return memo[i][j];

        int ans = 1; // empty palindrome

        if (i <= j) {
            for (int k = 0; k < 4; ++k) {
                int i0 = nxt[i][k];
                int j0 = prv[j][k];

                if (i <= i0 && i0 <= j) ans++;
                if (-1 < i0 && i0 < j0) ans += dp(i0 + 1, j0 - 1);

                if (ans >= MOD) ans -= MOD;
            }
        }

        memo[i][j] = ans;
        return ans;
    }
}
```

These are the standard exhaustive accepted solutions for the problem.
