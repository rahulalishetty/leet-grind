# Palindrome Removal — Detailed Summary

## Problem

You are given an integer array `arr`.

In one move, you may choose a **palindromic subarray**:

```text
arr[i], arr[i+1], ..., arr[j]
```

and remove it.

After removal, the remaining elements close the gap.

We want the **minimum number of moves** needed to remove the entire array.

---

# Core difficulty

This is not a greedy problem.

A subarray that is not currently a palindrome may become removable later after deleting elements in between.

That means the order of removals matters.

So the natural approach is **interval dynamic programming**.

---

# Main DP definition

Let:

```text
dp[i][j]
```

be the **minimum number of moves** needed to remove the subarray:

```text
arr[i..j]
```

Our goal is:

```text
dp[0][n - 1]
```

---

# First idea

Suppose we focus on the first element in the interval, `arr[i]`.

One obvious option is:

- remove `arr[i]` by itself in one move
- then remove the rest `arr[i+1..j]`

So:

```text
dp[i][j] = 1 + dp[i+1][j]
```

This gives a valid upper bound.

But sometimes we can do better.

---

# Key optimization idea

If there exists some `k > i` such that:

```text
arr[i] == arr[k]
```

then `arr[i]` may be removed **together** with `arr[k]`.

How?

If we first remove everything between them:

```text
arr[i+1..k-1]
```

then `arr[i]` and `arr[k]` become adjacent.

Since two equal numbers form a palindrome of length 2, they can then disappear together as part of one removal.

This creates the important merge transition.

---

# Important subtlety: adjacent equal elements

A common mistake is to write:

```text
dp[i][j] = dp[i+1][k-1] + dp[k+1][j]
```

for all matching `k`.

This is **wrong when `k = i+1`**.

Why?

Because if `arr[i] == arr[i+1]`, removing `[arr[i], arr[i+1]]` still costs **1 move**, not `0`.

So adjacent matching elements must be handled carefully.

---

# Correct recurrence

## Base case

A single element is always a palindrome:

```text
dp[i][i] = 1
```

---

## Transition 1: remove `arr[i]` alone

```text
dp[i][j] = 1 + dp[i+1][j]
```

---

## Transition 2: remove two adjacent equal elements together

If:

```text
arr[i] == arr[i+1]
```

then we can remove the pair `[arr[i], arr[i+1]]` in **one move**.

So:

```text
dp[i][j] = min(dp[i][j], 1 + dp[i+2][j])
```

if `i + 1 <= j`.

This is the correction that prevents the adjacent-pair bug.

---

## Transition 3: merge `arr[i]` with a later equal `arr[k]` where `k >= i+2`

If:

```text
arr[i] == arr[k]
```

and `k >= i + 2`, then:

1. first remove the middle part `arr[i+1..k-1]`
2. then `arr[i]` and `arr[k]` can be removed together
3. then remove the suffix `arr[k+1..j]`

So:

```text
dp[i][j] = min(dp[i][j], dp[i+1][k-1] + dp[k+1][j])
```

with empty intervals treated as `0`.

This works because `arr[i]` merges into the removal involving `arr[k]`, so no extra `+1` is needed beyond clearing the middle and suffix.

---

# Why the merge recurrence is valid

Suppose `arr[i] == arr[k]` for some `k >= i+2`.

If we remove `arr[i+1..k-1]` completely first, then the two equal elements become adjacent.

Now they can participate in the same final palindromic deletion.

After that, we only still need to remove the suffix `arr[k+1..j]`.

So total cost becomes:

```text
cost to clear middle + cost to clear suffix
```

which is:

```text
dp[i+1][k-1] + dp[k+1][j]
```

The reason there is no extra `+1` here is that `arr[i]` is not removed separately — it is absorbed into the removal with `arr[k]`.

But this logic only works cleanly when `k >= i+2`.
For `k = i+1`, the middle interval is empty and the pair itself still needs 1 move, hence the special adjacent case.

---

# Full recurrence summary

For `i < j`:

```text
dp[i][j] = 1 + dp[i+1][j]
```

If `arr[i] == arr[i+1]`:

```text
dp[i][j] = min(dp[i][j], 1 + dp[i+2][j])
```

For every `k` such that:

```text
k >= i+2 and arr[i] == arr[k]
```

update:

```text
dp[i][j] = min(dp[i][j], dp[i+1][k-1] + dp[k+1][j])
```

Base:

```text
dp[i][i] = 1
```

---

# Order of computation

This is interval DP, so compute by increasing subarray length:

- length 1
- length 2
- length 3
- ...
- length n

That ensures all smaller intervals needed by the recurrence are already computed.

---

# Correct Java solution

