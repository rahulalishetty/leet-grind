# House of Cards Problem — Detailed Explanation

## Problem

You are given an integer `n` representing the number of playing cards.

A **house of cards** is constructed using rows of triangles and horizontal cards with the following rules:

1. Each triangle is formed by **two leaning cards**.
2. **One horizontal card** must be placed between adjacent triangles in the same row.
3. Any triangle placed in a higher row must sit **on top of a horizontal card** from the row below.
4. Triangles in a row are always placed **leftmost first**.
5. A house must use **exactly `n` cards**.
6. Two houses are considered **distinct** if any row has a different number of triangles.

Return the **number of distinct houses** that can be built.

---

# Key Observation

Let:

`t` = number of triangles in a row.

Each triangle uses:

- **2 slanted cards**

Between adjacent triangles we need:

- **(t − 1) horizontal cards**

Therefore the total cards used by a row is:

```
2t + (t - 1)
```

Simplifying:

```
3t - 1
```

So a row containing `t` triangles consumes:

```
3t - 1 cards
```

---

# Total Cards Used

If the house contains rows:

```
t1, t2, t3 ... tk
```

Then the total cards used is:

```
(3t1 - 1) + (3t2 - 1) + ... + (3tk - 1)
```

---

# Structural Constraint

Because triangles in higher rows must sit on horizontal cards below:

- A row with `t` triangles has **t-1 horizontal cards**
- Therefore the next row can have **at most (t-1) triangles**

Thus:

```
t1 > t2 > t3 > ... > tk >= 1
```

The row sizes must be **strictly decreasing**.

This means each row size `t` can be used **only once**.

---

# Reformulating the Problem

The problem becomes:

Count the number of ways to write `n` as a sum of numbers:

```
3t - 1
```

where each `t` is used **at most once**.

This is a classic **subset sum counting problem**.

---

# Dynamic Programming Idea

Let

```
dp[s] = number of ways to build houses using exactly s cards
```

Base case:

```
dp[0] = 1
```

Meaning: there is one way to use zero cards (build nothing).

For each possible row size `t`:

```
cost = 3*t - 1
```

Update the DP table using **0/1 knapsack** style transition:

```
dp[s] += dp[s - cost]
```

We iterate backwards so that each `t` is used only once.

---

# Algorithm

1. Initialize `dp[0] = 1`
2. For each `t` such that `3t - 1 <= n`
3. Compute row cost = `3t - 1`
4. Update `dp` backwards
5. Answer = `dp[n]`

---

# Java Implementation

```java
class Solution {
    public int houseOfCards(int n) {

        long[] dp = new long[n + 1];
        dp[0] = 1;

        for (int t = 1; 3 * t - 1 <= n; t++) {
            int cost = 3 * t - 1;

            for (int s = n; s >= cost; s--) {
                dp[s] += dp[s - cost];
            }
        }

        return (int) dp[n];
    }
}
```

---

# Example

Suppose:

```
n = 16
```

Possible row costs:

| t   | cards |
| --- | ----- |
| 1   | 2     |
| 2   | 5     |
| 3   | 8     |
| 4   | 11    |
| 5   | 14    |

Now find subsets that sum to `16`.

```
11 + 5 = 16
```

So the house is:

```
Row 1 → 4 triangles
Row 2 → 2 triangles
```

Answer:

```
1 house
```

---

# Complexity Analysis

Let

```
3t - 1 <= n
```

Then

```
t ≈ n / 3
```

## Time Complexity

```
O(n * (n/3)) ≈ O(n²)
```

## Space Complexity

```
O(n)
```

---

# Why This Works

Once the set of row sizes `{t1, t2, ...}` is chosen:

- Row placement is **forced**
- Geometry uniquely determines the house

Therefore counting houses reduces to **counting subsets** of row costs.

This observation converts a seemingly geometric problem into a **classic DP subset-sum counting problem**.
