# Maximal Square (LeetCode 221) — Exhaustive Java Approaches

## Problem Restatement

You are given an `m x n` binary matrix containing only `'0'` and `'1'`.

Goal:

- Find the **largest square** consisting only of `'1'`
- Return its **area**

Important:

- The answer is **area**, not side length

---

## Core Observation

To form a square ending at cell `(i, j)`, three neighboring positions matter:

- top: `(i - 1, j)`
- left: `(i, j - 1)`
- top-left diagonal: `(i - 1, j - 1)`

Why?

Because if `(i, j)` is the bottom-right corner of a square of side `k`, then:

- above it must support a square of at least `k - 1`
- left of it must support a square of at least `k - 1`
- diagonal top-left must also support a square of at least `k - 1`

So the limiting factor is the **minimum** of those three.

This leads to the classic recurrence:

```text
if matrix[i][j] == '1':
    dp[i][j] = 1 + min(
        dp[i - 1][j],     // top
        dp[i][j - 1],     // left
        dp[i - 1][j - 1]  // diagonal
    )
else:
    dp[i][j] = 0
```

The largest value in `dp` gives the **maximum side length**.
Answer = `maxSide * maxSide`

---

# Approach 1: Brute Force Expansion from Every Cell

## Idea

Treat every cell as a possible **top-left corner** of a square.

If the cell contains `'1'`, try to expand the square one layer at a time:

- side length `1`
- then `2`
- then `3`
- and so on

For each attempted expansion, verify whether the newly included row and column are all `'1'`.

---

## How It Works

For every `(i, j)`:

1. If `matrix[i][j] == '0'`, skip
2. Otherwise start with side length `1`
3. Keep expanding while:
   - square remains within bounds
   - new bottom row is all `'1'`
   - new right column is all `'1'`

Track the maximum side found.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int maxSide = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    int side = 1;
                    maxSide = Math.max(maxSide, side);

                    boolean canExpand = true;
                    while (i + side < rows && j + side < cols && canExpand) {
                        for (int k = j; k <= j + side; k++) {
                            if (matrix[i + side][k] == '0') {
                                canExpand = false;
                                break;
                            }
                        }

                        for (int k = i; k <= i + side; k++) {
                            if (matrix[k][j + side] == '0') {
                                canExpand = false;
                                break;
                            }
                        }

                        if (canExpand) {
                            side++;
                            maxSide = Math.max(maxSide, side);
                        }
                    }
                }
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Complexity

### Time Complexity

- Worst case: **O(m _ n _ min(m, n)^2)**

Reason:

- For each cell, you may try many side lengths
- Each expansion may scan a row and a column

### Space Complexity

- **O(1)**

---

## Pros

- Very intuitive
- Easy to visualize

## Cons

- Too slow for large matrices
- Repeats a lot of work

---

# Approach 2: Brute Force with Full Square Validation

## Idea

Again treat each cell as a possible top-left corner.

But now, for every possible side length, validate the **entire square**.

