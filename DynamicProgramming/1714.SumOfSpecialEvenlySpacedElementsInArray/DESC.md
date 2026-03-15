# 1714. Sum Of Special Evenly-Spaced Elements In Array

## Problem Description

You are given a **0-indexed integer array `nums`** consisting of `n` non‑negative integers.

You are also given an array `queries`, where:

```
queries[i] = [x_i, y_i]
```

For each query `(x, y)`, compute the **sum of all `nums[j]`** such that:

- `x ≤ j < n`
- `(j - x)` is divisible by `y`

In other words, the indices considered are:

```
x, x + y, x + 2y, x + 3y, ...
```

as long as they remain within the array.

Return an array `answer` such that:

```
answer.length == queries.length
answer[i] = result of query i modulo 1e9 + 7
```

---

# Examples

## Example 1

**Input**

```
nums = [0,1,2,3,4,5,6,7]
queries = [[0,3],[5,1],[4,2]]
```

**Output**

```
[9,18,10]
```

**Explanation**

1. Query `[0,3]`
   Valid indices:

   ```
   0, 3, 6
   ```

   Sum:

   ```
   nums[0] + nums[3] + nums[6] = 0 + 3 + 6 = 9
   ```

2. Query `[5,1]`
   Valid indices:

   ```
   5, 6, 7
   ```

   Sum:

   ```
   nums[5] + nums[6] + nums[7] = 5 + 6 + 7 = 18
   ```

3. Query `[4,2]`
   Valid indices:

   ```
   4, 6
   ```

   Sum:

   ```
   nums[4] + nums[6] = 4 + 6 = 10
   ```

---

## Example 2

**Input**

```
nums = [100,200,101,201,102,202,103,203]
queries = [[0,7]]
```

**Output**

```
[303]
```

**Explanation**

Valid indices:

```
0, 7
```

Sum:

```
nums[0] + nums[7] = 100 + 203 = 303
```

---

# Constraints

```
n == nums.length
1 ≤ n ≤ 5 * 10^4
0 ≤ nums[i] ≤ 10^9

1 ≤ queries.length ≤ 1.5 * 10^5
0 ≤ x_i < n
1 ≤ y_i ≤ 5 * 10^4
```

---

# Key Observation

For a query `(x, y)`, we compute:

```
nums[x] + nums[x+y] + nums[x+2y] + ...
```

This forms an **arithmetic progression of indices**.

The challenge is answering up to **150,000 queries efficiently**.
