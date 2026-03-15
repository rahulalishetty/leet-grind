# 2430. Maximum Deletions on a String

## Problem Statement

You are given a string `s` consisting only of lowercase English letters.

In one operation, you may:

1. **Delete the entire current string**, or
2. **Delete the first `i` characters** if the first `i` characters are equal to the next `i` characters, where:

```text
1 <= i <= currentLength / 2
```

For example, for:

```text
s = "ababc"
```

you may delete the first `"ab"` because the next two characters are also `"ab"`, leaving `"abc"`.

The goal is to return the **maximum number of operations** needed to delete the entire string.

---

## Example 1

```text
Input: s = "abcabcdabc"
Output: 2
```

Explanation:

- Delete `"abc"` because the next `"abc"` matches it.
  Remaining string: `"abcdabc"`
- Delete the remaining string.

Total operations = `2`.

---

## Example 2

```text
Input: s = "aaabaab"
Output: 4
```

Explanation:

- Delete `"a"` → `"aabaab"`
- Delete `"aab"` → `"aab"`
- Delete `"a"` → `"ab"`
- Delete the remaining string

Total operations = `4`.

---

## Example 3

```text
Input: s = "aaaaa"
Output: 5
```

Explanation:

Delete one `'a'` at a time.

---

## Constraints

- `1 <= s.length <= 4000`
- `s` consists only of lowercase English letters

---

# Core Insight

Let:

```text
dp[i] = maximum number of operations needed to delete s[i...n-1]
```

Then at minimum, we can always delete the entire suffix in one move:

```text
dp[i] = 1
```

But if for some length `len`:

```text
s[i ... i+len-1] == s[i+len ... i+2*len-1]
```

then we may delete the first `len` characters and continue from index `i + len`:

```text
dp[i] = max(dp[i], 1 + dp[i + len])
```

So the whole problem becomes:

> For every starting index `i`, find all lengths `len` such that the next block matches the current block.

That string-comparison step is the real challenge.

---

# Approach 1: Pure Recursion with Direct Substring Comparison

## Intuition

Try every valid first deletion recursively.

At each position:

- either delete the whole suffix now
- or try deleting a prefix block of length `len` if it equals the next block

This is the most direct formulation, but it repeats a lot of work.

---

## Java Code

```java
class Solution {
    public int deleteString(String s) {
        return dfs(s, 0);
    }

    private int dfs(String s, int start) {
        int n = s.length();
        if (start == n) {
            return 0;
        }

        int answer = 1; // delete the whole remaining suffix

        for (int len = 1; start + 2 * len <= n; len++) {
            if (isEqual(s, start, start + len, len)) {
                answer = Math.max(answer, 1 + dfs(s, start + len));
            }
        }

        return answer;
    }

    private boolean isEqual(String s, int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (s.charAt(i + k) != s.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Very large, exponential in practice.

Why?

- Many recursive states repeat.
- Each state tries many lengths.
- Each length may require `O(len)` comparison.

### Space Complexity

```text
O(n)
```

for recursion depth.

---

## Verdict

Useful only as a conceptual starting point.

---

# Approach 2: Memoized DFS + Direct Comparison

## Intuition

The repeated subproblem is:

```text
maximum deletions starting from index i
```

So memoization immediately helps.

We still compare substrings character by character, but each suffix state is solved once.

---

## State Definition

```text
memo[i] = maximum number of operations to delete s[i...]
```

Transition:

- initialize `memo[i] = 1`
- for every `len` such that `i + 2 * len <= n`
  - if the two adjacent blocks match, try:
    ```text
    1 + memo[i + len]
    ```

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int deleteString(String s) {
        int n = s.length();
        int[] memo = new int[n];
        Arrays.fill(memo, -1);
        return dfs(s, 0, memo);
    }

    private int dfs(String s, int start, int[] memo) {
        int n = s.length();
        if (start == n) {
            return 0;
        }

        if (memo[start] != -1) {
            return memo[start];
        }

        int answer = 1;

        for (int len = 1; start + 2 * len <= n; len++) {
            if (isEqual(s, start, start + len, len)) {
                answer = Math.max(answer, 1 + dfs(s, start + len, memo));
            }
        }

        memo[start] = answer;
        return answer;
    }

    private boolean isEqual(String s, int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (s.charAt(i + k) != s.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Worst case:

```text
O(n^3)
```

Reason:

- `n` states
- each state tries `O(n)` lengths
- each equality check can cost `O(n)`

### Space Complexity

```text
O(n)
```

---

## Verdict

Much better than pure recursion, but still too slow for `n = 4000` in the worst case.

---

# Approach 3: Bottom-Up DP + Direct Comparison

## Intuition

Instead of top-down recursion, compute `dp[i]` from right to left.

This avoids recursion overhead, but if we still compare blocks character by character, the complexity remains cubic.

---

## Java Code

```java
class Solution {
    public int deleteString(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = 1;

            for (int len = 1; i + 2 * len <= n; len++) {
                if (isEqual(s, i, i + len, len)) {
                    dp[i] = Math.max(dp[i], 1 + dp[i + len]);
                }
            }
        }

