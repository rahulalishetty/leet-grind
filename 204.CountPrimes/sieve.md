# Sieve of Eratosthenes (Count Primes `< n`) — Clear, Detailed Summary

## Problem

Given an integer `n`, count how many **prime numbers** are **strictly less than `n`**.

A prime number is an integer `> 1` with exactly two divisors: `1` and itself.

---

## Core idea

Instead of testing each number for primality (which repeats work), the **Sieve of Eratosthenes**:

- assumes all numbers are prime at first,
- then **crosses out (marks) multiples** of each prime,
- whatever remains unmarked is prime.

You can think of it as building a “composite blacklist”.

---

## Intuition with a small example (primes < 21)

1. Create an array indexed `0..20` (each index represents the number).
2. Start with the smallest prime `2`.
3. Mark all multiples of `2` as composite: `4, 6, 8, 10, ...`.
4. Move to the next unmarked number: `3` (prime).
5. Mark multiples of `3`: `6, 9, 12, 15, 18`.
6. Continue. At the end, all unmarked numbers (from `2` upward) are primes.

Note: a number may be marked multiple times (e.g., `12` by `2` and `3`). That is fine.

---

## Why the outer loop stops at √n

If `x` is composite, then `x = a * b` with `a, b > 1`.
At least one of `a` or `b` must be `≤ √x`. Therefore:

- Every composite `< n` has a factor `≤ √n`.

So once you process all candidate `p` up to `⌊√n⌋`, every composite `< n` must already be marked.

**Practical rule:** outer loop uses `p * p < n` (or `p <= sqrt(n)`).

---

## Why the inner loop starts at `p*p` (not `2*p`)

When processing a prime `p`:

- Multiples `p*2, p*3, ..., p*(p-1)` are all `< p*p`.
- Each of those has a smaller factor (`2, 3, ..., p-1`) and would already have been marked when processing that smaller factor earlier.

So starting at `2*p` wastes work; starting at `p*p` avoids repeats.

**Example (p = 7, n = 50):**

- `7*2 = 14` already marked by `2`
- `7*3 = 21` already marked by `3`
- `7*5 = 35` already marked by `5`
- First new composite not guaranteed to be marked earlier is `7*7 = 49`

---

## Algorithm (step-by-step)

For counting primes `< n`:

1. If `n <= 2`, answer is `0`.
2. Create an array `isComposite[0..n-1]` initialized `false`.
   - `false` means “not known composite yet”.
   - `true` means “composite”.
3. For `p` from `2` while `p*p < n`:
   - if `isComposite[p] == false`, then `p` is prime:
     - mark `p*p, p*p + p, p*p + 2p, ... < n` as composite.
4. Count how many indices `i` in `[2, n)` still have `isComposite[i] == false`.

---

## Code (Java) — as in your example

```java
class Solution {
    public int countPrimes(int n) {
        if (n <= 2) {
            return 0;
        }

        boolean[] numbers = new boolean[n]; // false => "not marked composite"
        for (int p = 2; p <= (int) Math.sqrt(n); ++p) {
            if (numbers[p] == false) {
                for (int j = p * p; j < n; j += p) {
                    numbers[j] = true; // mark composite
                }
            }
        }

        int numberOfPrimes = 0;
        for (int i = 2; i < n; i++) {
            if (numbers[i] == false) {
                ++numberOfPrimes;
            }
        }

        return numberOfPrimes;
    }
}
```

### Notes on this Java version

- `numbers[i] == true` means **composite** (a bit inverted naming; common in competitive code).
- Outer loop uses `p <= sqrt(n)`. Equivalent (often preferred) condition: `p * p < n`.
- Inner loop starts at `p * p` to avoid redundant markings.

---

## Code (Python) — canonical sieve (count primes `< n`)

```python
def count_primes(n: int) -> int:
    if n <= 2:
        return 0

    is_composite = [False] * n
    p = 2
    while p * p < n:
        if not is_composite[p]:
            for j in range(p * p, n, p):
                is_composite[j] = True
        p += 1

    return sum(1 for i in range(2, n) if not is_composite[i])
```

### Python speed note (slice assignment idea)

For large `n`, Python loops are slower than operations implemented in C. A common micro-optimization is to mark multiples using slicing, e.g.:

```python
is_composite[p*p:n:p] = [True] * len(is_composite[p*p:n:p])
```

This can be faster in CPython because it reduces Python-level loop overhead. (It does allocate a temporary list; tradeoffs depend on `n`.)

---

## “Array vs Dictionary” discussion (why dictionary can be slower in practice)

**On paper**, a dictionary/HashMap approach can avoid “initializing an `O(n)` array”, because:

- only composites are stored as keys
- “not present in dict => prime”

**In practice**, for typical constraints, arrays win because:

- arrays are contiguous and cache-friendly (excellent locality)
- dict operations have higher constant factors:
  - hashing, probing, pointer chasing
  - more cache misses

So even if both are `O(n)` space in the end (you still mark lots of composites), the array usually performs better and is simpler.

---

## Complexity Analysis

### Space

- `O(n)` for the boolean array of size `n`.

### Time

Sieve marking work is approximately:

- for prime `2`: mark about `n/2` numbers
- for prime `3`: mark about `n/3` numbers
- for prime `5`: mark about `n/5` numbers
- ...

So total marking is:

\[
n\left(\frac{1}{2} + \frac{1}{3} + \frac{1}{5} + \cdots\right)
\]

The sum of reciprocals of primes up to `n` grows like `log log n`, so the sieve runtime is:

- **Marking:** `O(n log log n)`
- **Counting remaining primes:** `O(n)`

Overall:

\[
O(n \log\log n + n) = O(n \log\log n)
\]

(You’ll often see just `O(n log log n)`.)

---

## Key takeaways (quick checks)

- Outer loop only needs to consider `p` up to `√n`.
- Inner loop should start at `p*p` to avoid redundant marks.
- Boolean array sieve is usually fastest in practice for typical constraints.
