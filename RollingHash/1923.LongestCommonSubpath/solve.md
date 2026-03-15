# 1923. Longest Common Subpath — Java Solutions and Detailed Notes

## Problem

We are given:

- an integer `n`, the number of cities,
- a list of paths `paths`, where each `paths[i]` is a sequence of visited cities.

We need to return the **length of the longest common subpath** shared by **every** path.

A **subpath** is a **contiguous** sequence.

---

## Examples

### Example 1

```text
n = 5
paths = [
  [0,1,2,3,4],
  [2,3,4],
  [4,0,1,2,3]
]
```

A longest common subpath is:

```text
[2,3]
```

So the answer is:

```text
2
```

---

### Example 2

```text
n = 3
paths = [[0],[1],[2]]
```

No city sequence appears in every path, so the answer is:

```text
0
```

---

### Example 3

```text
n = 5
paths = [
  [0,1,2,3,4],
  [4,3,2,1,0]
]
```

The common subpaths of maximum length are just single cities:

```text
[0], [1], [2], [3], [4]
```

So the answer is:

```text
1
```

---

# Core observations

## 1. The answer is monotonic

If there exists a common subpath of length `L`, then there also exists a common subpath of every smaller length:

```text
1, 2, ..., L-1
```

That means we can **binary search** the answer length.

---

## 2. The real subproblem is this

For a fixed length `len`:

> Does there exist a subpath of length `len` that appears in **all** paths?

If we can answer that efficiently, then binary search gives the final answer.

---

## 3. Constraints strongly suggest hashing

The total number of path elements across all friends is at most:

```text
10^5
```

So a solution around:

```text
O(totalLength * log answer)
```

is feasible.

That is why the standard solution is:

- **binary search on length**
- **rolling hash / Rabin-Karp** for each fixed length check

---

# Approach 1: Brute force intersection of all subpaths (conceptual, too slow)

## Idea

For each path, generate all subpaths of all lengths.

Then intersect the sets across all paths and find the largest length that survives.

This is conceptually straightforward but completely impractical.

---

## Why it is too slow

A single path of length `m` has:

```text
m * (m + 1) / 2
```

subpaths.

Even though total input size is only `10^5`, explicit substring/subarray storage explodes.

---

## Java sketch

```java
import java.util.*;

class Solution {
    public int longestCommonSubpath(int n, int[][] paths) {
        Set<String> common = null;

        for (int[] path : paths) {
            Set<String> current = new HashSet<>();
            for (int i = 0; i < path.length; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = i; j < path.length; j++) {
                    sb.append(path[j]).append('#');
                    current.add(sb.toString());
                }
            }

            if (common == null) {
                common = current;
            } else {
                common.retainAll(current);
            }
        }

        int ans = 0;
        if (common != null) {
            for (String s : common) {
                int len = s.split("#").length;
                ans = Math.max(ans, len);
            }
        }
        return ans;
    }
}
```

---

## Complexity

Far too large in both time and memory.

This is only a baseline.

---

# Approach 2: Binary Search + HashSet of stringified subarrays (still too slow)

## Idea

Use binary search on the answer `len`.

To check a fixed `len`:

- generate every subpath of length `len` for the first path,
- store them in a set,
- for each next path, generate all subpaths of length `len` and intersect.

This avoids all lengths at once, but still creates explicit representations of subpaths.

---

## Complexity

If each subpath is converted to a string or list form, checking length `len` can cost:

```text
O(totalLength * len)
```

Binary search makes this too slow in the worst case.

So we need rolling hash.

---

# Approach 3: Binary Search + Single Rolling Hash (good, but collision risk)

## Idea

For a fixed length `len`:

1. Compute rolling hashes for every subpath of length `len` in each path.
2. For the first path, store all these hashes in a set.
3. For each subsequent path, compute its subpath hashes and keep only hashes that also appeared in all previous paths.
4. If after processing all paths the intersection is non-empty, then length `len` is feasible.

Then binary search the maximum feasible `len`.

---

## Why binary search works

Let `check(len)` mean:

> there exists a common subpath of length `len`.

If `check(len)` is true, then `check(x)` is also true for every `x < len`.

So feasibility is monotonic.

---

## Rolling hash formula

For an array `a[0..m-1]`, define prefix hash:

```text
H[i+1] = H[i] * base + (a[i] + 1)
```

Then subarray hash of `[l..r]` is:

```text
H[r+1] - H[l] * base^(r-l+1)
```

The `+1` is just to avoid issues with zero-valued cities.

---

## Java code (single hash)

