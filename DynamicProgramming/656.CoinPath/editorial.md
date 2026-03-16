# Coin Path / Cheapest Jump — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

The problem discussed here is the classic **Coin Path / Cheapest Jump** problem.

You are given:

- an array `A`, where `A[i]` is the cost of stepping on index `i`
- a maximum jump length `B`

You start at index `0` and want to reach the last index.

Rules:

- from index `i`, you may jump to any index `j` such that `i < j <= i + B`
- if `A[j] < 0`, that index is blocked and cannot be used
- among all valid paths to the end, return the path with **minimum total cost**
- if no valid path exists, return an empty list
- the returned path is usually expressed using **1-based indices**

The explanations below focus on how to compute the cheapest path and reconstruct it using a `next` array.

---

# Core Idea

For each index `i`, we want to know:

> What is the minimum cost to reach the end if we start from `i`?

That naturally suggests defining a subproblem for each index.

If we also remember **which next index gives the minimum cost**, then we can reconstruct the full path at the end.

This is why all approaches here use a `next` array:

```text
next[i] = the next index to jump to from i on the optimal path
```

Once `next` is known, we can start from `0` and repeatedly follow:

```text
i = next[i]
```

to build the answer.

---

# Approach 1: Brute Force

## Intuition

The brute force approach tries every possible jump sequence recursively.

Suppose we are currently at index `i`.

Then the next jump can go to any reachable index:

```text
i + 1, i + 2, ..., i + B
```

as long as:

- it is still within bounds
- the destination is not blocked (`A[j] >= 0`)

For each possible next index `j`, compute:

```text
A[i] + cost of the best path starting from j
```

and choose the minimum among them.

At the same time, record in `next[i]` which `j` gave the best result.

This is correct, but it is extremely slow because the same indices are recomputed through many different recursive paths.

---

## Algorithm

1. Create a `next` array initialized to `-1`
2. Define a recursive function:

```text
jump(A, B, i, next)
```

which returns the minimum cost to reach the end starting from index `i` 3. Base case:

- if `i` is the last index and it is valid, return `A[i]`

4. Otherwise:
   - try every `j` from `i + 1` to `i + B`
   - if `A[j] >= 0`, compute:
     ```text
     cost = A[i] + jump(A, B, j, next)
     ```
   - keep the minimum cost
   - store the best `j` in `next[i]`
5. After recursion finishes, reconstruct the path using `next`
6. If reconstruction reaches the last index, return the path
7. Otherwise, return an empty list

---

## Java Implementation

```java
public class Solution {
    public List<Integer> cheapestJump(int[] A, int B) {
        int[] next = new int[A.length];
        Arrays.fill(next, -1);

        jump(A, B, 0, next);

        List<Integer> res = new ArrayList();
        int i;
        for (i = 0; i < A.length && next[i] > 0; i = next[i])
            res.add(i + 1);

        if (i == A.length - 1 && A[i] >= 0)
            res.add(A.length);
        else
            return new ArrayList<Integer>();

        return res;
    }

    public long jump(int[] A, int B, int i, int[] next) {
        if (i == A.length - 1 && A[i] >= 0)
            return A[i];

        long min_cost = Integer.MAX_VALUE;

        for (int j = i + 1; j <= i + B && j < A.length; j++) {
            if (A[j] >= 0) {
                long cost = A[i] + jump(A, B, j, next);
                if (cost < min_cost) {
                    min_cost = cost;
                    next[i] = j;
                }
            }
        }
        return min_cost;
    }
}
```

---

## Why It Is Slow

The recursion explores all possible jump paths.

For example, if many indices are reachable from each position, then each call may branch into up to `B` recursive calls.

Worse, the same suffix problem can be recomputed many times.

For instance, the minimum cost from index `7` may be computed from multiple earlier indices independently.

That repeated work causes a combinatorial explosion.

---

## Complexity Analysis

Let:

- `n` = length of array `A`
- `B` = maximum jump distance

