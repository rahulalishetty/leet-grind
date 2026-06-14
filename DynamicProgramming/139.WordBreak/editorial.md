# 139. Word Break — Exhaustive Approach Summary (Java)

## Problem restatement

Given:

- a string `s`
- a dictionary `wordDict`

return `true` if `s` can be segmented into one or more dictionary words.

A word may be reused multiple times.

Example:

```text
s = "leetcode"
wordDict = ["leet", "code"]
```

Answer:

```text
true
```

because:

```text
"leetcode" = "leet" + "code"
```

---

## Core observation

At any index `i`, the real question is:

> Can the suffix `s[i..n-1]` be segmented using dictionary words?

From index `i`, try every dictionary word or every ending position `j` such that:

```text
s[i..j]
```

is a valid word.

If any such choice leads to a segmentable suffix, the answer is `true`.

This makes the problem a classic **prefix/suffix decomposition DP** problem.

---

# Approach 1 — Plain recursion (brute force)

## Idea

Start from index `0`.

At each position, try every possible ending index `end`:

- if `s[start..end]` is in the dictionary
- recursively check whether the rest can also be segmented

If any branch succeeds, return `true`.

---

## State definition

Let:

```text
dfs(start) = whether substring s[start..n-1] can be segmented
```

Then:

- try every `end` from `start` to `n - 1`
- if `s[start..end]` is a dictionary word and `dfs(end + 1)` is true, return true
- otherwise false

---

## Base case

If:

```text
start == s.length()
```

then we have segmented the entire string successfully, so return `true`.

---

## Java code

```java
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        return dfs(s, 0, words);
    }

    private boolean dfs(String s, int start, Set<String> words) {
        if (start == s.length()) {
            return true;
        }

        for (int end = start; end < s.length(); end++) {
            String prefix = s.substring(start, end + 1);
            if (words.contains(prefix) && dfs(s, end + 1, words)) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Time complexity

This is exponential in the worst case.

Why?

Because the same suffix may be recomputed many times.

A classic bad case is:

```text
s = "aaaaaaa....ab"
wordDict = ["a", "aa", "aaa", ...]
```

Many possible prefix choices are explored repeatedly.

A rough upper bound is:

```text
O(2^n)
```

or more precisely exponential with substring work on top.

---

## Space complexity

Recursion depth can be at most `n`:

```text
O(n)
```

---

## Strengths

- Very intuitive
- Best for deriving the recurrence

## Weaknesses

- Too slow
- Recomputes the same suffix states many times

---

# Approach 2 — Top-down DP with memoization

## Idea

The recursive state depends only on `start`.

So cache:

```text
memo[start] = whether s[start..n-1] can be segmented
```

This removes repeated recomputation of suffixes.

---

## Java code

```java
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        Boolean[] memo = new Boolean[s.length()];
        return dfs(s, 0, words, memo);
    }

    private boolean dfs(String s, int start, Set<String> words, Boolean[] memo) {
        if (start == s.length()) {
            return true;
        }

        if (memo[start] != null) {
            return memo[start];
        }

        for (int end = start; end < s.length(); end++) {
            String prefix = s.substring(start, end + 1);
            if (words.contains(prefix) && dfs(s, end + 1, words, memo)) {
                memo[start] = true;
                return true;
            }
        }

        memo[start] = false;
        return false;
    }
}
```

---

## Time complexity

There are `n` states for `start`.

For each `start`, we try up to `n` endings.

If substring extraction is treated as O(length), then practical complexity is often described as:

```text
O(n^3)
```

If you abstract away substring creation and assume dictionary lookup dominates, it is often described as:

```text
O(n^2)
```

For Java interview discussions, `O(n^3)` is the safer practical upper bound because `substring(start, end + 1)` creates a new string.

---

## Space complexity

- memo array: `O(n)`
- recursion stack: `O(n)`
- dictionary set: `O(m)` where `m` is total dictionary storage

Auxiliary stack+memo:

```text
O(n)
```

---

## Why this is a big improvement

This turns the exponential brute force into polynomial DP by recognizing that the answer for each suffix is reused.

---

# Approach 3 — Bottom-up DP (standard solution)

This is the most standard interview solution.

## Idea

Define:

```text
dp[i] = whether prefix s[0..i-1] can be segmented
```

So:

- `dp[0] = true` because empty string is segmentable
- answer is `dp[n]`

For each ending position `i`, try splitting at every `j < i`:

- if `dp[j]` is true
- and `s[j..i-1]` is in the dictionary
- then `dp[i] = true`

---

## Transition

```text
dp[i] = true
if there exists j in [0, i) such that:
    dp[j] == true
    and s[j..i-1] in dictionary
