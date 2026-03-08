# Maximum GCD-Sum of a Subarray With Length At Least `k`

## Problem

You are given:

- an integer array `nums`
- an integer `k`

For any subarray `a = nums[l..r]`:

- let `s` be the sum of the elements of that subarray
- let `g` be the GCD of all elements in that subarray

The **gcd-sum** of the subarray is:

`gcd-sum = s * g`

We need to return the **maximum gcd-sum** among all subarrays whose length is **at least `k`**.

---

# 1. First principles

A brute-force approach would try every subarray:

- choose `l`
- choose `r`
- compute sum
- compute gcd
- check length
- update answer

There are `O(n^2)` subarrays, and repeatedly computing GCD makes that too slow.

So the real question is:

**How do we avoid checking every subarray naively?**

---

# 2. Core mathematical intuition

## Observation 1: For a fixed right endpoint `r`, the GCDs do not vary arbitrarily

Fix the right end of the subarray at index `r`.

Now consider all subarrays ending at `r`:

- `nums[r..r]`
- `nums[r-1..r]`
- `nums[r-2..r]`
- ...
- `nums[0..r]`

As we move the left boundary leftward, the GCD can only:

- stay the same, or
- decrease

It can never increase.

That is because adding more numbers to a subarray can only preserve or reduce the GCD.

---

## Observation 2: Equal GCD values appear in contiguous blocks of start indices

For a fixed `r`, define:

`f(l) = gcd(nums[l..r])`

As `l` moves from `r` down to `0`, the value of `f(l)` changes only a small number of times.

So the possible starts `l` can be grouped into contiguous ranges where the GCD is the same.

Example shape:

- starts in `[8..10]` all give GCD `12`
- starts in `[5..7]` all give GCD `6`
- starts in `[0..4]` all give GCD `3`

This means that instead of examining every start one by one, we can examine **GCD-ranges**.

That is the central optimization.

---

## Observation 3: For a fixed GCD-range, the best start is the earliest valid one

Suppose for a fixed right endpoint `r`, and a fixed GCD value `g`, the starts in interval `[L..P]` all produce the same GCD `g`.

Then every subarray in this interval has value:

`(sum of nums[l..r]) * g`

Since `g` is fixed across the whole range, maximizing the gcd-sum becomes the same as maximizing the sum.

Now the array elements are positive integers, so:

- earlier start
- longer subarray
- larger sum

Therefore, among all valid starts in that GCD-range, the **leftmost valid start** gives the maximum sum and thus the maximum `sum * g`.

This is a critical simplification.

Without the “all numbers are positive” fact, this would not be safe.

---

# 3. High-level solution idea

For each right endpoint `r`:

1. Identify all contiguous ranges of left endpoints `l` such that `gcd(nums[l..r])` is constant.
2. For each such GCD-range:
   - check whether there exists a start in that range with length at least `k`
   - if yes, use the **earliest valid start** from that range
   - compute the corresponding gcd-sum
3. Update the global maximum

To do this efficiently, we need two supporting tools:

- **Prefix sums** for fast subarray sums
- **Sparse table for GCD** for fast GCD range queries

---

# 4. Prefix sums

Let:

- `pref[0] = 0`
- `pref[i + 1] = nums[0] + nums[1] + ... + nums[i]`

Then the sum of subarray `nums[l..r]` is:

`sum(l..r) = pref[r + 1] - pref[l]`

This gives subarray sum in `O(1)` time.

---

# 5. Sparse table for GCD

We need to query many values of:

`gcd(nums[l..r])`

A sparse table is suitable because:

- GCD is associative
- the array is static

## Sparse table definition

Let:

`st[j][i] = gcd of subarray nums[i..i + 2^j - 1]`

Then:

- `st[0][i] = nums[i]`
- `st[j][i] = gcd(st[j-1][i], st[j-1][i + 2^(j-1)])`

To answer a GCD query on `[l..r]`:

- let `len = r - l + 1`
- let `j = floor(log2(len))`

Then:

`gcd(nums[l..r]) = gcd(st[j][l], st[j][r - 2^j + 1])`

So after preprocessing:

- query time is `O(1)`
- preprocessing time is `O(n log n)`

---

# 6. How to enumerate GCD-ranges for one fixed `r`

Fix a right endpoint `r`.

We want to partition all start indices `l` into blocks where `gcd(nums[l..r])` is constant.

We do this from right to left.

Let:

`p = r`

At each step:

1. Compute `g = gcd(nums[p..r])`
2. Find the **leftmost** index `L` such that:
   `gcd(nums[L..r]) == g`
3. Then every start in `[L..p]` has GCD exactly `g`
4. Process that whole block at once
5. Move to the next block by setting:
   `p = L - 1`

Repeat until `p < 0`.

---

# 7. Why binary search works to find `L`

For fixed `r` and fixed `g = gcd(nums[p..r])`, the starts producing GCD `g` form a contiguous range.

So we can binary search the smallest `L` in `[0..p]` such that:

`gcd(nums[L..r]) == g`

Each check is an `O(1)` sparse-table GCD query, so finding `L` costs `O(log n)`.

---

# 8. Enforcing the length-at-least-`k` constraint

A subarray `nums[l..r]` has length:

`r - l + 1`

We need:

`r - l + 1 >= k`

Rearranging:

`l <= r - k + 1`

Define:

`limit = r - k + 1`

So for fixed `r`, a start `l` is valid iff:

`l <= limit`

Now consider a GCD-range `[L..P]`.

- If `L > limit`, then even the earliest start in that range is too far right, so **no subarray in that range is long enough**
- If `L <= limit`, then there exists at least one valid start in that range

Because all numbers are positive, the best valid start is the earliest one, namely `L`.

So the candidate value from this range is:

`(pref[r + 1] - pref[L]) * g`

---

# 9. Complete algorithm

## Preprocessing

1. Build prefix sums
2. Build sparse table for GCD
3. Build `log2[]` lookup array

## Main loop

For each `r` from `0` to `n - 1`:

1. Set `p = r`
2. Set `limit = r - k + 1`
3. While `p >= 0`:
   - compute `g = gcd(p..r)`
   - binary search for the leftmost `L` with `gcd(L..r) == g`
   - now starts in `[L..p]` all have GCD `g`
   - if `L <= limit`, compute:
     `sum = pref[r + 1] - pref[L]`
     `candidate = sum * g`
     update answer
   - set `p = L - 1`

Return the maximum answer.

---

# 10. Why this is correct

## Claim 1

For fixed `r`, every start index belongs to exactly one GCD-range.

Every subarray `nums[l..r]` has one exact GCD value, and equal-GCD starts form contiguous blocks. The enumeration walks these blocks from right to left with no overlap and no omission.

---

## Claim 2

Within a single GCD-range `[L..P]`, checking only `L` is enough.

Every start in `[L..P]` has the same GCD `g`.

So the gcd-sum for start `x` in this interval is:

`(pref[r + 1] - pref[x]) * g`

Since `g` is fixed and all numbers are positive, earlier start means larger or equal sum.

Therefore the best start in the range is the earliest valid one.

If the range contains any valid start, `L` is optimal for that range.

---

## Claim 3

The optimal answer must be found by examining these range representatives.

Take any optimal subarray `nums[l..r]`.

When processing endpoint `r`, that start `l` lies inside one GCD-range `[L..P]` with the same GCD `g`.

Since `L <= l` and all values are positive:

`sum(L..r) >= sum(l..r)`

and the GCD remains `g`.

So:

`sum(L..r) * g >= sum(l..r) * g`

Hence considering the earliest valid start in each GCD-range is sufficient to capture the optimum.

This proves correctness.

---

# 11. Walkthrough example

Suppose:

`nums = [6, 9, 3]`
`k = 2`