        return dp[0];
    }

    private boolean isEqual(String s, int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (s.charAt(i + k) != s.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^3)
```

### Space Complexity

```text
O(n)
```

---

## Verdict

Cleaner than recursion, but same asymptotic bottleneck.

---

# Approach 4: DP + Longest Common Prefix Table

## Intuition

The real bottleneck is repeatedly checking:

```text
s[i ... i+len-1] == s[i+len ... i+2*len-1]
```

A better way is to precompute:

```text
lcp[i][j] = length of the longest common prefix of suffixes s[i...] and s[j...]
```

Then the equality test becomes constant time:

```text
the blocks are equal iff lcp[i][i + len] >= len
```

This changes the whole game.

---

## How to Compute `lcp`

We define:

```text
lcp[i][j] =
    if s[i] == s[j], then 1 + lcp[i+1][j+1]
    else 0
```

We fill this from bottom-right toward top-left.

---

## Then DP Transition

For each index `i`:

- `dp[i] = 1`
- for each `len` with `i + 2 * len <= n`
  - if `lcp[i][i + len] >= len`, then:
    ```text
    dp[i] = max(dp[i], 1 + dp[i + len])
    ```

This gives an exact dynamic programming solution in `O(n^2)`.

---

## Java Code

```java
class Solution {
    public int deleteString(String s) {
        int n = s.length();
        int[][] lcp = new int[n + 1][n + 1];
        int[] dp = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = n - 1; j > i; j--) {
                if (s.charAt(i) == s.charAt(j)) {
                    lcp[i][j] = 1 + lcp[i + 1][j + 1];
                }
            }
        }

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = 1;

            for (int len = 1; i + 2 * len <= n; len++) {
                if (lcp[i][i + len] >= len) {
                    dp[i] = Math.max(dp[i], 1 + dp[i + len]);
                }
            }
        }

        return dp[0];
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

- `O(n^2)` to build `lcp`
- `O(n^2)` for DP transitions

### Space Complexity

```text
O(n^2)
```

because of the `lcp` table.

---

## Verdict

This is the standard strong solution.

It is exact, efficient enough for `n = 4000`, and conceptually clean.

---

# Approach 5: DP + Z-Algorithm on Every Suffix

## Intuition

Another way to answer substring equality quickly is to compute a Z-array for every suffix.

For a fixed starting index `i`, consider the suffix:

```text
t = s[i...]
```

Its Z-array tells us, for every offset `len`, the LCP between:

```text
t[0...] and t[len...]
```

That is exactly what we need to know whether:

```text
s[i ... i+len-1] == s[i+len ... i+2*len-1]
```

because that holds iff:

```text
z[len] >= len
```

for the suffix starting at `i`.

So for each `i`, we can compute a Z-array for `s.substring(i)` and use it to update `dp[i]`.

This is another exact `O(n^2)` method.

---

## Why This Works

For a fixed `i`, the candidate deletion lengths are all lengths `len`.

If we build the Z-array of `s[i...]`, we can instantly know which lengths `len` satisfy adjacent-equality.

Then:

```text
dp[i] = max(1, 1 + dp[i + len])
```

for all valid `len`.

---

## Java Code

