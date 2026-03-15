# 3231. Minimum Number of Increasing Subsequences to Be Removed — Java Solutions and Detailed Notes

## Problem

We are given an array `nums`.

In one operation, we may remove a **strictly increasing subsequence**.

We want the **minimum number of operations** needed to remove the entire array.

---

## Examples

### Example 1

```text
nums = [5,3,1,4,2]
```

Answer:

```text
3
```

One valid removal sequence:

- `[1,2]`
- `[3,4]`
- `[5]`

---

### Example 2

```text
nums = [1,2,3,4,5]
```

Answer:

```text
1
```

The whole array is strictly increasing.

---

### Example 3

```text
nums = [5,4,3,2,1]
```

Answer:

```text
5
```

No two elements can belong to the same strictly increasing subsequence.

---

# Core insight

This problem looks like a subsequence-removal problem, but the answer has a very clean form.

The minimum number of strictly increasing subsequences needed to partition the array is exactly:

```text
the length of the longest non-increasing subsequence
```

For this problem, because values can repeat and subsequences must be **strictly increasing**, the equivalent compact answer is:

```text
maximum frequency of any value in nums
```

That is the key result.

---

# Why is the answer the maximum frequency?

Suppose some value `x` appears `f` times.

Because every removed subsequence must be **strictly increasing**, no subsequence can contain `x` more than once.

So those `f` copies of `x` must be spread across at least `f` different operations.

Therefore:

```text
answer >= max frequency
```

Now we need to show this lower bound is also achievable.

It turns out it is. By a standard consequence of Dilworth / Mirsky style ordering arguments, the minimum number of strictly increasing subsequences needed to partition the sequence equals the maximum size of a non-increasing subsequence. In this setting, that value collapses to the maximum frequency.

So:

```text
answer = max frequency of any number
```

---

# Very important simplification

That means we do **not** need dynamic programming, greedy reconstruction, Fenwick trees, or LIS-style patience sorting to compute the final answer.

We only need:

```text
count frequencies
take the maximum
```

---

# Approach 1: Frequency Counting with Array (Optimal when value range is small)

## Idea

The constraints say:

```text
1 <= nums[i] <= 10^5
```

So we can use a frequency array of size `100001`.

For each number:

- increment its count
- track the maximum count seen so far

That maximum count is the answer.

---

## Java code

```java
class Solution {
    public int minOperations(int[] nums) {
        int[] freq = new int[100001];
        int ans = 0;

        for (int x : nums) {
            freq[x]++;
            ans = Math.max(ans, freq[x]);
        }

        return ans;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n)
```

Space complexity:

```text
O(U)
```

where `U = 100000` is the value bound.

Since `U` is fixed by constraints, this is effectively very efficient.

---

# Approach 2: Frequency Counting with HashMap

## Idea

If we did not want to rely on the value bound, we could use a `HashMap<Integer, Integer>`.

This is more general and still linear on average.

---

## Java code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int minOperations(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        int ans = 0;

        for (int x : nums) {
            int count = freq.getOrDefault(x, 0) + 1;
            freq.put(x, count);
            ans = Math.max(ans, count);
        }

        return ans;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n)
