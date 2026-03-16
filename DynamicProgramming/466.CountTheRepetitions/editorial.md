# 466. Count The Repetitions — Exhaustive Solution Notes

## Overview

This problem is about repeated strings and subsequence matching.

We are given:

- `str1 = [s1, n1]` meaning `s1` repeated `n1` times
- `str2 = [s2, n2]` meaning `s2` repeated `n2` times

We must return the largest integer `m` such that:

```text
[str2, m]
```

can be obtained from `str1` by deleting characters.

In simpler terms:

- count how many times `s2` can be matched as a subsequence inside `s1` repeated `n1` times
- divide that count by `n2`
- return the maximum full groups

The main difficulty is that `n1` and `n2` can be as large as `10^6`, so directly building the repeated strings is impossible.

This write-up explains two approaches:

1. **Brute Force**
2. **Pattern Detection / Better Brute Force**

---

## Problem Statement

We define:

```text
str = [s, n]
```

as the string formed by concatenating string `s` exactly `n` times.

For example:

```text
["abc", 3] = "abcabcabc"
```

We say that string `x` can be obtained from string `y` if we can remove some characters from `y` so that it becomes `x`.

That is exactly the definition of a **subsequence**.

Given strings `s1`, `s2` and integers `n1`, `n2`, define:

```text
str1 = [s1, n1]
str2 = [s2, n2]
```

Return the maximum integer `m` such that:

```text
[str2, m]
```

can be obtained from `str1`.

---

## Example 1

**Input**

```text
s1 = "acb", n1 = 4
s2 = "ab",  n2 = 2
```

**Output**

```text
2
```

**Explanation**

`str1` is:

```text
"acbacbacbacb"
```

Each copy of `s2 = "ab"` can be matched as a subsequence multiple times.

The total number of matched `"ab"` subsequences is `4`, so the number of full copies of:

```text
str2 = ["ab", 2] = "abab"
```

is:

```text
4 / 2 = 2
```

---

## Example 2

**Input**

```text
s1 = "acb", n1 = 1
s2 = "acb", n2 = 1
```

**Output**

```text
1
```

**Explanation**

The strings are identical, so one full copy can be matched.

---

## Constraints

- `1 <= s1.length, s2.length <= 100`
- `s1` and `s2` consist of lowercase English letters
- `1 <= n1, n2 <= 10^6`

---

# Core Idea

Suppose we scan through `str1` from left to right while trying to match characters of `s2` in order.

We keep an index into `s2`:

- when `s1[j] == s2[index]`, we advance `index`
- if `index` reaches the end of `s2`, we have matched one full copy of `s2`
- we reset `index = 0` and continue

So the real problem is:

> How many total times can we match `s2` as a subsequence inside `[s1, n1]`?

Once we know that count, say:

```text
repeat_count
```

then the answer is simply:

```text
repeat_count / n2
```

because one copy of `str2` requires `n2` copies of `s2`.

---

# Approach 1: Brute Force [Time Limit Exceeded]

## Intuition

The most direct solution is:

- repeat scanning `s1`, exactly `n1` times
- while scanning, try to match `s2`
- every time we finish one copy of `s2`, increment `repeat_count`
- finally return:
  ```text
  repeat_count / n2
  ```

This is conceptually simple and correct.

The problem is that `n1` can be huge.

---

## Algorithm

1. Initialize:
   - `index = 0` → current position in `s2`
   - `repeat_count = 0` → number of matched `s2` subsequences
2. For each block of `s1` from `0` to `n1 - 1`:
   - scan every character of `s1`
   - if the current character matches `s2[index]`, increment `index`
   - if `index == s2.length()`, then:
     - we matched one full `s2`
     - increment `repeat_count`
     - reset `index = 0`
3. Return:
   ```text
   repeat_count / n2
   ```

---

## C++ Implementation — Brute Force

```cpp
int getMaxRepetitions(string s1, int n1, string s2, int n2)
{
    int index = 0, repeat_count = 0;
    int s1_size = s1.size(), s2_size = s2.size();

    for (int i = 0; i < n1; i++) {
        for (int j = 0; j < s1_size; j++) {
            if (s1[j] == s2[index])
                ++index;

            if (index == s2_size) {
                index = 0;
                ++repeat_count;
            }
        }
    }

    return repeat_count / n2;
}
```

---

## Complexity Analysis — Brute Force

### Time Complexity

We scan all characters of `s1` exactly `n1` times.

So the time complexity is:

```text
O(n1 × |s1|)
```

This is too slow when `n1` is up to `10^6`.

---

### Space Complexity

Only a few variables are used.

So the space complexity is:

```text
O(1)
```

---

# Why the Brute Force Is Wasteful

The string `s1` repeats again and again.

When we finish scanning one whole copy of `s1`, the only information that matters for the future is:

