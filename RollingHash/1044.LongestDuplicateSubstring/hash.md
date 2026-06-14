# Hash Functions & Prime Numbers — The Mathematics Behind the Design

## 1. What Is a Hash Function, Really?

A hash function maps an arbitrarily large input (like a string of any length) to a fixed-size number within a bounded range `[0, MOD-1]`.

```
h : String → {0, 1, 2, ..., MOD-1}
```

The goals are:

- **Speed** — compute and update in O(1)
- **Uniformity** — different strings spread evenly across the range
- **Low collision rate** — two different strings rarely produce the same number

The rolling hash used here is a **polynomial hash**, sometimes called a Rabin-Karp hash, and every constant in it — the bases 31, 37 and the moduli 1,000,000,007 and 998,244,353 — is chosen for precise mathematical reasons.

---

## 2. The Polynomial Hash — Why a Polynomial?

Given a string of characters `c[0], c[1], ..., c[len-1]`, the hash is:

```
h = c[0]·BASE^(len-1) + c[1]·BASE^(len-2) + ... + c[len-1]·BASE^0   (mod MOD)
```

This is a **polynomial in BASE** evaluated at a single point, with character values as coefficients.

### Why not just sum the characters?

```
h = c[0] + c[1] + ... + c[len-1]           ← BAD
```

This is order-independent. "abc" and "bca" and "cab" all produce the same sum. A hash function that ignores order is useless for substring matching.

### Why not multiply all characters?

```
h = c[0] · c[1] · ... · c[len-1]           ← BAD
```

Multiplication is commutative too. "abc" and "cba" would collide. Also, many small integers multiply to the same product (e.g., 2·6 = 3·4).

### Why the polynomial works

Each character is weighted by a **different power of BASE**, so its position in the string affects the hash value. Swapping any two characters changes the result:

```
"ab"  → 1·BASE + 2 = BASE + 2
"ba"  → 2·BASE + 1 = 2·BASE + 1
```

These are equal only if `BASE + 2 = 2·BASE + 1`, i.e. `BASE = 1` — which is not the case for BASE = 31.

This is precisely why the polynomial structure encodes both **content** and **position**.

---

## 3. Why Modular Arithmetic?

Without a modulus, hashing a string of length 100 using BASE=31 would require computing numbers up to:

```
31^100 ≈ 10^148
```

That number has 149 digits. No CPU register can hold it. Arithmetic would require big-integer libraries and become astronomically slow.

The modulus `MOD` keeps every intermediate value within `[0, MOD-1]`, so all arithmetic fits in a 64-bit `long`.

### The modular polynomial

```
h = (c[0]·BASE^(len-1) + c[1]·BASE^(len-2) + ... + c[len-1]) mod MOD
```

Every addition and multiplication is taken mod MOD immediately. This is valid because modular arithmetic distributes over both operations:

```
(a + b) mod M  =  ((a mod M) + (b mod M)) mod M
(a · b) mod M  =  ((a mod M) · (b mod M)) mod M
```

So the final result is identical to computing the full polynomial and then taking the modulus — just without the enormous intermediate numbers.

---

## 4. Why Must the Modulus Be Prime?

This is the central mathematical question. The modulus could in principle be any large number, but **prime moduli are specifically chosen to minimise collisions**. Here is why.

### 4.1 The Collision Condition

Two different strings `A` and `B` of length `len` collide when:

```
h(A) ≡ h(B)   (mod MOD)
```

Subtracting:

```
h(A) - h(B) ≡ 0   (mod MOD)
```

Expanding the difference of the two polynomials:

```
(a[0]-b[0])·BASE^(len-1) + (a[1]-b[1])·BASE^(len-2) + ... + (a[len-1]-b[len-1]) ≡ 0   (mod MOD)
```

Let `d[i] = a[i] - b[i]`. This is a polynomial `P(BASE) ≡ 0 (mod MOD)`.

We want this to happen as rarely as possible.

### 4.2 Roots of a Polynomial mod MOD

By a classical result in algebra, **a polynomial of degree `k` over a field has at most `k` roots**.

The integers modulo a prime form a **field** (denoted Z/pZ). This means polynomial root counting works cleanly: a degree-(len-1) polynomial has at most `len-1` roots modulo a prime `MOD`.

