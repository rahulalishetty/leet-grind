# 471. Encode String with Shortest Length

## Problem Statement

Given a string `s`, encode it so that the encoded string has the shortest possible length.

The encoding format is:

```text
k[encoded_string]
```

where:

- `encoded_string` is repeated exactly `k` times
- `k` is a positive integer

If encoding does **not** make the string shorter, then do **not** encode it.

If there are multiple shortest answers, returning any one of them is valid.

---

## Example 1

```text
Input:  s = "aaa"
Output: "aaa"
```

Explanation:

Encoding it as `"3[a]"` has length `4`, which is longer than `"aaa"` of length `3`, so we keep the original string.

---

## Example 2

```text
Input:  s = "aaaaa"
Output: "5[a]"
```

Explanation:

`"5[a]"` has length `4`, which is shorter than `"aaaaa"` of length `5`.

---

## Example 3

```text
Input:  s = "aaaaaaaaaa"
Output: "10[a]"
```

Explanation:

`"10[a]"` is shorter than `"aaaaaaaaaa"`.

Other shortest encodings such as `"a9[a]"` or `"9[a]a"` may also exist, but any shortest valid answer is acceptable.

---

## Constraints

- `1 <= s.length <= 150`
- `s` consists only of lowercase English letters

---

# Core Insight

This is not just a pattern-detection problem.

It is an **optimization over all substrings** problem.

For every substring, we have to decide between multiple possibilities:

1. keep it as-is
2. encode it as repeated blocks, if possible
3. split it into two parts and encode each part optimally

That combination of:

- overlapping subproblems
- “shortest result”
- all substring intervals

strongly suggests **dynamic programming**.

---

# What Makes This Problem Tricky

Suppose the string is:

```text
"abababab"
```

It can be seen as:

- `"4[ab]"`
- or maybe split into `"abab" + "abab"`
- and then each half might also be encoded

So the best encoding for a whole substring may come from:

- direct repetition compression
- or from splitting into smaller optimal encodings

You must consider both.

---

# Approach 1: Brute Force Recursion Over All Possibilities

## Intuition

The most direct idea is:

For every substring:

- try all split points
- try to see if the whole substring is made of repeated copies of some smaller substring
- recursively encode everything
- return the shortest result

This is conceptually correct, but without memoization it repeats work heavily.

---

## Recursive Definition

Let:

```text
encode(substring)
```

be the shortest encoding for that substring.

Then:

- initial answer = substring itself
- for every split point `k`:
  - candidate = `encode(left) + encode(right)`
- if substring is formed by repeating a smaller pattern:
  - candidate = `repeatCount + "[" + encode(pattern) + "]"`

Return the shortest candidate.

---

## Why It Is Too Slow

The same substrings are solved again and again.

For example, while solving a longer string, the substring `"abab"` might be recomputed through many different split paths.

That leads to exponential blow-up.

---

# Approach 2: Top-Down DP (Memoized Recursion)

## Intuition

The brute force recursion becomes practical if we memoize every substring result.

This is the most natural recursive solution.

For each substring `s[l..r]`, compute the shortest encoding once and store it.

---

## High-Level Plan

For a substring `sub`:

1. Start with `best = sub`
2. Try every split:
   - `left = encode(sub[0..i])`
   - `right = encode(sub[i+1..end])`
   - update best if concatenation is shorter
3. Check if `sub` is a repetition of a smaller substring:
   - if yes, try `count[encode(pattern)]`
4. Memoize and return best

---

## Detecting Repeated Pattern

To check if a string `sub` consists of repeated copies of a shorter pattern, one common trick is:

```text
(sub + sub).indexOf(sub, 1)
```

If the first reappearance of `sub` inside `(sub + sub)` occurs before `sub.length()`, then `sub` is periodic.

Example:

```text
sub = "ababab"
sub + sub = "abababababab"
```