```java
class Solution {
    public int minimumMoves(int[] arr) {
        int n = arr.length;
        int[][] dp = new int[n][n];

        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;

                // Option 1: remove arr[i] alone
                dp[i][j] = 1 + dp[i + 1][j];

                // Option 2: remove arr[i] and arr[i+1] together
                if (arr[i] == arr[i + 1]) {
                    dp[i][j] = Math.min(
                        dp[i][j],
                        1 + ((i + 2 <= j) ? dp[i + 2][j] : 0)
                    );
                }

                // Option 3: merge arr[i] with some later equal arr[k], k >= i+2
                for (int k = i + 2; k <= j; k++) {
                    if (arr[i] == arr[k]) {
                        int left = dp[i + 1][k - 1];
                        int right = (k + 1 <= j) ? dp[k + 1][j] : 0;
                        dp[i][j] = Math.min(dp[i][j], left + right);
                    }
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

# Slightly cleaner helper-style version

```java
class Solution {
    public int minimumMoves(int[] arr) {
        int n = arr.length;
        int[][] dp = new int[n][n];

        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len <= n; i++) {
                int j = i + len - 1;

                dp[i][j] = 1 + dp[i + 1][j];

                if (arr[i] == arr[i + 1]) {
                    dp[i][j] = Math.min(dp[i][j], 1 + get(dp, i + 2, j));
                }

                for (int k = i + 2; k <= j; k++) {
                    if (arr[i] == arr[k]) {
                        dp[i][j] = Math.min(dp[i][j],
                                get(dp, i + 1, k - 1) + get(dp, k + 1, j));
                    }
                }
            }
        }

        return dp[0][n - 1];
    }

    private int get(int[][] dp, int l, int r) {
        return l > r ? 0 : dp[l][r];
    }
}
```

---

# Dry run 1

## Example

```text
arr = [1, 2, 2, 1]
```

This whole array is a palindrome, so answer should be:

```text
1
```

Let us see how DP can achieve that.

### Length 1

Each single element takes 1 move:

```text
dp[i][i] = 1
```

### Length 2

For `[2,2]`:

- remove first alone → `1 + 1 = 2`
- adjacent equal pair → `1`

So:

```text
dp[1][2] = 1
```

### Length 4, interval `[0..3]`

`arr[0] == arr[3] == 1`

So try merging them:

```text
dp[0][3] = dp[1][2] + 0 = 1 + 0 = 1
```

That correctly gives:

```text
dp[0][3] = 1
```

which means the whole array can be removed in one move.

---

# Dry run 2

## Example

```text
arr = [16, 13, 13, 10, 12]
```

This is the testcase that exposes the common bug.

Expected answer:

```text
4
```

One optimal sequence:

1. remove `[13,13]`
2. remove `[16]`
3. remove `[10]`
4. remove `[12]`

Total:

```text
4
```

### Why a wrong recurrence fails

A buggy formula might treat adjacent equal pair `[13,13]` as costing `0` after merge, which is impossible.

Removing `[13,13]` still takes **1 move**.

That is exactly why the recurrence needs the special adjacent-pair case:

```text
if arr[i] == arr[i+1]:
    dp[i][j] = min(dp[i][j], 1 + dp[i+2][j])
```

not:

```text
0 + dp[i+2][j]
```

---

# Dry run 3

## Example

```text
arr = [1, 3, 4, 1, 5]
```

A naive strategy might remove everything separately:

```text
5 moves
```

But we can do better.

Since the first and fourth elements are both `1`, we can try to merge them after clearing the middle `[3,4]`.

That gives a DP candidate:

```text
dp[0][4] = dp[1][2] + dp[4][4]
```

Now:

- `dp[1][2]` removes `[3,4]` in 2 moves
- `dp[4][4]` removes `[5]` in 1 move

So total can be `3`.

One optimal strategy is:

1. remove `[3]`
2. remove `[4]`
3. now the array becomes `[1,1,5]`
4. remove `[1,1]`
5. remove `[5]`

Actually that is 4 moves if done literally in that sequence, but DP is reasoning more abstractly about optimal merge structure over intervals. The recurrence identifies that matching equal ends can reduce cost compared to removing everything independently.

The important takeaway is that interval merging matters.

---

# Why this is interval DP

The problem always asks about removing a contiguous current subarray.

Once you choose to focus on some interval `arr[i..j]`, every optimal strategy for it depends on optimal strategies for smaller intervals inside it.

That is the hallmark of interval DP.

Common signs of interval DP:

- substring / subarray problems
- split points
- merging endpoints
- removing ranges

This problem has all of them.

---

# Why greedy does not work

A tempting wrong approach is:

- always remove the longest palindromic subarray available now
- or always remove adjacent equal values first

That fails because a removal can change future adjacency relationships.

A locally good move may destroy a much better future merge opportunity.

So only DP can systematically compare all meaningful interval strategies.

---

# Complexity analysis

Let:

```text
n = arr.length
```

## Time complexity

We iterate over:

- all interval lengths → `O(n)`
- all starting indices for each length → `O(n)`
- all matching positions `k` inside interval → `O(n)`

So total:

```text
O(n^3)
```

## Space complexity

We store the `n x n` DP table:

```text
O(n^2)
```

---

# Common mistakes

## 1. Treating adjacent equal pair as zero cost

Wrong:

```text
dp[i][j] = dp[i+1][i] + dp[i+2][j] = 0 + dp[i+2][j]
```

Correct:

```text
1 + dp[i+2][j]
```

because removing the pair itself still requires one move.

---

## 2. Forgetting that later merging is possible

If `arr[i] == arr[k]`, those values may be removed together **after** the middle part disappears.

That is the key optimization.

---

## 3. Using only ordinary palindrome checking

The removals are dynamic.
The array changes after each deletion, so the optimal strategy is not about just finding palindromic subarrays in the original fixed array.

---

## 4. Not handling empty intervals

Expressions like:

```text
dp[k+1][j]
```

should be treated as `0` when `k == j`.

Similarly for any `l > r`.

---

# Compact recurrence summary

For each interval `[i..j]`:

```text
dp[i][i] = 1
```

For `i < j`:

```text
dp[i][j] = 1 + dp[i+1][j]
```

If `arr[i] == arr[i+1]`:

```text
dp[i][j] = min(dp[i][j], 1 + dp[i+2][j])
```

For every `k >= i+2` with `arr[i] == arr[k]`:

```text
dp[i][j] = min(dp[i][j], dp[i+1][k-1] + dp[k+1][j])
```

---

# Final takeaway

The essential idea is:

- either remove the first element alone
- or merge it with a later equal element after clearing the middle

That makes this a classic interval DP with a crucial adjacency edge case.

## Final complexity

```text
Time:  O(n^3)
Space: O(n^2)
```
