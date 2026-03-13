# 2921. Maximum Profitable Triplets With Increasing Prices II — Exhaustive Java Notes

## Problem Statement

You are given two 0-indexed arrays:

- `prices`
- `profits`

Both have length `n`.

Item `i` has:

- price = `prices[i]`
- profit = `profits[i]`

We must pick **three items** with indices `i < j < k` such that:

```text
prices[i] < prices[j] < prices[k]
```

If we pick such a triplet, the total profit is:

```text
profits[i] + profits[j] + profits[k]
```

Return the **maximum profit** obtainable, or `-1` if no valid triplet exists.

---

## Example 1

```text
Input:  prices = [10,2,3,4], profits = [100,2,7,10]
Output: 19
```

Explanation:

The only valid triplet is `(1, 2, 3)`:

- prices: `2 < 3 < 4`
- profits: `2 + 7 + 10 = 19`

So the answer is `19`.

---

## Example 2

```text
Input:  prices = [1,2,3,4,5], profits = [1,5,3,4,6]
Output: 15
```

Explanation:

Since prices are strictly increasing, any triplet of indices is valid.

One optimal choice is `(1, 3, 4)`:

- profits = `5 + 4 + 6 = 15`

So the answer is `15`.

---

## Example 3

```text
Input:  prices = [4,3,2,1], profits = [33,20,19,87]
Output: -1
```

Explanation:

Prices are strictly decreasing, so no valid triplet exists.

---

## Constraints

- `3 <= prices.length == profits.length <= 50000`
- `1 <= prices[i] <= 5000`
- `1 <= profits[i] <= 10^6`

---

# 1. Core Insight

This problem is structurally identical to version I, but the constraints are much larger:

- version I allowed `n <= 2000`
- here we have `n <= 50000`

That changes everything.

The clean `O(n^2)` solution from version I is no longer acceptable.

The shape of a valid triplet is still:

```text
i < j < k
prices[i] < prices[j] < prices[k]
```

So if we fix the middle index `j`, then we want:

- the best `profits[i]` on the left with `prices[i] < prices[j]`
- the best `profits[k]` on the right with `prices[k] > prices[j]`

Then the best triplet using `j` is:

```text
bestLeft[j] + profits[j] + bestRight[j]
```

So the entire problem reduces to computing those two arrays efficiently.

---

# 2. Approach 1 — Brute Force Over All Triplets

## Idea

Try every triple `(i, j, k)` with `i < j < k`.

Check:

```text
prices[i] < prices[j] < prices[k]
```

and maximize the profit sum.

## Java Code

```java
class SolutionBruteForce {
    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;
        int ans = -1;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (prices[i] >= prices[j]) continue;

                for (int k = j + 1; k < n; k++) {
                    if (prices[j] < prices[k]) {
                        ans = Math.max(ans, profits[i] + profits[j] + profits[k]);
                    }
                }
            }
        }

        return ans;
    }
}
```

## Complexity

- Time: `O(n^3)`
- Space: `O(1)`

## Verdict

Completely unusable for `n = 50000`.

---

# 3. Approach 2 — Fix Middle Index and Scan Left/Right

## Idea

For each `j`:

- scan left to find max profit with smaller price
- scan right to find max profit with larger price

Then combine.

## Java Code

```java
class SolutionQuadratic {
    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;
        int ans = -1;

        for (int j = 0; j < n; j++) {
            int leftBest = -1;
            int rightBest = -1;

            for (int i = 0; i < j; i++) {
                if (prices[i] < prices[j]) {
                    leftBest = Math.max(leftBest, profits[i]);
                }
            }

            for (int k = j + 1; k < n; k++) {
                if (prices[j] < prices[k]) {
                    rightBest = Math.max(rightBest, profits[k]);
                }
            }

            if (leftBest != -1 && rightBest != -1) {
                ans = Math.max(ans, leftBest + profits[j] + rightBest);
            }
        }

        return ans;
    }
}
```

## Complexity

- Time: `O(n^2)`
- Space: `O(1)`

## Verdict

This was acceptable for version I with `n <= 2000`, but here it will time out badly.

Still useful as a bridge to the optimal idea.

---

# 4. Why This Version Suggests a Data Structure

For each position `j`, we need:

```text
max profit among previous indices with price < prices[j]
max profit among later indices with price > prices[j]
```

Notice what matters:

- comparison by **price**
- ordering by **index**

This suggests a sweep combined with a data structure keyed by price.

A skeptical way to phrase it is:

