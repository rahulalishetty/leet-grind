# 2413. Smallest Even Multiple

## Problem Restatement

We are given a positive integer `n`.

We need to return the **smallest positive integer** that is a multiple of both:

- `2`
- `n`

This is exactly the **least common multiple** of `2` and `n`:

```text
LCM(2, n)
```

---

## Key Observation

There are only two cases:

### Case 1: `n` is even

If `n` is already even, then `n` itself is divisible by `2`.

So `n` is already a multiple of both `2` and `n`.

Thus:

```text
answer = n
```

### Case 2: `n` is odd

If `n` is odd, then `n` is not divisible by `2`.

So the smallest number divisible by both `n` and `2` must be:

```text
2 * n
```

Thus:

```text
answer = 2 * n
```

So the whole problem reduces to:

```text
if n is even -> return n
else return 2 * n
```

---

# Approach 1 — Direct Case Analysis Using Parity

## Intuition

This is the simplest and best approach.

Check whether `n` is even:

- if yes, return `n`
- otherwise, return `2 * n`

---

## Java Code

```java
class Solution {
    public int smallestEvenMultiple(int n) {
        if (n % 2 == 0) {
            return n;
        }
        return 2 * n;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(1)
```

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Use Least Common Multiple Formula

## Intuition

The smallest positive integer that is a multiple of both `2` and `n` is their LCM.

We know:

```text
LCM(a, b) = (a * b) / GCD(a, b)
```

So here:

```text
LCM(2, n) = (2 * n) / GCD(2, n)
```

Since `GCD(2, n)` is either `1` or `2`, this automatically gives the correct result.

---

## Java Code

```java
class Solution {
    public int smallestEvenMultiple(int n) {
        return (2 * n) / gcd(2, n);
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

The GCD of `2` and `n` is computed in constant time in practice:

```text
O(log(min(2, n))) = O(1)
```

### Space Complexity

```text
O(1)
```

---

# Approach 3 — Brute Force Search

## Intuition

We can also solve it by directly searching for the first positive number that is divisible by both `2` and `n`.

This is not the best method, but it is a valid way to understand the problem.

---

## Algorithm

Start from `n` and keep increasing until finding a value divisible by `2`.

Since every multiple of `n` looks like:

```text
n, 2n, 3n, 4n, ...
```

the first one that is even is the answer.

---

## Java Code

```java
class Solution {
    public int smallestEvenMultiple(int n) {
        int multiple = n;

        while (true) {
            if (multiple % 2 == 0 && multiple % n == 0) {
                return multiple;
            }
            multiple += n;
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

In the worst case, for odd `n`, we check:

- `n` (odd, fails)
- `2n` (works)

So at most a constant number of iterations here.

Thus practically:

```text
O(1)
```

### Space Complexity

```text
O(1)
```

---

# Approach 4 — Bitwise Even Check

## Intuition

An integer is even if its least significant bit is `0`.

So instead of checking:

```java
n % 2 == 0
```

we can check:

```java
(n & 1) == 0
```

This leads to the same logic as Approach 1.

---

## Java Code

```java
class Solution {
    public int smallestEvenMultiple(int n) {
        return ((n & 1) == 0) ? n : 2 * n;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(1)
```

### Space Complexity

```text
O(1)
```

---

# Worked Examples

## Example 1

```text
n = 5
```

`5` is odd.

So it is not divisible by `2`.

The smallest positive integer divisible by both `5` and `2` is:

```text
2 * 5 = 10
```

Answer:

```text
10
```

---

## Example 2

```text
n = 6
```

`6` is even.

So `6` is already divisible by both `6` and `2`.

Answer:

```text
6
```

---

# Correctness Argument

## Claim

The smallest positive integer that is a multiple of both `2` and `n` is:

- `n` if `n` is even
- `2n` if `n` is odd

### Proof

If `n` is even, then `2` divides `n`, and clearly `n` divides itself. So `n` is a common multiple of `2` and `n`. No smaller positive number can be a multiple of `n`, so `n` is the smallest common multiple.

If `n` is odd, then `n` is not divisible by `2`, so `n` itself is not a common multiple. The next multiple of `n` is `2n`, and it is divisible by both `n` and `2`. Hence `2n` is the smallest common multiple.

Proved.

---

# Comparison of Approaches

## Approach 1 — Parity check

Pros:

- simplest
- shortest
- best for interviews and production

Cons:

- none

This is the recommended approach.

---

## Approach 2 — LCM formula

Pros:

- mathematically general
- good if you want to emphasize LCM/GCD connection

Cons:

- slightly more code than necessary

---

## Approach 3 — Brute force

Pros:

- intuitive
- easy to reason about

Cons:

- less elegant
- not the intended mathematical shortcut

---

## Approach 4 — Bitwise parity check

Pros:

- concise
- same performance as Approach 1

Cons:

- slightly less readable for beginners

---

# Final Recommended Java Solution

```java
class Solution {
    public int smallestEvenMultiple(int n) {
        return (n % 2 == 0) ? n : 2 * n;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(1)
Space: O(1)
```

## Approach 2

```text
Time:  O(1)
Space: O(1)
```

## Approach 3

```text
Time:  O(1)
Space: O(1)
```

## Approach 4

```text
Time:  O(1)
Space: O(1)
```

---

# Final Takeaway

This problem is just a disguised parity observation.

The answer is:

- `n` if `n` is even
- `2 * n` if `n` is odd

So the smallest even multiple is simply:

```text
(n % 2 == 0) ? n : 2 * n
```
