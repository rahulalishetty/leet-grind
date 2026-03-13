# 2916. Subarrays Distinct Element Sum of Squares II — Exhaustive Java Notes

## Problem Statement

You are given a 0-indexed integer array `nums`.

For any subarray `nums[i..j]`, its **distinct count** is the number of distinct values present in that subarray.

We need to compute:

```text
sum of (distinct count)^2 over all non-empty subarrays
```

Since the answer can be large, return it modulo:

```text
10^9 + 7
```

---

## Example 1

```text
Input: nums = [1,2,1]
Output: 15
```

All subarrays:

- `[1]` → distinct = 1 → square = 1
- `[2]` → distinct = 1 → square = 1
- `[1]` → distinct = 1 → square = 1
- `[1,2]` → distinct = 2 → square = 4
- `[2,1]` → distinct = 2 → square = 4
- `[1,2,1]` → distinct = 2 → square = 4

Total:

```text
1 + 1 + 1 + 4 + 4 + 4 = 15
```

---

## Example 2

```text
Input: nums = [2,2]
Output: 3
```

All subarrays:

- `[2]` → distinct = 1 → square = 1
- `[2]` → distinct = 1 → square = 1
- `[2,2]` → distinct = 1 → square = 1

Total:

```text
1 + 1 + 1 = 3
```

---

## Constraints

- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^5`

---

# 1. First Principles

Let:

```text
d(l, r) = number of distinct values in nums[l..r]
```

We want:

```text
sum over all l, r of d(l, r)^2
```

The brute-force interpretation is obvious, but `n` goes up to `10^5`, so we need something much more efficient.

The real difficulty is not computing distinct counts alone.
It is computing the **sum of squares** of distinct counts across all subarrays.

That square is what forces a more careful contribution-based or data-structure-based approach.

---

# 2. Approach 1 — Brute Force Over All Subarrays

## Idea

Enumerate every subarray `[l..r]`:

- maintain a set of seen values
- count distinct elements
- add square of that count

## Java Code

```java
import java.util.*;

class SolutionBruteForce {
    public int sumCounts(int[] nums) {
        long MOD = 1_000_000_007L;
        int n = nums.length;
        long ans = 0;

        for (int l = 0; l < n; l++) {
            Set<Integer> set = new HashSet<>();
            for (int r = l; r < n; r++) {
                set.add(nums[r]);
                long d = set.size();
                ans = (ans + d * d) % MOD;
            }
        }

        return (int) ans;
    }
}
```

## Complexity

- Time: `O(n^2)` average
- Space: `O(n)`

## Verdict

This is fine only for small `n`.

For `n = 10^5`, it is impossible.

---

# 3. Approach 2 — DP by Right Endpoint with Explicit Distinct Tracking

## Idea

For each right endpoint `r`, compute distinct counts of all subarrays ending at `r`:

```text
[l..r] for all 0 <= l <= r
```

Then sum their squares.

You can do this by scanning leftward and maintaining a frequency map / set.

## Java Code

```java
import java.util.*;

