# 1023. Camelcase Matching

## Problem Statement

You are given:

- an array of strings `queries`
- a string `pattern`

Return a boolean array `answer` where:

```text
answer[i] = true  if queries[i] matches pattern
answer[i] = false otherwise
```

A query matches the pattern if we can insert **lowercase English letters** into `pattern` so that it becomes exactly equal to the query.

Important implication:

- lowercase letters may be inserted freely
- uppercase letters in the query cannot be invented or skipped arbitrarily
- so uppercase structure must align with the pattern

---

## Example 1

```text
Input:
queries = ["FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"]
pattern = "FB"

Output:
[true,false,true,true,false]
```

Explanation:

- `"FooBar"` matches as `F + oo + B + ar`
- `"FootBall"` matches as `F + oot + B + all`
- `"FrameBuffer"` matches as `F + rame + B + uffer`

---

## Example 2

```text
Input:
queries = ["FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"]
pattern = "FoBa"

Output:
[true,false,true,false,false]
```

Explanation:

- `"FooBar"` matches as `Fo + o + Ba + r`
- `"FootBall"` matches as `Fo + ot + Ba + ll`

---

## Example 3

```text
Input:
queries = ["FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"]
pattern = "FoBaT"

Output:
[false,true,false,false,false]
```

Explanation:

- `"FooBarTest"` matches as `Fo + o + Ba + r + T + est`

---

## Constraints

- `1 <= pattern.length, queries.length <= 100`
- `1 <= queries[i].length <= 100`
- `queries[i]` and `pattern` consist of English letters

---

# Core Insight

A query matches the pattern if we can scan both from left to right and satisfy two rules:

1. if the current query character equals the current pattern character, consume both
2. otherwise, the query character is only allowed if it is lowercase, because lowercase letters may be inserted into the pattern

At the end:

- all pattern characters must be consumed
- any leftover characters in the query must all be lowercase

So this is fundamentally a **two-pointer matching** problem.

---

# Approach 1: Direct Two-Pointer Matching

## Intuition

Use one pointer for the query and one pointer for the pattern.

At each step:

- if characters match, move both pointers
- if they do not match:
  - if the query character is lowercase, skip it
  - if the query character is uppercase, matching fails immediately

After processing the query:

- the pattern pointer must be at the end
- if not, some pattern characters were never matched

This is the cleanest and most natural solution.

---

## Algorithm

For each query:

1. set `i = 0` for query
2. set `j = 0` for pattern
3. while `i < query.length()`:
   - if `j < pattern.length()` and `query[i] == pattern[j]`, increment both
   - else if `query[i]` is lowercase, increment `i`
   - else return `false`
4. return whether `j == pattern.length()`

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> answer = new ArrayList<>();

        for (String query : queries) {
            answer.add(matches(query, pattern));
        }

        return answer;
    }

    private boolean matches(String query, String pattern) {
        int i = 0, j = 0;

        while (i < query.length()) {
            if (j < pattern.length() && query.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else if (Character.isLowerCase(query.charAt(i))) {
                i++;
            } else {
                return false;
            }
        }

        return j == pattern.length();
    }
}
```

---

## Complexity Analysis

Let:

- `Q = queries.length`
- `L = average query length`
- `P = pattern.length()`

### Time Complexity

Each query is scanned once:

```text
O(query.length())
```

Across all queries:

```text
O(sum of query lengths)
```

With the constraints, this is easily efficient.

### Space Complexity

Ignoring the output list:

```text
O(1)
```

Extra working space is constant.

---

## Verdict

This is the best solution.

It is simple, exact, and optimal for this problem.

---

# Approach 2: Match Uppercase Skeleton First, Then Validate Full Pattern

## Intuition

A useful way to reason about the problem is to separate uppercase structure from full matching.

For a query to match:

1. the sequence of uppercase letters in the query must exactly match the uppercase letters in the pattern in the correct way
2. the remaining lowercase letters may be inserted freely, but actual pattern characters still must appear in order

This suggests a two-stage view:

- first check whether illegal uppercase letters exist
- then do ordered matching

In practice, this ends up very similar to the direct two-pointer method, but the perspective is valuable.

---

## Example

Consider:

```text
query   = "FooBarTest"
pattern = "FB"
```

The query contains uppercase letters:

```text
F B T
```

But pattern only accounts for:

```text
F B
```

The extra uppercase `T` cannot be inserted as lowercase noise.

So the answer is immediately `false`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> result = new ArrayList<>();

        for (String query : queries) {
            result.add(matches(query, pattern));
        }

        return result;
    }

    private boolean matches(String query, String pattern) {
        int j = 0;

        for (int i = 0; i < query.length(); i++) {
            char qc = query.charAt(i);

            if (j < pattern.length() && qc == pattern.charAt(j)) {
                j++;
            } else if (Character.isUpperCase(qc)) {
                return false;
            }
        }

        return j == pattern.length();
    }
}
```

---

## Why This Works

This version is essentially a slightly compressed version of the two-pointer method.

If a query character matches the next pattern character, consume that pattern character.

Otherwise:

- lowercase query letters can be ignored
- uppercase query letters cannot be ignored

So any unmatched uppercase character causes immediate failure.

---

## Complexity Analysis

### Time Complexity

```text
O(sum of query lengths)
```

### Space Complexity

```text
O(1)
```

---

## Verdict

Equivalent in strength to Approach 1, just written a bit more compactly.

---

# Approach 3: Explicit Simulation of Insertions (Conceptual, Not Recommended)

## Intuition

One naive way to think about the problem is:

Can we build the query from the pattern by inserting lowercase letters at arbitrary positions?

