# 1316. Distinct Echo Substrings — Java Solutions and Detailed Notes

## Problem

Given a string `text`, return the number of **distinct non-empty substrings** that can be written as:

```text
a + a
```

for some string `a`.

Such a substring is called an **echo substring**.

---

## Examples

### Example 1

```text
text = "abcabcabc"
```

Echo substrings:

- `"abcabc"` = `"abc" + "abc"`
- `"bcabca"` = `"bca" + "bca"`
- `"cabcab"` = `"cab" + "cab"`

Answer:

```text
3
```

---

### Example 2

```text
text = "leetcodeleetcode"
```

Echo substrings:

- `"ee"` = `"e" + "e"`
- `"leetcodeleetcode"` = `"leetcode" + "leetcode"`

Answer:

```text
2
```

---

# Core observation

A substring is an echo substring iff:

- its length is even,
- and its first half equals its second half.

So if a substring starts at `i` and has half-length `len`, then we need:

```text
text[i .. i + len - 1] == text[i + len .. i + 2*len - 1]
```

The challenge is to count **distinct** such substrings efficiently.

Since:

```text
n <= 2000
```

we do not need an ultra-advanced suffix automaton or suffix array solution, but we should still avoid cubic work if possible.

---

# Approach 1: Brute Force with substring comparison and HashSet

## Idea

Enumerate every possible starting index `i` and every possible half-length `len`.

Then:

- compare the first half and second half directly,
- if equal, add the whole substring to a `HashSet<String>` so duplicates are counted once.

---

## Java code

```java
import java.util.*;

class Solution {
    public int distinctEchoSubstrings(String text) {
        int n = text.length();
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int len = 1; i + 2 * len <= n; len++) {
                String left = text.substring(i, i + len);
                String right = text.substring(i + len, i + 2 * len);

                if (left.equals(right)) {
                    seen.add(text.substring(i, i + 2 * len));
                }
            }
        }

        return seen.size();
    }
}
```

---

## Complexity

There are `O(n^2)` candidate pairs `(i, len)`.

Each substring comparison may cost `O(len)`, and substring construction also costs up to `O(n)` in Java.

Worst-case time complexity:

```text
O(n^3)
```

Space complexity:

```text
O(n^2)
```

for storing distinct substrings in the set.

This is too slow in the worst case, though it may pass smaller tests.

---

# Approach 2: Dynamic Programming for longest common prefix of suffixes

## Idea

We want to quickly test whether:

```text
text[i .. i+len-1] == text[i+len .. i+2*len-1]
```

A standard trick is to precompute:

```text
lcp[i][j] = length of the longest common prefix of suffixes starting at i and j
```

Then the above condition is equivalent to:

```text
lcp[i][i+len] >= len
```

Once we can test equality in O(1), we can enumerate all `(i, len)` pairs and add valid full substrings to a set.

---

## DP recurrence

Fill `lcp` from the end:

```text
if text[i] == text[j]:
    lcp[i][j] = 1 + lcp[i+1][j+1]
else:
    lcp[i][j] = 0
```

Because `lcp[i][j]` depends on `lcp[i+1][j+1]`, we fill from bottom-right toward top-left.

---

## Java code

```java
import java.util.*;

class Solution {
    public int distinctEchoSubstrings(String text) {
        int n = text.length();
        int[][] lcp = new int[n + 1][n + 1];

        for (int i = n - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (text.charAt(i) == text.charAt(j)) {
                    lcp[i][j] = 1 + lcp[i + 1][j + 1];
                }
            }
        }

        Set<String> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int len = 1; i + 2 * len <= n; len++) {
                if (lcp[i][i + len] >= len) {
                    seen.add(text.substring(i, i + 2 * len));
                }
            }
        }

        return seen.size();
    }
}
```

---

## Complexity

Building the `lcp` table:

```text
O(n^2)
```

Enumerating all `(i, len)` pairs:

```text
O(n^2)
```

Each valid substring insertion into `HashSet<String>` requires substring creation, which costs `O(length)` in Java.

So worst-case time can still drift toward:

```text
O(n^3)
```

though much faster in practice than pure brute force.

Space complexity:

```text
O(n^2)
```

for the LCP table.

---

# Approach 3: Rolling Hash + HashSet of hashes (recommended)

## Idea

Use polynomial rolling hash so we can compare two halves in O(1).

