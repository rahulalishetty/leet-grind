# 1044. Longest Duplicate Substring — All Approaches in Java

---

## Problem Summary

Given a string `s`, find the longest substring that appears **at least twice** (occurrences may overlap). Return `""` if none exists.

**Constraints:** `2 <= s.length <= 3 * 10^4`, lowercase English letters only.

---

## Approach Overview

| #   | Approach                                  | Time Complexity | Space Complexity | Feasibility     |
| --- | ----------------------------------------- | --------------- | ---------------- | --------------- |
| 1   | Brute Force (all pairs)                   | O(n³)           | O(n²)            | TLE for large n |
| 2   | Binary Search + Set of Substrings         | O(n² log n)     | O(n²)            | TLE for large n |
| 3   | Binary Search + Rolling Hash (Rabin-Karp) | O(n log n) avg  | O(n)             | ✅ Optimal      |
| 4   | Suffix Array + LCP Array                  | O(n log n)      | O(n)             | ✅ Optimal      |

---

## Approach 1 — Brute Force (All Substring Pairs)

### Intuition

Try every possible substring length from longest to shortest. For each length `len`, generate all substrings of that length and check if any appears more than once using a `HashSet`. Return the first duplicate found.

### Algorithm

1. Iterate `len` from `n-1` down to `1`.
2. For each `len`, collect all substrings of that length into a `HashSet`.
3. If a substring is already in the set when we try to add it, it's a duplicate — return it.
4. If no duplicate found for any length, return `""`.

### Why It's Slow

- There are O(n²) substrings total.
- Each substring comparison/hash takes O(n) time.
- Total: **O(n³)** time and O(n²) space — too slow for n = 30,000.

### Code

```java
import java.util.HashSet;

public class BruteForce {
    public String longestDupSubstring(String s) {
        int n = s.length();

        // Try from longest possible duplicate down to length 1
        for (int len = n - 1; len >= 1; len--) {
            HashSet<String> seen = new HashSet<>();

            for (int i = 0; i <= n - len; i++) {
                String sub = s.substring(i, i + len);
                if (!seen.add(sub)) {
                    // 'add' returns false if already present → duplicate found
                    return sub;
                }
            }
        }

        return ""; // no duplicate substring exists
    }
}
```

### Complexity

- **Time:** O(n³) — outer loop O(n), inner loop O(n), substring creation O(n)
- **Space:** O(n²) — storing all substrings of a given length

---

## Approach 2 — Binary Search + HashSet of Substrings

### Intuition

