# Paint Fence (LeetCode 276) — Exhaustive Java Approaches

## Problem Restatement

You have:

- `n` fence posts
- `k` colors

Rules:

1. Every post must be painted exactly one color
2. There cannot be **three or more consecutive posts** with the same color

Goal:

- Return the number of valid ways to paint the fence

---

## Key Insight

The real rule is not “adjacent posts must differ.”

It is weaker:

- Two consecutive posts **may** have the same color
- But three consecutive posts **cannot** have the same color

That means when painting the current post, the decision depends on the relationship between the **last two posts**.

This is why the most natural DP states are:

- `same[i]` = number of ways to paint up to post `i` where post `i` has the **same** color as post `i - 1`
- `diff[i]` = number of ways to paint up to post `i` where post `i` has a **different** color from post `i - 1`

Then:

- `same[i] = diff[i - 1]`
- `diff[i] = (same[i - 1] + diff[i - 1]) * (k - 1)`

Total:

```text
same[i] + diff[i]
```

This is the cleanest core formulation.

---

# Approach 1: Pure Recursion by Tracking Run Length

## Idea

At every post, try all possible colors.

To validate whether a color is allowed, track:

- previous color
- how many times in a row it has been repeated

If choosing the same color would create a run of length `3`, reject it.

This is the most direct brute-force formulation.

---

## State

Define recursive function:

- `solve(index, prevColor, runLength)`

Meaning:

- we are painting post `index`
- previous post has color `prevColor`
- current run of that color has length `runLength`

Choices:

- try every color from `0` to `k - 1`
- if chosen color equals `prevColor`, new run length = `runLength + 1`
- otherwise new run length = `1`

Reject if new run length becomes `3`

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        return solve(0, -1, 0, n, k);
    }

    private int solve(int index, int prevColor, int runLength, int n, int k) {
        if (index == n) {
            return 1;
        }

        int ways = 0;

        for (int color = 0; color < k; color++) {
            if (color == prevColor) {
                if (runLength < 2) {
                    ways += solve(index + 1, color, runLength + 1, n, k);
                }
            } else {
                ways += solve(index + 1, color, 1, n, k);
            }
        }

        return ways;
    }
}
```

---

## Complexity

### Time Complexity

- Exponential, roughly **O(k^n)**

Because each post can try up to `k` colors.

### Space Complexity

- **O(n)** recursion stack depth

---

## Pros

- Most direct interpretation of the problem
- Good for understanding state

## Cons

- Too slow
- Huge repeated work

---

# Approach 2: Top-Down DP (Memoization) with Explicit State

## Idea

The brute-force recursion repeats the same states many times.

So memoize on:

- `index`
- `prevColor`
- `runLength`

This converts recursion into DP.

---

## State Count

Possible values:

- `index`: `0...n`
- `prevColor`: `-1...k-1`
- `runLength`: `0, 1, 2`

That is manageable because `n <= 50` and `k <= 10^5`, but this formulation is only practical if implemented carefully. A naive `n * k` memo table may be too large if we use explicit color states directly for very large `k`.

So this version is more conceptually valid than practically ideal for large `k`.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private final Map<String, Integer> memo = new HashMap<>();

    public int numWays(int n, int k) {
        return solve(0, -1, 0, n, k);
    }

    private int solve(int index, int prevColor, int runLength, int n, int k) {
        if (index == n) {
            return 1;
        }

        String key = index + "," + prevColor + "," + runLength;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int ways = 0;

        for (int color = 0; color < k; color++) {
            if (color == prevColor) {
                if (runLength < 2) {
                    ways += solve(index + 1, color, runLength + 1, n, k);
                }
            } else {
                ways += solve(index + 1, color, 1, n, k);
            }
        }

        memo.put(key, ways);
        return ways;
    }
}
```

---

## Complexity

### Time Complexity

Still not truly optimal due to iterating over all `k` colors at each state.

Roughly:

- **O(n \* k^2)** or worse depending on how states are revisited

### Space Complexity

- Memo storage for reachable states
- Recursion stack `O(n)`

---

## Honest Assessment

This is useful for learning, but not the best route.

The problem has more structure than this state representation exploits.

---

# Approach 3: Top-Down DP Using the Mathematical Recurrence

## Idea

Instead of tracking actual colors, track only the total number of valid ways for length `i`.

Let:

- `f(i)` = number of valid ways to paint `i` posts

