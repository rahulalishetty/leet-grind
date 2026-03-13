# 3444. Minimum Increments for Target Multiples in an Array — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int minimumIncrements(int[] nums, int[] target) {

    }
}
```

---

# Problem Restatement

We are given:

- an array `nums`
- an array `target`

In one operation, we may increment any one element of `nums` by `1`.

We want the minimum number of increments so that:

> for every value `t` in `target`, there exists at least one element in `nums` that is a multiple of `t`.

A single element in `nums` may serve multiple targets at once if its value is a multiple of all of them.

That is the key subtlety.

---

# Core Insight

Since one incremented number can cover multiple targets, we should not think target-by-target independently.

Example:

```text
nums = [8,4]
target = [10,5]
```

If we increment `8 -> 10`, then:

- `10` is a multiple of `10`
- `10` is also a multiple of `5`

So one modified element covers both targets.

This means the problem is really a **set cover / bitmask DP** problem over the small target array.

And the constraint:

```text
target.length <= 4
```

is the big simplification.

---

# Key Reduction: Covering Subsets of Target

Suppose we decide that one element `nums[i]` should cover a subset of targets.

If that subset is:

```text
{ target[j1], target[j2], ... }
```

then the final value of `nums[i]` must be a multiple of all those targets.

So it must be a multiple of:

```text
LCM(target[j1], target[j2], ...)
```

The smallest value we can increment `nums[i]` to is:

```text
ceil(nums[i] / lcm) * lcm
```

So the cost is:

```text
cost = nextMultiple(lcm) - nums[i]
```

Thus, for each `nums[i]` and for each subset of `target`, we can compute the minimum cost to make `nums[i]` cover exactly that subset.

Once we know these costs, the problem becomes:

> choose coverage subsets for some elements of `nums` so that the union covers all target indices, with minimum total cost.

That is a standard DP over masks.

---

# Approach 1 — Bitmask DP Over Target Coverage (Recommended)

## Idea

Let `m = target.length`, so:

```text
m <= 4
```

There are only:

```text
2^m
```

subsets of target.

### Step 1: Precompute LCM for every target subset

For each mask from `1` to `(1 << m) - 1`, compute:

```text
lcm[mask]
```

### Step 2: For each `nums[i]`, compute the cost to cover every subset

If subset mask has LCM `L`, then the minimum increment cost is:

```text
cost(i, mask) = smallest multiple of L >= nums[i]  - nums[i]
```

### Step 3: DP over coverage masks

Let:

```text
dp[mask] = minimum cost to cover exactly the target set represented by mask
```

Initially:

```text
dp[0] = 0
```

For each `nums[i]`, we may choose to use it to cover any subset `sub`, and update:

```text
newDp[mask | sub] = min(newDp[mask | sub], dp[mask] + cost(i, sub))
```

At the end, answer is:

```text
dp[(1 << m) - 1]
```

because that mask means all targets are covered.

---

## Why this works

Each array element can be left unused, or assigned to cover some subset of targets.

Since target count is tiny, the set of covered targets is the only global state we need.

The DP tries all ways of combining per-element subset coverage costs to cover the full target set with minimum total increments.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumIncrements(int[] nums, int[] target) {
        int m = target.length;
        int fullMask = (1 << m) - 1;

        long[] lcm = new long[1 << m];
        lcm[0] = 1;

        for (int mask = 1; mask <= fullMask; mask++) {
            long cur = 1;
            for (int j = 0; j < m; j++) {
                if (((mask >> j) & 1) == 1) {
                    cur = lcm(cur, target[j]);
                }
            }
            lcm[mask] = cur;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dp = new long[1 << m];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int x : nums) {
            long[] ndp = dp.clone();

            for (int mask = 0; mask <= fullMask; mask++) {
                if (dp[mask] == INF) continue;

                for (int sub = 1; sub <= fullMask; sub++) {
                    long cost = incrementCost(x, lcm[sub]);
                    int nextMask = mask | sub;
                    ndp[nextMask] = Math.min(ndp[nextMask], dp[mask] + cost);
                }
            }

            dp = ndp;
        }

        return (int) dp[fullMask];
    }

    private long incrementCost(long x, long lcm) {
        long r = x % lcm;
        if (r == 0) return 0;
        return lcm - r;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Complexity

Let:

- `n = nums.length`
- `m = target.length`

Then:

- subsets = `2^m <= 16`

DP complexity:

```text
Time:  O(n * 2^m * 2^m)
Space: O(2^m)
```

Since `m <= 4`, this is effectively:

```text
O(n * 256)
```

which is excellent for `n <= 5 * 10^4`.

---

# Approach 2 — Shortest Path / Relaxation View on Masks

## Idea

The same solution can be viewed as shortest-path relaxation on subset masks.

Each `nums[i]` offers transitions:

```text
mask -> mask | sub
```

with edge weight:

```text
cost(i, sub)
```

Processing elements one by one is equivalent to repeated relaxation.

This is not a fundamentally different algorithm, but it is a useful way to understand the DP.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumIncrements(int[] nums, int[] target) {
        int m = target.length;
        int full = (1 << m) - 1;

        long[] subsetLcm = new long[1 << m];
        subsetLcm[0] = 1;

        for (int mask = 1; mask <= full; mask++) {
            long cur = 1;
            for (int i = 0; i < m; i++) {
                if (((mask >> i) & 1) == 1) {
                    cur = lcm(cur, target[i]);
                }
            }
            subsetLcm[mask] = cur;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[1 << m];
        Arrays.fill(dist, INF);
        dist[0] = 0;

        for (int x : nums) {
            long[] next = dist.clone();

            for (int mask = 0; mask <= full; mask++) {
                if (dist[mask] == INF) continue;

                for (int sub = 1; sub <= full; sub++) {
                    long w = needToReachMultiple(x, subsetLcm[sub]);
                    next[mask | sub] = Math.min(next[mask | sub], dist[mask] + w);
                }
            }

            dist = next;
        }

        return (int) dist[full];
    }

    private long needToReachMultiple(long x, long mult) {
        long mod = x % mult;
        return mod == 0 ? 0 : mult - mod;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
Time:  O(n * 2^m * 2^m)
Space: O(2^m)
```