For each candidate `(i, len)`:

- compute hash of first half,
- compute hash of second half,
- if equal, then the substring is an echo substring,
- add the **hash of the full substring** to a set to count distinct values.

This avoids constructing actual substring objects repeatedly.

To reduce collision risk, use **double hashing**.

---

## Rolling hash setup

For a base `B` and modulus `M`:

```text
prefix[i+1] = prefix[i] * B + value(text[i])
```

Then substring hash of `[l..r]` is computed in O(1).

We do this with two moduli.

---

## Why storing full-substring hash works

We need distinct echo substrings, not just distinct `(start, len)` pairs.

So when a valid echo substring is found, we insert the hash of the **whole substring**:

```text
text[i .. i + 2*len - 1]
```

into a set.

Different positions producing the same substring will then collapse into the same stored hash.

---

## Java code

```java
import java.util.*;

class Solution {
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE = 911382323L;

    public int distinctEchoSubstrings(String text) {
        int n = text.length();

        long[] pow1 = new long[n + 1];
        long[] pow2 = new long[n + 1];
        long[] pref1 = new long[n + 1];
        long[] pref2 = new long[n + 1];

        pow1[0] = 1;
        pow2[0] = 1;

        for (int i = 0; i < n; i++) {
            int val = text.charAt(i) - 'a' + 1;
            pow1[i + 1] = (pow1[i] * BASE) % MOD1;
            pow2[i + 1] = (pow2[i] * BASE) % MOD2;

            pref1[i + 1] = (pref1[i] * BASE + val) % MOD1;
            pref2[i + 1] = (pref2[i] * BASE + val) % MOD2;
        }

        Set<Long> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int len = 1; i + 2 * len <= n; len++) {
                long left1 = getHash(pref1, pow1, MOD1, i, i + len - 1);
                long right1 = getHash(pref1, pow1, MOD1, i + len, i + 2 * len - 1);

                long left2 = getHash(pref2, pow2, MOD2, i, i + len - 1);
                long right2 = getHash(pref2, pow2, MOD2, i + len, i + 2 * len - 1);

                if (left1 == right1 && left2 == right2) {
                    long whole1 = getHash(pref1, pow1, MOD1, i, i + 2 * len - 1);
                    long whole2 = getHash(pref2, pow2, MOD2, i, i + 2 * len - 1);

                    long combined = (whole1 << 32) ^ whole2;
                    seen.add(combined);
                }
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

There are `O(n^2)` candidates `(i, len)`.

Each comparison is `O(1)`.

So time complexity becomes:

```text
O(n^2)
```

Space complexity:

```text
O(n^2)
```

in the worst case for the set of distinct substrings, plus `O(n)` for hash arrays.

This is the best practical solution for `n <= 2000`.

---

# Approach 4: Sliding-window / LCP hybrid using DP + hashing (educational refinement)

## Idea

We can mix the LCP observation and hashing:

- use LCP-style thinking for equality condition,
- use hashing to represent distinct substrings compactly.

That means:

- equality check can be via either DP LCP or rolling hash,
- distinctness is tracked by hash instead of actual substring objects.

This is mostly a conceptual variation of Approach 3.

---

## Java code

```java
import java.util.*;

class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 911382323L;

    public int distinctEchoSubstrings(String text) {
        int n = text.length();

        long[] pow = new long[n + 1];
        long[] pref = new long[n + 1];
        pow[0] = 1;

        for (int i = 0; i < n; i++) {
            int val = text.charAt(i) - 'a' + 1;
            pow[i + 1] = (pow[i] * BASE) % MOD;
            pref[i + 1] = (pref[i] * BASE + val) % MOD;
        }

        Set<Long> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int len = 1; i + 2 * len <= n; len++) {
                long h1 = getHash(pref, pow, i, i + len - 1);
                long h2 = getHash(pref, pow, i + len, i + 2 * len - 1);

                if (h1 == h2) {
                    long whole = getHash(pref, pow, i, i + 2 * len - 1);
                    seen.add(whole);
                }
            }
        }

        return seen.size();
    }

    private long getHash(long[] pref, long[] pow, int l, int r) {
        long ans = (pref[r + 1] - pref[l] * pow[r - l + 1]) % MOD;
        if (ans < 0) ans += MOD;
        return ans;
    }
}
```

---

## Complexity

Same as Approach 3:

```text
O(n^2)
```

time and up to `O(n^2)` distinct stored hashes.

This version is shorter but has a higher collision risk than double hashing.

---

# Why O(n^2) is enough

`n <= 2000`, so:

```text
n^2 = 4,000,000
```

which is entirely reasonable.

This is why a quadratic algorithm with efficient constant-time substring equality is the sweet spot here.

No need for suffix automata or suffix arrays.

---

# Comparison of approaches

## Approach 1: Brute force with substring comparison

### Pros

- easiest to understand

### Cons

- too slow in worst case
- repeated substring creation and comparison

### Complexity

```text
O(n^3)
```

---

## Approach 2: DP LCP + substring set

### Pros

- clever equality checking
- easier to reason about than hashing

### Cons

- still creates substring objects for distinctness
- larger memory due to `O(n^2)` DP table

### Complexity

```text
O(n^2)` DP + potentially heavy substring work
```

