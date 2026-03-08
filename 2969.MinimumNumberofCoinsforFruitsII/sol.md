# Minimum Coins to Acquire All Fruits — Detailed Summary of Both Solutions

## Problem Restatement

You are given a **1-indexed** array `prices`, where `prices[i]` is the cost to buy the `i`-th fruit.

Offer:

- If you buy fruit `i`, you may take the **next `i` fruits for free**.
- Even if a fruit is available for free, you are still allowed to **buy it anyway** to trigger a new offer.

Goal:

Return the **minimum number of coins** needed to acquire **all fruits**.

---

# High-Level Insight

This problem is easy to misunderstand if you think only in terms of “buy or take free” locally.

The real structure is:

- buying fruit `i` creates a **coverage interval**
- that purchase guarantees you get fruits:

```text
i, i+1, i+2, ..., min(n, 2*i)
```

because fruit `i` itself is bought, and the next `i` fruits are free

So one purchase at index `i` covers up to fruit `2*i`.

The difficult part is this:

> Even if some fruit becomes free, you may still choose to buy it and extend coverage again.

That is what turns the problem into DP.

---

# Core DP Relationship Behind Both Solutions

Let us switch to **0-indexed** thinking for the implementations shown later.

If you buy fruit at index `j`, then that purchase covers:

```text
j, j+1, ..., 2*j+1
```

Why?

- in 0-indexing, fruit `j` corresponds to the `(j+1)`-th fruit in the original problem
- buying the `(j+1)`-th fruit gives the next `(j+1)` fruits free
- so total covered range becomes up to index:

```text
j + (j+1) = 2*j+1
```

So if we later want to buy fruit `i`, then the previous purchased fruit `j` must satisfy:

```text
2*j + 1 >= i
```

because the offer from `j` must be enough to let us obtain fruit `i`.

That condition drives the optimized DP.

---

# Solution 1 — DP + Segment Tree (`O(n log n)`)

## DP Meaning

Define:

```text
dp[i] = minimum coins needed if the last fruit we buy is fruit i
```

Then to buy fruit `i`, the previous purchased fruit must be some `j` such that:

```text
2*j + 1 >= i
```

So:

```text
dp[i] = prices[i] + min(dp[j]) over all valid j
```

For `i = 0`, there is no previous fruit, so:

```text
dp[0] = prices[0]
```

---

## Rearranging the Valid Range

From:

```text
2*j + 1 >= i
```

we get:

```text
j >= (i - 1) / 2
```

So the valid previous indices form a suffix-like range:

```text
j in [ceil((i - 1)/2), i - 1]
```

That means for each `i`, we need a **range minimum query** over `dp`.

This is exactly where a segment tree helps.

---

## Why Segment Tree Works

A segment tree supports:

- **point update**: set value at index `i`
- **range minimum query**: get minimum on an interval

both in:

```text
O(log n)
```

So for each fruit `i`, we can compute:

```text
dp[i] = prices[i] + min(dp[L..R])
```

efficiently.

At the end, not every `dp[i]` is a valid final answer.

We need a last purchased fruit `i` whose offer covers the end of the array:

```text
2*i + 1 >= n - 1
```

Among those, answer is the minimum `dp[i]`.

---

## Segment Tree Solution Code

```java
import java.util.*;

class Solution {
    public int minimumCoins(int[] prices) {
        int n = prices.length;
        int[] dp = new int[n];
        SegmentTree st = new SegmentTree(n);

        dp[0] = prices[0];
        st.update(0, dp[0]);

        for (int i = 1; i < n; i++) {
            int left = (i - 1) / 2;
            int right = i - 1;

            int best = st.query(left, right);
            dp[i] = prices[i] + best;
            st.update(i, dp[i]);
        }

        int ans = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (2 * i + 1 >= n - 1) {
                ans = Math.min(ans, dp[i]);
            }
        }

        return ans;
    }

    static class SegmentTree {
        int n;
        int[] tree;
        static final int INF = (int) 1e9;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
            Arrays.fill(tree, INF);
        }

        void update(int idx, int val) {
            update(1, 0, n - 1, idx, val);
        }

        private void update(int node, int l, int r, int idx, int val) {
            if (l == r) {
                tree[node] = val;
                return;
            }
            int mid = (l + r) / 2;
            if (idx <= mid) update(node * 2, l, mid, idx, val);
            else update(node * 2 + 1, mid + 1, r, idx, val);

            tree[node] = Math.min(tree[node * 2], tree[node * 2 + 1]);
        }

        int query(int ql, int qr) {
            return query(1, 0, n - 1, ql, qr);
        }

        private int query(int node, int l, int r, int ql, int qr) {
            if (qr < l || r < ql) return INF;
            if (ql <= l && r <= qr) return tree[node];

            int mid = (l + r) / 2;
            return Math.min(
                query(node * 2, l, mid, ql, qr),
                query(node * 2 + 1, mid + 1, r, ql, qr)
            );
        }
    }
}
```