We can derive:

### Base cases

- `f(1) = k`
- `f(2) = k * k`

Why `f(2) = k * k`?
Because with only two posts:

- same color is allowed
- different color is allowed
  So all `k * k` assignments are valid.

### Recurrence

For post `i`:

- If last two posts are different, append any of `k - 1` colors to previous arrangement
- If last two posts are same, the previous pair must have been formed from a valid `f(i - 2)` arrangement, and current post must differ

This yields:

```text
f(i) = (k - 1) * (f(i - 1) + f(i - 2))
```

This is the most important recurrence for the problem.

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        Integer[] memo = new Integer[n + 1];
        return solve(n, k, memo);
    }

    private int solve(int n, int k, Integer[] memo) {
        if (n == 1) {
            return k;
        }

        if (n == 2) {
            return k * k;
        }

        if (memo[n] != null) {
            return memo[n];
        }

        memo[n] = (k - 1) * (solve(n - 1, k, memo) + solve(n - 2, k, memo));
        return memo[n];
    }
}
```

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(n)** for memo
- **O(n)** recursion stack

---

## Pros

- Elegant
- Uses the true structure of the problem
- Much better than explicit color-state DP

## Cons

- Still recursive
- Recurrence can feel magical if not derived carefully

---

# Approach 4: Bottom-Up DP with Total Ways Array

## Idea

Use the recurrence directly:

```text
dp[i] = (k - 1) * (dp[i - 1] + dp[i - 2])
```

with:

- `dp[1] = k`
- `dp[2] = k * k`

This is the simplest iterative formulation.

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }

        int[] dp = new int[n + 1];
        dp[1] = k;
        dp[2] = k * k;

        for (int i = 3; i <= n; i++) {
            dp[i] = (k - 1) * (dp[i - 1] + dp[i - 2]);
        }

        return dp[n];
    }
}
```

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(n)**

---

## Pros

- Easy to code
- Easy to trace
- Excellent interview solution

## Cons

- Uses more space than needed

---

# Approach 5: Bottom-Up DP with Explicit same / diff Arrays

## Idea

This is the most instructive DP modeling.

Let:

- `same[i]` = ways to paint up to post `i` such that post `i` and post `i - 1` have the same color
- `diff[i]` = ways to paint up to post `i` such that post `i` and post `i - 1` have different colors

Then:

### Base for `i = 1`

There is only one post:

- `same[1] = 0` (no previous post)
- `diff[1] = k`

### Transition

To make current post same as previous:

- previous pair must have been different
- so:

```text
same[i] = diff[i - 1]
```

To make current post different from previous:

- previous arrangement can be anything
- choose any of `k - 1` colors

```text
diff[i] = (same[i - 1] + diff[i - 1]) * (k - 1)
```

Answer:

```text
same[n] + diff[n]
```

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }

        int[] same = new int[n + 1];
        int[] diff = new int[n + 1];

        same[1] = 0;
        diff[1] = k;

        for (int i = 2; i <= n; i++) {
            same[i] = diff[i - 1];
            diff[i] = (same[i - 1] + diff[i - 1]) * (k - 1);
        }

        return same[n] + diff[n];
    }
}
```

---

## Example Dry Run

Take:

```text
n = 3, k = 2
```

### Post 1

- `same[1] = 0`
- `diff[1] = 2`

### Post 2

- `same[2] = diff[1] = 2`
- `diff[2] = (0 + 2) * 1 = 2`

Total:

```text
4
```

These are:

- RR
- GG
- RG
- GR

### Post 3

- `same[3] = diff[2] = 2`
- `diff[3] = (2 + 2) * 1 = 4`

Total:

```text
6
```

Correct.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(n)**

---

## Why This Approach Is Great

Because it makes the rule “no three in a row” feel natural.

The recurrence is no longer magical:

- `same` captures exactly the dangerous condition
- `diff` captures everything else

This is probably the best conceptual explanation.

---

# Approach 6: Space-Optimized same / diff DP

## Idea

Observe that at step `i`, we only need values from step `i - 1`.

So instead of arrays, keep only two variables:

- `same`
- `diff`

This is the cleanest optimized formulation.

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }

        int same = 0;
        int diff = k;

        for (int i = 2; i <= n; i++) {
            int newSame = diff;
            int newDiff = (same + diff) * (k - 1);

            same = newSame;
            diff = newDiff;
        }

        return same + diff;
    }
}
```

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)**

