# 2907. Maximum Profitable Triplets With Increasing Prices I — Exhaustive Java Notes

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

- Index `0` cannot be part of a valid increasing-price triplet because `10` is already too large.
- The only valid triplet is `(1, 2, 3)`:
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

Since prices are strictly increasing, **any** index triplet `i < j < k` is valid.

We want the most profitable valid triplet. One best choice is:

- indices `(1, 3, 4)`
- profits = `5 + 4 + 6 = 15`

So the answer is `15`.

---

## Example 3

```text
Input:  prices = [4,3,2,1], profits = [33,20,19,87]
Output: -1
```

Explanation:

Prices are strictly decreasing, so no triplet can satisfy:

```text
prices[i] < prices[j] < prices[k]
```

Hence the answer is `-1`.

---

## Constraints

- `3 <= prices.length == profits.length <= 2000`
- `1 <= prices[i] <= 10^6`
- `1 <= profits[i] <= 10^6`

---

# 1. Core Insight

We need a triplet `(i, j, k)` such that:

- `i < j < k`
- `prices[i] < prices[j] < prices[k]`

The middle index `j` is special.

If we fix `j`, then we need:

- a left item `i` with smaller price than `prices[j]`
- a right item `k` with larger price than `prices[j]`

Then the total becomes:

```text
bestLeftProfit[j] + profits[j] + bestRightProfit[j]
```

So the problem reduces to:

- for every `j`, find the **best profit on the left** with smaller price
- for every `j`, find the **best profit on the right** with larger price

Then take the maximum over all `j`.

That is the structural heart of the problem.

---

# 2. Approach 1 — Brute Force Over All Triplets

## Idea

Try every possible triplet `(i, j, k)` with `i < j < k`, and check whether:

```text
prices[i] < prices[j] < prices[k]
```

If valid, compute the profit sum and maximize it.

This is the most direct interpretation of the statement.

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

This works only for very small `n`.

Since `n <= 2000`, `O(n^3)` can be too slow in Java in the worst case.

---

# 3. Approach 2 — Fix Middle Index and Scan Left + Right

## Idea

For each middle index `j`:

- scan left side `0..j-1` to find the maximum `profits[i]` such that `prices[i] < prices[j]`
- scan right side `j+1..n-1` to find the maximum `profits[k]` such that `prices[j] < prices[k]`

If both exist, update answer:

```text
leftBest + profits[j] + rightBest
```

This avoids the explicit third nested loop.

## Why it works

Every valid triplet has a middle index `j`.

For that fixed `j`, the best triplet using `j` must choose:

- the most profitable valid left item
- the most profitable valid right item

So this scans all possible middle points and locally chooses the best companions.

## Java Code

```java
class SolutionScanMiddle {
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

This is already good enough for `n <= 2000`.

This is the simplest practical solution for this version of the problem.

---

# 4. Approach 3 — Precompute Best Left and Best Right Arrays

This is mostly a cleaner reorganization of Approach 2.

## Idea

Build two arrays:

- `left[j]` = best profit among indices `i < j` with `prices[i] < prices[j]`
- `right[j]` = best profit among indices `k > j` with `prices[j] < prices[k]`

Then the answer is:

```text
max(left[j] + profits[j] + right[j])
```

over all `j` where both `left[j]` and `right[j]` exist.

## Why bother?

Even though the asymptotic complexity remains `O(n^2)`, this version is often easier to reason about and debug.

It also cleanly separates:

- left compatibility
- right compatibility
- final combination

## Java Code

```java
import java.util.Arrays;

