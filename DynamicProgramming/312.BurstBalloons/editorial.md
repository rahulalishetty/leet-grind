# Burst Balloons (LeetCode 312) — Exhaustive Java Approaches

## Problem Restatement

You are given an array `nums` where each element represents a balloon.

If you burst balloon `i`, you gain:

```text
nums[i - 1] * nums[i] * nums[i + 1]
```

But this is based on the **current neighbors at the time of bursting**, not the original array.

Boundary rule:

- If a balloon goes out of bounds, treat it as `1`

Goal:

- Burst all balloons in the best order to maximize total coins

---

## Why This Problem Is Hard

At first glance, it looks like:

- at each step, pick one balloon
- earn some coins
- continue

That sounds like recursion or backtracking.

But the hard part is:

> Bursting one balloon changes the neighbors of the remaining balloons.

So a choice made now changes future rewards in a non-local way.

This destroys most straightforward greedy strategies.

---

# Core Insight: Think in Reverse

The natural question is:

> Which balloon should I burst first?

That is hard, because after bursting it, the array changes.

Instead ask:

> Which balloon is the **last** balloon to burst in an interval?

This is the crucial reversal.

If balloon `k` is the **last** balloon burst inside interval `(left, right)`, then at that moment:

- everything between `left` and `right` except `k` is already gone
- so the neighbors of `k` are exactly `left` and `right`

That means the coins earned for bursting `k` last are:

```text
arr[left] * arr[k] * arr[right]
```

Now the subproblems become independent:

- solve left interval
- solve right interval
- add final burst of `k`

This is the central interval-DP insight.

---

# Add Virtual Balloons

To simplify boundaries, create a new array:

```text
arr = [1] + nums + [1]
```

So if:

```text
nums = [3,1,5,8]
```

then:

```text
arr = [1,3,1,5,8,1]
```

Now every burst formula becomes consistent.

---

# Approach 1: Pure Recursion by Simulating Bursting Order

## Idea

Try every possible balloon to burst first.

After bursting one balloon:

- remove it
- recursively solve remaining array

This directly models the process.

---

## Why It Is Bad

The number of burst orders is enormous:

- roughly `n!`

Also, rebuilding arrays or lists repeatedly is expensive.

Still, it is useful conceptually because it shows why we need a better formulation.

---

## Java Code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public int maxCoins(int[] nums) {
        List<Integer> balloons = new ArrayList<>();
        for (int num : nums) {
            balloons.add(num);
        }
        return solve(balloons);
    }

    private int solve(List<Integer> balloons) {
        if (balloons.isEmpty()) {
            return 0;
        }

        int best = 0;

        for (int i = 0; i < balloons.size(); i++) {
            int left = (i - 1 >= 0) ? balloons.get(i - 1) : 1;
            int right = (i + 1 < balloons.size()) ? balloons.get(i + 1) : 1;
            int coins = left * balloons.get(i) * right;

            List<Integer> next = new ArrayList<>(balloons);
            next.remove(i);

            best = Math.max(best, coins + solve(next));
        }

        return best;
    }
}
```

---

## Complexity

### Time Complexity

- Exponential / factorial-scale
- Roughly **O(n! \* n)**

### Space Complexity

- Large recursion depth plus copied lists

---

## Pros

- Matches the original process exactly

## Cons

- Completely impractical
- Does not exploit problem structure

---

# Approach 2: Top-Down Recursion on Intervals (Without Memoization)

## Idea

Instead of asking which balloon to burst first, ask:

> Which balloon is burst last in interval `(left, right)`?

Let:

- `solve(left, right)` = maximum coins obtainable by bursting all balloons strictly between indices `left` and `right`

Then try every `k` with `left < k < right` as the last burst:

```text
solve(left, right) =
    max over k:
        solve(left, k)
      + solve(k, right)
      + arr[left] * arr[k] * arr[right]
```

This is the correct recurrence.

---

## Java Code

```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;

        for (int i = 0; i < n; i++) {
            arr[i + 1] = nums[i];
        }

        return solve(arr, 0, n + 1);
    }

    private int solve(int[] arr, int left, int right) {
        if (left + 1 == right) {
            return 0;
        }

        int best = 0;

        for (int k = left + 1; k < right; k++) {
            int coins = solve(arr, left, k)
                      + solve(arr, k, right)
                      + arr[left] * arr[k] * arr[right];

            best = Math.max(best, coins);
        }

        return best;
    }
}
```

---

## Complexity

### Time Complexity

- Exponential

Because the same intervals are recomputed repeatedly.

### Space Complexity

- Recursion stack up to **O(n)**

---

## Why Learn This?

Because this is the recurrence we actually want.

Memoization or bottom-up DP will make this efficient.

---

# Approach 3: Top-Down DP (Memoization) on Intervals

## Idea

Cache the answer for each interval `(left, right)`.

There are only `O(n^2)` intervals, and each interval tries `O(n)` possible last balloons.

This gives the standard efficient solution.

---

## State

- `memo[left][right]` = maximum coins obtainable by bursting all balloons strictly between `left` and `right`

Transition:

```text
memo[left][right] =
    max over k in (left, right):
        memo[left][k]
      + memo[k][right]
      + arr[left] * arr[k] * arr[right]