The first internal reappearance happens at index `2`, so the repeating unit length is `2`, i.e. `"ab"`.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private final Map<String, String> memo = new HashMap<>();

    public String encode(String s) {
        return dfs(s);
    }

    private String dfs(String s) {
        if (s.length() <= 4) {
            return s;
        }

        if (memo.containsKey(s)) {
            return memo.get(s);
        }

        String best = s;

        // Try splitting
        for (int i = 1; i < s.length(); i++) {
            String left = dfs(s.substring(0, i));
            String right = dfs(s.substring(i));
            String candidate = left + right;

            if (candidate.length() < best.length()) {
                best = candidate;
            }
        }

        // Try encoding as repeated pattern
        int idx = (s + s).indexOf(s, 1);
        if (idx < s.length()) {
            int patternLen = idx;
            String pattern = dfs(s.substring(0, patternLen));
            String candidate = (s.length() / patternLen) + "[" + pattern + "]";
            if (candidate.length() < best.length()) {
                best = candidate;
            }
        }

        memo.put(s, best);
        return best;
    }
}
```

---

## Complexity Analysis

Let `n = s.length()`.

There are `O(n^2)` substrings.

For each substring:

- we try all split points: `O(n)`
- repetition check and substring operations can also cost up to `O(n)`

So overall this is roughly:

```text
O(n^4)
```

in many implementations.

### Space Complexity

Memo stores `O(n^2)` substrings, each potentially holding a string result.

So space is at least:

```text
O(n^3)
```

if counting stored string content naively.

For problem constraints (`n <= 150`), this is acceptable.

---

# Approach 3: Bottom-Up Interval DP

## Intuition

Instead of solving recursively, we can build answers for all substrings in increasing order of length.

This is the cleanest classic DP approach.

Define:

```text
dp[i][j]
```

as the shortest encoding of substring:

```text
s[i..j]
```

Then compute smaller intervals first, then larger ones.

---

## DP Transition

For substring `s[i..j]`:

### Option 1: Keep it raw

```text
dp[i][j] = s.substring(i, j + 1)
```

### Option 2: Split at every `k`

For every `k` in `[i, j)`:

```text
candidate = dp[i][k] + dp[k+1][j]
```

Take the shorter result.

### Option 3: Encode as repetition

If `s[i..j]` is formed by repeating a smaller pattern, then:

```text
candidate = count + "[" + dp[i][i + patternLen - 1] + "]"
```

Take the shorter result.

---

## Why Bottom-Up Works

Each state `dp[i][j]` depends only on:

- smaller intervals from splits
- shorter repeating unit intervals

So processing by increasing substring length guarantees dependencies are already solved.

---

## Java Code

```java
class Solution {
    public String encode(String s) {
        int n = s.length();
        String[][] dp = new String[n][n];

        for (int len = 1; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                String sub = s.substring(i, j + 1);
                dp[i][j] = sub;

                if (len <= 4) {
                    continue;
                }

                // Try splitting
                for (int k = i; k < j; k++) {
                    String candidate = dp[i][k] + dp[k + 1][j];
                    if (candidate.length() < dp[i][j].length()) {
                        dp[i][j] = candidate;
                    }
                }

                // Try repeated-pattern encoding
                int idx = (sub + sub).indexOf(sub, 1);
                if (idx < sub.length()) {
                    int patternLen = idx;
                    String candidate =
                        (sub.length() / patternLen) + "[" + dp[i][i + patternLen - 1] + "]";
                    if (candidate.length() < dp[i][j].length()) {
                        dp[i][j] = candidate;
                    }
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

## Complexity Analysis

There are `O(n^2)` states.

For each state:

- splits cost `O(n)`
- repeated-pattern detection can cost `O(n)`

So the total is around:

```text
O(n^4)
```

### Space Complexity

`dp[i][j]` stores strings for all intervals:

```text
O(n^3)
```

counting content storage informally.

---

## Verdict

This is the standard interview/editorial-quality solution.

---

# Approach 4: Bottom-Up DP With Explicit Divisor Checking for Repetition

## Intuition

Instead of using the `(sub + sub).indexOf(...)` trick, we can explicitly check all valid repeating unit lengths.

If substring length is `L`, then a valid repeating unit length must divide `L`.

So for every divisor `d < L`:

- check whether `sub` is made of repeating `sub[0..d-1]`
- if yes, candidate is `L/d + "[" + encodedUnit + "]"`

This is more explicit and sometimes easier to explain.

---

## Java Code

```java
class Solution {
    public String encode(String s) {
        int n = s.length();
        String[][] dp = new String[n][n];

        for (int len = 1; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                String sub = s.substring(i, j + 1);
                dp[i][j] = sub;

                if (len <= 4) {
                    continue;
                }

                // Split
                for (int k = i; k < j; k++) {
                    String candidate = dp[i][k] + dp[k + 1][j];
                    if (candidate.length() < dp[i][j].length()) {
                        dp[i][j] = candidate;
                    }
                }

                // Explicit repetition checking
                for (int d = 1; d <= len / 2; d++) {
                    if (len % d != 0) {
                        continue;
                    }

                    String unit = sub.substring(0, d);
                    StringBuilder built = new StringBuilder();
                    int times = len / d;

                    for (int t = 0; t < times; t++) {
                        built.append(unit);
                    }

                    if (built.toString().equals(sub)) {
                        String candidate = times + "[" + dp[i][i + d - 1] + "]";
                        if (candidate.length() < dp[i][j].length()) {
                            dp[i][j] = candidate;
                        }
                    }
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

## Complexity Analysis

This can be slightly worse depending on repetition checking cost.

Still acceptable for `n <= 150`.

A rough upper bound is:

```text
O(n^4)
```

or slightly worse depending on string-building costs.

---

## Verdict

More explicit, less elegant than the periodicity trick.

---

# Important Optimization: Why Length <= 4 Is Never Worth Encoding

For very short substrings, encoding cannot help.

Example:

- `"a"` length 1
- `"aa"` length 2
- `"aaa"` length 3
- `"aaaa"` length 4

Even `"4[a]"` has length 4, so it is not shorter than `"aaaa"`.

Thus, substrings of length 4 or less can safely be left unencoded.

This small optimization avoids unnecessary work.

---

# Why Split + Repeat Must Both Be Considered

A common mistake is to only look for repeated patterns.

That is not enough.

Example:

```text
s = "aabcaabcd"
```

A good answer may come from splitting into:

```text
"2[aabc]d"
```

which involves both:

- recognizing repetition in a part
- then combining with another part

So the DP must always consider both:

1. splitting
2. repeated-pattern compression

---

# Common Mistakes

## 1. Encoding when it is not shorter

The problem explicitly says:

> If encoding does not shorten the string, do not encode it.

So always compare lengths before replacing.

## 2. Only checking full-string repetition

The shortest encoding may come from splitting.

## 3. Forgetting to encode the repeated unit recursively

If a string is:

```text
"abababababab"
```

you should not just build:

```text
"6[ab]"
```

blindly. In some cases the repeated unit itself may have a shorter encoding.

So use the DP result for the repeated unit.

## 4. Using greedy repetition compression only once

The problem needs a globally shortest answer, not a locally appealing one.

---

# Final Recommended Solution

Use interval dynamic programming.

For every substring:

- initialize with itself
- try all splits
- try repeated-pattern compression
- store the shortest result

This is the safest and cleanest solution.

---

## Clean Final Java Solution

```java
class Solution {
    public String encode(String s) {
        int n = s.length();
        String[][] dp = new String[n][n];

        for (int len = 1; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                String sub = s.substring(i, j + 1);
                dp[i][j] = sub;

                if (len <= 4) {
                    continue;
                }

                for (int k = i; k < j; k++) {
                    String candidate = dp[i][k] + dp[k + 1][j];
                    if (candidate.length() < dp[i][j].length()) {
                        dp[i][j] = candidate;
                    }
                }

                int idx = (sub + sub).indexOf(sub, 1);
                if (idx < sub.length()) {
                    int patternLen = idx;
                    String candidate =
                        (sub.length() / patternLen) + "[" + dp[i][i + patternLen - 1] + "]";
                    if (candidate.length() < dp[i][j].length()) {
                        dp[i][j] = candidate;
                    }
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

# Complexity Summary

## Top-Down Memoized Recursion

- Time: about `O(n^4)`
- Space: about `O(n^3)` with stored strings

## Bottom-Up Interval DP

- Time: about `O(n^4)`
- Space: about `O(n^3)` with stored strings

## Explicit Divisor-Based Repetition Check

- Time: similar or slightly worse, still acceptable
- Space: similar

---

# Interview Summary

This problem is an interval dynamic programming problem.

For each substring, the shortest encoding can come from either:

- keeping it raw
- splitting it into two optimal encodings
- compressing it as repeated copies of a smaller optimally encoded pattern

That is why both splitting and periodicity detection are required.

The bottom-up interval DP is the most standard and reliable solution.
