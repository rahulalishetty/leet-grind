# Maximum Number of Queries That Can Be Processed

## Problem Restatement

You are given:

- a `0`-indexed array `nums`
- a `0`-indexed array `queries`

Before processing begins, you may perform the following operation **at most once**:

- replace `nums` with **any subsequence** of `nums`

Then process `queries` from left to right.

For each query `queries[i] = q`:

- if **both** the first and the last element of the current `nums` are `< q`, processing stops
- otherwise, choose either:
  - the first element, if it is `>= q`, or
  - the last element, if it is `>= q`
- remove the chosen element

Return the **maximum number of queries** that can be processed by choosing the subsequence optimally and then making optimal removals.

---

## High-Level Insight

At first glance, the “replace with any subsequence” operation looks extremely flexible and hard to model directly.

But the key realization is this:

> Once you choose a subsequence, and then repeatedly remove only from its two ends, the remaining usable elements always correspond to some interval in the original array.

This is the central simplification.

We do **not** need to explicitly build the subsequence.

Instead, we only need to track which **original index range** still contains the elements that could remain.

---

## Why an Interval Is Enough

Suppose the original array is:

```text
nums = [a0, a1, a2, a3, a4, a5]
```

You are allowed to keep any subsequence, for example:

```text
[a1, a3, a5]
```

Now imagine you remove the left end of that subsequence. Then you are left with:

```text
[a3, a5]
```

Those surviving elements still lie inside an interval of original indices.

More generally:

- if you remove an element chosen from the **left side**, then everything that remains must come from the indices **to its right**
- if you remove an element chosen from the **right side**, then everything that remains must come from the indices **to its left**

Since the kept elements must preserve original order, the remaining possibilities always lie within some interval `[l, r]` of original indices.

This means the state of the problem can be summarized as:

> “After processing some prefix of queries, it is possible that the remaining chosen subsequence lies entirely inside the interval `[l, r]`.”

That is much easier to work with than reasoning directly about all subsequences.

---

## Core DP / State View

We maintain a set of **reachable intervals**.

Each reachable interval `[l, r]` means:

- after processing the current prefix of queries,
- there exists some valid subsequence using only indices in `[l, r]`,
- such that future processing can continue from those remaining elements.

Initially, before processing any query, the whole array is available:

```text
[0, n - 1]
```

So the initial reachable set is:

```text
{ [0, n - 1] }
```

---

## Transition for One Query

Suppose we currently have a reachable interval:

```text
[l, r]
```

and the next query value is:

```text
q
```

We want to see what intervals can result after processing this query.

To process query `q`, we must remove either:

- the first element of the chosen subsequence, or
- the last element of the chosen subsequence,

and that removed value must be at least `q`.

Because we may choose any subsequence inside `[l, r]`, we are really free to decide:

- which index in `[l, r]` will act as the left end
- which index in `[l, r]` will act as the right end

as long as its value is at least `q`.

---

## Left Removal Transition

If we want to remove from the left, we choose some index `x` in `[l, r]` such that:

```text
nums[x] >= q
```

and make that element the left end of the subsequence.

After removing it, every surviving element must come from indices strictly to its right.

So the new reachable interval becomes:

```text
[x + 1, r]
```

---

## Right Removal Transition

Similarly, if we want to remove from the right, we choose some index `x` in `[l, r]` such that:

```text
nums[x] >= q
```

and make that element the right end of the subsequence.

After removing it, all surviving elements must lie strictly to its left.

So the new reachable interval becomes:

```text
[l, x - 1]
```

---

## Crucial Greedy Observation

For a fixed interval `[l, r]` and query `q`, there may be many indices with `nums[x] >= q`.

Do we need to try all of them?

No.

That is the most important optimization.

### For left removal:

It is always best to choose the **leftmost** valid index.

Why?

Because if you choose a later valid index `x2` instead of an earlier valid index `x1`, then:

```text
x1 < x2
```

and the resulting intervals are:

```text
[x1 + 1, r]   and   [x2 + 1, r]
```

The first one is strictly larger or equal, so it leaves **more future choices**.

So any strategy possible after removing `x2` is also possible after removing `x1`.

Therefore, among all valid left-removal candidates, only the **leftmost** one matters.

### For right removal:

By symmetric reasoning, only the **rightmost** valid index matters.

So from each interval and each query, there are at most **two useful transitions**:

- remove the leftmost index in `[l, r]` whose value is `>= q`
- remove the rightmost index in `[l, r]` whose value is `>= q`

This sharply reduces the branching.

---

## State Explosion and Pruning

Even though each interval gives at most two next intervals, the number of intervals could still grow.

So we need pruning.

### Dominance Rule

Suppose we have two reachable intervals:

```text
[l1, r1]
[l2, r2]
```

If:

```text
l1 <= l2  and  r1 >= r2
```

then interval `[l1, r1]` **contains** interval `[l2, r2]`.

This means `[l1, r1]` is at least as good as `[l2, r2]` for all future processing, because it offers every index option that `[l2, r2]` offers, and maybe more.

Therefore:

> `[l2, r2]` is dominated and can be discarded.

This pruning is safe and essential.

---

## Efficient Search for Valid Indices

For each reachable interval `[l, r]` and query `q`, we need:

1. the **first** index in `[l, r]` with `nums[idx] >= q`
2. the **last** index in `[l, r]` with `nums[idx] >= q`

Doing this by linear scan every time would be too slow.

Instead, we build a **segment tree** storing range maximums.

### Why range maximum is enough

If a segment’s maximum value is `< q`, then no position in that segment can satisfy `nums[idx] >= q`.

So while searching:

- if segment max `< q`, skip the whole segment
- otherwise descend recursively

This allows us to find:

- the leftmost valid index by exploring left child first
- the rightmost valid index by exploring right child first

Each search takes:

```text
O(log n)
```

in the typical segment-tree sense.

---

## Full Algorithm

### Step 1: Build a segment tree over `nums`

Each node stores the maximum value in its range.

### Step 2: Maintain the current frontier of reachable intervals

Initially:

```text
states = { [0, n-1] }
```

### Step 3: Process queries one by one

For each query `q`:

- create an empty list `nextStates`
- for every current interval `[l, r]`:
  - find `leftIdx` = first position in `[l, r]` with `nums[leftIdx] >= q`
    - if found, add interval `[leftIdx + 1, r]`
  - find `rightIdx` = last position in `[l, r]` with `nums[rightIdx] >= q`
    - if found, add interval `[l, rightIdx - 1]`
- if `nextStates` is empty, we cannot process this query, so stop
- otherwise prune dominated intervals and continue