```

on average.

Space complexity:

```text
O(distinct values)
```

---

# Approach 3: Ordered-structure / patience-sorting interpretation (educational)

This is **not necessary** for implementation here, but it helps explain the theorem.

## Idea

Imagine building the minimum number of strictly increasing subsequences.

A classic greedy interpretation is:

- process the numbers from left to right
- place each number into an existing subsequence if possible
- otherwise start a new subsequence

This is dual to chain decomposition ideas.

The theoretical minimum number of increasing subsequences needed equals the size of the largest non-increasing subsequence.

For this problem, because duplicates alone already force different subsequences and are enough to determine the optimum, the result simplifies to the maximum frequency.

So this approach is useful conceptually, but overkill in practice.

---

# Formal proof that answer = maximum frequency

We prove both directions.

## Lower bound

Let value `v` appear `f` times.

A strictly increasing subsequence cannot contain the same value twice.

So each of the `f` copies of `v` must belong to different removed subsequences.

Hence any valid solution needs at least `f` operations.

Since this holds for every value:

```text
answer >= max frequency
```

---

## Upper bound

Now let:

```text
M = max frequency
```

We want to show the array can always be partitioned into exactly `M` strictly increasing subsequences.

This follows from the general theorem:

> The minimum number of strictly increasing subsequences needed to partition a sequence equals the length of its longest non-increasing subsequence.

In this problem setting, the largest obstruction comes from repeated equal values, and the optimum equals the maximum multiplicity.

So:

```text
answer <= M
```

Combining with the lower bound:

```text
answer = M
```

---

# Practical takeaway

For coding interviews or contests, once this observation is known, the entire problem reduces to:

```text
return maximum occurrence count of any element
```

---

# Walkthrough on the examples

## Example 1

```text
nums = [5,3,1,4,2]
```

Frequencies:

- `1 -> 1`
- `2 -> 1`
- `3 -> 1`
- `4 -> 1`
- `5 -> 1`

Maximum frequency = `1`?

At first glance that seems to suggest answer `1`, but this conflicts with the sample. That means the stronger “answer = max frequency” simplification is **not sufficient by itself for arbitrary order**.

So we must correct the statement:

The true answer is:

```text
length of the longest non-increasing subsequence
```

and **not merely** the maximum frequency.

Maximum frequency is only a lower bound, and equals the answer in some but not all cases.

For `[5,3,1,4,2]`, the longest non-increasing subsequence is:

```text
[5,3,1]
```

with length `3`, so the answer is `3`.

This matches the example.

---

# Correct core theorem

The minimum number of strictly increasing subsequences needed to partition an array equals:

```text
the length of the longest non-increasing subsequence (LNIS)
```

That is the actual correct solution.

So let us now present the right approaches.

---

# Approach 1 (Correct): Dynamic Programming brute force for LNIS (too slow)

## Idea

Compute the longest non-increasing subsequence length using classic O(n^2) DP.

Define:

```text
dp[i] = length of the longest non-increasing subsequence ending at i
```

Transition:

If:

```text
nums[j] >= nums[i]   for j < i
```

then we may append `nums[i]` after `nums[j]`.

So:

```text
dp[i] = max(dp[i], dp[j] + 1)
```

The answer is the maximum `dp[i]`.

---

## Java code

```java
class Solution {
    public int minOperations(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        int ans = 0;

        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] >= nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            ans = Math.max(ans, dp[i]);
        }

        return ans;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n^2)
```

Space complexity:

```text
O(n)
```

Too slow for `n = 10^5`.

---

# Approach 2 (Optimal): Patience Sorting style LNIS in O(n log n)

## Idea

We need the length of the **longest non-increasing subsequence**.

This is equivalent to the length of the **longest increasing subsequence** after negating values and switching the comparison appropriately, or more directly, we can run a patience-sorting style algorithm customized for non-increasing subsequences.

A simpler route:

- convert each value `x` to `-x`
- compute the LIS length of that transformed array where equal values must be handled carefully

For LNIS on original array, we need **non-increasing**:

```text
a1 >= a2 >= a3 ...
```

After negation:

```text
-a1 <= -a2 <= -a3 ...
```

So we need the **longest non-decreasing subsequence** on the negated values.

That can be computed with patience sorting using `upperBound`.

---

## Patience sorting rule here

For longest non-decreasing subsequence:

- maintain `tails`
- for each value, place it at the first index with value `> current`
- if none exists, append it

This is `upper_bound`.

Since Java has no built-in upper bound for arrays, we implement it manually.

---

## Java code

```java
class Solution {
    public int minOperations(int[] nums) {
        int n = nums.length;
        int[] tails = new int[n];
        int size = 0;

        for (int x : nums) {
            int val = -x; // transform LNIS into LNDS
            int pos = upperBound(tails, size, val);
            tails[pos] = val;
            if (pos == size) {
                size++;
            }
        }

        return size;
    }

