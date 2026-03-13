# 1952. Three Divisors

## Problem Restatement

We are given an integer `n`.

We need to return:

- `true` if `n` has **exactly three positive divisors**
- `false` otherwise

A divisor `m` of `n` satisfies:

```text
n = k * m
```

for some integer `k`.

---

## Key Mathematical Insight

A number has exactly **three positive divisors** **if and only if** it is the **square of a prime number**.

Why?

Suppose:

```text
n = p^2
```

where `p` is prime.

Then the divisors of `n` are exactly:

```text
1, p, p^2
```

That is exactly **three divisors**.

Now ask the reverse question:

Can any other kind of number have exactly three divisors?

No.

If a number has exactly three divisors, they must look like:

```text
1, d, n
```

That only happens when the number has exactly one nontrivial divisor, which means the number must be a perfect square of a prime.

So the entire problem reduces to:

1. check if `n` is a perfect square
2. let `root = sqrt(n)`
3. check whether `root` is prime

If both are true, answer is `true`.

---

# Approach 1 — Count Divisors Directly

## Intuition

The most direct solution is to count how many divisors `n` has.

If the count is exactly `3`, return `true`.

This is the simplest brute-force interpretation.

---

## Algorithm

1. Initialize divisor count = `0`
2. Loop from `1` to `n`
3. If `i` divides `n`, increment the count
4. Return whether count equals `3`

---

## Java Code

