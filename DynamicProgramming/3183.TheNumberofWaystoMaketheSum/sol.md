# Number of Ways to Make Sum `n` with Coins `1, 2, 6` (Unlimited) and Coin `4` (At Most 2 Copies)

## Problem Restatement

We have:

- unlimited coins of value `1`
- unlimited coins of value `2`
- unlimited coins of value `6`
- exactly **up to 2** coins of value `4`

Given an integer `n`, we need to count how many **distinct combinations** of coins can make sum `n`.

Important detail:

- order does **not** matter
- so `{1, 1, 2, 4}` is the same combination regardless of arrangement

The answer should be returned modulo:

```text
10^9 + 7
```

---

## Core Intuition

This problem looks like a coin change counting problem, but there is one special twist:

- coins `1`, `2`, and `6` are unlimited
- coin `4` is **bounded**: we can use it only `0`, `1`, or `2` times

That bounded part is the key simplification.

Instead of trying to handle everything together in one large DP, we can separate the problem into cases based on how many `4`-coins we use.

Since the number of `4`-coins is tiny, there are only **three possible cases**:

- use `0` coins of value `4`
- use `1` coin of value `4`
- use `2` coins of value `4`

For each case, subtract that contribution from `n`, and then count the number of ways to form the remaining amount using only unlimited `1`, `2`, and `6`.

So the hard part becomes:

> How many ways can we write a number `m` as
> `1*x + 2*y + 6*z = m`
> where `x, y, z >= 0`?

Once we solve that efficiently, we just evaluate it for at most three values of `m`.

---

## Step 1: Fix the Number of `4`-Coins

Let:

- `k` = number of `4`-coins used

Because only two `4`-coins are available:

```text
k ∈ {0, 1, 2}
```

If we use `k` such coins, they contribute:

```text
4k
```

So the remaining sum to build is:

```text
m = n - 4k
```

If `m < 0`, that case is impossible and contributes `0`.

So the answer is:

```text
ways(n) = count(m = n) + count(m = n - 4) + count(m = n - 8)
```

where `count(m)` means:

> number of ways to make `m` using unlimited `1`, `2`, and `6`

---

## Step 2: Count Ways to Form `m` Using `1`, `2`, and `6`

We need the number of nonnegative integer solutions to:

```text
x + 2y + 6z = m
```

where:

- `x` = number of `1`-coins
- `y` = number of `2`-coins
- `z` = number of `6`-coins

Since order does not matter, each valid triple `(x, y, z)` corresponds to exactly one combination.

So we only need to count how many such triples exist.

---

## Step 3: Fix the Number of `6`-Coins

Suppose we fix `z`.

Then the remaining amount is:

```text
m - 6z
```

Now we need:

```text
x + 2y = m - 6z
```

For a fixed `z`, how many choices of `y` are possible?

Since `x >= 0`, we need:

```text
2y <= m - 6z
```

So:

```text
y = 0, 1, 2, ..., floor((m - 6z) / 2)
```

For every such `y`, the value of `x` is uniquely determined:

```text
x = m - 6z - 2y
```

So for a fixed `z`, the number of valid combinations is:

```text
floor((m - 6z) / 2) + 1
```

Now sum this over all valid `z`:

```text
z = 0, 1, 2, ..., floor(m / 6)
```

Therefore:

```text
count(m) = Σ [ floor((m - 6z)/2) + 1 ]
           for z = 0 to floor(m/6)
```

This already gives a correct formula.

---

## Step 4: Simplify the Summation

Let:

```text
t = floor(m / 6)
```

Then:

```text
count(m) = Σ [ floor((m - 6z)/2) + 1 ]
           for z = 0 to t
```

Now notice something important:

- `6z` is always even
- subtracting an even number does not change the parity of `m`

So:

```text
floor((m - 6z)/2) = floor(m/2) - 3z
```

This is the critical algebraic simplification.

Substitute it into the sum:

```text
count(m) = Σ [ floor(m/2) - 3z + 1 ]
           for z = 0 to t
```

Rewrite:

```text
count(m) = Σ [ floor(m/2) + 1 ] - Σ [ 3z ]
           for z = 0 to t
```

There are `t + 1` terms, so:

```text
Σ [ floor(m/2) + 1 ] = (t + 1)(floor(m/2) + 1)
```

And:

```text
Σ z = t(t + 1) / 2
```

Therefore:

```text
count(m) = (t + 1)(floor(m/2) + 1) - 3 * t(t + 1)/2
```

where:

```text
t = floor(m/6)
```

This gives an `O(1)` formula for `count(m)`.

---

## Final Formula

For each `k` in `{0, 1, 2}`:

```text
m = n - 4k
```

If `m >= 0`, compute:

```text
t = floor(m / 6)

count(m) = (t + 1)(floor(m/2) + 1) - 3 * t(t + 1)/2
```

Then:

```text
answer = count(n) + count(n - 4) + count(n - 8)
```

ignoring any negative remainder.

Finally take modulo `10^9 + 7`.

---

## Why This Approach Is Better Than Standard DP

A standard coin change DP would also work, but it would usually require `O(n)` time and `O(n)` space (or at least `O(n)` time).

That is fine for moderate `n`, but unnecessary here.

This problem has a hidden mathematical structure:

- only one coin type is bounded
- and that bound is extremely small
- the remaining coin set `{1,2,6}` is easy to count analytically

So instead of dynamic programming, we derive a direct counting formula.

That reduces the complexity to:

- **Time:** `O(1)`
- **Space:** `O(1)`

This is much stronger.

---

## Java Code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;

    public int countWays(int n) {
        long ans = 0;

        for (int k = 0; k <= 2; k++) {
            int m = n - 4 * k;
            if (m < 0) continue;

            long t = m / 6L;
            long ways = (t + 1) * (m / 2L + 1) - 3L * t * (t + 1) / 2L;
            ans = (ans + ways) % MOD;
        }

        return (int) ans;
    }
}
```

---

## Code Walkthrough

### `private static final long MOD = 1_000_000_007L;`

We use the required modulo constant.

It is stored as `long` so intermediate arithmetic also happens safely in 64-bit range.

---

### `long ans = 0;`

This stores the total number of valid combinations across the three possible choices of how many `4`-coins we use.

---

### `for (int k = 0; k <= 2; k++)`

We iterate over the only possible counts of `4`-coins:

- `k = 0`
- `k = 1`
- `k = 2`

This is why the algorithm is constant time.

---

### `int m = n - 4 * k;`

After fixing `k`, the remaining amount to form using `1`, `2`, and `6` is `m`.

---

### `if (m < 0) continue;`

If the remaining amount is negative, that case is impossible.

Example:

- if `n = 3`
- and `k = 1`
- then `m = 3 - 4 = -1`

Impossible, so skip it.

---

### `long t = m / 6L;`

This computes:

```text
t = floor(m / 6)
```

which is the maximum possible number of `6`-coins.

---

### `long ways = (t + 1) * (m / 2L + 1) - 3L * t * (t + 1) / 2L;`

This is the closed-form formula for counting ways to make `m` using `1`, `2`, and `6`.

Breakdown:

- `(t + 1)` = number of possible values of `z`
- `(m / 2L + 1)` = corresponds to `floor(m/2) + 1`
- `3L * t * (t + 1) / 2L` subtracts the arithmetic progression part

This line is exactly the compact version of the summation derivation.

---

### `ans = (ans + ways) % MOD;`

Add the number of ways for this `k` to the total answer.

We apply modulo each time to keep numbers controlled.

---

### `return (int) ans;`

The final answer is guaranteed modulo `10^9 + 7`, so it fits in `int`.

---

## Small Example

Let `n = 8`.

We consider the three possible counts of `4`-coins.

### Case 1: `k = 0`

Remaining sum:

```text
m = 8
```

Need solutions to:

```text
x + 2y + 6z = 8
```

Possible `z`:

- `z = 0`: `x + 2y = 8` → `y = 0..4` → `5` ways
- `z = 1`: `x + 2y = 2` → `y = 0..1` → `2` ways

Total = `7`

Using formula:

- `t = floor(8/6) = 1`
- `floor(8/2) = 4`

```text
count(8) = (1 + 1)(4 + 1) - 3 * 1 * 2 / 2
         = 2 * 5 - 3
         = 7
