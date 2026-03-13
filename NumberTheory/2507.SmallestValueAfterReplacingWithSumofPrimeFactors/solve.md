# 2507. Smallest Value After Replacing With Sum of Prime Factors — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int smallestValue(int n) {

    }
}
```

---

# Problem Restatement

We are given a positive integer `n`.

We repeatedly replace `n` with the **sum of its prime factors**, counting multiplicity.

That means if:

```text
n = p1 * p2 * p3 * ...
```

then the next value becomes:

```text
p1 + p2 + p3 + ...
```

If a prime factor appears multiple times, it is added multiple times.

We must return the **smallest value** reached during this process.

---

# Core Insight

For a composite number, the sum of its prime factors is often smaller than the number itself.

For example:

```text
15 = 3 * 5  ->  3 + 5 = 8
8  = 2 * 2 * 2  ->  2 + 2 + 2 = 6
6  = 2 * 3  ->  2 + 3 = 5
```

Eventually, the process stabilizes.

Why?

Because once `n` becomes prime:

```text
sum of prime factors = n
```

So the value stops changing.

Thus the process always ends at a fixed point, and that fixed point is the answer.

---

# Key Operation: Sum of Prime Factors

To simulate the process, we need a function:

```text
sumPrimeFactors(x)
```

that returns the sum of all prime factors of `x`, counting multiplicity.

Example:

```text
x = 12 = 2 * 2 * 3
sumPrimeFactors(12) = 2 + 2 + 3 = 7
```

The standard way is trial division:

- divide by `2`, `3`, `4`, ...
- whenever a factor divides `x`, add it to the sum and divide `x`
- continue until all factors are consumed

If after the loop `x > 1`, then the remaining `x` is prime and must be added once.

---

# Approach 1 — Repeated Prime Factor Summation by Trial Division (Recommended)

## Idea

This is the most direct solution:

1. compute the sum of prime factors of `n`
2. if the sum equals `n`, stop
3. otherwise set `n = sum` and repeat

Because `n <= 10^5`, straightforward trial division is fast enough.

---

## Why this works

Each iteration exactly follows the rule from the statement.

The process stops when `n` is prime or when the factor sum equals the number itself.

At that point, the value cannot decrease any further, so that is the minimum reached.

---

## Java Code

```java
class Solution {
    public int smallestValue(int n) {
        while (true) {
            int sum = sumPrimeFactors(n);
            if (sum == n) {
                return n;
            }
            n = sum;
        }
    }

    private int sumPrimeFactors(int x) {
        int sum = 0;
        int num = x;

        for (int d = 2; d * d <= num; d++) {
            while (x % d == 0) {
                sum += d;
                x /= d;
            }
        }

        if (x > 1) {
            sum += x;
        }

        return sum;
    }
}
```

---

## Complexity

Let `M` be the current value during the process.

Each factorization takes about:

```text
O(sqrt(M))
```

The value decreases quickly, so the number of iterations is small.

Thus the total runtime is easily fast enough for:

```text
n <= 10^5
```

A reasonable bound is:

```text
Time:  O(T * sqrt(n))
Space: O(1)
```

where `T` is the number of iterations, usually very small.

---

# Approach 2 — Precompute Smallest Prime Factors (SPF)

## Idea

If we want faster factorization, we can preprocess the **smallest prime factor** for every number up to `10^5`.

Then factorization becomes very fast:

- repeatedly look up `spf[x]`
- add it to the sum
- divide by it

This is more efficient if we were solving the operation for many different values, or just want a more optimized factorization routine.

---

## Why it works

The smallest prime factor table lets us decompose any number quickly in near-logarithmic time.

For a single query this is somewhat more setup than necessary, but it is still a valid and clean approach.

---

## Java Code

```java
class Solution {
    public int smallestValue(int n) {
        int[] spf = buildSPF(100000);

        while (true) {
            int sum = sumPrimeFactors(n, spf);
            if (sum == n) {
                return n;
            }
            n = sum;
        }
    }

    private int sumPrimeFactors(int x, int[] spf) {
        int sum = 0;
        while (x > 1) {
            int p = spf[x];
            sum += p;
            x /= p;
        }
        return sum;
    }

