# 1494. Parallel Courses II — Exhaustive Java Notes

## Problem Statement

You are given:

- an integer `n`, representing `n` courses labeled from `1` to `n`
- a list of prerequisite relations `relations`, where:

```text
relations[i] = [prevCourse, nextCourse]
```

meaning:

```text
prevCourse must be taken before nextCourse
```

- an integer `k`, meaning in one semester you can take **at most `k` courses**

A course can be taken in the current semester only if **all its prerequisites were completed in previous semesters**.

Return the **minimum number of semesters** needed to complete all courses.

The graph is guaranteed to be a DAG, and it is guaranteed that finishing all courses is possible.

---

## Example 1

```text
Input:
n = 4
relations = [[2,1],[3,1],[1,4]]
k = 2

Output:
3
```

Explanation:

- Semester 1: take `2, 3`
- Semester 2: take `1`
- Semester 3: take `4`

---

## Example 2

```text
Input:
n = 5
relations = [[2,1],[3,1],[4,1],[1,5]]
k = 2

Output:
4
```

Explanation:

- Semester 1: take `2, 3`
- Semester 2: take `4`
- Semester 3: take `1`
- Semester 4: take `5`

---

## Constraints

```text
1 <= n <= 15
1 <= k <= n
0 <= relations.length <= n * (n - 1) / 2
The graph is a DAG
```

---

# 1. Core Insight

Because `n <= 15`, the strongest clue is:

> We can represent the set of completed courses as a **bitmask**.

A bitmask of length `n` can encode which courses are already finished:

- bit `i = 1` → course `i` is done
- bit `i = 0` → course `i` is not done

There are at most:

```text
2^15 = 32768
```

states, which is small enough for bitmask DP / BFS.

---

# 2. Prerequisite Mask Representation

For each course `c`, precompute a bitmask:

```text
pre[c]
```

where the set bits indicate all prerequisite courses required before `c`.

Then if `mask` represents completed courses, course `c` is available iff:

```text
(pre[c] & mask) == pre[c]
```

That means all prerequisites of `c` are already included in `mask`.

Also, `c` must not already be taken:

```text
((mask >> c) & 1) == 0
```

So availability is:

```text
course c is available iff
1) c not in mask
2) (pre[c] & mask) == pre[c]
```

---

# 3. Reformulating the Problem

At any state `mask`:

- find all currently available courses
- choose up to `k` of them for this semester
- transition to the new state with those courses marked done

Every transition costs exactly:

```text
+1 semester
```

So the task becomes:

> Find the shortest path from state `0` to state `(1<<n)-1`.

That naturally gives us:

- BFS on masks
- DP on masks

---

# 4. Approach 1 — Plain BFS over Bitmask States

## Idea

Each node in BFS is a completed-course mask.

From a mask:

1. compute all available courses
2. if number of available courses `<= k`, take all of them
3. otherwise enumerate all subsets of size `k`
4. push resulting masks into BFS

Because BFS expands by semester count, the first time we reach the full mask is the minimum answer.

---

## Why BFS Works

Every edge corresponds to exactly one semester.

