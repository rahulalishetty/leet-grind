# 2552. Count Increasing Quadruplets

## Problem Restatement

We are given a permutation `nums` of the integers from `1` to `n`.

We need to count quadruplets `(i, j, k, l)` such that:

```text
0 <= i < j < k < l < n
```

and:

```text
nums[i] < nums[k] < nums[j] < nums[l]
```

We must return the total number of such quadruplets.

---

## First Structural Observation

The inequality is not the usual increasing-by-index and increasing-by-value pattern.

Instead, for fixed middle indices `j` and `k` with `j < k`, we need:

- a left index `i < j` such that:
  ```text
  nums[i] < nums[k]
  ```
- a right index `l > k` such that:
  ```text
  nums[l] > nums[j]
  ```
- and also:
  ```text
  nums[k] < nums[j]
  ```

So if we fix `(j, k)` and `nums[k] < nums[j]`, then the number of valid quadruplets using that pair is:

```text
(# of i < j with nums[i] < nums[k]) * (# of l > k with nums[l] > nums[j])
```

This is the key decomposition.

---

# Approach 1 — Brute Force Over All Quadruplets

## Intuition

The most direct solution is to try every quadruplet `(i, j, k, l)` and test the condition.

This is correct but much too slow.

---

## Java Code

```java
class Solution {
    public long countQuadruplets(int[] nums) {
        int n = nums.length;
        long ans = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int l = k + 1; l < n; l++) {
                        if (nums[i] < nums[k] && nums[k] < nums[j] && nums[j] < nums[l]) {
                            ans++;
                        }
                    }
                }
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^4)
```

### Space Complexity

```text
O(1)
```

This is completely infeasible for `n = 4000`.

---

# Approach 2 — Fix `(j, k)` and Count Left/Right Naively

## Intuition

Using the structural decomposition:

For each pair `(j, k)` where `j < k` and `nums[k] < nums[j]`:

- count how many `i < j` satisfy `nums[i] < nums[k]`
- count how many `l > k` satisfy `nums[l] > nums[j]`

Multiply those two counts and add to the answer.

This avoids explicitly iterating over all quadruplets, but still does a lot of repeated work.

---

## Java Code