```

---

## Java code

```java
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
}
```

---

## Time complexity

There are:

- `n` end positions
- for each, up to `n` split points

That gives `O(n^2)` checks.

But substring creation in Java can cost O(length), so a safer practical upper bound is:

```text
O(n^3)
```

If substring handling is ignored in high-level DP analysis, people often write:

```text
O(n^2)
```

---

## Space complexity

- DP array: `O(n)`
- dictionary set: `O(m)`

Auxiliary DP space:

```text
O(n)
```

---

## Why this is usually the default answer

It is:

- iterative
- easy to explain
- standard
- avoids recursion

This is often the safest first “optimized” answer in interviews.

---

# Approach 4 — Bottom-up DP optimized by maximum word length

## Idea

In the standard DP, from each `i`, we try all `j` from `0` to `i - 1`.

But dictionary words have bounded length.

If the longest word in `wordDict` has length `maxLen`, then when checking position `i`, we only need to consider splits:

```text
j >= i - maxLen
```

because any longer substring cannot possibly be in the dictionary.

This can significantly reduce the inner loop.

---

## Java code

```java
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }

        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = Math.max(0, i - maxLen); j < i; j++) {
                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
}
```

---

## Time complexity

Let `L` be the maximum word length.

Then for each `i`, we check at most `L` positions.

So abstractly:

```text
O(n * L)
```

If substring creation cost is included, a more practical upper bound is:

```text
O(n * L^2)
```

Given the constraints `wordDict[i].length <= 20`, this is extremely effective in practice.

---

## Space complexity

```text
O(n)
```

for the DP array, plus dictionary storage.

---

## Why this is a strong practical optimization

The constraints explicitly bound word length to at most 20.

That makes this optimization especially valuable here.

This is often the best real-world answer for this exact problem in Java.

---

# Approach 5 — BFS over indices

## Idea

Treat each index in the string as a node.

From position `start`, you can move to position `end + 1` if:

```text
s[start..end]
```

is a dictionary word.

Then the problem becomes:

> Is index `n` reachable from index `0`?

This is equivalent to graph reachability over string indices.

---

## Why BFS works

Each state is just a start index.

If from that index you can consume a dictionary word, you jump forward.

If you can reach the end of the string, answer is true.

To avoid repeated work, mark visited start indices.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        Queue<Integer> queue = new ArrayDeque<>();
        boolean[] visited = new boolean[s.length()];

        queue.offer(0);

        while (!queue.isEmpty()) {
            int start = queue.poll();

            if (visited[start]) {
                continue;
            }

            for (int end = start + 1; end <= s.length(); end++) {
                if (words.contains(s.substring(start, end))) {
                    if (end == s.length()) {
                        return true;
                    }
                    queue.offer(end);
                }
            }

            visited[start] = true;
        }

        return false;
    }
}
```

---

## Time complexity

There are up to `n` indices.

From each index, you may try all later endings.

So abstractly `O(n^2)`, but with substring creation in Java:

```text
O(n^3)
```

Again, can be improved with max word length pruning.

---

## Space complexity

- queue: up to `O(n)`
- visited: `O(n)`

Total:

```text
O(n)
```

---

## When this approach is useful

This is great for intuition because it reframes the problem as:

- states = positions
- transitions = consuming dictionary words

It is less common than DP as the final answer, but still very valid.

---

# Approach 6 — Trie + DFS / DP

## Idea

Instead of checking every substring and looking it up in a hash set, build a Trie from the dictionary.

Then from each start index, walk forward character by character through the Trie.

Whenever you hit a terminal Trie node, that means a dictionary word ends there, so recurse or DP from the next index.

This avoids creating many substring objects and can be efficient when the dictionary is large.

---

## Why Trie helps

With a hash set approach, you repeatedly build substrings like:

```java
s.substring(j, i)
```

A Trie lets you scan characters directly without creating those temporary strings.

---

## Java code

