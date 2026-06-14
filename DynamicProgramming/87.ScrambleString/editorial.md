# LeetCode 87 — Scramble String

## Exhaustive summary of approaches with Java code, detailed explanation, and complexity analysis

---

## Problem

Given two strings `s1` and `s2` of the same length, determine whether `s2` is a scrambled string of `s1`.

A scramble is defined recursively:

1. If the string length is `1`, stop.
2. Otherwise, split the string into two non-empty parts at some index.
3. Either keep the two parts in the same order or swap them.
4. Recurse on both parts.

---

## Constraints

- `s1.length == s2.length`
- `1 <= n <= 30`
- lowercase English letters only

---

# 1. Core intuition

This is fundamentally a recursive partitioning problem.

At any substring pair, the only real question is:

> Is there a split position `k` such that the two parts match recursively,
> either without swap or with swap?

For a substring of length `len`, for each split `k` from `1` to `len - 1`, there are two possibilities:

1. **No swap**
   - left part matches left part
   - right part matches right part

2. **Swap**
   - left part matches right part
   - right part matches left part

This naturally leads to recursion.
The hard part is that many substring pairs repeat, so memoization or DP is essential.

---

# 2. Important observation: character counts must match

Before any recursive check, an extremely powerful pruning rule is:

If two substrings do not contain the same multiset of characters, they can never be scramble-equivalent.

Example:

- `"abc"` and `"abd"` can never match
- no recursive swapping can invent or remove characters

This pruning is critical in top-down solutions.

---

# 3. State definitions

There are two common state styles.

## Recursive substring-style

`f(i, j, len)` = whether substring of `s1` starting at `i` with length `len`
is a scramble of substring of `s2` starting at `j` with length `len`

This is the cleanest state for memoization and bottom-up 3D DP.

## Raw string-substring recursion

`f(sub1, sub2)` = whether `sub2` is a scramble of `sub1`

This is conceptually simple, but substring creation is expensive and should usually be avoided in Java.

---

# 4. Approach 1 — Pure brute force recursion on substrings

This is the most direct translation of the definition.

---

## Idea

To check whether `s1` and `s2` are scramble-equivalent:

1. If strings are equal, return `true`
2. If lengths differ, return `false`
3. Try every split position `k`
4. Recurse on:
   - no swap case
   - swap case

If any split works, return `true`

---

## Why it is bad

This explores a huge recursion tree:

- many split points
- two recursive options per split
- repeated evaluation of the same substring pairs

So the running time grows explosively.

---

## Complexity

A precise closed-form is messy, but it is definitely exponential.

A safe summary:

- **Time: exponential**
- **Space: `O(n)` recursion depth** ignoring substring allocations

If substring objects are created repeatedly, actual overhead is even worse.

---

## Java code

```java
class SolutionBruteForce {
    public boolean isScramble(String s1, String s2) {
        if (s1.equals(s2)) {
            return true;
        }

        if (s1.length() != s2.length()) {
            return false;
        }

        int n = s1.length();
        for (int k = 1; k < n; k++) {
            // no swap
            if (isScramble(s1.substring(0, k), s2.substring(0, k)) &&
                isScramble(s1.substring(k), s2.substring(k))) {
                return true;
            }

            // swap
            if (isScramble(s1.substring(0, k), s2.substring(n - k)) &&
                isScramble(s1.substring(k), s2.substring(0, n - k))) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Strengths

- Mirrors the definition directly
- Good for deriving the recurrence

## Weaknesses

- Exponential time
- Heavy substring allocation in Java
- Recomputes the same states many times

---

# 5. Approach 2 — Brute force recursion with character-frequency pruning

This is still exponential in principle, but often much faster in practice than raw brute force.

---

## Idea

Before trying splits, check whether the two current substrings have the same character counts.

If not, return `false` immediately.

This prevents recursion into impossible branches.

---

## Why this helps

Many substring pairs can be rejected without exploring any split.

For example:

- `"abcde"` vs `"caebd"`

At some recursive level, frequency mismatch quickly kills branches.

---

## Complexity

Worst-case is still exponential, because many equal-frequency branches may still exist.

- **Time: still exponential in worst case**
- **Space: `O(n)` recursion depth**
- plus substring creation and count-array work

---

## Java code

```java
class SolutionBruteForcePruned {
    public boolean isScramble(String s1, String s2) {
        if (s1.equals(s2)) {
            return true;
        }

        if (s1.length() != s2.length()) {
            return false;
        }

        if (!sameCounts(s1, s2)) {
            return false;
        }

        int n = s1.length();
        for (int k = 1; k < n; k++) {
            if (isScramble(s1.substring(0, k), s2.substring(0, k)) &&
                isScramble(s1.substring(k), s2.substring(k))) {
                return true;
            }

            if (isScramble(s1.substring(0, k), s2.substring(n - k)) &&
                isScramble(s1.substring(k), s2.substring(0, n - k))) {
                return true;
            }
        }

        return false;
    }

