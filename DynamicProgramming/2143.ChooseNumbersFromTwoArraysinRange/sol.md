# 2143. Choose Numbers From Two Arrays in Range — Detailed Explanation

## Problem Statement

You are given two `0-indexed` arrays `nums1` and `nums2`, both of length `n`.

For any range `[l, r]`, where `0 <= l <= r < n`, you must choose **exactly one** number at each index `i` in that range:

- either `nums1[i]`
- or `nums2[i]`

The range is called **balanced** if:

- the sum of chosen values from `nums1` equals
- the sum of chosen values from `nums2`

Two balanced ranges are considered different if:

- their `l` differs, or
- their `r` differs, or
- at some index, one chooses `nums1[i]` and the other chooses `nums2[i]`

Return the number of different balanced ranges modulo:

```text
10^9 + 7
```

---

# Core Transformation

This problem becomes much easier once we stop thinking in terms of:

- "sum chosen from nums1"
- "sum chosen from nums2"

and instead track a **difference**.

For each chosen index `i`:

- if we choose `nums1[i]`, we add `+nums1[i]`
- if we choose `nums2[i]`, we add `-nums2[i]`

So define:

```text
diff = (sum of chosen nums1 values) - (sum of chosen nums2 values)
```

A range is balanced exactly when:

```text
diff = 0
```

So now the question becomes:

> For how many contiguous ranges `[l, r]` are there choice assignments whose signed total is `0`?

---

# Solution 1 — Brute Force Over All Subarrays + HashMap DP

This is the most direct dynamic programming formulation.

## Idea

For every starting index `l`:

- expand the range one element at a time to the right
- maintain a DP map:
  - `dp[diff] = number of ways to achieve this difference for the current range`

At each new index `r`, every previous difference can transition in two ways:

- choose `nums1[r]` → `diff + nums1[r]`
- choose `nums2[r]` → `diff - nums2[r]`

After processing range `[l, r]`, the number of balanced assignments is:

```text
dp[0]
```

We add that to the answer.

---

## Why It Works

For a fixed range `[l, r]`, every valid assignment corresponds to a sequence of choices:

- choose from `nums1`
- or choose from `nums2`

The DP map enumerates all possible signed differences and counts how many assignments produce each one.

If the final difference is `0`, that assignment is balanced.

Doing this for every subarray counts all balanced ranges.

---

## Brute Force HashMap DP Code (TLE)

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    public int countSubranges(int[] nums1, int[] nums2) {
        int n = nums1.length;
        long ans = 0;

        for (int l = 0; l < n; l++) {
            Map<Integer, Long> dp = new HashMap<>();
            dp.put(0, 1L);

            for (int r = l; r < n; r++) {
                Map<Integer, Long> next = new HashMap<>();

                for (Map.Entry<Integer, Long> e : dp.entrySet()) {
                    int diff = e.getKey();
                    long ways = e.getValue();

                    int choose1 = diff + nums1[r];
                    int choose2 = diff - nums2[r];

                    next.put(choose1, (next.getOrDefault(choose1, 0L) + ways) % MOD);
                    next.put(choose2, (next.getOrDefault(choose2, 0L) + ways) % MOD);
                }

                dp = next;
                ans = (ans + dp.getOrDefault(0, 0L)) % MOD;
            }
        }

        return (int) ans;
    }
}
```

---

## Example Walkthrough for the Brute Force Idea

Take:

```text
nums1 = [1,2,5]
nums2 = [2,6,3]
```

### Range `[0,0]`

Start:

```text
dp = {0: 1}
```

At index `0`:

- choose `nums1[0] = 1` → diff `+1`
- choose `nums2[0] = 2` → diff `-2`

So:

```text
dp = {1: 1, -2: 1}
```

No `0`, so no balanced assignment here.

---

### Range `[0,1]`

Extend from previous map.

From diff `1`:

- choose `nums1[1] = 2` → `3`
- choose `nums2[1] = 6` → `-5`

From diff `-2`:

- choose `nums1[1] = 2` → `0`
- choose `nums2[1] = 6` → `-8`

So:

```text
dp = {3:1, -5:1, 0:1, -8:1}
```

Now `dp[0] = 1`, so there is exactly one balanced assignment for `[0,1]`.

That is:

- choose `nums2[0] = 2`
- choose `nums1[1] = 2`

Balanced because:

```text
2 = 2
```

---

## Complexity of Solution 1

This is the issue.

There are:

```text
O(n^2)
```

subarrays.

For each subarray, the number of difference states can grow significantly.

So this solution is too slow in practice and leads to **TLE**.

### Time Complexity

Roughly:

```text
O(n^2 * states)
```

and the constant factor is bad because of heavy `HashMap` usage.

### Space Complexity

```text
O(states)
```

for one subarray's DP map.

---

# Why the First Solution TLEs

The waste is that it recomputes DP from scratch for every starting point `l`.

That means a lot of repeated work.

For example, ranges:

- `[0, 5]`
- `[1, 5]`
- `[2, 5]`

all end at the same right boundary, but the brute-force method rebuilds their state transitions again and again.

We want a DP that **reuses work across subarrays**.

---

# Solution 2 — Optimized DP by Ending Position

This is the accepted approach.

## Main Insight

Instead of fixing the left boundary and expanding right, do the opposite:

At every index `i`, count all balanced ranges that **end at `i`**.

Let:

```text
dp[diff] = number of ways to form a contiguous range ending at i-1 with signed total = diff
```

When we move to index `i`, every range ending at `i` is either:

1. a brand new range `[i, i]`, or
2. an extension of some range ending at `i-1`

So we can build `next` from `dp`.

---

## Transition

Suppose a previous range ending at `i-1` has difference `diff`.

At index `i`, we can extend it by:

- choosing `nums1[i]` → new difference `diff + nums1[i]`
- choosing `nums2[i]` → new difference `diff - nums2[i]`

Also, we must consider starting a new range at `i`:

- choose `nums1[i]` → difference `+nums1[i]`
- choose `nums2[i]` → difference `-nums2[i]`

After building `next`, the number of balanced ranges ending at `i` is:

```text
next[0]
```

Add that to the answer.

Then set:

```text
dp = next
```

and continue.

---

## Why This Works

Every contiguous range ending at `i` belongs to exactly one of these two groups:

- it starts at `i`
- or it started earlier and was already a valid ending-at-`i-1` range before extension

So this DP counts every contiguous range exactly once.

It is much more efficient because it reuses all previously computed ending states.

---

## Range of Possible Differences

Since:

```text
0 <= nums1[i], nums2[i] <= 100
n <= 100
```

the maximum absolute difference is bounded by:

```text
sum(nums1) + sum(nums2) <= 20000
```

So we can store differences in an array instead of a `HashMap`.

To handle negative differences, use an offset.

---

## Optimized Accepted Code

```java
class Solution {
    static final int MOD = 1_000_000_007;

