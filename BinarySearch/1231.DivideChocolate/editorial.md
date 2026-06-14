# 1231. Divide Chocolate — Multiple Approaches

---

## Problem Summary

Given a chocolate bar as an array `sweetness[]` and integer `k`:

- Cut into exactly `k + 1` contiguous pieces
- You eat the piece with the **minimum sweetness**
- **Maximize the sweetness you receive**

---

## Approach 1: Binary Search + Greedy (Optimal)

### Intuition

Instead of trying every possible set of cuts, we ask:

> "Can we make cuts such that every piece has sweetness ≥ `x`?"

This decision question has a **monotonic structure**:

- If `x` is achievable → `x - 1` is also achievable
- If `x` is not achievable → `x + 1` is also not achievable

This means we can binary search on the answer `x`.

### Search Space

| Bound   | Value                    | Reason                                             |
| ------- | ------------------------ | -------------------------------------------------- |
| `left`  | `min(sweetness)`         | Smallest possible minimum                          |
| `right` | `sum(sweetness) / (k+1)` | If any piece exceeds this, we can't fit k+1 pieces |

### Greedy Check

For a candidate value `mid`, greedily accumulate chunks:

- When accumulated sweetness ≥ `mid` → cut a piece, reset counter
- Count total pieces made
- If pieces ≥ `k + 1` → `mid` is achievable

### Implementation

```java
class Solution {
    public int maximizeSweetness(int[] sweetness, int k) {
        int numberOfPeople = k + 1;

        int left = Arrays.stream(sweetness).min().getAsInt();
        int right = Arrays.stream(sweetness).sum() / numberOfPeople;

        while (left < right) {
            int mid = (left + right + 1) / 2; // upper-mid to avoid infinite loop

            int curSweetness = 0;
            int pieces = 0;

            for (int s : sweetness) {
                curSweetness += s;
                if (curSweetness >= mid) {
                    pieces++;
                    curSweetness = 0;
                }
            }

            if (pieces >= numberOfPeople) {
                left = mid;   // mid works, try higher
            } else {
                right = mid - 1; // mid too high, go lower
            }
        }

        return left;
    }
}
```

### Why Upper-Mid `(left + right + 1) / 2`?

When updating `left = mid`, if we used lower-mid and `left = right - 1`, then:

```
mid = (left + right) / 2 = left   →  left = mid = left   →  infinite loop
```

Using `(left + right + 1) / 2` ensures `mid` always moves forward.

### Complexity

|       | Complexity                                        |
| ----- | ------------------------------------------------- |
| Time  | `O(n · log(S / (k+1)))` where S = total sweetness |
| Space | `O(1)`                                            |

---

## Approach 2: Brute Force (Enumerate All Cut Positions)

### Intuition

Try every possible way to cut the chocolate bar into `k + 1` pieces. For each valid partition, compute the minimum piece sweetness and track the maximum.

### How It Works

Use **recursion with memoization** or **iterative backtracking**:

- At each chunk, decide whether to cut here or continue
- When exactly `k` cuts are made, record the minimum piece sweetness

### Implementation

```java
class Solution {
    private int[] sweetness;
    private int k;
    private int result = 0;

    public int maximizeSweetness(int[] sweetness, int k) {
        this.sweetness = sweetness;
        this.k = k;
        backtrack(0, 0, k, Integer.MAX_VALUE);
        return result;
    }

    // index    : current chunk index
    // currSum  : sweetness accumulated in current piece
    // cutsLeft : how many more cuts we can make
    // minSoFar : minimum piece sweetness seen so far
    private void backtrack(int index, int currSum, int cutsLeft, int minSoFar) {
        int n = sweetness.length;

        // Base case: used all chunks
        if (index == n) {
            // Last piece includes remaining chunks
            // Only valid if we've made exactly k cuts (no cuts left)
            if (cutsLeft == 0) {
                result = Math.max(result, Math.min(minSoFar, currSum));
            }
            return;
        }

        // Must leave enough chunks for remaining pieces
        if (n - index < cutsLeft + 1) return; // not enough chunks left

        currSum += sweetness[index];

        // Option 1: Cut here (only if cuts remain AND enough chunks ahead)
        if (cutsLeft > 0) {
            backtrack(index + 1, 0, cutsLeft - 1, Math.min(minSoFar, currSum));
        }

        // Option 2: Don't cut here (continue accumulating)
        backtrack(index + 1, currSum, cutsLeft, minSoFar);
    }
}
```

