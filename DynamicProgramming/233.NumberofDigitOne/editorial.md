# 233. Number of Digit One — Exhaustive Solution Notes

## Overview

This problem asks us to count how many times the digit `1` appears in **all non-negative integers from `0` to `n`**.

At first glance, the problem seems straightforward:

- iterate through every number from `1` to `n`,
- convert each number into a string,
- count how many `'1'` characters appear,
- add them all up.

That approach works conceptually, but it becomes too slow for large values of `n`, especially since:

```text
0 <= n <= 10^9
```

So the real challenge is not correctness, but efficiency.

This write-up explains two approaches in detail:

1. **Brute Force** — simple but too slow
2. **Mathematical Counting by Digit Position** — efficient and accepted

---

## Problem Statement

Given an integer `n`, count the total number of digit `1` appearing in all non-negative integers less than or equal to `n`.

---

## Example 1

**Input**

```text
n = 13
```

**Output**

```text
6
```

**Explanation**

The numbers from `0` to `13` are:

```text
0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
```

The digit `1` appears in:

- `1` → one `1`
- `10` → one `1`
- `11` → two `1`s
- `12` → one `1`
- `13` → one `1`

Total:

```text
1 + 1 + 2 + 1 + 1 = 6
```

---

## Example 2

**Input**

```text
n = 0
```

**Output**

```text
0
```

**Explanation**

There are no numbers containing digit `1` in the range from `0` to `0`.

---

## Constraints

- `0 <= n <= 10^9`

---

# Approach 1: Brute Force

## Intuition

This is the most direct interpretation of the problem.

We simply do exactly what the problem says:

- look at every integer from `1` to `n`,
- count how many times digit `1` occurs in that number,
- sum those counts.

This is easy to understand and easy to implement.

However, it is too slow for large values of `n`.

---

## Algorithm

For each integer `i` from `1` to `n`:

1. Convert `i` to a string
2. Count how many characters are equal to `'1'`
3. Add that count to the answer

---

## C++ Implementation — Brute Force

```cpp
int countDigitOne(int n)
{
    int countr = 0;
    for (int i = 1; i <= n; i++) {
        string str = to_string(i);
        countr += count(str.begin(), str.end(), '1');
    }
    return countr;
}
```

---

## Why This Approach Times Out

Suppose `n = 10^9`.

Then we are iterating through **one billion numbers**.

For each number, we convert it to a string and inspect all its digits.

That is far too much work.

Even though each number only has at most around `10` digits, the total work becomes enormous.

---

## Complexity Analysis — Brute Force

### Time Complexity

We iterate over all numbers from `1` to `n`, so there are:

```text
O(n)
```

iterations.

For each number, converting to string and counting digits takes time proportional to the number of digits, which is:

```text
O(log10(n))
```

So the total time complexity is:

```text
O(n × log10(n))
```

---

### Space Complexity

The extra space comes mainly from storing the string representation of a number.

That string has length proportional to the number of digits in the number:

```text
O(log10(n))
```

So the space complexity is:

```text
O(log10(n))
```

---

# Approach 2: Solve It Mathematically

## Intuition

The brute-force solution counts digit `1` manually across every number.

That is wasteful.

Instead, we should ask:

> Can we count how many `1`s appear in each digit position directly?

That is the key idea.

Rather than processing every number individually, we analyze how often digit `1` appears in:

- the ones place,
- the tens place,
- the hundreds place,
- the thousands place,
- and so on.

Once we can count the contribution of each place independently, we can sum them to get the final answer.

---

# Understanding the Pattern by Position

Let us first focus on a single digit position.

Suppose we want to count how many numbers from `0` to `n` have digit `1` in the:

- ones place,
- tens place,
- hundreds place,
- etc.

We process one place value at a time.

Let:

```text
i = 1      → ones place
i = 10     → tens place
i = 100    → hundreds place
i = 1000   → thousands place
...
```

For each `i`, we count how many numbers contribute a `1` in that digit position.

---

## Counting `1`s in the Ones Place

Let us look at the ones place first.

