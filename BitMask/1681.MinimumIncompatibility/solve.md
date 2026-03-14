# 1681. Minimum Incompatibility — Exhaustive Java Notes

## Problem Statement

You are given:

- an integer array `nums`
- an integer `k`

You must partition `nums` into exactly `k` subsets of equal size such that:

1. every element is used exactly once,
2. no subset contains duplicate values,
3. the size of each subset is the same.

For any subset, its **incompatibility** is:

```text
max(subset) - min(subset)
```

Return the minimum possible total incompatibility across all `k` subsets, or `-1` if such a partition is impossible.

---

## Example 1

```text
Input:
nums = [1,2,1,4], k = 2

Output:
4
```

Explanation:

One optimal partition is:

- `[1,2]` → incompatibility `2 - 1 = 1`
- `[1,4]` → incompatibility `4 - 1 = 3`

Total:

```text
1 + 3 = 4
```

---

## Example 2

```text
Input:
nums = [6,3,8,1,3,1,2,2], k = 4

Output:
6
```

One optimal partition:

- `[1,2]` → `1`
- `[2,3]` → `1`
- `[6,8]` → `2`
- `[1,3]` → `2`

Total:

```text
6
```

---

## Example 3

```text
Input:
nums = [5,3,3,6,3,3], k = 3

Output:
-1
```

Explanation:

There are too many copies of `3`, so some subset would be forced to contain duplicates.

---

## Constraints

```text
1 <= k <= nums.length <= 16
nums.length is divisible by k
1 <= nums[i] <= nums.length
```

The most important constraint is:

```text
nums.length <= 16
```

That immediately suggests:

- subset enumeration,
- bitmask DP,
- memoized search over used elements.

---

# 1. Core Insight

Let:

```text
n = nums.length
groupSize = n / k
```

We want to partition the array into `k` groups, each of size `groupSize`.

Because `n <= 16`, it is feasible to represent any subset of indices using a bitmask:

- bit `i = 1` → `nums[i]` is already used
- bit `i = 0` → `nums[i]` is still unused

The main challenge is:

- generate only valid groups (size = `groupSize`, no duplicates),
- compute each group's incompatibility,
- combine groups optimally.

---

# 2. Immediate Necessary Condition

If any value appears more than `k` times, the answer is impossible.

Why?

Because each subset may contain that value at most once, and there are only `k` subsets.

So before anything else, we should count frequencies.

If:

```text
freq[x] > k
```

return `-1`.

This is a powerful early pruning step.

---

# 3. Why Index Masks, Not Value Masks?

Even if values repeat, indices are distinct positions in the array.

Example:

```text
nums = [1, 1, 2, 4]
```

The two `1`s must still be treated as different positions during the partition process.

So the DP naturally works over **indices**, not just values.

---

# 4. Group Incompatibility

For any chosen group (subset of indices) of size `groupSize`, if all values are distinct:

```text
incompatibility = max(nums[i]) - min(nums[i])
```

This value depends only on the indices in that subset.

Thus we can precompute incompatibility for every valid subset of size `groupSize`.

That is the key to the standard DP solution.

---

# 5. Approach 1 — Bitmask DP with Precomputed Valid Groups

## Main Idea

1. Enumerate all subsets of indices of size `groupSize`.
2. Keep only those whose values are all distinct.
3. Compute incompatibility for each such subset.
4. Use DP over masks:
   - `dp[mask] = minimum incompatibility to cover exactly the indices in mask`

Transition:

- from `mask`, choose a valid group `sub` disjoint from `mask`
- update:

```text
dp[mask | sub] = min(dp[mask | sub], dp[mask] + incompatibility[sub])
```

However, a more efficient formulation is to build from smaller to larger masks, where only masks whose bit count is a multiple of `groupSize` are meaningful.

---

## Precomputing Valid Groups

For every subset `sub`:

- if `bitCount(sub) != groupSize`, skip
- check whether values inside are distinct
- if yes, store:

```text
cost[sub] = max - min
```

Otherwise mark as invalid.

Since `n <= 16`, scanning all `2^n` subsets is feasible.

