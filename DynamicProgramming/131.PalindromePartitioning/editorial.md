# 131. Palindrome Partitioning — Exhaustive Approach Summary (Java)

## Problem restatement

Given a string `s`, partition it into substrings such that **every substring is a palindrome**.

Return **all possible palindrome partitions**.

Example:

```text
s = "aab"
```

Valid outputs:

```text
["a", "a", "b"]
["aa", "b"]
```

because:

- `"a"` is a palindrome
- `"aa"` is a palindrome
- `"b"` is a palindrome

---

## Important observation

This is not an optimization problem.

You are asked to return **all valid partitions**, not just the minimum number of cuts or the count of partitions.

That means the core task is:

> generate all possible ways to split the string, while pruning any split where a chosen piece is not a palindrome

So this is fundamentally a **backtracking / DFS generation** problem.

---

## Core backtracking idea

At index `start`, try every possible ending index `end` such that:

```text
s[start...end]
```

is a palindrome.

If it is, choose that substring, recurse on `end + 1`, and then backtrack.

This is the central idea behind the standard solution.

---

# Approach 1 — Pure backtracking with on-the-fly palindrome checking

## Idea

At every starting position, try all substrings starting there:

- `s[start..start]`
- `s[start..start+1]`
- `s[start..start+2]`
- ...

Whenever a substring is a palindrome, include it in the current path and continue recursively.

Once `start == s.length()`, we have consumed the full string, so the current path is one valid partition.

---

## State definition

During DFS we track:

- `start` → the next index we need to partition
- `path` → current list of chosen palindromic substrings
- `result` → all completed partitions

---

## Palindrome check

To test if `s[l..r]` is a palindrome:

- compare characters from both ends
- move inward

This takes:

