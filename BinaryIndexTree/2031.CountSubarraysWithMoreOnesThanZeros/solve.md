# 2031. Count Subarrays With More Ones Than Zeros

## Problem Restatement

We are given a binary array `nums` containing only `0` and `1`.

We need to count the number of subarrays where:

```text
number of 1s > number of 0s
```

Since the answer can be large, return it modulo:

```text
10^9 + 7
```

---

## Key Transformation

This is the most important observation:

Convert the array as follows:

- treat `1` as `+1`
- treat `0` as `-1`

Then for any subarray:

```text
sum > 0
```

if and only if that subarray has more `1`s than `0`s.

So the original problem becomes:

> Count subarrays with **positive sum** in an array of `+1` and `-1`.

---

## Prefix Sum Reformulation

Let `prefix[i]` be the prefix sum up to index `i`.

For a subarray `nums[l..r]`, its sum is:

```text
prefix[r] - prefix[l - 1]
```

We want this to be positive:

```text
prefix[r] > prefix[l - 1]
```

So for each ending position `r`, we need to count how many earlier prefix sums are **strictly smaller** than the current prefix sum.

That turns the problem into:

> For each prefix sum, count how many previous prefix sums are smaller than it.

This is an order-statistics / prefix-frequency problem.

---

# Approach 1 — Brute Force Over All Subarrays

## Intuition

The simplest approach is to check every subarray.

For each subarray:

- count ones
- count zeros
- if ones > zeros, increment answer

This is correct but far too slow for `n = 10^5`.

Still, it is useful as the baseline idea.

---

## Java Code

```java
class Solution {
    public int subarraysWithMoreOnesThanZeroes(int[] nums) {
        final int MOD = 1_000_000_007;
        int n = nums.length;
        long ans = 0;

        for (int i = 0; i < n; i++) {
            int ones = 0;
            int zeros = 0;

            for (int j = i; j < n; j++) {
                if (nums[j] == 1) {
                    ones++;
                } else {
                    zeros++;
                }

                if (ones > zeros) {
                    ans++;
                }
            }
        }

        return (int)(ans % MOD);
    }
}
```

---

## Complexity Analysis

### Time Complexity

There are `O(n^2)` subarrays.

So:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Prefix Sums + Balanced Tree / Sorted List Idea

## Intuition

Using the prefix sum reformulation, for each current prefix sum we need the number of previous prefix sums that are smaller.

A conceptual way is:

1. compute prefix sums
2. maintain all previous prefix sums in sorted order
3. for each current prefix, count how many previous values are smaller than it
4. insert current prefix

This works in principle with an order-statistics tree.

In Java’s standard library, there is no direct order-statistics tree, so this is more of a conceptual approach than a practical one.

It helps explain why Fenwick Tree and Segment Tree are natural next steps.

---

## Complexity

With a true order-statistics tree:

```text
O(n log n)
```

But not directly implementable with standard Java collections.

---

# Approach 3 — Fenwick Tree with Coordinate Compression

## Intuition

Prefix sums can be negative, zero, or positive.

Because Fenwick Tree works with indices, we first compress all possible prefix sums into ranks.

Then while iterating through prefix sums:

- query how many previous prefix sums are strictly smaller than the current one
- add that to the answer
- update the current prefix sum into the Fenwick Tree

This is the cleanest practical solution.

---

## Why Coordinate Compression?

After mapping `0 -> -1` and `1 -> +1`, prefix sums lie in the range:

```text
[-n, n]
```

We could use an offset array directly, but coordinate compression is more general and clean.

Compression maps sorted distinct prefix sums to:

```text
1, 2, 3, ...
```