---

# Approach 3 — Greedy by Closest Target Multiple (Incorrect)

## Idea

A tempting approach is:

- for each target, find the cheapest `nums[i]` to increment into one of its multiples
- combine greedily

This fails because one `nums[i]` can cover multiple targets simultaneously.

So greedy per-target optimization misses important shared coverage opportunities.

---

## Counterexample

```text
nums = [8, 4]
target = [10, 5]
```

Greedy might separately think:

- cover `10` with `8 -> 10` cost 2
- cover `5` with `4 -> 5` cost 1

Total = 3

But optimal is:

```text
8 -> 10
```

cost = 2, and `10` covers both `10` and `5`.

So greedy is wrong.

---

# Approach 4 — Brute Force Assign Every nums[i] to a Target Subset (Too Big Conceptually)

## Idea

Each number could be assigned to:

- unused
- any non-empty subset of targets

That is:

```text
1 + (2^m - 1)
```

choices per element.

Brute force over all elements would therefore be exponential in `n`, which is impossible.

The mask DP works because it compresses the global state down to just which targets are already covered.

---

# Detailed Walkthrough

## Example 1

```text
nums = [1,2,3]
target = [4]
```

There is only one target:

```text
4
```

So we need at least one number in `nums` to become a multiple of `4`.

Costs:

- `1 -> 4` cost 3
- `2 -> 4` cost 2
- `3 -> 4` cost 1

Best is:

```text
3 -> 4
```

So answer is:

```text
1
```

---

## Example 2

```text
nums = [8,4]
target = [10,5]
```

Subsets of target:

- `{10}` -> LCM = 10
- `{5}` -> LCM = 5
- `{10,5}` -> LCM = 10

Now for `8`:

- cost to cover `{10}` = 2
- cost to cover `{5}` = 2 (8 -> 10)
- cost to cover `{10,5}` = 2

For `4`:

- cost to cover `{10}` = 6
- cost to cover `{5}` = 1 (4 -> 5)
- cost to cover `{10,5}` = 6

The best overall choice is:

```text
8 -> 10
```

which covers both targets with total cost `2`.

---

## Example 3

```text
nums = [7,9,10]
target = [7]
```

Since `7` already exists in `nums`, and `7` is a multiple of itself, the target is already covered.

So answer is:

```text
0
```

---

# Important Correctness Argument

For each `nums[i]`, if we decide it should cover a subset of targets, then the cheapest way to do so is to increment it to the smallest multiple of the subset’s LCM that is at least `nums[i]`.

So every element independently contributes a known cost for every subset.

The only interaction between elements is which targets are already covered.

That means the complete global state is exactly the target-coverage mask.

Thus the DP over masks is sufficient and complete.

---

# Common Pitfalls

## 1. Forgetting that one number can cover multiple targets

This is the most important property of the problem.

Always think in terms of subsets of targets, not individual targets only.

---

## 2. Using int for LCM without care

LCMs of up to 4 numbers around `10^4` can grow large.

Use `long`.

---

## 3. Overcomplicating because nums is large

`nums.length` is large, but `target.length <= 4`, which is what makes the bitmask DP possible.

---

## 4. Trying greedy

Greedy does not handle shared coverage correctly.

---

# Best Approach

## Recommended: Bitmask DP over covered targets

This is the cleanest and most efficient solution because:

- target size is tiny
- subset LCMs can be precomputed
- each number’s contribution to each subset is easy to compute
- the DP naturally handles shared coverage

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int minimumIncrements(int[] nums, int[] target) {
        int m = target.length;
        int fullMask = (1 << m) - 1;

        long[] lcm = new long[1 << m];
        lcm[0] = 1;

        for (int mask = 1; mask <= fullMask; mask++) {
            long cur = 1;
            for (int j = 0; j < m; j++) {
                if (((mask >> j) & 1) == 1) {
                    cur = lcm(cur, target[j]);
                }
            }
            lcm[mask] = cur;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dp = new long[1 << m];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int x : nums) {
            long[] ndp = dp.clone();

            for (int mask = 0; mask <= fullMask; mask++) {
                if (dp[mask] == INF) continue;

                for (int sub = 1; sub <= fullMask; sub++) {
                    long cost = incrementCost(x, lcm[sub]);
                    int nextMask = mask | sub;
                    ndp[nextMask] = Math.min(ndp[nextMask], dp[mask] + cost);
                }
            }

            dp = ndp;
        }

        return (int) dp[fullMask];
    }

    private long incrementCost(long x, long lcm) {
        long r = x % lcm;
        return r == 0 ? 0 : (lcm - r);
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

# Complexity Summary

Let:

- `n = nums.length`
- `m = target.length <= 4`

Then:

```text
Time:  O(n * 2^m * 2^m)
Space: O(2^m)
```

Since `m <= 4`, this is effectively linear in `n`.

---

# Final Takeaway

The critical reduction is:

- one incremented number can satisfy multiple targets
- the right way to model that is by target subsets
- each subset corresponds to an LCM requirement
- then a bitmask DP over covered targets gives the minimum total cost
