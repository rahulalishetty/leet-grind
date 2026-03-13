# 1808. Maximize Number of Nice Divisors

You are given a positive integer `primeFactors`. You are asked to construct a positive integer `n` that satisfies the following conditions:

1. The number of prime factors of `n` (not necessarily distinct) is **at most `primeFactors`**.
2. The **number of nice divisors of `n` is maximized**.

A divisor of `n` is **nice** if it is divisible by **every prime factor of `n`**.

Return the **maximum number of nice divisors** of `n`.

Since the result may be very large, return it **modulo 10^9 + 7**.

---

## Notes

- A **prime number** is a natural number greater than 1 that is not the product of two smaller natural numbers.
- The **prime factors** of a number `n` are prime numbers whose product equals `n`.

---

## Example 1

**Input**

```
primeFactors = 5
```

**Output**

```
6
```

**Explanation**

One valid choice is:

```
n = 200
```

Prime factors:

```
[2, 2, 2, 5, 5]
```

Nice divisors:

```
[10, 20, 40, 50, 100, 200]
```

Total nice divisors = **6**.

No other number with at most **5 prime factors** produces more nice divisors.

---

## Example 2

**Input**

```
primeFactors = 8
```

**Output**

```
18
```

---

## Constraints

```
1 <= primeFactors <= 10^9
```
