# 1808. Maximize Number of Nice Divisors — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int maxNiceDivisors(int primeFactors) {

    }
}
```

---

# Problem Restatement

We are given an integer:

```text
primeFactors
```

We must construct a positive integer `n` such that:

1. the total number of prime factors of `n` (counted with multiplicity) is **at most** `primeFactors`
2. the number of **nice divisors** of `n` is maximized

A divisor of `n` is **nice** if it is divisible by **every distinct prime factor** of `n`.

We must return the maximum possible number of nice divisors modulo:

```text
1_000_000_007
```

---

# First Key Insight: How to Count Nice Divisors

Suppose:

```text
n = p1^a1 * p2^a2 * ... * pk^ak
```

where `p1, p2, ..., pk` are distinct primes.

A divisor of `n` is nice if it contains **every one of these prime factors at least once**.

So for each prime `pi`, the exponent in the divisor can be chosen from:

```text
1, 2, ..., ai
```

That gives exactly `ai` choices for that prime.

Therefore the number of nice divisors is:

```text
a1 * a2 * ... * ak
```

So the problem becomes:

> Split `primeFactors` into positive integers whose sum is at most `primeFactors`, and maximize their product.

Because if we choose exponents:

```text
a1 + a2 + ... + ak <= primeFactors
```

then the number of nice divisors is:

```text
a1 * a2 * ... * ak
```

To maximize the product, we should use all available prime factors, so the sum should actually be exactly:

```text
a1 + a2 + ... + ak = primeFactors
```

---

# Second Key Insight: This Reduces to Integer Break

Now the problem is identical to the classic **integer break / maximize product by splitting a number** problem.

For a sum `x`, the product is maximized by cutting it mostly into **3s**.

Why?

Because:

- splitting into many 1s is bad
- `3` gives the best multiplicative growth
- special care is needed when the remainder is `1`

This leads to the standard rule:

- if `primeFactors <= 3`, answer is `primeFactors`
- otherwise:
  - if `primeFactors % 3 == 0`, answer is `3^(primeFactors / 3)`
  - if `primeFactors % 3 == 1`, answer is `3^(primeFactors / 3 - 1) * 4`
  - if `primeFactors % 3 == 2`, answer is `3^(primeFactors / 3) * 2`

All modulo `1_000_000_007`.

---

# Why 3 is Optimal

This is the central mathematical point.

Suppose we split an integer into parts whose sum is fixed.

We want to maximize the product.

### 1. Avoid large pieces greater than 4

If a part is at least 5, say `x`, then splitting it into:

```text
3 and (x - 3)
```

improves or preserves the product because:

```text
3 * (x - 3) >= x
```

for `x >= 5`.

So large parts should be broken down.

---

### 2. Avoid 1

Using `1` is wasteful because multiplying by 1 does not help.

For example:

```text
3 + 1  -> product = 3
2 + 2  -> product = 4
```

So if remainder becomes 1, we should replace:

```text
3 + 1
```

with:

```text
2 + 2
```

---

### 3. Therefore

Best decomposition uses:

- as many `3`s as possible
- except when remainder is `1`, where we convert one `3 + 1` into `2 + 2`

That is exactly the formula above.

---

# Approach 1 — Mathematical Greedy + Fast Power (Recommended)

## Idea

Directly apply the optimal decomposition into 3s.

Then compute the result using modular exponentiation.

This is the standard optimal solution.

---

## Case Analysis

Let:

```text
pf = primeFactors
```

### Case 1: `pf <= 3`

Then answer is simply:

```text
pf
```

Because:

- `1 -> 1`
- `2 -> 2`
- `3 -> 3`

---

### Case 2: `pf % 3 == 0`

Use all 3s:

```text
3^(pf / 3)
```

---

### Case 3: `pf % 3 == 1`

We do not want a trailing 1.

So replace one `3 + 1` with `2 + 2`.

Thus:

```text
3^(pf / 3 - 1) * 4
```

---

### Case 4: `pf % 3 == 2`

Use all 3s plus one 2:

```text
3^(pf / 3) * 2
```

---

## Java Code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;

    public int maxNiceDivisors(int primeFactors) {
        if (primeFactors <= 3) {
            return primeFactors;
        }

        if (primeFactors % 3 == 0) {
            return (int) modPow(3, primeFactors / 3);
        }

        if (primeFactors % 3 == 1) {
            return (int) ((modPow(3, primeFactors / 3 - 1) * 4) % MOD);
        }

        return (int) ((modPow(3, primeFactors / 3) * 2) % MOD);
    }

    private long modPow(long base, long exp) {
        long result = 1L;
        base %= MOD;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % MOD;
            }
            base = (base * base) % MOD;
            exp >>= 1;
        }

        return result;
    }
}
```

---

## Complexity

Fast exponentiation takes:

```text
O(log primeFactors)
```

So:

- **Time:** `O(log primeFactors)`
- **Space:** `O(1)`

This is optimal.

---

# Approach 2 — Same Math, Written as Explicit Exponent and Remainder Logic

## Idea

This is the same optimal observation, but structured more explicitly.

Let:

```text
q = primeFactors / 3
r = primeFactors % 3
```

Then:

- `r = 0` -> `3^q`
- `r = 1` -> `3^(q-1) * 4`
- `r = 2` -> `3^q * 2`

This is not a different algorithm mathematically, but it is a useful alternate implementation style.

---

