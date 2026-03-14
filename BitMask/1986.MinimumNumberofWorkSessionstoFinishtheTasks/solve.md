# 1986. Minimum Number of Work Sessions to Finish the Tasks

## Problem Restatement

You are given:

- `tasks[i]`: time needed for the `i-th` task
- `sessionTime`: maximum time available in one work session

Rules:

- A task cannot be split across sessions.
- Multiple tasks can be done in one session as long as their total time does not exceed `sessionTime`.
- Tasks can be reordered in any way.

Goal: return the **minimum number of sessions** needed to finish all tasks.

---

## Key Observations

### 1. Reordering is allowed

This changes the problem completely.
We are **not** forced to follow the original order, so this is really a **partitioning / packing** problem.

### 2. `n <= 14`

This is the biggest clue.

- Brute force over all permutations would be too large.
- But `2^14 = 16384`, which is small enough for **bitmask DP**.

That means we should think in terms of:

- a subset of tasks already completed
- transitions to larger subsets
- or choosing which subset fits in one session

### 3. This is similar to bin packing, but with very small `n`

In general, bin packing is hard.
But the small constraint makes exponential-state DP feasible.

---

# Approach 1: Bitmask DP with `(sessions used, remaining time)` state compression

## Core Idea

For every subset of completed tasks, store the **best state** after finishing exactly those tasks.

A state consists of:

- how many sessions have been opened so far
- how much time is already used in the current session, or equivalently how much remains

A very elegant version is:

- `dp[mask] = minimum sessions needed`
- `remain[mask] = maximum remaining time in the last session among all ways that use dp[mask] sessions`

But an even cleaner implementation is to store:

- `dp[mask] = minimum sessions used`
- `used[mask] = used time in the current last session` for the best configuration

When adding a new task:

- if it fits in the current session, extend current session
- otherwise open a new session

We compare two candidate states lexicographically:

1. fewer sessions is better
2. if sessions are equal, smaller `used time` in the current session is better
   because it leaves more free space for future tasks

---

## State Definition

Let:

- `mask` be a bitmask of completed tasks
- `dp[mask]` = minimum number of sessions needed for this subset
- `used[mask]` = used time in the current last session for that optimal plan

Initial state:

- `dp[0] = 1`
- `used[0] = 0`

Why `1` and not `0`?
Because we conceptually start with the first session available.
This makes transitions simpler.

---

## Transition

For every `mask`, try to add every task `j` not yet in `mask`.

Let `next = mask | (1 << j)`.

### Case 1: task fits in current session

If:

```java
used[mask] + tasks[j] <= sessionTime
```

then:

- `dp[next] = dp[mask]`
- `used[next] = used[mask] + tasks[j]`

### Case 2: task does not fit

Then we must open a new session:

- `dp[next] = dp[mask] + 1`
- `used[next] = tasks[j]`

We keep the better state for `next`.

---

## Why this works

At every subset, the only future-relevant information is:

- how many sessions have already been spent
- how full the current session is

The exact arrangement of earlier sessions does not matter anymore.

That is the DP principle here.

---

## Java Code

