# 3463. Check If Digits Are Equal in String After Operations II — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public boolean hasSameDigits(String s) {

    }
}
```

---

# Problem Restatement

We are given a digit string `s`.

We repeatedly replace it by the sequence of adjacent sums modulo `10`:

```text
new[i] = (s[i] + s[i+1]) % 10
```

until only two digits remain.

We must return:

- `true` if the final two digits are equal
- `false` otherwise

---

# Core Observation

A direct simulation shrinks the string by 1 each round.

If the original length is `n`, then naive simulation takes:

```text
(n - 1) + (n - 2) + ... + 2 = O(n^2)
```

That is too slow for:

```text
n <= 10^5
```

So we need to understand the final two digits mathematically.

---

# Pascal Triangle Structure

This operation is exactly the same pattern that generates binomial coefficients.

If the original digits are:

```text
a0, a1, a2, ..., a(n-1)
```

then after repeatedly applying adjacent sums, the first of the final two digits becomes:

```text
Σ C(n-2, i) * a[i] mod 10
```

for:

```text
i = 0 .. n-2
```

and the second final digit becomes:

```text
Σ C(n-2, i) * a[i+1] mod 10
```

for:

```text
i = 0 .. n-2
```

So the two final digits are equal iff:

```text
Σ C(n-2, i) * (a[i] - a[i+1]) ≡ 0 (mod 10)
```

This is the key formula.

---

# Why This Helps

Now the problem becomes:

- compute the binomial coefficients `C(n-2, i)` modulo `10`
- multiply them by digit differences
- check whether the total is `0 mod 10`

The challenge is that modulo `10` is not prime, so the usual factorial/inverse formula does not directly work.

But since:

```text
10 = 2 * 5
```

we can compute the coefficients modulo `2` and modulo `5`, then combine them with the Chinese Remainder Theorem.

That leads to an `O(n)` solution.

---

# Approach 1 — Binomial Coefficients Mod 10 via Lucas + CRT (Recommended)

## Idea

We need:

```text
C(n-2, i) mod 10
```

for all `i`.

Since `10 = 2 * 5`, compute:

- `C(n-2, i) mod 2`
- `C(n-2, i) mod 5`

Then combine them into mod 10.

### Mod 2

Lucas theorem makes this very simple:

```text
C(N, K) mod 2 = 1 iff every binary bit of K is contained in N
```

equivalently:

```text
(K & ~N) == 0
```

### Mod 5

Use Lucas theorem in base 5:

```text
C(N, K) mod 5 = Π C(N_j, K_j) mod 5
```

where `N_j`, `K_j` are digits in base 5.

Since digits are only `0..4`, small combinations are easy to precompute.

### CRT

Given:

- `x mod 2`
- `x mod 5`

the unique value modulo 10 can be reconstructed by trying `0..9`, or with a tiny formula.

Then we evaluate the weighted difference sum modulo 10.

---

## Java Code

```java
class Solution {
    private static final int[][] C5 = {
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {1, 2, 1, 0, 0},
        {1, 3, 3, 1, 0},
        {1, 4, 1, 4, 1}
    };

    public boolean hasSameDigits(String s) {
        int n = s.length();
        int N = n - 2;

        int total = 0;

        for (int i = 0; i <= N; i++) {
            int coeffMod2 = binomMod2(N, i);
            int coeffMod5 = binomMod5Lucas(N, i);
            int coeffMod10 = crtMod10(coeffMod2, coeffMod5);

            int left = s.charAt(i) - '0';
            int right = s.charAt(i + 1) - '0';
            int diff = left - right;

            total = (total + coeffMod10 * diff) % 10;
        }

        if (total < 0) total += 10;
        return total == 0;
    }

    private int binomMod2(int n, int k) {
        return ((k & ~n) == 0) ? 1 : 0;
    }

    private int binomMod5Lucas(int n, int k) {
        int res = 1;
        while (n > 0 || k > 0) {
            int nd = n % 5;
            int kd = k % 5;
            if (kd > nd) return 0;
            res = (res * C5[nd][kd]) % 5;
            n /= 5;
            k /= 5;
        }
        return res;
    }

