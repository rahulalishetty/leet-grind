# Most Expensive Item That Cannot Be Bought

## Problem

You are given two distinct prime numbers:

- `primeOne`
- `primeTwo`

Alice has an infinite number of coins of denominations:

- `primeOne`
- `primeTwo`

For every positive integer `x`, the market has an item priced exactly `x`.

Alice wants to know the **most expensive item price that she cannot form** using any nonnegative number of those two coin types.

In other words, we want the largest positive integer that **cannot** be written as:

`primeOne * a + primeTwo * b`

where:

- `a >= 0`
- `b >= 0`
- `a` and `b` are integers

---

# 1. What the problem is really asking

This is not a market simulation problem. It is a number theory problem.

The question is:

Given two coin denominations, what is the largest amount that cannot be formed?

This is the classic **Frobenius coin problem** for two denominations.

---

# 2. Key observation: the two numbers are coprime

The input says `primeOne` and `primeTwo` are **distinct prime numbers**.

That matters a lot.

Since they are distinct primes:

- neither divides the other
- their only common divisor is `1`

So:

`gcd(primeOne, primeTwo) = 1`

This means the two denominations are **coprime**.

That is the exact condition under which the standard two-number Frobenius formula applies.

---

# 3. The known result

For two **coprime** positive integers `a` and `b`, the largest integer that **cannot** be represented in the form:

`a*x + b*y`

for nonnegative integers `x` and `y` is:

`a*b - a - b`

This value is called the **Frobenius number** for two denominations.

So here the answer is simply:

`primeOne * primeTwo - primeOne - primeTwo`

---

# 4. Why this formula makes sense intuitively

## 4.1 What “reachable” means

A price is reachable if Alice can pay it exactly using:

- some number of `primeOne` coins
- some number of `primeTwo` coins

That means:

`price = primeOne * a + primeTwo * b`

for some nonnegative integers `a`, `b`.

If no such pair exists, that price is unreachable.

We want the **largest unreachable** price.

---

## 4.2 Why unreachable values stop eventually

Because the denominations are coprime, once the numbers get large enough, every value can be formed.

This is a deep but standard fact.

So there are only finitely many unreachable values, and asking for the largest unreachable one makes sense.

If the two denominations were **not** coprime, this would not be true.
For example, with denominations `4` and `6`, every reachable value is even, so infinitely many odd values would be unreachable.

But here they are distinct primes, so they are coprime, and the problem has a clean finite answer.

---

## 4.3 Why `a*b - a - b` is the boundary

Let the two denominations be:

- `a = primeOne`
- `b = primeTwo`

The theorem says the largest unreachable value is:

`a*b - a - b`

This can also be written as:

`(a - 1)(b - 1) - 1`

That form is often useful when thinking about the boundary.

The rough intuition is:

- below this threshold, there are still gaps
- at and above the next number, all numbers become representable

So:

- `a*b - a - b` is the **last missing value**
- every value greater than that is achievable

---

# 5. A more concrete intuition using modular thinking

Suppose we try to build numbers using:

`a*x + b*y`

Fix `y` and vary `x`. Then you get numbers spaced by `a`.

Fix `x` and vary `y`. Then you get numbers spaced by `b`.

Because `a` and `b` are coprime, multiples of one denomination cycle through **all remainders modulo the other denomination**.

For example, the values:

`0, a, 2a, 3a, ...`

produce every residue modulo `b` at some point.

That means for every remainder mod `b`, eventually you can hit a number with that remainder using multiples of `a`, and then add enough `b`s to reach all larger numbers with the same remainder.

So after a certain threshold, every residue class is covered, which means every sufficiently large number is reachable.

The theorem tells us that the exact last gap is:

`a*b - a - b`

---

# 6. Small example

Suppose:

- `primeOne = 2`
- `primeTwo = 5`

The formula gives:

`2*5 - 2 - 5 = 3`

Now list reachable values:

- `2 = 2`
- `4 = 2 + 2`
- `5 = 5`
- `6 = 2 + 2 + 2`
- `7 = 5 + 2`
- `8 = 2 + 2 + 2 + 2`
- `9 = 5 + 2 + 2`

Unreachable positive values are:

- `1`
- `3`

The largest unreachable one is `3`.

That matches the formula.

---

# 7. Another example

Suppose:

- `primeOne = 3`
- `primeTwo = 5`

Formula:

`3*5 - 3 - 5 = 7`

Check the values:

Reachable:

- `3`
- `5`
- `6 = 3+3`
- `8 = 3+5`
- `9 = 3+3+3`
- `10 = 5+5`
- `11 = 3+3+5`

Unreachable:

- `1`
- `2`
- `4`
- `7`

Largest unreachable is `7`.

Again, formula works.

---

# 8. Why distinct primes matter

The formula `a*b - a - b` works whenever `a` and `b` are coprime.
The problem gives **distinct prime numbers**, which is a stronger condition.

Why stronger?

Because:

- every prime is greater than `1`
- two distinct primes cannot share a factor
- so they are guaranteed coprime

That removes the need to separately check `gcd(primeOne, primeTwo) == 1`.

If the problem had given arbitrary integers, we would first need to check whether they are coprime.

---

# 9. Final formula

Let:

- `a = primeOne`
- `b = primeTwo`

Then the answer is:

`a*b - a - b`

So:

`primeOne * primeTwo - primeOne - primeTwo`

---

# 10. Java code

```java
class Solution {
    public int mostExpensiveItem(int primeOne, int primeTwo) {
        return primeOne * primeTwo - primeOne - primeTwo;
    }
}
```

---

# 11. Code explanation line by line

## Method signature

```java
public int mostExpensiveItem(int primeOne, int primeTwo)
```

This method receives the two prime denominations.

## Return statement

```java
return primeOne * primeTwo - primeOne - primeTwo;
```

This directly applies the two-denomination Frobenius formula.

No loops are needed.
No dynamic programming is needed.
No search is needed.

The result is computed in constant time.

---

# 12. Why no brute force is needed

A brute-force approach might try to:

- generate all values from combinations of the two coins
- mark which sums are reachable
- search for the largest missing number

That would be unnecessary and inefficient.

Because this is a known number theory result, we can jump straight to the answer with a formula.

This is one of those problems where pattern recognition matters more than implementation complexity.

---

# 13. Correctness argument

We rely on the standard theorem:

For two coprime positive integers `a` and `b`, the largest nonrepresentable value of:

`a*x + b*y`

with `x, y >= 0` is:

`a*b - a - b`

Since `primeOne` and `primeTwo` are distinct primes, they are coprime.

Therefore the theorem applies directly, and the method returns exactly the largest unreachable item price.

---

# 14. Complexity analysis

## Time complexity

The method performs:

- one multiplication
- two subtractions

So:

**Time complexity: `O(1)`**

## Space complexity

The method uses only a few scalar variables and no extra data structures.

So:

**Space complexity: `O(1)`**

---

# 15. Final takeaway

This is a direct application of the **Frobenius coin theorem for two coprime denominations**.

Because the two denominations are distinct primes, they are automatically coprime.

So the largest price that Alice cannot form is:

`primeOne * primeTwo - primeOne - primeTwo`

That leads to a very short solution:

- no iteration
- no recursion
- no DP
- no greedy simulation

Just the formula.

---

# 16. One subtle point

The representation allows **zero** coins of one denomination.

So values like:

- `primeOne`
- `2 * primeOne`
- `primeTwo`

are all valid.

We are solving for nonnegative integer combinations, not strictly positive counts of both coin types.

That is exactly the setting required by the theorem.

---
