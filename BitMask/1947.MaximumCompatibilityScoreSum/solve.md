# 1947. Maximum Compatibility Score Sum — Exhaustive Java Notes

## Problem Statement

There are:

- `m` students
- `m` mentors

Each person answered `n` yes/no questions, where every answer is either:

- `0`
- `1`

You are given:

- `students[i]` = answers of student `i`
- `mentors[j]` = answers of mentor `j`

You must assign:

- each student to exactly one mentor
- each mentor to exactly one student

The **compatibility score** of a student-mentor pair is the number of positions where their answers are equal.

Your goal is to maximize the total compatibility score over all pairings.

Return the maximum possible total score.

---

## Example 1

```text
Input:
students = [[1,1,0],[1,0,1],[0,0,1]]
mentors  = [[1,0,0],[0,0,1],[1,1,0]]

Output:
8
```

One optimal assignment:

- student 0 → mentor 2, score = 3
- student 1 → mentor 0, score = 2
- student 2 → mentor 1, score = 3

Total:

```text
3 + 2 + 3 = 8
```

---

## Example 2

```text
Input:
students = [[0,0],[0,0],[0,0]]
mentors  = [[1,1],[1,1],[1,1]]

Output:
0
```

Every answer differs, so every pair score is `0`.

---

## Constraints

```text
1 <= m, n <= 8
```

This is the crucial clue.

Because `m <= 8`, exponential solutions over mentor-assignment states are feasible.

---

# 1. Core Insight

This is a classic **assignment / matching** problem.

We want to match `m` students with `m` mentors to maximize total pair score.

If `m` were large, this would be a weighted bipartite matching problem. But here `m <= 8`, so a much simpler and more direct solution is possible:

- backtracking over assignments
- bitmask DP over used mentors
- top-down memoization or bottom-up DP

The key optimization is to precompute the compatibility score for every `(student, mentor)` pair.

---

# 2. Precompute Pair Scores

Before doing DP or backtracking, compute:

```text
score[i][j] = compatibility score between student i and mentor j
```

To compute it:

- compare the `n` answers
- count positions where they match

This preprocessing costs:

```text
O(m * m * n)
```

which is tiny because `m, n <= 8`.

---

# 3. Approach 1 — Backtracking over Mentor Assignments

## Idea

Assign students one by one.

For the current student `i`, try pairing them with any unused mentor `j`.

Track:

- which mentors are already used
- current total score
- best score seen so far

This explores all permutations of mentor assignments.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int m;
    private int[][] score;
    private int best;

    public int maxCompatibilitySum(int[][] students, int[][] mentors) {
        m = students.length;
        score = new int[m][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                score[i][j] = compatibility(students[i], mentors[j]);
            }
        }

        best = 0;
        boolean[] used = new boolean[m];
        dfs(0, 0, used);
        return best;
    }

    private void dfs(int studentIdx, int current, boolean[] used) {
        if (studentIdx == m) {
            best = Math.max(best, current);
            return;
        }

        for (int mentorIdx = 0; mentorIdx < m; mentorIdx++) {
            if (!used[mentorIdx]) {
                used[mentorIdx] = true;
                dfs(studentIdx + 1, current + score[studentIdx][mentorIdx], used);
                used[mentorIdx] = false;
            }
        }
    }

    private int compatibility(int[] a, int[] b) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) c++;
        }
        return c;
    }
}
```

---

## Complexity

There are `m!` possible assignments.

For each recursive branch we do `O(1)` transition work after preprocessing.

So time complexity is roughly:

```text
O(m! + m^2 * n)
```

Since `m <= 8`, this is fine.

Space complexity:

```text
O(m)
```

for recursion stack and used array.

---

# 4. Approach 2 — Backtracking with Pruning

## Idea

The plain backtracking solution works, but we can prune.

One simple pruning technique:

- compute an optimistic upper bound on how much score can still be added
- if `current + upperBound <= best`, stop exploring that branch

A very usable upper bound is:

For each remaining student, assume they can still get their best possible mentor score, regardless of conflicts.

This upper bound may be loose, but it is enough to prune many branches.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int m;
    private int[][] score;
    private int[] maxPerStudent;
    private int best;

    public int maxCompatibilitySum(int[][] students, int[][] mentors) {
        m = students.length;
        score = new int[m][m];
        maxPerStudent = new int[m];

        for (int i = 0; i < m; i++) {
            int rowBest = 0;
            for (int j = 0; j < m; j++) {
                score[i][j] = compatibility(students[i], mentors[j]);
                rowBest = Math.max(rowBest, score[i][j]);
            }
            maxPerStudent[i] = rowBest;
        }

        best = 0;
        boolean[] used = new boolean[m];
        dfs(0, 0, used);
        return best;
    }

    private void dfs(int studentIdx, int current, boolean[] used) {
        if (studentIdx == m) {
            best = Math.max(best, current);
            return;
        }

        int optimistic = current;
        for (int i = studentIdx; i < m; i++) {
            optimistic += maxPerStudent[i];
        }
        if (optimistic <= best) {
            return;
        }

        for (int mentorIdx = 0; mentorIdx < m; mentorIdx++) {
            if (!used[mentorIdx]) {
                used[mentorIdx] = true;
                dfs(studentIdx + 1, current + score[studentIdx][mentorIdx], used);
                used[mentorIdx] = false;
            }
        }
    }

    private int compatibility(int[] a, int[] b) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) c++;
        }
        return c;
    }
}
```