The pattern of the ones digit repeats every `10` numbers:

```text
0, 1, 2, 3, 4, 5, 6, 7, 8, 9
```

Within every block of `10` numbers, exactly **one** number has a `1` in the ones place.

Examples:

- `1`
- `11`
- `21`
- `31`
- `41`
- ...

So for every complete group of `10`, we get `1` occurrence of digit `1` in the ones place.

---

## Counting `1`s in the Tens Place

Now consider the tens place.

The pattern repeats every `100` numbers:

```text
00 to 99
```

Within each block of `100`, the tens digit is `1` for:

```text
10 to 19
```

That is exactly `10` numbers.

So for every complete group of `100`, we get `10` occurrences of digit `1` in the tens place.

---

## Counting `1`s in the Hundreds Place

Now consider the hundreds place.

The pattern repeats every `1000` numbers.

Within each block of `1000`, the hundreds digit is `1` for:

```text
100 to 199
```

That is `100` numbers.

So for every complete group of `1000`, we get `100` occurrences of digit `1` in the hundreds place.

---

## General Pattern

For a digit position `i`:

- the pattern repeats every `i * 10`,
- within each full block, digit `1` appears exactly `i` times at that position.

So the contribution from complete blocks is:

```text
(n / (i * 10)) * i
```

This counts all the full cycles.

But there may also be a **partial leftover block** at the end.

That leftover part must be handled separately.

---

# Handling the Partial Block

After counting complete cycles, we still need to account for the remainder:

```text
n % (i * 10)
```

This leftover section may contribute additional `1`s.

We compute this extra contribution using:

```text
min(max((n % (i * 10)) - i + 1, 0), i)
```

This is the most subtle part of the solution, so let us break it down carefully.

---

## Why This Formula Works

Let:

```text
divider = i * 10
remainder = n % divider
```

Then within the leftover portion:

- if `remainder < i - 1`, there are no additional `1`s for this position
- if `remainder` is between `i - 1` and `2*i - 2`, we get a partial contribution
- if `remainder >= 2*i - 1`, then all `i` numbers of that block are included

This behavior is captured by:

```text
max(remainder - i + 1, 0)
```

which starts counting only when the leftover block reaches the `1` segment,

and then:

```text
min(..., i)
```

ensures we do not count more than `i` such numbers.

So the total contribution for digit position `i` becomes:

```text
(n / (i * 10)) * i + min(max(n % (i * 10) - i + 1, 0), i)
```

---

# Full Formula

For each digit position `i`:

```text
count at position i =
    (n / (i * 10)) * i
    + min(max(n % (i * 10) - i + 1, 0), i)
```

We sum this over:

```text
i = 1, 10, 100, 1000, ...
```

until `i > n`.

---

# Detailed Example: n = 1234

Let us compute the answer step by step.

---

## 1) Ones Place (`i = 1`)

Complete cycles:

```text
(1234 / 10) * 1 = 123
```

Remainder part:

```text
1234 % 10 = 4
min(max(4 - 1 + 1, 0), 1) = min(4, 1) = 1
```

Total for ones place:

```text
123 + 1 = 124
```

---

## 2) Tens Place (`i = 10`)

Complete cycles:

```text
(1234 / 100) * 10 = 12 * 10 = 120
```

Remainder part:

```text
1234 % 100 = 34
min(max(34 - 10 + 1, 0), 10)
= min(25, 10)
= 10
```

Total for tens place:

```text
120 + 10 = 130
```

---

## 3) Hundreds Place (`i = 100`)

Complete cycles:

```text
(1234 / 1000) * 100 = 1 * 100 = 100
```

Remainder part:

```text
1234 % 1000 = 234
min(max(234 - 100 + 1, 0), 100)
= min(135, 100)
= 100
```

Total for hundreds place:

```text
100 + 100 = 200
```

---

## 4) Thousands Place (`i = 1000`)

Complete cycles:

```text
(1234 / 10000) * 1000 = 0
```

Remainder part:

```text
1234 % 10000 = 1234
min(max(1234 - 1000 + 1, 0), 1000)
= min(235, 1000)
= 235
```

Total for thousands place:

```text
0 + 235 = 235
```

---

## Final Total

Add all contributions:

```text
124 + 130 + 200 + 235 = 689
```

So the number of digit `1`s from `0` to `1234` is:

```text
689
```

---

# Algorithm

1. Initialize `countr = 0`
2. Iterate through digit places:
   - `i = 1`
   - `i *= 10` each step
3. For each position:
   - compute the full-cycle contribution
   - compute the leftover contribution
   - add both to `countr`
4. Return `countr`

---

## C++ Implementation — Mathematical Counting

```cpp
int countDigitOne(int n)
{
    int countr = 0;
    for (long long i = 1; i <= n; i *= 10) {
        long long divider = i * 10;
        countr += (n / divider) * i + min(max(n % divider - i + 1, 0LL), i);
    }
    return countr;
}
```

---

# Why `long long` Is Used

Even though `n <= 10^9`, the expression:

```text
i * 10
```

can overflow a 32-bit integer when `i` becomes large.

For example, near the highest power of ten, multiplying by `10` may exceed the integer range.

So `long long` is used to keep the arithmetic safe.

---

# Complexity Analysis — Mathematical Approach

### Time Complexity

The loop runs once for each digit position in `n`.

The number of digit positions is:

```text
O(log10(n))
```

So the total time complexity is:

```text
O(log10(n))
```

---

### Space Complexity

We use only a constant number of variables.

So the space complexity is:

```text
O(1)
```

---

# Why This Approach Is So Much Faster

The brute force method looks at every number individually.

The mathematical method does not.

Instead, it counts how many times digit `1` appears in each digit position by recognizing a repeating pattern.

So instead of billions of operations, it performs only about:

- 1 iteration for each digit place,
- which is at most about `10` iterations when `n <= 10^9`.

That is a dramatic improvement.

---

# Common Mistakes

## 1. Off-by-One Errors in the Remainder Formula

The expression:

```text
n % divider - i + 1
```

is easy to get wrong.

That `+1` is necessary because the counting is inclusive.

---

## 2. Using `int` Instead of `long long`

This can cause overflow when computing:

```text
i * 10
```

or related expressions.

---

## 3. Forgetting That We Count All Occurrences

For example, `11` contributes **two** `1`s, not one.

The formula handles that naturally by counting digit position contributions independently.

---

## 4. Mishandling `n = 0`

If `n = 0`, the answer is `0`.

The loop condition `i <= n` correctly prevents any iterations.

---

# Interview Insight

This is a strong digit-DP-style counting problem, although it can be solved without full digit DP.

The key interview idea is:

> count contribution by digit position instead of by number

That shift in viewpoint is what turns a time-limit-exceeded solution into a logarithmic-time solution.

A strong explanation usually includes:

1. analyzing the ones place,
2. generalizing to tens, hundreds, thousands,
3. counting full cycles,
4. counting the leftover partial cycle.

---

# Final Summary

## Approach 1: Brute Force

### Idea

Check every number from `1` to `n`, convert to string, count `'1'`.

### Complexity

- Time: `O(n × log10(n))`
- Space: `O(log10(n))`

### Status

Too slow for large `n`.

---

## Approach 2: Mathematical Counting

### Idea

Count how many `1`s appear in each digit position independently.

### Formula

For each digit position `i`:

```text
(n / (i * 10)) * i + min(max(n % (i * 10) - i + 1, 0), i)
```

### Complexity

- Time: `O(log10(n))`
- Space: `O(1)`

### Status

Accepted and efficient.

---

# Best Final C++ Solution

```cpp
class Solution {
public:
    int countDigitOne(int n) {
        int countr = 0;

        for (long long i = 1; i <= n; i *= 10) {
            long long divider = i * 10;
            countr += (n / divider) * i + min(max(n % divider - i + 1, 0LL), i);
        }

        return countr;
    }
};
```

This is the cleanest and most efficient solution for the problem.