---

## Pros

- Best practical solution
- Clean and elegant
- Easy once states are understood

## Cons

- Slightly abstract if same/diff idea is not internalized

---

# Approach 7: Space-Optimized Total-Ways Recurrence

## Idea

Using:

```text
f(i) = (k - 1) * (f(i - 1) + f(i - 2))
```

we only need:

- previous answer
- answer before previous

So compress to two variables.

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }

        int prev2 = k;       // f(1)
        int prev1 = k * k;   // f(2)

        for (int i = 3; i <= n; i++) {
            int curr = (k - 1) * (prev1 + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)**

---

## Pros

- Very short
- Elegant
- Direct use of recurrence

## Cons

- Harder to justify unless recurrence is already derived

---

# Approach 8: Matrix Exponentiation Perspective

## Idea

The recurrence:

```text
f(i) = (k - 1) * (f(i - 1) + f(i - 2))
```

is a linear recurrence.

So in theory, it can be represented as:

```text
[ f(i)   ]   = M * [ f(i - 1) ]
[ f(i-1) ]         [ f(i - 2) ]
```

where

```text
M = [ (k - 1)  (k - 1) ]
    [   1          0    ]
```

Then:

```text
[ f(n)   ] = M^(n-2) * [ f(2) ]
[ f(n-1) ]             [ f(1) ]
```

This gives an `O(log n)` solution.

For this problem, `n <= 50`, so this is unnecessary, but it is mathematically interesting.

---

## Java Code

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }
        if (n == 2) {
            return k * k;
        }

        long[][] matrix = {
            {k - 1, k - 1},
            {1, 0}
        };

        long[][] power = matrixPower(matrix, n - 2);

        long f2 = (long) k * k;
        long f1 = k;

        long result = power[0][0] * f2 + power[0][1] * f1;
        return (int) result;
    }

    private long[][] matrixPower(long[][] matrix, int exp) {
        long[][] result = {
            {1, 0},
            {0, 1}
        };

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = multiply(result, matrix);
            }
            matrix = multiply(matrix, matrix);
            exp >>= 1;
        }

        return result;
    }

    private long[][] multiply(long[][] a, long[][] b) {
        long[][] c = new long[2][2];
        c[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0];
        c[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1];
        c[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0];
        c[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1];
        return c;
    }
}
```

---

## Complexity

### Time Complexity

- **O(log n)**

### Space Complexity

- **O(1)** ignoring constant-size matrices

---

## Why Mention This?

Because it reveals the recurrence as a linear algebra object.

But for interview purposes, this is usually overkill.

---

# Deriving the Main Recurrence Carefully

This is the crucial part.

Let `f(i)` = number of valid ways to paint `i` posts.

We want `f(i)` in terms of smaller answers.

Consider the last two posts:

## Case 1: Last two posts have different colors

Then:

- any valid arrangement of first `i - 1` posts works
- current post can be any of `k - 1` colors different from previous

Ways:

```text
f(i - 1) * (k - 1)
```

## Case 2: Last two posts have the same color

Then:

- post `i - 1` and post `i` are same
- so post `i - 2` must be different from post `i - 1`
- therefore, first `i - 2` posts can be any valid arrangement
- choose a color for post `i - 1` different from post `i - 2`: `k - 1` choices
- post `i` is forced to match post `i - 1`

Ways:

```text
f(i - 2) * (k - 1)
```

Add them:

```text
f(i) = (k - 1) * (f(i - 1) + f(i - 2))
```

This is the central recurrence.

---

# Comparing the Two Best Formulations

## Formulation A: Total ways recurrence

```text
f(i) = (k - 1)(f(i - 1) + f(i - 2))
```

### Pros

- Compact
- Elegant
- Easy to optimize

### Cons

- Harder to derive intuitively

---

## Formulation B: same / diff states

```text
same[i] = diff[i - 1]
diff[i] = (same[i - 1] + diff[i - 1]) * (k - 1)
```

### Pros

- Very intuitive
- Mirrors the actual rule directly

### Cons

- Slightly more state bookkeeping

---

# Comparing All Approaches

| Approach                         |              Time | Space | Notes                   |
| -------------------------------- | ----------------: | ----: | ----------------------- |
| Pure Recursion                   |            O(k^n) |  O(n) | Conceptual only         |
| Memoized Explicit State          | Large / not ideal | Large | Educational but awkward |
| Top-Down Recurrence DP           |              O(n) |  O(n) | Clean recursive DP      |
| Bottom-Up Total DP               |              O(n) |  O(n) | Standard iterative      |
| Bottom-Up same/diff Arrays       |              O(n) |  O(n) | Best conceptual model   |
| Space-Optimized same/diff        |              O(n) |  O(1) | Best practical solution |
| Space-Optimized Total Recurrence |              O(n) |  O(1) | Shortest good solution  |
| Matrix Exponentiation            |          O(log n) |  O(1) | Overkill here           |

---

# Recommended Final Java Solution

## Best Conceptual Version

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }

        int same = 0;
        int diff = k;

        for (int i = 2; i <= n; i++) {
            int newSame = diff;
            int newDiff = (same + diff) * (k - 1);

            same = newSame;
            diff = newDiff;
        }

        return same + diff;
    }
}
```

