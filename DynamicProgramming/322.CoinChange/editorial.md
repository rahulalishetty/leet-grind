# Coin Change (LeetCode 322) — Exhaustive Java Approaches

## Problem Restatement

You are given:

- an array `coins` of coin denominations
- an integer `amount`

Goal:

- return the **fewest number of coins** needed to make exactly `amount`
- if impossible, return `-1`

Important:

- You have an **infinite supply** of every coin
- This is a **minimum optimization** problem, not a counting problem

---

## Core Insight

For any target amount `a`, if the last coin used is `coin`, then the remaining problem is:

```text
minimum coins to make (a - coin)
```

So the core recurrence is:

```text
dp[a] = min(dp[a], 1 + dp[a - coin])
```

for every coin where `a - coin >= 0`.

This is the main idea behind nearly every correct solution.

---

# Approach 1: Pure Recursion (Brute Force)

## Idea

For a target `amount`, try every possible coin as the next chosen coin.

Then recursively solve the smaller remaining amount.

Return the minimum among all valid choices.

---

## Recursive Definition

Let:

```text
solve(rem) = minimum number of coins needed to make rem
```

Then:

```text
solve(rem) = min(1 + solve(rem - coin)) for all coin <= rem
```

Base cases:

- `solve(0) = 0`
- `solve(rem < 0)` = impossible

---

## Java Code

```java
class Solution {
    public int coinChange(int[] coins, int amount) {
        int ans = solve(coins, amount);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private int solve(int[] coins, int rem) {
        if (rem == 0) {
            return 0;
        }

        if (rem < 0) {
            return Integer.MAX_VALUE;
        }

        int best = Integer.MAX_VALUE;

        for (int coin : coins) {
            int sub = solve(coins, rem - coin);
            if (sub != Integer.MAX_VALUE) {
                best = Math.min(best, 1 + sub);
            }
        }

        return best;
    }
}
```

---

## Why It Is Bad

This recomputes the same subproblems many times.

For example, when computing `solve(11)` with coins `[1,2,5]`:

- `solve(10)` may be reached from coin `1`
- `solve(9)` may be reached from coin `2`
- later those recurse into the same states again

Huge overlap.

---

## Complexity

### Time Complexity

- Exponential in the worst case

### Space Complexity

- `O(amount)` recursion depth in worst case

---

## When to Use

Only for:

- understanding the recurrence
- seeing why memoization helps

Not practical.

---

# Approach 2: Top-Down DP (Memoization)

## Idea

Cache the answer for each remaining amount.

Since the only variable state is `rem`, we store:

- `memo[rem] = minimum coins needed for rem`

Now each subproblem is computed once.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2); // uncomputed
        int ans = solve(coins, amount, memo);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private int solve(int[] coins, int rem, int[] memo) {
        if (rem == 0) {
            return 0;
        }

        if (rem < 0) {
            return Integer.MAX_VALUE;
        }

        if (memo[rem] != -2) {
            return memo[rem];
        }

        int best = Integer.MAX_VALUE;

        for (int coin : coins) {
            int sub = solve(coins, rem - coin, memo);
            if (sub != Integer.MAX_VALUE) {
                best = Math.min(best, 1 + sub);
            }
        }

        memo[rem] = best;
        return best;
    }
}
```

---

## Complexity

### Time Complexity

- `O(amount * n)`
- where `n = coins.length`

Each amount `0...amount` is solved once, and for each we try all coins.

### Space Complexity

- `O(amount)` for memo
- plus recursion stack up to `O(amount)`

---

## Pros

- Natural transition from recursion
- Easy to derive

## Cons

- Uses recursion stack
- Slight overhead from recursive calls

---

# Approach 3: Bottom-Up DP (Classic 1D DP)

## Idea

Build answers for every amount from `0` to `amount`.

Let:

```text
dp[a] = minimum coins needed to make amount a
```

Initialize:

- `dp[0] = 0`
- everything else = large sentinel value

Transition:

```text
dp[a] = min(dp[a], 1 + dp[a - coin])
```

for every coin such that `coin <= a`.

This is the standard solution.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);

        dp[0] = 0;

        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (coin <= a) {
                    dp[a] = Math.min(dp[a], 1 + dp[a - coin]);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }
}
```

