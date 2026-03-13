# 2470. Number of Subarrays With LCM Equal to K

You are given an integer array `nums` and an integer `k`.

Return the number of **subarrays** whose **least common multiple (LCM)** is exactly `k`.

A **subarray** is a contiguous non-empty sequence of elements within an array.

The **least common multiple (LCM)** of an array is the smallest positive integer that is divisible by all the elements of that array.

---

# Example 1

## Input

```
nums = [3,6,2,7,1]
k = 6
```

## Output

```
4
```

## Explanation

The subarrays whose LCM equals `6` are:

```
[3,6]
[6]
[6,2]
[3,6,2]
```

Thus the answer is:

```
4
```

---

# Example 2

## Input

```
nums = [3]
k = 2
```

## Output

```
0
```

## Explanation

No subarray has LCM equal to `2`.

---

# Constraints

```
1 <= nums.length <= 1000
1 <= nums[i], k <= 1000
```
