# 2263. Make Array Non-decreasing or Non-increasing

## Problem Description

You are given a **0-indexed integer array `nums`**.

In one operation, you can:

- Choose an index `i` where `0 <= i < nums.length`
- Set:

```
nums[i] = nums[i] + 1
```

or

```
nums[i] = nums[i] - 1
```

Return the **minimum number of operations** required to make the array:

- **non-decreasing**, or
- **non-increasing**.

---

# Example 1

### Input

```
nums = [3,2,4,5,0]
```

### Output

```
4
```

### Explanation

One possible way to transform the array into **non-increasing order**:

Operations:

```
+1 to nums[1]  -> 2 becomes 3
-1 to nums[2]  -> 4 becomes 3
-1 to nums[3]  -> 5 becomes 4
-1 to nums[3]  -> 4 becomes 3
```

Final array:

```
[3,3,3,3,0]
```

This array is **non-increasing**.

Total operations:

```
4
```

Another valid transformation:

```
[4,4,4,4,0]
```

also requires **4 operations**.

It can be proven that **4 is the minimum**.

---

# Example 2

### Input

```
nums = [2,2,3,4]
```

### Output

```
0
```

### Explanation

The array is already **non-decreasing**, so no operations are required.

---

# Example 3

### Input

```
nums = [0]
```

### Output

```
0
```

### Explanation

A single element array is always monotonic.

---

# Constraints

```
1 <= nums.length <= 1000
0 <= nums[i] <= 1000
```

---

# Follow Up

Can you solve the problem in:

```
O(n log n)
```

time complexity?
