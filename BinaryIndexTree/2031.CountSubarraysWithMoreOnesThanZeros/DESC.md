# 2031. Count Subarrays With More Ones Than Zeros

## Problem Statement

You are given a **binary array `nums`** containing only the integers `0` and `1`.

Your task is to **count the number of subarrays** that contain **more `1`s than `0`s**.

Since the answer may be very large, return the result **modulo `10^9 + 7`**.

A **subarray** is a contiguous sequence of elements within an array.

---

# Example 1

## Input

```
nums = [0,1,1,0,1]
```

## Output

```
9
```

## Explanation

Subarrays with more ones than zeros:

### Size 1

```
[1], [1], [1]
```

### Size 2

```
[1,1]
```

### Size 3

```
[0,1,1], [1,1,0], [1,0,1]
```

### Size 4

```
[1,1,0,1]
```

### Size 5

```
[0,1,1,0,1]
```

Total = **9**

---

# Example 2

## Input

```
nums = [0]
```

## Output

```
0
```

## Explanation

No subarrays have more `1`s than `0`s.

---

# Example 3

## Input

```
nums = [1]
```

## Output

```
1
```

## Explanation

Subarrays with more `1`s than `0`s:

```
[1]
```

---
