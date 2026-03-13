# 3569. Maximize Count of Distinct Primes After Split — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int[] maximumCount(int[] nums, int[][] queries) {

    }
}
```

---

# Problem Restatement

For each query:

1. update one position in `nums`
2. choose a split point `k` with:

```text
1 <= k < n
```

so that:

- prefix = `nums[0..k-1]`
- suffix = `nums[k..n-1]`

and the value

```text
(# distinct prime values in prefix) + (# distinct prime values in suffix)
```

is maximized.

Updates persist across queries.

We must return the maximum value after each query.

---

# Core Insight

For any prime value `p`, its contribution to a split is:

- `1` if `p` appears in the prefix
- `1` if `p` appears in the suffix

So for a fixed split, each distinct prime contributes:

- `0` if absent everywhere
- `1` if all occurrences lie only on one side
- `2` if it appears on both sides

This is much more useful than thinking position-by-position.

---

# Contribution of One Prime

Suppose a prime `p` appears at positions:

```text
first[p] ... last[p]
```

For a split at position `k`:

- `p` is in the prefix iff some occurrence is `< k`
- `p` is in the suffix iff some occurrence is `>= k`

So `p` contributes `2` exactly when:

```text
first[p] < k <= last[p]
```

That means:

> prime `p` contributes an extra `+1` on all split points inside the interval
> `(first[p], last[p]]`

And every prime that exists at all contributes a baseline `1`.

So the answer for a split `k` is:

```text
(number of distinct prime values present in nums)
+ (number of prime intervals covering split k)
```

Therefore the problem reduces to dynamically maintaining:

1. how many distinct prime values currently exist
2. for each split point `k`, how many prime intervals cover it
3. the maximum covered split value over all `k`

---

# Range Add Interpretation

For each distinct prime value `p` currently present:

- if it appears once, it contributes only baseline `1`
- if it appears multiple times, it contributes extra `+1` on split positions:

```text
[first[p] + 1, last[p]]
```

because a split `k` belongs to prefix/suffix convention:

- left side uses indices `< k`
- right side uses indices `>= k`

So if we maintain an array over split positions `1..n-1` where each prime interval adds `+1`, then:

```text
answer = distinctPrimeCount + maxCoverage
```

where `maxCoverage` is the maximum value in that split-position array.

This is the main reduction.

---

# Data Structure Needed

We need dynamic updates after each query.

Changing `nums[idx]` only affects:

- the old value at `idx`
- the new value at `idx`

So only two prime values can change their occurrence sets per query.

For each prime value, we need to know:

- its smallest index
- its largest index

This suggests maintaining, for each prime value, an ordered set of positions.

When a prime’s first/last occurrence changes, we update its interval contribution in a segment tree or Fenwick-like range-add structure.

Because we need the **maximum** coverage over all split points after range adds, a lazy segment tree is a natural fit.

---

# Approach 1 — Ordered Sets per Prime + Lazy Segment Tree (Recommended)

## Idea

Maintain:

- `isPrime[x]` by sieve up to `10^5`
- for each prime value `p`, a sorted set of positions where `nums[i] == p`
- `distinctPrimeCount`
- a segment tree over split positions `1..n-1` supporting:
  - range add
  - global max query

For a prime `p`:

- if its position set is empty: contributes nothing
- if non-empty: contributes baseline `1` to `distinctPrimeCount`
- if `first < last`: contributes range add `+1` over:

```text
[first + 1, last]
```

When a query changes `nums[idx]`:

1. remove old value’s contribution if old value is prime
2. update its position set
3. re-add its new contribution if still present
4. do the same for the new value

Then answer is:

```text
distinctPrimeCount + segTree.max()
```

---

## Why this works

Every prime present contributes at least `1` no matter where we split.

The only extra gain comes from primes appearing on both sides, and that happens exactly for split points inside the interval from its first to last occurrence.

So the global optimum is exactly the baseline distinct-prime count plus the maximum overlap of these intervals.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class SegTree {
        int n;
        int[] max;
        int[] lazy;

        SegTree(int n) {
            this.n = n;
            this.max = new int[4 * Math.max(1, n)];
            this.lazy = new int[4 * Math.max(1, n)];
        }

        void rangeAdd(int l, int r, int val) {
            if (l > r || n == 0) return;
            rangeAdd(1, 1, n, l, r, val);
        }

        private void rangeAdd(int node, int nl, int nr, int l, int r, int val) {
            if (r < nl || nr < l) return;
            if (l <= nl && nr <= r) {
                max[node] += val;
                lazy[node] += val;
                return;
            }
            push(node);
            int mid = (nl + nr) >>> 1;
            rangeAdd(node << 1, nl, mid, l, r, val);
            rangeAdd(node << 1 | 1, mid + 1, nr, l, r, val);
            max[node] = Math.max(max[node << 1], max[node << 1 | 1]);
        }

        private void push(int node) {
            if (lazy[node] != 0) {
                int v = lazy[node];
                max[node << 1] += v;
                lazy[node << 1] += v;
                max[node << 1 | 1] += v;
                lazy[node << 1 | 1] += v;
                lazy[node] = 0;
            }
        }

        int queryMax() {
            return n == 0 ? 0 : max[1];
        }
    }

    public int[] maximumCount(int[] nums, int[][] queries) {
        int n = nums.length;
        int maxV = 100000;
        boolean[] prime = sieve(maxV);

        @SuppressWarnings("unchecked")
        TreeSet<Integer>[] pos = new TreeSet[maxV + 1];

        int distinctPrimeCount = 0;
        SegTree st = new SegTree(n - 1);

        for (int i = 0; i < n; i++) {
            int v = nums[i];
            if (!prime[v]) continue;
            if (pos[v] == null) pos[v] = new TreeSet<>();
            if (pos[v].isEmpty()) distinctPrimeCount++;
            pos[v].add(i);
        }

        for (int v = 2; v <= maxV; v++) {
            if (!prime[v] || pos[v] == null || pos[v].isEmpty()) continue;
            addPrimeInterval(pos[v], st, +1);
        }

        int[] ans = new int[queries.length];

        for (int qi = 0; qi < queries.length; qi++) {
            int idx = queries[qi][0];
            int newVal = queries[qi][1];
            int oldVal = nums[idx];

            if (oldVal == newVal) {
                ans[qi] = distinctPrimeCount + st.queryMax();
                continue;
            }

            if (prime[oldVal]) {
                removePrimeContribution(oldVal, idx, pos, st);
                if (pos[oldVal].isEmpty()) distinctPrimeCount--;
            }

            nums[idx] = newVal;

            if (prime[newVal]) {
                if (pos[newVal] == null) pos[newVal] = new TreeSet<>();
                if (pos[newVal].isEmpty()) distinctPrimeCount++;
                addPrimeContribution(newVal, idx, pos, st);
            }

            ans[qi] = distinctPrimeCount + st.queryMax();
        }

        return ans;
    }

    private void removePrimeContribution(int value, int idx, TreeSet<Integer>[] pos, SegTree st) {
        TreeSet<Integer> set = pos[value];
        addPrimeInterval(set, st, -1);
        set.remove(idx);
        addPrimeInterval(set, st, +1);
    }

    private void addPrimeContribution(int value, int idx, TreeSet<Integer>[] pos, SegTree st) {
        TreeSet<Integer> set = pos[value];
        addPrimeInterval(set, st, -1);
        set.add(idx);
        addPrimeInterval(set, st, +1);
    }

    private void addPrimeInterval(TreeSet<Integer> set, SegTree st, int delta) {
        if (set == null || set.isEmpty()) return;
        int first = set.first();
        int last = set.last();
        if (first < last) {
            st.rangeAdd(first + 1, last, delta);
        }
    }

    private boolean[] sieve(int n) {
        boolean[] prime = new boolean[n + 1];
        if (n >= 2) Arrays.fill(prime, 2, n + 1, true);
        for (int p = 2; p * p <= n; p++) {
            if (!prime[p]) continue;
            for (int x = p * p; x <= n; x += p) {
                prime[x] = false;
            }
        }
        return prime;
    }
}
```

---

## Complexity

Let:

- `n = nums.length`
- `q = queries.length`

Each query only changes two values’ occurrence sets, and each change performs a constant number of:

- ordered-set operations: `O(log n)`
- segment tree range updates: `O(log n)`

So:

```text
Time:  O((n + q) log n + V log log V)
Space: O(n + V)
```

where `V = 10^5` for the sieve.

This fits the constraints comfortably.

---

# Approach 2 — Occurrence Sets + Difference Array Rebuild per Query (Too Slow for Worst Case)

## Idea

We can keep the same observation:

```text
answer = distinctPrimeCount + max interval overlap
```

But instead of using a segment tree dynamically, we could rebuild the interval-overlap array from scratch after every query:

1. for each prime currently present, add `+1` to its interval `(first, last]`
2. build prefix sums over split positions
3. take the maximum

This is conceptually simple, but too slow in the worst case.

---

## Why it is useful

This approach is still valuable for understanding the interval reduction, even though it is not efficient enough.

---

## Java Sketch

```java
import java.util.*;

class Solution {
    public int[] maximumCount(int[] nums, int[][] queries) {
        int[] ans = new int[queries.length];

        for (int qi = 0; qi < queries.length; qi++) {
            nums[queries[qi][0]] = queries[qi][1];
            ans[qi] = recompute(nums);
        }

        return ans;
    }

    private int recompute(int[] nums) {
        // 1) collect first/last positions of each prime
        // 2) distinctPrimeCount = number of prime values present
        // 3) difference array over split positions
        // 4) prefix max
        return 0;
    }
}
```

---

## Complexity

If we rebuild everything every query, the cost is roughly:

```text
O(q * n)
```

or worse.

With both up to `5 * 10^4`, this is too slow.

---

# Approach 3 — Prefix/Suffix Distinct Prime Recompute per Query (Also Too Slow)

## Idea

For each query, rebuild:

- prefix distinct-prime counts
- suffix distinct-prime counts

Then compute:

```text
max(prefixDistinct[k-1] + suffixDistinct[k])
```

This directly mirrors the problem statement.

---

## Why it fails

That is `O(n)` per query, giving:

```text
O(nq)
```

which is too slow.

Still, it gives the right conceptual baseline.

---

## Java Sketch

```java
import java.util.*;

class Solution {
    public int[] maximumCount(int[] nums, int[][] queries) {
        int n = nums.length;
        int[] ans = new int[queries.length];

        for (int qi = 0; qi < queries.length; qi++) {
            nums[queries[qi][0]] = queries[qi][1];

            int[] pref = new int[n];
            int[] suff = new int[n];
            // recompute distinct prime counts from left and right
            // then try all split points

            ans[qi] = 0;
        }

        return ans;
    }
}
```

---

# Detailed Walkthrough

## Example 1

```text
nums = [2,1,3,1,2]
```

Distinct prime values present are:

```text
{2, 3}
```

So baseline contribution is:

```text
2
```

Now look at prime intervals:

- prime `2` appears at positions `0` and `4`
  - contributes extra `+1` for splits `1..4`
- prime `3` appears only at position `2`
  - no extra interval contribution

So overlap over split points:

```text
split 1 -> 1
split 2 -> 1
split 3 -> 1
split 4 -> 1
```

Maximum overlap is `1`.

So best answer is:

```text
2 + 1 = 3
```

which matches.

After the second update:

```text
nums = [2,2,3,3,2]
```

Intervals:

- `2`: first `0`, last `4` -> covers splits `1..4`
- `3`: first `2`, last `3` -> covers split `3`

At split `3`, overlap is `2`.

Baseline distinct primes is still `2`.

So answer is:

```text
2 + 2 = 4
```

which matches.

---

# Important Correctness Argument

Take any prime value `p`.

For a split `k`, `p` is counted in:

- prefix iff some occurrence is before `k`
- suffix iff some occurrence is at or after `k`

Thus:

- if `p` exists anywhere, it contributes at least `1`
- it contributes a second `1` exactly when the split lies between its first and last occurrence

So each prime’s behavior is completely captured by one interval.

Summing over all prime values gives:

```text
answer(k) = distinctPrimeCount + overlap(k)
```

Thus maximizing over `k` is exactly the same as finding the maximum overlap of these intervals.

That proves the reduction.

---

# Common Pitfalls

## 1. Counting prime occurrences instead of distinct prime values

The problem asks for distinct prime values, not number of prime elements.

---

## 2. Forgetting that updates persist

Each query changes the working array permanently.

---

## 3. Recomputing prefix/suffix arrays from scratch every query

That is too slow.

---

## 4. Missing the interval interpretation

Without the interval view, the dynamic update problem looks much harder than it really is.

---

# Best Approach

## Recommended: Maintain first/last occurrence interval of each prime and dynamic maximum overlap

This is the cleanest efficient solution because:

- target quantity reduces to interval overlap
- each query only affects at most two prime values
- range add + global max is exactly what a lazy segment tree is good at

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class SegTree {
        int n;
        int[] max;
        int[] lazy;

        SegTree(int n) {
            this.n = n;
            this.max = new int[4 * Math.max(1, n)];
            this.lazy = new int[4 * Math.max(1, n)];
        }

        void rangeAdd(int l, int r, int val) {
            if (l > r || n == 0) return;
            rangeAdd(1, 1, n, l, r, val);
        }

        private void rangeAdd(int node, int nl, int nr, int l, int r, int val) {
            if (r < nl || nr < l) return;
            if (l <= nl && nr <= r) {
                max[node] += val;
                lazy[node] += val;
                return;
            }
            push(node);
            int mid = (nl + nr) >>> 1;
            rangeAdd(node << 1, nl, mid, l, r, val);
            rangeAdd(node << 1 | 1, mid + 1, nr, l, r, val);
            max[node] = Math.max(max[node << 1], max[node << 1 | 1]);
        }

        private void push(int node) {
            if (lazy[node] != 0) {
                int v = lazy[node];
                max[node << 1] += v;
                lazy[node << 1] += v;
                max[node << 1 | 1] += v;
                lazy[node << 1 | 1] += v;
                lazy[node] = 0;
            }
        }

        int queryMax() {
            return n == 0 ? 0 : max[1];
        }
    }

    public int[] maximumCount(int[] nums, int[][] queries) {
        int n = nums.length;
        int maxV = 100000;
        boolean[] prime = sieve(maxV);

        @SuppressWarnings("unchecked")
        TreeSet<Integer>[] pos = new TreeSet[maxV + 1];

        int distinctPrimeCount = 0;
        SegTree st = new SegTree(n - 1);

        for (int i = 0; i < n; i++) {
            int v = nums[i];
            if (!prime[v]) continue;
            if (pos[v] == null) pos[v] = new TreeSet<>();
            if (pos[v].isEmpty()) distinctPrimeCount++;
            pos[v].add(i);
        }

        for (int v = 2; v <= maxV; v++) {
            if (!prime[v] || pos[v] == null || pos[v].isEmpty()) continue;
            applyInterval(pos[v], st, +1);
        }

        int[] ans = new int[queries.length];

        for (int qi = 0; qi < queries.length; qi++) {
            int idx = queries[qi][0];
            int newVal = queries[qi][1];
            int oldVal = nums[idx];

            if (oldVal != newVal) {
                if (prime[oldVal]) {
                    TreeSet<Integer> set = pos[oldVal];
                    applyInterval(set, st, -1);
                    set.remove(idx);
                    if (set.isEmpty()) distinctPrimeCount--;
                    else applyInterval(set, st, +1);
                }

                nums[idx] = newVal;

                if (prime[newVal]) {
                    if (pos[newVal] == null) pos[newVal] = new TreeSet<>();
                    TreeSet<Integer> set = pos[newVal];
                    if (!set.isEmpty()) applyInterval(set, st, -1);
                    else distinctPrimeCount++;
                    set.add(idx);
                    applyInterval(set, st, +1);
                }
            }

            ans[qi] = distinctPrimeCount + st.queryMax();
        }

        return ans;
    }

    private void applyInterval(TreeSet<Integer> set, SegTree st, int delta) {
        if (set == null || set.isEmpty()) return;
        int first = set.first();
        int last = set.last();
        if (first < last) {
            st.rangeAdd(first + 1, last, delta);
        }
    }

    private boolean[] sieve(int n) {
        boolean[] prime = new boolean[n + 1];
        if (n >= 2) Arrays.fill(prime, 2, n + 1, true);
        for (int p = 2; p * p <= n; p++) {
            if (!prime[p]) continue;
            for (int x = p * p; x <= n; x += p) {
                prime[x] = false;
            }
        }
        return prime;
    }
}
```

---

# Complexity Summary

```text
Time:  O((n + q) log n)
Space: O(n + V)
```

with `V = 10^5` for primality preprocessing.

This is efficient for the given constraints.

---

# Final Takeaway

The decisive simplification is:

- each distinct prime contributes baseline `1`
- it contributes another `1` exactly on split points between its first and last occurrence

So the problem becomes:

> maintain the maximum overlap of prime intervals under point updates
