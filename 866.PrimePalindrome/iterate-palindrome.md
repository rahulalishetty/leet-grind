# Approach 1: Iterate Palindromes via Roots

## Problem

Given an integer `N`, return the smallest **prime palindrome** `X` such that `X >= N`.

A **palindrome** reads the same forward and backward.
A **prime** has no positive divisors other than `1` and itself.

---

## Why not naive brute force over all integers?

A naive scan `N, N+1, N+2, ...` can be slow because:

- Palindromes are relatively sparse.
- Primality testing is expensive compared to palindrome checking.

Also, palindromes and primes are **not independent**. A critical fact:

> **Every palindrome with an even number of digits is divisible by 11** (except `11`), so it cannot be prime.

This means most even-length palindromes are guaranteed composite, and we can bias our search toward **odd-length palindromes**.

---

## Key Math Fact: Even-length palindromes are divisible by 11

Let an even-length palindrome have digits:

`a_{d-1} a_{d-2} ... a_1 a_0` with `a_i = a_{d-1-i}`, and `d` even.

Using the divisibility rule for 11:
A number is divisible by 11 iff the alternating sum of digits is divisible by 11.

In an even-length palindrome, digits pair symmetrically with opposite parity positions, so the alternating sum cancels to 0 → divisible by 11.

Therefore:

- **All even-digit palindromes > 11 are composite.**

---

## Intuition

Instead of scanning all numbers, **generate palindromes directly**.

Any palindrome is determined by its **root** (first half, plus middle digit for odd length).
Example (odd length):

- Root `123` → palindrome `12321`
- Next root `124` → palindrome `12421`

For a root with `L` digits:

- Odd-length palindrome has `2L-1` digits
- Even-length palindrome has `2L` digits

We generate palindromes in increasing order by iterating roots in increasing order.

---

## Algorithm

For `L = 1..5` (enough to cover answers up to 9 digits in this problem):

1. For each `root` with `L` digits:
   - Build the **odd-length** palindrome from `root`
   - If palindrome `>= N` and is prime → return it
2. Optionally (not required for correctness if you use the 11 shortcut), also build **even-length** palindrome from `root` and test it.

Why `L <= 5` works in LeetCode constraints:

- It’s known (for this problem’s constraints) that a 9-digit prime palindrome exists early (e.g., `100030001`), so scanning roots up to 5 digits suffices.

---

## Java Code (Root → Palindrome Enumeration)

```java
class Solution {
    public int primePalindrome(int N) {
        // L is the number of digits in the root
        for (int L = 1; L <= 5; ++L) {

            // 1) Generate odd-length palindromes (2L-1 digits)
            int start = (int) Math.pow(10, L - 1);
            int end   = (int) Math.pow(10, L);
            for (int root = start; root < end; ++root) {
                int x = makeOddPalindrome(root);
                if (x >= N && isPrime(x)) return x;
            }

            // 2) (Optional) Generate even-length palindromes (2L digits)
            // These are divisible by 11 for length > 2, so mostly redundant,
            // but included for completeness.
            for (int root = start; root < end; ++root) {
                int x = makeEvenPalindrome(root);
                if (x >= N && isPrime(x)) return x;
            }
        }
        throw new IllegalStateException("Unreachable under problem constraints");
    }

    // Example: root=123 -> 12321
    private int makeOddPalindrome(int root) {
        int x = root;
        int t = root / 10;        // skip last digit to avoid duplicating the middle
        while (t > 0) {
            x = x * 10 + (t % 10);
            t /= 10;
        }
        return x;
    }

    // Example: root=123 -> 123321
    private int makeEvenPalindrome(int root) {
        int x = root;
        int t = root;
        while (t > 0) {
            x = x * 10 + (t % 10);
            t /= 10;
        }
        return x;
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        int r = (int) Math.sqrt(n);
        for (int d = 3; d <= r; d += 2) {
            if (n % d == 0) return false;
        }
        return true;
    }
}
```

---

## Complexity Discussion

Let `P` be the number of palindromes generated until the answer.

- Generating each palindrome is `O(L)` digit work.
- Primality test is `O(sqrt(X))`.

So runtime is roughly:

- `O( sum_{tested palindromes} sqrt(palindrome) )`

In practice (under LeetCode constraints), this passes because:

- The search space of palindromes up to 9 digits is relatively small.
- You avoid scanning non-palindromes entirely.

**Space:** `O(1)` (ignoring call stack and integer digit operations).

---

## Notes / Caveat

The “how many prime palindromes exist” question is deep mathematically. For the coding problem, we rely on the bounded constraints and known existence of a 9-digit prime palindrome early in the scan.
