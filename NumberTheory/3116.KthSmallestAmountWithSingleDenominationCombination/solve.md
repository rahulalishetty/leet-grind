# 3116. Kth Smallest Amount With Single Denomination Combination — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public long findKthSmallest(int[] coins, int k) {

    }
}
```

---

# Problem Restatement

We are given:

- an array `coins` of distinct denominations
- an integer `k`

For each denomination `c`, we may form only amounts of the form:

```text
c, 2c, 3c, 4c, ...
```

We are **not** allowed to mix different denominations together.

So the full set of valid amounts is the union of multiples of each coin.

We must return the:

```text
k-th smallest distinct amount
```

---

# Core Insight

For a given number `x`, we can ask:

> How many valid amounts are `<= x`?

If we can answer that efficiently, then we can use **binary search on the answer**.

So the problem becomes a classic:

- binary search over possible amount
- counting function `count(x)` = number of distinct valid amounts `<= x`

---

# Key Challenge in Counting

A number can be a multiple of more than one coin.

Example:

```text
coins = [2, 5]
```

Then:

- `10` is a multiple of `2`
- `10` is also a multiple of `5`

So if we simply sum:

```text
x / 2 + x / 5
```

we would double-count common multiples.

This is exactly what **Inclusion-Exclusion Principle** is for.

---

# Inclusion-Exclusion Principle

For a set of coin denominations, let:

```text
A_i = set of multiples of coins[i] that are <= x
```

We want:

```text
|A_1 ∪ A_2 ∪ ... ∪ A_n|
```

By inclusion-exclusion:

```text
sum of singles
- sum of pair intersections
+ sum of triple intersections
- ...
```

The size of the intersection of a subset of coins is:

```text
x / lcm(subset)
```

So the counting function becomes:

```text
count(x) = Σ (-1)^(subset_size+1) * floor(x / lcm(subset))
```

over all non-empty subsets.

Because `coins.length <= 15`, enumerating all subsets is feasible:

```text
2^15 = 32768
```

That is small enough.

---

# Approach 1 — Binary Search + Inclusion-Exclusion (Recommended)

## Idea

1. Precompute LCM for every non-empty subset of `coins`
2. For a candidate `mid`, compute how many valid amounts are `<= mid` using inclusion-exclusion
3. Binary search for the smallest `x` such that:

```text
count(x) >= k
```

That `x` is the answer.

---

## Why binary search works

The function:

```text
count(x)
```

is monotonic non-decreasing.

As `x` grows, the number of valid amounts `<= x` never decreases.

So binary search is valid.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long findKthSmallest(int[] coins, int k) {
        int n = coins.length;
        List<long[]> subsets = new ArrayList<>();

        for (int mask = 1; mask < (1 << n); mask++) {
            long lcm = 1;
            boolean valid = true;

            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    lcm = lcm(lcm, coins[i]);
                    if (lcm > Long.MAX_VALUE / 2) {
                        valid = false;
                        break;
                    }
                }
            }

            if (valid) {
                int bits = Integer.bitCount(mask);
                subsets.add(new long[]{lcm, bits});
            }
        }

        long left = 1, right = (long) Arrays.stream(coins).min().getAsInt() * k;

        while (left < right) {
            long mid = left + (right - left) / 2;
            long cnt = count(mid, subsets);

            if (cnt >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private long count(long x, List<long[]> subsets) {
        long total = 0;

        for (long[] entry : subsets) {
            long lcm = entry[0];
            int bits = (int) entry[1];

            long add = x / lcm;
            if ((bits & 1) == 1) {
                total += add;
            } else {
                total -= add;
            }
        }

        return total;
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

```text
n = coins.length
```

Then:

- number of subsets = `2^n - 1`
- each count query processes all subsets
- binary search takes about `O(log answer)`

So:

```text
Time:  O(2^n * n + 2^n * log answer)
Space: O(2^n)
```

Since:

```text
n <= 15
```

this is easily fast enough.

---

# Approach 2 — Binary Search + On-the-Fly Inclusion-Exclusion DFS

## Idea

Instead of precomputing all subset LCMs, we can compute the inclusion-exclusion count using DFS recursion on the fly.

For each candidate `x`, recursively generate subsets:

- include or exclude each coin
- maintain current LCM
- add/subtract `x / lcm` based on subset size parity

This avoids storing all subset LCMs explicitly.

It is mathematically the same as Approach 1.

---

## Java Code

```java
class Solution {
    public long findKthSmallest(int[] coins, int k) {
        long left = 1;
        long minCoin = Integer.MAX_VALUE;
        for (int c : coins) minCoin = Math.min(minCoin, c);
        long right = minCoin * (long) k;

        while (left < right) {
            long mid = left + (right - left) / 2;
            long cnt = dfsCount(coins, 0, 1L, 0, mid);

            if (cnt >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private long dfsCount(int[] coins, int idx, long curLcm, int chosen, long limit) {
        long total = 0;

        for (int i = idx; i < coins.length; i++) {
            long nextLcm = lcm(curLcm, coins[i]);
            if (nextLcm > limit || nextLcm <= 0) continue;

            long contrib = limit / nextLcm;
            if ((chosen + 1) % 2 == 1) {
                total += contrib;
            } else {
                total -= contrib;
            }

            total += dfsCount(coins, i + 1, nextLcm, chosen + 1, limit);
        }

        return total;
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

Still essentially:

```text
Time:  O(2^n * log answer)
Space: O(n) recursion stack
```

This is also fully acceptable.

---

# Approach 3 — Heap / Merge of Multiple Arithmetic Progressions (Conceptual, but Tricky)

## Idea

Each coin produces an infinite sorted arithmetic progression:

```text
coin, 2*coin, 3*coin, ...
```

One might try to merge these sequences using a min-heap, similar to merging `k` sorted lists.

Example:

- for coin `2`: `2,4,6,8,...`
- for coin `5`: `5,10,15,...`

Push the first multiple of every coin into a heap, then repeatedly pop and advance.

---

## Why this is not ideal

The trouble is **duplicate handling**.

The same amount can come from multiple coins:

```text
10 = 2*5 = 5*2
```

So we need deduplication.

Also, `k` can be as large as:

```text
2 * 10^9
```

which makes heap simulation infeasible.

So this is a useful intuition, but not a practical final solution.

---

# Approach 4 — Brute Force Generation of All Amounts (Impossible)

## Idea

Generate valid amounts one by one, deduplicate, sort, and pick the `k-th`.

This is clearly impossible because:

- there are infinitely many valid amounts
- `k` can be enormous

So a direct generation approach is not viable.

---

# Detailed Walkthrough

## Example 2

```text
coins = [5, 2]
k = 7
```

Valid amounts are the union of:

- multiples of `5`
- multiples of `2`

So we want the 7th distinct number in:

```text
2, 4, 5, 6, 8, 10, 12, 14, 15, ...
```

Answer should be:

```text
12
```

Now suppose binary search checks `x = 12`.

Count numbers `<= 12`:

- multiples of `5`: `12 / 5 = 2` -> `{5,10}`
- multiples of `2`: `12 / 2 = 6` -> `{2,4,6,8,10,12}`

Naively total:

```text
2 + 6 = 8
```

But `10` is counted twice.

Intersection uses:

```text
lcm(5,2) = 10
12 / 10 = 1
```

So by inclusion-exclusion:

```text
8 - 1 = 7
```

Exactly 7 valid amounts are `<= 12`.

Thus 12 is indeed the 7th smallest.

---

# Important Correctness Argument

Let:

```text
f(x) = number of distinct valid amounts <= x
```

A value `v` is the answer iff:

- at least `k` valid amounts are `<= v`
- fewer than `k` valid amounts are `< v`

That is exactly the binary search condition:

```text
smallest x such that f(x) >= k
```

Since inclusion-exclusion counts `f(x)` exactly, binary search returns the correct answer.

---

# Common Pitfalls

## 1. Forgetting deduplication

Different coins may produce the same amount.

That is why inclusion-exclusion is necessary.

---

## 2. Using product instead of LCM in intersections

The common multiples of a subset of coins are multiples of their:

```text
LCM
```

not their product.

---

## 3. Overflow in LCM computation

Even though coin values are small, subset LCMs can grow.

Use:

```java
lcm(a, b) = a / gcd(a, b) * b
```

with `long`.

---

## 4. Trying heap simulation for large k

`k` is far too large for iterative generation.

Binary search on the answer is the right tool.

---

# Best Approach

## Recommended: Binary Search + Inclusion-Exclusion

This is the optimal approach because:

- the count of valid amounts `<= x` is monotonic
- inclusion-exclusion gives exact distinct counts
- `coins.length <= 15`, so subset enumeration is feasible

This is the intended and most robust solution.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public long findKthSmallest(int[] coins, int k) {
        int n = coins.length;
        List<long[]> subsets = new ArrayList<>();

        for (int mask = 1; mask < (1 << n); mask++) {
            long lcm = 1;
            boolean valid = true;

            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    lcm = lcm(lcm, coins[i]);
                    if (lcm <= 0) {
                        valid = false;
                        break;
                    }
                }
            }

            if (valid) {
                subsets.add(new long[]{lcm, Integer.bitCount(mask)});
            }
        }

        long minCoin = Integer.MAX_VALUE;
        for (int c : coins) minCoin = Math.min(minCoin, c);

        long left = 1;
        long right = minCoin * (long) k;

        while (left < right) {
            long mid = left + (right - left) / 2;
            long cnt = 0;

            for (long[] entry : subsets) {
                long lcm = entry[0];
                int bits = (int) entry[1];

                long add = mid / lcm;
                if ((bits & 1) == 1) cnt += add;
                else cnt -= add;
            }

            if (cnt >= k) right = mid;
            else left = mid + 1;
        }

        return left;
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

Let `n = coins.length`.

Because `n <= 15`, subset enumeration is feasible:

```text
Time:  O(2^n * n + 2^n * log answer)
Space: O(2^n)
```

This fits the constraints comfortably.

---

# Final Takeaway

The problem is not about constructing amounts directly.

The right perspective is:

1. define a counting function `f(x)` = number of valid distinct amounts `<= x`
2. compute `f(x)` using inclusion-exclusion on multiples
3. binary search for the smallest `x` with `f(x) >= k`

That turns an infinite-generation problem into a compact combinatorial counting problem.