---

## DP Formulation

Let:

```text
dp[mask] = minimum incompatibility for used indices = mask
```

Base:

```text
dp[0] = 0
```

Transition:

If `mask` has size that is a multiple of `groupSize`, we choose a valid group `sub` disjoint from `mask`.

This yields:

```text
dp[mask | sub] = min(dp[mask | sub], dp[mask] + cost[sub])
```

Final answer:

```text
dp[(1 << n) - 1]
```

If unreachable, return `-1`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumIncompatibility(int[] nums, int k) {
        int n = nums.length;
        int groupSize = n / k;

        int[] freq = new int[n + 1];
        for (int x : nums) {
            freq[x]++;
            if (freq[x] > k) return -1;
        }

        int totalMasks = 1 << n;
        int[] cost = new int[totalMasks];
        Arrays.fill(cost, -1);

        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) != groupSize) continue;

            boolean[] seen = new boolean[n + 1];
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            boolean ok = true;

            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    if (seen[nums[i]]) {
                        ok = false;
                        break;
                    }
                    seen[nums[i]] = true;
                    min = Math.min(min, nums[i]);
                    max = Math.max(max, nums[i]);
                }
            }

            if (ok) {
                cost[mask] = max - min;
            }
        }

        int INF = 1_000_000_000;
        int[] dp = new int[totalMasks];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] == INF) continue;
            if (Integer.bitCount(mask) % groupSize != 0) continue;

            int remain = ((1 << n) - 1) ^ mask;

            for (int sub = remain; sub > 0; sub = (sub - 1) & remain) {
                if (cost[sub] != -1) {
                    dp[mask | sub] = Math.min(dp[mask | sub], dp[mask] + cost[sub]);
                }
            }
        }

        return dp[totalMasks - 1] == INF ? -1 : dp[totalMasks - 1];
    }
}
```

---

## Complexity

Let `n = nums.length`.

Precomputing subset costs:

```text
O(2^n * n)
```

DP transitions are the expensive part.
In the worst case, iterating all submasks for each mask can be large, but because only subsets of size `groupSize` with valid cost matter, the practical complexity is acceptable for `n <= 16`.

A common loose bound is:

```text
O(3^n)
```

Space:

```text
O(2^n)
```

---

# 6. Important Optimization for Approach 1

The above DP can be improved by **forcing one canonical unused index** into the next group.

Why?

Without this, many equivalent orderings of subset selection are explored repeatedly.

Suppose we already used indices in `mask`.
Let `firstUnused` be the first index not in `mask`.

Then every next group we form must include `firstUnused`.

This avoids permutation duplicates among groups.

That leads to a much faster transition structure.

---

# 7. Approach 2 — DFS + Memoization with Canonical First Unused Index

## Main Idea

Let:

```text
dfs(mask)
```

be the minimum additional incompatibility needed to partition all unused indices starting from `mask`.

At every step:

1. find the first unused index `first`
2. generate all valid groups of size `groupSize` that:
   - include `first`
   - use only unused indices
   - contain distinct values
3. recurse on `mask | group`

This dramatically reduces symmetry.

---

## Why This Works

Without loss of generality, in any final partition there is exactly one group containing the first unused element.
We can decide that group next.
This avoids counting the same partition under different orders of group creation.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[] nums;
    private int n;
    private int groupSize;
    private int[] memo;
    private int INF = 1_000_000_000;

    public int minimumIncompatibility(int[] nums, int k) {
        this.nums = nums;
        this.n = nums.length;
        this.groupSize = n / k;

        int[] freq = new int[n + 1];
        for (int x : nums) {
            freq[x]++;
            if (freq[x] > k) return -1;
        }

        memo = new int[1 << n];
        Arrays.fill(memo, -2);

        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) {
            return 0;
        }
        if (memo[mask] != -2) {
            return memo[mask];
        }

        int first = 0;
        while (((mask >> first) & 1) == 1) first++;

        List<Integer> available = new ArrayList<>();
        for (int i = first; i < n; i++) {
            if (((mask >> i) & 1) == 0) {
                available.add(i);
            }
        }

        int best = INF;
        boolean[] usedVals = new boolean[n + 1];
        usedVals[nums[first]] = true;

        generate(mask, first, 1, nums[first], nums[first], available, 1 << first, usedVals, bestHolder -> {
            int next = dfs(mask | bestHolder.groupMask);
            if (next != INF) {
                best = Math.min(best, bestHolder.cost + next);
            }
        });

        memo[mask] = best;
        return best;
    }

    private interface Consumer {
        void accept(GroupInfo g);
    }

    private static class GroupInfo {
        int groupMask;
        int cost;
        GroupInfo(int groupMask, int cost) {
            this.groupMask = groupMask;
            this.cost = cost;
        }
    }

    private void generate(int mask, int firstIdx, int chosen, int minVal, int maxVal,
                          List<Integer> available, int groupMask, boolean[] usedVals, Consumer consumer) {
        if (chosen == groupSize) {
            consumer.accept(new GroupInfo(groupMask, maxVal - minVal));
            return;
        }

        for (int idx : available) {
            if (((groupMask >> idx) & 1) == 1) continue;
            if (idx <= firstIdx) continue;
            if (usedVals[nums[idx]]) continue;

            usedVals[nums[idx]] = true;
            generate(mask, idx, chosen + 1,
                    Math.min(minVal, nums[idx]),
                    Math.max(maxVal, nums[idx]),
                    available,
                    groupMask | (1 << idx),
                    usedVals,
                    consumer);
            usedVals[nums[idx]] = false;
        }
    }
}
```