```java
import java.util.*;

class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 100_000_019L;

    public int longestCommonSubpath(int n, int[][] paths) {
        int minLen = Integer.MAX_VALUE;
        for (int[] path : paths) {
            minLen = Math.min(minLen, path.length);
        }

        long[] pow = new long[minLen + 1];
        pow[0] = 1;
        for (int i = 1; i <= minLen; i++) {
            pow[i] = (pow[i - 1] * BASE) % MOD;
        }

        int left = 0, right = minLen;
        while (left < right) {
            int mid = left + (right - left + 1) / 2;
            if (check(paths, mid, pow)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    private boolean check(int[][] paths, int len, long[] pow) {
        if (len == 0) return true;

        Set<Long> common = null;

        for (int[] path : paths) {
            if (path.length < len) return false;

            long[] prefix = new long[path.length + 1];
            for (int i = 0; i < path.length; i++) {
                prefix[i + 1] = (prefix[i] * BASE + (path[i] + 1)) % MOD;
            }

            Set<Long> current = new HashSet<>();
            for (int i = 0; i + len <= path.length; i++) {
                long hash = getHash(prefix, i, i + len - 1, pow);
                current.add(hash);
            }

            if (common == null) {
                common = current;
            } else {
                common.retainAll(current);
            }

            if (common.isEmpty()) return false;
        }

        return common != null && !common.isEmpty();
    }

    private long getHash(long[] prefix, int l, int r, long[] pow) {
        long ans = (prefix[r + 1] - (prefix[l] * pow[r - l + 1]) % MOD) % MOD;
        if (ans < 0) ans += MOD;
        return ans;
    }
}
```

---

## Complexity

Let `S = sum of all path lengths`.

Each `check(len)` processes all subpaths of length `len` across all paths:

```text
O(S)
```

Binary search adds:

```text
O(log minPathLength)
```

So time complexity is roughly:

```text
O(S log M)
```

where `M = min path length`.

Space complexity is about:

```text
O(S)
```

for the sets in the worst case.

---

## Limitation

Single rolling hash has collision risk.

That is why the fully robust solution uses **double hashing**.

---

# Approach 4: Binary Search + Double Rolling Hash (Recommended)

This is the standard strong solution.

## Idea

Same structure as Approach 3, but use two moduli.

A subpath is represented by a pair:

```text
(hash1, hash2)
```

This makes collision probability negligible.

To store them conveniently in Java, combine the two hashes into one `long`.

---

## High-level algorithm

### Step 1

Find the minimum path length.

That is the upper bound for the answer.

### Step 2

Binary search on `len`.

### Step 3

For a fixed `len`, compute all double-hashes of subpaths of length `len` in each path.

Use set intersection across all paths.

If the final intersection is non-empty, `len` works.

---

## Java code

```java
import java.util.*;

class Solution {
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE1 = 100_000_007L;
    private static final long BASE2 = 100_000_037L;

    public int longestCommonSubpath(int n, int[][] paths) {
        int minLen = Integer.MAX_VALUE;
        for (int[] path : paths) {
            minLen = Math.min(minLen, path.length);
        }

        long[] pow1 = new long[minLen + 1];
        long[] pow2 = new long[minLen + 1];
        pow1[0] = 1;
        pow2[0] = 1;
        for (int i = 1; i <= minLen; i++) {
            pow1[i] = (pow1[i - 1] * BASE1) % MOD1;
            pow2[i] = (pow2[i - 1] * BASE2) % MOD2;
        }

        int left = 0, right = minLen;
        while (left < right) {
            int mid = left + (right - left + 1) / 2;
            if (check(paths, mid, pow1, pow2)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    private boolean check(int[][] paths, int len, long[] pow1, long[] pow2) {
        if (len == 0) return true;

        Set<Long> common = null;

        for (int[] path : paths) {
            if (path.length < len) return false;

            long[] pref1 = new long[path.length + 1];
            long[] pref2 = new long[path.length + 1];

            for (int i = 0; i < path.length; i++) {
                long v = path[i] + 1L;
                pref1[i + 1] = (pref1[i] * BASE1 + v) % MOD1;
                pref2[i + 1] = (pref2[i] * BASE2 + v) % MOD2;
            }

            Set<Long> current = new HashSet<>();
            for (int i = 0; i + len <= path.length; i++) {
                long h1 = getHash(pref1, i, i + len - 1, pow1, MOD1);
                long h2 = getHash(pref2, i, i + len - 1, pow2, MOD2);
                long combined = (h1 << 32) ^ h2;
                current.add(combined);
            }

            if (common == null) {
                common = current;
            } else {
                common.retainAll(current);
            }

            if (common.isEmpty()) return false;
        }

        return common != null && !common.isEmpty();
    }

    private long getHash(long[] pref, int l, int r, long[] pow, long mod) {
        long ans = (pref[r + 1] - (pref[l] * pow[r - l + 1]) % mod) % mod;
        if (ans < 0) ans += mod;
        return ans;
    }
}
```

---

## Complexity

Let `S = sum(paths[i].length)`.

Each `check(len)` runs in:

```text
O(S)
```

Binary search over length adds:

```text
O(log minPathLength)
```

Total time complexity:

```text
O(S log M)
```

where `M = min path length`.

Space complexity:

```text
O(S)
```

This is the best practical solution for the given constraints.

---

# Approach 5: Suffix Array / Suffix Automaton style discussion (advanced but not practical here)

## Idea

One might think of concatenating paths with separators and using suffix array / suffix automaton / suffix tree ideas to find the longest substring appearing in all groups.

This is theoretically possible, but significantly more complicated because:

