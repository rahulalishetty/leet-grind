# 3520. Minimum Threshold for Inversion Pairs Count

You are given an array of integers **nums** and an integer **k**.

An **inversion pair with threshold `x`** is defined as a pair of indices `(i, j)` such that:

- `i < j`
- `nums[i] > nums[j]`
- The difference between the numbers is at most `x`

```
nums[i] - nums[j] <= x
```

Your task is to determine the **minimum integer `min_threshold`** such that there are **at least `k` inversion pairs** with threshold `min_threshold`.

If no such integer exists, return:

```
-1
```

---

# Example 1

### Input

```
nums = [1,2,3,4,3,2,1]
k = 7
```

### Output

```
2
```

### Explanation

For threshold `x = 2`, the inversion pairs are:

```
(3,4)  nums[3] = 4 , nums[4] = 3
(2,5)  nums[2] = 3 , nums[5] = 2
(3,5)  nums[3] = 4 , nums[5] = 2
(4,5)  nums[4] = 3 , nums[5] = 2
(1,6)  nums[1] = 2 , nums[6] = 1
(2,6)  nums[2] = 3 , nums[6] = 1
(4,6)  nums[4] = 3 , nums[6] = 1
(5,6)  nums[5] = 2 , nums[6] = 1
```

There are fewer than `k` inversion pairs for any threshold less than `2`.

---

# Example 2

### Input

```
nums = [10,9,9,9,1]
k = 4
```

### Output

```
8
```

### Explanation

For threshold `x = 8`, the inversion pairs are:

```
(0,1)  nums[0] = 10 , nums[1] = 9
(0,2)  nums[0] = 10 , nums[2] = 9
(0,3)  nums[0] = 10 , nums[3] = 9
(1,4)  nums[1] = 9 , nums[4] = 1
(2,4)  nums[2] = 9 , nums[4] = 1
(3,4)  nums[3] = 9 , nums[4] = 1
```

There are fewer than `k` inversion pairs if the threshold is less than `8`.

---

# Constraints

```
1 <= nums.length <= 10^4
1 <= nums[i] <= 10^9
1 <= k <= 10^9
```