The probability that a random BASE hits one of those roots is at most:

```
(len - 1) / MOD
```

For `len ≤ 100,000` and `MOD = 1,000,000,007`:

```
99,999 / 1,000,000,007 ≈ 10⁻⁴
```

That is already small — and we're not choosing BASE randomly, so in practice BASE = 31 is a fixed non-root by design.

### 4.3 What Goes Wrong with Composite Moduli

If `MOD` is composite — say `MOD = 2^61 - 1` isn't prime, or suppose `MOD = 10^9` — then the integers mod `MOD` are **not** a field. They are only a **ring**, and rings permit **zero divisors**: pairs `a, b` where `a·b ≡ 0 (mod MOD)` even though neither `a` nor `b` is zero.

Example: `2 · 500,000,000 ≡ 0 (mod 10^9)`. Neither factor is zero, but their product is.

Zero divisors cause the polynomial root bound to break down catastrophically. A polynomial of degree `k` can have far more than `k` roots modulo a composite number — in some cases, every value is a root. This directly means far more collisions.

**Prime moduli guarantee the field property, which guarantees the polynomial root bound, which guarantees a collision probability of at most `(len-1)/MOD`.**

### 4.4 The Two Primes Used

| Constant | Value         | Why chosen                                                                                                                                                    |
| -------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `MOD1`   | 1,000,000,007 | Largest prime below 10^9; fits comfortably in a 32-bit unsigned int; universally used in competitive programming                                              |
| `MOD2`   | 998,244,353   | Also prime; equals 119·2^23 + 1, making it an **NTT-friendly prime** (supports fast number-theoretic transforms); widely used as a second independent modulus |

Both are just below 10^9. Their product is about 10^18, which fits in a `long` (max ~9.2 × 10^18). This matters for double hashing: the combined collision probability is:

```
P(collision on both) ≈ 1/MOD1 · 1/MOD2 ≈ 10⁻⁹ · 10⁻⁹ = 10⁻¹⁸
```

To put that in perspective: if you checked 10^9 pairs of substrings per second, you'd expect to wait roughly 10^9 seconds — about 31 years — before seeing a single spurious collision.

---

## 5. Why Are the Bases 31 and 37?

### 5.1 The Base Must Be Larger Than the Alphabet

The characters `'a'` through `'z'` are mapped to `1` through `26`. The base must be **strictly greater than 26** so that no two distinct sequences of characters produce the same polynomial value by coincidence of cancellation.

If BASE = 5 and the alphabet has values 1–26, then `c[i] / BASE` can exceed 1, and the "digit" analogy breaks down — different polynomials can collapse to the same value.

BASE = 31 > 26 ✓
BASE = 37 > 26 ✓

### 5.2 Primes Are Preferred for the Base Too

The base should itself be prime, or at minimum coprime to MOD (i.e., share no common factor with MOD).

If `gcd(BASE, MOD) > 1`, then `BASE^k mod MOD` eventually cycles through fewer distinct values — the sequence of powers doesn't cover the full range `[1, MOD-1]`. This reduces the effective number of distinct hash values and increases collision probability.

Since MOD1 and MOD2 are prime and BASE ∈ {31, 37} are also prime (and much smaller than the moduli), `gcd(BASE, MOD) = 1` trivially holds.

### 5.3 Why 31 and 37 Specifically?

These are the **two smallest primes greater than 26** (the alphabet size):

```
Primes:  2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, ...
                                                 ^^   ^^
                                    first two primes above 26
```

Using the smallest valid primes minimises the magnitude of `BASE^len`, keeping intermediate values as small as possible before the modulus is applied — this slightly reduces the risk of overflow in any intermediate multiplication step.

Additionally, 31 has a historical role: the Java standard library uses BASE = 31 in `String.hashCode()` because `31 * x == (x << 5) - x`, enabling a compiler optimisation via bit shifts instead of multiplication. While that optimisation doesn't apply here (we compute mod a large prime), the choice carries over as a well-validated constant.

Using **two different bases** (31 and 37) ensures the two hash functions are truly independent. If the same base were used with two different moduli, a carefully crafted adversarial input could still collide both hashes simultaneously. Different bases eliminate that structural dependency.

---

