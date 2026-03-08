# Count of Self-Divisible Permutations

## Problem

You are given an integer `n`.

Consider the 1-indexed array:

`nums = [1, 2, 3, ..., n]`

We need to count how many **permutations** of these numbers are **self-divisible**.

A permutation `a` of length `n` is called **self-divisible** if for every position `i` from `1` to `n`:

`gcd(a[i], i) == 1`

That means:

- the value placed at position `i`
- and the position index `i`

must be **coprime**.

We need to return the total number of such valid permutations.

---

# 1. What the problem is really asking

This is a constrained permutation-counting problem.

Normally, if there were no restrictions, the number of permutations of `[1..n]` would be:

`n!`

But here, not every number can go into every position.

For each position `i`, only some values `x` are allowed:

`x` is allowed at position `i` if `gcd(x, i) == 1`

So this is really a problem of:

- assigning numbers to positions
- without reuse
- while respecting compatibility constraints

That is exactly the kind of structure where **bitmask DP** works very well.

---

# 2. Core intuition

## 2.1 Think in terms of filling positions one by one

Imagine we fill the permutation from left to right:

- first fill position `1`
- then position `2`
- then position `3`
- ...
- finally position `n`

At each step:

- some numbers have already been used
- some numbers are still available
- for the current position, we can only choose an unused number that is coprime with the position index

That naturally suggests a state defined by:

- which numbers are already used

Once we know that, we also know:

- how many positions have been filled
- which position comes next

That is the central DP insight.

---

## 2.2 Why the used-set is enough as a state

Suppose we know exactly which numbers have already been chosen.

Then:

- the number of chosen values tells us how many positions are already filled
- if `k` values are chosen, then the next position to fill is `k + 1`

So we do **not** need to separately store the current position in the state.
It is implied by the number of bits set in the mask.

This makes the DP compact and elegant.

---

# 3. Bitmask representation

We use a bitmask of length `n`.

- bit `0` corresponds to number `1`
- bit `1` corresponds to number `2`
- ...
- bit `n-1` corresponds to number `n`

If a bit is `1`, that means the corresponding number has already been used.

Example for `n = 4`:

- mask `0101` means numbers `1` and `3` are used
- so two positions are already filled
- the next position to fill is `3`

because `popcount(0101) = 2`

---

# 4. DP state definition

Let:

`dp[mask] = number of ways to fill the first popcount(mask) positions using exactly the numbers marked in mask`

This definition is very important.

It means:

- the numbers used are exactly those in `mask`
- they have already been arranged validly into the first few positions
- and `dp[mask]` counts how many such valid arrangements exist

---

# 5. Base case

If no numbers are used yet:

`mask = 0`

Then no positions are filled yet, and there is exactly one way to do nothing.

So:

`dp[0] = 1`

This is the starting point of the DP.

---

# 6. Transition

Suppose we are at state `mask`.

Let:

`pos = popcount(mask) + 1`

This is the next position we want to fill.

Now try every number `num` from `1` to `n`:

### Step 1: check whether `num` is unused

If the bit for `num` is already set in `mask`, skip it.

### Step 2: check whether `num` can go into `pos`

It must satisfy:

`gcd(num, pos) == 1`

If true, then placing `num` at position `pos` is valid.

### Step 3: move to the next state

Let:

`newMask = mask | (1 << (num - 1))`

Then:

`dp[newMask] += dp[mask]`

because every valid arrangement counted by `dp[mask]` can be extended by placing `num` at the next position.

---

# 7. Why this DP is correct

## 7.1 Every valid permutation is counted

Take any valid self-divisible permutation.

If we read it from position `1` to `n`, then at each step:

- the chosen number is unused
- it is coprime with the current position

So the permutation corresponds to a valid sequence of DP transitions from `mask = 0` to the full mask.

Therefore every valid permutation contributes to the answer.

---

## 7.2 No invalid permutation is counted