    private int crtMod10(int mod2, int mod5) {
        for (int x = 0; x < 10; x++) {
            if (x % 2 == mod2 && x % 5 == mod5) {
                return x;
            }
        }
        return 0;
    }
}
```

---

## Complexity

For each `i`, Lucas in base 5 takes `O(log_5 n)` steps, which is tiny.

So overall:

```text
Time:  O(n log n)
Space: O(1)
```

In practice this is very fast.

---

# Approach 2 — Streaming Binomial Coefficients Mod 10 With Prime-Exponent Tracking

## Idea

Another way is to generate the row:

```text
C(N,0), C(N,1), ..., C(N,N)
```

incrementally using:

```text
C(N, i+1) = C(N, i) * (N-i) / (i+1)
```

But because we work modulo 10, division is tricky.

To fix that, we track powers of 2 and 5 separately and also track the coprime remainder.

That allows us to maintain each coefficient modulo 10 safely.

This is more technical than Approach 1, but it is a valid alternative.

---

## High-Level Sketch

For each transition:

```text
C(N, i+1) = C(N, i) * (N-i) / (i+1)
```

- factor out all 2s and 5s from numerator and denominator
- maintain counts of exponent of 2 and exponent of 5
- maintain the rest modulo 10 using modular multiplication
- reconstruct the coefficient modulo 10 from:
  - coprime part
  - remaining power of 2
  - remaining power of 5

This avoids Lucas but is trickier to implement correctly.

---

## Java Code

```java
class Solution {
    public boolean hasSameDigits(String s) {
        int n = s.length();
        int N = n - 2;

        int coeff = 1; // C(N,0)
        int twos = 0, fives = 0;
        int core = 1;

        int total = applyTerm(0, coeff, s, 0);

        for (int i = 0; i < N; i++) {
            int num = N - i;
            int den = i + 1;

            while (num % 2 == 0) {
                num /= 2;
                twos++;
            }
            while (num % 5 == 0) {
                num /= 5;
                fives++;
            }

            while (den % 2 == 0) {
                den /= 2;
                twos--;
            }
            while (den % 5 == 0) {
                den /= 5;
                fives--;
            }

            core = (core * (num % 10)) % 10;
            core = (core * invMod10Coprime(den % 10)) % 10;

            coeff = rebuild(core, twos, fives);
            total = (total + applyTerm(i + 1, coeff, s, total)) % 10;
        }

        if (total < 0) total += 10;
        return total == 0;
    }

    private int applyTerm(int i, int coeff, String s, int total) {
        int diff = (s.charAt(i) - '0') - (s.charAt(i + 1) - '0');
        return coeff * diff;
    }

    private int invMod10Coprime(int x) {
        x %= 10;
        for (int y = 1; y < 10; y++) {
            if ((x * y) % 10 == 1) return y;
        }
        return 1;
    }

    private int rebuild(int core, int twos, int fives) {
        int res = core % 10;

        while (twos-- > 0) res = (res * 2) % 10;
        while (fives-- > 0) res = (res * 5) % 10;

        return res;
    }
}
```

---

## Caution

This approach is more delicate. It is harder to reason about than Lucas + CRT, and the Lucas approach is usually preferable.

---

# Approach 3 — Straightforward Simulation (Too Slow)

## Idea

Repeatedly build the next string/array:

```text
next[i] = (cur[i] + cur[i+1]) % 10
```

until only two digits remain.

This directly matches the problem statement.

---

## Java Code

```java
class Solution {
    public boolean hasSameDigits(String s) {
        int[] arr = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            arr[i] = s.charAt(i) - '0';
        }

        int len = arr.length;
        while (len > 2) {
            for (int i = 0; i < len - 1; i++) {
                arr[i] = (arr[i] + arr[i + 1]) % 10;
            }
            len--;
        }

