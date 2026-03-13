# 3233. Find the Count of Numbers Which Are Not Special

## Problem Restatement

We are given two integers:

- `l`
- `r`

We need to count how many numbers in the range:

```text
[l, r]
```

are **not special**.

A number is called **special** if it has **exactly 2 proper divisors**.

A proper divisor of `x` is any positive divisor of `x` except `x` itself.

---

## First Key Observation

Let us understand which numbers have exactly **2 proper divisors**.

Take some examples:

- `4` → divisors: `1, 2, 4`
  proper divisors: `1, 2` → exactly 2
- `9` → divisors: `1, 3, 9`
  proper divisors: `1, 3` → exactly 2
- `25` → divisors: `1, 5, 25`
  proper divisors: `1, 5` → exactly 2

Now try a non-example:

- `6` → divisors: `1, 2, 3, 6`
  proper divisors: `1, 2, 3` → 3 proper divisors

This suggests something important:

> A number is special **iff it is the square of a prime number**.

Why?

If `x = p^2` where `p` is prime, then divisors are:

```text
1, p, p^2
```

So proper divisors are exactly:

```text
1, p
```

which are 2 in number.

If a number is not a prime square, it cannot have exactly 2 proper divisors.

So the problem becomes:

> Count numbers in `[l, r]` that are **not prime squares**.

That means:

```text
answer = total numbers in [l, r] - count of prime squares in [l, r]
```

---

# Approach 1 — Brute Force Check Every Number

## Intuition

For every number `x` in `[l, r]`:

1. find how many proper divisors it has
2. if it has exactly 2, it is special
3. otherwise it is not special

This is the most direct approach.

It is useful for understanding, but it is too slow for the actual constraints because:

```text
r can be as large as 10^9
```

and the interval can also be huge.

---

## Algorithm

For each `x` from `l` to `r`:

- count divisors of `x`
- subtract 1 if including `x` itself
- if proper divisor count is exactly 2, mark as special

Return the count of non-special numbers.

---

## Java Code

```java
class Solution {
    public int nonSpecialCount(int l, int r) {
        int count = 0;

        for (int x = l; x <= r; x++) {
            if (!isSpecial(x)) {
                count++;
            }
        }

        return count;
    }

    private boolean isSpecial(int x) {
        int properDivisors = 0;

        for (int d = 1; d * d <= x; d++) {
            if (x % d == 0) {
                int q = x / d;

                if (d != x) properDivisors++;
                if (q != d && q != x) properDivisors++;
            }
        }

        return properDivisors == 2;
    }
}
```

---

## Complexity Analysis

Let:

```text
N = r - l + 1
```

For each number, divisor counting takes about:

```text
O(sqrt(r))
```

So total:

```text
O(N * sqrt(r))
```

This is far too slow for the full constraints.

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Check Prime Squares in the Range

## Intuition

From the key observation:

> Special numbers are exactly prime squares.

So instead of checking every number directly, we only need to know:

- which primes `p` satisfy

```text
p^2 in [l, r]
```

If we count such primes, then:

```text
non-special count = (r - l + 1) - special count
```

This is much faster, because the relevant primes are only up to:

```text
sqrt(r)
```

And:

```text
sqrt(10^9) ≈ 31623
```

That is tiny.

---

## Algorithm

1. Compute:
   - `left = ceil(sqrt(l))`
   - `right = floor(sqrt(r))`
2. Count how many prime numbers lie in `[left, right]`
3. Each such prime contributes one special number (`p^2`)
4. Return:

```text
(r - l + 1) - primeCount
```

---

## Java Code

```java
class Solution {
    public int nonSpecialCount(int l, int r) {
        int left = (int) Math.ceil(Math.sqrt(l));
        int right = (int) Math.floor(Math.sqrt(r));

        int specialCount = 0;

        for (int x = left; x <= right; x++) {
            if (isPrime(x)) {
                specialCount++;
            }
        }

        return (r - l + 1) - specialCount;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) return false;
        }
        return true;
    }
}
```

---

## Complexity Analysis

We only check numbers up to `sqrt(r)`.

Let:

```text
M = floor(sqrt(r))
```

