# 3624. Number of Integers With Popcount-Depth Equal to K II

You are given an integer array **nums**.

For any positive integer **x**, define the following sequence:

```
p0 = x
pi+1 = popcount(pi) for all i >= 0
```

Where **popcount(y)** is the number of set bits (`1`s) in the binary representation of `y`.

This sequence will eventually reach the value **1**.

The **popcount-depth** of `x` is defined as the smallest integer `d >= 0` such that:

```
pd = 1
```

---

## Example

If:

```
x = 7
binary = 111
```

The sequence becomes:

```
7 → 3 → 2 → 1
```

So the **popcount-depth** of `7` is:

```
3
```

---

# Queries

You are also given a 2D array **queries** where each query is one of the following:

### Query Type 1

```
[1, l, r, k]
```

Determine the number of indices `j` such that:

```
l ≤ j ≤ r
popcount-depth(nums[j]) == k
```

---

### Query Type 2

```
[2, idx, val]
```

Update:

```
nums[idx] = val
```

---

Return an integer array **answer** where:

```
answer[i] = result of the i-th Type 1 query
```

---

# Example 1

Input:

```
nums = [2,4]
queries = [[1,0,1,1],[2,1,1],[1,0,1,0]]
```

Output:

```
[2,1]
```

Explanation:

| i   | query     | nums  | binary(nums) | depth | [l,r] | k   | valid nums[j] | updated nums | answer |
| --- | --------- | ----- | ------------ | ----- | ----- | --- | ------------- | ------------ | ------ |
| 0   | [1,0,1,1] | [2,4] | [10,100]     | [1,1] | [0,1] | 1   | [0,1]         | —            | 2      |
| 1   | [2,1,1]   | [2,4] | [10,100]     | [1,1] | —     | —   | —             | [2,1]        | —      |
| 2   | [1,0,1,0] | [2,1] | [10,1]       | [1,0] | [0,1] | 0   | [1]           | —            | 1      |

Final answer:

```
[2,1]
```

---

# Example 2

Input:

```
nums = [3,5,6]
queries = [[1,0,2,2],[2,1,4],[1,1,2,1],[1,0,1,0]]
```

Output:

```
[3,1,0]
```

---

# Example 3

Input:

```
nums = [1,2]
queries = [[1,0,1,1],[2,0,3],[1,0,0,1],[1,0,0,2]]
```

Output:

```
[1,0,1]
```

---

# Constraints

```
1 <= n == nums.length <= 10^5
1 <= nums[i] <= 10^15
1 <= queries.length <= 10^5
queries[i].length == 3 or 4
queries[i] == [1, l, r, k] or [2, idx, val]
0 <= l <= r <= n - 1
0 <= k <= 5
0 <= idx <= n - 1
1 <= val <= 10^15
```
