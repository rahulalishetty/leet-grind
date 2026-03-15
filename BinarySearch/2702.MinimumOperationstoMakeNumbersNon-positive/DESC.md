# 2702. Minimum Operations to Make Numbers Non-positive

## Problem Description

You are given:

- An integer array `nums`
- Two integers `x` and `y`

In **one operation**, you must:

1. Choose an index `i` such that:

```
0 <= i < nums.length
```

2. Perform the following updates:

```
nums[i] -= x
nums[j] -= y  for all j != i
```

---

## Goal

Return the **minimum number of operations** required so that **all values in `nums` become ≤ 0**.

---

# Example 1

Input

```
nums = [3,4,1,7,6]
x = 4
y = 2
```

Output

```
3
```

### Explanation

One optimal sequence:

Operation 1 → choose `i = 3`

```
nums = [1,2,-1,3,4]
```

Operation 2 → choose `i = 3`

```
nums = [-1,0,-3,-1,2]
```

Operation 3 → choose `i = 4`

```
nums = [-3,-2,-5,-3,-2]
```

Now all numbers are **≤ 0**.

So the answer is:

```
3
```

---

# Example 2

Input

```
nums = [1,2,1]
x = 2
y = 1
```

Output

```
1
```

### Explanation

Choose `i = 1`

```
nums → [0,0,0]
```

All numbers are now **non‑positive**.

Therefore the answer is:

```
1
```

---

# Constraints

```
1 <= nums.length <= 10^5
```

```
1 <= nums[i] <= 10^9
```

```
1 <= y < x <= 10^9
```
