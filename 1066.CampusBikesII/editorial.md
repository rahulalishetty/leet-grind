# 1066. Campus Bikes II — Exhaustive Summary of All 4 Approaches

## Problem Restatement

We are given:

- `n` workers
- `m` bikes
- `n <= m`
- each worker and bike is a 2D point

We must assign **exactly one unique bike to each worker** so that the **total Manhattan distance** is minimized.

### Manhattan Distance

For points `(x1, y1)` and `(x2, y2)`:

```text
|x1 - x2| + |y1 - y2|
```

---

## Core Observation

This is an **assignment / matching** problem with very small constraints:

- `1 <= n <= m <= 10`

That small upper bound is the reason several exponential approaches are acceptable here.

The main challenge is:

- each worker must get one bike
- each bike can be used at most once
- we want the **minimum total cost**

Because choices for later workers depend on which bikes were already taken, this is a classic **state-space search** problem.

---

# Approach 1: Greedy Backtracking

## Intuition

The most direct solution is:

- assign a bike to worker `0`
- then assign a remaining bike to worker `1`
- continue until all workers are assigned

That means we explore all valid worker-bike assignments.

If there are `m` bikes and `n` workers, then:

- worker 0 has `m` choices
- worker 1 has `m - 1` choices
- worker 2 has `m - 2` choices
- ...
- worker `n - 1` has `m - n + 1` choices

So the total number of complete assignments is:

```text
m * (m - 1) * (m - 2) * ... * (m - n + 1)
= m! / (m - n)!
```

That is already feasible for small `m`, but still expensive.

### Important Pruning Idea

Suppose:

- `smallestDistanceSum` = best complete answer found so far
- `currDistanceSum` = current partial assignment sum

If at any point:

```text
currDistanceSum >= smallestDistanceSum
```

then we should stop exploring that branch immediately.

Why?

Because assigning more bikes can only increase the total distance, never decrease it.

So this is plain backtracking with a very useful pruning condition.

---

## How the State Works

At any recursive call, we need:

- which worker we are assigning now: `workerIndex`
- which bikes are already taken: `visited[]`
- current total distance so far: `currDistanceSum`

---

## Backtracking Flow

For a given worker:

1. loop over all bikes
2. if a bike is free, assign it
3. recurse for the next worker
4. undo the assignment

This is standard choose → explore → unchoose recursion.

---

## Java Code

```java
class Solution {
    // Maximum number of bikes is 10
    boolean[] visited = new boolean[10];
    int smallestDistanceSum = Integer.MAX_VALUE;

    private int findDistance(int[] worker, int[] bike) {
        return Math.abs(worker[0] - bike[0]) + Math.abs(worker[1] - bike[1]);
    }

    private void minimumDistanceSum(int[][] workers, int workerIndex,
                                    int[][] bikes, int currDistanceSum) {
        if (workerIndex >= workers.length) {
            smallestDistanceSum = Math.min(smallestDistanceSum, currDistanceSum);
            return;
        }

        // Prune useless branches
        if (currDistanceSum >= smallestDistanceSum) {
            return;
        }

        for (int bikeIndex = 0; bikeIndex < bikes.length; bikeIndex++) {
            if (!visited[bikeIndex]) {
                visited[bikeIndex] = true;

                minimumDistanceSum(
                    workers,
                    workerIndex + 1,
                    bikes,
                    currDistanceSum + findDistance(workers[workerIndex], bikes[bikeIndex])
                );

                visited[bikeIndex] = false;
            }
        }
    }

    public int assignBikes(int[][] workers, int[][] bikes) {
        minimumDistanceSum(workers, 0, bikes, 0);
        return smallestDistanceSum;
    }
}
```

---

## Small Example Walkthrough

### Example

```text
workers = [[0,0],[2,1]]
bikes   = [[1,2],[3,3]]
```

### Distances

- worker 0 → bike 0 = `|0-1| + |0-2| = 3`
- worker 0 → bike 1 = `|0-3| + |0-3| = 6`
- worker 1 → bike 0 = `|2-1| + |1-2| = 2`
- worker 1 → bike 1 = `|2-3| + |1-3| = 3`

Possible assignments:

1. worker 0 → bike 0, worker 1 → bike 1
   total = `3 + 3 = 6`

2. worker 0 → bike 1, worker 1 → bike 0
   total = `6 + 2 = 8`

Best = `6`

---

## Complexity

Let:

- `N` = number of workers
- `M` = number of bikes

### Time Complexity

```text
O(M! / (M - N)!)
```

