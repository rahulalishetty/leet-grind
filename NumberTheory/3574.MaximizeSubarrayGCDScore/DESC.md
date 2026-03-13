# 3574. Maximize Subarray GCD Score

You are given an array of positive integers `nums` and an integer `k`.

You may perform **at most `k` operations**. In each operation, you can **choose one element in the array and double its value**. Each element can be doubled **at most once**.

The **score of a contiguous subarray** is defined as:

```
score = length_of_subarray × gcd_of_subarray
```

Where:

- `length_of_subarray` is the number of elements in the subarray
- `gcd_of_subarray` is the **greatest common divisor** of all elements in that subarray

Your task is to **return the maximum score** that can be achieved by selecting a contiguous subarray from the modified array.

---

# Notes

- The **greatest common divisor (GCD)** of an array is the largest integer that evenly divides all the array elements.
- Each element can be doubled **at most once**.
- At most **k elements** can be doubled.

---

# Example 1

## Input

```
nums = [2,4]
k = 1
```

## Output

```
8
```

## Explanation

Double `nums[0]`:

```
[2,4] → [4,4]
```

Subarray:

```
[4,4]
```

```
GCD = 4
length = 2
score = 2 × 4 = 8
```

---

# Example 2

## Input

```
nums = [3,5,7]
k = 2
```

## Output

```
14
```

## Explanation

Double `nums[2]`:

```
[3,5,7] → [3,5,14]
```

Best subarray:

```
[14]
```

```
GCD = 14
length = 1
score = 1 × 14 = 14
```

---

# Example 3

## Input

```
nums = [5,5,5]
k = 1
```

## Output

```
15
```

## Explanation

Subarray:

```
[5,5,5]
```

```
GCD = 5
length = 3
score = 3 × 5 = 15
```

Doubling any element does not increase the score.

---

# Constraints

```
1 <= n == nums.length <= 1500
1 <= nums[i] <= 10^9
1 <= k <= n
```
