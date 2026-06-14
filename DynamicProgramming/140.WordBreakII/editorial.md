# 140. Word Break II — Exhaustive Approach Summary (Java)

## Problem restatement

Given:

- a string `s`
- a dictionary `wordDict`

return **all possible sentences** formed by inserting spaces into `s` such that every word is in the dictionary.

You may reuse dictionary words.

Example:

```text
s = "catsanddog"
wordDict = ["cat", "cats", "and", "sand", "dog"]
```

Valid answers:

```text
"cats and dog"
"cat sand dog"
```

---

## Very important distinction from Word Break (139)

### Word Break (139)

Return:

```text
true / false
```

### Word Break II (140)

Return:

```text
all valid sentences
```

That changes the problem type a lot.

This is no longer just boolean DP.

This is a **generation problem** with overlapping subproblems.

So the natural solution is:

- DFS / backtracking to generate
- memoization / DP to avoid recomputing the same suffixes

---

## Core idea

At index `start`, try every dictionary-valid prefix:

```text
s[start..end]
```

If that prefix is a word, then recursively generate all valid sentences for the suffix starting at `end + 1`.

Then prepend the chosen word to each suffix sentence.

This is the heart of the problem.

---

# Approach 1 — Plain DFS / backtracking without memoization

## Idea

From each starting index:

- try every possible ending index
- if the substring is in the dictionary, recursively solve the suffix
- combine the chosen word with all suffix sentences

If `start == s.length()`, return a base representation of an empty sentence.

---

## State definition

Let:

```text
dfs(start) = all valid sentences that can be formed from s[start..n-1]
```

For each valid word `s[start..end]`:

- get all suffix sentences from `dfs(end + 1)`
- prepend the current word

---

## Base case

If:

```text
start == s.length()
```

then there is one valid decomposition of the empty suffix:

```text
[""]
```

This sentinel-style base case is very convenient for sentence joining.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        return dfs(s, 0, words);
    }

    private List<String> dfs(String s, int start, Set<String> words) {
        List<String> result = new ArrayList<>();

        if (start == s.length()) {
            result.add("");
            return result;
        }

        for (int end = start; end < s.length(); end++) {
            String word = s.substring(start, end + 1);

            if (!words.contains(word)) {
                continue;
            }

            List<String> suffixSentences = dfs(s, end + 1, words);

            for (String suffix : suffixSentences) {
                if (suffix.isEmpty()) {
                    result.add(word);
                } else {
                    result.add(word + " " + suffix);
                }
            }
        }

        return result;
    }
}
```

---

## Time complexity

This is exponential in the worst case.

Why?

Because the same suffix may be recomputed many times, and there can be exponentially many valid sentences.

A practical way to describe it:

```text
Exponential, dominated by repeated suffix generation
```

Even if the number of final answers is bounded by input generation, the naive recursion can still do redundant work.

---

## Space complexity

Ignoring output:

- recursion depth up to `O(n)`

Including output, total space can be very large because all valid sentences are returned.

---

## Strengths

- Very intuitive
- Directly matches the problem structure

## Weaknesses

- Recomputes the same suffix results many times
- Can become extremely slow

---

# Approach 2 — Top-down DFS with memoization (standard solution)

This is the most standard solution.

## Idea

The answer for a suffix starting at index `start` is reusable.

So cache:

```text
memo[start] = all valid sentences for s[start..n-1]
```

Then each suffix is solved once.

---

## State definition

```text
dfs(start) = List<String> of all valid sentences from s[start..]
```

Use memoization to avoid recomputing `dfs(start)`.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        Map<Integer, List<String>> memo = new HashMap<>();
        return dfs(s, 0, words, memo);
    }

    private List<String> dfs(String s, int start, Set<String> words,
                             Map<Integer, List<String>> memo) {
        if (memo.containsKey(start)) {
            return memo.get(start);
        }

        List<String> result = new ArrayList<>();

        if (start == s.length()) {
            result.add("");
            memo.put(start, result);
            return result;
        }

        for (int end = start; end < s.length(); end++) {
            String word = s.substring(start, end + 1);

            if (!words.contains(word)) {
                continue;
            }

            List<String> suffixSentences = dfs(s, end + 1, words, memo);

            for (String suffix : suffixSentences) {
                if (suffix.isEmpty()) {
                    result.add(word);
                } else {
                    result.add(word + " " + suffix);
                }
            }
        }

        memo.put(start, result);
        return result;
    }
}
```