Because we may explore every valid assignment.

### Space Complexity

```text
O(N + M)
```

- `O(M)` for `visited`
- `O(N)` recursion depth

---

## Strengths

- conceptually simple
- very natural first solution
- pruning helps a lot in practice

## Weaknesses

- still factorial/permutation-like in worst case
- not the most efficient systematic solution

---

# Approach 2: Top-Down Dynamic Programming + Bitmasking

## Intuition

The backtracking solution repeats many equivalent subproblems.

For example, suppose different assignment orders lead to the same set of bikes already taken. Once that happens, the remaining work is identical:

- same remaining workers
- same available bikes

So instead of recomputing from scratch, we should cache the answer.

That is exactly dynamic programming.

---

## Why Bitmasking?

Since `m <= 10`, we can represent which bikes are taken using a bitmask.

### Meaning of `mask`

If bike `i` is taken, then bit `i` in `mask` is `1`.

For example, if `m = 4`:

```text
mask = 0101
```

means:

- bike 0 taken
- bike 2 taken
- bikes 1 and 3 free

---

## Key Insight: `workerIndex` is Determined by `mask`

If `mask` has `k` set bits, that means exactly `k` bikes have already been assigned.

Since we assign workers in order:

- worker 0 first
- then worker 1
- then worker 2
- ...

the next worker index is simply:

```text
number of set bits in mask
```

So memoization can be done using only `mask`.

That is a very important simplification.

---

## DP Definition

Let:

```text
dp(mask) = minimum additional distance needed
           after assigning the bikes marked in mask
```

Then:

- if all workers are assigned, answer is `0`
- otherwise try assigning each free bike to the next worker

Transition:

```text
dp(mask) = min(
    dist(workerIndex, bikeIndex) + dp(mask | (1 << bikeIndex))
)
```

where `workerIndex = popcount(mask)`.

---

## Java Code

```java
class Solution {
    int[] memo = new int[1024];

    private int findDistance(int[] worker, int[] bike) {
        return Math.abs(worker[0] - bike[0]) + Math.abs(worker[1] - bike[1]);
    }

    private int minimumDistanceSum(int[][] workers, int[][] bikes,
                                   int workerIndex, int mask) {
        if (workerIndex >= workers.length) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int smallestDistanceSum = Integer.MAX_VALUE;

        for (int bikeIndex = 0; bikeIndex < bikes.length; bikeIndex++) {
            if ((mask & (1 << bikeIndex)) == 0) {
                smallestDistanceSum = Math.min(
                    smallestDistanceSum,
                    findDistance(workers[workerIndex], bikes[bikeIndex]) +
                    minimumDistanceSum(
                        workers,
                        bikes,
                        workerIndex + 1,
                        mask | (1 << bikeIndex)
                    )
                );
            }
        }

        return memo[mask] = smallestDistanceSum;
    }

    public int assignBikes(int[][] workers, int[][] bikes) {
        Arrays.fill(memo, -1);
        return minimumDistanceSum(workers, bikes, 0, 0);
    }
}
```

---

## Cleaner Variant Using `workerIndex = popcount(mask)`

The original explanation says `workerIndex` does not actually need to be stored separately.

Here is the equivalent cleaner version:

```java
class Solution {
    int[] memo = new int[1 << 10];

    private int dist(int[] w, int[] b) {
        return Math.abs(w[0] - b[0]) + Math.abs(w[1] - b[1]);
    }

    private int dfs(int[][] workers, int[][] bikes, int mask) {
        int workerIndex = Integer.bitCount(mask);

        if (workerIndex == workers.length) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int ans = Integer.MAX_VALUE;

        for (int bikeIndex = 0; bikeIndex < bikes.length; bikeIndex++) {
            if ((mask & (1 << bikeIndex)) == 0) {
                ans = Math.min(
                    ans,
                    dist(workers[workerIndex], bikes[bikeIndex]) +
                    dfs(workers, bikes, mask | (1 << bikeIndex))
                );
            }
        }

        return memo[mask] = ans;
    }

    public int assignBikes(int[][] workers, int[][] bikes) {
        Arrays.fill(memo, -1);
        return dfs(workers, bikes, 0);
    }
}
```

This version makes the state interpretation more explicit.

---

## Why This Is Better Than Plain Backtracking

In plain backtracking, the same set of assigned bikes may be reached through multiple paths.

With memoization:

- once we solve `mask`, we never solve it again
- all future visits return instantly

That collapses many exponential branches into one shared subproblem.

