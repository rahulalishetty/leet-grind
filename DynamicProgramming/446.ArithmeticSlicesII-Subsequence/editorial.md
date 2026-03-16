# 446. Arithmetic Slices II - Subsequence — Exhaustive Solution Notes

## Overview

This problem asks us to count **all arithmetic subsequences** in an array.

Two details make the problem tricky:

1. The subsequences do **not** need to be contiguous.
2. A valid arithmetic subsequence must have length **at least 3**.

At first glance, brute force seems possible: generate every subsequence and check whether it is arithmetic.
But that is far too slow because the number of subsequences grows exponentially.

The accepted solution uses **dynamic programming with hash maps**, and the key idea is to count not only complete arithmetic subsequences, but also **partial or weak arithmetic subsequences** of length 2.

This write-up explains both approaches in detail:

1. **Brute Force**
2. **Dynamic Programming**

---

## Problem Statement

Given an integer array `nums`, return the number of all arithmetic subsequences of `nums`.

A sequence is arithmetic if:

- it has at least **three elements**
- the difference between every pair of consecutive elements is the same

A subsequence is formed by deleting some elements without changing the order of the remaining elements.

The answer is guaranteed to fit in a 32-bit signed integer.

---

## Example 1

**Input**

```text
nums = [2,4,6,8,10]
```

**Output**

```text
7
```

**Explanation**

All arithmetic subsequences are:

```text
[2,4,6]
[4,6,8]
[6,8,10]
[2,4,6,8]
[4,6,8,10]
[2,4,6,8,10]
[2,6,10]
```

So the answer is:

```text
7
```

---

## Example 2

**Input**

```text
nums = [7,7,7,7,7]
```

**Output**

```text
16
```

**Explanation**

Every subsequence of length at least 3 is arithmetic because all consecutive differences are `0`.

So the total number of valid arithmetic subsequences is:

```text
16
```

---

## Constraints

- `1 <= nums.length <= 1000`
- `-2^31 <= nums[i] <= 2^31 - 1`

---

# Why Brute Force Is Too Slow

For every element in the array, we have two choices:

- include it in the subsequence
- exclude it from the subsequence

So the total number of subsequences is:

```text
2^n
```

For `n = 1000`, this is completely infeasible.

That is why we need a polynomial-time solution.

---

# Approach 1: Brute Force [Time Limit Exceeded]

## Intuition

The most direct solution is:

1. generate all subsequences
2. keep only those with length at least 3
3. check whether each is arithmetic
4. count how many pass

This approach is conceptually simple, but it is much too slow.

---

## Algorithm

We can use depth-first search to generate all subsequences.

At each array element `A[dep]`, we decide:

- skip it
- include it

Once we reach the end of the array:

- if the current subsequence has fewer than 3 elements, ignore it
- otherwise, compute the common difference using the first two elements
- verify that all consecutive differences match
- if yes, increment the answer

---

## Java Implementation — Brute Force

```java
class Solution {
    private int n;
    private int ans;

    private void dfs(int dep, int[] A, List<Long> cur) {
        if (dep == n) {
            if (cur.size() < 3) {
                return;
            }

            long diff = cur.get(1) - cur.get(0);
            for (int i = 1; i < cur.size(); i++) {
                if (cur.get(i) - cur.get(i - 1) != diff) {
                    return;
                }
            }

            ans++;
            return;
        }

        dfs(dep + 1, A, cur);

        cur.add((long) A[dep]);
        dfs(dep + 1, A, cur);
        cur.remove((long) A[dep]);
    }

    public int numberOfArithmeticSlices(int[] A) {
        n = A.length;
        ans = 0;
        List<Long> cur = new ArrayList<Long>();
        dfs(0, A, cur);
        return ans;
    }
}
```

---

## Complexity Analysis — Brute Force

### Time Complexity

Every element can either be included or excluded, so there are:

```text
O(2^n)
```

subsequences.

Checking each subsequence can also take linear time in the subsequence length, but the dominant factor is still exponential.

So the time complexity is:

```text
O(2^n)
```

---

### Space Complexity

The recursion stack and current subsequence storage both take:

```text
O(n)
```

So the space complexity is:

```text
O(n)
```

---

# Approach 2: Dynamic Programming [Accepted]

## Intuition

To determine whether a sequence is arithmetic, we need at least two pieces of information:

1. where the sequence ends
2. what the common difference is

That suggests a DP state involving:

- an ending index
- a common difference

But there is a subtle issue.

A valid arithmetic subsequence must have length at least 3, so how do we start building them?

The trick is to count **weak arithmetic subsequences**.

---

# Weak Arithmetic Subsequences

We define a **weak arithmetic subsequence** as:

> a subsequence of length at least 2 whose consecutive differences are all equal