---

## Time complexity

This is best described as:

```text
O(number of generated sentence fragments + substring / lookup work)
```

Since the output itself can be exponential, there is no polynomial worst-case bound in terms of output size alone.

A good practical description:

- each start index is solved once
- from each start, you try all possible endings
- but building all final sentences still costs at least proportional to total output size

So a realistic complexity statement is:

```text
O(n^2 + total output size)
```

if substring and dictionary checks are optimized conceptually.

In Java, substring creation adds overhead, so practical cost can be higher.

The key truth is:

> memoization removes repeated suffix generation, but cannot remove the inherent cost of producing all valid sentences

---

## Space complexity

- memo stores lists of sentences for suffixes
- recursion depth is `O(n)`

So space is heavily influenced by output size.

Auxiliary recursion depth:

```text
O(n)
```

but total memory including memoized sentences can be large, up to output scale.

---

## Why this is usually the best answer

It is:

- elegant
- standard
- avoids repeated work
- naturally returns all sentences

For interviews, this is usually the strongest default solution.

---

# Approach 3 — Top-down DFS with memoization + maximum word length optimization

## Idea

In Approach 2, from index `start` we try all endings up to the end of the string.

But dictionary words have limited length.

If the maximum word length is `maxLen`, then from `start` we only need to consider:

```text
end <= start + maxLen - 1
```

because longer substrings cannot be dictionary words.

This cuts unnecessary exploration.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }

        Map<Integer, List<String>> memo = new HashMap<>();
        return dfs(s, 0, words, memo, maxLen);
    }

    private List<String> dfs(String s, int start, Set<String> words,
                             Map<Integer, List<String>> memo, int maxLen) {
        if (memo.containsKey(start)) {
            return memo.get(start);
        }

        List<String> result = new ArrayList<>();

        if (start == s.length()) {
            result.add("");
            memo.put(start, result);
            return result;
        }

        int limit = Math.min(s.length(), start + maxLen);
        for (int end = start; end < limit; end++) {
            String word = s.substring(start, end + 1);

            if (!words.contains(word)) {
                continue;
            }

            List<String> suffixSentences = dfs(s, end + 1, words, memo, maxLen);

            for (String suffix : suffixSentences) {
                if (suffix.isEmpty()) {
                    result.add(word);
                } else {
                    result.add(word + " " + suffix);
                }
            }
        }

        memo.put(start, result);
        return result;
    }
}
```

---

## Time complexity

Still output-sensitive and potentially exponential in the number of valid sentences.

But the branching work is reduced to at most `maxLen` endings per start.

A practical description:

```text
O(n * maxLen + total output size related work)
```

with Java string construction overhead on top.

Given the constraint:

```text
wordDict[i].length <= 10
```

this optimization is especially useful here.

---

## Space complexity

Same memo/output considerations as Approach 2.

---

## Why this is a strong practical refinement

The input constraints make max word length small.

So this is often one of the best real-world versions in Java.

---

# Approach 4 — DFS + boolean DP precheck pruning

## Idea

Before generating all sentences, first solve Word Break I:

```text
canBreak[i] = whether suffix s[i..] is segmentable
```

Then during DFS, only recurse into suffixes that are known to be segmentable.

This can dramatically reduce wasted exploration on dead branches.

---

## Step 1 — Precompute feasibility

Let:

```text
canBreak[i] = whether s[i..n-1] can be segmented
```

Base case:

```text
canBreak[n] = true
```

Transition:

```text
canBreak[i] = true
if there exists j >= i such that:
    s[i..j] in dictionary
    and canBreak[j+1] == true