This is conceptually even simpler, though usually worse.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int maxSide = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int side = 1; i + side <= rows && j + side <= cols; side++) {
                    if (isAllOnes(matrix, i, j, side)) {
                        maxSide = Math.max(maxSide, side);
                    }
                }
            }
        }

        return maxSide * maxSide;
    }

    private boolean isAllOnes(char[][] matrix, int row, int col, int side) {
        for (int i = row; i < row + side; i++) {
            for (int j = col; j < col + side; j++) {
                if (matrix[i][j] == '0') {
                    return false;
                }
            }
        }
        return true;
    }
}
```

---

## Complexity

### Time Complexity

- Roughly **O(m _ n _ min(m, n)^3)** in the worst case

Reason:

- Try many top-left cells
- Try many side lengths
- Validate full square each time

### Space Complexity

- **O(1)**

---

## Why Learn This?

Only to understand why dynamic programming is needed.

This approach is too expensive.

---

# Approach 3: Top-Down DP (Memoization)

## Idea

Define:

- `solve(i, j)` = side length of the largest square whose **top-left corner** is `(i, j)`

If `matrix[i][j] == '0'`, answer is `0`

If `matrix[i][j] == '1'`, then:

```text
solve(i, j) = 1 + min(
    solve(i + 1, j),     // down
    solve(i, j + 1),     // right
    solve(i + 1, j + 1)  // diagonal
)
```

This is the same square logic, just viewed from the top-left instead of bottom-right.

---

## Why the Recurrence Works

Suppose `(i, j)` is top-left of a square of side `k`.

Then:

- the cell below must support a square of side `k - 1`
- the cell right must support a square of side `k - 1`
- the diagonal below-right must also support a square of side `k - 1`

So the largest possible size is limited by the smallest of those three.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] memo = new int[rows][cols];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        int maxSide = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maxSide = Math.max(maxSide, solve(matrix, i, j, memo));
            }
        }

        return maxSide * maxSide;
    }

    private int solve(char[][] matrix, int i, int j, int[][] memo) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        if (i >= rows || j >= cols) {
            return 0;
        }

        if (memo[i][j] != -1) {
            return memo[i][j];
        }

        int down = solve(matrix, i + 1, j, memo);
        int right = solve(matrix, i, j + 1, memo);
        int diagonal = solve(matrix, i + 1, j + 1, memo);

        if (matrix[i][j] == '1') {
            memo[i][j] = 1 + Math.min(down, Math.min(right, diagonal));
        } else {
            memo[i][j] = 0;
        }

        return memo[i][j];
    }
}
```

---

## Complexity

### Time Complexity

- **O(m \* n)**

Each state `(i, j)` is computed once.

### Space Complexity

- **O(m \* n)** for memo
- plus recursion stack up to **O(m + n)** in worst case path depth

Overall commonly stated as **O(m \* n)**

---

## Pros

- Good for deriving the recurrence
- Elegant recursive interpretation

## Cons

- Uses recursion
- Can feel less direct than bottom-up DP

---

# Approach 4: Bottom-Up DP (Classic 2D DP)

## Idea

Let:

- `dp[i][j]` = side length of the largest square whose **bottom-right corner** is `(i - 1, j - 1)` in the original matrix

We use a `(rows + 1) x (cols + 1)` table to avoid boundary checks.

Transition:

```text
if matrix[i - 1][j - 1] == '1':
    dp[i][j] = 1 + min(
        dp[i - 1][j],
        dp[i][j - 1],
        dp[i - 1][j - 1]
    )
else:
    dp[i][j] = 0
```

This is the standard and most teachable solution.

---

## Why Use Extra Padding?

If we use `dp[rows + 1][cols + 1]`, then:

- `dp[0][*] = 0`
- `dp[*][0] = 0`

So first row and first column of the original matrix do not need special handling.

That makes the code much cleaner.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] dp = new int[rows + 1][cols + 1];
        int maxSide = 0;

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                if (matrix[i - 1][j - 1] == '1') {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                    maxSide = Math.max(maxSide, dp[i][j]);
                }
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Example Walkthrough

For:

```text
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0
```

When we fill `dp`, each `dp[i][j]` tells:

- largest square ending at that cell

Key moment:

- the 2x2 square of ones appears around the middle-right region
- maximum side becomes `2`
- area = `2 * 2 = 4`

---

## Complexity

### Time Complexity

- **O(m \* n)**

Each cell is processed once.

### Space Complexity

- **O(m \* n)**

---

## Pros

- Clean
- Standard
- Very interview friendly
- Easy to explain

## Cons

- Uses extra 2D table

---

# Approach 5: Bottom-Up DP Without Padding

## Idea

Same DP as above, but use `dp[rows][cols]` directly.

This requires manual handling of:

- first row
- first column

because those cannot look up top or left safely.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] dp = new int[rows][cols];
        int maxSide = 0;

        for (int i = 0; i < rows; i++) {
            dp[i][0] = matrix[i][0] - '0';
            maxSide = Math.max(maxSide, dp[i][0]);
        }

        for (int j = 0; j < cols; j++) {
            dp[0][j] = matrix[0][j] - '0';
            maxSide = Math.max(maxSide, dp[0][j]);
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                    maxSide = Math.max(maxSide, dp[i][j]);
                }
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Complexity

### Time Complexity

