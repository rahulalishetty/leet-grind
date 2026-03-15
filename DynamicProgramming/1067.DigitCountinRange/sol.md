# Count Digit Occurrences in a Range

## Problem

Given a single-digit integer `d` and two integers `low` and `high`, return the number of times that digit `d` occurs in all integers in the inclusive range `[low, high]`.

---

## Example

### Example 1

```text
Input:  d = 1, low = 1, high = 13
Output: 6
```

Explanation:

The digit `1` appears in:

- `1` → 1 time
- `10` → 1 time
- `11` → 2 times
- `12` → 1 time
- `13` → 1 time

Total = `6`

---

## High-Level Strategy

A brute-force solution would iterate from `low` to `high`, convert every number to a string, and count how many times digit `d` appears.

That works for small ranges, but it becomes inefficient when `high` is large.

A much better approach is:

1. Count how many times digit `d` appears in all numbers from `0` to `high`
2. Count how many times digit `d` appears in all numbers from `0` to `low - 1`
3. Subtract the two counts

So the answer becomes:

```text
count(high, d) - count(low - 1, d)
```

This reduces the range problem to solving a helper function:

```text
count(n, d) = number of times digit d appears in all integers from 0 to n
```

---

## Core Insight

Instead of examining each number individually, we count digit occurrences **position by position**:

- ones place
- tens place
- hundreds place
- thousands place
- etc.

For each position, we determine how many numbers contribute digit `d` at that position.

This works because digits repeat in predictable cycles.

---

## Positional Decomposition

For a given digit position `factor`:

- `factor = 1` for ones place
- `factor = 10` for tens place
- `factor = 100` for hundreds place
- etc.

For a number `n`, we split it into three parts relative to that position:

```text
higher = n / (factor * 10)
curr   = (n / factor) % 10
lower  = n % factor
```

### Meaning

- `higher` = digits to the left of current position
- `curr` = digit at current position
- `lower` = digits to the right of current position

### Example

Let:

```text
n = 3456
factor = 10
```

Then:

```text
higher = 34
curr   = 5
lower  = 6
```

This means we are analyzing the **tens digit**, which is `5`.

---

## Why This Decomposition Helps

Suppose we want to count how many times digit `d = 3` appears in the tens place from `0` to `3456`.

Numbers are grouped in cycles of length `100` for the tens place:

```text
00..99
100..199
200..299
...
```

Inside each full block of size `100`, the tens digit is `3` for exactly `10` numbers:

```text
30..39
130..139
230..239
...
```

That is why the contribution is based on:

- number of full cycles (`higher`)
- what the current digit is (`curr`)
- how much of the incomplete cycle is included (`lower`)

---

# Case 1: Counting a Non-Zero Digit

Let `d != 0`.

For each position:

### If `curr > d`

Then all full `higher` blocks contribute, and the current partial block also contains a full run of digit `d`.

So contribution is:

```text
(higher + 1) * factor
```

### If `curr == d`

Then full `higher` blocks contribute, and the partial block contributes only the trailing part:

```text
higher * factor + lower + 1
```

### If `curr < d`

Then only the full `higher` blocks contribute:

```text
higher * factor
```

---

## Formula Summary for `d != 0`

```text
if curr > d:
    count += (higher + 1) * factor
else if curr == d:
    count += higher * factor + lower + 1
else:
    count += higher * factor
```

---

## Example: Count digit 1 from 0 to 13

We compute `count(13, 1)`.

### Ones place (`factor = 1`)

```text
higher = 13 / 10 = 1
curr   = 13 % 10 = 3
lower  = 0
```

Since `curr = 3 > 1`:

```text
count += (higher + 1) * factor
       += (1 + 1) * 1
       += 2
```

These are:

- `1`
- `11`

So ones-place contribution = `2`

---

### Tens place (`factor = 10`)

```text
higher = 13 / 100 = 0
curr   = (13 / 10) % 10 = 1
lower  = 13 % 10 = 3
```

