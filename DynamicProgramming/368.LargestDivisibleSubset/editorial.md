# 368. Largest Divisible Subset

## Problem Restatement

Given a set of **distinct positive integers** `nums`, return the **largest subset** such that for every pair of elements `(a, b)` in the subset:

- `a % b == 0`, or
- `b % a == 0`

If multiple answers exist, returning **any one** is acceptable.

---

## Core Observation

This problem looks like a subset problem, but it becomes much more structured after **sorting**.

If we sort the array in ascending order:

```text
a1 <= a2 <= a3 <= ...
```

then for a valid divisible chain, we only need to ensure:

```text
next % prev == 0
```

Why is that enough?

Because divisibility is **transitive** in this sorted setting:

- if `b % a == 0`
- and `c % b == 0`

then `c % a == 0`

So instead of checking every pair explicitly, we can build a chain where each element is divisible by the previous one.

That reduces the problem to something very close to **Longest Increasing Subsequence (LIS)** style dynamic programming.

---

# Approach 1: Brute Force Backtracking (Generate All Subsets)

## Idea

Try all possible subsets, and for each subset, check whether every pair satisfies the divisibility condition.

This is the most direct interpretation of the problem.

### Steps

1. Generate all subsets using recursion.
2. For each subset:
   - check all pairs
   - if valid, compare its size with the best answer found so far

---

## Why it works

It is exhaustive. Since every possible subset is explored, the largest valid one will definitely be found.

---

## Time Complexity

There are `2^n` subsets.

For each subset of size `k`, checking all pairs costs `O(k^2)`.

In the worst case:

```text
Time = O(2^n * n^2)
Space = O(n) recursion stack + O(n) current subset
```

