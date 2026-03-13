# 1735. Count Ways to Make Array With Product — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int[] waysToFillArray(int[][] queries) {

    }
}
```

---

# Problem Restatement

For each query `[n, k]`, count the number of arrays of length `n` consisting of **positive integers** such that:

```text
a1 * a2 * ... * an = k
```

Return the answer modulo:

```text
10^9 + 7
```

---

# Core Mathematical Insight

The key to this problem is **prime factorization**.

Suppose:

```text
k = p1^e1 * p2^e2 * ... * pm^em
```

To make an array whose product is `k`, we must distribute the exponent of each prime across the `n` positions.

For one prime `p^e`, this becomes:

> In how many ways can we distribute `e` identical items into `n` boxes?

That is a standard **stars and bars** problem:

```text
C(n + e - 1, e)
```

Since different prime factors are independent, the total number of arrays is:

```text
Π C(n + ei - 1, ei)
```

over all prime exponents `ei` in the factorization of `k`.

So the whole problem reduces to:

1. Prime factorize `k`
2. For each exponent `e`, multiply by `C(n + e - 1, e)`

---

# Approach 1 — Prime Factorization + Combinations (Recommended)

## Idea

For each query:

1. Factorize `k`
2. For each prime exponent `e`
   - multiply result by `C(n + e - 1, e)`
3. Return modulo `1_000_000_007`

Since `k <= 10^4`, the maximum exponent is small, so this is efficient.

---

## Why this works

If:

```text
k = 2^3 * 3^2
```

then for the factor `2^3`, we distribute exponent `3` across `n` slots:

```text
x1 + x2 + ... + xn = 3
```

Number of solutions:

```text
C(n + 3 - 1, 3)
```

Similarly for `3^2`:

```text
C(n + 2 - 1, 2)
```

Multiply the two counts because the prime distributions are independent.

---

## Java Code

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;
    static final int MAX_K = 10000;
    static final int MAX_E = 14; // enough since 2^13 = 8192 and 2^14 > 10000
    static final int MAX_N = 10000 + MAX_E + 5;

    long[] fact = new long[MAX_N];
    long[] invFact = new long[MAX_N];

    public int[] waysToFillArray(int[][] queries) {
        buildFactorials();

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int n = queries[i][0];
            int k = queries[i][1];

            long ways = 1L;
            Map<Integer, Integer> factors = factorize(k);

            for (int exp : factors.values()) {
                ways = (ways * comb(n + exp - 1, exp)) % MOD;
            }

            ans[i] = (int) ways;
        }

        return ans;
    }

    private void buildFactorials() {
        fact[0] = 1;
        for (int i = 1; i < MAX_N; i++) {
            fact[i] = (fact[i - 1] * i) % MOD;
        }

        invFact[MAX_N - 1] = modPow(fact[MAX_N - 1], MOD - 2);
        for (int i = MAX_N - 2; i >= 0; i--) {
            invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
        }
    }

    private long comb(int n, int r) {
        if (r < 0 || r > n) return 0;
        return (((fact[n] * invFact[r]) % MOD) * invFact[n - r]) % MOD;
    }

    private Map<Integer, Integer> factorize(int x) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int p = 2; p * p <= x; p++) {
            while (x % p == 0) {
                map.put(p, map.getOrDefault(p, 0) + 1);
                x /= p;
            }
        }
        if (x > 1) {
            map.put(x, map.getOrDefault(x, 0) + 1);
        }
        return map;
    }

    private long modPow(long a, long e) {
        long res = 1L;
        while (e > 0) {
            if ((e & 1) == 1) res = (res * a) % MOD;
            a = (a * a) % MOD;
            e >>= 1;
        }
        return res;
    }
}
```

---

## Complexity

Let `q = queries.length`.

For each query:

- factorization of `k`: about `O(sqrt(k))`
- number of prime exponents is very small
- each combination is `O(1)` after preprocessing

Overall:

```text
Preprocessing: O(MAX_N)
Per query: O(sqrt(k))
Total: O(MAX_N + q * sqrt(MAX_K))
```

This is efficient enough.

---