**Key observation:** If a duplicate substring of length `k` exists, then a duplicate of length `k-1` also exists (it's a sub-substring). This means the answer space is **monotonic** — we can binary search on the length.

Instead of trying every length, binary search on `len` and for each candidate length, check existence using a `HashSet<String>`.

### Algorithm

1. Binary search on `len` in range `[1, n-1]`.
2. For each `len`, call `check(s, len)`:
   - Slide a window of size `len` across `s`.
   - Add each substring to a `HashSet`.
   - If any is already present → duplicate exists → return it (not null).
3. If `check` succeeds, try longer (`lo = mid + 1`) and save the result.
4. If `check` fails, try shorter (`hi = mid - 1`).

### Code

```java
import java.util.HashSet;

public class BinarySearchHashSet {

    public String longestDupSubstring(String s) {
        int lo = 1, hi = s.length() - 1;
        String result = "";

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            String found = check(s, mid);

            if (found != null) {
                // A duplicate of this length exists, try longer
                result = found;
                lo = mid + 1;
            } else {
                // No duplicate of this length, try shorter
                hi = mid - 1;
            }
        }

        return result;
    }

    /**
     * Returns a duplicate substring of exactly length `len`, or null if none exists.
     */
    private String check(String s, int len) {
        HashSet<String> seen = new HashSet<>();

        for (int i = 0; i <= s.length() - len; i++) {
            String sub = s.substring(i, i + len);
            if (!seen.add(sub)) {
                return sub;
            }
        }

        return null;
    }
}
```

### Complexity

- **Time:** O(n² log n) — O(log n) binary search steps × O(n) substrings × O(n) per substring hash
- **Space:** O(n²) — storing substrings in the set

### Improvement over Approach 1

Binary search cuts the number of `check` calls from O(n) to O(log n). But the bottleneck is still the O(n) cost per substring hash/comparison, making it O(n² log n) overall — still too slow for n = 30,000.

---

## Approach 3 — Binary Search + Rolling Hash (Rabin-Karp) ✅ Optimal

### Intuition

The bottleneck in Approach 2 is hashing each substring in O(n) time. We can compute hashes in **O(1)** per slide using a **rolling hash** (Rabin-Karp technique):

```
hash(s[i..i+len-1]) = s[i]*BASE^(len-1) + s[i+1]*BASE^(len-2) + ... + s[i+len-1]

Rolling step:
hash(s[i+1..i+len]) = hash(s[i..i+len-1]) * BASE
                    - s[i] * BASE^len
                    + s[i+len]
```

We use **double hashing** (two independent hash functions) to avoid collisions, since a single hash can give false positives.

### Algorithm

1. Binary search on `len` in `[1, n-1]`.
2. For each `len`, use `rollingHashCheck(s, len)`:
   - Compute the initial hash of `s[0..len-1]`.
   - Slide the window, updating the hash in O(1).
   - Store hashes in a `HashSet<Long>`.
   - On collision, verify the actual substring (to handle hash collisions).
3. Return the best result found.

### Code

```java
import java.util.HashMap;
import java.util.Map;

public class BinarySearchRollingHash {

    // Two bases and two mods for double hashing (reduces collision probability)
    private static final long BASE1 = 31L;
    private static final long MOD1  = 1_000_000_007L;
    private static final long BASE2 = 37L;
    private static final long MOD2  = 998_244_353L;

    public String longestDupSubstring(String s) {
        int lo = 1, hi = s.length() - 1;
        String result = "";

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            String found = rollingHashCheck(s, mid);

            if (found != null) {
                result = found;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return result;
    }

    /**
     * Uses rolling hash to find a duplicate substring of given length.
     * Returns the duplicate, or null if none exists.
     */
    private String rollingHashCheck(String s, int len) {
        int n = s.length();

        // Precompute BASE^len mod MOD for rolling removal of leading character
        long pow1 = 1L, pow2 = 1L;
        for (int i = 0; i < len; i++) {
            pow1 = pow1 * BASE1 % MOD1;
            pow2 = pow2 * BASE2 % MOD2;
        }

        long h1 = 0L, h2 = 0L;

        // Compute hash of first window s[0..len-1]
        for (int i = 0; i < len; i++) {
            int c = s.charAt(i) - 'a' + 1;
            h1 = (h1 * BASE1 + c) % MOD1;
            h2 = (h2 * BASE2 + c) % MOD2;
        }

        // Map from combined hash → starting index (for collision verification)
        // Key: packed 128-bit equivalent using a string "h1#h2"
        Map<String, Integer> seen = new HashMap<>();
        String key = h1 + "#" + h2;
        seen.put(key, 0);

        // Slide the window
        for (int i = 1; i <= n - len; i++) {
            int outChar = s.charAt(i - 1) - 'a' + 1;   // leaving window
            int inChar  = s.charAt(i + len - 1) - 'a' + 1; // entering window

            // Roll hash forward
            h1 = (h1 * BASE1 - outChar * pow1 % MOD1 + inChar + MOD1 * 2) % MOD1;
            h2 = (h2 * BASE2 - outChar * pow2 % MOD2 + inChar + MOD2 * 2) % MOD2;

            key = h1 + "#" + h2;

            if (seen.containsKey(key)) {
                // Verify to rule out hash collision
                int prevStart = seen.get(key);
                String candidate = s.substring(i, i + len);
                if (candidate.equals(s.substring(prevStart, prevStart + len))) {
                    return candidate;
                }
            }

            seen.put(key, i);
        }

        return null;
    }
}
```

### Complexity

- **Time:** O(n log n) average — O(log n) binary search steps × O(n) rolling hash per step
- **Space:** O(n) — the hash map stores at most n entries per check

### Key Insight: Why Double Hash?

A single hash function has ~1/MOD chance of collision per pair. With n = 30,000, we have up to ~10⁹ pairs — practically guaranteed false positives. Using two independent hashes reduces collision probability to ~1/(MOD1 × MOD2) ≈ 1/10¹⁸, which is negligible.

---

## Approach 4 — Suffix Array + LCP Array ✅ Optimal

### Intuition

A **suffix array** is an array of all suffixes of `s`, sorted lexicographically. The **Longest Common Prefix (LCP) array** stores the length of the longest common prefix between each pair of adjacent suffixes in the sorted order.

**Key insight:** The answer is simply `max(lcp[i])` over all `i`. Because adjacent suffixes in sorted order share the most common prefix, the maximum LCP value gives us the longest duplicate substring — no binary search needed.

### Example

For `s = "banana"`:

```
Suffixes sorted:
  0: "a"
  1: "ana"
  2: "anana"
  3: "banana"
  4: "na"
  5: "nana"

LCP array:
  lcp[0] = 0  (no prev)
  lcp[1] = 1  ("a" vs "ana"    → "a")
  lcp[2] = 3  ("ana" vs "anana"→ "ana")   ← maximum!
  lcp[3] = 0  ("anana" vs "banana")
  lcp[4] = 0  ("banana" vs "na")
  lcp[5] = 2  ("na" vs "nana"  → "na")

Max LCP = 3 at index 2, starting position = suffix_array[2] = 1 → "ana" ✅
```

### Building the Suffix Array in O(n log n)

We use the **prefix doubling** (Skew/DC3-lite) approach:

1. Start with each character as a rank-1 suffix.
2. Repeatedly double the comparison window, re-ranking suffixes.
3. After O(log n) rounds, all suffixes are fully sorted.

### Building the LCP Array in O(n) — Kasai's Algorithm

Uses the **inverse suffix array** and the property that `lcp[rank[i]] >= lcp[rank[i-1]] - 1`:

```
For each original position i (in text order):
  Let r = rank[i]  (position of suffix i in sorted array)
  Extend LCP from previous known value (never decreases by more than 1 per step)
```

### Code

```java
import java.util.Arrays;

public class SuffixArrayLCP {

    public String longestDupSubstring(String s) {
        int n = s.length();
        int[] sa  = buildSuffixArray(s);   // sorted suffix start indices
        int[] lcp = buildLCPArray(s, sa);  // LCP between adjacent suffixes

        int maxLen = 0, startIdx = 0;
        for (int i = 1; i < n; i++) {      // lcp[0] is 0 by convention
            if (lcp[i] > maxLen) {
                maxLen   = lcp[i];
                startIdx = sa[i];
            }
        }

        return s.substring(startIdx, startIdx + maxLen);
    }

    // ---------------------------------------------------------------
    // Suffix Array — O(n log² n) with Arrays.sort
    // (O(n log n) is possible with radix sort but this is simpler)
    // ---------------------------------------------------------------
    private int[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank   = new int[n];
        int[] tmp    = new int[n];

        // Initial ranks = character values
        for (int i = 0; i < n; i++) {
            sa[i]   = i;
            rank[i] = s.charAt(i);
        }

        for (int gap = 1; gap < n; gap <<= 1) {
            final int g      = gap;
            final int[] rank_ = rank;

            // Sort by (rank[i], rank[i+gap])
            Arrays.sort(sa, (a, b) -> {
                if (rank_[a] != rank_[b]) return rank_[a] - rank_[b];
                int ra = a + g < n ? rank_[a + g] : -1;
                int rb = b + g < n ? rank_[b + g] : -1;
                return ra - rb;
            });

            // Recompute ranks
            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                tmp[sa[i]] = tmp[sa[i - 1]];
                // Same pair as previous → same rank; otherwise increment
                boolean samePair =
                    rank[sa[i]]     == rank[sa[i - 1]] &&
                    (sa[i]     + g < n ? rank[sa[i]     + g] : -1) ==
                    (sa[i - 1] + g < n ? rank[sa[i - 1] + g] : -1);
                if (!samePair) tmp[sa[i]]++;
            }

            System.arraycopy(tmp, 0, rank, 0, n);

            // All ranks unique → fully sorted
            if (rank[sa[n - 1]] == n - 1) break;
        }

        // Convert Integer[] → int[]
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    // ---------------------------------------------------------------
    // LCP Array — Kasai's Algorithm O(n)
    // ---------------------------------------------------------------
    private int[] buildLCPArray(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n]; // inverse suffix array: rank[sa[i]] = i
        for (int i = 0; i < n; i++) rank[sa[i]] = i;

        int[] lcp = new int[n];
        int h = 0; // current LCP length being extended

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // the suffix just before suffix i in sorted order
                // Extend common prefix
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) h--; // LCP can drop by at most 1 when moving to next suffix in text order
            }
        }

        return lcp;
    }
}
```

### Complexity

- **Time:** O(n log² n) for the suffix array (O(n log n) with radix sort), O(n) for LCP → **O(n log² n)** total
- **Space:** O(n) — suffix array, rank, LCP arrays

### Comparison with Approach 3

|                | Binary Search + Rolling Hash | Suffix Array + LCP     |
| -------------- | ---------------------------- | ---------------------- |
| Time           | O(n log n) average           | O(n log² n)            |
| Collision risk | Tiny but exists              | None (exact)           |
| Implementation | Moderate                     | More complex           |
| Single-pass    | No (log n passes)            | Yes (one sort + Kasai) |

---

## Putting It All Together — Driver / Test Harness

```java
public class Main {
    public static void main(String[] args) {
        String[] inputs  = {"banana", "abcd", "aaaa", "abcabc"};
        String[] expects = {"ana",    "",     "aaa",  "abc"   };

        BinarySearchRollingHash sol1 = new BinarySearchRollingHash();
        SuffixArrayLCP          sol2 = new SuffixArrayLCP();

        for (int i = 0; i < inputs.length; i++) {
            String s   = inputs[i];
            String r1  = sol1.longestDupSubstring(s);
            String r2  = sol2.longestDupSubstring(s);
            System.out.printf(
                "s=%-10s  expected=%-5s  rollingHash=%-5s  suffixArray=%-5s  ok=%b%n",
                s, expects[i], r1, r2,
                r1.length() == expects[i].length() && r2.length() == expects[i].length()
            );
        }
    }
}
```

**Output:**

```
s=banana      expected=ana    rollingHash=ana    suffixArray=ana    ok=true
s=abcd        expected=       rollingHash=       suffixArray=       ok=true
s=aaaa        expected=aaa    rollingHash=aaa    suffixArray=aaa    ok=true
s=abcabc      expected=abc    rollingHash=abc    suffixArray=abc    ok=true
```

---

## Which Approach Should You Use?

```
Is n small (< 1000)?
  └─ Yes → Approach 1 (Brute Force) — simplest to write and debug

Is n medium (< 5000)?
  └─ Yes → Approach 2 (Binary Search + HashSet) — easy to understand

Is n large (up to 30,000)?
  ├─ Want simplest correct solution? → Approach 3 (Rolling Hash) ✅
  └─ Want provably correct / no collision risk? → Approach 4 (Suffix Array) ✅
```

In interviews, **Approach 3** is the sweet spot: fast, reasonably short, and demonstrates deep algorithmic knowledge (binary search + hashing). **Approach 4** demonstrates mastery of string data structures but is significantly more code.