- **O(m \* n)**

### Space Complexity

- **O(m \* n)**

---

## Pros

- Same efficiency as padded version
- Explicit about boundaries

## Cons

- Slightly more verbose
- Easier to make boundary mistakes

---

# Approach 6: Space-Optimized DP with 1D Array

## Idea

Notice the recurrence only needs:

- current row, previous column (`dp[j - 1]`)
- previous row, same column (`dp[j]` before update)
- previous row, previous column (a saved variable)

So we can compress 2D DP into 1D.

Let:

- `dp[j]` = previous row's answer for column `j`, then updated to current row
- `prevDiagonal` = old `dp[j - 1]` from previous row

This is one of the most important optimizations here.

---

## Transition Details

Before updating `dp[j]`:

- `dp[j]` = top
- `dp[j - 1]` = left (already updated in current row)
- `prevDiagonal` = top-left

If `matrix[i - 1][j - 1] == '1'`:

```text
newDpJ = 1 + min(top, left, topLeft)
```

Else:

```text
newDpJ = 0
```

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[] dp = new int[cols + 1];
        int maxSide = 0;

        for (int i = 1; i <= rows; i++) {
            int prevDiagonal = 0;

            for (int j = 1; j <= cols; j++) {
                int temp = dp[j]; // old top value

                if (matrix[i - 1][j - 1] == '1') {
                    dp[j] = 1 + Math.min(dp[j], Math.min(dp[j - 1], prevDiagonal));
                    maxSide = Math.max(maxSide, dp[j]);
                } else {
                    dp[j] = 0;
                }

                prevDiagonal = temp;
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Why `prevDiagonal` Is Needed

Because once `dp[j]` is updated, the old value is lost.

But the recurrence needs the old top-left cell from the previous row.

So:

- store old `dp[j]` in `temp`
- after update, move `prevDiagonal = temp`

This preserves the needed diagonal dependency.

---

## Complexity

### Time Complexity

- **O(m \* n)**

### Space Complexity

- **O(n)**

where `n` is number of columns.

---

## Pros

- Same speed as 2D DP
- Much lower space usage
- Excellent optimized solution

## Cons

- Slightly trickier to understand
- Easy to get the diagonal variable wrong

---

# Approach 7: Prefix Sum + Binary Search on Side Length

## Idea

Instead of DP, we can ask:

> Does there exist a square of side `k` containing only ones?

If we can answer that efficiently, then we can binary search the largest `k`.

To check whether a square is all ones quickly, use a **2D prefix sum**.

For any square:

- sum of its cells = `k * k` means all ones

---

## Step 1: Build Prefix Sum

Let `prefix[i][j]` store the sum of submatrix from `(0,0)` to `(i-1,j-1)`.

Then any square sum can be queried in O(1).

---

## Step 2: Check if Side `k` Exists

Try every `k x k` square:

- if any square has sum `k * k`, then side `k` is possible

---

## Step 3: Binary Search on `k`

Search from `1` to `min(rows, cols)`.

This is a valid and interesting alternative.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] prefix = new int[rows + 1][cols + 1];

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                int val = matrix[i - 1][j - 1] - '0';
                prefix[i][j] = val
                    + prefix[i - 1][j]
                    + prefix[i][j - 1]
                    - prefix[i - 1][j - 1];
            }
        }

        int low = 0;
        int high = Math.min(rows, cols);

        while (low < high) {
            int mid = (low + high + 1) / 2;
            if (existsSquare(prefix, rows, cols, mid)) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }

        return low * low;
    }

    private boolean existsSquare(int[][] prefix, int rows, int cols, int side) {
        if (side == 0) {
            return true;
        }

        for (int i = side; i <= rows; i++) {
            for (int j = side; j <= cols; j++) {
                int sum = prefix[i][j]
                        - prefix[i - side][j]
                        - prefix[i][j - side]
                        + prefix[i - side][j - side];

                if (sum == side * side) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity

### Time Complexity

- Prefix construction: **O(m \* n)**
- Each existence check: **O(m \* n)**
- Binary search over side length: **O(log(min(m, n)))**

Total:

- **O(m _ n _ log(min(m, n)))**

### Space Complexity

- **O(m \* n)**

---

## Pros

- Very interesting alternative
- Useful if existence queries are the main framing
- Good for learning 2D prefix sums

## Cons

- More complex than necessary
- Usually not as elegant as DP for this problem

---

# Approach 8: Prefix Sum Without Binary Search (Enumerate Side Lengths)

## Idea

Using the same 2D prefix sum, try every possible side length from largest to smallest.

For each side, check whether any square of that size has total sum equal to `side * side`.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] prefix = new int[rows + 1][cols + 1];

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                int val = matrix[i - 1][j - 1] - '0';
                prefix[i][j] = val
                        + prefix[i - 1][j]
                        + prefix[i][j - 1]
                        - prefix[i - 1][j - 1];
            }
        }

        for (int side = Math.min(rows, cols); side >= 1; side--) {
            for (int i = side; i <= rows; i++) {
                for (int j = side; j <= cols; j++) {
                    int sum = prefix[i][j]
                            - prefix[i - side][j]
                            - prefix[i][j - side]
                            + prefix[i - side][j - side];

                    if (sum == side * side) {
                        return side * side;
                    }
                }
            }
        }

        return 0;
    }
}
```

---

## Complexity

### Time Complexity

- In worst case roughly **O(m _ n _ min(m, n))**

### Space Complexity

- **O(m \* n)**

---

## Why Mention It?

Because prefix sums let you validate whole regions quickly.

Still, DP remains the cleaner fit.

---

# Approach 9: In-Place DP (Only If Input Modification Is Allowed)

## Idea

If modification of the matrix representation is allowed, we can store DP values directly in another integer matrix derived from input, or conceptually update values in place if input were numeric.

Since the given matrix is `char[][]`, true in-place mutation into DP values is awkward and not ideal in Java. But the conceptual optimization is worth understanding.

You can create an `int[][]` from the input and then reuse it as the DP table.

---

## Java Code

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] dp = new int[rows][cols];
        int maxSide = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dp[i][j] = matrix[i][j] - '0';

                if (dp[i][j] == 1 && i > 0 && j > 0) {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                }

                maxSide = Math.max(maxSide, dp[i][j]);
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Complexity

### Time Complexity

- **O(m \* n)**

### Space Complexity

- **O(m \* n)**

---

## Why It Matters

This emphasizes that the DP value at `(i, j)` overwrites the original binary interpretation.

This is common in matrix DP patterns.

---

# Comparing All Approaches

| Approach                      |                     Time |     Space | Notes                         |
| ----------------------------- | -----------------------: | --------: | ----------------------------- |
| Brute Force Expansion         |    O(m _ n _ min(m,n)^2) |      O(1) | Intuitive but slow            |
| Full Square Validation        |    O(m _ n _ min(m,n)^3) |      O(1) | Simplest but worst            |
| Top-Down Memoization          |                O(m \* n) | O(m \* n) | Recursive DP                  |
| Bottom-Up 2D DP (Padded)      |                O(m \* n) | O(m \* n) | Standard solution             |
| Bottom-Up 2D DP (No Padding)  |                O(m \* n) | O(m \* n) | Same idea, more boundary work |
| Space-Optimized 1D DP         |                O(m \* n) |      O(n) | Best optimized DP             |
| Prefix Sum + Binary Search    | O(m _ n _ log(min(m,n))) | O(m \* n) | Nice alternative              |
| Prefix Sum + Side Enumeration |      O(m _ n _ min(m,n)) | O(m \* n) | Region-query perspective      |
| Reused DP Matrix              |                O(m \* n) | O(m \* n) | Conceptual in-place style     |

---

# Full Derivation of the DP Recurrence

Suppose `matrix[i][j] == '1'`.

We want the largest square ending at `(i, j)`.

For `(i, j)` to extend a square of side `k - 1` into a square of side `k`, the following must all already support side at least `k - 1`:

- top cell region
- left cell region
- top-left diagonal region

If any one of them only supports a smaller square, then the current square is limited by that smallest one.

So:

```text
dp[i][j] = 1 + min(
    dp[i - 1][j],
    dp[i][j - 1],
    dp[i - 1][j - 1]
)
```

only when `matrix[i][j] == '1'`.

If `matrix[i][j] == '0'`, then:

```text
dp[i][j] = 0
```

because no square of ones can end there.

This is the central recurrence.

---

# Recommended Final Java Solution

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] dp = new int[rows + 1][cols + 1];
        int maxSide = 0;

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                if (matrix[i - 1][j - 1] == '1') {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                    maxSide = Math.max(maxSide, dp[i][j]);
                }
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Recommended Optimized Version

```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[] dp = new int[cols + 1];
        int maxSide = 0;

        for (int i = 1; i <= rows; i++) {
            int prevDiagonal = 0;

            for (int j = 1; j <= cols; j++) {
                int temp = dp[j];

                if (matrix[i - 1][j - 1] == '1') {
                    dp[j] = 1 + Math.min(dp[j], Math.min(dp[j - 1], prevDiagonal));
                    maxSide = Math.max(maxSide, dp[j]);
                } else {
                    dp[j] = 0;
                }

                prevDiagonal = temp;
            }
        }

        return maxSide * maxSide;
    }
}
```

---

## Complexity of Recommended Versions

### 2D DP

- Time: **O(m \* n)**
- Space: **O(m \* n)**

### 1D DP

- Time: **O(m \* n)**
- Space: **O(n)**

---

# Dry Run of the Classic DP

Input:

```text
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0
```

We compute largest square ending at each cell.

Example key position:

- when processing the cell in row 3, col 4 (1-based discussion), its top, left, and diagonal are all at least `1`
- so current becomes `2`

That identifies a `2 x 2` square.

No location can become `3`, so:

- `maxSide = 2`
- answer = `4`

---

# Edge Cases

## 1. Single cell `'0'`

```text
[["0"]]
```

Answer = `0`

## 2. Single cell `'1'`

```text
[["1"]]
```

Answer = `1`

## 3. All zeros

Largest square side = `0`

## 4. All ones

Largest square side = `min(m, n)`

## 5. Thin matrix

If one dimension is `1`, answer is either `1` or `0` depending on presence of a `'1'`

---

# Common Mistakes

## Mistake 1: Thinking only current row or current column matters

A square needs consistency in both dimensions and the diagonal structure too.

That is why the diagonal dependency is essential.

---

## Mistake 2: Using `max` instead of `min`

The recurrence must use:

```text
1 + min(top, left, diagonal)
```

not `max`.

Why?
Because the square can only be as large as its weakest supporting side.

---

## Mistake 3: Returning side length instead of area

Problem asks for:

```text
maxSide * maxSide
```

not just `maxSide`.

---

## Mistake 4: Boundary bugs in 2D DP without padding

Using a padded DP table avoids many off-by-one issues.

---

## Mistake 5: Forgetting to reset 1D DP cell to zero when matrix cell is `'0'`

In the 1D solution, if current cell is zero, you must explicitly set:

```java
dp[j] = 0;
```

Otherwise stale values corrupt later answers.

---

# Pattern Recognition

This is a classic **matrix DP** problem with local neighborhood dependence.

Signals that DP is appropriate:

- answer at a cell depends on nearby already-computed cells
- you need the “largest valid structure ending here”
- brute force repeats huge amounts of overlapping work

Related patterns:

- maximal rectangle
- largest plus sign
- longest increasing path in matrix
- count square submatrices with all ones

---

# Interview Explanation Template

A strong explanation:

1. Let `dp[i][j]` be the side length of the largest square ending at cell `(i, j)`.
2. If the current cell is `0`, then `dp[i][j] = 0`.
3. If it is `1`, then the square size depends on top, left, and top-left diagonal neighbors.
4. So:
   `dp[i][j] = 1 + min(top, left, diagonal)`
5. Track the maximum side length and return its square as area.

This is concise and complete.

---

# Final Takeaway

The heart of this problem is:

> A cell can extend a square of ones only if its top, left, and diagonal neighbors can all support that extension.

That immediately leads to the recurrence:

```text
1 + min(top, left, diagonal)
```

The best practical solution is:

- **2D DP** for clarity
- **1D DP** for optimization
