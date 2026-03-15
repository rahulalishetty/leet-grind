# 1918. Kth Smallest Subarray Sum — Java Solutions and Detailed Notes

## Problem

Given an integer array `nums` of length `n` and an integer `k`, return the **kth smallest subarray sum**.

A **subarray** is a **non-empty contiguous** sequence of elements.

```java
class Solution {
    public int kthSmallestSubarraySum(int[] nums, int k) {

    }
}
```

---

## Core observation

All numbers are **positive**:

```text
1 <= nums[i]
```

That changes the problem completely.

Because every element is positive:

- extending a subarray always **increases** its sum,
- for a fixed start index, subarray sums are strictly increasing as we extend right,
- the number of subarrays with sum `<= X` can be counted with a **sliding window** in linear time.

That is the key to the optimal solution.

---

# Approach 1: Brute Force with Prefix Sums + Sort

## Idea

Generate **every subarray sum**, sort them, and return the `k`th one.

This is the most direct interpretation of the problem.

---

## How it works

If we build prefix sums:

```text
prefix[i] = sum of nums[0..i-1]
```

then the sum of subarray `[l..r]` is:

```text
prefix[r + 1] - prefix[l]
```

So we can enumerate all `O(n^2)` subarrays, collect all sums, sort them, and pick the `k`th smallest.

---

## Example

```text
nums = [2,1,3]
```

Prefix sums:

```text
prefix = [0,2,3,6]
```

Subarray sums:

- `[2] = 2`
- `[2,1] = 3`
- `[2,1,3] = 6`
- `[1] = 1`
- `[1,3] = 4`
- `[3] = 3`

Sorted:

```text
[1,2,3,3,4,6]
```

4th smallest is `3`.

---

## Java code

```java
import java.util.*;

class Solution {
    public int kthSmallestSubarraySum(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        List<Long> sums = new ArrayList<>();

        for (int left = 0; left < n; left++) {
            for (int right = left; right < n; right++) {
                sums.add(prefix[right + 1] - prefix[left]);
            }
        }

        Collections.sort(sums);
        return sums.get(k - 1).intValue();
    }
}
```

---

## Complexity

Time complexity:

```text
O(n^2 log(n^2)) = O(n^2 log n)
```

Space complexity:

```text
O(n^2)
```

---

## Verdict

This is useful for understanding the problem, but it is not suitable for the full constraints.

---

# Approach 2: Min-Heap / K-Way Merge

## Idea

For each start index `i`, the sequence of subarray sums:

```text
nums[i]
nums[i] + nums[i+1]
nums[i] + nums[i+1] + nums[i+2]
...
```

is increasing because all numbers are positive.

So we have `n` sorted lists:

- list starting at `0`
- list starting at `1`
- list starting at `2`
- ...

We need the `k`th smallest value among all these sorted lists.

That is exactly a **k-way merge** problem.

---

## Heap state

Each heap node stores:

- current subarray sum,
- start index,
- end index.

Initially push:

```text
(start=i, end=i, sum=nums[i])
```

for every `i`.

Then repeat `k` times:

1. pop the smallest sum,
2. that popped sum is the next smallest subarray sum,
3. extend that subarray one step right and push the new sum back if possible.

The `k`th popped value is the answer.

---

## Example

```text
nums = [2,1,3]
```

Initial heap:

```text
(2,[0..0]), (1,[1..1]), (3,[2..2])
```

Pop order:

1. `1` from `[1..1]`, push `[1..2] = 4`
2. `2` from `[0..0]`, push `[0..1] = 3`
3. `3` from `[2..2]`
4. `3` from `[0..1]`, push `[0..2] = 6`

The 4th popped value is `3`.

---

## Java code

