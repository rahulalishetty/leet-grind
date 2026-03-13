# 2862. Maximum Element-Sum of a Complete Subset of Indices — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public long maximumSum(List<Integer> nums) {

    }
}
```

---

# Problem Restatement

We are given a **1-indexed** array `nums`.

We want to choose a subset of indices such that for **every pair** of selected indices `i` and `j`:

```text
i * j
```

is a perfect square.

Among all such valid subsets, we want the **maximum possible sum** of the selected elements.

We return that maximum sum.

---

# Core Insight

The condition is on the **indices**, not the values.

So the real problem is:

> Which indices can coexist in the same subset?

This is a number theory question.

---

# Key Number Theory Observation

Every positive integer can be written uniquely as:

```text
x = s * t^2
```

where:

- `s` is square-free
- `t` is an integer

The square-free part `s` is the product of primes whose exponent is odd.

This is often called the **square-free kernel** or **square-free part** of the number.

---

# Why the Square-Free Part Matters

Take two indices:

```text
i = s1 * a^2
j = s2 * b^2
```

Then:

```text
i * j = s1 * s2 * (ab)^2
```

For `i * j` to be a perfect square, the square-free part must disappear.

That happens **iff**:

```text
s1 = s2
```

So two indices can be in the same valid subset exactly when they have the same square-free part.

That means:

> Every complete subset is exactly a group of indices sharing the same square-free part.

So the problem becomes:

1. compute square-free part of each index `1..n`
2. group indices by that value
3. sum `nums[index - 1]` inside each group
4. return the maximum group sum

This is the entire solution.

---

# Approach 1 — Group Indices by Square-Free Part (Recommended)

## Idea

For each index `i` from `1` to `n`:

1. compute the square-free part of `i`
2. add `nums[i - 1]` to that group's sum

Finally, return the largest group sum.

---

## Why this works

If two indices have the same square-free part, then their product is a perfect square.

If two indices have different square-free parts, then their product is not a perfect square.

Therefore a valid complete subset must be fully contained inside one square-free-part group, and every such group is valid.

So the optimal answer is just the maximum sum among these groups.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long maximumSum(List<Integer> nums) {
        int n = nums.size();
        Map<Integer, Long> groupSum = new HashMap<>();
        long ans = 0;

        for (int i = 1; i <= n; i++) {
            int key = squareFreePart(i);
            long sum = groupSum.getOrDefault(key, 0L) + nums.get(i - 1);
            groupSum.put(key, sum);
            ans = Math.max(ans, sum);
        }

        return ans;
    }

    private int squareFreePart(int x) {
        int result = 1;

        for (int p = 2; p * p <= x; p++) {
            int count = 0;
            while (x % p == 0) {
                x /= p;
                count++;
            }

            if ((count & 1) == 1) {
                result *= p;
            }
        }

        if (x > 1) {
            result *= x;
        }

        return result;
    }
}
```

---

## Complexity

Let:

```text
n = nums.length
```

For each index `i`, we compute its square-free part by trial division up to `sqrt(i)`.

So total complexity is roughly:

```text
Time:  O(n * sqrt(n))
Space: O(n)
```

This is acceptable for:

```text
n <= 10^4
```

and is very simple to implement.

---

# Approach 2 — Precompute Smallest Prime Factor (SPF) for Faster Square-Free Parts

## Idea

Instead of factorizing every index by trial division, we precompute the **smallest prime factor** for all numbers up to `n`.

Then we can factorize each index much faster.

The grouping logic stays exactly the same:

- compute square-free part
- group indices by it
- take maximum sum

---

## Why SPF helps

With SPF:

- each factorization becomes much faster
- prime exponents are extracted using repeated division by the smallest prime factor
- this is cleaner if you want a more optimized implementation

---

## Java Code

