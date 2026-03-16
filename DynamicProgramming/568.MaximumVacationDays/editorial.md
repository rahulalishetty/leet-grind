# Maximum Vacation Days — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

The problem discussed here is the classic **Maximum Vacation Days** problem.

You are given:

- a `flights` matrix, where `flights[i][j] == 1` means you can fly from city `i` to city `j`
- a `days` matrix, where `days[i][w]` is the number of vacation days you can spend in city `i` during week `w`

You start in city `0` at week `0`.

At the start of each week, you may:

- stay in the same city, or
- fly to another city if a flight exists

The goal is to maximize the total number of vacation days across all weeks.

---

# Core Insight

At every week, your decision depends on:

- the city you are currently in
- the week number
- the best choices available in future weeks

This immediately suggests **dynamic programming**, because the optimal answer for the current state depends on optimal answers of smaller subproblems.

A state can be defined as:

```text
(current city, current week)
```

meaning:

> What is the maximum number of vacation days I can still collect if I start this week in this city?

---

# Approach 1: Using Depth First Search (Time Limit Exceeded)

## Intuition

The most direct solution is recursion.

Define a function:

```text
dfs(cur_city, weekno)
```

which returns:

> the maximum vacation days that can be taken starting from city `cur_city` at week `weekno`

At each function call:

- try staying in the same city
- try flying to any reachable city
- recursively compute the future answer
- choose the maximum

This is correct, but it performs the same subproblem many times and therefore becomes too slow.

---

## Recursive Transition

From state `(cur_city, weekno)`:

For every city `i` such that:

- `i == cur_city` (stay), or
- `flights[cur_city][i] == 1` (fly)

the total vacation is:

```text
days[i][weekno] + dfs(i, weekno + 1)
```

Take the maximum over all such cities.

---

## Java Implementation

```java
public class Solution {
    public int maxVacationDays(int[][] flights, int[][] days) {
        return dfs(flights, days, 0, 0);
    }

    public int dfs(int[][] flights, int[][] days, int cur_city, int weekno) {
        if (weekno == days[0].length)
            return 0;

        int maxvac = 0;

        for (int i = 0; i < flights.length; i++) {
            if (flights[cur_city][i] == 1 || i == cur_city) {
                int vac = days[i][weekno] + dfs(flights, days, i, weekno + 1);
                maxvac = Math.max(maxvac, vac);
            }
        }

        return maxvac;
    }
}
```

---

## Why This Gets TLE

The recursion tree contains many repeated states.

For example, the state:

```text
dfs(city = 2, week = 3)
```

may be reached from multiple earlier paths.

Without caching, each repeated occurrence recomputes the entire subtree again.

This causes exponential blow-up.

---

# Approach 2: DFS with Memoization

## Intuition

The previous recursive solution repeats work because the same state:

```text
(cur_city, weekno)
```

is solved many times.

To eliminate repeated work, we store the answer once it is computed.

Let:

```text
memo[i][j]
```

represent:

> the maximum vacation days obtainable starting from city `i` at week `j`

Then every recursive state is solved once and reused later.

---

## Algorithm

1. Create a 2D `memo` array
2. Initialize all entries to a sentinel value such as `Integer.MIN_VALUE`
3. In `dfs(cur_city, weekno)`:
   - if `weekno == totalWeeks`, return `0`
   - if `memo[cur_city][weekno]` is already filled, return it
   - otherwise try all valid next cities
   - compute the maximum result
   - store it in `memo[cur_city][weekno]`
4. Return `dfs(0, 0)`

---

## Java Implementation

```java
public class Solution {
    public int maxVacationDays(int[][] flights, int[][] days) {
        int[][] memo = new int[flights.length][days[0].length];
        for (int[] l : memo)
            Arrays.fill(l, Integer.MIN_VALUE);

        return dfs(flights, days, 0, 0, memo);
    }

    public int dfs(int[][] flights, int[][] days, int cur_city, int weekno, int[][] memo) {
        if (weekno == days[0].length)
            return 0;

        if (memo[cur_city][weekno] != Integer.MIN_VALUE)
            return memo[cur_city][weekno];

        int maxvac = 0;
        for (int i = 0; i < flights.length; i++) {
            if (flights[cur_city][i] == 1 || i == cur_city) {
                int vac = days[i][weekno] + dfs(flights, days, i, weekno + 1, memo);
                maxvac = Math.max(maxvac, vac);
            }
        }

        memo[cur_city][weekno] = maxvac;
        return maxvac;
    }
}
```

---

## Complexity Analysis

Let:

- `n` = number of cities
- `k` = number of weeks

### Time Complexity

There are `n * k` distinct states.

For each state, we iterate over all `n` cities to decide where to go next.

So the total is:

```text
O(n^2 * k)
```

### Space Complexity

The memo table uses:

```text
O(n * k)
```

The recursion stack can go as deep as `k`, which is smaller than the table size.

So the overall space complexity is:

```text
O(n * k)
```

---

# Approach 3: Using 2-D Dynamic Programming

## Intuition

Instead of recursion, we can solve the same state transition iteratively.

Define:

```text
dp[i][week]
```

as:

> the maximum vacation days obtainable starting from city `i` at week `week`

Notice something important:

The value of `dp[i][week]` depends only on:

- `days[?][week]`
- `dp[?][week + 1]`

That means the DP can be filled **backward in time**, starting from the last week.

---

## DP Transition

At week `week`, starting from city `cur_city`, there are two categories of choices:

### 1. Stay in the same city

Then:

```text
days[cur_city][week] + dp[cur_city][week + 1]
```

### 2. Fly to another reachable city

For any `dest_city` such that:

```text
flights[cur_city][dest_city] == 1
```