```java
class Solution {
    public boolean isThree(int n) {
        int count = 0;

        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                count++;
            }
        }

        return count == 3;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Count Divisors Using sqrt(n)

## Intuition

Divisors come in pairs.

If `d` divides `n`, then:

```text
n / d
```

is also a divisor.

So we only need to loop up to `sqrt(n)`.

This is a much better way to count divisors.

---

## Important Detail

If `i * i == n`, then `i` contributes only one divisor.

Otherwise, each valid divisor `i` contributes two divisors:

```text
i and n / i
```

---

## Algorithm

1. Initialize count = `0`
2. Loop `i` from `1` while `i * i <= n`
3. If `i` divides `n`:
   - if `i * i == n`, add `1`
   - else add `2`
4. Return whether count is `3`

---

## Java Code

```java
class Solution {
    public boolean isThree(int n) {
        int count = 0;

        for (int i = 1; i * i <= n; i++) {
            if (n % i == 0) {
                if (i * i == n) {
                    count++;
                } else {
                    count += 2;
                }
            }
        }

        return count == 3;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(sqrt(n))
```

### Space Complexity

```text
O(1)
```

---

# Approach 3 — Perfect Square + Prime Check

## Intuition

Using the key theorem:

> `n` has exactly three divisors iff `n` is the square of a prime.

So instead of counting all divisors, we do:

1. compute `root = sqrt(n)`
2. check whether `root * root == n`
3. check whether `root` is prime

This is the cleanest mathematical solution.

---

## Algorithm

1. Compute integer square root:
   ```text
   root = (int) Math.sqrt(n)
   ```
2. If:
   ```text
   root * root != n
   ```
   return `false`
3. Otherwise, return whether `root` is prime

---

## Java Code

```java
class Solution {
    public boolean isThree(int n) {
        int root = (int) Math.sqrt(n);

        if (root * root != n) {
            return false;
        }

        return isPrime(root);
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;

        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

Let:

```text
root = sqrt(n)
```

Prime checking takes:

```text
O(sqrt(root))
```

So total time is:

```text
O(sqrt(sqrt(n))) = O(n^(1/4))
```

### Space Complexity

```text
O(1)
```

---

# Approach 4 — Precompute Small Primes with Sieve

## Intuition

Since:

```text
1 <= n <= 10^4
```

the square root of `n` is at most `100`.

That means we can precompute all primes up to `100` using a sieve.

Then the logic becomes:

1. check whether `n` is a perfect square
2. check whether its square root is marked prime

This is especially neat when the function may be called many times.

---

## Java Code

```java
class Solution {
    public boolean isThree(int n) {
        int root = (int) Math.sqrt(n);

        if (root * root != n) {
            return false;
        }

        boolean[] isPrime = sieve(100);
        return isPrime[root];
    }

    private boolean[] sieve(int limit) {
        boolean[] isPrime = new boolean[limit + 1];

        for (int i = 2; i <= limit; i++) {
            isPrime[i] = true;
        }

        for (int p = 2; p * p <= limit; p++) {
            if (isPrime[p]) {
                for (int multiple = p * p; multiple <= limit; multiple += p) {
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

Since the sieve limit is only `100`, preprocessing is constant.

### Time Complexity

```text
O(1)
```

for this problem’s fixed constraints.

### Space Complexity

```text
O(1)
```

---

# Correctness Proof

## Claim

An integer `n` has exactly three positive divisors if and only if `n = p^2` for some prime `p`.

### Proof

### Forward direction

Assume `n` has exactly three positive divisors.

Every positive integer has divisor `1` and divisor `n`.

Since there are exactly three divisors, there is exactly one divisor strictly between them. Call it `d`.

So the divisors are:

```text
1, d, n
```

Now divisors usually come in pairs:

```text
d and n / d
```

For there to be only one middle divisor, it must pair with itself:

```text
d = n / d
```

So:

```text
d^2 = n
```

Thus `n` is a perfect square.

Now if `d` were composite, it would have some divisor other than `1` and itself, which would also become a divisor of `n`, giving more than three divisors. Contradiction.

So `d` must be prime.

Hence:

```text
n = p^2
```

for some prime `p`.

### Reverse direction

Assume:

```text
n = p^2
```

where `p` is prime.

Then the only divisors of `n` are:

```text
1, p, p^2
```

because `p` has no divisors other than `1` and itself.

So `n` has exactly three positive divisors.

Proved.

---

# Worked Examples

## Example 1

```text
n = 2
```

Square root:

```text
sqrt(2) is not an integer
```

So `2` is not a perfect square of a prime.

Answer:

```text
false
```

Divisors are:

```text
1, 2
```

Only two divisors.

---

## Example 2

```text
n = 4
```

Square root:

```text
root = 2
```

Check:

```text
2 * 2 = 4
```

Now test whether `2` is prime.

It is prime.

So:

```text
4 = 2^2
```

and divisors are:

```text
1, 2, 4
```

Exactly three divisors.

Answer:

```text
true
```

---

# Edge Cases

## 1. `n = 1`

Divisors:

```text
1
```

Only one divisor.

So answer is `false`.

---

## 2. Prime numbers

Example:

```text
n = 7
```

Prime numbers have exactly two divisors:

```text
1, 7
```

So they are never valid.

---

## 3. Perfect square of a composite number

Example:

```text
n = 16
```

Square root is `4`, but `4` is not prime.

Divisors of `16` are:

```text
1, 2, 4, 8, 16
```

So answer is `false`.

---

## 4. Prime square

Example:

```text
n = 9
```

Square root is `3`, and `3` is prime.

Divisors are:

```text
1, 3, 9
```

Exactly three.

So answer is `true`.

---

# Comparison of Approaches

## Approach 1 — Count all divisors

Pros:

- easiest to understand first

Cons:

- unnecessarily slow

---

## Approach 2 — Count divisors using sqrt(n)

Pros:

- better than full brute force
- still direct

Cons:

- still more work than needed

---

## Approach 3 — Perfect square + prime check

Pros:

- best mathematical insight
- simplest efficient solution
- ideal for interviews

Cons:

- requires noticing the theorem

This is the recommended approach.

---

## Approach 4 — Sieve

Pros:

- great if many queries existed
- very fast for this small bound

Cons:

- slightly more setup for a single query

---

# Final Recommended Java Solution

```java
class Solution {
    public boolean isThree(int n) {
        int root = (int) Math.sqrt(n);

        if (root * root != n) {
            return false;
        }

        if (root < 2) {
            return false;
        }

        for (int d = 2; d * d <= root; d++) {
            if (root % d == 0) {
                return false;
            }
        }

        return true;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n)
Space: O(1)
```

## Approach 2

```text
Time:  O(sqrt(n))
Space: O(1)
```

## Approach 3

```text
Time:  O(n^(1/4))
Space: O(1)
```

## Approach 4

```text
Time:  O(1) for fixed constraints
Space: O(1)
```

---

# Final Takeaway

This problem is really a disguised number theory question.

The decisive fact is:

> A number has exactly three divisors **iff** it is the square of a prime.

So the clean solution is:

1. check whether `n` is a perfect square
2. check whether its square root is prime
