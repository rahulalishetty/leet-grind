# 1723. Find Minimum Time to Finish All Jobs — Exhaustive Java Notes

## Problem Statement

You are given an array:

```text
jobs[i]
```

where `jobs[i]` is the time needed to complete the `i-th` job.

You also have `k` workers.

Rules:

- every job must be assigned to exactly one worker,
- a worker may receive multiple jobs,
- the workload of a worker is the sum of their assigned jobs.

Your goal is to minimize:

```text
the maximum workload among all workers
```

Return the minimum possible value of that maximum workload.

---

## Example 1

```text
Input:
jobs = [3,2,3], k = 3

Output:
3
```

Explanation:

Assign one job per worker:

- worker 1 → 3
- worker 2 → 2
- worker 3 → 3

Maximum load = `3`.

---

## Example 2

```text
Input:
jobs = [1,2,4,7,8], k = 2

Output:
11
```

One optimal assignment:

- worker 1 → [1,2,8] = 11
- worker 2 → [4,7] = 11

So the minimum possible maximum workload is `11`.

---

## Constraints

```text
1 <= k <= jobs.length <= 12
1 <= jobs[i] <= 10^7
```

The crucial constraint is:

```text
jobs.length <= 12
```

That strongly suggests:

- backtracking with pruning,
- subset DP,
- binary search + feasibility DFS.

---

# 1. Core Insight

This is a classic **minimum makespan scheduling** problem on small `n`.

If `n` were large, the problem is hard in general.
But here:

```text
n <= 12
```

so exponential approaches are feasible.

There are three especially useful directions:

1. **Backtracking with branch-and-bound**
2. **Binary search on answer + feasibility checking**
3. **Bitmask DP / subset partition DP**

All three are valid and worth understanding.

---

# 2. What Are We Optimizing?

We want to assign all jobs to workers such that the largest worker sum is as small as possible.

Equivalently:

> Find the smallest value `X` such that all jobs can be assigned and every worker gets total load at most `X`.

This phrasing directly leads to binary search.

---

# 3. Very Important Preprocessing: Sort Jobs Descending

For almost every backtracking-based solution, the first strong optimization is:

```java
Arrays.sort(jobs);
reverse(jobs);
```

Why?

Because large jobs are hardest to place.
If a large job causes failure, we learn that early and prune many branches.

Example:

```text
jobs = [8,7,4,2,1]
```

is much better to process than:

```text
[1,2,4,7,8]
```

for DFS placement.

This is one of the biggest practical optimizations.

---

# 4. Approach 1 — Pure Backtracking with Pruning

## Main Idea

Assign jobs one by one to workers.

Let:

```text
loads[i]
```

be the current total assigned to worker `i`.

At each step, place the current job into one worker, recurse, then backtrack.

Track the best answer found so far.

---

## Key Pruning Rules

### Pruning 1: Current max already worse than best

If after placing jobs, the current maximum load is already `>= best`, there is no point continuing.

### Pruning 2: Symmetric workers

If two workers currently have the same load, assigning the next job to either produces equivalent states.

So we should skip duplicate worker-load states.

### Pruning 3: Empty bucket symmetry