the result is:

```text
days[dest_city][week] + dp[dest_city][week + 1]
```

Take the maximum over all such options.

---

## Why Backward Filling Works

At week `week`, all needed future values belong to week `week + 1`.

So if we fill weeks in reverse order:

```text
last week -> previous week -> ... -> first week
```

then whenever we need `dp[*][week + 1]`, it has already been computed.

---

## Java Implementation

```java
public class Solution {
    public int maxVacationDays(int[][] flights, int[][] days) {
        if (days.length == 0 || flights.length == 0) return 0;

        int[][] dp = new int[days.length][days[0].length + 1];

        for (int week = days[0].length - 1; week >= 0; week--) {
            for (int cur_city = 0; cur_city < days.length; cur_city++) {
                dp[cur_city][week] = days[cur_city][week] + dp[cur_city][week + 1];

                for (int dest_city = 0; dest_city < days.length; dest_city++) {
                    if (flights[cur_city][dest_city] == 1) {
                        dp[cur_city][week] = Math.max(
                            days[dest_city][week] + dp[dest_city][week + 1],
                            dp[cur_city][week]
                        );
                    }
                }
            }
        }

        return dp[0][0];
    }
}
```

---

## Complexity Analysis

Let:

- `n` = number of cities
- `k` = number of weeks

### Time Complexity

The DP table has `n * k` cells.

Each cell considers all `n` destination cities.

So total time is:

```text
O(n^2 * k)
```

### Space Complexity

The DP table stores `n * (k + 1)` values:

```text
O(n * k)
```

---

# Approach 4: Using 1-D Dynamic Programming

## Intuition

In the 2D DP approach, observe that while computing values for week `week`, we only ever use values from:

```text
week + 1
```

That means we do not need the entire 2D table.

We only need:

- the DP values for the next week
- a temporary array for the current week

So we can compress the DP from 2D to 1D.

---

## DP Meaning

Let:

```text
dp[i]
```

mean:

> the maximum vacation days obtainable starting from city `i` in the next week already processed

Then while processing the current week, we build:

```text
temp[i]
```

for the current week using the values stored in `dp`.

Once the row for the current week is finished, assign:

```text
dp = temp
```

and continue to the previous week.

---

## Transition

Same recurrence as before:

For city `cur_city`:

### Stay

```text
days[cur_city][week] + dp[cur_city]
```

### Fly

For any reachable `dest_city`:

```text
days[dest_city][week] + dp[dest_city]
```

Take the maximum among all valid options.

---

## Java Implementation

```java
public class Solution {
    public int maxVacationDays(int[][] flights, int[][] days) {
        if (days.length == 0 || flights.length == 0) return 0;

        int[] dp = new int[days.length];

        for (int week = days[0].length - 1; week >= 0; week--) {
            int[] temp = new int[days.length];

            for (int cur_city = 0; cur_city < days.length; cur_city++) {
                temp[cur_city] = days[cur_city][week] + dp[cur_city];

                for (int dest_city = 0; dest_city < days.length; dest_city++) {
                    if (flights[cur_city][dest_city] == 1) {
                        temp[cur_city] = Math.max(
                            days[dest_city][week] + dp[dest_city],
                            temp[cur_city]
                        );
                    }
                }
            }

            dp = temp;
        }

        return dp[0];
    }
}
```

---

## Complexity Analysis

Let:

- `n` = number of cities
- `k` = number of weeks

### Time Complexity

For each of the `k` weeks, we compute `n` cities, and for each city we iterate through all `n` possible destinations.

So:

```text
O(n^2 * k)
```

### Space Complexity

Now we only store two arrays of length `n`:

- `dp`
- `temp`

So the actual extra space is:

```text
O(n)
```

> Note: The text you provided states `O(k)` for space, but for this 1-D DP implementation the correct compressed DP space is `O(n)`, since the arrays are indexed by city, not by week.

---

# Why the 1-D DP Works

The recurrence only depends on the immediately next week.

Therefore, as soon as we finish using `week + 1`, we can discard older weeks entirely.

This is a classic DP space optimization:

- 2D table when all states are stored
- 1D rolling array when only one future layer is needed

---

# Comparison of Approaches

| Approach          | Main Idea                             |   Time Complexity | Space Complexity |
| ----------------- | ------------------------------------- | ----------------: | ---------------: |
| DFS               | Try all reachable cities recursively  | Exponential / TLE |  Recursive stack |
| DFS + Memoization | Cache `(city, week)` states           |      `O(n^2 * k)` |       `O(n * k)` |
| 2-D DP            | Fill DP table backward by week        |      `O(n^2 * k)` |       `O(n * k)` |
| 1-D DP            | Compress DP by keeping only next week |      `O(n^2 * k)` |           `O(n)` |

---

# Key Takeaways

## 1. The state is `(city, week)`

This is the central modeling step.

Once that state is identified, both memoization and bottom-up DP become natural.

## 2. The answer depends only on the future

This allows backward DP.

At state `(city, week)`, we do not care about how we got there, only about the best future decision.

## 3. Memoization removes duplicate recursion

The plain DFS is too slow because identical states are recomputed many times.

## 4. Bottom-up DP is the iterative version of memoized DFS

The recurrence is the same. The only difference is the order in which states are computed.

## 5. Space can be optimized

Because each state only depends on the next week, we can reduce `O(n * k)` space to `O(n)`.

---

# Final Insight

The most important transition in thinking is this:

> The problem is not about simulating travel week by week greedily.

Instead, it is about:

> For each `(city, week)` state, what is the best total vacation count I can still obtain?

That state-based view turns the problem into standard dynamic programming and leads directly to the accepted efficient solutions.
