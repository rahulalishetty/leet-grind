# Count Numbers With Unique Digits in a Range

## Problem

Given two positive integers `a` and `b`, count how many integers in the inclusive range `[a, b]` have **all distinct digits** in their decimal representation.

Examples:

- `10` is valid because digits `1` and `0` are different.
- `101` is invalid because digit `1` repeats.
- `9876543210` is valid because every digit appears exactly once.

---

## High-Level Strategy

The direct goal is:

> Count valid numbers in `[a, b]`.

A standard and very powerful trick for range counting problems is:

```text
count([a, b]) = count([0, b]) - count([0, a - 1])
```

So instead of solving the range directly, we define:

```text
f(x) = number of integers in [0, x] whose digits are all unique
```

Then the final answer is:

```text
answer = f(b) - f(a - 1)
```

This reduces the whole problem to computing `f(x)` efficiently.

---

## Why Brute Force Is Not Ideal

A brute-force method would do this:

1. Iterate from `a` to `b`
2. For each number, extract digits
3. Check whether any digit repeats

This works when the range is small, but becomes too slow when `b - a` is large.

For example, if the range size is millions or billions, brute force wastes a lot of time scanning each number independently.

The better idea is to count valid numbers **combinatorially while constructing them digit by digit**.

That is exactly what **Digit DP** is for.

---

## Core Intuition Behind Digit DP

When counting numbers up to some limit `x`, we do not want to test every number individually.

Instead, we build numbers from left to right and keep track of:

- which position we are filling,
- whether the prefix we built is still exactly matching `x`,
- which digits have already been used,
- whether we have actually started the number yet or are still skipping leading zeros.

This lets us count many valid numbers at once through dynamic programming.

---

## What Makes This a Digit DP Problem

Digit DP is useful whenever:

- the problem is about **numbers within a bound**
- the property depends on **digits**
- the property can be tracked incrementally while building the number

This problem fits perfectly:

- we need numbers `<= x`
- the property is “no digit repeats”
- whether a future digit is allowed depends on which digits were used earlier

---

## Defining `f(x)`

We define:

```text
f(x) = count of numbers in [0, x] with all unique digits
```

If `x < 0`, then `f(x) = 0`.

Once we can compute `f(x)`, the answer for `[a, b]` is:

```text
f(b) - f(a - 1)
```

---

## DP State Design

Suppose `x = 5274`.

We convert it to a digit array:

```text
digits = ['5', '2', '7', '4']
```

Now we process one position at a time.

We use the state:

```text
dp(pos, tight, mask, started)
```

Meaning:

- `pos`
  The current digit position we are filling.

- `tight`
  Whether the digits chosen so far are still exactly equal to the prefix of `x`.
  - `tight = 1`: we are constrained by `x` at this position
  - `tight = 0`: we are already smaller, so we can place any digit `0..9`

- `mask`
  A 10-bit bitmask representing which digits `0..9` have already been used.

  Example:
  - if digits `2` and `5` were used, then bits 2 and 5 are set

- `started`
  Whether we have placed any non-leading-zero digit yet.

  This matters because leading zeros should not count as “used digits”.

---

## Why `started` Is Necessary

This is one of the most important details.

Consider number `57`, but we are processing it in a 4-digit framework as:

```text
0057
```

Those initial zeros are not real digits of the number. They are just padding.

If we treated leading zeros as used digits, then:

- many valid numbers would be counted incorrectly
- the number `0` would become awkward to handle
- digit reuse logic would break

So:

- while `started = 0`, picking digit `0` means “skip this position”
- once we place a non-zero digit, `started` becomes `1`
- after that, every chosen digit, including `0`, is a real digit and must obey uniqueness

---

## Transition Logic

At state `dp(pos, tight, mask, started)`, we try all digits we are allowed to place.

### Determine the upper bound for this position

If `tight = 1`, we cannot exceed the corresponding digit in `x`.

So:

```text
limit = digits[pos] - '0'
```

Otherwise:

```text
limit = 9
```

