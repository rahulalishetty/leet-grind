# LeetCode 85 — Maximal Rectangle

## Exhaustive summary of approaches with Java code, detailed explanation, and complexity analysis

## Problem

Given a `rows x cols` binary matrix filled with `'0'` and `'1'`, find the area of the largest rectangle containing only `1`s.

---

## Constraints

- `1 <= rows, cols <= 200`
- `matrix[i][j]` is `'0'` or `'1'`

---

# 1. Core intuition

A rectangle is determined by:

- a top boundary
- a bottom boundary
- a left boundary
- a right boundary

Brute force would try many such combinations.
That becomes expensive quickly.

The key insight is that this 2D problem can often be reduced to a sequence of **1D histogram problems**.

Why?

If we process the matrix row by row and keep track of the height of consecutive `1`s ending at the current row, then every row becomes the base of a histogram.
The maximal all-1 rectangle ending at that row is exactly the **largest rectangle in that histogram**.

This gives the most important optimal approach.

---

# 2. Important reduction: matrix row -> histogram

Suppose at row `r`, for each column `c`, we define:

```text
heights[c] = number of consecutive '1's vertically ending at row r
```

Example:

```text
matrix up to row r

1 0 1 1
1 1 1 1
0 1 1 1
```

At the third row, heights become:

```text
[0, 2, 3, 3]
```

Now ask:

> What is the largest rectangle in this histogram?

That answer corresponds to the largest rectangle of `1`s whose bottom edge is the current row.

If we do this for every row and keep the maximum, we solve the whole problem.

---

# 3. Approach 1 — Brute force over all rectangles

This is the most direct idea.

---

## Idea

Try every possible rectangle:

- choose top row
- choose bottom row
- choose left col
- choose right col

Then check whether every cell in that rectangle is `1`.

---

## Complexity reasoning

Number of rectangles in a matrix:

- top: `rows`
- bottom: `rows`
- left: `cols`
- right: `cols`

So roughly:

- `O(rows^2 * cols^2)` choices of rectangles

Checking one rectangle may take:

- `O(rows * cols)` in the worst case

So total is:

- `O(rows^3 * cols^3)`

For `200 x 200`, this is far too slow.

---

## Java code

```java
class SolutionBruteForce {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int maxArea = 0;

        for (int top = 0; top < rows; top++) {
            for (int bottom = top; bottom < rows; bottom++) {
                for (int left = 0; left < cols; left++) {
                    for (int right = left; right < cols; right++) {
                        if (isAllOnes(matrix, top, bottom, left, right)) {
                            int area = (bottom - top + 1) * (right - left + 1);
                            maxArea = Math.max(maxArea, area);
                        }
                    }
                }
            }
        }

        return maxArea;
    }

    private boolean isAllOnes(char[][] matrix, int top, int bottom, int left, int right) {
        for (int r = top; r <= bottom; r++) {
            for (int c = left; c <= right; c++) {
                if (matrix[r][c] == '0') {
                    return false;
                }
            }
        }
        return true;
    }
}
```

---

## Strengths

- Very straightforward
- Useful only as a conceptual baseline

## Weaknesses

- Completely impractical
- Massive repeated work

---

# 4. Approach 2 — Brute force with prefix sums for O(1) rectangle validation

This improves rectangle checking, but still not enough.

---

## Idea

Build a 2D prefix sum of ones.

Then for any rectangle, quickly determine:

- how many `1`s are in that rectangle

If the count equals the area, then the rectangle is all `1`s.

---

## Prefix sum definition

Let `prefix[r+1][c+1]` store the number of `1`s in the submatrix from `(0,0)` to `(r,c)`.

Then rectangle sum can be obtained in `O(1)`.

---

## Complexity

- Building prefix sums: `O(rows * cols)`
- Number of rectangles: `O(rows^2 * cols^2)`
- Rectangle check: `O(1)`

Total:

- **Time: `O(rows^2 * cols^2)`**
- **Space: `O(rows * cols)`**

This is much better than naive brute force, but still not the best solution.

---

## Java code

```java
class SolutionPrefixSum {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] prefix = new int[rows + 1][cols + 1];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int val = matrix[r][c] == '1' ? 1 : 0;
                prefix[r + 1][c + 1] = val
                        + prefix[r][c + 1]
                        + prefix[r + 1][c]
                        - prefix[r][c];
            }
        }

        int maxArea = 0;

        for (int top = 0; top < rows; top++) {
            for (int bottom = top; bottom < rows; bottom++) {
                for (int left = 0; left < cols; left++) {
                    for (int right = left; right < cols; right++) {
                        int ones = rectangleSum(prefix, top, bottom, left, right);
                        int area = (bottom - top + 1) * (right - left + 1);
                        if (ones == area) {
                            maxArea = Math.max(maxArea, area);
                        }
                    }
                }
            }
        }

        return maxArea;
    }

    private int rectangleSum(int[][] prefix, int top, int bottom, int left, int right) {
        return prefix[bottom + 1][right + 1]
                - prefix[top][right + 1]
                - prefix[bottom + 1][left]
                + prefix[top][left];
    }
}
```

