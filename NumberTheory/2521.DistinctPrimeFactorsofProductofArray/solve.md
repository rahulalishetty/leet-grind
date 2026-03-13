# 2521. Distinct Prime Factors of Product of Array — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int distinctPrimeFactors(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`.

We need the number of **distinct prime factors** in the product of all elements:

```text
nums[0] * nums[1] * nums[2] * ...
```

We do **not** need the product itself.

We only need the set of prime numbers that divide it.

---

# Core Insight

The prime factors of a product are just the union of the prime factors of each multiplicand.

So instead of computing:

```text
product = nums[0] * nums[1] * ...
```

which can become huge, we can simply:

1. factorize each number
2. add its prime factors to a set
3. return the size of the set

This is the whole idea.

---

# Why This Works

Suppose:

```text
a = p1^x * p2^y
b = p2^m * p3^n
```

Then:

```text
a * b = p1^x * p2^(y+m) * p3^n
```

So the distinct prime factors of `a * b` are just:

```text
{p1, p2, p3}
```

That means we never need to form the full product.

We only need the union of prime factors across all array elements.

---

# Approach 1 — Trial Division with a HashSet (Recommended)

## Idea

For each number `x` in `nums`:

- try all divisors `d` from `2` to `sqrt(x)`
- whenever `d` divides `x`, add `d` to the set and divide out all copies of it
- after the loop, if `x > 1`, then the remaining `x` itself is prime, so add it too

At the end, the answer is:

```text
set.size()
```

---

## Why divide out all copies?

If `x = 12`, then:

```text
12 = 2 * 2 * 3
```

When we find factor `2`, we do not want to keep checking the same factor repeatedly for distinctness.
So we divide out all copies:

```text
12 -> 6 -> 3
```

This keeps the factorization efficient.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int distinctPrimeFactors(int[] nums) {
        Set<Integer> primes = new HashSet<>();

        for (int x : nums) {
            int num = x;

            for (int d = 2; d * d <= num; d++) {
                if (num % d == 0) {
                    primes.add(d);
                    while (num % d == 0) {
                        num /= d;
                    }
                }
            }

            if (num > 1) {
                primes.add(num);
            }
        }

        return primes.size();
    }
}
```

---

## Complexity

Let `n = nums.length`, and let `M` be the maximum element in `nums`.

For each number, trial division costs about:

```text
O(sqrt(M))
```

So total complexity is:

```text
Time:  O(n * sqrt(M))
Space: O(P)
```

where `P` is the number of distinct prime factors collected.

Since:

```text
nums[i] <= 1000
```

this is easily fast enough.

---

# Approach 2 — Trial Division with a Boolean Array

## Idea

Because `nums[i] <= 1000`, every prime factor is also at most `1000`.

So instead of a `HashSet`, we can use a boolean array:

```text
seen[p] = true if prime p appears
```

Then count how many entries are true.

This can be slightly faster than using a set.

---

## Java Code

```java
class Solution {
    public int distinctPrimeFactors(int[] nums) {
        boolean[] seen = new boolean[1001];

        for (int x : nums) {
            int num = x;

            for (int d = 2; d * d <= num; d++) {
                if (num % d == 0) {
                    seen[d] = true;
                    while (num % d == 0) {
                        num /= d;
                    }
                }
            }

            if (num > 1) {
                seen[num] = true;
            }
        }

        int count = 0;
        for (boolean b : seen) {
            if (b) count++;
        }

        return count;
    }
}
```

---

## Complexity

Same asymptotic complexity:

```text
Time:  O(n * sqrt(M))
Space: O(M)
```

Since `M <= 1000`, this is very small.

---

# Approach 3 — Precompute Smallest Prime Factor (SPF)

## Idea

We can preprocess the smallest prime factor for every number from `2` to `1000`.

Then factorization of each number becomes very fast:

- repeatedly look up the smallest prime factor
- mark it
- divide it out

This is a nice technique when many factorizations are needed.

---

## Java Code

```java
class Solution {
    public int distinctPrimeFactors(int[] nums) {
        int limit = 1000;
        int[] spf = buildSPF(limit);
        boolean[] seen = new boolean[limit + 1];

        for (int x : nums) {
            int num = x;
            while (num > 1) {
                int p = spf[num];
                seen[p] = true;
                while (num % p == 0) {
                    num /= p;
                }
            }
        }

        int ans = 0;
        for (boolean b : seen) {
            if (b) ans++;
        }
        return ans;
    }

