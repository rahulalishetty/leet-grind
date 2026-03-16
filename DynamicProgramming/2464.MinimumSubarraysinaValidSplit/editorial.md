# Valid Subarray Split — Dynamic Programming (Detailed Notes)

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Idea

We are given an integer array `nums`.

We want to split it into contiguous subarrays such that:

- the original order is preserved
- for every chosen subarray, the **greatest common divisor (GCD)** of its **first** and **last** elements is greater than `1`

Our objective is to return the **minimum number of subarrays** in such a split.

If no valid split exists, return:

```text
-1
```

---

# Core Constraint

For any chosen subarray:

```text
nums[l...r]
```

it is valid only if:

```text
gcd(nums[l], nums[r]) > 1
```

Notice something important:

- the condition only depends on the **first** and **last** elements of the subarray
- the values in the middle do not directly affect validity

That is what makes the dynamic programming formulation straightforward.

---

# Why Dynamic Programming Fits

This problem has **optimal substructure**.

Suppose the optimal split of the prefix ending at index `i` ends with a valid subarray that starts at index `j` and ends at index `i`.

Then:

- the suffix `nums[j...i]` is one valid final subarray
- the prefix before it, `nums[0...j-1]`, must itself be split optimally

Otherwise, if the prefix before `j` were not split optimally, we could replace it with a better split and improve the total answer, contradicting optimality.

So the solution for a prefix depends on optimal solutions to smaller prefixes.

That is the classic DP pattern.

---

# DP Definition

Let:

```text
dp[i]
```

represent:

> the minimum number of valid subarrays needed to split the **first `i` elements** of `nums`

That means:

- `dp[0] = 0` because an empty prefix requires no subarrays
- `dp[n]` is the final answer, where `n = nums.length`

---

# Transition Logic

Suppose we want to compute `dp[i]`.

This means we want the minimum number of valid subarrays for the prefix:

```text
nums[0...i-1]
```

Now consider the last subarray in this split.

Assume that last subarray starts at position `j - 1` and ends at position `i - 1` in 0-based indexing.

Then:

- the previous part contributes `dp[j - 1]`
- the last valid subarray contributes `1`

So if:

```text
gcd(nums[i - 1], nums[j - 1]) > 1
```

then we can update:

```text
dp[i] = min(dp[i], dp[j - 1] + 1)
```

We try all possible starting points `j` for the final subarray and take the minimum.

---

# Recurrence Relation

Using 1-based DP indexing:

```text
dp[i] = min(dp[i], dp[j - 1] + 1)
```

for every `j` such that:

```text
1 <= j <= i
and gcd(nums[i - 1], nums[j - 1]) > 1
```

---

# Why the Transition Is Correct

If `nums[j - 1...i - 1]` is a valid final subarray, then:

- everything before it is the prefix of length `j - 1`
- that prefix can be split optimally using `dp[j - 1]`
- we append this one additional valid subarray

So every valid candidate split ending at `i - 1` has size:

```text
dp[j - 1] + 1
```

Taking the minimum across all such `j` gives the optimal answer for `dp[i]`.

---

# Example Walkthrough

Suppose:

```text
nums = [2, 6, 3, 4]
```

Let us compute the DP conceptually.

## Step 1: Initialization

```text
dp[0] = 0
dp[1...n] = INF
```

---

## Step 2: Compute `dp[1]`

Prefix is:

```text
[2]
```

Only possible last subarray is `[2]`.

Check:

```text
gcd(2, 2) = 2 > 1
```

So:

```text
dp[1] = dp[0] + 1 = 1
```

---

## Step 3: Compute `dp[2]`

Prefix is:

```text
[2, 6]
```

Possible final subarrays:

### `j = 2`

Subarray is `[6]`

```text
gcd(6, 6) = 6 > 1
```

Candidate:

```text
dp[1] + 1 = 2
```

### `j = 1`

Subarray is `[2, 6]`

```text
gcd(2, 6) = 2 > 1
```

Candidate:

```text
dp[0] + 1 = 1
```

So:

```text
dp[2] = 1
```

Meaning the first two elements can stay as one valid subarray.

---

## Step 4: Continue Similarly

At each position, try every possible starting point of the final subarray and check the gcd condition.

That is exactly what the nested loops are doing.

---

# Java Implementation

```java
class Solution {

    static final int INF = 1000000000;

    public int validSubarraySplit(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (gcd(nums[i - 1], nums[j - 1]) > 1) {
                    dp[i] = Math.min(dp[i], dp[j - 1] + 1);
                }
            }
        }
        return dp[n] == INF ? -1 : dp[n];
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
```

---

# Code Explanation

## `INF`

