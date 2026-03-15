# 3303. Find the Occurrence of First Almost Equal Substring

## Problem Statement

You are given two strings `s` and `pattern`.

A string `x` is called **almost equal** to a string `y` if you can change **at most one character** in `x` to make it identical to `y`.

Return the **smallest starting index** of a substring in `s` that is almost equal to `pattern`.

If no such substring exists, return `-1`.

A substring is a contiguous non-empty sequence of characters.

---

## Example 1

```text
Input:
s = "abcdefg"
pattern = "bcdffg"

Output:
1
```

Explanation:

The substring:

```text
s[1..6] = "bcdefg"
```

differs from `"bcdffg"` at only one position, so it is almost equal.

---

## Example 2

```text
Input:
s = "ababbababa"
pattern = "bacaba"

Output:
4
```

Explanation:

The substring:

```text
s[4..9] = "bababa"
```

can be turned into `"bacaba"` by changing one character.

---

## Example 3

```text
Input:
s = "abcd"
pattern = "dba"

Output:
-1
```

---

## Example 4

```text
Input:
s = "dde"
pattern = "d"

Output:
0
```

---

## Constraints

- `1 <= pattern.length < s.length <= 10^5`
- `s` and `pattern` consist only of lowercase English letters

---

## Follow-up

Could you solve the problem if at most `k` consecutive characters can be changed?

---

# Core Insight

For a starting index `i`, we compare:

```text
s[i .. i + m - 1]
```

with:

```text
pattern[0 .. m - 1]
```

where `m = pattern.length()`.

A substring is valid if the number of mismatched positions is at most `1`.

So the direct condition is:

> Find the smallest `i` such that the Hamming distance between
> `s[i .. i+m-1]` and `pattern` is at most `1`.

The challenge is doing this efficiently for all possible starting indices when `|s|` is up to `10^5`.

---

# Approach 1: Brute Force Comparison

## Intuition

The most direct solution is:

- try every starting index `i`
- compare `pattern` against the substring of `s` starting at `i`
- count mismatches
- if mismatches are at most `1`, return `i`

This is easy to understand but too slow in the worst case.

---

## Algorithm

1. For each starting index `i` from `0` to `s.length() - pattern.length()`
2. Compare all characters of `pattern` with `s[i + j]`
3. Count mismatches
4. If mismatch count is at most `1`, return `i`
5. If no such index exists, return `-1`

---

## Java Code

```java
class Solution {
    public int minStartingIndex(String s, String pattern) {
        int n = s.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int mismatches = 0;

            for (int j = 0; j < m; j++) {
                if (s.charAt(i + j) != pattern.charAt(j)) {
                    mismatches++;
                    if (mismatches > 1) {
                        break;
                    }
                }
            }

            if (mismatches <= 1) {
                return i;
            }
        }

        return -1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

There are `O(n)` starting positions, and each may compare `O(m)` characters:

```text
O(n * m)
```

This is too slow when both strings are large.

### Space Complexity

```text
O(1)
```

---

## Verdict

Correct but not scalable.

---

# Approach 2: Prefix and Suffix Match Length Arrays Using Z-Algorithm

## Intuition

A substring differs from `pattern` in at most one position if:

- a prefix of the substring matches a prefix of `pattern`
- a suffix of the substring matches a suffix of `pattern`
- together they cover all but at most one position

So for every starting index `i`, if we know:

- `pref[i]` = longest prefix match length between `s[i...]` and `pattern`
- `suff[i]` = longest suffix match length between `s[...i]` and `pattern` aligned from the end

then the substring starting at `i` is valid if:

```text
pref[i] + suff[i + m - 1] >= m - 1
```

Why?

Because the left matching block and right matching block together cover all positions except possibly one mismatched position.

This is the key optimal approach.

---

## How to Compute `pref`

We want, for every position `i` in `s`, the longest common prefix between:

```text
pattern
and
s[i...]
```

This is a classic Z-algorithm trick.

Build:

```text
pattern + "#" + s
```

Then Z-values in the `s` portion tell us exactly those match lengths.

---

## How to Compute `suff`

We also need suffix matches aligned from the end.

This can be converted into another prefix match problem by reversing both strings.

Let:

```text
rs = reverse(s)
rp = reverse(pattern)
```

Now compute longest common prefixes between `rp` and suffixes of `rs`.

Those correspond to suffix matches in the original strings.

---

## Validity Condition

For substring `s[i .. i+m-1]`:

- left match length = `pref[i]`
- right match length = `suff[i + m - 1]`

If:

```text
pref[i] == m
```

then it is an exact match, so valid immediately.

Otherwise, if:

```text
pref[i] + suff[i + m - 1] >= m - 1
```

then there is room for at most one mismatch.

---

## Java Code

```java
class Solution {
    public int minStartingIndex(String s, String pattern) {
        int n = s.length();
        int m = pattern.length();

        int[] pref = buildPrefixMatches(pattern, s);

        String rs = new StringBuilder(s).reverse().toString();
        String rp = new StringBuilder(pattern).reverse().toString();
        int[] revPref = buildPrefixMatches(rp, rs);

        int[] suff = new int[n];
        for (int j = 0; j < n; j++) {
            // original index j corresponds to reversed index n - 1 - j
            suff[j] = revPref[n - 1 - j];
        }

        for (int i = 0; i <= n - m; i++) {
            if (pref[i] >= m) {
                return i;
            }

            if (pref[i] + suff[i + m - 1] >= m - 1) {
                return i;
            }
        }

        return -1;
    }

