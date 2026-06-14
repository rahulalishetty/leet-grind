# House Robber (LeetCode 198) — Exhaustive Java Approaches

## Problem Restatement

You are given an array `nums` where `nums[i]` is the money in the `i`th house.

Constraint:

- You **cannot rob two adjacent houses**.

Goal:

- Return the **maximum amount of money** you can rob.

---

## Core Observation

At every house `i`, you only have two meaningful choices:

1. **Rob this house**
   - Then you **must skip** house `i - 1`
   - Total becomes:
     `nums[i] + best till i-2`

2. **Skip this house**
   - Then total remains:
     `best till i-1`

So the recurrence is:

```text
dp[i] = max(
    dp[i - 1],          // skip current house
    nums[i] + dp[i - 2] // rob current house
)
```

This is the entire problem.

---

# Approach 1: Pure Recursion (Brute Force)

## Idea

For each index, try both possibilities:

- rob current house and jump to `i + 2`
- skip current house and move to `i + 1`

Take the maximum.

This is the most direct way to think about the problem.

---

## Recursive Definition

Let `solve(i)` = maximum money that can be robbed starting from house `i`.

Then:

```text
solve(i) = max(
    nums[i] + solve(i + 2),
    solve(i + 1)
)
```

Base case:

- if `i >= nums.length`, return `0`

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        return solve(nums, 0);
    }

    private int solve(int[] nums, int i) {
        if (i >= nums.length) {
            return 0;
        }

        int robCurrent = nums[i] + solve(nums, i + 2);
        int skipCurrent = solve(nums, i + 1);

        return Math.max(robCurrent, skipCurrent);
    }
}
```

---

## Example Walkthrough

For `nums = [2, 7, 9, 3, 1]`

At index `0`:

- rob `2` → solve from index `2`
- skip `2` → solve from index `1`

This causes many repeated calls:

- `solve(2)` gets computed multiple times
- `solve(3)` gets computed multiple times
- and so on

That repeated work is the main problem.

---

## Complexity

### Time Complexity

- **O(2^n)** in the worst case

Because each house branches into two choices.

### Space Complexity

- **O(n)** recursion stack depth in the worst case

---

## When to Use

Almost never in production for this problem.
Useful only for:

- understanding the state transition
- deriving memoization and DP

---

# Approach 2: Top-Down DP (Memoization)

## Idea

The brute-force recursion repeats the same subproblems.

So we cache results:

- once `solve(i)` is computed, store it
- next time `solve(i)` is needed, return cached value

This converts exponential recursion into linear time.

---

## State

Let:

- `memo[i]` = answer for subproblem starting at index `i`

If `memo[i] != -1`, return it directly.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int rob(int[] nums) {
        int[] memo = new int[nums.length];
        Arrays.fill(memo, -1);
        return solve(nums, 0, memo);
    }

    private int solve(int[] nums, int i, int[] memo) {
        if (i >= nums.length) {
            return 0;
        }

        if (memo[i] != -1) {
            return memo[i];
        }

        int robCurrent = nums[i] + solve(nums, i + 2, memo);
        int skipCurrent = solve(nums, i + 1, memo);

        memo[i] = Math.max(robCurrent, skipCurrent);
        return memo[i];
    }
}
```

---

## Why This Works

Each index `i` is solved once.

At every index:

- compute best if robbed
- compute best if skipped
- store the maximum

Since there are only `n` possible starting indices, total work becomes linear.

---

## Complexity

### Time Complexity

- **O(n)**

Each state `i` is computed once.

### Space Complexity

- **O(n)**
  - `O(n)` for memo array
  - `O(n)` recursion stack in worst case

---

## Advantages

- Easy transition from recursion
- Very intuitive
- Good when you naturally think recursively

## Disadvantages

- Uses recursion stack
- Slightly less space-efficient than iterative DP

---

# Approach 3: Bottom-Up DP with Array

## Idea

Instead of solving from the top recursively, build the answer iteratively.

Let:

- `dp[i]` = maximum money that can be robbed from houses `0...i`

Then:

```text
dp[i] = max(dp[i - 1], nums[i] + dp[i - 2])
```

---

## Base Cases

For array indexing:

- `dp[0] = nums[0]`
- `dp[1] = max(nums[0], nums[1])`

