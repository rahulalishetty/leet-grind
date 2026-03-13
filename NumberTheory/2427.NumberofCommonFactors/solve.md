# 2427. Number of Common Factors

## Problem Restatement

We are given two positive integers:

- `a`
- `b`

We need to return the **number of common factors** of `a` and `b`.

A number `x` is a common factor if:

```text
a % x == 0 and b % x == 0
```

---

## Key Observation

A number is a common factor of `a` and `b` **if and only if** it is a divisor of:

```text
gcd(a, b)
```

So instead of thinking about both numbers separately, we can reduce the problem to:

1. compute `g = gcd(a, b)`
2. count how many divisors `g` has

This gives a more mathematical and often cleaner solution.

Because:

```text
1 <= a, b <= 1000
```

even brute-force solutions are fully acceptable here, but it is still worth understanding the stronger approach.

---

# Approach 1 — Brute Force Check All Numbers Up to min(a, b)

## Intuition

The most direct approach is:

- any common factor must be at most `min(a, b)`
- so try every number from `1` to `min(a, b)`
- count how many divide both numbers

This is simple and easy to understand.

---

## Algorithm

1. Let `limit = min(a, b)`
2. Initialize `count = 0`
3. For each `x` from `1` to `limit`:
   - if `a % x == 0` and `b % x == 0`, increment `count`
4. Return `count`

---

## Java Code

```java
class Solution {
    public int commonFactors(int a, int b) {
        int limit = Math.min(a, b);
        int count = 0;

        for (int x = 1; x <= limit; x++) {
            if (a % x == 0 && b % x == 0) {
                count++;
            }
        }

        return count;
    }
}
```

---

## Complexity Analysis

### Time Complexity

We check all values from `1` to `min(a, b)`:

```text
O(min(a, b))
```

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Compute GCD, Then Count Divisors Brute Force

## Intuition

A common factor of `a` and `b` is exactly a divisor of:

```text
gcd(a, b)
```

So first compute:

```text
g = gcd(a, b)
```

Then count how many divisors `g` has by checking all numbers from `1` to `g`.

This separates the problem into two smaller ideas:

- Euclidean algorithm for GCD
- divisor counting

---

## Algorithm

1. Compute `g = gcd(a, b)`
2. Initialize `count = 0`
3. For each `x` from `1` to `g`:
   - if `g % x == 0`, increment `count`
4. Return `count`

---

## Java Code