```java
class Solution {
    public int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        int totalMasks = 1 << n;

        int[] dp = new int[totalMasks];
        int[] used = new int[totalMasks];

        int INF = (int) 1e9;
        for (int mask = 0; mask < totalMasks; mask++) {
            dp[mask] = INF;
            used[mask] = INF;
        }

        dp[0] = 1;     // start with one available session
        used[0] = 0;   // current session has 0 time used

        for (int mask = 0; mask < totalMasks; mask++) {
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) continue;

                int next = mask | (1 << j);
                int nextSessions;
                int nextUsed;

                if (used[mask] + tasks[j] <= sessionTime) {
                    nextSessions = dp[mask];
                    nextUsed = used[mask] + tasks[j];
                } else {
                    nextSessions = dp[mask] + 1;
                    nextUsed = tasks[j];
                }

                if (nextSessions < dp[next] ||
                    (nextSessions == dp[next] && nextUsed < used[next])) {
                    dp[next] = nextSessions;
                    used[next] = nextUsed;
                }
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

## Example Walkthrough

### Input

```text
tasks = [1,2,3], sessionTime = 3
```

Start:

- `mask = 000`, `dp = 1`, `used = 0`

Try adding task `1`:

- `001 -> sessions=1, used=1`

Try adding task `2`:

- `010 -> sessions=1, used=2`

Try adding task `3`:

- `100 -> sessions=1, used=3`

Now from `001` (`used=1`), add task `2`:

- fits, so `011 -> sessions=1, used=3`

From `011`, add task `3`:

- does not fit, so `111 -> sessions=2, used=3`

Answer = `2`

---

## Complexity

- Number of states: `2^n`
- For each state, try all `n` tasks

Time:

```text
O(n * 2^n)
```

Space:

```text
O(2^n)
```

---

## Pros

- Very clean
- Fast enough for `n <= 14`
- Usually the best interview / contest solution

## Cons

- The state idea is slightly subtle at first
- Needs careful tie-breaking

---

# Approach 2: Subset DP by precomputing which subsets fit in one session

## Core Idea

Think differently:

- Any subset of tasks whose total time is `<= sessionTime` can be completed in **one** session.
- Then the problem becomes:

> Partition all tasks into the minimum number of valid subsets.

This leads to a classic subset DP.

---

## Step 1: Precompute valid subsets

For every subset `mask`, compute:

```text
sum(mask) = total duration of tasks in that subset
```

If `sum(mask) <= sessionTime`, then that subset can be done in one session.

Let:

```text
valid[mask] = true if mask fits in one session
```

---

## Step 2: DP over subsets

Let:

```text
dp[mask] = minimum sessions needed to finish exactly tasks in mask
```

Base case:

```text
dp[0] = 0
```

Transition:

For each `mask`, iterate over its submasks `sub`:

- if `valid[sub]` is true
- then we can do `sub` in one session
- and the remaining tasks are `mask ^ sub`

So:

```text
dp[mask] = min(dp[mask], dp[mask ^ sub] + 1)
```

---

## Why this works

This DP tries every possible choice for the **last session**.

If the last session contains exactly `sub`, then the rest must be `mask ^ sub`.
Since `sub` itself is feasible in one session, this is a valid partition.

---

## Efficient Submask Iteration

To iterate over all submasks of `mask`:

```java
for (int sub = mask; sub > 0; sub = (sub - 1) & mask)
```

This is a standard bitmask trick.

---

## Java Code

```java
class Solution {
    public int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        int totalMasks = 1 << n;

        int[] sum = new int[totalMasks];
        boolean[] valid = new boolean[totalMasks];

        for (int mask = 1; mask < totalMasks; mask++) {
            int lsb = mask & -mask;
            int bit = Integer.numberOfTrailingZeros(lsb);
            int prev = mask ^ lsb;
            sum[mask] = sum[prev] + tasks[bit];
            valid[mask] = sum[mask] <= sessionTime;
        }

        int INF = (int) 1e9;
        int[] dp = new int[totalMasks];
        for (int mask = 0; mask < totalMasks; mask++) {
            dp[mask] = INF;
        }
        dp[0] = 0;