Then a Fenwick Tree can store frequencies.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int subarraysWithMoreOnesThanZeroes(int[] nums) {
        final int MOD = 1_000_000_007;
        int n = nums.length;

        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + (nums[i] == 1 ? 1 : -1);
        }

        int[] sorted = prefix.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int x : sorted) {
            if (!rank.containsKey(x)) {
                rank.put(x, r++);
            }
        }

        Fenwick bit = new Fenwick(r + 2);
        long ans = 0;

        // Insert prefix[0]
        bit.add(rank.get(prefix[0]), 1);

        for (int i = 1; i <= n; i++) {
            int currentRank = rank.get(prefix[i]);

            // count previous prefix sums strictly smaller than prefix[i]
            ans += bit.query(currentRank - 1);
            ans %= MOD;

            bit.add(currentRank, 1);
        }

        return (int) ans;
    }

    static class Fenwick {
        int[] tree;

        Fenwick(int n) {
            tree = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }
            return sum;
        }
    }
}
```

---

## Complexity Analysis

Let `n = nums.length`.

### Time Complexity

- computing prefix sums: `O(n)`
- sorting for compression: `O(n log n)`
- Fenwick operations for all prefix sums: `O(n log n)`

Overall:

```text
O(n log n)
```

### Space Complexity

- prefix array
- compressed ranks
- Fenwick Tree

So:

```text
O(n)
```

---

# Approach 4 — Fenwick Tree with Fixed Offset

## Intuition

Because every element contributes either `+1` or `-1`, the prefix sum range is tightly bounded:

```text
[-n, n]
```

So instead of coordinate compression, we can shift the range by an offset:

```text
index = prefix + offset
```

where:

```text
offset = n + 1
```

Then we can directly use a Fenwick Tree.

This is slightly simpler than compression for this specific problem.

---

## Java Code

```java
class Solution {
    public int subarraysWithMoreOnesThanZeroes(int[] nums) {
        final int MOD = 1_000_000_007;
        int n = nums.length;

        int size = 2 * n + 5;
        int offset = n + 2;
        Fenwick bit = new Fenwick(size);

        long ans = 0;
        int prefix = 0;

        // prefix sum 0 before processing any element
        bit.add(prefix + offset, 1);

        for (int x : nums) {
            prefix += (x == 1 ? 1 : -1);

            ans += bit.query(prefix + offset - 1);
            ans %= MOD;

            bit.add(prefix + offset, 1);
        }

        return (int) ans;
    }

    static class Fenwick {
        int[] tree;

