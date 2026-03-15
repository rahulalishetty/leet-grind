# 3529. Count Cells in Overlapping Horizontal and Vertical Substrings

## Problem Statement

You are given an `m x n` matrix `grid` consisting of lowercase characters and a string `pattern`.

A **horizontal substring** is read left to right across the matrix.
If we reach the end of a row before finishing the substring, we continue from the first column of the next row.
We do **not** wrap from the last row back to the first row.

A **vertical substring** is read top to bottom down the matrix.
If we reach the bottom of a column before finishing the substring, we continue from the first row of the next column.
We do **not** wrap from the last column back to the first column.

We must count how many cells belong to:

- at least one horizontal substring equal to `pattern`, and
- at least one vertical substring equal to `pattern`

In other words, count cells that are covered by **both** a horizontal match and a vertical match.

---

## Example 1

```text
Input:
grid =
[
  ["a","a","c","c"],
  ["b","b","b","c"],
  ["a","a","b","a"],
  ["c","a","a","c"],
  ["a","a","b","a"]
]
pattern = "abaca"

Output:
1
```

Explanation:

The pattern appears once horizontally and once vertically, and those two matches intersect at exactly one cell.

---

## Example 2

```text
Input:
grid =
[
  ["c","a","a","a"],
  ["a","a","b","a"],
  ["b","b","a","a"],
  ["a","a","b","a"]
]
pattern = "aba"

Output:
4
```

Explanation:

There are several horizontal and vertical matches of `"aba"`.
Exactly four cells belong to at least one match in both directions.

---

## Example 3

```text
Input:
grid = [["a"]]
pattern = "a"

Output:
1
```

---

## Constraints

- `m == grid.length`
- `n == grid[i].length`
- `1 <= m, n <= 1000`
- `1 <= m * n <= 10^5`
- `1 <= pattern.length <= m * n`
- `grid` and `pattern` consist only of lowercase English letters

---

# Core Insight

The wrap rules look two-dimensional, but both directions can be converted into **1D strings**.

## Horizontal order

If we read the matrix row by row, left to right, top to bottom, that is exactly the horizontal traversal order with wrapping to the next row.

So if we flatten the grid row-wise:

```text
H = grid[0][0], grid[0][1], ..., grid[0][n-1], grid[1][0], ..., grid[m-1][n-1]
```

then every valid horizontal substring is just a normal contiguous substring of `H`.

## Vertical order

If we read the matrix column by column, top to bottom, left to right, that is exactly the vertical traversal order with wrapping to the next column.

So if we flatten the grid column-wise:

```text
V = grid[0][0], grid[1][0], ..., grid[m-1][0], grid[0][1], ..., grid[m-1][n-1]
```

then every valid vertical substring is just a normal contiguous substring of `V`.

So the problem becomes:

1. Find all occurrences of `pattern` in `H`
2. Mark all cells covered by those horizontal occurrences
3. Find all occurrences of `pattern` in `V`
4. Mark all cells covered by those vertical occurrences
5. Count how many cells are marked in both sets

That is the decisive transformation.

---

# Approach 1: Brute Force Matching on Both Flattened Strings

## Intuition

Once we flatten horizontally and vertically, the simplest approach is:

- try every possible start position
- compare the substring against `pattern`
- if it matches, mark all covered cells

This is easy to understand, but can be too slow when both the flattened length and pattern length are large.

Since:

```text
m * n <= 10^5
pattern.length can also be up to 10^5
```

a naive `O(N * P)` matcher is not safe.

Still, it is useful as a baseline.

---

## Mapping 1D Positions Back to Cells

Let total cells be:

```text
N = m * n
```

### In horizontal flattening

A horizontal flattened index `idx` corresponds to matrix cell:

```text
row = idx / n
col = idx % n
```

### In vertical flattening

A vertical flattened index `idx` corresponds to matrix cell:

```text
row = idx % m
col = idx / m
```

These mappings are essential for marking covered cells.

---

## Java Code

