# 1434. Number of Ways to Wear Different Hats to Each Other

## Approach 1: Top-Down Dynamic Programming + Bitmasks

### Intuition

A naive solution would iterate over **people** and assign a preferred hat to each person while ensuring hats are not reused. However, since there are **up to 40 hats**, tracking hat usage would require:

```
2^40 states ≈ 1 trillion
```

This is infeasible.

Instead, observe the constraint:

```
number of people ≤ 10
```

So instead of tracking **which hats are used**, we track **which people already have hats**.

This changes the DP state size dramatically:

| Strategy              | State |
| --------------------- | ----- |
| Track hats used       | 2^40  |
| Track people assigned | 2^10  |

Thus we iterate over **hats** instead of **people**.

---

### Key Idea

For each hat we decide:

1. **Skip the hat**
2. **Assign it to a person who prefers it and doesn't have a hat yet**

We maintain a bitmask representing people who already received hats.

---

### DP Definition

```
dp(hat, mask)
```

Where:

- `hat` = current hat number being considered
- `mask` = bitmask representing which people already have hats

The `i-th` bit in mask is set if person `i` already has a hat.

The function returns the **number of valid assignments** for hats `[hat..40]`.

Final answer:

```
dp(1, 0)
```

---

### Base Cases

1. **All people assigned hats**

```
mask == done → return 1
```

where

```
done = (1 << n) - 1
```

2. **Ran out of hats**

```
hat > 40 → return 0
```

---

### Recurrence

Two options:

**Skip current hat**

```
ans = dp(hat + 1, mask)
```

**Assign hat to a person**

For each person who prefers this hat:

```
if (mask & (1 << person)) == 0
    ans += dp(hat + 1, mask | (1 << person))
```

Take modulo:

```
10^9 + 7
```

---

### Java Implementation

```java
class Solution {
    int[][] memo;
    int done;
    int n;
    int MOD = 1000000007;
    Map<Integer, ArrayList<Integer>> hatsToPeople;

    public int numberWays(List<List<Integer>> hats) {
        n = hats.size();

        hatsToPeople = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int hat: hats.get(i)) {
                hatsToPeople.putIfAbsent(hat, new ArrayList<>());
                hatsToPeople.get(hat).add(i);
            }
        }

        done = (1 << n) - 1;
        memo = new int[41][1 << n];

        for (int[] row : memo)
            Arrays.fill(row, -1);

        return dp(1, 0);
    }

    private int dp(int hat, int mask) {
        if (mask == done) return 1;
        if (hat > 40) return 0;

        if (memo[hat][mask] != -1)
            return memo[hat][mask];

        int ans = dp(hat + 1, mask);

        if (hatsToPeople.containsKey(hat)) {
            for (int person : hatsToPeople.get(hat)) {
                if ((mask & (1 << person)) == 0) {
                    ans = (ans + dp(hat + 1, mask | (1 << person))) % MOD;
                }
            }
        }

        memo[hat][mask] = ans;
        return ans;
    }
}
```

---

### Complexity Analysis

Let:

```
n = number of people
k = number of hats
```

#### Time Complexity

```
O(k * n * 2^n)
```

- `k` hat states
- `2^n` mask states
- up to `n` transitions per state

#### Space Complexity

```
O(k * 2^n)
```

Used for memoization table.

---

# Approach 2: Bottom-Up Dynamic Programming

### Intuition

This approach implements the **same recurrence** but iteratively instead of recursion.

We compute states in reverse order starting from the base cases.

DP table:

```
dp[hat][mask]
```

- `hat` ranges from `1..40`
- `mask` ranges from `0..(2^n - 1)`

---

### Base Case

When everyone has a hat:

```
dp[hat][done] = 1
```

for all hats.

---

### Transition

```
dp[hat][mask] =
    dp[hat+1][mask] +
    sum(dp[hat+1][mask | (1 << person)])
```

for every person who prefers this hat.

---

### Java Implementation

```java
class Solution {
    public int numberWays(List<List<Integer>> hats) {
        int n = hats.size();

        Map<Integer, ArrayList<Integer>> hatsToPeople = new HashMap<>();

        for (int i = 0; i < n; i++) {
            for (int hat : hats.get(i)) {
                hatsToPeople.putIfAbsent(hat, new ArrayList<>());
                hatsToPeople.get(hat).add(i);
            }
        }

        int done = (1 << n) - 1;
        int MOD = 1000000007;

        int[][] dp = new int[42][done + 1];

        for (int i = 0; i < 42; i++)
            dp[i][done] = 1;

        for (int hat = 40; hat >= 1; hat--) {
            for (int mask = done; mask >= 0; mask--) {
                int ans = dp[hat + 1][mask];

                if (hatsToPeople.containsKey(hat)) {
                    for (int person : hatsToPeople.get(hat)) {
                        if ((mask & (1 << person)) == 0) {
                            ans = (ans + dp[hat + 1][mask | (1 << person)]) % MOD;
                        }
                    }
                }

                dp[hat][mask] = ans;
            }
        }

        return dp[1][0];
    }
}
```

---

### Complexity Analysis

#### Time Complexity

```
O(k * n * 2^n)
```

#### Space Complexity

```
O(k * 2^n)
```
