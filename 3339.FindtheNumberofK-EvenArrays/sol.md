# K-Even Arrays — Detailed Solution Summary

## Problem Restatement

We are given three integers:

- `n` = length of the array
- `m` = each array element must lie in the range `[1, m]`
- `k` = required number of indices `i` such that

```text
(arr[i] * arr[i + 1]) - arr[i] - arr[i + 1]
```

is even, for `0 <= i < n - 1`.

We must count how many arrays of length `n` satisfy this condition **for exactly `k` adjacent indices**, and return the answer modulo:

```text
10^9 + 7
```

---

## The Real Core of the Problem

At first glance, the expression

```text
(arr[i] * arr[i + 1]) - arr[i] - arr[i + 1]
```

looks arithmetic-heavy.

But the question only asks whether it is **even or odd**.

That means we should immediately think in terms of **parity** rather than exact values.

This is the key simplification.

---

## Step 1: Reduce the Expression by Parity

Let:

- `a = arr[i]`
- `b = arr[i + 1]`

We want to know when:

```text
a * b - a - b
```

is even.

When checking evenness, only parity matters, so work modulo 2.

In modulo 2 arithmetic:

- subtraction is the same as addition, because `-1 ≡ 1 (mod 2)`

So:

```text
a*b - a - b ≡ a*b + a + b (mod 2)
```

Now test all four parity cases.

### Case 1: `a` even, `b` even

```text
0*0 + 0 + 0 = 0
```

Even.

### Case 2: `a` even, `b` odd

```text
0*1 + 0 + 1 = 1
```

Odd.

### Case 3: `a` odd, `b` even

```text
1*0 + 1 + 0 = 1
```

Odd.

### Case 4: `a` odd, `b` odd

```text
1*1 + 1 + 1 = 3 ≡ 1 (mod 2)
```

Odd.

### Conclusion

The expression is even **if and only if both adjacent numbers are even**.

So the entire problem becomes:

> Count arrays of length `n` with values in `[1, m]` such that **exactly `k` adjacent pairs are both even**.

This is the decisive transformation.

---

## Step 2: Collapse Values Into Two Categories

The exact values no longer matter individually. Only whether each chosen number is:

- even
- odd

matters.

Let:

- `E` = number of even integers in `[1, m]`
- `O` = number of odd integers in `[1, m]`

Then:

```text
E = floor(m / 2)
O = m - E
```

Why?

- Every second number is even
- The rest are odd

So:

- choosing an even at one position gives `E` possible values
- choosing an odd at one position gives `O` possible values

Now the problem is fundamentally about counting **parity sequences**, weighted by how many actual values each parity can represent.

---

## Reframed Problem

We want to build an array of length `n` where each position is conceptually either:

- `E` (even)
- `O` (odd)

and exactly `k` adjacent pairs are `E-E`.

Then, for each such parity pattern, multiply by:

- `E` for every even position
- `O` for every odd position

The dynamic programming will do that weighting automatically.

---

## Why Dynamic Programming Fits Naturally

When we build the array left to right, whether adding the next number creates a new valid counted index depends only on:

1. the parity of the previous element
2. how many counted adjacent pairs we have formed so far

That is a classic DP signature:

- small state
- local transition
- cumulative count

We do **not** need the full array history.

We only need:

- how many good adjacent pairs have been created so far
- whether the last element is even or odd

That is enough to correctly decide the effect of the next appended value.

---

## DP State Design

Let:

- `dpE[j]` = number of arrays built so far that:
  - end in an **even** number
  - have exactly `j` counted indices so far

- `dpO[j]` = number of arrays built so far that:
  - end in an **odd** number
  - have exactly `j` counted indices so far

Here, a “counted index” means an adjacent pair `(i, i+1)` that is `even-even`.

So `j` ranges from `0` to `k`.

---

## Why These States Are Sufficient

Suppose we already built some prefix.

When we append a new number, the only newly formed adjacent pair is:

```text
(previous element, new element)
```

Whether this pair contributes to the count depends only on whether both are even.

So to decide the next state, we only need to know:

- was the previous element even or odd?
- how many valid even-even pairs have already been formed?

That is exactly what `dpE[j]` and `dpO[j]` store.

This is why the DP is minimal and sufficient.

---

## Base Case: Arrays of Length 1

For an array of length 1, there are no adjacent pairs yet.

So the number of counted indices must be `0`.

Now:

- If the single element is even, there are `E` choices
- If it is odd, there are `O` choices

So:

```text
dpE[0] = E
dpO[0] = O
```

and all other entries are zero.

---

## Transition Logic

Now suppose we are extending arrays of current length `len - 1` to arrays of length `len`.

We append one more value.

There are two possibilities:

- append an even value
- append an odd value

Let the new arrays be stored in:

- `newE[j]`
- `newO[j]`

