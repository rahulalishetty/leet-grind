# 2584. Split the Array to Make Coprime Products — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int findValidSplit(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`.

A split at index `i` is valid if:

- left part = `nums[0..i]`
- right part = `nums[i+1..n-1]`

and the products of the two parts are coprime.

That means:

```text
gcd(product(left), product(right)) == 1
```

We must return the **smallest valid split index**, or `-1` if none exists.

---

# Core Insight

The products themselves are far too large to compute directly.

So we should not think in terms of multiplying values.

Instead, we should think in terms of **prime factors**.

A split is valid exactly when:

> no prime factor appears on both sides of the split.

Because if the same prime appears in both left and right products, then the gcd of the two products is greater than 1.

So the problem becomes:

- track where each prime factor appears in the array
- find the smallest split where all prime factors are fully contained in either the left or the right side, but not both

---

# Key Reframing

For each prime factor `p`, look at all indices where `p` appears in `nums`.

If `p` appears from index `L` to index `R`, then any split inside:

```text
[L, R - 1]
```

is invalid, because `p` would exist on both sides.

So every prime factor defines an **interval**.

The split is valid only after we move beyond the end of every interval that started before or at the current point.

This becomes an interval merging / sweep problem.

---

# Approach 1 — Prime Interval Expansion (Recommended)

## Idea

For each number, factorize it.

For each prime factor, record its **last occurrence index**.

Then scan left to right:

- at index `i`, factorize `nums[i]`
- among all its prime factors, find the farthest last occurrence
- maintain a running right boundary `maxReach`
- if current index `i` reaches `maxReach`, then all prime dependencies of the left part end here, so this is a valid split

This is the cleanest and standard solution.

---

## Why it works

Suppose we are scanning from left to right.

At index `i`, every prime factor seen so far may force us to extend the split boundary to the last occurrence of that prime.

If at some point:

```text
i == maxReach
```

then every prime factor that appeared in the left part is completely contained in the left part.

Therefore none of them can appear in the right part.

So the products are coprime.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int findValidSplit(int[] nums) {
        int n = nums.length;
        Map<Integer, Integer> last = new HashMap<>();

        for (int i = 0; i < n; i++) {
            for (int p : getPrimeFactors(nums[i])) {
                last.put(p, i);
            }
        }

        int maxReach = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int p : getPrimeFactors(nums[i])) {
                maxReach = Math.max(maxReach, last.get(p));
            }

            if (i == maxReach) {
                return i;
            }
        }

        return -1;
    }

    private List<Integer> getPrimeFactors(int x) {
        List<Integer> factors = new ArrayList<>();

        for (int p = 2; p * p <= x; p++) {
            if (x % p == 0) {
                factors.add(p);
                while (x % p == 0) {
                    x /= p;
                }
            }
        }

        if (x > 1) {
            factors.add(x);
        }

        return factors;
    }
}
```

---

## Complexity

Let:

- `n = nums.length`
- `M = max(nums[i])`

Each number is factorized in about:

```text
O(sqrt(M))
```

So total complexity is roughly:

```text
Time:  O(n * sqrt(M))
Space: O(number of distinct primes)
```

This is fine for:

```text
n <= 10^4
nums[i] <= 10^6
```

---

# Approach 2 — Smallest Prime Factor Sieve + Interval Sweep

## Idea

The previous solution repeatedly factorizes numbers by trial division.

We can speed that up by precomputing the **smallest prime factor (SPF)** for every value up to `max(nums)`.

Then prime factorization becomes much faster.

The interval logic stays exactly the same:

1. compute last occurrence of each prime
2. scan left to right, extending the current interval
3. first place where the interval closes is the answer

---

## Why SPF is useful

With SPF:

- factorization becomes near logarithmic per number
- repeated prime extraction is cheap
- this is more optimized than trial division

---

## Java Code

```java
import java.util.*;

class Solution {
    public int findValidSplit(int[] nums) {
        int n = nums.length;
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] spf = buildSPF(maxVal);
        Map<Integer, Integer> last = new HashMap<>();

        for (int i = 0; i < n; i++) {
            for (int p : getPrimeFactors(nums[i], spf)) {
                last.put(p, i);
            }
        }