    public int countSubranges(int[] nums1, int[] nums2) {
        int n = nums1.length;

        int sum = 0;
        for (int x : nums1) sum += x;
        for (int x : nums2) sum += x;

        int OFFSET = sum;
        int SIZE = 2 * sum + 1;

        long[] dp = new long[SIZE];
        long ans = 0;

        for (int i = 0; i < n; i++) {
            long[] next = new long[SIZE];

            // Start a new range at i
            next[OFFSET + nums1[i]] = (next[OFFSET + nums1[i]] + 1) % MOD;
            next[OFFSET - nums2[i]] = (next[OFFSET - nums2[i]] + 1) % MOD;

            // Extend all previous ranges ending at i - 1
            for (int d = 0; d < SIZE; d++) {
                if (dp[d] == 0) continue;

                int nd1 = d + nums1[i];
                int nd2 = d - nums2[i];

                next[nd1] = (next[nd1] + dp[d]) % MOD;
                next[nd2] = (next[nd2] + dp[d]) % MOD;
            }

            ans = (ans + next[OFFSET]) % MOD;
            dp = next;
        }

        return (int) ans;
    }
}
```

---

# Detailed Walkthrough of the Optimized Solution

Take:

```text
nums1 = [0,1]
nums2 = [1,0]
```

Expected answer:

```text
4
```

---

## Preprocessing

Total possible sum:

```text
sum(nums1) + sum(nums2) = 1 + 1 = 2
```

So:

```text
OFFSET = 2
SIZE = 5
```

Meaning array indices represent differences:

```text
index 0 -> diff -2
index 1 -> diff -1
index 2 -> diff  0
index 3 -> diff +1
index 4 -> diff +2
```

---

## Step `i = 0`

### Start new range `[0,0]`

- choose `nums1[0] = 0` → diff `0`
- choose `nums2[0] = 1` → diff `-1`

So:

```text
next[0] += 1
next[-1] += 1
```

In indexed form:

```text
next[2] = 1
next[1] = 1
```

There are no old ranges to extend yet.

Balanced ranges ending at `0`:

```text
next[OFFSET] = next[2] = 1
```

So answer becomes:

```text
1
```

This is the balanced range:

- `[0,0]`, choose `nums1[0] = 0`

---

## Step `i = 1`

Create fresh `next`.

### Start new range `[1,1]`

- choose `nums1[1] = 1` → diff `+1`
- choose `nums2[1] = 0` → diff `0`

So new contributions:

```text
next[+1] += 1
next[0] += 1
```

### Extend old ranges from `dp`

Old `dp` had:

- diff `0` with count `1`
- diff `-1` with count `1`

From diff `0`:

- choose `nums1[1] = 1` → diff `+1`
- choose `nums2[1] = 0` → diff `0`

From diff `-1`:

- choose `nums1[1] = 1` → diff `0`
- choose `nums2[1] = 0` → diff `-1`

Now balanced count at this step is all ways with diff `0`.

That gives:

- `[1,1]` choosing `nums2[1]`
- `[0,1]` choosing `nums1[0], nums2[1]`
- `[0,1]` choosing `nums2[0], nums1[1]`

Together with previous answer `1`, total becomes `4`.

---

# Correctness Intuition

## Why signed difference is the right model

Every choice contributes to exactly one side:

- choosing from `nums1` helps the left side
- choosing from `nums2` helps the right side

Subtracting the right-side sum from the left-side sum compresses the condition into a single target:

```text
difference = 0
```

This is a standard and very powerful DP trick.

---

## Why `dp` stores ranges ending at `i-1`

This is the contiguous-subarray part.

When you move from `i-1` to `i`, the only subarrays you can extend are those that ended exactly at `i-1`.

That is why the state naturally becomes:

```text
all ways for contiguous ranges ending at previous index
```

This avoids mixing non-contiguous selections.

---

## Why starting new ranges separately is necessary

If you only extend older ranges, you miss ranges like:

```text
[i, i]
```

or more generally any range whose left endpoint is exactly `i`.

So every step must include:

- start new range at `i`
- extend existing ranges

Both are essential.

---

# Complexity Comparison

## Solution 1 — Brute Force HashMap DP

### Time Complexity

```text
O(n^2 * states)
```

This is too slow.

### Space Complexity

```text
O(states)
```

---

## Solution 2 — Optimized Ending-Position DP

Let:

```text
S = sum(nums1) + sum(nums2)
```

Then:

### Time Complexity

```text
O(n * S)
```

Because for each index, we scan all possible differences once.

### Space Complexity

```text
O(S)
```

for the DP arrays.

With constraints:

```text
n <= 100
nums1[i], nums2[i] <= 100
S <= 20000
```

this is completely fine.

---

# Practical Takeaway

The first solution is useful because it exposes the true DP structure very clearly:

- choose `+nums1[i]` or `-nums2[i]`
- balanced means total signed difference is `0`

But it is not efficient enough.

The second solution keeps the same core DP idea while reorganizing the traversal to reuse work across subarrays. That is the key optimization.

---

# Final Recommendation

Use the optimized ending-position DP.

It is:

- simpler to bound
- array-based instead of `HashMap`-heavy
- fast enough for the constraints
- directly accepted

---

# Both Solutions Together

## Solution 1 — Easy to Understand, But TLE

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    public int countSubranges(int[] nums1, int[] nums2) {
        int n = nums1.length;
        long ans = 0;

        for (int l = 0; l < n; l++) {
            Map<Integer, Long> dp = new HashMap<>();
            dp.put(0, 1L);

            for (int r = l; r < n; r++) {
                Map<Integer, Long> next = new HashMap<>();

                for (Map.Entry<Integer, Long> e : dp.entrySet()) {
                    int diff = e.getKey();
                    long ways = e.getValue();

                    int choose1 = diff + nums1[r];
                    int choose2 = diff - nums2[r];

                    next.put(choose1, (next.getOrDefault(choose1, 0L) + ways) % MOD);
                    next.put(choose2, (next.getOrDefault(choose2, 0L) + ways) % MOD);
                }

                dp = next;
                ans = (ans + dp.getOrDefault(0, 0L)) % MOD;
            }
        }

        return (int) ans;
    }
}
```

## Solution 2 — Optimized and Accepted

```java
class Solution {
    static final int MOD = 1_000_000_007;

    public int countSubranges(int[] nums1, int[] nums2) {
        int n = nums1.length;

        int sum = 0;
        for (int x : nums1) sum += x;
        for (int x : nums2) sum += x;

        int OFFSET = sum;
        int SIZE = 2 * sum + 1;

        long[] dp = new long[SIZE];
        long ans = 0;

        for (int i = 0; i < n; i++) {
            long[] next = new long[SIZE];

            next[OFFSET + nums1[i]] = (next[OFFSET + nums1[i]] + 1) % MOD;
            next[OFFSET - nums2[i]] = (next[OFFSET - nums2[i]] + 1) % MOD;

            for (int d = 0; d < SIZE; d++) {
                if (dp[d] == 0) continue;

                int nd1 = d + nums1[i];
                int nd2 = d - nums2[i];

                next[nd1] = (next[nd1] + dp[d]) % MOD;
                next[nd2] = (next[nd2] + dp[d]) % MOD;
            }

            ans = (ans + next[OFFSET]) % MOD;
            dp = next;
        }

        return (int) ans;
    }
}
```
