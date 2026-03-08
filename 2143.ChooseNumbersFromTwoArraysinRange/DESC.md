# 2143. Choose Numbers From Two Arrays in Range

## Problem Description

You are given two **0-indexed integer arrays** `nums1` and `nums2` of length `n`.

A range `[l, r]` (inclusive) where:

```
0 ≤ l ≤ r < n
```

is considered **balanced** if the following conditions hold:

1. For every `i` in the range `[l, r]`, you pick **either** `nums1[i]` **or** `nums2[i]`.
2. The **sum of numbers chosen from `nums1` equals the sum of numbers chosen from `nums2`**.
3. If no numbers are chosen from one array, its sum is considered **0**.

---

# Distinct Balanced Ranges

Two balanced ranges `[l1, r1]` and `[l2, r2]` are considered **different** if at least one of the following is true:

- `l1 ≠ l2`
- `r1 ≠ r2`
- For some `i`, the first range picks `nums1[i]` while the second picks `nums2[i]` (or vice versa).

Your task is to **return the number of different balanced ranges**.

Since the result may be very large, return it **modulo 10^9 + 7**.

---

# Example 1

**Input**

```
nums1 = [1,2,5]
nums2 = [2,6,3]
```

**Output**

```
3
```

**Explanation**

The balanced ranges are:

1. `[0, 1]` choosing:

```
nums2[0], nums1[1]
```

Sum check:

```
nums1 sum = 2
nums2 sum = 2
```

2. `[0, 2]` choosing:

```
nums1[0], nums2[1], nums1[2]
```

Sum check:

```
1 + 5 = 6
```

3. `[0, 2]` choosing:

```
nums1[0], nums1[1], nums2[2]
```

Sum check:

```
1 + 2 = 3
```

Note that the second and third ranges are **different** because the element chosen at index `1` differs.

---

# Example 2

**Input**

```
nums1 = [0,1]
nums2 = [1,0]
```

**Output**

```
4
```

**Explanation**

The balanced ranges are:

1. `[0, 0]` choosing:

```
nums1[0]
```

```
0 = 0
```

2. `[1, 1]` choosing:

```
nums2[1]
```

```
0 = 0
```

3. `[0, 1]` choosing:

```
nums1[0], nums2[1]
```

```
0 = 0
```

4. `[0, 1]` choosing:

```
nums2[0], nums1[1]
```

```
1 = 1
```

---

# Constraints

```
n == nums1.length == nums2.length
1 ≤ n ≤ 100
0 ≤ nums1[i], nums2[i] ≤ 100
```