    private boolean sameCounts(String a, String b) {
        int[] freq = new int[26];
        for (int i = 0; i < a.length(); i++) {
            freq[a.charAt(i) - 'a']++;
            freq[b.charAt(i) - 'a']--;
        }
        for (int x : freq) {
            if (x != 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Strengths

- Much better practical pruning than plain recursion
- Still easy to understand

## Weaknesses

- Still not polynomial
- Still repeats states
- Still uses expensive substring creation

---

# 6. Approach 3 — Top-down DP (memoization) using indices and length

This is the standard recursive optimization and usually the best way to understand the problem deeply.

---

## Core state

Define:

```text
f(i, j, len) = whether s1[i .. i+len-1] is a scramble of s2[j .. j+len-1]
```

Now instead of passing substring objects, we pass:

- start index in `s1`
- start index in `s2`
- length

This avoids substring allocation and makes memoization natural.

---

## Recurrence

### Base cases

1. If the substrings are exactly equal, return `true`
2. If character counts differ, return `false`
3. If `len == 1`, then equality check decides it

### Transition

Try every split `k` from `1` to `len - 1`

#### No swap

```text
f(i, j, k) && f(i + k, j + k, len - k)
```

#### Swap

```text
f(i, j + len - k, k) && f(i + k, j, len - k)
```

If either works for some `k`, return `true`.

---

## Complexity

### Number of states

Possible values:

- `i`: `O(n)`
- `j`: `O(n)`
- `len`: `O(n)`

So total states:

- **`O(n^3)`**

### Work per state

For each state, try all split points:

- `O(n)`

If frequency comparison is done in `O(len)` every time, that adds overhead.
With only `n <= 30`, many implementations simply do that.
A safe summary for the standard version is:

- **Time: `O(n^4)`**
- **Space: `O(n^3)`** for memo, plus recursion stack

If counting is recomputed naively inside every state, some analyses loosely say `O(n^5)` in the worst accounting model.
But the standard DP recurrence itself is best thought of as `O(n^4)`.

---

## Java code

```java
class SolutionTopDown {
    private String s1;
    private String s2;
    private Boolean[][][] memo;

    public boolean isScramble(String s1, String s2) {
        int n = s1.length();
        if (n != s2.length()) {
            return false;
        }

        this.s1 = s1;
        this.s2 = s2;
        this.memo = new Boolean[n][n][n + 1];

        return dfs(0, 0, n);
    }

    private boolean dfs(int i, int j, int len) {
        if (memo[i][j][len] != null) {
            return memo[i][j][len];
        }

        if (equalSubstrings(i, j, len)) {
            return memo[i][j][len] = true;
        }

        if (!sameCounts(i, j, len)) {
            return memo[i][j][len] = false;
        }

        for (int k = 1; k < len; k++) {
            // no swap
            if (dfs(i, j, k) && dfs(i + k, j + k, len - k)) {
                return memo[i][j][len] = true;
            }

            // swap
            if (dfs(i, j + len - k, k) && dfs(i + k, j, len - k)) {
                return memo[i][j][len] = true;
            }
        }

        return memo[i][j][len] = false;
    }

    private boolean equalSubstrings(int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (s1.charAt(i + k) != s2.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }

    private boolean sameCounts(int i, int j, int len) {
        int[] freq = new int[26];
        for (int k = 0; k < len; k++) {
            freq[s1.charAt(i + k) - 'a']++;
            freq[s2.charAt(j + k) - 'a']--;
        }
        for (int x : freq) {
            if (x != 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Strengths

- Natural recursive reasoning
- Avoids substring creation
- Memoization removes repeated work
- Usually the clearest serious solution

## Weaknesses

- 3D memo may look intimidating
- Recursive overhead
- Frequency checks can still be costly if recomputed often

---

# 7. Approach 4 — Top-down DP with prefix frequency arrays for faster pruning

This is a refinement of memoized recursion.

---

## Idea

Character-frequency pruning is valuable, but recomputing counts for every state costs time.

We can preprocess prefix frequency counts for both strings:

```text
prefix1[c][i] = count of character c in s1[0..i-1]
prefix2[c][i] = count of character c in s2[0..i-1]
```

Then for any substring `(start, len)`, its character counts can be checked in `O(26)` time.

---

## Why it helps

Without prefix counts:

- checking `sameCounts(i, j, len)` costs `O(len)`

With prefix counts:

- it becomes `O(26)` = effectively constant

This can significantly improve constant factors and tighten the recurrence accounting.

---

## Complexity

- States: `O(n^3)`
- Split trials per state: `O(n)`
- Count check per state: `O(26)`

So standard summary:

- **Time: `O(n^4)`**
- **Space: `O(n^3)`**

Prefix arrays themselves only add `O(26n)` space.

---

## Java code

```java
class SolutionTopDownPrefixCount {
    private String s1;
    private String s2;
    private Boolean[][][] memo;
    private int[][] pref1;
    private int[][] pref2;

    public boolean isScramble(String s1, String s2) {
        int n = s1.length();
        if (n != s2.length()) {
            return false;
        }

        this.s1 = s1;
        this.s2 = s2;
        this.memo = new Boolean[n][n][n + 1];
        this.pref1 = buildPrefix(s1);
        this.pref2 = buildPrefix(s2);

        return dfs(0, 0, n);
    }

    private boolean dfs(int i, int j, int len) {
        if (memo[i][j][len] != null) {
            return memo[i][j][len];
        }

        if (equalSubstrings(i, j, len)) {
            return memo[i][j][len] = true;
        }

        if (!sameCounts(i, j, len)) {
            return memo[i][j][len] = false;
        }

        for (int k = 1; k < len; k++) {
            if (dfs(i, j, k) && dfs(i + k, j + k, len - k)) {
                return memo[i][j][len] = true;
            }
            if (dfs(i, j + len - k, k) && dfs(i + k, j, len - k)) {
                return memo[i][j][len] = true;
            }
        }

        return memo[i][j][len] = false;
    }

    private int[][] buildPrefix(String s) {
        int n = s.length();
        int[][] pref = new int[26][n + 1];
        for (int i = 0; i < n; i++) {
            for (int c = 0; c < 26; c++) {
                pref[c][i + 1] = pref[c][i];
            }
            pref[s.charAt(i) - 'a'][i + 1]++;
        }
        return pref;
    }

    private boolean sameCounts(int i, int j, int len) {
        for (int c = 0; c < 26; c++) {
            int count1 = pref1[c][i + len] - pref1[c][i];
            int count2 = pref2[c][j + len] - pref2[c][j];
            if (count1 != count2) {
                return false;
            }
        }
        return true;
    }

    private boolean equalSubstrings(int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (s1.charAt(i + k) != s2.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Strengths

- Same clean memoized structure
- Stronger practical efficiency
- Better pruning cost

## Weaknesses

- More code
- Still `O(n^4)` DP logic due to split enumeration

---

# 8. Approach 5 — Bottom-up 3D dynamic programming

This is the iterative DP counterpart to the memoized recursion.

---

## State

Let:

```text
dp[len][i][j] = whether s1 substring starting at i of length len
                is a scramble of s2 substring starting at j of length len
```

Equivalent to the memoized state, just filled iteratively.

---

## Base case

For length `1`:

```text
dp[1][i][j] = (s1.charAt(i) == s2.charAt(j))
```

---

## Transition

For every length `len` from `2` to `n`,
for every valid `i`, `j`,
try every split `k` from `1` to `len - 1`.

### No swap

```text
dp[k][i][j] && dp[len-k][i+k][j+k]
```

### Swap

```text
dp[k][i][j+len-k] && dp[len-k][i+k][j]
```

If either is true for some split, then `dp[len][i][j] = true`.

---

## Complexity

- lengths: `O(n)`
- `i`: `O(n)`
- `j`: `O(n)`
- split `k`: `O(n)`

So:

- **Time: `O(n^4)`**
- **Space: `O(n^3)`**

This is the standard bottom-up complexity.

---

## Java code

```java
class SolutionBottomUp {
    public boolean isScramble(String s1, String s2) {
        int n = s1.length();
        if (n != s2.length()) {
            return false;
        }

        boolean[][][] dp = new boolean[n + 1][n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dp[1][i][j] = s1.charAt(i) == s2.charAt(j);
            }
        }

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len <= n; i++) {
                for (int j = 0; j + len <= n; j++) {
                    for (int k = 1; k < len; k++) {
                        boolean noSwap = dp[k][i][j] && dp[len - k][i + k][j + k];
                        boolean swap = dp[k][i][j + len - k] && dp[len - k][i + k][j];
                        if (noSwap || swap) {
                            dp[len][i][j] = true;
                            break;
                        }
                    }
                }
            }
        }

        return dp[n][0][0];
    }
}
```

---

## Strengths

- No recursion
- Very systematic
- Good if you like explicit DP tables

## Weaknesses

- 3D DP is memory-heavy
- Less intuitive than top-down for many people
- Still expensive in time

---

# 9. Approach 6 — Bottom-up 3D DP with frequency pruning

This is an optimization of the iterative approach.

---

## Idea

Before trying all split positions for `(len, i, j)`, first check whether the two substrings have the same character counts.

If not, set `dp[len][i][j] = false` immediately.

This reduces wasted split checks.

---

## Complexity

Worst-case asymptotic time is still generally summarized as:

- **Time: `O(n^4)`**
- **Space: `O(n^3)`**

But practical runtime can improve substantially.

---

## Java code

```java
class SolutionBottomUpPruned {
    public boolean isScramble(String s1, String s2) {
        int n = s1.length();
        if (n != s2.length()) {
            return false;
        }

        boolean[][][] dp = new boolean[n + 1][n][n];
        int[][] pref1 = buildPrefix(s1);
        int[][] pref2 = buildPrefix(s2);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dp[1][i][j] = s1.charAt(i) == s2.charAt(j);
            }
        }

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len <= n; i++) {
                for (int j = 0; j + len <= n; j++) {
                    if (!sameCounts(pref1, pref2, i, j, len)) {
                        continue;
                    }

                    for (int k = 1; k < len; k++) {
                        boolean noSwap = dp[k][i][j] && dp[len - k][i + k][j + k];
                        boolean swap = dp[k][i][j + len - k] && dp[len - k][i + k][j];
                        if (noSwap || swap) {
                            dp[len][i][j] = true;
                            break;
                        }
                    }
                }
            }
        }

        return dp[n][0][0];
    }

    private int[][] buildPrefix(String s) {
        int n = s.length();
        int[][] pref = new int[26][n + 1];
        for (int i = 0; i < n; i++) {
            for (int c = 0; c < 26; c++) {
                pref[c][i + 1] = pref[c][i];
            }
            pref[s.charAt(i) - 'a'][i + 1]++;
        }
        return pref;
    }

    private boolean sameCounts(int[][] pref1, int[][] pref2, int i, int j, int len) {
        for (int c = 0; c < 26; c++) {
            int count1 = pref1[c][i + len] - pref1[c][i];
            int count2 = pref2[c][j + len] - pref2[c][j];
            if (count1 != count2) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Strengths

- Stronger practical performance than unpruned bottom-up
- Still fully iterative

## Weaknesses

- Even more code
- Same asymptotic DP shape
- 3D table remains bulky

---

# 10. Approach 7 — Memoization using string keys / hash maps

This is a flexible but less efficient variant of top-down DP.

---

## Idea

Instead of a 3D array, use a map keyed by substring positions or actual substring text pairs.

Example key ideas:

- `"i,j,len"`
- or `"sub1#sub2"`

This can be convenient in quick prototypes.

---

## Why it is usually inferior in Java here

- string key creation adds overhead
- hash map lookup is heavier than array indexing
- substring objects can be expensive

Since `n <= 30`, a 3D array is usually cleaner and faster.

---

## Complexity

If indexed by `(i,j,len)` and memoized correctly:

- **Time: around `O(n^4)`**
- **Space: `O(n^3)`**

But with larger constants than array-based memoization.

---

## Java code

```java
import java.util.HashMap;
import java.util.Map;

class SolutionMemoMap {
    private String s1;
    private String s2;
    private Map<String, Boolean> memo = new HashMap<>();

    public boolean isScramble(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        this.s1 = s1;
        this.s2 = s2;
        return dfs(0, 0, s1.length());
    }

    private boolean dfs(int i, int j, int len) {
        String key = i + "," + j + "," + len;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        if (equalSubstrings(i, j, len)) {
            memo.put(key, true);
            return true;
        }

        if (!sameCounts(i, j, len)) {
            memo.put(key, false);
            return false;
        }

        for (int k = 1; k < len; k++) {
            if (dfs(i, j, k) && dfs(i + k, j + k, len - k)) {
                memo.put(key, true);
                return true;
            }
            if (dfs(i, j + len - k, k) && dfs(i + k, j, len - k)) {
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    private boolean equalSubstrings(int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (s1.charAt(i + k) != s2.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }

    private boolean sameCounts(int i, int j, int len) {
        int[] freq = new int[26];
        for (int k = 0; k < len; k++) {
            freq[s1.charAt(i + k) - 'a']++;
            freq[s2.charAt(j + k) - 'a']--;
        }
        for (int x : freq) {
            if (x != 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Strengths

- Flexible
- Easy to prototype
- Useful when fixed-size DP arrays are awkward

## Weaknesses

- Slower than array-based memo
- More GC / object overhead
- Less elegant for this particular constraint set

---

# 11. Why greedy or simple two-pointer ideas fail

This is important conceptually.

Scramble equivalence depends on recursive partition structure, not local adjacency or local ordering.

Two strings can have:

- identical character counts
- same length
- many local similarities

and still not be scramble-equivalent.

So:

- sorting / frequency alone is insufficient
- greedy partition choices can easily fail
- we must consider many split points

This is exactly why the problem needs recursion + DP over substring intervals.

---

# 12. Relation to interval DP style problems

This problem belongs to the broader family of:

- interval DP
- substring partition DP
- recursive split problems

The common pattern is:

1. define a substring/interval state
2. try every split
3. combine left and right answers

This is similar in spirit to:

- Matrix Chain Multiplication
- Burst Balloons
- Boolean parenthesization style splitting

The special extra twist here is the swap vs no-swap branch.

---

# 13. Full comparison of approaches

| Approach                        |                   Time |            Space | Practicality | Notes                                       |
| ------------------------------- | ---------------------: | ---------------: | ------------ | ------------------------------------------- |
| Pure brute force recursion      |            Exponential | `O(n)` recursion | No           | Only useful for deriving recurrence         |
| Brute force + frequency pruning | Exponential worst-case | `O(n)` recursion | Rarely       | Better practical pruning, still exponential |
| Top-down DP with indices        |               `O(n^4)` |         `O(n^3)` | Yes          | Standard and clear                          |
| Top-down DP + prefix counts     |               `O(n^4)` |         `O(n^3)` | Yes          | Better pruning cost                         |
| Bottom-up 3D DP                 |               `O(n^4)` |         `O(n^3)` | Yes          | Fully iterative                             |
| Bottom-up 3D DP + pruning       |               `O(n^4)` |         `O(n^3)` | Yes          | Faster in practice                          |
| Memoization with map keys       |               `O(n^4)` |         `O(n^3)` | Sometimes    | More flexible, larger constants             |

---

# 14. Which approach should you choose?

## For learning the problem

Use:

- top-down DP with `(i, j, len)` state

It reflects the recursive definition cleanly.

## For interview clarity

Also use:

- top-down DP with memoization and frequency pruning

It shows:

- recursive insight
- state compression
- pruning awareness

## If recursion is discouraged

Use:

- bottom-up 3D DP

It is systematic, though less intuitive.

---

# 15. Common edge cases

## Length 1

```text
s1 = "a", s2 = "a" -> true
s1 = "a", s2 = "b" -> false
```

## Already equal strings

```text
s1 = "great", s2 = "great" -> true
```

This should be caught immediately.

## Same counts but not scramble

```text
s1 = "abcde", s2 = "caebd" -> false
```

Important because count equality alone is not enough.

## Repeated characters

Repeated characters can create many equivalent-looking branches:

```text
s1 = "aabb", s2 = "abab"
```

This is where memoization becomes especially valuable.

---

# 16. Recommended final answer

For LeetCode 87, the strongest standard answer is:

- **Top-down DP with memoization**
- state = `(i, j, len)`
- plus character-count pruning

### Final complexity

- **Time: `O(n^4)`**
- **Space: `O(n^3)`**

This is usually the best combination of clarity, correctness, and performance for `n <= 30`.

---

# 17. Final takeaway

The essence of Scramble String is:

> Two substrings match if there exists a split where the left and right parts match recursively, either in the same order or swapped.

That immediately leads to:

- interval/substring state
- split enumeration
- recursive branching
- memoization or 3D DP

The decisive optimizations are:

1. do not create substring objects repeatedly
2. memoize `(i, j, len)`
3. prune impossible states using character counts

Those three ideas turn an exponential recursion into a manageable polynomial-time solution.
