# 1554. Strings Differ by One Character — Java Solutions and Detailed Notes

## Problem

We are given an array of strings `dict`.

All strings:

- have the same length,
- are unique,
- contain only lowercase English letters.

We must return `true` if there exist **two strings that differ by exactly one character at the same index**.

Otherwise return `false`.

---

## What does “differ by one character in the same index” mean?

Suppose we have:

```text
"abcd"
"aacd"
```

Compare character by character:

- index 0: `a == a`
- index 1: `b != a`
- index 2: `c == c`
- index 3: `d == d`

They differ at exactly one index, so the answer is `true`.

---

## Examples

### Example 1

```text
dict = ["abcd", "acbd", "aacd"]
```

Compare:

```text
"abcd"
"aacd"
```

They differ only at index `1`, so the answer is:

```text
true
```

---

### Example 2

```text
dict = ["ab", "cd", "yz"]
```

No pair differs by exactly one character at the same index.

Answer:

```text
false
```

---

### Example 3

```text
dict = ["abcd", "cccc", "abyd", "abab"]
```

There exists a valid pair, so the answer is:

```text
true
```

---

# Key observation

Two strings differ by exactly one character iff:

- they have the same length,
- and if we remove the same index from both strings, the remaining parts become identical.

Example:

```text
"abcd"
"aacd"
```

If we remove index `1` from both:

```text
"acd"
"acd"
```

They match.

This observation leads to the main efficient solutions.

---

# Approach 1: Brute Force Pair Comparison

## Idea

Check every pair of strings.

For each pair:

- compare characters one by one,
- count how many positions differ,
- if the count is exactly `1`, return `true`.

If no such pair exists, return `false`.

---

## Java code

```java
class Solution {
    public boolean differByOne(String[] dict) {
        int n = dict.length;
        int m = dict[0].length();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int diff = 0;
                for (int k = 0; k < m; k++) {
                    if (dict[i].charAt(k) != dict[j].charAt(k)) {
                        diff++;
                        if (diff > 1) {
                            break;
                        }
                    }
                }
                if (diff == 1) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity

Let:

- `n = number of strings`
- `m = length of each string`

Time complexity:

```text
O(n^2 * m)
```

Space complexity:

```text
O(1)
```

This is too slow when the total input size is large.

---

# Approach 2: Replace each index with wildcard and use a HashSet

## Idea

For each string, and for each index `j`:

- replace `dict[i][j]` with a wildcard like `'*'`,
- if the resulting masked pattern has been seen before, then two strings differ at exactly that index.

Example:

```text
"abcd" -> "*bcd", "a*cd", "ab*d", "abc*"
"aacd" -> "*acd", "a*cd", "aa*d", "aac*"
```

Here both strings generate:

```text
"a*cd"
```

So they differ only at index `1`.

---

## Why this works

If two strings become identical after removing or masking the same index, then they differ only at that index.

Since all original strings are unique, they cannot be identical at all positions, so matching masked forms implies exactly one differing position.

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean differByOne(String[] dict) {
        int m = dict[0].length();
        Set<String> seen = new HashSet<>();

        for (String word : dict) {
            char[] chars = word.toCharArray();

            for (int i = 0; i < m; i++) {
                char old = chars[i];
                chars[i] = '*';
                String pattern = new String(chars);

                if (!seen.add(pattern)) {
                    return true;
                }

                chars[i] = old;
            }
        }

        return false;
    }
}
```

---

## Complexity

For every string, we generate `m` patterns, and each pattern construction costs `O(m)`.

Time complexity:

```text
O(n * m^2)
```

Space complexity:

```text
O(n * m)
```

This is often much better than brute force, but we can still do better.

---

# Approach 3: Rolling Hash / Rabin-Karp style hashing (Recommended)

## Idea

Instead of building masked strings explicitly, compute a hash for each string.

Then, for each index `j`, compute the hash of the string with character `j` removed **implicitly**.

If two strings produce the same reduced hash at the same removed index, then they differ by exactly one character at that index.