---

## Strengths

- Good demonstration of prefix sums
- Much better than full brute force

## Weaknesses

- Still too slow for the best solution
- Does not exploit the structure of consecutive vertical `1`s

---

# 5. Approach 3 — Fix top and bottom rows, reduce to largest consecutive width

This is an intermediate 2D optimization.

---

## Idea

Choose a pair of rows:

- `top`
- `bottom`

Now for each column, determine whether every cell between these rows is `1`.

That turns the problem into a 1D array of valid/invalid columns.

Example:

```text
for chosen top..bottom:
[1, 1, 0, 1, 1, 1]
```

Then the best rectangle for these fixed rows is:

- height = `bottom - top + 1`
- width = longest consecutive run of `1`s in this derived array

Area = height \* width.

---

## How to maintain valid columns efficiently

As we extend `bottom` downward from a fixed `top`, maintain an array:

```text
valid[c] = whether all cells from top..bottom in column c are 1
```

Update rule:

```text
valid[c] &= (matrix[bottom][c] == '1')
```

Then scan `valid` to find the longest consecutive run of true values.

---

## Complexity

- choose top: `O(rows)`
- choose bottom: `O(rows)`
- update and scan columns: `O(cols)`

Total:

- **Time: `O(rows^2 * cols)`**
- **Space: `O(cols)`**

This is already much better and may pass given the small constraints, but it is not the best-known approach.

---

## Java code

```java
class SolutionTopBottomRows {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int maxArea = 0;

        for (int top = 0; top < rows; top++) {
            boolean[] valid = new boolean[cols];
            for (int c = 0; c < cols; c++) {
                valid[c] = true;
            }

            for (int bottom = top; bottom < rows; bottom++) {
                for (int c = 0; c < cols; c++) {
                    valid[c] = valid[c] && (matrix[bottom][c] == '1');
                }

                int width = longestRun(valid);
                int height = bottom - top + 1;
                maxArea = Math.max(maxArea, width * height);
            }
        }

        return maxArea;
    }

    private int longestRun(boolean[] valid) {
        int best = 0;
        int curr = 0;

        for (boolean v : valid) {
            if (v) {
                curr++;
                best = Math.max(best, curr);
            } else {
                curr = 0;
            }
        }

        return best;
    }
}
```

---

## Strengths

- Significant improvement over rectangle enumeration
- Good stepping stone toward better solutions

## Weaknesses

- Still quadratic in rows
- Does not reach the optimal histogram-stack performance

---

# 6. Approach 4 — Dynamic programming with height, left, right arrays

This is a classic `O(rows * cols)` solution.

---

## Core idea

For each row, maintain three arrays:

- `height[c]` = consecutive number of `1`s ending at current row in column `c`
- `left[c]` = leftmost boundary of rectangle ending at current row for column `c`
- `right[c]` = rightmost exclusive boundary of rectangle ending at current row for column `c`

Then area at column `c` is:

```text
height[c] * (right[c] - left[c])
```

---

## Why this works

At each row:

### `height`

If current cell is `'1'`, increase height.
If `'0'`, reset to 0.

### `left`

Track the furthest left boundary allowed by zeros seen in the current row.

### `right`

Track the furthest right boundary allowed by zeros seen in the current row.

The combination tells us the maximal rectangle of height `height[c]` that can use column `c` as part of its base.

---

## Detailed update rules

### Update heights

For each column `c`:

- if `matrix[r][c] == '1'`: `height[c]++`
- else: `height[c] = 0`

### Update left boundaries

Sweep left to right with `currLeft`.

- if cell is `'1'`: `left[c] = max(left[c], currLeft)`
- else:
  - `left[c] = 0`
  - `currLeft = c + 1`

### Update right boundaries

Sweep right to left with `currRight`.

- if cell is `'1'`: `right[c] = min(right[c], currRight)`
- else:
  - `right[c] = cols`
  - `currRight = c`

---

## Complexity

Each row does a few linear passes across columns.

- **Time: `O(rows * cols)`**
- **Space: `O(cols)`**

This is optimal in asymptotic terms.

---

## Java code