    private int[] buildPrefixMatches(String pattern, String text) {
        String combined = pattern + "#" + text;
        int[] z = zFunction(combined);
        int m = pattern.length();
        int n = text.length();
        int[] res = new int[n];

        for (int i = 0; i < n; i++) {
            res[i] = Math.min(z[m + 1 + i], m);
        }

        return res;
    }

    private int[] zFunction(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

## Complexity Analysis

### Time Complexity

- one Z computation on `pattern + "#" + s`
- one Z computation on `reverse(pattern) + "#" + reverse(s)`
- one linear scan to test candidate positions

Total:

```text
O(n + m)
```

### Space Complexity

```text
O(n + m)
```

for the Z arrays and helper arrays.

---

## Verdict

This is the strongest exact solution.

---

# Approach 3: Rolling Hash + Binary Search of Mismatch Position

## Intuition

Another way is to compare substrings using hashing.

For a candidate start index `i`:

1. find the first mismatch between `s[i...]` and `pattern`
2. then check whether everything after that mismatch matches exactly

If yes, total mismatches are at most one.

To find the first mismatch quickly, use rolling hash and binary search on longest common prefix.

This is elegant and fast, though more complicated and hash-based rather than purely exact combinatorial matching.

---

## High-Level Idea

Precompute rolling hash for:

- `s`
- `pattern`

For each starting index `i`:

- find longest common prefix length `L` between `s[i...]` and `pattern`
- if `L == m`, exact match
- otherwise compare the suffixes after skipping one mismatched position:
  - compare `s[i + L + 1 .. i + m - 1]`
  - with `pattern[L + 1 .. m - 1]`

If they match, answer is `i`.

---

## Java Code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 911382323L;

    public int minStartingIndex(String s, String pattern) {
        RollingHash hs = new RollingHash(s);
        RollingHash hp = new RollingHash(pattern);

        int n = s.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int lcp = longestCommonPrefix(hs, hp, i, m);

            if (lcp == m) {
                return i;
            }

            if (equalRange(hs, hp, i + lcp + 1, m - (lcp + 1), lcp + 1)) {
                return i;
            }
        }

        return -1;
    }

