# Minimum Number of Subarrays with GCD Greater Than 1

## Problem Restatement

You are given an array `nums` of positive integers.

You must split it into one or more **contiguous** subarrays such that:

- every element belongs to exactly one subarray,
- the **GCD** of every chosen subarray is **strictly greater than 1**.

Your task is to return the **minimum number of subarrays** needed.

If no such split is possible, the natural extension of the DP solution is to return `-1`.

---

## Core Intuition

This is a partition DP problem.

Whenever a problem says:

- split an array into contiguous parts,
- each part must satisfy some condition,
- minimize the number of parts,

there is a very standard way to think about it:

> Let the last subarray end at index `i`. Where could that subarray start?

If the last subarray is `nums[j..i]`, then:

- the prefix `nums[0..j-1]` must already be optimally split,
- and `gcd(nums[j..i])` must be greater than `1`.

So the recurrence is:

```text
dp[i + 1] = min(dp[j] + 1) for all j such that gcd(nums[j..i]) > 1
```

where:

- `dp[t]` means the minimum number of valid subarrays needed to split the first `t` elements, i.e. `nums[0..t-1]`.

This recurrence is correct, but a direct implementation is too slow.

---

## Why the Naive Solution is Too Slow

A brute-force DP would do this for each ending index `i`:

- try every possible start `j` from `i` down to `0`,
- compute `gcd(nums[j..i])`,
- if that gcd is `> 1`, update the answer.

That leads to roughly `O(n^2)` subarrays.

Even if you update GCD incrementally while moving `j` left, the total work is still quadratic in the worst case.

So the real question becomes:

> Can we avoid considering all starts `j` individually?

Yes. The crucial observation is that many different starting points produce the **same GCD**.

---

## Key Observation: Distinct GCDs Per Ending Index Are Few

Fix an ending index `i`.
Look at all subarrays ending at `i`:

- `nums[i..i]`
- `nums[i-1..i]`
- `nums[i-2..i]`
- ...
- `nums[0..i]`

Now examine their GCDs.

As the subarray expands leftward:

- the GCD can only stay the same or decrease,
- and every time it changes, it must change to a divisor of the previous value.

That means the sequence of distinct GCD values is short.
It does **not** blow up to `O(n)` in practice.

This is the classic optimization used in many “subarray GCD” DP problems.

So instead of storing every possible start index `j`, we store only:

- each distinct GCD value of a subarray ending at `i`, and
- the best DP cost associated with that GCD.

---

## State Compression Idea

For each index `i`, maintain a compressed set of states for all subarrays ending at `i`.

Each state is:

```text
(g, bestCost)
```

Meaning:

- there exists at least one subarray `nums[j..i]` whose GCD is `g`,
- among all such starts `j`, the minimum value of `dp[j]` is `bestCost`.

Why is `dp[j]` the right thing to store?

Because if `nums[j..i]` is chosen as the last subarray, then the total number of parts is:

```text
dp[j] + 1
```

So for a fixed GCD `g`, we only care about the **minimum** `dp[j]`, not all possible `j` values.

This is the compression that makes the solution efficient.

---

## Transition Logic

Suppose we already know all compressed states for subarrays ending at `i - 1`.
Call this structure `prev`.

Now we process `nums[i] = x`.

Every subarray ending at `i` is of one of two types:

### 1. A new subarray starts at `i`

That gives the subarray:

```text
[i..i]
```

Its GCD is simply:

```text
x
```

Its partition cost before starting it is:

```text
dp[i]
```

So this contributes the state:

```text
(x, dp[i])
```

---

### 2. Some old subarray ending at `i - 1` is extended by `x`

Suppose an old state in `prev` is:

```text
(g, cost)
```

This means there exists some subarray ending at `i - 1` whose GCD is `g`, and whose prefix cost is `cost`.

If we append `x = nums[i]`, the new GCD becomes:

```text
gcd(g, x)
```

The start index does not change, so the prefix cost is still the same:

```text
cost
```

So this contributes the state:

```text
(gcd(g, x), cost)
```

---

### 3. Merge equal GCD values