### Note

The above code shows the idea, but Java lambdas with mutable closure of `best` are awkward here.
Below is the cleaner production version.

---

## Cleaner Java Implementation

```java
import java.util.*;

class Solution {
    private int[] nums;
    private int n;
    private int groupSize;
    private int[] memo;
    private int INF = 1_000_000_000;

    public int minimumIncompatibility(int[] nums, int k) {
        this.nums = nums;
        this.n = nums.length;
        this.groupSize = n / k;

        int[] freq = new int[n + 1];
        for (int x : nums) {
            freq[x]++;
            if (freq[x] > k) return -1;
        }

        Arrays.sort(this.nums);
        memo = new int[1 << n];
        Arrays.fill(memo, -1);

        int ans = dfs(0);
        return ans >= INF ? -1 : ans;
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) return 0;
        if (memo[mask] != -1) return memo[mask];

        int first = 0;
        while (((mask >> first) & 1) == 1) first++;

        List<Integer> candidates = new ArrayList<>();
        for (int i = first; i < n; i++) {
            if (((mask >> i) & 1) == 0) {
                candidates.add(i);
            }
        }

        int[] best = new int[]{INF};
        boolean[] usedVals = new boolean[n + 1];
        usedVals[nums[first]] = true;

        buildGroups(first, 1, nums[first], nums[first], 1 << first, candidates, usedVals, mask, best);

        memo[mask] = best[0];
        return memo[mask];
    }

    private void buildGroups(int lastIndex, int chosen, int minVal, int maxVal, int groupMask,
                             List<Integer> candidates, boolean[] usedVals, int mask, int[] best) {
        if (chosen == groupSize) {
            int next = dfs(mask | groupMask);
            if (next < INF) {
                best[0] = Math.min(best[0], (maxVal - minVal) + next);
            }
            return;
        }

        for (int idx : candidates) {
            if (idx <= lastIndex) continue;
            if (((groupMask >> idx) & 1) == 1) continue;
            if (usedVals[nums[idx]]) continue;

            usedVals[nums[idx]] = true;
            buildGroups(
                idx,
                chosen + 1,
                Math.min(minVal, nums[idx]),
                Math.max(maxVal, nums[idx]),
                groupMask | (1 << idx),
                candidates,
                usedVals,
                mask,
                best
            );
            usedVals[nums[idx]] = false;
        }
    }
}
```

---

## Complexity

The exact bound is messy, but this approach is usually described as exponential in `n` with memoization over `2^n` masks.

A practical upper bound is around:

```text
O(2^n * C(n, groupSize))
```

