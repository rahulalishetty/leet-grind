# 3292. Minimum Number of Valid Strings to Form Target II

## Problem Statement

You are given an array of strings `words` and a string `target`.

A string `x` is called **valid** if `x` is a **prefix** of any string in `words`.

Return the **minimum number of valid strings** whose concatenation equals `target`.

If it is impossible, return `-1`.

---

## Example 1

```text
Input:
words = ["abc","aaaaa","bcdef"]
target = "aabcdabc"

Output:
3
```

Explanation:

We can form the target using:

- `"aa"` = prefix of `"aaaaa"`
- `"bcd"` = prefix of `"bcdef"`
- `"abc"` = prefix of `"abc"`

So answer = `3`.

---

## Example 2

```text
Input:
words = ["abababab","ab"]
target = "ababaababa"

Output:
2
```

Explanation:

We can form the target using:

- `"ababa"` = prefix of `"abababab"`
- `"ababa"` = prefix of `"abababab"`

So answer = `2`.

---

## Example 3

```text
Input:
words = ["abcdef"]
target = "xyz"

Output:
-1
```

Explanation:

No valid prefix can help form `"xyz"`.

---

## Constraints

- `1 <= words.length <= 100`
- `1 <= words[i].length <= 5 * 10^4`
- `sum(words[i].length) <= 10^5`
- `words[i]` contains only lowercase English letters
- `1 <= target.length <= 5 * 10^4`
- `target` contains only lowercase English letters

---

# Core Insight

A **valid string** is not arbitrary. It must be a **prefix of some word in `words`**.

That means at every position `i` in `target`, we want to know:

> what is the **longest prefix length** we can take starting from `target[i]` that matches a prefix of some word?

If from position `i` we can cover up to position `reach[i]`, then the problem becomes:

> Minimum number of jumps to go from index `0` to index `n`

where each jump from `i` can go to any position in:

```text
(i + 1) ... reach[i]
```

So this is a string-matching problem plus a minimum-segments / jump-game problem.

That is the decisive transformation.

---

# Why Problem II Is Harder

A brute-force DP that checks every word prefix at every target index can become too slow because:

- target length can be `5 * 10^4`
- total word length can be `10^5`

So the main difficulty is efficient matching.

Once we know the maximum reachable end from every index, the minimum count can be solved greedily or with DP.

---

# Approach 1: Naive DP with Direct Prefix Matching

## Intuition

Let:

```text
dp[i] = minimum number of valid strings needed to form target[i...]
```

Then at index `i`, we try every valid prefix that matches starting there.

If a valid prefix has length `len`, then:

```text
dp[i] = min(dp[i], 1 + dp[i + len])
```

This is correct, but generating all matching valid prefixes naively is too expensive.

---

## Why It Is Slow

For each index in `target`:

- try each word
- try each prefix length of that word
- compare characters

