# 2403. Minimum Time to Kill All Monsters

## Problem Summary

You are given an array `power[]` where `power[i]` represents the mana required to defeat the `i-th` monster.

Rules:

- You start with:
  - `mana = 0`
  - `gain = 1`
- Each day:
  1. Mana increases by `gain`
  2. If `mana >= power[i]`, you may defeat that monster
- When a monster is defeated:
  - `mana` resets to **0**
  - `gain` increases by **1**

Goal:
Find the **minimum number of days** needed to defeat all monsters.

Constraints:

- `1 ≤ n ≤ 17`
- `1 ≤ power[i] ≤ 10^9`

---

# Key Observations

## 1. Killing order matters

If we kill monsters in different orders:

- Early kills give **smaller gain**
- Later kills give **larger gain**

So killing a **large monster later** might be beneficial because mana grows faster.

Therefore:

**We must explore different orders of killing monsters.**

This is essentially a **permutation optimization problem**.

---

# Core Insight

Suppose:

- `k` monsters have already been defeated
- Current `gain = k + 1`

To defeat monster `i` with power `p`, we need to accumulate enough mana.

Mana gained per day = `gain`.

Days needed:

```
days = ceil(p / gain)
```

Formula:

```
days = (p + gain - 1) // gain
```

After killing:

```
gain -> gain + 1
mana -> 0
```

So each kill costs:

```
days required to reach monster power
```

---

# State Representation

Since `n ≤ 17`, we can represent which monsters are defeated using a **bitmask**.

Example:

```
mask = 01011
```

Meaning monsters `0,1,3` are already defeated.

Total states:

```
2^17 ≈ 131072
```

Very manageable.

---

# Dynamic Programming on Bitmask

Define:

```
dp[mask] = minimum days needed to defeat monsters in mask
```

Transition:

For every monster `j` not in `mask`:

1. Current killed monsters:

```
k = bitcount(mask)
gain = k + 1
```

2. Days to defeat monster `j`

```
days = ceil(power[j] / gain)
```

3. Transition:

```
new_mask = mask | (1 << j)

dp[new_mask] = min(
    dp[new_mask],
    dp[mask] + days
)
```

---

# Initial State

```
dp[0] = 0
```

No monsters killed yet.

---

# Final Answer

```
dp[(1 << n) - 1]
```

All monsters defeated.

---

# Algorithm

1. Let `n = power.length`
2. Initialize:

```
dp size = 2^n
fill with infinity
dp[0] = 0
```

3. For each mask:

- count killed monsters
- compute gain
- try killing remaining monsters

4. Update DP.

---

# Java Implementation

```java
class Solution {
    public int minimumTime(int[] power) {
        int n = power.length;
        int N = 1 << n;

        long[] dp = new long[N];
        Arrays.fill(dp, Long.MAX_VALUE);
        dp[0] = 0;

        for (int mask = 0; mask < N; mask++) {
            int killed = Integer.bitCount(mask);
            int gain = killed + 1;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) {
                    long days = (power[i] + gain - 1) / gain;

                    int next = mask | (1 << i);
                    dp[next] = Math.min(dp[next], dp[mask] + days);
                }
            }
        }

        return (int) dp[N - 1];
    }
}
```

---

# Example Walkthrough

Example:

```
power = [3,1,4]
```

Possible orders:

### Order 1

```
1 -> 3 -> 4
```

Kill 1:

```
gain=1
days=1
```

Kill 3:

```
gain=2
days=2
```

Kill 4:

```
gain=3
days=2
```

Total = 5

---

### Optimal Order

```
1 -> 4 -> 3
```

Kill 1:

```
gain=1
days=1
```

Kill 4:

```
gain=2
days=2
```

Kill 3:

```
gain=3
days=1
```

Total = **4**

---

# Complexity Analysis

## Number of states

```
2^n
```

where:

```
n ≤ 17
```

```
2^17 = 131072
```

---

## Transitions per state

At most:

```
n
```

---

## Time Complexity

```
O(n * 2^n)
```

Worst case:

```
17 * 131072 ≈ 2.2 million operations
```

Very fast.

---

## Space Complexity

```
O(2^n)
```

DP table size.

---

# Why Greedy Does NOT Work

You might try:

- kill smallest monster first
- kill largest monster last

But this fails because:

- early gain increases affect future kills
- the cost depends on **position in order**

Thus the problem is a **permutation DP problem**.

---

# Pattern Recognition

This problem belongs to the family:

```
Bitmask DP over permutations
```

Common signals:

- `n ≤ 20`
- order of operations matters
- cost depends on number of completed tasks

Classic problems using the same idea:

- Traveling Salesman
- Task scheduling with state costs
- Minimum cost permutation

---

# Final Takeaway

The optimal strategy is found by:

```
DP over subsets (bitmask DP)
```

Where the cost of selecting a monster depends on:

```
current gain = killed_monsters + 1
```