```

---

## Java Code

```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;

        for (int i = 0; i < n; i++) {
            arr[i + 1] = nums[i];
        }

        Integer[][] memo = new Integer[n + 2][n + 2];
        return solve(arr, 0, n + 1, memo);
    }

    private int solve(int[] arr, int left, int right, Integer[][] memo) {
        if (left + 1 == right) {
            return 0;
        }

        if (memo[left][right] != null) {
            return memo[left][right];
        }

        int best = 0;

        for (int k = left + 1; k < right; k++) {
            int coins = solve(arr, left, k, memo)
                      + solve(arr, k, right, memo)
                      + arr[left] * arr[k] * arr[right];

            best = Math.max(best, coins);
        }

        memo[left][right] = best;
        return best;
    }
}
```

---

## Complexity

### Time Complexity

- **O(n^3)**

Reason:

- `O(n^2)` interval states
- each tries `O(n)` choices of `k`

### Space Complexity

- **O(n^2)** for memo
- recursion stack up to **O(n)**

---

## Pros

- Clear derivation from recurrence
- Elegant
- Easy transition from recursive thinking

## Cons

- Uses recursion stack
- Slightly more overhead than bottom-up DP

---

# Approach 4: Bottom-Up Interval DP (Classic Solution)

## Idea

Fill the DP table by increasing interval length.

Let:

- `dp[left][right]` = maximum coins obtainable by bursting all balloons strictly between `left` and `right`

If interval has no balloon inside:

```text
left + 1 == right
```

then:

```text
dp[left][right] = 0
```

Otherwise:

```text
dp[left][right] =
    max over k in (left, right):
        dp[left][k]
      + dp[k][right]
      + arr[left] * arr[k] * arr[right]
```

This is the standard textbook solution.

---

## Fill Order

We must compute smaller intervals before bigger ones.

So iterate by interval length:

- length = 2 → no real balloon inside
- length = 3
- length = 4
- ...
- length = n + 2

---

## Java Code

```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;

        for (int i = 0; i < n; i++) {
            arr[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int len = 2; len < n + 2; len++) {
            for (int left = 0; left + len < n + 2; left++) {
                int right = left + len;

                for (int k = left + 1; k < right; k++) {
                    dp[left][right] = Math.max(
                        dp[left][right],
                        dp[left][k] + dp[k][right] + arr[left] * arr[k] * arr[right]
                    );
                }
            }
        }

        return dp[0][n + 1];
    }
}
```

---

## Complexity

### Time Complexity

- **O(n^3)**

### Space Complexity

- **O(n^2)**

---

## Pros

- Standard answer
- No recursion
- Very interview-friendly
- Most common accepted optimal DP approach

## Cons

- Cubic time can look heavy, but is intended here

---

# Approach 5: Bottom-Up DP with Explicit Interval Size Interpretation

## Idea

Same as classic DP, but think in terms of the number of real balloons inside the interval.

This is not a different complexity class, but it is often easier to reason about.

Let:

- interval `(left, right)` contain balloons strictly inside it

If there is exactly one balloon `k` inside:

```text
dp[left][right] = arr[left] * arr[k] * arr[right]
```

If there are more, try each one as the last burst.

---

## Java Code

```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;

        for (int i = 1; i <= n; i++) {
            arr[i] = nums[i - 1];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int balloonsInside = 1; balloonsInside <= n; balloonsInside++) {
            for (int left = 0; left + balloonsInside + 1 < n + 2; left++) {
                int right = left + balloonsInside + 1;

                for (int k = left + 1; k < right; k++) {
                    int coins = dp[left][k]
                              + dp[k][right]
                              + arr[left] * arr[k] * arr[right];

                    dp[left][right] = Math.max(dp[left][right], coins);
                }
            }
        }

        return dp[0][n + 1];
    }
}
```

---

## Complexity

### Time Complexity

- **O(n^3)**

### Space Complexity

- **O(n^2)**

---

## Why Mention This?

Because many people understand interval DP better when framed by “how many balloons are inside the interval.”

---

# Approach 6: Top-Down DP with Zero Filtering Optimization

## Idea

If some balloons have value `0`, bursting them contributes no coins directly.

Although zero values still affect neighbor structure, in practice one common micro-optimization is to build the padded array while skipping zeros.

Why can this help?

- Bursting a zero-valued balloon last in an interval contributes `0` from its own multiplication
- Removing zeros reduces DP state size in many practical cases

This does **not** change the worst-case complexity class, but can reduce runtime significantly when many zeros exist.

---

## Java Code

```java
class Solution {
    public int maxCoins(int[] nums) {
        int nonZeroCount = 0;
        for (int num : nums) {
            if (num > 0) {
                nonZeroCount++;
            }
        }

        int[] arr = new int[nonZeroCount + 2];
        arr[0] = 1;
        arr[nonZeroCount + 1] = 1;

        int idx = 1;
        for (int num : nums) {
            if (num > 0) {
                arr[idx++] = num;
            }
        }

        Integer[][] memo = new Integer[nonZeroCount + 2][nonZeroCount + 2];
        return solve(arr, 0, nonZeroCount + 1, memo);
    }

