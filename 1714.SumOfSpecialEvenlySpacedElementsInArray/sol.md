# Sum of Special Subsequences by Queries — Detailed Summary

## Problem

You are given a `0`-indexed integer array `nums` of length `n` consisting of non-negative integers.

You are also given an array `queries`, where:

```text
queries[i] = [x_i, y_i]
```

For each query `(x, y)`, we must compute:

- the sum of all `nums[j]`
- such that `x <= j < n`
- and `(j - x)` is divisible by `y`

In other words, for one query we need:

```text
nums[x] + nums[x + y] + nums[x + 2y] + nums[x + 3y] + ...
```

as long as the index stays inside the array.

Return an array `answer` where:

```text
answer[i] = result of query i modulo 1_000_000_007
```

---

## Core observation

Each query asks for an arithmetic progression of indices:

```text
x, x + y, x + 2y, x + 3y, ...
```

So the straightforward solution is:

- start at `x`
- jump by `y`
- add values until leaving the array

That costs:

```text
O(n / y)
```

per query.

This is fine for large `y`, because then there are only a few jumps.
But it is expensive for small `y`, because then each query touches many elements.

So the problem naturally splits into two cases:

- **small step size `y`**
- **large step size `y`**

This leads to a classic **sqrt decomposition** strategy.

---

# Brute force idea first

For a single query `(x, y)`:

```java
long sum = 0;
for (int j = x; j < n; j += y) {
    sum += nums[j];
}
```

This is simple and correct.

But if many queries have small `y` like `1`, `2`, or `3`, then each query can cost almost `O(n)`, which becomes too slow.

So we need preprocessing.

---

# Main idea: sqrt decomposition

Let:

```text
B = floor(sqrt(n)) + 1
```

We divide all queries into two groups.

## Group 1: small `y` (`y < B`)

For these step sizes, each query may touch many positions.
So instead of recomputing every time, we **precompute** the answers.

## Group 2: large `y` (`y >= B`)

For these step sizes, each query touches at most about:

```text
n / B ≈ sqrt(n)
```

positions, which is already cheap enough to compute directly.

This gives a balanced overall complexity.

---

# Precomputation for small step sizes

For every small step size `y`, define:

```text
dp[y][i] = nums[i] + nums[i + y] + nums[i + 2y] + ...
```

This is exactly the sum needed for a query that starts at index `i` and jumps by `y`.

So if a query is `(x, y)` and `y` is small, the answer is just:

```text
dp[y][x]
```

---

## Recurrence

For fixed `y`, if we know the answer from `i + y`, then:

```text
dp[y][i] = nums[i] + dp[y][i + y]
```

provided `i + y < n`.

Otherwise:

```text
dp[y][i] = nums[i]
```

So:

```text
dp[y][i] = nums[i]                              if i + y >= n
dp[y][i] = nums[i] + dp[y][i + y]              otherwise
```

We compute this from right to left.

---

# Why right to left?

Because `dp[y][i]` depends on `dp[y][i + y]`, which is to the right.

So for each small `y`:

- start from `i = n - 1`
- move backward to `0`

That ensures `dp[y][i + y]` is already computed when needed.

---

# Full algorithm

## Step 1: choose block threshold

```text
B = floor(sqrt(n)) + 1
```

## Step 2: precompute for all small step sizes

For every `y` from `1` to `B - 1`:

- compute `dp[y][i]` for all `i` from `n - 1` down to `0`

## Step 3: answer each query

For query `(x, y)`:

- if `y < B`, answer is `dp[y][x]`
- otherwise, compute directly by jumping `x, x + y, x + 2y, ...`

Take modulo `1_000_000_007` throughout.

---

# Java solution

