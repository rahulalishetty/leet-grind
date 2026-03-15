# Minimum Number of Primes to Sum to `n` Using the First `m` Primes

## Problem

You are given two integers:

- `n`
- `m`

You may choose a **multiset** of prime numbers from the **first `m` prime numbers**.

That means:

- only the first `m` primes are allowed
- each allowed prime may be used **any number of times**
- the selected primes must sum to exactly `n`

We need to return:

- the **minimum number of primes** needed to make sum `n`
- or `-1` if it is impossible

---

# 1. What the problem is really asking

This is a classic **minimum coin change** problem.

Why?

Because the first `m` prime numbers behave exactly like coin denominations:

- allowed denominations = first `m` primes
- target sum = `n`
- each denomination can be used repeatedly
- goal = minimize how many denominations are used

So the problem reduces to:

> Using unlimited copies of the first `m` primes, what is the minimum number of values needed to make total `n`?

That is the standard **unbounded knapsack / minimum coin change** pattern.

---

# 2. Key insight

Suppose the allowed primes are:

`p1, p2, p3, ..., pm`

Now think about some target sum `x`.

If the optimal solution for `x` ends by using prime `p`, then before that last choice we must have already formed:

`x - p`

So if we knew the minimum number of primes needed to form all smaller sums, we could build the answer for `x`.

That leads directly to dynamic programming.

---

# 3. DP state

Let:

`dp[s] = minimum number of primes needed to form sum s`

This is the central state.

Meaning:

- if `dp[s]` is finite, then sum `s` is reachable
- and `dp[s]` is the smallest number of primes needed to reach it
- if `dp[s]` is infinity, then `s` is not yet reachable

---

# 4. Base case

The easiest sum is:

`0`

To make sum `0`, we need no primes at all.

So:

`dp[0] = 0`

For every other sum initially:

`dp[s] = INF`

where `INF` is some very large number meaning “not reachable yet”.

---

# 5. Transition

For each allowed prime `p`, and for each target sum `s >= p`:

If `s - p` is already reachable, then we can make `s` by:

- first making `s - p`
- then adding one more prime `p`

So the transition is:

`dp[s] = min(dp[s], dp[s - p] + 1)`

This is the exact minimum-coin-change recurrence.

---

# 6. Why forward iteration is correct

Because each prime may be used **multiple times**, this is an **unbounded** usage problem.

That is why, when processing a prime `p`, we iterate sums in increasing order:

```text
for sum = p to n
```

This matters.

If we update in forward order, then while processing prime `p`, the value `dp[sum - p]` may already have been improved using the same prime `p` earlier in the same iteration.

That correctly allows repeated use of the same prime.

So forward iteration is the correct pattern for **unbounded** coin change.

---

# 7. Why this is optimal

This is a standard optimal-substructure argument.

Suppose an optimal solution for sum `s` uses some last prime `p`.

Then the rest of the solution must form sum:

`s - p`

And that smaller part must itself be optimal.

Why?

Because if there were a better way to form `s - p`, we could replace it and get a better solution for `s`, which would contradict optimality.

So taking the minimum over all allowed last primes gives the true optimum.

That is why the recurrence works.

---

# 8. First we must generate the first `m` primes

Before the DP starts, we need the allowed denominations.

So we generate:

- first 1 prime → `[2]`
- first 2 primes → `[2, 3]`
- first 3 primes → `[2, 3, 5]`
- first 4 primes → `[2, 3, 5, 7]`
- and so on

A simple prime generator using trial division is enough unless constraints are extremely large.

---

# 9. Prime generation logic

To generate the first `m` primes:

1. Start from `2`
2. Test each number for primality
3. If it is prime, add it to the list
4. Stop when we have collected `m` primes

For primality testing by trial division:

A number `x` is prime if no integer from `2` to `sqrt(x)` divides it.

This is straightforward and usually sufficient for moderate constraints.

---

# 10. Complete algorithm

## Step 1

Generate the first `m` prime numbers.

## Step 2

Create a DP array `dp` of size `n + 1`.

## Step 3

Initialize:

- `dp[0] = 0`
- all others = `INF`

## Step 4

For each allowed prime `p`:

- for each sum `s` from `p` to `n`:
  - if `dp[s - p]` is reachable:
    - update `dp[s] = min(dp[s], dp[s - p] + 1)`

## Step 5

If `dp[n]` is still `INF`, return `-1`.
Otherwise return `dp[n]`.

---

# 11. Small example

Suppose:

`n = 11`
`m = 3`

The first 3 primes are:

`[2, 3, 5]`

We want the minimum number of these primes that sum to `11`.

Possible constructions:

- `5 + 3 + 3 = 11` → 3 primes
- `5 + 2 + 2 + 2 = 11` → 4 primes
- `3 + 2 + 2 + 2 + 2 = 11` → 5 primes

So the best answer is:

`3`

---

# 12. DP walkthrough for the example

Allowed primes:

`[2, 3, 5]`

We initialize:

- `dp[0] = 0`
- everything else = `INF`

---

## After processing prime `2`

Reachable sums:

- `dp[2] = 1`
- `dp[4] = 2`
- `dp[6] = 3`
- `dp[8] = 4`
- `dp[10] = 5`

Odd sums are still unreachable.

---

## After processing prime `3`

Now we can improve and create more sums:

- `dp[3] = 1`
- `dp[5] = dp[2] + 1 = 2`
- `dp[6] = min(3, dp[3] + 1 = 2)` → becomes `2`
- `dp[7] = dp[4] + 1 = 3`
- `dp[8] = min(4, dp[5] + 1 = 3)` → becomes `3`
- `dp[9] = dp[6] + 1 = 3`
- `dp[10] = min(5, dp[7] + 1 = 4)` → becomes `4`
- `dp[11] = dp[8] + 1 = 4`

At this point, sum `11` is reachable in `4` primes.

---

## After processing prime `5`

Now we may improve further:

- `dp[5] = min(2, dp[0] + 1 = 1)` → becomes `1`
- `dp[7] = min(3, dp[2] + 1 = 2)` → becomes `2`
- `dp[8] = min(3, dp[3] + 1 = 2)` → becomes `2`
- `dp[10] = min(4, dp[5] + 1 = 2)` → becomes `2`
- `dp[11] = min(4, dp[6] + 1 = 3)` → becomes `3`

So the final answer is:

`dp[11] = 3`

which matches:

`5 + 3 + 3`

---

# 13. Java code

