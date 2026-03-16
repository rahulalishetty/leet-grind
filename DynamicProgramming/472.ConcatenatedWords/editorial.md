# 472. Concatenated Words — Exhaustive Solution Notes

## Overview

This problem asks us to find all words in a dictionary that can be formed by concatenating **at least two shorter words** from the same dictionary.

A useful way to think about it is:

- every word is a candidate target,
- the entire word list acts like a dictionary,
- and for each target word, we ask:

> Can this word be segmented into two or more valid dictionary words?

That makes this problem very closely related to **Word Break**.

The two approaches described here both use the same core idea:

1. **Dynamic Programming**
2. **DFS with memo-style visited pruning**

Even though one is iterative and the other is recursive, both are really solving the same **reachability** problem.

---

## Problem Statement

You are given an array of unique strings `words`.

Return all the **concatenated words** in the list.

A concatenated word is a string that is made entirely of **at least two shorter words** in the given array.

The words used to build the concatenated word:

- must exist in the same input array,
- may repeat,
- do not need to be distinct.

---

## Example 1

**Input**

```text
words = ["cat","cats","catsdogcats","dog","dogcatsdog","hippopotamuses","rat","ratcatdogcat"]
```

**Output**

```text
["catsdogcats","dogcatsdog","ratcatdogcat"]
```

**Explanation**

- `"catsdogcats"` = `"cats"` + `"dog"` + `"cats"`
- `"dogcatsdog"` = `"dog"` + `"cats"` + `"dog"`
- `"ratcatdogcat"` = `"rat"` + `"cat"` + `"dog"` + `"cat"`

These are all formed by concatenating at least two shorter words from the list.

---

## Example 2

**Input**

```text
words = ["cat","dog","catdog"]
```

**Output**

```text
["catdog"]
```

**Explanation**

```text
"catdog" = "cat" + "dog"
```

---

## Constraints

- `1 <= words.length <= 10^4`
- `1 <= words[i].length <= 30`
- `words[i]` consists only of lowercase English letters
- all strings in `words` are unique
- `1 <= sum(words[i].length) <= 10^5`

---

# Core Idea

Treat the word list as a dictionary.

For each word, ask:

> Can this word be formed by concatenating smaller dictionary words?

This is exactly a segmentation problem.

For example, if the word is:

```text
catsdogcats
```

then one valid split is:

```text
cats | dog | cats
```

So the problem becomes:

- for each word,
- determine whether it can be broken into valid pieces from the dictionary,
- with the extra condition that the whole word itself cannot be used directly as one piece.

That last condition is important.

---

# Important Corner Case

Suppose the dictionary contains `"catdog"`.

If we are checking whether `"catdog"` is a concatenated word, we must **not** simply accept it because `"catdog"` is present in the dictionary.

Why?

Because a concatenated word must be formed by **at least two shorter words**.

So we need to avoid the trivial match where the whole word matches itself in one step.

Both solutions handle this carefully.

---

# Approach 1: Dynamic Programming

## Intuition

For each word, define a reachability DP over prefixes.

Let:

```text
dp[i]
```

mean:

> whether the prefix `word[0...i-1]` can be formed by concatenating dictionary words

This is very similar to the standard Word Break problem.

If `dp[i]` is true, that means the first `i` characters are buildable.

To compute `dp[i]`, we try to split the prefix at some earlier position `j`:

- if `dp[j]` is true, and
- `word[j...i-1]` is in the dictionary,

then `dp[i]` becomes true.

---

## State Definition

For a word of length `L`, define:

```text
dp[i] = true if the prefix of length i can be formed by concatenation
```

That means:

- `dp[0]` refers to the empty string
- `dp[L]` tells whether the full word can be formed

---

## Base Case

The empty string is always considered reachable:

```text
dp[0] = true
```

This is standard in segmentation DP.

It means we can start building the word from nothing.

---

## Transition