So weak arithmetic subsequences include:

- all valid arithmetic subsequences of length `>= 3`
- plus all length-2 pairs

Why is this useful?

Because any pair of indices `(j, i)` always forms a weak arithmetic subsequence.

That gives us a clean base for DP transitions.

---

## DP State Definition

Let:

```text
f[i][d]
```

denote:

> the number of weak arithmetic subsequences that end at index `i` and have common difference `d`

This is the key DP state.

---

## Transition

Suppose we want to compute states ending at `A[i]`.

For every earlier index `j < i`, the difference is:

```text
d = A[i] - A[j]
```

Now consider all weak arithmetic subsequences ending at `j` with difference `d`.

There are `f[j][d]` such subsequences.

If we append `A[i]` to each of them, they remain arithmetic, and now they end at `i`.

Also, the pair:

```text
[A[j], A[i]]
```

itself forms a new weak arithmetic subsequence of length 2.

So the transition is:

```text
f[i][d] += f[j][d] + 1
```

where:

- `f[j][d]` = existing weak arithmetic subsequences extended by `A[i]`
- `1` = the new pair `(j, i)`

---

# Where the Final Answer Comes From

We are asked to count arithmetic subsequences of length at least 3.

In the transition:

```text
f[i][d] += f[j][d] + 1
```

the `+1` corresponds to a new pair of length 2, which is **not yet** a valid arithmetic subsequence.

But every sequence counted in `f[j][d]` already has length at least 2, so extending it by `A[i]` gives a sequence of length at least 3.

Therefore:

- `f[j][d]` contributes directly to the final answer
- `+1` updates the DP table but does **not** contribute to the final answer

So at each pair `(j, i)`:

```text
answer += f[j][d]
```

This is the key counting idea.

---

# Why This Works

Suppose we already have a weak arithmetic subsequence ending at index `j` with difference `d`.

If:

```text
A[i] - A[j] = d
```

then appending `A[i]` preserves the common difference.

So every such weak subsequence becomes a longer arithmetic subsequence ending at `i`.

Meanwhile, the pair `(A[j], A[i])` starts a new weak subsequence for possible future extension.

---

# Example Walkthrough

Consider:

```text
nums = [2,4,6,8,10]
```

We process each index from left to right.

---

## At `i = 1` (`4`)

Compare with `j = 0` (`2`):

```text
diff = 4 - 2 = 2
```

There are no previous weak subsequences ending at `0` with diff `2`.

So:

```text
f[1][2] = 0 + 1 = 1
```

This corresponds to the weak subsequence:

```text
[2,4]
```

No arithmetic subsequence of length 3 yet, so answer stays `0`.

---

## At `i = 2` (`6`)

Compare with `j = 1` (`4`):

```text
diff = 6 - 4 = 2
```

Now `f[1][2] = 1`, which corresponds to:

```text
[2,4]
```

Appending `6` creates:

```text
[2,4,6]
```

which is a valid arithmetic subsequence.

So:

- add `1` to answer
- update:
  ```text
  f[2][2] += 1 + 1 = 2
  ```

These two weak subsequences are:

```text
[4,6]
[2,4,6]
```

Continue similarly and all valid arithmetic subsequences get counted exactly once.

---

# Why We Need Hash Maps

The common difference `d` can vary widely.

Since:

```text
nums[i] can be as small as -2^31
nums[i] can be as large as  2^31 - 1
```

the difference can overflow a 32-bit integer if not handled carefully.

Also, there may be many different differences per index.

So instead of a fixed 2D array on differences, we store for each index:

```text
Map<difference, count>
```

That is both flexible and space-efficient.

---

# Overflow Handling

When computing:

```text
delta = (long) A[i] - (long) A[j]
```

we must use `long`, not `int`.

Otherwise integer overflow may occur.

After computing the difference safely, we only proceed if the difference fits within `int`, because the map uses `Integer` as the key in this implementation.

---

## Java Implementation — Dynamic Programming

```java
class Solution {
    public int numberOfArithmeticSlices(int[] A) {
        int n = A.length;
        long ans = 0;

        Map<Integer, Integer>[] cnt = new Map[n];

        for (int i = 0; i < n; i++) {
            cnt[i] = new HashMap<>(i);

            for (int j = 0; j < i; j++) {
                long delta = (long) A[i] - (long) A[j];

                if (delta < Integer.MIN_VALUE || delta > Integer.MAX_VALUE) {
                    continue;
                }

                int diff = (int) delta;

                int sum = cnt[j].getOrDefault(diff, 0);
                int origin = cnt[i].getOrDefault(diff, 0);

                cnt[i].put(diff, origin + sum + 1);

                ans += sum;
            }
        }

        return (int) ans;
    }
}
```

---

