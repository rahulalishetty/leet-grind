# Number of k-Free Subsets

## Problem Restatement

You are given:

- an integer array `nums`
- all elements in `nums` are **distinct**
- an integer `k`

A subset is called **k-Free** if it contains **no two elements** whose absolute difference is exactly `k`.

That means for any two chosen numbers `a` and `b`, the following must **not** happen:

```text
|a - b| = k
```

You must return the total number of k-Free subsets of `nums`.

The empty subset is also valid.

---

## First Intuition

A brute-force approach would try all subsets.

If `nums` has length `n`, then there are:

```text
2^n
```

subsets.

For each subset, you could check whether it contains a bad pair whose difference is exactly `k`.

That is correct in principle, but completely impractical for large `n`.

So the real question is:

> How do we exploit the structure of the condition `|a - b| = k`?

That condition is very restrictive, and that is the opening for an efficient solution.

---

## Core Observation 1: Conflicts Only Happen Within the Same Modulo Class

If two numbers differ by exactly `k`, then they must have the same remainder modulo `k`.

Why?

Suppose:

```text
a - b = k
```

Then:

```text
a ≡ b (mod k)
```

Similarly, if:

```text
b - a = k
```

then again they have the same remainder modulo `k`.

So if two numbers belong to **different remainder classes modulo `k`**, they can **never** differ by exactly `k`.

This is a major simplification.

It means:

- numbers from different modulo groups are completely independent
- all conflicts are local to a single remainder group

So we can split `nums` into groups by:

```text
num % k
```

and solve each group separately.

Then multiply the answers.

---

## Why Multiplication Is Valid

Suppose one modulo group contributes `A` valid choices and another independent group contributes `B` valid choices.

Since these groups do not interact, every valid choice in the first group can be paired with every valid choice in the second group.

So the total number of choices is:

```text
A * B
```

This is the standard product rule from combinatorics.

Thus:

> total answer = product of answers for all remainder groups

---

## Core Observation 2: Inside One Group, the Conflict Structure Becomes a Set of Chains

Now take one remainder group and sort it.

Example:

```text
group = [2, 5, 8, 14, 17], k = 3
```

Notice:

- `5 - 2 = 3` -> conflict
- `8 - 5 = 3` -> conflict
- `14 - 8 = 6` -> no direct conflict
- `17 - 14 = 3` -> conflict

So this group splits into:

```text
[2, 5, 8]    and    [14, 17]
```

Each such part is a **chain** where consecutive elements differ by exactly `k`.

Inside a chain:

```text
x, x+k, x+2k, ...
```

the only forbidden pairs are adjacent elements in that chain.

Why only adjacent ones?

Because:

- `x` conflicts with `x+k`
- `x+k` conflicts with `x+2k`
- but `x` does **not** conflict with `x+2k`, since their difference is `2k`, not `k`

So each chain behaves like a simple path graph:

```text
x -- x+k -- x+2k -- ...
```

And now the problem becomes:

> Count how many ways to choose nodes from a path so that no two adjacent nodes are chosen.

That is the classic **independent set on a path** problem.

---

## Graph Interpretation

This problem becomes very clean if you visualize it as a graph:

- each number is a node
- draw an edge between two nodes if their difference is exactly `k`

A k-Free subset is exactly:

> an independent set in this graph

Because no two chosen elements may share an edge.

Now because all numbers are distinct:

- within each modulo group, sorted numbers only connect to neighbors spaced by exactly `k`
- so every connected component is a path

Therefore, the graph is a disjoint union of paths.

Counting k-Free subsets becomes:

1. count independent sets on each path
2. multiply across all paths

This is the full structural reason the DP works.

---

## Counting Valid Choices on One Chain

Suppose a chain has length `L`.

Example:

```text
[a1, a2, a3, ..., aL]
```

where each adjacent pair differs by exactly `k`.

Since adjacent elements cannot both be chosen, this is exactly the classic recurrence:

Let:

- `dp0` = number of valid ways up to the current position where the current element is **not taken**
- `dp1` = number of valid ways up to the current position where the current element **is taken**

Then when processing the next element:

### If current is not taken

Then the previous one may be taken or not.

So:

```text
new_dp0 = dp0 + dp1
```

### If current is taken

Then the previous one must not be taken.

So:

```text
new_dp1 = dp0
```

At the end, total ways for that chain are:

```text
dp0 + dp1
```

---

## Small Values for a Chain

This helps build intuition.

