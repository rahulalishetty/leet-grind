# Approach 1: Brute Force Over Possible Group Sizes

## Problem

Given an integer array `deck`, determine whether it can be partitioned into groups such that:

1. Each group has exactly `X` cards,
2. `X >= 2`,
3. All cards in each group have the same value.

---

## Core Observation

Let:

- `N = deck.length`
- `C_i = count of cards with value i`

If we divide into groups of size `X`, then:

1. `N % X == 0`
2. For every value i: `C_i % X == 0`

These conditions are **necessary and sufficient**.

If every frequency is divisible by `X`, we can form groups of size `X` without leftovers.

---

## Algorithm

1. Count frequencies.
2. Collect all non-zero counts.
3. For each possible `X` from 2 to N:
   - If `N % X != 0`, skip.
   - Check if every count is divisible by `X`.
   - If yes → return true.
4. Otherwise return false.

---

## Java Code

```java
class Solution {
    public boolean hasGroupsSizeX(int[] deck) {
        int N = deck.length;
        int[] count = new int[10000];

        for (int c : deck)
            count[c]++;

        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 10000; ++i)
            if (count[i] > 0)
                values.add(count[i]);

        search:
        for (int X = 2; X <= N; ++X)
            if (N % X == 0) {
                for (int v : values)
                    if (v % X != 0)
                        continue search;
                return true;
            }

        return false;
    }
}
```

---

## Complexity

Time:

- Worst case ~ O(N²)

Space:

- O(N)

---

## Drawback

Tests many invalid group sizes. Not efficient for large N.

---