class SolutionRightEndpointDP {
    public int sumCounts(int[] nums) {
        long MOD = 1_000_000_007L;
        int n = nums.length;
        long ans = 0;

        for (int r = 0; r < n; r++) {
            Set<Integer> seen = new HashSet<>();
            for (int l = r; l >= 0; l--) {
                seen.add(nums[l]);
                long d = seen.size();
                ans = (ans + d * d) % MOD;
            }
        }

        return (int) ans;
    }
}
```

## Complexity

- Time: `O(n^2)` average
- Space: `O(n)`

## Verdict

This is conceptually useful because it frames the problem by growing subarrays ending at each index.

Still too slow for the actual constraint.

---

# 4. Key Mathematical Insight for the Optimal Solution

Suppose we process the array from left to right and append `nums[i]` as the new right endpoint.

Let:

```text
f_i(l) = distinct count of subarray nums[l..i]
```

Then we want to add:

```text
sum over l of f_i(l)^2
```

Now ask:

> When we append `nums[i]`, for which starting positions `l` does the distinct count increase by 1?

If the previous occurrence of `nums[i]` is at position `prev`, then:

- for all `l > prev`, the value `nums[i]` is **new** inside `[l..i]`
- for all `l <= prev`, the value already existed in `[l..i-1]`, so distinct count does **not** increase

So only subarrays starting in:

```text
[prev + 1, i]
```

gain `+1` distinct count.

That is the core interval update.

---

## 4.1 What happens to the square?

If a subarray previously had distinct count `x`, and now it becomes `x + 1`, then:

```text
(x + 1)^2 - x^2 = 2x + 1
```

So when processing index `i`, for every start `l` in `[prev+1, i]` we must add:

```text
2 * currentDistinctCount(l) + 1
```

This means we need a structure that can do two things over that interval:

1. know the current sum of distinct counts on that interval
2. increment all those distinct counts by 1

That immediately suggests a lazy segment tree.

---

# 5. Approach 3 — Lazy Segment Tree Over Starting Positions (Optimal)

This is the standard optimal solution.

## Main viewpoint

For each fixed right endpoint `i`, consider all subarrays ending at `i`:

```text
[0..i], [1..i], [2..i], ..., [i..i]
```

We maintain an array over starting positions `l`:

```text
cnt[l] = distinct count of subarray [l..currentRight]
```

When we append `nums[i]`:

- let `prev` be the previous occurrence of `nums[i]`, or `-1` if none
- for all `l in [prev+1, i]`, `cnt[l] += 1`

Then the contribution of all subarrays ending at `i` is:

```text
sum over l=0..i of cnt[l]^2
```

So the segment tree maintains:

- `sum` = sum of `cnt[l]`
- `sq` = sum of `cnt[l]^2`
- lazy increment tag

for each segment.

---

## 5.1 Why maintaining both `sum` and `sq` is enough

Suppose we add `+1` to every value in a segment of length `len`.

If current values are `x1, x2, ..., xlen`, then:

```text
new sum = old sum + len
```

and

```text
new sq = sum((xk + 1)^2)
       = sum(xk^2 + 2xk + 1)
       = old sq + 2 * old sum + len
```

More generally, if we add `delta` to every value:

```text
new sum = old sum + delta * len
new sq  = old sq + 2 * delta * old sum + delta^2 * len
```

This is exactly what makes lazy propagation work cleanly.

---

## 5.2 Segment tree node meaning

For each segment `[L..R]`, store:

- `sum` = sum of current distinct counts on this range of starts
- `sq` = sum of squares of current distinct counts on this range of starts
- `lazy` = pending increment to all counts in this range

When processing right endpoint `i`:

1. update range `[prev+1, i]` by `+1`
2. query `sq` on range `[0, i]`
3. add that to global answer

Because range `[0, i]` corresponds exactly to all valid starting points for subarrays ending at `i`.

---

## 5.3 Java Code — Lazy Segment Tree

```java
import java.util.*;

class Solution {
    static class SegmentTree {
        int n;
        long[] sum;
        long[] sq;
        long[] lazy;
        static final long MOD = 1_000_000_007L;

        SegmentTree(int n) {
            this.n = n;
            this.sum = new long[4 * n];
            this.sq = new long[4 * n];
            this.lazy = new long[4 * n];
        }

        void apply(int node, int left, int right, long delta) {
            long len = right - left + 1;
            long oldSum = sum[node];

            sq[node] = (sq[node] + 2L * delta % MOD * oldSum % MOD + delta * delta % MOD * len) % MOD;
            sum[node] = (sum[node] + delta * len) % MOD;
            lazy[node] = (lazy[node] + delta) % MOD;
        }

        void push(int node, int left, int right) {
            if (lazy[node] == 0 || left == right) return;

            int mid = left + (right - left) / 2;
            apply(node * 2, left, mid, lazy[node]);
            apply(node * 2 + 1, mid + 1, right, lazy[node]);
            lazy[node] = 0;
        }