The number of successfully processed queries is the answer.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maximumProcessableQueries(int[] nums, int[] queries) {
        int n = nums.length;
        SegmentTree st = new SegmentTree(nums);

        List<int[]> states = new ArrayList<>();
        states.add(new int[]{0, n - 1}); // reachable intervals after current prefix

        int processed = 0;

        for (int q : queries) {
            List<int[]> nextStates = new ArrayList<>();

            for (int[] state : states) {
                int l = state[0], r = state[1];
                if (l > r) continue;

                int leftIdx = st.findFirstGE(l, r, q);
                if (leftIdx != -1) {
                    nextStates.add(new int[]{leftIdx + 1, r});
                }

                int rightIdx = st.findLastGE(l, r, q);
                if (rightIdx != -1) {
                    nextStates.add(new int[]{l, rightIdx - 1});
                }
            }

            if (nextStates.isEmpty()) break;

            states = prune(nextStates);
            processed++;
        }

        return processed;
    }

    // Remove dominated intervals.
    // If [l1, r1] has l1 <= l2 and r1 >= r2, then [l2, r2] is useless.
    private List<int[]> prune(List<int[]> intervals) {
        intervals.sort((a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(b[1], a[1]); // larger r first for same l
        });

        List<int[]> res = new ArrayList<>();
        int bestR = Integer.MIN_VALUE;

        for (int[] in : intervals) {
            int l = in[0], r = in[1];
            if (r > bestR) {
                res.add(new int[]{l, r});
                bestR = r;
            }
        }
        return res;
    }

    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int[] nums) {
            n = nums.length;
            tree = new int[4 * n];
            build(1, 0, n - 1, nums);
        }

        private void build(int node, int l, int r, int[] nums) {
            if (l == r) {
                tree[node] = nums[l];
                return;
            }
            int mid = (l + r) >>> 1;
            build(node << 1, l, mid, nums);
            build(node << 1 | 1, mid + 1, r, nums);
            tree[node] = Math.max(tree[node << 1], tree[node << 1 | 1]);
        }

        int findFirstGE(int ql, int qr, int target) {
            return findFirstGE(1, 0, n - 1, ql, qr, target);
        }

        private int findFirstGE(int node, int l, int r, int ql, int qr, int target) {
            if (r < ql || l > qr || tree[node] < target) return -1;
            if (l == r) return l;

            int mid = (l + r) >>> 1;
            int left = findFirstGE(node << 1, l, mid, ql, qr, target);
            if (left != -1) return left;
            return findFirstGE(node << 1 | 1, mid + 1, r, ql, qr, target);
        }

        int findLastGE(int ql, int qr, int target) {
            return findLastGE(1, 0, n - 1, ql, qr, target);
        }

        private int findLastGE(int node, int l, int r, int ql, int qr, int target) {
            if (r < ql || l > qr || tree[node] < target) return -1;
            if (l == r) return l;

            int mid = (l + r) >>> 1;
            int right = findLastGE(node << 1 | 1, mid + 1, r, ql, qr, target);
            if (right != -1) return right;
            return findLastGE(node << 1, l, mid, ql, qr, target);
        }
    }
}
```

---

## Deep Intuition Behind the State Representation

A common point of confusion is:

> “How can an interval represent a subsequence? A subsequence can skip many elements.”

That is true, but the interval is **not** saying all elements in `[l, r]` are kept.

It only says:

> all future kept elements must come from inside `[l, r]`

Within that interval, you are still free to keep or skip any elements because the initial “choose a subsequence” operation is arbitrary.

So the interval is really a compact description of the remaining feasible region, not the exact subsequence.

This is why the method works.

---

## Why Leftmost and Rightmost Are Sufficient

Let us justify this more carefully.

### Left choice

Suppose for query `q` and interval `[l, r]`, there are two valid indices:

```text
x1 < x2
```

with both:

```text
nums[x1] >= q
nums[x2] >= q
```

If we remove `x1`, the next interval is:

```text
[x1 + 1, r]
```

If we remove `x2`, the next interval is:

```text
[x2 + 1, r]
```

Since `x1 < x2`, we have:

```text
[x2 + 1, r] ⊆ [x1 + 1, r]
```

So the interval from choosing `x1` is never worse.

Thus choosing any left candidate other than the leftmost is redundant.

### Right choice

Similarly, if `x1 < x2` are both valid right-removal candidates:

- removing `x1` gives `[l, x1 - 1]`
- removing `x2` gives `[l, x2 - 1]`

and since `x2 > x1`, we have:

```text
[l, x1 - 1] ⊆ [l, x2 - 1]
```

So choosing the rightmost valid candidate is never worse.

That proves the “only two transitions per interval” fact.

---

## Why the Pruning Rule Is Correct

Suppose interval `A = [l1, r1]` contains interval `B = [l2, r2]`.

Any future valid move from `B` picks some valid index inside `[l2, r2]`.

But `[l2, r2]` is fully inside `[l1, r1]`, so the same valid index is also available from `A`.

Moreover, because `A` is larger, it may allow additional choices that `B` does not.

So `B` can never lead to a better future than `A`.

Discarding `B` is therefore safe.

---

## How the `prune` Function Works

After generating candidate intervals, we sort them by:

1. increasing `l`
2. decreasing `r` for equal `l`

Then we scan left to right.

We keep an interval only if its `r` is strictly greater than every previously kept `r`.

Why does this remove dominated intervals?

If an interval appears later with a smaller or equal `r`, and its `l` is not smaller than the previous ones, then some earlier interval already contains it.

So it is dominated.

This is a standard interval dominance pruning trick.

---

## Code Walkthrough

## 1. Main method

```java
public int maximumProcessableQueries(int[] nums, int[] queries)
```

This drives the whole process.

### Initialization

```java
int n = nums.length;
SegmentTree st = new SegmentTree(nums);