```java
class Solution {
    public int commonFactors(int a, int b) {
        int g = gcd(a, b);
        int count = 0;

        for (int x = 1; x <= g; x++) {
            if (g % x == 0) {
                count++;
            }
        }

        return count;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

### Time Complexity

- GCD: `O(log(min(a, b)))`
- divisor scan up to `g`: `O(g)`

Overall:

```text
O(g + log(min(a, b)))
```

Since `g <= 1000`, this is easily fine.

### Space Complexity

```text
O(1)
```

---

# Approach 3 — Compute GCD, Then Count Divisors Using sqrt(g)

## Intuition

Approach 2 still checks every number from `1` to `g`.

But divisors come in pairs.

If `x` divides `g`, then:

```text
g / x
```

also divides `g`.

So we only need to iterate up to:

```text
sqrt(g)
```

This is the most efficient general-purpose approach.

---

## Important Detail

If:

```text
x * x == g
```

then `x` contributes only **one** divisor.

Otherwise, it contributes **two** divisors:

- `x`
- `g / x`

---

## Algorithm

1. Compute `g = gcd(a, b)`
2. Initialize `count = 0`
3. For each `x` from `1` while `x * x <= g`:
   - if `g % x == 0`:
     - if `x * x == g`, add `1`
     - else add `2`
4. Return `count`

---

## Java Code

```java
class Solution {
    public int commonFactors(int a, int b) {
        int g = gcd(a, b);
        int count = 0;

        for (int x = 1; x * x <= g; x++) {
            if (g % x == 0) {
                if (x * x == g) {
                    count++;
                } else {
                    count += 2;
                }
            }
        }

        return count;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

### Time Complexity

- GCD computation:

```text
O(log(min(a, b)))
```

- divisor counting up to `sqrt(g)`:

```text
O(sqrt(g))
```

Overall:

```text
O(log(min(a, b)) + sqrt(g))
```

This is the best clean mathematical solution.

### Space Complexity

```text
O(1)
```

---

# Approach 4 — Precompute Common Factors Directly with GCD Insight

## Intuition

Since the constraints are tiny (`<= 1000`), another simple practical solution is:

- compute `g = gcd(a, b)`
- loop through `1..1000`
- count how many numbers divide `g`

This is not the most elegant mathematically, but it is completely valid because the bound is fixed and small.

This is more of a “constraint-driven” approach than an algorithmically optimal one.

---

## Java Code

```java
class Solution {
    public int commonFactors(int a, int b) {
        int g = gcd(a, b);
        int count = 0;

        for (int x = 1; x <= 1000; x++) {
            if (g % x == 0) {
                count++;
            }
        }

        return count;
    }

    private int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }
}
```

---

## Complexity Analysis

Since the loop always runs at most `1000` times:

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

# Why GCD Solves the Problem

## Claim

The common factors of `a` and `b` are exactly the divisors of `gcd(a, b)`.

### Proof

Let:

```text
g = gcd(a, b)
```

### Forward direction

If `x` is a common factor of `a` and `b`, then `x` divides both numbers.

By definition of greatest common divisor, every common divisor of `a` and `b` must divide `g`.

So `x` is a divisor of `g`.

### Reverse direction

If `x` divides `g`, and `g` divides both `a` and `b`, then `x` also divides both `a` and `b`.

So `x` is a common factor.

Therefore, the set of common factors of `a` and `b` is exactly the set of divisors of `gcd(a, b)`.

Proved.

---

# Worked Examples

## Example 1

```text
a = 12, b = 6
```

Compute:

```text
gcd(12, 6) = 6
```

Divisors of `6` are:

```text
1, 2, 3, 6
```

Count:

```text
4
```

So answer is:

```text
4
```

---

## Example 2

```text
a = 25, b = 30
```

Compute:

```text
gcd(25, 30) = 5
```

Divisors of `5` are:

```text
1, 5
```

Count:

```text
2
```

So answer is:

```text
2
```

---

# Edge Cases

## 1. `a == b`

Example:

```text
a = 8, b = 8
```

Then all common factors are just the divisors of `8`:

```text
1, 2, 4, 8
```

Answer is:

```text
4
```

---

## 2. GCD is 1

Example:

```text
a = 7, b = 20
```

Then:

```text
gcd(7, 20) = 1
```

The only common factor is:

```text
1
```

So answer is:

```text
1
```

---

## 3. One number divides the other

Example:

```text
a = 18, b = 6
```

Then:

```text
gcd(18, 6) = 6
```

So the common factors are exactly divisors of `6`.

---

# Comparison of Approaches

## Approach 1 — Check all numbers up to min(a, b)

Pros:

- easiest to understand
- no extra mathematical reduction required

Cons:

- does more work than necessary

---

## Approach 2 — GCD + divisor scan up to g

Pros:

- cleaner than Approach 1
- uses the main mathematical idea

Cons:

- still scans more than needed

---

## Approach 3 — GCD + sqrt divisor counting

Pros:

- strongest mathematical solution
- best asymptotic complexity
- ideal for interviews

Cons:

- slightly more logic because of divisor pairing

This is the recommended approach.

---

## Approach 4 — Fixed-bound loop

Pros:

- very simple under these constraints
- constant-time for this exact problem

Cons:

- not scalable
- too tied to the problem bounds

---

# Final Recommended Java Solution

```java
class Solution {
    public int commonFactors(int a, int b) {
        int g = gcd(a, b);
        int count = 0;

        for (int x = 1; x * x <= g; x++) {
            if (g % x == 0) {
                if (x * x == g) {
                    count++;
                } else {
                    count += 2;
                }
            }
        }

        return count;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(min(a, b))
Space: O(1)
```

## Approach 2

```text
Time:  O(g + log(min(a, b)))
Space: O(1)
```

## Approach 3

```text
Time:  O(sqrt(g) + log(min(a, b)))
Space: O(1)
```

## Approach 4

```text
Time:  O(1) for fixed constraints
Space: O(1)
```

where:

```text
g = gcd(a, b)
```

---

# Final Takeaway

The most important reduction is:

> Common factors of `a` and `b` are exactly the divisors of `gcd(a, b)`.

So the cleanest solution is:

1. compute `gcd(a, b)`
2. count how many divisors that GCD has
3. use divisor pairing up to `sqrt(g)` for the most efficient version