        void update(int node, int left, int right, int ql, int qr, long delta) {
            if (ql > right || qr < left) return;

            if (ql <= left && right <= qr) {
                apply(node, left, right, delta);
                return;
            }

            push(node, left, right);

            int mid = left + (right - left) / 2;
            update(node * 2, left, mid, ql, qr, delta);
            update(node * 2 + 1, mid + 1, right, ql, qr, delta);

            sum[node] = (sum[node * 2] + sum[node * 2 + 1]) % MOD;
            sq[node] = (sq[node * 2] + sq[node * 2 + 1]) % MOD;
        }

        long querySq(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return 0;

            if (ql <= left && right <= qr) {
                return sq[node];
            }

            push(node, left, right);

            int mid = left + (right - left) / 2;
            return (querySq(node * 2, left, mid, ql, qr)
                  + querySq(node * 2 + 1, mid + 1, right, ql, qr)) % MOD;
        }

        void update(int l, int r, long delta) {
            if (l > r) return;
            update(1, 0, n - 1, l, r, delta);
        }

        long querySq(int l, int r) {
            if (l > r) return 0;
            return querySq(1, 0, n - 1, l, r);
        }
    }

    public int sumCounts(int[] nums) {
        int n = nums.length;
        SegmentTree st = new SegmentTree(n);
        Map<Integer, Integer> lastPos = new HashMap<>();
        long ans = 0;
        long MOD = 1_000_000_007L;

        for (int i = 0; i < n; i++) {
            int prev = lastPos.getOrDefault(nums[i], -1);

            st.update(prev + 1, i, 1);

            ans = (ans + st.querySq(0, i)) % MOD;

            lastPos.put(nums[i], i);
        }

        return (int) ans;
    }
}
```

---

## 5.4 Complexity

- Each index performs one range update and one range query
- Each is `O(log n)`

Overall:

- Time: `O(n log n)`
- Space: `O(n)`

This easily fits `n <= 10^5`.

---

# 6. Dry Run of the Optimal Solution

Take:

```text
nums = [1, 2, 1]
```

We process each right endpoint.

Initially all `cnt[l] = 0`.

---

## i = 0, value = 1

Previous occurrence of `1` is `-1`.

So update interval:

```text
[0, 0] += 1
```

Now:

```text
cnt = [1, 0, 0]
```

Relevant starts for subarrays ending at `0` are only `[0]`.

Squares:

```text
1^2 = 1
```

Contribution added = `1`.

Total so far = `1`.

---

## i = 1, value = 2

Previous occurrence of `2` is `-1`.

So update interval:

```text
[0, 1] += 1
```

Now:

- previous `cnt[0] = 1`, becomes `2`
- previous `cnt[1] = 0`, becomes `1`

So:

```text
cnt = [2, 1, 0]
```

These correspond to subarrays ending at `1`:

- `[0..1] = [1,2]` → distinct = 2
- `[1..1] = [2]` → distinct = 1

Squares:

```text
2^2 + 1^2 = 4 + 1 = 5
```

Contribution added = `5`.

Total so far = `6`.

---

## i = 2, value = 1

Previous occurrence of `1` is `0`.

So update interval:

```text
[1, 2] += 1
```

Why not `[0,2]`?
Because subarrays starting at `0` already contained `1`.

Before update:

```text
cnt = [2, 1, 0]
```

After update:

```text
cnt = [2, 2, 1]
```

These correspond to subarrays ending at `2`:

- `[0..2] = [1,2,1]` → distinct = 2
- `[1..2] = [2,1]` → distinct = 2
- `[2..2] = [1]` → distinct = 1

Squares:

```text
2^2 + 2^2 + 1^2 = 4 + 4 + 1 = 9
```

Contribution added = `9`.

Final total:

```text
1 + 5 + 9 = 15
```

Correct.

---

# 7. Why the Range Update Interval Is `[prev+1, i]`

This is the crucial step.

When processing `nums[i] = x`, ask for which starts `l` the new subarray `[l..i]` gains one new distinct value compared with `[l..i-1]`.

That happens exactly when `x` did **not** already appear inside `[l..i-1]`.

If the previous occurrence of `x` is `prev`, then:

- if `l <= prev`, the subarray `[l..i-1]` already contains `x`
- if `l > prev`, the subarray `[l..i-1]` does not contain `x`

Therefore the increment applies exactly to:

```text
l in [prev+1, i]
```

No other starts are affected.

---

# 8. Alternative View: Contribution Formula

Another useful algebraic way to look at it:

If distinct count for a subarray increases from `d` to `d + 1`, then square increases by:

```text
(d + 1)^2 - d^2 = 2d + 1
```

So when processing `nums[i]`, over the interval `[prev+1, i]` we need to add:

- `1` for each affected subarray
- plus `2 * currentDistinctCount`

This is exactly why the segment tree must know the range sum of current counts, not just the counts individually.

A skeptical question is:

> Could a Fenwick tree be enough?

Not easily, because we need both:

- range increment on counts
- sum of squares across ranges

A plain BIT handles linear aggregates much better than quadratic ones.
The segment tree is natural here because it can track both first moment (`sum`) and second moment (`sq`) under lazy range increments.

---

# 9. Approach 4 — Coordinate Compression + Segment Tree Variant

The values in `nums` are already bounded by `10^5`, so a `HashMap` for last occurrence is enough.

But conceptually, if values were huge, we could coordinate-compress them first and still use the exact same logic.

The algorithmic structure remains unchanged:

1. Track `last occurrence`
2. Range update `[prev+1, i]`
3. Accumulate square sum on `[0, i]`

So this is not a fundamentally different algorithm, just an implementation variant for broader value ranges.

---

# 10. Correctness Proof of the Optimal Algorithm

We now prove the segment tree method carefully.

## Definition

At step `i`, define `cnt_i(l)` for `0 <= l <= i` as the distinct count of subarray:

```text
nums[l..i]
```

The algorithm maintains these values implicitly in the segment tree.

---

## Lemma 1

When processing index `i`, `cnt_i(l) = cnt_{i-1}(l) + 1` exactly for `l in [prev+1, i]`, where `prev` is the previous occurrence of `nums[i]`.

### Proof

A subarray `[l..i]` gains one new distinct value iff `nums[i]` was absent from `[l..i-1]`. Since the latest previous occurrence is `prev`, this absence is equivalent to `l > prev`. Also `l <= i` must hold for the subarray to exist. Therefore exactly `l in [prev+1, i]` are incremented. ∎

---

## Lemma 2

After applying the range update `[prev+1, i] += 1`, the segment tree stores exactly `cnt_i(l)` for every start position `l`.

### Proof

By induction on `i`.

- Base case `i = 0`: previous occurrence is `-1`, so range `[0,0]` gets `+1`. Thus `cnt_0(0)=1`, correct.
- Inductive step: assuming tree stores `cnt_{i-1}(l)`, by Lemma 1 the exact required changes are to add 1 on `[prev+1, i]`. Therefore after the update the tree stores `cnt_i(l)` exactly. ∎

---

## Lemma 3

The segment tree’s `sq` value over range `[0, i]` equals:

```text
sum_{l=0..i} cnt_i(l)^2
```

### Proof

Each leaf corresponds to one start position `l` and stores `cnt_i(l)` and `cnt_i(l)^2`. Internal nodes store sums of children, so the queried square sum is exactly the sum of squares over the interval. ∎

---

## Theorem

The algorithm returns the sum of squares of distinct counts of all subarrays.

### Proof

For each right endpoint `i`, by Lemma 3 the algorithm adds exactly:

```text
sum_{l=0..i} cnt_i(l)^2
```

which is the sum of squares of distinct counts of all subarrays ending at `i`. Summing over all `i` covers every subarray exactly once. Therefore the final answer is correct. ∎

---

# 11. Common Mistakes

## Mistake 1: Only tracking whether current value is new globally

That is not enough. A value may be new for some subarrays ending at `i` and not new for others.

## Mistake 2: Updating `[0, i]` instead of `[prev+1, i]`

This overcounts subarrays that already contained the value.

## Mistake 3: Thinking only distinct counts are enough

We need **sum of squares**, so we must maintain second moments too.

## Mistake 4: Forgetting modulo during lazy updates

Since the tree values grow large, all updates should be done modulo `10^9+7`.

## Mistake 5: Using `int` internally

Use `long` for tree math before mod reduction.

---

# 12. Comparison of Approaches

| Approach            | Idea                                                                   |         Time |  Space | Notes                  |
| ------------------- | ---------------------------------------------------------------------- | -----------: | -----: | ---------------------- |
| Brute force         | Enumerate all subarrays, count distinct with set                       | `O(n^2)` avg | `O(n)` | Too slow for `10^5`    |
| Right-endpoint scan | For each `r`, scan left and count distinct                             | `O(n^2)` avg | `O(n)` | Good for intuition     |
| Lazy segment tree   | Maintain distinct counts of subarrays ending at current right endpoint | `O(n log n)` | `O(n)` | Optimal and standard   |
| Compression variant | Same as above with compressed values                                   | `O(n log n)` | `O(n)` | Implementation variant |

---

# 13. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class SegmentTree {
        int n;
        long[] sum;
        long[] sq;
        long[] lazy;
        static final long MOD = 1_000_000_007L;

        SegmentTree(int n) {
            this.n = n;
            this.sum = new long[4 * n];
            this.sq = new long[4 * n];
            this.lazy = new long[4 * n];
        }

        void apply(int node, int left, int right, long delta) {
            long len = right - left + 1;
            long oldSum = sum[node];

            sq[node] = (sq[node]
                    + 2L * delta % MOD * oldSum % MOD
                    + delta * delta % MOD * len) % MOD;

            sum[node] = (sum[node] + delta * len) % MOD;
            lazy[node] = (lazy[node] + delta) % MOD;
        }

        void push(int node, int left, int right) {
            if (lazy[node] == 0 || left == right) return;

            int mid = left + (right - left) / 2;
            apply(node * 2, left, mid, lazy[node]);
            apply(node * 2 + 1, mid + 1, right, lazy[node]);
            lazy[node] = 0;
        }

        void update(int node, int left, int right, int ql, int qr, long delta) {
            if (ql > right || qr < left) return;

            if (ql <= left && right <= qr) {
                apply(node, left, right, delta);
                return;
            }

            push(node, left, right);

            int mid = left + (right - left) / 2;
            update(node * 2, left, mid, ql, qr, delta);
            update(node * 2 + 1, mid + 1, right, ql, qr, delta);

            sum[node] = (sum[node * 2] + sum[node * 2 + 1]) % MOD;
            sq[node] = (sq[node * 2] + sq[node * 2 + 1]) % MOD;
        }

        long querySq(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return 0;

            if (ql <= left && right <= qr) {
                return sq[node];
            }

            push(node, left, right);

            int mid = left + (right - left) / 2;
            return (querySq(node * 2, left, mid, ql, qr)
                  + querySq(node * 2 + 1, mid + 1, right, ql, qr)) % MOD;
        }

        void update(int l, int r, long delta) {
            if (l > r) return;
            update(1, 0, n - 1, l, r, delta);
        }

        long querySq(int l, int r) {
            if (l > r) return 0;
            return querySq(1, 0, n - 1, l, r);
        }
    }

    public int sumCounts(int[] nums) {
        int n = nums.length;
        SegmentTree st = new SegmentTree(n);
        Map<Integer, Integer> lastPos = new HashMap<>();
        long ans = 0;
        long MOD = 1_000_000_007L;

        for (int i = 0; i < n; i++) {
            int prev = lastPos.getOrDefault(nums[i], -1);

            st.update(prev + 1, i, 1);

            ans = (ans + st.querySq(0, i)) % MOD;

            lastPos.put(nums[i], i);
        }

        return (int) ans;
    }
}
```

---

# 14. Interview Summary

The decisive observation is:

- when appending `nums[i]`, only subarrays starting after the previous occurrence of `nums[i]` gain one new distinct element

So for each right endpoint `i`, distinct counts of subarrays ending at `i` receive a range increment on:

```text
[prev + 1, i]
```

Because the answer needs **squares** of distinct counts, the segment tree must maintain both:

- sum of counts
- sum of squares of counts

under lazy range increments.

That yields an `O(n log n)` solution.
