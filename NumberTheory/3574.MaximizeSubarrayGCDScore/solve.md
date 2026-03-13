# 3574. Maximize Subarray GCD Score — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public long maxGCDScore(int[] nums, int k) {

    }
}
```

---

# Problem Restatement

We are given:

- an array `nums`
- an integer `k`

We may perform at most `k` operations.

In one operation:

- choose one element
- double it
- each element can be doubled at most once

For any contiguous subarray, its score is:

```text
score = (length of subarray) * gcd(of all elements in subarray)
```

We must return the maximum possible score over all subarrays after performing at most `k` allowed doublings.

---

# Core Insight

Doubling an element only changes its factorization by adding **one extra factor of 2**.

So the odd part of each number never changes.

That means for any chosen subarray:

- its GCD’s odd part is fixed
- only the power of 2 in the GCD can increase
- and it can increase by at most 1

But that increase happens only if we can make **every element in the subarray** divisible by one more factor of 2.

So for a fixed subarray, the question is:

> Can we use at most `k` doublings so that the GCD of this subarray is doubled?

This leads to a very clean condition.

---

# v2 Decomposition

For each number `x`, define:

- `v2(x)` = exponent of 2 in `x`
- `odd(x)` = odd part of `x`

Example:

```text
12 = 2^2 * 3
v2(12) = 2
odd(12) = 3
```

For a subarray:

```text
nums[l..r]
```

let:

```text
g = gcd(nums[l], nums[l+1], ..., nums[r])
```

Then:

- the base score is `(r-l+1) * g`
- after optional doublings, the GCD can only become either:
  - `g`
  - or `2g`

It cannot become `4g` or higher, because each element can be doubled at most once.

---

# When Can the GCD Double?

Suppose the subarray GCD is `g`.

Write:

```text
m = min(v2(nums[i])) over the subarray
```

Then the subarray GCD contains exactly `2^m`.

To make the GCD gain one more factor of 2, every element whose `v2` equals `m` must be doubled.

Why?

Because those are exactly the bottleneck elements preventing one more common factor of 2.

So:

> The GCD of a subarray can be doubled iff the number of elements in the subarray having the minimum `v2` is at most `k`.

That is the main reduction.

So for each subarray we only need:

1. the GCD of the subarray
2. the minimum `v2` in the subarray
3. how many elements achieve that minimum

Then the best score for that subarray is:

```text
len * gcd          if min-v2 frequency > k
len * 2 * gcd      if min-v2 frequency <= k
```

---

# Approach 1 — Enumerate All Subarrays with Incremental GCD and v2 Tracking (Recommended)

## Idea

Since:

```text
n <= 1500
```

an `O(n^2)` or `O(n^2 log A)` solution is fully acceptable.

For each starting index `l`, extend the subarray to the right:

- update the running gcd
- update the minimum `v2`
- update how many times that minimum occurs

This gives all necessary information for each subarray in `O(1)` amortized per extension, except the gcd operation.

Then compute the best score immediately.

---

## Why this works

As we extend from `l` to `r`:

- `gcd` can be updated incrementally:
  ```text
  gcd(g, nums[r])
  ```
- `v2(nums[r])` is easy to compute once
- the minimum `v2` and its count can also be updated incrementally

So each subarray can be processed directly without any complicated data structure.

---

## Java Code

```java
class Solution {
    public long maxGCDScore(int[] nums, int k) {
        int n = nums.length;
        int[] twos = new int[n];

        for (int i = 0; i < n; i++) {
            twos[i] = v2(nums[i]);
        }

        long ans = 0;

        for (int l = 0; l < n; l++) {
            long g = 0;
            int minV2 = Integer.MAX_VALUE;
            int minCount = 0;

            for (int r = l; r < n; r++) {
                g = gcd(g, nums[r]);

                if (twos[r] < minV2) {
                    minV2 = twos[r];
                    minCount = 1;
                } else if (twos[r] == minV2) {
                    minCount++;
                }

                long len = r - l + 1L;
                long bestG = g;
                if (minCount <= k) {
                    bestG = g * 2L;
                }

                ans = Math.max(ans, len * bestG);
            }
        }

        return ans;
    }

