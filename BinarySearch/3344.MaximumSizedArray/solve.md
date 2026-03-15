# 3344. Maximum Sized Array — Java Solutions and Detailed Notes

## Problem

For a given non-negative integer `s`, define a 3D array `A` of size:

```text
n × n × n
```

with:

```text
A[i][j][k] = i * (j OR k),   where 0 <= i, j, k < n
```

We need the **maximum** integer `n` such that:

```text
sum of all A[i][j][k] <= s
```

Return that maximum `n`.

---

## First decomposition

The total sum is:

```text
Σ(i * (j OR k))
```

over all:

```text
0 <= i, j, k < n
```

Because `i` is independent from `(j, k)`, this separates nicely:

```text
Σ_i Σ_j Σ_k i * (j OR k)
= (Σ_i i) * (Σ_j Σ_k (j OR k))
```

So if we define:

```text
S1(n) = Σ_{i=0}^{n-1} i = n(n-1)/2
S2(n) = Σ_{j=0}^{n-1} Σ_{k=0}^{n-1} (j OR k)
```

then:

```text
Total(n) = S1(n) * S2(n)
```

So the real challenge is computing `S2(n)` efficiently.

---

# Approach 1: Direct Brute Force (conceptual only)

## Idea

For a fixed `n`, directly compute:

```java
sum += i * (j | k)
```

for all triples.

Then binary search on `n`.

This is only useful as a conceptual baseline.

---

## Java code

```java
class SolutionBruteForce {
    public int maxSizedArray(long s) {
        int n = 1;
        while (sumFor(n + 1) <= s) {
            n++;
        }
        return n;
    }

    private long sumFor(int n) {
        long total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    total += 1L * i * (j | k);
                }
            }
        }
        return total;
    }
}
```

---

## Complexity

For one `n`:

```text
O(n^3)
```

This is obviously too slow even for moderate `n`.

---

# Approach 2: Binary Search on n + O(n^2) computation of S2(n)

## Idea

Still use:

```text
Total(n) = (sum of i) * (sum of j|k)
```

But instead of triple looping, compute:

- `Σ i` in O(1)
- `Σ (j OR k)` with two loops in O(n²)

Then binary search on `n`.

This is much better than cubic, but still too slow if done many times.

---

## Formula pieces

### Sum over i

```text
Σ_{i=0}^{n-1} i = n(n-1)/2
```

### Sum over j, k

```text
S2(n) = Σ_{j=0}^{n-1} Σ_{k=0}^{n-1} (j OR k)
```

We can compute this directly with two loops.

---

## Java code

```java
class Solution {
    public int maxSizedArray(long s) {
        long left = 1, right = 1;

        while (total(right) <= s) {
            right <<= 1;
        }

        while (left < right) {
            long mid = left + (right - left + 1) / 2;
            if (total(mid) <= s) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return (int) left;
    }

    private long total(long n) {
        long sumI = n * (n - 1) / 2;
        long sumOr = 0;

        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                sumOr += (j | k);
            }
        }

        return sumI * sumOr;
    }
}
```

---

## Complexity

Per `total(n)` evaluation:

```text
O(n^2)
```

Binary search adds another logarithmic factor.

Still not practical for large `n`.

---

# Approach 3: Bit Contribution Formula for S2(n) + Binary Search (Optimal)

This is the intended efficient approach.

## Core idea

Instead of summing `(j OR k)` directly, sum bit-by-bit.

For each bit position `b`, let:

```text
bitValue = 2^b
```

A pair `(j, k)` contributes `bitValue` to `(j OR k)` iff **at least one** of `j` or `k` has bit `b` set.

So if we know how many numbers in `[0, n-1]` have bit `b` set, we can count how many pairs contribute that bit.

---

## Count numbers with bit b set in [0, n-1]

Let:

```text
ones(n, b) = count of integers x in [0, n-1] such that bit b of x is 1
```

This is a standard periodic-counting problem.

For bit `b`, the pattern repeats every:

```text
2^(b+1)
```

with exactly:

```text
2^b
```

ones in each full block.

So:

```text
block = 1L << (b + 1)
half  = 1L << b

fullBlocks = n / block
remainder  = n % block

ones = fullBlocks * half + max(0, remainder - half)
```

because the range is `[0, n-1]` and there are `n` numbers total.

---

## Count pairs (j, k) where bit b appears in (j OR k)

If `ones = count of numbers with bit b = 1`,
then `zeros = n - ones`.

A pair `(j, k)` has bit `b` absent from `(j OR k)` only if both numbers have bit `b = 0`.

That gives:

```text
zeroPairs = zeros * zeros
```

Total ordered pairs:

```text
n * n
```

So bit `b` appears in:

```text
pairsWithBit = n*n - zeros*zeros
```

Therefore contribution of bit `b` to `S2(n)` is:

```text
pairsWithBit * (1 << b)
```

Hence:

