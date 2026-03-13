# 3671. Sum of Beautiful Subsequences

You are given an integer array `nums` of length `n`.

For every positive integer `g`, we define the **beauty of `g`** as:

```
beauty(g) = g × (number of strictly increasing subsequences of nums whose GCD is exactly g)
```

Return the **sum of beauty values for all positive integers `g`**.

Since the answer may be very large, return it modulo:

```
10^9 + 7
```

---

# Definitions

### Strictly Increasing Subsequence

A subsequence where:

```
nums[i1] < nums[i2] < nums[i3] < ...
```

and the indices satisfy:

```
i1 < i2 < i3 < ...
```

### GCD

The **greatest common divisor (GCD)** of a set of numbers is the largest integer that divides all numbers in the set.

---

# Example 1

## Input

```
nums = [1,2,3]
```

## Output

```
10
```

## Explanation

All strictly increasing subsequences and their GCDs:

| Subsequence | GCD |
| ----------- | --- |
| [1]         | 1   |
| [2]         | 2   |
| [3]         | 3   |
| [1,2]       | 1   |
| [1,3]       | 1   |
| [2,3]       | 1   |
| [1,2,3]     | 1   |

### Beauty Calculation

| GCD | Count of subsequences | Beauty    |
| --- | --------------------- | --------- |
| 1   | 5                     | 1 × 5 = 5 |
| 2   | 1                     | 2 × 1 = 2 |
| 3   | 1                     | 3 × 1 = 3 |

Total beauty:

```
5 + 2 + 3 = 10
```

---

# Example 2

## Input

```
nums = [4,6]
```

## Output

```
12
```

## Explanation

All strictly increasing subsequences and their GCDs:

| Subsequence | GCD |
| ----------- | --- |
| [4]         | 4   |
| [6]         | 6   |
| [4,6]       | 2   |

### Beauty Calculation

| GCD | Count of subsequences | Beauty    |
| --- | --------------------- | --------- |
| 2   | 1                     | 2 × 1 = 2 |
| 4   | 1                     | 4 × 1 = 4 |
| 6   | 1                     | 6 × 1 = 6 |

Total beauty:

```
2 + 4 + 6 = 12
```

---

# Constraints

```
1 <= n == nums.length <= 10^4
1 <= nums[i] <= 7 * 10^4
```