That can blow up badly.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minValidStrings(String[] words, String target) {
        int n = target.length();
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[n] = 0;

        for (int i = n - 1; i >= 0; i--) {
            for (String word : words) {
                int maxLen = Math.min(word.length(), n - i);

                for (int len = 1; len <= maxLen; len++) {
                    if (matches(word, target, i, len)) {
                        dp[i] = Math.min(dp[i], 1 + dp[i + len]);
                    } else {
                        break;
                    }
                }
            }
        }

        return dp[0] >= Integer.MAX_VALUE / 2 ? -1 : dp[0];
    }

    private boolean matches(String word, String target, int start, int len) {
        for (int i = 0; i < len; i++) {
            if (word.charAt(i) != target.charAt(start + i)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

This can degrade toward:

```text
O(targetLength * totalWordLength * averagePrefixChecks)
```

Too slow for the full constraints.

### Space Complexity

```text
O(targetLength)
```

---

## Verdict

Correct baseline, but not suitable for Problem II.

---

# Approach 2: Trie + DP

## Intuition

A trie stores all prefixes of all words naturally.

If we insert every word into a trie, then from a target position `i`, we can walk forward through the trie character by character:

- as long as target characters follow trie edges, every step corresponds to a valid prefix
- so every depth reached is a valid string length

Then we can run DP.

This is a major improvement because trie traversal shares prefix work.

---

## DP Transition

If from `target[i]` the trie lets us advance to `j`, then:

```text
dp[i] = min(dp[i], 1 + dp[j + 1])
```

for every reachable `j`.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class TrieNode {
        TrieNode[] next = new TrieNode[26];
    }

    public int minValidStrings(String[] words, String target) {
        TrieNode root = buildTrie(words);
        int n = target.length();
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[n] = 0;

        for (int i = n - 1; i >= 0; i--) {
            TrieNode node = root;

            for (int j = i; j < n; j++) {
                int c = target.charAt(j) - 'a';
                if (node.next[c] == null) {
                    break;
                }
                node = node.next[c];
                dp[i] = Math.min(dp[i], 1 + dp[j + 1]);
            }
        }

        return dp[0] >= Integer.MAX_VALUE / 2 ? -1 : dp[0];
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int idx = ch - 'a';
                if (node.next[idx] == null) {
                    node.next[idx] = new TrieNode();
                }
                node = node.next[idx];
            }
        }

        return root;
    }
}
```

---

## Complexity Analysis

Let:

- `N = target.length()`
- `S = sum(words[i].length())`

### Time Complexity

In the worst case, for each target position we may walk far in the trie:

```text
O(N^2)
```

in adversarial cases.

### Space Complexity

```text
O(S + N)
```

---

## Verdict

Much better conceptually, but still too slow in the worst case.

We need a way to get the **maximum match length** at each position much faster.

---

# Approach 3: Rolling Hash + Binary Search for Maximum Match

## Intuition

At every target position `i`, we want the maximum length `L` such that:

```text
target[i...i+L-1]
```

is a prefix of some word.

One possible approach:

- group prefixes by length using hashing
- precompute rolling hashes for the target
- binary search the largest valid length at each position

This can be made efficient, but it is probabilistic because of hash collisions.

Since exact solutions exist, rolling hash is more of an alternative than the best final answer.

---

## High-Level Idea

1. Precompute hashes for all word prefixes
2. Precompute rolling hash on target
3. For each position, binary search largest valid length
4. Reduce to minimum jumps / segments

---

## Verdict

Fast in practice, but not the strongest exact answer.

---

# Approach 4: Aho-Corasick Over All Prefixes (Too Heavy Conceptually)

## Intuition

Another thought is to insert **all valid strings**, meaning all prefixes of all words, into a matching automaton.

But the number of valid strings can be enormous in terms of explicit count, even though total prefix character content is bounded by total word length in trie representation.

Still, using Aho-Corasick here is awkward because:

- we do not actually need all matches
- we mainly need the **maximum prefix match starting at each position**

That makes a suffix-automaton-style approach less natural than necessary.

So while possible, it is not the cleanest way to reason about the problem.

---

# Approach 5: Trie + Greedy Interval Cover / Jump Game

## Intuition

This is the key exact approach.

Suppose for each index `i` in `target`, we know:

```text
furthest[i] = the furthest position we can reach by taking one valid string starting at i
```

Then the problem becomes:

> Cover the whole target using the minimum number of segments,
> where from position `i` one segment can end anywhere up to `furthest[i]`.

This is exactly the same structure as:

- minimum interval cover
- minimum jumps in Jump Game II

If at the current layer we can reach up to `currentEnd`, then among all starts inside this range we compute the maximum next reach.
When we must move beyond `currentEnd`, that means we used one more valid string.

So the problem has two parts:

1. compute `furthest[i]`
2. greedily count minimum jumps

---

## How to Compute `furthest[i]`

Using a trie, from each index `i` we can walk as far as target matches trie edges.

Since every trie depth corresponds to a valid prefix, the maximum depth reached gives the farthest endpoint.

That gives:

```text
furthest[i] = i + longestMatchLength
```

If no match exists at `i`, then `furthest[i] = i`.

---

## Greedy Rule

This is exactly Jump Game II style.

Maintain:

- `currentEnd`: end of the current segment layer
- `nextEnd`: farthest endpoint reachable from any start in the current layer
- `steps`: number of valid strings used

Scan from left to right.

If we ever reach an index beyond `nextEnd`, target is impossible.

Whenever we pass `currentEnd`, we must take another segment.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class TrieNode {
        TrieNode[] next = new TrieNode[26];
    }

    public int minValidStrings(String[] words, String target) {
        TrieNode root = buildTrie(words);
        int n = target.length();
        int[] furthest = new int[n];

        for (int i = 0; i < n; i++) {
            TrieNode node = root;
            int best = i;

            for (int j = i; j < n; j++) {
                int c = target.charAt(j) - 'a';
                if (node.next[c] == null) {
                    break;
                }
                node = node.next[c];
                best = j + 1;
            }

            furthest[i] = best;
        }

        int steps = 0;
        int currentEnd = 0;
        int nextEnd = 0;

        for (int i = 0; i < n; i++) {
            if (i > nextEnd) {
                return -1;
            }

            nextEnd = Math.max(nextEnd, furthest[i]);

            if (i == currentEnd) {
                if (currentEnd == nextEnd) {
                    return -1;
                }
                steps++;
                currentEnd = nextEnd;
                if (currentEnd >= n) {
                    return steps;
                }
            }
        }

        return currentEnd >= n ? steps : -1;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int idx = ch - 'a';
                if (node.next[idx] == null) {
                    node.next[idx] = new TrieNode();
                }
                node = node.next[idx];
            }
        }

        return root;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building trie:

```text
O(S)
```

where `S` is total word length.

Computing furthest naively from each target position can still be too much in worst case:

```text
O(N^2)
```

So while the greedy part is optimal, this version still needs better matching.

### Space Complexity

```text
O(S + N)
```

---

## Verdict

This reveals the right greedy structure, but naive trie scanning per position is still too slow.

We need a faster way to compute match lengths at all target positions.

---

# Approach 6: Z-Algorithm / Prefix-Matching Per Word + Greedy Jump Game

## Intuition

This is the strongest exact solution.

For each word `w`, we want to know, at every target position `i`, how many characters of `w` match `target[i...]` from the start of `w`.

That is exactly what the Z-algorithm can provide if we build:

```text
w + "#" + target
```

Then every position inside the target part tells us the LCP length between `w` and `target[i...]`.

Why is that enough?

Because **every prefix of a word is valid**.
So if the longest common prefix with `w` at position `i` is `L`, then from position `i` we may take any length from `1` to `L`.
The only thing we need for greedy jumping is the **maximum possible endpoint**, namely:

```text
i + L
```

We do this for every word, and keep the best possible reach at each target index.

After that, the problem becomes minimum jumps to cover `[0, n)`.

This gives an exact near-linear solution in total input size.

---

## Why This Is Efficient

For one word `w`, Z on:

```text
w + "#" + target
```

costs:

```text
O(|w| + |target|)
```

There are at most `100` words, and total word length is at most `10^5`, target length at most `5 * 10^4`.

So total is manageable:

```text
O(sum|w| + numberOfWords * |target|)
```

which fits comfortably here.

---

## Algorithm

1. Let `n = target.length`
2. Create array:

```text
maxReach[i] = furthest position reachable using one valid string starting at i
```

3. For each word `w`:
   - build combined string:
     ```text
     w + "#" + target
     ```
   - compute Z-array
   - for each target position `i`, extract match length:
     ```text
     L = min(z[offset + i], w.length())
     ```
   - update:
     ```text
     maxReach[i] = max(maxReach[i], i + L)
     ```

4. Run greedy minimum-jumps:
   - `currentEnd`, `nextEnd`, `steps`
   - exactly like Jump Game II
5. If coverage reaches `n`, return steps, otherwise `-1`

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minValidStrings(String[] words, String target) {
        int n = target.length();
        int[] maxReach = new int[n];

        for (String word : words) {
            String combined = word + "#" + target;
            int[] z = buildZ(combined);
            int offset = word.length() + 1;

            for (int i = 0; i < n; i++) {
                int matchLen = Math.min(z[offset + i], word.length());
                maxReach[i] = Math.max(maxReach[i], i + matchLen);
            }
        }

        int steps = 0;
        int currentEnd = 0;
        int nextEnd = 0;

        for (int i = 0; i < n; i++) {
            if (i > nextEnd) {
                return -1;
            }

            nextEnd = Math.max(nextEnd, maxReach[i]);

            if (i == currentEnd) {
                if (currentEnd == nextEnd) {
                    return -1;
                }
                steps++;
                currentEnd = nextEnd;
                if (currentEnd >= n) {
                    return steps;
                }
            }
        }

        return currentEnd >= n ? steps : -1;
    }

    private int[] buildZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
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

Let:

- `W = sum of word lengths`
- `M = number of words`
- `N = target.length()`

### Time Complexity

For each word, Z computation costs:

```text
O(|word| + N)
```

Across all words:

```text
O(W + M * N)
```

Greedy jump phase:

```text
O(N)
```

Total:

```text
O(W + M * N)
```

Given:

- `W <= 10^5`
- `M <= 100`
- `N <= 5 * 10^4`

this is efficient.

### Space Complexity

Main arrays:

- `maxReach`: `O(N)`
- one Z-array at a time: `O(|word| + N)`

So working space is:

```text
O(N + maxWordLength)
```

or more simply:

```text
O(N + max(|word|))
```

---

## Verdict

This is the best exact solution for this problem.

It is clean, scalable, and avoids the worst-case blowups of per-position trie traversal.

---

# Why the Greedy Jump Count Is Correct

Once we know `maxReach[i]`, the problem is:

> At position `i`, one chosen valid string can take us anywhere up to `maxReach[i]`.
> What is the minimum number of choices needed to reach the end?

This is identical to the classic Jump Game II greedy proof.

At any stage, suppose all positions up to `currentEnd` are reachable using `steps` strings.

Among those positions, compute the farthest point reachable with one more string:

```text
nextEnd = max(maxReach[i]) for i in current layer
```

To minimize the number of strings, we should always extend the current layer as far as possible before increasing the count.

That greedy rule is optimal.

---

# Comparing the Main Approaches

## Trie + DP

### Strengths

- intuitive
- direct representation of valid prefixes

### Weakness

- can be quadratic in worst case

---

## Trie + Greedy Jump Game

### Strengths

- identifies the correct minimum-cover structure

### Weakness

- still too slow if maxReach is computed naively

---

## Z-algorithm per word + Greedy

### Strengths

- exact
- efficient
- avoids substring hashing collisions
- turns matching into LCP computation cleanly

### Weakness

- slightly less obvious at first glance

---

## Rolling Hash

### Strengths

- fast in practice

### Weakness

- probabilistic
- weaker than exact Z solution here

---

# Final Recommended Solution

Use:

## Z-algorithm on each word against the target

plus

## greedy minimum-jump coverage

This is the strongest exact solution.

---

## Clean Final Java Solution

```java
class Solution {
    public int minValidStrings(String[] words, String target) {
        int n = target.length();
        int[] maxReach = new int[n];

        for (String word : words) {
            String combined = word + "#" + target;
            int[] z = new int[combined.length()];
            int left = 0, right = 0;

            for (int i = 1; i < combined.length(); i++) {
                if (i <= right) {
                    z[i] = Math.min(right - i + 1, z[i - left]);
                }

                while (i + z[i] < combined.length()
                        && combined.charAt(z[i]) == combined.charAt(i + z[i])) {
                    z[i]++;
                }

                if (i + z[i] - 1 > right) {
                    left = i;
                    right = i + z[i] - 1;
                }
            }

            int offset = word.length() + 1;
            for (int i = 0; i < n; i++) {
                int matchLen = Math.min(z[offset + i], word.length());
                maxReach[i] = Math.max(maxReach[i], i + matchLen);
            }
        }

        int steps = 0;
        int currentEnd = 0;
        int nextEnd = 0;

        for (int i = 0; i < n; i++) {
            if (i > nextEnd) {
                return -1;
            }

            nextEnd = Math.max(nextEnd, maxReach[i]);

            if (i == currentEnd) {
                if (currentEnd == nextEnd) {
                    return -1;
                }
                steps++;
                currentEnd = nextEnd;
                if (currentEnd >= n) {
                    return steps;
                }
            }
        }

        return -1;
    }
}
```

---

# Common Mistakes

## 1. Forgetting that **every prefix** of a word is valid

This is the most important detail.

If a word matches the target for `L` characters at some position, then all prefix lengths `1..L` are valid.

So only the **maximum match length** matters for reachability.

---

## 2. Using DP over all prefix lengths explicitly

That can lead to too many transitions.

Once we realize the problem is minimum interval cover / jump game, greedy becomes enough.

---

## 3. Treating this as exact word segmentation

We are not limited to whole words.
Any prefix of any word can be used.

---

## 4. Using brute-force prefix comparison at every target index

That can be too slow for `target.length = 5 * 10^4`.

---

## 5. Using `int` infinity carelessly

In DP versions, use a large safe value like:

```java
Integer.MAX_VALUE / 2
```

to avoid overflow on `+1`.

---

# Complexity Summary

## Naive DP with direct prefix matching

- Time: too slow, potentially very large
- Space: `O(N)`

## Trie + DP

- Time: can degrade toward `O(N^2)`
- Space: `O(S + N)`

## Rolling hash + greedy

- Time: fast in practice
- Space: linear
- Caveat: probabilistic

## Z-algorithm per word + greedy jump game

- Time: `O(W + M * N)`
- Space: `O(N + maxWordLength)`
- Exact and recommended

---

# Interview Summary

The real problem is:

1. for each target index, find how far one valid string can extend
2. use the minimum number of such extensions to cover the whole target

Because every prefix of every word is valid, we only care about the **longest match** from each position.

Z-algorithm gives those match lengths efficiently for each word against the target.

Once we have the farthest reach from every position, the rest is just **Jump Game II greedy**.

That is the cleanest exact solution.
