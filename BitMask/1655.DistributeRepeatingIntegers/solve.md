# Distribute Integers to Customers — Exhaustive Java Notes

## Problem Statement

You are given:

- an integer array `nums`
- an integer array `quantity`

For each customer `i`, they need exactly:

```text
quantity[i]
```

integers.

The distribution must satisfy all of the following:

1. Customer `i` receives exactly `quantity[i]` integers.
2. All integers given to a single customer must be equal.
3. Every customer must be satisfied.

Return `true` if such a distribution is possible, otherwise return `false`.

---

## Example 1

```text
Input:
nums = [1,2,3,4]
quantity = [2]

Output:
false
```

Explanation:

The only customer wants 2 equal integers, but all values are distinct.

---

## Example 2

```text
Input:
nums = [1,2,3,3]
quantity = [2]

Output:
true
```

Explanation:

Give `[3,3]` to the customer.

---

## Example 3

```text
Input:
nums = [1,1,2,2]
quantity = [2,2]

Output:
true
```

Explanation:

- customer 0 gets `[1,1]`
- customer 1 gets `[2,2]`

---

## Constraints

```text
1 <= nums.length <= 10^5
1 <= nums[i] <= 1000
1 <= quantity.length <= 10
1 <= quantity[i] <= 10^5
There are at most 50 unique values in nums.
```

---

# 1. Core Insight

The actual values in `nums` do not matter individually.

What matters is only the **frequency** of each distinct value.

For example, if:

```text
nums = [1,1,1,2,2,3]
```

then the usable resource pool is:

```text
count(1) = 3
count(2) = 2
count(3) = 1
```

Because each customer must receive equal integers, each customer must be assigned entirely from **one frequency bucket**.

So the problem becomes:

> Can we assign each customer demand to one of the available frequency counts, such that no frequency is exceeded?

---

# 2. Why the Constraints Suggest Bitmask / DP

The large `nums.length` is a distraction.

Important constraints are:

- at most **50 unique values**
- number of customers `m <= 10`

That means the expensive dimension is not the array itself, but the customer subset space:

```text
2^m <= 2^10 = 1024
```

This is small.

That strongly suggests:

- backtracking over customers
- bitmask DP over customer subsets
- memoization on subsets

---

# 3. Preprocessing

## Step 1: Compress `nums` into counts

Build a frequency map over `nums`.

Example:

```text
nums = [1,1,1,2,2,3]
```

becomes:

```text
counts = [3,2,1]
```

Only these frequencies matter.

---

## Step 2: Sort customer demands in descending order

Sorting `quantity` in descending order is very useful.

Why?

Because large demands are harder to place. If a large demand fails, we can prune early.

Example:

```text
quantity = [2,3,1]
```

sort to:

```text
[3,2,1]
```

This often greatly reduces branching in backtracking.

---

# 4. Approach 1 — Backtracking on Customer Demands

## Idea

After converting `nums` into counts, try to assign customers one by one.

For customer `i`, we try every count bucket that can satisfy `quantity[i]`.

If bucket `j` has at least `quantity[i]`, assign the customer there:

```text
counts[j] -= quantity[i]
```

recurse, and then backtrack.

---

## Important Pruning

If multiple buckets currently have the same remaining count, trying all of them is redundant.

Example:

```text
counts = [5,5,3]
quantity[i] = 2
```

Assigning to the first `5` or the second `5` leads to symmetric states.