This avoids creating `O(m)`-length masked strings repeatedly.

---

## Hash formulation

Suppose a string is:

```text
s[0], s[1], ..., s[m-1]
```

We compute a polynomial rolling hash:

```text
hash(s) = s[0] * base^(m-1) + s[1] * base^(m-2) + ... + s[m-1]
```

Now for index `j`, we want the hash of the string with `s[j]` removed.

We can combine:

- hash of prefix before `j`
- hash of suffix after `j`

into one length-`m-1` hash.

If the reduced hash has been seen before for the same removal position, return `true`.

---

## Why this works

If two strings become identical after removing the same index, they differ only at that index.

The rolling hash lets us test this efficiently.

To be fully robust against collisions, we can use double hashing.
For this problem, a single good hash is often accepted, but double hash is safer.

---

## Java code (single rolling hash version)

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean differByOne(String[] dict) {
        int n = dict.length;
        int m = dict[0].length();

        long base = 27L;
        long mod = 1_000_000_007L;

        long[] pow = new long[m + 1];
        pow[0] = 1;
        for (int i = 1; i <= m; i++) {
            pow[i] = (pow[i - 1] * base) % mod;
        }

        long[] fullHash = new long[n];
        for (int i = 0; i < n; i++) {
            long h = 0;
            for (int j = 0; j < m; j++) {
                int val = dict[i].charAt(j) - 'a' + 1;
                h = (h * base + val) % mod;
            }
            fullHash[i] = h;
        }

        for (int j = 0; j < m; j++) {
            Set<Long> seen = new HashSet<>();

            for (int i = 0; i < n; i++) {
                long h = 0;
                for (int k = 0; k < m; k++) {
                    if (k == j) continue;
                    int val = dict[i].charAt(k) - 'a' + 1;
                    h = (h * base + val) % mod;
                }

                if (!seen.add(h)) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity

For each removed index `j`, we recompute reduced hashes from scratch.

Time complexity:

```text
O(n * m^2)
```

This is still not ideal, though it avoids creating strings.

We can optimize further by using prefix and suffix hashes.

---

# Approach 4: Optimized rolling hash with prefix/suffix precomputation

## Idea

For each string, precompute:

- prefix hashes,
- powers of base.

Then the hash of “string with index `j` removed” can be computed in O(1).

This gives an `O(n * m)` solution.

---

## How to compute removed-character hash in O(1)

Let:

```text
prefixHash[i] = hash of substring [0..i-1]
```

Then for removing index `j`:

- left part is `s[0..j-1]`
- right part is `s[j+1..m-1]`

We combine them as:

```text
hashRemoved = hash(left) * base^(length(right)) + hash(right)
```

This effectively forms the string without the removed character.

For every index `j`, store reduced hashes in a set.

If a duplicate appears, return `true`.

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean differByOne(String[] dict) {
        int n = dict.length;
        int m = dict[0].length();

        long base = 27L;
        long mod = 1_000_000_007L;

        long[] pow = new long[m + 1];
        pow[0] = 1;
        for (int i = 1; i <= m; i++) {
            pow[i] = (pow[i - 1] * base) % mod;
        }

        long[][] prefix = new long[n][m + 1];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int val = dict[i].charAt(j) - 'a' + 1;
                prefix[i][j + 1] = (prefix[i][j] * base + val) % mod;
            }
        }

        for (int j = 0; j < m; j++) {
            Set<Long> seen = new HashSet<>();

            for (int i = 0; i < n; i++) {
                long leftHash = prefix[i][j];

                long rightHash = getSubHash(prefix[i], pow, mod, j + 1, m);

                int rightLen = m - (j + 1);
                long combined = (leftHash * pow[rightLen] + rightHash) % mod;

                if (!seen.add(combined)) {
                    return true;
                }
            }
        }

        return false;
    }

    private long getSubHash(long[] prefix, long[] pow, long mod, int l, int r) {
        long val = (prefix[r] - prefix[l] * pow[r - l]) % mod;
        if (val < 0) val += mod;
        return val;
    }
}
```

---

## Complexity

Let `n = number of strings`, `m = length of each string`.

Building prefix hashes:

```text
O(n * m)
```

For each of the `m` positions, process all `n` strings in O(1) each:

```text
O(n * m)
```

So total time complexity:

```text
O(n * m)
```

Space complexity:

```text
O(n * m)
```

This is the best practical solution.

---

# Approach 5: Trie-based thinking (not practical here)

## Idea

One may think of using a trie and comparing strings position by position.

But the condition is very specific:

> differ by exactly one character at the same index

A trie does not directly help enough here, because we would need to allow one mismatch at a fixed position while matching the rest exactly.

Compared to hashing or wildcard masking, trie-based solutions are more complicated and not better here.

So this is not recommended.

---

# Why wildcard masking works

Suppose two strings differ by exactly one character at index `j`.

If we replace that index with `*`, both strings become the same pattern.

Example:

```text
"abcd" -> "a*cd"
"aacd" -> "a*cd"
```

Conversely, because all original strings are unique, if two strings generate the same masked pattern at the same index, then they must differ at exactly that position.

So the wildcard-mask approach is conceptually simple and correct.

---

# Comparison of approaches

## Approach 1: Brute force pair comparison

### Pros

- easiest to understand

### Cons

- too slow

### Complexity

```text
O(n^2 * m)
```

---

## Approach 2: Wildcard masking with HashSet

### Pros

- simple and elegant
- easy to implement

### Cons

- pattern creation costs O(m)
- total O(n \* m^2)

### Complexity

```text
O(n * m^2)
```

---

## Approach 3: Rolling hash without prefix optimization

### Pros

- avoids building strings

### Cons

- still recomputes reduced hash too often

### Complexity

```text
O(n * m^2)
```

---

## Approach 4: Prefix/suffix rolling hash (Recommended)

### Pros

- optimal practical complexity
- no substring construction
- scalable

### Cons

- more implementation detail

### Complexity

```text
O(n * m)
```

---

# Final recommended Java solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean differByOne(String[] dict) {
        int n = dict.length;
        int m = dict[0].length();

        long base = 27L;
        long mod = 1_000_000_007L;

        long[] pow = new long[m + 1];
        pow[0] = 1;
        for (int i = 1; i <= m; i++) {
            pow[i] = (pow[i - 1] * base) % mod;
        }

        long[][] prefix = new long[n][m + 1];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int val = dict[i].charAt(j) - 'a' + 1;
                prefix[i][j + 1] = (prefix[i][j] * base + val) % mod;
            }
        }

        for (int removeIdx = 0; removeIdx < m; removeIdx++) {
            Set<Long> seen = new HashSet<>();

            for (int i = 0; i < n; i++) {
                long leftHash = prefix[i][removeIdx];
                long rightHash = getSubHash(prefix[i], pow, mod, removeIdx + 1, m);
                int rightLen = m - removeIdx - 1;

                long combined = (leftHash * pow[rightLen] + rightHash) % mod;

                if (!seen.add(combined)) {
                    return true;
                }
            }
        }

        return false;
    }

    private long getSubHash(long[] prefix, long[] pow, long mod, int l, int r) {
        long ans = (prefix[r] - prefix[l] * pow[r - l]) % mod;
        if (ans < 0) ans += mod;
        return ans;
    }
}
```

---

# Worked example

## Example 1

```text
dict = ["abcd", "acbd", "aacd"]
```

Try removing index `1`:

- `"abcd"` -> `"acd"`
- `"acbd"` -> `"abd"`
- `"aacd"` -> `"acd"`

Now `"abcd"` and `"aacd"` produce the same reduced string after removing index `1`.

So they differ only at index `1`.

Answer:

```text
true
```

---

# Takeaway pattern

This problem is a good example of a general trick:

> If two strings must differ at exactly one position, remove that position and compare what remains.

That leads naturally to:

- wildcard masking,
- or even better,
- hashing of “string with one index removed”.

For large input, rolling hash with prefix/suffix preprocessing is the strongest practical solution.
