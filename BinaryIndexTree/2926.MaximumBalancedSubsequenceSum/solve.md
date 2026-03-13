# 2926. Maximum Balanced Subsequence Sum — Exhaustive Java Notes

## Problem Statement

You are given a 0-indexed integer array `nums`.

A subsequence with indices:

```text
i0 < i1 < ... < ik-1
```

is **balanced** if for every adjacent pair in the subsequence:

```text
nums[ij] - nums[ij-1] >= ij - ij-1
```

A subsequence of length `1` is always balanced.

Return the **maximum possible sum** of elements in a balanced subsequence.

---

## Example 1

```text
Input: nums = [3,3,5,6]
Output: 14
```

One optimal balanced subsequence is:

```text
[3,5,6]
```

using indices `(0,2,3)`.

Check:

- `5 - 3 >= 2 - 0`
- `6 - 5 >= 3 - 2`

So it is balanced, and the sum is:

```text
3 + 5 + 6 = 14
```

---

## Example 2

```text
Input: nums = [5,-1,-3,8]
Output: 13
```

One optimal balanced subsequence is:

```text
[5,8]
```

using indices `(0,3)`.

Check:

```text
8 - 5 >= 3 - 0
```

So it is balanced, and the sum is:

```text
5 + 8 = 13
```

---

## Example 3

```text
Input: nums = [-2,-1]
Output: -1
```

The best balanced subsequence is just:

```text
[-1]
```

Any single-element subsequence is balanced.

---

## Constraints

- `1 <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`

---

# 1. Key Algebraic Insight

The condition for adjacent chosen indices `p < i` is:

```text
nums[i] - nums[p] >= i - p
```

Rearrange it:

```text
nums[i] - i >= nums[p] - p
```

This is the decisive simplification.

Define:

```text
key[i] = nums[i] - i
```

Then a subsequence is balanced exactly when the sequence of chosen `key` values is **non-decreasing**.

So the problem becomes:

> Pick a subsequence with non-decreasing `key[i] = nums[i] - i`, maximizing the sum of `nums[i]`.

That turns the problem into a weighted LIS-style DP.

---

# 2. DP Formulation

Let:

```text
dp[i] = maximum sum of a balanced subsequence ending at index i
```

Then:

```text
dp[i] = nums[i]
```

if we start a new subsequence at `i`, or

```text
dp[i] = nums[i] + max(dp[p]) over all p < i with key[p] <= key[i]
```

So the transition is:

```text
dp[i] = nums[i] + bestPrefix(key[i])
```

where `bestPrefix(x)` means the maximum `dp` among previously processed indices with key `<= x`.

This is exactly a prefix maximum query over keys.

---

# 3. Approach 1 — Brute Force Recursion / Exhaustive Subsequences

## Idea

Enumerate all subsequences, test whether each is balanced, and track the best sum.

## Why it works

It checks every possible subsequence.

## Why it is useless here

There are `2^n - 1` non-empty subsequences. This explodes immediately.

## Java Sketch

```java
class SolutionBruteForce {
    long ans = Long.MIN_VALUE;

    public long maxBalancedSubsequenceSum(int[] nums) {
        dfs(nums, 0, Long.MIN_VALUE, 0L, false);
        return ans;
    }

    private void dfs(int[] nums, int index, long lastKey, long sum, boolean takenAny) {
        if (index == nums.length) {
            if (takenAny) ans = Math.max(ans, sum);
            return;
        }

        // Skip
        dfs(nums, index + 1, lastKey, sum, takenAny);

        long key = (long) nums[index] - index;
        if (!takenAny || key >= lastKey) {
            dfs(nums, index + 1, key, sum + nums[index], true);
        }
    }
}
```

## Complexity

- Time: `O(2^n)`
- Space: `O(n)` recursion

## Verdict

Only useful to understand the structure.

---

# 4. Approach 2 — Quadratic DP

## Idea

Use the DP directly.

For every index `i`, scan all previous indices `p < i` and take the best valid transition:

```text
if key[p] <= key[i]:
    dp[i] = max(dp[i], dp[p] + nums[i])
```

## Java Code