```java
import java.util.List;

class Solution {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    public boolean wordBreak(String s, List<String> wordDict) {
        TrieNode root = buildTrie(wordDict);
        Boolean[] memo = new Boolean[s.length()];
        return dfs(s, 0, root, memo);
    }

    private boolean dfs(String s, int start, TrieNode root, Boolean[] memo) {
        if (start == s.length()) {
            return true;
        }

        if (memo[start] != null) {
            return memo[start];
        }

        TrieNode node = root;
        for (int i = start; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (node.children[idx] == null) {
                break;
            }

            node = node.children[idx];
            if (node.isWord && dfs(s, i + 1, root, memo)) {
                memo[start] = true;
                return true;
            }
        }

        memo[start] = false;
        return false;
    }

    private TrieNode buildTrie(List<String> wordDict) {
        TrieNode root = new TrieNode();

        for (String word : wordDict) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int idx = ch - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.isWord = true;
        }

        return root;
    }
}
```

---

## Time complexity

Let:

- `n = s.length()`
- `L = maximum word length`

Then from each index, Trie walking can continue only as long as a dictionary prefix matches.

With memoization, each start index is solved once.

A practical upper bound is:

```text
O(n * L)
```

or more generally dependent on how much Trie matching succeeds.

Trie build cost is:

```text
O(total characters in dictionary)
```

---

## Space complexity

- Trie storage: proportional to total dictionary characters
- memo: `O(n)`
- recursion stack: `O(n)`

---

## Strengths

- Avoids repeated substring creation
- Elegant for dictionary-heavy string segmentation problems

## Weaknesses

- More code
- Usually not necessary unless optimizing harder or discussing advanced variants

---

# Approach 7 — DP by iterating over dictionary words instead of split points

## Idea

At each position `i` where `dp[i]` is true, try every dictionary word:

- if the word matches starting at `i`
- then mark `dp[i + word.length()] = true`

This can be better when dictionary size is moderate and word lengths are short.

---

## Java code

```java
import java.util.List;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 0; i < n; i++) {
            if (!dp[i]) {
                continue;
            }

            for (String word : wordDict) {
                int end = i + word.length();
                if (end <= n && s.startsWith(word, i)) {
                    dp[end] = true;
                }
            }
        }

        return dp[n];
    }
}
```

---

## Time complexity

Let:

- `n = s.length()`
- `d = wordDict.size()`
- `L = average / max word length`

Then checking `startsWith` for each word yields roughly:

```text
O(n * d * L)
```

This can be good if `d` is manageable and word lengths are short.

---

## Space complexity

```text
O(n)
```

for the DP array.

---

## When this approach is attractive

It is nice when:

- the dictionary is small/moderate
- words are short
- you want to avoid substring creation

It is not always asymptotically better, but often clean in practice.

---

# Detailed dry run of the standard bottom-up DP

Input:

```text
s = "leetcode"
wordDict = ["leet", "code"]
```

Let:

```text
dp[i] = whether s[0..i-1] can be segmented
```

So:

```text
dp[0] = true
```

because empty prefix is segmentable.

---

## i = 1, 2, 3

No valid dictionary word matches prefixes ending there.

So:

```text
dp[1] = dp[2] = dp[3] = false
```

---

## i = 4

Try splits:

- `j = 0`: `dp[0] == true` and `s[0..3] = "leet"` is in dictionary

So:

```text
dp[4] = true
```

---

## i = 5, 6, 7

No valid segmentation yet.

---

## i = 8

Try splits:

- `j = 4`: `dp[4] == true` and `s[4..7] = "code"` is in dictionary

So:

```text
dp[8] = true
```

Answer:

```text
true
```

---

# Another dry run

Input:

```text
s = "catsandog"
wordDict = ["cats","dog","sand","and","cat"]
```

Some prefixes are segmentable:

- `"cat"`
- `"cats"`
- `"sand"`
- `"and"`

But eventually you reach:

```text
"og"
```

which cannot be matched.

So `dp[n]` remains false.

Answer:

```text
false
```

---

# Edge cases

## 1. Single character string

If the character itself is in `wordDict`, answer is true; otherwise false.

---

## 2. Reusing words

Example:

```text
s = "applepenapple"
wordDict = ["apple", "pen"]
```

You are allowed to use `"apple"` more than once.

The DP handles this naturally because states only depend on positions, not on how many times a word was used.

---

## 3. No possible first word

If no dictionary word matches the prefix starting at index 0, the answer is immediately false.

---

## 4. Many overlapping decompositions

Example:

```text
s = "aaaaaaa"
wordDict = ["a", "aa", "aaa"]
```

There are many possible paths.

This is exactly where memoization / DP is crucial.

---