---

## Segment Tree Code Walkthrough

### `dp[0] = prices[0]`

```java
dp[0] = prices[0];
```

The very first fruit must be bought. There is no way to get fruit `0` free before anything starts.

---

### Range for previous valid purchases

```java
int left = (i - 1) / 2;
int right = i - 1;
```

These are all previous fruits `j` whose free coverage can reach `i`.

Reason:

```text
2*j + 1 >= i
=> j >= (i - 1) / 2
```

---

### Best previous cost

```java
int best = st.query(left, right);
dp[i] = prices[i] + best;
```

We choose the cheapest previous purchased fruit that can validly lead into buying `i`.

---

### Update segment tree

```java
st.update(i, dp[i]);
```

Once `dp[i]` is known, it becomes available for future range minimum queries.

---

### Final answer

```java
if (2 * i + 1 >= n - 1) {
    ans = Math.min(ans, dp[i]);
}
```

Fruit `i` can be the final purchased fruit only if its free coverage reaches the last fruit.

---

## Segment Tree Correctness Intuition

For every fruit `i`, `dp[i]` captures the cheapest way to make `i` the next fruit you explicitly buy.

The last bought fruit before `i` must have an offer covering `i`, so only indices `j` with:

```text
2*j + 1 >= i
```

are legal predecessors.

Taking the minimum over all such `j` ensures optimality.

The segment tree does not change the recurrence. It only speeds up the repeated “minimum over a range” operation.

---

## Segment Tree Complexity

### Time Complexity

For each index `i`:

- one range minimum query: `O(log n)`
- one point update: `O(log n)`

Total:

```text
O(n log n)
```

### Space Complexity

- `dp`: `O(n)`
- segment tree: `O(n)`

Total:

```text
O(n)
```

---

# Solution 2 — DP + Monotonic Deque (`O(n)`)

Your code:

```java
class Solution {
    public int minimumCoins(int[] prices) {
        int n = prices.length;
        int[] dp = new int[n];
        Deque<Integer> q = new ArrayDeque<>();
        dp[0] = prices[0];
        q.add(0);

        for (int i = 1; i < n; ++i) {
            dp[i] = dp[q.peekFirst()] + prices[i];
            while (!q.isEmpty() && q.peekFirst() + q.peekFirst() + 1 < i) {
                q.pollFirst();
            }
            while (!q.isEmpty() && dp[q.peekLast()] >= dp[i]) {
                q.pollLast();
            }
            q.add(i);
        }
        return dp[q.peekFirst()];
    }
}
```

---

## What This Solution Is Doing

It uses the **same DP recurrence** as the segment tree solution:

```text
dp[i] = prices[i] + min(dp[j])
for all j such that 2*j + 1 >= i
```

The difference is in how the minimum is maintained.

Observe that as `i` increases, the valid range of previous `j` values moves only in one direction.

That means we have a **sliding window minimum** problem.

A monotonic deque can maintain the minimum in amortized `O(1)` per step.

---

## Important Window Insight

A previous purchased fruit `j` remains valid for current fruit `i` as long as:

```text
2*j + 1 >= i
```

If:

```text
2*j + 1 < i
```

then `j` can no longer help for this or any future larger index, so it should be removed from the front.

That is why your code does:

```java
while (!q.isEmpty() && q.peekFirst() + q.peekFirst() + 1 < i) {
    q.pollFirst();
}
```

---

## A Note About the Order of Operations

A common cleaner presentation is:

1. remove expired indices
2. use the front to compute `dp[i]`
3. remove dominated indices from the back
4. push `i`

Your version computes `dp[i]` first and then removes expired indices.

When teaching the idea, many people prefer the cleanup-first order because it is easier to reason about. A cleaner equivalent presentation is:

```java
while (!q.isEmpty() && 2 * q.peekFirst() + 1 < i) {
    q.pollFirst();
}
dp[i] = dp[q.peekFirst()] + prices[i];
while (!q.isEmpty() && dp[q.peekLast()] >= dp[i]) {
    q.pollLast();
}
q.add(i);
```

That version makes the invariant more obvious.

---

## Deque Invariant

The deque stores candidate indices `j` such that:

1. they are in increasing index order
2. their `dp[j]` values are in increasing order

So:

- the front always has the smallest `dp[j]`
- if front becomes invalid for the current position, remove it
- when adding a new index `i`, remove all larger or equal `dp` values from the back because they are never better than `i`

That is standard monotonic queue logic.

---