---

## Transition 1: Append an Even Number

If we append an even number, the new array ends in even.

How does the count of valid indices change?

### Case A: Previous array ended in odd

Then the new adjacent pair is:

```text
odd-even
```

This is **not** even-even, so the count `j` does not change.

Contribution:

```text
dpO[j] * E
```

because for each such previous array, we have `E` choices for the new even value.

### Case B: Previous array ended in even

Then the new adjacent pair is:

```text
even-even
```

This **does** create one new counted index.

So if the new array has `j` counted indices, the old one must have had `j - 1`.

Contribution:

```text
dpE[j - 1] * E
```

### Combine both cases

```text
newE[j] = E * (dpO[j] + dpE[j - 1])
```

with the understanding that `dpE[-1]` is invalid and treated as zero.

---

## Transition 2: Append an Odd Number

If we append an odd number, the new array ends in odd.

Now the new adjacent pair is:

- even-odd, or
- odd-odd

Neither is even-even.

So no new counted index is created.

That means if the new array has `j` counted indices, the previous array must also have `j`.

Both kinds of previous arrays contribute:

- previous ended in even
- previous ended in odd

And there are `O` choices for the appended odd value.

So:

```text
newO[j] = O * (dpE[j] + dpO[j])
```

---

## Full Recurrence

For every `j` from `0` to `k`:

```text
newE[j] = E * (dpO[j] + (j > 0 ? dpE[j - 1] : 0))
newO[j] = O * (dpE[j] + dpO[j])
```

All operations are taken modulo `10^9 + 7`.

---

## Final Answer

After building arrays of length `n`, the valid arrays with exactly `k` counted indices may end in either:

- even
- odd

So the final answer is:

```text
dpE[k] + dpO[k]
```

modulo `10^9 + 7`.

---