# Common mistakes

## Mistake 1 — Confusing this with Word Break II

This problem asks:

- return `true` / `false`

Word Break II asks:

- return all valid sentences

That changes the complexity and solution style a lot.

---

## Mistake 2 — Not using a hash set for dictionary lookup

If you keep `wordDict` as a list and search linearly every time, lookups become much slower.

Usually convert to:

```java
Set<String> words = new HashSet<>(wordDict);
```

---

## Mistake 3 — Ignoring substring creation cost in Java

Even if the high-level DP is `O(n^2)`, Java substring extraction creates new strings, which can add extra cost.

This is why Trie-based or `startsWith`-based methods can be appealing in Java.

---

## Mistake 4 — Forgetting `dp[0] = true`

This is the base case that makes the whole recurrence work.

It means:

> the empty prefix can always be segmented

Without it, no other state becomes true.

---

## Mistake 5 — Re-exploring the same start index in BFS/DFS

Without memoization or visited marking, exponential blow-up returns quickly.

The whole improvement comes from realizing:

> whether suffix `s[i..]` can be segmented is a reusable state

---

# Comparing all approaches

| Approach                         | Idea                                          |                                     Time |         Space | Notes                                 |
| -------------------------------- | --------------------------------------------- | ---------------------------------------: | ------------: | ------------------------------------- |
| Plain recursion                  | Try all dictionary-valid prefixes recursively |                              Exponential |        `O(n)` | Best only for deriving recurrence     |
| Top-down memoization             | Cache suffix results by start index           |     Usually `O(n^2)` to `O(n^3)` in Java |        `O(n)` | Strong recursive DP                   |
| Bottom-up DP                     | `dp[i] = prefix s[0..i-1] is segmentable`     |     Usually `O(n^2)` to `O(n^3)` in Java |        `O(n)` | Standard interview answer             |
| Bottom-up DP + max word length   | Restrict split search window                  |         Often `O(n * L)` to `O(n * L^2)` |        `O(n)` | Excellent practical optimization here |
| BFS over indices                 | Reachability among string positions           |                Similar polynomial bounds |        `O(n)` | Good graph interpretation             |
| Trie + DFS/DP                    | Match dictionary words character by character | Often around `O(n * L)` after Trie build | Trie + `O(n)` | Avoids many substring allocations     |
| DP by iterating dictionary words | From each reachable index, try each word      |                           `O(n * d * L)` |        `O(n)` | Nice when dictionary is moderate      |

Here:

- `n = s.length()`
- `L = max dictionary word length`
- `d = wordDict.size()`

---

# Which solution should you give in interviews?

The safest default answer is:

1. explain the recursive suffix idea
2. convert it to DP
3. present bottom-up DP
4. mention max-word-length optimization

That is usually both clear and practical.

If the interviewer wants a more advanced optimization discussion, mention Trie-based matching.

---

# Recommended final interview solution

This version is both standard and practical for the given constraints:

```java
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);

        int maxLen = 0;
        for (String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }

        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = Math.max(0, i - maxLen); j < i; j++) {
                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
}
```

---

## Time complexity of the final solution

Let `L` be the maximum word length.

A practical bound is:

```text
O(n * L^2)
```

in Java, because the inner loop checks at most `L` split points and substring creation may cost up to `O(L)`.

At a high-level abstract DP level, people may say:

```text
O(n * L)
```

if substring creation is ignored.

---

## Space complexity

```text
O(n)
```

for the DP array, plus dictionary storage.

---

# Compact interview explanation

You can explain it like this:

> Let `dp[i]` mean whether the prefix `s[0..i-1]` can be segmented.
> Then `dp[i]` is true if there exists some `j < i` such that:
>
> - `dp[j]` is true, and
> - `s[j..i-1]` is in the dictionary
>
> Initialize `dp[0] = true` for the empty prefix.
> To optimize, only check split points within the maximum dictionary word length.

That is usually concise and strong.

---

# Final takeaway

This problem is a classic **segmentation DP** problem.

The central mental model is:

> if you know which earlier prefixes are valid, you can decide whether the current prefix is valid by checking one more word

That same idea appears in many string DP problems.

---

# Practice extension

The most useful related problems after this are:

- Word Break II
- Palindrome Partitioning II
- Concatenated Words
- Regular Expression Matching

These help reinforce the difference between:

- boolean segmentation DP
- counting DP
- minimum-cost segmentation
- generating all segmentations