```text
S2(n) = Σ_b [ (n*n - zeros_b^2) * 2^b ]
```

---

## Final formula

So:

```text
Total(n) = [n(n-1)/2] * Σ_b [ (n^2 - zeros_b^2) * 2^b ]
```

Now each `Total(n)` can be computed in about 60 bit positions, which is very fast.

Then binary search the largest `n` such that `Total(n) <= s`.

---

## Overflow note

Intermediate values can get large. Since `s <= 10^15`, we can safely cap computations once they exceed `s` during feasibility checks.

This makes the code safer.

---

## Java code (recommended)

```java
class Solution {
    public int maxSizedArray(long s) {
        long left = 1, right = 1;

        while (can(right, s)) {
            right <<= 1;
        }

        while (left < right) {
            long mid = left + (right - left + 1) / 2;
            if (can(mid, s)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return (int) left;
    }

    private boolean can(long n, long s) {
        long sumI = n * (n - 1) / 2;
        if (sumI == 0) {
            return 0 <= s;
        }

        long sumOr = 0;

        for (int b = 0; b < 61; b++) {
            long bit = 1L << b;
            if (bit > s && sumOr > 0) {
                break;
            }

            long ones = countOnes(n, b);
            long zeros = n - ones;

            long totalPairs;
            if (n > 3_000_000_000L) {
                totalPairs = Long.MAX_VALUE;
            } else {
                totalPairs = n * n;
            }

            long zeroPairs;
            if (zeros > 3_000_000_000L) {
                zeroPairs = Long.MAX_VALUE;
            } else {
                zeroPairs = zeros * zeros;
            }

            long pairsWithBit = totalPairs - zeroPairs;

            if (pairsWithBit <= 0) continue;

            if (pairsWithBit > s / bit + 1) {
                return false;
            }

            long contrib = pairsWithBit * bit;
            sumOr += contrib;

            if (sumOr > s) {
                return false;
            }
        }

        if (sumOr == 0) return true;
        return sumI <= s / sumOr;
    }

    private long countOnes(long n, int b) {
        long half = 1L << b;
        long block = half << 1;

        long fullBlocks = n / block;
        long remainder = n % block;

        return fullBlocks * half + Math.max(0L, remainder - half);
    }
}
```

---

# Cleaner implementation with saturation arithmetic

The previous version is safe, but we can make it cleaner by capping values above `s`.

This is often easier to reason about.

```java
class Solution {
    public int maxSizedArray(long s) {
        long lo = 1, hi = 1;

        while (sumFor(hi, s) <= s) {
            hi <<= 1;
        }

        while (lo < hi) {
            long mid = lo + (hi - lo + 1) / 2;
            if (sumFor(mid, s) <= s) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        return (int) lo;
    }

    private long sumFor(long n, long limit) {
        long sumI = n * (n - 1) / 2;
        if (sumI == 0) return 0;

        long sumOr = 0;
        for (int b = 0; b < 61; b++) {
            long bit = 1L << b;
            long ones = countOnes(n, b);
            long zeros = n - ones;

            long pairsWithBit = safeSub(safeMul(n, n, limit), safeMul(zeros, zeros, limit), limit);
            long contrib = safeMul(pairsWithBit, bit, limit);

            sumOr = safeAdd(sumOr, contrib, limit);
            if (sumOr > limit) return limit + 1;
        }

        return safeMul(sumI, sumOr, limit);
    }

    private long countOnes(long n, int b) {
        long half = 1L << b;
        long block = half << 1;
        long full = n / block;
        long rem = n % block;
        return full * half + Math.max(0L, rem - half);
    }

    private long safeAdd(long a, long b, long limit) {
        if (a > limit - b) return limit + 1;
        return a + b;
    }

    private long safeSub(long a, long b, long limit) {
        return a - b;
    }

    private long safeMul(long a, long b, long limit) {
        if (a == 0 || b == 0) return 0;
        if (a > limit / b) return limit + 1;
        return a * b;
    }
}
```

This version uses capped arithmetic to avoid overflow while still only caring whether the total exceeds `s`.

---

# Approach 4: Math formula + direct iteration over n (educational)

## Idea

Once we can compute `Total(n)` in `O(log s)` time with the bit formula, we might try increasing `n` one by one until the sum exceeds `s`.

This is simpler conceptually than binary search, but binary search is still better.

---

## Java sketch

```java
class SolutionLinear {
    public int maxSizedArray(long s) {
        int n = 1;
        while (sumFor(n + 1, s) <= s) {
            n++;
        }
        return n;
    }

    private long sumFor(long n, long limit) {
        long sumI = n * (n - 1) / 2;
        long sumOr = 0;

        for (int b = 0; b < 61; b++) {
            long bit = 1L << b;
            long ones = countOnes(n, b);
            long zeros = n - ones;
            long pairsWithBit = n * n - zeros * zeros;
            sumOr += pairsWithBit * bit;
            if (sumOr > limit) return limit + 1;
        }

        if (sumI > limit / sumOr) return limit + 1;
        return sumI * sumOr;
    }

    private long countOnes(long n, int b) {
        long half = 1L << b;
        long block = half << 1;
        long full = n / block;
        long rem = n % block;
        return full * half + Math.max(0L, rem - half);
    }
}
```

