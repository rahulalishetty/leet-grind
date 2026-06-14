# House Robber II (LeetCode 213) — Exhaustive Java Approaches

## Problem Restatement

You are given an integer array `nums` where `nums[i]` is the money in the `i`th house.

Difference from House Robber I:

- The houses are arranged in a **circle**
- So the **first and last houses are adjacent**

Constraint:

- You cannot rob two adjacent houses

Goal:

- Return the maximum amount of money you can rob

---

## Why This Problem Is Different from House Robber I

In House Robber I, the houses are in a straight line.

Here, because the houses form a circle:

```text
0 -- 1 -- 2 -- ... -- n-2 -- n-1
|                              |
--------------------------------
```

That means:

- If you rob house `0`, you cannot rob house `n - 1`
- If you rob house `n - 1`, you cannot rob house `0`

This single extra circular constraint is the entire difficulty.

---

# Core Reduction

## Key Insight

In any valid solution:

- either you **do not rob the last house**
- or you **do not rob the first house**

So the circular problem can be reduced to **two linear subproblems**:

1. Rob houses in range `[0 ... n - 2]`
2. Rob houses in range `[1 ... n - 1]`

Then take the maximum:

```text
answer = max(
    robLinear(nums, 0, n - 2),
    robLinear(nums, 1, n - 1)
)
```

This is the central idea behind almost every correct solution.

---

## Why This Reduction Is Correct

Because the first and last houses are adjacent, a valid answer can never contain both.

So every valid solution must belong to one of these categories:

### Category 1: solutions that exclude the last house

Those are entirely inside `[0 ... n - 2]`

### Category 2: solutions that exclude the first house

Those are entirely inside `[1 ... n - 1]`

The optimal answer must be the better of these two categories.

This is not just a trick — it is a complete partition of all valid solutions.

---

# Approach 1: Pure Recursion After Circular Reduction

## Idea

First reduce the circular problem into two linear ranges.

Then solve each range recursively:

- rob current house and move `i + 2`
- skip current house and move `i + 1`

This is the most direct conceptual version.

---

## Recursive Definition

For a linear range, define:

- `solve(i, end)` = maximum money that can be robbed from index `i` to `end`

Then:

```text
solve(i, end) = max(
    nums[i] + solve(i + 2, end),
    solve(i + 1, end)
)
```

Base case:

- if `i > end`, return `0`

Circular answer:

```text
max(
    solve(0, n - 2),
    solve(1, n - 1)
)
```

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        return Math.max(
            solve(nums, 0, n - 2),
            solve(nums, 1, n - 1)
        );
    }

    private int solve(int[] nums, int i, int end) {
        if (i > end) {
            return 0;
        }

        int robCurrent = nums[i] + solve(nums, i + 2, end);
        int skipCurrent = solve(nums, i + 1, end);

        return Math.max(robCurrent, skipCurrent);
    }
}
```

---

## Complexity

### Time Complexity

- **O(2^n)** in the worst case

Even though we split into two ranges, each recursive call still branches heavily.

### Space Complexity

- **O(n)** recursion depth

---

## When to Use

Only for:

- understanding the decision tree
- deriving memoization
- seeing how the circular reduction works

Not practical for real use.

---

# Approach 2: Top-Down DP (Memoization) After Circular Reduction

## Idea

The recursive version recomputes the same states repeatedly.

So for each linear range, cache subproblem results.

---

## State

For one linear run:

- `memo[i]` = best answer starting from index `i`

For range `[start ... end]`, only states within that window matter.

We run memoized recursion twice:

- once for `[0 ... n - 2]`
- once for `[1 ... n - 1]`

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        int[] memo1 = new int[n];
        int[] memo2 = new int[n];
        Arrays.fill(memo1, -1);
        Arrays.fill(memo2, -1);

        int case1 = solve(nums, 0, n - 2, memo1);
        int case2 = solve(nums, 1, n - 1, memo2);

        return Math.max(case1, case2);
    }

    private int solve(int[] nums, int i, int end, int[] memo) {
        if (i > end) {
            return 0;
        }

        if (memo[i] != -1) {
            return memo[i];
        }

        int robCurrent = nums[i] + solve(nums, i + 2, end, memo);
        int skipCurrent = solve(nums, i + 1, end, memo);

        memo[i] = Math.max(robCurrent, skipCurrent);
        return memo[i];
    }
}
```

---

## Why This Works