Different previous states may collapse into the same new GCD after extension.

When that happens, we keep only the smallest cost.

So for each resulting GCD value `ng`, we store:

```text
minimum cost among all ways to get ng
```

This deduplication is essential.

---

## How `dp[i+1]` Is Computed

Once we build all compressed states for subarrays ending at `i`, we inspect them.

Every state `(g, cost)` represents some candidate last segment ending at `i`.

If:

```text
g > 1
```

then that subarray is valid, and the total partition count is:

```text
cost + 1
```

So:

```text
dp[i + 1] = min(cost + 1 for all states with gcd > 1)
```

That is exactly the recurrence we wanted, but now computed efficiently using compressed GCD states.

---

## Why This DP Is Correct

We should be careful here. The compression is powerful, but only if it preserves all relevant information.

### What must be considered for the optimal answer at position `i`?

Any valid partition of `nums[0..i]` must end with some final subarray `nums[j..i]` such that:

- `gcd(nums[j..i]) > 1`, and
- the prefix `nums[0..j-1]` is split optimally using `dp[j]` parts.

So every optimal answer is of the form:

```text
dp[j] + 1
```

for some start `j`.

### What does the compressed state store?

For every possible GCD value of a subarray ending at `i`, it stores the **minimum** `dp[j]` among all starts `j` that produce that GCD.

That means:

- no candidate last subarray is lost,
- and among candidates with the same GCD, only the best prefix cost is retained.

Since the final validity check depends only on whether `g > 1`, and the total cost depends only on `dp[j] + 1`, keeping only the minimum `dp[j]` for each GCD is sufficient.

Therefore, taking the minimum over all stored states with `g > 1` gives exactly the optimal answer.

---

## Step-by-Step Example

Take:

```text
nums = [2, 6, 3, 4]
```

We will track:

- `dp`
- compressed GCD states ending at each index

Initialize:

```text
dp[0] = 0
```

because zero elements need zero subarrays.

All other `dp` values start as infinity.

---

### Index 0: value = 2

Possible subarrays ending at `0`:

- `[2]`, gcd = 2

Compressed states:

```text
{ 2 -> dp[0] = 0 }
```

Since `2 > 1`, this is a valid final segment.
So:

```text
dp[1] = 0 + 1 = 1
```

Interpretation:

- the first element alone forms one valid subarray.

---

### Index 1: value = 6

Start new subarray at index `1`:

- `[6]`, gcd = 6, cost = `dp[1] = 1`

Extend previous state:

- old gcd `2` becomes `gcd(2, 6) = 2`, cost stays `0`

Compressed states now:

```text
{ 6 -> 1, 2 -> 0 }
```

Both GCDs are `> 1`, so candidates are:

- choose `[6]` as the last segment: total = `1 + 1 = 2`
- choose `[2,6]` as the last segment: total = `0 + 1 = 1`

Hence:

```text
dp[2] = 1
```

Best split so far is the whole prefix `[2,6]` as one segment.

---

### Index 2: value = 3

Start new subarray:

- `[3]`, gcd = 3, cost = `dp[2] = 1`

Extend previous states:

- gcd `6` becomes `gcd(6,3)=3`, cost `1`
- gcd `2` becomes `gcd(2,3)=1`, cost `0`

After merging equal GCDs:

```text
{ 3 -> 1, 1 -> 0 }
```

Only gcd `3` is valid.
So:

```text
dp[3] = 1 + 1 = 2
```

Interpretation:

- best split is `[2,6] | [3]`

The segment `[2,6,3]` is not allowed because its GCD is `1`.

---

### Index 3: value = 4

Start new subarray:

- `[4]`, gcd = 4, cost = `dp[3] = 2`

Extend previous states:

- gcd `3` becomes `gcd(3,4)=1`, cost `1`
- gcd `1` becomes `gcd(1,4)=1`, cost `0`

Merge:

```text
{ 4 -> 2, 1 -> 0 }
```

Only gcd `4` is valid.
So:

```text
dp[4] = 2 + 1 = 3
```

Final answer:

```text
3
```

One optimal split is:

```text
[2,6] | [3] | [4]
```

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumSplits(int[] nums) {
        int n = nums.length;
        final int INF = 1_000_000_000;

        int[] dp = new int[n + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        // For subarrays ending at previous index:
        // gcd value -> minimum dp[start]
        Map<Integer, Integer> prev = new HashMap<>();

        for (int i = 0; i < n; i++) {
            Map<Integer, Integer> cur = new HashMap<>();

            // Case 1: start new subarray at i
            cur.put(nums[i], Math.min(cur.getOrDefault(nums[i], INF), dp[i]));

            // Case 2: extend all previous subarrays ending at i-1
            for (Map.Entry<Integer, Integer> entry : prev.entrySet()) {
                int g = entry.getKey();
                int cost = entry.getValue();

                int ng = gcd(g, nums[i]);
                cur.put(ng, Math.min(cur.getOrDefault(ng, INF), cost));
            }

            // Compute dp[i + 1]
            for (Map.Entry<Integer, Integer> entry : cur.entrySet()) {
                int g = entry.getKey();
                int cost = entry.getValue();

                if (g > 1) {
                    dp[i + 1] = Math.min(dp[i + 1], cost + 1);
                }
            }

            prev = cur;
        }

        return dp[n] >= INF ? -1 : dp[n];
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Line-by-Line Code Explanation

### DP array

```java
int[] dp = new int[n + 1];
Arrays.fill(dp, INF);
dp[0] = 0;
```

- `dp[k]` = minimum number of valid subarrays needed to partition the first `k` elements.
- `dp[0] = 0` is the base case.
- everything else starts unreachable.

---

### Previous compressed GCD states

```java
Map<Integer, Integer> prev = new HashMap<>();
```

`prev` stores compressed information for all subarrays ending at the previous index.

Meaning of an entry:

```text
prev[g] = minimum dp[start]
```

among all subarrays ending at `i-1` whose gcd is `g`.

---

### Processing each index

```java
for (int i = 0; i < n; i++) {
    Map<Integer, Integer> cur = new HashMap<>();
```

For every position `i`, we build a new compressed map `cur` for subarrays ending at `i`.

---

### Starting a new segment at `i`

```java
cur.put(nums[i], Math.min(cur.getOrDefault(nums[i], INF), dp[i]));
```

This corresponds to the subarray `[i..i]`.

- gcd is `nums[i]`
- prefix cost is `dp[i]`

We take `min(...)` because multiple paths could theoretically contribute the same gcd.

---

### Extending all old segments

```java
for (Map.Entry<Integer, Integer> entry : prev.entrySet()) {
    int g = entry.getKey();
    int cost = entry.getValue();

    int ng = gcd(g, nums[i]);
    cur.put(ng, Math.min(cur.getOrDefault(ng, INF), cost));
}
```

Every old subarray ending at `i-1` becomes a new subarray ending at `i` by appending `nums[i]`.

- old gcd = `g`
- new gcd = `gcd(g, nums[i])`
- starting index remains the same, so prefix cost remains `cost`

If many old states produce the same `ng`, only the smallest cost is kept.

---

### Computing the new DP value

```java
for (Map.Entry<Integer, Integer> entry : cur.entrySet()) {
    int g = entry.getKey();
    int cost = entry.getValue();

    if (g > 1) {
        dp[i + 1] = Math.min(dp[i + 1], cost + 1);
    }
}
```

Each entry of `cur` represents some possible last subarray ending at `i`.

If its gcd is valid (`> 1`), then choosing that as the last piece gives total:

```text
cost + 1
```

We minimize over all such choices.

---

### Move to next index

```java
prev = cur;
```

Now the current states become the previous states for the next iteration.

---

### Final return

```java
return dp[n] >= INF ? -1 : dp[n];
```

If the full array cannot be partitioned into valid pieces, return `-1`.
Otherwise return the minimum number of pieces.

---

## Complexity Analysis

Let:

- `n` = length of the array
- `A` = maximum value in `nums`
- `k` = number of distinct GCD values among subarrays ending at a fixed index

### Time Complexity

At each index:

- we iterate over all distinct GCD states from the previous step,
- for each one we compute one `gcd` and one map update.

So each index costs about:

```text
O(k * log A)
```

if we count Euclid’s algorithm as `O(log A)`.

Across all indices:

```text
O(n * k * log A)
```

Now the important practical fact is:

- `k` is usually small,
- and in standard analysis of this pattern, `k = O(log A)`.

So the total is often written as:

```text
O(n log^2 A)
```

In real implementations, this tends to be fast enough because the number of distinct GCDs per index stays small.

---

### Space Complexity

We use:

- `dp` array of size `O(n)`
- two GCD maps (`prev` and `cur`) whose size is `O(k)` each

So total space is:

```text
O(n + k)
```

or simply:

```text
O(n)
```

if the DP array is counted as dominant.

If we only cared about the answer and not intermediate values, we still cannot fully remove the DP array because `dp[i]` is needed when starting new segments at each position. So `O(n)` space is a natural form here.

---

## Why Greedy Does Not Work Reliably

A tempting idea is:

- keep extending the current subarray while gcd stays `> 1`,
- cut only when necessary.

This feels plausible, but greedy choices can block better future groupings.

The issue is that local decisions about where to cut depend on future values.
A long segment that is valid now may force extra cuts later, while a shorter segment now may allow a larger valid segment later.

That is exactly why DP is needed.

The problem has an optimal-substructure flavor, but not a simple local-choice property.

---

## Alternative View of the Same DP

You can also think of the compressed states this way:

For each position `i`, we are grouping all candidate starts `j` by the value of:

```text
gcd(nums[j..i])
```

Inside each group, only the smallest `dp[j]` matters.

So this algorithm is really:

- enumerate all possible last subarrays ending at `i`,
- but compress equivalent ones by GCD.

That is the clean conceptual picture.

---

## Edge Cases

### 1. Single element array

If `nums = [x]`:

- answer is `1` if `x > 1`
- answer is `-1` if `x = 1`

because the only subarray is the whole array itself.

---

### 2. Presence of `1`

Since all numbers are positive, `1` is especially restrictive.

- any subarray containing `1` can only have gcd `> 1` if all elements also share some common factor with `1`
- but `gcd(1, anything) = 1`

So any segment containing `1` is invalid.
That means:

- if `nums` contains `1`, then that element must stand alone,
- but a single-element subarray `[1]` also has gcd `1`, which is invalid.

Therefore, if any element is `1`, the answer is immediately impossible.

The DP naturally handles this and returns `-1`.

---

### 3. Entire array already has gcd > 1

Then the answer is simply `1`.

The DP will discover that the whole prefix can be one valid segment.

---

### 4. No adjacent grouping works

Example:

```text
[2, 3, 5, 7]
```

Each prime is individually valid because its gcd with itself is the number itself.
So the answer is `4`, not `-1`.

This is an easy place to make a conceptual mistake:

- subarray GCD must be `> 1`
- a one-element subarray `[p]` has gcd `p`
- so any element greater than `1` can always stand alone

Hence impossibility only occurs if some element equals `1`.

---

## Important Simplification

Because every element greater than `1` can form a valid single-element subarray, the array is always splittable unless it contains `1`.

So the real problem is not feasibility in most cases.
The real challenge is:

> how many adjacent elements can be grouped together while keeping GCD > 1, so that the number of segments is minimized?

That framing often makes the DP easier to understand.

---

## Final Takeaway

The solution combines two ideas:

1. **Partition DP**
   - `dp[i]` = minimum number of valid subarrays for the prefix

2. **Subarray-GCD compression**
   - instead of considering every start index individually,
   - group subarrays ending at the same index by their GCD,
   - and keep only the best prefix cost for each GCD

This turns an apparently quadratic partition problem into an efficient solution that runs in about:

```text
O(n log^2 A)
```

with:

```text
O(n)
```

space.

It is a very useful pattern to remember because the same compression trick appears in several advanced DP problems involving:

- subarray GCD,
- subarray AND/OR,
- distinct monotonic value transitions.