```java
class Solution {
    public long countQuadruplets(int[] nums) {
        int n = nums.length;
        long ans = 0;

        for (int j = 1; j < n - 2; j++) {
            for (int k = j + 1; k < n - 1; k++) {
                if (nums[k] >= nums[j]) continue;

                int leftCount = 0;
                for (int i = 0; i < j; i++) {
                    if (nums[i] < nums[k]) {
                        leftCount++;
                    }
                }

                int rightCount = 0;
                for (int l = k + 1; l < n; l++) {
                    if (nums[l] > nums[j]) {
                        rightCount++;
                    }
                }

                ans += 1L * leftCount * rightCount;
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

- `O(n^2)` choices of `(j, k)`
- for each pair, up to `O(n)` counting on the left and `O(n)` on the right

Overall:

```text
O(n^3)
```

### Space Complexity

```text
O(1)
```

Still too slow for the full constraints.

---

# Approach 3 — Precompute Left-Smaller and Right-Greater Tables

## Intuition

We can eliminate repeated counting.

For every pair `(j, k)` with `j < k`, precompute:

- `leftLess[j][k]` conceptually = number of indices `i < j` with `nums[i] < nums[k]`
- `rightGreater[j][k]` conceptually = number of indices `l > k` with `nums[l] > nums[j]`

Then for every valid `(j, k)` with `nums[k] < nums[j]`:

```text
answer += leftLess * rightGreater
```

A full 2D table can be built in `O(n^2)`, which is acceptable for `n <= 4000`.

This is one of the main intended directions.

---

## More Practical Formulation

Instead of storing two full conceptually named tables, we can precompute one side and accumulate the other on the fly.

A classic version is:

- precompute `greater[j][k]` = number of `l > k` with `nums[l] > nums[j]`
- while iterating `j`, maintain how many left values are smaller than current `nums[k]`

---

## Java Code

```java
class Solution {
    public long countQuadruplets(int[] nums) {
        int n = nums.length;
        int[][] greater = new int[n][n];

        // greater[j][k] = number of l > k with nums[l] > nums[j]
        for (int j = 0; j < n; j++) {
            int cnt = 0;
            for (int k = n - 1; k >= 0; k--) {
                greater[j][k] = cnt;
                if (nums[k] > nums[j]) {
                    cnt++;
                }
            }
        }

        long ans = 0;

        for (int j = 1; j < n - 2; j++) {
            int leftSmaller = 0;
            for (int k = 0; k < j; k++) {
                if (nums[k] < nums[j]) {
                    leftSmaller++;
                }
            }

            int smallerBeforeJForK = 0;
            for (int k = j + 1; k < n - 1; k++) {
                if (nums[k] < nums[j]) {
                    int leftCount = 0;
                    for (int i = 0; i < j; i++) {
                        if (nums[i] < nums[k]) {
                            leftCount++;
                        }
                    }
                    ans += 1L * leftCount * greater[j][k];
                }
            }
        }

        return ans;
    }
}
```

This version is conceptually closer to the decomposition, though it still has extra scanning inside and is not the cleanest `O(n^2)` implementation.

So the stronger optimized form is the next approach.

---

# Approach 4 — Optimized `O(n^2)` DP / Counting

## Intuition

We want a genuine `O(n^2)` solution.

A very elegant way is to let:

```text
dp[j] = number of indices i < j such that nums[i] < nums[current_k]
```

while iterating possible `k` values.

A standard optimized solution iterates `j` as the second index and updates counts as `k` moves.

An even clearer formulation is this:

For each `j`, scan `k > j`.

Maintain:

```text
countSmaller = number of i < j such that nums[i] < nums[k]
```

But recomputing that directly is hard.

A well-known `O(n^2)` solution instead uses:

- `dp[j]` = number of valid `(i, j)` pairs that can serve as the left half for future `k, l`

As we scan `k`, we update `dp[j]` when `nums[j] > nums[k]`, and add contributions when `nums[j] < nums[k]`.

However, that formulation is subtle. The clearest robust `O(n^2)` implementation is the prefix/suffix counting form below.

---

# Approach 5 — Clean `O(n^2)` Prefix/Contribution Solution

## Intuition

Fix `j` as the second index.

Now scan `k > j`.

For a valid quadruplet we need:

```text
nums[k] < nums[j]
```

Then contribution is:

```text
(# of i < j with nums[i] < nums[k]) * (# of l > k with nums[l] > nums[j])
```

We can compute the right factor incrementally:

While scanning `k` from right to left for fixed `j`, maintain how many numbers to the right are greater than `nums[j]`.

But there is an even simpler left-to-right formulation widely used:

Let:

```text
dp[j] = number of pairs (i, j) already formed for current sweep
```

When scanning `k` from left to right:

- if `nums[j] < nums[k]`, then every earlier valid pair ending at `j` can be extended to quadruplets with this `k` as the `l` role
- if `nums[j] > nums[k]`, then indices before `j` that are smaller than `nums[k]` can form new partial structures

This yields the concise `O(n^2)` algorithm below.

---

## Java Code

```java
class Solution {
    public long countQuadruplets(int[] nums) {
        int n = nums.length;
        long ans = 0;
        int[] dp = new int[n];

        for (int k = 0; k < n; k++) {
            int smaller = 0;

            for (int j = 0; j < k; j++) {
                if (nums[j] < nums[k]) {
                    ans += dp[j];
                    smaller++;
                } else if (nums[j] > nums[k]) {
                    dp[j] += smaller;
                }
            }
        }

        return ans;
    }
}
```

---

## Why this works

When processing index `k`:

- `smaller` tracks how many earlier indices `i` satisfy:
  ```text
  i < j < k and nums[i] < nums[k]
  ```
  as `j` moves from left to right.

Now for each earlier index `j`:

### Case 1: `nums[j] < nums[k]`

Then current `k` can serve as the `l` in quadruplets ending with second index `j`.

Every partial count stored in `dp[j]` contributes to the final answer.

### Case 2: `nums[j] > nums[k]`

Then current `k` can serve as the `k` in the pattern:

```text
nums[i] < nums[k] < nums[j]
```

and the number of valid `i` choices is exactly `smaller`.

So we add `smaller` to `dp[j]`.

This means:

```text
dp[j]
```

stores how many `(i, k)` choices can pair with this `j` to form the middle inequality for future larger indices.

This is the most elegant intended `O(n^2)` solution.

---

## Complexity Analysis

### Time Complexity

Two nested loops:

```text
O(n^2)
```

With `n <= 4000`, this is acceptable.

### Space Complexity

```text
O(n)
```

This is the recommended solution.

---

# Worked Example

## Example 1

```text
nums = [1,3,2,4,5]
```

Valid quadruplets are:

- `(0,1,2,3)` -> `1 < 2 < 3 < 4`
- `(0,1,2,4)` -> `1 < 2 < 3 < 5`

So answer is:

```text
2
```

Using the `dp` method, when later large values `4` and `5` are processed, they accumulate contributions from the earlier pattern `(1,3,2)` centered around indices `(i,j,k)`.

---

# Correctness Argument for the `O(n^2)` DP Solution

## Invariant

At any point during processing current index `k`, for every earlier index `j`, `dp[j]` stores the number of pairs `(i, x)` such that:

```text
i < j < x < current_k
and
nums[i] < nums[x] < nums[j]
```

That is, `dp[j]` counts how many ways index `j` can serve as the second element in the middle inequality pattern, ready to be completed by some future `l`.

---

## Why update `ans` when `nums[j] < nums[k]`

If `nums[j] < nums[k]`, then current index `k` can be used as `l` in:

```text
nums[i] < nums[x] < nums[j] < nums[k]
```

Every partial structure already counted in `dp[j]` becomes a full quadruplet.

So we do:

```text
ans += dp[j]
```

---

## Why update `dp[j]` when `nums[j] > nums[k]`

If `nums[j] > nums[k]`, then current index `k` can act as the third position in the quadruplet pattern.

We need indices `i < j` such that:

```text
nums[i] < nums[k]
```

The variable `smaller` counts exactly those earlier indices.

So each such `i` forms a new partial structure for this `j`, and we do:

```text
dp[j] += smaller
```

Thus the invariant is maintained.

---

# Comparison of Approaches

## Approach 1 — Brute force `O(n^4)`

Pros:

- direct from the definition

Cons:

- useless for constraints

---

## Approach 2 — Fix `(j, k)` and count sides naively

Pros:

- exposes the key combinational structure

Cons:

- still too slow

---

## Approach 3 — Prefix/suffix precompute ideas

Pros:

- helps derive the optimized structure

Cons:

- easy to make bulky or partially optimized implementations

---

## Approach 4 — Optimized `O(n^2)` DP

Pros:

- elegant
- concise
- matches constraints perfectly
- standard best solution

Cons:

- not immediately obvious on first reading

This is the recommended approach.

---

# Final Recommended Java Solution

```java
class Solution {
    public long countQuadruplets(int[] nums) {
        int n = nums.length;
        long ans = 0;
        int[] dp = new int[n];

        for (int k = 0; k < n; k++) {
            int smaller = 0;

            for (int j = 0; j < k; j++) {
                if (nums[j] < nums[k]) {
                    ans += dp[j];
                    smaller++;
                } else if (nums[j] > nums[k]) {
                    dp[j] += smaller;
                }
            }
        }

        return ans;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n^4)
Space: O(1)
```

## Approach 2

```text
Time:  O(n^3)
Space: O(1)
```

## Approach 3

```text
Time:  O(n^2) to O(n^3), depending on implementation
Space: O(n^2) or O(n)
```

## Approach 4

```text
Time:  O(n^2)
Space: O(n)
```

---

# Final Takeaway

The crucial step is to stop thinking in terms of all four indices at once.

Instead:

1. treat `j` as the “high middle”
2. treat `k` as the “low middle”
3. count how many left choices `i` and right choices `l` can attach to them

That leads to the elegant `O(n^2)` DP solution where:

- `dp[j]` stores partial middle structures
- later larger values complete them into quadruplets