```java
class Solution {
    public int countCells(char[][] grid, String pattern) {
        int m = grid.length;
        int n = grid[0].length;
        int total = m * n;
        int p = pattern.length();

        StringBuilder horizontal = new StringBuilder();
        StringBuilder vertical = new StringBuilder();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                horizontal.append(grid[i][j]);
            }
        }

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                vertical.append(grid[i][j]);
            }
        }

        boolean[] hCover = new boolean[total];
        boolean[] vCover = new boolean[total];

        for (int start = 0; start + p <= total; start++) {
            if (matches(horizontal, start, pattern)) {
                for (int k = 0; k < p; k++) {
                    int idx = start + k;
                    hCover[idx] = true;
                }
            }
        }

        for (int start = 0; start + p <= total; start++) {
            if (matches(vertical, start, pattern)) {
                for (int k = 0; k < p; k++) {
                    int idx = start + k;
                    int row = idx % m;
                    int col = idx / m;
                    vCover[row * n + col] = true;
                }
            }
        }

        int answer = 0;
        for (int i = 0; i < total; i++) {
            if (hCover[i] && vCover[i]) {
                answer++;
            }
        }

        return answer;
    }

    private boolean matches(StringBuilder s, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (s.charAt(start + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

Let:

- `N = m * n`
- `P = pattern.length()`

### Time Complexity

Naive matching:

```text
O(N * P)
```

This is too slow in the worst case.

### Space Complexity

```text
O(N)
```

for coverage arrays and flattened strings.

---

## Verdict

Correct, but not scalable enough.

---

# Approach 2: KMP Matching + Direct Cell Marking

## Intuition

We need a faster string matching algorithm.

After flattening, the task is just pattern matching in two 1D strings, so **KMP** is a natural fit.

KMP finds all occurrences in linear time:

```text
O(N + P)
```

If we run KMP on both flattened strings, we can get all match start positions efficiently.

Then for each occurrence, we mark all cells covered by that occurrence.

This is already a strong solution, but there is one more optimization to make marking efficient.

---

## KMP Refresher

KMP preprocesses `pattern` into an `lps` array:

```text
lps[i] = length of the longest proper prefix of pattern[0..i]
         which is also a suffix
