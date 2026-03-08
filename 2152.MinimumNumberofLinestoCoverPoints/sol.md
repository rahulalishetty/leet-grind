# Minimum Number of Straight Lines to Cover All Points

## Problem

You are given an array `points` where:

```
points[i] = [xi, yi]
```

represents a point on the **2D X-Y plane**.

You may draw **straight lines** on the plane.

Your goal is to ensure **every point lies on at least one line**.

Return the **minimum number of straight lines** required to cover all points.

---

# Key Observations

1. **Any two points determine exactly one line.**
2. A single line may cover **more than two points** if they are **collinear**.
3. Therefore we want to group as many collinear points as possible into the same line.

This becomes a **minimum line cover problem**.

---

# Strategy

We solve this using **Bitmask Dynamic Programming**.

## Why Bitmask?

If there are `n` points:

```
0 <= n <= small (usually ≤ 10)
```

We can represent which points are already covered using a **bitmask**.

Example for `n = 4`:

```
mask = 0101
```

means points `0` and `2` are already covered.

---

# DP Definition

Let:

```
dp[mask] = minimum lines needed to cover all points NOT yet covered in mask
```

`mask` represents points already covered.

Goal:

```
dp[0]
```

because initially no points are covered.

---

# Transition

For a given mask:

1. Find the **first uncovered point `i`**
2. That point must lie on some line in the optimal solution.

We consider two possibilities:

### Case 1 — Cover point alone

Draw a line that covers only point `i`.

```
newMask = mask | (1 << i)
```

Transition:

```
1 + dp(newMask)
```

---

### Case 2 — Form line using another point

Pick another uncovered point `j`.

The line `(i, j)` is uniquely determined.

Then collect **all points collinear with this line**.

Mark them covered.

```
newMask = mask ∪ all points on line(i,j)
```

Transition:

```
1 + dp(newMask)
```

Take the minimum across all choices.

---

# Collinearity Test

Three points:

```
A(x1,y1)
B(x2,y2)
C(x3,y3)
```

are collinear if:

```
(x2-x1)*(y3-y1) == (y2-y1)*(x3-x1)
```

This avoids floating point errors.

---

# Algorithm

```
function solve(mask):

    if mask == fullMask:
        return 0

    find first uncovered point i

    ans = 1 + solve(mask | (1<<i))

    for each uncovered point j > i:

        compute line(i,j)

        newMask = mask

        for every point k:
            if k lies on this line:
                add k to newMask

        ans = min(ans, 1 + solve(newMask))

    return ans
```

Use **memoization** to avoid recomputation.

---

# Java Implementation

```java
import java.util.Arrays;

class Solution {

    public int minimumLines(int[][] points) {

        int n = points.length;

        if (n <= 2)
            return 1;

        int[] memo = new int[1 << n];
        Arrays.fill(memo, -1);

        return dfs(0, points, memo);
    }

    private int dfs(int mask, int[][] points, int[] memo) {

        int n = points.length;
        int full = (1 << n) - 1;

        if (mask == full)
            return 0;

        if (memo[mask] != -1)
            return memo[mask];

        int i = 0;

        while (((mask >> i) & 1) == 1)
            i++;

        int ans = 1 + dfs(mask | (1 << i), points, memo);

        for (int j = i + 1; j < n; j++) {

            if (((mask >> j) & 1) == 1)
                continue;

            int newMask = mask;

            for (int k = 0; k < n; k++) {

                if (isCollinear(points[i], points[j], points[k])) {
                    newMask |= (1 << k);
                }
            }

            ans = Math.min(ans, 1 + dfs(newMask, points, memo));
        }

        memo[mask] = ans;
        return ans;
    }

    private boolean isCollinear(int[] a, int[] b, int[] c) {

        long x1 = b[0] - a[0];
        long y1 = b[1] - a[1];

        long x2 = c[0] - a[0];
        long y2 = c[1] - a[1];

        return x1 * y2 == y1 * x2;
    }
}
```

---

# Example

Input:

```
points = [[0,0],[1,1],[2,2],[0,1]]
```

Observation:

```
(0,0), (1,1), (2,2) are collinear
``

So we draw:

```

Line 1 → covers 3 diagonal points
Line 2 → covers (0,1)

```

Result:

```

2 lines

```

---

# Complexity Analysis

Let:

```

n = number of points

```

Number of DP states:

```

2^n

```

For each state:

- choose point pairs
- check collinearity across points

Time complexity:

```

O(2^n \* n^2)

```

Worst case often written as:

```

O(2^n \* n^3)

```

Space complexity:

```

O(2^n)

```

for memoization.

---

# Edge Cases

| Points | Result |
|------|------|
| 1 point | 1 |
| 2 points | 1 |
| All points collinear | 1 |
| No three points collinear | ceil(n/2) |

---

# Summary

Key ideas:

1. Represent covered points using **bitmask**
2. Use **DP + memoization**
3. Generate lines using **pairs of points**
4. Expand each line to include **all collinear points**
5. Minimize number of lines used

This converts a geometric problem into a **state compression dynamic programming problem**.
```
