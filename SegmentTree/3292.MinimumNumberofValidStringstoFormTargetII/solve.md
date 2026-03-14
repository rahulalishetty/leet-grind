## Problem Restatement

You are given:

- an array of strings `words`
- a string `target`

A string `x` is called **valid** if `x` is a prefix of **at least one** string in `words`.

You want to form `target` by concatenating the **minimum number** of valid strings.

Return that minimum number, or `-1` if it is impossible.

---

## Key Constraints

```text
1 <= words.length <= 100
sum(words[i].length) <= 10^5
1 <= target.length <= 5 * 10^4
```

The important clue is that `target.length` can be as large as `5 * 10^4`.

That means:

- the straightforward `O(n^2)` DP from Version I is no longer good enough
- we need a more optimized way to know, for every starting position `i`, how far we can extend using a valid prefix

This is essentially a **minimum jumps / interval coverage** problem once we know the maximum valid prefix length starting at each index.

---

# Core Insight

If at position `i` in `target`, the longest valid prefix we can use has length `L[i]`, then from position `i` we may jump to any position in:

```text
[i + 1, i + L[i]]
```

because every shorter prefix is also valid.

Why?

If a string is a prefix of some word, then all its prefixes are also prefixes of that word.

So if we know the **maximum** valid length from `i`, we automatically know that **every smaller length** is also allowed.

That reduces the problem to:

> Starting from index `0`, what is the minimum number of jumps needed to reach index `n`, where from `i` you can jump as far as `i + L[i]`?

This is the same shape as the classic **minimum jumps to reach the end** problem.

So the real challenge becomes:

1. compute `L[i]` efficiently for every `i`
2. run greedy/DP on those jump lengths

---

# Approach 1: Trie + Greedy Interval Expansion (Recommended)

## Idea

Build a Trie from all words.

Then for each starting position `i` in `target`, walk forward in the Trie and find the maximum prefix of `target[i...]` that exists in the Trie.

Because every node in the Trie corresponds to a valid prefix, the traversal length directly gives `L[i]`.

Once all `L[i]` are known, solve the minimum-segments problem greedily.

---

## Why greedy works after `L[i]` is known

Suppose from every position `i` you know the farthest reachable point:

```text
reach[i] = i + L[i]
```

Then the problem becomes identical to covering the interval `[0, n)` with the fewest jumps.

Use the standard greedy idea:

- while scanning positions in the current reachable window, keep the farthest next reach
- when the current window ends, commit one more segment
- if you cannot advance, return `-1`

This is the same logic as Jump Game II.

---

## Trie Construction

Insert every word into the Trie.

Important detail:

A string is valid if it is a prefix of any word, not necessarily a complete word.

That means **every node in the Trie is already valid**.
We do not even need end-of-word markers for the main logic.

---

## Computing `L[i]`

Start from the Trie root and scan:

```text
target[i], target[i+1], ...
```

until no outgoing edge exists.

The number of matched characters is `L[i]`.

Because all prefixes along that path are valid, lengths `1..L[i]` are all usable from `i`.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class TrieNode {
        TrieNode[] next = new TrieNode[26];
    }

    public int minValidStrings(String[] words, String target) {
        TrieNode root = new TrieNode();

        // Build trie
        for (String w : words) {
            TrieNode cur = root;
            for (char ch : w.toCharArray()) {
                int idx = ch - 'a';
                if (cur.next[idx] == null) {
                    cur.next[idx] = new TrieNode();
                }
                cur = cur.next[idx];
            }
        }

        int n = target.length();
        int[] maxLen = new int[n];
        char[] t = target.toCharArray();

        // Compute max valid prefix length from each position
        for (int i = 0; i < n; i++) {
            TrieNode cur = root;
            int len = 0;

            for (int j = i; j < n; j++) {
                int idx = t[j] - 'a';
                if (cur.next[idx] == null) break;
                cur = cur.next[idx];
                len++;
            }

            maxLen[i] = len;
        }

        // Greedy minimum jumps
        int ans = 0;
        int curEnd = 0;
        int farthest = 0;

        for (int i = 0; i < n; i++) {
            if (i > farthest) return -1; // cannot even reach this position

            farthest = Math.max(farthest, i + maxLen[i]);

            if (i == curEnd) {
                if (curEnd == n) break;
                if (farthest == curEnd) return -1; // stuck
                ans++;
                curEnd = farthest;
                if (curEnd >= n) return ans;
            }
        }

        return curEnd >= n ? ans : -1;
    }
}
```

---

## Complexity

Let:

- `T = target.length()`
- `W = sum of lengths of all words`

Trie construction:

```text
O(W)
```

Computing `L[i]` naively by walking the Trie from each target position:

```text
O(T^2)` in the worst case
```

