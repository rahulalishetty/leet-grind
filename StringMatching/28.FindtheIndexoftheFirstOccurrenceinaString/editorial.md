# 28. Find the Index of the First Occurrence in a String

## Overview

The problem is a standard **String Matching Problem**.

It can be stated as:

> Find one or more occurrences of a pattern
> `x₀ x₁ … xₘ₋₁`
> in a text
> `y₀ y₁ … yₙ₋₁`.

In this problem, we have to find the **first occurrence** of `needle` in `haystack`.

The characters are taken from the set of lowercase English letters:

```text
{a, b, c, ..., y, z}
```

String matching has many applications, including:

- Spell Checker
- Plagiarism Detection
- Text Editors
- Spam Filters
- Digital Forensics
- Matching DNA Sequences
- Intrusion Detection
- Search Engines
- Bioinformatics and Cheminformatics
- Information Retrieval Systems
- Language Syntax Checkers

Throughout the article:

- `m` denotes the length of `needle`
- `n` denotes the length of `haystack`

---

# Approach 1: Sliding Window

## Intuition

The most naïve approach is to examine every substring of length `m` in `haystack` and check if it equals `needle`.

The first substring starts at index `0` and ends at index `m - 1`.

The second substring starts at index `1` and ends at index `m`.

The last substring of length `m` starts at index:

```text
n - m
```

So we slide a window of size `m` across the haystack.

For each `windowStart`, compare the substring:

```text
haystack[windowStart .. windowStart + m - 1]
```

with `needle`.

If all characters match, return `windowStart`.

If no match is found, return `-1`.

---

## Algorithm

1. Let `m = needle.length()` and `n = haystack.length()`
2. Iterate `windowStart` from `0` to `n - m`
3. For each `windowStart`, compare characters one by one
4. If all match, return `windowStart`
5. If no match exists, return `-1`

---

## Implementation

```java
class Solution {
    public int strStr(String haystack, String needle) {
        int m = needle.length();
        int n = haystack.length();

        for (int windowStart = 0; windowStart <= n - m; windowStart++) {
            for (int i = 0; i < m; i++) {
                if (needle.charAt(i) != haystack.charAt(windowStart + i)) {
                    break;
                }
                if (i == m - 1) {
                    return windowStart;
                }
            }
        }

        return -1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n * m)
```

For every starting position, we may compare up to `m` characters.

### Space Complexity

```text
O(1)
```

Only a few variables are used.

---

# Approach 2: Rabin–Karp Algorithm (Single Hash)

## Intuition

Rabin–Karp uses **hashing**.

If two strings are equal, then their hash values must be equal.

However, the reverse is not always true: two different strings can have the same hash value.
Such false matches are called **spurious hits**.

To reduce collisions:

- assign positional weights
- use a base (radix), preferably ≥ size of alphabet
- use modular arithmetic with a large prime

### Rolling Hash

Instead of recomputing the hash of every substring from scratch, we update it in `O(1)`.

If:

```text
H[i] = hash of haystack substring starting at i
```

then:

```text
H[i+1] = H[i] * base - outgoing_char * base^m + incoming_char
```

with modulo arithmetic applied.

Then:

- compare hash of current window with hash of `needle`
- only if hashes match, compare characters explicitly

---

## Algorithm

1. Let `m = needle.length()`, `n = haystack.length()`
2. If `n < m`, return `-1`
3. Choose:
   - `RADIX = 26`
   - `MOD = large prime`
4. Compute hash of `needle`
5. Compute rolling hashes of each window of length `m` in `haystack`
6. If window hash equals `needle` hash, verify character by character
7. Return the first matching index, otherwise `-1`

---

## Implementation