- paths are arrays of integers, not plain lowercase strings,
- we need substring presence across **multiple distinct sequences**,
- separators and ownership tracking become necessary,
- implementation complexity in Java becomes high.

So while suffix structures are possible in theory, the intended competitive-programming solution here is:

```text
Binary Search + Rolling Hash
```

---

# Why set intersection works

For a fixed `len`, every path contributes a set of hashes of its subpaths of length `len`.

If we intersect those sets across all paths:

- any remaining hash corresponds to a candidate subpath that appears in every path.

So the existence question becomes:

```text
intersection is non-empty ?
```

That is exactly the `check(len)` predicate we need for binary search.

---

# Why binary search works

Suppose a common subpath of length `L` exists.

Then taking any contiguous part of that subpath of length `x < L` also gives a common subpath.

So feasibility is monotonic:

```text
true, true, true, ..., false, false
```

This is the ideal structure for binary search.

---

# Comparison of approaches

## Approach 1: Full brute force over subpaths

### Pros

- conceptually straightforward

### Cons

- completely impractical

---

## Approach 2: Binary search + explicit subarray/string representation

### Pros

- improves over brute force
- conceptually easy

### Cons

- still too heavy due to representation costs

---

## Approach 3: Binary search + single rolling hash

### Pros

- fast
- relatively easy to code

### Cons

- collision risk

### Complexity

```text
O(S log M)
```

---

## Approach 4: Binary search + double rolling hash

### Pros

- practical and robust
- standard intended solution
- handles constraints well

### Cons

- more code than single hash

### Complexity

```text
O(S log M)
```

---

## Approach 5: Suffix-structure discussion

### Pros

- theoretically elegant

### Cons

- overcomplicated here
- not practical for this problem in Java contest conditions

---

# Final recommended solution

The best solution is:

## Binary Search + Double Rolling Hash + Set Intersection

It is efficient, robust, and directly matches the monotonic structure of the problem.

---

# Final polished Java solution

```java
import java.util.*;

class Solution {
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE1 = 100_000_007L;
    private static final long BASE2 = 100_000_037L;

    public int longestCommonSubpath(int n, int[][] paths) {
        int minLen = Integer.MAX_VALUE;
        for (int[] path : paths) {
            minLen = Math.min(minLen, path.length);
        }

        long[] pow1 = new long[minLen + 1];
        long[] pow2 = new long[minLen + 1];
        pow1[0] = 1;
        pow2[0] = 1;

        for (int i = 1; i <= minLen; i++) {
            pow1[i] = (pow1[i - 1] * BASE1) % MOD1;
            pow2[i] = (pow2[i - 1] * BASE2) % MOD2;
        }

        int left = 0, right = minLen;
        while (left < right) {
            int mid = left + (right - left + 1) / 2;
            if (existsCommon(paths, mid, pow1, pow2)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    private boolean existsCommon(int[][] paths, int len, long[] pow1, long[] pow2) {
        if (len == 0) return true;

        Set<Long> common = null;

        for (int[] path : paths) {
            if (path.length < len) return false;

            long[] pref1 = new long[path.length + 1];
            long[] pref2 = new long[path.length + 1];

            for (int i = 0; i < path.length; i++) {
                long val = path[i] + 1L;
                pref1[i + 1] = (pref1[i] * BASE1 + val) % MOD1;
                pref2[i + 1] = (pref2[i] * BASE2 + val) % MOD2;
            }

            Set<Long> current = new HashSet<>();
            for (int i = 0; i + len <= path.length; i++) {
                long h1 = subHash(pref1, i, i + len - 1, pow1, MOD1);
                long h2 = subHash(pref2, i, i + len - 1, pow2, MOD2);
                long key = (h1 << 32) ^ h2;
                current.add(key);
            }

            if (common == null) {
                common = current;
            } else {
                common.retainAll(current);
            }

            if (common.isEmpty()) {
                return false;
            }
        }

        return common != null && !common.isEmpty();
    }

    private long subHash(long[] pref, int l, int r, long[] pow, long mod) {
        long ans = (pref[r + 1] - (pref[l] * pow[r - l + 1]) % mod) % mod;
        if (ans < 0) ans += mod;
        return ans;
    }
}
```

---

# Worked example

## Example 1

```text
paths = [
  [0,1,2,3,4],
  [2,3,4],
  [4,0,1,2,3]
]
```

Try binary search length `2`.

Subpaths of length 2:

- path 1:

  ```text
  [0,1], [1,2], [2,3], [3,4]
  ```

- path 2:

  ```text
  [2,3], [3,4]
  ```

- path 3:
  ```text
  [4,0], [0,1], [1,2], [2,3]
  ```

Intersection contains:

```text
[2,3]
```

So length `2` works.

Try length `3`.

No common subpath of length `3` appears in all three paths.

So the answer is:

```text
2
```

---

# Takeaway pattern

This problem is a classic example of:

```text
Binary Search on answer + Rolling Hash for fixed-length substring/subarray checks
```

Whenever you see:

- longest common substring / subarray,
- multiple sequences,
- monotonic answer length,

you should strongly consider this pattern.

For this problem, that is the right tool.