---

## Why This Is the Best Practical Answer

It directly models the constraint:

- `same` means current post matches previous
- `diff` means current post differs from previous

And the no-three-in-a-row rule becomes automatic:

- you can only move into `same` from a previous `diff`

That is the heart of the problem.

---

# Alternative Recommended Short Version

```java
class Solution {
    public int numWays(int n, int k) {
        if (n == 1) {
            return k;
        }

        int prev2 = k;
        int prev1 = k * k;

        for (int i = 3; i <= n; i++) {
            int curr = (k - 1) * (prev1 + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

# Dry Run of the Recommended same/diff Solution

Take:

```text
n = 3, k = 2
```

## Start

For first post:

- `same = 0`
- `diff = 2`

## Post 2

- `newSame = diff = 2`
- `newDiff = (same + diff) * (k - 1)`
- `newDiff = (0 + 2) * 1 = 2`

Update:

- `same = 2`
- `diff = 2`

Total so far:

```text
4
```

## Post 3

- `newSame = diff = 2`
- `newDiff = (2 + 2) * 1 = 4`

Update:

- `same = 2`
- `diff = 4`

Final answer:

```text
same + diff = 6
```

Correct.

---

# Edge Cases

## 1. `n = 1`

Only one post:

```text
answer = k
```

## 2. `k = 1`

Only one color exists.

Then:

- `n = 1` → 1 way
- `n = 2` → 1 way
- `n >= 3` → 0 ways

The recurrence naturally produces this.

Example:

- `f(1) = 1`
- `f(2) = 1`
- `f(3) = 0 * (1 + 1) = 0`

Correct.

## 3. Large `k`

No issue — recurrence handles it directly.

---

# Common Mistakes

## Mistake 1: Treating it like adjacent posts must differ

That would incorrectly forbid two identical consecutive posts.

But the rule only forbids **three** consecutive identical posts.

---

## Mistake 2: Wrong base case for `n = 2`

Some people write:

```text
f(2) = k * (k - 1)
```

That is wrong.

For two posts:

- same color is allowed
- different color is allowed

So:

```text
f(2) = k * k
```

---

## Mistake 3: Forgetting why `same[i] = diff[i - 1]`

If current post matches previous, then previous pair must have ended in `diff`.
Otherwise we would create three consecutive equal posts.

---

## Mistake 4: Integer overflow paranoia

The problem guarantees the final answer fits in 32-bit signed integer, so `int` is acceptable here.

---

# Pattern Recognition

This problem is a classic example of:

- DP on sequences
- state machine DP
- recurrence based on local pattern restrictions

The strongest signal is:

- validity depends on how many times the current run has repeated

That almost always suggests:

- explicit run-length state
- or compressed “same / diff” state

---

# Interview Explanation Template

A clean explanation:

1. Let `same` be the number of ways where the last two posts have the same color.
2. Let `diff` be the number of ways where the last two posts have different colors.
3. For each new post:
   - `newSame = diff`
   - `newDiff = (same + diff) * (k - 1)`
4. Return `same + diff`.

Time:

- `O(n)`

Space:

- `O(1)`

This is concise and strong.

---

# Final Takeaway

The core difficulty is not painting; it is tracking whether the latest color run has length:

- `1`
- or `2`

That is why the best DP state is:

- `same`
- `diff`

Once that is clear, the recurrence becomes natural, and the optimized solution is straightforward.