List<int[]> states = new ArrayList<>();
states.add(new int[]{0, n - 1});
```

- build segment tree once
- start with one reachable interval: the whole array

---

## 2. Processing each query

```java
for (int q : queries) {
    List<int[]> nextStates = new ArrayList<>();
```

For each query, we compute the next set of reachable intervals.

---

## 3. Expand from every current interval

```java
for (int[] state : states) {
    int l = state[0], r = state[1];
    if (l > r) continue;
```

Ignore empty intervals.

---

## 4. Try leftmost valid removal

```java
int leftIdx = st.findFirstGE(l, r, q);
if (leftIdx != -1) {
    nextStates.add(new int[]{leftIdx + 1, r});
}
```

If a valid left end exists, removing it leaves everything to its right.

---

## 5. Try rightmost valid removal

```java
int rightIdx = st.findLastGE(l, r, q);
if (rightIdx != -1) {
    nextStates.add(new int[]{l, rightIdx - 1});
}
```

If a valid right end exists, removing it leaves everything to its left.

---

## 6. Stop if nothing is reachable

```java
if (nextStates.isEmpty()) break;
```

This means no current state can process the next query.

So the answer is the number already processed.

---

## 7. Prune dominated states

```java
states = prune(nextStates);
processed++;
```

Keep only useful intervals and count this query as processed.

---

## Segment Tree Walkthrough

The segment tree stores:

```text
max value in each segment
```

That lets us quickly answer:

- does any value `>= target` exist in this segment?

If not, skip the segment entirely.

### `findFirstGE`

This searches for the first index in `[ql, qr]` with `nums[idx] >= target`.

Logic:

- if current node range is outside query range, return `-1`
- if current node max `< target`, return `-1`
- if it is a leaf, return that index
- otherwise search left child first
- if not found there, search right child

This guarantees the leftmost valid index.

### `findLastGE`

Same idea, but searches the right child first.

That gives the rightmost valid index.

---

## Example Dry Run

Consider:

```text
nums = [4, 1, 5, 2, 6]
queries = [3, 5, 2]
```

Initial reachable states:

```text
[0, 4]
```

---

### Query 1: `q = 3`

From interval `[0, 4]`:

- leftmost index with value `>= 3` is `0` (`nums[0] = 4`)
  - next interval: `[1, 4]`
- rightmost index with value `>= 3` is `4` (`nums[4] = 6`)
  - next interval: `[0, 3]`

States after query 1:

```text
[1, 4], [0, 3]
```

Neither dominates the other.

Processed = 1

---

### Query 2: `q = 5`

From `[1, 4]`:

- leftmost valid index is `2` (`5`)
  - gives `[3, 4]`
- rightmost valid index is `4` (`6`)
  - gives `[1, 3]`

From `[0, 3]`:

- leftmost valid index is `2` (`5`)
  - gives `[3, 3]`
- rightmost valid index is `2` (`5`)
  - gives `[0, 1]`

Candidate states:

```text
[3, 4], [1, 3], [3, 3], [0, 1]
```

Now prune:

- `[3, 3]` is dominated by `[3, 4]`

Remaining:

```text
[0, 1], [1, 3], [3, 4]
```

Processed = 2

---

### Query 3: `q = 2`

From `[0, 1]`:

- leftmost valid index is `0` (`4`) -> `[1, 1]`
- rightmost valid index is `0` (`4`) -> `[0, -1]`

From `[1, 3]`:

- leftmost valid index is `2` (`5`) -> `[3, 3]`
- rightmost valid index is `3` (`2`) -> `[1, 2]`

From `[3, 4]`:

- leftmost valid index is `3` (`2`) -> `[4, 4]`
- rightmost valid index is `4` (`6`) -> `[3, 3]`

So query 3 is processable too.

Processed = 3

Answer = `3`.

---

## Correctness Summary

The algorithm is correct because:

1. **Every feasible future can be represented by an interval**
   - remaining kept elements always lie in some original index interval

2. **From each interval, only two transitions matter**
   - leftmost valid left removal
   - rightmost valid right removal

3. **Dominated intervals can be discarded safely**
   - a larger containing interval can simulate any future of a smaller contained one

4. **Segment tree queries find exactly the needed extremal valid positions efficiently**

Thus the algorithm explores all relevant optimal possibilities without redundant branching.

---

## Time Complexity

Let:

- `n = nums.length`
- `m = queries.length`
- `S` = maximum number of non-dominated states kept after pruning at any step

### Segment tree build

```text
O(n)
```

### Per query

For each state:

- one `findFirstGE` search
- one `findLastGE` search

Each search is:

```text
O(log n)` in the usual segment-tree search model
```

So transition work per query is:

```text
O(S log n)
```

Then pruning sorts up to `2S` intervals:

```text
O(S log S)
```

Therefore each query costs:

```text
O(S log n + S log S)
```

Across all queries:

```text
O(m * (S log n + S log S))
```

or equivalently:

```text
O(mS(log n + log S))
```

### Worst case

In the worst case, `S` could grow to `O(n)`, giving:

```text
O(mn log n)
```

which is still much better than brute-force exploration of all subsequence choices.

---

## Space Complexity

### Segment tree

```text
O(n)
```

### Reachable states

At most:

```text
O(S)
```

So total auxiliary space is:

```text
O(n + S)
```

Worst case:

```text
O(n)
```

if `S = O(n)`.

---

## Why Brute Force Is Infeasible

A naive solution might try:

- every possible subsequence of `nums`
- simulate queries on each one
- choose the best result

But `nums` has exponentially many subsequences:

```text
2^n
```

That is completely infeasible.

Even if you try to simulate both left/right choices dynamically, the number of possibilities still explodes.

The interval-based DP compresses all those possibilities into a much smaller set of reachable states.

That is the main insight of the solution.

---

## Final Takeaway

The problem looks like a messy subsequence optimization problem, but the right perspective is:

- do not track the subsequence explicitly
- track only the original-index interval in which all remaining usable elements must lie

Then:

- for each query, only two extremal choices matter
- dominated intervals can be pruned
- segment tree makes transitions efficient

This turns a seemingly exponential problem into a structured dynamic frontier search with pruning.

It is a strong example of reducing “choose any subsequence” into a compact state representation by focusing only on what information actually matters for future decisions.