### Dry Run Example

```
sweetness = [1, 2, 3, 4, 5],  k = 2  →  need 3 pieces

Some partitions:
[1] | [2] | [3,4,5]   → min = 1
[1] | [2,3] | [4,5]   → min = 1
[1,2] | [3] | [4,5]   → min = 2  ← candidate
[1,2] | [3,4] | [5]   → min = 2  ← candidate
[1,2,3] | [4] | [5]   → min = 4  ← better
[1,2,3] | [4,5] | ... → invalid, only 2 pieces
...
Best = 5? Let's check [1,2,3,4] | [5] → only 2 pieces with k=2, invalid.
[3,4,5] alone = 12 but need 3 pieces...

Best valid: [1,2,3] | [4] | [5] → min(6,4,5) = 4
```

### Complexity

|       | Complexity                                           |
| ----- | ---------------------------------------------------- |
| Time  | `O(2^n)` — exponential, impractical for large inputs |
| Space | `O(n)` — recursion stack depth                       |

> **Note:** This approach is only feasible for very small inputs (n ≤ 20). It illustrates the problem structure but is not a viable solution.

---

## Approach 3: Dynamic Programming

### Intuition

Define:

```
dp[i][j] = maximum possible minimum sweetness
           when cutting the first i chunks into j pieces
```

### Recurrence

For each possible last cut position `p` (where the j-th piece starts at chunk `p+1`):

```
dp[i][j] = max over all p < i of:
               min( dp[p][j-1],  sum(sweetness[p+1 .. i]) )
```

In plain English: the j-th piece is `sweetness[p+1..i]`. We want to maximize the minimum across all pieces.

### Base Case

```
dp[i][1] = sum(sweetness[0..i-1])   for all i ≥ 1
           (one piece = just take everything up to i)
```

### Answer

```
dp[n][k+1]
```

### Implementation

```java
class Solution {
    private int[][] memo;
    private int[] prefix;
    private int n;

    public int maximizeSweetness(int[] sweetness, int k) {
        n = sweetness.length;
        int pieces = k + 1;

        prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + sweetness[i];
        }

        // memo[i][j] = max-min sweetness of first i elements split into j pieces
        memo = new int[n + 1][pieces + 1];
        for (int[] row : memo) Arrays.fill(row, -1);

        return solve(n, pieces);
    }

    // Returns: best possible min-piece-sweetness when splitting
    //          sweetness[0..i-1] into exactly j pieces
    private int solve(int i, int j) {
        // Base case: exactly 1 piece → only option is the whole prefix sum
        if (j == 1) return prefix[i];

        // Need at least j elements to form j pieces
        if (i < j) return 0;

        if (memo[i][j] != -1) return memo[i][j];

        int best = 0;

        // Try every possible start p of the last piece: sweetness[p..i-1]
        for (int p = j - 1; p < i; p++) {
            int lastPiece = prefix[i] - prefix[p];

            // The min sweetness of this split = min(best split of first p into j-1, lastPiece)
            int candidate = Math.min(solve(p, j - 1), lastPiece);

            best = Math.max(best, candidate);
        }

        return memo[i][j] = best;
    }
}

class Solution {
    public int maximizeSweetness(int[] sweetness, int k) {
        int n = sweetness.length;
        int pieces = k + 1;

        // prefix[i] = sum of sweetness[0..i-1]
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + sweetness[i];
        }

        // dp[i][j] = max-min sweetness using first i chunks split into j pieces
        int[][] dp = new int[n + 1][pieces + 1];

        // Base: 1 piece from first i chunks = total sum of those chunks
        for (int i = 1; i <= n; i++) {
            dp[i][1] = prefix[i];
        }

        for (int j = 2; j <= pieces; j++) {
            for (int i = j; i <= n; i++) { // need at least j chunks for j pieces
                for (int p = j - 1; p < i; p++) { // last piece is sweetness[p..i-1]
                    int lastPiece = prefix[i] - prefix[p];
                    dp[i][j] = Math.max(dp[i][j], Math.min(dp[p][j - 1], lastPiece));
                }
            }
        }

        return dp[n][pieces];
    }
}
```

### Visualization

For `sweetness = [1, 2, 3, 4, 5]`, `k = 2` (3 pieces):