So we skip duplicate count states at the same recursion depth.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean canDistribute(int[] nums, int[] quantity) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }

        int[] counts = new int[freq.size()];
        int idx = 0;
        for (int c : freq.values()) {
            counts[idx++] = c;
        }

        Arrays.sort(quantity);
        reverse(quantity); // descending

        Arrays.sort(counts);
        reverse(counts);   // optional but often helps

        return backtrack(0, quantity, counts);
    }

    private boolean backtrack(int i, int[] quantity, int[] counts) {
        if (i == quantity.length) {
            return true;
        }

        int need = quantity[i];
        Set<Integer> used = new HashSet<>();

        for (int j = 0; j < counts.length; j++) {
            if (counts[j] < need) continue;
            if (used.contains(counts[j])) continue;

            used.add(counts[j]);
            counts[j] -= need;

            if (backtrack(i + 1, quantity, counts)) {
                return true;
            }

            counts[j] += need;
        }

        return false;
    }

    private void reverse(int[] arr) {
        for (int l = 0, r = arr.length - 1; l < r; l++, r--) {
            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;
        }
    }
}
```

---

## Complexity

Let:

- `u` = number of unique values in `nums` (`u <= 50`)
- `m` = number of customers (`m <= 10`)

Worst-case backtracking is exponential, roughly:

```text
O(u^m)
```

but in practice:

- sorting
- duplicate pruning
- small `m`

make it fast enough.

Space:

```text
O(m)
```

for recursion depth, ignoring input storage.

---

# 5. Approach 2 — DP with Subset Sums + Backtracking over Frequency Buckets

## Main Idea

This is the classic and strongest approach.

Instead of assigning one customer at a time directly, for each frequency bucket we ask:

> which subset of customers can this bucket satisfy all at once?

Since `m <= 10`, we can enumerate subsets of customers.

---

## Step A: Precompute subset sums of customer demands

Let:

```text
sum[mask]
```

be the total demand of the customer subset represented by `mask`.

Example:

If:

```text
quantity = [2,3,1]
```

then:

- `mask 001` → `1`
- `mask 010` → `3`
- `mask 011` → `3 + 1 = 4`
- etc.

This can be precomputed for all masks in:

```text
O(m * 2^m)
```

---

## Step B: DP / DFS over count buckets and served customer subsets

Suppose we have frequency counts:

```text
counts[0], counts[1], ..., counts[u-1]
```

Define:

```text
dfs(i, mask)
```

where:

- `i` = which count bucket we are considering
- `mask` = which customers have already been satisfied

Return whether it is possible to satisfy all remaining customers using buckets from `i` onward.

### Choices at bucket `i`

We can assign this bucket to **any subset of unsatisfied customers** whose total demand is at most `counts[i]`.

Then recurse.

We can also skip the bucket.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[] counts;
    private int[] quantity;
    private int[] subsetSum;
    private Boolean[][] memo;
    private int fullMask;

    public boolean canDistribute(int[] nums, int[] quantity) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }

        counts = new int[freq.size()];
        int idx = 0;
        for (int c : freq.values()) {
            counts[idx++] = c;
        }

        Arrays.sort(counts);
        reverse(counts);

        this.quantity = quantity.clone();
        Arrays.sort(this.quantity);
        reverse(this.quantity);

        int m = this.quantity.length;
        fullMask = (1 << m) - 1;

        subsetSum = new int[1 << m];
        for (int mask = 1; mask <= fullMask; mask++) {
            int lsb = mask & -mask;
            int bit = Integer.numberOfTrailingZeros(lsb);
            subsetSum[mask] = subsetSum[mask ^ lsb] + this.quantity[bit];
        }

        memo = new Boolean[counts.length + 1][1 << m];
        return dfs(0, 0);
    }

    private boolean dfs(int i, int mask) {
        if (mask == fullMask) {
            return true;
        }
        if (i == counts.length) {
            return false;
        }
        if (memo[i][mask] != null) {
            return memo[i][mask];
        }

        // Option 1: skip this count bucket
        if (dfs(i + 1, mask)) {
            return memo[i][mask] = true;
        }

        int remaining = fullMask ^ mask;

        // Try all subsets of remaining customers
        for (int sub = remaining; sub > 0; sub = (sub - 1) & remaining) {
            if (subsetSum[sub] <= counts[i]) {
                if (dfs(i + 1, mask | sub)) {
                    return memo[i][mask] = true;
                }
            }
        }

        return memo[i][mask] = false;
    }

    private void reverse(int[] arr) {
        for (int l = 0, r = arr.length - 1; l < r; l++, r--) {
            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;
        }
    }
}
```

---