### Chain length 1

Elements: `[a]`

Valid subsets:

- `{}`
- `{a}`

Count:

```text
2
```

---

### Chain length 2

Elements: `[a, b]`, where `|a-b| = k`

Valid subsets:

- `{}`
- `{a}`
- `{b}`

Invalid:

- `{a, b}`

Count:

```text
3
```

---

### Chain length 3

Elements: `[a, b, c]`

Valid subsets:

- `{}`
- `{a}`
- `{b}`
- `{c}`
- `{a, c}`

Count:

```text
5
```

---

### Chain length 4

Count becomes:

```text
8
```

These counts follow a Fibonacci-like pattern.

---

## Alternative DP View on Sorted Group

Instead of explicitly splitting into chains first, you can process the sorted group from left to right.

Let `dp[i]` = number of valid subsets using the first `i+1` elements of the sorted group.

Then for the current element:

### If it conflicts with the previous one

That means:

```text
group[i] - group[i - 1] == k
```

Then:

- either do not take current -> `dp[i-1]`
- or take current, which forces previous not taken -> `dp[i-2]`

So:

```text
dp[i] = dp[i-1] + dp[i-2]
```

### If it does not conflict with previous one

That means current starts a new disconnected component relative to the previous element.

Then for every previous valid subset, you may:

- exclude current
- include current

So the count doubles:

```text
dp[i] = dp[i-1] * 2
```

This leads to a very compact implementation.

---

## Approach Used Here

We will use the sorted-group DP because it is elegant and directly captures both cases:

- adjacent conflict -> Fibonacci recurrence
- no conflict -> doubling recurrence

---

## Full Java Code

```java
import java.util.*;

class Solution {
    public long countTheNumOfKFreeSubsets(int[] nums, int k) {
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int num : nums) {
            groups.computeIfAbsent(num % k, z -> new ArrayList<>()).add(num);
        }

        long ans = 1L;

        for (List<Integer> group : groups.values()) {
            Collections.sort(group);

            int n = group.size();
            long prev2 = 1; // dp[i-2]
            long prev1 = 1; // dp[i-1]

            for (int i = 0; i < n; i++) {
                long cur;
                if (i > 0 && group.get(i) - group.get(i - 1) == k) {
                    cur = prev1 + prev2;
                } else {
                    cur = prev1 * 2;
                }
                prev2 = prev1;
                prev1 = cur;
            }

            ans *= prev1;
        }

        return ans;
    }
}
```

---

## Detailed Code Walkthrough

## 1. Group numbers by remainder modulo `k`

```java
Map<Integer, List<Integer>> groups = new HashMap<>();
for (int num : nums) {
    groups.computeIfAbsent(num % k, z -> new ArrayList<>()).add(num);
}
```

This creates a map:

```text
remainder -> list of numbers with that remainder
```

Why is this correct?

Because only numbers in the same modulo class can differ by exactly `k`.

So numbers from different groups never interact.

---

## 2. Initialize the global answer

```java
long ans = 1L;
```

We will multiply the number of valid choices from each independent group.

Start with `1` because multiplication identity is `1`.

---

## 3. Process each group independently

```java
for (List<Integer> group : groups.values()) {
    Collections.sort(group);
```

We sort the group.

Sorting is important because after sorting, if two numbers differ by exactly `k`, they will appear as consecutive members within a chain.

That allows the simple left-to-right DP.

---

## 4. DP state initialization

```java
int n = group.size();
long prev2 = 1; // dp[i-2]
long prev1 = 1; // dp[i-1]
```

This may look slightly subtle.

Interpretation:

Before processing any element:

- there is exactly `1` valid subset: the empty set

So both base references begin at `1`.

This setup makes the recurrence uniform.

---

## 5. Process the sorted group

```java
for (int i = 0; i < n; i++) {
    long cur;
    if (i > 0 && group.get(i) - group.get(i - 1) == k) {
        cur = prev1 + prev2;
    } else {
        cur = prev1 * 2;
    }
    prev2 = prev1;
    prev1 = cur;
}
```

This is the heart of the solution.

### Case A: current element conflicts with previous one

```java
group.get(i) - group.get(i - 1) == k
```

Then current and previous cannot both be chosen.

So valid subsets are:

- subsets that exclude current -> `prev1`
- subsets that include current -> `prev2`

Hence:

```java
cur = prev1 + prev2;
```

That is exactly the independent-set-on-a-path recurrence.

---