with strong pruning from:

- first-unused canonicalization
- duplicate-value restriction
- sorting

This is very fast for `n <= 16`.

Space:

```text
O(2^n)
```

for memo, plus recursion stack.

---

# 8. Approach 3 — Precompute Valid Group Masks + DFS / DP on Used Mask

This is a refined version of Approaches 1 and 2.

## Idea

Precompute **all valid group masks** once:

- mask size = `groupSize`
- all values distinct
- cost = incompatibility

Then during DFS on `mask`, only consider valid groups that:

- are disjoint from `mask`
- contain the first unused index

This gives the clarity of precomputation plus the efficiency of canonicalized recursion.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int n;
    private int groupSize;
    private int[] nums;
    private int[] memo;
    private List<int[]> validGroups;
    private int INF = 1_000_000_000;

    public int minimumIncompatibility(int[] nums, int k) {
        this.nums = nums.clone();
        Arrays.sort(this.nums);
        this.n = nums.length;
        this.groupSize = n / k;

        int[] freq = new int[n + 1];
        for (int x : nums) {
            freq[x]++;
            if (freq[x] > k) return -1;
        }

        validGroups = new ArrayList<>();
        int totalMasks = 1 << n;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) != groupSize) continue;

            boolean[] seen = new boolean[n + 1];
            boolean ok = true;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    if (seen[this.nums[i]]) {
                        ok = false;
                        break;
                    }
                    seen[this.nums[i]] = true;
                    min = Math.min(min, this.nums[i]);
                    max = Math.max(max, this.nums[i]);
                }
            }

            if (ok) {
                validGroups.add(new int[]{mask, max - min});
            }
        }

        memo = new int[1 << n];
        Arrays.fill(memo, -1);

        int ans = dfs(0);
        return ans >= INF ? -1 : ans;
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) return 0;
        if (memo[mask] != -1) return memo[mask];

        int firstUnused = 0;
        while (((mask >> firstUnused) & 1) == 1) firstUnused++;

        int best = INF;

        for (int[] g : validGroups) {
            int groupMask = g[0];
            int cost = g[1];

            if (((groupMask >> firstUnused) & 1) == 0) continue;
            if ((groupMask & mask) != 0) continue;

            int next = dfs(mask | groupMask);
            if (next < INF) {
                best = Math.min(best, cost + next);
            }
        }

        memo[mask] = best;
        return best;
    }
}
```

---

## Why This Version Is Nice

It cleanly separates:

1. **which subsets are legal groups**
2. **how we combine them optimally**

This is often the easiest version to explain clearly in an interview.

---

# 9. Why Frequency > k Means Impossible

Suppose a value `x` appears `f` times.

Each subset may contain `x` at most once.
There are only `k` subsets total.

So at most `k` copies of `x` can be placed validly.

If:

```text
f > k
```

then pigeonhole principle says some subset would contain `x` twice.

Thus the answer must be `-1`.

This is an essential first pruning step.

---

# 10. Why Greedy Fails

A tempting strategy is:

> always build the locally cheapest valid subset first

This is not correct.

Because using a certain number now may block the only valid placement for duplicates later.

Example-style intuition:

- repeated values create global constraints,
- small local incompatibility can still force impossible or expensive future groups.

So the problem has strong combinational dependency, and DP/backtracking is required.

---

# 11. Worked Example

Take:

```text
nums = [1,2,1,4], k = 2
```

Then:

```text
groupSize = 2
```

All size-2 subsets of indices:

- `{0,1}` → values `[1,2]` valid, cost `1`
- `{0,2}` → values `[1,1]` invalid
- `{0,3}` → `[1,4]` valid, cost `3`
- `{1,2}` → `[2,1]` valid, cost `1`
- `{1,3}` → `[2,4]` valid, cost `2`
- `{2,3}` → `[1,4]` valid, cost `3`

Now choose two disjoint valid groups covering all 4 indices.

Best choices:

- `{0,1}` and `{2,3}` → cost `1 + 3 = 4`
- `{1,2}` and `{0,3}` → cost `1 + 3 = 4`

So answer is `4`.

---

# 12. Correctness Sketch

The problem asks for a partition into valid groups of fixed size.

Every valid solution is exactly a collection of disjoint valid subsets of size `groupSize` that together cover all indices.

So if we:

1. enumerate all valid groups,
2. recursively or iteratively choose disjoint groups until all indices are covered,
3. minimize total cost,

then we are exploring exactly the solution space.

Canonicalization by first unused index does not remove any valid partition. It only fixes the order in which groups are chosen, eliminating symmetric duplicates.

Thus the optimal solution is preserved, and the minimum found is correct.

---

# 13. Comparison of Approaches

| Approach                      | Main Idea                                  |                               Time |                  Space | Notes                   |
| ----------------------------- | ------------------------------------------ | ---------------------------------: | ---------------------: | ----------------------- |
| Bitmask DP with valid subsets | transitions using all valid groups         |                   roughly `O(3^n)` |               `O(2^n)` | standard                |
| DFS + memo + first unused     | canonical recursive grouping               | exponential, very fast in practice |               `O(2^n)` | strong interview choice |
| Precompute groups + DFS       | clean separation of legality + combination |                            similar | `O(2^n + validGroups)` | easiest to explain      |

---

# 14. Recommended Approach

For interview clarity, I would recommend:

## Precompute valid groups + DFS memo on used mask

Why?

- easy to derive,
- easy to prove,
- avoids repeated subset legality checks,
- symmetry reduction is clean.

If you want the most “editorial-like” concise DP, Approach 1 is also excellent.

---

# 15. Final Interview Summary

This is a classic `n <= 16` problem, so the natural tool is:

```text
bitmask DP
```

Key steps:

1. `groupSize = n / k`
2. early impossible check: any frequency `> k` → return `-1`
3. precompute all valid subsets of size `groupSize`
4. each valid subset has incompatibility = `max - min`
5. use memoized DFS or DP over masks to choose disjoint valid subsets covering all indices
6. minimize total incompatibility

The hardest part conceptually is noticing that the problem is really:

> partition the indices into fixed-size valid subsets, then minimize sum of per-subset costs.

Once seen that way, the bitmask formulation becomes natural.

---

# 16. Recommended Java Solution

```java
import java.util.*;