```

---

## Step 2 — DFS generation with pruning

When exploring from `start`, only recurse on `end + 1` if:

```text
canBreak[end + 1] == true
```

So impossible suffixes are skipped immediately.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }

        boolean[] canBreak = buildCanBreak(s, words, maxLen);
        List<String> result = new ArrayList<>();

        if (!canBreak[0]) {
            return result;
        }

        dfs(s, 0, words, maxLen, canBreak, new ArrayList<>(), result);
        return result;
    }

    private boolean[] buildCanBreak(String s, Set<String> words, int maxLen) {
        int n = s.length();
        boolean[] canBreak = new boolean[n + 1];
        canBreak[n] = true;

        for (int i = n - 1; i >= 0; i--) {
            int limit = Math.min(n, i + maxLen);
            for (int end = i; end < limit; end++) {
                if (words.contains(s.substring(i, end + 1)) && canBreak[end + 1]) {
                    canBreak[i] = true;
                    break;
                }
            }
        }

        return canBreak;
    }

    private void dfs(String s, int start, Set<String> words, int maxLen,
                     boolean[] canBreak, List<String> path, List<String> result) {
        if (start == s.length()) {
            result.add(String.join(" ", path));
            return;
        }

        int limit = Math.min(s.length(), start + maxLen);
        for (int end = start; end < limit; end++) {
            String word = s.substring(start, end + 1);

            if (words.contains(word) && canBreak[end + 1]) {
                path.add(word);
                dfs(s, end + 1, words, maxLen, canBreak, path, result);
                path.remove(path.size() - 1);
            }
        }
    }
}
```

---

## Time complexity

### Feasibility DP

With max length pruning:

```text
O(n * maxLen)
```

conceptually, plus substring handling.

### DFS generation

Still proportional to the number of valid generated outputs, but dead branches are pruned early.

So a good practical statement is:

```text
O(n * maxLen + output-related generation cost)
```

---

## Space complexity

- `canBreak`: `O(n)`
- recursion stack/path: `O(n)`
- output: proportional to answer size

Auxiliary space:

```text
O(n)
```

ignoring output strings.

---

## Why this approach is excellent in practice

This is often one of the most robust solutions:

- early impossibility pruning
- no repeated dead-end exploration
- good practical performance

It is especially useful when many branches look promising locally but fail later.

---

# Approach 5 — Bottom-up DP storing all sentences per prefix

## Idea

Instead of recursive memoization on suffixes, define:

```text
dp[i] = all valid sentences that build prefix s[0..i-1]
```

Then for every `i`, try all `j < i` such that:

- `dp[j]` has valid sentences
- `s[j..i-1]` is a dictionary word

