# 115. Distinct Subsequences — Exhaustive Solution Notes

## Overview

This problem is an excellent example of how a solution can evolve through three important stages:

1. **Recursion + Memoization**
2. **Iterative Dynamic Programming**
3. **Space-Optimized Dynamic Programming**

It is especially useful for understanding:

- how to model choices in subsequence problems,
- how recursive state definitions turn into DP tables,
- and how to optimize space once the recurrence is understood clearly.

---

## Problem Statement

Given two strings `s` and `t`, return the number of **distinct subsequences** of `s` which equals `t`.

The test cases are generated so that the answer fits in a **32-bit signed integer**.

---

## Example 1

**Input**

```text
s = "rabbbit"
t = "rabbit"
```

**Output**

```text
3
```

**Explanation**

There are 3 different ways to delete one of the three `'b'` characters from `"rabbbit"` to form `"rabbit"`.

---

## Example 2

**Input**

```text
s = "babgbag"
t = "bag"
```

**Output**

```text
5
```

**Explanation**

There are 5 distinct ways to select characters from `"babgbag"` in order so that they form `"bag"`.

---

## Constraints

- `1 <= s.length, t.length <= 1000`
- `s` and `t` consist of English letters.

---

# Core Idea

A **subsequence** is formed by deleting zero or more characters **without changing the order** of the remaining characters.

So for every character in `s`, we effectively have a choice:

- **skip it**, or
- **use it** if it helps match the current character in `t`.

That choice structure is exactly what leads to recursion and dynamic programming.

---

# Approach 1: Recursion + Memoization

## Intuition

Let us first think about a simpler version of the problem:

> Can we determine whether `t` is a subsequence of `s`?

A natural way is to use two pointers:

- one pointer on `s`,
- one pointer on `t`.

At each step:

- if `s[i] == t[j]`, we can advance both,
- otherwise, we advance only `i`.

That works for checking **existence**.

But in this problem, we need the **count of all possible subsequences**, not just one successful match.

So when `s[i] == t[j]`, we now have **two choices**:

1. **Use** `s[i]` to match `t[j]`
   - move to `(i + 1, j + 1)`

2. **Skip** `s[i]`
   - move to `(i + 1, j)`

If the characters do **not** match, then the only choice is:

- skip `s[i]`
- move to `(i + 1, j)`

This gives us the recursive structure.

---

## State Definition

Let:

```text
recurse(i, j)
```

represent:

> the number of distinct subsequences in `s[i..M-1]` that equal `t[j..N-1]`

where:

- `M = s.length()`
- `N = t.length()`

This is the key modeling step.

---

## Recursive Transition

### Case 1: Characters match

If:

```text
s[i] == t[j]
```

then we can either:

- ignore `s[i]`, or
- use `s[i]` to match `t[j]`

So:

```text
recurse(i, j) = recurse(i + 1, j) + recurse(i + 1, j + 1)
```

---

### Case 2: Characters do not match

If:

```text
s[i] != t[j]
```

then the current character in `s` cannot help match `t[j]`.

So:

```text
recurse(i, j) = recurse(i + 1, j)
```

---

## Base Cases

There are two important base cases.

### Base Case 1: Entire `t` is matched

If:

```text
j == N
```

then we have successfully matched all of `t`.

So:

```text
recurse(i, N) = 1
```

This means:

- there is exactly one way to match an empty target string,
- by choosing nothing from the remaining suffix of `s`.

---

### Base Case 2: `s` is exhausted before `t`

If:

```text
i == M and j < N
```

then there are no characters left in `s` to finish matching `t`.

So:

```text
recurse(M, j) = 0
```

---

### Useful Pruning Condition

If the remaining length of `s` is smaller than the remaining length of `t`, then matching is impossible:

```text
M - i < N - j
```

So we can immediately return `0`.

This helps reduce unnecessary recursive exploration.

---

## Why Memoization Is Needed

Without memoization, the recursion tree repeats the same states many times.

For example, the same pair `(i, j)` can be reached through multiple paths.

That means the recursion would do redundant work.

To avoid this, we cache results using a map:

- **key** = `(i, j)`
- **value** = `recurse(i, j)`

So each unique state is computed only once.

---

## Recursive Pseudocode

```text
func recurse(i, j):
    if j == N:
        return 1

    if i == M:
        return 0

    if M - i < N - j:
        return 0

    if (i, j) is in memo:
        return memo[(i, j)]

    ans = recurse(i + 1, j)

    if s[i] == t[j]:
        ans += recurse(i + 1, j + 1)

    memo[(i, j)] = ans
    return ans
```

---

## Java Implementation — Recursion + Memoization