## 6. The Overflow Problem and `+ MOD * 2`

The rolling update is:

```java
h1 = (h1 * BASE1 - outChar * pow1 % MOD1 + inChar + MOD1 * 2) % MOD1;
```

The subtraction `- outChar * pow1 % MOD1` can make the expression negative before the final `% MOD1`. In Java, `%` is the **remainder operator**, not the mathematical modulus — it preserves the sign of the dividend:

```
-3 % 7 = -3   (Java)
-3 mod 7 = 4  (mathematics)
```

To guarantee a non-negative result, we add `MOD1 * 2` before taking the modulus. Why `2`?

The worst-case negative contribution from the subtraction is at most `MOD1 - 1` (since all values are already reduced mod MOD1). Adding `MOD1` once would suffice mathematically, but in practice the multiplication `h1 * BASE1` can transiently exceed `MOD1` before reduction, so `2 * MOD1` provides a safe upper bound that keeps the entire expression positive without risking overflow of a 64-bit `long`.

Check: the maximum value before `% MOD1`:

```
h1 * BASE1     ≤ (MOD1 - 1) · BASE1  ≈ 10^9 · 31  ≈  3.1 × 10^10
- outChar*pow1 ≥ -(MOD1 - 1)         ≈ -10^9
+ inChar       ≤ 26
+ MOD1 * 2     ≤ 2 × 10^9

Total maximum ≈ 3.1×10^10 + 2×10^9 ≈ 3.3×10^10
```

`Long.MAX_VALUE ≈ 9.2 × 10^18` — well above 3.3 × 10^10. No overflow occurs.

---

## 7. Why `BASE^len` Is Precomputed

The rolling subtraction requires `BASE^len mod MOD` on every single window slide:

```
new_h = old_h · BASE - outChar · BASE^len + inChar   (mod MOD)
```

If `BASE^len` were recomputed each time, that would be O(len) work per slide, destroying the O(1) advantage.

Instead, it is computed **once** before the sliding loop using repeated multiplication (Horner-style):

```java
long pow1 = 1L;
for (int i = 0; i < len; i++) pow1 = pow1 * BASE1 % MOD1;
```

This is O(len) total — paid once — and then the same value is reused for all `n - len` slides.

An equivalent and faster alternative is **fast exponentiation** (repeated squaring), which computes `BASE^len mod MOD` in O(log len):

```
BASE^len  →  (BASE^(len/2))^2  →  recurse
```

For the typical use case here the simple loop is fine, but in systems where `len` can be very large, fast exponentiation would be preferable.

---

## 8. Collision Probability — Putting It All Together

| Scenario                                         | Probability                 |
| ------------------------------------------------ | --------------------------- |
| Single hash (one prime) collision per pair       | ≈ 1 / MOD ≈ 10⁻⁹            |
| Double hash collision per pair                   | ≈ 1 / (MOD1 · MOD2) ≈ 10⁻¹⁸ |
| Double hash collision across n=10^5 window pairs | ≈ n² / (MOD1·MOD2) ≈ 10⁻⁸   |

Even for strings of length 100,000, the probability of _any_ spurious collision in the entire algorithm is around 10⁻⁸. The additional string-equality verification step reduces the effective error rate to exactly zero — it catches and discards any false positive that the hashing missed.

---

## 9. Summary Table

| Constant         | Value            | Mathematical Reason                                           |
| ---------------- | ---------------- | ------------------------------------------------------------- |
| `BASE1`          | 31               | Smallest prime > alphabet size 26; coprime to MOD1            |
| `BASE2`          | 37               | Second smallest prime > 26; independent of BASE1              |
| `MOD1`           | 1,000,000,007    | Large prime → Z/pZ is a field → polynomial root bound holds   |
| `MOD2`           | 998,244,353      | Second large prime → independent hash dimension               |
| `+ MOD * 2`      | defensive offset | Prevents negative intermediate values under Java's signed `%` |
| `pow = BASE^len` | precomputed      | Reused O(n) times to maintain O(1) rolling update             |

The entire design is a chain of mathematical necessities: polynomial structure to encode position, modular arithmetic to fit in 64 bits, prime moduli to guarantee field properties and bounded collision probability, and two independent hash functions to square the collision resistance.