    private int[] buildSPF(int limit) {
        int[] spf = new int[limit + 1];
        for (int i = 0; i <= limit; i++) {
            spf[i] = i;
        }

        for (int i = 2; i * i <= limit; i++) {
            if (spf[i] != i) continue;
            for (int j = i * i; j <= limit; j += i) {
                if (spf[j] == j) {
                    spf[j] = i;
                }
            }
        }

        return spf;
    }
}
```

---

## Complexity

- SPF preprocessing:

```text
O(N log log N)
```

with `N = 10^5`

- each factorization after that is very fast, roughly proportional to the number of prime factors

So overall:

```text
Time:  O(N log log N + T log n)
Space: O(N)
```

This is excellent, though for one input value the simpler trial-division solution is usually preferred.

---

# Approach 3 — Recursive Simulation

## Idea

We can write the process recursively:

- compute the sum of prime factors
- if unchanged, return `n`
- otherwise recurse on the new value

This is elegant, though iterative code is usually simpler in Java.

---

## Java Code

```java
class Solution {
    public int smallestValue(int n) {
        int sum = sumPrimeFactors(n);
        if (sum == n) {
            return n;
        }
        return smallestValue(sum);
    }

    private int sumPrimeFactors(int x) {
        int sum = 0;
        int num = x;

        for (int d = 2; d * d <= num; d++) {
            while (x % d == 0) {
                sum += d;
                x /= d;
            }
        }

        if (x > 1) {
            sum += x;
        }

        return sum;
    }
}
```

---

## Complexity

Same asymptotic complexity as the iterative trial-division approach:

```text
Time:  O(T * sqrt(n))
Space: O(T)   // recursion stack
```

Still fine here, but iterative is slightly safer.

---

# Approach 4 — Incorrect Shortcut: “Answer is always the smallest prime factor” (Wrong)

## Idea

One might guess the process always ends at the smallest prime factor.

That is not true.

Example:

```text
n = 12
12 -> 2 + 2 + 3 = 7
```

The smallest prime factor is `2`, but the answer is `7`.

So there is no such shortcut.

---

# Detailed Walkthrough

## Example 1

```text
n = 15
```

Prime factorization:

```text
15 = 3 * 5
```

So:

```text
15 -> 3 + 5 = 8
```

Now factorize `8`:

```text
8 = 2 * 2 * 2
```

So:

```text
8 -> 2 + 2 + 2 = 6
```

Now factorize `6`:

```text
6 = 2 * 3
```

So:

```text
6 -> 2 + 3 = 5
```

Now `5` is prime:

```text
sumPrimeFactors(5) = 5
```

So the process stops.

Answer:

```text
5
```

---

## Example 2

```text
n = 3
```

`3` is prime, so its sum of prime factors is just:

```text
3
```

No change happens.

Answer:

```text
3
```

---

# Important Correctness Argument

At each step, we replace `n` with exactly the sum of its prime factors, as required by the problem.

If `n` is prime, then the sum is `n` itself, so the process stops.

If `n` is composite, the process either decreases or stays the same, and once it reaches a prime or fixed point, it cannot go lower.

So the returned value is exactly the smallest value the process ever reaches.

---

# Common Pitfalls

## 1. Forgetting multiplicity of prime factors

For example:

```text
8 = 2 * 2 * 2
```

So the sum is:

```text
2 + 2 + 2 = 6
```

not just `2`.

---

## 2. Mistaking prime divisors for prime factors with multiplicity

You must count repeated factors every time they divide the number.

---

## 3. Stopping too early

The process continues until the value stops changing, not just for one replacement.

---

## 4. Using the shrinking value incorrectly in the factor loop

When trial dividing, it is fine to reduce `x` while looping, but be careful about the loop condition and final remaining prime.

---

# Best Approach

## Recommended: iterative simulation with trial division

This is the best approach here because:

- it is simple
- it is easy to reason about
- it is fast enough for `n <= 10^5`
- it needs no extra preprocessing

---

# Final Recommended Java Solution

```java
class Solution {
    public int smallestValue(int n) {
        while (true) {
            int sum = sumPrimeFactors(n);
            if (sum == n) {
                return n;
            }
            n = sum;
        }
    }

    private int sumPrimeFactors(int x) {
        int sum = 0;
        int num = x;

        for (int d = 2; d * d <= num; d++) {
            while (x % d == 0) {
                sum += d;
                x /= d;
            }
        }

        if (x > 1) {
            sum += x;
        }

        return sum;
    }
}
```

---

# Complexity Summary

```text
Time:  O(T * sqrt(n))
Space: O(1)
```

where `T` is the number of iterations of the replacement process.

---

# Final Takeaway

The problem is just repeated prime factorization with multiplicity.

The process always stabilizes once the value becomes prime, so the solution is to keep replacing `n` by the sum of its prime factors until it no longer changes.