All valid subarrays with length at least 2 are:

- `[6, 9]`
  - sum = 15
  - gcd = 3
  - value = 45

- `[9, 3]`
  - sum = 12
  - gcd = 3
  - value = 36

- `[6, 9, 3]`
  - sum = 18
  - gcd = 3
  - value = 54

Answer = `54`

## Prefix sums

`pref = [0, 6, 15, 18]`

---

## For `r = 0`

`limit = -1`

No valid subarray of length at least 2 ends at index 0.

---

## For `r = 1`

`limit = 0`
`p = 1`

### Block 1

`g = gcd(nums[1..1]) = 9`

Leftmost `L` with gcd `9` is `1`.

So range is `[1..1]`.

But `1 > 0`, so it is too short.

Set `p = 0`.

### Block 2

`g = gcd(nums[0..1]) = gcd(6, 9) = 3`

Leftmost `L` with gcd `3` is `0`.

Range is `[0..0]`.

Now `0 <= 0`, valid.

Sum = `pref[2] - pref[0] = 15`
Candidate = `15 * 3 = 45`

Answer becomes `45`.

---

## For `r = 2`

`limit = 1`
`p = 2`

### Block 1

`g = gcd(nums[2..2]) = 3`

Find leftmost `L` with gcd `3`.

We check:

- `gcd(nums[2..2]) = 3`
- `gcd(nums[1..2]) = 3`
- `gcd(nums[0..2]) = 3`

So `L = 0`.

This means all starts in `[0..2]` produce gcd `3`.

Since `0 <= 1`, this block is valid.

Best start is `0`.

Sum = `pref[3] - pref[0] = 18`
Candidate = `18 * 3 = 54`

Answer becomes `54`.

Done.

---

# 12. Java code

