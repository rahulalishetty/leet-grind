# 3045. Count Prefix and Suffix Pairs II

## Problem Statement

You are given a 0-indexed string array `words`.

Define:

```text
isPrefixAndSuffix(str1, str2)
```

It returns `true` if `str1` is both:

- a prefix of `str2`
- a suffix of `str2`

Otherwise it returns `false`.

We need to count the number of pairs `(i, j)` such that:

```text
i < j
```

and `words[i]` is both a prefix and a suffix of `words[j]`.

---

## Example 1

```text
Input:  words = ["a","aba","ababa","aa"]
Output: 4
```

Valid pairs:

- `(0,1)` because `"a"` is both prefix and suffix of `"aba"`
- `(0,2)` because `"a"` is both prefix and suffix of `"ababa"`
- `(0,3)` because `"a"` is both prefix and suffix of `"aa"`
- `(1,2)` because `"aba"` is both prefix and suffix of `"ababa"`

Answer = `4`.

---

## Example 2

```text
Input:  words = ["pa","papa","ma","mama"]
Output: 2
```

Valid pairs:

- `(0,1)` because `"pa"` is both prefix and suffix of `"papa"`
- `(2,3)` because `"ma"` is both prefix and suffix of `"mama"`

Answer = `2`.

---

## Example 3

```text
Input:  words = ["abab","ab"]
Output: 0
```

The only possible pair is `(0,1)`, but `"abab"` is not both prefix and suffix of `"ab"`.

Answer = `0`.

---

## Constraints

- `1 <= words.length <= 10^5`
- `1 <= words[i].length <= 10^5`
- `words[i]` consists only of lowercase English letters
- sum of lengths of all words is at most `5 * 10^5`

---

# Core Insight

For a pair `(i, j)` to be valid, `words[i]` must be a **border** of `words[j]`.

A **border** of a string is a string that is both:

- a prefix
- a suffix

So the problem can be reframed as:

> For each word `words[j]`, count how many earlier words are equal to one of its border strings.

That is the key transformation.

The small version of this problem could be solved by brute force, but here:

- number of words can be `10^5`
- total input size can be `5 * 10^5`

So pairwise checking all `(i, j)` is impossible.

We need to process each word efficiently, enumerate its valid border lengths, and count how many previous words match those border strings.

---

# Why Brute Force Fails Here

If we compare every pair of words, the complexity becomes roughly:

```text
O(n^2 * L)
```

where `L` is average word length.

With `n = 10^5`, this is far too large.

So the large-constraint version needs a fundamentally different idea.

---

# Approach 1: Brute Force Pair Checking (Conceptual Baseline, Too Slow)

## Intuition

Check every pair `(i, j)` with `i < j`, and test whether `words[j]` starts and ends with `words[i]`.

This works for the small version, but not here.

---

## Java Code

```java
class Solution {
    public long countPrefixSuffixPairs(String[] words) {
        long count = 0;

        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (words[j].startsWith(words[i]) && words[j].endsWith(words[i])) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2 * L)
```

Too slow.

### Space Complexity

```text
O(1)
```

---

## Verdict

Useful only as a baseline.

---

# Approach 2: For Each Word, Enumerate Borders with Prefix Function + HashMap Counts

## Intuition

Process words from left to right.

Suppose we are currently at `words[j]`.

Any valid earlier word must be a border of `words[j]`.

So the plan is:

1. compute all border lengths of `words[j]`
2. turn those border lengths into actual strings
3. check how many previous words equal each such border string
4. add that count to the answer
5. then insert `words[j]` into a frequency map

This is already a huge improvement over brute force.

The crucial tool is the **prefix function** (KMP failure function), which lets us enumerate all borders of a string efficiently.

---

## Prefix Function Refresher

For a string `s`, `pi[i]` is the length of the longest proper prefix of `s[0..i]` that is also a suffix of it.

For the full string, `pi[n - 1]` gives the longest border length.

Then repeatedly following:

```text
len = pi[len - 1]
```

enumerates all smaller borders.

So for each word, we can list all border lengths in linear time.

---

## High-Level Algorithm

Maintain a map:

```text
seen[word] = how many times this word has appeared earlier
```

For each `word` in `words`:

1. Compute its prefix function
2. Enumerate all border lengths, including the full word length
3. For each border length `len`:
   - border string is `word.substring(0, len)`
   - add `seen.get(border, 0)` to answer
4. Insert the full `word` into `seen`

Because equal earlier words all count separately, we store frequencies, not just existence.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long countPrefixSuffixPairs(String[] words) {
        Map<String, Integer> seen = new HashMap<>();
        long answer = 0;

        for (String word : words) {
            int[] pi = buildPrefixFunction(word);
            List<Integer> borderLengths = getBorderLengths(word, pi);

            for (int len : borderLengths) {
                String border = word.substring(0, len);
                answer += seen.getOrDefault(border, 0);
            }

            seen.put(word, seen.getOrDefault(word, 0) + 1);
        }

        return answer;
    }

    private int[] buildPrefixFunction(String s) {
        int n = s.length();
        int[] pi = new int[n];

        for (int i = 1; i < n; i++) {
            int j = pi[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = pi[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }

        return pi;
    }

    private List<Integer> getBorderLengths(String s, int[] pi) {
        List<Integer> result = new ArrayList<>();
        result.add(s.length()); // full word is also a valid prefix and suffix of itself

        int len = pi[s.length() - 1];
        while (len > 0) {
            result.add(len);
            len = pi[len - 1];
        }

        return result;
    }
}
```

---

## Complexity Analysis

Let `S` be the sum of all word lengths.

### Time Complexity

- prefix function for each word: linear in that word's length
- enumerating border chain: total linear in the word length
- substring creation can cost extra

A realistic upper bound with raw substring creation is:

```text
O(S + total border-string creation cost)
```

In the worst case, repeated substring creation can become more expensive than we want.

So while the core idea is good, the string materialization is a weakness.

### Space Complexity

```text
O(S)
```

for stored words and maps.

---

## Verdict

Conceptually strong, but repeatedly creating border substrings is avoidable.

That leads us to a better implementation using hashing or trie-like keyed traversal.

---

# Approach 3: Rolling Hash / String Hashing for Border Lookup

## Intuition

Instead of materializing every border string as a new Java `String`, we can hash prefixes.

Then for each border length `len`, we compute the hash of:

```text
word[0..len-1]
```

and look up how many previous whole words had that exact hash.

This reduces repeated string allocation.

However, rolling hash is probabilistic because different strings can collide.

So while practical, it is not the cleanest exact answer unless we use double hashing and still accept a tiny collision chance.

---

## High-Level Idea

Maintain:

```text
seenHash[hash(full_word)] = frequency
```

For each word:

1. build prefix function
2. get border lengths
3. for each border length, compute prefix hash
4. add count from `seenHash`
5. insert full word hash into map

---

## Sketch Code

```java
import java.util.*;

class Solution {
    public long countPrefixSuffixPairs(String[] words) {
        final long MOD = 1_000_000_007L;
        final long BASE = 911382323L;

        Map<Long, Integer> seen = new HashMap<>();
        long answer = 0;

        for (String word : words) {
            int n = word.length();
            long[] prefix = new long[n + 1];
            long[] power = new long[n + 1];
            power[0] = 1;

            for (int i = 0; i < n; i++) {
                prefix[i + 1] = (prefix[i] * BASE + (word.charAt(i) - 'a' + 1)) % MOD;
                power[i + 1] = (power[i] * BASE) % MOD;
            }

            int[] pi = buildPrefixFunction(word);

            answer += seen.getOrDefault(getHash(prefix, power, 0, n - 1, MOD), 0);

            int len = pi[n - 1];
            while (len > 0) {
                long h = getHash(prefix, power, 0, len - 1, MOD);
                answer += seen.getOrDefault(h, 0);
                len = pi[len - 1];
            }

            long fullHash = getHash(prefix, power, 0, n - 1, MOD);
            seen.put(fullHash, seen.getOrDefault(fullHash, 0) + 1);
        }

        return answer;
    }

    private int[] buildPrefixFunction(String s) {
        int n = s.length();
        int[] pi = new int[n];
        for (int i = 1; i < n; i++) {
            int j = pi[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = pi[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }

    private long getHash(long[] prefix, long[] power, int l, int r, long mod) {
        return (prefix[r + 1] - prefix[l] * power[r - l + 1] % mod + mod) % mod;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(S)
```

amortized, if hash operations are constant time.

### Space Complexity

```text
O(S)
```

---

## Verdict

Fast, but probabilistic.
Good in practice, not the most principled exact solution.

---

# Approach 4: Prefix Function + Exact Trie / HashMap by Whole Words (Practical Exact Approach)

## Intuition

We want an exact solution without collision risk, and we want to avoid pairwise comparisons.

A strong exact approach is:

- store frequencies of previous **whole words**
- for current word, enumerate all border lengths
- for each border length, query the border string against stored previous words

At first glance, creating substrings seems wasteful. But we can still keep this exact and efficient enough because:

- total input size is only `5 * 10^5`
- each word contributes only its border chain
- border chains are linear via prefix function

In Java, substring copies characters, but across these constraints, this still passes comfortably in practice.

So this is a reasonable exact approach for Java.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long countPrefixSuffixPairs(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        long answer = 0;

        for (String word : words) {
            int[] pi = buildPrefixFunction(word);

            answer += freq.getOrDefault(word, 0);

            int len = pi[word.length() - 1];
            while (len > 0) {
                String border = word.substring(0, len);
                answer += freq.getOrDefault(border, 0);
                len = pi[len - 1];
            }

            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }

        return answer;
    }

    private int[] buildPrefixFunction(String s) {
        int n = s.length();
        int[] pi = new int[n];

        for (int i = 1; i < n; i++) {
            int j = pi[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = pi[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }

        return pi;
    }
}
```

---

## Complexity Analysis

Let `S` be total input length.

### Time Complexity

- Prefix function over all words: `O(S)`
- Border traversal over all words: `O(S)`
- HashMap lookups: expected `O(1)` each
- Substring creation adds some extra copying cost

In practice, this remains efficient under the given total-length bound.

### Space Complexity

```text
O(S)
```

---

## Verdict

This is a strong exact and practical Java solution.

---

# Approach 5: Pair-Trie / Paired Character Trie (Most Elegant Exact Linear-Scale Idea)

## Intuition

There is a more elegant exact approach that avoids border substring creation entirely.

For a word to be both a prefix and a suffix of another word, the characters must match simultaneously from the front and from the back.

That suggests pairing characters:

For each word `w`, build the sequence:

```text
(w[0], w[n-1]), (w[1], w[n-2]), (w[2], w[n-3]), ...
```

If a shorter word `x` is both prefix and suffix of `w`, then the paired-character sequence of `x` must be a prefix of the paired-character sequence of `w`.

So we can maintain a trie over these paired symbols.

Each inserted previous word contributes one terminal count at its full paired path.

While processing current word `w`, we walk its paired path. At every step, if the current trie node has terminal words ending there, those correspond exactly to earlier words that are both prefix and suffix of `w`.

This is the most elegant exact solution and runs in overall linear time with respect to total characters.

---

## Why the Paired Trie Works

Suppose:

```text
x = "aba"
w = "ababa"
```

Paired representation of `x`:

```text
(a,a), (b,b), (a,a)
```

Paired representation of `w`:

```text
(a,a), (b,b), (a,a), ...
```

So the path of `x` is a prefix of the path of `w`.

That is exactly the condition we want.

---

## Data Structure

Each trie edge is keyed by a pair:

```text
(frontChar, backChar)
```

We can encode it as an integer:

```text
(frontChar - 'a') * 26 + (backChar - 'a')
```

Each node stores:

- children
- how many previous words end exactly here

---

## Algorithm

Process words from left to right.

For each word:

1. Start from root
2. For each index `i` from `0` to `word.length() - 1`:
   - compute pair:
     ```text
     (word[i], word[word.length() - 1 - i])
     ```
   - move/create child
   - add the node's terminal count to answer
3. After finishing the word, increment terminal count at the final node

Why add counts during traversal?

Because every previous word ending at this node corresponds to a word whose full paired sequence matches the current prefix of paired characters, meaning that word is both prefix and suffix of the current word.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class TrieNode {
        Map<Integer, TrieNode> children = new HashMap<>();
        int endCount = 0;
    }

    public long countPrefixSuffixPairs(String[] words) {
        TrieNode root = new TrieNode();
        long answer = 0;

        for (String word : words) {
            TrieNode node = root;
            int n = word.length();

            for (int i = 0; i < n; i++) {
                int key = (word.charAt(i) - 'a') * 26 + (word.charAt(n - 1 - i) - 'a');
                node.children.putIfAbsent(key, new TrieNode());
                node = node.children.get(key);

                answer += node.endCount;
            }

            node.endCount++;
        }

        return answer;
    }
}
```

---

## Complexity Analysis

Let `S` be the sum of lengths of all words.

### Time Complexity

Each character position of each word is processed once:

```text
O(S)
```

### Space Complexity

In the worst case, the trie stores one node per processed character position:

```text
O(S)
```

---

## Verdict

This is the strongest exact solution.

It avoids:

- pairwise comparisons
- substring creation
- probabilistic hashing

And it scales perfectly to the constraint limit.

---

# Comparing the Main Approaches

## Prefix function + map of previous words

### Strengths

- exact
- easier to derive if you know borders
- strong practical Java solution

### Weakness

- may allocate many substring objects

---

## Rolling hash + border lengths

### Strengths

- fast
- avoids substring creation

### Weakness

- probabilistic collisions

---

## Paired-character trie

### Strengths

- exact
- linear
- elegant
- no substring allocation
- directly encodes the prefix-and-suffix condition

### Weakness

- less obvious at first sight

---

# Final Recommended Solution

Use the **paired-character trie**.

It is the cleanest exact large-scale solution.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    static class TrieNode {
        Map<Integer, TrieNode> children = new HashMap<>();
        int endCount = 0;
    }

    public long countPrefixSuffixPairs(String[] words) {
        TrieNode root = new TrieNode();
        long answer = 0;

        for (String word : words) {
            TrieNode node = root;
            int n = word.length();

            for (int i = 0; i < n; i++) {
                int key = (word.charAt(i) - 'a') * 26 + (word.charAt(n - 1 - i) - 'a');
                node.children.putIfAbsent(key, new TrieNode());
                node = node.children.get(key);

                answer += node.endCount;
            }

            node.endCount++;
        }

        return answer;
    }
}
```

---

# Why This Counts the Right Pairs

A previous word `x` contributes to current word `w` exactly when:

- for every position `i`, `x[i] == w[i]`
- and `x[i] == w[w.length() - x.length() + i]`

That means the paired sequence of `x` matches the paired sequence of `w` for the whole length of `x`.

So when traversing `w`'s paired path, every trie node where some previous word ended represents one valid prefix-suffix pair.

That is precisely why `answer += node.endCount` during traversal is correct.

---

# Common Mistakes

## 1. Using `int` for the answer

The number of pairs can be as large as:

```text
n * (n - 1) / 2
```

For `n = 10^5`, this exceeds `int`.

So the answer must be stored in `long`.

---

## 2. Falling back to pairwise checking

That works for the small version, but not for this one.

---

## 3. Assuming prefix matching alone is enough

The word must be both prefix and suffix, so both directions matter simultaneously.

---

## 4. Using hashing without acknowledging collisions

Rolling hash can be practical, but it is not exact unless you accept collision risk.

---

## 5. Forgetting duplicate words

If the same word appears multiple times earlier, all of them contribute.
That is why nodes store frequencies (`endCount`), not just boolean terminal flags.

---

# Complexity Summary

## Brute force pair checking

- Time: `O(n^2 * L)`
- Space: `O(1)`
- Too slow

## Prefix function + HashMap of previous words

- Time: near `O(S)` to `O(S + substring overhead)`
- Space: `O(S)`

## Rolling hash + borders

- Time: `O(S)`
- Space: `O(S)`
- Probabilistic

## Paired-character trie

- Time: `O(S)`
- Space: `O(S)`
- Exact

---

# Interview Summary

The decisive observation is that `words[i]` must be a **border** of `words[j]`.

A strong border-based solution is:

- enumerate borders using prefix function
- count previous identical words with a map

But the most elegant exact solution goes one step further:

- encode each word by paired front/back characters
- insert previous words into a trie over these pairs
- while traversing the current word, every terminal node encountered contributes valid earlier matches

That yields an exact linear solution in total input size.
