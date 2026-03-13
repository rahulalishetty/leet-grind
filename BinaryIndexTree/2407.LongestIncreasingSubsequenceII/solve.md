# 2407. Longest Increasing Subsequence II

## Problem Restatement

We are given:

- an integer array `nums`
- an integer `k`

We need the length of the longest subsequence such that:

1. it is **strictly increasing**
2. the difference between adjacent chosen values is **at most `k`**

So if the subsequence is:

```text
[a1, a2, a3, ...]
```

then for every adjacent pair:

```text
a(i+1) > ai
and
a(i+1) - ai <= k
```

We must return the maximum possible length.

---

## Core DP Insight

Let:

```text
dp[x] = length of the longest valid subsequence that ends with value x
```

Now suppose we process a number `num`.

To append `num` to a valid subsequence, the previous value must be in:

```text
[num - k, num - 1]
```

because:

- it must be smaller than `num`
- and the difference must be at most `k`

So:

```text
dp[num] = 1 + max(dp[v]) for v in [num-k, num-1]
```

This is the heart of the problem.

The challenge is that we must repeatedly query a **range maximum** over values.

That naturally leads to:

- Segment Tree
- Fenwick-style discussion, though Fenwick is not ideal for max range queries
- coordinate/value-based DP

---

# Approach 1 — Brute Force DP

## Intuition

The most direct DP is:

```text
dp[i] = longest valid subsequence ending at index i
```

Then for every `i`, check all earlier `j < i`:

- `nums[j] < nums[i]`
- `nums[i] - nums[j] <= k`

If valid:

```text
dp[i] = max(dp[i], dp[j] + 1)
```

This is easy to derive, but too slow for `n = 10^5`.

---

## Java Code

```java
class Solution {
    public int lengthOfLIS(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        int ans = 1;

        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i] && nums[i] - nums[j] <= k) {
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

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

### Space Complexity

```text
O(n)
```

This is too slow for the constraints.

---

# Approach 2 — Value-Based DP + Segment Tree

## Intuition

Instead of DP by index, use DP by value.

For each number `num`, we need:

```text
best = max(dp[v]) for v in [num-k, num-1]
```

Then:

```text
dp[num] = best + 1
```

Because `nums[i]` and `k` are both at most `10^5`, we can build a segment tree over value range:

```text
[1, max(nums)]
```

The segment tree stores, for each value, the best subsequence length ending at that value.

Then each step becomes:

- query max on `[num-k, num-1]`
- update position `num` with `best + 1`

This is the standard optimal solution.

---

## Why segment tree works perfectly

We need:

- point update: set/update `dp[num]`
- range maximum query: over `[num-k, num-1]`

Segment tree handles both in:

```text
O(log M)
```

where `M = max(nums)`.

---

## Java Code

```java
class Solution {
    public int lengthOfLIS(int[] nums, int k) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        SegmentTree seg = new SegmentTree(maxVal);
        int ans = 1;

        for (int num : nums) {
            int left = Math.max(1, num - k);
            int right = num - 1;

            int best = 0;
            if (left <= right) {
                best = seg.query(left, right);
            }

            int cur = best + 1;
            seg.update(num, cur);
            ans = Math.max(ans, cur);
        }