---

## Complexity

### Number of States

There are `2^M` possible masks.

### Work Per State

For each mask, we try all `M` bikes.

So total time:

```text
O(M * 2^M)
```

### Space

```text
O(2^M)
```

for memo, plus recursion stack `O(N)`.

More precisely:

```text
O(2^M + N)
```

---

## Strengths

- much faster than brute force
- elegant state compression
- usually the standard optimal interview solution

## Weaknesses

- requires understanding bitmask DP
- recursive version still uses call stack

---

# Approach 3: Bottom-Up Dynamic Programming + Bitmasking

## Intuition

Approach 2 computes answers recursively from larger subproblems to smaller ones.

We can do the same thing iteratively.

Instead of:

- starting from `mask = 0` recursively,
- branching deeper,

we iterate through masks in increasing order and propagate transitions forward.

This avoids recursion stack usage.

---

## DP Meaning

Let:

```text
memo[mask] = minimum distance sum to reach this mask
```

where `mask` tells us which bikes are already assigned.

If `mask` has `k` set bits, that means we already assigned bikes to the first `k` workers.

So the next worker is:

```text
nextWorkerIndex = popcount(mask)
```

---

## Transition

For each `mask`:

- determine `nextWorkerIndex`
- try giving any unused bike to that worker
- update `newMask`

```text
newMask = mask | (1 << bikeIndex)
memo[newMask] = min(memo[newMask],
                    memo[mask] + dist(worker[nextWorkerIndex], bike[bikeIndex]))
```

---

## Java Code

```java
class Solution {
    int[] memo = new int[1024];

    private int countNumOfOnes(int mask) {
        int count = 0;
        while (mask != 0) {
            mask &= (mask - 1);
            count++;
        }
        return count;
    }

    private int findDistance(int[] worker, int[] bike) {
        return Math.abs(worker[0] - bike[0]) + Math.abs(worker[1] - bike[1]);
    }

    private int minimumDistanceSum(int[][] workers, int[][] bikes) {
        int numOfBikes = bikes.length;
        int numOfWorkers = workers.length;
        int smallestDistanceSum = Integer.MAX_VALUE;

        memo[0] = 0;

        for (int mask = 0; mask < (1 << numOfBikes); mask++) {
            int nextWorkerIndex = countNumOfOnes(mask);

            if (nextWorkerIndex >= numOfWorkers) {
                smallestDistanceSum = Math.min(smallestDistanceSum, memo[mask]);
                continue;
            }

            for (int bikeIndex = 0; bikeIndex < numOfBikes; bikeIndex++) {
                if ((mask & (1 << bikeIndex)) == 0) {
                    int newMask = (1 << bikeIndex) | mask;

                    memo[newMask] = Math.min(
                        memo[newMask],
                        memo[mask] + findDistance(workers[nextWorkerIndex], bikes[bikeIndex])
                    );
                }
            }
        }

        return smallestDistanceSum;
    }

    public int assignBikes(int[][] workers, int[][] bikes) {
        Arrays.fill(memo, Integer.MAX_VALUE);
        return minimumDistanceSum(workers, bikes);
    }
}
```

---

## Cleaner Bottom-Up Version

This version only checks masks that assign at most `n` workers and directly extracts the answer from masks with exactly `n` set bits.

```java
class Solution {
    public int assignBikes(int[][] workers, int[][] bikes) {
        int n = workers.length, m = bikes.length;
        int size = 1 << m;
        int[] dp = new int[size];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int mask = 0; mask < size; mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue;

            int workerIndex = Integer.bitCount(mask);
            if (workerIndex >= n) continue;

            for (int bikeIndex = 0; bikeIndex < m; bikeIndex++) {
                if ((mask & (1 << bikeIndex)) == 0) {
                    int newMask = mask | (1 << bikeIndex);
                    int dist = Math.abs(workers[workerIndex][0] - bikes[bikeIndex][0]) +
                               Math.abs(workers[workerIndex][1] - bikes[bikeIndex][1]);

                    dp[newMask] = Math.min(dp[newMask], dp[mask] + dist);
                }
            }
        }

        int ans = Integer.MAX_VALUE;
        for (int mask = 0; mask < size; mask++) {
            if (Integer.bitCount(mask) == n) {
                ans = Math.min(ans, dp[mask]);
            }
        }
        return ans;
    }
}
```

---

## Why Bottom-Up Works

Think of each mask as a node in a DAG of states.

