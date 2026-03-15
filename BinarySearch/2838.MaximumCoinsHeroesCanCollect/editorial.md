# 2838. Maximum Coins Heroes Can Collect — Approach Explanation

## Overview

We are given:

- An array **heroes** where `heroes[i]` represents the power of the `i-th` hero.
- An array **monsters** where `monsters[j]` represents the power of the `j-th` monster.
- An array **coins** where `coins[j]` represents the coins obtained after defeating monster `j`.

A hero `heroes[i]` can defeat a monster `monsters[j]` **only if**:

```
heroes[i] >= monsters[j]
```

If a hero defeats a monster, they gain `coins[j]` coins.

Our goal is to compute the **total coins each hero can collect by defeating all monsters they are capable of defeating**.

Return an array where:

```
ans[i] = total coins collected by hero i
```

---

# Approach 1: Sorting + Prefix Sum + Binary Search

## Intuition

A brute-force solution would check **every monster for every hero**.

```
for hero in heroes:
    for monster in monsters:
        check if hero can defeat monster
```

This results in:

```
O(n * m)
```

Which is too slow for large inputs.

### Key Observation

If we **sort monsters by power**, then:

- A hero can defeat **all monsters whose power ≤ hero power**.
- These monsters will form a **prefix** in the sorted array.

Thus, instead of scanning the entire array:

- We **binary search** to find the largest monster the hero can defeat.
- Then use **prefix sums** to quickly compute total coins.

---

# Step 1: Maintain Monster-Coin Mapping

If we sort only `monsters`, we lose the mapping to `coins`.

So we construct:

```
monsterAndCoin[i] = [monsterPower, coinReward]
```

Example:

```
monsters = [1,5,2]
coins    = [10,4,6]

monsterAndCoin =

[1,10]
[5,4]
[2,6]
```

---

# Step 2: Sort Monsters

Sort the array by monster power.

```
monsterAndCoin =

[1,10]
[2,6]
[5,4]
```

---

# Step 3: Prefix Sum of Coins

We compute a prefix sum array:

```
coinsSum[i] = total coins from monster 0 → i
```

Example:

```
coins:     [10, 6, 4]

coinsSum:  [10, 16, 20]
```

Meaning:

- Defeat monster 0 → 10 coins
- Defeat monsters 0..1 → 16 coins
- Defeat monsters 0..2 → 20 coins

---

# Step 4: Binary Search per Hero

For each hero:

1. Binary search for the **largest monster power ≤ heroPower**
2. That index `r` means hero can defeat monsters `[0..r]`
3. Total coins = `coinsSum[r]`

If hero cannot defeat any monster → result = `0`

---

# Algorithm

1. Create `monsterAndCoin` array.
2. Sort by monster power.
3. Compute prefix sum array `coinsSum`.
4. For each hero:
   - Binary search the largest monster ≤ hero power.
   - Use prefix sum to compute coins.

---

# Java Implementation

```java
class Solution {

    public long[] maximumCoins(int[] heroes, int[] monsters, int[] coins) {
        long[] ans = new long[heroes.length];
        int[][] monsterAndCoin = new int[monsters.length][2];

        for (int i = 0; i < monsters.length; i++) {
            monsterAndCoin[i][0] = monsters[i];
            monsterAndCoin[i][1] = coins[i];
        }

        Arrays.sort(monsterAndCoin, (a, b) -> a[0] - b[0]);

        long[] coinsSum = new long[coins.length];
        long prefixSum = 0;

        for (int i = 0; i < monsterAndCoin.length; i++) {
            prefixSum += monsterAndCoin[i][1];
            coinsSum[i] = prefixSum;
        }

        for (int i = 0; i < heroes.length; i++) {
            ans[i] = findTotalCoins(monsterAndCoin, heroes[i], coinsSum);
        }

        return ans;
    }

    private long findTotalCoins(int[][] monsterAndCoin, int heroPower, long[] coinsSum) {

        int l = 0;
        int r = monsterAndCoin.length - 1;

        while (l <= r) {
            int mid = (l + r) / 2;

            if (monsterAndCoin[mid][0] > heroPower) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        if (r < 0) return 0;

        return coinsSum[r];
    }
}
```

---

# Complexity Analysis

## Time Complexity

```
Sorting monsters:      O(m log m)
Binary search heroes:  O(n log m)

Total = O((m+n) log m)
```

Where:

```
n = heroes.length
m = monsters.length
```

---

## Space Complexity

Additional memory used:

```
monsterAndCoin → O(m)
coinsSum       → O(m)
```

Sorting overhead depends on language:

| Language | Sorting Algorithm | Space    |
| -------- | ----------------- | -------- |
| Java     | QuickSort variant | O(log n) |
| C++      | IntroSort         | O(log n) |
| Python   | TimSort           | O(n)     |

Total:

```
O(m + S)
```

Where `S` is sorting stack space.