```

Then the text is scanned in linear time without restarting comparisons from scratch.

---

## Direct Marking Concern

If the pattern appears many times, marking every character of every occurrence one by one can become expensive.

Suppose pattern length is large and matches overlap heavily.
Then direct marking can push total marking work toward `O(numberOfMatches * P)`.

So while KMP solves the matching problem, we still want a smarter way to mark coverage.

That leads to difference arrays.

---

# Approach 3: KMP + Difference Arrays on Flattened Indices

## Intuition

This is the best exact solution.

Instead of marking each covered cell one by one for every match, we mark intervals on the flattened strings using difference arrays.

If a match of length `P` starts at `start`, then in the flattened sequence it covers:

```text
[start, start + P - 1]
```

Using a difference array:

```text
diff[start] += 1
diff[start + P] -= 1
```

After prefix-summing, every positive value indicates that position belongs to at least one match.

We do this:

- once for horizontal flattened string
- once for vertical flattened string

Then convert the vertical flattened coverage back into original cell indices and intersect with horizontal coverage.

This gives a clean linear solution.

---

## Why Difference Arrays Help

Suppose pattern `"aba"` appears at starts:

```text
2, 5, 6
```

Instead of explicitly marking all covered positions one by one, we just record interval boundaries.

Later, one prefix sum reconstructs coverage for all positions.

That changes marking from potentially large repeated work into linear work overall.

---

## Full Plan

1. Flatten grid row-wise into `H`
2. Flatten grid column-wise into `V`
3. Run KMP to get all match starts in `H`
4. Mark horizontal matched intervals with a difference array
5. Prefix-sum to get horizontal coverage per row-major index
6. Run KMP to get all match starts in `V`
7. Mark vertical matched intervals with a difference array
8. Prefix-sum to get vertical coverage per column-major index
9. Map vertical covered positions back to row-major cell ids
10. Count cells covered in both directions

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countCells(char[][] grid, String pattern) {
        int m = grid.length;
        int n = grid[0].length;
        int total = m * n;

        String horizontal = buildHorizontal(grid);
        String vertical = buildVertical(grid);

        int[] hDiff = new int[total + 1];
        int[] vDiff = new int[total + 1];

        markMatches(horizontal, pattern, hDiff);
        markMatches(vertical, pattern, vDiff);

        boolean[] hCovered = buildCovered(hDiff, total);
        boolean[] vCoveredRowMajor = buildVerticalCoveredRowMajor(vDiff, m, n);

        int answer = 0;
        for (int i = 0; i < total; i++) {
            if (hCovered[i] && vCoveredRowMajor[i]) {
                answer++;
            }
        }

        return answer;
    }

    private String buildHorizontal(char[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            for (char c : row) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String buildVertical(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < n; col++) {
            for (int row = 0; row < m; row++) {
                sb.append(grid[row][col]);
            }
        }
        return sb.toString();
    }

    private void markMatches(String text, String pattern, int[] diff) {
        List<Integer> starts = kmpSearch(text, pattern);
        int p = pattern.length();

        for (int start : starts) {
            diff[start]++;
            if (start + p < diff.length) {
                diff[start + p]--;
            }
        }
    }

    private boolean[] buildCovered(int[] diff, int total) {
        boolean[] covered = new boolean[total];
        int running = 0;
        for (int i = 0; i < total; i++) {
            running += diff[i];
            covered[i] = running > 0;
        }
        return covered;
    }

    private boolean[] buildVerticalCoveredRowMajor(int[] vDiff, int m, int n) {
        int total = m * n;
        boolean[] covered = new boolean[total];
        int running = 0;

        for (int idx = 0; idx < total; idx++) {
            running += vDiff[idx];
            if (running > 0) {
                int row = idx % m;
                int col = idx / m;
                covered[row * n + col] = true;
            }
        }

        return covered;
    }

    private List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int[] lps = buildLPS(pattern);

        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return result;
    }

    private int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;

        for (int i = 1; i < pattern.length(); ) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

Let:

- `N = m * n`
- `P = pattern.length()`

### Time Complexity

- Flatten horizontal: `O(N)`
- Flatten vertical: `O(N)`
- KMP on horizontal: `O(N + P)`
- KMP on vertical: `O(N + P)`
- Prefix sums / mapping / intersection: `O(N)`

Total:

```text
O(N + P)
```

up to constant factors.

### Space Complexity

- two flattened strings: `O(N)`
- difference arrays: `O(N)`
- coverage arrays: `O(N)`
- KMP LPS: `O(P)`

Total:

```text
O(N + P)
```

---

## Verdict

This is the strongest exact and scalable solution.

---

# Approach 4: Z-Algorithm Instead of KMP

## Intuition

KMP is not the only way to find all pattern occurrences in linear time.

We can also use the Z-algorithm by building:

```text
pattern + "#" + text
```

Then any position in the text part with Z-value at least `pattern.length()` is a match start.

So the whole solution can be rewritten as:

- flatten strings
- use Z instead of KMP
- mark intervals with difference arrays
- intersect coverage

This is equivalent in asymptotic strength.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countCells(char[][] grid, String pattern) {
        int m = grid.length;
        int n = grid[0].length;
        int total = m * n;

        String horizontal = buildHorizontal(grid);
        String vertical = buildVertical(grid);

        int[] hDiff = new int[total + 1];
        int[] vDiff = new int[total + 1];

        markMatchesWithZ(horizontal, pattern, hDiff);
        markMatchesWithZ(vertical, pattern, vDiff);

        boolean[] hCovered = buildCovered(hDiff, total);
        boolean[] vCoveredRowMajor = buildVerticalCoveredRowMajor(vDiff, m, n);

        int answer = 0;
        for (int i = 0; i < total; i++) {
            if (hCovered[i] && vCoveredRowMajor[i]) {
                answer++;
            }
        }

        return answer;
    }

    private void markMatchesWithZ(String text, String pattern, int[] diff) {
        String combined = pattern + "#" + text;
        int[] z = buildZ(combined);
        int p = pattern.length();

        for (int i = p + 1; i < combined.length(); i++) {
            if (z[i] >= p) {
                int start = i - (p + 1);
                diff[start]++;
                if (start + p < diff.length) {
                    diff[start + p]--;
                }
            }
        }
    }

    private int[] buildZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }

    private String buildHorizontal(char[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            for (char c : row) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String buildVertical(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < n; col++) {
            for (int row = 0; row < m; row++) {
                sb.append(grid[row][col]);
            }
        }
        return sb.toString();
    }

    private boolean[] buildCovered(int[] diff, int total) {
        boolean[] covered = new boolean[total];
        int running = 0;
        for (int i = 0; i < total; i++) {
            running += diff[i];
            covered[i] = running > 0;
        }
        return covered;
    }

    private boolean[] buildVerticalCoveredRowMajor(int[] vDiff, int m, int n) {
        int total = m * n;
        boolean[] covered = new boolean[total];
        int running = 0;

        for (int idx = 0; idx < total; idx++) {
            running += vDiff[idx];
            if (running > 0) {
                int row = idx % m;
                int col = idx / m;
                covered[row * n + col] = true;
            }
        }

        return covered;
    }
}
```

---

## Complexity Analysis

Exactly like the KMP version:

### Time Complexity

```text
O(N + P)
```

### Space Complexity

```text
O(N + P)
```

---

## Verdict

Excellent alternative.
KMP and Z are equally strong here.

---

# Approach 5: Rolling Hash + Interval Marking

## Intuition

Another way to find pattern occurrences is rolling hash.

We can hash the pattern and slide a hash window across the flattened strings.

When the hashes match, we record an occurrence and mark the interval.

This can be fast in practice, but there is an important caveat:

- rolling hash is probabilistic because of collisions

