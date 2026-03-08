# Number of Subsequences With Odd Sum

## Problem

Given an array `nums`, return the number of subsequences whose sum is odd.

Since the answer can be very large, return it modulo `10^9 + 7`.

---

## Core Observation

A subsequence has an **odd sum** if and only if it contains an **odd number of odd elements**.

That is the entire problem in one sentence.

Why?

- Adding an **even** number does **not** change parity.
- Adding an **odd** number **flips** parity.
- So the total sum is odd exactly when the count of chosen odd elements is odd.

This converts the problem from “count subsequences with odd sum” into:

> Count how many subsequences contain an odd number of odd elements.

---

## Step 1: Separate the array by parity

Let:

- `E` = number of even elements
- `O` = number of odd elements
- `n = E + O`

Now think about constructing a subsequence:

- From the `E` even elements, we may choose **any subset**
- From the `O` odd elements, we must choose a subset of **odd size**

So the answer is:

```text
(number of ways to choose an odd-sized subset from the odd elements)
×
(number of ways to choose any subset from the even elements)
```

---

## Step 2: Count choices from even elements

Every even element can either be:

- included, or
- excluded

independently.

So the number of ways to choose from the even elements is:

```text
2^E
```

---

## Step 3: Count odd-sized subsets from odd elements

Now the subtle part:

How many subsets of `O` odd elements have odd size?

The total number of subsets of `O` elements is:

```text
2^O
```

Among these, exactly half have even size and half have odd size, as long as `O > 0`.

So the number of odd-sized subsets is:

```text
2^(O - 1)
```

Therefore the total answer becomes:

```text
2^E × 2^(O - 1) = 2^(E + O - 1) = 2^(n - 1)
```

So:

- if there is **at least one odd element**, answer = `2^(n - 1)`
- if there are **no odd elements**, answer = `0`

because then every subsequence sum is even.

---

## Final Formula

```text
If the array contains no odd element:
    answer = 0

Otherwise:
    answer = 2^(n - 1) mod (10^9 + 7)
```

---

## Deep Intuition: Why Exactly Half?

This is the most important idea in the whole solution.

Assume the array contains at least one odd element.

Pick one odd element and call it the **special odd**.

Now consider any subsequence formed from the remaining `n - 1` elements.

For each such subsequence, there are exactly two related possibilities:

1. the subsequence itself
2. the same subsequence plus the special odd element

Adding one odd element flips parity:

- even sum becomes odd
- odd sum becomes even

So these two subsequences form a pair:

- one has even sum
- one has odd sum

This means all subsequences can be partitioned into pairs, and in every pair exactly one subsequence has odd sum.

Since the total number of subsequences is:

```text
2^n
```

the number with odd sum is:

```text
2^n / 2 = 2^(n - 1)
```

This argument works **only if at least one odd element exists**.

If no odd exists, there is no parity-flipping element, and every subsequence sum stays even.

---

## Example 1

```text
nums = [1, 2, 3]
```

- odd elements: `1, 3` → `O = 2`
- even elements: `2` → `E = 1`
- `n = 3`

Since there is at least one odd:

```text
answer = 2^(3 - 1) = 4
```

Let us verify.

All subsequences:

- `[]` → 0 even
- `[1]` → 1 odd
- `[2]` → 2 even
- `[3]` → 3 odd
- `[1,2]` → 3 odd
- `[1,3]` → 4 even
- `[2,3]` → 5 odd
- `[1,2,3]` → 6 even

Odd-sum subsequences:

- `[1]`
- `[3]`
- `[1,2]`
- `[2,3]`

Total = `4`

Correct.

---

## Example 2

```text
nums = [2, 4, 6]
```

All elements are even.

Every subsequence sum is even.

So the answer is:

```text
0
```

---

## Example 3

```text
nums = [5]
```

There is one odd element.

```text
answer = 2^(1 - 1) = 1
```

The only non-empty odd-sum subsequence is:

- `[5]`

Correct.

---

## Why We Do Not Need DP Here

A standard DP approach would track:

- number of subsequences with even sum so far
- number of subsequences with odd sum so far