Append the new word to every sentence in `dp[j]`.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int n = s.length();

        List<String>[] dp = new List[n + 1];
        for (int i = 0; i <= n; i++) {
            dp[i] = new ArrayList<>();
        }

        dp[0].add("");

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                String word = s.substring(j, i);

                if (!words.contains(word)) {
                    continue;
                }

                for (String prev : dp[j]) {
                    if (prev.isEmpty()) {
                        dp[i].add(word);
                    } else {
                        dp[i].add(prev + " " + word);
                    }
                }
            }
        }

        return dp[n];
    }
}
```

---

## Time complexity

This is highly output-sensitive.

It can be efficient for smaller answer sets, but storing all sentences for every prefix can cause lots of string copying.

A fair statement:

```text
O(total generated sentence construction work)
```

with potentially large overhead because partial sentences are stored at many prefixes.

---

## Space complexity

Can be very large, because sentences for many intermediate prefixes are stored simultaneously.

So this can be worse than top-down memoization in memory usage.

---

## Strengths

- Fully iterative
- Clear DP perspective

## Weaknesses

- Often memory heavier
- Repeated partial sentence storage can be expensive

---

# Approach 6 — Trie + DFS with memoization

## Idea

Build a Trie from dictionary words.

Then from each `start`, walk forward in the string character by character through the Trie.

Whenever a Trie node marks the end of a word, recursively solve the suffix.

This avoids repeated substring creation and can be elegant when dictionary size is large.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    public List<String> wordBreak(String s, List<String> wordDict) {
        TrieNode root = buildTrie(wordDict);
        Map<Integer, List<String>> memo = new HashMap<>();
        return dfs(s, 0, root, memo);
    }

    private List<String> dfs(String s, int start, TrieNode root,
                             Map<Integer, List<String>> memo) {
        if (memo.containsKey(start)) {
            return memo.get(start);
        }

        List<String> result = new ArrayList<>();

        if (start == s.length()) {
            result.add("");
            memo.put(start, result);
            return result;
        }

        TrieNode node = root;
        for (int i = start; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (node.children[idx] == null) {
                break;
            }

            node = node.children[idx];

            if (node.isWord) {
                String word = s.substring(start, i + 1);
                List<String> suffixSentences = dfs(s, i + 1, root, memo);

                for (String suffix : suffixSentences) {
                    if (suffix.isEmpty()) {
                        result.add(word);
                    } else {
                        result.add(word + " " + suffix);
                    }
                }
            }
        }

        memo.put(start, result);
        return result;
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

- Trie build: `O(total dictionary characters)`
- DFS with memo: each start index solved once, branching depends on Trie matches and output size

A practical description:

```text
O(total Trie traversal work + total output construction work)
```

---

## Space complexity

- Trie storage proportional to dictionary size
- memo/output can be large
- recursion depth `O(n)`

---

## When this approach is attractive

This is useful when you want to reduce substring lookup overhead and discuss more advanced dictionary matching.

For this problem, it is good but not required.

---

# Approach 7 — Backtracking with path list + final join

## Idea

Instead of recursively returning strings, keep a `path` list of words.

When a full segmentation is found, join them with spaces and add to result.

This is often cleaner from a backtracking standpoint.

However, without memoization it is not enough; with memoization, returning ready-made suffix lists is often simpler.

So this is more of a structural variant.

---

## Java code

```java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }

        boolean[] canBreak = buildCanBreak(s, words, maxLen);
        List<String> result = new ArrayList<>();

        backtrack(s, 0, words, maxLen, canBreak, new ArrayList<>(), result);
        return result;
    }

    private boolean[] buildCanBreak(String s, Set<String> words, int maxLen) {
        int n = s.length();
        boolean[] canBreak = new boolean[n + 1];
        canBreak[n] = true;

        for (int i = n - 1; i >= 0; i--) {
            int limit = Math.min(n, i + maxLen);
            for (int end = i; end < limit; end++) {
                if (words.contains(s.substring(i, end + 1)) && canBreak[end + 1]) {
                    canBreak[i] = true;
                    break;
                }
            }
        }

        return canBreak;
    }

    private void backtrack(String s, int start, Set<String> words, int maxLen,
                           boolean[] canBreak, List<String> path, List<String> result) {
        if (start == s.length()) {
            result.add(String.join(" ", path));
            return;
        }

        int limit = Math.min(s.length(), start + maxLen);
        for (int end = start; end < limit; end++) {
            String word = s.substring(start, end + 1);
            if (words.contains(word) && canBreak[end + 1]) {
                path.add(word);
                backtrack(s, end + 1, words, maxLen, canBreak, path, result);
                path.remove(path.size() - 1);
            }
        }
    }
}
```

---

## Time complexity

Output-sensitive, similar to other generation approaches.

---

## Space complexity

- recursion/path: `O(n)`
- result: proportional to total answer size

---

## Why this version is useful

Some people find this style more intuitive because it mirrors classic backtracking patterns.

It is especially nice when combined with the boolean prune table.

---

# Detailed dry run of the memoized DFS solution

Input:

```text
s = "catsanddog"
wordDict = ["cat","cats","and","sand","dog"]
```

---

## dfs(0)

Try prefixes from index 0:

- `"c"` no
- `"ca"` no
- `"cat"` yes → recurse `dfs(3)`
- `"cats"` yes → recurse `dfs(4)`

---

## dfs(3) for suffix `"sanddog"`

Try prefixes:

- `"s"` no
- `"sa"` no
- `"san"` no
- `"sand"` yes → recurse `dfs(7)`

---

## dfs(7) for suffix `"dog"`

Try prefixes:

- `"d"` no
- `"do"` no
- `"dog"` yes → recurse `dfs(10)`

---

## dfs(10)

At the end of string.

Return:

```text
[""]
```

---

## Build back upward

From `dfs(7)`:

- word = `"dog"`
- suffix = `""`

Result:

```text
["dog"]
```

From `dfs(3)`:

- word = `"sand"`
- suffix sentence = `"dog"`

Result:

```text
["sand dog"]
```

From `dfs(0)` via `"cat"`:

```text
"cat" + " sand dog" = "cat sand dog"
```

---

## dfs(4) for suffix `"anddog"`

Try prefixes:

- `"a"` no
- `"an"` no
- `"and"` yes → recurse `dfs(7)`

But `dfs(7)` is already memoized as:

```text
["dog"]
```

So:

```text
"and dog"
```

Then from `dfs(0)` via `"cats"`:

```text
"cats and dog"
```

Final result:

```text
["cat sand dog", "cats and dog"]
```

---

# Edge cases

## 1. No valid segmentation

```text
s = "catsandog"
wordDict = ["cats","dog","sand","and","cat"]
```

No complete path reaches the end.

Answer:

```text
[]
```

---

## 2. Multiple overlapping words

```text
s = "pineapplepenapple"
wordDict = ["apple","pen","applepen","pine","pineapple"]
```

Different valid prefix choices lead to multiple valid full sentences.

This is exactly why memoizing suffix results is so valuable.

---

## 3. Single-character string

If the character is in the dictionary, return `[s]`, otherwise `[]`.

---

# Common mistakes

## Mistake 1 — Solving it like Word Break I

Word Break I only needs `true/false`.

Word Break II requires building all actual sentences.

So boolean DP alone is not enough.

---

## Mistake 2 — Forgetting that output size can be exponential

Even the best algorithm cannot avoid spending time proportional to the total answer size.

So do not expect a purely polynomial bound independent of output.

---

## Mistake 3 — Not memoizing suffix results

Without memoization, the same suffix gets recomputed repeatedly, causing huge blow-ups.

Memoization is the key optimization.

---

## Mistake 4 — Wrong handling of the base case

Returning:

```java
[""]
```

at the end is intentional.

It makes combining strings elegant.

If you return an empty list instead, no sentence gets formed correctly.

---

## Mistake 5 — Adding extra spaces incorrectly

When combining:

- if suffix is empty, add just `word`
- otherwise add:
  ```java
  word + " " + suffix
  ```

Otherwise you may get trailing spaces or malformed sentences.

---

## Mistake 6 — Using a plain list instead of a set for dictionary lookup

Always convert:

```java
Set<String> words = new HashSet<>(wordDict);
```

for O(1)-average membership checks.

---

# Comparing all approaches

| Approach                       | Idea                                              |                                     Time |                     Space | Notes                                                |
| ------------------------------ | ------------------------------------------------- | ---------------------------------------: | ------------------------: | ---------------------------------------------------- |
| Plain DFS/backtracking         | Try all valid prefixes recursively                |           Exponential with repeated work |       `O(n)` aux + output | Simplest but inefficient                             |
| DFS + memoization              | Cache suffix sentence lists                       | Output-sensitive, usually best practical |         Memo/output heavy | Standard answer                                      |
| DFS + memo + max word length   | Limit candidate prefix lengths                    |                 Better practical pruning | Similar memo/output costs | Excellent refinement here                            |
| DFS + boolean DP prune         | First precompute feasible suffixes, then generate |     Output-sensitive with strong pruning |       `O(n)` aux + output | Very robust practical approach                       |
| Bottom-up DP storing sentences | Build all sentences per prefix                    |     Output-sensitive, often memory-heavy |              Can be large | Fully iterative alternative                          |
| Trie + DFS + memo              | Avoid repeated substring lookups                  |            Output-sensitive + Trie build |        Trie + memo/output | Advanced dictionary matching approach                |
| Backtracking with path + join  | Build path lists then join at leaves              |                         Output-sensitive |       `O(n)` aux + output | Clean backtracking style, often paired with prune DP |

---

# Which solution should you give in interviews?

The best default answer is:

1. explain DFS over valid prefixes
2. mention overlapping suffix subproblems
3. add memoization
4. optionally mention max word length or feasibility pruning

That gives a strong and complete solution.

If the interviewer wants a practical optimization discussion, add the boolean DP prune table.

---

# Recommended final interview solution

This is the cleanest standard solution:

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }

        Map<Integer, List<String>> memo = new HashMap<>();
        return dfs(s, 0, words, memo, maxLen);
    }

    private List<String> dfs(String s, int start, Set<String> words,
                             Map<Integer, List<String>> memo, int maxLen) {
        if (memo.containsKey(start)) {
            return memo.get(start);
        }

        List<String> result = new ArrayList<>();

        if (start == s.length()) {
            result.add("");
            memo.put(start, result);
            return result;
        }

        int limit = Math.min(s.length(), start + maxLen);
        for (int end = start; end < limit; end++) {
            String word = s.substring(start, end + 1);

            if (!words.contains(word)) {
                continue;
            }

            List<String> suffixSentences = dfs(s, end + 1, words, memo, maxLen);

            for (String suffix : suffixSentences) {
                if (suffix.isEmpty()) {
                    result.add(word);
                } else {
                    result.add(word + " " + suffix);
                }
            }
        }

        memo.put(start, result);
        return result;
    }
}
```

