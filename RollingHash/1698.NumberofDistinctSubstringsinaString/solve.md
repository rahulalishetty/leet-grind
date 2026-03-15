# 1698. Number of Distinct Substrings in a String — Java Solutions and Detailed Notes

## Problem

Given a string `s`, return the number of **distinct substrings** of `s`.

A substring is any contiguous part of the string.

---

## Examples

### Example 1

```text
Input:  s = "aabbaba"
Output: 21
```

Distinct substrings include:

```text
"a","b","aa","bb","ab","ba",
"aab","abb","bab","bba","aba",
"aabb","abba","bbab","baba",
"aabba","abbab","bbaba",
"aabbab","abbaba","aabbaba"
```

---

### Example 2

```text
Input:  s = "abcdefg"
Output: 28
```

Because every substring is unique in a string with all distinct characters, the answer is:

```text
7 + 6 + 5 + 4 + 3 + 2 + 1 = 28
```

---

## Constraints

```text
1 <= s.length <= 500
s consists of lowercase English letters.
```

---

## Follow-up

Can this be solved in `O(n)`?

Yes — with a **suffix automaton**.

---

# Core insight

A string of length `n` has:

```text
n * (n + 1) / 2
```

total substrings if we count duplicates.

The challenge is to count only **distinct** ones.

There are several classic ways to do this:

1. Enumerate all substrings and store them in a set.
2. Use rolling hash to represent substrings more efficiently.
3. Build a trie of all suffixes.
4. Use suffix array + LCP.
5. Use suffix automaton.

Because `n <= 500`, even quadratic and cubic-ish ideas may pass. But the follow-up points toward the suffix automaton.

---

# Approach 1: Brute force with HashSet of substrings

## Idea

Generate every substring:

- choose start index `i`
- choose end index `j`
- insert `s.substring(i, j + 1)` into a `HashSet`

At the end, the set size is the number of distinct substrings.

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int countDistinct(String s) {
        int n = s.length();
        Set<String> set = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                set.add(s.substring(i, j + 1));
            }
        }

        return set.size();
    }
}
```

---

## Complexity

There are `O(n^2)` substrings.

Creating each substring in Java costs `O(length)`.

So worst-case time complexity is about:

```text
O(n^3)
```

Space complexity is also large because all distinct substrings are stored explicitly.

For `n <= 500`, this can still work.

---

# Approach 2: Rolling hash + HashSet of hashes

## Idea

Instead of storing substring objects, represent each substring by a rolling hash.

Then:

- precompute prefix hashes and powers,
- compute hash of any substring in `O(1)`,
- insert that hash into a set.

To reduce collision risk, use **double hashing**.

This avoids repeatedly constructing substring objects.

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE = 911382323L;

    public int countDistinct(String s) {
        int n = s.length();

        long[] pow1 = new long[n + 1];
        long[] pow2 = new long[n + 1];
        long[] pref1 = new long[n + 1];
        long[] pref2 = new long[n + 1];

        pow1[0] = 1;
        pow2[0] = 1;

        for (int i = 0; i < n; i++) {
            int val = s.charAt(i) - 'a' + 1;
            pow1[i + 1] = (pow1[i] * BASE) % MOD1;
            pow2[i + 1] = (pow2[i] * BASE) % MOD2;
            pref1[i + 1] = (pref1[i] * BASE + val) % MOD1;
            pref2[i + 1] = (pref2[i] * BASE + val) % MOD2;
        }

        Set<Long> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                long h1 = getHash(pref1, pow1, MOD1, i, j);
                long h2 = getHash(pref2, pow2, MOD2, i, j);
                long combined = (h1 << 32) ^ h2;
                seen.add(combined);
            }
        }

        return seen.size();
    }

    private long getHash(long[] pref, long[] pow, long mod, int l, int r) {
        long ans = (pref[r + 1] - pref[l] * pow[r - l + 1]) % mod;
        if (ans < 0) ans += mod;
        return ans;
    }
}
```

---

## Complexity

There are `O(n^2)` substrings, each hash is computed in `O(1)`.

Time complexity:

```text
O(n^2)
```

Space complexity:

```text
O(n^2)
```

This is already much better than Approach 1.

---

# Approach 3: Trie of all suffixes

## Idea

Insert every suffix of the string into a trie.

Each time we create a new trie node while inserting suffixes, that corresponds to one new distinct substring.

Why?

Because every path from the root corresponds to a substring, and a new node means a new unique prefix of some suffix, hence a new distinct substring.

---

## Example intuition

For:

```text
s = "aba"
```