Each subproblem `i` within a fixed linear range is solved once.

Since we do this for two ranges, total work is still linear.

---

## Complexity

### Time Complexity

- **O(n)**

Each index in each pass is computed once.

### Space Complexity

- **O(n)**
  - memo arrays
  - recursion stack

---

## Advantages

- Natural progression from recursion
- Easy to reason about
- Shows DP clearly

## Disadvantages

- Uses recursion stack
- Slightly heavier than iterative solution

---

# Approach 3: Bottom-Up DP Array After Circular Reduction

## Idea

Convert each of the two linear subproblems into iterative DP.

For a linear range:

- `dp[i]` = best answer up to that local position

Classic recurrence:

```text
dp[i] = max(dp[i - 1], value[i] + dp[i - 2])
```

Then compute:

- once for `[0 ... n-2]`
- once for `[1 ... n-1]`

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        return Math.max(
            robLinear(nums, 0, n - 2),
            robLinear(nums, 1, n - 1)
        );
    }

    private int robLinear(int[] nums, int start, int end) {
        int len = end - start + 1;

        if (len == 1) {
            return nums[start];
        }

        int[] dp = new int[len];
        dp[0] = nums[start];
        dp[1] = Math.max(nums[start], nums[start + 1]);

        for (int i = 2; i < len; i++) {
            int robCurrent = nums[start + i] + dp[i - 2];
            int skipCurrent = dp[i - 1];
            dp[i] = Math.max(robCurrent, skipCurrent);
        }

        return dp[len - 1];
    }
}
```

---

## Example Walkthrough

For:

```text
nums = [1, 2, 3, 1]
```

### Case 1: range `[0...2]` → `[1,2,3]`

- `dp[0] = 1`
- `dp[1] = 2`
- `dp[2] = max(2, 3 + 1) = 4`

Answer = `4`

### Case 2: range `[1...3]` → `[2,3,1]`

- `dp[0] = 2`
- `dp[1] = 3`
- `dp[2] = max(3, 1 + 2) = 3`

Answer = `3`

Final answer:

```text
max(4, 3) = 4
```

---

## Complexity

### Time Complexity

- **O(n)**

Two linear passes.

### Space Complexity

- **O(n)**

Because of DP arrays.

---

## Advantages

- No recursion
- Easy to trace
- Good for learning

## Disadvantages

- Uses unnecessary extra space

---

# Approach 4: Space-Optimized Bottom-Up DP (Recommended)

## Idea

For linear House Robber, the recurrence is:

```text
dp[i] = max(dp[i - 1], nums[i] + dp[i - 2])
```

To compute `dp[i]`, we only need:

- `dp[i - 1]`
- `dp[i - 2]`

So instead of a full array, keep just two variables.

Do that for both ranges:

- `[0 ... n - 2]`
- `[1 ... n - 1]`

Take the max.

This is the best practical solution.

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        return Math.max(
            robLinear(nums, 0, n - 2),
            robLinear(nums, 1, n - 1)
        );
    }

    private int robLinear(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;

        for (int i = start; i <= end; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

## Why This Works

Inside each linear range:

- `prev1` = best answer up to previous house
- `prev2` = best answer up to house before previous

At current house:

- skip current → `prev1`
- rob current → `nums[i] + prev2`

So:

```text
curr = max(prev1, nums[i] + prev2)
```

Then shift the window:

```text
prev2 = prev1
prev1 = curr
```

This is exactly the same logic as House Robber I, just applied twice because of the circle.

---

## Complexity

### Time Complexity

- **O(n)**

Two linear passes.

### Space Complexity

- **O(1)**

Only a few variables.

---

## Advantages

- Best practical solution
- Minimal memory
- Elegant and interview-friendly

## Disadvantages

- Slightly less visual than DP table for beginners

---

# Approach 5: Explicit Take / Skip State DP

## Idea

Instead of storing just “best so far,” store two states for a linear range:

- `take` = best total if current house is robbed
- `skip` = best total if current house is skipped

Transitions:

- `newTake = skip + nums[i]`
- `newSkip = max(skip, take)`

Run this over both valid ranges and take the max.

This is logically equivalent to the optimized DP approach, but the state meaning is more explicit.

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        return Math.max(
            robRange(nums, 0, n - 2),
            robRange(nums, 1, n - 1)
        );
    }

    private int robRange(int[] nums, int start, int end) {
        int take = 0;
        int skip = 0;

        for (int i = start; i <= end; i++) {
            int newTake = skip + nums[i];
            int newSkip = Math.max(skip, take);

            take = newTake;
            skip = newSkip;
        }

        return Math.max(take, skip);
    }
}
```