```
prefix = [0, 1, 3, 6, 10, 15]

dp[i][1] = prefix[i]
dp[1][1]=1, dp[2][1]=3, dp[3][1]=6, dp[4][1]=10, dp[5][1]=15

dp[i][2]: best 2-piece split of first i chunks
dp[2][2] = min(dp[1][1], sum[1..1]) = min(1,2) = 1
dp[3][2] = max(
    min(dp[1][1], sum[1..2]) = min(1,5) = 1,
    min(dp[2][1], sum[2..2]) = min(3,3) = 3
) = 3
dp[4][2] = max(min(1,9),min(3,6),min(6,4)) = max(1,3,4) = 4
dp[5][2] = max(min(1,14),min(3,12),min(6,9),min(10,5)) = max(1,3,6,5) = 6

dp[i][3]: best 3-piece split
dp[3][3] = min(dp[2][2], sum[2..2]) = min(1,3) = 1
dp[4][3] = max(min(dp[2][2],sum[2..3]),min(dp[3][2],sum[3..3]))
         = max(min(1,7), min(3,4)) = max(1,3) = 3
dp[5][3] = max(
    min(dp[2][2], sum[2..4]) = min(1,12) = 1,
    min(dp[3][2], sum[3..4]) = min(3,9)  = 3,
    min(dp[4][2], sum[4..4]) = min(4,5)  = 4   ← best
) = 4

Answer = dp[5][3] = 4  ✓
```

### Complexity

|       | Complexity                       |
| ----- | -------------------------------- |
| Time  | `O(n² · k)` — three nested loops |
| Space | `O(n · k)` — the DP table        |

---

## Approach 4: DP with Space Optimization

### Intuition

Notice that in the DP recurrence:

```
dp[i][j] depends only on dp[p][j-1]  (previous layer j-1)
```

We only need **two 1D arrays**: previous layer and current layer.

### Implementation

```java
class Solution {
    public int maximizeSweetness(int[] sweetness, int k) {
        int n = sweetness.length;
        int pieces = k + 1;

        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + sweetness[i];
        }

        // prev[i] = best result for (i chunks, j-1 pieces)
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        // Base: 1 piece
        for (int i = 1; i <= n; i++) {
            prev[i] = prefix[i];
        }

        for (int j = 2; j <= pieces; j++) {
            Arrays.fill(curr, 0);
            for (int i = j; i <= n; i++) {
                for (int p = j - 1; p < i; p++) {
                    int lastPiece = prefix[i] - prefix[p];
                    curr[i] = Math.max(curr[i], Math.min(prev[p], lastPiece));
                }
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }
}
```

### Complexity

|       | Complexity                    |
| ----- | ----------------------------- |
| Time  | `O(n² · k)` — same as full DP |
| Space | `O(n)` — only two 1D arrays   |

---

## Approach Comparison

| Approach               | Time              | Space      | Practical? | Notes                  |
| ---------------------- | ----------------- | ---------- | ---------- | ---------------------- |
| Brute Force            | `O(2^n)`          | `O(n)`     | ❌ No      | Good for understanding |
| DP (full table)        | `O(n² · k)`       | `O(n · k)` | ⚠️ Partial | Works for small n, k   |
| DP (space opt.)        | `O(n² · k)`       | `O(n)`     | ⚠️ Partial | Better memory          |
| Binary Search + Greedy | `O(n · log(S/k))` | `O(1)`     | ✅ Yes     | **Best approach**      |

For LeetCode constraints (`n ≤ 10^4`, `sweetness[i] ≤ 10^5`):

- Binary Search runs in roughly `10^4 × 17 ≈ 170,000` operations
- DP runs in roughly `10^8 × k` — too slow

---

## Key Insight: Why Binary Search Works Here

The core observation that enables the optimal solution:

```
If we can achieve minimum sweetness = x,
then we can certainly achieve minimum sweetness = x - 1
(just use the same cuts).
```

This gives us a **monotone predicate** — all achievable values form a contiguous prefix of the search space. Whenever you see:

> "Maximize/minimize a value subject to a feasibility condition that is monotone"

Think **Binary Search on the Answer**.

---

## Recommended Solution

**Use Approach 1: Binary Search + Greedy.**

It is:

- Optimal in both time and space
- Easy to implement once the insight is clear
- A classic pattern applicable to many similar problems:
  - LC 410 — Split Array Largest Sum
  - LC 875 — Koko Eating Bananas
  - LC 1011 — Capacity to Ship Packages
  - LC 774 — Minimize Max Distance to Gas Station