class SolutionPrecompute {
    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;
        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, -1);
        Arrays.fill(right, -1);

        // Compute left[j]
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < j; i++) {
                if (prices[i] < prices[j]) {
                    left[j] = Math.max(left[j], profits[i]);
                }
            }
        }

        // Compute right[j]
        for (int j = 0; j < n; j++) {
            for (int k = j + 1; k < n; k++) {
                if (prices[j] < prices[k]) {
                    right[j] = Math.max(right[j], profits[k]);
                }
            }
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

## Complexity

- Time: `O(n^2)`
- Space: `O(n)`

## Verdict

Excellent for clarity.
Same performance class as Approach 2.

---

# 5. Approach 4 — Coordinate Compression + Fenwick Tree (More Advanced)

This problem has `n <= 2000`, so `O(n^2)` is enough.
But it is still valuable to understand how to solve it more efficiently.

## Idea

The condition depends on comparing prices:

- left side wants `price < currentPrice`
- right side wants `price > currentPrice`

This suggests a data structure keyed by price.

Since `prices[i]` can be up to `10^6`, we first **coordinate-compress** them.

Then:

- scan from left to right to compute best left profit for each `j`
- scan from right to left to compute best right profit for each `j`

We can use a Fenwick Tree for **prefix maximum**.

### Left side

For each `j`, query:

```text
max profit among compressed prices < prices[j]
```

Then update the Fenwick Tree with `profits[j]` at `prices[j]`.

### Right side

For the right side, we need:

```text
max profit among prices > prices[j]
```

A Fenwick Tree naturally supports prefix maximum, not suffix maximum.

So we reverse the compressed indices and turn:

```text
price > currentPrice
```

into a prefix query in reversed order.

This is a classic trick.

---

## 5.1 Coordinate Compression

Suppose sorted unique prices are:

```text
[2, 3, 4, 10]
```

Then we map:

```text
2 -> 0
3 -> 1
4 -> 2
10 -> 3
```

Now price comparisons become index comparisons.

---

## 5.2 Fenwick Tree for Maximum

A Fenwick Tree is often used for sums, but it can also maintain:

```text
bit[x] = maximum value in a prefix
```

The operation becomes:

- `update(index, value)` = set all relevant tree nodes to `max(current, value)`
- `query(index)` = maximum in prefix `[0..index]`

This works because `max` is associative and monotonic for these update/query patterns.

---

## 5.3 Java Code

```java
import java.util.*;

class SolutionFenwick {
    static class FenwickMax {
        int n;
        int[] bit;

        FenwickMax(int n) {
            this.n = n;
            this.bit = new int[n + 1];
            Arrays.fill(this.bit, -1);
        }

        void update(int index, int value) {
            index++; // 1-based
            while (index <= n) {
                bit[index] = Math.max(bit[index], value);
                index += index & -index;
            }
        }

        int query(int index) {
            int ans = -1;
            index++; // 1-based
            while (index > 0) {
                ans = Math.max(ans, bit[index]);
                index -= index & -index;
            }
            return ans;
        }
    }

    public int maxProfit(int[] prices, int[] profits) {
        int n = prices.length;

        int[] sorted = prices.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (int x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }

        int[] unique = Arrays.copyOf(sorted, m);

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[i] = Arrays.binarySearch(unique, prices[i]);
        }

        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, -1);
        Arrays.fill(right, -1);

        // Left side: best profit among prices < prices[j]
        FenwickMax fwLeft = new FenwickMax(m);
        for (int j = 0; j < n; j++) {
            int r = rank[j];
            if (r - 1 >= 0) {
                left[j] = fwLeft.query(r - 1);
            }
            fwLeft.update(r, profits[j]);
        }

        // Right side: best profit among prices > prices[j]
        // Reverse ranks so "greater original price" becomes "smaller reversed rank"
        FenwickMax fwRight = new FenwickMax(m);
        for (int j = n - 1; j >= 0; j--) {
            int rev = m - 1 - rank[j];
            if (rev - 1 >= 0) {
                right[j] = fwRight.query(rev - 1);
            }
            fwRight.update(rev, profits[j]);
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

## 5.4 Complexity

- Coordinate compression: `O(n log n)`
- Left Fenwick pass: `O(n log n)`
- Right Fenwick pass: `O(n log n)`

Overall:

- Time: `O(n log n)`
- Space: `O(n)`

## Verdict

This is the most advanced and efficient solution among the approaches here.

For this problem's small constraints it is not necessary, but it is great practice for harder variants.

---

# 6. Dry Run of the Simple `O(n^2)` Method

Let:

```text
prices  = [10, 2, 3, 4]
profits = [100, 2, 7, 10]
```

We try each middle index `j`.

## j = 0

Price = 10

Left side does not exist.
So `j = 0` cannot be the middle.

## j = 1

Price = 2

Left side indices: `[0]`

- `prices[0] = 10`, not `< 2`

So no valid left item.

No valid triplet through `j = 1`.

## j = 2

Price = 3

Left side:

- `i = 0`, price 10 not `< 3`
- `i = 1`, price 2 `< 3`, so left best = 2

Right side:

- `k = 3`, price 4 `> 3`, so right best = 10

Total:

```text
2 + 7 + 10 = 19
```

## j = 3

Price = 4

Left side:

- `i = 1`, price 2 `< 4`, profit 2
- `i = 2`, price 3 `< 4`, profit 7
- best left = 7

Right side: none

So `j = 3` cannot be the middle.

Final answer = `19`.

---

# 7. Why the Middle-Index Strategy Is Correct

This is worth proving carefully.

Suppose a valid triplet is `(i, j, k)`.

Then `j` is its middle index.
For this fixed `j`, any valid triplet must use:

- some valid left item `i < j` with `prices[i] < prices[j]`
- some valid right item `k > j` with `prices[j] < prices[k]`

Among all such valid left items, the best one is the one with maximum profit.
Similarly for the right side.

So for every fixed `j`, the best possible triplet containing `j` is:

```text
bestLeft(j) + profits[j] + bestRight(j)
```

Therefore, maximizing over all `j` gives the global optimum.

That is why the `O(n^2)` solution is correct.

---

# 8. Correctness Argument for the Fenwick Approach

## Lemma 1

After processing indices `0..j-1` in the left-to-right Fenwick pass, querying ranks strictly smaller than `prices[j]` returns the maximum profit among all `i < j` with `prices[i] < prices[j]`.

### Reason

The Fenwick Tree stores profits of already-seen indices, keyed by compressed price rank. A prefix query up to `rank[j] - 1` includes exactly the smaller prices.

---

## Lemma 2

After processing indices `n-1..j+1` in the right-to-left Fenwick pass, querying reversed ranks strictly smaller than the reversed rank of `prices[j]` returns the maximum profit among all `k > j` with `prices[k] > prices[j]`.

### Reason

Reversing the ranks turns “greater original price” into “smaller reversed rank”. Therefore a prefix query on reversed ranks captures exactly the greater-price items to the right.

---

## Lemma 3

For each index `j`, `left[j]` and `right[j]` computed by the Fenwick passes are exactly the best valid profits on the left and right sides of `j`.

### Reason

By Lemma 1 and Lemma 2, each query returns exactly the needed maximum under the proper price inequality and index direction.

---

## Theorem

The algorithm returns the maximum obtainable profit over all valid triplets.

### Reason

For each `j`, the algorithm forms the best valid triplet having `j` as middle. Taking the maximum over all `j` yields the global optimum.

---

# 9. Common Mistakes

## Mistake 1: Ignoring index ordering

The condition is not only about prices. We also need:

```text
i < j < k
```

So sorting the items by price alone is not enough.

## Mistake 2: Allowing equal prices

The condition is strictly increasing:

```text
prices[i] < prices[j] < prices[k]
```

Equal prices are not allowed.

## Mistake 3: Using `0` instead of `-1` as “not found”

Profits are positive, so `-1` is a safer sentinel meaning “no valid choice exists”.

## Mistake 4: Overcomplicating this version

Since `n <= 2000`, the `O(n^2)` method is perfectly sufficient.
Do not force a harder structure unless you want practice.

---

# 10. Which Approach Should You Use?

## Best practical choice for this exact problem

Use the `O(n^2)` middle-index scan.

Why:

- easiest to understand
- easy to implement correctly
- efficient enough for `n <= 2000`

## Best for learning advanced patterns

Use the Fenwick-tree version.

Why:

- teaches coordinate compression
- teaches prefix-maximum BIT
- prepares you for more difficult follow-up variants

---

# 11. Final Recommended Java Solution

This is the cleanest solution for the given constraints.

```java
class Solution {
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

---

# 12. Complexity Summary

| Approach                         |         Time |  Space | Notes                 |
| -------------------------------- | -----------: | -----: | --------------------- |
| Brute force triplets             |     `O(n^3)` | `O(1)` | Direct but slow       |
| Fix middle and scan left/right   |     `O(n^2)` | `O(1)` | Best choice here      |
| Precompute left/right arrays     |     `O(n^2)` | `O(n)` | Cleaner organization  |
| Coordinate compression + Fenwick | `O(n log n)` | `O(n)` | Advanced and scalable |

---

# 13. Interview Summary

The key observation is that any valid triplet has a middle item `j`.
Once `j` is fixed, the problem splits into:

- best valid left profit with smaller price
- best valid right profit with larger price

So the simplest correct solution is to scan all `j` and compute those two maxima.

For this version, that gives an `O(n^2)` solution, which is fully acceptable.