## Why This Works

Each frequency bucket corresponds to one integer value.

If a bucket has size `c`, it can satisfy any subset of customers whose total demand is at most `c`, because all those customers can receive that same integer value.

The DP tries all such valid subset assignments across all buckets.

Because every customer must be satisfied exactly once, the mask tracks which customers are already covered.

---

## Complexity

Let:

- `u <= 50`
- `m <= 10`

State count:

```text
O(u * 2^m)
```

Transition per state may iterate over all submasks:

```text
O(2^m)
```

So upper bound:

```text
O(u * 4^m)
```

Since `m <= 10`, this is fully acceptable.

Space:

```text
O(u * 2^m)
```

for memo.

---

# 6. Approach 3 — Bottom-Up DP on Customer Masks

## Idea

We can convert the same subset-based logic into iterative DP.

Let:

```text
dp[mask] = whether it is possible to satisfy exactly the set of customers in mask
```

Start with:

```text
dp[0] = true
```

Process each count bucket one by one.

For each existing mask, try adding a subset of unsatisfied customers whose total demand fits into the current bucket.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean canDistribute(int[] nums, int[] quantity) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }

        int[] counts = new int[freq.size()];
        int idx = 0;
        for (int c : freq.values()) {
            counts[idx++] = c;
        }

        int m = quantity.length;
        int full = (1 << m) - 1;

        int[] subsetSum = new int[1 << m];
        for (int mask = 1; mask <= full; mask++) {
            int lsb = mask & -mask;
            int bit = Integer.numberOfTrailingZeros(lsb);
            subsetSum[mask] = subsetSum[mask ^ lsb] + quantity[bit];
        }

        boolean[] dp = new boolean[1 << m];
        dp[0] = true;

        for (int c : counts) {
            boolean[] next = dp.clone();

            for (int mask = 0; mask <= full; mask++) {
                if (!dp[mask]) continue;

                int remain = full ^ mask;
                for (int sub = remain; sub > 0; sub = (sub - 1) & remain) {
                    if (subsetSum[sub] <= c) {
                        next[mask | sub] = true;
                    }
                }
            }

            dp = next;
        }

        return dp[full];
    }
}
```

---

## Complexity

Same essential bound as subset DP:

```text
O(u * 4^m)
```

Space:

```text
O(2^m)
```

This version is iterative and sometimes preferred if recursion is not desired.

---

# 7. Strong Optimization: Sort Quantities Descending

This optimization helps both backtracking and DP pruning.

Why?

Suppose customers need:

```text
[1,1,1,5]
```

If you place `5` first, failure is detected much earlier than if you place small demands first.

That is why many accepted solutions sort `quantity` descending.

---

# 8. Another Optimization: Remove Useless Symmetry in Backtracking

In Approach 1, if multiple count buckets have the same remaining capacity, trying them all for the same customer is redundant.

That is why this line matters:

```java
Set<Integer> used = new HashSet<>();
```

It prevents exploring symmetric assignments multiple times.

This is one of the most important practical pruning tricks.

---

# 9. Why Greedy Fails

A tempting strategy is:

> always assign the biggest customer to the biggest frequency bucket first

This is not always correct.

Example intuition:

A large bucket may be the only one flexible enough to absorb multiple future combinations, while a slightly smaller bucket is sufficient for the current customer.

So local best-fit does not guarantee global feasibility.

That is why we need backtracking or subset DP.

---

# 10. Small Worked Example

Take:

```text
nums = [1,1,2,2]
quantity = [2,2]
```

Frequency counts:

```text
[2,2]
```

Customer demands:

```text
[2,2]
```

Using subset-sum DP:

- subsets of customers:
  - `01` sum = 2
  - `10` sum = 2
  - `11` sum = 4

First bucket `2` can satisfy `01` or `10`.

Suppose it satisfies `01`.
Then second bucket `2` satisfies `10`.

Full mask reached → answer is true.

---

# 11. Correctness Sketch for Subset DP

For each distinct value in `nums`, its count bucket can only be used to serve customers with that exact value.

Because customers only care that all numbers they receive are equal, any count bucket of capacity `c` can satisfy any subset of currently unsatisfied customers whose total demand is at most `c`.

Thus the problem becomes assigning customer subsets to count buckets.

The DP state `mask` captures exactly which customers are already satisfied.
The recursion or iterative transition explores every valid subset assignment for each bucket.
Hence, if a feasible allocation exists, one DP path reaches the full customer mask.
If not, no such path exists.

Therefore the algorithm is correct.

---

# 12. Comparison of Approaches

| Approach            | Main Idea                                    |                                Time |        Space | Notes                       |
| ------------------- | -------------------------------------------- | ----------------------------------: | -----------: | --------------------------- |
| Backtracking        | assign customers one by one to count buckets | exponential, practical with pruning |       `O(m)` | easy to write               |
| Top-down subset DP  | assign subsets of customers to each bucket   |                        `O(u * 4^m)` | `O(u * 2^m)` | strongest standard approach |
| Bottom-up subset DP | iterative version of subset assignment       |                        `O(u * 4^m)` |     `O(2^m)` | avoids recursion            |

Where:

- `u` = number of unique values in `nums` (`<= 50`)
- `m` = number of customers (`<= 10`)

---

# 13. Which Approach Should You Prefer?

## In interviews

Use **Approach 2** if you want the most robust solution.

Why?

- it uses the small-customer-count constraint perfectly
- it is systematic
- it handles all cases cleanly

## For quick implementation

Use **Approach 1** if you are comfortable with pruning-heavy backtracking.

It is shorter and often passes easily because `m <= 10`.

---

# 14. Final Interview Summary

This problem looks large because `nums.length` can be `100000`, but that is misleading.

The real constraints that matter are:

- at most `50` unique values
- at most `10` customers

So:

1. compress `nums` into frequencies
2. treat each frequency as a resource bucket
3. decide which customers each bucket can satisfy

The most standard optimal strategy is:

- precompute subset sums of customer demands
- use DP over customer masks
- for each count bucket, try all submasks that fit

That yields an efficient solution with manageable complexity because:

```text
2^10 = 1024
```

which is tiny.

---

# 15. Recommended Java Solution

```java
import java.util.*;