        Fenwick(int n) {
            tree = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }
            return sum;
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

Fenwick operations for each element:

```text
O(log n)
```

So total:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

This is often the most elegant implementation for this exact problem.

---

# Approach 5 — Segment Tree

## Intuition

A Segment Tree can also maintain frequencies of prefix sums.

For each prefix sum:

- query how many previous prefix sums are strictly smaller
- update the current prefix sum frequency

This is similar to the Fenwick solution, but more verbose.

Segment Tree is useful when you want a more general range-query structure.

---

## Java Code

```java
class Solution {
    public int subarraysWithMoreOnesThanZeroes(int[] nums) {
        final int MOD = 1_000_000_007;
        int n = nums.length;

        int offset = n + 1;
        int maxIndex = 2 * n + 2;

        SegmentTree seg = new SegmentTree(maxIndex);
        long ans = 0;

        int prefix = 0;
        seg.update(prefix + offset, 1);

        for (int x : nums) {
            prefix += (x == 1 ? 1 : -1);

            ans += seg.query(0, prefix + offset - 1);
            ans %= MOD;

            seg.update(prefix + offset, 1);
        }

        return (int) ans;
    }

    static class SegmentTree {
        int[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void update(int idx, int delta) {
            update(1, 0, n - 1, idx, delta);
        }

        private void update(int node, int left, int right, int idx, int delta) {
            if (left == right) {
                tree[node] += delta;
                return;
            }

            int mid = left + (right - left) / 2;
            if (idx <= mid) {
                update(node * 2, left, mid, idx, delta);
            } else {
                update(node * 2 + 1, mid + 1, right, idx, delta);
            }

            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int ql, int qr) {
            if (ql > qr) return 0;
            return query(1, 0, n - 1, ql, qr);
        }

        private int query(int node, int left, int right, int ql, int qr) {
            if (ql <= left && right <= qr) return tree[node];
            if (right < ql || left > qr) return 0;

            int mid = left + (right - left) / 2;
            return query(node * 2, left, mid, ql, qr)
                 + query(node * 2 + 1, mid + 1, right, ql, qr);
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

Each update and query takes:

```text
O(log n)
```

Total:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

---

# Why the Prefix-Sum Counting Works

## Transformation

Map:

- `1 -> +1`
- `0 -> -1`

Then a subarray has more ones than zeros iff its transformed sum is positive.

---

## Prefix Form

If `prefix[i]` is the sum of the first `i` transformed elements, then subarray `(l..r)` has sum:

```text
prefix[r + 1] - prefix[l]
```

We want:

```text
prefix[r + 1] - prefix[l] > 0
```

So:

```text
prefix[r + 1] > prefix[l]
```

Thus for each current prefix sum, the number of valid subarrays ending here equals the number of previous prefix sums that are strictly smaller.

That is exactly what the Fenwick/Segment Tree counts.

---

# Worked Example

## Example 1

```text
nums = [0,1,1,0,1]
```

Transform `0 -> -1`, `1 -> +1`:

```text
[-1, +1, +1, -1, +1]
```

Prefix sums:

```text
0, -1, 0, 1, 0, 1
```

Now for each prefix sum, count how many earlier prefix sums are smaller:

- current `-1`: smaller earlier = 0
- current `0`: smaller earlier = 1 (`-1`)
- current `1`: smaller earlier = 3 (`0, -1, 0`)
- current `0`: smaller earlier = 1 (`-1`)
- current `1`: smaller earlier = 4 (`0, -1, 0, 0`)

Total:

```text
0 + 1 + 3 + 1 + 4 = 9
```

Answer:

```text
9
```

---

# Edge Cases

## 1. All zeros

Example:

```text
[0,0,0]
```

Every transformed value is `-1`, so no subarray can have positive sum.

Answer:

```text
0
```

---

## 2. All ones

Example:

```text
[1,1,1]
```

Every subarray has more ones than zeros.

Number of subarrays:

```text
n * (n + 1) / 2
```

For `n = 3`, answer is:

```text
6
```

---

## 3. Single element

- `[0]` -> answer `0`
- `[1]` -> answer `1`

---

# Comparison of Approaches

## Approach 1 — Brute force

Pros:

- simplest to understand
- direct

Cons:

- too slow for large `n`

---

## Approach 2 — Fenwick + compression

Pros:

- general
- standard order-statistics approach
- robust

Cons:

- coordinate compression adds setup

---

## Approach 3 — Fenwick + fixed offset

Pros:

- cleanest for this exact problem
- no sorting needed
- elegant

Cons:

- relies on knowing prefix sums are bounded in `[-n, n]`

This is the recommended approach.

---

## Approach 4 — Segment Tree

Pros:

- equally powerful
- useful for related range-query variants

Cons:

- more verbose than Fenwick Tree

---

# Final Recommended Java Solution

```java
class Solution {
    public int subarraysWithMoreOnesThanZeroes(int[] nums) {
        final int MOD = 1_000_000_007;
        int n = nums.length;

        int size = 2 * n + 5;
        int offset = n + 2;
        int[] bit = new int[size];

        long ans = 0;
        int prefix = 0;

        update(bit, prefix + offset, 1);

        for (int x : nums) {
            prefix += (x == 1 ? 1 : -1);

            ans += query(bit, prefix + offset - 1);
            ans %= MOD;

            update(bit, prefix + offset, 1);
        }

        return (int) ans;
    }

    private void update(int[] bit, int index, int delta) {
        while (index < bit.length) {
            bit[index] += delta;
            index += index & -index;
        }
    }

    private int query(int[] bit, int index) {
        int sum = 0;
        while (index > 0) {
            sum += bit[index];
            index -= index & -index;
        }
        return sum;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n^2)
Space: O(1)
```

## Approach 2

```text
Time:  O(n log n)
Space: O(n)
```

## Approach 3

```text
Time:  O(n log n)
Space: O(n)
```

## Approach 4

```text
Time:  O(n log n)
Space: O(n)
```

---

# Final Takeaway

The decisive step is to transform:

- `1 -> +1`
- `0 -> -1`

Then the problem becomes:

> Count subarrays with positive sum.

Using prefix sums, that becomes:

> For each prefix sum, count how many earlier prefix sums are smaller.

That is exactly what Fenwick Tree or Segment Tree is designed to do.