```java
import java.util.Arrays;

class SolutionDPBoundaries {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[] height = new int[cols];
        int[] left = new int[cols];
        int[] right = new int[cols];
        Arrays.fill(right, cols);

        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            int currLeft = 0;
            int currRight = cols;

            for (int c = 0; c < cols; c++) {
                if (matrix[r][c] == '1') {
                    height[c]++;
                } else {
                    height[c] = 0;
                }
            }

            for (int c = 0; c < cols; c++) {
                if (matrix[r][c] == '1') {
                    left[c] = Math.max(left[c], currLeft);
                } else {
                    left[c] = 0;
                    currLeft = c + 1;
                }
            }

            for (int c = cols - 1; c >= 0; c--) {
                if (matrix[r][c] == '1') {
                    right[c] = Math.min(right[c], currRight);
                } else {
                    right[c] = cols;
                    currRight = c;
                }
            }

            for (int c = 0; c < cols; c++) {
                maxArea = Math.max(maxArea, height[c] * (right[c] - left[c]));
            }
        }

        return maxArea;
    }
}
```

---

## Strengths

- Optimal asymptotic complexity
- No stack needed
- Elegant once understood

## Weaknesses

- The meaning of `left` and `right` is subtle
- Harder to invent than the histogram-stack approach
- Easier to make boundary mistakes

---

# 7. Approach 5 — Histogram per row + monotonic stack (most standard optimal solution)

This is the most common and arguably the cleanest optimal solution.

---

## Master reduction

For each row:

1. Update histogram heights
2. Compute largest rectangle in that histogram
3. Keep the global maximum

So the problem becomes repeated applications of:

- **Largest Rectangle in Histogram** (LeetCode 84)

---

## Step 1: build histogram heights

For each column `c`:

- if `matrix[r][c] == '1'`, `heights[c]++`
- else `heights[c] = 0`

This means `heights[c]` is the height of consecutive `1`s ending at current row.

---

## Step 2: solve largest rectangle in histogram

Given an array `heights`, find the maximum rectangle area.

The classic optimal method uses a **monotonic increasing stack** of indices.

---

## Why the monotonic stack works

For every bar of height `h`, we want to know:

- nearest smaller bar to the left
- nearest smaller bar to the right

Then this bar can be the limiting height of a rectangle spanning between them.

Width becomes:

```text
rightSmallerIndex - leftSmallerIndex - 1
```

Area:

```text
h * width
```

A monotonic stack computes this in linear time.

---

## Stack invariant

Store indices of bars in increasing height order.

When we encounter a bar shorter than the top:

- we pop
- the popped bar now knows its right boundary
- the new stack top gives its left boundary

---

## Complexity

For one row histogram:

- `O(cols)`

For all rows:

- `rows * O(cols)`

So:

- **Time: `O(rows * cols)`**
- **Space: `O(cols)`**

This is optimal.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class SolutionHistogramStack {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (matrix[r][c] == '1') {
                    heights[c]++;
                } else {
                    heights[c] = 0;
                }
            }

            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }

        return maxArea;
    }

    private int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i <= n; i++) {
            int currHeight = (i == n) ? 0 : heights[i];

            while (!stack.isEmpty() && currHeight < heights[stack.peek()]) {
                int h = heights[stack.pop()];
                int leftSmaller = stack.isEmpty() ? -1 : stack.peek();
                int width = i - leftSmaller - 1;
                maxArea = Math.max(maxArea, h * width);
            }

            stack.push(i);
        }

        return maxArea;
    }
}
```

---

## Strengths

- Optimal
- Reuses the well-known histogram problem
- Very standard interview solution
- Usually the best answer to present

## Weaknesses

- Requires already knowing or deriving histogram stack logic
- Stack width calculation can be tricky initially

---

# 8. Approach 6 — Histogram with precomputed nearest smaller left/right arrays

This is a variation of the histogram method.

---

## Idea

Instead of computing areas directly while popping from a stack, explicitly compute:

- `left[i]` = index of nearest smaller bar to the left
- `right[i]` = index of nearest smaller bar to the right

Then area for bar `i` is:

```text
heights[i] * (right[i] - left[i] - 1)
```

Do this histogram computation for every row.

---

## Complexity

For one histogram:

- compute left: `O(cols)`
- compute right: `O(cols)`
- compute max area: `O(cols)`

Total per row:

- `O(cols)`

Overall:

- **Time: `O(rows * cols)`**
- **Space: `O(cols)`**

Same asymptotic complexity as the standard stack version.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class SolutionHistogramBoundaries {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                heights[c] = matrix[r][c] == '1' ? heights[c] + 1 : 0;
            }
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }

        return maxArea;
    }

    private int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int[] left = new int[n];
        int[] right = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                stack.pop();
            }
            left[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        stack.clear();

        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                stack.pop();
            }
            right[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        int maxArea = 0;
        for (int i = 0; i < n; i++) {
            maxArea = Math.max(maxArea, heights[i] * (right[i] - left[i] - 1));
        }

        return maxArea;
    }
}
```