# Breaking the Code Down Carefully

For each pair `(j, i)`:

### Step 1: Compute difference

```java
long delta = (long) A[i] - (long) A[j];
```

### Step 2: Skip overflowed differences

```java
if (delta < Integer.MIN_VALUE || delta > Integer.MAX_VALUE) {
    continue;
}
```

### Step 3: Look up how many weak subsequences already end at `j` with this difference

```java
int sum = cnt[j].getOrDefault(diff, 0);
```

### Step 4: Look up current count for `i` and this difference

```java
int origin = cnt[i].getOrDefault(diff, 0);
```

### Step 5: Update DP

```java
cnt[i].put(diff, origin + sum + 1);
```

Why `+1`?

Because `(A[j], A[i])` is always a new weak arithmetic subsequence of length 2.

### Step 6: Add only `sum` to answer

```java
ans += sum;
```

Why not `sum + 1`?

Because only the extensions of existing weak subsequences produce valid arithmetic subsequences of length at least 3.

The fresh pair is only length 2.

---

# Complexity Analysis — Dynamic Programming

### Time Complexity

We use a double loop over all pairs `(j, i)` with `j < i`.

That gives:

```text
O(n^2)
```

Each map operation is expected `O(1)` on average.

So the total time complexity is:

```text
O(n^2)
```

---

### Space Complexity

For each index `i`, the map `cnt[i]` may store up to `O(n)` different differences.

Across all indices, that gives:

```text
O(n^2)
```

space in the worst case.

---

# Why the DP Counts Exactly the Right Subsequences

Every arithmetic subsequence of length at least 3 has a unique last two indices `(j, i)` and a unique common difference `d`.

When we process pair `(j, i)`, we extend exactly the weak subsequences ending at `j` with that difference.

So every valid arithmetic subsequence is counted exactly once, at the moment its final element is appended.

That is why the answer is correct and there is no double-counting.

---

# Common Mistakes

## 1. Counting the new pair `(j, i)` in the answer

This is wrong because a pair has length only 2.

Only extensions of existing weak arithmetic subsequences create valid arithmetic subsequences of length at least 3.

So:

```text
answer += f[j][d]
```

not:

```text
answer += f[j][d] + 1
```

---

## 2. Using `int` for differences directly

This can overflow because:

```text
nums[i] - nums[j]
```

may exceed 32-bit range.

Always compute the difference using `long`.

---

## 3. Confusing subsequences with subarrays

This problem is about **subsequences**, not contiguous slices.

So indices can be skipped.

That is what makes brute force exponential and also why the DP state must be more careful.

---

## 4. Forgetting that duplicates are allowed

Arrays like:

```text
[7,7,7,7,7]
```

produce many arithmetic subsequences with common difference `0`.

The DP handles this naturally.

---

# Interview Perspective

This problem is an important DP pattern.

A strong explanation usually goes like this:

1. Brute force is exponential because every subsequence may be chosen or skipped.
2. To characterize arithmetic subsequences, we need:
   - ending index
   - common difference
3. Since valid arithmetic subsequences require length at least 3, it helps to count **weak arithmetic subsequences** of length at least 2.
4. Transition:
   ```text
   f[i][d] += f[j][d] + 1
   ```
5. Only `f[j][d]` contributes to the final answer.

That progression shows real understanding.

---

# Final Summary

## Brute Force

### Idea

Generate every subsequence and test whether it is arithmetic.

### Complexity

- Time: `O(2^n)`
- Space: `O(n)`

### Status

Too slow.

---

## Dynamic Programming

### State

```text
f[i][d] = number of weak arithmetic subsequences ending at index i with common difference d
```

### Transition

For every `j < i`:

```text
d = A[i] - A[j]
f[i][d] += f[j][d] + 1
answer += f[j][d]
```

### Complexity

- Time: `O(n^2)`
- Space: `O(n^2)`

### Status

Accepted.

---

# Best Final Java Solution

```java
class Solution {
    public int numberOfArithmeticSlices(int[] A) {
        int n = A.length;
        long ans = 0;
        Map<Integer, Integer>[] cnt = new Map[n];

        for (int i = 0; i < n; i++) {
            cnt[i] = new HashMap<>(i);

            for (int j = 0; j < i; j++) {
                long delta = (long) A[i] - (long) A[j];
                if (delta < Integer.MIN_VALUE || delta > Integer.MAX_VALUE) {
                    continue;
                }

                int diff = (int) delta;
                int sum = cnt[j].getOrDefault(diff, 0);
                int origin = cnt[i].getOrDefault(diff, 0);

                cnt[i].put(diff, origin + sum + 1);
                ans += sum;
            }
        }

        return (int) ans;
    }
}
```

This is the standard optimal dynamic programming solution for the problem.
