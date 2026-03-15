# 1060. Missing Element in Sorted Array

Given an integer array `nums` which is sorted in ascending order and whose elements are all unique, and given an integer `k`, return the **kth missing number** starting from the **leftmost number** of the array.

---

## Example 1

**Input**

```text
nums = [4,7,9,10], k = 1
```

**Output**

```text
5
```

**Explanation**

The first missing number is:

```text
5
```

---

## Example 2

**Input**

```text
nums = [4,7,9,10], k = 3
```

**Output**

```text
8
```

**Explanation**

The missing numbers are:

```text
[5,6,8,...]
```

Hence, the third missing number is:

```text
8
```

---

## Example 3

**Input**

```text
nums = [1,2,4], k = 3
```

**Output**

```text
6
```

**Explanation**

The missing numbers are:

```text
[3,5,6,7,...]
```

Hence, the third missing number is:

```text
6
```

---

## Constraints

- `1 <= nums.length <= 5 * 10^4`
- `1 <= nums[i] <= 10^7`
- `nums` is sorted in ascending order
- All elements in `nums` are unique
- `1 <= k <= 10^8`

---

## Follow-up

Can you find a logarithmic time complexity solution?

```text
O(log n)
```