- `mask = 0` means no bikes assigned
- from `mask`, we can go to `newMask` by assigning one more bike
- every move increases the number of assigned bikes by exactly one

So the states naturally form levels based on popcount.

That makes iterative DP very natural.

---

## Complexity

For each of `2^M` masks, we may iterate over `M` bikes.

So:

```text
Time: O(M * 2^M)
Space: O(2^M)
```

If popcount is computed by Kernighan’s algorithm each time, there is still no asymptotic change.

---

## Strengths

- same asymptotic efficiency as top-down DP
- no recursion stack
- often slightly faster in practice

## Weaknesses

- iterative logic can feel less intuitive at first
- may process some masks that top-down never reaches

---

# Approach 4: Priority Queue (Dijkstra-Like State Search)

## Intuition

Approach 3 iterates through all masks in numeric order.

But numeric order is not meaningful for cost.

What we really care about is:

- which partial assignments currently have the smallest total distance?

That suggests a best-first search:

- always expand the state with the smallest distance sum so far

This is exactly the spirit of Dijkstra’s algorithm.

---

## State Graph View

Each state is a `mask`.

- node = a particular set of assigned bikes
- edge = assign one additional free bike to the next worker
- edge weight = Manhattan distance added by that assignment

We want the shortest path from:

```text
mask = 0
```

to any mask with exactly `n` assigned bikes.

Because all edge weights are non-negative, Dijkstra-style expansion works.

---

## Why Priority Queue Helps

Instead of exploring every state in arbitrary order, we process the cheapest partial assignment first.

So when we first pop a state whose number of assigned bikes equals the number of workers, that cost is guaranteed to be optimal.

---

## Algorithm Outline

1. push `(0, 0)` into min-heap
   meaning `(distanceSum = 0, mask = 0)`

2. repeatedly pop the smallest distance state

3. if already visited, skip

4. compute `workerIndex = popcount(mask)`

5. if `workerIndex == numOfWorkers`, return the current distance

6. otherwise assign every unused bike and push new states

---

## Java Code

```java
class Solution {
    private int findDistance(int[] worker, int[] bike) {
        return Math.abs(worker[0] - bike[0]) + Math.abs(worker[1] - bike[1]);
    }

    private int countNumOfOnes(int mask) {
        int count = 0;
        while (mask != 0) {
            mask &= (mask - 1);
            count++;
        }
        return count;
    }

    public int assignBikes(int[][] workers, int[][] bikes) {
        int numOfBikes = bikes.length, numOfWorkers = workers.length;

        PriorityQueue<int[]> priorityQueue =
            new PriorityQueue<>((a, b) -> a[0] - b[0]);
        Set<Integer> visited = new HashSet<>();

        priorityQueue.add(new int[]{0, 0});

        while (!priorityQueue.isEmpty()) {
            int currentDistanceSum = priorityQueue.peek()[0];
            int currentMask = priorityQueue.peek()[1];
            priorityQueue.remove();

            if (visited.contains(currentMask)) {
                continue;
            }

            visited.add(currentMask);

            int workerIndex = countNumOfOnes(currentMask);

            if (workerIndex == numOfWorkers) {
                return currentDistanceSum;
            }

            for (int bikeIndex = 0; bikeIndex < numOfBikes; bikeIndex++) {
                if ((currentMask & (1 << bikeIndex)) == 0) {
                    int nextStateDistanceSum =
                        currentDistanceSum +
                        findDistance(workers[workerIndex], bikes[bikeIndex]);

                    int nextStateMask = currentMask | (1 << bikeIndex);
                    priorityQueue.add(new int[]{nextStateDistanceSum, nextStateMask});
                }
            }
        }

        return -1;
    }
}
```

---

## Important Subtlety

The same `mask` may enter the priority queue multiple times.

Example:

- `0001 -> 0011`
- `0010 -> 0011`

So we need a `visited` set.

The first time we pop a mask from the min-heap, it has the minimum possible cost for that mask. After that, later copies can be ignored.

This is standard Dijkstra behavior.

---

## Complexity

This approach is more complicated to analyze because duplicate masks can be inserted into the heap.

The editorial states:

```text
Time:
O(P(M, N) * log(P(M, N)) + M * log(P(M, N)) * 2^M)

where P(M, N) = M! / (M - N)!
```

and

```text
Space:
O(P(M, N) + 2^M)
```

### Practical Interpretation

This approach can work well in practice because:

- it expands low-cost states first
- it may find the answer earlier than scanning all masks

But asymptotically, the DP approaches are cleaner and easier to reason about.

---