# Approach 2 — Precompute Small Pascal Triangle for Exponents

## Idea

Since the maximum exponent of any prime factor in `k <= 10^4` is small, we can precompute:

```text
C(n + e - 1, e)
```

using a Pascal-style table or repeated combination computation.

This avoids factorial inverses if you prefer a more direct combinatorics approach.

The logic is still the same:

- factorize `k`
- multiply stars-and-bars counts

---

## Java Code

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;
    static final int MAX_N = 10000;
    static final int MAX_E = 14;

    long[][] comb = new long[MAX_N + MAX_E + 1][MAX_E + 1];

    public int[] waysToFillArray(int[][] queries) {
        buildComb();

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int n = queries[i][0];
            int k = queries[i][1];

            Map<Integer, Integer> factors = factorize(k);
            long ways = 1L;

            for (int exp : factors.values()) {
                ways = (ways * comb[n + exp - 1][exp]) % MOD;
            }

            ans[i] = (int) ways;
        }

        return ans;
    }

    private void buildComb() {
        for (int i = 0; i < comb.length; i++) {
            comb[i][0] = 1;
            for (int j = 1; j <= Math.min(i, MAX_E); j++) {
                if (j == i) comb[i][j] = 1;
                else comb[i][j] = (comb[i - 1][j - 1] + comb[i - 1][j]) % MOD;
            }
        }
    }

    private Map<Integer, Integer> factorize(int x) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int p = 2; p * p <= x; p++) {
            while (x % p == 0) {
                map.put(p, map.getOrDefault(p, 0) + 1);
                x /= p;
            }
        }
        if (x > 1) map.put(x, map.getOrDefault(x, 0) + 1);
        return map;
    }
}
```

---

## Complexity

- Pascal precompute: about `O(MAX_N * MAX_E)`
- each query: `O(sqrt(k))`

This is also efficient, because `MAX_E` is tiny.

---

# Approach 3 — Recursive / DP Distribution Per Prime Exponent (Conceptual)

## Idea

Instead of using stars and bars directly, we could define a DP:

```text
dp[i][s] = number of ways to distribute total exponent s across first i positions
```

Transition:

```text
dp[i][s] = sum(dp[i - 1][s - take]) for all take in [0..s]
```

Then compute this per exponent and multiply.

This is mathematically valid, but much slower than combinations.

---

## Why it is inferior

For each exponent `e` and length `n`, this DP costs roughly:

```text
O(n * e^2)
```

Even though `e` is small here, this is still more cumbersome and unnecessary compared to direct combinations.

Still, it is useful conceptually because it leads naturally to stars and bars.

---

## Java Code

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    public int[] waysToFillArray(int[][] queries) {
        int[] ans = new int[queries.length];

        for (int qi = 0; qi < queries.length; qi++) {
            int n = queries[qi][0];
            int k = queries[qi][1];

            Map<Integer, Integer> factors = factorize(k);
            long ways = 1L;

            for (int exp : factors.values()) {
                ways = (ways * countWaysDP(n, exp)) % MOD;
            }

            ans[qi] = (int) ways;
        }

        return ans;
    }

    private long countWaysDP(int n, int exp) {
        long[][] dp = new long[n + 1][exp + 1];
        dp[0][0] = 1;

        for (int i = 1; i <= n; i++) {
            for (int s = 0; s <= exp; s++) {
                long val = 0;
                for (int take = 0; take <= s; take++) {
                    val = (val + dp[i - 1][s - take]) % MOD;
                }
                dp[i][s] = val;
            }
        }

        return dp[n][exp];
    }

    private Map<Integer, Integer> factorize(int x) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int p = 2; p * p <= x; p++) {
            while (x % p == 0) {
                map.put(p, map.getOrDefault(p, 0) + 1);
                x /= p;
            }
        }
        if (x > 1) map.put(x, map.getOrDefault(x, 0) + 1);
        return map;
    }
}
```

---

## Complexity

Much worse than the combinatorics solution.

This is mostly educational, not recommended.

---

# Detailed Walkthrough

## Example: `[n = 2, k = 6]`

Factorize:

```text
6 = 2^1 * 3^1
```