```java
class Solution {
    public int hashValue(String string, int RADIX, int MOD, int m) {
        long ans = 0;
        long factor = 1;
        for (int i = m - 1; i >= 0; i--) {
            ans = (ans + (string.charAt(i) - 'a') * factor) % MOD;
            factor = (factor * RADIX) % MOD;
        }
        return (int) ans;
    }

    public int strStr(String haystack, String needle) {
        int m = needle.length();
        int n = haystack.length();
        if (n < m) return -1;

        int RADIX = 26;
        int MOD = 1000000033;
        long MAX_WEIGHT = 1;

        for (int i = 0; i < m; i++) {
            MAX_WEIGHT = (MAX_WEIGHT * RADIX) % MOD;
        }

        long hashNeedle = hashValue(needle, RADIX, MOD, m), hashHay = 0;

        for (int windowStart = 0; windowStart <= n - m; windowStart++) {
            if (windowStart == 0) {
                hashHay = hashValue(haystack, RADIX, MOD, m);
            } else {
                hashHay = (((hashHay * RADIX) % MOD)
                        - (((int)(haystack.charAt(windowStart - 1) - 'a') * MAX_WEIGHT) % MOD)
                        + (int)(haystack.charAt(windowStart + m - 1) - 'a')
                        + MOD) % MOD;
            }

            if (hashNeedle == hashHay) {
                for (int i = 0; i < m; i++) {
                    if (needle.charAt(i) != haystack.charAt(i + windowStart)) {
                        break;
                    }
                    if (i == m - 1) {
                        return windowStart;
                    }
                }
            }
        }

        return -1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Worst case:

```text
O(n * m)
```

because every hash match may require character-by-character verification.

Best case:

```text
O(n + m)
```

if very few or no spurious hits occur.

### Space Complexity

```text
O(1)
```

---

# Approach 3: Rabin–Karp Algorithm (Double Hash)

## Intuition

To reduce spurious hits further, compute **two hash values** instead of one.

Use two different:

- radix values
- mod values

Now, a substring matches only if **both hash values** match.

This makes collisions extremely unlikely, allowing us to return as soon as the hash pair matches.

---

## Algorithm

1. Let `m = needle.length()`, `n = haystack.length()`
2. If `n < m`, return `-1`
3. Use:
   - `RADIX_1`, `MOD_1`
   - `RADIX_2`, `MOD_2`
4. Compute hash pair of `needle`
5. Compute rolling hash pair for each window in `haystack`
6. If both match, return `windowStart`
7. Else return `-1`

---

## Implementation

```java
class Solution {
    final int RADIX_1 = 26;
    final int MOD_1 = 1000000033;
    final int RADIX_2 = 27;
    final int MOD_2 = 2147483647;

    public long[] hashPair(String string, int m) {
        long hash1 = 0, hash2 = 0;
        long factor1 = 1, factor2 = 1;

        for (int i = m - 1; i >= 0; i--) {
            hash1 += ((int) (string.charAt(i) - 'a') * factor1) % MOD_1;
            factor1 = (factor1 * RADIX_1) % MOD_1;

            hash2 += ((int) (string.charAt(i) - 'a') * factor2) % MOD_2;
            factor2 = (factor2 * RADIX_2) % MOD_2;
        }

        return new long[] { hash1 % MOD_1, hash2 % MOD_2 };
    }