---

## Complexity

Worst-case is still factorial:

```text
O(m!)
```

but pruning often makes it much faster in practice.

Space remains:

```text
O(m)
```

---

# 5. Approach 3 — Top-Down Bitmask DP (Most Standard)

## Idea

This is the cleanest and most standard solution.

Let:

```text
dfs(mask)
```

be the maximum compatibility score we can get after assigning some prefix of students, where `mask` tells us which mentors are already used.

If `bitCount(mask) = i`, then we are about to assign student `i`.

For each unused mentor `j`:

```text
dfs(mask) = max(score[i][j] + dfs(mask | (1 << j)))
```

Memoize by `mask`.

---

## Why This Works

Once the used mentors are known, the next student index is uniquely determined by how many mentors are already used.

So the entire state is captured by the single bitmask.

That gives only:

```text
2^m
```

states.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int m;
    private int[][] score;
    private int[] memo;

    public int maxCompatibilitySum(int[][] students, int[][] mentors) {
        m = students.length;
        score = new int[m][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                score[i][j] = compatibility(students[i], mentors[j]);
            }
        }

        memo = new int[1 << m];
        Arrays.fill(memo, -1);
        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == (1 << m) - 1) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int studentIdx = Integer.bitCount(mask);
        int best = 0;

        for (int mentorIdx = 0; mentorIdx < m; mentorIdx++) {
            if (((mask >> mentorIdx) & 1) == 0) {
                best = Math.max(
                    best,
                    score[studentIdx][mentorIdx] + dfs(mask | (1 << mentorIdx))
                );
            }
        }

        memo[mask] = best;
        return best;
    }

    private int compatibility(int[] a, int[] b) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) c++;
        }
        return c;
    }
}
```

---

## Complexity

There are `2^m` masks.

For each mask, we try at most `m` mentors.

So:

```text
O(m * 2^m + m^2 * n)
```

The `m^2 * n` comes from score preprocessing.

With `m <= 8`, this is extremely fast.

Space:

```text
O(2^m)
```

---

# 6. Approach 4 — Bottom-Up Bitmask DP

## Idea

We can compute the same DP iteratively.

Let:

```text
dp[mask] = maximum compatibility score after using the mentors in mask
```

If `bitCount(mask) = i`, then we have already assigned students `0..i-1`, and next we assign student `i`.

Transition:

```text
dp[mask | (1 << j)] = max(dp[mask | (1 << j)], dp[mask] + score[i][j])
```

for every unused mentor `j`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maxCompatibilitySum(int[][] students, int[][] mentors) {
        int m = students.length;
        int[][] score = new int[m][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                int c = 0;
                for (int k = 0; k < students[i].length; k++) {
                    if (students[i][k] == mentors[j][k]) c++;
                }
                score[i][j] = c;
            }
        }

        int totalMasks = 1 << m;
        int[] dp = new int[totalMasks];

        for (int mask = 0; mask < totalMasks; mask++) {
            int studentIdx = Integer.bitCount(mask);
            if (studentIdx == m) continue;

            for (int mentorIdx = 0; mentorIdx < m; mentorIdx++) {
                if (((mask >> mentorIdx) & 1) == 0) {
                    int nextMask = mask | (1 << mentorIdx);
                    dp[nextMask] = Math.max(dp[nextMask], dp[mask] + score[studentIdx][mentorIdx]);
                }
            }
        }

        return dp[totalMasks - 1];
    }
}
```

---

## Complexity

Same asymptotic complexity as top-down bitmask DP:

```text
O(m * 2^m + m^2 * n)
```

Space:

```text
O(2^m)
```

---

# 7. Approach 5 — Permutation Enumeration

## Idea

Since `m <= 8`, another direct option is:

1. generate all permutations of mentor indices,
2. compute the total score for that assignment,
3. keep the maximum.

This is conceptually simple, but inferior to bitmask DP.

