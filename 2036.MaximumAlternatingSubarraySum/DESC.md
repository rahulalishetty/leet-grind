# 2036. Maximum Alternating Subarray Sum

## Problem Description

A **subarray** of a 0-indexed integer array is a **contiguous non-empty sequence** of elements within the array.

The **alternating subarray sum** of a subarray that ranges from index `i` to `j` (inclusive) is defined as:

```
nums[i] - nums[i+1] + nums[i+2] - nums[i+3] + ... +/- nums[j]
```

In other words, the signs alternate starting with a **positive sign** at the first element of the subarray.

Your task is to compute the **maximum alternating subarray sum** among all possible subarrays.

---

# Examples

## Example 1

**Input**

```
nums = [3,-1,1,2]
```

**Output**

```
5
```

**Explanation**

The subarray:

```
[3,-1,1]
```

has the largest alternating sum:

```
3 - (-1) + 1 = 5
```

---

## Example 2

**Input**

```
nums = [2,2,2,2,2]
```

**Output**

```
2
```

**Explanation**

Several subarrays achieve the same maximum alternating sum.

Examples:

```
[2]
[2,2,2]
[2,2,2,2,2]
```

Their alternating sums:

```
[2] -> 2
[2,2,2] -> 2 - 2 + 2 = 2
[2,2,2,2,2] -> 2 - 2 + 2 - 2 + 2 = 2
```

The maximum value is:

```
2
```

---

## Example 3

**Input**

```
nums = [1]
```

**Output**

```
1
```

**Explanation**

There is only one possible subarray:

```
[1]
```

Alternating sum:

```
1
```

---

# Constraints

```
1 <= nums.length <= 10^5
-10^5 <= nums[i] <= 10^5
```