```text
O(length of substring)
```

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        List<String> path = new ArrayList<>();
        backtrack(s, 0, path, result);
        return result;
    }

    private void backtrack(String s, int start, List<String> path, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                path.add(s.substring(start, end + 1));
                backtrack(s, end + 1, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
```

---

## Time complexity

This needs careful treatment.

There are exponentially many valid partitions in the worst case.

For example:

```text
s = "aaaaaaaaaaaaaaaa"
```

Almost every split is valid because every substring is a palindrome.

So the number of partitions itself is exponential.

A common upper-bound way to describe this solution is:

```text
O(n * 2^n) to O(n^2 * 2^n)
```

depending on how precisely you account for palindrome checks and substring creation.

A more practical explanation:

- there are up to `2^(n-1)` ways to place cuts
- for each recursive branch, palindrome checking may cost up to `O(n)`

So a safe upper bound is:

```text
O(n^2 * 2^n)
```

---

## Space complexity

Ignoring output storage:

- recursion depth is at most `n`
- current path can hold at most `n` substrings

So auxiliary space is:

```text
O(n)
```

Including the output, total space is exponential because the output itself can be exponential.

---

## Strengths

- Very intuitive
- Standard interview solution
- Easy to derive

## Weaknesses

- Repeats palindrome checks for the same substrings many times

---

# Approach 2 — Backtracking with precomputed palindrome DP table

## Idea

The inefficiency in Approach 1 is repeated palindrome testing.

The same substring `s[i..j]` may be checked many times in different recursive branches.

So first precompute whether every substring is a palindrome.

Then backtracking becomes faster because palindrome lookup is O(1).

---

## Palindrome DP definition

Let:

```text
isPal[i][j] = whether substring s[i..j] is a palindrome
```

Transition:

```text
isPal[i][j] = (s[i] == s[j]) && (j - i <= 2 || isPal[i + 1][j - 1])
```

Why?

A substring is a palindrome if:

- its end characters match, and
- its middle part is also a palindrome
- or the substring length is 1, 2, or 3 where the middle condition is trivial

---

## Precomputation order

We must compute shorter substrings before longer ones.

A common way:

- iterate `i` from `n - 1` down to `0`
- iterate `j` from `i` up to `n - 1`

This ensures `isPal[i + 1][j - 1]` is already known when needed.

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<String>> partition(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        List<String> path = new ArrayList<>();
        backtrack(s, 0, isPal, path, result);
        return result;
    }

    private void backtrack(String s, int start, boolean[][] isPal,
                           List<String> path, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPal[start][end]) {
                path.add(s.substring(start, end + 1));
                backtrack(s, end + 1, isPal, path, result);
                path.remove(path.size() - 1);
            }
        }
    }
}
```

---

## Time complexity

### DP precomputation

There are `n * n` substring states:

```text
O(n^2)
```

### Backtracking

Still exponential in the number of partitions, because we must output all valid partitions.

A practical bound is:

```text
O(n * 2^n)
```

or more conservatively, including substring copying and result construction:

```text
O(n * 2^n + n^2)
```

Since output itself can be exponential, the important point is:

> precomputation removes repeated palindrome testing, but cannot remove the exponential cost of generating all answers

---

## Space complexity

- palindrome table: `O(n^2)`
- recursion depth + path: `O(n)`
- output: exponential

Auxiliary space:

```text
O(n^2)
```

---

## Why this is usually the best practical answer

This is the most standard optimized solution:

- simple backtracking structure
- avoids repeated palindrome work
- easy to explain in interviews

---

# Approach 3 — Top-down DFS with memoization on suffix partitions

## Idea

Instead of thinking only in terms of backtracking with a mutable path, define:

```text
solve(start) = all palindrome partitions of substring s[start...n-1]
```

Then for every palindromic prefix `s[start..end]`, prepend it to every partition returned by `solve(end + 1)`.

This is a more functional / recursive decomposition view.

---

## State definition

```text
memo[start] = list of all palindrome partitions for suffix starting at index start
```

Base case:

```text
solve(n) = [[]]
```

Meaning:

- the empty suffix has one valid partition: the empty partition

This base case is powerful because it lets us append palindromic prefixes naturally.

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<String>> partition(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                }
            }
        }

        List<List<String>>[] memo = new List[n + 1];
        return dfs(s, 0, isPal, memo);
    }

    private List<List<String>> dfs(String s, int start, boolean[][] isPal,
                                   List<List<String>>[] memo) {
        if (memo[start] != null) {
            return memo[start];
        }

        List<List<String>> result = new ArrayList<>();

        if (start == s.length()) {
            result.add(new ArrayList<>());
            memo[start] = result;
            return result;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPal[start][end]) {
                String prefix = s.substring(start, end + 1);
                List<List<String>> suffixPartitions = dfs(s, end + 1, isPal, memo);

                for (List<String> suffix : suffixPartitions) {
                    List<String> partition = new ArrayList<>();
                    partition.add(prefix);
                    partition.addAll(suffix);
                    result.add(partition);
                }
            }
        }

        memo[start] = result;
        return result;
    }
}
```

---

## Time complexity

This is still exponential in the size of the output.

Memoization avoids recomputing suffix partitions, but it cannot avoid building every output partition.

A useful way to describe it:

```text
O(total output size + n^2)
```

Since total output size can be exponential, worst case remains exponential.

---

## Space complexity

- palindrome table: `O(n^2)`
- memoized partition lists: can be exponential due to stored output
- recursion stack: `O(n)`

So total space including memoized outputs can be very large, potentially exponential.

---

## Strengths

- Elegant recursive decomposition
- Good for understanding overlapping subproblems in generation problems

## Weaknesses

- Can use more memory than plain backtracking because it stores partitions for suffixes
- Less common as the first interview answer

---

# Approach 4 — Backtracking with center-expansion palindrome precomputation

## Idea

Instead of DP for palindromic substrings, precompute all palindromic intervals by expanding around centers.

Every palindrome has a center:

- odd-length palindromes centered at one character
- even-length palindromes centered between two characters

We can expand from each center and mark all palindromic ranges.

Then use backtracking exactly as before.

---

## How center expansion works

For each index `center`:

### Odd palindromes

Expand `(center, center)`

### Even palindromes

Expand `(center, center + 1)`

While characters match, mark:

```text
isPal[left][right] = true
```

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<String>> partition(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int center = 0; center < n; center++) {
            expand(s, center, center, isPal);       // odd length
            expand(s, center, center + 1, isPal);   // even length
        }

        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, isPal, new ArrayList<>(), result);
        return result;
    }

    private void expand(String s, int left, int right, boolean[][] isPal) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            isPal[left][right] = true;
            left--;
            right++;
        }
    }

    private void backtrack(String s, int start, boolean[][] isPal,
                           List<String> path, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPal[start][end]) {
                path.add(s.substring(start, end + 1));
                backtrack(s, end + 1, isPal, path, result);
                path.remove(path.size() - 1);
            }
        }
    }
}
```

---

## Time complexity

### Palindrome precomputation

Center expansion overall is:

```text
O(n^2)
```

### Backtracking

Still exponential in output size.

Total practical complexity:

```text
O(n^2 + output-related exponential work)
```

---

## Space complexity

