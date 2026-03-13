# 3072. Distribute Elements Into Two Arrays II

## Problem Statement

You are given a **1-indexed array** of integers `nums` of length `n`.

We define a function:

```
greaterCount(arr, val)
```

which returns the number of elements in `arr` that are **strictly greater than `val`**.

Your task is to distribute all elements of `nums` into two arrays `arr1` and `arr2` using `n` operations.

---

## Rules for Distribution

1. **Operation 1**
   Append `nums[1]` to `arr1`.

2. **Operation 2**
   Append `nums[2]` to `arr2`.

3. **For each operation `i` (i ≥ 3):**
   - If

     ```
     greaterCount(arr1, nums[i]) > greaterCount(arr2, nums[i])
     ```

     append `nums[i]` to `arr1`.

   - If

     ```
     greaterCount(arr1, nums[i]) < greaterCount(arr2, nums[i])
     ```

     append `nums[i]` to `arr2`.

   - If both counts are equal:
     - append to the array with **fewer elements**.

   - If there is still a tie:
     - append to **arr1**.

---

## Result Array

After processing all elements:

```
result = arr1 + arr2
```

(concatenation of `arr1` followed by `arr2`).

Example:

```
arr1 = [1,2,3]
arr2 = [4,5,6]

result = [1,2,3,4,5,6]
```

---

# Example 1

### Input

```
nums = [2,1,3,3]
```

### Output

```
[2,3,1,3]
```

### Explanation

After first two operations:

```
arr1 = [2]
arr2 = [1]
```

3rd operation (`nums[3] = 3`):

```
greaterCount(arr1,3) = 0
greaterCount(arr2,3) = 0
```

Lengths equal → append to `arr1`.

```
arr1 = [2,3]
```

4th operation (`nums[4] = 3`):

```
greaterCount(arr1,3) = 0
greaterCount(arr2,3) = 0
```

`arr2` has fewer elements → append to `arr2`.

```
arr1 = [2,3]
arr2 = [1,3]
```

Result:

```
[2,3,1,3]
```

---

# Example 2

### Input

```
nums = [5,14,3,1,2]
```

### Output

```
[5,3,1,2,14]
```

### Explanation

Initial state:

```
arr1 = [5]
arr2 = [14]
```

3rd element (`3`):

```
greaterCount(arr1,3) = 1
greaterCount(arr2,3) = 1
```

Equal counts and lengths → append to `arr1`.

```
arr1 = [5,3]
```

4th element (`1`):

```
greaterCount(arr1,1) = 2
greaterCount(arr2,1) = 1
```

Append to `arr1`.

```
arr1 = [5,3,1]
```

5th element (`2`):

```
greaterCount(arr1,2) = 2
greaterCount(arr2,2) = 1
```

Append to `arr1`.

Final arrays:

```
arr1 = [5,3,1,2]
arr2 = [14]
```

Result:

```
[5,3,1,2,14]
```

---

# Example 3

### Input

```
nums = [3,3,3,3]
```

### Output

```
[3,3,3,3]
```

### Explanation

At the end:

```
arr1 = [3,3]
arr2 = [3,3]
```

Result:

```
[3,3,3,3]
```

---

# Constraints

```
3 ≤ n ≤ 10^5
1 ≤ nums[i] ≤ 10^9
```
