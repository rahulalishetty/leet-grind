# Create Sorted Array Through Instructions

## Problem Restatement

We are given an integer array `instructions`.

We start with an empty sorted container:

```text
nums = []
```

We process `instructions` from left to right.

For each value `x = instructions[i]`, we insert it into `nums`.

The insertion cost is:

```text
min(
    number of elements in nums strictly less than x,
    number of elements in nums strictly greater than x
)
```

We need the total cost of all insertions, modulo:

```text
10^9 + 7
```

---

## Core Difficulty

The container stays sorted, but it changes after every insertion.

For each new value `x`, we need two counts among the already-inserted elements:

- how many are `< x`
- how many are `> x`

So this is fundamentally an **online order statistics** problem.

The naive way is to insert into a sorted list and use binary search, but insertion itself is expensive.

Because:

```text
instructions.length <= 10^5
instructions[i] <= 10^5
```

we want a data structure that supports:

- prefix counts
- updates

efficiently.

That naturally leads to:

- Fenwick Tree (Binary Indexed Tree)
- Segment Tree

---

# Approach 1 — Naive Sorted List Simulation

## Intuition

Maintain the current sorted array explicitly.

For each `x`:

1. find the first position where `x` can be inserted
2. find the first position after all copies of `x`
3. then:
   - elements strictly less than `x` = left insertion index
   - elements strictly greater than `x` = current size - right insertion index
4. insertion cost is the minimum of the two
5. physically insert `x` into the list

