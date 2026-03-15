# 1682. Longest Palindromic Subsequence II — Detailed Summary

## Problem

A subsequence of a string `s` is called a **good palindromic subsequence** if:

1. it is a subsequence of `s`
2. it is a palindrome
3. it has **even length**
4. no two consecutive characters are equal, **except the two middle characters**

We need the **length** of the longest such subsequence.

---

## Why this problem is tricky

This is not the ordinary longest palindromic subsequence problem.

A normal palindromic subsequence only cares about:

- matching characters on both ends

But here we also have the extra adjacency restriction:

- equal consecutive characters are forbidden
- except exactly at the middle pair

That means a standard LPS DP is not enough.

The subtle issue is this:

When we wrap a smaller palindrome with the same character on both ends, we must ensure we do **not** create equal adjacent characters at the boundary between the outer pair and the inner palindrome.

So we need more information than just “best answer in substring `s[i..j]`”.

---

# Main insight

To safely extend a palindrome, we must know what character is sitting on its outer ends.

That leads to the key DP state:

```text
dp[i][j][c]
```

where:

- `i`, `j` define the substring `s[i..j]`
- `c` is a letter from `'a'` to `'z'`
- `dp[i][j][c]` = length of the longest good palindromic subsequence inside `s[i..j]`
  whose **first and last characters are both `c`**

This “outer character remembered” state is exactly what makes the restriction manageable.

---

# Why remembering the outer character is enough

Suppose we want to build a good palindrome that starts and ends with character `c`.

If we choose `c` on both ends, then the inside palindrome must:

- be empty, giving `"cc"` of length `2`
- or have a **different outer character** `d != c`

Why `d != c`?

Because if the inside palindrome also started and ended with `c`, then after wrapping we would create:

```text
c c ... c c
```

and the adjacent `cc` at the boundary would be illegal, unless it were the center pair.

But these boundary equalities are not the middle pair in general, so they are forbidden.

So the inner palindrome must begin and end with a different letter.

That directly motivates the transition.

---

# DP definition

Let:

```text
dp[i][j][c]
```

be the longest valid good palindromic subsequence in `s[i..j]` whose first and last characters are both the letter `c`.

If no such subsequence exists, the value is `0`.

---

# Transition

For each substring `s[i..j]` and each character `c`, there are two broad options.

## Option 1: skip one side

We may not use both `s[i]` and `s[j]` as the outer pair of the answer with outer letter `c`.

Then we inherit from smaller ranges:

```text
dp[i][j][c] = max(dp[i+1][j][c], dp[i][j-1][c])
```

This is the standard “ignore left or ignore right” idea from substring DP.

---

## Option 2: use both ends

If:

```text
s[i] == s[j] == c
```

then we can form a palindrome with outer letter `c`.

There are two subcases.

### Subcase A: use only these two characters

Then we form:

```text
"cc"
```

which has length `2`.

This is always valid because the middle pair is allowed to be equal.

So:

```text
dp[i][j][c] = max(dp[i][j][c], 2)
```

### Subcase B: wrap a smaller valid palindrome

If inside `s[i+1..j-1]` we have a valid palindrome with outer character `d != c`, then wrapping it with `c` is valid:

```text
c + (inner palindrome with outer d) + c
```

So:

```text
dp[i][j][c] = max(dp[i][j][c], 2 + dp[i+1][j-1][d]) for all d != c
```

Putting it together:

```text
if s[i] == s[j] == c:
    dp[i][j][c] = max(
        dp[i][j][c],
        2,
        2 + max(dp[i+1][j-1][d]) for all d != c
    )
```

---

# Why `"cc"` is valid

A length-2 palindrome like `"aa"` is valid because:

- it has even length
- it is a palindrome
- the only adjacent equal pair is also the middle pair

So length `2` is the natural base construction.

---

# Base cases

We do not need heavy explicit base initialization.

Why?

