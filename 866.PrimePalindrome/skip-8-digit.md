# Approach 2: Brute Force with Mathematical Shortcut (Skip 8-digit Range)

## Problem

Given an integer `N`, return the smallest **prime palindrome** `X` such that `X >= N`.

---

## Intuition

The simplest brute force is:

1. Check if `N` is a palindrome.
2. If yes, check if it is prime.
3. If not, increment `N` and repeat.

This can still work if we add one key math shortcut:

> **All even-length palindromes are divisible by 11 (except 11).**

So there are **no** 8-digit prime palindromes.
That means when `N` is in the 8-digit range `(10,000,000 .. 99,999,999)`, we can jump directly to `100,000,000` (9 digits).

This removes the biggest slow region of the naive scan.

---

## Mathematical Shortcut (Why skipping 8 digits is valid)

- Any 8-digit palindrome has even length.
- Any even-length palindrome > 11 is divisible by 11.
- Hence, no 8-digit palindrome is prime.

Therefore, if `N` enters the 8-digit interval, the answer cannot be in that interval, so skipping is safe.

---

## Algorithm

Loop forever:

1. If `N` is a palindrome (`reverse(N) == N`) and `N` is prime → return `N`.
2. Otherwise `N++`.
3. If `N` is now in `(10,000,000 .. 99,999,999)`, set `N = 100,000,000`.

---

## Java Code (Brute Force + Skip Range)

```java
class Solution {
    public int primePalindrome(int N) {
        while (true) {
            if (isPalindrome(N) && isPrime(N)) return N;
            N++;

            // Skip 8-digit numbers entirely:
            // any even-length palindrome is divisible by 11, so no 8-digit prime palindromes exist
            if (10_000_000 < N && N < 100_000_000) {
                N = 100_000_000;
            }
        }
    }

    private boolean isPalindrome(int n) {
        return n == reverse(n);
    }

    private int reverse(int n) {
        int ans = 0;
        while (n > 0) {
            ans = ans * 10 + (n % 10);
            n /= 10;
        }
        return ans;
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

Let `T` be the number of integers tested until the answer.
Each iteration does:

- Palindrome test: `O(log N)` digit operations (reverse)
- Prime test (only if palindrome): `O(sqrt(N))` worst-case

So the total time is roughly:

- `O(T * log N)` for palindrome checks
- plus `O(#palindromes tested * sqrt(N))` for primality checks

The key improvement is that `T` is **dramatically reduced** because the scan skips the entire 8-digit region.

**Space:** `O(1)`.

---

## When to prefer this approach

- You want the simplest implementation.
- You rely on the 11-divisibility property to avoid the slow region.
- Constraints are small enough that brute force remains acceptable.
