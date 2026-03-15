# 3344. Maximum Sized Array

## Problem Description

Given a positive integer `s`, let **A** be a 3D array of dimensions:

```
n × n × n
```

Each element of the array is defined as:

```
A[i][j][k] = i * (j OR k)
```

where:

```
0 <= i, j, k < n
```

The operator `OR` represents the **bitwise OR** operation.

Your task is to return the **maximum possible value of `n`** such that the **sum of all elements** in array `A` **does not exceed `s`**.

---

# Example 1

### Input

```
s = 10
```

### Output

```
2
```

### Explanation

For `n = 2`, the array elements are:

```
A[0][0][0] = 0 * (0 OR 0) = 0
A[0][0][1] = 0 * (0 OR 1) = 0
A[0][1][0] = 0 * (1 OR 0) = 0
A[0][1][1] = 0 * (1 OR 1) = 0
A[1][0][0] = 1 * (0 OR 0) = 0
A[1][0][1] = 1 * (0 OR 1) = 1
A[1][1][0] = 1 * (1 OR 0) = 1
A[1][1][1] = 1 * (1 OR 1) = 1
```

Total sum of all elements:

```
0 + 0 + 0 + 0 + 0 + 1 + 1 + 1 = 3
```

Since `3 <= 10`, the maximum valid `n` is:

```
2
```

---

# Example 2

### Input

```
s = 0
```

### Output

```
1
```

### Explanation

For `n = 1`:

```
A[0][0][0] = 0 * (0 OR 0) = 0
```

Total sum:

```
0
```

Since `0 <= 0`, the maximum value of `n` is:

```
1
```

---

# Constraints

```
0 <= s <= 10^15
```