```

Correct.

---

### Case 2: `k = 1`

Remaining sum:

```text
m = 4
```

Need solutions to:

```text
x + 2y + 6z = 4
```

Only `z = 0` works:

- `x + 2y = 4` → `y = 0..2` → `3` ways

Formula:

- `t = 0`
- `floor(4/2) = 2`

```text
count(4) = (0 + 1)(2 + 1) - 0 = 3
```

Correct.

---

### Case 3: `k = 2`

Remaining sum:

```text
m = 0
```

Need solutions to:

```text
x + 2y + 6z = 0
```

Only one way:

- `x = 0, y = 0, z = 0`

Formula:

- `t = 0`
- `floor(0/2) = 0`

```text
count(0) = 1
```

Correct.

---

### Final answer

```text
7 + 3 + 1 = 11
```

So there are `11` ways to make `8`.

---

## Correctness Argument

We can justify the solution formally.

### 1. Partition by number of `4`-coins

Every valid combination uses either:

- `0` coins of value `4`
- `1` coin of value `4`
- `2` coins of value `4`

These cases are disjoint and cover all possibilities.

So summing over these cases is complete and non-overlapping.

---

### 2. For fixed `k`, counting reduces to solutions of `x + 2y + 6z = m`

Once `k` is fixed, the contribution of `4`-coins is fixed.

The remaining sum must be built only using `1`, `2`, and `6`.

Thus every valid combination corresponds exactly to one nonnegative integer triple `(x, y, z)` satisfying:

```text
x + 2y + 6z = m
```

---

### 3. For fixed `z`, number of `(x, y)` pairs is `floor((m - 6z)/2) + 1`

After choosing `z`, the remaining equation is:

```text
x + 2y = m - 6z
```

For each valid `y`, there is exactly one `x`.

So the count is exactly the number of valid values of `y`, which is:

```text
floor((m - 6z)/2) + 1
```

---

### 4. Summing over all possible `z` gives the full count

`z` can range from `0` to `floor(m/6)`.

Summing the above count over all such `z` counts every valid triple exactly once.

So the formula is correct.

---

### 5. Algebraic simplification preserves equality

The step:

```text
floor((m - 6z)/2) = floor(m/2) - 3z
```

is valid because `6z` is even.

Hence the closed form is exactly equal to the original summation, not an approximation.

Therefore the final algorithm is correct.

---

## Complexity Analysis

### Time Complexity: `O(1)`

Why?

- the loop over `k` runs exactly `3` times
- each iteration performs only constant-time arithmetic

So total time is constant.

This is stronger than `O(log n)` or `O(n)`.

---

### Space Complexity: `O(1)`

Why?

- only a few variables are used: `ans`, `k`, `m`, `t`, `ways`
- no arrays, hash maps, recursion stack, or DP table

So the extra space is constant.

---

## Practical Notes

### 1. Why use `long`?

Even though the answer is returned modulo `10^9 + 7`, intermediate values in the formula can become larger than `int`.

So `long` is the safe choice for arithmetic.

---

### 2. Why modulo only after each case?

Because there are only three cases. Intermediate values are already safe in `long`, so this is enough.

---

### 3. Could DP also solve it?

Yes.

A DP approach can be written, but it would be less elegant and less efficient for this particular structure.

This formula-based solution is the cleanest one.

---

## Final Takeaway

The entire problem becomes simple once you notice two things:

1. the count of `4`-coins is tiny (`0`, `1`, or `2`)
2. after fixing that, counting ways with `1`, `2`, and `6` can be done mathematically

So instead of building combinations explicitly or using a full DP, we:

- split by number of `4`-coins
- derive a counting formula for the rest
- sum the three results

That yields a very compact and powerful solution:

- mathematically clean
- constant time
- constant space
- easy to implement safely in Java