        return ans;
    }

    static class SegmentTree {
        int[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void update(int index, int value) {
            update(1, 1, n, index, value);
        }

        private void update(int node, int left, int right, int index, int value) {
            if (left == right) {
                tree[node] = Math.max(tree[node], value);
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index, value);
            } else {
                update(node * 2 + 1, mid + 1, right, index, value);
            }

            tree[node] = Math.max(tree[node * 2], tree[node * 2 + 1]);
        }

        int query(int ql, int qr) {
            return query(1, 1, n, ql, qr);
        }

        private int query(int node, int left, int right, int ql, int qr) {
            if (ql <= left && right <= qr) {
                return tree[node];
            }
            if (right < ql || left > qr) {
                return 0;
            }

            int mid = left + (right - left) / 2;
            return Math.max(
                query(node * 2, left, mid, ql, qr),
                query(node * 2 + 1, mid + 1, right, ql, qr)
            );
        }
    }
}
```

---

## Complexity Analysis

Let:

- `n = nums.length`
- `M = max(nums)`

### Time Complexity

For each number:

- one range query
- one point update

Each takes:

```text
O(log M)
```

So overall:

```text
O(n log M)
```

### Space Complexity

Segment tree uses:

```text
O(M)
```

This is the recommended solution.

---

# Approach 3 — Coordinate Compression + Segment Tree

## Intuition

In this problem, raw values are already bounded by `10^5`, so compression is not strictly needed.

But it is still a valuable generalization.

If values were huge, we could compress them into ranks.

However, there is a subtlety: the valid transition range depends on actual values:

```text
[num-k, num-1]
```

not just relative order.

So with compression, we must still binary search on the sorted unique values to find the compressed indices that correspond to that numeric interval.

Then we query the segment tree on that compressed range.

This is slightly more complex than Approach 2, but more general.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int lengthOfLIS(int[] nums, int k) {
        int[] vals = nums.clone();
        Arrays.sort(vals);

        int[] uniq = Arrays.stream(vals).distinct().toArray();
        SegmentTree seg = new SegmentTree(uniq.length);

        int ans = 1;

        for (int num : nums) {
            int leftVal = num - k;
            int rightVal = num - 1;

            int l = lowerBound(uniq, leftVal);
            int r = upperBound(uniq, rightVal) - 1;

            int best = 0;
            if (l <= r) {
                best = seg.query(l + 1, r + 1);
            }

            int idx = lowerBound(uniq, num) + 1;
            int cur = best + 1;
            seg.update(idx, cur);
            ans = Math.max(ans, cur);
        }

        return ans;
    }

    private int lowerBound(int[] arr, int target) {
        int l = 0, r = arr.length;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (arr[mid] < target) l = mid + 1;
            else r = mid;
        }
        return l;
    }

    private int upperBound(int[] arr, int target) {
        int l = 0, r = arr.length;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (arr[mid] <= target) l = mid + 1;
            else r = mid;
        }
        return l;
    }

    static class SegmentTree {
        int[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            tree = new int[4 * n];
        }

        void update(int index, int value) {
            update(1, 1, n, index, value);
        }

        private void update(int node, int left, int right, int index, int value) {
            if (left == right) {
                tree[node] = Math.max(tree[node], value);
                return;
            }
            int mid = left + (right - left) / 2;
            if (index <= mid) update(node * 2, left, mid, index, value);
            else update(node * 2 + 1, mid + 1, right, index, value);
            tree[node] = Math.max(tree[node * 2], tree[node * 2 + 1]);
        }

        int query(int ql, int qr) {
            return query(1, 1, n, ql, qr);
        }

        private int query(int node, int left, int right, int ql, int qr) {
            if (ql <= left && right <= qr) return tree[node];
            if (right < ql || left > qr) return 0;
            int mid = left + (right - left) / 2;
            return Math.max(
                query(node * 2, left, mid, ql, qr),
                query(node * 2 + 1, mid + 1, right, ql, qr)
            );
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

- compression sort: `O(n log n)`
- each query/update: `O(log n)`

Overall:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

---

# Approach 4 — Why Patience Sorting LIS Is Not Enough

## Intuition

For the classic LIS problem, we often use the patience sorting / tails array technique.

That works because only the increasing-order property matters.

Here, however, we also need:

```text
adjacent difference <= k
```

This extra local-value constraint breaks the standard tails-array trick.

Why?

Because the usual LIS structure only tells us the smallest tail for a given length. It does not support efficiently answering:

> among all previous subsequences ending with values in `[num-k, num-1]`, what is the best length?

That is a **range maximum by value**, which is exactly why segment tree is needed.

So while this is LIS-flavored, the classic LIS optimization alone is insufficient.

---

# Worked Example

## Example 1

```text
nums = [4,2,1,4,3,4,5,8,15]
k = 3
```

We process values one by one.

### num = 4

Need best among endings in:

```text
[1,3]
```

None exist yet.

So:

```text
dp[4] = 1
```

### num = 2

Need best among endings in:

```text
[1,1]
```

None exist.

```text
dp[2] = 1
```

### num = 1

Need best among endings in:

```text
[-2,0]
```

None exist.

```text
dp[1] = 1
```

### num = 4

Need best among endings in:

```text
[1,3]
```

Currently best is from value `3` or `2` or `1`, giving length 2 after update.

Continuing similarly, the best final subsequence becomes:

```text
[1,3,4,5,8]
```

with length:

```text
5
```

---

# Why the Segment Tree DP Is Correct

## Claim 1

For a number `num`, any valid predecessor must lie in `[num-k, num-1]`.

### Reason

The subsequence must be strictly increasing, so predecessor must be smaller than `num`.

Also adjacent difference must be at most `k`, so:

```text
num - prev <= k
```

Combining both:

```text
prev in [num-k, num-1]
```

---

## Claim 2

`dp[num] = 1 + max(dp[v])` over that interval.

### Reason

Any valid subsequence ending at `num` is formed by taking a valid subsequence ending at some valid predecessor `v`, then appending `num`.

To maximize the final length, we choose the best such predecessor.

If no predecessor exists, the subsequence is just `[num]`, so length `1`.

---

## Claim 3

The segment tree always stores the best subsequence length ending at each value seen so far.

### Reason

Whenever we process a value `num`, we compute the optimal length ending at `num` and update the segment tree at position `num` with the maximum of old and new value.

Thus the tree remains a correct summary of best endings seen so far.

---

# Comparison of Approaches

## Approach 1 — Brute force DP

Pros:

- easiest to derive
- useful for understanding the recurrence

Cons:

- too slow

---

## Approach 2 — Segment tree over value range

Pros:

- optimal for given constraints
- clean
- directly matches the DP recurrence

Cons:

- needs segment tree implementation

This is the recommended approach.

---

## Approach 3 — Compression + segment tree

Pros:

- more general
- useful when values are large

Cons:

- extra complexity unnecessary here

---

## Approach 4 — Classical LIS tails

Pros:

- good to compare conceptually

Cons:

- does not solve this problem alone

---

# Final Recommended Java Solution

```java
class Solution {
    public int lengthOfLIS(int[] nums, int k) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        SegmentTree seg = new SegmentTree(maxVal);
        int ans = 1;