---

## Why `amount + 1`?

The worst possible valid answer can never exceed `amount` if coin `1` exists.

So `amount + 1` is a safe “infinity” sentinel:

- larger than any valid minimum
- small enough to avoid most overflow problems

---

## Complexity

### Time Complexity

- `O(amount * n)`

### Space Complexity

- `O(amount)`

---

## Pros

- Standard
- Efficient
- Simple
- Best general solution in interviews

## Cons

- None significant for this problem

---

# Approach 4: Bottom-Up DP by Coins Outer Loop

## Idea

Same recurrence, but iterate over coins first:

```text
for coin in coins:
    for a from coin to amount:
        dp[a] = min(dp[a], 1 + dp[a - coin])
```

This still works because we have unlimited coins.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);

        dp[0] = 0;

        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) {
                dp[a] = Math.min(dp[a], 1 + dp[a - coin]);
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }
}
```

---

## Does Loop Order Matter?

For this **minimum coin count with unlimited coins**, both of these work:

1. amount outer, coin inner
2. coin outer, amount inner

Why?
Because the state transition allows reusing the same coin infinitely.

This differs from some 0/1 knapsack problems where loop direction is crucial.

---

## Complexity

### Time Complexity

- `O(amount * n)`

### Space Complexity

- `O(amount)`

---

## Why Mention This Version?

Because it helps connect coin change with:

- unbounded knapsack
- complete knapsack patterns

---

# Approach 5: BFS on Amount States

## Idea

Think of each amount as a node in a graph.

Start at `0`.
From current sum `x`, you can go to:

- `x + coin1`
- `x + coin2`
- ...

Each edge means “use one more coin”.

Now the problem becomes:

> Find the shortest path from `0` to `amount`

Since every edge has equal cost `1`, BFS finds the minimum number of coins.

---

## Java Code

```java
import java.util.LinkedList;
import java.util.Queue;

class Solution {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        }

        boolean[] visited = new boolean[amount + 1];
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(0);
        visited[0] = true;

        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            steps++;

            for (int i = 0; i < size; i++) {
                int curr = queue.poll();

                for (int coin : coins) {
                    int next = curr + coin;

                    if (next == amount) {
                        return steps;
                    }

                    if (next < amount && !visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
        }

        return -1;
    }
}
```

---

## Why It Works

Each BFS layer corresponds to:

- using exactly `steps` coins

The first time we reach `amount`, we have used the fewest coins.

---

## Complexity

### Time Complexity

- `O(amount * n)` in worst case

### Space Complexity

- `O(amount)`

---

## Pros

- Nice graph perspective
- Very intuitive as shortest path

## Cons

- Less standard than DP
- Can have larger constant factors

---

# Approach 6: 2D DP (Take / Skip Style)

## Idea

Use a DP table where:

- rows represent considering first `i` coin types
- columns represent target amount `a`

Let:

```text
dp[i][a] = minimum coins needed to make amount a using first i coin types
```

Transition:

- do not take current coin
- take current coin (and stay in same row because infinite supply)

This is a direct unbounded-knapsack formulation.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int n = coins.length;
        int INF = amount + 1;

        int[][] dp = new int[n + 1][amount + 1];

        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], INF);
            dp[i][0] = 0;
        }

        for (int i = 1; i <= n; i++) {
            int coin = coins[i - 1];

            for (int a = 1; a <= amount; a++) {
                dp[i][a] = dp[i - 1][a];

                if (coin <= a) {
                    dp[i][a] = Math.min(dp[i][a], 1 + dp[i][a - coin]);
                }
            }
        }

        return dp[n][amount] > amount ? -1 : dp[n][amount];
    }
}
```

---

## Complexity