        return arr[0] == arr[1];
    }
}
```

---

## Why it fails

This takes:

```text
O(n^2)
```

which is too slow for:

```text
n <= 10^5
```

So it is only useful for intuition.

---

# Detailed Derivation of the Binomial Formula

Suppose the original row is:

```text
a0, a1, a2, a3, ...
```

After one round:

```text
a0+a1, a1+a2, a2+a3, ...
```

After the next round, coefficients accumulate just like Pascal’s triangle.

For example, with 4 digits:

```text
a0 a1 a2 a3
```

after one step:

```text
a0+a1, a1+a2, a2+a3
```

after two steps:

```text
a0 + 2a1 + a2,
a1 + 2a2 + a3
```

So the coefficients are exactly binomial coefficients.

In general, after `n-2` rounds, the final two digits are:

```text
Σ C(n-2, i) a[i]
Σ C(n-2, i) a[i+1]
```

Thus equality reduces to one weighted difference sum.

---

# Example Walkthrough

## Example 1

```text
s = "3902"
```

Digits:

```text
3, 9, 0, 2
```

Here:

```text
n = 4
N = n - 2 = 2
```

Binomial coefficients:

```text
C(2,0)=1, C(2,1)=2, C(2,2)=1
```

Check:

```text
1*(3-9) + 2*(9-0) + 1*(0-2)
= -6 + 18 - 2
= 10
≡ 0 (mod 10)
```

So the final two digits are equal, and answer is:

```text
true
```

---

## Example 2

```text
s = "34789"
```

Then:

```text
n = 5
N = 3
```

Coefficients:

```text
1, 3, 3, 1
```

Weighted difference:

```text
1*(3-4) + 3*(4-7) + 3*(7-8) + 1*(8-9)
= -1 - 9 - 3 - 1
= -14
≡ 6 (mod 10)
```

Not zero, so final two digits are not equal.

Answer:

```text
false
```

---

# Important Correctness Argument

The repeated adjacent-sum process is linear modulo 10.

Each final digit is a linear combination of the original digits.

The coefficients follow Pascal’s triangle, hence they are binomial coefficients.

So computing the weighted difference with these coefficients is mathematically identical to simulating all operations, just much faster.

Thus the algorithm is correct.

---

# Common Pitfalls

## 1. Simulating the process directly

That is quadratic and too slow.

---

## 2. Using factorial/inverse binomial formulas modulo 10

Modulo 10 is not prime, so modular inverses do not always exist.

---

## 3. Forgetting the second final digit is shifted by one index

The two final digits are not built from exactly the same original positions.

---

## 4. Mishandling negative modulo

When accumulating differences, normalize back into `0..9`.

---

# Best Approach

## Recommended: Binomial coefficients mod 10 using Lucas theorem + CRT

This is the cleanest robust approach because:

- it uses the exact mathematical structure of the process
- it avoids quadratic simulation
- it handles modulo 10 correctly despite non-primality

---

# Final Recommended Java Solution

```java
class Solution {
    private static final int[][] C5 = {
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {1, 2, 1, 0, 0},
        {1, 3, 3, 1, 0},
        {1, 4, 1, 4, 1}
    };

    public boolean hasSameDigits(String s) {
        int n = s.length();
        int N = n - 2;

        int total = 0;

        for (int i = 0; i <= N; i++) {
            int coeff2 = binomMod2(N, i);
            int coeff5 = binomMod5Lucas(N, i);
            int coeff10 = crtMod10(coeff2, coeff5);

            int diff = (s.charAt(i) - '0') - (s.charAt(i + 1) - '0');
            total = (total + coeff10 * diff) % 10;
        }

        if (total < 0) total += 10;
        return total == 0;
    }

    private int binomMod2(int n, int k) {
        return ((k & ~n) == 0) ? 1 : 0;
    }

    private int binomMod5Lucas(int n, int k) {
        int res = 1;
        while (n > 0 || k > 0) {
            int nd = n % 5;
            int kd = k % 5;
            if (kd > nd) return 0;
            res = (res * C5[nd][kd]) % 5;
            n /= 5;
            k /= 5;
        }
        return res;
    }

    private int crtMod10(int mod2, int mod5) {
        for (int x = 0; x < 10; x++) {
            if (x % 2 == mod2 && x % 5 == mod5) {
                return x;
            }
        }
        return 0;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n log n)
Space: O(1)
```

This is efficient for:

```text
n <= 10^5
```

---

# Final Takeaway

The repeated adjacent-sum process hides a Pascal triangle structure.

Once you recognize that, the problem becomes:

- compute a weighted sum of digit differences
- where weights are binomial coefficients modulo 10

That replaces an `O(n^2)` simulation with a fast number-theoretic solution.