    public int strStr(String haystack, String needle) {
        int m = needle.length();
        int n = haystack.length();
        if (n < m) return -1;

        long MAX_WEIGHT_1 = 1;
        long MAX_WEIGHT_2 = 1;
        for (int i = 0; i < m; i++) {
            MAX_WEIGHT_1 = (MAX_WEIGHT_1 * RADIX_1) % MOD_1;
            MAX_WEIGHT_2 = (MAX_WEIGHT_2 * RADIX_2) % MOD_2;
        }

        long[] hashNeedle = hashPair(needle, m);
        long[] hashHay = { 0, 0 };

        for (int windowStart = 0; windowStart <= n - m; windowStart++) {
            if (windowStart == 0) {
                hashHay = hashPair(haystack, m);
            } else {
                hashHay[0] = (((hashHay[0] * RADIX_1) % MOD_1)
                        - (((int)(haystack.charAt(windowStart - 1) - 'a') * MAX_WEIGHT_1) % MOD_1)
                        + (int)(haystack.charAt(windowStart + m - 1) - 'a')
                        + MOD_1) % MOD_1;

                hashHay[1] = (((hashHay[1] * RADIX_2) % MOD_2)
                        - (((int)(haystack.charAt(windowStart - 1) - 'a') * MAX_WEIGHT_2) % MOD_2)
                        + (int)(haystack.charAt(windowStart + m - 1) - 'a')
                        + MOD_2) % MOD_2;
            }

            if (hashNeedle[0] == hashHay[0] && hashNeedle[1] == hashHay[1]) {
                return windowStart;
            }
        }

        return -1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

Hash pair comparison is `O(1)` per window, with no character-by-character verification.

### Space Complexity

```text
O(1)
```

---

# Approach 4: Knuth–Morris–Pratt (KMP) Algorithm

## Intuition

The brute-force method repeats many comparisons after mismatch.

KMP avoids that by preprocessing `needle` into a table often called:

- `lps`
- `prefix table`
- `longest_border`

This table tells us how far we can shift the pattern without re-checking characters we already know matched.

### Important Definitions

- **Prefix**: starts at beginning
- **Suffix**: ends at end
- **Proper Prefix**: not equal to whole string
- **Proper Suffix**: not equal to whole string
- **Border**: both proper prefix and proper suffix

### `lps[i]`

The length of the longest border of `needle[0..i]`.

This allows us to skip redundant work during matching.

---

## Algorithm

### Preprocess `needle`

Build `longest_border` array in linear time.

### Search

Use two pointers:

- `haystackPointer`
- `needlePointer`

If characters match:

- move both forward

If mismatch:

- if `needlePointer == 0`, move `haystackPointer`
- else set:

```text
needlePointer = longest_border[needlePointer - 1]
```

If `needlePointer == m`, match found.

---

## Implementation

```java
class Solution {
    public int strStr(String haystack, String needle) {
        int m = needle.length();
        int n = haystack.length();

        if (n < m) return -1;

        int[] longest_border = new int[m];
        int prev = 0;
        int i = 1;

        while (i < m) {
            if (needle.charAt(i) == needle.charAt(prev)) {
                prev += 1;
                longest_border[i] = prev;
                i += 1;
            } else {
                if (prev == 0) {
                    longest_border[i] = 0;
                    i += 1;
                } else {
                    prev = longest_border[prev - 1];
                }
            }
        }

        int haystackPointer = 0;
        int needlePointer = 0;

        while (haystackPointer < n) {
            if (haystack.charAt(haystackPointer) == needle.charAt(needlePointer)) {
                needlePointer += 1;
                haystackPointer += 1;

                if (needlePointer == m) {
                    return haystackPointer - m;
                }
            } else {
                if (needlePointer == 0) {
                    haystackPointer += 1;
                } else {
                    needlePointer = longest_border[needlePointer - 1];
                }
            }
        }

        return -1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

- preprocessing `needle`: `O(m)`
- searching in `haystack`: `O(n)`

Since `n >= m` when we proceed, total is `O(n)`.

### Space Complexity

```text
O(m)
```

for the `longest_border` / `lps` array.

---

# Summary

| Approach                 |                              Time |  Space | Notes                              |
| ------------------------ | --------------------------------: | -----: | ---------------------------------- |
| Sliding Window           |                        `O(n * m)` | `O(1)` | Simple brute force                 |
| Rabin–Karp (Single Hash) | Worst `O(n * m)`, best `O(n + m)` | `O(1)` | May have spurious hits             |
| Rabin–Karp (Double Hash) |                            `O(n)` | `O(1)` | Collision probability becomes tiny |
| KMP                      |                            `O(n)` | `O(m)` | Exact linear-time solution         |