Then fill from `i = 2` onward.

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        int[] dp = new int[n];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            int robCurrent = nums[i] + dp[i - 2];
            int skipCurrent = dp[i - 1];
            dp[i] = Math.max(robCurrent, skipCurrent);
        }

        return dp[n - 1];
    }
}
```

---

## Example Walkthrough

For `nums = [2, 7, 9, 3, 1]`

### Initialization

- `dp[0] = 2`
- `dp[1] = max(2, 7) = 7`

### Fill

- `dp[2] = max(dp[1], nums[2] + dp[0]) = max(7, 9 + 2) = 11`
- `dp[3] = max(dp[2], nums[3] + dp[1]) = max(11, 3 + 7) = 11`
- `dp[4] = max(dp[3], nums[4] + dp[2]) = max(11, 1 + 11) = 12`

Answer = `12`

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(n)** for the DP array

---

## Advantages

- No recursion
- Easy to trace
- Very standard DP formulation

## Disadvantages

- Uses extra array even though only last two states are needed

---

# Approach 4: Bottom-Up DP with Space Optimization

## Idea

Look at the recurrence again:

```text
dp[i] = max(dp[i - 1], nums[i] + dp[i - 2])
```

To compute `dp[i]`, we only need:

- `dp[i - 1]`
- `dp[i - 2]`

So we do **not** need the full array.

We can keep just two variables.

---

## Variables

Let:

- `prev2` = `dp[i - 2]`
- `prev1` = `dp[i - 1]`

Then for each house:

- `curr = max(prev1, nums[i] + prev2)`

After that:

- shift variables forward

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return nums[0];
        }

        int prev2 = nums[0];
        int prev1 = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

## Slightly Cleaner Variant

This version treats the running answer more generically.

```java
class Solution {
    public int rob(int[] nums) {
        int prev2 = 0; // dp[i - 2]
        int prev1 = 0; // dp[i - 1]

        for (int money : nums) {
            int curr = Math.max(prev1, money + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

This is often considered the cleanest solution.

---

## Why the Cleaner Variant Works

Suppose:

- `prev1` = best answer up to previous house
- `prev2` = best answer up to house before previous

For current `money`:

- skip current → `prev1`
- rob current → `money + prev2`

Take max, then slide window forward.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)**

---

## Advantages

- Best practical solution
- Minimal memory
- Clean and efficient

## Disadvantages

- Slightly less obvious than full DP array for beginners

---

# Approach 5: DP by Explicit “Take / Skip” States

## Idea

Instead of saying `dp[i] = best till i`, we can model two states at every house:

- `take[i]` = maximum money if we **must rob** house `i`
- `skip[i]` = maximum money if we **do not rob** house `i`

Transitions:

- `take[i] = skip[i - 1] + nums[i]`
- `skip[i] = max(skip[i - 1], take[i - 1])`

Final answer:

- `max(take[n - 1], skip[n - 1])`

This is logically equivalent, but sometimes easier to reason about.

---

## Java Code (Array Version)

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        int[] take = new int[n];
        int[] skip = new int[n];

        take[0] = nums[0];
        skip[0] = 0;

        for (int i = 1; i < n; i++) {
            take[i] = skip[i - 1] + nums[i];
            skip[i] = Math.max(skip[i - 1], take[i - 1]);
        }

        return Math.max(take[n - 1], skip[n - 1]);
    }
}
```

---

## Space-Optimized Version

```java
class Solution {
    public int rob(int[] nums) {
        int take = 0;
        int skip = 0;

        for (int money : nums) {
            int newTake = skip + money;
            int newSkip = Math.max(skip, take);

            take = newTake;
            skip = newSkip;
        }

        return Math.max(take, skip);
    }
}
```

---

## Why This Formulation Is Useful

This framing becomes very helpful in related problems where the state is more explicit:

- stock DP problems
- cooldown constraints
- circular robber
- tree robber

So while it is not necessary here, it is a very transferable way to think.

---

## Complexity

### Array Version

- Time: **O(n)**
- Space: **O(n)**

### Optimized Version

- Time: **O(n)**
- Space: **O(1)**

---

# Approach 6: Reverse DP

## Idea

Instead of defining answer from left to right, define:

- `dp[i]` = best answer starting from house `i`

Then:

```text
dp[i] = max(
    nums[i] + dp[i + 2],
    dp[i + 1]
)
```

This is the same logic as top-down memoization, but implemented iteratively from right to left.

---

## Java Code

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n + 2];

        for (int i = n - 1; i >= 0; i--) {
            int robCurrent = nums[i] + dp[i + 2];
            int skipCurrent = dp[i + 1];
            dp[i] = Math.max(robCurrent, skipCurrent);
        }

        return dp[0];
    }
}
```

---

## Why `n + 2`?

Because for the last indices, we access:

- `dp[i + 1]`
- `dp[i + 2]`

Adding two extra cells initialized to `0` avoids messy boundary checks.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(n)**

---

## Why Learn This Version?

Because it matches the recursive relation directly:

- top-down: `solve(i)`
- bottom-up reverse: fill `dp[i]` from end toward start

This often makes transitions between recursion and iteration easier.

---

# Comparing All Approaches

| Approach             |   Time |        Space | Notes                                   |
| -------------------- | -----: | -----------: | --------------------------------------- |
| Pure Recursion       | O(2^n) |         O(n) | Conceptual only                         |
| Top-Down Memoization |   O(n) |         O(n) | Great for recursive thinking            |
| Bottom-Up DP Array   |   O(n) |         O(n) | Most standard table DP                  |
| Space-Optimized DP   |   O(n) |         O(1) | Best practical solution                 |
| Take/Skip State DP   |   O(n) | O(1) or O(n) | Great mental model for related problems |
| Reverse DP           |   O(n) |         O(n) | Mirrors recursive recurrence nicely     |

---

# Which Approach Should You Prefer?

## For interviews

Best choice:

- **Space-optimized DP**

Because it is:

- efficient
- elegant
- easy to explain after deriving recurrence

## For learning DP

Best progression:

1. pure recursion
2. memoization
3. bottom-up table
4. space optimization

This teaches the full DP thought process rather than just memorizing the final code.

## For transfer to harder problems

The **take/skip state model** is extremely useful because it generalizes well.

---

# Full Derivation of the Optimal Iterative Solution

Let:

- `prev2` = best answer up to house `i - 2`
- `prev1` = best answer up to house `i - 1`

At house `i`, choices are:

### Skip current

Then total remains `prev1`

### Rob current

Then total becomes `nums[i] + prev2`

So:

```text
curr = max(prev1, nums[i] + prev2)
```

Then move forward:

```text
prev2 = prev1
prev1 = curr
```

That is the complete optimized DP.

---

# Recommended Final Java Solution

```java
class Solution {
    public int rob(int[] nums) {
        int prev2 = 0;
        int prev1 = 0;

        for (int money : nums) {
            int curr = Math.max(prev1, money + prev2);
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

For `nums = [2, 7, 9, 3, 1]`

Initial:

- `prev2 = 0`
- `prev1 = 0`

### House 0, money = 2

- `curr = max(0, 2 + 0) = 2`
- update:
  - `prev2 = 0`
  - `prev1 = 2`

### House 1, money = 7

- `curr = max(2, 7 + 0) = 7`
- update:
  - `prev2 = 2`
  - `prev1 = 7`

### House 2, money = 9

- `curr = max(7, 9 + 2) = 11`
- update:
  - `prev2 = 7`
  - `prev1 = 11`

### House 3, money = 3

- `curr = max(11, 3 + 7) = 11`
- update:
  - `prev2 = 11`
  - `prev1 = 11`

### House 4, money = 1

- `curr = max(11, 1 + 11) = 12`
- update:
  - `prev2 = 11`
  - `prev1 = 12`

Answer = `12`

---

# Edge Cases

## 1. Single house

Example:

```text
nums = [5]
```

Answer = `5`

## 2. All zeros

Example:

```text
nums = [0, 0, 0]
```

Answer = `0`

## 3. Two houses

Example:

```text
nums = [2, 10]
```

Answer = `10`

## 4. Large alternating values

Example:

```text
nums = [100, 1, 100, 1, 100]
```

Answer = `300`

---

# Common Mistakes

## Mistake 1: Greedy choice

A tempting but incorrect idea:

- always rob the bigger of two adjacent houses

This fails because local decisions do not guarantee global optimum.

Example:

```text
[2, 1, 1, 2]
```

Correct answer = `4`, by robbing house 0 and house 3.

A naive greedy approach can miss that.

---

## Mistake 2: Forgetting the “skip current” case

Some people only think:

```text
dp[i] = nums[i] + dp[i - 2]
```

But that ignores the possibility that skipping current is better.

The correct recurrence must compare:

```text
max(dp[i - 1], nums[i] + dp[i - 2])
```

---

## Mistake 3: Boundary bugs

In bottom-up DP:

- handle `n == 1`
- initialize first entries properly

In reverse DP:

- allocate `n + 2` if you want simpler indexing

---

# Pattern Recognition

This problem is one of the cleanest examples of:

- **1D DP**
- **pick / not-pick DP**
- **adjacency constraint DP**
- **state compression**

You should recognize this pattern whenever:

- choosing one item blocks adjacent items
- each position depends on nearby previous positions
- the recurrence involves “take vs skip”

Related problems:

- House Robber II
- House Robber III
- Delete and Earn
- Maximum sum of non-adjacent elements

---

# Interview Explanation Template

A clean way to explain it in an interview:

1. At each house, I have two choices:
   - rob it, so I must skip previous
   - skip it, so I keep previous best

2. That gives recurrence:
   `dp[i] = max(dp[i - 1], nums[i] + dp[i - 2])`

3. Since only the last two states are needed, I can optimize space to `O(1)`.

4. Final complexity:
   - Time: `O(n)`
   - Space: `O(1)`

---

# Final Takeaway

The heart of the problem is not robbery.
It is this:

> From every position, choose between **taking current and skipping adjacent**, or **skipping current and keeping prior optimum**.

Once that becomes clear, every version of the solution becomes mechanical.

The best implementation in practice is the **space-optimized DP** version.