- a single character cannot form an even-length palindrome
- so substrings of length `1` contribute nothing
- `dp` naturally starts with zeros

Then for substrings of length `2` and above, the transition can produce `2` when the two ends match.

That is enough.

---

# Order of computation

We compute by increasing substring length:

- length `2`
- length `3`
- ...
- length `n`

This works because `dp[i][j]` depends only on:

- `dp[i+1][j]`
- `dp[i][j-1]`
- `dp[i+1][j-1]`

all of which correspond to smaller substrings.

---

# Full Java solution

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        if (n < 2) return 0;

        char[] a = s.toCharArray();
        int[][][] dp = new int[n][n][26];

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len <= n; i++) {
                int j = i + len - 1;

                // Inherit answers by skipping left or right.
                for (int c = 0; c < 26; c++) {
                    dp[i][j][c] = 0;
                    if (i + 1 <= j) {
                        dp[i][j][c] = Math.max(dp[i][j][c], dp[i + 1][j][c]);
                    }
                    if (i <= j - 1) {
                        dp[i][j][c] = Math.max(dp[i][j][c], dp[i][j - 1][c]);
                    }
                }

                // Try to use both ends.
                if (a[i] == a[j]) {
                    int c = a[i] - 'a';

                    // Base palindrome "cc"
                    dp[i][j][c] = Math.max(dp[i][j][c], 2);

                    // Wrap a smaller palindrome whose outer letter is different.
                    if (i + 1 <= j - 1) {
                        for (int d = 0; d < 26; d++) {
                            if (d == c) continue;
                            if (dp[i + 1][j - 1][d] > 0) {
                                dp[i][j][c] = Math.max(dp[i][j][c], 2 + dp[i + 1][j - 1][d]);
                            }
                        }
                    }
                }
            }
        }

        int ans = 0;
        for (int c = 0; c < 26; c++) {
            ans = Math.max(ans, dp[0][n - 1][c]);
        }
        return ans;
    }
}
```

---

# Cleaner recurrence summary

For each substring `s[i..j]` and letter `c`:

```text
dp[i][j][c] = max(dp[i+1][j][c], dp[i][j-1][c])
```

And if `s[i] == s[j] == c`:

```text
dp[i][j][c] = max(dp[i][j][c], 2)
dp[i][j][c] = max(dp[i][j][c], 2 + dp[i+1][j-1][d]) for all d != c
```

Final answer:

```text
max(dp[0][n-1][c]) for c in [0..25]
```

---

# Dry run 1

## Example

```text
s = "bbabab"
```

Expected answer:

```text
4
```

One valid good palindromic subsequence is:

```text
"baab"
```

Let us reason structurally.

Characters:

```text
index: 0 1 2 3 4 5
char : b b a b a b
```

We want an even palindrome with no equal consecutive letters except possibly the middle pair.

`"baab"` works:

- palindrome
- even length `4`
- adjacent pairs are `b-a`, `a-a`, `a-b`
- only the middle `aa` are equal, which is allowed

Why not `"bbbb"`?

Because equal adjacent characters would appear outside the center boundary.

The DP will discover `"baab"` by:

- taking outer `b ... b`
- finding inner `"aa"` with outer character `a`
- since `a != b`, wrapping is legal
- result becomes `2 + 2 = 4`

That is exactly why the `d != c` condition exists.

---

# Dry run 2

## Example

```text
s = "dcbccacdb"
```

Expected answer:

```text
4
```

One valid answer is:

```text
"dccd"
```

Check validity:

- palindrome
- even length
- adjacent pairs are `d-c`, `c-c`, `c-d`
- only the middle `cc` are equal, which is allowed

The DP can form this by:

- outer letter `d`
- inner length-2 palindrome `"cc"` with outer letter `c`
- since `c != d`, wrapping is valid
- total length `4`

---

# Why ordinary LPS DP fails

A normal LPS DP might try to build longer palindromes just from matching ends:

```text
if s[i] == s[j]:
    dp[i][j] = 2 + dp[i+1][j-1]