```java
class Solution {
    public int[] solveQueries(int[] nums, int[][] queries) {
        int n = nums.length;
        int q = queries.length;
        int MOD = 1_000_000_007;
        int B = (int) Math.sqrt(n) + 1;

        long[][] dp = new long[B][n];

        // Precompute answers for all small step sizes.
        for (int y = 1; y < B; y++) {
            for (int i = n - 1; i >= 0; i--) {
                dp[y][i] = nums[i];
                if (i + y < n) {
                    dp[y][i] += dp[y][i + y];
                }
                dp[y][i] %= MOD;
            }
        }

        int[] answer = new int[q];

        for (int k = 0; k < q; k++) {
            int x = queries[k][0];
            int y = queries[k][1];

            if (y < B) {
                answer[k] = (int) dp[y][x];
            } else {
                long sum = 0;
                for (int i = x; i < n; i += y) {
                    sum += nums[i];
                    if (sum >= MOD) {
                        sum %= MOD;
                    }
                }
                answer[k] = (int) (sum % MOD);
            }
        }

        return answer;
    }
}
```

---

# Dry run

## Example

```text
nums = [0, 1, 2, 3, 4, 5]
queries = [[1, 2], [0, 1], [2, 3]]
```

We evaluate each query.

---

## Query 1: `(1, 2)`

We need indices:

```text
1, 3, 5
```

because:

```text
j >= 1 and (j - 1) % 2 == 0
```

So sum is:

```text
nums[1] + nums[3] + nums[5]
= 1 + 3 + 5
= 9
```

If `2` is a small step, this is exactly:

```text
dp[2][1]
```

---

## Query 2: `(0, 1)`

We need indices:

```text
0, 1, 2, 3, 4, 5
```

So sum is:

```text
0 + 1 + 2 + 3 + 4 + 5 = 15
```

This is:

```text
dp[1][0]
```

---

## Query 3: `(2, 3)`

We need indices:

```text
2, 5
```

So sum is:

```text
2 + 5 = 7
```

This is:

```text
dp[3][2]
```

if `3` is small enough; otherwise it is computed directly.

Final answer:

```text
[9, 15, 7]
```

---

# Precompute table intuition

Suppose:

```text
nums = [0, 1, 2, 3, 4, 5]
```

Let us build `dp[2][i]`.

We compute from right to left.

## `i = 5`

```text
dp[2][5] = nums[5] = 5
```

because `5 + 2 >= 6`.

## `i = 4`

```text
dp[2][4] = nums[4] = 4
```

because `4 + 2 >= 6`.

## `i = 3`

```text
dp[2][3] = nums[3] + dp[2][5]
         = 3 + 5
         = 8
```

## `i = 2`

```text
dp[2][2] = nums[2] + dp[2][4]
         = 2 + 4
         = 6
```

## `i = 1`

```text
dp[2][1] = nums[1] + dp[2][3]
         = 1 + 8
         = 9
```

## `i = 0`

```text
dp[2][0] = nums[0] + dp[2][2]
         = 0 + 6
         = 6
```

So:

```text
dp[2] = [6, 9, 6, 8, 4, 5]
```

Interpretation:

- `dp[2][0] = nums[0] + nums[2] + nums[4] = 6`
- `dp[2][1] = nums[1] + nums[3] + nums[5] = 9`
- and so on

That is exactly the structure of the queries.

---

# Why this split is optimal enough

The key tradeoff is:

- if `y` is small, there are many terms in the sum
- if `y` is large, there are few terms in the sum

So:

- small `y` → invest preprocessing and answer in `O(1)`
- large `y` → skip preprocessing and answer directly in about `O(sqrt(n))`

This is the standard shape of sqrt decomposition.

---

# Correctness argument

We need to show both query categories are answered correctly.

## Case 1: `y < B`

For fixed `y`, we defined:

```text
dp[y][i] = nums[i] + nums[i + y] + nums[i + 2y] + ...
```

The recurrence:

```text
dp[y][i] = nums[i] + dp[y][i + y]
```

is correct because after taking `nums[i]`, the remaining required indices are exactly:

```text
i + y, i + 2y, i + 3y, ...
```

When `i + y >= n`, only `nums[i]` remains, so the base case is also correct.

Therefore `dp[y][x]` is exactly the answer for query `(x, y)`.

## Case 2: `y >= B`

We directly iterate through indices:

```text
x, x + y, x + 2y, ...
```

which are precisely the indices satisfying:

```text
j >= x and (j - x) % y == 0
```

So the direct computation is also correct.

Since every query belongs to exactly one of the two cases, the algorithm is correct.

