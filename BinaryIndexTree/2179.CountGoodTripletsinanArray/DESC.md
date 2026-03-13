# 2179. Count Good Triplets in an Array

## Problem Statement

You are given two **0-indexed arrays** `nums1` and `nums2` of length `n`, both of which are **permutations of `[0, 1, ..., n - 1]`**.

A **good triplet** is a set of **3 distinct values** that appear in **increasing order by position in both arrays**.

Let:

- `pos1[v]` = index of value `v` in `nums1`
- `pos2[v]` = index of value `v` in `nums2`

A triplet `(x, y, z)` is **good** if:

```
pos1[x] < pos1[y] < pos1[z]
AND
pos2[x] < pos2[y] < pos2[z]
```

Return the **total number of good triplets**.

---

# Example 1

## Input

```
nums1 = [2,0,1,3]
nums2 = [0,1,2,3]
```

## Output

```
1
```

## Explanation

Possible triplets `(x, y, z)` satisfying:

```
pos1[x] < pos1[y] < pos1[z]
```

are:

```
(2,0,1)
(2,0,3)
(2,1,3)
(0,1,3)
```

Among them, only:

```
(0,1,3)
```

also satisfies:

```
pos2[x] < pos2[y] < pos2[z]
```

So the answer is:

```
1
```

---

# Example 2

## Input

```
nums1 = [4,0,1,3,2]
nums2 = [4,1,0,2,3]
```

## Output

```
4
```

## Explanation

The 4 good triplets are:

```
(4,0,3)
(4,0,2)
(4,1,3)
(4,1,2)
```

---

# Constraints

```
n == nums1.length == nums2.length
3 <= n <= 10^5
0 <= nums1[i], nums2[i] <= n - 1
nums1 and nums2 are permutations of [0, 1, ..., n - 1]
```
