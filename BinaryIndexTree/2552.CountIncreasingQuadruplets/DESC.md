# 2552. Count Increasing Quadruplets

Given a **0-indexed integer array** `nums` of size `n` containing **all numbers from `1` to `n`**, return the number of **increasing quadruplets**.

A quadruplet `(i, j, k, l)` is considered **increasing** if:

```
0 <= i < j < k < l < n
```

and

```
nums[i] < nums[k] < nums[j] < nums[l]
```

---

# Example 1

Input:

```
nums = [1,3,2,4,5]
```

Output:

```
2
```

Explanation:

There are **two valid quadruplets**:

1. `i = 0, j = 1, k = 2, l = 3`

```
nums[0] < nums[2] < nums[1] < nums[3]
1 < 2 < 3 < 4
```

2. `i = 0, j = 1, k = 2, l = 4`

```
nums[0] < nums[2] < nums[1] < nums[4]
1 < 2 < 3 < 5
```

---

# Example 2

Input:

```
nums = [1,2,3,4]
```

Output:

```
0
```

Explanation:

Only one quadruplet exists:

```
(0,1,2,3)
```

But:

```
nums[j] < nums[k]
2 < 3
```

So it **does not satisfy** the required condition.

---

# Constraints

```
4 <= nums.length <= 4000
1 <= nums[i] <= nums.length
All integers are unique
nums is a permutation of [1..n]
```
