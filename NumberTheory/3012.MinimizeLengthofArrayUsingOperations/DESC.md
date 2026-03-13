# 3012. Minimize Length of Array Using Operations

You are given a **0-indexed integer array `nums`** containing positive integers.

Your task is to **minimize the length of `nums`** by performing the following operation **any number of times (including zero)**:

1. Select two distinct indices `i` and `j` such that:

```
nums[i] > 0
nums[j] > 0
```

2. Insert the result of:

```
nums[i] % nums[j]
```

at the end of the array.

3. Delete the elements at indices `i` and `j` from the array.

Return an integer representing the **minimum possible length** of the array after performing the operations.

---

# Example 1

## Input

```
nums = [1,4,3,1]
```

## Output

```
1
```

## Explanation

One possible sequence of operations:

### Operation 1

Select indices `2` and `1`

```
nums[2] % nums[1] = 3 % 4 = 3
```

Array becomes:

```
[1,4,3,1,3]
```

Delete indices `2` and `1`:

```
[1,1,3]
```

### Operation 2

Select indices `1` and `2`

```
1 % 3 = 1
```

Array becomes:

```
[1,1,3,1]
```

Delete indices `1` and `2`:

```
[1,1]
```

### Operation 3

Select indices `1` and `0`

```
1 % 1 = 0
```

Array becomes:

```
[1,1,0]
```

Delete indices `1` and `0`:

```
[0]
```

The array cannot be reduced further.

Final length:

```
1
```

---

# Example 2

## Input

```
nums = [5,5,5,10,5]
```

## Output

```
2
```

## Explanation

### Operation 1

Select indices `0` and `3`

```
5 % 10 = 5
```

Array becomes:

```
[5,5,5,10,5,5]
```

Delete indices `0` and `3`:

```
[5,5,5,5]
```

### Operation 2

Select indices `2` and `3`

```
5 % 5 = 0
```

Array becomes:

```
[5,5,5,5,0]
```

Delete indices `2` and `3`:

```
[5,5,0]
```

### Operation 3

Select indices `0` and `1`

```
5 % 5 = 0
```

Array becomes:

```
[5,5,0,0]
```

Delete indices `0` and `1`:

```
[0,0]
```

The array cannot be reduced further.

Final length:

```
2
```

---

# Example 3

## Input

```
nums = [2,3,4]
```

## Output

```
1
```

## Explanation

### Operation 1

Select indices `1` and `2`

```
3 % 4 = 3
```

Array becomes:

```
[2,3,4,3]
```

Delete indices `1` and `2`:

```
[2,3]
```

### Operation 2

Select indices `1` and `0`

```
3 % 2 = 1
```

Array becomes:

```
[2,3,1]
```

Delete indices `1` and `0`:

```
[1]
```

The array cannot be reduced further.

Final length:

```
1
```

---

# Constraints

```
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^9
```