```java
class SolutionQuadraticDP {
    public long maxBalancedSubsequenceSum(int[] nums) {
        int n = nums.length;
        long[] dp = new long[n];
        long ans = Long.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            dp[i] = nums[i]; // start new subsequence
            long keyI = (long) nums[i] - i;

            for (int p = 0; p < i; p++) {
                long keyP = (long) nums[p] - p;
                if (keyP <= keyI) {
                    dp[i] = Math.max(dp[i], dp[p] + nums[i]);
                }
            }

            ans = Math.max(ans, dp[i]);
        }

        return ans;
    }
}
```

## Complexity

- Time: `O(n^2)`
- Space: `O(n)`

## Verdict

Correct, simple, and great for deriving the recurrence.

But `n = 10^5`, so this will time out.

---

# 5. Approach 3 — Coordinate Compression + Fenwick Tree for Prefix Maximum (Optimal)

This is the standard best solution.

## Why a Fenwick Tree works

The DP transition needs:

```text
max dp[p] for all previous p with key[p] <= key[i]
```

That is a prefix maximum query over the ordered values of `key`.

But `key[i] = nums[i] - i` can be as small as about `-10^9 - 10^5` and as large as about `10^9`, so we cannot index directly.

So we:

1. compute all `key[i]`
2. coordinate-compress them
3. use a Fenwick Tree that stores **prefix maximums**

Then for each `i`:

- query the best previous `dp` on compressed keys `<= key[i]`
- compute:
  ```text
  dp[i] = nums[i] + max(0, bestPrevious)
  ```
- update the Fenwick Tree at `key[i]` with `dp[i]`

The `max(0, bestPrevious)` part matters because starting fresh at `i` is always allowed.

---

## 5.1 Why `max(0, bestPrevious)` is correct

Suppose the best previous balanced subsequence ending with allowable key has negative sum.

Appending it would only hurt us.

Since a single-element subsequence is always balanced, we can always choose to start at `i`.

So:

```text
dp[i] = nums[i] + max(0, bestPrevious)
```

This elegantly handles negative values.

---

## 5.2 Fenwick Tree for Maximum

A Fenwick Tree is often shown for sums, but it can also be used for maximum if updates are monotonic:

- `update(pos, value)` means set relevant Fenwick nodes to `max(existing, value)`
- `query(pos)` returns maximum in prefix `[1..pos]`

That is exactly what we need.

---

## 5.3 Java Code — Fenwick Tree Solution

```java
import java.util.*;

class Solution {
    static class FenwickMax {
        int n;
        long[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new long[n + 1];
            Arrays.fill(this.bit, Long.MIN_VALUE);
        }

        void update(int index, long value) {
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        long query(int index) {
            long ans = Long.MIN_VALUE;
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public long maxBalancedSubsequenceSum(int[] nums) {
        int n = nums.length;
        long[] keys = new long[n];
        for (int i = 0; i < n; i++) {
            keys[i] = (long) nums[i] - i;
        }

        long[] sorted = keys.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (long x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }

        long[] unique = Arrays.copyOf(sorted, m);

        FenwickMax bit = new FenwickMax(m);
        long ans = Long.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            int rank = lowerBound(unique, keys[i]) + 1; // 1-based
            long bestPrev = bit.query(rank);

            long dp = nums[i];
            if (bestPrev != Long.MIN_VALUE) {
                dp = Math.max(dp, nums[i] + Math.max(0L, bestPrev));
            }

            bit.update(rank, dp);
            ans = Math.max(ans, dp);
        }

        return ans;
    }

    private int lowerBound(long[] arr, long target) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}
```

---

## 5.4 Complexity

- Compute keys: `O(n)`
- Coordinate compression: `O(n log n)`
- Each Fenwick query/update: `O(log n)`

Overall:

- Time: `O(n log n)`
- Space: `O(n)`

This comfortably fits the constraints.

---

# 6. Approach 4 — Coordinate Compression + Segment Tree

A segment tree can do the same job.

## Idea

Maintain a segment tree over compressed `key` values.

At step `i`:

- query range max on `[1 .. rank(key[i])]`
- compute `dp[i]`
- point-update at `rank(key[i])` with `dp[i]`

This is slightly more verbose than Fenwick, but equally valid.

---

## Java Code — Segment Tree