For each index `i` from `1` to `L`, check whether there exists some `j < i` such that:

1. `dp[j] == true`
2. `word.substring(j, i)` exists in the dictionary

If both hold, then:

```text
dp[i] = true
```

---

## Why the Final Step Needs Special Handling

Normally, if the entire word is already in the dictionary, then for `i = L`, choosing `j = 0` would make:

```text
dp[L] = true
```

immediately.

But that would incorrectly classify every dictionary word as concatenated.

So when `i == L`, we do **not** allow `j = 0`.

That ensures the full word must be split into at least two parts.

In code, the inner loop starts from:

```text
j = (i == length ? 1 : 0)
```

This is the critical detail.

---

## Example Walkthrough

Suppose the dictionary contains:

```text
"cats", "dog"
```

and we check the word:

```text
"catsdog"
```

We build `dp`:

- `dp[0] = true`
- `dp[4] = true` because `"cats"` is in the dictionary
- `dp[7] = true` because:
  - `dp[4]` is true
  - `"dog"` is in the dictionary

So the full word is reachable through concatenation.

---

## Algorithm

1. Put all words into a hash set `dictionary`.
2. Create an empty result list `answer`.
3. For each word:
   - create a boolean array `dp` of size `length + 1`
   - set `dp[0] = true`
   - for each `i` from `1` to `length`:
     - try every split point `j`
     - if `dp[j]` is true and the suffix `word[j...i-1]` is in the dictionary, set `dp[i] = true`
     - when `i == length`, do not allow `j = 0`
4. If `dp[length]` is true, add the word to `answer`
5. Return `answer`

---

## Java Implementation — Dynamic Programming

```java
class Solution {
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        final Set<String> dictionary = new HashSet<>(Arrays.asList(words));
        final List<String> answer = new ArrayList<>();

        for (final String word : words) {
            final int length = word.length();
            final boolean[] dp = new boolean[length + 1];
            dp[0] = true;

            for (int i = 1; i <= length; ++i) {
                for (int j = (i == length ? 1 : 0); !dp[i] && j < i; ++j) {
                    dp[i] = dp[j] && dictionary.contains(word.substring(j, i));
                }
            }

            if (dp[length]) {
                answer.add(word);
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis — Dynamic Programming

Let:

- `N` = number of words
- `M` = maximum word length

### Time Complexity

Building the dictionary takes:

```text
O(N × M)
```

because hashing strings depends on their length.

Now for each word:

- outer loop over `i` runs `O(M)` times
- inner loop over `j` runs `O(M)` times
- substring creation and dictionary lookup may each cost up to `O(M)`

So for one word, the cost is:

```text
O(M^3)
```

For all words:

```text
O(N × M^3)
```

---

### Space Complexity

The dictionary stores all words:

```text
O(N × M)
```

The DP array per word is:

```text
O(M)
```

So the total dominant space complexity is:

```text
O(N × M)
```

---

# Approach 2: DFS

## Intuition

This problem can also be modeled as a graph reachability problem.

For a word of length `L`, think of each prefix length as a node:

```text
0, 1, 2, ..., L
```

Node `i` means:

> we have successfully built the prefix `word[0...i-1]`

There is an edge from `i` to `j` if:

- `word[i...j-1]` is in the dictionary,
- and the move is allowed under the concatenation rules.

Then the question becomes:

> Is node `L` reachable from node `0`?

That is a standard DFS/BFS problem.

---

## Graph Interpretation

For a word like `"catdog"`:

- node `0` = empty prefix
- node `3` = prefix `"cat"`
- node `6` = full word `"catdog"`

If `"cat"` is in the dictionary, then there is an edge:

```text
0 -> 3
```

If `"dog"` is in the dictionary, then:

```text
3 -> 6
```

So if `6` is reachable from `0`, the word is concatenated.

---

## Why This Works

Any valid concatenation corresponds to a path through prefix lengths.

For example:

```text
ratcatdogcat
```

can be seen as:

```text
0 -> 3 -> 6 -> 9 -> 12
```

where each jump corresponds to a dictionary word.

So checking whether a word is concatenated is exactly checking whether the end node is reachable.

---

## DFS State

The DFS function takes:

```text
dfs(word, length)
```

where `length` means:

> current prefix length already constructed

If `length == word.length()`, then we have reached the end of the word successfully.

---

## Visited Optimization

Without pruning, DFS may revisit the same prefix length many times.

So we maintain:

```text
visited[length]
```

meaning:

> starting DFS from this prefix length has already been tried and failed

If `visited[length]` is true, we can stop early.

This avoids repeated work and acts like memoization for failure states.

---

## Why We Must Handle the Full Word Carefully Here Too

At the start (`length == 0`), we must not allow the entire word itself to be chosen as one dictionary word.

That would incorrectly accept every dictionary word.

So in the loop, when `length == 0`, the candidate end index `i` must satisfy:

```text
i < word.length()
```

This is why the DFS loop starts from:

```text
word.length() - (length == 0 ? 1 : 0)
```

It prevents the first step from taking the whole word as one piece.

---

## Algorithm

For each word:

1. Use the full word list as a dictionary.
2. Create a `visited` array of size `word.length()`.
3. Run DFS from prefix length `0`.
4. In DFS:
   - if we reached the end of the word, return true
   - if this prefix length was already visited, return false
   - mark it visited
   - try every possible suffix starting at the current prefix length
   - if the suffix is a dictionary word and the recursive call succeeds, return true
5. If DFS succeeds, add the word to the answer

---

## Java Implementation — DFS

```java
class Solution {
    private boolean dfs(final String word, int length, final boolean[] visited, final Set<String> dictionary) {
        if (length == word.length()) {
            return true;
        }

        if (visited[length]) {
            return false;
        }

        visited[length] = true;

        for (int i = word.length() - (length == 0 ? 1 : 0); i > length; --i) {
            if (dictionary.contains(word.substring(length, i))
                && dfs(word, i, visited, dictionary)) {
                return true;
            }
        }

        return false;
    }

    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        final Set<String> dictionary = new HashSet<>(Arrays.asList(words));
        final List<String> answer = new ArrayList<>();