---

## Complexity

If answer is `N`:

```text
O(N log s)
```

which is worse than binary search.

This is only educational.

---

# Worked example

## Example 1: s = 10

Try `n = 2`.

### Step 1: sum over i

```text
Σ i = 0 + 1 = 1
```

### Step 2: sum over (j OR k) for j, k in {0,1}

Pairs:

```text
0|0 = 0
0|1 = 1
1|0 = 1
1|1 = 1
```

So:

```text
S2(2) = 3
```

Hence:

```text
Total(2) = 1 * 3 = 3
```

which is <= 10.

Try `n = 3`.

```text
Σ i = 0+1+2 = 3
```

For `j,k in [0,2]`, the OR-sum becomes larger; total exceeds 10.
So answer is `2`.

---

## Example 2: s = 0

For `n = 1`:

```text
A[0][0][0] = 0 * (0|0) = 0
```

Total is `0`, so valid.

For `n = 2`, total is already `3 > 0`.

Answer is `1`.

---

# Why the bit formula is correct

For each bit `b`, `(j OR k)` has that bit set unless **both** `j` and `k` have that bit unset.

If `zeros_b` is the number of numbers in `[0, n-1]` with bit `b = 0`, then the number of ordered pairs `(j,k)` with that bit absent is:

```text
zeros_b^2
```

So the number of ordered pairs with the bit present is:

```text
n^2 - zeros_b^2
```

Each such pair contributes `2^b` to the OR value.

Summing over all bits gives exactly:

```text
S2(n) = Σ_b (n^2 - zeros_b^2) * 2^b
```

Then multiplying by:

```text
Σ_i i = n(n-1)/2
```

gives the total 3D array sum.

---

# Comparison of approaches

## Approach 1: Triple brute force

### Pros

- easiest to understand

### Cons

- completely infeasible

### Complexity

```text
O(n^3)
```

---

## Approach 2: Two-loop OR sum + binary search

### Pros

- better than brute force
- straightforward decomposition

### Cons

- still too slow

### Complexity

```text
O(n^2 log answer)
```

---

## Approach 3: Bit contribution formula + binary search (recommended)

### Pros

- optimal and efficient
- handles huge `s`
- elegant bitwise counting

### Cons

- requires the key bit-contribution insight

### Complexity

Each feasibility check computes around 61 bits:

```text
O(log s)
```

Binary search adds another `O(log answer)` factor, both tiny.

Overall effectively:

```text
O((log s)^2)
```

with very small constants.

---

## Approach 4: Bit formula + linear search on n

### Pros

- simpler than binary search conceptually

### Cons

- slower than needed

---

# Final recommended Java solution

```java
class Solution {
    public int maxSizedArray(long s) {
        long lo = 1, hi = 1;

        while (sumFor(hi, s) <= s) {
            hi <<= 1;
        }

        while (lo < hi) {
            long mid = lo + (hi - lo + 1) / 2;
            if (sumFor(mid, s) <= s) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        return (int) lo;
    }

    private long sumFor(long n, long limit) {
        long sumI = n * (n - 1) / 2;
        if (sumI == 0) return 0;

        long sumOr = 0;
        for (int b = 0; b < 61; b++) {
            long bit = 1L << b;
            long ones = countOnes(n, b);
            long zeros = n - ones;

            long totalPairs = safeMul(n, n, limit);
            long zeroPairs = safeMul(zeros, zeros, limit);
            long pairsWithBit = totalPairs - zeroPairs;

            long contrib = safeMul(pairsWithBit, bit, limit);
            sumOr = safeAdd(sumOr, contrib, limit);

            if (sumOr > limit) return limit + 1;
        }

        return safeMul(sumI, sumOr, limit);
    }

    private long countOnes(long n, int b) {
        long half = 1L << b;
        long block = half << 1;
        long full = n / block;
        long rem = n % block;
        return full * half + Math.max(0L, rem - half);
    }

    private long safeAdd(long a, long b, long limit) {
        if (a > limit - b) return limit + 1;
        return a + b;
    }

    private long safeMul(long a, long b, long limit) {
        if (a == 0 || b == 0) return 0;
        if (a > limit / b) return limit + 1;
        return a * b;
    }
}
```

---

# Takeaway pattern

This problem is a strong example of the pattern:

1. separate dimensions algebraically,
2. replace direct summation with **bit contribution counting**,
3. use monotonicity of the answer for **binary search**.

Whenever you see an expression involving OR / AND / XOR over a large range, always consider summing **bit by bit** instead of value by value.