```java
import java.util.*;

class Solution {
    private static class Node {
        long sum;
        int start;
        int end;

        Node(long sum, int start, int end) {
            this.sum = sum;
            this.start = start;
            this.end = end;
        }
    }

    public int kthSmallestSubarraySum(int[] nums, int k) {
        int n = nums.length;

        PriorityQueue<Node> minHeap = new PriorityQueue<>(
            Comparator.comparingLong(a -> a.sum)
        );

        for (int i = 0; i < n; i++) {
            minHeap.offer(new Node(nums[i], i, i));
        }

        long answer = 0;

        for (int count = 0; count < k; count++) {
            Node cur = minHeap.poll();
            answer = cur.sum;

            if (cur.end + 1 < n) {
                minHeap.offer(new Node(
                    cur.sum + nums[cur.end + 1],
                    cur.start,
                    cur.end + 1
                ));
            }
        }

        return (int) answer;
    }
}
```

---

## Complexity

Let `n = nums.length`.

Time complexity:

```text
O((n + k) log n)
```

- `O(n log n)` to initialize heap with `n` items,
- `k` pop/push operations, each `O(log n)`.

Space complexity:

```text
O(n)
```

---

## Verdict

This is much better than brute force and is elegant.

But `k` can be as large as:

```text
n * (n + 1) / 2
```

which can be around `2 * 10^8` when `n = 2 * 10^4`.

So this approach is still too slow in the worst case.

---

# Approach 3: Binary Search on Answer + Sliding Window Count

## Why this is the optimal approach

We do not need to explicitly generate the kth smallest sum.

Instead, we binary search on the answer:

> For a candidate value `X`, how many subarrays have sum `<= X`?

If that count is at least `k`, then the kth smallest sum is `<= X`.

If that count is less than `k`, then the kth smallest sum is `> X`.

That gives a monotonic condition, so binary search applies.

The only remaining challenge is:

> How do we count subarrays with sum `<= X` efficiently?

Because all `nums[i] > 0`, we can do this in **O(n)** with a sliding window.

---

## Step 1: Binary search range

Minimum possible subarray sum:

```text
min(nums)
```

Actually, since all numbers are positive, the smallest subarray sum is the smallest single element.
But using `1` as lower bound also works.

Maximum possible subarray sum:

```text
sum(nums)
```

So binary search range is:

```text
[1, sum(nums)]
```

---

## Step 2: Count how many subarrays have sum <= X

Maintain a sliding window `[left..right]` with running sum.

For each `right`:

- add `nums[right]`,
- while sum `> X`, move `left` rightward,
- then all subarrays ending at `right` and starting anywhere from `left` to `right` are valid.

Count contributed by this `right`:

```text
right - left + 1
```

Why?

Because these subarrays are:

```text
[left..right]
[left+1..right]
...
[right..right]
```

All of them have sum `<= X`.

Since every number is positive, once `[left..right]` is valid, every shorter suffix ending at `right` is also valid.

---

## Example of counting

```text
nums = [2,1,3], X = 3
```

Subarrays with sum `<= 3` are:

- `[2]`
- `[1]`
- `[3]`
- `[2,1]`

Count = `4`.

Sliding window:

### right = 0

sum = 2
valid subarrays ending at 0:

```text
[2]
```

count += 1

### right = 1

sum = 3
valid:

```text
[2,1], [1]
```

count += 2

### right = 2

sum = 6 > 3
shrink:

- remove 2 => sum = 4
- remove 1 => sum = 3

Now valid window is `[2..2]`.

valid:

```text
[3]
```

count += 1

Total = 4.

---

## Step 3: Binary search logic

Let `count(X)` be the number of subarrays with sum `<= X`.

- if `count(mid) >= k`, then answer is in the left half, including `mid`
- else answer is in the right half

This is classic **lower bound binary search** for the smallest `X` such that:

```text
count(X) >= k
```

That `X` is exactly the kth smallest subarray sum.

---

## Java code