So the shortest path in this state graph is exactly the minimum number of semesters.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        int[] pre = new int[n];
        for (int[] r : relations) {
            int u = r[0] - 1;
            int v = r[1] - 1;
            pre[v] |= (1 << u);
        }

        int full = (1 << n) - 1;
        Queue<Integer> queue = new ArrayDeque<>();
        boolean[] seen = new boolean[1 << n];

        queue.offer(0);
        seen[0] = true;

        int semesters = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int s = 0; s < size; s++) {
                int mask = queue.poll();
                if (mask == full) {
                    return semesters;
                }

                int available = 0;
                for (int c = 0; c < n; c++) {
                    if (((mask >> c) & 1) == 0 && (pre[c] & mask) == pre[c]) {
                        available |= (1 << c);
                    }
                }

                if (Integer.bitCount(available) <= k) {
                    int next = mask | available;
                    if (!seen[next]) {
                        seen[next] = true;
                        queue.offer(next);
                    }
                } else {
                    for (int sub = available; sub > 0; sub = (sub - 1) & available) {
                        if (Integer.bitCount(sub) == k) {
                            int next = mask | sub;
                            if (!seen[next]) {
                                seen[next] = true;
                                queue.offer(next);
                            }
                        }
                    }
                }
            }

            semesters++;
        }

        return -1;
    }
}
```

---

## Complexity

Let `N = n`.

There are at most:

```text
2^N
```

states.

For each state:

- computing available courses costs `O(N)`
- subset enumeration can cost up to `O(2^N)` in the worst case

Worst-case upper bound:

```text
O(2^N * (N + 2^N))
```

For `N <= 15`, this is still workable.

Space:

```text
O(2^N)
```

---

# 5. Important Optimization: If More Than k Are Available, Only Enumerate k-Sized Choices

Suppose at some state you have 6 available courses and `k = 2`.

You cannot take 1 course if taking 2 is always at least as good in terms of minimizing semesters.

Why?

Because:

- prerequisites are already satisfied before the semester starts
- taking more courses now cannot hurt future feasibility
- semesters are counted uniformly

So when available count exceeds `k`, we only need to consider subsets of size exactly `k`.

That dramatically reduces branching.

---

# 6. Approach 2 — DP on Bitmask

## Idea

Let:

```text
dp[mask] = minimum semesters needed to finish the courses in mask
```

We start with:

```text
dp[0] = 0
```

For each `mask`, compute available courses, and relax transitions to `mask | chosenSubset`.

This is essentially shortest path on DAG-like mask transitions, but written as DP.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        int[] pre = new int[n];
        for (int[] r : relations) {
            int u = r[0] - 1;
            int v = r[1] - 1;
            pre[v] |= (1 << u);
        }

        int totalStates = 1 << n;
        int full = totalStates - 1;
        int[] dp = new int[totalStates];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;

        for (int mask = 0; mask < totalStates; mask++) {
            if (dp[mask] >= Integer.MAX_VALUE / 2) {
                continue;
            }

            int available = 0;
            for (int c = 0; c < n; c++) {
                if (((mask >> c) & 1) == 0 && (pre[c] & mask) == pre[c]) {
                    available |= (1 << c);
                }
            }

            if (Integer.bitCount(available) <= k) {
                dp[mask | available] = Math.min(dp[mask | available], dp[mask] + 1);
            } else {
                for (int sub = available; sub > 0; sub = (sub - 1) & available) {
                    if (Integer.bitCount(sub) == k) {
                        dp[mask | sub] = Math.min(dp[mask | sub], dp[mask] + 1);
                    }
                }
            }
        }

        return dp[full];
    }
}
```

---

## Complexity

Same essential bound as BFS:

```text
O(2^N * (N + 2^N))
```

Space:

```text
O(2^N)
```

---

# 7. Approach 3 — Top-Down Memoized DFS

## Idea

Define:

```text
dfs(mask) = minimum semesters needed to finish all remaining courses starting from mask
```

Base case:

```text
dfs(fullMask) = 0
```

Transition:

- find currently available courses
- if available count `<= k`, take all of them
- else try every subset of size `k`
- answer is:

```text
1 + min(dfs(nextMask))
```