### Time Complexity

At each position, there can be up to `B` recursive branches.

In the worst case, the recursion tree can grow exponentially.

The provided explanation gives:

```text
O(B^n)
```

which captures the idea that every level can branch up to `B` times.

### Space Complexity

The recursion depth can go up to `n`, and `next` has size `n`:

```text
O(n)
```

---

# Approach 2: Using Memoization

## Intuition

The brute force recursion is slow because the same state is recomputed many times.

But notice:

> The minimum cost starting from index `i` depends only on `i`, not on how we reached `i`.

So we can store the answer for each index after computing it once.

Let:

```text
memo[i]
```

store:

> the minimum cost to reach the end starting from index `i`

Then every recursive state is solved only once.

This is standard **top-down dynamic programming** with memoization.

---

## Algorithm

1. Create:
   - `next[]`, initialized to `-1`
   - `memo[]`, initialized to `0` or a sentinel
2. Define recursive `jump(A, B, i, next, memo)`
3. If `memo[i]` is already computed, return it immediately
4. If `i` is the last valid index, return `A[i]`
5. Otherwise:
   - try all `j` in `[i + 1, i + B]`
   - if `A[j] >= 0`, compute:
     ```text
     A[i] + jump(A, B, j, next, memo)
     ```
   - keep the minimum
   - update `next[i]`
6. Store the result in `memo[i]`
7. Reconstruct the path using `next`

---

## Java Implementation

```java
public class Solution {
    public List<Integer> cheapestJump(int[] A, int B) {
        int[] next = new int[A.length];
        Arrays.fill(next, -1);

        long[] memo = new long[A.length];
        jump(A, B, 0, next, memo);

        List<Integer> res = new ArrayList();
        int i;
        for (i = 0; i < A.length && next[i] > 0; i = next[i])
            res.add(i + 1);

        if (i == A.length - 1 && A[i] >= 0)
            res.add(A.length);
        else
            return new ArrayList<Integer>();

        return res;
    }

    public long jump(int[] A, int B, int i, int[] next, long[] memo) {
        if (memo[i] > 0)
            return memo[i];

        if (i == A.length - 1 && A[i] >= 0)
            return A[i];

        long min_cost = Integer.MAX_VALUE;

        for (int j = i + 1; j <= i + B && j < A.length; j++) {
            if (A[j] >= 0) {
                long cost = A[i] + jump(A, B, j, next, memo);
                if (cost < min_cost) {
                    min_cost = cost;
                    next[i] = j;
                }
            }
        }

        memo[i] = min_cost;
        return memo[i];
    }
}
```

---

## Why Memoization Helps

Without memoization, index `i` may be solved repeatedly from different recursive branches.

With memoization, once `jump(i)` is computed, every future request for `jump(i)` returns in `O(1)` time.

So instead of exploring an exponential-size recursion tree, we only solve each index once.

---

## Complexity Analysis

### Time Complexity

There are `n` indices.

For each index `i`, we may inspect up to `B` next indices.

So total:

```text
O(nB)
```

### Space Complexity

We store:

- `memo` of size `n`
- `next` of size `n`
- recursion stack up to `n`

So total:

```text
O(n)
```

---

# Approach 3: Using Dynamic Programming

## Intuition

From the memoized solution, we can observe that the cost from index `i` depends only on later indices.

That means the problem has a natural **bottom-up** dynamic programming structure.

Let:

```text
dp[i]
```

represent:

> the minimum cost to reach the end starting from index `i`

Then `dp[i]` depends on `dp[j]` for indices `j > i`.

So we can compute the answer from right to left.

At the same time, we store in `next[i]` the best next jump.

---

## Transition

For each index `i`, consider every reachable next index:

```text
j in [i + 1, i + B]
```

If `A[j] >= 0`, then going from `i` to `j` costs:

```text
A[i] + dp[j]
```

Choose the `j` that minimizes this expression.

Set:

```text
dp[i] = minimum such cost
next[i] = corresponding j
```

---

## Filling Order

We start from the end of the array and move backward, because:

- the last index is the simplest state
- earlier indices depend on later indices

So the DP is filled from:

```text
n - 1 down to 0
```

---

## Java Implementation

```java
public class Solution {
    public List<Integer> cheapestJump(int[] A, int B) {
        int[] next = new int[A.length];
        long[] dp = new long[A.length];
        Arrays.fill(next, -1);

        List<Integer> res = new ArrayList();

        for (int i = A.length - 2; i >= 0; i--) {
            long min_cost = Integer.MAX_VALUE;

            for (int j = i + 1; j <= i + B && j < A.length; j++) {
                if (A[j] >= 0) {
                    long cost = A[i] + dp[j];
                    if (cost < min_cost) {
                        min_cost = cost;
                        next[i] = j;
                    }
                }
            }

            dp[i] = min_cost;
        }

        int i;
        for (i = 0; i < A.length && next[i] > 0; i = next[i])
            res.add(i + 1);

        if (i == A.length - 1 && A[i] >= 0)
            res.add(A.length);
        else
            return new ArrayList<Integer>();

        return res;
    }
}
```

---

## Why This Works

The recurrence is the same as in memoization, but computed iteratively.

Since every `dp[i]` depends only on later values `dp[j]`, once those later values are already known, `dp[i]` can be computed directly.

The `next` array again stores the choice that leads to the best result.

Then path reconstruction is straightforward.

---

## Complexity Analysis

### Time Complexity

For each of the `n` indices, we may consider up to `B` jump options:

```text
O(nB)
```

### Space Complexity

We store:

- `dp` of size `n`
- `next` of size `n`

So:

```text
O(n)
```

---

# Path Reconstruction

All three approaches above use the same basic reconstruction idea.

After computing the optimal next jump for each index, we build the path by following:

```text
0 -> next[0] -> next[next[0]] -> ...
```

At each visited index `i`, append:

```text
i + 1
```

to the result, because the required output uses **1-based indexing**.

If this process successfully reaches the last index, the path is valid.

Otherwise, no valid path exists and the answer is an empty list.

---

# Why `next` Is Important

The DP or recursion computes the **cost**, but not automatically the actual path.

So whenever we find a better next jump from `i` to `j`, we save:

```text
next[i] = j
```

This is exactly like storing parent pointers in shortest-path reconstruction problems.

---

# Comparison of Approaches

| Approach     | Main Idea                            | Time Complexity | Space Complexity |
| ------------ | ------------------------------------ | --------------: | ---------------: |
| Brute Force  | Try all jump paths recursively       |     exponential |           `O(n)` |
| Memoization  | Cache minimum cost from each index   |         `O(nB)` |           `O(n)` |
| Bottom-Up DP | Compute best cost from right to left |         `O(nB)` |           `O(n)` |

---

# Key Takeaways

## 1. The natural state is the index

The subproblem is:

> What is the minimum cost to reach the end starting from index `i`?

That makes this a one-dimensional DP problem.

## 2. Brute force repeats work

The same suffix of the path can be explored from many earlier starting indices.

## 3. Memoization removes redundancy

By caching `jump(i)`, each index is solved only once.

## 4. Bottom-up DP is the iterative version

It uses the same recurrence as memoization, just filled from right to left.

## 5. `next` reconstructs the optimal path

Without storing `next`, we would know only the optimal cost, not the actual path.

---

# Final Insight

This problem is fundamentally a shortest-path-style dynamic programming problem on an array.

Every index is a state, every allowed jump is an edge, and the goal is to minimize total path cost.

That is why:

- brute force explores all paths
- memoization caches the best suffix cost
- bottom-up DP computes the same values iteratively

Among these, the **bottom-up DP** solution is usually the cleanest practical approach: it is efficient, avoids recursion depth issues, and reconstructs the path neatly using the `next` array.
