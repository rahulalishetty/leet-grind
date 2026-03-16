# 689. Maximum Sum of 3 Non-Overlapping Subarrays

## Problem Description

Given an integer array `nums` and an integer `k`, find **three non-overlapping subarrays** of length `k` with the **maximum total sum**.

Return the result as a list of **indices representing the starting position** of each interval (0-indexed).

If there are multiple answers, return the **lexicographically smallest** one.

---

## Example 1

### Input

```
nums = [1,2,1,2,6,7,5,1]
k = 2
```

### Output

```
[0,3,5]
```

### Explanation

The chosen subarrays are:

```
[1,2]  -> start index 0
[2,6]  -> start index 3
[7,5]  -> start index 5
```

Total sum:

```
(1+2) + (2+6) + (7+5) = 3 + 8 + 12 = 23
```

Another possibility could be:

```
[2,1], [2,6], [7,5]
start indices -> [1,3,5]
```

But `[0,3,5]` is **lexicographically smaller**, so it is chosen.

---

## Example 2

### Input

```
nums = [1,2,1,2,1,2,1,2,1]
k = 2
```

### Output

```
[0,2,4]
```

---

## Constraints

```
1 <= nums.length <= 2 * 10^4
1 <= nums[i] < 2^16
1 <= k <= floor(nums.length / 3)
```

---

## Key Points

- We must select **three subarrays**
- Each subarray must have **length k**
- Subarrays **must not overlap**
- We must **maximize the total sum**
- If multiple answers exist, return the **lexicographically smallest starting indices**