Then we perform about `M` primality checks, each costing `O(sqrt(M))`.

So total:

```text
O(M * sqrt(M))
```

Since `M <= 31623`, this is acceptable.

### Space Complexity

```text
O(1)
```

---

# Approach 3 — Sieve of Eratosthenes up to sqrt(r)

## Intuition

Approach 2 is already good, but we can do even better.

Instead of testing each number in `[ceil(sqrt(l)), floor(sqrt(r))]` individually for primality, we can generate **all primes up to `sqrt(r)`** using the Sieve of Eratosthenes.

Then counting prime squares in the range becomes straightforward.

This is the cleanest and most efficient approach for the actual constraints.

---

## Why Sieve Works Well Here

Since:

```text
r <= 10^9
```

we only need primes up to:

```text
sqrt(r) <= 31623
```

A sieve up to 31623 is extremely cheap.

---

## Algorithm

1. Compute:

```text
limit = floor(sqrt(r))
```

2. Build a sieve of primes up to `limit`
3. For each prime `p`:
   - compute `p * p`
   - if it lies in `[l, r]`, it is special
4. Let `specialCount` be the number of such prime squares
5. Return:

```text
(r - l + 1) - specialCount
```

---

## Java Code

```java
import java.util.*;

class Solution {
    public int nonSpecialCount(int l, int r) {
        int limit = (int) Math.sqrt(r);
        boolean[] isPrime = sieve(limit);

        int specialCount = 0;

        for (int p = 2; p <= limit; p++) {
            if (isPrime[p]) {
                long square = 1L * p * p;
                if (square >= l && square <= r) {
                    specialCount++;
                }
            }
        }

        return (r - l + 1) - specialCount;
    }

    private boolean[] sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);

        if (n >= 0) isPrime[0] = false;
        if (n >= 1) isPrime[1] = false;

        for (int p = 2; p * p <= n; p++) {
            if (isPrime[p]) {
                for (int multiple = p * p; multiple <= n; multiple += p) {
                    isPrime[multiple] = false;
                }
            }
        }

        return isPrime;
    }
}
```

---

## Complexity Analysis

Let:

```text
M = floor(sqrt(r))
```

### Time Complexity

Sieve construction:

```text
O(M log log M)
```

Then scanning primes up to `M`:

```text
O(M)
```

Overall:

```text
O(M log log M)
```

Since `M <= 31623`, this is extremely fast.

### Space Complexity

Sieve array:

```text
O(M)
```

---

# Approach 4 — Precompute Prefix Prime Counts up to 31623

## Intuition

This is useful if we imagine multiple queries.

We can precompute:

- all primes up to 31623
- prefix count of primes

Then for a given query `[l, r]`, we compute:

- `left = ceil(sqrt(l))`
- `right = floor(sqrt(r))`

and count how many primes lie in `[left, right]` using prefix sums.

This is overkill for a single query, but it is a nice extension.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int nonSpecialCount(int l, int r) {
        int limit = (int) Math.sqrt(r);
        boolean[] isPrime = sieve(limit);
        int[] prefix = new int[limit + 1];

        for (int i = 1; i <= limit; i++) {
            prefix[i] = prefix[i - 1] + (isPrime[i] ? 1 : 0);
        }

        int left = (int) Math.ceil(Math.sqrt(l));
        int right = (int) Math.floor(Math.sqrt(r));

        int specialCount = 0;
        if (left <= right) {
            specialCount = prefix[right] - (left > 1 ? prefix[left - 1] : 0);
        }

        return (r - l + 1) - specialCount;
    }

    private boolean[] sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);

        if (n >= 0) isPrime[0] = false;
        if (n >= 1) isPrime[1] = false;

        for (int p = 2; p * p <= n; p++) {
            if (isPrime[p]) {
                for (int multiple = p * p; multiple <= n; multiple += p) {
                    isPrime[multiple] = false;
                }
            }
        }

        return isPrime;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Sieve + prefix build:

```text
O(M log log M)
```

Query answering:

```text
O(1)
```

### Space Complexity

```text
O(M)
```

---

# Correctness Proof

## Claim 1

A number is special if and only if it is the square of a prime.

### Proof

Suppose `x` is special.