### Time Complexity

- `O(n * amount)`

### Space Complexity

- `O(n * amount)`

---

## Pros

- Very clear knapsack interpretation
- Good for learning

## Cons

- Uses unnecessary extra space compared to 1D DP

---

# Approach 7: Backtracking with Sorting + Pruning

## Idea

Sort coins in descending order and try using larger denominations first.

Use DFS with pruning:

- if current count already exceeds best found, stop
- if even optimistic use of current coin cannot beat best, stop

This can work surprisingly well on some inputs.

But it does **not** have the same clean polynomial guarantee as DP.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private int best = Integer.MAX_VALUE;

    public int coinChange(int[] coins, int amount) {
        Arrays.sort(coins);
        reverse(coins);

        dfs(coins, 0, amount, 0);
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    private void dfs(int[] coins, int index, int remain, int count) {
        if (remain == 0) {
            best = Math.min(best, count);
            return;
        }

        if (index == coins.length) {
            return;
        }

        int coin = coins[index];
        int maxUse = remain / coin;

        for (int use = maxUse; use >= 0; use--) {
            int newCount = count + use;
            int newRemain = remain - use * coin;

            if (newCount >= best) {
                continue;
            }

            dfs(coins, index + 1, newRemain, newCount);
        }
    }

    private void reverse(int[] arr) {
        int l = 0, r = arr.length - 1;
        while (l < r) {
            int temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;
            l++;
            r--;
        }
    }
}
```

---

## Complexity

### Time Complexity

- Worst case exponential

### Space Complexity

- `O(n)` recursion depth

---

## Honest Assessment

This is an interesting search-based approach, but DP is safer and more standard.

---

# Approach 8: Dijkstra / Shortest Path Perspective

## Idea

Like BFS, treat each reachable amount as a graph node.

But since every coin adds one coin count, BFS already suffices.

So Dijkstra is not necessary here.

Still, it is worth noting the graph interpretation:

- Node = amount
- Edge from `x` to `x + coin`
- Edge weight = 1

Then shortest path to `amount` gives the answer.

Because all weights are equal:

- BFS is the correct simpler version

So Dijkstra is mostly a theoretical mention here, not a recommended implementation.

---

# Why Greedy Fails

A tempting idea:

- always take the largest coin possible

This fails.

Example:

```text
coins = [1, 3, 4]
amount = 6
```

Greedy:

- take `4`
- remain `2`
- take `1`
- take `1`
  Total = `3` coins

Optimal:

- `3 + 3`
  Total = `2` coins

So greedy is not reliable unless the coin system has special properties.

---

# Full Derivation of the DP Recurrence

Let:

```text
dp[a] = minimum coins needed to make amount a
```

For every coin:

- if we choose `coin` as the last coin, then before that we must have formed amount `a - coin`

So:

```text
dp[a] = min(dp[a], 1 + dp[a - coin])
```

Base case:

```text
dp[0] = 0
```

Impossible states remain at “infinity”.

This recurrence is the heart of the problem.

---

# Dry Run of Classic Bottom-Up DP

Example:

```text
coins = [1,2,5]
amount = 11
```

Initialize:

```text
dp[0] = 0
dp[1..11] = 12
```

---

## a = 1

- coin 1 → `1 + dp[0] = 1`
  So:

```text
dp[1] = 1
```

## a = 2

- coin 1 → `1 + dp[1] = 2`
- coin 2 → `1 + dp[0] = 1`
  So:

```text
dp[2] = 1
```

## a = 3

- coin 1 → `1 + dp[2] = 2`
- coin 2 → `1 + dp[1] = 2`
  So:

```text
dp[3] = 2
```

## a = 4

- coin 1 → `1 + dp[3] = 3`
- coin 2 → `1 + dp[2] = 2`
  So:

```text
dp[4] = 2
```

## a = 5

- coin 1 → `1 + dp[4] = 3`
- coin 2 → `1 + dp[3] = 3`
- coin 5 → `1 + dp[0] = 1`
  So:

```text
dp[5] = 1
```

## a = 6

- coin 1 → `1 + dp[5] = 2`
- coin 2 → `1 + dp[4] = 3`
- coin 5 → `1 + dp[1] = 2`
  So:

```text
dp[6] = 2
```

Continue similarly...

Final:

```text
dp[11] = 3
```

Combination:

```text
5 + 5 + 1
```

---

# Comparing All Approaches

| Approach                |                   Time |          Space | Notes                     |
| ----------------------- | ---------------------: | -------------: | ------------------------- |
| Pure Recursion          |            Exponential |      O(amount) | Conceptual only           |
| Top-Down Memoization    |         O(amount \* n) |      O(amount) | Good recursive DP         |
| Bottom-Up 1D DP         |         O(amount \* n) |      O(amount) | Standard best solution    |
| Bottom-Up Coin-Outer DP |         O(amount \* n) |      O(amount) | Unbounded knapsack view   |
| BFS on Amount States    |         O(amount \* n) |      O(amount) | Shortest-path perspective |
| 2D DP                   |         O(amount \* n) | O(amount \* n) | Educational, heavier      |
| Backtracking + Pruning  | Exponential worst case |           O(n) | Search-based alternative  |
| Dijkstra View           |             Not needed |     Not needed | BFS already enough        |

---

# Recommended Final Java Solution

## Best Practical Solution

```java
import java.util.Arrays;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);

        dp[0] = 0;

        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (coin <= a) {
                    dp[a] = Math.min(dp[a], 1 + dp[a - coin]);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }
}
```

---

## Complexity of Recommended Solution

- **Time Complexity:** `O(amount * coins.length)`
- **Space Complexity:** `O(amount)`

---

# Edge Cases

## 1. `amount = 0`

Answer is:

```text
0
```

No coins needed.

## 2. Impossible amount

Example:

```text
coins = [2], amount = 3
```

No exact combination exists, so answer is:

```text
-1
```

## 3. Single coin type

Example:

```text
coins = [5], amount = 15
```

Answer:

```text
3
```

## 4. Large coin bigger than amount

It is simply ignored by the DP transition.

---

# Common Mistakes

## Mistake 1: Using greedy

Greedy is not generally correct.

## Mistake 2: Confusing this with “number of ways to make amount”

This problem asks for:

- minimum number of coins

not:

- number of combinations

Different DP meaning.

## Mistake 3: Overflow with impossible states

If you use `Integer.MAX_VALUE` as infinity, then:

```java
1 + dp[a - coin]
```

can overflow.

Safer to use:

```java
amount + 1
```

or check before adding.

## Mistake 4: Wrong base case

Must have:

```text
dp[0] = 0
```

Without that, no valid transitions can build upward correctly.

---

# Pattern Recognition

This problem is a classic:

- unbounded knapsack
- minimum optimization DP
- shortest path on state graph

Signals:

- unlimited reuse of choices
- target amount
- minimize count / cost

Related problems:

- Coin Change 2 (count combinations)
- Perfect Squares
- Minimum Number of Refueling Stops
- Integer Break
- Unbounded Knapsack

---

# Interview Explanation Template

A strong concise explanation:

1. Let `dp[a]` be the fewest number of coins needed to make amount `a`.
2. Initialize `dp[0] = 0`, and all other states to a large sentinel.
3. For each amount `a`, try every coin:
   - if `coin <= a`, then
     `dp[a] = min(dp[a], 1 + dp[a - coin])`
4. If `dp[amount]` is still sentinel, return `-1`; otherwise return it.

Time:

- `O(amount * n)`

Space:

- `O(amount)`

---

# Final Takeaway

The heart of the problem is:

> If the last coin used is `coin`, then the rest of the work is just the optimal solution for `amount - coin`.

That directly leads to the recurrence:

```text
dp[a] = min(dp[a], 1 + dp[a - coin])
```

The best practical solution is the classic **1D bottom-up DP**.