The DP only adds transitions when:

`gcd(num, pos) == 1`

So every assignment made by the DP respects the rule at every position.

Also, a number can only be used once because the mask prevents reuse.

Therefore every counted construction is a valid permutation.

---

## 7.3 No permutation is counted twice

A permutation determines exactly one order of choices:

- the number at position `1`
- then the number at position `2`
- ...
- then the number at position `n`

So every valid permutation maps to exactly one unique path through the DP.

Hence the counting is exact.

---

# 8. Final answer

When all numbers are used, the mask is:

`(1 << n) - 1`

This means all `n` positions have been filled.

So the answer is:

`dp[(1 << n) - 1]`

---

# 9. Small worked example: `n = 3`

We need permutations of `[1, 2, 3]` such that:

- position `1`: `gcd(a[1], 1) = 1`
- position `2`: `gcd(a[2], 2) = 1`
- position `3`: `gcd(a[3], 3) = 1`

## 9.1 Allowed numbers by position

### Position 1

Every number is coprime with `1`.

Allowed: `{1, 2, 3}`

### Position 2

Need coprime with `2`.

- `gcd(1,2)=1` allowed
- `gcd(2,2)=2` not allowed
- `gcd(3,2)=1` allowed

Allowed: `{1, 3}`

### Position 3

Need coprime with `3`.

- `gcd(1,3)=1` allowed
- `gcd(2,3)=1` allowed
- `gcd(3,3)=3` not allowed

Allowed: `{1, 2}`

## 9.2 Check all permutations

Permutations of `[1,2,3]`:

1. `[1,2,3]`
   - position 2 has 2, `gcd(2,2)=2` → invalid

2. `[1,3,2]`
   - position 2 has 3, `gcd(3,2)=1`
   - position 3 has 2, `gcd(2,3)=1`
   - valid

3. `[2,1,3]`
   - position 3 has 3, `gcd(3,3)=3` → invalid

4. `[2,3,1]`
   - valid

5. `[3,1,2]`
   - valid

6. `[3,2,1]`
   - position 2 has 2, `gcd(2,2)=2` → invalid

So the valid permutations are:

- `[1,3,2]`
- `[2,3,1]`
- `[3,1,2]`

Total = `3`

This example also shows why checking carefully matters.
It is easy to miss `[1,3,2]` if you reason too quickly.

---

# 10. Step-by-step DP sketch for `n = 3`

We use a 3-bit mask.

- bit 0 → number 1
- bit 1 → number 2
- bit 2 → number 3

Initial:

`dp[000] = 1`

## From `000`

`popcount = 0`, so next position is `1`

Allowed unused numbers at position `1`: `1, 2, 3`

Transitions:

- place `1` → `001`
- place `2` → `010`
- place `3` → `100`

So:

- `dp[001] += 1`
- `dp[010] += 1`
- `dp[100] += 1`

## From `001`

Used numbers: `{1}`
Next position: `2`

Unused numbers: `2, 3`

Check compatibility with position `2`:

- `2` → `gcd(2,2)=2`, invalid
- `3` → `gcd(3,2)=1`, valid

Transition:

- `001 -> 101`

## From `010`

Used numbers: `{2}`
Next position: `2`

Unused numbers: `1, 3`

- `1` valid
- `3` valid

Transitions:

- `010 -> 011`
- `010 -> 110`

## From `100`

Used numbers: `{3}`
Next position: `2`

Unused numbers: `1, 2`

- `1` valid
- `2` invalid

Transition:

- `100 -> 101`

## Continue to full masks

The full valid paths lead to full mask `111` exactly 3 times.

So answer = `3`.

---

# 11. Java code

