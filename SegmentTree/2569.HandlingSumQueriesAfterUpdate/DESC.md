# 2569. Handling Sum Queries After Update

## Problem Description

You are given two **0-indexed arrays**:

- `nums1`
- `nums2`

You are also given a **2D array `queries`** representing a sequence of operations.

There are **three types of queries**.

---

## Query Types

### Type 1

```
[1, l, r]
```

Flip the values in `nums1` from index `l` to `r` (inclusive).

Flip means:

```
0 -> 1
1 -> 0
```

Both `l` and `r` are **0-indexed**.

---

### Type 2

```
[2, p, 0]
```

For every index `0 ≤ i < n`:

```
nums2[i] = nums2[i] + nums1[i] * p
```

---

### Type 3

```
[3, 0, 0]
```

Return the **sum of all elements in `nums2`**.

---

## Goal

Return an array containing the answers to **all queries of type 3**.

---

# Example 1

## Input

```
nums1 = [1,0,1]
nums2 = [0,0,0]
queries = [[1,1,1],[2,1,0],[3,0,0]]
```

## Output

```
[3]
```

## Explanation

After the first query:

```
nums1 = [1,1,1]
```

After the second query:

```
nums2[i] = nums2[i] + nums1[i] * 1
nums2 = [1,1,1]
```

Sum of `nums2`:

```
1 + 1 + 1 = 3
```

Result:

```
[3]
```

---

# Example 2

## Input

```
nums1 = [1]
nums2 = [5]
queries = [[2,0,0],[3,0,0]]
```

## Output

```
[5]
```

## Explanation

First query:

```
nums2[i] = nums2[i] + nums1[i] * 0
```

No change:

```
nums2 = [5]
```

Second query returns:

```
5
```

---

# Constraints

```
1 <= nums1.length, nums2.length <= 10^5
nums1.length = nums2.length

1 <= queries.length <= 10^5
queries[i].length = 3

0 <= l <= r <= nums1.length - 1
0 <= p <= 10^6

0 <= nums1[i] <= 1
0 <= nums2[i] <= 10^9
```
