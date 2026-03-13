# 3520. Minimum Threshold for Inversion Pairs Count — Exhaustive Java Notes

## Problem Statement

You are given an integer array `nums` and an integer `k`.

An inversion pair with threshold `x` is a pair of indices `(i, j)` such that:

- `i < j`
- `nums[i] > nums[j]`
- `nums[i] - nums[j] <= x`

We need to find the **minimum threshold** `x` such that the number of such pairs is at least `k`.

If no such threshold exists, return `-1`.

---

## Example 1

```text
nums = [1,2,3,4,3,2,1], k = 7
answer = 2
```

For `x = 2`, there are at least `7` valid inversion pairs.

---

## Example 2

```text
nums = [10,9,9,9,1], k = 4
answer = 8
```

For `x = 8`, there are at least `4` valid inversion pairs.

---

## Constraints

```text
1 <= nums.length <= 10^4
1 <= nums[i] <= 10^9
1 <= k <= 10^9
```

---

# 1. Core Observation

Define:

```text
count(x) = number of inversion pairs (i, j) such that
           i < j, nums[i] > nums[j], and nums[i] - nums[j] <= x
```

As `x` grows, the condition becomes easier to satisfy.

So `count(x)` is **monotone non-decreasing**.

That immediately suggests:

- binary search on the answer `x`
- plus a method to compute `count(x)`

So the real challenge is:

> given a threshold `x`, how do we count the number of valid inversion pairs efficiently?

---

# 2. Rewriting the Condition

We need pairs with:

```text
nums[i] > nums[j]
nums[i] - nums[j] <= x
```

Rearrange the second inequality:

```text
nums[i] <= nums[j] + x
```

Combine both:

```text
nums[j] < nums[i] <= nums[j] + x
```

So for every `j`, we want to count earlier values `nums[i]` that lie in:

```text
(nums[j], nums[j] + x]
```

That is a **range counting** problem.

---

# 3. Approach 1 — Brute Force Check for Every Threshold

## Idea

Try thresholds and directly count all valid pairs using nested loops.

## Java Code

```java
class SolutionBruteForce {
    public int minThreshold(int[] nums, int k) {
        int n = nums.length;
        long totalInversions = 0;
        int maxDiff = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j]) {
                    totalInversions++;
                    maxDiff = Math.max(maxDiff, nums[i] - nums[j]);
                }
            }
        }

        if (totalInversions < k) return -1;

        for (int x = 0; x <= maxDiff; x++) {
            long count = 0;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (nums[i] > nums[j] && nums[i] - nums[j] <= x) {
                        count++;
                    }
                }
            }
            if (count >= k) return x;
        }

        return -1;
    }
}
```

## Complexity

- Counting one threshold: `O(n^2)`
- Trying many thresholds: even worse

This is only useful for intuition.

---

# 4. Approach 2 — Binary Search + Brute Force Count

## Idea

Since `count(x)` is monotone, binary search on `x`.

For each `x`, count pairs by checking all `(i, j)`.

## Java Code

```java
class SolutionBinaryBruteForce {
    public int minThreshold(int[] nums, int k) {
        int n = nums.length;
        long totalInversions = 0;
        int hi = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j]) {
                    totalInversions++;
                    hi = Math.max(hi, nums[i] - nums[j]);
                }
            }
        }

        if (totalInversions < k) return -1;

        int lo = 0, ans = hi;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            long cnt = countPairs(nums, mid);

            if (cnt >= k) {
                ans = mid;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        return ans;
    }

    private long countPairs(int[] nums, int x) {
        int n = nums.length;
        long cnt = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j] && nums[i] - nums[j] <= x) {
                    cnt++;
                }
            }
        }

        return cnt;
    }
}
```

## Complexity

- Binary search: about `O(log V)`
- Each count: `O(n^2)`

So total:

```text
O(n^2 log V)
```

Still too slow in spirit, though `n = 10^4` makes it borderline only for tiny constants and not ideal.

We need better counting.

---

# 5. Approach 3 — Binary Search + Fenwick Tree Range Counting

This is the best practical solution.

## Key idea

For a fixed threshold `x`, process `nums` from left to right.

At index `j`, we want to know how many earlier `nums[i]` satisfy:

```text
nums[j] < nums[i] <= nums[j] + x
```

So among previously seen numbers, count how many fall in that value range.

That is exactly what a Fenwick Tree can do after coordinate compression.

---

# 6. Coordinate Compression

Values are up to `10^9`, so we cannot build a BIT directly on values.

We compress all unique values from `nums`:

```text
sortedUnique = sorted distinct nums values
```

Then each `nums[i]` gets an index in this sorted array.

To count numbers in a value range:

```text
(L, R]
```

we binary search the compressed array to find:

- first index > `L`
- last index <= `R`

and query the Fenwick Tree.

---

# 7. Counting Pairs for a Fixed Threshold x

Suppose we are at `nums[j] = v`.

We need previous values in:

```text
(v, v + x]
```

Steps:

1. Find compressed range corresponding to values:
   - greater than `v`
   - at most `v + x`