```java
class Solution {
    private HashMap<String, Integer> memo;

    private int recurse(String s, String t, int i, int j) {
        int M = s.length();
        int N = t.length();

        // Base cases
        if (j == N) {
            return 1;
        }

        if (i == M) {
            return 0;
        }

        if (M - i < N - j) {
            return 0;
        }

        String key = i + "," + j;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int ans = recurse(s, t, i + 1, j);

        if (s.charAt(i) == t.charAt(j)) {
            ans += recurse(s, t, i + 1, j + 1);
        }

        memo.put(key, ans);
        return ans;
    }

    public int numDistinct(String s, String t) {
        memo = new HashMap<>();
        return recurse(s, t, 0, 0);
    }
}
```

---

## Complexity Analysis — Approach 1

### Time Complexity

There are at most:

```text
O(M × N)
```

unique states `(i, j)`.

Each state is processed once, and each processing step takes constant time apart from recursive calls already memoized.

So the total time complexity is:

```text
O(M × N)
```

---

### Space Complexity

Memo table stores up to:

```text
O(M × N)
```

states.

Additionally, recursion stack depth can go up to:

```text
O(M)
```

So total space is:

```text
O(M × N)
```

with recursion stack overhead.

---

# Approach 2: Iterative Dynamic Programming

## Intuition

The recursive solution is correct, but it depends on the call stack.

Since `s.length` and `t.length` can both be up to `1000`, iterative DP is safer and usually faster.

The main idea is to convert the recursion into a bottom-up table.

---

## DP State Definition

Let:

```text
dp[i][j]
```

represent:

> the number of distinct subsequences in `s[i..M-1]` that equal `t[j..N-1]`

This is exactly the same meaning as `recurse(i, j)`.

So:

```text
dp[i][j] = recurse(i, j)
```

---

## DP Transition

From the recurrence:

- if `s[i] != t[j]`:

```text
dp[i][j] = dp[i + 1][j]
```

- if `s[i] == t[j]`:

```text
dp[i][j] = dp[i + 1][j] + dp[i + 1][j + 1]
```

This matches the recursive logic exactly.

---

## DP Table Size

We use a table of size:

```text
(M + 1) × (N + 1)
```

Why `+1`?

Because we also need to represent:

- `i == M`
- `j == N`

These are the base-case boundaries.

---

## Base Case Initialization

### Last Column: `j == N`

If `t` is already fully matched, then the answer is always `1`.

So:

```text
dp[i][N] = 1    for all i
```

---

### Last Row: `i == M`

If `s` is exhausted but `t` is not, then the answer is `0`.

So:

```text
dp[M][j] = 0    for all j < N
```

And note:

```text
dp[M][N] = 1
```

because empty `t` matches empty `s` in exactly one way.

---

## Filling Order

Since `dp[i][j]` depends on:

- `dp[i + 1][j]`
- `dp[i + 1][j + 1]`

we must fill the table from bottom to top and from right to left:

- `i` goes from `M - 1` down to `0`
- `j` goes from `N - 1` down to `0`

---

## Java Implementation — Iterative DP

```java
class Solution {
    public int numDistinct(String s, String t) {
        int M = s.length();
        int N = t.length();

        int[][] dp = new int[M + 1][N + 1];

        // Base case: empty t can always be formed
        for (int i = 0; i <= M; i++) {
            dp[i][N] = 1;
        }

        // Base case: non-empty t cannot be formed from empty s
        for (int j = 0; j < N; j++) {
            dp[M][j] = 0;
        }

        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                dp[i][j] = dp[i + 1][j];

                if (s.charAt(i) == t.charAt(j)) {
                    dp[i][j] += dp[i + 1][j + 1];
                }
            }
        }

        return dp[0][0];
    }
}
```

---

## Complexity Analysis — Approach 2

### Time Complexity

We fill a table with:

```text
(M + 1) × (N + 1)
```

cells.

Each cell is computed in constant time.

So the total time complexity is:

```text
O(M × N)
```

---

### Space Complexity

The DP table uses:

```text
O(M × N)
```

space.

---

# Approach 3: Space-Optimized Dynamic Programming

## Intuition

In the 2D DP solution, observe this carefully:

```text
dp[i][j]
```

depends only on values from the **next row**:

- `dp[i + 1][j]`
- `dp[i + 1][j + 1]`

That means while computing row `i`, we do not need the full matrix.

We only need the information from the row below.

So the 2D table can be compressed into a 1D array.

---

## Key Observation

In the original recurrence:

```text
dp[i][j] = dp[i + 1][j]
```

and if characters match:

```text
dp[i][j] += dp[i + 1][j + 1]
```

So for each row, we need:

- current `dp[j]` to represent `dp[i + 1][j]`
- previous diagonal value to represent `dp[i + 1][j + 1]`

That is why we use:

- a 1D array `dp`
- one extra variable, often called `prev`

---

## How In-Place Update Works

Suppose `dp[j]` currently stores the value from the next row:

```text
dp[j] = old dp[i + 1][j]
```

While iterating from right to left, we store the old value before overwriting it.

That old value becomes the diagonal value needed for the next position on the left.

This is the tricky but elegant part.

---

## Initialization

We use:

```text
int[] dp = new int[N + 1];
```

and initialize:

```text
dp[N] = 1
```