This is completely infeasible for `n = 1000`.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer> best = new ArrayList<>();

    public List<Integer> largestDivisibleSubset(int[] nums) {
        backtrack(nums, 0, new ArrayList<>());
        return best;
    }

    private void backtrack(int[] nums, int index, List<Integer> current) {
        if (index == nums.length) {
            if (isValid(current) && current.size() > best.size()) {
                best = new ArrayList<>(current);
            }
            return;
        }

        // Exclude
        backtrack(nums, index + 1, current);

        // Include
        current.add(nums[index]);
        backtrack(nums, index + 1, current);
        current.remove(current.size() - 1);
    }

    private boolean isValid(List<Integer> subset) {
        for (int i = 0; i < subset.size(); i++) {
            for (int j = i + 1; j < subset.size(); j++) {
                int a = subset.get(i);
                int b = subset.get(j);
                if (a % b != 0 && b % a != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
```

---

## Verdict

Useful only for understanding the raw search space.

---

# Approach 2: Backtracking with Incremental Validity Check

## Idea

Instead of generating a full subset and validating it at the end, validate while building it.

When trying to add a new number to the current subset, make sure it is compatible with all numbers already inside the subset.

This prunes some invalid branches early.

---

## Why it helps

If a partial subset is already invalid, there is no need to explore extensions of it.

---

## Limitation

Even with pruning, the search is still exponential in the worst case.

For large `n`, this still times out badly.

---

## Time Complexity

Worst case is still exponential:

```text
Time = O(2^n * n)
Space = O(n)
```

The exact runtime depends on how much pruning happens.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer> best = new ArrayList<>();

    public List<Integer> largestDivisibleSubset(int[] nums) {
        backtrack(nums, 0, new ArrayList<>());
        return best;
    }

    private void backtrack(int[] nums, int index, List<Integer> current) {
        if (current.size() > best.size()) {
            best = new ArrayList<>(current);
        }

        for (int i = index; i < nums.length; i++) {
            if (canAdd(current, nums[i])) {
                current.add(nums[i]);
                backtrack(nums, i + 1, current);
                current.remove(current.size() - 1);
            }
        }
    }

    private boolean canAdd(List<Integer> current, int x) {
        for (int num : current) {
            if (num % x != 0 && x % num != 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Verdict

Better than pure brute force conceptually, but still not a practical solution for the given constraints.

---

# Approach 3: Dynamic Programming After Sorting (Classic Optimal Solution)

## High-Level Insight

Sort the numbers first.

Once sorted, if `nums[i] % nums[j] == 0` for `j < i`, then `nums[i]` can extend the divisible subset ending at `nums[j]`.

This becomes:

> "Find the longest divisible chain ending at every index."

This is exactly like LIS, except the transition condition is:

```text
nums[i] % nums[j] == 0
```

instead of:

```text
nums[i] > nums[j]
```

---

## DP Definition

Let:

```text
dp[i] = length of the largest divisible subset ending at index i
parent[i] = previous index used to build that subset
```

### Transition

For every `i`, check all `j < i`:

- if `nums[i] % nums[j] == 0`
- and `dp[j] + 1 > dp[i]`

then:

```text
dp[i] = dp[j] + 1
parent[i] = j
```

At the end, find the index with maximum `dp[i]` and reconstruct the subset using `parent`.

---

## Why Sorting Is Valid

Suppose we have a valid subset. Its elements can be arranged in nondecreasing order.

In that order, for every earlier element `a` and later element `b`, the subset condition implies `b % a == 0`.

So sorting does not destroy a valid answer. It gives us a convenient order to build chains.

---

## Time and Space Complexity

We compare every pair `(j, i)` with `j < i`.

```text
Time = O(n^2)
Space = O(n)
```

This is the standard accepted solution for `n <= 1000`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> largestDivisibleSubset(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);

        int[] dp = new int[n];
        int[] parent = new int[n];

        Arrays.fill(dp, 1);
        Arrays.fill(parent, -1);

        int maxLen = 1;
        int maxIndex = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] % nums[j] == 0 && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }

            if (dp[i] > maxLen) {
                maxLen = dp[i];
                maxIndex = i;
            }
        }

        List<Integer> result = new ArrayList<>();
        while (maxIndex != -1) {
            result.add(nums[maxIndex]);
            maxIndex = parent[maxIndex];
        }

        Collections.reverse(result);
        return result;
    }
}
```

---

## Walkthrough Example

### Input

```text
nums = [1, 2, 4, 8]
```

After sorting:

```text
[1, 2, 4, 8]
```

### Step-by-step

- `i = 0`, value = 1
  `dp[0] = 1`

- `i = 1`, value = 2
  `2 % 1 == 0`
  so `dp[1] = dp[0] + 1 = 2`

- `i = 2`, value = 4
  `4 % 1 == 0`, candidate length = 2
  `4 % 2 == 0`, candidate length = 3
  so `dp[2] = 3`

- `i = 3`, value = 8
  `8 % 1 == 0`, candidate length = 2
  `8 % 2 == 0`, candidate length = 3
  `8 % 4 == 0`, candidate length = 4
  so `dp[3] = 4`

Final chain reconstructed from parents:

```text
[1, 2, 4, 8]
```

---

## Why This Is the Best Practical Approach

- Handles `n = 1000` comfortably
- Simple to reason about
- Easy reconstruction
- Minimal extra memory

This is the solution you should remember for interviews and problem solving.

---

# Approach 4: Top-Down DP + Memoization

## Idea

We can define a recursive function:

```text
f(i) = largest divisible subset starting from index i
```

After sorting, try all `j > i` such that:

```text
nums[j] % nums[i] == 0
```

Then recursively compute the best extension.

Memoize the result for each index.

---

## Important Note

This is conceptually elegant, but returning full lists from recursion can create extra copying overhead.

It is usually less efficient and less clean than the bottom-up version, though still acceptable for this constraint if implemented carefully.

---

## Time Complexity

If memoized properly, each pair is considered at most once:

```text
Time = O(n^2 + reconstruction/copy costs)
Space = O(n) memo metadata + recursion stack
```

If full lists are copied repeatedly, practical overhead increases.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[] nums;
    private List<Integer>[] memo;

    public List<Integer> largestDivisibleSubset(int[] nums) {
        Arrays.sort(nums);
        this.nums = nums;
        this.memo = new ArrayList[nums.length];

        List<Integer> best = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            List<Integer> candidate = dfs(i);
            if (candidate.size() > best.size()) {
                best = candidate;
            }
        }

        return best;
    }

    private List<Integer> dfs(int i) {
        if (memo[i] != null) {
            return memo[i];
        }

        List<Integer> bestSuffix = new ArrayList<>();

        for (int j = i + 1; j < nums.length; j++) {
            if (nums[j] % nums[i] == 0) {
                List<Integer> candidate = dfs(j);
                if (candidate.size() > bestSuffix.size()) {
                    bestSuffix = candidate;
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        result.add(nums[i]);
        result.addAll(bestSuffix);

        memo[i] = result;
        return result;
    }
}
```

---

## Verdict

Good for conceptual understanding of state recursion.
In practice, the bottom-up DP is usually better.

---

# Approach 5: DP with HashMap by Value (Why It Is Usually Inferior Here)

## Idea

Since divisibility is about values, you may wonder whether we can build DP based on the numbers themselves using a map:

```text
dp[value] = best subset length ending at this value
```

This sounds attractive, but it does not actually simplify the problem much.

You still need to find which previous values divide the current value.

Without an efficient way to enumerate only relevant divisors present in the array, you end up scanning many numbers anyway.

---

## Difficulty

For each number `x`, you would need to find all `d` in the input such that:

```text
x % d == 0
```

That means:

- either scan all previous numbers => back to `O(n^2)`
- or enumerate divisors of `x`

But `nums[i]` can be as large as `2 * 10^9`, so divisor enumeration is not always attractive either.

---

## Complexity

Usually still:

```text
Time = O(n^2)
Space = O(n)
```

but with more complexity and no real benefit.

---

## Verdict

Possible, but not the cleanest formulation for this problem.

---

# Approach 6: Graph / DAG Longest Path Interpretation

## Insight

After sorting, construct a directed graph:

- each number is a node
- add edge `j -> i` if:
  - `j < i`
  - `nums[i] % nums[j] == 0`

Because edges only go from smaller index to larger index, the graph is a **DAG**.

Then the problem becomes:

> Find the longest path in this DAG.

That is exactly what the classic DP is doing.

---

## Why this view is useful

This helps you connect the problem to deeper DP patterns:

- LIS
- longest path in DAG
- parent reconstruction
- partial order problems

---

## Complexity

Building and solving this graph explicitly still costs:

```text
Time = O(n^2)
Space = O(n^2) if edges are stored explicitly
```

That is worse in space than the direct DP, so explicit graph construction is unnecessary.

---

## Verdict

Great mental model. Not the best implementation strategy.

---

# Can We Do Better Than O(n^2)?

For this problem's constraints, `O(n^2)` is already good enough.

Could there be a more optimized approach?

In theory, maybe with heavy number-theoretic tricks and value-based indexing for smaller ranges. But here values are as large as `2 * 10^9` and numbers are arbitrary.

That means there is no clean monotonic structure beyond pairwise divisibility after sorting.

So in practical algorithmic terms:

> `O(n^2)` DP is the right answer.

---

# Why the Pairwise Condition Collapses into a Chain

This is one of the most important insights.

The problem statement says:

> for every pair in the subset, one must divide the other

That sounds like a full pairwise condition.

But after sorting a valid subset:

```text
a1 <= a2 <= a3 <= ...
```

the condition forces:

```text
a2 % a1 == 0
a3 % a2 == 0
...
```

and because divisibility is transitive:

```text
a3 % a1 == 0
a4 % a2 == 0
...
```

So checking adjacent compatibility while building the chain is enough.

This is why LIS-style DP works.

---

# Comparison of Approaches

| Approach                  | Main Idea                                       |                         Time |              Space | Practical?          |
| ------------------------- | ----------------------------------------------- | ---------------------------: | -----------------: | ------------------- |
| Brute Force All Subsets   | Generate every subset, validate all pairs       |               `O(2^n * n^2)` |             `O(n)` | No                  |
| Backtracking with Pruning | Build subsets incrementally, prune invalid ones |       Exponential worst case |             `O(n)` | No                  |
| Bottom-Up DP + Parent     | Sort + longest divisible chain                  |                     `O(n^2)` |             `O(n)` | Yes, best           |
| Top-Down Memoization      | Recursive best chain from each index            | ~`O(n^2)` plus copy overhead | `O(n)` + recursion | Yes, but less clean |
| Explicit DAG Longest Path | Build divisibility graph after sorting          |                     `O(n^2)` |           `O(n^2)` | Usually unnecessary |
| HashMap by Value          | DP keyed by values                              |         Often still `O(n^2)` |             `O(n)` | Not ideal           |

---

# Recommended Interview Answer

If asked in an interview, the best progression is:

1. Brute force: all subsets, pairwise check
2. Observe that sorting helps
3. Realize valid subsets can be treated as divisible chains
4. Apply LIS-style DP
5. Use parent array to reconstruct the actual subset

That gives both strong reasoning and the optimal practical solution.

---

# Final Best Solution (Clean Version)

```java
import java.util.*;

class Solution {
    public List<Integer> largestDivisibleSubset(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);

        int[] dp = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dp, 1);
        Arrays.fill(parent, -1);

        int bestLen = 1;
        int bestEnd = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] % nums[j] == 0 && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }

            if (dp[i] > bestLen) {
                bestLen = dp[i];
                bestEnd = i;
            }
        }

        List<Integer> result = new ArrayList<>();
        while (bestEnd != -1) {
            result.add(nums[bestEnd]);
            bestEnd = parent[bestEnd];
        }

        Collections.reverse(result);
        return result;
    }
}
```

---

# Space and Time Complexity of the Best Solution

## Time Complexity

- Sorting: `O(n log n)`
- DP transitions: `O(n^2)`

Overall:

```text
O(n^2)
```

because `n^2` dominates `n log n`.

## Space Complexity

- `dp[]`: `O(n)`
- `parent[]`: `O(n)`
- output list: `O(n)`

Overall auxiliary space:

```text
O(n)
```

---

# Key Takeaways

1. This is a **subset** problem, but sorting turns it into a **chain** problem.
2. Divisibility gives a **partial order**.
3. After sorting, the problem behaves like **LIS**.
4. The standard solution is:
   - sort
   - DP for best chain length ending at each index
   - parent array for reconstruction
5. The optimal practical complexity is:

```text
Time: O(n^2)
Space: O(n)
```

---

# Extra Interview Notes

A few useful points to mention if pressed further:

- The subset condition is pairwise, but transitivity of divisibility allows chain-based DP.
- Sorting is safe because order is irrelevant in subsets.
- This is not a subsequence problem in the original array order.
- The DAG interpretation is a strong abstraction if the interviewer wants a more theoretical framing.