```java
import java.util.*;

class Solution {
    public int minNumberOfPrimes(int n, int m) {
        List<Integer> primes = firstMPrimes(m);

        int INF = 1_000_000_000;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int p : primes) {
            for (int sum = p; sum <= n; sum++) {
                if (dp[sum - p] != INF) {
                    dp[sum] = Math.min(dp[sum], dp[sum - p] + 1);
                }
            }
        }

        return dp[n] == INF ? -1 : dp[n];
    }

    private List<Integer> firstMPrimes(int m) {
        List<Integer> primes = new ArrayList<>();
        int num = 2;

        while (primes.size() < m) {
            if (isPrime(num)) {
                primes.add(num);
            }
            num++;
        }

        return primes;
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

# 14. Code explanation in detail

## 14.1 Main method

```java
public int minNumberOfPrimes(int n, int m)
```

This method returns the minimum number of allowed primes needed to make sum `n`.

---

## 14.2 Generate the allowed primes

```java
List<Integer> primes = firstMPrimes(m);
```

This builds the list of denominations.

If `m = 4`, the list will be:

`[2, 3, 5, 7]`

---

## 14.3 Initialize the DP array

```java
int INF = 1_000_000_000;
int[] dp = new int[n + 1];
Arrays.fill(dp, INF);
dp[0] = 0;
```

- `INF` means unreachable
- `dp[0] = 0` because zero sum needs zero primes

---

## 14.4 Process each prime

```java
for (int p : primes) {
```

We treat each prime like a reusable coin denomination.

---

## 14.5 Process sums in increasing order

```java
for (int sum = p; sum <= n; sum++) {
```

This forward iteration allows unlimited reuse of `p`.

That is exactly what we want.

---

## 14.6 Transition

```java
if (dp[sum - p] != INF) {
    dp[sum] = Math.min(dp[sum], dp[sum - p] + 1);
}
```

If `sum - p` is reachable, then we can reach `sum` by adding one more prime `p`.

We take the minimum across all such choices.

---

## 14.7 Return result

```java
return dp[n] == INF ? -1 : dp[n];
```

If `dp[n]` is still unreachable, return `-1`.
Otherwise return the minimum count.

---

## 14.8 Prime generator

```java
private List<Integer> firstMPrimes(int m)
```

This collects the first `m` prime numbers by checking successive integers.

---

## 14.9 Primality check

```java
private boolean isPrime(int x)
```

A number is prime if no divisor from `2` through `sqrt(x)` divides it.

This is standard trial division.

---

# 15. Correctness argument

We can justify correctness formally.

## Claim

After processing all allowed primes, `dp[s]` equals the minimum number of allowed primes needed to sum to `s`, or `INF` if impossible.

## Proof idea

### Base case

`dp[0] = 0` is correct because zero sum requires zero primes.

### Inductive step

Suppose we want the optimal way to form sum `s`.

If it is possible, then there exists a last chosen prime `p`.
Before choosing `p`, we must have formed `s - p`.

By the inductive reasoning of dynamic programming, `dp[s - p]` stores the minimum number of primes needed for `s - p`.

So using `p` last gives a candidate:

`dp[s - p] + 1`

Taking the minimum over all allowed primes `p` gives the true optimum for `s`.

If no such `p` exists, then `s` remains unreachable.

Therefore the DP computes the correct answer.

---

# 16. Complexity analysis

Let:

- `n` = target sum
- `m` = number of allowed primes
- `P` = approximate largest number examined while generating the first `m` primes

---

## 16.1 Prime generation complexity

Using naive trial division:

- each primality test takes about `O(sqrt(x))`
- we test numbers from `2` upward until we collect `m` primes

So the generation cost is roughly:

**`O(P * sqrt(P))`**

This is a loose upper bound for trial-division generation.

For moderate input sizes, this is usually fine.

---

## 16.2 DP complexity

We have:

- `m` primes
- `n` sums

For each prime, we scan all sums from `p` to `n`.

So the DP cost is:

**Time complexity: `O(m * n)`**

---

## 16.3 Space complexity

The DP array has size `n + 1`.

So:

**Space complexity: `O(n)`**

The prime list itself stores `m` elements, which is smaller unless `m` is very large.

More precisely, total auxiliary space is:

**`O(n + m)`**

but the dominant DP space is usually summarized as:

**`O(n)`**

---

# 17. Edge cases

## Case 1: `n = 0`

We need zero primes to sum to zero.

Answer:

`0`

---

## Case 2: `m = 0`

No primes are available.

Then:

- if `n = 0`, answer is `0`
- otherwise answer is `-1`

---

## Case 3: all allowed primes are larger than `n`

Then no sum can be formed except possibly `0`.

So answer is `-1` for positive `n`.

---

## Case 4: impossible target

Even with some primes available, not every sum may be reachable.

Example:

- allowed primes = `[5, 7]`
- target `n = 1`

Impossible, so answer is `-1`.

---

# 18. Why greedy does not work

A greedy approach such as:

- always take the largest possible prime first

is not reliable for minimum-count coin problems in general.

Example intuition:
A large coin may look good locally, but it may leave a remainder that forces many extra coins, while a different earlier choice could lead to a better total.

So dynamic programming is the safe and correct method.

---

# 19. Final takeaway

This problem is a direct **minimum coin change** problem where:

- the coin denominations are the first `m` primes
- each coin can be used infinitely many times
- the target is `n`

So the solution is:

1. generate the first `m` primes
2. run unbounded knapsack DP
3. return the minimum count for sum `n`, or `-1` if unreachable

The core recurrence is:

`dp[s] = min(dp[s], dp[s - p] + 1)`

for every allowed prime `p`.

That gives a clean, correct, and efficient solution.

---