suffixes are:

```text
"aba"
"ba"
"a"
```

Insert them into a trie. The number of non-root nodes equals the number of distinct substrings.

---

## Java code

```java
class Solution {
    static class TrieNode {
        TrieNode[] next = new TrieNode[26];
    }

    public int countDistinct(String s) {
        TrieNode root = new TrieNode();
        int ans = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            TrieNode curr = root;
            for (int j = i; j < n; j++) {
                int idx = s.charAt(j) - 'a';
                if (curr.next[idx] == null) {
                    curr.next[idx] = new TrieNode();
                    ans++;
                }
                curr = curr.next[idx];
            }
        }

        return ans;
    }
}
```

---

## Complexity

There are `O(n^2)` character insertions in total.

Time complexity:

```text
O(n^2)
```

Space complexity:

```text
O(n^2)
```

This is a very elegant exact solution and easy to reason about.

---

# Approach 4: Suffix Array + LCP

## Idea

All distinct substrings can be counted by:

```text
total substrings - duplicated contribution
```

Total substrings:

```text
n * (n + 1) / 2
```

Now sort all suffixes.

For each suffix, the number of **new** substrings contributed is:

```text
suffix length - LCP with previous suffix
```

So if suffixes are sorted, the answer is:

```text
sum over all suffixes of (suffixLength - previousLCP)
```

---

## Why this works

Every substring is a prefix of some suffix.

When suffixes are sorted, common prefixes between neighboring suffixes capture overlap.

So subtracting the LCP removes duplicates already counted before.

---

## Java code

```java
import java.util.Arrays;

class Solution {
    public int countDistinct(String s) {
        int n = s.length();
        String[] suffixes = new String[n];

        for (int i = 0; i < n; i++) {
            suffixes[i] = s.substring(i);
        }

        Arrays.sort(suffixes);

        int ans = suffixes[0].length();

        for (int i = 1; i < n; i++) {
            int lcp = longestCommonPrefix(suffixes[i - 1], suffixes[i]);
            ans += suffixes[i].length() - lcp;
        }

        return ans;
    }

    private int longestCommonPrefix(String a, String b) {
        int len = Math.min(a.length(), b.length());
        int i = 0;
        while (i < len && a.charAt(i) == b.charAt(i)) {
            i++;
        }
        return i;
    }
}
```

---

## Complexity

Sorting suffix strings directly is not very efficient.

Worst-case time can be about:

```text
O(n^2 log n)
```

or worse depending on string comparisons.

Still fine for `n <= 500`.

This is conceptually important because it leads to the linear-time suffix-array/LCP theory.

---

# Approach 5: Suffix Automaton (Follow-up, O(n))

This is the strongest and most elegant solution asymptotically.

## Core theorem

For a suffix automaton, the number of distinct substrings equals:

```text
sum over all states of (len[state] - len[link[state]])
```

excluding the initial state.

Where:

- `len[state]` = maximum length of a string represented by the state
- `link[state]` = suffix link of the state

---

## Why this formula works

Each state represents an equivalence class of substrings with lengths in the interval:

```text
(len[link[state]] + 1) ... len[state]
```

So this state contributes exactly:

```text
len[state] - len[link[state]]
```

new distinct substrings.

Summing over all states gives the total number of distinct substrings.

---

## Suffix automaton construction idea

We extend the automaton character by character.

Each extension either:

- creates a normal new state,
- or creates a clone state when branching structure requires it.

This is standard suffix automaton construction.

---

## Java code

```java
class Solution {
    static class State {
        int[] next = new int[26];
        int link = -1;
        int len = 0;

        State() {
            for (int i = 0; i < 26; i++) next[i] = -1;
        }
    }

    public int countDistinct(String s) {
        State[] st = new State[2 * s.length()];
        for (int i = 0; i < st.length; i++) st[i] = new State();

        int size = 1;
        int last = 0;

        for (char ch : s.toCharArray()) {
            int c = ch - 'a';
            int cur = size++;
            st[cur].len = st[last].len + 1;

            int p = last;
            while (p != -1 && st[p].next[c] == -1) {
                st[p].next[c] = cur;
                p = st[p].link;
            }

            if (p == -1) {
                st[cur].link = 0;
            } else {
                int q = st[p].next[c];
                if (st[p].len + 1 == st[q].len) {
                    st[cur].link = q;
                } else {
                    int clone = size++;
                    st[clone].len = st[p].len + 1;
                    st[clone].link = st[q].link;
                    System.arraycopy(st[q].next, 0, st[clone].next, 0, 26);

                    while (p != -1 && st[p].next[c] == q) {
                        st[p].next[c] = clone;
                        p = st[p].link;
                    }

                    st[q].link = clone;
                    st[cur].link = clone;
                }
            }

            last = cur;
        }

        long ans = 0;
        for (int v = 1; v < size; v++) {
            ans += st[v].len - st[st[v].link].len;
        }

        return (int) ans;
    }
}
```