That means `x` has exactly 2 proper divisors.

One proper divisor is always `1`, so there is exactly one other proper divisor.

If `x` had more than one nontrivial divisor, then it would have more than 2 proper divisors.

Thus `x` must have exactly three total divisors:

```text
1, p, x
```

This happens exactly when:

```text
x = p^2
```

for a prime `p`.

Conversely, if `x = p^2` where `p` is prime, then its divisors are:

```text
1, p, p^2
```

So its proper divisors are:

```text
1, p
```

exactly two.

Hence, a number is special iff it is a prime square.

Proved.

---

## Claim 2

The number of special numbers in `[l, r]` equals the number of primes `p` such that:

```text
p^2 in [l, r]
```

This follows directly from Claim 1.

So once we count such primes, the answer is:

```text
(r - l + 1) - specialCount
```

Proved.

---

# Worked Example

## Example 1

```text
l = 5, r = 7
```

Numbers in range:

```text
5, 6, 7
```

Prime squares near this range:

- `2^2 = 4` → too small
- `3^2 = 9` → too large

So there are no special numbers.

Answer:

```text
3 - 0 = 3
```

---

## Example 2

```text
l = 4, r = 16
```

Prime squares in range:

- `2^2 = 4`
- `3^2 = 9`

`4^2 = 16`, but `4` is not prime, so `16` is not special.

So special numbers are:

```text
4, 9
```

Total numbers in range:

```text
16 - 4 + 1 = 13
```

Non-special count:

```text
13 - 2 = 11
```

---

# Edge Cases

## 1. `l = r`

Single number interval.

Just check whether that number is a prime square.

Example:

```text
l = r = 4
```

Since `4 = 2^2` and `2` is prime, `4` is special.

So answer is `0`.

---

## 2. Very large `r`

Even when:

```text
r = 10^9
```

we only need primes up to:

```text
sqrt(10^9) ≈ 31623
```

So the sieve approach remains fast.

---

## 3. Perfect square whose root is not prime

Example:

```text
16 = 4^2
```

But `4` is not prime.

So `16` is **not** special.

This is a common trap.

---

# Comparison of Approaches

## Approach 1 — Brute force divisor counting

Pros:

- most direct
- easiest to understand initially

Cons:

- far too slow for constraints

---

## Approach 2 — Prime-check square roots directly

Pros:

- much faster
- easy to implement

Cons:

- repeated primality checks

---

## Approach 3 — Sieve of Eratosthenes

Pros:

- fastest clean solution
- ideal for the constraints
- mathematically elegant

Cons:

- slightly more setup than direct prime checking

This is the recommended solution.

---

## Approach 4 — Prefix prime counts

Pros:

- useful extension for multiple queries
- fast query answering

Cons:

- slightly over-engineered for one query

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int nonSpecialCount(int l, int r) {
        int limit = (int) Math.sqrt(r);
        boolean[] isPrime = new boolean[limit + 1];
        Arrays.fill(isPrime, true);

        if (limit >= 0) isPrime[0] = false;
        if (limit >= 1) isPrime[1] = false;

        for (int p = 2; p * p <= limit; p++) {
            if (isPrime[p]) {
                for (int multiple = p * p; multiple <= limit; multiple += p) {
                    isPrime[multiple] = false;
                }
            }
        }

        int specialCount = 0;
        for (int p = 2; p <= limit; p++) {
            if (isPrime[p]) {
                long square = 1L * p * p;
                if (square >= l && square <= r) {
                    specialCount++;
                }
            }
        }

        return (r - l + 1) - specialCount;
    }
}
```

---

# Complexity Summary

Let:

```text
M = floor(sqrt(r))
```

## Approach 1

```text
Time:  O((r - l + 1) * sqrt(r))
Space: O(1)
```

## Approach 2

```text
Time:  O(M * sqrt(M))
Space: O(1)
```

## Approach 3

```text
Time:  O(M log log M)
Space: O(M)
```

## Approach 4

```text
Time:  O(M log log M)
Space: O(M)
```

---

# Final Takeaway

The decisive insight is:

> A number is special **iff it is a square of a prime**.

So the task is not really about divisor counting over `[l, r]`.