class Solution {
    private int[] counts;
    private int[] quantity;
    private int[] subsetSum;
    private Boolean[][] memo;
    private int fullMask;

    public boolean canDistribute(int[] nums, int[] quantity) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }

        counts = new int[freq.size()];
        int idx = 0;
        for (int c : freq.values()) {
            counts[idx++] = c;
        }

        Arrays.sort(counts);
        reverse(counts);

        this.quantity = quantity.clone();
        Arrays.sort(this.quantity);
        reverse(this.quantity);

        int m = this.quantity.length;
        fullMask = (1 << m) - 1;

        subsetSum = new int[1 << m];
        for (int mask = 1; mask <= fullMask; mask++) {
            int lsb = mask & -mask;
            int bit = Integer.numberOfTrailingZeros(lsb);
            subsetSum[mask] = subsetSum[mask ^ lsb] + this.quantity[bit];
        }

        memo = new Boolean[counts.length + 1][1 << m];
        return dfs(0, 0);
    }

    private boolean dfs(int i, int mask) {
        if (mask == fullMask) {
            return true;
        }
        if (i == counts.length) {
            return false;
        }
        if (memo[i][mask] != null) {
            return memo[i][mask];
        }

        if (dfs(i + 1, mask)) {
            return memo[i][mask] = true;
        }

        int remaining = fullMask ^ mask;
        for (int sub = remaining; sub > 0; sub = (sub - 1) & remaining) {
            if (subsetSum[sub] <= counts[i]) {
                if (dfs(i + 1, mask | sub)) {
                    return memo[i][mask] = true;
                }
            }
        }

        return memo[i][mask] = false;
    }

    private void reverse(int[] arr) {
        for (int l = 0, r = arr.length - 1; l < r; l++, r--) {
            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;
        }
    }
}
```