        int maxReach = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int p : getPrimeFactors(nums[i], spf)) {
                maxReach = Math.max(maxReach, last.get(p));
            }

            if (i == maxReach) {
                return i;
            }
        }

        return -1;
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

    private List<Integer> getPrimeFactors(int x, int[] spf) {
        List<Integer> res = new ArrayList<>();

        while (x > 1) {
            int p = spf[x];
            res.add(p);
            while (x % p == 0) {
                x /= p;
            }
        }

        return res;
    }
}
```

---

## Complexity

- SPF preprocessing:

```text
O(M log log M)
```

- factorization of all numbers: near linear in total prime factors
- sweep: `O(n)`

So overall:

```text
Time:  O(M log log M + n * log M)
Space: O(M)
```

This is often the best practical implementation.

---

# Approach 3 — Prefix/Suffix Prime-Frequency Maps (Conceptual)

## Idea

Another possible direction is:

- build prime-factor counts for the whole array on the right side
- move from left to right
- shift prime factors of `nums[i]` from right map to left map
- after each move, check whether the left and right sides share any prime

This is logically correct, but checking shared-prime overlap efficiently is more cumbersome than the interval approach.

---

## Why it is less elegant

The true condition is “no prime appears on both sides.”

Tracking this directly with two maps is possible, but:

- more bookkeeping is needed
- interval merging captures the same condition much more cleanly

So this is usually not the preferred implementation.

---

# Approach 4 — Direct Product GCD Checking (Too Slow / Incorrect Numerically)

## Idea

For each split, compute:

- product of left side
- product of right side
- gcd of the two products

This is not practical because:

- products overflow immediately
- even using big integers would be too slow
- recomputing for every split is wasteful

So this is only a conceptual baseline.

---

# Detailed Walkthrough

## Example 1

```text
nums = [4,7,8,15,3,5]
```

Prime factors:

- `4` -> `{2}`
- `7` -> `{7}`
- `8` -> `{2}`
- `15` -> `{3,5}`
- `3` -> `{3}`
- `5` -> `{5}`

Last occurrences:

- `2` -> index 2
- `7` -> index 1
- `3` -> index 4
- `5` -> index 5

Now sweep:

### i = 0

`nums[0] = 4`, factors `{2}`
So:

```text
maxReach = 2
```

Not valid yet.

### i = 1

`nums[1] = 7`, factors `{7}`
So:

```text
maxReach = max(2, 1) = 2
```

Still not valid yet.

### i = 2

`nums[2] = 8`, factors `{2}`
Still:

```text
maxReach = 2
```

Now:

```text
i == maxReach
```

So split at index 2 is valid.

Answer:

```text
2
```

---

## Example 2

```text
nums = [4,7,15,8,3,5]
```

Prime factors:

- `4` -> `{2}`
- `7` -> `{7}`
- `15` -> `{3,5}`
- `8` -> `{2}`
- `3` -> `{3}`
- `5` -> `{5}`

Last occurrences:

- `2` -> index 3
- `7` -> index 1
- `3` -> index 4
- `5` -> index 5

Sweep:

### i = 0

factor `{2}` -> `maxReach = 3`

### i = 1

factor `{7}` -> `maxReach = 3`

### i = 2

factors `{3,5}` -> `maxReach = 5`

Now the interval extends to the end.

No index before `n - 1` can close the interval.

Answer:

```text
-1
```

---

# Important Correctness Argument

A split is invalid if some prime factor appears on both sides.

For each prime `p`, let:

- `first[p]` = first occurrence index
- `last[p]` = last occurrence index

Then every split inside:

```text
[first[p], last[p] - 1]
```

is invalid.

So the valid split must lie outside all active prime intervals.

The scanning algorithm maintains the union of all intervals started so far.
The first index where this union closes is exactly the first valid split.

That is why the interval sweep is correct.

---

# Common Pitfalls

## 1. Thinking in terms of full products

The products are huge and unnecessary.
Only prime factors matter.

---

## 2. Forgetting repeated prime factors within a single number

A number like `12` still only contributes prime set `{2,3}` for this problem.
Multiplicity inside one side does not matter for coprimality across sides.

---

## 3. Recomputing product gcds for every split

That is too slow and numerically unsafe.

---

## 4. Using only adjacent gcd checks

The split condition is global, not local.

---

# Best Approach

## Recommended: Prime interval sweep

This is the cleanest solution because:

- coprimality across products depends only on shared prime factors
- each prime defines an interval of forbidden split positions
- the answer is the first position where all active intervals close

The SPF-optimized version is best if you want faster factorization.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int findValidSplit(int[] nums) {
        int n = nums.length;
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] spf = buildSPF(maxVal);
        Map<Integer, Integer> last = new HashMap<>();

        for (int i = 0; i < n; i++) {
            for (int p : getPrimeFactors(nums[i], spf)) {
                last.put(p, i);
            }
        }

        int maxReach = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int p : getPrimeFactors(nums[i], spf)) {
                maxReach = Math.max(maxReach, last.get(p));
            }

            if (i == maxReach) {
                return i;
            }
        }

        return -1;
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

    private List<Integer> getPrimeFactors(int x, int[] spf) {
        List<Integer> res = new ArrayList<>();

        while (x > 1) {
            int p = spf[x];
            res.add(p);
            while (x % p == 0) {
                x /= p;
            }
        }

        return res;
    }
}
```

---

# Complexity Summary

Let:

- `n = nums.length`
- `M = max(nums[i])`

Then for the recommended SPF-based approach:

```text
Time:  O(M log log M + n log M)
Space: O(M)
```

This is efficient for the constraints.

---

# Final Takeaway

The crucial shift is:

Do **not** compare the left product and right product directly.

Instead:

1. factorize numbers into primes
2. observe that a split is invalid whenever a prime appears on both sides
3. treat each prime as an interval from its first to last occurrence
4. find the earliest point where all active intervals end

That gives a clean and efficient solution.