        for (int mask = 1; mask < totalMasks; mask++) {
            for (int sub = mask; sub > 0; sub = (sub - 1) & mask) {
                if (valid[sub]) {
                    dp[mask] = Math.min(dp[mask], dp[mask ^ sub] + 1);
                }
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

## Complexity

### Precompute subset sums

```text
O(2^n)
```

### DP transitions

For each mask, iterate over all submasks.

Total complexity:

```text
O(3^n)
```

Why `3^n`?

Because across all masks, the total number of `(mask, submask)` pairs is `3^n`.

Space:

```text
O(2^n)
```

---

## Pros

- Conceptually very direct
- Easy to reason about correctness
- Very nice when you think in terms of partitioning into valid sessions

## Cons

- Slower than Approach 1
- `O(3^n)` is still fine for `n <= 14`, but less elegant computationally

---

# Approach 3: Backtracking + Pruning

## Core Idea

Try to place tasks one by one into existing sessions, or start a new session.

This is a search solution rather than full DP.

To make it practical, use strong pruning:

1. Sort tasks in descending order
   Place big tasks first. This reduces branching.

2. Avoid symmetric states
   If two sessions currently have the same used time, placing the task into either is equivalent.

3. Track the current best answer
   If current number of sessions already reaches or exceeds best known answer, stop.

---

## Why descending sort helps

Large tasks are harder to place than small ones.

If you place small tasks first, you may keep many equivalent possibilities alive.
Placing large tasks first forces the structure earlier and prunes much more aggressively.

---

## Backtracking State

- `sessions[i]` = used time in session `i`
- `count` = number of sessions currently opened
- `index` = which task we are placing next
- `best` = global minimum answer found so far

At each step:

- try to place `tasks[index]` into each existing session if it fits
- also try opening a new session

---

## Important Pruning Rules

### Rule 1: Bound by current best

If `count >= best`, return.

### Rule 2: Skip duplicate session loads

If two sessions have same used time, trying both gives symmetric results.

### Rule 3: If task does not fit in an empty session candidate, break appropriately

A common pruning:

- once you try putting a task into an empty new session and backtrack,
- there is no point trying other empty sessions

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private int best;

    public int minSessions(int[] tasks, int sessionTime) {
        Arrays.sort(tasks);
        reverse(tasks);

        best = tasks.length; // worst case: one task per session
        int[] sessions = new int[tasks.length];

        dfs(tasks, sessionTime, 0, sessions, 0);
        return best;
    }

    private void dfs(int[] tasks, int sessionTime, int index, int[] sessions, int count) {
        if (count >= best) return;

        if (index == tasks.length) {
            best = count;
            return;
        }

        int task = tasks[index];

        for (int i = 0; i < count; i++) {
            if (sessions[i] + task <= sessionTime) {
                // Skip symmetric states
                boolean duplicate = false;
                for (int j = 0; j < i; j++) {
                    if (sessions[j] == sessions[i]) {
                        duplicate = true;
                        break;
                    }
                }
                if (duplicate) continue;

                sessions[i] += task;
                dfs(tasks, sessionTime, index + 1, sessions, count);
                sessions[i] -= task;
            }
        }

        // Open a new session
        sessions[count] = task;
        dfs(tasks, sessionTime, index + 1, sessions, count + 1);
        sessions[count] = 0;
    }

    private void reverse(int[] arr) {
        int i = 0, j = arr.length - 1;
        while (i < j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++;
            j--;
        }
    }
}
```

---

## Complexity

Worst case is exponential and harder to state tightly.

A rough statement:

- worst case can approach factorial / exponential-style search
- but with `n <= 14` and strong pruning, it usually performs very well

Space:

```text
O(n)
```

for recursion depth and session array.

---

## Pros

- Very intuitive
- Great for understanding the packing process
- Often surprisingly fast in practice

## Cons

- Harder to guarantee performance mathematically
- More error-prone than DP
- Usually not the most standard answer for this problem

---

# Which approach should you choose?

## Best practical choice: Approach 1

Use the **bitmask DP with `(sessions, used time)`**.

Why?

- optimal time complexity for this constraint range
- compact
- standard accepted solution
- elegant state compression

## Best for conceptual clarity: Approach 2

If you think in terms of:

> “Which subsets can form one session?”

then subset partition DP is very natural.

## Best for interview discussion depth: Approach 3

Backtracking with pruning is useful because it shows:

- symmetry reduction
- branch-and-bound thinking
- packing intuition

---

# Deep Intuition: Why Approach 1 is stronger than it first looks

At first glance, it feels like we should track all sessions, because future tasks might depend on how earlier sessions were filled.

But that is the trap.

Once we know:

1. how many sessions have been used
2. how full the current session is

we do **not** care about the exact arrangement of earlier sessions anymore.
Those earlier sessions are already closed and cannot accept more tasks.

So the only “open” decision frontier is the **last current session**.

That is why the DP compresses the state so well.

---

# Correctness Sketch for Approach 1

We prove that `dp[mask]` and `used[mask]` represent an optimal way to finish tasks in `mask`.

## Base case

For `mask = 0`:

- no task is done
- one empty session is available
- so `(1 session, 0 used)` is valid

## Inductive step

Assume all smaller subsets are correctly represented.

For a given `mask`, when adding an unfinished task `j`, there are only two valid possibilities:

1. it fits in current session
2. it starts a new session

Both are considered by the transition.

Among all possible ways to reach `next`, we keep:

- minimum number of sessions
- and among those, minimum used time in the current session

This tie-break is safe because with equal number of sessions, having less used time leaves at least as much flexibility for all future insertions.

So the stored state dominates all worse equal-session states.

Therefore the DP remains correct.

---

# Edge Cases

## 1. Single task

```text
tasks = [5], sessionTime = 5
```

Answer is `1`.

## 2. All tasks fit into one session

```text
tasks = [1,2,3,4,5], sessionTime = 15
```

Answer is `1`.

## 3. Every task nearly fills a session

```text
tasks = [5,5,5], sessionTime = 5
```

Answer is `3`.

## 4. Many small tasks with many combinations

This is where bitmask DP shines.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
class Solution {
    public int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        int totalMasks = 1 << n;
        int INF = (int) 1e9;

        int[] dp = new int[totalMasks];
        int[] used = new int[totalMasks];

        for (int mask = 0; mask < totalMasks; mask++) {
            dp[mask] = INF;
            used[mask] = INF;
        }

        dp[0] = 1;
        used[0] = 0;

        for (int mask = 0; mask < totalMasks; mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) continue;

                int next = mask | (1 << i);
                int sessions = dp[mask];
                int curUsed = used[mask];

                if (curUsed + tasks[i] <= sessionTime) {
                    int candidateSessions = sessions;
                    int candidateUsed = curUsed + tasks[i];

                    if (candidateSessions < dp[next] ||
                        (candidateSessions == dp[next] && candidateUsed < used[next])) {
                        dp[next] = candidateSessions;
                        used[next] = candidateUsed;
                    }
                } else {
                    int candidateSessions = sessions + 1;
                    int candidateUsed = tasks[i];

                    if (candidateSessions < dp[next] ||
                        (candidateSessions == dp[next] && candidateUsed < used[next])) {
                        dp[next] = candidateSessions;
                        used[next] = candidateUsed;
                    }
                }
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

# Comparison Table

| Approach   | Idea                                                       |         Time |    Space | Recommended        |
| ---------- | ---------------------------------------------------------- | -----------: | -------: | ------------------ |
| Approach 1 | Bitmask DP storing best `(sessions, used)` for each subset | `O(n * 2^n)` | `O(2^n)` | Yes, best          |
| Approach 2 | Subset partition DP using valid one-session subsets        |     `O(3^n)` | `O(2^n)` | Good               |
| Approach 3 | Backtracking with pruning                                  |  Exponential |   `O(n)` | Good for intuition |

---

# Takeaway

This problem looks like scheduling, but the real pattern is:

- **small `n`**
- **subset exploration**
- **state compression**

That should strongly suggest **bitmask DP**.

The most important insight is:

> for a completed subset, the only useful information is how many sessions we used and how full the current one is.

Once that clicks, the solution becomes much cleaner.