- palindrome table: `O(n^2)`
- recursion/path: `O(n)`

Auxiliary space:

```text
O(n^2)
```

---

## Is this better than DP precomputation?

Asymptotically, no.

It is just another way to compute palindrome intervals.

Some people find the palindrome-DP recurrence easier to explain; others prefer center expansion because it matches the intuitive structure of palindromes.

---

# Approach 5 — Bitmask / cut-position enumeration

This is more of a conceptual alternative than a practical recommendation.

## Idea

There are `n - 1` possible cut positions between characters.

For each cut position, either place a cut or do not place a cut.

So there are:

```text
2^(n - 1)
```

possible ways to partition the string.

For each partition pattern:

1. extract all substrings
2. check whether every substring is a palindrome
3. if yes, add to answer

---

## Why it works

Every partition corresponds exactly to a set of cut positions.

So enumerating all cut sets enumerates all possible partitions.

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<String>> partition(String s) {
        int n = s.length();
        List<List<String>> result = new ArrayList<>();
        int totalMasks = 1 << (n - 1);

        for (int mask = 0; mask < totalMasks; mask++) {
            List<String> parts = new ArrayList<>();
            int start = 0;
            boolean valid = true;

            for (int i = 0; i < n - 1; i++) {
                if (((mask >> i) & 1) == 1) {
                    String part = s.substring(start, i + 1);
                    if (!isPalindrome(part)) {
                        valid = false;
                        break;
                    }
                    parts.add(part);
                    start = i + 1;
                }
            }

            if (!valid) {
                continue;
            }

            String lastPart = s.substring(start);
            if (isPalindrome(lastPart)) {
                parts.add(lastPart);
                result.add(parts);
            }
        }

        return result;
    }

    private boolean isPalindrome(String str) {
        int left = 0, right = str.length() - 1;
        while (left < right) {
            if (str.charAt(left++) != str.charAt(right--)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Time complexity

There are:

```text
2^(n - 1)
```

partitions to test.

For each one, substring extraction and palindrome checking may cost up to `O(n^2)` in the worst case.

So a rough upper bound is:

```text
O(n^2 * 2^n)
```

---

## Space complexity

Ignoring output:

```text
O(n)
```

for current partition pieces.

Output itself is exponential.

---

## Why this is usually not preferred

It is less elegant than DFS/backtracking because it generates all cut patterns blindly, even obviously bad ones.

Backtracking prunes sooner by checking palindromes incrementally.

Still, this approach is useful because it shows the full search space very explicitly.

---

# Detailed dry run of the standard backtracking solution

Input:

```text
s = "aab"
```

We start at index `0`.

---

## Step 1: start = 0

Try all substrings starting at `0`.

### end = 0

Substring:

```text
"a"
```

Palindrome → choose it.

Path becomes:

```text
["a"]
```

Recurse with `start = 1`.

---

## Step 2: start = 1

Try substrings starting at `1`.

### end = 1

Substring:

```text
"a"
```

Palindrome → choose it.

Path:

```text
["a", "a"]
```

Recurse with `start = 2`.

---

## Step 3: start = 2

Try substrings starting at `2`.

### end = 2

Substring:

```text
"b"
```

Palindrome → choose it.

Path:

```text
["a", "a", "b"]
```

Recurse with `start = 3`.

Now `start == s.length()`, so this is a complete partition.

Add:

```text
["a", "a", "b"]
```

Backtrack.

---

## Back to start = 1

No more choices from index `1`.

Backtrack to start = 0.

---

## Step 4: start = 0 again, next choice

### end = 1

Substring:

```text
"aa"
```

Palindrome → choose it.

Path:

```text
["aa"]
```

Recurse with `start = 2`.

At `start = 2`, choose `"b"`.

This gives:

```text
["aa", "b"]
```

Add it.

---

## Step 5: start = 0, final choice

### end = 2

Substring:

```text
"aab"
```

Not a palindrome → skip.

Done.

Final answer:

```text
[["a","a","b"], ["aa","b"]]
```

---

# Edge cases

## 1. Single character

```text
s = "a"
```

Only one partition exists:

```text
["a"]
```

---

## 2. All characters same

```text
s = "aaa"
```

Every substring is a palindrome.

So there are many valid partitions:

```text
["a","a","a"]
["a","aa"]
["aa","a"]
["aaa"]
```

This is the kind of input that causes exponential output size.

---

## 3. No long palindromes

```text
s = "abc"
```

Only single-character substrings work.

Answer:

```text
["a","b","c"]
```

only.

---

# Common mistakes

## Mistake 1 — Forgetting to copy the current path

When you reach a complete partition:

```java
result.add(new ArrayList<>(path));
```

must create a copy.

If you add `path` directly, later backtracking mutations will corrupt stored answers.

---

## Mistake 2 — Using substring end index incorrectly

In Java:

```java
s.substring(start, end + 1)
```

is required if `end` is inclusive.

A very common bug is forgetting the `+1`.

---

## Mistake 3 — Confusing this with Palindrome Partitioning II

This problem asks for:

- **all partitions**

Palindrome Partitioning II asks for:

- **minimum cuts**

That is a different problem with different DP goals.

---

## Mistake 4 — Rechecking palindrome substrings repeatedly without noticing

The plain backtracking solution is correct, but if performance matters, repeated palindrome checks are a hotspot.

Precomputing the palindrome table is the standard optimization.

---

## Mistake 5 — Wrong palindrome DP fill order

For:

```text
isPal[i][j] = (s[i] == s[j]) && isPal[i+1][j-1]
```

you must ensure `isPal[i+1][j-1]` is already known.

That is why `i` is usually iterated backward.

---

# Comparing all approaches

| Approach                                   | Idea                                         |                                         Time |                                Space | Notes                                          |
| ------------------------------------------ | -------------------------------------------- | -------------------------------------------: | -----------------------------------: | ---------------------------------------------- |
| Backtracking + on-the-fly palindrome check | DFS partitions, check palindrome each time   | Exponential, often bounded as `O(n^2 * 2^n)` |                  `O(n)` aux + output | Simplest standard solution                     |
| Backtracking + palindrome DP table         | Precompute `isPal[i][j]`                     |   `O(n^2 + output-related exponential work)` |                `O(n^2)` aux + output | Best practical answer                          |
| DFS on suffix partitions + memoization     | `solve(start)` returns all suffix partitions |                           Output-exponential | Can be large due to memoized outputs | Elegant, more memory heavy                     |
| Backtracking + center expansion table      | Precompute palindromes by center expansion   |   `O(n^2 + output-related exponential work)` |                `O(n^2)` aux + output | Alternative palindrome preprocessing           |
| Bitmask cut enumeration                    | Enumerate every cut pattern                  |                       Roughly `O(n^2 * 2^n)` |                  `O(n)` aux + output | Conceptually clear, less efficient in practice |

---

# Which solution should you give in interviews?

The strongest default answer is:

1. Explain plain backtracking
2. Mention repeated palindrome checks
3. Optimize with a precomputed palindrome DP table

That gives a very standard and strong final solution.

If the interviewer wants the simplest correct version first, start with Approach 1.
If they want the optimized version, give Approach 2.

---

# Recommended final interview solution

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<String>> partition(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, isPal, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(String s, int start, boolean[][] isPal,
                           List<String> path, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPal[start][end]) {
                path.add(s.substring(start, end + 1));
                backtrack(s, end + 1, isPal, path, result);
                path.remove(path.size() - 1);
            }
        }
    }
}
```

---

## Time complexity of the final solution

- palindrome table: `O(n^2)`
- DFS generation: exponential in number of valid partitions

A good practical description is:

```text
O(n^2 + output-related exponential work)
```

or more explicitly in worst case:

```text
O(n * 2^n + n^2)
```

with output itself potentially exponential.

---

## Space complexity of the final solution

- palindrome table: `O(n^2)`
- recursion/path: `O(n)`
- output: exponential

Auxiliary space:

```text
O(n^2)
```

---

# Compact interview explanation

You can explain it like this:

> Use backtracking to build partitions from left to right.
> At index `start`, try every substring `s[start..end]`. If it is a palindrome, choose it and recurse on `end + 1`.
> To avoid repeatedly checking the same substrings, precompute a boolean table `isPal[i][j]` that tells whether `s[i..j]` is a palindrome.
> This makes palindrome checks O(1), while the DFS still explores all valid partitions.

That is usually concise and strong.

---

# Final takeaway

This problem is a classic combination of:

- **backtracking** for generating all answers
- **DP preprocessing** for fast palindrome lookup

The big mental model is:

> generate choices recursively, but precompute expensive validity checks when they repeat

That pattern appears in many string-partitioning problems.

---

# Practice extension

The natural next related problems are:

- Palindrome Partitioning II
- Word Break II
- Restore IP Addresses
- Partition to K Equal Sum Subsets

These help reinforce the difference between:

- generating all valid decompositions
- optimizing over decompositions
- counting decompositions