2. Query BIT for how many previous numbers fall in that range
3. Add current number `v` into BIT

This counts all valid pairs `(i, j)` with `i < j`.

---

# 8. Why This Counts Exactly the Right Pairs

For every `j`, we only query previously inserted elements, so `i < j` is guaranteed.

The query range enforces:

```text
nums[i] > nums[j]
nums[i] <= nums[j] + x
```

which is equivalent to:

```text
nums[i] > nums[j]
nums[i] - nums[j] <= x
```

So each valid pair is counted once.

---

# 9. Full Optimal Java Solution

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        long[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new long[n + 2];
        }

        void add(int idx, long delta) {
            for (idx++; idx <= n + 1; idx += idx & -idx) {
                bit[idx] += delta;
            }
        }

        long sumPrefix(int idx) {
            long res = 0;
            for (idx++; idx > 0; idx -= idx & -idx) {
                res += bit[idx];
            }
            return res;
        }

        long rangeSum(int l, int r) {
            if (l > r) return 0;
            return sumPrefix(r) - (l == 0 ? 0 : sumPrefix(l - 1));
        }
    }

    public int minThreshold(int[] nums, int k) {
        int n = nums.length;

        // Count total inversion pairs and max difference to know the binary-search range.
        long totalInv = 0;
        int hi = -1;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j]) {
                    totalInv++;
                    hi = Math.max(hi, nums[i] - nums[j]);
                }
            }
        }

        if (totalInv < k) return -1;
        if (hi == -1) return -1; // no inversion at all

        int[] sorted = Arrays.stream(nums).distinct().sorted().toArray();

        int lo = 0, ans = hi;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            long cnt = countPairs(nums, sorted, mid);

            if (cnt >= k) {
                ans = mid;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        return ans;
    }

    private long countPairs(int[] nums, int[] sorted, int x) {
        Fenwick fw = new Fenwick(sorted.length);
        long cnt = 0;

        for (int v : nums) {
            int left = upperBound(sorted, v);         // first > v
            int right = upperBound(sorted, v + x) - 1; // last <= v+x

            if (left <= right) {
                cnt += fw.rangeSum(left, right);
            }

            int pos = Arrays.binarySearch(sorted, v);
            fw.add(pos, 1);
        }

        return cnt;
    }

    private int upperBound(int[] arr, long target) {
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] <= target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
```

---

# 10. Complexity Analysis

Let `n = nums.length`.

## Pre-check total inversions and max difference

The implementation above uses a simple `O(n^2)` pre-check:

```text
O(n^2)
```

With `n <= 10^4`, this is acceptable in many settings, but we can improve it.

## Binary search loop

- binary search over threshold: `O(log V)`
- each count with Fenwick: `O(n log n)`

So:

```text
O(n log n log V)
```

for the counting phase.

Overall with the simple pre-check:

```text
O(n^2 + n log n log V)
```

Because `n = 10^4`, this is often fine.

---

# 11. Improved Version — Also Compute Total Inversions in O(n log n)

If we want a cleaner asymptotic bound, we can compute total inversions and even the maximum difference more carefully.

## Total inversion count

Use Fenwick Tree from right to left:

- for each `nums[i]`, count how many smaller elements have appeared to the right

This gives total inversion count in:

```text
O(n log n)
```

## Maximum possible valid threshold

The largest threshold that can ever matter is:

```text
max(nums[i] - nums[j]) over inversions
```

A simple `O(n^2)` way computes it, but we can also get a safe upper bound:

```text
max(nums) - min(nums)
```

That is enough for binary search.

So we do not actually need the exact maximum inversion difference.

That improves the whole solution to:

```text
O(n log n log V)
```

---

# 12. Cleaner Recommended Java Solution

This is the version I would recommend in practice.

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        long[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new long[n + 2];
        }

        void add(int idx, long delta) {
            for (idx++; idx <= n + 1; idx += idx & -idx) {
                bit[idx] += delta;
            }
        }

        long sumPrefix(int idx) {
            long res = 0;
            for (idx++; idx > 0; idx -= idx & -idx) {
                res += bit[idx];
            }
            return res;
        }

        long rangeSum(int l, int r) {
            if (l > r) return 0;
            return sumPrefix(r) - (l == 0 ? 0 : sumPrefix(l - 1));
        }
    }

    public int minThreshold(int[] nums, int k) {
        int[] sorted = Arrays.stream(nums).distinct().sorted().toArray();

        long totalInv = countTotalInversions(nums, sorted);
        if (totalInv < k) return -1;

        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        for (int v : nums) {
            minVal = Math.min(minVal, v);
            maxVal = Math.max(maxVal, v);
        }

        int lo = 0, hi = maxVal - minVal, ans = hi;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            long cnt = countPairs(nums, sorted, mid);

            if (cnt >= k) {
                ans = mid;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        return ans;
    }

    private long countTotalInversions(int[] nums, int[] sorted) {
        Fenwick fw = new Fenwick(sorted.length);
        long inv = 0;

        for (int i = nums.length - 1; i >= 0; i--) {
            int pos = Arrays.binarySearch(sorted, nums[i]);
            inv += fw.rangeSum(0, pos - 1); // smaller values to the right
            fw.add(pos, 1);
        }

        return inv;
    }

    private long countPairs(int[] nums, int[] sorted, int x) {
        Fenwick fw = new Fenwick(sorted.length);
        long cnt = 0;

        for (int v : nums) {
            int left = upperBound(sorted, v);             // first > v
            int right = upperBound(sorted, (long) v + x) - 1; // last <= v+x

            if (left <= right) {
                cnt += fw.rangeSum(left, right);
            }

            int pos = Arrays.binarySearch(sorted, v);
            fw.add(pos, 1);
        }

        return cnt;
    }

    private int upperBound(int[] arr, long target) {
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] <= target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
```