    private int v2(int x) {
        int cnt = 0;
        while ((x & 1) == 0) {
            x >>= 1;
            cnt++;
        }
        return cnt;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

For each left endpoint, we expand all right endpoints.

So:

```text
Time:  O(n^2 * log A)
Space: O(n)
```

where `A` is the magnitude of the numbers.

Since `n <= 1500`, this is fine.

---

# Approach 2 — Prefix-Like GCD Compression Per Right Endpoint

## Idea

A standard trick for subarray gcd problems is:

- for each right endpoint `r`
- maintain all distinct gcd values of subarrays ending at `r`

Because the number of distinct gcd values per endpoint is small, this often gives a faster practical solution.

Here we would also need to carry:

- minimum `v2`
- frequency of that minimum

So each state becomes richer:

```text
(gcd, minV2, minCount, shortest/longest span info)
```

This can be done, but it becomes more complicated than necessary for `n <= 1500`.

Still, it is a legitimate alternative viewpoint.

---

## High-Level Sketch

For each `r`, take all states ending at `r-1` and extend by `nums[r]`:

- new gcd = gcd(oldGcd, nums[r])
- update minV2/minCount with `v2(nums[r])`

Also start the one-element subarray `[r..r]`.

Then evaluate the score of every resulting state.

Because many states may merge to the same gcd, compression can reduce practical work.

---

## Why it is less attractive here

Although elegant, it complicates the bookkeeping for:

- how many elements have the minimum `v2`
- segment length
- merging states correctly

Given `n <= 1500`, the direct quadratic solution is much cleaner.

---

# Approach 3 — Brute Force With Recomputing GCD and Min v2 Every Time (Too Slow)

## Idea

Try every subarray `[l..r]`, and for each one:

- scan all its elements
- compute gcd from scratch
- compute min `v2`
- compute how many times min `v2` occurs

This is straightforward but cubic in the worst case.

---

## Java Code

```java
class Solution {
    public long maxGCDScore(int[] nums, int k) {
        int n = nums.length;
        long ans = 0;

        for (int l = 0; l < n; l++) {
            for (int r = l; r < n; r++) {
                long g = 0;
                int minV2 = Integer.MAX_VALUE;
                int minCount = 0;

                for (int i = l; i <= r; i++) {
                    g = gcd(g, nums[i]);
                    int t = v2(nums[i]);
                    if (t < minV2) {
                        minV2 = t;
                        minCount = 1;
                    } else if (t == minV2) {
                        minCount++;
                    }
                }

                long bestG = (minCount <= k ? g * 2L : g);
                ans = Math.max(ans, (long)(r - l + 1) * bestG);
            }
        }

        return ans;
    }

    private int v2(int x) {
        int cnt = 0;
        while ((x & 1) == 0) {
            x >>= 1;
            cnt++;
        }
        return cnt;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Why it fails

This is roughly:

```text
O(n^3 log A)
```

which is too slow for `n = 1500`.

So it is only useful as a conceptual baseline.

---

# Detailed Walkthrough

## Example 1

```text
nums = [2,4], k = 1
```

Subarray `[2,4]`:

- gcd = 2
- `v2(2)=1`, `v2(4)=2`
- minimum `v2 = 1`
- count of minimum = 1

Since `1 <= k`, we can double the bottleneck element `2 -> 4`.

Then subarray becomes `[4,4]`:

- new gcd = 4
- length = 2
- score = 8

So answer is:

```text
8
```

---

## Example 2

```text
nums = [3,5,7], k = 2
```

All numbers are odd, so each has `v2 = 0`.

For subarray `[3,5,7]`:

- gcd = 1
- minimum `v2 = 0`
- count of minimum = 3

Since `3 > k`, we cannot double all bottlenecks, so gcd cannot become 2.

Best large subarray scores are small.

But a single element subarray `[7]`:

- gcd = 7
- min-count = 1 <= 2

So we can double it to `14`, giving score:

```text
1 * 14 = 14
```

which is maximal.

---

## Example 3

```text
nums = [5,5,5], k = 1
```

Whole subarray:

- gcd = 5
- all `v2 = 0`
- min-count = 3

We would need to double all three elements to make gcd become 10, but `k = 1`.

So gcd stays 5.

Length is 3, so score:

```text
3 * 5 = 15
```

That is the maximum.

---

# Important Correctness Argument

For any subarray, let its gcd be:

```text
g = 2^m * oddPart
```

where `m` is the minimum `v2` among its elements.

Doubling an element increases only its `v2` by 1.

So after all allowed operations, every element’s odd part is unchanged, and the common odd part of the subarray gcd is unchanged.

The only possible improvement is increasing the common power of 2 from `2^m` to `2^(m+1)`.

That is possible iff every element currently achieving the minimum `v2 = m` is doubled.

Hence the condition “minimum-v2 frequency <= k” is both necessary and sufficient.

So the score formula used by the algorithm is exact.

---

# Common Pitfalls

## 1. Thinking repeated doublings of one element are allowed

They are not.
Each element can be doubled at most once.

---

## 2. Assuming GCD can increase by more than 2x

It cannot.
Each element gets at most one extra factor of 2, so the subarray gcd can gain at most one extra factor of 2.

---

## 3. Ignoring the odd part of the GCD

Doubling never changes odd prime factors, so only the power of 2 matters.

---

## 4. Trying cubic brute force

That is too slow for `n = 1500`.

---

# Best Approach

## Recommended: Enumerate all subarrays with incremental gcd and minimum-v2 tracking

This is the best practical solution because:

- the problem’s real structure is simple once you isolate the power-of-2 behavior
- `n <= 1500` allows a clean quadratic solution
- implementation is short and robust

---

# Final Recommended Java Solution

```java
class Solution {
    public long maxGCDScore(int[] nums, int k) {
        int n = nums.length;
        int[] twos = new int[n];

        for (int i = 0; i < n; i++) {
            twos[i] = v2(nums[i]);
        }

        long ans = 0;

        for (int l = 0; l < n; l++) {
            long g = 0;
            int minV2 = Integer.MAX_VALUE;
            int minCount = 0;

            for (int r = l; r < n; r++) {
                g = gcd(g, nums[r]);

                if (twos[r] < minV2) {
                    minV2 = twos[r];
                    minCount = 1;
                } else if (twos[r] == minV2) {
                    minCount++;
                }

                long bestG = g;
                if (minCount <= k) {
                    bestG = g * 2L;
                }

                long len = r - l + 1L;
                ans = Math.max(ans, len * bestG);
            }
        }

        return ans;
    }

    private int v2(int x) {
        int cnt = 0;
        while ((x & 1) == 0) {
            x >>= 1;
            cnt++;
        }
        return cnt;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n^2 * log A)
Space: O(n)
```

This is efficient enough for:

```text
n <= 1500
```

---

# Final Takeaway

The key simplification is:

- doubling changes only the power of 2
- therefore a subarray GCD can only stay the same or double
- it doubles exactly when all minimum-`v2` elements in that subarray can be chosen

That turns the problem into a clean quadratic scan over subarrays with incremental tracking.
