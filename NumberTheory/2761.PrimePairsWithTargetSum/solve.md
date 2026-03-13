# 2761. Prime Pairs With Target Sum

## Problem Restatement

We are given an integer `n`.

We need to return all pairs:

```text
[x, y]
```

such that:

- `1 <= x <= y <= n`
- `x + y == n`
- both `x` and `y` are prime

The output must be sorted in increasing order of `x`.

If no such pairs exist, return an empty list.

---

## Key Observation

If a pair `(x, y)` satisfies:

```text
x + y = n
```

then once we choose `x`, the value of `y` is forced:

```text
y = n - x
```

So the task becomes:

- iterate possible `x`
- compute `y = n - x`
- check whether both are prime
- keep only pairs with `x <= y`

The real challenge is efficient primality testing.

Since:

```text
n <= 10^6
```

the Sieve of Eratosthenes is especially effective here.

---

# Approach 1 — Brute Force with Trial Division

## Intuition

The most direct solution is:

1. try every `x` from `2` to `n`
2. let `y = n - x`
3. if `x <= y` and both are prime, add the pair

This is simple, but repeated primality checks can be expensive.

---

## Algorithm

For each `x` from `2` to `n / 2`:

- set `y = n - x`
- if `x` and `y` are prime, add `[x, y]`

We only go up to `n / 2` because:

- pairs must satisfy `x <= y`
- once `x > y`, we would just repeat symmetric cases

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> findPrimePairs(int n) {
        List<List<Integer>> ans = new ArrayList<>();

        for (int x = 2; x <= n / 2; x++) {
            int y = n - x;
            if (isPrime(x) && isPrime(y)) {
                ans.add(Arrays.asList(x, y));
            }
        }

        return ans;
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

Let `n` be the input.

### Time Complexity

We test about `n / 2` values of `x`.

Each primality check takes:

```text
O(sqrt(n))
```

So overall:

```text
O(n * sqrt(n))
```

This is too slow compared with what we can do using a sieve.

### Space Complexity

```text
O(1)
```

excluding the output list.

---

# Approach 2 — Sieve of Eratosthenes + Linear Scan

## Intuition

Instead of checking primality repeatedly, precompute all primes up to `n`.

Then for each `x`, prime checking becomes:

```text
isPrime[x]
```

in constant time.

This is the standard and best solution for this problem.

---

## Sieve Idea

The Sieve of Eratosthenes marks all primes up to `n` efficiently.

Steps:

1. assume all numbers are prime
2. starting from `2`, mark all multiples of each prime as composite
3. remaining unmarked numbers are prime

After this preprocessing, we can test primality in `O(1)` time.

---

## Algorithm

1. Build a boolean sieve `isPrime[0..n]`
2. Iterate `x` from `2` to `n / 2`
3. Let `y = n - x`
4. If both `isPrime[x]` and `isPrime[y]` are true, add `[x, y]`
5. Return the list

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> findPrimePairs(int n) {
        boolean[] isPrime = sieve(n);
        List<List<Integer>> ans = new ArrayList<>();

        for (int x = 2; x <= n / 2; x++) {
            int y = n - x;
            if (isPrime[x] && isPrime[y]) {
                ans.add(Arrays.asList(x, y));
            }
        }

        return ans;
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

Building the sieve:

```text
O(n log log n)
```

Scanning possible `x` values:

```text
O(n)
```

Overall:

```text
O(n log log n)
```

### Space Complexity

The sieve array uses:

```text
O(n)
```

---

# Approach 3 — Sieve + Prime List Iteration

## Intuition

After generating the sieve, we can also extract all primes into a list.

Then instead of scanning every `x`, we scan only prime `x` values.

For each prime `x`:

- compute `y = n - x`
- if `x <= y` and `y` is also prime, add the pair

This can be slightly cleaner conceptually because only prime candidates are tried.

---

## Algorithm

1. Build sieve up to `n`
2. Collect all primes into a list
3. For each prime `x` in the list:
   - if `x > n / 2`, stop
   - let `y = n - x`
   - if `isPrime[y]`, add `[x, y]`

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> findPrimePairs(int n) {
        boolean[] isPrime = sieve(n);
        List<Integer> primes = new ArrayList<>();

        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }

        List<List<Integer>> ans = new ArrayList<>();

        for (int x : primes) {
            if (x > n / 2) break;
            int y = n - x;
            if (isPrime[y]) {
                ans.add(Arrays.asList(x, y));
            }
        }

        return ans;
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

- sieve: `O(n log log n)`
- collecting primes: `O(n)`
- scanning prime list: at most `O(n)`

Overall:

```text
O(n log log n)
```

### Space Complexity

- sieve array: `O(n)`
- prime list: `O(number of primes)` which is also at most `O(n)`

So total:

```text
O(n)
```

---

# Approach 4 — Two-Pointer Style on Prime List

## Intuition

Once we have all primes in sorted order, we can also think of this as a two-sum style problem.

However, because the target is fixed and we only care about pairs `[x, n-x]`, the direct scan from Approach 2 is already simpler.

Still, it is useful to see the alternative perspective.

We maintain:

- one pointer on smaller primes
- look up whether the complement is prime

This is not really better here, but it shows the connection to pair-sum problems.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> findPrimePairs(int n) {
        boolean[] isPrime = sieve(n);
        List<Integer> primes = new ArrayList<>();

        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) primes.add(i);
        }

        List<List<Integer>> ans = new ArrayList<>();

        for (int i = 0; i < primes.size(); i++) {
            int x = primes.get(i);
            int y = n - x;

            if (x > y) break;
            if (y >= 2 && isPrime[y]) {
                ans.add(Arrays.asList(x, y));
            }
        }

        return ans;
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

Same as the sieve-based methods:

```text
Time:  O(n log log n)
Space: O(n)
```

---

# Correctness Reasoning

## Claim 1

A pair `[x, y]` is valid if and only if:

- `x + y = n`
- `x` is prime
- `y` is prime
- `x <= y`

This is exactly the problem definition.

---

## Claim 2

Scanning `x` only from `2` to `n / 2` is sufficient.

### Proof

If `x + y = n` and `x <= y`, then:

```text
x <= n / 2
```

So every valid pair must have its first component in that range.

Conversely, for every `x` in that range, `y = n - x` automatically satisfies `x <= y`.

Thus scanning only up to `n / 2` finds every valid pair exactly once.

Proved.

---

## Claim 3

The output is sorted by increasing `x`.

### Proof

We iterate `x` in increasing order.

Whenever a valid pair is found, we append `[x, y]`.

Therefore pairs are added in increasing order of `x`.

Proved.

---

# Worked Example

## Example 1

```text
n = 10
```

Scan `x = 2` to `5`:

- `x = 2`, `y = 8` → `8` not prime
- `x = 3`, `y = 7` → both prime → add `[3,7]`
- `x = 4`, `y = 6` → not prime
- `x = 5`, `y = 5` → both prime → add `[5,5]`

Answer:

```text
[[3,7],[5,5]]
```

---

## Example 2

```text
n = 2
```

No valid `x >= 2` with `x <= n / 2`.

So answer is:

```text
[]
```

---

# Edge Cases

## 1. Very small `n`

If `n < 4`, no prime pair exists.

Example:

```text
n = 1, 2, 3
```

Result is always empty.

---

## 2. Even vs odd `n`

- If `n` is odd, then one of the primes must be `2`, because odd = even + odd
- If `n` is even, both primes may be odd, or both may be `2` only when `n = 4`

This does not change the algorithm, but it is a useful sanity check.

---

## 3. Pair with equal primes

Example:

```text
n = 10
```

`[5,5]` is valid because:

- `5 + 5 = 10`
- `5` is prime
- `x <= y` still holds

---

# Comparison of Approaches

## Approach 1 — Trial division

Pros:

- simplest conceptually
- easy to write first

Cons:

- slower than needed

---

## Approach 2 — Sieve + scan

Pros:

- best balance of speed and simplicity
- standard optimal solution
- easy to explain

Cons:

- uses `O(n)` space

This is the recommended approach.

---

## Approach 3 — Sieve + prime list

Pros:

- tries only prime candidates
- slightly more number-theoretic feel

Cons:

- a little more bookkeeping

---

## Approach 4 — Prime-list pair view

Pros:

- connects with pair-sum thinking

Cons:

- not simpler than direct scan

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public List<List<Integer>> findPrimePairs(int n) {
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

        List<List<Integer>> ans = new ArrayList<>();

        for (int x = 2; x <= n / 2; x++) {
            int y = n - x;
            if (isPrime[x] && isPrime[y]) {
                ans.add(Arrays.asList(x, y));
            }
        }

        return ans;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n * sqrt(n))
Space: O(1)   (excluding output)
```

## Approach 2

```text
Time:  O(n log log n)
Space: O(n)
```

## Approach 3

```text
Time:  O(n log log n)
Space: O(n)
```

## Approach 4

```text
Time:  O(n log log n)
Space: O(n)
```

---

# Final Takeaway

The problem reduces to a simple pattern:

- choose `x`
- set `y = n - x`
- check whether both are prime

Because `n` can be as large as `10^6`, the right tool is the **Sieve of Eratosthenes**.

So the cleanest solution is:

1. sieve primes up to `n`
2. scan `x` from `2` to `n / 2`
3. add `[x, n-x]` whenever both are prime