Since exact linear-time solutions already exist using KMP or Z, rolling hash is not the best final choice.

---

## Why It Is Weaker

Even if collisions are rare, they are still theoretically possible.

For interview-quality reasoning, exact algorithms are stronger when they are available at the same asymptotic complexity.

---

# Why Flattening Is Correct

This is the most important reasoning step.

## Horizontal traversal

The problem says:

- read left to right
- if row ends, continue at start of next row
- no wrap from bottom to top

That is exactly ordinary row-major order.

So every horizontal substring is simply a contiguous segment of the row-major flattened string.

## Vertical traversal

The problem says:

- read top to bottom
- if column ends, continue at top of next column
- no wrap from last column to first

That is exactly ordinary column-major order.

So every vertical substring is simply a contiguous segment of the column-major flattened string.

Once this is clear, the 2D-looking problem becomes a pair of standard 1D string matching problems.

---

# Why Interval Marking Is Correct

If `pattern` matches starting at flattened index `start`, then every flattened position in:

```text
[start, start + pattern.length - 1]
```

belongs to that occurrence.

So to know whether a cell belongs to **at least one** match, it is enough to know whether its flattened position lies inside at least one matched interval.

Difference arrays capture exactly that.

---

# Final Recommended Solution

Use:

## flatten to row-major and column-major strings

## run KMP (or Z) to find all occurrences

## mark matched intervals with difference arrays

## intersect coverage

This is exact and linear.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    public int countCells(char[][] grid, String pattern) {
        int m = grid.length;
        int n = grid[0].length;
        int total = m * n;

        StringBuilder h = new StringBuilder(total);
        StringBuilder v = new StringBuilder(total);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                h.append(grid[i][j]);
            }
        }

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                v.append(grid[i][j]);
            }
        }

        int[] hDiff = new int[total + 1];
        int[] vDiff = new int[total + 1];

        markKmp(h.toString(), pattern, hDiff);
        markKmp(v.toString(), pattern, vDiff);

        boolean[] hCovered = new boolean[total];
        boolean[] vCovered = new boolean[total];

        int running = 0;
        for (int i = 0; i < total; i++) {
            running += hDiff[i];
            hCovered[i] = running > 0;
        }

        running = 0;
        for (int idx = 0; idx < total; idx++) {
            running += vDiff[idx];
            if (running > 0) {
                int row = idx % m;
                int col = idx / m;
                vCovered[row * n + col] = true;
            }
        }

        int answer = 0;
        for (int i = 0; i < total; i++) {
            if (hCovered[i] && vCovered[i]) {
                answer++;
            }
        }

        return answer;
    }

    private void markKmp(String text, String pattern, int[] diff) {
        int[] lps = buildLps(pattern);
        int i = 0, j = 0;
        int p = pattern.length();

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == p) {
                    int start = i - j;
                    diff[start]++;
                    if (start + p < diff.length) {
                        diff[start + p]--;
                    }
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
    }

    private int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;

        for (int i = 1; i < pattern.length(); ) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }
}
```

---

# Common Mistakes

## 1. Treating horizontal matching as confined to one row

That is wrong because horizontal substrings may wrap into the next row.

---

## 2. Treating vertical matching as confined to one column

That is wrong because vertical substrings may wrap into the next column.

---

## 3. Wrapping around the whole grid cyclically

The problem explicitly forbids:

- bottom row → top row
- last column → first column

So the flattened string is linear, not circular.

---

## 4. Marking matched cells one by one for every occurrence

This can create unnecessary extra work when matches are numerous.

Difference arrays are cleaner and linear.

---

## 5. Mapping vertical positions back incorrectly

For a column-major index `idx`:

```text
row = idx % m
col = idx / m
```

not the row-major formula.

---

# Complexity Summary

## Naive flatten + brute-force matching

- Time: `O(N * P)`
- Space: `O(N)`

## KMP + direct marking

- Matching is linear, but marking per occurrence can add overhead

## KMP + difference arrays

- Time: `O(N + P)`
- Space: `O(N + P)`

## Z + difference arrays

- Time: `O(N + P)`
- Space: `O(N + P)`

## Rolling hash + interval marking

- Time: near linear
- Space: linear
- Caveat: probabilistic

---

# Interview Summary

The decisive step is to flatten the matrix in the two traversal orders:

- row-major for horizontal
- column-major for vertical

Then the problem becomes:

- find all occurrences of `pattern` in two strings
- mark which flattened positions belong to at least one match
- map vertical coverage back to original cells
- intersect with horizontal coverage

The best exact solution uses:

- **KMP** or **Z-algorithm** for matching
- **difference arrays** for interval coverage

That yields a clean linear solution.
