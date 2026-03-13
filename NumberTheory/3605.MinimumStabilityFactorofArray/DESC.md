# 3605. Minimum Stability Factor of Array

You are given an integer array `nums` and an integer `maxC`.

A subarray is called **stable** if the **highest common factor (HCF)** of all its elements is **greater than or equal to 2**.

The **stability factor** of an array is defined as the **length of its longest stable subarray**.

You may modify **at most `maxC` elements** of the array to **any integer**.

Return the **minimum possible stability factor** of the array after at most `maxC` modifications.

If **no stable subarray remains**, return:

```
0
```

---

# Notes

- The **highest common factor (HCF)** of an array is the largest integer that evenly divides all the array elements.
- A **subarray of length 1** is stable if its only element is **≥ 2**, because:

```
HCF([x]) = x
```

---

# Example 1

## Input

```
nums = [3,5,10]
maxC = 1
```

## Output

```
1
```

## Explanation

Stable subarray:

```
[5, 10]
```

```
HCF = 5
length = 2
```

Since `maxC = 1`, change:

```
nums[1] → 7
```

New array:

```
[3, 7, 10]
```

Now no subarray of length > 1 has `HCF ≥ 2`.

Thus the stability factor becomes:

```
1
```

---

# Example 2

## Input

```
nums = [2,6,8]
maxC = 2
```

## Output

```
1
```

## Explanation

Original stable subarray:

```
[2, 6, 8]
HCF = 2
length = 3
```

Modify:

```
nums[1] → 3
nums[2] → 5
```

New array:

```
[2,3,5]
```

Now no subarray of length > 1 has `HCF ≥ 2`.

So stability factor becomes:

```
1
```

---

# Example 3

## Input

```
nums = [2,4,9,6]
maxC = 1
```

## Output

```
2
```

## Explanation

Stable subarrays:

```
[2,4] → HCF = 2
[9,6] → HCF = 3
```

Both have length:

```
2
```

Even after one modification, at least one stable subarray of length `2` remains.

Therefore the minimum possible stability factor is:

```
2
```

---

# Constraints

```
1 <= n == nums.length <= 10^5
1 <= nums[i] <= 10^9
0 <= maxC <= n
```