```java
import java.util.*;

class SolutionSegmentTree {
    static class SegmentTree {
        int n;
        long[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new long[4 * n];
            Arrays.fill(this.tree, Long.MIN_VALUE);
        }

        void update(int node, int left, int right, int index, long value) {
            if (left == right) {
                tree[node] = Math.max(tree[node], value);
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index, value);
            } else {
                update(node * 2 + 1, mid + 1, right, index, value);
            }

            tree[node] = Math.max(tree[node * 2], tree[node * 2 + 1]);
        }

        long query(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return Long.MIN_VALUE;
            if (ql <= left && right <= qr) return tree[node];

            int mid = left + (right - left) / 2;
            return Math.max(
                query(node * 2, left, mid, ql, qr),
                query(node * 2 + 1, mid + 1, right, ql, qr)
            );
        }

        void update(int index, long value) {
            update(1, 1, n, index, value);
        }

        long query(int l, int r) {
            if (l > r) return Long.MIN_VALUE;
            return query(1, 1, n, l, r);
        }
    }

    public long maxBalancedSubsequenceSum(int[] nums) {
        int n = nums.length;
        long[] keys = new long[n];
        for (int i = 0; i < n; i++) {
            keys[i] = (long) nums[i] - i;
        }

        long[] sorted = keys.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (long x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }

        long[] unique = Arrays.copyOf(sorted, m);
        SegmentTree seg = new SegmentTree(m);

        long ans = Long.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            int rank = lowerBound(unique, keys[i]) + 1;
            long bestPrev = seg.query(1, rank);

            long dp = nums[i];
            if (bestPrev != Long.MIN_VALUE) {
                dp = Math.max(dp, nums[i] + Math.max(0L, bestPrev));
            }

            seg.update(rank, dp);
            ans = Math.max(ans, dp);
        }

        return ans;
    }

    private int lowerBound(long[] arr, long target) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] >= target) right = mid;
            else left = mid + 1;
        }
        return left;
    }
}
```

---

## Complexity

- Time: `O(n log n)`
- Space: `O(n)`

---

# 7. Dry Run of the Optimal Idea

Take:

```text
nums = [3, 3, 5, 6]
```

Compute:

```text
key[i] = nums[i] - i
```

So:

- `key[0] = 3 - 0 = 3`
- `key[1] = 3 - 1 = 2`
- `key[2] = 5 - 2 = 3`
- `key[3] = 6 - 3 = 3`

Thus:

```text
keys = [3, 2, 3, 3]
```

A balanced subsequence is exactly a subsequence with non-decreasing keys.

Now compute `dp`:

## i = 0

- key = 3
- best previous with key <= 3: none
- `dp[0] = 3`

## i = 1

- key = 2
- best previous with key <= 2: none
- `dp[1] = 3`

## i = 2

- key = 3
- best previous with key <= 3: max of `dp[0], dp[1]` = 3
- `dp[2] = 5 + 3 = 8`

## i = 3

- key = 3
- best previous with key <= 3: max of previous dp = 8
- `dp[3] = 6 + 8 = 14`

Answer = `14`.

This matches the example.

---

# 8. Why the Transformation Works

This is the conceptual heart of the problem.

Original condition between consecutive picked indices `p < i`:

```text
nums[i] - nums[p] >= i - p
```

Move terms:

```text
nums[i] - i >= nums[p] - p
```

So every allowed transition depends only on comparing the transformed values:

```text
key[x] = nums[x] - x
```

This means:

- original subsequence must preserve index order
- transformed `key` values must be non-decreasing

So the problem is really:

> Maximum-sum subsequence under a non-decreasing transformed-key constraint.

That is why a weighted LIS-style DP appears naturally.

---

# 9. Correctness Proof

## Lemma 1

A subsequence `i0 < i1 < ... < ik-1` is balanced if and only if:

```text
key[i0] <= key[i1] <= ... <= key[ik-1]
```

where `key[i] = nums[i] - i`.

### Proof

For adjacent chosen indices `a < b`, the balanced condition is:

```text
nums[b] - nums[a] >= b - a
```

which rearranges to:

```text
nums[b] - b >= nums[a] - a
```

that is:

```text
key[b] >= key[a]
```

Applying this to every adjacent pair gives exactly non-decreasing keys. ∎

---

## Lemma 2

`dp[i]` defined as the maximum balanced subsequence sum ending at index `i` satisfies:

```text
dp[i] = nums[i] + max(0, max(dp[p] for p < i and key[p] <= key[i]))
```

### Proof

Any balanced subsequence ending at `i` either:

- consists only of `nums[i]`, sum = `nums[i]`, or
- extends a balanced subsequence ending at some `p < i` such that `key[p] <= key[i]`

By Lemma 1, that is exactly the valid extension condition. Taking the best such previous subsequence, or starting fresh, yields the recurrence. ∎

---

## Lemma 3

The Fenwick/segment tree query at step `i` returns exactly:

```text
max(dp[p]) for p < i and key[p] <= key[i]
```

### Proof

All previous indices have already updated their `dp` values at their compressed key positions. A prefix maximum query up to `rank(key[i])` therefore returns the maximum `dp` among all previous indices with key not exceeding `key[i]`. ∎

---

## Theorem

The Fenwick/segment-tree algorithm returns the maximum possible balanced subsequence sum.

### Proof

By Lemma 2, the recurrence for `dp[i]` is correct. By Lemma 3, the data structure computes each transition correctly. Therefore each `dp[i]` is correct. The answer is the maximum over all `dp[i]`, since every balanced subsequence must end somewhere. ∎

---

# 10. Common Mistakes

## Mistake 1: Missing the transformation

If you keep staring at:

```text
nums[i] - nums[p] >= i - p
```

without rearranging it, the problem looks much harder than it is.

Always rewrite it as:

```text
nums[i] - i >= nums[p] - p
```

## Mistake 2: Thinking this is a normal LIS on `nums`

It is not. The ordering condition is on `nums[i] - i`, not just on `nums[i]`.

## Mistake 3: Forgetting that starting fresh is allowed

Even if previous best is negative, a single-element subsequence is valid.

So use:

```text
nums[i] + max(0, bestPrev)
```

not just `nums[i] + bestPrev`.

## Mistake 4: Using `int`

The sum can be large:

- `n` up to `10^5`
- `nums[i]` up to `10^9`

So use `long`.

## Mistake 5: Trying to index keys directly

`nums[i] - i` can be large negative or positive. Compress them first.

---

# 11. Comparison of Approaches

| Approach                   |         Time |  Space | Notes                        |
| -------------------------- | -----------: | -----: | ---------------------------- |
| Exhaustive subsequences    |     `O(2^n)` | `O(n)` | Purely conceptual            |
| Quadratic DP               |     `O(n^2)` | `O(n)` | Good for deriving recurrence |
| Fenwick + compression      | `O(n log n)` | `O(n)` | Best practical solution      |
| Segment tree + compression | `O(n log n)` | `O(n)` | Equally valid, more verbose  |

---

# 12. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class FenwickMax {
        int n;
        long[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new long[n + 1];
            Arrays.fill(this.bit, Long.MIN_VALUE);
        }

        void update(int index, long value) {
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        long query(int index) {
            long ans = Long.MIN_VALUE;
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public long maxBalancedSubsequenceSum(int[] nums) {
        int n = nums.length;

        long[] keys = new long[n];
        for (int i = 0; i < n; i++) {
            keys[i] = (long) nums[i] - i;
        }

        long[] sorted = keys.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (long x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }

        long[] unique = Arrays.copyOf(sorted, m);
        FenwickMax bit = new FenwickMax(m);

        long ans = Long.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            int rank = lowerBound(unique, keys[i]) + 1; // 1-based
            long bestPrev = bit.query(rank);

            long dp = nums[i];
            if (bestPrev != Long.MIN_VALUE) {
                dp = Math.max(dp, nums[i] + Math.max(0L, bestPrev));
            }

            bit.update(rank, dp);
            ans = Math.max(ans, dp);
        }

        return ans;
    }

    private int lowerBound(long[] arr, long target) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}
```

---

# 13. Interview Summary

The balancing condition:

```text
nums[i] - nums[p] >= i - p
```

rearranges to:

```text
nums[i] - i >= nums[p] - p
```

So a balanced subsequence is exactly one whose transformed values `nums[i] - i` are non-decreasing.

That turns the problem into a weighted LIS-style DP:

```text
dp[i] = nums[i] + max(0, best previous dp with key <= current key)
```

Use coordinate compression on `nums[i] - i` and a Fenwick Tree for prefix maximum queries to get an `O(n log n)` solution.