    private int solve(int[] arr, int left, int right, Integer[][] memo) {
        if (left + 1 == right) {
            return 0;
        }

        if (memo[left][right] != null) {
            return memo[left][right];
        }

        int best = 0;

        for (int k = left + 1; k < right; k++) {
            int coins = solve(arr, left, k, memo)
                      + solve(arr, k, right, memo)
                      + arr[left] * arr[k] * arr[right];

            best = Math.max(best, coins);
        }

        memo[left][right] = best;
        return best;
    }
}
```

---

## Complexity

Let `m` be the number of non-zero balloons.

### Time Complexity

- **O(m^3)**

### Space Complexity

- **O(m^2)**

Worst case still:

- **O(n^3)** time
- **O(n^2)** space

---

## Important Note

This is an optimization, not a new algorithmic idea.

It helps only when zeros are common.

---

# Why Greedy Approaches Fail

A tempting idea is:

- burst balloon with maximum value first
- or burst balloon giving maximum immediate coins first
- or burst smallest balloon first

These fail because:

- immediate gain says nothing about future neighbor structure
- bursting a balloon changes the context for all later moves

Example:

```text
nums = [3,1,5,8]
```

Bursting `8` early can destroy better future combinations.

This is a strong sign that interval DP is needed.

---

# Why “Last Balloon” Works but “First Balloon” Does Not

This is the heart of the problem.

## If you burst first

The neighbors after removal are uncertain and depend on future bursts.

So the subproblems are not independent.

## If you burst last in an interval

At that moment:

- everything else inside the interval is already gone
- the boundary balloons are fixed
- gain is exactly:

```text
arr[left] * arr[k] * arr[right]
```

Now left and right intervals become independent.

This is why the reversal is essential.

---

# Full Derivation of the Recurrence

Let:

- `dp[left][right]` = maximum coins from bursting all balloons strictly between `left` and `right`

Suppose `k` is the **last** balloon to burst inside `(left, right)`.

Then:

1. First burst everything in `(left, k)`
2. First burst everything in `(k, right)`
3. Finally burst `k`

Coins obtained:

```text
dp[left][k]
+ dp[k][right]
+ arr[left] * arr[k] * arr[right]
```

Take maximum over all valid `k`:

```text
dp[left][right] =
    max_{left < k < right} (
        dp[left][k]
      + dp[k][right]
      + arr[left] * arr[k] * arr[right]
    )
