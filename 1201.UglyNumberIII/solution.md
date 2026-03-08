# Nth Ugly Number (Divisible by a OR b OR c)

## Problem

An ugly number is a positive integer divisible by **a**, **b**, or **c**.

Given integers `n, a, b, c`, return the **nth ugly number**.

---

# Core Idea

Instead of generating ugly numbers one by one, we:

1. Use **binary search** on the answer.
2. Use **inclusion–exclusion principle** to count how many ugly numbers ≤ X.
3. Find the smallest X such that count(X) ≥ n.

That X is the nth ugly number.

---

# Counting Formula (Rank Function)

Let:

- `ab = lcm(a, b)`
- `ac = lcm(a, c)`
- `bc = lcm(b, c)`
- `abc = lcm(a, b, c)`

The number of ugly numbers ≤ X is:

rank(X) =
X/a + X/b + X/c

- X/ab - X/ac - X/bc

* X/abc

Explanation:

- Add multiples of a, b, c.
- Subtract pair overlaps.
- Add triple overlap back.

This gives exactly how many numbers ≤ X are divisible by at least one of a, b, or c.

Since rank(X) is monotonic increasing, binary search applies.

---

# Binary Search Strategy

We search for the smallest X such that:

rank(X) ≥ n

If rank(mid) < n → move right
If rank(mid) ≥ n → move left

---

# Java Implementation

```java
class Solution {
    public int nthUglyNumber(int n, int a, int b, int c) {
        long A = a, B = b, C = c;

        long ab = lcm(A, B);
        long ac = lcm(A, C);
        long bc = lcm(B, C);
        long abc = lcm(A, bc);

        long lo = 1, hi = 2_000_000_000L;

        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;
            long cnt = count(mid, A, B, C, ab, ac, bc, abc);

            if (cnt >= n)
                hi = mid;
            else
                lo = mid + 1;
        }

        return (int) lo;
    }

    private long count(long x, long a, long b, long c,
                       long ab, long ac, long bc, long abc) {
        return x / a + x / b + x / c
             - x / ab - x / ac - x / bc
             + x / abc;
    }

    private long gcd(long x, long y) {
        while (y != 0) {
            long temp = x % y;
            x = y;
            y = temp;
        }
        return x;
    }

    private long lcm(long x, long y) {
        return x / gcd(x, y) * y;
    }
}
```

---

# Why LCM is Necessary

We use LCM to correctly count overlaps.

Example:
If a=4 and b=6, common multiples are multiples of 12 (LCM),
not simply a\*b.

Using LCM prevents overcounting.

---

# Time Complexity

Binary search range ≈ 2 × 10^9 → O(log X)

Each step does constant arithmetic.

Time Complexity:

O(log X)

Since X ≤ 2e9, this is about 31–32 iterations.

---

# Space Complexity

O(1)

Only constant extra space is used.

---

# Key Insight

We do not generate the sequence.

We compute:

"How many valid numbers exist up to X?"

Then binary search the smallest X whose count reaches n.

This transforms a sequence problem into a counting problem.
