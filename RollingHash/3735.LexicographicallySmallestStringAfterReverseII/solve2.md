# 3735. Lexicographically Smallest String After Reverse II — TLE Fix

## Why the previous solution TLEs

The direct approach tries all `2n` operations:

- reverse first `k`
- reverse last `k`

and then compares/builds strings of length `n`.

That becomes:

```text
O(n^2)
```

which is too slow for:

```text
n <= 10^5
```

---

# Better idea: compare candidates **without building them fully**

Each result is one of two structured forms.

## 1) Reverse prefix of length `k`

```text
reverse(s[0..k-1]) + s[k..n-1]
```

## 2) Reverse suffix of length `k`

```text
s[0..n-k-1] + reverse(s[n-k..n-1])
```

So there are still `2n` candidates, but each candidate is highly structured.

The key improvement is:

- compare two candidates using **LCP (longest common prefix)**
- compute prefix hashes in `O(1)`
- binary search the first mismatch in `O(log n)`

That gives:

```text
O(n log n)
```

overall.

---

# Data structure

We build rolling hashes for:

- `s`
- `rev = reverse(s)`

Then any substring hash can be queried in `O(1)`.

Since every candidate is made from at most two substrings of `s` / `rev`, we can compute the hash of any candidate prefix in `O(1)` as well.

---

# Candidate representation

We represent a candidate by:

- `type = 0` → reverse prefix
- `type = 1` → reverse suffix
- `k`

## Character at position `pos`

### Prefix reversal

```text
if pos < k:
    s[k - 1 - pos]
else:
    s[pos]
```

### Suffix reversal

Let:

```text
start = n - k
```

Then:

```text
if pos < start:
    s[pos]
else:
    s[n - 1 - (pos - start)]
```

---

# Comparing two candidates

To compare candidate `A` and `B` lexicographically:

1. binary search the largest prefix length `L` such that the first `L` characters are equal
2. if `L == n`, they are identical
3. otherwise compare the character at index `L`

This makes one comparison cost:

```text
O(log n)
```

If we keep the current best candidate and compare every other candidate against it, total becomes:

```text
O(n log n)
```

---

# Correct Java Solution

```java
class Solution {
    static class Hasher {
        private final long mod;
        private final long base;
        private final long[] pref;
        private final long[] pow;

        Hasher(String s, long base, long mod) {
            this.mod = mod;
            this.base = base;
            int n = s.length();
            pref = new long[n + 1];
            pow = new long[n + 1];
            pow[0] = 1;

            for (int i = 0; i < n; i++) {
                pref[i + 1] = (pref[i] * base + (s.charAt(i) - 'a' + 1)) % mod;
                pow[i + 1] = (pow[i] * base) % mod;
            }
        }

        long get(int l, int r) {
            if (l > r) return 0;
            long res = (pref[r + 1] - pref[l] * pow[r - l + 1]) % mod;
            if (res < 0) res += mod;
            return res;
        }

        long concat(long leftHash, int rightLen, long rightHash) {
            return (leftHash * pow[rightLen] + rightHash) % mod;
        }
    }

    private String s;
    private String rev;
    private int n;

    private Hasher hs1, hs2, hr1, hr2;
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE1 = 911_382_323L;
    private static final long BASE2 = 972_663_749L;

    public String smallestString(String s) {
        this.s = s;
        this.rev = new StringBuilder(s).reverse().toString();
        this.n = s.length();

        hs1 = new Hasher(s, BASE1, MOD1);
        hs2 = new Hasher(s, BASE2, MOD2);
        hr1 = new Hasher(rev, BASE1, MOD1);
        hr2 = new Hasher(rev, BASE2, MOD2);

        int bestType = 0;
        int bestK = 1;

        for (int type = 0; type < 2; type++) {
            for (int k = 1; k <= n; k++) {
                if (compare(type, k, bestType, bestK) < 0) {
                    bestType = type;
                    bestK = k;
                }
            }
        }

        return build(bestType, bestK);
    }

    private int compare(int type1, int k1, int type2, int k2) {
        int lo = 0, hi = n;
        while (lo < hi) {
            int mid = (lo + hi + 1) >>> 1;
            if (samePrefix(type1, k1, type2, k2, mid)) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        int lcp = lo;
        if (lcp == n) return 0;

        char c1 = getChar(type1, k1, lcp);
        char c2 = getChar(type2, k2, lcp);
        return c1 - c2;
    }

    private boolean samePrefix(int type1, int k1, int type2, int k2, int len) {
        long h11 = prefixHash(type1, k1, len, hs1, hr1);
        long h12 = prefixHash(type1, k1, len, hs2, hr2);
        long h21 = prefixHash(type2, k2, len, hs1, hr1);
        long h22 = prefixHash(type2, k2, len, hs2, hr2);
        return h11 == h21 && h12 == h22;
    }

    private long prefixHash(int type, int k, int len, Hasher hs, Hasher hr) {
        if (len == 0) return 0;

        if (type == 0) { // reverse prefix of length k
            // candidate = rev[n-k .. n-1] + s[k .. n-1]
            if (len <= k) {
                return hr.get(n - k, n - k + len - 1);
            } else {
                long left = hr.get(n - k, n - 1);
                long right = hs.get(k, k + (len - k) - 1);
                return hs.concat(left, len - k, right);
            }
        } else { // reverse suffix of length k
            int start = n - k;
            // candidate = s[0 .. start-1] + rev[0 .. k-1]
            if (len <= start) {
                return hs.get(0, len - 1);
            } else {
                long left = hs.get(0, start - 1);
                long right = hr.get(0, len - start - 1);
                return hs.concat(left, len - start, right);
            }
        }
    }

    private char getChar(int type, int k, int pos) {
        if (type == 0) { // reverse prefix
            if (pos < k) return s.charAt(k - 1 - pos);
            return s.charAt(pos);
        } else { // reverse suffix
            int start = n - k;
            if (pos < start) return s.charAt(pos);
            return s.charAt(n - 1 - (pos - start));
        }
    }

    private String build(int type, int k) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(getChar(type, k, i));
        }
        return sb.toString();
    }
}
```

---

# Complexity

## Time

There are `2n` candidates.

Each comparison with the current best uses binary search on the LCP:

```text
O(log n)
```

So total:

```text
O(n log n)
```

## Space

Hashes, powers, reversed string, and output:

```text
O(n)
```

---

# Summary

Your earlier solution timed out because it effectively did full-string work for every `k`.

The fix is to:

- keep all candidates implicit
- compare them with rolling hash + LCP
- build only the final winner

That changes the solution from:

```text
O(n^2)
```

to:

```text
O(n log n)
```