1. how many copies of `s2` we have fully matched so far
2. which index in `s2` we are currently waiting for next

The important part is the second one:

```text
index
```

That tells us the "state" at the start of the next `s1` block.

Since `s2.length <= 100`, this state can only take a small number of values.

So eventually some state must repeat.

That repeated state is the key to the optimized solution.

---

# Approach 2: A Better Brute Force [Accepted]

## Intuition

Instead of scanning all `n1` copies of `s1`, we exploit repetition.

After each full block of `s1`, we record:

- the current `index` in `s2`
- the total `count` of matched `s2` copies so far

If the same `index` appears again at the start of two different `s1` blocks, then the matching process from that point onward will repeat in a cycle.

Why?

Because:

- the next block of `s1` is always the same
- the current matching position in `s2` is the same
- so the future behavior must also be the same

This gives us a repeating pattern.

Then we can skip large parts of the simulation using arithmetic instead of character-by-character scanning.

---

# Why Repetition Must Happen

The current matching position `index` can only be one of:

```text
0, 1, 2, ..., |s2| - 1
```

So there are only `|s2|` possible states.

After scanning enough copies of `s1`, one of these states must repeat.

This is an application of the **Pigeonhole Principle**:

> If more than `m` items are placed into `m` containers, at least one container must hold more than one item.

Here:

- items = observed block states
- containers = possible `index` values

So after at most about `|s2| + 1` blocks, repetition is guaranteed.

---

# What We Store

We maintain two arrays:

## 1. `indexr[i]`

The value of `index` after finishing the `i`-th copy of `s1`.

## 2. `countr[i]`

The number of full `s2` matches obtained after finishing the `i`-th copy of `s1`.

These arrays let us detect a repeated state and compute the answer efficiently.

---

# Cycle Structure

Suppose while processing block `i`, we find that:

```text
indexr[k] == index
```

for some earlier block `k`.

That means the state repeated.

Then the process can be broken into three parts:

## 1. Prefix before the cycle

The part before repetition starts.

Count:

```text
prev_count = countr[k]
```

---

## 2. Repeating cycle

From block `k + 1` to block `i`.

This block pattern repeats many times.

Each cycle contributes:

```text
countr[i] - countr[k]
```

matched `s2` strings.

The number of full cycle repetitions is:

```text
(n1 - 1 - k) / (i - k)
```

So:

```text
pattern_count = (countr[i] - countr[k]) * ((n1 - 1 - k) / (i - k))
```

---

## 3. Remaining tail after full cycles

There may be a leftover number of blocks after repeating the cycle as many times as possible.

That leftover contributes:

```text
remain_count = countr[k + (n1 - 1 - k) % (i - k)] - countr[k]
```

---

## Final total

So the total number of matched `s2` copies is:

```text
prev_count + pattern_count + remain_count
```

Then divide by `n2`.

---

# Algorithm

1. If `n1 == 0`, return `0`.
2. Initialize:
   - `index = 0`
   - `count = 0`
3. Create arrays:
   - `indexr`
   - `countr`
4. For each block `i` from `0` to `n1 - 1`:
   - scan one full copy of `s1`
   - update `index` and `count`
   - store:
     ```text
     indexr[i] = index
     countr[i] = count
     ```
   - check all earlier blocks `k < i`
   - if `indexr[k] == index`, then a cycle is found:
     - compute prefix count
     - compute repeating cycle count
     - compute remaining tail count
     - return total / `n2`
5. If no repetition is found, return:
   ```text
   countr[n1 - 1] / n2
   ```

---

## C++ Implementation — Better Brute Force / Cycle Detection

```cpp
int getMaxRepetitions(string s1, int n1, string s2, int n2)
{
    if (n1 == 0)
        return 0;

    int indexr[s2.size() + 1] = { 0 }; // index at start/end of each s1 block
    int countr[s2.size() + 1] = { 0 }; // count of repetitions till present block

    int index = 0, count = 0;

    for (int i = 0; i < n1; i++) {
        for (int j = 0; j < s1.size(); j++) {
            if (s1[j] == s2[index])
                ++index;

            if (index == s2.size()) {
                index = 0;
                ++count;
            }
        }

        countr[i] = count;
        indexr[i] = index;

        for (int k = 0; k < i; k++) {
            if (indexr[k] == index) {
                int prev_count = countr[k];
                int pattern_count = (countr[i] - countr[k]) * (n1 - 1 - k) / (i - k);
                int remain_count = countr[k + (n1 - 1 - k) % (i - k)] - countr[k];

                return (prev_count + pattern_count + remain_count) / n2;
            }
        }
    }

    return countr[n1 - 1] / n2;
}
```

---

# Understanding the Formula More Carefully

Suppose repetition is detected between blocks `k` and `i`.

That means:

- after block `k`, the `s2` index is some value `x`
- after block `i`, the `s2` index is again `x`