```java
static final int INF = 1000000000;
```

This represents an impossible or not-yet-reachable state.

We initialize all `dp[i]` values to `INF` so that we can later minimize against them.

---

## DP Array

```java
int[] dp = new int[n + 1];
Arrays.fill(dp, INF);
dp[0] = 0;
```

- `dp[0] = 0` because no elements require no subarrays
- all others start as impossible until proven otherwise

---

## Outer Loop

```java
for (int i = 1; i <= n; i++)
```

This computes the answer for the first `i` elements.

---

## Inner Loop

```java
for (int j = 1; j <= i; j++)
```

This tries every possible starting position `j - 1` for the final subarray ending at `i - 1`.

---

## GCD Check

```java
if (gcd(nums[i - 1], nums[j - 1]) > 1)
```

This checks whether the subarray:

```text
nums[j - 1...i - 1]
```

is valid.

Remember: validity depends only on first and last elements.

---

## DP Update

```java
dp[i] = Math.min(dp[i], dp[j - 1] + 1);
```

If the suffix is valid, then the total split count is:

- best split count for the prefix before it
- plus one for this suffix

---

## Final Return

```java
return dp[n] == INF ? -1 : dp[n];
```

If `dp[n]` was never updated, then no valid split exists.

So return `-1`.

Otherwise return the minimum number of valid subarrays.

---

# GCD Function

```java
public static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
}
```

This is the standard Euclidean algorithm.

It works because:

```text
gcd(a, b) = gcd(b, a % b)
```

until `b` becomes `0`.

---

# Complexity Analysis

Let:

- `n` = length of `nums`
- `MAXVAL` = maximum value in `nums`

## Time Complexity

There are two nested loops:

- outer loop over `i`
- inner loop over `j`

That gives:

```text
O(n^2)
```

For each pair `(i, j)`, we compute a GCD.

Using the Euclidean algorithm, each GCD takes:

```text
O(log(MAXVAL))
```

So the full complexity is:

```text
O(n^2 * log(MAXVAL))
```

In many summaries this is informally shortened to quadratic DP with logarithmic gcd cost.

---

## Space Complexity

We only use the DP array of size `n + 1`.

So the extra space is:

```text
O(n)
```

---

# Why This Is a Prefix DP

This is a classic **prefix dynamic programming** problem.

The state:

```text
dp[i]
```

depends on earlier states:

```text
dp[0], dp[1], ..., dp[i-1]
```

Each transition chooses where the final valid subarray begins.

That is exactly the prefix-partitioning pattern.

---

# Important Insight

A subarray is valid if:

```text
gcd(first element, last element) > 1
```

Nothing about the middle matters directly.

That is why we can test validity in constant structural form once we know the endpoints.

This makes the recurrence simple.

---

# Interpretation of the Answer

The value `dp[n]` is:

> the minimum number of valid subarrays needed to split the whole array

It is **not** the number of cut positions.

If you wanted the number of cuts, it would be:

```text
dp[n] - 1
```

provided `dp[n]` is valid.

But this problem asks for the number of subarrays in the split, so returning `dp[n]` is correct.

---

# When the Answer Is `-1`

If no valid decomposition exists, then the DP state for the full array never gets updated from `INF`.

That means there is no way to partition the full array into valid subarrays.

So the result is:

```text
-1
```

---

# Why Greedy Does Not Work Well Here

A greedy rule like:

- always take the longest valid suffix
- or always split as soon as possible

can fail.

Why?

Because choosing one locally valid subarray may block a better partition later.

Since the problem asks for the **minimum number of subarrays**, we must compare many candidate split positions.

That is exactly why DP is needed.

---

# Summary of the Dynamic Programming Strategy

## State

```text
dp[i] = minimum number of valid subarrays for first i elements
```

## Base Case

```text
dp[0] = 0
```

## Transition

For every `j` from `1` to `i`:

```text
if gcd(nums[j - 1], nums[i - 1]) > 1:
    dp[i] = min(dp[i], dp[j - 1] + 1)
```

## Final Answer

```text
dp[n] if reachable, else -1
```

---

# Final Takeaways

1. This is a **partition DP** problem.
2. The last subarray in an optimal split determines the recurrence.
3. The validity of a subarray depends only on its **first and last elements**.
4. The DP is quadratic in the number of elements.
5. GCD checking adds a logarithmic factor in the value range.

---

# Final Insight

The most important conceptual move is this:

> Instead of thinking about how to split the whole array at once, think about where the **last valid subarray** begins.

Once you do that, the recurrence becomes immediate:

- solve the prefix before the last subarray
- add one valid subarray at the end

That is the essence of this dynamic programming solution.