```java
import java.util.*;

class Solution {
    public long maximumSum(List<Integer> nums) {
        int n = nums.size();
        int[] spf = buildSPF(n);

        Map<Integer, Long> groupSum = new HashMap<>();
        long ans = 0;

        for (int i = 1; i <= n; i++) {
            int key = squareFreePart(i, spf);
            long sum = groupSum.getOrDefault(key, 0L) + nums.get(i - 1);
            groupSum.put(key, sum);
            ans = Math.max(ans, sum);
        }

        return ans;
    }

    private int[] buildSPF(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            spf[i] = i;
        }

        for (int i = 2; i * i <= n; i++) {
            if (spf[i] == i) {
                for (int j = i * i; j <= n; j += i) {
                    if (spf[j] == j) {
                        spf[j] = i;
                    }
                }
            }
        }

        return spf;
    }

    private int squareFreePart(int x, int[] spf) {
        int result = 1;

        while (x > 1) {
            int p = spf[x];
            int count = 0;

            while (x % p == 0) {
                x /= p;
                count++;
            }

            if ((count & 1) == 1) {
                result *= p;
            }
        }

        return result;
    }
}
```

---

## Complexity

- SPF preprocessing:

```text
O(n log log n)
```

- factorizing all indices using SPF:

```text
O(n log n)
```

Overall:

```text
Time:  O(n log log n + n log n)
Space: O(n)
```

For `n <= 10^4`, this is also easily fast enough.

---

# Approach 3 — Enumerate Multiples of Perfect Squares (Alternative View)

## Idea

If an index has square-free part `s`, then all indices in its group look like:

```text
s * 1^2, s * 2^2, s * 3^2, ...
```

as long as they stay within `n`.

So instead of factorizing every index and grouping by kernel, we can iterate over possible square-free bases `s` and add all positions:

```text
s * j^2
```

