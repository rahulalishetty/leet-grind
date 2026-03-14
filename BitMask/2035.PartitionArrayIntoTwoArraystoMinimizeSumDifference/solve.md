# 2035. Partition Array Into Two Arrays to Minimize Sum Difference

## Problem Restatement

You are given an array `nums` of length `2 * n`.

You must split it into:

- one array of length `n`
- another array of length `n`

Let their sums be:

- `sum1`
- `sum2`

Your goal is to minimize:

```text
abs(sum1 - sum2)
```

Return the minimum possible value.

---

## Very Important Constraint

```text
1 <= n <= 15
nums.length == 2 * n
```

This is the key.

Since `2 * n <= 30`, the full array can have at most `30` elements.

That is too large for brute force over all subsets of size `n` directly if done naively:

```text
C(30, 15) = 155,117,520
```

Possible in some low-level optimized settings, but not the intended clean approach.

However, `n <= 15` strongly suggests:

- split the array into two halves
- process subsets of each half separately
- combine them cleverly

That is the classic **meet-in-the-middle** pattern.

---

# Core Mathematical Reformulation

Suppose total sum is:

```text
total = sum(nums)
```

If one chosen group of length `n` has sum `picked`, then the other group automatically has sum:

```text
total - picked
```

So the difference becomes:

```text
abs(picked - (total - picked))
= abs(2 * picked - total)
```

So the whole problem becomes:

> Choose exactly `n` elements whose sum is as close as possible to `total / 2`.

That is the real target.

---

# Approach 1: Meet-in-the-Middle (Recommended)

## High-Level Idea

Split `nums` into two halves:

- left half of length `n`
- right half of length `n`

Now suppose we choose:

- `k` elements from the left half
- `n - k` elements from the right half

Then together we have chosen exactly `n` elements.

So the plan is:

1. generate all subset sums of the left half, grouped by how many elements were chosen
2. generate all subset sums of the right half, grouped by how many elements were chosen
3. for every possible `k`, combine:
   - one sum from left choosing `k`
   - one sum from right choosing `n-k`
4. try to get total selected sum as close to `total / 2` as possible

---

## Why grouping by subset size matters

A very common mistake is to only think about subset sums.

But here the chosen group must contain **exactly `n` elements**.

That means when combining left and right halves, the counts must add up exactly to `n`.

So we store:

```text
leftSums[k]  = all subset sums from left half using exactly k elements
rightSums[k] = all subset sums from right half using exactly k elements
```

---

## Step 1: Generate subset sums by count

For each half of size `n`, there are at most:

```text
2^15 = 32768
```

subsets.

For each subset:

- compute number of chosen elements
- compute sum
- store that sum in the corresponding bucket

Example:

```text
leftSums[0], leftSums[1], ..., leftSums[n]
```

---

## Step 2: Sort the right buckets

For efficient searching, sort each `rightSums[k]`.

Then for each sum in `leftSums[k]`, we need a sum in `rightSums[n-k]` such that:

```text
leftSum + rightSum
```

is as close as possible to:

```text
total / 2
```

That means we can use **binary search**.

---

## Step 3: Binary search for the best partner

Let:

- `l = sum from left bucket k`
- we need `r` from `rightSums[n-k]`

We want:

```text
l + r ≈ total / 2
```

So target for `r` is:

```text
target = total / 2 - l
```

Since the list is sorted, binary search gives the closest candidate(s).

For each candidate, compute:

```text
picked = l + r
answer = min(answer, abs(total - 2 * picked))
```

---

## Why this works

Every valid selection of exactly `n` elements can be uniquely seen as:

- some subset from left half
- some subset from right half

with counts adding to `n`.

We generate all such possibilities and evaluate the best one.