---

## Approach 3: Double rolling hash (Recommended)

### Pros

- fast equality checks
- no repeated substring object construction
- practical and efficient

### Cons

- more implementation detail
- tiny theoretical collision probability

### Complexity

```text
O(n^2)
```

---

## Approach 4: Single rolling hash

### Pros

- shortest efficient solution

### Cons

- higher collision risk than double hash

### Complexity

```text
O(n^2)
```

---

# Final recommended Java solution

The best balance of speed and safety is the **double rolling hash** approach.

```java
import java.util.*;

class Solution {
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE = 911382323L;

    public int distinctEchoSubstrings(String text) {
        int n = text.length();

        long[] pow1 = new long[n + 1];
        long[] pow2 = new long[n + 1];
        long[] pref1 = new long[n + 1];
        long[] pref2 = new long[n + 1];

        pow1[0] = 1;
        pow2[0] = 1;

        for (int i = 0; i < n; i++) {
            int val = text.charAt(i) - 'a' + 1;
            pow1[i + 1] = (pow1[i] * BASE) % MOD1;
            pow2[i + 1] = (pow2[i] * BASE) % MOD2;
            pref1[i + 1] = (pref1[i] * BASE + val) % MOD1;
            pref2[i + 1] = (pref2[i] * BASE + val) % MOD2;
        }

        Set<Long> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int len = 1; i + 2 * len <= n; len++) {
                long left1 = getHash(pref1, pow1, MOD1, i, i + len - 1);
                long right1 = getHash(pref1, pow1, MOD1, i + len, i + 2 * len - 1);

                if (left1 != right1) continue;

                long left2 = getHash(pref2, pow2, MOD2, i, i + len - 1);
                long right2 = getHash(pref2, pow2, MOD2, i + len, i + 2 * len - 1);

                if (left2 == right2) {
                    long whole1 = getHash(pref1, pow1, MOD1, i, i + 2 * len - 1);
                    long whole2 = getHash(pref2, pow2, MOD2, i, i + 2 * len - 1);
                    long combined = (whole1 << 32) ^ whole2;
                    seen.add(combined);
                }
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

# Walkthrough for Example 1

```text
text = "abcabcabc"
```

Check substrings with even lengths.

For start `0`, half-length `3`:

- first half = `"abc"`
- second half = `"abc"`

So `"abcabc"` is valid.

For start `1`, half-length `3`:

- first half = `"bca"`
- second half = `"bca"`

So `"bcabca"` is valid.

For start `2`, half-length `3`:

- first half = `"cab"`
- second half = `"cab"`

So `"cabcab"` is valid.

No other distinct echo substrings exist.

Answer = `3`.

---

# Walkthrough for Example 2

```text
text = "leetcodeleetcode"
```

Valid cases include:

- `"ee"` = `"e" + "e"`
- `"leetcodeleetcode"` = `"leetcode" + "leetcode"`

So answer = `2`.

---

# Takeaway pattern

This problem is a classic example of:

- checking equality of many substring pairs,
- avoiding repeated substring construction,
- using rolling hash or LCP-style preprocessing.

Whenever you see:

```text
many substring equality checks over O(n^2) candidates
```

you should strongly consider:

- rolling hash,
- suffix/LCP preprocessing,
- or Z/KMP-inspired structure.

For this problem size, rolling hash is the cleanest and most practical tool.
