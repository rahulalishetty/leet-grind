# 2507. Smallest Value After Replacing With Sum of Prime Factors

You are given a positive integer **n**.

You must repeatedly replace **n** with the **sum of its prime factors** until the value no longer decreases.

If a prime factor divides **n** multiple times, it must be included in the sum the same number of times.

Return the **smallest value** that **n** reaches during this process.

---

# Example 1

## Input

```
n = 15
```

## Output

```
5
```

## Explanation

```
15 = 3 * 5
sum = 3 + 5 = 8

8 = 2 * 2 * 2
sum = 2 + 2 + 2 = 6

6 = 2 * 3
sum = 2 + 3 = 5
```

Since **5 is prime**, its prime factor sum equals itself.

Thus the smallest value reached is:

```
5
```

---

# Example 2

## Input

```
n = 3
```

## Output

```
3
```

## Explanation

```
3 is already prime
```

The sum of its prime factors is **3**, so the value does not decrease.

Therefore the smallest value is:

```
3
```

---

# Constraints

```
2 <= n <= 10^5
```

---

# Key Idea

Repeat the following steps:

1. Compute the **sum of prime factors of n**
2. If the sum is **smaller than n**, replace **n**
3. Otherwise stop

The smallest value encountered during this process is the answer.