---

# 13. Dry Run on Example 1

```text
nums = [1,2,3,4,3,2,1]
k = 7
```

Try threshold:

```text
x = 2
```

Process left to right.

For each value `v = nums[j]`, count previous values in:

```text
(v, v+2]
```

Examples:

- at last `1`, previous values in `(1, 3]` are `2,3,3,2`
- that contributes 4 valid inversion pairs ending there

Accumulating over all positions gives at least `7`.

If `x = 1`, there are fewer than `7`.

So answer is `2`.

---

# 14. Alternative Approach — Merge Sort Based Counting

Another valid way to count `count(x)` is a modified merge sort.

## Idea

Classic inversion count uses merge sort.

For this problem, while merging sorted halves, count pairs satisfying:

```text
leftValue > rightValue
leftValue <= rightValue + x
```

This is more delicate because we need a **bounded inversion count**, not all inversions.

It can still be done with two pointers inside merge sort.

## Why Fenwick is easier here

Fenwick + coordinate compression is much easier to reason about and implement correctly.

So although merge sort is possible, it is not the cleanest choice for this problem.

---

# 15. Common Mistakes

## Mistake 1 — Forgetting monotonicity

The threshold answer should be found by binary search, not linear scan.

## Mistake 2 — Counting the wrong direction

The condition is:

```text
i < j
nums[i] > nums[j]
```

When processing `j`, only count earlier indices `i`.

## Mistake 3 — Off-by-one in the range

We need:

```text
nums[j] < nums[i] <= nums[j] + x
```

So the lower bound is **strictly greater than** `nums[j]`, not greater than or equal.

## Mistake 4 — Overflow in `v + x`

Use `long` in:

```java
upperBound(sorted, (long)v + x)
```

because `v` can be up to `10^9`.

## Mistake 5 — Forgetting the impossible case

If total inversion pairs in the array are less than `k`, answer is `-1`.

---

# 16. Correctness Sketch

## Lemma 1

For a fixed threshold `x`, the number of valid pairs is monotone in `x`.

### Reason

If a pair satisfies `nums[i] - nums[j] <= x`, then it also satisfies the condition for every larger threshold.

So binary search is valid.

## Lemma 2

For a fixed `j`, the valid earlier values `nums[i]` are exactly those in:

```text
(nums[j], nums[j] + x]
```

### Reason

That interval is exactly the combined form of:

```text
nums[i] > nums[j]
nums[i] - nums[j] <= x
```

## Lemma 3

The Fenwick query at step `j` counts exactly the number of valid indices `i < j`.

### Reason

The Fenwick tree stores exactly the multiset of earlier values.
The queried compressed range corresponds exactly to valid values from Lemma 2.

## Lemma 4

Summing those counts over all `j` counts every valid pair exactly once.

### Reason

Each pair has a unique ending index `j`, and is counted when processing that `j`.

## Theorem

The algorithm returns the minimum threshold whose valid pair count is at least `k`.

### Reason

Binary search finds the smallest threshold where the monotone predicate

```text
count(x) >= k
```

becomes true, and the Fenwick-based counter computes `count(x)` correctly.

---

# 17. Comparison of Approaches

| Approach                            | Count Method          |               Time |  Space | Verdict                  |
| ----------------------------------- | --------------------- | -----------------: | -----: | ------------------------ |
| Brute force thresholds              | nested loops          |           terrible | `O(1)` | useless except intuition |
| Binary search + brute force count   | nested loops          |     `O(n^2 log V)` | `O(1)` | simpler but weaker       |
| Binary search + Fenwick             | range count by values | `O(n log n log V)` | `O(n)` | recommended              |
| Binary search + merge sort counting | custom merge logic    | `O(n log n log V)` | `O(n)` | possible but trickier    |

---

# 18. Final Interview Summary

This problem is a classic monotone-answer problem.

Let:

```text
count(x) = number of inversion pairs whose value difference is at most x
```

Then `count(x)` is monotone, so binary search on `x`.

To compute `count(x)` efficiently:

- process the array left to right
- for each `nums[j]`, count earlier values in:

```text
(nums[j], nums[j] + x]
```

- use coordinate compression + Fenwick Tree for range counting

That gives an efficient and clean solution:

```text
O(n log n log V)
```

which easily fits the constraints.