> We do not care about all left elements individually; we only care about the maximum profit among those with price below a threshold.

That is exactly what Fenwick trees / segment trees are good at when prices are bounded or compressed.

And here prices are especially friendly:

```text
1 <= prices[i] <= 5000
```

So the price domain is tiny compared with `n`.

That makes an indexed max structure very natural.

---

# 5. Approach 3 — Fenwick Tree / BIT for Prefix Maximum (Optimal)

This is the best standard solution for this problem.

## High-level plan

Compute:

- `left[j]` = max `profits[i]` for `i < j` and `prices[i] < prices[j]`
- `right[j]` = max `profits[k]` for `k > j` and `prices[k] > prices[j]`

Then answer is:

```text
max(left[j] + profits[j] + right[j])
```

if both sides exist.

---

## 5.1 Computing `left[j]`

Scan from left to right.

At step `j`, all indices `< j` have already been processed.

We need:

```text
maximum profit among prices < prices[j]
```

That is a prefix maximum query over price ranks.

After answering for `j`, insert/update the current item:

```text
price = prices[j], value = profits[j]
```

---

## 5.2 Computing `right[j]`

Scan from right to left.

At step `j`, all indices `> j` have already been processed.

We need:

```text
maximum profit among prices > prices[j]
```

Fenwick naturally supports prefix maximum, not suffix maximum.

So we reverse the price coordinate:

```text
revPrice = MAX_PRICE - prices[j] + 1
```

Then original condition:

```text
price > prices[j]
```

becomes a prefix in reversed coordinates.

That lets us use the same Fenwick idea again.

---

## 5.3 Fenwick Tree for Maximum

Normally a BIT is used for sums.
But it can also be used for prefix maximums if updates are monotonic:

- `update(pos, value)` means `bit[x] = max(bit[x], value)` along the path
- `query(pos)` returns max over prefix `[1..pos]`

This works perfectly here.

---

## 5.4 Java Code — Fenwick Tree Solution

```java
import java.util.*;

class Solution {
    static class FenwickMax {
        int n;
        int[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new int[n + 1];
            Arrays.fill(this.bit, -1);
        }

        void update(int index, int value) {
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        int query(int index) {
            int ans = -1;
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;
        int maxPrice = 5000;

        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, -1);
        Arrays.fill(right, -1);

        // left[j] = best profit on left with smaller price
        FenwickMax leftBit = new FenwickMax(maxPrice);
        for (int j = 0; j < n; j++) {
            int p = prices[j];
            left[j] = leftBit.query(p - 1);
            leftBit.update(p, profits[j]);
        }

        // right[j] = best profit on right with larger price
        // reverse price so larger original price becomes smaller reversed index
        FenwickMax rightBit = new FenwickMax(maxPrice);
        for (int j = n - 1; j >= 0; j--) {
            int rev = maxPrice - prices[j] + 1;
            right[j] = rightBit.query(rev - 1);
            rightBit.update(rev, profits[j]);
        }

        int ans = -1;
        for (int j = 0; j < n; j++) {
            if (left[j] != -1 && right[j] != -1) {
                ans = Math.max(ans, left[j] + profits[j] + right[j]);
            }
        }

        return ans;
    }
}
```

---

## 5.5 Complexity

- Left pass: `O(n log P)`
- Right pass: `O(n log P)`
- Final combine: `O(n)`

where:

```text
P = 5000
```

So:

- Time: `O(n log 5000)` which is essentially very fast
- Space: `O(n + 5000)`

This is excellent for the constraints.

---

# 6. Dry Run of the Fenwick Solution

Take:

```text
prices  = [10, 2, 3, 4]
profits = [100, 2, 7, 10]
```

We compute `left` first.

---

## Left pass

### j = 0

- price = 10
- query prices `< 10` → none
- `left[0] = -1`
- update price 10 with profit 100

### j = 1

- price = 2
- query prices `< 2` → none
- `left[1] = -1`
- update price 2 with profit 2

### j = 2

- price = 3
- query prices `< 3` → best is price 2 with profit 2
- `left[2] = 2`
- update price 3 with profit 7

### j = 3

- price = 4
- query prices `< 4` → best among prices 2 and 3 is 7
- `left[3] = 7`
- update price 4 with profit 10

So:

```text
left = [-1, -1, 2, 7]
```

---

## Right pass

We scan from right to left.

### j = 3

- price = 4
- need prices `> 4` on right → none
- `right[3] = -1`
- update reversed price of 4 with profit 10