Then for every digit `d` from `0` to `limit`, we consider placing it.

---

## Two Main Cases

### Case 1: We have not started yet, and we place `0`

If:

```text
started == 0 and d == 0
```

then this is still a leading zero.

So:

- `started` remains `0`
- `mask` remains unchanged
- no digit is considered “used”
- we move to the next position

This corresponds to continuing to delay the start of the number.

---

### Case 2: We place a real digit

This happens when:

- `started == 1`, or
- `started == 0` but `d != 0`

Now `d` is a genuine digit of the number.

We must check if `d` was already used.

- If yes: this choice is invalid, skip it.
- If no: mark it in the mask and continue.

That is:

```text
if (mask has digit d already) skip
else recurse with mask | (1 << d)
```

---

## How `tight` Changes

After placing digit `d`, the next state's `tight` depends on whether we were already tight and whether we matched the current limit exactly.

If we were tight and choose exactly the max allowed digit, we remain tight.

Otherwise we become loose.

Formally:

```text
nextTight = (tight == 1 && d == limit) ? 1 : 0
```

This is correct because when `tight = 1`, `limit` equals the current digit of `x`.
When `tight = 0`, `limit = 9`, and choosing any digit keeps us loose.

---

## Base Case

When:

```text
pos == digits.length
```

we have assigned all positions.

That means we formed exactly one valid number under all constraints accumulated so far, so return:

```text
1
```

This includes the all-leading-zero path, which corresponds to number `0`.

That is fine because we are computing `f(x)` over `[0, x]`.

---

## Why Counting `0` Is Okay

At first, returning `1` even when no non-zero digit was ever placed may seem suspicious.

But it is actually correct.

The path where we never start corresponds to the number `0`.

Since `f(x)` counts numbers in `[0, x]`, `0` should be included whenever `x >= 0`.

And when computing the final range:

```text
f(b) - f(a - 1)
```

everything works naturally, including for positive `a` and `b`.

---

## Memoization

Without memoization, the recursion would recompute the same subproblems repeatedly.

But the future count from a state depends only on:

- current position
- tight flag
- used-digit mask
- started flag

So we memoize:

```text
memo[pos][tight][mask][started]
```

If a state has already been solved, return the stored value immediately.

This makes the solution efficient.

---

## Full Algorithm

To compute answer for `[a, b]`:

1. Compute `countUpTo(b)`
2. Compute `countUpTo(a - 1)`
3. Return the difference

Inside `countUpTo(x)`:

1. If `x < 0`, return `0`
2. Convert `x` to a digit array
3. Initialize memo table with `-1`
4. Run DFS from state:
   - `pos = 0`
   - `tight = 1`
   - `mask = 0`
   - `started = 0`

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int countNumbersWithUniqueDigitsInRange(int a, int b) {
        return (int)(countUpTo(b) - countUpTo(a - 1L));
    }

    private long[][][][] memo;
    private char[] digits;

    private long countUpTo(long x) {
        if (x < 0) return 0;

        digits = String.valueOf(x).toCharArray();
        int n = digits.length;

        memo = new long[n][2][1 << 10][2];
        for (int i = 0; i < n; i++) {
            for (int t = 0; t < 2; t++) {
                for (int m = 0; m < (1 << 10); m++) {
                    Arrays.fill(memo[i][t][m], -1);
                }
            }
        }

        return dfs(0, 1, 0, 0);
    }

    private long dfs(int pos, int tight, int mask, int started) {
        if (pos == digits.length) {
            return 1;
        }

        if (memo[pos][tight][mask][started] != -1) {
            return memo[pos][tight][mask][started];
        }

        int limit = (tight == 1) ? (digits[pos] - '0') : 9;
        long ans = 0;

        for (int d = 0; d <= limit; d++) {
            int nextTight = (tight == 1 && d == limit) ? 1 : 0;

            if (started == 0 && d == 0) {
                ans += dfs(pos + 1, nextTight, mask, 0);
            } else {
                if ((mask & (1 << d)) != 0) continue;
                ans += dfs(pos + 1, nextTight, mask | (1 << d), 1);
            }
        }

        return memo[pos][tight][mask][started] = ans;
    }
}
```

---

## Detailed Code Walkthrough

### Method: `countNumbersWithUniqueDigitsInRange`

```java
public int countNumbersWithUniqueDigitsInRange(int a, int b) {
    return (int)(countUpTo(b) - countUpTo(a - 1L));
}
```

This directly applies the range-counting identity:

```text
[a, b] = [0, b] - [0, a - 1]
```

The `a - 1L` ensures subtraction happens in `long`, preventing issues in some edge cases.

---

### Method: `countUpTo`

```java
private long countUpTo(long x) {
    if (x < 0) return 0;
```

If the upper bound is negative, there are no valid nonnegative integers to count.

---

```java
digits = String.valueOf(x).toCharArray();
int n = digits.length;
```

We store the decimal digits of `x` so the DP can compare against them position by position.

---

```java
memo = new long[n][2][1 << 10][2];
```

Dimensions:

- `n` positions
- `2` choices for tight
- `1024` masks (`2^10`)
- `2` choices for started

---

```java
for (int i = 0; i < n; i++) {
    for (int t = 0; t < 2; t++) {
        for (int m = 0; m < (1 << 10); m++) {
            Arrays.fill(memo[i][t][m], -1);
        }
    }
}
```

Initialize all states as “not computed yet”.

---

```java
return dfs(0, 1, 0, 0);
```

Start from the first digit:

- at position `0`
- tight to `x`
- no used digits
- number not started yet

---

### Method: `dfs`

```java
private long dfs(int pos, int tight, int mask, int started)
```

Returns how many valid numbers can be formed from this state.

---

### Base case

```java
if (pos == digits.length) {
    return 1;
}
```

Once every position has been processed, we formed one valid number.

---

### Memo check

```java
if (memo[pos][tight][mask][started] != -1) {
    return memo[pos][tight][mask][started];
}
```

Avoid recomputing repeated states.

---

### Compute current limit

```java
int limit = (tight == 1) ? (digits[pos] - '0') : 9;
```

- if tight, we cannot exceed the digit of `x`
- otherwise, any digit is allowed

---

### Try every possible digit

```java
for (int d = 0; d <= limit; d++) {
```

We explore all feasible choices at this position.

---

### Update tight for next state

```java
int nextTight = (tight == 1 && d == limit) ? 1 : 0;
```

If we were constrained and choose the max allowed digit, we remain constrained.
Otherwise, we become free.

A slightly more explicit equivalent version is:

```java
int nextTight = (tight == 1 && d == digits[pos] - '0') ? 1 : 0;
```

The code uses `limit`, which is compact and still correct.

---

### Leading-zero branch

```java
if (started == 0 && d == 0) {
    ans += dfs(pos + 1, nextTight, mask, 0);
}
```

Still not starting the real number.

No digit gets marked as used.

---

### Real-digit branch

```java
else {
    if ((mask & (1 << d)) != 0) continue;
    ans += dfs(pos + 1, nextTight, mask | (1 << d), 1);
}
```

If `d` was already used, skip it.

Otherwise:

- mark digit `d` in the bitmask
- mark `started = 1`
- recurse

---

### Store and return result

```java
return memo[pos][tight][mask][started] = ans;
```

Save the computed answer for reuse.

---

## Dry Run Example

Let us walk through a smaller example:

```text
a = 10, b = 25
```

We want the count of valid numbers in `[10, 25]`.

This is:

```text
f(25) - f(9)
```

---

### Step 1: `f(25)`

Numbers from `0` to `25` with unique digits are:

```text
0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
10, 12, 13, 14, 15, 16, 17, 18, 19,
20, 21, 23, 24, 25
```

Invalid ones include:

```text
11, 22
```

So:

```text
f(25) = 24
```

---

### Step 2: `f(9)`

All numbers `0` through `9` have unique digits.

So:

```text
f(9) = 10
```

---

### Final answer

```text
24 - 10 = 14
```

Which matches the valid numbers in `[10, 25]`:

```text
10, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24, 25
```

---

## Bitmask Intuition

The mask is a compact way to remember which digits have been used.

Example:

- digit `3` corresponds to bit `3`
- digit `7` corresponds to bit `7`

If digits `3` and `7` have been used:

```text
mask = (1 << 3) | (1 << 7)
```

To check whether digit `d` is already used:

```java
(mask & (1 << d)) != 0
```

To mark digit `d` as used:

```java
mask | (1 << d)
```

This is much faster and cleaner than using a boolean array inside recursion.

---

## Why This Is Correct

The correctness comes from these facts:

### 1. Every number in `[0, x]` corresponds to exactly one DP path

We process one digit at a time.
At each position, the chosen digit uniquely determines the path.

So every number is represented once.

---

### 2. Tight ensures we never exceed `x`

When the built prefix exactly matches `x`, the current digit is restricted.
Once we become smaller, all future digits are free.

So the DP explores exactly the valid numbers `<= x`.

---

### 3. Mask ensures no digit repeats

Before placing a real digit, we check whether it is already in the mask.
If it is, that path is discarded.

So every counted number has unique digits.

---

### 4. Started handles leading zeros correctly

Leading zeros do not consume digits and do not pollute the mask.
This ensures numbers with fewer digits are represented properly.

---

## Time Complexity

Let `n` be the number of digits in `x`.

The DP state space is:

- `n` positions
- `2` values for `tight`
- `2^10 = 1024` masks
- `2` values for `started`

So the number of states is:

```text
O(n * 2 * 1024 * 2) = O(n * 1024)
```

From each state, we try at most 10 digits.

Therefore the total time complexity is:

```text
O(n * 1024 * 10)
```

Since `1024` and `10` are constants, this is effectively:

```text
O(n)
```

with a moderate constant factor.

For 32-bit or 64-bit integers, `n` is at most around 10 or 19, so this is extremely fast.

---

## Space Complexity

The memo table stores:

```text
O(n * 2 * 1024 * 2) = O(n * 1024)
```

states.

So the space complexity is:

```text
O(n * 1024)
```

Again, this is very manageable.

---

## Practical Notes

### Why use `long` in the DP?

The count of valid numbers can exceed `int` during intermediate computation, especially if bounds are large.

Using `long` for memo values and DFS return values is safer.

The final result is cast to `int` in the provided solution. That is acceptable only if the problem guarantees the answer fits in `int`.

If not guaranteed, the return type should also be `long`.

---

### Edge Case: Single number range

If `a == b`, the formula still works:

```text
f(b) - f(a - 1)
```

It will return `1` if that number has unique digits, else `0`.

---

### Edge Case: Numbers containing zero

Zero is treated normally once the number has started.

For example:

- `10` is valid
- `101` is invalid
- `1002` is invalid

No special handling is needed beyond `started`.

---

### Edge Case: More than 10 digits

A decimal number with more than 10 real digits cannot have all unique digits, because there are only 10 distinct digits available.

The DP naturally handles this without any special case.

---

## Brute Force Comparison

### Brute force

For each number in `[a, b]`:

- extract digits
- test uniqueness

Complexity:

```text
O((b - a + 1) * digits_per_number)
```

This is fine only for small ranges.

---

### Digit DP

Counts all valid numbers up to a bound using shared state and memoization.

Complexity:

```text
O(number_of_digits * 1024 * 10)
```

This is dramatically better for large ranges.

---

## Final Takeaway

The main insight is not to count numbers directly in `[a, b]`, but instead:

1. turn the range problem into two prefix problems,
2. solve each prefix problem with digit DP,
3. track used digits with a bitmask,
4. use `tight` to stay under the bound,
5. use `started` to handle leading zeros cleanly.

This is the standard, scalable way to solve digit-constraint counting problems such as:

- no repeated digits
- digit sum constraints
- forbidden digits
- monotonic digit rules
- exact number of occurrences of some digit