        for (int num : nums) {
            int left = Math.max(1, num - k);
            int right = num - 1;

            int best = 0;
            if (left <= right) {
                best = seg.query(left, right);
            }

            int cur = best + 1;
            seg.update(num, cur);
            ans = Math.max(ans, cur);
        }

        return ans;
    }

    static class SegmentTree {
        int[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void update(int index, int value) {
            update(1, 1, n, index, value);
        }

        private void update(int node, int left, int right, int index, int value) {
            if (left == right) {
                tree[node] = Math.max(tree[node], value);
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index, value);
            } else {
                update(node * 2 + 1, mid + 1, right, index, value);
            }

            tree[node] = Math.max(tree[node * 2], tree[node * 2 + 1]);
        }

        int query(int ql, int qr) {
            return query(1, 1, n, ql, qr);
        }

        private int query(int node, int left, int right, int ql, int qr) {
            if (ql <= left && right <= qr) {
                return tree[node];
            }
            if (right < ql || left > qr) {
                return 0;
            }

            int mid = left + (right - left) / 2;
            return Math.max(
                query(node * 2, left, mid, ql, qr),
                query(node * 2 + 1, mid + 1, right, ql, qr)
            );
        }
    }
}
```

---

# Complexity Summary

Let:

- `n = nums.length`
- `M = max(nums)`

## Approach 1

```text
Time:  O(n^2)
Space: O(n)
```

## Approach 2

```text
Time:  O(n log M)
Space: O(M)
```

## Approach 3

```text
Time:  O(n log n)
Space: O(n)
```

---

# Final Takeaway

This problem is LIS-like, but the extra condition:

```text
adjacent difference <= k
```

changes it into a **range maximum DP by value** problem.

The key recurrence is:

```text
dp[num] = 1 + max(dp[v]) for v in [num-k, num-1]
```

So the cleanest efficient solution is a **segment tree** that supports:

- range maximum query
- point update