### Case B: current element does not conflict with previous one

Then current belongs to a different connected component from what was just before it.

So for every previous valid subset, we have two choices:

- do not take current
- take current

Hence the number of subsets doubles:

```java
cur = prev1 * 2;
```

---

## 6. Roll the DP variables

```java
prev2 = prev1;
prev1 = cur;
```

This is standard space optimization.

Instead of storing a full DP array, we only keep the last two values because the recurrence depends only on them.

---

## 7. Multiply this group's contribution into the global answer

```java
ans *= prev1;
```

After finishing one group, `prev1` is the number of k-Free subsets that can be formed using that group.

Since groups are independent, multiply into the global answer.

---

## 8. Return the result

```java
return ans;
```

That is the final count of all k-Free subsets.

---

## Worked Example 1

Take:

```text
nums = [2, 5, 8], k = 3
```

### Step 1: Group by modulo 3

All numbers have remainder `2` modulo `3`, so one group:

```text
[2, 5, 8]
```

### Step 2: Sort

Already sorted.

### Step 3: Process with DP

Initialize:

```text
prev2 = 1
prev1 = 1
```

#### i = 0, current = 2

No previous element, so no conflict case:

```text
cur = prev1 * 2 = 2
```

Update:

```text
prev2 = 1
prev1 = 2
```

#### i = 1, current = 5

Difference from previous:

```text
5 - 2 = 3 = k
```

Conflict exists:

```text
cur = prev1 + prev2 = 2 + 1 = 3
```

Update:

```text
prev2 = 2
prev1 = 3
```

#### i = 2, current = 8

Difference from previous:

```text
8 - 5 = 3 = k
```

Conflict exists:

```text
cur = prev1 + prev2 = 3 + 2 = 5
```

Final:

```text
prev1 = 5
```

Answer:

```text
5
```

Valid subsets are:

- `{}`
- `{2}`
- `{5}`
- `{8}`
- `{2, 8}`

Correct.

---

## Worked Example 2

Take:

```text
nums = [1, 4, 7, 10, 2], k = 3
```

### Group by modulo 3

- remainder 1: `[1, 4, 7, 10]`
- remainder 2: `[2]`

Now solve each group separately.

---

### Group `[1, 4, 7, 10]`

This is a chain of length 4.

Independent-set count on a path of length 4 is:

```text
8
```

---

### Group `[2]`

Single element, count is:

```text
2
```

---

### Multiply

```text
8 * 2 = 16
```

So the final answer is:

```text
16
```

---

## Why Sorting Is Enough

Inside one modulo class, suppose the sorted values are:

```text
a1 < a2 < a3 < ... < at
```

If `ai` conflicts with some later `aj`, then:

```text
aj - ai = k
```

Because the numbers are sorted and distinct, there cannot be another value strictly between them that belongs to the same arithmetic chain spacing unless it changes the gap structure.

So checking consecutive differences is enough to identify the path components.

This is why the sorted order naturally reveals the conflict graph.

---

## Complexity Analysis

Let `n = nums.length`.

### Time Complexity

#### Grouping

We process each number once:

```text
O(n)
```

#### Sorting

All numbers are distributed across groups, but the total size across all groups is still `n`.

Sorting all groups together costs:

```text
O(n log n)
```

in total.

#### DP scan

We scan every element once after sorting:

```text
O(n)
```

### Total Time Complexity

```text
O(n log n)
```

---

### Space Complexity

We store:

- the modulo groups
- the numbers inside them

That requires:

```text
O(n)
```

The DP itself uses only constant extra space per group.

So total space complexity is:

```text
O(n)
```

---

## Why This Solution Is Efficient

The problem looks like a subset-counting problem, which often suggests exponential complexity.

But the special form of the constraint:

```text
|a - b| = k
```

creates a very sparse interaction structure.

That structure lets us turn the problem into:

- independent remainder groups
- each group becomes disjoint chains
- each chain is solved by a tiny DP

That is the big conceptual reduction.

---

## Final Takeaway

The clean way to think about this problem is:

1. Two numbers can conflict only if they have the same remainder modulo `k`
2. So split numbers into modulo groups
3. Sort each group
4. Inside a group, consecutive numbers differing by `k` form path-like conflict chains
5. Count independent sets on those chains using Fibonacci-style DP
6. Multiply the counts across independent groups

This yields an elegant and efficient solution:

- **Time Complexity:** `O(n log n)`
- **Space Complexity:** `O(n)`