```

That is not enough here.

Example issue:

Suppose the inside palindrome already starts and ends with the same character as the outer one. Then wrapping would create forbidden equal adjacency near the boundaries.

So the DP must know the outer character of the inside palindrome.

That is the missing information ordinary LPS does not track.

---

# Alternative intuition

You can think of a valid good palindrome as layers.

Example:

```text
b a a b
```

The outer layer is `b ... b`.

The next inner layer is `a ... a`.

To remain valid, two adjacent layers cannot use the same character.

So the sequence of outer-pair characters, moving inward, must alternate between different letters, except the center is simply one pair like `aa` or `bb`.

That layering view matches the `d != c` transition exactly.

---

# Complexity analysis

Let `n = s.length()`.

There are:

- `O(n^2)` substrings
- `26` possible outer characters
- for each matched-end transition, we may scan all `26` letters to find valid inner outer characters

So:

## Time complexity

```text
O(n^2 * 26 * 26)
```

Since `26` is a constant, this is effectively:

```text
O(n^2)
```

For `n <= 250`, this is easily manageable.

## Space complexity

```text
O(n^2 * 26)
```

Again, effectively `O(n^2)` with a constant factor of `26`.

---

# Why this passes the constraints

Maximum `n` is only `250`.

So:

- `n^2 = 62500`
- multiplied by `26` and another small factor `26`

This is comfortably within limits for Java.

A more brute-force subsequence approach would be hopeless, because the number of subsequences is exponential.

The DP compresses the problem into polynomial time.

---

# Common mistakes

## 1. Using normal LPS recurrence

This ignores the adjacency restriction and overcounts invalid palindromes.

## 2. Forgetting the even-length condition

Odd-length palindromes are not allowed at all.

That is why the base is not `1`, unlike ordinary LPS.

## 3. Allowing inner outer letter equal to outer wrapping letter

That creates illegal adjacency at the boundary.

This is the most important bug to avoid.

Correct rule:

```text
when wrapping with c, inner outer letter must be d != c
```

## 4. Thinking `"bbbb"` should be valid

It is not.

Equal adjacent characters occur repeatedly, not just at the central pair.

---

# Compact Java version with the same logic

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        char[] a = s.toCharArray();
        int[][][] dp = new int[n][n][26];

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len <= n; i++) {
                int j = i + len - 1;

                for (int c = 0; c < 26; c++) {
                    dp[i][j][c] = Math.max(
                        i + 1 <= j ? dp[i + 1][j][c] : 0,
                        i <= j - 1 ? dp[i][j - 1][c] : 0
                    );
                }

                if (a[i] == a[j]) {
                    int c = a[i] - 'a';
                    dp[i][j][c] = Math.max(dp[i][j][c], 2);

                    if (i + 1 <= j - 1) {
                        for (int d = 0; d < 26; d++) {
                            if (d != c && dp[i + 1][j - 1][d] > 0) {
                                dp[i][j][c] = Math.max(dp[i][j][c], 2 + dp[i + 1][j - 1][d]);
                            }
                        }
                    }
                }
            }
        }

        int ans = 0;
        for (int c = 0; c < 26; c++) {
            ans = Math.max(ans, dp[0][n - 1][c]);
        }
        return ans;
    }
}
```

---

# Final takeaway

This problem looks like LPS at first, but it is not ordinary LPS.

The crucial extra restriction is:

- equal consecutive letters are forbidden except at the exact middle

That forces us to remember the outer character of the palindrome we are building.

So the right DP is:

```text
dp[i][j][c] = best good palindrome inside s[i..j] whose outer letter is c
```

Then:

- skip left or right as usual
- if both ends match `c`, either form `"cc"` or wrap an inner palindrome with outer letter `d != c`

That gives a clean and correct `O(n^2)` solution with small constants.