```java
import java.util.*;

class Solution {
    private int[][] st;
    private int[] log2;
    private long[] pref;

    public long maxGcdSum(int[] nums, int k) {
        int n = nums.length;

        buildPrefix(nums);
        buildSparseTable(nums);

        long ans = 0;

        for (int r = 0; r < n; r++) {
            int p = r;
            int limit = r - k + 1;

            while (p >= 0) {
                int g = queryGcd(p, r);

                // Find the leftmost index L such that gcd(nums[L..r]) == g
                int lo = 0, hi = p;
                while (lo < hi) {
                    int mid = (lo + hi) >>> 1;
                    if (queryGcd(mid, r) == g) {
                        hi = mid;
                    } else {
                        lo = mid + 1;
                    }
                }
                int L = lo;

                // Starts in [L..p] all have gcd == g
                if (L <= limit) {
                    long sum = pref[r + 1] - pref[L];
                    ans = Math.max(ans, sum * g);
                }

                p = L - 1;
            }
        }

        return ans;
    }

    private void buildPrefix(int[] nums) {
        int n = nums.length;
        pref = new long[n + 1];
        for (int i = 0; i < n; i++) {
            pref[i + 1] = pref[i] + nums[i];
        }
    }

    private void buildSparseTable(int[] nums) {
        int n = nums.length;

        log2 = new int[n + 1];
        for (int i = 2; i <= n; i++) {
            log2[i] = log2[i / 2] + 1;
        }

        int maxLog = log2[n] + 1;
        st = new int[maxLog][n];

        for (int i = 0; i < n; i++) {
            st[0][i] = nums[i];
        }

        for (int j = 1; j < maxLog; j++) {
            int len = 1 << j;
            int half = len >> 1;
            for (int i = 0; i + len <= n; i++) {
                st[j][i] = gcd(st[j - 1][i], st[j - 1][i + half]);
            }
        }
    }

    private int queryGcd(int l, int r) {
        int len = r - l + 1;
        int j = log2[len];
        return gcd(st[j][l], st[j][r - (1 << j) + 1]);
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# 13. Code explanation in detail

## Fields

```java
private int[][] st;
private int[] log2;
private long[] pref;
```

- `st` stores the sparse table
- `log2` stores floor-log values
- `pref` stores prefix sums

---

## Main method

```java
public long maxGcdSum(int[] nums, int k)
```

This is the main solver.

### Step A: preprocessing

```java
buildPrefix(nums);
buildSparseTable(nums);
```

These allow fast sum and gcd queries.

---

### Step B: try every right endpoint

```java
for (int r = 0; r < n; r++) {
    int p = r;
    int limit = r - k + 1;
```

- `r` is the fixed right boundary
- `p` marks the current unprocessed rightmost start
- `limit` is the maximum allowed start index for a valid length

---

### Step C: enumerate GCD-ranges

```java
while (p >= 0) {
    int g = queryGcd(p, r);
```

This obtains the GCD value for the current suffix ending at `r`.

---

### Step D: binary search the left boundary of this GCD block

```java
int lo = 0, hi = p;
while (lo < hi) {
    int mid = (lo + hi) >>> 1;
    if (queryGcd(mid, r) == g) {
        hi = mid;
    } else {
        lo = mid + 1;
    }
}
int L = lo;
```

This finds the leftmost start `L` whose subarray ending at `r` still has GCD `g`.

So every start in `[L..p]` belongs to the same GCD block.

---

### Step E: evaluate the block

```java
if (L <= limit) {
    long sum = pref[r + 1] - pref[L];
    ans = Math.max(ans, sum * g);
}
```

If the earliest start is valid, then it is the best one in the block, because it gives the largest sum.

---

### Step F: move left to the next block

```java
p = L - 1;
```

That skips the whole processed block and continues with the next one.

---

## Prefix construction

```java
private void buildPrefix(int[] nums)
```

Builds `pref` so subarray sums can be answered in `O(1)` time.

---

## Sparse table construction

```java
private void buildSparseTable(int[] nums)
```

Builds all power-of-two GCD segments.

This takes `O(n log n)` time and `O(n log n)` space.

---

## Range GCD query

```java
private int queryGcd(int l, int r)
```

Uses two overlapping sparse table blocks to answer `gcd(nums[l..r])` in `O(1)` time.

---

## Euclidean GCD

```java
private int gcd(int a, int b)
```

Standard iterative Euclidean algorithm.

---

# 14. Complexity analysis

Let:

- `n` = length of `nums`
- `V` = maximum value in `nums`

## Prefix sums

- **Time:** `O(n)`
- **Space:** `O(n)`

---

## Sparse table preprocessing

There are `O(log n)` levels, each with `O(n)` cells.

- **Time:** `O(n log n)`
- **Space:** `O(n log n)`

---

## GCD query

Each sparse-table query is:

- **Time:** `O(1)`

---

## Binary search per GCD block

For each GCD block, finding its left boundary takes:

- **Time:** `O(log n)`

because each midpoint check uses an `O(1)` GCD query.

---

## Number of GCD blocks per right endpoint

For fixed `r`, as the left boundary moves left, the GCD only changes to divisors of previous values.

So the number of distinct GCD values is small.

In standard analysis, this is commonly treated as around:

- **`O(log V)`** distinct GCD blocks per right endpoint

---

## Total time

For each `r`:

- about `O(log V)` GCD blocks
- each block costs `O(log n)` because of binary search

So:

- main loop = `O(n log V log n)`

Including preprocessing:

- total = `O(n log n + n log V log n)`

Usually summarized as:

**Time complexity: `O(n log V log n)`**

---

## Total space

- prefix sums: `O(n)`
- log table: `O(n)`
- sparse table: `O(n log n)`

So:

**Space complexity: `O(n log n)`**

---

# 15. Important caveat

This approach is correct for the version where:

- `nums` contains positive integers
- we want the maximum of `sum(subarray) * gcd(subarray)`
- subarray length must be at least `k`

The positivity assumption is crucial for the argument that the earliest valid start is best within a fixed GCD block.

---