    private int[] buildSPF(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            spf[i] = i;
        }

        for (int i = 2; i * i <= n; i++) {
            if (spf[i] != i) continue;
            for (int j = i * i; j <= n; j += i) {
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

SPF preprocessing:

```text
O(M log log M)
```

Factorization of all numbers is very fast afterward.

Overall:

```text
Time:  O(M log log M + total factorization work)
Space: O(M)
```

For `M = 1000`, this is excellent, though the simpler trial-division approach is already enough.

---

# Approach 4 — Compute the Full Product First (Not Recommended)

## Idea

One might think of multiplying all elements together and then factorizing the product.

This is a bad idea because the product can become extremely large and overflow standard numeric types.

So even though the mathematical definition mentions the product, we should never explicitly build it.

---

# Detailed Walkthrough

## Example 1

```text
nums = [2,4,3,7,10,6]
```

Factorize each number:

- `2` -> `{2}`
- `4 = 2^2` -> `{2}`
- `3` -> `{3}`
- `7` -> `{7}`
- `10 = 2 * 5` -> `{2,5}`
- `6 = 2 * 3` -> `{2,3}`

Union of all prime factors:

```text
{2,3,5,7}
```

So the answer is:

```text
4
```

---

## Example 2

```text
nums = [2,4,8,16]
```

Factorize:

- `2` -> `{2}`
- `4` -> `{2}`
- `8` -> `{2}`
- `16` -> `{2}`

Union:

```text
{2}
```

So the answer is:

```text
1
```

---

# Important Correctness Argument

Every prime factor of the full product must come from at least one element of the array.

And every prime factor of any array element will also divide the full product.

Therefore:

```text
prime factors of product(nums)
=
union of prime factors of nums[i]
```

So collecting prime factors from each element and taking their union is exactly correct.

---

# Common Pitfalls

## 1. Trying to multiply all numbers first

This risks overflow and is unnecessary.

---

## 2. Forgetting to include repeated prime factors only once in the final answer

We only want distinct primes, not total multiplicity across the whole product.

---

## 3. Forgetting the remaining prime after trial division

After removing all small factors, if `num > 1`, it must itself be prime and must be added.

---

## 4. Using the loop condition with the original value instead of the shrinking value incorrectly

Trial division is usually written with the shrinking value, and that works fine as long as the final remainder is handled properly.

---

# Best Approach

## Recommended: trial division + set

This is the cleanest solution because:

- it is simple
- it avoids overflow
- it is fully efficient for the given constraints

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int distinctPrimeFactors(int[] nums) {
        Set<Integer> primes = new HashSet<>();

        for (int x : nums) {
            int num = x;

            for (int d = 2; d * d <= num; d++) {
                if (num % d == 0) {
                    primes.add(d);
                    while (num % d == 0) {
                        num /= d;
                    }
                }
            }

            if (num > 1) {
                primes.add(num);
            }
        }

        return primes.size();
    }
}
```

---

# Complexity Summary

```text
Time:  O(n * sqrt(M))
Space: O(P)
```

where:

- `n = nums.length`
- `M = max(nums[i])`
- `P = number of distinct prime factors found`

Given:

```text
nums[i] <= 1000
```

this is comfortably efficient.

---

# Final Takeaway

You never need the product itself.

The only thing that matters is which prime factors appear anywhere in the array.

So the solution is simply:

- factorize each number
- collect distinct primes
- return how many unique primes were seen