---

## Why This Formulation Is Valuable

This representation is useful because it generalizes well.

You can carry this thinking into:

- stock DP problems
- cooldown problems
- House Robber III
- problems with explicit “can take / cannot take” state

So even though this problem can be solved with simpler variables, this framing builds strong DP intuition.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)**

---

## Advantages

- Very expressive state meaning
- Good transfer learning to harder DP problems

## Disadvantages

- Slightly more abstract if you only want the simplest answer

---

# Approach 6: Reverse DP on Each Range

## Idea

Instead of solving from left to right, solve from right to left.

For a linear range:

- `dp[i]` = best answer starting at house `i`

Recurrence:

```text
dp[i] = max(
    nums[i] + dp[i + 2],
    dp[i + 1]
)
```

Run once for:

- `[0 ... n - 2]`
- `[1 ... n - 1]`

Then return the max.

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        return Math.max(
            robReverse(nums, 0, n - 2),
            robReverse(nums, 1, n - 1)
        );
    }

    private int robReverse(int[] nums, int start, int end) {
        int len = end - start + 1;
        int[] dp = new int[len + 2];

        for (int i = len - 1; i >= 0; i--) {
            int actualIndex = start + i;
            int robCurrent = nums[actualIndex] + dp[i + 2];
            int skipCurrent = dp[i + 1];
            dp[i] = Math.max(robCurrent, skipCurrent);
        }

        return dp[0];
    }
}
```

---

## Why Learn This Version

This mirrors the top-down recursive relation directly:

- recursive state: “best from index `i` onward”
- reverse DP: fill answers for index `i` from right to left

This is useful when translating recursion into iteration.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(n)**

---

# Approach 7: One Helper + Manual Case Splitting

## Idea

Some people like to write the circular logic very explicitly.

Instead of thinking in abstract ranges, define:

- `case1` = answer when excluding last house
- `case2` = answer when excluding first house

This is the same optimized solution, but written in a very interview-readable style.

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        int excludeLast = robLinear(nums, 0, n - 2);
        int excludeFirst = robLinear(nums, 1, n - 1);

        return Math.max(excludeLast, excludeFirst);
    }

    private int robLinear(int[] nums, int left, int right) {
        int prev2 = 0;
        int prev1 = 0;

        for (int i = left; i <= right; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

## Why Mention This Separately?

Because in interviews and production code, presentation matters.

This version makes the circular reduction visually obvious:

- “exclude last”
- “exclude first”

That helps reviewers immediately see the correctness argument.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)**

---

# Comparing All Approaches

| Approach                   |   Time | Space | Notes                     |
| -------------------------- | -----: | ----: | ------------------------- |
| Pure Recursion             | O(2^n) |  O(n) | Conceptual only           |
| Top-Down Memoization       |   O(n) |  O(n) | Good recursive DP version |
| Bottom-Up DP Array         |   O(n) |  O(n) | Standard iterative DP     |
| Space-Optimized DP         |   O(n) |  O(1) | Best practical solution   |
| Take / Skip State DP       |   O(n) |  O(1) | Great mental model        |
| Reverse DP                 |   O(n) |  O(n) | Mirrors recursion nicely  |
| Manual Case Split + Helper |   O(n) |  O(1) | Clean interview style     |

---

# Full Derivation of the Optimal Solution

## Step 1: Handle trivial size

If there is only one house:

```java
if (nums.length == 1) return nums[0];
```

Because circular adjacency does not matter.

---

## Step 2: Break circle

Because first and last are adjacent:

- cannot choose both

So optimal solution must be one of:

- best from houses `[0 ... n - 2]`
- best from houses `[1 ... n - 1]`

---

## Step 3: Solve each subproblem linearly

Linear recurrence:

```text
best(i) = max(best(i - 1), nums[i] + best(i - 2))
```

Interpretation:

- skip current house → keep previous best
- rob current house → add current value to best before previous

---

## Step 4: Compress space

Only `i - 1` and `i - 2` are needed.

So use:

- `prev1` for previous best
- `prev2` for best before previous

At each step:

```text
curr = max(prev1, nums[i] + prev2)
prev2 = prev1
prev1 = curr
```

That gives the final optimized answer.

---

# Recommended Final Java Solution

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        return Math.max(
            robLinear(nums, 0, n - 2),
            robLinear(nums, 1, n - 1)
        );
    }

    private int robLinear(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;

        for (int i = start; i <= end; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

## Complexity of Recommended Solution

- **Time Complexity:** `O(n)`
- **Space Complexity:** `O(1)`

---

# Dry Run of Recommended Solution

Take:

```text
nums = [1, 2, 3, 1]
```

## Case 1: Exclude last → range `[0...2]`

### Start

- `prev2 = 0`
- `prev1 = 0`

### i = 0, money = 1

- `curr = max(0, 1 + 0) = 1`
- update:
  - `prev2 = 0`
  - `prev1 = 1`

### i = 1, money = 2

- `curr = max(1, 2 + 0) = 2`
- update:
  - `prev2 = 1`
  - `prev1 = 2`

### i = 2, money = 3

- `curr = max(2, 3 + 1) = 4`
- update:
  - `prev2 = 2`
  - `prev1 = 4`

Case 1 result = `4`

---

## Case 2: Exclude first → range `[1...3]`

Values: `[2, 3, 1]`

### Start

- `prev2 = 0`
- `prev1 = 0`

### i = 1, money = 2

- `curr = max(0, 2 + 0) = 2`
- update:
  - `prev2 = 0`
  - `prev1 = 2`

### i = 2, money = 3

- `curr = max(2, 3 + 0) = 3`
- update:
  - `prev2 = 2`
  - `prev1 = 3`

### i = 3, money = 1

- `curr = max(3, 1 + 2) = 3`
- update:
  - `prev2 = 3`
  - `prev1 = 3`

Case 2 result = `3`

Final answer:

```text
max(4, 3) = 4
```

---

# Edge Cases

## 1. Single house

```text
nums = [5]
```

Answer = `5`

---

## 2. Two houses

```text
nums = [2, 7]
```

Since they are adjacent in a circle, you can only rob one.

Answer = `7`

---

## 3. Three houses

```text
nums = [2, 3, 2]
```

Cannot rob both `2`s because first and last are adjacent.

Answer = `3`

---

## 4. Increasing values

```text
nums = [1, 2, 3, 4, 5]
```

Circle constraint prevents simply taking alternating extremes blindly.

Need both linear cases.

---

## 5. All zeros

```text
nums = [0, 0, 0]
```

Answer = `0`

---

# Common Mistakes

## Mistake 1: Solving it exactly like House Robber I

This ignores the circular adjacency.

Example:

```text
[2, 3, 2]
```

Linear answer would incorrectly allow first and last together in some formulations.

You must split into two cases.

---

## Mistake 2: Forgetting the `n == 1` case

If you directly call:

```java
robLinear(nums, 0, n - 2)
robLinear(nums, 1, n - 1)
```

and `n == 1`, the ranges become invalid.

So handle it explicitly first.

---

## Mistake 3: Trying to keep first/last status inside one DP

This often leads to unnecessary complexity.

The two-range reduction is much cleaner and fully sufficient.

---

## Mistake 4: Greedy thinking

Choosing the locally larger adjacent house does not work.

This is a global optimization problem, not a greedy one.

---

# Pattern Recognition

This problem teaches an important transformation pattern:

> When an array problem becomes circular, break it into a small number of linear cases.

You will see this pattern often in:

- circular subarray problems
- circular adjacency constraints
- ring-based DP problems

Related problems:

- House Robber I
- House Robber III
- Delete and Earn
- Maximum sum of non-adjacent elements in circular array

---

# Interview Explanation Template

A strong concise explanation:

1. In the circular arrangement, the first and last houses are adjacent, so we cannot rob both.
2. Therefore, every valid solution must exclude either the first house or the last house.
3. That lets us reduce the problem to two standard linear House Robber problems:
   - rob houses `0` to `n-2`
   - rob houses `1` to `n-1`
4. For each linear case, use:
   `dp[i] = max(dp[i-1], nums[i] + dp[i-2])`
5. Since only the previous two states are needed, optimize space to `O(1)`.
6. Return the maximum of the two cases.

---

# Final Takeaway

House Robber II looks like a new problem, but it is really:

- **one structural twist** on House Robber I
- solved by a **case split**
- followed by the same linear DP

The main insight is:

> A circle becomes manageable when you break it into a few valid lines.

The best implementation in practice is the **space-optimized two-pass DP**.