This is often the cleanest formulation.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int n;
    private int k;
    private int full;
    private int[] pre;
    private int[] memo;

    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        this.n = n;
        this.k = k;
        this.full = (1 << n) - 1;
        this.pre = new int[n];
        this.memo = new int[1 << n];
        Arrays.fill(memo, -1);

        for (int[] r : relations) {
            int u = r[0] - 1;
            int v = r[1] - 1;
            pre[v] |= (1 << u);
        }

        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == full) {
            return 0;
        }
        if (memo[mask] != -1) {
            return memo[mask];
        }

        int available = 0;
        for (int c = 0; c < n; c++) {
            if (((mask >> c) & 1) == 0 && (pre[c] & mask) == pre[c]) {
                available |= (1 << c);
            }
        }

        int best = Integer.MAX_VALUE / 2;

        if (Integer.bitCount(available) <= k) {
            best = 1 + dfs(mask | available);
        } else {
            for (int sub = available; sub > 0; sub = (sub - 1) & available) {
                if (Integer.bitCount(sub) == k) {
                    best = Math.min(best, 1 + dfs(mask | sub));
                }
            }
        }

        memo[mask] = best;
        return best;
    }
}
```

---

## Complexity

Again:

```text
O(2^N * (N + 2^N))
```

Space:

```text
O(2^N)
```

plus recursion stack `O(2^N)` memo, `O(N)` stack depth in practice.

---

# 8. Strong Optimization: Submask Enumeration Trick

This line:

```java
for (int sub = available; sub > 0; sub = (sub - 1) & available)
```

enumerates all submasks of `available`.

This is a standard bitmask trick.

If `available = 10110`, then the loop visits every submask contained inside those set bits.

This is much faster and cleaner than generating subsets manually.

---

# 9. Another Optimization: Precompute Bit Counts

Repeatedly calling:

```java
Integer.bitCount(sub)
```

is fine for `n <= 15`, but if you want cleaner constant factors, you can precompute bit counts for all masks.

Example:

```java
int[] bits = new int[1 << n];
for (int mask = 1; mask < (1 << n); mask++) {
    bits[mask] = bits[mask >> 1] + (mask & 1);
}
```

Then replace `Integer.bitCount(x)` with `bits[x]`.

This is optional but nice.

---

# 10. Approach 4 — DP with Precomputed Valid Semester Choices

## Idea

For each mask, after computing `available`, we can derive the exact set of valid choices for the current semester:

- if `bitcount(available) <= k`: only one choice, namely `available`
- else: all submasks of `available` with exactly `k` bits

This is not fundamentally different, but it makes the transition logic easier to reason about if separated.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        int[] pre = new int[n];
        for (int[] r : relations) {
            int u = r[0] - 1;
            int v = r[1] - 1;
            pre[v] |= (1 << u);
        }

        int total = 1 << n;
        int full = total - 1;
        int[] dp = new int[total];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;

        for (int mask = 0; mask < total; mask++) {
            if (dp[mask] >= Integer.MAX_VALUE / 2) {
                continue;
            }

            int available = 0;
            for (int c = 0; c < n; c++) {
                if (((mask >> c) & 1) == 0 && (pre[c] & mask) == pre[c]) {
                    available |= (1 << c);
                }
            }

            List<Integer> choices = new ArrayList<>();
            if (Integer.bitCount(available) <= k) {
                choices.add(available);
            } else {
                for (int sub = available; sub > 0; sub = (sub - 1) & available) {
                    if (Integer.bitCount(sub) == k) {
                        choices.add(sub);
                    }
                }
            }

            for (int take : choices) {
                dp[mask | take] = Math.min(dp[mask | take], dp[mask] + 1);
            }
        }

        return dp[full];
    }
}
```

This is mainly a structural rewrite of Approach 2.

---

# 11. Why Greedy Fails

A tempting strategy is:

> each semester, just take any `k` currently available courses

This is not always optimal.

Why?

Because not all currently available courses are equally valuable. Some unlock deeper dependency chains earlier than others.

You may need to prioritize courses whose completion unlocks bottlenecks.

So a pure greedy local choice can produce extra semesters.

That is why we must search across subsets of available courses.

---

# 12. Worked Example

Consider:

```text
n = 4
relations = [[2,1],[3,1],[1,4]]
k = 2
```

Convert to zero-based:

- `1 <- 2,3`
- `4 <- 1`

Prerequisite masks:

- `pre[0]` (course 1) = bits of courses 2 and 3 = `0110`
- `pre[1]` (course 2) = `0000`
- `pre[2]` (course 3) = `0000`
- `pre[3]` (course 4) = bit of course 1 = `0001`

Start:

```text
mask = 0000
```

Available:

- course 2
- course 3

So:

```text
available = 0110
```

Since bitcount = 2 and `k = 2`, take both:

```text
next = 0110
semester = 1
```

Now from `0110`, available:

- course 1

Take it:

```text
next = 0111
semester = 2
```

Now from `0111`, available:

- course 4

Take it:

```text
next = 1111
semester = 3
```

Done.

---

# 13. Correctness Sketch

We model each completed-course set as a state.

From each state, every legal semester choice corresponds to an edge to a new state with one more semester consumed.

Therefore:

- BFS finds the shortest number of semesters because all edges have equal weight 1.
- DP / memoized DFS are computing the same shortest-path recurrence over the state graph.

By enumerating all legal subsets of size `k` when needed, we guarantee no optimal schedule is missed.

Thus the returned value is the true minimum.

---

# 14. Comparison of Approaches

| Approach                     |             Style |                 Time |    Space | Notes                   |
| ---------------------------- | ----------------: | -------------------: | -------: | ----------------------- |
| BFS on masks                 |     shortest path | `O(2^N * (N + 2^N))` | `O(2^N)` | very intuitive          |
| Bottom-up DP                 |         iterative | `O(2^N * (N + 2^N))` | `O(2^N)` | compact and robust      |
| Top-down DFS + memo          |      recursive DP | `O(2^N * (N + 2^N))` | `O(2^N)` | often easiest to derive |
| DP with explicit choice list | iterative variant |                 same |     same | mostly stylistic        |

---

# 15. Practical Best Choice

For interviews and contest settings, the best balance is usually:

- **Top-down memoized DFS** if you want the most natural formulation
- **Bottom-up DP** if you want an iterative, stack-safe implementation

Both are excellent.

---

# 16. Final Interview Summary

This problem is a classic:

> shortest schedule under prerequisite constraints + small `n`

The key move is to use a bitmask of completed courses.

For each mask:

1. determine which courses are currently available
2. choose up to `k` of them
3. transition to the next mask

Because `n <= 15`, all subsets of courses are manageable.

The answer is the shortest path / minimum DP value from:

```text
mask = 0
```

to:

```text
mask = (1 << n) - 1
```

The subtle but crucial detail is:

- when more than `k` courses are available, enumerate all submasks of size `k`
- greedy selection is not safe

That yields a correct and efficient:

```text
O(2^n * (n + 2^n))
```

solution, which is fully acceptable for the constraints.

---

# 17. Reference Implementation (Recommended)

This is the version I would recommend most often in Java.

```java
import java.util.*;

class Solution {
    private int n;
    private int k;
    private int full;
    private int[] pre;
    private int[] memo;

    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        this.n = n;
        this.k = k;
        this.full = (1 << n) - 1;
        this.pre = new int[n];
        this.memo = new int[1 << n];
        Arrays.fill(memo, -1);

        for (int[] r : relations) {
            int u = r[0] - 1;
            int v = r[1] - 1;
            pre[v] |= (1 << u);
        }

        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == full) {
            return 0;
        }
        if (memo[mask] != -1) {
            return memo[mask];
        }

        int available = 0;
        for (int c = 0; c < n; c++) {
            if (((mask >> c) & 1) == 0 && (pre[c] & mask) == pre[c]) {
                available |= (1 << c);
            }
        }

        int best = Integer.MAX_VALUE / 2;

        if (Integer.bitCount(available) <= k) {
            best = 1 + dfs(mask | available);
        } else {
            for (int sub = available; sub > 0; sub = (sub - 1) & available) {
                if (Integer.bitCount(sub) == k) {
                    best = Math.min(best, 1 + dfs(mask | sub));
                }
            }
        }

        memo[mask] = best;
        return best;
    }
}
```
