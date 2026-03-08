# Prime Numbers: Unique Factorization and Infinitude (Euclid’s Theorem)

This note explains two foundational ideas from elementary number theory:

1. **Unique Factorization** (Fundamental Theorem of Arithmetic)
2. **Infinitude of Primes** (Euclid’s proof)

It also clarifies **Euclid’s Lemma**, and the role of **Euclid numbers**.

---

## 1) Prime Factorization (What it means)

A **prime factorization** of an integer `n > 1` is writing it as a product of primes.

Example:

- `50 = 2 × 5 × 5 = 2 × 5²`

Here:

- `2` and `5` are **prime factors**
- `5` appears twice, so we use exponent notation `5²`.

This is not just a convenient notation: it reflects a deeper structural fact about integers.

---

## 2) Fundamental Theorem of Arithmetic (FTA)

### Statement

Every integer `n > 1` can be written as a product of primes, and this factorization is **unique** up to reordering.

“Unique up to reordering” means:

If:

- `n = p₁ p₂ … p_k`
- `n = q₁ q₂ … q_m`

where all `p_i` and `q_j` are primes, then:

- `k = m`
- after reordering, `p_i = q_i` for all i
  (i.e., the **same primes appear the same number of times**)

### Why it matters

This theorem is why primes are called the “building blocks” of the integers: every number is made from primes in exactly one way.

---

## 3) Euclid’s Lemma (Key tool for uniqueness)

### Statement (Euclid’s Lemma)

If `p` is prime and:

- `p | (a b)`

then:

- `p | a` **or** `p | b` (or both)

In words:

> If a prime divides a product, it must divide at least one of the factors.

### Why this is special to primes

The lemma is **not true** for arbitrary composite numbers.

Example:

- `6 | (2 × 3)` is true (6 divides 6)
- but `6` does **not** divide `2` and does **not** divide `3`

So this “divide-the-product implies divide-a-factor” property is characteristic of primes.

### Converse (also true)

If a number `p > 1` satisfies:

> whenever `p | (ab)` then `p | a` or `p | b`

then `p` must be prime.

So Euclid’s Lemma is effectively an alternate definition of primality.

---

## 4) How Euclid’s Lemma implies uniqueness (FTA uniqueness idea)

Suppose an integer `n` has two prime factorizations:

- `n = p₁ p₂ … p_k`
- `n = q₁ q₂ … q_m`

Take `p₁`. Since `p₁ | n`, and `n = q₁ q₂ … q_m`, we have:

- `p₁ | (q₁ q₂ … q_m)`

Apply Euclid’s Lemma repeatedly to the product:
`p₁` must divide some `q_j`. But `q_j` is prime, so the only way `p₁ | q_j` is:

- `p₁ = q_j`

Now cancel `p₁` from both sides and repeat the argument for the remaining primes.
This forces both factorizations to contain exactly the same primes with the same multiplicities.

That is the backbone of uniqueness.

---

## 5) Infinitude of Primes (Euclid’s Theorem)

### Statement

There are **infinitely many primes**.

Equivalently:

> No finite list contains all primes.

### Euclid’s proof (classic contradiction)

Assume for contradiction that there are only finitely many primes:

`p₁, p₂, …, p_n`

Now form the number:

`N = 1 + p₁ p₂ … p_n`

#### Step A: N is not divisible by any listed prime

For any `p_i`, the product `p₁ p₂ … p_n` is divisible by `p_i`.

So when dividing `N` by `p_i`:

- `N = (p₁ p₂ … p_n) + 1`
- remainder is `1`

Therefore:

- `p_i ∤ N` for every i

So `N` is not divisible by any prime in the list.

#### Step B: But N must have a prime divisor

By the Fundamental Theorem of Arithmetic, any integer `N > 1` has a prime factorization.
So `N` must have at least one prime divisor, call it `p`.

But we just proved **none** of `p₁..p_n` divides `N`.

So `p` is a prime **not in the original list**, contradicting the assumption that the list contained all primes.

Therefore, there must be infinitely many primes.

---

## 6) Euclid Numbers (and an important warning)

Numbers of the form:

`E_n = 1 + (p₁ p₂ … p_n)`

are sometimes called **Euclid numbers** (built from the first n primes).

Euclid’s argument shows:

- each `E_n` has a prime factor not among `p₁..p_n`

But it does **NOT** mean `E_n` is always prime.

Example:

`E_6 = 1 + (2·3·5·7·11·13)`
`= 1 + 30030`
`= 30031`
`= 59 × 509`

So `30031` is composite.

**Key takeaway:**

- Euclid numbers are not always prime,
- but they always introduce _new prime factors_ not in the original list.

---

## 7) Summary

### Unique factorization (FTA)

- Every `n > 1` can be written as product of primes
- That factorization is unique up to ordering

### Euclid’s Lemma

- Prime dividing a product must divide one factor
- This property characterizes primes
- It is central to proving uniqueness

### Infinitude of primes (Euclid)

- Given any finite list of primes, build `N = 1 + product`
- None of the listed primes divides N
- N must have a prime factor not in the list
- Therefore primes are infinite

### Euclid numbers

- `1 + product of first n primes`
- Not always prime (e.g., 30031 = 59·509)
- Still guarantee existence of new prime factors

---

If you want, we can extend this to:

- Euler’s analytic proof (via divergence of sum 1/p),
- why `log* n` arises in Union-Find proofs (conceptually similar “tower growth”),
- or the relationship between prime factorization and gcd/lcm in algorithms.