```java
class Solution {
    public int kthSmallestSubarraySum(int[] nums, int k) {
        int left = 1;
        int right = 0;

        for (int num : nums) {
            right += num;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (countSubarraysWithSumAtMost(nums, mid) >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private long countSubarraysWithSumAtMost(int[] nums, int limit) {
        long count = 0;
        long windowSum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            windowSum += nums[right];

            while (windowSum > limit) {
                windowSum -= nums[left];
                left++;
            }

            count += (right - left + 1);
        }

        return count;
    }
}
```

---

## Complexity

Let:

- `n = nums.length`
- `S = sum(nums)`

Time complexity:

```text
O(n log S)
```

Why?

- Binary search over answer range `[1, S]` takes `O(log S)`
- each count check is `O(n)`

Space complexity:

```text
O(1)
```

---

# Why Approach 3 works so well

The big win is that we never enumerate subarrays explicitly.

There are:

```text
n * (n + 1) / 2
```

subarrays, which is far too many.

Instead, we ask a counting question:

```text
How many subarrays have sum <= X?
```

That count can be computed in linear time because the array is strictly positive.

That positivity constraint is the entire reason this solution works cleanly.

---

# Important note: why positivity matters

The sliding window counting method depends on:

```text
nums[i] > 0
```

If negative numbers were allowed, then after shrinking or expanding the window, the monotonic behavior would break, and:

```text
right - left + 1
```

would no longer correctly count valid subarrays.

So this solution is tightly coupled to the problem constraint:

```text
1 <= nums[i]
```

---

# Comparison of approaches

## Approach 1: Brute Force + Sort

### Pros

- simplest to understand
- direct translation of the problem

### Cons

- too slow
- too much memory

### Complexity

```text
Time:  O(n^2 log n)
Space: O(n^2)
```

---

## Approach 2: Min-Heap / K-Way Merge

### Pros

- much better than brute force
- elegant use of positivity
- good intermediate approach

### Cons

- still too slow when `k` is huge

### Complexity

```text
Time:  O((n + k) log n)
Space: O(n)
```

---

## Approach 3: Binary Search + Sliding Window

### Pros

- optimal for this problem
- handles full constraints comfortably
- elegant once the counting insight is seen

### Cons

- less obvious initially
- depends critically on all numbers being positive

### Complexity

```text
Time:  O(n log S)
Space: O(1)
```

---

# Final recommended solution

Use:

```text
Binary Search on Answer + Sliding Window Counting
```

because it is the only approach here that is truly robust for the full constraints.

---

# Final polished Java solution

```java
class Solution {
    public int kthSmallestSubarraySum(int[] nums, int k) {
        int left = 1;
        int right = 0;

        for (int num : nums) {
            right += num;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            long count = countSubarraysWithSumAtMost(nums, mid);

            if (count >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private long countSubarraysWithSumAtMost(int[] nums, int limit) {
        long count = 0;
        long sum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (sum > limit) {
                sum -= nums[left++];
            }

            count += right - left + 1;
        }

        return count;
    }
}
```

---

# Quick intuition recap

We are not directly finding the kth smallest sum.

We are finding the smallest value `X` such that:

```text
at least k subarrays have sum <= X
```

That value is exactly the kth smallest subarray sum.

---

# Edge cases to keep in mind

## 1. Single element array

```text
nums = [5], k = 1
```

Only one subarray exists, answer is `5`.

## 2. Many equal sums

```text
nums = [1,1,1]
```

Subarray sums contain duplicates:

```text
1,1,1,2,2,3
```

Binary search handles duplicates naturally because it is based on counting `<= X`.

## 3. Large k

When `k` is near the total number of subarrays, heap-based methods become weak, but binary search remains efficient.

---

# Takeaway pattern

This problem is a strong example of the pattern:

```text
Binary Search on Answer
+ Count how many candidates are <= mid
```

Whenever you see:

- kth smallest / kth largest,
- monotonic feasibility,
- a way to count values <= mid efficiently,

you should immediately test whether **binary search on answer** is possible.