---

## Strengths

- Same optimal complexity
- Sometimes easier to reason about because boundaries are explicit

## Weaknesses

- Slightly more code and extra arrays
- Does not reduce complexity beyond the standard histogram stack

---

# 9. Approach 7 — Expansion from each top-left cell (conceptual, not recommended)

This is another naive family of ideas.

---

## Idea

Start from each cell containing `'1'` and try to expand rectangles downward while tracking the minimum width seen so far.

For a starting cell `(top, left)`:

- compute width of consecutive `1`s in the current row starting at `left`
- move downward row by row
- keep shrinking the allowed width to the minimum encountered
- update area each time

---

## Complexity

In the worst case this can still be around:

- **Time: `O(rows^2 * cols^2)`**
- **Space: `O(1)`**

It is better than full rectangle checking, but still not competitive with the optimal approaches.

---

## Java code

```java
class SolutionExpandFromCell {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int maxArea = 0;

        for (int top = 0; top < rows; top++) {
            for (int left = 0; left < cols; left++) {
                if (matrix[top][left] == '0') {
                    continue;
                }

                int minWidth = Integer.MAX_VALUE;

                for (int bottom = top; bottom < rows; bottom++) {
                    int width = 0;
                    while (left + width < cols && matrix[bottom][left + width] == '1') {
                        width++;
                    }

                    if (width == 0) {
                        break;
                    }

                    minWidth = Math.min(minWidth, width);
                    int height = bottom - top + 1;
                    maxArea = Math.max(maxArea, minWidth * height);
                }
            }
        }

        return maxArea;
    }
}
```

---

## Strengths

- Easier to invent than stack methods
- Nice stepping-stone idea

## Weaknesses

- Too slow in worst case
- Still repeats lots of work

---

# 10. Comparison of all approaches

| Approach                                |                 Time |            Space | Practical?         | Notes                                           |
| --------------------------------------- | -------------------: | ---------------: | ------------------ | ----------------------------------------------- |
| Full brute force rectangles             | `O(rows^3 * cols^3)` |           `O(1)` | No                 | Baseline only                                   |
| Prefix sums + rectangle enumeration     | `O(rows^2 * cols^2)` | `O(rows * cols)` | Usually no         | Faster validation but still too many rectangles |
| Fix top/bottom rows + longest run       |   `O(rows^2 * cols)` |        `O(cols)` | Sometimes          | Good intermediate optimization                  |
| DP with height/left/right               |     `O(rows * cols)` |        `O(cols)` | Yes                | Optimal, subtle boundaries                      |
| Histogram + monotonic stack             |     `O(rows * cols)` |        `O(cols)` | Yes, best standard | Most common optimal answer                      |
| Histogram + explicit smaller left/right |     `O(rows * cols)` |        `O(cols)` | Yes                | Same complexity, more explicit                  |
| Expand from each cell                   | `O(rows^2 * cols^2)` |           `O(1)` | No                 | Conceptual only                                 |

---

# 11. Which approach should you choose?

## For learning

A good progression is:

1. brute force rectangles
2. fix top/bottom rows
3. histogram reduction
4. monotonic stack

That progression helps you see where the real insight comes from.

## For interview performance

Use:

- **Histogram + monotonic stack**

It is the most standard and easiest optimal answer to justify.

## If you dislike stacks

Use:

- **DP with height/left/right**

It is equally optimal, but conceptually a little more subtle.

---

# 12. Why the histogram approach is the key insight

The hard part of this problem is that rectangles are 2D objects.
The histogram transformation converts the 2D geometry into a 1D boundary problem.

At each row:

- heights summarize all information about vertical continuity
- the largest rectangle ending at this row becomes a histogram problem

This is exactly the kind of reduction that often appears in hard matrix problems:

- compress one dimension
- solve the compressed subproblem efficiently

That is the central insight of LeetCode 85.

---

# 13. Common edge cases

## Single cell

```text
[['0']] -> 0
[['1']] -> 1
```

## Entire matrix all zeros

```text
0 0
0 0
answer = 0
```

## Entire matrix all ones

```text
1 1
1 1
answer = 4
```

## Rectangle not square

A maximal rectangle may be wide and short or narrow and tall.

## Gaps reset heights

Any `'0'` resets the histogram height at that column to 0.

---

# 14. Recommended final answer

For LeetCode 85, the strongest standard solution is:

- build histogram heights row by row
- solve Largest Rectangle in Histogram each row with a monotonic stack

### Final complexity

- **Time: `O(rows * cols)`**
- **Space: `O(cols)`**

That is optimal and the most interview-ready answer.