```java
class Solution {
    public int selfDivisiblePermutationCount(int n) {
        int size = 1 << n;
        long[] dp = new long[size];
        dp[0] = 1;

        for (int mask = 0; mask < size; mask++) {
            int pos = Integer.bitCount(mask) + 1; // next position to fill
            if (pos > n) continue;

            for (int num = 1; num <= n; num++) {
                int bit = 1 << (num - 1);

                // skip if this number is already used
                if ((mask & bit) != 0) continue;

                // can place num at position pos only if they are coprime
                if (gcd(num, pos) == 1) {
                    dp[mask | bit] += dp[mask];
                }
            }
        }

        return (int) dp[size - 1];
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# 12. Code explanation in detail

## 12.1 DP array size

```java
int size = 1 << n;
long[] dp = new long[size];
```

There are `2^n` possible subsets of numbers `1..n`.

So we need one DP entry per mask.

`long` is safer than `int` for intermediate counts.

## 12.2 Base state

```java
dp[0] = 1;
```

There is exactly one way to fill zero positions with zero numbers.

## 12.3 Iterate through all masks

```java
for (int mask = 0; mask < size; mask++) {
```

We process every subset of used numbers.

## 12.4 Determine the next position

```java
int pos = Integer.bitCount(mask) + 1;
```

If `mask` has `k` used numbers, then positions `1..k` are already filled.

So the next position is `k + 1`.

## 12.5 Skip full masks

```java
if (pos > n) continue;
```

If all positions are already filled, there is nothing left to extend.

## 12.6 Try every possible number

```java
for (int num = 1; num <= n; num++) {
    int bit = 1 << (num - 1);
```

We test whether `num` can be placed at the current position.

## 12.7 Ignore used numbers

```java
if ((mask & bit) != 0) continue;
```

If its bit is already set, this number has already been used in the permutation.

## 12.8 Check the coprime condition

```java
if (gcd(num, pos) == 1) {
```

Only then is the placement valid.

## 12.9 Update the next state

```java
dp[mask | bit] += dp[mask];
```

All valid arrangements leading to `mask` can be extended by placing `num`.

So their count is added to the new mask.

## 12.10 Return the full-mask answer

```java
return (int) dp[size - 1];
```

`size - 1` is the mask with all `n` bits set.

That state represents all numbers used and all positions filled.

---

# 13. Complexity analysis

Let us analyze it carefully.

## 13.1 Number of states

There are:

`2^n`

possible masks.

So the DP has `2^n` states.

## 13.2 Work per state

For each mask, we try all numbers from `1` to `n`.

So each state does up to:

`O(n)`

work.

## 13.3 Cost of gcd

Each transition checks:

`gcd(num, pos)`

Using Euclid’s algorithm, GCD takes about:

`O(log n)`

in the worst case.

So the exact upper bound is:

`O(n * 2^n * log n)`

## 13.4 Common simplified complexity

Because `gcd(num, pos)` is very small and fast in practice, many explanations simplify this to:

**Time complexity: `O(n * 2^n)`**

This is the standard DP complexity if GCD is treated as constant-time.

## 13.5 Space complexity

The DP array has size:

`2^n`

So:

**Space complexity: `O(2^n)`**

No other large structure is needed.

---

# 14. Why this approach is the right fit

This problem is too constrained for a simple combinatorics formula and too structured for brute force.

A direct DFS over all permutations would take:

`O(n!)`

which becomes infeasible quickly.

The bitmask DP improves this dramatically by merging many equivalent partial constructions into the same state.

That is the key benefit of dynamic programming here:

- different orderings that lead to the same used-set do not need to be recomputed separately
- the DP reuses those subresults

---

# 15. Alternative viewpoint: matching interpretation

You can also think of this as a bipartite matching counting problem.

- left side = positions `1..n`
- right side = values `1..n`
- edge `(i, x)` exists if `gcd(i, x) == 1`

Then the problem asks for the number of **perfect matchings** in this bipartite graph.

Counting perfect matchings directly is hard in general, but for `n` small enough, bitmask DP is the standard way to do it.

This perspective explains why the DP is naturally about assigning unused values to the next position.

---