This is mathematically elegant, though a bit less direct than grouping by square-free part.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long maximumSum(List<Integer> nums) {
        int n = nums.size();
        boolean[] isSquareFree = new boolean[n + 1];
        Arrays.fill(isSquareFree, true);

        for (int p = 2; p * p <= n; p++) {
            int sq = p * p;
            for (int multiple = sq; multiple <= n; multiple += sq) {
                isSquareFree[multiple] = false;
            }
        }

        long ans = 0;

        for (int base = 1; base <= n; base++) {
            if (!isSquareFree[base]) continue;

            long sum = 0;
            for (long j = 1; base * j * j <= n; j++) {
                int idx = (int) (base * j * j);
                sum += nums.get(idx - 1);
            }

            ans = Math.max(ans, sum);
        }

        return ans;
    }
}
```

---

## Why this works

All indices with the same square-free part `base` are exactly:

```text
base * square
```

So we are enumerating each valid group directly.

---

## Complexity

The total work is around the number of `(base, j)` pairs with:

```text
base * j^2 <= n
```

which is manageable for `n <= 10^4`.

Still, the square-free grouping solution is usually simpler and clearer.

---

# Approach 4 — Check All Subsets / Pair Conditions Directly (Too Slow)

## Idea

A naive approach would be:

- generate subsets of indices
- for each subset, verify whether every pair product is a perfect square
- track the maximum sum

This is obviously infeasible except for tiny arrays.

---

## Why it fails

There are:

```text
2^n
```

subsets.

With:

```text
n <= 10^4
```

this is impossible.

So this is only a conceptual baseline.

---

# Detailed Walkthrough

## Example 1

```text
nums = [8,7,3,5,7,2,4,9]
```

Indices are `1..8`.

Compute square-free parts:

- `1 = 1 * 1^2` -> kernel `1`
- `2 = 2 * 1^2` -> kernel `2`
- `3 = 3 * 1^2` -> kernel `3`
- `4 = 1 * 2^2` -> kernel `1`
- `5 = 5 * 1^2` -> kernel `5`
- `6 = 6 * 1^2` -> kernel `6`
- `7 = 7 * 1^2` -> kernel `7`
- `8 = 2 * 2^2` -> kernel `2`

Group sums:

- kernel `1`: indices `1,4` -> `8 + 5 = 13`
- kernel `2`: indices `2,8` -> `7 + 9 = 16`
- kernel `3`: index `3` -> `3`
- kernel `5`: index `5` -> `7`
- kernel `6`: index `6` -> `2`
- kernel `7`: index `7` -> `4`

Maximum is:

```text
16
```

which matches the answer.

---

## Example 2

```text
nums = [8,10,3,8,1,13,7,9,4]
```

Indices `1..9`.

Square-free parts:

- `1` -> `1`
- `2` -> `2`
- `3` -> `3`
- `4` -> `1`
- `5` -> `5`
- `6` -> `6`
- `7` -> `7`
- `8` -> `2`
- `9` -> `1`

Groups:

- kernel `1`: indices `1,4,9` -> `8 + 8 + 4 = 20`
- kernel `2`: indices `2,8` -> `10 + 9 = 19`
- kernel `3`: index `3` -> `3`
- kernel `5`: index `5` -> `1`
- kernel `6`: index `6` -> `13`
- kernel `7`: index `7` -> `7`

Maximum is:

```text
20
```

which matches the example.

---

# Important Proof Idea

Why must all indices in a valid subset share the same square-free part?

Take any two selected indices `i` and `j`.

We require:

```text
i * j
```

to be a perfect square.

That means every prime exponent in the product must be even.

So the primes with odd exponent in `i` must match the primes with odd exponent in `j`.

That is exactly saying:

```text
squareFreePart(i) = squareFreePart(j)
```

Therefore all indices in a complete subset must lie in one square-free-part group.

And any one such group is valid, because any pair inside it multiplies to a square.

That proves the grouping characterization.

---

# Common Pitfalls

## 1. Thinking the condition depends on array values

It does not.
The condition depends only on the **indices**.

---

## 2. Checking pairwise perfect-square products directly

That leads to unnecessary quadratic work.

The square-free kernel reduces the condition to simple grouping.

---

## 3. Forgetting the array is 1-indexed

Index `1` corresponds to `nums.get(0)`, index `2` to `nums.get(1)`, and so on.

---

## 4. Confusing square-free part with prime factorization itself

We only care about primes with **odd exponent**.

For example:

- `12 = 2^2 * 3` -> square-free part = `3`
- `18 = 2 * 3^2` -> square-free part = `2`

---

# Best Approach

## Recommended: Group indices by square-free part

This is the best solution because:

- it captures the exact mathematical condition
- it is straightforward to implement
- it avoids pairwise checks entirely
- it runs comfortably within the constraints

The SPF version is a nice optimized implementation, but the direct trial-division grouping solution is often enough for `n <= 10^4`.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public long maximumSum(List<Integer> nums) {
        int n = nums.size();
        Map<Integer, Long> groupSum = new HashMap<>();
        long ans = 0;

        for (int i = 1; i <= n; i++) {
            int key = squareFreePart(i);
            long sum = groupSum.getOrDefault(key, 0L) + nums.get(i - 1);
            groupSum.put(key, sum);
            ans = Math.max(ans, sum);
        }

        return ans;
    }

    private int squareFreePart(int x) {
        int result = 1;

        for (int p = 2; p * p <= x; p++) {
            int count = 0;
            while (x % p == 0) {
                x /= p;
                count++;
            }

            if ((count & 1) == 1) {
                result *= p;
            }
        }

        if (x > 1) {
            result *= x;
        }

        return result;
    }
}
```

---

# Complexity Summary

For the recommended trial-division approach:

```text
Time:  O(n * sqrt(n))
Space: O(n)
```

For the SPF-optimized approach:

```text
Time:  O(n log log n + n log n)
Space: O(n)
```

Both are fully acceptable for:

```text
n <= 10^4
```

---

# Final Takeaway

The trick is to recognize a hidden number theory structure:

- two indices can coexist iff their product is a perfect square
- that happens iff they have the same square-free part

So the problem is not really about subsets at all.
It is just:

1. compute square-free part of each index
2. group indices by that value
3. sum values in each group
4. return the maximum sum
