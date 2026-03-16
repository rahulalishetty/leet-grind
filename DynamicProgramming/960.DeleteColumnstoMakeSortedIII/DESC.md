# 960. Delete Columns to Make Sorted III

## Problem Description

You are given an array of `n` strings `strs`, where all strings have the **same length**.

You may choose a set of **column indices** to delete. When you delete a column index, that column is removed from **every string** in the array.

Your goal is to remove the **minimum number of columns** so that **each row becomes lexicographically sorted**.

In other words, after deleting the chosen columns, for every string:

```
strs[i][0] <= strs[i][1] <= strs[i][2] <= ... <= strs[i][k]
```

must hold.

Return the **minimum number of deletions required**.

---

## Example 1

### Input

```
strs = ["babca","bbazb"]
```

### Output

```
3
```

### Explanation

Delete columns:

```
{0, 1, 4}
```

Remaining strings become:

```
["bc", "az"]
```

Check each row:

```
b <= c
a <= z
```

Both rows are lexicographically sorted.

Note:

```
"bc" > "az"
```

This is allowed because **only individual rows must be sorted**, not the order between rows.

---

## Example 2

### Input

```
strs = ["edcba"]
```

### Output

```
4
```

### Explanation

The single string:

```
"edcba"
```

is strictly decreasing.

To make it sorted, we must delete **4 columns**, leaving only one character.

---

## Example 3

### Input

```
strs = ["ghi","def","abc"]
```

### Output

```
0
```

### Explanation

Each row is already sorted:

```
g <= h <= i
d <= e <= f
a <= b <= c
```

So no deletions are required.

---

## Constraints

```
n == strs.length
1 <= n <= 100
1 <= strs[i].length <= 100
strs[i] consists of lowercase English letters
```

---

## Key Observations

1. The constraint applies **individually to each row**.
2. Column deletions affect **all rows simultaneously**.
3. We must ensure that **remaining columns preserve non-decreasing order in every row**.
4. The problem essentially asks for the **longest subsequence of columns that maintains sorted order across all rows**.
5. The minimum deletions equals:

```
total_columns - longest_valid_column_subsequence
```

---

## Difficulty

```
Hard
```

"""

path = Path("/mnt/data/minimum_deletion_size_rows_sorted_problem.md")
path.write_text(content)

str(path)