That suggests a recursive or dynamic programming formulation where we decide at each step whether the next query character comes from:

- matching the next pattern character
- or being an inserted lowercase letter

This is conceptually correct, but unnecessary.

The insertion freedom is so constrained that the greedy two-pointer scan is enough.

---

## Recursive State

A recursive formulation could use:

```text
f(i, j) = whether query[i...] can be formed from pattern[j...]
```

Transitions:

- if `query[i] == pattern[j]`, we may match both
- if `query[i]` is lowercase, we may treat it as inserted and skip it
- if `query[i]` is uppercase and does not match, fail

This is basically the same logic as the greedy approach, but with extra overhead.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> result = new ArrayList<>();

        for (String query : queries) {
            result.add(dfs(query, pattern, 0, 0));
        }

        return result;
    }

    private boolean dfs(String query, String pattern, int i, int j) {
        if (i == query.length()) {
            return j == pattern.length();
        }

        if (j < pattern.length() && query.charAt(i) == pattern.charAt(j)) {
            if (dfs(query, pattern, i + 1, j + 1)) {
                return true;
            }
        }

        if (Character.isLowerCase(query.charAt(i))) {
            return dfs(query, pattern, i + 1, j);
        }

        return false;
    }
}
```

---

## Complexity Analysis

In theory this can branch, though constraints are tiny enough that memoization could make it workable.

Without memoization, this is not the right solution style.

### Time Complexity

Potentially worse than needed.

### Space Complexity

Recursive stack plus optional memoization overhead.

---

## Verdict

Conceptually interesting, but overcomplicated.

The greedy scan already solves the problem cleanly.

---

# Approach 4: Dynamic Programming (Also Unnecessary)

## Intuition

We can define:

```text
dp[i][j] = whether first i characters of query
           can be formed from first j characters of pattern
```

Transition rules:

- if query character matches pattern character, inherit from `dp[i-1][j-1]`
- if query character is lowercase, it may be inserted, so inherit from `dp[i-1][j]`
- if query character is uppercase and does not match, state is false

This is valid, but again too heavy for a problem that has a greedy solution.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> answer = new ArrayList<>();

        for (String query : queries) {
            answer.add(matches(query, pattern));
        }

        return answer;
    }

    private boolean matches(String query, String pattern) {
        int n = query.length();
        int m = pattern.length();
        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true;

        for (int i = 1; i <= n; i++) {
            char qc = query.charAt(i - 1);

            if (Character.isLowerCase(qc)) {
                dp[i][0] = dp[i - 1][0];
            }

            for (int j = 1; j <= m; j++) {
                char pc = pattern.charAt(j - 1);

                if (qc == pc) {
                    dp[i][j] |= dp[i - 1][j - 1];
                }
                if (Character.isLowerCase(qc)) {
                    dp[i][j] |= dp[i - 1][j];
                }
            }
        }

        return dp[n][m];
    }
}
```

---

## Complexity Analysis

For one query:

```text
O(query.length() * pattern.length())
```

Across all queries:

```text
O(sum(query.length() * pattern.length()))
```

### Space Complexity

```text
O(query.length() * pattern.length())
```

---

## Verdict

Correct but clearly overkill.

---

# Why the Greedy Two-Pointer Approach Is Enough

This is the most important reasoning point.

Suppose we scan the query from left to right.

At a query character `q`:

- if it matches the next needed pattern character, consuming it is always safe
- if it does not match:
  - lowercase `q` can always be inserted
  - uppercase `q` cannot be inserted, so matching fails immediately

There is no benefit to postponing a valid match, because the pattern must appear in order and unmatched uppercase letters are fatal.

That is why greedy matching works.

---

# Common Mistakes

## 1. Ignoring extra uppercase letters in the query

This is the most common bug.

Example:

```text
query = "FooBarTest"
pattern = "FB"
```

Even though `F` and `B` appear in order, the extra uppercase `T` makes the answer `false`.

---

## 2. Requiring lowercase letters to match exactly

Lowercase letters may be inserted freely into the pattern.

So the query may contain many extra lowercase letters.

---

## 3. Forgetting that pattern must be fully consumed

Even if the query ends cleanly, all pattern characters must have been matched.

Example:

```text
query = "FoB"
pattern = "FoBa"
```

This is `false` because the final `a` in the pattern is never matched.

---

## 4. Using recursion or DP when a greedy scan is enough

The problem structure is much simpler than it first appears.

Two pointers solve it directly.

---

# Final Recommended Solution

Use the greedy two-pointer scan.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> answer = new ArrayList<>();

        for (String query : queries) {
            answer.add(matches(query, pattern));
        }

        return answer;
    }

    private boolean matches(String query, String pattern) {
        int i = 0, j = 0;

        while (i < query.length()) {
            if (j < pattern.length() && query.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else if (Character.isLowerCase(query.charAt(i))) {
                i++;
            } else {
                return false;
            }
        }

        return j == pattern.length();
    }
}
```

---

# Complexity Summary

## Two-pointer greedy

- Time: `O(sum of query lengths)`
- Space: `O(1)`

## Recursive simulation

- Time: unnecessary branching unless memoized
- Space: recursion stack / memo

## DP

- Time: `O(query.length() * pattern.length())` per query
- Space: `O(query.length() * pattern.length())`

---

# Interview Summary

The pattern can generate a query by inserting only lowercase letters.

So while scanning a query:

- matching characters consume the pattern
- unmatched lowercase letters are harmless
- unmatched uppercase letters are fatal

That leads directly to a simple two-pointer greedy solution, which is optimal and clean.