class Solution {
    private int n;
    private int groupSize;
    private int[] nums;
    private int[] memo;
    private List<int[]> validGroups;
    private int INF = 1_000_000_000;

    public int minimumIncompatibility(int[] nums, int k) {
        this.nums = nums.clone();
        Arrays.sort(this.nums);
        this.n = nums.length;
        this.groupSize = n / k;

        int[] freq = new int[n + 1];
        for (int x : nums) {
            freq[x]++;
            if (freq[x] > k) return -1;
        }

        validGroups = new ArrayList<>();
        int totalMasks = 1 << n;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) != groupSize) continue;

            boolean[] seen = new boolean[n + 1];
            boolean ok = true;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {
                    if (seen[this.nums[i]]) {
                        ok = false;
                        break;
                    }
                    seen[this.nums[i]] = true;
                    min = Math.min(min, this.nums[i]);
                    max = Math.max(max, this.nums[i]);
                }
            }

            if (ok) {
                validGroups.add(new int[]{mask, max - min});
            }
        }

        memo = new int[1 << n];
        Arrays.fill(memo, -1);

        int ans = dfs(0);
        return ans >= INF ? -1 : ans;
    }

    private int dfs(int mask) {
        if (mask == (1 << n) - 1) return 0;
        if (memo[mask] != -1) return memo[mask];

        int firstUnused = 0;
        while (((mask >> firstUnused) & 1) == 1) firstUnused++;

        int best = INF;

        for (int[] g : validGroups) {
            int groupMask = g[0];
            int cost = g[1];

            if (((groupMask >> firstUnused) & 1) == 0) continue;
            if ((groupMask & mask) != 0) continue;

            int next = dfs(mask | groupMask);
            if (next < INF) {
                best = Math.min(best, cost + next);
            }
        }

        memo[mask] = best;
        return best;
    }
}
```