This works conceptually, but insertion into an `ArrayList` costs `O(n)` due to shifting.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int createSortedArray(int[] instructions) {
        final int MOD = 1_000_000_007;
        List<Integer> nums = new ArrayList<>();
        long cost = 0;

        for (int x : instructions) {
            int left = lowerBound(nums, x);
            int right = upperBound(nums, x);

            int less = left;
            int greater = nums.size() - right;

            cost = (cost + Math.min(less, greater)) % MOD;
            nums.add(left, x);
        }

        return (int) cost;
    }

    private int lowerBound(List<Integer> nums, int target) {
        int lo = 0, hi = nums.size();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums.get(mid) < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    private int upperBound(List<Integer> nums, int target) {
        int lo = 0, hi = nums.size();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums.get(mid) <= target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}
```

---

## Complexity Analysis

Let `n = instructions.length`.

### Time Complexity

For each insertion:

- binary search: `O(log n)`
- insert into ArrayList: `O(n)`

So overall:

```text
O(n^2)
```

This is too slow for `n = 10^5`.

### Space Complexity

```text
O(n)
```

---

# Approach 2 — Fenwick Tree (Binary Indexed Tree)

## Intuition

Since values are bounded:

```text
instructions[i] <= 10^5
```

we can use a frequency array over values.

A Fenwick Tree supports:

- updating the count of a value
- querying how many inserted values are `<= x`

in:

```text
O(log M)
```

where `M = max(instructions)`.

That is exactly what we need.

---

## How to Compute the Two Counts

Suppose we are currently inserting `x`, and `i` elements have already been inserted.

If the Fenwick Tree stores counts of inserted values, then:

### 1. Count of elements strictly less than `x`

```text
less = query(x - 1)
```

### 2. Count of elements strictly greater than `x`

Elements `<= x` are:

```text
query(x)
```

So elements strictly greater than `x` are:

```text
greater = i - query(x)
```

Then:

```text
cost += min(less, greater)
```

and we update the tree with the new `x`.

---

## Java Code

```java
class Solution {
    public int createSortedArray(int[] instructions) {
        final int MOD = 1_000_000_007;

        int maxVal = 0;
        for (int x : instructions) {
            maxVal = Math.max(maxVal, x);
        }

        Fenwick bit = new Fenwick(maxVal + 2);
        long ans = 0;

        for (int i = 0; i < instructions.length; i++) {
            int x = instructions[i];

            int less = bit.query(x - 1);
            int greater = i - bit.query(x);

            ans = (ans + Math.min(less, greater)) % MOD;
            bit.add(x, 1);
        }

        return (int) ans;
    }

    static class Fenwick {
        private final int[] tree;

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

Let:

- `n = instructions.length`
- `M = max(instructions)`

### Time Complexity

Each element performs:

- two prefix sum queries
- one update

Each in:

```text
O(log M)
```

So total:

```text
O(n log M)
```

### Space Complexity

Fenwick Tree size:

```text
O(M)
```

This is the best practical solution for this problem.

---

# Approach 3 — Segment Tree

## Intuition

A Segment Tree can solve the same frequency-count problem.

Each node stores how many inserted values fall in its interval.

Then for each `x`, we query:

- how many are in `[1, x - 1]`
- how many are in `[x + 1, M]`

and then update the position `x`.

This is asymptotically equivalent to the Fenwick Tree approach, though the implementation is more verbose.

---

## Java Code

```java
class Solution {
    public int createSortedArray(int[] instructions) {
        final int MOD = 1_000_000_007;

        int maxVal = 0;
        for (int x : instructions) {
            maxVal = Math.max(maxVal, x);
        }

        SegmentTree seg = new SegmentTree(maxVal);
        long ans = 0;

        for (int x : instructions) {
            int less = seg.query(1, x - 1);
            int greater = seg.query(x + 1, maxVal);

            ans = (ans + Math.min(less, greater)) % MOD;
            seg.update(x, 1);
        }

        return (int) ans;
    }

    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void update(int index, int delta) {
            update(1, 1, n, index, delta);
        }

        private void update(int node, int left, int right, int index, int delta) {
            if (left == right) {
                tree[node] += delta;
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index, delta);
            } else {
                update(node * 2 + 1, mid + 1, right, index, delta);
            }

            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int ql, int qr) {
            if (ql > qr) return 0;
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
            return query(node * 2, left, mid, ql, qr)
                 + query(node * 2 + 1, mid + 1, right, ql, qr);
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

Each insertion performs:

- two range queries
- one point update

Each in:

```text
O(log M)
```

So total:

```text
O(n log M)
```

### Space Complexity

Segment Tree array:

```text
O(M)
```

up to a constant factor of `4`.

---

# Approach 4 — Coordinate Compression + Fenwick Tree

## Intuition

The constraints already give:

```text
instructions[i] <= 10^5
```

so compression is not necessary here.

But in a more general variant, values could be very large. Then we would compress distinct values to ranks.

This is still worth understanding because it generalizes the Fenwick Tree solution.

---

## Steps

1. Copy and sort all distinct values from `instructions`
2. Map each value to its compressed rank
3. Use Fenwick Tree over ranks instead of raw values
4. Query counts the same way:
   - less than current rank
   - greater than current rank

---

## Java Code

```java
import java.util.*;

class Solution {
    public int createSortedArray(int[] instructions) {
        final int MOD = 1_000_000_007;

        int[] sorted = instructions.clone();
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

        for (int i = 0; i < instructions.length; i++) {
            int compressed = rank.get(instructions[i]);

            int less = bit.query(compressed - 1);
            int greater = i - bit.query(compressed);

            ans = (ans + Math.min(less, greater)) % MOD;
            bit.add(compressed, 1);
        }

        return (int) ans;
    }

    static class Fenwick {
        private final int[] tree;

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

Let:

- `n = instructions.length`
- `u = number of distinct values`

### Time Complexity

- sorting for compression: `O(n log n)`
- per insertion Fenwick operations: `O(log u)`

Overall:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

for sorted copy, rank map, and tree.

---

# Correctness Reasoning

## Claim 1

For each instruction `x`, the number of previously inserted elements strictly less than `x` is exactly the prefix count up to `x - 1`.

### Why?

The frequency structure stores how many times each value has already appeared.

So summing counts of values in:

```text
[1, x - 1]
```

gives exactly the number of inserted elements strictly less than `x`.

---

## Claim 2

For each instruction `x`, the number of previously inserted elements strictly greater than `x` is:

```text
insertedSoFar - count(<= x)
```

### Why?

Among the already inserted elements:

- some are `< x`
- some are `= x`
- some are `> x`

The frequency prefix query up to `x` gives all elements:

```text
<= x
```

Subtracting from the total inserted count leaves exactly those:

```text
> x
```

---

## Claim 3

The algorithm computes the correct insertion cost for every element.

### Why?

The problem defines cost as:

```text
min(
    # strictly less,
    # strictly greater
)
```

Claims 1 and 2 show the algorithm computes exactly these two counts.

Therefore it computes the exact cost for each insertion.

---

## Claim 4

Summing these costs over all insertions yields the correct total answer.

### Why?

Each insertion cost is independent once based on the current state of inserted elements.

By processing instructions left to right and updating the frequency structure after every element, the data structure always reflects the correct current `nums`.

So the accumulated sum is the required total cost.

---

# Worked Example

## Example 1

```text
instructions = [1, 5, 6, 2]
```

Process left to right:

### Insert 1

Current nums:

```text
[]
```

Less than `1`: `0`
Greater than `1`: `0`
Cost:

```text
0
```

### Insert 5

Current nums:

```text
[1]
```

Less than `5`: `1`
Greater than `5`: `0`
Cost:

```text
0
```

### Insert 6

Current nums:

```text
[1, 5]
```

Less than `6`: `2`
Greater than `6`: `0`
Cost:

```text
0
```

### Insert 2

Current nums:

```text
[1, 5, 6]
```

Less than `2`: `1`
Greater than `2`: `2`
Cost:

```text
1
```

Total:

```text
1
```

---

# Edge Cases

## 1. All values equal

Example:

```text
[3, 3, 3, 3]
```

For every insertion:

- strictly less = `0`
- strictly greater = `0`

So every cost is `0`.

---

## 2. Strictly increasing sequence

Example:

```text
[1, 2, 3, 4]
```

Every new element has:

- all prior elements less than it
- zero greater than it

So every cost is `0`.

---

## 3. Strictly decreasing sequence

Example:

```text
[4, 3, 2, 1]
```

Every new element has:

- zero less than it
- all prior elements greater than it

So every cost is `0`.

This is worth noticing: both monotonic cases give zero total cost.

---

## 4. Duplicates mixed with smaller and larger values

This is exactly where the problem becomes interesting, because we must carefully distinguish:

- strictly less
- strictly greater
- equal

Equal values contribute to neither side.

---

# Comparison of Approaches

## Approach 1 — Sorted list simulation

Pros:

- intuitive
- easy to derive

Cons:

- too slow for large `n`

---

## Approach 2 — Fenwick Tree

Pros:

- efficient
- compact
- ideal for bounded values
- best practical solution

Cons:

- requires familiarity with BIT

This is the recommended approach.

---

## Approach 3 — Segment Tree

Pros:

- equally efficient asymptotically
- flexible for range query problems

Cons:

- more verbose than Fenwick Tree

---

## Approach 4 — Coordinate compression + Fenwick

Pros:

- generalizes to large value ranges
- reusable pattern

Cons:

- extra setup not needed for this exact constraint

---

# Final Recommended Java Solution

```java
class Solution {
    public int createSortedArray(int[] instructions) {
        final int MOD = 1_000_000_007;

        int maxVal = 0;
        for (int x : instructions) {
            maxVal = Math.max(maxVal, x);
        }

        int[] tree = new int[maxVal + 2];
        long ans = 0;

        for (int i = 0; i < instructions.length; i++) {
            int x = instructions[i];

            int less = query(tree, x - 1);
            int greater = i - query(tree, x);

            ans = (ans + Math.min(less, greater)) % MOD;
            update(tree, x, 1);
        }

        return (int) ans;
    }

    private void update(int[] tree, int index, int delta) {
        while (index < tree.length) {
            tree[index] += delta;
            index += index & -index;
        }
    }

    private int query(int[] tree, int index) {
        int sum = 0;
        while (index > 0) {
            sum += tree[index];
            index -= index & -index;
        }
        return sum;
    }
}
```

---

# Complexity Summary

Let:

- `n = instructions.length`
- `M = max(instructions)`

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
Time:  O(n log M)
Space: O(M)
```

## Approach 4

```text
Time:  O(n log n)
Space: O(n)
```

---

# Final Takeaway

This problem is really about counting, for each insertion:

- how many previous values are smaller
- how many previous values are larger

That makes it a classic fit for a **Fenwick Tree** or **Segment Tree**.

For this exact problem, the cleanest optimal solution is:

1. maintain frequencies of inserted values
2. use prefix sums to get:
   - strictly less count
   - strictly greater count
3. accumulate the minimum
4. update the data structure
