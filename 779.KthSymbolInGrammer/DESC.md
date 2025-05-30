# 779. K-th Symbol in Grammar

<code style="color:rgb(250, 195, 29);">Medium</code>

We build a table of n rows (1-indexed). We start by writing 0 in the 1st row.
Now in every subsequent row, we look at the previous row and replace each occurrence of 0 with 01,
and each occurrence of 1 with 10.

For example, for n = 3, the 1st row is 0, the 2nd row is 01, and the 3rd row is 0110.
Given two integer n and k, return the kth (1-indexed) symbol in the nth row of a table of n rows.

Example 1:

```text
Input: n = 1, k = 1
Output: 0
Explanation: row 1: 0
```

Example 2:

```text
Input: n = 2, k = 1
Output: 0
Explanation:
row 1: 0
row 2: 01
```

Example 3:

```text
Input: n = 2, k = 2
Output: 1
Explanation:
row 1: 0
row 2: 01
```

Constraints:

```text
1 <= n <= 30
1 <= k <= 2n - 1
```