because empty `t` can always be matched in one way.

All other entries start at `0`.

---

## Update Order

We iterate:

- `i` from `M - 1` down to `0`
- `j` from `N - 1` down to `0`

For each row:

- start `prev = 1`, because that corresponds to `dp[i + 1][N]`

Then for each `j`:

1. save current `dp[j]` into `old`
2. if `s[i] == t[j]`, then add `prev` to `dp[j]`
3. update `prev = old`

---

## Java Implementation — Space Optimized DP

```java
class Solution {
    public int numDistinct(String s, String t) {
        int M = s.length();
        int N = t.length();

        int[] dp = new int[N + 1];
        dp[N] = 1;

        for (int i = M - 1; i >= 0; i--) {
            int prev = 1; // represents dp[i + 1][N]

            for (int j = N - 1; j >= 0; j--) {
                int old = dp[j];

                if (s.charAt(i) == t.charAt(j)) {
                    dp[j] += prev;
                }

                prev = old;
            }
        }

        return dp[0];
    }
}
```

---

## Complexity Analysis — Approach 3

### Time Complexity

We still process every pair `(i, j)` once.

So time complexity remains:

```text
O(M × N)
```

---

### Space Complexity

We only store one row of size:

```text
O(N)
```

This is a major improvement over the 2D table.

---

# Step-by-Step Conceptual Example

Let:

```text
s = "babgbag"
t = "bag"
```

We want the number of subsequences of `"babgbag"` equal to `"bag"`.

At each character in `s`, we decide whether to use it or skip it.

Some valid ways are:

1. `b(0) a(1) g(3)`
2. `b(0) a(1) g(6)`
3. `b(0) a(5) g(6)`
4. `b(2) a(5) g(6)`
5. `b(4) a(5) g(6)`

So the answer is `5`.

This example shows why simple greedy matching does not work.
We must count **all possible valid paths**, which is why dynamic programming is required.

---

# Why Greedy Fails

A tempting thought is:

> Whenever characters match, just take the match and move forward.

That works for checking whether `t` is a subsequence of `s`, but it fails for counting.

Why?

Because when `s[i] == t[j]`, we must consider both:

- using this character,
- and skipping it to potentially use a later matching character.

This branching is exactly why the recurrence has two terms.

---

# Important Interview Insights

This problem is frequently used in interviews because it tests whether you can:

- define a recursive state correctly,
- identify overlapping subproblems,
- convert recursion to tabulation,
- and optimize space from `O(M × N)` to `O(N)`.

A strong interview progression usually looks like this:

1. Start with recursion
2. Add memoization
3. Convert to iterative DP
4. Compress the DP table

If you can explain all four clearly, that is usually a strong signal.

---

# Common Mistakes

## 1. Wrong Base Case for Empty `t`

Many people forget that:

```text
t == ""
```

should return `1`, not `0`.

There is exactly one way to match an empty string: choose nothing.

---

## 2. Wrong DP Fill Direction

Since `dp[i][j]` depends on `dp[i + 1][j]` and `dp[i + 1][j + 1]`, the table must be filled:

- bottom to top,
- right to left.

Filling in the wrong direction breaks dependencies.

---

## 3. Forgetting the Extra Column

When using iterative DP, you need size:

```text
N + 1
```

to represent the empty suffix of `t`.

---

## 4. Incorrect In-Place Update in 1D DP

The space-optimized version is easy to get wrong unless you carefully preserve the diagonal value before overwriting `dp[j]`.

---

# Final Recommendation

If your goal is clarity:

- use **Approach 2: Iterative DP**

If your goal is best space efficiency:

- use **Approach 3: Space-Optimized DP**

If your goal is learning the recurrence deeply:

- start from **Approach 1: Recursion + Memoization**

That learning sequence is the most valuable one.

---

# Final Summary

## Recurrence

If `s[i] == t[j]`:

```text
dp[i][j] = dp[i + 1][j] + dp[i + 1][j + 1]
```

Else:

```text
dp[i][j] = dp[i + 1][j]
```

---

## Base Cases

```text
dp[i][N] = 1
dp[M][j] = 0 for j < N
dp[M][N] = 1
```

---

## Complexities

### Recursion + Memoization

- Time: `O(M × N)`
- Space: `O(M × N)` + recursion stack

### Iterative DP

- Time: `O(M × N)`
- Space: `O(M × N)`

### Space-Optimized DP

- Time: `O(M × N)`
- Space: `O(N)`

---

# Best Final Java Solution

```java
class Solution {
    public int numDistinct(String s, String t) {
        int M = s.length();
        int N = t.length();

        int[] dp = new int[N + 1];
        dp[N] = 1;

        for (int i = M - 1; i >= 0; i--) {
            int prev = 1;

            for (int j = N - 1; j >= 0; j--) {
                int old = dp[j];

                if (s.charAt(i) == t.charAt(j)) {
                    dp[j] += prev;
                }

                prev = old;
            }
        }

        return dp[0];
    }
}
```