So blocks between `k+1` and `i` form one repeating unit of length:

```text
cycle_length = i - k
```

The number of `s2` matches produced by one such cycle is:

```text
cycle_count = countr[i] - countr[k]
```

Now the number of blocks left after the prefix is:

```text
n1 - 1 - k
```

So:

- full cycle repetitions:
  ```text
  (n1 - 1 - k) / cycle_length
  ```
- leftover blocks:
  ```text
  (n1 - 1 - k) % cycle_length
  ```

That is exactly how the formula is derived.

---

# Why This Is Much Faster

The brute force solution may scan up to `n1` blocks of `s1`.

This optimized solution only needs to scan until a repeated `index` appears.

Since `index` has at most `|s2|` possible values, repetition appears quickly.

So instead of depending on `n1`, the work depends mainly on:

- `|s1|`
- `|s2|`

which are at most `100`.

That makes the solution efficient.

---

# Complexity Analysis — Better Brute Force

Let:

- `|s1|` = length of `s1`
- `|s2|` = length of `s2`

### Time Complexity

By the Pigeonhole Principle, repetition is found after at most about `|s2| + 1` blocks.

For each block:

- scan all of `s1`
- possibly compare with earlier stored states

So the time complexity is:

```text
O(|s1| × |s2|)
```

This is much better than depending on `n1`.

---

### Space Complexity

We store arrays of size roughly `|s2|`.

So the space complexity is:

```text
O(|s2|)
```

---

# Small Conceptual Example

Suppose:

```text
s1 = "acb"
s2 = "ab"
```

While scanning one block of `s1`:

- `'a'` matches first char of `s2`
- `'c'` ignored
- `'b'` matches second char of `s2`

So one full `s2` is matched per block of `s1`.

If this behavior repeats identically block after block, then after detecting the repeated `index`, we know the whole future pattern.

That is exactly why cycle detection works.

---

# Common Mistakes

## 1. Building `str1` or `str2` explicitly

This is not feasible when `n1` or `n2` are up to `10^6`.

Always simulate using indices and counts.

---

## 2. Forgetting that subsequence order matters

You may skip characters, but you cannot reorder them.

So matching must always proceed left to right.

---

## 3. Not tracking the `s2` index state

The repeated `index` in `s2` is the key state that determines future behavior.

Without tracking it, the cycle cannot be detected.

---

## 4. Assuming repetition depends on full counts only

It does not.

Two situations with the same total `count` but different current `index` in `s2` can behave differently afterward.

The real cycle state is the current `index`.

---

# Interview Perspective

This is a classic repeated-pattern optimization problem.

A strong explanation usually goes like this:

1. The naive solution simulates matching `s2` inside repeated `s1`.
2. That is too slow because `n1` can be huge.
3. The only relevant state after each `s1` block is the current `index` in `s2`.
4. Since there are only `|s2|` possible index values, one state must repeat.
5. Once the state repeats, the process enters a cycle.
6. Use arithmetic to count the prefix, repeating cycles, and leftover tail.

That reasoning is the most important part.

---

# Final Summary

## Approach 1: Brute Force

### Idea

Simulate all `n1` copies of `s1`, count how many full `s2` matches occur, then divide by `n2`.

### Complexity

- Time: `O(n1 × |s1|)`
- Space: `O(1)`

### Status

Too slow for large `n1`.

---

## Approach 2: Better Brute Force / Cycle Detection

### Idea

Track the `s2` index after each full block of `s1`.
Once the same index repeats, the process has entered a cycle.
Use the cycle to compute the answer in constant time.

### Complexity

- Time: `O(|s1| × |s2|)`
- Space: `O(|s2|)`

### Status

Accepted.

---

# Best Final C++ Solution

```cpp
class Solution {
public:
    int getMaxRepetitions(string s1, int n1, string s2, int n2) {
        if (n1 == 0) {
            return 0;
        }

        vector<int> indexr(s2.size() + 1, 0);
        vector<int> countr(s2.size() + 1, 0);

        int index = 0, count = 0;

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < s1.size(); j++) {
                if (s1[j] == s2[index]) {
                    ++index;
                }

                if (index == s2.size()) {
                    index = 0;
                    ++count;
                }
            }

            countr[i] = count;
            indexr[i] = index;

            for (int k = 0; k < i; k++) {
                if (indexr[k] == index) {
                    int prev_count = countr[k];
                    int pattern_count = (countr[i] - countr[k]) * (n1 - 1 - k) / (i - k);
                    int remain_count = countr[k + (n1 - 1 - k) % (i - k)] - countr[k];

                    return (prev_count + pattern_count + remain_count) / n2;
                }
            }
        }

        return countr[n1 - 1] / n2;
    }
};
```

This is the standard cycle-detection solution for the problem.
