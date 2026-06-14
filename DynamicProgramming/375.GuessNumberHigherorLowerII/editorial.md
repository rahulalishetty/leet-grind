# 375. Guess Number Higher or Lower II

## Problem Restatement

You need to guess a number between `1` and `n`.

- If you guess correctly, you pay nothing for that guess.
- If you guess wrongly with value `x`, you pay exactly `x` dollars.
- After each wrong guess, you are told whether the target is higher or lower.
- You want the **minimum amount of money required to guarantee a win in the worst case**.

This is a **minimax dynamic programming** problem.

---

# Core Insight

This is **not** asking for:

- minimum expected cost
- minimum guesses
- average-case strategy

It is asking for:

> the minimum money needed to **guarantee** a win against the worst possible hidden number.

That means whenever we choose a pivot `x`, the real cost is:

```text
x + max(cost of left side, cost of right side)
```

Why `max`?

Because after guessing `x`, the number could be on either side, and we must survive the **worse** side.

Then among all possible guesses `x`, we choose the one with **minimum** worst-case cost.

So the recurrence is:

```text
dp[l][r] = min over x in [l..r] of ( x + max(dp[l][x-1], dp[x+1][r]) )
```

Base case:

```text
dp[l][r] = 0 when l >= r
```

because if there is at most one number left, no money is required to guarantee the answer.

---

# Approach 1: Pure Brute Force Recursion

## Idea

Try every possible first guess for range `[l, r]`.

For each guess `x`:

- left interval becomes `[l, x-1]`
- right interval becomes `[x+1, r]`
- because the hidden number can force us into the worse branch, cost becomes:

```text
x + max(solve(l, x-1), solve(x+1, r))
```

Take the minimum over all `x`.

---

## Why it works

It directly models the decision process.

- We try all legal first guesses.
- For each guess, we assume the adversarial worst-case branch.
- Then we choose the guess minimizing this worst-case cost.

This exactly matches the problem.

---

## Recurrence

```text
solve(l, r) = 0, if l >= r

solve(l, r) = min over x in [l..r] of:
              x + max(solve(l, x-1), solve(x+1, r))
```

---

## Time Complexity

This is extremely expensive because the same subranges are recomputed again and again.

The exact recurrence grows super-polynomially. Practically, it becomes unusable quickly.

A safe way to describe it:

```text
Time: Exponential
Space: O(n) recursion depth
```

---

## Java Code

```java
class Solution {
    public int getMoneyAmount(int n) {
        return solve(1, n);
    }

    private int solve(int left, int right) {
        if (left >= right) {
            return 0;
        }

        int ans = Integer.MAX_VALUE;

        for (int guess = left; guess <= right; guess++) {
            int costLeft = solve(left, guess - 1);
            int costRight = solve(guess + 1, right);
            int worstCase = guess + Math.max(costLeft, costRight);
            ans = Math.min(ans, worstCase);
        }

        return ans;
    }
}
```

---

## Verdict

Useful only for discovering the recurrence.

Not practical.

---

# Approach 2: Top-Down Dynamic Programming (Memoization)

## Idea

The brute-force recursion repeats the same subproblems many times.

For example, `solve(4, 7)` may be computed from several different branches.

So we memoize:

```text
memo[l][r] = answer for range [l, r]
```

Now each interval is solved once.

---

## Why this is the key improvement

The state is fully determined by the current search interval `[l, r]`.

There are only `O(n^2)` such intervals.

For each interval, we try every possible first guess.

That gives a polynomial solution.

---

## Time Complexity

Number of states:

```text
O(n^2)
```

Transition per state:

```text
O(n)
```

So:

```text
Time: O(n^3)
Space: O(n^2) + O(n) recursion stack
```

For `n <= 200`, this is fully acceptable.

---

## Java Code

```java
class Solution {
    public int getMoneyAmount(int n) {
        int[][] memo = new int[n + 1][n + 1];
        return solve(1, n, memo);
    }

    private int solve(int left, int right, int[][] memo) {
        if (left >= right) {
            return 0;
        }

        if (memo[left][right] != 0) {
            return memo[left][right];
        }

        int ans = Integer.MAX_VALUE;

        for (int guess = left; guess <= right; guess++) {
            int costLeft = solve(left, guess - 1, memo);
            int costRight = solve(guess + 1, right, memo);
            int worstCase = guess + Math.max(costLeft, costRight);
            ans = Math.min(ans, worstCase);
        }

        memo[left][right] = ans;
        return ans;
    }
}
```

---

## Walkthrough for `n = 4`

We want `solve(1, 4)`.

Try each first guess:

### Guess 1

Cost:

```text
1 + solve(2, 4)
```

because left side is empty.

### Guess 2

Cost:

```text
2 + max(solve(1, 1), solve(3, 4))
= 2 + max(0, solve(3, 4))
```

### Guess 3

Cost:

```text
3 + max(solve(1, 2), solve(4, 4))
= 3 + max(solve(1, 2), 0)
```

### Guess 4

Cost:

```text
4 + solve(1, 3)
```

Take the minimum among them.

Memoization ensures ranges like `solve(1, 2)` and `solve(3, 4)` are reused.