## Java Code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;

    public int maxNiceDivisors(int primeFactors) {
        if (primeFactors <= 3) {
            return primeFactors;
        }

        long q = primeFactors / 3;
        long r = primeFactors % 3;

        if (r == 0) {
            return (int) fastPow(3, q);
        } else if (r == 1) {
            return (int) ((fastPow(3, q - 1) * 4) % MOD);
        } else {
            return (int) ((fastPow(3, q) * 2) % MOD);
        }
    }

    private long fastPow(long base, long exp) {
        long ans = 1;
        base %= MOD;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                ans = (ans * base) % MOD;
            }
            base = (base * base) % MOD;
            exp >>= 1;
        }

        return ans;
    }
}
```

---

## Complexity

Exactly the same:

- **Time:** `O(log primeFactors)`
- **Space:** `O(1)`

---

# Approach 3 — DP / Integer Break Style (Educational, Not Practical for Real Constraints)

## Idea

If constraints were small, we could define:

```text
dp[x] = maximum product obtainable by splitting x
```

Then try every split:

```text
dp[x] = max(dp[x], max(j, dp[j]) * max(x-j, dp[x-j]))
```

This is the classic integer break dynamic programming formulation.

It works conceptually, and it helps derive the math.

However it is completely impractical here because:

```text
primeFactors <= 10^9
```

So an `O(primeFactors^2)` or even `O(primeFactors)` DP is impossible.

Still, this approach is useful for understanding.

---

## Java Code (Educational Only)

```java
class Solution {
    public int maxNiceDivisors(int primeFactors) {
        if (primeFactors <= 3) {
            return primeFactors;
        }

        long[] dp = new long[primeFactors + 1];
        dp[1] = 1;

        for (int x = 2; x <= primeFactors; x++) {
            long best = x;
            for (int j = 1; j < x; j++) {
                long left = Math.max(j, dp[j]);
                long right = Math.max(x - j, dp[x - j]);
                best = Math.max(best, left * right);
            }
            dp[x] = best;
        }

        return (int) dp[primeFactors];
    }
}
```

---

## Why this is not usable

The code above is only for conceptual understanding.

For:

```text
primeFactors <= 10^9
```

it is impossible.

So this is **not a valid final solution**, only a teaching approach.

---

# Detailed Walkthrough

## Example 1: `primeFactors = 5`

We want to split 5 into positive integers maximizing product.

Possible good splits:

- `5` -> product `5`
- `3 + 2` -> product `6`
- `2 + 2 + 1` -> product `4`
- `1 + 1 + 1 + 1 + 1` -> product `1`

Best is:

```text
3 + 2 -> 6
```

So answer is `6`.

Interpretation in the original problem:

- choose exponents `3` and `2`
- number of nice divisors = `3 * 2 = 6`

---

## Example 2: `primeFactors = 8`

Split 8 optimally:

```text
3 + 3 + 2
```

Product:

```text
3 * 3 * 2 = 18
```

So answer is `18`.

---

# Why the Nice Divisor Count Becomes Product of Exponents

Suppose:

```text
n = 2^3 * 5^2
```

Distinct prime factors are:

```text
2 and 5
```

A nice divisor must contain both 2 and 5 at least once.

So exponent choices in the divisor are:

- for 2: choose exponent `1, 2, or 3` -> 3 choices
- for 5: choose exponent `1 or 2` -> 2 choices

Total nice divisors:

```text
3 * 2 = 6
```

That exactly matches the product of exponents.

So maximizing nice divisors is equivalent to maximizing the product of a partition of `primeFactors`.

---

# Common Pitfalls

## 1. Forgetting that the number of prime factors is counted with multiplicity

For example:

```text
72 = 2 * 2 * 2 * 3 * 3
```

has 5 prime factors, not 2.

---

## 2. Confusing “number of divisors” with “number of nice divisors”

We are **not** counting all divisors.

Only those divisible by every distinct prime factor.

---

## 3. Using 1 in the partition

A part of size 1 is almost never useful in maximizing product.

The only special care is for very small inputs `<= 3`.

---

## 4. Handling remainder 1 incorrectly

This is the classic trap.

If:

```text
primeFactors % 3 == 1
```

do **not** leave a `1`.

Instead convert:

```text
3 + 1
```

into:

```text
2 + 2
```

because:

```text
3 * 1 = 3
2 * 2 = 4
```

---

# Best Approach

## Recommended: Mathematical Greedy + Modular Exponentiation

This is the optimal solution because:

- derived from the exact mathematical structure of the problem
- runs in logarithmic time
- works for very large `primeFactors`
- simple once the transformation is understood

---

# Final Recommended Java Solution

```java
class Solution {
    private static final long MOD = 1_000_000_007L;

    public int maxNiceDivisors(int primeFactors) {
        if (primeFactors <= 3) {
            return primeFactors;
        }

        if (primeFactors % 3 == 0) {
            return (int) modPow(3, primeFactors / 3);
        } else if (primeFactors % 3 == 1) {
            return (int) ((modPow(3, primeFactors / 3 - 1) * 4) % MOD);
        } else {
            return (int) ((modPow(3, primeFactors / 3) * 2) % MOD);
        }
    }

    private long modPow(long base, long exp) {
        long result = 1L;
        base %= MOD;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % MOD;
            }
            base = (base * base) % MOD;
            exp >>= 1;
        }

        return result;
    }
}
```

---

# Final Complexity Summary

Using the recommended approach:

- **Time:** `O(log primeFactors)`
- **Space:** `O(1)`

---

# Final Takeaway

This problem looks like number theory, but the real transformation is:

1. factorize the structure of nice divisors
2. realize the count becomes the product of exponents
3. reduce to maximizing the product of integers with fixed sum
4. apply the standard “mostly 3s” integer break rule
5. compute with modular exponentiation

That yields the optimal logarithmic-time solution.