Still, it is useful as a baseline.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[][] score;
    private int m;
    private int best;

    public int maxCompatibilitySum(int[][] students, int[][] mentors) {
        m = students.length;
        score = new int[m][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                score[i][j] = compatibility(students[i], mentors[j]);
            }
        }

        int[] perm = new int[m];
        for (int i = 0; i < m; i++) perm[i] = i;

        best = 0;
        permute(perm, 0);
        return best;
    }

    private void permute(int[] perm, int idx) {
        if (idx == m) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                sum += score[i][perm[i]];
            }
            best = Math.max(best, sum);
            return;
        }

        for (int i = idx; i < m; i++) {
            swap(perm, idx, i);
            permute(perm, idx + 1);
            swap(perm, idx, i);
        }
    }

    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private int compatibility(int[] a, int[] b) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) c++;
        }
        return c;
    }
}
```

---

## Complexity

There are:

```text
m!
```

mentor permutations.

For each permutation, we may compute assignment score in `O(m)`.

So:

```text
O(m! * m + m^2 * n)
```

This passes for `m <= 8`, but bitmask DP is cleaner and better.

---

# 8. Why Bitmask DP Is Better Than Permutations

Permutation enumeration explores full orderings directly.

Bitmask DP merges many equivalent prefixes into a single state.

For example, if the same set of mentors has already been used, the future only depends on that set — not on the exact order in which they were chosen.

This is the whole reason bitmask DP reduces:

```text
m!
```

to:

```text
2^m
```

states.

That is a huge improvement.

---

# 9. Small Worked Example

Take:

```text
students = [[1,1,0],[1,0,1],[0,0,1]]
mentors  = [[1,0,0],[0,0,1],[1,1,0]]
```

First precompute scores:

- student 0 vs mentor 0 = 2
- student 0 vs mentor 1 = 1
- student 0 vs mentor 2 = 3

- student 1 vs mentor 0 = 2
- student 1 vs mentor 1 = 2
- student 1 vs mentor 2 = 1

- student 2 vs mentor 0 = 2
- student 2 vs mentor 1 = 3
- student 2 vs mentor 2 = 0

So score matrix:

```text
[2,1,3]
[2,2,1]
[2,3,0]
```

Now the best assignment is:

- student 0 → mentor 2 = 3
- student 1 → mentor 0 = 2
- student 2 → mentor 1 = 3

Total:

```text
8
```

---

# 10. Why Greedy Fails

A tempting strategy is:

> for each student, assign the currently best unused mentor

This is not safe.

A mentor that looks slightly suboptimal for the current student may be the only excellent option for a later student.

This is a standard assignment problem issue: local greedy choices do not preserve global optimality.

That is exactly why we need DP or exhaustive search.

---

# 11. Correctness Sketch for Bitmask DP

At state `mask`, exactly `bitCount(mask)` students have already been assigned.

So the next student index is uniquely determined.

For that student, every valid full assignment must choose one currently unused mentor.
For each such mentor choice, the problem reduces to the same form on a larger mask.

Thus:

- every valid pairing corresponds to a path in the DP,
- every DP path corresponds to a valid pairing,
- maximizing over all transitions yields the optimal score.

Memoization ensures each state is solved once.

Therefore the DP is correct.

---

# 12. Comparison of Approaches

| Approach                | Main Idea                       |                       Time |    Space | Notes                 |
| ----------------------- | ------------------------------- | -------------------------: | -------: | --------------------- |
| Backtracking            | assign mentors recursively      |                    `O(m!)` |   `O(m)` | simple baseline       |
| Backtracking + pruning  | recursive with optimistic bound | still factorial worst-case |   `O(m)` | practical speedup     |
| Top-down bitmask DP     | DFS on used mentors             |               `O(m * 2^m)` | `O(2^m)` | best overall          |
| Bottom-up bitmask DP    | iterative subset DP             |               `O(m * 2^m)` | `O(2^m)` | recursion-free        |
| Permutation enumeration | try all mentor orders           |                `O(m! * m)` |   `O(m)` | educational, not best |

---

# 13. Recommended Approach

For this problem, the best solution is clearly:

## Top-Down Bitmask DP

Why?

- small state space,
- very easy recurrence,
- optimal complexity,
- clean code.

The bottom-up version is equally good if you prefer iterative DP.

---

# 14. Recommended Java Solution

```java
import java.util.*;

class Solution {
    private int m;
    private int[][] score;
    private int[] memo;

    public int maxCompatibilitySum(int[][] students, int[][] mentors) {
        m = students.length;
        score = new int[m][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                score[i][j] = compatibility(students[i], mentors[j]);
            }
        }

        memo = new int[1 << m];
        Arrays.fill(memo, -1);
        return dfs(0);
    }

    private int dfs(int mask) {
        if (mask == (1 << m) - 1) {
            return 0;
        }

        if (memo[mask] != -1) {
            return memo[mask];
        }

        int studentIdx = Integer.bitCount(mask);
        int best = 0;

        for (int mentorIdx = 0; mentorIdx < m; mentorIdx++) {
            if (((mask >> mentorIdx) & 1) == 0) {
                best = Math.max(
                    best,
                    score[studentIdx][mentorIdx] + dfs(mask | (1 << mentorIdx))
                );
            }
        }

        memo[mask] = best;
        return best;
    }

    private int compatibility(int[] a, int[] b) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) c++;
        }
        return c;
    }
}
```

---

# 15. Final Takeaway

This problem is a textbook case of:

- small matching size (`m <= 8`)
- pairwise score matrix
- one-to-one assignment

That almost always points to:

```text
bitmask DP
```

The decisive observation is:

> once we know which mentors are already used, we automatically know which student must be assigned next

That collapses the problem into a DP over subsets of mentors, yielding:

```text
O(m * 2^m)
```

which is tiny for `m <= 8`.