    // first index in tails[0..size) with tails[idx] > target
    private int upperBound(int[] tails, int size, int target) {
        int left = 0, right = size;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (tails[mid] <= target) {
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

## Why this works

The patience-sorting method computes the length of the longest non-decreasing subsequence on `-nums`, which is exactly the longest non-increasing subsequence on `nums`.

That LNIS length equals the minimum number of strictly increasing subsequences needed to partition the array.

Hence it equals the minimum number of removal operations.

---

## Complexity

Time complexity:

```text
O(n log n)
```

Space complexity:

```text
O(n)
```

This is optimal for the constraints.

---

# Approach 3: Multiset / greedy partition interpretation (educational)

## Idea

Another way to view the problem:

We want to partition the array into the fewest strictly increasing subsequences.

Process elements left to right, and greedily place each number into an existing subsequence whose last value is **strictly smaller** than the current number.

To minimize the number of subsequences, we should place it into the subsequence with the **largest possible tail < current**.

If no such subsequence exists, we must start a new one.

The number of subsequences created at the end is the answer.

This is equivalent to a greedy chain decomposition and matches the LNIS theorem.

To implement efficiently, we would need an ordered multiset of tails.

In Java, this can be simulated with `TreeMap`.

---

## Java code

```java
import java.util.Map;
import java.util.TreeMap;

class Solution {
    public int minOperations(int[] nums) {
        TreeMap<Integer, Integer> tails = new TreeMap<>();

        for (int x : nums) {
            Integer key = tails.lowerKey(x); // largest tail < x

            if (key != null) {
                decrement(tails, key);
            }

            tails.put(x, tails.getOrDefault(x, 0) + 1);
        }

        int ans = 0;
        for (int count : tails.values()) {
            ans += count;
        }
        return ans;
    }

    private void decrement(TreeMap<Integer, Integer> map, int key) {
        int count = map.get(key);
        if (count == 1) {
            map.remove(key);
        } else {
            map.put(key, count - 1);
        }
    }
}
```

---

## Complexity

Each operation on `TreeMap` is `O(log n)`.

Total time complexity:

```text
O(n log n)
```

Space complexity:

```text
O(n)
```

This is also efficient, though the patience-sorting version is usually lighter.

---

# Comparison of approaches

## Approach 1: O(n^2) DP for LNIS

### Pros

- straightforward
- easy to derive

### Cons

- too slow for large `n`

### Complexity

```text
Time:  O(n^2)
Space: O(n)
```

---

## Approach 2: Patience sorting for LNIS (Recommended)

### Pros

- optimal
- concise
- directly computes the needed subsequence length

### Cons

- requires knowing the LNIS/LNDS transformation

### Complexity

```text
Time:  O(n log n)
Space: O(n)
```

---

## Approach 3: Greedy partition with TreeMap

### Pros

- directly models the partitioning process
- elegant ordered-structure interpretation

### Cons

- slightly more bookkeeping than patience sorting

### Complexity

```text
Time:  O(n log n)
Space: O(n)
```

---

# Final recommended Java solution

```java
class Solution {
    public int minOperations(int[] nums) {
        int n = nums.length;
        int[] tails = new int[n];
        int size = 0;

        for (int x : nums) {
            int val = -x;
            int pos = upperBound(tails, size, val);
            tails[pos] = val;
            if (pos == size) {
                size++;
            }
        }

        return size;
    }

    private int upperBound(int[] tails, int size, int target) {
        int left = 0, right = size;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (tails[mid] <= target) {
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

# Worked examples

## Example 1

```text
nums = [5,3,1,4,2]
```

A longest non-increasing subsequence is:

```text
[5,3,1]
```

length `3`.

So answer = `3`.

---

## Example 2

```text
nums = [1,2,3,4,5]
```

Longest non-increasing subsequence has length `1`.

So answer = `1`.

---

## Example 3

```text
nums = [5,4,3,2,1]
```

The whole array is non-increasing, so LNIS length is `5`.

Answer = `5`.

---

# Key theorem behind the solution

The minimum number of strictly increasing subsequences needed to partition a sequence equals the length of the longest non-increasing subsequence.

This is the dual form of a classic chain decomposition theorem.

So the entire problem is really:

```text
compute LNIS length
```

Once that is recognized, the problem becomes standard.

---

# Takeaway pattern

If a problem asks for:

```text
minimum number of increasing subsequences needed to cover the array
```

or equivalently repeated removal of increasing subsequences,

you should immediately think of the dual statement:

```text
answer = longest non-increasing subsequence length
```

Then use an `O(n log n)` LIS-style method.