        for (final String word : words) {
            final int length = word.length();
            final boolean[] visited = new boolean[length];

            if (dfs(word, 0, visited, dictionary)) {
                answer.add(word);
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis — DFS

Let:

- `N` = number of words
- `M` = maximum word length

### Time Complexity

For one word:

- there are at most `M` starting positions
- from each starting position, there are up to `O(M)` possible end positions
- substring extraction and hash lookup may cost `O(M)`

So for one word, the time complexity is:

```text
O(M^3)
```

For all words:

```text
O(N × M^3)
```

---

### Space Complexity

The dictionary takes:

```text
O(N × M)
```

The visited array and recursion stack take:

```text
O(M)
```

So the overall dominant space complexity remains:

```text
O(N × M)
```

---

# Comparing DP and DFS

## Dynamic Programming

### Strengths

- explicit and iterative
- easy to reason about with prefix reachability
- very close to Word Break

### Weaknesses

- still does many substring checks
- same asymptotic complexity as DFS

---

## DFS

### Strengths

- elegant graph / reachability interpretation
- natural recursive structure
- visited array avoids repeated failed searches

### Weaknesses

- recursion overhead
- same substring cost as DP
- less explicit than DP for some readers

---

# Why Both Are Really Solving the Same Problem

DP asks:

> Is prefix `i` reachable?

DFS asks:

> Can we reach the end starting from prefix `i`?

These are two views of the same reachability structure.

So even though one is iterative and one is recursive, the core logic is identical:

- split the word into prefix + suffix
- require the suffix to be a dictionary word
- reduce the problem to a smaller prefix/subword state

---

# Common Mistakes

## 1. Allowing the whole word to match itself directly

This is the most important bug.

If the whole word is in the dictionary and you allow it as a single piece, then every word would incorrectly appear concatenated.

That is why both approaches exclude the trivial one-piece split.

---

## 2. Forgetting the "at least two words" condition

A concatenated word must be made of **two or more shorter words**.

Not one identical word.

---

## 3. Confusing this with exact prefix-only matching

The word can be split in many different ways.
You must test all valid segmentation points.

---

## 4. Ignoring substring/hash cost in complexity

Even with a `HashSet`, substring creation and hashing are not free.

That is why the stated complexity is `O(N × M^3)`, not just `O(N × M^2)`.

---

# Example Walkthrough with DP

Consider:

```text
words = ["cat", "dog", "catdog"]
```

Dictionary contains all three words.

Now check `"catdog"`:

Length = 6

Create:

```text
dp[0...6]
```

Initialize:

```text
dp[0] = true
```

Now compute:

- `dp[3] = true` because `"cat"` is in dictionary
- `dp[6] = true` because:
  - `dp[3]` is true
  - `"dog"` is in dictionary

So `"catdog"` is added to the answer.

But if we only allowed the split:

```text
0 -> 6
```

using `"catdog"` directly, that would be invalid for concatenation.
The special handling prevents this.

---

# Example Walkthrough with DFS

Again for `"catdog"`:

Start at node `0`.

Possible valid move:

```text
0 -> 3   because "cat" is in dictionary
```

Then from `3`:

```text
3 -> 6   because "dog" is in dictionary
```

Now node `6` equals the word length, so DFS returns true.

Thus `"catdog"` is concatenated.

---

# Final Summary

## Problem Reduction

For each word, determine whether it can be segmented into **at least two** dictionary words.

This is essentially a variant of **Word Break**.

---

## Approach 1: Dynamic Programming

### State

```text
dp[i] = whether prefix of length i can be formed
```

### Transition

```text
dp[i] = true if there exists j < i such that
dp[j] is true and word[j...i-1] is in dictionary
```

### Complexity

- Time: `O(N × M^3)`
- Space: `O(N × M)`

---

## Approach 2: DFS

### View

Treat prefix lengths as graph nodes and ask whether the end node is reachable from 0.

### Complexity

- Time: `O(N × M^3)`
- Space: `O(N × M)`

---

# Best Final Java Solution (DP)

```java
class Solution {
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        final Set<String> dictionary = new HashSet<>(Arrays.asList(words));
        final List<String> answer = new ArrayList<>();

        for (final String word : words) {
            final int length = word.length();
            final boolean[] dp = new boolean[length + 1];
            dp[0] = true;

            for (int i = 1; i <= length; ++i) {
                for (int j = (i == length ? 1 : 0); !dp[i] && j < i; ++j) {
                    dp[i] = dp[j] && dictionary.contains(word.substring(j, i));
                }
            }

            if (dp[length]) {
                answer.add(word);
            }
        }

        return answer;
    }
}
```

---

# Best Final Java Solution (DFS)

```java
class Solution {
    private boolean dfs(final String word, int length, final boolean[] visited, final Set<String> dictionary) {
        if (length == word.length()) {
            return true;
        }

        if (visited[length]) {
            return false;
        }

        visited[length] = true;

        for (int i = word.length() - (length == 0 ? 1 : 0); i > length; --i) {
            if (dictionary.contains(word.substring(length, i))
                && dfs(word, i, visited, dictionary)) {
                return true;
            }
        }

        return false;
    }

    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        final Set<String> dictionary = new HashSet<>(Arrays.asList(words));
        final List<String> answer = new ArrayList<>();

        for (final String word : words) {
            final boolean[] visited = new boolean[word.length()];
            if (dfs(word, 0, visited, dictionary)) {
                answer.add(word);
            }
        }

        return answer;
    }
}
```

These are the standard exhaustive solutions for the problem.
