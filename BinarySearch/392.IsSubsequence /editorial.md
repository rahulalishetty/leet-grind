# 392. Is Subsequence — Summary (Java)

Given two strings `s` and `t`, return `true` if `s` is a subsequence of `t`, or `false` otherwise.

---

## Approach 1: Recursive

Base cases handle empty strings. On a character match, recurse on both remainders; otherwise skip a character in `t`. Simple but creates many `String` objects due to `substring()`.

```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        if (s.isEmpty()) return true;
        if (t.isEmpty()) return false;

        if (s.charAt(0) == t.charAt(0)) {
            return isSubsequence(s.substring(1), t.substring(1));
        } else {
            return isSubsequence(s, t.substring(1));
        }
    }
}
```

**Time:** O(|t|) — **Space:** O(|t|) stack

---

## Approach 2: Two Pointers ★ (Recommended)

Two indices `i` (for `s`) and `j` (for `t`). Advance `i` only on a match; always advance `j`. If `i` reaches `s.length()`, every character was found in order.

```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        int i = 0, j = 0; // i → s, j → t

        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                i++; // match: advance s pointer
            }
            j++;     // always advance t pointer
        }

        return i == s.length();
    }
}
```

**Time:** O(|t|) — **Space:** O(1)

> Best solution for a single query: linear time, constant space, clean code.

---

## Approach 3: Iterator Style

Java doesn't have Python's lazy iterator trick, but the same idea can be simulated by sharing a cursor `pos` across the loop over `s`.

```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        int pos = 0; // shared cursor into t

        for (char c : s.toCharArray()) {
            while (pos < t.length() && t.charAt(pos) != c) {
                pos++;
            }
            if (pos == t.length()) return false;
            pos++;
        }

        return true;
    }
}
```

**Time:** O(|t|) — **Space:** O(1)

---

## Approach 4a: DP — Top-Down (Memoization)

The recursive solution (Approach 1) re-computes the same `(i, j)` subproblems repeatedly. Memoizing on index pairs `(i, j)` — where `i` is the position in `s` and `j` is the position in `t` — eliminates the repeated work and makes the recurrence explicit before flattening it into a table.

The recurrence is:

- `memo[i][j] = true` if `s[i..] is a subsequence of t[j..]`
- If `s[i] == t[j]`: recurse on `(i+1, j+1)`
- Else: recurse on `(i, j+1)` — skip `t[j]`

```java
class Solution {
    private int[][] memo;

    public boolean isSubsequence(String s, String t) {
        memo = new int[s.length() + 1][t.length() + 1];
        // 0 = unvisited, 1 = true, -1 = false
        return dp(s, t, 0, 0);
    }

    private boolean dp(String s, String t, int i, int j) {
        if (i == s.length()) return true;   // all of s matched
        if (j == t.length()) return false;  // t exhausted

        if (memo[i][j] != 0) return memo[i][j] == 1;

        boolean result;
        if (s.charAt(i) == t.charAt(j)) {
            result = dp(s, t, i + 1, j + 1); // match: advance both
        } else {
            result = dp(s, t, i, j + 1);     // skip t[j]
        }

        memo[i][j] = result ? 1 : -1;
        return result;
    }
}
```

**Time:** O(|s| × |t|) — **Space:** O(|s| × |t|) memo table + O(|t|) stack

> This is the natural bridge between the plain recursion and the bottom-up table. Once you see which `(i, j)` cells are filled, the bottom-up iteration order becomes obvious.

---

## Approach 4b: DP — Bottom-Up (LCS Table)

Build a 2-D LCS table. `s` is a subsequence of `t` iff `LCS(s, t) == s.length()`. Correct but overkill — O(n²) time and space for a problem solvable in O(n).

```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        int m = s.length(), n = t.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n] == m;
    }
}
```

**Time:** O(|s| × |t|) — **Space:** O(|s| × |t|)

---

## Approach 5: Binary Search ★ (Multiple Queries Follow-up)

**Follow-up:** If `k ≥ 10⁹` strings need to be checked against the same fixed `t`, the two-pointer approach costs O(k × |t|) — too slow.

Preprocess `t` once: store a sorted list of positions for each character. For each query `s`, binary-search for the next valid position of each character. Total cost: O(|t| + k × |s| × log|t|).

```java
import java.util.*;

class Solution {
    private Map<Character, List<Integer>> charIndices = new HashMap<>();

    public void preprocess(String t) {
        for (int i = 0; i < t.length(); i++) {
            charIndices
                .computeIfAbsent(t.charAt(i), k -> new ArrayList<>())
                .add(i);
        }
    }

    public boolean isSubsequence(String s, String t) {
        if (charIndices.isEmpty()) preprocess(t);

        int cur = 0; // next match must be at index >= cur

        for (char c : s.toCharArray()) {
            List<Integer> positions = charIndices.get(c);
            if (positions == null) return false;

            // binary search: first index >= cur
            int lo = 0, hi = positions.size();
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (positions.get(mid) < cur) lo = mid + 1;
                else hi = mid;
            }
            if (lo == positions.size()) return false;

            cur = positions.get(lo) + 1;
        }

        return true;
    }
}
```

**Preprocessing:** O(|t|) — **Per query:** O(|s| × log|t|) — **Space:** O(|t|)

---

## Comparison

| Approach           | Time                        | Space          | Best For                         |
| ------------------ | --------------------------- | -------------- | -------------------------------- |
| 1. Recursive       | O(\|t\|)                    | O(\|t\|) stack | Learning / small inputs          |
| 2. Two Pointers ★  | O(\|t\|)                    | O(1)           | **Single query (recommended)**   |
| 3. Iterator style  | O(\|t\|)                    | O(1)           | Idiomatic, easy to read          |
| 4a. DP Top-Down    | O(\|s\|×\|t\|)              | O(\|s\|×\|t\|) | Bridge: recursion → table        |
| 4b. DP Bottom-Up   | O(\|s\|×\|t\|)              | O(\|s\|×\|t\|) | Academic / LCS problems          |
| 5. Binary Search ★ | O(\|t\| + k·\|s\|·log\|t\|) | O(\|t\|)       | **Multiple queries (follow-up)** |

---

## Key Takeaways

- `String.substring()` in the recursive approach allocates a new `String` on each call — avoid for large inputs in Java.
- The **two-pointer approach** uses only primitive `int` indices — the go-to solution for a single query.
- For the binary search approach, the manual `lo/hi` loop is clearer than `Collections.binarySearch()`, which returns a negative insertion point for missing values.
- In a real interview follow-up, separate `preprocess(t)` into a constructor so the same instance handles all `k` queries against the same `t`.