## Deque Code Walkthrough

### Initialization

```java
dp[0] = prices[0];
q.add(0);
```

Fruit `0` must be bought, so its cost is just `prices[0]`.

Deque starts with candidate `0`.

---

### Compute current DP

```java
dp[i] = dp[q.peekFirst()] + prices[i];
```

The best valid predecessor is always at the front of the deque.

So we take the cheapest reachable previous purchase and add the cost of buying fruit `i`.

---

### Remove expired candidates

```java
while (!q.isEmpty() && q.peekFirst() + q.peekFirst() + 1 < i) {
    q.pollFirst();
}
```

If index `j` cannot cover fruit `i`, it cannot cover any later fruit either.

So remove it permanently.

---

### Maintain increasing `dp` values

```java
while (!q.isEmpty() && dp[q.peekLast()] >= dp[i]) {
    q.pollLast();
}
```

If the new index `i` has a `dp[i]` smaller than or equal to the candidate at the back, then the back candidate is useless:

- it is older
- it has no smaller DP value
- so it will never be preferable later

Remove it.

---

### Add current index

```java
q.add(i);
```

Now `i` becomes a candidate predecessor for future fruits.

---

### Return answer

```java
return dp[q.peekFirst()];
```

At the end, the front holds the minimum `dp[i]` among indices whose coverage can reach the end.

That is the optimal final cost.

---

## Deque Correctness Intuition

The recurrence is unchanged:

```text
dp[i] = prices[i] + minimum dp among valid previous j
```

The only challenge is retrieving that minimum quickly.

Since the valid predecessor window only slides forward, we can keep candidates in a deque:

- remove expired candidates from the front
- remove dominated candidates from the back

This guarantees the front always contains the optimal predecessor.

Each index enters once and leaves once, which is why the solution becomes linear.

---

## Deque Complexity

### Time Complexity

Each index is:

- added to deque once
- removed from front at most once
- removed from back at most once

So total deque work is linear.

Overall:

```text
O(n)
```

### Space Complexity

- `dp`: `O(n)`
- deque: `O(n)` worst case

Total:

```text
O(n)
```

---

# Comparing the Two Solutions

## 1. DP recurrence

Both solutions use the same DP idea.

They differ only in how they compute the minimum over the valid previous range.

---

## 2. Segment Tree

Pros:

- easier to derive from “range minimum query”
- very general technique
- good when the valid range is arbitrary or not monotonic enough for a deque

Cons:

- slower than necessary here
- more code
- more implementation overhead

Complexity:

```text
O(n log n)
```

---

## 3. Monotonic Deque

Pros:

- optimal time complexity
- elegant once the sliding-window-minimum pattern is recognized
- less overhead than segment tree

Cons:

- harder to discover
- invariants are subtle
- easier to make mistakes in index/window reasoning

Complexity:

```text
O(n)
```

---

# Which One Should You Prefer?

## In an interview

If you quickly see the deque pattern and can explain it cleanly, the deque solution is stronger.

But if you are unsure, a segment tree solution is still very good because:

- the recurrence is correct
- the optimization is systematic
- it is easier to defend rigorously

A correct `O(n log n)` answer is usually better than a buggy `O(n)` one.

---

## In competitive programming

Prefer the deque solution if you recognize the pattern confidently.

---

## In learning terms

The segment tree version helps you see the DP structure clearly.
The deque version teaches the deeper optimization pattern:

> DP + moving valid range + need minimum over window
> => monotonic deque candidate

That pattern shows up often.

---

# Final Correctness Summary

Both solutions rely on the same fact:

If fruit `j` is the previously purchased fruit, then buying `j` covers fruit `i` iff:

```text
2*j + 1 >= i
```

So the transition is:

```text
dp[i] = prices[i] + min(dp[j])
```

over all valid `j`.

Then the final answer is the minimum `dp[i]` among indices `i` that can cover the end of the array.

- the segment tree computes that minimum in `O(log n)` per step
- the deque maintains that minimum in amortized `O(1)` per step

Therefore both are correct, with the deque being asymptotically faster.

---

# Final Complexity Summary

## Segment Tree Solution

- **Time:** `O(n log n)`
- **Space:** `O(n)`

## Monotonic Deque Solution

- **Time:** `O(n)`
- **Space:** `O(n)`

---

# Final Takeaway

This problem is not really about greedily deciding whether to take a fruit for free.

It is a constrained-DP problem where each purchase creates a future-reach interval.

Once you model:

- what buying fruit `i` covers
- which previous purchases can legally lead to buying `i`

the recurrence becomes clear.

From there:

- segment tree is the straightforward optimization
- monotonic deque is the sharper optimization because the valid range slides monotonically

That is why the deque solution is the best version among the two.
