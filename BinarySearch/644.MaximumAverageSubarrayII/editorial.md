# 644. Maximum Average Subarray II — Solution Approaches

---

## Problem Summary

Given an integer array `nums` of length `n` and integer `k`, find the **contiguous subarray of length ≥ k** with the **maximum average** and return that average value.

---

## Approach 1 — Brute Force (O(n²))

### Explanation

Try every possible subarray whose length is ≥ k. For each starting index `i`, expand the window to every valid ending index `j` (where `j - i + 1 >= k`), compute the sum, and track the maximum average.

- **Time Complexity:** O(n²) — two nested loops
- **Space Complexity:** O(1)
- **Verdict:** Correct but too slow for n = 10⁴ (TLE risk). Useful to understand the baseline.

### Java Code

```java
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        double maxAvg = Double.NEGATIVE_INFINITY;

        for (int i = 0; i <= nums.length - k; i++) {
            double sum = 0;
            // Build up to length k first
            for (int j = i; j < i + k; j++) {
                sum += nums[j];
            }
            maxAvg = Math.max(maxAvg, sum / k);

            // Extend window beyond k
            for (int j = i + k; j < nums.length; j++) {
                sum += nums[j];
                maxAvg = Math.max(maxAvg, sum / (j - i + 1));
            }
        }

        return maxAvg;
    }
}
```

---

## Approach 2 — Prefix Sum + Sliding Window (O(n²), optimised constant)

### Explanation

Precompute a **prefix sum array** so that the sum of any subarray `[i, j]` is computed in O(1):

```
prefixSum[j+1] - prefixSum[i]
```

Then iterate over all valid `(i, j)` pairs where `j - i + 1 >= k`. The prefix sum eliminates the inner recomputation of sums.

- **Time Complexity:** O(n²)
- **Space Complexity:** O(n) for the prefix array
- **Verdict:** Same asymptotic complexity as brute force but cleaner and faster in practice.

### Java Code

```java
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        int n = nums.length;
        double[] prefix = new double[n + 1];

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        double maxAvg = Double.NEGATIVE_INFINITY;

        for (int i = 0; i <= n - k; i++) {
            for (int j = i + k; j <= n; j++) {
                double avg = (prefix[j] - prefix[i]) / (j - i);
                maxAvg = Math.max(maxAvg, avg);
            }
        }

        return maxAvg;
    }
}
```

---

## Approach 3 — Binary Search on the Answer (O(n log(max−min))) ✅ Optimal

### Explanation

This is the classic **binary search on the answer** technique.

#### Key Insight

Instead of searching through subarrays directly, ask:

> _"Is there a subarray of length ≥ k with average ≥ mid?"_

If `mid` is the candidate average, subtract `mid` from every element of `nums`. Now the question becomes:

> _"Does the transformed array have a subarray of length ≥ k with sum ≥ 0?"_

This check can be done in **O(n)** using a sliding minimum of prefix sums.

#### Binary Search Range

- **Low:** minimum value in `nums`
- **High:** maximum value in `nums`
- Converge until `high - low < 1e-5`

#### Feasibility Check — O(n)

For the transformed array `t[i] = nums[i] - mid`:

1. Compute prefix sums of `t`.
2. For each `j >= k`, check if `prefix[j] - min(prefix[0..j-k]) >= 0`.
3. Track the running minimum of `prefix[0]` through `prefix[j-k]` as `j` moves forward.

If any such pair satisfies the condition, the answer is feasible (there exists a valid subarray with average ≥ mid).

#### Complexity

- **Time:** O(n · log((max − min) / 1e-5)) — roughly O(n · 64) ≈ O(n log n) in practice
- **Space:** O(1) extra (excluding input)

### Java Code

```java
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        double lo = nums[0], hi = nums[0];

        // Find the search range
        for (int num : nums) {
            lo = Math.min(lo, num);
            hi = Math.max(hi, num);
        }

        // Binary search on the answer
        while (hi - lo > 1e-6) {
            double mid = lo + (hi - lo) / 2.0;
            if (isFeasible(nums, k, mid)) {
                lo = mid; // mid is achievable, try higher
            } else {
                hi = mid; // mid is too high, go lower
            }
        }

        return lo;
    }

    /**
     * Returns true if there exists a subarray of length >= k
     * whose average is >= target.
     *
     * Equivalent to: does (nums[i] - target) summed over some
     * subarray of length >= k give a non-negative result?
     */
    private boolean isFeasible(int[] nums, int k, double target) {
        int n = nums.length;

        // Build prefix sums of the transformed array
        double[] prefix = new double[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + (nums[i] - target);
        }

        // For each j >= k, check prefix[j] - min(prefix[0..j-k]) >= 0
        double minPrefix = prefix[0]; // minimum of prefix[0..j-k]

        for (int j = k; j <= n; j++) {
            // Update minimum with prefix[j - k] entering the window
            minPrefix = Math.min(minPrefix, prefix[j - k]);
            if (prefix[j] - minPrefix >= 0) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Dry Run — Example 1

```
nums = [1, 12, -5, -6, 50, 3],  k = 4
lo = -6,  hi = 50
```

| Iteration | lo   | hi    | mid    | Feasible?              |
| --------- | ---- | ----- | ------ | ---------------------- |
| 1         | -6   | 50    | 22.0   | No                     |
| 2         | -6   | 22.0  | 8.0    | Yes                    |
| 3         | 8.0  | 22.0  | 15.0   | No                     |
| 4         | 8.0  | 15.0  | 11.5   | Yes                    |
| 5         | 11.5 | 15.0  | 13.25  | No                     |
| 6         | 11.5 | 13.25 | 12.375 | Yes                    |
| …         | …    | …     | …      | converges to **12.75** |

---

## Comparison of Approaches

| Approach         | Time Complexity | Space | Notes                           |
| ---------------- | --------------- | ----- | ------------------------------- |
| Brute Force      | O(n²)           | O(1)  | Simple, TLE for large n         |
| Prefix Sum       | O(n²)           | O(n)  | Cleaner, same big-O             |
| Binary Search ✅ | O(n log R)      | O(n)  | Optimal, passes all constraints |

> **R** = (max − min) / tolerance ≈ 20000 / 1e-5 = 2 × 10⁹ → ~31 iterations

---

## Key Takeaways

- **Binary search on the answer** is a powerful technique when the answer is continuous (or monotone) and a feasibility check is efficient.
- Transforming "average ≥ target" into "sum of shifted array ≥ 0" is the core trick that makes the O(n) check possible.
- The sliding minimum of prefix sums lets us efficiently find the best left boundary for any right boundary in a single pass.