    private int longestCommonPrefix(RollingHash hs, RollingHash hp, int startS, int m) {
        int lo = 0, hi = m;
        while (lo < hi) {
            int mid = (lo + hi + 1) >>> 1;
            if (hs.getHash(startS, startS + mid - 1) == hp.getHash(0, mid - 1)) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    private boolean equalRange(RollingHash hs, RollingHash hp, int startS, int len, int startP) {
        if (len <= 0) {
            return true;
        }
        return hs.getHash(startS, startS + len - 1) == hp.getHash(startP, startP + len - 1);
    }

    static class RollingHash {
        long[] pref;
        long[] pow;

        RollingHash(String s) {
            int n = s.length();
            pref = new long[n + 1];
            pow = new long[n + 1];
            pow[0] = 1;

            for (int i = 0; i < n; i++) {
                pref[i + 1] = (pref[i] * BASE + s.charAt(i)) % MOD;
                pow[i + 1] = (pow[i] * BASE) % MOD;
            }
        }

        long getHash(int l, int r) {
            if (l > r) {
                return 0;
            }
            long val = (pref[r + 1] - pref[l] * pow[r - l + 1]) % MOD;
            if (val < 0) {
                val += MOD;
            }
            return val;
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

For each starting position, we do a binary search over prefix length:

```text
O(log m)
```

and constant-time hash checks.

Total:

```text
O((n - m + 1) * log m)
```

### Space Complexity

```text
O(n + m)
```

---

## Verdict

Very good, but more complex and hash-based.

The Z-based method is cleaner and fully deterministic.

---

# Approach 4: KMP-Style Thinking (Why Plain KMP Is Not Enough)

## Intuition

This problem looks like substring matching, so it is natural to think of KMP.

But ordinary KMP is built for exact matches.
Here we allow one mismatch anywhere.

That changes the structure significantly.

You can extend KMP-like ideas or automata for approximate matching, but for this problem the prefix/suffix matching decomposition is much simpler.

So while KMP inspires the thinking, plain KMP is not the clean answer here.

---

# Why the Prefix + Suffix Coverage Condition Works

Suppose the substring starting at `i` differs from `pattern` in at most one position.

Then:

- from the left, the first `pref[i]` characters match
- from the right, the last `suff[i+m-1]` characters match

If there is at most one bad position, then left and right matches together cover at least:

```text
m - 1
```

positions.

Conversely, if they cover at least `m - 1`, then at most one position is uncovered, so at most one mismatch exists.

That gives the exact criterion:

```text
pref[i] + suff[i + m - 1] >= m - 1
```

This is the main proof idea.

---

# Common Mistakes

## 1. Using edit distance logic

This is **not** insertion/deletion/edit distance.

The substring and the pattern have equal length, and only substitutions are allowed.

So the problem is about **Hamming distance ≤ 1**.

---

## 2. Forgetting exact matches are also valid

“At most one character” includes zero changes.

---

## 3. Off-by-one errors in suffix indexing

When mapping reversed-string match lengths back to original indices, index conversion must be handled carefully.

---

## 4. Thinking overlap of left and right matched regions is a problem

It is not a problem.

Overlap simply means even fewer mismatches.

---

# Follow-up: At Most `k` Consecutive Characters Can Be Changed

The follow-up changes the rule from:

```text
at most one mismatched position
```

to:

```text
at most k consecutive positions may be changed
```

That means the substring is valid if all mismatches lie inside one contiguous block of length at most `k`.

A natural extension of the same prefix/suffix idea is:

For start index `i`, the substring is valid if there exists a block `[L, R]` such that:

- prefix up to `L-1` matches
- suffix from `R+1` onward matches
- `R - L + 1 <= k`

Equivalently, if the first mismatch from the left and the first mismatch from the right leave a gap of length at most `k`.

This can also be handled using prefix/suffix match arrays.

So the same structural idea generalizes well.

---

# Final Recommended Solution

Use:

- Z-algorithm to compute longest prefix matches at every alignment
- Z-algorithm on reversed strings to compute suffix matches
- test whether the left and right matching segments cover all but at most one position

---

## Clean Final Java Solution

```java
class Solution {
    public int minStartingIndex(String s, String pattern) {
        int n = s.length();
        int m = pattern.length();

        int[] pref = buildMatches(pattern, s);

        String rs = new StringBuilder(s).reverse().toString();
        String rp = new StringBuilder(pattern).reverse().toString();
        int[] rev = buildMatches(rp, rs);

        int[] suff = new int[n];
        for (int i = 0; i < n; i++) {
            suff[i] = rev[n - 1 - i];
        }

        for (int i = 0; i <= n - m; i++) {
            if (pref[i] >= m || pref[i] + suff[i + m - 1] >= m - 1) {
                return i;
            }
        }

        return -1;
    }

    private int[] buildMatches(String pattern, String text) {
        String combined = pattern + "#" + text;
        int[] z = zFunction(combined);
        int m = pattern.length();
        int[] ans = new int[text.length()];

        for (int i = 0; i < text.length(); i++) {
            ans[i] = Math.min(z[m + 1 + i], m);
        }

        return ans;
    }

    private int[] zFunction(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

# Complexity Summary

## Brute Force

- Time: `O(n * m)`
- Space: `O(1)`

## Rolling Hash + Binary Search

- Time: `O((n - m + 1) log m)`
- Space: `O(n + m)`

## Z-Algorithm Prefix/Suffix Method

- Time: `O(n + m)`
- Space: `O(n + m)`

---

# Interview Summary

The substring is valid if it differs from `pattern` in at most one position.

So for every alignment, we want to know:

- how many characters match from the left
- how many characters match from the right

If these two matching segments cover at least `m - 1` positions, then at most one mismatch exists.

Z-algorithm gives both arrays efficiently, making the whole solution linear.