So the optimal answer cannot be missed.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumDifference(int[] nums) {
        int n = nums.length / 2;
        int[] left = Arrays.copyOfRange(nums, 0, n);
        int[] right = Arrays.copyOfRange(nums, n, 2 * n);

        List<Integer>[] leftSums = new ArrayList[n + 1];
        List<Integer>[] rightSums = new ArrayList[n + 1];

        for (int i = 0; i <= n; i++) {
            leftSums[i] = new ArrayList<>();
            rightSums[i] = new ArrayList<>();
        }

        generateSums(left, leftSums);
        generateSums(right, rightSums);

        for (int i = 0; i <= n; i++) {
            Collections.sort(rightSums[i]);
        }

        long total = 0;
        for (int x : nums) total += x;

        long ans = Long.MAX_VALUE;

        for (int leftCount = 0; leftCount <= n; leftCount++) {
            int rightCount = n - leftCount;
            List<Integer> rightList = rightSums[rightCount];

            for (int leftSum : leftSums[leftCount]) {
                double need = total / 2.0 - leftSum;

                int idx = lowerBound(rightList, need);

                if (idx < rightList.size()) {
                    long picked = (long) leftSum + rightList.get(idx);
                    ans = Math.min(ans, Math.abs(total - 2 * picked));
                }

                if (idx > 0) {
                    long picked = (long) leftSum + rightList.get(idx - 1);
                    ans = Math.min(ans, Math.abs(total - 2 * picked));
                }
            }
        }

        return (int) ans;
    }

    private void generateSums(int[] arr, List<Integer>[] sums) {
        int m = arr.length;
        int totalMasks = 1 << m;

        for (int mask = 0; mask < totalMasks; mask++) {
            int count = 0;
            int sum = 0;

            for (int i = 0; i < m; i++) {
                if ((mask & (1 << i)) != 0) {
                    count++;
                    sum += arr[i];
                }
            }

            sums[count].add(sum);
        }
    }

    private int lowerBound(List<Integer> list, double target) {
        int left = 0, right = list.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

## Complexity

Let `n` be half the array size.

### Subset generation

Each half has `2^n` subsets.

Computing sums naively costs:

```text
O(n * 2^n)
```

for each half.

### Sorting

Across all buckets, the total number of entries is `2^n`.

Sorting all right buckets together costs roughly:

```text
O(2^n log(2^n)) = O(n * 2^n)
```

### Combining

For each left subset sum, perform binary search:

```text
O(2^n log(2^n))
```

### Total

```text
O(n * 2^n)
```

up to logarithmic factors, which is excellent for `n <= 15`.

Space:

```text
O(2^n)
```

---

## Deep Intuition

Why does this problem need meet-in-the-middle?

Because the direct state space is just a bit too large:

- 30 elements overall
- exact-size selection constraint
- values can be negative, so classic knapsack-by-sum DP is not practical

This is exactly the type of setting where:

- `N = 30` is too large for full subset brute force over all 30 bits
- but `N/2 = 15` is small enough for subset enumeration

That is the textbook MITM zone.

---

# Approach 2: Meet-in-the-Middle with Signed Contribution View

## A Different Way to Think About It

Instead of selecting which `n` elements go into the first group, think of assigning each element to one of two groups.

Suppose:

- elements in group A contribute `+nums[i]`
- elements in group B contribute `-nums[i]`

Then the final difference is:

```text
sum(A) - sum(B)
```

We want its absolute value minimized.

But we also must enforce:

- group A has exactly `n` elements
- group B has exactly `n` elements

This leads to the same grouping-by-count structure, but the mental model is different.

---

## How it maps to the first approach

If we choose `n` elements into group A with sum `picked`, then:

```text
difference = picked - (total - picked)
           = 2 * picked - total
```

So this approach is mathematically identical, but the interpretation is:

> choose balanced counts and make signed difference close to zero

This view is useful when explaining correctness.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumDifference(int[] nums) {
        int n = nums.length / 2;
        int[] left = Arrays.copyOfRange(nums, 0, n);
        int[] right = Arrays.copyOfRange(nums, n, 2 * n);

        List<Integer>[] leftDiffs = new ArrayList[n + 1];
        List<Integer>[] rightDiffs = new ArrayList[n + 1];

        for (int i = 0; i <= n; i++) {
            leftDiffs[i] = new ArrayList<>();
            rightDiffs[i] = new ArrayList<>();
        }

        generate(left, leftDiffs);
        generate(right, rightDiffs);

        for (int i = 0; i <= n; i++) {
            Collections.sort(rightDiffs[i]);
        }

        int ans = Integer.MAX_VALUE;

        for (int leftCount = 0; leftCount <= n; leftCount++) {
            int rightCount = n - leftCount;
            List<Integer> list = rightDiffs[rightCount];

            for (int x : leftDiffs[leftCount]) {
                int idx = lowerBound(list, -x);

                if (idx < list.size()) {
                    ans = Math.min(ans, Math.abs(x + list.get(idx)));
                }

                if (idx > 0) {
                    ans = Math.min(ans, Math.abs(x + list.get(idx - 1)));
                }
            }
        }

        return ans;
    }

    // For a subset with chosen elements contributing +arr[i]
    // and unchosen contributing -arr[i], grouped by number chosen.
    private void generate(int[] arr, List<Integer>[] bucket) {
        int m = arr.length;
        int totalMasks = 1 << m;

        for (int mask = 0; mask < totalMasks; mask++) {
            int count = 0;
            int diff = 0;

            for (int i = 0; i < m; i++) {
                if ((mask & (1 << i)) != 0) {
                    count++;
                    diff += arr[i];
                } else {
                    diff -= arr[i];
                }
            }

            bucket[count].add(diff);
        }
    }

    private int lowerBound(List<Integer> list, int target) {
        int left = 0, right = list.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

## Complexity

Same asymptotic complexity as Approach 1:

```text
O(n * 2^n)
```

Space:

```text
O(2^n)
```

---

## Pros

- Same strength as the standard MITM solution
- The “difference close to zero” viewpoint is mathematically elegant

## Cons

- Slightly less intuitive if you first think in terms of subset sums
- The signed contribution trick can feel abstract initially

---

# Approach 3: Backtracking / Brute Force Over Choosing n Elements

## Idea

Try all ways to choose exactly `n` elements out of `2n`.

Track:

- how many selected so far
- current selected sum

When exactly `n` elements are chosen, compute:

```text
abs(total - 2 * selectedSum)
```

Take the minimum.

---

## Java Code

```java
class Solution {
    private long ans;
    private long total;
    private int n;
    private int[] nums;

    public int minimumDifference(int[] nums) {
        this.nums = nums;
        this.n = nums.length / 2;
        this.ans = Long.MAX_VALUE;
        this.total = 0;

        for (int x : nums) total += x;

        dfs(0, 0, 0L);
        return (int) ans;
    }

    private void dfs(int index, int chosen, long sum) {
        if (chosen > n) return;
        if (nums.length - index < n - chosen) return;

        if (index == nums.length) {
            if (chosen == n) {
                ans = Math.min(ans, Math.abs(total - 2 * sum));
            }
            return;
        }

        // choose nums[index]
        dfs(index + 1, chosen + 1, sum + nums[index]);

        // skip nums[index]
        dfs(index + 1, chosen, sum);
    }
}
```

---

## Complexity

This explores combinations of size `n` from `2n`:

```text
O(C(2n, n))
```

At worst:

```text
C(30, 15) ≈ 1.55 * 10^8
```

That is too large for a clean accepted solution in Java in most settings.

Space:

```text
O(n)
```

for recursion depth.

---

## Pros

- Conceptually very simple
- Good for understanding the problem structure

## Cons

- Too slow in the worst case
- Not the intended solution

---

# Approach 4: DP by Sum (Why it is not practical here)

## Tempting Idea

You may think of classic subset sum DP:

```text
dp[i][count][sum] = whether we can choose `count` elements among first `i` elements to get sum `sum`
```

Then search for the achievable sum closest to `total / 2`.

This works beautifully when values are small and non-negative.

---

## Why it fails here

Constraints include:

```text
-10^7 <= nums[i] <= 10^7
```

So possible sums can be enormous and also negative.

Even with offsetting, the sum range is far too large.

So sum-based DP is not practical.

---

## Verdict

This is a useful thought path, but not a feasible implementation here.

---

# Example Walkthrough for Approach 1

## Example 1

```text
nums = [3, 9, 7, 3]
n = 2
```

Split into:

```text
left  = [3, 9]
right = [7, 3]
```

### Left subset sums by count

- choose 0: `[0]`
- choose 1: `[3, 9]`
- choose 2: `[12]`

### Right subset sums by count

- choose 0: `[0]`
- choose 1: `[7, 3]`
- choose 2: `[10]`

Total sum:

```text
22
```

We want chosen sum as close as possible to:

```text
11
```

Try:

- left choose 1, right choose 1
- `3 + 7 = 10`
- `3 + 3 = 6`
- `9 + 7 = 16`
- `9 + 3 = 12`

Closest picked sums to 11 are 10 and 12.

Difference:

```text
abs(22 - 2 * 10) = 2
abs(22 - 2 * 12) = 2
```

Answer is:

```text
2
```

---

## Example 3

```text
nums = [2, -1, 0, 4, -2, -9]
n = 3
```

Total sum:

```text
-6
```

We want chosen sum close to:

```text
-3
```

One valid chosen group is:

```text
[2, 4, -9] -> sum = -3
```

Then the other group also sums to `-3`, so difference is:

```text
0
```

Optimal.

---

# Why Binary Search Is Enough

For a fixed `leftSum`, we want a `rightSum` that makes:

```text
leftSum + rightSum
```

as close as possible to `total / 2`.

Once the candidate list is sorted, the best value must be:

- the first value `>= target`
- or the one just before it

No other element can be closer.

That is exactly why checking `idx` and `idx - 1` is sufficient.

---

# Correctness Sketch for Approach 1

We prove the algorithm finds the optimal answer.

## Claim 1

Every valid partition corresponds to choosing exactly `n` elements whose sum is `picked`, and its difference equals:

```text
abs(total - 2 * picked)
```

This follows directly from algebra.

## Claim 2

Every choice of exactly `n` elements can be split into:

- `k` selected elements from left half
- `n-k` selected elements from right half

for some `k`.

Since the array is partitioned into left and right halves, this is unavoidable.

## Claim 3

The algorithm enumerates all such possibilities.

It generates all subset sums for each half, grouped by chosen count.
So for any valid selection, its left contribution and right contribution both appear in the proper buckets.

## Claim 4

For each fixed left sum, binary search finds the best possible right sum from the needed bucket.

Because the right bucket is sorted, the closest value to target must be at or adjacent to lower bound.

From Claims 1–4, the algorithm evaluates the optimal partition, so the answer is correct.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    public int minimumDifference(int[] nums) {
        int n = nums.length / 2;
        int[] left = Arrays.copyOfRange(nums, 0, n);
        int[] right = Arrays.copyOfRange(nums, n, 2 * n);

        List<Integer>[] leftSums = new ArrayList[n + 1];
        List<Integer>[] rightSums = new ArrayList[n + 1];

        for (int i = 0; i <= n; i++) {
            leftSums[i] = new ArrayList<>();
            rightSums[i] = new ArrayList<>();
        }

        generateSums(left, leftSums);
        generateSums(right, rightSums);

        for (int i = 0; i <= n; i++) {
            Collections.sort(rightSums[i]);
        }

        long total = 0;
        for (int x : nums) total += x;

        long ans = Long.MAX_VALUE;

        for (int leftCount = 0; leftCount <= n; leftCount++) {
            int rightCount = n - leftCount;
            List<Integer> rightList = rightSums[rightCount];

            for (int leftSum : leftSums[leftCount]) {
                double target = total / 2.0 - leftSum;
                int idx = lowerBound(rightList, target);

                if (idx < rightList.size()) {
                    long picked = (long) leftSum + rightList.get(idx);
                    ans = Math.min(ans, Math.abs(total - 2 * picked));
                }

                if (idx > 0) {
                    long picked = (long) leftSum + rightList.get(idx - 1);
                    ans = Math.min(ans, Math.abs(total - 2 * picked));
                }
            }
        }

        return (int) ans;
    }

    private void generateSums(int[] arr, List<Integer>[] bucket) {
        int m = arr.length;
        int totalMasks = 1 << m;

        for (int mask = 0; mask < totalMasks; mask++) {
            int count = 0;
            int sum = 0;

            for (int i = 0; i < m; i++) {
                if ((mask & (1 << i)) != 0) {
                    count++;
                    sum += arr[i];
                }
            }

            bucket[count].add(sum);
        }
    }

    private int lowerBound(List<Integer> list, double target) {
        int left = 0, right = list.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                    |   Time Complexity | Space Complexity | Practical? |
| ---------- | -------------------------------------------- | ----------------: | ---------------: | ---------- |
| Approach 1 | Meet-in-the-middle with subset sums by count |      `O(n * 2^n)` |         `O(2^n)` | Yes, best  |
| Approach 2 | Meet-in-the-middle using signed differences  |      `O(n * 2^n)` |         `O(2^n)` | Yes        |
| Approach 3 | Brute force choose `n` elements              |     `O(C(2n, n))` |           `O(n)` | No         |
| Approach 4 | DP by sum                                    | Pseudo-polynomial |             Huge | No         |

---

# Pattern Recognition Takeaway

This is a classic signal for **meet-in-the-middle**:

- total elements around 30
- exact-size subset selection
- values too large for sum-DP
- need optimization over subset sums

A useful rule of thumb:

- `N <= 20`: direct subset enumeration may work
- `N around 30–40`: strongly suspect meet-in-the-middle
- huge values: avoid sum-based DP unless range is small

That is exactly this problem.

---

# Final Takeaway

The cleanest solution is:

1. split array into two halves
2. enumerate subset sums in each half, grouped by chosen count
3. for each left subset, binary search the best complementary right subset
4. minimize:

```text
abs(total - 2 * pickedSum)
```

That is the intended and most robust solution for this problem.