---

## Complexity

Suffix automaton construction is linear.

Time complexity:

```text
O(n)
```

Space complexity:

```text
O(n)
```

This solves the follow-up optimally.

---

# Comparison of approaches

## Approach 1: HashSet of substrings

### Pros

- simplest
- very direct

### Cons

- cubic-ish in Java because substring creation is costly

### Complexity

```text
Time:  O(n^3)
Space: O(n^2)
```

---

## Approach 2: Rolling hash

### Pros

- much faster
- easy to code if familiar with hashing

### Cons

- hash collision risk unless double hashing is used

### Complexity

```text
Time:  O(n^2)
Space: O(n^2)
```

---

## Approach 3: Trie of suffixes

### Pros

- elegant and exact
- very intuitive for “distinct substrings”

### Cons

- quadratic memory in worst case

### Complexity

```text
Time:  O(n^2)
Space: O(n^2)
```

---

## Approach 4: Suffix array + LCP

### Pros

- classic theory
- strong conceptual method

### Cons

- direct Java suffix sorting is not optimal
- more complex than trie for this input size

### Complexity

```text
Time:  roughly O(n^2 log n)
Space: O(n^2)
```

---

## Approach 5: Suffix automaton

### Pros

- optimal
- solves the follow-up
- elegant once understood

### Cons

- most advanced implementation

### Complexity

```text
Time:  O(n)
Space: O(n)
```

---

# Final recommended solution

For the given constraint `n <= 500`, the **trie of suffixes** or **rolling hash** solution is excellent.

For the follow-up asking for `O(n)`, the correct answer is **suffix automaton**.

---

# Best practical solution for the original constraints: Trie of all suffixes

```java
class Solution {
    static class TrieNode {
        TrieNode[] next = new TrieNode[26];
    }

    public int countDistinct(String s) {
        TrieNode root = new TrieNode();
        int ans = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            TrieNode curr = root;
            for (int j = i; j < n; j++) {
                int idx = s.charAt(j) - 'a';
                if (curr.next[idx] == null) {
                    curr.next[idx] = new TrieNode();
                    ans++;
                }
                curr = curr.next[idx];
            }
        }

        return ans;
    }
}
```

This is exact, clean, and easy to explain.

---

# Best asymptotic solution: Suffix Automaton

```java
class Solution {
    static class State {
        int[] next = new int[26];
        int link = -1;
        int len = 0;

        State() {
            for (int i = 0; i < 26; i++) next[i] = -1;
        }
    }

    public int countDistinct(String s) {
        State[] st = new State[2 * s.length()];
        for (int i = 0; i < st.length; i++) st[i] = new State();

        int size = 1, last = 0;

        for (char ch : s.toCharArray()) {
            int c = ch - 'a';
            int cur = size++;
            st[cur].len = st[last].len + 1;

            int p = last;
            while (p != -1 && st[p].next[c] == -1) {
                st[p].next[c] = cur;
                p = st[p].link;
            }

            if (p == -1) {
                st[cur].link = 0;
            } else {
                int q = st[p].next[c];
                if (st[p].len + 1 == st[q].len) {
                    st[cur].link = q;
                } else {
                    int clone = size++;
                    st[clone].len = st[p].len + 1;
                    st[clone].link = st[q].link;
                    System.arraycopy(st[q].next, 0, st[clone].next, 0, 26);

                    while (p != -1 && st[p].next[c] == q) {
                        st[p].next[c] = clone;
                        p = st[p].link;
                    }

                    st[q].link = clone;
                    st[cur].link = clone;
                }
            }
            last = cur;
        }

        long ans = 0;
        for (int i = 1; i < size; i++) {
            ans += st[i].len - st[st[i].link].len;
        }
        return (int) ans;
    }
}
```

---

# Takeaway pattern

This problem is a classic “count distinct substrings” question.

The key tools to remember are:

- **Trie of suffixes** for a simple exact quadratic solution,
- **Suffix array + LCP** for a classic sorting-based approach,
- **Suffix automaton** for the optimal linear-time solution.

If the interviewer asks for the follow-up, the expected answer is almost certainly:

```text
Suffix Automaton
```