```

This is the central recurrence.

---

# Comparing All Approaches

| Approach                    |                    Time |      Space | Notes                                  |
| --------------------------- | ----------------------: | ---------: | -------------------------------------- |
| Simulate Bursting Order     | Factorial / exponential |  Very high | Direct but hopeless                    |
| Recursion on Intervals      |             Exponential | O(n) stack | Right recurrence, no caching           |
| Top-Down Memoization        |                  O(n^3) |     O(n^2) | Elegant recursive DP                   |
| Bottom-Up Interval DP       |                  O(n^3) |     O(n^2) | Standard best answer                   |
| Interval Size Framing       |                  O(n^3) |     O(n^2) | Same algorithm, clearer interpretation |
| Zero-Filtering Optimization |                  O(m^3) |     O(m^2) | Practical speedup only                 |

---

# Recommended Final Java Solution

## Best Practical / Standard Version

```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;

        for (int i = 0; i < n; i++) {
            arr[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int len = 2; len < n + 2; len++) {
            for (int left = 0; left + len < n + 2; left++) {
                int right = left + len;

                for (int k = left + 1; k < right; k++) {
                    dp[left][right] = Math.max(
                        dp[left][right],
                        dp[left][k] + dp[k][right] + arr[left] * arr[k] * arr[right]
                    );
                }
            }
        }

        return dp[0][n + 1];
    }
}
```

---

## Complexity of Recommended Solution

- **Time Complexity:** `O(n^3)`
- **Space Complexity:** `O(n^2)`

For `n <= 300`, this is the intended accepted solution.

---

# Dry Run on Example 1

Input:

```text
nums = [3,1,5,8]
```

Padded:

```text
arr = [1,3,1,5,8,1]
```

We want:

```text
dp[0][5]
```

---

## Small intervals first

### Interval `(0,2)`

Only `k = 1`

```text
dp[0][2] = 1 * 3 * 1 = 3
```

### Interval `(1,3)`

Only `k = 2`

```text
dp[1][3] = 3 * 1 * 5 = 15
```

### Interval `(2,4)`

Only `k = 3`

```text
dp[2][4] = 1 * 5 * 8 = 40
```

### Interval `(3,5)`

Only `k = 4`

```text
dp[3][5] = 5 * 8 * 1 = 40
```

---

## Larger intervals

### Interval `(0,3)`

Try:

- `k = 1`
- `k = 2`

For `k = 1`:

```text
0 + 15 + 1*3*5 = 30
```

For `k = 2`:

```text
3 + 0 + 1*1*5 = 8
```

So:

```text
dp[0][3] = 30
```

---

### Interval `(1,4)`

For `k = 2`:

```text
0 + 40 + 3*1*8 = 64
```

For `k = 3`:

```text
15 + 0 + 3*5*8 = 135
```

So:

```text
dp[1][4] = 135
```

---

### Interval `(2,5)`

For `k = 3`:

```text
0 + 40 + 1*5*1 = 45
```

For `k = 4`:

```text
40 + 0 + 1*8*1 = 48
```

So:

```text
dp[2][5] = 48
```

---

## Final interval `(0,5)`

Try all `k`:

### `k = 1`

```text
dp[0][1] + dp[1][5] + 1*3*1
= 0 + 159 + 3
= 162
```

### `k = 2`

```text
dp[0][2] + dp[2][5] + 1*1*1
= 3 + 48 + 1
= 52
```

### `k = 3`

```text
dp[0][3] + dp[3][5] + 1*5*1
= 30 + 40 + 5
= 75
```

### `k = 4`

```text
dp[0][4] + dp[4][5] + 1*8*1
= 159 + 0 + 8
= 167
```

Final:

```text
dp[0][5] = 167
```

Correct.

---

# Common Mistakes

## Mistake 1: Thinking greedily

Local best burst does not imply global best answer.

## Mistake 2: Trying to burst first in DP

This makes neighbors unstable and subproblems dependent.

## Mistake 3: Forgetting boundary `1`s

Padding is essential for clean recurrence.

## Mistake 4: Misunderstanding interval meaning

`dp[left][right]` means:

- burst all balloons strictly **inside**
- not including boundaries

That distinction is crucial.

## Mistake 5: Wrong fill order

In bottom-up DP, smaller intervals must be solved before larger ones.

---

# Pattern Recognition

This problem is a classic:

- **interval DP**
- similar spirit to matrix chain multiplication
- “choose the last operation in a segment”

You should suspect interval DP when:

- order matters
- removing or combining elements changes future interactions
- reversing the process makes boundaries fixed

Related patterns:

- Matrix Chain Multiplication
- Minimum Score Triangulation of Polygon
- Merge Stones
- Strange Printer

---

# Interview Explanation Template

A strong explanation:

1. Bursting a balloon first is hard because neighbors change dynamically.
2. Instead, define `dp[left][right]` as the maximum coins from bursting balloons strictly between `left` and `right`.
3. Try every balloon `k` in that interval as the **last** balloon to burst.
4. Then the gain from `k` is fixed:
   `arr[left] * arr[k] * arr[right]`
5. Add optimal answers from left and right subintervals.
6. This gives:
   `dp[left][right] = max(dp[left][k] + dp[k][right] + arr[left]*arr[k]*arr[right])`
7. Complexity is `O(n^3)` time and `O(n^2)` space.

---

# Final Takeaway

The real trick is not bursting balloons.

It is this reversal:

> Do not decide who bursts first. Decide who bursts last in each interval.

That transforms a messy dynamic-neighbor problem into a clean interval DP problem.

The best practical solution is the classic **O(n^3)** interval DP.