### j = 2

- price = 3
- need prices `> 3` on right → best is price 4, profit 10
- `right[2] = 10`
- update reversed price of 3 with profit 7

### j = 1

- price = 2
- need prices `> 2` on right → best is max(7, 10) = 10
- `right[1] = 10`
- update reversed price of 2 with profit 2

### j = 0

- price = 10
- need prices `> 10` on right → none
- `right[0] = -1`

So:

```text
right = [-1, 10, 10, -1]
```

---

## Combine

Try each middle `j`:

- `j = 0`: invalid
- `j = 1`: invalid
- `j = 2`: `2 + 7 + 10 = 19`
- `j = 3`: invalid

Answer:

```text
19
```

Correct.

---

# 7. Approach 4 — Segment Tree Over Prices

The Fenwick approach is best here because we only need prefix maxima.

But a segment tree also works.

## Idea

Maintain a segment tree over the price domain `[1..5000]`.

Support:

- point update: at a price, keep max profit seen so far
- range max query:
  - left side: query `[1 .. price-1]`
  - right side: query `[price+1 .. 5000]`

This is more general than BIT but slightly more verbose.

---

## Java Code — Segment Tree

```java
import java.util.*;

class SolutionSegmentTree {
    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
            Arrays.fill(this.tree, -1);
        }

        void update(int node, int left, int right, int index, int value) {
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

        int query(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return -1;
            if (ql <= left && right <= qr) return tree[node];

            int mid = left + (right - left) / 2;
            return Math.max(
                query(node * 2, left, mid, ql, qr),
                query(node * 2 + 1, mid + 1, right, ql, qr)
            );
        }

        void update(int index, int value) {
            update(1, 1, n, index, value);
        }

        int query(int l, int r) {
            if (l > r) return -1;
            return query(1, 1, n, l, r);
        }
    }

    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;
        int maxPrice = 5000;

        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, -1);
        Arrays.fill(right, -1);

        SegmentTree leftSeg = new SegmentTree(maxPrice);
        for (int j = 0; j < n; j++) {
            left[j] = leftSeg.query(1, prices[j] - 1);
            leftSeg.update(prices[j], profits[j]);
        }

        SegmentTree rightSeg = new SegmentTree(maxPrice);
        for (int j = n - 1; j >= 0; j--) {
            right[j] = rightSeg.query(prices[j] + 1, maxPrice);
            rightSeg.update(prices[j], profits[j]);
        }

        int ans = -1;
        for (int j = 0; j < n; j++) {
            if (left[j] != -1 && right[j] != -1) {
                ans = Math.max(ans, left[j] + profits[j] + right[j]);
            }
        }

        return ans;
    }
}
```

---

## Complexity

- Time: `O(n log 5000)`
- Space: `O(n + 5000)`

Same asymptotic class as Fenwick.

---

# 8. Approach 5 — Coordinate Compression Variant

Because prices are already bounded by `5000`, compression is not needed here.

But if this same problem had:

```text
prices[i] up to 10^9
```

then we would coordinate-compress prices first and apply the exact same Fenwick/segment tree logic.

So the real pattern is not “bounded price values”, but:

> ordered keys + prefix/suffix max queries

Compression is just what makes large key spaces manageable.

---

# 9. Why the Middle-Index Formula Is Correct

Let us justify the key formula:

```text
bestLeft[j] + profits[j] + bestRight[j]
```

for a fixed `j`.

Any valid triplet with middle index `j` has form:

```text
(i, j, k)
```

such that:

- `i < j < k`
- `prices[i] < prices[j] < prices[k]`

For this fixed `j`:

- the left part depends only on choosing a valid `i`
- the right part depends only on choosing a valid `k`

There is no interaction between which valid `i` and which valid `k` we choose, other than both needing to satisfy the independent price inequalities against `prices[j]`.

Therefore, the best triplet using `j` is obtained by independently taking:

- the valid left index with maximum profit
- the valid right index with maximum profit

That proves the decomposition.

---

# 10. Correctness Proof for the Fenwick Solution

## Definition

Let:

- `left[j]` be the maximum profit among indices `i < j` with `prices[i] < prices[j]`
- `right[j]` be the maximum profit among indices `k > j` with `prices[k] > prices[j]`

The algorithm computes these arrays and then maximizes:

```text
left[j] + profits[j] + right[j]
```

---

## Lemma 1

After processing indices `0..j-1` in the left-to-right pass, the Fenwick tree stores the maximum profit seen for each price prefix.