---

## Verdict

This is the most natural correct solution once you see the recurrence.

---

# Approach 3: Bottom-Up Interval DP

## Idea

Instead of recursion, compute answers for smaller ranges first, then build larger ranges.

This is classic **interval DP**.

Let:

```text
dp[left][right] = minimum money required to guarantee a win for range [left, right]
```

Base case:

```text
dp[left][right] = 0 when left >= right
```

Then fill intervals by increasing length.

---

## Transition

For every interval `[left, right]`, try every possible first guess `guess`:

```text
cost = guess + max(dp[left][guess - 1], dp[guess + 1][right])
dp[left][right] = min(dp[left][right], cost)
```

This is exactly the same logic as memoization, just iteratively.

---

## Fill Order

The important detail is:

- smaller intervals must be solved before larger intervals

So we iterate by interval length:

- length = 2
- length = 3
- ...
- length = n

---

## Time Complexity

Again:

- states = `O(n^2)`
- transitions per state = `O(n)`

So:

```text
Time: O(n^3)
Space: O(n^2)
```

---

## Java Code

```java
class Solution {
    public int getMoneyAmount(int n) {
        int[][] dp = new int[n + 2][n + 2];

        for (int len = 2; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                dp[left][right] = Integer.MAX_VALUE;

                for (int guess = left; guess <= right; guess++) {
                    int costLeft = (guess > left) ? dp[left][guess - 1] : 0;
                    int costRight = (guess < right) ? dp[guess + 1][right] : 0;
                    int worstCase = guess + Math.max(costLeft, costRight);
                    dp[left][right] = Math.min(dp[left][right], worstCase);
                }
            }
        }

        return dp[1][n];
    }
}
```

---

## Why many people prefer this

- No recursion overhead
- No stack depth concerns
- Makes the interval dependency very explicit
- Often easier to reason about in interviews if you are comfortable with interval DP

---

# Approach 4: Bottom-Up DP with Transition Pruning at Boundaries

## Idea

Observe that guessing `left` or `right` is rarely optimal for larger ranges, but from a correctness standpoint, we can still consider all guesses.

A slightly refined implementation can avoid awkward boundary checks by iterating only meaningful interior structure while still preserving correctness.

This does **not** change the asymptotic complexity. It is only a code-clarity or small constant-factor improvement.

---

## Example Cleaner Implementation

```java
class Solution {
    public int getMoneyAmount(int n) {
        int[][] dp = new int[n + 2][n + 2];

        for (int len = 2; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                int best = Integer.MAX_VALUE;

                for (int guess = left; guess <= right; guess++) {
                    int worst = guess + Math.max(dp[left][guess - 1], dp[guess + 1][right]);
                    best = Math.min(best, worst);
                }

                dp[left][right] = best;
            }
        }

        return dp[1][n];
    }
}
```

Because `dp` is declared slightly larger (`n + 2`), accesses like `dp[left][left - 1]` safely behave as `0`.

---

## Complexity

```text
Time: O(n^3)
Space: O(n^2)
```

---

## Verdict

Same algorithm as interval DP, just a cleaner implementation style.

---

# Approach 5: Why Greedy Does Not Work

## Tempting wrong idea

You might think:

- always guess the middle
- like binary search

That would minimize the number of guesses, so maybe it minimizes money too.

But this problem is **not minimizing guesses**. It is minimizing the sum of wrong guessed values in the worst case.

These are very different objectives.

---

## Counterexample intuition

Suppose the interval is `[1, 10]`.

Binary search would suggest guessing around `5` or `6`.

But the optimal answer for `n = 10` starts with `7`, not `5`.

Why?

Because larger guesses cost more **when they are wrong**, but they can drastically reduce worst-case exposure on one side. The objective is a balance between:

- immediate guess cost
- worst-case future interval cost

That balance is not captured by simple midpoint logic.

---

## Important lesson

This is a classic sign of minimax DP:

> local balance does not imply global optimality

So:

- greedy midpoint => not reliable
- binary search => not optimal here
- must use DP

---

# Approach 6: Minimax Game Interpretation

## Another way to view the same DP

This problem can be seen as a two-player style game:

- **You** choose a guess to minimize total guaranteed payment
- **Nature / adversary** chooses the side that maximizes your payment

That yields:

```text
min over guesses (
    guess + max(left subproblem, right subproblem)
)
```

This is exactly the minimax structure.

---

## Why this mental model is useful

It helps you immediately identify:

- choose action => `min`
- worst branch => `max`

Whenever you see:

- "guarantee"
- "regardless of what happens"
- "worst case"

you should become suspicious that minimax DP may be involved.

---

# Can We Reduce Space?

The DP state is `dp[left][right]`, and intervals depend on many smaller intervals with different boundaries.

That means this is a genuine 2D interval dependency graph.

So unlike some DP problems, you generally **cannot compress it cleanly to 1D** without losing needed subproblems.

Thus the practical space complexity remains:

```text
O(n^2)
```

---

# Can We Improve Time Below O(n^3)?

For this problem as typically solved in interviews and LeetCode, the standard answer is:

```text
O(n^3) time, O(n^2) space
```

There are advanced optimizations for some interval DP problems when the optimal split point is monotonic or satisfies quadrangle inequality / Knuth optimization style properties.

For this problem, that is not the usual route expected in interviews, and the standard accepted solution is the cubic DP.

So the practical answer to remember is:

- **Top-down memoization** or
- **Bottom-up interval DP**

both with:

```text
Time: O(n^3)
Space: O(n^2)
```

---

# Comparison of Approaches

| Approach               | Idea                        |        Time |          Space | Practical?          |
| ---------------------- | --------------------------- | ----------: | -------------: | ------------------- |
| Pure Recursion         | Try every pivot recursively | Exponential |     O(n) stack | No                  |
| Top-Down Memoization   | Cache interval answers      |      O(n^3) | O(n^2) + stack | Yes                 |
| Bottom-Up Interval DP  | Fill intervals by length    |      O(n^3) |         O(n^2) | Yes, best iterative |
| Greedy / Binary Search | Guess middle                |   Incorrect |           O(1) | No                  |

---

# Best Interview Progression

A strong explanation sequence is:

1. Define subproblem on interval `[l, r]`
2. If you guess `x`, worst-case cost is:
   ```text
   x + max(dp[l][x-1], dp[x+1][r])
   ```
3. Minimize across all guesses `x`
4. Base case: no uncertainty => zero cost
5. Use memoization or interval DP

That shows both:

- problem modeling skill
- DP formulation skill

---

# Final Recommended Solution: Bottom-Up Interval DP

```java
class Solution {
    public int getMoneyAmount(int n) {
        int[][] dp = new int[n + 2][n + 2];

        for (int len = 2; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                dp[left][right] = Integer.MAX_VALUE;

                for (int guess = left; guess <= right; guess++) {
                    int worstCase = guess + Math.max(dp[left][guess - 1], dp[guess + 1][right]);
                    dp[left][right] = Math.min(dp[left][right], worstCase);
                }
            }
        }

        return dp[1][n];
    }
}
```

---

# Detailed Complexity of the Best Solution

## Time Complexity

There are about `n^2 / 2` intervals.

For each interval, we try all possible guesses, up to `O(n)`.

So:

```text
Time = O(n^3)
```

For `n <= 200`, this is absolutely fine.

---

## Space Complexity

We store a 2D DP table:

```text
dp[left][right]
```

So:

```text
Space = O(n^2)
```

---

# Worked Example: `n = 4`

We compute:

## Length 2 intervals

- `dp[1][2]`
  - guess 1 => `1 + dp[2][2] = 1`
  - guess 2 => `2 + dp[1][1] = 2`
  - result = `1`

- `dp[2][3]`
  - guess 2 => `2`
  - guess 3 => `3`
  - result = `2`

- `dp[3][4]`
  - guess 3 => `3`
  - guess 4 => `4`
  - result = `3`

## Length 3 intervals

- `dp[1][3]`
  - guess 1 => `1 + dp[2][3] = 3`
  - guess 2 => `2 + max(dp[1][1], dp[3][3]) = 2`
  - guess 3 => `3 + dp[1][2] = 4`
  - result = `2`

- `dp[2][4]`
  - guess 2 => `2 + dp[3][4] = 5`
  - guess 3 => `3 + max(dp[2][2], dp[4][4]) = 3`
  - guess 4 => `4 + dp[2][3] = 6`
  - result = `3`

## Length 4 interval

- `dp[1][4]`
  - guess 1 => `1 + dp[2][4] = 4`
  - guess 2 => `2 + max(dp[1][1], dp[3][4]) = 5`
  - guess 3 => `3 + max(dp[1][2], dp[4][4]) = 4`
  - guess 4 => `4 + dp[1][3] = 6`
  - result = `4`

So answer for `n = 4` is:

```text
4
```

---

# Common Mistakes

## 1. Using binary search intuition

This minimizes number of guesses, not payment.

## 2. Using `min(left, right)` instead of `max(left, right)`

You do **not** control which side the hidden number lies in.

Worst case means you must survive the more expensive branch.

## 3. Thinking correct guess should also cost money

No. Only **wrong** guesses cost.

## 4. Forgetting base case

If `left >= right`, answer is `0`.

---

# Key Takeaways

1. This is a **minimax interval DP** problem.
2. State:
   ```text
   dp[left][right]
   ```
3. Transition:
   ```text
   dp[left][right] =
       min over guess in [left..right] of
       guess + max(dp[left][guess-1], dp[guess+1][right])
   ```
4. Base case:
   ```text
   0 when left >= right
   ```
5. Best practical complexity:
   ```text
   Time: O(n^3)
   Space: O(n^2)
   ```

---

# Final Summary

The cleanest way to solve this problem is to recognize that every guess creates two remaining intervals, and because the question asks for a guaranteed win, you must pay for the **worse** side. That produces a minimax recurrence over intervals, which is solved efficiently using either memoization or bottom-up interval DP.

For interviews and practical use, remember:

- **Top-down memoization** for conceptual clarity
- **Bottom-up interval DP** for clean iterative implementation