For exponent `1` of prime `2`, number of distributions into 2 slots:

```text
C(2 + 1 - 1, 1) = C(2,1) = 2
```

For exponent `1` of prime `3`:

```text
C(2 + 1 - 1, 1) = 2
```

Multiply:

```text
2 * 2 = 4
```

Arrays are:

```text
[1,6]
[2,3]
[3,2]
[6,1]
```

---

## Example: `[n = 5, k = 1]`

Prime factorization of `1` is empty.

So there is nothing to distribute, and the only valid array is:

```text
[1,1,1,1,1]
```

Answer:

```text
1
```

---

# Important Corner Cases

## 1. `k = 1`

Then the answer is always:

```text
1
```

because every element must be `1`.

---

## 2. Prime `k`

If `k` is prime, say:

```text
k = p^1
```

then the answer is:

```text
C(n, 1) = n
```

because the single prime factor can go into any one of the `n` positions.

---

## 3. Large `n`, small factorization

This is why combinatorics works well:
the hard part is not `n`, but the exponents in the factorization of `k`.
Those exponents are tiny because `k <= 10^4`.

---

# Why Stars and Bars Appears Here

Suppose:

```text
k = 2^4
```

and array size is `n = 3`.

We want:

```text
a1 * a2 * a3 = 2^4
```

Each array element contributes some exponent of `2`:

```text
a1 = 2^x1
a2 = 2^x2
a3 = 2^x3
```

with:

```text
x1 + x2 + x3 = 4
```

The number of nonnegative integer solutions is:

```text
C(3 + 4 - 1, 4) = C(6,4)
```

That is exactly stars and bars.

---

# Best Approach

## Recommended: Prime Factorization + Combinations

This is the standard best solution because:

- mathematically direct
- very efficient
- easy to reason about once factorization is recognized
- clean Java implementation

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;
    static final int MAX_E = 14;
    static final int MAX_N = 10000 + MAX_E + 5;

    long[] fact = new long[MAX_N];
    long[] invFact = new long[MAX_N];

    public int[] waysToFillArray(int[][] queries) {
        buildFactorials();

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int n = queries[i][0];
            int k = queries[i][1];

            long ways = 1L;
            Map<Integer, Integer> factors = factorize(k);

            for (int exp : factors.values()) {
                ways = (ways * comb(n + exp - 1, exp)) % MOD;
            }

            ans[i] = (int) ways;
        }

        return ans;
    }

    private void buildFactorials() {
        fact[0] = 1;
        for (int i = 1; i < MAX_N; i++) {
            fact[i] = (fact[i - 1] * i) % MOD;
        }

        invFact[MAX_N - 1] = modPow(fact[MAX_N - 1], MOD - 2);
        for (int i = MAX_N - 2; i >= 0; i--) {
            invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
        }
    }

    private long comb(int n, int r) {
        if (r < 0 || r > n) return 0;
        return (((fact[n] * invFact[r]) % MOD) * invFact[n - r]) % MOD;
    }

    private Map<Integer, Integer> factorize(int x) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int p = 2; p * p <= x; p++) {
            while (x % p == 0) {
                map.put(p, map.getOrDefault(p, 0) + 1);
                x /= p;
            }
        }
        if (x > 1) {
            map.put(x, map.getOrDefault(x, 0) + 1);
        }
        return map;
    }

    private long modPow(long a, long e) {
        long res = 1L;
        while (e > 0) {
            if ((e & 1) == 1) {
                res = (res * a) % MOD;
            }
            a = (a * a) % MOD;
            e >>= 1;
        }
        return res;
    }
}
```

---

# Final Summary

For each query `[n, k]`:

1. Prime factorize `k`
2. For each exponent `e`, compute:

```text
C(n + e - 1, e)
```

3. Multiply all such values modulo `10^9 + 7`

This works because each prime exponent is distributed independently across the array positions.

## Final Complexity

Using the recommended approach:

- **Preprocessing:** `O(MAX_N)`
- **Per query:** `O(sqrt(k))`
- **Total:** `O(MAX_N + q * sqrt(MAX_K))`

This is the cleanest and most efficient solution.