This may still be too slow for the largest case.

So while this approach is conceptually excellent, it is not the strongest worst-case implementation for Version II.

---

## Verdict

Great for intuition, but Version II really wants something stronger for computing all longest matches.

---

# Approach 2: Rolling Hash + Binary Search + Greedy Jumps

## Idea

We need to know, for each `i`, the **maximum** length `L[i]` such that:

```text
target[i .. i + L[i) - 1]
```

is a prefix of some word.

A clean way is:

1. Precompute hashes for all prefixes of all words, grouped by length.
2. Precompute rolling hashes for `target`.
3. For each start position `i`, binary search the maximum length whose substring hash belongs to the prefix-hash set of that length.
4. Then solve minimum jumps greedily.

---

## Why this works

A substring `target[i..j]` is valid iff it equals some prefix of some word.

So for every length `len`, we can store the hash of every valid prefix of length `len`.

Then checking whether `target[i..i+len-1]` is valid becomes `O(1)` hash lookup.

Binary searching the maximum valid length at each position gives all `L[i]`.

---

## Hash Preparation

For each word:

- compute all prefix hashes
- add the hash of prefix length `1`, `2`, ..., `word.length()` into `prefixSet[len]`

For `target`, use prefix rolling hash so substring hashes are `O(1)`.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 911382323L;

    public int minValidStrings(String[] words, String target) {
        int n = target.length();

        @SuppressWarnings("unchecked")
        HashSet<Long>[] prefixSets = new HashSet[n + 1];
        for (int i = 0; i <= n; i++) {
            prefixSets[i] = new HashSet<>();
        }

        // Store all valid prefixes by length
        for (String w : words) {
            long h = 0;
            int limit = Math.min(w.length(), n);
            for (int i = 0; i < limit; i++) {
                h = (h * BASE + (w.charAt(i) - 'a' + 1)) % MOD;
                prefixSets[i + 1].add(h);
            }
        }

        // Rolling hash for target
        long[] pref = new long[n + 1];
        long[] pow = new long[n + 1];
        pow[0] = 1;
        for (int i = 0; i < n; i++) {
            pref[i + 1] = (pref[i] * BASE + (target.charAt(i) - 'a' + 1)) % MOD;
            pow[i + 1] = (pow[i] * BASE) % MOD;
        }

        int[] maxLen = new int[n];

        for (int i = 0; i < n; i++) {
            int lo = 0, hi = n - i;
            while (lo < hi) {
                int mid = (lo + hi + 1) >>> 1;
                long h = getHash(pref, pow, i, i + mid - 1);
                if (prefixSets[mid].contains(h)) {
                    lo = mid;
                } else {
                    hi = mid - 1;
                }
            }
            maxLen[i] = lo;
        }

        // Greedy minimum jumps
        int ans = 0, curEnd = 0, farthest = 0;
        for (int i = 0; i < n; i++) {
            if (i > farthest) return -1;
            farthest = Math.max(farthest, i + maxLen[i]);

            if (i == curEnd) {
                if (curEnd == n) break;
                if (farthest == curEnd) return -1;
                ans++;
                curEnd = farthest;
                if (curEnd >= n) return ans;
            }
        }

        return curEnd >= n ? ans : -1;
    }

    private long getHash(long[] pref, long[] pow, int l, int r) {
        long val = (pref[r + 1] - pref[l] * pow[r - l + 1]) % MOD;
        if (val < 0) val += MOD;
        return val;
    }
}
```

---

## Complexity

Let `T = target.length()`, `W = sum(words[i].length)`.

Building prefix hash sets:

```text
O(W)
```

For each target position, binary search up to `O(log T)` lengths, each with `O(1)` hash check:

```text
O(T log T)
```

Greedy jumps:

```text
O(T)
```

Total:

```text
O(W + T log T)
```

Space:

```text
O(W)
```

---

## Pros

- Fast enough for Version II
- Clean separation: matching + jumping
- Very practical

## Cons

- Rolling hash introduces collision risk, though very low
- More technical than trie-based thinking

---

# Approach 3: Z-Algorithm / Prefix Matching Per Word + Best Reach Array

## Idea

For each word `w`, build the string:

```text
w + '#' + target
```

Then run the Z-algorithm.

For each position in `target`, Z tells us how many starting characters of `w` match there.

Since every prefix of `w` is valid, that gives a possible match length from that position.

Take the maximum over all words.

So we can compute:

```text
L[i] = max over all words of longest prefix match at target index i
```

Then use greedy jumps exactly as before.

---

## Why this is good

Instead of re-walking a trie from every target position, each word processes the whole target in linear time using Z-algorithm.

Because:

- total word length is at most `10^5`
- target length is at most `5 * 10^4`
- number of words is at most `100`

this can still be efficient enough in practice.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minValidStrings(String[] words, String target) {
        int n = target.length();
        int[] maxLen = new int[n];

        for (String w : words) {
            String combined = w + "#" + target;
            int[] z = zFunction(combined);
            int m = w.length();

            for (int i = 0; i < n; i++) {
                int zi = z[m + 1 + i];
                maxLen[i] = Math.max(maxLen[i], Math.min(zi, m));
            }
        }

        int ans = 0, curEnd = 0, farthest = 0;

        for (int i = 0; i < n; i++) {
            if (i > farthest) return -1;

            farthest = Math.max(farthest, i + maxLen[i]);

            if (i == curEnd) {
                if (farthest == curEnd) return -1;
                ans++;
                curEnd = farthest;
                if (curEnd >= n) return ans;
            }
        }

        return curEnd >= n ? ans : -1;
    }

    private int[] zFunction(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

## Complexity

For each word `w`, Z-algorithm runs in:

```text
O(|w| + T)
```

Across all words:

```text
O(W + (#words) * T)
```

Then greedy is `O(T)`.

Since `#words <= 100`, this is often acceptable.

---

## Pros

- Deterministic, unlike hashing
- Elegant string-matching approach
- Computes all start-position match lengths directly

## Cons

- Slightly heavier constant factor
- Still processes the full target once per word

---

# Approach 4: Naive DP Checking All Prefixes (Too Slow)

## Idea

The Version I approach is:

```text
dp[i] = minimum valid strings to form target[0..i)
```

For each `i`, try all previous `j < i` and check whether `target[j..i)` is a valid prefix.

This is correct but too slow for Version II.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minValidStrings(String[] words, String target) {
        Set<String> valid = new HashSet<>();

        for (String w : words) {
            for (int i = 1; i <= w.length(); i++) {
                valid.add(w.substring(0, i));
            }
        }

        int n = target.length();
        int INF = 1_000_000_000;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (valid.contains(target.substring(j, i))) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }

        return dp[n] >= INF ? -1 : dp[n];
    }
}
```

---

## Complexity

Potentially:

```text
O(T^3)
```

depending on substring handling.

Even with optimization, still too slow.

---

## Pros

- Very straightforward

## Cons

- Not suitable for the constraints

---

# Deep Intuition

## Why the problem becomes “minimum jumps”

Once you know the maximum valid prefix length starting from each position `i`, you no longer care which exact word supplied that prefix.

All that matters is:

```text
from i, how far can I go?
```

And because every smaller prefix is also valid, from `i` you can stop anywhere up to that farthest reach.

That is exactly why the problem turns into interval coverage / minimum jumps.

---

## Why greedy is enough after preprocessing

Suppose from positions inside the current window you can compute the farthest next reachable position.

Then the optimal strategy is to extend as far as possible before committing to another segment.

This is identical to the correctness argument of Jump Game II.

So the hard part is not the DP anymore.
The hard part is computing `L[i]` fast.

---

## Why Version II is harder than Version I

In Version I, `target.length` is small enough that `O(T^2)` or similar can survive.

In Version II:

```text
T <= 5 * 10^4
```

So we must accelerate substring-prefix matching substantially.

That is why string algorithms or hashing become much more relevant.

---

# Correctness Sketch for Approach 2

We prove the rolling-hash + greedy solution is correct.

## Step 1: Prefix membership detection

For every length `len`, we store exactly the hashes of all valid strings of length `len`, meaning all prefixes of words of that length.

For a target position `i`, a substring of length `len` starting at `i` is valid iff its hash belongs to that set.

Thus binary search correctly finds the maximum valid prefix length `L[i]`.

## Step 2: Reach interpretation

From position `i`, any prefix length from `1` to `L[i]` is valid, so we can move to any position up to:

```text
i + L[i]
```

Thus the construction problem becomes a minimum-jump problem on reach intervals.

## Step 3: Greedy correctness

The standard greedy interval-jump strategy is optimal:

- scan all positions reachable with the current number of segments
- compute the farthest next position reachable from them
- when the current frontier is exhausted, commit one more segment

This always minimizes the number of jumps/segments.

Therefore the algorithm returns the minimum number of valid strings.

---

# Example Walkthrough

## Example 1

```text
words = ["abc","aaaaa","bcdef"]
target = "aabcdabc"
```

Longest valid prefix lengths from each useful position:

- at `0`, longest valid prefix is `"aa"` → length 2
- at `2`, longest valid prefix is `"bcd"` → length 3
- at `5`, longest valid prefix is `"abc"` → length 3

So one valid optimal decomposition is:

```text
"aa" + "bcd" + "abc"
```

Answer:

```text
3
```

---

## Example 2

```text
words = ["abababab","ab"]
target = "ababaababa"
```

At position `0`, longest valid prefix can be `"ababa"`.

At position `5`, again longest valid prefix can be `"ababa"`.

So:

```text
"ababa" + "ababa"
```

Answer:

```text
2
```

---

## Example 3

```text
words = ["abcdef"]
target = "xyz"
```

At position `0`, no prefix matches, so `L[0] = 0`.

Therefore we cannot make any progress.

Answer:

```text
-1
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 911382323L;

    public int minValidStrings(String[] words, String target) {
        int n = target.length();

        @SuppressWarnings("unchecked")
        HashSet<Long>[] prefixSets = new HashSet[n + 1];
        for (int i = 0; i <= n; i++) {
            prefixSets[i] = new HashSet<>();
        }

        // Store all valid prefixes by length
        for (String w : words) {
            long h = 0;
            int limit = Math.min(w.length(), n);
            for (int i = 0; i < limit; i++) {
                h = (h * BASE + (w.charAt(i) - 'a' + 1)) % MOD;
                prefixSets[i + 1].add(h);
            }
        }

        // Rolling hash for target
        long[] pref = new long[n + 1];
        long[] pow = new long[n + 1];
        pow[0] = 1;

        for (int i = 0; i < n; i++) {
            pref[i + 1] = (pref[i] * BASE + (target.charAt(i) - 'a' + 1)) % MOD;
            pow[i + 1] = (pow[i] * BASE) % MOD;
        }

        int[] maxLen = new int[n];

        for (int i = 0; i < n; i++) {
            int lo = 0, hi = n - i;

            while (lo < hi) {
                int mid = (lo + hi + 1) >>> 1;
                long h = getHash(pref, pow, i, i + mid - 1);

                if (prefixSets[mid].contains(h)) {
                    lo = mid;
                } else {
                    hi = mid - 1;
                }
            }

            maxLen[i] = lo;
        }

        // Greedy minimum jumps
        int ans = 0, curEnd = 0, farthest = 0;

        for (int i = 0; i < n; i++) {
            if (i > farthest) return -1;

            farthest = Math.max(farthest, i + maxLen[i]);

            if (i == curEnd) {
                if (farthest == curEnd) return -1;
                ans++;
                curEnd = farthest;
                if (curEnd >= n) return ans;
            }
        }

        return curEnd >= n ? ans : -1;
    }

    private long getHash(long[] pref, long[] pow, int l, int r) {
        long val = (pref[r + 1] - pref[l] * pow[r - l + 1]) % MOD;
        if (val < 0) val += MOD;
        return val;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                            |     Time Complexity | Space Complexity | Recommended        |
| ---------- | ---------------------------------------------------- | ------------------: | ---------------: | ------------------ |
| Approach 1 | Trie + longest prefix from each index + greedy jumps | worst-case too high |           `O(W)` | Good for intuition |
| Approach 2 | Rolling hash + binary search + greedy jumps          |    `O(W + T log T)` |       `O(W + T)` | Yes                |
| Approach 3 | Z-algorithm per word + greedy jumps                  | `O(W + (#words)*T)` |           `O(T)` | Good               |
| Approach 4 | Naive DP / substring checking                        |            Too slow |            Large | No                 |

Here:

- `W = sum of word lengths`
- `T = target.length`

---

# Pattern Recognition Takeaway

This problem has a very recognizable structure:

- pieces are prefixes of dictionary words
- all shorter prefixes are also valid
- objective is minimum number of pieces
- target is large

That suggests a two-phase plan:

1. for every position, find the farthest valid reach
2. solve minimum jumps on those reaches

This is the key reduction.

---

# Final Takeaway

The cleanest way to solve Version II is:

1. preprocess all valid prefixes efficiently, preferably with hashing or a strong string-matching method
2. compute the maximum usable prefix length from every target position
3. convert the problem into a minimum-jumps / interval coverage problem
4. solve that greedily

That gives an efficient solution for the larger constraints.
