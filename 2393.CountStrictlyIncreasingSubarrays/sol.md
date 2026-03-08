# 2393. Count Strictly Increasing Subarrays — Detailed Notes

## Problem Restatement

Given an array `nums` of positive integers, count how many **contiguous subarrays** are **strictly increasing**.

A subarray is strictly increasing if for every adjacent pair inside it:

```text
nums[i] < nums[i + 1]
```

---

## Key Observation

A strictly increasing subarray is completely determined by an **increasing run**.

For example, in:

```text
[1, 3, 5, 4, 4, 6]
```

the increasing runs are:

- `[1, 3, 5]` with length `3`
- `[4]` with length `1`
- `[4, 6]` with length `2`

Now look at a run of length `L`.

Every contiguous piece inside that run is also strictly increasing.

So the number of strictly increasing subarrays contributed by one run of length `L` is:

```text
L * (L + 1) / 2
```

because:

- there are `L` subarrays of length `1`
- `L - 1` subarrays of length `2`
- `L - 2` subarrays of length `3`
- ...
- `1` subarray of length `L`

So total:

```text
L + (L - 1) + (L - 2) + ... + 1 = L * (L + 1) / 2
```

---

## Better Observation for a One-Pass Solution

Instead of explicitly collecting run lengths first, we can compute the answer while scanning.

Let:

```text
len = length of the current strictly increasing subarray ending at index i
```

Then:

- if `nums[i] > nums[i - 1]`, we can extend the previous increasing run, so `len++`
- otherwise, the increasing run breaks, so `len = 1`

Why add `len` to the answer at every index?

Because `len` is exactly the number of strictly increasing subarrays that **end at index `i`**.

If the current increasing run ending at `i` has length `len`, then the valid increasing subarrays ending at `i` are:

- the last `1` element
- the last `2` elements
- ...
- the last `len` elements

So there are exactly `len` such subarrays.

Thus:

```text
answer += len
```

at every step.

This avoids any extra array or post-processing.

---

## Step-by-Step Example

### Example 1

```text
nums = [1, 3, 5, 4, 4, 6]
```

We track:

- `len` = current increasing run length ending here
- `ans` = total count

| Index | nums[i] | Comparison with previous | len | ans added | total ans |
| ----: | ------: | ------------------------ | --: | --------: | --------: |
|     0 |       1 | start                    |   1 |         1 |         1 |
|     1 |       3 | `3 > 1`                  |   2 |         2 |         3 |
|     2 |       5 | `5 > 3`                  |   3 |         3 |         6 |
|     3 |       4 | `4 <= 5` break           |   1 |         1 |         7 |
|     4 |       4 | `4 <= 4` break           |   1 |         1 |         8 |
|     5 |       6 | `6 > 4`                  |   2 |         2 |        10 |

Final answer:

```text
10
```

---

## Why This Works

### Local meaning of `len`

At each index `i`, `len` stores the length of the longest strictly increasing suffix ending at `i`.

That means every suffix of that suffix is also strictly increasing.

So the number of increasing subarrays ending at `i` is exactly `len`.

### Global meaning of the sum

Every strictly increasing subarray has exactly one ending index.

So if we count:

```text
(number of increasing subarrays ending at 0)
+ (number ending at 1)
+ ...
+ (number ending at n - 1)
```

we count each valid subarray exactly once.

That is precisely what the algorithm does.

---

## Optimal Algorithm

1. Initialize:
   - `ans = 0`
   - `len = 0`
2. Traverse the array from left to right.
3. For each index:
   - if this is the first element, or `nums[i] <= nums[i - 1]`, set `len = 1`
   - otherwise set `len = len + 1`
4. Add `len` to `ans`
5. Return `ans`

---

## Java Code

```java
class Solution {
    public long countSubarrays(int[] nums) {
        long ans = 0;
        long len = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i == 0 || nums[i] <= nums[i - 1]) {
                len = 1;
            } else {
                len++;
            }
            ans += len;
        }

        return ans;
    }
}
```

---

## Dry Run of the Code

Take:

```text
nums = [1, 2, 3, 2]
```

### i = 0

- first element
- `len = 1`
- `ans = 1`

### i = 1

- `2 > 1`
- extend run
- `len = 2`
- `ans = 1 + 2 = 3`

### i = 2

- `3 > 2`
- extend run
- `len = 3`
- `ans = 3 + 3 = 6`

### i = 3

- `2 <= 3`
- run breaks
- `len = 1`
- `ans = 6 + 1 = 7`

Final answer:

```text
7
```

The strictly increasing subarrays are:

```text
[1], [2], [3], [2], [1,2], [2,3], [1,2,3]
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

We scan the array once, and each step does constant work.

### Space Complexity

```text
O(1)
```

We only use a few variables: `ans` and `len`.

---

## Why `long` Is Important

The number of subarrays can be as large as:

```text
n * (n + 1) / 2
```

If `n = 100000`, then:

```text
100000 * 100001 / 2 = 5,000,050,000
```

This does **not** fit in a 32-bit `int`, so the answer must be stored in `long`.

That is why both `ans` and `len` are written as `long` in Java.

---

## Alternative View: Run-Length Formula

Another equally valid way to think about the problem:

1. Find each maximal strictly increasing run.
2. Suppose its length is `L`
3. Add:

```text
L * (L + 1) / 2
```

For:

```text
[1, 3, 5, 4, 4, 6]
```

the run lengths are:

```text
3, 1, 2
```

So total is:

```text
3 * 4 / 2 + 1 * 2 / 2 + 2 * 3 / 2
= 6 + 1 + 3
= 10
```

This is mathematically the same as the one-pass `len` accumulation method.

The one-pass version is cleaner in code.

---

## Common Mistakes

### 1. Using `>=` instead of `>`

The subarray must be **strictly** increasing.

So:

```text
nums[i] > nums[i - 1]
```

is correct.

If you use `>=`, equal adjacent values would wrongly be treated as increasing.

---

### 2. Forgetting to reset the length

When the increasing pattern breaks:

```text
nums[i] <= nums[i - 1]
```

you must reset:

```text
len = 1
```

because the current element alone always forms an increasing subarray of length `1`.

---

### 3. Using `int` for the answer

For large arrays, the total can exceed `2^31 - 1`.

Use `long`.

---

## Short Intuition Summary

At every index, ask:

> How many strictly increasing subarrays end here?

If the current increasing run ending here has length `len`, then the answer is exactly `len`.

So we keep extending the current run when possible, reset it when the order breaks, and keep adding the current run length to the total.

---

## Final Takeaway

This is a classic counting-by-ending-position problem.

The central trick is:

- `len` = number of strictly increasing subarrays ending at current index
- add `len` into the final answer

That gives an optimal:

- **Time:** `O(n)`
- **Space:** `O(1)`
