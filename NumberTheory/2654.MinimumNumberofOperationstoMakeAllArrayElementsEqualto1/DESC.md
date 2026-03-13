# 2654. Minimum Number of Operations to Make All Array Elements Equal to 1

## Problem Statement

You are given a **0-indexed array `nums`** consisting of positive integers.

You can perform the following operation **any number of times**:

- Select an index `i` such that `0 ≤ i < n - 1`
- Replace **either** `nums[i]` **or** `nums[i+1]` with:

```
gcd(nums[i], nums[i+1])
```

where `gcd(a, b)` is the **greatest common divisor** of `a` and `b`.

### Goal

Return the **minimum number of operations** required to make **all elements of `nums` equal to `1`**.

If it is **impossible**, return:

```
-1
```

---

# Example 1

## Input

```
nums = [2,6,3,4]
```

## Output

```
4
```

## Explanation

We can perform the following operations:

1. Choose `i = 2`

```
gcd(3,4) = 1
```

Replace `nums[2]`:

```
[2,6,1,4]
```

2. Choose `i = 1`

```
gcd(6,1) = 1
```

Replace `nums[1]`:

```
[2,1,1,4]
```

3. Choose `i = 0`

```
gcd(2,1) = 1
```

Replace `nums[0]`:

```
[1,1,1,4]
```

4. Choose `i = 2`

```
gcd(1,4) = 1
```

Replace `nums[3]`:

```
[1,1,1,1]
```

Minimum operations = **4**

---

# Example 2

## Input

```
nums = [2,10,6,14]
```

## Output

```
-1
```

## Explanation

It is impossible to produce a `1` using gcd operations from the given numbers.

Therefore the answer is:

```
-1
```

---

# Constraints

```
2 ≤ nums.length ≤ 50
1 ≤ nums[i] ≤ 10^6
```

---

# Function Signature (Java)

```java
class Solution {
    public int minOperations(int[] nums) {

    }
}
```