### Proof

Each processed item updates its price position with its profit via `max`. Fenwick prefix query returns the maximum over all prices up to that threshold. ∎

---

## Lemma 2

For each index `j`, the left pass computes exactly:

```text
left[j] = max profits[i] over all i < j with prices[i] < prices[j]
```

### Proof

At step `j`, only indices `< j` have been inserted. Querying prefix `prices[j] - 1` retrieves the maximum profit among all smaller prices. That is exactly the definition of `left[j]`. ∎

---

## Lemma 3

For each index `j`, the right pass computes exactly:

```text
right[j] = max profits[k] over all k > j with prices[k] > prices[j]
```

### Proof

Scanning from right to left ensures only indices `> j` are present. Reversing the price order converts “greater price” into a prefix condition. Thus the Fenwick prefix query returns exactly the maximum valid right profit. ∎

---

## Lemma 4

For any fixed middle index `j`, the maximum profit of a valid triplet using `j` is:

```text
left[j] + profits[j] + right[j]
```

whenever both sides exist.

### Proof

By definition, `left[j]` is the maximum valid left profit and `right[j]` is the maximum valid right profit. Since the left and right choices are independent given `j`, their sum with `profits[j]` is optimal. ∎

---

## Theorem

The algorithm returns the maximum profit among all valid triplets, or `-1` if none exists.

### Proof

By Lemma 4, for each `j` the algorithm computes the best valid triplet using `j` as middle. Taking the maximum over all `j` yields the global optimum. If no `j` has both sides valid, then no valid triplet exists, so `-1` is correct. ∎

---

# 11. Common Mistakes

## Mistake 1: Reusing the `O(n^2)` solution from version I

That will time out for `n = 50000`.

## Mistake 2: Forgetting the strict inequality

The condition is:

```text
prices[i] < prices[j] < prices[k]
```

Equal prices do not work.

So left query must use strictly smaller prices, and right query must use strictly larger prices.

## Mistake 3: Ignoring index order

You cannot just sort by price and pick three items. The original indices must satisfy:

```text
i < j < k
```

## Mistake 4: Thinking BIT only works for sums

A BIT can also support prefix maximum queries when updates are monotonic max-updates.

## Mistake 5: Using `long` unnecessarily for the final answer type

Here the maximum possible total is:

```text
3 * 10^6
```

which fits safely in `int`.

---

# 12. Comparison of Approaches

| Approach                    |            Time |         Space | Notes                     |
| --------------------------- | --------------: | ------------: | ------------------------- |
| Brute force triplets        |        `O(n^3)` |        `O(1)` | Impossible                |
| Fix middle, scan both sides |        `O(n^2)` |        `O(1)` | Too slow for this version |
| Fenwick tree by price       | `O(n log 5000)` | `O(n + 5000)` | Best practical solution   |
| Segment tree by price       | `O(n log 5000)` | `O(n + 5000)` | Also good, more verbose   |
| Compression + tree          |    `O(n log n)` |        `O(n)` | Generalized version       |

---

# 13. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class FenwickMax {
        int n;
        int[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new int[n + 1];
            Arrays.fill(this.bit, -1);
        }

        void update(int index, int value) {
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        int query(int index) {
            int ans = -1;
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;
        int maxPrice = 5000;

        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, -1);
        Arrays.fill(right, -1);

        FenwickMax leftBit = new FenwickMax(maxPrice);
        for (int j = 0; j < n; j++) {
            left[j] = leftBit.query(prices[j] - 1);
            leftBit.update(prices[j], profits[j]);
        }

        FenwickMax rightBit = new FenwickMax(maxPrice);
        for (int j = n - 1; j >= 0; j--) {
            int rev = maxPrice - prices[j] + 1;
            right[j] = rightBit.query(rev - 1);
            rightBit.update(rev, profits[j]);
        }

        int ans = -1;
        for (int j = 0; j < n; j++) {
            if (left[j] != -1 && right[j] != -1) {
                ans = Math.max(ans, left[j] + profits[j] + right[j]);
            }
        }

        return ans;
    }
}
```

---

# 14. Interview Summary

The decisive observation is that every valid triplet has a middle index `j`.

So for each `j`, the problem splits into two independent queries:

- best left profit with smaller price
- best right profit with larger price

Because prices are bounded by `5000`, we can use Fenwick trees over the price domain to answer those max queries efficiently.

That yields an `O(n log 5000)` solution, which is fast enough for `n = 50000`.