---

## Time complexity of the final solution

The honest answer is:

```text
Output-sensitive
```

because all valid sentences must be generated.

A good practical description is:

```text
O(number of suffix states explored + total output construction work)
```

with memoization ensuring each start index is solved once.

Because the answer length is guaranteed not to exceed `10^5`, this is manageable for the given constraints.

---

## Space complexity

- recursion depth: `O(n)`
- memoized sentence lists: proportional to stored output
- final result: proportional to total answer size

So the dominant factor is output/memo storage.

---

# Compact interview explanation

You can explain it like this:

> Use DFS where `dfs(i)` returns all valid sentences that can be formed from suffix `s[i..]`.
> For each dictionary-valid prefix `s[i..j]`, recursively get all suffix sentences from `dfs(j+1)` and prepend the current word.
> Memoize `dfs(i)` so each suffix is solved only once.
> This is efficient because it removes repeated suffix work, while still generating all required answers.

That is usually the cleanest explanation.

---

# Final takeaway

This problem is a classic example of:

- **generation**
- plus **overlapping suffix subproblems**
- solved elegantly with **DFS + memoization**

The key pattern is:

> when a problem asks for all decompositions, memoize the list of decompositions for each suffix

That same pattern appears in many advanced string-generation problems.

---

# Practice extension

The most useful related problems after this are:

- Word Break (139)
- Palindrome Partitioning (131)
- Restore IP Addresses
- Expression Add Operators

These help strengthen the distinction between:

- boolean decomposition DP
- minimum-cost decomposition
- generating all valid decompositions
