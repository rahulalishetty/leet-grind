# 2338. Count the Number of Ideal Arrays — Combinatorial Mathematics Approach

## Intuition

We are given:

- `n` → the length of the array
- `maxValue` → the maximum allowed value in the array

We must count arrays `arr` such that:

```
1 ≤ arr[i] ≤ maxValue
arr[i-1] divides arr[i] for all i
```

This means every array forms a **divisibility chain**.

---

# Key Idea

Instead of constructing arrays directly, we fix the **last element** of the array.

Let:

```
arr[n−1] = x
```

where:

```
1 ≤ x ≤ maxValue
```

If the array satisfies the divisibility rule:

```
arr[0] | arr[1] | arr[2] | ... | arr[n−1]
```

then the entire array forms a **multiplicative chain ending at x**.

We can rewrite the sequence as:

```
arr[0] = k0
arr[1] = k0 * k1
arr[2] = k0 * k1 * k2
...
arr[n−1] = k0 * k1 * ... * k(n−1) = x
```

So we must count:

```
k0 * k1 * ... * k(n−1) = x
```

where all `k` values are positive integers.

---

# Prime Factorization

Let the prime factorization of `x` be:

```
x = p1^a1 * p2^a2 * ... * pm^am
```

Each exponent `aj` must be distributed across the `n` multiplicative slots.

Example:

```
p^a distributed into n positions
```

This is a **stars and bars** combinatorics problem.

Number of ways:

```
C(a + n - 1, a)
```

---

# Independent Prime Contributions

Since prime factors are independent, we multiply contributions.

For:

```
x = p1^a1 * p2^a2 * ... * pm^am
```

Total sequences ending in `x`:

```
∏ C(aj + n - 1, aj)
```

---

# Final Answer

We sum over all possible last values:

```
x from 1 → maxValue
```

```
answer = Σ sequences ending in x
```

All operations are performed **modulo 10^9 + 7**.

---

# Java Implementation

```java
class Solution {

    static int MOD = 1000000007;
    static int MAX_N = 10010;
    static int MAX_P = 15;
    static int[][] c = new int[MAX_N + MAX_P][MAX_P + 1];
    static int[] sieve = new int[MAX_N];
    static List<Integer>[] ps = new List[MAX_N];

    public Solution() {
        if (c[0][0] == 1) {
            return;
        }

        for (int i = 0; i < MAX_N; i++) {
            ps[i] = new ArrayList<>();
        }

        for (int i = 2; i < MAX_N; i++) {
            if (sieve[i] == 0) {
                for (int j = i; j < MAX_N; j += i) {
                    if (sieve[j] == 0) {
                        sieve[j] = i;
                    }
                }
            }
        }

        for (int i = 2; i < MAX_N; i++) {
            int x = i;
            while (x > 1) {
                int p = sieve[x], cnt = 0;
                while (x % p == 0) {
                    x /= p;
                    cnt++;
                }
                ps[i].add(cnt);
            }
        }

        c[0][0] = 1;

        for (int i = 1; i < MAX_N + MAX_P; i++) {
            c[i][0] = 1;

            for (int j = 1; j <= Math.min(i, MAX_P); j++) {
                c[i][j] = (c[i - 1][j] + c[i - 1][j - 1]) % MOD;
            }
        }
    }

    public int idealArrays(int n, int maxValue) {

        long ans = 0;

        for (int x = 1; x <= maxValue; x++) {

            long mul = 1;

            for (int p : ps[x]) {
                mul = (mul * c[n + p - 1][p]) % MOD;
            }

            ans = (ans + mul) % MOD;
        }

        return (int) ans;
    }
}
```

---

# Complexity Analysis

Let:

```
m = maxValue
n = array length
ω(m) = number of distinct prime factors
```

Average:

```
ω(m) ≈ log log m
```

---

## Time Complexity

```
O((n + ω(m)) * ω(m) + m * ω(m))
```

Breakdown:

- Sieve preprocessing → `O(n log log n)`
- Prime factorization → `O(n log n)`
- Combination computation → `O((n + ω(m)) * ω(m))`
- Final summation → `O(m ω(m))`

Which simplifies to approximately:

```
O(m log log m)
```

---

## Space Complexity

```
O((n + log m) * log m)
```

This comes from storing:

- binomial coefficients
- prime factor counts
- sieve arrays

---

# Key Insight

The difficult part of the problem becomes easy once we observe:

```
arr[i-1] | arr[i]
```

means the array corresponds to distributing **prime exponents** across positions.

This converts the problem into a **pure combinatorics problem** using:

```
Stars and Bars + Prime Factorization
```