Since `curr == d`:

```text
count += higher * factor + lower + 1
       += 0 * 10 + 3 + 1
       += 4
```

These are:

- `10`
- `11`
- `12`
- `13`

So tens-place contribution = `4`

---

### Total

```text
2 + 4 = 6
```

Correct.

---

# Case 2: Counting Digit 0

This is the tricky part.

Why?

Because the general formula would accidentally count **leading zeros**, which are not valid digits in normal decimal representation.

For example:

- We write `7`, not `007`
- We write `42`, not `042`

So when counting zeros, we must exclude cases where zero appears only due to padding.

---

## Special Adjustment for Zero

For digit `0`, before applying the logic, we reduce `higher` by `1` to exclude the invalid leading-zero block.

Then:

### If `curr > 0`

```text
count += (higher + 1) * factor
```

### If `curr == 0`

```text
count += higher * factor + lower + 1
```

And if there is no higher part at all, we stop because further positions would only be leading zeros.

---

## Formula Summary for `d == 0`

```text
if higher == 0:
    break

higher -= 1

if curr > 0:
    count += (higher + 1) * factor
else:
    count += higher * factor + lower + 1
```

---

## Why This Fix Works

For non-zero digits, every full cycle is valid.

For zero, the very first cycle corresponds to numbers that would require leading zeros to exist at that position, so we must remove that block.

That is exactly what `higher -= 1` achieves.

---

# Full Java Solution

```java
class Solution {

    public int digitsCount(int d, int low, int high) {
        return count(high, d) - count(low - 1, d);
    }

    private int count(int n, int d) {
        if (n <= 0) return 0;

        long factor = 1;
        int count = 0;

        while (factor <= n) {
            long higher = n / (factor * 10);
            long curr = (n / factor) % 10;
            long lower = n % factor;

            if (d != 0) {
                if (curr > d) {
                    count += (higher + 1) * factor;
                } else if (curr == d) {
                    count += higher * factor + lower + 1;
                } else {
                    count += higher * factor;
                }
            } else {
                if (higher == 0) break;

                higher--;

                if (curr > 0) {
                    count += (higher + 1) * factor;
                } else {
                    count += higher * factor + lower + 1;
                }
            }

            factor *= 10;
        }

        return count;
    }
}
```

---

# Dry Run in Detail

## Example 1

```text
d = 1, low = 1, high = 13
```

We compute:

```text
count(13, 1) - count(0, 1)
```

### `count(13, 1)`

#### `factor = 1`

```text
higher = 1
curr   = 3
lower  = 0
```

`curr > 1`, so:

```text
count += (1 + 1) * 1 = 2
```

#### `factor = 10`

```text
higher = 0
curr   = 1
lower  = 3
```

`curr == 1`, so:

```text
count += 0 * 10 + 3 + 1 = 4
```

Total:

```text
2 + 4 = 6
```

### `count(0, 1) = 0`

Final answer:

```text
6 - 0 = 6
```

---

## Example 2

```text
d = 3, low = 100, high = 250
```

We compute:

```text
count(250, 3) - count(99, 3)
```

The algorithm handles this efficiently in `O(log10 n)` time.

---

# Intuition Behind the Formula

This part is worth understanding deeply.

Consider counting digit `1` in the ones place.

From `0` to `99`, the ones digit cycles like:

```text
0,1,2,3,4,5,6,7,8,9,
0,1,2,3,4,5,6,7,8,9,
...
```

Every block of `10` numbers contributes exactly one `1` in the ones place.

So if there are `higher` full blocks and a partial block, we can compute the answer directly without enumerating numbers.

Now consider the tens place.

From `0` to `99`, tens digit is:

```text
0 for 00..09
1 for 10..19
2 for 20..29
...
9 for 90..99
```

Digit `1` appears in the tens place exactly `10` times every `100` numbers.

Same idea generalizes to every position.

That is why the logic is fundamentally about:

- how many full cycles fit
- whether the current digit has passed `d`
- and how much of the current partial cycle remains

---

# Alternative Brute Force Solution

For completeness, here is the straightforward solution.

```java
class Solution {
    public int digitsCount(int d, int low, int high) {
        int ans = 0;
        char target = (char) ('0' + d);

        for (int num = low; num <= high; num++) {
            int x = num;
            if (x == 0 && d == 0) {
                ans++;
                continue;
            }

            while (x > 0) {
                if ((x % 10) == d) ans++;
                x /= 10;
            }
        }

        return ans;
    }
}
```

---

## Why Brute Force Is Inferior

This scans every number in the range, and for each number scans its digits.

### Time Complexity

If the range length is `R = high - low + 1`, then:

```text
O(R * log10(high))
```

This becomes too slow for large inputs.

### Space Complexity

```text
O(1)
```

Space is fine, but time is the problem.

---

# Optimized Approach Complexity

For the mathematical digit-position method:

## Time Complexity

We iterate once per decimal position.

So:

```text
O(log10(n))
```

where `n` is roughly `high`.

For 32-bit integers, this is at most about `10` iterations.

## Space Complexity

```text
O(1)
```

---

# Important Edge Cases

## 1. `low = 0`

If the range includes `0`, be careful about whether the problem counts digit `0` in the number `0`.

In this implementation, `count(n, d)` returns `0` when `n <= 0`, so the handling is aligned with counting numbers in the positive range formulation commonly used for this problem.

If the exact platform version includes `0` in the range and expects digit counting inside the number `0`, then a tiny adjustment may be needed. For the standard LeetCode formulation of this problem, the above logic is accepted.

## 2. `d = 0`

This is the most subtle case because of leading zeros.

Always treat it separately.

## 3. Large values of `high`

Use `long` for `factor`, `higher`, `curr`, and `lower` to avoid overflow when multiplying by `10`.

---

# Why `long` Is Used Internally

Even if inputs fit in `int`, this expression can overflow:

```java
factor * 10
```

when `factor` becomes large.

Using `long` ensures safety during intermediate arithmetic.

That is why the code uses:

```java
long factor = 1;
long higher = ...
long curr = ...
long lower = ...
```

---

# A Slightly More Commented Java Version

```java
class Solution {

    public int digitsCount(int d, int low, int high) {
        return countUpTo(high, d) - countUpTo(low - 1, d);
    }

    private int countUpTo(int n, int d) {
        if (n <= 0) return 0;

        int ans = 0;
        long factor = 1;

        while (factor <= n) {
            long higher = n / (factor * 10);
            long curr = (n / factor) % 10;
            long lower = n % factor;

            if (d != 0) {
                if (curr < d) {
                    ans += higher * factor;
                } else if (curr == d) {
                    ans += higher * factor + lower + 1;
                } else {
                    ans += (higher + 1) * factor;
                }
            } else {
                // Avoid counting leading zeros
                if (higher == 0) break;
                higher--;

                if (curr == 0) {
                    ans += higher * factor + lower + 1;
                } else {
                    ans += (higher + 1) * factor;
                }
            }

            factor *= 10;
        }

        return ans;
    }
}
```

---

# Summary of the Thought Process

To solve this efficiently:

1. Convert the range query into:

   ```text
   count(high, d) - count(low - 1, d)
   ```

2. Solve `count(n, d)` by counting contributions from each digit position

3. For each position, split the number into:
   - `higher`
   - `curr`
   - `lower`

4. Use case analysis:
   - `curr < d`
   - `curr == d`
   - `curr > d`

5. Handle `d = 0` separately to avoid counting leading zeros

---

# Final Takeaway

This is one of the most useful digit-counting patterns.

Whenever you see:

- count occurrences of a digit
- count numbers with some decimal property
- answer over a range `[low, high]`

you should immediately think:

- solve `0..n`
- then subtract prefix counts
- analyze each decimal position independently

That pattern appears often in digit DP and number theory style interview problems.

---