```java
class Solution {
    public int deleteString(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = 1;
            int m = n - i;
            int[] z = buildZ(s, i);

            for (int len = 1; 2 * len <= m; len++) {
                if (z[len] >= len) {
                    dp[i] = Math.max(dp[i], 1 + dp[i + len]);
                }
            }
        }

        return dp[0];
    }

    private int[] buildZ(String s, int start) {
        int n = s.length() - start;
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n &&
                   s.charAt(start + z[i]) == s.charAt(start + i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

Each suffix builds its own Z-array, and the sum of suffix lengths is quadratic.

### Space Complexity

```text
O(n)
```

extra working space per iteration, plus `dp`.

If you count total transient work over time, it is still `O(n^2)` time but only `O(n)` extra live space.

---

## Verdict

This is a very elegant alternative.

Compared with the LCP table:

- it uses less live memory
- but the LCP table may be easier to reason about for direct comparisons across all pairs

---

# Comparing the Best Two Approaches

## LCP Table + DP

### Strengths

- direct and explicit
- equality queries are `O(1)` anywhere
- very interview friendly

### Weakness

- uses `O(n^2)` memory

---

## Per-Suffix Z-Algorithm + DP

### Strengths

- exact `O(n^2)` time
- only `O(n)` extra live memory
- beautiful string-algorithm angle

### Weakness

- slightly less obvious at first glance

---

# Why Greedy Does Not Work

A tempting thought is:

> always delete the smallest valid prefix to maximize number of operations

That is not generally safe without proof.

The local choice may block a better chain later.

This is why dynamic programming is necessary.

We need to evaluate:

```text
1 + best answer from the remaining suffix
```

over all valid first deletions.

---

# Dry Run on `"aaabaab"`

## String

```text
s = "aaabaab"
```

At index `0`:

- `len = 1` works because `"a" == "a"`
- `len = 2` does not work because `"aa" != "ab"`
- `len = 3` does not work because `"aaa" != "baa"`

So one possible move is deleting 1 char and solving `"aabaab"`.

At index corresponding to `"aabaab"`:

- `len = 1` works
- `len = 3` works because `"aab" == "aab"`

The best sequence eventually leads to `4`.

This is exactly what DP captures.

---

# Final Recommended Solution

The most practical exact solution is:

## DP + LCP table

It is:

- deterministic
- easy to implement
- comfortably fast for `n <= 4000`

---

## Clean Final Java Solution

```java
class Solution {
    public int deleteString(String s) {
        int n = s.length();
        int[][] lcp = new int[n + 1][n + 1];
        int[] dp = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = n - 1; j > i; j--) {
                if (s.charAt(i) == s.charAt(j)) {
                    lcp[i][j] = 1 + lcp[i + 1][j + 1];
                }
            }
        }

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = 1;

            for (int len = 1; i + 2 * len <= n; len++) {
                if (lcp[i][i + len] >= len) {
                    dp[i] = Math.max(dp[i], 1 + dp[i + len]);
                }
            }
        }

        return dp[0];
    }
}
```

---

# Common Mistakes

## 1. Using greedy deletion

Choosing the shortest or longest valid prefix first is not guaranteed to be optimal.

---

## 2. Re-comparing substrings character by character inside DP

That turns the solution into `O(n^3)`, which is too slow in the worst case.

---

## 3. Forgetting that deleting the entire string is always allowed

So every state should start with:

```text
dp[i] = 1
```

---

## 4. Off-by-one errors in adjacent block comparison

To compare two adjacent length-`len` blocks starting at `i`, the second block starts at:

```text
i + len
```

and must satisfy:

```text
i + 2 * len <= n
```

---

## 5. Using recursion without memoization

That explodes due to repeated overlapping suffix states.

---

# Complexity Summary

## Pure recursion

- Time: exponential
- Space: `O(n)`

## Memoized DFS + direct compare

- Time: `O(n^3)`
- Space: `O(n)`

## Bottom-up DP + direct compare

- Time: `O(n^3)`
- Space: `O(n)`

## DP + LCP table

- Time: `O(n^2)`
- Space: `O(n^2)`

## DP + per-suffix Z-algorithm

- Time: `O(n^2)`
- Space: `O(n)`

---

# Interview Summary

The problem is really:

> From each suffix, try deleting a prefix block if it matches the next block, and maximize the number of deletions.

So:

1. define `dp[i]` = best answer for suffix `s[i...]`
2. transition to `i + len` when two adjacent length-`len` blocks match
3. accelerate substring-equality checks with either:
   - an `lcp` table, or
   - Z-algorithm on each suffix

The best exact solution is `O(n^2)`.

---

# Final Answer

```java
class Solution {
    public int deleteString(String s) {
        int n = s.length();
        int[][] lcp = new int[n + 1][n + 1];
        int[] dp = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = n - 1; j > i; j--) {
                if (s.charAt(i) == s.charAt(j)) {
                    lcp[i][j] = 1 + lcp[i + 1][j + 1];
                }
            }
        }

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = 1;

            for (int len = 1; i + 2 * len <= n; len++) {
                if (lcp[i][i + len] >= len) {
                    dp[i] = Math.max(dp[i], 1 + dp[i + len]);
                }
            }
        }

        return dp[0];
    }
}
```