---

# Complexity analysis

Let:

- `n = nums.length`
- `q = queries.length`
- `B = floor(sqrt(n)) + 1`

## Precomputation cost

We precompute for each small `y`:

- `y = 1, 2, ..., B - 1`

For each such `y`, we fill all `n` positions.

So preprocessing costs:

```text
O(n * B) = O(n * sqrt(n))
```

---

## Query answering cost

### Small `y`

Each query is answered in:

```text
O(1)
```

because we directly return `dp[y][x]`.

### Large `y`

Each query jumps by at least `B`, so the number of visited indices is at most about:

```text
n / B = O(sqrt(n))
```

So each large-step query costs:

```text
O(sqrt(n))
```

Across all queries, total query time is:

```text
O(q * sqrt(n))
```

in the worst case.

---

## Total time complexity

```text
O(n * sqrt(n) + q * sqrt(n))
```

---

## Space complexity

The DP table has size:

```text
B * n
```

So space is:

```text
O(n * sqrt(n))
```

---

# Why modulo is needed

The problem asks for results modulo:

```text
1_000_000_007
```

Since many `nums[j]` values may be added, the total could be large.

So:

- apply modulo while precomputing
- apply modulo while brute forcing large-step queries

This prevents overflow growth and keeps values in range.

---

# Common implementation details

## 1. Use `long` during summation

Even though final answers are modulo `1_000_000_007`, intermediate sums should use `long`:

```java
long sum = 0;
```

and:

```java
long[][] dp
```

This avoids overflow before the modulo is applied.

---

## 2. Why `B = sqrt(n) + 1`

Using:

```java
int B = (int) Math.sqrt(n) + 1;
```

ensures that:

- all truly small step sizes are covered
- the large-step case has at most about `sqrt(n)` jumps

The `+1` is just a safe integer boundary choice.

---

## 3. Indexing is already 0-based

The query starts from `x`, not from `0`.

So the arithmetic progression is:

```text
x, x + y, x + 2y, ...
```

not multiples of `y` starting from zero.

That is an easy detail to get wrong.

---

# Alternative perspective

You can think of the problem as:

For each fixed jump size `y`, precompute the suffix-like sums along arithmetic progressions.

For example:

- for `y = 1`, this is an ordinary suffix sum
- for `y = 2`, it is suffix sums over even and odd chains separately
- for `y = 3`, it is suffix sums over residue classes modulo `3`
- and so on

That makes the DP recurrence feel more natural.

---

# Compact solution summary

## Precompute for small `y`

```text
dp[y][i] = nums[i] + dp[y][i + y]
```

## Answer query `(x, y)`

- if `y < B` → return `dp[y][x]`
- else → simulate:

```text
x, x + y, x + 2y, ...
```

---

# Final Java solution again

```java
class Solution {
    public int[] solveQueries(int[] nums, int[][] queries) {
        int n = nums.length;
        int q = queries.length;
        int MOD = 1_000_000_007;
        int B = (int) Math.sqrt(n) + 1;

        long[][] dp = new long[B][n];

        for (int y = 1; y < B; y++) {
            for (int i = n - 1; i >= 0; i--) {
                dp[y][i] = nums[i];
                if (i + y < n) {
                    dp[y][i] = (dp[y][i] + dp[y][i + y]) % MOD;
                }
            }
        }

        int[] answer = new int[q];

        for (int k = 0; k < q; k++) {
            int x = queries[k][0];
            int y = queries[k][1];

            if (y < B) {
                answer[k] = (int) dp[y][x];
            } else {
                long sum = 0;
                for (int i = x; i < n; i += y) {
                    sum = (sum + nums[i]) % MOD;
                }
                answer[k] = (int) sum;
            }
        }

        return answer;
    }
}
```

---

# Final takeaway

This problem looks like many independent arithmetic-progression sums, but the repeated step sizes create structure.

The right insight is:

- **small steps repeat too much, so precompute them**
- **large steps visit few indices, so brute force them**

That gives an efficient sqrt decomposition solution with:

- **Time:** `O((n + q) * sqrt(n))`
- **Space:** `O(n * sqrt(n))`