That works, but it is unnecessary because the parity argument collapses the whole problem into a simple formula.

This is a good example of a problem that **looks like DP** but is actually solved more cleanly with a combinatorial invariant.

Whenever parity is involved, it is worth asking:

- what actually changes parity?
- can I count by structure instead of by state transitions?

Here, that line of thinking gives a dramatically simpler solution.

---

## Implementation Strategy

We only need to do two things:

1. scan the array and check whether at least one odd element exists
2. if yes, compute `2^(n - 1) mod MOD`

For the exponentiation, use **binary exponentiation** (`modPow`) rather than multiplying by 2 repeatedly.

Binary exponentiation is the right habit because:

- it is asymptotically faster
- it is standard for modular power
- it stays efficient even when exponents get large

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;

    public int subsequenceCount(int[] nums) {
        boolean hasOdd = false;

        for (int x : nums) {
            if ((x & 1) == 1) {
                hasOdd = true;
                break;
            }
        }

        if (!hasOdd) {
            return 0;
        }

        return (int) modPow(2, nums.length - 1, MOD);
    }

    private long modPow(long base, int exp, int mod) {
        long result = 1;
        base %= mod;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exp >>= 1;
        }

        return result;
    }
}
```

---

## Code Walkthrough

### 1. `hasOdd` check

```java
boolean hasOdd = false;

for (int x : nums) {
    if ((x & 1) == 1) {
        hasOdd = true;
        break;
    }
}
```

We scan the array once and stop as soon as we find an odd number.

Why use:

```java
(x & 1) == 1
```

Because the least significant bit tells us parity:

- even → last bit is `0`
- odd → last bit is `1`

This is a standard, efficient parity test.

### 2. Handle the all-even case

```java
if (!hasOdd) {
    return 0;
}
```

If no odd element exists, no odd-sum subsequence can exist.

### 3. Compute `2^(n-1) mod MOD`

```java
return (int) modPow(2, nums.length - 1, MOD);
```

This directly applies the formula proved above.

### 4. Binary exponentiation

```java
private long modPow(long base, int exp, int mod) {
    long result = 1;
    base %= mod;

    while (exp > 0) {
        if ((exp & 1) == 1) {
            result = (result * base) % mod;
        }
        base = (base * base) % mod;
        exp >>= 1;
    }

    return result;
}
```

This computes powers in `O(log exp)` time.

Idea:

- if the current exponent bit is `1`, multiply the answer by the current base
- square the base each step
- shift exponent right by one bit

This is the standard repeated squaring method.

---

## Correctness Argument

We can summarize the proof cleanly.

### Case 1: No odd element exists

Then every chosen element is even, and the sum of any subsequence is even.

So the number of odd-sum subsequences is `0`.

### Case 2: At least one odd element exists

Choose one odd element `x`.

For every subsequence `S` that does not force a choice about `x`, pair:

- `S`
- `S ∪ {x}`

Since `x` is odd, these two subsequences have opposite parity.

Therefore, in each pair exactly one subsequence has odd sum.

Since all subsequences are partitioned into such pairs, exactly half of all subsequences have odd sum.

The total number of subsequences is `2^n`, so the number of odd-sum subsequences is `2^(n - 1)`.

Thus the algorithm is correct.

---

## Time Complexity

Let `n = nums.length`.

### Array scan

We scan the array once:

```text
O(n)
```

### Modular exponentiation

We compute `2^(n - 1)` using binary exponentiation:

```text
O(log n)
```

### Total

```text
O(n + log n)
```

Since `O(n)` dominates:

```text
O(n)
```

---

## Space Complexity

We use only a few variables:

- `hasOdd`
- loop variable
- variables inside `modPow`

So the auxiliary space is:

```text
O(1)
```

---

## Why This Solution Is Elegant

This problem is a strong reminder of a useful principle:

> When the property being counted is based only on parity, try to reduce the problem to parity structure instead of enumerating subsequences.

A brute-force solution would be exponential.
A DP solution would be linear.
But the parity symmetry lets us go one step further and derive a closed-form count.

That is the real insight.

---