If you try putting the current job into an empty worker and it fails, trying another empty worker is redundant.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int best;

    public int minimumTimeRequired(int[] jobs, int k) {
        Arrays.sort(jobs);
        reverse(jobs);

        best = Arrays.stream(jobs).sum();
        int[] loads = new int[k];
        dfs(jobs, 0, loads, 0);
        return best;
    }

    private void dfs(int[] jobs, int idx, int[] loads, int currentMax) {
        if (currentMax >= best) {
            return;
        }

        if (idx == jobs.length) {
            best = Math.min(best, currentMax);
            return;
        }

        int job = jobs[idx];
        Set<Integer> seen = new HashSet<>();

        for (int i = 0; i < loads.length; i++) {
            if (seen.contains(loads[i])) {
                continue;
            }
            seen.add(loads[i]);

            loads[i] += job;
            dfs(jobs, idx + 1, loads, Math.max(currentMax, loads[i]));
            loads[i] -= job;

            if (loads[i] == 0) {
                break;
            }
        }
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

We explore all possible assignments of jobs to workers, but pruning removes huge portions of symmetric or hopeless search.

Since `n <= 12`, this is extremely effective in practice.

---

## Complexity

Worst-case is exponential and hard to express tightly, since it depends on pruning.

A rough loose bound is:

```text
O(k^n)
```

because each of `n` jobs could be assigned to one of `k` workers.

But in practice, sorting and symmetry pruning make it much faster.

Space:

```text
O(k + n)
```

for worker loads and recursion stack.

---

# 5. Approach 2 — Binary Search on Answer + DFS Feasibility

## Main Idea

Instead of directly minimizing the maximum load, ask:

> Can we assign all jobs so that every worker’s load is at most `limit`?

If we can check that efficiently, then the final answer can be found with binary search.

---

## Binary Search Range

Lower bound:

```text
max(jobs)
```

because some worker must do the largest job.

Upper bound:

```text
sum(jobs)
```

because one worker could do everything.

So we binary search in:

```text
[max(jobs), sum(jobs)]
```

---

## Feasibility DFS

To test a candidate `limit`:

- assign jobs in descending order,
- place each job into a worker only if the resulting load stays `<= limit`,
- prune symmetric worker states.

If all jobs can be placed, then `limit` is feasible.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumTimeRequired(int[] jobs, int k) {
        Arrays.sort(jobs);
        reverse(jobs);

        int left = jobs[0];
        int right = 0;
        for (int x : jobs) {
            right += x;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;
            int[] loads = new int[k];
            if (canAssign(jobs, 0, loads, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canAssign(int[] jobs, int idx, int[] loads, int limit) {
        if (idx == jobs.length) {
            return true;
        }

        int job = jobs[idx];
        Set<Integer> seen = new HashSet<>();

        for (int i = 0; i < loads.length; i++) {
            if (loads[i] + job > limit) {
                continue;
            }
            if (seen.contains(loads[i])) {
                continue;
            }
            seen.add(loads[i]);

            loads[i] += job;
            if (canAssign(jobs, idx + 1, loads, limit)) {
                return true;
            }
            loads[i] -= job;

            if (loads[i] == 0) {
                break;
            }
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

## Why Binary Search Works

If a limit `X` is feasible, then any larger limit is also feasible.

So feasibility is monotonic:

```text
false false false ... true true true
```

That is exactly the condition needed for binary search.

---

## Complexity

Binary search performs:

```text
O(log(sum(jobs)))
```

checks.

Each check is an exponential DFS with strong pruning.

So total is roughly:

```text
O(log(sum(jobs)) * feasibilityDFS)
```

This is usually one of the best practical solutions.

---

# 6. Approach 3 — Bitmask DP over Job Subsets

## Main Idea

Because `n <= 12`, we can use subsets of jobs as DP states.

A common subset DP technique is:

- precompute `sum[mask]` = total time of jobs in subset `mask`
- let `dp[mask]` represent the minimum possible maximum workload to finish exactly the jobs in `mask`

A more refined DP is based on workers:

```text
dp[w][mask] = minimum possible maximum load using w workers to cover jobs in mask
```

But that becomes heavier.

A more elegant formulation uses:

> partition all jobs into at most `k` subsets minimizing the maximum subset sum

This can be solved with DP where each worker takes one subset.

---

## Precompute Subset Sums

For every subset mask:

```text
sum[mask] = total processing time of those jobs
```

This takes:

```text
O(n * 2^n)
```

---

## DP Definition

Let:

```text
dp[mask] = minimum workers needed if each worker load must stay <= limit
```

This form is best paired with binary search.

But since the prompt asks for multiple approaches, we will also show the direct subset partition DP:

### Direct DP by Number of Workers

Let:

```text
dp[w][mask] = minimum possible maximum load when assigning jobs in mask using exactly w workers
```

Transition:

Choose a subset `sub` of `mask` for the last worker:

```text
dp[w][mask] = min over sub ⊆ mask of max(dp[w-1][mask ^ sub], sum[sub])
```

This is correct but can be expensive.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumTimeRequired(int[] jobs, int k) {
        int n = jobs.length;
        int totalMasks = 1 << n;

        int[] subsetSum = new int[totalMasks];
        for (int mask = 1; mask < totalMasks; mask++) {
            int lsb = mask & -mask;
            int bit = Integer.numberOfTrailingZeros(lsb);
            subsetSum[mask] = subsetSum[mask ^ lsb] + jobs[bit];
        }

        int INF = 1_000_000_000;
        int[][] dp = new int[k + 1][totalMasks];

        for (int w = 0; w <= k; w++) {
            Arrays.fill(dp[w], INF);
        }

        dp[0][0] = 0;

        for (int w = 1; w <= k; w++) {
            for (int mask = 0; mask < totalMasks; mask++) {
                for (int sub = mask; sub > 0; sub = (sub - 1) & mask) {
                    dp[w][mask] = Math.min(
                        dp[w][mask],
                        Math.max(dp[w - 1][mask ^ sub], subsetSum[sub])
                    );
                }
                if (mask == 0) {
                    dp[w][mask] = 0;
                }
            }
        }

        return dp[k][totalMasks - 1];
    }
}
```

---

## Complexity

This approach has:

- `k * 2^n` states
- and each state iterates over all submasks

Total complexity is roughly:

```text
O(k * 3^n)
```

Space:

```text
O(k * 2^n)
```

Since `n <= 12`, this is acceptable.

---

# 7. Approach 4 — Binary Search + Subset DP Feasibility

## Main Idea

This combines the best of both worlds:

1. binary search on the answer
2. check feasibility using subset DP instead of DFS

Feasibility question:

> Can all jobs be partitioned into at most `k` subsets such that each subset sum <= limit?

---

## Feasibility DP

Precompute all subsets whose sum is <= limit.
Then compute the minimum number of workers needed to cover all jobs.

Let:

```text
dp[mask] = minimum number of workers needed to finish jobs in mask
```

Transition:

Choose a valid submask `sub` from remaining jobs:

```text
dp[mask | sub] = min(dp[mask | sub], dp[mask] + 1)
```

At the end, feasibility is:

```text
dp[fullMask] <= k
```

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumTimeRequired(int[] jobs, int k) {
        int n = jobs.length;
        int left = 0, right = 0;
        for (int x : jobs) {
            left = Math.max(left, x);
            right += x;
        }

        int totalMasks = 1 << n;
        int[] subsetSum = new int[totalMasks];
        for (int mask = 1; mask < totalMasks; mask++) {
            int lsb = mask & -mask;
            int bit = Integer.numberOfTrailingZeros(lsb);
            subsetSum[mask] = subsetSum[mask ^ lsb] + jobs[bit];
        }

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canFinish(subsetSum, n, k, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canFinish(int[] subsetSum, int n, int k, int limit) {
        int totalMasks = 1 << n;
        int INF = 1_000_000_000;
        int[] dp = new int[totalMasks];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] >= k) continue;

            int remain = ((1 << n) - 1) ^ mask;
            for (int sub = remain; sub > 0; sub = (sub - 1) & remain) {
                if (subsetSum[sub] <= limit) {
                    dp[mask | sub] = Math.min(dp[mask | sub], dp[mask] + 1);
                }
            }
        }

        return dp[totalMasks - 1] <= k;
    }
}
```

---

## Complexity

Binary search:

```text
O(log(sum(jobs)))
```

Each feasibility check is approximately:

```text
O(3^n)
```

Space:

```text
O(2^n)
```

This is also fully acceptable for `n <= 12`.

---

# 8. Which Approach Is Best?

## Best practical interview solution

**Approach 2: Binary Search + DFS feasibility**

Why?

- conceptually elegant,
- strong pruning,
- shorter than subset DP,
- widely recognized pattern.

## Best “pure DP” solution

**Approach 3: Direct subset DP**

Why?

- no binary search,
- exact optimization DP,
- neat use of submask enumeration.

## Best if you want iterative feasibility

**Approach 4: Binary Search + subset DP feasibility**

Why?

- avoids recursion,
- still uses monotonic answer.

---

# 9. Important Symmetry Pruning in DFS

This line matters a lot:

```java
if (seen.contains(loads[i])) continue;
```

Suppose current worker loads are:

```text
[5, 5, 2]
```

Trying to assign the next job to worker 0 or worker 1 produces equivalent states, since both workers have identical current loads.

So we only need to try one of them.

This dramatically reduces repeated work.

---

# 10. Why Greedy Fails

A tempting greedy rule is:

> always assign the next largest job to the currently least-loaded worker

This is the classic load-balancing heuristic, but it is not always optimal.

Example-style intuition:

- a locally balanced assignment can block a globally optimal exact partition,
- the problem requires minimizing the maximum load exactly, not approximately.

So greedy alone is insufficient.

---

# 11. Small Worked Example

Take:

```text
jobs = [1,2,4,7,8], k = 2
```

The total sum is:

```text
22
```

Lower bound:

```text
max(jobs) = 8
```

Upper bound:

```text
22
```

Try binary search midpoint:

```text
15
```

Feasible? yes.

Try smaller:

```text
11
```

Feasible? yes:

- worker A → 8 + 2 + 1 = 11
- worker B → 7 + 4 = 11

Try smaller:

```text
10
```

Not feasible.

So answer is `11`.

---

# 12. Correctness Sketch

## For Backtracking

We enumerate all possible job-to-worker assignments, with pruning only removing states that are:

- symmetric duplicates, or
- already worse than the best found.

Thus the best feasible assignment is preserved.

## For Binary Search

Feasibility is monotonic:
if load limit `X` is feasible, then any `Y >= X` is feasible.
Therefore binary search correctly finds the smallest feasible maximum load.

## For Subset DP

The DP explicitly considers all ways to partition subsets of jobs among workers and uses the correct recurrence on subset sums.
Hence the result is exact.

---

# 13. Comparison Table

| Approach                  | Main Idea                        |                   Time |        Space | Notes                   |
| ------------------------- | -------------------------------- | ---------------------: | -----------: | ----------------------- |
| Backtracking + pruning    | assign jobs directly             | exponential, practical |     `O(k+n)` | very strong in practice |
| Binary Search + DFS       | search answer, DFS check         |       `O(log S * DFS)` |     `O(k+n)` | best interview approach |
| Direct subset DP          | partition via subsets            |           `O(k * 3^n)` | `O(k * 2^n)` | exact and elegant       |
| Binary Search + subset DP | monotone + iterative feasibility |       `O(log S * 3^n)` |     `O(2^n)` | recursion-free          |

where `S = sum(jobs)`.

---

# 14. Recommended Java Solution

This is the version I would usually recommend first in an interview:

## Binary Search + DFS Feasibility

```java
import java.util.*;

class Solution {
    public int minimumTimeRequired(int[] jobs, int k) {
        Arrays.sort(jobs);
        reverse(jobs);

        int left = jobs[0];
        int right = 0;
        for (int x : jobs) {
            right += x;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;
            int[] loads = new int[k];
            if (canAssign(jobs, 0, loads, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canAssign(int[] jobs, int idx, int[] loads, int limit) {
        if (idx == jobs.length) {
            return true;
        }

        int job = jobs[idx];
        Set<Integer> seen = new HashSet<>();

        for (int i = 0; i < loads.length; i++) {
            if (loads[i] + job > limit) {
                continue;
            }
            if (seen.contains(loads[i])) {
                continue;
            }
            seen.add(loads[i]);

            loads[i] += job;
            if (canAssign(jobs, idx + 1, loads, limit)) {
                return true;
            }
            loads[i] -= job;

            if (loads[i] == 0) {
                break;
            }
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

# 15. Final Takeaway

This problem is all about recognizing the small `n`.

Because:

```text
jobs.length <= 12
```

exact exponential methods are viable.

The cleanest mental model is:

> minimize the smallest feasible workload cap

which naturally leads to:

- binary search on the answer,
- recursive assignment with symmetry pruning.

That is usually the best balance of:

- correctness,
- performance,
- explainability.
