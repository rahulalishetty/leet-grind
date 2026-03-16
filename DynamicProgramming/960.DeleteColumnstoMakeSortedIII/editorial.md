# Minimum Deletion Size to Make Rows Sorted — Exhaustive Solution Notes

## Overview

We are given an array of strings `strs`, all having the same length.

We may delete any set of **column indices**, and when we delete a column, that column disappears from **every string**.

Our goal is to make **each individual row** non-decreasing in lexicographic order using the **minimum number of deleted columns**.

A very important detail:

> We do **not** need the rows to be sorted relative to each other.
> We only need every **single row** to be internally sorted from left to right.

This problem becomes much easier if we stop thinking in terms of which columns to delete and instead think in terms of:

> what is the **largest set of columns we can keep** so that every row remains non-decreasing?

Then:

```text
minimum deletions = total columns - maximum columns kept
```

This write-up explains the accepted **dynamic programming** approach in detail.

---

## Problem Statement

You are given an array of `n` strings `strs`, all of the same length.

You may choose any set of column indices and delete those columns from every string.

Return the minimum number of deletions needed so that every row becomes non-decreasing.

That means for each row:

```text
row[0] <= row[1] <= row[2] <= ...
```

after deletions.

---

## Example 1

**Input**

```text
strs = ["babca", "bbazb"]
```

**Output**

```text
3
```

**Explanation**

Delete columns:

```text
{0, 1, 4}
```

Then the rows become:

```text
["bc", "az"]
```

Check row-wise:

- `"bc"` is non-decreasing because `b <= c`
- `"az"` is non-decreasing because `a <= z`

So 3 deletions are enough, and this is optimal.

---

## Example 2

**Input**

```text
strs = ["edcba"]
```

**Output**

```text
4
```

**Explanation**

The single row is strictly decreasing:

```text
e > d > c > b > a
```

To make it non-decreasing, we can keep only one column.

So we must delete:

```text
5 - 1 = 4
```

columns.

---

## Example 3

**Input**

```text
strs = ["ghi", "def", "abc"]
```

**Output**

```text
0
```

**Explanation**

Each row is already non-decreasing:

- `g <= h <= i`
- `d <= e <= f`
- `a <= b <= c`

So no deletions are needed.

---

## Constraints

- `n == strs.length`
- `1 <= n <= 100`
- `1 <= strs[i].length <= 100`
- `strs[i]` consists of lowercase English letters

---

# Main Insight

Instead of directly finding which columns to delete, find the **longest subsequence of columns** that can be kept.

Suppose the strings have width `W`.

If we can keep `kept` columns in valid order, then the answer is:

```text
W - kept
```

So the real task is:

> Find the longest sequence of column indices
> `c1 < c2 < c3 < ...`
> such that for every row:
>
> ```text
> row[c1] <= row[c2] <= row[c3] <= ...
> ```

This is very similar to finding a **Longest Increasing Subsequence (LIS)**, except the comparison between two columns must hold across **all rows simultaneously**.

---

# Reframing the Problem as LIS on Columns

Let each column index be treated like an item in a subsequence.

We want to know whether column `i` can come before column `j` in the kept subsequence.

That is possible **only if for every row**:

```text
row[i] <= row[j]
```

If this condition fails for even one row, then columns `i` and `j` cannot both remain in that order after deletions.

So the compatibility rule is:

```text
i -> j is valid if for all rows, row[i] <= row[j]
```

Now the problem is:

> Find the longest valid subsequence of column indices under this compatibility rule.

That is a dynamic programming problem.

---

# DP State Definition

Let:

```text
dp[i]
```

be the maximum number of columns we can keep in a valid subsequence **starting from column `i`**, assuming column `i` itself is kept.

So `dp[i]` is at least 1, because we can always keep column `i` alone.

Then we try to extend from `i` to some later column `j`.

If column `j` is compatible with column `i` across all rows, then:

```text
dp[i] = max(dp[i], 1 + dp[j])
```

This is exactly the LIS-style recurrence.

---

# Transition Condition

We want to know whether column `j` can follow column `i`.

That happens only if for every string `row`:

```text
row.charAt(i) <= row.charAt(j)
```

If even one row violates this:

```text
row.charAt(i) > row.charAt(j)
```

then `j` cannot follow `i`.

So the transition condition is:

```text
for all rows: row[i] <= row[j]
```

---

# Why Iterate from Right to Left

Since `dp[i]` depends on later columns `j > i`, it is convenient to compute `dp` from right to left.

At the moment we process column `i`, all `dp[j]` for `j > i` are already known.

That gives a straightforward bottom-up dynamic programming solution.

---

# Step-by-Step Algorithm

Suppose the width of each string is `W`.

## Step 1

Create an array:

```text
dp[0 ... W-1]
```

and initialize every value to 1.

Why 1?

Because even if no later column can follow `i`, we can still keep column `i` itself.

---

## Step 2

Process columns from right to left.

For each column `i`, try every later column `j > i`.

For each pair `(i, j)`:

- check every row
- if all rows satisfy `row[i] <= row[j]`, then column `j` can follow column `i`
- update:

```text
dp[i] = max(dp[i], 1 + dp[j])
```

---

## Step 3

After filling the DP array, the maximum number of columns we can keep is:

```text
max(dp[i]) over all i
```

Call this value `kept`.

---

## Step 4

Return:

```text
W - kept
```

because that is the minimum number of deletions.

---

# Worked Example

Consider:

```text
strs = ["babca", "bbazb"]
```

Width:

```text
W = 5
```

Columns are indexed:

```text
0 1 2 3 4
```

Let us check compatibility.

## Compare column 2 and column 3

Column 2 characters:

- first row: `'b'`
- second row: `'a'`

Column 3 characters:

- first row: `'c'`
- second row: `'z'`

Check row-wise:

- `b <= c` true
- `a <= z` true

So column 3 can follow column 2.

---

## Compare column 2 and column 4

Column 4 characters:

- first row: `'a'`
- second row: `'b'`

Check row-wise:

- `b <= a` false

So column 4 cannot follow column 2.

---

If we compute all such relationships, the best subsequence of columns we can keep has length 2, for example columns `[2, 3]`.

So minimum deletions:

```text
5 - 2 = 3
```

which matches the answer.

---

# Why This Is Correct

The dynamic programming is correct because:

1. Any valid solution corresponds to a subsequence of column indices.
2. Two consecutive kept columns must satisfy the row-wise compatibility condition.
3. `dp[i]` correctly represents the best valid subsequence starting at `i`.
4. The recurrence tries every valid next column `j`, so it considers all possibilities.
5. Therefore the maximum value among all `dp[i]` is the length of the longest valid subsequence of columns.
6. Deleting everything else is optimal.

So:

```text
minimum deletions = total columns - longest valid subsequence
```

---

# Java Implementation

```java
class Solution {
    public int minDeletionSize(String[] A) {
        int W = A[0].length();
        int[] dp = new int[W];
        Arrays.fill(dp, 1);

        for (int i = W - 2; i >= 0; --i)
            search: for (int j = i + 1; j < W; ++j) {
                for (String row : A)
                    if (row.charAt(i) > row.charAt(j))
                        continue search;

                dp[i] = Math.max(dp[i], 1 + dp[j]);
            }

        int kept = 0;
        for (int x : dp)
            kept = Math.max(kept, x);

        return W - kept;
    }
}
```

---

# Code Walkthrough

## `W = A[0].length();`

This is the number of columns.

---

## `int[] dp = new int[W];`

`dp[i]` will store the maximum number of columns that can be kept starting from column `i`.

---

## `Arrays.fill(dp, 1);`

Every column alone forms a valid subsequence of length 1.

---

## Outer loop: `for (int i = W - 2; i >= 0; --i)`

We process from right to left because `dp[i]` depends on later `dp[j]`.

---

## Inner loop: `for (int j = i + 1; j < W; ++j)`

Try every possible next column `j` after `i`.

---

## Row compatibility check

```java
for (String row : A)
    if (row.charAt(i) > row.charAt(j))
        continue search;
```

If any row violates the condition, then `j` cannot follow `i`, so skip this `j`.

The labeled `continue search` is used to jump to the next candidate `j`.

---

## DP update

```java
dp[i] = Math.max(dp[i], 1 + dp[j]);
```

If column `j` can follow `i`, then keeping `i` plus the best subsequence starting at `j` gives a candidate length of `1 + dp[j]`.

---

## Final answer

```java
int kept = 0;
for (int x : dp)
    kept = Math.max(kept, x);

return W - kept;
```

We find the longest valid subsequence and subtract it from the total number of columns.

---

# Complexity Analysis

Let:

- `N` = number of strings
- `W` = length of each string

## Time Complexity

We compare all pairs of columns `(i, j)`:

```text
O(W^2)
```

For each pair, we scan all rows:

```text
O(N)
```

So total time complexity is:

```text
O(N * W^2)
```

---

## Space Complexity

We use only the DP array of size `W`:

```text
O(W)
```

---

# Why This Is Like LIS

This problem is very close to Longest Increasing Subsequence.

In standard LIS:

- each element is a number
- you can place `a[i]` before `a[j]` if `a[i] < a[j]`

Here:

- each “element” is a **column**
- you can place column `i` before column `j` if for **every row**:
  ```text
  row[i] <= row[j]
  ```

So it is basically LIS under a custom comparison rule on columns.

That is the core pattern.

---

# Common Mistakes

## 1. Confusing row sorting with array sorting

We do **not** need:

```text
strs[0] <= strs[1] <= ...
```

We only need each row individually to be non-decreasing after deletions.

---

## 2. Trying greedy deletion

Deleting the first “bad” column greedily can fail because the optimal kept subsequence may require a more global choice.

This is why dynamic programming is needed.

---

## 3. Forgetting to keep at least one column

If no two columns are compatible, the longest valid subsequence is still 1, because any single column is trivially non-decreasing in every row.

That is why `dp[i]` starts at 1.

---

## 4. Using `<` instead of `<=`

The condition is **non-decreasing**, not strictly increasing.

So valid compatibility is:

```text
row[i] <= row[j]
```

not:

```text
row[i] < row[j]
```

---

# Final Summary

## Main Idea

Instead of directly minimizing deletions, maximize the number of columns we can keep.

That becomes a longest valid subsequence of columns problem.

---

## DP Definition

```text
dp[i] = longest valid subsequence starting at column i
```

Transition:

```text
if for all rows, row[i] <= row[j]:
    dp[i] = max(dp[i], 1 + dp[j])
```

Answer:

```text
W - max(dp)
```

---

## Complexity

- Time: `O(N * W^2)`
- Space: `O(W)`

---

# Best Final Java Solution

```java
class Solution {
    public int minDeletionSize(String[] A) {
        int W = A[0].length();
        int[] dp = new int[W];
        Arrays.fill(dp, 1);

        for (int i = W - 2; i >= 0; --i)
            search: for (int j = i + 1; j < W; ++j) {
                for (String row : A)
                    if (row.charAt(i) > row.charAt(j))
                        continue search;

                dp[i] = Math.max(dp[i], 1 + dp[j]);
            }

        int kept = 0;
        for (int x : dp)
            kept = Math.max(kept, x);

        return W - kept;
    }
}
```

This is the accepted dynamic programming solution for **Minimum Deletion Size to Make Rows Sorted**.