# Comparing the 4 Approaches

## 1) Greedy Backtracking

### Idea

Try every assignment with pruning.

### Best For

Understanding the brute-force search structure first.

### Complexity

- Time: `O(M! / (M - N)!)`
- Space: `O(N + M)`

### Pros

- easiest to understand
- pruning is intuitive

### Cons

- worst-case still very expensive

---

## 2) Top-Down DP + Bitmask

### Idea

Memoize by `mask` to avoid repeated work.

### Best For

Most standard high-quality solution.

### Complexity

- Time: `O(M * 2^M)`
- Space: `O(2^M + N)`

### Pros

- elegant
- efficient
- natural recursive transition

### Cons

- requires bitmask DP comfort

---

## 3) Bottom-Up DP + Bitmask

### Idea

Iteratively build states from smaller masks to larger masks.

### Best For

Avoiding recursion while keeping the same DP logic.

### Complexity

- Time: `O(M * 2^M)`
- Space: `O(2^M)`

### Pros

- no recursion stack
- very systematic

### Cons

- state transitions may feel less intuitive initially

---

## 4) Priority Queue / Dijkstra-Like

### Idea

Expand the currently cheapest partial assignment first.

### Best For

Seeing the problem as shortest-path over state space.

### Complexity

- More involved than DP
- generally less clean than bitmask DP for this problem

### Pros

- interesting alternate perspective
- early exit on optimal complete state

### Cons

- duplicate states in heap
- more complex analysis
- usually not the first recommended solution

---

# Which Approach Should You Prefer?

## Interview / LeetCode Preference

The most practical answer is:

### Prefer **Approach 2: Top-Down DP + Bitmasking**

because it gives:

- strong performance
- compact state
- clear recurrence
- good balance of intuition and efficiency

If you prefer iterative DP, then:

### Approach 3 is equally strong asymptotically

---

# Bitmasking Notes

Since bitmasking is central to approaches 2, 3, and 4, here is a quick refresher.

## Check if bike `i` is used

```java
(mask & (1 << i)) != 0
```

## Check if bike `i` is free

```java
(mask & (1 << i)) == 0
```

## Mark bike `i` as used

```java
mask | (1 << i)
```

## Remove bike `i` from mask

```java
mask ^ (1 << i)
```

Though in these solutions, unsetting is usually not needed because we create new masks instead of mutating old ones.

---

# Why `workerIndex = popcount(mask)` Is Valid

This is one of the most important logical points.

We always assign workers in fixed order:

- worker 0
- worker 1
- worker 2
- ...

If `mask` contains exactly 3 set bits, then exactly 3 bikes have already been assigned. Therefore exactly 3 workers have already been processed.

So the next worker must be:

```text
workerIndex = 3
```

This is why `mask` alone fully determines the state.

Without this observation, you may incorrectly think both `workerIndex` and `mask` are needed for memoization.

---

# Final Recommended Java Solution

This is the clean top-down DP + bitmask solution you would usually want to submit.

```java
class Solution {
    private int[] memo;

    private int dist(int[] w, int[] b) {
        return Math.abs(w[0] - b[0]) + Math.abs(w[1] - b[1]);
    }

    private int dfs(int[][] workers, int[][] bikes, int mask) {
        int workerIndex = Integer.bitCount(mask);

        if (workerIndex == workers.length) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int ans = Integer.MAX_VALUE;

        for (int bikeIndex = 0; bikeIndex < bikes.length; bikeIndex++) {
            if ((mask & (1 << bikeIndex)) == 0) {
                ans = Math.min(
                    ans,
                    dist(workers[workerIndex], bikes[bikeIndex]) +
                    dfs(workers, bikes, mask | (1 << bikeIndex))
                );
            }
        }

        return memo[mask] = ans;
    }

    public int assignBikes(int[][] workers, int[][] bikes) {
        memo = new int[1 << bikes.length];
        Arrays.fill(memo, -1);
        return dfs(workers, bikes, 0);
    }
}
```

---

# Final Takeaway

This problem is a compact and very instructive example of **state compression DP**.

The progression of ideas is valuable:

1. brute-force all assignments
2. prune bad branches
3. realize repeated states exist
4. compress state with a bitmask
5. solve with memoized DP or iterative DP
6. optionally reinterpret the state graph with Dijkstra-like expansion

That makes this problem a very good pattern to remember for:

- assignment problems with small `n`
- “choose one unique item per person”
- DP where used/unused choices matter
- problems with `n <= 10..20` where bitmasking is natural

---