## Full Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;

    public int countKEvenArrays(int n, int m, int k) {
        if (k > n - 1) return 0;

        long E = m / 2;      // number of even values
        long O = m - E;      // number of odd values

        long[] dpE = new long[k + 1];
        long[] dpO = new long[k + 1];

        // Base case: arrays of length 1
        dpE[0] = E % MOD;
        dpO[0] = O % MOD;

        for (int len = 2; len <= n; len++) {
            long[] newE = new long[k + 1];
            long[] newO = new long[k + 1];

            for (int j = 0; j <= k; j++) {
                // Append even
                long waysEndEven = dpO[j];
                if (j > 0) {
                    waysEndEven = (waysEndEven + dpE[j - 1]) % MOD;
                }
                newE[j] = (waysEndEven * E) % MOD;

                // Append odd
                long waysEndOdd = (dpO[j] + dpE[j]) % MOD;
                newO[j] = (waysEndOdd * O) % MOD;
            }

            dpE = newE;
            dpO = newO;
        }

        return (int) ((dpE[k] + dpO[k]) % MOD);
    }
}
```

---

## Detailed Code Walkthrough

## 1. Handle impossible `k`

```java
if (k > n - 1) return 0;
```

An array of length `n` has exactly `n - 1` adjacent pairs.

So it is impossible to have more than `n - 1` counted indices.

This guard is correct and important.

---

## 2. Count even and odd available values

```java
long E = m / 2;
long O = m - E;
```

This compresses the range `[1, m]` into the only two categories that matter.

For example:

- if `m = 5`, then `E = 2`, `O = 3`
- if `m = 6`, then `E = 3`, `O = 3`

---

## 3. Initialize DP arrays

```java
long[] dpE = new long[k + 1];
long[] dpO = new long[k + 1];
```

We only care about counts from `0` to `k`, so arrays of size `k + 1` are enough.

---

## 4. Base case for length 1

```java
dpE[0] = E % MOD;
dpO[0] = O % MOD;
```

A single-element array has no adjacent pairs, so the count must be zero.

- `E` ways to choose an even single element
- `O` ways to choose an odd single element

---

## 5. Iterate over lengths from 2 to `n`

```java
for (int len = 2; len <= n; len++) {
```

At each iteration, we append one more element.

The DP moves from arrays of length `len - 1` to arrays of length `len`.

---

## 6. Build fresh next-state arrays

```java
long[] newE = new long[k + 1];
long[] newO = new long[k + 1];
```

This avoids corrupting current states while computing transitions.

---

## 7. Compute `newE[j]`

```java
long waysEndEven = dpO[j];
if (j > 0) {
    waysEndEven = (waysEndEven + dpE[j - 1]) % MOD;
}
newE[j] = (waysEndEven * E) % MOD;
```

Interpretation:

- `dpO[j]`:
  append even after odd, so no new even-even pair
- `dpE[j - 1]`:
  append even after even, creating one new pair

Then multiply by `E` because there are `E` possible even values.

This exactly matches the recurrence.

---

## 8. Compute `newO[j]`

```java
long waysEndOdd = (dpO[j] + dpE[j]) % MOD;
newO[j] = (waysEndOdd * O) % MOD;
```

Interpretation:

- append odd after odd
- append odd after even

Neither creates an even-even pair.

So the count `j` is preserved, and then multiply by `O` for the number of odd choices.

---

## 9. Move to the next layer

```java
dpE = newE;
dpO = newO;
```

After processing one more position, the new arrays become the current DP state.

---

## 10. Return arrays of length `n` with exactly `k` valid indices

```java
return (int) ((dpE[k] + dpO[k]) % MOD);
```

The final array can end in either parity.

So sum both possibilities.

---

## Worked Example

Consider:

```text
n = 3, m = 5, k = 1
```

Then:

- even numbers in `[1,5]` are `{2,4}` → `E = 2`
- odd numbers in `[1,5]` are `{1,3,5}` → `O = 3`

We want arrays of length 3 with exactly one adjacent even-even pair.

### Valid parity patterns

Since length is 3, there are 2 adjacent pairs.

We want exactly one of them to be `E-E`.

The possible parity patterns are:

1. `E E O`
2. `O E E`

Now count actual arrays.

### Pattern `E E O`

- first position: 2 choices
- second position: 2 choices
- third position: 3 choices

Total:

```text
2 * 2 * 3 = 12
```

### Pattern `O E E`

- first position: 3 choices
- second position: 2 choices
- third position: 2 choices

Total:

```text
3 * 2 * 2 = 12
```

### Final total

```text
12 + 12 = 24
```

So the answer is:

```text
24
```

The DP produces this same result.

---

## Another Small Sanity Check

Suppose:

```text
n = 2
```

Then there is only one adjacent pair.

So:

- `k = 0`: count all arrays except even-even
- `k = 1`: count exactly even-even arrays

Number of even-even arrays:

```text
E * E
```

Number of all arrays:

```text
m * m
```

So for `k = 0`:

```text
m^2 - E^2
```

for `k = 1`:

```text
E^2
```

The DP agrees with this.

That is a useful correctness check.

---

## Formal Correctness Intuition

We can state an invariant.

### Invariant

After processing arrays of length `len`:

- `dpE[j]` equals the number of length-`len` arrays ending in even with exactly `j` even-even adjacent pairs
- `dpO[j]` equals the number of length-`len` arrays ending in odd with exactly `j` even-even adjacent pairs

### Base case

For `len = 1`:

- there are no adjacent pairs
- `dpE[0] = E`
- `dpO[0] = O`

So the invariant holds.

### Inductive step

Assume the invariant holds for length `len - 1`.

To build a length-`len` array:

- append even:
  - after odd: no new even-even pair
  - after even: exactly one new even-even pair
- append odd:
  - never creates even-even

These are the only possibilities.

So the recurrence counts all valid arrays exactly once and places them in the correct state.

Thus the invariant holds for all lengths.

Finally, summing `dpE[k] + dpO[k]` gives all length-`n` arrays with exactly `k` valid indices.

So the algorithm is correct.

---

## Why Brute Force Is Bad

A brute-force solution would try all arrays of length `n`:

```text
m^n
```

This explodes immediately for even moderate values of `n` and `m`.

For each array, checking all adjacent pairs would add another factor of `n`.

So brute force is completely impractical.

The DP avoids enumerating actual arrays and instead aggregates them by parity and count state.

That is the main optimization.

---

## Why This DP Is Efficient

The algorithm only tracks:

- two parity-ending states
- for each possible count `0..k`

So for each of the `n` positions, it performs only `O(k)` work.

That is a major reduction from exponential brute force.

---

## Time Complexity

For each length from `2` to `n`, we iterate through all `j` from `0` to `k`.

Each transition is constant time.

So:

```text
Time Complexity = O(n * k)
```

---

## Space Complexity

We store only:

- `dpE`
- `dpO`
- `newE`
- `newO`

Each array has size `k + 1`.

So the total auxiliary space is:

```text
Space Complexity = O(k)
```

because we only keep the previous and current DP layers, not all `n` layers.

---

## Main Takeaways

This problem becomes simple once you refuse to get distracted by the original algebraic expression and instead ask:

> Does only parity matter?

The answer is yes.

Then the problem reduces to:

> Count arrays with exactly `k` adjacent even-even pairs.

From there, the clean DP state is:

- how many such pairs so far
- whether the current last number is even or odd

That yields a compact and efficient solution:

- **Time:** `O(nk)`
- **Space:** `O(k)`

The decisive ideas are:

1. reduce the expression modulo 2
2. observe that only `even-even` contributes
3. count values by parity (`E`, `O`)
4. build a DP over length, pair-count, and last parity

This is the full logic behind the solution.
